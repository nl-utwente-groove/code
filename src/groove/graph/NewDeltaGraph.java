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
 * $Id: NewDeltaGraph.java,v 1.5 2007-10-10 08:59:58 rensink Exp $
 */
package groove.graph;

import groove.graph.iso.CertificateStrategy;
import groove.util.TreeHashSet;

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
public class NewDeltaGraph extends AbstractGraph<GraphCache> implements DeltaGraphFactory<NewDeltaGraph> {
	/** 
	 * Constructs a graph with a given basis and delta
	 * The basis may be <code>null</code>, meaning that it is the empty graph.
	 * @param basis the basis for the new delta graph; possibly <code>null</code>
	 * @param delta the delta with respect to the basis; non-<code>null</code>
	 * @param copyData if <code>true</code>, the data structures will be copied
	 * from one graph to the next; otherwise, they will be reused
	 */
	private NewDeltaGraph(final NewDeltaGraph basis, final DeltaApplier delta, boolean copyData) {
		this.basis = basis;
		this.copyData = copyData;
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

	public NewDeltaGraph newGraph(NewDeltaGraph graph, DeltaApplier applier) {
		return new NewDeltaGraph(graph, applier, copyData);
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
		if (nodeEdgeMap == null) {
			initData();
		}
		return nodeEdgeMap.keySet();
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
	
	@Override
	public Map<Node, Set<Edge>> nodeEdgeMap() {
		if (nodeEdgeMap == null) {
		    initData();
//			if (nodeEdgeMap == null) { 
//				nodeEdgeMap = computeNodeEdgeMap();
//			}
		}
		return nodeEdgeMap;
	}
		
	@Override
	public Set<? extends Edge> edgeSet(Node node) {
		return nodeEdgeMap().get(node);
	}
//
//	/** 
//	 * Computes the node-to-edgeset map from the node and edge sets.
//	 * This method is only used if the map could not be obtained from the basis.
//	 */
//	private Map<Node, Set<Edge>> computeNodeEdgeMap() {
//		Map<Node,Set<Edge>> result = new HashMap<Node,Set<Edge>>();
//		for (Node node: nodeSet()) {
//			result.put(node, createEdgeSet(null));
//		}
//		for (Edge edge: edgeSet()) {
//			for (int i = 0; i < edge.endCount(); i++) {
//				result.get(edge.end(i)).add(edge);
//			}
//		}
//		return result;
//	}
//	
	/** 
	 * Initialises all the data structures, if this has not yet been done.
	 */
	private void initData() {
		reporter.start(INIT_DATA);
		if (edgeSet == null) {
            assert nodeEdgeMap == null;
            assert labelEdgeMaps == null;
            if (basis == null) {
                edgeSet = createEdgeSet(null);
        		nodeEdgeMap = new HashMap<Node,Set<Edge>>();
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
        DataTarget result = copyData ? new CopyTarget() : new SwingTarget();
        reporter.stop();
        return result;
    }

	/** 
	 * Creates a copy of an existing set of edges, or an empty set if the
	 * given set is <code>null</code>.
	 */
	EdgeSet createEdgeSet(EdgeSet edgeSet) {
	    if (edgeSet == null) {
	        return new EdgeSet();
	    } else {
	        return new EdgeSet(edgeSet);
	    }
//	    EdgeSet result = new TreeHashSet<Edge>();
//		if (edgeSet != null) {
//		    result.addAll(edgeSet);
//		}
//		return result;
	}
	
	NodeSet createNodeSet(NodeSet nodeSet) {
		if (nodeSet == null) {
			return new NodeSet();
		} else {
			return new NodeSet(nodeSet);
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
	NewDeltaGraph basis;
	/** The fixed delta of this graph. */
	DeltaApplier delta;
	
	/** The (initially null) edge set of this graph. */
	EdgeSet edgeSet;
//	/** The (initially null) node set of this graph. */
//	private NodeSet nodeSet;
	/** The map from nodes to sets of incident edges. */
	Map<Node,Set<Edge>> nodeEdgeMap;
	/** List of maps from labels to sets of edges with that label and arity. */
	List<Map<Label,Set<Edge>>> labelEdgeMaps;
	/** The certificate strategy of this graph, set on demand. */
	private Reference<CertificateStrategy> certifier;
	/** Flag indicating that data should be copied rather than shared in {@link #getDataTarget()}. */ 
	private boolean copyData = true;
	/** Factory instance of this class. */
	static private final NewDeltaGraph copyInstance = new NewDeltaGraph(null,null,true);
	/** Factory instance of this class. */
	static private final NewDeltaGraph swingInstance = new NewDeltaGraph(null,null,false);
	/** 
	 * Returns a fixed factory instance of the {@link NewDeltaGraph} class,
	 * which either copies or aliases the data.
	 * @param copyData if <code>true</code>, the graph produced by the factory
	 * copy their data structure from one graph to the next; otherwise, data are
	 * shared (and hence must be reconstructed more often)
	 */
	static public NewDeltaGraph getInstance(boolean copyData) {
		return copyData ? copyInstance : swingInstance;
	}
	
	/** 
	 * Specialisation of a set of edges, for use inside this class.
	 */
	static private class EdgeSet extends TreeHashSet<Edge> {
	    /** Creates an empty edge set. */
        public EdgeSet() {
            super();
        }
        /** Creates a copy of an existing edge set. */
        public EdgeSet(EdgeSet other) {
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
		void install(NewDeltaGraph child) {
			child.edgeSet = (EdgeSet) edgeSet;
			child.nodeEdgeMap = nodeEdgeMap;
			child.labelEdgeMaps = labelEdgeMaps;
			child.delta = null;
			child.basis = null;
		}
		
		/** Edge set to be filled by this target. */
		Set<Edge> edgeSet;
		/** Node/edge map to be filled by this target. */
		Map<Node,Set<Edge>> nodeEdgeMap;
		/** Label/edge map to be filled by this target. */
		List<Map<Label,Set<Edge>>> labelEdgeMaps;
	}
	
	/** Delta target to initialise the data structures. */
	private class SwingTarget extends DataTarget {
		/** Constructs and instance for a given node and edge set. */
		public SwingTarget() {
			NewDeltaGraph graph = NewDeltaGraph.this;
			// only construct a node set if the node-edge map is not there. */
			this.edgeSet = graph.edgeSet;
			this.nodeEdgeMap = graph.nodeEdgeMap;
			this.labelEdgeMaps = graph.labelEdgeMaps;
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
			EdgeSet outEdgeSet = (EdgeSet) nodeEdgeMap.get(elem.source());
			if (outEdgeSet == null) {
				nodeEdgeMap.put(elem.source(), outEdgeSet = createEdgeSet(null));
			}
			outEdgeSet.add(elem);
			if (elem.source() != elem.opposite()) {
				EdgeSet inEdgeSet = (EdgeSet) nodeEdgeMap.get(elem.opposite());
				if (inEdgeSet == null) {
					nodeEdgeMap.put(elem.opposite(), inEdgeSet = createEdgeSet(null));
				}
				inEdgeSet.add(elem);
			}
			// adapt label-edge map
			if (labelEdgeMaps != null) {
				Label label = elem.label();
				Map<Label,Set<Edge>> arityLabelEdgeMap = labelEdgeMaps.get(arity);
				EdgeSet edgeSet = (EdgeSet) arityLabelEdgeMap.get(label);
				if (edgeSet == null) {
					arityLabelEdgeMap.put(label, edgeSet = createEdgeSet(edgeSet));
				}
				edgeSet.add(elem);
			}
			return result;
		}

		/** Adds the node to the node set and the node-edge map. */
		public boolean addNode(Node elem) {
			Set<Edge> edges = nodeEdgeMap.put(elem, createEdgeSet(null));
			assert edges == null;
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
			nodeEdgeMap.get(elem.source()).remove(elem);
			if (elem.source() != elem.opposite()) {
				nodeEdgeMap.get(elem.opposite()).remove(elem);
			}
			// adapt label-edge map
			if (labelEdgeMaps != null) {
				Label label = elem.label();
				labelEdgeMaps.get(arity).get(label).remove(elem);
			}
			return result;
		}

		/** Removes the node from the node set and the node-edge map. */
		public boolean removeNode(Node elem) {
			Set<Edge> edges = nodeEdgeMap.remove(elem);
			assert edges.isEmpty();
			return true;
		}
		
		@Override
		void install(NewDeltaGraph child) {
			NewDeltaGraph graph = NewDeltaGraph.this;
			graph.edgeSet = null;
			graph.nodeEdgeMap = null;
			graph.labelEdgeMaps = null;
			if (graph.delta == null) {
				graph.basis = child;
				graph.delta = ((DeltaStore) child.delta).invert(true);
			}
			super.install(child);
		}
	}

    /** Delta target to initialise the data structures. */
    private class CopyTarget extends DataTarget {
        /** Constructs and instance for a given node and edge set. */
        public CopyTarget() {
        	NewDeltaGraph graph = NewDeltaGraph.this;
        	edgeSet = createEdgeSet(graph.edgeSet);
            nodeEdgeMap = new HashMap<Node,Set<Edge>>(graph.nodeEdgeMap);
            freshNodeKeys = createNodeSet(null);
            if (graph.labelEdgeMaps != null) {
                labelEdgeMaps = new ArrayList<Map<Label, Set<Edge>>>();
                freshLabelKeys = new ArrayList<Set<Label>>();
                for (Map<Label, Set<Edge>> arityLabelEdgeMap : graph.labelEdgeMaps) {
                    if (arityLabelEdgeMap == null) {
                    	labelEdgeMaps.add(null);
                        freshLabelKeys.add(null);
                    } else {
                    	labelEdgeMaps.add(new HashMap<Label, Set<Edge>>(arityLabelEdgeMap));
                        freshLabelKeys.add(new HashSet<Label>());
                    }
                }
            } else {
                freshLabelKeys = null;
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
			Node source = elem.source();
			Node opposite = elem.opposite();
			EdgeSet outEdgeSet = (EdgeSet) nodeEdgeMap.get(source);
			if (freshNodeKeys.add(source)) {
				nodeEdgeMap.put(source, outEdgeSet = createEdgeSet(outEdgeSet));
			}
			outEdgeSet.add(elem);
			if (source != opposite) {
				EdgeSet inEdgeSet = (EdgeSet) nodeEdgeMap.get(opposite);
				if (freshNodeKeys.add(opposite)) {
					nodeEdgeMap.put(opposite, inEdgeSet = createEdgeSet(inEdgeSet));
				}
				inEdgeSet.add(elem);
			}
			// adapt label-edge map
            if (labelEdgeMaps != null) {
                Label label = elem.label();
                Map<Label,Set<Edge>> arityLabelEdgeMap = labelEdgeMaps.get(arity);
                EdgeSet edgeSet = (EdgeSet) arityLabelEdgeMap.get(label);
                Set<Label> freshArityLabelKeys = freshLabelKeys.get(arity);
                if (freshArityLabelKeys.add(label)) {
                    arityLabelEdgeMap.put(label, edgeSet = createEdgeSet(edgeSet));
                }
                edgeSet.add(elem);
            }
            return result;
        }

        /** Adds the node to the node set and the node-edge map. */
        public boolean addNode(Node elem) {
        	Set<Edge> edges = nodeEdgeMap.put(elem, createEdgeSet(null));
        	assert edges == null : String.format("Node %s already has incident edges %s", elem, edges);
        	freshNodeKeys.add(elem);
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
            Node source = elem.source();
			Node opposite = elem.opposite();
			EdgeSet outEdgeSet = (EdgeSet) nodeEdgeMap.get(source);
			if (freshNodeKeys.add(source)) {
				nodeEdgeMap.put(source, outEdgeSet = createEdgeSet(outEdgeSet));
			}
			outEdgeSet.remove(elem);
			if (source != opposite) {
				EdgeSet inEdgeSet = (EdgeSet) nodeEdgeMap.get(opposite);
				if (freshNodeKeys.add(opposite)) {
					nodeEdgeMap.put(opposite, inEdgeSet = createEdgeSet(inEdgeSet));
				}
				inEdgeSet.remove(elem);
			}
            // adapt label-edge map
            if (labelEdgeMaps != null) {
                Label label = elem.label();
                Map<Label,Set<Edge>> arityLabelEdgeMap = labelEdgeMaps.get(arity);
                EdgeSet labelEdgeSet = (EdgeSet) arityLabelEdgeMap.get(label);
                Set<Label> freshArityLabelKeys = freshLabelKeys.get(arity);
                if (freshArityLabelKeys.add(label)) {
                    arityLabelEdgeMap.put(label, labelEdgeSet = createEdgeSet(labelEdgeSet));
                }
                labelEdgeSet.remove(elem);
            }
            return result;
        }

        /** Removes the node from the node set and the node-edge map. */
        public boolean removeNode(Node elem) {
        	Set<Edge> edges = nodeEdgeMap.remove(elem);
        	assert edges.isEmpty();
        	freshNodeKeys.remove(elem);
            return true;
        }
        
        /** Auxiliary set to determine the nodes changed w.r.t. the basis. */
        private final Set<Node> freshNodeKeys;
        /** Auxiliary set to determine the labels changed w.r.t. the basis. */
        private final List<Set<Label>> freshLabelKeys;
    }
}
