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
 * $Id: LTSJGraph.java,v 1.10 2008-01-30 09:33:14 iovka Exp $
 */
package groove.gui.jgraph;

import static groove.gui.jgraph.JGraphMode.SELECT_MODE;
import groove.graph.Element;
import groove.gui.ModelCheckingMenu;
import groove.gui.Options;
import groove.gui.SetLayoutMenu;
import groove.gui.Simulator;
import groove.gui.layout.Layouter;
import groove.gui.layout.SpringLayouter;
import groove.gui.tree.LabelTree;
import groove.lts.GraphNextState;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.RecipeTransition;
import groove.lts.RuleTransition;
import groove.util.Colors;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;

import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphModel;

/**
 * Implementation of MyJGraph that provides the proper popup menu. To construct
 * an instance, setupPopupMenu() should be called after all global final
 * variables have been set.
 */
public class LTSJGraph extends GraphJGraph implements Serializable {
    /** Constructs an instance of the j-graph for a given simulator. */
    public LTSJGraph(Simulator simulator) {
        super(simulator, true);
        this.simulator = simulator;
        // turn off double buffering to improve performance
        setDoubleBuffered(false);
    }

    @Override
    public void setModel(GraphModel model) {
        // reset the active state and transition
        this.activeState = null;
        this.activeTransition = null;
        super.setModel(model);
    }

    /** Specialises the return type to a {@link LTSJModel}. */
    @Override
    public LTSJModel getModel() {
        return (LTSJModel) this.graphModel;
    }

    @Override
    public LTSJModel newModel() {
        return new LTSJModel(this, LTSJVertex.getPrototype(this),
            LTSJEdge.getPrototype(this));
    }

    @Override
    public boolean isShowNodeIdentities() {
        return getOptionValue(Options.SHOW_STATE_IDS_OPTION);
    }

    /** Indicates if partial transitions and transient states should be shown. */
    public boolean isShowPartialTransitions() {
        return getOptionValue(Options.SHOW_PARTIAL_GTS_OPTION);
    }

    /**
     * Node hiding doesn't mean much in the LTS, so always show the edges unless
     * explicitly filtered.
     */
    @Override
    public boolean isShowUnfilteredEdges() {
        return true;
    }

    /**
     * Scrolls the view to a given node or edge of the underlying graph model.
     */
    public void scrollTo(Element nodeOrEdge) {
        GraphJCell cell = getModel().getJCell(nodeOrEdge);
        if (cell != null) {
            scrollCellToVisible(cell);
        }
    }

    /**
     * This implementation adds actions to move to different states within the
     * LTS, to apply the current transition and to explore the LTS, and
     * subsequently invokes the super implementation.
     */
    @Override
    public JMenu createPopupMenu(Point atPoint) {
        JMenu result = new JMenu("Popup");
        if (getMode() == SELECT_MODE) {
            addSubmenu(result, createExploreMenu());
            addSubmenu(result, createGotoMenu());
            addSubmenu(result, super.createPopupMenu(atPoint));
        } else {
            addSubmenu(result, createGotoMenu());
            addSubmenu(result, createShowHideMenu());
            addSubmenu(result, createZoomMenu());
        }
        return result;
    }

    @Override
    public JMenu createExportMenu() {
        JMenu result = new JMenu();
        result.add(getActions().getSaveLTSAsAction());
        result.add(getActions().getSaveStateAction());
        addMenuItems(result, super.createExportMenu());
        return result;
    }

    /** Creates a state exploration sub-menu. */
    public JMenu createExploreMenu() {
        JMenu result = new JMenu("Explore");
        result.add(getActions().getExplorationDialogAction());
        result.add(getActions().getApplyMatchAction());
        result.add(getActions().getExploreAction());
        result.addSeparator();
        result.add(createCheckerMenu());
        return result;
    }

    /** Creates a traversal sub-menu. */
    public JMenu createGotoMenu() {
        JMenu result = new JMenu("Go To");
        result.add(getActions().getGotoStartStateAction());
        result.add(getActions().getGotoFinalStateAction());
        result.add(getScrollToCurrentAction());
        return result;
    }

    /**
     * Overwrites the menu, so the forest layouter takes the LTS start state as
     * its root.
     */
    @Override
    public SetLayoutMenu createSetLayoutMenu() {
        SetLayoutMenu result = new SetLayoutMenu(this, new MyForestLayouter());
        result.addLayoutItem(new SpringLayouter());
        return result;
    }

    /**
     * Lazily creates and returns the model-checking menu.
     */
    protected final JMenu createCheckerMenu() {
        return new ModelCheckingMenu(this.simulator);
    }

