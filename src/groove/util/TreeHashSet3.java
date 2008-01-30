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
/*
 * $Id: TreeHashSet3.java,v 1.5 2008-01-30 09:32:13 iovka Exp $
 */
package groove.util;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Set implementation that uses a search tree over "hash" code.
 * If the number of elements is small or the keys are evenly distributed, this 
 * outperforms the {@link java.util.HashSet}. 
 * @author Arend Rensink
 * @version $Revision: 1.5 $ $Date: 2008-01-30 09:32:13 $
 */
public class TreeHashSet3<T> extends AbstractSet<T> {
	/**
	 * Interface used for testing whether two elements with the same hash
	 * code are actually equal.
	 */
	static public interface Equator {
	    public int getCode(Object key);
        
		/**
		 * Method that determines if two objects, with the same hash codes, are
		 * actually to be considered equal.
		 * This is used when adding objects to the set: an object will only be added
		 * if it is not equal to an already included object, in the sense established
		 * by the equator.
		 * The method should only be called if <code>o1.hashcode() == o2.hashcode()</code>.
		 * @param o1 the first object to be compared
		 * @param o2 the second object to be compared
		 * @return <code>true</code> if <code>o1</code> and <code>o2</code> are to be considered equal.
		 * @see TreeHashSet3#EQUALS_EQUATOR
		 * @see TreeHashSet3#IDENTITY_EQUATOR
		 * @see TreeHashSet3#HASHCODE_EQUATOR
		 */
		public boolean areEqual(Object o1, Object o2);
	}
	
	/**
	 * Auxiliary class to encode the linked list of equal entries with the
	 * same code.
	 * The linking is done through <code>int</code>-values, which represent
	 * indices in the {@link TreeHashSet3#keys}-array.
	 */
	static private class MyListEntry<T> {
    	MyListEntry(T value, int next) {
    		this.next = next;
    		this.value = value;
    	}
    	
		public int getNext() {
			return next;
		}

		public void setNext(int next) {
			this.next = next;
		}
    	
		public T getValue() {
			return value;
		}
    	
		private final T value;
		private int next;
    }

	/**
	 * Equator that calls {@link Object#hashCode()} in {@link groove.util.TreeHashSet3.Equator#getCode(Object)} and
     * {@link Object#equals(java.lang.Object)} in {@link groove.util.TreeHashSet3.Equator#areEqual(Object, Object)}.
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
	};
	
	/**
	 * Equator that calls {@link System#identityHashCode(Object)} in {@link Equator#getCode(Object)} and
     * object equality in {@link groove.util.TreeHashSet3.Equator#areEqual(Object, Object)}.
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
	};
	
	/**
     * Equator that calls {@link Object#hashCode()} in {@link groove.util.TreeHashSet3.Equator#getCode(Object)} and
     * always returns <code>true</code> in {@link groove.util.TreeHashSet3.Equator#areEqual(Object, Object)}.
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
	};
	
	/**
	 * The equator to be used if none is indicated explicitly.
	 * Set to {@link #EQUALS_EQUATOR}.
	 */
	static public final Equator DEFAULT_EQUATOR = EQUALS_EQUATOR;
	/**
	 * The default initial capacity of the set.
	 */
	static public final int DEFAULT_CAPACITY = 16;
	/**
	 * The default resolution of the set.
	 */
	static public final int DEFAULT_RESOLUTION = 2;
	/**
	 * Number of bytes in an <code>int</code>.
	 */
	static private final int BYTES_PER_INT = 4;
	/**
	 * Number of bits in an object reference.
	 */
	static private final int BYTES_PER_REF = 4;

	/**
	 * Creates an instance of a tree store set with a given branch resolution,
	 * initial capacity, and equator.
	 * @param capacity the initial capacity of the set
	 * @param equator the equator used for deciding equality of objects in the set
	 */
	public TreeHashSet3(int capacity, Equator equator) {
		if (RESOLUTION < 1) {
			throw new IllegalArgumentException("Resolution should be at least 1");
		}
		this.equator = equator;
		this.hashcodeEquator = (equator == HASHCODE_EQUATOR);
		// initialize the keys and tree
		this.codes = new int[1 + capacity];
		this.keys = new Object[1 + capacity];
		this.tree = new int[capacity];
	}
	
