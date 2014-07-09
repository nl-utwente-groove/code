/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.util.collect;

import java.util.HashMap;

/**
 * Pool of objects, used to create canonical representative instances.
 * Canonical instances are identical when they are equal.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Pool<E> extends HashMap<E,E> {
    /** Returns a canonical instance for a given element. */
    public E canonical(E elem) {
        E result = elem;
        E oldResult = put(result, result);
        if (oldResult != null) {
            // there was a canonical instance; put it back into the map
            put(oldResult, oldResult);
            result = oldResult;
        }
        return result;
    }
}
