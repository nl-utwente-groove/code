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

import groove.graph.Node;

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
public class EquivClass<T> extends HashSet<T> {

    /** 
     * Method to downcast the elements of the equivalence class, in order
     * to keep the stupid type checker of the compiler happy.
     * Will throw a ClassCastException if called when the elements are not
     * a sub-type of Node. Use it with care.
     */
    @SuppressWarnings("unchecked")
    public Set<Node> downcast() {
        return (Set<Node>) this;
    }

    /** Specialises the return type of the super method. */
    @Override
    @SuppressWarnings("unchecked")
    public EquivClass<T> clone() {
        return (EquivClass<T>) super.clone();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        for (T elem : this) {
            result = prime * result + elem.hashCode();
        }
        return result;
    }

}
