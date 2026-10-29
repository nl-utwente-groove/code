/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2026
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
 */
package nl.utwente.groove.test.algebra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.CodeSource;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.spi.ToolProvider;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import nl.utwente.groove.algebra.AlgebraFamily;
import nl.utwente.groove.algebra.Operator;
import nl.utwente.groove.algebra.UserSignature;
import nl.utwente.groove.annotation.UserOperation;
import nl.utwente.groove.grammar.GrammarKey;
import nl.utwente.groove.io.store.SystemStore;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.UserClassPath;
import nl.utwente.groove.util.parse.FormatException;
import nl.utwente.groove.util.parse.Severity;

/**
 * Tests the class path sources for user-defined operations: a class that is
 * not on the application class path is found in the {@code lib} jars or the
 * {@code classes} directory of the grammar, through the grammar's
 * {@code userClassPath} property, or through the installation-level user class
 * path. The class is compiled at run time with the JDK's compiler tool, so the
 * tests are skipped on a runtime without it.
 */
@AIGenerated("Claude Fable 5.1, 2026-10")
@NonNullByDefault
public class UserClassPathTest {
    /** Restores the application class loader, so that the classes loaded here
     * do not leak into tests that run later in the same JVM.
     */
    @AfterEach
    void resetUserSignature() {
        System.clearProperty(UserClassPath.PROPERTY);
        UserSignature.setUserClass(List.of(), null);
    }

    @Test
    void classIsNotOnTheApplicationClassPath() {
        try {
            UserSignature.checkUserClass(NAMES).throwException();
            fail("The test class should not be loadable from the application class path");
        } catch (FormatException exc) {
            var message = exc.getMessage();
            assertNotNull(message);
            assertTrue(message.contains("cannot be loaded"), message);
        }
    }

    @Test
    void jarInLibIsLoaded(@TempDir Path tmp) throws IOException {
        Path grammar = tmp.resolve("jar.gps");
        Path jar = grammar.resolve(UserSignature.LIB_DIR).resolve("ops.jar");
        writeJar(jar, compile(tmp.resolve("build")));
        UserSignature.setUserClass(NAMES, grammar.toFile());
        assertTwice();
        // checking goes through the same loader
        try {
            UserSignature.checkUserClass(NAMES).throwException();
        } catch (FormatException exc) {
            fail(exc.getMessage());
        }
        // the jar itself is not held open by the loader, so it can be replaced
        Files.writeString(jar, "replaced");
        // and unloading makes the operation disappear again
        UserSignature.setUserClass(List.of(), null);
        assertNull(UserSignature.getOperators().get(OP_NAME));
    }

    @Test
    void classesDirectoryIsLoaded(@TempDir Path tmp) throws IOException {
        Path grammar = tmp.resolve("dir.gps");
        Path classFile = grammar
            .resolve(UserSignature.CLASSES_DIR)
            .resolve(PACKAGE)
            .resolve(SIMPLE_NAME + ".class");
        Files.createDirectories(classFile.getParent());
        Files.write(classFile, compile(tmp.resolve("build")));
        UserSignature.setUserClass(NAMES, grammar.toFile());
        assertTwice();
    }

    /** Tests that loading a grammar installs its grammar-local class path,
     * so that the grammar properties check out without errors, and that saving
     * the grammar elsewhere carries the class path along.
     */
    @Test
    void storeInstallsTheGrammarClassPath(@TempDir Path tmp) throws IOException {
        Path grammar = tmp.resolve("store.gps");
        Path jar = grammar.resolve(UserSignature.LIB_DIR).resolve("ops.jar");
        writeJar(jar, compile(tmp.resolve("build")));
        Files
            .writeString(grammar.resolve("system.properties"),
                         GrammarKey.USER_OPS.getName() + "=" + CLASS_NAME + "\n");
        var store = SystemStore.newStore(grammar.toFile(), false, true);
        var model = store.toGrammarModel();
        try {
            model.getProperties().check(model);
        } catch (FormatException exc) {
            fail(exc.getMessage());
        }
        assertTwice();
        // saving the grammar elsewhere copies the jar along
        Path copy = tmp.resolve("copy.gps");
        var copyStore = store.save(copy.toFile(), false);
        assertEquals(copy.toFile(), copyStore.getLocation());
        assertEquals(Files.size(jar),
                     Files.size(copy.resolve(UserSignature.LIB_DIR).resolve("ops.jar")));
        assertTwice();
    }

