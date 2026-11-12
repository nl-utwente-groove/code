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
 * record class. A collection return type is not allowed: an operation yields a single value.
 * The last parameter may instead be a sequence (an {@link IntSeq}, {@link RealSeq},
 * {@link BoolSeq} or {@link StringSeq}, or a {@link UserSeq} of a user type), in which
 * case the operation is called with the elements of the sequence as trailing arguments,
 * any number of them.
 * @author Arend Rensink
 * @version $Revision$
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface UserOperation {
    /** Flag indicating if this is an indeterminate operation. */
    boolean indeterminate() default false;
}
