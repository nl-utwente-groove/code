/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2026 University of Twente
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

import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.annotation.UserDisplay;
import nl.utwente.groove.annotation.UserType;
import nl.utwente.groove.util.AIGenerated;

/**
 * User type with a display method that yields a display string
 * for codes greater than one only.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.2, 2026-11")
@UserType
public record UserTypeDisplayFailing(int code) {
    /** Throws an exception for a negative code, and returns {@code null}
     * for zero and the empty string for one.
     */
    @UserDisplay
    public @Nullable String show() {
        if (code() < 0) {
            throw new IllegalStateException("Negative code " + code());
        } else if (code() == 0) {
            return null;
        } else if (code() == 1) {
            return "";
        } else {
            return "code " + code();
        }
    }
}
