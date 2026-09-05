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
import nl.utwente.groove.grammar.aspect.AspectNode;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.GraphBasedModel;
import nl.utwente.groove.grammar.type.TypeGraph;
import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.Element;
import nl.utwente.groove.graph.Node;
import nl.utwente.groove.gui.view.AspectGraphViewModel;
import nl.utwente.groove.gui.view.AspectViewCell;
import nl.utwente.groove.gui.view.AspectViewVertex;
import nl.utwente.groove.gui.view.cell.AspectEdgeCell;
import nl.utwente.groove.gui.view.cell.AspectVertexCell;
import nl.utwente.groove.util.QualName;

/**
 * JGraph model adapter of an {@link AspectGraphViewModel}: keeps the JGraph
 * edit semantics (insertion, removal, cloning, connection acceptance) and asks
 * the view model to rebuild the graph after a structural edit.
 * @author Arend Rensink
 * @version $Revision$
 */
final public class AspectJModel extends JModel<@NonNull AspectGraph> {
    /** Creates a new model for a given aspect JGraph. */
    AspectJModel(AspectJGraph jGraph) {
        super(jGraph);
    }

    @Override
    public AspectGraphViewModel getViewModel() {
        return (AspectGraphViewModel) super.getViewModel();
    }

    /* Specialises the return type. */
    @Override
    public AspectJGraph getJGraph() {
        return (AspectJGraph) super.getJGraph();
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

    @Override
    public AspectViewCell getJCellForEdge(Edge edge) {
        return getViewModel().getJCellForEdge(edge);
    }

    @Override
    public AspectViewVertex getJCellForNode(Node node) {
        return getViewModel().getJCellForNode(node);
    }

    /**
     * Creates a new model with the same grammar and editing status as this one,
     * loaded with a given graph.
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

    /** Reconstructs the aspect graph on the basis of the current cells. */
    public void syncGraph() {
        getViewModel().syncGraph();
    }

    /** Returns the resource model of the graph shown. */
    public GraphBasedModel<?> getResourceModel() {
        return getViewModel().getResourceModel();
    }

    /** Returns the type graph against which the graph shown is typed. */
    public TypeGraph getTypeGraph() {
        return getViewModel().getTypeGraph();
    }

    /** Returns the name of the graph shown as a qualified name. */
    public QualName getQualName() {
        return getViewModel().getQualName();
    }

    /** Changes the name of the model (and the underlying graph). */
    public void setQualName(QualName name) {
        getViewModel().setQualName(name);
    }

    /** Returns the properties associated with this model. */
    public final ResourceProperties getProperties() {
        return getViewModel().getProperties();
    }

    /** Sets or resets the flag that the graph of this model is being edited. */
    public void setBeingEdited(boolean flag) {
        getViewModel().setBeingEdited(flag);
    }

    @Override
    public boolean acceptsSource(Object edge, Object port) {
        return port != null;// && port != ((ViewEdge) edge).getTarget();
    }

    /* Removes the incident edges of removed vertices as well. */
    @SuppressWarnings("unchecked")
    @Override
    public void remove(Object[] roots) {
        List<Object> removables = new LinkedList<>(Arrays.asList(roots));
        for (Object element : roots) {
            if (element instanceof JVertex<?> cell) {
                removables.addAll(cell.getPort().getEdges());
            }
        }
        super.remove(removables.toArray());
    }

    /* Only inserts edges whose source and target ports are connected. */
    @SuppressWarnings("rawtypes")
    @Override
    public void insert(Object[] roots, Map attributes, ConnectionSet cs, ParentMap pm,
                       UndoableEdit[] edits) {
        Set<Object> insertables = new LinkedHashSet<>();
        for (Object root : roots) {
            boolean insert = true;
            if (root instanceof JEdge<?> jEdge) {
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

    /*
     * The clones are bound to this model and their vertices get fresh node
     * numbers; the clones of the view cells are made by the JGraph cells.
     */
    @Override
    public Map<?,?> cloneCells(Object[] cells) {
        Map<?,?> result = super.cloneCells(cells);
        // assign new node numbers to the vertices
        getViewModel().startNodeNumbering();
        List<AspectVertexCell> newVertices = new ArrayList<>();
        for (Object cell : result.values()) {
            if (cell instanceof JVertex<?> jVertex
                && jVertex.getViewCell() instanceof AspectVertexCell vertex) {
                vertex.setViewModel(getViewModel());
                vertex.setNode(createAspectNode());
                vertex.initialise();
                newVertices.add(vertex);
            } else if (cell instanceof JEdge<?> jEdge
                && jEdge.getViewCell() instanceof AspectEdgeCell edge) {
                edge.setViewModel(getViewModel());
                edge.initialise();
            }
        }
        for (AspectVertexCell vertex : newVertices) {
            vertex.setNodeFixed();
        }
        getViewModel().stopNodeNumbering();
        return result;
    }

    /** Flags the graph as dirty, so it is rebuilt on the next request. */
    public void setGraphDirty() {
        getViewModel().setGraphDirty();
    }

    /** Signals a modification of the graph to the graph change listeners. */
    public void setGraphModified() {
        getViewModel().setGraphModified();
    }

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
}
