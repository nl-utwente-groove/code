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
 * $Id: LTSPanel.java,v 1.21 2008-02-05 13:28:06 rensink Exp $
 */
package groove.gui;

import static groove.gui.Options.SHOW_ANCHORS_OPTION;
import static groove.gui.Options.SHOW_ASPECTS_OPTION;
import static groove.gui.Options.SHOW_BIDIRECTIONAL_EDGES_OPTION;
import static groove.gui.Options.SHOW_NODE_IDS_OPTION;
import static groove.gui.Options.SHOW_UNFILTERED_EDGES_OPTION;
import static groove.gui.Options.SHOW_VALUE_NODES_OPTION;
import static groove.gui.SimulatorModel.Change.GRAMMAR;
import static groove.gui.SimulatorModel.Change.GTS;
import static groove.gui.SimulatorModel.Change.MATCH;
import static groove.gui.SimulatorModel.Change.STATE;
import groove.gui.SimulatorModel.Change;
import groove.gui.jgraph.AspectJCell;
import groove.gui.jgraph.AspectJGraph;
import groove.gui.jgraph.AspectJModel;
import groove.gui.jgraph.AspectJVertex;
import groove.gui.jgraph.GraphJCell;
import groove.gui.jgraph.GraphJModel;
import groove.gui.jgraph.JAttr;
import groove.gui.list.ErrorListPanel;
import groove.io.HTMLConverter;
import groove.lts.GTS;
import groove.lts.GraphNextState;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.MatchResult;
import groove.lts.RecipeTransition;
import groove.lts.RuleTransition;
import groove.lts.StartGraphState;
import groove.trans.HostEdge;
import groove.trans.HostGraph.HostToAspectMap;
import groove.trans.HostGraphMorphism;
import groove.trans.HostNode;
import groove.trans.Proof;
import groove.trans.RuleApplication;
import groove.trans.RuleNode;
import groove.view.FormatError;
import groove.view.GrammarModel;
import groove.view.HostModel;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectNode;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JTree;

import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;

/**
 * Window that displays and controls the current lts graph. Auxiliary class for
 * Simulator.
 * 
 * @author Arend Rensink
 * @version $Revision: 4278 $ $Date: 2008-02-05 13:28:06 $
 */
public class StateDisplay extends Display {
    /** Creates a LTS panel for a given simulator. */
    public StateDisplay(Simulator simulator) {
        super(simulator, DisplayKind.STATE);
    }

    @Override
    protected void buildDisplay() {
        JToolBar toolBar = Options.createToolBar();
        fillToolBar(toolBar);
        add(toolBar, BorderLayout.NORTH);
        add(getDisplayPanel());
    }

    @Override
    protected void installListeners() {
        // nothing to be installed
    }

    @Override
    protected JTree createList() {
        return new StateList(getSimulator());
    }

    @Override
    protected JToolBar createListToolBar() {
        JToolBar result = Options.createToolBar();
        result.add(getActions().getEditStateAction());
        result.add(getActions().getSaveStateAction());
        result.addSeparator();
        result.add(getActions().getBackAction());
        result.add(getActions().getForwardAction());
        return result;
    }

    @Override
    protected JComponent createInfoPanel() {
        LabelTree labelTree = getJGraph().getLabelTree();
        TitledPanel result =
            new TitledPanel(Options.LABEL_PANE_TITLE, labelTree,
                labelTree.createToolBar(), true);
        result.setEnabledBackground(JAttr.STATE_BACKGROUND);
        return result;
    }

    private void fillToolBar(JToolBar result) {
        result.removeAll();
        result.add(getActions().getExplorationDialogAction());
        result.addSeparator();
        result.add(getActions().getStartSimulationAction());
        result.add(getActions().getApplyMatchAction());
        result.add(getActions().getAnimateAction());
        result.add(getActions().getExploreAction());
        result.addSeparator();
        result.add(getActions().getBackAction());
        result.add(getActions().getForwardAction());
    }

