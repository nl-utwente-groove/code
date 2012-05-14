/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.abstraction.pattern.io.xml;

import groove.abstraction.MyHashMap;
import groove.abstraction.pattern.shape.TypeEdge;
import groove.abstraction.pattern.shape.TypeGraph;
import groove.abstraction.pattern.shape.TypeNode;
import groove.trans.HostNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import de.gupro.gxl.gxl_1_0.AttrType;
import de.gupro.gxl.gxl_1_0.EdgeType;
import de.gupro.gxl.gxl_1_0.GraphElementType;
import de.gupro.gxl.gxl_1_0.GraphType;
import de.gupro.gxl.gxl_1_0.GxlType;
import de.gupro.gxl.gxl_1_0.NodeType;
import de.gupro.gxl.gxl_1_0.TypeType;

/**
 * Class to read pattern type graphs in GXL format, using JXB data binding.
 * 
 * @author Eduardo Zambon
 */
public final class TypeGraphJaxbGxlIO {

    // ------------------------------------------------------------------------
    // Static fields
    // ------------------------------------------------------------------------

    /** Attribute name for edge labels. */
    static private final String LABEL_ATTR_NAME = "label";
    /** Role for GXL type graph. */
    private static final String GXL_T_ROLE = "gxl_ptgraph";
    /** Role for GXL type graph. */
    private static final String GXL_S_ROLE = "ptgraph";
    /** Prefixes used in identities. */
    private static final String NODE_ID_PREFIX = "p";
    private static final String EDGE_ID_PREFIX = "d";

    private static final TypeGraphJaxbGxlIO instance = new TypeGraphJaxbGxlIO();

    // ------------------------------------------------------------------------
    // Static methods
    // ------------------------------------------------------------------------

    /** Returns the singleton instance of this class. */
    public static TypeGraphJaxbGxlIO getInstance() {
        return instance;
    }

    private static int parseId(String id) {
        return Integer.parseInt(id.substring(1));
    }

    private static String getTypeString(TypeType type) {
        return type.getOtherAttributes().values().iterator().next().substring(1);
    }

    private static boolean isNodeId(String id) {
        return id.startsWith(NODE_ID_PREFIX);
    }

    private static boolean isEdgeId(String id) {
        return id.startsWith(EDGE_ID_PREFIX);
    }

    private static String getAttrValue(String attrName, List<AttrType> attrs) {
        for (AttrType attr : attrs) {
            if (attr.getName().equals(attrName)) {
                return attr.getString();
            }
        }
        return null;
    }

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    /** Reusable context for JAXB (un)marshalling. */
    private JAXBContext context;
    /** Reusable unmarshaller. */
    private javax.xml.bind.Unmarshaller unmarshaller;
    /** Auxiliary maps. */
    private Map<String,TypeNode> nodeMap;
    private Map<String,TypeEdge> edgeMap;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Private constructor for the singleton instance. */
    private TypeGraphJaxbGxlIO() {
        try {
            this.context =
                JAXBContext.newInstance(GxlType.class.getPackage().getName());
            this.unmarshaller = this.context.createUnmarshaller();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        this.nodeMap = null;
        this.edgeMap = null;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    private TypeNode getTypeNode(String type) {
        return this.nodeMap.get(type);
    }

    private TypeEdge getTypeEdge(String type) {
        return this.edgeMap.get(type);
    }

    /** Loads a pattern type graph from the given file. */
    public TypeGraph unmarshalTypeGraph(File file) throws IOException {
        InputStream in = new FileInputStream(file);
        List<GraphType> readGraphs = unmarshal(in);
        assert readGraphs.size() == 2;
        // First graph should be the type structure.
        TypeGraph result = readTypeGraph(readGraphs.get(0));
        readSimpleGraphs(readGraphs.get(1), result);
        assert result.isWellFormed();
        assert !result.isCommuting();
        return result;
    }

    private List<GraphType> unmarshal(InputStream inputStream)
        throws IOException {
        try {
            @SuppressWarnings("unchecked")
            JAXBElement<GxlType> doc =
                (JAXBElement<GxlType>) this.unmarshaller.unmarshal(inputStream);
            inputStream.close();
            return doc.getValue().getGraph();
        } catch (JAXBException e) {
            throw new IOException(String.format("Error in %s: %s", inputStream,
                e.getMessage()));
        }
    }

    private TypeGraph readTypeGraph(GraphType gxlTypeGraph) {
        assert gxlTypeGraph.getRole().equals(GXL_T_ROLE);
        assert gxlTypeGraph.isEdgeids();

        String name = gxlTypeGraph.getId();
        TypeGraph typeGraph = new TypeGraph(name);

        // Now read the nodes and edges. We assume a proper order to avoid
        // a second pass on the file.
        this.nodeMap = new MyHashMap<String,TypeNode>();
        this.edgeMap = new MyHashMap<String,TypeEdge>();
        for (GraphElementType gxlElement : gxlTypeGraph.getNodeOrEdgeOrRel()) {
            if (gxlElement instanceof NodeType) {
                String nodeId = gxlElement.getId();
                TypeNode node = typeGraph.addNode(parseId(nodeId));
                this.nodeMap.put(nodeId, node);
            }

            if (gxlElement instanceof EdgeType) {
                EdgeType edgeType = (EdgeType) gxlElement;
                String edgeId = edgeType.getId();
                String srcId = ((NodeType) edgeType.getFrom()).getId();
                String tgtId = ((NodeType) edgeType.getTo()).getId();
                TypeNode source = getTypeNode(srcId);
                TypeNode target = getTypeNode(tgtId);
                TypeEdge edge =
                    typeGraph.addEdge(parseId(edgeId), source, target);
                this.edgeMap.put(edgeId, edge);
            }
        }

        return typeGraph;
    }

    private void readSimpleGraphs(GraphType gxlSimpleGraph, TypeGraph typeGraph) {
        assert gxlSimpleGraph.getRole().equals(GXL_S_ROLE);

        Map<String,HostNode> snodeMap = new MyHashMap<String,HostNode>();
        for (GraphElementType gxlElement : gxlSimpleGraph.getNodeOrEdgeOrRel()) {
            String type = getTypeString(gxlElement.getType());
            if (gxlElement instanceof NodeType) {
                TypeNode typeNode = getTypeNode(type);
                String snodeId = gxlElement.getId();
                HostNode snode =
                    typeNode.getPattern().addNode(parseId(snodeId));
                snodeMap.put(snodeId, snode);
            }

            if (gxlElement instanceof EdgeType) {
                EdgeType edgeType = (EdgeType) gxlElement;
                String srcId = ((NodeType) edgeType.getFrom()).getId();
                String tgtId = ((NodeType) edgeType.getTo()).getId();
                HostNode source = snodeMap.get(srcId);
                HostNode target = snodeMap.get(tgtId);
                if (isNodeId(type)) {
                    TypeNode typeNode = getTypeNode(type);
                    String label =
                        getAttrValue(LABEL_ATTR_NAME, gxlElement.getAttr());
                    typeNode.getPattern().addEdge(source, label, target);
                } else {
                    assert isEdgeId(type);
                    TypeEdge typeEdge = getTypeEdge(type);
                    typeEdge.getMorphism().putNode(source, target);
                }
            }
        }

        typeGraph.setFixed();
    }

}
