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

import org.eclipse.jdt.annotation.NonNull;

/**
 * Pool of objects, used to create canonical representative instances.
 * Canonical instances are identical when they are equal.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Pool<E> extends HashMap<E,E> {
    /** Returns a canonical instance for a given element. */
    public @NonNull E canonical(@NonNull E elem) {
        E result = get(elem);
        if (result == null) {
            synchronized (this) {
                result = get(elem);
                if (result == null) {
                    put(elem, result = elem);
                }
            }
        }
        return result;
    }
}
