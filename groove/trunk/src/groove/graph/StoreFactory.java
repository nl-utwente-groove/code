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
 * $Id$
 */
package groove.graph;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import groove.grammar.host.HostEdge;
import groove.graph.plain.PlainEdge;
import groove.util.Dispenser;
import groove.util.collect.TreeHashSet;

/**
 * Abstract factory class that stores canonical representatives
 * of nodes and edges.
 * @author Arend Rensink
 * @version $Revision $
 */
abstract public class StoreFactory<N extends Node,E extends Edge,L extends Label>
    extends ElementFactory<N,E> {
    /** Constructor for a fresh factory. */
    @SuppressWarnings("unchecked")
    protected StoreFactory() {
        this.nodes = (N[]) new Node[INIT_CAPACITY];
        this.edgeStore = createEdgeStore();
    }

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
        return nr < this.nodes.length ? this.nodes[nr] : null;
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
        assert!isUsed(nr);
        if (nr >= this.nodes.length) {
            // extend the nodes array
            int newSize = Math.max((int) (this.nodes.length * GROWTH_FACTOR), nr + 1);
            @SuppressWarnings("unchecked") N[] newNodes = (N[]) new Node[newSize];
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
     * Array of canonical nodes, such that <code>nodes[i] == 0</code> or
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
        E edge = newEdge(source, label, target, getEdgeCount());
        return storeEdge(edge);
    }

    /** Tests if a given edge was constructed by this factory. */
    public boolean containsEdge(E edge) {
        return this.edgeStore.put(edge) == edge;
    }

    /**
     * Returns the total number of host edges created.
     * Since they are numbered in sequence, this is also the next free edge number.
     */
    public int getEdgeCount() {
        return this.edgeStore.size();
    }

    /** Puts an edge in the store and returns its canonical representative. */
    protected E storeEdge(@NonNull E edge) {
        @Nullable E result = this.edgeStore.put(edge);
        if (result == null) {
            result = edge;
        }
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
        return new TreeHashSet<E>() {
            /**
             * As {@link HostEdge}s test equality by object identity,
             * we need to weaken the set's equality test.
             */
            @Override
            final protected boolean areEqual(E o1, E o2) {
                return o1.source()
                    .equals(o2.source())
                    && o1.target()
                        .equals(o2.target())
                    && o1.label()
                        .equals(o2.label());
            }

            @Override
            final protected boolean allEqual() {
                return false;
            }
        };
    }

    /**
     * A identity map, mapping previously created instances of
     * {@link PlainEdge} to themselves. Used to ensure that edge objects are
     * reused.
     */
    private final TreeHashSet<E> edgeStore;

    /** Initial capacity of the nodes array. */
    static protected final int INIT_CAPACITY = 100;

    /** Growth factor of the nodes array. */
    static protected final float GROWTH_FACTOR = 2.0f;
}
