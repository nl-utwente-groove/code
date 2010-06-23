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
import groove.graph.DeltaTarget;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Node;
import groove.trans.Rule;
import groove.trans.RuleMatch;
import groove.util.Pair;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * EDUARDO
 * This is where the magic happens... :P
 * @author Eduardo Zambon
 * @version $Revision $
 */
public class Shape extends DefaultGraph implements DeltaTarget {

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    private Graph graph;

    // EZ says to himself: BE CAREFUL HERE. Remember that:
    // - If 'this.graph' is an instance of a concrete graph, then the shaping
    //   relation is an abstraction morphism that goes from elements of
    //   'this.graph' to elements of 'this'.
    // - If 'this.graph' is an instance of a shape, then the shaping relation is
    //   a shaping morphism that goes in the OTHER direction, from elements of
    //   'this' to elements of 'this.graph'.
    // WTF??? EZ says to himself: actually the above is not true after
    // normalising a shape... :(
    private final Map<Node,ShapeNode> nodeShaping;
    private final Map<Edge,ShapeEdge> edgeShaping;

    private final EquivRelation<ShapeNode> equivRel;
    private final Map<ShapeNode,Multiplicity> nodeMultMap;
    private final Map<EdgeSignature,Multiplicity> outEdgeMultMap;
    private final Map<EdgeSignature,Multiplicity> inEdgeMultMap;
    private final Set<EdgeSignature> edgeSigSet;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** EDUARDO */
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
        this.buildShape(false);
    }

    private Shape() {
        // Empty constructor.
        // After creating an object with this constructor
        // method buildShapeFromShape should be called.
        super();
        this.graph = null;
        this.nodeShaping = new HashMap<Node,ShapeNode>();
        this.edgeShaping = new HashMap<Edge,ShapeEdge>();
        this.equivRel = new EquivRelation<ShapeNode>();
        this.nodeMultMap = new HashMap<ShapeNode,Multiplicity>();
        this.outEdgeMultMap = new HashMap<EdgeSignature,Multiplicity>();
        this.inEdgeMultMap = new HashMap<EdgeSignature,Multiplicity>();
        this.edgeSigSet = new HashSet<EdgeSignature>();
    }

    /** Copying constructor. */
    private Shape(Shape shape) {
        super(shape);
        this.graph = shape.graph;
        this.nodeShaping = new HashMap<Node,ShapeNode>(shape.nodeShaping);
        this.edgeShaping = new HashMap<Edge,ShapeEdge>(shape.edgeShaping);
        this.nodeMultMap =
            new HashMap<ShapeNode,Multiplicity>(shape.nodeMultMap);
        this.equivRel = new EquivRelation<ShapeNode>(shape.equivRel);

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
     * If you want to avoid the method side-effects and perform the book
     * keeping yourself, the call super.addNode(Node) . In this case, be careful
     * not to leave the shape structures in an inconsistent state...
     */
    @Override
    public boolean addNode(Node node) {
        assert node instanceof ShapeNode : "Invalid node type!";
        boolean added = super.addNode(node);
        if (added) {
            ShapeNode nodeS = (ShapeNode) node;
            this.setNodeMult(nodeS, Multiplicity.getMultOf(1));
            EquivClass<ShapeNode> newEc = new EquivClass<ShapeNode>();
            newEc.add(nodeS);
            this.equivRel.add(newEc);
        }
        return added;
    }

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

        // Remove node from equivalence relation.
        this.getEquivClassOf(nodeS).remove(nodeS);
        // Remove node from graph.
        return super.removeNodeWithoutCheck(node);
    }

    @Override
    public boolean removeEdge(Edge edge) {
        ShapeEdge edgeS = (ShapeEdge) edge;
        // Remove entry from edge shaping map.
        this.edgeShaping.remove(edgeS);
        // Update outgoing multiplicity map.
        EdgeSignature outEs = this.getEdgeOutSignature(edgeS);
        if (outEs.isUnique()) {
            this.outEdgeMultMap.remove(outEs);
        }
        // Update incoming multiplicity map.
        EdgeSignature inEs = this.getEdgeInSignature(edgeS);
        if (inEs.isUnique()) {
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

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    private void buildShapeFromShape(Shape origShape) {
        this.graph = origShape;
        this.buildShape(true);
    }

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
        this.createEdgeMultMaps(currGraphNeighEquiv, fromShape);
    }

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

    private void createShapeNodesEquivRel(GraphNeighEquiv prevGraphNeighEquiv) {
        // Create the equivalence relation between shape nodes.
        // We use the previous (i-1) graph equivalence relation.
        for (EquivClass<Node> nodeEquivClass : prevGraphNeighEquiv) {
            EquivClass<ShapeNode> shapeEquivClass = new EquivClass<ShapeNode>();
            for (Node graphNode : nodeEquivClass) {
                shapeEquivClass.add(this.nodeShaping.get(graphNode));
            }
            this.equivRel.add(shapeEquivClass);
        }
    }

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

    private void createEdgeMultMaps(GraphNeighEquiv currGraphNeighEquiv,
            boolean fromShape) {
        // For all labels.
        for (Label label : Util.binaryLabelSet(this.graph)) {
            // For all nodes in the graph.
            for (Node node : this.graph.nodeSet()) {
                // For all equivalence classes in the shape.
                for (EquivClass<ShapeNode> ecS : this.equivRel) {
                    Multiplicity outMult;
                    Multiplicity inMult;
                    ShapeNode nodeS = this.getShapeNode(node);
                    Set<Node> nodesG = this.getReverseNodeMap(ecS);
                    EdgeSignature es = this.getEdgeSignature(nodeS, label, ecS);

                    if (fromShape) {
                        Shape origShape = (Shape) this.graph;
                        // Compute the set of equivalence classes from the shape that
                        // we need to consider.
                        Set<EquivClass<ShapeNode>> kSet =
                            new HashSet<EquivClass<ShapeNode>>();
                        for (EquivClass<ShapeNode> possibleK : origShape.equivRel) {
                            if (nodesG.containsAll(possibleK)) {
                                kSet.add(possibleK);
                            }
                        }
                        // Calculate the sums.
                        outMult =
                            Multiplicity.sumOutMult(origShape,
                                (ShapeNode) node, label, kSet);
                        inMult =
                            Multiplicity.sumInMult(origShape, (ShapeNode) node,
                                label, kSet);

                    } else { // From graph.
                        // Outgoing multiplicity.
                        Set<Edge> outInter =
                            Util.getIntersectEdges(this.graph, node, nodesG,
                                label);
                        outMult = Multiplicity.getEdgeSetMult(outInter);
                        // Incoming multiplicity.
                        Set<Edge> inInter =
                            Util.getIntersectEdges(this.graph, nodesG, node,
                                label);
                        inMult = Multiplicity.getEdgeSetMult(inInter);
                    }

                    if (outMult.isPositive()) {
                        this.setEdgeOutMult(es, outMult);
                    } // else don't store multiplicity zero.
                    if (inMult.isPositive()) {
                        this.setEdgeInMult(es, inMult);
                    } // else don't store multiplicity zero.
                }
            }
        }
        // Clean the signature set.
        this.edgeSigSet.clear();
        this.edgeSigSet.addAll(this.outEdgeMultMap.keySet());
        this.edgeSigSet.addAll(this.inEdgeMultMap.keySet());
    }

    /** EDUARDO */
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

    /** EDUARDO */
    public void setEdgeOutMult(EdgeSignature es, Multiplicity mult) {
        this.outEdgeMultMap.put(es, mult);
    }

    /** EDUARDO */
    public void setEdgeInMult(EdgeSignature es, Multiplicity mult) {
        this.inEdgeMultMap.put(es, mult);
    }

    /** EDUARDO */
    public Multiplicity getNodeMult(ShapeNode node) {
        Multiplicity mult = this.nodeMultMap.get(node);
        if (mult == null) {
            mult = Multiplicity.getMultOf(0);
        }
        return mult;
    }

    /** EDUARDO */
    public Multiplicity getEdgeOutMult(ShapeEdge edge) {
        EdgeSignature es = this.getEdgeOutSignature(edge);
        Multiplicity mult = this.outEdgeMultMap.get(es);
        if (mult == null) {
            mult = Multiplicity.getMultOf(0);
        }
        return mult;
    }

    /** EDUARDO */
    public Multiplicity getEdgeInMult(ShapeEdge edge) {
        EdgeSignature es = this.getEdgeInSignature(edge);
        Multiplicity mult = this.inEdgeMultMap.get(es);
        if (mult == null) {
            mult = Multiplicity.getMultOf(0);
        }
        return mult;
    }

    private Set<Node> getReverseNodeMap(EquivClass<ShapeNode> ecS) {
        Set<Node> nodesG = new HashSet<Node>();
        for (Entry<Node,ShapeNode> entry : this.nodeShaping.entrySet()) {
            if (ecS.contains(entry.getValue())) {
                nodesG.add(entry.getKey());
            }
        }
        return nodesG;
    }

    private ShapeNode getShapeNode(Node node) {
        return this.nodeShaping.get(node);
    }

    /** EDUARDO */
    public EdgeSignature getEdgeOutSignature(ShapeEdge edge) {
        EquivClass<ShapeNode> ec = this.getEquivClassOf(edge.opposite());
        return this.getEdgeSignature(edge.source(), edge.label(), ec);
    }

    /** EDUARDO */
    public EdgeSignature getEdgeInSignature(ShapeEdge edge) {
        EquivClass<ShapeNode> ec = this.getEquivClassOf(edge.source());
        return this.getEdgeSignature(edge.opposite(), edge.label(), ec);
    }

    /** EDUARDO */
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

    /** EDUARDO */
    public EdgeSignature getEdgeSignature(EdgeSignature es) {
        ShapeNode node = es.getNode();
        Label label = es.getLabel();
        EquivClass<ShapeNode> ec =
            this.getEquivClassOf(es.getEquivClass().iterator().next());
        return this.getEdgeSignature(node, label, ec);
    }

    /** EDUARDO */
    public EquivClass<ShapeNode> getEquivClassOf(ShapeNode node) {
        assert this.nodeSet().contains(node) : "Node " + node
            + " is not in the shape!";
        return this.equivRel.getEquivClassOf(node);
    }

    /** EDUARDO */
    public Set<EdgeSignature> getEdgeSigSet() {
        return this.edgeSigSet;
    }

    /** EDUARDO */
    public Multiplicity getEdgeSigOutMult(EdgeSignature es) {
        Multiplicity mult = this.outEdgeMultMap.get(es);
        if (mult == null) {
            mult = Multiplicity.getMultOf(0);
        }
        return mult;
    }

    /** EDUARDO */
    public Multiplicity getEdgeSigInMult(EdgeSignature es) {
        Multiplicity mult = this.inEdgeMultMap.get(es);
        if (mult == null) {
            mult = Multiplicity.getMultOf(0);
        }
        return mult;
    }

    /** EDUARDO */
    public Set<RuleMatch> getPreMatches(Rule rule) {
        return PreMatch.getPreMatches(this, rule);
    }

    /** EDUARDO */
    public void copyUnaryEdges(ShapeNode from, ShapeNode to) {
        for (Edge edge : this.outEdgeSet(from)) {
            if (Util.isUnary(edge)) {
                Label label = edge.label();
                this.addEdge(to, label, to);
            }
        }
    }

    /** EDUARDO */
    public Set<ShapeEdge> outBinaryEdgeSet(ShapeNode source) {
        Set<ShapeEdge> result = new HashSet<ShapeEdge>();
        for (ShapeEdge edge : this.outEdgeSet(source)) {
            if (!Util.isUnary(edge)) {
                result.add(edge);
            }
        }
        return result;
    }

    /** EDUARDO */
    public Set<ShapeEdge> inBinaryEdgeSet(ShapeNode target) {
        Set<ShapeEdge> result = new HashSet<ShapeEdge>();
        for (ShapeEdge edge : this.edgeSet(target)) {
            if (!Util.isUnary(edge) && edge.opposite().equals(target)) {
                result.add(edge);
            }
        }
        return result;
    }

    /** EDUARDO */
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

    /** EDUARDO */
    public Set<Pair<ShapeNode,Set<Multiplicity>>> materialiseNodes(
            Set<Pair<ShapeNode,Integer>> nodes) {
        Set<Pair<ShapeNode,Set<Multiplicity>>> result =
            new HashSet<Pair<ShapeNode,Set<Multiplicity>>>();

        // For all nodes that need to be materialised.
        for (Pair<ShapeNode,Integer> pair : nodes) {
            ShapeNode origNode = pair.first();

            int numberNewNodes = pair.second().intValue();
            Multiplicity oneMult = Multiplicity.getMultOf(1);

            // Create the new nodes.
            ShapeNode newNodes[] = new ShapeNode[numberNewNodes];
            EquivClass<ShapeNode> origNodeEc = this.getEquivClassOf(origNode);
            for (int i = 0; i < numberNewNodes; i++) {
                ShapeNode newNode = this.createNode();
                newNodes[i] = newNode;
                // Add the new node to the shape. Call the super method because
                // we have additional information on the node to be added.
                super.addNode(newNode);
                // The new node is concrete so set its multiplicity to one.
                this.setNodeMult(newNode, oneMult);
                // Copy the labels from the original node.
                this.copyUnaryEdges(origNode, newNode);
                // Add the new node to the equivalence class of the original node.
                origNodeEc.add(newNode);
                // Update the shaping morphism.
                this.nodeShaping.put(newNode, origNode);
            }

            // Now that we have all new nodes, duplicate all incoming and
            // outgoing edges were the original node occurs. Also, update
            // all maps of the shape accordingly.

            // Outgoing edges with origNode as source.
            Set<ShapeEdge> newEdges = new HashSet<ShapeEdge>(numberNewNodes);
            for (ShapeEdge origEdge : this.outBinaryEdgeSet(origNode)) {
                Label label = origEdge.label();
                ShapeNode target = origEdge.opposite();
                Multiplicity origEdgeOutMult = this.getEdgeOutMult(origEdge);
                for (ShapeNode newNode : newNodes) {
                    ShapeEdge newEdge = this.createEdge(newNode, label, target);
                    newEdges.add(newEdge);
                    // Update the outgoing multiplicity map.
                    EdgeSignature newEdgeSig =
                        this.getEdgeOutSignature(newEdge);
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

            // OK, we materialised the node, now store the multiplicity set
            // that will come out from the original node.
            Multiplicity toSub = Multiplicity.getMultOf(numberNewNodes);
            Set<Multiplicity> mults =
                this.getNodeMult(origNode).subNodeMult(toSub);
            result.add(new Pair<ShapeNode,Set<Multiplicity>>(origNode, mults));
        }

        return result;
    }

    /** EDUARDO */
    public Map<Node,ShapeNode> getNodeShaping() {
        return this.nodeShaping;
    }

    /** EDUARDO */
    public Map<Edge,ShapeEdge> getEdgeShaping() {
        return this.edgeShaping;
    }

    /** EDUARDO */
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

    /** EDUARDO */
    public EquivRelation<ShapeNode> getEquivRelation() {
        return this.equivRel;
    }

    /** EDUARDO */
    public void removeImpossibleOutEdges(EdgeSignature es, ShapeEdge edgeToKeep) {
        ShapeNode source = es.getNode();
        Label label = es.getLabel();
        for (ShapeNode target : es.getEquivClass()) {
            ShapeEdge edge = this.getShapeEdge(source, label, target);
            if (edge != null && !edgeToKeep.equals(edge)) {
                this.removeEdge(edge);
            }
        }
    }

    /** EDUARDO */
    public void removeImpossibleInEdges(EdgeSignature es, ShapeEdge edgeToKeep) {
        ShapeNode target = es.getNode();
        Label label = es.getLabel();
        for (ShapeNode source : es.getEquivClass()) {
            ShapeEdge edge = this.getShapeEdge(source, label, target);
            if (edge != null && !edgeToKeep.equals(edge)) {
                this.removeEdge(edge);
            }
        }
    }

    /** EDUARDO */
    public Shape normalise() {
        Shape normalisedShape = new Shape();
        normalisedShape.buildShapeFromShape(this);
        return normalisedShape;
    }
}
