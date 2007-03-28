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
 * $Id: BindingGxl.java,v 1.2 2007-03-28 15:12:32 rensink Exp $
 */
package groove.io;

import groove.graph.DefaultLabel;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphFactory;
import groove.graph.Node;
import groove.graph.iso.DefaultIsoChecker;
import groove.graph.iso.IsoChecker;
import groove.util.Pair;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;

/**
 * Class to convert graphs to GXL format and back.
 * Currently the conversion only supports binary edges.
 * This class is implemented using data binding.
 * @author Arend Rensink
 * @version $Revision: 1.2 $
 * @deprecated use {@link UntypedGxl}
 */
@Deprecated
public class BindingGxl extends AbstractXml {
    /** Attribute name for node and edge ids */
    static public final String LABEL_ATTR_NAME = "label";

    static private final IsoChecker isoChecker = new DefaultIsoChecker();
    static private GraphFactory defaultGraphFactory = GraphFactory.getInstance();
    /**
     * Returns a default graph factory for the construction of graphs
     * during unmarshalling.
     */
    static public GraphFactory getDefaultGraphFactory() {
        return defaultGraphFactory;
    }

    static public void main(String[] args) {
        System.out.println("Test of groove.io.BindingGxl");
        System.out.println("===================");
        groove.io.BindingGxl gxl = new groove.io.BindingGxl();
        for (int i = 0; i < args.length; i++) {
            System.out.println("\nTesting: " + args[i]);
            try {
                System.out.print("    Creating input file: ");
                java.io.File file = new java.io.File(args[i]);
                System.out.println("OK");
                // Unmarshal graph
                System.out.print("    Unmarshalling graph: ");
                Graph graph = gxl.unmarshalGraph(file);
                System.out.println("OK");
                System.out.print("    Creating output file: ");
                file = new java.io.File(args[i] + ".tmp");
                System.out.println("OK");
                System.out.print("    Re-marshalling graph: ");
                gxl.marshalGraph(graph, file);
                System.out.println("OK");
                // unmarshal again and test for isomorphism
                System.out.print("    Testing for isomorphism of original and re-marshalled graph: ");
                Graph newGraph = gxl.unmarshalGraph(file);

                if (isoChecker.areIsomorphic(newGraph,graph))
                    System.out.println("OK");
                else {
                    System.out.println("ERROR");
                    System.out.println("Unmarshalled graph");
                    System.out.println("------------------");
                    System.out.println(newGraph);
                }
            } catch (Exception exc) {
                System.out.println(exc);
                exc.printStackTrace();
            }
        }
    }

    /**
     * Constructs a Gxl transformer with a given graph factory
     * for the graphs constructed by unmarshalling.
     * @throws XmlRuntimeException if setting up the document builder 
     * fails for some internal reason
     */
    public BindingGxl(GraphFactory graphFactory) throws XmlRuntimeException {
        this.graphFactory = graphFactory;
    }

    /**
     * Constructs a Gxl transformer with a default graph factory.
     * @throws XmlRuntimeException if setting up the document builder 
     * fails for some internal reason
     * @see #getDefaultGraphFactory()
     */
    public BindingGxl() throws XmlRuntimeException {
        this(getDefaultGraphFactory());
    }

    public void marshalGraph(Graph graph, File file) {
        groove.gxl.Graph gxlGraph = new groove.gxl.Graph();
        gxlGraph.setEdgeids(false);
        gxlGraph.setId("graph");
        gxlGraph.setRole("graph");
        // add the nodes
        Map<Node,groove.gxl.Node> nodeMap = new HashMap<Node,groove.gxl.Node>();
        for (Node node: graph.nodeSet()) {
            // create an xml element for this node
            groove.gxl.Node gxlNode = new groove.gxl.Node();
            nodeMap.put(node, gxlNode);
            // give the element an id
            gxlNode.setId(node.toString());
            // add the node to the graph
            groove.gxl.GraphTypeItem gxlGraphTypeItem = new groove.gxl.GraphTypeItem();
            gxlGraphTypeItem.setNode(gxlNode);
            gxlGraph.addGraphTypeItem(gxlGraphTypeItem);
        }
        // add the edges
        for (Edge edge: graph.edgeSet()) {
            // create an xml element for this edge
            groove.gxl.Edge gxlEdge = new groove.gxl.Edge();
            gxlEdge.setFrom(nodeMap.get(edge.source()));
            if (edge.endCount() == 2) {
                gxlEdge.setTo(nodeMap.get(edge.end(Edge.TARGET_INDEX)));
            }
            // give the edge element a label
            groove.gxl.Value gxlLabelText = new groove.gxl.Value();
            gxlLabelText.setString(edge.label().text());
            groove.gxl.Attr gxlLabelAttr = new groove.gxl.Attr();
            gxlLabelAttr.setName(LABEL_ATTR_NAME);
            gxlLabelAttr.setValue(gxlLabelText);
            gxlEdge.addAttr(gxlLabelAttr);
            // add the edge to the graph
            groove.gxl.GraphTypeItem gxlGraphTypeItem = new groove.gxl.GraphTypeItem();
            gxlGraphTypeItem.setEdge(gxlEdge);
            gxlGraph.addGraphTypeItem(gxlGraphTypeItem);
        }
        groove.gxl.Gxl gxl = new groove.gxl.Gxl();
        gxl.addGraph(gxlGraph);
        try {
            gxl.validate();
            Marshaller marshaller = new Marshaller(new FileWriter(file));
            marshaller.marshal(gxl);
        } catch (MarshalException e) {
//            e.printStackTrace();
            throw new XmlRuntimeException(e.getMessage());
        } catch (IOException e) {
//            e.printStackTrace();
            throw new XmlRuntimeException(e.getMessage());
        } catch (ValidationException e) {
//            e.printStackTrace();
            throw new XmlRuntimeException(e.getMessage());
        }
    }

