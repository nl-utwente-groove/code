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
 * $Id: ConditionSearchPlanFactory.java,v 1.23 2008-02-05 13:43:26 rensink Exp $
 */
package groove.match.plan;

import static groove.match.SearchEngine.SearchMode.NORMAL;
import groove.algebra.AlgebraFamily;
import groove.automaton.RegExpr;
import groove.grammar.Condition;
import groove.grammar.Condition.Op;
import groove.grammar.EdgeEmbargo;
import groove.grammar.GrammarProperties;
import groove.grammar.rule.Anchor;
import groove.grammar.rule.AnchorKey;
import groove.grammar.rule.DefaultRuleNode;
import groove.grammar.rule.LabelVar;
import groove.grammar.rule.OperatorNode;
import groove.grammar.rule.RuleEdge;
import groove.grammar.rule.RuleGraph;
import groove.grammar.rule.RuleLabel;
import groove.grammar.rule.RuleNode;
import groove.grammar.rule.VariableNode;
import groove.grammar.type.TypeGraph;
import groove.grammar.type.TypeLabel;
import groove.grammar.type.TypeNode;
import groove.graph.EdgeRole;
import groove.graph.Label;
import groove.match.SearchEngine;
import groove.match.ValueOracle;
import groove.util.collect.Bag;
import groove.util.collect.HashBag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.TreeSet;

/**
 * Factory that adds to a graph search plan the following items the search items
 * for the simple negative conditions (edge and merge embargoes).
 * @author Arend Rensink
 * @version $Revision: 3291 $
 */
public class PlanSearchEngine extends SearchEngine {
    /**
     * Private constructor. Get the instance through
     * {@link #getInstance()}.
     * @see AlgebraFamily#getInstance(String)
     */
    private PlanSearchEngine(SearchMode searchMode) {
        this.searchMode = searchMode;
    }

    @Override
    public PlanSearchStrategy createMatcher(Condition condition, Anchor seed, ValueOracle oracle) {
        Set<AnchorKey> anchorKeys = new HashSet<AnchorKey>();
        if (condition.hasRule()) {
            anchorKeys.addAll(condition.getRule().getAnchor());
        }
        PlanData planData = new PlanData(condition, this.searchMode);
        if (seed == null) {
            seed = new Anchor();
        }
        SearchPlan plan = planData.getPlan(seed);
        for (AbstractSearchItem item : plan) {
            boolean relevant = anchorKeys.removeAll(item.bindsNodes());
            relevant |= anchorKeys.removeAll(item.bindsEdges());
            relevant |= anchorKeys.removeAll(item.bindsVars());
            // universal conditions need to find all matches, so everything is relevant
            relevant |= condition.getOp() == Op.FORALL;
            // universal conditions may result in a tree match that does
            // not have any proof; therefore they must be considered relevant
            // in order not to miss matches
            relevant |=
                item instanceof ConditionSearchItem
                    && ((ConditionSearchItem) item).getCondition().getOp() == Op.FORALL;
            // EZ says: when working in minimal search mode, everything is
            // relevant. This is true even if the sub-tree is formed only of
            // readers and NACs because the pre-match checks on multiplicities
            // may fail and we want to properly backtrack and continue the search.
            relevant |= this.searchMode == SearchMode.MINIMAL;
            item.setRelevant(relevant);
        }
        PlanSearchStrategy result = new PlanSearchStrategy(this, plan, oracle);
        if (PRINT) {
            System.out.print(String.format("%nPlan for %s, seed %s:%n    %s", condition.getName(),
                seed, result));
            System.out.printf("%n    Dependencies & Relevance: [");
            for (int i = 0; i < plan.size(); i++) {
                if (i > 0) {
                    System.out.print(", ");
                }
                System.out.printf("%d%s: %s", i, plan.get(i).isRelevant() ? "*" : "",
                    plan.getDependency(i));
            }
            System.out.println("]");
        }
        result.setFixed();
        return result;
    }

    /** The search mode for plans created by this engine. */
    private final SearchMode searchMode;

    /** Returns an instance of this factory class.
     * @see AlgebraFamily#getInstance(String)
     */
    static public PlanSearchEngine getInstance() {
        return getInstance(SearchMode.NORMAL);
    }

