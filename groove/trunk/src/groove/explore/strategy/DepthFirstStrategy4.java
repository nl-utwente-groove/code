package groove.explore.strategy;

import java.util.Stack;

import groove.explore.util.ExploreCache;
import groove.explore.util.MatchesIterator;
import groove.graph.Edge;
import groove.graph.GraphShape;
import groove.graph.Node;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.LTS;
import groove.lts.LTSListener;
import groove.lts.State;

/** As  {@link DepthFirstStrategy1}, makes a depth first exploration by
 * closing each visited states. Maintains a stack for the order in which
 * states are to be explored (thus is less memory efficient). Is suitable
 * for conditional strategies.
 * 
 * This strategy is not considered as a backtracking strategy, as states are
 * fully explored and there is no need of maintaining caches.
 * 
 * @author IovkaBoneva
 *
 */
public class DepthFirstStrategy4 extends AbstractStrategy {

	@Override
	public boolean next() {
		if (getAtState() == null) {
			getGTS().removeGraphListener(toExplore);
			return false; 
		}
		ExploreCache cache = getCache(false, false);
		MatchesIterator matchIter = getMatchesIterator(cache);
	
		while (matchIter.hasNext()) {
			getGenerator().addTransition(getAtState(), matchIter.next(), cache);
		}
		setClosed(getAtState());
		updateAtState();
		return true;
	}

	
	@Override
	protected void updateAtState() {
		if (this.toExplore.isEmpty()) {
			this.atState = null;
		} else {
			this.atState = toExplore.pop();
		}
	}
	
	@Override
	public void setGTS(GTS gts) {
		super.setGTS(gts);
		gts.addGraphListener(toExplore);
	}	
	
	
	/** Stack giving the order in which states are to be explored. */
	protected StackToExplore toExplore = new StackToExplore();

	
	protected class StackToExplore extends Stack<GraphState> implements LTSListener {
	
		@Override
		public void addUpdate(GraphShape graph, Node node) {
			push((GraphState) node);
		}

		@Override
		public void closeUpdate(LTS graph, State explored) { /* empty */ }
		
		@Override
		public void addUpdate(GraphShape graph, Edge edge) { /* empty */ }

		@Override
		public void removeUpdate(GraphShape graph, Node node) { /* empty */ }
		
		@Override
		public void removeUpdate(GraphShape graph, Edge elem) { /* empty */ }
	}
	
}
