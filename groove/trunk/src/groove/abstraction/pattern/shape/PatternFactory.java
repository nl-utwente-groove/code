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

import groove.grammar.host.HostEdge;
import groove.graph.ElementFactory;
import groove.graph.Label;
import groove.graph.Morphism;
import groove.graph.plain.PlainEdge;
import groove.util.Dispenser;
import groove.util.FreeNumberDispenser;
import groove.util.collect.TreeHashSet;

import java.util.Collection;

/**
 * Factory for pattern graph elements.
 * 
 * @author Eduardo Zambon
 */
public final class PatternFactory extends
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
     * {@link PlainEdge} to themselves. Used to ensure that edge objects are
     * reused.
     */
    private final TreeHashSet<PatternEdge> edges;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Default constructor. */
    public PatternFactory(TypeGraph typeGraph) {
        this.typeGraph = typeGraph;
        this.edges = createEdgeStore();
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

    /**
     * Creates and returns a node with a given type and the next available
     * node number.
     */
    public PatternNode createNode(TypeNode type) {
        return createNode(getNodeNrDispenser(), type);
    }

    /**
     * Creates and returns a node with the given type. Tries to re-use node
     * numbers that do not occur in the given set, while ensuring type
     * consistency.
     * @see #createNode(Dispenser, TypeNode)
     */
    public PatternNode createNode(TypeNode type,
            Collection<PatternNode> usedNodes) {
        FreeNumberDispenser dispenser = new FreeNumberDispenser(usedNodes);
        return createNode(dispenser, type);
    }

    /**
     * Creates and returns a node with the given type. Tries to re-use node
     * numbers that do not occur in the given array, while ensuring type
     * consistency.
     * @see #createNode(Dispenser, TypeNode)
     */
    public PatternNode createNode(TypeNode type, int usedNodes[]) {
        FreeNumberDispenser dispenser = new FreeNumberDispenser(usedNodes);
        return createNode(dispenser, type);
    }

    /**
     * Creates a node with given type, and a number taken from a dispenser. 
     * @param dispenser source of the number of the new node
     * @param type type of the new node
     */
    public PatternNode createNode(Dispenser dispenser, TypeNode type) {
        PatternNode result = null;
        do {
            int nr = dispenser.getNext();
            if (nr > getMaxNodeNr() || this.nodes[nr] == null) {
                // create a new node of the correct type
                result = newNode(nr, type);
                // extend the nodes array if necessary
                if (nr >= this.nodes.length) {
                    extendNodes(nr);
                }
                // store the new node
                this.nodes[nr] = result;
                // register the node number
                registerNode(result);
            } else if (this.nodes[nr].getType().equals(type)) {
                // use the existing node with this number
                result = this.nodes[nr];
            }
        } while (result == null);
        return result;
    }

    /**
     * Extends the nodes array to make more room.
     */
    protected void extendNodes(int nr) {
        int newSize =
            Math.max((int) (this.nodes.length * NODES_GROWTH_FACTOR), nr + 1);
        PatternNode newNodes[] = new PatternNode[newSize];
        System.arraycopy(this.nodes, 0, newNodes, 0, this.nodes.length);
        this.nodes = newNodes;
    }

    /** Callback factory method for a new {@link PatternNode}. */
    private PatternNode newNode(int nr, TypeNode type) {
        return new PatternNode(nr, type);
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
    protected PatternNode newNode(int nr) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Label createLabel(String text) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PatternEdge createEdge(PatternNode source, Label label,
            PatternNode target) {
        throw new UnsupportedOperationException();
    }
}
