package groove.explore.result;

import groove.lts.GraphState;

/** Defines a condition that may or not hold in a {@link GraphState}.
 * The condition may be negated.
 * Such conditions may be used by strategies in order to explore only states
 * that satisfy the condition.
 * @author Iovka Boneva
 *
 * @param <C> Type of the object defining the condition.
 */
public abstract class ExploreCondition<C> {
	
	/** Determines whether the condition is satisfied by a graph state. 
	 * This method typically uses the condition set using
	 * {@link #setCondition(Object)}.
	 * 
	 * @param state
	 * @return
	 */
	public abstract boolean isSatisfiedBy (GraphState state);
	
	/** The parameter determines whether the condition
	 * is to be checked positively or negatively.
	 * 
	 * @param b
	 */
	public void setNegated(boolean b) {
		this.isNegated = b; 
	}
	
	/** Sets the condition.
	 * @param condition should not be null
	 */
	public void setCondition(C condition) {
		this.condition = condition;
	}
	
	public Class<?> getConditionType() {
		return condition.getClass();
	}
	
	/** Indicates whether the condition is negated. */
	protected boolean isNegated;
	/** The condition. */
	protected C condition;

}
