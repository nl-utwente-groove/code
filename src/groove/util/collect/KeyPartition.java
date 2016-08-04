/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id$
 */
package groove.util.collect;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Storage structure combining a map from keys to sets of values, and the set of
 * all values partitioned by the map.
 * @author Arend Rensink
 * @version $Revision $
 */
abstract public class KeyPartition<T,U> {
    /**
     * Creates an empty partition object. A parameter determines if the
     * partition may have empty cells.
     * @param emptyCells if <code>true</code>, the partition may have empty
     *        cells.
     */
    public KeyPartition(boolean emptyCells) {
        this.emptyCells = emptyCells;
        this.partitionMap = new HashMap<>();
        this.valueSet = new ValueSetView();
    }

    /**
     * Creates an empty partition object, without empty cells.
     */
    public KeyPartition() {
        this(false);
    }

    /**
     * Returns an unmodifiable view on the entries of the partition map.
     */
    public Set<Entry<T,Set<U>>> entrySet() {
        return Collections.unmodifiableSet(this.partitionMap.entrySet());
    }

    /**
     * Returns an unmodifiable view on the keys of the partition map.
     */
    public Set<T> keySet() {
        return Collections.unmodifiableSet(this.partitionMap.keySet());
    }

    /** Returns the number of cells in the partition map. */
    public int cellCount() {
        return this.partitionMap.size();
    }

    /** Tests if a given key value occurs as key in the partition map. */
    public boolean containsKey(T key) {
        return this.partitionMap.containsKey(key);
    }

    /**
     * Removes a key (and corresponding cell) from the partition. The return
     * value indicates if the key was actually present.
     * @param key the key to remove
     * @return if <code>true</code>, there was a (possibly empty) cell for
     *         the key
     */
    public Set<U> removeCell(T key) {
        Set<U> result = this.partitionMap.remove(key);
        if (result != null) {
            this.size -= result.size();
        }
        return result;
    }

    /**
     * Adds a key (with empty cell) to the partition. This is only successful if
     * empty cells are allowed, and the key is not yet in the partition.
     * @param key the key to be added
     * @return if <code>true</code>, the key and cell were added
     */
    public boolean addCell(T key) {
        boolean result = this.emptyCells && !containsKey(key);
        if (result) {
            this.partitionMap.put(key, createCell());
        }
        return result;
    }

    /**
     * Returns an unmodifiable view on the cell associated with a given key, if
     * any.
     */
    public Set<U> getCell(T key) {
        Set<U> result = this.partitionMap.get(key);
        return result == null ? null : Collections.unmodifiableSet(result);
    }

    /**
     * Returns a view on the combined map values that behaves as the union of
     * the cells.
     */
    public Set<U> values() {
        return this.valueSet;
    }

    /** Returns the total number of values in the partition. */
    public int size() {
        return this.size;
    }

    /** Clears the partition. */
    public void clear() {
        this.partitionMap.clear();
        this.size = 0;
    }

    /** Tests if a given value is among the values of the partition. */
    public boolean contains(U value) {
        Set<U> cell = this.partitionMap.get(getKey(value));
        return cell != null && cell.contains(value);
    }

    /**
     * Adds a given value to the partition. If the value is the last of the
     * corresponding key, and empty cells are not allowed, then the key is also
     * removed.
     */
    public boolean add(U value) {
        T key = getKey(value);
        Set<U> cell = this.partitionMap.get(key);
        if (cell == null) {
            this.partitionMap.put(key, cell = createCell());
        }
        boolean result = cell.add(value);
        if (result) {
            this.size++;
        }
        return result;
    }

    /**
     * Removes a given value from the value set. If the value is the last of the
     * corresponding key, and empty cells are not allowed, then the key is also
     * removed.
     */
    public boolean remove(Object value) {
        T key = getKey(value);
        Set<U> cell = this.partitionMap.get(key);
        boolean result = cell != null && cell.remove(value);
        if (result) {
            assert cell != null;
            this.size--;
            if (cell.isEmpty() && !this.emptyCells) {
                this.partitionMap.remove(key);
            }
        }
        return result;
    }

    /** Indicates if this partition may have empty cells. */
    public boolean allowsEmptyCells() {
        return this.emptyCells;
    }

    /** Callback factory method to create a partition cell. */
    protected Set<U> createCell() {
        return new HashSet<>();
    }

    /**
     * Method to retrieve a key from a value. The method should return
     * <code>null</code> if the value is not of the appropriate type.
     */
    abstract protected T getKey(Object value);

    /**
     * @return Returns the partitionMap.
     */
    final Map<T,Set<U>> getPartitionMap() {
        return this.partitionMap;
    }

    /** The underlying partition map. */
    private final Map<T,Set<U>> partitionMap;
    /** View on the values of the partition map as a modifiable set. */
    private final Set<U> valueSet;
    /** Total size of the (partitioned) set. */
    private int size;
    /** Flag indicating if the partition may have empty cells. */
    private final boolean emptyCells;

    /** Class implementing a set view on the values of the partition map. */
    private class ValueSetView extends SetOfDisjointSets<U> {
        /** Creates a fresh instance of the view. */
        public ValueSetView() {
            super(getPartitionMap().values());
        }

        @Override
        public boolean add(U elem) {
            return KeyPartition.this.add(elem);
        }

        @Override
        public boolean remove(Object elem) {
            return KeyPartition.this.remove(elem);
        }

        @Override
        public int size() {
            return KeyPartition.this.size();
        }
    }
}
