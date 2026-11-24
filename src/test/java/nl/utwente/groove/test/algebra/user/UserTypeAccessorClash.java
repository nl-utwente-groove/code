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
 * User type that is correct on its own, but has an operation of its own with the name
 * of an accessor generated for the sequence-valued field {@link UserContainer#items()},
 * without implementing that interface; so it is a duplicate (gh #933).
 * @author Arend Rensink
 * @version $Revision$
 */
@UserType
@NonNullByDefault
@AIGenerated("Claude Opus 5.5, 2026-11")
public record UserTypeAccessorClash(int count) {
    /** Returns the number of items, like the accessor generated for a sequence of them. */
    @UserOperation
    public int itemsSize() {
        return count();
    }
}
