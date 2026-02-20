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
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import nl.utwente.groove.annotation.UserOperation;
import nl.utwente.groove.util.Callback;
import nl.utwente.groove.util.Factory;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;

/**
 * The signature for the user algebra.
 * @author Arend Rensink
 * @version $Revision: 6239 $
 */
public sealed abstract class UserSignature implements Signature permits UserAlgebra {
    /** Sets the used-defined class containing the operator definitions.
     * All methods with an {@link UserOperation}-annotations are taken as operators.
    * Silently ignores any annotation errors in the class; to check for those, call {@link #checkUserClass(String)} instead
     */
    public static void setUserClass(String className) {
        methods.clear();
        if (!className.isBlank()) {
            try {
                methods.addAll(checkUserClass(className));
            } catch (FormatException exc) {
                // silently ignore the errors
            }
            resetUsers();
        }
    }

    /** Checks whether a class with a given name can be found and has suitable user-defined operations,
     * and returns the loaded class.
     * @throws FormatException if there are annotation errors in the class
     */
    @SuppressWarnings("null")
    public static Set<Method> checkUserClass(String className) throws FormatException {
        var result = new LinkedHashSet<Method>();
        try {
            var errors = new FormatErrorSet();
            var claz = ClassLoader.getSystemClassLoader().loadClass(className);
            // retrieve the @UserOperation-annotated methods
            for (var m : claz.getDeclaredMethods()) {
                if (m.getAnnotation(UserOperation.class) == null) {
                    continue;
                }
                if (!Modifier.isStatic(m.getModifiers())) {
                    errors.add("User operation '%s.%s' is not static", className, m.getName());
                } else if (!m.canAccess(null)) {
                    errors.add("User operation '%s.%s' is not accessible", className, m.getName());
                } else {
                    try {
                        new Operator(m);
                        result.add(m);
                    } catch (IllegalArgumentException exc) {
                        errors
                            .add("Erroneous user operation in '%s.%s': %s", className, m.getName(),
                                 exc.getMessage());
                    }
                }
            }
            errors.throwException();
        } catch (ClassNotFoundException exc) {
            throw new FormatException("Class '%s' cannot be loaded", className);
        }
        return result;
    }

    /** Returns the set of operators defined in the user class. */
    public static Set<Method> getMethods() {
        return methods;
    }

    /** Lazily computed set of operators in the user class. */
    static private final Set<Method> methods = new LinkedHashSet<>();

    /** Returns the set of operators defined in the user class. */
    public static Set<Operator> getOperators() {
        return operators.get();
    }

    /** Lazily computed set of operators in the user class. */
    static private final Factory<Set<Operator>> operators
        = Factory.lazy(UserSignature::computeOperators);

    static private Set<Operator> computeOperators() {
        var result = new LinkedHashSet<Operator>();
        getMethods().stream().map(Operator::new).forEach(result::add);
        return result;
    }

    /** Adds a reset method of a user that needs to be invoked when a new user class is loaded. */
    static public void addUser(Callback user) {
        users.add(user);
    }

    /** List of dependants that need to be reset when a new user class is loaded. */
    static private List<Callback> users = new ArrayList<>();

    /** Calls the {@link Runnable#run()} method on all elements of {@link #users}. */
    static private void resetUsers() {
        users.forEach(Callback::call);
    }
}
