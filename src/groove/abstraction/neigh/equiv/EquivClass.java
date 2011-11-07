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
import groove.util.Fixable;

import java.util.Set;

/**
 * An equivalence class (C) is a set of elements that are similar according to
 * a certain equivalence relation.
 * This interface allows different implementations for different elements, for
 * example, equivalence classes for shape nodes and shape edges.
 * 
 * @author Eduardo Zambon
 */
public interface EquivClass<T extends HostElement> extends Fixable,
        Iterable<T>, Set<T> {

    /** Fixes the equivalence class to avoid modifications. */
    public void setFixed();

    /** Returns true if this equivalence class has just one element. */
    public boolean isSingleton();

    /**
     * Shallow clone. Clones the equivalence class but not the elements. 
     * The clone is not fixed, even if the original is.
     */
    public EquivClass<T> clone();

}
