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
package groove.graph.aspect;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import groove.algebra.Algebra;
import groove.algebra.Constant;
import groove.algebra.DefaultBooleanAlgebra;
import groove.algebra.DefaultIntegerAlgebra;
import groove.algebra.DefaultStringAlgebra;
import groove.algebra.Operation;
import groove.algebra.UnknownSymbolException;
import groove.algebra.Variable;
import groove.graph.Edge;
import groove.graph.GraphFormatException;
import groove.graph.Node;
import groove.graph.algebra.AlgebraConstants;
import groove.graph.algebra.AlgebraEdge;
import groove.graph.algebra.ProductEdge;
import groove.graph.algebra.ProductNode;
import groove.graph.algebra.ValueNode;
import groove.util.Groove;

/**
 * Graph aspect dealing with primitive data types (attributes).
 * Relevant information is: the type, and the role of the element.
 * @author Arend Rensink
 * @version $Revision$
 */
public class AttributeAspect extends AbstractAspect {
    /**
     * The name of the attribute aspect.
     */
    public static final String ATTRIBUTE_ASPECT_NAME = "attribute";

    /**
     * The singleton instance of this class.
     */
    private static final AttributeAspect instance = new AttributeAspect();

    /** Name of the argument aspect value. */
    public static final String ARGUMENT_NAME = Groove.getXMLProperty("label.argument.prefix");
    /** The argument aspect value. */
    public static final AspectValue ARGUMENT;
    /** Name of the attribute aspect value. */
    public static final String VALUE_NAME = Groove.getXMLProperty("label.attribute.prefix");
    /** The attribute aspect value. */
    public static final AspectValue VALUE;
    /** Name of the product aspect value. */
    public static final String PRODUCT_NAME = Groove.getXMLProperty("label.product.prefix");
    /** The product aspect value. */
    public static final AspectValue PRODUCT;
    /** Name of the integer aspect value. */
    public static final String INTEGER_NAME = Groove.getXMLProperty("label.integer.prefix");
    /** The integer aspect value. */
    public static final AspectValue INTEGER;
    /** Name of the boolean aspect value. */
    public static final String BOOLEAN_NAME = Groove.getXMLProperty("label.boolean.prefix");
    /** The boolean aspect value. */
    public static final AspectValue BOOLEAN;
    /** Name of the string aspect value. */
    public static final String STRING_NAME = Groove.getXMLProperty("label.string.prefix");
    /** The string aspect value. */
    public static final AspectValue STRING;

    /** 
     * Map from aspect values to those algebras that they represent. 
     * TODO there should be a better way to look up this relation
     */
    private static Map<AspectValue, Algebra> algebraMap;
    
	static {
		try {
		    ARGUMENT = instance.addEdgeValue(ARGUMENT_NAME);
		    VALUE = instance.addNodeValue(VALUE_NAME);
		    PRODUCT = instance.addNodeValue(PRODUCT_NAME);
		    INTEGER = instance.addEdgeValue(INTEGER_NAME);
		    BOOLEAN = instance.addEdgeValue(BOOLEAN_NAME);
		    STRING = instance.addEdgeValue(STRING_NAME);
		    ARGUMENT.setEdgeToSource(PRODUCT);
		    ARGUMENT.setEdgeToTarget(VALUE);
		    INTEGER.setEdgeToTarget(VALUE);
		    BOOLEAN.setEdgeToTarget(VALUE);
		    STRING.setEdgeToTarget(VALUE);
		    // no AttributeAspect values can be combined with creators or erasers
		    for (AspectValue value: instance.getValues()) {
		    	value.setIncompatible(RuleAspect.CREATOR);
		    	value.setIncompatible(RuleAspect.ERASER);
		    	value.setIncompatible(RuleAspect.EMBARGO);
		    }
		    // initialise the algebra map
		    algebraMap = new HashMap<AspectValue, Algebra>();
		    algebraMap.put(INTEGER, DefaultIntegerAlgebra.getInstance());
		    algebraMap.put(BOOLEAN, DefaultBooleanAlgebra.getInstance());
		    algebraMap.put(STRING, DefaultStringAlgebra.getInstance());
		} catch (GraphFormatException exc) {
			throw new Error("Aspect '" + ATTRIBUTE_ASPECT_NAME
					+ "' cannot be initialised due to name conflict", exc);
		}
    }
	
    /**
     * Returns the singleton instance of this aspect.
     */
    public static AttributeAspect getInstance() {
        return instance;
    }
    
    /** 
     * Creates an attribute-related node from a given
     * {@link AspectNode} found in a given {@link AspectGraph}. The type of the resulting
     * node depends on the {@link AttributeAspect} value of the given node and 
     * its incident edges. 
     * The result is a {@link ValueNode} or {@link ProductNode}, or <code>null</code> if
     * the node contains no special {@link AttributeAspect} value.
     * An exception is thrown if the context of the node in the graph is incorrect.
     * @param node the node for which we want an attribute-related node
     * @param graph the graph containing <code>node</code>
     * @return a {@link ValueNode} or {@link ProductNode} corresponding to <code>node</code>, or <code>null</code>
     * @throws GraphFormatException if attribute-related errors are found in <code>graph</code> 
     */
    public static Node createAttributeNode(AspectNode node, AspectGraph graph) throws GraphFormatException {
    	Node result;
    	AspectValue attributeValue = node.getValue(getInstance());
    	if (attributeValue == null) {
    		result = null;
    	} else if (attributeValue == VALUE) {
    		result = createValueNode(node, graph);
    	} else {
    		assert attributeValue == PRODUCT;
    		result = createProductNode(node, graph);
    	}
    	return result;
    }

