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
package nl.utwente.groove.test;

import nl.utwente.groove.annotation.UserOperation;

/**
 * Excample class with user operations.
 * @author Arend Rensink
 * @version $Revision$
 */
public class UserExample {
    /** Returns a random integer number between 0 and 9 (inclusive). */
    @UserOperation(indeterminate = true)
    static public int randomInt() {
        return (int) (10 * Math.random());
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

    /** Retrieves the nth character of a string. */
    @UserOperation
    static public String charAt(String input, int n) {
        return "" + input.charAt(n);
    }

    /** Tests if a given string is a prefix of another. */
    @UserOperation
    static public boolean isPrefix(String prefix, String total) {
        return total.startsWith(prefix);
    }

    /** User operation with erroneous return type. */
    @UserOperation
    static public Object errObjectReturn() {
        return null;
    }

    /** User operation with erroneous void type. */
    @UserOperation
    static public void errVoidReturn() {
        // empty
    }

    /** User operation with erroneous return type. */
    @UserOperation
    static public Object errObjectPar(Object par) {
        return null;
    }
}
