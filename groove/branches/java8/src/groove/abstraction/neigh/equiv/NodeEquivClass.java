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

import groove.grammar.host.HostElement;
import groove.grammar.host.HostFactory;
import groove.grammar.host.HostNode;

import java.util.AbstractSet;
import java.util.BitSet;
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
public final class NodeEquivClass<T extends HostNode> extends AbstractSet<T> implements
    EquivClass<T> {

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Basic constructor. */
    public NodeEquivClass(HostFactory factory) {
        this.bitset = new BitSet();
        this.factory = factory;
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public boolean setFixed() {
        boolean result = !isFixed();
        if (result) {
            this.fixed = true;
        }
        return result;
    }

    @Override
    public boolean isFixed() {
        return this.fixed;
    }

    /**
     * Specialises the return type of the super method.
     * Shallow clone. Clones the equivalence class but not the elements.
     * The clone is not fixed, even if the original is.
     */
    @Override
    public NodeEquivClass<T> clone() {
        NodeEquivClass<T> clone = new NodeEquivClass<T>(this.factory);
        clone.bitset.or(this.bitset);
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
            result = this.bitset.get(idx);
        }
        return result;
    }

    @Override
    public boolean isEmpty() {
        return this.bitset.isEmpty();
    }

    @Override
    public void clear() {
        this.bitset.clear();
    }

    /**
     * Sets the bit indexed by the object number.
     * Fails in an assertion if the class is fixed.
     */
    @Override
    public boolean add(T obj) {
        assert !this.isFixed();
        int idx = obj.getNumber();
        if (!this.bitset.get(idx)) {
            this.bitset.set(idx);
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
        if (this.bitset.get(idx)) {
            this.bitset.clear(idx);
            return true;
        }
        return false;
    }

    @Override
    public int size() {
        return this.bitset.cardinality();
    }

    @Override
    public boolean isSingleton() {
        return size() == 1;
    }

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    /** Flag for indicating if the equivalence class is fixed. */
    private boolean fixed;
    /** Factory reference used to retrieve node objects. */
    private final HostFactory factory;
    /** The actual underlying set. */
    private final BitSet bitset;

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
        private int curr = NodeEquivClass.this.bitset.nextSetBit(0);

        @Override
        public boolean hasNext() {
            return this.curr >= 0;
        }

        /** Returns the current node and computes the next one. */
        @Override
        @SuppressWarnings("unchecked")
        public T next() {
            T elem = (T) NodeEquivClass.this.factory.getNode(this.curr);
            this.curr = NodeEquivClass.this.bitset.nextSetBit(this.curr + 1);
            return elem;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

}
