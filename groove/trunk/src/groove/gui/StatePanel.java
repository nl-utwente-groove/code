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
import groove.graph.LabelStore;
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
import groove.trans.SystemProperties;
import groove.util.Converter;
import groove.view.GraphView;
import groove.view.StoredGrammarView;

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
                    if (getSelectedMatch() != null) {
                        simulator.setRule(getSelectedMatch().getRule().getName());
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
    }

    /**
     * Specialises the return type to {@link GraphJModel}.
     */
    @Override
    public GraphJModel getJModel() {
        return (GraphJModel) super.getJModel();
    }

    /**
     * Sets the graph model in the jgraph. Convenience method for
     * <code>this.jGraph.setModel(newModel)</code>.
     */
    private void setJModel(GraphJModel newModel) {
        this.jGraph.setModel(newModel);
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
            setJModel(AspectJModel.EMPTY_ASPECT_JMODEL);
            setEnabled(false);
        } else {
            GraphView startGraph = grammar.getStartGraphView();
            this.jGraph.setModel(getGraphJModel(startGraph),
                grammar.getLabelStore());
            setEnabled(true);
        }
        refreshStatus();
    }

    public synchronized void startSimulationUpdate(GTS gts) {
        this.stateJModelMap.clear();
        // take either the GTS start state or the grammar start graph as model
        GraphJModel jModel = getCurrentStateJModel();
        assert jModel != null;
        if (getJModel() != jModel) {
            setJModel(jModel);
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
        setJModel(getCurrentStateJModel());
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
        GraphJModel newJModel = getCurrentStateJModel();
        if (getJModel() != newJModel) {
            clearSelectedMatch();
            // get a model for the new graph and set it
            setJModel(newJModel);
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
        setJModel(getCurrentStateJModel());
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
        if (!getSimulator().isAbstractSimulation()) {
            GraphState oldState = transition.source();
            Morphism morphism = transition.getMorphism();
            copyLayout(getStateJModel(oldState, true), newModel, morphism);
        }
        // set the graph model to the new state
        setJModel(newModel);
        refreshStatus();
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        assert e.getSource() == getStateList();
        if (getStateList().getSelectedIndices().length == 1) {
            String graphName = (String) getStateList().getSelectedValue();
            GraphView graphView =
                getSimulator().getGrammarView().getGraphView(graphName);
            GraphJModel newModel =
                graphView == null ? getCurrentStateJModel()
                        : getGraphJModel(graphView);
            if (newModel != null) {
                setJModel(newModel);
                refreshStatus();
            }
        }
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
        StringBuilder result = new StringBuilder();
        String graphName = (String) getStateList().getSelectedValue();
        if (graphName == null) {
            result.append(FRAME_NAME);
        } else if (this.simulator.getGrammarView().getGraphView(graphName) == null) {
            // we're dealing with a state
            result.append(FRAME_NAME);
            result.append(": ");
            result.append(Converter.STRONG_TAG.on(graphName));
            if (getSimulator().getCurrentTransition() != null) {
                GraphTransition trans = getSimulator().getCurrentTransition();
                if (getOptions().isSelected(SHOW_ANCHORS_OPTION)) {
                    result.append(String.format(" (with match %s)",
                        trans.source(), trans.getEvent()));
                } else {
                    result.append(String.format(" (with match of %s)",
                        trans.source(), trans.getEvent().getRule().getName()));
                }
            }
        } else {
            result.append("Graph: ");
            result.append(Converter.STRONG_TAG.on(graphName));
        }
        return Converter.HTML_TAG.on(result).toString();
    }

    /** Returns the graph model for the current graph state. */
    private GraphJModel getCurrentStateJModel() {
        GraphState currentState = getSimulator().getCurrentState();
        return currentState == null ? null : getStateJModel(currentState, true);
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
        // if (getSimulator().getGTS() != null
        // && state == getSimulator().getGTS().startState()
        // && !getSimulator().isAbstractSimulation()) {
        // result = this.startGraphJModel;
        // } else {
        // result = this.stateJModelMap.get(state);
        if (result == null) {
            result = computeStateJModel(state, copyLayout);
            assert result != null;
            this.stateJModelMap.put(state, result);
        }
        // }
        return result;
    }

    /**
     * Computes a fresh GraphJModel for a given graph state.
     */
    private GraphJModel computeStateJModel(GraphState state, boolean copyLayout) {
        // create a fresh model
        GraphJModel result = createStateJModel(state);
        result.setName(state.toString());
        // try to find layout information for the model
        if (!getSimulator().isAbstractSimulation() && copyLayout) {
            if (state instanceof GraphNextState) {
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
        }
        return result;
    }

    /** Creates a j-model for a given graph. */
    private GraphJModel createStateJModel(GraphState state) {
        Graph graph = state.getGraph();
        return getSimulator().isAbstractSimulation()
                ? AbstrGraphJModel.newInstance((AbstrGraph) graph, getOptions())
                : GraphJModel.newInstance(graph, getOptions());
    }

    /**
     * Returns a graph model for a given graph view. The graph model is
     * retrieved from {@link #graphJModelMap}; if there is no image for the
     * requested state then one is created using
     * {@link #createGraphJModel(GraphView)}.
     */
    private AspectJModel getGraphJModel(GraphView graph) {
        AspectJModel result = this.graphJModelMap.get(graph);
        if (result == null) {
            result = createGraphJModel(graph);
            this.graphJModelMap.put(graph, result);
        }
        return result;
    }

    /** Creates a j-model for a given graph view. */
    private AspectJModel createGraphJModel(GraphView graph) {
        return AspectJModel.newInstance(graph, getOptions());
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
     * Returns the state list associated with the simulator. Convenience method
     * for <code>getSimulator().getStateList()</code>.
     */
    private StateJList getStateList() {
        return getSimulator().getStateList();
    }

    /**
     * Mapping from states to the corresponding graph models.
     */
    private final Map<State,GraphJModel> stateJModelMap =
        new HashMap<State,GraphJModel>();
    /**
     * Mapping from graphs to the corresponding graph models.
     */
    private final Map<GraphView,AspectJModel> graphJModelMap =
        new HashMap<GraphView,AspectJModel>();

    /** The currently emphasised match (nullable). */
    private RuleMatch selectedMatch;

    /** Returns the simulator associated with this panel. */
    private final Simulator getSimulator() {
        return this.simulator;
    }

    /** The simulator to which this panel belongs. */
    private final Simulator simulator;
}