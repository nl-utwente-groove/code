// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/*
 * $Id: DefaultGxl.java,v 1.21 2007-12-03 08:55:18 rensink Exp $
 */
package groove.abstraction.neigh.io.xml;

import groove.abstraction.neigh.Multiplicity;
import groove.abstraction.neigh.Multiplicity.EdgeMultDir;
import groove.abstraction.neigh.Multiplicity.MultKind;
import groove.abstraction.neigh.Util;
import groove.abstraction.neigh.equiv.EquivClass;
import groove.abstraction.neigh.equiv.NodeEquivClass;
import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.shape.ShapeEdge;
import groove.abstraction.neigh.shape.ShapeFactory;
import groove.abstraction.neigh.shape.ShapeNode;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Node;
import groove.graph.TypeGraph;
import groove.io.xml.AbstractJaxbGxlIO;
import groove.view.FormatException;

import java.util.List;
import java.util.Map;

import de.gupro.gxl.gxl_1_0.AttrType;
import de.gupro.gxl.gxl_1_0.EdgeType;
import de.gupro.gxl.gxl_1_0.GraphElementType;
import de.gupro.gxl.gxl_1_0.GraphType;
import de.gupro.gxl.gxl_1_0.NodeType;
import de.gupro.gxl.gxl_1_0.RelType;
import de.gupro.gxl.gxl_1_0.RelendType;

/**
 * Class to read and write shapes in GXL format, using JXB data binding.
 * 
 * @author Eduardo Zambon
 */
