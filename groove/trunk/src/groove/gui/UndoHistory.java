/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
 * $Id: UndoHistory.java,v 1.8 2008-01-30 09:33:36 iovka Exp $
 */
package groove.gui;

import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.State;
import groove.lts.Transition;
import groove.trans.NameLabel;
import groove.trans.RuleMatch;
import groove.util.Groove;
import groove.util.History;
import groove.view.DefaultGrammarView;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 * Manager of the undo history.
 * @version $Revision: 1.8 $ $Date: 2008-01-30 09:33:36 $
 * @author Arend Rensink
 */
class UndoHistory implements SimulationListener {
    /**
     * Creates a new, empty history log and 
     * registers this undo history as a simulation listener.
     * @param simulator the "parent" simulator
     */
    public UndoHistory(Simulator simulator) {
        //simulator.addSimulationListener(this);
        this.history = new History<HistoryAction>();
        this.simulator = simulator;
        simulator.addSimulationListener(this);
        this.undoAction = new UndoAction();
        this.redoAction = new RedoAction();
    }

    /**
     * Returns the (unique) redo action associated with this undo history.
     */
    public Action getRedoAction() {
        return redoAction;
    }

    /**
     * Returns the (unique) undo action associated with this undo history.
     */
    public Action getUndoAction() {
        return undoAction;
    }

    /**
     * Clears the history.
     */
    public synchronized void setGrammarUpdate(DefaultGrammarView grammar) {
        history.clear();
		setActionEnablings();
		ignoreSimulationUpdates = false;
    }

    /** Sets the start state. */
    public synchronized void startSimulationUpdate(GTS gts) {
		history.add(new SetStateAction(gts.startState()));
		setActionEnablings();
		ignoreSimulationUpdates = false;
	}

	/**
	 * Adds a state to the history, if it is not already the current element.
	 */    
    public synchronized void setStateUpdate(GraphState state) {
        if (! ignoreSimulationUpdates) {
        	HistoryAction newAction = new SetStateAction(state);
        	if (history.isEmpty() || !state.equals(history.current().getState())) {
        		history.add(newAction);
        	} else {
        		history.replace(newAction);
        	}
        }
        setActionEnablings();
    }

    /**
     * Adds the newly selected transition to the history.
     * If the current history element is selecting the source state of
     * the transition, remove it (so we don't get the selection of a state
     * followed by the selection of an outgoing transition, but only the latter).
     * @see #applyTransitionUpdate(GraphTransition)
     */
    public synchronized void setTransitionUpdate(GraphTransition transition) {
        if (! ignoreSimulationUpdates) {
        	HistoryAction newAction = new SetTransitionAction(transition);
        	// test if the previous history action was setting the source state of this transition
        	if (history.isEmpty() || !transition.source().equals(history.current().getState())) {
        		history.add(newAction);
        	} else {
        		history.replace(newAction);
        	}
        }
        setActionEnablings();
    }

    /** Match updates have no effect on the history. */
    public void setMatchUpdate(RuleMatch match) {
        // explicitly empty
    }

    /** Rule updates have no effect on the history. */
    public void setRuleUpdate(NameLabel name) { 
        // explicitly empty
    }

    /**
     * Adds the applied transition and its target state to the history.
     */
    public void applyTransitionUpdate(GraphTransition transition) {
    	setStateUpdate(transition.target());
//        history.add(new TransitionElement(transition));
//        history.add(transition.target());
//        setActionEnablings();
    }

    /**
     * Action to set the current state forward to the next one in the history.
     * (May in due time be replaced by an UndoableEditManager.)
     */
    private class RedoAction extends AbstractAction {
    	/** Creates an instance of the redo action. */
        RedoAction() {
            super(Options.REDO_ACTION_NAME);
            putValue(ACCELERATOR_KEY, Options.REDO_KEY);
        }

        public void actionPerformed(ActionEvent evt) {
            if (history.hasNext()) {
                ignoreSimulationUpdates = true;
                HistoryAction next = history.next();
                if (DEBUG)
                    Groove.message("Redo: restore "+next);
                doHistoryAction(next);
                ignoreSimulationUpdates = false;
            }
        }
    }

