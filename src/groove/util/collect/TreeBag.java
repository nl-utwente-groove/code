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
 * $Id$
 */
package groove.util.collect;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * A bag (= multiset) of elements, based on an underlying tree map.
 * @author Arend Rensink
 * @version $Revision$
 */
public class TreeBag<T> extends AbstractCollection<T> implements Cloneable, Bag<T> {
    /**
     * Models the multiplicity of an element in a bag. The multiplicity value is
     * initially 1, and never becomes zero.
     */
    protected class MyMultiplicity implements Multiplicity, Cloneable {
        /**
         * Constructs a fresh multiplicity, with initial value 1.
         * @ensure <tt>getValue() == 1</tt>
         */
        protected MyMultiplicity() {
            this.value = 1;
            incSize();
        }

        /**
         * Returns the current multiplicity value.
         * @return The multiplicity value
         * @ensure <tt>result > 0</tt>
         */
        @Override
        public int getValue() {
            assert this.value >= 0;
            return this.value;
        }

        @Override
        public String toString() {
            return "" + this.value;
        }

        // ------------------------ object overrides --------------------

        /** Returns the current multiplicity value as a hash code. */
        @Override
        public int hashCode() {
            return this.value;
        }

        /**
         * Two <tt>Multiplicity</tt> objects are considered equal if they
         * contain the same values.
         */
        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(Object obj) {
            return obj instanceof Multiplicity && ((MyMultiplicity) obj).value == this.value;
        }

        @SuppressWarnings("unchecked")
        @Override
        public MyMultiplicity clone() {
            try {
                return (MyMultiplicity) super.clone();
            } catch (CloneNotSupportedException exc) {
                assert false;
                return null;
            }
        }

        /**
         * Increases the multiplicity value by 1.
         */
        protected int inc() {
            this.value++;
            incSize();
            assert this.value > 0;
            return this.value;
        }

        /**
         * Decreases the multiplicity value by 1. If the multiplicity becomes
         * zero, it should be removed from the bag.
         */
        protected int dec() {
            assert this.value > 0;
            this.value--;
            decSize();
            return this.value;
        }

        /**
         * The current multiplicity value.
         * @invariant <tt>value > 0</tt>
         */
        private int value;
    }

    @Override
    public boolean contains(Object key) {
        return this.bag.containsKey(key);
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                if (this.count == 0) {
                    return this.entryIter.hasNext();
                } else {
                    return true;
                }
            }

            @Override
            public T next() {
                if (this.count == 0) {
                    nextEntry();
                }
                this.count--;
                this.removed = false;
                return this.entry.getKey();
            }

            @Override
            public void remove() {
                if (this.removed) {
                    throw new IllegalStateException();
                } else {
                    try {
                        if (this.mult.dec() == 0) {
                            this.entryIter.remove();
                        }
                    } catch (IllegalStateException exc) {
                        this.entryIter.remove();
                    }
                    this.removed = true;
                }
            }

            private void nextEntry() {
                this.entry = this.entryIter.next();
                this.mult = this.entry.getValue();
                this.count = this.mult.getValue();
            }

