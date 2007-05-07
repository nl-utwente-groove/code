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
 * $Id: AspectGraph.java,v 1.3 2007-05-07 17:24:35 rensink Exp $
 */
package groove.view.aspect;

import groove.graph.DefaultLabel;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.graph.GraphShape;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.NodeEdgeHashMap;
import groove.graph.NodeEdgeMap;
import groove.graph.NodeSetEdgeSetGraph;
import groove.util.Groove;
import groove.view.FormatException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Graph implementation to convert from a label prefix representation
 * of an aspect graph to a graph where the aspect values are stored in
 * {@link AspectNode}s and {@link AspectEdge}s.
 * @author Arend Rensink
 * @version $Revision $
 */
public class AspectGraph extends NodeSetEdgeSetGraph {
	/**
	 * Constructor that returns an empty graph.
	 */
	public AspectGraph() {
	    this.errors = Collections.emptyList();
	}

	/**
	 * Specialises the return type.
	 */
	@Override
	public Set<AspectEdge> edgeSet() {
		return (Set<AspectEdge>) super.edgeSet();
	}

	/**
	 * Specialises the return type.
	 */
	@Override
	public Set<AspectEdge> outEdgeSet(Node node) {
		return (Set<AspectEdge>) super.outEdgeSet(node);
	}

	/**
	 * Specialises the return type.
	 */
	@Override
	public Set<AspectNode> nodeSet() {
		return (Set<AspectNode>) super.nodeSet();
	}

    /** 
     * Returns the list of format errors in this graph.
     * If the list is empty, the graph has no errors.
     * @return a possubly empty, non-<code>null</code> list of format errors in this aspect graph
     */
    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    /** 
     * Indicates if this aspect graph has format errors.
     * Convenience method for <code>! getErrors().isEmpty()</code>.
     * @return <code>true</code> if this aspect graph has format errors
     */
    public boolean hasErrors() {
        return ! errors.isEmpty();
    }

    /** Sets the list of errors to a opy of a given list. */
    private void setErrors(List<String> errors) {
        this.errors = new ArrayList<String>(errors);
    }

    /**
	 * Method that returns an {@link AspectGraph} based on a graph
	 * whose edges are interpreted as aspect value prefixed.
	 * This means that nodes with self-edges that have no text (apart from 
	 * their aspect prefixes) are treated as indicating the node aspect.
     * The method never throws an exception, but the resulting graph
     * may have format errors, reported in {@link #getErrors()}.
	 * @param graph the graph to take as input.
	 */
	public AspectGraph fromPlainGraph(GraphShape graph) {
		// map from original graph elements to aspect graph elements
		NodeEdgeMap elementMap = new NodeEdgeHashMap();
		return fromPlainGraph(graph, elementMap);
	}

