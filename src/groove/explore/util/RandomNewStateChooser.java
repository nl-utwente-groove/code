package groove.explore.util;

import groove.graph.GraphAdapter;
import groove.graph.GraphShape;
import groove.graph.Node;
import groove.lts.GraphState;

/** Listens to a GTS and allows to pick a random state
 * among those newly added to the GTS.
 * Should listen to a single GTS.
 */
public class RandomNewStateChooser extends GraphAdapter {
	
	/** Returns a randomly chosen state among those newly
	 * added to the GTS it listens to since last {@link #reset()}
	 * operation. Two successive calls will return the same
	 * element.
	 * @return a randomly chosen state among those newly
	 * added to the GTS it listens to since last {@link #reset()}, or
	 * <code>null</code> if no new state was added.
	 */
	public GraphState pickRandomNewState() { return rc.pickRandom(); }
	
	/** Forgets all new states it has seen so far. */
	public void reset () { rc.reset(); }
	
	public void addUpdate(GraphShape shape, Node node) {
		rc.show((GraphState) node);
	}

	private RandomChooserInSequence<GraphState> rc = new RandomChooserInSequence<GraphState>();
}

