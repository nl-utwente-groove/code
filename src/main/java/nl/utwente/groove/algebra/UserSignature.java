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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarFile;

import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.Operator.SeqAccessor;
import nl.utwente.groove.algebra.syntax.CallExpr;
import nl.utwente.groove.algebra.syntax.Expression;
import nl.utwente.groove.annotation.Seq;
import nl.utwente.groove.annotation.UserDisplay;
import nl.utwente.groove.annotation.UserInterface;
import nl.utwente.groove.annotation.UserOperation;
import nl.utwente.groove.annotation.UserSeq;
import nl.utwente.groove.annotation.UserType;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.Callback;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.Factory;
import nl.utwente.groove.util.ModuleName;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.UserClassPath;
import nl.utwente.groove.util.io.FileType;
import nl.utwente.groove.util.parse.FallibleObject;
import nl.utwente.groove.util.parse.FormatError;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;
import nl.utwente.groove.util.parse.Severity;

/**
 * The signature for the user algebra.
 * @author Arend Rensink
 * @version $Revision: 6239 $
 */
public sealed abstract class UserSignature implements Signature permits UserAlgebra {
    /** Sets the user-defined classes containing the operator definitions,
     * loaded through the class path of a given grammar directory
     * on top of GROOVE's own class path (see {@link #newLoader(File, List)}).
     * All methods with an {@link UserOperation}-annotation are taken as operators.
     * Silently ignores any errors in the classes; to check for those,
     * call {@link #checkUserClass} afterwards.
     * @param classNames the names of the classes to load
     * @param grammarDir the grammar directory holding the grammar-local class path;
     * if {@code null}, the classes are loaded from the user class path and
     * GROOVE's own class path only
     * @param grammarEntries the entries of the grammar's class path property,
     * resolved against the grammar directory
     */
    @AIGenerated("Claude Fable 5.1, 2026-10")
    public static void setUserClass(List<QualName> classNames, @Nullable File grammarDir,
                                    List<String> grammarEntries) {
        setLoader(grammarDir, grammarEntries);
        setUserClass(classNames);
    }

    /** Convenience method for {@link #setUserClass(List, File, List)} without
     * grammar class path entries.
     */
    public static void setUserClass(List<QualName> classNames, @Nullable File grammarDir) {
        setUserClass(classNames, grammarDir, List.of());
    }

    /** Sets the user-defined classes containing the operator definitions,
     * loaded through the currently installed class loader
     * (see {@link #setUserClass(List, File, List)}).
     * All methods with an {@link UserOperation}-annotation are taken as operators.
     * Silently ignores any errors in the classes; to check for those,
     * call {@link #checkUserClass} instead.
     */
    public static void setUserClass(List<QualName> classNames) {
        implementations.clear();
        userTypes.clear();
        operators.reset();
        var classes = toClasses(classNames);
        var result = new FallibleObject<>(new LinkedHashMap<Operator,Impl>());
        parseUserClasses(result, classes.get());
        implementations.putAll(result.get());
        for (var claz : classes.get()) {
            if (claz.getAnnotation(UserType.class) != null && claz.isRecord()) {
                userTypes.add(claz);
            }
        }
        resetUsers();
    }

    /** Checks whether a given set of named classes can be loaded through the currently
     * installed class loader (see {@link #setUserClass(List, File, List)}) and have suitable
     * user-defined operations, and returns the operations with their implementations,
     * together with the errors found: a class that cannot be loaded or has annotation
     * errors is an error, as are two operations of the same name (see {@link #addOperator});
     * a class that exists in more than one place on the class path is a warning.
     */
    public static FallibleObject<Map<Operator,Impl>> checkUserClass(List<QualName> classNames) {
        var result = new FallibleObject<Map<Operator,Impl>>(new LinkedHashMap<>());
        var classes = toClasses(classNames);
        result.addErrors(classes.getErrors());
        if (!classes.getErrors().hasErrors()) {
            parseUserClasses(result, classes.get());
        }
        return result;
    }

    /** Adds the operations of a given set of user classes to a given result,
     * together with the errors found, in the individual classes
     * (see {@link #parseUserClass}) and between them: an operation may not have the name
     * of a sequence-valued field of a user type or user interface, in whatever class
     * it is declared. That name is reserved for an operator that yields the sequence
     * as a whole, which only exists once sequences are values (gh #942).
     * The user interfaces that the classes implement or extend are user classes
     * as well, whether or not they are among the given ones (see {@link #addUserInterfaces}).
     */
    @AIGenerated("Claude Fable 5.2, 2026-11")
    private static void parseUserClasses(FallibleObject<? extends Map<Operator,Impl>> result,
                                         Set<Class<?>> classes) {
        var all = new LinkedHashSet<Class<?>>();
        for (var claz : classes) {
            addUserInterfaces(all, claz);
            all.add(claz);
        }
        checkTypeNames(result, all);
        var seqFields = new LinkedHashMap<String,@Nullable Method>();
        for (var claz : all) {
            parseUserClass(result, claz, all, seqFields);
        }
        for (var e : result.get().entrySet()) {
            var name = e.getKey().getName();
            var seqField = seqFields.get(name);
            if (seqField != null) {
                result
                    .addError("User operation '%s' in '%s' has the name of sequence-valued field '%s.%s', which is reserved",
                              name,
                              e.getValue().executable().getDeclaringClass().getCanonicalName(),
                              seqField.getDeclaringClass().getCanonicalName(), name);
            }
        }
    }

    /** Tests that no user interface has the simple name of another user interface
     * or of a user type, among a given set of user classes.
     * User values are described by the simple names of their types, in messages
     * and wherever a type is to be named in a grammar (gh #944).
     * The simple names of user types are unique already, being those of their
     * constructor operations, so two user types are not reported here.
     */
    @AIGenerated("Claude Fable 5.2, 2026-11")
    private static void checkTypeNames(FallibleObject<?> result, Set<Class<?>> classes) {
        // only looked up, never iterated
        var named = new HashMap<String,Class<?>>();
        for (var claz : classes) {
            boolean isInterface = claz.getAnnotation(UserInterface.class) != null;
            if (!isInterface && claz.getAnnotation(UserType.class) == null) {
                continue;
            }
            var other = named.putIfAbsent(claz.getSimpleName(), claz);
            if (other == null) {
                continue;
            }
            boolean otherIsInterface = other.getAnnotation(UserInterface.class) != null;
            if (isInterface) {
                result
                    .addError("User interface '%s' has the same simple name as user %s '%s'",
                              claz.getCanonicalName(), otherIsInterface
                                  ? "interface"
                                  : "type",
                              other.getCanonicalName());
            } else if (otherIsInterface) {
                result
                    .addError("User interface '%s' has the same simple name as user type '%s'",
                              other.getCanonicalName(), claz.getCanonicalName());
            }
        }
    }