    /** Lazily creates and returns the top-level display panel. */
    private JSplitPane getDisplayPanel() {
        JSplitPane result = this.displayPanel;
        if (result == null) {
            this.displayPanel =
                result = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            result.setTopComponent(getStatePanel());
            result.setBottomComponent(getErrorPanel());
            result.setDividerSize(0);
            result.setContinuousLayout(true);
            result.setResizeWeight(0.9);
            result.resetToPreferredSizes();
            result.setBorder(null);
        }
        return result;
    }

    /** Returns the currently displayed state graph. */
    public AspectGraph getStateGraph() {
        return getJGraph().getModel().getGraph();
    }

    /** Returns the JGraph component of the state display. */
    final public AspectJGraph getJGraph() {
        return getGraphPanel().getJGraph();
    }

    /** Returns the state tab on this display. */
    private StateGraphPanel getGraphPanel() {
        if (this.stateGraphPanel == null) {
            this.stateGraphPanel = new StateGraphPanel();
        }
        return this.stateGraphPanel;
    }

    /** Gets the state panel, creating it (lazily) if necessary. */
    private JComponent getStatePanel() {
        if (this.statePanel == null) {
            this.statePanel = getGraphPanel().createGraphPane();
        }
        return this.statePanel;
    }

    /** Gets the error panel, creating it (lazily) if necessary. */
    ErrorListPanel getErrorPanel() {
        if (this.errorPanel == null) {
            this.errorPanel = new ErrorListPanel("Errors in state graph");
            this.errorPanel.addSelectionListener(createErrorListener());
        }
        return this.errorPanel;
    }

