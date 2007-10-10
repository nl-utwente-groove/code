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
 * $Id: RichDeltaGraph.java,v 1.3 2007-10-10 08:59:58 rensink Exp $
 */
package groove.graph;

import groove.graph.iso.CertificateStrategy;
import groove.util.KeyPartition;
import groove.util.StackedSet;
import groove.util.TreeHashSet;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
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
public class RichDeltaGraph extends AbstractGraph<GraphCache> implements DeltaGraphFactory<RichDeltaGraph> {
	/** 
	 * Constructs a graph with a given basis and delta
	 * The basis may be <code>null</code>, meaning that it is the empty graph.
	 * @param basis the basis for the new delta graph; possibly <code>null</code>
	 * @param delta the delta with respect to the basis; non-<code>null</code>
	 */
	private RichDeltaGraph(final RichDeltaGraph basis, final DeltaApplier delta) {
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

	public RichDeltaGraph newGraph(RichDeltaGraph graph, DeltaApplier applier) {
		return new RichDeltaGraph(graph, applier);
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
		if (outEdgeMap == null) {
			initData();
		}
		return outEdgeMap.keySet();
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
				labelEdgeMap.put(edge.label(), edges = createEdgeSet(null));
			}
			edges.add(edge);
		}
		return result;
	}
//	
//	@Override
//	public Map<Node, Set<Edge>> nodeEdgeMap() {
//		if (nodeEdgeMap == null) {
//		    initData();
//			if (nodeEdgeMap == null) { 
//				nodeEdgeMap = computeNodeEdgeMap();
//			}
//		}
//		return nodeEdgeMap;
//	}
		
	@Override
	public Set<? extends Edge> edgeSet(Node node) {
		initData();
		Set<BinaryEdge> outEdgeSet = outEdgeMap.getCell(node);
		Set<BinaryEdge> inEdgeSet = inEdgeMap.getCell(node);
		return new StackedSet<BinaryEdge>(outEdgeSet, inEdgeSet, inEdgeSet);
	}

	@Override
	public int edgeCount() {
		initData();
		return outEdgeMap.size();
	}

	@Override
	public Set<? extends Edge> edgeSet(Node node, int i) {
		if (i == Edge.SOURCE_INDEX) {
			return outEdgeSet(node);
		} else {
			return inEdgeSet(node);
		}
	}

	@Override
	public Set<? extends Edge> outEdgeSet(Node node) {
		initData();
		return outEdgeMap.getCell(node);
	}

	public Set<BinaryEdge> inEdgeSet(Node node) {
		initData();
		return inEdgeMap.getCell(node);
	}

	public Set<Edge> edgeSet(Node node, int arity, Label label) {
		initData();
		assert arity == 2;
		Map<Node,Set<Edge>> nodeEdgeMap = getLabelMap().get(label);
		if (nodeEdgeMap == null) {
			return null;
		} else {
			return nodeEdgeMap.get(node);
		}
	}
	
	private Map<Label,Map<Node,Set<Edge>>> getLabelMap() {
		if (labelMap == null) {
			labelMap = new HashMap<Label,Map<Node,Set<Edge>>>();
			for (Edge edge: edgeSet()) {
				addEdge(labelMap, edge);
			}
		}
		return labelMap;
	}
	
	/** 
	 * Initialises all the data structures, if this has not yet been done.
	 */
	private void initData() {
		reporter.start(INIT_DATA);
		if (edgeSet == null) {
            assert edgeSet == null;
            assert outEdgeMap == null;
            assert inEdgeMap == null;
            assert labelEdgeMaps == null;
            if (basis == null) {
                inEdgeMap = new KeyPartition<Node,BinaryEdge>(true) {
					@Override
					protected Node getKey(Object value) {
						if (value instanceof BinaryEdge) {
							return ((BinaryEdge) value).target();
						} else {
							return null;
						}
					}

					@Override
					protected Set<BinaryEdge> createCell() {
						return createEdgeSet(null);
					}        
                };
                outEdgeMap = new KeyPartition<Node,BinaryEdge>(true) {
					@Override
					protected Node getKey(Object value) {
						if (value instanceof BinaryEdge) {
							return ((BinaryEdge) value).source();
						} else {
							return null;
						}
					}                	

					@Override
					protected Set<BinaryEdge> createCell() {
						return createEdgeSet(null);
					}        
                };
                edgeSet = createEdgeSet(null);
                // apply the delta to fill the structures
                delta.applyDelta(new SwingTarget());
            } else {
                DataTarget target = basis.getDataTarget();
                // apply the delta to fill the structures
                delta.applyDelta(target);
                target.install(this);
            }
        }
		reporter.stop();
	}

    /** 
     * Creates a delta target that will construct the
     * necessary data structures for a child graph.
     */
    private DataTarget getDataTarget() {
        reporter.start(TRANSFER_DATA);
        // initialise own data, if necessary
        initData();
        DataTarget result = new SwingTarget();
        reporter.stop();
        return result;
    }

	/** 
	 * Creates a copy of an existing set of edges, or an empty set if the
	 * given set is <code>null</code>.
	 */
	<E extends Edge> EdgeSet<E> createEdgeSet(EdgeSet<E> edgeSet) {
	    if (edgeSet == null) {
	        return new EdgeSet<E>();
	    } else {
	        return new EdgeSet<E>(edgeSet);
	    }
	}

	@Override
	public CertificateStrategy getCertifier() {
		if (certifier == null || certifier.get() == null) {
			certifier = new WeakReference<CertificateStrategy>(AbstractGraph.getCertificateFactory().newInstance(this));
		}
		return certifier.get(); 
	}

	/** The fixed (possibly <code>null</code> basis of this graph. */
	RichDeltaGraph basis;
	/** The fixed delta of this graph. */
	DeltaApplier delta;
	
	/** The (initially null) edge set of this graph. */
	EdgeSet<Edge> edgeSet;
	/** The map from nodes to sets of incident edges. */
	KeyPartition<Node,BinaryEdge> outEdgeMap;
	/** The map from nodes to sets of incident edges. */
	KeyPartition<Node,BinaryEdge> inEdgeMap;
	/** List of maps from labels to sets of edges with that label and arity. */
	List<Map<Label,Set<Edge>>> labelEdgeMaps;
	Map<Label,Map<Node,Set<Edge>>> labelMap;
	/** The certificate strategy of this graph, set on demand. */
	private Reference<CertificateStrategy> certifier;
	/** Factory instance of this class. */
	static private final RichDeltaGraph instance = new RichDeltaGraph(null,null);
	/** 
	 * Returns a fixed factory instance of the {@link RichDeltaGraph} class.
	 */
	static public RichDeltaGraph getInstance() {
		return instance;
	}
	
	static void addEdge(Map<Label,Map<Node,Set<Edge>>> map, Edge edge) {
		Map<Node,Set<Edge>> nodeEdgeMap = map.get(edge.label());
		if (nodeEdgeMap == null) {
			map.put(edge.label(), nodeEdgeMap = new HashMap<Node,Set<Edge>>());
		}
		Node source = edge.source();
		Set<Edge> outEdgeSet = nodeEdgeMap.get(source);
		if (outEdgeSet == null) {
			nodeEdgeMap.put(source, outEdgeSet = new EdgeSet<Edge>());
		}
		boolean result = outEdgeSet.add(edge);
		assert result;
		Node opposite = edge.opposite();
		if (opposite != source) {
			Set<Edge> inEdgeSet = nodeEdgeMap.get(opposite);
			if (inEdgeSet == null) {
				nodeEdgeMap.put(opposite, inEdgeSet = new EdgeSet<Edge>());
			}
			inEdgeSet.add(edge);
		}
	}
	
	static void removeEdge(Map<Label,Map<Node,Set<Edge>>> map, Edge edge) {
		Map<Node,Set<Edge>> nodeEdgeMap = map.get(edge.label());
		Node source = edge.source();
		nodeEdgeMap.get(source).remove(edge);
		Node opposite = edge.opposite();
		if (opposite != source) {
			nodeEdgeMap.get(opposite).remove(edge);
		}
	}
	
	/**
	 * Specialisation of a set of edges, for use inside this class.
	 */
	static private class EdgeSet<E extends Edge> extends TreeHashSet<E> {
	    /** Creates an empty edge set. */
        public EdgeSet() {
            super();
        }
        /** Creates a copy of an existing edge set. */
        public EdgeSet(EdgeSet<E> other) {
            super(other);
        }	    
	}
	
	/**
	 * Superclass for data construction targets.
	 * Subclasses should fill the instance variables of this
	 * class during construction time and the invocation of 
	 * the {@link DeltaTarget} add and remove methods.
	 * @author Arend Rensink
	 * @version $Revision $
	 */
	static abstract private class DataTarget implements DeltaTarget {
		/** Empty constructor with correct visibility. */
		DataTarget() {
			// empty
		}
		/** 
		 * Assigns the data structures computed in this data object
		 * to a given delta graph.
		 * @param child the graph to which the data structures should be installed
		 */
		void install(RichDeltaGraph child) {
			child.edgeSet = edgeSet;
			child.outEdgeMap = outEdgeMap;
			child.inEdgeMap = inEdgeMap;
			child.labelEdgeMaps = labelEdgeMaps;
			child.labelMap = labelMap;
			child.delta = null;
			child.basis = null;
		}
		
		/** Edge set to be filled by this target. */
		EdgeSet<Edge> edgeSet;
		/** Outgoing edge map to be filled by this target. */
		KeyPartition<Node,BinaryEdge> outEdgeMap;
		/** Incoming edge map to be filled by this target. */
		KeyPartition<Node,BinaryEdge> inEdgeMap;
		/** Label/edge map to be filled by this target. */
		List<Map<Label,Set<Edge>>> labelEdgeMaps;
		/** Label//node/edge map to be filled by this target. */
		Map<Label,Map<Node,Set<Edge>>> labelMap;
	}
	
	/** Delta target to initialise the data structures. */
	private class SwingTarget extends DataTarget {
		/** Constructs and instance for a given node and edge set. */
		public SwingTarget() {
			RichDeltaGraph graph = RichDeltaGraph.this;
			this.edgeSet = graph.edgeSet;
			this.outEdgeMap = graph.outEdgeMap;
			this.inEdgeMap = graph.inEdgeMap;
			this.labelEdgeMaps = graph.labelEdgeMaps;
			this.labelMap = graph.labelMap;
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
			result = outEdgeMap.add((BinaryEdge) elem);
			assert result;
			if (inEdgeMap != null) {
				result = inEdgeMap.add((BinaryEdge) elem);
				assert result;
			}
			// adapt label-edge map
			if (labelEdgeMaps != null) {
				Label label = elem.label();
				Map<Label,Set<Edge>> arityLabelEdgeMap = labelEdgeMaps.get(arity);
				EdgeSet<Edge> edgeSet = (EdgeSet) arityLabelEdgeMap.get(label);
				if (edgeSet == null) {
					arityLabelEdgeMap.put(label, edgeSet = createEdgeSet(edgeSet));
				}
				result = edgeSet.add(elem);
				assert result;
			}
			if (labelMap != null) {
				RichDeltaGraph.addEdge(labelMap, elem);
			}
			return result;
		}

		/** Adds the node to the node set and the node-edge map. */
		public boolean addNode(Node elem) {
			outEdgeMap.addCell(elem);
			if (inEdgeMap != null) {
				inEdgeMap.addCell(elem);
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
			result = outEdgeMap.remove(elem);
			assert result;
			if (inEdgeMap != null) {
				result = inEdgeMap.remove(elem);
				assert result;
			}
			// adapt label-edge map
			if (labelEdgeMaps != null) {
				Label label = elem.label();
				Map<Label,Set<Edge>> arityLabelEdgeMap = labelEdgeMaps.get(arity);
				Set<Edge> edgeSet = arityLabelEdgeMap.get(label);
				result = edgeSet.remove(elem);
				assert result;
			}
			if (labelMap != null) {
				RichDeltaGraph.removeEdge(labelMap, elem);
			}
			return result;
		}

		/** Removes the node from the node set and the node-edge map. */
		public boolean removeNode(Node elem) {
			outEdgeMap.removeCell(elem);
			if (inEdgeMap != null) {
				inEdgeMap.removeCell(elem);
			}
			if (labelMap != null) {
				for (Map<Node,Set<Edge>> nodeEdgeMap: labelMap.values()) {
					Set<Edge> removed = nodeEdgeMap.remove(elem);
					assert removed == null || removed.isEmpty() : String.format("Removed node %s has remaining edges %s", elem, removed);
				}
			}
			return true;
		}
		
		@Override
		void install(RichDeltaGraph child) {
			RichDeltaGraph graph = RichDeltaGraph.this;
			graph.edgeSet = null;
			graph.inEdgeMap = null;
			graph.outEdgeMap = null;
			graph.labelEdgeMaps = null;
			graph.labelMap = null;
			if (graph.delta == null) {
				graph.basis = child;
				graph.delta = ((DeltaStore) child.delta).invert(true);
			}
			super.install(child);
		}
	}
}
