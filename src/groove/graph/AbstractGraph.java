/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: AbstractGraph.java,v 1.25 2008-02-19 10:35:31 fladder Exp $
 */
package groove.graph;

import groove.graph.iso.CertificateStrategy;
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
 * Partial implementation of a graph. Adds to the AbstractGraphShape the ability
 * to add nodes and edges, and some morphism capabilities.
 * @author Arend Rensink
 * @version $Revision$
 */
public abstract class AbstractGraph<C extends GraphCache> extends
        AbstractGraphShape<C> implements InternalGraph {
    /**
     * Factory method for nodes of this graph.
     * @return the freshly created node
     */
    public Node createNode() {
        return DefaultNode.createNode();
    }

    /**
     * Factory method for nodes of this graph.
     * @param constructor an object that specializes the constructor.
     * @return the freshly created node
     */
    public Node createNode(DefaultNode constructor) {
        return DefaultNode.createNode(constructor);
    }

    /**
     * Returns the node counter used to number nodes distinctly.
     */
    final protected Dispenser getNodeCounter() {
        return getCache().getNodeCounter();
    }

    /**
     * Factory method for edges of this graph. This implementation delegates to
     * {@link #createEdge(Node, Label, Node)} if <code>ends.length == 2</code>
     * and throws a {@link IllegalArgumentException} otherwise.
     * @param ends the endpoints of the new edge
     * @param label the label of the new edge
     * @return the freshly created Edge
     * @throws IllegalArgumentException if the number of edges is not supported
     *         by this graph.
     * @deprecated use {@link #createEdge(Node, Label, Node)} instead
     */
    @Deprecated
    public Edge createEdge(Node[] ends, Label label) {
        return createEdge(ends[0], label, ends[1]);
    }

    /**
     * Factory method for binary edges of this graph. This implementation
     * returns a {@link DefaultEdge}.
     * @param source the source node of the new edge
     * @param label the label of the new edge
     * @param target the target node of the new edge
     * @return the freshly binary created edge
     */
    public Edge createEdge(Node source, Label label, Node target) {
        return DefaultEdge.createEdge(source, label, target);
    }

    /**
     * Factory method for binary edges of this graph. This implementation
     * returns a sub type of {@link DefaultEdge}, determined by the constructor.
     * @param source the source node of the new edge
     * @param label the label of the new edge
     * @param target the target node of the new edge
     * @param constructor an object that specializes the constructor.
     * @return the freshly binary created edge
     */
    public Edge createEdge(Node source, Label label, Node target,
            DefaultEdge constructor) {
        return DefaultEdge.createEdge(source, label, target, constructor);
    }

    public Node addNode() {
        Node freshNode = createNode();
        assert !nodeSet().contains(freshNode) : String.format(
            "Fresh node %s already in node set %s", freshNode, nodeSet());
        addNode(freshNode);
        return freshNode;
    }

    /**
     * Creates its result using {@link #createEdge(Node, Label, Node)}.
     */
    public Edge addEdge(Node source, Label label, Node target) {
        Edge result = createEdge(source, label, target);
        addEdge(result);
        return result;
    }

    /**
     * Creates its result using {@link #createEdge(Node[], Label)}.
     */
    @Deprecated
    public Edge addEdge(Node[] ends, Label label) {
        Edge newEdge = createEdge(ends, label);
        addEdge(newEdge);
        return newEdge;
    }

    public boolean addNodeSet(Collection<? extends Node> nodeSet) {
        boolean added = false;
        for (Node node : nodeSet) {
            added |= addNode(node);
        }
        return added;
    }

    public boolean addEdgeSet(Collection<? extends Edge> edgeSet) {
        boolean added = false;
        for (Edge edge : edgeSet) {
            added |= addEdge(edge);
        }
        return added;
    }

    public boolean addEdgeSetWithoutCheck(Collection<? extends Edge> edgeSet) {
        boolean added = false;
        for (Edge edge : edgeSet) {
            added |= addEdgeWithoutCheck(edge);
        }
        return added;
    }

    public boolean removeNodeSet(Collection<Node> nodeSet) {
        boolean removed = false;
        for (Node node : nodeSet) {
            removed |= removeNode(node);
        }
        return removed;
    }

    public boolean removeNodeSetWithoutCheck(Collection<Node> nodeSet) {
        boolean removed = false;
        for (Node node : nodeSet) {
            removed |= removeNodeWithoutCheck(node);
        }
        return removed;
    }

    public boolean removeEdgeSet(Collection<? extends Edge> edgeSet) {
        boolean removed = false;
        for (Edge edge : edgeSet) {
            removed |= removeEdge(edge);
        }
        return removed;
    }

    public boolean mergeNodes(Node from, Node to) {
        if (!from.equals(to)) {
            fireReplaceNode(from, to);
            // compute edge replacements and add new edges
            for (Edge edge : new HashSet<Edge>(edgeSet(from))) {
                boolean changed = false;
                Node source = edge.source();
                if (source.equals(from)) {
                    source = to;
                    changed = true;
                }
                Node target = edge.target();
                if (target.equals(from)) {
                    target = to;
                    changed = true;
                }
                if (changed) {
                    Edge newEdge = createEdge(source, edge.label(), target);
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
     * Calls {@link GraphListener#replaceUpdate(GraphShape, Node, Node)} on all
     * registered GraphListeners.
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
     * Calls {@link GraphListener#replaceUpdate(GraphShape, Edge, Edge)} on all
     * registered GraphListeners.
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
     * indicated number of edges. This implementation only returns
     * <code>true</code> if <code>endCount</code> equals <code>2</code>,
     * meaning that the graph only supports binary edges.
     * @param endCount the number for which to check wether its valid
     * @return <tt>true</tt> if <code>endCount</code> equals 2,
     *         <tt>false</tt> otherwise
     * @see #addEdge(Node[], Label)
     */
    protected boolean isValidEndCount(int endCount) {
        return endCount == 2;
    }

    /**
     * Tests if the certificate strategy (of the correct strength) is currently instantiated.
     * @param strong the strength of the required certifier
     * @see CertificateStrategy#getStrength()
     */
    public boolean hasCertifier(boolean strong) {
        return !isCacheCleared() && getCache().hasCertifier(strong);
    }

    /**
     * Returns the certificate strategy object used for this graph. The
     * certificate strategy is used to decide isomorphism between graphs.
     * @param strong if <code>true</code>, a strong certifier is returned.
     * @see CertificateStrategy#getStrength()
     */
    public CertificateStrategy getCertifier(boolean strong) {
        return getCache().getCertifier(strong);
    }

    /**
     * Factory method for a morphism. This implementation invokes
     * {@link GraphFactory#newMorphism(Graph, Graph)} on the current graph
     * factory.
     */
    protected Morphism createMorphism(Graph dom, Graph cod) {
        return graphFactory.newMorphism(dom, cod);
    }

    /**
     * Factory method for a graph cache. This implementation returns a
     * {@link GraphCache}.
     * @return the graph cache
     */
    @Override
    @SuppressWarnings("all")
    protected C createCache() {
        return (C) new GraphCache(this);
    }

    /**
     * Partitions a set of graph elements into its maximal connected subsets.
     * The set does not necessarily contain all endpoints of edges it contains.
     * A subset is connected if there is a chain of edges and edge endpoints,
     * all of which are in the set, between all pairs of elements in the set.
     * @param nodeSet the set of nodes to be partitioned
     * @param edgeSet the set of edges to be partitioned
     * @return The set of maximal connected subsets of <code>elementSet</code>
     */
    static public <N extends Node,E extends Edge> Set<Pair<Set<N>,Set<E>>> getConnectedSets(
            Collection<N> nodeSet, Collection<E> edgeSet) {
        // mapping from nodes of elementSet to sets of connected elements
        Map<Element,Pair<Set<N>,Set<E>>> resultMap =
            new HashMap<Element,Pair<Set<N>,Set<E>>>();
        for (N elem : nodeSet) {
            // the node cell consists of a singleton for the time being
            Set<N> nodeCellSecond = new HashSet<N>();
            nodeCellSecond.add(elem);
            resultMap.put(elem, new Pair<Set<N>,Set<E>>(nodeCellSecond,
                new HashSet<E>()));
        }
        for (E edge : edgeSet) {
            Pair<Set<N>,Set<E>> sourceCell = resultMap.get(edge.source());
            Pair<Set<N>,Set<E>> targetCell = resultMap.get(edge.target());
            if (targetCell != null) {
                if (sourceCell == null) {
                    sourceCell = targetCell;
                } else if (targetCell != sourceCell) {
                    sourceCell.first().addAll(targetCell.first());
                    sourceCell.second().addAll(targetCell.second());
                    for (N loser : targetCell.first()) {
                        resultMap.put(loser, sourceCell);
                    }
                    for (E loser : targetCell.second()) {
                        resultMap.put(loser, sourceCell);
                    }
                }
            }
            if (sourceCell == null) {
                // no end nodes of edge have a cell
                Set<E> cellSecond = new HashSet<E>();
                cellSecond.add(edge);
                sourceCell =
                    new Pair<Set<N>,Set<E>>(new HashSet<N>(), cellSecond);
            } else {
                sourceCell.second().add(edge);
            }
            resultMap.put(edge, sourceCell);
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

    /** Returns an empty graph. */
    @SuppressWarnings("all")
    static public <C extends GraphCache> AbstractGraph<C> emptyGraph() {
        return EMPTY_GRAPH;
    }

    /**
     * The factory used to get morphisms from
     * @see #createMorphism(Graph,Graph)
     */
    static private GraphFactory graphFactory = GraphFactory.getInstance();

    /**
     * The current strategy for computing isomorphism certificates.
     * @see #getCertifier(boolean)
     */
    static private CertificateStrategy certificateFactory =
        new groove.graph.iso.PartitionRefiner(null);

    /**
     * Fixed empty graph.
     */
    @SuppressWarnings("all")
    static public final EmptyGraph EMPTY_GRAPH = new EmptyGraph();

    /**
     * Changes the strategy for computing isomorphism certificates.
     * @param certificateFactory the new strategy
     * @see #getCertifier(boolean)
     */
    static public void setCertificateFactory(
            CertificateStrategy certificateFactory) {
        AbstractGraph.certificateFactory = certificateFactory;
    }

    /**
     * Returns the strategy for computing isomorphism certificates.
     * @return the strategy for computing isomorphism certificates
     */
    static public CertificateStrategy getCertificateFactory() {
        return certificateFactory;
    }

    /** Fixed empty graphs, used for the constant <tt>{@link #EMPTY_GRAPH}</tt>. */
    private static class EmptyGraph<C extends GraphCache> extends
            AbstractGraph<C> implements Cloneable {
        /**
         * The empty graph to which no elements can be added.
         */
        EmptyGraph() {
            setFixed();
        }

        public boolean addEdgeWithoutCheck(Edge edge) {
            throw new UnsupportedOperationException(
                "Can't add element to fixed empty graph");
        }

        public boolean removeNodeWithoutCheck(Node node) {
            throw new UnsupportedOperationException(
                "Can't remove element from fixed empty graph");
        }

        @Override
        public Graph clone() {
            return new EmptyGraph<C>();
        }

        public Graph newGraph() {
            return new EmptyGraph<C>();
        }

        public boolean addEdge(Edge edge) {
            throw new UnsupportedOperationException(
                "Can't add element to fixed empty graph");
        }

        public boolean addNode(Node node) {
            throw new UnsupportedOperationException(
                "Can't add element to fixed empty graph");
        }

        public Set<? extends Edge> edgeSet() {
            return Collections.emptySet();
        }

        public Set<? extends Node> nodeSet() {
            return Collections.emptySet();
        }

        public boolean removeEdge(Edge edge) {
            throw new UnsupportedOperationException(
                "Can't remove element from fixed empty graph");
        }

        public boolean removeNode(Node node) {
            throw new UnsupportedOperationException(
                "Can't remove element from fixed empty graph");
        }
    }
}