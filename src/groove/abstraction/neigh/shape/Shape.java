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
import groove.abstraction.neigh.Multiplicity;
import groove.abstraction.neigh.Multiplicity.EdgeMultDir;
import groove.abstraction.neigh.Multiplicity.MultKind;
import groove.abstraction.neigh.MyHashMap;
import groove.abstraction.neigh.MyHashSet;
import groove.abstraction.neigh.Parameters;
import groove.abstraction.neigh.Util;
import groove.abstraction.neigh.equiv.EquivClass;
import groove.abstraction.neigh.equiv.EquivRelation;
import groove.abstraction.neigh.equiv.GraphNeighEquiv;
import groove.abstraction.neigh.equiv.ShapeNeighEquiv;
import groove.abstraction.neigh.trans.Materialisation;
import groove.abstraction.neigh.trans.RuleToShapeMap;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.TypeLabel;
import groove.trans.DefaultHostGraph;
import groove.trans.HostEdge;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.trans.RuleEdge;
import groove.trans.RuleNode;
import groove.util.Duo;

import java.util.Collection;
import java.util.Map;
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
    private final Map<ShapeNode,Multiplicity> nodeMultMap;
    /**
     * The outgoing edge multiplicity map.
     */
    private final Map<EdgeSignature,Multiplicity> outEdgeMultMap;
    /**
     * The incoming edge multiplicity map.
     */
    private final Map<EdgeSignature,Multiplicity> inEdgeMultMap;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Private default constructor. Creates an empty shape. */
    private Shape(ShapeFactory factory) {
        super("shape", factory);
        this.equivRel = new EquivRelation<ShapeNode>();
        this.nodeMultMap = new MyHashMap<ShapeNode,Multiplicity>();
        this.outEdgeMultMap = new MyHashMap<EdgeSignature,Multiplicity>();
        this.inEdgeMultMap = new MyHashMap<EdgeSignature,Multiplicity>();
    }

    /** Copying constructor. Used in cloning. */
    private Shape(Shape shape) {
        super(shape);
        // Clone the equivalence relation. A deep copy is used.
        this.equivRel = shape.equivRel.clone();
        // Clone the multiplicity maps. A shallow copy is sufficient.
        this.nodeMultMap =
            ((MyHashMap<ShapeNode,Multiplicity>) shape.nodeMultMap).clone();
        this.outEdgeMultMap =
            ((MyHashMap<EdgeSignature,Multiplicity>) shape.outEdgeMultMap).clone();
        this.inEdgeMultMap =
            ((MyHashMap<EdgeSignature,Multiplicity>) shape.inEdgeMultMap).clone();
    }

    // ------------------------------------------------------------------------
    // Static Methods
    // ------------------------------------------------------------------------

    /** Creates a shape from the given graph. */
    public static Shape createShape(HostGraph graph) {
        Shape shape = new Shape(ShapeFactory.newInstance());
        HostToShapeMap map = new HostToShapeMap(shape.getFactory());
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

    /** Deep copy of all shape structures. */
    @Override
    public Shape clone() {
        Shape shape = new Shape(this);
        return shape;
    }

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
    public ShapeEdge createEdge(HostNode source, Label label, HostNode target) {
        return (ShapeEdge) super.createEdge(source, label, target);
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
        for (ShapeEdge e : this.binaryEdgeSet()) {
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
            Multiplicity one = Multiplicity.getMultiplicity(1, 1, EDGE_MULT);
            for (EdgeMultDir direction : EdgeMultDir.values()) {
                EdgeSignature es = this.getEdgeSignature(edgeS, direction);
                Multiplicity mult = this.getEdgeSigMult(es);
                if (mult.isZero()) {
                    this.setEdgeSigMult(es, one);
                }
            }
        }
        return added;
    }

    /** Removes the node from the shape and updates all related structures. */
    @Override
    public boolean removeNode(HostNode node) {
        assert !this.isFixed();
        assert this.nodeSet().contains(node);

        ShapeNode nodeS = (ShapeNode) node;

        // Remove all edges that are incident to this node.
        Set<ShapeEdge> toRemove = new MyHashSet<ShapeEdge>();
        for (ShapeEdge edgeS : this.edgeSet(nodeS)) {
            toRemove.add(edgeS);
        }
        for (ShapeEdge edgeToRemove : toRemove) {
            this.removeEdge(edgeToRemove);
        }

        // Update the equivalence relation.
        EquivClass<ShapeNode> ec = this.getEquivClassOf(nodeS);
        if (ec.isSingleton()) {
            // Remove singleton equivalence class from the relation.
            this.equivRel.remove(ec);
        } else {
            // Remove node from equivalence class.
            // Equivalence classes are fixed, so we have to clone.
            EquivClass<ShapeNode> newEc = ec.clone();
            newEc.remove(nodeS);
            // Update all structures that used the old equivalence class class.
            this.replaceEc(ec, newEc);
        }

        // Remove entry from node multiplicity map.
        this.nodeMultMap.remove(nodeS);
        // Remove node from graph.
        return super.removeNodeWithoutCheck(node);
    }

    @Override
    public boolean removeNodeSetWithoutCheck(
            Collection<? extends HostNode> nodeSet) {
        assert !this.isFixed();
        boolean removed = false;
        for (HostNode node : nodeSet) {
            removed |= this.removeNode(node);
        }
        return removed;
    }

    /** Removes the edge from the shape and updates all related structures. */
    @Override
    public boolean removeEdge(HostEdge edge) {
        assert !this.isFixed();
        assert edge instanceof ShapeEdge;
        ShapeEdge edgeS = (ShapeEdge) edge;
        if (edgeS.getRole() == BINARY) {
            for (EdgeMultDir direction : EdgeMultDir.values()) {
                EdgeSignature es = this.getEdgeSignature(edgeS, direction);
                if (es.getEquivClass().isSingleton()
                    || this.isEdgeSigUnique(es)) {
                    // Update multiplicity map.
                    this.getEdgeMultMap(direction).remove(es);
                }
            }
        }
        // Remove edge from graph.
        return super.removeEdge(edgeS);
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

    /** Creates an edge accordingly to the given direction. */
    public ShapeEdge createEdge(HostNode node0, HostNode node1, Label label,
            EdgeMultDir direction) {
        switch (direction) {
        case OUTGOING:
            return this.createEdge(node0, label, node1);
        case INCOMING:
            return this.createEdge(node1, label, node0);
        default:
            assert false;
            return null;
        }
    }

    /** Returns the set of binary edges of this shape. */
    private Set<ShapeEdge> binaryEdgeSet() {
        Set<ShapeEdge> result = new MyHashSet<ShapeEdge>();
        for (ShapeEdge edge : this.edgeSet()) {
            if (edge.getRole() == BINARY) {
                result.add(edge);
            }
        }
        return result;
    }

    /** Returns the set of binary edges with the given node as source. */
    private Set<ShapeEdge> outBinaryEdgeSet(ShapeNode source) {
        Set<ShapeEdge> result = new MyHashSet<ShapeEdge>();
        for (ShapeEdge edge : this.outEdgeSet(source)) {
            if (edge.getRole() == BINARY) {
                result.add(edge);
            }
        }
        return result;
    }

    /** Returns the set of binary edges with the given node as target. */
    private Set<ShapeEdge> inBinaryEdgeSet(ShapeNode target) {
        Set<ShapeEdge> result = new MyHashSet<ShapeEdge>();
        for (ShapeEdge edge : this.inEdgeSet(target)) {
            if (edge.getRole() == BINARY) {
                result.add(edge);
            }
        }
        return result;
    }

    /**
     * Returns the set of binary edges with the given node as source or target
     * accordingly to the given direction.
     */
    public Set<ShapeEdge> binaryEdgeSet(ShapeNode node, EdgeMultDir direction) {
        Set<ShapeEdge> result = null;
        switch (direction) {
        case OUTGOING:
            result = this.outBinaryEdgeSet(node);
            break;
        case INCOMING:
            result = this.inBinaryEdgeSet(node);
            break;
        default:
            assert false;
        }
        return result;
    }

    /** Ugly hack to circumvent typing problems. */
    @SuppressWarnings({"cast", "unchecked", "rawtypes"})
    public Graph<ShapeNode,ShapeEdge> downcast() {
        return (Graph<ShapeNode,ShapeEdge>) ((Graph) this);
    }

    /** Ugly hack to circumvent typing problems. */
    @SuppressWarnings({"rawtypes"})
    public static Shape upcast(Graph<ShapeNode,ShapeEdge> shape) {
        return (Shape) ((Graph) shape);
    }

    /**
     * Creates nodes in the shape based on the equivalence relation given.
     * Used when creating a shape from a host graph. 
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
     * Creates nodes in the shape based on the equivalence relation given.
     * Used when creating a shape from a shape, i.e., during normalisation. 
     */
    private void createShapeNodes(ShapeNeighEquiv sne, HostToShapeMap map,
            Shape origShape) {
        assert !this.isFixed();
        // Each node of the shape correspond to an equivalence class.
        for (EquivClass<HostNode> ec : sne) {
            // We are building a shape from another shape so we can re-use
            // nodes. Since shape morphisms are non-injective w.r.t. the
            // equivalence relation, we can pick an arbitrary node from
            // the equivalence class to be the representative of the class
            // in the new shape we are creating.
            ShapeNode nodeS = (ShapeNode) ec.iterator().next();
            // Add a shape node to the shape.
            // Call the super method because we have additional information on
            // the node to be added.
            super.addNode(nodeS);
            // Fill the shape node multiplicity.
            Multiplicity mult = origShape.getNodeSetMultSum(ec.downcast());
            this.setNodeMult(nodeS, mult);
            // Update the shape morphism.
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
            ShapeEdge edgeS = this.createEdge(srcS, labelS, tgtS);
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
        for (ShapeEdge edgeS : this.binaryEdgeSet()) {
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
                this.setEdgeSigMult(es, mult);
            }
        }
    }

    /**
     * Creates the edge multiplicity maps from a shape neighbourhood relation.
     * See item 6 of Def. 22 on page 17 of the Technical Report.
     */
    private void createEdgeMultMaps(ShapeNeighEquiv currGraphNeighEquiv,
            HostToShapeMap map, Shape origShape) {
        assert !this.isFixed();
        // For all binary edges of the shape. (T)
        for (ShapeEdge edgeT : this.binaryEdgeSet()) {
            // For outgoing and incoming maps.
            for (EdgeMultDir direction : EdgeMultDir.values()) {
                // Get the edge signature.
                EdgeSignature esT = this.getEdgeSignature(edgeT, direction);
                // Get an arbitrary node of the original shape (S) that was
                // mapped to the node of the edge signature of T.
                ShapeNode nodeS =
                    (ShapeNode) map.getPreImages(esT.getNode()).iterator().next();
                // Get the reverse map of ecT from the shape morphism.
                EquivClass<HostNode> ecTonS =
                    map.getPreImages(esT.getEquivClass());
                // Compute the set of equivalence classes from the original
                // shape that we need to consider.
                EquivRelation<ShapeNode> kSet = new EquivRelation<ShapeNode>();
                for (EquivClass<ShapeNode> possibleK : origShape.getEquivRelation()) {
                    if (ecTonS.containsAll(possibleK)) {
                        kSet.add(possibleK);
                    }
                }
                // Compute the bounded multiplicity sum.
                Multiplicity mult =
                    ShapeNeighEquiv.getEdgeSetMult(origShape, nodeS,
                        esT.getLabel(), kSet, direction);
                // Store the multiplicity in the proper multiplicity map.
                this.setEdgeSigMult(esT, mult);
            }
        }
    }

    /** Returns the proper multiplicity map accordingly to the given direction. */
    private Map<EdgeSignature,Multiplicity> getEdgeMultMap(EdgeMultDir direction) {
        Map<EdgeSignature,Multiplicity> result = null;
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
    public Set<EdgeSignature> getEdgeMultMapKeys(EdgeMultDir direction) {
        return this.getEdgeMultMap(direction).keySet();
    }

    /** Basic getter method. */
    public EquivRelation<ShapeNode> getEquivRelation() {
        return this.equivRel;
    }

    /** Basic getter method. */
    private ShapeEdge getShapeEdge(ShapeNode source, TypeLabel label,
            ShapeNode target) {
        ShapeEdge result = null;
        for (ShapeEdge edge : this.outEdgeSet(source)) {
            if (edge.label().equals(label) && edge.target().equals(target)) {
                result = edge;
                break;
            }
        }
        return result;
    }

    /** Basic getter method. */
    public ShapeEdge getShapeEdge(ShapeNode node0, ShapeNode node1,
            TypeLabel label, EdgeMultDir direction) {
        switch (direction) {
        case OUTGOING:
            return this.getShapeEdge(node0, label, node1);
        case INCOMING:
            return this.getShapeEdge(node1, label, node0);
        default:
            assert false;
            return null;
        }
    }

    /**
     * Looks at the keys of the edge multiplicity maps for an edge signature
     * that contains the given edge. If no suitable edge signature is found, a
     * new one is created and returned, but it is not stored.
     */
    public EdgeSignature getEdgeSignature(ShapeEdge edge, EdgeMultDir direction) {
        EdgeSignature result = null;
        for (EdgeSignature es : this.getEdgeMultMapKeys(direction)) {
            if (es.contains(edge)) {
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
            result = new EdgeSignature(direction, node, label, ec);
        }
        return result;
    }

    /**
     * Looks at the keys of the edge multiplicity maps for an edge signature
     * with the given fields. If no suitable edge signature is found, a new
     * one is created and returned, but it is not stored.
     */
    public EdgeSignature getEdgeSignature(EdgeMultDir direction,
            ShapeNode node, TypeLabel label, EquivClass<ShapeNode> ec) {
        EdgeSignature result = null;
        for (EdgeSignature es : this.getEdgeMultMapKeys(direction)) {
            if (es.getNode().equals(node) && es.getLabel().equals(label)
                && es.getEquivClass().equals(ec)) {
                result = es;
                break;
            }
        }
        if (result == null) {
            result = new EdgeSignature(direction, node, label, ec);
        }
        return result;
    }

    /**
     * Sets the node multiplicity. If the multiplicity given is zero, then
     * the node is removed from the shape.
     */
    public void setNodeMult(ShapeNode node, Multiplicity mult) {
        assert !this.isFixed();
        assert mult.isNodeKind();
        assert this.containsNode(node) : "Node " + node
            + " is not in the shape!";
        if (!mult.isZero()) {
            this.nodeMultMap.put(node, mult);
        } else {
            // Setting a node multiplicity to zero is equivalent to removing
            // the node from the shape.
            this.removeNode(node);
        }
    }

    /** Sets the edge multiplicity. */
    public void setEdgeMult(ShapeEdge edge, EdgeMultDir direction,
            Multiplicity mult) {
        assert !this.isFixed();
        EdgeSignature es = this.getEdgeSignature(edge, direction);
        this.setEdgeSigMult(es, mult);
    }

    /** Sets the edge signature multiplicity. */
    public void setEdgeSigMult(EdgeSignature es, Multiplicity mult) {
        assert !this.isFixed();
        assert mult.isEdgeKind();
        EdgeMultDir direction = es.getDirection();
        if (!mult.isZero()) {
            this.getEdgeMultMap(direction).put(es, mult);
        } else {
            // Setting a multiplicity to zero is equivalent to
            // removing all edges in the signature from the shape.
            for (ShapeEdge edge : this.getEdgesFromSig(es)) {
                this.removeEdge(edge);
            }
            this.getEdgeMultMap(direction).remove(es);
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

    /**
     * Returns the bounded sum of the node multiplicities of the given set.
     */
    public Multiplicity getNodeSetMultSum(Set<ShapeNode> nodes) {
        Multiplicity accumulator =
            Multiplicity.getMultiplicity(0, 0, MultKind.NODE_MULT);
        for (ShapeNode node : nodes) {
            Multiplicity nodeMult = this.getNodeMult(node);
            accumulator = accumulator.add(nodeMult);
        }
        return accumulator;
    }

    /** Basic getter method. */
    public Multiplicity getEdgeMult(ShapeEdge edge, EdgeMultDir direction) {
        EdgeSignature es = this.getEdgeSignature(edge, direction);
        Multiplicity mult = this.getEdgeSigMult(es);
        return mult;
    }

    /** Basic getter method. */
    public Multiplicity getEdgeSigMult(EdgeSignature es) {
        Multiplicity mult = this.getEdgeMultMap(es.getDirection()).get(es);
        if (mult == null) {
            mult = Multiplicity.getMultiplicity(0, 0, EDGE_MULT);
        }
        return mult;
    }

    /**
     * Returns the bounded sum of the edge multiplicities of the given set.
     */
    public Multiplicity getEdgeSigSetMultSum(Set<EdgeSignature> esS) {
        Multiplicity accumulator =
            Multiplicity.getMultiplicity(0, 0, MultKind.EDGE_MULT);
        for (EdgeSignature es : esS) {
            Multiplicity edgeMult = this.getEdgeSigMult(es);
            accumulator = accumulator.add(edgeMult);
        }
        return accumulator;
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
     * Returns a pair of strings representing the multiplicity labels that
     * should be used in a graphical representation.
     */
    public Duo<String> getEdgeMultLabels(ShapeEdge edge) {
        Duo<String> result = new Duo<String>("", "");
        for (EdgeMultDir direction : EdgeMultDir.values()) {
            EdgeSignature es = this.getEdgeSignature(edge, direction);
            if (es.getEquivClass().isSingleton() || this.isEdgeSigUnique(es)
                || edge.equals(this.getMinimumEdgeFromSig(es))) {
                String multStr = this.getEdgeSigMult(es).toString();
                switch (direction) {
                case OUTGOING:
                    result.setTwo(multStr);
                    break;
                case INCOMING:
                    result.setOne(multStr);
                    break;
                default:
                    assert false;
                }
            }
        }
        return result;
    }

    /** Returns true if the given signature occurs in the multiplicity map. */
    public boolean hasEdgeSignature(EdgeSignature es) {
        return this.getEdgeMultMapKeys(es.getDirection()).contains(es);
    }

    /**
     * Returns true if the number of edges from the signature
     * occurring in the shape is one.
     */
    public boolean isEdgeSigUnique(EdgeSignature es) {
        return this.getEdgesFromSig(es).size() == 1;
    }

    private ShapeEdge getMinimumEdgeFromSig(EdgeSignature es) {
        ShapeEdge result = null;
        ShapeNode resultOpposite = null;
        ShapeNode node = es.getNode();
        TypeLabel label = es.getLabel();
        EdgeMultDir direction = es.getDirection();
        for (ShapeNode ecNode : es.getEquivClass()) {
            ShapeEdge edge = this.getShapeEdge(node, ecNode, label, direction);
            if (edge != null && !edge.isLoop()) {
                if (resultOpposite == null
                    || ecNode.getNumber() < resultOpposite.getNumber()) {
                    result = edge;
                    resultOpposite = ecNode;
                }
            }
        }
        return result;
    }

    /**
     * Returns the set of edges from the signature occurring in the shape.
     */
    public Set<ShapeEdge> getEdgesFromSig(EdgeSignature es) {
        Set<ShapeEdge> result = new MyHashSet<ShapeEdge>();
        ShapeNode node = es.getNode();
        TypeLabel label = es.getLabel();
        EdgeMultDir direction = es.getDirection();
        for (ShapeNode ecNode : es.getEquivClass()) {
            ShapeEdge edge = this.getShapeEdge(node, ecNode, label, direction);
            if (edge != null) {
                result.add(edge);
            }
        }
        return result;
    }

    /**
     * Materialises the given collector node in this shape according to the
     * given materialisation.
     * The number of new nodes created from the collector is determined by
     * the number of rule nodes that were mapped to the collector.
     * All new nodes get multiplicity one and are put in a singleton equivalence
     * class.
     * In addition, the rule match and the shape morphism are properly
     * adjusted. 
     */
    public void materialiseNode(Materialisation mat, ShapeNode collectorNode) {
        assert !this.isFixed();
        assert this.containsNode(collectorNode);
        assert mat.getStage() == 1;

        // The current match to be updated.
        RuleToShapeMap match = mat.getMatch();
        // Check the nodes on the rule that were mapped to nodeS.
        Set<RuleNode> nodesR =
            mat.getOriginalMatch().getPreImages(collectorNode);
        assert !nodesR.isEmpty();
        // Compute how many copies of the node we need to materialise.
        int copies = nodesR.size();
        Multiplicity one = Multiplicity.getMultiplicity(1, 1, NODE_MULT);

        // Create a new shape node for each rule node.
        for (RuleNode nodeR : nodesR) {
            ShapeNode newNode = this.getFactory().createNode();
            // Add the new node to the shape. Call the super method because
            // we have additional information on the node to be added.
            super.addNode(newNode);
            // The new node is concrete so set its multiplicity to one.
            this.setNodeMult(newNode, one);
            // Copy the labels from the original node.
            this.copyUnaryEdges(collectorNode, newNode, nodeR, match);
            // Add the new node to a new equivalence class.
            this.addToNewEquivClass(newNode);
            // Add the new node to the materialisation.
            mat.addMatNode(newNode, collectorNode, nodeR);
        }

        // Adjust the multiplicity of the original node.
        Multiplicity oldMult = this.getNodeMult(collectorNode);
        Multiplicity newMult = oldMult.sub(Multiplicity.scale(one, copies));
        this.setNodeMult(collectorNode, newMult);

        // Final update of the materialisation regarding the collector node.
        mat.handleCollectorNode(collectorNode);
    }

    /**
     * Materialises the given collector edge in this shape according to the
     * given materialisation.
     * We use the rule match to determine the new edges that have to be created.
     * The multiplicities of the original collector edge are properly adjusted
     * and so is the rule match. 
     */
    public void materialiseEdge(Materialisation mat, ShapeEdge inconsistentEdge) {
        assert !this.isFixed();
        assert mat.getStage() == 1;
        // All information for inconsistentEdge must come from the
        // original shape because the edge may no longer be present in the
        // current shape. This is the case, for example, when the node
        // materialisation process leaves a collector node with multiplicity
        // zero, thus removing the node from the shape.
        assert mat.getOriginalShape().containsEdge(inconsistentEdge);

        // The current match to be updated.
        RuleToShapeMap match = mat.getMatch();
        // Check the edges on the rule that were mapped to edgeS.
        Set<RuleEdge> edgesR =
            mat.getOriginalMatch().getPreImages(inconsistentEdge);
        assert !edgesR.isEmpty();
        TypeLabel label = inconsistentEdge.label();

        for (RuleEdge edgeR : edgesR) {
            // Get the image of source and target from the match.
            ShapeNode srcS = match.getNode(edgeR.source());
            ShapeNode tgtS = match.getNode(edgeR.target());
            ShapeEdge newEdge = this.createEdge(srcS, label, tgtS);
            // Add the new edge to the shape. The edge multiplicity maps are
            // properly adjusted.
            this.addEdgeWithoutCheck(newEdge);
            // Add the new edge to the materialisation.
            mat.addMatEdge(newEdge, inconsistentEdge, edgeR);
        }

        // Final update of the materialisation regarding the inconsistent edge.
        mat.handleInconsistentEdge(inconsistentEdge);
    }

    /**
     * Puts the given node in a singleton equivalence class. In order to do so
     * it is necessary for all edges incident to the node to be concrete. This
     * means that this method can only be called after all nodes and edges
     * have already been materialised.
     * Since all new materialised nodes are already put in a singleton class,
     * this method is only useful when there is a concrete shape node that
     * was matched by a rule node and the shape node is not already in a
     * singleton class.
     */
    public void singulariseNode(Materialisation mat, ShapeNode nodeS) {
        assert !this.isFixed();
        assert this.containsNode(nodeS);
        assert mat.getStage() == 1;

        // Check if the node is not already in a singleton equivalence class.
        if (this.getEquivClassOf(nodeS).isSingleton()) {
            return;
        }

        // First make sure we are not deleting any edges with this operation.
        this.handleCrossingEdges(mat, nodeS);

        // The original equivalence class to be split.
        EquivClass<ShapeNode> origEc = this.getEquivClassOf(nodeS);
        // The remaining equivalence class after singularisation.
        EquivClass<ShapeNode> remEc = origEc.clone();
        remEc.remove(nodeS);
        // The singular equivalence class created by the operation.
        EquivClass<ShapeNode> singEc = new EquivClass<ShapeNode>();
        singEc.add(nodeS);
        // Update the multiplicities of the new singleton class.
        this.addNewSingletonEc(origEc, singEc);
        // Replace the original equivalence class with the remainder of the
        // split.
        this.replaceEc(origEc, remEc);
    }

    /**
     * Removes all edges from the shape that cross equivalence classes and that
     * will be affected during the materialisation process. Those edges are
     * stored as possible edges in the materialisation object.
     */
    private void handleCrossingEdges(Materialisation mat, ShapeNode nodeS) {
        Set<ShapeEdge> possibleEdges = new MyHashSet<ShapeEdge>();
        for (EdgeSignature es : this.getEdgeSignatures(this.getEquivClassOf(nodeS))) {
            if (es.isSelfReferencing()) {
                continue;
            }
            for (ShapeEdge edgeS : this.getEdgesFromSig(es)) {
                if (!this.isEdgeConcrete(edgeS)) {
                    // We have an edge that is not concrete.
                    // Add it to the list of possible edges.
                    possibleEdges.add(edgeS);
                }
            }
        }
        for (ShapeEdge edgeS : this.edgeSet(nodeS)) {
            if (edgeS.getRole() != BINARY) {
                continue;
            }
            if (!this.isEdgeConcrete(edgeS)) {
                // We have an edge that is not concrete.
                // Add it to the list of possible edges.
                possibleEdges.add(edgeS);
            }
        }
        // Now remove all possible edges from the shape.
        for (ShapeEdge possibleEdge : possibleEdges) {
            this.removeEdge(possibleEdge);
            mat.addPossibleEdge(possibleEdge, possibleEdge, true);
        }
    }

    /**
     * Splits the given shape node into the given number of copies. The original
     * node is kept and counts as one of the copies. All the structures of the
     * shape are properly updated but note that:
     * - The new split nodes are not assigned a multiplicity at this point.
     * - The new split nodes are unconnected, i.e., the incident binary edges
     *   are not duplicated.
     */
    public void splitNode(Materialisation mat, ShapeNode nodeS, int copies) {
        assert !this.isFixed();
        assert this.containsNode(nodeS);
        assert mat.getStage() == 2;

        EquivClass<ShapeNode> oldEc = this.getEquivClassOf(nodeS);
        EquivClass<ShapeNode> newEc = oldEc.clone();
        for (int i = 0; i < copies - 1; i++) {
            // Create a new shape node.
            ShapeNode newNode = getFactory().createNode();
            // Add the new node to the shape. Call the super method because
            // we have additional information on the node to be added.
            super.addNode(newNode);
            // Copy the labels from the pulled node.
            this.copyUnaryEdges(nodeS, newNode, null, null);
            newEc.add(newNode);
            mat.addSplitNode(newNode, nodeS);
        }
        this.replaceEc(oldEc, newEc);
    }

    /** Duplicate all unary edges occurring in the given 'from' node. */
    private void copyUnaryEdges(ShapeNode from, ShapeNode to, RuleNode nodeR,
            RuleToShapeMap match) {
        assert !this.isFixed();
        for (ShapeEdge edge : this.outEdgeSet(from)) {
            if (edge.getRole() != BINARY) {
                TypeLabel label = edge.label();
                ShapeEdge edgeS = (ShapeEdge) this.addEdge(to, label, to);
                if (match != null && nodeR != null) {
                    RuleEdge edgeR = match.getSelfEdge(nodeR, label);
                    if (edgeR != null) {
                        match.putEdge(edgeR, edgeS);
                    }
                }
            }
        }
    }

    private Set<EdgeSignature> getEdgeSignatures(EquivClass<ShapeNode> ec) {
        Set<EdgeSignature> result = new MyHashSet<EdgeSignature>();
        for (EdgeMultDir direction : EdgeMultDir.values()) {
            for (EdgeSignature es : this.getEdgeMultMapKeys(direction)) {
                if (es.hasEquivClass(ec)) {
                    result.add(es);
                }
            }
        }
        return result;
    }

    private void addNewSingletonEc(EquivClass<ShapeNode> oldEc,
            EquivClass<ShapeNode> newEc) {
        // Update all maps that use edge signatures that contained the old
        // equivalence class.
        for (EdgeSignature oldEs : this.getEdgeSignatures(oldEc)) {
            EdgeSignature newEs =
                this.getEdgeSignature(oldEs.getDirection(), oldEs.getNode(),
                    oldEs.getLabel(), newEc);
            Multiplicity mult = this.getEdgeSigMult(oldEs);
            if (!mult.isZero()) {
                if (this.getEdgesFromSig(newEs).size() > 0) {
                    this.setEdgeSigMult(newEs, mult);
                }
            }
        }
        // Update the equivalence relation.
        this.equivRel.add(newEc);
    }

    private void replaceEc(EquivClass<ShapeNode> oldEc,
            EquivClass<ShapeNode> newEc) {
        // Update all maps that use edge signatures that contained the old
        // equivalence class.
        for (EdgeSignature oldEs : this.getEdgeSignatures(oldEc)) {
            EdgeMultDir direction = oldEs.getDirection();
            EdgeSignature newEs =
                this.getEdgeSignature(direction, oldEs.getNode(),
                    oldEs.getLabel(), newEc);
            Multiplicity mult = this.getEdgeSigMult(oldEs);
            if (!mult.isZero()) {
                this.getEdgeMultMap(direction).remove(oldEs);
                if (this.getEdgesFromSig(newEs).size() > 0) {
                    this.setEdgeSigMult(newEs, mult);
                }
            }
        }
        // Update the equivalence relation.
        this.equivRel.remove(oldEc);
        this.equivRel.add(newEc);
    }

    /** Returns true if both end points of the edge are concrete. */
    public boolean isEdgeConcrete(ShapeEdge edge) {
        return this.isEdgeConcrete(edge, OUTGOING)
            && this.isEdgeConcrete(edge, INCOMING);
    }

    /**
     * Returns true if the edge signature for the given edge and direction is
     * concrete.
     */
    private boolean isEdgeConcrete(ShapeEdge edge, EdgeMultDir direction) {
        EdgeSignature es = this.getEdgeSignature(edge, direction);
        return this.isEdgeSigConcrete(es);
    }

    /**
     * Returns true if the given signature has multiplicity one and is unique,
     * i.e., has only one edge.
     */
    public boolean isEdgeSigConcrete(EdgeSignature es) {
        return this.getEdgeSigMult(es).isOne() && this.isEdgeSigUnique(es);
    }

    /**
     * Returns true if both source and target of given edge have multiplicity
     * one. 
     */
    public boolean areNodesConcrete(ShapeEdge edge) {
        return this.getNodeMult(edge.source()).isOne()
            && this.getNodeMult(edge.target()).isOne();
    }

    private boolean isEdgeUnique(ShapeEdge edge, EdgeMultDir direction) {
        EdgeSignature es = this.getEdgeSignature(edge, direction);
        return this.isEdgeSigUnique(es);
    }

    private boolean isEdgeUnique(ShapeEdge edge) {
        return this.isEdgeUnique(edge, OUTGOING)
            && this.isEdgeUnique(edge, INCOMING);
    }

    /** Normalises the shape object and returns the newly modified shape. */
    public Shape normalise() {
        Shape newShape = new Shape(this.getFactory());
        HostToShapeMap map = new HostToShapeMap(this.getFactory());
        int radius = Parameters.getAbsRadius();
        // Compute the equivalence relation on this shape.
        ShapeNeighEquiv sne = new ShapeNeighEquiv(this, radius);
        // Now build the shape.
        newShape.createShapeNodes(sne, map, this);
        newShape.createEquivRelation(sne.getPrevEquivRelation(), map);
        newShape.createShapeEdges(sne.getEdgesEquivRel(), map);
        newShape.createEdgeMultMaps(sne, map, this);

        // Optimization: making node and edges multiplicities more precise, when possible.
        Multiplicity one = Multiplicity.getMultiplicity(1, 1, NODE_MULT);
        for (ShapeNode node : newShape.nodeSet()) {
            Multiplicity nodeMult = newShape.getNodeMult(node);
            for (EdgeMultDir direction : EdgeMultDir.values()) {
                EdgeMultDir reverse = direction.reverse();
                for (ShapeEdge edge : newShape.binaryEdgeSet(node, direction)) {
                    ShapeNode opp = edge.opposite(direction);
                    Multiplicity oppMult = newShape.getNodeMult(opp);
                    if (nodeMult.isCollector() && oppMult.isOne()
                        && newShape.isEdgeConcrete(edge)) {
                        // The node can only be concrete.
                        newShape.setNodeMult(node, one);
                    } else if (nodeMult.isOne() && oppMult.isCollector()
                        && newShape.isEdgeUnique(edge)) {
                        EdgeSignature oppEs =
                            newShape.getEdgeSignature(edge, reverse);
                        if (newShape.getEdgeSigMult(oppEs).isOne()) {
                            // We can set the edge multiplicity to the opposite
                            // node multiplicity.
                            EdgeSignature es =
                                newShape.getEdgeSignature(edge, direction);
                            Multiplicity oldEsMult =
                                newShape.getEdgeSigMult(es);
                            Multiplicity newEsMult = oppMult.toEdgeKind();
                            if (oldEsMult.subsumes(newEsMult)) {
                                newShape.setEdgeSigMult(es, newEsMult);
                            }
                        }
                    }
                }
            }
        }

        assert newShape.isInvariantOK();
        return newShape;
    }

    /** Returns true if the given node has no incident binary edges. */
    public boolean isUnconnected(ShapeNode node) {
        assert this.containsNode(node);
        return this.binaryEdgeSet(node, OUTGOING).isEmpty()
            && this.binaryEdgeSet(node, INCOMING).isEmpty();
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
                            this.getEdgeSignature(OUTGOING, node, label, ec);
                        Multiplicity mult = this.getEdgeSigMult(es);
                        if (!mult.isZero()) {
                            result = false;
                            break labelLoop;
                        }
                    }
                    // Check incoming multiplicities.
                    if (Util.getIntersectEdges(this, ec, node, label).isEmpty()) {
                        EdgeSignature es =
                            this.getEdgeSignature(INCOMING, node, label, ec);
                        Multiplicity mult = this.getEdgeSigMult(es);
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
