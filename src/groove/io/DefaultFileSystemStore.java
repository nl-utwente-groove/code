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

import groove.graph.Graph;
import groove.graph.GraphFactory;
import groove.graph.GraphInfo;
import groove.trans.RuleName;
import groove.trans.SystemProperties;
import groove.util.Groove;
import groove.view.StoredGrammarView;
import groove.view.aspect.AspectGraph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Properties;

/**
 * Implementation based on {@link AspectGraph} representations of the rules and
 * graphs, and using a (default) <code>.gps</code> directory as persistent
 * storage.
 * @author Arend Rensink
 * @version $Revision $
 */
public class DefaultFileSystemStore extends Observable implements SystemStore {
    /**
     * Constructs a store from a given file. The file should be a directory with
     * extension {@link Groove#RULE_SYSTEM_EXTENSION}. The store is writable.
     * @param file source directory of the underlying persistent storage
     * @param layouted if <code>true</code>, the store will load and save graph
     *        layout.
     * @throws IllegalArgumentException if <code>file</code> is not an existing
     *         directory, or does not have the correct extension.
     */
    public DefaultFileSystemStore(File file, boolean layouted)
        throws IllegalArgumentException {
        if (!file.exists()) {
            throw new IllegalArgumentException(String.format(
                "File '%s' does not exist", file));
        }
        if (!file.isDirectory()) {
            throw new IllegalArgumentException(String.format(
                "File '%s' is not a directory", file));
        }
        if (!GRAMMAR_FILTER.accept(file)) {
            throw new IllegalArgumentException(String.format(
                "File '%s' does not refer to a production system", file));
        }
        this.file = file;
        this.name = GRAMMAR_FILTER.stripExtension(this.file.getName());
        this.marshaller =
            createGraphMarshaller(GraphFactory.getInstance(), true);
    }

    /**
     * Constructs a store from a given URL. The URL should specify the
     * <code>file:</code> protocol, and the file should be a directory with
     * extension {@link Groove#RULE_SYSTEM_EXTENSION}. The store is writable.
     * @param location source location of the underlying persistent storage;
     *        should refer to a file.
     * @param layouted if <code>true</code>, the store will load and save graph
     *        layout.
     * @throws IllegalArgumentException if <code>location</code> does not
     *         conform to URI syntax, or does not point to an existing
     *         directory, or does not have the correct extension.
     */
    public DefaultFileSystemStore(URL location, boolean layouted)
        throws IllegalArgumentException {
        this(toFile(location), layouted);
        this.url = location;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public AspectGraph deleteGraph(String name) {
        testInit();
        AspectGraph result = this.graphMap.remove(name);
        this.marshaller.deleteGraph(createGraphFile(name));
        notify(SystemStore.GRAPH_CHANGE);
        return result;
    }

    @Override
    public AspectGraph deleteRule(RuleName name)
        throws UnsupportedOperationException {
        testInit();
        AspectGraph result = this.ruleMap.remove(name);
        this.marshaller.deleteGraph(new File(this.file,
            RULE_FILTER.addExtension(Groove.toString(name.tokens(), "", "",
                Groove.FILE_SEPARATOR))));
        notify(SystemStore.RULE_CHANGE);
        return result;
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
    public String putControl(String name, String control) throws IOException {
        testInit();
        String result = this.controlMap.put(name, control);
        saveControl(name, control);
        notify(SystemStore.CONTROL_CHANGE);
        return result;
    }

    @Override
    public AspectGraph putGraph(AspectGraph graph) throws IOException {
        testInit();
        String name = GraphInfo.getName(graph);
        AspectGraph result = this.graphMap.put(name, graph);
        this.marshaller.marshalGraph(graph, createGraphFile(name));
        notify(SystemStore.GRAPH_CHANGE);
        return result;
    }

    @Override
    public void putProperties(SystemProperties properties) throws IOException {
        testInit();
        this.properties = properties;
        saveProperties();
        notify(SystemStore.PROPERTIES_CHANGE);
    }

    @Override
    public AspectGraph putRule(AspectGraph rule) throws IOException {
        testInit();
        RuleName name = new RuleName(GraphInfo.getName(rule));
        AspectGraph result = this.ruleMap.put(name, rule);
        this.marshaller.marshalGraph(rule, createRuleFile(name));
        notify(SystemStore.RULE_CHANGE);
        return result;
    }

    @Override
    public AspectGraph renameGraph(String oldName, String newName)
        throws IOException {
        testInit();
        AspectGraph graph = this.graphMap.remove(oldName);
        if (graph != null || this.graphMap.containsKey(newName)) {
            this.marshaller.deleteGraph(createGraphFile(oldName));
            GraphInfo.setName(graph, newName);
            this.graphMap.put(newName, graph);
            this.marshaller.marshalGraph(graph, createGraphFile(newName));
        }
        notify(SystemStore.GRAPH_CHANGE);
        return graph;
    }

    @Override
    public AspectGraph renameRule(String oldName, String newName)
        throws IOException {
        testInit();
        RuleName oldRuleName = new RuleName(oldName);
        RuleName newRuleName = new RuleName(newName);
        AspectGraph rule = this.ruleMap.remove(oldRuleName);
        if (rule != null || this.ruleMap.containsKey(newRuleName)) {
            this.marshaller.deleteGraph(createRuleFile(oldRuleName));
            GraphInfo.setName(rule, newName);
            this.ruleMap.put(newRuleName, rule);
            this.marshaller.marshalGraph(rule, createRuleFile(newRuleName));
        }
        notify(SystemStore.RULE_CHANGE);
        return rule;
    }

    @Override
    public void reload() throws IOException {
        loadProperties();
        loadRules();
        loadGraphs();
        loadControls();
        this.initialised = true;
    }

    public StoredGrammarView toGrammarView() {
        if (this.view == null) {
            this.view = new StoredGrammarView(this);
            addObserver(this.view);
        }
        return this.view;
    }

    public String getLocation() {
        if (this.url == null) {
            return this.file.toString();
        } else {
            return this.url.toString();
        }
    }

    public SystemStore save(File file) throws IOException {
        return save(file, this);
    }

    /** This type of system store is modifiable. */
    @Override
    public boolean isModifiable() {
        return true;
    }

    /**
     * Two system stores are considered equal if the locations they load from
     * are equal.
     * @see #getLocation()
     */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof DefaultFileSystemStore)
            && ((DefaultFileSystemStore) obj).getLocation().equals(
                getLocation());
    }

