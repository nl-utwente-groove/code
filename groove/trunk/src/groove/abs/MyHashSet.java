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
package groove.abs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Vector;

/**
 * A set of objects of type <T> with additional getAndAdd operation that makes
 * atomic the test of containment followed by adding an element. This may be
 * useful when the test of equality for elements of T is costly. This
 * implementation is based on a hash table and is parametrisable by an Equalizer
 * for testing equality (equivalence) of objects. Ensures constant time for
 * basic operations (provided a good hash function). A Hasher for computing the
 * hash code may be provided on construction. Remove operation is not
 * implemented. Does not support the null element.
 * @author Iovka Boneva
 * @version $Revision $
 * @param <T>
 */
public class MyHashSet<T> implements Iterable<T>, Set<T> {

    /** Defines an equivalence relation over the objects of the same type. */
    public interface Equalizer<T> {
        /**
         * @param o1
         * @param o2
         * @return true if o1 and o2 are equal for the equivalence relation
         */
        public boolean areEqual(T o1, T o2);
    }

    /**
     * Defines an equivalence relation over the objects of the same type and a
     * compatible hash function.
     */
    public interface Hasher<T> extends Equalizer<T> {

        /**
         * @param o
         * @return the hash code of object o
         * @ensure areEqual(o1,o2) implies getHashCode(o1) == getHashCode(o2)
         */
        public int getHashCode(T o);
    }

    /**
     * Checks whether the set contains an object equivalent to o
     * @param o
     * @return true if the set contains an object equivalent to o, false
     *         otherwise
     */
    @SuppressWarnings("unchecked")
    public boolean contains(Object o) {
        try {
            return this.get((T) o) != null;
        } catch (ClassCastException e) {
            return false;
        }
    }

    /**
     * A combination of a get and an add operation. This operation makes atomic
     * a get (or containment operation) followed by an add operation in case the
     * object is not yet in the set. When the element <code>o</code> is to be
     * added, the cost of the operation is the same as for an add only (i.e. the
     * test for containment is comes for free).
     * @invariant getAndAdd(o) has the same effect as { e = get(o); if (e ==
     *            null) { add(o); } return e; }
     * @param o
     * @return null if the object o was added to the set; the object equal to o
     *         contained in the set otherwise
     */
    public T getAndAdd(T o) {
        List<T> list = getElementsWithCode(getHash(o));
        for (T e : list) {
            if (this.hasher.areEqual(o, e)) {
                return e;
            }
        }
        // no equivalent element found, add o
        list.add(o);
        this.size++;
        return null;
    }

    /**
     * Returns the object equal to <code>o</code> that is contained in this
     * set, if any.
     * @param o
     * @return the object equal to <code>o</code> contained in the set, if
     *         there is such an object; null otherwise
     */
    public T get(T o) {
        for (T e : getElementsWithCode(getHash(o))) {
            if (this.hasher.areEqual(o, e)) {
                return e;
            }
        }
        return null;
    }

    /**
     * Returns an iterator over whe values contained in this set.
     * @return an iterator over the values in the set
     */
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            /**
             * The result of the following call of next() method, or null if
             * there is no next element. This field can be set by the hasNext()
             * method when it tests for the existence of a next element.
             */
            private T next;
            /** An iterator on the current bucket list of values. */
            private ListIterator<T> currentBucketIt;
            /** Index of the current bucket in the store. */
            private int currentIdx;

            {
                this.currentIdx = 0;
                this.currentBucketIt =
                    MyHashSet.this.getElementsWithCode(0).listIterator();
                this.next = null;
            } // Initialisation

            public boolean hasNext() {
                if (this.next != null) {
                    return true;
                }
                try {
                    this.goToNext();
                } catch (NoSuchElementException e) {
                    return false;
                }
                return true;
            }

            /** @ensure this.next == null */
            public T next() {
                // if next has a value (i.e. this.next != null) -> return
                // this.next
                // else, 2 possibilities
                // 1. no next element
                // 2. next element not computed
                // in both cases, try to go to the next value
                // then if 1. this will raise an exception
                // and if 2. the next value is found and returned
                if (this.next == null) {
                    // exception or this.next is the next value
                    this.goToNext();
                }
                T result = this.next;
                this.next = null;
                return result;
            }

