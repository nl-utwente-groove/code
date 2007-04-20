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
 * $Id: StateGenerator.java,v 1.6 2007-04-20 15:12:27 rensink Exp $
 */
package groove.lts;

import groove.graph.GraphAdapter;
import groove.graph.GraphShape;
import groove.graph.Node;
import groove.trans.RuleApplication;
import groove.trans.RuleApplier;
import groove.trans.SystemRecord;
import groove.util.Reporter;
import groove.util.TransformIterator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Class providing functionality to generate new states in a GTS.
 * @author Arend Rensink
 * @version $Revision $
 */
public class StateGenerator {
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
    
	/**
	 * Constructs an explorer for a given GTS.
	 * @param gts the GTS for which the states are to be explored.
	 */
	public StateGenerator(final GTS gts) {
		this.gts = gts;
		this.record = new SystemRecord(gts.ruleSystem());
		this.collector = new FreshStateCollector();
		gts.addGraphListener(collector);
		applier = new AliasRuleApplier(record);
	}

	/** Returns the system record kept by this generator. */
	public final SystemRecord getRecord() {
		return record;
	}

	/**
	 * Computes and returns the set of successor states of a given state,
	 * insofar thay are not yet in the GTS at time of invocation.
	 * The states that are returned are added to the GTS first.
	 * @param state the state for which the successors are to be generated;
	 * assumed to be in the GTS
	 * @return the new states generated as a result of the invocation
	 */
    public Collection<? extends GraphState> computeSuccessors(GraphState state) {
        reporter.start(SUCC);
        final Collection<GraphState> result = new ArrayList<GraphState>();
        // check if the transitions have not yet been generated
        if (!state.isClosed()) {
        	collector.set(result);
        	getApplier(state).doApplications(new RuleApplier.Action() {
				public void perform(RuleApplication application) {
                    addTransition(application);
				}
        	});
            collector.reset();
            if (result.isEmpty()) {
                gts.setFinal(state);
            }
//            Set<RuleApplication> derivations = getApplier(state).getApplications();
//            // if there are no rule applications, the state is final
//            if (derivations.isEmpty()) {
//                gts.setFinal(state);
//            } else {
//            	collector.set(result);
//            	for (RuleApplication appl: derivations) {
//                    addTransition(appl);
//            	}
//                collector.reset();
//            }
            gts.setClosed(state);
        }
        reporter.stop();
        return result;
    }
    
