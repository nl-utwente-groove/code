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
 * $Id: Groove.java,v 1.1.1.2 2007-03-20 10:42:58 kastenberg Exp $
 */
package groove.util;

import groove.calc.DefaultGraphCalculator;
import groove.calc.GraphCalculator;
import groove.graph.Graph;
import groove.io.ExtensionFilter;
import groove.io.GpsGrammar;
import groove.io.UntypedGxl;
import groove.io.Xml;
import groove.lts.DerivedGraphRuleFactory;
import groove.trans.GraphGrammar;
import groove.trans.RuleSystem;
import groove.trans.view.RuleGraph;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;

/**
 * Globals and convenience methods.
 * @version $Revision: 1.1.1.2 $ 
 * @version Arend Rensink
 */
public class Groove {
	/** The working directory of the application. */
    public static final String WORKING_DIR = System.getProperty("user.dir");
	/** The user's home directory. */
    public static final String HOME = System.getProperty("user.home");
    /** The system's file separator. */
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    /** The default sample directory. */
    public static final String SAMPLE_DIR = WORKING_DIR + FILE_SEPARATOR + "samples";

    /** Extension for GXL (Graph eXchange Language) files. */
    public static final String GXL_EXTENSION = ".gxl";
    /** Extension for GPR (Graph Production Rule) files. */
    public static final String RULE_EXTENSION = ".gpr";
    /** Extension for GST (Graph STate) files. */
    public static final String STATE_EXTENSION = ".gst";
    /** Extension for GPS (Graph Production System) files. */
    public static final String RULE_SYSTEM_EXTENSION = ".gps";
    /** Extension for FSM (Finite State Machine) files. */
    public static final String FSM_EXTENSION = ".fsm";
    /** Extension for JPG image files. */
    public static final String JPG_EXTENSION = ".jpg";
    /** Extension for PNG (Portable Network Graphics) files. */
    public static final String PNG_EXTENSION = ".png";
    /** Extension for EPS (Embedded PostScript) files. */
    public static final String EPS_EXTENSION = ".eps";
    /** Extension for Graph Layout files. */
    public static final String LAYOUT_EXTENSION = ".gl";
    /** Extension for property files. */
    public static final String PROPERTY_EXTENSION = ".properties";
    /** File name for GUI properties. */
    public static final String GUI_PROPERTIES_FILE = "groove.gui"+PROPERTY_EXTENSION;
    /** File name for XML properties. */
    public static final String XML_PROPERTIES_FILE = "groove.xml"+PROPERTY_EXTENSION;
    /** Icon for GPS folders. */
    public static final ImageIcon GPS_FOLDER_ICON = new ImageIcon(getResource("gps.gif"));
    /** Small icon for graphs. */
    public static final ImageIcon GRAPH_SMALL_ICON = new ImageIcon(getResource("graph-small.gif"));
    /** Icon for graphs. */
    public static final ImageIcon GRAPH_ICON = new ImageIcon(getResource("graph.gif"));
    /** Icon for graph (GXL or GST) files. */
    public static final ImageIcon GRAPH_FILE_ICON = new ImageIcon(getResource("graph-file.gif"));
    /** Icon for the state panel of the simulator. */
    public static final ImageIcon GRAPH_FRAME_ICON = new ImageIcon(getResource("graph-frame.gif"));
    /** Icon for graph with emphasised match. */
    public static final ImageIcon GRAPH_MATCH_ICON = new ImageIcon(getResource("graph-match.gif"));
    /** Small icon for production rules. */
    public static final ImageIcon RULE_SMALL_ICON = new ImageIcon(getResource("rule-small.gif"));
    /** Icon for production rules. */
    public static final ImageIcon RULE_ICON = new ImageIcon(getResource("rule.gif"));
    /** Icon for rule (GPR) files. */
    public static final ImageIcon RULE_FILE_ICON = new ImageIcon(getResource("rule-file.gif"));
    /** Icon for the rule panel of the simulator. */
    public static final ImageIcon RULE_FRAME_ICON = new ImageIcon(getResource("rule-frame.gif"));
    /** Icon for the LTS panel of the simulator. */
    public static final ImageIcon LTS_FRAME_ICON = new ImageIcon(getResource("lts-frame.gif"));
    /** GROOVE project icon in 16x16 format. */
    public static final ImageIcon GROOVE_ICON_16x16 = new ImageIcon(getResource("groove-g-16x16.gif"));
    /** GROOVE project icon in 32x32 format. */
    public static final ImageIcon GROOVE_ICON_32x32 = new ImageIcon(getResource("groove-g-32x32.gif"));

