/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id: KeyPartition.java,v 1.2 2007-09-22 09:10:33 rensink Exp $
 */
package groove.util;

import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
abstract public class KeyPartition<T,U,S extends Set<U>> extends AbstractSet<U> {
    /** 
     * Creates an empty partition object.
     * A further parameter determines if the partition may
     * have empty cells. 
     */
    public KeyPartition(boolean emptyCells) {
        this.partitionMap = createPartitionMap();
        this.emptyCells = emptyCells;
    }

    /** 
     * Creates an empty partition object, without empty cells.
     */
    public KeyPartition() {
        this(false);
    }

	@Override
	public Iterator<U> iterator() {
		return new NestedIterator<U>(new TransformIterator<Set<U>, Iterator<U>>(partitionMap.values()){
			@Override
			protected Iterator<U> toOuter(Set<U> from) {
				return from.iterator();
			}
		});
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean add(U value) {
		T key = getKey(value);
		S cell = getCell(key);
		if (cell == null) {
			cell = createCell();
			partitionMap.put(key, cell);
		}
		boolean result = cell.add(value);
		if (result) {
			size++;
		}
		return result;
	}

	@Override
	public void clear() {
		partitionMap.clear();
		size = 0;
	}

	@Override
	public boolean contains(Object value) {
		Set<U> cell = getCell(getKey((U) value));
		return cell != null && cell.contains(value);
	}

	/** 
	 * If the value is the last of the corresponding key,
	 * and empty cells are not allowed, then the key is also removed.
	 */
	@Override
	public boolean remove(Object value) {
		T key = getKey((U) value);
		Set<U> cell = getCell(key);
		boolean result = cell != null &&  cell.remove(value);
		if (result) {
			size--;
			if (cell.isEmpty() && !emptyCells) {
				partitionMap.remove(key);
			}
		}
		return result;
	}

	/** 
	 * Returns the partition cell for a given key.
	 * Directly modifying the cell will lead to inconsistencies. 
	 */
	public S getCell(T key) {
		return partitionMap.get(key);
	}
	
	/** 
	 * Returns the mapping from keys to partition calls.
	 * Directly modifying this map may result in inconsistencies.
	 */
	public Map<T,S> getPartitionMap() {
		return partitionMap;
	}
	
	/**
	 * Adds a key (with empty cell) to the partition.
	 * This is only successful if empty cells are allowed, and the
	 * key is not yet in the partition.
	 * @param key the key to be added
	 * @return if <code>true</code>, the key and cell were added
	 */
	public boolean addKey(T key) {
		boolean result = emptyCells && !partitionMap.containsKey(key);
		if (result) {
			partitionMap.put(key, createCell());
		}
		return result;
	}
	
	/** 
	 * Removes a key (and corresponding cell) from the partition. 
	 * The return value indicates if the key was actually present.
	 * @param key the key to remove
	 * @return if <code>true</code>, there was a (possibly empty) cell for the key
	 */
	public boolean removeKey(T key) {
		Set<U> cell = partitionMap.remove(key);
		boolean result = cell != null;
		if (result) {
			size -= cell.size();
		}
		return result;
	}
	
	/** Indicates if this partition may have empty cells. */
	public boolean allowsEmptyCells() {
		return emptyCells;
	}
	
	/** Callback factory method to create the inner partition map. */
	protected Map<T,S> createPartitionMap() {
		return new HashMap<T,S>();
	}
	
	/** Callback factory method to create a partition cell. */
	abstract protected S createCell();
	
	/** Method to retrieve a key from a value. */
	abstract protected T getKey(U value);
	
	/** The inner partition map, from keys to cells. */
	private final Map<T,S> partitionMap;
	/** Total size of the (partitioned) set. */
	private int size;
	/** Flag indicating if the partition may have empty cells. */
	private final boolean emptyCells;
}
