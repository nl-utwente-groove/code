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
 * $Id: PreMatchSearchItem.java,v 1.2 2007-09-25 11:21:06 iovka Exp $
 */
package groove.match;

import groove.graph.Edge;
import groove.graph.Node;
import groove.match.SearchPlanStrategy.Search;
import groove.rel.VarSupport;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Search item that reflects (and optionally checks)
 * that a set of elements (nodes, variables and edges) have
 * already been matched.
 * @author Arend Rensink
 * @version $Revision $
 */
public class PreMatchSearchItem extends AbstractSearchItem {
	/** 
	 * Creates an instance with given sets of pre-matched nodes, edges and variables.
	 * @param nodes the set of pre-matched nodes; not <code>null</code>
	 * @param edges the set of pre-matched edges; not <code>null</code> 
	 */
	PreMatchSearchItem(Collection<? extends Node> nodes, Collection<? extends Edge> edges) {
		this.nodes = new HashSet<Node>(nodes);
		this.edges = new HashSet<Edge>(edges);
		this.vars = new HashSet<String>();
		for (Edge edge: edges) {
			vars.addAll(VarSupport.getAllVars(edge));
		}
	}
	
	/** This implementation returns the set of pre-matched edges. */
	@Override
	public Collection<? extends Edge> bindsEdges() {
		return edges;
	}

	/** This implementation returns the set of pre-matched nodes. */
	@Override
	public Collection<? extends Node> bindsNodes() {
		return nodes;
	}

	/** This implementation returns the set of pre-matched variables. */
	@Override
	public Collection<String> bindsVars() {
		return vars;
	}

	/** 
	 * This item gets the highest rating
	 * since it should be scheduled first. 
	 */
	@Override
	int getRating() {
		return Integer.MAX_VALUE;
	}

	public void activate(SearchPlanStrategy strategy) {
		nodeIxMap = new HashMap<Node,Integer>();
		for (Node node: nodes) {
			nodeIxMap.put(node, strategy.getNodeIx(node));
		}
		edgeIxMap = new HashMap<Edge,Integer>();
		for (Edge edge: edges) {
			edgeIxMap.put(edge, strategy.getEdgeIx(edge));
		}
		varIxMap = new HashMap<String,Integer>();
		for (String var: vars) {
			varIxMap.put(var, strategy.getVarIx(var));
		}
	}

	public Record getRecord(Search search) {
		return new PreMatchRecord(search);
	}

	/** The set of pre-matched nodes. */
	private final Set<Node> nodes;
	/** The set of pre-matched edges. */
	private final Set<Edge> edges;
	/** The set of pre-matched variables. */
	private final Set<String> vars;
	/** Mapping from pre-matched nodes (in {@link #nodes}) to their indices in the result. */
	private Map<Node,Integer> nodeIxMap;
	/** Mapping from pre-matched edges (in {@link #edges}) to their indices in the result. */
	private Map<Edge,Integer> edgeIxMap;
	/** Mapping from pre-matched variables (in {@link #vars}) to their indices in the result. */
	private Map<String,Integer> varIxMap;
	
	
	/**
	 * @author Arend Rensink
	 * @version $Revision $
	 */
	public class PreMatchRecord implements Record {
		/** Constructs an instance for a given search. */
		public PreMatchRecord(Search search) {
			assert allElementsMatched(search) : String.format("Elements %s not pre-matched", unmatched);
		}

		public boolean find() {
			found = !found;
			return found;
		}

		private boolean allElementsMatched(Search search) {
			unmatched = new HashSet<Object>();
			for (Map.Entry<Node,Integer> nodeEntry: nodeIxMap.entrySet()) {
				if (search.getNode(nodeEntry.getValue()) == null) {
					unmatched.add(nodeEntry.getKey());
				}
			}
			for (Map.Entry<Edge,Integer> edgeEntry: edgeIxMap.entrySet()) {
				if (search.getEdge(edgeEntry.getValue()) == null) {
					unmatched.add(edgeEntry.getKey());
				}
			}
			for (Map.Entry<String,Integer> varEntry: varIxMap.entrySet()) {
				if (search.getVar(varEntry.getValue()) == null) {
					unmatched.add(varEntry.getKey());
				}
			}
			return unmatched.isEmpty();
		}
		
		public boolean isSingular() {
			return true;
		}

		public void reset() {
			found = false;
		}
		
		/** 
		 * Indicates if {@link #find()} has returned <code>true</code>
		 * on the last invocation (so the next invocation should return <code>false</code>).
		 */
		private boolean found;
		/** The set of unmatched graph elements (that should have been pre-matched) . */
		private Set<Object> unmatched;
	}
}
