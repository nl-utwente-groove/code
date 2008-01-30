package groove.explore.result;

import groove.graph.GraphShape;
import groove.graph.Node;
import groove.lts.GraphState;

/** Accepts states that violate an (possible negated) invariant condition on states.
 * The invariant is defined by the applicability of a given rule.
 * @author Iovka Boneva
 * @param <C> The type of the condition.
 *
 */
public class InvariantViolatedAcceptor<C> extends ConditionalAcceptor<C> {
	@Override
	public void addUpdate(GraphShape graph, Node node) {
		if (! getCondition().isSatisfiedBy((GraphState) node)) {
			getResult().add((GraphState) node);
		}
	}
}
