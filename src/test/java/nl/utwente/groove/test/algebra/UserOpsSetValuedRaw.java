/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2026
 * University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id$
 */
package nl.utwente.groove.test.algebra;

import java.util.Set;

import nl.utwente.groove.annotation.UserOperation;
import nl.utwente.groove.util.AIGenerated;

/**
 * Erroneous example class with user operations: set without element type.
 */
@AIGenerated("Claude Fable 5.2, 2026-11")
public class UserOpsSetValuedRaw {
    /** Set-valued operation whose return type does not reveal the sort of the values. */
    @UserOperation
    static public Set<?> errWildcard(int value) {
        return Set.of(value);
    }
}
