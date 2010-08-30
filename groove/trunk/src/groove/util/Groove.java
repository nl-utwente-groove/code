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

import groove.calc.DefaultGraphCalculator;
import groove.calc.GraphCalculator;
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.graph.GraphShape;
import groove.graph.LabelStore;
import groove.graph.NodeEdgeMap;
import groove.graph.iso.DefaultIsoChecker;
import groove.gui.Exporter;
import groove.gui.Exporter.StructuralFormat;
import groove.io.Aut;
import groove.io.DefaultGxl;
import groove.io.ExtensionFilter;
import groove.io.Xml;
import groove.match.GraphSearchPlanFactory;
import groove.rel.VarNodeEdgeMap;
import groove.trans.GraphGrammar;
import groove.view.FormatException;
import groove.view.StoredGrammarView;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;

/**
 * Globals and convenience methods.
 * @version $Revision $
 * @version Arend Rensink
 */
public class Groove {
    /** The working directory of the application. */
    public static final String WORKING_DIR = System.getProperty("user.dir");
    /** The last accessed working directory. */
    public static String CURRENT_WORKING_DIR = WORKING_DIR;
    /** The user's home directory. */
    public static final String HOME = System.getProperty("user.home");
    /** The system's file separator. */
    public static final String FILE_SEPARATOR =
        System.getProperty("file.separator");
    /** Lower case letter pi. */
    public static final char LC_PI = '\u03C0';
    /** Unicode character for up-triangle. */
    public static final char UP_TRIANGLE = '\u25B3';
    /** Unicode character for down-triangle. */
    public static final char DOWN_TRIANGLE = '\u25BD';
    /** Unicode character for down-triangle. */
    public static final char UP_ARROW = '\u2191';
    /** Unicode character for down-triangle. */
    public static final char DOWN_ARROW = '\u2193';
    /** The default sample directory. */
    public static final String SAMPLE_DIR =
        WORKING_DIR + FILE_SEPARATOR + "samples";

    /** Tests if a given role string equals {@link #GRAPH_ROLE}. */
    static public boolean isGraphRole(String role) {
        return GRAPH_ROLE.equals(role);
    }

    /**
     * Role value indicating that a graph represents an ordinary graph.
     * @see GraphInfo#getRole()
     */
    static public final String GRAPH_ROLE = "graph";

    /** Tests if a given role string equals {@link #RULE_ROLE}. */
    static public boolean isRuleRole(String role) {
        return RULE_ROLE.equals(role);
    }

    /**
     * Role value indicating that a graph represents a rule.
     * @see GraphInfo#getRole()
     */
    static public final String RULE_ROLE = "rule";

    /** Tests if a given role string equals {@link #TYPE_ROLE}. */
    static public boolean isTypeRole(String role) {
        return TYPE_ROLE.equals(role);
    }

    /**
     * Role value indicating that a graph represents a type.
     * @see GraphInfo#getRole()
     */
    static public final String TYPE_ROLE = "type";

    /** Extension for GXL (Graph eXchange Language) files. */
    public static final String GXL_EXTENSION = ".gxl";
    /** Extension for GPR (Graph Production Rule) files. */
    public static final String RULE_EXTENSION = ".gpr";
    /** Extension for GST (Graph STate) files. */
    public static final String STATE_EXTENSION = ".gst";
    /** Extension for GTP (Graph TYpe) files. */
    public static final String TYPE_EXTENSION = ".gty";
    /** Extension for GPS (Graph Production System) files. */
    public static final String RULE_SYSTEM_EXTENSION = ".gps";
    /** Extension for FSM (Finite State Machine) files. */
    public static final String FSM_EXTENSION = ".fsm";
    /** Extension for JPG image files. */
    public static final String JPG_EXTENSION = ".jpg";
    /** Extension for Lisp layout files. */
    public static final String LISP_EXTENSION = ".lsp";
    /** Extension for PNG (Portable Network Graphics) files. */
    public static final String PNG_EXTENSION = ".png";
    /** Extension for EPS (Embedded PostScript) files. */
    public static final String EPS_EXTENSION = ".eps";
    /** Extension for CADP <code>.aut</code> files. */
    public static final String AUT_EXTENSION = ".aut";
    /** Extension for Graph Layout files. */
    public static final String LAYOUT_EXTENSION = ".gl";
    /** Extension for text files. */
    public static final String TEXT_EXTENSION = ".txt";

