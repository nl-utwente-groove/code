/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
 * $Id: GraphSearchPlanFactory.java,v 1.10 2007-09-11 10:17:08 rensink Exp $
 */
package groove.match;

import groove.algebra.Constant;
import groove.graph.DefaultEdge;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.algebra.AlgebraEdge;
import groove.graph.algebra.ProductEdge;
import groove.graph.algebra.ProductNode;
import groove.graph.algebra.ValueNode;
import groove.rel.RegExpr;
import groove.rel.RegExprLabel;
import groove.rel.VarSupport;
import groove.util.Bag;
import groove.util.HashBag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.TreeSet;

/**
 * Factory for search plan-based graph matching strategies.
 * The search plans include items for all graph nodes and edges, ordered
 * by a lexicographically applied sequence of search item comparators. 
 * @author Arend Rensink
 * @version $Revision: 1.10 $
 */
public class GraphSearchPlanFactory {
    /** 
     * Private, empty constructor.
     * This is a singleton class; get the instance through {@link #getInstance()}.
     */
    GraphSearchPlanFactory() {
        // empty
    }

    /** 
     * Factory method returning a list of search items for matching a given graph, given also
     * that certain nodes and edges have already been pre-matched (<i>bound</i>).
     * @param graph the graph that is to be matched
     * @param preMatchedNodes the set of pre-matched nodes when searching; may be <code>null</code> if there are
     * no pre-matched nodes
     * @param preMatchedEdges the set of pre-matched edges when searching; may be <code>null</code> if there are
     * no pre-matched edges. It is assumed that the end nodes of all pre-matched edges are themselves pre-matched.
     * @return a list of search items that will result in a matching of <code>graph</code>
     * when successfully executed in the given order
     */
    public SearchPlanStrategy createMatcher(Graph graph, Collection<? extends Node> preMatchedNodes, Collection<? extends Edge> preMatchedEdges) {
        PlanData data = new PlanData(graph, preMatchedNodes, preMatchedEdges);
        return new SearchPlanStrategy(data.getPlan(), false);
    }
	
    /** Returns the singleton instance of this factory class. */
    static public GraphSearchPlanFactory getInstance() {
        return instance;
    }
    
    /** The fixed, singleton instance of this factory. */
    static private final GraphSearchPlanFactory instance = new GraphSearchPlanFactory();
    
    /**
     * Internal class to collect the data necessary to create a plan
     * and to create the actual plan. 
     * @author Arend Rensink
     * @version $Revision $
     */
    class PlanData extends Observable implements Comparator<SearchItem> {
        /**
         * Construct a given plan data object for a given graph,
         * with certain sets of already pre-matched elements.
         * @param graph the graph to be matched by the plan
         * @param preMatchedNodes the set of pre-matched nodes
         * @param preMatchedEdges the set of pre-matched edges
         */
        PlanData(Graph graph, Collection<? extends Node> preMatchedNodes, Collection<? extends Edge> preMatchedEdges) {
            // compute the set of remaining (unmatched) nodes
            remainingNodes = new HashSet<Node>(graph.nodeSet());
            if (preMatchedNodes != null) {
                remainingNodes.removeAll(preMatchedNodes);
            }
            // compute the set of remaining (unmatched) edges and variables
            remainingEdges = new HashSet<Edge>(graph.edgeSet());
            remainingVars = new HashSet<String>(VarSupport.getAllVars(graph));
            if (preMatchedEdges != null) {
                for (Edge edge: preMatchedEdges) {
                    remainingEdges.remove(edge);
                    remainingVars.removeAll(VarSupport.getBoundVars(edge));
                }
            }
        }

        /**
         * Creates and returns a search plan on the basis of the given data.
         */
        public List<SearchItem> getPlan() {
            if (used) {
                throw new IllegalStateException("Method getPlan() was already called");
            } else {
                used = true;
            }
            List<SearchItem> result = new ArrayList<SearchItem>();
            Collection<SearchItem> items = computeSearchItems();
            while (!items.isEmpty()) {
                SearchItem bestItem = Collections.max(items, this);
                result.add(bestItem);
                items.remove(bestItem);
                remainingNodes.removeAll(bestItem.bindsNodes());
                remainingVars.removeAll(bestItem.bindsVars());
                // notify the observing comparators of the change
                setChanged();
                notifyObservers(bestItem);
            }
            return result;
        }

