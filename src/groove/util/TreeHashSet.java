// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/**
 * 
 */
package groove.util;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Set implementation that uses a search tree over "hash" code.
 * If the number of elements is small or the keys are evenly distributed, this 
 * outperforms the {@link java.util.HashSet}. 
 * @author Arend Rensink
 * @version $Revision: 1.8 $
 */
public class TreeHashSet<T> extends AbstractSet<T> {
	/**
	 * Creates an instance of a tree store set with a given root and branch resolution,
	 * initial capacity, and equator.
	 * The resolution is required to be at least <code>1</code>.
	 * @param capacity the initial capacity of the set
	 * @param resolution the resolution of the tree; should be at least <code>1</code>
	 * @param rootResolution the resolution of the root branch; should be at least <code>1</code>
	 * @param equator the equator used for deciding equality of objects in the set
	 */
	public TreeHashSet(int capacity, int resolution, int rootResolution, Equator<T> equator) {
        if (resolution < 1) {
            throw new IllegalArgumentException("Resolution should be at least 1");
        }
        if (rootResolution < 1) {
            throw new IllegalArgumentException("Root resolution should be at least 1");
        }
		this.resolution = resolution;
		this.mask = (1 << resolution) - 1;
		this.rootResolution = rootResolution;
		this.rootMask = (1 << rootResolution) - 1;
		this.equator = equator;
		// initialise the keys and tree
		this.codes = new int[capacity];
		this.keys = new Object[capacity];
		this.tree = new int[Math.max(capacity,rootMask+1)];
	}

    /**
     * Creates an instance of a tree store set with a given initial capacity and resolution.
     * The resolution is required to be at least <code>1</code>.
     * @param capacity the initial capacity of the set
     * @param resolution the resolution of the tree branches; should be at least <code>1</code>
     * @param rootResolution the resolution of the tree root; should be at least <code>1</code>
     */
    public TreeHashSet(int capacity, int resolution, int rootResolution) {
        this(capacity, resolution, rootResolution, DEFAULT_EQUATOR);
    }

    /**
     * Creates an instance of a tree store set with a given resolution
     * and initial capacity.
     * The resolution is required to be at least <code>1</code>.
     * @param capacity the initial capacity of the set
     * @param resolution the resolution of the tree branches; should be at least <code>1</code>
     */
    public TreeHashSet(int capacity, int resolution) {
        this(capacity, resolution, Math.max(resolution, DEFAULT_ROOT_RESOLUTION));
    }
    
    /**
     * Creates an instance of a tree store set with a given initial capacity and equator.
     * @param capacity the initial capacity of the set
     * @param equator the equator used for deciding equality of objects in the set
     */
	public TreeHashSet(int capacity, Equator<T> equator) {
		this(capacity, DEFAULT_RESOLUTION, DEFAULT_ROOT_RESOLUTION, equator);
	}

    /**
     * Creates an instance of a tree store set with a given equator.
     * @param equator the equator used for deciding equality of objects in the set
     */
	public TreeHashSet(Equator<T> equator) {
		this(DEFAULT_CAPACITY, equator);
	}
    
    /**
     * Creates an instance of a tree store set with a given initial capacity.
     * @param capacity the initial capacity of the set
     */
	public TreeHashSet(int capacity) {
		this(capacity, DEFAULT_RESOLUTION, DEFAULT_ROOT_RESOLUTION);
	}
	
    /**
     * Creates an instance of a tree store set.
     */
	public TreeHashSet() {
		this(DEFAULT_RESOLUTION);
	}
    
    /**
     * Creates an instance of a tree store which is a copy of another.
     * This is a much more efficient way of copying sets than by adding the elements
     * one at a time. 
     */
    public TreeHashSet(TreeHashSet<T> other) {
        this(other.size(), other.resolution, other.rootResolution, other.equator);
        int otherTreeLength = other.tree.length;
        if (this.tree.length < otherTreeLength) {
            this.tree = new int[otherTreeLength];
        }
        System.arraycopy(other.tree, 0, this.tree, 0, otherTreeLength);
        int otherCodesLength = other.codes.length;
        if (this.codes.length < otherCodesLength) {
            this.codes = new int[otherCodesLength];
            this.keys = new Object[otherCodesLength];
        }
        System.arraycopy(other.codes, 0, this.codes, 0, otherCodesLength);
        System.arraycopy(other.keys, 0, this.keys, 0, otherCodesLength);
        this.size = other.size;
        this.treeSize = other.treeSize;
        this.freeKeyIndex = other.freeKeyIndex;
        this.maxKeyIndex = other.maxKeyIndex;
        assert this.equals(other) : String.format("Clone    %s does not equal%noriginal %s",
            this,
            other);
    }
	