    /** Returns an instance of this factory class, for a given search mode.
     * @see AlgebraFamily#getInstance(String)
     */
    static public PlanSearchEngine getInstance(SearchMode searchMode) {
        if (instance == null) {
            instance = new EnumMap<SearchMode,PlanSearchEngine>(SearchMode.class);
            for (SearchMode mode : SearchMode.values()) {
                instance.put(mode, new PlanSearchEngine(mode));
            }
        }
        return instance.get(searchMode);
    }

    static private Map<SearchMode,PlanSearchEngine> instance;

    /** Flag to control search plan printing. */
    static private final boolean PRINT = false;

    /**
     * Plan data extension based on a graph condition. Additionally it takes the
     * control labels of the condition into account.
     * @author Arend Rensink
     * @version $Revision $
     */
    private static class PlanData extends Observable implements Comparator<SearchItem> {
        /**
         * Constructs a fresh instance of the plan data, based on a given set of
         * system properties, and sets of already matched nodes and edges.
         * @param condition the graph condition for which we develop the search
         *        plan
         */
        PlanData(Condition condition, SearchMode searchMode) {
            this.condition = condition;
            this.searchMode = searchMode;
            this.typeGraph = condition.getTypeGraph();
            this.remainingNodes = new LinkedHashSet<RuleNode>();
            this.remainingEdges = new LinkedHashSet<RuleEdge>();
            this.remainingVars = new LinkedHashSet<LabelVar>();
            if (condition.hasPattern()) {
                RuleGraph graph = condition.getPattern();
                // compute the set of remaining (unmatched) nodes
                this.remainingNodes.addAll(graph.nodeSet());
                // compute the set of remaining (unmatched) edges and variables
                this.remainingEdges.addAll(graph.edgeSet());
                this.remainingVars.addAll(graph.varSet());
                this.algebraFamily = condition.getGrammarProperties().getAlgebraFamily();
            } else {
                this.algebraFamily = AlgebraFamily.DEFAULT;
            }
        }

        private void testUsed() {
            if (this.used) {
                throw new IllegalStateException("Method getPlan() was already called");
            } else {
                this.used = true;
            }
        }

        private boolean getInjectivity() {
            switch (this.searchMode) {
            case MINIMAL:
            case REGEXPR:
                return false;
            case REVERSE:
                return true;
            case NORMAL:
                return this.condition.isInjective();
            default:
                assert false;
                return false;
            }
        }

        /**
         * Creates and returns a search plan on the basis of the given data.
         * @param seed the pre-matched subgraph; non-{@code null}
         */
        public SearchPlan getPlan(Anchor seed) {
            testUsed();
            boolean injective = getInjectivity();
            SearchPlan result = new SearchPlan(this.condition, seed, injective);
            Collection<AbstractSearchItem> items = computeSearchItems(seed);
            while (!items.isEmpty()) {
                AbstractSearchItem bestItem = Collections.max(items, this);
                result.add(bestItem);
                this.remainingEdges.removeAll(bestItem.bindsEdges());
                this.remainingNodes.removeAll(bestItem.bindsNodes());
                this.remainingVars.removeAll(bestItem.bindsVars());
                // notify the observing comparators of the change
                setChanged();
                notifyObservers(bestItem);
                items.remove(bestItem);
            }
            assert this.remainingEdges.isEmpty() : String.format("Unmatched edges %s",
                this.remainingEdges);
            assert this.remainingNodes.isEmpty() : String.format("Unmatched nodes %s",
                this.remainingNodes);
            assert this.remainingVars.isEmpty() : String.format("Unmatched variables %s",
                this.remainingVars);
            return result;
        }

        /**
         * Orders search items according to the lexicographic order of the
         * available item comparators.
         */
        @Override
        final public int compare(SearchItem o1, SearchItem o2) {
            int result = 0;
            Iterator<Comparator<SearchItem>> comparatorIter = getComparators().iterator();
            while (result == 0 && comparatorIter.hasNext()) {
                Comparator<SearchItem> next = comparatorIter.next();
                result = next.compare(o1, o2);
            }
            if (result == 0) {
                result = o1.compareTo(o2);
            }
            return result;
        }

