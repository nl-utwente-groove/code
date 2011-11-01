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
 * An equivalence class (C) is a set of elements that are similar according to
 * a certain equivalence relation.
 * This class is essentially a HashSet and it was created mainly to improve the
 * code readability.
 * 
 * @author Eduardo Zambon
 */
public final class NodeEquivClass<T extends HostNode> extends BitSet implements
        EquivClass<T> {

    private boolean fixed;
    private int elemCount;
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

    /** Basic constructor. */
    public NodeEquivClass(int size, HostFactory factory) {
        super(size);
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

    @Override
    @SuppressWarnings("unchecked")
    public NodeEquivClass<T> clone() {
        NodeEquivClass<T> clone = (NodeEquivClass<T>) super.clone();
        clone.fixed = false;
        return clone;
    }

    @Override
    public Iterator<T> iterator() {
        return new MyIterator();
    }

    @Override
    public boolean contains(Object o) {
        boolean result = false;
        if (o instanceof HostElement) {
            int idx = ((HostElement) o).getNumber();
            result = this.get(idx);
        }
        return result;
    }

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

    @Override
    public boolean addAll(Collection<? extends T> c) {
        assert !this.isFixed();
        boolean changed = false;
        for (T obj : c) {
            changed |= this.add(obj);
        }
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

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
     * If the equivalence class is fixed, fails in an assertion.
     * Otherwise, delegates to super class.
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
     * If the equivalence class is fixed, fails in an assertion.
     * Otherwise, delegates to super class.
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

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    public boolean isSingleton() {
        return this.elemCount == 1;
    }

    private class MyIterator implements Iterator<T> {

        private int curr = NodeEquivClass.this.nextSetBit(0);

        @Override
        public boolean hasNext() {
            return this.curr >= 0;
        }

        @Override
        @SuppressWarnings("unchecked")
        public T next() {
            T elem = (T) NodeEquivClass.this.factory.getNodeFromNr(this.curr);
            this.curr = NodeEquivClass.this.nextSetBit(this.curr + 1);
            return elem;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

}
