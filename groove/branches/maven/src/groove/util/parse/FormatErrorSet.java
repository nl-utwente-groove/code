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

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * Set of format errors, with additional functionality for
 * adding errors and throwing an exception on the basis of the
 * errors.
 * @author Arend Rensink
 * @version $Revision $
 */
public class FormatErrorSet extends LinkedHashSet<FormatError> {
    /** Constructs a fresh, empty error set. */
    public FormatErrorSet() {
        super();
    }

    /** Constructs a copy of a given set of errors. */
    public FormatErrorSet(Collection<? extends FormatError> c) {
        super(c);
    }

    /** Constructs a singleton error set. */
    public FormatErrorSet(String message, Object... args) {
        add(message, args);
    }

    /** Adds a format error based on a given error message and set of arguments. */
    public boolean add(String message, Object... args) {
        return add(new FormatError(message, args));
    }

    /** Adds a format error based on an existing error and set of additional arguments. */
    public boolean add(FormatError error, Object... args) {
        return add(new FormatError(error, args));
    }

    /**
     * Throws an exception based on this error set if the error set is nonempty.
     * Does nothing otherwise.
     * @throws FormatException if this error set is nonempty.
     */
    public void throwException() throws FormatException {
        if (!isEmpty()) {
            throw new FormatException(this);
        }
    }

    /** Returns a new format error set in which the context information is transferred. */
    public FormatErrorSet transfer(Map<?,?> map) {
        FormatErrorSet result = new FormatErrorSet();
        for (FormatError error : this) {
            result.add(error.transfer(map));
        }
        return result;
    }

    @Override
    public FormatErrorSet clone() {
        return new FormatErrorSet(this);
    }
}