	public TreeHashSet3(int capacity) {
		this(capacity, DEFAULT_EQUATOR);
	}
	
	public TreeHashSet3(Equator equator) {
		this(DEFAULT_CAPACITY, equator);
	}
	
	public TreeHashSet3() {
		this(DEFAULT_CAPACITY);
	}
	
	public TreeHashSet3(Collection<? extends T> obj, int capacity, Equator equator) {
		this(capacity, equator);
		if (equalsType(obj)) {
			TreeHashSet3<? extends T> other = (TreeHashSet3<? extends T>) obj;
			int otherTreeSize = other.treeSize;
			if (this.tree.length < otherTreeSize) {
				this.tree = new int[otherTreeSize];
			}
			System.arraycopy(other.tree, 0, this.tree, 0, otherTreeSize);
			int otherMaxKeyIndex = other.maxKeyIndex;
			if (this.codes.length <= otherMaxKeyIndex) {
				this.codes = new int[otherMaxKeyIndex + DEFAULT_RESOLUTION];
				this.keys = new Object[otherMaxKeyIndex + DEFAULT_RESOLUTION];
			}
			System.arraycopy(other.codes, 0, this.codes, 0, otherMaxKeyIndex+1);
			System.arraycopy(other.keys, 0, this.keys, 0, otherMaxKeyIndex+1);
			this.size = other.size;
			this.treeSize = otherTreeSize;
			this.freeKeyIndex = other.freeKeyIndex;
			this.maxKeyIndex = otherMaxKeyIndex;
			assert this.equals(obj) : "Clone    "+this+" does not equal\noriginal "+obj;
		} else {
			addAll(obj);
		}
	}
	
	public TreeHashSet3(Collection<? extends T> obj, Equator equator) {
		this(obj, obj instanceof TreeHashSet3 ? 0 : obj.size(), equator);
	}
	
