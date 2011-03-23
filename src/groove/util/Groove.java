// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/*
 * $Id: Groove.java,v 1.27 2008-03-13 14:52:56 rensink Exp $
 */
package groove.util;

import static groove.io.FileType.GRAMMAR_FILTER;
import static groove.io.FileType.GXL_FILTER;
import static groove.io.FileType.STATE_FILTER;
import groove.graph.DefaultGraph;
import groove.graph.Graph;
import groove.io.FileType;
import groove.io.xml.DefaultGxl;
import groove.view.StoredGrammarView;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.StringTokenizer;

/**
 * Globals and convenience methods.
 * @version $Revision $
 * @version Arend Rensink
 */
public class Groove {
    /** Toggle this flag to shift between native Look'n'Feel and old one. */
    public static boolean USE_NATIVE_LNF = false;

    /** The working directory of the application. */
    public static final String WORKING_DIR = System.getProperty("user.dir");
    /** The last accessed working directory. */
    public static String CURRENT_WORKING_DIR = WORKING_DIR;
    /** The system's file separator. */
    public static final String FILE_SEPARATOR =
        System.getProperty("file.separator");

    /** Lower case letter pi. */
    public static final char LC_PI = '\u03C0';

    /** Default name for the start graph. */
    public static final String DEFAULT_START_GRAPH_NAME = "start";
    /** Default name for control files. */
    public static final String DEFAULT_CONTROL_NAME = "control";
    /** Default name for the type graph */
    public static final String DEFAULT_TYPE_NAME = "type";
    /** Default name for property files. */
    public static final String PROPERTY_NAME = "system";

    /** File name for XML properties. */
    public static final String XML_PROPERTIES_FILE = "groove.xml"
        + FileType.PROPERTY.getExtension();

    /**
     * Flag to indicate if various types of statistics should be computed. This
     * flag is intended to be used globally.
     */
    static public final boolean GATHER_STATISTICS = true;

    /**
     * Attempts to load in a graph from a given <tt>.gst</tt> file and return
     * it. Tries out the <tt>.gxl</tt> and <tt>.gst</tt> extensions if the
     * filename has no extension.
     * @param filename the name of the file to load the graph from
     * @return the graph contained in <code>filename</code>, or
     *         <code>null</code> if no file with this name can be found
     * @throws IOException if <code>filename</code> does not exist or is wrongly
     *         formatted
     */
    static public DefaultGraph loadGraph(String filename) throws IOException {
        // attempt to find the intended file
        File file = new File(filename);
        if (GXL_FILTER.accept(file) || STATE_FILTER.accept(file)) {
            file = new File(GXL_FILTER.addExtension(filename));
            if (!file.exists()) {
                file = new File(STATE_FILTER.addExtension(filename));
            }
        }
        return loadGraph(file);
    }

    /**
     * Attempts to load in a graph from a file.
     * @param file file to load the graph from
     * @return the graph contained in <code>file</code>, or <code>null</code> if
     *         the file does not exist
     * @throws IOException if <code>file</code> cannot be parsed as a graph
     */
    static public DefaultGraph loadGraph(File file) throws IOException {
        return gxlGraphLoader.unmarshalGraph(file.toURI().toURL());
    }

    /**
     * Attempts to save a graph to a file with a given name. Adds the
     * <tt>.gxl</tt> extension if the file has no extension.
     * @param graph the graph to be saved
     * @param filename the intended filename
     * @throws IOException if saving ran into problems
     */
    static public File saveGraph(Graph<?,?> graph, String filename)
        throws IOException {
        if (!STATE_FILTER.hasExtension(filename)) {
            filename = GXL_FILTER.addExtension(filename);
        }
        File file = new File(filename);
        saveGraph(graph, file);
        return file;
    }

    /**
     * Attempts to save a graph to a given file.
     * @param graph the graph to be saved
     * @param file the intended file
     * @throws IOException if saving ran into problems
     */
    static public void saveGraph(Graph<?,?> graph, File file)
        throws IOException {
        gxlGraphLoader.marshalAnyGraph(graph, file);
    }

    /**
     * Attempts to load in a graph grammar from a given <tt>.gps</tt> directory
     * and return it. Adds the <tt>.gps</tt> extension if the directory name has
     * no extension.
     * @param dirname the name of the directory to load the graph grammar from
     * @return the rule system contained in <code>dirname</code>
     * @throws IOException if <code>dirname</code> does not exist or is wrongly
     *         formatted
     */
    static public StoredGrammarView loadGrammar(String dirname)
        throws IOException {
        File dir = new File(GRAMMAR_FILTER.addExtension(dirname));
        return StoredGrammarView.newInstance(dir, false);
    }

    /**
     * Converts a {@link Rectangle2D} to a {@link Rectangle}.
     */
    static public Rectangle toRectangle(Rectangle2D r) {
        if (r != null) {
            return new Rectangle((int) r.getX(), (int) r.getY(),
                (int) r.getWidth(), (int) r.getHeight());
        }
        return null;
    }

