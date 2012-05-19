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

import static groove.io.FileType.GRAMMAR_FILTER;
import static groove.io.FileType.PROPERTIES_FILTER;
import static groove.trans.ResourceKind.HOST;
import static groove.trans.ResourceKind.PROPERTIES;
import static groove.trans.ResourceKind.RULE;
import static groove.trans.ResourceKind.TYPE;
import groove.graph.DefaultGraph;
import groove.graph.TypeLabel;
import groove.gui.EditType;
import groove.gui.Options;
import groove.io.ExtensionFilter;
import groove.io.FileType;
import groove.io.PriorityFileName;
import groove.io.xml.DefaultGxl;
import groove.io.xml.LayedOutXml;
import groove.io.xml.Xml;
import groove.trans.QualName;
import groove.trans.ResourceKind;
import groove.trans.SystemProperties;
import groove.util.Groove;
import groove.view.FormatException;
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
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

/**
 * Implementation based on {@link AspectGraph} representations of the rules and
 * graphs, and using a (default) <code>.gps</code> directory as persistent
 * storage.
 * @author Arend Rensink
 * @version $Revision $
 */
public class DefaultFileSystemStore extends SystemStore {
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
    public Map<String,String> getTexts(ResourceKind kind) {
        testInit();
        return Collections.unmodifiableMap(getTextMap(kind));
    }

    @Override
    public Map<String,String> putTexts(ResourceKind kind,
            Map<String,String> texts) throws IOException {
        Map<String,String> result = null;
        TextBasedEdit edit = doPutTexts(kind, texts);
        if (edit != null) {
            edit.checkAndSetVersion();
            postEdit(edit);
            result = edit.getOldTexts();
        }
        return result;
    }

    /**
     * Implements the functionality of {@link #putTexts(ResourceKind, Map)}.
     * Returns an undoable edit wrapping this functionality.
     */
    private TextBasedEdit doPutTexts(ResourceKind kind,
            Map<String,String> newTexts) throws IOException {
        testInit();
        Map<String,String> oldTexts = new HashMap<String,String>();
        for (Map.Entry<String,String> entry : newTexts.entrySet()) {
            String name = entry.getKey();
            String newText = entry.getValue();
            saveText(kind, name, newText);
            String oldText = getTextMap(kind).put(name, newText);
            if (oldText != null) {
                oldTexts.put(name, oldText);
            }
        }
        return new TextBasedEdit(kind, oldTexts.isEmpty() ? EditType.CREATE
                : EditType.MODIFY, oldTexts, newTexts, null, null);
    }

    @Override
    public Map<String,String> deleteTexts(ResourceKind kind,
            Collection<String> names) throws IOException {
        Map<String,String> result = null;
        TextBasedEdit deleteEdit = doDeleteTexts(kind, names);
        if (deleteEdit != null) {
            deleteEdit.checkAndSetVersion();
            postEdit(deleteEdit);
            result = deleteEdit.getOldTexts();
        }
        return result;
    }

    /**
     * Implements the functionality of the {@link #deleteTexts(ResourceKind, Collection)}
     * method. Returns a corresponding undoable edit.
     */
    private TextBasedEdit doDeleteTexts(ResourceKind kind,
            Collection<String> names) throws IOException {
        testInit();
        Map<String,String> oldTexts = new HashMap<String,String>();
        boolean activeChanged = false;
        Set<String> activeNames = new TreeSet<String>();
        switch (kind) {
        case PROLOG:
        case CONTROL:
            activeNames.addAll(getProperties().getEnabledNames(kind));
            break;
        }
        for (String name : names) {
            assert name != null;
            String text = getTextMap(kind).remove(name);
            if (text != null) {
                oldTexts.put(name, text);
                createFile(kind, name).delete();
                activeChanged |= activeNames.remove(name);
            }
        }
        // change the control-related system properties, if necessary
        SystemProperties oldProps = null;
        SystemProperties newProps = null;
        if (activeChanged) {
            oldProps = getProperties();
            newProps = getProperties().clone();
            switch (kind) {
            case PROLOG:
            case CONTROL:
                newProps.setEnabledNames(kind, activeNames);
            }
            doPutProperties(newProps);
        }
        return new TextBasedEdit(kind, EditType.DELETE, oldTexts,
            Collections.<String,String>emptyMap(), oldProps, newProps);
    }

