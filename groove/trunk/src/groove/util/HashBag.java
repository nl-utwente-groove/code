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
 * $Id: HashBag.java,v 1.5 2008-01-30 09:32:10 iovka Exp $
 */
package groove.util;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A bag (= multiset) of elements, based on an underlying hash map.
 * @author Arend Rensink
 * @version $Revision$
 */
public class HashBag<T> extends AbstractCollection<T> implements Cloneable, Bag<T> {
    /**
     * Models the multiplicity of an element in a bag.
     * The multiplicity value is initially 1, and never becomes zero.
     */
    protected class MyMultiplicity implements Multiplicity, Cloneable, Comparable<Object> {
        /**
         * Constructs a fresh multiplicity, with initial value 1.
         * @ensure <tt>getValue() == 1</tt>
         */
        protected MyMultiplicity() {
            value = 1;
            incSize();
        }

        /**
         * Returns the current multiplicity value.
         * @return The multiplicity value
         * @ensure <tt>result > 0</tt>
         */
        public int getValue() {
            assert value >= 0;
            return value;
        }

        @Override
        public String toString() {
            return "" + value;
        }

        // ------------------------ object overrides --------------------

        /** Returns the current multiplicity value as a hash code. */
        @Override
        public int hashCode() {
            return value;
        }

        /**
         * Two <tt>Multiplicity</tt> objects are considered equal if they
         * contain the same values.
         */
        @Override
        public boolean equals(Object obj) {
            return obj instanceof HashBag.MyMultiplicity && ((MyMultiplicity) obj).value == value;
        }

        @Override
        public MyMultiplicity clone() {
            try {
                return (MyMultiplicity) super.clone();
            } catch (CloneNotSupportedException exc) {
                assert false;
                return null;
            }
        }
        
        public int compareTo(Object o) {
            return hashCode()-o.hashCode();
        }

        /**
         * Increases the multiplicity value by 1.
         */
        protected int inc() {
            value++;
            incSize();
            assert value > 0;
            return value;
        }

        /**
         * Decreases the multiplicity value by 1.
         * If the multiplicity becomes zero, it should be removed from the bag.
         */
        protected int dec() {
            assert value > 0;
            value--;
            decSize();
            return value;
        }

        /**
         * The current multiplicity value.
         * @invariant <tt>value > 0</tt>
         */
        private int value;
    }