    /**
     * Flag to indicate if variaous types of statistics should be computed.
     * This flag is intended to be used globally.
     */
    static public final boolean GATHER_STATISTICS = true;

    /**
     * Returns a fresh extension filter for <tt>GXL_EXTENSION</tt>.
     * By default, the filter accepts directories.
     * @see #GXL_EXTENSION
     */
    public static ExtensionFilter createGxlFilter() {
        return new ExtensionFilter("GXL files", GXL_EXTENSION);
    }

    /**
     * Returns a fresh an extension filter for <tt>GXL_EXTENSION</tt>.
     * A switch controls whether the filter accepts directories.
     * @param acceptDirectories if true, the filter accepts directries.
     * @see #GXL_EXTENSION
     */
    public static ExtensionFilter createGxlFilter(boolean acceptDirectories) {
        ExtensionFilter res = createGxlFilter();
        res.setAcceptDirectories(acceptDirectories);
        return res;
    }

    /**
     * Returns a fresh extension filter for <tt>RULE_EXTENSION</tt>.
     * By default, the filter accepts directories.
     * @see #RULE_EXTENSION
     */
    public static ExtensionFilter createRuleFilter() {
        return new ExtensionFilter("Groove production rules", RULE_EXTENSION);
    }

    /**
     * Returns a fresh an extension filter for <tt>RULE_EXTENSION</tt>.
     * A switch controls whether the filter accepts directories.
     * @param acceptDirectories if true, the filter accepts directries.
     * @see #RULE_EXTENSION
     */
    public static ExtensionFilter createRuleFilter(boolean acceptDirectories) {
        ExtensionFilter res = createRuleFilter();
        res.setAcceptDirectories(acceptDirectories);
        return res;
    }

    /**
     * Returns a fresh extension filter for <tt>RULE_SYSTEM_EXTENSION</tt>.
     * By default, the filter accepts directories.
     * @see #RULE_SYSTEM_EXTENSION
     */
    public static ExtensionFilter createRuleSystemFilter() {
        return new ExtensionFilter("Groove production systems", RULE_SYSTEM_EXTENSION);
    }

    /**
     * Returns a fresh an extension filter for <tt>RULE_SYSTEM_EXTENSION</tt>.
     * A switch controls whether the filter accepts directories.
     * @param acceptDirectories if true, the filter accepts directries.
     * @see #RULE_SYSTEM_EXTENSION
     */
    public static ExtensionFilter createRuleSystemFilter(boolean acceptDirectories) {
        ExtensionFilter res = createRuleSystemFilter();
        res.setAcceptDirectories(acceptDirectories);
        return res;
    }

    /**
     * Returns a fresh extension filter for <tt>FSM_EXTENSION</tt>.
     * By default, the filter accepts directories.
     * @see #FSM_EXTENSION
     */
    public static ExtensionFilter createFsmFilter() {
        return new ExtensionFilter("FSM layout files", FSM_EXTENSION);
    }

    /**
     * Returns a fresh extension filter for <tt>STATE_EXTENSION</tt>.
     * By default, the filter accepts directories.
     * @see #STATE_EXTENSION
     */
    public static ExtensionFilter createStateFilter() {
        return new ExtensionFilter("Groove state graphs", STATE_EXTENSION);
    }

    /**
     * Returns a fresh extension filter for <tt>PROPERTIES_EXTENSION</tt>.
     * By default, the filter accepts directories.
     * @see #STATE_EXTENSION
     */
    public static ExtensionFilter createPropertyFilter() {
        return new ExtensionFilter("Groove property files", PROPERTY_EXTENSION);
    }

