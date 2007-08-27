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
 * $Id: GraphSearchPlanFactory.java,v 1.2 2007-08-27 07:25:11 rensink Exp $
 */
package groove.match;

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

/**
 * Strategy that yields the edges in order of ascending indegree of
 * their source nodes.
 * The idea is that the "roots" of a graph (those starting in nodes with
 * small indegree) are likely to give a better immediate reduction of
 * the number of possible matches.
 * Furthermore, regular expression edges are saved to the last.
 * @author Arend Rensink
 * @version $Revision: 1.2 $
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
     * no pre-matched edges
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
	 */
	protected List<Comparator<Edge>> createComparators(Set<? extends Node> nodeSet, Set<? extends Edge> edgeSet) {
		List<Comparator<Edge>> result = new ArrayList<Comparator<Edge>>();
        result.add(new RegExprComparator());
		result.add(new ConnectivityComparator(nodeSet));
		result.add(new IndegreeComparator(edgeSet));
		return result;
	}
	
    /**
	 * Callback factory method for creating an edge search item.
	 */
	protected SearchItem createEdgeSearchItem(Edge edge, boolean[] matched) {
        Label label = edge.label();
        RegExpr negOperand = RegExprLabel.getNegOperand(label);
        if (negOperand instanceof RegExpr.Empty) {
            return createInjectionSearchItem(Arrays.asList(edge.ends()));
        } else if (negOperand != null) {
            Edge negatedEdge = DefaultEdge.createEdge(edge.source(), negOperand.toLabel(), edge.opposite());
            return createNegatedSearchItem(createEdgeSearchItem(negatedEdge, matched));
        } else if (RegExprLabel.getWildcardId(label) != null) {
            return new VarEdgeSearchItem(edge, matched);
        } else if (RegExprLabel.isWildcard(label)) {
            return new WildcardEdgeSearchItem(edge, matched);
        } else if (RegExprLabel.isAtom(label)) {
            Edge defaultEdge = DefaultEdge.createEdge(edge.source(), RegExprLabel.getAtomText(label), edge.opposite());
            return new EdgeSearchItem(defaultEdge, matched);
        } else if (label instanceof RegExprLabel) {
            return new RegExprEdgeSearchItem(edge, matched);
        } else if (edge instanceof ProductEdge) {
			return new OperatorEdgeSearchItem((ProductEdge) edge, matched);
		} else {
			return new EdgeSearchItem(edge, matched);
		}
	}

	/**
     * Callback factory method for creating a node search item.
     */
    protected SearchItem createNodeSearchItem(Node node) {
    	if (node instanceof ValueNode) {
    		return new ValueNodeSearchItem((ValueNode) node);
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
    protected class PlanData extends Observable implements Comparator<Edge> {
        /**
         * Construct a given plan data object for a given graph,
         * with certain sets of already pre-matched elements.
         * @param graph the graph to be matched by the plan
         * @param preMatchedNodes the set of pre-matched nodes
         * @param preMatchedEdges the set of pre-matched edges
         */
        public PlanData(Graph graph, Collection<? extends Node> preMatchedNodes, Collection<? extends Edge> preMatchedEdges) {
            nodeSet = new HashSet<Node>(graph.nodeSet());
            if (preMatchedNodes != null) {
                nodeSet.removeAll(preMatchedNodes);
            }
            edgeSet = new HashSet<Edge>(graph.edgeSet());
            if (preMatchedEdges != null) {
                edgeSet.removeAll(preMatchedEdges);
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
            // copy the node and edge sets to avoid sharing problems
            Set<Node> remainingNodes = new HashSet<Node>(nodeSet);
            Set<Edge> remainingEdges = new HashSet<Edge>(edgeSet);
            // first do all non-variable value nodes, 
            // and remove all value and product nodes
            Iterator<Node> nodeIter = remainingNodes.iterator();
            while (nodeIter.hasNext()) {
                Node node = nodeIter.next();
                if (node instanceof ValueNode) {
                    if (((ValueNode) node).hasValue()) {
                        result.add(createNodeSearchItem(node));
                        nodeIter.remove();
                    }
                } else if (node instanceof ProductNode) {
                    nodeIter.remove();                  
                }
            }
            // collect the non-constant operator edges, and remove all
            // operator and argument edges
            Map<ProductEdge,Set<Node>> productEdgeMap = new HashMap<ProductEdge,Set<Node>>();
            Iterator<Edge> edgeIter = remainingEdges.iterator();
            while (edgeIter.hasNext()) {
                Edge edge = edgeIter.next();
                if (edge instanceof ProductEdge) {
                    ProductEdge productEdge = (ProductEdge) edge;
                    if (productEdge.getOperation().arity() > 0) {
                        productEdgeMap.put(productEdge, new HashSet<Node>(productEdge.source().getArguments()));
                    }
                    edgeIter.remove();
                } else if (edge instanceof AlgebraEdge) {
                    edgeIter.remove();
                }
            }
            // pick the best remaining edge each time and add it to the
            // result, adjusting the remaining edges, nodes and indegrees
            while (! remainingEdges.isEmpty()) {
                scheduleProductEdges(result, remainingNodes, productEdgeMap);
                Edge bestEdge = Collections.max(remainingEdges, this);
                int arity = bestEdge.endCount();
                boolean allMatched = true;
                boolean[] matched = new boolean[arity];
                for (int i = 0; i < arity; i++) {
                    matched[i] = !remainingNodes.remove(bestEdge.end(i));
                    allMatched &= matched[i];
                }
                if (allMatched) {
                    matched = null;
                }
                result.add(createEdgeSearchItem(bestEdge, matched));
                remainingEdges.remove(bestEdge);
                setChanged();
                notifyObservers(bestEdge);
            }
            scheduleProductEdges(result, remainingNodes, productEdgeMap);
            // remaining nodes are loose nodes: add them
            for (Node node: remainingNodes) {
                assert ! (node instanceof ValueNode) : String.format("Remaining value node %s", node);
                result.add(createNodeSearchItem(node));
            }
            assert productEdgeMap.isEmpty() : String.format("Remaining operator edges %s", productEdgeMap.keySet());
            return result;
        }

        /**
         * Schedules all produce edges of which the arguments have 
         * already been scheduled (i.e., are no longer in the remaining nodes).
         */
        private void scheduleProductEdges(List<SearchItem> result, Set<Node> remainingNodes, Map<ProductEdge, Set<Node>> productEdgeMap) {
            // flag to indicate there might yeat be something to be scheduled
            boolean schedule;
            do {
                schedule = false;
                // see if there is a product edge whose arguments are all
                // matched
                Iterator<Map.Entry<ProductEdge, Set<Node>>> productEdgeIter = productEdgeMap.entrySet().iterator();
                while (productEdgeIter.hasNext()) {
                    Map.Entry<ProductEdge, Set<Node>> productEdgeEntry = productEdgeIter.next();
                    Set<Node> arguments = new HashSet<Node>(productEdgeEntry.getValue());
                    if (!arguments.removeAll(remainingNodes)) {
                        ProductEdge productEdge = productEdgeEntry.getKey();
                        boolean targetMatched = !remainingNodes.remove(productEdge.target());
                        boolean[] matched = new boolean[] { true, targetMatched };
                        result.add(createEdgeSearchItem(productEdge, matched));
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
        public int compare(Edge o1, Edge o2) {
            int result = 0;
            Iterator<Comparator<Edge>> comparatorIter = getComparators().iterator();
            while (result == 0 && comparatorIter.hasNext()) {
                Comparator<Edge> next = comparatorIter.next();
                result = next.compare(o1, o2);
            }
            if (result == 0) {
                return o1.compareTo(o2);
            } else {
                return result;
            }
        }
        
        /**
         * Lazily creates and returns the set of edge comparators that determines 
         * their priority in the search plan.
         */
        protected List<Comparator<Edge>> getComparators() {
            if (comparators == null) {
                comparators = createComparators();
                // add those comparators as listeners that implement the observer interface
                for (Comparator<Edge> comparator: comparators) {
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
        protected List<Comparator<Edge>> createComparators() {
            return GraphSearchPlanFactory.this.createComparators(nodeSet, edgeSet);
        }

        /**
         * The set of nodes to be matched.
         */
        protected final Set<? extends Node> nodeSet;
        /**
         * The set of edges to be matched.
         */
        protected final Set<? extends Edge> edgeSet;
        /**
         * The comparators used to determine the order in which the edges
         * should be matched.
         */
        private List<Comparator<Edge>> comparators;
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
    protected static class ConnectivityComparator implements Comparator<Edge>, Observer {
        /**
         * Constructs a comparator, on the basis of the set of 
         * already matched nodes. This set is updated during the matching
         * process.
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

        /**
         * This method is called when a new edge is scheduled.
         * It removes the end nodes of that edge from the set of remaining nodes.
         */
        public void update(Observable o, Object arg) {
            for (Node end: ((Edge) arg).ends()) {
                remainingNodes.remove(end);
            }
        }

        /**
         * Returns the number of matched end points of a given edge.
         * An end point is matched if it is not in the set of remaining nodes.
         */
        private int connectivity(Edge edge) {
            int result = 0;
            for (int i = 0; i < edge.endCount(); i++) {
                Node end = edge.end(i);
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
    static protected class IndegreeComparator implements Comparator<Edge>, Observer {
        /**
         * Constructs a comparator on the basis of the set of currently unmatched
         * edges.
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
        public int compare(Edge second, Edge first) {
            int result;
            // first test for the indegree of the source (lower = better)
            result = indegree(second.source()) - indegree(first.source()); 
            if (result == 0) {
                // now test for the indegree of the target (higher = better)
                result = indegree(second.opposite()) - indegree(first.opposite()); 
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
     * Edge comparator for regular expression edges.
     * An edge is better if it is not regular, or if the automaton is not reflexive.
     * @author Arend Rensink
     * @version $Revision $
     */
    private static class RegExprComparator implements Comparator<Edge> {
        /**
         * Compares two labels, with the purpose of determining which one should
         * be tried first. The rules are as follows:
         * <ul>
         * <li> Regular expression labels are worse than others
         * <li> Reflexive regular expressions are worse than others
         * </ul>
         */
        public int compare(Edge o1, Edge o2) {
            Label first = o1.label();
            Label second = o2.label();
            int result = compare(second instanceof RegExprLabel,
                    first instanceof RegExprLabel);
            if (result == 0 && first instanceof RegExprLabel) {
                // remove the potential outer negation from the labels
                if (RegExprLabel.isNeg(first)) {
                    first = RegExprLabel.getNegOperand(first).toLabel();
                }
                if (RegExprLabel.isNeg(second)) {
                    second = RegExprLabel.getNegOperand(second).toLabel();
                }
                result = compare(!((RegExprLabel) first).getAutomaton().isAcceptsEmptyWord(),
                        !((RegExprLabel) second).getAutomaton().isAcceptsEmptyWord());
            }
            return result;
        }

        /**
         * Compares two booleans, and returns the result of the comparison as an integer.
         * @return <code>+1</code> if <code>first</code> is <code>true</code>
         * but <code>second</code> is not, <code>-1</code> if the reverse is the case,
         * and <code>0</code> if their values are equal.
         */
        protected int compare(boolean first, boolean second) {
            return first ? (second ? 0 : +1) : (second ? -1 : 0);
        }
    }
}
