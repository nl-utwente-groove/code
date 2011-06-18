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
package groove.io.store;

import static groove.io.FileType.GRAMMAR_FILTER;
import static groove.io.FileType.JAR_FILTER;
import static groove.io.FileType.LAYOUT_FILTER;
import static groove.io.FileType.PROPERTIES_FILTER;
import static groove.io.FileType.RULE_FILTER;
import static groove.io.FileType.ZIP_FILTER;
import static groove.trans.ResourceKind.PROPERTIES;
import static groove.trans.ResourceKind.RULE;
import static groove.trans.ResourceKind.TYPE;
import groove.graph.DefaultEdge;
import groove.graph.DefaultGraph;
import groove.graph.DefaultNode;
import groove.graph.GraphInfo;
import groove.graph.TypeLabel;
import groove.gui.EditType;
import groove.gui.layout.LayoutMap;
import groove.io.ExtensionFilter;
import groove.io.FileType;
import groove.io.LayoutIO;
import groove.io.xml.JaxbGxlIO;
import groove.trans.ResourceKind;
import groove.trans.RuleName;
import groove.trans.SystemProperties;
import groove.util.Groove;
import groove.util.Pair;
import groove.view.FormatException;
import groove.view.aspect.AspectGraph;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.undo.AbstractUndoableEdit;

/**
 * Implementation based on {@link AspectGraph} representations of the rules and
 * graphs, and using a (default) <code>.gps</code> directory as persistent
 * storage.
 * @author Arend Rensink
 * @version $Revision $
 */
public class DefaultArchiveSystemStore extends SystemStore { //UndoableEditSupport implements SystemStore {
    /**
     * Constructs a store from a given file. The file should be a JAR or ZIP
     * file containing a single subdirectory with extension
     * {@link FileType#GRAMMAR}. The store is immutable.
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
        this.location = file.toString();
        this.file = file;
        this.entryName = extendedName;
        this.grammarName = GRAMMAR_FILTER.stripExtension(extendedName);
        this.url = null;
    }

    /**
     * Constructs a store from a given URL. The URL should either specify the
     * <code>jar:</code> protocol, in which case the sub-URL should point to a
     * JAR or ZIP file; or the URL should itself specify a JAR or ZIP file.
     * Query and reference part of the URL are ignored. The store is immutable.
     * @param url source location of the underlying persistent storage; should
     *        refer to a ZIP or JAR file.
     * @throws IllegalArgumentException if <code>location</code> does not
     *         specify the correct protocol
     * @throws IOException if the URL has the JAR protocol, but cannot be opened
     */
    public DefaultArchiveSystemStore(URL url) throws IllegalArgumentException,
        IOException {
        // first strip query and anchor part
        try {
            url = new URL(url.getProtocol(), url.getAuthority(), url.getPath());
        } catch (MalformedURLException exc) {
            assert false : String.format(
                "Stripping URL '%s' throws exception: %s", url, exc);
        }
        this.location = url.toString();
        // artificially append the jar protocol, if it is not yet there
        if (!url.getProtocol().equals(JAR_PROTOCOL)) {
            if (!JAR_FILTER.hasExtension(url.getPath())
                && !ZIP_FILTER.hasExtension(url.getPath())) {
                throw new IllegalArgumentException(String.format(
                    "URL '%s' is not a JAR or ZIP file", url));
            }
            url = new URL(JAR_PROTOCOL, null, url.toString() + "!/");
        }
        this.entryName = extractEntryName(url);
        // take the last part of the entry name as grammar name
        File fileFromEntry = new File(this.entryName);
        this.grammarName =
            GRAMMAR_FILTER.stripExtension(fileFromEntry.getName());
        this.url = url;
        this.file = null;
    }

    /**
     * Extracts the entry name for the grammar from a given JAR or ZIP URL. The
     * name is either given in the JAR entry part of the URL, or it is taken to
     * be the name of the archive file.
     * @param url the URL to be parsed; guaranteed to be a JAR or ZIP
     * @return the name; non-null
     * @throws IllegalArgumentException if no name can be found according to the
     *         above rules
     * @throws IOException if the URL cannot be opened
     */
    private String extractEntryName(URL url) throws IOException {
        String result;
        JarURLConnection connection = (JarURLConnection) url.openConnection();
        result = connection.getEntryName();
        if (result == null) {
            result =
                ExtensionFilter.getPureName(new File(
                    connection.getJarFileURL().getPath()));
        }
        return result;
    }

    @Override
    public String getName() {
        return this.grammarName;
    }

    @Override
    public Map<String,AspectGraph> getGraphs(ResourceKind kind) {
        testInit();
        return Collections.unmodifiableMap(getGraphMap(kind));
    }

    @Override
    public Collection<AspectGraph> putGraphs(ResourceKind kind,
            Collection<AspectGraph> graphs) throws IOException {
        throw createImmutable();
    }

    @Override
    public Collection<AspectGraph> deleteGraphs(ResourceKind kind,
            Collection<String> names) throws IOException {
        throw createImmutable();
    }

    @Override
    public Map<String,String> getTexts(ResourceKind kind) {
        testInit();
        return Collections.unmodifiableMap(getTextMap(kind));
    }

