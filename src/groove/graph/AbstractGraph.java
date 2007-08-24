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
 * $Id: AbstractGraph.java,v 1.13 2007-08-24 17:34:54 rensink Exp $
 */

package groove.graph;

import groove.graph.iso.CertificateStrategy;
import groove.graph.iso.DefaultIsoChecker;
import groove.graph.iso.IsoChecker;
import groove.match.IsoMatchFactory;
import groove.match.MatchStrategy;
import groove.util.Dispenser;
import groove.util.Pair;
import groove.view.FormatException;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Partial implementation of a graph.
 * Adds to the AbstractGraphShape the ability to add nodes and edges,
 * and some morphism capabilities.
 * @author Arend Rensink
 * @version $Revision: 1.13 $
 */
public abstract class AbstractGraph<C extends GraphCache> extends AbstractGraphShape<C> implements InternalGraph {
    /**
     * The factory used to get morphisms from
     * @see #createMorphism(Graph,Graph)
     */
    static private GraphFactory graphFactory = GraphFactory.getInstance();
    
    /**
     * The isomorphism checking strategy.
     * @see #getIsoChecker()
     */
    static private IsoChecker isoChecker = new DefaultIsoChecker();

    /**
     * The current strategy for computing isomorphism certificates.
     * @see #getCertifier()
     */
    static private CertificateStrategy certificateFactory = new groove.graph.iso.Bisimulator(null);
    
    /** Fixed empty graphs, used for the constant <tt>{@link #EMPTY_GRAPH}</tt>. */
    private static class EmptyGraph extends AbstractGraph<GraphCache> {
        /**
         * The empty graph to which no elements can be added.
         */
        private EmptyGraph() {
            setFixed();
        }

        public boolean addEdgeWithoutCheck(Edge edge) {
            throw new UnsupportedOperationException("Can't add element to fixed empty graph");
        }

        public boolean removeNodeWithoutCheck(Node node) {
            throw new UnsupportedOperationException("Can't remove element vrom fixed empty graph");
        }

        @Override
        public Graph clone() {
            return new EmptyGraph();
        }

        public Graph newGraph() {
            return new EmptyGraph();
        }

        public boolean addEdge(Edge edge) {
            throw new UnsupportedOperationException("Can't add element to fixed empty graph");
        }

        public boolean addNode(Node node) {
            throw new UnsupportedOperationException("Can't add element to fixed empty graph");
        }

        public Set<? extends Edge> edgeSet() {
            return Collections.emptySet();
        }

        public Set<? extends Node> nodeSet() {
            return Collections.emptySet();
        }

        public boolean removeEdge(Edge edge) {
            throw new UnsupportedOperationException("Can't remove element vrom fixed empty graph");
        }

        public boolean removeNode(Node node) {
            throw new UnsupportedOperationException("Can't remove element vrom fixed empty graph");
        }
    }
    
    /**
     * Fixed empty graph.
     */
    static public final EmptyGraph EMPTY_GRAPH = new EmptyGraph();

    /**
     * Changes the strategy for computing isomorphism certificates.
     * @param certificateFactory the new strategy
     * @see #getCertifier()
     */
    static protected void setCertificateFactory(CertificateStrategy certificateFactory) {
        AbstractGraph.certificateFactory = certificateFactory;
    }

    /**
     * Returns the strategy for computing isomorphism certificates.
     * @return the strategy for computing isomorphism certificates
     */
    static protected CertificateStrategy getCertificateFactory() {
        return certificateFactory;
    }

    public Collection<? extends Morphism> getMatchesTo(Graph to) {
        reporter.start(GET_MATCHES_TO);
        Collection<? extends Morphism> res = createMorphism(this, to).getTotalExtensions();
        reporter.stop();
        return res;
    }

    public Iterator<? extends Morphism> getMatchesToIter(Graph to) {
        reporter.start(GET_MATCHES_TO);
        Iterator<? extends Morphism> res = createMorphism(this, to).getTotalExtensionsIter();
        reporter.stop();
        return res;
    }

    /**
     * This implementation checks if the other is also an <tt>AbstractGraph</tt>; if so, it first
     * compares the graph certificates at increasing precision to ensure that it is actually worth
     * trying to compute an isomorphism.
     */
    public Morphism getIsomorphismTo(Graph to) {
        reporter.start(GET_ISOMORPHISM_TO);
        Morphism isoMorphism = new DefaultMorphism(this, to) {
			@Override
			protected MatchStrategy createMatchStrategy() {
				return IsoMatchFactory.getInstance().createSearchPlan(dom());
			}
        };
        Morphism result = isoMorphism.getTotalExtension();
        reporter.stop();
        return result;
    }

