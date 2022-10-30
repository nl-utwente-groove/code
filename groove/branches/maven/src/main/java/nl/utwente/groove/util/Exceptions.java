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
package nl.utwente.groove.util;

/**
 * Class defining standard exceptions.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Exceptions {
    /** Fixed exception to be thrown at unreachable program locations,
     * e.g., default clauses of switches where all cases have been dealt with.
     */
    public static final RuntimeException UNREACHABLE = new RuntimeException("Unreachable location");

    /** Constructs an {@link IllegalArgumentException} for a given message and set of message parameters. */
    public static final IllegalArgumentException illegalArg(String message, Object... args) {
        return new IllegalArgumentException(String.format(message, args));
    }

    /** Constructs an {@link IllegalStateException} for a given message and set of message parameters. */
    public static final IllegalStateException illegalState(String message, Object... args) {
        return new IllegalStateException(String.format(message, args));
    }

    /** Constructs an {@link UnsupportedOperationException} for a given message and set of message parameters. */
    public static final UnsupportedOperationException unsupportedOp(String message,
        Object... args) {
        return new UnsupportedOperationException(String.format(message, args));
    }

}
