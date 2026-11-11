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
import nl.utwente.groove.annotation.UserType;
import nl.utwente.groove.util.AIGenerated;

/**
 * User type that is correct on its own, but whose operations clash with those of
 * {@link UserTypeIntString}: the accessor of its record component and its annotated
 * method both carry a name that also occurs there (gh #934).
 * The component deliberately has another sort than its namesake, so that a test
 * can tell which of the two accessors is in place.
 * @author Arend Rensink
 * @version $Revision$
 */
@UserType
@AIGenerated("Claude Fable 5.2, 2026-11")
public record UserTypeDuplicateOps(double intField) {
    /** Returns the textual form of the wrapped number. */
    @UserOperation
    public String charAt() {
        return "" + intField();
    }
}