    /**
     * Action to set the current state back to the previous one in the history.
     * (May in due time be replaced by an UndoableEditManager.)
     */
    private class UndoAction extends AbstractAction {
    	/** Creates an instance of the undo action. */
        UndoAction() {
            super(Options.UNDO_ACTION_NAME);
            putValue(ACCELERATOR_KEY, Options.UNDO_KEY);
        }
        
        public void actionPerformed(ActionEvent evt) {
            if (history.hasPrevious()) {
                ignoreSimulationUpdates = true;
                HistoryAction previous = history.previous();
                if (DEBUG)
                    Groove.message("Undo: restore "+previous);
                doHistoryAction(previous);
                ignoreSimulationUpdates = false;
            }
        }
    }

    /** 
     * Does the action encoded in a given history element.
     * This means either setting a state or a transition in the simulator.
     * @param action the history action to perform
     */
    protected void doHistoryAction(HistoryAction action) {
        if (action instanceof SetStateAction) {
            simulator.setState(action.getState());
        } else {
            assert action instanceof SetTransitionAction;
            simulator.setTransition(action.getTransition());
        }
    }
    
    /**
     * Enables this undo history's undo and redo action,
     * based on the state of the history log.
     * @see #getRedoAction()
     * @see #getUndoAction()
     */
    protected void setActionEnablings() {
        undoAction.setEnabled(history.hasPrevious());
        redoAction.setEnabled(history.hasNext());
    }

    /**
     * The parent simulator.
     * @invariant simulator != null
     */
    protected final Simulator simulator;
    /**
     * The (unique) undo action associated with this undo history.
     */
    protected final UndoAction undoAction;
    /**
     * The (unique) redo action associated with this undo history.
     */
    protected final RedoAction redoAction;
    /**
     * The history log. 
     * The log consists of at least one sub-sequence of the form
     * <tt>(State Transition)^* State</tt>
     * where the transitions are between their predecessor and successor states.
     * @invariant history.size() > 0
     */
    protected final History<HistoryAction> history;
    /**
     * Indicator whether simulation updates should currently be ignored.
     * Is set to true if the change is instigated by our un/redo actions.
     */
    protected boolean ignoreSimulationUpdates = true;

    /** 
     * Class to record history elements without having to rely
     * on the distinction between {@link State}s and {@link Transition}s.
     */
    private abstract class HistoryAction {
    	/** 
    	 * Creates an action that consists of either setting
    	 * a state or a transition in the simulator.
    	 * @param state the state set in the action; <code>null</code> if the action is setting a transition
    	 * @param transition the transition set in the action; <code>null</code> if the action is setting a state
    	 */
    	public HistoryAction(final GraphState state, final GraphTransition transition) {
			this.transition = transition;
			if (state == null) {
				this.state = transition.source();
			} else {
				this.state = state;
			}
		}

		/** Returns either the state stored in this action, or the source state of the transition. */
    	GraphState getState() {
    		 return state;
    	 }
    	
    	/** Returns the transition stored in this action, if any, or <code>null</code> otherwise. */
    	GraphTransition getTransition() {
    		return transition;
    	}
		
		/**
		 * This implementation compares actions on the basis of their stored
		 * <code>state</code> and <code>transition</code> components.
		 */
		@Override
		public boolean equals(Object obj) {
			return obj instanceof HistoryAction && stateEquals((HistoryAction) obj) && transitionEquals((HistoryAction) obj);
		}

		/** Compares the stored <code>state</code> components. */
		protected boolean stateEquals(HistoryAction other) {
			if (state == null) {
				return other.state == null;
			} else {
				return state.equals(other.state);
			}
		}

		/** Compares the stored <code>transition</code> components. */
		protected boolean transitionEquals(HistoryAction other) {
			if (transition == null) {
				return other.transition == null;
			} else {
				return transition.equals(other.transition);
			}
		}

		/** 
		 * The state stored in this history action; may be <code>null</code> if the
		 * action is setting a transition.
		 */ 
    	private final GraphState state;
		/** 
		 * The transition stored in this history action; may be <code>null</code> if the
		 * action is setting a state.
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


