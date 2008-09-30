package groove.control;

import groove.trans.Rule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
/** * Builder for reusable ControlLocation objects. *  * @version $Revision $ */
public class LocationAutomatonBuilder {
	/** Returns a control location based on a single control state. */
	public ControlLocation getLocation(ControlState startState) {		HashSet<ControlState> states = new HashSet<ControlState>();		states.add(startState);		return getLocation(states);	}
	
	/**	 * Returns a location for a set of ControlStates.	 */	public ControlLocation getLocation(Set<ControlState> states) {		Set<ControlState> baseSet = lambdaMaximize(states);		ControlLocation location = locationMap.get(baseSet);
		if( location == null ) {			location = computeLocation(baseSet);			locationMap.put(baseSet, location);		}		return location;	}	/**
	 * Builds a new control location for a given set of control states.
	 */
	private ControlLocation computeLocation(Set<ControlState> states) {
		Map<ControlState,Set<Set<Rule>>> stateFailuresMap = new HashMap<ControlState,Set<Set<Rule>>>();
		collectStateFailures(states, EMPTYSET, stateFailuresMap);
		return new ControlLocation(stateFailuresMap, this);
	}
//
//	/**
//	 * Collect all Pairs of states and enablingFailures.
//	 * 
//	 * @param states
//	 *            the lambdaMaximized set of states for the given failure set
//	 * @param failure
//	 *            the failure set for all ControlStates in states
//	 * @param result
//	 *            the container for all the pairs found
//	 */
//	private void collectStatePairs(Set<ControlState> states, Set<Rule> failure, Set<Pair<ControlState, Set<Rule>>> result) {
//		for( ControlState state : states ) {
//			Pair<ControlState,Set<Rule>> p = new Pair<ControlState,Set<Rule>>(state, failure);
//			if( result.add(p) ) {
//				for( ControlTransition trans : state.elseTransitions() ) {
//					// concat the failure
//					Set<Rule> elseFailure = new HashSet<Rule>();
//					elseFailure.addAll(failure);
//					elseFailure.addAll(trans.getFailureSet());
//					Set<ControlState> elseStates = new HashSet<ControlState>();
//					elseStates.add(trans.target());
//					collectStatePairs(lambdaMaximize(elseStates), elseFailure, result);
//				}
//			}
//		}
//	}

	/**
	 * Collect all Pairs of states and enablingFailures.
	 * 
	 * @param states
	 *            the lambdaMaximized set of states for the given failure set
	 * @param failure
	 *            the failure set for all ControlStates in states
	 * @param result
	 *            the container for all the pairs found
	 */
	private void collectStateFailures(Set<ControlState> states, Set<Rule> failure, Map<ControlState,Set<Set<Rule>>> result) {
		for( ControlState state : states ) {
			Set<Set<Rule>> failuresSet = result.get(state);
			if (failuresSet == null) {
				result.put(state, failuresSet = new HashSet<Set<Rule>>());
			}
			if (failuresSet.add(failure)) {
				for( ControlTransition trans : state.elseTransitions() ) {
					// concat the failure
					Set<Rule> elseFailure = new HashSet<Rule>(failure);
					elseFailure.addAll(trans.getFailureSet());
					Set<ControlState> elseStates = new HashSet<ControlState>();
					elseStates.add(trans.target());
					collectStateFailures(lambdaMaximize(elseStates), elseFailure, result);
				}
			}
		}
	}
	/**
	 * Extends a set of states with all states reachable by lambda-transitions.
	 */
	private Set<ControlState> lambdaMaximize(Set<ControlState> states) {
		Set<ControlState> result = new HashSet<ControlState>(states);
		Collection<ControlState> newStates = new ArrayList<ControlState>(states);
		while(newStates.size() > 0) {
			Iterator<ControlState> newStateIter = newStates.iterator();
			newStates = new ArrayList<ControlState>();
			while (newStateIter.hasNext()) {
				for (ControlState lambdaTarget: newStateIter.next().lambdaTargets()) {
					if (result.add(lambdaTarget)) {
						newStates.add(lambdaTarget);
					}
				}
			}
		}
		return result;
	}

	/** 
	 * Map from sets of control states to corresponding control locations.
	 * This map is built on demand. 
	 */
	private Map<Set<ControlState>, ControlLocation> locationMap = new HashMap<Set<ControlState>, ControlLocation>();

	/** Constant empty set (to save on memory). */
	private static Set<Rule> EMPTYSET = new HashSet<Rule>();
}