    @Override
    public void rename(ResourceKind kind, String oldName, String newName)
        throws IOException {
        MyEdit edit =
            kind.isGraphBased() ? doRenameGraph(kind, oldName, newName)
                    : doRenameText(kind, oldName, newName);
        if (edit != null) {
            edit.checkAndSetVersion();
            postEdit(edit);
        }
    }

    /**
     * Implements the functionality of {@link #rename(ResourceKind, String, String)}
     * for text-based resources.
     * Returns an undoable edit wrapping this functionality.
     */
    private TextBasedEdit doRenameText(ResourceKind kind, String oldName,
            String newName) throws IOException {
        testInit();
        Map<String,String> oldTexts = new HashMap<String,String>();
        Map<String,String> newTexts = new HashMap<String,String>();
        String text = getTextMap(kind).remove(oldName);
        assert text != null;
        oldTexts.put(oldName, text);
        createFile(kind, oldName).renameTo(createFile(kind, newName));
        String previous = getTextMap(kind).put(newName, text);
        assert previous == null;
        newTexts.put(newName, text);
        // check if this affects the system properties
        SystemProperties oldProps = null;
        SystemProperties newProps = null;
        Set<String> activeNames = new TreeSet<String>();
        switch (kind) {
        case PROLOG:
        case CONTROL:
            activeNames.addAll(getProperties().getEnabledNames(kind));
        }
        if (activeNames.remove(oldName)) {
            oldProps = getProperties();
            newProps = getProperties().clone();
            switch (kind) {
            case PROLOG:
            case CONTROL:
                activeNames.add(newName);
                newProps.setEnabledNames(kind, activeNames);
            }
            doPutProperties(newProps);
        }
        return new TextBasedEdit(kind, EditType.RENAME, oldTexts, newTexts,
            oldProps, newProps);
    }

    /**
     * Implements the functionality of {@link #rename(ResourceKind, String, String)}
     * for graph-based resources.
     * Returns an undoable edit wrapping this functionality.
     */
    private GraphBasedEdit doRenameGraph(ResourceKind kind, String oldName,
            String newName) throws IOException {
        testInit();
        AspectGraph oldGraph = getGraphMap(kind).remove(oldName);
        assert oldGraph != null;
        File oldFile = createFile(kind, oldName);
        this.marshaller.deleteGraph(oldFile);
        AspectGraph newGraph = oldGraph.rename(newName);
        AspectGraph previous = getGraphMap(kind).put(newName, newGraph);
        assert previous == null;
        this.marshaller.marshalGraph(newGraph.toPlainGraph(),
            createFile(kind, newName));
        deleteEmptyDirectories(oldFile.getParentFile());
        // change the properties if there is a change in the enabled types
        SystemProperties oldProps = null;
        SystemProperties newProps = null;
        Set<String> typeNames = getProperties().getTypeNames();
        if (kind == TYPE && typeNames.contains(oldName)) {
            oldProps = getProperties();
            typeNames = new TreeSet<String>(typeNames);
            typeNames.remove(oldName);
            typeNames.add(newName);
            newProps = oldProps.clone();
            newProps.setTypeNames(typeNames);
            doPutProperties(newProps);
        }
        return new GraphBasedEdit(kind, EditType.RENAME,
            Collections.singleton(oldGraph), Collections.singleton(newGraph),
            oldProps, newProps);
    }

    /**
     * Recursively clean up empty directories, after a delete or a rename.
     */
    private static void deleteEmptyDirectories(File directory) {
        if (directory == null || !directory.isDirectory()) {
            return;
        }
        if (directory.listFiles().length > 0) {
            return;
        }
        File parent = directory.getParentFile();
        directory.delete();
        deleteEmptyDirectories(parent);
    }

    @Override
    public Map<String,AspectGraph> getGraphs(ResourceKind kind) {
        testInit();
        return Collections.unmodifiableMap(getGraphMap(kind));
    }

    @Override
    public Collection<AspectGraph> putGraphs(ResourceKind kind,
            Collection<AspectGraph> graphs) throws IOException {
        Collection<AspectGraph> result = Collections.emptySet();
        GraphBasedEdit edit = doPutGraphs(kind, graphs);
        if (edit != null) {
            edit.checkAndSetVersion();
            postEdit(edit);
            result = edit.getOldGraphs();
        }
        return result;
    }

