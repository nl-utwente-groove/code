package groove.explore.strategy;

import groove.explore.result.ExploreCondition;

/** To be extended by strategies that explore only portions
 * of a GTS, defined by some exploration condition.
 * @author Iovka Boneva
 */
// IOVKA There is a possibility of making conditions orthogonal to strategies, so
// that all strategies can be used with conditions. 
// However, this makes implementation of strategies much less simple,
// as they should remember which states should not be explored.
// Therefore, I prefer for now leave the strategies simpler, and
// if necessary all strategies may be made conditional (with possibly empty condition)
// In order to do this, the ConditionalStrategy signature is to be
// merged with the AbstractStrategy signature, and implementation of 
// (at least) next() and updateAtState() methods would change.
public interface ConditionalStrategy extends Strategy {

	/** The exploration condition that, when not satisfied by a 
	 * state, forbids exploring this state.
	 * @param condition
	 */
	public void setExploreCondition (ExploreCondition<?> condition);

}
