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
package nl.utwente.groove.util.parse;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Object wrapped in a {@link Fallible}.
 * This essentially combines a (correct) outcome with a set of errors.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class FallibleObject<O> implements Fallible {
    /** Constructs a fallible object. */
    public FallibleObject(O content) {
        this.content = content;
        this.errors = new FormatErrorSet();
    }

    /** Returns the content object. */
    public O get() {
        return this.content;
    }

    private final O content;

    @Override
    public @NonNull FormatErrorSet getErrors() {
        return this.errors;
    }

    private final FormatErrorSet errors;

    /** If the object contains errors, throw an exception.
     * Convenience method for {@code getErrors().throwException()}.
     */
    public void throwException() throws FormatException {
        getErrors().throwException();
    }
}
