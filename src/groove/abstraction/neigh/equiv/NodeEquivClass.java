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
 * $Id$
 */
package groove.abstraction.neigh.equiv;

import groove.trans.HostElement;
import groove.trans.HostFactory;
import groove.trans.HostNode;

import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;

/**
 * An equivalence class implementation for nodes (both from host graphs and
 * shapes). This class is implemented as bit set instead of a hash set for
 * efficiency. Node numbers are used as indices in the bit set, so in order to
 * keep the objects of this class small, it is necessary that the number of
 * nodes in the store also be small.
 * 
 * @author Eduardo Zambon
 */
public final class NodeEquivClass<T extends HostNode> extends BitSet implements
        EquivClass<T> {

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    /** Flag for indicating if the equivalence class is fixed. */
    private boolean fixed;
    /**
     * Simple counter to avoid the need to go over the whole set to discover
     * its size.
     */
    private int elemCount;
    /** Factory reference used to retrieve node objects. */
    private final HostFactory factory;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Basic constructor. */
    public NodeEquivClass(HostFactory factory) {
        super();
        this.factory = factory;
        this.elemCount = 0;
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public void setFixed() {
        this.fixed = true;
    }

    @Override
    public boolean isFixed() {
        return this.fixed;
    }

    @Override
    public void testFixed(boolean fixed) {
        if (this.isFixed() != fixed) {
            throw new IllegalStateException();
        }
    }

    /**
     * Specialises the return type of the super method.
     * Shallow clone. Clones the equivalence class but not the elements. 
     * The clone is not fixed, even if the original is.
     */
    @Override
    @SuppressWarnings("unchecked")
    public NodeEquivClass<T> clone() {
        NodeEquivClass<T> clone = (NodeEquivClass<T>) super.clone();
        clone.fixed = false;
        return clone;
    }

    /** Creates and returns a new iterator for elements of this class. */
    @Override
    public Iterator<T> iterator() {
        return new MyIterator();
    }

    /** Fast containment check based on element numbers. */
    @Override
    public boolean contains(Object o) {
        boolean result = false;
        if (o instanceof HostElement) {
            int idx = ((HostElement) o).getNumber();
            result = this.get(idx);
        }
        return result;
    }

    /** Creates and returns a new array with all elements of this class. */
    @Override
    public Object[] toArray() {
        Object result[] = new Object[this.elemCount];
        int i = 0;
        for (Object obj : this) {
            result[i] = obj;
            i++;
        }
        return result;
    }

    /**
     * Stores the elements of this class in the given array, if possible.
     * Otherwise creates and returns a new array.
     */
    @Override
    @SuppressWarnings({"hiding", "unchecked"})
    public <T> T[] toArray(T[] a) {
        if (a.length <= this.elemCount) {
            int i = 0;
            for (Object obj : this) {
                a[i] = (T) obj;
                i++;
            }
            return a;
        } else {
            return (T[]) this.toArray();
        }
    }

    /** Checks the containment for all elements of the given collection. */
    @Override
    public boolean containsAll(Collection<?> c) {
        boolean result = true;
        for (Object obj : c) {
            result &= this.contains(obj);
            if (!result) {
                break;
            }
        }
        return result;
    }

    /**
     * Adds all elements of the given collection to the equivalence class.
     * Fails in an assertion if the class is fixed.
     */
    @Override
    public boolean addAll(Collection<? extends T> c) {
        assert !this.isFixed();
        boolean changed = false;
        for (T obj : c) {
            changed |= this.add(obj);
        }
        return changed;
    }

    /** Throws an UnsupportedOperationException. */
    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * Removes all elements of the given collection from the equivalence class.
     * Fails in an assertion if the class is fixed.
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        assert !this.isFixed();
        boolean changed = false;
        for (Object obj : c) {
            changed |= this.remove(obj);
        }
        return changed;
    }

    /**
     * Sets the bit indexed by the object number.
     * Fails in an assertion if the class is fixed.
     */
    @Override
    public boolean add(T obj) {
        assert !this.isFixed();
        int idx = obj.getNumber();
        if (!this.get(idx)) {
            this.set(idx);
            this.elemCount++;
            return true;
        }
        return false;
    }

    /**
     * Clears the bit indexed by the object number.
     * Fails in an assertion if the class is fixed.
     */
    @Override
    public boolean remove(Object obj) {
        assert !this.isFixed();
        assert obj instanceof HostElement;
        int idx = ((HostElement) obj).getNumber();
        if (this.get(idx)) {
            this.clear(idx);
            this.elemCount--;
            return true;
        }
        return false;
    }

    @Override
    public int size() {
        return this.elemCount;
    }

    @Override
    public boolean isSingleton() {
        return this.elemCount == 1;
    }

    // ------------------------------------------------------------------------
    // Inner classes
    // ------------------------------------------------------------------------

    /**
     * Dedicated iterator for node equivalence classes. 
     * 
     * @author Eduardo Zambon
     */
    private class MyIterator implements Iterator<T> {

        /** The number of the node that should be returned by next(). */
        private int curr = NodeEquivClass.this.nextSetBit(0);

        @Override
        public boolean hasNext() {
            return this.curr >= 0;
        }

        /** Returns the current node and computes the next one. */
        @Override
        @SuppressWarnings("unchecked")
        public T next() {
            T elem = (T) NodeEquivClass.this.factory.getNode(this.curr);
            this.curr = NodeEquivClass.this.nextSetBit(this.curr + 1);
            return elem;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

}
