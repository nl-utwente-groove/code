/* $Id: FixedDeltaGraph.java,v 1.5 2007-05-25 22:16:46 rensink Exp $ */
package groove.graph;

import groove.graph.iso.CertificateStrategy;
import groove.util.CollectionOfCollections;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
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
 */
public class FixedDeltaGraph extends AbstractGraph<GraphCache> implements DeltaGraphFactory<FixedDeltaGraph> {
	/** 
	 * Constructs a graph with a given basis and delta
	 * The basis may be <code>null</code>, meaning that it is the empty graph.
	 * @param basis the basis for the new delta graph; possibly <code>null</code>
	 * @param delta the delta with respect to the basis; non-<code>null</code>
	 */
	public FixedDeltaGraph(final FixedDeltaGraph basis, final DeltaApplier delta) {
		this.basis = basis;
		this.delta = delta;
		super.setFixed();
	}
//	
//	@Override
//	public boolean isFixed() {
//		return true;
//	}

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

	public FixedDeltaGraph newGraph(FixedDeltaGraph basis, DeltaApplier applier) {
		return new FixedDeltaGraph(basis, applier);
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
		if (nodeSet == null) {
			initData();
		}
		return nodeSet;
	}
	
	public Set<Edge> edgeSet() {
		if (edgeSet == null) {
			initData();
		}
		return Collections.unmodifiableSet(edgeSet);
	}
	
	@Override
	public Map<Label, Set<Edge>> labelEdgeMap(int i) {
		return getLabelEdgeMaps().get(i);
	}
	
