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
 * Marks code that was substantially written by an AI assistant.
 * The annotation documents provenance, not quality: annotated code has been
 * reviewed like any other, but readers should know its origin.
 * <p>
 * Usage convention:
 * <ul>
 * <li>New classes written wholesale by an AI assistant carry the annotation at
 * the type level; wholly new members added to existing classes carry it at the
 * member level. Small edits to existing code are not marked (version history
 * covers that granularity).
 * <li>Whoever substantially rewrites an annotated element removes or retains
 * the annotation as part of that change, so that it does not go stale.
 * </ul>
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude, 2026-08")
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD})
public @interface AIGenerated {
    /** Free-form provenance note, typically the model and date of writing,
     * e.g. {@code "Claude, 2026-08"}. */
    String value() default "";
}
