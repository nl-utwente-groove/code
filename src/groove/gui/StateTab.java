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
import static groove.gui.Options.SHOW_LTS_OPTION;
import static groove.gui.Options.SHOW_NODE_IDS_OPTION;
import static groove.gui.Options.SHOW_UNFILTERED_EDGES_OPTION;
import static groove.gui.Options.SHOW_VALUE_NODES_OPTION;
import static groove.gui.SimulatorModel.Change.GRAMMAR;
import static groove.gui.SimulatorModel.Change.GTS;
import static groove.gui.SimulatorModel.Change.MATCH;
import static groove.gui.SimulatorModel.Change.STATE;
import groove.gui.Display.Tab;
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
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectNode;

import java.awt.Color;
import java.awt.Component;
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

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JSplitPane;

import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;

/**
 * Window that displays and controls the current state graph.
 * @author Arend Rensink
 * @version $Revision$
 */
public class StateTab extends JGraphPanel<AspectJGraph> implements Tab,
        SimulatorListener {

    // --------------------- INSTANCE DEFINITIONS ----------------------

    /** Constructs a new state panel. */
    public StateTab(LTSDisplay display) {
        super(
            new AspectJGraph(display.getSimulator(), display.getKind(), false),
            true);
        this.display = display;
        initialise();
        setBorder(null);
        setEnabledBackground(JAttr.STATE_BACKGROUND);
        getJGraph().setToolTipEnabled(true);
    }

    @Override
    public JComponent createMainPane() {
        this.mainPane =
            new JSplitPane(JSplitPane.VERTICAL_SPLIT, getStatePanel(),
                getErrorPanel());
        this.mainPane.setDividerSize(0);
        this.mainPane.setContinuousLayout(true);
        this.mainPane.setResizeWeight(0.9);
        this.mainPane.resetToPreferredSizes();
        this.mainPane.setBorder(null);
        return this.mainPane;
    }

    /** Gets the main pane, creating it (lazily) if necessary. */
    public JSplitPane getMainPane() {
        if (this.mainPane == null) {
            createMainPane();
        }
        return this.mainPane;
    }

    /** Gets the state panel, creating it (lazily) if necessary. */
    private JComponent getStatePanel() {
        if (this.statePanel == null) {
            this.statePanel = super.createMainPane();
        }
        return this.statePanel;
    }

    /** Gets the error panel, crearing it (lazily) if necessary. */
    private ErrorListPanel getErrorPanel() {
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
                    GraphJCell errorCell = getJModel().getErrorMap().get(arg);
                    if (errorCell != null) {
                        getJGraph().setSelectionCell(errorCell);
                    }
                }
            }
        };
    }

    @Override
    public LTSDisplay getDisplay() {
        return this.display;
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public Icon getIcon() {
        return Icons.STATE_MODE_ICON;
    }

    @Override
    public String getTitle() {
        if (getJModel() == null) {
            return NO_STATE_TEXT;
        } else {
            StringBuilder result = new StringBuilder(getJModel().getName());
            HTMLConverter.ITALIC_TAG.on(result);
            result.insert(0, "State ");
            HTMLConverter.HTML_TAG.on(result);
            return result.toString();
        }
    }

    @Override
    public boolean isEditor() {
        return false;
    }

    @Override
    public void dispose() {
        super.dispose();
        suspendListening();
    }

    @Override
    public TabLabel getTabLabel() {
        if (this.tabLabel == null) {
            TabLabel result =
                new TabLabel(this.display, this, getIcon(), "Current state");
            this.tabLabel = result;
        }
        return this.tabLabel;
    }

    @Override
    protected void installListeners() {
        super.installListeners();
        this.simulatorListener = this;
        this.graphSelectionListener = new GraphSelectionListener() {
            @Override
            public void valueChanged(GraphSelectionEvent e) {
                if (StateTab.this.matchSelected) {
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
        addRefreshListener(SHOW_LTS_OPTION);
        addRefreshListener(SHOW_VALUE_NODES_OPTION);
        addRefreshListener(SHOW_UNFILTERED_EDGES_OPTION);
        getSimulatorModel().addListener(this.simulatorListener, GRAMMAR, GTS,
            STATE, MATCH);
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
        if (changes.contains(Change.GRAMMAR)) {
            updateGrammar(source.getGrammar());
        } else if (changes.contains(GTS)
            && source.getGts() != oldModel.getGts()) {
            startSimulation(source.getGts());
        } else if (changes.contains(STATE)) {
            GraphState newState = source.getState();
            if (newState == null) {
                clearSelectedMatch(true);
                displayState(null);
            } else {
                GraphTransition transition = oldModel.getTransition();
                GraphState target =
                    transition == null ? null : transition.target();
                if (target == newState) {
                    HostGraphMorphism morphism = transition.getMorphism();
                    copyLayout(getAspectMap(transition.source()),
                        getAspectMap(newState), morphism,
                        extractColors(transition));
                }
                // set the graph model to the new state
                displayState(newState);
            }
        }
        if (changes.contains(MATCH)) {
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
        getTabLabel().setTitle(getTitle());
        if (state != null && state.isError()) {
            getErrorPanel().setEntries(errors);
            getMainPane().setBottomComponent(getErrorPanel());
            getMainPane().resetToPreferredSizes();
            getTabLabel().setError(true);
        } else {
            getErrorPanel().clearEntries();
            getTabLabel().setError(false);
            getMainPane().remove(getErrorPanel());
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
            result.append(HTMLConverter.STRONG_TAG.on(getSimulatorModel().getState().toString()));
            MatchResult match = getSimulatorModel().getMatch();
            if (match != null) {
                if (getOptions().isSelected(SHOW_ANCHORS_OPTION)) {
                    result.append(String.format(" (with match %s)",
                        match.getEvent()));
                } else {
                    result.append(String.format(" (with match of %s)",
                        match.getEvent().getRule().getName()));
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
                copyNextStateLayout((GraphNextState) state);
            } else {
                assert state instanceof StartGraphState;
                // this is the start state
                setStartGraphLayout(result);
            }
        }
        return result;
    }

    /** Copies layout from a parent state. */
    private void copyNextStateLayout(GraphNextState state) {
        HostToAspectMap aspectMap = getAspectMap(state);
        // mapping from host nodes to colours
        Map<HostNode,Color> colorMap = extractColors(state);
        GraphState oldState = state.source();
        HostGraphMorphism morphism = state.getMorphism();
        // walk back along the derivation chain to find one for
        // which we have a state model (and hence layout information)
        while (!this.stateToAspectMap.containsKey(oldState)
            && oldState instanceof GraphNextState) {
            GraphNextState oldNextState = (GraphNextState) oldState;
            Map<HostNode,Color> oldColorMap = extractColors(oldNextState);
            // only retain colours for nodes that are preserved by the morphism
            // and that are not overwritten by newer colours
            for (Map.Entry<HostNode,Color> oldColorEntry : oldColorMap.entrySet()) {
                HostNode newNode = morphism.getNode(oldColorEntry.getKey());
                if (!colorMap.containsKey(newNode)) {
                    colorMap.put(newNode, oldColorEntry.getValue());
                }
            }
            oldState = oldNextState.source();
            morphism = oldNextState.getMorphism().then(morphism);
        }
        // the following call will make sure the start state
        // is actually loaded
        getAspectJModel(oldState);
        HostToAspectMap oldStateMap = getAspectMap(oldState);
        copyLayout(oldStateMap, aspectMap, morphism, colorMap);
    }

    /**
     * Extracts the colours that were added to the target state of a given
     * graph transition.
     */
    private Map<HostNode,Color> extractColors(GraphTransition transition) {
        Map<HostNode,Color> result = new HashMap<HostNode,Color>();
        RuleApplication application = transition.createRuleApplication();
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
        return result;
    }

    /** Copies layout from the host model of the start graph. */
    private void setStartGraphLayout(AspectJModel result) {
        AspectGraph startGraph = getGrammar().getStartGraphModel().getSource();
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
            stateVertex.getAttributes().applyMap(graphVertex.getAttributes());
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

    /**
     * Copies the layout information from one JModel to another,
     * modulo a mapping from the nodes and edges of the underlying graphs.
     * @param oldState the host graph to copy the layout information from
     * @param newState the host graph to copy the layout information to
     * @param morphism mapping from the nodes and edges of the old to the
     *        new host graph
     */
    private void copyLayout(HostToAspectMap oldState, HostToAspectMap newState,
            HostGraphMorphism morphism, Map<HostNode,Color> colorMap) {
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
            if (oldStateNode == null) {
                continue;
            }
            AspectNode newStateNode = newState.getNode(entry.getValue());
            AspectJVertex sourceCell =
                oldStateJModel.getJCellForNode(oldStateNode);
            assert sourceCell != null : "Source element " + oldStateNode
                + " unknown";
            AspectJVertex targetCell =
                newStateJModel.getJCellForNode(newStateNode);
            assert targetCell != null : "Target element " + newStateNode
                + " unknown";
            Rectangle2D sourceBounds =
                GraphConstants.getBounds(sourceCell.getAttributes());
            GraphConstants.setBounds(targetCell.getAttributes(), sourceBounds);
            Color sourceColor =
                GraphConstants.getForeground(sourceCell.getAttributes());
            if (sourceColor != null) {
                GraphConstants.setForeground(targetCell.getAttributes(),
                    sourceColor);
            }
            targetCell.setLayoutable(false);
            targetCell.setGrayedOut(sourceCell.isGrayedOut());
            targetCell.setColor(sourceCell.getColor());
            newStateJModel.synchroniseLayout(targetCell);
        }
        for (Map.Entry<HostNode,Color> colorEntry : colorMap.entrySet()) {
            AspectNode newStateNode = newState.getNode(colorEntry.getKey());
            AspectJVertex targetCell =
                newStateJModel.getJCellForNode(newStateNode);
            targetCell.setColor(colorEntry.getValue());
            newStateJModel.synchroniseLayout(targetCell);
        }
        Set<AspectEdge> newEdges =
            new HashSet<AspectEdge>(newStateJModel.getGraph().edgeSet());
        for (Map.Entry<HostEdge,HostEdge> entry : morphism.edgeMap().entrySet()) {
            AspectEdge oldStateEdge = oldState.getEdge(entry.getKey());
            if (oldStateEdge == null) {
                continue;
            }
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

    private final LTSDisplay display;
    private JSplitPane mainPane;
    private JComponent statePanel;
    private ErrorListPanel errorPanel;
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

    /** The tab label for this tab. */
    private TabLabel tabLabel;
    /** Flag indicating that the listeners are activated. */
    private boolean listening;
    private SimulatorListener simulatorListener;
    private GraphSelectionListener graphSelectionListener;

    /** The currently emphasised match (nullable). */
    private boolean matchSelected;
    /** Tab label in case there is no state selected. */
    public static final String NO_STATE_TEXT = "No state";
    /** Display name of this panel. */
    public static final String FRAME_NAME = "Current state";

}