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

/**
 * Comparator class that offers a convenience method for comparing booleans.
 * @author Arend Rensink
 * @version $Revision $
 */
public abstract class Comparator<T> implements java.util.Comparator<T> {
    /** Returns a negative number if the first argument is {@code true}
     * and the second isn't, or a positive number if the first is {@code false}
     * and the second isn't; or zero otherwise.
     */
    protected final int compare(boolean b1, boolean b2) {
        if (b1) {
            return b2 ? 0 : -1;
        } else {
            return b2 ? +1 : 0;
        }
    }
}
