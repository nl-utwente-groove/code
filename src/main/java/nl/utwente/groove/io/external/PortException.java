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
package nl.utwente.groove.io.external;

/**
 * Exception used within exporting system.
 * @author Harold
 * @version $Revision$
 */
public class PortException extends Exception {
    /** Creates an empty exception. */
    public PortException() {
        // empty by design
    }

    /** Creates an exception based on another exception. */
    public PortException(Throwable cause) {
        super(cause);
    }

    /** Creates an exception with a message.
     * The message is formatted through a call to {@link String#format(String, Object...)}
     */
    public PortException(String message, Object... args) {
        super(message.formatted(args));
    }
}
