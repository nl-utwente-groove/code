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
package nl.utwente.groove.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for a user-defined algebraic operation.
 * The method thus annotated should be static and only have parameters
 * and return type int, double, boolean or String, or a {@link UserType}-annotated
 * record class.
 * <p>
 * The return type may also be a {@link java.util.Set} (or a subtype, such as
 * {@link java.util.SortedSet}) of {@link Integer}, {@link Double},
 * {@link Boolean}, {@link String} or a {@link UserType}-annotated record class.
 * The operation is then <i>set-valued</i>: in a rule, it is matched once for
 * every value in the set it returns. Other collection types are not allowed:
 * a match cannot tell repeated values apart, so a value that occurs more than once
 * would still be matched only once. To match every occurrence in a list, match the
 * positions instead (for instance through the predefined set-valued operator
 * {@code range}) and look up the value at each position.
 * <p>
 * The order of the values has no meaning in a rule, but it has to be
 * <i>deterministic</i>: the returned set must iterate over its values in an order that
 * depends on those values only, and is therefore the same in every run. This cannot
 * be checked; violating it may cause successive explorations of the same grammar
 * to differ. Use a {@link java.util.LinkedHashSet} filled in a fixed order, or a
 * sorted set. Do not use {@link java.util.Set#of} or {@link java.util.Set#copyOf},
 * whose iteration order varies from run to run, nor a {@link java.util.HashSet} of
 * values with an identity-based hash code (which includes records with an enum component).
 * <p>
 * A set-valued operation cannot be indeterminate.
 * @author Arend Rensink
 * @version $Revision$
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface UserOperation {
    /** Flag indicating if this is an indeterminate operation. */
    boolean indeterminate() default false;
}