    @Override
    public Map<String,String> putTexts(ResourceKind kind,
            Map<String,String> texts) throws IOException {
        throw createImmutable();
    }

    @Override
    public Map<String,String> deleteTexts(ResourceKind kind,
            Collection<String> names) throws IOException {
        throw createImmutable();
    }

    @Override
    public void rename(ResourceKind kind, String oldName, String newName)
        throws IOException {
        throw createImmutable();
    }

    @Override
    public SystemProperties getProperties() {
        testInit();
        return this.properties;
    }

    @Override
    public void putProperties(SystemProperties properties) throws IOException {
        throw createImmutable();
    }

    @Override
    public void relabel(TypeLabel oldLabel, TypeLabel newLabel)
        throws IOException {
        throw createImmutable();
    }

    /** Creates an exception stating that the archive is immutable. */
    private IOException createImmutable() {
        return new IOException(
            String.format(
                "Archived grammar '%s' is immutable.%nSave locally to allow changes.",
                getName()));
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
        Map<ResourceKind,Map<String,ZipEntry>> zipEntryMap =
            new EnumMap<ResourceKind,Map<String,ZipEntry>>(ResourceKind.class);
        for (ResourceKind kind : EnumSet.allOf(ResourceKind.class)) {
            zipEntryMap.put(kind, new HashMap<String,ZipEntry>());
        }
        ZipEntry properties = null;
        for (Enumeration<? extends ZipEntry> entries = zipFile.entries(); entries.hasMoreElements();) {
            ZipEntry entry = entries.nextElement();
            String entryName = entry.getName();
            int entryPrefixLength = this.entryName.length() + 1;
            if (entryName.startsWith(this.entryName)
                && entryName.length() > entryPrefixLength) {
                // strip off prefix + 1 to take case of file separator
                String restName = entryName.substring(entryPrefixLength);
                // find out the resource kind by testing the extension
                ResourceKind kind = null;
                for (ResourceKind tryKind : EnumSet.allOf(ResourceKind.class)) {
                    if (restName.endsWith(tryKind.getFilter().getExtension())) {
                        kind = tryKind;
                        break;
                    }
                }
                if (kind == PROPERTIES) {
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
                } else if (kind == null) {
                    // must be a layout file
                    assert restName.endsWith(LAYOUT_FILTER.getExtension());
                    String objectName = LAYOUT_FILTER.stripExtension(restName);
                    this.layoutEntryMap.put(objectName, entry);
                } else if (kind == RULE
                    || new File(restName).getParent() == null) {
                    // only look at lower directories in case of rules
                    Object oldEntry =
                        zipEntryMap.get(kind).put(restName, entry);
                    assert oldEntry == null : String.format(
                        "Duplicate %s name '%s'", kind.getName(), restName);
                }
            }
        }
        // now process the entries
        // first load the properties, as they are necessary for loading types
        loadProperties(zipFile, properties);
        for (ResourceKind kind : EnumSet.allOf(ResourceKind.class)) {
            if (kind.isTextBased()) {
                loadTexts(kind, zipFile, zipEntryMap.get(kind));
            } else if (kind.isGraphBased()) {
                loadGraphs(kind, zipFile, zipEntryMap.get(kind));
            }
        }
        zipFile.close();
        notify(EnumSet.allOf(ResourceKind.class));
        this.initialised = true;
    }

    /**
     * Returns the string representation of the URL or file this store was
     * loaded from.
     */
    @Override
    public Object getLocation() {
        return this.location;
    }

    @Override
    public SystemStore save(File file, boolean clearDir) throws IOException {
        return SystemStore.save(file, this, clearDir);
    }

    /** This type of system store is not modifiable. */
    @Override
    public boolean isModifiable() {
        return false;
    }

