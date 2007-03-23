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
 * $Id: StateGenerator.java,v 1.2 2007-03-23 15:42:58 rensink Exp $
 */
package groove.lts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import groove.trans.Deriver;
import groove.trans.RuleApplication;
import groove.util.Reporter;
import groove.util.TransformIterator;

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
            Set<RuleApplication> derivations = getDeriver().getDerivations(state.getGraph());
            // if there are no rule applications, the state is final
            if (derivations.isEmpty()) {
                gts.setFinal(state);
            } else {
                Iterator<RuleApplication> derivationIter = derivations.iterator();
                do {
                    RuleApplication appl = derivationIter.next();
                    // to test if the eventual target is fresh, compare it 
                    // with the original derivation's target
                    GraphState realTarget = addTransition(appl);
                    if (appl.isTargetSet() && appl.getTarget() == realTarget && realTarget != state) {
                    	result.add(realTarget);
                    }
                } while (derivationIter.hasNext());
            }
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
            final Iterator<RuleApplication> derivationIter = getDeriver().getDerivationIter(state.getGraph());
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
     * if a symmetric one is found then that is taken as target state, and
     * the derivation is adjusted accordingly. If no symmetric state is found,
     * then a fresh target state is added.
     * The actual target state is returned as the result of the method.
     * @param appl the derivation underlying the transition to be added
     * @return the target state of the resulting transition
     * @require <tt>devon.dom() == state.getGraph()</tt>
     * @ensure <tt>state.containsOutTransition(new GraphTransition(devon.rule(), devon.match(), result))</tt>
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
        gts.addTransition((GraphState) appl.getSource(), appl, targetState);
        reporter.stop();
        return targetState;
    }

	/**
	 * Computes the target state of a rule application.
	 * The target state is added to the underlying GTS, after checking for already
	 * existing isomorphic states.
	 * @param appl the rule application from which the target state is
	 * to be extracted
	 */
	private GraphState computeTargetState(RuleApplication appl) {
		GraphState result = (GraphState) appl.getTarget();
        // see if isomorphic graph is already in the LTS
        // special case: source = target
        if (appl.getSource() != result) {
            GraphState isoState = gts.addState(result);
            if (isoState != null) {
                // the following line is to ensure the cache is cleared
                // even if the state is still used as the basis of another
            	result.dispose();
                result = isoState;
            }
        }
		return result;
	}

    /**
     * Returns the target of a given rule application, by trying to walk 
     * around three sides of a confluent diamond instead of computing the
     * target directly.
     */
    private GraphState getConfluentTarget(RuleApplication appl) {
        if (!NextStateDeriver.isUseDependencies() || !(appl instanceof AliasRuleApplication)) {
            return null;
        }
        GraphOutTransition prior = ((AliasRuleApplication) appl).getPrior();
        if (prior == null) {
            return null;
        }
        GraphState priorTarget = prior.target();
        if (!priorTarget.isClosed()) {
            return null;
        }
        GraphTransition prevTransition = (DerivedGraphState) appl.getSource();
        GraphState result = priorTarget.getNextState(prevTransition.getEvent());
        if (result != null) {
        	confluentDiamondCount++;
        }
        return result;
    }

	/**
	 * @return Returns the deriver, lazily creating it using 
	 * {@link #createDeriver()} if it has not been initialised at construction time.
	 */
	protected Deriver getDeriver() {
		if (deriver == null) {
			deriver = createDeriver();
		}
		return deriver;
	}
	
	/**
	 * Callback factory method.
	 * Creates a derivation strategy based on the rule system of the GTS.
	 */
	protected Deriver createDeriver() {
		return new NextStateDeriver(gts.ruleSystem().getRules());
	}

	/** The deriver strategy generating the rule productions. */
	private Deriver deriver;
	/** The underlying GTS. */
	private final GTS gts;
	
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
