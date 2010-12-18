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
import groove.util.AbstractCacheHolder;
import groove.util.Dispenser;
import groove.util.Groove;
import groove.util.Pair;

import java.lang.ref.Reference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Partial implementation of a graph. Adds to the AbstractGraphShape the ability
 * to add nodes and edges, and some morphism capabilities.
 * @author Arend Rensink
 * @version $Revision$
 */
public abstract class AbstractGraph<N extends Node,L extends Label,E extends Edge>
        extends AbstractCacheHolder<GraphCache<N,L,E>> implements Graph<N,L,E> {
    /**
     * This constructor polls the cache reference queue and calls
     * {@link Reference#clear()} on all encountered references.
     */
    protected AbstractGraph() {
        super(null);
        modifiableGraphCount++;
    }

    public int nodeCount() {
        return nodeSet().size();
    }

    public int edgeCount() {
        return edgeSet().size();
    }

    /**
     * Defers the containment question to {@link #nodeSet()}
     */
    public boolean containsNode(Node elem) {
        assert isTypeCorrect(elem);
        return nodeSet().contains(elem);
    }

    /**
     * Defers the containment question to {@link #edgeSet()}
     */
    public boolean containsEdge(Edge elem) {
        assert isTypeCorrect(elem);
        return edgeSet().contains(elem);
    }

    public int size() {
        return nodeCount() + edgeCount();
    }

    public boolean isEmpty() {
        return nodeCount() == 0;
    }

    /**
     * This implementation retrieves the node-to-edges mapping from the cache,
     * and looks up the required set in the image for <tt>node</tt>.
     */
    public Set<? extends E> edgeSet(Node node) {
        assert isTypeCorrect(node);
        Set<? extends E> result = getCache().getNodeEdgeMap().get(node);
        if (result == null) {
            return Collections.emptySet();
        } else {
            return Collections.unmodifiableSet(result);
        }
    }

    /**
     * This implementation retrieves the node-to-out-edges mapping from the cache,
     * and looks up the required set in the image for <tt>node</tt>.
     */
    public Set<? extends E> outEdgeSet(Node node) {
        assert isTypeCorrect(node) : String.format(
            "Type %s of node %s incorrect for this graph",
            node.getClass().getName(), node);
        Set<? extends E> result = getCache().getNodeOutEdgeMap().get(node);
        if (result == null) {
            return Collections.emptySet();
        } else {
            return Collections.unmodifiableSet(result);
        }
    }

    /**
     * This implementation retrieves the node-to-in-edges mapping from the cache,
     * and looks up the required set in the image for <tt>node</tt>.
     */
    public Set<? extends E> inEdgeSet(Node node) {
        assert isTypeCorrect(node);
        Set<? extends E> result = getCache().getNodeInEdgeMap().get(node);
        if (result == null) {
            return Collections.emptySet();
        } else {
            return Collections.unmodifiableSet(result);
        }
    }

    public Set<? extends E> labelEdgeSet(Label label) {
        assert isTypeCorrect(label);
        Set<? extends E> result = getCache().getLabelEdgeMap().get(label);
        if (result != null) {
            return Collections.unmodifiableSet(result);
        } else {
            return Collections.emptySet();
        }
    }

    public GraphInfo<N,E> getInfo() {
        return this.graphInfo;
    }

    /**
     * Callback factory method for a graph information object.
     * @param info the {@link groove.graph.GraphInfo} to create a fresh instance
     *        of
     * @return a fresh instance of {@link groove.graph.GraphInfo} based on
     *         <code>info</code>
     */
    protected GraphInfo<N,E> createInfo(GraphInfo<?,?> info) {
        return new GraphInfo<N,E>(info);
    }

    public GraphInfo<N,E> setInfo(GraphInfo<?,?> info) {
        return this.graphInfo = (info == null ? null : createInfo(info));
    }

    public boolean isFixed() {
        return isCacheCollectable();
    }

    public void setFixed() {
        if (!isFixed()) {
            setCacheCollectable();
            if (GATHER_STATISTICS) {
                modifiableGraphCount--;
            }
        }
    }

    @Override
    public void testFixed(boolean fixed) throws IllegalStateException {
        if (isFixed() != fixed) {
            throw new IllegalStateException(String.format(
                "Expected graph to be %s", fixed ? "fixed" : "unfixed"));
        }
    }

    /** Calls {@link #toString(Graph)}. */
    @Override
    public String toString() {
        return toString(this);
    }

    // -------------------- Graph listener methods ---------------------------

    /**
     * Calls {@link GraphCache#addUpdate(Node)} 
     * if the cache is not cleared.
     * @param node the node being added
     */
    protected void fireAddNode(N node) {
        if (!isCacheCleared()) {
            getCache().addUpdate(node);
        }
    }

    /**
     * Calls {@link GraphCache#addUpdate(Edge)}
     * if the cache is not cleared.
     * @param edge the edge being added
     */
    protected void fireAddEdge(E edge) {
        if (!isCacheCleared()) {
            getCache().addUpdate(edge);
        }
    }

    /**
     * Calls {@link GraphCache#removeUpdate(Node)}
     * if the cache is not cleared.
     * @param node the node being removed
     */
    protected void fireRemoveNode(N node) {
        if (!isCacheCleared()) {
            getCache().removeUpdate(node);
        }
    }

    /**
     * Calls {@link GraphCache#removeUpdate(Edge)}
     * if the cache is not cleared.
     * @param edge the edge being removed
     */
    protected void fireRemoveEdge(E edge) {
        if (!isCacheCleared()) {
            getCache().removeUpdate(edge);
        }
    }

    /** 
     * Tests if a node is of the correct type to be included in this graph.
     */
    protected boolean isTypeCorrect(Node node) {
        return true;
    }

    /** 
     * Tests if a label is of the correct type to be included in this graph.
     */
    protected boolean isTypeCorrect(Label node) {
        return true;
    }

    /** 
     * Tests if an edge is of the correct type to be included in this graph.
     */
    protected boolean isTypeCorrect(Edge edge) {
        return true;
    }

    /**
     * Factory method for numbered nodes of this graph.
     * @return the freshly created node
     */
    final protected N createNode(int nr) {
        return getFactory().createNode(nr);
    }

    /**
     * Returns the node counter used to number nodes distinctly.
     */
    final protected Dispenser getNodeCounter() {
        return getCache().getNodeCounter();
    }

    /**
     * Factory method for binary edges of this graph. This implementation
     * returns a {@link DefaultEdge}.
     * @param source the source node of the new edge
     * @param label the label of the new edge
     * @param target the target node of the new edge
     * @return the freshly binary created edge
     */
    final protected E createEdge(N source, L label, N target) {
        return getFactory().createEdge(source, label, target);
    }

    public N addNode() {
        N freshNode = createNode(getNodeCounter().getNext());
        assert !nodeSet().contains(freshNode) : String.format(
            "Fresh node %s already in node set %s", freshNode, nodeSet());
        addNode(freshNode);
        return freshNode;
    }

    public N addNode(int nr) {
        N freshNode = createNode(nr);
        assert !nodeSet().contains(freshNode) : String.format(
            "Fresh node %s already in node set %s", freshNode, nodeSet());
        addNode(freshNode);
        return freshNode;
    }

    /**
     * Creates its result using {@link #createEdge(Node, Label, Node)}.
     */
    public E addEdge(N source, String label, N target) {
        return addEdge(source, getFactory().createLabel(label), target);
    }

    /**
     * Creates its result using {@link #createEdge(Node, Label, Node)}.
     */
    public E addEdge(N source, L label, N target) {
        E result = createEdge(source, label, target);
        addEdge(result);
        return result;
    }

    public boolean addNodeSet(Collection<? extends N> nodeSet) {
        boolean added = false;
        for (N node : nodeSet) {
            added |= addNode(node);
        }
        return added;
    }

    public boolean addEdgeSet(Collection<? extends E> edgeSet) {
        boolean added = false;
        for (E edge : edgeSet) {
            added |= addEdge(edge);
        }
        return added;
    }

    /** 
     * This implementation calls {@link #addEdgeWithoutCheck(Edge)} for all
     * elements of the given edge set.
     */
    public boolean addEdgeSetWithoutCheck(Collection<? extends E> edgeSet) {
        boolean added = false;
        for (E edge : edgeSet) {
            added |= addEdgeWithoutCheck(edge);
        }
        return added;
    }

    /**
     * This implementation calls {@link #addNode(Node)} and
     * {@link #addEdgeWithoutCheck(Edge)} for the actual addition of
     * the edge and its incident nodes.
     */
    @SuppressWarnings("unchecked")
    public boolean addEdge(E edge) {
        assert !isFixed() : "Trying to add " + edge + " to unmodifiable graph";
        boolean added = !containsEdge(edge);
        if (added) {
            addNode((N) edge.source());
            addNode((N) edge.target());
            addEdgeWithoutCheck(edge);
        }
        return added;
    }

    /**
     * This implementation calls {@link #removeEdge(Edge)} and 
     * {@link #removeNodeWithoutCheck(Node)} for the actual removal
     * of the incident edges and the node.
     */
    public boolean removeNode(N node) {
        assert !isFixed() : "Trying to remove " + node
            + " from unmodifiable graph";
        boolean removed = containsNode(node);
        if (removed) {
            for (E edge : edgeSet(node)) {
                removeEdge(edge);
            }
            removeNodeWithoutCheck(node);
        }
        return removed;
    }

    public boolean removeNodeSet(Collection<? extends N> nodeSet) {
        boolean removed = false;
        for (N node : nodeSet) {
            removed |= removeNode(node);
        }
        return removed;
    }

    public boolean removeNodeSetWithoutCheck(Collection<? extends N> nodeSet) {
        boolean removed = false;
        for (N node : nodeSet) {
            removed |= removeNodeWithoutCheck(node);
        }
        return removed;
    }

    public boolean removeEdgeSet(Collection<? extends E> edgeSet) {
        boolean removed = false;
        for (E edge : edgeSet) {
            removed |= removeEdge(edge);
        }
        return removed;
    }

    public boolean mergeNodes(N from, N to) {
        assert isTypeCorrect(from);
        assert isTypeCorrect(to);
        if (!from.equals(to)) {
            // compute edge replacements and add new edges
            for (E edge : new HashSet<E>(edgeSet(from))) {
                boolean changed = false;
                @SuppressWarnings("unchecked")
                N source = (N) edge.source();
                if (source.equals(from)) {
                    source = to;
                    changed = true;
                }
                @SuppressWarnings("unchecked")
                N target = (N) edge.target();
                if (target.equals(from)) {
                    target = to;
                    changed = true;
                }
                if (changed) {
                    @SuppressWarnings("unchecked")
                    L label = (L) edge.label();
                    E newEdge = createEdge(source, label, target);
                    addEdgeWithoutCheck(newEdge);
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
    public abstract AbstractGraph<N,L,E> clone();

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
     * Factory method for a graph cache. This implementation returns a
     * {@link GraphCache}.
     * @return the graph cache
     */
    @Override
    @SuppressWarnings("all")
    protected GraphCache createCache() {
        return new GraphCache(this);
    }

    /** The default is not to create any graph elements. */
    @Override
    public ElementFactory<N,L,E> getFactory() {
        throw new UnsupportedOperationException();
    }

    /**
     * Map in which varies kinds of data can be stored.
     */
    private GraphInfo<N,E> graphInfo;

    // -------------------- REPORTER DEFINITIONS ------------------------

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
                    sourceCell.one().addAll(targetCell.one());
                    sourceCell.two().addAll(targetCell.two());
                    for (N loser : targetCell.one()) {
                        resultMap.put(loser, sourceCell);
                    }
                    for (E loser : targetCell.two()) {
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
                sourceCell.two().add(edge);
            }
            resultMap.put(edge, sourceCell);
        }
        return new HashSet<Pair<Set<N>,Set<E>>>(resultMap.values());
    }

    /** Returns an empty graph. */
    @SuppressWarnings("unchecked")
    static public <N extends Node,L extends Label,E extends Edge> AbstractGraph<N,L,E> emptyGraph() {
        return EMPTY_GRAPH;
    }

    /**
     * Returns the number of graphs created and never fixed.
     * @return the number of graphs created and never fixed
     */
    static public int getModifiableGraphCount() {
        return modifiableGraphCount;
    }

    /**
     * Provides a textual description of a given graph. Lists the nodes and
     * their outgoing edges.
     * @param graph the graph to be described
     * @return a textual description of <tt>graph</tt>
     */
    public static String toString(Graph<?,?,?> graph) {
        StringBuffer result = new StringBuffer();
        result.append(graph.getInfo());
        result.append(String.format("Nodes: %s%n", graph.nodeSet()));
        result.append(String.format("Edges: %s%n", graph.edgeSet()));
        return "Nodes: " + graph.nodeSet() + "; Edges: " + graph.edgeSet();
    }

    /**
     * Private copy of the static variable to allow compiler optimization.
     */
    static private final boolean GATHER_STATISTICS = Groove.GATHER_STATISTICS;

    /**
     * Counts the number of graphs that were not fixed. Added for debugging
     * purposes: observers of modifiable graphs may cause memory leaks.
     */
    static private int modifiableGraphCount = 0;
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
    private static class EmptyGraph<N extends Node,L extends Label,E extends Edge>
            extends AbstractGraph<N,L,E> implements Cloneable {
        /**
         * The empty graph to which no elements can be added.
         */
        EmptyGraph() {
            setFixed();
        }

        public boolean addEdgeWithoutCheck(E edge) {
            throw new UnsupportedOperationException(
                "Can't add element to fixed empty graph");
        }

        public boolean removeNodeWithoutCheck(N node) {
            throw new UnsupportedOperationException(
                "Can't remove element from fixed empty graph");
        }

        @Override
        public EmptyGraph<N,L,E> clone() {
            return new EmptyGraph<N,L,E>();
        }

        public EmptyGraph<N,L,E> newGraph() {
            return new EmptyGraph<N,L,E>();
        }

        public boolean addNode(N node) {
            throw new UnsupportedOperationException(
                "Can't add element to fixed empty graph");
        }

        public Set<? extends E> edgeSet() {
            return Collections.emptySet();
        }

        public Set<? extends N> nodeSet() {
            return Collections.emptySet();
        }

        public boolean removeEdge(E edge) {
            throw new UnsupportedOperationException(
                "Can't remove element from fixed empty graph");
        }
    }
}