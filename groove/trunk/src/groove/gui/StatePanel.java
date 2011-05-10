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
 * $Id: StatePanel.java,v 1.31 2008-02-05 13:28:05 rensink Exp $
 */
package groove.gui;

import static groove.gui.Options.SHOW_ANCHORS_OPTION;
import static groove.gui.Options.SHOW_ASPECTS_OPTION;
import static groove.gui.Options.SHOW_NODE_IDS_OPTION;
import static groove.gui.Options.SHOW_REMARKS_OPTION;
import static groove.gui.Options.SHOW_UNFILTERED_EDGES_OPTION;
import static groove.gui.Options.SHOW_VALUE_NODES_OPTION;
import groove.graph.GraphRole;
import groove.graph.LabelStore;
import groove.graph.TypeLabel;
import groove.gui.SimulatorModel.Change;
import groove.gui.jgraph.AspectJCell;
import groove.gui.jgraph.AspectJGraph;
import groove.gui.jgraph.AspectJModel;
import groove.gui.jgraph.AspectJVertex;
import groove.gui.jgraph.GraphJModel;
import groove.gui.jgraph.JGraphMode;
import groove.io.HTMLConverter;
import groove.lts.GTS;
import groove.lts.GraphNextState;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.StartGraphState;
import groove.trans.HostEdge;
import groove.trans.HostGraph.HostToAspectMap;
import groove.trans.HostGraphMorphism;
import groove.trans.HostNode;
import groove.trans.Proof;
import groove.trans.SystemProperties;
import groove.view.FormatException;
import groove.view.GraphView;
import groove.view.StoredGrammarView;
import groove.view.TypeView;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectNode;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;

/**
 * Window that displays and controls the current state graph. Auxiliary class
 * for Simulator.
 * @author Arend Rensink
 * @version $Revision$
 */