    /** 
     * This implementation delegates the method to the {@link CertificateStrategy}.
     * @see #getCertifier()
     * @see CertificateStrategy#getGraphCertificate()
     */
    @Deprecated
    public Object getCertificate() {
        return getCertifier().getGraphCertificate();
    }

    /**
     * Factory method for nodes of this graph.
     * @return the freshly created node
     */
    public Node createNode() {
        return new DefaultNode(getNodeCounter());
    }
    
    /**
     * Returns the node counter used to number nodes distinctly.
     */
    final protected Dispenser getNodeCounter() {
    	return getCache().getNodeCounter();
    }

    /**
     * Factory method for edges of this graph.
     * This implementation delegates to {@link #createEdge(Node, Label)} if
     * <code>ends.length == 1</code>, to {@link #createEdge(Node, Label, Node)} if
     * <code>ends.length == 2</code> and throws a {@link IllegalArgumentException}
     * otherwise.
     * @param ends the endpoints of the new edge
     * @param label the label of the new edge
     * @return the freshly created Edge
     * @throws IllegalArgumentException if the number of edges is not supported by this graph.
     */
    public Edge createEdge(Node[] ends, Label label) {
        switch (ends.length) {
        case 1 :
            return createEdge(ends[Edge.SOURCE_INDEX], label);
        case 2 :
            return createEdge(ends[Edge.SOURCE_INDEX], label, ends[Edge.TARGET_INDEX]);
        default :
            throw new IllegalArgumentException("Hyperedges with "+ends.length+" tentacles not supported");
        }
    }

    /**
     * Factory method for binary edges of this graph.
     * This implementation returns a {@link DefaultEdge}.
     * @param source the source node of the new edge
     * @param label the label of the new edge
     * @param target the target node of the new edge
     * @return the freshly binary created edge
     */
    public BinaryEdge createEdge(Node source, Label label, Node target) {
//        if (label instanceof DefaultLabel) {
//            return new DefaultLabelEdge(source, (DefaultLabel) label, target);
//        } else {
            return DefaultEdge.createEdge(source, label, target);
//        }
    }

    /**
     * Factory method for unary edges of this graph.
     * This implementation returns a {@link DefaultFlag}.
     * Subclasses may choose to throw a {@link UnsupportedOperationException} 
     * if unary edges are not supported.
     * @param source the source node of the new edge
     * @param label the label of the new edge
     * @return the freshly created unary edge
     */
    public UnaryEdge createEdge(Node source, Label label) {
//        if (label instanceof DefaultLabel) {
//            return new DefaultLabelFlag(source, (DefaultLabel) label);
//        } else {
            return new DefaultFlag(source, label);
//        }
    }

    public Node addNode() {
        Node freshNode = createNode();
        addNode(freshNode);
        return freshNode;
    }

    /**
     * Creates its result using {@link #createEdge(Node, Label, Node)}.
     */
    public BinaryEdge addEdge(Node source, Label label, Node target) {
        BinaryEdge result = createEdge(source, label, target);
        addEdge(result);
        return result;
    }

    /**
     * Creates its result using {@link #createEdge(Node[], Label)}.
     */
    public Edge addEdge(Node[] ends, Label label) {
        Edge newEdge = createEdge(ends, label);
        addEdge(newEdge);
        return newEdge;
    }

    public boolean addNodeSet(Collection<? extends Node> nodeSet) {
        boolean added = false;
        for (Node node: nodeSet) {
            added |= addNode(node);
        }
        return added;
    }

    public boolean addEdgeSet(Collection<? extends Edge> edgeSet) {
        boolean added = false;
        for (Edge edge: edgeSet) {
            added |= addEdge(edge);
        }
        return added;
    }

    public boolean addEdgeSetWithoutCheck(Collection<? extends Edge> edgeSet) {
        boolean added = false;
        for (Edge edge: edgeSet) {
            added |= addEdgeWithoutCheck(edge);
        }
        return added;
    }

    public boolean removeNodeSet(Collection<Node> nodeSet) {
        boolean removed = false;
        for (Node node: nodeSet) {
            removed |= removeNode(node);
        }
        return removed;
    }

    public boolean removeNodeSetWithoutCheck(Collection<Node> nodeSet) {
        boolean removed = false;
        for (Node node: nodeSet) {
            removed |= removeNodeWithoutCheck(node);
        }
        return removed;
    }

