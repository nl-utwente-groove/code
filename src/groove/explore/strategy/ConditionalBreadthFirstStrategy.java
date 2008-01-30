package groove.explore.strategy;

import groove.explore.result.ExploreCondition;

/** Breadth first exploration, by exploring only non explored states.
 * @author Iovka Boneva
 *
 */
public class ConditionalBreadthFirstStrategy extends BreadthFirstStrategy
		implements ConditionalStrategy {

	
	@Override
	public void updateAtState() {
		this.atState = null;
		while (! this.toExplore.isEmpty() && this.atState == null) {
			this.atState = this.toExplore.poll();
			if (!getExplCond().isSatisfiedBy(this.atState)) {
				this.atState = null;
			}
		}
	}
		
	@Override
	public void setExploreCondition(ExploreCondition<?> condition) {
		this.explCond = condition;
	}

	private ExploreCondition<?> getExplCond () {
		return this.explCond;
	}
	
	private ExploreCondition<?> explCond;

}