    /** Extension for KTH <code>.kth</code> files.
     *  Used by the program analysis tool by Marieke et al. 
     */
    public static final String KTH_EXTENSION = ".kth";

    /** Default name for the start graph. */
    public static final String DEFAULT_START_GRAPH_NAME = "start";

    /** Default name for control files. */
    public static final String DEFAULT_CONTROL_NAME = "control";

    /** Default name for the type graph */
    public static final String DEFAULT_TYPE_NAME = "type";
    /** Extension for control files. */
    public static final String CONTROL_EXTENSION = ".gcp";

    /** Default name for property files. */
    public static final String PROPERTY_NAME = "system";

    /** Extension for property files. */
    public static final String PROPERTY_EXTENSION = ".properties";
    /** File name for GUI properties. */
    public static final String GUI_PROPERTIES_FILE =
        "groove.gui" + PROPERTY_EXTENSION;
    /** File name for XML properties. */
    public static final String XML_PROPERTIES_FILE =
        "groove.xml" + PROPERTY_EXTENSION;

    // Icons

    /** Cancel action icon. */
    public static final ImageIcon CANCEL_ICON =
        new ImageIcon(Groove.getResource("cancel-smaller.gif"));
    /** Copy action icon. */
    public static final ImageIcon COPY_ICON =
        new ImageIcon(Groove.getResource("copy.gif"));
    /** Cut action icon. */
    public static final ImageIcon CUT_ICON =
        new ImageIcon(Groove.getResource("cut.gif"));
    /** Delete action icon. */
    public static final ImageIcon DELETE_ICON =
        new ImageIcon(Groove.getResource("delete.gif"));
    /** Disable action icon. */
    public static final ImageIcon DISABLE_ICON =
        new ImageIcon(Groove.getResource("disable-smaller.gif"));
    /** Edit action icon. */
    public static final ImageIcon EDIT_ICON =
        new ImageIcon(Groove.getResource("edit.gif"));
    /** Enable action icon. */
    public static final ImageIcon ENABLE_ICON =
        new ImageIcon(Groove.getResource("enable.gif"));
    /** Icon for a New action. */
    public static final ImageIcon NEW_ICON =
        new ImageIcon(getResource("new.gif"));
    /** Icon for a New Graph action. */
    public static final ImageIcon NEW_GRAPH_ICON =
        new ImageIcon(getResource("new-G.gif"));
    /** Icon for a New Rule action. */
    public static final ImageIcon NEW_RULE_ICON =
        new ImageIcon(getResource("new-R.gif"));
    /** Icon for a New Type action. */
    public static final ImageIcon NEW_TYPE_ICON =
        new ImageIcon(getResource("new-T.gif"));
    /** Rename action icon. */
    public static final ImageIcon RENAME_ICON =
        new ImageIcon(Groove.getResource("rename.gif"));
    /** Redo action icon. */
    public static final ImageIcon REDO_ICON =
        new ImageIcon(Groove.getResource("redo.gif"));
    /** Undo action icon. */
    public static final ImageIcon UNDO_ICON =
        new ImageIcon(Groove.getResource("undo.gif"));
    /** Icon for GPS folders. */
    public static final ImageIcon GPS_FOLDER_ICON =
        new ImageIcon(getResource("gps.gif"));
    /** Graph editing mode icon. */
    public static final ImageIcon GRAPH_MODE_ICON =
        new ImageIcon(getResource("graph-mode.gif"));
    /** Icon for Control Panel. */
    public static final ImageIcon CONTROL_FRAME_ICON =
        new ImageIcon(getResource("cp-frame.gif"));
    /** Icon for Control Files. */
    public static final ImageIcon CONTROL_FILE_ICON =
        new ImageIcon(getResource("control-file.gif"));
    /** Icon for graphs. */
    public static final ImageIcon GRAPH_ICON =
        new ImageIcon(getResource("graph.gif"));
    /** Icon for graph (GXL or GST) files. */
    public static final ImageIcon GRAPH_FILE_ICON =
        new ImageIcon(getResource("graph-file.gif"));
    /** Icon for the state panel of the simulator. */
    public static final ImageIcon GRAPH_FRAME_ICON =
        new ImageIcon(getResource("graph-frame.gif"));
    /** Icon for graph with emphasised match. */
    public static final ImageIcon GRAPH_MATCH_ICON =
        new ImageIcon(getResource("graph-match.gif"));
    /** Small icon for production rules. */
    public static final ImageIcon RULE_SMALL_ICON =
        new ImageIcon(getResource("rule-small.gif"));
    /** Icon for production rules. */
    public static final ImageIcon RULE_ICON =
        new ImageIcon(getResource("rule.gif"));
    /** Icon for rule (GPR) files. */
    public static final ImageIcon RULE_FILE_ICON =
        new ImageIcon(getResource("rule-file.gif"));
    /** Icon for the rule panel of the simulator. */
    public static final ImageIcon RULE_FRAME_ICON =
        new ImageIcon(getResource("rule-frame.gif"));
    /** Rule editing mode icon. */
    public static final ImageIcon RULE_MODE_ICON =
        new ImageIcon(getResource("rule-mode.gif"));
    /** Icon for the LTS panel of the simulator. */
    public static final ImageIcon LTS_FRAME_ICON =
        new ImageIcon(getResource("lts-frame.gif"));
    /** Icon for type (GTY) files. */
    public static final ImageIcon TYPE_FILE_ICON =
        new ImageIcon(getResource("type-file.gif"));
    /** Icon for Type Panel. */
    public static final ImageIcon TYPE_FRAME_ICON =
        new ImageIcon(getResource("type-frame.gif"));
    /** Type editing mode icon. */
    public static final ImageIcon TYPE_MODE_ICON =
        new ImageIcon(getResource("type-mode.gif"));
    /** GROOVE project icon in 16x16 format. */
    public static final ImageIcon GROOVE_ICON_16x16 =
        new ImageIcon(getResource("groove-g-16x16.gif"));
    /** GROOVE project icon in 32x32 format. */
    public static final ImageIcon GROOVE_ICON_32x32 =
        new ImageIcon(getResource("groove-g-32x32.gif"));
    /** GROOVE project icon in blue colour - 32x32 format. */
    public static final ImageIcon GROOVE_BLUE_ICON_32x32 =
        new ImageIcon(getResource("groove-blue-g-32x32.gif"));
    /** Transparent open up-arrow icon. */
    public static final ImageIcon OPEN_UP_ARROW_ICON =
        new ImageIcon(getResource("open-up-arrow.gif"));
    /** Transparent open down-arrow icon. */
    public static final ImageIcon OPEN_DOWN_ARROW_ICON =
        new ImageIcon(getResource("open-down-arrow.gif"));
    /** Paste action icon. */
    public static final ImageIcon PASTE_ICON =
        new ImageIcon(Groove.getResource("paste.gif"));
    /** Special icon denoting choice e/a. */
    public static final ImageIcon E_A_CHOICE_ICON =
        new ImageIcon(getResource("e-a-choice.gif"));
    /** Save action icon. */
    public static final ImageIcon SAVE_ICON =
        new ImageIcon(Groove.getResource("save.gif"));
    /** Save as action icon. */
    public static final ImageIcon SAVE_AS_ICON =
        new ImageIcon(Groove.getResource("saveas.gif"));
    /** Start action icon. */
    public static final ImageIcon START_ICON =
        new ImageIcon(Groove.getResource("start.gif"));
    /** The file containing the configuration for allowed scenarios. */
    public static final String ALLOWED_SCENARIOS_CONFIGURATION_FILE =
        "configuration";

