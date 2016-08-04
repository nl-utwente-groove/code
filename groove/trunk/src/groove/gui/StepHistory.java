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
 * $Id$
 */
package groove.gui;

import java.awt.event.ActionEvent;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;

import groove.gui.SimulatorModel.Change;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.RuleTransition;
import groove.util.Groove;
import groove.util.History;

/**
 * History of simulation steps.
 * @version $Revision$ $Date: 2008-01-30 09:33:36 $
 * @author Arend Rensink
 */
public class StepHistory implements SimulatorListener {
    /**
     * Creates a new, empty history log and registers this undo history as a
     * simulation listener.
     * @param simulator the "parent" simulator
     */
    public StepHistory(Simulator simulator) {
        this.history = new History<>();
        this.simulator = simulator;
        this.simulatorModel = simulator.getModel();
        this.undoAction = new BackAction();
        this.redoAction = new ForwardAction();
        this.simulatorModel.addListener(this, Change.GTS, Change.STATE, Change.MATCH);
    }

    /**
     * Returns the (unique) redo action associated with this undo history.
     */
    public Action getForwardAction() {
        return this.redoAction;
    }

    /**
     * Returns the (unique) undo action associated with this undo history.
     */
    public Action getBackAction() {
        return this.undoAction;
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel, Set<Change> changes) {
        if (changes.contains(Change.GTS) && source.getGTS() != oldModel.getGTS()) {
            this.history.clear();
            refreshActions();
        }
        if (changes.contains(Change.STATE) && source.hasState()) {
            setStateUpdate(source.getState());
        }
        if (changes.contains(Change.MATCH) && source.hasTransition()) {
            setTransitionUpdate(source.getTransition());
        }
    }

    /**
     * Adds a state to the history, if it is not already the current element.
     */
    private synchronized void setStateUpdate(GraphState state) {
        if (!this.ignoreSimulationUpdates) {
            HistoryAction newAction = new SetStateAction(state);
            if (this.history.isEmpty() || !state.equals(this.history.current()
                .getState())) {
                this.history.add(newAction);
            } else {
                this.history.replace(newAction);
            }
        }
        refreshActions();
    }

    /**
     * Adds the newly selected transition to the history. If the current history
     * element is selecting the source state of the transition, remove it (so we
     * don't get the selection of a state followed by the selection of an
     * outgoing transition, but only the latter).
     */
    private synchronized void setTransitionUpdate(GraphTransition transition) {
        if (!this.ignoreSimulationUpdates) {
            HistoryAction newAction = new SetTransitionAction(transition);
            // test if the previous history action was setting the source state
            // of this transition
            if (this.history.isEmpty() || !transition.source()
                .equals(this.history.current()
                    .getState())) {
                this.history.add(newAction);
            } else {
                this.history.replace(newAction);
            }
        }
        refreshActions();
    }

    /**
     * Action to set the current state forward to the next one in the history.
     * (May in due time be replaced by an UndoableEditManager.)
     */
    private class ForwardAction extends AbstractAction {
        /** Creates an instance of the redo action. */
        ForwardAction() {
            super(Options.FORWARD_ACTION_NAME, Icons.ARROW_SIMPLE_RIGHT_ICON);
            putValue(ACCELERATOR_KEY, Options.FORWARD_KEY);
            putValue(SHORT_DESCRIPTION, Options.FORWARD_ACTION_NAME);
            StepHistory.this.simulator.addAccelerator(this);
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            if (StepHistory.this.history.hasNext()) {
                StepHistory.this.ignoreSimulationUpdates = true;
                HistoryAction next = StepHistory.this.history.next();
                if (DEBUG) {
                    Groove.message("Redo: restore " + next);
                }
                doHistoryAction(next);
                StepHistory.this.ignoreSimulationUpdates = false;
            }
        }
    }

    /**
     * Action to set the current state back to the previous one in the history.
     * (May in due time be replaced by an UndoableEditManager.)
     */
    private class BackAction extends AbstractAction {
        /** Creates an instance of the undo action. */
        BackAction() {
            super(Options.BACK_ACTION_NAME, Icons.ARROW_SIMPLE_LEFT_ICON);
            putValue(ACCELERATOR_KEY, Options.BACK_KEY);
            putValue(SHORT_DESCRIPTION, Options.BACK_ACTION_NAME);
            StepHistory.this.simulator.addAccelerator(this);
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            if (StepHistory.this.history.hasPrevious()) {
                StepHistory.this.ignoreSimulationUpdates = true;
                HistoryAction previous = StepHistory.this.history.previous();
                if (DEBUG) {
                    Groove.message("Undo: restore " + previous);
                }
                doHistoryAction(previous);
                StepHistory.this.ignoreSimulationUpdates = false;
            }
        }
    }

