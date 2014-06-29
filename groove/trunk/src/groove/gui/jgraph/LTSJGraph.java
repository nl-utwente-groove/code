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

import static groove.gui.Options.SHOW_ABSENT_STATES_OPTION;
import static groove.gui.Options.SHOW_CONTROL_STATE_OPTION;
import static groove.gui.Options.SHOW_RECIPE_STEPS_OPTION;
import static groove.gui.Options.SHOW_STATE_IDS_OPTION;
import static groove.gui.Options.SHOW_STATE_STATUS_OPTION;
import static groove.gui.jgraph.JGraphMode.SELECT_MODE;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.GraphRole;
import groove.graph.Node;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.menu.ModelCheckingMenu;
import groove.gui.menu.MyJMenu;
import groove.lts.GTS;
import groove.lts.GraphNextState;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.RecipeTransition;
import groove.lts.RuleTransition;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;

import org.jgraph.graph.GraphModel;

/**
 * Implementation of MyJGraph that provides the proper popup menu. To construct
 * an instance, setupPopupMenu() should be called after all global final
 * variables have been set.
 */
public class LTSJGraph extends JGraph<GTS> implements Serializable {
    /** Constructs an instance of the j-graph for a given simulator. */
    public LTSJGraph(Simulator simulator) {
        super(simulator);
        this.simulator = simulator;
        // turn off double buffering to improve performance
        setDoubleBuffered(false);
    }

    @Override
    protected void installListeners() {
        super.installListeners();
        addOptionListener(SHOW_STATE_IDS_OPTION);
        addOptionListener(SHOW_STATE_STATUS_OPTION);
        addOptionListener(SHOW_CONTROL_STATE_OPTION);
        addOptionListener(SHOW_ABSENT_STATES_OPTION);
        addOptionListener(SHOW_RECIPE_STEPS_OPTION);
    }

    @Override
    protected RefreshListener getRefreshListener(String option) {
        RefreshListener result = null;
        if (SHOW_RECIPE_STEPS_OPTION.equals(option)) {
            result = new RefreshListener() {
                @Override
                protected void doRefresh() {
                    LTSJModel model = getModel();
                    if (model != null) {
                        GTS gts = getModel().getGraph();
                        if (gts != null && gts.hasTransientStates()) {
                            model.setLayoutable(false);
                            model.loadGraph(model.getGraph());
                            refreshAllCells();
                            doLayout(false);
                        }
                    }
                }
            };
        } else if (SHOW_ABSENT_STATES_OPTION.equals(option)) {
            result = new RefreshListener() {
                @Override
                protected void doRefresh() {
                    LTSJModel model = getModel();
                    if (model != null) {
                        GTS gts = getModel().getGraph();
                        if (gts != null && gts.hasAbsentStates()) {
                            model.setLayoutable(false);
                            model.loadGraph(model.getGraph());
                            refreshAllCells();
                            doLayout(false);
                        }
                    }
                }
            };
        } else {
            result = super.getRefreshListener(option);
        }
        return result;
    }

