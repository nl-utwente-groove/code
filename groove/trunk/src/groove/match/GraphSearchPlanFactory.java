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
 * $Id: GraphSearchPlanFactory.java,v 1.3 2007-08-28 22:01:20 rensink Exp $
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
import groove.util.Bag;
import groove.util.HashBag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

/**
 * Strategy that yields the edges in order of ascending indegree of
 * their source nodes.
 * The idea is that the "roots" of a graph (those starting in nodes with
 * small indegree) are likely to give a better immediate reduction of
 * the number of possible matches.
 * Furthermore, regular expression edges are saved to the last.
 * @author Arend Rensink
 * @version $Revision: 1.3 $
 */
public class GraphSearchPlanFactory {
    /** 
     * Private, empty constructor.
     * This is a ginleton class; get the instance through {@link #getInstance()}.
     */
    GraphSearchPlanFactory() {
        // empty
    }
//    /** 
//     * Factory method returning a search strategy for matching a given graph. 
//     * This is a convenience method for {@link #createSearchPlan(Graph, Collection, Collection)} with
//     * empty sets of pre-matched nodes and edges.
//     * @param graph the graph that is to be matched
//     * @return a search strategy to look for a subgraph matchings of <code>graph</code>
//     */
//	public Iterable<SearchItem> createSearchPlan(Graph graph) {
//        return createSearchPlan(graph, Collections.<Node>emptySet(), Collections.<Edge>emptySet());
//	}
//
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
    public SearchPlanStrategy createSearchPlan(Graph graph, Collection<? extends Node> preMatchedNodes, Collection<? extends Edge> preMatchedEdges) {
        PlanData data = new PlanData(graph, preMatchedNodes, preMatchedEdges);
        return new SearchPlanStrategy(data.getPlan(), false);
    }