    public boolean removeEdgeSet(Collection<Edge> edgeSet) {
        boolean removed = false;
        for (Edge edge: edgeSet) {
            removed |= removeEdge(edge);
        }
        return removed;
    }

    public boolean mergeNodes(Node from, Node to) {
        if (! from.equals(to)) {
            fireReplaceNode(from, to);
            // compute edge replacements and add new edges
            for (Edge edge: new HashSet<Edge>(edgeSet(from))) {
                boolean changed = false;
                Node[] ends = edge.ends();
                for (int i = 0; i < ends.length; i++) {
                    if (ends[i].equals(from)) {
                        ends[i] = to;
                        changed = true;
                    }
                }
                if (changed) {
                    Edge newEdge = createEdge(ends, edge.label());
                    addEdgeWithoutCheck(newEdge);
                    fireReplaceEdge(edge, newEdge);
                    removeEdge(edge);
                }
            }
            // delete the old node and edges
            removeNodeWithoutCheck(from);
            return true;
        } else {
            return false;
        }
    }

    /** This should return a <i>modifiable</i> clone of the graph. */
    @Override
    public abstract Graph clone();

    public Graph newGraph(Graph graph) throws FormatException {
        Graph result = newGraph();
        result.addNodeSet(graph.nodeSet());
        result.addEdgeSet(graph.edgeSet());
        result.setInfo(graph.getInfo());
        return result;
    }

    /**
     * Calls {@link GraphListener#replaceUpdate(GraphShape, Node, Node)} on all registered GraphListeners.
     * @param from the replaced node
     * @param to the new node
     * @see GraphListener#replaceUpdate(GraphShape,Node,Node)
     */
    protected synchronized void fireReplaceNode(Node from, Node to) {
        Iterator<GraphShapeListener> iter = getGraphListeners();
        while (iter.hasNext()) {
            GraphShapeListener listener = iter.next();
            if (listener instanceof GraphListener) {
            	((GraphListener) listener).replaceUpdate(this, from, to);
            }
        }
    }

    /**
     * Calls {@link GraphListener#replaceUpdate(GraphShape, Edge, Edge)} on all registered GraphListeners.
     * @param from the replaced edge
     * @param to the new edge
     * @see GraphListener#replaceUpdate(GraphShape,Edge,Edge)
     */
    protected synchronized void fireReplaceEdge(Edge from, Edge to) {
        Iterator<GraphShapeListener> iter = getGraphListeners();
        while (iter.hasNext()) {
            GraphShapeListener listener = iter.next();
            if (listener instanceof GraphListener) {
            	((GraphListener) listener).replaceUpdate(this, from, to);
            }
        }
    }
    
    /**
     * Callback method that indicates if the graph supports edges with the 
     * indicated number of edges.
     * This implementation only returns <code>true</code> if <code>endCount</code>
     * equals <code>2</code>, meaning that the graph only supports binary edges.
     * @param endCount the number for which to check wether its valid
     * @return <tt>true</tt> if <code>endCount</code> equals 2, <tt>false</tt> otherwise
     * @see #addEdge(Node[], Label)
     */
    protected boolean isValidEndCount(int endCount) {
        return endCount == 2;
    }
    
    /**
     * Returns a certificate strategy for the current state of this graph.
     * This implementation retrieves the strategy from the graph cache.
     * @see #getCertificate()
     */
    public CertificateStrategy getCertifier() {
        return getCache().getCertificateStrategy();
    }
    
    /**
     * Returns the isomorphism checking strategy used by this graph.
     * This implementation returns a statically set {@link DefaultIsoChecker}.
     * @return the isomorphism checking strategy used by this graph
     * @see #getIsomorphismTo(Graph)
     */
    protected IsoChecker getIsoChecker() {
    	return isoChecker;
    }
//
//    /**
//     * Returns a graph cache for this graph.
//     * The graph cache is newly created, using {@link #createCache()}, if no
//     * cache is currently set. A reference to the cache is created using
//     * {@link #createCacheReference(GraphCache)}.
//     * @return a graph cache for this graph
//     */
//    @Override
//    public GraphCache getCache() {
//        return (GraphCache) super.getCache();
//    }

    /**
     * Factory method for a morphism.
     * This implementation invokes {@link GraphFactory#newMorphism(Graph, Graph)} on
     * the current graph factory.
     */
    protected Morphism createMorphism(Graph dom, Graph cod) {
        return graphFactory.newMorphism(dom, cod);
    }

