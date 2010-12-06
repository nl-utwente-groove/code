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
 * $Id: GraphSearchPlanFactory.java,v 1.23 2007-11-29 12:49:37 rensink Exp $
 */
package groove.match;

import groove.graph.DefaultLabel;
import groove.graph.Edge;
import groove.graph.Label;
import groove.graph.LabelStore;
import groove.graph.Node;
import groove.graph.algebra.ArgumentEdge;
import groove.graph.algebra.OperatorEdge;
import groove.graph.algebra.ProductNode;
import groove.graph.algebra.ValueNode;
import groove.graph.algebra.VariableNode;
import groove.rel.LabelVar;
import groove.rel.RegExpr;
import groove.rel.RegExprLabel;
import groove.rel.VarSupport;
import groove.trans.RuleEdge;
import groove.trans.RuleGraph;
import groove.trans.RuleNode;
import groove.util.Bag;
import groove.util.HashBag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.TreeSet;

/**
 * Factory for search plan-based graph matching strategies. The search plans
 * include items for all graph nodes and edges, ordered by a lexicographically
 * applied sequence of search item comparators.
 * @author Arend Rensink
 * @version $Revision$
 */
public class GraphSearchPlanFactory {
    /**
     * Private, empty constructor with a parameter to control injectivity. This
     * is a singleton class; get the instance through
     * {@link #getInstance(boolean,boolean)}.
     * @param injective if <code>true</code>, the factory produces injective
     *        matchers.
     * @param ignoreNeg if <code>true</code>, the factory produces matchings
     *        that do not regard the negated edges
     */
    GraphSearchPlanFactory(boolean injective, boolean ignoreNeg) {
        this.injective = injective;
        this.ignoreNeg = ignoreNeg;
    }

    /**
     * Factory method returning a list of search items for matching a given
     * graph, given also that certain nodes and edges have already been
     * pre-matched (<i>bound</i>).
     * @param graph the graph that is to be matched
     * @param anchorNodes the set of pre-matched nodes when searching; may be
     *        <code>null</code> if there are no pre-matched nodes
     * @param anchorEdges the set of pre-matched edges when searching; may be
     *        <code>null</code> if there are no pre-matched edges. It is assumed
     *        that the end nodes of all pre-matched edges are themselves
     *        pre-matched.
     * @param labelStore the node subtype relation in the graph
     * @return a list of search items that will result in a matching of
     *         <code>graph</code> when successfully executed in the given order
     */
    public SearchPlanStrategy createMatcher(RuleGraph graph,
            Collection<RuleNode> anchorNodes, Collection<RuleEdge> anchorEdges,
            LabelStore labelStore) {
        PlanData data = new PlanData(graph, labelStore);
        SearchPlanStrategy result =
            new SearchPlanStrategy(graph,
                data.getPlan(anchorNodes, anchorEdges), this.injective);
        result.setFixed();
        return result;
    }

    /** Indicates if the matchers this factory produces are injective. */
    public final boolean isInjective() {
        return this.injective;
    }

    /**
     * Indicates if the matchers this factory produces ignore negations in the
     * host graph.
     */
    public final boolean isIgnoreNeg() {
        return this.ignoreNeg;
    }

    /** Flag indicating if this factory creates injective matchings. */
    private final boolean injective;

    /**
     * Flag indicating if this factory creates matchings that ignore negations
     * in the source graph.
     */
    final boolean ignoreNeg;

    /**
     * Returns the default instance of this factory class, which matches
     * non-injectively.
     */
    static public GraphSearchPlanFactory getInstance() {
        return getInstance(false, false);
    }

    /**
     * Returns an instance of this factory class.
     * @param injective if <code>true</code>, the factory produces injective
     *        matchers.
     * @param ignoreNeg if <code>true</code>, the factory produces matchings
     *        that do not regard the negated edges
     */
    static public GraphSearchPlanFactory getInstance(boolean injective,
            boolean ignoreNeg) {
        return instances[injective ? 1 : 0][ignoreNeg ? 1 : 0];
    }

