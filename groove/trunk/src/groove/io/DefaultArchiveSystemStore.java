/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
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
package groove.io;

import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.graph.Node;
import groove.gui.layout.LayoutMap;
import groove.trans.RuleName;
import groove.trans.SystemProperties;
import groove.util.Groove;
import groove.util.Pair;
import groove.view.FormatException;
import groove.view.StoredGrammarView;
import groove.view.aspect.AspectGraph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Implementation based on {@link AspectGraph} representations of the rules and
 * graphs, and using a (default) <code>.gps</code> directory as persistent
 * storage.
 * @author Arend Rensink
 * @version $Revision $
 */
public class DefaultArchiveSystemStore implements SystemStore {
    /**
     * Constructs a store from a given file. The file should be a JAR or ZIP
     * file containing a single subdirectory with extension
     * {@link Groove#RULE_SYSTEM_EXTENSION}. The store is immutable.
     * @param file source JAR or ZIP file containing the rule system
     * @throws IllegalArgumentException if <code>file</code> is not a correct
     *         archive
     */
    public DefaultArchiveSystemStore(File file) throws IllegalArgumentException {
        if (!file.exists()) {
            throw new IllegalArgumentException(String.format(
                "File '%s' does not exist", file));
        }
        String extendedName;
        if (!file.isDirectory() && JAR_FILTER.accept(file)) {
            extendedName = JAR_FILTER.stripExtension(file.getName());
        } else if (!file.isDirectory() && ZIP_FILTER.accept(file)) {
            extendedName = ZIP_FILTER.stripExtension(file.getName());
        } else {
            throw new IllegalArgumentException(String.format(
                "File '%s' is not a JAR or ZIP file", file));
        }
        this.file = file;
        this.entryName = extendedName;
        this.grammarName = GRAMMAR_FILTER.stripExtension(extendedName);
        this.url = null;
    }

    /**
     * Constructs a store from a given URL. The URL should specify the
     * <code>jar:</code> protocol, and the sub-URL should point to a JAR or ZIP
     * file. The store is immutable.
     * @param location source location of the underlying persistent storage;
     *        should refer to a file.
     * @throws IllegalArgumentException if <code>location</code> does not
     *         specify the correct protocol
     * @throws IOException if the URL has the JAR protocol, but cannot be opened
     */
    public DefaultArchiveSystemStore(URL location)
        throws IllegalArgumentException, IOException {
        if (!location.getProtocol().equals(JAR_PROTOCOL)) {
            throw new IllegalArgumentException(String.format(
                "Protocol '%s' should be '%s'", location.getProtocol(),
                JAR_PROTOCOL));
        }
        this.url = location;
        this.entryName = extractEntryName(location);
        // take the last part of the entry name as grammar name
        File fileFromEntry = new File(this.entryName);
        this.grammarName =
            GRAMMAR_FILTER.stripExtension(fileFromEntry.getName());
        this.file = null;
    }

    /**
     * Extracts the entry name for the grammar from a given JAR or ZIP URL. The
     * name is either given in the JAR entry part of the URL, or it is taken to
     * be the only directory inside the archive file.
     * @param url the URL to be parsed; guaranteed to be a JAR or ZIP
     * @return the name; non-null
     * @throws IllegalArgumentException if no name can be found according to the
     *         above rules
     * @throws IOException if the URL cannot be opened
     */
    private String extractEntryName(URL url) throws IOException {
        String result;
        JarURLConnection connection = (JarURLConnection) url.openConnection();
        if (connection.getEntryName() == null) {
            result =
                ExtensionFilter.getPureName(new File(
                    connection.getJarFileURL().getPath()));
        } else {
            result = connection.getEntryName();
        }
        return result;
        // if (jarEntryName == null) {
        // }
        // // look inside the jar
        // JarFile jarFile = connection.getJarFile();
        // JarEntry jarEntry = jarFile.entries().nextElement();
        // if (jarEntry == null) {
        // throw new IllegalArgumentException("Archive is empty");
        // } else {
        // // take the first part of the entry name as grammar name
        // File fileFromEntry = new File(jarEntry.getName());
        // while (fileFromEntry.getParent() != null) {
        // fileFromEntry = fileFromEntry.getParentFile();
        // }
        // result = fileFromEntry.getName();
        // }
        // } else {
        // // take the last part of the entry name as grammar name
        // result = new File(jarEntryName).getName();
        // }
        // return result;
    }

    @Override
    public String getName() {
        return this.grammarName;
    }

