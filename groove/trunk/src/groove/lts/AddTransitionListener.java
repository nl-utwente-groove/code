package groove.lts;

import groove.graph.Edge;
import groove.graph.GraphShape;

/**
 * Listener that collects the fresh states into a set.
 */
public class AddTransitionListener extends LTSAdapter {

	/**
	 * Sets the result set to the empty set.
	 */
	public void reset() {
		transitionsAdded = false;
	}
	
	/** 
	 * Indicates if any transitions were added since {@link #reset()}
	 * was last called.
	 * @return <code>true</code> if any transitions were added
	 */
	public boolean isTransitionsAdded() {
		return transitionsAdded;
	}
	
	@Override
	public void addUpdate(GraphShape graph, Edge edge) {
		transitionsAdded = true;
	}
	
	/** 
	 * Variable that records if any transition have been added since the last
	 * {@link #reset()}.
	 */
	private boolean transitionsAdded;
}