    /**
     * Attempts to load in a graph grammar from a given <tt>.gps</tt> directory,
     * with an explicitly given start graph name, and return it. Adds the
     * <tt>.gps</tt> extension if the file has no extension.
     * @param dirname the name of the directory to load the graph grammar from
     * @param startfilename the name of the start graph; if {@code null}, the
     * default start graph is used
     * @return the graph grammar made up by <code>dirname</code> and
     *         <code>startfilename</code>
     * @throws IOException if <code>dirname</code> or <code>startfilename</code>
     *         do not exist or are wrongly formatted
     */
    static public StoredGrammarView loadGrammar(String dirname,
            String startfilename) throws IOException {
        File dir = new File(GRAMMAR_FILTER.addExtension(dirname));
        return StoredGrammarView.newInstance(dir, startfilename, false);
    }

    /**
     * Gives the current time as a number-formatted string with given
     * parameters.
     * @param lossfactor the multiple of milliseconds by which time should be
     *        measured; i.e. a value of 10 means measure by centiseconds, 100
     *        means by deciseconds
     * @param modulo the multiple of the measured time unit (after taking loss
     *        into account) above which time should be cut off
     * @param fraction the fraction of the measured time that should appear
     *        after the decimal point
     */
    public static String currentTime(int lossfactor, int modulo, int fraction) {
        long time = (System.currentTimeMillis() / lossfactor);
        StringBuffer res = new StringBuffer();
        while (modulo > 1) {
            res.insert(0, time > 0 ? "" + time % 10 : "");
            time /= 10;
            fraction /= 10;
            if (fraction == 1) {
                res.insert(0, ".");
            }
            modulo /= 10;
        }
        return res.toString();
    }

    /**
     * Gives the current time as a number-formatted string of the form "ss.cc",
     * where ss are seconds and cc centiseconds.
     */
    public static String currentTime() {
        return currentTime(10, 10000, 100);
    }

    /**
     * Prints a timestamped message.
     */
    public static void message(Object obj) {
        System.out.println(currentTime() + ": " + obj);
    }

    /**
     * Prints a timestamped message regarding the time of starting a given
     * method.
     */
    public static void startMessage(String method) {
        message("Starting " + method);
    }

    /**
     * Prints a timestamped message regarding the time of ending a given method.
     */
    public static void endMessage(String method) {
        message("Ending " + method);
    }

    /**
     * Returns a URL for a given resource name using the class loader.
     */
    public static URL getResource(String name) {
        return Groove.class.getClassLoader().getResource(name);
    }

    /**
     * Converts a space-separated string value to an <tt>int</tt> array. Returns
     * <tt>null</tt> if the string is <tt>null</tt>, does not decompose into
     * space-separated sub-strings, or does not convert to <tt>int</tt> values.
     */
    static public int[] toIntArray(String text) {
        return toIntArray(text, null);
    }

    /**
     * Converts a delimiter-separated string value to an <tt>int</tt> array. Returns
     * <tt>null</tt> if the string is <tt>null</tt>, does not decompose into
     * space-separated sub-strings, or does not convert to <tt>int</tt> values.
     * @param text the text to be decomposed
     * @param delims string consisting of characters that will be considered delimiters.
     * If {@code null}, all whitespace characters are considered delimiters 
     */
    static public int[] toIntArray(String text, String delims) {
        if (text == null) {
            return null;
        }
        try {
            StringTokenizer tokenizer =
                delims == null ? new StringTokenizer(text)
                        : new StringTokenizer(text, delims);
            int[] result = new int[tokenizer.countTokens()];
            int count = 0;
            while (tokenizer.hasMoreTokens()) {
                String nextToken = tokenizer.nextToken();
                result[count] = Integer.parseInt(nextToken);
                count++;
            }
            return result;
        } catch (NumberFormatException exc) {
            return null;
        }
    }

    /**
     * Fills out a string to a given length by padding it with white space on
     * the left or right. Has no effect if the string is already longer than the
     * desired length.
     * @param text the string to be padded
     * @param length the desired length
     * @param right <tt>true</tt> if the space should be added on the right
     * @return A new string, consisting of <tt>text</tt> preceded or followed by
     *         spaces, up to minimum length <tt>length</tt>
     */
    static public String pad(String text, int length, boolean right) {
        StringBuffer result = new StringBuffer(text);
        while (result.length() < length) {
            if (right) {
                result.append(' ');
            } else {
                result.insert(0, ' ');
            }
        }
        return result.toString();
    }

    /**
     * Fills out a string to a given length by padding it with white space on
     * the right. Has no effect if the string is already longer than the desired
     * length.
     * @param text the string to be padded
     * @param length the desired length
     * @return A new string, with <tt>text</tt> as prefix, followed by spaces,
     *         up to minimum length <tt>length</tt>
     */
    static public String pad(String text, int length) {
        return pad(text, length, true);
    }

