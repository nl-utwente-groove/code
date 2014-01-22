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
import groove.graph.iso.PartitionRefiner;
import groove.util.Dispenser;
import groove.util.Groove;
import groove.util.cache.AbstractCacheHolder;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Partial implementation of a graph. Adds to the AbstractGraphShape the ability
 * to add nodes and edges, and some morphism capabilities.
 * @author Arend Rensink
 * @version $Revision$
 */
public abstract class AGraph<N extends Node,E extends Edge> extends
        AbstractCacheHolder<GraphCache<N,E>> implements GGraph<N,E> {
    /**
     * Constructs an abstract named graph.
     * @param name the (non-{@code null}) name of the graph.
     */
    protected AGraph(String name) {
        super(null);
        modifiableGraphCount++;
        assert name != null;
        this.name = name;
    }

    @Override
    public int nodeCount() {
        return nodeSet().size();
    }

    @Override
    public int edgeCount() {
        return edgeSet().size();
    }

    /*
     * Defers the containment question to {@link #nodeSet()}
     */
    @Override
    public boolean containsNode(Node elem) {
        assert isTypeCorrect(elem);
        return nodeSet().contains(elem);
    }

    /*
     * Defers the containment question to {@link #edgeSet()}
     */
    @Override
    public boolean containsEdge(Edge elem) {
        assert isTypeCorrect(elem) : String.format("Edge %s is not of correct type", elem);
        return edgeSet().contains(elem);
    }

    /**
     * Tests whether this graph contains an edge with given
     * source, label and target.
     */
    public boolean containsEdge(N source, Label label, N target) {
        return containsEdge(getFactory().createEdge(source, label, target));
    }

    @Override
    public int size() {
        return nodeCount() + edgeCount();
    }

    @Override
    public boolean isEmpty() {
        return nodeCount() == 0;
    }

    /**
     * This implementation retrieves the node-to-edges mapping from the cache,
     * and looks up the required set in the image for <tt>node</tt>.
     */
    @Override
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
    @Override
    public Set<? extends E> outEdgeSet(Node node) {
        assert isTypeCorrect(node) : String.format("Type %s of node %s incorrect for this graph",
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
    @Override
    public Set<? extends E> inEdgeSet(Node node) {
        assert isTypeCorrect(node);
        Set<? extends E> result = getCache().getNodeInEdgeMap().get(node);
        if (result == null) {
            return Collections.emptySet();
        } else {
            return Collections.unmodifiableSet(result);
        }
    }

    @Override
    public Set<? extends E> edgeSet(Label label) {
        assert isTypeCorrect(label);
        Set<? extends E> result = getCache().getLabelEdgeMap().get(label);
        if (result != null) {
            return Collections.unmodifiableSet(result);
        } else {
            return Collections.emptySet();
        }
    }

    @Override
    public boolean hasInfo() {
        return this.graphInfo != null;
    }

    @Override
    public GraphInfo getInfo() {
        if (this.graphInfo == null) {
            this.graphInfo = new GraphInfo();
        }
        return this.graphInfo;
    }

    @Override
    public boolean isFixed() {
        return isCacheCollectable();
    }

    @Override
    public boolean setFixed() {
        boolean result = !isFixed();
        if (result) {
            setCacheCollectable();
            if (hasInfo()) {
                getInfo().setFixed();
            }
            if (GATHER_STATISTICS) {
                modifiableGraphCount--;
            }
        }
        return result;
    }

    @Override
    public void testFixed(boolean fixed) throws IllegalStateException {
        if (isFixed() != fixed) {
            throw new IllegalStateException(String.format("Expected graph '%s' to be %s",
                getName(), fixed ? "fixed" : "unfixed"));
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
        if (hasCache()) {
            getCache().addUpdate(node);
        }
    }

    /**
     * Calls {@link GraphCache#addUpdate(Edge)}
     * if the cache is not cleared.
     * @param edge the edge being added
     */
    protected void fireAddEdge(E edge) {
        if (hasCache()) {
            getCache().addUpdate(edge);
        }
    }

    /**
     * Calls {@link GraphCache#removeUpdate(Node)}
     * if the cache is not cleared.
     * @param node the node being removed
     */
    protected void fireRemoveNode(N node) {
        if (hasCache()) {
            getCache().removeUpdate(node);
        }
    }

    /**
     * Calls {@link GraphCache#removeUpdate(Edge)}
     * if the cache is not cleared.
     * @param edge the edge being removed
     */
    protected void fireRemoveEdge(E edge) {
        if (hasCache()) {
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
     * Returns the node counter used to number nodes distinctly.
     */
    final protected Dispenser getNodeCounter() {
        return getCache().getNodeCounter();
    }

    @Override
    public N addNode() {
        N freshNode = getFactory().createNode(getNodeCounter());
        assert !nodeSet().contains(freshNode) : String.format(
            "Fresh node %s already in node set %s", freshNode, nodeSet());
        addNode(freshNode);
        return freshNode;
    }

    @Override
    public N addNode(int nr) {
        N freshNode = getFactory().createNode(nr);
        assert !nodeSet().contains(freshNode) : String.format(
            "Fresh node %s already in node set %s", freshNode, nodeSet());
        addNode(freshNode);
        return freshNode;
    }

    @Override
    public boolean addNodeSet(Collection<? extends N> nodeSet) {
        boolean added = false;
        for (N node : nodeSet) {
            added |= addNode(node);
        }
        return added;
    }

    /**
     * Creates its result using {@link ElementFactory#createEdge(Node, Label, Node)}.
     */
    @Override
    public E addEdge(N source, String label, N target) {
        return addEdge(source, getFactory().createLabel(label), target);
    }

    /**
     * Creates its result using {@link ElementFactory#createEdge(Node, Label, Node)}.
     */
    @Override
    public E addEdge(N source, Label label, N target) {
        assert containsNode(source);
        assert containsNode(target);
        E result = getFactory().createEdge(source, label, target);
        addEdge(result);
        return result;
    }

    /**
     * This implementation calls {@link #addNode(Node)} and
     * {@link #addEdge(Edge)} for the actual addition of
     * the edge and its incident nodes.
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean addEdgeContext(E edge) {
        assert !isFixed() : "Trying to add " + edge + " to unmodifiable graph";
        boolean added = !containsEdge(edge);
        if (added) {
            addNode((N) edge.source());
            addNode((N) edge.target());
            addEdge(edge);
        }
        return added;
    }

    @Override
    public boolean addEdgeSetContext(Collection<? extends E> edgeSet) {
        boolean added = false;
        for (E edge : edgeSet) {
            added |= addEdgeContext(edge);
        }
        return added;
    }

    /*
     * This implementation calls {@link #removeEdge(Edge)} and 
     * {@link #removeNodeWithoutCheck(Node)} for the actual removal
     * of the incident edges and the node.
     */
    @Override
    public boolean removeNodeContext(N node) {
        assert !isFixed() : "Trying to remove " + node + " from unmodifiable graph";
        boolean removed = containsNode(node);
        if (removed) {
            for (E edge : edgeSet(node)) {
                removeEdge(edge);
            }
            removeNode(node);
        }
        return removed;
    }

    @Override
    public boolean removeNodeSetContext(Collection<? extends N> nodeSet) {
        boolean removed = false;
        for (N node : nodeSet) {
            removed |= removeNodeContext(node);
        }
        return removed;
    }

    @Override
    public boolean removeNodeSet(Collection<? extends N> nodeSet) {
        boolean removed = false;
        for (N node : nodeSet) {
            removed |= removeNode(node);
        }
        return removed;
    }

    @Override
    public boolean removeEdgeSet(Collection<? extends E> edgeSet) {
        boolean removed = false;
        for (E edge : edgeSet) {
            removed |= removeEdge(edge);
        }
        return removed;
    }

    /**
     * Merges two nodes in this graph, by adding all edges to and from the first
     * node to the second, and subsequently removing the first.
     * @param from node to be deleted
     * @param to node to receive copies of the edges to and from the other
     * @return {@code true} if {@code from} is distinct from
     *         {@code to}, so a merge actually took place
     */
    @SuppressWarnings("unchecked")
    public boolean mergeNodes(N from, N to) {
        assert isTypeCorrect(from);
        assert isTypeCorrect(to);
        if (!from.equals(to)) {
            // compute edge replacements and add new edges
            for (E edge : new HashSet<E>(edgeSet(from))) {
                boolean changed = false;
                N source = (N) edge.source();
                if (source.equals(from)) {
                    source = to;
                    changed = true;
                }
                N target = (N) edge.target();
                if (target.equals(from)) {
                    target = to;
                    changed = true;
                }
                if (changed) {
                    Label label = edge.label();
                    addEdge(source, label, target);
                    removeEdge(edge);
                }
            }
            // delete the old node and edges
            removeNode(from);
            return true;
        } else {
            return false;
        }
    }

    /* Overridden to rule out the CloneNotSupportedException */
    @Override
    public abstract AGraph<N,E> clone();

    /**
     * Tests if the certificate strategy (of the correct strength) is currently instantiated.
     * @param strong the strength of the required certifier
     * @see CertificateStrategy#getStrength()
     */
    public boolean hasCertifier(boolean strong) {
        return hasCache() && getCache().hasCertifier(strong);
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
    public ElementFactory<N,E> getFactory() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setName(String name) {
        assert !isFixed();
        assert name != null;
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    private String name = NO_NAME;

    /**
     * Map in which various kinds of data can be stored.
     */
    private GraphInfo graphInfo;

    // -------------------- REPORTER DEFINITIONS ------------------------

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
    public static String toString(Graph graph) {
        StringBuffer result = new StringBuffer();
        if (graph.hasInfo()) {
            result.append(graph.getInfo());
        }
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
    static private CertificateStrategy certificateFactory = new PartitionRefiner(
        (GGraph<Node,Edge>) null);

    /**
     * Changes the strategy for computing isomorphism certificates.
     * @param certificateFactory the new strategy
     * @see #getCertifier(boolean)
     */
    static public void setCertificateFactory(CertificateStrategy certificateFactory) {
        AGraph.certificateFactory = certificateFactory;
    }

    /**
     * Returns the strategy for computing isomorphism certificates.
     * @return the strategy for computing isomorphism certificates
     */
    static public CertificateStrategy getCertificateFactory() {
        return certificateFactory;
    }
}