/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id$
 */
package groove.explore.result;

import java.util.Map;
import java.util.Set;

import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Label;
import groove.lts.GraphState;

/**
 * Condition on the number of edges in a graph state.
 * 
 * The condition is given by a map associating a maximum (or minimum if the
 * condition is negated) number of edges with labels.
 * 
 * @author Iovka Boneva
 */
public class EdgeBoundCondition extends ExploreCondition<Map<Label,Integer>> {

    @Override
    public boolean isSatisfied(GraphState state) {
        boolean result = true;
        Graph g = state.getGraph();
        for (Map.Entry<Label,Integer> entry : this.condition.entrySet()) {
            Set<? extends Edge> labelSet = g.labelEdgeSet(2, entry.getKey());
            if (labelSet != null) {
                result = labelSet.size() <= entry.getValue();
            }
            if (!result) {
                break;
            }
        }

        return this.negated ? !result : result;
    }

}