    /**
     * Implements the functionality of {@link #putGraphs(ResourceKind, Collection)}. Returns
     * an undoable edit wrapping this functionality.
     */
    private GraphBasedEdit doPutGraphs(ResourceKind kind,
            Collection<AspectGraph> newGraphs) throws IOException {
        testInit();
        // if we're relabelling, it may be that there are already graphs
        // under the names of the new ones
        Set<AspectGraph> oldGraphs = new HashSet<AspectGraph>();
        for (AspectGraph newGraph : newGraphs) {
            String name = newGraph.getName();
            this.marshaller.marshalGraph(newGraph.toPlainGraph(),
                createFile(kind, name));
            AspectGraph oldGraph = getGraphMap(kind).put(name, newGraph);
            if (oldGraph != null) {
                oldGraphs.add(oldGraph);
            }
        }
        return new GraphBasedEdit(kind, oldGraphs.isEmpty() ? EditType.CREATE
                : EditType.MODIFY, oldGraphs, newGraphs, null, null);
    }

    @Override
    public Collection<AspectGraph> deleteGraphs(ResourceKind kind,
            Collection<String> name) throws IOException {
        Collection<AspectGraph> result = Collections.emptySet();
        GraphBasedEdit edit = doDeleteGraphs(kind, name);
        if (edit != null) {
            edit.checkAndSetVersion();
            postEdit(edit);
            result = edit.getOldGraphs();
        }
        return result;
    }

    /**
     * Implements the functionality of the {@link #deleteGraphs(ResourceKind, Collection)} method.
     * Returns a corresponding undoable edit.
     */
    private GraphBasedEdit doDeleteGraphs(ResourceKind kind,
            Collection<String> names) throws IOException {
        testInit();
        List<AspectGraph> deletedGraphs =
            new ArrayList<AspectGraph>(names.size());
        Set<String> typeNames =
            kind == TYPE ? new TreeSet<String>(getProperties().getTypeNames())
                    : Collections.<String>emptySet();
        boolean typesChanged = false;
        for (String name : names) {
            AspectGraph graph = getGraphMap(kind).remove(name);
            assert graph != null;
            File oldFile = createFile(kind, name);
            this.marshaller.deleteGraph(oldFile);
            deleteEmptyDirectories(oldFile.getParentFile());
            deletedGraphs.add(graph);
            typesChanged |= typeNames.remove(name);
        }
        SystemProperties oldProps = null;
        SystemProperties newProps = null;
        if (typesChanged) {
            oldProps = getProperties();
            newProps = getProperties().clone();
            newProps.setTypeNames(typeNames);
            doPutProperties(newProps);
        }
        return new GraphBasedEdit(kind, EditType.DELETE, deletedGraphs,
            Collections.<AspectGraph>emptySet(), oldProps, newProps);
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
        MyCompoundEdit result = new MyCompoundEdit(Options.REPLACE_ACTION_NAME);
        for (ResourceKind kind : EnumSet.allOf(ResourceKind.class)) {
            if (kind.isGraphBased()) {
                List<AspectGraph> newGraphs =
                    new ArrayList<AspectGraph>(getGraphs(kind).size());
                for (AspectGraph graph : getGraphs(kind).values()) {
                    AspectGraph newGraph = graph.relabel(oldLabel, newLabel);
                    if (newGraph != graph) {
                        newGraphs.add(newGraph);
                    }
                }
                result.addEdit(doPutGraphs(kind, newGraphs));
            }
        }
        SystemProperties newProperties =
            this.properties.relabel(oldLabel, newLabel);
        if (newProperties != this.properties) {
            Edit edit = doPutProperties(newProperties);
            result.addEdit(edit);
        }
        result.end();
        return result.getChange().isEmpty() ? null : result;
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
        for (ResourceKind kind : EnumSet.allOf(ResourceKind.class)) {
            if (kind.isGraphBased()) {
                List<AspectGraph> newGraphs =
                    new ArrayList<AspectGraph>(getGraphs(kind).size());
                for (AspectGraph graph : getGraphs(kind).values()) {
                    AspectGraph newGraph = graph.renumber();
                    if (newGraph != graph) {
                        newGraphs.add(newGraph);
                    }
                }
                result.addEdit(doPutGraphs(kind, newGraphs));
            }
        }
        result.end();
        return result.getChange().isEmpty() ? null : result;
    }

