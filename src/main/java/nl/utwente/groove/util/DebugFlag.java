/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
package nl.utwente.groove.util;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a {@code boolean} field as a development-only debug switch.
 * <p>
 * A field carrying this annotation exists to turn on tracing, consistency
 * checks or other diagnostics while developing; it must be {@code false} in
 * committed code. Debug switches and legitimate configuration constants look
 * alike ({@code static final boolean X = ...}), and a switch left on can go
 * unnoticed for years while costing exploration time. The annotation makes the
 * distinction explicit, and the test {@code DebugFlagTest} scans the sources
 * for annotated fields, failing when one is not a {@code boolean} initialised
 * with the literal {@code false}.
 * <p>
 * Do not annotate constants that select behaviour meant for production, such
 * as feature toggles or performance trade-offs, even if they are {@code true}.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface DebugFlag {
    // marker annotation
}
