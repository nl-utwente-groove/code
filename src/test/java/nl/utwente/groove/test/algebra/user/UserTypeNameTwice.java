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
package nl.utwente.groove.test.algebra.user;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.annotation.UserOperation;
import nl.utwente.groove.annotation.UserType;
import nl.utwente.groove.util.AIGenerated;

/**
 * Erroneous user type: next to its implementation of {@link UserComparable#sameAs},
 * it has an annotated overload of that method for its own type, which could
 * implement the operation as well (gh #933).
 * @author Arend Rensink
 * @version $Revision$
 */
@UserType
@NonNullByDefault
@AIGenerated("Claude Fable 5.2, 2026-11")
public record UserTypeNameTwice(int value) implements UserComparable {
    @Override
    @UserOperation
    public boolean sameAs(UserComparable other) {
        return equals(other);
    }

    /** Tests if this value is the same as another of this type. */
    @UserOperation
    public boolean sameAs(UserTypeNameTwice other) {
        return value() == other.value();
    }
}
