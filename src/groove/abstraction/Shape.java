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
import groove.util.Groove;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * EDUARDO
 * @author Eduardo Zambon
 * @version $Revision $
 */
public class Shape extends DefaultGraph {

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    private final Graph graph;
    private final Map<Node,ShapeNode> nodeShaping;
    private final Map<Edge,ShapeEdge> edgeShaping;
    private final EquivRelation<ShapeNode> equivRel;
    private final Map<ShapeNode,Multiplicity> nodeMultMap;
    private final Map<EdgeSignature,Multiplicity> outEdgeMultMap;
    private final Map<EdgeSignature,Multiplicity> inEdgeMultMap;

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
        this.buildShape();
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public Set<ShapeNode> nodeSet() {
        return (Set<ShapeNode>) super.nodeSet();
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
        this.createShapeEdges(currGraphNeighEquiv.getEdgesEquivRel(),
            prevGraphNeighEquiv);
    }

    private void createShapeNodes(GraphNeighEquiv currGraphNeighEquiv) {
        // Each node of the shape correspond to an equivalence class
        // of the graph.
        for (EquivClass<Node> nodeEquivClass : currGraphNeighEquiv) {
            ShapeNode shapeNode = (ShapeNode) this.createNode();
            // Add a shape node to the shape.
            this.addNode(shapeNode);
            // Update the shaping information.
            for (Node graphNode : nodeEquivClass) {
                this.nodeShaping.put(graphNode, shapeNode);
            }
            // Fill the shape node multiplicity mapping.
            Multiplicity nodeMult = Multiplicity.getNodeSetMult(nodeEquivClass);
            this.nodeMultMap.put(shapeNode, nodeMult);
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

    private void createShapeEdges(EquivRelation<Edge> edgeEquivRel,
            GraphNeighEquiv prevGraphNeighEquiv) {
        // Each edge of the shape correspond to an equivalence class
        // of the graph.
        for (EquivClass<Edge> edgeEquivClass : edgeEquivRel) {
            // Get an arbitrary edge from the equivalence class.
            Edge graphEdge = edgeEquivClass.iterator().next();

            Node srcG = graphEdge.source();
            Node tgtG = graphEdge.opposite();
            ShapeNode seSource = this.nodeShaping.get(srcG);
            ShapeNode seTarget = this.nodeShaping.get(tgtG);
            Label seLabel = graphEdge.label();
            // Add a shape edge to the shape.
            ShapeEdge shapeEdge =
                (ShapeEdge) this.createEdge(seSource, seLabel, seTarget);

            // Update the shaping information.
            for (Edge edge : edgeEquivClass) {
                this.edgeShaping.put(edge, shapeEdge);
            }

            // Fill the shape edge in and out multiplicity mapping.
            EquivClass<Node> gTargetEc =
                prevGraphNeighEquiv.getEquivClassOf(tgtG);
            Set<Edge> nInterEc =
                Util.getIntersectEdges(this.graph, srcG, gTargetEc, seLabel);
            Set<Edge> ecInterN =
                Util.getIntersectEdges(this.graph, gTargetEc, srcG, seLabel);
            Multiplicity outEdgeMult = Multiplicity.getEdgeSetMult(nInterEc);
            Multiplicity inEdgeMult = Multiplicity.getEdgeSetMult(ecInterN);
            // EDUARDO: Check if we need to change this.
            EquivClass<ShapeNode> seTargetEc =
                this.equivRel.getEquivClassOf(seTarget);
            EdgeSignature seSig =
                new EdgeSignature(seSource, seLabel, seTargetEc);
            this.outEdgeMultMap.put(seSig, outEdgeMult);
            this.inEdgeMultMap.put(seSig, inEdgeMult);
        }
    }

    // ------------------------------------------------------------------------
    // Test methods
    // ------------------------------------------------------------------------

    private static void testShapeBuild0() {
        File file = new File("/home/zambon/Temp/abs-list.gps/equiv-test-0.gst");
        try {
            Graph graph = Groove.loadGraph(file);
            System.out.println(file);
            Shape shape = new Shape(graph);
            System.out.println(shape);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Used for unit testing. */
    public static void main(String args[]) {
        Multiplicity.initMultStore();
        testShapeBuild0();
    }

}
