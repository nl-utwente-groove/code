/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
package groove.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import groove.util.Groove;

/**
 * Useful file system functionalities for performing I/O.
 * Source code adapted from from org.apache.commons.io.FileUtils class.
 */
public class Util {
    /**
     * Copies a whole directory to a new location.
     * <p>
     * This method copies the contents of the specified source directory
     * to within the specified destination directory.
     * <p>
     * The destination directory is created if it does not exist.
     * If the destination directory did exist, then this method merges
     * the source with the destination, with the source taking precedence.
     * <p>
     * <strong>Note:</strong> Setting <code>preserveFileDate</code> to
     * <code>true</code> tries to preserve the files' last modified
     * date/times using {@link File#setLastModified(long)}, however it is
     * not guaranteed that those operations will succeed.
     * If the modification operation fails, no indication is provided.
     *
     * @param srcDir  an existing directory to copy, must not be <code>null</code>
     * @param destDir  the new directory, must not be <code>null</code>
     * @param preserveFileDate  true if the file date of the copy
     *  should be the same as the original
     *
     * @throws NullPointerException if source or destination is <code>null</code>
     * @throws IOException if source or destination is invalid
     * @throws IOException if an IO error occurs during copying
     * @since Commons IO 1.1
     */
    public static void copyDirectory(File srcDir, File destDir, boolean preserveFileDate)
        throws IOException {
        if (srcDir == null) {
            throw new NullPointerException("Source must not be null");
        }
        if (destDir == null) {
            throw new NullPointerException("Destination must not be null");
        }
        if (srcDir.exists() == false) {
            throw new FileNotFoundException("Source '" + srcDir + "' does not exist");
        }
        if (srcDir.isDirectory() == false) {
            throw new IOException("Source '" + srcDir + "' exists but is not a directory");
        }
        if (srcDir.getCanonicalPath()
            .equals(destDir.getCanonicalPath())) {
            throw new IOException(
                "Source '" + srcDir + "' and destination '" + destDir + "' are the same");
        }

        // Cater for destination being directory within the source directory (see IO-141)
        List<String> exclusionList = null;
        if (destDir.getCanonicalPath()
            .startsWith(srcDir.getCanonicalPath())) {
            File[] srcFiles = srcDir.listFiles();
            if (srcFiles != null && srcFiles.length > 0) {
                exclusionList = new ArrayList<>(srcFiles.length);
                for (File srcFile : srcFiles) {
                    File copiedFile = new File(destDir, srcFile.getName());
                    exclusionList.add(copiedFile.getCanonicalPath());
                }
            }
        }
        doCopyDirectory(srcDir, destDir, preserveFileDate, exclusionList);
    }

    /**
     * Internal copy directory method.
     *
     * @param srcDir  the validated source directory, must not be <code>null</code>
     * @param destDir  the validated destination directory, must not be <code>null</code>
     * @param preserveFileDate  whether to preserve the file date
     * @param exclusionList  List of files and directories to exclude from the copy, may be null
     * @throws IOException if an error occurs
     * @since Commons IO 1.1
     */
    private static void doCopyDirectory(File srcDir, File destDir, boolean preserveFileDate,
        List<String> exclusionList) throws IOException {
        // recurse
        File[] files = srcDir.listFiles();
        if (files == null) { // null if security restricted
            throw new IOException("Failed to list contents of " + srcDir);
        }
        if (destDir.exists()) {
            if (destDir.isDirectory() == false) {
                throw new IOException(
                    "Destination '" + destDir + "' exists but is not a directory");
            }
        } else {
            if (destDir.mkdirs() == false) {
                throw new IOException("Destination '" + destDir + "' directory cannot be created");
            }
        }
        if (destDir.canWrite() == false) {
            throw new IOException("Destination '" + destDir + "' cannot be written to");
        }
        for (File file : files) {
            File copiedFile = new File(destDir, file.getName());
            if (exclusionList == null || !exclusionList.contains(file.getCanonicalPath())) {
                if (file.isDirectory()) {
                    doCopyDirectory(file, copiedFile, preserveFileDate, exclusionList);
                } else {
                    doCopyFile(file, copiedFile, preserveFileDate);
                }
            }
        }

        // Do this last, as the above has probably affected directory metadata
        if (preserveFileDate) {
            destDir.setLastModified(srcDir.lastModified());
        }
    }

    /**
     * Internal copy file method.
     *
     * @param srcFile  the validated source file, must not be <code>null</code>
     * @param destFile  the validated destination file, must not be <code>null</code>
     * @param preserveFileDate  whether to preserve the file date
     * @throws IOException if an error occurs
     */
    private static void doCopyFile(File srcFile, File destFile, boolean preserveFileDate)
        throws IOException {
        if (destFile.exists() && destFile.isDirectory()) {
            throw new IOException("Destination '" + destFile + "' exists but is a directory");
        }

        try (FileInputStream fis = new FileInputStream(srcFile);
            FileOutputStream fos = new FileOutputStream(destFile);
            FileChannel input = fis.getChannel();
            FileChannel output = fos.getChannel();) {
            long size = input.size();
            long pos = 0;
            long count = 0;
            while (pos < size) {
                count = (size - pos) > FIFTY_MB ? FIFTY_MB : (size - pos);
                pos += output.transferFrom(input, pos, count);
            }
        }

        if (srcFile.length() != destFile.length()) {
            throw new IOException(
                "Failed to copy full contents from '" + srcFile + "' to '" + destFile + "'");
        }
        if (preserveFileDate) {
            destFile.setLastModified(srcFile.lastModified());
        }
    }