    /**
     * Returns a fresh an extension filter for <tt>STATE_EXTENSION</tt>.
     * A switch controls whether the filter accepts directories.
     * @param acceptDirectories if true, the filter accepts directries.
     * @see #STATE_EXTENSION
     */
    public static ExtensionFilter createStateFilter(boolean acceptDirectories) {
        ExtensionFilter res = createStateFilter();
        res.setAcceptDirectories(acceptDirectories);
        return res;
    }

    /**
     * Retrieves a property from the gui properties file
     * @param key the property description
     * @return the value associated with <tt>key</tt> in the gui properties file
     */
    static public String getGUIProperty(String key) {
        return guiProperties.getProperty(key);
    }

    /**
     * Retrieves a property from the xml properties file
     * @param key the property description
     * @return the value associated with <tt>key</tt> in the xml properties file
     */
    static public String getXMLProperty(String key) {
        return xmlProperties.getProperty(key);
    }

    /**
     * Attempts to load in a graph from a given <tt>.gst</tt> file and return it.
     * Tries out the <tt>.gxl</tt> and <tt>.gst</tt> extensions if the filename has no extension.
     * @param filename the name of the file to lod the graph from
     * @return the graph contained in <code>filename</code>, or <code>null</code> if
     * no file with this name can be found
     * @throws IOException if <code>filename</code> does not exist or is wrongly formatted
     */
    static public Graph loadGraph(String filename) throws IOException {
        // attempt to find the intended file
        File file = new File(createGxlFilter().addExtension(filename));
        if (!file.exists()) {
            file = new File(createStateFilter().addExtension(filename));
        }
        return loadGraph(file);
    }

    /**
     * Attempts to load in a graph from a file.
     * @param file file to load the graph from
     * @return the graph contained in <code>file</code>, or <code>null</code> if
     * the file does not exist
     * @throws IOException if <code>file</code> cannot be parsed as a graph
     */
    static public Graph loadGraph(File file) throws IOException {
        if (file.exists()) {
            return graphLoader.unmarshal(file);
        } else {
        	return null;
        }
    }
    
    /**
     * Attempts to save a graph to a file with a given name.
     * Adds the <tt>.gxl</tt> extension if the file has no extension.
     * @param graph the graph to be saved
     * @param filename the intended filename
     * @throws IOException if saving ran into problems
     */
    static public void saveGraph(Graph graph, String filename) throws IOException {
        File file = new File(createGxlFilter().addExtension(filename));
        graphLoader.marshal(graph, file);
    }
    
    /**
     * Attempts to load in a rule graph from a given <tt>.gpr</tt> file and return it.
     * Adds the <tt>.gpr</tt> extension if the filename has no extension.
     * @param filename the name of the file to load the rule graph from
     * @return the rule graph contained in <code>filename</code>
     * @throws IOException if <code>filename</code> does not exist or is wrongly formatted
     */
    static public RuleGraph loadRuleGraph(String filename) throws IOException {
        File file = new File(createRuleFilter().addExtension(filename));
        return gpsLoader.unmarshalRule(file);
    }
    
    /**
     * Attempts to load in a rule system from a given <tt>.gps</tt> directory and return it.
     * Adds the <tt>.gps</tt> extension if the directory name has no extension.
     * @param dirname the name of the directory to load the rule system from
     * @return the rule system contained in <code>dirname</code>
     * @throws IOException if <code>dirname</code> does not exist or is wrongly formatted
     */
    static public RuleSystem loadRuleSystem(String dirname) throws IOException {
        File dir = new File(createRuleSystemFilter().addExtension(dirname));
        return gpsLoader.unmarshal(dir);
    }
    
    /**
     * Attempts to save a rule system to a directory with a given name.
     * Adds the <tt>.gps</tt> extension if the directory name has no extension.
     * @param rules the rule rystem to be saved
     * @param dirname the intended directory name
     * @throws IOException if saving ran into problems
     */
    static public void saveRuleSystem(RuleSystem rules, String dirname) throws IOException {
        File file = new File(createRuleSystemFilter().addExtension(dirname));
        gpsLoader.marshalRules(rules, file);
    }
    
