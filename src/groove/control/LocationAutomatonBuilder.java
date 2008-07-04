package groove.control;

import groove.trans.Rule;
import groove.util.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
/** *  * Builder for reusable ControlLocation objects. *  * @author Arend Rensink * @version $Revision $ */
public class LocationAutomatonBuilder {
	private Map<ControlState, Map<Set<Rule>, ControlState>> interalDepensencyTargets;	/** stores controllocations by the contained states **/	private Map<Set<ControlState>, ControlLocation> uniqueLocations;
	/** **/	public ControlLocation startLocation(ControlState startState) {		HashSet<ControlState> states = new HashSet<ControlState>();		states.add(startState);		return getLocation(states);	}
	private static Set<Rule> EMPTYSET = new HashSet<Rule>();	private Map<Set<ControlState>, ControlLocation> locationMap = new HashMap<Set<ControlState>, ControlLocation>();
	/**	 * Returns a location for a set of ControlStates.	 * Will be built first if it does not exist yet.	 * 	 * @param states 	 * @return location	 */	public ControlLocation getLocation(Set<ControlState> states) {		Set<ControlState> baseSet = lambdaMaximize(states);		ControlLocation location = locationMap.get(baseSet);
		if( location == null ) {			location = buildLocation(baseSet);			locationMap.put(baseSet, location);		}		return location;	}	/**	 * Extends a set with states reacheable from lambda-transitions.	 * @param states	 * @return set	 */
	private Set<ControlState> lambdaMaximize(Set<ControlState> states) {		int count = 0;		while( states.size() > count) {			count = states.size();			for( ControlState state : states ) {				states.addAll(state.lambdaTargets());			}		}		return states;	}
	/**	 * Collect all Pairs of states and enablingFailures.	 * 	 * @param states the lambdaMaximized set of states for the given failure set	 * @param failure the failureset for all ControlStates in states	 * @param result the container for all the pairs found	 */	private void collectStatePairs(Set<ControlState> states, Set<Rule> failure, Set<Pair<ControlState, Set<Rule>>> result) {		for( ControlState state : states ) {			Pair<ControlState,Set<Rule>> p = new Pair<ControlState,Set<Rule>>(state, failure);
			if( !result.contains(p)) {
				result.add(new Pair<ControlState,Set<Rule>>(state, failure));				// handle the elsetransitions of this state
			
				for( ControlTransition trans : state.elseTransitions() ) {					// concat the failure					Set<Rule> elseFailure = new HashSet<Rule>();					elseFailure.addAll(failure);					elseFailure.addAll(trans.getFailureSet());
					Set<Rule> transFailure = trans.getFailureSet();					Set<ControlState> elseStates = new HashSet<ControlState>();					elseStates.add(trans.target());					collectStatePairs(lambdaMaximize(elseStates), elseFailure, result);				}
			}		}
	}	/**	 *  Build a ControlLocation for a set of states when it does not exists yet.	 *  	 * @param states	 * @return control location	 */
	private ControlLocation buildLocation(Set<ControlState> states) {		ControlLocation location = new ControlLocation(this);		Set<Pair<ControlState, Set<Rule>>> pairs = new HashSet<Pair<ControlState, Set<Rule>>>();		collectStatePairs(states, EMPTYSET, pairs);		location.setStates(pairs);		location.initialize();		return location;
	}
}