    /** Adds the user interfaces that a given class implements or extends to a set
     * of classes, directly or through other user interfaces, with every interface
     * after the ones it extends.
     */
    @AIGenerated("Claude Fable 5.2, 2026-11")
    private static void addUserInterfaces(Set<Class<?>> result, Class<?> claz) {
        for (var sup : claz.getInterfaces()) {
            if (!result.contains(sup)) {
                // an interface that is not a user interface may extend one
                addUserInterfaces(result, sup);
                if (sup.getAnnotation(UserInterface.class) != null) {
                    result.add(sup);
                }
            }
        }
    }

    /** Returns the operation of the user interfaces that a given class implements
     * or extends, directly or through other user interfaces, that is declared by a method
     * of a given name: an operation, or a sequence-valued field. If the method is declared
     * in an interface and again in one that it extends, the latter is returned,
     * as that is where the operation belongs.
     * @return the method that declares the operation, or {@code null} if there is none
     */
    @AIGenerated("Claude Fable 5.2, 2026-11")
    private static @Nullable Method findInterfaceOp(Class<?> claz, String name) {
        for (var sup : claz.getInterfaces()) {
            // an interface that is not a user interface may extend one
            var result = findInterfaceOp(sup, name);
            if (result == null && sup.getAnnotation(UserInterface.class) != null) {
                for (var m : sup.getDeclaredMethods()) {
                    if (m.getName().equals(name) && m.getAnnotation(UserOperation.class) != null
                        && !Modifier.isStatic(m.getModifiers()) && !m.isBridge()) {
                        result = m;
                        break;
                    }
                }
            }
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    /** Tests if a given field or method of a user type or user interface implements
     * an operation of a user interface that the type implements or extends
     * (see {@link #findInterfaceOp}). If so, it is no operation of its own:
     * the operation is applied by invoking the method of the interface.
     * It is an error if the method has the name of such an operation without
     * overriding the method that declares it, if the class has another field or method
     * that overrides it, or if the two do not agree on being indeterminate.
     * @param member the method, or the accessor method of the field
     * @param implemented the names of the fields and methods of the class that were
     * found to implement an operation so far; the name of the member is added to them
     * if it implements one
     * @return {@code true} if the member has the name of an operation of a user interface
     */
    @AIGenerated("Claude Fable 5.2, 2026-11")
    private static boolean implementsInterfaceOp(FallibleObject<?> result, Method member,
                                                 Set<String> implemented) {
        var name = member.getName();
        var op = findInterfaceOp(member.getDeclaringClass(), name);
        if (op == null) {
            return false;
        }
        var memberName = member.getDeclaringClass().getCanonicalName() + "." + name;
        var opName = op.getDeclaringClass().getCanonicalName() + "." + name;
        var annotation = member.getAnnotation(UserOperation.class);
        var opAnnotation = op.getAnnotation(UserOperation.class);
        assert opAnnotation != null; // otherwise it would not declare an operation
        if (!overrides(member, op)) {
            result
                .addError("User operation '%s' has the name of operation '%s' but does not implement it",
                          memberName, opName);
        } else if (!implemented.add(name)) {
            result
                .addError("Duplicate user operation '%s': operation '%s' is implemented more than once",
                          memberName, opName);
        } else if (annotation != null
            && annotation.indeterminate() != opAnnotation.indeterminate()) {
            result
                .addError("User operation '%s' differs from operation '%s', which it implements, in being indeterminate",
                          memberName, opName);
        }
        return true;
    }

    /** Tests if a method can be an override of another, going by their parameter
     * and return types: the same number of parameters, with types that are the same
     * or, for a method that overrides a generic one, subtypes; and likewise for the return type.
     * The names and declaring classes of the methods are not compared.
     */
    @AIGenerated("Claude Fable 5.2, 2026-11")
    private static boolean overrides(Method method, Method overridden) {
        var parTypes = method.getParameterTypes();
        var oldParTypes = overridden.getParameterTypes();
        if (parTypes.length != oldParTypes.length
            || !overridden.getReturnType().isAssignableFrom(method.getReturnType())) {
            return false;
        }
        for (int i = 0; i < parTypes.length; i++) {
            if (!oldParTypes[i].isAssignableFrom(parTypes[i])) {
                return false;
            }
        }
        return true;
    }

    /** Returns the loadable classes among a list of class names.
     * A class that cannot be loaded is an error; a class that exists in more than one
     * place is a warning naming all places, the one actually used first.
     * A name ending on a wildcard stands for the user classes of a package
     * (see {@link #addPackageClasses}).
     */
    static private FallibleObject<? extends Set<Class<?>>> toClasses(List<QualName> classNames) {
        var result = new FallibleObject<>(new LinkedHashSet<Class<?>>());
        for (var className : classNames) {
            if (className.hasWildCard()) {
                addPackageClasses(result, className);
            } else {
                addClass(result, className.toString());
            }
        }
        return result;
    }

    /** Adds the class of a given name to a result, with the errors
     * described under {@link #toClasses}.
     */
    static private void addClass(FallibleObject<? extends Set<Class<?>>> result, String className) {
        try {
            result.get().add(UserSignature.loader.loadClass(className));
            var copies = findCopies(className);
            if (copies.size() > 1) {
                result
                    .getErrors()
                    .add(new FormatError(Severity.WARNING,
                        "Class '%s' is found in several places: %s (used), %s", className,
                        copies.get(0), String.join(", ", copies.subList(1, copies.size()))));
            }
        } catch (ClassNotFoundException exc) {
            result.addError("Class '%s' cannot be loaded from %s", className, sourceDescription);
        }
    }

    /** Adds the user classes of a package to a result, for a name of the form
     * {@code package.*} (gh #939): the top-level classes of that package, not of its
     * subpackages, that are a {@link UserType} or declare a {@link UserOperation},
     * in the order of their names. The other classes of the package are left alone,
     * so that helper classes need not be public or otherwise fit to be a user class.
     * It is an error if the package has no user classes, or if the wildcard is not
     * the last part of the name; a class that cannot be loaded is skipped with a warning.
     */
    @AIGenerated("Claude Fable 5.2, 2026-11")
    static private void addPackageClasses(FallibleObject<? extends Set<Class<?>>> result,
                                          QualName pattern) {
        var pkg = pattern.parent();
        if (pkg.hasWildCard()) {
            result
                .addError("Wildcard in '%s' is only allowed as the last part of a class name",
                          pattern);
            return;
        }
        boolean found = false;
        for (var className : findClassNames(pkg)) {
            try {
                if (isUserClass(UserSignature.loader.loadClass(className))) {
                    addClass(result, className);
                    found = true;
                }
            } catch (ClassNotFoundException | LinkageError exc) {
                result
                    .getErrors()
                    .add(new FormatError(Severity.WARNING,
                        "Class '%s' matching '%s' is skipped because it cannot be loaded (%s)",
                        className, pattern, exc));
            }
        }
        if (!found) {
            result
                .addError("No class with user-defined types or operations matches '%s' in %s",
                          pattern, sourceDescription);
        }
    }

    /** Tests if a class is a user type or user interface, or declares a user operation. */
    @AIGenerated("Claude Fable 5.2, 2026-11")
    static private boolean isUserClass(Class<?> claz) {
        return claz.getAnnotation(UserType.class) != null
            || claz.getAnnotation(UserInterface.class) != null || Arrays
            .stream(claz.getDeclaredMethods())
            .anyMatch(m -> m.getAnnotation(UserOperation.class) != null);
    }

    /** Returns the names of the top-level classes of a given package, in the order
     * of the names, as found on GROOVE's own class path and in the entries of the
     * grammar loader. A jar on GROOVE's own class path is only searched if it has
     * a directory entry for the package, which the standard jar tool always writes.
     */
    @AIGenerated("Claude Fable 5.2, 2026-11")
    static private Set<String> findClassNames(ModuleName pkg) {
        var result = new TreeSet<String>();
        var prefix = pkg.isTop()
            ? ""
            : pkg + ".";
        var dirName = prefix.replace('.', '/');
        try {
            var urls = PARENT_LOADER.getResources(dirName);
            while (urls.hasMoreElements()) {
                var url = urls.nextElement();
                if (url.getProtocol().equals("file")) {
                    addDirClassNames(result, Path.of(url.toURI()), prefix);
                } else if (url.openConnection() instanceof JarURLConnection conn) {
                    // an uncached connection has a jar file of its own, which can be closed
                    conn.setUseCaches(false);
                    try (var jar = conn.getJarFile()) {
                        addJarClassNames(result, jar, prefix);
                    }
                }
            }
        } catch (IOException | URISyntaxException exc) {
            // the enumeration is incomplete; report what was found
        }
        if (loader instanceof GrammarClassLoader grammarLoader) {
            grammarLoader.addClassNames(result, prefix);
        }
        return result;
    }

    /** Adds the names of the top-level classes in a given package directory to a result.
     * @param prefix the package name followed by a dot, or the empty string
     * for the default package
     */
    @AIGenerated("Claude Fable 5.2, 2026-11")
    static private void addDirClassNames(Set<String> result, Path dir, String prefix) {
        var fileNames = dir.toFile().list();
        if (fileNames != null) {
            for (var fileName : fileNames) {
                addClassName(result, prefix, fileName);
            }
        }
    }

    /** Adds the names of the top-level classes of a given package in a jar to a result.
     * @param prefix the package name followed by a dot, or the empty string
     * for the default package
     */
    @AIGenerated("Claude Fable 5.2, 2026-11")
    static private void addJarClassNames(Set<String> result, JarFile jar, String prefix) {
        var dirName = prefix.replace('.', '/');
        for (var entries = jar.entries(); entries.hasMoreElements();) {
            var entryName = entries.nextElement().getName();
            if (entryName.startsWith(dirName) && entryName.indexOf('/', dirName.length()) < 0) {
                addClassName(result, prefix, entryName.substring(dirName.length()));
            }
        }
    }

    /** Adds the name of the class in a file of a given name to a result, if this is
     * the class file of a top-level class: not of a nested, local or anonymous class,
     * and not a package or module descriptor.
     */
    @AIGenerated("Claude Fable 5.2, 2026-11")
    static private void addClassName(Set<String> result, String prefix, String fileName) {
        if (fileName.endsWith(CLASS_SUFFIX) && fileName.indexOf('$') < 0
            && fileName.indexOf('-') < 0) {
            result
                .add(prefix + fileName.substring(0, fileName.length() - CLASS_SUFFIX.length()));
        }
    }

    /** Suffix of the name of a class file. */
    static private final String CLASS_SUFFIX = ".class";

    /** Returns the places where a given class is found by the current loader, as
     * descriptions, in search order: GROOVE's own class path (the parent, consulted
     * first) if it has the class, followed by the entries of the grammar loader.
     */
    @AIGenerated("Claude Fable 5.1, 2026-10")
    static private List<String> findCopies(String className) {
        var result = new ArrayList<String>();
        var resource = className.replace('.', '/') + ".class";
        if (PARENT_LOADER.getResource(resource) != null) {
            result.add(GROOVE_CLASS_PATH);
        }
        if (loader instanceof GrammarClassLoader grammarLoader) {
            try {
                var urls = grammarLoader.findResources(resource);
                while (urls.hasMoreElements()) {
                    result.add(grammarLoader.getLabel(urls.nextElement()));
                }
            } catch (IOException exc) {
                // the enumeration is incomplete; report what was found
            }
        }
        return result;
    }

    /** Checks whether a given class has suitable user-defined operations,
     * and adds the methods on which those operations are based to a given result,
     * together with the errors found.
     * The result is shared among the user classes, since the operations of all of them
     * live in the single sort {@link Sort#USER}: two operations of the same name
     * are an error (see {@link #addOperator}).
     * A field or method of a user type that implements an operation of a user interface
     * is not an operation of its own, and is not added (see {@link #implementsInterfaceOp}):
     * that is how user types share a name (gh #933).
     * A sequence-valued field has no operation of its own name, but two generated ones
     * (see {@link SeqAccessor}), both based on the accessor method of the field;
     * that method is added to a given map, from the name of the field. A user interface
     * declares such a field by a method without parameters that returns the sequence.
     */
    private static void parseUserClass(FallibleObject<? extends Map<Operator,Impl>> result,
                                       Class<?> claz, Set<Class<?>> others,
                                       Map<String,@Nullable Method> seqFields) {
        String className = claz.getCanonicalName();
        // check that the class is public
        if (!Modifier.isPublic(claz.getModifiers())) {
            result.addError("Class '%s' is not public", className);
        }
        boolean isUserInterface = claz.getAnnotation(UserInterface.class) != null;
        if (isUserInterface && !claz.isInterface()) {
            result.addError("User interface '%s' is not an interface", className);
        }
        // the names of the fields and methods that implement an operation of a user interface
        var implemented = new LinkedHashSet<String>();
        // the accessor methods of the fields, which may be declared and annotated explicitly
        var accessors = new LinkedHashSet<Method>();
        // if the class is annotated as UserType, check that it is a record type with primitively sorted fields
        boolean isUserType = claz.getAnnotation(UserType.class) != null;
        if (isUserType) {
            if (!claz.isRecord()) {
                result.addError("User type '%s' is not a record type", className);
            } else {
                boolean clazOk = true;
                var rcs = claz.getRecordComponents();
                assert rcs != null : "Record type without record components";
                // collect the types of the record components
                var parTypes = new Class<?>[rcs.length];
                for (int i = 0; i < rcs.length; i++) {
                    var rc = rcs[i];
                    var name = rc.getName();
                    accessors.add(rc.getAccessor());
                    parTypes[i] = rc.getType();
                    var sort = Sort.toSort(rc.getType());
                    if (Seq.class.isAssignableFrom(rc.getType())) {
                        // a sequence-valued field: its elements are the tail of the
                        // constructor arguments, so it has to be the last field
                        if (i < rcs.length - 1) {
                            result
                                .addError("Sequence-valued field '%s.%s' is not the last field",
                                          className, name);
                            clazOk = false;
                        } else if (checkSeqField(result, rc.getAccessor())) {
                            seqFields.putIfAbsent(name, rc.getAccessor());
                            if (!implementsInterfaceOp(result, rc.getAccessor(), implemented)) {
                                addSeqAccessors(result, rc.getAccessor());
                            }
                        } else {
                            clazOk = false;
                        }
                    } else if (sort == null || !sort.isSystem()) {
                        result
                            .addError("Type of field '%s.%s' is not a system sort", className,
                                      name);
                        clazOk = false;
                    } else if (!implementsInterfaceOp(result, rc.getAccessor(), implemented)) {
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
        // retrieve the @UserOperation-annotated methods; a bridge method, which the
        // compiler generates for an override with other parameter or return types
        // and which has the annotations of that override, is not one of them,
        // and neither is the accessor method of a field, which was dealt with above
        for (var m : claz.getDeclaredMethods()) {
            if (m.getAnnotation(UserOperation.class) == null || m.isBridge()
                || accessors.contains(m)) {
                continue;
            }
            boolean isStatic = Modifier.isStatic(m.getModifiers());
            if (!isUserType && !isUserInterface && !isStatic) {
                result.addError("User operation '%s.%s' is not static", className, m.getName());
            } else if (!Modifier.isPublic(m.getModifiers())) {
                result.addError("User operation '%s.%s' is not public", className, m.getName());
            } else if (!isStatic && implementsInterfaceOp(result, m, implemented)) {
                // the operation is that of the user interface
            } else if (isUserInterface && !isStatic && m.getParameterCount() == 0
                && Seq.class.isAssignableFrom(m.getReturnType())) {
                // the declaration of a sequence-valued field
                if (checkSeqField(result, m)) {
                    seqFields.putIfAbsent(m.getName(), m);
                    addSeqAccessors(result, m);
                }
            } else {
                try {
                    var newMethod = checkMethod(m, others);
                    addExecutable(result, newMethod);
                } catch (FormatException exc) {
                    result.addErrors(exc.getErrors());
                }
            }
        }
        checkDisplayMethods(result, claz, isUserType);
    }

    /** Checks the type of a sequence-valued field of a user type or user interface,
     * which has to be a sequence of values of a system sort, and adds the error
     * to a given result if it is not.
     * @param field the accessor method of the field, or the method that declares it
     * in a user interface
     * @return {@code true} if the type is correct
     */
    @AIGenerated("Claude Fable 5.2, 2026-11")
    private static boolean checkSeqField(FallibleObject<?> result, Method field) {
        var className = field.getDeclaringClass().getCanonicalName();
        if (field.getReturnType() == UserSeq.class) {
            result
                .addError("Field '%s.%s' is a sequence of user values, which is only allowed as an operation parameter",
                          className, field.getName());
            return false;
        } else if (Operator.getSeqElementType(field.getReturnType()) == null) {
            result
                .addError("Type of field '%s.%s' is not a sequence of a system sort", className,
                          field.getName());
            return false;
        } else {
            return true;
        }
    }

    /** Adds the generated accessors of a sequence-valued field of a user type
     * or user interface to a given result.
     * @param field the accessor method of the field, or the method that declares it
     * in a user interface
     * @see SeqAccessor
     */
    @AIGenerated("Claude Fable 5.2, 2026-11")
    private static void addSeqAccessors(FallibleObject<? extends Map<Operator,Impl>> result,
                                        Method field) {
        for (var accessor : SeqAccessor.values()) {
            addOperator(result, new Operator(field, accessor),
                        new Impl(field, field.getDeclaringClass(), accessor));
        }
    }

    /** Checks the {@link UserDisplay}-annotated methods of a given class, and adds
     * the errors found to a given result: such a method is only allowed in a user type,
     * which can have at most one of them, and should be public and non-static, without
     * parameters and with return type {@code String}.
     */
    @AIGenerated("Claude Fable 5.2, 2026-11")
    private static void checkDisplayMethods(FallibleObject<?> result, Class<?> claz,
                                            boolean isUserType) {
        String className = claz.getCanonicalName();
        int count = 0;
        // the declared methods come in no particular order: sort them, for stable messages
        var methods = Arrays
            .stream(claz.getDeclaredMethods())
            .filter(m -> m.getAnnotation(UserDisplay.class) != null)
            .sorted(Comparator.comparing(Method::getName))
            .toList();
        for (var m : methods) {
            count++;
            if (!isUserType) {
                result
                    .addError("Display method '%s.%s' is not declared in a user type", className,
                              m.getName());
            } else if (!Modifier.isPublic(m.getModifiers())) {
                result.addError("Display method '%s.%s' is not public", className, m.getName());
            } else if (!isDisplayMethod(m)) {
                result
                    .addError("Display method '%s.%s' should be non-static, without parameters and with return type String",
                              className, m.getName());
            }
        }
        if (isUserType && count > 1) {
            result.addError("User type '%s' has more than one display method", className);
        }
    }

    /** Tests if a given method is fit to be the display method of its declaring class,
     * apart from that class being a user type:
     * the method is annotated with {@link UserDisplay}, public and non-static,
     * and has no parameters and return type {@code String}.
     */
    @AIGenerated("Claude Fable 5.2, 2026-11")
    private static boolean isDisplayMethod(Method m) {
        int modifiers = m.getModifiers();
        return m.getAnnotation(UserDisplay.class) != null && Modifier.isPublic(modifiers)
            && !Modifier.isStatic(modifiers) && m.getParameterCount() == 0
            && m.getReturnType() == String.class;
    }

    /** Returns the display method of a given class: the {@link UserDisplay}-annotated
     * method that yields the string by which the state display shows a value of the class.
     * @return the display method; {@code null} if the class is not a user type or does not
     * have precisely one suitable display method (see {@link #checkDisplayMethods})
     */
    @AIGenerated("Claude Fable 5.2, 2026-11")
    static @Nullable Method getDisplayMethod(Class<?> claz) {
        var result = DISPLAY_METHODS.get(claz);
        return result == null
            ? null
            : result.orElse(null);
    }

    /** The display methods of the classes for which {@link #getDisplayMethod} has been called.
     * A class value does not keep the classes of a discarded grammar class loader alive.
     */
    static private final ClassValue<Optional<Method>> DISPLAY_METHODS = new ClassValue<>() {
        @Override
        protected Optional<Method> computeValue(Class<?> claz) {
            Optional<Method> result = Optional.empty();
            if (Sort.toSort(claz) == Sort.USER && claz.isRecord()) {
                var methods = Arrays
                    .stream(claz.getDeclaredMethods())
                    .filter(m -> m.getAnnotation(UserDisplay.class) != null)
                    .toList();
                if (methods.size() == 1 && isDisplayMethod(methods.get(0))) {
                    result = Optional.of(methods.get(0));
                }
            }
            return result;
        }
    };

    /** Adds the operation based on a given executable to a result, or reports an error
     * if the result already has an operation of that name.
     * @see #addOperator
     */
    static private void addExecutable(FallibleObject<? extends Map<Operator,Impl>> result,
                                      Executable exec) {
        addOperator(result, new Operator(exec), Impl.of(exec));
    }

    /** Adds a given operation, with its implementation, to a result, or reports
     * an error if the result already has an operation of that name. In that case the existing
     * operation is kept, so that an operator is never paired with the implementation of another.
     * This also finds the clashes of the generated accessors of a sequence-valued field
     * with other operations, in both orders.
     * User types can only have a field or operation of the same name by implementing
     * an operation of a user interface, which is then the one operation of that name
     * (gh #933; see {@link #implementsInterfaceOp}).
     */
    @AIGenerated("Claude Fable 5.2, 2026-11")
    static private void addOperator(FallibleObject<? extends Map<Operator,Impl>> result,
                                    Operator op, Impl impl) {
        var oldImpl = result.get().putIfAbsent(op, impl);
        if (oldImpl != null) {
            var oldReceiver = oldImpl.receiver();
            var receiver = impl.receiver();
            // the hint is for a namesake in a user type, not for one in two user interfaces
            var hint = oldReceiver != null && receiver != null && oldReceiver != receiver
                && !(oldReceiver.isInterface() && receiver.isInterface())
                    ? "; user types can only share the name of an operation of a user interface that they implement"
                    : "";
            result
                .addError("Duplicate user operation '%s' in %s and %s%s", op.getName(),
                          describeSource(oldImpl), describeSource(impl), hint);
        }
    }

    /** Returns a description of where a given implementation stems from, for error messages:
     * the class of the executable on which it is based, and for a generated accessor
     * the sequence-valued field as well.
     */
    @AIGenerated("Claude Fable 5.2, 2026-11")
    static private String describeSource(Impl impl) {
        var exec = impl.executable();
        var result = "'" + exec.getDeclaringClass().getCanonicalName() + "'";
        if (impl.accessor() != null) {
            result += " (generated for sequence-valued field '" + exec.getName() + "')";
        }
        return result;
    }

    /**
     * The implementation of a user operator: the constructor or method that computes
     * its outcome, together with what determines how that is invoked.
     * @param executable the constructor or method on which the implementation is based;
     * for a generated accessor of a sequence-valued field, this is the accessor method
     * of the field, or the method that declares the field in a user interface
     * @param receiver the user type or user interface whose values this implementation
     * applies to as first argument, if the executable is an instance method of that type;
     * {@code null} for a constructor or a static method. For a user interface,
     * the invocation of the method ends up in the user type of the value, which is
     * how user types share an operation (gh #933).
     * @param accessor the kind of accessor that this implementation is for
     * a sequence-valued field of the receiver, or {@code null} if it is not such an accessor.
     * An accessor is generated, rather than declared by a user operation;
     * it is evaluated by applying the accessor to the value of the field.
     */
    @AIGenerated("Claude Fable 5.2, 2026-11")
    public record Impl(Executable executable, @Nullable Class<?> receiver,
        @Nullable SeqAccessor accessor) {
        /** Returns the implementation consisting of the invocation of a given
         * constructor or method.
         */
        static Impl of(Executable exec) {
            var receiver = exec instanceof Method m && !Modifier.isStatic(m.getModifiers())
                ? m.getDeclaringClass()
                : null;
            return new Impl(exec, receiver, null);
        }

        /** Returns the Java class of the outcome of this implementation:
         * the return type of a method, and the constructed type for a constructor.
         * This is only meaningful for an operator with result sort {@link Sort#USER}:
         * for a generated accessor, it is the type of the field rather than of the outcome.
         */
        Class<?> returnClass() {
            return switch (executable()) {
            case Method m -> m.getReturnType();
            case Constructor<?> c -> c.getDeclaringClass();
            };
        }

        /** Returns the Java class that this implementation requires of the argument
         * at a given position of a call of its operator: the receiver at position 0,
         * if there is one, and otherwise the type of the corresponding parameter
         * of the executable, where every position in a variable argument tail
         * has the element type of the sequence (see {@link Operator#getTailType}).
         * @param index the position of the argument, counting from 0
         * @return the required class, or {@code null} if this implementation does
         * not pass the argument on to its executable, as for the position
         * of a generated accessor
         */
        @Nullable Class<?> parClass(int index) {
            var receiver = receiver();
            if (receiver != null) {
                if (index == 0) {
                    return receiver;
                }
                index--;
            }
            var parTypes = executable().getParameterTypes();
            int last = parTypes.length - 1;
            if (index > last) {
                // beyond the parameters: a later position in the tail, if there is one
                index = last;
            }
            if (index < 0) {
                return null;
            }
            if (index == last) {
                var tailType = Operator.getTailType(executable());
                if (tailType != null) {
                    return tailType;
                }
            }
            return parTypes[index];
        }

        /** Returns this implementation as it is for first arguments of a given class,
         * which is a subtype of the receiver: the method of that class that overrides
         * the one of this implementation may have a more specific return type,
         * and more specific parameter types if the overridden method is generic.
         * This serves to check a call; it is not the implementation to apply.
         * @param receiverClass the class of the first argument, as far as that is known
         * @return the implementation based on the overriding method, or this one if
         * it has no receiver, if the given class is not a proper subtype of it,
         * or if it is not certain which method of that class is the overriding one
         */
        Impl refine(Class<?> receiverClass) {
            var receiver = receiver();
            if (receiver == null || receiver == receiverClass || accessor() != null
                || !receiver.isAssignableFrom(receiverClass)
                || !(executable() instanceof Method method)) {
                return this;
            }
            // the method with the same parameter types, if there is one that is not
            // generated by the compiler; failing that, the only method that fits otherwise
            Method exact = null;
            Method other = null;
            int otherCount = 0;
            for (var m : receiverClass.getMethods()) {
                if (m.isBridge() || Modifier.isStatic(m.getModifiers())
                    || !m.getName().equals(method.getName()) || !overrides(m, method)) {
                    continue;
                }
                if (!Arrays.equals(m.getParameterTypes(), method.getParameterTypes())) {
                    other = m;
                    otherCount++;
                } else if (exact == null
                    || exact.getReturnType().isAssignableFrom(m.getReturnType())) {
                    exact = m;
                }
            }
            var result = exact != null
                ? exact
                : otherCount == 1
                    ? other
                    : null;
            return result == null
                ? this
                : new Impl(result, receiverClass, null);
        }
    }

    /** Tests if a value of one class can be a value of another: one of them is a subtype
     * of the other, or they are user interfaces that some currently loaded user type
     * implements both.
     * @param required the class that is required of a value: a user type or user interface
     * @param actual the class that the value is known to have; the class of the value
     * itself may be a subtype of this
     */
    @AIGenerated("Claude Fable 5.2, 2026-11")
    private static boolean isCompatible(Class<?> required, Class<?> actual) {
        return required.isAssignableFrom(actual) || userTypes
            .stream()
            .anyMatch(t -> required.isAssignableFrom(t) && actual.isAssignableFrom(t));
    }

    /**
     * Returns the Java class of the values of a given expression, if the expression
     * is of sort {@link Sort#USER} and the class is statically known (gh #933).
     * That is the case for a user type constant and a constructor call, which have
     * the constructed type, and for a call of another user operator, which has
     * the return type of the method that implements the operator.
     * If the operator is one of a user interface and the class of the first argument
     * is known, that is the method of that class (see {@link Impl#refine}).
     * The class returned may be a user interface, in which case the values
     * are of user types that implement it.
     * The class of a variable or field is never known.
     * <p>
     * The class is derived from the currently loaded user classes whenever it is asked for,
     * rather than stored in the expression: it is only needed while a call is being built,
     * and the classes are replaced whenever a grammar is (re)loaded,
     * whereas expressions are created in many places and compared by content.
     * @return the class of the values of {@code expr}, or {@code null} if it is
     * not statically known
     */
    @AIGenerated("Claude Fable 5.2, 2026-11")
    public static @Nullable Class<?> getUserClass(Expression expr) {
        if (expr.getSort() != Sort.USER) {
            return null;
        }
        CallExpr call = null;
        if (expr instanceof CallExpr c) {
            call = c;
        } else if (expr instanceof Constant c) {
            // an error value has no constructor call
            call = c.getUserRepr();
        }
        if (call == null) {
            return null;
        }
        var found = getImplementations().get(call.getOperator());
        if (found == null) {
            return null;
        }
        Impl impl = found;
        var args = call.getArgs();
        if (impl.receiver() != null && !args.isEmpty()) {
            var receiverClass = getUserClass(args.get(0));
            if (receiverClass != null) {
                impl = impl.refine(receiverClass);
            }
        }
        return impl.returnClass();
    }

    /**
     * Checks a call of a user operator against the Java classes of its implementation,
     * as far as the classes of the arguments are statically known
     * (see {@link #getUserClass}); arguments of unknown class are never an error (gh #933).
     * The arguments are assumed to have the sorts that the operator requires.
     * The call is rejected at the first argument that cannot have a value of the class
     * that the implementation requires in that position, as its evaluation would
     * then yield an error value (see {@link #isCompatible}). For an operator of
     * a user interface, the class of the first argument determines what is required of
     * the others, if it is known (see {@link Impl#refine}).
     * This does nothing for an operator that is not defined in the currently loaded
     * user classes.
     * @throws FormatException if the operator cannot be applied to the arguments of the call
     */
    @AIGenerated("Claude Fable 5.2, 2026-11")
    public static void checkCall(CallExpr call) throws FormatException {
        var op = call.getOperator();
        var found = getImplementations().get(op);
        if (found == null) {
            return;
        }
        Impl impl = found;
        var receiver = impl.receiver();
        var args = call.getArgs();
        for (int i = 0; i < args.size(); i++) {
            var argClass = getUserClass(args.get(i));
            if (argClass == null) {
                continue;
            }
            var parClass = impl.parClass(i);
            if (parClass == null) {
                continue;
            }
            boolean isReceiver = i == 0 && receiver != null;
            if (isCompatible(parClass, argClass)) {
                if (isReceiver) {
                    impl = impl.refine(argClass);
                }
            } else if (isReceiver) {
                throw new FormatException(
                    "User operation '%s' is not implemented for a value of type %s, only for %s",
                    op.getName(), argClass.getSimpleName(), describeType(parClass));
            } else {
                var message = String
                    .format("Parameter %s of user operation '%s' should have type %s rather than %s",
                            i, op.getName(), describeType(parClass), argClass.getSimpleName());
                var refined = impl.receiver();
                if (refined != null && refined != receiver) {
                    message += ", as implemented for " + refined.getSimpleName();
                }
                throw new FormatException(message);
            }
        }
    }

    /** Checks a given method for suitability as the basis for a user-defined operation.
     * If no error is thrown, returns the method (for the purpose of call chaining).
     */
    static private Method checkMethod(Method m, Set<Class<?>> userClasses) throws FormatException {
        var errors = new FormatErrorSet();
        var className = m.getDeclaringClass().getName();
        try {
            var op = new Operator(m);
            boolean isObjectMethod = !Modifier.isStatic(m.getModifiers());
            var tailType = Operator.getTailType(m);
            for (int i = 0; i < op.getArity(); i++) {
                var parType = isObjectMethod
                    ? (i == 0
                        ? m.getDeclaringClass()
                        : m.getParameterTypes()[i - 1])
                    : m.getParameterTypes()[i];
                if (tailType != null && i == op.getArity() - 1) {
                    // the arguments are the elements of the sequence
                    parType = tailType;
                }
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

    /** Name of the grammar subdirectory that serves as a class directory for user-defined classes. */
    public static final String CLASSES_DIR = "classes";
    /** Name of the grammar subdirectory holding jars with user-defined classes. */
    public static final String LIB_DIR = "lib";

    /** Replaces the class loader for the user-defined classes by one for a given
     * grammar directory and grammar class path, closing the previous one.
     * @see #newLoader(File, List)
     */
    @AIGenerated("Claude Fable 5.1, 2026-10")
    static private void setLoader(@Nullable File grammarDir, List<String> grammarEntries) {
        if (loader instanceof GrammarClassLoader old) {
            try {
                old.close();
            } catch (IOException exc) {
                // the loader is discarded in any case
            }
        }
        loader = newLoader(grammarDir, grammarEntries);
    }

    /**
     * Creates a class loader for the user-defined classes of a grammar. GROOVE's own
     * class loader is the parent and, by the standard delegation, is consulted first;
     * the loader's own entries are then searched in this order: the grammar-local class
     * path, consisting of the {@link #CLASSES_DIR} subdirectory of the grammar directory
     * (if it exists) followed by the jars in its {@link #LIB_DIR} subdirectory (if any)
     * sorted by file name, case-insensitively and so identically on all platforms; the
     * entries of the grammar class path, resolved against the grammar directory; and the
     * installation-level {@link UserClassPath}.
     * Entries that do not exist are skipped. If no entry remains, the
     * parent loader itself is returned.
     * Jars are loaded from temporary copies, so that the originals stay writable
     * (on Windows, a jar that is open in a class loader cannot be overwritten);
     * closing the loader deletes the copies.
     * As a side effect, records a description of the sources for the error message
     * on classes that cannot be found.
     * @param grammarDir the grammar directory; if {@code null}, the grammar-local
     * class path and the grammar class path are skipped
     * @param grammarEntries the entries of the grammar class path
     */
    @AIGenerated("Claude Fable 5.1, 2026-10")
    static ClassLoader newLoader(@Nullable File grammarDir, List<String> grammarEntries) {
        var entries = new LinkedHashMap<URL,String>();
        var copies = new ArrayList<Path>();
        var sources = new ArrayList<String>();
        if (grammarDir != null) {
            var local = new ArrayList<String>();
            var classesDir = new File(grammarDir, CLASSES_DIR);
            if (classesDir.isDirectory()) {
                var label = CLASSES_DIR + File.separator;
                entries.put(toURL(classesDir.toPath()), label + GRAMMAR_DIR_SUFFIX);
                local.add(label);
            }
            var jars = new File(grammarDir, LIB_DIR)
                .listFiles(f -> f.isFile() && FileType.JAR.hasExtension(f));
            if (jars != null) {
                Arrays.sort(jars, JAR_ORDER);
                for (var jar : jars) {
                    var label = LIB_DIR + File.separator + jar.getName();
                    addEntry(jar.toPath(), label + GRAMMAR_DIR_SUFFIX, entries, copies);
                    local.add(label);
                }
            }
            if (!local.isEmpty()) {
                sources.add("the grammar directory (" + String.join(", ", local) + ")");
            }
            var base = grammarDir.toPath();
            var found = new ArrayList<String>();
            for (var entry : grammarEntries) {
                if (addEntry(base.resolve(entry).normalize(), entry + " (grammar class path)",
                             entries, copies)) {
                    found.add(entry);
                }
            }
            if (!found.isEmpty()) {
                sources.add("the grammar class path (" + String.join(", ", found) + ")");
            }
        }
        var found = new ArrayList<String>();
        for (var entry : UserClassPath.get()) {
            if (addEntry(entry, entry + " (user class path)", entries, copies)) {
                found.add(entry.toString());
            }
        }
        if (!found.isEmpty()) {
            sources.add("the user class path (" + String.join(", ", found) + ")");
        }
        sources.add(0, GROOVE_CLASS_PATH);
        sourceDescription = sources.size() == 1
            ? sources.get(0)
            : String.join(", ", sources.subList(0, sources.size() - 1)) + " or "
                + sources.get(sources.size() - 1);
        return entries.isEmpty()
            ? PARENT_LOADER
            : new GrammarClassLoader(entries, copies, PARENT_LOADER);
    }

    /** Adds a class path entry, with a label for messages, to a map of entries:
     * a directory as it is, a jar as a temporary copy (registered in the list of
     * copies), falling back to the jar itself if the copy fails.
     * @return {@code true} if the entry exists and was added
     */
    @AIGenerated("Claude Fable 5.1, 2026-10")
    static private boolean addEntry(Path entry, String label, Map<URL,String> entries,
                                    List<Path> copies) {
        if (Files.isDirectory(entry)) {
            entries.put(toURL(entry), label);
            return true;
        } else if (Files.isRegularFile(entry)) {
            try {
                var copy = Files.createTempFile("groove-user-", FileType.JAR.getExtension());
                Files.copy(entry, copy, StandardCopyOption.REPLACE_EXISTING);
                copy.toFile().deleteOnExit();
                copies.add(copy);
                entries.put(toURL(copy), label);
            } catch (IOException exc) {
                entries.put(toURL(entry), label);
            }
            return true;
        } else {
            return false;
        }
    }

    /** Order of the jars in the {@link #LIB_DIR} subdirectory: by file name,
     * case-insensitively, with the exact name as tie-break; the same on all platforms,
     * unlike the natural order of {@link File}, which ignores case only on Windows.
     */
    static private final Comparator<File> JAR_ORDER = Comparator
        .comparing(File::getName, String.CASE_INSENSITIVE_ORDER)
        .thenComparing(File::getName);

    /** Description of GROOVE's own class path, as a source of user classes. */
    static private final String GROOVE_CLASS_PATH = "GROOVE's own class path";
    /** Suffix of the labels of grammar-local entries. */
    static private final String GRAMMAR_DIR_SUFFIX = " (grammar directory)";

    /** Description of the sources searched by the current loader, for error messages. */
    static private String sourceDescription = GROOVE_CLASS_PATH;

    static private URL toURL(Path path) {
        try {
            return path.toUri().toURL();
        } catch (MalformedURLException exc) {
            throw Exceptions.unreachable();
        }
    }

    /** GROOVE's own class loader, the parent of every grammar-local class loader. */
    static private final ClassLoader PARENT_LOADER;
    static {
        var parent = UserSignature.class.getClassLoader();
        assert parent != null : "GROOVE is not loaded by the bootstrap loader";
        PARENT_LOADER = parent;
    }

    /** Class loader through which the user classes are loaded. */
    static private ClassLoader loader = PARENT_LOADER;

    /** Class loader for the user classes of a grammar, over labelled entries;
     * closing it deletes the temporary jar copies it was created from.
     */
    @AIGenerated("Claude Fable 5.1, 2026-10")
    static private final class GrammarClassLoader extends URLClassLoader {
        GrammarClassLoader(Map<URL,String> entries, List<Path> copies, ClassLoader parent) {
            super("groove-grammar", entries.keySet().toArray(new URL[0]), parent);
            this.entries = entries;
            this.copies = copies;
        }

        /** Returns the label of the entry from which a given resource URL stems,
         * or the URL itself if none matches.
         */
        String getLabel(URL resource) {
            var text = resource.toString();
            for (var entry : this.entries.entrySet()) {
                var base = entry.getKey().toString();
                if (text.startsWith(base) || text.startsWith("jar:" + base + "!/")) {
                    return entry.getValue();
                }
            }
            return text;
        }

        /** Adds the names of the top-level classes of a given package, as found in
         * the entries of this loader, to a result. An entry that cannot be read is skipped.
         * @param prefix the package name followed by a dot, or the empty string
         * for the default package
         */
        @AIGenerated("Claude Fable 5.2, 2026-11")
        void addClassNames(Set<String> result, String prefix) {
            for (var entry : this.entries.keySet()) {
                try {
                    var path = Path.of(entry.toURI());
                    if (Files.isDirectory(path)) {
                        addDirClassNames(result, path.resolve(prefix.replace('.', '/')), prefix);
                    } else {
                        try (var jar = new JarFile(path.toFile())) {
                            addJarClassNames(result, jar, prefix);
                        }
                    }
                } catch (IOException | URISyntaxException exc) {
                    // the enumeration is incomplete; report what was found
                }
            }
        }

        private final Map<URL,String> entries;

        @Override
        public void close() throws IOException {
            super.close();
            for (var copy : this.copies) {
                Files.deleteIfExists(copy);
            }
        }

        private final List<Path> copies;
    }

    /** Returns the operators defined in the user classes, each with its implementation. */
    public static Map<Operator,Impl> getImplementations() {
        return implementations;
    }

    /** The operators defined in the user classes, with their implementations. */
    static private final Map<Operator,Impl> implementations = new LinkedHashMap<>();

    /** Returns the currently loaded user types that are a given class or implement it:
     * the classes that a value of the given class can have.
     * @param type a user type or user interface
     */
    @AIGenerated("Claude Fable 5.2, 2026-11")
    public static List<Class<?>> getUserTypes(Class<?> type) {
        return userTypes.stream().filter(type::isAssignableFrom).toList();
    }

    /** Returns a description of a user type or user interface for messages:
     * its name, followed for a user interface by the names of the user types
     * that implement it (see {@link #getUserTypes}).
     */
    @AIGenerated("Claude Fable 5.2, 2026-11")
    static String describeType(Class<?> type) {
        var result = type.getSimpleName();
        if (type.isInterface()) {
            var names = getUserTypes(type).stream().map(Class::getSimpleName).toList();
            result += names.isEmpty()
                ? " (which no user type implements)"
                : " (implemented by " + String.join(", ", names) + ")";
        }
        return result;
    }

    /** The currently loaded user types. */
    static private final List<Class<?>> userTypes = new ArrayList<>();

    /** Returns the set of operators defined in the user class. */
    public static Map<String,Operator> getOperators() {
        return operators.get();
    }

    /** Lazily computed set of operators in the user class. */
    static private final Factory<Map<String,Operator>> operators
        = Factory.lazy(UserSignature::computeOperators);

    static private Map<String,Operator> computeOperators() {
        var result = new LinkedHashMap<String,Operator>();
        for (var op : getImplementations().keySet()) {
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
