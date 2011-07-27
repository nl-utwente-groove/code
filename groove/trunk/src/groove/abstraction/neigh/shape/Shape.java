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
package groove.abstraction.neigh.shape;

import static groove.abstraction.neigh.Multiplicity.EdgeMultDir.INCOMING;
import static groove.abstraction.neigh.Multiplicity.EdgeMultDir.OUTGOING;
import static groove.abstraction.neigh.Multiplicity.MultKind.EDGE_MULT;
import static groove.abstraction.neigh.Multiplicity.MultKind.NODE_MULT;
import static groove.graph.EdgeRole.BINARY;
import gnu.trove.THashMap;
import groove.abstraction.neigh.Multiplicity;
import groove.abstraction.neigh.Multiplicity.EdgeMultDir;
import groove.abstraction.neigh.Util;
import groove.abstraction.neigh.equiv.EquivClass;
import groove.abstraction.neigh.equiv.EquivRelation;
import groove.graph.Edge;
import groove.graph.Node;
import groove.trans.DefaultHostGraph;
import groove.trans.HostEdge;
import groove.trans.HostNode;

import java.util.Set;

/**
 * This is where the magic happens... :P
 *
 * This is the core class for the abstraction package. We assume that the
 * reader is familiar with the concepts of shape abstraction as described in 
 * the Technical Report "Graph Abstraction and Abstract Graph Transformation".
 * 
 * A shape is composed by an underlying graph structure, an equivalence
 * relation on its nodes, and multiplicity mappings for nodes, and outgoing and
 * incoming edge signatures. 
 * 
 * WARNING: Beware of the code in this class. It's rather tricky.
 * 
 * @author Eduardo Zambon
 */