        /**
         * Adds embargo and injection search items to the super result.
         * @param seed the pre-matched subgraph
         */
        private Collection<AbstractSearchItem> computeSearchItems(Anchor seed) {
            Collection<AbstractSearchItem> result = new ArrayList<AbstractSearchItem>();
            if (this.condition.hasPattern()) {
                result.addAll(computePatternSearchItems(seed));
            }
            for (Condition subCondition : this.condition.getSubConditions()) {
                AbstractSearchItem item = null;
                if (subCondition.isCompatible(this.searchMode)) {
                    if (subCondition instanceof EdgeEmbargo) {
                        item = createEdgeEmbargoItem((EdgeEmbargo) subCondition);
                    } else {
                        item = new ConditionSearchItem(subCondition);
                    }
                } else {
                    if (this.searchMode == SearchMode.REVERSE && subCondition.isReversable()) {
                        item = new ConditionSearchItem(subCondition.reverse());
                    }
                }
                if (item != null) {
                    result.add(item);
                }
            }
            return result;
        }

        /** Returned item may be null. */
        private AbstractSearchItem createEdgeEmbargoItem(EdgeEmbargo subCondition) {
            AbstractSearchItem item = null;
            RuleEdge embargoEdge = subCondition.getEmbargoEdge();
            if (!embargoEdge.label().isEmpty()) {
                AbstractSearchItem edgeSearchItem = createEdgeSearchItem(embargoEdge);
                item = createNegatedSearchItem(edgeSearchItem);
            } else {
                if (!this.condition.isInjective()) {
                    item = new EqualitySearchItem(embargoEdge, false);
                }
            }
            return item;
        }

        /**
         * Adds embargo and injection search items to the super result.
         * @param seed the set of pre-matched nodes
         */
        Collection<AbstractSearchItem> computePatternSearchItems(Anchor seed) {
            Collection<AbstractSearchItem> result = new ArrayList<AbstractSearchItem>();
            Map<RuleNode,RuleNode> unmatchedNodes = new LinkedHashMap<RuleNode,RuleNode>();
            for (RuleNode node : this.remainingNodes) {
                unmatchedNodes.put(node, node);
            }
            Set<RuleEdge> unmatchedEdges = new LinkedHashSet<RuleEdge>(this.remainingEdges);
            // first a single search item for the pre-matched elements
            if (seed == null) {
                seed = new Anchor();
            }
            Set<RuleNode> constraint = new HashSet<RuleNode>();
            if (!seed.isEmpty()) {
                AbstractSearchItem seedItem = new SeedSearchItem(seed);
                result.add(seedItem);
                // nodes in the seed and the currently matched graph my be equal
                // but differ in their type constraints
                for (RuleNode seedNode : seedItem.bindsNodes()) {
                    RuleNode myNode = unmatchedNodes.get(seedNode);
                    if (seedNode.stronglyEquals(myNode)) {
                        unmatchedNodes.remove(seedNode);
                    } else {
                        constraint.add(myNode);
                    }
                }
                unmatchedEdges.removeAll(seedItem.bindsEdges());
            }
            // match all the value nodes and guard-carrying nodes explicitly
            Iterator<RuleNode> unmatchedNodeIter = unmatchedNodes.keySet().iterator();
            while (unmatchedNodeIter.hasNext()) {
                RuleNode node = unmatchedNodeIter.next();
                if (node instanceof VariableNode && ((VariableNode) node).getConstant() != null
                    || !node.getTypeGuards().isEmpty() || constraint.contains(node)) {
                    AbstractSearchItem nodeItem = createNodeSearchItem(node);
                    if (nodeItem != null) {
                        result.add(nodeItem);
                        unmatchedNodeIter.remove();
                    }
                }
            }
            // then a search item per remaining edge
            for (RuleEdge edge : unmatchedEdges) {
                AbstractSearchItem edgeItem = createEdgeSearchItem(edge);
                if (edgeItem != null) {
                    result.add(edgeItem);
                    // end nodes are only matched if the item is not negated and
                    // types are not specialised
                    RuleNode source = edge.source();
                    if (edgeItem.bindsNodes().contains(source) && edge.getType() != null
                        && edge.getType().source() == source.getType()) {
                        unmatchedNodes.remove(source);
                    }
                    RuleNode target = edge.target();
                    if (edgeItem.bindsNodes().contains(target) && edge.getType() != null
                        && edge.getType().target() == target.getType()) {
                        unmatchedNodes.remove(target);
                    }
                }
            }
            // finally a search item per remaining node
            for (RuleNode node : unmatchedNodes.keySet()) {
                AbstractSearchItem nodeItem = createNodeSearchItem(node);
                if (nodeItem != null) {
                    assert !(node instanceof VariableNode) || ((VariableNode) node).hasConstant()
                        || this.algebraFamily.supportsSymbolic() || seed.nodeSet().contains(node) : String.format(
                        "Variable node '%s' should be among anchors %s", node, seed);
                    result.add(nodeItem);
                }
            }
            return result;
        }

