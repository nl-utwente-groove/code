package groove.explore.strategy;

import groove.explore.util.ExploreCache;
import groove.explore.util.MatchesIterator;
import groove.graph.GraphAdapter;
import groove.graph.GraphShape;
import groove.graph.Node;
import groove.lts.GTS;
import groove.lts.GraphState;

/** Explores a single path until reaching a final state or a loop.
 * In case of abstract simulation, this implementation will prefer
 * going along a path then stopping exploration when a loop is met.
 * @author Iovka Boneva
 *
 */
public class LinearStrategy extends AbstractStrategy {

	@Override
	public boolean next() {
		if (this.atState == null) { 
			getGTS().removeGraphListener(this.collector);
			return false;
		}
		ExploreCache cache = getCache(true, false);
		MatchesIterator matchIter = getMatchesIterator(cache);
		this.collector.reset();
		if (matchIter.hasNext()) {
			getGenerator().addTransition(getAtState(), matchIter.next(), cache);
		} else {
			setClosed(atState);
		}
		updateAtState();
		return true;
	}
	
	@Override
	protected void updateAtState() {
		this.atState = this.collector.getNewState();
	}
	
	@Override
	public void setGTS(GTS gts) {
		super.setGTS(gts);
		gts.addGraphListener(collector);
	}

	/** Collects states newly added to the GTS. */
	private NewStateCollector collector = new NewStateCollector();
	
	/** Registers the first new state added to the 
	 * GTS it listens to.
	 * Such an object should be added as listener only to a single GTS. 
	 */
	public class NewStateCollector extends GraphAdapter {

		NewStateCollector() { reset(); }
		
		/** Returns the collected new state,
		 * or null if no new state was registered.
		 * @return the collected new state,
		 * or null if no new state was registered since last reset operation
		 */
		GraphState getNewState() { return this.newState; }
		
		/** Forgets collected new state. */
		void reset () { this.newState = null; }
		
		public void addUpdate(GraphShape shape, Node node) {
			if (newState == null) {
				newState = (GraphState) node;
			}
		}
		private GraphState newState;
	}

}
