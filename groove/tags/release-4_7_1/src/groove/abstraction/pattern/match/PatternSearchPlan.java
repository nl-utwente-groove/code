/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
 * $Id$
 */
package groove.abstraction.pattern.match;

import groove.abstraction.MyHashSet;
import groove.abstraction.pattern.trans.PatternRule;
import groove.abstraction.pattern.trans.PatternRuleGraph;
import groove.abstraction.pattern.trans.RuleEdge;
import groove.abstraction.pattern.trans.RuleNode;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * List of search items with backwards dependencies.
 * @author Eduardo Zambon
 */
public final class PatternSearchPlan extends ArrayList<SearchItem> {

    /** The rule for which this is the search plan. */
    private final PatternRule pRule;
    /** Direct dependencies of all search plan items. */
    private final List<Integer> dependencies;
    /** Flag indicating that the search should be injective. */
    private final boolean injective;

    /** Basic constructor. */
    public PatternSearchPlan(PatternRule pRule, boolean injective) {
        this.pRule = pRule;
        this.injective = injective;
        this.dependencies = new ArrayList<Integer>();
        computeSearchItems();
    }

    /** Constructs dependency information, in addition to appending the search item. */
    @Override
    public boolean add(SearchItem e) {
        int position = size();
        boolean result = super.add(e);
        // Collection of direct dependencies of the new search item.
        int depend = -1;
        Set<RuleNode> usedNodes = new MyHashSet<RuleNode>();
        usedNodes.addAll(e.needsNodes());
        usedNodes.addAll(e.bindsNodes());
        for (int i = 0; i < position; i++) {
            // Set a dependency if the item at position i binds a required node.
            if (usedNodes.removeAll(get(i).bindsNodes())) {
                depend = i;
            }
        }
        // Add dependencies due to injective matching.
        if (this.injective) {
            // Cumulative set of nodes bound by search items up to i.
            Set<RuleNode> boundNodes = new MyHashSet<RuleNode>();
            // For each item, whether it binds new nodes.
            BitSet bindsNewNodes = new BitSet();
            for (int i = 0; i <= position; i++) {
                bindsNewNodes.set(i, boundNodes.addAll(get(i).bindsNodes()));
            }
            if (bindsNewNodes.get(position)) {
                // The new item depends on all other items that bind new nodes.
                for (int i = 0; i < position; i++) {
                    if (bindsNewNodes.get(i)) {
                        depend = i;
                    }
                }
            }
        }
        // Transitively close the indirect dependencies.
        this.dependencies.add(depend);
        return result;
    }

    /** Basic getter method. */
    public PatternRule getRule() {
        return this.pRule;
    }

    /**
     * Returns the index of the last predecessor on the result of which this one 
     * depends for its matching, or {@code -1} if there is no such dependency.
     */
    public int getDependency(int i) {
        return this.dependencies.get(i);
    }

    private void computeSearchItems() {
        PlanData planData = new PlanData(this.pRule);
        for (SearchItem item : planData.getPlanItems()) {
            // EZ says: for now we just set everything as relevant...
            item.setRelevant(true);
            add(item);
        }
        // We are done.
        trimToSize();
    }

