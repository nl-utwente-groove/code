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
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.annotation.UserOperation;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.Callback;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.Factory;
import nl.utwente.groove.util.io.FileType;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.UserClassPath;
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
     * Silently ignores any annotation errors in the classes; to check for those,
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
     * Silently ignores any annotation errors in the classes; to check for those,
     * call {@link #checkUserClass} instead.
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

    /** Checks whether a given set of named classes can be loaded through the currently
     * installed class loader (see {@link #setUserClass(List, File, List)}) and have suitable
     * user-defined operations, and returns the operations as executables, together with
     * the errors found: a class that cannot be loaded or has annotation errors is an
     * error, a class that exists in more than one place on the class path is a warning.
     */
    public static FallibleObject<Map<Operator,Executable>> checkUserClass(List<QualName> classNames) {
        var result = new FallibleObject<Map<Operator,Executable>>(new LinkedHashMap<>());
        var classes = toClasses(classNames);
        result.addErrors(classes.getErrors());
        if (!classes.getErrors().hasErrors()) {
            for (var claz : classes.get()) {
                var m = parseUserClass(claz, classes.get());
                result.get().putAll(m.get());
                result.addErrors(m.getErrors());
            }
        }
        return result;
    }

    /** Returns the loadable classes among a list of class names.
     * A class that cannot be loaded is an error; a class that exists in more than one
     * place is a warning naming all places, the one actually used first.
     */
    static private FallibleObject<? extends Set<Class<?>>> toClasses(List<QualName> classNames) {
        var result = new FallibleObject<>(new LinkedHashSet<Class<?>>());
        for (var className : classNames) {
            try {
                result.get().add(UserSignature.loader.loadClass(className.toString()));
                var copies = findCopies(className.toString());
                if (copies.size() > 1) {
                    result
                        .getErrors()
                        .add(new FormatError(Severity.WARNING,
                            "Class '%s' is found in several places: %s (used), %s", className,
                            copies.get(0), String.join(", ", copies.subList(1, copies.size()))));
                }
            } catch (ClassNotFoundException exc) {
                result
                    .addError("Class '%s' cannot be loaded from %s", className,
                              sourceDescription);
            }
        }
        return result;
    }

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
     * and returns the methods on which those operations are based.
     */
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
                assert rcs != null : "Record type without record components";
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
                        : m.getParameterTypes()[i - 1])
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
    static private final Comparator<File> JAR_ORDER
        = Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER)
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