    @Override
    public Pair<Graph,Map<String,Node>> unmarshalGraphMap(File file) throws XmlException, IOException {
        Graph graph = graphFactory.newGraph();
        // get a gxl object from the reader
        groove.gxl.Gxl gxl;
        try {
            gxl = new groove.gxl.Gxl();
            Unmarshaller unmarshaller = new Unmarshaller(gxl);
            unmarshaller.setLogWriter(new PrintWriter(System.err));
            unmarshaller.unmarshal(new FileReader(file));
        } catch (MarshalException e) {
            e.printStackTrace();
            throw new XmlException(e.getMessage());
        } catch (ValidationException e) {
            e.printStackTrace();
            throw new XmlException(e.getMessage());
        }

        // now convert the gxl to a graph        
        if (gxl.getGraphCount() != 1)
            throw new XmlException("Only one graph allowed in document");
        // Get the first and only graph element
        groove.gxl.Graph gxlGraph = gxl.getGraph(0);

        // Hashmap for the ID lookup (ID to Vertex)
        Map<String,Node> nodeIds = new HashMap<String,Node>();
        // iterator over the nodes
        Enumeration<?> nodeEnum = gxlGraph.enumerateGraphTypeItem();
        while (nodeEnum.hasMoreElements()) {
            groove.gxl.GraphTypeItem element = (groove.gxl.GraphTypeItem) nodeEnum.nextElement();
            groove.gxl.Node gxlNode = element.getNode();
            if (gxlNode != null) {
                // this is a node
                String nodeId = gxlNode.getId();
                // get graph node from map or create it
                Node node = nodeIds.get(nodeId);
                if (node == null) {
                    // Add Node to Graph
                    node = graph.addNode();
                    // Add ID, groove.graph.Node pair to Map
                    nodeIds.put(nodeId, node);
                }
                // Fetch Label
                String labelText = null;
                Enumeration<?> attrEnum = gxlNode.enumerateAttr();
                while (labelText == null & attrEnum.hasMoreElements()) {
                    groove.gxl.Attr attr = (groove.gxl.Attr) attrEnum.nextElement();
                    if (attr.getName().toLowerCase().equals(LABEL_ATTR_NAME)) {
                        labelText = attr.getValue().getString();
                        assert labelText != null;
                    }
                }
                if (labelText != null) {
                    graph.addEdge(node, DefaultLabel.createLabel(labelText), node);
                }
            }
        }
        // Get the edge tags
        Enumeration<?> edgeEnum = gxlGraph.enumerateGraphTypeItem();
        while (edgeEnum.hasMoreElements()) {
            groove.gxl.GraphTypeItem element = (groove.gxl.GraphTypeItem) edgeEnum.nextElement();
            groove.gxl.Edge gxlEdge = element.getEdge();
            if (gxlEdge != null) {
                // Create graph Edge
                // Fetch Source node
                String sourceId = ((groove.gxl.Node) gxlEdge.getFrom()).getId();
                Node sourceNode = nodeIds.get(sourceId);
                String targetId = ((groove.gxl.Node) gxlEdge.getTo()).getId();
                Node targetNode = nodeIds.get(targetId);
                // Fetch Label
                String labelText = null;
                Enumeration<?> attrEnum = gxlEdge.enumerateAttr();
                while (labelText == null & attrEnum.hasMoreElements()) {
                    groove.gxl.Attr attr = (groove.gxl.Attr) attrEnum.nextElement();
                    if (attr.getName().toLowerCase().equals(LABEL_ATTR_NAME)) {
                        labelText = attr.getValue().getString();
                        assert labelText != null;
                    }
                }
                // decompose labelText and add new graph edges
                if (labelText == null) {
                    throw new XmlException("Graph " + gxlGraph.getId() + " contains edge without label");
                }
                if (targetNode == null) {
                    graph.addEdge(new Node[] { sourceNode }, DefaultLabel.createLabel(labelText));
                } else {
                    graph.addEdge(sourceNode, DefaultLabel.createLabel(labelText), targetNode);
                }
            }
        }
        graph.setFixed();
        return new Pair<Graph,Map<String,Node>>(graph, nodeIds);
    }

    /**
     * Implementation of <tt>AbstractXml</tt>'s abstract method.
     * Returns the graph factory set by the constructor.
     * @see #BindingGxl(GraphFactory)
     */
    @Override
    protected GraphFactory getGraphFactory() {
        return graphFactory;
    }

    protected final GraphFactory graphFactory;
}