    @Override
    public SearchItem set(int index, SearchItem element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, SearchItem element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SearchItem remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends SearchItem> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * Plan data extension based on a graph condition.
     * @author Arend Rensink and Eduardo Zambon
     */
    private static final class PlanData implements Comparator<SearchItem> {

        /** The rule or which we develop the plan. */
        private final PatternRule pRule;
        /** The set of nodes to be matched. */
        private final Set<RuleNode> remainingNodes;
        /** The set of edges to be matched. */
        private final Set<RuleEdge> remainingEdges;
        /**
         * The comparators used to determine the order in which the edges should
         * be matched.
         */
        private final Collection<Comparator<SearchItem>> comparators;

        /**
         * Constructs a fresh instance of the plan data, based on a given set of
         * system properties, and sets of already matched nodes and edges.
         */
        PlanData(PatternRule pRule) {
            this.pRule = pRule;
            PatternRuleGraph lhs = this.pRule.lhs();
            // compute the set of remaining (unmatched) nodes
            this.remainingNodes = new LinkedHashSet<RuleNode>(lhs.nodeSet());
            // compute the set of remaining (unmatched) edges and variables
            this.remainingEdges = new LinkedHashSet<RuleEdge>(lhs.edgeSet());
            this.comparators = computeComparators();
        }

        /**
         * Creates and returns a list of search items.
         */
        List<SearchItem> getPlanItems() {
            List<SearchItem> result = new ArrayList<SearchItem>();
            Collection<SearchItem> items = computeSearchItems();
            while (!items.isEmpty()) {
                SearchItem bestItem = Collections.max(items, this);
                result.add(bestItem);
                this.remainingEdges.removeAll(bestItem.bindsEdges());
                this.remainingNodes.removeAll(bestItem.bindsNodes());
                items.remove(bestItem);
            }
            return result;
        }

        /**
         * Creates the comparators for the search plan. 
         * @return a list of comparators determining the order in which edges
         *         should be matched
         */
        Collection<Comparator<SearchItem>> computeComparators() {
            Collection<Comparator<SearchItem>> result =
                new TreeSet<Comparator<SearchItem>>(
                    new ItemComparatorComparator());
            result.add(new ItemTypeComparator());
            result.add(new ConnectedPartsComparator(this.remainingNodes));
            result.add(new LayerComparator());
            return result;
        }

        /**
         * Orders search items according to the lexicographic order of the
         * available item comparators.
         */
        public int compare(SearchItem o1, SearchItem o2) {
            int result = 0;
            Iterator<Comparator<SearchItem>> comparatorIter =
                this.comparators.iterator();
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
         * Creates and returns the search items for the plan that is being
         * created...
         */
        Collection<SearchItem> computeSearchItems() {
            Collection<SearchItem> result = new ArrayList<SearchItem>();
            Set<RuleNode> unmatchedNodes =
                new LinkedHashSet<RuleNode>(this.remainingNodes);
            // First a search item per remaining edge.
            for (RuleEdge edge : this.remainingEdges) {
                SearchItem edgeItem = createEdgeSearchItem(edge);
                result.add(edgeItem);
                unmatchedNodes.removeAll(edgeItem.bindsNodes());
            }
            // Then a search item per remaining node.
            for (RuleNode node : unmatchedNodes) {
                SearchItem nodeItem = createNodeSearchItem(node);
                result.add(nodeItem);
            }
            // Create a negated search item if we have a closure rule.
            if (this.pRule.isClosure()) {
                RuleEdge edge = this.pRule.getCreatorEdges()[0];
                SearchItem negatedEdge = createNegatedSearchItem(edge);
                result.add(negatedEdge);
            }
            return result;
        }

        /**
         * Callback factory method for creating a node search item.
         */
        SearchItem createNodeSearchItem(RuleNode node) {
            return new PatternNodeSearchItem(node);
        }

        /**
         * Callback factory method for creating an edge search item.
         */
        SearchItem createEdgeSearchItem(RuleEdge edge) {
            return new PatternEdgeSearchItem(edge);
        }

        /**
         * Callback factory method for creating a negated search item.
         */
        SearchItem createNegatedSearchItem(RuleEdge edge) {
            return new NegatedSearchItem(createEdgeSearchItem(edge));
        }
    }

    /**
     * Comparator determining the ordering in which the search item comparators
     * should be applied. Comparators will be applied in increasing order, so
     * the comparators should be ordered in decreasing priority.
     * @author Arend Rensink
     */
    private static class ItemComparatorComparator implements
            Comparator<Comparator<SearchItem>> {
        /** Empty constructor with the correct visibility. */
        ItemComparatorComparator() {
            // Empty by design.
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
         * <li> {@link ItemTypeComparator}
         * <li> {@link ConnectedPartsComparator}
         * <li> {@link LayerComparator}
         * </ul>
         */
        private int getRating(Comparator<SearchItem> comparator) {
            int result = 0;
            Class<?> compClass = comparator.getClass();
            if (compClass == ItemTypeComparator.class) {
                return result;
            }
            result++;
            if (compClass == ConnectedPartsComparator.class) {
                return result;
            }
            result++;
            if (compClass == LayerComparator.class) {
                return result;
            }
            throw new IllegalArgumentException(String.format(
                "Unknown comparator class %s", compClass));
        }
    }

    /**
     * Comparator for item types
     * @author Arend Rensink
     */
    private static class ItemTypeComparator implements Comparator<SearchItem> {
        /**
         * Compares two items, with the purpose of
         * determining which one should be scheduled first. In order from worst
         * to best:
         * <ul>
         * <li> {@link NegatedSearchItem}s
         * <li> {@link PatternNodeSearchItem}s
         * <li> {@link PatternEdgeSearchItem}s
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

            if (itemClass == NegatedSearchItem.class) {
                return result;
            }
            result++;
            if (itemClass == PatternNodeSearchItem.class) {
                return result;
            }
            result++;
            if (itemClass == PatternEdgeSearchItem.class) {
                return result;
            }

            throw new IllegalArgumentException(String.format(
                "Unrecognised search item %s", item));
        }
    }

    /**
     * Search item comparator that gives higher priority to items of which more
     * parts have been matched.
     * @author Arend Rensink
     */
    private static class ConnectedPartsComparator implements
            Comparator<SearchItem> {
        ConnectedPartsComparator(Set<RuleNode> remainingNodes) {
            this.remainingNodes = remainingNodes;
        }

        /**
         * Compares the connect count (higher is better).
         */
        public int compare(SearchItem o1, SearchItem o2) {
            return getConnectCount(o1) - getConnectCount(o2);
        }

        /**
         * Returns the number of nodes bound by the item that have
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
            return result;
        }

        /** The set of (as yet) unscheduled nodes. */
        private final Set<RuleNode> remainingNodes;
    }

    /**
     * Layer comparator. An item is better if it is in a deeper layer.
     * @author Eduardo Zambon
     */
    private static class LayerComparator implements Comparator<SearchItem> {

        /** Basic constructor. */
        LayerComparator() {
            // Empty by design.
        }

        /**
         * Favours the item with the largest layer.
         */
        public int compare(SearchItem item1, SearchItem item2) {
            if (item1 instanceof NegatedSearchItem) {
                item1 = ((NegatedSearchItem) item1).inner;
            }
            if (item2 instanceof NegatedSearchItem) {
                item2 = ((NegatedSearchItem) item2).inner;
            }
            RuleNode node1 = null;
            RuleNode node2 = null;
            if (item1 instanceof PatternNodeSearchItem) {
                node1 = ((PatternNodeSearchItem) item1).getNode();
            } else if (item1 instanceof PatternEdgeSearchItem) {
                node1 = ((PatternEdgeSearchItem) item1).getEdge().target();
            }
            if (item2 instanceof PatternNodeSearchItem) {
                node2 = ((PatternNodeSearchItem) item2).getNode();
            } else if (item2 instanceof PatternEdgeSearchItem) {
                node2 = ((PatternEdgeSearchItem) item2).getEdge().target();
            }
            assert node1 != null && node2 != null;
            return node1.getLayer() - node2.getLayer();
        }
    }

}