    /**
     * Returns the hash code of this store's location.
     * @see #getLocation()
     */
    @Override
    public int hashCode() {
        return getLocation().hashCode();
    }

    /**
     * Returns a human-readable combination of the name and location of this
     * store.
     * @see #getName()
     * @see #getLocation()
     */
    @Override
    public String toString() {
        String location =
            this.file == null ? getLocation() : this.file.getParent();
        return getName() + " - " + location;
    }

    /** Callback factory method for creating a graph marshaller. */
    private Xml<Graph> createGraphMarshaller(GraphFactory graphFactory,
            boolean layouted) {
        if (layouted) {
            return new LayedOutXml(graphFactory);
        } else {
            return new DefaultGxl(graphFactory);
        }
    }

    /**
     * Loads the named graphs from specified location and returns the
     * corresponding AspectGraphs
     */
    private void loadGraphs() throws IOException {
        this.graphMap.clear();
        File[] files = this.file.listFiles(STATE_FILTER);
        // read in production rules
        for (File file : files) {
            // check for overlapping rule and directory names
            if (!file.isDirectory()) {
                Graph plainGraph = this.marshaller.unmarshalGraph(file);
                String graphName = STATE_FILTER.stripExtension(file.getName());
                /*
                 * For backward compatibility, we set the role and name of the
                 * graph
                 */
                GraphInfo.setRole(plainGraph, Groove.GRAPH_ROLE);
                GraphInfo.setName(plainGraph, graphName);
                AspectGraph graph = AspectGraph.newInstance(plainGraph);
                /* Store the graph */
                Object oldEntry = this.graphMap.put(graphName, graph);
                assert oldEntry == null : String.format(
                    "Duplicate graph name '%s'", graphName);
            }
        }
    }