	/**
	 * Creates a {@link ValueNode} corresponding to a given aspect node 
	 * whose {@link AttributeAspect} value equals {@link #VALUE}.
	 * This is either a variable node (if the original node has no outgoing edges),
	 * or a constant node whose value depends on the label of the node's self-edge.
	 * @param node the node for which a {@link ValueNode} is to be created
	 * @param graph the graph in which <code>node</code> occurs
	 * @throws GraphFormatException if the outgoing edges of <code>node</code>
	 * are incorrect
	 */
	private static Node createValueNode(AspectNode node, AspectGraph graph) throws GraphFormatException {
		Node result;
		// check if there is a constant edge on this node
		Set<AspectEdge> outEdges = graph.outEdgeSet(node);
		if (outEdges.isEmpty()) {
			result = new ValueNode(new Variable());
		} else {
			AspectEdge outEdge = outEdges.iterator().next();
			AspectValue algebraValue = outEdge.getValue(getInstance());
			if (algebraValue == null) {
				throw new GraphFormatException("Label %s of value node should be a constant", outEdge.getLabelText());
			}
			Algebra algebra = algebraMap.get(algebraValue);
			if (algebra == null) {
				throw new GraphFormatException("Label %s of value node should be a constant", outEdge.getLabelText());
			}
			try {
				Operation nodeValue = algebra.getOperation(outEdge.label().text());
				if (! (nodeValue instanceof Constant)) {
					throw new GraphFormatException("Operation %s on value node should be a constant", outEdge.label());
				}
				result = new ValueNode((Constant) nodeValue);
			} catch (UnknownSymbolException exc) {
				throw new GraphFormatException(exc.getMessage());
			}
		}
		return result;
	}
	
	/**
	 * Creates a product node corresponding to a node with {@link AttributeAspect} value {@link #PRODUCT}.
	 * @param node the node for which a {@link ProductNode} is to be created
	 * @param graph the graph in which <code>node</code> occurs
	 * @throws GraphFormatException if the outgoing edges of <code>node</code>
	 * are incorrect
	 */
	private static ProductNode createProductNode(AspectNode node, AspectGraph graph) throws GraphFormatException {
		ProductNode result;
		int argumentCount = 0;
		int maxArgumentNr = -1;
		result = new ProductNode();
		return result;
	}
    
    /** 
     * Creates an attribute-related edge from a given
     * {@link AspectEdge} found in a given {@link AspectGraph}. The type of the
     * resulting edge depends on the {@link AttributeAspect} value of the given edge. 
     * The result is a {@link ProductEdge} or {@link AlgebraEdge}, or <code>null</code> if
     * the edge contains no special {@link AttributeAspect} value.
     * An exception is thrown if the context of the edge in the graph is incorrect.
     * @param edge the edge for which we want an attribute-related edge
     * @param graph the graph containing <code>edge</code>
     * @return a {@link ProductEdge} or {@link AlgebraEdge} corresponding to <code>edge</code>, or <code>null</code>
     * @throws GraphFormatException if attribute-related errors are found in <code>graph</code> 
     */
    public static Edge createAttributeEdge(AspectEdge edge, AspectGraph graph, Node source, Node target) throws GraphFormatException {
    	Edge result;
    	AspectValue attributeValue = edge.getValue(getInstance());
    	if (attributeValue == null) {
    		result = null;
    	} else if (attributeValue == ARGUMENT) {
    		result = createArgumentEdge(edge, graph, source, target);
    	} else {
    		assert attributeValue == INTEGER || attributeValue == BOOLEAN || attributeValue == STRING;
    		result = createOperationEdge(edge, graph, source, target);
    	}
    	return result;
    }
	
	private static ProductEdge createOperationEdge(AspectEdge edge, AspectGraph graph, Node source, Node target) throws GraphFormatException {
		try {
			Algebra algebra = algebraMap.get(edge.getValue(getInstance()));
			Operation operation = algebra.getOperation(edge.label().text());
			ProductEdge result = new ProductEdge((ProductNode) source, (ValueNode) target, operation);
			return result;
		} catch (UnknownSymbolException exc) {
			throw new GraphFormatException(exc.getMessage());
		}
	}
	
	private static AlgebraEdge createArgumentEdge(AspectEdge edge, AspectGraph graph, Node source, Node target) throws GraphFormatException {
		AlgebraEdge result = new AlgebraEdge(source, edge.label(), target);
		return result;
	}
    
    /** Private constructor to create the singleton instance. */
    private AttributeAspect() {
        super(ATTRIBUTE_ASPECT_NAME);
    }
}
