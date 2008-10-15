package groove.calc;

import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.util.Property;

public class MaximalStateProperty extends Property<GraphState> {
    @Override
    public boolean isSatisfied(GraphState state) {
        return isMaximal(state);
    }

    private boolean isMaximal(GraphState state) {
        for (GraphTransition trans : state.getTransitionSet()) {
            if (!trans.target().equals(state)) {
                return false;
            }
        }
        return true;
    }
}