    /**
     * Attempts to load in a graph grammar from a given <tt>.gps</tt> directory and return it.
     * Adds the <tt>.gps</tt> extension if the directory name has no extension.
     * @param dirname the name of the directory to load the graph grammar from
     * @return the rule system contained in <code>dirname</code>
     * @throws IOException if <code>dirname</code> does not exist or is wrongly formatted
     */
    static public GraphGrammar loadGrammar(String dirname) throws IOException {
        File dir = new File(createRuleSystemFilter().addExtension(dirname));
        return gpsLoader.unmarshal(dir);
    }
    
    /**
     * Creates and returns a calculator on the basis of a graph grammar given by
     * a filename.
     * @param filename the name of the file where the grammar is located
     * @return A graph calculator based on the graph grammar found at <code>filename</code>
     * @throws IOException if no grammar can be found at <code>filename</code>
     */
    static public GraphCalculator createCalculator(String filename) throws IOException {
        return createCalculator(loadGrammar(filename));
    }
    
    /**
     * Creates and returns a calculator on the basis of a graph grammar and start graph given by
     * filenames.
     * @param grammarFilename the name of the file where the grammar is located
     * @param startfilename the name of the start graph, interpreted relative to <code>grammarFilename</code>
     * @return A graph calculator based on the graph grammar found at <code>grammarFilename</code> and <code>startFilename</code>
     * @throws IOException if no grammar can be found at <code>grammarFilename</code>
     */
    static public GraphCalculator createCalculator(String grammarFilename, String startfilename) throws IOException {
        return createCalculator(loadGrammar(grammarFilename, startfilename));
    }
    
    /**
     * Creates a new graph calculator based on a given graph grammar.
     * @param grammar the graph grammar to be used as the basis
     * @return a calculator based on <code>grammar</code>
     */
    static public GraphCalculator createCalculator(GraphGrammar grammar) {
        return new DefaultGraphCalculator(grammar);
    }
    
    /**
     * Attempts to load in a graph grammar from a given <tt>.gps</tt> directory,
     * with an explicitly given start graph name, and return it.
     * Adds the <tt>.gps</tt> extension if the file has no extension.
     * @param dirname the name of the directory to load the graph grammar from
     * @param startfilename the name of the start graph
     * @return the graph grammar made up by <code>dirname</code> and <code>startfilename</code>
     * @throws IOException if <code>dirname</code> or <code>startfilename</code> do not exist or are wrongly formatted
     */
    static public GraphGrammar loadGrammar(String dirname, String startfilename) throws IOException {
        File dir = new File(createRuleSystemFilter().addExtension(dirname));
        return gpsLoader.unmarshal(dir, startfilename);
    }