    /**
     * Loads the named graphs from specified location and returns the
     * corresponding AspectGraphs
     */
    private void loadControls() throws IOException {
        this.controlMap.clear();
        File[] files = this.file.listFiles(CONTROL_FILTER);
        // read in the control files
        for (File file : files) {
            // check for overlapping rule and directory names
            if (!file.isDirectory()) {
                // read the program in as a single string
                BufferedReader reader =
                    new BufferedReader(new FileReader(file));
                StringBuilder program = new StringBuilder();
                String nextLine = reader.readLine();
                while (nextLine != null) {
                    program.append(nextLine);
                    program.append("\n");
                    nextLine = reader.readLine();
                }
                reader.close();
                // insert the string into the control map
                this.controlMap.put(
                    CONTROL_FILTER.stripExtension(file.getName()),
                    program.toString());
            }
        }
    }

    /**
     * Loads the properties file from file (if any), and assigns the properties
     * to {@link #properties}.
     */
    private void loadProperties() throws IOException {
        this.properties = new SystemProperties();
        File propertiesFile =
            new File(this.file,
                PROPERTIES_FILTER.addExtension(Groove.PROPERTY_NAME));
        // backwards compatibility: <grammar name>.properties
        if (!propertiesFile.exists()) {
            propertiesFile =
                new File(this.file, PROPERTIES_FILTER.addExtension(this.name));
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
    private void loadRules() throws IOException {
        this.ruleMap.clear();
        collectRules(this.file, null);
    }

    private void saveControl(String name, String program) throws IOException {
        File controlFile =
            new File(this.file, CONTROL_FILTER.addExtension(name));
        Writer writer = new FileWriter(controlFile);
        writer.write(program);
        writer.close();
    }

    private void saveProperties() throws IOException {
        File propertiesFile =
            new File(this.file,
                PROPERTIES_FILTER.addExtension(Groove.PROPERTY_NAME));
        Writer propertiesWriter = new FileWriter(propertiesFile);
        this.properties.store(propertiesWriter, null);
        propertiesWriter.close();
    }

    /**
     * Auxiliary method to descend recursively into subdirectories and load all
     * rules found there into the rule map
     * @param directory directory to descend into
     * @param rulePath rule name prefix for the current directory (with respect
     *        to the global file)
     * @throws IOException if an error occurs while loading a rule graph
     */
    private void collectRules(File directory, RuleName rulePath)
        throws IOException {
        File[] files = directory.listFiles(RULE_FILTER);
        if (files == null) {
            throw new IOException(LOAD_ERROR + ": no files found at "
                + directory);
        } else {
            // read in production rules
            for (File file : files) {
                String fileName = RULE_FILTER.stripExtension(file.getName());
                PriorityFileName priorityFileName =
                    new PriorityFileName(fileName);
                RuleName ruleName =
                    new RuleName(rulePath, priorityFileName.getActualName());
                // check for overlapping rule and directory names
                if (file.isDirectory()) {
                    if (!file.getName().startsWith(".")) {
                        collectRules(file, ruleName);
                    }
                } else {
                    Graph plainGraph = this.marshaller.unmarshalGraph(file);
                    /*
                     * For backward compatibility, we set the role and name of
                     * the rule graph
                     */
                    GraphInfo.setRole(plainGraph, Groove.RULE_ROLE);
                    GraphInfo.setName(plainGraph, ruleName.text());
                    AspectGraph ruleGraph = AspectGraph.newInstance(plainGraph);
                    /* Store the rule graph */
                    AspectGraph oldRule = this.ruleMap.put(ruleName, ruleGraph);
                    assert oldRule == null : String.format(
                        "Duplicate rule name '%s'", ruleName);
                }
            }
        }
    }

    private void testInit() throws IllegalStateException {
        if (!this.initialised) {
            throw new IllegalStateException(
                "Operation should only be called after initialisation");
        }
    }

    /**
     * Creates a file name from a given graph name. The file name consists of
     * the store location, the name, and the state extension.
     */
    private File createGraphFile(String graphName) {
        return new File(this.file, STATE_FILTER.addExtension(graphName));
    }

    /**
     * Creates a file name from a given rule name. The file name consists of a
     * given parent file, the name, and the state extension.
     */
    private File createRuleFile(RuleName ruleName) {
        return new File(this.file, RULE_FILTER.addExtension(Groove.toString(
            ruleName.tokens(), "", "", Groove.FILE_SEPARATOR)));
    }

    /** Notifies the observers with a given string value. */
    private void notify(String property) {
        setChanged();
        notifyObservers(property);
    }

    /** The name-to-rule map of the source. */
    private final Map<RuleName,AspectGraph> ruleMap =
        new HashMap<RuleName,AspectGraph>();
    /** The name-to-graph map of the source. */
    private final Map<String,AspectGraph> graphMap =
        new HashMap<String,AspectGraph>();
    /** The name-to-control-program map of the source. */
    private final Map<String,String> controlMap = new HashMap<String,String>();
    /** The system properties object of the source. */
    private SystemProperties properties;
    /**
     * The location from which the source is loaded. If <code>null</code>, the
     * location was specified by file rather than url.
     */
    private URL url;
    /** The file obtained from <code>location</code>. */
    private final File file;
    /** Name of the rule system. */
    private final String name;
    /** The graph marshaller used for retrieving rule and graph files. */
    private final Xml<Graph> marshaller;
    /** Flag indicating whether the store has been loaded. */
    private boolean initialised;
    /** The grammar view associated with this store. */
    private StoredGrammarView view;

    /** Saves the content of a given system store to file. */
    static public SystemStore save(File file, SystemStore store)
        throws IOException {
        if (!GRAMMAR_FILTER.accept(file)) {
            throw new IOException(String.format(
                "File '%s' does not refer to a production system", file));
        }
        // if the file already exists, rename it
        // in order to be able to restore if saving fails
        File newFile = null;
        if (file.exists()) {
            newFile = file;
            do {
                newFile =
                    new File(newFile.getParent(), "Copy of "
                        + newFile.getName());
            } while (newFile.exists());
            if (!file.renameTo(newFile)) {
                throw new IOException(String.format(
                    "Can't save grammar to existing file '%s'", file));
            }
        }
        try {
            file.mkdir();
            DefaultFileSystemStore result =
                new DefaultFileSystemStore(file, true);
            result.reload();
            result.putProperties(store.getProperties());
            // save control programs
            for (Map.Entry<String,String> controlEntry : store.getControls().entrySet()) {
                result.putControl(controlEntry.getKey(),
                    controlEntry.getValue());
            }
            // save properties
            result.putProperties(store.getProperties());
            // save graphs
            for (AspectGraph stateGraph : store.getGraphs().values()) {
                result.putGraph(stateGraph);
            }
            // save rules
            for (AspectGraph ruleGraph : store.getRules().values()) {
                result.putRule(ruleGraph);
            }
            if (newFile != null) {
                boolean deleted = deleteRecursive(newFile);
                assert deleted : String.format("Failed to delete '%s'", newFile);
            }
            return result;
        } catch (IOException exc) {
            file.delete();
            // attempt to re-rename previously existing file
            if (newFile != null) {
                newFile.renameTo(file);
            }
            throw exc;
        }
    }

    /**
     * Recursively traverses all subdirectories and deletes all files and
     * directories.
     */
    private static boolean deleteRecursive(File location) {
        if (location.isDirectory()) {
            for (File file : location.listFiles()) {
                if (!deleteRecursive(file)) {
                    return false;
                }
            }
            return location.delete();
        } else {
            location.delete();
            return true;
        }
    }

    /**
     * Returns a file based on a given URL, if there is one such.
     * @return a file based on <code>url</code>; non-null
     * @throws IllegalArgumentException if <code>url</code> does not conform to
     *         URI syntax or does not point to an existing file.
     */
    private static File toFile(URL url) throws IllegalArgumentException {
        try {
            // ignore query and reference part of the URL
            return new File(new URI(url.getProtocol(), url.getAuthority(),
                url.getPath(), null, null));
        } catch (URISyntaxException exc) {
            throw new IllegalArgumentException(String.format(
                "URL '%s' is not formatted correctly: %s", url,
                exc.getMessage()));
        } catch (IllegalArgumentException exc) {
            throw new IllegalArgumentException(String.format(
                "URL '%s' is not a valid file: %s", url, exc.getMessage()));
        }
    }

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

    /** Error message if a grammar cannot be loaded. */
    static private final String LOAD_ERROR = "Can't load graph grammar";
}
