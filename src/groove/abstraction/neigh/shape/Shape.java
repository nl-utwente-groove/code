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

import static groove.abstraction.neigh.Multiplicity.ONE_NODE_MULT;
import static groove.abstraction.neigh.Multiplicity.ZERO_EDGE_MULT;
import static groove.abstraction.neigh.Multiplicity.ZERO_NODE_MULT;
import static groove.abstraction.neigh.Multiplicity.EdgeMultDir.INCOMING;
import static groove.abstraction.neigh.Multiplicity.EdgeMultDir.OUTGOING;
import static groove.abstraction.neigh.Multiplicity.MultKind.EDGE_MULT;
import static groove.abstraction.neigh.Multiplicity.MultKind.NODE_MULT;
import static groove.graph.EdgeRole.BINARY;
import groove.abstraction.neigh.Multiplicity;
import groove.abstraction.neigh.Multiplicity.EdgeMultDir;
import groove.abstraction.neigh.MyHashSet;
import groove.abstraction.neigh.Parameters;
import groove.abstraction.neigh.Util;
import groove.abstraction.neigh.equiv.EquivClass;
import groove.abstraction.neigh.equiv.EquivRelation;
import groove.abstraction.neigh.equiv.GraphNeighEquiv;
import groove.abstraction.neigh.equiv.NodeEquivClass;
import groove.abstraction.neigh.equiv.ShapeNeighEquiv;
import groove.abstraction.neigh.trans.Materialisation;
import groove.abstraction.neigh.trans.RuleToShapeMap;
import groove.graph.Graph;
import groove.graph.GraphRole;
import groove.graph.Label;
import groove.graph.TypeLabel;
import groove.trans.HostEdge;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.trans.RuleEdge;
import groove.trans.RuleNode;
import groove.util.Duo;
import groove.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
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
public final class Shape extends ShapeGraph {

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Default constructor. Creates an empty shape. */
    public Shape(String name, ShapeFactory factory) {
        super(name, factory);
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public GraphRole getRole() {
        return GraphRole.SHAPE;
    }

    @Override
    public Shape newGraph(String name) {
        return new Shape(name, getFactory());
    }

    /** Deep copy of all shape structures. */
    @Override
    public Shape clone() {
        return (Shape) super.clone();
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
        sb.append("Equiv. Relation: " + getEquivRelation() + "\n");
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
    // EZ says: this method is used in rule application.
    @Override
    public boolean addNode(HostNode node) {
        assert !this.isFixed();
        assert node instanceof ShapeNode;
        boolean added = super.addNode(node);
        if (added) {
            ShapeNode nodeS = (ShapeNode) node;
            this.setNodeMult(nodeS, ONE_NODE_MULT);
            this.addToNewEquivClass(nodeS);
        }
        return added;
    }

    // EZ says: this method is not type safe and thus cannot be used when
    // a type graph is enabled. Use method createNode(TypeLabel) instead.
    @Override
    public ShapeNode addNode() {
        assert false;
        return null;
    }

    /**
     * Adds the given edge to the shape and properly adjust the multiplicities
     * when necessary.
     */
    @Override
    public boolean addEdgeWithoutCheck(HostEdge edge) {
        boolean added = super.addEdgeWithoutCheck(edge);
        if (added && edge.getRole() == BINARY) {
            ShapeEdge edgeS = (ShapeEdge) edge;
            for (EdgeMultDir direction : EdgeMultDir.values()) {
                EdgeSignatureStore store = getEdgeSigStore(direction);
                store.addEdge(edgeS, direction);
            }
        }
        return added;
    }

    /** Removes the node from the shape and updates all related structures. */
    @Override
    public boolean removeNode(HostNode node) {
        assert !this.isFixed();
        boolean result = containsNode(node);
        if (result) {
            // Remove all edges that are incident to this node.
            // first collect the edges, to avoid ConcurrentModificationExceptions
            Collection<ShapeEdge> toRemove =
                new ArrayList<ShapeEdge>(edgeSet(node));
            for (ShapeEdge edgeToRemove : toRemove) {
                this.removeEdge(edgeToRemove);
            }
            removeNodeWithoutCheck(node);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean removeNodeWithoutCheck(HostNode node) {
        // Remove node from graph.
        boolean result = super.removeNodeWithoutCheck(node);
        if (result) {
            // make sure the auxiliary structures get updated as well
            ShapeNode nodeS = (ShapeNode) node;

            // Update the equivalence relation.
            EquivClass<ShapeNode> ec =
                getEquivRelation().getEquivClassOf(nodeS);
            if (ec.isSingleton()) {
                // Remove singleton equivalence class from the relation.
                getEquivRelation().remove(ec);
            } else {
                // Remove node from equivalence class.
                // Equivalence classes are fixed, so we have to clone.
                EquivClass<ShapeNode> newEc = ec.clone();
                newEc.remove(nodeS);
                // Update all structures that used the old equivalence class class.
                replaceEc(ec, newEc);
            }
            // Remove entry from node multiplicity map.
            getNodeMultMap().remove(nodeS);
        }
        return result;
    }

    /** Removes the edge from the shape and updates all related structures. */
    @Override
    public boolean removeEdge(HostEdge edge) {
        assert !this.isFixed();
        boolean result = super.removeEdge(edge);
        if (result && edge.getRole() == BINARY) {
            // update the edge signatures
            ShapeEdge edgeS = (ShapeEdge) edge;
            for (EdgeMultDir direction : EdgeMultDir.values()) {
                EdgeSignatureStore edgeSigs = getEdgeSigStore(direction);
                edgeSigs.removeEdge(edgeS);
                assert edgeSigs.isComplete();
            }
        }
        return result;
    }

    @Override
    public void setFixed() {
        super.setFixed();
        getEquivRelation().setFixed();
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /**
     * This method performs a search over the nodes in the shape and uses the
     * lowest free number for the new node. We have to bypass the factory in
     * this way because otherwise the number of nodes in the store keeps
     * increasing and this hurts performance a lot. (See, in particular, the
     * implementation of {@link NodeEquivClass} for the reason).
     * 
     * WARNING: after creating the new node this method makes a call to
     * super.addNode(HostNode) instead of this.addNode(HostNode). This is
     * because the second has side-effects that we don't want when creating
     * a new node.
     */
    private ShapeNode createNode(TypeLabel type) {
        ShapeNode freshNode = this.getFactory().createNode(type, nodeSet());
        assert !nodeSet().contains(freshNode) : String.format(
            "Fresh node %s already in node set %s", freshNode, nodeSet());
        super.addNode(freshNode);
        return freshNode;
    }

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
        // Each node of the shape corresponds to an equivalence class
        // of the graph.
        for (EquivClass<HostNode> ec : gne) {
            // We are building a shape from a graph, this means that the
            // graph nodes are from a different type and therefore are
            // stored in a different node factory. Thus we have to create
            // shape nodes. Remember that we have additional information on
            // the node to be added.
            // Get an arbitrary host node from the equivalence class so we know
            // the type of the shape node that we have to create.
            HostNode nodeG = ec.iterator().next();
            ShapeNode nodeS = this.createNode(nodeG.getType().label());
            // Fill the shape node multiplicity.
            int size = ec.size();
            Multiplicity mult = Multiplicity.approx(size, size, NODE_MULT);
            // Make sure we are using the proper multiplicity values.
            mult = widenMultRange(mult);
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
            Multiplicity mult = origShape.getNodeSetMultSum(ec);
            // Make sure we are using the proper multiplicity values.
            mult = widenMultRange(mult);
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
            EquivClass<ShapeNode> ecS = newNodeEquivClass();
            for (HostNode node : ecG) {
                ecS.add(map.getNode(node));
            }
            getEquivRelation().add(ecS);
        }
    }

    /** Creates and returns a new node equivalence class object. */
    private EquivClass<ShapeNode> newNodeEquivClass() {
        return new NodeEquivClass<ShapeNode>(this.getFactory());
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
            addEdgeWithoutCheck(edgeS);
            // Update the abstraction morphism map.
            for (HostEdge eG : ecG) {
                map.putEdge(eG, edgeS);
            }
        }
    }

    /**
     * Creates the edge multiplicity maps from a graph neighbourhood relation.
     */
    // EZ says: this method could be optimised. Not done yet because it's only
    // used once, when creating the initial shape.
    private void createEdgeMultMaps(GraphNeighEquiv gne, HostToShapeMap map,
            HostGraph graph) {
        assert !this.isFixed();
        Set<HostEdge> intersectEdges = new MyHashSet<HostEdge>();
        // For all binary edges of the shape.
        for (ShapeEdge edgeS : this.binaryEdgeSet()) {
            // For outgoing and incoming maps.
            for (EdgeMultDir direction : EdgeMultDir.values()) {
                // Get the edge signature.
                EdgeSignature es = this.getEdgeSignature(edgeS, direction);
                // Get the reverse abstraction morphism for the class.
                EquivClass<HostNode> ecG = map.getPreImages(es.getEquivClass());
                HostNode nodeG = null;
                switch (direction) {
                case OUTGOING:
                    // Get an arbitrary node of the graph that was mapped to
                    // the shape edge source.
                    nodeG = map.getPreImages(edgeS.source()).iterator().next();
                    // Compute the number of intersecting edges in the graph.
                    Util.getIntersectEdges(graph, nodeG, ecG, edgeS.label(),
                        intersectEdges);
                    break;
                case INCOMING:
                    // Get an arbitrary node of the graph that was mapped to
                    // the shape edge target.
                    nodeG = map.getPreImages(edgeS.target()).iterator().next();
                    // Compute the number of intersecting edges in the graph.
                    Util.getIntersectEdges(graph, ecG, nodeG, edgeS.label(),
                        intersectEdges);
                    break;
                default:
                    assert false;
                }
                int size = intersectEdges.size();
                // Approximate the cardinality of the set of intersecting edges.
                Multiplicity mult = Multiplicity.approx(size, size, EDGE_MULT);
                // Make sure we are using the proper multiplicity values.
                mult = widenMultRange(mult);
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
        // For outgoing and incoming maps.
        for (EdgeMultDir direction : EdgeMultDir.values()) {
            // For all binary edges of the shape. (T)
            Map<EdgeSignature,Multiplicity> esTMap =
                getEdgeSigStore(direction).getMultMap();
            for (Map.Entry<EdgeSignature,Multiplicity> esTEntry : esTMap.entrySet()) {
                // Get the edge signature.
                EdgeSignature esT = esTEntry.getKey();
                // Get an arbitrary node of the original shape (S) that was
                // mapped to the node of the edge signature of T.
                ShapeNode nodeS =
                    (ShapeNode) map.getPreImages(esT.getNode()).iterator().next();
                // Get the reverse map of ecT from the shape morphism.
                EquivClass<HostNode> ecTonS =
                    map.getPreImages(esT.getEquivClass());
                // Compute the bounded multiplicity sum.
                Multiplicity mult =
                    currGraphNeighEquiv.getMultSum(direction, nodeS,
                        esT.getLabel(), ecTonS);
                // Make sure we are using the proper multiplicity values.
                mult = widenMultRange(mult);
                // Store the multiplicity in the proper multiplicity map.
                esTEntry.setValue(mult);
            }
        }
    }

    /** Basic getter method. */
    private ShapeEdge getShapeEdge(ShapeNode source, TypeLabel label,
            ShapeNode target) {
        ShapeEdge result = getFactory().createEdge(source, label, target);
        if (!containsEdge(result)) {
            result = null;
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
     * Returns an edge signature
     * that contains the given edge. If no suitable edge signature is found, a
     * new one is created and returned, but it is not stored.
     */
    public EdgeSignature getEdgeSignature(ShapeEdge edge, EdgeMultDir direction) {
        EdgeSignature result = getEdgeSigStore(direction).getSig(edge);
        if (result == null) {
            result = createEdgeSignature(direction, edge);
        }
        return result;
    }

    /**
     * Returns an edge signature
     * with the given fields. If no suitable edge signature is found, a new
     * one is created and returned, but it is not stored.
     */
    public EdgeSignature getEdgeSignature(EdgeMultDir direction,
            ShapeNode node, TypeLabel label, EquivClass<ShapeNode> ec) {
        EdgeSignature result =
            getEdgeSigStore(direction).getSig(direction, node, label, ec);
        if (result == null) {
            result = createEdgeSignature(direction, node, label, ec);
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
            getNodeMultMap().put(node, mult);
        } else {
            // Setting a node multiplicity to zero is equivalent to removing
            // the node from the shape.
            this.removeNode(node);
        }
    }

    /** Sets the edge signature multiplicity. */
    public void setEdgeSigMult(EdgeSignature es, Multiplicity mult) {
        assert !this.isFixed();
        assert mult.isEdgeKind();
        assert this.containsNode(es.getNode());
        assert getEquivRelation().contains(es.getEquivClass());
        EdgeSignatureStore store = getEdgeSigStore(es.getDirection());
        if (mult.isZero()) {
            // Setting a multiplicity to zero is equivalent to
            // removing all edges in the signature from the shape.
            // collect edges to avoid CuncurrentModificationExceptions
            Collection<ShapeEdge> toRemove =
                new ArrayList<ShapeEdge>(store.getEdges(es));
            for (ShapeEdge edge : toRemove) {
                this.removeEdge(edge);
            }
        } else {
            store.setEdgeMult(es, mult);
        }
    }

    /** Basic getter method. */
    public Multiplicity getNodeMult(ShapeNode node) {
        Multiplicity result = getNodeMultMap().get(node);
        return result == null ? ZERO_NODE_MULT : result;
    }

    /**
     * Returns the bounded sum of the node multiplicities of the given set.
     */
    Multiplicity getNodeSetMultSum(Set<? extends HostNode> nodes) {
        Multiplicity accumulator = ZERO_NODE_MULT;
        for (HostNode node : nodes) {
            Multiplicity nodeMult = this.getNodeMult((ShapeNode) node);
            accumulator = accumulator.add(nodeMult);
        }
        return accumulator;
    }

    /** Basic getter method. */
    public Multiplicity getEdgeMult(ShapeEdge edge, EdgeMultDir direction) {
        Multiplicity result = getEdgeSigStore(direction).getMult(edge);
        return result == null ? ZERO_EDGE_MULT : result;

    }

    /** Basic getter method. */
    public Multiplicity getEdgeSigMult(EdgeSignature es) {
        Multiplicity result = this.getEdgeMultMap(es.getDirection()).get(es);
        return result == null ? ZERO_EDGE_MULT : result;
    }

    /**
     * Returns the bounded sum of the edge multiplicities of the given set.
     */
    Multiplicity getEdgeSigSetMult(Set<EdgeSignature> esS) {
        Multiplicity accumulator = ZERO_EDGE_MULT;
        for (EdgeSignature es : esS) {
            Multiplicity edgeMult = this.getEdgeSigMult(es);
            accumulator = accumulator.add(edgeMult);
        }
        return accumulator;
    }

    /** Creates a new equivalence class and adds the given node to it. */
    private EquivClass<ShapeNode> addToNewEquivClass(ShapeNode node) {
        assert !this.isFixed();
        EquivClass<ShapeNode> newEc = newNodeEquivClass();
        newEc.add(node);
        getEquivRelation().add(newEc);
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

    /** Returns true if the given signature occurs in the shape. */
    public boolean hasEdgeSignature(EdgeSignature es) {
        return getEdgeSigSet(es.getDirection()).contains(es);
    }

    /**
     * Returns true if the number of edges from the signature
     * occurring in the shape is one.
     */
    public boolean isEdgeSigUnique(EdgeSignature es) {
        return this.getEdgesFromSig(es).size() == 1;
    }

    /**
     * Finds the edge of the given signature with the minimal number. This
     * edge is taken to be the main edge of the signature in a graphical
     * representation. 
     */
    private ShapeEdge getMinimumEdgeFromSig(EdgeSignature es) {
        ShapeEdge result = null;
        ShapeNode resultOpposite = null;
        EdgeMultDir direction = es.getDirection();
        for (ShapeEdge edge : getEdgesFromSig(es)) {
            ShapeNode ecNode = direction.opposite(edge);
            if (resultOpposite == null
                || ecNode.getNumber() < resultOpposite.getNumber()) {
                result = edge;
                resultOpposite = ecNode;
            }
        }
        return result;
    }

    /**
     * Returns the set of edges from the signature occurring in the shape.
     */
    public Set<ShapeEdge> getEdgesFromSig(EdgeSignature es) {
        return getEdgeSigStore(es.getDirection()).getEdges(es);
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
        assert this.getNodeMult(collectorNode).isCollector();

        // The current match to be updated.
        RuleToShapeMap match = mat.getMatch();
        // Check the nodes on the rule that were mapped to nodeS.
        Set<RuleNode> nodesR =
            mat.getOriginalMatch().getPreImages(collectorNode);
        assert !nodesR.isEmpty();
        // Compute how many copies of the node we need to materialise.
        int copies = nodesR.size();

        // Special case when the collector node multiplicity is not
        // unbounded and equals exactly the number of copies we want.
        boolean useCollector = false;
        Multiplicity collectNodeMult = this.getNodeMult(collectorNode);
        if (!collectNodeMult.isUnbounded()
            && collectNodeMult.getLowerBound() == copies) {
            useCollector = true;
            copies--;
        }

        // Constant singleton node multiplicity
        // Create a new shape node for each rule node.
        Iterator<RuleNode> iter = nodesR.iterator();
        for (int i = 0; i < copies; i++) {
            RuleNode nodeR = iter.next();
            ShapeNode newNode =
                this.createNode(collectorNode.getType().label());
            // The new node is concrete so set its multiplicity to one.
            this.setNodeMult(newNode, ONE_NODE_MULT);
            // Copy the labels from the original node.
            this.copyUnaryEdges(collectorNode, newNode, nodeR, match);
            // Add the new node to a new equivalence class.
            this.addToNewEquivClass(newNode);
            // Add the new node to the materialisation.
            mat.addMatNode(newNode, collectorNode, nodeR);
        }

        // Adjust the multiplicity of the original node.
        Multiplicity oldMult = this.getNodeMult(collectorNode);
        Multiplicity newMult =
            oldMult.sub(Multiplicity.scale(ONE_NODE_MULT, copies));
        assert !newMult.isZero();
        this.setNodeMult(collectorNode, newMult);

        if (useCollector) {
            assert newMult.isOne();
            RuleNode nodeR = iter.next();
            this.copyUnaryEdges(collectorNode, collectorNode, nodeR, match);
            mat.addMatNode(collectorNode, collectorNode, nodeR);
            assert !iter.hasNext();
        }

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
    @SuppressWarnings("unchecked")
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
        EquivClass<ShapeNode> singEc = newNodeEquivClass();
        singEc.add(nodeS);
        // Update the edge multiplicity map with the new singleton class.
        //        this.addNewSingletonEc(origEc, singEc);
        // Replace the original equivalence class with the remainder of the
        // split.
        this.replaceEc(origEc, remEc, singEc);
        // Update the equivalence relation.
        //        getEquivRelation().add(singEc);
    }

    /**
     * Removes all edges from the shape that cross equivalence classes and that
     * will be affected during the materialisation process. Those edges are
     * stored as possible edges in the materialisation object.
     */
    private void handleCrossingEdges(Materialisation mat, ShapeNode nodeS) {
        Set<ShapeEdge> possibleEdges = new MyHashSet<ShapeEdge>();
        // Handle all incident edges to the node.
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
            mat.addPossibleEdge(possibleEdge, possibleEdge);
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
    @SuppressWarnings("unchecked")
    public void splitNode(Materialisation mat, ShapeNode nodeS, int copies) {
        assert !this.isFixed();
        assert this.containsNode(nodeS);
        assert mat.getStage() == 2;

        EquivClass<ShapeNode> oldEc = this.getEquivClassOf(nodeS);
        EquivClass<ShapeNode> newEc = oldEc.clone();
        for (int i = 0; i < copies; i++) {
            // Create a new shape node.
            ShapeNode newNode = this.createNode(nodeS.getType().label());
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

    /**
     * Replaces an equivalence class with one or more new classes.
     * The edge signature maps are updated by removing all signatures
     * to the old equivalence class and creating new signatures (with the
     * same multiplicity as the old one) where
     * there is at least one edge going to a node in the equivalence class.
     * The node equivalence maps are also updated, by removing the
     * old equivalence class and adding all new ones.
     */
    private void replaceEc(EquivClass<ShapeNode> oldEc,
            EquivClass<ShapeNode>... newEcs) {
        // Update the equivalence relation.
        getEquivRelation().remove(oldEc);
        for (EquivClass<ShapeNode> newEc : newEcs) {
            getEquivRelation().add(newEc);
        }
        // Update all maps that use edge signatures that contained the old
        // equivalence class.
        for (EdgeMultDir dir : EdgeMultDir.values()) {
            List<EdgeSignature> removed = new ArrayList<EdgeSignature>();
            List<Pair<EdgeSignature,Multiplicity>> added =
                new ArrayList<Pair<EdgeSignature,Multiplicity>>();
            EdgeSignatureStore edgeSigs = getEdgeSigStore(dir);
            for (Map.Entry<EdgeSignature,Multiplicity> sigEntry : edgeSigs.getMultMap().entrySet()) {
                EdgeSignature oldEs = sigEntry.getKey();
                if (!oldEs.hasSameEquivClass(oldEc)) {
                    continue;
                }
                // replace the old signatures by new ones
                removed.add(oldEs);
                Multiplicity mult = sigEntry.getValue();
                if (!mult.isZero()) {
                    ShapeNode esNode = oldEs.getNode();
                    TypeLabel eslabel = oldEs.getLabel();
                    for (EquivClass<ShapeNode> newEc : newEcs) {
                        EdgeSignature newEs =
                            createEdgeSignature(dir, esNode, eslabel, newEc);
                        if (newEs.hasEdges(this)) {
                            added.add(Pair.newPair(newEs, mult));
                        }
                    }
                }
            }
            for (EdgeSignature remove : removed) {
                edgeSigs.removeSig(remove);
            }
            for (Pair<EdgeSignature,Multiplicity> add : added) {
                this.setEdgeSigMult(add.one(), add.two());
            }
            assert edgeSigs.isComplete();
        }
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

    /** Returns true if the edge is the only one in the signature. */
    public boolean isEdgeUnique(ShapeEdge edge, EdgeMultDir direction) {
        EdgeSignature es = this.getEdgeSignature(edge, direction);
        return this.isEdgeSigUnique(es);
    }

    /** Normalises the shape object and returns the newly modified shape. */
    public Shape normalise() {
        Shape newShape = newGraph(getName());
        HostToShapeMap map = new HostToShapeMap(this.getFactory());
        int radius = Parameters.getAbsRadius();
        // Compute the equivalence relation on this shape.
        ShapeNeighEquiv sne = new ShapeNeighEquiv(this, radius);
        // Now build the shape.
        newShape.createShapeNodes(sne, map, this);
        newShape.createEquivRelation(sne.getPrevEquivRelation(), map);
        newShape.createShapeEdges(sne.getEdgesEquivRel(), map);
        newShape.createEdgeMultMaps(sne, map, this);
        // Making node multiplicities more precise, when possible.
        nodeLoop: for (ShapeNode node : newShape.nodeSet()) {
            Multiplicity nodeMult = newShape.getNodeMult(node);
            if (nodeMult.isUnbounded()) {
                for (EdgeMultDir direction : EdgeMultDir.values()) {
                    for (ShapeEdge edge : newShape.binaryEdgeSet(node,
                        direction)) {
                        ShapeNode opp = direction.opposite(edge);
                        Multiplicity oppMult = newShape.getNodeMult(opp);
                        if (oppMult.isOne() && newShape.isEdgeConcrete(edge)) {
                            // The node can only be concrete.
                            newShape.setNodeMult(node, ONE_NODE_MULT);
                            continue nodeLoop;
                        }
                    }
                }
            }
        }
        assert newShape.isInvariantOK();
        return newShape;
    }

    /**
     * Reduces the shape by removing all nodes with multiplicity 0+. The shape
     * is modified by this method so make sure to clone it first if you want
     * to preserve the original one.
     */
    public void reduce() {
        Set<ShapeNode> toRemove = new MyHashSet<ShapeNode>();
        for (ShapeNode node : this.nodeSet()) {
            if (this.getNodeMult(node).isZeroPlus()) {
                toRemove.add(node);
            }
        }
        for (ShapeNode node : toRemove) {
            this.removeNode(node);
        }
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
     * @return true if the invariant holds, false otherwise.
     */
    public boolean isInvariantOK() {
        for (EdgeMultDir direction : EdgeMultDir.values()) {
            for (EdgeSignature es : getEdgeSigSet(direction)) {
                // Make sure that the equivalence class of the edge signature
                // is in the equivalence relation.
                if (!getEquivRelation().contains(es.getEquivClass())) {
                    return false;
                }
                // Make sure the edge signature has corresponding edges.
                if (this.getEdgesFromSig(es).size() == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /** Returns true if the shape has an edge multiplicity different than 1. */
    public boolean hasCollectorEdgeMults() {
        for (EdgeMultDir direction : EdgeMultDir.values()) {
            for (Multiplicity mult : getEdgeMultMap(direction).values()) {
                if (mult.isCollector()) {
                    return true;
                }
            }
        }
        return false;
    }

    // ------------------------------------------------------------------------
    // Static Methods
    // ------------------------------------------------------------------------

    /** Creates a shape from the given graph. */
    public static Shape createShape(HostGraph graph) {
        Shape shape =
            new Shape(graph.getName(),
                ShapeFactory.newInstance(graph.getTypeGraph()));
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

    /**
     * If the abstraction is set to use only three multiplicity values, then
     * this method projects any unbounded multiplicity to 0+.
     * If all multiplicity values are used, then this method returns the object
     * given as parameter.
     */
    private static Multiplicity widenMultRange(Multiplicity mult) {
        if (Parameters.isUseThreeValues() && mult.isUnbounded()
            && !mult.isZeroPlus()) {
            return Multiplicity.getMultiplicity(0, Multiplicity.OMEGA,
                mult.getKind());
        } else {
            return mult;
        }
    }
}
