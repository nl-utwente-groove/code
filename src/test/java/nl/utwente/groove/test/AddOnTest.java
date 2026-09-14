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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.AddOn;
import nl.utwente.groove.util.AddOn.Outcome;
import nl.utwente.groove.util.AddOn.Pending;
import nl.utwente.groove.util.AddOn.Status;
import nl.utwente.groove.util.Extensions;
import nl.utwente.groove.util.Version;

/**
 * Checks the installation of an add-on from its zip into an extension directory:
 * the unpacking and verification, the rejection of a zip for another GROOVE version
 * or of another shape, the status as seen by the extension scan, and the removal.
 * The download itself needs the network and is not tested.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public class AddOnTest {
    @Test
    void namesDeriveFromTheVersion() {
        assertEquals("groove-7_5_4-yfiles-addon.zip", AddOn.YFILES.getZipName("7.5.4"));
        assertEquals("https://github.com/nl-utwente-groove/code/releases/download/release-7_5_4/groove-7_5_4-yfiles-addon.zip",
                     AddOn.YFILES.getDownloadUri("7.5.4").toString());
        assertEquals("YFILES-ADDON.md", AddOn.YFILES.getNoticeName());
    }

    @Test
    void installsVerifiesAndRemoves(@TempDir Path tmp) throws IOException {
        Path ext = tmp.resolve("ext");
        AddOn addOn = AddOn.YFILES;
        assertEquals(Status.ABSENT, addOn.getStatus(Extensions.scan(ext)));
        assertFalse(addOn.isPresent(ext));

        // a proper add-on zip: the directory with a versioned jar, a library and the notice
        Path zip = writeZip(tmp.resolve("good.zip"),
                            Map.of("yfiles/groove-yfiles.jar", jar(Version.NUMBER),
                                   "yfiles/lib.jar", jar(null), "yfiles/YFILES-ADDON.md",
                                   "notice".getBytes()));
        assertEquals(Outcome.DONE, addOn.install(zip, ext));
        Path dir = addOn.getDir(ext);
        assertEquals(ext.resolve("yfiles"), dir);
        assertTrue(Files.isRegularFile(dir.resolve("groove-yfiles.jar")));
        assertTrue(Files.isRegularFile(dir.resolve("lib.jar")));
        assertEquals("notice", Files.readString(dir.resolve("YFILES-ADDON.md")));
        assertTrue(addOn.isPresent(ext));
        assertEquals(Status.INSTALLED, addOn.getStatus(Extensions.scan(ext)));
        // no staging directory is left behind
        assertEquals(1, entries(ext));

        // a zip for another version is rejected, and the installed add-on is untouched
        Path stale = writeZip(tmp.resolve("stale.zip"),
                              Map.of("yfiles/groove-yfiles.jar", jar("0.0.0")));
        var exc = assertThrows(IOException.class, () -> addOn.install(stale, ext));
        String message = exc.getMessage();
        assertTrue(message != null && message.contains("0.0.0"), message);
        assertTrue(Files.isRegularFile(dir.resolve("lib.jar")));
        assertEquals(1, entries(ext));
        // so is a zip without a versioned jar, or with entries outside the add-on directory
        Path unversioned = writeZip(tmp.resolve("unversioned.zip"),
                                    Map.of("yfiles/lib.jar", jar(null)));
        assertThrows(IOException.class, () -> addOn.install(unversioned, ext));
        Path elsewhere = writeZip(tmp.resolve("elsewhere.zip"),
                                  Map.of("other/groove-yfiles.jar", jar(Version.NUMBER)));
        assertThrows(IOException.class, () -> addOn.install(elsewhere, ext));
        Path escaping = writeZip(tmp.resolve("escaping.zip"),
                                 Map.of("yfiles/../escaped.jar", jar(Version.NUMBER)));
        assertThrows(IOException.class, () -> addOn.install(escaping, ext));
        assertFalse(Files.exists(ext.resolve("escaped.jar")));
        assertEquals(1, entries(ext));

        // an installed add-on for another version is stale, and replaced by a new installation
        Files.delete(dir.resolve("groove-yfiles.jar"));
        Files.write(dir.resolve("groove-yfiles.jar"), jar("0.0.0"));
        assertEquals(Status.STALE, addOn.getStatus(Extensions.scan(ext)));
        assertEquals(Outcome.DONE, addOn.install(zip, ext));
        assertEquals(Status.INSTALLED, addOn.getStatus(Extensions.scan(ext)));

        assertEquals(Outcome.DONE, addOn.uninstall(ext));
        assertFalse(addOn.isPresent(ext));
        assertEquals(Status.ABSENT, addOn.getStatus(Extensions.scan(ext)));
        assertEquals(Outcome.DONE, addOn.uninstall(ext));
        assertEquals(Pending.NONE, addOn.getPending(ext));
    }

    /**
     * Checks that a removal or replacement of an add-on whose files cannot be deleted is
     * deferred to the next scan, which hides the add-on until it succeeds; and that a
     * pending removal and a pending installation cancel one another.
     */
    @Test
    void defersWhileFilesAreInUse(@TempDir Path tmp) throws Exception {
        Path ext = tmp.resolve("ext");
        AddOn addOn = AddOn.YFILES;
        Path zip = writeZip(tmp.resolve("good.zip"),
                            Map.of("yfiles/groove-yfiles.jar", jar(Version.NUMBER),
                                   "yfiles/lib.jar", jar(null), "yfiles/YFILES-ADDON.md",
                                   "notice".getBytes()));
        assertEquals(Outcome.DONE, addOn.install(zip, ext));
        Path dir = addOn.getDir(ext);
        Path jar = dir.resolve("groove-yfiles.jar");
        Path pending = Extensions.pendingInstallDir(ext, addOn.getName());

        // a removal that cannot delete the files is left pending, with the add-on intact
        try (var lock = lock(jar)) {
            assertEquals(Outcome.DEFERRED, addOn.uninstall(ext));
            assertEquals(Pending.REMOVE, addOn.getPending(ext));
            assertTrue(addOn.isPresent(ext));
            assertTrue(Files.exists(jar));
            assertTrue(Files.exists(dir.resolve("lib.jar")));
            // a scan while the files are still in use hides the add-on, and the removal stays pending
            assertEquals(Status.ABSENT, addOn.getStatus(Extensions.scan(ext)));
            assertEquals(Pending.REMOVE, addOn.getPending(ext));
            assertTrue(Files.exists(dir.resolve("lib.jar")));
            // so the removal can still be cancelled
            addOn.reactivate(ext);
            assertEquals(Pending.NONE, addOn.getPending(ext));
            assertEquals(Status.INSTALLED, addOn.getStatus(Extensions.scan(ext)));
            assertEquals(Outcome.DEFERRED, addOn.uninstall(ext));
            assertEquals(Pending.REMOVE, addOn.getPending(ext));
        }
        // the next scan carries the removal out
        assertEquals(Status.ABSENT, addOn.getStatus(Extensions.scan(ext)));
        assertFalse(Files.exists(dir));
        assertEquals(Pending.NONE, addOn.getPending(ext));
        assertFalse(addOn.isPresent(ext));
        assertEquals(0, entries(ext));

        // a replacement that cannot delete the old version leaves the new one pending
        assertEquals(Outcome.DONE, addOn.install(zip, ext));
        try (var lock = lock(jar)) {
            assertEquals(Outcome.DEFERRED, addOn.install(zip, ext));
            assertEquals(Pending.INSTALL, addOn.getPending(ext));
            assertTrue(addOn.isPresent(ext));
            assertTrue(Files.isRegularFile(pending.resolve("groove-yfiles.jar")));
            // a scan while the old version is still in use hides the add-on, and the installation stays pending
            assertEquals(Status.ABSENT, addOn.getStatus(Extensions.scan(ext)));
            assertEquals(Pending.INSTALL, addOn.getPending(ext));
            // a removal cancels the pending installation
            assertEquals(Outcome.DEFERRED, addOn.uninstall(ext));
            assertEquals(Pending.REMOVE, addOn.getPending(ext));
            assertFalse(Files.exists(pending));
            // and an installation cancels the pending removal
            assertEquals(Outcome.DEFERRED, addOn.install(zip, ext));
            assertEquals(Pending.INSTALL, addOn.getPending(ext));
        }
        // the next scan puts the new version in place
        assertEquals(Status.INSTALLED, addOn.getStatus(Extensions.scan(ext)));
        assertEquals(Pending.NONE, addOn.getPending(ext));
        assertTrue(Files.isRegularFile(jar));
        assertEquals("notice", Files.readString(dir.resolve("YFILES-ADDON.md")));
        assertEquals(1, entries(ext));
    }

    /**
     * Keeps the files of an add-on directory from being deleted or moved, until the
     * result is closed, by holding a jar open as the extension loader does with the jars
     * of a loaded add-on. This only works on Windows (a {@link JarFile} is opened without
     * delete sharing there; a NIO stream would be opened with it), and elsewhere an open
     * file does not stop deletion at all, so the test is skipped by assumption on other
     * platforms.
     */
    private static AutoCloseable lock(Path jar) throws IOException {
        assumeTrue(System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win"),
                   "an open file blocks deletion only on Windows");
        return new JarFile(jar.toFile());
    }

    /** Returns the number of entries of a directory. */
    private static long entries(Path dir) throws IOException {
        try (var stream = Files.list(dir)) {
            return stream.count();
        }
    }

    /** Returns the bytes of a jar with only a manifest, declaring an optional GROOVE version. */
    private static byte[] jar(@Nullable String version) throws IOException {
        var manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        if (version != null) {
            manifest.getMainAttributes().put(Extensions.VERSION_ATTRIBUTE, version);
        }
        var bytes = new java.io.ByteArrayOutputStream();
        try (var jar = new java.util.jar.JarOutputStream(bytes, manifest)) {
            // only the manifest
        }
        return bytes.toByteArray();
    }

    /** Writes a zip with given entries. */
    private static Path writeZip(Path path, Map<String,byte[]> entries) throws IOException {
        try (var zip = new ZipOutputStream(Files.newOutputStream(path))) {
            for (var entry : entries.entrySet()) {
                zip.putNextEntry(new ZipEntry(entry.getKey()));
                zip.write(entry.getValue());
                zip.closeEntry();
            }
        }
        return path;
    }
}
