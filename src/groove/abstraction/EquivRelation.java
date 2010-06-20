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
 * EDUARDO
 * @author Eduardo Zambon
 * @version $Revision $
 */
public class EquivRelation<T> extends HashSet<EquivClass<T>> {

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** EDUARDO */
    public EquivRelation() {
        super();
    }

    /** EDUARDO */
    @SuppressWarnings("unchecked")
    public EquivRelation(Collection<? extends EquivClass<T>> elems) {
        super();
        for (EquivClass<T> ec : elems) {
            this.add((EquivClass<T>) ec.clone());
        }
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** EDUARDO */
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

    /** EDUARDO */
    public boolean areEquivalent(T elem0, T elem1) {
        EquivClass<T> ec0 = this.getEquivClassOf(elem0);
        EquivClass<T> ec1 = this.getEquivClassOf(elem1);
        return ec0 == ec1;
    }

}
