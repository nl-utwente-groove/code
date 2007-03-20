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
 * $Id: DefaultSearchPlanFactory.java,v 1.1.1.2 2007-03-20 10:42:44 kastenberg Exp $
 */
package groove.graph.match;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Node;
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
 * @version $Revision: 1.1.1.2 $
 */
public class DefaultSearchPlanFactory implements SearchPlanFactory {
	/**
	 * Edge comparator based on the number of already matched end nodes of a given
	 * edge. The higher this number, the less nondeterminism matching the node
	 * will cause, so the sooner it should be scheduled.
	 * The class is an observer in order to be able to maintain its internal data.
	 * @author Arend Rensink
	 * @version $Revision $
	 */
	private class ConnectivityComparator implements Comparator<Edge>, Observer {
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
	private class IndegreeComparator implements Comparator<Edge>, Observer {
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
	 * Internal class to collect the data necessary to create a plan
	 * and to create the actual plan. 
	 * @author Arend Rensink
	 * @version $Revision $
	 */
	protected class PlanData extends Observable implements Comparator<Edge> {
		/**
		 * Constructs a data object on the basis of a given node and edge set.
		 * The sets are shared so should not be changed.
		 * @param nodeSet the set of nodes to be matched in the plan
		 * @param edgeSet the set of edges to be matched in the plan
		 */
		public PlanData(Set<? extends Node> nodeSet, Set<? extends Edge> edgeSet) {
			this.nodeSet = nodeSet;
			this.edgeSet = edgeSet;
		}

		/**
		 * Creates and returns a search plan on the basis of the given data.
		 */
		public List<SearchItem> getPlan() {
			List<SearchItem> result = new ArrayList<SearchItem>();
			// copy the node and edge sets to avoid sharing problems
			Set<Node> remainingNodes = new HashSet<Node>(nodeSet);
			Set<Edge> remainingEdges = new HashSet<Edge>(edgeSet);
	        // pick the best remaining edge each time and add it to the
	        // result, adjusting the remaining edges, nodes and indegrees
	        while (! remainingEdges.isEmpty()) {
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
//	            } else {
//	            	for (int i = 0; i < arity; i++) {
//						if (!matched[i]) {
//							remainingNodes.remove(bestEdge.end(i));
//						}
//					}
	            }
	            result.add(createEdgeSearchItem(bestEdge, matched));
	            remainingEdges.remove(bestEdge);
	            setChanged();
	            notifyObservers(bestEdge);
	        }
	        // remaining nodes are loose nodes: add them
	        for (Node node: remainingNodes) {
	        	result.add(createNodeSearchItem(node));
	        }
	        return result;
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
		 * Getter for the list of comparators to be used for constructing the plan.
		 * At each stage, the maximal element is chosen according to the lexicographic
		 * ordering established by this list of edge comparators.
		 */
		protected List<Comparator<Edge>> getComparators() {
			// construct the comparators lazily
			if (comparators == null) {
				comparators = computeComparators();
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
		protected List<Comparator<Edge>> computeComparators() {
			List<Comparator<Edge>> result = DefaultSearchPlanFactory.this.createComparators(nodeSet, edgeSet);
			return result;
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
	}
	
	/**
	 * Creates the search plan by constructing a {@link groove.graph.match.DefaultSearchPlanFactory.PlanData} object
	 * and then invoking {@link groove.graph.match.DefaultSearchPlanFactory.PlanData#getPlan()}.
	 */
	public Iterable<SearchItem> createSearchPlan(Graph graph) {
        PlanData data = new PlanData(graph.nodeSet(), graph.edgeSet());
        return data.getPlan();
	}

	/**
	 * Callback method creating the list of comparators that is used to
	 * construct the searc plan.
	 */
	protected List<Comparator<Edge>> createComparators(Set<? extends Node> nodeSet, Set<? extends Edge> edgeSet) {
		List<Comparator<Edge>> result = new ArrayList<Comparator<Edge>>();
		result.add(new ConnectivityComparator(nodeSet));
		result.add(new IndegreeComparator(edgeSet));
		return result;
	}
	
    /**
	 * Callback factory method for creating an edge search item.
	 */
	protected SearchItem createEdgeSearchItem(Edge edge, boolean[] matched) {
		return new EdgeSearchItem<Edge>(edge, matched);
	}

	/**
     * Callback factory method for creating a node search item.
     */
    protected SearchItem createNodeSearchItem(Node node) {
    	return new NodeSearchItem(node);
    }
}
