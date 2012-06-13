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
        return createNode(nextNodeNr());
    }

    /**
     * Factory method to create a default node with a certain (positive) number. The idea
     * is to create canonical representatives, so node equality is object
     * equality.
     */
    public N createNode(int nr) {
        N result;
        assert nr >= 0;
        if (nr >= this.nodes.length) {
            int newSize =
                Math.max((int) (this.nodes.length * GROWTH_FACTOR), nr + 1);
            @SuppressWarnings("unchecked")
            N[] newNodes = (N[]) new Node[newSize];
            System.arraycopy(this.nodes, 0, newNodes, 0, this.nodes.length);
            this.nodes = newNodes;
        }
        result = this.nodes[nr];
        if (result == null) {
            result = this.nodes[nr] = this.factory.newNode(nr);
            this.nextNodeNr = Math.max(this.nextNodeNr, nr);
            this.nodeCount++;
        }
        return result;
    }

    /**
     * Returns the total number of nodes created.
     * @return the {@link #nodeCount}-value
     */
    public int getNodeCount() {
        return this.nodeCount;
    }

    /**
     * Returns the maximum node number created.
     */
    public int size() {
        return this.nextNodeNr;
    }

    /**
     * Returns the next free node number, according to the static counter.
     * @return the next node-number
     */
    private int nextNodeNr() {
        while (this.nextNodeNr < this.nodes.length
            && this.nodes[this.nextNodeNr] != null) {
            this.nextNodeNr++;
        }
        return this.nextNodeNr;
    }

    /**
     * The total number of nodes in the {@link #nodes} array.
     */
    private int nodeCount;

    /**
     * First (potentially) fresh node number available.
     */
    private int nextNodeNr;

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