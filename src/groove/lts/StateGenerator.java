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
 * $Id: StateGenerator.java,v 1.12 2007-05-29 13:32:19 rensink Exp $
 */
package groove.lts;

import groove.graph.Edge;
import groove.graph.GraphShape;
import groove.trans.RuleApplication;
import groove.trans.RuleApplier;
import groove.trans.RuleEvent;
import groove.trans.SystemRecord;
import groove.util.Reporter;
import groove.util.TransformIterator;

import java.util.Collection;
import java.util.Iterator;

/**
 * Class providing functionality to generate new states in a GTS.
 * @author Arend Rensink
 * @version $Revision $
 */
public class StateGenerator {
	/** Constructs a state generator with no GTS or system record set. */
	public StateGenerator() {
		this.collector = new AddTransitionListener();
	}
	
	/**
	 * Returns the underlying GTS.
	 */
	public GTS getGTS() {
		return this.gts;
	}

	/**
	 * Sets a new GTS, and resets the system record.
	 */
	public void setGTS(GTS gts) {
		this.gts = gts;
		this.collector.setGTS(gts);
		this.applier = null;
	}

	/** Convenience method to retrieve the record from the current GTS. */
	protected final SystemRecord getRecord() {
		return getGTS().getRecord();
	}

	/**
	 * Computes and returns the set of successor states of a given state,
	 * insofar thay are not yet in the GTS at time of invocation.
	 * The states that are returned are added to the GTS first.
	 * @param state the state for which the successors are to be generated;
	 * assumed to be in the GTS
	 */
    public void explore(final GraphState state) {
        reporter.start(SUCC);
        // check if the transitions have not yet been generated
        if (!state.isClosed()) {
            collector.reset();
        	getApplier(state).doApplications(new RuleApplier.Action() {
				public void perform(RuleApplication application) {
                    addTransition(state, application);
				}
        	});
            if (! collector.isTransitionsAdded()) {
                getGTS().setFinal(state);
            }
            getGTS().setClosed(state);
        }
        reporter.stop();
    }
    
    /**
	 * Generates the set of successor states of a given state and
	 * adds them to the underlying GTS; then returns the set of all
	 * successor states.
	 * @param state the state for which the successors are to be generated;
	 * assumed to be in the GTS
	 * @return the set of all successors of <code>state</code>
	 */
    public Collection<GraphState> getSuccessors(GraphState state) {
        explore(state);
        return state.getNextStateSet();
    }

	/**
	 * Returns an iterator over all successor states of a given state,
	 * generating the states and adding them to the GTS if necessary.
	 * @param state the state for which the successors are to be generated;
	 * assumed to be in the GTS
	 * @return a lazy iterator over all successors of <code>state</code>
	 */
    public Iterator<? extends GraphState> getSuccessorIter(final GraphState state) {
        if (state.isClosed()) {
            // get the next states from the outgoing edges
            return state.getNextStateIter();
        } else {
            final Iterator<RuleApplication> derivationIter = getApplier(state).getApplicationIter();
            if (!derivationIter.hasNext()) {
            	getGTS().setFinal(state);
            }
            // get the next states by adding transitions for the derivations
            return new TransformIterator<RuleApplication,GraphState>(derivationIter) {
            	@Override
                public boolean hasNext() {
                    if (hasNext) {
                        hasNext = super.hasNext();
                        if (!hasNext) {
                        	getGTS().setClosed(state);
                        }
                    }
                    return hasNext;
                }

            	@Override
                protected GraphState toOuter(RuleApplication from) {
                    return addTransition(state, from);
                }
                
                private boolean hasNext = true;
            };
        }
    }
    
    /**
     * Adds a transition to the GTS, constructed from a given rule application.
     * The application's target graph is compared to the existing states for symmetry;
     * if a symmetric one is found then that is taken as target state. 
     * If no symmetric state is found, then a fresh target state is added.
     * The actual target state is returned as the result of the method.
     * @param source the source state of the new transition
     * @param appl the derivation underlying the transition to be added
     * @return the target state of the resulting transition
     */
    public GraphState addTransition(GraphState source, RuleApplication appl) {
        reporter.start(ADD_TRANSITION);
        GraphTransition result;
        if (!appl.getRule().isModifying()) {
            result = createTransition(appl, source, source, false);
        } else {
            // check for confluent diamond
            GraphState confluentTarget = getConfluentTarget(source, appl);
            if (confluentTarget == null) {
                GraphNextState freshTarget = createState(appl, source);
                GraphState isoTarget = getGTS().addState(freshTarget);
                if (isoTarget == null) {
                    result = freshTarget;
                } else {
                    // the following line is to ensure the cache is cleared
                    // even if the state is still used as the basis of another
                    // result.dispose();
                    result = createTransition(appl, source, isoTarget, true);
                }
            } else {
                result = createTransition(appl, source, confluentTarget, false);
            }
        }
        getGTS().addTransition(result);
        reporter.stop();
        return result.target();
    }
    
//
//	/**
//	 * Computes the target state of a rule application.
//	 * The target state is added to the underlying GTS, after checking for already
//	 * existing isomorphic states.
//	 * @param source the source state of the rule application
//	 * @param appl the provisional target from which the real target state is to be extracted
//	 */
//	private GraphTransition computeTargetState(GraphState source, RuleApplication appl) {
//		GraphTransition result;
//        // see if isomorphic graph is already in the LTS
//        // special case: source = target
//        if (appl.getRule().isModifying()) {
//        	GraphNextState target = createState(source, appl);
//            GraphState isoState = gts.addState(target);
//            if (isoState == null) {
//            	result = target;
//            } else {
//                // the following line is to ensure the cache is cleared
//                // even if the state is still used as the basis of another
////            	result.dispose();
//            	result = createTransition(appl.getEvent(), source, target, false);
//            }
//        } else {
//        	result = createTransition(appl.getEvent(), source, source, true);
//        }
//		return result;
//	}

