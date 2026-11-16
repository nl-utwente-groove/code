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
import nl.utwente.groove.annotation.UserInterface;
import nl.utwente.groove.annotation.UserOperation;
import nl.utwente.groove.util.AIGenerated;

/**
 * User interface of named containers of numbers, implemented by
 * {@link UserTypeStack} and {@link UserTypeQueue} (gh #933). It declares a
 * sequence-valued field, abstract operations, among which some with the
 * implementing type as parameter or return type, and a default operation.
 * @param <T> the implementing type
 * @author Arend Rensink
 * @version $Revision$
 */
@UserInterface
@AIGenerated("Claude Opus 5.5, 2026-11")
@NonNullByDefault
public interface UserContainer<T extends UserContainer<T>> extends UserNamed {
    /** Returns the elements of this container: a sequence-valued field. */
    @UserOperation
    IntSeq items();

    /** Returns the element that is next in line. */
    @UserOperation
    int next();

    /** Returns this container extended by a given element. */
    @UserOperation
    T push(int item);

    /** Tests if this container holds the same elements as another of its type. */
    @UserOperation
    boolean sameItems(T other);

    /** Tests if this container holds no elements. */
    @UserOperation
    default boolean isEmpty() {
        return items().size() == 0;
    }
}
