/*
t * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
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

import groove.graph.DefaultGraph;
import groove.graph.GraphInfo;
import groove.graph.GraphRole;
import groove.graph.TypeLabel;
import groove.gui.Options;
import groove.trans.RuleName;
import groove.trans.SystemProperties;
import groove.util.Groove;
import groove.view.FormatException;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Properties;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import javax.swing.undo.UndoableEditSupport;

/**
 * Implementation based on {@link AspectGraph} representations of the rules and
 * graphs, and using a (default) <code>.gps</code> directory as persistent
 * storage.
 * @author Arend Rensink
 * @version $Revision $
 */
public class DefaultFileSystemStore extends UndoableEditSupport implements
        SystemStore {
    /**
     * Constructs a store from a given file. The file should be a directory with
     * extension {@link Groove#RULE_SYSTEM_EXTENSION}. The store is writable.
     * @param file source directory of the underlying persistent storage
     * @param create if <code>true</code> and <code>file</code> does not yet
     *        exist, attempt to create it.
     * @throws IllegalArgumentException if <code>file</code> is not an existing
     *         directory, or does not have the correct extension.
     */
    public DefaultFileSystemStore(File file, boolean create)
        throws IllegalArgumentException {
        if (!file.exists()) {
            if (create) {
                if (!file.mkdirs()) {
                    throw new IllegalArgumentException(String.format(
                        "Could not create directory '%s'", file));
                }
            } else {
                throw new IllegalArgumentException(String.format(
                    "File '%s' does not exist", file));
            }
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
        this.marshaller = createGraphMarshaller(true);
        if (create) {
            this.createVersionProperties();
        }
    }

    /**
     * Constructs a store from a given URL. The URL should specify the
     * <code>file:</code> protocol, and the file should be a directory with
     * extension {@link Groove#RULE_SYSTEM_EXTENSION}. The store is writable.
     * @param location source location of the underlying persistent storage;
     *        should refer to a file.
     * @throws IllegalArgumentException if <code>location</code> does not
     *         conform to URI syntax, or does not point to an existing
     *         directory, or does not have the correct extension.
     */
    public DefaultFileSystemStore(URL location) throws IllegalArgumentException {
        this(toFile(location), false);
        this.url = location;
    }

    private void createVersionProperties() {
        SystemProperties prop = new SystemProperties();
        prop.setCurrentVersionProperties();
        try {
            this.saveProperties(prop);
        } catch (IOException e) {
            throw new IllegalArgumentException(
                "Could not create properties file.");
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String deleteControl(String name) {
        String result = null;
        DeleteControlEdit deleteEdit = doDeleteControl(name);
        if (deleteEdit != null) {
            deleteEdit.checkAndSetVersion();
            postEdit(deleteEdit);
            result = deleteEdit.getControl();
        }
        return result;
    }

    /**
     * Implements the functionality of the {@link #deleteControl(String)}
     * method. Returns a corresponding undoable edit.
     */
    private DeleteControlEdit doDeleteControl(String name) {
        DeleteControlEdit result = null;
        testInit();
        String control = this.controlMap.remove(name);
        if (control != null) {
            createControlFile(name).delete();
            // change the control-related system properties, if necessary
            SystemProperties oldProps = null;
            SystemProperties newProps = null;
            if (name.equals(getProperties().getControlName())) {
                oldProps = getProperties();
                newProps = getProperties().clone();
                newProps.setUseControl(false);
                newProps.setControlName("");
            }
            result = new DeleteControlEdit(name, control, oldProps, newProps);
        }
        return result;
    }

    @Override
    public AspectGraph deleteGraph(String name) {
        AspectGraph result = null;
        DeleteGraphEdit edit = doDeleteGraph(name);
        if (edit != null) {
            edit.checkAndSetVersion();
            postEdit(edit);
            result = edit.getGraph();
        }
        return result;
    }

    /**
     * Implements the functionality of the {@link #deleteGraph(String)} method.
     * Returns a corresponding undoable edit.
     */
    private DeleteGraphEdit doDeleteGraph(String name) {
        DeleteGraphEdit result = null;
        testInit();
        AspectGraph graph = this.graphMap.remove(name);
        if (graph != null) {
            this.marshaller.deleteGraph(createGraphFile(name));
            result = new DeleteGraphEdit(graph);
        }
        return result;
    }

    @Override
    public AspectGraph deleteRule(RuleName name) {
        AspectGraph result = null;
        DeleteRuleEdit edit = doDeleteRule(name);
        if (edit != null) {
            edit.checkAndSetVersion();
            postEdit(edit);
            result = edit.getRule();
        }
        return result;
    }

    /**
     * Implements the functionality of the {@link #deleteRule(RuleName)} method.
     * Returns a corresponding undoable edit.
     */
    private DeleteRuleEdit doDeleteRule(RuleName name) {
        DeleteRuleEdit result = null;
        testInit();
        AspectGraph rule = this.ruleMap.remove(name);
        if (rule != null) {
            this.marshaller.deleteGraph(new File(this.file,
                RULE_FILTER.addExtension(Groove.toString(name.tokens(), "", "",
                    Groove.FILE_SEPARATOR))));
            result = new DeleteRuleEdit(rule);
        }
        return result;
    }

    @Override
    public AspectGraph deleteType(String name) {
        AspectGraph result = null;
        DeleteTypeEdit edit = doDeleteType(name);
        if (edit != null) {
            edit.checkAndSetVersion();
            postEdit(edit);
            result = edit.getType();
        }
        return result;
    }

    /**
     * Implements the functionality of the {@link #deleteType(String)} method.
     * Returns a corresponding undoable edit.
     */
    private DeleteTypeEdit doDeleteType(String name) {
        DeleteTypeEdit result = null;
        testInit();
        AspectGraph type = this.typeMap.remove(name);
        if (type != null) {
            this.marshaller.deleteGraph(createTypeFile(name));
            // change the type-related system properties, if necessary
            SystemProperties oldProps = null;
            SystemProperties newProps = null;
            ArrayList<String> types = getProperties().getTypeNames();
            if (types.remove(name)) {
                oldProps = getProperties();
                newProps = getProperties().clone();
                newProps.setTypeNames(types);
            }
            result = new DeleteTypeEdit(type, oldProps, newProps);
        }
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
        SystemProperties properties = null;
        if (!this.initialised) {
            try {
                properties = this.loadGrammarProperties();
            } catch (IOException e) {
                // Should not happen...
            }
        } else {
            properties = this.properties;
        }
        return properties;
    }

    @Override
    public Map<RuleName,AspectGraph> getRules() {
        testInit();
        return Collections.unmodifiableMap(this.ruleMap);
    }

    @Override
    public Map<String,AspectGraph> getTypes() {
        testInit();
        return Collections.unmodifiableMap(this.typeMap);
    }

    @Override
    public String putControl(String name, String control) throws IOException {
        String result = null;
        PutControlEdit edit = doPutControl(name, control);
        if (edit != null) {
            edit.checkAndSetVersion();
            postEdit(edit);
            result = edit.getOldControl();
        }
        return result;
    }

    /**
     * Implements the functionality of {@link #putControl(String, String)}.
     * Returns an undoable edit wrapping this functionality.
     */
    private PutControlEdit doPutControl(String name, String control)
        throws IOException {
        testInit();
        saveControl(name, control);
        String oldControl = this.controlMap.put(name, control);
        return new PutControlEdit(name, oldControl, control);
    }

    @Override
    public AspectGraph putGraph(AspectGraph graph) throws IOException {
        AspectGraph result = null;
        PutGraphEdit edit = doPutGraph(graph);
        if (edit != null) {
            edit.checkAndSetVersion();
            postEdit(edit);
            result = edit.getOldGraph();
        }
        return result;
    }

    /**
     * Implements the functionality of {@link #putGraph(AspectGraph)}. Returns
     * an undoable edit wrapping this functionality.
     */
    private PutGraphEdit doPutGraph(AspectGraph graph) throws IOException {
        testInit();
        String name = GraphInfo.getName(graph);
        this.marshaller.marshalGraph(graph.toPlainGraph(),
            createGraphFile(name));
        AspectGraph oldGraph = this.graphMap.put(name, graph);
        return new PutGraphEdit(oldGraph, graph);
    }

    @Override
    public void putProperties(SystemProperties properties) throws IOException {
        PutPropertiesEdit edit = doPutProperties(properties);
        if (edit != null) {
            edit.checkAndSetVersion();
            postEdit(edit);
        }
    }

    /**
     * Implements the functionality of {@link #putProperties(SystemProperties)}.
     * Returns an undoable edit wrapping this functionality.
     */
    private PutPropertiesEdit doPutProperties(SystemProperties properties)
        throws IOException {
        testInit();
        SystemProperties oldProperties = this.properties;
        this.properties = properties;
        saveProperties();
        return new PutPropertiesEdit(oldProperties, properties);
    }

    @Override
    public AspectGraph putRule(AspectGraph rule) throws IOException {
        AspectGraph result = null;
        PutRuleEdit edit = doPutRule(rule);
        if (edit != null) {
            edit.checkAndSetVersion();
            postEdit(edit);
            result = edit.getOldRule();
        }
        return result;
    }

    /**
     * Implements the functionality of {@link #putRule(AspectGraph)}. Returns an
     * undoable edit wrapping this functionality.
     */
    private PutRuleEdit doPutRule(AspectGraph rule) throws IOException {
        testInit();
        RuleName name = new RuleName(GraphInfo.getName(rule));
        this.marshaller.marshalGraph(rule.toPlainGraph(), createRuleFile(name));
        AspectGraph oldRule = this.ruleMap.put(name, rule);
        return new PutRuleEdit(oldRule, rule);
    }

    @Override
    public AspectGraph putType(AspectGraph type) throws IOException {
        AspectGraph result = null;
        PutTypeEdit edit = doPutType(type);
        if (edit != null) {
            edit.checkAndSetVersion();
            postEdit(edit);
            result = edit.getOldType();
        }
        return result;
    }

    /**
     * Implements the functionality of {@link #putType(AspectGraph)}. Returns an
     * undoable edit wrapping this functionality.
     */
    private PutTypeEdit doPutType(AspectGraph type) throws IOException {
        testInit();
        String name = GraphInfo.getName(type);
        this.marshaller.marshalGraph(type.toPlainGraph(), createTypeFile(name));
        AspectGraph oldType = this.typeMap.put(name, type);
        return new PutTypeEdit(oldType, type);
    }

    @Override
    public AspectGraph renameGraph(String oldName, String newName)
        throws IOException {
        AspectGraph result = null;
        RenameGraphEdit edit = doRenameGraph(oldName, newName);
        if (edit != null) {
            edit.checkAndSetVersion();
            postEdit(edit);
            result = edit.getOldGraph();
        }
        return result;
    }

    /**
     * Implements the functionality of {@link #renameGraph(String, String)}.
     * Returns an undoable edit wrapping this functionality.
     */
    private RenameGraphEdit doRenameGraph(String oldName, String newName)
        throws IOException {
        RenameGraphEdit result = null;
        testInit();
        AspectGraph graph = this.graphMap.remove(oldName);
        if (graph != null) {
            this.marshaller.deleteGraph(createGraphFile(oldName));
            GraphInfo.setName(graph, newName);
            AspectGraph oldGraph = this.graphMap.put(newName, graph);
            this.marshaller.marshalGraph(graph.toPlainGraph(),
                createGraphFile(newName));
            result = new RenameGraphEdit(oldName, newName, oldGraph);
        } else if (this.graphMap.containsKey(newName)) {
            AspectGraph oldGraph = this.graphMap.remove(newName);
            result = new RenameGraphEdit(oldName, newName, oldGraph);
        }
        return result;
    }

    @Override
    public AspectGraph renameRule(String oldName, String newName)
        throws IOException {
        AspectGraph result = null;
        RenameRuleEdit edit = doRenameRule(oldName, newName);
        if (edit != null) {
            edit.checkAndSetVersion();
            postEdit(edit);
            result = edit.getOldRule();
        }
        return result;
    }

    /**
     * Implements the functionality of {@link #renameRule(String, String)}.
     * Returns an undoable edit wrapping this functionality.
     */
    private RenameRuleEdit doRenameRule(String oldName, String newName)
        throws IOException {
        RenameRuleEdit result = null;
        testInit();
        RuleName oldRuleName = new RuleName(oldName);
        RuleName newRuleName = new RuleName(newName);
        AspectGraph rule = this.ruleMap.remove(oldRuleName);
        if (rule != null) {
            this.marshaller.deleteGraph(createRuleFile(oldRuleName));
            GraphInfo.setName(rule, newName);
            AspectGraph oldRule = this.ruleMap.put(newRuleName, rule);
            this.marshaller.marshalGraph(rule.toPlainGraph(),
                createRuleFile(newRuleName));
            result = new RenameRuleEdit(oldName, newName, oldRule);
        } else if (this.ruleMap.containsKey(newRuleName)) {
            this.marshaller.deleteGraph(createRuleFile(newRuleName));
            AspectGraph oldRule = this.ruleMap.remove(newRuleName);
            result = new RenameRuleEdit(oldName, newName, oldRule);
        }
        return result;
    }

    @Override
    public AspectGraph renameType(String oldName, String newName)
        throws IOException {
        AspectGraph result = null;
        RenameTypeEdit edit = doRenameType(oldName, newName);
        if (edit != null) {
            edit.checkAndSetVersion();
            postEdit(edit);
            result = edit.getOldType();
        }
        return result;
    }

    /**
     * Implements the functionality of {@link #renameType(String, String)}.
     * Returns an undoable edit wrapping this functionality.
     */
    private RenameTypeEdit doRenameType(String oldName, String newName)
        throws IOException {
        RenameTypeEdit result = null;
        testInit();
        AspectGraph type = this.typeMap.remove(oldName);
        boolean edited = false;
        AspectGraph oldType = null;
        if (type != null) {
            oldType = this.typeMap.put(newName, type);
            this.marshaller.deleteGraph(createTypeFile(oldName));
            GraphInfo.setName(type, newName);
            this.marshaller.marshalGraph(type.toPlainGraph(),
                createTypeFile(newName));
            edited = true;
        } else if (this.typeMap.containsKey(newName)) {
            oldType = this.typeMap.remove(newName);
            this.marshaller.deleteGraph(createTypeFile(newName));
            edited = true;
        }
        if (edited) {
            SystemProperties oldProps = null;
            SystemProperties newProps = null;
            List<String> types = getProperties().getTypeNames();
            if (types.remove(oldName)) {
                oldProps = getProperties();
                newProps = getProperties().clone();
                types.add(newName);
                newProps.setTypeNames(types);
            }
            result =
                new RenameTypeEdit(oldName, newName, oldType, oldProps,
                    newProps);
        }
        return result;
    }

    @Override
    public void relabel(TypeLabel oldLabel, TypeLabel newLabel)
        throws IOException {
        Edit edit = doRelabel(oldLabel, newLabel);
        if (edit != null) {
            postEdit(edit);
        }
    }

    /**
     * Implements the functionality of {@link #relabel(TypeLabel, TypeLabel)}. Returns
     * an undoable edit wrapping this functionality.
     */
    private MyCompoundEdit doRelabel(TypeLabel oldLabel, TypeLabel newLabel)
        throws IOException {
        MyCompoundEdit result = new MyCompoundEdit(Options.RELABEL_ACTION_NAME);
        for (AspectGraph graph : getGraphs().values()) {
            AspectGraph newGraph = graph.relabel(oldLabel, newLabel);
            if (newGraph != graph) {
                Edit edit = doPutGraph(newGraph);
                result.addEdit(edit);
            }
        }
        for (AspectGraph type : getTypes().values()) {
            AspectGraph newType = type.relabel(oldLabel, newLabel);
            if (newType != type) {
                Edit edit = doPutType(newType);
                result.addEdit(edit);
            }
        }
        for (AspectGraph rule : getRules().values()) {
            AspectGraph newRule = rule.relabel(oldLabel, newLabel);
            if (newRule != rule) {
                Edit edit = doPutRule(newRule);
                result.addEdit(edit);
            }
        }
        SystemProperties newProperties =
            this.properties.relabel(oldLabel, newLabel);
        if (newProperties != this.properties) {
            Edit edit = doPutProperties(newProperties);
            result.addEdit(edit);
        }
        result.end();
        return result.getChange() == 0 ? null : result;
    }

    /** Renumbers all nodes in the rules and graphs of this grammar
     * to a consecutive sequence starting with {@code 0}.
     * @throws IOException -
     */
    public void renumber() throws IOException {
        Edit edit = doRenumber();
        if (edit != null) {
            postEdit(edit);
        }
    }

    /**
     * Implements the functionality of {@link #renumber()}. Returns
     * an undoable edit wrapping this functionality.
     */
    private MyCompoundEdit doRenumber() throws IOException {
        MyCompoundEdit result =
            new MyCompoundEdit(Options.RENUMBER_ACTION_NAME);
        for (AspectGraph graph : getGraphs().values()) {
            AspectGraph newGraph = graph.renumber();
            if (newGraph != graph) {
                Edit edit = doPutGraph(newGraph);
                result.addEdit(edit);
            }
        }
        for (AspectGraph type : getTypes().values()) {
            AspectGraph newType = type.renumber();
            if (newType != type) {
                Edit edit = doPutType(newType);
                result.addEdit(edit);
            }
        }
        for (AspectGraph rule : getRules().values()) {
            AspectGraph newRule = rule.renumber();
            if (newRule != rule) {
                Edit edit = doPutRule(newRule);
                result.addEdit(edit);
            }
        }
        result.end();
        return result.getChange() == 0 ? null : result;
    }

    @Override
    public void reload() throws IOException {
        loadProperties();
        loadRules();
        loadGraphs();
        loadTypes();
        loadControls();
        notifyObservers(new MyEdit(SystemStore.PROPERTIES_CHANGE
            | SystemStore.RULE_CHANGE | SystemStore.GRAPH_CHANGE
            | SystemStore.TYPE_CHANGE | SystemStore.CONTROL_CHANGE));
        this.initialised = true;
    }

    public StoredGrammarView toGrammarView() {
        if (this.view == null) {
            this.view = new StoredGrammarView(this);
            this.observable.addObserver(this.view);
        }
        return this.view;
    }

    public Object getLocation() {
        if (this.file == null) {
            return this.url;
        } else {
            return this.file;
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
            this.file == null ? getLocation().toString()
                    : this.file.getParent();
        return getName() + " - " + location;
    }

    /** Callback factory method for creating a graph marshaller. */
    private Xml<DefaultGraph> createGraphMarshaller(boolean layouted) {
        if (layouted) {
            return new LayedOutXml();
        } else {
            return new DefaultGxl();
        }
    }

    /**
     * Loads the named graphs from specified location and returns the
     * corresponding AspectGraphs
     */
    private void loadGraphs() throws IOException {
        collectObjects(this.graphMap, STATE_FILTER, GraphRole.HOST);
    }

    /**
     * Loads the named graphs from specified location and returns the
     * corresponding AspectGraphs
     */
    private void loadTypes() throws IOException {
        collectObjects(this.typeMap, TYPE_FILTER, GraphRole.TYPE);
    }

    /**
     * Collects all aspect graphs from the {@link #file} directory with a given
     * extension, and a given role.
     * @see GraphInfo#getRole()
     */
    private void collectObjects(Map<String,AspectGraph> result,
            ExtensionFilter filter, GraphRole role) throws IOException {
        result.clear();
        File[] files = this.file.listFiles(filter);
        // read in production rules
        for (File file : files) {
            // check for overlapping rule and directory names
            if (!file.isDirectory()) {
                DefaultGraph plainGraph = this.marshaller.unmarshalGraph(file);
                String graphName = filter.stripExtension(file.getName());
                /*
                 * For backward compatibility, we set the role and name of the
                 * graph
                 */
                GraphInfo.setRole(plainGraph, role);
                GraphInfo.setName(plainGraph, graphName);
                AspectGraph graph = AspectGraph.newInstance(plainGraph);
                /* Store the graph */
                Object oldEntry = result.put(graphName, graph);
                assert oldEntry == null : String.format(
                    "Duplicate %s name '%s'", role, graphName);
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
        this.properties = this.loadGrammarProperties();
    }

    /**
     * Loads the properties file from file (if any), and returns them.
     */
    public SystemProperties loadGrammarProperties() throws IOException {
        SystemProperties properties = new SystemProperties();
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
            try {
                grammarProperties.load(s);
            } finally {
                s.close();
            }
            properties.putAll(grammarProperties);
        }
        return properties;
    }

    /**
     * Loads all the rules from the file into {@link #ruleMap}.
     */
    private void loadRules() throws IOException {
        this.ruleMap.clear();
        try {
            collectRules(this.file, null);
        } catch (FormatException e) {
            throw new IOException(e.getMessage());
        }
    }

    private void saveControl(String name, String program) throws IOException {
        File controlFile = createControlFile(name);
        Writer writer = new FileWriter(controlFile);
        try {
            writer.write(program);
        } finally {
            writer.close();
        }
    }

    private void saveProperties() throws IOException {
        File propertiesFile =
            new File(this.file,
                PROPERTIES_FILTER.addExtension(Groove.PROPERTY_NAME));
        Writer propertiesWriter = new FileWriter(propertiesFile);
        try {
            this.properties.store(propertiesWriter, null);
        } finally {
            propertiesWriter.close();
        }
    }

    private void saveProperties(SystemProperties properties) throws IOException {
        File propertiesFile =
            new File(this.file,
                PROPERTIES_FILTER.addExtension(Groove.PROPERTY_NAME));
        Writer propertiesWriter = new FileWriter(propertiesFile);
        try {
            properties.store(propertiesWriter, null);
        } finally {
            propertiesWriter.close();
        }
    }

    /**
     * Auxiliary method to descend recursively into subdirectories and load all
     * rules found there into the rule map
     * @param directory directory to descend into
     * @param rulePath rule name prefix for the current directory (with respect
     *        to the global file)
     * @throws IOException if an error occurs while loading a rule graph
     * @throws FormatException if there is a rule name with an error
     */
    private void collectRules(File directory, RuleName rulePath)
        throws IOException, FormatException {
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
                    DefaultGraph plainGraph =
                        this.marshaller.unmarshalGraph(file);
                    /*
                     * For backward compatibility, we set the role and name of
                     * the rule graph
                     */
                    GraphInfo.setRole(plainGraph, GraphRole.RULE);
                    GraphInfo.setName(plainGraph, ruleName.toString());
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
     * Creates a file name from a given control program name. The file name
     * consists of the store location, the program, and the control extension.
     */
    private File createControlFile(String controlName) {
        return new File(this.file, CONTROL_FILTER.addExtension(controlName));
    }

    /**
     * Creates a file name from a given graph name. The file name consists of
     * the store location, the name, and the state extension.
     */
    private File createGraphFile(String graphName) {
        return new File(this.file, STATE_FILTER.addExtension(graphName));
    }

    /**
     * Creates a file name from a given graph name. The file name consists of
     * the store location, the name, and the state extension.
     */
    private File createTypeFile(String graphName) {
        return new File(this.file, TYPE_FILTER.addExtension(graphName));
    }

    /**
     * Creates a file name from a given rule name. The file name consists of a
     * given parent file, the name, and the state extension.
     */
    private File createRuleFile(RuleName ruleName) {
        return new File(this.file, RULE_FILTER.addExtension(Groove.toString(
            ruleName.tokens(), "", "", Groove.FILE_SEPARATOR)));
    }

    /** Posts the edit, and also notifies the observers. */
    @Override
    public synchronized void postEdit(UndoableEdit e) {
        super.postEdit(e);
        notifyObservers((Edit) e);
    }

    /** Notifies the observers with a given string value. */
    private void notifyObservers(Edit edit) {
        this.observable.notifyObservers(edit);
    }

    /** The name-to-rule map of the source. */
    private final Map<RuleName,AspectGraph> ruleMap =
        new HashMap<RuleName,AspectGraph>();
    /** The name-to-graph map of the source. */
    private final Map<String,AspectGraph> graphMap =
        new HashMap<String,AspectGraph>();
    /** The name-to-types map of the source. */
    private final Map<String,AspectGraph> typeMap =
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
    private final Xml<DefaultGraph> marshaller;
    /** Flag indicating whether the store has been loaded. */
    private boolean initialised;
    /** The grammar view associated with this store. */
    private StoredGrammarView view;
    /** The observable object associated with this system store. */
    private final Observable observable = new Observable() {
        /** Always invokes {@link #setChanged()}. */
        @Override
        public void notifyObservers(Object arg) {
            setChanged();
            super.notifyObservers(arg);
        }
    };

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
            DefaultFileSystemStore result =
                new DefaultFileSystemStore(file, true);
            result.reload();
            // save properties
            result.doPutProperties(store.getProperties());
            // save control programs
            for (Map.Entry<String,String> controlEntry : store.getControls().entrySet()) {
                result.doPutControl(controlEntry.getKey(),
                    controlEntry.getValue());
            }
            // save graphs
            for (AspectGraph stateGraph : store.getGraphs().values()) {
                result.doPutGraph(stateGraph);
            }
            // save rules
            for (AspectGraph ruleGraph : store.getRules().values()) {
                result.doPutRule(ruleGraph);
            }
            // save type graphs
            for (AspectGraph typeGraph : store.getTypes().values()) {
                result.doPutType(typeGraph);
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

    /** File filter for type files. */
    static private final ExtensionFilter TYPE_FILTER =
        Groove.createTypeFilter();

    /** File filter for property files. */
    static private final ExtensionFilter PROPERTIES_FILTER =
        Groove.createPropertyFilter();

    /** File filter for control files. */
    static private final ExtensionFilter CONTROL_FILTER =
        Groove.createControlFilter();

    /** Error message if a grammar cannot be loaded. */
    static private final String LOAD_ERROR = "Can't load graph grammar";

    private class MyEdit extends AbstractUndoableEdit implements Edit {
        public MyEdit(int change) {
            this.change = change;
        }

        @Override
        public int getChange() {
            return this.change;
        }

        public void checkAndSetVersion() {
            if (!getProperties().isCurrentVersionProperties()) {
                this.origProp = getProperties().clone();
                getProperties().setCurrentVersionProperties();
                try {
                    saveProperties();
                } catch (IOException e) {
                    // Silently fail..?
                }
            }
        }

        @Override
        public void redo() throws CannotRedoException {
            super.redo();
            this.checkAndSetVersion();
        }

        @Override
        public void undo() throws CannotUndoException {
            super.undo();
            try {
                if (this.origProp != null) {
                    DefaultFileSystemStore.this.properties = this.origProp;
                    saveProperties();
                    this.origProp = null;
                }
            } catch (IOException exc) {
                throw new CannotUndoException();
            }
        }

        /**
         * The change information in this edit.
         * @see #getChange()
         */
        private final int change;

        private SystemProperties origProp = null;
    }

    /** Edit wrapping a relabelling. */
    private static class MyCompoundEdit extends CompoundEdit implements Edit {
        /** Constructs a compound edit with a given name. */
        public MyCompoundEdit(String presentationName) {
            this.presentationName = presentationName;
        }

        @Override
        public String getPresentationName() {
            return this.presentationName;
        }

        @Override
        public String getRedoPresentationName() {
            return Options.REDO_ACTION_NAME + " " + getPresentationName();
        }

        @Override
        public String getUndoPresentationName() {
            return Options.UNDO_ACTION_NAME + " " + getPresentationName();
        }

        @Override
        public boolean addEdit(UndoableEdit anEdit) {
            boolean result = super.addEdit(anEdit);
            if (result) {
                assert anEdit instanceof Edit;
                this.change |= ((Edit) anEdit).getChange();
            }
            return result;
        }

        @Override
        public int getChange() {
            return this.change;
        }

        /**
         * The change information in this edit.
         * @see #getChange()
         */
        private int change;
        /** The name of this edit. */
        private final String presentationName;
    }

    /** Edit consisting of the deletion of a control program. */
    private class DeleteControlEdit extends MyEdit {
        public DeleteControlEdit(String name, String control,
                SystemProperties oldProps, SystemProperties newProps) {
            super(CONTROL_CHANGE);
            this.name = name;
            this.control = control;
            this.oldProps = oldProps;
            this.newProps = newProps;
        }

        @Override
        public String getPresentationName() {
            return Options.DELETE_CONTROL_ACTION_NAME;
        }

        @Override
        public void redo() throws CannotRedoException {
            super.redo();
            try {
                if (this.newProps != null) {
                    doPutProperties(this.newProps);
                }
                doDeleteControl(this.name);
            } catch (IOException exc) {
                throw new CannotUndoException();
            }
            notifyObservers(this);
        }

        @Override
        public void undo() throws CannotUndoException {
            super.undo();
            try {
                doPutControl(this.name, this.control);
                if (this.oldProps != null) {
                    doPutProperties(this.oldProps);
                }
            } catch (IOException exc) {
                throw new CannotUndoException();
            }
            notifyObservers(this);
        }

        /** Returns the deleted control program. */
        public final String getControl() {
            return this.control;
        }

        /** The name of the deleted control program. */
        private final String name;
        /** The deleted control program. */
        private final String control;
        /** The old system properties; possibly {@code null}. */
        private final SystemProperties oldProps;
        /** The new system properties; possibly {@code null}. */
        private final SystemProperties newProps;
    }

    /** Edit consisting of the deletion of a graph. */
    private class DeleteGraphEdit extends MyEdit {
        public DeleteGraphEdit(AspectGraph graph) {
            super(GRAPH_CHANGE);
            this.graph = graph;
        }

        @Override
        public String getPresentationName() {
            return Options.DELETE_GRAPH_ACTION_NAME;
        }

        @Override
        public void redo() throws CannotRedoException {
            super.redo();
            doDeleteGraph(GraphInfo.getName(this.graph));
            notifyObservers(this);
        }

        @Override
        public void undo() throws CannotUndoException {
            super.undo();
            try {
                doPutGraph(this.graph);
            } catch (IOException exc) {
                throw new CannotUndoException();
            }
            notifyObservers(this);
        }

        /** Returns the deleted graph. */
        public final AspectGraph getGraph() {
            return this.graph;
        }

        /** The deleted graph. */
        private final AspectGraph graph;
    }

    /** Edit consisting of the deletion of a rule. */
    private class DeleteRuleEdit extends MyEdit {
        public DeleteRuleEdit(AspectGraph rule) {
            super(RULE_CHANGE);
            this.rule = rule;
        }

        @Override
        public String getPresentationName() {
            return Options.DELETE_RULE_ACTION_NAME;
        }

        @Override
        public void redo() throws CannotRedoException {
            super.redo();
            doDeleteRule(new RuleName(GraphInfo.getName(this.rule)));
            notifyObservers(this);
        }

        @Override
        public void undo() throws CannotUndoException {
            super.undo();
            try {
                doPutRule(this.rule);
            } catch (IOException exc) {
                throw new CannotUndoException();
            }
            notifyObservers(this);
        }

        /** Returns the deleted graph. */
        public final AspectGraph getRule() {
            return this.rule;
        }

        /** The deleted graph. */
        private final AspectGraph rule;
    }

    /** Edit consisting of the deletion of a graph type. */
    private class DeleteTypeEdit extends MyEdit {
        public DeleteTypeEdit(AspectGraph type, SystemProperties oldProps,
                SystemProperties newProps) {
            super(TYPE_CHANGE);
            this.type = type;
            this.oldProps = oldProps;
            this.newProps = newProps;
        }

        @Override
        public String getPresentationName() {
            return Options.DELETE_TYPE_ACTION_NAME;
        }

        @Override
        public void redo() throws CannotRedoException {
            super.redo();
            try {
                if (this.newProps != null) {
                    doPutProperties(this.newProps);
                }
                doDeleteType(GraphInfo.getName(this.type));
            } catch (IOException exc) {
                throw new CannotUndoException();
            }
            notifyObservers(this);
        }

        @Override
        public void undo() throws CannotUndoException {
            super.undo();
            try {
                doPutType(this.type);
                if (this.oldProps != null) {
                    doPutProperties(this.oldProps);
                }
            } catch (IOException exc) {
                throw new CannotUndoException();
            }
            notifyObservers(this);
        }

        /** Returns the deleted graph. */
        public final AspectGraph getType() {
            return this.type;
        }

        /** The deleted graph. */
        private final AspectGraph type;
        /** The old system properties; possibly {@code null}. */
        private final SystemProperties oldProps;
        /** The new system properties; possibly {@code null}. */
        private final SystemProperties newProps;
    }

    /** Edit consisting of the renaming of a graph. */
    private class RenameGraphEdit extends MyEdit {
        public RenameGraphEdit(String oldName, String newName,
                AspectGraph oldGraph) {
            super(GRAPH_CHANGE);
            this.oldName = oldName;
            this.newName = newName;
            this.oldGraph = oldGraph;
        }

        @Override
        public String getPresentationName() {
            return Options.RENAME_GRAPH_ACTION_NAME;
        }

        @Override
        public void redo() throws CannotRedoException {
            super.redo();
            try {
                doRenameGraph(this.oldName, this.newName);
            } catch (IOException exc) {
                throw new CannotRedoException();
            }
            notifyObservers(this);
        }

        @Override
        public void undo() throws CannotUndoException {
            super.undo();
            try {
                doRenameGraph(this.newName, this.oldName);
                if (this.oldGraph != null) {
                    doPutGraph(this.oldGraph);
                }
            } catch (IOException exc) {
                throw new CannotUndoException();
            }
            notifyObservers(this);
        }

        /** Returns the graph previously stored under {@link #newName}, if any. */
        public final AspectGraph getOldGraph() {
            return this.oldGraph;
        }

        /** The old name of the renamed graph. */
        private final String oldName;
        /** The new name of the renamed graph. */
        private final String newName;
        /** The graph previously stored under {@link #newName}, if any. */
        private final AspectGraph oldGraph;
    }

    /** Edit consisting of the renaming of a rule. */
    private class RenameRuleEdit extends MyEdit {
        public RenameRuleEdit(String oldName, String newName,
                AspectGraph oldRule) {
            super(RULE_CHANGE);
            this.oldName = oldName;
            this.newName = newName;
            this.oldRule = oldRule;
        }

        @Override
        public String getPresentationName() {
            return Options.RENAME_RULE_ACTION_NAME;
        }

        @Override
        public void redo() throws CannotRedoException {
            super.redo();
            try {
                doRenameRule(this.oldName, this.newName);
            } catch (IOException exc) {
                throw new CannotRedoException();
            }
            notifyObservers(this);
        }

        @Override
        public void undo() throws CannotUndoException {
            super.undo();
            try {
                doRenameRule(this.newName, this.oldName);
                if (this.oldRule != null) {
                    doPutRule(this.oldRule);
                }
            } catch (IOException exc) {
                throw new CannotUndoException();
            }
            notifyObservers(this);
        }

        /** Returns the rule previously stored under {@link #newName}, if any. */
        public final AspectGraph getOldRule() {
            return this.oldRule;
        }

        /** The old name of the renamed rule. */
        private final String oldName;
        /** The new name of the renamed rule. */
        private final String newName;
        /** The rule previously stored under {@link #newName}, if any. */
        private final AspectGraph oldRule;
    }

    /** Edit consisting of the renaming of a type graph. */
    private class RenameTypeEdit extends MyEdit {
        public RenameTypeEdit(String oldName, String newName,
                AspectGraph oldType, SystemProperties oldProps,
                SystemProperties newProps) {
            super(TYPE_CHANGE);
            this.oldName = oldName;
            this.newName = newName;
            this.oldType = oldType;
            this.oldProps = oldProps;
            this.newProps = newProps;
        }

        @Override
        public String getPresentationName() {
            return Options.RENAME_TYPE_ACTION_NAME;
        }

        @Override
        public void redo() throws CannotRedoException {
            super.redo();
            try {
                if (this.newProps != null) {
                    doPutProperties(this.newProps);
                }
                doRenameType(this.oldName, this.newName);
            } catch (IOException exc) {
                throw new CannotRedoException();
            }
            notifyObservers(this);
        }

        @Override
        public void undo() throws CannotUndoException {
            super.undo();
            try {
                if (this.oldProps != null) {
                    doPutProperties(this.oldProps);
                }
                doRenameType(this.newName, this.oldName);
                if (this.oldType != null) {
                    doPutType(this.oldType);
                }
            } catch (IOException exc) {
                throw new CannotUndoException();
            }
            notifyObservers(this);
        }

        /** Returns the graph previously stored under {@link #newName}, if any. */
        public final AspectGraph getOldType() {
            return this.oldType;
        }

        /** The old name of the renamed graph. */
        private final String oldName;
        /** The new name of the renamed graph. */
        private final String newName;
        /** The graph previously stored under {@link #newName}, if any. */
        private final AspectGraph oldType;
        /** The old system properties; possibly {@code null}. */
        private final SystemProperties oldProps;
        /** The new system properties; possibly {@code null}. */
        private final SystemProperties newProps;
    }

    /** Edit consisting of the addition of a control program. */
    private class PutControlEdit extends MyEdit {
        public PutControlEdit(String name, String oldControl, String newControl) {
            super(CONTROL_CHANGE);
            this.name = name;
            this.oldControl = oldControl;
            this.newControl = newControl;
        }

        @Override
        public String getPresentationName() {
            return this.oldControl == null ? Options.NEW_CONTROL_ACTION_NAME
                    : Options.EDIT_CONTROL_ACTION_NAME;
        }

        @Override
        public void redo() throws CannotRedoException {
            super.redo();
            try {
                doPutControl(this.name, this.newControl);
            } catch (IOException exc) {
                throw new CannotRedoException();
            }
            notifyObservers(this);
        }

        @Override
        public void undo() throws CannotUndoException {
            super.undo();
            if (this.oldControl == null) {
                doDeleteControl(this.name);
            } else {
                try {
                    doPutControl(this.name, this.oldControl);
                } catch (IOException exc) {
                    throw new CannotUndoException();
                }
            }
            notifyObservers(this);
        }

        /** Returns the deleted control program, if any. */
        public final String getOldControl() {
            return this.oldControl;
        }

        /** The name of the deleted control program. */
        private final String name;
        /** The old control program with this name. */
        private final String oldControl;
        /** The new control program with this name. */
        private final String newControl;
    }

    /** Edit consisting of the addition of a control program. */
    private class PutPropertiesEdit extends MyEdit {
        public PutPropertiesEdit(SystemProperties oldProperties,
                SystemProperties newProperties) {
            super(PROPERTIES_CHANGE);
            this.oldProperties = oldProperties;
            this.newProperties = newProperties;
        }

        @Override
        public String getPresentationName() {
            return Options.SYSTEM_PROPERTIES_ACTION_NAME;
        }

        @Override
        public void redo() throws CannotRedoException {
            super.redo();
            try {
                doPutProperties(this.newProperties);
            } catch (IOException exc) {
                throw new CannotRedoException();
            }
            notifyObservers(this);
        }

        @Override
        public void undo() throws CannotUndoException {
            super.undo();
            try {
                doPutProperties(this.oldProperties);
            } catch (IOException exc) {
                throw new CannotUndoException();
            }
            notifyObservers(this);
        }

        /** The old control program with this name. */
        private final SystemProperties oldProperties;
        /** The new control program with this name. */
        private final SystemProperties newProperties;
    }

    /** Edit consisting of the addition (or replacement) of a graph. */
    private class PutGraphEdit extends MyEdit {
        public PutGraphEdit(AspectGraph oldGraph, AspectGraph newGraph) {
            super(GRAPH_CHANGE);
            this.oldGraph = oldGraph;
            this.newGraph = newGraph;
        }

        @Override
        public String getPresentationName() {
            return this.oldGraph == null ? Options.NEW_GRAPH_ACTION_NAME
                    : Options.EDIT_GRAPH_ACTION_NAME;
        }

        @Override
        public void redo() throws CannotRedoException {
            super.redo();
            try {
                doPutGraph(this.newGraph);
            } catch (IOException exc) {
                throw new CannotRedoException();
            }
            notifyObservers(this);
        }

        @Override
        public void undo() throws CannotUndoException {
            super.undo();
            if (this.oldGraph == null) {
                doDeleteGraph(GraphInfo.getName(this.newGraph));
            } else {
                try {
                    doPutGraph(this.oldGraph);
                } catch (IOException exc) {
                    throw new CannotUndoException();
                }
            }
            notifyObservers(this);
        }

        /** Returns the deleted graph. */
        public final AspectGraph getOldGraph() {
            return this.oldGraph;
        }

        /** The deleted graph, if any. */
        private final AspectGraph oldGraph;
        /** The added graph. */
        private final AspectGraph newGraph;
    }

    /** Edit consisting of the addition (or replacement) of a type graph. */
    private class PutTypeEdit extends MyEdit {
        public PutTypeEdit(AspectGraph oldType, AspectGraph newType) {
            super(TYPE_CHANGE);
            this.oldType = oldType;
            this.newType = newType;
        }

        @Override
        public String getPresentationName() {
            return this.oldType == null ? Options.NEW_TYPE_ACTION_NAME
                    : Options.EDIT_TYPE_ACTION_NAME;
        }

        @Override
        public void redo() throws CannotRedoException {
            super.redo();
            try {
                doPutType(this.newType);
            } catch (IOException exc) {
                throw new CannotRedoException();
            }
            notifyObservers(this);
        }

        @Override
        public void undo() throws CannotUndoException {
            super.undo();
            if (this.oldType == null) {
                doDeleteType(GraphInfo.getName(this.newType));
            } else {
                try {
                    doPutType(this.oldType);
                } catch (IOException exc) {
                    throw new CannotUndoException();
                }
            }
            notifyObservers(this);
        }

        /** Returns the deleted graph. */
        public final AspectGraph getOldType() {
            return this.oldType;
        }

        /** The deleted graph, if any. */
        private final AspectGraph oldType;
        /** The added graph. */
        private final AspectGraph newType;
    }

    /** Edit consisting of the addition (or replacement) of a rule. */
    private class PutRuleEdit extends MyEdit {
        public PutRuleEdit(AspectGraph oldRule, AspectGraph newRule) {
            super(RULE_CHANGE);
            this.oldRule = oldRule;
            this.newRule = newRule;
        }

        @Override
        public String getPresentationName() {
            return this.oldRule == null ? Options.NEW_RULE_ACTION_NAME
                    : Options.EDIT_RULE_ACTION_NAME;
        }

        @Override
        public void redo() throws CannotRedoException {
            super.redo();
            try {
                doPutRule(this.newRule);
            } catch (IOException exc) {
                throw new CannotRedoException();
            }
            notifyObservers(this);
        }

        @Override
        public void undo() throws CannotUndoException {
            super.undo();
            if (this.oldRule == null) {
                doDeleteRule(new RuleName(GraphInfo.getName(this.newRule)));
            } else {
                try {
                    doPutRule(this.oldRule);
                } catch (IOException exc) {
                    throw new CannotUndoException();
                }
            }
            notifyObservers(this);
        }

        /** Returns the deleted rule, if any. */
        public final AspectGraph getOldRule() {
            return this.oldRule;
        }

        /** The deleted rule, if any. */
        private final AspectGraph oldRule;
        /** The added rule. */
        private final AspectGraph newRule;
    }
}
