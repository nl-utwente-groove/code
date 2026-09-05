/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
package nl.utwente.groove.gui.jgraph;

import java.beans.PropertyChangeListener;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.undo.UndoableEdit;

import org.eclipse.jdt.annotation.NonNull;
import org.jgraph.event.GraphModelEvent.GraphModelChange;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.ParentMap;

import nl.utwente.groove.grammar.ResourceProperties;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.aspect.AspectKind;
import nl.utwente.groove.grammar.aspect.AspectNode;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.GraphBasedModel;
import nl.utwente.groove.grammar.model.ResourceModel;
import nl.utwente.groove.grammar.type.TypeGraph;
import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.Element;
import nl.utwente.groove.graph.Node;
import nl.utwente.groove.gui.view.AspectGraphViewModel;
import nl.utwente.groove.gui.view.AspectViewCell;
import nl.utwente.groove.util.QualName;

/**
 * Implements jgraph's GraphModel interface on top of a {@link ResourceModel}. This is
 * used to visualise rules and attributed graphs: the backend adapter of an
 * {@link AspectGraphViewModel}, adding JGraph's clipboard and connection semantics
 * and the detection of structural edits.
 * @author Arend Rensink
 * @version $Revision$
 */
final public class AspectJModel extends JModel<@NonNull AspectGraph> {
    /**
     * Creates an new model, initially without a graph or grammar loaded.
     * Call {@link #setGrammar(GrammarModel)} to complete construction.
     */
    AspectJModel(AspectJGraph jGraph) {
        super(jGraph);
    }

    @Override
    protected AspectGraphViewModel createViewModel() {
        return new AspectGraphViewModel(getJGraph().getController(), this);
    }

    @Override
    public AspectGraphViewModel getViewModel() {
        return (AspectGraphViewModel) super.getViewModel();
    }

    @Override
    public AspectJGraph getJGraph() {
        return (AspectJGraph) super.getJGraph();
    }

    /** Specialises the type to a list of {@link nl.utwente.groove.gui.view.ViewCell}s. */
    @Override
    @SuppressWarnings("unchecked")
    public List<? extends AspectViewCell> getRoots() {
        return (List<? extends AspectViewCell>) super.getRoots();
    }

    /** Sets a grammar model, with respect to which typing is resolved. */
    public void setGrammar(GrammarModel grammar) {
        getViewModel().setGrammar(grammar);
    }

    /** Returns the (possibly {@code null}) grammar set for this model. */
    GrammarModel getGrammar() {
        return getViewModel().getGrammar();
    }

    @Override
    public AspectViewCell getJCell(Element elem) {
        return getViewModel().getJCell(elem);
    }

    /** Specialises the return type. */
    @Override
    public AspectViewCell getJCellForEdge(Edge edge) {
        return getViewModel().getJCellForEdge(edge);
    }

    /** Specialises the return type. */
    @Override
    public AspectJVertex getJCellForNode(Node node) {
        return (AspectJVertex) getViewModel().getJCellForNode(node);
    }

    /**
     * Clones this model, and initialises the new model with the given
     * argument graph.
     */
    public AspectJModel cloneWithNewGraph(AspectGraph graph) {
        AspectJModel result = getJGraph().newModel();
        if (getGrammar() != null) {
            result.setGrammar(getGrammar());
        }
        result.setBeingEdited(getViewModel().isBeingEdited());
        result.loadGraph(graph);
        return result;
    }

    /**
     * Reconstructs the aspect graph on the basis of the current
     * content of the JModel.
     * This method should be called immediately after the changes to
     * the JModel have been made, but before any graph listeners are
     * notified.
     */
    public void syncGraph() {
        getViewModel().syncGraph();
    }

    /** Returns an up-to-date resource model for the graph being edited here. */
    public GraphBasedModel<?> getResourceModel() {
        return getViewModel().getResourceModel();
    }

    /** Returns the type graph associated with this jModel, if any. */
    public TypeGraph getTypeGraph() {
        return getViewModel().getTypeGraph();
    }