    /**
     * Two system stores are considered equal if the locations they load from
     * are equal.
     * @see #getLocation()
     */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof DefaultArchiveSystemStore)
            && ((DefaultArchiveSystemStore) obj).getLocation().equals(
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
        return getName() + " - " + getLocation();
    }

    /**
     * Loads the named control programs from a zip file, using the prepared map
     * from program names to zip entries.
     */
    private void loadTexts(ResourceKind kind, ZipFile file,
            Map<String,ZipEntry> texts) throws IOException {
        getTextMap(kind).clear();
        for (Map.Entry<String,ZipEntry> textEntry : texts.entrySet()) {
            String controlName =
                kind.getFilter().stripExtension(textEntry.getKey());
            InputStream in = file.getInputStream(textEntry.getValue());
            String program = groove.io.Util.readInputStreamToString(in);
            getTextMap(kind).put(controlName, program);
        }
    }

    /**
     * Loads the named graphs from a zip file, using the prepared map from graph
     * names to zip entries.
     */
    private void loadGraphs(ResourceKind kind, ZipFile file,
            Map<String,ZipEntry> graphs) throws IOException {
        Map<String,AspectGraph> graphMap = getGraphMap(kind);
        graphMap.clear();
        for (Map.Entry<String,AspectGraph> entry : loadObjects(kind, file,
            graphs).entrySet()) {
            String name =
                kind == RULE ? createRuleName(entry.getKey()).toString()
                        : entry.getKey();
            graphMap.put(name, entry.getValue());
        }
        if (kind == TYPE && this.properties != null) {
            // enable the active types listed in the system properties
            Set<String> enabledTypes = this.properties.getTypeNames();
            for (String enabledType : enabledTypes) {
                GraphInfo.getProperties(graphMap.get(enabledType), true).setEnabled(
                    true);
            }
        }
    }

    /**
     * Loads the properties file from file (if any), and assigns the properties
     * to {@link #properties}.
     */
    private void loadProperties(ZipFile file, ZipEntry entry)
        throws IOException {
        this.properties = new SystemProperties();
        if (entry != null) {
            Properties grammarProperties = new Properties();
            InputStream s = file.getInputStream(entry);
            grammarProperties.load(s);
            s.close();
            this.properties.putAll(grammarProperties);
            this.hasSystemPropertiesFile = true;
        } else {
            this.hasSystemPropertiesFile = false;
        }
    }

    /**
     * Loads the named objects from a zip file, using the prepared map from
     * names to zip entries. Returns a map from names to {@link AspectGraph}s.
     * @param file the file to read from
     * @param graphs the mapping from object names to zip entries
     */
    private Map<String,AspectGraph> loadObjects(ResourceKind kind,
            ZipFile file, Map<String,ZipEntry> graphs) throws IOException {
        ExtensionFilter filter = kind.getFilter();
        Map<String,AspectGraph> result = new HashMap<String,AspectGraph>();
        for (Map.Entry<String,ZipEntry> graphEntry : graphs.entrySet()) {
            String graphName = filter.stripExtension(graphEntry.getKey());
            InputStream in = file.getInputStream(graphEntry.getValue());
            try {
                Pair<DefaultGraph,Map<String,DefaultNode>> plainGraphAndMap =
                    JaxbGxlIO.getInstance().loadGraphWithMap(in);
                DefaultGraph plainGraph = plainGraphAndMap.one();
                /*
                 * For backward compatibility, we set the role and name of the
                 * graph.
                 */
                plainGraph.setRole(kind.getGraphRole());
                plainGraph.setName(graphName);
                addLayout(file, graphEntry.getKey(), plainGraph,
                    plainGraphAndMap.two());
                AspectGraph graph = AspectGraph.newInstance(plainGraph);
                /* Store the graph */
                result.put(graphName, graph);
            } catch (FormatException exc) {
                throw new IOException(String.format(
                    "Format error while loading '%s':\n%s", graphName,
                    exc.getMessage()), exc);
            } catch (IOException exc) {
                throw new IOException(String.format(
                    "Error while loading '%s':\n%s", graphName,
                    exc.getMessage()), exc);
            }
        }
        return result;
    }

    /**
     * Adds layout information to a graph, based on the layout info found in a
     * given zip file.
     */
    private void addLayout(ZipFile file, String entryName,
            DefaultGraph plainGraph, Map<String,DefaultNode> nodeMap)
        throws IOException {
        ZipEntry layoutEntry = this.layoutEntryMap.get(entryName);
        if (layoutEntry != null) {
            try {
                LayoutMap<DefaultNode,DefaultEdge> layout =
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
     * '/'-separators are interpreted as rule name hierarchy.
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

    /** Notifies the observers with a given string value. */
    private void notify(Set<ResourceKind> property) {
        this.observable.hasChanged();
        this.observable.notifyObservers(new MyEdit(property));
    }

    private void testInit() throws IllegalStateException {
        if (!this.initialised) {
            throw new IllegalStateException(
                "Operation should only be called after initialisation");
        }
    }

    @Override
    protected boolean hasSystemProperties() {
        return this.hasSystemPropertiesFile;
    }

    private SystemProperties properties;
    /** Map from entry name to layout entry. */
    private final Map<String,ZipEntry> layoutEntryMap =
        new HashMap<String,ZipEntry>();
    /**
     * The location from which the source is loaded. If <code>null</code>, the
     * location was specified by file rather than url.
     */
    private final URL url;
    /** The file obtained from <code>location</code>. */
    private final File file;
    /**
     * Location identifier; either the URL or the file from which this store was
     * created.
     */
    private final String location;
    /** Name of the jar entry containing the grammar. */
    private final String entryName;
    /** Name of the rule system. */
    private final String grammarName;
    /** Flag indicating whether the store has been loaded. */
    private boolean initialised;
    /** The observable object associated with this system store. */
    private final Observable observable = new Observable();
    /** Flag whether this store contains a 'system.properties' file. */
    private boolean hasSystemPropertiesFile = false;

    /** Name of the JAR protocol and file extension. */
    static private final String JAR_PROTOCOL = FileType.JAR.getExtensionName();

    private static class MyEdit extends AbstractUndoableEdit implements Edit {
        public MyEdit(Set<ResourceKind> change) {
            this.change = change;
        }

        @Override
        public EditType getType() {
            return EditType.CREATE;
        }

        @Override
        public Set<ResourceKind> getChange() {
            return this.change;
        }

        private final Set<ResourceKind> change;
    }
}
