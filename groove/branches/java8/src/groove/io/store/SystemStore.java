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
import static groove.io.FileType.GRAMMAR;
import groove.grammar.GrammarProperties;
import groove.grammar.aspect.AspectGraph;
import groove.grammar.model.GrammarModel;
import groove.grammar.model.Resource;
import groove.grammar.model.ResourceKind;
import groove.grammar.model.Text;
import groove.grammar.type.TypeLabel;
import groove.io.Util;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.swing.undo.UndoableEdit;
import javax.swing.undo.UndoableEditSupport;

/**
 * Instance of the generic system store where both the graph and the rule
 * representations are {@link AspectGraph}s.
 * @author Arend Rensink
 * @version $Revision $
 */
abstract public class SystemStore extends UndoableEditSupport {
    /**
     * Returns the name of this store.
     * @return the name of this store; cannot be <code>null</code> or empty.
     */
    abstract public String getName();

    /**
     * Returns the location of this store. The location uniquely identifies the
     * place from where the store was obtained.
     * @return the location of this store; cannot be <code>null</code> or empty.
     */
    abstract public Object getLocation();

    /** Checks if the store is empty. */
    public boolean isEmpty() {
        boolean result = true;
        for (ResourceKind kind : ResourceKind.values()) {
            if (kind == PROPERTIES) {
                result = !this.hasSystemProperties();
            } else {
                result = getResourceMap(kind).isEmpty();
            }
            if (!result) {
                break;
            }
        }
        return result;
    }

    /** Returns the resource corresponding to a given resource kind
     * and resource name.
     * @param kind the resource kind
     * @param name the name of the requested resource
     * @return the requested resource, or {@code null} if there is none
     */
    final public Resource get(ResourceKind kind, String name) {
        testInit();
        return getResourceMap(kind).get(name);
    }

    /** Returns the graph resource corresponding to a given (graph-based) resource kind
     * and resource name.
     * @param kind the resource kind; should be graph-based
     * @param name the name of the requested resource
     * @return the requested graph, or {@code null} if there is none
     */
    final public AspectGraph getGraph(ResourceKind kind, String name) {
        assert kind.isGraphBased() : String.format("Resource kind %s is not graph-based", kind);
        return (AspectGraph) get(kind, name);
    }

    /** Returns the graph resource corresponding to a given (text-based) resource kind
     * and resource name.
     * @param kind the resource kind; should be text-based
     * @param name the name of the requested resource
     * @return the requested text, or {@code null} if there is none
     */
    final public Text getText(ResourceKind kind, String name) {
        assert kind.isTextBased() : String.format("Resource kind %s is not text-based", kind);
        return (Text) get(kind, name);
    }

    /**
     * Returns an unmodifiable name-to-resource map of a given resource kind.
     * @param kind the kind of resource for which the map is requested
     */
    final public Map<String,Resource> get(ResourceKind kind) {
        testInit();
        return getResourceMap(kind);
    }

    /**
     * Adds or replaces a set of resources in the store.
     * @param kind the kind of resource affected
     * @param resources the resources to be added or replaced
     * @return old (replaced) resources
     * @throws IOException if an error occurred while storing the rule
     */
    final public Collection<? extends Resource> put(ResourceKind kind, Collection<? extends Resource> resources)
        throws IOException {
        return put(kind, resources, false);
    }

    /**
     * Adds or replaces a set of resources in the store, with a flag
     * indicating that this is a minor (layout) change with respect to the existing resources.
     * @param kind the kind of resource affected
     * @param resources the resources to be added or replaced
     * @param layout flag indicating that this is a layout change only,
     * which should be propagated as {@link EditType#LAYOUT}.
     * @return old (replaced) resources
     * @throws IOException if an error occurred while storing the rule
     */
    abstract public Collection<? extends Resource> put(ResourceKind kind, Collection<? extends Resource> resources,
        boolean layout) throws IOException;

    /**
     * Deletes a set of resources from the store.
     * @param kind the resource kind; must be graph-based
     * @param names names of the resources to be deleted (non-null)
     * @return the named resources, insofar they existed
     * @throws IOException if the store is immutable
     */
    abstract public Collection<? extends Resource> delete(ResourceKind kind, Collection<String> names)
        throws IOException;

    /**
     * Renames a resource in the store.
     * It is an error if no resource with the old name exists, or if a resource
     * with the new name exists.
     * @param kind the resource kind; must be text-based
     * @param oldName the name of the resource to be renamed (non-null)
     * @param newName the intended new name of the resource (non-null)
     * @throws IOException if an error occurred while storing the renamed resource
     */
    abstract public void rename(ResourceKind kind, String oldName, String newName)
        throws IOException;

    /** The system properties object in the store (non-null). */
    abstract public GrammarProperties getProperties();

    /**
     * Replaces the system properties in the store
     * @param properties the new system properties object
     * @throws IOException if an error occurred while storing the properties
     */
    abstract public void putProperties(GrammarProperties properties) throws IOException;

    /**
     * Changes a label into another in all relevant elements of the store.
     * @throws IOException if an error occurred while storing the properties
     */
    abstract public void relabel(TypeLabel oldLabel, TypeLabel newLabel) throws IOException;