    /** Creates the listener of the error panel. */
    private Observer createErrorListener() {
        return new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                if (arg != null) {
                    GraphJCell errorCell =
                        getGraphPanel().getJModel().getErrorMap().get(arg);
                    if (errorCell != null) {
                        getGraphPanel().getJGraph().setSelectionCell(errorCell);
                    }
                }
            }
        };
    }

    private JSplitPane displayPanel;
    private JComponent statePanel;
    private ErrorListPanel errorPanel;
    private StateGraphPanel stateGraphPanel;

    /**
     * Window that displays and controls the current state graph.
     * @author Arend Rensink
     * @version $Revision$
     */
    private class StateGraphPanel extends JGraphPanel<AspectJGraph> implements
            SimulatorListener {

        // --------------------- INSTANCE DEFINITIONS ----------------------

        /** Constructs a new state panel. */
        public StateGraphPanel() {
            super(new AspectJGraph(getSimulator(), getKind(), false), true);
            initialise();
            setBorder(null);
            setEnabledBackground(JAttr.STATE_BACKGROUND);
            getJGraph().setToolTipEnabled(true);
        }

        @Override
        public void dispose() {
            super.dispose();
            suspendListening();
        }

        @Override
        protected void installListeners() {
            super.installListeners();
            this.simulatorListener = this;
            this.graphSelectionListener = new GraphSelectionListener() {
                @Override
                public void valueChanged(GraphSelectionEvent e) {
                    if (StateGraphPanel.this.matchSelected) {
                        // change only if cells were removed from the selection
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
            };
            addRefreshListener(SHOW_NODE_IDS_OPTION);
            addRefreshListener(SHOW_ASPECTS_OPTION);
            addRefreshListener(SHOW_ANCHORS_OPTION);
            addRefreshListener(SHOW_VALUE_NODES_OPTION);
            addRefreshListener(SHOW_UNFILTERED_EDGES_OPTION);
            addRefreshListener(SHOW_BIDIRECTIONAL_EDGES_OPTION);
            getSimulatorModel().addListener(this.simulatorListener, GRAMMAR,
                GTS, STATE, MATCH);
            activateListening();
        }

        /**
         * Activates all listeners.
         */
        private void activateListening() {
            if (this.listening) {
                throw new IllegalStateException();
            }
            // make sure that removals from the selection model
            // also deselect the match
            getJGraph().addGraphSelectionListener(this.graphSelectionListener);
            this.listening = true;
        }

        /**
         * Suspend all listening activity to avoid dependent updates.
         */
        private boolean suspendListening() {
            boolean result = this.listening;
            if (result) {
                getJGraph().removeGraphSelectionListener(
                    this.graphSelectionListener);
                this.listening = false;
            }
            return result;
        }

        @Override
        public void setJModel(GraphJModel<?,?> jModel) {
            super.setJModel(jModel);
            StateDisplay.this.setEnabled(jModel != null);
        }

        /**
         * Specialises the return type to {@link GraphJModel}.
         */
        @Override
        public AspectJModel getJModel() {
            return (AspectJModel) super.getJModel();
        }

        @Override
        public void update(SimulatorModel source, SimulatorModel oldModel,
                Set<Change> changes) {
            if (!suspendListening()) {
                return;
            }
            // check if layout should be transferred
            GraphTransition oldTtrans = oldModel.getTransition();
            boolean transferLayout =
                oldTtrans != null && oldTtrans != source.getTransition()
                    && oldTtrans.target() == source.getState();
            if (changes.contains(Change.GRAMMAR)) {
                updateGrammar(source.getGrammar());
            } else if (changes.contains(GTS)
                && source.getGts() != oldModel.getGts()) {
                startSimulation(source.getGts());
            } else if (changes.contains(STATE)) {
                GraphState newState = source.getState();
                if (newState != null) {
                    // clear the match on the current state
                    clearSelectedMatch(true);
                } else {
                    if (transferLayout) {
                        transferLayout(oldTtrans);
                        transferLayout = false;
                    }
                }
                // set the graph model to the new state
                displayState(newState);
            }
            if (changes.contains(MATCH)) {
                if (transferLayout) {
                    transferLayout(oldTtrans);
                }
                if (source.getMatch() == null) {
                    clearSelectedMatch(true);
                } else {
                    selectMatch(source.getMatch().getEvent().getMatch(
                        source.getState().getGraph()));
                }
                refreshStatus();
            }
            activateListening();
        }

        /**
         * Sets the underlying model of this state frame to the initial graph of the
         * new grammar.
         */
        public void updateGrammar(GrammarModel grammar) {
            if (grammar.hasErrors()) {
                displayState(null);
            } else {
                startSimulation(getSimulatorModel().getGts());
            }
        }

        private void startSimulation(GTS gts) {
            // clear the states from the aspect and model maps
            this.graphToJModel.clear();
            this.stateToAspectMap.clear();
            // only change the displayed model if we are currently displaying a
            // state
            displayState(getSimulatorModel().getState());
            refreshStatus();
        }

        /**
         * Emphasise the given match.
         * @param match the match to be emphasised (non-null)
         */
        private void selectMatch(Proof match) {
            assert match != null : "Match update should not be called with empty match";
            displayState(getSimulatorModel().getState());
            AspectJModel jModel = getJModel();
            HostToAspectMap aspectMap =
                getAspectMap(getSimulatorModel().getState());
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
            getJGraph().setSelectionCells(emphElems.toArray());
            this.matchSelected = true;
        }

        /** Changes the display to a given state. */
        public void displayState(GraphState state) {
            clearSelectedMatch(true);
            Collection<? extends FormatError> errors = null;
            if (state == null) {
                setJModel(null);
            } else {
                AspectJModel model = getAspectJModel(state);
                setJModel(model);
                errors = model.getResourceModel().getErrors();
            }
            if (state != null && state.isError()) {
                getErrorPanel().setEntries(errors);
                getDisplayPanel().setBottomComponent(getErrorPanel());
                getDisplayPanel().resetToPreferredSizes();
            } else {
                getErrorPanel().clearEntries();
                getDisplayPanel().remove(getErrorPanel());
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
            boolean result = this.listening && this.matchSelected;
            if (result) {
                this.matchSelected = false;
                if (clear) {
                    getJGraph().clearSelection();
                }
                getSimulatorModel().setMatch(null);
                refreshStatus();
            }
            return result;
        }

        /**
         * Text to indicate which state is chosen and which match is emphasised.
         */
        @Override
        protected String getStatusText() {
            StringBuilder result = new StringBuilder();
            result.append(FRAME_NAME);
            if (getSimulatorModel().getState() != null) {
                result.append(": ");
                String stateID = getSimulatorModel().getState().toString();
                result.append(HTMLConverter.STRONG_TAG.on(stateID));
                if (stateID.equals("s0")) {
                    HostModel startGraph =
                        getSimulatorModel().getGrammar().getStartGraphModel();
                    if (startGraph != null) {
                        result.append("=");
                        result.append(startGraph.getLastName());
                    }
                }
                MatchResult match = getSimulatorModel().getMatch();
                if (match != null) {
                    if (getOptions().isSelected(SHOW_ANCHORS_OPTION)) {
                        result.append(String.format(" (with match %s)",
                            match.getEvent()));
                    } else {
                        result.append(String.format(" (with match of %s)",
                            match.getEvent().getRule().getFullName()));
                    }
                }
            }
            return HTMLConverter.HTML_TAG.on(result).toString();
        }

        /**
         * Returns a graph model for a given state graph. The graph model is
         * retrieved from {@link #graphToJModel}; if there is no image for the requested
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
                    transferLayout((GraphNextState) state);
                } else {
                    assert state instanceof StartGraphState;
                    // this is the start state
                    setStartGraphLayout(result);
                }
            }
            return result;
        }

        /** Transfers colours and layout from the source to the target of a given transition. */
        private void transferLayout(GraphTransition trans) {
            GraphState source = trans.source();
            AspectJModel sourceJModel = getAspectJModel(source);
            HostToAspectMap sourceAspectMap = getAspectMap(source);
            GraphState target = trans.target();
            AspectJModel targetJModel = getAspectJModel(target);
            HostToAspectMap targetAspectMap = getAspectMap(target);
            HostGraphMorphism morphism = trans.getMorphism();
            // find out newly created colours
            Map<HostNode,Color> newColorMap = extractColors(trans);
            // compute target node attributes
            Set<HostNode> newNodes =
                new HashSet<HostNode>(target.getGraph().nodeSet());
            Map<AspectNode,Attributes> nodeAttrMap =
                new HashMap<AspectNode,Attributes>();
            for (Map.Entry<HostNode,HostNode> entry : morphism.nodeMap().entrySet()) {
                AspectNode sourceAspectNode =
                    sourceAspectMap.getNode(entry.getKey());
                if (sourceAspectNode == null) {
                    continue;
                }
                AspectJVertex sourceCell =
                    sourceJModel.getJCellForNode(sourceAspectNode);
                assert sourceCell != null : "Source element "
                    + sourceAspectNode + " unknown";
                HostNode targetNode = entry.getValue();
                newNodes.remove(targetNode);
                Attributes attr =
                    new Attributes(
                        GraphConstants.getBounds(sourceCell.getAttributes()),
                        sourceCell.isGrayedOut(), newColorMap.get(targetNode));
                AspectNode targetAspectNode =
                    targetAspectMap.getNode(targetNode);
                if (targetAspectNode == null) {
                    continue;
                }
                nodeAttrMap.put(targetAspectNode, attr);
            }
            // add colours for new nodes
            for (HostNode targetNode : newNodes) {
                Attributes attr =
                    new Attributes(null, false, newColorMap.get(targetNode));
                AspectNode targetAspectNode =
                    targetAspectMap.getNode(targetNode);
                if (targetAspectNode == null) {
                    continue;
                }
                nodeAttrMap.put(targetAspectNode, attr);
            }
            // compute target edge attributes
            Map<AspectEdge,Attributes> edgeAttrMap =
                new HashMap<AspectEdge,Attributes>();
            for (Map.Entry<HostEdge,HostEdge> entry : morphism.edgeMap().entrySet()) {
                AspectEdge sourceAspectEdge =
                    sourceAspectMap.getEdge(entry.getKey());
                if (sourceAspectEdge == null) {
                    continue;
                }
                AspectJCell sourceCell =
                    sourceJModel.getJCellForEdge(sourceAspectEdge);
                if (sourceCell instanceof AspectJVertex) {
                    continue;
                }
                AttributeMap sourceAttributes = sourceCell.getAttributes();
                Attributes attr =
                    new Attributes(GraphConstants.getPoints(sourceAttributes),
                        GraphConstants.getLabelPosition(sourceAttributes),
                        GraphConstants.getLineStyle(sourceAttributes),
                        sourceCell.isGrayedOut());
                AspectEdge targetAspectEdge =
                    targetAspectMap.getEdge(entry.getValue());
                edgeAttrMap.put(targetAspectEdge, attr);
            }
            // store target node attributes
            for (Map.Entry<AspectNode,Attributes> e : nodeAttrMap.entrySet()) {
                AspectNode targetAspectNode = e.getKey();
                Attributes attr = e.getValue();
                AspectJVertex targetCell =
                    targetJModel.getJCellForNode(targetAspectNode);
                assert targetCell != null : "Target element "
                    + targetAspectNode + " unknown";
                if (attr.bounds != null) {
                    GraphConstants.setBounds(targetCell.getAttributes(),
                        attr.bounds);
                }
                targetCell.setGrayedOut(attr.grayedOut);
                if (attr.color != null) {
                    targetCell.setColor(attr.color);
                }
                targetCell.setLayoutable(attr.bounds == null);
                targetJModel.synchroniseLayout(targetCell);
            }
            // store target edge attributes
            for (Map.Entry<AspectEdge,Attributes> e : edgeAttrMap.entrySet()) {
                AspectEdge targetAspectEdge = e.getKey();
                AspectJCell targetCell =
                    targetJModel.getJCellForEdge(targetAspectEdge);
                if (targetCell instanceof AspectJVertex) {
                    continue;
                }
                assert targetCell != null : "Target element "
                    + targetAspectEdge + " unknown";
                AttributeMap targetAttributes = targetCell.getAttributes();
                Attributes attr = e.getValue();
                if (attr.points != null) {
                    GraphConstants.setPoints(targetAttributes,
                        new LinkedList<Object>(attr.points));
                }
                if (attr.labelPosition != null) {
                    GraphConstants.setLabelPosition(targetAttributes,
                        attr.labelPosition);
                }
                GraphConstants.setLineStyle(targetAttributes, attr.lineStyle);
                targetCell.setGrayedOut(attr.grayedOut);
                targetCell.setLayoutable(attr.points == null);
                targetJModel.synchroniseLayout(targetCell);
            }
        }

        /**
         * Extracts the colours that were created for the target graph
         * in the course of a given graph transition.
         */
        private Map<HostNode,Color> extractColors(GraphTransition trans) {
            Map<HostNode,Color> result = new HashMap<HostNode,Color>();
            // extract colours from source graph
            GraphState source = trans.source();
            AspectJModel sourceJModel = getAspectJModel(source);
            HostToAspectMap sourceAspectMap = getAspectMap(source);
            for (Map.Entry<HostNode,? extends AspectNode> entry : sourceAspectMap.nodeMap().entrySet()) {
                AspectJVertex sourceCell =
                    sourceJModel.getJCellForNode(entry.getValue());
                result.put(entry.getKey(), sourceCell.getColor());
            }
            // transfer colours along transition
            if (trans instanceof RuleTransition) {
                result = transferColors(result, (RuleTransition) trans);
            } else {
                for (RuleTransition ruleTrans : ((RecipeTransition) trans).getPath()) {
                    result = transferColors(result, ruleTrans);
                }
            }
            return result;
        }

        /** 
         * Transforms a colour map of a transition source graph
         * into a colour map for the transition target graph.
         * @param colorMap original colour map for the source graph
         * @param trans transition from source to target
         * @return colour map for the target graph
         */
        private Map<HostNode,Color> transferColors(
                Map<HostNode,Color> colorMap, RuleTransition trans) {
            Map<HostNode,Color> result = new HashMap<HostNode,Color>();
            // extract new colours from target
            RuleApplication application = trans.createRuleApplication();
            Map<RuleNode,Set<HostNode>> comatch = application.getComatch();
            for (Map.Entry<RuleNode,Color> colorEntry : application.getRule().getColorMap().entrySet()) {
                Set<HostNode> matches = comatch.get(colorEntry.getKey());
                // possibly this node has no matches, for instance if it is universally
                // quantified
                if (matches != null) {
                    for (HostNode hostNode : matches) {
                        result.put(hostNode, colorEntry.getValue());
                    }
                }
            }
            // now copy colours from source to target
            HostGraphMorphism morphism = trans.getMorphism();
            for (Map.Entry<HostNode,Color> colorEntry : colorMap.entrySet()) {
                HostNode newNode = morphism.getNode(colorEntry.getKey());
                if (!result.containsKey(newNode)) {
                    result.put(newNode, colorEntry.getValue());
                }
            }
            return result;
        }

        /** Copies layout from the host model of the start graph. */
        private void setStartGraphLayout(AspectJModel result) {
            AspectGraph startGraph =
                getGrammar().getStartGraphModel().getSource();
            AspectJModel startModel = getAspectJModel(startGraph);
            for (AspectNode node : startGraph.nodeSet()) {
                AspectJVertex stateVertex = result.getJCellForNode(node);
                // meta nodes are not in the state;
                // data nodes may have been merged
                if (stateVertex == null) {
                    continue;
                }
                AspectJVertex graphVertex = startModel.getJCellForNode(node);
                stateVertex.refreshAttributes();
                stateVertex.getAttributes().applyMap(
                    graphVertex.getAttributes());
                stateVertex.setGrayedOut(graphVertex.isGrayedOut());
                result.synchroniseLayout(stateVertex);
                stateVertex.setLayoutable(false);
                stateVertex.setColor(graphVertex.getColor());
            }
            for (AspectEdge edge : startGraph.edgeSet()) {
                AspectJCell stateEdge = result.getJCellForEdge(edge);
                // meta edges and merged data edges are not in the state
                if (stateEdge == null) {
                    continue;
                }
                AspectJCell graphEdge = startModel.getJCellForEdge(edge);
                stateEdge.refreshAttributes();
                stateEdge.getAttributes().applyMap(graphEdge.getAttributes());
                stateEdge.setGrayedOut(graphEdge.isGrayedOut());
                result.synchroniseLayout(stateEdge);
                stateEdge.setLayoutable(false);
            }
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

        /** Convenience method to retrieve the current grammar view. */
        private GrammarModel getGrammar() {
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

        /** Flag indicating that the listeners are activated. */
        private boolean listening;
        private SimulatorListener simulatorListener;
        private GraphSelectionListener graphSelectionListener;

        /** The currently emphasised match (nullable). */
        private boolean matchSelected;
        /** Display name of this panel. */
        public static final String FRAME_NAME = "Current state";
    }

    /** Temporary record of graph element attributes. */
    private static class Attributes {
        Attributes(Rectangle2D bounds, boolean grayedOut, Color color) {
            this.bounds = bounds;
            this.grayedOut = grayedOut;
            this.color = color;
            this.points = null;
            this.labelPosition = null;
            this.lineStyle = 0;
        }

        Attributes(List<?> points, Point2D labelPosition, int lineStyle,
                boolean grayedOut) {
            this.bounds = null;
            this.grayedOut = grayedOut;
            this.color = null;
            this.points = points;
            this.labelPosition = labelPosition;
            this.lineStyle = lineStyle;
        }

        final Rectangle2D bounds;
        final Color color;
        final boolean grayedOut;
        final List<?> points;
        final Point2D labelPosition;
        final int lineStyle;
    }
}
