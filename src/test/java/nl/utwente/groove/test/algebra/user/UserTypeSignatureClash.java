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

import nl.utwente.groove.annotation.IntSeq;
import nl.utwente.groove.annotation.UserOperation;
import nl.utwente.groove.annotation.UserType;
import nl.utwente.groove.util.AIGenerated;

/**
 * User type that is correct on its own, but whose field and operations cannot be
 * implementations of the equally named operators of {@link UserTypePoly}, as each
 * of them has another signature there (gh #933): another result sort, a variable
 * argument tail where there is a single argument, and an indeterminate outcome.
 * @author Arend Rensink
 * @version $Revision$
 */
@UserType
@NonNullByDefault
@AIGenerated("Claude Fable 5.2, 2026-11")
public record UserTypeSignatureClash(int name) {
    /** Returns this value, whatever the coefficients. */
    @UserOperation
    public UserTypeSignatureClash append(IntSeq coeffs) {
        return this;
    }

    /** Returns the wrapped number. */
    @UserOperation(indeterminate = true)
    public int degree() {
        return name();
    }
}