	/**
	 * Uses the <code>capacity</code> parameter to assign a new length
	 * to the underlying arrays, if they are smaller than this capacity.
	 */
    @Override
	public void clear() {
    	treeSize = 0;
    	size = 0;
    	Arrays.fill(keys, 0, maxKeyIndex+1, null);
    	maxKeyIndex = 0;
    	freeKeyIndex = 0;
	}

    @Override
	public int size() {
		return size;
	}

    @Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			public boolean hasNext() {
				Object next = this.next;
				if (next == null) {
					if (remainingCount == 0) {
						return false;
					}
					int index = this.index;
					final Object[] keys = this.keys;
					do {
						next = keys[index];
						index++;
					} while (next == null);
					this.next = next instanceof MyListEntry ? ((MyListEntry<T>) next).getValue() : (T) next;
					this.index = index;
					assert this.next != null;
					remainingCount--;
				}
				return true;
			}

			public T next() {
				if (hasNext()) {
//					assert removeKey == null || !next.equals(removeKey);
					T result = next;
					this.next = null;
//					this.removeKey = next;
					return result;
				} else {
					throw new NoSuchElementException();
				}
			}

			public void remove() {
                // since removing a key may mean that the next element of
                // the key chain is moved to the position of the removed key,
                // which the iterator has already passed by, we don't allow it.
				throw new UnsupportedOperationException();
			}

