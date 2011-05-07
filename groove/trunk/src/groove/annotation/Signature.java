/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
package groove.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** 
 * Signature of a Prolog predicate.
 * The value consists of a series of parameter names, followed
 * by a series of (at least one) allowed I/O specifications for the parameters.
 * <p>
 * An IO specification is a string of characters specifying the
 * directionality of the corresponding parameters,
 * respectively. Possible values are:
 * <dd>
 * <table>
 * <tr><td> <b>+</b> <td> The argument shall be instantiated.
 * <tr><td> <b>?</b> <td> The argument shall be instantiated or a variable.
 * <tr><td> <b>@</b> <td> The argument shall remain unaltered.
 * <tr><td> <b>-</b> <td> The argument shall be a variable that will be instantiated
 * </table>
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Signature {
    /** The signature value. */
    String[] value();
}
