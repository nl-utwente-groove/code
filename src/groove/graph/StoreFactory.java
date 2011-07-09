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
import groove.trans.HostNode;
import groove.util.TreeHashSet;

/**
 * Abstract factory class that stores canonical representatives 
 * of nodes and edges.
 * @author Arend Rensink
 * @version $Revision $
 */
abstract public class StoreFactory<N extends Node,E extends Edge<N>,L extends Label>
        implements ElementFactory<N,E> {
    /** Constructor for a fresh factory. */
    protected StoreFactory() {
        this.nodeStore = createNodeStore();
        this.edgeStore = createEdgeStore();
    }

    /** Creates a fresh default host node. */
    public N createNode() {
        return this.nodeStore.createNode();
    }

    /**
     * Creates a fresh node with a given number.
     * @throws IllegalArgumentException if this factory has already created a 
     * node with this number
     */
    public N createNode(int nr) {
        return this.nodeStore.createNode(nr);
    }

    /** 
     * Adds a given (existing) node to this factory.
     * Throws an exception if an incompatible node with the same number
     * is already in the factory.
     * @return {@code true} if the node was not already in the factory
     * @throws IllegalArgumentException if an incompatible node with the same
     * number is already in the factory
     */
    public boolean addNode(HostNode node) throws IllegalArgumentException {
        return this.nodeStore.addNode(node);
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
        E result = this.edgeStore.put(edge);
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
    abstract protected E createEdge(N source, Label label, N target, int nr);

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

    /** Returns the highest default node node number. */
    @Override
    public int getMaxNodeNr() {
        return this.nodeStore.getMaxNodeNr();
    }

    /** Returns the number of known nodes. */
    public int getNodeCount() {
        return this.nodeStore.getNodeCount();
    }

    /**
     * Returns the total number of host edges created.
     * Since they are numbered in sequence, this is also the next free edge number.
     */
    public int getEdgeCount() {
        return this.edgeStore.size();
    }

    /** Clears the store of canonical nodes and edges. */
    protected void clear() {
        this.edgeStore.clear();
        this.nodeStore.clear();
    }

    /** Callback factory method to initialise the node store. */
    abstract protected NodeStore<? extends N> createNodeStore();

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

    /** Returns the node store of this factory. */
    protected NodeStore<? extends N> getNodeStore() {
        return this.nodeStore;
    }

    /** Store and factory of canonical host nodes. */
    private final NodeStore<? extends N> nodeStore;

    /**
     * A identity map, mapping previously created instances of
     * {@link DefaultEdge} to themselves. Used to ensure that edge objects are
     * reused.
     */
    private final TreeHashSet<E> edgeStore;
}
