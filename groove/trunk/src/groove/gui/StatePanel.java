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
import groove.abs.AbstrGraph;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.NodeEdgeMap;
import groove.gui.jgraph.AbstrGraphJModel;
import groove.gui.jgraph.AspectJModel;
import groove.gui.jgraph.GraphJModel;
import groove.gui.jgraph.JCell;
import groove.gui.jgraph.JEdge;
import groove.gui.jgraph.StateJGraph;
import groove.lts.GTS;
import groove.lts.GraphNextState;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.State;
import groove.trans.RuleMatch;
import groove.trans.RuleName;
import groove.view.AspectualGraphView;
import groove.view.StoredGrammarView;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;

/**
 * Window that displays and controls the current state graph. Auxiliary class
 * for Simulator.
 * @author Arend Rensink
 * @version $Revision$
 */
public class StatePanel extends JGraphPanel<StateJGraph> implements
        SimulationListener {
    /** Display name of this panel. */
    public static final String FRAME_NAME = "Current state";

    // --------------------- INSTANCE DEFINITIONS ----------------------

    /**
     * Constructs a new state panel.
     */
    public StatePanel(final Simulator simulator) {
        super(new StateJGraph(simulator), true, simulator.getOptions());
        this.simulator = simulator;
        simulator.addSimulationListener(this);
        addRefreshListener(SHOW_NODE_IDS_OPTION);
        addRefreshListener(SHOW_ASPECTS_OPTION);
        addRefreshListener(SHOW_ANCHORS_OPTION);
        addRefreshListener(SHOW_REMARKS_OPTION);
        addRefreshListener(SHOW_VALUE_NODES_OPTION);
        addRefreshListener(SHOW_UNFILTERED_EDGES_OPTION);
        getJGraph().setToolTipEnabled(true);
        // make sure that emphasis due to selections in the label list
        // cause any selected transition to be deselected first
        getJGraph().getLabelList().addListSelectionListener(
            new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if (getSelectedMatch() != null) {
                        simulator.setRule(getSelectedMatch().getRule().getName());
                    }
                }
            });
    }

    /**
     * Specialises the return type to {@link GraphJModel}.
     */
    @Override
    public GraphJModel getJModel() {
        return (GraphJModel) super.getJModel();
    }

    /**
     * Sets the underlying model of this state frame to the initial graph of the
     * new grammar.
     */
    public synchronized void setGrammarUpdate(StoredGrammarView grammar) {
        clearSelectedMatch();
        this.stateJModelMap.clear();
        this.jGraph.getFilteredLabels().clear();
        if (grammar == null || grammar.getStartGraphView() == null) {
            this.jGraph.setModel(this.startGraphJModel =
                AspectJModel.EMPTY_ASPECT_JMODEL);
            setEnabled(false);
        } else {
            AspectualGraphView startGraph = grammar.getStartGraphView();
            this.jGraph.setModel(this.startGraphJModel =
                createGraphJModel(startGraph));
            setEnabled(true);
        }
        refreshStatus();
    }

    public synchronized void startSimulationUpdate(GTS gts) {
        this.stateJModelMap.clear();
        // take either the GTS start state or the grammar start graph as model
        GraphJModel jModel = getStateJModel(gts.startState(), true);
        assert jModel != null;
        if (getJModel() != jModel) {
            this.jGraph.setModel(jModel);
        }
        refreshStatus();
    }

    /**
     * Sets the underlying model of this state frame to a new graph. Creates a
     * state model for the new graph, if it was not displayed before.
     * @param state the new underlying state graph
     */
    public synchronized void setStateUpdate(GraphState state) {
        clearSelectedMatch();
        // set the graph model to the new state
        this.jGraph.setModel(getStateJModel(state, true));
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
        GraphJModel newJModel = getStateJModel(trans.source(), true);
        if (getJModel() != newJModel) {
            clearSelectedMatch();
            // get a model for the new graph and set it
            this.jGraph.setModel(newJModel);
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
        Set<Element> emphElems = new HashSet<Element>();
        for (Node matchedNode : match.getNodeValues()) {
            emphElems.add(matchedNode);
        }
        for (Edge matchedEdge : match.getEdgeValues()) {
            emphElems.add(matchedEdge);
        }
        GraphJModel currentModel = getJModel();
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
        clearSelectedMatch();
        GraphState newState = transition.target();
        GraphJModel newModel = getStateJModel(newState, false);
        if (!this.simulator.isAbstractSimulation()) {
            GraphState oldState = transition.source();
            Morphism morphism = transition.getMorphism();
            copyLayout(getStateJModel(oldState, true), newModel, morphism);
        }

        // set the graph model to the new state
        this.jGraph.setModel(newModel);
        refreshStatus();
    }

    /**
     * @return Returns the selectedTransition.
     */
    final RuleMatch getSelectedMatch() {
        return this.selectedMatch;
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
        String text = null;
        if (this.simulator.getCurrentTransition() != null) {
            GraphTransition trans = this.simulator.getCurrentTransition();
            if (getOptions().isSelected(SHOW_ANCHORS_OPTION)) {
                text =
                    String.format("%s (with match %s)", trans.source(),
                        trans.getEvent());
            } else {
                text =
                    String.format("%s (with match of %s)", trans.source(),
                        trans.getEvent().getRule().getName());
            }
        } else if (getJModel() != null) {
            text = getJModel().getName();
        }
        if (text == null) {
            return FRAME_NAME;
        } else {
            return FRAME_NAME + ": " + text;
        }
    }

    /**
     * Returns a graph model for a given state graph. The graph model is
     * retrieved from stateJModelMap; if there is no image for the requested
     * state then one is created.
     */
    private GraphJModel getStateJModel(GraphState state, boolean copyLayout) {
        GraphJModel result = this.stateJModelMap.get(state);
        // IOVKA : added additional condition here, and additional condition for
        // abstract simulation
        if (state == this.simulator.getGTS().startState()
            && !this.simulator.isAbstractSimulation()) {
            result = this.startGraphJModel;
        } else {
            result = this.stateJModelMap.get(state);
            if (result == null) {
                result = computeStateJModel(state, copyLayout);
                assert result != null;
                this.stateJModelMap.put(state, result);
            }
        }
        return result;
    }

    /**
     * Computes a fresh GraphJModel for a given graph state.
     */
    private GraphJModel computeStateJModel(GraphState state, boolean copyLayout) {
        // create a fresh model
        GraphJModel result = createGraphJModel(state.getGraph());
        result.setName(state.toString());
        // try to find layout information for the model
        if (!this.simulator.isAbstractSimulation() && copyLayout
            && state instanceof GraphNextState) {
            GraphState oldState = ((GraphNextState) state).source();
            Morphism morphism = ((GraphNextState) state).getMorphism();
            // walk back along the derivation chain to find one for
            // which we have a state model (and hence layout information)
            while (!this.stateJModelMap.containsKey(oldState)
                && oldState instanceof GraphNextState) {
                morphism =
                    ((GraphNextState) oldState).getMorphism().then(morphism);
                oldState = ((GraphNextState) oldState).source();
            }
            GraphJModel oldJModel = getStateJModel(oldState, true);
            copyLayout(oldJModel, result, morphism);
        }
        return result;
    }

    /** Creates a j-model for a given graph view. */
    private AspectJModel createGraphJModel(AspectualGraphView graph) {
        return AspectJModel.newInstance(graph, getOptions());
    }

    /** Creates a j-model for a given graph. */
    private GraphJModel createGraphJModel(Graph graph) {
        return this.simulator.isAbstractSimulation()
                ? AbstrGraphJModel.newInstance((AbstrGraph) graph, getOptions())
                : GraphJModel.newInstance(graph, getOptions());
    }

    /**
     * Copies the layout information from the current j-model to a new one,
     * modulo a mapping from the nodes and edges of the underlying graphs.
     * @param oldStateJModel the model to copy the layout information from
     * @param newStateJModel the model to copy the layout information to
     * @param derivationMap mapping from the nodes and edges of the old to the
     *        new j-model
     */
    private void copyLayout(GraphJModel oldStateJModel,
            GraphJModel newStateJModel, NodeEdgeMap derivationMap) {
        Set<JCell> newGrayedOut = new HashSet<JCell>();
        for (Map.Entry<Node,Node> entry : derivationMap.nodeMap().entrySet()) {
            JCell sourceCell = oldStateJModel.getJCell(entry.getKey());
            assert sourceCell != null : "Source element " + entry.getKey()
                + " unknown";
            JCell targetCell = newStateJModel.getJCell(entry.getValue());
            assert targetCell != null : "Target element " + entry.getValue()
                + " unknown";
            Rectangle2D sourceBounds =
                GraphConstants.getBounds(sourceCell.getAttributes());
            GraphConstants.setBounds(targetCell.getAttributes(), sourceBounds);
            newStateJModel.removeLayoutable(targetCell);
            if (oldStateJModel.isGrayedOut(sourceCell)) {
                newGrayedOut.add(targetCell);
            }
        }
        Set<Edge> newEdges =
            new HashSet<Edge>(newStateJModel.getGraph().edgeSet());
        for (Map.Entry<Edge,Edge> entry : derivationMap.edgeMap().entrySet()) {
            JCell sourceCell = oldStateJModel.getJCell(entry.getKey());
            AttributeMap sourceAttributes = sourceCell.getAttributes();
            JCell targetCell = newStateJModel.getJCell(entry.getValue());
            assert targetCell != null : "Target element " + entry.getValue()
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
        for (Edge newEdge : newEdges) {
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
     * Mapping from states to the corresponding graph models.
     */
    private final Map<State,GraphJModel> stateJModelMap =
        new HashMap<State,GraphJModel>();
    // /**
    // * Mapping from graphs to the corresponding graph models.
    // */
    // private final Map<AspectualGraphView,GraphJModel> graphJModelMap = new
    // HashMap<AspectualGraphView,GraphJModel>();
    /**
     * Model for the start graph, if any. We make this an {@link AspectJModel}
     * so that remarks can be displayed.
     */
    private AspectJModel startGraphJModel;
    /** The currently emphasised match (nullable). */
    private RuleMatch selectedMatch;
    /** The simulator to which this panel belongs. */
    private final Simulator simulator;
}