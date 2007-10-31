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
 * $Id: SwingDeltaGraph.java,v 1.12 2007-09-25 22:57:53 rensink Exp $
 */
package groove.graph;

import groove.graph.iso.CertificateStrategy;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class to serve to capture the graphs associated with graph states.
 * These have the characteristic that they are fixed, and are defined by a
 * delta to another graph (where the delta is the result of a rule application). 
 * @author Arend Rensink
 * @version $Revision $
 * @deprecated use {@link NewDeltaGraph} instead
 */
@Deprecated
public class SwingDeltaGraph extends AbstractGraph<GraphCache> implements DeltaGraphFactory<SwingDeltaGraph> {
	/** 
	 * Constructs a graph with a given basis and delta
	 * The basis may be <code>null</code>, meaning that it is the empty graph.
	 * @param basis the basis for the new delta graph; possibly <code>null</code>
	 * @param delta the delta with respect to the basis; non-<code>null</code>
	 */
	private SwingDeltaGraph(final SwingDeltaGraph basis, final DeltaApplier delta) {
		this.basis = basis;
		if (delta == null || delta instanceof DeltaStore || delta instanceof FrozenDeltaApplier) {
			this.delta = delta;
		} else {
			this.delta = new DeltaStore(delta);
		}
		setFixed();
	}
	
	/**
	 * Since the result should be modifiable, returns a {@link DeltaGraph}.
	 */
	@Override
	public Graph clone() {
		return new DeltaGraph(this);
	}

	/**
	 * Since the result should be modifiable, returns a {@link DeltaGraph}.
	 */
	public DeltaGraph newGraph() {
		return new DeltaGraph();
	}

	public SwingDeltaGraph newGraph(SwingDeltaGraph graph, DeltaApplier applier) {
		return new SwingDeltaGraph(graph, applier);
	}

	/** 
	 * Since the graph is fixed, this method always throws an exception.
	 * @throws UnsupportedOperationException always.
	 */
	public boolean addEdge(Edge edge) {
		throw new UnsupportedOperationException();
	}

	/** 
	 * Since the graph is fixed, this method always throws an exception.
	 * @throws UnsupportedOperationException always.
	 */
	public boolean addNode(Node node) {
		throw new UnsupportedOperationException();
	}
	
	/** 
	 * Since the graph is fixed, this method always throws an exception.
	 * @throws UnsupportedOperationException always.
	 */
	public boolean removeEdge(Edge edge) {
		throw new UnsupportedOperationException();
	}
	
	/** 
	 * Since the graph is fixed, this method always throws an exception.
	 * @throws UnsupportedOperationException always.
	 */
	public boolean removeNode(Node node) {
		throw new UnsupportedOperationException();
	}
	
	/** 
	 * Since the graph is fixed, this method always throws an exception.
	 * @throws UnsupportedOperationException always.
	 */
	public boolean addEdgeWithoutCheck(Edge edge) {
		throw new UnsupportedOperationException();
	}
	
	/** 
	 * Since the graph is fixed, this method always throws an exception.
	 * @throws UnsupportedOperationException always.
	 */
	public boolean removeNodeWithoutCheck(Node node) {
		throw new UnsupportedOperationException();
	}

	public Set<Node> nodeSet() {
		if (nodeSet == null && nodeEdgeMap == null) {
			initData();
		}
		if (nodeSet != null) {
			return nodeSet;
		} else {
			return nodeEdgeMap.keySet();
		}
	}
	
	public Set<Edge> edgeSet() {
		if (edgeSet == null) {
			initData();
		}
		return edgeSet;
	}
	
	@Override
	public Map<Label, Set<Edge>> labelEdgeMap(int i) {
		return getLabelEdgeMaps().get(i);
	}
	
	@Override
	protected List<Map<Label, Set<Edge>>> getLabelEdgeMaps() {
		if (labelEdgeMaps == null) {
		    initData();
			if (labelEdgeMaps == null) {
				labelEdgeMaps = computeLabelEdgeMaps();
			}
		}
		return labelEdgeMaps;
	}

	/** 
	 * Computes the label-to-edgeset map from the node and edge sets.
	 * This method is only used if the map could not be obtained from the basis.
	 */
	private List<Map<Label,Set<Edge>>> computeLabelEdgeMaps() {
		List<Map<Label,Set<Edge>>> result = new ArrayList<Map<Label,Set<Edge>>>();
		result.add(null);
		for (int i = 0; i <= AbstractEdge.getMaxEndCount(); i++) {
			result.add(new HashMap<Label,Set<Edge>>());
		}
		for (Edge edge: edgeSet()) {
			Map<Label,Set<Edge>> labelEdgeMap = result.get(edge.endCount());
			Set<Edge> edges = labelEdgeMap.get(edge.label());
			if (edges == null) {
				labelEdgeMap.put(edge.label(), edges = new HashSet<Edge>());
			}
			edges.add(edge);
		}
		return result;
	}
	
