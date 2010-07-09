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

import java.util.Collection;
import java.util.HashSet;

/**
 * An equivalence relation is represented as a set of equivalence classes.
 * This class is essentially a HashSet and it was created mainly to improve the
 * code readability.
 * 
 * @author Eduardo Zambon
 */
public class EquivRelation<T> extends HashSet<EquivClass<T>> {

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Empty constructor. */
    public EquivRelation() {
        super();
    }

    /**
     * Constructs a new equivalence relation from the set of equivalence
     * classes given. The equivalence classes are cloned to avoid reference
     * aliasing. 
     */
    public EquivRelation(Collection<? extends EquivClass<T>> elems) {
        super();
        for (EquivClass<T> ec : elems) {
            this.add(ec.clone());
        }
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** 
     * Returns the equivalence class to which the given element belongs.
     * May return null if the element is not in any equivalence class of the
     * equivalence relation.
     * 
     * This method assumes that the equivalence relation is consistent, i.e.,
     * an element belongs only to a single equivalence class.
     *  */
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
        return ec0 == ec1;
    }

}