    @Override
    public boolean contains(Object key) {
        return bag.containsKey(key);
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            public boolean hasNext() {
                if (count == 0) {
                    return entryIter.hasNext();
                } else {
                    return true;
                }
            }

            public T next() {
                if (count == 0) {
                    nextEntry();
                }
                count--;
                removed = false;
                return entry.getKey();
            }

            public void remove() {
                if (removed) {
                    throw new IllegalStateException();
                } else {
                    try {
                        if (mult.dec() == 0) {
                            entryIter.remove();
                        }
                    } catch (IllegalStateException exc) {
                        entryIter.remove();
                    }
                    removed = true;
                }
            }

            private void nextEntry() {
                entry = entryIter.next();
                mult = entry.getValue();
                count = mult.getValue();
            }

            private final Iterator<Map.Entry<T,MyMultiplicity>> entryIter = bag.entrySet().iterator();
            private Map.Entry<T,MyMultiplicity> entry;
            private MyMultiplicity mult;
            private int count;
            private boolean removed = true;
        };
    }

    @Override
    public int size() {
        assert size == computeSize() : "Stored size " + size + " differs from actual size " + computeSize();
        return size;
    }

    /**
     * Returns the set of elements in this bag, i.e., the set of keys
     * with positive multiplicity.
     * @return the set of elements occurring in this bag
     */
    public Set<T> elementSet() {
        return bag.keySet();
    }

    /**
     * Returns the multiplicity of a given element in this bag.
     * @ensure <tt>result >= 0</tt>
     */
    public int multiplicity(Object elem) {
        Multiplicity mult = bag.get(elem);
        if (mult == null) {
            return 0;
        } else {
            return mult.getValue();
        }
    }

    /**
     * Returns a mapping from keys to (positive) multiplicities.
     * @ensure <tt>result.keysSet().equals(elementSet())</tt>
     */
    public Map<T,? extends Multiplicity> multiplicityMap() {
        return Collections.unmodifiableMap(bag);
    }

    @Override
    public boolean add(T elem) {
        MyMultiplicity mult = bag.get(elem);
        if (mult == null) {
            bag.put(elem, newMultiplicity());
        } else {
            mult.inc();
        }
        return true;
    }

    @Override
    public void clear() {
        bag.clear();
        size = 0;
    }

    @Override
    public boolean remove(Object elem) {
        return removeGetCount(elem) >= 0;
    }

    /**
     * Removes a copy of an object.
     * The resturn value signifies if this was the last copy.
     * @param elem the object to be removed
     * @return <tt>true</tt> if and only if the last instance of <tt>elem</tt>
     * was removed
     * @see #remove(Object)
     */
    public boolean removeWasLast(Object elem) {
        return removeGetCount(elem) == 0;
    }
    
    public boolean minus(Collection<?> c) {
        boolean result = false;
        for (Object element: c) {
            result |= remove(element);
        }
        return result;
    }

    // -------------------------- object overrides -------------------------------

    /**
     * Returns the sum of all elements' hash codes.
     */
    @Override
    public int hashCode() {
        int result = 0;
        for (Map.Entry<T,MyMultiplicity> entry: bag.entrySet()) {
            result += entry.getKey().hashCode() * entry.getValue().getValue();
        }
        return result;
    }

    /**
     * Returns a shallow clone: elements are shared, multiplicities are
     * copied.
     */
    @Override
    public Object clone() {
        HashBag<T> result = new HashBag<T>();
        for (Map.Entry<T,MyMultiplicity> entry: bag.entrySet()) {
            result.bag.put(entry.getKey(), entry.getValue().clone());
        }
        return result;
    }

    /**
     * Tests whether the other is also a bag, with the same multiplicities.
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof HashBag && ((HashBag<?>) obj).bag.equals(bag);
    }

    /**
     * Returns the underlying map as a string representation of this bag.
     */
    @Override
    public String toString() {
        return bag.toString();
    }

    /**
     * Factory method for a multiplicity object.
     * To be overwritten in subclasses.
     * @return a new multiplicity, with initial value 1
     */
    protected MyMultiplicity newMultiplicity() {
        return new MyMultiplicity();
    }

    /**
     * Removes an element and returns the remaining multiplicity of that element
     * @param elem the element to be removed
     * @return the remaining multiplicity of <tt>elem</tt> atfter removing one instance;
     * <tt>-1</tt> if <tt>elem did not occur in the first place</tt>
     */
    public int removeGetCount(Object elem) {
        MyMultiplicity mult = bag.remove(elem);
        if (mult == null) {
            return -1;
        } else {
            int value = mult.dec();
            if (value > 0) {
                bag.put((T) elem, mult);
            }
            return value;
        }
    }

    /**
     * Internal method to compute the total number of elements (i.e., occurrences)
     * in this multiset.
     */
    private int computeSize() {
        int result = 0;
        for (Map.Entry<T,MyMultiplicity> entry: bag.entrySet()) {
            MyMultiplicity mult = entry.getValue();
            result += mult.getValue();
        }
        return result;
    }
    
    /** Increments the size variable. */
    final void incSize() {
    	size++;
    }
    
    /** Decrements the size variable. */
    final void decSize() {
    	size--;
    }

    /**
     * The underlying mapping from elements to multiplicities.
     * @invariant <tt>bag : Object --> Multiplicity</tt>
     */
    protected final Map<T,MyMultiplicity> bag = new HashMap<T,MyMultiplicity>();
    /**
     * The number of element (occurrences) in this bag.
     * @invariant <tt>size == computeSize()</tt>
     */
    private int size;
}
