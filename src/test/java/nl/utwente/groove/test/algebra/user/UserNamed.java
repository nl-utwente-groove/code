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

import nl.utwente.groove.annotation.UserInterface;
import nl.utwente.groove.annotation.UserOperation;
import nl.utwente.groove.util.AIGenerated;

/**
 * User interface of the user types that have a name, which they implement by
 * a field (gh #933).
 * @author Arend Rensink
 * @version $Revision$
 */
@UserInterface
@NonNullByDefault
@AIGenerated("Claude Opus 5.5, 2026-11")
public interface UserNamed {
    /** Returns the name of this value. */
    @UserOperation
    String name();
}