        /**
         * Creates the comparators for the search plan. Adds a comparator based
         * on the control labels available in the grammar, if any.
         * @return a list of comparators determining the order in which edges
         *         should be matched
         */
        Collection<Comparator<SearchItem>> computeComparators() {
            Collection<Comparator<SearchItem>> result =
                new TreeSet<Comparator<SearchItem>>(new ItemComparatorComparator());
            result.add(new NeededPartsComparator(this.remainingNodes, this.remainingVars));
            result.add(new ItemTypeComparator());
            result.add(new ConnectedPartsComparator(this.remainingNodes, this.remainingVars));
            result.add(new IndegreeComparator(this.remainingEdges));
            GrammarProperties properties = this.condition.getGrammarProperties();
            if (properties != null) {
                List<String> controlLabels = properties.getControlLabels();
                List<String> commonLabels = properties.getCommonLabels();
                result.add(new FrequencyComparator(controlLabels, commonLabels));
            }
            return result;
        }

        /**
         * Lazily creates and returns the set of search item comparators that
         * determines their priority in the search plan.
         */
        final Collection<Comparator<SearchItem>> getComparators() {
            if (this.comparators == null) {
                this.comparators = computeComparators();
                // add those comparators as listeners that implement the
                // observer interface
                for (Comparator<SearchItem> comparator : this.comparators) {
                    if (comparator instanceof Observer) {
                        addObserver((Observer) comparator);
                    }
                }
            }
            return this.comparators;
        }

        /**
         * Callback factory method for creating an edge search item.
         */
        protected AbstractSearchItem createEdgeSearchItem(RuleEdge edge) {
            AbstractSearchItem result = null;
            RuleLabel label = edge.label();
            RuleNode target = edge.target();
            RuleNode source = edge.source();
            RegExpr negOperand = label.getNegOperand();
            if (negOperand instanceof RegExpr.Empty) {
                result = new EqualitySearchItem(edge, false);
            } else if (negOperand != null) {
                RuleLabel negatedLabel = negOperand.toLabel();
                AbstractSearchItem negatedItem;
                if (negatedLabel.getRole() == EdgeRole.NODE_TYPE && !this.typeGraph.isImplicit()) {
                    TypeNode negatedType = this.typeGraph.getNode(negatedLabel);
                    negatedItem = new NodeTypeSearchItem(edge.source(), negatedType);
                } else {
                    RuleEdge negatedEdge =
                        this.condition.getFactory().createEdge(source, negatedLabel, target);
                    negatedItem = createEdgeSearchItem(negatedEdge);
                }
                result = createNegatedSearchItem(negatedItem);
                this.remainingEdges.remove(edge);
            } else if (label.getWildcardGuard() != null) {
                assert !this.typeGraph.isNodeType(edge);
                result = new VarEdgeSearchItem(edge);
            } else if (label.isEmpty()) {
                result = new EqualitySearchItem(edge, true);
            } else if (label.isSharp() || label.isAtom()) {
                result = new Edge2SearchItem(edge);
            } else {
                result = new RegExprEdgeSearchItem(edge, this.typeGraph);
            }
            return result;
        }

        /**
         * Callback factory method for creating a node search item.
         */
        protected AbstractSearchItem createNodeSearchItem(RuleNode node) {
            AbstractSearchItem result = null;
            if (node instanceof VariableNode) {
                assert this.searchMode == NORMAL;
                if (((VariableNode) node).hasConstant() || this.algebraFamily.supportsSymbolic()) {
                    result = new ValueNodeSearchItem((VariableNode) node, this.algebraFamily);
                }
                // otherwise, the node must be among the count nodes of
                // the subconditions
            } else if (node instanceof OperatorNode) {
                assert this.searchMode == NORMAL;
                result = new OperatorNodeSearchItem((OperatorNode) node, this.algebraFamily);
            } else {
                assert node instanceof DefaultRuleNode;
                result = new NodeTypeSearchItem(node);
            }
            return result;
        }

