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
import java.util.ArrayList;
import java.util.HashSet;
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
     */
    public static void setUserClass(String className) {
        Class<?> userClass = null;
        if (!className.isBlank()) {
            try {
                userClass = loadUserClass(className);
            } catch (FormatException exc) {
                // do nothing
            }
        }
        setUserClass(userClass);
    }

    /** Returns the currently installed user-operation defining class. */
    public static Class<?> getUserClass() {
        return userClass;
    }

    /** Loads a class with a given name while checking whether it
     * has suitable user operation definitions.
     */
    public static Class<?> loadUserClass(String className) throws FormatException {
        Class<?> result;
        try {
            var errors = new FormatErrorSet();
            result = ClassLoader.getSystemClassLoader().loadClass(className);
            for (var m : extractMethods(result)) {
                try {
                    new Operator(m);
                } catch (IllegalArgumentException exc) {
                    errors
                        .add("Erroneous user operation in '%s.%s': %s", className, m.getName(),
                             exc.getMessage());
                }
            }
            errors.throwException();
        } catch (ClassNotFoundException exc) {
            throw new FormatException("Class '%s' cannot be loaded", className);
        }
        return result;
    }

    /** (Re)sets the used-defined class containing the operator definitions.
     * All methods with an {@link UserOperation}-annotations are taken as operators.
     */
    private static void setUserClass(Class<?> userClass) {
        if (userClass != UserSignature.userClass) {
            methods.reset();
            resetUsers();
            UserSignature.userClass = userClass;
        }
    }

    /** The class containing user operations. */
    static protected Class<?> userClass;

    /** Returns the set of operators defined in the user class. */
    public static Set<Operator> getOperators() {
        return operators.get();
    }

    /** Lazily computed set of operators in the user class. */
    static private final Factory<Set<Operator>> operators
        = Factory.lazy(UserSignature::computeOperators);

    /** Computes the value of {@link #operators}. */
    static private Set<Operator> computeOperators() {
        Set<Operator> result = new HashSet<>();
        getMethods().stream().map(Operator::new).forEach(result::add);
        return result;
    }

    /** Returns the set of {@link UserOperation}-annotated methods in the user class. */
    static protected Set<Method> getMethods() {
        return methods.get();
    }

    /** Lazily computed set of {@link UserOperation}-annotated methods in the user class. */
    static private final Factory<Set<Method>> methods = Factory.lazy(UserSignature::computeMethods);

    /** Computes the value of {@link #methods}. */
    static private Set<Method> computeMethods() {
        return extractMethods(userClass);
    }

    /** Extracts the {@link UserOperation}-annotated methods from a given class. */
    @SuppressWarnings("null")
    static private Set<Method> extractMethods(Class<?> claz) {
        Set<Method> result = new HashSet<>();
        if (claz != null) {
            // retrieve the @UserOperation-annotated methods
            for (Method m : claz.getMethods()) {
                if (m.getAnnotation(UserOperation.class) == null) {
                    continue;
                }
                result.add(m);
            }
        }
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