    /**
	 * Callback method creating the list of comparators that is used to
	 * construct the search plan.
     * @param nodeSet view on the set of currently unmtched nodes
     * @param edgeSet view on the set of currently unmtched edges
     * @param varSet view on the set of currently unmtched variables
	 */
	protected List<Comparator<SearchItem>> createComparators(Set<Node> nodeSet, Set<Edge> edgeSet, Set<String> varSet) {
		List<Comparator<SearchItem>> result = new ArrayList<Comparator<SearchItem>>();
        result.add(new NeededPartsComparator(nodeSet, varSet));
        result.add(new ItemTypeComparator());
		result.add(new IndegreeComparator(edgeSet));
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
    protected class PlanData extends Observable implements Comparator<SearchItem> {
        /**
         * Construct a given plan data object for a given graph,
         * with certain sets of already pre-matched elements.
         * @param graph the graph to be matched by the plan
         * @param preMatchedNodes the set of pre-matched nodes
         * @param preMatchedEdges the set of pre-matched edges
         */
        public PlanData(Graph graph, Collection<? extends Node> preMatchedNodes, Collection<? extends Edge> preMatchedEdges) {
            remainingNodes = new HashSet<Node>(graph.nodeSet());
            if (preMatchedNodes != null) {
                remainingNodes.removeAll(preMatchedNodes);
            }
            remainingEdges = new HashSet<Edge>();
            remainingVars = new HashSet<String>();
            for (Edge edge : graph.edgeSet()) {
                remainingEdges.add(edge);
                RegExpr edgeExpr = RegExprLabel.getRegExpr(edge.label());
                if (edgeExpr != null) {
                    remainingVars.addAll(edgeExpr.allVarSet());
                }
            }
            if (preMatchedEdges != null) {
                for (Edge edge: preMatchedEdges) {
//                    assert preMatchedNodes.containsAll(Arrays.asList(edge.ends())) : String.format("Ends of pre-matched edge %s not in pre-matched nodes %s", edge, preMatchedNodes);
                    remainingEdges.remove(edge);
                    RegExpr edgeExpr = RegExprLabel.getRegExpr(edge.label());
                    if (edgeExpr != null) {
                        remainingVars.removeAll(edgeExpr.boundVarSet());
                    }                    
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
            Collection<SearchItem> items = getSearchItems();
            while (!items.isEmpty()) {
                SearchItem bestItem = Collections.max(items, this);
                result.add(bestItem);
                items.remove(bestItem);
                remainingNodes.removeAll(bestItem.bindsNodes());
                remainingVars.removeAll(bestItem.bindsVars());
            }
//            // copy the node and edge sets to avoid sharing problems
//            Set<Node> remainingNodes = new HashSet<Node>(remainingNodes);
//            Set<Edge> remainingEdges = new HashSet<Edge>(edgeSet);
//            // first do all non-variable value nodes, 
//            // and remove all value and product nodes
//            Iterator<Node> nodeIter = remainingNodes.iterator();
//            while (nodeIter.hasNext()) {
//                Node node = nodeIter.next();
//                SearchItem nodeItem = createNodeSearchItem(node);
//                if (node instanceof ValueNode) {
//                    // value nodes always match themselve, and so can be scheduled straight away
//                    if (((ValueNode) node).hasValue()) {
//                        result.add(nodeItem);
//                        items.add(nodeItem);
//                        nodeIter.remove();
//                    }
//                } else if (node instanceof ProductNode) {
//                    // product nodes will not be matched explicitly
//                    // (rather, the outgoing operator edges will be matched)
//                    nodeIter.remove();                  
//                } else {
//                    items.add(nodeItem);
//                }
//            }
//            // collect the non-constant operator edges, and remove all
//            // operator and argument edges
//            Map<ProductEdge,Set<Node>> operatorEdgeMap = new HashMap<ProductEdge,Set<Node>>();
//            Iterator<Edge> edgeIter = remainingEdges.iterator();
//            while (edgeIter.hasNext()) {
//                Edge edge = edgeIter.next();
//                if (edge instanceof ProductEdge) {
//                    ProductEdge operatorEdge = (ProductEdge) edge;
//                    if (operatorEdge.getOperation().arity() > 0) {
//                        operatorEdgeMap.put(operatorEdge, new HashSet<Node>(operatorEdge.source().getArguments()));
//                    }
//                    edgeIter.remove();
//                } else if (edge instanceof AlgebraEdge) {
//                    edgeIter.remove();
//                }
//            }
//            // pick the best remaining edge each time and add it to the
//            // result, adjusting the remaining edges, nodes and indegrees
//            while (! remainingEdges.isEmpty()) {
//                scheduleOperatorEdges(result, operatorEdgeMap);
//                Edge bestEdge = Collections.max(remainingEdges, this);
//                int arity = bestEdge.endCount();
//                boolean allMatched = true;
//                boolean[] matched = new boolean[arity];
//                for (int i = 0; i < arity; i++) {
//                    matched[i] = !remainingNodes.remove(bestEdge.end(i));
//                    allMatched &= matched[i];
//                }
//                if (allMatched) {
//                    matched = null;
//                }
//                result.add(createEdgeSearchItem(bestEdge));
//                remainingEdges.remove(bestEdge);
//                setChanged();
//                notifyObservers(bestEdge);
//            }
//            scheduleOperatorEdges(result, operatorEdgeMap);
//            // remaining nodes are loose nodes: add them
//            for (Node node: remainingNodes) {
//                assert ! (node instanceof ValueNode) : String.format("Remaining value node %s", node);
//                result.add(createNodeSearchItem(node));
//            }
//            assert operatorEdgeMap.isEmpty() : String.format("Remaining operation edges %s", operatorEdgeMap.keySet());
            return result;
        }

        /** Returns the collection of search items for the plan. */
        protected Collection<SearchItem> getSearchItems() {
            Collection<SearchItem> result = new ArrayList<SearchItem>();
            Set<Node> unmatchedNodes = new HashSet<Node>(remainingNodes);
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
         * Schedules all produce edges of which the arguments have 
         * already been scheduled (i.e., are no longer in the remaining nodes).
         */
        private void scheduleOperatorEdges(List<SearchItem> result, Map<ProductEdge, Set<Node>> productEdgeMap) {
            // flag to indicate there might yeat be something to be scheduled
            boolean schedule;
            do {
                schedule = false;
                // see if there is a product edge whose arguments are all
                // matched
                Iterator<Map.Entry<ProductEdge, Set<Node>>> productEdgeIter = productEdgeMap.entrySet().iterator();
                while (productEdgeIter.hasNext()) {
                    Map.Entry<ProductEdge, Set<Node>> productEdgeEntry = productEdgeIter.next();
                    // schedule the edge if all arguments are matched
                    if (!new HashSet<Node>(productEdgeEntry.getValue()).removeAll(remainingNodes)) {
                        ProductEdge productEdge = productEdgeEntry.getKey();
                        boolean targetMatched = !remainingNodes.remove(productEdge.target());
                        boolean[] matched = new boolean[] { true, targetMatched };
                        result.add(createEdgeSearchItem(productEdge));
                        productEdgeIter.remove();
                        schedule = true;
                    }
                }
            } while (schedule);
        }
        
        /**
         * Orders edges according to the lexicographic order of 
         * the available edge comparators.
         */
        public int compare(SearchItem o1, SearchItem o2) {
            int result = 0;
            Iterator<Comparator<SearchItem>> comparatorIter = getComparators().iterator();
            while (result == 0 && comparatorIter.hasNext()) {
                Comparator<SearchItem> next = comparatorIter.next();
                result = next.compare(o1, o2);
            }
            return result;
        }
        
        /**
         * Lazily creates and returns the set of edge comparators that determines 
         * their priority in the search plan.
         */
        protected List<Comparator<SearchItem>> getComparators() {
            if (comparators == null) {
                comparators = createComparators();
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
         * Constructs the list of edge comparators to be used.
         */
        protected List<Comparator<SearchItem>> createComparators() {
            return GraphSearchPlanFactory.this.createComparators(remainingNodes, remainingEdges, remainingVars);
        }

        /**
         * The set of nodes to be matched.
         */
        protected final Set<Node> remainingNodes;
        /**
         * The set of edges to be matched.
         */
        protected final Set<Edge> remainingEdges;
        /**
         * The set of variables to be matched.
         */
        protected final Set<String> remainingVars;
        /**
         * The comparators used to determine the order in which the edges
         * should be matched.
         */
        private List<Comparator<SearchItem>> comparators;
        /** Flag determining if {@link #getPlan()} was already called. */
        private boolean used;
    }

    /**
     * Edge comparator based on the number of already matched end nodes of a given
     * edge. The higher this number, the less nondeterminism matching the node
     * will cause, so the sooner it should be scheduled.
     * The class is an observer in order to be able to maintain its internal data.
     * @author Arend Rensink
     * @version $Revision $
     */
    protected static class ConnectivityComparator implements Comparator<Edge> {
        /**
         * Constructs a comparator, on the basis of the set of 
         * currently matched nodes. This set is shared with the caller.
         * @param remainingNodes the set of remaining unmatched nodes.
         */
        private ConnectivityComparator(Set<? extends Node> remainingNodes) {
            this.remainingNodes = new HashSet<Node>(remainingNodes);
        }
        
        /** 
         * Favours the edge with the highest number of nodes not in 
         * the set of remaining nodes.
         */
        public int compare(Edge first, Edge second) {
            return connectivity(first) - connectivity(second);
        }
//
//        /**
//         * This method is called when a new edge is scheduled.
//         * It removes the end nodes of that edge from the set of remaining nodes.
//         */
//        public void update(Observable o, Object arg) {
//            for (Node end: ((Edge) arg).ends()) {
//                remainingNodes.remove(end);
//            }
//        }

        /**
         * Returns the number of matched end points of a given edge.
         * An end point is matched if it is not in the set of remaining nodes.
         */
        private int connectivity(Edge edge) {
            int result = 0;
            for (Node end : edge.ends()) {
                if (!remainingNodes.contains(end)) {
                    result++;
                }
            }
            return result;
        }
        
        /** The set of currently unmatched nodes. */
        private final Set<Node> remainingNodes; 
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
    static protected class IndegreeComparator implements Comparator<SearchItem>, Observer {
        /**
         * Constructs a comparator on the basis of a given set of unmatched edges.
         */
        private IndegreeComparator(Set<? extends Edge> remainingEdges) {
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
            Edge selected = (Edge) arg;
            for (Node end: selected.ends()) {
                if (!end.equals(selected.source())) {
                    indegrees.remove(end);
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
     * @version $Revision: 1.3 $
     */
    private static class NeededPartsComparator implements Comparator<SearchItem> {
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
         * Returns the number of nodes and variables bound by the
         * item that have not yet been matched. The lower this number,
         * the more desirable it is to schedule the item.
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
    private static class ItemTypeComparator implements Comparator<SearchItem> {
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
}
