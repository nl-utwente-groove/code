package groove.explore.strategy;


import groove.explore.util.ExploreCache;
import groove.explore.util.MatchesIterator;
import groove.explore.util.RandomNewStateChooser;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.trans.RuleMatch;

/** This depth-first search algorithm generates at one step all outgoing 
 * transitions for some rule.
 * 
 * At each step, the exploration continues with a random successor fresh state,
 * or backtracks if there are no unexplored successor states.
 * @author Iovka Boneva
 *
 */
public class DepthFirstStrategy2 extends AbstractBacktrackingStrategy {
	
	/**
	 * Explores all outgoing transitions for one rule. 
	 */
	@Override
	public boolean next() {
		if (getAtState() == null) {
			getGTS().removeGraphListener(this.collector);
			return false; 
		}
		ExploreCache cache = getCache(false, true);
		currMatchIter = getMatchesIterator(cache);
	
		this.collector.reset();
		while (currMatchIter.hasNext() && ! currMatchIter.isEndRule()) {
			RuleMatch match = currMatchIter.next();
			getGenerator().addTransition(getAtState(), match, cache);
		}
		if (! currMatchIter.hasNext()) {
			setClosed(getAtState());
		}
		updateAtState();
		return true;
	}
	
	/** Computes the new value for {@link #atState}. */
	@Override
	protected void updateAtState() {
		this.backFrom = null;    // will be given non null value in case of backtracking
		if (this.collector.pickRandomNewState() != null) {
			this.atState = this.collector.pickRandomNewState();
			return;
		}
		GraphState s = getRandomOpenSuccessor(this.atState);
		if (s != null) {
			this.atState = s;
			return;
		}
		if (currMatchIter.hasNext()) {
			// continue exploring the same state
			return;
		}
		// backtracking
		do {
			this.backFrom = this.atState;
			this.atState = parentOf(this.atState);
		} while (this.atState != null && (s = getRandomOpenSuccessor(this.atState)) == null && !getGTS().isOpen(this.atState));
		
		// identify the reason of exiting the loop
		if (this.atState == null) { return; } // the start state is reached and does not have open successors
		if (s != null) { // the current state has an open successor (is not really backtracking, a sibling state is fully explored)
			this.backFrom = null;
			this.atState = s; 
		}  
		// else, atState is open, so we continue exploring it
	}
	
	@Override
	public void setGTS(GTS gts) {
		super.setGTS(gts);
		this.getGTS().addGraphListener(this.collector);
	}
	
	/** Creates a strategy with a given cache size. 
	 * @param cacheSize the number of states to be stored in the cache. A smaller
	 * value optimizes memory usage.
	 */
	public DepthFirstStrategy2 (int cacheSize) {
		this.aliasing = false;
		this.explCacheCache = new CacheMap<GraphState,ExploreCache>(cacheSize);
		// the matches iterator cache is not used, thus its size is left to 0
	}
	
//	/** Used to store a limited number of caches for states that have not been fully explored yet. */
//	private CacheMap<GraphState,ExploreCache> cacheMap;
	/** Used to register a state added to the GTS. */
	private RandomNewStateChooser collector = new RandomNewStateChooser();;
	/** The current matches iterator, associated to atState.
	 * Is initialized by {@link #next()} and is used in
	 * {@link #updateAtState()}.
	 */
	private MatchesIterator currMatchIter;

}
