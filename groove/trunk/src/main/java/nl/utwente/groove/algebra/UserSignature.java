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
 * $Id: IntSignature.java 6239 2023-10-24 16:02:10Z rensink $
 */
package nl.utwente.groove.algebra;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import nl.utwente.groove.annotation.UserOperation;

/**
 * The signature for the user algebra.
 * @author Arend Rensink
 * @version $Revision: 6239 $
 */
public sealed abstract class UserSignature implements Signature permits UserAlgebra {
    /** Sets the used-defined class containing the operator definitions.
     * All methods with an {@link UserOperation}-annotations are taken as operators.
     */
    public static void setUserClass(Class<?> userClass) {
        UserSignature.userClass = userClass;
    }

    static private Class<?> userClass;

    @SuppressWarnings("null")
    /** Returns the set of operators defined in the user class. */
    public static Set<Operator> getOperators() {
        Set<Operator> result = new HashSet<>();
        var userClass = UserSignature.userClass;
        if (userClass != null) {
            // retrieve the @UserOperation-annotated methods
            for (Method m : userClass.getMethods()) {
                if (m.getAnnotation(UserOperation.class) == null) {
                    continue;
                }

                for (var parType : m.getParameterTypes()) {

                }
            }
        }
        return result;
    }
}
