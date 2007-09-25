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
 * $Id: AlgebraConstants.java,v 1.8 2007-09-25 22:57:52 rensink Exp $
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
 * @version $Revision: 1.8 $ $Date: 2007-09-25 22:57:52 $
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