    @Override
    public AspectGraph deleteGraph(String name) {
        throw new UnsupportedOperationException(String.format(
            "Archived grammar '%s' is immutable", getName()));
    }

    @Override
    public AspectGraph deleteRule(RuleName name)
        throws UnsupportedOperationException {
        throw new UnsupportedOperationException(String.format(
            "Archived grammar '%s' is immutable", getName()));
    }

    @Override
    public Map<String,String> getControls() {
        testInit();
        return Collections.unmodifiableMap(this.controlMap);
    }

    @Override
    public Map<String,AspectGraph> getGraphs() {
        testInit();
        return Collections.unmodifiableMap(this.graphMap);
    }

    @Override
    public SystemProperties getProperties() {
        testInit();
        return this.properties;
    }

    @Override
    public Map<RuleName,AspectGraph> getRules() {
        testInit();
        return Collections.unmodifiableMap(this.ruleMap);
    }

    @Override
    public String putControl(String name, String control)
        throws UnsupportedOperationException {
        throw new UnsupportedOperationException(String.format(
            "Archived grammar '%s' is immutable", getName()));
    }

    @Override
    public AspectGraph putGraph(AspectGraph graph)
        throws UnsupportedOperationException {
        throw new UnsupportedOperationException(String.format(
            "Archived grammar '%s' is immutable", getName()));
    }

    @Override
    public void putProperties(SystemProperties properties)
        throws UnsupportedOperationException {
        throw new UnsupportedOperationException(String.format(
            "Archived grammar '%s' is immutable", getName()));
    }

    @Override
    public AspectGraph putRule(AspectGraph rule)
        throws UnsupportedOperationException {
        throw new UnsupportedOperationException(String.format(
            "Archived grammar '%s' is immutable", getName()));
    }

    @Override
    public AspectGraph renameGraph(String oldName, String newName)
        throws UnsupportedOperationException {
        throw new UnsupportedOperationException(String.format(
            "Archived grammar '%s' is immutable", getName()));
    }

    @Override
    public AspectGraph renameRule(String oldName, String newName)
        throws UnsupportedOperationException {
        throw new UnsupportedOperationException(String.format(
            "Archived grammar '%s' is immutable", getName()));
    }

    @Override
    public void reload() throws IOException {
        ZipFile zipFile;
        if (this.file == null) {
            zipFile =
                ((JarURLConnection) this.url.openConnection()).getJarFile();
        } else {
            zipFile = new ZipFile(this.file);
        }
        // collect the relevant entries
        Map<String,ZipEntry> rules = new HashMap<String,ZipEntry>();
        Map<String,ZipEntry> graphs = new HashMap<String,ZipEntry>();
        Map<String,ZipEntry> controls = new HashMap<String,ZipEntry>();
        ZipEntry properties = null;
        for (Enumeration<? extends ZipEntry> entries = zipFile.entries(); entries.hasMoreElements();) {
            ZipEntry entry = entries.nextElement();
            String entryName = entry.getName();
            if (entryName.startsWith(this.entryName)) {
                // strip off prefix + 1 to take case of file separator
                String restName =
                    entryName.substring(this.entryName.length() + 1);
                if (restName.endsWith(PROPERTIES_FILTER.getExtension())) {
                    String propertiesName =
                        PROPERTIES_FILTER.stripExtension(restName);
                    if (propertiesName.equals(Groove.PROPERTY_NAME)) {
                        // preferably take the one with the default name
                        properties = entry;
                    } else if (properties == null
                        && propertiesName.equals(this.grammarName)) {
                        // otherwise, take the one with the grammar name
                        properties = entry;
                    }
                } else if (restName.endsWith(RULE_FILTER.getExtension())) {
                    Object oldEntry = rules.put(restName, entry);
                    assert oldEntry == null : String.format(
                        "Duplicate rule name '%s'", restName);
                } else if (restName.endsWith(STATE_FILTER.getExtension())) {
                    // check if the entry is top level
                    if (new File(restName).getParent() == null) {
                        Object oldEntry = graphs.put(restName, entry);
                        assert oldEntry == null : String.format(
                            "Duplicate graph name '%s'", restName);
                    }
                } else if (restName.endsWith(CONTROL_FILTER.getExtension())) {
                    // check if the entry is top level
                    if (new File(restName).getParent() == null) {
                        Object oldEntry = controls.put(restName, entry);
                        assert oldEntry == null : String.format(
                            "Duplicate control name '%s'", restName);
                    }
                } else if (restName.endsWith(LAYOUT_FILTER.getExtension())) {
                    String objectName = LAYOUT_FILTER.stripExtension(restName);
                    this.layoutEntryMap.put(objectName, entry);
                }
            }
        }
        loadProperties(zipFile, properties);
        loadRules(zipFile, rules);
        loadGraphs(zipFile, graphs);
        loadControls(zipFile, controls);
        zipFile.close();
        this.initialised = true;
    }