    /**
     * Start symbol for the string representation of an array.
     * @see #toString(Object[], String, String, String)
     */
    static public final String ARRAY_START = "[";
    /**
     * End symbol for the string representation of an array.
     * @see #toString(Object[], String, String, String)
     */
    static public final String ARRAY_END = "]";
    /**
     * Separator symbol for the string representation of an array.
     * @see #toString(Object[], String, String, String)
     */
    static public final String ARRAY_SEPARATOR = ",";

    /**
     * Converts an array of <code>int</code>s to an array of
     * <code>Integer</code>s.
     */
    static public Integer[] toArray(int[] array) {
        Integer[] result = new Integer[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i];
        }
        return result;
    }

    /**
     * Returns a string representation of a given array, starting with
     * {@value #ARRAY_START}, ending with {@value #ARRAY_END} and with elements
     * separated by {@value #ARRAY_SEPARATOR}.
     */
    static public <T> String toString(T[] array) {
        return toString(array, ARRAY_START, ARRAY_END, ARRAY_SEPARATOR);
    }

    /**
     * Returns a string representation of a given array. The representation is
     * parameterised by start, end, and separator symbols.
     * @param array the array to be converted
     * @param start the start symbol of the resulting text representation
     * @param end the end symbol of the resulting text representation
     * @param separator the symbol separating the elements in the resulting text
     *        representation
     */
    static public <T> String toString(T[] array, String start, String end,
            String separator) {
        return toString(array, start, end, separator, separator);
    }

    /**
     * Returns a string representation of a given array. The representation is
     * parameterised by start, end, and separator symbols, one for the standard
     * separation, and one separating the penultimate and ultimate elements.
     * @param array the array to be converted
     * @param start the start symbol of the resulting text representation
     * @param end the end symbol of the resulting text representation
     * @param separator the symbol separating the elements in the resulting text
     *        representation, except for the last two
     * @param finalSeparator the symbol separating the last two elements in the
     *        resulting text representation
     */
    static public String toString(Object[] array, String start, String end,
            String separator, String finalSeparator) {
        StringBuffer result = new StringBuffer(start);
        for (int i = 0; i < array.length; i++) {
            result.append(array[i]);
            if (i < array.length - 2) {
                result.append(separator);
            } else if (i == array.length - 2) {
                result.append(finalSeparator);
            }
        }
        result.append(end);
        return result.toString();
    }

    /**
     * Converts a File to a URL.
     */
    public static URL toURL(File file) {
        URL url = null;
        try {
            url = file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(String.format(
                "File '%s' cannot be converted to URL", file));
        }
        return url;
    }

    /**
     * Returns the file corresponding to a given URL, if the URL points to a
     * file. Otherwise, returns <code>null</code>. The URL points to a file in
     * two cases:
     * <ul>
     * <li>its protocol is 'file' with undefined authority, query, and fragment
     * components;
     * <li>its protocol is 'jar' with undefined entry, and an inner URL which is
     * a file URL of the first kind.
     * </ul>
     */
    public static File toFile(URL url) {
        if (url.getProtocol().equals("file")) {
            try {
                return new File(url.toURI());
            } catch (URISyntaxException e) {
                return null;
            } catch (IllegalArgumentException e) {
                // possibly thrown by the File constructor
                return null;
            }
        } else if (url.getProtocol().equals("jar")) {
            try {
                URL innerURL =
                    ((JarURLConnection) url.openConnection()).getJarFileURL();
                return toFile(innerURL);
            } catch (IOException exc) {
                return null;
            }
        } else {
            return null;
        }
    }

    /** Prints the own-code part of the stack trace to the given output. 
     * @param allLines if {@code true}, print all lines, otherwise just
     * those that are in own code
     */
    static public void printStackTrace(PrintStream out, boolean allLines) {
        StackTraceElement[] stackTrace = new Exception().getStackTrace();
        String method = stackTrace[1].getMethodName();
        out.printf("%s called from: %n", method);
        for (int myCode = 2; myCode < stackTrace.length; myCode++) {
            if (allLines || stackTrace[myCode].getLineNumber() >= 0) {
                out.printf("  %s%n", stackTrace[myCode]);
            }
        }
    }

    /**
     * The fixed GXL graph loader.
     */
    static private final DefaultGxl gxlGraphLoader = DefaultGxl.getInstance();

    // Platform dependent information.

    /** Detect if we are on Windows.  */
    public static boolean IS_PLATFORM_WINDOWS =
        System.getProperty("os.name").toLowerCase().indexOf("windows") > -1;

    /** Detect if we are on Mac.  */
    public static boolean IS_PLATFORM_MAC =
        System.getProperty("os.name").toLowerCase().indexOf("mac os x") > -1;

    /** Detect if we are on Linux.  */
    public static boolean IS_PLATFORM_LINUX =
        System.getProperty("os.name").toLowerCase().indexOf("linux") > -1;

}
