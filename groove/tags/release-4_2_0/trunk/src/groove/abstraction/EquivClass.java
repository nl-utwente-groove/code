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
package groove.abstraction;

import groove.trans.HostNode;

import java.util.HashSet;
import java.util.Set;

/**
 * An equivalence class (C) is a set of elements that are similar according to
 * a certain equivalence relation.
 * This class is essentially a HashSet and it was created just to improve the
 * code readability.
 * 
 * @author Eduardo Zambon
 */
public final class EquivClass<T> extends HashSet<T> {

    /** 
     * Method to downcast the elements of the equivalence class, in order
     * to keep the stupid type checker of the compiler happy.
     * Will throw a ClassCastException if called when the elements are not
     * a sub-type of Node. Use it with care.
     */
    @SuppressWarnings("unchecked")
    public Set<HostNode> downcast() {
        return (Set<HostNode>) this;
    }

    /** Specialises the return type of the super method. */
    @Override
    @SuppressWarnings("unchecked")
    public EquivClass<T> clone() {
        return (EquivClass<T>) super.clone();
    }

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int sum = 0;
        for (T elem : this) {
            sum += elem.hashCode();
        }
        return prime * sum;
    }

}
