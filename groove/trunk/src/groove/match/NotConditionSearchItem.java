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

import groove.match.SearchPlanStrategy.Search;
import groove.rel.LabelVar;
import groove.rel.VarSupport;
import groove.trans.Condition;
import groove.trans.RuleEdge;
import groove.trans.RuleGraphMorphism;
import groove.trans.RuleNode;
import groove.trans.RuleToHostMap;
import groove.trans.SystemProperties;

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
class NotConditionSearchItem extends AbstractSearchItem {
    /**
     * Constructs a search item for a given condition.
     * @param condition the condition to be matched
     */
    public NotConditionSearchItem(Condition condition) {
        this.condition = condition;
        SystemProperties properties = condition.getSystemProperties();
        this.matcher =
            SearchPlanEngine.getInstance(properties.isInjective(),
                properties.getAlgebraFamily()).createMatcher(condition);
        this.rootMap = condition.getRootMap();
        this.neededEdges = this.rootMap.edgeMap().keySet();
        this.neededNodes = this.rootMap.nodeMap().keySet();
        this.neededVars = new HashSet<LabelVar>();
        for (RuleEdge edge : this.rootMap.edgeMap().keySet()) {
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
        return -this.condition.getPattern().nodeCount() - this.rootMap.size();
    }

    @Override
    public boolean isTestsNodes() {
        return true;
    }

    public void activate(SearchPlanStrategy strategy) {
        this.nodeIxMap = new HashMap<RuleNode,Integer>();
        for (RuleNode node : this.neededNodes) {
            this.nodeIxMap.put(node, strategy.getNodeIx(node));
        }
        this.edgeIxMap = new HashMap<RuleEdge,Integer>();
        for (RuleEdge node : this.neededEdges) {
            this.edgeIxMap.put(node, strategy.getEdgeIx(node));
        }
        this.varIxMap = new HashMap<LabelVar,Integer>();
        for (LabelVar var : this.neededVars) {
            this.varIxMap.put(var, strategy.getVarIx(var));
        }
    }

    public Record createRecord(Search search) {
        return new NotConditionRecord(search);
    }

    @Override
    public String toString() {
        return String.format("NAC %s", this.matcher.getPlan());
    }

    /** The graph condition that should be matched by this search item. */
    final Condition condition;
    final SearchPlanStrategy matcher;
    /** The root map of the graph condition. */
    private final RuleGraphMorphism rootMap;
    /** The source nodes of the root map. */
    private final Set<RuleNode> neededNodes;
    /** The source edges of the root map. */
    private final Set<RuleEdge> neededEdges;
    /** The variables occurring in edges of the root map. */
    private final Set<LabelVar> neededVars;
    /** Mapping from the needed nodes to indices in the matcher. */
    Map<RuleNode,Integer> nodeIxMap;
    /** Mapping from the needed edges to indices in the matcher. */
    Map<RuleEdge,Integer> edgeIxMap;
    /** Mapping from the needed nodes to indices in the matcher. */
    Map<LabelVar,Integer> varIxMap;

    /**
     * Search record for a graph condition.
     */
    private class NotConditionRecord extends SingularRecord {
        /** Constructs a record for a given search. */
        public NotConditionRecord(Search search) {
            super(search);
        }

        @Override
        boolean find() {
            RuleToHostMap contextMap =
                this.host.getFactory().createRuleToHostMap();
            for (Map.Entry<RuleNode,Integer> nodeIxEntry : NotConditionSearchItem.this.nodeIxMap.entrySet()) {
                contextMap.putNode(nodeIxEntry.getKey(),
                    this.search.getNode(nodeIxEntry.getValue()));
            }
            for (Map.Entry<RuleEdge,Integer> edgeIxEntry : NotConditionSearchItem.this.edgeIxMap.entrySet()) {
                contextMap.putEdge(edgeIxEntry.getKey(),
                    this.search.getEdge(edgeIxEntry.getValue()));
            }
            for (Map.Entry<LabelVar,Integer> varIxEntry : NotConditionSearchItem.this.varIxMap.entrySet()) {
                contextMap.putVar(varIxEntry.getKey(),
                    this.search.getVar(varIxEntry.getValue()));
            }
            return NotConditionSearchItem.this.matcher.find(this.host,
                contextMap, null) == null;
        }

        @Override
        boolean write() {
            // There is nothing to write
            return true;
        }

        @Override
        void erase() {
            // There is nothing to erase
        }
    }
}
