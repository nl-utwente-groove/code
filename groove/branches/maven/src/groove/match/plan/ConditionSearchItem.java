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
package groove.match.plan;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import groove.algebra.Algebra;
import groove.algebra.AlgebraFamily;
import groove.algebra.Sort;
import groove.grammar.Condition;
import groove.grammar.Condition.Op;
import groove.grammar.GrammarProperties;
import groove.grammar.host.HostNode;
import groove.grammar.host.ValueNode;
import groove.grammar.rule.LabelVar;
import groove.grammar.rule.RuleEdge;
import groove.grammar.rule.RuleGraph;
import groove.grammar.rule.RuleNode;
import groove.grammar.rule.RuleToHostMap;
import groove.match.Matcher;
import groove.match.MatcherFactory;
import groove.match.TreeMatch;
import groove.match.plan.PlanSearchStrategy.Search;

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
    public ConditionSearchItem(Condition condition, boolean simple) {
        this.condition = condition;
        GrammarProperties properties = condition.getGrammarProperties();
        this.matcher = MatcherFactory.instance(simple)
            .createMatcher(condition);
        if (condition.hasPattern()) {
            this.intAlgebra = properties.getAlgebraFamily()
                .getAlgebra(Sort.INT);
            this.rootGraph = condition.getRoot();
            this.neededNodes = condition.getInputNodes();
            this.neededVars = this.rootGraph.varSet();
            this.positive = condition.isPositive();
            this.countNode = condition.getCountNode();
            this.boundNodes = condition.getOutputNodes();
        } else {
            this.intAlgebra = null;
            this.rootGraph = null;
            this.neededNodes = Collections.emptySet();
            this.neededVars = Collections.emptySet();
            this.positive = false;
            this.boundNodes = Collections.emptySet();
            this.countNode = null;
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
    public Collection<? extends RuleNode> bindsNodes() {
        return this.boundNodes;
    }

    @Override
    public int compareTo(SearchItem item) {
        int result = super.compareTo(item);
        if (result != 0) {
            return result;
        }
        ConditionSearchItem other = (ConditionSearchItem) item;
        return this.condition.getName()
            .compareTo(other.condition.getName());
    }

    @Override
    int getRating() {
        switch (this.condition.getOp()) {
        case EXISTS:
        case FORALL:
        case NOT:
            return -this.condition.getPattern()
                .nodeCount() - (this.rootGraph == null ? 0 : this.rootGraph.size());
        case TRUE:
            return 0;
        case FALSE:
        case AND:
        case OR:
        default:
            throw new IllegalStateException();
        }
    }

    @Override
    int computeHashCode() {
        int result = super.computeHashCode();
        return result * 31 + getCondition().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        ConditionSearchItem other = (ConditionSearchItem) obj;
        return getCondition().equals(other.getCondition());
    }

    @Override
    public boolean isTestsNodes() {
        return true;
    }

    @Override
    public void activate(PlanSearchStrategy strategy) {
        if (this.condition.getOp() != Condition.Op.NOT) {
            this.condIx = strategy.getCondIx(this.condition);
        }
        if (this.condition.hasPattern()) {
            this.nodeIxMap = new HashMap<>();
            for (RuleNode node : this.rootGraph.nodeSet()) {
                this.nodeIxMap.put(node, strategy.getNodeIx(node));
            }
            this.edgeIxMap = new HashMap<>();
            for (RuleEdge edge : this.rootGraph.edgeSet()) {
                this.edgeIxMap.put(edge, strategy.getEdgeIx(edge));
            }
            this.varIxMap = new HashMap<>();
            for (LabelVar var : this.rootGraph.varSet()) {
                this.varIxMap.put(var, strategy.getVarIx(var));
            }
            if (this.countNode != null) {
                this.preCounted = strategy.isNodeFound(this.countNode);
                this.countNodeIx = strategy.getNodeIx(this.countNode);
            }
        }
    }

    @Override
    public Record createRecord(Search search) {
        switch (this.condition.getOp()) {
        case EXISTS:
        case FORALL:
            return new QuantifierRecord(search);
        case NOT:
            return new NegConditionRecord(search);
        case TRUE:
            return new TrueRecord(search);
        case OR:
        case AND:
        case FALSE:
        default:
            throw new IllegalStateException();
        }
    }

    @Override
    public String toString() {
        return String.format("%s %s: %s",
            this.condition.getOp()
                .getName(),
            this.condition.getName(),
            ((PlanSearchStrategy) this.matcher.getSearchStrategy()).getPlan());
    }

    @Override
    void setRelevant(boolean relevant) {
        // only change to irrelevant if there are no modifying rules
        // in the condition hierarchy
        super.setRelevant(relevant || isModifying());
    }

    /**
     * Returns the condition wrapped in this search item.
     */
    public Condition getCondition() {
        return this.condition;
    }

    /** Tests if this condition or one of its subconditions is a modifying rule. */
    private boolean isModifying() {
        return isModifying(this.condition);
    }

    /** tests if a given condition or one of its subconditions is a modifying rule. */
    private boolean isModifying(Condition condition) {
        boolean result = false;
        if (condition.hasRule()) {
            result = condition.getRule()
                .isModifying();
        } else {
            for (Condition subCondition : condition.getSubConditions()) {
                if (isModifying(subCondition)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    /** The graph condition that should be matched by this search item. */
    final Condition condition;
    /** The matcher for the condition. */
    final Matcher matcher;
    /** The algebra used for integers. */
    final Algebra<?> intAlgebra;
    /** The count node of the universal condition, if any. */
    final RuleNode countNode;
    /** Flag indicating that the condition must be matched at least once. */
    final boolean positive;
    /** The index of the condition in the search. */
    int condIx;
    /** Flag indicating if the match count is predetermined. */
    boolean preCounted;
    /** The index of the count node (if any). */
    int countNodeIx = -1;
    /** The root graph of the condition. */
    private final RuleGraph rootGraph;
    /** The source nodes of the root map. */
    private final Set<RuleNode> neededNodes;
    /** The variables occurring in edges of the root map. */
    private final Set<LabelVar> neededVars;
    /** The set containing the count node of the universal condition, if any. */
    private final Set<? extends RuleNode> boundNodes;
    /** Mapping from the needed nodes to indices in the matcher. */
    Map<RuleNode,Integer> nodeIxMap;
    /** Mapping from the needed nodes to indices in the matcher. */
    Map<RuleEdge,Integer> edgeIxMap;
    /** Mapping from the needed nodes to indices in the matcher. */
    Map<LabelVar,Integer> varIxMap;

    private class TrueRecord extends SingularRecord {
        public TrueRecord(Search search) {
            super(search);
        }

        @Override
        boolean find() {
            return write();
        }

        @Override
        boolean write() {
            this.search.putSubMatch(ConditionSearchItem.this.condIx,
                new TreeMatch(ConditionSearchItem.this.condition, null));
            return true;
        }

        @Override
        void erase() {
            this.search.putSubMatch(ConditionSearchItem.this.condIx, null);
        }

        @Override
        public String toString() {
            return "Match of " + ConditionSearchItem.this.toString();
        }

    }

    /**
     * Search record for a graph condition.
     */
    abstract private class PatternRecord extends SingularRecord {
        /** Constructs a record for a given search. */
        public PatternRecord(Search search) {
            super(search);
        }

        /** Creates a context map for the condition, based on
         * the elements found so far during the search.
         */
        final RuleToHostMap createContextMap() {
            RuleToHostMap result = this.host.getFactory()
                .createRuleToHostMap();
            for (Map.Entry<RuleNode,Integer> nodeIxEntry : ConditionSearchItem.this.nodeIxMap
                .entrySet()) {
                result.putNode(nodeIxEntry.getKey(), this.search.getNode(nodeIxEntry.getValue()));
            }
            for (Map.Entry<RuleEdge,Integer> edgeIxEntry : ConditionSearchItem.this.edgeIxMap
                .entrySet()) {
                result.putEdge(edgeIxEntry.getKey(), this.search.getEdge(edgeIxEntry.getValue()));
            }
            for (Map.Entry<LabelVar,Integer> varIxEntry : ConditionSearchItem.this.varIxMap
                .entrySet()) {
                result.putVar(varIxEntry.getKey(), this.search.getVar(varIxEntry.getValue()));
            }
            return result;
        }
    }

    /**
     * Search record for a quantifier condition.
     */
    private class QuantifierRecord extends PatternRecord {
        /** Constructs a record for a given search. */
        public QuantifierRecord(Search search) {
            super(search);
        }

        @Override
        boolean find() {
            boolean result = true;
            if (ConditionSearchItem.this.preCounted) {
                HostNode countImage = this.search.getNode(ConditionSearchItem.this.countNodeIx);
                this.preCount =
                    (Integer) AlgebraFamily.DEFAULT.toValue(((ValueNode) countImage).getTerm());
            }
            RuleToHostMap contextMap = createContextMap();
            List<TreeMatch> matches =
                ConditionSearchItem.this.matcher.findAll(this.host, contextMap);
            if (ConditionSearchItem.this.condition.getOp() == Op.FORALL
                && ConditionSearchItem.this.positive && matches.isEmpty()) {
                result = false;
            } else if (ConditionSearchItem.this.preCounted) {
                result = matches.size() == this.preCount;
            } else if (ConditionSearchItem.this.countNode != null) {
                Algebra<?> intAlgebra = ConditionSearchItem.this.intAlgebra;
                this.countImage = this.host.getFactory()
                    .createNode(intAlgebra, intAlgebra.toValueFromJava(matches.size()));
            }
            if (result) {
                this.match = createMatch(matches);
                result = write();
            } else {
                this.match = null;
            }
            return result;
        }

        /** Creates a match object for a given set of pattern matches. */
        private TreeMatch createMatch(List<TreeMatch> matches) {
            boolean noMatches = matches.isEmpty();
            boolean positive = ConditionSearchItem.this.positive;
            Condition.Op op;
            switch (ConditionSearchItem.this.condition.getOp()) {
            case AND:
                op = noMatches ? Op.TRUE : Op.AND;
                break;
            case FORALL:
                op = noMatches ? (positive ? Op.FALSE : Op.TRUE) : Op.AND;
                break;
            case OR:
                op = noMatches ? Op.FALSE : Op.OR;
                break;
            case EXISTS:
                op = noMatches ? (positive ? Op.FALSE : Op.TRUE) : Op.OR;
                break;
            default:
                throw new IllegalStateException();
            }
            TreeMatch result = new TreeMatch(op, ConditionSearchItem.this.condition);
            if (!noMatches) {
                result.addSubMatches(matches);
            }
            return result;
        }

        @Override
        boolean write() {
            boolean result = true;
            if (this.countImage != null) {
                result = this.search.putNode(ConditionSearchItem.this.countNodeIx, this.countImage);
            }
            if (result) {
                result = this.search.putSubMatch(ConditionSearchItem.this.condIx, this.match);
            }
            return result;
        }

        @Override
        void erase() {
            if (this.countImage != null) {
                this.search.putNode(ConditionSearchItem.this.countNodeIx, null);
            }
            this.search.putSubMatch(ConditionSearchItem.this.condIx, null);
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
        private TreeMatch match;
    }

    /**
     * Search record for a negative graph condition.
     */
    private class NegConditionRecord extends PatternRecord {
        /** Constructs a record for a given search. */
        public NegConditionRecord(Search search) {
            super(search);
        }

        @Override
        boolean find() {
            return ConditionSearchItem.this.matcher.find(this.host, createContextMap()) == null;
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
