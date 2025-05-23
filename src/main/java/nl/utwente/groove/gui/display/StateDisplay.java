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
package nl.utwente.groove.gui.display;

import static nl.utwente.groove.gui.SimulatorModel.Change.GRAMMAR;
import static nl.utwente.groove.gui.SimulatorModel.Change.GTS;
import static nl.utwente.groove.gui.SimulatorModel.Change.MATCH;
import static nl.utwente.groove.gui.SimulatorModel.Change.STATE;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.WeakHashMap;

import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JTree;

import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;

import nl.utwente.groove.grammar.aspect.AspectEdge;
import nl.utwente.groove.grammar.aspect.AspectElement;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.aspect.AspectNode;
import nl.utwente.groove.grammar.aspect.GraphConverter;
import nl.utwente.groove.grammar.aspect.GraphConverter.HostToAspectMap;
import nl.utwente.groove.grammar.host.HostEdge;
import nl.utwente.groove.grammar.host.HostGraphMorphism;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.grammar.host.HostNodeSet;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.HostModelMap;
import nl.utwente.groove.grammar.rule.RuleNode;
import nl.utwente.groove.graph.Element;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.SimulatorListener;
import nl.utwente.groove.gui.SimulatorModel;
import nl.utwente.groove.gui.SimulatorModel.Change;
import nl.utwente.groove.gui.jgraph.AspectJCell;
import nl.utwente.groove.gui.jgraph.AspectJEdge;
import nl.utwente.groove.gui.jgraph.AspectJGraph;
import nl.utwente.groove.gui.jgraph.AspectJModel;
import nl.utwente.groove.gui.jgraph.AspectJVertex;
import nl.utwente.groove.gui.jgraph.JAttr;
import nl.utwente.groove.gui.jgraph.JGraph;
import nl.utwente.groove.gui.list.ErrorListPanel;
import nl.utwente.groove.gui.look.VisualKey;
import nl.utwente.groove.gui.look.VisualMap;
import nl.utwente.groove.gui.tree.StateTree;
import nl.utwente.groove.gui.tree.TypeTree;
import nl.utwente.groove.io.HTMLConverter;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GraphNextState;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.lts.GraphTransition;
import nl.utwente.groove.lts.MatchResult;
import nl.utwente.groove.lts.RecipeTransition;
import nl.utwente.groove.lts.RuleTransition;
import nl.utwente.groove.lts.StartGraphState;
import nl.utwente.groove.transform.Proof;
import nl.utwente.groove.transform.RuleApplication;
import nl.utwente.groove.util.line.LineStyle;
import nl.utwente.groove.util.parse.FormatError;

/**
 * Window that displays and controls the current lts graph. Auxiliary class for
 * Simulator.
 *
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-02-05 13:28:06 $
 */
public class StateDisplay extends Display implements SimulatorListener {
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
    protected JTree createList() {
        return new StateTree(getSimulator());
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
        TypeTree labelTree = getLabelTree();
        TitledPanel result
            = new TitledPanel(Options.LABEL_PANE_TITLE, labelTree, labelTree.createToolBar(), true);
        result.setEnabledBackground(JAttr.STATE_BACKGROUND);
        return result;
    }

