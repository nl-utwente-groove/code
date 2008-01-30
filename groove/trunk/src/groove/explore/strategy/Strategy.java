package groove.explore.strategy;

import groove.lts.GTS;
import groove.lts.GraphState;

/** A strategy defines an order in which the states of a graph transition system
 * are to be explored. It can also determine which states are to be explored 
 * either according to some condition (see {@link ConditionalStrategy}),
 * or because of the nature of the strategy (see for instance {@link LinearStrategy}).
 * Most often, a strategy starts its exploration at some state, fixed by 
 * the {@link #setState(GraphState)} method.
 * 
 * A strategy adds states and transitions to a graph transition system. However, 
 * it should use a {@link StateGenerator} and not manipulate the graph transition
 * system directly.
 * @author 
 *
 */
public interface Strategy {

	/** Executes one step of the strategy. 
	 * @return false if the strategy is completed, false otherwise.
	 * @require The previous call of this method, if any, 
	 * returned <code>true</code>. Otherwise, the behavior is not guaranteed.
	 */
	public boolean next();
	
	/** Sets the state where the strategy starts exploring.
	 * @param state
	 */
	public void setState(GraphState state);
	/** Sets the graph transition system to be explored.
	 * @param gts
	 */
	public void setGTS(GTS gts);
	
}
