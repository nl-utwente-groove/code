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
import nl.utwente.groove.annotation.UserSeq;
import nl.utwente.groove.util.AIGenerated;

/**
 * User operations whose last parameter is a sequence of the values of a user
 * interface, which can hold values of the different user types that
 * implement it (gh #933).
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
@AIGenerated("Claude Fable 5.2, 2026-11")
public class UserOpsInterfaceSeq {
    /** Lists the names of any number of named values, separated by commas. */
    @UserOperation
    static public String names(UserSeq<UserNamed> named) {
        var result = new StringBuilder();
        for (int i = 0; i < named.size(); i++) {
            if (i > 0) {
                result.append(',');
            }
            result.append(named.get(i).name());
        }
        return result.toString();
    }

    /** Counts the items of any number of containers: a generic user interface. */
    @UserOperation
    static public int countItems(UserSeq<UserContainer<?>> containers) {
        int result = 0;
        for (int i = 0; i < containers.size(); i++) {
            result += containers.get(i).items().size();
        }
        return result;
    }
}
