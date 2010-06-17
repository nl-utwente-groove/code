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
import groove.trans.Rule;
import groove.trans.RuleMatch;
import groove.util.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * EDUARDO
 * @author Eduardo Zambon
 * @version $Revision $
 */
public class Shape extends DefaultGraph {

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    private Graph graph;

    // EZ says to himself: BE CAREFUL HERE. Remember that:
    // - If this.graph is an instance of a concrete graph, then the shaping
    //   relation is an abstraction morphism that goes from elements of
    //   this.graph to elements of this .
    // - If this.graph is an instance of a shape, then the shaping relation is
    //   a shaping morphism that goes in the OTHER direction, from elements of
    //   this to elements of this.graph .
    private final Map<Node,ShapeNode> nodeShaping;
    private final Map<Edge,ShapeEdge> edgeShaping;

    private final EquivRelation<ShapeNode> equivRel;
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
        this.outEdgeMultMap = new HashMap<EdgeSignature,Multiplicity>();
        this.inEdgeMultMap = new HashMap<EdgeSignature,Multiplicity>();
        this.edgeSigSet = new HashSet<EdgeSignature>();
        this.buildShape();
    }

    /** Copying constructor. */
    private Shape(Shape shape) {
        super(shape);
        this.graph = shape.graph;
        this.nodeShaping = new HashMap<Node,ShapeNode>(shape.nodeShaping);
        this.edgeShaping = new HashMap<Edge,ShapeEdge>(shape.edgeShaping);
        this.equivRel = new EquivRelation<ShapeNode>(shape.equivRel);
        this.outEdgeMultMap =
            new HashMap<EdgeSignature,Multiplicity>(shape.outEdgeMultMap);
        this.inEdgeMultMap =
            new HashMap<EdgeSignature,Multiplicity>(shape.inEdgeMultMap);
        this.edgeSigSet = new HashSet<EdgeSignature>(shape.edgeSigSet);
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
    public boolean removeEdge(Edge edge) {
        boolean result = super.removeEdge(edge);
        if (result) {
            // Update edge multiplicity maps.
            // EDUARDO : Implement this.
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Nodes:\n");
        for (ShapeNode node : this.nodeSet()) {
            sb.append("  " + node + ":" + node.getMultiplicity() + " ");
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

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    private void buildShape() {
        // First we create the equivalence relation for the nodes in the graph.
        GraphNeighEquiv prevGraphNeighEquiv = null;
        GraphNeighEquiv currGraphNeighEquiv = new GraphNeighEquiv(this.graph);
        // This loop is guaranteed to be executed at least once, because
        // we start at radius 0 and the abstraction radius is at least 1.
        while (currGraphNeighEquiv.getRadius() < Parameters.getAbsRadius()) {
            prevGraphNeighEquiv = (GraphNeighEquiv) currGraphNeighEquiv.clone();
            currGraphNeighEquiv.refineEquivRelation();
        }
        // At this point variable prevGraphNeighEquiv is no longer null.

        this.createShapeNodes(currGraphNeighEquiv);
        this.createShapeNodesEquivRel(prevGraphNeighEquiv);
        this.createShapeEdges(currGraphNeighEquiv.getEdgesEquivRel());
        this.createEdgeMultMaps(currGraphNeighEquiv);
    }

    private void createShapeNodes(GraphNeighEquiv currGraphNeighEquiv) {
        // Each node of the shape correspond to an equivalence class
        // of the graph.
        for (EquivClass<Node> nodeEquivClass : currGraphNeighEquiv) {
            ShapeNode shapeNode = this.createNode();
            // Add a shape node to the shape.
            this.addNode(shapeNode);
            // Update the shaping information.
            for (Node graphNode : nodeEquivClass) {
                this.nodeShaping.put(graphNode, shapeNode);
            }
            // Fill the shape node multiplicity.
            Multiplicity nodeMult = Multiplicity.getNodeSetMult(nodeEquivClass);
            shapeNode.setMultiplicity(nodeMult);
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

    private void createEdgeMultMaps(GraphNeighEquiv currGraphNeighEquiv) {
        // For all labels.
        for (Label label : Util.binaryLabelSet(this.graph)) {
            // For all nodes in the graph.
            for (Node node : this.graph.nodeSet()) {
                // For all equivalence classes in the shape.
                for (EquivClass<ShapeNode> ecS : this.equivRel) {
                    Set<Node> nodesG = this.getReverseNodeMap(ecS);
                    ShapeNode nodeS = this.getShapeNode(node);
                    EdgeSignature es = this.getEdgeSignature(nodeS, label, ecS);

                    // Outgoing multiplicity.
                    Set<Edge> outInter =
                        Util.getIntersectEdges(this.graph, node, nodesG, label);
                    Multiplicity outMult =
                        Multiplicity.getEdgeSetMult(outInter);
                    if (outMult.isPositive()) {
                        this.setEdgeOutMult(es, outMult);
                    } // else don't store multiplicity zero.

                    // Incoming multiplicity.
                    Set<Edge> inInter =
                        Util.getIntersectEdges(this.graph, nodesG, node, label);
                    Multiplicity inMult = Multiplicity.getEdgeSetMult(inInter);
                    if (inMult.isPositive()) {
                        this.setEdgeInMult(es, inMult);
                    } // else don't store multiplicity zero.
                }
            }
        }
    }

    private void setEdgeOutMult(EdgeSignature es, Multiplicity mult) {
        this.outEdgeMultMap.put(es, mult);
    }

    private void setEdgeInMult(EdgeSignature es, Multiplicity mult) {
        this.inEdgeMultMap.put(es, mult);
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

    private EdgeSignature getEdgeOutSignature(ShapeEdge edge) {
        EquivClass<ShapeNode> ec = this.getEquivClassOf(edge.opposite());
        return this.getEdgeSignature(edge.source(), edge.label(), ec);
    }

    private EdgeSignature getEdgeInSignature(ShapeEdge edge) {
        EquivClass<ShapeNode> ec = this.getEquivClassOf(edge.source());
        return this.getEdgeSignature(edge.opposite(), edge.label(), ec);
    }

    private EdgeSignature getEdgeSignature(ShapeNode node, Label label,
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

    private EquivClass<ShapeNode> getEquivClassOf(ShapeNode node) {
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
    public void materialiseNodes(Set<Pair<ShapeNode,Integer>> nodes) {
        // For all nodes that need to be materialised.
        for (Pair<ShapeNode,Integer> pair : nodes) {
            ShapeNode origNode = pair.first();

            int numberNewNodes = pair.second().intValue();
            //Multiplicity toSub = Multiplicity.getMultOf(numberNewNodes);
            Multiplicity oneMult = Multiplicity.getMultOf(1);

            // Create the new nodes.
            ShapeNode newNodes[] = new ShapeNode[numberNewNodes];
            EquivClass<ShapeNode> origNodeEc = this.getEquivClassOf(origNode);
            for (int i = 0; i < numberNewNodes; i++) {
                ShapeNode newNode = this.createNode();
                newNodes[i] = newNode;
                // The new node is concrete so set its multiplicity to one.
                newNode.setMultiplicity(oneMult);
                // Add the new node to the shape.
                this.addNode(newNode);
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
        }
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
    public Set<ShapeEdge> getInSharedEdges(ShapeEdge edgeS) {
        Set<ShapeEdge> result = new HashSet<ShapeEdge>();

        ShapeNode target = edgeS.opposite();
        Label label = edgeS.label();
        EdgeSignature es = this.getEdgeInSignature(edgeS);
        for (ShapeNode source : es.getEquivClass()) {
            ShapeEdge edge = this.getShapeEdge(source, label, target);
            if (edge != null) {
                result.add(edge);
            }
        }

        return result;
    }

    private ShapeEdge getShapeEdge(ShapeNode source, Label label,
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

}
