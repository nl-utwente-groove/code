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
package nl.utwente.groove.algebra;

import nl.utwente.groove.annotation.UserOperation;
import nl.utwente.groove.annotation.UserType;

/**
 * Example user type record class.
 * @author Arend Rensink
 * @version $Revision$
 */
@UserType
public record UserTypeIntBool(int intField, boolean boolField) {
    /** Retrieves the int field of an instance of this type. */
    @UserOperation
    static public int getInt(UserTypeIntBool self) {
        return self.intField();
    }

    /** Tests whether the int field of an instance of this type has a negative value. */
    @UserOperation
    static public boolean isNeg(UserTypeIntBool self) {
        return self.intField() < 0;
    }
}