        /**
         * Callback factory method for a negated search item.
         * @param inner the internal search item which this one negates
         * @return an instance of {@link NegatedSearchItem}
         */
        protected NegatedSearchItem createNegatedSearchItem(SearchItem inner) {
            return new NegatedSearchItem(inner);
        }

        /**
         * The set of nodes to be matched.
         */
        private final Set<RuleNode> remainingNodes;
        /**
         * The set of edges to be matched.
         */
        private final Set<RuleEdge> remainingEdges;
        /**
         * The set of variables to be matched.
         */
        private final Set<LabelVar> remainingVars;
        /** The label store containing the subtype relation. */
        private final TypeGraph typeGraph;
        /** 
         * The algebra family to be used for algebraic operations.
         * If {@code null}, the default will be used.
         * @see AlgebraFamily#getInstance(String)
         */
        private final AlgebraFamily algebraFamily;

        /**
         * The comparators used to determine the order in which the edges should
         * be matched.
         */
        private Collection<Comparator<SearchItem>> comparators;
        /**
         * Flag determining if {@link #getPlan(Anchor)} was
         * already called.
         */
        private boolean used;

        /** The search mode for the plan. */
        private final SearchMode searchMode;
        /** The graph condition for which we develop the plan. */
        private final Condition condition;
    }

    /**
     * Edge comparator based on the number of incoming edges of the source and
     * target nodes. An edge is better if it has lower source indegree, or
     * failing that, higher target indegree. The idea is that the "roots" of a
     * graph (those starting in nodes with small indegree) are likely to give a
     * better immediate reduction of the number of possible matches. For the
     * outdegree the reasoning is that the more constraints a matching causes,
     * the better. The class is an observer in order to be able to maintain the
     * indegrees.
     * @author Arend Rensink
     * @version $Revision $
     */
    static class IndegreeComparator implements Comparator<SearchItem>, Observer {
        /**
         * Constructs a comparator on the basis of a given set of unmatched
         * edges.
         */
        IndegreeComparator(Set<? extends RuleEdge> remainingEdges) {
            // compute indegrees
            Bag<RuleNode> indegrees = new HashBag<RuleNode>();
            for (RuleEdge edge : remainingEdges) {
                if (!edge.target().equals(edge.source())) {
                    indegrees.add(edge.target());
                }
            }
            this.indegrees = indegrees;
        }

        /**
         * Favours the edge with the lowest source indegree, or, failing that,
         * the highest target indegree.
         */
        @Override
        public int compare(SearchItem item1, SearchItem item2) {
            int result = 0;
            if (item1 instanceof Edge2SearchItem && item2 instanceof Edge2SearchItem) {
                RuleEdge first = ((Edge2SearchItem) item1).getEdge();
                RuleEdge second = ((Edge2SearchItem) item2).getEdge();
                // first test for the indegree of the source (lower = better)
                result = indegree(second.source()) - indegree(first.source());
                if (result == 0) {
                    // now test for the indegree of the target (higher = better)
                    result = indegree(first.target()) - indegree(second.target());
                }
            }
            return result;
        }

        /**
         * This method is called when a new edge is scheduled. It decreases the
         * indegree of all the edge target.
         */
        @Override
        public void update(Observable o, Object arg) {
            if (arg instanceof Edge2SearchItem) {
                RuleEdge selected = ((Edge2SearchItem) arg).getEdge();
                this.indegrees.remove(selected.target());
            }
        }

        /**
         * Returns the indegree of a given node.
         */
        private int indegree(RuleNode node) {
            return this.indegrees.multiplicity(node);
        }

        /**
         * The indegrees.
         */
        private final Bag<RuleNode> indegrees;
    }

    /**
     * Search item comparator that gives least priority to items of which some
     * needed nodes or variables have not yet been matched. Among those of which
     * all needed parts have been matched, the comparator prefers those of which
     * the most bound parts have also been matched.
     * @author Arend Rensink
     * @version $Revision: 3291 $
     */
    static class NeededPartsComparator implements Comparator<SearchItem> {
        NeededPartsComparator(Set<RuleNode> remainingNodes, Set<LabelVar> remainingVars) {
            this.remainingNodes = remainingNodes;
            this.remainingVars = remainingVars;
        }

        /**
         * First compares the need count (higher is better), then the bind count
         * (lower is better).
         */
        @Override
        public int compare(SearchItem o1, SearchItem o2) {
            return getNeedCount(o1) - getNeedCount(o2);
        }