	@Override
	protected List<Map<Label, Set<Edge>>> getLabelEdgeMaps() {
		if (labelEdgeMaps == null) {
			if (nodeSet == null) {
				initData();
			}
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
	
	/** Indicates if the label-to-edge map has been initialised. */
	private boolean isLabelEdgeMapSet() {
		return labelEdgeMaps != null;
	}

	@Override
	public Map<Node, Set<Edge>> nodeEdgeMap() {
		if (nodeEdgeMap == null) {
			if (nodeSet == null) {
				initData();
			}
			if (nodeEdgeMap == null) { 
				nodeEdgeMap = computeNodeEdgeMap();
			}
		}
		testNodeEdgeMap();
		return nodeEdgeMap;
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
	
	/** Indicates if the label-to-edge map has been initialised. */
	private boolean isNodeEdgeMapSet() {
		return nodeEdgeMap != null;
	}

	private void testNodeEdgeMap() {
		assert edgeSet == null || nodeEdgeMap == null || edgeSet.equals(new HashSet<Edge>(new CollectionOfCollections<Edge>(nodeEdgeMap.values()))) : "Map not correct: \nEdges "+edgeSet+" not compatible with \nnode/edge map"+nodeEdgeMap;
	}
	
	/** 
	 * Computes all the data structures that are available from
	 * the basis graph.
	 */
	private void initData() {
		// initialise the node set from the basis
		assert nodeSet == null;
		nodeSet = createNodeSet();
		if (basis != null) {
			nodeSet.addAll(basis.nodeSet());
		}
		// initialise the edge set from the basis
		assert edgeSet == null;
		edgeSet = createEdgeSet();
		if (basis != null) {
			edgeSet.addAll(basis.edgeSet());
		}
		// initialise the node-edge map from the basis, if it is set in the basis
		assert nodeEdgeMap == null;
		if (basis != null && basis.isNodeEdgeMapSet()) {
			nodeEdgeMap = new HashMap<Node,Set<Edge>>(basis.nodeEdgeMap());
		}
		// initialise the label-edge map list from the basis, if it is set in the basis
		assert labelEdgeMaps == null;
		if (basis != null && basis.isLabelEdgeMapSet()) {
			labelEdgeMaps = new ArrayList<Map<Label, Set<Edge>>>();
			for (Map<Label, Set<Edge>> arityLabelEdgeMap : basis.getLabelEdgeMaps()) {
				if (arityLabelEdgeMap == null) {
					labelEdgeMaps.add(null);
				} else {
					labelEdgeMaps.add(new HashMap<Label, Set<Edge>>(arityLabelEdgeMap));
				}
			}
		}
		// apply the delta to fill the structures
		delta.applyDelta(new Target(nodeSet, edgeSet, nodeEdgeMap, labelEdgeMaps));
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
	private final FixedDeltaGraph basis;
	/** The fixed delta of this graph. */
	private final DeltaApplier delta;
	
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
	static private final DeltaGraphFactory instance = new FixedDeltaGraph(null,null);
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
			if (nodeEdgeMap != null) {
				this.freshNodeKeys = new HashSet<Node>();
			} else {
				this.freshNodeKeys = null;
			}
			this.labelEdgeMaps = labelEdgeMaps;
			if (labelEdgeMaps != null) {
				this.freshLabelKeys = new ArrayList<Set<Label>>();
				for (Map<Label, Set<Edge>> arityLabelEdgeMap : labelEdgeMaps) {
					if (arityLabelEdgeMap == null) {
						freshLabelKeys.add(null);
					} else {
						freshLabelKeys.add(new HashSet<Label>());
					}
				}
			} else {
				this.freshLabelKeys = null;
			}
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
					if (! freshNodeKeys.contains(end)) {
						nodeEdgeMap.put(end, edgeSet = createEdgeSet(edgeSet));
						freshNodeKeys.add(end);
					}
					edgeSet.add(elem);
				}
			}
			// adapt label-edge map
			if (labelEdgeMaps != null) {
				Label label = elem.label();
				Map<Label,Set<Edge>> arityLabelEdgeMap = labelEdgeMaps.get(arity);
				Set<Edge> edgeSet = arityLabelEdgeMap.get(label);
				Set<Label> freshArityLabelKeys = freshLabelKeys.get(arity);
				if (! freshArityLabelKeys.contains(label)) {
					arityLabelEdgeMap.put(label, edgeSet = createEdgeSet(edgeSet));
					freshArityLabelKeys.add(label);
				}
				edgeSet.add(elem);
			}
			return result;
		}

		/** Adds the node to the node set and the node-edge map. */
		public boolean addNode(Node elem) {
			boolean result = nodeSet.add(elem);
			assert result;
			if (nodeEdgeMap != null) {
				Set<Edge> edges = nodeEdgeMap.put(elem, new HashSet<Edge>());
				assert edges == null : String.format("Node %s already has incident edges %s", elem, edges);
				freshNodeKeys.add(elem);
			}
			return result;
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
					if (edgeSet != null) {
					if (! freshNodeKeys.contains(end)) {
						nodeEdgeMap.put(end, edgeSet = createEdgeSet(edgeSet));
						freshNodeKeys.add(end);
					}
					edgeSet.remove(elem);
					}
				}
			}
			// adapt label-edge map
			if (labelEdgeMaps != null) {
				Label label = elem.label();
				Map<Label,Set<Edge>> arityLabelEdgeMap = labelEdgeMaps.get(arity);
				Set<Edge> edgeSet = arityLabelEdgeMap.get(label);
				Set<Label> freshArityLabelKeys = freshLabelKeys.get(arity);
				if (! freshArityLabelKeys.contains(label)) {
					arityLabelEdgeMap.put(label, edgeSet = createEdgeSet(edgeSet));
					freshArityLabelKeys.add(label);
				}
				edgeSet.remove(elem);
			}
			return result;
		}

		/** Removes the node from the node set and the node-edge map. */
		public boolean removeNode(Node elem) {
			boolean result = nodeSet.remove(elem);
			assert result;
			if (nodeEdgeMap != null) {
				Set<Edge> edges = nodeEdgeMap.remove(elem);
				assert edges.isEmpty();
				freshNodeKeys.remove(elem);
			}
			return result;
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
		/** Auxiliary set to determine the nodes changed w.r.t. the basis. */
		private final Set<Node> freshNodeKeys;
		/** Auxiliary set to determine the labels changed w.r.t. the basis. */
		private final List<Set<Label>> freshLabelKeys;
	}
}
