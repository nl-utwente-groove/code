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

import gnu.trove.THashSet;
import groove.trans.HostElement;
import groove.util.Fixable;

/**
 * An equivalence class (C) is a set of elements that are similar according to
 * a certain equivalence relation.
 * This class is essentially a HashSet and it was created mainly to improve the
 * code readability.
 * 
 * @author Eduardo Zambon
 */
public final class EquivClass<T extends HostElement> extends THashSet<T>
        implements Fixable {

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    /**
     * The hash code of this equivalence class. Once computed it cannot be 0.
     * Once it's different than 0, the equivalence class is fixed and no
     * elements can be added or removed. This avoids nasty hashing problems.
     */
    private int hashCode;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Basic constructor. */
    public EquivClass() {
        super();
        this.hashCode = 0;
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    /** Two equivalence classes are equal if they have the same objects. */
    @Override
    public boolean equals(Object o) {
        boolean result;
        if (this == o) {
            result = true;
        } else if (!(o instanceof EquivClass<?>)) {
            result = false;
        } else {
            EquivClass<?> other = (EquivClass<?>) o;
            result = this.containsAll(other) && other.containsAll(this);
        }
        // Check for consistency between equals and hashCode.
        assert (!result || this.hashCode() == o.hashCode());
        return result;
    }

    /** The hash code is computed by {@link #computeHashCode()}. */
    @Override
    final public int hashCode() {
        // Lazy computation because the class may not have been populated yet.
        if (this.hashCode == 0) {
            this.hashCode = this.computeHashCode();
            if (this.hashCode == 0) {
                this.hashCode = -1;
            }
        }
        return this.hashCode;
    }

    /**
     * Specialises the return type of the super method.
     * Shallow clone. Clones the equivalence class but not the elements. 
     * The clone is not fixed, even if the original is.
     */
    @Override
    public EquivClass<T> clone() {
        EquivClass<T> result = (EquivClass<T>) super.clone();
        result.hashCode = 0;
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

    /**
     * If the equivalence class is fixed, fails in an assertion.
     * Otherwise, delegates to super class.
     */
    @Override
    public boolean add(T obj) {
        assert !this.isFixed();
        return super.add(obj);
    }

    /**
     * If the equivalence class is fixed, fails in an assertion.
     * Otherwise, delegates to super class.
     */
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
        int sum = 0;
        for (T elem : this) {
            sum += elem.hashCode();
        }
        return prime * sum;
    }

    /** Returns true if this equivalence class has just one element. */
    public boolean isSingleton() {
        return this.size() == 1;
    }

}