    /** Returns the name of this aspect model as a qualified name. */
    public QualName getQualName() {
        return getViewModel().getQualName();
    }

    /** Changes the name of the model (and the underlying graph). */
    public void setQualName(QualName name) {
        getViewModel().setQualName(name);
    }

    /**
     * Returns the properties associated with this j-model.
     */
    public final ResourceProperties getProperties() {
        return getViewModel().getProperties();
    }

    /** Change the being-edited flag of the view model. */
    public void setBeingEdited(boolean flag) {
        getViewModel().setBeingEdited(flag);
    }

    /**
     * New source is only acceptable if not <tt>null</tt>.
     */
    @Override
    public boolean acceptsSource(Object edge, Object port) {
        return port != null;// && port != ((ViewEdge) edge).getTarget();
    }

    /**
     * Overrides the method so also incident edges of removed nodes are removed.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void remove(Object[] roots) {
        List<Object> removables = new LinkedList<>(Arrays.asList(roots));
        for (Object element : roots) {
            if (element instanceof AspectJVertex cell) {
                removables.addAll(cell.getPort().getEdges());
            }
        }
        super.remove(removables.toArray());
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void insert(Object[] roots, Map attributes, ConnectionSet cs, ParentMap pm,
                       UndoableEdit[] edits) {
        Set<Object> insertables = new LinkedHashSet<>();
        // only copy edges whose source and target ports are connected
        for (Object root : roots) {
            boolean insert = true;
            if (root instanceof AspectJEdge jEdge) {
                DefaultPort sourcePort = (DefaultPort) cs.getPort(jEdge, true);
                DefaultPort targetPort = (DefaultPort) cs.getPort(jEdge, false);
                insert = sourcePort != null && targetPort != null;
            }
            if (insert) {
                insertables.add(root);
            } else {
                // if the root is not copied over, remove it from the attribute map
                // to avoid its being flagged as a changed element
                attributes.remove(root);
            }
        }
        // adjust the connection set by removing all connections for edges
        // that were just removed
        if (cs != null) {
            Iterator it = cs.connections();
            while (it.hasNext()) {
                ConnectionSet.Connection conn = (ConnectionSet.Connection) it.next();
                if (!insertables.contains(conn.getEdge())) {
                    it.remove();
                }
            }
        }
        super.insert(insertables.toArray(), attributes, cs, pm, edits);
    }

    @Override
    public Map<?,?> cloneCells(Object[] cells) {
        Map<?,?> result = super.cloneCells(cells);
        // assign new node numbers to the JVertices
        getViewModel().startNodeNumbering();
        // we reuse the JCells to keep their connection and user object intact;
        // however, all auxiliary structures need to be cleared
        List<AspectJVertex> newJVertices = new ArrayList<>();
        for (Object cell : result.values()) {
            AspectViewCell jCell = null;
            if (cell instanceof AspectJVertex jVertex) {
                jVertex.setNode(createAspectNode());
                newJVertices.add(jVertex);
                jCell = jVertex;
            } else if (cell instanceof AspectJEdge jEdge) {
                jCell = jEdge;
            }
            if (jCell != null) {
                ((AJCell<?,?,?>) jCell).setJModel(this);
                jCell.initialise();
            }
        }
        for (AspectJVertex jVertex : newJVertices) {
            jVertex.setNodeFixed();
        }
        getViewModel().stopNodeNumbering();
        return result;
    }

    /**
     * Notifies the model (but not the listeners) that the underlying graph has changed.
     * @see AspectJModel#setGraphModified()
     */
    public void setGraphDirty() {
        getViewModel().setGraphDirty();
    }

    /**
     * Notifies the model and all listeners that the underlying graph has
     * been modified.
     */
    public void setGraphModified() {
        getViewModel().setGraphModified();
    }

