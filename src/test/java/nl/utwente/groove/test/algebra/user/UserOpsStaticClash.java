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
import nl.utwente.groove.util.AIGenerated;

/**
 * Class with a static operation that has the name of an operation of the user
 * interface {@link UserContainer}, implemented by {@link UserTypeStack}. Only the
 * user types that implement a user interface share the names of its operations,
 * so this is a duplicate (gh #933).
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
@AIGenerated("Claude Fable 5.2, 2026-11")
public class UserOpsStaticClash {
    /** Returns a given stack extended by a given element. */
    @UserOperation
    public static UserTypeStack push(UserTypeStack stack, int item) {
        return stack.push(item);
    }
}
