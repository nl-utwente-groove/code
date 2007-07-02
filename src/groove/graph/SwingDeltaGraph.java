/* $Id: SwingDeltaGraph.java,v 1.2 2007-07-02 07:21:32 rensink Exp $ */
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
 */
public class SwingDeltaGraph extends AbstractGraph<GraphCache> implements DeltaGraphFactory<SwingDeltaGraph> {
	/** 
	 * Constructs a graph with a given basis and delta
	 * The basis may be <code>null</code>, meaning that it is the empty graph.
	 * @param basis the basis for the new delta graph; possibly <code>null</code>
	 * @param delta the delta with respect to the basis; non-<code>null</code>
	 */
	public SwingDeltaGraph(final SwingDeltaGraph basis, final DeltaApplier delta) {
		assert delta != null;
		this.basis = basis;
		if (delta == null || delta instanceof DeltaStore) {
			this.delta = (DeltaStore) delta;
		} else {
			this.delta = new DeltaStore(delta);
		}
	}
	
	@Override
	public boolean isFixed() {
		return true;
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
		if (nodeSet == null) {
			initData();
		}
		return nodeSet;
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

	/** 
	 * Computes all the data structures that are available from
	 * the basis graph.
	 */
	private void initData() {
		reporter.start(INIT_DATA);
		assert nodeSet == null;
		assert edgeSet == null;
		assert nodeEdgeMap == null;
		assert labelEdgeMaps == null;
		if (basis == null) {
			nodeSet = createNodeSet();
			edgeSet = createEdgeSet();
			// apply the delta to fill the structures
			delta.applyDelta(new Target(nodeSet, edgeSet, null, null));
			delta = null;
		} else {
			nodeSet = basis.nodeSet();
			edgeSet = basis.edgeSet();
			// initialise the node-edge map from the basis, if it is set in the basis
			if (basis.isNodeEdgeMapSet()) {
				nodeEdgeMap = basis.nodeEdgeMap();
			}
			if (basis.isLabelEdgeMapSet()) {
				labelEdgeMaps = basis.getLabelEdgeMaps();
			}
			// apply the delta to fill the structures
			delta.applyDelta(new Target(nodeSet, edgeSet, nodeEdgeMap, labelEdgeMaps));
			basis.releaseData(this, delta);
			basis = null;
			delta = null;
		}
		reporter.stop();
	}

	private void releaseData(SwingDeltaGraph basis, DeltaStore basisDelta) {
		this.nodeSet = null;
		this.edgeSet = null;
		this.nodeEdgeMap = null;
		this.labelEdgeMaps = null;
		this.certifier = null;
		this.basis = basis;
		this.delta = basisDelta.invert();
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
	private DeltaStore delta;
	
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
			boolean result = nodeSet.add(elem);
			assert result;
			if (nodeEdgeMap != null) {
				Set<Edge> edges = nodeEdgeMap.put(elem, new HashSet<Edge>());
				assert edges == null;
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
			boolean result = nodeSet.remove(elem);
			assert result;
			if (nodeEdgeMap != null) {
				Set<Edge> edges = nodeEdgeMap.remove(elem);
				assert edges.isEmpty();
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
	}
}