    /**
     * Reads the contents of a file into a String.
     *
     * @param file  the file to read, must not be <code>null</code>
     * @throws IOException in case of an I/O error
     */
    public static String readFileToString(File file) throws IOException {
        StringBuffer fileData = new StringBuffer(1000);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            char[] buf = new char[1024];
            int numRead = 0;
            while ((numRead = reader.read(buf)) != -1) {
                fileData.append(buf, 0, numRead);
            }
        }
        return fileData.toString();
    }

    /**
     * Reads the contents of a input stream into a String.
     *
     * @param in  the stream to read, must not be <code>null</code>
     * @throws IOException in case of an I/O error
     */
    public static String readInputStreamToString(InputStream in) throws IOException {
        StringBuilder result = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String nextLine = reader.readLine();
            while (nextLine != null) {
                result.append(nextLine);
                result.append("\n");
                nextLine = reader.readLine();
            }
        }
        return result.toString();
    }

    /**
     * Constructs a relative path that addresses a given file
     * target from a current directory.
     * @param currentDir Must be absolute
     * @param target Must be absolute
     * @return Relative path from currentDir to target
     */
    public static File getRelativePath(File currentDir, File target) {
        if (currentDir.isFile()) {
            currentDir = new File(currentDir.getParent());
        }
        if (!currentDir.isAbsolute() || !target.isAbsolute()) {
            return null;
        }

        String[] dirParts = currentDir.toString()
            .split("\\Q" + File.pathSeparator + "\\E");
        String[] targetParts = target.toString()
            .split("\\Q" + File.pathSeparator + "\\E");

        int i = 0;
        int max = Math.max(dirParts.length, targetParts.length);
        while (i < max && dirParts[i].equals(targetParts[i])) {
            i++;
        }
        StringBuilder relPath = new StringBuilder();
        int j = i;
        while (j < dirParts.length) {
            relPath.append(".." + File.pathSeparator);
            j++;
        }
        while (i < targetParts.length) {
            relPath.append(targetParts[i]);
            i++;
            if (i < targetParts.length) {
                relPath.append(File.pathSeparator);
            }
        }

        return new File(relPath.toString());
    }

    /**
     * The number of bytes in a 50 MB.
     */
    private static final long FIFTY_MB = 1024 * 1024 * 50;

    /** Reads a CSV file from the resources dir and returns its contents as a String matrix. */
    public final static List<String[]> readCSV(String name, char sep) {
        List<String[]> result = null;
        try (
            CSVReader reader = new CSVReader(new InputStreamReader(Groove.getResource(name + ".csv")
                .openStream()), sep)) {
            result = reader.readAll();
        } catch (IOException e) {
            // no result
        }
        return result;
    }

    /** Unicode hex string for "there exists" (existential quantifier) */
    public final static char EXISTS = '\u2203';
    /** Unicode hex string for "for all" (universal quantifier) */
    public final static char FORALL = '\u2200';
    /** Unicode hex string for "and" (conjunction) */
    public final static char WEDGE = '\u2227';
    /** Unicode hex string for "or" (disjunction) */
    public final static char VEE = '\u2228';
    /** Unicode hex string for "not" (negation) */
    public final static char NEG = '\u00AC';

    /** Lower case letter pi. */
    public static final char LC_PI = '\u03C0';
    /** HTML tau symbol. */
    static public final char LC_TAU = '\u03C4';
    /** HTML epsilon symbol. */
    static public final char LC_EPSILON = '\u03B5';
    /** HTML lambda symbol. */
    static public final char LC_LAMBDA = '\u03BB';

    /** HTML opening << quote symbol. */
    static public final char FRENCH_QUOTES_OPEN = '\u00AB';
    /** HTML closing >> quote symbol. */
    static public final char FRENCH_QUOTES_CLOSED = '\u00BB';
    /** HTML greater than symbol. */
    static public final char GT = '>';
    /** HTML left angular bracket symbol. */
    static public final char LANGLE = '<'; // &#9001;
    /** HTML right angular bracket symbol. */
    static public final char RANGLE = '>'; // &#9002;
    /** HTML times symbol. */
    static public final char TIMES = '\u00D7';
    /** Unicode right-triangle symbol; large/small = ..B6/B8. */
    static public final char RT = '\u25B8';
    /** Unicode left-triangle symbol; large/small = ..C0/C2. */
    static public final char LT = '\u25C2';
    /** Unicode up-triangle symbol; large/small = ..B2/B4. */
    static public final char UT = '\u25B4';
    /** Unicode down-triangle symbol; large/small = ..BC/BE. */
    static public final char DT = '\u25BE';
    /** Unicode right-arrow symbol. */
    static public final char RA = '\u2192';
    /** Unicode left-arrow symbol. */
    static public final char LA = '\u2190';
    /** Unicode up-arrow symbol. */
    static public final char UA = '\u2191';
    /** Unicode down-arrow symbol. */
    static public final char DA = '\u2193';
    /** Unicode up-right-arrow symbol. */
    static public final char URA = '\u2197';
    /** Unicode up-left-arrow symbol. */
    static public final char ULA = '\u2196';
    /** Unicode down-right-arrow symbol. */
    static public final char DRA = '\u2198';
    /** Unicode down-left-arrow symbol. */
    static public final char DLA = '\u2199';
    /** Unicode  circular arrow symbol. */
    static public final char CA = '\u21ba';
    /** Unicode thin space symbol. */
    static public final char THIN_SPACE = '\u2009';
    /** Unicode plusminus symbol. */
    static public final char PLUSMINUS = '\u00b1';
    /** Unicode end-of-text character. */
    static public final char EOT = '\u0003';
}
