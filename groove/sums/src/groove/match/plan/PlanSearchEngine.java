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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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

import org.eclipse.jdt.annotation.NonNull;

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
import groove.grammar.type.TypeEdge;
import groove.grammar.type.TypeGraph;
import groove.grammar.type.TypeLabel;
import groove.grammar.type.TypeNode;
import groove.graph.EdgeRole;
import groove.graph.Label;
import groove.match.SearchEngine;
import groove.util.collect.Bag;
import groove.util.collect.HashBag;

/**
 * Factory that adds to a graph search plan the following items the search items
 * for the simple negative conditions (edge and merge embargoes).
 * @author Arend Rensink
 * @version $Revision$
 */
public class PlanSearchEngine extends SearchEngine {
    /**
     * Private constructor. Get the instance through
     * {@link #instance(boolean)}.
     */
    private PlanSearchEngine(boolean simple) {
        this.simple = simple;
    }

    @Override
    public PlanSearchStrategy createMatcher(Condition condition, Anchor seed) {
        Set<AnchorKey> anchorKeys = new HashSet<>();
        if (condition.hasRule()) {
            anchorKeys.addAll(condition.getRule()
                .getAnchor());
        }
        anchorKeys.addAll(condition.getOutputNodes());
        PlanData planData = new PlanData(condition, this.simple);
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
            if (item instanceof ConditionSearchItem) {
                Condition sub = ((ConditionSearchItem) item).getCondition();
                // universal conditions may result in a tree match that does
                // not have any proof; therefore they must be considered relevant
                // in order not to miss matches
                relevant |= sub.getOp() == Op.FORALL;
                // conditions with output nodes are relevant
                relevant |= !sub.getOutputNodes()
                    .isEmpty();
            }
            item.setRelevant(relevant);
        }
        PlanSearchStrategy result = new PlanSearchStrategy(this, plan);
        if (PRINT) {
            System.out.print(String.format("%nPlan for %s, seed %s:%n    %s",
                condition.getName(),
                seed,
                result));
            System.out.printf("%n    Dependencies & Relevance: [");
            for (int i = 0; i < plan.size(); i++) {
                if (i > 0) {
                    System.out.print(", ");
                }
                System.out.printf("%d%s: %s", i, plan.get(i)
                    .isRelevant() ? "*" : "", plan.getDependency(i));
            }
            System.out.println("]");
        }
        result.setFixed();
        return result;
    }

    /** Flag indicating if this engine matches simple or multi-graphs. */
    final boolean simple;

    /** Returns an instance of this factory class.
     */
    static public PlanSearchEngine instance(boolean simple) {
        PlanSearchEngine result = simple ? simpleInstance : multiInstance;
        if (result == null) {
            if (simple) {
                result = simpleInstance = new PlanSearchEngine(true);
            } else {
                result = multiInstance = new PlanSearchEngine(false);
            }
        }
        return result;
    }

    /** Matcher factory instance for simple graphs. */
    private static PlanSearchEngine simpleInstance;
    /** Matcher factory instance for multi-graphs. */
    private static PlanSearchEngine multiInstance;

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
         * @param simple flag indicating if we are matching simple graphs
         */
        PlanData(Condition condition, boolean simple) {
            this.condition = condition;
            this.simple = simple;
            this.typeGraph = condition.getTypeGraph();
            this.boundNodes = new LinkedHashSet<>();
            this.boundEdges = new LinkedHashSet<>();
            this.boundVars = new LinkedHashSet<>();
            if (condition.hasPattern()) {
                this.algebraFamily = condition.getGrammarProperties()
                    .getAlgebraFamily();
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

            return this.condition.isInjective();

        }

        /**
         * Creates and returns a search plan on the basis of the given data.
         * @param seed the pre-matched subgraph; non-{@code null}
         */
        public SearchPlan getPlan(@NonNull Anchor seed) {
            testUsed();
            boolean injective = getInjectivity();
            SearchPlan result = new SearchPlan(this.condition, seed, injective);
            Collection<AbstractSearchItem> items = computeSearchItems(seed);
            while (!items.isEmpty()) {
                AbstractSearchItem bestItem = Collections.max(items, this);
                result.add(bestItem);
                this.boundNodes.addAll(bestItem.bindsNodes());
                this.boundEdges.addAll(bestItem.bindsEdges());
                this.boundVars.addAll(bestItem.bindsVars());
                // notify the observing comparators of the change
                setChanged();
                notifyObservers(bestItem);
                items.remove(bestItem);
            }
            assert allMatched(result) : "Unmatched condition elements";
            return result;
        }

        private boolean allMatched(SearchPlan result) {
            if (!this.condition.hasPattern()) {
                return true;
            }
            RuleGraph graph = this.condition.getPattern();
            Set<RuleEdge> remainingEdges = new HashSet<>(graph.edgeSet());
            remainingEdges.removeAll(this.boundEdges);
            assert remainingEdges.isEmpty() : "Unmatched edges " + remainingEdges;
            Set<RuleNode> remainingNodes = new HashSet<>(graph.nodeSet());
            remainingNodes.removeAll(this.boundNodes);
            assert remainingNodes.isEmpty() : "Unmatched nodes " + remainingNodes;
            Set<LabelVar> remainingVars = new HashSet<>(graph.varSet());
            remainingVars.removeAll(this.boundVars);
            assert remainingVars.isEmpty() : "Unmatched variables " + remainingVars;
            return true;
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
         * Computes and returns all search items, without taking dependencies into account.
         * @param seed the pre-matched subgraph
         */
        private Collection<AbstractSearchItem> computeSearchItems(@NonNull Anchor seed) {
            Collection<AbstractSearchItem> result = new ArrayList<>();
            if (this.condition.hasPattern()) {
                result.addAll(computePatternSearchItems(seed));
            }
            for (Condition subCondition : this.condition.getSubConditions()) {
                AbstractSearchItem item = null;
                if (subCondition instanceof EdgeEmbargo) {
                    item = createEdgeEmbargoItem((EdgeEmbargo) subCondition);
                } else {
                    item = new ConditionSearchItem(subCondition, this.simple);
                }
                if (item != null) {
                    result.add(item);
                }
            }
            return result;
        }

        /**
         * Creates and returns a search item for a single edge embargo NAC.
         * @return a search item for the subcondition, or <code>null</code> if
         * the condition is already tested in some other way */
        private AbstractSearchItem createEdgeEmbargoItem(EdgeEmbargo subCondition) {
            AbstractSearchItem item = null;
            RuleEdge embargoEdge = subCondition.getEmbargoEdge();
            if (!embargoEdge.label()
                .isEmpty()) {
                AbstractSearchItem edgeSearchItem = createEdgeSearchItem(embargoEdge);
                item = createNegatedSearchItem(edgeSearchItem);
            } else {
                // if the condition is injective, local injectivity does not need to be tested
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
        Collection<AbstractSearchItem> computePatternSearchItems(@NonNull Anchor seed) {
            Collection<AbstractSearchItem> result = new ArrayList<>();
            Map<RuleNode,RuleNode> unmatchedNodes = new LinkedHashMap<>();
            RuleGraph graph = this.condition.getPattern();
            for (RuleNode node : graph.nodeSet()) {
                unmatchedNodes.put(node, node);
            }
            Set<RuleEdge> unmatchedEdges = new LinkedHashSet<>(graph.edgeSet());
            // first a single search item for the pre-matched elements
            Set<RuleNode> constraint = new HashSet<>();
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
            Iterator<RuleNode> unmatchedNodeIter = unmatchedNodes.keySet()
                .iterator();
            while (unmatchedNodeIter.hasNext()) {
                RuleNode node = unmatchedNodeIter.next();
                if (node instanceof VariableNode && ((VariableNode) node).getConstant() != null
                    || !node.getTypeGuards()
                        .isEmpty()
                    || constraint.contains(node)) {
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
                    TypeEdge edgeType = edge.getType();
                    if (edgeItem.bindsNodes()
                        .contains(source) && edgeType != null
                        && edgeType.source() == source.getType()) {
                        unmatchedNodes.remove(source);
                    }
                    RuleNode target = edge.target();
                    if (edgeItem.bindsNodes()
                        .contains(target) && edgeType != null
                        && edgeType.target() == target.getType()) {
                        unmatchedNodes.remove(target);
                    }
                }
            }
            // finally a search item per remaining node
            for (RuleNode node : unmatchedNodes.keySet()) {
                AbstractSearchItem nodeItem = createNodeSearchItem(node);
                if (nodeItem != null) {
                    assert !(node instanceof VariableNode) || ((VariableNode) node).hasConstant()
                        || this.algebraFamily.supportsSymbolic() || seed.nodeSet()
                            .contains(node) : String.format(
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
                new TreeSet<>(new ItemComparatorComparator());
            result.add(new NeededPartsComparator(this.boundNodes, this.boundVars));
            result.add(new ItemTypeComparator());
            result.add(new ConnectedPartsComparator(this.boundNodes, this.boundVars));
            result.add(new IndegreeComparator(this.condition.getPattern()
                .edgeSet()));
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
                    RuleEdge negatedEdge = this.condition.getFactory()
                        .createEdge(source, negatedLabel, target);
                    negatedItem = createEdgeSearchItem(negatedEdge);
                }
                result = createNegatedSearchItem(negatedItem);
                this.boundEdges.add(edge);
            } else if (label.getWildcardGuard() != null) {
                assert !this.typeGraph.isNodeType(edge);
                result = new VarEdgeSearchItem(edge, this.simple);
            } else if (label.isEmpty()) {
                result = new EqualitySearchItem(edge, true);
            } else if (label.isSharp() || label.isAtom()) {
                result = new Edge2SearchItem(edge, this.simple);
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
                if (((VariableNode) node).hasConstant() || this.algebraFamily.supportsSymbolic()) {
                    result = new ValueNodeSearchItem((VariableNode) node, this.algebraFamily);
                }
                // otherwise, the node must be among the count nodes of
                // the subconditions
            } else if (node instanceof OperatorNode) {
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
         * The set of nodes bound by the search items scheduled so far.
         */
        private final Set<RuleNode> boundNodes;
        /**
         * The set of edges bound by the search items scheduled so far.
         */
        private final Set<RuleEdge> boundEdges;
        /**
         * The set of variables bound by the search items scheduled so far.
         */
        private final Set<LabelVar> boundVars;
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
        /** The graph condition for which we develop the plan. */
        private final Condition condition;
        /** Flag indicating if we are matching simple or multi-graphs. */
        private final boolean simple;
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
        IndegreeComparator(Set<? extends RuleEdge> edges) {
            // compute indegrees
            Bag<RuleNode> indegrees = new HashBag<>();
            for (RuleEdge edge : edges) {
                if (!edge.target()
                    .equals(edge.source())) {
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
     * @version $Revision$
     */
    static class NeededPartsComparator implements Comparator<SearchItem> {
        NeededPartsComparator(Set<RuleNode> boundNodes, Set<LabelVar> boundVars) {
            this.boundNodes = boundNodes;
            this.boundVars = boundVars;
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
            if (item.needsNodes()
                .stream()
                .anyMatch(n -> !this.boundNodes.contains(n))) {
                return 0;
            }
            if (item.needsVars()
                .stream()
                .anyMatch(v -> !this.boundVars.contains(v))) {
                return 0;
            }
            return 1;
        }

        /** The set of currently scheduled nodes. */
        private final Set<RuleNode> boundNodes;
        /** The set of currently scheduled variables. */
        private final Set<LabelVar> boundVars;
    }

    /**
     * Search item comparator that gives higher priority to items of which less
     * parts are as yet unmatched.
     * @author Arend Rensink
     * @version $Revision$
     */
    static class ConnectedPartsComparator implements Comparator<SearchItem> {
        ConnectedPartsComparator(Set<RuleNode> boundNodes, Set<LabelVar> boundVars) {
            this.boundNodes = boundNodes;
            this.boundVars = boundVars;
        }

        /**
         * Compares the connect count (lower is better).
         */
        @Override
        public int compare(SearchItem o1, SearchItem o2) {
            return getConnectCount(o2) - getConnectCount(o1);
        }

        /**
         * Returns the number of nodes and variables bound by the item that are as yet
         * unmatched. More unmatched parts means more
         * non-determinism, so the lower the better.
         */
        private int getConnectCount(SearchItem item) {
            int result = 0;
            result += item.bindsNodes()
                .stream()
                .filter(i -> !this.boundNodes.contains(i))
                .mapToInt(i -> 1)
                .sum();
            result += item.bindsVars()
                .stream()
                .filter(i -> !this.boundVars.contains(i))
                .mapToInt(i -> 1)
                .sum();
            return result;
        }

        /** The set of already bound nodes. */
        private final Set<RuleNode> boundNodes;
        /** The set of already bound variables. */
        private final Set<LabelVar> boundVars;
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
                && ((RegExprEdgeSearchItem) item).getEdgeExpr()
                    .isAcceptsEmptyWord()) {
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
            this.priorities = new HashMap<>();
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
                Label firstLabel = ((Edge2SearchItem) first).getEdge()
                    .label();
                Label secondLabel = ((Edge2SearchItem) second).getEdge()
                    .label();
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
     * @version $Revision$
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
            throw new IllegalArgumentException(
                String.format("Unknown comparator class %s", compClass));
        }
    }
}
