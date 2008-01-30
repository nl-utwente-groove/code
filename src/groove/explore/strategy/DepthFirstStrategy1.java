package groove.explore.strategy;

import java.util.Iterator;

import groove.explore.util.ExploreCache;
import groove.explore.util.RandomNewStateChooser;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.trans.RuleMatch;

/** This depth-first search algorithm systematically generates all outgoing 
 * transitions of any visited state.
 * 
 * At each step, the exploration continues with a random successor fresh state,
 * or backtracks if there are no unexplored successor states.
 * 
 * Even though this depth first search backtracks for finding the next state
 * to explore, it is not considered as a backtracking strategy (in the sense
 * of {@link AbstractBacktrackingStrategy}. This is because all explored
 * states are closed, thus the strategy does not need to cache any
 * information, neither to know from where it backtracked. 
 * 
 * @author Iovka Boneva
 *
 */
public class DepthFirstStrategy1 extends AbstractStrategy {

	@Override
	/**
	 * The next step makes atomic the full exploration of a state.
	 */
	public boolean next() {
		if (getAtState() == null) {
			getGTS().removeGraphListener(this.collector);
			return false; 
		}
		ExploreCache cache = getCache(false, false);
		Iterator<RuleMatch> matchIter = getMatchesIterator(cache);
		this.collector.reset();
		while (matchIter.hasNext()) {
			getGenerator().addTransition(getAtState(), matchIter.next(), cache);
		}
		setClosed(getAtState());
		updateAtState();
		return true;
	}
	
	@Override
	protected void updateAtState() {
		if (this.collector.pickRandomNewState() != null) {
			this.atState = this.collector.pickRandomNewState();
			return;
		}
		// backtracking
		GraphState s = atState;
		do {
			s = parentOf(s);
			this.atState = s == null ? null : getRandomOpenSuccessor(s);
		} while (s != null && this.atState == null); 
	}
	
	@Override
	public void setGTS(GTS gts) {
		super.setGTS(gts);
		gts.addGraphListener(this.collector);
	}
	
	private RandomNewStateChooser collector = new RandomNewStateChooser();
}
