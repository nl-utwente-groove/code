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
 * $Id: AlgebraConstants.java,v 1.7 2007-08-26 07:23:57 rensink Exp $
 */
package groove.graph.algebra;

import groove.algebra.Algebra;
import groove.algebra.Constant;
import groove.algebra.Operation;
import groove.algebra.UnknownSymbolException;
import groove.graph.BinaryEdge;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Node;
import groove.util.Groove;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectNode;
import groove.view.aspect.AttributeAspect;

import java.util.Iterator;

/**
 * Class containing all the constant values used for dealing with attributed graphs.
 * @author Harmen Kastenberg
 * @version $Revision: 1.7 $ $Date: 2007-08-26 07:23:57 $
 */
public class AlgebraConstants {
	/** Code for attributes of type integer. */
	static public final int INTEGER = 0;
	/** Code for attributes of type string. */
	static public final int STRING = 1;
	/** Code for attributes of type boolean. */
	static public final int BOOLEAN = 2;
	/** Code for attributes of type boolean. */
	static public final int NO_TYPE = -1;

	/** Code for non-argument nodes */
    static public final int NO_ARGUMENT = -1;
    /** Separator between prefix and rest of label. */
    static public final String SEPARATOR = Groove.getXMLProperty("label.aspect.separator");
    /** Prefix for the label text of attributes in general. */
    static public final String ATTRIBUTE_PREFIX = Groove.getXMLProperty("label.attribute.prefix") + SEPARATOR;
    /** Prefix for the label text of product nodes. */
    static public final String PRODUCT_PREFIX = Groove.getXMLProperty("label.product.prefix") + SEPARATOR;
    /** Prefix for the label text of argument-edges. */
    static public final String ARGUMENT_PREFIX = Groove.getXMLProperty("label.argument.prefix") + SEPARATOR;

    /** Prefix for the label text of integer attributes. */
    static public final String INTEGER_PREFIX = Groove.getXMLProperty("label.integer.prefix") + SEPARATOR;
    /** Prefix for the label text of string attributes. */
    static public final String STRING_PREFIX = Groove.getXMLProperty("label.string.prefix") + SEPARATOR;
    /** Prefix for the label text of boolean attributes. */
    static public final String BOOLEAN_PREFIX = Groove.getXMLProperty("label.boolean.prefix") + SEPARATOR;

    /** 
     * Array of attribute prefixes.
     * <b>Important:</b> the indices within this array correspond to the attribute code.
     */
    static public final String[] TYPE_PREFIX = { INTEGER_PREFIX, STRING_PREFIX, BOOLEAN_PREFIX };

    /**
     * The singleton {@link groove.graph.algebra.AlgebraGraph}-instance.
     */
    static private AlgebraGraph algebraGraph;

    /**
     * @return the singleton {@link groove.graph.algebra.AlgebraGraph}-instance
     */
    static private AlgebraGraph getAlgebraGraph() {
    	if (algebraGraph == null)
    		algebraGraph = AlgebraGraph.getInstance();
    	return algebraGraph;
    }

    /**
     * Checks whether the label equals the attribute-prefix indicating that this
     * node represents an attribute.
     * @param label the label whose role is to be investigated
     * @return <tt>true</tt> if the label-text equals the attribute-prefix, <tt>false</tt> otherwise.
     */
    static public boolean isAttributeLabel(Label label) {
        return label.text().equals(ATTRIBUTE_PREFIX);
    }

    /**
     * Checks whether the given label is a special product-label.
     * @param label the label to check
     * @return <tt>true</tt> if the label is a special product-label, <tt>false</tt> otherwise
     */
    static public boolean isProductLabel(Label label) {
        return label.text().equals(PRODUCT_PREFIX);
    }
    
    /**
     * Checks whether the given label is an argument label as used for denoting the
     * arguments of algebra-operations. If so, the index of the argument (derived from
     * the label) is returned. If not, -1 is returned.
     * 
     * @param label the label for which to check whether it is an argument-label
     * @return the index of the argument if the label is an argument-label, -1 otherwise
     */
    static public int isArgumentLabel(Label label) {
        int result = NO_ARGUMENT;
        if (label.text().startsWith(ARGUMENT_PREFIX)) {
            result = Integer.parseInt(label.text().substring(ARGUMENT_PREFIX.length()));
        }
        return result;
    }

