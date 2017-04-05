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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.undo.UndoableEdit;
import javax.swing.undo.UndoableEditSupport;

import groove.grammar.GrammarProperties;
import groove.grammar.QualName;
import groove.grammar.aspect.AspectGraph;
import groove.grammar.model.GrammarModel;
import groove.grammar.model.ResourceKind;
import groove.grammar.type.TypeLabel;
import groove.io.Util;

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
            } else if (kind.isTextBased()) {
                result = getTextMap(kind).isEmpty();
            } else {
                result = getGraphMap(kind).isEmpty();
            }
            if (!result) {
                break;
            }
        }
        return result;
    }

    /**
     * Immutable view on the name-to-aspect graph map of a given graph-based resource kind.
     * @param kind the kind of resource for which the map is requested
     */
    abstract public Map<QualName,AspectGraph> getGraphs(ResourceKind kind);

    /**
     * Adds or replaces a set of graph-based resources in the store.
     * @param kind the kind of resource affected
     * @param graphs the resources to be added or replaced
     * @param layout flag indicating that this is a layout change only,
     * which should be propagated as {@link EditType#LAYOUT}.
     * @return old (replaced) resources
     * @throws IOException if an error occurred while storing the rule
     */
    abstract public Collection<AspectGraph> putGraphs(ResourceKind kind,
        Collection<AspectGraph> graphs, boolean layout) throws IOException;

    /**
     * Deletes a set of graph-based resources from the store.
     * @param kind the resource kind; must be graph-based
     * @param names names of the resources to be deleted (non-null)
     * @return the named resources, insofar they existed
     * @throws IOException if the store is immutable
     */
    abstract public Collection<AspectGraph> deleteGraphs(ResourceKind kind,
        Collection<QualName> names) throws IOException;

    /**
     * Immutable view on the name-to-text map of a given text-based resource kind.
     * @param kind the kind of resource for which the map is requested
     */
    abstract public Map<QualName,String> getTexts(ResourceKind kind);

    /**
     * Adds or replaces a set of text-based resources in the store.
     * @param kind the kind of resource affected
     * @param texts the resources to be added or replaced
     * @return old (replaced) resources
     * @throws IOException if an error occurred while storing the rule
     */
    abstract public Map<QualName,String> putTexts(ResourceKind kind, Map<QualName,String> texts)
        throws IOException;

    /**
     * Deletes a set of text-based resources from the store.
     * @param kind the resource kind; must be text-based
     * @param names names of the resources to be deleted (non-null)
     * @return the named resources, insofar they existed
     * @throws IOException if the store is immutable
     */
    abstract public Map<QualName,String> deleteTexts(ResourceKind kind, Collection<QualName> names)
        throws IOException;

    /**
     * Renames a text-based resource in the store.
     * It is an error if no resource with the old name exists, or if a rule
     * with the new name exists.
     * @param kind the resource kind; must be text-based
     * @param oldName the name of the rule to be renamed (non-null)
     * @param newName the intended new name of the rule (non-null)
     * @throws IOException if an error occurred while storing the renamed rule
     */
    abstract public void rename(ResourceKind kind, QualName oldName, QualName newName)
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
     */
    abstract public void reload() throws IOException;

    /**
     * Saves the content of this grammar store to a given file, and returns the
     * saved store.
     * @throws IOException if the file does not have a known extension, or
     *         already exists, or if something goes wrong during saving. If an
     *         exception is thrown, any partial results are deleted.
     */
    abstract public SystemStore save(File file, boolean clearDir) throws IOException;

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

    /** Returns the resource map for a given graph-based resource kind. */
    protected final Map<QualName,AspectGraph> getGraphMap(ResourceKind kind) {
        Map<QualName,AspectGraph> result = this.graphMap.get(kind);
        if (result == null) {
            this.graphMap.put(kind, result = new TreeMap<>());
        }
        return result;
    }

    /** Returns the resource map for a given text-based resource kind. */
    protected final Map<QualName,String> getTextMap(ResourceKind kind) {
        Map<QualName,String> result = this.textMap.get(kind);
        if (result == null) {
            this.textMap.put(kind, result = new TreeMap<>());
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
    private final Map<ResourceKind,Map<QualName,AspectGraph>> graphMap =
        new EnumMap<>(ResourceKind.class);
    /** The name-to-text maps of the store. */
    private final Map<ResourceKind,Map<QualName,String>> textMap =
        new EnumMap<>(ResourceKind.class);

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

    /** Saves the content of a given system store to file. */
    static public SystemStore save(File file, SystemStore store, boolean clearDir)
        throws IOException {
        if (!GRAMMAR.hasExtension(file)) {
            throw new IOException(
                String.format("File '%s' does not refer to a production system", file));
        }
        // if the file already exists, rename it
        // in order to be able to restore if saving fails
        File newFile = null;
        if (file.exists()) {
            newFile = file;
            do {
                newFile = new File(newFile.getParent(), "Copy of " + newFile.getName());
            } while (newFile.exists());
            if (clearDir) {
                if (!file.renameTo(newFile)) {
                    throw new IOException(
                        String.format("Can't save grammar to existing file '%s'", file));
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
                } else if (kind.isTextBased()) {
                    result.putTexts(kind, store.getTexts(kind));
                } else {
                    result.putGraphs(kind, store.getGraphs(kind)
                        .values(), false);
                }
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
    static private boolean deleteRecursive(File location) {
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