    /**
     * Factory method for a graph cache.
     * This implementation returns a {@link GraphCache}.
     * @return the graph cache
     */
    @Override
    protected C createCache() {
        return (C) new GraphCache(this);
    }
    
//    /**
//     * Factory method for a reference to a given graph cache.
//     * @param referent the graph cache for which to create a reference
//     * @return This implementation returns a {@link CacheReference}.
//     */
//    @Override
//    protected CacheReference<? extends GraphCache> createCacheReference(GraphShapeCache referent) {
//        return new CacheReference<GraphCache>(this, (GraphCache) referent);
//    }
//    

    /**
     * Partitions a set of graph elements into its maximal connected subsets.
     * The set does not necessarily contain all endpoints of edges it contains.
     * A subset is connected if there is a chain of edges and edge endpoints,
     * all of which are in the set, between all pairs of elements in the set.
     * @param nodeSet the set of nodes to be partitioned
     * @param edgeSet the set of edges to be partitioned
     * @return The set of maximal connected subsets of <code>elementSet</code>
     */
    static public <N extends Node,E extends Edge> Set<Pair<Set<N>, Set<E>>> getConnectedSets(Collection<N> nodeSet, Collection<E> edgeSet) {
        // mapping from nodes of elementSet to sets of connected elements
        Map<Element,Pair<Set<N>,Set<E>>> resultMap = new HashMap<Element,Pair<Set<N>,Set<E>>>();
        for (N elem : nodeSet) {
			// the node cell consists of a singleton for the time being
			Set<N> nodeCellSecond = new HashSet<N>();
			nodeCellSecond.add(elem);
			resultMap.put(elem, new Pair<Set<N>, Set<E>>(nodeCellSecond, new HashSet<E>()));
		}
        for (E edge : edgeSet) {
        	Pair<Set<N>,Set<E>> cell = null;
			for (int i = 0; i < edge.endCount(); i++) {
				Pair<Set<N>,Set<E>> newCell = resultMap.get(edge.end(i));
				if (newCell != null) {
					if (cell == null) {
						cell = newCell;
					} else if (newCell != cell) {
						cell.first().addAll(newCell.first());
						cell.second().addAll(newCell.second());
						for (N loser : newCell.first()) {
							resultMap.put(loser, cell);
						}
						for (E loser : newCell.second()) {
							resultMap.put(loser, cell);
						}
					}
				}
			}
			if (cell == null) {
				// no end nodes of edge have a cell
				Set<E> cellSecond = new HashSet<E>();
				cellSecond.add(edge);
				cell = new Pair<Set<N>,Set<E>>(new HashSet<N>(), cellSecond);
			} else {
				cell.second().add(edge);
			}
			resultMap.put(edge, cell);
		}
		return new HashSet<Pair<Set<N>,Set<E>>>(resultMap.values());
    }

    /**
	 * Tests if a given graph is connected; throws a
	 * {@link IllegalArgumentException} if it is not. Implemented by testing
	 * whether the number of partitions of the graph equals 1.
	 * 
	 * @return <tt>true</tt> if this graph contains exactly one connected
	 *         component, <tt>false</tt> otherwise
	 */
    public boolean isConnected() {
        return getConnectedSets(nodeSet(), edgeSet()).size() == 1;
    }
    
    // -------------------- REPORTER DEFINITIONS ------------------------

    /** Handle for profiling the {@link #getMatchesTo(Graph)} method */
    static final int GET_MATCHES_TO = reporter.newMethod("getMatchesTo(Graph)");
    /** Handle for profiling the {@link #getIsomorphismTo(Graph)} method */
    static final int GET_ISOMORPHISM_TO = reporter.newMethod("getIsomorphismTo(Graph)");
    /** Handle for profiling the {@link #clone()} method */
    static final int CLONE = reporter.newMethod("clone()");
    /** Handle for profiling the {@link #containsElement(Element)} method */
    static final int CONTAINS_ELEMENT = reporter.newMethod("containsElement(Element)");
    /** Handle for profiling the {@link #addNode(Node)} method */
    static final int ADD_NODE = reporter.newMethod("addNode(Node)");
    /** Handle for profiling the {@link #addEdge(Edge)} method */
    static final int ADD_EDGE = reporter.newMethod("addEdge(Edge)");
    /** Handle for profiling the {@link #removeNode(Node)} method */
    static final int REMOVE_NODE = reporter.newMethod("removeNode(Node)");
    /** Handle for profiling the {@link #removeEdge(Edge)} method */
    static final int REMOVE_EDGE = reporter.newMethod("removeEdge(Edge)");
    /** Handle for profiling the initDelta method of the delta implementations */
    static protected final int INIT_DATA = reporter.newMethod("initDelta()");
}