			/**
			 * Copy from the enclosing class.
			 */
			private final Object[] keys = TreeHashSet.this.keys;
			/**
			 * The index in {@link TreeHashSet#keys} where we're currently at.
			 * @invariant <code>index <= maxKeyIndex</code>
			 */
			private int index = 1;
			/**
			 * The next object; if <code>null</code>, the next yet has to be found.
			 */
			private T next;
			/**
			 * The number of remaining elements.
			 */
			private int remainingCount = size;
		};
	}

    @Override
    public boolean add(T key) {
		boolean result = put(key) == null;
		return result;
    }

    /**
     * Tries to insert a new object in the set.
     * If an equal object is already in the set, returns that object.
     * If the new key is really inserted, the method returns <code>null</code>
     * The difference with {@link #add(Object)} is thus only in the return value.
     * @param key the object to be inserted
     * @return <code>null</code> if <code>key</code> is inserted, otherwise an object
     * that was already present, such that <code>areEqual(key, result)</code>.
     */
    public T put(T key) {
        int code = getCode(key);
        if (size == 0) {
            // at the first key, we still have to create the root of the tree
            int index = newBranchIndex();
            tree[index + (code & rootMask)] = -newKeyIndex(code, key);
            return null;
        } else {
            // local copy of store, for efficiency
            int[] tree = this.tree;
            int mask = this.mask;
            int resolution = this.resolution;
            // precise node where the current value of index was retrieved from
            int indexPlusOffset = code & rootMask;
            // current search position
            int index = tree[indexPlusOffset];
            // remaining search key
            int search = code >>> rootResolution;
            // current depth search, in number of bits
            int depth = rootResolution;
            while (index > 0) {
                index = tree[indexPlusOffset = (index + (search & mask))];
                search >>>= resolution;
                depth += resolution;
            }
            if (index == 0) {
                // we're at an empty place of the tree
                tree[indexPlusOffset] = -newKeyIndex(code, key);
                return null;
            } else {
                // we've found an existing key
                int oldCode = codes[-index];
                if (oldCode == code) {
                    // the old code is the same as the one we're inserting
                    return putEqualKey(code, key, -index);
                } else {
                    // we have a new key, so we have to relocate
                    // first store the position of the old key
                    int oldKeyIndex = index;
                    // create a new position
                    int newIndex = newBranchIndex();
                    index = (tree = this.tree)[indexPlusOffset] = newIndex;
                    // the old search value
                    int oldSearch = oldCode >>> depth;
                    // the old and new branch values
                    int oldOffset, newOffset;
                    // so long as old and new key coincide, keep relocating
                    while ((newOffset = (search & mask)) == (oldOffset = (oldSearch & mask))) {
                        newIndex = newBranchIndex();
                        index = (tree = this.tree)[index + newOffset] = newIndex;
                        search >>>= resolution;
                        oldSearch >>>= resolution;
                    }
                    // we've found a difference, so store.
                    tree[index + oldOffset] = oldKeyIndex;
                    tree[index + newOffset] = -newKeyIndex(code, key);
                    return null;
                }
            }
        }
    }
    
    @Override
	public boolean remove(Object obj) {
		if (size == 0) {
			return false;
		}
		T key = (T) obj;
		int index = indexOf(getCode(key));
		if (index < 0) {
			// the key is a new one
			return false;
		} else if (allEqual()) {
			// we've found an existing key code and we're only looking at codes
			// so the key found at index should be removed
			int keyIndex = -tree[index];
			disposeBranchIndex(index);
			disposeKeyIndex(keyIndex);
			return true;
		} else {
			// we've found an existing key code but now we're going to compare
			Object[] keys = this.keys;
			// the current position in the key list
			int keyIndex = -tree[index];
			// the key retrieved from the key list
			Object knownKey = keys[keyIndex];
			// index of the previous key in the chain, if any (0 means none)
			int prevKeyIndex = 0;
			// walk the list of MyListEntries, if any
			while (knownKey instanceof MyListEntry) {
				MyListEntry<T> entry = (MyListEntry) knownKey;
				int nextKeyIndex = entry.getNext();
				if (areEqual(key, entry.getValue())) {
					keys[keyIndex] = keys[nextKeyIndex];
					disposeKeyIndex(nextKeyIndex);
					return true;
				} else {
					prevKeyIndex = keyIndex;
					knownKey = keys[keyIndex = nextKeyIndex];
				}
			}
			assert knownKey != null;
			// we're at a key that is not a MyListEntry
			if (areEqual(key, (T) knownKey)) {
				// maybe we have to adapt the tree
				if (prevKeyIndex == 0) {
					// there is no chain, so we have to adapt the tree
					disposeBranchIndex(index);
				} else {
					// the previous key has to be converted from a 
					// MyListEntry to the object inside
					keys[prevKeyIndex] = ((MyListEntry)keys[prevKeyIndex]).getValue();
				}
				disposeKeyIndex(keyIndex);
				return true;
			} else {
				return false;
			}
		}
	}
	
    @Override
	public boolean contains(Object obj) {
		if (size == 0) {
			return false;
		}
		T key = (T) obj;
		int index = indexOf(getCode(key));
		if (index < 0) {
			// the key is a new one
			return false;
		} else {
			// we've found an existing key code
			return allEqual() || containsAt(key, -tree[index]);
		}
	}
	
	/**
	 * Returns the memory space used for storing the set, expressed in 
	 * number of bytes per element stored. 
	 */
	public double getBytesPerElement() {
		int treeSpace = BYTES_PER_INT * tree.length;
		int codesSpace = BYTES_PER_INT * codes.length;
		int keysSpace = BYTES_PER_REF * keys.length;
		int myEntrySpace = (BYTES_PER_OBJECT + BYTES_PER_INT + BYTES_PER_REF) * getMyListEntryCount();
		return (treeSpace + codesSpace + keysSpace + myEntrySpace) / (double) size;
	}
	
    /**
     * Determines whether two objects, that are already determined to have the
     * same key codes, are to be considered equal for the purpose of this set.
     * The default implementation calls <code>areEqual(key, otherKey)</code> on the equator.
     * If a the {@link #HASHCODE_EQUATOR} is set during construction time, this method is <i>not</i> called.
     */
    protected boolean areEqual(T newKey, T oldKey) {
        return equator.areEqual(newKey, oldKey);
    }
    
	/**
	 * Determines the (hash) code used to store a key.
     * The default implementation calls <code>getCode(key)</code> on the equator.
	 */
	protected int getCode(T key) {
	    return equator.getCode(key);
	}

    /** 
     * Signals if all objects with the same code are considered equal,
     * i.e., if {@link #areEqual(Object, Object)} always returns <code>true</code>.
     * If so, the equality test can be skipped.
     * @return if <code>true</code>, {@link #areEqual(Object, Object)} always returns <code>true</code>
     */
    protected boolean allEqual() {
    	return equator.allEqual();
    }
    
	/**
	 * Returns the index in {@link #tree} of a tree node pointing to (the first instance
	 * of) a given code.
	 * @param code the code we are looking for
	 * @return either <code>-1</code> if <code>code</code> does not occur in
	 * the set, or an index in {@link #tree} such that <code>codes[-tree[result]]==code</code>
	 */
	private int indexOf(int code) {
		// local copy of store, for efficiency
		int[] tree = this.tree;
		// current search position
		int oldIndexPlusOffset = code & rootMask;
		int index = tree[oldIndexPlusOffset];
		if (index > 0) {
			int search = code >>> rootResolution;
			int mask = this.mask;
			int resolution = this.resolution;
			index = tree[oldIndexPlusOffset = (index + (search & mask))];
			while (index > 0) {
				search >>>= resolution;
				index = tree[oldIndexPlusOffset = (index + (search & mask))];
			}
		}
		if (index == 0 || codes[-index] != code) {
			// the code is a new one
			return -1;
		} else {
			// we've found the code
			return oldIndexPlusOffset;
		}
	}
	
	/**
     * Tests if a given key is in the key chain starting at a given index.
     * @param newKey the key to be found
     * @param keyIndex the index in {@link #keys} where to start looking for <code>key</code>
     * @return <code>true</code> if <code>key</code> is found
     */
    private boolean containsAt(T newKey, int keyIndex) {
    	Object[] keys = this.keys;
    	Object oldKey = keys[keyIndex];
    	// walk the list of MyListEntries, if any
    	while (oldKey instanceof MyListEntry) {
    		MyListEntry<T> entry = (MyListEntry) oldKey;
    		if (areEqual(newKey, entry.getValue())) {
    			return true;
    		} else {
    			oldKey = keys[entry.getNext()];
    		}
    	}
    	return areEqual(newKey, (T) oldKey);
    }
    
	/**
	 * Reserves space for a new tree branch, and returns the index of the first
	 * position of the new branch.
	 */
    private int newBranchIndex() {
        int result = size == 0 ? 0 : this.treeSize;
        int upper = result + (result == 0 ? rootMask+1 : mask+1);
        if (upper > tree.length) {
    		// extend the length of the next array
    		int[] newTree = new int[(int) (GROWTH_FACTOR * upper)];
    		System.arraycopy(tree, 0, newTree, 0, tree.length);
    		if (SIZE_PRINT) {
    			System.out.printf("Set %s (size %d) from %d to %d tree nodes %n", System.identityHashCode(this), size, tree.length, newTree.length);
    		}
			tree = newTree;
		} else {
			// clean the new fragment of the next array
			Arrays.fill(tree, result, upper, 0);
		}
    	this.treeSize = upper;
    	return result;
    }
    
    private void disposeBranchIndex(int branchIndex) {
    	tree[branchIndex] = 0;
    }
    
    /**
     * Inserts a new code/key pair at the next available place in the {@link #codes} 
     * and {@link #keys} arrays, and returns the index of the new position.
     * The index is always positive.
     * @param code the code to be inserted
     * @param key the key to be inserted; it is assumed that <code>code == key.hashCode()</code>.
     * @return the index in {@link #codes} where <code>code</code> is stored,
     * resp. in {@link #keys} where <code>key</code> is stored
     */
    private int newKeyIndex(int code, T key) {
    	assert code == getCode(key) : "Key "+key+" should have hash code "+code+", but has "+getCode(key);
    	int result = freeKeyIndex;
		if (result == 0) {
			result = (this.maxKeyIndex += 1);
			if (result >= keys.length) {
				Object[] newKeys = new Object[(int) (GROWTH_FACTOR * result + 1)];
	    		if (SIZE_PRINT) {
	    			System.out.printf("Set %s (size %d) from %d to %d keys %n", System.identityHashCode(this), size, keys.length, newKeys.length);
	    		}
				System.arraycopy(keys, 0, newKeys, 0, keys.length);
				keys = newKeys;
				int[] newCodes = new int[(int) (GROWTH_FACTOR * result + 1)];
				System.arraycopy(codes, 0, newCodes, 0, codes.length);
				codes = newCodes;
			}
		} else {
			freeKeyIndex = codes[result];
		}
		codes[result] = code;
    	keys[result] = key;
    	size++;
    	return result;
    }
    
    /**
     * Disposes the key at a given index, and adds the position to the free key chain.
     * @param keyIndex the index that we want to free
     */
    private void disposeKeyIndex(int keyIndex) {
    	keys[keyIndex] = null;
    	codes[keyIndex] = freeKeyIndex;
    	freeKeyIndex = keyIndex;
    	size--;
    	if (size == 0) {
    		treeSize = 0;
    	}
    }
