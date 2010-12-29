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
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.LabelStore;
import groove.graph.TypeLabel;
import groove.gui.jgraph.AspectJModel;
import groove.gui.jgraph.GraphJModel;
import groove.gui.jgraph.JCell;
import groove.gui.jgraph.JEdge;
import groove.gui.jgraph.StateJGraph;
import groove.lts.GTS;
import groove.lts.GraphNextState;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.HostEdge;
import groove.trans.HostGraph.HostToAspectMap;
import groove.trans.HostGraphMorphism;
import groove.trans.HostNode;
import groove.trans.RuleMatch;
import groove.trans.RuleName;
import groove.trans.SystemProperties;
import groove.util.Converter;
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

import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;

/**
 * Window that displays and controls the current state graph. Auxiliary class
 * for Simulator.
 * @author Arend Rensink
 * @version $Revision$
 */
public class StatePanel extends JGraphPanel<StateJGraph> implements
        SimulationListener, ListSelectionListener {
    /** Display name of this panel. */
    public static final String FRAME_NAME = "Current state";

    // --------------------- INSTANCE DEFINITIONS ----------------------

    /**
     * Constructs a new state panel.
     */
    public StatePanel(final Simulator simulator) {
        super(new StateJGraph(simulator), true, true, simulator.getOptions());
        this.simulator = simulator;
        simulator.addSimulationListener(this);
        simulator.getStateList().addListSelectionListener(this);
        addRefreshListener(SHOW_NODE_IDS_OPTION);
        addRefreshListener(SHOW_ASPECTS_OPTION);
        addRefreshListener(SHOW_ANCHORS_OPTION);
        addRefreshListener(SHOW_REMARKS_OPTION);
        addRefreshListener(SHOW_VALUE_NODES_OPTION);
        addRefreshListener(SHOW_UNFILTERED_EDGES_OPTION);
        getJGraph().setToolTipEnabled(true);
        // make sure that emphasis due to selections in the label tree
        // cause any selected transition to be deselected first
        getJGraph().getLabelTree().addTreeSelectionListener(
            new TreeSelectionListener() {
                public void valueChanged(TreeSelectionEvent e) {
                    RuleMatch match = StatePanel.this.selectedMatch;
                    if (match != null) {
                        simulator.setRule(match.getRule().getName());
                    }
                }
            });
        // ensure that changes in the inheritance strocture of the label tree
        // get stored in the properties
        getJGraph().getLabelTree().addLabelStoreObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                assert arg instanceof LabelStore;
                final SystemProperties newProperties =
                    simulator.getGrammarView().getProperties().clone();
                newProperties.setSubtypes(((LabelStore) arg).toDirectSubtypeString());
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        simulator.doSaveProperties(newProperties);
                    }
                });
            }
        });
        setEnabled(false);
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
        clearSelectedMatch();
        this.jGraph.setModel(newModel);
    }

    /**
     * Sets the underlying model of this state frame to the initial graph of the
     * new grammar.
     */
    public synchronized void setGrammarUpdate(StoredGrammarView grammar) {
        this.stateToAspectMap.clear();
        this.graphToJModel.clear();
        this.jGraph.getFilteredLabels().clear();
        if (grammar == null || grammar.getStartGraphView() == null) {
            setJModel(AspectJModel.EMPTY_ASPECT_JMODEL);
            setEnabled(false);
        } else {
            GraphView startGraphView = grammar.getStartGraphView();
            Map<String,Set<TypeLabel>> labelsMap =
                new HashMap<String,Set<TypeLabel>>();
            for (String typeName : grammar.getActiveTypeNames()) {
                try {
                    TypeView view = grammar.getTypeView(typeName);
                    labelsMap.put(typeName, view.getLabels());
                } catch (FormatException e) {
                    // don't add labels from this type view
                }
            }
            this.jGraph.setLabelStore(grammar.getLabelStore(), labelsMap);
            setGraphModel(startGraphView.getName());
            setEnabled(true);
        }
        refreshStatus();
    }

    public synchronized void startSimulationUpdate(GTS gts) {
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
     * Sets the underlying model of this state frame to a new graph. Creates a
     * state model for the new graph, if it was not displayed before.
     * @param state the new underlying state graph
     */
    public synchronized void setStateUpdate(GraphState state) {
        // set the graph model to the new state
        setStateModel(state);
        refreshStatus();
    }

    /**
     * Resets the emphasis in the state model and the current derivation.
     */
    public synchronized void setRuleUpdate(RuleName rule) {
        if (clearSelectedMatch()) {
            refreshStatus();
        }
    }

    /**
     * Changes the current state display by emphasising the match of a given
     * direct derivation. Emphasis is by fat lines.
     */
    public synchronized void setTransitionUpdate(GraphTransition trans) {
        if (this.selectedState != trans.source()) {
            // get a model for the new graph and set it
            setStateModel(trans.source());
        }
        // now emphasise at will
        RuleMatch match = trans.getMatch();
        setMatchUpdate(match);
    }

    /**
     * Emphasise the given match.
     * @param match the match to be emphasised (non-null)
     */
    public void setMatchUpdate(RuleMatch match) {
        assert match != null : "Match update should not be called with empty match";
        setStateModel(this.simulator.getCurrentState());
        HostToAspectMap aspectMap =
            this.stateToAspectMap.get(this.selectedState);
        Set<Element> emphElems = new HashSet<Element>();
        for (HostNode matchedNode : match.getNodeValues()) {
            emphElems.add(aspectMap.getNode(matchedNode));
        }
        for (HostEdge matchedEdge : match.getEdgeValues()) {
            emphElems.add(aspectMap.getEdge(matchedEdge));
        }
        GraphJModel<?,?> currentModel = getJModel();
        currentModel.setEmphasized(currentModel.getJCellSet(emphElems));
        this.selectedMatch = match;
        refreshStatus();
    }

    /**
     * Sets the state to the transition target. Copies and freezes the bounds
     * from the current state and starts layout to find positions for newly
     * created nodes.
     */
    public synchronized void applyTransitionUpdate(GraphTransition transition) {
        GraphState newState = transition.target();
        AspectJModel newModel = getAspectJModel(newState);
        GraphState oldState = transition.source();
        HostGraphMorphism morphism = transition.getMorphism();
        getAspectJModel(oldState);
        copyLayout(this.stateToAspectMap.get(oldState),
            this.stateToAspectMap.get(newState), morphism);
        // set the graph model to the new state
        setJModel(newModel);
        refreshStatus();
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        assert e.getSource() == getStateList();
        int[] selection = getStateList().getSelectedIndices();
        if (selection.length == 1) {
            if (selection[0] == 0) {
                setStateModel(this.simulator.getCurrentState());
            } else {
                setGraphModel((String) getStateList().getSelectedValue());
            }
        }
    }

    /**
     * Changes the display to the graph with a given name, if there is such a
     * graph in the current grammar.
     */
    private void setGraphModel(String graphName) {
        GraphView graphView =
            this.simulator.getGrammarView().getGraphView(graphName);
        if (graphView != null && graphName != this.selectedGraph) {
            this.selectedGraph = graphName;
            this.selectedState = null;
            setJModel(getAspectJModel(graphView.getView()));
            setEnabled(true);
            refreshStatus();
        }
    }

    /** Changes the display to a given state. */
    private void setStateModel(GraphState state) {
        this.selectedGraph = null;
        this.selectedState = state;
        if (state != null) {
            setJModel(getAspectJModel(state));
            setEnabled(true);
        }
        refreshStatus();
    }

    /**
     * Clears the emphasis due to the currently selected match, if any.
     * @return <code>true</code> if there was a match to be cleared.
     */
    private boolean clearSelectedMatch() {
        boolean result = this.selectedMatch != null;
        if (result) {
            this.selectedMatch = null;
            getJModel().clearEmphasized();
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
            result.append(Converter.STRONG_TAG.on(this.selectedGraph));
        } else if (this.selectedState != null) {
            result.append(FRAME_NAME);
            result.append(": ");
            result.append(Converter.STRONG_TAG.on(this.selectedState.toString()));
            GraphTransition trans = this.simulator.getCurrentTransition();
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
        return Converter.HTML_TAG.on(result).toString();
    }

    /**
     * Returns a graph model for a given state graph. The graph model is
     * retrieved from stateJModelMap; if there is no image for the requested
     * state then one is created.
     */
    private AspectJModel getAspectJModel(GraphState state) {
        HostToAspectMap aspectMap = this.stateToAspectMap.get(state);
        if (aspectMap == null) {
            this.stateToAspectMap.put(state, aspectMap =
                state.getGraph().toAspectMap());
        }
        AspectGraph aspectGraph = aspectMap.getAspectGraph();
        AspectJModel result = this.graphToJModel.get(aspectGraph);
        if (result == null) {
            result = computeStateJModel(state, aspectMap);
            assert result != null;
            this.graphToJModel.put(aspectGraph, result);
        }
        return result;
    }

    /**
     * Computes a fresh GraphJModel for a given graph state.
     */
    private AspectJModel computeStateJModel(GraphState state,
            HostToAspectMap stateMap) {
        // create a fresh model
        AspectJModel result = createAspectJModel(stateMap.getAspectGraph());
        result.setName(state.toString());
        // try to find layout information for the model
        if (state instanceof GraphNextState) {
            GraphState oldState = ((GraphNextState) state).source();
            HostGraphMorphism morphism = ((GraphNextState) state).getMorphism();
            // walk back along the derivation chain to find one for
            // which we have a state model (and hence layout information)
            while (!this.stateToAspectMap.containsKey(oldState)
                && oldState instanceof GraphNextState) {
                morphism =
                    ((GraphNextState) oldState).getMorphism().then(morphism);
                oldState = ((GraphNextState) oldState).source();
            }
            HostToAspectMap oldStateMap = this.stateToAspectMap.get(oldState);
            copyLayout(oldStateMap, stateMap, morphism);
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
        return AspectJModel.newInstance(graph, getOptions());
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
        Set<JCell> newGrayedOut = new HashSet<JCell>();
        for (Map.Entry<HostNode,HostNode> entry : morphism.nodeMap().entrySet()) {
            AspectNode oldStateNode = oldState.getNode(entry.getKey());
            AspectNode newStateNode = newState.getNode(entry.getValue());
            JCell sourceCell = oldStateJModel.getJCell(oldStateNode);
            assert sourceCell != null : "Source element " + oldStateNode
                + " unknown";
            JCell targetCell = newStateJModel.getJCell(newStateNode);
            assert targetCell != null : "Target element " + newStateNode
                + " unknown";
            Rectangle2D sourceBounds =
                GraphConstants.getBounds(sourceCell.getAttributes());
            GraphConstants.setBounds(targetCell.getAttributes(), sourceBounds);
            newStateJModel.removeLayoutable(targetCell);
            if (oldStateJModel.isGrayedOut(sourceCell)) {
                newGrayedOut.add(targetCell);
            }
        }
        Set<Edge<?>> newEdges =
            new HashSet<Edge<?>>(newStateJModel.getGraph().edgeSet());
        for (Map.Entry<HostEdge,HostEdge> entry : morphism.edgeMap().entrySet()) {
            AspectEdge oldStateEdge = oldState.getEdge(entry.getKey());
            AspectEdge newStateEdge = newState.getEdge(entry.getValue());
            JCell sourceCell = oldStateJModel.getJCell(oldStateEdge);
            AttributeMap sourceAttributes = sourceCell.getAttributes();
            JCell targetCell = newStateJModel.getJCell(newStateEdge);
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
            newStateJModel.removeLayoutable(targetCell);
            if (oldStateJModel.isGrayedOut(sourceCell)) {
                newGrayedOut.add(targetCell);
            }
            newEdges.remove(entry.getValue());
        }
        // new edges should be shown, including their source and target vertex
        for (Edge<?> newEdge : newEdges) {
            JCell targetCell = newStateJModel.getJCell(newEdge);
            if (targetCell instanceof JEdge) {
                newGrayedOut.remove(((JEdge) targetCell).getSourceVertex());
                newGrayedOut.remove(((JEdge) targetCell).getTargetVertex());
            }
        }
        // remove for now; it's doing more harm than good
        newStateJModel.setGrayedOut(newGrayedOut);
    }

    /**
     * Returns the state list associated with the simulator. Convenience method
     * for <code>getSimulator().getStateList()</code>.
     */
    private StateJList getStateList() {
        return this.simulator.getStateList();
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
    private RuleMatch selectedMatch;

    /** The simulator to which this panel belongs. */
    private final Simulator simulator;
    /** Either {@code null} or the graph currently showing in the panel. */
    private String selectedGraph;
    /** Either {@code null} or the state currently showing in the panel. */
    private GraphState selectedState;
}