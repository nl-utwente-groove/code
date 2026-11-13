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
 * User type whose operations clash with those generated for its sequence-valued field:
 * another field and an operation have the names of the generated accessors,
 * and a further operation has the name of the field itself.
 * @author Arend Rensink
 * @version $Revision$
 */
@UserType
@NonNullByDefault
@AIGenerated("Claude Fable 5.2, 2026-11")
public record UserTypeSeqClash(int itemsSize, IntSeq items) {
    /** Has the name of the generated element accessor. */
    @UserOperation
    public int itemsAt(int index) {
        return items().get(index);
    }

    /** Has the name of the sequence-valued field. */
    @UserOperation
    public int items(int index) {
        return items().get(index);
    }
}
