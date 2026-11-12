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
import nl.utwente.groove.annotation.UserSeq;
import nl.utwente.groove.util.AIGenerated;

/**
 * User operations that use a sequence type where it is not allowed.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
@AIGenerated("Claude Fable 5.2, 2026-11")
public class UserOpsSeqErrors {
    /** A sequence as a parameter that is not the last. */
    @UserOperation
    static public int notLast(IntSeq values, int index) {
        return values.get(index);
    }

    /** A sequence as the result. */
    @UserOperation
    static public IntSeq result(int value) {
        return IntSeq.of(value);
    }

    /** A sequence of user values that does not state the type of its elements. */
    @UserOperation
    @SuppressWarnings("rawtypes")
    static public int untyped(UserSeq values) {
        return values.size();
    }
}
