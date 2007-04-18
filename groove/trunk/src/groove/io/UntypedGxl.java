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
 * $Id: UntypedGxl.java,v 1.4 2007-04-18 08:36:21 rensink Exp $
 */
package groove.io;

import groove.graph.DefaultLabel;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphFactory;
import groove.graph.GraphShape;
import groove.graph.HyperEdge;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.iso.DefaultIsoChecker;
import groove.graph.iso.IsoChecker;
import groove.util.FormatException;
import groove.util.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
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
 * @version $Revision: 1.4 $
 */
public class UntypedGxl extends AbstractXml {
    /**
     * The following implementation of <tt>Label</tt> allows us to
     * use graphs to store attribute maps. It does not strictly satisfy
     * the contract of <tt>Label</tt> since parsing is not supported.
     */
    static public class AttributeLabel implements Label {
        public AttributeLabel(Map<String, String> attributes) {
            this.attributes = new HashMap<String,String>(attributes);
        }

        /**
         * Parsing is not supported for attribute labels.
         * @throws UnsupportedOperationException always
         */
        @Deprecated
        public Label parse(String text) throws FormatException {
            throw new UnsupportedOperationException();
        }

        public String text() {
            return attributes.toString();
        }

        @Override
        public String toString() {
            return attributes.toString();
        }

        public Map<String, String> getAttributes() {
            return attributes;
        }

        private final Map<String,String> attributes;

        /**
         * This implementation compares the string descriptions (as given by
         * <tt>toString</tt>).
         */
        public int compareTo(Label o) {
            return toString().compareTo(o.toString());
        }
    }

    static public class AttributeEdge extends HyperEdge {
        public AttributeEdge(Node source, AttributeLabel label, Node target) {
            this(new Node[] { source, target }, label);
        }
        
        public AttributeEdge(Node[] ends, AttributeLabel label) {
            super(ends, label);
        }
        
        public AttributeEdge(Node source, Map<String, String> attributes, Node target) {
            this(source, new AttributeLabel(attributes), target);
        }
    }
    
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
        System.out.println("Test of groove.io.UntypedGxl");
        System.out.println("===================");
        groove.io.UntypedGxl gxl = new groove.io.UntypedGxl();
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
    public UntypedGxl(GraphFactory graphFactory) throws XmlRuntimeException {
        this.graphFactory = graphFactory;
    }

    /**
     * Constructs a Gxl transformer with a default graph factory.
     * @throws XmlRuntimeException if setting up the document builder 
     * fails for some internal reason
     * @see #getDefaultGraphFactory()
     */
    public UntypedGxl() throws XmlRuntimeException {
        this(getDefaultGraphFactory());
    }

    /**
	 * This implementation works by converting the graph to an attributed graph
	 * using {@link #attrToGxlGraph}, and marshalling the result
	 * using {@link #marshalGxlGraph}.
	 */
	public void marshalGraph(Graph graph, File file) throws FormatException, IOException {
	    Graph attrGraph = normToAttrGraph(graph);
	    groove.gxl.Graph gxlGraph = attrToGxlGraph(attrGraph);
	    // now marshal the attribute graph
	    marshalGxlGraph(gxlGraph, file);
	}

	/**
	 * This implementation works by unmarshalling to an attributed graph using
	 * {@link #unmarshalGxlGraph} and {@link #gxlToAttrGraph}, and converting
	 * the result to an ordinary graph using <tt>{@link #attrToNormGraph}</tt>.
	 */
	@Override
	public Pair<Graph,Map<String,Node>> unmarshalGraphMap(File file)
			throws FormatException, FileNotFoundException {
		groove.gxl.Graph gxlGraph = unmarshalGxlGraph(file);
		Pair<Graph,Map<String,Node>> attrGraph = gxlToAttrGraph(gxlGraph);
		Graph result = attrToNormGraph(attrGraph.first());
		return new Pair<Graph,Map<String,Node>>(result, attrGraph.second());
	}