	/**
	 * Method that returns an {@link AspectGraph} based on a graph
	 * whose edges are interpreted as aspect value prefixed.
	 * This means that nodes with self-edges that have no text (apart from 
	 * their aspect prefixes) are treated as indicating the node aspect.
	 * The mapping from the old to the new graph is stored in a parameter.
     * The method never throws an exception, but the resulting graph
     * may have format errors, reported in {@link #getErrors()}.
	 * @param graph the graph to take as input.
	 * @param elementMap output parameter for mapping from plain graph elements to resulting {@link AspectGraph} elements;
	 * should be initially empty
	 */
	private AspectGraph fromPlainGraph(GraphShape graph, NodeEdgeMap elementMap) {
        List<String> errors = new ArrayList<String>();
		assert elementMap != null && elementMap.isEmpty();
		AspectGraph result = new AspectGraph();
		Set<Edge> edges = new HashSet<Edge>(graph.edgeSet());
		// first do the nodes;
		// map from original graph nodes to aspect graph nodes
		Map<Node,AspectNode> nodeMap = new HashMap<Node,AspectNode>();
		for (Node node: graph.nodeSet()) {
			AspectNode nodeImage = result.createNode();
			result.addNode(nodeImage);
			// update the maps
			nodeMap.put(node, nodeImage);
			elementMap.putNode(node, nodeImage);
		}
		// look for node aspect indicators
		for (Edge edge: graph.edgeSet()) {
			try {
                AspectNode sourceImage = nodeMap.get(edge.source());
                AspectNode targetImage = nodeMap.get(edge.opposite());
                String labelText = edge.label().text();
                AspectValue nodeValue = getNodeValue(edge);
                if (nodeValue != null) {
                	// the edge encodes a node aspect
                	sourceImage.setDeclaredValue(nodeValue);
                } else {
                	AspectParseData aspectLabel = parser.getParseData(labelText);
                	// add inferred aspect values to the source and target
                	for (AspectValue edgeValue : aspectLabel.getAspectMap().values()) {
                		AspectValue sourceValue = edgeValue.edgeToSource();
                		if (sourceValue != null) {
                			sourceImage.setInferredValue(sourceValue);
                		}
                		AspectValue targetValue = edgeValue.edgeToTarget();
                		if (targetValue != null) {
                			targetImage.setInferredValue(targetValue);
                		}
                	}
                }
            } catch (FormatException e) {
                errors.addAll(e.getErrors());
            }
		}
		// Now iterate over the remaining edges
		for (Edge edge: edges) {
			try {
                if (getNodeValue(edge) == null) {
                	AspectParseData parseData = parser.getParseData(edge.label().text());
                	Edge edgeImage = createAspectEdge(nodeMap.get(edge.source()),
                			nodeMap.get(edge.opposite()),
                			parseData);
                	result.addEdge(edgeImage);
                	elementMap.putEdge(edge, edgeImage);
                }
            } catch (FormatException e) {
                errors.addAll(e.getErrors());
            }
		}
		GraphInfo.transfer(graph, result, elementMap);
        result.setErrors(errors);
		return result;
	}

	/** 
	 * Creates a graph where the aspect values are represented 
	 * as label prefixes for the edges, and as special edges for the nodes.
	 */
	public Graph toPlainGraph() {
		NodeEdgeMap elementMap = new NodeEdgeHashMap();
		return toPlainGraph(elementMap);
	}

	/** Returns the set of aspects of which this graph contains at least one value. */
	public Set<Aspect> getAspects() {
		Set<Aspect> result = new HashSet<Aspect>();
		for (AspectNode node: nodeSet()) {
			result.addAll(node.getAspectMap().keySet());
		}
		for (AspectEdge edge: edgeSet()) {
			result.addAll(edge.getAspectMap().keySet());
		}
		return result;
	}
	
	/** Convenience method to test if a given aspect is used in this graps. 
	 * @see #getAspects()
	 */
	public boolean hasAspect(Aspect aspect) {
		return getAspects().contains(aspect);
	}
	
	/** 
	 * Creates a graph where the aspect values are represented 
	 * as label prefixes for the edges, and as special edges for the nodes.	 
	 * The mapping from the old to the new graph is stored in a parameter.
	 * @param elementMap output parameter for mapping from plain graph elements to resulting {@link AspectGraph} elements;
	 * should be initially empty
	 */
	private Graph toPlainGraph(NodeEdgeMap elementMap) {
		Graph result = createPlainGraph();
		for (AspectNode node: nodeSet()) {
			Node nodeImage = result.addNode();
			elementMap.putNode(node, nodeImage);
			for (AspectValue value: node.getDeclaredValues()) {
				result.addEdge(nodeImage, createLabel(AspectParser.toString(value)), nodeImage);
			}
		}
		for (AspectEdge edge: edgeSet()) {
			Node[] nodeImages = new Node[edge.endCount()];
			for (int i = 0; i < edge.endCount(); i++) {
				nodeImages[i] = elementMap.getNode(edge.end(i));
			}
			Edge edgeImage = result.addEdge(nodeImages, createLabel(edge.getPlainText()));
			elementMap.putEdge(edge, edgeImage);
		}
		GraphInfo.transfer(this, result, elementMap);
		return result;
	}