    /** The fixed, singleton instance of this factory. */
    static private final GraphSearchPlanFactory[][] instances =
        new GraphSearchPlanFactory[2][2];
    static {
        for (int injective = 0; injective <= 1; injective++) {
            for (int ignoreNeg = 0; ignoreNeg <= 1; ignoreNeg++) {
                instances[injective][ignoreNeg] =
                    new GraphSearchPlanFactory(injective == 1, ignoreNeg == 1);
            }
        }
    }

    /**
     * Internal class to collect the data necessary to create a plan and to
     * create the actual plan.
     * @author Arend Rensink
     * @version $Revision $
     */
    class PlanData extends Observable implements Comparator<SearchItem> {
        /**
         * Construct a given plan data object for a given graph, with certain
         * sets of already pre-matched elements.
         * @param graph the graph to be matched by the plan
         * @param labelStore the label store containing the subtype relation
         */
        PlanData(RuleGraph graph, LabelStore labelStore) {
            // compute the set of remaining (unmatched) nodes
            this.remainingNodes = new LinkedHashSet<RuleNode>(graph.nodeSet());
            // compute the set of remaining (unmatched) edges and variables
            this.remainingEdges = new LinkedHashSet<RuleEdge>(graph.edgeSet());
            this.remainingVars =
                new LinkedHashSet<LabelVar>(VarSupport.getAllVars(graph));
            this.labelStore = labelStore;
        }

        /**
         * Creates and returns a search plan on the basis of the given data.
         * @param anchorNodes the set of pre-matched nodes; may be
         *        <code>null</code> for an empty set
         * @param anchorEdges the set of pre-matched edges; may be
         *        <code>null</code> for an empty set
         */
        public List<AbstractSearchItem> getPlan(
                Collection<RuleNode> anchorNodes,
                Collection<RuleEdge> anchorEdges) {
            if (this.used) {
                throw new IllegalStateException(
                    "Method getPlan() was already called");
            } else {
                this.used = true;
            }
            List<AbstractSearchItem> result =
                new ArrayList<AbstractSearchItem>();
            Collection<AbstractSearchItem> items =
                computeSearchItems(anchorNodes, anchorEdges);
            while (!items.isEmpty()) {
                AbstractSearchItem bestItem = Collections.max(items, this);
                result.add(bestItem);
                items.remove(bestItem);
                this.remainingEdges.removeAll(bestItem.bindsEdges());
                this.remainingNodes.removeAll(bestItem.bindsNodes());
                this.remainingVars.removeAll(bestItem.bindsVars());
                // notify the observing comparators of the change
                setChanged();
                notifyObservers(bestItem);
            }
            return result;
        }

