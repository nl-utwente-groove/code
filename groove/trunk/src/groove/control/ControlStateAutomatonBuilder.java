package groove.control;

import groove.trans.Rule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Builder for reusable ControlLocation objects.
 * 
 * @version $Revision $
 */
@Deprecated
public class ControlStateAutomatonBuilder {
    /**
     * Returns a location for a set of ControlStates.
     */
    public ControlLocation getLocation(Set<ControlState> states) {
        Set<ControlState> baseSet = lambdaMaximize(states);
        ControlLocation location = this.locationMap.get(baseSet);
        if (location == null) {
            location = computeLocation(baseSet);
            this.locationMap.put(baseSet, location);
            location.setName("" + this.locationMap.size());
        }
        return location;
    }

    /**
     * Builds a new control location for a given set of control states.
     */
    private ControlLocation computeLocation(Set<ControlState> states) {
        Map<ControlState,Set<Set<Rule>>> stateFailuresMap =
            new HashMap<ControlState,Set<Set<Rule>>>();
        collectStateFailures(states, EMPTYSET, stateFailuresMap);
        for (Set<Set<Rule>> failureSet : stateFailuresMap.values()) {
            normaliseFailures(failureSet);
        }
        return null;
        //return new ControlLocation(stateFailuresMap, this);
    }

    /**
     * Recursively collects a map from failure-reachable states to the sets of
     * failures under which they are reachable.
     * @param states set of states to be added to the map
     * @param failure failure under which all elements of <code>states</code>
     *        are reachable
     * @param result container for all the pairs found
     */
    private void collectStateFailures(Set<ControlState> states,
            Set<Rule> failure, Map<ControlState,Set<Set<Rule>>> result) {
        for (ControlState state : states) {
            Set<Set<Rule>> failuresSet = result.get(state);
            if (failuresSet == null) {
                result.put(state, failuresSet = new HashSet<Set<Rule>>());
            }
            if (failuresSet.add(failure)) {
                for (ControlTransition trans : state.elseTransitions()) {
                    // concat the failure
                    Set<Rule> elseFailure = new HashSet<Rule>(failure);
                    elseFailure.addAll(trans.getFailureSet());
                    Set<ControlState> elseStates = new HashSet<ControlState>();
                    elseStates.add(trans.target());
                    collectStateFailures(lambdaMaximize(elseStates),
                        elseFailure, result);
                }
            }
        }
    }

    /** Removes failures that are proper supersets of others. */
    private void normaliseFailures(Set<Set<Rule>> failuresSet) {
        Iterator<Set<Rule>> failuresIter = failuresSet.iterator();
        while (failuresIter.hasNext()) {
            Set<Rule> failure = failuresIter.next();
            for (Set<Rule> otherFailure : new ArrayList<Set<Rule>>(failuresSet)) {
                if (failure.size() > otherFailure.size()
                    && failure.containsAll(otherFailure)) {
                    // failure is a proper superset of otherFailure
                    failuresIter.remove();
                    break;
                }
            }
        }
    }

    /**
     * Extends a set of states with all states reachable by lambda-transitions.
     */
    private Set<ControlState> lambdaMaximize(Set<ControlState> states) {
        Set<ControlState> result = new HashSet<ControlState>(states);
        Collection<ControlState> newStates =
            new ArrayList<ControlState>(states);
        while (newStates.size() > 0) {
            Iterator<ControlState> newStateIter = newStates.iterator();
            newStates = new ArrayList<ControlState>();
            while (newStateIter.hasNext()) {
                for (ControlState lambdaTarget : newStateIter.next().lambdaTargets()) {
                    if (result.add(lambdaTarget)) {
                        newStates.add(lambdaTarget);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Map from sets of control states to corresponding control locations. This
     * map is built on demand.
     */
    private final Map<Set<ControlState>,ControlLocation> locationMap =
        new HashMap<Set<ControlState>,ControlLocation>();

    /** Constant empty set (to save on memory). */
    private static Set<Rule> EMPTYSET = new HashSet<Rule>();
}