    /**
     * Flag to indicate if various types of statistics should be computed. This
     * flag is intended to be used globally.
     */
    static public final boolean GATHER_STATISTICS = true;

    /**
     * Returns a fresh extension filter for {@link #AUT_EXTENSION}.
     * @see #AUT_EXTENSION
     */
    public static ExtensionFilter createAutFilter() {
        return getFilter("CADP .aut files", AUT_EXTENSION, true);
    }

    /**
     * Returns a fresh extension filer for <tt>CONTROL_EXTENSION</tt>. By
     * default, the filter accepts directories.
     */
    public static ExtensionFilter createControlFilter() {
        return createControlFilter(true);
    }

    /**
     * Returns a fresh extension filer for <tt>CONTROL_EXTENSION</tt>. A switch
     * controls whether the filter accepts directories.
     * @param acceptDirectories if true, the filter accepts directories.
     */
    public static ExtensionFilter createControlFilter(boolean acceptDirectories) {
        return new ExtensionFilter("Groove control files", CONTROL_EXTENSION,
            acceptDirectories);
    }

    /**
     * Returns a fresh extension filter for <tt>GXL_EXTENSION</tt>. By default,
     * the filter accepts directories.
     * @see #GXL_EXTENSION
     */
    public static ExtensionFilter createGxlFilter() {
        return createGxlFilter(true);
    }

