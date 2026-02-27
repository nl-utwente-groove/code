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

import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.utwente.groove.annotation.UserOperation;
import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.util.Callback;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.Factory;
import nl.utwente.groove.util.parse.FallibleObject;
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
    * Silently ignores any annotation errors in the class; to check for those, call {@link #checkUserClass} instead
     */
    public static void setUserClass(List<QualName> classNames) {
        methods.clear();
        operators.reset();
        var classes = toClasses(classNames);
        for (var claz : classes.get()) {
            methods.putAll(parseUserClass(claz, classes.get()).get());
        }
        resetUsers();
    }

    /** Checks whether a given set of named classes can be loaded and have suitable user-defined operations,
     * and returns the operations as executables.
     * @throws FormatException if there are annotation errors in the class
     */
    public static Map<Operator,Executable> checkUserClass(List<QualName> classNames) throws FormatException {
        var result = new LinkedHashMap<Operator,Executable>();
        var errors = new FormatErrorSet();
        var classes = toClasses(classNames);
        classes.throwException();
        for (var claz : classes.get()) {
            var m = parseUserClass(claz, classes.get());
            result.putAll(m.get());
            errors.addAll(m.getErrors());
        }
        errors.throwException();
        return result;
    }

    /** Returns the loadable classes among a list of class names. */
    static private FallibleObject<? extends Set<Class<?>>> toClasses(List<QualName> classNames) {
        var result = new FallibleObject<>(new LinkedHashSet<Class<?>>());
        var loader = ClassLoader.getSystemClassLoader();
        for (var className : classNames) {
            try {
                result.get().add(loader.loadClass(className.toString()));
            } catch (ClassNotFoundException exc) {
                result.addError("Class '%s' cannot be loaded", className);
            }
        }
        return result;
    }

    /** Checks whether a given class has suitable user-defined operations,
     * and returns the methods on which those operations are based.
     */
    @SuppressWarnings("null")
    private static FallibleObject<? extends Map<Operator,Executable>> parseUserClass(Class<?> claz,
                                                                                     Set<Class<?>> others) {
        var result = new FallibleObject<>(new LinkedHashMap<Operator,Executable>());
        String className = claz.getCanonicalName();
        // check that the class is public
        if (!Modifier.isPublic(claz.getModifiers())) {
            result.addError("Class '%s' is not public", className);
        }
        // if the class is annotated as UserType, check that it is a record type with primitively sorted fields
        boolean isUserType = Sort.toSort(claz) == Sort.USER;
        if (isUserType) {
            if (!claz.isRecord()) {
                result.addError("User type '%s' is not a record type", className);
            } else {
                boolean clazOk = true;
                var rcs = claz.getRecordComponents();
                // collect the types of the record components
                var parTypes = new Class<?>[rcs.length];
                for (int i = 0; i < rcs.length; i++) {
                    var rc = rcs[i];
                    var name = rc.getName();
                    parTypes[i] = rc.getType();
                    var sort = Sort.toSort(rc.getType());
                    if (sort == null || !sort.isSystem()) {
                        result
                            .addError("Type of field '%s.%s' is not a system sort", className,
                                      name);
                        clazOk = false;
                    } else {
                        addExecutable(result, rc.getAccessor());
                    }
                }
                if (clazOk) {
                    try {
                        addExecutable(result, claz.getConstructor(parTypes));
                    } catch (NoSuchMethodException | SecurityException exc) {
                        throw Exceptions.unreachable();
                    }
                }
            }
        }
        // retrieve the @UserOperation-annotated methods
        for (var m : claz.getDeclaredMethods()) {
            if (m.getAnnotation(UserOperation.class) == null) {
                continue;
            }
            if (!isUserType && !Modifier.isStatic(m.getModifiers())) {
                result.addError("User operation '%s.%s' is not static", className, m.getName());
            } else if (!Modifier.isPublic(m.getModifiers())) {
                result.addError("User operation '%s.%s' is not public", className, m.getName());
            } else {
                try {
                    var newMethod = checkMethod(m, others);
                    addExecutable(result, newMethod);
                } catch (FormatException exc) {
                    result.addErrors(exc.getErrors());
                }
            }
        }
        return result;
    }

    static private void addExecutable(FallibleObject<? extends Map<Operator,Executable>> result,
                                      Executable exec) {
        var op = new Operator(exec);
        var oldExec = result.get().put(op, exec);
        if (oldExec != null) {
            result
                .addError("Duplicate user operation '%s' in '%s' and '%s'", op.getName(),
                          oldExec.getDeclaringClass().getCanonicalName(),
                          exec.getDeclaringClass().getCanonicalName());
        }
    }

    /** Checks a given method for suitability as the basis for a user-defined operation.
     * If no error is thrown, returns the method (for the purpose of call chaining).
     */
    static private Method checkMethod(Method m, Set<Class<?>> userClasses) throws FormatException {
        var errors = new FormatErrorSet();
        var className = m.getClass().getName();
        try {
            var op = new Operator(m);
            boolean isObjectMethod = !Modifier.isStatic(m.getModifiers());
            for (int i = 0; i < op.getArity(); i++) {
                var parType = isObjectMethod
                    ? (i == 0
                        ? m.getDeclaringClass()
                        : m.getParameterTypes()[i - i])
                    : m.getParameterTypes()[i];
                if (op.getParamSorts().get(i) == Sort.USER && !userClasses.contains(parType)) {
                    errors
                        .add("User-defined parameter type %s of user operation '%s.%s' not declared in systems properties",
                             parType.getName(), className, m.getName());
                }
            }
            var returnType = m.getReturnType();
            if (op.getResultSort() == Sort.USER && !userClasses.contains(returnType)) {
                errors
                    .add("User-defined return type %s of user operation '%s.%s' not declared in systems properties",
                         returnType.getName(), className, m.getName());
            }
        } catch (IllegalArgumentException exc) {
            errors
                .add("Erroneous user operation '%s.%s': %s", className, m.getName(),
                     exc.getMessage());
        }
        errors.throwException();
        return m;
    }

    /** Returns the set of operators defined in the user classes. */
    public static Map<Operator,Executable> getMethods() {
        return methods;
    }

    /** Lazily computed set of operators in the user class. */
    static private final Map<Operator,Executable> methods = new LinkedHashMap<>();

    /** Returns the set of operators defined in the user class. */
    public static Map<String,Operator> getOperators() {
        return operators.get();
    }

    /** Lazily computed set of operators in the user class. */
    static private final Factory<Map<String,Operator>> operators
        = Factory.lazy(UserSignature::computeOperators);

    static private Map<String,Operator> computeOperators() {
        var result = new LinkedHashMap<String,Operator>();
        for (var op : getMethods().keySet()) {
            result.put(op.getName(), op);
        }
        return result;
    }

    /** Adds a reset method of a user that needs to be invoked when a new user class is loaded. */
    static public void addUser(Callback user) {
        users.add(user);
    }

    /** List of dependants that need to be reset when a new user class is loaded. */
    static private Set<Callback> users = new LinkedHashSet<>();

    /** Calls the {@link Runnable#run()} method on all elements of {@link #users}. */
    static private void resetUsers() {
        users.forEach(Callback::call);
    }
}
