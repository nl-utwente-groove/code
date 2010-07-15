package groove.explore.result;

import groove.lts.GraphState;
import groove.lts.GraphTransition;

/**
 * Condition expressing that a state is maximal if it has only
 * self-transitions.
 * 
 * @author Tom Staijen
 * @version $Revision $
 */
@Deprecated
public class MaximalStateCondition extends OldExploreCondition<GraphState> {
    
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