    /**
     * Returns the target of a given rule application, by trying to walk 
     * around three sides of a confluent diamond instead of computing the
     * target directly.
     * @param source the source state of the fourth side of the (prospective) diamond
     * @param appl the rule application (applied to <code>source.getGraph()</code>)
     * @return the target state; <code>null</code> if no confluent diamond was found
     */
    private GraphState getConfluentTarget(GraphState source, RuleApplication appl) {
        if (!AliasRuleApplier.isUseDependencies() || !(appl instanceof AliasRuleApplication)) {
            return null;
        }
        assert source instanceof GraphNextState;
        AliasRuleApplication aliasAppl = (AliasRuleApplication) appl;
        GraphTransitionStub prior = aliasAppl.getPrior();
        if (prior.isSymmetry()) {
        	return null;
        }
//        if (!priorTarget.isClosed()) {
//        	// the prior target does not have its outgoing transitions computed yet
//            return null;
//        }
        RuleEvent sourceEvent = ((GraphNextState) source).getEvent();
        if (aliasAppl.getEvent().conflicts(sourceEvent)) {
        	// alternating the events does not imply confluence
            return null;
        }
        GraphState result = prior.target().getNextState(sourceEvent);
        if (result != null) {
        	confluentDiamondCount++;
        }
        return result;
    }

	/**
	 * Creates a fresh graph state, based on a given rule application and source state.
	 */
	protected GraphNextState createState(RuleApplication appl, GraphState source) {
		return new DefaultGraphNextState((AbstractGraphState) source, appl.getEvent(), appl.getCoanchorImage());
	}

    /**
     * Creates a fresh graph transition, based on a given rule application and source state.
     */
    protected GraphTransition createTransition(RuleApplication appl, GraphState source) {
        return createTransition(appl, source, createState(appl, source), false);
    }
    
    /**
     * Creates a fresh graph transition, based on a given rule application and source and target state.
     * A final parameter determines if the target state is directly derived from the source, or modulo a symmetry.
     */
    protected GraphTransition createTransition(RuleApplication appl, GraphState source, GraphState target, boolean symmetry) {
        return new DefaultGraphTransition(appl.getEvent(), appl.getCoanchorImage(), source, target, symmetry);
    }
    
	/**
	 * Callback method to obtain a rule applier for this generator's rule set.
	 * This implementation uses flyweight, so discard the result before calling the method again.
	 */
	protected RuleApplier getApplier(GraphState state) {
		if (applier == null) {
			applier = new AliasRuleApplier(getRecord(), state);
		} else {
			applier.setState(state);
		}
		return applier;
	}

	/** The underlying GTS. */
	private GTS gts;
	/** Collector instance that listens to the underlying GTS. */
	private final AddTransitionListener collector;
	/** The fixed rule applier for this generator. */
	private AliasRuleApplier applier;
	/**
	 * The number of confluent diamonds found.
	 */
	private static int confluentDiamondCount;
	/**
	 * Returns the number of confluent diamonds found during generation.
	 */
	public static int getConfluentDiamondCount() {
	    return confluentDiamondCount;
	}

	/**
	 * Returns the time spent generating successors.
	 */
	public static long getGenerateTime() {
	    return reporter.getTotalTime(ADD_TRANSITION);
	}

	/** Reporter for profiling information; aliased to {@link GTS#reporter}. */
    static private final Reporter reporter = Reporter.register(StateGenerator.class);
    /** Profiling aid for adding states. */
    static public final int ADD_STATE = reporter.newMethod("addState");
    /** Profiling aid for adding transitions. */
    static public final int ADD_TRANSITION = reporter.newMethod("addTransition");
    /** Profiling aid for adding transitions. */
    static public final int ADD_TRANSITION_START = reporter.newMethod("addTransition - start");
    /** Profiling aid for adding transitions. */
    static private final int SUCC = reporter.newMethod("computing successors");
	/**
	 * Listener that collects the fresh states into a set.
	 */
	abstract static protected class GTSListener extends LTSAdapter {
		/** 
		 * Sets or changes the GTS to which the listener is listening.
		 * @param gts the new GTS to which the listener should listen; may be <code>null</code> 
		 */
		public void setGTS(GTS gts) {
			if (this.gts != null) {
				this.gts.removeGraphListener(this);
			}
			this.gts = gts;
			if (this.gts != null) {
				this.gts.addGraphListener(this);
			}
		}
		
		/** GTS to which the listener is currently listening. */
		private GTS gts;
	}
	
	/**
	 * Listener that collects the fresh states into a set.
	 */
	static private class AddTransitionListener extends GTSListener {
		/**
		 * Sets the result set to the empty set.
		 */
		public void reset() {
			transitionsAdded = false;
		}
		
		/** 
		 * Indicates if any transitions were added since {@link #reset()}
		 * was last called.
		 * @return <code>true</code> if any transitions were added
		 */
		public boolean isTransitionsAdded() {
			return transitionsAdded;
		}
		
		@Override
		public void addUpdate(GraphShape graph, Edge edge) {
			transitionsAdded = true;
		}
		
		/** 
		 * Variable that records if any transition have been added since the last
		 * {@link #reset()}.
		 */
		private boolean transitionsAdded;
	}
}