	/**
	 * Tests if a given edge encodes a node aspect value, and returns that value.
	 * An edge encodes a node aspect value if it has no text of its own.
	 * Returns <code>null</code> if the edge does not encode a node aspect value,
	 * and throws an exception if the edge is not a self-edge or contains more than 
	 * one aspect value.
	 * @param edge the edge to be tested
	 * @return a node aspect value for the (unique) endpoint of the edge, or
	 * <code>null</code> if <code>edge</code> does not encode a node aspect value.
	 * @throws FormatException if <code>edge</code> does ancode a node aspect
	 * value, but is not a self-edge or contains more than one aspect value
	 */
	protected AspectValue getNodeValue(Edge edge) throws FormatException {
		AspectValue result;	
		String labelText = edge.label().text();
		AspectParseData parseData = parser.getParseData(labelText);
		if (!parseData.hasText()) {
			AspectMap aspectMap = parseData.getAspectMap();
			// this edge indicates a node aspect
			if (edge.opposite() != edge.source()) {
				// Only one aspect per node self-edge
				throw new FormatException("Node aspect indicator %s only allowed on self-edges",labelText);
			} else if (aspectMap.size() != 1) {
				// Only one aspect per node self-edge
				throw new FormatException("Multiple node aspect indicator "+labelText);
			} else {
				// add the aspect value found
				result = aspectMap.values().iterator().next();
			}
		} else {
			result = null;
		}
		return result;
	}

	/**
	 * Factory method for a <code>Graph</code>.
	 * @see #toPlainGraph()
	 */
	protected Graph createPlainGraph() {
		return new NodeSetEdgeSetGraph();
	}

	/**
	 * Factory method for an <code>AspectNode</code>.
	 */
	@Override
	public AspectNode createNode() {
		return new AspectNode(getNodeCounter().getNumber());
	}

	/**
	 * Factory method for an <code>AspectEdge</code>.
	 * @throws FormatException if the aspect label is inconsistent with the end node aspect values
	 */
	AspectEdge createAspectEdge(AspectNode source, AspectNode target, AspectParseData aspectLabel) throws FormatException {
		return new AspectEdge(source, target, aspectLabel);
	}
	
	/** 
	 * Creates a label from a string. 
	 * @see DefaultLabel#parseLabel(String) 
	 */
	protected Label createLabel(String text) {
		return DefaultLabel.createLabel(text);
	}
	
	/**
	 * Copies this aspect graph to one with the same nodes, edges and graph info.
	 */
	@Override
	public AspectGraph clone() {
		AspectGraph result = new AspectGraph();
		result.addNodeSet(nodeSet());
		result.addEdgeSetWithoutCheck(edgeSet());
		GraphInfo.transfer(this, result, null);
        result.setErrors(getErrors());
		return result;
	}
    
    /** Format errors in this aspect graph. */
    private List<String> errors;

    /**
     * Returns a factory for {@link AspectGraph}s, i.e., an object to
     * invoke {@link #fromPlainGraph(GraphShape)} upon.
     */
    public static AspectGraph getFactory() {
    	return factory;
    }

    /** 
     * Main method, taking a sequence of filenames and
     * testing conversion from plain to aspect graphs of
     * the graphs contained in those files.
     */
    public static void main(String[] args) {
    	if (args.length == 0) {
    		System.err.println("Call with sequence of files or directories");
    	}
    	for (String arg: args) {
    		File file = new File(arg);
    		if (! file.exists()) {
    			System.err.printf("File %s cannot be found", arg);
    		} else {
    			try {
    				testFile(file);
    			} catch (FormatException exc) {
    				exc.printStackTrace();
    			}
    		}
    	}
    }

    /**
     * Loads a graph from a file and tests its conversion from plain to aspect graph
     * and back, using {@link #testTranslation(Graph)}. 
     * Recursively descends into directories.
     */
    private static void testFile(File file) throws FormatException {
    	if (file.isDirectory()) {
    		for (File nestedFile: file.listFiles()) {
    			testFile(nestedFile);
    		}
    	} else try {
    		Graph plainGraph = Groove.loadGraph(file);
    		if (plainGraph != null) {
    			System.out.printf("Testing %s", file);
    			testTranslation(plainGraph);
    			System.out.println(" - OK");
    		}
    	} catch (IOException exc) {
    		// do nothing (skip)
    	}
    }

