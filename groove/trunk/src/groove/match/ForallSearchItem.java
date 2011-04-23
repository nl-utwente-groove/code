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

import groove.graph.algebra.ValueNode;
import groove.match.SearchPlanStrategy.Search;
import groove.rel.LabelVar;
import groove.rel.VarSupport;
import groove.trans.AbstractCondition;
import groove.trans.CompositeMatch;
import groove.trans.ForallCondition;
import groove.trans.HostNode;
import groove.trans.RuleEdge;
import groove.trans.RuleGraphMorphism;
import groove.trans.RuleNode;
import groove.trans.RuleToHostMap;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Search item to test for the satisfaction of a graph condition.
 * @author Arend Rensink
 * @version $Revision $
 */
class ForallSearchItem extends AbstractSearchItem {
    /**
     * Constructs a search item for a given condition.
     * @param condition the condition to be matched
     * @param conditionIx the index of the condition in the search
     */
    public ForallSearchItem(ForallCondition condition, int conditionIx) {
        this.condition = condition;
        this.rootMap = condition.getRootMap();
        this.neededNodes = this.rootMap.nodeMap().keySet();
        this.neededVars = new HashSet<LabelVar>();
        for (RuleEdge edge : this.rootMap.edgeMap().keySet()) {
            this.neededVars.addAll(VarSupport.getAllVars(edge));
        }
        this.countNode = condition.getCountNode();
        this.boundNodes =
            this.countNode == null ? Collections.<RuleNode>emptySet()
                    : Collections.singleton(this.countNode);
        this.forallIx = conditionIx;
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
    public Collection<RuleNode> bindsNodes() {
        return this.boundNodes;
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
        this.varIxMap = new HashMap<LabelVar,Integer>();
        for (LabelVar var : this.neededVars) {
            this.varIxMap.put(var, strategy.getVarIx(var));
        }
        if (this.countNode != null) {
            this.preCounted = strategy.isNodeFound(this.countNode);
            this.countNodeIx = strategy.getNodeIx(this.countNode);
        }
    }

    public Record createRecord(Search search) {
        return new ForallRecord(search);
    }

    @Override
    public String toString() {
        return String.format(
            "Universal condition %s",
            ((SearchPlanStrategy) ((AbstractCondition<?>) this.condition).getMatcher()).getPlan());
    }

    /** The graph condition that should be matched by this search item. */
    final ForallCondition condition;
    /** The count node of the universal condition, if any. */
    final RuleNode countNode;
    /** The index of the condition in the search. */
    final int forallIx;
    /** Flag indicating if the match count is predetermined. */
    boolean preCounted;
    /** The index of the count node (if any). */
    int countNodeIx = -1;
    /** The root map of the graph condition. */
    private final RuleGraphMorphism rootMap;
    /** The source nodes of the root map. */
    private final Set<RuleNode> neededNodes;
    /** The variables occurring in edges of the root map. */
    private final Set<LabelVar> neededVars;
    /** The set containing the count node of the universal condition, if any. */
    private final Set<RuleNode> boundNodes;
    /** Mapping from the needed nodes to indices in the matcher. */
    Map<RuleNode,Integer> nodeIxMap;
    /** Mapping from the needed nodes to indices in the matcher. */
    Map<LabelVar,Integer> varIxMap;

    /**
     * Search record for a graph condition.
     */
    private class ForallRecord extends MultipleRecord<CompositeMatch> {
        /** Constructs a record for a given search. */
        public ForallRecord(Search search) {
            super(search);
        }

        @Override
        void init() {
            if (ForallSearchItem.this.preCounted) {
                HostNode countImage =
                    this.search.getNode(ForallSearchItem.this.countNodeIx);
                this.count =
                    Integer.parseInt(((ValueNode) countImage).getSymbol());
            }
            RuleToHostMap contextMap =
                this.host.getFactory().createRuleToHostMap();
            for (Map.Entry<RuleNode,Integer> nodeIxEntry : ForallSearchItem.this.nodeIxMap.entrySet()) {
                contextMap.putNode(nodeIxEntry.getKey(),
                    this.search.getNode(nodeIxEntry.getValue()));
            }
            for (Map.Entry<LabelVar,Integer> varIxEntry : ForallSearchItem.this.varIxMap.entrySet()) {
                contextMap.putVar(varIxEntry.getKey(),
                    this.search.getVar(varIxEntry.getValue()));
            }
            this.imageIter =
                ForallSearchItem.this.condition.getAllMatches(this.host,
                    contextMap).iterator();
        }

        @Override
        boolean write(CompositeMatch image) {
            boolean result = true;
            if (ForallSearchItem.this.preCounted) {
                result = image.getSubMatches().size() == this.count;
            } else if (ForallSearchItem.this.countNode != null) {
                ValueNode countImage =
                    this.host.getFactory().createNode(
                        ForallSearchItem.this.condition.getIntAlgebra(),
                        "" + image.getSubMatches().size());
                result =
                    this.search.putNode(ForallSearchItem.this.countNodeIx,
                        countImage);
            }
            if (result) {
                result =
                    this.search.putForallMatch(ForallSearchItem.this.forallIx,
                        image);
            }
            return result;
        }

        @Override
        void erase() {
            if (!ForallSearchItem.this.preCounted
                && ForallSearchItem.this.countNodeIx >= 0) {
                this.search.putNode(ForallSearchItem.this.countNodeIx, null);
            }
            this.search.putForallMatch(ForallSearchItem.this.forallIx, null);
        }

        @Override
        public String toString() {
            return "Match of " + ForallSearchItem.this.toString();
        }

        private int count;
    }
}