            /** Unsupported operation */
            public void remove() {
                throw new UnsupportedOperationException();
            }

            /**
             * Goes to the next element to be returned by the following call to
             * next()
             * @throws NoSuchElementException when no next element
             */
            private void goToNext() throws NoSuchElementException {
                // if currentBucketIt has a next element, then advance to this
                // next element and return
                // else, advance currendIdx and currentBucketIt to the next
                // bucket and
                // continue searching for the next element (recursive call)
                if (this.currentBucketIt.hasNext()) {
                    this.next = this.currentBucketIt.next();
                    return;
                }
                if (this.currentIdx == MyHashSet.this.store.capacity() - 1) {
                    throw new NoSuchElementException();
                }
                this.currentIdx++;
                this.currentBucketIt =
                    MyHashSet.this.getElementsWithCode(this.currentIdx).listIterator();
                this.goToNext();
            }
        };
    }

    /**
     * @return the number of elements of the set
     */
    public int size() {
        return this.size;
    }

    /**
     * The hash code for an object, depending whether a hasher is provided or
     * not
     * @param o
     * @return the hash code
     */
    private int getHash(T o) {
        return this.hasher.getHashCode(o) % this.storeCapacity;
    }

    /** */
    protected Hasher<T> getHasher() {
        return this.hasher;
    }

    /** The array of elements with given hash code. */
    protected List<T> getElementsWithCode(int hashCode) {
        return this.store.elementAt((hashCode >= 0 ? hashCode : -hashCode)
            % this.storeCapacity);
    }

    // ---------------------------------------------------------------------------
    // FIELDS, CONSTRUCTORS AND STANDARD METHODS
    // ---------------------------------------------------------------------------
    /**
     * Initializes the set. The hasher must be compatible with the equalizer.
     * @param hasher the hasher used for computing hash code. If null, then the
     *        default hashCode() method is used.
     */
    public MyHashSet(Hasher<T> hasher) {
        this.size = 0;
        this.storeCapacity = DEFAULT_STORE_SIZE;
        this.store = new Vector<List<T>>(this.storeCapacity);
        this.store.ensureCapacity(this.storeCapacity);
        this.hasher = hasher != null ? hasher : getDefaultHasher();
        for (int i = 0; i < this.storeCapacity; i++) {
            this.store.add(i, new ArrayList<T>(INITIAL_LIST_CAPACITY));
        }
    }

    /** The hashing function used */
    private final Hasher<T> hasher;

    /** The table of the hash table. */
    Vector<List<T>> store;
    /** The capacity of the store */
    private final int storeCapacity;
    /** The number of elements in the set. */
    private int size;

    /** The default capacity of the set, if not provided by the user. */
    private static final int DEFAULT_STORE_SIZE = 16;
    /**
     * The capacity increment for the lists contained in the buckets for
     * collision management.
     */
    private static final int INITIAL_LIST_CAPACITY = 5;

    /** Should be used once. */
    private Hasher<T> getDefaultHasher() {
        return new Hasher<T>() {
            public boolean areEqual(Object o1, Object o2) {
                return o1.equals(o2);
            }

            public int getHashCode(Object o) {
                return o.hashCode();
            }
        };

    }

    // ---------------------------------------------------------------------------
    // METHODS FROM THE SET INTERFACE
    // ---------------------------------------------------------------------------

    public boolean add(T e) {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public boolean containsAll(Collection<?> c) {
        boolean result = true;
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return result;
    }

    public boolean isEmpty() {
        return size() != 0;
    }

    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    public Object[] toArray() {
        Object[] result = new Object[size()];
        int i = 0;
        for (Object o : this) {
            result[i++] = o;
        }
        return result;
    }

    public <U> U[] toArray(U[] a) {
        throw new UnsupportedOperationException();
    }

}