    /**
	 * Generates the set of successor states of a given state and
	 * adds them to the underlying GTS; then returns the set of all
	 * successor states.
	 * @param state the state for which the successors are to be generated;
	 * assumed to be in the GTS
	 * @return the set of all successors of <code>state</code>
	 */
    public Collection<? extends GraphState> getSuccessors(GraphState state) {
        computeSuccessors(state);
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
                gts.setFinal(state);
            }
            // get the next states by adding transitions for the derivations
            return new TransformIterator<RuleApplication,GraphState>(derivationIter) {
            	@Override
                public boolean hasNext() {
                    if (hasNext) {
                        hasNext = super.hasNext();
                        if (!hasNext) {
                            gts.setClosed(state);
                        }
                    }
                    return hasNext;
                }

            	@Override
                protected GraphState toOuter(RuleApplication from) {
                    return addTransition(from);
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
     * @param appl the derivation underlying the transition to be added
     * @return the target state of the resulting transition
     */
    public GraphState addTransition(RuleApplication appl) {
        reporter.start(ADD_TRANSITION);
        reporter.start(ADD_TRANSITION_START);
        // check for confluent diamond
        GraphState targetState = getConfluentTarget(appl);
        reporter.stop();
        if (targetState == null) {
            // determine target state of this transition
            targetState = computeTargetState(appl);
        }
        gts.addTransition((GraphState) appl.getSource(), appl.getEvent(), targetState);
        reporter.stop();
        return targetState;
    }

	/**
	 * Computes the target state of a rule application.
	 * The target state is added to the underlying GTS, after checking for already
	 * existing isomorphic states.
	 * @param appl the provisional target from which the real target state is to be extracted
	 */
	private GraphState computeTargetState(RuleApplication appl) {
		GraphState result;
        // see if isomorphic graph is already in the LTS
        // special case: source = target
        if (appl.getRule().isModifying()) {
        	result = createState(appl);
            GraphState isoState = gts.addState(result);
            if (isoState != null) {
                // the following line is to ensure the cache is cleared
                // even if the state is still used as the basis of another
            	result.dispose();
                result = isoState;
            }
        } else {
        	result = (GraphState) appl.getSource();
        }
		return result;
	}

    /**
     * Returns the target of a given rule application, by trying to walk 
     * around three sides of a confluent diamond instead of computing the
     * target directly.
     */
    private GraphState getConfluentTarget(RuleApplication appl) {
        if (!AliasRuleApplier.isUseDependencies() || !(appl instanceof AliasRuleApplication)) {
            return null;
        }
        AliasRuleApplication aliasAppl = (AliasRuleApplication) appl;
        GraphOutTransition prior = aliasAppl.getPrior();
        GraphState priorTarget = prior.target();
        if (!priorTarget.isClosed()) {
        	// the prior target does not have its outgoing transitions computed yet
            return null;
        }
        GraphTransition prevTransition = (DerivedGraphState) aliasAppl.getSource();
        if (prior.getEvent().conflicts(prevTransition.getEvent())) {
        	// alternating the events does not imply confluence
            return null;
        }
        GraphState result = priorTarget.getNextState(prevTransition.getEvent());
        if (result != null) {
        	confluentDiamondCount++;
        }
        return result;
    }
//
//	/**
//	 * @return Returns the deriver, lazily creating it using 
//	 * {@link #createDeriver(GraphState)} if it has not been initialised at construction time.
//	 */
//	protected Deriver getDeriver() {
//		if (deriver == null) {
//			deriver = createDeriver(state);
//		}
//		return deriver;
//	}

	/**
	 * Creates a fresh graph state, based on a given rule application.
	 */
	private GraphNextState createState(RuleApplication appl) {
		DerivedGraphState result = new DerivedGraphState(appl);
		result.setFixed();
		return result;
	}

	/**
	 * Callback method to obtain a rule applier for this generator's rule set.
	 * This implementation uses flyweight, so discard the result before calling the method again.
	 */
	private RuleApplier getApplier(GraphState state) {
		applier.setState(state);
		return applier;
	}

	/** The underlying GTS. */
	private final GTS gts;
	/** The rule system instance used by this generator. */
	private final SystemRecord record;
	/** Collector instance that listens to the underlying GTS. */
	private final FreshStateCollector collector;
	/** The fixed rule applier for this generator. */
	private final AliasRuleApplier applier;
	/**
	 * Listener that collects the fresh states into a set.
	 */
	static class FreshStateCollector extends GraphAdapter {
		/**
		 * Sets the result set to an alial of a given set.
		 */
		public void set(Collection<GraphState> result){
			this.result = result;
		}
		
		/**
		 * Sets the result set to the empty set.
		 */
		public void reset() {
			result = null;
		}
		
		@Override
		public void addUpdate(GraphShape graph, Node node) {
			if (result != null) {
				result.add((GraphState) node);
			}
		}
		
		/** The set to collect the fresh states. */
		private Collection<GraphState> result;
	}
	
    /** Reporter for profiling information; aliased to {@link GTS#reporter}. */
    static private final Reporter reporter = new Reporter(StateGenerator.class);
    /** Profiling aid for adding states. */
    static public final int ADD_STATE = reporter.newMethod("addState");
    /** Profiling aid for adding transitions. */
    static public final int ADD_TRANSITION = reporter.newMethod("addTransition");
    /** Profiling aid for adding transitions. */
    static public final int ADD_TRANSITION_START = reporter.newMethod("addTransition - start");
    /** Profiling aid for adding transitions. */
    static private final int SUCC = reporter.newMethod("computing successors");
}
