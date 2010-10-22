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

import groove.graph.DefaultGraph;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.NodeEdgeMap;
import groove.graph.iso.DefaultIsoChecker;
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
public class Shape extends DefaultGraph implements Cloneable {

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    /**
     * The graph (or shape) from which this shape was constructed. Note that
     * this is NOT the underlying graph structure of the shape but instead a
     * reference to the object that gave rise to this current shape.
     */
    private Graph graph;

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
    private final Map<Node,ShapeNode> nodeShaping;
    private final Map<Edge,ShapeEdge> edgeShaping;

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

    /** Set of frozen edges of the shape, i.e., the edges that are guaranteed
     *  to exist. This imply also that they are concrete, i.e., have all
     *  multiplicities equal to one.
     */
    private final Set<ShapeEdge> frozenEdges;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Default constructor. Creates a shape from a concrete graph. */
    public Shape(Graph graph) {
        super();
        this.graph = graph;
        this.nodeShaping = new HashMap<Node,ShapeNode>();
        this.edgeShaping = new HashMap<Edge,ShapeEdge>();
        this.equivRel = new EquivRelation<ShapeNode>();
        this.nodeMultMap = new HashMap<ShapeNode,Multiplicity>();
        this.outEdgeMultMap = new HashMap<EdgeSignature,Multiplicity>();
        this.inEdgeMultMap = new HashMap<EdgeSignature,Multiplicity>();
        this.edgeSigSet = new HashSet<EdgeSignature>();
        this.frozenEdges = new HashSet<ShapeEdge>();
        this.buildShape(false);
    }

    /**
     * Empty constructor. After creating an object with this constructor
     * method buildShapeFromShape should be called.
     */
    private Shape() {
        super();
        this.graph = null;
        this.nodeShaping = new HashMap<Node,ShapeNode>();
        this.edgeShaping = new HashMap<Edge,ShapeEdge>();
        this.equivRel = new EquivRelation<ShapeNode>();
        this.nodeMultMap = new HashMap<ShapeNode,Multiplicity>();
        this.outEdgeMultMap = new HashMap<EdgeSignature,Multiplicity>();
        this.inEdgeMultMap = new HashMap<EdgeSignature,Multiplicity>();
        this.edgeSigSet = new HashSet<EdgeSignature>();
        this.frozenEdges = new HashSet<ShapeEdge>();
    }