        /**
         * Returns 0 if the item needs a node or variable that has not yet been
         * matched, 1 if all needed parts have been matched.
         */
        private int getNeedCount(SearchItem item) {
            boolean missing = false;
            Iterator<RuleNode> neededNodeIter = item.needsNodes().iterator();
            while (!missing && neededNodeIter.hasNext()) {
                missing = this.remainingNodes.contains(neededNodeIter.next());
            }
            Iterator<LabelVar> neededVarIter = item.needsVars().iterator();
            while (!missing && neededVarIter.hasNext()) {
                missing = this.remainingVars.contains(neededVarIter.next());
            }
            return missing ? 0 : 1;
        }

        /** The set of (as yet) unscheduled nodes. */
        private final Set<RuleNode> remainingNodes;
        /** The set of (as yet) unscheduled variables. */
        private final Set<LabelVar> remainingVars;
    }

    /**
     * Search item comparator that gives higher priority to items of which more
     * parts have been matched.
     * @author Arend Rensink
     * @version $Revision: 3291 $
     */
    static class ConnectedPartsComparator implements Comparator<SearchItem> {
        ConnectedPartsComparator(Set<RuleNode> remainingNodes, Set<LabelVar> remainingVars) {
            this.remainingNodes = remainingNodes;
            this.remainingVars = remainingVars;
        }

        /**
         * Compares the connect count (higher is better).
         */
        @Override
        public int compare(SearchItem o1, SearchItem o2) {
            return getConnectCount(o1) - getConnectCount(o2);
        }

        /**
         * Returns the number of nodes and variables bound by the item that have
         * not yet been matched. More unmatched parts means more
         * non-determinism, so the lower the better.
         */
        private int getConnectCount(SearchItem item) {
            int result = 0;
            for (RuleNode node : item.bindsNodes()) {
                if (!this.remainingNodes.contains(node)) {
                    result++;
                }
            }
            for (LabelVar var : item.bindsVars()) {
                if (!this.remainingVars.contains(var)) {
                    result++;
                }
            }
            return result;
        }

        /** The set of (as yet) unscheduled nodes. */
        private final Set<RuleNode> remainingNodes;
        /** The set of (as yet) unscheduled variables. */
        private final Set<LabelVar> remainingVars;
    }

    /**
     * Edge comparator for regular expression edges. An edge is better if it is
     * not regular, or if the automaton is not reflexive.
     * @author Arend Rensink
     * @version $Revision $
     */
    static class ItemTypeComparator implements Comparator<SearchItem> {
        /**
         * Compares two regular expression-based items, with the purpose of
         * determining which one should be scheduled first. In order from worst
         * to best:
         * <ul>
         * <li> {@link NodeTypeSearchItem}s
         * <li> {@link ConditionSearchItem}s
         * <li> {@link RegExprEdgeSearchItem}s
         * <li> {@link VarEdgeSearchItem}s
         * <li> {@link Edge2SearchItem}s
         * <li> {@link EqualitySearchItem}s
         * <li> {@link NegatedSearchItem}s
         * <li> {@link OperatorNodeSearchItem}s
         * <li> {@link ValueNodeSearchItem}s
         * <li> {@link SeedSearchItem}s
         * </ul>
         */
        @Override
        public int compare(SearchItem o1, SearchItem o2) {
            return getRating(o1) - getRating(o2);
        }

        /**
         * Computes a rating for a search item from its type. A higher rating is
         * better.
         */
        int getRating(SearchItem item) {
            int result = 0;
            Class<?> itemClass = item.getClass();
            if (itemClass == RegExprEdgeSearchItem.class
                && ((RegExprEdgeSearchItem) item).getEdgeExpr().isAcceptsEmptyWord()) {
                return result;
            }
            if (itemClass == NodeTypeSearchItem.class) {
                return result;
            }
            result++;
            if (itemClass == ConditionSearchItem.class) {
                return result;
            }
            result++;
            if (itemClass == RegExprEdgeSearchItem.class) {
                return result;
            }
            result++;
            if (itemClass == VarEdgeSearchItem.class) {
                return result;
            }
            result++;
            if (itemClass == Edge2SearchItem.class) {
                return result;
            }
            result++;
            if (itemClass == EqualitySearchItem.class) {
                return result;
            }
            result++;
            if (itemClass == NegatedSearchItem.class) {
                return result;
            }
            result++;
            if (itemClass == OperatorNodeSearchItem.class) {
                return result;
            }
            result++;
            if (itemClass == ValueNodeSearchItem.class) {
                return result;
            }
            result++;
            if (itemClass == SeedSearchItem.class) {
                return result;
            }
            throw new IllegalArgumentException(String.format("Unrecognised search item %s", item));
        }
    }