public final class ShapeJaxbGxlIO extends
        AbstractJaxbGxlIO<ShapeNode,ShapeEdge> {

    // ------------------------------------------------------------------------
    // Static fields
    // ------------------------------------------------------------------------

    /** Attribute name for node multiplicities. */
    private static final String NODE_MULT_ATTR_NAME = "nmult";
    /** Attribute name for out edge multiplicities. */
    private static final String EDGE_OUT_MULT_ATTR_NAME = "omult";
    /** Attribute name for in edge multiplicities. */
    private static final String EDGE_IN_MULT_ATTR_NAME = "imult";

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    private final TypeGraph typeGraph;
    private ShapeFactory elementFactory;

    // ------------------------------------------------------------------------
    // Static methods
    // ------------------------------------------------------------------------

    /** 
     * Returns an instance of this class that will use a given type graph
     * for creating shapes. 
     */
    public static ShapeJaxbGxlIO getInstance(TypeGraph typeGraph) {
        return new ShapeJaxbGxlIO(typeGraph);
    }

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Private constructor for the singleton instance. */
    private ShapeJaxbGxlIO(TypeGraph typeGraph) {
        this.typeGraph = typeGraph;
    }

    // ------------------------------------------------------------------------
    // Overriden methods
    // ------------------------------------------------------------------------

    @Override
    protected Graph<ShapeNode,ShapeEdge> createGraph(String name) {
        this.elementFactory = ShapeFactory.newInstance(this.typeGraph);
        return new Shape(this.elementFactory).downcast();
    }

    @Override
    protected ShapeNode createNode(String nodeId) {
        // attempt to construct node number from gxl node
        // by looking at trailing number shape of node id
        boolean digitFound = false;
        int nodeNr = 0;
        int unit = 1;
        int charIx;
        for (charIx = nodeId.length() - 1; charIx >= 0
            && Character.isDigit(nodeId.charAt(charIx)); charIx--) {
            nodeNr += unit * (nodeId.charAt(charIx) - '0');
            unit *= 10;
            digitFound = true;
        }
        if (charIx >= 0 && nodeId.charAt(charIx) == '-') {
            nodeNr = -nodeNr;
        }
        return digitFound ? this.elementFactory.createNode(nodeNr) : null;
    }

    @Override
    protected ShapeEdge createEdge(ShapeNode sourceNode, String label,
            ShapeNode targetNode) {
        return (ShapeEdge) this.elementFactory.createEdge(sourceNode, label,
            targetNode);
    }

    /**
     * Stores the node and edge multiplicities and also the equivalence relation.
     */
    @Override
    protected void storeAdditionalStructure(Graph<?,?> graph,
            GraphType gxlGraph, Map<Node,NodeType> nodeMap,
            Map<Edge,EdgeType> edgeMap) {
        assert graph instanceof Shape;
        Shape shape = (Shape) graph;

        // Store the node multiplicities.
        for (ShapeNode node : shape.nodeSet()) {
            Multiplicity nodeMult = shape.getNodeMult(node);
            AttrType nodeMultAttr = this.factory.createAttrType();
            nodeMultAttr.setName(NODE_MULT_ATTR_NAME);
            nodeMultAttr.setString(nodeMult.toSerialString());
            NodeType gxlNode = nodeMap.get(node);
            gxlNode.getAttr().add(nodeMultAttr);
        }

        // Store the edge multiplicities.
        for (ShapeEdge edge : Util.getBinaryEdges(shape)) {
            EdgeType gxlEdge = edgeMap.get(edge);
            for (EdgeMultDir direction : EdgeMultDir.values()) {
                Multiplicity edgeMult = shape.getEdgeMult(edge, direction);
                AttrType edgeMultAttr = this.factory.createAttrType();
                if (direction == EdgeMultDir.OUTGOING) {
                    edgeMultAttr.setName(EDGE_OUT_MULT_ATTR_NAME);
                } else { // INCOMING
                    edgeMultAttr.setName(EDGE_IN_MULT_ATTR_NAME);
                }
                edgeMultAttr.setString(edgeMult.toSerialString());
                gxlEdge.getAttr().add(edgeMultAttr);
            }
        }

        // Store the equivalence relation.
        List<GraphElementType> nodesEdgesRels = gxlGraph.getNodeOrEdgeOrRel();
        int i = 0;
        for (EquivClass<ShapeNode> ec : shape.getEquivRelation()) {
            RelType gxlRel = this.factory.createRelType();
            // Create an arbitrary id for the equivalence class.
            gxlRel.setId("ec" + i);
            i++;
            // For each equivalence class, create a relation end.
            for (ShapeNode node : ec) {
                RelendType relEnd = this.factory.createRelendType();
                relEnd.setId(node.toString());
                gxlRel.getRelend().add(relEnd);
            }
            nodesEdgesRels.add(gxlRel);
        }
    }

    /**
     * Loads the node and edge multiplicities and also the equivalence relation.
     */
    @Override
    protected void loadAdditionalStructure(Graph<ShapeNode,ShapeEdge> graph,
            GraphType gxlGraph, Map<String,ShapeNode> nodeMap,
            Map<EdgeType,ShapeEdge> edgeMap) throws FormatException {
        Shape shape = Shape.upcast(graph);
        shape.clearStructuresForLoading();

        // First pass. Sets node multiplicities and the equivalence relation.
        for (GraphElementType gxlElement : gxlGraph.getNodeOrEdgeOrRel()) {
            // Check if we got a node.
            if (gxlElement instanceof NodeType) {
                // Load the node multiplicities.
                String nodeId = gxlElement.getId();
                ShapeNode node = nodeMap.get(nodeId);
                List<AttrType> attrs = ((NodeType) gxlElement).getAttr();
                // Save the multiplicity.
                String nodeMultStr =
                    getAttrValue(NODE_MULT_ATTR_NAME, attrs, "node " + nodeId);
                Multiplicity nodeMult =
                    getMultiplicity(nodeMultStr, MultKind.NODE_MULT);
                shape.setNodeMult(node, nodeMult);
            } else if (gxlElement instanceof RelType) {
                // We got a relation.
                NodeEquivClass<ShapeNode> ec =
                    new NodeEquivClass<ShapeNode>(this.elementFactory);
                for (RelendType relEnd : ((RelType) gxlElement).getRelend()) {
                    ShapeNode node = nodeMap.get(relEnd.getId());
                    ec.add(node);
                }
                shape.getEquivRelation().add(ec);
            }
        }

        // Second pass. Sets edge multiplicities.
        for (GraphElementType gxlElement : gxlGraph.getNodeOrEdgeOrRel()) {
            // Check if we got an edge.
            if (gxlElement instanceof EdgeType) {
                ShapeEdge edge = edgeMap.get(gxlElement);
                // Extract the multiplicities from the gxlElement attributes.
                List<AttrType> attrs = ((EdgeType) gxlElement).getAttr();
                for (EdgeMultDir direction : EdgeMultDir.values()) {
                    String attrName;
                    if (direction == EdgeMultDir.OUTGOING) {
                        attrName = EDGE_OUT_MULT_ATTR_NAME;
                    } else { // INCOMING
                        attrName = EDGE_IN_MULT_ATTR_NAME;
                    }
                    String multStr = getAttrValue(attrName, attrs, "edge");
                    if (multStr != null) {
                        Multiplicity mult =
                            getMultiplicity(multStr, MultKind.EDGE_MULT);
                        shape.setEdgeMult(edge, direction, mult);
                    }
                }
            }
        }
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** Converts the given string to a proper multiplicity, based on given kind. */
    private Multiplicity getMultiplicity(String multStr, MultKind kind) {
        String[] parts = multStr.split(" ");
        int lowerBound = Integer.parseInt(parts[0]);
        int upperBound;
        if ("w".equals(parts[1])) {
            upperBound = Multiplicity.OMEGA;
        } else {
            upperBound = Integer.parseInt(parts[1]);
        }
        return Multiplicity.getMultiplicity(lowerBound, upperBound, kind);
    }

}