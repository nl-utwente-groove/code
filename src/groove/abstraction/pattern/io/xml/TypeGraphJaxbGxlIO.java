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

import groove.abstraction.pattern.shape.TypeGraph;
import groove.abstraction.pattern.shape.TypeNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
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

/**
 * Class to read pattern type graphs in GXL format, using JXB data binding.
 * 
 * @author Eduardo Zambon
 */
public final class TypeGraphJaxbGxlIO {

    // ------------------------------------------------------------------------
    // Static fields
    // ------------------------------------------------------------------------

    /** Attribute name for maximum node count. */
    private static final String NODE_COUNT_ATTR_NAME = "nodecount";
    /** Attribute name for maximum edge count. */
    private static final String EDGE_COUNT_ATTR_NAME = "edgecount";
    /** Role for GXL type graph. */
    private static final String GXL_ROLE = "gxl_ptgraph";

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

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    /** Reusable context for JAXB (un)marshalling. */
    private JAXBContext context;
    /** Reusable unmarshaller. */
    private javax.xml.bind.Unmarshaller unmarshaller;

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
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** Loads a pattern type graph from the given file. */
    public TypeGraph unmarshalTypeGraph(File file) throws IOException {
        InputStream in = new FileInputStream(file);
        List<GraphType> readGraphs = unmarshal(in);
        assert readGraphs.size() == 2;
        // First graph should be the type structure.
        TypeGraph result = readTypeGraph(readGraphs.get(0));
        readSimpleGraphs(readGraphs.get(1), result);
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
        assert gxlTypeGraph.getRole().equals(GXL_ROLE);
        assert gxlTypeGraph.isEdgeids();

        // Read the graph attributes so we can create the type graph object.
        int maxNodeCount = 0;
        int maxEdgeCount = 0;
        List<AttrType> attrs = gxlTypeGraph.getAttr();
        for (AttrType attr : attrs) {
            if (attr.getName().equals(NODE_COUNT_ATTR_NAME)) {
                maxNodeCount = attr.getInt().intValue();
            } else if (attr.getName().equals(EDGE_COUNT_ATTR_NAME)) {
                maxEdgeCount = attr.getInt().intValue();
            }
        }
        assert maxNodeCount > 0 && maxEdgeCount > 0;
        String name = gxlTypeGraph.getId();

        TypeGraph typeGraph = new TypeGraph(name, maxNodeCount, maxEdgeCount);

        // Now read the nodes and edges. We assume a proper order to avoid
        // a second pass on the file.
        Map<String,TypeNode> nodeMap = new HashMap<String,TypeNode>();
        for (GraphElementType gxlElement : gxlTypeGraph.getNodeOrEdgeOrRel()) {
            if (gxlElement instanceof NodeType) {
                String nodeId = gxlElement.getId();
                TypeNode node = typeGraph.addNode(parseId(nodeId));
                nodeMap.put(nodeId, node);
            }

            if (gxlElement instanceof EdgeType) {
                EdgeType edgeType = (EdgeType) gxlElement;
                String edgeId = edgeType.getId();
                String srcId = ((NodeType) edgeType.getFrom()).getId();
                String tgtId = ((NodeType) edgeType.getTo()).getId();
                TypeNode source = nodeMap.get(srcId);
                TypeNode target = nodeMap.get(tgtId);
                typeGraph.addEdge(parseId(edgeId), source, target);
            }
        }

        return typeGraph;
    }

    private void readSimpleGraphs(GraphType gxlSimpleGraph, TypeGraph typeGraph) {
        // EDUARDO: Implement this...
    }

    /** EDUARDO: Comment this... */
    public static void main(String args[]) {
        File file = new File("/home/zambon/Temp/test.gxl");
        TypeGraph typeGraph = null;
        try {
            typeGraph = getInstance().unmarshalTypeGraph(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(typeGraph);
    }
}