    /**
     * Returns a fresh an extension filter for <tt>GXL_EXTENSION</tt>. A switch
     * controls whether the filter accepts directories.
     * @param acceptDirectories if true, the filter accepts directories.
     * @see #GXL_EXTENSION
     */
    public static ExtensionFilter createGxlFilter(boolean acceptDirectories) {
        return getFilter("GXL files", GXL_EXTENSION, acceptDirectories);
    }

    /**
     * Returns a fresh extension filter for <tt>RULE_EXTENSION</tt>. By default,
     * the filter accepts directories.
     * @see #RULE_EXTENSION
     */
    public static ExtensionFilter createRuleFilter() {
        return createRuleFilter(true);
    }

    /**
     * Returns a fresh an extension filter for <tt>RULE_EXTENSION</tt>. A switch
     * controls whether the filter accepts directories.
     * @param acceptDirectories if true, the filter accepts directries.
     * @see #RULE_EXTENSION
     */
    public static ExtensionFilter createRuleFilter(boolean acceptDirectories) {
        return getFilter("Groove production rules", RULE_EXTENSION,
            acceptDirectories);
    }

    /**
     * Returns a fresh extension filter for <tt>RULE_SYSTEM_EXTENSION</tt>. By
     * default, the filter accepts directories.
     * @see #RULE_SYSTEM_EXTENSION
     */
    public static ExtensionFilter createRuleSystemFilter() {
        return createRuleSystemFilter(true);
    }

    /**
     * Returns a fresh an extension filter for <tt>RULE_SYSTEM_EXTENSION</tt>. A
     * switch controls whether the filter accepts directories.
     * @param acceptDirectories if true, the filter accepts directories.
     * @see #RULE_SYSTEM_EXTENSION
     */
    public static ExtensionFilter createRuleSystemFilter(
            boolean acceptDirectories) {
        return getFilter("Groove production systems", RULE_SYSTEM_EXTENSION,
            acceptDirectories);
    }

    /**
     * Returns a fresh extension filter for <tt>FSM_EXTENSION</tt>. By default,
     * the filter accepts directories.
     * @see #FSM_EXTENSION
     */
    public static ExtensionFilter createFsmFilter() {
        return getFilter("FSM layout files", FSM_EXTENSION, true);
    }

    /**
     * Returns a fresh extension filter for <tt>STATE_EXTENSION</tt>. By
     * default, the filter accepts directories.
     * @see #STATE_EXTENSION
     */
    public static ExtensionFilter createStateFilter() {
        return createStateFilter(true);
    }

    /**
     * Returns a fresh an extension filter for <tt>STATE_EXTENSION</tt>. A
     * switch controls whether the filter accepts directories.
     * @param acceptDirectories if true, the filter accepts directories.
     * @see #STATE_EXTENSION
     */
    public static ExtensionFilter createStateFilter(boolean acceptDirectories) {
        return getFilter("Groove state graphs", STATE_EXTENSION,
            acceptDirectories);
    }

