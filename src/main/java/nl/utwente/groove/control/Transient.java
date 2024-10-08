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
package nl.utwente.groove.control;

/**
 * Interface for objects with a non-negative transient depth.
 * A {@link Transient} is called <i>transient</i> if its transient depth is positive,
 * and <i>steady</i> otherwise.
 * @author Arend Rensink
 * @version $Revision$
 */
public interface Transient {
    /** Returns the transience level (a.k.a. transient depth) of this object. */
    public int getTransience();

    /** Indicates if this object is transient, i.e., has positive transient depth. */
    default public boolean isTransient() {
        return getTransience() > 0;
    }

    /** Indicates if this object is steady, i.e., has zero transient depth. */
    default public boolean isSteady() {
        return getTransience() == 0;
    }
}