    /**
     * Gives the current time as a number-formatted string with given parameters.
     * @param lossfactor the multiple of milliseconds by which time should be measured;
     * i.e. a value of 10 means measure by centiseconds, 100 means by deciseconds
     * @param modulo the multiple of the measured time unit (after taking loss into 
     * account) above which time should be cut off
     * @param fraction the fraction of the measured time that should appear 
     * after the decimal point
     */
    public static String currentTime(int lossfactor, int modulo, int fraction) {
        long time = (System.currentTimeMillis() / lossfactor);
        StringBuffer res = new StringBuffer();
        while (modulo > 1) {
            res.insert(0, time > 0 ? "" + time % 10 : "");
            time /= 10;
            fraction /= 10;
            if (fraction == 1)
                res.insert(0, ".");
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
     * Prints a timestamped message regarding the time of starting a given method.
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
     * Creates a comparator that corresponds to the alphabetical order
     * of the object's descriptions (according to <tt>toString()</tt>).
     */
    public static <T >java.util.Comparator<T> createAlphaComparator() {
        return new java.util.Comparator<T>() {
            public int compare(T o1, T o2) {
                return o1.toString().compareTo(o2.toString());
            }
        };
    }

    /**
     * Converts a space-separated string value to an <tt>int</tt> array.
     * Returns <tt>null</tt> if the string is <tt>null</tt>, does not decompose into 
     * space-separated sub-strings, or does not convert to <tt>int</tt> values.
     */
    static public int[] toIntArray(String text) {
        if (text == null) {
            return null;
        }
        try {
            StringTokenizer tokenizer = new StringTokenizer(text);
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
     * Converts a space-separated string value to a <tt>float</tt> array.
     * Returns <tt>null</tt> if the string is <tt>null</tt>, does not decompose into 
     * space-separated sub-strings, or does not convert to <tt>float</tt> values.
     */
    static public float[] toFloatArray(String text) {
        if (text == null) {
            return null;
        }
        try {
            StringTokenizer tokenizer = new StringTokenizer(text);
            float[] result = new float[tokenizer.countTokens()];
            int count = 0;
            while (tokenizer.hasMoreTokens()) {
                String nextToken = tokenizer.nextToken();
                result[count] = Float.parseFloat(nextToken);
                count++;
            }
            return result;
        } catch (NumberFormatException exc) {
            return null;
        }
    }

    /**
     * Fills out a string to a given length by padding it with white space on the left or right.
     * Has no effect if the string is already longer than the desired length.
     * @param text the string to be padded
     * @param length the desired length
     * @param right <tt>true</tt> if the space should be added on the right
     * @return A new string, consisting of <tt>text</tt> preceded or followed by spaces, up to minimum length <tt>length</tt>
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
     * Fills out a string to a given length by padding it with white space on the right.
     * Has no effect if the string is already longer than the desired length.
     * @param text the string to be padded
     * @param length the desired length
     * @return A new string, with <tt>text</tt> as prefix, followed by spaces, up to minimum length <tt>length</tt>
     */
    static public String pad(String text, int length) {
        return pad(text, length, true);
    }
    
    /**
     * Converts a {@link Rectangle2D} to a {@link Rectangle}.
     */
    static public Rectangle toRectangle(Rectangle2D r) {
    	if (r != null)
    		return new Rectangle((int) r.getX(), (int) r.getY(), (int) r.getWidth(), (int) r.getHeight());
    	return null;
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
     * Returns a string representation of a given array, starting
     * with {@link #ARRAY_START}, ending with {@link #ARRAY_END} and
     * with elements separated by {@link #ARRAY_SEPARATOR}.
     */
    static public String toString(Object[] array) {
    	return toString(array, ARRAY_START, ARRAY_END, ARRAY_SEPARATOR);
    }

    /**
     * Returns a string representation of a given array. The representation
     * is parameterised by start, end, and separator symbols.
     * @param array the array to be converted
     * @param start the start symbol of the resulting text representation
     * @param end the end symbol of the resulting text representation
     * @param separator the symbol separating the elements in the resulting text representation
     */
    static public String toString(Object[] array, String start, String end, String separator) {
    	return toString(array, start, end, separator, separator);
    }
    
    /**
     * Returns a string representation of a given array. The representation
     * is parameterised by start, end, and separator symbols, one for the
     * standard separation, and one separating the penultimate and ultimate elements.
     * @param array the array to be converted
     * @param start the start symbol of the resulting text representation
     * @param end the end symbol of the resulting text representation
     * @param separator the symbol separating the elements in the resulting text representation,
     * except for the last two
     * @param finalSeparator the symbol separating the last two elements in the resulting text representation
     */
    static public String toString(Object[] array, String start, String end, String separator, String finalSeparator) {
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

    /** Properties object for the GUI properties. */
    static public final Properties guiProperties = new Properties();
    /** Properties object for the XML properties. */
    static public final Properties xmlProperties = new Properties();

    /** Loads a properties object fomr a URL given as a string. */
    static private void loadProperties(Properties properties, String propertiesName) {
        try {
            URL propertiesURL = getResource(propertiesName);
            properties.load(propertiesURL.openStream());
        } catch (IOException e) {
            System.err.println("Could not open properties file: " + propertiesName);
        }
    }

    static {
        loadProperties(guiProperties, GUI_PROPERTIES_FILE);
        loadProperties(xmlProperties, XML_PROPERTIES_FILE);
    }
    
    /**
     * The fixed graph loader.
     */
    static private final Xml graphLoader = new UntypedGxl();
    /**
     * The fixed grammar loader.
     */
    static private final GpsGrammar gpsLoader = new GpsGrammar(graphLoader, DerivedGraphRuleFactory.getInstance());
}