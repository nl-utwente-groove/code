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
package groove.util.parse;

/**
 * General interface for objects that may contain stored errors.
 * @author Arend Rensink
 * @version $Id$
 */
public interface Fallible {
    /** Adds an error to this fallible object. */
    public default void addError(String message, Object... args) {
        getErrors().add(message, args);
    }

    /** Adds an error to this fallible object. */
    public default void addError(FormatError error) {
        getErrors().add(error);
    }

    /** Adds errors to this fallible object. */
    public default void addErrors(FormatErrorSet error) {
        getErrors().addAll(error);
    }

    /** Indicates if this fallible object has any errors. */
    public default boolean hasErrors() {
        return !getErrors().isEmpty();
    }

    /** Returns the errors in this object. */
    public FormatErrorSet getErrors();
}