	@Override
	public Map<Node, Set<Edge>> nodeEdgeMap() {
		if (nodeEdgeMap == null) {
		    initData();
			if (nodeEdgeMap == null) { 
				nodeEdgeMap = computeNodeEdgeMap();
			}
		}
		return nodeEdgeMap;
	}
		
	@Override
	public Set<? extends Edge> edgeSet(Node node) {
		return nodeEdgeMap().get(node);
	}

	/** 
	 * Computes the node-to-edgeset map from the node and edge sets.
	 * This method is only used if the map could not be obtained from the basis.
	 */
	private Map<Node, Set<Edge>> computeNodeEdgeMap() {
		Map<Node,Set<Edge>> result = new HashMap<Node,Set<Edge>>();
		for (Node node: nodeSet()) {
			result.put(node, new HashSet<Edge>());
		}
		for (Edge edge: edgeSet()) {
			for (int i = 0; i < edge.endCount(); i++) {
				result.get(edge.end(i)).add(edge);
			}
		}
		return result;
	}
	
	/** 
	 * Initialises all the data structures, if this has not yet been done.
	 */
	private void initData() {
		reporter.start(INIT_DATA);
		if (edgeSet == null) {
            assert nodeSet == null;
            assert edgeSet == null;
            assert nodeEdgeMap == null;
            assert labelEdgeMaps == null;
            if (basis == null) {
                nodeSet = createNodeSet();
                edgeSet = createEdgeSet();
                // apply the delta to fill the structures
                delta.applyDelta(new Target(nodeSet, edgeSet, null, null));
            } else {
                basis.transferData(this);
                basis = null;
            }
        }
		reporter.stop();
	}

    /** Transfers the data from this graph to a child graph. */
    private void transferData(SwingDeltaGraph child) {
        reporter.start(INIT_DATA);
        assert child.basis == this;
        assert child.nodeSet == null;
        assert child.edgeSet == null;
        assert child.nodeEdgeMap == null;
        assert child.labelEdgeMaps == null;
        // initialise own data, if necessary
        initData();
        DeltaApplier delta = child.delta;
        // if the node-edge map is set, no need to construct the node set
        if (nodeEdgeMap != null) {
        	nodeSet = null;
        }
        // apply the delta to fill the structures
        delta.applyDelta(new Target(nodeSet, edgeSet, nodeEdgeMap, labelEdgeMaps));
        child.nodeSet = nodeSet;
        child.edgeSet = edgeSet;
        child.nodeEdgeMap = nodeEdgeMap;
        child.labelEdgeMaps = labelEdgeMaps;
        child.delta = null;
        reporter.stop();
        this.nodeSet = null;
        this.edgeSet = null;
        this.nodeEdgeMap = null;
        this.labelEdgeMaps = null;
        this.certifier = null;
        if (this.delta == null) {
            this.basis = child;
            this.delta = ((DeltaStore) delta).invert(true);
        }
    }
    
	private Set<Edge> createEdgeSet() {
		return new HashSet<Edge>();
	}

	private Set<Node> createNodeSet() {
		return new NodeSet();
	}

	@Override
	public CertificateStrategy getCertifier() {
		if (certifier == null || certifier.get() == null) {
			certifier = new WeakReference<CertificateStrategy>(AbstractGraph.getCertificateFactory().newInstance(this));
		}
		return certifier.get(); 
	}

	/** The fixed (possibly <code>null</code> basis of this graph. */
	private SwingDeltaGraph basis;
	/** The fixed delta of this graph. */
	private DeltaApplier delta;
	
	/** The (initially null) edge set of this graph. */
	private Set<Edge> edgeSet;
	/** The (initially null) node set of this graph. */
	private Set<Node> nodeSet;
	/** The map from nodes to sets of incident edges. */
	private Map<Node,Set<Edge>> nodeEdgeMap;
	/** List of maps from labels to sets of edges with that label and arity. */
	private List<Map<Label,Set<Edge>>> labelEdgeMaps;
	/** The certificate strategy of this graph, set on demand. */
	private Reference<CertificateStrategy> certifier;

