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
package nl.utwente.groove.util;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * The extension directory of a GROOVE installation, and the jars found in it.
 * <p>
 * Optional parts of GROOVE that cannot ship with the standard distribution are installed
 * by the user into a user-level directory, from whose jars they are loaded at start-up;
 * the yFiles graph backend is the first such part (gh #909). The directory is the one
 * named by the system property {@link #DIR_PROPERTY} if it is set, otherwise the platform
 * default of {@link #defaultDir()}. The jars in the directory itself and in its immediate
 * subdirectories (except entries whose name starts with a dot, which are hidden from the
 * scan) are loaded through one class loader whose parent is the application's
 * class loader, so that the extension classes see GROOVE and its dependencies, while
 * GROOVE itself sees the extensions only through services ({@link java.util.ServiceLoader}
 * over {@link #getLoader()}).
 * <p>
 * An extension jar compiled against GROOVE declares the GROOVE version it was built for
 * in the manifest attribute {@link #VERSION_ATTRIBUTE}. A jar declaring another version
 * than the running one is skipped with a warning, since it would otherwise fail at an
 * arbitrary later moment (typically with a {@link NoSuchMethodError}); a jar without the
 * attribute (a library the extension needs) is loaded as it is. The check is on the
 * version string, so a jar built for the same snapshot version as a later development
 * build passes it even if the interfaces changed in between.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public final class Extensions {
    /**
     * Scans a given directory for extension jars.
     * The result is not shared; the extension directory of this run is scanned once,
     * by {@link #instance()}.
     * @param dir the directory to scan; need not exist
     */
    public static Extensions scan(Path dir) {
        return new Extensions(dir);
    }

    // the class loader is never closed: it lives as long as the JVM
    @SuppressWarnings("resource")
    private Extensions(Path dir) {
        this.dir = dir;
        this.jars = collect(dir);
        var accepted = this.jars.stream().filter(Jar::accepted).map(Jar::path).toList();
        ClassLoader parent = Extensions.class.getClassLoader();
        assert parent != null : "GROOVE is not loaded by the bootstrap loader";
        this.loader = accepted.isEmpty()
            ? parent
            : new URLClassLoader("groove-extensions", toURLs(accepted), parent);
    }

    /** Returns the directory that was scanned. */
    public Path getDir() {
        return this.dir;
    }

    private final Path dir;

    /**
     * Returns the jars found in the directory, in the order in which the accepted ones
     * are on the class path of {@link #getLoader()}.
     */
    public List<Jar> getJars() {
        return this.jars;
    }

    private final List<Jar> jars;

    /**
     * Returns the class loader through which the accepted extension jars are loaded.
     * If there are none, this is the application's class loader itself.
     */
    public ClassLoader getLoader() {
        return this.loader;
    }

    private final ClassLoader loader;

    /** Collects the jars of a directory and its immediate subdirectories, sorted by path. */
    private static List<Jar> collect(Path dir) {
        if (!Files.isDirectory(dir)) {
            LOGGER.log(Level.DEBUG, "No extension directory {0}", dir);
            return List.of();
        }
        List<Path> paths = new ArrayList<>();
        try (Stream<Path> entries = Files.list(dir)) {
            for (Path entry : entries.sorted().toList()) {
                if (isHidden(entry)) {
                    continue;
                }
                if (isJar(entry)) {
                    paths.add(entry);
                } else if (Files.isDirectory(entry)) {
                    try (Stream<Path> subEntries = Files.list(entry)) {
                        subEntries
                            .sorted()
                            .filter(p -> !isHidden(p) && isJar(p))
                            .forEach(paths::add);
                    }
                }
            }
        } catch (IOException exc) {
            LOGGER.log(Level.WARNING, "Cannot read extension directory {0}: {1}", dir, exc);
        }
        List<Jar> result = new ArrayList<>();
        for (Path path : paths) {
            Jar jar = Jar.read(path);
            // a skipped jar is worth a warning on the console; a loaded one is not
            LOGGER.log(jar.accepted()
                ? Level.DEBUG
                : Level.WARNING, "{0}", jar);
            result.add(jar);
        }
        return List.copyOf(result);
    }

    /** Entries with a leading dot are hidden from the scan, e.g. an add-on being installed. */
    private static boolean isHidden(Path path) {
        return path.getFileName().toString().startsWith(".");
    }

    private static boolean isJar(Path path) {
        return Files.isRegularFile(path)
            && path.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".jar");
    }

    private static URL[] toURLs(List<Path> paths) {
        URL[] result = new URL[paths.size()];
        for (int i = 0; i < result.length; i++) {
            try {
                result[i] = paths.get(i).toUri().toURL();
            } catch (MalformedURLException exc) {
                throw Exceptions.unreachable();
            }
        }
        return result;
    }

    /**
     * Returns the extension directory of this run: the one named by the system property
     * {@link #DIR_PROPERTY} if set, otherwise {@link #defaultDir()}. The directory need
     * not exist.
     */
    public static Path dir() {
        String property = System.getProperty(DIR_PROPERTY);
        return property == null
            ? defaultDir()
            : Path.of(property);
    }

    /**
     * Returns the platform default of the extension directory: a user-writable location
     * where applications keep their data on the platform, with a subdirectory for GROOVE.
     * On Windows this is {@code %APPDATA%\GROOVE\extensions}, on macOS
     * {@code ~/Library/Application Support/GROOVE/extensions}, elsewhere
     * {@code ~/.groove/extensions}.
     */
    public static Path defaultDir() {
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        String home = System.getProperty("user.home", ".");
        Path base;
        if (os.contains("win")) {
            String appData = System.getenv("APPDATA");
            base = appData == null
                ? Path.of(home, "AppData", "Roaming", "GROOVE")
                : Path.of(appData, "GROOVE");
        } else if (os.contains("mac")) {
            base = Path.of(home, "Library", "Application Support", "GROOVE");
        } else {
            base = Path.of(home, ".groove");
        }
        return base.resolve("extensions");
    }

    /** Returns the scan of the extension directory of this run, made at the first call. */
    public static Extensions instance() {
        return Instance.INSTANCE;
    }

    /** Lazy holder of the scan of {@link #dir()}. */
    private static final class Instance {
        private Instance() {
            // not to be instantiated
        }

        static final Extensions INSTANCE = scan(dir());
    }

    /** System property naming the extension directory, overriding the platform default. */
    public static final String DIR_PROPERTY = "groove.extensions.dir";
    /**
     * Manifest attribute by which an extension jar declares the GROOVE version it was
     * built against; a jar declaring another version than {@link Version#NUMBER} is skipped.
     */
    public static final Attributes.Name VERSION_ATTRIBUTE = new Attributes.Name("GROOVE-Version");

    private static final Logger LOGGER = Log.getLogger("util.extensions");

    /**
     * A jar found in the extension directory.
     * @param path the location of the jar
     * @param version the GROOVE version declared in the jar's manifest; {@code null} if
     * the jar declares none, or could not be read
     * @param status whether the jar is loaded, and if not, why not
     */
    public record Jar(Path path, @Nullable String version, Status status) {
        /** Reads the manifest of a jar file and determines its status. */
        static Jar read(Path path) {
            try (JarFile jar = new JarFile(path.toFile())) {
                Manifest manifest = jar.getManifest();
                String version = manifest == null
                    ? null
                    : manifest.getMainAttributes().getValue(VERSION_ATTRIBUTE);
                Status status = version == null || version.equals(Version.NUMBER)
                    ? Status.ACCEPTED
                    : Status.STALE;
                return new Jar(path, version, status);
            } catch (IOException exc) {
                return new Jar(path, null, Status.UNREADABLE);
            }
        }

        /** Indicates if this jar is loaded, i.e., has status {@link Status#ACCEPTED}. */
        public boolean accepted() {
            return status() == Status.ACCEPTED;
        }

        @Override
        public String toString() {
            return switch (status()) {
            case ACCEPTED -> "Loading extension jar " + path();
            case STALE -> "Skipping extension jar " + path() + ": built for GROOVE " + version()
                + ", this is GROOVE " + Version.NUMBER;
            case UNREADABLE -> "Skipping extension jar " + path() + ": not a readable jar file";
            };
        }
    }

    /** Status of a jar in the extension directory. */
    public enum Status {
        /** The jar is loaded. */
        ACCEPTED,
        /** The jar declares another GROOVE version than the running one, and is skipped. */
        STALE,
        /** The jar cannot be read, and is skipped. */
        UNREADABLE;
    }
}