    /**
     * Returns the role of a label as indicated by the prefix of its text.
     * @param label the label whose role is to be investigated
     * @return the role as indicated by <tt>label</tt>
     * @ensure <tt>isValidRole(result) || result == NO_ROLE</tt>
     */
    static public int labelType(Label label) {
        return labelType(label.text());
    }

    /**
     * Returns the role of a given label text.
     * @param text the label text from which we want to determine the role
     * @return the role as indicated by <tt>text</tt>
     * @ensure <tt>isValidRole(result) || result == NO_ROLE</tt>
     */
    static private int labelType(String text) {
        for (int i = 0; i < TYPE_PREFIX.length; i++) {
            if (text.startsWith(TYPE_PREFIX[i]))
                return i;
        }
        // no role prefix recognised: take default
        return AlgebraConstants.NO_TYPE;
    }
    
    /**
     * Returns the text of a label as minus its role prefix.
     * @param label the label whose text prefix is to be cut off
     * @return label text without its role prefix 
     */
    static private String labelText(Label label) {
        int type = labelType(label);
        if (type == AlgebraConstants.NO_TYPE)
            return label.text();
        else
            return label.text().substring(TYPE_PREFIX[type].length());
    }

    /**
     * Returns the type as indicated by an ordinary edge.
     * An edge indicates a type if it is a self-edge labelled only with the type prefix.
     * @param edge the label whose type indication is to be investigated
     * @return the type as indicated by <tt>edge</tt>
     * @ensure <tt>isValidType(result) || result == NO_TYPE</tt>
     */
    @Deprecated
    static public int selfEdgeType(Edge edge) {
        if (edge instanceof BinaryEdge && edge.source() != ((BinaryEdge) edge).target())
            return AlgebraConstants.NO_TYPE;
        else {
            int type = labelType(edge.label());
            if (type != AlgebraConstants.NO_TYPE && edge.label().text().equals(TYPE_PREFIX[type]))
                type = AlgebraConstants.NO_TYPE;
            return type;
        }
    }

    /**
     * Gets the algebraic value of a given node in a given graph.
     * @param node the node for which to determine the algebraic value it represents
     * @param graph the graph providing the edges on this node which are needed
     * to determine that algebraic value
     * @return the algebraic value the node represents
     */
    @Deprecated
    static public Constant getNodeValue(Graph graph, Node node) {
    	Constant result = null;

    	Edge selfEdge = null;

   		int nodeType = AlgebraConstants.NO_TYPE;
//   		AlgebraGraph algebraGraph = AlgebraGraph.getInstance();

   		// ok, now look up the type: it is indicated by the prefix of a self-edge
   		Iterator<? extends Edge> selfEdgeIter = graph.outEdgeSet(node).iterator();
   		while (nodeType == AlgebraConstants.NO_TYPE && selfEdgeIter.hasNext()) {
   			selfEdge = selfEdgeIter.next();
   			nodeType = AlgebraConstants.selfEdgeType(selfEdge);
   		}

   		if (nodeType != AlgebraConstants.NO_TYPE) {
   	   		Algebra algebra = getAlgebraGraph().getAlgebra(nodeType);
   	   		try {
   	   			result = (Constant) algebra.getOperation(AlgebraConstants.labelText(selfEdge.label()));
   	   		} catch (UnknownSymbolException use) {
   	   			use.printStackTrace();
   	   		}
   	   		return result;
   		}
   		else {
   			return null;
   		}
    }

