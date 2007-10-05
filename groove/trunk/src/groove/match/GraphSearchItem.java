/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id: GraphSearchItem.java,v 1.1 2007-10-05 08:31:44 rensink Exp $
 */
package groove.match;

import groove.graph.Node;
import groove.graph.NodeEdgeHashMap;
import groove.graph.NodeEdgeMap;
import groove.match.SearchPlanStrategy.Search;
import groove.trans.ExistsCondition;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Search item to test for the presence of an entire sub-graph.
 * @author Arend Rensink
 * @version $Revision $
 */
public class GraphSearchItem extends AbstractSearchItem {
    /**
     * 
     */
    public GraphSearchItem(ExistsCondition condition) {
        this.condition = condition;
        this.patternMap = condition.getPatternMap();
        this.neededNodes = patternMap.nodeMap().keySet();
    }

    @Override
    public Collection<Node> needsNodes() {
        return neededNodes;
    }

    @Override
    int getRating() {
        return - condition.getTarget().nodeCount() - patternMap.size();
    }

    public void activate(SearchPlanStrategy strategy) {
        nodeIxMap = new HashMap<Node,Integer>();
        for (Node node: neededNodes) {
            nodeIxMap.put(node, strategy.getNodeIx(node));
        }
    }
    
    public Record getRecord(Search search) {
        return new GraphRecord(search);
    }

    private final ExistsCondition condition;
    private final NodeEdgeMap patternMap;
    private final Set<Node> neededNodes;
    private Map<Node,Integer> nodeIxMap;

    /**
     * @author Arend Rensink
     * @version $Revision $
     */
    public class GraphRecord extends SingularRecord {
        public GraphRecord(Search search) {
            super(search);
        }

        @Override
        boolean set() {
            NodeEdgeMap contextMap = new NodeEdgeHashMap();
            for (Map.Entry<Node,Integer> nodeIxEntry: nodeIxMap.entrySet()) {
                contextMap.putNode(nodeIxEntry.getKey(), search.getNode(nodeIxEntry.getValue()));
            }
            return condition.getMatchIter(host, contextMap).hasNext();
        }
    }    
}
