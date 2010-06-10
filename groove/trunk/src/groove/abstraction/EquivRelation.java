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
import java.util.Iterator;

/**
 * EDUARDO
 * @author Eduardo Zambon
 * @version $Revision $
 */
public class EquivRelation<T extends Node> extends
        HashSet<EquivClass<T>> {

    /** EDUARDO */
    public EquivClass<T> getEquivClassOf(T node) {
        EquivClass<T> result = null;
        Iterator<EquivClass<T>> iterator = this.iterator();
        while (result == null && iterator.hasNext()) {
            EquivClass<T> ec = iterator.next();
            if (ec.contains(node)) {
                result = ec;
            }
        }
        return result;
    }

    /** EDUARDO */
    public boolean areEquivalent(T n0, T n1) {
        EquivClass<T> ec0 = this.getEquivClassOf(n0);
        EquivClass<T> ec1 = this.getEquivClassOf(n1);
        return ec0 == ec1;
    }
}
