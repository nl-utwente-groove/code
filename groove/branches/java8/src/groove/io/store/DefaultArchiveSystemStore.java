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

import static groove.grammar.model.ResourceKind.PROPERTIES;
import static groove.grammar.model.ResourceKind.TYPE;
import static groove.io.FileType.GRAMMAR;
import static groove.io.FileType.JAR;
import static groove.io.FileType.LAYOUT;
import static groove.io.FileType.RULE;
import static groove.io.FileType.ZIP;
import groove.grammar.GrammarProperties;
import groove.grammar.QualName;
import groove.grammar.aspect.AspectGraph;
import groove.grammar.model.Resource;
import groove.grammar.model.ResourceKind;
import groove.grammar.model.Text;
import groove.grammar.type.TypeLabel;
import groove.graph.GraphInfo;
import groove.io.FileType;
import groove.io.graph.AttrGraph;
import groove.io.graph.GxlIO;
import groove.io.graph.LayoutIO;
import groove.util.Groove;
import groove.util.parse.FormatException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
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
    public DefaultArchiveSystemStore(Path file) throws IllegalArgumentException {
        if (!Files.exists(file)) {
            throw new IllegalArgumentException(String.format("File '%s' does not exist", file));
        }
        String fileName = file.getFileName().toString();
        String extendedName;
        if (!Files.isDirectory(file) && JAR.hasExtension(file)) {
            extendedName = JAR.stripExtension(fileName);
        } else if (!Files.isDirectory(file) && ZIP.hasExtension(file)) {
            extendedName = ZIP.stripExtension(fileName);
        } else {
            throw new IllegalArgumentException(String.format("File '%s' " + NO_JAR_OR_ZIP_SUFFIX,
                file));
        }
        this.location = file.toString();
        this.file = file;
        this.entryName = extendedName;
        this.grammarName = GRAMMAR.stripExtension(extendedName);
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
    public DefaultArchiveSystemStore(URL url) throws IllegalArgumentException, IOException {
        // first strip query and anchor part
        try {
            url = new URL(url.getProtocol(), url.getAuthority(), url.getPath());
        } catch (MalformedURLException exc) {
            assert false : String.format("Stripping URL '%s' throws exception: %s", url, exc);
        }
        this.location = url.toString();
        // artificially append the jar protocol, if it is not yet there
        if (!url.getProtocol().equals(JAR_PROTOCOL)) {
            if (!JAR.hasExtension(url.getPath()) && !ZIP.hasExtension(url.getPath())) {
                throw new IllegalArgumentException(
                    String.format("URL '%s' is not a JAR or ZIP file", url));
            }
            url = new URL(JAR_PROTOCOL, null, url.toString() + "!/");
        }
        this.entryName = extractEntryName(url);
        // take the last part of the entry name as grammar name
        File fileFromEntry = new File(this.entryName);
        this.grammarName = GRAMMAR.stripExtension(fileFromEntry.getName());
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
            result = FileType.getPureName(new File(connection.getJarFileURL().getPath()));
        }
        return result;
    }

    @Override
    public String getName() {
        return this.grammarName;
    }

    @Override
    public Collection<Resource> put(ResourceKind kind, Collection<Resource> graphs, boolean layout)
        throws IOException {
        throw createImmutable();
    }

    @Override
    public Collection<Resource> delete(ResourceKind kind, Collection<String> names)
        throws IOException {
        throw createImmutable();
    }

    @Override
    public void rename(ResourceKind kind, String oldName, String newName) throws IOException {
        throw createImmutable();
    }

    @Override
    public GrammarProperties getProperties() {
        testInit();
        return this.properties;
    }

    @Override
    public void putProperties(GrammarProperties properties) throws IOException {
        throw createImmutable();
    }

    @Override
    public void relabel(TypeLabel oldLabel, TypeLabel newLabel) throws IOException {
        throw createImmutable();
    }

    /** Creates an exception stating that the archive is immutable. */
    private IOException createImmutable() {
        return new IOException(
            String.format("Archived grammar '%s' is immutable.%nSave locally to allow changes.",
                getName()));
    }

    @Override
    public void reload() throws IOException {
        super.reload();
        ZipFile zipFile;
        if (this.file == null) {
            zipFile = ((JarURLConnection) this.url.openConnection()).getJarFile();
        } else {
            zipFile = new ZipFile(this.file.toFile());
        }
        // collect the relevant entries
        Map<ResourceKind,Map<String,ZipEntry>> zipEntryMap =
            new EnumMap<ResourceKind,Map<String,ZipEntry>>(ResourceKind.class);
        for (ResourceKind kind : ResourceKind.values()) {
            zipEntryMap.put(kind, new HashMap<String,ZipEntry>());
        }
        ZipEntry properties = null;
        for (Enumeration<? extends ZipEntry> entries = zipFile.entries(); entries.hasMoreElements();) {
            ZipEntry entry = entries.nextElement();
            String entryName = entry.getName();
            int entryPrefixLength = this.entryName.length() + 1;
            if (entryName.startsWith(this.entryName) && entryName.length() > entryPrefixLength) {
                // strip off prefix + 1 to take case of file separator
                String restName = entryName.substring(entryPrefixLength);
                // find out the resource kind by testing the extension
                ResourceKind kind = null;
                for (ResourceKind tryKind : ResourceKind.values()) {
                    if (restName.endsWith(tryKind.getFileType().getExtension())) {
                        kind = tryKind;
                        break;
                    }
                }
                if (kind == PROPERTIES) {
                    String propertiesName = PROPERTIES.getFileType().stripExtension(restName);
                    if (propertiesName.equals(Groove.PROPERTY_NAME)) {
                        // preferably take the one with the default name
                        properties = entry;
                    } else if (properties == null && propertiesName.equals(this.grammarName)) {
                        // otherwise, take the one with the grammar name
                        properties = entry;
                    }
                } else if (kind == null) {
                    // must be a layout file
                    if (LAYOUT.hasExtension(restName)) {
                        String objectName = LAYOUT.stripExtension(restName);
                        this.layoutEntryMap.put(objectName, entry);
                    }
                } else {
                    Object oldEntry = zipEntryMap.get(kind).put(restName, entry);
                    assert oldEntry == null : String.format("Duplicate %s name '%s'",
                        kind.getName(),
                        restName);
                }
            }
        }
        // now process the entries
        // first load the properties, as they are necessary for loading types
        loadProperties(zipFile, properties);
        for (ResourceKind kind : ResourceKind.values()) {
            if (kind.isTextBased()) {
                loadTexts(kind, zipFile, zipEntryMap.get(kind));
            } else if (kind.isGraphBased()) {
                loadGraphs(kind, zipFile, zipEntryMap.get(kind));
            }
        }
        zipFile.close();
        notify(EnumSet.allOf(ResourceKind.class));
    }

    /**
     * Returns the string representation of the URL or file this store was
     * loaded from.
     */
    @Override
    public Object getLocation() {
        return this.location;
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
            && ((DefaultArchiveSystemStore) obj).getLocation().equals(getLocation());
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
     * Loads the named textual resources from a zip file, using the prepared map
     * from resource names to zip entries.
     */
    private void loadTexts(ResourceKind kind, ZipFile file, Map<String,ZipEntry> texts)
        throws IOException {
        Map<String,Resource> resourceMap = getResourceMap(kind);
        resourceMap.clear();
        for (Map.Entry<String,ZipEntry> textEntry : texts.entrySet()) {
            String name = kind.getFileType().stripExtension(textEntry.getKey());
            List<String> lines = new ArrayList<>();
            InputStream in = file.getInputStream(textEntry.getValue());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String nextLine = reader.readLine();
            while (nextLine != null) {
                lines.add(nextLine);
                nextLine = reader.readLine();
            }
            resourceMap.put(name, new Text(kind, name, lines));
        }
    }

    /**
     * Loads the named graphs from a zip file, using the prepared map from graph
     * names to zip entries.
     */
    private void loadGraphs(ResourceKind kind, ZipFile file, Map<String,ZipEntry> graphs)
        throws IOException {
        Map<String,Resource> resourceMap = getResourceMap(kind);
        resourceMap.clear();
        for (Map.Entry<String,AspectGraph> entry : loadObjects(kind, file, graphs).entrySet()) {
            String name = createQualName(entry.getKey()).toString();
            resourceMap.put(name, entry.getValue());
        }
        // set the type graphs themselves to active, if for those graphs whose name
        // is in the active type list of the grammar properties
        if (kind == TYPE && this.properties != null) {
            // enable the active types listed in the system properties
            Set<String> enabledTypes = this.properties.getActiveNames(kind);
            for (String enabledType : enabledTypes) {
                GraphInfo.setEnabled((AspectGraph) resourceMap.get(enabledType), true);
            }
        }
    }

    /**
     * Loads the properties file from file (if any), and assigns the properties
     * to {@link #properties}.
     */
    private void loadProperties(ZipFile file, ZipEntry entry) throws IOException {
        this.properties = new GrammarProperties();
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
    private Map<String,AspectGraph> loadObjects(ResourceKind kind, ZipFile file,
        Map<String,ZipEntry> graphs) throws IOException {
        FileType filter = kind.getFileType();
        Map<String,AspectGraph> result = new HashMap<String,AspectGraph>();
        for (Map.Entry<String,ZipEntry> graphEntry : graphs.entrySet()) {
            String graphName = filter.stripExtension(graphEntry.getKey());
            InputStream in = file.getInputStream(graphEntry.getValue());
            try {
                AttrGraph xmlGraph = GxlIO.instance().loadGraph(in);
                /*
                 * For backward compatibility, we set the role and name of the
                 * graph.
                 */
                xmlGraph.setRole(kind.getGraphRole());
                xmlGraph.setName(createQualName(graphName).toString());
                addLayout(file, graphEntry.getKey(), xmlGraph);
                AspectGraph graph = xmlGraph.toAspectGraph();
                /* Store the graph */
                result.put(graphName, graph);
            } catch (FormatException exc) {
                throw new IOException(String.format("Format error while loading '%s':\n%s",
                    graphName,
                    exc.getMessage()), exc);
            } catch (IOException exc) {
                throw new IOException(String.format("Error while loading '%s':\n%s",
                    graphName,
                    exc.getMessage()), exc);
            }
        }
        return result;
    }

    /**
     * Adds layout information to a graph, based on the layout info found in a
     * given zip file.
     */
    private void addLayout(ZipFile file, String entryName, AttrGraph xmlGraph) throws IOException {
        ZipEntry layoutEntry = this.layoutEntryMap.get(entryName);
        if (layoutEntry != null) {
            LayoutIO.getInstance().loadLayout(xmlGraph, file.getInputStream(layoutEntry));
        }
    }

    /**
     * Creates a rule name from the remainder of a JAR/ZIP entry name. The
     * '/'-separators are interpreted as rule name hierarchy.
     * @param restName The entry name to convert; non-null
     * @return The resulting rule name; non-null
     */
    private QualName createQualName(String restName) throws IOException {
        StringBuilder result = new StringBuilder();
        File nameAsFile = new File(RULE.stripExtension(restName));
        result.append(nameAsFile.getName());
        while (nameAsFile.getParent() != null) {
            nameAsFile = nameAsFile.getParentFile();
            result.insert(0, QualName.SEPARATOR);
            result.insert(0, nameAsFile.getName());
        }
        try {
            return new QualName(result.toString());
        } catch (FormatException e) {
            throw new IOException(e.getMessage());
        }
    }

    /** Notifies the observers with a given string value. */
    private void notify(Set<ResourceKind> property) {
        this.observable.hasChanged();
        this.observable.notifyObservers(new MyEdit(property));
    }

    @Override
    protected boolean hasSystemProperties() {
        return this.hasSystemPropertiesFile;
    }

    private GrammarProperties properties;
    /** Map from entry name to layout entry. */
    private final Map<String,ZipEntry> layoutEntryMap = new HashMap<String,ZipEntry>();
    /**
     * The location from which the source is loaded. If <code>null</code>, the
     * location was specified by file rather than url.
     */
    private final URL url;
    /** The file obtained from <code>location</code>. */
    private final Path file;
    /**
     * Location identifier; either the URL or the file from which this store was
     * created.
     */
    private final String location;
    /** Name of the jar entry containing the grammar. */
    private final String entryName;
    /** Name of the rule system. */
    private final String grammarName;
    /** The observable object associated with this system store. */
    private final Observable observable = new Observable();
    /** Flag whether this store contains a 'system.properties' file. */
    private boolean hasSystemPropertiesFile = false;

    /** Name of the JAR protocol and file extension. */
    static private final String JAR_PROTOCOL = FileType.JAR.getExtensionName();
    /** Suffix of 'no JAR or no ZIP' error message. Allows error to be
     * recognized from the outside. */
    public static final String NO_JAR_OR_ZIP_SUFFIX = "is not a JAR or ZIP file";

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