    /**
     * Returns a fresh extension filter for {@link #TYPE_EXTENSION}. By default,
     * the filter accepts directories.
     * @see #TYPE_EXTENSION
     */
    public static ExtensionFilter createTypeFilter() {
        return createTypeFilter(true);
    }

    /**
     * Returns a fresh extension filter for {@link #TYPE_EXTENSION}. A switch
     * controls whether the filter accepts directories.
     * @param acceptDirectories if true, the filter accepts directories.
     * @see #TYPE_EXTENSION
     */
    public static ExtensionFilter createTypeFilter(boolean acceptDirectories) {
        return getFilter("Groove type graphs", TYPE_EXTENSION,
            acceptDirectories);
    }

    /**
     * Returns a fresh extension filer for <tt>TEXT_EXTENSION</tt>. By default,
     * the filter accepts directories.
     */
    public static ExtensionFilter createTextFilter() {
        return new ExtensionFilter("Text files", TEXT_EXTENSION);
    }

    /**
     * Returns a fresh extension filter for <tt>PROPERTIES_EXTENSION</tt>. By
     * default, the filter accepts directories.
     * @see #STATE_EXTENSION
     */
    public static ExtensionFilter createPropertyFilter() {
        return new ExtensionFilter("Groove property files", PROPERTY_EXTENSION);
    }

