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
package groove.util;

/**
 * Pair of elements of the same type.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Duo<O> extends Pair<O,O> {
    /** Creates a new duo. */
    public Duo(O one, O two) {
        super(one, two);
    }

    /** Constructs and returns a new duo. */
    public static <O> Duo<O> newDuo(O one, O two) {
        return new Duo<>(one, two);
    }
}
