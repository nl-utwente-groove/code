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

import java.util.Set;

/**
 * General interface for objects that may contain stored errors.
 * @author Arend Rensink
 * @version $Id$
 */
public interface Fallible {
    /** Indicates if this expression has any errors. */
    public boolean hasErrors();

    /** Returns the errors in this object. */
    public FormatErrorSet getErrors();

    /** Adds an error to this object. */
    public void addError(FormatError error);

    /** Adds multiple errors to this object. */
    public void addErrors(Set<FormatError> errors);
}
