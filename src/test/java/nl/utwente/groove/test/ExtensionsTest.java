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
 *
 * $Id$
 */
package nl.utwente.groove.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.CodeSource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.spi.ToolProvider;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import nl.utwente.groove.gui.view.GraphBackend;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.Extensions;
import nl.utwente.groove.util.Extensions.Status;
import nl.utwente.groove.util.Version;

/**
 * Checks the scanning of the extension directory and the discovery of graph backends
 * through it: a backend jar built for the running version is loaded, one built for
 * another version is skipped, and the backend on the class path is found exactly once
 * either way.
 * <p>
 * The tests run against the temporary directory they fill, not against the extension
 * directory of the run (which the test configuration points to an empty location
 * anyway). The provider class is compiled into the jar at run time, with the
 * {@code javac} tool of the running JDK: a class from the test class path would not do,
 * since the class loader of the extension jar delegates to its parent first, and the
 * {@link java.util.ServiceLoader} in any case ignores class-path providers that live in
 * a named module, which is where surefire patches the test classes. The tests are skipped
 * on a runtime without the compiler tool.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public class ExtensionsTest {
    @Test
    void defaultDirIsAbsoluteAndNamedExtensions() {
        Path dir = Extensions.defaultDir();
        assertTrue(dir.isAbsolute(), dir.toString());
        assertEquals("extensions", dir.getFileName().toString());
    }

    @Test
    void missingOrEmptyDirectoryUsesTheApplicationLoader(@TempDir Path tmp) throws IOException {
        var missing = Extensions.scan(tmp.resolve("missing"));
        assertTrue(missing.getJars().isEmpty());
        assertSame(Extensions.class.getClassLoader(), missing.getLoader());
        Files.writeString(tmp.resolve("notes.txt"), "not a jar");
        var empty = Extensions.scan(tmp);
        assertTrue(empty.getJars().isEmpty());
        assertSame(Extensions.class.getClassLoader(), empty.getLoader());
        assertEquals(List.of(GraphBackend.JGRAPH), names(GraphBackend.discover(empty.getLoader())));
    }

    @Test
    void jarsAreScannedAndVersionGuarded(@TempDir Path tmp) throws IOException {
        byte[] provider = compileProvider(tmp.resolve("build"));
        // a backend jar for the running version, in the directory itself
        Path current = tmp.resolve("current.jar");
        writeJar(current, Version.NUMBER, provider);
        // a backend jar for another version, in a subdirectory
        Path stale = tmp.resolve("old").resolve("stale.jar");
        writeJar(stale, "0.0.0", provider);
        // a jar without a version, e.g. a library
        Path plain = tmp.resolve("plain.jar");
        writeJar(plain, null, null);
        // a non-jar and a nested subdirectory, both ignored
        Files.writeString(tmp.resolve("notes.txt"), "not a jar");
        Path deep = tmp.resolve("old").resolve("deeper").resolve("deep.jar");
        writeJar(deep, Version.NUMBER, provider);

        var ext = Extensions.scan(tmp);
        assertEquals(tmp, ext.getDir());
        assertEquals(List.of(current, stale, plain),
                     ext.getJars().stream().map(Extensions.Jar::path).toList());
        var status = ext
            .getJars()
            .stream()
            .collect(Collectors.toMap(Extensions.Jar::path, Extensions.Jar::status));
        assertEquals(Map.of(current, Status.ACCEPTED, stale, Status.STALE, plain, Status.ACCEPTED), status);
        assertEquals("0.0.0", ext.getJars().get(1).version());
        assertNull(ext.getJars().get(2).version());

        // the provider class is loaded from the jar by the extension loader
        ClassLoader loader = ext.getLoader();
        assertTrue(loader != Extensions.class.getClassLoader());
        // the loader keeps the jars open, which would keep JUnit from deleting the
        // directory on Windows; the loader of the real extension directory lives as
        // long as the JVM and is never closed
        try (var closeable = (URLClassLoader) loader) {
            Class<?> providerClass = loader.loadClass(PROVIDER_NAME);
            assertSame(loader, providerClass.getClassLoader());
            // and discovered as a backend, next to the one on the class path
            var backends = GraphBackend.discover(loader);
            assertEquals(2, backends.size(), names(backends).toString());
            assertTrue(names(backends).contains(GraphBackend.JGRAPH));
            assertTrue(names(backends).contains(PROVIDER_BACKEND));
        } catch (ClassNotFoundException exc) {
            throw new AssertionError(exc);
        }
    }

    @Test
    void unreadableJarIsSkipped(@TempDir Path tmp) throws IOException {
        Files.writeString(tmp.resolve("broken.jar"), "not a zip");
        var ext = Extensions.scan(tmp);
        assertEquals(1, ext.getJars().size());
        assertEquals(Status.UNREADABLE, ext.getJars().get(0).status());
        assertSame(Extensions.class.getClassLoader(), ext.getLoader());
    }

    private static List<String> names(List<GraphBackend> backends) {
        return backends.stream().map(GraphBackend::getName).toList();
    }

    /**
     * Compiles the provider class with the JDK's compiler tool and returns its bytes;
     * skips the test by assumption if the tool is not available.
     */
    private static byte[] compileProvider(Path buildDir) throws IOException {
        Optional<ToolProvider> found = ToolProvider.findFirst("javac");
        assumeTrue(found.isPresent(), "no javac tool in this runtime");
        ToolProvider javac = found.get();
        Files.createDirectories(buildDir);
        Path source = buildDir.resolve("ExtBackend.java");
        Files.writeString(source, PROVIDER_SOURCE);
        var out = new StringWriter();
        var err = new StringWriter();
        int status = javac
            .run(new PrintWriter(out), new PrintWriter(err), "-d", buildDir.toString(), "-cp",
                 compileClassPath(), "-proc:none", "-implicit:none", source.toString());
        assertEquals(0, status, "javac failed: " + err + out);
        return Files.readAllBytes(buildDir.resolve("ext").resolve("ExtBackend.class"));
    }

    /**
     * Returns the class path to compile the provider against: wherever the GROOVE classes
     * come from, plus the module path and class path of this JVM.
     */
    private static String compileClassPath() {
        var result = new StringBuilder();
        CodeSource groove = GraphBackend.class.getProtectionDomain().getCodeSource();
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

    /**
     * Writes a jar with an optional GROOVE version in its manifest and an optional
     * provider class with its service declaration.
     */
    private static void writeJar(Path path, @Nullable String version,
                                 byte @Nullable [] provider) throws IOException {
        Files.createDirectories(path.getParent());
        var manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        if (version != null) {
            manifest.getMainAttributes().put(Extensions.VERSION_ATTRIBUTE, version);
        }
        try (var jar = new JarOutputStream(Files.newOutputStream(path), manifest)) {
            if (provider != null) {
                jar.putNextEntry(new JarEntry(PROVIDER_NAME.replace('.', '/') + ".class"));
                jar.write(provider);
                jar.closeEntry();
                jar.putNextEntry(new JarEntry("META-INF/services/" + GraphBackend.class.getName()));
                jar.write((PROVIDER_NAME + "\n").getBytes());
                jar.closeEntry();
            }
        }
    }

    /** Name of the backend provided by the compiled provider class. */
    private static final String PROVIDER_BACKEND = "ext";
    /** Name of the compiled provider class. */
    private static final String PROVIDER_NAME = "ext.ExtBackend";
    /** Source of the provider class: a backend with a name and no canvases. */
    private static final String PROVIDER_SOURCE = """
        package ext;
        import nl.utwente.groove.graph.Graph;
        import nl.utwente.groove.gui.view.*;
        public class ExtBackend implements GraphBackend {
            public String getName() { return "%s"; }
            public AspectGraphCanvas newAspectCanvas(AspectGraphViewController c) { throw new UnsupportedOperationException(); }
            public LTSGraphCanvas newLTSCanvas(LTSGraphViewController c) { throw new UnsupportedOperationException(); }
            public CtrlGraphCanvas newCtrlCanvas(CtrlGraphViewController c) { throw new UnsupportedOperationException(); }
            public GraphCanvas<Graph> newPlainCanvas(PlainGraphViewController c) { throw new UnsupportedOperationException(); }
        }
        """.formatted(PROVIDER_BACKEND);
}