    @Override
    public GraphRole getGraphRole() {
        return GraphRole.LTS;
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

    /** Indicates if state identities should be shown on states. */
    public boolean isShowStateIdentities() {
        return getOptionValue(Options.SHOW_STATE_IDS_OPTION);
    }

    /** Indicates if state status should be shown on states. */
    public boolean isShowStateStatus() {
        return getOptionValue(Options.SHOW_STATE_STATUS_OPTION);
    }

    /** Indicates if control state info should be shown on states. */
    public boolean isShowControlStates() {
        return getOptionValue(Options.SHOW_CONTROL_STATE_OPTION);
    }

    /** Indicates if absent states should be shown. */
    public boolean isShowAbsentStates() {
        return getOptionValue(Options.SHOW_ABSENT_STATES_OPTION);
    }

    /** Indicates if in-recipe states and transitions should be shown. */
    public boolean isShowRecipeSteps() {
        return getOptionValue(Options.SHOW_RECIPE_STEPS_OPTION);
    }

    /**
     * Scrolls the view to a given node or edge of the underlying graph model.
     */
    public void scrollTo(Element nodeOrEdge) {
        JCell<GTS> cell = getModel().getJCell(nodeOrEdge);
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
        MyJMenu result = new MyJMenu("Popup");
        if (getMode() == SELECT_MODE) {
            result.addSubmenu(createExploreMenu());
            result.addSubmenu(createGotoMenu());
            result.addSubmenu(super.createPopupMenu(atPoint));
        } else {
            result.addSubmenu(createGotoMenu());
            result.addSubmenu(createShowHideMenu());
            result.addSubmenu(createZoomMenu());
        }
        return result;
    }

    @Override
    public JMenu createExportMenu() {
        MyJMenu result = new MyJMenu();
        result.add(getActions().getSaveLTSAsAction());
        result.add(getActions().getSaveStateAction());
        result.addMenuItems(super.createExportMenu());
        return result;
    }

    /** Creates a state exploration sub-menu. */
    public JMenu createExploreMenu() {
        JMenu result = new JMenu("Explore");
        result.add(getActions().getExplorationDialogAction());
        result.add(getActions().getApplyMatchAction());
        result.add(getActions().getExploreAction());
        result.addSeparator();
        result.add(getCheckerMenu());
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
     * Lazily creates and returns the model-checking menu.
     */
    private JMenu getCheckerMenu() {
        if (this.checkerMenu == null) {
            this.checkerMenu = new ModelCheckingMenu(this.simulator);
        }
        return this.checkerMenu;
    }

    private JMenu checkerMenu;

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
        List<JCell<GTS>> activeCells = new ArrayList<JCell<GTS>>();
        List<JCell<GTS>> changedCells = new ArrayList<JCell<GTS>>();
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
            LTSJVertex jCell = (LTSJVertex) getModel().getJCellForNode(previousState);
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

    /**
     * Refreshes the active state and transition, if any.
     * This is necessary after reloading the LTS.
     */
    void reactivate() {
        List<JCell<GTS>> activeCells = new ArrayList<JCell<GTS>>();
        GraphState activeState = getActiveState();
        if (activeState != null) {
            LTSJCell activeCell = (LTSJCell) getModel().getJCellForNode(activeState);
            if (activeCell != null) {
                activeCell.setActive(true);
                activeCells.add(activeCell);
            }
        }
        GraphTransition activeTrans = getActiveTransition();
        if (activeTrans != null) {
            LTSJCell activeCell = (LTSJCell) getModel().getJCellForEdge(activeTrans);
            if (activeCell != null) {
                activeCell.setActive(true);
                activeCells.add(activeCell);
            }
        }
        if (!activeCells.isEmpty()) {
            setSelectionCells(activeCells.toArray());
            refreshCells(activeCells);
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
                jCell = (LTSJCell) getModel().getJCellForNode(subTrans.source());
                if (jCell != null) {
                    result.add(jCell);
                }
            }
        }
        return result;
    }

    /** Returns the traces from the given set of states to the start state. */
    public Set<JCell<GTS>> findTraces(Collection<GraphState> states) {
        Set<GraphTransition> simulatorTrace = new HashSet<GraphTransition>();
        simulatorTrace.clear();
        Set<JCell<GTS>> result = new HashSet<JCell<GTS>>();
        LTSJModel model = getModel();
        for (GraphState finalState : states) {
            GraphState state = finalState;
            while (state instanceof GraphNextState) {
                result.add(model.getJCellForNode(state));
                GraphTransition trans = ((GraphNextState) state).getInTransition();
                result.add(model.getJCellForEdge(trans));
                simulatorTrace.add(trans);
                state = trans.source();
            }
            result.add(model.getJCellForNode(state));
        }
        getSimulatorModel().setTrace(simulatorTrace);
        return result;
    }

    /** Filters the LTS. */
    public void refreshFiltering() {
        boolean changed = false;
        if (isFiltering()) {
            Set<JCell<GTS>> trace = findTraces(getModel().getGraph().getResultStates());
            for (Object element : getRoots()) {
                LTSJCell jCell = (LTSJCell) element;
                changed |= jCell.setVisibleFlag(trace.isEmpty() || trace.contains(jCell));
            }
        } else {
            for (Object element : getRoots()) {
                LTSJCell jCell = (LTSJCell) element;
                changed |= jCell.setVisibleFlag(true);
            }
        }
        if (changed) {
            refreshAllCells();
        }
    }

    /** Set the filtering flag of this model to the given value. */
    public void setFiltering(boolean filtering) {
        this.filtering = filtering;
    }

    /** Returns the filtering flag of this model. */
    private boolean isFiltering() {
        return this.filtering;
    }

    private boolean filtering = false;

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
    private final ScrollToCurrentAction scrollToCurrentAction = new ScrollToCurrentAction();

    /**
     * Action to scroll the LTS display to a (previously set) node or edge.
     * @see #scrollTo(Element)
     */
    public class ScrollToCurrentAction extends AbstractAction {
        @Override
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

    @Override
    protected JGraphFactory<GTS> createFactory() {
        return new MyFactory();
    }

    private class MyFactory extends JGraphFactory<GTS> {
        public MyFactory() {
            super(LTSJGraph.this);
        }

        /* The node is expected to be a non-null GraphState. */
        @Override
        public LTSJVertex newJVertex(Node node) {
            assert node instanceof GraphState;
            return LTSJVertex.newInstance();
        }

        /* The edge is expected to be a non-null GraphTransition. */
        @Override
        public LTSJEdge newJEdge(Edge edge) {
            assert edge instanceof GraphTransition;
            return LTSJEdge.newInstance();
        }

        @Override
        public LTSJModel newModel() {
            return new LTSJModel((LTSJGraph) getJGraph());
        }
    }
}