    /**
     * Reloads all data from the persistent storage into this store. Should be
     * called once immediately after construction of the store.
     * To be overwritten by subclasses
     * @throws IOException if an I/O error occurred during reloading
     */
    public void reload() throws IOException {
        this.initialised = true;
    }

    /** Returns a grammar model backed up by this store. */
    public GrammarModel toGrammarModel() {
        if (this.model == null) {
            this.model = new GrammarModel(this);
            addObserver(this.model);
        }
        return this.model;
    }

    /**
     * Adds an observer to the model.
     * The observer is notified of all {@link Edit} occurrences.
     */
    public void addObserver(Observer observer) {
        this.observable.addObserver(observer);
    }

    /**
     * Indicates if this store can be modified. If the store cannot be modified,
     * all the operations that attempt to modify it will throw
     * {@link IOException}s.
     * @return <code>true</code> if the store is modifiable
     */
    abstract public boolean isModifiable();

    /** Indicates if edits are currently added to the undo list. */
    public boolean isUndoSuspended() {
        return this.undoSuspended;
    }

    /** Changes the registration of edits to the undo list. */
    public void setUndoSuspended(boolean undoSuspended) {
        this.undoSuspended = undoSuspended;
    }

    /** Returns the (modifiable) map from names to resources of a given kind. */
    protected final Map<String,Resource> getResourceMap(ResourceKind kind) {
        Map<String,Resource> result = this.resourceMap.get(kind);
        if (result == null) {
            this.resourceMap.put(kind, result = new HashMap<>());
        }
        return result;
    }

    /** Indicates if there was a system properties file in the store. */
    abstract protected boolean hasSystemProperties();

    /** Notifies the observers with a given string value. */
    final protected void notifyObservers(Edit edit) {
        this.observable.notifyObservers(edit);
    }

    /** The name-to-graph maps of the store. */
    private final Map<ResourceKind,Map<String,Resource>> resourceMap = new EnumMap<>(
        ResourceKind.class);

    /** The grammar view associated with this store. */
    private GrammarModel model;
    /** Flag indicating that edits are currently not posted. */
    private boolean undoSuspended;
    /** The observable object associated with this system store. */
    private final Observable observable = new Observable() {
        /** Always invokes {@link #setChanged()}. */
        @Override
        public void notifyObservers(Object arg) {
            setChanged();
            super.notifyObservers(arg);
        }
    };

    /** Indicates if the store has been initialised. */
    final protected boolean isInit() {
        return this.initialised;
    }

    /** Tests if the store has been initialised by a call of {@link #reload()}. */
    final protected void testInit() throws IllegalStateException {
        if (!this.initialised) {
            throw new IllegalStateException("Operation should only be called after initialisation");
        }
    }

    private boolean initialised;

    /** Saves the content of a given system store to file. */
    static public SystemStore save(Path file, SystemStore store, boolean clearDir)
        throws IOException {
        if (!GRAMMAR.hasExtension(file)) {
            throw new IOException(String.format("File '%s' does not refer to a production system",
                file));
        }
        // if the file already exists, rename it
        // in order to be able to restore if saving fails
        Path newFile = null;
        if (Files.exists(file)) {
            newFile = file;
            do {
                Path parent = newFile.getParent();
                String copyName = "Copy of " + newFile.getFileName().toString();
                newFile = parent == null ? Paths.get(copyName) : parent.resolve(copyName);
            } while (Files.exists(newFile));
            if (clearDir) {
                try {
                    Files.move(file, newFile);
                } catch (IOException e) {
                    throw new IOException(String.format("Can't save grammar to existing file '%s'",
                        file));
                }
            } else {
                Util.copyDirectory(file, newFile, true);
            }
        }
        try {
            DefaultFileSystemStore result = new DefaultFileSystemStore(file, true);
            result.reload();
            // save properties
            for (ResourceKind kind : ResourceKind.values()) {
                if (kind == PROPERTIES) {
                    result.putProperties(store.getProperties());
                } else {
                    result.put(kind, store.get(kind).values());
                }
            }
            if (newFile != null) {
                boolean deleted = deleteRecursive(newFile);
                assert deleted : String.format("Failed to delete '%s'", newFile);
            }
            return result;
        } catch (IOException exc) {
            Files.delete(file);
            // attempt to re-rename previously existing file
            if (newFile != null) {
                Files.move(newFile, file);
            }
            throw exc;
        }
    }

    /**
     * Recursively traverses all subdirectories and deletes all files and
     * directories.
     */
    static private boolean deleteRecursive(Path location) {
        try {
            if (Files.isDirectory(location)) {
                try (DirectoryStream<Path> files = Files.newDirectoryStream(location)) {
                    for (Path file : files) {
                        if (!deleteRecursive(file)) {
                            return false;
                        }
                    }
                }
            }
            Files.delete(location);
            return true;
        } catch (IOException exc) {
            return false;
        }
    }

    /** Edit object for system stores. */
    public static interface Edit extends UndoableEdit {
        /**
         * Returns the set of changes of this edit.
         */
        public Set<ResourceKind> getChange();

        /**
         * Returns the type of this edit.
         */
        public EditType getType();
    }
}