    /** 
     * Tests the {@link AspectGraph} implementation 
     * by translating a plain graph to an aspect graph and back,
     * and checking if the result is isomorphic to the original.
     * @throws FormatException if anything goes wrong in the translation
     */
    public static void testTranslation(Graph plainGraph) throws FormatException {
    	NodeEdgeMap fromPlainToAspect = new NodeEdgeHashMap();
    	NodeEdgeMap fromAspectToPlain = new NodeEdgeHashMap();
    	AspectGraph aspectGraph = getFactory().fromPlainGraph(plainGraph, fromPlainToAspect);
    	Graph result = aspectGraph.toPlainGraph(fromAspectToPlain);
    	if (result.nodeCount() > plainGraph.nodeCount()) {
    		throw new FormatException(
    				"Result graph has more nodes: %s (%d) than original: %s (%d)",
    				plainGraph.nodeSet(), plainGraph.nodeCount(),
    				result.nodeSet(), result.nodeCount());
    	}
    	if (result.edgeCount() > plainGraph.edgeCount()) {
    		throw new FormatException(
    				"Result graph has more nodes: %s (%d) than original: %s (%d)",
    				plainGraph.edgeSet(), plainGraph.edgeCount(),
    				result.edgeSet(), result.edgeCount());
    	}
    	for (Node plainNode: plainGraph.nodeSet()) {
    		Node aspectNode = fromPlainToAspect.getNode(plainNode);
    		if (aspectNode == null) {
    			throw new FormatException("Node %s not translated to aspect node", plainNode);
    		}
    		Node resultNode = fromAspectToPlain.getNode(aspectNode);
    		if (resultNode == null) {
    			throw new FormatException("Node %s translated to aspect node %s, but not back", plainNode, aspectNode);
    		}
    		Set<AspectValue> plainNodeValues = getNodeValues(plainGraph, plainNode);
    		Set<AspectValue> resultNodeValues = getNodeValues(result, resultNode);
    		if (! plainNodeValues.equals(resultNodeValues)) {
    			throw new FormatException("Node values for %s and %s differ: %s versus %s", plainNode, resultNode, plainNodeValues, resultNodeValues);
    		}
    	}
    	for (Edge plainEdge: plainGraph.edgeSet()) {
    		Edge aspectEdge = fromPlainToAspect.getEdge(plainEdge);
    		if (aspectGraph.getNodeValue(plainEdge) == null) {
    			if (aspectEdge == null) {
    				throw new FormatException(
    						"Edge %s not translated to aspect edge", plainEdge);
    			}
    			Edge resultEdge = fromAspectToPlain.getEdge(aspectEdge);
    			if (resultEdge == null) {
    				throw new FormatException(
    						"Edge %s translated to aspect edge %s, but not back", plainEdge, aspectEdge);
    			}
    		} else {
    			if (aspectEdge != null) {
    				throw new FormatException(
    						"Node value-encoding edge %s translated to aspect edge %s", plainEdge, aspectEdge);
    			}
    		}
    	}
    }

    /**
     * Retrieves all node values of a given node in a given (plain) graph.
     */
    private static Set<AspectValue> getNodeValues(Graph graph, Node node) throws FormatException {
    	Set<AspectValue> result = new HashSet<AspectValue>();
    	for (Edge outEdge: graph.outEdgeSet(node)) {
    		AspectValue nodeValue = getFactory().getNodeValue(outEdge);
    		if (nodeValue != null) {
    			result.add(nodeValue);
    		}
    	}
    	return result;
    }

    /**
     * The singleton aspect parser. 
     */
    private static final AspectParser parser = AspectParser.getInstance();

    /**
     * The static instance serving as a factory. 
     */
    private static final AspectGraph factory = new AspectGraph();
}
