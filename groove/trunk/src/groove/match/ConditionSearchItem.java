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
 * $Id: ConditionSearchItem.java,v 1.12 2008-01-30 09:33:28 iovka Exp $
 */
package groove.match;

import groove.graph.Edge;
import groove.match.SearchPlanStrategy.Search;
import groove.rel.LabelVar;
import groove.rel.RuleToStateHashMap;
import groove.rel.RuleToStateMap;
import groove.rel.VarSupport;
import groove.trans.AbstractCondition;
import groove.trans.Condition;
import groove.trans.RuleGraphMap;
import groove.trans.RuleNode;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Search item to test for the satisfaction of a graph condition.
 * @author Arend Rensink
 * @version $Revision $
 */
class ConditionSearchItem extends AbstractSearchItem {
    /**
     * Constructs a search item for a given condition.
     * @param condition the condition to be matched
     */
    public ConditionSearchItem(Condition condition) {
        this.condition = condition;
        this.rootMap = condition.getRootMap();
        this.neededNodes = this.rootMap.nodeMap().keySet();
        this.neededVars = new HashSet<LabelVar>();
        for (Edge edge : this.rootMap.edgeMap().keySet()) {
            this.neededVars.addAll(VarSupport.getAllVars(edge));
        }
    }

    @Override
    public Collection<RuleNode> needsNodes() {
        return this.neededNodes;
    }

    @Override
    public Collection<LabelVar> needsVars() {
        return this.neededVars;
    }

    @Override
    int getRating() {
        return -this.condition.getTarget().nodeCount() - this.rootMap.size();
    }

    public void activate(SearchPlanStrategy strategy) {
        this.nodeIxMap = new HashMap<RuleNode,Integer>();
        for (RuleNode node : this.neededNodes) {
            this.nodeIxMap.put(node, strategy.getNodeIx(node));
        }
        this.varIxMap = new HashMap<LabelVar,Integer>();
        for (LabelVar var : this.neededVars) {
            this.varIxMap.put(var, strategy.getVarIx(var));
        }
    }

    public Record getRecord(Search search) {
        return new ConditionRecord(search);
    }

    @Override
    public String toString() {
        return String.format(
            "NAC %s",
            ((SearchPlanStrategy) ((AbstractCondition<?>) this.condition).getMatcher()).getPlan());
    }

    /** The graph condition that should be matched by this search item. */
    final Condition condition;
    /** The root map of the graph condition. */
    private final RuleGraphMap rootMap;
    /** The source nodes of the root map. */
    private final Set<RuleNode> neededNodes;
    /** The variables occurring in edges of the root map. */
    private final Set<LabelVar> neededVars;
    /** Mapping from the needed nodes to indices in the matcher. */
    Map<RuleNode,Integer> nodeIxMap;
    /** Mapping from the needed nodes to indices in the matcher. */
    Map<LabelVar,Integer> varIxMap;

    /**
     * Search record for a graph condition.
     */
    public class ConditionRecord extends SingularRecord {
        /** Constructs a record for a given search. */
        public ConditionRecord(Search search) {
            super(search);
        }

        @Override
        boolean set() {
            RuleToStateMap contextMap = new RuleToStateHashMap();
            for (Map.Entry<RuleNode,Integer> nodeIxEntry : ConditionSearchItem.this.nodeIxMap.entrySet()) {
                contextMap.putNode(nodeIxEntry.getKey(),
                    this.search.getNode(nodeIxEntry.getValue()));
            }
            for (Map.Entry<LabelVar,Integer> varIxEntry : ConditionSearchItem.this.varIxMap.entrySet()) {
                contextMap.putVar(varIxEntry.getKey(),
                    this.search.getVar(varIxEntry.getValue()));
            }
            return ConditionSearchItem.this.condition.getMatchIter(this.host,
                contextMap).hasNext();
        }
    }
}
