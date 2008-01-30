package groove.control;

import groove.trans.Rule;
import groove.util.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LocationAutomatonBuilder {

	private Map<ControlState, Map<Set<Rule>, ControlState>> interalDepensencyTargets;
	
	/** stores controllocations by the contained states **/
	private Map<Set<ControlState>, ControlLocation> uniqueLocations;

	public LocationAutomatonBuilder() {
	}
	
	/** **/
	public ControlLocation startLocation(ControlState startState) {
		HashSet<ControlState> states = new HashSet<ControlState>();
		states.add(startState);
		return getLocation(states);
	}
	
	private static Set<Rule> EMPTYSET = new HashSet<Rule>();

	private Map<Set<ControlState>, ControlLocation> locationMap = new HashMap<Set<ControlState>, ControlLocation>();
	
	public ControlLocation getLocation(Set<ControlState> states) {
		// these states have to be extended with lambda's and elses
		// TODO: fix this to complete control????
		
		Set<ControlState> baseSet = lambdaMaximize(states);
		
		ControlLocation location = locationMap.get(baseSet);
		if( location == null ) {
			location = buildLocation(baseSet);
			locationMap.put(baseSet, location);
		}
		return location;
	}
	
	private Set<ControlState> lambdaMaximize(Set<ControlState> states) {
		
		int count = 0;
		while( states.size() > count) {
			count = states.size();
			for( ControlState state : states ) {
				states.addAll(state.lambdaTargets());
			}
		}
		return states;
	}
	
	/**
	 * 
	 * @param states the lambdaMaximized set of states for the given failure set
	 * @param failure the failureset for all ControlStates in states
	 * @param result the container for all the pairs found
	 */
	private void collectStatePairs(Set<ControlState> states, Set<Rule> failure, Set<Pair<ControlState, Set<Rule>>> result) {
		
		for( ControlState state : states ) {
			result.add(new Pair(state, failure));
			// handle the elsetransitions of this state
			for( ElseControlTransition trans : state.elseTransitions() ) {
				// concat the failure
				Set<Rule> elseFailure = new HashSet<Rule>();
				elseFailure.addAll(failure);
				elseFailure.addAll(trans.getFailureSet());
				Set<ControlState> elseStates = new HashSet<ControlState>();
				elseStates.add(trans.target());
				collectStatePairs(lambdaMaximize(elseStates), elseFailure, result);
			}
		}
	}
	
	private ControlLocation buildLocation(Set<ControlState> states) {
		ControlLocation location = new ControlLocation(this);
		Set<Pair<ControlState, Set<Rule>>> pairs = new HashSet<Pair<ControlState, Set<Rule>>>();
		collectStatePairs(states, EMPTYSET, pairs);
		location.setStates(pairs);
		location.initialize();
		return location;
	}
}