public final class Shape extends DefaultHostGraph {

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    /**
     * The equivalence relation over the nodes of the shape.
     */
    private final EquivRelation<ShapeNode> equivRel;
    /**
     * The node multiplicity map.
     */
    private final THashMap<ShapeNode,Multiplicity> nodeMultMap;
    /**
     * The outgoing edge multiplicity map.
     */
    private final THashMap<EdgeSignature,Multiplicity> outEdgeMultMap;
    /**
     * The incoming edge multiplicity map.
     */
    private final THashMap<EdgeSignature,Multiplicity> inEdgeMultMap;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** EDUARDO: Todo */
    public Shape(String name) {
        super(name);
        this.equivRel = null;
        this.nodeMultMap = null;
        this.outEdgeMultMap = null;
        this.inEdgeMultMap = null;
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public Set<ShapeNode> nodeSet() {
        return (Set<ShapeNode>) super.nodeSet();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<ShapeEdge> edgeSet() {
        return (Set<ShapeEdge>) super.edgeSet();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<ShapeEdge> edgeSet(Node node) {
        return (Set<ShapeEdge>) super.edgeSet(node);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<ShapeEdge> outEdgeSet(Node node) {
        return (Set<ShapeEdge>) super.outEdgeSet(node);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<ShapeEdge> inEdgeSet(Node node) {
        return (Set<ShapeEdge>) super.inEdgeSet(node);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Nodes:\n");
        for (ShapeNode node : this.nodeSet()) {
            sb.append("  " + node + ":" + this.getNodeMult(node) + " ");
            sb.append(Util.getNodeLabels(this, node) + "\n");
        }
        sb.append("Edges:\n");
        for (ShapeEdge e : edgeSet()) {
            if (e.getRole() != BINARY) {
                continue;
            }
            sb.append("  " + this.getEdgeMult(e, OUTGOING) + ":" + e + ":"
                + this.getEdgeMult(e, INCOMING) + "\n");
        }
        return sb.toString();
    }

    private String getEdgeMult(ShapeEdge e, EdgeMultDir outgoing) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * WARNING! Be very careful with this method!
     * Since we are overriding it, we cannot add additional parameters.
     * Therefore, we have no information to update the shape. Thus, we have
     * to resort to a default behaviour, that is:
     * - The node gets multiplicity one;
     * - The node gets its own new equivalence class.
     * If you want to avoid the method side-effects and perform the book-
     * keeping yourself, then call super.addNode(Node) directly. In this case,
     * be careful not to leave the shape structures in an inconsistent state.
     */
    @Override
    public boolean addNode(HostNode node) {
        assert !this.isFixed();
        assert node instanceof ShapeNode;
        boolean added = super.addNode(node);
        if (added) {
            ShapeNode nodeS = (ShapeNode) node;
            this.setNodeMult(nodeS,
                Multiplicity.getMultiplicity(1, 1, NODE_MULT));
            this.addToNewEquivClass(nodeS);
        }
        return added;
    }

    /**
     * Adds the given edge to the shape and properly adjust the multiplicities
     * when necessary.
     */
    @Override
    public boolean addEdgeWithoutCheck(HostEdge edge) {
        assert !this.isFixed();
        assert edge instanceof ShapeEdge;

        boolean added = super.addEdgeWithoutCheck(edge);
        if (added && edge.getRole() == BINARY) {
            ShapeEdge edgeS = (ShapeEdge) edge;
            Multiplicity zero = Multiplicity.getMultiplicity(0, 0, EDGE_MULT);
            Multiplicity one = Multiplicity.getMultiplicity(1, 1, EDGE_MULT);

            for (EdgeMultDir direction : EdgeMultDir.values()) {
                EdgeSignature es = this.getEdgeSignature(edgeS, direction);
                Multiplicity mult = this.getEdgeSigMult(es, direction);
                if (mult.equals(zero)) {
                    this.setEdgeSigMult(es, direction, one);
                }
            }
        }
        return added;
    }

    private EdgeSignature getEdgeSignature(ShapeEdge edgeS,
            EdgeMultDir direction) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ShapeFactory getFactory() {
        return (ShapeFactory) super.getFactory();
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** Returns the proper multiplicity map accordingly to the given direction. */
    private THashMap<EdgeSignature,Multiplicity> getEdgeMultMap(
            EdgeMultDir direction) {
        THashMap<EdgeSignature,Multiplicity> result = null;
        switch (direction) {
        case OUTGOING:
            result = this.outEdgeMultMap;
            break;
        case INCOMING:
            result = this.inEdgeMultMap;
            break;
        default:
            assert false;
        }
        return result;
    }

    /** Basic getter method. */
    public EquivRelation<ShapeNode> getEquivRelation() {
        return this.equivRel;
    }

    /**
     * Sets the node multiplicity. If the multiplicity given is zero, then
     * the node is removed from the shape.
     */
    private void setNodeMult(ShapeNode node, Multiplicity mult) {
        assert !this.isFixed();
        assert this.nodeSet().contains(node) : "Node " + node
            + " is not in the shape!";
        if (mult.isPositive()) {
            this.nodeMultMap.put(node, mult);
        } else {
            // Setting a node multiplicity to zero is equivalent to removing
            // the node from the shape.
            this.removeNode(node);
        }
    }

    /** Sets the edge signature multiplicity. */
    private void setEdgeSigMult(EdgeSignature es, EdgeMultDir direction,
            Multiplicity mult) {
        assert !this.isFixed();
        if (mult.isPositive()) {
            this.getEdgeMultMap(direction).put(es, mult);
        } else {
            // Setting a multiplicity to zero is equivalent to
            // removing all edges in the signature from the shape.
            // EDUARDO : Check this.
        }
    }

    /** Basic getter method. */
    public Multiplicity getNodeMult(ShapeNode node) {
        Multiplicity mult = this.nodeMultMap.get(node);
        if (mult == null) {
            mult = Multiplicity.getMultiplicity(0, 0, NODE_MULT);
        }
        return mult;
    }

    /** Creates a new equivalence class and adds the given node in it. */
    private EquivClass<ShapeNode> addToNewEquivClass(ShapeNode node) {
        assert !this.isFixed();
        EquivClass<ShapeNode> newEc = new EquivClass<ShapeNode>();
        newEc.add(node);
        this.equivRel.add(newEc);
        return newEc;
    }

    @Override
    protected boolean isTypeCorrect(Node node) {
        return node instanceof ShapeNode;
    }

    @Override
    protected boolean isTypeCorrect(Edge<?> edge) {
        return edge instanceof ShapeEdge;
    }

    public Multiplicity getEdgeSigMult(EdgeSignature es, EdgeMultDir direction) {
        // TODO Auto-generated method stub
        return null;
    }

}
