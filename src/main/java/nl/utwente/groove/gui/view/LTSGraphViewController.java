/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package nl.utwente.groove.gui.view;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Action;
import javax.swing.JMenu;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.graph.Element;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.action.ScrollToActiveAction;
import nl.utwente.groove.gui.layout.ForestLayouter;
import nl.utwente.groove.gui.layout.Layouter;
import nl.utwente.groove.gui.menu.ModelCheckingMenu;
import nl.utwente.groove.gui.menu.MyJMenu;
import nl.utwente.groove.lts.ExploreResult;
import nl.utwente.groove.lts.Filter;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GTSFragment;
import nl.utwente.groove.lts.GraphNextState;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.lts.GraphTransition;
import nl.utwente.groove.lts.GraphTransition.Claz;
import nl.utwente.groove.lts.RecipeTransition;
import nl.utwente.groove.lts.RuleTransition;

/**
 * Display controller for graph views showing the LTS.
 * Adds the exploration and traversal menus.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class LTSGraphViewController extends GraphViewController<GTS> {
    /**
     * Constructs a controller for a given graph-view component.
     * @param canvas the canvas that this controller belongs to
     * @param simulator simulator to which the display belongs; may be {@code null}
     */
    public LTSGraphViewController(LTSGraphCanvas canvas, @Nullable Simulator simulator) {
        super(canvas, simulator);
    }

    /* Specialises the return type. */
    @Override
    public LTSGraphCanvas getCanvas() {
        return (LTSGraphCanvas) super.getCanvas();
    }

    /* The LTS is laid out as a forest by default. */
    @Override
    public Layouter getDefaultLayouter() {
        return ForestLayouter.PROTOTYPE;
    }

    /*
     * This implementation adds actions to move to different states within the
     * LTS, to apply the current transition and to explore the LTS, and
     * subsequently invokes the super implementation.
     */
    @Override
    public JMenu createPopupMenu(@Nullable Point atPoint) {
        MyJMenu result = new MyJMenu("Popup");
        if (getCanvas().getMode() == GraphViewMode.SELECT_MODE) {
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
        var actions = getActions();
        assert actions != null; // the LTS view only exists with a simulator present
        result.add(actions.getSaveLTSAsAction());
        result.add(actions.getSaveStateAction());
        result.addMenuItems(super.createExportMenu());
        return result;
    }

    /** Creates a state exploration sub-menu. */
    public JMenu createExploreMenu() {
        JMenu result = new JMenu("Explore");
        var actions = getActions();
        assert actions != null; // the LTS view only exists with a simulator present
        result.add(actions.getExplorationDialogAction());
        result.add(actions.getApplyMatchAction());
        result.add(actions.getExploreAction());
        result.addSeparator();
        result.add(getCheckerMenu());
        return result;
    }

    /** Creates a traversal sub-menu. */
    public JMenu createGotoMenu() {
        JMenu result = new JMenu("Go To");
        var actions = getActions();
        assert actions != null; // the LTS view only exists with a simulator present
        result.add(actions.getGotoStartStateAction());
        result.add(actions.getGotoFinalStateAction());
        result.add(getScrollToActiveAction());
        return result;
    }

    /**
     * Lazily creates and returns the model-checking menu.
     */
    private JMenu getCheckerMenu() {
        var result = this.checkerMenu;
        if (result == null) {
            var simulator = getSimulator();
            assert simulator != null; // the LTS view only exists with a simulator present
            this.checkerMenu = result = new ModelCheckingMenu(simulator);
        }
        return result;
    }

    /** The lazily created model-checking menu. */
    private @Nullable JMenu checkerMenu;

    /** Initialises and returns the action to scroll to the active state or transition. */
    private Action getScrollToActiveAction() {
        if (getActiveTransition() == null) {
            this.scrollToActiveAction.setState(getActiveState());
        } else {
            this.scrollToActiveAction.setTransition(getActiveTransition());
        }
        return this.scrollToActiveAction;
    }

    /**
     * Action to scroll the graph view to the current state or derivation.
     */
    private final ScrollToActiveAction scrollToActiveAction = new ScrollToActiveAction(this);

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

    /** Indicates if system properties should be shown on states. */
    public boolean isShowSystemProperties() {
        return getOptionValue(Options.SHOW_SYSTEM_STATE_PROPERTIES_OPTION);
    }

    /** Indicates if invariants should be shown on states. */
    public boolean isShowInvariants() {
        return getOptionValue(Options.SHOW_INVARIANTS_OPTION);
    }

    /** Indicates if absent states should be shown. */
    public boolean isShowAbsentStates() {
        return getOptionValue(Options.SHOW_ABSENT_STATES_OPTION);
    }

    /** Indicates if in-recipe states and transitions should be shown. */
    public boolean isShowRecipeSteps() {
        return getOptionValue(Options.SHOW_RECIPE_STEPS_OPTION);
    }

    /** Returns the class of transitions that is currently being shown in the LTS. */
    public Claz getTransitionClass() {
        return Claz.getClass(isShowRecipeSteps(), isShowAbsentStates());
    }

    /** Scrolls the graph view to the active transition or state. */
    public void scrollToActive() {
        Element elem = getActiveTransition();
        if (elem == null) {
            elem = getActiveState();
        }
        if (elem != null) {
            getCanvas().scrollTo(elem);
        }
    }

    /**
     * Returns the active transition of the LTS, if any. The active transition
     * is the one currently selected in the simulator. Returns <tt>null</tt> if
     * no transition is selected.
     */
    public @Nullable GraphTransition getActiveTransition() {
        return this.activeTransition;
    }

    /**
     * The currently active transition of the LTS. The source node of
     * emphasizedEdge (if non-null) is also emphasized. Is null if there is no
     * currently emphasized edge.
     */
    private @Nullable GraphTransition activeTransition;

    /**
     * Returns the active state of the LTS, if any. The active state is the
     * one currently displayed in the state frame. Returns <tt>null</tt> if no
     * state is active (which should occur only if no grammar is loaded and
     * hence the LTS is empty).
     */
    public @Nullable GraphState getActiveState() {
        return this.activeState;
    }

    /**
     * The active state of the LTS. Is null if there is no active state.
     */
    private @Nullable GraphState activeState;

    /** Resets the active state and transition to {@code null},
     * without updating the display. */
    public void resetActive() {
        this.activeState = null;
        this.activeTransition = null;
    }

    /** Resets the active state and transition.
     * @return {@code true} if states were added (necessitating a re-layout).
     */
    public boolean refreshActive() {
        return setActive(getActiveState(), getActiveTransition());
    }

    /**
     * Sets the active state and transition to a new value. Both old and new
     * values may be <tt>null</tt>.
     * @param activeState the new active state
     * @param activeTrans the new active transition
     * @return {@code true} if states were added (necessitating a re-layout).
     */
    public boolean setActive(@Nullable GraphState activeState,
                             @Nullable GraphTransition activeTrans) {
        boolean result = false;
        List<ViewCell<GTS>> activeCells = new ArrayList<>();
        List<ViewCell<GTS>> changedCells = new ArrayList<>();
        GraphTransition oldActiveTrans = getActiveTransition();
        this.activeTransition = activeTrans;
        if (oldActiveTrans != null) {
            for (LTSViewCell jCell : getTransitionCells(oldActiveTrans)) {
                if (jCell.setActive(false)) {
                    changedCells.add(jCell);
                }
            }
        }
        if (activeTrans != null) {
            for (LTSViewCell jCell : getTransitionCells(activeTrans)) {
                if (jCell.getVisuals().isVisible()) {
                    activeCells.add(jCell);
                }
                if (jCell.setActive(true)) {
                    changedCells.add(jCell);
                }
            }
        }
        var model = getCanvas().getNonNullViewModel();
        GraphState oldActiveState = this.activeState;
        this.activeState = activeState;
        if (oldActiveState != null) {
            LTSViewCell jCell = (LTSViewCell) model.getJCellForNode(oldActiveState);
            if (jCell != null && jCell.setActive(false)) {
                changedCells.add(jCell);
            }
        }
        if (activeState != null && getCanvas().getViewModel() != null) {
            LTSViewCell jCell = (LTSViewCell) model.getJCellForNode(activeState);
            if (jCell == null) {
                result = addToModel(activeState);
                jCell = (LTSViewCell) model.getJCellForNode(activeState);
            }
            if (jCell != null) {
                if (jCell.setActive(true)) {
                    changedCells.add(jCell);
                }
                if (jCell.getVisuals().isVisible()) {
                    activeCells.add(jCell);
                }
            }
        }
        if (!activeCells.isEmpty()) {
            getCanvas().select(activeCells);
        }
        if (!changedCells.isEmpty()) {
            getCanvas().refresh(changedCells, false);
        }
        return result;
    }

    private boolean addToModel(GraphState state) {
        var model = getCanvas().getNonNullViewModel();
        // add the state and its parents and successors to the model
        Set<GraphState> newStates = new HashSet<>();
        Set<GraphTransition> newTransitions = new HashSet<>();
        newStates.add(state);
        GraphState parent = state;
        while (parent instanceof GraphNextState ns) {
            GraphTransition in = ns.getInTransition();
            newTransitions.add(in);
            parent = in.source();
            if (model.getJCellForNode(parent) == null) {
                newStates.add(parent);
            }
        }
        for (GraphTransition trans : state.getTransitions(getTransitionClass())) {
            if (model.getJCellForEdge(trans) == null) {
                newTransitions.add(trans);
                newStates.add(trans.target());
            }
        }
        var canvas = getCanvas();
        int oldBound = canvas.setStateBound(Integer.MAX_VALUE);
        boolean result = canvas.addElements(newStates, newTransitions, false);
        canvas.setStateBound(oldBound);
        return result;
    }

    /**
     * Refreshes the active state and transition, if any.
     * This is necessary after reloading the LTS.
     */
    public void reactivate() {
        List<ViewCell<GTS>> activeCells = new ArrayList<>();
        GraphState activeState = getActiveState();
        var model = getCanvas().getNonNullViewModel();
        if (activeState != null) {
            LTSViewCell activeCell = (LTSViewCell) model.getJCellForNode(activeState);
            if (activeCell != null) {
                activeCell.setActive(true);
                activeCells.add(activeCell);
            }
        }
        GraphTransition activeTrans = getActiveTransition();
        if (activeTrans != null) {
            LTSViewCell activeCell = (LTSViewCell) model.getJCellForEdge(activeTrans);
            if (activeCell != null) {
                activeCell.setActive(true);
                activeCells.add(activeCell);
            }
        }
        if (!activeCells.isEmpty()) {
            getCanvas().select(activeCells);
            getCanvas().refresh(activeCells, false);
        }
    }

    /** Collects all cells for a given transition and its subtransitions. */
    private Collection<LTSViewCell> getTransitionCells(GraphTransition trans) {
        var model = getCanvas().getNonNullViewModel();
        Collection<LTSViewCell> result = new ArrayList<>();
        LTSViewCell jCell = (LTSViewCell) model.getJCellForEdge(trans);
        if (jCell != null) {
            result.add(jCell);
        }
        if (trans instanceof RecipeTransition) {
            for (RuleTransition subTrans : ((RecipeTransition) trans).getSteps()) {
                jCell = (LTSViewCell) model.getJCellForEdge(subTrans);
                if (jCell != null) {
                    result.add(jCell);
                }
                jCell = (LTSViewCell) model.getJCellForNode(subTrans.source());
                if (jCell != null) {
                    result.add(jCell);
                }
            }
        }
        return result;
    }

    /** Returns the traces to the given set of states from the start state. */
    public Set<GraphTransition> findTraces(Iterable<GraphState> states) {
        Set<GraphTransition> result = new HashSet<>();
        for (GraphState state : states) {
            while (state instanceof GraphNextState) {
                GraphTransition trans = ((GraphNextState) state).getInTransition();
                result.add(trans);
                state = trans.source();
            }
        }
        var simulatorModel = getSimulatorModel();
        assert simulatorModel != null; // traces are only computed from the simulator UI
        simulatorModel.setTrace(result);
        return result;
    }

    /** Convenience method to test if there is a non-empty result object. */
    private boolean hasResult() {
        var result = getResult();
        return result != null && !result.isEmpty();
    }

    /** Convenience method to return the result object from the simulator model, if any. */
    private @Nullable ExploreResult getResult() {
        var simulatorModel = getSimulatorModel();
        return simulatorModel == null
            ? null
            : simulatorModel.getExploreResult();
    }

    /** Convenience method to test whether a given state is included in the result object. */
    public boolean isResult(GraphState state) {
        ExploreResult result = getResult();
        return result != null && result.contains(state);
    }

    /** Convenience method to test whether a given transition is included in the result object. */
    public boolean isResult(GraphTransition trans) {
        ExploreResult result = getResult();
        return result != null && result.contains(trans);
    }

    /** Filters the LTS according to the current value of {@link #getFilter()}.
     * @return {@code true} if any cells were added (necessitating a relayout).
     */
    public boolean refreshFiltering() {
        boolean result = false;
        GTSFragment fragment;
        var exploreResult = getResult();
        if (getFilter() == Filter.RESULT && exploreResult != null && !exploreResult.isEmpty()) {
            fragment = exploreResult.toFragment(isShowRecipeSteps());
        } else {
            var lts = getCanvas().getNonNullViewModel().getGraph();
            assert lts != null; // filtering is invoked only while an LTS is shown
            fragment = lts.toFragment(getFilter() == Filter.NONE, isShowRecipeSteps());
        }
        var cells = getCanvas().getCells();
        // first make the vertices (in)visible,
        // as otherwise they may prevent the edges from becoming visible
        for (var cell : cells) {
            if (cell instanceof ViewVertex<?> jVertex && cell instanceof LTSViewCell ltsCell) {
                boolean visible = fragment.nodeSet().contains(jVertex.getNode());
                boolean thisChanged = ltsCell.setVisibleFlag(visible);
                result |= thisChanged & visible;
            }
        }
        // now change the visibility of the edges
        for (var cell : cells) {
            if (cell instanceof ViewEdge<?> jEdge && cell instanceof LTSViewCell ltsCell) {
                var visibleEdges = fragment.edgeSet();
                boolean visible = jEdge.getEdges().stream().anyMatch(visibleEdges::contains);
                boolean thisChanged = ltsCell.setVisibleFlag(visible);
                result |= thisChanged & visible;
            }
        }
        return result;
    }

    /** Sets the filtering value of the graph view to the given value. */
    public boolean setFilter(Filter filter) {
        boolean result = this.filter != filter;
        if (result) {
            this.filter = filter;
        }
        return result;
    }

    /** Returns the filtering value of the graph view. */
    public Filter getFilter() {
        return this.filter;
    }

    private Filter filter = Filter.NONE;

    /** Indicates if there are no states not added or invisible due to node bound or filter. */
    public boolean isComplete() {
        var model = getCanvas().getViewModel();
        boolean result = model != null && getFilter() != Filter.SPANNING;
        if (result) {
            assert model != null;
            if (getCanvas().getStateBound() < model.nodeCount()) {
                result = false;
            } else if (getFilter() == Filter.RESULT && getSimulatorModel() != null) {
                result = !hasResult();
            }
        }
        return result;
    }
}
