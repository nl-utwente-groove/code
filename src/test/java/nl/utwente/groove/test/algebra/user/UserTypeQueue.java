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
 * User type that shares the names of its fields and operations with
 * {@link UserTypeStack}, with the same signatures: a named queue of numbers,
 * with the head as first element (gh #933).
 * Where the signature has a user value other than the receiving object,
 * the types differ: each of the two has its own type there.
 * @author Arend Rensink
 * @version $Revision$
 */
@UserType
@NonNullByDefault
@AIGenerated("Claude Fable 5.2, 2026-11")
public record UserTypeQueue(String name, IntSeq items) {
    /** Returns the element that is next in line, which is the one added first. */
    @UserOperation
    public int next() {
        return items().get(0);
    }

    /** Returns this queue extended by a given element. */
    @UserOperation
    public UserTypeQueue push(int item) {
        return new UserTypeQueue(name(), items().append(item));
    }

    /** Tests if this queue holds the same elements as another. */
    @UserOperation
    public boolean sameItems(UserTypeQueue other) {
        return items().equals(other.items());
    }
}