    /** Copying constructor. Clones all structures of the shape. */
    private Shape(Shape shape) {
        super(shape);
        this.graph = shape.graph;
        this.nodeShaping = new HashMap<Node,ShapeNode>(shape.nodeShaping);
        this.edgeShaping = new HashMap<Edge,ShapeEdge>(shape.edgeShaping);
        this.nodeMultMap =
            new HashMap<ShapeNode,Multiplicity>(shape.nodeMultMap);
        this.equivRel = new EquivRelation<ShapeNode>(shape.equivRel);
        this.frozenEdges = new HashSet<ShapeEdge>(shape.frozenEdges);

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
    public ShapeNode createNode() {
        return (ShapeNode) super.createNode(ShapeNode.CONS);
    }

    @Override
    public ShapeEdge createEdge(Node source, Label label, Node target) {
        return (ShapeEdge) super.createEdge(source, label, target,
            ShapeEdge.CONS);
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
            sb.append("  " + this.getEdgeOutMult(e) + ":" + edge + ":"
                + this.getEdgeInMult(e) + "\n");
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
    public boolean addNode(Node node) {
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
    public boolean addEdgeWithoutCheck(Edge edge) {
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
    public boolean removeNode(Node node) {
        assert this.nodeSet().contains(node) : "Cannot remove non-existent node.";
        ShapeNode nodeS = (ShapeNode) node;

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
    public boolean removeEdge(Edge edge) {
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
    public boolean removeNodeSetWithoutCheck(Collection<Node> nodeSet) {
        boolean removed = false;
        for (Node node : nodeSet) {
            removed |= this.removeNode(node);
        }
        return removed;
    }

    /** Compares two shapes for isomorphism. */
    @Override
    public boolean equals(Object o) {
        boolean result;
        if (!(o instanceof Shape)) {
            result = false;
        } else {
            Shape shape = (Shape) o;
            // Check first the graph structure of both shapes.
            NodeEdgeMap morphism = this.getPreIsomorphism(shape);
            if (morphism == null) {
                // The graph structure differs...
                result = false;
            } else {
                // The graph structure is isomorphic, so now check if the shape
                // constraints are equivalent. 
                result = this.isValidIsomorphism(morphism, shape);
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result =
            prime * result
                + ((this.equivRel == null) ? 0 : this.equivRel.hashCode());
        result =
            prime * result + ((this.graph == null) ? 0 : this.graph.hashCode());
        result =
            prime
                * result
                + ((this.inEdgeMultMap == null) ? 0
                        : this.inEdgeMultMap.hashCode());
        result =
            prime
                * result
                + ((this.nodeMultMap == null) ? 0 : this.nodeMultMap.hashCode());
        result =
            prime
                * result
                + ((this.outEdgeMultMap == null) ? 0
                        : this.outEdgeMultMap.hashCode());
        return result;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

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
        // First we create the equivalence relation for the nodes in the graph.
        GraphNeighEquiv prevGraphNeighEquiv = null;
        GraphNeighEquiv currGraphNeighEquiv;
        if (fromShape) {
            currGraphNeighEquiv = new ShapeNeighEquiv(this.graph);
        } else {
            currGraphNeighEquiv = new GraphNeighEquiv(this.graph);
        }
        // This loop is guaranteed to be executed at least once, because
        // we start at radius 0 and the abstraction radius is at least 1.
        while (currGraphNeighEquiv.getRadius() < Parameters.getAbsRadius()) {
            prevGraphNeighEquiv = (GraphNeighEquiv) currGraphNeighEquiv.clone();
            currGraphNeighEquiv.refineEquivRelation();
        }
        // At this point variable prevGraphNeighEquiv is no longer null.
        this.createShapeNodes(currGraphNeighEquiv, fromShape);
        this.createShapeNodesEquivRel(prevGraphNeighEquiv);
        this.createShapeEdges(currGraphNeighEquiv.getEdgesEquivRel());
        if (fromShape) {
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
        // Each node of the shape correspond to an equivalence class
        // of the graph.
        for (EquivClass<Node> nodeEquivClass : currGraphNeighEquiv) {
            ShapeNode shapeNode = this.createNode();
            // Add a shape node to the shape.
            // Call the super method because we have additional information on
            // the node to be added.
            super.addNode(shapeNode);
            // Update the shaping information.
            for (Node graphNode : nodeEquivClass) {
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
        // We use the previous (i-1) graph equivalence relation.
        for (EquivClass<Node> nodeEquivClass : prevGraphNeighEquiv) {
            EquivClass<ShapeNode> shapeEquivClass = new EquivClass<ShapeNode>();
            for (Node graphNode : nodeEquivClass) {
                shapeEquivClass.add(this.nodeShaping.get(graphNode));
            }
            this.equivRel.add(shapeEquivClass);
        }
    }

    /**
     * Creates the edges of this shape based on the equivalence relation given. 
     */
    private void createShapeEdges(EquivRelation<Edge> edgeEquivRel) {
        // Each edge of the shape correspond to an equivalence class
        // of the graph.
        for (EquivClass<Edge> edgeEquivClassG : edgeEquivRel) {
            // Get an arbitrary edge from the equivalence class.
            Edge edgeG = edgeEquivClassG.iterator().next();

            // Create and add a shape edge to the shape.
            Node srcG = edgeG.source();
            Node tgtG = edgeG.opposite();
            ShapeNode srcS = this.nodeShaping.get(srcG);
            ShapeNode tgtS = this.nodeShaping.get(tgtG);
            Label labelS = edgeG.label();
            ShapeEdge edgeS = this.createEdge(srcS, labelS, tgtS);
            this.addEdge(edgeS);

            // Update the shaping information.
            for (Edge eG : edgeEquivClassG) {
                this.edgeShaping.put(eG, edgeS);
            }
        }
    }

    /**
     * Creates the edge multiplicity maps from a graph neighbourhood relation.
     */
    private void createEdgeMultMaps(GraphNeighEquiv currGraphNeighEquiv) {
        // For all nodes in the graph.
        for (Node node : this.graph.nodeSet()) {
            ShapeNode nodeS = this.getShapeNode(node);
            // For all binary labels.
            for (Edge edge : this.graph.edgeSet(node)) {
                if (Util.isUnary(edge)) {
                    // EZ says: I don't like this jump, but if I don't use it,
                    // I will have one extra indentation level, which makes
                    // the code look like crap...
                    continue;
                } // else, we have a binary edge.

                Label label = edge.label();
                // For all equivalence classes in the shape.
                for (EquivClass<ShapeNode> ecS : this.equivRel) {
                    Set<Node> nodesG = this.getReverseNodeMap(ecS);
                    EdgeSignature es = this.getEdgeSignature(nodeS, label, ecS);

                    // Outgoing multiplicity.
                    Set<Edge> outInter =
                        Util.getIntersectEdges(this.graph, node, nodesG, label);
                    Multiplicity outMult =
                        Multiplicity.getEdgeSetMult(outInter);
                    // Incoming multiplicity.
                    Set<Edge> inInter =
                        Util.getIntersectEdges(this.graph, nodesG, node, label);
                    Multiplicity inMult = Multiplicity.getEdgeSetMult(inInter);

                    if (outMult.isPositive()) {
                        this.setEdgeOutMult(es, outMult);
                    } // else don't store multiplicity zero.
                    if (inMult.isPositive()) {
                        this.setEdgeInMult(es, inMult);
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
        // Original shape (S).
        Shape origShape = (Shape) this.graph;
        // For all the nodes in the new shape (T).
        for (ShapeNode nodeT : this.nodeSet()) {
            // First get a node from S that is part of the equivalence class
            // that corresponds to nodeT. We may take any node from such a
            // class because all nodes of the class have the same multiplicity
            // sum. This was checked when the neighbourhood equivalence
            // relation was built.
            ShapeNode nodeS = this.getReverseNodeMap(nodeT).iterator().next();

            // For all binary labels.
            for (ShapeEdge edge : this.edgeSet(nodeT)) {
                if (Util.isUnary(edge)) {
                    // EZ says: I don't like this jump, but if I don't use it,
                    // I will have one extra indentation level, which makes
                    // the code look like crap...
                    continue;
                } // else, we have a binary edge.

                Label label = edge.label();
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
                        this.setEdgeOutMult(es, outMult);
                    } // else don't store multiplicity zero.
                    if (inMult.isPositive()) {
                        this.setEdgeInMult(es, inMult);
                    } // else don't store multiplicity zero.
                }
            }
        }
        this.cleanEdgeSigSet();
    }

    /** Clears the edge signature set of spurious signatures. */
    private void cleanEdgeSigSet() {
        this.edgeSigSet.clear();
        this.edgeSigSet.addAll(this.outEdgeMultMap.keySet());
        this.edgeSigSet.addAll(this.inEdgeMultMap.keySet());
    }

    /**
     * Sets the node multiplicity. If the multiplicity given is zero, then
     * the node is removed from the shape.
     */
    public void setNodeMult(ShapeNode node, Multiplicity mult) {
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
            mult = this.outEdgeMultMap.get(es);
            if (mult == null) {
                mult = Multiplicity.getMultOf(0);
            }
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
            mult = this.inEdgeMultMap.get(es);
            if (mult == null) {
                mult = Multiplicity.getMultOf(0);
            }
        }
        return mult;
    }

    /**
     * Returns the set of nodes in the shaping relation that maps to values
     * occurring in the given equivalence class. 
     */
    private Set<Node> getReverseNodeMap(EquivClass<ShapeNode> ecS) {
        Set<Node> nodesG = new HashSet<Node>();
        for (Entry<Node,ShapeNode> entry : this.nodeShaping.entrySet()) {
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
        for (Entry<Node,ShapeNode> entry : this.nodeShaping.entrySet()) {
            if (nodeS.equals(entry.getValue())) {
                nodesG.add((ShapeNode) entry.getKey());
            }
        }
        return nodesG;
    }

    /**
     * Returns the set of edges in the shaping relation that maps to the
     * given edges.
     * It is an error to call this method if this.graph is not a Shape. 
     */
    public Set<ShapeEdge> getReverseEdgeMap(ShapeEdge edgeS) {
        assert this.graph instanceof Shape : "Invalid method call.";
        Set<ShapeEdge> edgesG = new HashSet<ShapeEdge>();
        for (Entry<Edge,ShapeEdge> entry : this.edgeShaping.entrySet()) {
            if (edgeS.equals(entry.getValue())) {
                edgesG.add((ShapeEdge) entry.getKey());
            }
        }
        return edgesG;
    }

    /**
     * Returns the equivalence class in the shaping relation that maps to values
     * occurring in the given equivalence class.
     * It is an error to call this method if this.graph is not a Shape. 
     */
    private EquivClass<ShapeNode> getReverseEc(EquivClass<ShapeNode> ecS) {
        assert this.graph instanceof Shape : "Invalid method call.";
        EquivClass<ShapeNode> rEc = new EquivClass<ShapeNode>();
        for (Entry<Node,ShapeNode> entry : this.nodeShaping.entrySet()) {
            if (ecS.contains(entry.getValue())) {
                rEc.add((ShapeNode) entry.getKey());
            }
        }
        return rEc;
    }

    /**
     * Returns the node in the shaping relation mapped by the given key.
     */
    private ShapeNode getShapeNode(Node node) {
        return this.nodeShaping.get(node);
    }

    /**
     * Constructs and returns an EdgeSignature from the given edge. The source
     * of the edge is taken to be the node of the signature.
     */
    public EdgeSignature getEdgeOutSignature(ShapeEdge edge) {
        EquivClass<ShapeNode> ec = this.getEquivClassOf(edge.opposite());
        return this.getEdgeSignature(edge.source(), edge.label(), ec);
    }

    /**
     * Constructs and returns an EdgeSignature from the given edge. The target
     * of the edge is taken to be the node of the signature.
     */
    public EdgeSignature getEdgeInSignature(ShapeEdge edge) {
        EquivClass<ShapeNode> ec = this.getEquivClassOf(edge.source());
        return this.getEdgeSignature(edge.opposite(), edge.label(), ec);
    }

    /**
     * Produces an EdgeSignature object with the information given as
     * parameters. To avoid duplication of objects, this method looks for an
     * already existing signature in the shape signature set. If the signature
     * object has to be created, then it is stored in this set. 
     */
    public EdgeSignature getEdgeSignature(ShapeNode node, Label label,
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
        Label label = es.getLabel();
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
            for (ShapeEdge edge : this.frozenEdges) {
                if (es.asOutSigContains(edge)) {
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
            for (ShapeEdge edge : this.frozenEdges) {
                if (es.asInSigContains(edge)) {
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
     * respects node multiplicities.
     */
    public boolean isValidPreMatch(RuleEvent event) {
        return PreMatch.isValidPreMatch(this, event);
    }

    /** Duplicate all unary edges occurring in the given 'from' node. */
    private void copyUnaryEdges(ShapeNode from, ShapeNode to) {
        for (Edge edge : this.outEdgeSet(from)) {
            if (Util.isUnary(edge)) {
                Label label = edge.label();
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
            if (!Util.isUnary(edge) && edge.opposite().equals(target)) {
                result.add(edge);
            }
        }
        return result;
    }

    /** The method name is self-explanatory. */
    public void setShapeAndCreateIdentityMorphism(Graph graph) {
        assert graph instanceof Shape : "Cannot create a shaping morphism from a non-abstract graph.";

        this.graph = graph;

        // Clear the old shaping map.
        this.nodeShaping.clear();
        this.edgeShaping.clear();

        // Create identity node morphism.
        for (Node node : this.graph.nodeSet()) {
            this.nodeShaping.put(node, (ShapeNode) node);
        }

        // Create identity edge morphism.
        for (Edge edge : this.graph.edgeSet()) {
            this.edgeShaping.put(edge, (ShapeEdge) edge);
        }
    }

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
        assert this.nodeSet().contains(nodeS);
        assert mult != null && mult.isPositive();
        assert copies > 0;

        ShapeNode origNode = nodeS;

        // Create the new nodes.
        ShapeNode newNodes[] = new ShapeNode[copies];
        EquivClass<ShapeNode> origNodeEc = this.getEquivClassOf(origNode);
        for (int i = 0; i < copies; i++) {
            ShapeNode newNode = this.createNode();
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
            Label label = origEdge.label();
            ShapeNode target = origEdge.opposite();
            Multiplicity origEdgeOutMult = this.getEdgeOutMult(origEdge);
            for (ShapeNode newNode : newNodes) {
                ShapeEdge newEdge = this.createEdge(newNode, label, target);
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
            Label label = origEdge.label();
            ShapeNode source = origEdge.source();
            Multiplicity origEdgeInMult = this.getEdgeInMult(origEdge);
            for (ShapeNode newNode : newNodes) {
                ShapeEdge newEdge = this.createEdge(source, label, newNode);
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
    public Map<Node,ShapeNode> getNodeShaping() {
        return this.nodeShaping;
    }

    /** Basic getter method. */
    public Map<Edge,ShapeEdge> getEdgeShaping() {
        return this.edgeShaping;
    }

    /** Basic getter method. */
    public ShapeEdge getShapeEdge(ShapeNode source, Label label,
            ShapeNode target) {
        ShapeEdge result = null;
        for (ShapeEdge edge : this.outEdgeSet(source)) {
            if (edge.label().equals(label) && edge.opposite().equals(target)) {
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
     * Returns true if the number of edges from the signature occurring in 
     * the shape is one.
     */
    public boolean isOutEdgeSigUnique(EdgeSignature es) {
        return this.getOutEdgeSigCount(es) == 1;
    }

    /** Returns the number of edges from the signature occurring in the shape. */
    private int getOutEdgeSigCount(EdgeSignature es) {
        int edgeCount = 0;
        ShapeNode source = es.getNode();
        Label label = es.getLabel();
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
     * Returns true if the number of edges from the signature occurring in 
     * the shape is one.
     */
    public boolean isInEdgeSigUnique(EdgeSignature es) {
        return this.getInEdgeSigCount(es) == 1;
    }

    /** Returns the number of edges from the signature occurring in the shape. */
    private int getInEdgeSigCount(EdgeSignature es) {
        int edgeCount = 0;
        ShapeNode target = es.getNode();
        Label label = es.getLabel();
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

    private Set<ShapeEdge> getEdgesFrom(EdgeSignature es, boolean outgoing) {
        Set<ShapeEdge> result = new HashSet<ShapeEdge>();
        ShapeNode node = es.getNode();
        Label label = es.getLabel();
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
     * EDUARDO: Comment this...
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
     * EDUARDO: Comment this...
     */
    public void splitEc(EquivClass<ShapeNode> origEc,
            EquivClass<ShapeNode> singEc, EquivClass<ShapeNode> remEc,
            boolean trivial) {
        assert singEc.size() == 1 && origEc.containsAll(singEc)
            && origEc.containsAll(remEc);
        assert this.equivRel.contains(origEc);

        this.cleanEdgeSigSet();

        for (EdgeSignature origEs : this.getEdgeSignatures(origEc)) {
            EdgeSignature newEs =
                new EdgeSignature(origEs.getNode(), origEs.getLabel(), remEc);
            Multiplicity outMult = this.outEdgeMultMap.remove(origEs);
            if (outMult != null) {
                this.outEdgeMultMap.put(newEs, outMult);
            }
            Multiplicity inMult = this.inEdgeMultMap.remove(origEs);
            if (inMult != null) {
                this.inEdgeMultMap.put(newEs, inMult);
            }
        }

        this.equivRel.remove(origEc);
        this.equivRel.add(singEc);
        this.equivRel.add(remEc);
    }

    /** EDUARDO: Comment this... */
    public boolean isNonFrozenEdgeFromSigInvolved(EdgeSignature es,
            ShapeNode singNode, boolean outgoing) {
        boolean result = false;
        ShapeNode node = es.getNode();
        Label label = es.getLabel();
        EquivClass<ShapeNode> ec = es.getEquivClass();
        for (ShapeNode adjNode : ec) {
            ShapeEdge edge;
            if (outgoing) {
                edge = this.getShapeEdge(node, label, adjNode);
            } else { // incoming
                edge = this.getShapeEdge(adjNode, label, node);
            }
            if (edge != null && !this.isFrozen(edge)
                && adjNode.equals(singNode)) {
                result = true;
                break;
            }
        }
        return result;
    }

    /** EDUARDO: Comment this... */
    public void freezeEdges(Set<ShapeEdge> edgesToFreeze) {
        for (ShapeEdge edgeToFreeze : edgesToFreeze) {
            assert this.edgeSet().contains(edgeToFreeze);
            EdgeSignature outEs = this.getEdgeOutSignature(edgeToFreeze);
            if (this.isOutEdgeSigUnique(outEs)) {
                this.outEdgeMultMap.remove(outEs);
            }
            EdgeSignature inEs = this.getEdgeInSignature(edgeToFreeze);
            if (this.isInEdgeSigUnique(inEs)) {
                this.inEdgeMultMap.remove(inEs);
            }
        }
        this.frozenEdges.addAll(edgesToFreeze);
    }

    /** Returns true if the given edge is frozen in the shape. */
    public boolean isFrozen(ShapeEdge edge) {
        assert this.edgeSet().contains(edge);
        return this.frozenEdges.contains(edge);
    }

    /** Normalise the shape object and returns the newly modified shape. */
    public Shape normalise() {
        Shape normalisedShape = new Shape();
        normalisedShape.buildShapeFromShape(this);
        return normalisedShape;
    }

    /**
     * Computes the pre-isomorphism between two shapes, i.e., the isomorphism
     * map between the underlying graph structures of the shapes.
     */
    private NodeEdgeMap getPreIsomorphism(Shape shape) {
        return DefaultIsoChecker.getInstance(true).getIsomorphism(this, shape);
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
     * Checks if the shape admits concretisations by looking at opposite
     * outgoing and incoming multiplicities from equivalence classes.
     * @return true if the multiplicity configuration is valid, false otherwise.
     */
    public boolean isAdmissible() {
        boolean result = true;
        // For all binary labels.
        outerLoop: for (Label label : Util.binaryLabelSet(this)) {
            // For all equivalence classes. (As outgoing)
            for (EquivClass<ShapeNode> ecO : this.equivRel) {
                // For all equivalence classes. (As incoming)
                for (EquivClass<ShapeNode> ecI : this.equivRel) {

                    // Compute the unbounded sum of the nodes on the outgoing
                    // equivalence class.
                    Multiplicity outMultSum = Multiplicity.getMultOf(0);
                    for (ShapeNode nO : ecO) {
                        Multiplicity nOMult = this.getNodeMult(nO);
                        EdgeSignature nOEs =
                            this.getEdgeSignature(nO, label, ecI);
                        Multiplicity eOMult = this.getEdgeSigOutMult(nOEs);
                        Multiplicity outMult = nOMult.multiply(eOMult);
                        outMultSum = outMultSum.uadd(outMult);
                    }

                    // Compute the unbounded sum of the nodes on the incoming
                    // equivalence class.
                    Multiplicity inMultSum = Multiplicity.getMultOf(0);
                    for (ShapeNode nI : ecI) {
                        Multiplicity nIMult = this.getNodeMult(nI);
                        EdgeSignature nIEs =
                            this.getEdgeSignature(nI, label, ecO);
                        Multiplicity eIMult = this.getEdgeSigInMult(nIEs);
                        Multiplicity inMult = nIMult.multiply(eIMult);
                        inMultSum = inMultSum.uadd(inMult);
                    }

                    if (!outMultSum.overlaps(inMultSum)) {
                        // Violation of condition.
                        result = false;
                        break outerLoop;
                    }
                }
            }
        }
        // Sanity check.
        if (result) {
            this.checkShapeInvariant();
        }
        return result;
    }

    /**
     * Check if the shape is in a state that complies to the shape invariant.
     * See last item of Def. 7, pg. 10.
     */
    public void checkShapeInvariant() {
        // For all nodes in the shape.
        for (ShapeNode node : this.nodeSet()) {
            // For all labels.
            for (Label label : Util.binaryLabelSet(this)) {
                // For all equivalence classes.
                for (EquivClass<ShapeNode> ec : this.equivRel) {
                    EdgeSignature es = this.getEdgeSignature(node, label, ec);
                    // Check outgoing multiplicities.
                    Multiplicity sigOutMult = this.getEdgeSigOutMult(es);
                    if (!sigOutMult.isPositive()) {
                        Multiplicity interOutMult =
                            Multiplicity.getEdgeSetMult(Util.getIntersectEdges(
                                this, node, ec.downcast(), label));
                        assert sigOutMult.equals(interOutMult) : "Violation of outgoing multiplicities";
                    }
                    // Check incoming multiplicities.
                    Multiplicity sigInMult = this.getEdgeSigInMult(es);
                    if (!sigInMult.isPositive()) {
                        Multiplicity interInMult =
                            Multiplicity.getEdgeSetMult(Util.getIntersectEdges(
                                this, ec.downcast(), node, label));
                        assert sigInMult.equals(interInMult) : "Violation of incoming multiplicities";
                    }
                }
            }
        }
        this.cleanEdgeSigSet();
    }

}