public class StatePanel extends JGraphPanel<AspectJGraph> implements
        ListSelectionListener, SimulatorListener {
    /** Display name of this panel. */
    public static final String FRAME_NAME = "Current state";

    // --------------------- INSTANCE DEFINITIONS ----------------------

    /**
     * Constructs a new state panel.
     */
    public StatePanel(final Simulator simulator) {
        super(new AspectJGraph(simulator, GraphRole.HOST), true);
        initialise();
        getJGraph().setToolTipEnabled(true);
    }

    @Override
    protected JToolBar createToolBar() {
        JToolBar result = new JToolBar();
        result.add(getSimulator().getNewGraphAction());
        result.add(getSimulator().getEditGraphAction());
        result.add(getSimulator().getSaveGraphAction());
        result.addSeparator();
        result.add(getJGraph().getModeButton(JGraphMode.SELECT_MODE));
        result.add(getJGraph().getModeButton(JGraphMode.PAN_MODE));
        return result;
    }

    @Override
    protected void installListeners() {
        super.installListeners();
        getSimulator().addSimulatorListener(this);
        getSimulator().getStateList().addListSelectionListener(this);
        addRefreshListener(SHOW_NODE_IDS_OPTION);
        addRefreshListener(SHOW_ASPECTS_OPTION);
        addRefreshListener(SHOW_ANCHORS_OPTION);
        addRefreshListener(SHOW_REMARKS_OPTION);
        addRefreshListener(SHOW_VALUE_NODES_OPTION);
        addRefreshListener(SHOW_UNFILTERED_EDGES_OPTION);
        // make sure that removals from the selection model
        // also deselect the match
        getJGraph().addGraphSelectionListener(new GraphSelectionListener() {
            @Override
            public void valueChanged(GraphSelectionEvent e) {
                if (!StatePanel.this.changing
                    && StatePanel.this.selectedMatch != null) {
                    // change only if cells were removed
                    boolean removed = false;
                    Object[] cells = e.getCells();
                    for (int i = 0; !removed && i < cells.length; i++) {
                        removed = !e.isAddedCell(i);
                    }
                    if (removed) {
                        clearSelectedMatch(false);
                    }
                }
            }
        });
        getJGraph().getLabelTree().addLabelStoreObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                assert arg instanceof LabelStore;
                final SystemProperties newProperties =
                    getGrammar().getProperties().clone();
                newProperties.setSubtypes(((LabelStore) arg).toDirectSubtypeString());
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        getSimulator().doSaveProperties(newProperties);
                    }
                });
            }
        });
    }

    /**
     * Specialises the return type to {@link GraphJModel}.
     */
    @Override
    public AspectJModel getJModel() {
        return (AspectJModel) super.getJModel();
    }

    /**
     * Sets the graph model in the jgraph. Convenience method for
     * <code>this.jGraph.setModel(newModel)</code>.
     */
    private void setJModel(AspectJModel newModel) {
        clearSelectedMatch(true);
        this.jGraph.setModel(newModel);
    }

    /**
     * Sets the underlying model of this state frame to the initial graph of the
     * new grammar.
     */
    private synchronized void setGrammarUpdate(StoredGrammarView grammar) {
        this.stateToAspectMap.clear();
        this.graphToJModel.clear();
        this.selectedState = null;
        this.selectedGraph = null;
        this.jGraph.getFilteredLabels().clear();
        if (grammar == null || grammar.getStartGraphView() == null) {
            setEnabled(false);
            setJModel(null);
            getJGraph().setType(null, null);
        } else {
            // first enable so the edge label background is set correctly
            setEnabled(true);
            // reset the model so it doesn't get mixed up with the new type
            getJGraph().setModel(null);
            // set the type or the label store for the JGraph
            if (!grammar.getActiveTypeNames().isEmpty()) {
                Map<String,Set<TypeLabel>> labelsMap =
                    new HashMap<String,Set<TypeLabel>>();
                try {
                    for (String typeName : grammar.getActiveTypeNames()) {
                        TypeView view = grammar.getTypeView(typeName);
                        // the view may be null if type names
                        // overlap modulo upper/lowercase
                        if (view != null) {
                            labelsMap.put(typeName, view.getLabels());
                        }
                    }
                    getJGraph().setType(grammar.toModel().getType(), labelsMap);
                } catch (FormatException e) {
                    getJGraph().setLabelStore(grammar.getLabelStore());
                }
            } else {
                getJGraph().setLabelStore(grammar.getLabelStore());
            }
            GraphView startGraphView = grammar.getStartGraphView();
            setGraphModel(startGraphView.getName());
        }
        refreshStatus();
    }

    private synchronized void startSimulationUpdate(GTS gts) {
        // clear the states from the aspect and model maps
        for (HostToAspectMap aspectMap : this.stateToAspectMap.values()) {
            this.graphToJModel.remove(aspectMap.getAspectGraph());
        }
        this.stateToAspectMap.clear();
        // only change the displayed model if we are currently displaying a
        // state
        if (this.selectedGraph == null) {
            setStateModel(gts.startState());
        }
        refreshStatus();
    }

    /**
     * Emphasise the given match.
     * @param match the match to be emphasised (non-null)
     */
    private void setMatchUpdate(Proof match) {
        assert match != null : "Match update should not be called with empty match";
        setStateModel(getSimulatorModel().getState());
        AspectJModel jModel = getJModel();
        HostToAspectMap aspectMap = getAspectMap(this.selectedState);
        Set<AspectJCell> emphElems = new HashSet<AspectJCell>();
        for (HostNode matchedNode : match.getNodeValues()) {
            AspectJCell jCell =
                jModel.getJCellForNode(aspectMap.getNode(matchedNode));
            if (jCell != null) {
                emphElems.add(jCell);
            }
        }
        for (HostEdge matchedEdge : match.getEdgeValues()) {
            AspectJCell jCell =
                jModel.getJCellForEdge(aspectMap.getEdge(matchedEdge));
            if (jCell != null) {
                emphElems.add(jCell);
            }
        }
        assert !this.changing;
        this.changing = true;
        this.jGraph.setSelectionCells(emphElems.toArray());
        this.changing = false;
        this.selectedMatch = match;
        refreshStatus();
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel,
            Set<Change> changes) {
        if (changes.contains(Change.GRAMMAR)) {
            setGrammarUpdate(source.getGrammar());
        } else if (changes.contains(Change.GTS)) {
            startSimulationUpdate(source.getGts());
        } else if (changes.contains(Change.STATE)) {
            GraphState newState = source.getState();
            if (newState == null) {
                if (clearSelectedMatch(true)) {
                    refreshStatus();
                }
            } else {
                GraphTransition transition = oldModel.getTransition();
                GraphState target =
                    transition == null ? null : transition.target();
                if (target == newState) {
                    HostGraphMorphism morphism = transition.getMorphism();
                    copyLayout(getAspectMap(transition.source()),
                        getAspectMap(newState), morphism);
                }
                // set the graph model to the new state
                setStateModel(newState);
            }
        }
        if (changes.contains(Change.EVENT)) {
            if (source.getEvent() == null) {
                if (clearSelectedMatch(true)) {
                    refreshStatus();
                }
            } else {
                setMatchUpdate(source.getEvent().getMatch(
                    source.getState().getGraph()));
            }
        }
        refreshStatus();
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        assert e.getSource() == getStateList();
        int[] selection = getStateList().getSelectedIndices();
        if (selection.length == 1) {
            if (selection[0] == 0) {
                setStateModel(getSimulatorModel().getState());
            } else {
                setGraphModel((String) getStateList().getSelectedValue());
            }
        }
    }

    /** Indicates if the panel is currently showing a state (rather than
     * one of the host graphs in the grammar view).
     */
    public boolean isShowingState() {
        return this.selectedState != null;
    }

    /**
     * Changes the display to the graph with a given name, if there is such a
     * graph in the current grammar.
     */
    private void setGraphModel(String graphName) {
        GraphView graphView = getGrammar().getGraphView(graphName);
        if (graphView != null && graphName != this.selectedGraph) {
            this.selectedGraph = graphName;
            this.selectedState = null;
            setJModel(getAspectJModel(graphView.getAspectGraph()));
            setEnabled(true);
            refreshStatus();
        }
    }

    /** Changes the display to a given state. */
    private void setStateModel(GraphState state) {
        boolean change = this.selectedState != state;
        if (change) {
            this.selectedGraph = null;
            this.selectedState = state;
            if (state != null) {
                setJModel(getAspectJModel(state));
                setEnabled(true);
            }
            refreshStatus();
        }
    }

    /** 
     * Clears the emphasis due to the currently selected match, if any.
     * Also changes the match selection in the rule tree to the corresponding
     * rule.
     * @param clear if {@code true}, the current selection should be cleared;
     *  otherwise it should be preserved
     */
    private boolean clearSelectedMatch(boolean clear) {
        boolean result = !this.changing && this.selectedMatch != null;
        if (result) {
            this.changing = true;
            this.selectedMatch = null;
            if (clear) {
                getJGraph().clearSelection();
            }
            this.changing = false;
        }
        return result;
    }

    /**
     * Text to indicate which state is chosen and which match is emphasised.
     */
    @Override
    protected String getStatusText() {
        StringBuilder result = new StringBuilder();
        if (this.selectedGraph != null) {
            result.append("Graph: ");
            result.append(HTMLConverter.STRONG_TAG.on(this.selectedGraph));
        } else if (this.selectedState != null) {
            result.append(FRAME_NAME);
            result.append(": ");
            result.append(HTMLConverter.STRONG_TAG.on(this.selectedState.toString()));
            GraphTransition trans = getSimulatorModel().getTransition();
            if (trans != null) {
                if (getOptions().isSelected(SHOW_ANCHORS_OPTION)) {
                    result.append(String.format(" (with match %s)",
                        trans.getEvent()));
                } else {
                    result.append(String.format(" (with match of %s)",
                        trans.getEvent().getRule().getName()));
                }
            }
        } else {
            result.append(FRAME_NAME);
        }
        return HTMLConverter.HTML_TAG.on(result).toString();
    }

    /**
     * Returns a graph model for a given state graph. The graph model is
     * retrieved from stateJModelMap; if there is no image for the requested
     * state then one is created.
     */
    private AspectJModel getAspectJModel(GraphState state) {
        HostToAspectMap aspectMap = getAspectMap(state);
        AspectGraph aspectGraph = aspectMap.getAspectGraph();
        AspectJModel result = this.graphToJModel.get(aspectGraph);
        if (result == null) {
            result = createAspectJModel(aspectGraph);
            assert result != null;
            this.graphToJModel.put(aspectGraph, result);
            // try to find layout information for the model
            if (state instanceof GraphNextState) {
                GraphState oldState = ((GraphNextState) state).source();
                HostGraphMorphism morphism =
                    ((GraphNextState) state).getMorphism();
                // walk back along the derivation chain to find one for
                // which we have a state model (and hence layout information)
                while (!this.stateToAspectMap.containsKey(oldState)
                    && oldState instanceof GraphNextState) {
                    morphism =
                        ((GraphNextState) oldState).getMorphism().then(morphism);
                    oldState = ((GraphNextState) oldState).source();
                }
                // the following call will make sure the start state
                // is actually loaded
                getAspectJModel(oldState);
                HostToAspectMap oldStateMap = getAspectMap(oldState);
                copyLayout(oldStateMap, aspectMap, morphism);
            } else {
                assert state instanceof StartGraphState;
                // this is the start state
                AspectGraph startGraph =
                    getGrammar().getStartGraphView().getAspectGraph();
                AspectJModel startModel = getAspectJModel(startGraph);
                for (AspectNode node : startGraph.nodeSet()) {
                    AspectJVertex stateVertex = result.getJCellForNode(node);
                    // meta nodes are not in the state;
                    // data nodes may have been merged
                    if (stateVertex == null) {
                        continue;
                    }
                    AspectJVertex graphVertex =
                        startModel.getJCellForNode(node);
                    stateVertex.refreshAttributes();
                    stateVertex.getAttributes().applyMap(
                        graphVertex.getAttributes());
                    stateVertex.setGrayedOut(graphVertex.isGrayedOut());
                    result.synchroniseLayout(stateVertex);
                    stateVertex.setLayoutable(false);
                }
                for (AspectEdge edge : startGraph.edgeSet()) {
                    AspectJCell stateEdge = result.getJCellForEdge(edge);
                    // meta edges and merged data edges are not in the state
                    if (stateEdge == null) {
                        continue;
                    }
                    AspectJCell graphEdge = startModel.getJCellForEdge(edge);
                    stateEdge.refreshAttributes();
                    stateEdge.getAttributes().applyMap(
                        graphEdge.getAttributes());
                    stateEdge.setGrayedOut(graphEdge.isGrayedOut());
                    result.synchroniseLayout(stateEdge);
                    stateEdge.setLayoutable(false);
                }
            }
        }
        return result;
    }

    /**
     * Returns a graph model for a given graph view. The graph model is
     * retrieved from {@link #graphToJModel}; if there is no image for the
     * requested state then one is created using
     * {@link #createAspectJModel(AspectGraph)}.
     */
    private AspectJModel getAspectJModel(AspectGraph graph) {
        AspectJModel result = this.graphToJModel.get(graph);
        if (result == null) {
            result = createAspectJModel(graph);
            this.graphToJModel.put(graph, result);
        }
        return result;
    }

    /** Creates a j-model for a given aspect graph. */
    private AspectJModel createAspectJModel(AspectGraph graph) {
        AspectJModel result = getJGraph().newModel();
        result.loadGraph(graph);
        return result;
    }

    /**
     * Copies the layout information from one JModel to another,
     * modulo a mapping from the nodes and edges of the underlying graphs.
     * @param oldState the host graph to copy the layout information from
     * @param newState the host graph to copy the layout information to
     * @param morphism mapping from the nodes and edges of the old to the
     *        new host graph
     */
    private void copyLayout(HostToAspectMap oldState, HostToAspectMap newState,
            HostGraphMorphism morphism) {
        AspectJModel oldStateJModel =
            getAspectJModel(oldState.getAspectGraph());
        AspectJModel newStateJModel =
            getAspectJModel(newState.getAspectGraph());
        // initially set all cells of the new model to layoutable
        for (AspectJCell jCell : newStateJModel.getRoots()) {
            jCell.setLayoutable(true);
        }
        for (Map.Entry<HostNode,HostNode> entry : morphism.nodeMap().entrySet()) {
            AspectNode oldStateNode = oldState.getNode(entry.getKey());
            AspectNode newStateNode = newState.getNode(entry.getValue());
            AspectJCell sourceCell =
                oldStateJModel.getJCellForNode(oldStateNode);
            assert sourceCell != null : "Source element " + oldStateNode
                + " unknown";
            AspectJCell targetCell =
                newStateJModel.getJCellForNode(newStateNode);
            assert targetCell != null : "Target element " + newStateNode
                + " unknown";
            Rectangle2D sourceBounds =
                GraphConstants.getBounds(sourceCell.getAttributes());
            GraphConstants.setBounds(targetCell.getAttributes(), sourceBounds);
            targetCell.setLayoutable(false);
            targetCell.setGrayedOut(sourceCell.isGrayedOut());
            newStateJModel.synchroniseLayout(targetCell);
        }
        Set<AspectEdge> newEdges =
            new HashSet<AspectEdge>(newStateJModel.getGraph().edgeSet());
        for (Map.Entry<HostEdge,HostEdge> entry : morphism.edgeMap().entrySet()) {
            AspectEdge oldStateEdge = oldState.getEdge(entry.getKey());
            AspectEdge newStateEdge = newState.getEdge(entry.getValue());
            AspectJCell sourceCell =
                oldStateJModel.getJCellForEdge(oldStateEdge);
            AttributeMap sourceAttributes = sourceCell.getAttributes();
            AspectJCell targetCell =
                newStateJModel.getJCellForEdge(newStateEdge);
            assert targetCell != null : "Target element " + newStateEdge
                + " unknown";
            AttributeMap targetAttributes = targetCell.getAttributes();
            List<?> sourcePoints = GraphConstants.getPoints(sourceAttributes);
            if (sourcePoints != null) {
                GraphConstants.setPoints(targetAttributes,
                    new LinkedList<Object>(sourcePoints));
            }
            Point2D labelPosition =
                GraphConstants.getLabelPosition(sourceAttributes);
            if (labelPosition != null) {
                GraphConstants.setLabelPosition(targetAttributes, labelPosition);
            }
            GraphConstants.setLineStyle(targetAttributes,
                GraphConstants.getLineStyle(sourceAttributes));
            targetCell.setLayoutable(false);
            targetCell.setGrayedOut(sourceCell.isGrayedOut());
            newStateJModel.synchroniseLayout(targetCell);
            newEdges.remove(newStateEdge);
        }
    }

    /**
     * Returns the state list associated with the simulator. Convenience method
     * for <code>getSimulator().getStateList()</code>.
     */
    private StateJList getStateList() {
        return getSimulator().getStateList();
    }

    /** Convenience method to retrieve the current grammar view. */
    private StoredGrammarView getGrammar() {
        return getSimulatorModel().getGrammar();
    }

    /** 
     * Returns the aspect map for a given state. 
     * Retrieves the result from {@link #stateToAspectMap},
     * creating and inserting it if necessary.
     */
    private HostToAspectMap getAspectMap(GraphState state) {
        HostToAspectMap result = this.stateToAspectMap.get(state);
        if (result == null) {
            this.stateToAspectMap.put(state, result =
                state.getGraph().toAspectMap());
        }
        return result;
    }

    /**
     * Mapping from graphs to the corresponding graph models.
     */
    private final Map<AspectGraph,AspectJModel> graphToJModel =
        new HashMap<AspectGraph,AspectJModel>();
    /**
     * Mapping from graphs to the corresponding graph models.
     */
    private final Map<GraphState,HostToAspectMap> stateToAspectMap =
        new HashMap<GraphState,HostToAspectMap>();
    /** The currently emphasised match (nullable). */
    private Proof selectedMatch;

    /** Either {@code null} or the graph currently showing in the panel. */
    private String selectedGraph;
    /** Either {@code null} or the state currently showing in the panel. */
    private GraphState selectedState;
    /** 
     * Flag indicating that a status change in the simulator is initiated
     * from this object (so we shouldn't react to it)
     */
    private boolean changing;
}