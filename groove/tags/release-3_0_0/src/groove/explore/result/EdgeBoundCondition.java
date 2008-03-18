package groove.explore.result;

import java.util.Map;
import java.util.Set;

import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Label;
import groove.lts.GraphState;

/** Condition on the number of edges in a graph state. 
 * 
 * The condition is given by a map associating a maximum (or minimum if the condition is negated)
 * number of edges with labels. 
 * 
 * @author Iovka Boneva
 * */
public class EdgeBoundCondition extends ExploreCondition<Map<Label,Integer>> {

	@Override
	public boolean isSatisfiedBy(GraphState state) {
		boolean result = true;
		Graph g = state.getGraph();
		for (Map.Entry<Label,Integer> entry : condition.entrySet()) {
			Set<? extends Edge> labelSet = g.labelEdgeSet(2, entry.getKey());
            if (labelSet != null) {
                result = labelSet.size() <= entry.getValue();
            }
            if (! result) { break; }
		}
	
		return isNegated ? !result : result;
	}

}