    @Override
    protected LabelTree createLabelTree() {
        // no tool bar on the label tree
        return new LabelTree(this, false, isFiltering());
    }

    /**
     * Returns the active transition of the LTS, if any. The active transition
     * is the one currently selected in the simulator. Returns <tt>null</tt> if
     * no transition is selected.
     */
    public GraphTransition getActiveTransition() {
        return this.activeTransition;
    }

    /**
     * Returns the active state of the LTS, if any. The active transition is the
     * one currently displayed in the state frame. Returns <tt>null</tt> if no
     * state is active (which should occur only if no grammar is loaded and
     * hence the LTS is empty).
     */
    public GraphState getActiveState() {
        return this.activeState;
    }

    /**
     * Sets the active state and transition to a new value. Both old and new
     * values may be <tt>null</tt>.
     * @param state the new active state
     * @param trans the new active transition
     */
    public void setActive(GraphState state, GraphTransition trans) {
        List<GraphJCell> activeCells = new ArrayList<GraphJCell>();
        List<GraphJCell> changedCells = new ArrayList<GraphJCell>();
        GraphTransition previousTrans = getActiveTransition();
        this.activeTransition = trans;
        if (previousTrans != null) {
            for (LTSJCell jCell : getTransitionCells(previousTrans)) {
                if (jCell.setActive(false)) {
                    changedCells.add(jCell);
                }
            }
        }
        if (trans != null) {
            for (LTSJCell jCell : getTransitionCells(trans)) {
                activeCells.add(jCell);
                if (jCell.setActive(true)) {
                    changedCells.add(jCell);
                }
            }
        }
        GraphState previousState = this.activeState;
        this.activeState = state;
        if (previousState != null) {
            LTSJVertex jCell =
                (LTSJVertex) getModel().getJCellForNode(previousState);
            if (jCell != null && jCell.setActive(false)) {
                changedCells.add(jCell);
            }
        }
        if (state != null && getModel() != null) {
            LTSJVertex jCell = (LTSJVertex) getModel().getJCellForNode(state);
            if (jCell != null) {
                if (jCell.setActive(true)) {
                    changedCells.add(jCell);
                }
                activeCells.add(jCell);
            }
        }
        if (!changedCells.isEmpty()) {
            setSelectionCells(activeCells.toArray());
            refreshCells(changedCells);
        }
        Element elem = state == null ? trans : state;
        if (elem != null) {
            scrollTo(elem);
        }
    }

    /** Collects all cells for a given transition and its subtransitions. */
    private Collection<LTSJCell> getTransitionCells(GraphTransition trans) {
        Collection<LTSJCell> result = new ArrayList<LTSJCell>();
        LTSJCell jCell = (LTSJCell) getModel().getJCellForEdge(trans);
        if (jCell != null) {
            result.add(jCell);
        }
        if (trans instanceof RecipeTransition) {
            for (RuleTransition subTrans : ((RecipeTransition) trans).getSteps()) {
                jCell = (LTSJCell) getModel().getJCellForEdge(subTrans);
                if (jCell != null) {
                    result.add(jCell);
                }
                jCell =
                    (LTSJCell) getModel().getJCellForNode(subTrans.source());
                if (jCell != null) {
                    result.add(jCell);
                }
            }
        }
        return result;
    }

    /** Returns the traces from the given set of states to the start state. */
    public Set<GraphJCell> findTraces(Collection<GraphState> states) {
        Set<RuleTransition> simulatorTrace = getSimulatorModel().getTrace();
        simulatorTrace.clear();
        Set<GraphJCell> result = new HashSet<GraphJCell>();
        LTSJModel model = getModel();
        for (GraphState finalState : states) {
            GraphState state = finalState;
            while (state instanceof GraphNextState) {
                result.add(model.getJCellForNode(state));
                result.add(model.getJCellForEdge((RuleTransition) state));
                simulatorTrace.add((RuleTransition) state);
                state = ((GraphNextState) state).source();
            }
            result.add(model.getJCellForNode(state));
        }
        return result;
    }

    /** Filters the LTS. */
    public void refreshFiltering() {
        if (getModel().isFiltering()) {
            Set<GraphJCell> trace =
                findTraces(getModel().getGraph().getResultStates());
            for (Object element : getRoots()) {
                LTSJCell jCell = (LTSJCell) element;
                jCell.setVisible(trace.isEmpty() || trace.contains(jCell));
            }
        } else {
            for (Object element : getRoots()) {
                LTSJCell jCell = (LTSJCell) element;
                jCell.setVisible(true);
            }
        }
        refreshAllCells();
    }