    @Override
    protected void installListeners() {
        this.graphSelectionListener = new GraphSelectionListener() {
            @Override
            public void valueChanged(GraphSelectionEvent e) {
                if (StateDisplay.this.matchSelected) {
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
        getSimulatorModel().addListener(this, GRAMMAR, GTS, STATE, MATCH);
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
            getJGraph().removeGraphSelectionListener(this.graphSelectionListener);
            this.listening = false;
        }
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
            this.displayPanel = result = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            result.setTopComponent(getGraphPanel());
            result.setBottomComponent(getErrorPanel());
            result.setDividerSize(0);
            result.setContinuousLayout(true);
            result.setResizeWeight(0.9);
            result.resetToPreferredSizes();
            result.setBorder(null);
        }
        return result;
    }

    /** Split pane containing the {@link #stateGraphPanel} and the {@link #errorPanel}. */
    private JSplitPane displayPanel;

    /** Returns the currently displayed state graph. */
    public AspectGraph getStateGraph() {
        var jModel = getJGraph().getModel();
        assert jModel != null;
        return jModel.getGraph();
    }

    /** Returns component on which the state graph is displayed. */
    public JGraphPanel<AspectGraph> getGraphPanel() {
        JGraphPanel<AspectGraph> result = this.stateGraphPanel;
        if (result == null) {
            result = this.stateGraphPanel = new JGraphPanel<>(getJGraph()) {
                @Override
                public void setEnabled(boolean enabled) {
                    super.setEnabled(enabled);
                    getInfoPanel().setEnabled(enabled);
                }
            };
            result.initialise();
            result.setBorder(null);
            result.setEnabledBackground(JAttr.STATE_BACKGROUND);
            result.getJGraph().setToolTipEnabled(true);
        }
        return result;
    }

    /** JGraph panel on this display. */
    private JGraphPanel<AspectGraph> stateGraphPanel;

    /** Gets the error panel, creating it (lazily) if necessary. */
    private ErrorListPanel getErrorPanel() {
        if (this.errorPanel == null) {
            this.errorPanel = new ErrorListPanel("Errors in state graph");
            this.errorPanel.addSelectionListener(createErrorListener());
        }
        return this.errorPanel;
    }

    /** List of state errors, only shown if there are any errors in the current state. */
    private ErrorListPanel errorPanel;

    /** Returns the JGraph component of the state display. */
    final public AspectJGraph getJGraph() {
        AspectJGraph result = this.jGraph;
        if (result == null) {
            result = this.jGraph = new AspectJGraph(getSimulator(), getKind(), false) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (getSimulatorModel().hasAbsentState()) {
                        JAttr.paintHatch(this, g);
                    }
                }
            };
            result.setLabelTree(getLabelTree());
        }
        return result;
    }

    /** JGraph showing the current state. */
    private AspectJGraph jGraph;

    /** Lazily creates and returns the label tree for the display. */
    private TypeTree getLabelTree() {
        TypeTree result = this.labelTree;
        if (result == null) {
            result = this.labelTree = new TypeTree(getJGraph(), true) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (getSimulatorModel().hasAbsentState()) {
                        JAttr.paintHatch(this, g);
                    }
                }
            };
        }
        return result;
    }

    /** The tree component showing (and allowing filtering of) the transitions in the LTS. */
    private TypeTree labelTree;

    /** Creates the listener of the error panel. */
    private PropertyChangeListener createErrorListener() {
        return arg -> {
            if (arg != null) {
                var error = (FormatError) arg.getNewValue();
                selectError(error);
            }
        };
    }

    /** Selects the elements of a given error in the {@link JGraph}. */
    void selectError(FormatError error) {
        if (error == null) {
            getJGraph().clearSelection();
        } else {
            // the error elements may be host elements rather than aspect elements
            List<Element> elements = new ArrayList<>();
            var aspectMap = getAspectMap(getSimulatorModel().getState());
            for (var elem : error.getElements()) {
                if (elem instanceof AspectElement) {
                    elements.add(elem);
                } else if (elem instanceof HostNode node) {
                    elements.add(aspectMap.getNode(node));
                } else if (elem instanceof HostEdge edge) {
                    elements.add(aspectMap.getEdge(edge));
                }
            }
            getJGraph().setSelectionCells(elements);
        }
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel, Set<Change> changes) {
        if (!suspendListening()) {
            return;
        }
        // check if layout should be transferred
        GraphTransition oldTtrans = oldModel.getTransition();
        boolean transferLayout = oldTtrans != null && oldTtrans != source.getTransition()
            && oldTtrans.target() == source.getState();
        if (changes.contains(GTS) && source.getGTS() != oldModel.getGTS()) {
            startSimulation(source.getGTS());
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
                selectMatch(source.getMatch().getEvent().getMatch(source.getState().getGraph()));
            }
            // all cells repainted, even though everything but the
            // edge colour seems to be OK even without doing this
            getJGraph().refreshAllCells();
        }
        updateStatus();
        activateListening();
    }

    private void startSimulation(GTS gts) {
        // clear the states from the aspect and model maps
        this.stateToJModel.clear();
        this.stateToAspectMap.clear();
        // only change the displayed model if we are currently displaying a
        // state
        displayState(getSimulatorModel().getState());
    }

    /**
     * Emphasise the given match.
     * @param match the match to be emphasised (non-null)
     */
    private void selectMatch(Proof match) {
        assert match != null : "Match update should not be called with empty match";
        displayState(getSimulatorModel().getState());
        AspectJModel jModel = getJGraph().getModel();
        assert jModel != null;
        HostToAspectMap aspectMap = getAspectMap(getSimulatorModel().getState());
        Set<AspectJCell> emphElems = new HashSet<>();
        match
            .getNodeValues()
            .stream()
            .map(n -> jModel.getJCellForNode(aspectMap.getNode(n)))
            .filter(c -> c != null)
            .forEach(c -> emphElems.add(c));
        match
            .getEdgeValues()
            .stream()
            .map(e -> jModel.getJCellForEdge(aspectMap.getEdge(e)))
            .filter(c -> c != null)
            .forEach(c -> emphElems.add(c));
        getJGraph().setSelectionCells(emphElems.toArray());
        this.matchSelected = true;
    }

    /** Updates the display status bar. */
    private void updateStatus() {
        StringBuilder result = new StringBuilder();
        result.append("Current state");
        GraphState state = getSimulatorModel().getState();
        if (state != null) {
            result.append(": ");
            String stateID = state.toString();
            result.append(HTMLConverter.UNDERLINE_TAG.on(stateID));
            if (stateID.equals("s0")) {
                var startGraph = getSimulatorModel().getGrammar().getStartGraphModel();
                result.append("=");
                result.append(startGraph.getName());
            }
            MatchResult match = getSimulatorModel().getMatch();
            boolean brackets = false;
            if (state.isInner()) {
                result
                    .append(brackets
                        ? ", "
                        : " (");
                brackets = true;
                result.append("transient state");
            }
            if (state.isInner()) {
                result
                    .append(brackets
                        ? ", "
                        : " (");
                brackets = true;
                result.append("not included in state space because the enclosing ");
                result
                    .append(state.isInner()
                        ? "recipe"
                        : "atomic block");
                result.append(" does not terminate");
            }
            if (state.isError()) {
                result
                    .append(brackets
                        ? ", "
                        : " (");
                brackets = true;
                result.append("has errors");
            }
            if (match != null) {
                result
                    .append(brackets
                        ? "; "
                        : " (");
                brackets = true;
                if (getJGraph().isShowAnchors()) {
                    result.append(String.format("with match '%s'", match.getEvent()));
                } else {
                    result
                        .append(String
                            .format("with match of <i>%s</i>",
                                    match.getEvent().getRule().getQualName()));
                }
            }
            if (brackets) {
                result.append(')');
            }
        }
        getGraphPanel().getStatusLabel().setText(HTMLConverter.HTML_TAG.on(result).toString());
    }

    /** Changes the display to a given state. */
    public void displayState(GraphState state) {
        clearSelectedMatch(true);
        boolean error = false;
        boolean internal = false;
        if (state == null) {
            getJGraph().setModel(null);
        } else {
            AspectJModel model = getAspectJModel(state);
            getJGraph().setModel(model);
            getJGraph().doLayout(false);
            error = state.isError();
            internal = state.isInner();
        }
        if (error) {
            getErrorPanel().setEntries(getAspectGraph(state).getErrors().get());
            getDisplayPanel().setBottomComponent(getErrorPanel());
            getDisplayPanel().resetToPreferredSizes();
        } else {
            getErrorPanel().clearEntries();
            getDisplayPanel().remove(getErrorPanel());
        }
        Color background = JAttr.getStateBackground(error, internal);
        getGraphPanel().setEnabledBackground(background);
        getLabelTree().setBackground(background);
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
            getSimulatorModel().setMatch(getSimulatorModel().getState(), null);
            updateStatus();
        }
        return result;
    }

    /**
     * Returns a graph model for a given state graph. The graph model is
     * retrieved from {@link #stateToJModel}; if there is no image for the requested
     * state then one is created.
     */
    private AspectJModel getAspectJModel(GraphState state) {
        AspectJModel result = this.stateToJModel.get(state);
        if (result == null) {
            if (state instanceof GraphNextState ns) {
                result = createNextStateJModel(ns);
            } else {
                // this is the start state
                result = createStartStateJModel((StartGraphState) state);
            }
            assert result != null;
            this.stateToJModel.put(state, result);
        }
        return result;
    }

    /** Copies layout from the host model of the start graph. */
    private AspectJModel createStartStateJModel(StartGraphState state) {
        HostToAspectMap stateMap = getAspectMap(state);
        var result = createAspectJModel(stateMap.getAspectGraph());
        var startHostModel = getGrammar().getStartGraphModel();
        AspectGraph startGraph = startHostModel.getSource();
        assert startGraph != null;
        HostModelMap startGraphMap = startHostModel.getMap();
        // the AspectGraph in result does not equal startGraph, we have to convert
        var startJModel = createAspectJModel(startGraph);
        for (AspectNode node : startGraph.nodeSet()) {
            AspectJVertex stateVertex
                = result.getJCellForNode(stateMap.getNode(startGraphMap.getNode(node)));
            // nesting nodes are not in the state;
            // data nodes may have been merged
            if (stateVertex == null) {
                continue;
            }
            AspectJVertex graphVertex = startJModel.getJCellForNode(node);
            stateVertex.putVisuals(graphVertex.getVisuals());
            stateVertex.setGrayedOut(graphVertex.isGrayedOut());
            result.synchroniseLayout(stateVertex);
            stateVertex.setLayoutable(false);
        }
        for (AspectEdge edge : startGraph.edgeSet()) {
            AspectJCell stateEdge
                = result.getJCellForEdge(stateMap.getEdge(startGraphMap.getEdge(edge)));
            // nesting edges and merged data edges are not in the state
            if (stateEdge == null) {
                continue;
            }
            AspectJCell graphEdge = startJModel.getJCellForEdge(edge);
            stateEdge.putVisuals(graphEdge.getVisuals());
            stateEdge.setGrayedOut(graphEdge.isGrayedOut());
            result.synchroniseLayout(stateEdge);
        }
        return result;
    }

    private AspectJModel createNextStateJModel(GraphNextState state) {
        var result = createAspectJModel(getAspectMap(state).getAspectGraph());
        Stack<GraphTransition> stack = new Stack<>();
        GraphState source = state;
        do {
            GraphTransition trans = ((GraphNextState) source).getInTransition();
            stack.push(trans);
            source = trans.source();
        } while (source instanceof GraphNextState && !this.stateToJModel.containsKey(source));
        AspectJModel model = getAspectJModel(source);
        AttributesMap map = extractAttributes(model, getAspectMap(source));
        while (!stack.isEmpty()) {
            GraphTransition trans = stack.pop();
            map = transferAttributes(map, trans);
        }
        applyAttributes(map, result, getAspectMap(state));
        return result;
    }

    /**
     * Returns a map from host graph elements to layout attributes,
     * extracted from a given aspect model under a host-to-aspect map.
     */
    private AttributesMap extractAttributes(AspectJModel model, HostToAspectMap aspectMap) {
        AttributesMap result = new AttributesMap();
        for (Map.Entry<HostNode,? extends AspectNode> entry : aspectMap.nodeMap().entrySet()) {
            AspectNode aspectNode = entry.getValue();
            AspectJVertex jCell = model.getJCellForNode(aspectNode);
            assert jCell != null : "Source element " + aspectNode + " unknown";
            result.nodeMap.put(entry.getKey(), new Attributes(jCell));
        }
        // compute target edge attributes
        for (Map.Entry<HostEdge,? extends AspectEdge> entry : aspectMap.edgeMap().entrySet()) {
            AspectEdge aspectEdge = entry.getValue();
            AspectJCell jCell = model.getJCellForEdge(aspectEdge);
            if (jCell instanceof AspectJEdge) {
                result.edgeMap.put(entry.getKey(), new Attributes((AspectJEdge) jCell));
            }
        }
        return result;
    }

    /** Creates a mapping from host elements to layout attributes for
     * the target graph of a transition, given the attributes mapping for
     * the source graph.
     */
    private AttributesMap transferAttributes(AttributesMap map, GraphTransition trans) {
        AttributesMap result = new AttributesMap();
        HostGraphMorphism morphism = trans.getMorphism();
        Map<HostNode,Attributes> sourceNodeMap = map.nodeMap;
        Map<HostNode,Attributes> resultNodeMap = result.nodeMap;
        Map<HostNode,Color> newColorMap = extractNewColors(trans);
        HostNodeSet newNodes = new HostNodeSet(trans.target().getGraph().nodeSet());
        // transfer node attributes
        for (Map.Entry<HostNode,HostNode> entry : morphism.nodeMap().entrySet()) {
            HostNode sourceNode = entry.getKey();
            Attributes attr = sourceNodeMap.get(sourceNode);
            HostNode targetNode = entry.getValue();
            assert trans.target().getGraph().containsNode(targetNode);
            Color newColor = newColorMap.get(targetNode);
            if (newColor != null) {
                if (attr == null) {
                    attr = new Attributes(newColor);
                } else {
                    attr.color = newColor;
                }
            }
            if (attr != null) {
                resultNodeMap.put(targetNode, attr);
            }
            newNodes.remove(targetNode);
        }
        // add node colours for the new nodes
        for (HostNode newNode : newNodes) {
            Color newColor = newColorMap.get(newNode);
            if (newColor != null) {
                Attributes attr = new Attributes(newColor);
                resultNodeMap.put(newNode, attr);
            }
        }
        // transfer edge attributes
        Map<HostEdge,Attributes> sourceEdgeMap = map.edgeMap;
        Map<HostEdge,Attributes> resultEdgeMap = result.edgeMap;
        for (Map.Entry<HostEdge,HostEdge> entry : morphism.edgeMap().entrySet()) {
            HostEdge sourceEdge = entry.getKey();
            Attributes attr = sourceEdgeMap.get(sourceEdge);
            if (attr != null) {
                HostEdge targetEdge = entry.getValue();
                resultEdgeMap.put(targetEdge, attr);
            }
        }
        return result;
    }

    /** Stores the computed attributes into an aspect model. */
    private void applyAttributes(AttributesMap map, AspectJModel result,
                                 HostToAspectMap aspectMap) {
        // initially set all cells to layoutable,
        // (which is partially undone later)
        // so the new nodes and edges get a change of being layed out
        result.setLayoutable(true);
        for (Map.Entry<HostNode,Attributes> e : map.nodeMap.entrySet()) {
            AspectNode aspectNode = aspectMap.getNode(e.getKey());
            assert aspectNode != null : "Target element " + e.getKey() + " unknown";
            AspectJVertex jCell = result.getJCellForNode(aspectNode);
            assert jCell != null : "Target element " + aspectNode + " unknown";
            Attributes attrs = e.getValue();
            jCell.putVisuals(attrs.toVisuals());
            jCell.setGrayedOut(attrs.grayedOut);
            jCell.setLayoutable(attrs.pos == null);
            result.synchroniseLayout(jCell);
            if (attrs.color != null) {
                // also colour all outgoing edges
                Iterator<? extends AspectJEdge> iter = jCell.getContext();
                while (iter.hasNext()) {
                    AspectJEdge jEdge = iter.next();
                    if (jEdge.getSourceVertex() == jCell) {
                        jEdge.putVisual(VisualKey.COLOR, attrs.color);
                    }
                }
            }
        }
        // store target edge attributes
        for (Map.Entry<HostEdge,Attributes> e : map.edgeMap.entrySet()) {
            AspectEdge aspectEdge = aspectMap.getEdge(e.getKey());
            assert aspectEdge != null : "Target element " + e.getKey() + " unknown";
            AspectJCell jCell = result.getJCellForEdge(aspectEdge);
            if (jCell instanceof AspectJVertex) {
                continue;
            }
            assert jCell != null : "Target element " + aspectEdge + " unknown";
            Attributes attr = e.getValue();
            jCell.putVisuals(attr.toVisuals());
            jCell.setGrayedOut(attr.grayedOut);
            result.synchroniseLayout(jCell);
        }
    }

    /** Transfers colours and layout from the source to the target of a given transition. */
    private void transferLayout(GraphTransition trans) {
        AttributesMap map = extractAttributes(this.stateToJModel.get(trans.source()),
                                              getAspectMap(trans.source()));
        map = transferAttributes(map, trans);
        applyAttributes(map, this.stateToJModel.get(trans.target()), getAspectMap(trans.target()));
    }

    /**
     * Creates a mapping from host nodes to colours
     * as newly generated by a given transition.
     */
    private Map<HostNode,Color> extractNewColors(GraphTransition trans) {
        Map<HostNode,Color> result = new HashMap<>();
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
    private Map<HostNode,Color> transferColors(Map<HostNode,Color> colorMap, RuleTransition trans) {
        Map<HostNode,Color> result = new HashMap<>();
        // extract new colours from target
        RuleApplication application = trans.createRuleApplication();
        Map<RuleNode,HostNodeSet> comatch = application.getComatch();
        for (Map.Entry<RuleNode,Color> colorEntry : application
            .getRule()
            .getColorMap()
            .entrySet()) {
            HostNodeSet matches = comatch.get(colorEntry.getKey());
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
     * Returns the aspect graph for a given state.
     * Convenience method for {@code getAspectMap(state).getAspectGraph()}.
     */
    private AspectGraph getAspectGraph(GraphState state) {
        return getAspectMap(state).getAspectGraph();
    }

    /**
     * Returns the aspect map for a given state.
     * Retrieves the result from {@link #stateToAspectMap},
     * creating and inserting it if necessary.
     */
    private HostToAspectMap getAspectMap(GraphState state) {
        HostToAspectMap result = this.stateToAspectMap.get(state);
        if (result == null) {
            this.stateToAspectMap.put(state, result = GraphConverter.toAspectMap(state.getGraph()));
        }
        return result;
    }

    /**
     * Mapping from graphs to the corresponding graph models.
     */
    private final Map<GraphState,AspectJModel> stateToJModel = new WeakHashMap<>();
    /**
     * Mapping from graphs to the corresponding graph models.
     */
    private final Map<GraphState,HostToAspectMap> stateToAspectMap = new WeakHashMap<>();

    /** Flag indicating that the listeners are activated. */
    private boolean listening;
    private GraphSelectionListener graphSelectionListener;
    /** Flag indicating if there is any match selected. */
    private boolean matchSelected;

    @Override
    public void doRepeat() {
        var jGraph = getJGraph();
        if (jGraph != null) {
            jGraph.scrollToNextSelectedRoot();
        }
    }

    /** Temporary record of graph element attributes. */
    private static class Attributes {
        Attributes(AspectJVertex jVertex) {
            VisualMap visuals = jVertex.getVisuals();
            this.pos = visuals.getNodePos();
            this.grayedOut = jVertex.isGrayedOut();
            this.color = visuals.getColor();
            this.points = null;
            this.labelPosition = null;
            this.lineStyle = null;
        }

        Attributes(Color color) {
            this.pos = null;
            this.grayedOut = false;
            this.color = color;
            this.points = null;
            this.labelPosition = null;
            this.lineStyle = LineStyle.DEFAULT_VALUE;
        }

        Attributes(AspectJEdge jEdge) {
            VisualMap visuals = jEdge.getVisuals();
            this.pos = null;
            this.grayedOut = jEdge.isGrayedOut();
            this.color = null;
            this.points = visuals.getPoints();
            this.labelPosition = visuals.getLabelPos();
            this.lineStyle = visuals.getLineStyle();
        }

        VisualMap toVisuals() {
            VisualMap result = new VisualMap();
            if (this.pos != null) {
                result.setNodePos(this.pos);
            }
            if (this.color != null) {
                result.setColor(this.color);
            }
            if (this.points != null) {
                result.setPoints(this.points);
            }
            if (this.labelPosition != null) {
                result.setLabelPos(this.labelPosition);
            }
            if (this.lineStyle != null) {
                result.setLineStyle(this.lineStyle);
            }
            return result;
        }

        final Point2D pos;
        Color color;
        final boolean grayedOut;
        final List<Point2D> points;
        final Point2D labelPosition;
        final LineStyle lineStyle;
    }

    /** Mapping from host elements to attributes. */
    private static class AttributesMap {
        final Map<HostNode,Attributes> nodeMap = new HashMap<>();
        final Map<HostEdge,Attributes> edgeMap = new HashMap<>();
    }
}
