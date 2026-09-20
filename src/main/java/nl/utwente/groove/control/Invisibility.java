/*
 * GROOVE: GRaphs for Object Oriented VErification
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
 */
package nl.utwente.groove.control;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.util.AIGenerated;

/**
 * Reason why a callable unit declared in the grammar is not visible
 * in the control program under compilation. Registered through
 * {@link CtrlLoader#addInvisibleControl} and {@link CtrlLoader#addInvisibleRules}
 * to give more informative error messages for calls and imports of such units;
 * see gh #560.
 */
@NonNullByDefault
@AIGenerated("Claude Fable 5, 2026-08")
public enum Invisibility {
    /** The declaring resource is not enabled in the grammar. */
    DISABLED,
    /** The unit (a rule) is enabled but has errors. */
    ERRONEOUS,
    /** The unit is declared in another (enabled) control program, which is
     * not visible because the program under scrutiny is checked in isolation. */
    ISOLATED;
}
