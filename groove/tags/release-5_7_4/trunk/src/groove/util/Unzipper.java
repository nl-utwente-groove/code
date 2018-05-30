/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package groove.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Code copied from <a href="http://www.codejava.net/java-se/file-io/programmatically-extract-a-zip-file-using-java">here</a>.
 * This utility extracts files and directories of a standard zip file to
 * a destination directory.
 * @author www.codejava.net
 *
 */
public class Unzipper {
    /** Private constructor for the singleton instance. */
    private Unzipper() {
        // empty
    }

    /** Interprets an input specification as a URL to invoke {@link #unzip(URL)} on;
     * if the input is not a well-formed URL, interprets it as a file name to invoke
     * {@link #unzip(File)} on.
     * @param input input specification
     * @throws IOException if an error occurred during extraction
     */
    public Path unzip(String input) throws IOException {
        try {
            return unzip(new URL(input));
        } catch (MalformedURLException exc) {
            return unzip(new File(input));
        }
    }

    /**
     * Extracts a URL pointing to a zip file to a directory specified by
     * destDirectory (which will be created if does not exist and
     * emptied if it does exist).
     * @param url the URL pointing to a zip file
     * @throws IOException if an error occurred during extraction
     */
    public Path unzip(URL url) throws IOException {
        try (InputStream input = url.openStream()) {
            return instance().unzip(input);
        }
    }

    /**
     * Extracts a zipfile to a directory specified by
     * destDirectory (which will be created if does not exist and
     * emptied if it does exist).
     * @param file the zipfile
     * @throws IOException if an error occurred during extraction
     */
    public Path unzip(File file) throws IOException {
        try (InputStream input = new FileInputStream(file)) {
            return instance().unzip(input);
        }
    }

    /**
     * Extracts a zipped input stream to a fresh temporary directory.
     * @param input the zipped input stream
     * @throws IOException if an error occurred during extraction
     */
    public Path unzip(InputStream input) throws IOException {
        Path result = Files.createTempDirectory(null);
        try (ZipInputStream zipIn = new ZipInputStream(input)) {
            ZipEntry entry = zipIn.getNextEntry();
            // iterates over entries in the zip file
            while (entry != null) {
                String filePath = result + File.separator + entry.getName();
                if (!entry.isDirectory()) {
                    // if the entry is a file, extracts it
                    extractFile(zipIn, filePath);
                } else {
                    // if the entry is a directory, make the directory
                    File dir = new File(filePath);
                    dir.mkdir();
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        }
        return result;
    }

    /**
     * Extracts a zip entry (file entry).
     * @param zipIn the input stream containing the entry to be extracted
     * @param filePath the path to the file where the extracted entry should be saved
     * @throws IOException if any error occurred during extraction
     */
    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
            byte[] bytesIn = new byte[BUFFER_SIZE];
            int read = 0;
            while ((read = zipIn.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
        }
    }

    /**
     * Size of the buffer to read/write data
     */
    private static final int BUFFER_SIZE = 4096;

    /** Returns the singleton instance of this class. */
    public static Unzipper instance() {
        return INSTANCE;
    }

    /** The singleton instance of this class. */
    private static final Unzipper INSTANCE = new Unzipper();

    /** Unzips a file contained in the first argument
     * into a directory given as second argument.
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.printf("Usage: Unzipper zipfile");
        } else {
            try {
                System.out.printf("%s unzipped to %s%n", args[0], instance().unzip(args[0]));
            } catch (IOException exc) {
                exc.printStackTrace();
            }
        }
    }
}