    @Override
    public boolean isFiltering(Element jCellKey) {
        boolean result = super.isFiltering(jCellKey);
        if (!result && !isShowPartialTransitions()) {
            if (jCellKey instanceof RuleTransition) {
                result = ((RuleTransition) jCellKey).isPartial();
            } else if (jCellKey instanceof GraphState) {
                result = ((GraphState) jCellKey).isTransient();
            }
        }
        return result;
    }

    /**
     * The active state of the LTS. Is null if there is no active state.
     * @invariant activeState == null || ltsJModel.graph().contains(activeState)
     */
    private GraphState activeState;
    /**
     * The currently active transition of the LTS. The source node of
     * emphasizedEdge (if non-null) is also emphasized. Is null if there is no
     * currently emphasized edge.
     * @invariant activeTransition == null ||
     *            ltsJModel.graph().contains(activeTransition)
     */
    private GraphTransition activeTransition;

    /**
     * The simulator to which this j-graph is associated.
     */
    private final Simulator simulator;

    /** Initialises and returns the action to scroll to the active state or transition. */
    private Action getScrollToCurrentAction() {
        if (getActiveTransition() == null) {
            this.scrollToCurrentAction.setState(getSimulatorModel().getState());
        } else {
            this.scrollToCurrentAction.setTransition(getSimulatorModel().getTransition());
        }
        return this.scrollToCurrentAction;
    }

    /**
     * Action to scroll the JGraph to the current state or derivation.
     */
    private final ScrollToCurrentAction scrollToCurrentAction =
        new ScrollToCurrentAction();
    /** The default node attributes of the LTS */
    static public final JAttr.AttributeMap LTS_NODE_ATTR;
    /** The start node attributes of the LTS */
    static public final JAttr.AttributeMap LTS_START_NODE_ATTR;
    /** Unexplored node attributes */
    static public final JAttr.AttributeMap LTS_OPEN_NODE_ATTR;
    /** Final node attributes */
    static public final JAttr.AttributeMap LTS_FINAL_NODE_ATTR;
    /** Result node attributes */
    static public final JAttr.AttributeMap LTS_RESULT_NODE_ATTR;
    /** Error node attributes */
    static public final JAttr.AttributeMap LTS_ERROR_NODE_ATTR;
    /** The default edge attributes of the LTS */
    static public final JAttr.AttributeMap LTS_EDGE_ATTR;
    /** Transient node attributes of the LTS */
    static public final JAttr.AttributeMap LTS_NODE_TRANSIENT_CHANGE;
    /** Transient edge attributes of the LTS */
    static public final JAttr.AttributeMap LTS_EDGE_TRANSIENT_CHANGE;
    /** Absent node attributes of the LTS */
    static public final JAttr.AttributeMap LTS_NODE_ABSENT_CHANGE;
    /** Absent edge attributes of the LTS */
    static public final JAttr.AttributeMap LTS_EDGE_ABSENT_CHANGE;
    /** Active node attributes of the LTS */
    static public final JAttr.AttributeMap LTS_NODE_ACTIVE_CHANGE;
    /** Active edge attributes of the LTS */
    static public final JAttr.AttributeMap LTS_EDGE_ACTIVE_CHANGE;
    /** Transient active node attributes of the LTS */
    static public final JAttr.AttributeMap LTS_NODE_TRANSIENT_ACTIVE_CHANGE;
    /** Transient active edge attributes of the LTS */
    static public final JAttr.AttributeMap LTS_EDGE_TRANSIENT_ACTIVE_CHANGE;

