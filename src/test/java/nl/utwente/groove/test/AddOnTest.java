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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.AddOn;
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
        Path dir = addOn.install(zip, ext);
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
        addOn.install(zip, ext);
        assertEquals(Status.INSTALLED, addOn.getStatus(Extensions.scan(ext)));

        assertTrue(addOn.uninstall(ext));
        assertFalse(addOn.isPresent(ext));
        assertEquals(Status.ABSENT, addOn.getStatus(Extensions.scan(ext)));
        assertFalse(addOn.uninstall(ext));
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
