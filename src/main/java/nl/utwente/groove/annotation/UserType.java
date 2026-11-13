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

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation for records that serve as user types.
 * The record thus annotated should be public, and its components should be of type
 * int, double, boolean or String. The record gives rise to a constructor operation
 * named after the record, and for each component an accessor operation named after
 * the component.
 * <p>
 * The last component may instead be a sequence of values of one of those types:
 * an {@link IntSeq}, {@link RealSeq}, {@link BoolSeq} or {@link StringSeq}. For
 * such a component {@code c},
 * <ul>
 * <li> the constructor operation takes the elements of the sequence as trailing
 * arguments, any number of them, and a constant of the type is written likewise;
 * <li> in place of the accessor there are two operations: {@code cAt}, which takes
 * a value of the user type and a position (counting from 0) and yields the element
 * at that position, or an error value if there is none; and {@code cSize}, which
 * yields the number of elements;
 * <li> the name {@code c} itself is reserved: no user operation may have it.
 * </ul>
 * Methods of the record annotated with {@link UserOperation} are operations as well.
 * @author Arend Rensink
 * @version $Revision$
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface UserType {
    // empty
}
