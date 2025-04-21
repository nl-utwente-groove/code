/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.util.collect;

/** The degree of equality of two objects. */
public enum Likeness {
    /** Two objects are the same. */
    SAME,
    /** Two objects are not the same but are equal, according to their own {@link Object#equals(Object)} method. */
    EQUAL,
    /** Two objects are not equal according to their own {@link Object#equals(Object)} method, but
     * are isomorphic.
     */
    ISO,
    /** Two objects are distinct. */
    DISTINCT;

    /** Indicates if this equality means the objects in question are indeed considered equal,
     * or distinct.
     * The only value for which this method returns {@code false} is {@link #DISTINCT}.
     * @return {@code true} iff this is not {@link #DISTINCT}
     */
    public boolean isAlike() {
        return this != DISTINCT;
    }
}
