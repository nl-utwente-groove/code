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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.LongConsumer;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.util.Extensions.Jar;

/**
 * An add-on of GROOVE: an optional part distributed as a zip of its own, which is
 * installed into a subdirectory of the extension directory (see {@link Extensions}),
 * from which GROOVE loads it at start-up. The yFiles graph backend is the one add-on
 * there is (gh #909).
 * <p>
 * The zip of an add-on unpacks into a directory named after the add-on, holding its
 * jars and its notice, at least one of the jars declaring the GROOVE version the add-on
 * is built for. It is published with the GROOVE release of that version, under a name
 * derived from the version, so that a running GROOVE can download the add-on that
 * belongs to it.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public final class AddOn {
    /** The yFiles graph backend, licensed for non-commercial use only. */
    public static final AddOn YFILES = new AddOn("yfiles", "yFiles backend", "YFILES-ADDON.md");

    private AddOn(String name, String displayName, String noticeName) {
        this.name = name;
        this.displayName = displayName;
        this.noticeName = noticeName;
    }

    /** Returns the name of this add-on: the name of its directory and the infix of its zip. */
    public String getName() {
        return this.name;
    }

    private final String name;

    /** Returns the name of this add-on as shown to the user. */
    public String getDisplayName() {
        return this.displayName;
    }

    private final String displayName;

    /** Returns the file name of the notice that comes with this add-on. */
    public String getNoticeName() {
        return this.noticeName;
    }

    private final String noticeName;

    /** Returns the directory of this add-on within a given extension directory. */
    public Path getDir(Path extensionDir) {
        return extensionDir.resolve(getName());
    }

    /** Indicates if the directory of this add-on exists within a given extension directory. */
    public boolean isPresent(Path extensionDir) {
        return Files.isDirectory(getDir(extensionDir));
    }

    /**
     * Returns the status of this add-on in a scan of the extension directory:
     * {@link Status#INSTALLED} if the add-on's directory holds a jar built for the
     * running GROOVE version, {@link Status#STALE} if it holds jars built for GROOVE
     * versions but none for the running one, {@link Status#ABSENT} otherwise.
     */
    public Status getStatus(Extensions extensions) {
        Path dir = getDir(extensions.getDir());
        Status result = Status.ABSENT;
        for (Jar jar : extensions.getJars()) {
            if (jar.path().startsWith(dir) && jar.version() != null) {
                if (jar.accepted()) {
                    return Status.INSTALLED;
                }
                result = Status.STALE;
            }
        }
        return result;
    }

    /** Returns the file name of the zip of this add-on for a given GROOVE version. */
    public String getZipName(String version) {
        return "groove-" + version.replace('.', '_') + "-" + getName() + "-addon.zip";
    }

    /**
     * Returns the location from which the zip of this add-on for a given GROOVE
     * version is downloaded: the github release of that version.
     */
    public URI getDownloadUri(String version) {
        return URI
            .create(RELEASES + "release-" + version.replace('.', '_') + "/" + getZipName(version));
    }

    /** Location of the github releases of GROOVE. */
    private static final String RELEASES
        = "https://github.com/nl-utwente-groove/code/releases/download/";

    /**
     * Downloads the zip of this add-on for a given GROOVE version to a temporary file.
     * @param version the GROOVE version whose add-on is downloaded
     * @param progress called with the number of bytes received so far, on the calling
     * thread; the total is given by {@link #getSize(String)}
     * @return the downloaded file, which the caller should delete after use
     * @throws IOException if there is no add-on for the version, or the download fails
     */
    public Path download(String version, LongConsumer progress) throws IOException {
        URI uri = getDownloadUri(version);
        Path result = Files.createTempFile("groove-addon-", ".zip");
        try {
            HttpResponse<InputStream> response
                = send(HttpRequest.newBuilder(uri).GET().build(), HttpResponse.BodyHandlers.ofInputStream());
            checkStatus(response.statusCode(), version, uri);
            try (InputStream in = response.body(); OutputStream out = Files.newOutputStream(result)) {
                byte[] buffer = new byte[BUFFER_SIZE];
                long received = 0;
                int read;
                while ((read = in.read(buffer)) >= 0) {
                    out.write(buffer, 0, read);
                    received += read;
                    progress.accept(received);
                }
            }
            return result;
        } catch (IOException | RuntimeException exc) {
            Files.deleteIfExists(result);
            throw exc;
        }
    }

    /**
     * Returns the size in bytes of the zip of this add-on for a given GROOVE version,
     * as reported by its download location; {@code -1} if the location does not report
     * it, or does not answer the request (the download itself may still succeed).
     * @throws IOException if there is no add-on for the version
     */
    public long getSize(String version) throws IOException {
        URI uri = getDownloadUri(version);
        HttpResponse<Void> response
            = send(HttpRequest.newBuilder(uri).method("HEAD", HttpRequest.BodyPublishers.noBody()).build(),
                   HttpResponse.BodyHandlers.discarding());
        if (response.statusCode() == 404) {
            checkStatus(404, version, uri);
        }
        return response.statusCode() == 200
            ? response.headers().firstValueAsLong("Content-Length").orElse(-1L)
            : -1L;
    }

    private <T> HttpResponse<T> send(HttpRequest request,
                                     HttpResponse.BodyHandler<T> handler) throws IOException {
        try {
            return CLIENT.send(request, handler);
        } catch (InterruptedException exc) {
            Thread.currentThread().interrupt();
            throw new IOException("Download interrupted", exc);
        }
    }

    private void checkStatus(int status, String version, URI uri) throws IOException {
        if (status == 404) {
            throw new IOException("No " + getDisplayName() + " add-on is published for GROOVE "
                + version + " (" + uri + ")");
        } else if (status != 200) {
            throw new IOException("Download of " + uri + " failed with HTTP status " + status);
        }
    }

    /** Shared client; github serves the release files from another host, hence the redirects. */
    private static final HttpClient CLIENT = HttpClient
        .newBuilder()
        .followRedirects(HttpClient.Redirect.NORMAL)
        .connectTimeout(Duration.ofSeconds(20))
        .build();

    /**
     * Installs this add-on from its zip into a given extension directory, replacing an
     * installed version. The zip must consist of a directory named after this add-on,
     * holding at least one jar built for the running GROOVE version (see
     * {@link Extensions#VERSION_ATTRIBUTE}); anything else is rejected, leaving the
     * extension directory as it was. The zip is unpacked into a staging directory
     * first, hidden from the scan of {@link Extensions} by its leading dot, so that
     * an installation that fails halfway leaves nothing that would be loaded.
     * @param zip the zip file to install from
     * @param extensionDir the extension directory to install into; created if absent
     * @return the directory of the installed add-on
     * @throws IOException if the zip is not an add-on for the running version, or the
     * installation fails
     */
    public Path install(Path zip, Path extensionDir) throws IOException {
        Files.createDirectories(extensionDir);
        Path target = getDir(extensionDir);
        Path staging = extensionDir.resolve("." + getName() + "-installing");
        deleteRecursively(staging);
        try {
            unzip(zip, staging);
            checkVersion(staging);
            deleteRecursively(target);
            Files.move(staging, target, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException | RuntimeException exc) {
            deleteRecursively(staging);
            throw exc;
        }
        return target;
    }

    /** Unpacks the add-on's directory in a zip into a staging directory. */
    private void unzip(Path zip, Path staging) throws IOException {
        String prefix = getName() + "/";
        boolean empty = true;
        try (ZipInputStream in = new ZipInputStream(Files.newInputStream(zip))) {
            for (ZipEntry entry = in.getNextEntry(); entry != null; entry = in.getNextEntry()) {
                String entryName = entry.getName();
                if (!entryName.startsWith(prefix) || entryName.contains("..")
                    || entryName.contains("\\")) {
                    throw new IOException("Not a " + getDisplayName() + " add-on zip: unexpected entry '"
                        + entryName + "' in " + zip);
                }
                Path file = staging.resolve(entryName.substring(prefix.length()));
                if (entry.isDirectory()) {
                    Files.createDirectories(file);
                } else {
                    Path parent = file.getParent();
                    assert parent != null;
                    Files.createDirectories(parent);
                    Files.copy(in, file);
                    empty = false;
                }
            }
        }
        if (empty) {
            throw new IOException("Not a " + getDisplayName() + " add-on zip: no files in " + zip);
        }
    }

    /**
     * Checks that a staging directory holds a jar built for the running GROOVE version.
     */
    private void checkVersion(Path staging) throws IOException {
        List<String> versions = new ArrayList<>();
        for (Path path : jars(staging)) {
            Jar jar = Jar.read(path);
            String version = jar.version();
            if (version == null) {
                continue;
            }
            if (jar.accepted()) {
                return;
            }
            versions.add(version);
        }
        String reason = versions.isEmpty()
            ? "it declares no GROOVE version"
            : "it is built for GROOVE " + String.join(", ", versions);
        throw new IOException("This " + getDisplayName() + " add-on cannot be installed: " + reason
            + ", and this is GROOVE " + Version.NUMBER);
    }

    private static List<Path> jars(Path dir) throws IOException {
        try (Stream<Path> files = Files.walk(dir)) {
            return files
                .filter(p -> Files.isRegularFile(p) && p.getFileName().toString().endsWith(".jar"))
                .sorted()
                .toList();
        }
    }

    /**
     * Removes this add-on from a given extension directory, if it is present.
     * @return {@code true} if the add-on was present
     */
    public boolean uninstall(Path extensionDir) throws IOException {
        boolean result = isPresent(extensionDir);
        deleteRecursively(getDir(extensionDir));
        return result;
    }

    private static void deleteRecursively(Path dir) throws IOException {
        if (!Files.exists(dir)) {
            return;
        }
        try (Stream<Path> files = Files.walk(dir)) {
            for (Path path : files.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(path);
            }
        }
    }

    private static final int BUFFER_SIZE = 64 * 1024;

    /** Status of an add-on in the extension directory. */
    public enum Status {
        /** The add-on is not installed. */
        ABSENT,
        /** The add-on is installed, but for another GROOVE version; it is not loaded. */
        STALE,
        /** The add-on is installed for the running GROOVE version. */
        INSTALLED;
    }
}
