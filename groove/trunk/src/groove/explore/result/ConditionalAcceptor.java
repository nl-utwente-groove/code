package groove.explore.result;

import groove.lts.GraphState;

/** A graph state acceptor that can be fed with an external object that
 * defines the acceptance condition.
 * The condition is given by an {@link ExploreCondition}. 
 * 
 * @author Iovka Boneva
 * @param <C> The parameter type of the explore condition.
 *
 */
public abstract class ConditionalAcceptor<C> extends Acceptor<GraphState> {
	
	/** Sets the condition to be used by {@link #isSatisfiedBy(Object)}. 
	 * @param condition 
	 */
	public void setCondition(ExploreCondition<C> condition) {
		this.condition = condition;
	}
	
	/** The pre-set condition.
	 * @return
	 */
	protected ExploreCondition<C> getCondition() {
		return this.condition;
	}
	
	/** The condition. */
	protected ExploreCondition<C> condition;
}
