package groove.explore.strategy;

import java.util.Iterator;

import groove.explore.util.ExploreCache;
import groove.trans.RuleMatch;

/** Explores all outgoing transitions of a given state.
 * @author Iovka Boneva
 *
 */
public class ExploreStateStrategy extends AbstractStrategy {
	
	/** Creates a strategy with empty graph transition system and empty start state. 
	 * The GTS and the state should be set
	 * before using it.
	 * 
	 */
	public ExploreStateStrategy() {
		// empty
	}

	public boolean next() {
		if (! getGTS().isOpen(this.startState())) {
			return false;
		}
		// rule might have been interrupted 
		ExploreCache cache = getCache(true, false);
		Iterator<RuleMatch> matchesIter = getMatchesIterator(cache);
//		done when setClosed is called
//		if (!matchesIter.hasNext()) {
//			this.getGTS().setFinal(this.startState());
//		}
		while (matchesIter.hasNext()) {
			getGenerator().addTransition(this.startState(), matchesIter.next(), cache);
		}
		// the current state has been fully explored
		// therefore we can close it
		setClosed(this.startState());
		return false;
	}

	@Override
	protected void updateAtState() { 
		// unused
	}

}
