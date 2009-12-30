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

import groove.graph.Label;
import groove.trans.RuleName;
import groove.trans.SystemProperties;
import groove.view.StoredGrammarView;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.swing.event.UndoableEditListener;

/**
 * Interface for any source of rule system data. The data consist of a list of
 * graphs, a list of rules, a list of control programs, and a rule system
 * properties object. Depending on the implementation, the store may be
 * immutable.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface GenericSystemStore<R,G> {
    /**
     * Returns the name of this store.
     * @return the name of this store; cannot be <code>null</code> or empty.
     */
    public String getName();

    /**
     * Returns the location of this store. The location uniquely identifies the
     * place from where the store was obtained.
     * @return the location of this store; cannot be <code>null</code> or empty.
     */
    public Object getLocation();

    /** Immutable view on the rulename-to-rule map in the store. */
    public Map<RuleName,R> getRules();

    /** Immutable view on the name-to-graph map in the store. */
    public Map<String,G> getGraphs();

    /** Immutable view on the name-to-control-program map in the store. */
    public Map<String,String> getControls();

    /** The system properties object in the store (non-null). */
    public SystemProperties getProperties();

    /**
     * Deletes a rule from the store.
     * @param name name of the rule to be deleted (non-null)
     * @return the rule with name <code>name</code>, or <code>null</code> if
     *         there was no such rule
     * @throws UnsupportedOperationException if the store is immutable
     */
    public R deleteRule(RuleName name) throws UnsupportedOperationException;

    /**
     * Adds or replaces a rule in the store.
     * @param rule the rule to be added (non-null)
     * @return the old rule with the name of <code>rule</code>, if any;
     *         <code>null</code> otherwise
     * @throws UnsupportedOperationException if the store is immutable
     * @throws IOException if an error occurred while storing the rule
     */
    public R putRule(R rule) throws UnsupportedOperationException, IOException;

    /**
     * Renames a rule in the store.
     * @param oldName the name of the rule to be renamed (non-null)
     * @param newName the intended new name of the rule (non-null)
     * @return the renamed rule, or <code>null</code> if no rule named
     *         <code>oldName</code> existed
     * @throws IOException if an error occurred while storing the renamed rule
     * @throws UnsupportedOperationException if the store is immutable
     */
    public R renameRule(String oldName, String newName) throws IOException;

    /**
     * Deletes a graph from the store.
     * @param name name of the graph to be deleted
     * @return the graph with name <code>name</code>, or <code>null</code> if
     *         there was no such graph
     * @throws UnsupportedOperationException if the store is immutable
     */
    public G deleteGraph(String name) throws UnsupportedOperationException;

    /**
     * Adds or replaces a graph in the store.
     * @param graph the graph to be added
     * @return the old graph with the name of <code>graph</code>, if any;
     *         <code>null</code> otherwise
     * @throws UnsupportedOperationException if the store is immutable
     * @throws IOException if an error occurred while storing the graph
     */
    public G putGraph(G graph) throws UnsupportedOperationException,
        IOException;

    /**
     * Renames a graph in the store.
     * @param oldName the name of the graph to be renamed (non-null)
     * @param newName the intended new name of the graph (non-null)
     * @return the renamed graph, or <code>null</code> if no graph named
     *         <code>oldName</code> existed
     * @throws IOException if an error occurred while storing the renamed graph
     * @throws UnsupportedOperationException if the store is immutable
     */
    public G renameGraph(String oldName, String newName) throws IOException;

    /**
     * Deletes a control program from the store.
     * @param name name of the control program to be deleted
     * @return the program with name <code>name</code>, or <code>null</code> if
     *         there was no such program
     * @throws UnsupportedOperationException if the store is immutable
     */
    public String deleteControl(String name)
        throws UnsupportedOperationException;

    /**
     * Adds or replaces a control program in the store.
     * @param control the control program to be added
     * @return the old control program with name <code>name</code>, if any;
     *         <code>null</code> otherwise
     * @throws UnsupportedOperationException if the store is immutable
     * @throws IOException if an error occurred while storing the control
     *         program
     */
    public String putControl(String name, String control)
        throws UnsupportedOperationException, IOException;

    /**
     * Replaces the system properties in the store
     * @param properties the new system properties object
     * @throws UnsupportedOperationException if the store is immutable
     * @throws IOException if an error occurred while storing the properties
     */
    public void putProperties(SystemProperties properties)
        throws UnsupportedOperationException, IOException;

    /**
     * Changes a label into another in all relevant elements of the store.
     * @throws UnsupportedOperationException if the store is immutable
     * @throws IOException if an error occurred while storing the properties
     */
    public void relabel(Label oldLabel, Label newLabel)
        throws UnsupportedOperationException, IOException;

    /**
     * Reloads all data from the persistent storage into this store. Should be
     * called once immediately after construction of the store.
     */
    public void reload() throws IOException;

    /**
     * Saves the content of this grammar store to a given file, and returns the
     * saved store.
     * @throws IOException if the file does not have a known extension, or
     *         already exists, or if something goes wrong during saving. If an
     *         exception is thrown, any partial results are deleted.
     */
    public SystemStore save(File file) throws IOException;

    /** Returns a stored grammar view backed up by this store. */
    public StoredGrammarView toGrammarView();

    /**
     * Indicates if this store can be modified. If the store cannot be modified,
     * all the operations that attempt to modify it will throw
     * {@link UnsupportedOperationException}s.
     * @return <code>true</code> if the store is modifiable
     */
    public boolean isModifiable();

    /** Adds a listener to this store. */
    public void addUndoableEditListener(UndoableEditListener listener);

    /** Removes a listener from this store. */
    public void removeUndoableEditListener(UndoableEditListener listener);
}