    @Override
    public void reload() throws IOException {
        for (ResourceKind kind : EnumSet.allOf(ResourceKind.class)) {
            if (kind == PROPERTIES) {
                loadProperties();
            } else if (kind.isTextBased()) {
                loadTexts(kind, kind.getFilter());
            } else {
                loadGraphs(kind, kind.getFilter());
            }
        }
        notifyObservers(new MyEdit(EditType.CREATE,
            EnumSet.allOf(ResourceKind.class)));
        this.initialised = true;
    }

    @Override
    public Object getLocation() {
        if (this.file == null) {
            return this.url;
        } else {
            return this.file;
        }
    }

    @Override
    public SystemStore save(File file, boolean clearDir) throws IOException {
        return SystemStore.save(file, this, clearDir);
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
     * Collects all aspect graphs from the {@link #file} directory with a given
     * extension, and a given role.
     */
    private void loadGraphs(ResourceKind kind, ExtensionFilter filter)
        throws IOException {
        getGraphMap(kind).clear();
        try {
            collectGraphs(kind, filter, this.file, null);
        } catch (FormatException e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Loads all corresponding graph resources from both the current directory
     * and all its subdirectories.
     * @param kind the {@link ResourceKind} to load
     * @param filter the {@link ExtensionFilter} of the resource
     * @param path the current path
     * @param pathName the name of the current path relative to the grammar
     * @throws IOException if an error occurs while loading a rule graph
     * @throws FormatException if there is a rule name with an error
     */
    private void collectGraphs(ResourceKind kind, ExtensionFilter filter,
            File path, QualName pathName) throws IOException, FormatException {

        // find all files in the current path 
        File[] files = path.listFiles(filter);
        if (files == null) {
            throw new IOException(LOAD_ERROR + ": unable to get list of files"
                + "in path " + path);
        }

        // process all files one by one
        for (File file : files) {
            // get name of file
            String fileName =
                new PriorityFileName(filter.stripExtension(file.getName())).getActualName();

            // recurse if the file is a directory
            if (file.isDirectory()) {
                if (!file.getName().startsWith(".")) {
                    QualName extendedPathName =
                        new QualName(pathName, fileName);
                    collectGraphs(kind, filter, file, extendedPathName);
                }
                continue;
            }

            // read graph from file
            DefaultGraph plainGraph = this.marshaller.unmarshalGraph(file);
            QualName graphName = new QualName(pathName, fileName);

            // backwards compatibility: set role and name
            plainGraph.setRole(kind.getGraphRole());
            plainGraph.setName(graphName.toString());

            // store graph in corresponding map
            AspectGraph graph = AspectGraph.newInstance(plainGraph);
            Object oldEntry =
                getGraphMap(kind).put(graphName.toString(), graph);
            assert oldEntry == null : String.format("Duplicate %s name '%s'",
                kind.getGraphRole(), graphName);

        }
    }

    private void loadTexts(ResourceKind kind, ExtensionFilter filter)
        throws IOException {
        getTextMap(kind).clear();
        File[] files = this.file.listFiles(filter);
        // read in the prolog files
        for (File file : files) {
            // check for overlapping rule and directory names
            if (!file.isDirectory()) {
                // read the program in as a single string
                String program = groove.io.Util.readFileToString(file);
                // insert the string into the control map
                getTextMap(kind).put(filter.stripExtension(file.getName()),
                    program);
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
    private SystemProperties loadGrammarProperties() throws IOException {
        SystemProperties properties = new SystemProperties();
        File propertiesFile = getDefaultPropertiesFile();
        // backwards compatibility: <grammar name>.properties
        if (!propertiesFile.exists()) {
            propertiesFile = getOldDefaultPropertiesFile();
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

    /** Returns the file that by default holds the system properties. */
    private File getDefaultPropertiesFile() {
        return new File(this.file,
            PROPERTIES_FILTER.addExtension(Groove.PROPERTY_NAME));
    }

    /** Returns the file that held the system properties in the distant past. */
    private File getOldDefaultPropertiesFile() {
        return new File(this.file, PROPERTIES_FILTER.addExtension(this.name));
    }

    private void saveText(ResourceKind kind, String name, String program)
        throws IOException {
        File file = createFile(kind, name);
        Writer writer = new FileWriter(file);
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
        File propertiesFile = getDefaultPropertiesFile();
        Writer propertiesWriter = new FileWriter(propertiesFile);
        try {
            properties.store(propertiesWriter, null);
        } finally {
            propertiesWriter.close();
        }
        // delete the old-style properties file, if any
        File oldPropertiesFile = getOldDefaultPropertiesFile();
        if (oldPropertiesFile.exists()) {
            oldPropertiesFile.delete();
        }
    }

    private void testInit() throws IllegalStateException {
        if (!this.initialised) {
            throw new IllegalStateException(
                "Operation should only be called after initialisation");
        }
    }

    /**
     * Creates a file name for a given resource kind.
     */
    private File createFile(ResourceKind kind, String name) {
        File basis = this.file;
        String shortName = name;
        if (kind == RULE || kind == HOST || kind == TYPE) {
            QualName ruleName = new QualName(name);
            for (int i = 0; i < ruleName.size() - 1; i++) {
                basis = new File(basis, ruleName.get(i));
                basis.mkdir();
            }
            shortName = ruleName.get(ruleName.size() - 1);
        }
        return new File(basis, kind.getFilter().addExtension(shortName));
    }

    /** Posts the edit, and also notifies the observers. */
    @Override
    public synchronized void postEdit(UndoableEdit e) {
        super.postEdit(e);
        notifyObservers((Edit) e);
    }

    @Override
    protected boolean hasSystemProperties() {
        return this.hasSystemPropertiesFile;
    }

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
    /** Flag whether this store contains a 'system.properties' file. */
    private boolean hasSystemPropertiesFile = false;

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
                url.toURI().getPath(), null, null));
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
        public MyEdit(EditType type, ResourceKind first, ResourceKind... rest) {
            this.type = type;
            this.change = EnumSet.of(first, rest);
            this.kind = first;
        }

        public MyEdit(EditType type, Set<ResourceKind> change) {
            this.type = type;
            this.change = change;
            this.kind = null;
        }

        /** Adds a resource kind to the ones that this edit is changing. */
        protected void addChange(ResourceKind kind) {
            this.change.add(kind);
        }

        @Override
        public Set<ResourceKind> getChange() {
            return this.change;
        }

        /** 
         * Returns the main resource kind affected by this edit.
         * Used to construct the presentation name of the edit.
         */
        public ResourceKind getResourceKind() {
            return this.kind;
        }

        @Override
        public EditType getType() {
            return this.type;
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

        /** The main resource kind of this edit. */
        private final ResourceKind kind;
        /** The type of this edit. */
        private final EditType type;
        /**
         * The change information in this edit.
         * @see #getChange()
         */
        private final Set<ResourceKind> change;

        private SystemProperties origProp = null;
    }

    /** Edit wrapping a relabelling. */
    private static class MyCompoundEdit extends CompoundEdit implements Edit {
        /** Constructs a compound edit with a given name. */
        public MyCompoundEdit(String presentationName) {
            this.presentationName = presentationName;
        }

        @Override
        public EditType getType() {
            return EditType.MODIFY;
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
                this.change.addAll(((Edit) anEdit).getChange());
            }
            return result;
        }

        @Override
        public Set<ResourceKind> getChange() {
            return this.change;
        }

        /**
         * The change information in this edit.
         * @see #getChange()
         */
        private final Set<ResourceKind> change =
            EnumSet.noneOf(ResourceKind.class);
        /** The name of this edit. */
        private final String presentationName;
    }

    /** Edit consisting of additions and deletions of text-based resources. */
    private class TextBasedEdit extends MyEdit {
        public TextBasedEdit(ResourceKind kind, EditType type,
                Map<String,String> oldTexts, Map<String,String> newTexts,
                SystemProperties oldProps, SystemProperties newProps) {
            super(type, kind);
            this.oldTexts = oldTexts;
            this.newTexts = newTexts;
            this.oldProps = oldProps;
            this.newProps = newProps;
            if (oldProps != null) {
                addChange(PROPERTIES);
            }
        }

        @Override
        public String getPresentationName() {
            String result =
                Options.getEditActionName(getType(), getResourceKind(), false);
            if (this.newTexts.size() > 1 || this.oldTexts.size() > 1) {
                result += "s";
            }
            return result;
        }

        @Override
        public void redo() throws CannotRedoException {
            super.redo();
            try {
                doDeleteTexts(getResourceKind(), this.oldTexts.keySet());
                if (this.newProps != null) {
                    doPutProperties(this.newProps);
                }
                doPutTexts(getResourceKind(), this.newTexts);
            } catch (IOException exc) {
                throw new CannotRedoException();
            }
            notifyObservers(this);
        }

        @Override
        public void undo() throws CannotUndoException {
            super.undo();
            try {
                doDeleteTexts(getResourceKind(), this.newTexts.keySet());
                if (this.oldProps != null) {
                    doPutProperties(this.oldProps);
                }
                doPutTexts(getResourceKind(), this.oldTexts);
            } catch (IOException exc) {
                throw new CannotUndoException();
            }
            notifyObservers(this);
        }

        /** Returns the deleted texts. */
        public final Map<String,String> getOldTexts() {
            return this.oldTexts;
        }

        /** The deleted texts, if any. */
        private final Map<String,String> oldTexts;
        /** The added texts. */
        private final Map<String,String> newTexts;
        /** The old system properties; possibly {@code null}. */
        private final SystemProperties oldProps;
        /** The new system properties; possibly {@code null}. */
        private final SystemProperties newProps;
    }

    /** Edit consisting of additions and deletions of graph-based resources. */
    private class GraphBasedEdit extends MyEdit {
        public GraphBasedEdit(ResourceKind kind, EditType type,
                Collection<AspectGraph> oldGraphs,
                Collection<AspectGraph> newGraphs, SystemProperties oldProps,
                SystemProperties newProps) {
            super(type, kind);
            this.oldGraphs = oldGraphs;
            this.newGraphs = newGraphs;
            this.oldProps = oldProps;
            this.newProps = newProps;
            if (oldProps != null) {
                addChange(PROPERTIES);
            }
        }

        @Override
        public String getPresentationName() {
            String result =
                Options.getEditActionName(getType(), getResourceKind(), false);
            if (this.newGraphs.size() > 1 || this.oldGraphs.size() > 1) {
                result += "s";
            }
            return result;
        }

        @Override
        public void redo() throws CannotRedoException {
            super.redo();
            try {
                doDeleteGraphs(getResourceKind(), getNames(this.oldGraphs));
                if (this.newProps != null) {
                    doPutProperties(this.newProps);
                }
                doPutGraphs(getResourceKind(), this.newGraphs);
            } catch (IOException exc) {
                throw new CannotRedoException();
            }
            notifyObservers(this);
        }

        @Override
        public void undo() throws CannotUndoException {
            super.undo();
            try {
                doDeleteGraphs(getResourceKind(), getNames(this.newGraphs));
                if (this.oldProps != null) {
                    doPutProperties(this.oldProps);
                }
                doPutGraphs(getResourceKind(), this.oldGraphs);
            } catch (IOException exc) {
                throw new CannotUndoException();
            }
            notifyObservers(this);
        }

        /** Returns the deleted graphs. */
        public final Collection<AspectGraph> getOldGraphs() {
            return this.oldGraphs;
        }

        /** The deleted graph, if any. */
        private final Collection<AspectGraph> oldGraphs;
        /** The added graph. */
        private final Collection<AspectGraph> newGraphs;
        /** The old system properties; possibly {@code null}. */
        private final SystemProperties oldProps;
        /** The new system properties; possibly {@code null}. */
        private final SystemProperties newProps;
    }

    /** Edit consisting of the addition of a control program. */
    private class PutPropertiesEdit extends MyEdit {
        public PutPropertiesEdit(SystemProperties oldProperties,
                SystemProperties newProperties) {
            super(EditType.MODIFY, PROPERTIES);
            for (ResourceKind kind : EnumSet.of(ResourceKind.PROLOG,
                ResourceKind.TYPE, ResourceKind.CONTROL)) {
                Set<String> oldNames = oldProperties.getEnabledNames(kind);
                Set<String> newNames = newProperties.getEnabledNames(kind);
                if (!oldNames.equals(newNames)) {
                    addChange(kind);
                }
            }
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