    /**
     * Does the action encoded in a given history element. This means either
     * setting a state or a transition in the simulator.
     * @param action the history action to perform
     */
    protected void doHistoryAction(HistoryAction action) {
        if (action instanceof SetStateAction) {
            this.simulatorModel.setState(action.getState());
        } else {
            assert action instanceof SetTransitionAction;
            this.simulatorModel.setTransition(action.getTransition());
        }
    }

    /**
     * Enables this undo history's undo and redo action, based on the state of
     * the history log.
     * @see #getForwardAction()
     * @see #getBackAction()
     */
    private void refreshActions() {
        this.undoAction.setEnabled(this.history.hasPrevious());
        this.redoAction.setEnabled(this.history.hasNext());
    }

    private final Simulator simulator;
    /**
     * The parent simulator.
     * @invariant simulator != null
     */
    private final SimulatorModel simulatorModel;
    /**
     * The (unique) undo action associated with this undo history.
     */
    protected final BackAction undoAction;
    /**
     * The (unique) redo action associated with this undo history.
     */
    protected final ForwardAction redoAction;
    /**
     * The history log. The log consists of at least one sub-sequence of the
     * form <tt>(State Transition)^* State</tt> where the transitions are
     * between their predecessor and successor states.
     * @invariant history.size() > 0
     */
    protected final History<HistoryAction> history;
    /**
     * Indicator whether simulation updates should currently be ignored. Is set
     * to true if the change is instigated by our un/redo actions.
     */
    protected boolean ignoreSimulationUpdates;

    /**
     * Class to record history elements without having to rely on the
     * distinction between {@link GraphState}s and {@link RuleTransition}s.
     */
    private static abstract class HistoryAction {
        /**
         * Creates an action that consists of either setting a state or a
         * transition in the simulator.
         * @param state the state set in the action; <code>null</code> if the
         *        action is setting a transition
         * @param transition the transition set in the action; <code>null</code>
         *        if the action is setting a state
         */
        public HistoryAction(final GraphState state, final GraphTransition transition) {
            this.transition = transition;
            if (state == null) {
                this.state = transition.source();
            } else {
                this.state = state;
            }
        }

        /**
         * Returns either the state stored in this action, or the source state
         * of the transition.
         */
        GraphState getState() {
            return this.state;
        }

        /**
         * Returns the transition stored in this action, if any, or
         * <code>null</code> otherwise.
         */
        GraphTransition getTransition() {
            return this.transition;
        }

        @Override
        public int hashCode() {
            return this.state == null ? 0 : this.state.hashCode();
        }

        /**
         * This implementation compares actions on the basis of their stored
         * <code>state</code> and <code>transition</code> components.
         */
        @Override
        public boolean equals(Object obj) {
            return obj instanceof HistoryAction && stateEquals((HistoryAction) obj)
                && transitionEquals((HistoryAction) obj);
        }

        /** Compares the stored <code>state</code> components. */
        protected boolean stateEquals(HistoryAction other) {
            if (this.state == null) {
                return other.state == null;
            } else {
                return this.state.equals(other.state);
            }
        }

        /** Compares the stored <code>transition</code> components. */
        protected boolean transitionEquals(HistoryAction other) {
            if (this.transition == null) {
                return other.transition == null;
            } else {
                return this.transition.equals(other.transition);
            }
        }

        /**
         * The state stored in this history action; may be <code>null</code> if
         * the action is setting a transition.
         */
        private final GraphState state;
        /**
         * The transition stored in this history action; may be
         * <code>null</code> if the action is setting a state.
         */
        private final GraphTransition transition;
    }

    /** Action that records setting a state. */
    private class SetStateAction extends HistoryAction {
        /** Constructs an instance for a given graph state. */
        public SetStateAction(final GraphState state) {
            super(state, null);
        }
    }

    /** Action that records setting a transition. */
    private class SetTransitionAction extends HistoryAction {
        /** Constructs an instance for a given graph transition. */
        public SetTransitionAction(final GraphTransition transition) {
            super(null, transition);
        }
    }

    /** Debug flag. */
    private static final boolean DEBUG = false;
}
