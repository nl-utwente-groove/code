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
 * $Id: AttributeAspect.java,v 1.1.1.2 2007-03-20 10:42:43 kastenberg Exp $
 */
package groove.graph.aspects;

import groove.graph.GraphFormatException;
import groove.util.Groove;

/**
 * Graph aspect dealing with primitive data types (attributes).
 * Relevant information is: the type, and the role of the element.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $
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
		    }
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
    
    /** Private constructor to create the singleton instance. */
    private AttributeAspect() {
        super(ATTRIBUTE_ASPECT_NAME);
    }
}