    /**
     * If the given node represents an algebra node in the given
     * graph, this method returns an instance of the correct node-type.
     * If the node does not represent an algebra node, it will
     * return <tt>null</tt>.
     * @param node the node for which to determine its algebra-role
     * @param graph the graph the given node is in and, more importantly,
     * its adjacent edge
     * @return an instance of the correct node-type, or <tt>null</tt> if the
     * node has nothing to do with algebra-stuff
     * @deprecated No longer used; functionality taken over by {@link AttributeAspect#createAttributeNode(AspectNode, AspectGraph)}
     */
    @Deprecated
    public static Node getAlgebraNode(Graph graph, Node node) {
    	Node result = null;
    	// first check whether this node is an product node
    	result = getProductNode(graph, node);
    	if (result != null)
    		return result;

    	// then we check whether it represents a specific algebraic data value
    	result = getValueNode(graph, node);
    	if (result != null)
    		return result;

    	// at last, we check whether it represents a variable
    	result = getVariableNode(graph, node);
    	if (result != null)
    		return result;
    	return result;
    }

    /**
     * Given a node and the graph this node is in, it checks whether this node
     * represents a product node (i.e. an ordered tuple of data values). If so,
     * it returns a fresh instance of {@link groove.graph.algebra.ProductNode}.
     * If not, it returns <tt>null</tt>.
     * @param node the node for which to check whether it represents a product
     * @param graph the graph containing this node and, more importantly, its
     * adjacent edges
     * @return a fresh instance of {@link groove.graph.algebra.ProductNode} if
     * this nodes represents an product, <tt>null</tt> otherwise
     */
    static private Node getProductNode(Graph graph, Node node) {
    	Iterator<? extends Edge> selfEdgeIter = graph.edgeSet(node).iterator();
    	while (selfEdgeIter.hasNext()) {
    		Edge nextEdge = selfEdgeIter.next();
    		if (isProductLabel(nextEdge.label())) {
    			return new ProductNode(0);
    		}
    	}
    	return null;
    }

    /**
     * Given a node and the graph this node is in, it checks whether this node
     * represents an algebraic data value. If so, it returns the only
     * {@link groove.graph.algebra.ValueNode} for this data value, otherwise
     * <tt>null</tt>.
     * @param node the node for which to check whether it represents an algebraic
     * data value
     * @param graph the graph containing this node and, more importantly, its
     * adjacent edges
     * @return the {@link groove.graph.algebra.ValueNode} if the given node
     * represents an algebraic data value, <tt>null</tt> otherwise
     */
    @Deprecated
    static private Node getValueNode(Graph graph, Node node) {
    	Constant constant = getNodeValue(graph, node);
    	if (constant != null)
        	return AlgebraGraph.getInstance().getValueNode(constant);
    	return null;
    }

    /**
     * Given a node and the graph this node is in, it checks whether this node
     * represents a data variable. If so, it returns a fresh instance of
     * {@link groove.graph.algebra.ValueNode}. If not, it returns <tt>null</tt>.
     * @param node the node for which to check whether it represents a data variable
     * @param graph the graph containing this node and, more importantly, its
     * adjacent edges
     * @return a fresh instance of {@link groove.graph.algebra.ValueNode} if this
     * node represents a data variable, <tt>null</tt> otherwise
     * @deprecated No longer used; functionality taken over by {@link AttributeAspect#createAttributeNode(AspectNode, AspectGraph)}
     */
    @Deprecated
    public static Node getVariableNode(Graph graph, Node node) {
		Iterator<? extends Edge> selfEdgeIter = graph.edgeSet(node).iterator();
		while (selfEdgeIter.hasNext()) {
			Edge nextEdge = selfEdgeIter.next();
			if (isAttributeLabel(nextEdge.label())) {
				return new ValueNode();
			}
		}
		return null;
    }

    /**
	 * Returns the operation this label is encoding.
	 * 
	 * @param label
	 *            the label from which to determine the encoded operation
	 * @return the operation encoded by the given label, or <tt>null</tt> if
	 *         the label is not encoding an algebraic operation
	 */
    public static Operation toOperation(Label label) throws UnknownSymbolException {
//    	try {
    	Operation result = null;
    	int type = labelType(label);
    	if (type != NO_TYPE) {
    		Algebra algebra = getAlgebraGraph().getAlgebra(type);
    		result = algebra.getOperation(AlgebraConstants.labelText(label));
    	}
    	return result;
//    	} catch (UnknownSymbolException use) {
//    		use.printStackTrace();
//    	}
//		return null;
    }
}
