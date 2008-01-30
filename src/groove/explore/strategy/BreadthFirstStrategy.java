package groove.explore.strategy;

import groove.explore.util.ExploreCache;
import groove.explore.util.MatchesIterator;
import groove.graph.Edge;
import groove.graph.GraphShape;
import groove.graph.GraphShapeListener;
import groove.graph.Node;
import groove.lts.GTS;
import groove.lts.GraphState;

import java.util.LinkedList;

/** A breadth-first exploration that uses its own queue of open states.
 * Guarantees a breadth-first exploration, but consumes lots of memory.
 */
public class BreadthFirstStrategy extends AbstractStrategy {
	
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
	public void updateAtState() {
		this.atState = this.toExplore.poll();
	}
	
	@Override
	public void setGTS(GTS gts) {
		super.setGTS(gts);
		gts.addGraphListener(toExplore);
	}	
	
	/** The states to be explored, in a FIFO order. */
	protected ToExploreQueue toExplore = new ToExploreQueue();
	/** Iterator over the matches of the current state. */
	
	/** A queue with states to be explored, used as a FIFO. */
	protected class ToExploreQueue extends LinkedList<GraphState> implements GraphShapeListener {

		@Override
		public void addUpdate(GraphShape graph, Node node) {
			offer((GraphState) node);
		}
		@Override
		public void addUpdate(GraphShape graph, Edge edge) { /* empty */ }

		@Override
		public void removeUpdate(GraphShape graph, Node node) { /* empty */ }

		@Override
		public void removeUpdate(GraphShape graph, Edge elem) { /* empty */ }
		
	}
	
	
}
