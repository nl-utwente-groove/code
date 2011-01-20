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

import groove.graph.Node.Factory;

import java.util.Arrays;

/**
 * Store and factory for canonical representatives of node types.
 * @author Arend Rensink
 * @version $Revision $
 */
public class NodeStore<N extends Node> {
    /**
     * Creates a store, given a prototype edge object to 
     * create new edges off.
     */
    public NodeStore(Factory<N> factory) {
        this.factory = factory;
    }

    /** Returns the node with the first currently unused node number. */
    public N createNode() {
        return createNode(getNextNodeNr());
    }

    /**
     * Factory method to create a default node with a certain (positive) number. The idea
     * is to create canonical representatives, so node equality is object
     * equality.
     */
    public N createNode(int nr) {
        N result;
        assert nr >= 0;
        if (nr >= this.nodes.length || this.nodes[nr] == null) {
            addNode(result = this.factory.newNode(nr));
        } else {
            result = this.nodes[nr];
        }
        return result;
    }

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

    /** The prototype object used to create new nodes from. */
    private final Factory<N> factory;

    /**
     * Array of canonical nodes, such that <code>nodes[i] == 0</code> or
     * <code>nodes[i].getNumber() == i</code> for all <code>i</code>.
     */
    @SuppressWarnings("unchecked")
    private N[] nodes = (N[]) new Node[INIT_CAPACITY];

    /** Initial capacity of the nodes array. */
    static private final int INIT_CAPACITY = 100;

    /** Growth factor of the nodes array. */
    static private final float GROWTH_FACTOR = 2.0f;
}
