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
package nl.utwente.groove.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import nl.utwente.groove.util.AIGenerated;

/**
 * Annotation for the display method of a {@link UserType}-annotated record.
 * The method thus annotated should be public and non-static, have no parameters
 * and have return type {@code String}; a user type can have at most one such method.
 * <p>
 * The state display of the Simulator shows a value of a user type with a display
 * method by the string that the method returns, rather than by the constructor term
 * of the value, which gets wide for a type with many fields. The constructor term
 * is still shown in the tooltip of the node, and whenever aspect prefixes are shown.
 * <p>
 * The display string is shown only: the value itself and the way it is written,
 * saved and parsed, which is always the constructor term, do not change. Editors,
 * saved graphs and transition labels therefore keep showing the constructor term.
 * If the method throws an exception or returns {@code null} or the empty string,
 * the constructor term is shown as well.
 * <p>
 * The annotation is separate from {@code toString()} on purpose: every record has
 * a {@code toString()}, which is typically meant for debugging. A display method
 * may call it, or be called by it. A display method may be a {@link UserOperation}
 * as well.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.2, 2026-11")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface UserDisplay {
    // empty
}
