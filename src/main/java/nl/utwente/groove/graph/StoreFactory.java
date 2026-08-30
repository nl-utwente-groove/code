/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
 * $Id$
 */
package nl.utwente.groove.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.Dispenser;
import nl.utwente.groove.util.collect.TreeHashSet;

/**
 * Abstract factory class that stores canonical representatives
 * of nodes and edges.
 * @author Arend Rensink
 * @version $Revision$
 */
abstract public class StoreFactory<N extends Node,E extends NumberedEdge,L extends Label>
    extends ElementFactory<N,E> {
    /** Constructor for a fresh factory.
     * @param simple indicates if the edges created by this factory are simple
     */
    @SuppressWarnings("unchecked")
    protected StoreFactory(boolean simple) {
        this.simple = simple;
        this.nodes = (N[]) new Node[INIT_CAPACITY];
        this.edges = (E[]) new NumberedEdge[INIT_CAPACITY];
        this.edgeStore = createEdgeStore();
    }

    /**
     * Constructor for a copy of a given factory. The copy shares the
     * canonical node and edge instances created so far, but numbers
     * subsequently created elements independently of the original: both
     * resume from the state of the original at the time of copying.
     */
    @AIGenerated("Claude Fable 5, 2026-08")
    protected StoreFactory(StoreFactory<N,E,L> original) {
        super(original);
        this.simple = original.simple;
        this.nodes = original.nodes.clone();
        this.nodeCount = original.nodeCount;
        this.edges = original.edges.clone();
        this.edgeCount = original.edgeCount;
        this.nextEdgeNr = original.nextEdgeNr;
        this.edgeStore = createEdgeStore();
        for (E edge : this.edges) {
            if (edge != null) {
                poolEdge(edge);
            }
        }
    }

    /** Indicates if the edges created by this factory are simple,
     * meaning that content-equal edges (same source, target and label)
     * are pooled, so that no two distinct such edges (parallel edges)
     * can coexist within this factory.
     */
    public boolean isSimple() {
        return this.simple;
    }

    /** Flag indicating if the edges created by this factory are simple. */
    private final boolean simple;

    /** Tests if a given node number is currently in use. */
    public boolean isUsed(int nr) {
        return nr < this.nodes.length && this.nodes[nr] != null;
    }

    /**
     * Returns a node with a given number, if created by this factory.
     * @return a node with number {@code nr}, or {@code null} if this factory
     * never created such a node
     */
    @Override
    public N getNode(int nr) {
        assert nr >= 0 : "invalid node number " + nr;
        return nr < this.nodes.length
            ? this.nodes[nr]
            : null;
    }

    /** Tests if a given node was created by this factory. */
    public boolean containsNode(N node) {
        Node stored = getNode(node.getNumber());
        return stored == node;
    }

    /**
     * Returns the total number of nodes created.
     * @return the {@link #nodeCount}-value
     */
    public int getNodeCount() {
        return this.nodeCount;
    }

    /*
     * Puts a node into the store kept by this factory.
     * This is only allowed if there is no node with this number.
     */
    @Override
    protected void registerNode(N node) {
        super.registerNode(node);
        int nr = node.getNumber();
        assert !isUsed(nr);
        if (nr >= this.nodes.length) {
            // extend the nodes array
            int newSize = Math.max((int) (this.nodes.length * GROWTH_FACTOR), nr + 1);
            @SuppressWarnings("unchecked")
            N[] newNodes = (N[]) new Node[newSize];
            System.arraycopy(this.nodes, 0, newNodes, 0, this.nodes.length);
            this.nodes = newNodes;
        }
        this.nodes[nr] = node;
        this.nodeCount++;
    }

    @Override
    protected Dispenser createNodeNrDispenser() {
        return new Dispenser() {
            @Override
            protected int computeNext() {
                do {
                    this.last++;
                } while (isUsed(this.last));
                return this.last;
            }

            @Override
            public void notifyUsed(int nr) {
                // do nothing; the usage is recorded in the nodes array
            }

            private int last = -1;
        };
    }

    /**
     * Array of canonical nodes, such that <code>nodes[i] == null</code> or
     * <code>nodes[i].getNumber() == i</code> for all <code>i</code>.
     */
    private N[] nodes;

    /**
     * The total number of nodes in the {@link #nodes} array.
     */
    private int nodeCount;

    @Override
    public E createEdge(N source, Label label, N target) {
        assert source != null : "Source node " + label.text() + "-edge should not be null";
        assert target != null : "Target node " + label.text() + "-edge should not be null";
        E edge = newEdge(source, label, target, getNextEdgeNr());
        return storeEdge(edge);
    }

    /**
     * Returns a suitable edge with a given (non-negative) edge number.
     * If this factory previously created an edge with that number, that edge
     * is returned, provided its content matches; for simple edges, an existing
     * edge with the same content but a different number also takes precedence.
     * Otherwise, a new edge is created and registered under the given number.
     * It is an error to call this for a number that is in use
     * by an edge with different content, unless the edges are simple
     * and an edge with the requested content exists under another number.
     */
    public E createEdge(N source, Label label, N target, int nr) {
        assert source != null : "Source node " + label.text() + "-edge should not be null";
        assert target != null : "Target node " + label.text() + "-edge should not be null";
        E edge = newEdge(source, label, target, nr);
        return storeEdge(edge);
    }

    /** Tests if a given edge number is currently in use. */
    public boolean isUsedEdgeNr(int nr) {
        return nr < this.edges.length && this.edges[nr] != null;
    }

    /**
     * Returns the edge with a given number, if created by this factory.
     * @return the edge with number {@code nr}, or {@code null} if this factory
     * never created such an edge
     */
    public @Nullable E getEdge(int nr) {
        assert nr >= 0 : "invalid edge number " + nr;
        return nr < this.edges.length
            ? this.edges[nr]
            : null;
    }

    /** Tests if a given edge was constructed by this factory. */
    public boolean containsEdge(E edge) {
        return getEdge(edge.getNumber()) == edge;
    }

    /**
     * Returns the total number of edges created.
     * @return the {@link #edgeCount}-value
     */
    public int getEdgeCount() {
        return this.edgeCount;
    }

    /**
     * Returns the canonical representative of a given edge,
     * registering the edge if it is new.
     * For simple edges, an existing edge with the same content takes
     * precedence, regardless of its number; for non-simple edges, an
     * existing edge with the same number takes precedence, provided
     * its content matches.
     */
    protected E storeEdge(@NonNull E edge) {
        if (isSimple()) {
            @Nullable
            E pooled = this.edgeStore.put(edge);
            if (pooled != null) {
                return pooled;
            }
        } else {
            @Nullable
            E stored = getEdge(edge.getNumber());
            if (stored != null) {
                assert stored.equals(edge) : "Edge number " + edge.getNumber()
                    + " is already used by " + stored;
                return stored;
            }
            poolEdge(edge);
        }
        registerEdge(edge);
        return edge;
    }

    /**
     * Returns an edge with the given content (source, label and target of the
     * probe) that does not satisfy a given exclusion predicate, minting the
     * probe as a fresh parallel copy only if all existing content-equal edges
     * are excluded. The existing copies are tried in edge number order, which
     * coincides with minting order, so that the answer is a function of the
     * content and the exclusion alone (see gh #905).
     * Only supported for non-simple factories; in a simple factory,
     * {@link #storeEdge} is canonical already.
     * @param probe freshly constructed edge carrying the requested content
     *        and the next free edge number
     * @param excluded predicate determining which existing copies are not
     *        acceptable as a result
     */
    @AIGenerated("Claude Fable 5, 2026-08")
    protected E storePooledEdge(@NonNull E probe, Predicate<E> excluded) {
        assert !isSimple();
        @Nullable
        E head = this.edgeStore.put(probe);
        if (head == null) {
            // no content-equal edge exists yet; the probe becomes the first copy
            registerEdge(probe);
            return probe;
        }
        var edgeCopies = this.edgeCopies;
        List<E> copies = edgeCopies == null
            ? null
            : edgeCopies.get(head);
        if (copies == null) {
            // the head is the only existing copy
            if (!excluded.test(head)) {
                return head;
            }
        } else {
            for (E copy : copies) {
                if (!excluded.test(copy)) {
                    return copy;
                }
            }
        }
        // all existing copies are excluded; mint the probe as a fresh copy
        registerEdge(probe);
        insertEdgeCopy(head, probe);
        return probe;
    }

    /**
     * Inserts an edge into the content-indexed pool: as the head if no
     * content-equal edge exists, as a further copy of the existing head
     * otherwise. Every registered edge of a non-simple factory passes
     * through here exactly once (via {@link #storeEdge},
     * {@link #storePooledEdge} or the copy constructor), so the pool covers
     * the {@link #edges} array.
     */
    @AIGenerated("Claude Fable 5, 2026-08")
    private void poolEdge(E edge) {
        @Nullable
        E head = this.edgeStore.put(edge);
        if (head != null) {
            assert !isSimple();
            insertEdgeCopy(head, edge);
        }
    }

    /**
     * Inserts a parallel copy into the pool list of a given head edge,
     * maintaining number order. The list holds all copies of its content,
     * the head included; it is created (seeded with the head) when the
     * second copy arrives. Number order makes the pool canonical: the list
     * is independent of registration order, and in particular a factory
     * copy, whose constructor replays the edges array in number order and
     * may thus elect a different head, still answers pooled requests
     * identically to the original.
     */
    @AIGenerated("Claude Fable 5, 2026-08")
    private void insertEdgeCopy(E head, E copy) {
        var edgeCopies = this.edgeCopies;
        if (edgeCopies == null) {
            this.edgeCopies = edgeCopies = new HashMap<>();
        }
        List<E> copies = edgeCopies.get(head);
        if (copies == null) {
            edgeCopies.put(head, copies = new ArrayList<>());
            copies.add(head);
        }
        int nr = copy.getNumber();
        int i = copies.size();
        while (i > 0 && copies.get(i - 1).getNumber() > nr) {
            i--;
        }
        copies.add(i, copy);
    }

    /**
     * Lazily created map from pool head edges (the copy of their content
     * stored in {@link #edgeStore}) to all parallel copies of that content,
     * the head included, in number order; contents with a single copy have
     * no entry. Only used in non-simple mode; lookup-only, never iterated,
     * so a hash map is deterministic here.
     */
    private @Nullable Map<E,List<E>> edgeCopies;

    /**
     * Puts an edge into the store kept by this factory.
     * This is only allowed if there is no edge with this number.
     */
    protected void registerEdge(E edge) {
        int nr = edge.getNumber();
        assert !isUsedEdgeNr(nr);
        if (nr >= this.edges.length) {
            // extend the edges array
            int newSize = Math.max((int) (this.edges.length * GROWTH_FACTOR), nr + 1);
            @SuppressWarnings("unchecked")
            E[] newEdges = (E[]) new NumberedEdge[newSize];
            System.arraycopy(this.edges, 0, newEdges, 0, this.edges.length);
            this.edges = newEdges;
        }
        this.edges[nr] = edge;
        this.edgeCount++;
    }

    /**
     * Returns the next free edge number, without reserving it.
     * The number remains the next free one until an edge is registered under it.
     */
    protected int getNextEdgeNr() {
        int result = this.nextEdgeNr;
        while (isUsedEdgeNr(result)) {
            result++;
        }
        this.nextEdgeNr = result;
        return result;
    }

    /**
     * Callback factory method to create a new edge object.
     * This will then be compared with the edge store to replace it by its
     * canonical representative.
     */
    abstract protected @NonNull E newEdge(N source, Label label, N target, int nr);

    /** Callback factory method to initialise the edge store. */
    protected TreeHashSet<E> createEdgeStore() {
        return new TreeHashSet<>() {
            @Override
            final protected boolean areEqual(E newKey, E oldKey) {
                return areStoredEqual(newKey, oldKey);
            }

            @Override
            final protected int getCode(E key) {
                return getStoredCode(key);
            }
        };
    }

    /**
     * Callback method determining the equality used by the edge store:
     * content-based, i.e., on source, target and label.
     * This is deliberately independent of the edge class's own equality,
     * as the store's task is to find the canonical representative
     * for a freshly created (hence not yet canonical) edge.
     */
    protected boolean areStoredEqual(E one, E two) {
        return one.source().equals(two.source()) && one.target().equals(two.target())
            && one.label().equals(two.label());
    }

    /**
     * Callback method determining the hash code used by the edge store:
     * content-based, consistent with {@link #areStoredEqual}.
     * The computation mirrors {@link AEdge#computeHashCode()} for simple edges.
     */
    protected int getStoredCode(E edge) {
        int labelCode = edge.label().hashCode();
        int sourceCode = 3 * edge.source().hashCode();
        int targetCode = (labelCode + 2) * edge.target().hashCode();
        return labelCode ^ ((sourceCode << 1) + (sourceCode >>> 31))
            + ((targetCode << 2) + (targetCode >>> 30));
    }

    /**
     * Array of canonical edges, such that <code>edges[i] == null</code> or
     * <code>edges[i].getNumber() == i</code> for all <code>i</code>.
     */
    private E[] edges;

    /**
     * The total number of edges in the {@link #edges} array.
     */
    private int edgeCount;

    /** Lower bound for the next free edge number. */
    private int nextEdgeNr;

    /**
     * Store of canonical edge representatives, used in simple mode
     * (see {@link #isSimple()}) to ensure that edges with the same
     * content are reused. In non-simple mode, where content-equal edges
     * with distinct numbers may coexist, edges are only kept in the
     * {@link #edges} array.
     */
    private final TreeHashSet<E> edgeStore;

    /** Initial capacity of the nodes array. */
    static protected final int INIT_CAPACITY = 100;

    /** Growth factor of the nodes array. */
    static protected final float GROWTH_FACTOR = 2.0f;
}
