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

import groove.abstraction.neigh.MyHashSet;
import groove.trans.HostElement;
import groove.util.Fixable;

/**
 * An equivalence relation is represented as a set of equivalence classes.
 * This class is essentially a HashSet and it was created mainly to improve the
 * code readability.
 * 
 * @author Eduardo Zambon
 */
public class EquivRelation<T extends HostElement> extends
        MyHashSet<EquivClass<T>> implements Fixable {

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    /**
     * The hash code of this equivalence relation. Once computed it cannot be 0.
     * Once it's different than 0, the equivalence relation is fixed and no
     * elements can be added or removed. This avoids nasty hashing problems.
     */
    private int hashCode;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Basic constructor. */
    public EquivRelation() {
        super();
        this.hashCode = 0;
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    /** Two equivalence relations are equal if they have the same classes. */
    @Override
    public boolean equals(Object o) {
        boolean result;
        if (this == o) {
            result = true;
        } else if (!(o instanceof EquivRelation<?>)) {
            result = false;
        } else {
            EquivClass<?> other = (EquivClass<?>) o;
            if (this.size() != other.size()) {
                result = false;
            } else {
                result = this.containsAll(other) && other.containsAll(this);
            }
        }
        // Check for consistency between equals and hashCode.
        assert (!result || this.hashCode() == o.hashCode());
        return result;
    }

    /** The hash code is computed by {@link #computeHashCode()}. */
    @Override
    final public int hashCode() {
        // Lazy computation because the relation may not have been populated yet.
        if (this.hashCode == 0) {
            this.hashCode = this.computeHashCode();
            if (this.hashCode == 0) {
                this.hashCode = -1;
            }
        }
        return this.hashCode;
    }

    /**
     * Deep clone. Clones the equivalence relation and all equivalence classes. 
     */
    @Override
    public EquivRelation<T> clone() {
        EquivRelation<T> result = new EquivRelation<T>();
        for (EquivClass<T> ec : this) {
            result.add(ec.clone());
        }
        return result;
    }

    @Override
    public void setFixed() {
        this.hashCode();
    }

    @Override
    public boolean isFixed() {
        return this.hashCode != 0;
    }

    @Override
    public void testFixed(boolean fixed) {
        if (this.isFixed() != fixed) {
            throw new IllegalStateException();
        }
    }

    @Override
    public boolean add(EquivClass<T> obj) {
        assert !this.isFixed();
        obj.setFixed();
        return super.add(obj);
    }

    @Override
    public boolean remove(Object obj) {
        assert !this.isFixed();
        return super.remove(obj);
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** Callback method computing the hash code. */
    private int computeHashCode() {
        final int prime = 31;
        int result = 0;
        for (EquivClass<T> elem : this) {
            // We can't multiply the result by prime here because this would
            // make the hash dependent of the ordering of elements.
            result += elem.hashCode();
        }
        // Multiply here. This probably least to a worst hash function, but
        // nothing to do for now...
        return result * prime;
    }

    /** 
     * Returns the equivalence class to which the given element belongs.
     * May return null if the element is not in any equivalence class of the
     * equivalence relation.
     * 
     * This method assumes that the equivalence relation is consistent, i.e.,
     * an element belongs only to a single equivalence class.
     */
    public EquivClass<T> getEquivClassOf(T elem) {
        EquivClass<T> result = null;
        for (EquivClass<T> ec : this) {
            if (ec.contains(elem)) {
                result = ec;
                break;
            }
        }
        return result;
    }

    /**
     * Returns true if the given elements are equivalent according to the
     * equivalence relation, i.e., if both elements belong to the same
     * equivalence class.
     */
    public boolean areEquivalent(T elem0, T elem1) {
        EquivClass<T> ec0 = this.getEquivClassOf(elem0);
        EquivClass<T> ec1 = this.getEquivClassOf(elem1);
        // No need to call equals. We want reference equality.
        return ec0 == ec1;
    }

}
