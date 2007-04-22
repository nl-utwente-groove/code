/* $Id: FixedDeltaGraph.java,v 1.1 2007-04-22 23:32:22 rensink Exp $ */
package groove.graph;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Arend Rensink
 * @version $Revision $
 */
public class FixedDeltaGraph extends AbstractGraph<GraphCache> {
	/** 
	 * Constructs a graph with a given basis and delta
	 * The basis may be <code>null</code>, meaning that it is the empty graph.
	 * @param basis the basis for the new delta graph; possibly <code>null</code>
	 * @param delta the delta with respect to the basis; non-<code>null</code>
	 */
	public FixedDeltaGraph(final FixedDeltaGraph basis, final DeltaStore delta) {
		this.basis = basis;
		this.delta = delta;
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
			initNodeEdgeSet();
		}
		return nodeSet;
	}
	
	public Set<Edge> edgeSet() {
		if (edgeSet == null) {
			initNodeEdgeSet();
		}
		return edgeSet;
	}
	
	@Override
	public Set<? extends Edge> edgeSet(Node node, int i) {
		// TODO Auto-generated method stub
		return super.edgeSet(node, i);
	}

	@Override
	public Set<? extends Edge> edgeSet(Node node) {
		// TODO Auto-generated method stub
		return super.edgeSet(node);
	}

	@Override
	public Map<Label, ? extends Set<? extends Edge>> labelEdgeMap(int i) {
		// TODO Auto-generated method stub
		return super.labelEdgeMap(i);
	}

	@Override
	public Set<? extends Edge> labelEdgeSet(int arity, Label label) {
		// TODO Auto-generated method stub
		return super.labelEdgeSet(arity, label);
	}

	@Override
	public Map<Node, Set<Edge>> nodeEdgeMap() {
		// TODO Auto-generated method stub
		return super.nodeEdgeMap();
	}

	@Override
	public Set<? extends Edge> outEdgeSet(Node node) {
		// TODO Auto-generated method stub
		return super.outEdgeSet(node);
	}

	private void initNodeEdgeSet() {
		nodeSet = createNodeSet();
		if (basis != null) {
			nodeSet.addAll(basis.nodeSet());
		}
		edgeSet = createEdgeSet();
		if (basis != null) {
			edgeSet.addAll(basis.edgeSet());
		}
		delta.applyDelta(new NodeEdgeTarget(nodeSet, edgeSet));
	}

	private Set<Edge> createEdgeSet() {
		return new HashSet<Edge>();
	}

	private Set<Node> createNodeSet() {
		return new NodeSet();
	}
	
	/** The fixed (possibly <code>null</code> basis of this graph. */
	private final FixedDeltaGraph basis;
	/** The fixed delta of this graph. */
	private final DeltaStore delta;
	
	/** The (initially null) edge set of this graph. */
	private Set<Edge> edgeSet;
	/** The (initially null) node set of this graph. */
	private Set<Node> nodeSet;
	
	/** Delta target to initialise the node and edge sets. */
	static private class NodeEdgeTarget implements DeltaTarget {
		/** Constructs and instance for a given node and edge set. */
		public NodeEdgeTarget(final Set<Node> nodeSet, final Set<Edge> edgeSet) {
			super();
			this.nodeSet = nodeSet;
			this.edgeSet = edgeSet;
		}

		public boolean addEdge(Edge elem) {
			return edgeSet.add(elem);
		}

		public boolean addNode(Node elem) {
			return nodeSet.add(elem);
		}

		public boolean removeEdge(Edge elem) {
			return edgeSet.remove(elem);
		}

		public boolean removeNode(Node elem) {
			return nodeSet.add(elem);
		}
		
		private final Set<Node> nodeSet;
		private final Set<Edge> edgeSet;
	}
}
