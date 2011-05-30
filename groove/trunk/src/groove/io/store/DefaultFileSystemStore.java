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
package groove.io.store;

import static groove.graph.GraphRole.RULE;
import static groove.io.FileType.CONTROL_FILTER;
import static groove.io.FileType.GRAMMAR_FILTER;
import static groove.io.FileType.PROLOG_FILTER;
import static groove.io.FileType.PROPERTIES_FILTER;
import static groove.io.FileType.RULE_FILTER;
import static groove.io.FileType.STATE_FILTER;
import static groove.io.FileType.TYPE_FILTER;
import groove.graph.DefaultGraph;
import groove.graph.GraphInfo;
import groove.graph.GraphRole;
import groove.graph.TypeLabel;
import groove.gui.Options;
import groove.io.ExtensionFilter;
import groove.io.FileType;
import groove.io.PriorityFileName;
import groove.io.Util;
import groove.io.xml.DefaultGxl;
import groove.io.xml.LayedOutXml;
import groove.io.xml.Xml;
import groove.trans.RuleName;
import groove.trans.SystemProperties;
import groove.util.Groove;
import groove.view.FormatException;
import groove.view.StoredGrammarView;
import groove.view.aspect.AspectGraph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

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
     * extension {@link FileType#GRAMMAR}. The store is writable.
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
        if (!GRAMMAR_FILTER.acceptExtension(file)) {
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
     * extension {@link FileType#GRAMMAR}. The store is writable.
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
            this.hasSystemPropertiesFile = true;
        } catch (IOException e) {
            throw new IllegalArgumentException(
                "Could not create properties file.");
        }
        prop.setShowLoopsAsLabels(false);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Map<String,String> getControls() {
        testInit();
        return Collections.unmodifiableMap(this.controlMap);
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
    public String deleteControl(String name) throws IOException {
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
    private DeleteControlEdit doDeleteControl(String name) throws IOException {
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
                doPutProperties(newProps);
            }
            result = new DeleteControlEdit(name, control, oldProps, newProps);
        }
        return result;
    }

    @Override
    public String renameControl(String oldName, String newName)
        throws IOException {
        String result = null;
        RenameControlEdit edit = doRenameControl(oldName, newName);
        if (edit != null) {
            edit.checkAndSetVersion();
            postEdit(edit);
            result = edit.getOldControl();
        }
        return result;
    }

    /**
     * Implements the functionality of {@link #renameRule(String, String)}.
     * Returns an undoable edit wrapping this functionality.
     */
    private RenameControlEdit doRenameControl(String oldName, String newName)
        throws IOException {
        testInit();
        String control = this.controlMap.remove(oldName);
        assert control != null;
        createControlFile(oldName).renameTo(createControlFile(newName));
        String oldControl = this.controlMap.put(newName, control);
        assert oldControl == null;
        SystemProperties oldProps = null;
        SystemProperties newProps = null;
        String activeControl = getProperties().getControlName();
        if (oldName.equals(activeControl)) {
            oldProps = getProperties();
            newProps = getProperties().clone();
            newProps.setControlName(newName);
            doPutProperties(newProps);
        }
        return new RenameControlEdit(oldName, newName, oldProps, newProps,
            control);
    }

    @Override
    public Map<String,String> getProlog() {
        testInit();
        return Collections.unmodifiableMap(this.prologMap);
    }

    @Override
    public String putProlog(String name, String prolog) throws IOException {
        String result = null;
        PutPrologEdit edit = doPutProlog(name, prolog);
        if (edit != null) {
            edit.checkAndSetVersion();
            postEdit(edit);
            result = edit.getOldProlog();
        }
        return result;
    }

    /**
     * Implements the functionality of {@link #putControl(String, String)}.
     * Returns an undoable edit wrapping this functionality.
     */
    private PutPrologEdit doPutProlog(String name, String prolog)
        throws IOException {
        testInit();
        saveProlog(name, prolog);
        String oldProlog = this.prologMap.put(name, prolog);
        return new PutPrologEdit(name, oldProlog, prolog);
    }

    @Override
    public String deleteProlog(String name) {
        String result = null;
        DeletePrologEdit deleteEdit = doDeleteProlog(name);
        if (deleteEdit != null) {
            deleteEdit.checkAndSetVersion();
            postEdit(deleteEdit);
            result = deleteEdit.getProlog();
        }
        return result;
    }

    /**
     * Implements the functionality of the {@link #deleteControl(String)}
     * method. Returns a corresponding undoable edit.
     */
    private DeletePrologEdit doDeleteProlog(String name) {
        DeletePrologEdit result = null;
        testInit();
        String prolog = this.prologMap.remove(name);
        if (prolog != null) {
            createPrologFile(name).delete();
            result = new DeletePrologEdit(name, prolog);
        }
        return result;
    }

    @Override
    public String renameProlog(String oldName, String newName) {
        String result = null;
        RenamePrologEdit edit = doRenameProlog(oldName, newName);
        if (edit != null) {
            edit.checkAndSetVersion();
            postEdit(edit);
            result = edit.getOldProlog();
        }
        return result;
    }

    /**
     * Implements the functionality of {@link #renameRule(String, String)}.
     * Returns an undoable edit wrapping this functionality.
     */
    private RenamePrologEdit doRenameProlog(String oldName, String newName) {
        testInit();
        String prolog = this.prologMap.remove(oldName);
        assert prolog != null;
        createPrologFile(oldName).renameTo(createPrologFile(newName));
        String oldProlog = this.prologMap.put(newName, prolog);
        assert oldProlog == null;
        return new RenamePrologEdit(oldName, newName, prolog);
    }

    @Override
    public Map<String,AspectGraph> getGraphs() {
        testInit();
        return Collections.unmodifiableMap(this.graphMap);
    }

    @Override
    public Collection<AspectGraph> putGraphs(Collection<AspectGraph> graphs)
        throws IOException {
        Collection<AspectGraph> result = Collections.emptySet();
        GraphEdit edit = doPutGraphs(graphs);
        if (edit != null) {
            edit.checkAndSetVersion();
            postEdit(edit);
            result = edit.getOldGraphs();
        }
        return result;
    }

    /**
     * Implements the functionality of {@link #putGraphs(Collection)}. Returns
     * an undoable edit wrapping this functionality.
     */
    private GraphEdit doPutGraphs(Collection<AspectGraph> newGraphs)
        throws IOException {
        testInit();
        Set<AspectGraph> oldGraphs = new HashSet<AspectGraph>();
        for (AspectGraph newGraph : newGraphs) {
            String name = newGraph.getName();
            this.marshaller.marshalGraph(newGraph.toPlainGraph(),
                createGraphFile(name));
            AspectGraph oldGraph = this.graphMap.put(name, newGraph);
            if (oldGraph != null) {
                oldGraphs.add(oldGraph);
            }
        }
        return new GraphEdit(oldGraphs, newGraphs);
    }

    @Override
    public Collection<AspectGraph> deleteGraphs(Collection<String> name) {
        Collection<AspectGraph> result = Collections.emptySet();
        GraphEdit edit = doDeleteGraphs(name);
        if (edit != null) {
            edit.checkAndSetVersion();
            postEdit(edit);
            result = edit.getOldGraphs();
        }
        return result;
    }

    /**
     * Implements the functionality of the {@link #deleteGraphs(Collection)} method.
     * Returns a corresponding undoable edit.
     */
    private GraphEdit doDeleteGraphs(Collection<String> names) {
        testInit();
        List<AspectGraph> deletedGraphs =
            new ArrayList<AspectGraph>(names.size());
        for (String name : names) {
            AspectGraph graph = this.graphMap.remove(name);
            assert graph != null;
            this.marshaller.deleteGraph(createGraphFile(name));
            deletedGraphs.add(graph);
        }
        return new GraphEdit(deletedGraphs, Collections.<AspectGraph>emptySet());
    }

    @Override
    public AspectGraph renameGraph(String oldName, String newName)
        throws IOException {
        AspectGraph result = null;
        GraphEdit edit = doRenameGraph(oldName, newName);
        if (edit != null) {
            edit.checkAndSetVersion();
            postEdit(edit);
            result = edit.getNewGraphs().iterator().next();
        }
        return result;
    }

    /**
     * Implements the functionality of {@link #renameGraph(String, String)}.
     * Returns an undoable edit wrapping this functionality.
     */
    private GraphEdit doRenameGraph(String oldName, String newName)
        throws IOException {
        testInit();
        AspectGraph oldGraph = this.graphMap.remove(oldName);
        assert oldGraph != null;
        this.marshaller.deleteGraph(createGraphFile(oldName));
        AspectGraph newGraph = oldGraph.rename(newName);
        AspectGraph previous = this.graphMap.put(newName, newGraph);
        assert previous == null;
        this.marshaller.marshalGraph(newGraph.toPlainGraph(),
            createGraphFile(newName));
        return new GraphEdit(Collections.singleton(oldGraph),
            Collections.singleton(newGraph));
    }

    @Override
    public Map<String,AspectGraph> getRules() {
        testInit();
        return Collections.unmodifiableMap(this.ruleMap);
    }

    @Override
    public Collection<AspectGraph> putRules(Collection<AspectGraph> rule)
        throws IOException {
        Collection<AspectGraph> result = Collections.emptySet();
        RuleEdit edit = doPutRules(rule);
        if (edit != null) {
            edit.checkAndSetVersion();
            postEdit(edit);
            result = edit.getOldRules();
        }
        return result;
    }

    /**
     * Implements the functionality of {@link #putRules(Collection)}. Returns an
     * undoable edit wrapping this functionality.
     */
    private RuleEdit doPutRules(Collection<AspectGraph> rules)
        throws IOException {
        testInit();
        Set<AspectGraph> oldRules = new HashSet<AspectGraph>();
        for (AspectGraph newRule : rules) {
            String name = newRule.getName();
            this.marshaller.marshalGraph(newRule.toPlainGraph(),
                createRuleFile(name));
            AspectGraph oldRule = this.ruleMap.put(name, newRule);
            if (oldRule != null) {
                oldRules.add(oldRule);
            }
        }
        return new RuleEdit(oldRules, rules);
    }

    @Override
    public Collection<AspectGraph> deleteRules(Collection<String> names) {
        Collection<AspectGraph> result = Collections.emptySet();
        RuleEdit edit = doDeleteRules(names);
        if (edit != null) {
            edit.checkAndSetVersion();
            postEdit(edit);
            result = edit.getOldRules();
        }
        return result;
    }

    /**
     * Implements the functionality of the {@link #deleteRules(Collection)} method.
     * Returns a corresponding undoable edit.
     */
    private RuleEdit doDeleteRules(Collection<String> names) {
        testInit();
        List<AspectGraph> deletedRules =
            new ArrayList<AspectGraph>(names.size());
        for (String name : names) {
            AspectGraph rule = this.ruleMap.remove(name);
            assert rule != null;
            this.marshaller.deleteGraph(new File(
                this.file,
                RULE_FILTER.addExtension(Groove.toString(
                    new RuleName(name).tokens(), "", "", Groove.FILE_SEPARATOR))));
            deletedRules.add(rule);
        }
        return new RuleEdit(deletedRules, Collections.<AspectGraph>emptySet());
    }

    @Override
    public AspectGraph renameRule(String oldName, String newName)
        throws IOException {
        AspectGraph result = null;
        RuleEdit edit = doRenameRule(oldName, newName);
        if (edit != null) {
            edit.checkAndSetVersion();
            postEdit(edit);
            result = edit.getNewRules().iterator().next();
        }
        return result;
    }

    /**
     * Implements the functionality of {@link #renameRule(String, String)}.
     * Returns an undoable edit wrapping this functionality.
     */
    private RuleEdit doRenameRule(String oldName, String newName)
        throws IOException {
        testInit();
        AspectGraph oldRule = this.ruleMap.remove(oldName);
        assert oldRule != null;
        this.marshaller.deleteGraph(createRuleFile(oldName));
        AspectGraph newRule = oldRule.rename(newName);
        AspectGraph previous = this.ruleMap.put(newName, newRule);
        assert previous == null;
        this.marshaller.marshalGraph(newRule.toPlainGraph(),
            createRuleFile(newName));
        return new RuleEdit(Collections.singleton(oldRule),
            Collections.singleton(newRule));
    }

    @Override
    public Map<String,AspectGraph> getTypes() {
        testInit();
        return Collections.unmodifiableMap(this.typeMap);
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
        String name = type.getName();
        this.marshaller.marshalGraph(type.toPlainGraph(), createTypeFile(name));
        AspectGraph oldType = this.typeMap.put(name, type);
        return new PutTypeEdit(oldType, type);
    }

    @Override
    public AspectGraph deleteType(String name) throws IOException {
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
    private DeleteTypeEdit doDeleteType(String name) throws IOException {
        DeleteTypeEdit result = null;
        testInit();
        AspectGraph type = this.typeMap.remove(name);
        if (type != null) {
            this.marshaller.deleteGraph(createTypeFile(name));
            // change the type-related system properties, if necessary
            SystemProperties oldProps = null;
            SystemProperties newProps = null;
            List<String> types =
                new ArrayList<String>(getProperties().getTypeNames());
            if (types.remove(name)) {
                oldProps = getProperties();
                newProps = getProperties().clone();
                newProps.setTypeNames(types);
                doPutProperties(newProps);
            }
            result = new DeleteTypeEdit(type, oldProps, newProps);
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
            this.marshaller.deleteGraph(createTypeFile(oldName));
            type = type.rename(newName);
            oldType = this.typeMap.put(newName, type);
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
            List<String> types =
                new ArrayList<String>(getProperties().getTypeNames());
            if (types.remove(oldName)) {
                oldProps = getProperties();
                newProps = getProperties().clone();
                types.add(newName);
                newProps.setTypeNames(types);
                doPutProperties(newProps);
            }
            result =
                new RenameTypeEdit(oldName, newName, oldType, oldProps,
                    newProps);
        }
        return result;
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

        List<AspectGraph> newGraphs =
            new ArrayList<AspectGraph>(getGraphs().size());
        for (AspectGraph graph : getGraphs().values()) {
            AspectGraph newGraph = graph.relabel(oldLabel, newLabel);
            if (newGraph != graph) {
                newGraphs.add(newGraph);
            }
        }
        result.addEdit(doPutGraphs(newGraphs));
        for (AspectGraph type : getTypes().values()) {
            AspectGraph newType = type.relabel(oldLabel, newLabel);
            if (newType != type) {
                Edit edit = doPutType(newType);
                result.addEdit(edit);
            }
        }
        List<AspectGraph> newRules =
            new ArrayList<AspectGraph>(getRules().size());
        for (AspectGraph rule : getRules().values()) {
            AspectGraph newRule = rule.relabel(oldLabel, newLabel);
            if (newRule != rule) {
                newRules.add(newRule);
            }
        }
        result.addEdit(doPutRules(newRules));
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
        List<AspectGraph> newGraphs =
            new ArrayList<AspectGraph>(getGraphs().size());
        for (AspectGraph graph : getGraphs().values()) {
            AspectGraph newGraph = graph.renumber();
            if (newGraph != graph) {
                newGraphs.add(newGraph);
            }
        }
        result.addEdit(doPutGraphs(newGraphs));
        for (AspectGraph type : getTypes().values()) {
            AspectGraph newType = type.renumber();
            if (newType != type) {
                Edit edit = doPutType(newType);
                result.addEdit(edit);
            }
        }
        List<AspectGraph> newRules =
            new ArrayList<AspectGraph>(getRules().size());
        for (AspectGraph rule : getRules().values()) {
            AspectGraph newRule = rule.renumber();
            if (newRule != rule) {
                newRules.add(newRule);
            }
        }
        result.addEdit(doPutRules(newRules));
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
        loadProlog();
        notifyObservers(new MyEdit(SystemStore.PROPERTIES_CHANGE
            | SystemStore.RULE_CHANGE | SystemStore.GRAPH_CHANGE
            | SystemStore.TYPE_CHANGE | SystemStore.CONTROL_CHANGE
            | SystemStore.PROLOG_CHANGE));
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

    public SystemStore save(File file, boolean clearDir) throws IOException {
        return save(file, this, clearDir);
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
            return LayedOutXml.getInstance();
        } else {
            return DefaultGxl.getInstance();
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
        enableTypes();
    }

    /**
     * Processes the {@link SystemProperties#getTypeNames()} field by
     * setting the enabling of the rtype graphs accordingly
     */
    private void enableTypes() {
        // enable the active types listed in the system properties
        Set<String> enabledTypes =
            new HashSet<String>(getProperties().getTypeNames());
        for (AspectGraph typeGraph : this.typeMap.values()) {
            GraphInfo.getProperties(typeGraph, true).setEnabled(
                enabledTypes.contains(typeGraph.getName()));
        }
    }

    /**
     * Collects all aspect graphs from the {@link #file} directory with a given
     * extension, and a given role.
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
                plainGraph.setRole(role);
                plainGraph.setName(graphName);
                AspectGraph graph = AspectGraph.newInstance(plainGraph);
                /* Store the graph */
                Object oldEntry = result.put(graphName, graph);
                assert oldEntry == null : String.format(
                    "Duplicate %s name '%s'", role, graphName);
            }
        }
    }

    /**
     * Loads the control programs.
     */
    private void loadControls() throws IOException {
        this.controlMap.clear();
        File[] files = this.file.listFiles(CONTROL_FILTER);
        // read in the control files
        for (File file : files) {
            // check for overlapping rule and directory names
            if (!file.isDirectory()) {
                // read the program in as a single string
                String program = groove.io.Util.readFileToString(file);
                // insert the string into the control map
                this.controlMap.put(
                    CONTROL_FILTER.stripExtension(file.getName()), program);
            }
        }
    }

    /**
     * Loads the control programs.
     */
    private void loadProlog() throws IOException {
        this.prologMap.clear();
        File[] files = this.file.listFiles(PROLOG_FILTER);
        // read in the prolog files
        for (File file : files) {
            // check for overlapping rule and directory names
            if (!file.isDirectory()) {
                // read the program in as a single string
                String program = groove.io.Util.readFileToString(file);
                // insert the string into the control map
                this.prologMap.put(
                    PROLOG_FILTER.stripExtension(file.getName()), program);
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
            this.hasSystemPropertiesFile = true;
        } else {
            this.hasSystemPropertiesFile = false;
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

    @Override
    public boolean isEmpty() {
        return !this.hasSystemPropertiesFile && this.graphMap.isEmpty()
            && this.typeMap.isEmpty() && this.ruleMap.isEmpty();
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

    private void saveProlog(String name, String program) throws IOException {
        File prologFile = createPrologFile(name);
        Writer writer = new FileWriter(prologFile);
        try {
            writer.write(program);
        } finally {
            writer.close();
        }
    }

    private void saveProperties() throws IOException {
        saveProperties(this.properties);
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
        enableTypes();
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
                    plainGraph.setRole(RULE);
                    plainGraph.setName(ruleName.toString());
                    AspectGraph ruleGraph = AspectGraph.newInstance(plainGraph);
                    /* Store the rule graph */
                    AspectGraph oldRule =
                        this.ruleMap.put(ruleName.toString(), ruleGraph);
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
     * Creates a file name from a given control program name. The file name
     * consists of the store location, the program, and the control extension.
     */
    private File createPrologFile(String controlName) {
        return new File(this.file, PROLOG_FILTER.addExtension(controlName));
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
    private File createRuleFile(String ruleName) {
        return new File(this.file, RULE_FILTER.addExtension(Groove.toString(
            new RuleName(ruleName).tokens(), "", "", Groove.FILE_SEPARATOR)));
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
    private final Map<String,AspectGraph> ruleMap =
        new TreeMap<String,AspectGraph>();
    /** The name-to-graph map of the source. */
    private final Map<String,AspectGraph> graphMap =
        new TreeMap<String,AspectGraph>();
    /** The name-to-types map of the source. */
    private final Map<String,AspectGraph> typeMap =
        new TreeMap<String,AspectGraph>();
    /** The name-to-control-program map of the source. */
    private final Map<String,String> controlMap = new TreeMap<String,String>();
    /** The name-to-prolog-program map of the source. */
    private final Map<String,String> prologMap = new TreeMap<String,String>();
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
    /** Flag whether this store contains a 'system.properties' file. */
    private boolean hasSystemPropertiesFile = false;

    /** Saves the content of a given system store to file. */
    static public SystemStore save(File file, SystemStore store,
            boolean clearDir) throws IOException {
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
            if (clearDir) {
                if (!file.renameTo(newFile)) {
                    throw new IOException(String.format(
                        "Can't save grammar to existing file '%s'", file));
                }
            } else {
                Util.copyDirectory(file, newFile, true);
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
            result.doPutGraphs(store.getGraphs().values());
            // save rules
            result.doPutRules(store.getRules().values());
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

        /** Returns a fresh set consisting of the names of a given set of graphs. */
        final protected Set<String> getNames(Collection<AspectGraph> graphs) {
            Set<String> result = new HashSet<String>();
            for (AspectGraph graph : graphs) {
                result.add(graph.getName());
            }
            return result;
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
            try {
                if (this.oldControl == null) {
                    doDeleteControl(this.name);
                } else {
                    doPutControl(this.name, this.oldControl);
                }
            } catch (IOException exc) {
                throw new CannotUndoException();
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

    /** Edit consisting of the renaming of a control program. */
    private class RenameControlEdit extends MyEdit {
        public RenameControlEdit(String oldName, String newName,
                SystemProperties oldProps, SystemProperties newProps,
                String oldControl) {
            super(CONTROL_CHANGE);
            this.oldName = oldName;
            this.newName = newName;
            this.oldProps = oldProps;
            this.newProps = newProps;
            this.oldControl = oldControl;
        }

        @Override
        public String getPresentationName() {
            return Options.RENAME_RULE_ACTION_NAME;
        }

        @Override
        public void redo() throws CannotRedoException {
            super.redo();
            try {
                if (this.newProps != null) {
                    doPutProperties(this.newProps);
                }
                doRenameControl(this.oldName, this.newName);
                notifyObservers(this);
            } catch (IOException exc) {
                throw new CannotRedoException();
            }
        }

        @Override
        public void undo() throws CannotUndoException {
            super.undo();
            try {
                if (this.oldProps != null) {
                    doPutProperties(this.oldProps);
                }
                doRenameControl(this.newName, this.oldName);
                notifyObservers(this);
            } catch (IOException exc) {
                throw new CannotUndoException();
            }
        }

        /** Returns the text of the renamed control program. */
        public final String getOldControl() {
            return this.oldControl;
        }

        /** The old name of the renamed control program. */
        private final String oldName;
        /** The new name of the renamed control program. */
        private final String newName;
        /** The text of the renamed control program. */
        private final String oldControl;
        /** The old system properties; possibly {@code null}. */
        private final SystemProperties oldProps;
        /** The new system properties; possibly {@code null}. */
        private final SystemProperties newProps;
    }

    /** Edit consisting of the addition of a control program. */
    private class PutPrologEdit extends MyEdit {
        public PutPrologEdit(String name, String oldProlog, String newProlog) {
            super(PROLOG_CHANGE);
            this.name = name;
            this.oldProlog = oldProlog;
            this.newProlog = newProlog;
        }

        @Override
        public String getPresentationName() {
            return this.oldProlog == null ? Options.NEW_PROLOG_ACTION_NAME
                    : Options.EDIT_PROLOG_ACTION_NAME;
        }

        @Override
        public void redo() throws CannotRedoException {
            super.redo();
            try {
                doPutProlog(this.name, this.newProlog);
            } catch (IOException exc) {
                throw new CannotRedoException();
            }
            notifyObservers(this);
        }

        @Override
        public void undo() throws CannotUndoException {
            super.undo();
            if (this.oldProlog == null) {
                doDeleteProlog(this.name);
            } else {
                try {
                    doPutProlog(this.name, this.oldProlog);
                } catch (IOException exc) {
                    throw new CannotUndoException();
                }
            }
            notifyObservers(this);
        }

        /** Returns the deleted control program, if any. */
        public final String getOldProlog() {
            return this.oldProlog;
        }

        /** The name of the deleted control program. */
        private final String name;
        /** The old control program with this name. */
        private final String oldProlog;
        /** The new control program with this name. */
        private final String newProlog;
    }

    /** Edit consisting of the deletion of a control program. */
    private class DeletePrologEdit extends MyEdit {
        public DeletePrologEdit(String name, String prolog) {
            super(CONTROL_CHANGE);
            this.name = name;
            this.prolog = prolog;
        }

        @Override
        public String getPresentationName() {
            return Options.DELETE_PROLOG_ACTION_NAME;
        }

        @Override
        public void redo() throws CannotRedoException {
            super.redo();
            doDeleteProlog(this.name);
            notifyObservers(this);
        }

        @Override
        public void undo() throws CannotUndoException {
            super.undo();
            try {
                doPutProlog(this.name, this.prolog);
            } catch (IOException exc) {
                throw new CannotUndoException();
            }
            notifyObservers(this);
        }

        /** Returns the deleted control program. */
        public final String getProlog() {
            return this.prolog;
        }

        /** The name of the deleted control program. */
        private final String name;
        /** The deleted control program. */
        private final String prolog;
    }

    /** Edit consisting of the renaming of a prolog program. */
    private class RenamePrologEdit extends MyEdit {
        public RenamePrologEdit(String oldName, String newName, String oldProlog) {
            super(CONTROL_CHANGE);
            this.oldName = oldName;
            this.newName = newName;
            this.oldProlog = oldProlog;
        }

        @Override
        public String getPresentationName() {
            return Options.RENAME_RULE_ACTION_NAME;
        }

        @Override
        public void redo() throws CannotRedoException {
            super.redo();
            doRenameProlog(this.oldName, this.newName);
            notifyObservers(this);
        }

        @Override
        public void undo() throws CannotUndoException {
            super.undo();
            doRenameProlog(this.newName, this.oldName);
            notifyObservers(this);
        }

        /** Returns the text of the renamed prolog program. */
        public final String getOldProlog() {
            return this.oldProlog;
        }

        /** The old name of the renamed prolog program. */
        private final String oldName;
        /** The new name of the renamed prolog program. */
        private final String newName;
        /** The text of the renamed prolog program. */
        private final String oldProlog;
    }

    /** Edit consisting of the addition (or replacement) of a graph. */
    private class GraphEdit extends MyEdit {
        public GraphEdit(Collection<AspectGraph> oldGraphs,
                Collection<AspectGraph> newGraphs) {
            super(GRAPH_CHANGE);
            this.oldGraphs = oldGraphs;
            this.newGraphs = newGraphs;
        }

        @Override
        public String getPresentationName() {
            if (this.oldGraphs.isEmpty()) {
                return Options.NEW_GRAPH_ACTION_NAME;
            } else if (this.newGraphs.isEmpty()) {
                return Options.DELETE_GRAPH_ACTION_NAME;
            } else if (this.newGraphs.size() == 1 && this.oldGraphs.size() == 1) {
                String oldRuleName = this.oldGraphs.iterator().next().getName();
                String newRuleName = this.newGraphs.iterator().next().getName();
                if (oldRuleName.equals(newRuleName)) {
                    return Options.EDIT_GRAPH_ACTION_NAME;
                } else {
                    return Options.RENAME_GRAPH_ACTION_NAME;
                }
            } else {
                return Options.CHANGE_GRAPHS_ACTION_NAME;
            }
        }

        @Override
        public void redo() throws CannotRedoException {
            super.redo();
            doDeleteGraphs(getNames(this.oldGraphs));
            try {
                doPutGraphs(this.newGraphs);
            } catch (IOException exc) {
                throw new CannotRedoException();
            }
            notifyObservers(this);
        }

        @Override
        public void undo() throws CannotUndoException {
            super.undo();
            doDeleteGraphs(getNames(this.newGraphs));
            try {
                doPutGraphs(this.oldGraphs);
            } catch (IOException exc) {
                throw new CannotUndoException();
            }
            notifyObservers(this);
        }

        /** Returns the deleted graphs. */
        public final Collection<AspectGraph> getOldGraphs() {
            return this.oldGraphs;
        }

        /** Returns the created graphs. */
        public final Collection<AspectGraph> getNewGraphs() {
            return this.newGraphs;
        }

        /** The deleted graph, if any. */
        private final Collection<AspectGraph> oldGraphs;
        /** The added graph. */
        private final Collection<AspectGraph> newGraphs;
    }

    /** Edit consisting adding and removing sets of rules. */
    private class RuleEdit extends MyEdit {
        public RuleEdit(Collection<AspectGraph> oldRules,
                Collection<AspectGraph> newRules) {
            super(RULE_CHANGE);
            this.oldRules = oldRules;
            this.newRules = newRules;
        }

        @Override
        public String getPresentationName() {
            if (this.oldRules.isEmpty()) {
                return Options.NEW_RULE_ACTION_NAME;
            } else if (this.newRules.isEmpty()) {
                return Options.DELETE_RULE_ACTION_NAME;
            } else if (this.newRules.size() == 1 && this.oldRules.size() == 1) {
                String oldRuleName = this.oldRules.iterator().next().getName();
                String newRuleName = this.newRules.iterator().next().getName();
                if (oldRuleName.equals(newRuleName)) {
                    return Options.EDIT_RULE_ACTION_NAME;
                } else {
                    return Options.RENAME_RULE_ACTION_NAME;
                }
            } else {
                return Options.CHANGE_RULES_ACTION_NAME;
            }
        }

        @Override
        public void redo() throws CannotRedoException {
            super.redo();
            doDeleteRules(getNames(this.oldRules));
            try {
                doPutRules(this.newRules);
            } catch (IOException exc) {
                throw new CannotRedoException();
            }
            notifyObservers(this);
        }

        @Override
        public void undo() throws CannotUndoException {
            super.undo();
            doDeleteRules(getNames(this.newRules));
            try {
                doPutRules(this.oldRules);
            } catch (IOException exc) {
                throw new CannotUndoException();
            }
            notifyObservers(this);
        }

        /** Returns the deleted rules, if any. */
        public final Collection<AspectGraph> getOldRules() {
            return this.oldRules;
        }

        /** Returns the created rules, if any. */
        public final Collection<AspectGraph> getNewRules() {
            return this.newRules;
        }

        /** The deleted rule, if any. */
        private final Collection<AspectGraph> oldRules;
        /** The added rule. */
        private final Collection<AspectGraph> newRules;
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
            try {
                if (this.oldType == null) {
                    doDeleteType(this.newType.getName());
                } else {
                    doPutType(this.oldType);
                }
            } catch (IOException exc) {
                throw new CannotUndoException();
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
                doDeleteType(this.type.getName());
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
}
