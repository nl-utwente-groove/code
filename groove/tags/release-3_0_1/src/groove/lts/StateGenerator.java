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
 * $Id: StateGenerator.java,v 1.32 2008/03/04 14:48:00 kastenberg Exp $
 */
package groove.lts;

import groove.control.Location;
import groove.explore.DefaultScenario;
import groove.explore.util.ExploreCache;
import groove.trans.Rule;
import groove.trans.RuleApplication;
import groove.trans.RuleEvent;
import groove.trans.RuleMatch;
import groove.trans.SystemRecord;
import groove.util.Reporter;
import groove.verify.BuchiGraphState;
import groove.verify.BuchiLocation;
import groove.verify.ModelChecking;

import java.util.HashSet;
import java.util.Set;

/**
 * Class providing functionality to generate new states in a GTS.
 * @author Arend Rensink
 * @version $Revision: 1.32 $
 */
public class StateGenerator {
	/** Constructs a state generator with no GTS or system record set. */
	public StateGenerator() {
//		this.collector = new AddTransitionListener();
	}

	public StateGenerator(GTS gts) {
		super();
		this.setGTS(gts);
	}

	/**
	 * Constructor for a state genenerator for a product gts.
	 * @param gts the product gts
	 */
	public StateGenerator(ProductGTS gts) {
		super();
		this.setProductGTS(gts);
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
//		this.collector.setGTS(gts);
//		this.applier = null;
	}

	/**
	 * Returns the product gts.
	 * @return the product gts
	 */
	public ProductGTS getProductGTS() {
		return productGts;
	}

	/**
	 * Sets the product gts.
	 * @param gts the product gts
	 */
	public void setProductGTS(ProductGTS gts) {
		this.productGts = gts;
	}

