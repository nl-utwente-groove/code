/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: AttributeAspect.java,v 1.19 2008/01/21 11:28:05 rensink Exp $
 */
package groove.view.aspect;

import groove.algebra.Algebra;
import groove.algebra.AlgebraRegister;
import groove.algebra.Operator;
import groove.algebra.UnknownSymbolException;
import groove.graph.Element;
import groove.graph.algebra.ArgumentEdge;
import groove.graph.algebra.OperatorEdge;
import groove.graph.algebra.ProductNode;
import groove.graph.algebra.ValueNode;
import groove.util.Groove;
import groove.view.FormatException;

import java.util.HashMap;
import java.util.Map;

/**
 * Graph aspect dealing with primitive data types (attributes). Relevant
 * information is: the type, and the role of the element.
 * @author Arend Rensink
 * @version $Revision$
 */
public class AttributeAspect extends AbstractAspect {
    /** Private constructor to create the singleton instance. */
    private AttributeAspect() {
        super(ATTRIBUTE_ASPECT_NAME);
    }

    /** Creates a {@link ConstantAspectValue} for algebra aspects. */
    @Override
    protected AspectValue createValue(String name) throws FormatException {
        if (AlgebraRegister.getSignatureNames().contains(name)) {
            return new ConstantAspectValue(name);
        } else {
            return super.createValue(name);
        }
    }

    /**
     * Returns the singleton instance of this aspect.
     */
    public static AttributeAspect getInstance() {
        return instance;
    }

    /**
     * Tests if a given aspect value corresponds to a data type; i.e., if it is
     * not equal to {@link #ARGUMENT}, {@link #VALUE} or {@link #PRODUCT}
     */
    static public boolean isDataValue(AspectValue value) {
        return algebraMap.containsKey(value);
    }

    /**
     * Returns the aspect value corresponding to a given algebra.
     */
    public static AspectValue getAttributeValueFor(Algebra<?> algebra) {
        return getAttributeValueFor(AlgebraRegister.getSignatureName(algebra));
    }

    /**
     * Returns the aspect value corresponding to a given signature.
     */
    public static AspectValue getAttributeValueFor(String signature) {
        return aspectValueMap.get(signature);
    }

    /**
     * Returns the appropriate attribute aspect value associated with an
     * (ordinary) graph element, if any. The value depends on the type of the
     * element.
     * @param elem the graph element for which an attribute aspect value is
     *        required
     * @return the attribute aspect value for <code>elem</code>, such that
     *         <code>result.getAspect() == getInstance()</code>, or
     *         <code>null</code> if <code>elem</code> does not have any
     *         attribute information.
     */
    public static AspectValue getAttributeValueFor(Element elem) {
        if (elem instanceof ValueNode) {
            return VALUE;
        } else if (elem instanceof ProductNode) {
            return PRODUCT;
        } else if (elem instanceof ArgumentEdge) {
            return ARGUMENT;
        } else if (elem instanceof OperatorEdge) {
            Operator operation = ((OperatorEdge) elem).getOperator();
            return getAttributeValueFor(operation.getSignature());
        } else {
            return null;
        }
    }

    /** Returns the algebra associated with a given aspect value. */
    static public String getAlgebra(AspectValue aspectValue) {
        return algebraMap.get(aspectValue);
    }

    /**
     * Adds a pair of algebra and aspect value to the internal maps
     * {@link #algebraMap} and {@link #aspectValueMap}.
     */
    private static void addSignature(String algebra, AspectValue value) {
        String oldAlgebra = algebraMap.put(value, algebra);
        AspectValue oldValue = aspectValueMap.put(algebra, value);
        if (oldAlgebra != null || oldValue != null) {
            throw new IllegalStateException(String.format(
                "Duplicate algebra %s", algebra));
        }
        value.setLast(true);
    }

    /**
     * Tests if an edge encodes an algebra constant.
     */
    public static boolean isConstant(AspectEdge edge) {
        throw new UnsupportedOperationException();
    }

    /**
     * The name of the attribute aspect.
     */
    public static final String ATTRIBUTE_ASPECT_NAME = "attribute";

    /**
     * The singleton instance of this class.
     */
    private static final AttributeAspect instance = new AttributeAspect();

    /** Name of the argument aspect value. */
    public static final String ARGUMENT_NAME =
        Groove.getXMLProperty("label.argument.prefix");
    /** The argument aspect value. */
    public static final AspectValue ARGUMENT;
    /** Name of the attribute aspect value. */
    public static final String VALUE_NAME =
        Groove.getXMLProperty("label.attribute.prefix");
    /** The attribute aspect value. */
    public static final AspectValue VALUE;
    /** Name of the product aspect value. */
    public static final String PRODUCT_NAME =
        Groove.getXMLProperty("label.product.prefix");
    /** The product aspect value. */
    public static final AspectValue PRODUCT;

    /**
     * Map from aspect values to those algebras that they represent.
     */
    private static final Map<AspectValue,String> algebraMap =
        new HashMap<AspectValue,String>();
    /**
     * Map from algebras to aspect values that represent them.
     */
    private static final Map<String,AspectValue> aspectValueMap =
        new HashMap<String,AspectValue>();

    static {
        try {
            ARGUMENT = instance.addEdgeValue(ARGUMENT_NAME);
            ARGUMENT.setLast(true);
            VALUE = instance.addNodeValue(VALUE_NAME);
            PRODUCT = instance.addNodeValue(PRODUCT_NAME);
            for (String signatureName : AlgebraRegister.getSignatureNames()) {
                AspectValue value = instance.addValue(signatureName);
                addSignature(signatureName, value);
            }
        } catch (FormatException exc) {
            throw new Error("Aspect '" + ATTRIBUTE_ASPECT_NAME
                + "' cannot be initialised due to name conflict", exc);
        }
    }

    /**
     * Aspect value encoding a data constant
     * @author Arend Rensink
     * @version $Revision $
     */
    public class ConstantAspectValue extends AspectValue {
        /**
         * Constructs a new constant-containing aspect value.
         * @param name the aspect value name
         */
        public ConstantAspectValue(String name) throws FormatException {
            super(getInstance(), name, true);
        }

        /** Constructs a value wrapping a data constant. */
        private ConstantAspectValue(ConstantAspectValue original, String value)
            throws FormatException {
            super(original, value);
        }

        @Override
        public ConstantAspectValue newValue(String value)
            throws FormatException {
            try {
                if (AlgebraRegister.isConstant(getName(), value)) {
                    return new ConstantAspectValue(this, value);
                } else {
                    throw new FormatException(
                        "Signature '%s' has no constant %s", getName(), value);
                }
            } catch (UnknownSymbolException e) {
                assert false : String.format(
                    "Method called for unknown signature '%s'", getName());
                return null;
            }
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder(getName());
            result.append(Aspect.VALUE_SEPARATOR);
            if (getContent() != null && getContent().length() != 0) {
                result.append(this.getContent());
            }
            return result.toString();
        }
    }
}