	/**
     * Converts an attributed graph to an untyped GXL graph.
     * The attributes are encoded in <tt>AttributeLabel</tt>s.
     * @param graph a graph with only <tt>AttributeLabel</tt>s
     * @return the resulting GXL graph
     */
    private groove.gxl.Graph attrToGxlGraph(Graph graph) {
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
            } else if (edge.endCount() > 2) {
                throw new IllegalArgumentException("Hyperedge " + edge + " not supported");
            }
            // add the attributes from the map
            assert edge.label()
                instanceof AttributeLabel : "Label " + edge.label() + " should have been an attribute label";
            for (Map.Entry<String,String> entry: ((AttributeLabel) edge.label()).getAttributes().entrySet()) {
                groove.gxl.Value gxlLabelText = new groove.gxl.Value();
                gxlLabelText.setString(entry.getValue().toString());
                groove.gxl.Attr gxlLabelAttr = new groove.gxl.Attr();
                gxlLabelAttr.setName(entry.getKey());
                gxlLabelAttr.setValue(gxlLabelText);
                gxlEdge.addAttr(gxlLabelAttr);
            }
            // add the edge to the graph
            groove.gxl.GraphTypeItem gxlGraphTypeItem = new groove.gxl.GraphTypeItem();
            gxlGraphTypeItem.setEdge(gxlEdge);
            gxlGraph.addGraphTypeItem(gxlGraphTypeItem);
        }
        return gxlGraph;
    }

    /**
     * Converts an untyped GXL graph to an attributed (groove) graph.
     * Node attributes are ignored.
     * Edge attributes are encoded in <tt>AttributeLabel</tt>s.
     * The method returns a map from GXL node ids to <tt>Node</tt>s.
     * @param gxlGraph the source of the unmarhalling
     * @return graph the resulting attribute graph; i.e., it will receive only <tt>AttributeLabel</tt>s
     */
    private Pair<Graph, Map<String, Node>> gxlToAttrGraph(groove.gxl.Graph gxlGraph) {
        Graph graph = getGraphFactory().newGraph();
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
                Map<String,String> attributes = new HashMap<String,String>();
                Enumeration<?> attrEnum = gxlEdge.enumerateAttr();
                while (attrEnum.hasMoreElements()) {
                    groove.gxl.Attr attr = (groove.gxl.Attr) attrEnum.nextElement();
                    attributes.put(attr.getName().toLowerCase(), attr.getValue().getString());
                }
                graph.addEdge(createEdge(sourceNode, attributes, targetNode));
            }
        }
        return new Pair<Graph,Map<String,Node>>(graph, nodeIds);
    }

    /**
     * Converts an ordinary graph into an attribute graph,
     * by turning edge labels into maps with a <tt>LABEL_ATTR_NAME</tt>-key.
     * @param graph the original graph
     * @return the new, equivalent attribute graph.
     */
    private Graph normToAttrGraph(GraphShape graph) {
        Graph attrGraph = graphFactory.newGraph();
        // just copy the nodes
        attrGraph.addNodeSet(graph.nodeSet());
        // turn the edges into attribute maps and store those
        Map<String,String> labelAttr = new HashMap<String,String>();
        for (Edge edge: graph.edgeSet()) {
            labelAttr.put(LABEL_ATTR_NAME, edge.label().text());
            attrGraph.addEdge(createEdge(edge.ends(), labelAttr));
        }
        attrGraph.setFixed();
        return attrGraph;
    }

    /**
     * Converts an attribute graph into an ordinary graph,
     * by turning maps with a <tt>LABEL_ATTR_NAME</tt>-key into a {@link DefaultLabel}.
     * @param attrGraph the original attributed graph
     * @return the new, equivalent graph.
     */
    private Graph attrToNormGraph(Graph attrGraph) {
        Graph graph = getGraphFactory().newGraph();
        // Simply copy the nodes
        graph.addNodeSet(attrGraph.nodeSet());
        // Take the label value of the attribute labels
        for (Edge edge: attrGraph.edgeSet()) {
            Map<String,String> attributes = ((AttributeLabel) edge.label()).getAttributes();
            graph.addEdge(edge.ends(), DefaultLabel.createLabel(attributes.get(LABEL_ATTR_NAME)));
        }
        return graph;
    }

    /**
     * Marshals a GXL graph to an untyped GXL writer.
     * @param gxlGraph the GXL graph
     * @param file the destination for the marshalling operation
     */
    private void marshalGxlGraph(groove.gxl.Graph  gxlGraph, File file) {
        groove.gxl.Gxl gxl = new groove.gxl.Gxl();
        gxl.addGraph(gxlGraph);
        try {
            gxl.validate();
            Writer writer = new FileWriter(file);
            Marshaller marshaller = new Marshaller(writer);
            marshaller.marshal(gxl);
            writer.close();
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

    /**
     * Unmarshals an untyped GXL reader to a GXL graph.
     * @param file the source of the unmarhalling
     * @return the resulting GXL graph
     */
    private groove.gxl.Graph unmarshalGxlGraph(File file) throws FormatException, FileNotFoundException {
        // get a gxl object from the reader
        groove.gxl.Gxl gxl;
        try {
            gxl = new groove.gxl.Gxl();
            Unmarshaller unmarshaller = new Unmarshaller(gxl);
            unmarshaller.setLogWriter(new PrintWriter(System.err));
            Reader reader = new FileReader(file);
            unmarshaller.unmarshal(reader);
        } catch (MarshalException e) {
            throw new FormatException("Error while unmarshalling %s: %s", file, e.getMessage());
        } catch (ValidationException e) {
            throw new FormatException("Error while unmarshalling %s: %s", file, e.getMessage());
        }

        // now convert the gxl to an attribute graph        
        if (gxl.getGraphCount() != 1)
            throw new FormatException("Only one graph allowed in document");
        // Get the first and only graph element
        return gxl.getGraph(0);
    }

    /**
	 * Callback factory method to create an attribute edge with given ends and
	 * attribute map.
	 */
    protected AttributeEdge createEdge(Node[] ends, Map<String, String> attributes) {
        return new AttributeEdge(ends, new AttributeLabel(attributes));
    }

    /**
     * Callback factory method to create an attribute edge with given
     * source node, and a label based on a given attribute map. The edge will be unary of
     * <code>targetNode == null</code>, binary otherwise.
     */
    protected AttributeEdge createEdge(Node sourceNode, Map<String, String> attributes, Node targetNode) {
        if (targetNode == null) {
            return new AttributeEdge(new Node[] { sourceNode }, new AttributeLabel(attributes));
        } else {
            return new AttributeEdge(new Node[] { sourceNode, targetNode }, new AttributeLabel(attributes));            
        }
    }
}