	/** Convenience method to retrieve the record from the current GTS. */
	protected final SystemRecord getRecord() {
		return getGTS().getRecord();
	}

//	/**
//	 * Computes and returns the set of successor states of a given state,
//	 * insofar they are not yet in the GTS at time of invocation.
//	 * The states that are returned are added to the GTS first.
//	 * @param state the state for which the successors are to be generated;
//	 * assumed to be in the GTS
//	 */
//    public void explore(final GraphState state) {
//        reporter.start(SUCC);
//        // check if the transitions have not yet been generated
//        if (!state.isClosed()) {
//            collector.reset();
//        	
//            
//            Location lBefore = state.getLocation();
//            
//            getApplier(state).doApplications(new RuleApplier.Action() {
//				public void perform(RuleApplication application) {
//                    addTransition(state, application);
//				}
//        	});
//        	
//            
//            Location lAfter = state.getLocation();
//            
//            if( lBefore != null && lAfter != null ) {
//            	if( !lBefore.equals(lAfter)) {
//            		// recursive call, if location of current state was updated
//            		explore(state);
//            	}
//            }
//        	
//            //if (! collector.isTransitionsAdded()) {
//            // this test was not enough in case a linear exploration had already happened and one tried
//        	// to explore a state that didn't have more then the already existing outgoing application 
//        	// getGTS().setFinal(state);
//            //}
//        	
//        	// new test, just look if there are any transitions stored for this state
//
//        	// should this change when there is control? let's see what happens :)
//        	if( state.getLocation() != null ) {
//        		// thus, if there is control, red means the state is a success-state
//        		if( state.getLocation().isSuccess() ) {
//        			// let's make it closed first, so it will appear red in the statespace.
//        			// TODO: fix closed, might not be realistic
//        			getGTS().setClosed(state);
//        			getGTS().setFinal(state);
//        		}
//        	}
//        	else if (state.getTransitionSet().isEmpty()) {
//        		getGTS().setFinal(state);
//        	}
//            
//        	// not closing when there's control available...
//        	// TODO: fix this somehow..
//        	if( state.getLocation() == null ) {
//        		getGTS().setClosed(state);
//        	}
//        }
//        reporter.stop();
//    }
    
//    /**
//	 * Generates the set of successor states of a given state and
//	 * adds them to the underlying GTS; then returns the set of all
//	 * successor states.
//	 * @param state the state for which the successors are to be generated;
//	 * assumed to be in the GTS
//	 * @return the set of all successors of <code>state</code>
//	 */
//    public Collection<GraphState> getSuccessors(GraphState state) {
//        explore(state);
//        return state.getNextStateSet();
//    }
     
//	/**
//	 * Returns an iterator over all successor states of a given state,
//	 * generating the states and adding them to the GTS if necessary.
//	 * @param state the state for which the successors are to be generated;
//	 * assumed to be in the GTS
//	 * @return a lazy iterator over all successors of <code>state</code>
//	 */
//    public Iterator<? extends GraphState> getSuccessorIter(final GraphState state) {
//        if (state.isClosed()) {
//            // get the next states from the outgoing edges
//            return state.getNextStateIter();
//        } else {
//            final Iterator<RuleApplication> derivationIter = getApplier(state).getApplicationIter();
//            if (!derivationIter.hasNext()) {
//            	getGTS().setFinal(state);
//            }
//            // get the next states by adding transitions for the derivations
//            return new TransformIterator<RuleApplication,GraphState>(derivationIter) {
//            	@Override
//                public boolean hasNext() {
//                    if (hasNext) {
//                        hasNext = super.hasNext();
//                        if (!hasNext) {
//                        	getGTS().setClosed(state);
//                        }
//                    }
//                    return hasNext;
//                }
//
//            	@Override
//                protected GraphState toOuter(RuleApplication from) {
//                    return addTransition(state, from);
//                }
//                
//                private boolean hasNext = true;
//            };
//        }
//    }
    
//    /**
//     * Adds a transition to the GTS, constructed from a given rule application.
//     * The application's target graph is compared to the existing states for symmetry;
//     * if a symmetric one is found then that is taken as target state. 
//     * If no symmetric state is found, then a fresh target state is added.
//     * The actual target state is returned as the result of the method.
//     * @param source the source state of the new transition
//     * @param appl the derivation underlying the transition to be added
//     * @return the target state of the resulting transition
//     */
	/** To be called only by {@link #addTransition(GraphState, RuleMatch, ExploreCache)}.*/
    private Set<? extends GraphTransition> addTransition(GraphState source, RuleApplication appl, ExploreCache cache) {
        reporter.start(ADD_TRANSITION);
        
        GraphTransition transition;
        if (!appl.getRule().isModifying() ) {
        	Location targetLocation = cache.getTarget(appl.getRule());
        	if (source.getLocation() != targetLocation) {
	        	GraphNextState freshTarget = createState(appl, source);
	        	freshTarget.setLocation(targetLocation);

	        	reporter.start(ADD_STATE);
	        	GraphState isoTarget = getGTS().addState(freshTarget);
	        	reporter.stop();
	        	if (isoTarget == null) {
	        		transition = freshTarget;
	        	} else {
	        		transition = createTransition(appl, source, isoTarget, true);
	            }
        	} else {
            	transition = createTransition(appl, source, source, false);
        	}
        } else {
        	GraphState confluentTarget = getConfluentTarget(source, appl);
        	Location targetLocation = cache.getTarget(appl.getRule());
        	
            if (confluentTarget == null || confluentTarget.getLocation() != targetLocation ) {
	        	// can't have this as add_transition, it may be counted as matching 
	        	GraphNextState freshTarget = createState(appl, source);
	        	freshTarget.setLocation(cache.getTarget(appl.getRule()));
	            	
	        	reporter.start(ADD_STATE);
	        	GraphState isoTarget = getGTS().addState(freshTarget);
	        	reporter.stop();
	        	if (isoTarget == null) {
	        		transition = freshTarget;
	        	} else {
	        		transition = createTransition(appl, source, isoTarget, true);
	            }
            }
            else {
            	transition = createTransition(appl, source, confluentTarget, false);
            	confluentDiamondCount++;
            }
        }

        // add transition to gts
        getGTS().addTransition(transition);
              
        reporter.stop();
        Set<GraphTransition> result = new HashSet<GraphTransition>(1);
        result.add(transition);
        return result;
    }
  
    
    /**
     * Adds to the GTS the transitions defined from a given rule match. (Multiple
     * transitions may be added only in the case of abstract simulation.)
     * The applications' target graphs are compared to the existing states for symmetry;
     * if a symmetric one is found then that is taken as target state. 
     * If no symmetric state is found, then a fresh target state is added.
     * The method returns the set of target states of all transitions defined by the match.
     * @param source the source state of the new transition
     * @param match the rule match defining the derivation
     * @return the set of actually added states
     */
	public Set<? extends GraphState> addTransition(GraphState source, RuleMatch match, ExploreCache cache) {
		
		Set<? extends GraphTransition> gtrs = applyMatch(source, match, cache);
		HashSet<GraphState> states = new HashSet<GraphState>();
		for( GraphTransition trans : gtrs ) {
			states.add(trans.target());
		}
		
		return states;
	}
	
