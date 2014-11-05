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
package groove.io.conceptual.lang;

import groove.io.conceptual.AbstractionException;

/** Exception class for errors occurring during a conceptual import action. */
public class ImportException extends AbstractionException {
    /** Constructs an exception with a given message. */
    public ImportException(String message) {
        super(message);
    }

    /** Constructs an exception based on a given previous exception. */
    public ImportException(Throwable cause) {
        super(cause);
    }

    /** Constructs an exception based on a given previous exception, with a modified
     * error message.
     */
    public ImportException(String message, Throwable cause) {
        super(message, cause);
    }
}
