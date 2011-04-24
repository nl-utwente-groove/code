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

import groove.algebra.Algebra;
import groove.algebra.AlgebraFamily;
import groove.graph.algebra.ValueNode;
import groove.match.SearchPlanStrategy.Search;
import groove.rel.LabelVar;
import groove.rel.VarSupport;
import groove.trans.Condition;
import groove.trans.ForallCondition;
import groove.trans.HostNode;
import groove.trans.RuleEdge;
import groove.trans.RuleGraphMorphism;
import groove.trans.RuleNode;
import groove.trans.RuleToHostMap;
import groove.trans.SystemProperties;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
     * @param conditionIx the index of the condition in the search
     */
    public ConditionSearchItem(Condition condition, int conditionIx) {
        this.condition = condition;
        SystemProperties properties = condition.getSystemProperties();
        this.matcher =
            SearchPlanEngine.getInstance(properties.isInjective(),
                properties.getAlgebraFamily()).createMatcher(condition);
        this.intAlgebra =
            AlgebraFamily.getInstance(properties.getAlgebraFamily()).getAlgebra(
                "int");
        this.rootMap = condition.getRootMap();
        this.neededNodes = this.rootMap.nodeMap().keySet();
        this.neededEdges = this.rootMap.edgeMap().keySet();
        this.neededVars = new HashSet<LabelVar>();
        for (RuleEdge edge : this.rootMap.edgeMap().keySet()) {
            this.neededVars.addAll(VarSupport.getAllVars(edge));
        }
        this.positive =
            (condition instanceof ForallCondition)
                && ((ForallCondition) condition).isPositive();
        this.countNode =
            condition instanceof ForallCondition
                    ? ((ForallCondition) condition).getCountNode() : null;
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
        this.edgeIxMap = new HashMap<RuleEdge,Integer>();
        for (RuleEdge edge : this.neededEdges) {
            this.edgeIxMap.put(edge, strategy.getEdgeIx(edge));
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
        return new NewForallRecord(search);
    }

    @Override
    public String toString() {
        return String.format("Universal condition %s", this.matcher.getPlan());
    }

    /** The graph condition that should be matched by this search item. */
    final Condition condition;
    /** The matcher for the condition. */
    final SearchPlanStrategy matcher;
    /** The algebra used for integers. */
    final Algebra<?> intAlgebra;
    /** The count node of the universal condition, if any. */
    final RuleNode countNode;
    /** Flag indicating that the condition must be matched at least once. */
    final boolean positive;
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
    /** The source edges of the root map. */
    private final Set<RuleEdge> neededEdges;
    /** The variables occurring in edges of the root map. */
    private final Set<LabelVar> neededVars;
    /** The set containing the count node of the universal condition, if any. */
    private final Set<RuleNode> boundNodes;
    /** Mapping from the needed nodes to indices in the matcher. */
    Map<RuleNode,Integer> nodeIxMap;
    /** Mapping from the needed nodes to indices in the matcher. */
    Map<RuleEdge,Integer> edgeIxMap;
    /** Mapping from the needed nodes to indices in the matcher. */
    Map<LabelVar,Integer> varIxMap;

    /**
     * Search record for a graph condition.
     */
    private class NewForallRecord extends SingularRecord {
        /** Constructs a record for a given search. */
        public NewForallRecord(Search search) {
            super(search);
        }

        @Override
        boolean find() {
            boolean result = true;
            if (ConditionSearchItem.this.preCounted) {
                HostNode countImage =
                    this.search.getNode(ConditionSearchItem.this.countNodeIx);
                this.preCount =
                    Integer.parseInt(((ValueNode) countImage).getSymbol());
            }
            RuleToHostMap contextMap =
                this.host.getFactory().createRuleToHostMap();
            for (Map.Entry<RuleNode,Integer> nodeIxEntry : ConditionSearchItem.this.nodeIxMap.entrySet()) {
                contextMap.putNode(nodeIxEntry.getKey(),
                    this.search.getNode(nodeIxEntry.getValue()));
            }
            for (Map.Entry<RuleEdge,Integer> edgeIxEntry : ConditionSearchItem.this.edgeIxMap.entrySet()) {
                contextMap.putEdge(edgeIxEntry.getKey(),
                    this.search.getEdge(edgeIxEntry.getValue()));
            }
            for (Map.Entry<LabelVar,Integer> varIxEntry : ConditionSearchItem.this.varIxMap.entrySet()) {
                contextMap.putVar(varIxEntry.getKey(),
                    this.search.getVar(varIxEntry.getValue()));
            }
            List<TreeMatch> matches =
                ConditionSearchItem.this.matcher.findAll(this.host, contextMap,
                    null);
            if (ConditionSearchItem.this.positive && matches.size() == 0) {
                result = false;
            } else if (ConditionSearchItem.this.preCounted) {
                result = matches.size() != this.preCount;
            }
            if (result) {
                this.match = matches;
                if (ConditionSearchItem.this.countNode != null) {
                    this.countImage =
                        this.host.getFactory().createNode(
                            ConditionSearchItem.this.intAlgebra,
                            "" + matches.size());
                }
                result = write();
            } else {
                this.match = null;
            }
            return result;
        }

        @Override
        boolean write() {
            boolean result = true;
            if (this.countImage != null) {
                result =
                    this.search.putNode(ConditionSearchItem.this.countNodeIx,
                        this.countImage);
            }
            if (result) {
                result =
                    this.search.putSubMatch(ConditionSearchItem.this.forallIx,
                        this.match);
            }
            return result;
        }

        @Override
        void erase() {
            if (this.countImage != null) {
                this.search.putNode(ConditionSearchItem.this.countNodeIx, null);
            }
            this.search.putSubMatch(ConditionSearchItem.this.forallIx, null);
        }

        @Override
        public String toString() {
            return "Match of " + ConditionSearchItem.this.toString();
        }

        /** The pre-matched subcondition count. */
        private int preCount;
        /** The actual subcondition count. */
        private ValueNode countImage;
        /** The matches found for the condition. */
        private Collection<TreeMatch> match;
    }
}
