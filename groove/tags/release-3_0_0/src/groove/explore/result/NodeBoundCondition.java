package groove.explore.result;

import groove.lts.GraphState;

/** Condition on the number of nodes of a state.
 * 
 * This condition is given by an integer value which gives a maximum
 * (or minimum if negated) bound on the number of nodes in a state.
 * 
 * @author Iovka Boneva
 *
 */
public class NodeBoundCondition extends ExploreCondition<Integer> {

	@Override
	public boolean isSatisfiedBy(GraphState state) {
		boolean result = state.getGraph().nodeCount() <= this.condition;
		return isNegated ? !result : result;
	}

}