	/**
	 * Applies a match and returns the corresponding graph transitions
	 * 
	 * @param source
	 * @param match
	 * @param cache
	 * @return
	 */
	public Set<? extends GraphTransition> applyMatch(GraphState source, RuleMatch match, ExploreCache cache) {
		DefaultScenario.reporter.start(DefaultScenario.GET_DERIVATIONS);
		RuleApplication appl = getRecord().getApplication(match, source.getGraph());
		DefaultScenario.reporter.stop();
		return this.addTransition(source, appl, cache);
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
    	// FIXME: where is isUseDependencies set then?
    	//!AliasRuleApplier.isUseDependencies() ||
    	if ( !(appl instanceof AliasRuleApplication)) {
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
//        if (result != null) {
//        	confluentDiamondCount++;
//        }
        return result;
    }

	/**
	 * Creates a fresh graph state, based on a given rule application and source state.
	 */
	private GraphNextState createState(RuleApplication appl, GraphState source) {
		return new DefaultGraphNextState((AbstractGraphState) source, appl, null);
//		return new DefaultGraphNextState((AbstractGraphState) source, appl.getEvent(), appl.getCoanchorImage(), location);
	}

//    /**
//     * Creates a fresh graph transition, based on a given rule application and source state.
//     */
//    private GraphTransition createTransition(RuleApplication appl, GraphState source) {
//    	return createTransition(appl, source, createState(appl, source), false);
//    }
    
    /**
     * Creates a fresh graph transition, based on a given rule application and source and target state.
     * A final parameter determines if the target state is directly derived from the source, or modulo a symmetry.
     */
    private GraphTransition createTransition(RuleApplication appl, GraphState source, GraphState target, boolean symmetry) {
        return new DefaultGraphTransition(appl.getEvent(), appl.getCreatedNodes(), source, target, symmetry);
    }
    

    /**
     * Adds a transition to the product gts given a source Buechi graph-state,
     * a graph transition, and a target Buechi location.
     * @param source the source Buechi graph-state
     * @param transition the graph transition
     * @param targetLocation the target Buechi location
     * @return the added product transition
     */
    public Set<ProductTransition> addTransition(BuchiGraphState source, GraphTransition transition, BuchiLocation targetLocation) {
    	reporter.start(ADD_TRANSITION);
    	// we assume that we only add transitions for modifying graph transitions
    	BuchiGraphState target = createBuchiGraphState(source, transition, targetLocation);
    	BuchiGraphState isoTarget = getProductGTS().addState(target);
    	ProductTransition productTransition = null;

    	if (isoTarget == null) {
    		// no isomorphic state found
    		productTransition = createProductTransition(source, transition, target);
    	} else {
    		assert (isoTarget.iteration() <= ModelChecking.CURRENT_ITERATION) : "This state belongs to the next iteration and should not be explored now.";
    		productTransition = createProductTransition(source, transition, isoTarget);
    	}

    	reporter.stop();
    	return getProductGTS().addTransition(productTransition);
    }

    private BuchiGraphState createBuchiGraphState(BuchiGraphState source, GraphTransition transition, BuchiLocation targetLocation) {
    	if (transition == null) {
    		// the system-state is a final one for which we assume an artificial self-loop
    		// the resulting Buchi graph-state is nevertheless the product of the
    		// graph-state component of the source Buchi graph-state and the target
    		// Buchi-location
    		return new BuchiGraphState(getProductGTS().getRecord(), source.getGraphState(), targetLocation, source);
    	} else {
    		return new BuchiGraphState(getProductGTS().getRecord(), transition.target(), targetLocation, source);
    	}
    }

    private ProductTransition createProductTransition(BuchiGraphState source, GraphTransition transition, BuchiGraphState target) {
    	return new ProductTransition(source, transition, target);
    }

    /** The underlying GTS. */
	private GTS gts;
	private ProductGTS productGts;
//	/**
//	 * The number of confluent diamonds found.
//	 */
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
//    static public final int ADD_TRANSITION_START = reporter.newMethod("addTransition - start");
    /** Profiling aid for adding transitions. */
//    static private final int SUCC = reporter.newMethod("computing successors");
}