    public StoredGrammarView toGrammarView() {
        if (this.view == null) {
            this.view = new StoredGrammarView(this);
        }
        return this.view;
    }

    /**
     * Returns the string representation of the URL or file this store was
     * loaded from.
     */
    public String getLocation() {
        if (this.url == null) {
            return this.file.toString();
        } else {
            return this.url.toString();
        }
    }

    public SystemStore save(File file) throws IOException {
        return DefaultFileSystemStore.save(file, this);
    }

    /** This type of system store is not modifiable. */
    @Override
    public boolean isModifiable() {
        return false;
    }

    /**
     * Loads the named graphs from a zip file, using the prepared map from graph
     * names to zip entries.
     */
    private void loadGraphs(ZipFile file, Map<String,ZipEntry> graphs)
        throws IOException {
        this.graphMap.clear();
        for (Map.Entry<String,ZipEntry> graphEntry : graphs.entrySet()) {
            String graphName = STATE_FILTER.stripExtension(graphEntry.getKey());
            InputStream in = file.getInputStream(graphEntry.getValue());
            Pair<Graph,Map<String,Node>> plainGraphAndMap =
                DefaultGxlIO.getInstance().loadGraphWithMap(in);
            Graph plainGraph = plainGraphAndMap.first();
            /*
             * For backward compatibility, we set the role and name of the graph
             * graph
             */
            GraphInfo.setRole(plainGraph, Groove.GRAPH_ROLE);
            GraphInfo.setName(plainGraph, graphName);
            addLayout(file, graphEntry.getKey(), plainGraph,
                plainGraphAndMap.second());
            AspectGraph graph = AspectGraph.newInstance(plainGraph);
            /* Store the graph */
            this.graphMap.put(graphName, graph);
        }
    }

    /**
     * Loads the named control programs from a zip file, using the prepared map
     * from program names to zip entries.
     */
    private void loadControls(ZipFile file, Map<String,ZipEntry> controls)
        throws IOException {
        this.controlMap.clear();
        for (Map.Entry<String,ZipEntry> controlEntry : controls.entrySet()) {
            String controlName =
                CONTROL_FILTER.stripExtension(controlEntry.getKey());
            InputStream in = file.getInputStream(controlEntry.getValue());
            // read the program in as a single string
            BufferedReader reader =
                new BufferedReader(new InputStreamReader(in));
            StringBuilder program = new StringBuilder();
            String nextLine = reader.readLine();
            while (nextLine != null) {
                program.append(nextLine);
                program.append("\n");
                nextLine = reader.readLine();
            }
            reader.close();
            /* Store the control program */
            this.controlMap.put(controlName, program.toString());
        }
    }

    /**
     * Loads the properties file from file (if any), and assigns the properties
     * to {@link #properties}.
     */
    private void loadProperties(ZipFile file, ZipEntry entry)
        throws IOException {
        this.properties = new SystemProperties();
        File propertiesFile =
            new File(this.file,
                PROPERTIES_FILTER.addExtension(Groove.PROPERTY_NAME));
        // backwards compatibility: <grammar name>.properties
        if (!propertiesFile.exists()) {
            propertiesFile =
                new File(this.file,
                    PROPERTIES_FILTER.addExtension(this.grammarName));
        }
        if (propertiesFile.exists()) {
            Properties grammarProperties = new Properties();
            InputStream s = new FileInputStream(propertiesFile);
            grammarProperties.load(s);
            s.close();
            this.properties.putAll(grammarProperties);
        }
    }

    /**
     * Loads all the rules from the file into {@link #ruleMap}.
     */
    private void loadRules(ZipFile file, Map<String,ZipEntry> rules)
        throws IOException {
        this.ruleMap.clear();
        for (Map.Entry<String,ZipEntry> ruleEntry : rules.entrySet()) {
            RuleName ruleName =
                createRuleName(RULE_FILTER.stripExtension(ruleEntry.getKey()));
            InputStream in = file.getInputStream(ruleEntry.getValue());
            Pair<Graph,Map<String,Node>> plainGraphAndMap =
                DefaultGxlIO.getInstance().loadGraphWithMap(in);
            Graph plainGraph = plainGraphAndMap.first();
            /*
             * For backward compatibility, we set the role and name of the graph
             * graph
             */
            GraphInfo.setRole(plainGraph, Groove.RULE_ROLE);
            GraphInfo.setName(plainGraph, ruleName.toString());
            addLayout(file, ruleEntry.getKey(), plainGraph,
                plainGraphAndMap.second());
            AspectGraph graph = AspectGraph.newInstance(plainGraph);
            /* Store the graph */
            this.ruleMap.put(ruleName, graph);
        }
    }