	/** Factory instance of this class. */
	static private final DeltaGraphFactory instance = new SwingDeltaGraph(null,null);
	/** Returns a fixed factory instance of the {@link FixedDeltaGraph} class. */
	static public DeltaGraphFactory getInstance() {
		return instance;
	}
	
	/** Delta target to initialise the data structures. */
	static private class Target implements DeltaTarget {
		/** Constructs and instance for a given node and edge set. */
		public Target(final Set<Node> nodeSet, final Set<Edge> edgeSet, Map<Node,Set<Edge>> nodeEdgeMap, List<Map<Label,Set<Edge>>> labelEdgeMaps) {
			this.nodeSet = nodeSet;
			this.edgeSet = edgeSet;
			this.nodeEdgeMap = nodeEdgeMap;
			this.labelEdgeMaps = labelEdgeMaps;
		}

		/** 
		 * Adds the edge to the edge set, the node-edge map (if it is set),
		 * and the label-edge maps (if it is set). 
		 */
		public boolean addEdge(Edge elem) {
			boolean result = edgeSet.add(elem);
			assert result;
			int arity = elem.endCount();
			// adapt node-edge map
			if (nodeEdgeMap != null) {
				for (int i = 0; i < arity; i++) {
					Node end = elem.end(i);
					Set<Edge> edgeSet = nodeEdgeMap.get(end);
					if (edgeSet == null) {
						nodeEdgeMap.put(end, edgeSet = createEdgeSet(edgeSet));
					}
					edgeSet.add(elem);
				}
			}
			// adapt label-edge map
			if (labelEdgeMaps != null) {
				Label label = elem.label();
				Map<Label,Set<Edge>> arityLabelEdgeMap = labelEdgeMaps.get(arity);
				Set<Edge> edgeSet = arityLabelEdgeMap.get(label);
				if (edgeSet == null) {
					arityLabelEdgeMap.put(label, edgeSet = createEdgeSet(edgeSet));
				}
				edgeSet.add(elem);
			}
			return result;
		}

		/** Adds the node to the node set and the node-edge map. */
		public boolean addNode(Node elem) {
			if (nodeSet != null) {
				boolean result = nodeSet.add(elem);
				assert result;
			}
			if (nodeEdgeMap != null) {
				Set<Edge> edges = nodeEdgeMap.put(elem, new HashSet<Edge>());
				assert edges == null;
			}
			return true;
		}

		/** 
		 * Removes the edge from the edge set, the node-edge map (if it is set),
		 * and the label-edge maps (if it is set). 
		 */
		public boolean removeEdge(Edge elem) {
			boolean result = edgeSet.remove(elem);
			assert result;
			int arity = elem.endCount();
			// adapt node-edge map
			if (nodeEdgeMap != null) {
				for (int i = 0; i < arity; i++) {
					Node end = elem.end(i);
					Set<Edge> edgeSet = nodeEdgeMap.get(end);
					assert edgeSet != null : String.format("Node edge map %s does not contain image for %s", nodeEdgeMap, end);
					edgeSet.remove(elem);
				}
			}
			// adapt label-edge map
			if (labelEdgeMaps != null) {
				Label label = elem.label();
				Map<Label,Set<Edge>> arityLabelEdgeMap = labelEdgeMaps.get(arity);
				Set<Edge> edgeSet = arityLabelEdgeMap.get(label);
				edgeSet.remove(elem);
			}
			return result;
		}

		/** Removes the node from the node set and the node-edge map. */
		public boolean removeNode(Node elem) {
			if (nodeSet != null) {
				boolean result = nodeSet.remove(elem);
				assert result;
			}
			if (nodeEdgeMap != null) {
				Set<Edge> edges = nodeEdgeMap.remove(elem);
				assert edges.isEmpty();
			}
			return true;
		}
		
		/** 
		 * Creates a copy of an existing set of edges, or an empty set if the
		 * given set is <code>null</code>.
		 */
		private Set<Edge> createEdgeSet(Set<Edge> edgeSet) {
			if (edgeSet == null) {
				return new HashSet<Edge>();
			} else {
				return new HashSet<Edge>(edgeSet);
			}
		}

		/** Node set to be filled by this target. */
		private final Set<Node> nodeSet;
		/** Edge set to be filled by this target. */
		private final Set<Edge> edgeSet;
		/** Node/edge map to be filled by this target. */
		private final Map<Node,Set<Edge>> nodeEdgeMap;
		/** Label/edge map to be filled by this target. */
		private final List<Map<Label,Set<Edge>>> labelEdgeMaps;
	}
}
