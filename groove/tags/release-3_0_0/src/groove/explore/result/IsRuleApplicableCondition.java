package groove.explore.result;

import groove.lts.GraphState;
import groove.trans.Rule;

/** Condition satisfied when a rule is applicable.
 * @author Iovka Boneva
 *
 */
public class IsRuleApplicableCondition extends ExploreCondition<Rule> {

	@Override
	public boolean isSatisfiedBy(GraphState state) {
		boolean result = this.condition.hasMatch(state.getGraph());
		return isNegated ? !result : result;
	}

}