        /** Callback method to compute the collection of search items for the plan. */
        Collection<SearchItem> computeSearchItems() {
            Collection<SearchItem> result = new ArrayList<SearchItem>();
            Set<Node> unmatchedNodes = new HashSet<Node>(remainingNodes);
//            // first match all value nodes
//            Iterator<Node> unmatchedNodeIter = unmatchedNodes.iterator();
//            while (unmatchedNodeIter.hasNext()) {
//                Node node = unmatchedNodeIter.next();
//                if (node instanceof ValueNode && ((ValueNode) node).hasValue()) {
//                    SearchItem nodeItem = createNodeSearchItem(node);
//                    result.add(nodeItem);
//                    unmatchedNodeIter.remove();
//                }
//            }
            for (Edge edge: remainingEdges) {
                SearchItem edgeItem = createEdgeSearchItem(edge);
                if (edgeItem != null) {
                    result.add(edgeItem);
                    unmatchedNodes.removeAll(edgeItem.bindsNodes());
                }
            }
            for (Node node : unmatchedNodes) {
                SearchItem nodeItem = createNodeSearchItem(node);
                if (nodeItem != null) {
                    result.add(nodeItem);
                }
            }
            return result;
        }
        
        /**
         * Orders search items according to the lexicographic order of 
         * the available item comparators.
         */
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
         * Lazily creates and returns the set of search item comparators that determines 
         * their priority in the search plan.
         */
        final Collection<Comparator<SearchItem>> getComparators() {
            if (comparators == null) {
                comparators = computeComparators();
                // add those comparators as listeners that implement the observer interface
                for (Comparator<SearchItem> comparator: comparators) {
                    if (comparator instanceof Observer) {
                        addObserver((Observer) comparator);
                    }
                }
            }
            return comparators;
        }
        
        /**
         * Callback method to construct the list of search item comparators to be used.
         */
        Collection<Comparator<SearchItem>> computeComparators() {
            Collection<Comparator<SearchItem>> result = new TreeSet<Comparator<SearchItem>>(new ItemComparatorComparator());
            result.add(new NeededPartsComparator(remainingNodes, remainingVars));
            result.add(new ItemTypeComparator());
            result.add(new IndegreeComparator(remainingEdges));
            return result;
        }

        /**
         * Callback factory method for creating an edge search item.
         */
        protected SearchItem createEdgeSearchItem(Edge edge) {
            Label label = edge.label();
            RegExpr negOperand = RegExprLabel.getNegOperand(label);
            if (negOperand instanceof RegExpr.Empty) {
                return createInjectionSearchItem(Arrays.asList(edge.ends()));
            } else if (negOperand != null) {
                Edge negatedEdge = DefaultEdge.createEdge(edge.source(), negOperand.toLabel(), edge.opposite());
                return createNegatedSearchItem(createEdgeSearchItem(negatedEdge));
            } else if (RegExprLabel.getWildcardId(label) != null) {
                return new VarEdgeSearchItem(edge);
            } else if (RegExprLabel.isWildcard(label)) {
                return new WildcardEdgeSearchItem(edge);
            } else if (RegExprLabel.isAtom(label)) {
                Edge defaultEdge = DefaultEdge.createEdge(edge.source(), RegExprLabel.getAtomText(label), edge.opposite());
                return new EdgeSearchItem(defaultEdge);
            } else if (label instanceof RegExprLabel) {
                return new RegExprEdgeSearchItem(edge);
            } else if (edge instanceof ProductEdge) {
                if (((ProductEdge) edge).getOperation() instanceof Constant) {
                    // constants are more efficiently matched as ValueNodes
                    return null;
                } else {
                    return new OperatorEdgeSearchItem((ProductEdge) edge);
                }
        	} else if (edge instanceof AlgebraEdge) {
        		return null;
        	} else {
        	    return new EdgeSearchItem(edge);      
            }
        }