    /**
     * We override this method to ensure that the aspect graph
     * remains in sync with any changes made to the JModel, <i>before</i>
     * the listeners are notified of the changes.
     * If a relevant change was made, the view model's
     * {@link AspectGraphViewModel#syncGraph()} is invoked.
     */
    @Override
    protected void fireGraphChanged(Object source, GraphModelChange edit) {
        // synchronise the graph to match the edits,
        // unless the model is busy loading the graph
        if (!isLoading()) {
            // only reload if the edit changed the graph structure
            // (and not just the layout)
            boolean changed = edit.getInserted() != null && edit.getInserted().length > 0
                || edit.getRemoved() != null && edit.getRemoved().length > 0
                || edit.getConnectionSet() != null && !edit.getConnectionSet().isEmpty();
            // only user object changes in the attribute should trigger a reload
            if (!changed && edit.getAttributes() != null) {
                for (Object attrValue : ((Map<?,?>) edit.getAttributes()).values()) {
                    // the user object changed if the attribute map contains an
                    // entry for the VALUE key
                    AttributeMap attrMap = (AttributeMap) attrValue;
                    if (attrMap.containsKey(GraphConstants.VALUE)) {
                        changed = true;
                        break;
                    }
                }
            }
            if (changed) {
                syncGraph();
            }
        }
        if (GUI_DEBUG) {
            System.out.printf("Firing graph change in %s%n", getName());
            printStackTrace(System.out, false);
        }
        super.fireGraphChanged(source, edit);
    }

    /**
     * Creates a new aspect node, with a fresh node number and
     * the graph role taken from the editor.
     */
    AspectNode createAspectNode() {
        return getViewModel().createAspectNode();
    }

    /** Adds a listener to graph modifications. */
    public void addGraphChangeListener(PropertyChangeListener listener) {
        getViewModel().addGraphChangeListener(listener);
    }

    /** Removes a listener to graph modifications. */
    public void removeGraphChangeListener(PropertyChangeListener listener) {
        getViewModel().removeGraphChangeListener(listener);
    }

    /** Role names (for the tool tips). */
    static final Map<AspectKind,String> ROLE_NAMES = new EnumMap<>(AspectKind.class);
    /** Role descriptions (for the tool tips). */
    static final Map<AspectKind,String> ROLE_DESCRIPTIONS = new EnumMap<>(AspectKind.class);

    static private final boolean GUI_DEBUG = false;

    /** Prints the own-code part of the stack trace to the given output.
     * @param allLines if {@code true}, print all lines, otherwise just
     * those that are in own code
     */
    static private void printStackTrace(PrintStream out, boolean allLines) {
        StackTraceElement[] stackTrace = new Exception().getStackTrace();
        String method = stackTrace[1].getMethodName();
        out.printf("%s called from: %n", method);
        for (int myCode = 2; myCode < stackTrace.length; myCode++) {
            if (allLines || stackTrace[myCode].getLineNumber() >= 0) {
                out.printf("  %s%n", stackTrace[myCode]);
            }
        }
    }

    static {
        ROLE_NAMES.put(AspectKind.EMBARGO, "Embargo");
        ROLE_NAMES.put(AspectKind.READER, "Reader");
        ROLE_NAMES.put(AspectKind.CREATOR, "Creator");
        ROLE_NAMES.put(AspectKind.ADDER, "Adder");
        ROLE_NAMES.put(AspectKind.ERASER, "Eraser");
        ROLE_NAMES.put(AspectKind.REMARK, "Remark");

        ROLE_DESCRIPTIONS
            .put(AspectKind.EMBARGO, "Must be absent from a graph for this rule to apply");
        ROLE_DESCRIPTIONS.put(AspectKind.READER, "Must be matched for this rule to apply");
        ROLE_DESCRIPTIONS.put(AspectKind.CREATOR, "Will be created by applying this rule");
        ROLE_DESCRIPTIONS
            .put(AspectKind.ADDER,
                 "Must be absent from a graph for this rule to apply, and will be created when applying this rule");
        ROLE_DESCRIPTIONS.put(AspectKind.ERASER, "Will be deleted by applying this rule");
        ROLE_DESCRIPTIONS.put(AspectKind.REMARK, "Has no effect on the execution of the rule");
    }
}
