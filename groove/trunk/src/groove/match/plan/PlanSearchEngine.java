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
import groove.graph.Label;
import groove.graph.TypeGraph;
import groove.graph.TypeLabel;
import groove.graph.algebra.OperatorEdge;
import groove.graph.algebra.VariableNode;
import groove.match.SearchEngine;
import groove.rel.LabelVar;
import groove.rel.RegExpr;
import groove.rel.VarSupport;
import groove.trans.Condition;
import groove.trans.Condition.Op;
import groove.trans.DefaultRuleNode;
import groove.trans.EdgeEmbargo;
import groove.trans.RuleEdge;
import groove.trans.RuleGraph;
import groove.trans.RuleLabel;
import groove.trans.RuleNode;
import groove.trans.SystemProperties;
import groove.util.Bag;
import groove.util.HashBag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
    public PlanSearchStrategy createMatcher(Condition condition,
            Collection<RuleNode> seedNodes, Collection<RuleEdge> seedEdges) {
        assert (seedNodes == null) == (seedEdges == null) : "Anchor nodes and edges should be null simultaneously";
        Set<RuleNode> anchorNodes = new HashSet<RuleNode>();
        Set<RuleEdge> anchorEdges = new HashSet<RuleEdge>();
        if (condition.hasRule()) {
            anchorNodes.addAll(Arrays.asList(condition.getRule().getAnchorNodes()));
            anchorEdges.addAll(Arrays.asList(condition.getRule().getAnchorEdges()));
        }
        PlanData planData = new PlanData(condition, this.searchMode);
        SearchPlan plan = planData.getPlan(seedNodes, seedEdges);
        for (AbstractSearchItem item : plan) {
            boolean relevant = anchorNodes.removeAll(item.bindsNodes());
            relevant |= anchorEdges.removeAll(item.bindsEdges());
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
        PlanSearchStrategy result = new PlanSearchStrategy(this, plan);
        if (PRINT) {
            System.out.print(String.format(
                "%nPlan for %s, prematched nodes %s, prematched edges %s:%n    %s",
                condition.getName(), seedNodes, seedEdges, result));
            System.out.printf("%n    Dependencies & Relevance: [");
            for (int i = 0; i < plan.size(); i++) {
                if (i > 0) {
                    System.out.print(", ");
                }
                System.out.printf("%d%s: %s", i, plan.get(i).isRelevant() ? "*"
                        : "", plan.getDependency(i));
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
            instance =
                new EnumMap<SearchMode,PlanSearchEngine>(SearchMode.class);
            for (SearchMode mode : EnumSet.allOf(SearchMode.class)) {
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
    private static class PlanData extends Observable implements
            Comparator<SearchItem> {
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
            if (condition.hasPattern()) {
                RuleGraph graph = condition.getPattern();
                // compute the set of remaining (unmatched) nodes
                this.remainingNodes =
                    new LinkedHashSet<RuleNode>(graph.nodeSet());
                // compute the set of remaining (unmatched) edges and variables
                this.remainingEdges =
                    new LinkedHashSet<RuleEdge>(graph.edgeSet());
                this.remainingVars =
                    new LinkedHashSet<LabelVar>(VarSupport.getAllVars(graph));
                this.algebraFamily =
                    AlgebraFamily.getInstance(condition.getSystemProperties().getAlgebraFamily());
            } else {
                this.remainingNodes = null;
                this.remainingEdges = null;
                this.remainingVars = null;
                this.algebraFamily = null;
            }
        }

        /**
         * Creates and returns a search plan on the basis of the given data.
         * @param seedNodes the set of pre-matched nodes; may be
         *        <code>null</code> for an empty set
         * @param seedEdges the set of pre-matched edges; may be
         *        <code>null</code> for an empty set
         */
        public SearchPlan getPlan(Collection<RuleNode> seedNodes,
                Collection<RuleEdge> seedEdges) {
            if (this.used) {
                throw new IllegalStateException(
                    "Method getPlan() was already called");
            } else {
                this.used = true;
            }
            boolean injective =
                this.condition.getSystemProperties().isInjective();
            if (this.searchMode == SearchMode.MINIMAL
                || this.searchMode == SearchMode.REGEXPR) {
                // We don't want injectivity in these modes.
                injective = false;
            } else if (this.searchMode == SearchMode.REVERSE) {
                // We always want injectivity in this mode.
                injective = true;
            }
            SearchPlan result =
                new SearchPlan(this.condition, seedNodes, seedEdges, injective);
            Collection<AbstractSearchItem> items =
                computeSearchItems(seedNodes, seedEdges);
            while (!items.isEmpty()) {
                AbstractSearchItem bestItem = Collections.max(items, this);
                // check if the item is compatible with the search mode
                boolean include;
                switch (this.searchMode) {
                case MINIMAL:
                    include = bestItem.isMinimal();
                    break;
                case REGEXPR:
                    include = (bestItem instanceof RegExprEdgeSearchItem);
                    break;
                default:
                    include = true;
                }
                if (include) {
                    result.add(bestItem);
                    this.remainingEdges.removeAll(bestItem.bindsEdges());
                    this.remainingNodes.removeAll(bestItem.bindsNodes());
                    this.remainingVars.removeAll(bestItem.bindsVars());
                    // notify the observing comparators of the change
                    setChanged();
                    notifyObservers(bestItem);
                }
                items.remove(bestItem);
            }
            return result;
        }

        /**
         * Orders search items according to the lexicographic order of the
         * available item comparators.
         */
        final public int compare(SearchItem o1, SearchItem o2) {
            int result = 0;
            Iterator<Comparator<SearchItem>> comparatorIter =
                getComparators().iterator();
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
         * @param seedNodes the set of pre-matched nodes
         * @param seedEdges the set of pre-matched edges
         */
        Collection<AbstractSearchItem> computeSearchItems(
                Collection<RuleNode> seedNodes, Collection<RuleEdge> seedEdges) {
            Collection<AbstractSearchItem> result =
                new ArrayList<AbstractSearchItem>();
            if (this.condition.hasPattern()) {
                result.addAll(computePatternSearchItems(seedNodes, seedEdges));
            }
            for (Condition subCondition : this.condition.getSubConditions()) {
                AbstractSearchItem item;
                if (subCondition instanceof EdgeEmbargo) {
                    RuleEdge embargoEdge =
                        ((EdgeEmbargo) subCondition).getEmbargoEdge();
                    if (embargoEdge.label().isEmpty()) {
                        if (this.condition.getSystemProperties().isInjective()) {
                            item = null;
                        } else {
                            item =
                                createEqualitySearchItem(embargoEdge.source(),
                                    embargoEdge.target(), false);
                        }
                    } else {
                        AbstractSearchItem edgeSearchItem =
                            createEdgeSearchItem(embargoEdge);
                        if (this.searchMode == SearchMode.REVERSE) {
                            item = edgeSearchItem;
                        } else {
                            item = createNegatedSearchItem(edgeSearchItem);
                        }
                    }
                } else {
                    if (this.searchMode == SearchMode.REVERSE
                        && subCondition.getOp() == Op.NOT) {
                        item = new ConditionSearchItem(subCondition.reverse());
                    } else {
                        item = new ConditionSearchItem(subCondition);
                    }
                }
                if (item != null) {
                    result.add(item);
                }
            }
            return result;
        }

        /**
         * Adds embargo and injection search items to the super result.
         * @param seedNodes the set of pre-matched nodes
         * @param seedEdges the set of pre-matched edges
         */
        Collection<AbstractSearchItem> computePatternSearchItems(
                Collection<RuleNode> seedNodes, Collection<RuleEdge> seedEdges) {
            Collection<AbstractSearchItem> result =
                new ArrayList<AbstractSearchItem>();
            Set<RuleNode> unmatchedNodes =
                new LinkedHashSet<RuleNode>(this.remainingNodes);
            Set<RuleEdge> unmatchedEdges =
                new LinkedHashSet<RuleEdge>(this.remainingEdges);
            // first a single search item for the pre-matched elements
            if (seedNodes == null) {
                seedNodes = Collections.emptySet();
            }
            if (seedEdges == null) {
                seedEdges = Collections.emptySet();
            }
            if (!seedNodes.isEmpty() || !seedEdges.isEmpty()) {
                AbstractSearchItem seedItem =
                    new SeedSearchItem(seedNodes, seedEdges);
                result.add(seedItem);
                unmatchedNodes.removeAll(seedItem.bindsNodes());
                unmatchedEdges.removeAll(seedItem.bindsEdges());
            }
            // match all the value nodes and guard-carrying nodes explicitly
            Iterator<RuleNode> unmatchedNodeIter = unmatchedNodes.iterator();
            while (unmatchedNodeIter.hasNext()) {
                RuleNode node = unmatchedNodeIter.next();
                if (node instanceof VariableNode
                    && ((VariableNode) node).getConstant() != null
                    || !node.getTypeGuards().isEmpty()) {
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
                    if (edgeItem.bindsNodes().contains(source)
                        && (edge.getType() == null || edge.getType().source() == source.getType())) {
                        unmatchedNodes.remove(source);
                    }
                    RuleNode target = edge.target();
                    if (edgeItem.bindsNodes().contains(target)
                        && (edge.getType() == null || edge.getType().target() == target.getType())) {
                        unmatchedNodes.remove(target);
                    }
                }
            }
            // finally a search item per remaining node
            for (RuleNode node : unmatchedNodes) {
                AbstractSearchItem nodeItem = createNodeSearchItem(node);
                if (nodeItem != null) {
                    assert !(node instanceof VariableNode)
                        || ((VariableNode) node).getConstant() != null
                        || seedNodes.contains(node) : String.format(
                        "Variable node '%s' should be among anchors %s", node,
                        seedNodes);
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
                new TreeSet<Comparator<SearchItem>>(
                    new ItemComparatorComparator());
            result.add(new NeededPartsComparator(this.remainingNodes,
                this.remainingVars));
            result.add(new ItemTypeComparator());
            result.add(new ConnectedPartsComparator(this.remainingNodes,
                this.remainingVars));
            result.add(new IndegreeComparator(this.remainingEdges));
            SystemProperties properties = this.condition.getSystemProperties();
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
                result = createEqualitySearchItem(source, target, false);
            } else if (negOperand != null) {
                RuleEdge negatedEdge =
                    this.condition.getFactory().createEdge(source,
                        negOperand.toLabel(), target);
                result =
                    createNegatedSearchItem(createEdgeSearchItem(negatedEdge));
            } else if (label.getWildcardId() != null) {
                assert !this.typeGraph.isNodeType(edge);
                result = new VarEdgeSearchItem(edge);
            } else if (label.isWildcard()) {
                result = new WildcardEdgeSearchItem(edge);
            } else if (label.isEmpty()) {
                result = new EqualitySearchItem(source, target, true);
            } else if (label.isSharp() || label.isAtom()) {
                result = new Edge2SearchItem(edge);
            } else if (label.isOperator()) {
                assert this.searchMode == NORMAL;
                result =
                    new OperatorEdgeSearchItem((OperatorEdge) edge,
                        this.algebraFamily);
            } else if (!label.isArgument()) {
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
                if (((VariableNode) node).getConstant() != null) {
                    result =
                        new ValueNodeSearchItem((VariableNode) node,
                            this.algebraFamily);
                }
                // otherwise, the node must be among the count nodes of
                // the subconditions
            } else if (node instanceof DefaultRuleNode) {
                result = new NodeTypeSearchItem(node, this.typeGraph);
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
         * Callback factory method for an equality search item.
         * @param node1 the first node to be compared
         * @param node2 the second node to be compared
         * @param equals if {@code true}, the images of {@code node1} and 
         * {@code node2} should be equal, otherwise they should be distinct
         * @return an instance of {@link EqualitySearchItem}
         */
        protected EqualitySearchItem createEqualitySearchItem(RuleNode node1,
                RuleNode node2, boolean equals) {
            return new EqualitySearchItem(node1, node2, equals);
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
         * Flag determining if {@link #getPlan(Collection, Collection)} was
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
        public int compare(SearchItem item1, SearchItem item2) {
            int result = 0;
            if (item1 instanceof Edge2SearchItem
                && item2 instanceof Edge2SearchItem) {
                RuleEdge first = ((Edge2SearchItem) item1).getEdge();
                RuleEdge second = ((Edge2SearchItem) item2).getEdge();
                // first test for the indegree of the source (lower = better)
                result = indegree(second.source()) - indegree(first.source());
                if (result == 0) {
                    // now test for the indegree of the target (higher = better)
                    result =
                        indegree(first.target()) - indegree(second.target());
                }
            }
            return result;
        }

        /**
         * This method is called when a new edge is scheduled. It decreases the
         * indegree of all the edge target.
         */
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
        NeededPartsComparator(Set<RuleNode> remainingNodes,
                Set<LabelVar> remainingVars) {
            this.remainingNodes = remainingNodes;
            this.remainingVars = remainingVars;
        }

        /**
         * First compares the need count (higher is better), then the bind count
         * (lower is better).
         */
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
        ConnectedPartsComparator(Set<RuleNode> remainingNodes,
                Set<LabelVar> remainingVars) {
            this.remainingNodes = remainingNodes;
            this.remainingVars = remainingVars;
        }

        /**
         * Compares the connect count (higher is better).
         */
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
         * <li> {@link WildcardEdgeSearchItem}s
         * <li> {@link Edge2SearchItem}s
         * <li> {@link EqualitySearchItem}s
         * <li> {@link NegatedSearchItem}s
         * <li> {@link OperatorEdgeSearchItem}s
         * <li> {@link ValueNodeSearchItem}s
         * <li> {@link SeedSearchItem}s
         * </ul>
         */
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
            if (itemClass == WildcardEdgeSearchItem.class) {
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
            if (itemClass == OperatorEdgeSearchItem.class) {
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
            throw new IllegalArgumentException(String.format(
                "Unrecognised search item %s", item));
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
        public int compare(SearchItem first, SearchItem second) {
            if (first instanceof Edge2SearchItem
                && second instanceof Edge2SearchItem) {
                Label firstLabel = ((Edge2SearchItem) first).getEdge().label();
                Label secondLabel =
                    ((Edge2SearchItem) second).getEdge().label();
                // compare edge priorities
                return getEdgePriority(firstLabel)
                    - getEdgePriority(secondLabel);
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
    static class ItemComparatorComparator implements
            Comparator<Comparator<SearchItem>> {
        /** Empty constructor with the correct visibility. */
        ItemComparatorComparator() {
            // empty
        }

        /**
         * Returns the difference in ratings between the two comparators. This
         * means lower-rated comparators are ordered first.
         */
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
            throw new IllegalArgumentException(String.format(
                "Unknown comparator class %s", compClass));
        }
    }
}
