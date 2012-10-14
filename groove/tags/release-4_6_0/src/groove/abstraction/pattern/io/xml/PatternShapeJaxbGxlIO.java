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
package groove.abstraction.pattern.io.xml;

import groove.abstraction.Multiplicity;
import groove.abstraction.Multiplicity.MultKind;
import groove.abstraction.pattern.shape.PatternEdge;
import groove.abstraction.pattern.shape.PatternFactory;
import groove.abstraction.pattern.shape.PatternNode;
import groove.abstraction.pattern.shape.PatternShape;
import groove.abstraction.pattern.shape.TypeEdge;
import groove.abstraction.pattern.shape.TypeGraph;
import groove.abstraction.pattern.shape.TypeNode;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Node;
import groove.io.xml.AbstractJaxbGxlIO;
import groove.view.FormatException;

import java.util.List;
import java.util.Map;

import de.gupro.gxl.gxl_1_0.AttrType;
import de.gupro.gxl.gxl_1_0.EdgeType;
import de.gupro.gxl.gxl_1_0.GraphElementType;
import de.gupro.gxl.gxl_1_0.GraphType;
import de.gupro.gxl.gxl_1_0.NodeType;

/**
 * Class to read and write pattern shapes in GXL format, using JXB data binding.
 * 
 * @author Eduardo Zambon
 */
public final class PatternShapeJaxbGxlIO extends
        AbstractJaxbGxlIO<PatternNode,PatternEdge> {

    // ------------------------------------------------------------------------
    // Static fields
    // ------------------------------------------------------------------------

    /** Attribute name for node multiplicities. */
    private static final String NODE_MULT_ATTR_NAME = "nmult";
    /** Attribute name for edge multiplicities. */
    private static final String EDGE_MULT_ATTR_NAME = "emult";

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    private final TypeGraph typeGraph;
    private PatternFactory elementFactory;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Dafault constructor. */
    public PatternShapeJaxbGxlIO(TypeGraph typeGraph) {
        this.typeGraph = typeGraph;
    }

    // ------------------------------------------------------------------------
    // Overriden methods
    // ------------------------------------------------------------------------

    @Override
    protected PatternShape createGraph(String name) {
        PatternShape shape = new PatternShape(name, this.typeGraph);
        this.elementFactory = shape.getFactory();
        return shape;
    }

    @Override
    protected PatternNode createNode(String nodeId) {
        // EZ says: this method is NOT error resistant...
        // We are expecting a string of this format: "px:ty"
        String s[] = nodeId.split(":");
        assert s[0].indexOf("p") == 0;
        assert s[1].indexOf("t") == 0;
        int nr = Integer.parseInt(s[0].substring(1));
        int typeNr = Integer.parseInt(s[1].substring(1));
        TypeNode type = this.typeGraph.getTypeNodeByNumber(typeNr);
        return this.elementFactory.createNode(nr, type);
    }

    @Override
    protected PatternEdge createEdge(PatternNode sourceNode, String label,
            PatternNode targetNode) {
        // EZ says: this method is NOT error resistant...
        // We are expecting a label string of this format: "mx"
        assert label.indexOf("m") == 0;
        int typeNr = Integer.parseInt(label.substring(1));
        TypeEdge type = this.typeGraph.getTypeEdgeByNumber(typeNr);
        return this.elementFactory.createEdge(sourceNode, type, targetNode);
    }

    /**
     * Stores the node and edge multiplicities and also the equivalence relation.
     */
    @Override
    protected void storeAdditionalStructure(Graph<?,?> graph,
            GraphType gxlGraph, Map<Node,NodeType> nodeMap,
            Map<Edge,EdgeType> edgeMap) {
        assert graph instanceof PatternShape;
        PatternShape shape = (PatternShape) graph;

        // Store the node multiplicities.
        for (PatternNode node : shape.nodeSet()) {
            Multiplicity nodeMult = shape.getMult(node);
            AttrType nodeMultAttr = this.factory.createAttrType();
            nodeMultAttr.setName(NODE_MULT_ATTR_NAME);
            nodeMultAttr.setString(nodeMult.toSerialString());
            NodeType gxlNode = nodeMap.get(node);
            gxlNode.getAttr().add(nodeMultAttr);
        }

        // Store the edge multiplicities.
        for (PatternEdge edge : shape.edgeSet()) {
            Multiplicity edgeMult = shape.getMult(edge);
            AttrType edgeMultAttr = this.factory.createAttrType();
            edgeMultAttr.setName(EDGE_MULT_ATTR_NAME);
            edgeMultAttr.setString(edgeMult.toSerialString());
            EdgeType gxlEdge = edgeMap.get(edge);
            gxlEdge.getAttr().add(edgeMultAttr);
        }
    }

    /**
     * Loads the node and edge multiplicities and also the equivalence relation.
     */
    @Override
    protected void loadAdditionalStructure(
            Graph<PatternNode,PatternEdge> graph, GraphType gxlGraph,
            Map<String,PatternNode> nodeMap, Map<EdgeType,PatternEdge> edgeMap)
        throws FormatException {
        PatternShape shape = (PatternShape) graph;
        for (GraphElementType gxlElement : gxlGraph.getNodeOrEdgeOrRel()) {
            if (gxlElement instanceof NodeType) {
                String nodeId = gxlElement.getId();
                PatternNode node = nodeMap.get(nodeId);
                List<AttrType> attrs = ((NodeType) gxlElement).getAttr();
                String nodeMultStr =
                    getAttrValue(NODE_MULT_ATTR_NAME, attrs, "node " + nodeId);
                Multiplicity nodeMult =
                    getMultiplicity(nodeMultStr, MultKind.NODE_MULT);
                shape.setMult(node, nodeMult);
            } else if (gxlElement instanceof EdgeType) {
                PatternEdge edge = edgeMap.get(gxlElement);
                List<AttrType> attrs = ((EdgeType) gxlElement).getAttr();
                String edgeMultStr =
                    getAttrValue(EDGE_MULT_ATTR_NAME, attrs, "edge");
                Multiplicity edgeMult =
                    getMultiplicity(edgeMultStr, MultKind.EDGE_MULT);
                shape.setMult(edge, edgeMult);
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