    static private final Color FINAL_BACK = Color.red;
    static private final Color OPEN_BACK = Color.gray.brighter();
    static private final Color START_BACK = Color.green;
    static private final Color RESULT_BACK = Colors.findColor("255 165 0");
    static private final Color ACTIVE_COLOR = Color.BLUE;
    static private final Color TRANSIENT_ACTIVE_COLOR =
        Colors.findColor("165 42 149");
    // set the emphasis attributes
    static {
        // Ordinary LTS nodes and edges
        JAttr ltsValues = new JAttr() {
            {
                this.connectable = false;
                this.lineEnd = GraphConstants.ARROW_SIMPLE;
            }
        };
        LTS_NODE_ATTR = ltsValues.getNodeAttrs();
        LTS_EDGE_ATTR = ltsValues.getEdgeAttrs();
        LTS_START_NODE_ATTR = new JAttr() {
            {
                this.backColour = START_BACK;
            }
        }.getNodeAttrs();

        // Special LTS  nodes
        LTS_OPEN_NODE_ATTR = new JAttr() {
            {
                this.backColour = OPEN_BACK;
            }
        }.getNodeAttrs();
        LTS_FINAL_NODE_ATTR = new JAttr() {
            {
                this.backColour = FINAL_BACK;
            }
        }.getNodeAttrs();
        LTS_RESULT_NODE_ATTR = new JAttr() {
            {
                this.backColour = RESULT_BACK;
            }
        }.getNodeAttrs();
        LTS_ERROR_NODE_ATTR = new JAttr() {
            {
                this.backColour = this.lineColour = ERROR_COLOR;
            }
        }.getNodeAttrs();

        // active LTS nodes and edges
        JAttr ltsActive = new JAttr() {
            {
                this.lineColour = this.foreColour = ACTIVE_COLOR;
                this.linewidth = 3;
                this.lineEnd = GraphConstants.ARROW_SIMPLE;
            }
        };
        LTS_NODE_ACTIVE_CHANGE = ltsActive.getNodeAttrs().diff(LTS_NODE_ATTR);
        LTS_EDGE_ACTIVE_CHANGE = ltsActive.getEdgeAttrs().diff(LTS_EDGE_ATTR);
        // transient LTS nodes and edges
        JAttr ltsTransient = new JAttr() {
            {
                this.shape = JVertexShape.DIAMOND;
                this.foreColour = this.lineColour = TRANSIENT_COLOR;
            }
        };
        LTS_NODE_TRANSIENT_CHANGE =
            ltsTransient.getNodeAttrs().diff(LTS_NODE_ATTR);
        LTS_EDGE_TRANSIENT_CHANGE =
            ltsTransient.getEdgeAttrs().diff(LTS_EDGE_ATTR);
        // transient active LTS nodes and edges
        JAttr ltsTransientActive = new JAttr() {
            {
                this.lineColour = this.foreColour = TRANSIENT_ACTIVE_COLOR;
                this.linewidth = 3;
                this.lineEnd = GraphConstants.ARROW_SIMPLE;
            }
        };
        LTS_NODE_TRANSIENT_ACTIVE_CHANGE =
            ltsTransientActive.getNodeAttrs().diff(LTS_NODE_ATTR);
        LTS_EDGE_TRANSIENT_ACTIVE_CHANGE =
            ltsTransientActive.getEdgeAttrs().diff(LTS_EDGE_ATTR);
        // absent LTS nodes and edges
        JAttr ltsAbsent = new JAttr() {
            {
                this.dash = JAttr.ABSENT_DASH;
            }
        };
        LTS_NODE_ABSENT_CHANGE = ltsAbsent.getNodeAttrs().diff(LTS_NODE_ATTR);
        LTS_EDGE_ABSENT_CHANGE = ltsAbsent.getEdgeAttrs().diff(LTS_EDGE_ATTR);
    }

    /**
     * Action to scroll the LTS display to a (previously set) node or edge.
     * @see #scrollTo(Element)
     */
    public class ScrollToCurrentAction extends AbstractAction {
        public void actionPerformed(ActionEvent evt) {
            if (getSimulatorModel().getState() == null) {
                scrollTo(getSimulatorModel().getTransition());
            } else {
                scrollTo(getSimulatorModel().getState());
            }
        }

        /**
         * Adapts the name of the action so that it reflects that the element to
         * scroll to is a given transition.
         */
        public void setTransition(GraphTransition edge) {
            putValue(Action.NAME, Options.SCROLL_TO_ACTION_NAME + " transition");
        }

        /**
         * Adapts the name of the action so that it reflects that the element to
         * scroll to is a given state.
         */
        public void setState(GraphState node) {
            putValue(Action.NAME, Options.SCROLL_TO_ACTION_NAME + " state");
        }
    }

    /**
     * A specialisation of the forest layouter that takes the LTS start graph as
     * its suggested root.
     */
    private class MyForestLayouter extends groove.gui.layout.ForestLayouter {
        /**
         * Creates a prototype layouter
         */
        public MyForestLayouter() {
            super();
        }

        /**
         * Creates a new instance, for a given {@link GraphJGraph}.
         */
        public MyForestLayouter(String name, GraphJGraph jgraph) {
            super(name, jgraph);
        }

        /**
         * This method returns a singleton set consisting of the LTS start
         * state.
         */
        @Override
        protected Collection<?> getSuggestedRoots() {
            LTSJModel jModel = getModel();
            return Collections.singleton(jModel.getJCellForNode(jModel.getGraph().startState()));
        }

        /**
         * This implementation returns a {@link MyForestLayouter}.
         */
        @Override
        public Layouter newInstance(GraphJGraph jGraph) {
            return new MyForestLayouter(this.name, jGraph);
        }
    }
}