            private final Iterator<Map.Entry<T,MyMultiplicity>> entryIter =
                TreeBag.this.bag.entrySet().iterator();
            private Map.Entry<T,MyMultiplicity> entry;
            private MyMultiplicity mult;
            private int count;
            private boolean removed = true;
        };
    }

    @Override
    public int size() {
        assert this.size == computeSize() : "Stored size " + this.size
            + " differs from actual size " + computeSize();
        return this.size;
    }

    /**
     * Returns the set of elements in this bag, i.e., the set of keys with
     * positive multiplicity.
     * @return the set of elements occurring in this bag
     */
    @Override
    public Set<T> elementSet() {
        return this.bag.keySet();
    }

    /**
     * Returns the multiplicity of a given element in this bag.
     * @ensure <tt>result >= 0</tt>
     */
    @Override
    public int multiplicity(Object elem) {
        MyMultiplicity mult = this.bag.get(elem);
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
    @Override
    public Map<T,? extends Multiplicity> multiplicityMap() {
        return Collections.unmodifiableMap(this.bag);
    }

    @Override
    public boolean add(T elem) {
        MyMultiplicity mult = this.bag.get(elem);
        if (mult == null) {
            this.bag.put(elem, newMultiplicity());
        } else {
            mult.inc();
        }
        return true;
    }

    @Override
    public void clear() {
        this.bag.clear();
        this.size = 0;
    }

    /**
     * Removes a single copy of an object.
     * @see #removeWasLast(Object)
     */
    @Override
    public boolean remove(Object elem) {
        return removeGetCount(elem) >= 0;
    }

    /**
     * Removes a copy of an object. The return value signifies if this was the
     * last copy.
     * @param elem the object to be removed
     * @return <tt>true</tt> if and only if the last instance of <tt>elem</tt>
     *         was removed
     * @see #remove(Object)
     */
    @Override
    public boolean removeWasLast(Object elem) {
        return removeGetCount(elem) == 0;
    }

    /**
     * Removes an element and returns the remaining multiplicity of that element
     * @param elem the element to be removed
     * @return the remaining multiplicity of <tt>elem</tt> atfter removing one
     *         instance; <tt>-1</tt> if
     *         <tt>elem did not occur in the first place</tt>
     */
    @Override
    @SuppressWarnings("unchecked")
    public int removeGetCount(Object elem) {
        MyMultiplicity mult = this.bag.remove(elem);
        if (mult == null) {
            return -1;
        } else {
            int value = mult.dec();
            if (value > 0) {
                this.bag.put((T) elem, mult);
            }
            return value;
        }
    }

    @Override
    public boolean minus(Collection<?> c) {
        boolean result = false;
        for (Object elem : c) {
            result |= remove(elem);
        }
        return result;
    }

    // -------------------------- object overrides
    // -------------------------------

    /**
     * Returns the sum of all elements' hash codes.
     */
    @SuppressWarnings("unchecked")
    @Override
    public int hashCode() {
        int result = 0;
        for (Map.Entry<T,MyMultiplicity> entry : this.bag.entrySet()) {
            result += entry.getKey().hashCode() * ((MyMultiplicity) entry).getValue();
        }
        return result;
    }

    /**
     * Returns a shallow clone: elements are shared, multiplicities are copied.
     */
    @Override
    public Object clone() {
        TreeBag<T> result = new TreeBag<>();
        for (Map.Entry<T,MyMultiplicity> entry : this.bag.entrySet()) {
            result.bag.put(entry.getKey(), entry.getValue().clone());
        }
        return result;
    }

    /**
     * Tests whether the other is also a bag, with the same multiplicities.
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Bag<?> && ((TreeBag<?>) obj).bag.equals(this.bag);
    }

    /**
     * Returns the underlying map as a string representation of this bag.
     */
    @Override
    public String toString() {
        return this.bag.toString();
    }

    /**
     * Factory method for a multiplicity object. To be overwritten in
     * subclasses.
     * @return a new multiplicity, with initial value 1
     */
    protected MyMultiplicity newMultiplicity() {
        return new MyMultiplicity();
    }

    /**
     * Internal method to compute the total number of elements (i.e.,
     * occurrences) in this mutliset.
     */
    private int computeSize() {
        int result = 0;
        for (Map.Entry<T,MyMultiplicity> entry : this.bag.entrySet()) {
            MyMultiplicity mult = entry.getValue();
            result += mult.getValue();
        }
        return result;
    }

    /** Increases the size variable. */
    final void incSize() {
        this.size++;
    }

    /** Increases the size variable. */
    final void decSize() {
        this.size--;
    }

    /**
     * The underying mapping from elements to multiplicites.
     * @invariant <tt>bag : Object --> Multiplicity</tt>
     */
    final Map<T,MyMultiplicity> bag = new TreeMap<>();
    /**
     * The number of element (occurrences) in this bag.
     * @invariant <tt>size == computeSize()</tt>
     */
    private int size;
}