        /**
         * Callback method to compute the collection of search items for the
         * plan.
         * @param anchorNodes the set of pre-matched nodes; may be
         *        <code>null</code> for an empty set
         * @param anchorEdges the set of pre-matched edges; may be
         *        <code>null</code> for an empty set
         */
        Collection<AbstractSearchItem> computeSearchItems(
                Collection<RuleNode> anchorNodes,
                Collection<RuleEdge> anchorEdges) {
            Collection<AbstractSearchItem> result =
                new ArrayList<AbstractSearchItem>();
            Set<RuleNode> unmatchedNodes =
                new LinkedHashSet<RuleNode>(this.remainingNodes);
            Set<RuleEdge> unmatchedEdges =
                new LinkedHashSet<RuleEdge>(this.remainingEdges);
            // first a single search item for the pre-matched elements
            if (anchorNodes == null) {
                anchorNodes = Collections.emptySet();
            }
            if (anchorEdges == null) {
                anchorEdges = Collections.emptySet();
            }
            if (!anchorNodes.isEmpty() || !anchorEdges.isEmpty()) {
                AbstractSearchItem preMatchItem =
                    new AnchorSearchItem(anchorNodes, anchorEdges);
                result.add(preMatchItem);
                unmatchedNodes.removeAll(preMatchItem.bindsNodes());
                unmatchedEdges.removeAll(preMatchItem.bindsEdges());
            }
            // match all the value nodes explicitly
            Iterator<RuleNode> unmatchedNodeIter = unmatchedNodes.iterator();
            while (unmatchedNodeIter.hasNext()) {
                RuleNode node = unmatchedNodeIter.next();
                if (node instanceof ValueNode) {
                    result.add(createNodeSearchItem(node));
                    unmatchedNodeIter.remove();
                }
            }
            // then a search item per remaining edge
            for (RuleEdge edge : unmatchedEdges) {
                AbstractSearchItem edgeItem = createEdgeSearchItem(edge);
                if (edgeItem != null) {
                    result.add(edgeItem);
                    unmatchedNodes.removeAll(edgeItem.bindsNodes());
                }
            }
            // finally a search item per remaining node
            for (RuleNode node : unmatchedNodes) {
                AbstractSearchItem nodeItem = createNodeSearchItem(node);
                if (nodeItem != null) {
                    assert !(node instanceof VariableNode)
                        || anchorNodes.contains(node) : String.format(
                        "Variable node '%s' should be among anchors %s", node,
                        anchorNodes);
                    result.add(nodeItem);
                }
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
         * Callback method to construct the list of search item comparators to
         * be used.
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
            return result;
        }

        /**
         * Callback factory method for creating an edge search item.
         */
        protected AbstractSearchItem createEdgeSearchItem(RuleEdge edge) {
            Label label = edge.label();
            RuleNode target = edge.target();
            RuleNode source = edge.source();
            RegExpr negOperand = RegExprLabel.getNegOperand(label);
            if (negOperand instanceof RegExpr.Empty) {
                if (!GraphSearchPlanFactory.this.ignoreNeg) {
                    return createInjectionSearchItem(source, target);
                }
            } else if (negOperand != null) {
                if (!GraphSearchPlanFactory.this.ignoreNeg) {
                    RuleEdge negatedEdge =
                        new RuleEdge(source, negOperand.toLabel(), target);
                    return createNegatedSearchItem(createEdgeSearchItem(negatedEdge));
                }
            } else if (RegExprLabel.isSharp(label)) {
                return new Edge2SearchItem(edge);
            } else if (RegExprLabel.getWildcardId(label) != null) {
                return new VarEdgeSearchItem(edge);
            } else if (RegExprLabel.isWildcard(label)) {
                return new WildcardEdgeSearchItem(edge);
            } else if (label.isNodeType()) {
                return new NodeTypeSearchItem(edge, this.labelStore);
            } else if (RegExprLabel.isAtom(label)) {
                RuleEdge defaultEdge =
                    new RuleEdge(source, RegExprLabel.getAtomText(label),
                        target);
                return new Edge2SearchItem(defaultEdge);
            } else if (label instanceof RegExprLabel) {
                return new RegExprEdgeSearchItem(edge, this.labelStore);
            } else if (edge instanceof OperatorEdge) {
                return new OperatorEdgeSearchItem((OperatorEdge) edge);
            } else if (edge instanceof ArgumentEdge) {
                return null;
            } else {
                return new Edge2SearchItem(edge);
            }
            return null;
        }

        /**
         * Callback factory method for creating a node search item.
         */
        protected AbstractSearchItem createNodeSearchItem(RuleNode node) {
            if (node instanceof ValueNode) {
                return new ValueNodeSearchItem((ValueNode) node);
            } else if (node instanceof VariableNode) {
                return new VariableNodeSearchItem((VariableNode) node);
            } else if (node instanceof ProductNode) {
                return null;
            } else {
                return new NodeSearchItem(node);
            }
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
         * Callback factory method for an injection search item.
         * @param injection the set of nodes to be matched injectively
         * @return an instance of {@link InjectionSearchItem}
         */
        protected InjectionSearchItem createInjectionSearchItem(
                Collection<RuleNode> injection) {
            return new InjectionSearchItem(injection);
        }

        /**
         * Callback factory method for an injection search item.
         * @param node1 the first node to be matched injectively
         * @param node2 the second node to be matched injectively
         * @return an instance of {@link InjectionSearchItem}
         */
        protected InjectionSearchItem createInjectionSearchItem(RuleNode node1,
                RuleNode node2) {
            return new InjectionSearchItem(node1, node2);
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
        private final LabelStore labelStore;
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
        IndegreeComparator(Set<? extends Edge> remainingEdges) {
            // compute indegrees
            Bag<Node> indegrees = new HashBag<Node>();
            for (Edge edge : remainingEdges) {
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
                Edge first = ((Edge2SearchItem) item1).getEdge();
                Edge second = ((Edge2SearchItem) item2).getEdge();
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
                Edge selected = ((Edge2SearchItem) arg).getEdge();
                this.indegrees.remove(selected.target());
            }
        }

        /**
         * Returns the indegree of a given node.
         */
        private int indegree(Node node) {
            return this.indegrees.multiplicity(node);
        }

        /**
         * The indegrees.
         */
        private final Bag<Node> indegrees;
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
     * @version $Revision$
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
            for (Node node : item.bindsNodes()) {
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
     * Search item comparator that gives higher priority to items with more
     * unmatched parts.
     * @author Arend Rensink
     * @version $Revision$
     */
    static class BoundPartsComparator implements Comparator<SearchItem> {
        BoundPartsComparator(Set<Node> remainingNodes,
                Set<LabelVar> remainingVars) {
            this.remainingNodes = remainingNodes;
            this.remainingVars = remainingVars;
        }

        /**
         * Compares the connect count (higher is better).
         */
        public int compare(SearchItem o1, SearchItem o2) {
            return getBoundCount(o1) - getBoundCount(o2);
        }

        /**
         * Returns the number of nodes and variables bound by the item that have
         * not yet been matched. More unmatched parts means more
         * non-determinism, so the lower the better.
         */
        private int getBoundCount(SearchItem item) {
            int result = 0;
            for (Node node : item.bindsNodes()) {
                if (this.remainingNodes.contains(node)) {
                    result++;
                }
            }
            for (LabelVar var : item.bindsVars()) {
                if (this.remainingVars.contains(var)) {
                    result++;
                }
            }
            return result;
        }

        /** The set of (as yet) unscheduled nodes. */
        private final Set<Node> remainingNodes;
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
         * <li> {@link NodeSearchItem}s of a non-specialised type
         * <li> {@link ConditionSearchItem}s
         * <li> {@link RegExprEdgeSearchItem}s
         * <li> {@link VarEdgeSearchItem}s
         * <li> {@link WildcardEdgeSearchItem}s
         * <li> {@link Edge2SearchItem}s
         * <li> {@link InjectionSearchItem}s
         * <li> {@link NegatedSearchItem}s
         * <li> {@link OperatorEdgeSearchItem}s
         * <li> {@link ValueNodeSearchItem}s
         * <li> {@link VariableNodeSearchItem}s
         * <li> {@link AnchorSearchItem}s
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
            if (itemClass == NodeSearchItem.class) {
                return result;
            }
            if (itemClass == NodeTypeSearchItem.class) {
                return result;
            }
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
            if (itemClass == InjectionSearchItem.class) {
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
            if (itemClass == VariableNodeSearchItem.class) {
                return result;
            }
            result++;
            if (itemClass == AnchorSearchItem.class) {
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
                    Label label = DefaultLabel.createTypedLabel(rare.get(i));
                    this.priorities.put(label, rare.size() - i);
                }
            }
            if (common != null) {
                for (int i = 0; i < common.size(); i++) {
                    Label label = DefaultLabel.createTypedLabel(common.get(i));
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
     * @version $Revision$
     */
    static private class ItemComparatorComparator implements
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
