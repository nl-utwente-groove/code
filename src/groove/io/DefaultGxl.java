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
 * $Id: DefaultGxl.java,v 1.3 2007-05-04 22:51:41 rensink Exp $
 */
package groove.io;

import groove.graph.DefaultLabel;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphFactory;
import groove.graph.GraphInfo;
import groove.graph.GraphProperties;
import groove.graph.GraphShape;
import groove.graph.HyperEdge;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.iso.DefaultIsoChecker;
import groove.graph.iso.IsoChecker;
import groove.util.Pair;
import groove.view.FormatException;

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
 * @version $Revision: 1.3 $
 */
public class DefaultGxl extends AbstractXml {
	/** The name of graphs whose name is not explicitly included in the graph info. */
	static public final String DEFAULT_GRAPH_NAME = "graph";
    /** Attribute name for node and edge ids. */
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

    /** Test method: tries loading and saving graphs, and comparing them for isomorphism. */
    static public void main(String[] args) {
        System.out.println("Test of groove.io.UntypedGxl");
        System.out.println("===================");
        groove.io.DefaultGxl gxl = new groove.io.DefaultGxl();
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
     */
    public DefaultGxl(GraphFactory graphFactory) {
        super(graphFactory);
    }

    /**
     * Constructs a Gxl transformer with a default graph factory.
     * @see #getDefaultGraphFactory()
     */
    public DefaultGxl() {
        this(getDefaultGraphFactory());
    }

    /**
	 * This implementation works by converting the graph to an attributed graph
	 * using {@link #attrToGxlGraph}, and marshalling the result
	 * using {@link #marshalGxlGraph}.
	 */
	public void marshalGraph(Graph graph, File file) throws IOException {
		// create the file, if necessary
		file.getParentFile().mkdirs();
		file.createNewFile();
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
	public Pair<Graph,Map<String,Node>> unmarshalGraphMap(File file) throws FormatException {
		Graph result;
		Map<String, Node> conversion;
		try {
			groove.gxl.Graph gxlGraph = unmarshalGxlGraph(file);
			Pair<Graph, Map<String, Node>> attrGraph = gxlToAttrGraph(gxlGraph);
			result = attrToNormGraph(attrGraph.first());
			conversion = attrGraph.second();
		} catch (FileNotFoundException exc) {
			result = getGraphFactory().newGraph();
			conversion = new HashMap<String,Node>();
		}
		GraphInfo.setFile(result, file);
		PriorityFileName priorityName = new PriorityFileName(file);
		GraphInfo.setName(result, priorityName.getActualName());
		if (priorityName.hasPriority()) {
			GraphInfo.getProperties(result, true).setPriority(priorityName.getPriority());
		}
		return new Pair<Graph,Map<String,Node>>(result, conversion);
	}

	public void deleteGraph(File file) {
		file.delete();
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
        String name = GraphInfo.getName(graph);
        name = name == null ? DEFAULT_GRAPH_NAME : name;
        gxlGraph.setId(name);
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
        // add the graph attributes
        GraphProperties properties = GraphInfo.getProperties(graph, false);
        if (properties != null) {
			for (Map.Entry<Object, Object> entry : properties.entrySet()) {
				String attrName = ((String) entry.getKey()).toLowerCase();
				String value = (String) entry.getValue();
				groove.gxl.Value gxlValue = new groove.gxl.Value();
				gxlValue.setString(value);
				groove.gxl.Attr gxlLabelAttr = new groove.gxl.Attr();
				gxlLabelAttr.setName(attrName);
				gxlLabelAttr.setValue(gxlValue);
				gxlGraph.addAttr(gxlLabelAttr);
			}
		}
        return gxlGraph;
    }

    /**
	 * Converts an untyped GXL graph to an attributed (groove) graph. Node
	 * attributes are ignored. Edge attributes are encoded in
	 * <tt>AttributeLabel</tt>s. The method returns a map from GXL node ids
	 * to <tt>Node</tt>s.
	 * 
	 * @param gxlGraph
	 *            the source of the unmarhalling
	 * @return graph the resulting attribute graph; i.e., it will receive only
	 *         <tt>AttributeLabel</tt>s
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
        // add the graph attributes
        Enumeration<?> attrEnum = gxlGraph.enumerateAttr();
        GraphProperties properties = new GraphProperties();
        while (attrEnum.hasMoreElements()) {
            groove.gxl.Attr attr = (groove.gxl.Attr) attrEnum.nextElement();
            String attrName = attr.getName().toLowerCase();
            if (isKnownPropertyKey(attrName)) {
            	groove.gxl.Value attrValue = attr.getValue();
            	Object dataValue;
            	if (attrValue.hasBool()) {
            		dataValue = attrValue.getBool();
            	} else if (attrValue.hasInt()) {
            		dataValue = attrValue.getInt();
            	} else if (attrValue.hasFloat()) {
            		dataValue = attrValue.getFloat();
            	} else {
            		dataValue = attrValue.getString();
            	}
            	properties.setProperty(attrName, dataValue.toString());
            }
        }
        if (!properties.isEmpty()) {
        	GraphInfo.setProperties(graph, properties);
        }
        GraphInfo.setName(graph, gxlGraph.getId());
        return new Pair<Graph,Map<String,Node>>(graph, nodeIds);
    }

    /**
     * Converts an ordinary graph into an attribute graph,
     * by turning edge labels into maps with a <tt>LABEL_ATTR_NAME</tt>-key.
     * @param graph the original graph
     * @return the new, equivalent attribute graph.
     */
    private Graph normToAttrGraph(GraphShape graph) {
        Graph attrGraph = getGraphFactory().newGraph();
        // just copy the nodes
        attrGraph.addNodeSet(graph.nodeSet());
        // turn the edges into attribute maps and store those
        Map<String,String> labelAttr = new HashMap<String,String>();
        for (Edge edge: graph.edgeSet()) {
            labelAttr.put(LABEL_ATTR_NAME, edge.label().text());
            attrGraph.addEdge(createEdge(edge.ends(), labelAttr));
        }
        transferGraphProperties(graph, attrGraph);
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
        transferGraphProperties(attrGraph, graph);
        return graph;
    }

    /**
	 * Transfers the graph information items with allowed property names
	 * from one graph to another.
	 * @param source the source graph
	 * @param target the target graph
	 * @see #getPropertyKeys()
	 */
	private void transferGraphProperties(GraphShape source, Graph target) {
		GraphInfo.setProperties(target, GraphInfo.getProperties(source, false));
	}

	/**
     * Marshals a GXL graph to an untyped GXL writer.
     * @param gxlGraph the GXL graph
     * @param file the destination for the marshalling operation
     * @throws IOException is the marshalling runs into some IO or XML errors
     */
    private void marshalGxlGraph(groove.gxl.Graph  gxlGraph, File file) throws IOException {
        groove.gxl.Gxl gxl = new groove.gxl.Gxl();
        gxl.addGraph(gxlGraph);
        try {
            gxl.validate();
            Writer writer = new FileWriter(file);
            Marshaller marshaller = new Marshaller(writer);
            marshaller.marshal(gxl);
            writer.close();
        } catch (MarshalException e) {
            throw new IOException(e.getMessage());
        } catch (ValidationException e) {
            throw new IOException(e.getMessage());
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
            throw new FormatException("Only one graph allowed in document %s", file);
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
    
	/**
	 * The following implementation of <tt>Label</tt> allows us to
	 * use graphs to store attribute maps. 
	 */
	static private class AttributeLabel implements Label {
		/** Constructs an instance, based on a given attribute map. */
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
	
	    /** Returns the attribute map of this label instance. */
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

	/** Edge carrying an attribute map on its label, with an unknown end count. */ 
	static private class AttributeEdge extends HyperEdge {
		/** Constructs in instance for given ends and label. */
	    public AttributeEdge(Node[] ends, AttributeLabel label) {
	        super(ends, label);
	    }
	}
}