    /**
     * @param file
     * @param ruleEntryName
     * @param plainGraph
     * @param nodeMap
     * @throws IOException
     */
    private void addLayout(ZipFile file, String ruleEntryName,
            Graph plainGraph, Map<String,Node> nodeMap) throws IOException {
        ZipEntry layoutEntry = this.layoutEntryMap.get(ruleEntryName);
        if (layoutEntry != null) {
            try {
                LayoutMap<Node,Edge> layout =
                    LayoutIO.getInstance().readLayout(nodeMap,
                        file.getInputStream(layoutEntry));
                GraphInfo.setLayoutMap(plainGraph, layout);
            } catch (FormatException exc) {
                GraphInfo.addErrors(plainGraph, exc.getErrors());
            }
        }
    }

    /**
     * Creates a rule name from the remainder of a JAR/ZIP entry name. The
     * '/'-separators are interpreted as sule name hierarchy.
     * @param restName The entry name to convert; non-null
     * @return The resulting rule name; non-null
     */
    private RuleName createRuleName(String restName) {
        StringBuilder result = new StringBuilder();
        File nameAsFile = new File(RULE_FILTER.stripExtension(restName));
        result.append(nameAsFile.getName());
        while (nameAsFile.getParent() != null) {
            nameAsFile = nameAsFile.getParentFile();
            result.insert(0, RuleName.SEPARATOR);
            result.insert(0, nameAsFile.getName());
        }
        return new RuleName(result.toString());
    }

    private void testInit() throws IllegalStateException {
        if (!this.initialised) {
            throw new IllegalStateException(
                "Operation should only be called after initialisation");
        }
    }

    /** The name-to-rule map of the source. */
    private final Map<RuleName,AspectGraph> ruleMap =
        new HashMap<RuleName,AspectGraph>();
    /** The name-to-graph map of the source. */
    private final Map<String,AspectGraph> graphMap =
        new HashMap<String,AspectGraph>();
    /** The name-to-control-program map of the source. */
    private final Map<String,String> controlMap = new HashMap<String,String>();
    /** Map from entry name to layout entry. */
    private final Map<String,ZipEntry> layoutEntryMap =
        new HashMap<String,ZipEntry>();
    /** The system properties object of the source. */
    private SystemProperties properties;
    /**
     * The location from which the source is loaded. If <code>null</code>, the
     * location was specified by file rather than url.
     */
    private final URL url;
    /** The file obtained from <code>location</code>. */
    private final File file;
    /** Name of the jar entry containing the grammar. */
    private final String entryName;
    /** Name of the rule system. */
    private final String grammarName;
    /** Flag indicating whether the store has been loaded. */
    private boolean initialised;
    /** The grammar view associated with this store. */
    private StoredGrammarView view;

    /** Name of the JAR protocol and file extension. */
    static private final String JAR_PROTOCOL = "jar";
    /** Name of the ZIP protocol and file extension. */
    static private final String ZIP_PROTOCOL = "zip";
    /** File filter to accept JAR files. */
    static private final ExtensionFilter JAR_FILTER =
        new ExtensionFilter("." + JAR_PROTOCOL);
    /** File filter to accept ZIP files. */
    static private final ExtensionFilter ZIP_FILTER =
        new ExtensionFilter("." + ZIP_PROTOCOL);
    /** File filter for graph grammars in the GPS format. */
    static private final ExtensionFilter GRAMMAR_FILTER =
        Groove.createRuleSystemFilter();

    /** File filter for transformation rules in the GPR format. */
    static private final ExtensionFilter RULE_FILTER =
        Groove.createRuleFilter();

    /** File filter for state files. */
    static private final ExtensionFilter STATE_FILTER =
        Groove.createStateFilter();

    /** File filter for property files. */
    static private final ExtensionFilter PROPERTIES_FILTER =
        Groove.createPropertyFilter();

    /** File filter for control files. */
    static private final ExtensionFilter CONTROL_FILTER =
        Groove.createControlFilter();

    /** File filter for layout files. */
    static private final ExtensionFilter LAYOUT_FILTER =
        new ExtensionFilter(Groove.LAYOUT_EXTENSION);
}