	public TreeHashSet3(Collection<? extends T> other) {
		this(other, other instanceof TreeHashSet3 ? ((TreeHashSet3)other).equator : DEFAULT_EQUATOR);
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
					Object[] keys = this.keys;
					do {
						next = keys[index];
						index++;
					} while (next == null);
					this.next = next instanceof MyListEntry ? ((MyListEntry<? extends T>) next).getValue() : (T) next;
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
			private final Object[] keys = TreeHashSet3.this.keys;
			/**
			 * The index in {@link TreeHashSet3#keys} where we're currently at.
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
            tree[index + (code & ROOT_MASK)] = -newKeyIndex(code, key);
            return null;
        } else {
            // local copy of store, for efficiency
            int[] tree = this.tree;
            // precise node where the current value of index was retrieved from
            int indexPlusOffset = code & ROOT_MASK;
            // current search position
            int index = tree[indexPlusOffset];
            // remaining search key
            int search = code >>> ROOT_RESOLUTION;
            // current depth search, in number of bits
            int depth = ROOT_RESOLUTION;
            while (index > 0) {
                index = tree[indexPlusOffset = (index + (search & MASK))];
                search >>>= RESOLUTION;
                depth += RESOLUTION;
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
                    while ((newOffset = (search & MASK)) == (oldOffset = (oldSearch & MASK))) {
                        newIndex = newBranchIndex();
                        index = (tree = this.tree)[index + newOffset] = newIndex;
                        search >>>= RESOLUTION;
                        oldSearch >>>= RESOLUTION;
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
	public boolean remove(Object key) {
		if (size == 0) {
			return false;
		}
		int code = getCode(key);
		int index = indexOf(code);
		// the current position in the key list
		int keyIndex = -tree[index];
		if (keyIndex == 0 || codes[keyIndex] != code) {
			// the key is a new one
			return false;
		} else if (hashcodeEquator) {
			// we've found an existing key code and we're only looking at codes
			// so the key found at index should be removed
			disposeBranchIndex(index);
			disposeKeyIndex(keyIndex);
			return true;
		} else {
			// we've found an existing key code but now we're going to compare
			Object[] keys = this.keys;
			// the key retrieved from the key list
			Object knownKey = keys[keyIndex];
			// index of the previous key in the chain, if any (0 means none)
			int prevKeyIndex = 0;
			// walk the list of MyListEntries, if any
			while (knownKey instanceof MyListEntry) {
				MyListEntry<T> entry = (MyListEntry<T>) knownKey;
				int nextKeyIndex = entry.getNext();
				if (areEqual(entry.getValue(), key)) {
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
			if (areEqual(knownKey, key)) {
				// maybe we have to adapt the tree
				if (prevKeyIndex == 0) {
					// there is no chain, so we have to adapt the tree
					disposeBranchIndex(index);
				} else {
					// the prvious key has to be converted from a 
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
	
	/**
	 * We have to override this method, since the super implementation may
	 * try to invoke {@link Iterator#remove()} on the set's iterator, which is
	 * not allowed.
	 * This implementation always iterates over the argument and calls {@link #remove(Object)}
	 * for each object.
	 */
    @Override
	public boolean removeAll(Collection<?> set) {
		boolean result = false;
		for (Object elem: set) {
			result |= remove(elem);
		}
		return result;
	}

    @Override
	public boolean contains(Object key) {
		if (size == 0) {
			return false;
		} else {
			int code = getCode(key);
			int keyIndex = -tree[indexOf(code)];
			if (keyIndex == 0 || codes[keyIndex] != code) {
				// the key is a new one
				return false;
			} else {
				// we've found an existing key code
				return hashcodeEquator || containsAt(key, keyIndex);
			}
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
		return (treeSpace + codesSpace + keysSpace) / (double) size;
	}
	
	/*
    *//**
     * This implementation tries to make use of the internal structure,
     * if <code>other</code> is a set of the same type as this one.
     *//*
	public boolean containsAll(Collection other) {
	// the implementation does not pay off
        if (equalsType(other)) {
            return containsAll((TreeHashSet3) other);
        } else {
            return super.containsAll(other);
        }
    }*/
    
    /**
     * Determines whether two objects, that are already determined to have the
     * same key codes, are to be considered equal for the purpose of this set.
     * The default implementation calls <code>areEqual(key, otherKey)</code> on the equator.
     * If a the {@link #HASHCODE_EQUATOR} is set during construction time, this method is <i>not</i> called.
     */
    protected boolean areEqual(Object key, Object otherKey) {
        return equator.areEqual(key, otherKey);
    }
    
	/**
	 * Determines the (hash) code used to store a key.
     * The default implementation calls <code>getCode(key)</code> on the equator.
	 */
	protected int getCode(Object key) {
	    return equator.getCode(key);
	}

	/**
	 * Returns an index in {@link #tree} of the leaf corresponding to a 
	 * given code. Either <code>tree[result] == 0</code> or <code>-tree[result]</code>
	 * represents an index in {@link #codes}.
	 */
	private int indexOf(int code) {
		// local copy of store, for efficiency
		int[] tree = this.tree;
		// current search position
		int oldIndexPlusOffset = code & ROOT_MASK;
		int index = tree[oldIndexPlusOffset];
		if (index > 0) {
			code >>>= ROOT_RESOLUTION;
			index = tree[oldIndexPlusOffset = (index + (code & MASK))];
			while (index > 0) {
				code >>>= RESOLUTION;
				index = tree[oldIndexPlusOffset = (index + (code & MASK))];
			}
		}
		return oldIndexPlusOffset;
	}
	
	/**
	 * Tests of the dynamic type of another object equals that of this one,
	 * i.e., it has the same dynamic type (to make sure that
	 * {@link #getCode(Object)} and {@link #areEqual(Object, Object)} are the
	 * same), with the same {@link #RESOLUTION} and {@link #equator}. Used in
	 * {@link #containsAll(Set)} to determine if the local containment test can
	 * be used.
	 */
	private boolean equalsType(Object obj) {
		return getClass() == obj.getClass()
			&& equator == ((TreeHashSet3) obj).equator;
	}
//
//    /**
//     * Tests whether this set is contained antirely in another {@link TreeHashSet3}.
//     * It is assumed that both sets have the same resolution, code computation and equator.
//     * This method is called from {@link #containsAll(Set)} for efficiency, if the
//     * parameter is of the right kind.
//     */
//	private boolean containsAll(TreeHashSet3 other) {
//        // if we're empty, we're done straight away
//	    if (size == 0) {
//			return true;
//		}
//		// now start traversing the trees
//		int queueSize = width * (TOTAL_BIT_COUNT / resolution + 1);
//		// the queue of branches from my tree yet to be checked
//		int[] myQueue = new int[queueSize];
//		// the queue of branches from the other tree yet to be checked
//		int[] hisQueue = new int[queueSize];
//		// the depth (in the tree) of each queue element
//		int[] queueDepth = new int[queueSize];
//		queueDepth[0] = 1;
//		// the current queue index
//		int queueIndex = 1;
//		// my tree stored locally for efficiency
//		int[] myTree = this.tree;
//		// my codes stored locally for efficiency
//		int[] myCodes = this.codes;
//		// the other's tree stored locally for efficiency
//		int[] hisTree = other.tree;
//		// the other's codes stored locally for efficiency
//		int[] hisCodes = other.codes;
//		while (queueIndex > 0) {
//			int myBranch = myQueue[queueIndex];
//			int otherBranch = hisQueue[queueIndex];
//			int depth = queueDepth[queueIndex];
//			queueIndex--;
//			if (otherBranch <= 0 && depth > 1) {
//				// we're at a part where the other has resolved to
//				// emptyness or a value; my tree 
//				// may have branches, but they must resolve to the same
//				int otherCode = hisCodes[-otherBranch];
//				for (int offset = 0; offset < width; offset++) {
//					int myIndex = myTree[myBranch + offset];
//					if (myIndex < 0) {
//						if (myCodes[-myIndex] != otherCode) {
//							return false;
//						} else if (!containedInKeyChain(-myIndex, other, -otherBranch)) {
//							return false;
//						}
//					} else if (myIndex > 0) {
//						queueIndex++;
//						myQueue[queueIndex] = myIndex;
//						hisQueue[queueIndex] = otherBranch;
//						queueDepth[queueIndex] = depth+1;
//					}
//				}
//			} else {
//				for (int offset = 0; offset < width; offset++) {
//					int myIndex = myTree[myBranch + offset];
//					int otherIndex = hisTree[otherBranch + offset];
//					if (myIndex < 0) {
//						// we have a real code; find it in the other tree
//						if (otherIndex == 0) {
//							// no, no codes any more in the other tree
//							return false;
//						} else if (otherIndex > 0) {
//							// the other tree has another branch
//							// it's best to look directly for the value
//							otherIndex = other.indexOf(myCodes[-myIndex], depth, otherIndex);
//							if (otherIndex < 0) {
//								return false;
//							} else if (!containedInKeyChain(-myIndex, other, -hisTree[otherIndex])) {
//								return false;
//							}
//						} else if (myCodes[-myIndex] != hisCodes[-otherIndex]) {
//							// both have codes, but they are different
//							return false;
//						} else if (!containedInKeyChain(-myIndex, other, -otherIndex)) {
//							// both have the same code, but the containment for the code is not ok
//							return false;
//						}
//					} else if (myIndex > 0) {
//						// my tree has another branch
//						queueIndex++;
//						myQueue[queueIndex] = myIndex;
//						hisQueue[queueIndex] = otherIndex;
//						queueDepth[queueIndex] = depth+1;
//					}
//					// we don't have to test inverse containment, since the sizes are equal
//				}
//			}
//		}
//		return true;
//	}
//	
	/**
     * Tests if a given key is in the kay chain starting at a given index.
     * @param key the key to be found
     * @param keyIndex the index in {@link #keys} where to start looking for <code>key</code>
     * @return <code>true</code> if <code>key</code> is found
     */
    private boolean containsAt(Object key, int keyIndex) {
    	Object[] keys = this.keys;
    	Object oldKey = keys[keyIndex];
    	// walk the list of MyListEntries, if any
    	while (oldKey instanceof MyListEntry) {
    		MyListEntry<T> entry = (MyListEntry) oldKey;
    		if (areEqual(entry.getValue(), key)) {
    			return true;
    		} else {
    			oldKey = keys[entry.getNext()];
    		}
    	}
    	return areEqual(oldKey, key);
    }
	
	/**
	 * Reserves space for a new tree branch, and returns the index of the first
	 * position of the new branch.
	 */
    private int newBranchIndex() {
    	int result = size == 0 ? 0 : this.treeSize;
    	int upper = result + (result == 0 ? ROOT_WIDTH : WIDTH);
    	if (upper > tree.length) {
    		// extend the length of the next array
    		int[] newTree = new int[(int) (1.4 * upper)];
    		System.arraycopy(tree, 0, newTree, 0, tree.length);
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
     * @parak key the key to be inserted; it is assumed that <code>code == key.hashCode()</code>.
     * @return the index in {@link #codes} where <code>code</code> is stored,
     * resp. in {@link #keys} where <code>key</code> is stored
     */
    private int newKeyIndex(int code, Object key) {
    	assert code == getCode(key) : "Key "+key+" should have hash code "+code+", but has "+getCode(key);
    	int result = freeKeyIndex;
		if (result == 0) {
			result = (this.maxKeyIndex += 1);
			if (result >= keys.length) {
				Object[] newKeys = new Object[(int) (1.5 * result + 1)];
				System.arraycopy(keys, 0, newKeys, 0, keys.length);
				keys = newKeys;
				int[] newCodes = new int[(int) (1.5 * result + 1)];
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
     * @param key the key to be added
     * @param keyIndex the index in {@link #keys} where the first existing key
     * with code <code>code</code> is stored
     * @return <code>true</code> if no existing key was equal to <code>key</code>, according
     * to {@link #areEqual(Object, Object)}.
     */
    private T putEqualKey(int code, T key, int keyIndex) {
        if (hashcodeEquator) {
            return (T) this.keys[keyIndex];
        } else {
            // get local copies for efficieny
            Object[] keys = this.keys;
            Object oldKey = keys[keyIndex];
            // as long as the key is a MyListEntry, walk through the list
            while (oldKey instanceof MyListEntry) {
                MyListEntry<T> entry = (MyListEntry) oldKey;
                T value = entry.getValue();
                if (areEqual(value, key)) {
                    // the key existed already
                    return value;
                } else {
                    // walk on
                    oldKey = keys[keyIndex = entry.getNext()];
                }
            }
            assert oldKey != null;
            // we've reached the end of the list
            if (areEqual(oldKey, key)) {
                return (T) oldKey;
            } else {
                // it's really a new key
                MyListEntry<T> newEntry = new MyListEntry<T>((T) oldKey, newKeyIndex(code, key));
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
     * Number of bits involved in a single branch.
     */
    static private final int RESOLUTION = 3;
    /**
     * Number of bits involved in the root.
     */
    static private final int ROOT_RESOLUTION = 4;
    /**
     * The width of a single branch.
     * This equals <code>2^resolution</code>.
     */
    static private final int WIDTH = 1<<RESOLUTION;
    /**
     * The width of the root branch.
     * This equals <code>2^ROOT_RESOLUTION</code>.
     */
    static private final int ROOT_WIDTH = 1<<ROOT_RESOLUTION;
    /**
     * The mask of the branch value within a key.
     * This equals <code>width - 1</code>.
     */
    static private final int MASK = WIDTH-1;
    /**
     * The mask of the root branch value.
     * This equals <code>ROOT_WIDTH - 1</code>.
     */
    static private final int ROOT_MASK = ROOT_WIDTH-1;
    /**
     * The strategy to compare keys whose hashcodes are equal.
     */
    private final Equator equator;
    /**
     * Flag to signal no real equality test is necessary (i.e., it is always true)
     */
    private final boolean hashcodeEquator;
}