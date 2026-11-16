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
package nl.utwente.groove.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import nl.utwente.groove.util.AIGenerated;

/**
 * Annotation for interfaces that declare operations shared by several user types
 * (gh #933). The interface thus annotated should be public; the {@link UserType}-annotated
 * records that implement it are the user types that share its operations.
 * <p>
 * A non-static method of the interface annotated with {@link UserOperation} is an
 * operation that takes a value of any of the implementing user types as its first
 * argument, before the parameters of the method; it is applied as implemented by
 * the user type of that value. The method may be abstract or have a default implementation.
 * A user type implements it by a field (a record component) or method of that name;
 * the implementing method need not be annotated itself.
 * <p>
 * This is the only way for user types to have a field or operation of the same name:
 * a name that is not declared in a user interface they both implement can be used by
 * one user type only.
 * <p>
 * A method of the interface annotated with {@link UserOperation} that has no parameters
 * and returns a sequence of values of a system sort (an {@link IntSeq}, {@link RealSeq},
 * {@link BoolSeq} or {@link StringSeq}) declares a sequence-valued field: like such a
 * field {@code c} of a user type, it stands for the operations {@code cAt} and {@code cSize}
 * rather than for an operation {@code c}. User types that both have a sequence-valued
 * field of that name have to implement a user interface that declares it.
 * <p>
 * A user interface may be used as the type of a parameter or the return type of a user
 * operation, and it may extend other user interfaces; an operation belongs to the
 * topmost interface that declares it. A user interface need not be listed among the
 * user classes of a grammar: it is found through the user types that implement it.
 * @author Arend Rensink
 * @version $Revision$
 */
@Retention(RUNTIME)
@Target(TYPE)
@AIGenerated("Claude Fable 5.2, 2026-11")
public @interface UserInterface {
    // empty
}
