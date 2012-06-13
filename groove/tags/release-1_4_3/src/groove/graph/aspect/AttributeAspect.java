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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import groove.algebra.Algebra;
import groove.algebra.Constant;
import groove.algebra.DefaultBooleanAlgebra;
import groove.algebra.DefaultIntegerAlgebra;
import groove.algebra.DefaultStringAlgebra;
import groove.algebra.Operation;
import groove.algebra.UnknownSymbolException;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Node;
import groove.graph.algebra.AlgebraEdge;
import groove.graph.algebra.AlgebraGraph;
import groove.graph.algebra.ProductEdge;
import groove.graph.algebra.ProductNode;
import groove.graph.algebra.ValueNode;
import groove.util.FormatException;
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
    private static final Map<AspectValue, Algebra> algebraMap = new HashMap<AspectValue, Algebra>();
    /** 
     * Map from algebras to aspect values that represent them. 
     * TODO there should be a better way to look up this relation
     */
    private static final Map<Algebra, AspectValue> aspectValueMap = new HashMap<Algebra, AspectValue>();
    
    /** 
     * Adds a pair of algebra and aspect value to the internal maps
     * {@link #algebraMap} and {@link #aspectValueMap}.
     */
    private static void addAlgebra(Algebra algebra, AspectValue value) {
    	Algebra oldAlgebra = algebraMap.put(value, algebra);
    	AspectValue oldValue = aspectValueMap.put(algebra, value);
    	if (oldAlgebra != null || oldValue != null) {
    		throw new IllegalStateException(String.format("Duplicate algebra %s", algebra));
    	}
    }
    
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
		    addAlgebra(DefaultIntegerAlgebra.getInstance(), INTEGER);
		    addAlgebra(DefaultBooleanAlgebra.getInstance(), BOOLEAN);
		    addAlgebra(DefaultStringAlgebra.getInstance(), STRING);
		} catch (FormatException exc) {
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
     * Tests if a given aspect element carries an {@link AttributeAspect} value
     * that corresponds to a type.
     */
    static public boolean isTypeElement(AspectElement elem) {
    	return algebraMap.containsKey(elem);
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
     * @throws FormatException if attribute-related errors are found in <code>graph</code> 
     */
    public static Node createAttributeNode(AspectNode node, AspectGraph graph) throws FormatException {
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
	 * @throws FormatException if the outgoing edges of <code>node</code>
	 * are incorrect
	 */
	private static Node createValueNode(AspectNode node, AspectGraph graph) throws FormatException {
		Node result;
		// check if there is a single constant edge on this node
		Collection<AspectEdge> outEdges = graph.outEdgeSet(node);
		if (outEdges.isEmpty()) {
			result = new ValueNode();
		} else if (outEdges.size() > 1) {
			throw new FormatException("Too many edges on constant node: %s", outEdges);
		} else {
			AspectEdge outEdge = outEdges.iterator().next();
			AspectValue algebraValue = outEdge.getValue(getInstance());
			if (algebraValue == null) {
				throw new FormatException("Label %s on value node should be a constant", outEdge.getLabelText());
			}
			Algebra algebra = algebraMap.get(algebraValue);
			if (algebra == null) {
				throw new FormatException("Label %s on value node should be a constant", outEdge.getLabelText());
			}
			try {
				Operation nodeValue = algebra.getOperation(outEdge.label().text());
				if (! (nodeValue instanceof Constant)) {
					throw new FormatException("Operation %s on value node should be a constant", outEdge.label());
				}
				result = AlgebraGraph.getInstance().getValueNode(((Constant) nodeValue));
			} catch (UnknownSymbolException exc) {
				throw new FormatException(exc.getMessage());
			}
		}
		return result;
	}
	
	/**
	 * Creates a product node corresponding to a node with {@link AttributeAspect} value {@link #PRODUCT}.
	 * This only succeeds if the outgoing edges form a consecutive range of argument
	 * numbers, without duplication.
	 * @param node the node for which a {@link ProductNode} is to be created
	 * @param graph the graph in which <code>node</code> occurs
	 * @throws FormatException if the outgoing edges of <code>node</code>
	 * are incorrect
	 */
	private static ProductNode createProductNode(AspectNode node, AspectGraph graph) throws FormatException {
		return new ProductNode(arity(node, graph));
	}
	
	/** 
	 * Returns the number of outgoing argument edges.
	 * Argument edges are aspect edges with {@link AttributeAspect}
	 * value {@link #ARGUMENT}. Also checks that the argument edges actually
	 * form a consecutive sequence ranging from 0 to the arity. 
	 */
	private static int arity(AspectNode node, AspectGraph graph) throws FormatException {
		Set<Integer> argNumbers = new HashSet<Integer>();
		int maxArgNumber = -1;
		int result = 0;
		for (AspectEdge outEdge: graph.outEdgeSet(node)) {
			if (outEdge.getValue(getInstance()) == ARGUMENT) {
				try {
					int argNumber = Integer.parseInt(outEdge.label().text());
					if (! argNumbers.add(argNumber)) {
						throw new FormatException("Duplicate argument edge %d", argNumber);
					}
					maxArgNumber = Math.max(maxArgNumber, argNumber);
					result++;
				} catch (NumberFormatException exc) {
					throw new FormatException("Argument edge label %s is not a valid argument number", outEdge.label());
				}
			}
		}
		if (result != maxArgNumber+1) {
			throw new FormatException("Argument numbers %s do not form a consecutive range", argNumbers);
		}
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
     * @throws FormatException if attribute-related errors are found in <code>graph</code> 
     */
    public static Edge createAttributeEdge(AspectEdge edge, AspectGraph graph, Node[] ends) throws FormatException {
    	Edge result;
    	AspectValue attributeValue = edge.getValue(getInstance());
    	if (attributeValue == null) {
    		result = null;
    	} else if (attributeValue == ARGUMENT) {
    		AlgebraEdge argEdge = createArgumentEdge(edge, ends);
    		argEdge.source().setArgument(argEdge.getNumber(), argEdge.target());
    		result = argEdge;
    	} else {
    		assert algebraMap.containsKey(attributeValue);
    		result = createOperatorEdge(edge, graph, ends);
    	}
    	return result;
    }
	
    /**
     * Creates and returns a fresh {@link ProductEdge} derived from 
     * a given aspect edge (which should have attribute value {@link #PRODUCT}).
     * @param edge the edge for which the image is to be created
     * @param graph
     * @param ends the end nodes of the edge to be created
     * @return a fresh {@link ProductEdge}
     * @throws FormatException if <code>edge</code> does not have a correct 
     * set of outgoing attribute edges in <code>graph</code>
     */
	private static Edge createOperatorEdge(AspectEdge edge, AspectGraph graph, Node[] ends) throws FormatException {
		try {
			Algebra algebra = algebraMap.get(edge.getValue(getInstance()));
			Operation operator = algebra.getOperation(edge.label().text());
			ProductNode source = (ProductNode) ends[Edge.SOURCE_INDEX];
			if (operator.arity() != source.arity()) {
				throw new FormatException("Arity of source node does not match arity of operation %s", operator);
			}
			ValueNode target = (ValueNode) ends[Edge.TARGET_INDEX];
			return new ProductEdge(source, target, operator);
		} catch (UnknownSymbolException exc) {
			throw new FormatException(exc.getMessage());
		}
	}
	
	/**
	 * Returns an {@link AlgebraEdge} derived from a given 
	 * aspect edge (which should have attribute aspect value {@link #ARGUMENT}).
	 * @param edge the edge of which the image is to be created
	 * @param ends the end nodes of the edge to be created
	 * @return a fresh {@link AlgebraEdge}
	 */
	private static AlgebraEdge createArgumentEdge(AspectEdge edge, Node[] ends) {
		return new AlgebraEdge(ends[Edge.SOURCE_INDEX], edge.label(), ends[Edge.TARGET_INDEX]);
	}
	
	/**
	 * Returns the aspect value corresponding to a given signature.
	 */
	public static AspectValue getValue(Algebra algebra) {
		return aspectValueMap.get(algebra);
	}
	
	/**
	 * Returns the appropriate attribute aspect value associated with
	 * an (ordinary) graph element, if any.
	 * The value depends on the type of the element. 
	 * @param elem the graph element for which an attribute aspect value is required
	 * @return the attribute aspect value for <code>elem</code>, such that <code>result.getAspect() == getInstance()</code>,
	 * or <code>null</code> if <code>elem</code> does not have any attribute information.
	 */
	public static AspectValue getAttributeValue(Element elem) {
		if (elem instanceof ValueNode) {
			return VALUE;
		} else if (elem instanceof ProductNode) {
			return PRODUCT;
		} else if (elem instanceof AlgebraEdge) {
			return ARGUMENT;
		} else if (elem instanceof ProductEdge) {
			Operation operation = ((ProductEdge) elem).getOperation();
			return aspectValueMap.get(operation.algebra());
		} else {
			return null;
		}
	}
    
    /** Private constructor to create the singleton instance. */
    private AttributeAspect() {
        super(ATTRIBUTE_ASPECT_NAME);
    }
}