    /** Tests that a jar outside the grammar is found through a grammar-relative
     * entry of the {@code userClassPath} property, and that a missing entry is
     * reported on the property.
     */
    @Test
    void grammarClassPathIsResolvedAgainstTheGrammar(@TempDir Path tmp) throws IOException {
        Path grammar = tmp.resolve("rel.gps");
        Files.createDirectories(grammar);
        writeJar(tmp.resolve("ext").resolve("ops.jar"), compile(tmp.resolve("build")));
        Files
            .writeString(grammar.resolve("system.properties"),
                         GrammarKey.USER_OPS.getName() + "=" + CLASS_NAME + "\n"
                             + GrammarKey.USER_CLASS_PATH.getName() + "=../ext/ops.jar\n");
        var model = SystemStore.newStore(grammar.toFile(), false, true).toGrammarModel();
        try {
            model.getProperties().check(model);
        } catch (FormatException exc) {
            fail(exc.getMessage());
        }
        assertTwice();
        // a missing entry is an error on the property, with the class not found
        Files
            .writeString(grammar.resolve("system.properties"),
                         GrammarKey.USER_OPS.getName() + "=" + CLASS_NAME + "\n"
                             + GrammarKey.USER_CLASS_PATH.getName() + "=../missing\n");
        model = SystemStore.newStore(grammar.toFile(), false, true).toGrammarModel();
        try {
            model.getProperties().check(model);
            fail("The missing entry should be reported");
        } catch (FormatException exc) {
            var message = exc.getMessage();
            assertNotNull(message);
            assertTrue(message.contains("does not exist"), message);
            assertTrue(message.contains("cannot be loaded"), message);
        }
        assertNull(UserSignature.getOperators().get(OP_NAME));
    }

    /** Tests that the installation-level user class path, set through its system
     * property, is searched after the grammar sources.
     */
    @Test
    void userClassPathIsSearched(@TempDir Path tmp) throws IOException {
        Path grammar = tmp.resolve("inst.gps");
        Files.createDirectories(grammar);
        Path jar = tmp.resolve("elsewhere").resolve("ops.jar");
        writeJar(jar, compile(tmp.resolve("build")));
        Files
            .writeString(grammar.resolve("system.properties"),
                         GrammarKey.USER_OPS.getName() + "=" + CLASS_NAME + "\n");
        System.setProperty(UserClassPath.PROPERTY, jar.toString());
        assertEquals(List.of(jar), UserClassPath.get());
        var model = SystemStore.newStore(grammar.toFile(), false, true).toGrammarModel();
        try {
            model.getProperties().check(model);
        } catch (FormatException exc) {
            fail(exc.getMessage());
        }
        assertTwice();
    }

    /** Tests that a class present both in {@code classes/} and in a {@code lib/} jar
     * is reported as a warning naming both places, with the copy used first, and
     * that the jars are searched in case-insensitive name order.
     */
    @Test
    void duplicateClassIsAWarning(@TempDir Path tmp) throws IOException {
        Path grammar = tmp.resolve("dup.gps");
        byte[] bytes = compile(tmp.resolve("build"));
        Path classFile = grammar
            .resolve(UserSignature.CLASSES_DIR)
            .resolve(PACKAGE)
            .resolve(SIMPLE_NAME + ".class");
        Files.createDirectories(classFile.getParent());
        Files.write(classFile, bytes);
        // the case-insensitive order puts 'a.jar' before 'B.jar' on every platform
        writeJar(grammar.resolve(UserSignature.LIB_DIR).resolve("B.jar"), bytes);
        writeJar(grammar.resolve(UserSignature.LIB_DIR).resolve("a.jar"), bytes);
        UserSignature.setUserClass(NAMES, grammar.toFile());
        assertTwice();
        var check = UserSignature.checkUserClass(NAMES);
        assertFalse(check.getErrors().hasErrors(), check.getErrors().toString());
        assertEquals(1, count(check.getErrors()));
        var warning = check.getErrors().iterator().next();
        assertEquals(Severity.WARNING, warning.getSeverity());
        var message = warning.toString();
        assertTrue(message.contains("classes" + File.separator + " (grammar directory) (used)"),
                   message);
        assertTrue(message.indexOf("lib" + File.separator + "a.jar") < message
            .indexOf("lib" + File.separator + "B.jar"), message);
    }

    /** Tests that a class on GROOVE's own class path shadows a copy in the grammar,
     * and that this is reported as a warning.
     */
    @Test
    void grooveClassPathShadowsTheGrammar(@TempDir Path tmp) throws IOException {
        Path grammar = tmp.resolve("shadow.gps");
        var names = List.of(QualName.name(UserOps.class.getName()),
                            QualName.name(UserTypeIntString.class.getName()));
        var entries = new java.util.LinkedHashMap<String,byte[]>();
        for (var claz : List.of(UserOps.class, UserTypeIntString.class)) {
            try (var in = claz.getResourceAsStream(claz.getSimpleName() + ".class")) {
                assertNotNull(in);
                entries.put(claz.getName(), in.readAllBytes());
            }
        }
        writeJar(grammar.resolve(UserSignature.LIB_DIR).resolve("shadow.jar"), entries);
        UserSignature.setUserClass(names, grammar.toFile());
        var check = UserSignature.checkUserClass(names);
        assertFalse(check.getErrors().hasErrors(), check.getErrors().toString());
        assertEquals(2, count(check.getErrors()));
        for (var warning : check.getErrors()) {
            assertEquals(Severity.WARNING, warning.getSeverity());
            assertTrue(warning.toString().contains("GROOVE's own class path (used)"),
                       warning.toString());
        }
        // the class in use is the one of the application loader
        var one = UserSignature.getOperators().get("one");
        assertNotNull(one);
        var executable = UserSignature.getMethods().get(one);
        assertNotNull(executable);
        assertSame(UserOps.class.getClassLoader(),
                   executable.getDeclaringClass().getClassLoader());
    }

