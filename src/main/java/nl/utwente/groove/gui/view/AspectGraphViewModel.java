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
package nl.utwente.groove.gui.view;

import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.ResourceProperties;
import nl.utwente.groove.grammar.aspect.AspectEdge;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.aspect.AspectNode;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.GraphBasedModel;
import nl.utwente.groove.grammar.type.TypeGraph;
import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.Element;
import nl.utwente.groove.graph.GraphInfo;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.graph.Node;
import nl.utwente.groove.graph.layout.EdgeLayout;
import nl.utwente.groove.graph.layout.LayoutMap;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.ChangeCount;
import nl.utwente.groove.util.ChangeCount.Derived;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.parse.FormatError;

/**
 * View model of an aspect graph: a graph-based resource of a grammar, typed
 * against that grammar and possibly under edit. Knows how to rebuild the aspect
 * graph from the cells after an edit, how the grammar diagnoses the graph, and
 * how fresh nodes are numbered.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public class AspectGraphViewModel extends GraphViewModel<AspectGraph> {
    /**
     * Creates an new model, initially without a graph or grammar loaded.
     * Call {@link #setGrammar(GrammarModel)} to complete construction.
     */
    public AspectGraphViewModel(AspectGraphViewController controller,
                                CellStore<AspectGraph> store) {
        super(controller, store);
        this.graphModCount = new ChangeCount();
        this.resource = new Derived<>(this.graphModCount) {
            @Override
            protected GraphBasedModel<?> computeValue() {
                return getNonNullGrammar().createGraphModel(getNonNullGraph());
            }
        };
        this.typeGraph = new Derived<>(this.graphModCount) {
            @Override
            protected TypeGraph computeValue() {
                return getResourceModel().getTypeGraph();
            }
        };
        addGraphChangeListener(evt -> loadViewErrors());
    }

    @Override
    public AspectGraphViewController getController() {
        return (AspectGraphViewController) super.getController();
    }

    /** Sets a grammar model, with respect to which typing is resolved. */
    public void setGrammar(GrammarModel grammar) {
        assert this.grammar == null || this.grammar == grammar;
        this.grammar = grammar;
    }

    /** Returns the (possibly {@code null}) grammar set for this model. */
    public @Nullable GrammarModel getGrammar() {
        return this.grammar;
    }

    /** Returns the grammar set for this model; fails if there is none. */
    private GrammarModel getNonNullGrammar() {
        var result = getGrammar();
        assert result != null; // set right after construction
        return result;
    }

    /** Returns the graph of this model; fails if there is none. */
    private AspectGraph getNonNullGraph() {
        var result = getGraph();
        assert result != null; // loaded right after construction, before any content is used
        return result;
    }

    /** The associated grammar. */
    private @Nullable GrammarModel grammar;

    @Override
    public @Nullable AspectViewCell getJCell(Element elem) {
        return (AspectViewCell) super.getJCell(elem);
    }

    @Override
    public @Nullable AspectViewCell getJCellForEdge(Edge edge) {
        return (AspectViewCell) super.getJCellForEdge(edge);
    }

    @Override
    public @Nullable AspectViewVertex getJCellForNode(Node node) {
        return (AspectViewVertex) super.getJCellForNode(node);
    }

    @Override
    public void loadGraph(AspectGraph graph) {
        boolean wasLoading = isLoading();
        setLoading(true);
        setGraphDirty();
        // signal that graph is modified twice, to ensure
        // that all resources get synced properly
        super.loadGraph(graph);
        for (var cell : getCells()) {
            ((AspectViewCell) cell).refreshEditableLabels();
        }
        this.properties = ResourceProperties.getProperties(graph);
        setLoading(wasLoading);
        setGraphModified();
    }

    @Override
    public void setGraph(AspectGraph graph) {
        super.setGraph(graph);
        setGraphModified();
    }

    /**
     * Reconstructs the aspect graph on the basis of the current cells.
     * This method should be called immediately after the changes to
     * the cells have been made, but before any graph listeners are
     * notified.
     */
    public void syncGraph() {
        if (isLoading()) {
            return;
        }
        var grammar = getNonNullGrammar();
        var oldGraph = getNonNullGraph();
        GraphRole role = oldGraph.getRole();
        Map<AspectNode,AspectViewVertex> nodeJVertexMap = new HashMap<>();
        Map<AspectEdge,AspectViewCell> edgeJCellMap = new HashMap<>();
        AspectGraph graph = new AspectGraph(oldGraph.getName(), role,
            !grammar.getProperties().getSemantics().isMulti());
        graph.setTypeSortMap(grammar.getTypeModel().getTypeSortMap());
        for (var cell : getCells()) {
            if (cell instanceof AspectViewVertex jVertex) {
                jVertex.applyEditableLabels(graph);
                graph.addNode(jVertex.getNode());
                nodeJVertexMap.put(jVertex.getNode(), jVertex);
                for (AspectEdge edge : jVertex.getEdges()) {
                    edgeJCellMap.put(edge, jVertex);
                    graph.addEdgeContext(edge);
                }
            }
        }
        for (var cell : getCells()) {
            if (cell instanceof AspectViewEdge jEdge) {
                jEdge.applyEditableLabels(graph);
                for (AspectEdge edge : jEdge.getEdges()) {
                    edgeJCellMap.put(edge, jEdge);
                    graph.addEdgeContext(edge);
                }
            }
        }
        for (AspectViewVertex jVertex : nodeJVertexMap.values()) {
            jVertex.setNodeFixed();
        }
        // collect the layout information
        LayoutMap layoutMap = new LayoutMap();
        for (var cell : getCells()) {
            if (cell instanceof AspectViewVertex jVertex) {
                layoutMap.putNode(jVertex.getNode(), jVertex.getLayoutVisuals().toNodeLayout());
            } else if (cell instanceof AspectViewEdge jEdge) {
                EdgeLayout layout = jEdge.getLayoutVisuals().toEdgeLayout();
                if (!layout.isDefault()) {
                    for (AspectEdge edge : jEdge.getEdges()) {
                        layoutMap.putEdge(edge, layout);
                    }
                }
            }
        }
        GraphInfo.setLayoutMap(graph, layoutMap);
        ResourceProperties.setProperties(graph, getProperties());
        graph.setFixed();
        setJCellMaps(nodeJVertexMap, edgeJCellMap);
        setGraph(graph);
    }

    /**
     * Sets the extra-error flags of all the cells, based
     * on the errors in the view.
     */
    private void loadViewErrors() {
        if (getGrammar() == null) {
            return;
        }
        for (var cell : getCells()) {
            ((AspectViewCell) cell).getErrors().clear();
        }
        for (FormatError error : getResourceModel().getErrors()) {
            for (Element errorObject : error.getContext(Element.class)) {
                AspectViewCell errorCell = getJCell(errorObject);
                if (errorCell == null && errorObject instanceof Edge e) {
                    errorCell = getJCell(e.source());
                }
                if (errorCell != null) {
                    errorCell.getErrors().addError(error, true);
                }
            }
        }
    }

    /** Returns an up-to-date resource model for the graph being edited here. */
    public GraphBasedModel<?> getResourceModel() {
        return this.resource.getValue();
    }

    /** Returns the type graph associated with this model, if any. */
    public TypeGraph getTypeGraph() {
        return this.typeGraph.getValue();
    }

    /** Returns the name of this aspect model as a qualified name. */
    public QualName getQualName() {
        return QualName.parse(getNonNullGraph().getName());
    }

    /** Changes the name of the model (and the underlying graph). */
    public void setQualName(QualName name) {
        setGraph(getNonNullGraph().rename(name));
    }

    /**
     * Returns the properties associated with this model.
     */
    public final ResourceProperties getProperties() {
        var result = this.properties;
        if (result == null) {
            this.properties = result = new ResourceProperties();
        }
        return result;
    }

    /**
     * Enable bidirectional edges to be merged, if the aspect graph is a host
     * graph, and the grammar property is set to true.
     */
    @Override
    public boolean isMergeBidirectionalEdges() {
        if (this.beingEdited || getNonNullGraph().getRole() != GraphRole.HOST) {
            return false;
        } else {
            return super.isMergeBidirectionalEdges();
        }
    }

    /** Change the {@link #beingEdited} flag. */
    public void setBeingEdited(boolean flag) {
        this.beingEdited = flag;
    }

    /** Indicates if the graph of this model is being edited. */
    public boolean isBeingEdited() {
        return this.beingEdited;
    }

    /**
     * Creates a new aspect node, with a fresh node number and
     * the graph role taken from the current graph.
     */
    public AspectNode createAspectNode() {
        return new AspectNode(createNewNodeNr(), getNonNullGraph());
    }

    /**
     * Starts a batch of node number requests: the numbers in use are collected once,
     * and every number handed out until {@link #stopNodeNumbering()} is reserved.
     */
    public void startNodeNumbering() {
        collectNodeNrs();
    }

    /** Ends a batch of node number requests; see {@link #startNodeNumbering()}. */
    public void stopNodeNumbering() {
        resetNodeNrs();
    }

    /** Initialises the set {@link #usedNrs} with the currently used node numbers,
     * if that has not been done yet.
     * @return {@code true} if the numbers were collected by this call
     */
    private boolean collectNodeNrs() {
        boolean result = this.usedNrs == null;
        if (result) {
            Set<Integer> usedNrs = new HashSet<>();
            for (var cell : getCells()) {
                if (cell instanceof ViewVertex<?> v) {
                    usedNrs.add(v.getNumber());
                }
            }
            this.usedNrs = usedNrs;
        }
        return result;
    }

    /** Resets the set of used node numbers to {@code null}. */
    private void resetNodeNrs() {
        this.usedNrs = null;
    }

    /**
     * Returns the first non-negative number that is not used as a node number
     * in this model.
     */
    private int createNewNodeNr() {
        int result = 0;
        boolean collect = collectNodeNrs();
        var usedNrs = this.usedNrs;
        assert usedNrs != null; // just collected
        // search for an unused node number
        while (usedNrs.contains(result)) {
            result++;
        }
        if (collect) {
            resetNodeNrs();
        } else {
            usedNrs.add(result);
        }
        return result;
    }

    /**
     * Notifies the model (but not the listeners) that the underlying graph has changed.
     * @see #setGraphModified()
     */
    public void setGraphDirty() {
        this.graphModCount.increaseSilent();
    }

    /**
     * Notifies the model and all listeners that the underlying graph has
     * been modified.
     */
    public void setGraphModified() {
        this.graphModCount.increase();
    }

    /** Adds a listener to graph modifications. */
    public void addGraphChangeListener(PropertyChangeListener listener) {
        this.graphModCount.addObserver(listener);
    }

    /** Removes a listener to graph modifications. */
    public void removeGraphChangeListener(PropertyChangeListener listener) {
        this.graphModCount.deleteObserver(listener);
    }

    /** Counter of the modifications to the graph. */
    private final ChangeCount graphModCount;
    /** The resource model of the graph being edited. */
    private final Derived<GraphBasedModel<?>> resource;
    /** The type graph of the graph being edited. */
    private final Derived<TypeGraph> typeGraph;
    /** Flag to indicate if the graph is being edited or not. */
    private boolean beingEdited = false;
    /** Properties map of the graph being displayed or edited. */
    private @Nullable ResourceProperties properties;
    /** The set of used node numbers, during a numbering batch. */
    private @Nullable Set<Integer> usedNrs;
}
