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
import groove.abstraction.neigh.Parameters;
import groove.abstraction.neigh.Util;
import groove.abstraction.neigh.equiv.EquivClass;
import groove.abstraction.neigh.equiv.EquivRelation;
import groove.abstraction.neigh.equiv.GraphNeighEquiv;
import groove.graph.Edge;
import groove.graph.Node;
import groove.graph.TypeLabel;
import groove.trans.DefaultHostGraph;
import groove.trans.HostEdge;
import groove.trans.HostGraph;
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

    /** Private constructor. */
    private Shape() {
        super("shape", ShapeFactory.instance());
        this.equivRel = new EquivRelation<ShapeNode>();
        this.nodeMultMap = new THashMap<ShapeNode,Multiplicity>();
        this.outEdgeMultMap = new THashMap<EdgeSignature,Multiplicity>();
        this.inEdgeMultMap = new THashMap<EdgeSignature,Multiplicity>();
    }

    // ------------------------------------------------------------------------
    // Static Methods
    // ------------------------------------------------------------------------

    /** Creates a shape from the given graph. */
    public static Shape createShape(HostGraph graph) {
        return createShape(graph, new HostToShapeMap());
    }

    /** Creates a shape from the given graph. */
    public static Shape createShape(HostGraph graph, HostToShapeMap map) {
        assert map.isEmpty();
        Shape shape = new Shape();
        int radius = Parameters.getAbsRadius();
        // Compute the equivalence relation on the given graph.
        GraphNeighEquiv gne = new GraphNeighEquiv(graph, radius);
        // Now build the shape.
        shape.createShapeNodes(gne, map);
        shape.createEquivRelation(gne.getPrevEquivRelation(), map);
        shape.createShapeEdges(gne.getEdgesEquivRel(), map);
        shape.createEdgeMultMaps(gne, map, graph);
        assert shape.isInvariantOK();
        return shape;
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    protected boolean isTypeCorrect(Node node) {
        return node instanceof ShapeNode;
    }

    @Override
    protected boolean isTypeCorrect(Edge edge) {
        return edge instanceof ShapeEdge;
    }

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
        sb.append("Equiv. Relation: " + this.equivRel + "\n");
        return sb.toString();
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
            Multiplicity one = Multiplicity.getMultiplicity(1, 1, NODE_MULT);
            this.setNodeMult(nodeS, one);
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

    @Override
    public ShapeFactory getFactory() {
        return (ShapeFactory) super.getFactory();
    }

    @Override
    public void setFixed() {
        super.setFixed();
        this.equivRel.setFixed();
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /**
     * Creates nodes in the shape based on the equivalence relation given. 
     */
    private void createShapeNodes(GraphNeighEquiv gne, HostToShapeMap map) {
        assert !this.isFixed();
        // Each node of the shape correspond to an equivalence class
        // of the graph.
        for (EquivClass<HostNode> ec : gne) {
            // We are building a shape from a graph, this means that the
            // graph nodes are from a different type and therefore are
            // stored in a different node factory. Thus we have to create
            // shape nodes.
            ShapeNode nodeS = this.getFactory().createNode();
            // Add a shape node to the shape.
            // Call the super method because we have additional information on
            // the node to be added.
            super.addNode(nodeS);
            // Fill the shape node multiplicity.
            int size = ec.size();
            Multiplicity mult = Multiplicity.approx(size, size, NODE_MULT);
            this.setNodeMult(nodeS, mult);
            // Update the abstraction morphism map.
            for (HostNode node : ec) {
                map.putNode(node, nodeS);
            }
        }
    }

    /**
     * Creates the equivalence relation between shape nodes based on the
     * equivalence relation given.
     */
    private void createEquivRelation(EquivRelation<HostNode> er,
            HostToShapeMap map) {
        assert !this.isFixed();
        for (EquivClass<HostNode> ecG : er) {
            EquivClass<ShapeNode> ecS = new EquivClass<ShapeNode>();
            for (HostNode node : ecG) {
                ecS.add(map.getNode(node));
            }
            this.equivRel.add(ecS);
        }
    }

    /**
     * Creates the edges of the shape based on the equivalence relation given. 
     */
    private void createShapeEdges(EquivRelation<HostEdge> er, HostToShapeMap map) {
        assert !this.isFixed();
        // Each edge of the shape correspond to an equivalence class
        // of the graph.
        for (EquivClass<HostEdge> ecG : er) {
            // Get an arbitrary edge from the equivalence class.
            HostEdge edgeG = ecG.iterator().next();
            // Create and add a shape edge to the shape.
            HostNode srcG = edgeG.source();
            HostNode tgtG = edgeG.target();
            ShapeNode srcS = map.getNode(srcG);
            ShapeNode tgtS = map.getNode(tgtG);
            TypeLabel labelS = edgeG.label();
            ShapeEdge edgeS = (ShapeEdge) this.createEdge(srcS, labelS, tgtS);
            // Call the super method because we will set the multiplicity maps
            // later.
            super.addEdge(edgeS);
            // Update the abstraction morphism map.
            for (HostEdge eG : ecG) {
                map.putEdge(eG, edgeS);
            }
        }
    }

    /**
     * Creates the edge multiplicity maps from a graph neighbourhood relation.
     */
    private void createEdgeMultMaps(GraphNeighEquiv gne, HostToShapeMap map,
            HostGraph graph) {
        assert !this.isFixed();
        // For all binary edges of the shape.
        for (ShapeEdge edgeS : Util.getBinaryEdges(this)) {
            // For outgoing and incoming maps.
            for (EdgeMultDir direction : EdgeMultDir.values()) {
                // Get the edge signature.
                EdgeSignature es = this.getEdgeSignature(edgeS, direction);
                // Get the reverse abstraction morphism for the class.
                EquivClass<HostNode> ecG = map.getPreImages(es.getEquivClass());
                HostNode nodeG = null;
                int size = 0;
                switch (direction) {
                case OUTGOING:
                    // Get an arbitrary node of the graph that was mapped to
                    // the shape edge source.
                    nodeG = map.getPreImages(edgeS.source()).iterator().next();
                    // Compute the number of intersecting edges in the graph.
                    size =
                        Util.getIntersectEdges(graph, nodeG, ecG, edgeS.label()).size();
                    break;
                case INCOMING:
                    // Get an arbitrary node of the graph that was mapped to
                    // the shape edge target.
                    nodeG = map.getPreImages(edgeS.target()).iterator().next();
                    // Compute the number of intersecting edges in the graph.
                    size =
                        Util.getIntersectEdges(graph, ecG, nodeG, edgeS.label()).size();
                    break;
                default:
                    assert false;
                }
                // Approximate the cardinality of the set of intersecting edges.
                Multiplicity mult = Multiplicity.approx(size, size, EDGE_MULT);
                // Store the multiplicity in the proper multiplicity map.
                this.getEdgeMultMap(direction).put(es, mult);
            }
        }
    }

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
     * Looks at the keys of the edge multiplicity maps for an edge signature
     * that contains the given edge. If no suitable edge signature is found, a
     * new one is created and returned, but it is not stored.
     */
    private EdgeSignature getEdgeSignature(ShapeEdge edge, EdgeMultDir direction) {
        EdgeSignature result = null;
        for (EdgeSignature es : this.getEdgeMultMap(direction).keySet()) {
            if (es.contains(edge, direction)) {
                result = es;
                break;
            }
        }
        if (result == null) {
            ShapeNode node = null;
            TypeLabel label = edge.label();
            EquivClass<ShapeNode> ec = null;
            switch (direction) {
            case OUTGOING:
                node = edge.source();
                ec = this.getEquivClassOf(edge.target());
                break;
            case INCOMING:
                node = edge.target();
                ec = this.getEquivClassOf(edge.source());
                break;
            default:
                assert false;
            }
            result = new EdgeSignature(node, label, ec);
        }
        return result;
    }

    /**
     * Looks at the keys of the edge multiplicity maps for an edge signature
     * with the given fields. If no suitable edge signature is found, a new
     * one is created and returned, but it is not stored.
     */
    public EdgeSignature getEdgeSignature(ShapeNode node, TypeLabel label,
            EquivClass<ShapeNode> ec) {
        EdgeSignature result = null;
        dirLoop: for (EdgeMultDir direction : EdgeMultDir.values()) {
            for (EdgeSignature es : this.getEdgeMultMap(direction).keySet()) {
                if (es.getNode().equals(node) && es.getLabel().equals(label)
                    && es.getEquivClass().equals(ec)) {
                    result = es;
                    break dirLoop;
                }
            }
        }
        if (result == null) {
            result = new EdgeSignature(node, label, ec);
        }
        return result;
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
            // EDUARDO : IMPLEMENT this.
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

    /** Basic getter method. */
    public Multiplicity getEdgeMult(ShapeEdge edge, EdgeMultDir direction) {
        EdgeSignature es = this.getEdgeSignature(edge, direction);
        Multiplicity mult = this.getEdgeSigMult(es, direction);
        return mult;
    }

    /** Basic getter method. */
    public Multiplicity getEdgeSigMult(EdgeSignature es, EdgeMultDir direction) {
        Multiplicity mult = this.getEdgeMultMap(direction).get(es);
        if (mult == null) {
            mult = Multiplicity.getMultiplicity(0, 0, EDGE_MULT);
        }
        return mult;
    }

    /**
     * Returns the equivalence class of the given node. It is assumed that
     * the given node is in the shape. 
     */
    public EquivClass<ShapeNode> getEquivClassOf(ShapeNode node) {
        assert this.nodeSet().contains(node) : "Node " + node
            + " is not in the shape!";
        return this.equivRel.getEquivClassOf(node);
    }

    /** Creates a new equivalence class and adds the given node in it. */
    private EquivClass<ShapeNode> addToNewEquivClass(ShapeNode node) {
        assert !this.isFixed();
        EquivClass<ShapeNode> newEc = new EquivClass<ShapeNode>();
        newEc.add(node);
        this.equivRel.add(newEc);
        return newEc;
    }

    /**
     * Check if the shape is in a state that complies to the shape invariant.
     * See last item of Def. 7, pg. 10.
     * This check is expensive. Use it only in assertions.
     * @return true if the invariant holds, false otherwise.
     */
    public boolean isInvariantOK() {
        boolean result = true;
        // For all labels.
        labelLoop: for (TypeLabel label : Util.getBinaryLabels(this)) {
            // For all nodes in the shape.
            for (ShapeNode node : this.nodeSet()) {
                // For all equivalence classes.
                for (EquivClass<ShapeNode> ec : this.equivRel) {
                    // Check outgoing multiplicities.
                    if (Util.getIntersectEdges(this, node, ec, label).isEmpty()) {
                        EdgeSignature es =
                            this.getEdgeSignature(node, label, ec);
                        Multiplicity mult = this.getEdgeSigMult(es, OUTGOING);
                        if (!mult.isZero()) {
                            result = false;
                            break labelLoop;
                        }
                    }
                    // Check incoming multiplicities.
                    if (Util.getIntersectEdges(this, ec, node, label).isEmpty()) {
                        EdgeSignature es =
                            this.getEdgeSignature(node, label, ec);
                        Multiplicity mult = this.getEdgeSigMult(es, INCOMING);
                        if (!mult.isZero()) {
                            result = false;
                            break labelLoop;
                        }
                    }
                }
            }
        }
        return result;
    }
}