    /**
     * Edge comparator on the basis of lists of high- and low-priority labels.
     * Preference is given to labels occurring early in this list.
     * @author Arend Rensink
     * @version $Revision $
     */
    static class FrequencyComparator implements Comparator<SearchItem> {
        /**
         * Constructs a comparator on the basis of two lists of labels. The
         * first list contains high-priority labels, in the order of decreasing
         * priority; the second list low-priority labels, in order of increasing
         * priority. Labels not in either list have intermediate priority and
         * are ordered alphabetically.
         * @param rare high-priority labels, in order of decreasing priority;
         *        may be <code>null</code>
         * @param common low-priority labels, in order of increasing priority;
         *        may be <code>null</code>
         */
        FrequencyComparator(List<String> rare, List<String> common) {
            this.priorities = new HashMap<Label,Integer>();
            if (rare != null) {
                for (int i = 0; i < rare.size(); i++) {
                    Label label = TypeLabel.createLabel(rare.get(i));
                    this.priorities.put(label, rare.size() - i);
                }
            }
            if (common != null) {
                for (int i = 0; i < common.size(); i++) {
                    Label label = TypeLabel.createLabel(common.get(i));
                    this.priorities.put(label, i - common.size());
                }
            }
        }

        /**
         * Favours the edge occurring earliest in the high-priority labels, or
         * latest in the low-priority labels. In case of equal priority,
         * alphabetical ordering is used.
         */
        @Override
        public int compare(SearchItem first, SearchItem second) {
            if (first instanceof Edge2SearchItem && second instanceof Edge2SearchItem) {
                Label firstLabel = ((Edge2SearchItem) first).getEdge().label();
                Label secondLabel = ((Edge2SearchItem) second).getEdge().label();
                // compare edge priorities
                return getEdgePriority(firstLabel) - getEdgePriority(secondLabel);
            } else {
                return 0;
            }
        }

        /**
         * Returns the priority of an edge, judged by its label.
         */
        private int getEdgePriority(Label edgeLabel) {
            Integer result = this.priorities.get(edgeLabel);
            if (result == null) {
                return 0;
            } else {
                return result;
            }
        }

        /**
         * The priorities assigned to labels, on the basis of the list of labels
         * passed in at construction time.
         */
        private final Map<Label,Integer> priorities;
    }

    /**
     * Comparator determining the ordering in which the search item comparators
     * should be applied. Comparators will be applied in increating order, so
     * the comparators should be ordered in decreasing priority.
     * @author Arend Rensink
     * @version $Revision: 3291 $
     */
    static class ItemComparatorComparator implements Comparator<Comparator<SearchItem>> {
        /** Empty constructor with the correct visibility. */
        ItemComparatorComparator() {
            // empty
        }

        /**
         * Returns the difference in ratings between the two comparators. This
         * means lower-rated comparators are ordered first.
         */
        @Override
        public int compare(Comparator<SearchItem> o1, Comparator<SearchItem> o2) {
            return getRating(o1) - getRating(o2);
        }

        /**
         * Comparators are rated as follows, in increasing order:
         * <ul>
         * <li> {@link NeededPartsComparator}
         * <li> {@link ItemTypeComparator}
         * <li> {@link ConnectedPartsComparator}
         * <li> {@link FrequencyComparator}
         * <li> {@link IndegreeComparator}
         * </ul>
         */
        private int getRating(Comparator<SearchItem> comparator) {
            int result = 0;
            Class<?> compClass = comparator.getClass();
            if (compClass == NeededPartsComparator.class) {
                return result;
            }
            result++;
            if (compClass == ItemTypeComparator.class) {
                return result;
            }
            result++;
            if (compClass == ConnectedPartsComparator.class) {
                return result;
            }
            result++;
            if (compClass == FrequencyComparator.class) {
                return result;
            }
            result++;
            if (compClass == IndegreeComparator.class) {
                return result;
            }
            throw new IllegalArgumentException(String.format("Unknown comparator class %s",
                compClass));
        }
    }
}