    /**
     * Returns an extension filter with the required properties.
     * @param description general description of the filter
     * @param extension the extension to be filtered
     * @param acceptDirectories flag controlling whether directories should be
     *        accepted by the filter.
     * @return a filter with the required properties
     */
    public static ExtensionFilter getFilter(String description,
            String extension, boolean acceptDirectories) {
        Pair<ExtensionFilter,ExtensionFilter> result =
            extensionFilterMap.get(extension);
        if (result == null) {
            ExtensionFilter first =
                new ExtensionFilter(description, extension, false);
            ExtensionFilter second =
                new ExtensionFilter(description, extension, true);
            result = new Pair<ExtensionFilter,ExtensionFilter>(first, second);
            extensionFilterMap.put(extension, result);
        }
        return acceptDirectories ? result.second() : result.first();
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
     * Attempts to load in a graph from a given <tt>.gst</tt> file and return
     * it. Tries out the <tt>.gxl</tt> and <tt>.gst</tt> extensions if the
     * filename has no extension.
     * @param filename the name of the file to lod the graph from
     * @return the graph contained in <code>filename</code>, or
     *         <code>null</code> if no file with this name can be found
     * @throws IOException if <code>filename</code> does not exist or is wrongly
     *         formatted
     */
    static public Graph loadGraph(String filename) throws IOException {
        // attempt to find the intended file
        File file = new File(filename);
        if (!(createAutFilter().accept(file) || createGxlFilter().accept(file) || createStateFilter().accept(
            file))) {
            file = new File(createGxlFilter().addExtension(filename));
            if (!file.exists()) {
                file = new File(createStateFilter().addExtension(filename));
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
    static public Graph loadGraph(File file) throws IOException {
        Xml<Graph> marshaller;
        if (createAutFilter().accept(file)) {
            marshaller = autGraphLoader;
        } else {
            marshaller = gxlGraphLoader;
        }
        return marshaller.unmarshalGraph(file.toURI().toURL());
    }

    /**
     * Indicates if a given file is a rule file as recognized by the GROOVE
     * system.
     */
    static public boolean isRuleFile(File file) {
        return createRuleFilter().accept(file);
    }

    /**
     * Indicates if a given file is a state file as recognized by the GROOVE
     * system.
     */
    static public boolean isStateURL(URL url) {
        return createStateFilter().hasExtension(url.getFile());
    }

    /**
     * Indicates if a given file is a rule file as recognized by the GROOVE
     * system.
     */
    static public boolean isRuleURL(URL url) {
        return createRuleFilter().hasExtension(url.getFile());
    }

    /**
     * Indicates if a given file is a state file as recognized by the GROOVE
     * system.
     */
    static public boolean isStateFile(File file) {
        return createStateFilter().accept(file);
    }

    /**
     * Attempts to save a graph to a file with a given name. Adds the
     * <tt>.gxl</tt> extension if the file has no extension.
     * @param graph the graph to be saved
     * @param filename the intended filename
     * @throws IOException if saving ran into problems
     */
    static public File saveGraph(Graph graph, String filename)
        throws IOException {
        if (!createStateFilter().hasExtension(filename)) {
            filename = createGxlFilter().addExtension(filename);
        }
        File file = new File(filename);
        // System.err.println("Storing graph as " + file.getAbsolutePath());
        saveGraph(graph, file);
        return file;
    }

    /**
     * Attempts to save a graph to a given file.
     * @param graph the graph to be saved
     * @param file the intended file
     * @throws IOException if saving ran into problems
     */
    static public void saveGraph(Graph graph, File file) throws IOException {
        gxlGraphLoader.marshalGraph(graph, file);
    }

    /**
     * Attempts to export a graph to a file with a given name. The export format
     * is determined by the file extension. Returns a flag indicating if the
     * file could be exported.
     * @param graph the graph to be saved
     * @param filename the intended filename
     * @return <code>true</code> if the format was known
     * @throws IOException if saving ran into problems
     */
    static public boolean exportGraph(GraphShape graph, String filename)
        throws IOException {
        for (StructuralFormat exportFormat : new Exporter().getStructuralFormats()) {
            if (exportFormat.getFilter().hasExtension(filename)) {
                exportFormat.export(graph, new File(filename));
                return true;
            }
        }
        return false;
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
        File dir = new File(createRuleSystemFilter().addExtension(dirname));
        return StoredGrammarView.newInstance(dir, false);
    }

    /**
     * Creates and returns a calculator on the basis of a graph grammar given by
     * a filename.
     * @param filename the name of the file where the grammar is located
     * @return A graph calculator based on the graph grammar found at
     *         <code>filename</code>
     * @throws IOException if no grammar can be found at <code>filename</code>
     */
    static public GraphCalculator createCalculator(String filename)
        throws IOException, FormatException {
        return createCalculator(loadGrammar(filename).toGrammar());
    }

    /**
     * Creates and returns a calculator on the basis of a graph grammar and
     * start graph given by filenames.
     * @param grammarFilename the name of the file where the grammar is located
     * @param startfilename the name of the start graph, interpreted relative to
     *        <code>grammarFilename</code>
     * @return A graph calculator based on the graph grammar found at
     *         <code>grammarFilename</code> and <code>startFilename</code>
     * @throws IOException if no grammar can be found at
     *         <code>grammarFilename</code>
     */
    static public GraphCalculator createCalculator(String grammarFilename,
            String startfilename) throws IOException, FormatException {
        return createCalculator(loadGrammar(grammarFilename, startfilename).toGrammar());
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
        File dir = new File(createRuleSystemFilter().addExtension(dirname));

        return StoredGrammarView.newInstance(dir, startfilename, false);
    }

    /**
     * Returns an iterator over all (non-injective) embeddings of one graph into
     * another. The source graph may contain regular expression edges, as well
     * as variable edges.
     * @param source the graph to be embedded
     * @param target the graph into which it is to be embedded
     * @return an iterator over maps from the source to the target graph.
     * @see #getEmbeddings(GraphShape, GraphShape, LabelStore, boolean)
     */
    static public Iterator<VarNodeEdgeMap> getEmbeddings(GraphShape source,
            GraphShape target) {
        return getEmbeddings(source, target, null);
    }

    /**
     * Returns an iterator over all (non-injective) embeddings of one graph into
     * another. The source graph may contain regular expression edges, as well
     * as variable edges. Label subtyping can be taken into account.
     * @param source the graph to be embedded
     * @param target the graph into which it is to be embedded
     * @param labelStore subtype relation; if <code>null</code>, no subtyping
     *        exists
     * @return an iterator over maps from the source to the target graph.
     * @see #getEmbeddings(GraphShape, GraphShape, LabelStore, boolean)
     */
    static public Iterator<VarNodeEdgeMap> getEmbeddings(GraphShape source,
            GraphShape target, LabelStore labelStore) {
        return getEmbeddings(source, target, labelStore, false);
    }

    /**
     * Returns an iterator over all (injective or non-injective) embeddings of
     * one graph into another. The source graph may contain regular expression
     * edges, as well as variable edges.
     * @param source the graph to be embedded
     * @param target the graph into which it is to be embedded
     * @param injective flag to indicate whether the embeddings should be
     *        injective
     * @return an iterator over maps from the source to the target graph.
     */
    static public Iterator<VarNodeEdgeMap> getEmbeddings(GraphShape source,
            GraphShape target, boolean injective) {
        return getEmbeddings(source, target, null, injective);
    }

    /**
     * Returns an iterator over all (injective or non-injective) embeddings of
     * one graph into another. The source graph may contain regular expression
     * edges, as well as variable edges. Label subtyping can be taken into
     * account.
     * @param source the graph to be embedded
     * @param target the graph into which it is to be embedded
     * @param labelStore subtype relation; if <code>null</code>, no subtyping
     *        exists
     * @param injective flag to indicate whether the embeddings should be
     *        injective
     * @return an iterator over maps from the source to the target graph.
     */
    static public Iterator<VarNodeEdgeMap> getEmbeddings(GraphShape source,
            GraphShape target, LabelStore labelStore, boolean injective) {
        return GraphSearchPlanFactory.getInstance(injective, false).createMatcher(
            source, null, null, labelStore).getMatchIter(target, null);
    }

    /**
     * Constructs an isomorphism between two graphs, in the form of a mapping
     * between their nodes and edges.
     * @param source the first graph to be compared
     * @param target the second graph to be compared
     * @return an isomorphism from <code>source</code> to <code>target</code>,
     *         or <code>null</code> if
     *         {@link DefaultIsoChecker#areIsomorphic(Graph, Graph)} fails.
     */
    static public NodeEdgeMap getIsomorphism(Graph source, Graph target) {
        return DefaultIsoChecker.getInstance(true).getIsomorphism(source,
            target);
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
     * Creates a comparator that corresponds to the alphabetical order of the
     * object's descriptions (according to <tt>toString()</tt>).
     */
    public static <T> java.util.Comparator<T> createAlphaComparator() {
        return new java.util.Comparator<T>() {
            public int compare(T o1, T o2) {
                return o1.toString().compareTo(o2.toString());
            }
        };
    }

    /**
     * Converts a space-separated string value to an <tt>int</tt> array. Returns
     * <tt>null</tt> if the string is <tt>null</tt>, does not decompose into
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
     * Returns <tt>null</tt> if the string is <tt>null</tt>, does not decompose
     * into space-separated sub-strings, or does not convert to <tt>float</tt>
     * values.
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

    /** Properties object for the GUI properties. */
    static public final Properties guiProperties = new Properties();
    /** Properties object for the XML properties. */
    static public final Properties xmlProperties = new Properties();

    /** Loads a properties object from a URL given as a string. */
    static private void loadProperties(Properties properties,
            String propertiesName) {
        try {
            URL propertiesURL = getResource(propertiesName);
            InputStream in = propertiesURL.openStream();
            properties.load(in);
            in.close();
        } catch (IOException e) {
            System.err.println("Could not open properties file: "
                + propertiesName);
        }
    }

    static {
        loadProperties(guiProperties, GUI_PROPERTIES_FILE);
        loadProperties(xmlProperties, XML_PROPERTIES_FILE);
    }

    /**
     * Mapping from extensions to pairs of filters recognising/not recognising
     * directories.
     */
    static private final Map<String,Pair<ExtensionFilter,ExtensionFilter>> extensionFilterMap =
        new HashMap<String,Pair<ExtensionFilter,ExtensionFilter>>();
    /**
     * The fixed GXL graph loader.
     */
    static private final Xml<Graph> gxlGraphLoader = new DefaultGxl();
    /**
     * The fixed AUT graph loader.
     */
    static private final Xml<Graph> autGraphLoader = new Aut();
}
