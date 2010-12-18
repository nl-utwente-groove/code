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
package groove.abstraction;

import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.NodeEdgeMap;
import groove.graph.TypeLabel;
import groove.graph.iso.CertificateStrategy.Certificate;
import groove.graph.iso.DefaultIsoChecker;
import groove.graph.iso.DefaultIsoChecker.IsoCheckerState;
import groove.trans.DefaultHostGraph;
import groove.trans.HostEdge;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.trans.Rule;
import groove.trans.RuleEvent;
import groove.trans.RuleMatch;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
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

    private static boolean assertsEnabled = false;

    @SuppressWarnings("all")
    private static void testAssertions() {
        assert assertsEnabled = true; // Intentional side effect!!!
    }

    static {
        testAssertions();
    }

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    /**
     * The graph (or shape) from which this shape was constructed. Note that
     * this is NOT the underlying graph structure of the shape but instead a
     * reference to the object that gave rise to this current shape.
     */
    private HostGraph graph;

    /**
     * The shaping relation between 'this.graph' and 'this'.
     */
    // EZ says to himself: BE CAREFUL HERE. Remember that:
    // - If 'this.graph' is an instance of a concrete graph, then the shaping
    //   relation is a shaping morphism that goes from elements of
    //   'this.graph' to elements of 'this'.
    // - If 'this.graph' is an instance of a shape and 'this' is currently
    //   being materialised, then the shaping relation is an abstraction morphism
    //   that goes in the OTHER direction, from elements of 'this' to elements
    //   of 'this.graph'.
    // - If 'this.graph' is an instance of a shape and 'this' has been
    //   normalised, then the shaping relation is an abstraction morphism that
    //   goes from elements of 'this.graph' to elements of 'this'.
    private final Map<HostNode,ShapeNode> nodeShaping;
    private final Map<HostEdge,ShapeEdge> edgeShaping;

    /**
     * The equivalence relation over the nodes of the shape.
     */
    private EquivRelation<ShapeNode> equivRel;
    /**
     * The node multiplicity map.
     */
    private final Map<ShapeNode,Multiplicity> nodeMultMap;
    /**
     * The outgoing edge multiplicity map.
     */
    private Map<EdgeSignature,Multiplicity> outEdgeMultMap;
    /**
     * The incoming edge multiplicity map.
     */
    private Map<EdgeSignature,Multiplicity> inEdgeMultMap;
    /**
     * Auxiliary set to store edge signatures. As as invariant, this set will
     * contain at least all the edge signatures occurring as keys on the
     * multiplicity map.
     */
    private final Set<EdgeSignature> edgeSigSet;
    /** 
     * Set of frozen edges of the shape, i.e., the edges that are guaranteed
     * to exist. This also imply that they are concrete, i.e., have all
     * multiplicities equal to one. This is only used during the
     * materialisation phase.
     */
    private final Set<ShapeEdge> frozenEdges;
    /**
     * Flag to indicate if the shape has been frozen. A shape is frozen when
     * it is stored in a ShapeState of the transition system, to avoid unwanted
     * modifications. Once frozen a shape cannot be thawed, the only way to
     * perform modifications is by cloning the frozen shape and changing the
     * clone.
     */
    private boolean frozen;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Default constructor. Creates a shape from a concrete graph. */
    public Shape(HostGraph graph) {
        super();
        this.graph = graph;
        this.nodeShaping = new HashMap<HostNode,ShapeNode>();
        this.edgeShaping = new HashMap<HostEdge,ShapeEdge>();
        this.equivRel = new EquivRelation<ShapeNode>();
        this.nodeMultMap = new HashMap<ShapeNode,Multiplicity>();
        this.outEdgeMultMap = new HashMap<EdgeSignature,Multiplicity>();
        this.inEdgeMultMap = new HashMap<EdgeSignature,Multiplicity>();
        this.edgeSigSet = new HashSet<EdgeSignature>();
        this.frozenEdges = new HashSet<ShapeEdge>();
        this.frozen = false;
        this.buildShape(false);
    }

    /**
     * Empty constructor. After creating an object with this constructor
     * method buildShapeFromShape should be called.
     */
    private Shape() {
        super();
        this.graph = null;
        this.nodeShaping = new HashMap<HostNode,ShapeNode>();
        this.edgeShaping = new HashMap<HostEdge,ShapeEdge>();
        this.equivRel = new EquivRelation<ShapeNode>();
        this.nodeMultMap = new HashMap<ShapeNode,Multiplicity>();
        this.outEdgeMultMap = new HashMap<EdgeSignature,Multiplicity>();
        this.inEdgeMultMap = new HashMap<EdgeSignature,Multiplicity>();
        this.edgeSigSet = new HashSet<EdgeSignature>();
        this.frozenEdges = new HashSet<ShapeEdge>();
        this.frozen = false;
    }

    /** Copying constructor. Clones all structures of the shape. */
    private Shape(Shape shape) {
        super(shape);
        this.graph = shape.graph;
        this.nodeShaping = new HashMap<HostNode,ShapeNode>(shape.nodeShaping);
        this.edgeShaping = new HashMap<HostEdge,ShapeEdge>(shape.edgeShaping);
        this.nodeMultMap =
            new HashMap<ShapeNode,Multiplicity>(shape.nodeMultMap);
        this.equivRel = new EquivRelation<ShapeNode>(shape.equivRel);
        this.frozenEdges = new HashSet<ShapeEdge>(shape.frozenEdges);
        this.frozen = false;

        // Clone the edge signature set.
        this.edgeSigSet = new HashSet<EdgeSignature>();
        for (EdgeSignature es : shape.edgeSigSet) {
            this.edgeSigSet.add(this.getEdgeSignature(es));
        }

        // Clone the multiplicity maps.
        this.outEdgeMultMap = new HashMap<EdgeSignature,Multiplicity>();
        for (Entry<EdgeSignature,Multiplicity> entry : shape.outEdgeMultMap.entrySet()) {
            this.outEdgeMultMap.put(this.getEdgeSignature(entry.getKey()),
                entry.getValue());
        }
        this.inEdgeMultMap = new HashMap<EdgeSignature,Multiplicity>();
        for (Entry<EdgeSignature,Multiplicity> entry : shape.inEdgeMultMap.entrySet()) {
            this.inEdgeMultMap.put(this.getEdgeSignature(entry.getKey()),
                entry.getValue());
        }

        this.checkShapeInvariant();
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
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Nodes:\n");
        for (ShapeNode node : this.nodeSet()) {
            sb.append("  " + node + ":" + this.getNodeMult(node) + " ");
            sb.append(Util.getNodeLabels(this, node) + "\n");
        }
        sb.append("Edges:\n");
        for (Edge edge : Util.getBinaryEdges(this)) {
            ShapeEdge e = (ShapeEdge) edge;
            String frozen = "";
            if (this.isFrozen(e)) {
                frozen = " F ";
            }
            sb.append("  " + this.getEdgeOutMult(e) + ":" + edge + ":"
                + this.getEdgeInMult(e) + frozen + "\n");
        }
        return sb.toString();
    }

    @Override
    public Shape clone() {
        Shape shape = new Shape(this);
        return shape;
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
        assert !this.isFrozen();
        assert node instanceof ShapeNode : "Invalid node type!";
        boolean added = super.addNode(node);
        if (added) {
            ShapeNode nodeS = (ShapeNode) node;
            this.setNodeMult(nodeS, Multiplicity.getMultOf(1));
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
        assert !this.isFrozen();
        assert edge instanceof ShapeEdge : "Invalid edge type!";
        ShapeEdge edgeS = (ShapeEdge) edge;
        boolean added = super.addEdgeWithoutCheck(edgeS);
        if (added) {
            if (!Util.isUnary(edgeS)) {
                Multiplicity zero = Multiplicity.getMultOf(0);
                Multiplicity one = Multiplicity.getMultOf(1);

                // Outgoing multiplicity.
                EdgeSignature outEs = this.getEdgeOutSignature(edgeS);
                Multiplicity outMult = this.getEdgeSigOutMult(outEs);
                if (outMult.equals(zero)) {
                    this.setEdgeOutMult(outEs, one);
                }
                // Incoming multiplicity.
                EdgeSignature inEs = this.getEdgeInSignature(edgeS);
                Multiplicity inMult = this.getEdgeSigInMult(inEs);
                if (inMult.equals(zero)) {
                    this.setEdgeInMult(inEs, one);
                }
            }
        }
        return added;
    }

    /** Removes the node from the shape and updates all related structures. */
    @Override
    public boolean removeNode(HostNode node) {
        assert !this.isFrozen();
        assert node instanceof ShapeNode;
        ShapeNode nodeS = (ShapeNode) node;
        assert this.nodeSet().contains(node);

        // Remove entry from node multiplicity map.
        this.nodeMultMap.remove(nodeS);

        // Remove entry from node shaping map.
        this.nodeShaping.remove(nodeS);

        // Update edge signature maps.
        Iterator<EdgeSignature> esIter = this.edgeSigSet.iterator();
        while (esIter.hasNext()) {
            EdgeSignature es = esIter.next();
            if (es.getNode().equals(nodeS)) {
                this.outEdgeMultMap.remove(es);
                this.inEdgeMultMap.remove(es);
                esIter.remove();
            }
        }

        // Collect edges to remove.
        Set<ShapeEdge> toRemove = new HashSet<ShapeEdge>();
        toRemove.addAll(this.edgeSet(nodeS));
        for (ShapeEdge edgeS : toRemove) {
            this.removeEdge(edgeS);
        }

        // Update the equivalence relation
        EquivClass<ShapeNode> nodeEc = this.getEquivClassOf(nodeS);
        if (nodeEc.size() == 1) {
            // Remove singleton equivalence relation.
            this.equivRel.remove(nodeEc);
            this.cleanEdgeSigSet();
        } else {
            // Remove node from equivalence relation.
            nodeEc.remove(nodeS);
        }

        this.updateHashMaps();

        // Remove node from graph.
        return super.removeNodeWithoutCheck(node);
    }

    /** Removes the edge from the shape and updates all related structures. */
    @Override
    public boolean removeEdge(HostEdge edge) {
        assert !this.isFrozen();
        assert edge instanceof ShapeEdge;
        ShapeEdge edgeS = (ShapeEdge) edge;
        assert !this.isFrozen(edgeS);
        // Remove entry from edge shaping map.
        this.edgeShaping.remove(edgeS);
        // Update outgoing multiplicity map.
        EdgeSignature outEs = this.getEdgeOutSignature(edgeS);
        if (outEs.isUnique() || this.isOutEdgeSigUnique(outEs)) {
            this.outEdgeMultMap.remove(outEs);
        }
        // Update incoming multiplicity map.
        EdgeSignature inEs = this.getEdgeInSignature(edgeS);
        if (inEs.isUnique() || this.isInEdgeSigUnique(inEs)) {
            this.inEdgeMultMap.remove(inEs);
        }
        // Remove edge from graph.
        return super.removeEdge(edge);
    }

    @Override
    public boolean removeNodeSetWithoutCheck(
            Collection<? extends HostNode> nodeSet) {
        assert !this.isFrozen();
        boolean removed = false;
        for (HostNode node : nodeSet) {
            removed |= this.removeNode(node);
        }
        return removed;
    }

    /** Compares two shapes first using the hash codes and then isomorphism. */
    @Override
    public boolean equals(Object o) {
        boolean result;
        if (this == o) {
            result = true;
        } else if (!(o instanceof Shape)) {
            result = false;
        } else {
            Shape other = (Shape) o;
            result = this.hashCode() == other.hashCode();
            if (result) {
                // The shapes may be isomorphic.
                result = this.isIsomorphicTo(other);
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        // First get the certificate of the graph structure.
        int result = super.getCertifier(true).getGraphCertificate().hashCode();

        Map<Element,? extends Certificate<?>> certMap =
            super.getCertifier(true).getCertificateMap();

        // Add the hashes for the equivalence relation.
        int erHash = 0;
        for (EquivClass<ShapeNode> ec : this.equivRel) {
            int temp = 0;
            for (ShapeNode n : ec) {
                temp += certMap.get(n).hashCode();
            }
            erHash += temp * temp;
        }
        result = prime * result + erHash;

        return result;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    @Override
    public ShapeFactory getFactory() {
        return ShapeFactory.instance();
    }

    /**
     * Constructs a new shape from a given shape. This method is used when
     * normalising a transformed shape.
     */
    private void buildShapeFromShape(Shape origShape) {
        this.graph = origShape;
        this.buildShape(true);
    }

    /**
     * Builds a shape from a concrete graph or a shape.  
     */
    private void buildShape(boolean fromShape) {
        assert !this.isFrozen();
        int radius = Parameters.getAbsRadius();
        // The iterations on the equivalence relation computation.
        GraphNeighEquiv er[] = new GraphNeighEquiv[radius + 1];
        // Create the level 0 equivalence relation.
        if (fromShape) {
            er[0] = new ShapeNeighEquiv(this.graph);
        } else {
            er[0] = new GraphNeighEquiv(this.graph);
        }
        // Compute all equivalence relations up to the abstraction radius.
        for (int i = 1; i <= radius; i++) {
            int j = i - 1;
            assert er[j].getRadius() == j;
            er[i] = (GraphNeighEquiv) er[j].clone();
            er[i].refineEquivRelation();
        }
        // Find the adequate relations in the array such that the current
        // equivalence relation is coarser or equal to the previous one.
        GraphNeighEquiv currGraphNeighEquiv = er[radius];
        GraphNeighEquiv prevGraphNeighEquiv = er[radius - 1];

        // Now build the shape.
        this.createShapeNodes(currGraphNeighEquiv, fromShape);
        this.createShapeNodesEquivRel(prevGraphNeighEquiv);
        this.createShapeEdges(currGraphNeighEquiv.getEdgesEquivRel());
        if (fromShape) {
            assert currGraphNeighEquiv instanceof ShapeNeighEquiv;
            this.createEdgeMultMaps((ShapeNeighEquiv) currGraphNeighEquiv);
        } else {
            this.createEdgeMultMaps(currGraphNeighEquiv);
        }
        this.checkShapeInvariant();
    }

    /**
     * Creates the nodes of this shape based on the equivalence relation given. 
     */
    private void createShapeNodes(GraphNeighEquiv currGraphNeighEquiv,
            boolean fromShape) {
        assert !this.isFrozen();
        // Each node of the shape correspond to an equivalence class
        // of the graph.
        for (EquivClass<HostNode> nodeEquivClass : currGraphNeighEquiv) {
            ShapeNode shapeNode = getFactory().createNode();
            // Add a shape node to the shape.
            // Call the super method because we have additional information on
            // the node to be added.
            super.addNode(shapeNode);
            // Update the shaping information.
            for (HostNode graphNode : nodeEquivClass) {
                this.nodeShaping.put(graphNode, shapeNode);
            }
            // Fill the shape node multiplicity.
            Multiplicity nodeMult;
            if (fromShape) {
                nodeMult =
                    Multiplicity.getNodeSetMultSum((Shape) this.graph,
                        nodeEquivClass);
            } else {
                nodeMult = Multiplicity.getNodeSetMult(nodeEquivClass);
            }
            this.setNodeMult(shapeNode, nodeMult);
        }
    }

    /**
     * Creates the equivalence relation between shape nodes based on the
     * equivalence relation given.
     */
    private void createShapeNodesEquivRel(GraphNeighEquiv prevGraphNeighEquiv) {
        assert !this.isFrozen();
        // We use the previous (i-1) graph equivalence relation.
        for (EquivClass<HostNode> nodeEquivClass : prevGraphNeighEquiv) {
            EquivClass<ShapeNode> shapeEquivClass = new EquivClass<ShapeNode>();
            for (HostNode graphNode : nodeEquivClass) {
                shapeEquivClass.add(this.nodeShaping.get(graphNode));
            }
            this.equivRel.add(shapeEquivClass);
        }
    }

    /**
     * Creates the edges of this shape based on the equivalence relation given. 
     */
    private void createShapeEdges(EquivRelation<HostEdge> edgeEquivRel) {
        assert !this.isFrozen();
        // Each edge of the shape correspond to an equivalence class
        // of the graph.
        for (EquivClass<HostEdge> edgeEquivClassG : edgeEquivRel) {
            // Get an arbitrary edge from the equivalence class.
            HostEdge edgeG = edgeEquivClassG.iterator().next();

            // Create and add a shape edge to the shape.
            HostNode srcG = edgeG.source();
            HostNode tgtG = edgeG.target();
            ShapeNode srcS = this.nodeShaping.get(srcG);
            ShapeNode tgtS = this.nodeShaping.get(tgtG);
            TypeLabel labelS = edgeG.label();
            ShapeEdge edgeS = (ShapeEdge) this.createEdge(srcS, labelS, tgtS);
            this.addEdge(edgeS);

            // Update the shaping information.
            for (HostEdge eG : edgeEquivClassG) {
                this.edgeShaping.put(eG, edgeS);
            }
        }
    }

    /**
     * Creates the edge multiplicity maps from a graph neighbourhood relation.
     */
    private void createEdgeMultMaps(GraphNeighEquiv currGraphNeighEquiv) {
        assert !this.isFrozen();
        // For all binary labels.
        for (TypeLabel label : Util.binaryLabelSet(this.graph)) {
            // For all nodes in the graph.
            for (HostNode node : this.graph.nodeSet()) {
                ShapeNode nodeS = this.getShapeNode(node);
                // For all equivalence classes in the shape.
                for (EquivClass<ShapeNode> ecS : this.equivRel) {
                    Set<HostNode> nodesG = this.getReverseNodeMap(ecS);
                    EdgeSignature es = this.getEdgeSignature(nodeS, label, ecS);

                    // Outgoing multiplicity.
                    Set<HostEdge> outInter =
                        Util.getIntersectEdges(this.graph, node, nodesG, label);
                    Multiplicity outMult =
                        Multiplicity.getEdgeSetMult(outInter);
                    // Incoming multiplicity.
                    Set<HostEdge> inInter =
                        Util.getIntersectEdges(this.graph, nodesG, node, label);
                    Multiplicity inMult = Multiplicity.getEdgeSetMult(inInter);

                    if (outMult.isPositive()) {
                        this.outEdgeMultMap.put(es, outMult);
                    } // else don't store multiplicity zero.
                    if (inMult.isPositive()) {
                        this.inEdgeMultMap.put(es, inMult);
                    } // else don't store multiplicity zero.
                }
            }
        }
        this.cleanEdgeSigSet();
    }

    /**
     * Creates the edge multiplicity maps from a shape neighbourhood relation.
     * See item 6 of Def. 22 on page 17 of the Technical Report.
     */
    private void createEdgeMultMaps(ShapeNeighEquiv currGraphNeighEquiv) {
        assert !this.isFrozen();
        // Original shape (S).
        Shape origShape = (Shape) this.graph;
        // For all binary labels.
        for (TypeLabel label : Util.binaryLabelSet(origShape)) {
            // For all the nodes in the new shape (T).
            for (ShapeNode nodeT : this.nodeSet()) {
                // First get a node from S that is part of the equivalence class
                // that corresponds to nodeT. We may take any node from such a
                // class because all nodes of the class have the same multiplicity
                // sum. This was checked when the neighbourhood equivalence
                // relation was built.
                ShapeNode nodeS =
                    this.getReverseNodeMap(nodeT).iterator().next();

                // For all equivalence classes in the new shape (T).
                for (EquivClass<ShapeNode> ecT : this.equivRel) {
                    // Compute the set of equivalence classes from the original
                    // shape that we need to consider.
                    Set<EquivClass<ShapeNode>> kSet =
                        new HashSet<EquivClass<ShapeNode>>();
                    // Get the reverse map of ecT from the abstraction morphism.
                    EquivClass<ShapeNode> ecTonS = this.getReverseEc(ecT);
                    for (EquivClass<ShapeNode> possibleK : origShape.equivRel) {
                        if (ecTonS.containsAll(possibleK)) {
                            kSet.add(possibleK);
                        }
                    } // Now we have the kSet.

                    // Calculate the sums.
                    Multiplicity outMult =
                        Multiplicity.sumOutMult(origShape, nodeS, label, kSet);
                    Multiplicity inMult =
                        Multiplicity.sumInMult(origShape, nodeS, label, kSet);

                    EdgeSignature es = this.getEdgeSignature(nodeT, label, ecT);

                    if (outMult.isPositive()) {
                        this.outEdgeMultMap.put(es, outMult);
                    } // else don't store multiplicity zero.
                    if (inMult.isPositive()) {
                        this.inEdgeMultMap.put(es, inMult);
                    } // else don't store multiplicity zero.
                }
            }
        }
        this.cleanEdgeSigSet();
    }

    /** Clears the edge signature set of spurious signatures. */
    public void cleanEdgeSigSet() {
        this.edgeSigSet.clear();
        this.edgeSigSet.addAll(this.outEdgeMultMap.keySet());
        this.edgeSigSet.addAll(this.inEdgeMultMap.keySet());
    }

    /**
     * Sets the node multiplicity. If the multiplicity given is zero, then
     * the node is removed from the shape.
     */
    public void setNodeMult(ShapeNode node, Multiplicity mult) {
        assert !this.isFrozen();
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

    /** Sets the edge outgoing multiplicity. */
    public void setEdgeOutMult(EdgeSignature es, Multiplicity mult) {
        assert !this.isFrozen();
        if (mult.isPositive()) {
            this.outEdgeMultMap.put(es, mult);
        } else {
            // Setting an outgoing multiplicity to zero is equivalent to
            // removing all edges in the signature from the shape.
            ShapeNode src = es.getNode();
            Label label = es.getLabel();
            EquivClass<ShapeNode> ec = es.getEquivClass();
            for (ShapeEdge edge : this.outBinaryEdgeSet(src)) {
                if (!this.isFrozen(edge) && edge.label().equals(label)
                    && ec.contains(edge.target())) {
                    this.removeEdge(edge);
                }
            }
            this.outEdgeMultMap.remove(es);
        }
    }

    /** Sets the edge incoming multiplicity. */
    public void setEdgeInMult(EdgeSignature es, Multiplicity mult) {
        assert !this.isFrozen();
        if (mult.isPositive()) {
            this.inEdgeMultMap.put(es, mult);
        } else {
            // Setting an incoming multiplicity to zero is equivalent to
            // removing all edges in the signature from the shape.
            ShapeNode tgt = es.getNode();
            Label label = es.getLabel();
            EquivClass<ShapeNode> ec = es.getEquivClass();
            for (ShapeEdge edge : this.inBinaryEdgeSet(tgt)) {
                if (!this.isFrozen(edge) && edge.label().equals(label)
                    && ec.contains(edge.source())) {
                    this.removeEdge(edge);
                }
            }
            this.inEdgeMultMap.remove(es);
        }
    }

    /** Basic getter method. */
    public Multiplicity getNodeMult(ShapeNode node) {
        Multiplicity mult = this.nodeMultMap.get(node);
        if (mult == null) {
            mult = Multiplicity.getMultOf(0);
        }
        return mult;
    }

    /** Basic getter method. */
    public Multiplicity getEdgeOutMult(ShapeEdge edge) {
        Multiplicity mult;
        if (this.isFrozen(edge)) {
            mult = Multiplicity.getMultOf(1);
        } else {
            EdgeSignature es = this.getEdgeOutSignature(edge);
            mult = this.getEdgeSigOutMult(es);
        }
        return mult;
    }

    /** Basic getter method. */
    public Multiplicity getEdgeInMult(ShapeEdge edge) {
        Multiplicity mult;
        if (this.isFrozen(edge)) {
            mult = Multiplicity.getMultOf(1);
        } else {
            EdgeSignature es = this.getEdgeInSignature(edge);
            mult = this.getEdgeSigInMult(es);
        }
        return mult;
    }

    /**
     * Returns the set of nodes in the shaping relation that maps to values
     * occurring in the given equivalence class. 
     */
    private Set<HostNode> getReverseNodeMap(EquivClass<ShapeNode> ecS) {
        Set<HostNode> nodesG = new HashSet<HostNode>();
        for (Entry<HostNode,ShapeNode> entry : this.nodeShaping.entrySet()) {
            if (ecS.contains(entry.getValue())) {
                nodesG.add(entry.getKey());
            }
        }
        return nodesG;
    }

    /**
     * Returns the set of nodes in the shaping relation that maps to the
     * given node.
     * It is an error to call this method if this.graph is not a Shape. 
     */
    public Set<ShapeNode> getReverseNodeMap(ShapeNode nodeS) {
        assert this.graph instanceof Shape : "Invalid method call.";
        Set<ShapeNode> nodesG = new HashSet<ShapeNode>();
        for (Entry<HostNode,ShapeNode> entry : this.nodeShaping.entrySet()) {
            if (nodeS.equals(entry.getValue())) {
                nodesG.add((ShapeNode) entry.getKey());
            }
        }
        return nodesG;
    }

    /**
     * Returns the equivalence class in the shaping relation that maps to values
     * occurring in the given equivalence class.
     * It is an error to call this method if this.graph is not a Shape. 
     */
    private EquivClass<ShapeNode> getReverseEc(EquivClass<ShapeNode> ecS) {
        assert this.graph instanceof Shape : "Invalid method call.";
        EquivClass<ShapeNode> rEc = new EquivClass<ShapeNode>();
        for (Entry<HostNode,ShapeNode> entry : this.nodeShaping.entrySet()) {
            if (ecS.contains(entry.getValue())) {
                rEc.add((ShapeNode) entry.getKey());
            }
        }
        return rEc;
    }

    /** Returns the node in the shaping relation mapped by the given key. */
    private ShapeNode getShapeNode(Node node) {
        return this.nodeShaping.get(node);
    }

    /**
     * Constructs and returns an EdgeSignature from the given edge. The source
     * of the edge is taken to be the node of the signature.
     */
    public EdgeSignature getEdgeOutSignature(ShapeEdge edge) {
        EquivClass<ShapeNode> ec = this.getEquivClassOf(edge.target());
        return this.getEdgeSignature(edge.source(), edge.label(), ec);
    }

    /**
     * Constructs and returns an EdgeSignature from the given edge. The target
     * of the edge is taken to be the node of the signature.
     */
    public EdgeSignature getEdgeInSignature(ShapeEdge edge) {
        EquivClass<ShapeNode> ec = this.getEquivClassOf(edge.source());
        return this.getEdgeSignature(edge.target(), edge.label(), ec);
    }

    /**
     * Produces an EdgeSignature object with the information given as
     * parameters. To avoid duplication of objects, this method looks for an
     * already existing signature in the shape signature set. If the signature
     * object has to be created, then it is stored in this set. 
     */
    public EdgeSignature getEdgeSignature(ShapeNode node, TypeLabel label,
            EquivClass<ShapeNode> ec) {
        EdgeSignature newEs = new EdgeSignature(node, label, ec);
        EdgeSignature result = null;
        for (EdgeSignature es : this.edgeSigSet) {
            if (es.equals(newEs)) {
                result = es;
                break;
            }
        }
        if (result == null) {
            this.edgeSigSet.add(newEs);
            result = newEs;
        }
        return result;
    }

    /**
     * Returns the edge signature object used in the shape that matches
     * the one given.
     */
    public EdgeSignature getEdgeSignature(EdgeSignature es) {
        ShapeNode node = es.getNode();
        TypeLabel label = es.getLabel();
        EquivClass<ShapeNode> ec =
            this.getEquivClassOf(es.getEquivClass().iterator().next());
        return this.getEdgeSignature(node, label, ec);
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
        assert !this.isFrozen();
        EquivClass<ShapeNode> newEc = new EquivClass<ShapeNode>();
        newEc.add(node);
        this.equivRel.add(newEc);
        return newEc;
    }

    /** Basic getter method. */
    public Set<EdgeSignature> getEdgeSigSet() {
        return this.edgeSigSet;
    }

    /** Basic getter method. */
    public Multiplicity getEdgeSigOutMult(EdgeSignature es) {
        Multiplicity mult = this.outEdgeMultMap.get(es);
        if (mult == null) {
            mult = Multiplicity.getMultOf(0);
            // Check the frozen edges.
            for (ShapeEdge frozenEdge : this.frozenEdges) {
                if (es.asOutSigContains(frozenEdge)) {
                    mult = Multiplicity.getMultOf(1);
                    break;
                }
            }
        }
        return mult;
    }

    /** Basic getter method. */
    public Multiplicity getEdgeSigInMult(EdgeSignature es) {
        Multiplicity mult = this.inEdgeMultMap.get(es);
        if (mult == null) {
            mult = Multiplicity.getMultOf(0);
            // Check the frozen edges.
            for (ShapeEdge frozenEdge : this.frozenEdges) {
                if (es.asInSigContains(frozenEdge)) {
                    mult = Multiplicity.getMultOf(1);
                    break;
                }
            }
        }
        return mult;
    }

    /** Returns all pre-matches of the given rule into the shape. */
    public Set<RuleMatch> getPreMatches(Rule rule) {
        return PreMatch.getPreMatches(this, rule);
    }

    /**
     * Returns true if the given match in the host is a valid pre-match.
     * A pre-match is valid if the non-injective matching of the LHS
     * respects node and edge multiplicities.
     */
    public boolean isValidPreMatch(RuleEvent event) {
        return PreMatch.isValidPreMatch(this, event);
    }

    /** Duplicate all unary edges occurring in the given 'from' node. */
    private void copyUnaryEdges(ShapeNode from, ShapeNode to) {
        assert !this.isFrozen();
        for (HostEdge edge : this.outEdgeSet(from)) {
            if (Util.isUnary(edge)) {
                TypeLabel label = edge.label();
                this.addEdge(to, label, to);
            }
        }
    }

    /** Returns the set of binary edges with the given node as source. */
    public Set<ShapeEdge> outBinaryEdgeSet(ShapeNode source) {
        Set<ShapeEdge> result = new HashSet<ShapeEdge>();
        for (ShapeEdge edge : this.outEdgeSet(source)) {
            if (!Util.isUnary(edge)) {
                result.add(edge);
            }
        }
        return result;
    }

    /** Returns the set of binary edges with the given node as target. */
    public Set<ShapeEdge> inBinaryEdgeSet(ShapeNode target) {
        Set<ShapeEdge> result = new HashSet<ShapeEdge>();
        for (ShapeEdge edge : this.edgeSet(target)) {
            if (!Util.isUnary(edge) && edge.target().equals(target)) {
                result.add(edge);
            }
        }
        return result;
    }

    /** The method name is self-explanatory. */
    public void setShapeAndCreateIdentityMorphism(HostGraph graph) {
        assert !this.isFrozen();
        assert graph instanceof Shape : "Cannot create a shaping morphism from a non-abstract graph.";

        this.graph = graph;

        // Clear the old shaping map.
        this.nodeShaping.clear();
        this.edgeShaping.clear();

        // Create identity node morphism.
        for (HostNode node : this.graph.nodeSet()) {
            this.nodeShaping.put(node, (ShapeNode) node);
        }

        // Create identity edge morphism.
        for (HostEdge edge : this.graph.edgeSet()) {
            this.edgeShaping.put(edge, (ShapeEdge) edge);
        }
    }

    /**
     * Updates all the entries in the hash maps of this shape. This has to be
     * done because the equivalence relation and the edge signatures of the
     * shape may be modified and their hash code may change. This is an ugly
     * hack that hurts performance a lot but at least it solves the problem for
     * now...
     */
    private void updateHashMaps() {
        EquivRelation<ShapeNode> newEquivRel = new EquivRelation<ShapeNode>();
        HashMap<EdgeSignature,Multiplicity> newOutEdgeMultMap =
            new HashMap<EdgeSignature,Multiplicity>();
        HashMap<EdgeSignature,Multiplicity> newInEdgeMultMap =
            new HashMap<EdgeSignature,Multiplicity>();
        newEquivRel.addAll(this.equivRel);
        newOutEdgeMultMap.putAll(this.outEdgeMultMap);
        newInEdgeMultMap.putAll(this.inEdgeMultMap);
        this.equivRel = newEquivRel;
        this.outEdgeMultMap = newOutEdgeMultMap;
        this.inEdgeMultMap = newInEdgeMultMap;
    }

    /**
     * Materialises a node in the shape. Note that all edges adjacent to the
     * node being materialised are also duplicated.
     * @param nodeS - the node in the shape to be materialised.
     * @param mult - the multiplicity for each materialised node.
     * @param copies - the number of new nodes that will be produced from
     *                 nodeS.
     * @return a set of multiplicities that the given node may have in
     *         the materialisation.
     */
    public Set<Multiplicity> materialiseNode(ShapeNode nodeS,
            Multiplicity mult, int copies) {
        assert !this.isFrozen();
        assert this.nodeSet().contains(nodeS);
        assert mult != null && mult.isPositive();
        assert copies > 0;

        ShapeNode origNode = nodeS;

        // Create the new nodes.
        ShapeNode newNodes[] = new ShapeNode[copies];
        EquivClass<ShapeNode> origNodeEc = this.getEquivClassOf(origNode);
        for (int i = 0; i < copies; i++) {
            ShapeNode newNode = getFactory().createNode();
            newNodes[i] = newNode;
            // Add the new node to the shape. Call the super method because
            // we have additional information on the node to be added.
            super.addNode(newNode);
            // The new node is concrete so set its multiplicity to one.
            this.setNodeMult(newNode, mult);
            // Copy the labels from the original node.
            this.copyUnaryEdges(origNode, newNode);
            // Add the new node to the equivalence class of the original node.
            origNodeEc.add(newNode);
            // Update the shaping morphism.
            this.nodeShaping.put(newNode, origNode);
        }

        // We modified the equivalence classes, so the hash codes for the
        // edge signatures have changed.
        this.updateHashMaps();

        // Now that we have all new nodes, duplicate all incoming and
        // outgoing edges were the original node occurs. Also, update
        // all maps of the shape accordingly.

        // Outgoing edges with origNode as source.
        Set<ShapeEdge> newEdges = new HashSet<ShapeEdge>(copies);
        for (ShapeEdge origEdge : this.outBinaryEdgeSet(origNode)) {
            TypeLabel label = origEdge.label();
            ShapeNode target = origEdge.target();
            Multiplicity origEdgeOutMult = this.getEdgeOutMult(origEdge);
            for (ShapeNode newNode : newNodes) {
                ShapeEdge newEdge =
                    (ShapeEdge) this.createEdge(newNode, label, target);
                newEdges.add(newEdge);
                // Update the outgoing multiplicity map.
                EdgeSignature newEdgeSig = this.getEdgeOutSignature(newEdge);
                this.setEdgeOutMult(newEdgeSig, origEdgeOutMult);
                // Update the shaping morphism.
                this.edgeShaping.put(newEdge, origEdge);
            }
        }
        // We add the new edges in the end to avoid concurrent modification
        // errors from the iterator in the loop.
        this.addEdgeSetWithoutCheck(newEdges);

        // Incoming edges with origNode as target.
        newEdges.clear();
        for (ShapeEdge origEdge : this.inBinaryEdgeSet(origNode)) {
            TypeLabel label = origEdge.label();
            ShapeNode source = origEdge.source();
            Multiplicity origEdgeInMult = this.getEdgeInMult(origEdge);
            for (ShapeNode newNode : newNodes) {
                ShapeEdge newEdge =
                    (ShapeEdge) this.createEdge(source, label, newNode);
                newEdges.add(newEdge);
                // Update the incoming multiplicity map.
                EdgeSignature newEdgeSig = this.getEdgeInSignature(newEdge);
                this.setEdgeInMult(newEdgeSig, origEdgeInMult);
                // Update the shaping morphism.
                this.edgeShaping.put(newEdge, origEdge);
            }
        }
        // We add the new edges in the end to avoid concurrent modification
        // errors from the iterator in the loop.
        this.addEdgeSetWithoutCheck(newEdges);

        // Basic consistency check.
        this.checkShapeInvariant();

        // OK, we materialised the node, now create the multiplicity set
        // that will come out from the original node.
        Multiplicity toSub = Multiplicity.getMultOf(copies).multiply(mult);
        Set<Multiplicity> mults = this.getNodeMult(origNode).subNodeMult(toSub);
        return mults;
    }

    /** Basic getter method. */
    public Map<HostNode,ShapeNode> getNodeShaping() {
        return this.nodeShaping;
    }

    /** Basic getter method. */
    public Map<HostEdge,ShapeEdge> getEdgeShaping() {
        return this.edgeShaping;
    }

    /** Basic getter method. */
    public ShapeEdge getShapeEdge(ShapeNode source, TypeLabel label,
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
    public EquivRelation<ShapeNode> getEquivRelation() {
        return this.equivRel;
    }

    /** Basic getter method. */
    public Map<EdgeSignature,Multiplicity> getOutEdgeMultMap() {
        return this.outEdgeMultMap;
    }

    /** Basic getter method. */
    public Map<EdgeSignature,Multiplicity> getInEdgeMultMap() {
        return this.inEdgeMultMap;
    }

    /**
     * Returns true if the number of outgoing edges from the signature
     * occurring in the shape is one.
     */
    public boolean isOutEdgeSigUnique(EdgeSignature es) {
        return this.getOutEdgeSigCount(es) == 1;
    }

    /**
     * Returns the number of outgoing edges from the signature occurring in
     * the shape.
     */
    private int getOutEdgeSigCount(EdgeSignature es) {
        int edgeCount = 0;
        ShapeNode source = es.getNode();
        TypeLabel label = es.getLabel();
        for (ShapeNode target : es.getEquivClass()) {
            ShapeEdge edge = this.getShapeEdge(source, label, target);
            if (edge != null && !this.isFrozen(edge)) {
                edgeCount++;
                if (edgeCount > 1) {
                    break;
                }
            }
        }
        return edgeCount;
    }

    /**
     * Returns true if the number of incoming edges from the signature
     * occurring in the shape is one.
     */
    public boolean isInEdgeSigUnique(EdgeSignature es) {
        return this.getInEdgeSigCount(es) == 1;
    }

    /**
     * Returns the number of incoming edges from the signature occurring in
     * the shape.
     */
    private int getInEdgeSigCount(EdgeSignature es) {
        int edgeCount = 0;
        ShapeNode target = es.getNode();
        TypeLabel label = es.getLabel();
        for (ShapeNode source : es.getEquivClass()) {
            ShapeEdge edge = this.getShapeEdge(source, label, target);
            if (edge != null && !this.isFrozen(edge)) {
                edgeCount++;
                if (edgeCount > 1) {
                    break;
                }
            }
        }
        return edgeCount;
    }

    /**
     * Returns true if the outgoing multiplicity of the given edge
     * signature is one.
     */
    public boolean isOutEdgeSigConcrete(EdgeSignature es) {
        return this.getEdgeSigOutMult(es).equals(Multiplicity.getMultOf(1));
    }

    /**
     * Returns true if the incoming multiplicity of the given edge
     * signature is one.
     */
    public boolean isInEdgeSigConcrete(EdgeSignature es) {
        return this.getEdgeSigInMult(es).equals(Multiplicity.getMultOf(1));
    }

    /**
     * Getter method.
     * @param es - the edge signature to be searched.
     * @param outgoing - flag indicating if the given edge signature should be
     *                   used as outgoing or incoming.
     * @return all the edges of the shape that can be mapped to the given
     *         edge signature. Frozen edges are also returned.
     */
    public Set<ShapeEdge> getEdgesFrom(EdgeSignature es, boolean outgoing) {
        Set<ShapeEdge> result = new HashSet<ShapeEdge>();
        ShapeNode node = es.getNode();
        TypeLabel label = es.getLabel();
        EquivClass<ShapeNode> ec = es.getEquivClass();
        for (ShapeNode ecNode : ec) {
            ShapeEdge edge;
            if (outgoing) {
                edge = this.getShapeEdge(node, label, ecNode);
            } else { // incoming
                edge = this.getShapeEdge(ecNode, label, node);
            }
            if (edge != null) {
                result.add(edge);
            }
        }
        return result;
    }

    /**
     * Inspection method.
     * @param es - the edge signature to be searched.
     * @param edges - the set of edges to be checked for containment.
     * @param outgoing - flag indicating if the given edge signature should be
     *                   used as outgoing or incoming.
     * @return true if there is at least one edge in parameter 'edges' that can
     *         be mapped to the given edge signature. Frozen edges are also
     *         considered.
     */
    public boolean sigContains(EdgeSignature es, Set<ShapeEdge> edges,
            boolean outgoing) {
        boolean result = false;
        for (ShapeEdge edge : this.getEdgesFrom(es, outgoing)) {
            if (edges.contains(edge)) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Splits an equivalence class of the shape. The equivalence relation of
     * the shape and the edge multiplicity maps are all properly updated.
     * @param origEc - the original equivalence class to be split. Must be
     *                 present in the shape.
     * @param singEc - the singular equivalence class created by the
     *                 SingulariseNode operation. Must contain only one node.
     * @param remEc - the remaining equivalence class after singularisation.
     * It is required that origEc = singEc U remEc.
     */
    public void splitEc(EquivClass<ShapeNode> origEc,
            EquivClass<ShapeNode> singEc, EquivClass<ShapeNode> remEc) {
        assert !this.isFrozen();
        assert singEc.size() == 1 && origEc.containsAll(singEc)
            && origEc.containsAll(remEc);
        assert this.equivRel.contains(origEc);

        this.cleanEdgeSigSet();

        for (EdgeSignature origEs : this.getEdgeSignatures(origEc)) {
            ShapeNode origNode = origEs.getNode();
            TypeLabel label = origEs.getLabel();
            EdgeSignature remEs = new EdgeSignature(origNode, label, remEc);
            EdgeSignature singEs = new EdgeSignature(origNode, label, singEc);

            Multiplicity outMult = this.outEdgeMultMap.remove(origEs);
            if (outMult != null) {
                if (this.isNonFrozenEdgeFromSigInvolved(remEs, true)) {
                    this.outEdgeMultMap.put(remEs, outMult);
                }
                if (this.isNonFrozenEdgeFromSigInvolved(singEs, true)) {
                    this.outEdgeMultMap.put(singEs, outMult);
                }
            }

            Multiplicity inMult = this.inEdgeMultMap.remove(origEs);
            if (inMult != null) {
                if (this.isNonFrozenEdgeFromSigInvolved(remEs, false)) {
                    this.inEdgeMultMap.put(remEs, inMult);
                }
                if (this.isNonFrozenEdgeFromSigInvolved(singEs, false)) {
                    this.inEdgeMultMap.put(singEs, inMult);
                }
            }
        }

        this.equivRel.remove(origEc);
        this.equivRel.add(singEc);
        this.equivRel.add(remEc);
    }

    /**
     * Returns true if there is a non-frozen edge in the shape that is mapped
     * to the given edge signature and have the given node as source or target
     * (decided by the given outgoing flag).
     */
    public boolean isNonFrozenEdgeFromSigInvolved(EdgeSignature es,
            ShapeNode singNode, boolean outgoing) {
        boolean result = false;
        for (ShapeEdge edge : this.getEdgesFrom(es, outgoing)) {
            ShapeNode adjNode;
            if (outgoing) {
                adjNode = edge.target();
            } else { // incoming
                adjNode = edge.source();
            }
            if (!this.isFrozen(edge) && adjNode.equals(singNode)) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Returns true if there is a non-frozen edge in the shape that is mapped
     * to the given edge signature.
     */
    public boolean isNonFrozenEdgeFromSigInvolved(EdgeSignature es,
            boolean outgoing) {
        boolean result = false;
        for (ShapeEdge edge : this.getEdgesFrom(es, outgoing)) {
            if (!this.isFrozen(edge)) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Freezes the given edge in the shape. The given edge must be part of
     * the shape.
     * Recall that frozen edges are considered concrete, i.e., have outgoing
     * and incoming multiplicities equal to one. This invariant is not checked
     * by this method. It is assumed that either before or after calling the
     * method, the caller will ensure that the invariant is true.  
     */
    public void freeze(ShapeEdge edgeToFreeze) {
        assert !this.isFrozen();
        assert this.edgeSet().contains(edgeToFreeze);
        EdgeSignature outEs = this.getEdgeOutSignature(edgeToFreeze);
        if (this.isOutEdgeSigUnique(outEs)) {
            this.outEdgeMultMap.remove(outEs);
        }
        EdgeSignature inEs = this.getEdgeInSignature(edgeToFreeze);
        if (this.isInEdgeSigUnique(inEs)) {
            this.inEdgeMultMap.remove(inEs);
        }
        this.frozenEdges.add(edgeToFreeze);
    }

    /**
     * Freezes the given edges in the shape. The given edges must be part of
     * the shape.
     * Recall that frozen edges are considered concrete, i.e., have outgoing
     * and incoming multiplicities equal to one. This invariant is not check by
     * this method. It is assumed that either before or after calling the
     * method, the called will ensure that the invariant is true.  
     */
    public void freeze(Set<ShapeEdge> edgesToFreeze) {
        assert !this.isFrozen();
        for (ShapeEdge edgeToFreeze : edgesToFreeze) {
            this.freeze(edgeToFreeze);
        }
    }

    /** Freezes the shape, making it unmutable. */
    public void freeze() {
        this.frozen = true;
    }

    /** Returns true if the given edge is frozen in the shape. */
    public boolean isFrozen(ShapeEdge edge) {
        return this.frozenEdges.contains(edge);
    }

    /** Returns true is the shape is frozen. */
    public boolean isFrozen() {
        return this.frozen;
    }

    /**
     * Thaws the frozen edges of the shape. This is the last step of the
     * materialisation phase. The frozen edges are set to be normal edges
     * again and the edge multiplicity maps are properly updated.
     */
    public void unfreezeEdges() {
        assert !this.isFrozen();
        for (ShapeEdge frozenEdge : this.frozenEdges) {
            Multiplicity oneMult = Multiplicity.getMultOf(1);
            ShapeNode src = frozenEdge.source();
            ShapeNode tgt = frozenEdge.target();
            TypeLabel label = frozenEdge.label();
            EquivClass<ShapeNode> srcEc = this.getEquivClassOf(src);
            EquivClass<ShapeNode> tgtEc = this.getEquivClassOf(tgt);

            assert srcEc.size() == 1 || tgtEc.size() == 1;

            EdgeSignature outEs = this.getEdgeSignature(src, label, tgtEc);
            if (!this.outEdgeMultMap.containsKey(outEs)) {
                this.setEdgeOutMult(outEs, oneMult);
            }
            EdgeSignature inEs = this.getEdgeSignature(tgt, label, srcEc);
            if (!this.inEdgeMultMap.containsKey(inEs)) {
                this.setEdgeInMult(inEs, oneMult);
            }
        }
        this.frozenEdges.clear();
    }

    /** Normalise the shape object and returns the newly modified shape. */
    public Shape normalise() {
        assert !this.isFrozen();
        Shape normalisedShape = new Shape();
        normalisedShape.buildShapeFromShape(this);
        return normalisedShape;
    }

    /**
     * Returns true if the given this object and the given shape are isomorphic.
     * Two shapes are isomorphic if:
     * (0) their underlying graph structures are isomorphic;
     * (1) they have the same node multiplicities;
     * (2) they have the same outgoing and incoming edge multiplicities; and
     * (3) they have the same equivalence relation. 
     */
    public boolean isIsomorphicTo(Shape other) {
        boolean result = false;
        IsoCheckerState state = new IsoCheckerState();
        DefaultIsoChecker isoChecker = DefaultIsoChecker.getInstance(true);
        NodeEdgeMap morphism = isoChecker.getIsomorphism(this, other, state);
        while (morphism != null) {
            // We found an isomorphism between the graph structures.
            // Check for the extra conditions.
            if (this.isValidIsomorphism(morphism, other)) {
                // Valid shape isomorphism.
                result = true;
                break;
            } else {
                // Keep trying.
                morphism = isoChecker.getIsomorphism(this, other, state);
                if (state.isPlanEmpty()) {
                    // We got the same morphism back. The check fails.
                    result = false;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Returns true if the given pre-isomorphism is a valid shape isomorphism.
     * Two shapes are isomorphic if:
     * (0) their underlying graph structures are isomorphic (the given morphism);
     * (1) they have the same node multiplicities;
     * (2) they have the same outgoing and incoming edge multiplicities; and
     * (3) they have the same equivalence relation. 
     */
    private boolean isValidIsomorphism(NodeEdgeMap morphism, Shape shape) {
        // First check the node multiplicities.
        boolean complyToNodeMult = true;
        for (Entry<Node,Node> nodeEntry : morphism.nodeMap().entrySet()) {
            ShapeNode domNode = (ShapeNode) nodeEntry.getKey();
            ShapeNode codNode = (ShapeNode) nodeEntry.getValue();
            Multiplicity domNMult = this.getNodeMult(domNode);
            Multiplicity codNMult = shape.getNodeMult(codNode);
            if (!domNMult.equals(codNMult)) {
                complyToNodeMult = false;
                break;
            }
        }

        // Now check the edge multiplicities.
        boolean complyToEdgeMult = true;
        if (complyToNodeMult) {
            for (Entry<Edge,Edge> edgeEntry : morphism.edgeMap().entrySet()) {
                ShapeEdge domEdge = (ShapeEdge) edgeEntry.getKey();
                ShapeEdge codEdge = (ShapeEdge) edgeEntry.getValue();
                // Outgoing multiplicities.
                Multiplicity domEOutMult = this.getEdgeOutMult(domEdge);
                Multiplicity codEOutMult = shape.getEdgeOutMult(codEdge);
                if (!domEOutMult.equals(codEOutMult)) {
                    complyToEdgeMult = false;
                    break;
                }
                // Incoming multiplicities.
                Multiplicity domEInMult = this.getEdgeInMult(domEdge);
                Multiplicity codEInMult = shape.getEdgeInMult(codEdge);
                if (!domEInMult.equals(codEInMult)) {
                    complyToEdgeMult = false;
                    break;
                }
            }
        }

        // Last, check the equivalence relation.
        boolean complyToEquivRel = true;
        if (complyToNodeMult && complyToEdgeMult) {
            if (this.equivRel.size() != shape.equivRel.size()) {
                complyToEquivRel = false;
            } else {
                EquivClass<ShapeNode> mappedCodEc = new EquivClass<ShapeNode>();
                for (EquivClass<ShapeNode> domEc : this.equivRel) {
                    ShapeNode codNode = null;
                    for (ShapeNode domNode : domEc) {
                        codNode = (ShapeNode) morphism.getNode(domNode);
                        mappedCodEc.add(codNode);
                    }
                    EquivClass<ShapeNode> codEc =
                        shape.getEquivClassOf(codNode);
                    if (!codEc.equals(mappedCodEc)) {
                        complyToEquivRel = false;
                        break;
                    } else {
                        mappedCodEc.clear();
                    }
                }
            }
        }

        return complyToNodeMult && complyToEdgeMult && complyToEquivRel;
    }

    /**
     * Returns the set of edge signatures that have the given equivalence class.
     */
    public Set<EdgeSignature> getEdgeSignatures(EquivClass<ShapeNode> ec) {
        Set<EdgeSignature> result = new HashSet<EdgeSignature>();
        for (EdgeSignature es : this.edgeSigSet) {
            if (ec.equals(es.getEquivClass())) {
                result.add(es);
            }
        }
        return result;
    }

    /**
     * Check if the shape is in a state that complies to the shape invariant.
     * See last item of Def. 7, pg. 10.
     */
    public void checkShapeInvariant() {
        // We don't want to run this if the assertions are off, since nothing
        // will happen... This makes the code run faster.
        // EDUARDO: Refactor this method to make it return a boolean.
        if (!assertsEnabled) {
            return;
        }

        // For all labels.
        for (TypeLabel label : Util.binaryLabelSet(this)) {
            // For all nodes in the shape.
            for (ShapeNode node : this.nodeSet()) {
                // For all equivalence classes.
                for (EquivClass<ShapeNode> ec : this.equivRel) {
                    EdgeSignature es = this.getEdgeSignature(node, label, ec);

                    // Check outgoing multiplicities.
                    Multiplicity sigOutMult = this.getEdgeSigOutMult(es);
                    Multiplicity interOutMult =
                        Multiplicity.getEdgeSetMult(Util.getIntersectEdges(
                            this, node, ec.downcast(), label));
                    if ((sigOutMult.isZero() && interOutMult.isPositive())
                        || (interOutMult.isZero() && sigOutMult.isPositive())) {
                        assert false : "Violation of outgoing multiplicities";
                    }

                    // Check incoming multiplicities.
                    Multiplicity sigInMult = this.getEdgeSigInMult(es);
                    Multiplicity interInMult =
                        Multiplicity.getEdgeSetMult(Util.getIntersectEdges(
                            this, ec.downcast(), node, label));
                    if ((sigInMult.isZero() && interInMult.isPositive())
                        || (interInMult.isZero() && sigInMult.isPositive())) {
                        assert false : "Violation of incoming multiplicities";
                    }
                }
            }
        }
        this.cleanEdgeSigSet();
    }

}
