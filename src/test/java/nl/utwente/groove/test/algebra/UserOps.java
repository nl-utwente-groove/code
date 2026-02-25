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
package nl.utwente.groove.test.algebra;

import nl.utwente.groove.annotation.UserOperation;

/**
 * Example class with user operations.
 * @author Arend Rensink
 * @version $Revision$
 */
public class UserOps {
    /** Returns a random integer number between 0 and 9 (inclusive). */
    @UserOperation(indeterminate = true)
    static public int randomInt(int bound) {
        return (int) (bound * Math.random());
    }

    /** Returns the square root of its parameter. */
    @UserOperation
    static public double sqrt(double num) {
        return Math.sqrt(num);
    }

    /** Returns the number one. */
    @UserOperation
    static public int one() {
        return 1;
    }

    /** Constructs a value of a user type. */
    @UserOperation
    static public UserTypeIntBool get(int nr, boolean truth) {
        return new UserTypeIntBool(nr, truth);
    }
}