        /**
         * Callback factory method for creating a node search item.
         */
        protected SearchItem createNodeSearchItem(Node node) {
        	if (node instanceof ValueNode) {
        		return new ValueNodeSearchItem((ValueNode) node);
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
         * @param injection the first node to be matched injectively
         * @return an instance of {@link InjectionSearchItem}
         */
        protected InjectionSearchItem createInjectionSearchItem(Collection<? extends Node> injection) {
            return new InjectionSearchItem(injection);
        }

        /**
         * The set of nodes to be matched.
         */
        private final Set<Node> remainingNodes;
        /**
         * The set of edges to be matched.
         */
        private final Set<Edge> remainingEdges;
        /**
         * The set of variables to be matched.
         */
        private final Set<String> remainingVars;
        /**
         * The comparators used to determine the order in which the edges
         * should be matched.
         */
        private Collection<Comparator<SearchItem>> comparators;
        /** Flag determining if {@link #getPlan()} was already called. */
        private boolean used;
    }
  
    /**
     * Edge comparator based on the number of incoming edges of the
     * source and target nodes.
     * An edge is better if it has lower source indegree, or failing that,
     * higher target indegree.
     * The idea is that the "roots" of a graph (those starting in nodes with
     * small indegree) are likely to give a better immediate reduction of
     * the number of possible matches. For the outdegree the reasoning is that
     * the more constraints a matching causes, the better.
     * The class is an observer in order to be able to maintain the indegrees.
     * @author Arend Rensink
     * @version $Revision $
     */
    static class IndegreeComparator implements Comparator<SearchItem>, Observer {
        /**
         * Constructs a comparator on the basis of a given set of unmatched edges.
         */
        IndegreeComparator(Set<? extends Edge> remainingEdges) {
            // compute indegrees
            Bag<Node> indegrees = new HashBag<Node>();
            for (Edge edge: remainingEdges) {
                for (int i = 1; i < edge.endCount(); i++) {
                    if (!edge.end(i).equals(edge.source())) {
                        indegrees.add(edge.end(i));
                    }
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
            if (item1 instanceof EdgeSearchItem && item2 instanceof EdgeSearchItem) {
                Edge first = ((EdgeSearchItem) item1).getEdge();
                Edge second = ((EdgeSearchItem) item2).getEdge();
                // first test for the indegree of the source (lower = better)
                result = indegree(second.source()) - indegree(first.source()); 
                if (result == 0) {
                    // now test for the indegree of the target (higher = better)
                    result = indegree(first.opposite()) - indegree(second.opposite()); 
                }
            }
            return result;
        }

        /**
         * This method is called when a new edge is scheduled.
         * It decreases the indegree of all end nodes of that edge except its source.
         */
        public void update(Observable o, Object arg) {
            if (arg instanceof EdgeSearchItem) {
                Edge selected = ((EdgeSearchItem) arg).getEdge();
                for (Node end : selected.ends()) {
                    if (!end.equals(selected.source())) {
                        indegrees.remove(end);
                    }
                }
            }
        }

        /**
         * Returns the indegree of a given node.
         */
        private int indegree(Node node) {
            return indegrees.multiplicity(node);
        }

        /**
         * The indegrees.
         */
        private final Bag<Node> indegrees;
    }
    
    /** 
     * Search item comparator that gives least priority to items
     * of which some needed nodes or variables have not yet been matched.
     * Among those of which all needed parts have been matched,
     * the comparator prefers those of which the most bound parts 
     * have also been matched.
     * @author Arend Rensink
     * @version $Revision: 1.10 $
     */
    static class NeededPartsComparator implements Comparator<SearchItem> {
        NeededPartsComparator(Set<Node> remainingNodes, Set<String> remainingVars) {
            this.remainingNodes = remainingNodes;
            this.remainingVars = remainingVars;
        }

        /**
         * First compares the need count (higher is better), then
         * the bind count (lower is better).
         */
        public int compare(SearchItem o1, SearchItem o2) {
            int result = getNeedCount(o1) - getNeedCount(o2);
            if (result == 0) {
                result = getConnectCount(o1) - getConnectCount(o2);
            }
            if (result == 0) {
                result = getBindCount(o2) - getBindCount(o1);
            }
            return result;
        }
        
        /** 
         * Returns 0 if the item needs a node or variable that
         * has not yet been matched, 1 if all needed parts have been matched.
         */
        private int getNeedCount(SearchItem item) {
            boolean missing = false;
            Iterator<Node> neededNodeIter = item.needsNodes().iterator();
            while (!missing && neededNodeIter.hasNext()) {
                missing = remainingNodes.contains(neededNodeIter.next());
            }
            Iterator<String> neededVarIter = item.needsVars().iterator();
            while (!missing && neededVarIter.hasNext()) {
                missing = remainingVars.contains(neededVarIter.next());
            }
            return missing ? 0 : 1;
        }
        
        /** 
         * Returns the number of nodes bound by the item that have already been matched. 
         * More pre-matched nodes means less determinism, so the higher the better.
         */
        private int getConnectCount(SearchItem item) {
            int result = item.needsNodes().size() + item.needsVars().size();
            for (Node node: item.bindsNodes()) {
                if (!remainingNodes.contains(node)) {
                    result++;
                }
            }
            for (String var: item.bindsVars()) {
                if (remainingVars.contains(var)) {
                    result++;
                }
            }
            return result;
        }
        
        /** 
         * Returns the number of nodes and variables bound by the
         * item that have not yet been matched. 
         * More unmatched parts means more non-determinism, so the lower the better.
         */
        private int getBindCount(SearchItem item) {
            int result = 0;
            for (Node node: item.bindsNodes()) {
                if (remainingNodes.contains(node)) {
                    result++;
                }
            }
            for (String var: item.bindsVars()) {
                if (remainingVars.contains(var)) {
                    result++;
                }
            }
            return result;
        }
        
        /** The set of (as yet) unscheduled nodes. */
        private final Set<Node> remainingNodes;
        /** The set of (as yet) unscheduled variables. */
        private final Set<String> remainingVars;
    }
    
    /**
     * Edge comparator for regular expression edges.
     * An edge is better if it is not regular, or if the automaton is not reflexive.
     * @author Arend Rensink
     * @version $Revision $
     */
    static class ItemTypeComparator implements Comparator<SearchItem> {
        /**
         * Compares two regular expression-based items, with the purpose of determining which one should
         * be scheduled first. In order from worst to best:
         * <ul>
         * <li> {@link NodeSearchItem}s of a non-specialised type
         * <li> {@link RegExprEdgeSearchItem}s
         * <li> {@link VarEdgeSearchItem}s
         * <li> {@link WildcardEdgeSearchItem}s
         * <li> {@link EdgeSearchItem}s of a non-specialised type
         * <li> {@link ConditionSearchItem}s
         * <li> {@link OperatorEdgeSearchItem}s
         * <li> {@link ValueNodeSearchItem}s
         * </ul>
         */
        public int compare(SearchItem o1, SearchItem o2) {
            return getRating(o1) - getRating(o2);
        }
        
        /**
         * Computes a rating for a search item from its type.
         * A higher rating is better.
         */
        int getRating(SearchItem item) {
            int result = 0;
            Class<?> itemClass = item.getClass();
            if (itemClass == NodeSearchItem.class) {
                return result;
            } 
            result++;
            if (itemClass == RegExprEdgeSearchItem.class) {
                return result;
            } 
            result ++;
            if (itemClass == VarEdgeSearchItem.class) {
                return result;
            } 
            result++;
            if (itemClass == WildcardEdgeSearchItem.class) {
                return result;
            } 
            result++;
            if (itemClass == EdgeSearchItem.class) {
                return result;
            } 
            result++;
            if (ConditionSearchItem.class.isAssignableFrom(itemClass)) {
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
         * Constructs a comparator on the basis of two lists of labels.
         * The first list contains high-priority labels, in the order of decreasing priority;
         * the second list low-priority labels, in order of increasing priority.
         * Labels not in either list have intermediate priority and are ordered
         * alphabetically.
         * @param rare high-priority labels, in order of decreasing priority; may be <code>null</code>
         * @param common low-priority labels, in order of increasing priority; may be <code>null</code>
         */
        FrequencyComparator(List<String> rare, List<String> common) {
            this.priorities = new HashMap<String, Integer>();
            if (rare != null) {
                for (int i = 0; i < rare.size(); i++) {
                    priorities.put(rare.get(i), rare.size() - i);
                }
            }
            if (common != null) {
                for (int i = 0; i < common.size(); i++) {
                    priorities.put(common.get(i), i - common.size());
                }
            }
        }

        /**
         * Favours the edge occurring earliest in the high-priority labels, or
         * latest in the low-priority labels. In case of equal priority, alphabetical ordering is used.
         */
        public int compare(SearchItem first, SearchItem second) {
            if (first instanceof EdgeSearchItem && second instanceof EdgeSearchItem) {
                String firstLabel = ((EdgeSearchItem) first).getEdge().label().text();
                String secondLabel = ((EdgeSearchItem) second).getEdge().label().text();
                // compare edge priorities
                return getEdgePriority(firstLabel) - getEdgePriority(secondLabel);
            } else {
                return 0;
            }
        }

        /**
         * Returns the priority of an edge, judged by its label.
         */
        private int getEdgePriority(String edgeLabel) {
            Integer result = priorities.get(edgeLabel);
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
        private final Map<String, Integer> priorities;
    }
    
    /**
     * Comparator determining the ordering in which the search item comparators should be applied.
     * Comparators will be applied in increating order, so the comparators should be ordered
     * in decreasing priority.
     * @author Arend Rensink
     * @version $Revision: 1.10 $
     */
    static private class ItemComparatorComparator implements Comparator<Comparator<SearchItem>> {
        /** 
         * Returns the difference in ratings between the two comparators. 
         * This means lower-rated comparators are ordered first.
         */
        public int compare(Comparator<SearchItem> o1, Comparator<SearchItem> o2) {
            return getRating(o1) - getRating(o2);
        }
        
        /**
         * Comparators are rated as follows, in increasing order:
         * <ul>
         * <li> {@link NeededPartsComparator}
         * <li> {@link ItemTypeComparator}
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
            if (compClass == FrequencyComparator.class) {
                return result;
            }
            result++;
            if (compClass == IndegreeComparator.class) {
                return result;
            }
            throw new IllegalArgumentException(String.format("Unknown comparator class %s", compClass));
        }
    }
}
