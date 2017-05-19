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

import org.eclipse.jdt.annotation.NonNullByDefault;

import groove.grammar.host.HostEdge;
import groove.grammar.host.HostGraph;
import groove.grammar.type.TypeLabel;
import groove.lts.GraphState;

/**
 * Condition on the number of edges in a graph state.
 *
 * The condition is given by a map associating a maximum (or minimum if the
 * condition is negated) number of edges with labels.
 *
 * @author Iovka Boneva
 */
@NonNullByDefault
public class EdgeBoundCondition extends ExploreCondition<Map<TypeLabel,Integer>> {
    /**
     * Constructs a condition.
     */
    public EdgeBoundCondition(Map<TypeLabel,Integer> condition) {
        super(condition);
    }

    @Override
    public boolean isSatisfied(GraphState state) {
        boolean result = true;
        HostGraph g = state.getGraph();
        for (Map.Entry<TypeLabel,Integer> entry : this.condition.entrySet()) {
            Set<? extends HostEdge> labelSet = g.edgeSet(entry.getKey());
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
