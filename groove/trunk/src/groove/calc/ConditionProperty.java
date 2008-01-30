package groove.calc;

import groove.lts.GraphState;
import groove.trans.Condition;
import groove.util.Property;

public class ConditionProperty extends Property<GraphState> {

	private Condition test;
	
	public ConditionProperty(Condition test) {
		this.test = test;
	}
		@Override
	public boolean isSatisfied(GraphState state) {
		return test.hasMatch(state.getGraph());
	}
}