//    
//    /**
//     * Adds a key for an already existing code.
//     * The key is not added if it equals one of the keys already stored
//     * for this code
//     * @param code the code of the key to be added; should equal <code>key.hashCode()</code>
//     * @param key the key to be added
//     * @param keyIndex the index in {@link #keys} where the first existing key
//     * with code <code>code</code> is stored
//     * @return <code>true</code> if no existing key was equal to <code>key</code>, according
//     * to {@link #areEqual(Object, Object)}.
//     */
//    private boolean addEqualKey(int code, Object key, int keyIndex) {
//        if (hashcodeEquator) {
//            return false;
//        } else {
//            // get local copies for efficieny
//            Object[] keys = this.keys;
//            Object oldKey = keys[keyIndex];
//            // as long as the key is a MyListEntry, walk through the list
//            while (oldKey instanceof MyListEntry) {
//                MyListEntry entry = (MyListEntry) oldKey;
//                if (areEqual(entry.getValue(), key)) {
//                    // the key existed already
//                    return false;
//                } else {
//                    // walk on
//                    oldKey = keys[keyIndex = entry.getNext()];
//                }
//            }
//            assert oldKey != null;
//            // we've reached the end of the list
//            if (areEqual(oldKey, key)) {
//                return false;
//            } else {
//                // it's really a new key
//                MyListEntry newEntry = new MyListEntry(oldKey, newKeyIndex(code, key));
//                this.keys[keyIndex] = newEntry;
//                return true;
//            }
//        }
//    }
    
    /**
     * Adds a key for an already existing code.
     * The key is not added if it equals one of the keys already stored
     * for this code
     * @param code the code of the key to be added; should equal <code>key.hashCode()</code>
     * @param newKey the key to be added
     * @param keyIndex the index in {@link #keys} where the first existing key
     * with code <code>code</code> is stored
     * @return <code>true</code> if no existing key was equal to <code>key</code>, according
     * to {@link #areEqual(Object, Object)}.
     */
    private T putEqualKey(int code, T newKey, int keyIndex) {
        if (allEqual()) {
            return (T) this.keys[keyIndex];
        } else {
            // get local copies for efficiency
            Object[] keys = this.keys;
            Object key = keys[keyIndex];
            // as long as the key is a MyListEntry, walk through the list
            while (key instanceof MyListEntry) {
                MyListEntry<T> entry = (MyListEntry) key;
                T value = entry.getValue();
                if (areEqual(newKey, value)) {
                    // the key existed already
                    return value;
                } else {
                    // walk on
                    key = keys[keyIndex = entry.getNext()];
                }
            }
            assert key != null;
            T oldKey = (T) key;
            // we've reached the end of the list
            if (areEqual(newKey, oldKey)) {
                return oldKey;
            } else {
                // it's really a new key
                MyListEntry<T> newEntry = new MyListEntry<T>(oldKey, newKeyIndex(code, newKey));
                this.keys[keyIndex] = newEntry;
                return null;
            }
        }
    }
    
    /**
	 * Array holding the tree structure.
	 */
    private int[] tree;
    /**
     * The currently reserved number of positions in the store.
     */
    private int treeSize;
    /**
     * The index of the first free key in the free key chain
     */
    private int freeKeyIndex;
    /**
     * The highest index in {@link #keys} that is currently in use.
     */
    private int maxKeyIndex;
    /**
     * The number of elements in the store.
     */
    int size;
    /**
     * The key codes.
     */
    private int[] codes;
    /**
     * The array of current keys.
     */
    Object[] keys;
    /**
     * Number of bits involved in the root branch.
     */
    private final int rootResolution;
    /**
     * The mask of the branch value within a key.
     * This equals <code>2^rootResolution - 1</code>.
     */
    private final int rootMask;
    /**
     * Number of bits involved in a single branch.
     */
    private final int resolution;
    /**
     * The mask of the branch value within a key.
     * This equals <code>2^resolution - 1</code>.
     */
    private final int mask;
    /**
     * The strategy to compare keys whose hashcodes are equal.
     */
    private final Equator<T> equator;
    
    /** Returns an equator which calls {@link #equals(Object)} to determine equality. */
    static public <E> Equator<E> equalsEquator() {
        return EQUALS_EQUATOR;
    }
    
    /** Returns an equator which only compares hash codes to determine equality. */
    static public <E> Equator<E> hashCodeEquator() {
        return HASHCODE_EQUATOR;
    }
    
    /** Returns an equator which uses object identity to determine equality. */
    static public <E> Equator<E> identityEquator() {
        return IDENTITY_EQUATOR;
    }
    
    /** Returns the number of {@link MyListEntry} instances. */
    static public int getMyListEntryCount() {
    	return MyListEntry.instanceCount;
    }
    
    /**
     * The default initial capacity of the set.
     */
    static public final int DEFAULT_CAPACITY = 16;

    /**
     * The default resolution of the tree branches.
     */
    static public final int DEFAULT_RESOLUTION = 3;
    /**
     * The default resolution of the root branch.
     */
    static public final int DEFAULT_ROOT_RESOLUTION = 4;
    /**
     * Equator that calls {@link Object#hashCode()} in <code>Equator.getCode(Object)</code> and
     * {@link Object#equals(java.lang.Object)} in <code>Equator.areEqual(Object, Object)</code>.
     */
    static public final Equator EQUALS_EQUATOR = new Equator() {
        /**
         * @return <code>key.hashCode()</code>.
         */
        public int getCode(Object key) {
            return key.hashCode();
        }

        /**
         * @return <code>true</code> if <code>o1.equals(o2)</code>.
         */
        public boolean areEqual(Object o1, Object o2) {
            return o1.equals(o2);
        }

        /** This implementation returns <code>false</code> always. */
        public boolean allEqual() {
            return false;
        }
    };
    
    /**
     * Equator that calls {@link System#identityHashCode(Object)} in <code>Equator.getCode(Object)</code> and
     * object equality in <code>Equator.areEqual(Object, Object)</code>.
     */
    static public final Equator IDENTITY_EQUATOR = new Equator() {
        /**
         * @return <code>System.identityHashCode(key)</code> 
         */
        public int getCode(Object key) {
            return System.identityHashCode(key);
        }

        /**
         * @return <code>true</code> if <code>o1 == o2</code>.
         */
        public boolean areEqual(Object o1, Object o2) {
            return o1 == o2;
        }

        /** This implementation returns <code>false</code> always. */
        public boolean allEqual() {
            return false;
        }
    };
    
    /**
     * Equator that calls {@link Object#hashCode()} in <code>Equator.getCode(Object)</code> and
     * always returns <code>true</code> in <code>Equator.areEqual(Object, Object)</code>.
     */
    static public final Equator HASHCODE_EQUATOR = new Equator() {
        /**
         * @return <code>key.hashCode()</code>
         */
        public int getCode(Object key) {
            return key.hashCode();
        }

        /**
         * @return <code>true</code> always.
         */
        public boolean areEqual(Object o1, Object o2) {
            return true;
        }

        /** This implementation returns <code>true</code> always. */
        public boolean allEqual() {
            return true;
        }
    };
    
    /**
     * The equator to be used if none is indicated explicitly.
     * Set to {@link #EQUALS_EQUATOR}.
     */
    static public final Equator DEFAULT_EQUATOR = EQUALS_EQUATOR;
    /**
     * Number of bytes in an <code>int</code>.
     */
    static private final int BYTES_PER_INT = 4;
    /**
     * Number of bytes in an object reference.
     */
    static private final int BYTES_PER_REF = 4;
    /**
     * Number of bytes in an object handle.
     */
    static private final int BYTES_PER_OBJECT = 12;
    /** Factor by which the arrays grow if more space is needed. */
    static private final double GROWTH_FACTOR = 1.5;
    /** Flag indicating that some size statistics should be printed. */
    static private final boolean SIZE_PRINT = false;
    /**
     * Auxiliary class to encode the linked list of distinct entries with the
     * same code.
     * The linking is done through index values, which represent
     * indices in the {@link TreeHashSet#keys}-array.
     */
    static private class MyListEntry<T> {
        /** Constructs an entry with a given value, and a given next index. */
        MyListEntry(T value, int next) {
            this.next = next;
            this.value = value;
            instanceCount++;
        }
        
        /** Returns the index (in #key}s) of the next entry with the same code. */
        int getNext() {
            return next;
        }
        
        /** Returns the value in this entry. */
        T getValue() {
            return value;
        }
        
        /** The value of this entry. */
        private final T value;
        /** The index of the next entry. */
        private final int next;
        
        /** Number of {@link MyListEntry} instances. */
        static int instanceCount;
    }
}
