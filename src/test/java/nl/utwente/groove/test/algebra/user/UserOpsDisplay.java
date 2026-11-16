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

import nl.utwente.groove.annotation.UserDisplay;
import nl.utwente.groove.annotation.UserOperation;
import nl.utwente.groove.util.AIGenerated;

/**
 * Class with a user operation and a display method, which is not allowed
 * as the class is not a user type.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.2, 2026-11")
public class UserOpsDisplay {
    /** A correct user operation. */
    @UserOperation
    public static int two() {
        return 2;
    }

    /** A display method outside a user type. */
    @UserDisplay
    public String show() {
        return "two";
    }
}
