package groove.explore.strategy;

import groove.explore.result.ExploreCondition;

/** A conditional strategy based on a DepthFirstStrategy4. */
public class ConditionalDepthFirstStrategy extends DepthFirstStrategy4 implements ConditionalStrategy {

	
	@Override
	protected void updateAtState() {
		this.atState = null;
		while (this.atState == null && !this.toExplore.isEmpty()) {
			this.atState = this.toExplore.pop();
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
