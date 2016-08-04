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

import static groove.grammar.model.ResourceKind.PROPERTIES;
import static groove.grammar.model.ResourceKind.RULE;
import static groove.io.FileType.GRAMMAR;
import static groove.io.store.EditType.LAYOUT;

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
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import groove.grammar.GrammarProperties;
import groove.grammar.ModuleName;
import groove.grammar.QualName;
import groove.grammar.aspect.AspectGraph;
import groove.grammar.model.ResourceKind;
import groove.grammar.type.TypeLabel;
import groove.gui.Options;
import groove.io.ExtensionFilter;
import groove.io.FileType;
import groove.io.graph.AttrGraph;
import groove.io.graph.GxlIO;
import groove.util.Groove;
import groove.util.parse.FormatErrorSet;
import groove.util.parse.FormatException;

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
    public DefaultFileSystemStore(File file, boolean create) throws IOException {
        if (!file.exists()) {
            if (create) {
                if (!file.mkdirs()) {
                    throw new IOException(String.format("Could not create directory '%s'", file));
                }
            } else {
                throw new IllegalArgumentException(String.format("File '%s' does not exist", file));
            }
        }
        if (!file.isDirectory()) {
            throw new IllegalArgumentException(String.format("File '%s' is not a directory", file));
        }
        if (!GRAMMAR.hasExtension(file)) {
            throw new IllegalArgumentException(
                String.format("File '%s' does not refer to a production system", file));
        }
        this.file = file;
        this.name = GRAMMAR.stripExtension(this.file.getName());
        this.marshaller = GxlIO.instance();
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
    public DefaultFileSystemStore(URL location) throws IOException {
        this(toFile(location), false);
        this.url = location;
    }

    private void createVersionProperties() {
        GrammarProperties prop = new GrammarProperties();
        prop.setCurrentVersionProperties();
        try {
            this.saveProperties(prop);
            this.hasSystemPropertiesFile = true;
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not create properties file.");
        }
        prop.setShowLoopsAsLabels(false);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Map<QualName,String> getTexts(ResourceKind kind) {
        testInit();
        return Collections.unmodifiableMap(getTextMap(kind));
    }

    @Override
    public Map<QualName,String> putTexts(ResourceKind kind, Map<QualName,String> texts)
        throws IOException {
        Map<QualName,String> result = null;
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
    private TextBasedEdit doPutTexts(ResourceKind kind, Map<QualName,String> newTexts)
        throws IOException {
        testInit();
        Map<QualName,String> oldTexts = new HashMap<>();
        Set<QualName> newNames = new HashSet<>();
        for (Map.Entry<QualName,String> entry : newTexts.entrySet()) {
            QualName name = entry.getKey();
            String newText = entry.getValue();
            saveText(kind, name, newText);
            String oldText = getTextMap(kind).put(name, newText);
            if (oldText == null) {
                newNames.add(name);
            } else {
                oldTexts.put(name, oldText);
            }
        }
        GrammarProperties oldProps = getProperties();
        GrammarProperties newProps = doEnableDefaultName(kind, newNames);
        return new TextBasedEdit(kind, oldTexts.isEmpty() ? EditType.CREATE : EditType.MODIFY,
            oldTexts, newTexts, oldProps, newProps);
    }

    /**
     * Edits the system properties by setting the default name of a given
     * resource kind as active name, if a resource with that name is added
     * and no resource of that kind is currently active.
     * @return the new stored) properties, or {@code null} if no change was made
     */
    private GrammarProperties doEnableDefaultName(ResourceKind kind, Set<QualName> newNames)
        throws IOException {
        GrammarProperties result = null;
        QualName defaultName = kind.getDefaultName();
        if (defaultName != null && getProperties().getActiveNames(kind)
            .isEmpty() && newNames.contains(defaultName)) {
            result = getProperties().clone();
            result.setActiveNames(kind, Collections.singleton(defaultName));
            doPutProperties(result);
        }
        return result;
    }

    @Override
    public Map<QualName,String> deleteTexts(ResourceKind kind, Collection<QualName> names)
        throws IOException {
        Map<QualName,String> result = null;
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
    private TextBasedEdit doDeleteTexts(ResourceKind kind, Collection<QualName> names)
        throws IOException {
        testInit();
        Map<QualName,String> oldTexts = new HashMap<>();
        boolean activeChanged = false;
        Set<QualName> activeNames = new TreeSet<>(getProperties().getActiveNames(kind));
        for (QualName name : names) {
            assert name != null;
            String text = getTextMap(kind).remove(name);
            if (text != null) {
                oldTexts.put(name, text);
                createFile(kind, name).delete();
                activeChanged |= activeNames.remove(name);
            }
        }
        // change the control-related system properties, if necessary
        GrammarProperties oldProps = null;
        GrammarProperties newProps = null;
        if (activeChanged) {
            oldProps = getProperties();
            newProps = getProperties().clone();
            newProps.setActiveNames(kind, activeNames);
            doPutProperties(newProps);
        }
        return new TextBasedEdit(kind, EditType.DELETE, oldTexts,
            Collections.<QualName,String>emptyMap(), oldProps, newProps);
    }

    @Override
    public void rename(ResourceKind kind, QualName oldName, QualName newName) throws IOException {
        MyEdit edit = kind.isGraphBased() ? doRenameGraph(kind, oldName, newName)
            : doRenameText(kind, oldName, newName);
        if (edit != null) {
            edit.checkAndSetVersion();
            postEdit(edit);
        }
    }

    /**
     * Implements the functionality of {@link #rename(ResourceKind, QualName, QualName)}
     * for text-based resources.
     * Returns an undoable edit wrapping this functionality.
     */
    private TextBasedEdit doRenameText(ResourceKind kind, QualName oldName, QualName newName)
        throws IOException {
        testInit();
        Map<QualName,String> oldTexts = new HashMap<>();
        Map<QualName,String> newTexts = new HashMap<>();
        String text = getTextMap(kind).remove(oldName);
        assert text != null;
        oldTexts.put(oldName, text);
        createFile(kind, oldName).renameTo(createFile(kind, newName));
        String previous = getTextMap(kind).put(newName, text);
        assert previous == null;
        newTexts.put(newName, text);
        // check if this affects the system properties
        GrammarProperties oldProps = null;
        GrammarProperties newProps = null;
        Set<QualName> activeNames = new TreeSet<>(getProperties().getActiveNames(kind));
        if (activeNames.remove(oldName)) {
            oldProps = getProperties();
            newProps = getProperties().clone();
            activeNames.add(newName);
            newProps.setActiveNames(kind, activeNames);
            doPutProperties(newProps);
        }
        return new TextBasedEdit(kind, EditType.RENAME, oldTexts, newTexts, oldProps, newProps);
    }

    /**
     * Implements the functionality of {@link #rename(ResourceKind, QualName, QualName)}
     * for graph-based resources.
     * Returns an undoable edit wrapping this functionality.
     */
    private GraphBasedEdit doRenameGraph(ResourceKind kind, QualName oldName, QualName newName)
        throws IOException {
        testInit();
        AspectGraph oldGraph = getGraphMap(kind).remove(oldName);
        assert oldGraph != null;
        File oldFile = createFile(kind, oldName);
        this.marshaller.deleteGraph(oldFile);
        AspectGraph newGraph = oldGraph.rename(newName);
        AspectGraph previous = getGraphMap(kind).put(newName, newGraph);
        assert previous == null;
        this.marshaller.saveGraph(newGraph.toPlainGraph(), createFile(kind, newName));
        deleteEmptyDirectories(oldFile.getParentFile());
        // change the properties if there is a change in the enabled types
        GrammarProperties oldProps = null;
        GrammarProperties newProps = null;
        if (kind != RULE) {
            Set<QualName> activeNames = new TreeSet<>(getProperties().getActiveNames(kind));
            if (activeNames.remove(oldName)) {
                oldProps = getProperties();
                newProps = oldProps.clone();
                activeNames.add(newName);
                newProps.setActiveNames(kind, activeNames);
                doPutProperties(newProps);
            }
        }
        return new GraphBasedEdit(kind, EditType.RENAME, Collections.singleton(oldGraph),
            Collections.singleton(newGraph), oldProps, newProps);
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
    public Map<QualName,AspectGraph> getGraphs(ResourceKind kind) {
        testInit();
        return Collections.unmodifiableMap(getGraphMap(kind));
    }

    @Override
    public Collection<AspectGraph> putGraphs(ResourceKind kind, Collection<AspectGraph> graphs,
        boolean layout) throws IOException {
        Collection<AspectGraph> result = Collections.emptySet();
        GraphBasedEdit edit = doPutGraphs(kind, graphs, layout);
        if (edit != null) {
            edit.checkAndSetVersion();
            postEdit(edit);
            result = edit.getOldGraphs();
        }
        return result;
    }

    /**
     * Implements the functionality of {@link #putGraphs(ResourceKind, Collection, boolean)}. Returns
     * an undoable edit wrapping this functionality.
     * @param layout flag indicating that this is a layout change only,
     * which should be propagated as {@link EditType#LAYOUT}.
     */
    private GraphBasedEdit doPutGraphs(ResourceKind kind, Collection<AspectGraph> newGraphs,
        boolean layout) throws IOException {
        testInit();
        Set<QualName> newNames = new HashSet<>();
        // if we're relabelling, it may be that there are already graphs
        // under the names of the new ones
        Set<AspectGraph> oldGraphs = new HashSet<AspectGraph>();
        for (AspectGraph newGraph : newGraphs) {
            QualName name = newGraph.getQualName();
            this.marshaller.saveGraph(newGraph.toPlainGraph(), createFile(kind, name));
            AspectGraph oldGraph = getGraphMap(kind).put(name, newGraph);
            if (oldGraph == null) {
                newNames.add(name);
            } else {
                oldGraphs.add(oldGraph);
            }
        }
        GrammarProperties oldProps = getProperties();
        GrammarProperties newProps = doEnableDefaultName(kind, newNames);
        EditType type;
        if (oldGraphs.isEmpty()) {
            type = EditType.CREATE;
        } else if (layout) {
            type = EditType.LAYOUT;
        } else {
            type = EditType.MODIFY;
        }
        return new GraphBasedEdit(kind, type, oldGraphs, newGraphs, oldProps, newProps);
    }

    @Override
    public Collection<AspectGraph> deleteGraphs(ResourceKind kind, Collection<QualName> names)
        throws IOException {
        Collection<AspectGraph> result = Collections.emptySet();
        GraphBasedEdit edit = doDeleteGraphs(kind, names);
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
    private GraphBasedEdit doDeleteGraphs(ResourceKind kind, Collection<QualName> names)
        throws IOException {
        testInit();
        List<AspectGraph> deletedGraphs = new ArrayList<AspectGraph>(names.size());
        Set<QualName> activeNames =
            kind == RULE ? null : new TreeSet<>(getProperties().getActiveNames(kind));
        boolean activeChanged = false;
        for (QualName name : names) {
            AspectGraph graph = getGraphMap(kind).remove(name);
            assert graph != null;
            File oldFile = createFile(kind, name);
            this.marshaller.deleteGraph(oldFile);
            deleteEmptyDirectories(oldFile.getParentFile());
            deletedGraphs.add(graph);
            activeChanged |= activeNames != null && activeNames.remove(name);
        }
        GrammarProperties oldProps = null;
        GrammarProperties newProps = null;
        if (activeChanged) {
            oldProps = getProperties();
            newProps = getProperties().clone();
            newProps.setActiveNames(kind, activeNames);
            doPutProperties(newProps);
        }
        return new GraphBasedEdit(kind, EditType.DELETE, deletedGraphs,
            Collections.<AspectGraph>emptySet(), oldProps, newProps);
    }

    @Override
    public GrammarProperties getProperties() {
        GrammarProperties properties = null;
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
    public void putProperties(GrammarProperties properties) throws IOException {
        PutPropertiesEdit edit = doPutProperties(properties);
        if (edit != null) {
            edit.checkAndSetVersion();
            postEdit(edit);
        }
    }

    /**
     * Implements the functionality of {@link #putProperties(GrammarProperties)}.
     * Returns an undoable edit wrapping this functionality.
     */
    private PutPropertiesEdit doPutProperties(GrammarProperties properties) throws IOException {
        testInit();
        GrammarProperties oldProperties = this.properties;
        this.properties = properties;
        saveProperties();
        return new PutPropertiesEdit(oldProperties, properties);
    }

    @Override
    public void relabel(TypeLabel oldLabel, TypeLabel newLabel) throws IOException {
        Edit edit = doRelabel(oldLabel, newLabel);
        if (edit != null) {
            postEdit(edit);
        }
    }

    /**
     * Implements the functionality of {@link #relabel(TypeLabel, TypeLabel)}. Returns
     * an undoable edit wrapping this functionality.
     */
    private MyCompoundEdit doRelabel(TypeLabel oldLabel, TypeLabel newLabel) throws IOException {
        MyCompoundEdit result = new MyCompoundEdit(Options.REPLACE_ACTION_NAME);
        for (ResourceKind kind : ResourceKind.values()) {
            if (kind.isGraphBased()) {
                List<AspectGraph> newGraphs = new ArrayList<AspectGraph>(getGraphs(kind).size());
                for (AspectGraph graph : getGraphs(kind).values()) {
                    AspectGraph newGraph = graph.relabel(oldLabel, newLabel);
                    if (newGraph != graph) {
                        newGraphs.add(newGraph);
                    }
                }
                result.addEdit(doPutGraphs(kind, newGraphs, false));
            }
        }
        GrammarProperties newProperties = this.properties.relabel(oldLabel, newLabel);
        if (newProperties != this.properties) {
            Edit edit = doPutProperties(newProperties);
            result.addEdit(edit);
        }
        result.end();
        return result.getChange()
            .isEmpty() ? null : result;
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
        MyCompoundEdit result = new MyCompoundEdit(Options.RENUMBER_ACTION_NAME);
        for (ResourceKind kind : ResourceKind.values()) {
            if (kind.isGraphBased()) {
                List<AspectGraph> newGraphs = new ArrayList<AspectGraph>(getGraphs(kind).size());
                for (AspectGraph graph : getGraphs(kind).values()) {
                    AspectGraph newGraph = graph.renumber();
                    if (newGraph != graph) {
                        newGraphs.add(newGraph);
                    }
                }
                result.addEdit(doPutGraphs(kind, newGraphs, false));
            }
        }
        result.end();
        return result.getChange()
            .isEmpty() ? null : result;
    }

    @Override
    public void reload() throws IOException {
        for (ResourceKind kind : ResourceKind.values()) {
            if (kind == PROPERTIES) {
                loadProperties();
            } else if (kind.isTextBased()) {
                loadTexts(kind);
            } else {
                loadGraphs(kind);
            }
        }
        notifyObservers(new MyEdit(EditType.CREATE, EnumSet.allOf(ResourceKind.class)));
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
            && ((DefaultFileSystemStore) obj).getLocation()
                .equals(getLocation());
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
        String location = this.file == null ? getLocation().toString() : this.file.getParent();
        return getName() + " - " + location;
    }

    /**
     * Collects all aspect graphs from the {@link #file} directory with a given
     * extension, and a given role.
     */
    private void loadGraphs(ResourceKind kind) throws IOException {
        getGraphMap(kind).clear();
        Map<QualName,File> files;
        try {
            // read in the text files
            files = collectResources(kind, this.file, ModuleName.TOP);
        } catch (FormatException e) {
            throw new IOException(e.getMessage(), e);
        }
        for (Entry<QualName,File> fileEntry : files.entrySet()) {
            // read graph from file
            AttrGraph xmlGraph = this.marshaller.loadGraph(fileEntry.getValue());

            // backwards compatibility: set role and name
            xmlGraph.setRole(kind.getGraphRole());
            xmlGraph.setName(fileEntry.getKey()
                .toString());

            // store graph in corresponding map
            AspectGraph graph = xmlGraph.toAspectGraph();
            Object oldEntry = getGraphMap(kind).put(fileEntry.getKey(), graph);
            assert oldEntry == null : String.format("Duplicate %s name '%s'",
                kind.getGraphRole(),
                fileEntry.getKey());
        }
    }

    /**
     * Collects all text resources from the {@link #file} directory with a given
     * extension, and a given kind.
     */
    private void loadTexts(ResourceKind kind) throws IOException {
        getTextMap(kind).clear();
        Map<QualName,File> files;
        try {
            // read in the text files
            files = collectResources(kind, this.file, ModuleName.TOP);
        } catch (FormatException e) {
            throw new IOException(e.getMessage(), e);
        }
        for (Entry<QualName,File> fileEntry : files.entrySet()) {
            // read the file in as a single string
            String program = groove.io.Util.readFileToString(fileEntry.getValue());
            // insert the string into the resource map
            getTextMap(kind).put(fileEntry.getKey(), program);
        }
    }

    /**
     * Find all corresponding resources from both the current directory
     * and all its subdirectories.
     * Filenames with embedded separators are automatically renamed to use subdirectories instead
     * @param kind The kind of resource to load, with the {@link ExtensionFilter} of the resource
     * @param path the current path
     * @param pathName the name of the current path relative to the grammar; non-{@code null}
     * @return The map of files and their qualified names matching the given filter, not including directories
     * @throws IOException if an error occurs while trying to list the files
     * @throws FormatException if there is a subdirectory name with an error
     */
    private Map<QualName,File> collectResources(ResourceKind kind, File path, ModuleName pathName)
        throws IOException, FormatException {
        Map<QualName,File> result = new HashMap<QualName,File>();
        // find all files in the current path
        File[] curfiles = path.listFiles(kind.getFileType()
            .getFilter());
        if (curfiles == null) {
            throw new IOException(
                LOAD_ERROR + ": unable to get list of files " + "in path " + path);
        }
        // collect errors while loading files
        FormatErrorSet errors = new FormatErrorSet();
        // process all files one by one
        for (File file : curfiles) {
            // get qualified name of file
            String fileName = kind.getFileType()
                .stripExtension(file.getName());
            QualName qualFileName = pathName.extend(fileName)
                .testValid();
            if (file.isDirectory()) {
                result.putAll(collectResources(kind, file, qualFileName));
            } else {
                result.put(qualFileName, file);
            }
        }
        errors.throwException();
        return result;
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
    private GrammarProperties loadGrammarProperties() throws IOException {
        GrammarProperties properties = new GrammarProperties();
        File propertiesFile = getDefaultPropertiesFile();
        // backwards compatibility: <grammar name>.properties
        if (!propertiesFile.exists()) {
            propertiesFile = getOldDefaultPropertiesFile();
        }
        if (propertiesFile.exists()) {
            Properties grammarProperties = new Properties();
            try (InputStream s = new FileInputStream(propertiesFile)) {
                grammarProperties.load(s);
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
        return new File(this.file, PROPERTIES.getFileType()
            .addExtension(Groove.PROPERTY_NAME));
    }

    /** Returns the file that held the system properties in the distant past. */
    private File getOldDefaultPropertiesFile() {
        return new File(this.file, PROPERTIES.getFileType()
            .addExtension(this.name));
    }

    private void saveText(ResourceKind kind, QualName name, String program) throws IOException {
        File file = createFile(kind, name);
        try (Writer writer = new FileWriter(file)) {
            writer.write(program);
        }
    }

    private void saveProperties() throws IOException {
        saveProperties(this.properties);
    }

    private void saveProperties(GrammarProperties properties) throws IOException {
        File propertiesFile = getDefaultPropertiesFile();
        try (Writer propertiesWriter = new FileWriter(propertiesFile)) {
            properties.store(propertiesWriter, null);
        }
        // delete the old-style properties file, if any
        File oldPropertiesFile = getOldDefaultPropertiesFile();
        if (oldPropertiesFile.exists()) {
            oldPropertiesFile.delete();
        }
    }

    private void testInit() throws IllegalStateException {
        if (!this.initialised) {
            throw new IllegalStateException("Operation should only be called after initialisation");
        }
    }

    /**
     * Creates a file name for a given resource kind.
     */
    private File createFile(ResourceKind kind, QualName name) {
        File basis = this.file;
        for (int i = 0; i < name.size() - 1; i++) {
            basis = new File(basis, name.get(i));
            basis.mkdir();
        }
        String shortName = name.last();
        return new File(basis, kind.getFileType()
            .addExtension(shortName));
    }

    /** Posts the edit, and also notifies the observers. */
    @Override
    public synchronized void postEdit(UndoableEdit e) {
        if (!isUndoSuspended()) {
            super.postEdit(e);
            notifyObservers((Edit) e);
        }
    }

    @Override
    protected boolean hasSystemProperties() {
        return this.hasSystemPropertiesFile;
    }

    private GrammarProperties properties;
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
    private final GxlIO marshaller;
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
            return new File(new URI(url.getProtocol(), url.getAuthority(), url.toURI()
                .getPath(), null, null));
        } catch (URISyntaxException exc) {
            throw new IllegalArgumentException(
                String.format("URL '%s' is not formatted correctly: %s", url, exc.getMessage()));
        } catch (IllegalArgumentException exc) {
            throw new IllegalArgumentException(
                String.format("URL '%s' is not a valid file: %s", url, exc.getMessage()));
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
        final protected Set<QualName> getNames(Collection<AspectGraph> graphs) {
            Set<QualName> result = new HashSet<>();
            for (AspectGraph graph : graphs) {
                result.add(graph.getQualName());
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

        private GrammarProperties origProp = null;
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
        private final Set<ResourceKind> change = EnumSet.noneOf(ResourceKind.class);
        /** The name of this edit. */
        private final String presentationName;
    }

    /** Edit consisting of additions and deletions of text-based resources. */
    private class TextBasedEdit extends MyEdit {
        public TextBasedEdit(ResourceKind kind, EditType type, Map<QualName,String> oldTexts,
            Map<QualName,String> newTexts, GrammarProperties oldProps, GrammarProperties newProps) {
            super(type, kind);
            this.oldTexts = oldTexts;
            this.newTexts = newTexts;
            this.newProps = newProps;
            // properties only changed if newProps is non-null
            if (newProps == null) {
                this.oldProps = null;
            } else {
                this.oldProps = oldProps;
                addChange(PROPERTIES);
            }
        }

        @Override
        public String getPresentationName() {
            String result = Options.getEditActionName(getType(), getResourceKind(), false);
            if (this.newTexts.size() > 1 || this.oldTexts.size() > 1) {
                result += "s";
            }
            return result;
        }

        @Override
        public void redo() throws CannotRedoException {
            super.redo();
            try {
                Set<QualName> deleted = new HashSet<>(this.oldTexts.keySet());
                deleted.removeAll(this.newTexts.keySet());
                doDeleteTexts(getResourceKind(), deleted);
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
                Set<QualName> deleted = new HashSet<>(this.newTexts.keySet());
                deleted.removeAll(this.oldTexts.keySet());
                doDeleteTexts(getResourceKind(), deleted);
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
        public final Map<QualName,String> getOldTexts() {
            return this.oldTexts;
        }

        /** The deleted texts, if any. */
        private final Map<QualName,String> oldTexts;
        /** The added texts. */
        private final Map<QualName,String> newTexts;
        /** The old system properties; possibly {@code null}. */
        private final GrammarProperties oldProps;
        /** The new system properties; possibly {@code null}. */
        private final GrammarProperties newProps;
    }

    /** Edit consisting of additions and deletions of graph-based resources. */
    private class GraphBasedEdit extends MyEdit {
        public GraphBasedEdit(ResourceKind kind, EditType type, Collection<AspectGraph> oldGraphs,
            Collection<AspectGraph> newGraphs, GrammarProperties oldProps,
            GrammarProperties newProps) {
            super(type, kind);
            this.oldGraphs = oldGraphs;
            this.newGraphs = newGraphs;
            this.newProps = newProps;
            // properties only changed if newProps is non-null
            if (newProps == null) {
                this.oldProps = null;
            } else {
                this.oldProps = oldProps;
                addChange(PROPERTIES);
            }
        }

        @Override
        public String getPresentationName() {
            String result = Options.getEditActionName(getType(), getResourceKind(), false);
            if (this.newGraphs.size() > 1 || this.oldGraphs.size() > 1) {
                result += "s";
            }
            return result;
        }

        @Override
        public void redo() throws CannotRedoException {
            super.redo();
            try {
                boolean layout = getType() == LAYOUT;
                if (!layout) {
                    Set<QualName> deleted = getNames(this.oldGraphs);
                    deleted.removeAll(getNames(this.newGraphs));
                    doDeleteGraphs(getResourceKind(), deleted);
                    if (this.newProps != null) {
                        doPutProperties(this.newProps);
                    }
                }
                doPutGraphs(getResourceKind(), this.newGraphs, layout);
            } catch (IOException exc) {
                throw new CannotRedoException();
            }
            notifyObservers(this);
        }

        @Override
        public void undo() throws CannotUndoException {
            super.undo();
            try {
                boolean layout = getType() == LAYOUT;
                if (!layout) {
                    Set<QualName> deleted = getNames(this.newGraphs);
                    deleted.removeAll(getNames(this.oldGraphs));
                    doDeleteGraphs(getResourceKind(), deleted);
                    if (this.oldProps != null) {
                        doPutProperties(this.oldProps);
                    }
                }
                doPutGraphs(getResourceKind(), this.oldGraphs, layout);
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
        private final GrammarProperties oldProps;
        /** The new system properties; possibly {@code null}. */
        private final GrammarProperties newProps;
    }

    /** Edit consisting of changing the grammar properties. */
    private class PutPropertiesEdit extends MyEdit {
        public PutPropertiesEdit(GrammarProperties oldProperties, GrammarProperties newProperties) {
            super(EditType.MODIFY, PROPERTIES);
            for (ResourceKind kind : EnumSet.of(ResourceKind.PROLOG,
                ResourceKind.TYPE,
                ResourceKind.HOST,
                ResourceKind.CONTROL)) {
                Set<QualName> oldNames = oldProperties.getActiveNames(kind);
                Set<QualName> newNames = newProperties.getActiveNames(kind);
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
        private final GrammarProperties oldProperties;
        /** The new control program with this name. */
        private final GrammarProperties newProperties;
    }
}
