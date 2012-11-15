/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.abstraction.pattern.shape;

import groove.graph.DefaultEdge;
import groove.graph.ElementFactory;
import groove.graph.Label;
import groove.graph.Morphism;
import groove.trans.HostEdge;
import groove.util.FreeNumberDispenser;
import groove.util.TreeHashSet;

import java.util.Collection;

/**
 * Factory for pattern graph elements.
 * 
 * @author Eduardo Zambon
 */
public final class PatternFactory implements
        ElementFactory<PatternNode,PatternEdge> {

    // ------------------------------------------------------------------------
    // Static Fields
    // ------------------------------------------------------------------------

    /** Initial capacity of the nodes array. */
    static private final int NODES_INIT_CAPACITY = 100;

    /** Growth factor of the nodes array. */
    static private final float NODES_GROWTH_FACTOR = 2.0f;

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    private final TypeGraph typeGraph;

    private PatternNode nodes[] = new PatternNode[NODES_INIT_CAPACITY];

    /**
     * A identity map, mapping previously created instances of
     * {@link DefaultEdge} to themselves. Used to ensure that edge objects are
     * reused.
     */
    private final TreeHashSet<PatternEdge> edges;

    /**
     * Highest node number in the store.
     */
    private int maxNodeNr;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Default constructor. */
    public PatternFactory(TypeGraph typeGraph) {
        this.typeGraph = typeGraph;
        this.edges = createEdgeStore();
        this.maxNodeNr = -1;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    @Override
    public Morphism<PatternNode,PatternEdge> createMorphism() {
        return new Morphism<PatternNode,PatternEdge>(this);
    }

    /** Creates and returns an empty pattern graph associated with this factory. */
    public PatternGraph newPatternGraph() {
        return new PatternGraph(this.typeGraph, this);
    }

    @Override
    public int getMaxNodeNr() {
        return this.maxNodeNr;
    }

    /**
     * Returns the next free node number.
     */
    public int getNextNodeNr() {
        return this.maxNodeNr + 1;
    }

    /** Factory method to retrieve a node with a given number. */
    private PatternNode getNode(int nr) {
        assert nr >= 0 && nr < this.nodes.length;
        PatternNode result = this.nodes[nr];
        assert result != null;
        return result;
    }

    private PatternNode newNode(int nr, TypeNode type) {
        return new PatternNode(nr, type);
    }

    /**
     * Factory method to create a node with given type a certain (positive) number. 
     * The idea is to create canonical representatives, so node equality is object
     * equality.
     * @param nr the number of the new node
     */
    public PatternNode createNode(int nr, TypeNode type) {
        PatternNode result;
        assert nr >= 0;
        if (nr >= this.maxNodeNr || this.nodes[nr] == null) {
            addNode(result = newNode(nr, type));
        } else {
            result = this.nodes[nr];
        }
        assert result != null;
        return result;
    }

    /**
     * Creates and returns a node with a given type and the next available
     * node number.
     */
    public PatternNode createNode(TypeNode type) {
        return createNode(getNextNodeNr(), type);
    }

    /**
     * Creates and returns a node with the given type. Tries to re-use node
     * numbers that do not occur in the given set, while ensuring type
     * consistency.
     * @see #createNode(TypeNode, FreeNumberDispenser)
     */
    public PatternNode createNode(TypeNode type,
            Collection<PatternNode> usedNodes) {
        FreeNumberDispenser dispenser = new FreeNumberDispenser(usedNodes);
        return this.createNode(type, dispenser);
    }

    /**
     * Creates and returns a node with the given type. Tries to re-use node
     * numbers that do not occur in the given array, while ensuring type
     * consistency.
     * @see #createNode(TypeNode, FreeNumberDispenser)
     */
    public PatternNode createNode(TypeNode type, int usedNodes[]) {
        FreeNumberDispenser dispenser = new FreeNumberDispenser(usedNodes);
        return this.createNode(type, dispenser);
    }

    /**
     * Creates and returns a node with the given type. Tries to re-use node
     * numbers that do not occur in the set given to the dispenser, while
     * ensuring type consistency. 
     */
    private PatternNode createNode(TypeNode type, FreeNumberDispenser dispenser) {
        int freeNr = dispenser.getNext();
        PatternNode result = null;
        while (freeNr != -1) {
            // We have a free number of a node that already exists in the store.
            // Retrieve this node and check if the type coincide.
            result = retrieveNode(freeNr, type);
            if (result.getType() == type) {
                // Yes, the types are the same. We are done.
                return result;
            } else {
                // No, the types are different. Try another free number.
                freeNr = dispenser.getNext();
            }
        }
        // There are no more free numbers to try. We can go over the rest
        // of the node store and look for an node with the proper type.
        for (int i = dispenser.getMaxNumber() + 1; i < getMaxNodeNr(); i++) {
            result = getNode(i);
            if (result.getType() == type) {
                // Yes, the types are the same. We are done.
                return result;
            }
        }
        // Nothing else to do, we need to create a new node.
        result = createNode(type);
        return result;
    }

    /**
     * Returns the node from the store with the given number. If the entry is
     * empty a new node with the given number and type is created.
     * */
    private PatternNode retrieveNode(int nodeNr, TypeNode type) {
        PatternNode result = this.nodes[nodeNr];
        if (result == null) {
            result = createNode(nodeNr, type);
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
    public boolean addNode(PatternNode node) throws IllegalArgumentException {
        int nr = node.getNumber();
        if (nr < this.nodes.length && this.nodes[nr] != null
            && node != this.nodes[nr]) {
            throw new IllegalArgumentException(String.format(
                "Duplicate nodes %s and %s with the same number %d", node,
                this.nodes[nr], nr));
        }
        if (nr >= this.nodes.length) {
            int newSize =
                Math.max((int) (this.nodes.length * NODES_GROWTH_FACTOR),
                    nr + 1);
            PatternNode newNodes[] = new PatternNode[newSize];
            System.arraycopy(this.nodes, 0, newNodes, 0, this.nodes.length);
            this.nodes = newNodes;
        }
        if (this.nodes[nr] == null) {
            this.nodes[nr] = node;
            this.maxNodeNr = Math.max(this.maxNodeNr, nr);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns the total number of pattern edges created.
     * Since they are numbered in sequence, this is also the next free edge number.
     */
    public int getEdgeCount() {
        return this.edges.size();
    }

    /** Callback factory method to initialise the edge store. */
    private TreeHashSet<PatternEdge> createEdgeStore() {
        return new TreeHashSet<PatternEdge>() {
            /**
             * As {@link HostEdge}s test equality by object identity,
             * we need to weaken the set's equality test.
             */
            @Override
            final protected boolean areEqual(PatternEdge o1, PatternEdge o2) {
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

    /** Puts an edge in the store and returns its canonical representative. */
    public PatternEdge createEdge(PatternNode source, TypeEdge type,
            PatternNode target) {
        assert source != null : "Source node of pattern edge should not be null";
        assert target != null : "Target node of pattern edge should not be null";
        PatternEdge edge = newEdge(source, type, target, getEdgeCount());
        return storeEdge(edge);
    }

    private PatternEdge newEdge(PatternNode source, TypeEdge type,
            PatternNode target, int nr) {
        return new PatternEdge(nr, source, target, type);
    }

    /** Puts an edge in the store and returns its canonical representative. */
    private PatternEdge storeEdge(PatternEdge edge) {
        PatternEdge result = this.edges.put(edge);
        if (result == null) {
            result = edge;
        }
        return result;
    }

    // ------------------------------------------------------------------------
    // Unsupported methods
    // ------------------------------------------------------------------------

    @Override
    public PatternNode createNode(int nr) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Label createLabel(String text) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PatternEdge createEdge(PatternNode source, String text,
            PatternNode target) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PatternEdge createEdge(PatternNode source, Label label,
            PatternNode target) {
        throw new UnsupportedOperationException();
    }

}
