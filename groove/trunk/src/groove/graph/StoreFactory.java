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

import groove.trans.HostEdge;
import groove.util.TreeHashSet;

import java.util.Arrays;

/**
 * Abstract factory class that stores canonical representatives 
 * of nodes and edges.
 * @author Arend Rensink
 * @version $Revision $
 */
abstract public class StoreFactory<N extends Node,E extends Edge,L extends Label>
        implements ElementFactory<N,E> {
    /** Constructor for a fresh factory. */
    protected StoreFactory() {
        this.edgeStore = createEdgeStore();
    }

    /** Returns a node with a given type and the first currently unused node number. */
    public N createNode() {
        return createNode(getNextNodeNr());
    }

    /**
     * Factory method to create a node with given type a certain (positive) number. 
     * The idea is to create canonical representatives, so node equality is object
     * equality.
     * @param nr the number of the new node
     */
    public N createNode(int nr) {
        N result;
        assert nr >= 0;
        if (nr >= this.nodes.length || this.nodes[nr] == null) {
            addNode(result = newNode(nr));
        } else {
            result = this.nodes[nr];
        }
        return result;
    }

    /** Factory method to create a fresh node with a given number. */
    abstract protected N newNode(int nr);

    /** 
     * Adds a canonical node to the store.
     * This is only correct if a node with this number does not already
     * exist, or is identical to the added node. 
     * @throws IllegalArgumentException if a different node with the same number 
     * is already in the store
     * @return {@code true} if the store changed as a result of this operation
     */
    @SuppressWarnings("unchecked")
    public boolean addNode(Node node) throws IllegalArgumentException {
        int nr = node.getNumber();
        if (nr < this.nodes.length && this.nodes[nr] != null
            && node != this.nodes[nr]) {
            throw new IllegalArgumentException(String.format(
                "Duplicate nodes %s and %s with the same number %d", node,
                this.nodes[nr], nr));
        }
        if (nr >= this.nodes.length) {
            int newSize =
                Math.max((int) (this.nodes.length * GROWTH_FACTOR), nr + 1);
            N[] newNodes = (N[]) new Node[newSize];
            System.arraycopy(this.nodes, 0, newNodes, 0, this.nodes.length);
            this.nodes = newNodes;
        }
        if (this.nodes[nr] == null) {
            this.nodes[nr] = (N) node;
            this.nodeCount++;
            this.maxNodeNr = Math.max(this.maxNodeNr, nr);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns the total number of nodes created.
     * @return the {@link #nodeCount}-value
     */
    public int getNodeCount() {
        return this.nodeCount;
    }

    /** 
     * Resets the node store.
     * Nodes created after calling this method will not be compatible
     * with old nodes. 
     */
    public void clear() {
        Arrays.fill(this.nodes, null);
        this.nodeCount = 0;
        this.nextNodeNr = 0;
        this.maxNodeNr = -1;
        this.edgeStore.clear();
    }

    /**
     * Returns the next free node number.
     */
    public int getNextNodeNr() {
        while (this.nextNodeNr < this.nodes.length
            && this.nodes[this.nextNodeNr] != null) {
            this.nextNodeNr++;
        }
        return this.nextNodeNr;
    }

    /** Returns the highest node number in the store.
     * @return the highest number of a node in the store, or {@code -1} if
     * the store is empty.
     */
    public int getMaxNodeNr() {
        return this.maxNodeNr;
    }

    /** Returns the node in the store with the given number. */
    public Node getNodeFromNr(int nr) {
        Node node = this.nodes[nr];
        assert node != null;
        return node;
    }

    /** Creates a label with the given text. */
    abstract public L createLabel(String text);

    @Override
    public E createEdge(N source, String text, N target) {
        return createEdge(source, createLabel(text), target);
    }

    public E createEdge(N source, Label label, N target) {
        assert source != null : "Source node of host edge should not be null";
        assert target != null : "Target node of host edge should not be null";
        E edge = createEdge(source, label, target, getEdgeCount());
        return storeEdge(edge);
    }

    /** 
     * Callback factory method to create a new edge object.
     * This will then be compared with the edge store to replace it by its
     * canonical representative.
     */
    abstract protected E createEdge(N source, Label label, N target, int nr);

    /** Puts an edge in the store and returns its canonical representative. */
    protected E storeEdge(E edge) {
        E result = this.edgeStore.put(edge);
        if (result == null) {
            result = edge;
        }
        return result;
    }

    /** 
     * Adds a given edge to the edges known to this store.
     * The source and target nodes are assumed to be known already.
     * Throws an exception if an equal but not identical edge was already in the store
     * @return {@code true} if the edge was not already known in this store.
     * @throws IllegalArgumentException if an equal but not identical edge
     * was already in the store
     */
    public boolean addEdge(E edge) throws IllegalArgumentException {
        E oldEdge = this.edgeStore.put(edge);
        if (oldEdge != null && oldEdge != edge) {
            throw new IllegalArgumentException(String.format(
                "Duplicate edges %s", edge));
        }
        return oldEdge == null;
    }

    /**
     * Returns the total number of host edges created.
     * Since they are numbered in sequence, this is also the next free edge number.
     */
    public int getEdgeCount() {
        return this.edgeStore.size();
    }

    /** Callback factory method to initialise the edge store. */
    protected TreeHashSet<E> createEdgeStore() {
        return new TreeHashSet<E>() {
            /**
             * As {@link HostEdge}s test equality by object identity,
             * we need to weaken the set's equality test.
             */
            @Override
            final protected boolean areEqual(E o1, E o2) {
                return o1.source().equals(o2.source())
                    && o1.target().equals(o2.target())
                    && o1.label().equals(o2.label());
            }

            @Override
            final protected boolean allEqual() {
                return false;
            }
        };
    }

    /**
     * The total number of nodes in the {@link #nodes} array.
     */
    private int nodeCount;

    /**
     * First (potentially) fresh node number available.
     */
    private int nextNodeNr;

    /**
     * Highest node number in the store.
     */
    private int maxNodeNr = -1;

    /**
     * Array of canonical nodes, such that <code>nodes[i] == 0</code> or
     * <code>nodes[i].getNumber() == i</code> for all <code>i</code>.
     */
    @SuppressWarnings("unchecked")
    private N[] nodes = (N[]) new Node[INIT_CAPACITY];

    /**
     * A identity map, mapping previously created instances of
     * {@link DefaultEdge} to themselves. Used to ensure that edge objects are
     * reused.
     */
    private final TreeHashSet<E> edgeStore;

    /** Initial capacity of the nodes array. */
    static private final int INIT_CAPACITY = 100;

    /** Growth factor of the nodes array. */
    static private final float GROWTH_FACTOR = 2.0f;
}
