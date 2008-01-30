package groove.explore.strategy;

import groove.explore.util.ExploreCache;
import groove.explore.util.MatchesIterator;
import groove.lts.GraphState;


import java.util.Iterator;

/**
 * At each step, fully explores a randomly chosen open state.
 * The strategy is very memory-efficient.
 * This strategy ignores the start state.
 * @author Staijen
 *
 */
public class BranchingStrategy extends AbstractStrategy {

	/**
	 * A step of this strategy completely explores one state.
	 */
	public boolean next() {
		if (getAtState() == null) { return false; }
		ExploreCache cache = getCache(false, false);
		MatchesIterator matchesIter = getMatchesIterator(cache);
		
		while( matchesIter.hasNext() ) {
			getGenerator().addTransition(getAtState(), matchesIter.next(), cache);
		}
		setClosed(getAtState());
		updateAtState();
		return true;
	}
	
	@Override
	protected void updateAtState() {
		Iterator<GraphState> stateIter = getGTS().getOpenStateIter();
		if (stateIter.hasNext()) {
			this.atState = stateIter.next();
		} else {
			this.atState = null;
		}
	}
}