    /** Tests the round trip between class path strings and entry lists. */
    @Test
    void userClassPathParsesAndFormats() {
        assertEquals(List.of(), UserClassPath.parse(null));
        assertEquals(List.of(), UserClassPath.parse(" " + File.pathSeparator + " "));
        var entries = List.of(Path.of("a", "b.jar"), Path.of("c d"));
        var text = UserClassPath.format(entries);
        assertEquals("a" + File.separator + "b.jar" + File.pathSeparator + "c d", text);
        assertEquals(entries, UserClassPath.parse(" " + text + File.pathSeparator));
    }

    /** Returns the number of elements of an iterable. */
    private static int count(Iterable<?> iterable) {
        int result = 0;
        for (var it = iterable.iterator(); it.hasNext(); it.next()) {
            result++;
        }
        return result;
    }

    /** Asserts that the compiled operation is available and computes what it should. */
    private void assertTwice() {
        Operator op = UserSignature.getOperators().get(OP_NAME);
        assertNotNull(op);
        var operation = AlgebraFamily.DEFAULT.getOperation(op);
        assertNotNull(operation);
        assertEquals(6, operation.applyStrict(List.of(3)));
    }

    /**
     * Compiles the user class with the JDK's compiler tool and returns its bytes;
     * skips the test by assumption if the tool is not available.
     */
    private static byte[] compile(Path buildDir) throws IOException {
        Optional<ToolProvider> found = ToolProvider.findFirst("javac");
        assumeTrue(found.isPresent(), "no javac tool in this runtime");
        ToolProvider javac = found.get();
        Files.createDirectories(buildDir);
        Path source = buildDir.resolve(SIMPLE_NAME + ".java");
        Files.writeString(source, SOURCE);
        var out = new StringWriter();
        var err = new StringWriter();
        int status = javac
            .run(new PrintWriter(out), new PrintWriter(err), "-d", buildDir.toString(), "-cp",
                 compileClassPath(), "-proc:none", "-implicit:none", source.toString());
        assertEquals(0, status, "javac failed: " + err + out);
        return Files.readAllBytes(buildDir.resolve(PACKAGE).resolve(SIMPLE_NAME + ".class"));
    }

    /**
     * Returns the class path to compile the user class against: wherever the GROOVE
     * classes come from, plus the module path and class path of this JVM.
     */
    private static String compileClassPath() {
        var result = new StringBuilder();
        CodeSource groove = UserOperation.class.getProtectionDomain().getCodeSource();
        if (groove != null) {
            try {
                result.append(Path.of(groove.getLocation().toURI()));
            } catch (URISyntaxException exc) {
                throw new AssertionError(exc);
            }
        }
        for (String property : List.of("jdk.module.path", "java.class.path")) {
            @Nullable
            String value = System.getProperty(property);
            if (value != null && !value.isEmpty()) {
                result.append(File.pathSeparator).append(value);
            }
        }
        return result.toString();
    }

    /** Writes a jar containing the user class. */
    private static void writeJar(Path path, byte[] classBytes) throws IOException {
        writeJar(path, Map.of(CLASS_NAME, classBytes));
    }

    /** Writes a jar containing the given classes, keyed by qualified name. */
    private static void writeJar(Path path, Map<String,byte[]> classes) throws IOException {
        Files.createDirectories(path.getParent());
        try (var jar = new JarOutputStream(Files.newOutputStream(path))) {
            for (var entry : classes.entrySet()) {
                jar.putNextEntry(new JarEntry(entry.getKey().replace('.', '/') + ".class"));
                jar.write(entry.getValue());
                jar.closeEntry();
            }
        }
    }

    /** Package of the compiled user class. */
    private static final String PACKAGE = "jarops";
    /** Simple name of the compiled user class. */
    private static final String SIMPLE_NAME = "JarOps";
    /** Qualified name of the compiled user class. */
    private static final String CLASS_NAME = PACKAGE + "." + SIMPLE_NAME;
    /** Name of the operation declared in the user class. */
    private static final String OP_NAME = "twice";
    /** The user class names as handed to the signature. */
    private static final List<QualName> NAMES = List.of(QualName.name(CLASS_NAME));
    /** Source of the user class. */
    private static final String SOURCE = """
        package %s;
        import nl.utwente.groove.annotation.UserOperation;
        public class %s {
            @UserOperation
            public static int %s(int x) { return 2 * x; }
        }
        """.formatted(PACKAGE, SIMPLE_NAME, OP_NAME);
}
