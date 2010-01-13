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
import groove.algebra.Operation;
import groove.algebra.Operator;
import groove.algebra.UnknownSymbolException;
import groove.graph.Element;
import groove.graph.algebra.ArgumentEdge;
import groove.graph.algebra.OperatorEdge;
import groove.graph.algebra.ProductNode;
import groove.graph.algebra.ValueNode;
import groove.util.Groove;
import groove.view.FormatException;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

    /**
     * Checks if the context of an attribute-valued aspect node in an aspect
     * graph is correct, in the following sense. For a {@link #PRODUCT} node,
     * correctness means all incident edges are outgoing, and the
     * {@link #ARGUMENT} edges are numbered in the range <code>0..arity</code>,
     * where <code>arity</code> is the number of arguments expected by the
     * outgoing operation edges. For a {@link #VALUE} node, correctness means
     * all incident edges are incoming, and of equal type if they are operation
     * edges.
     */
    @Override
    public void checkNode(AspectNode node, AspectGraph graph)
        throws FormatException {
        AspectValue value = getAttributeValue(node);
        Set<AspectEdge> edges = graph.edgeSet(node);
        if (PRODUCT.equals(value)) {
            int arity = 0;
            BitSet arguments = new BitSet();
            for (AspectEdge edge : edges) {
                if (!edge.source().equals(node)) {
                    throw new FormatException(
                        "Product node '%s' has incoming edge '%s'", node, edge);
                }
                AspectValue edgeValue = getAttributeValue(edge);
                if (edgeValue == null && !NestingAspect.isMetaElement(edge)) {
                    throw new FormatException(
                        "Product node '%s' has non-attribute edge '%s'", node,
                        edge);
                } else if (ARGUMENT.equals(edgeValue)) {
                    // label is know to represent a natural number
                    int nr = Integer.parseInt(edge.label().text());
                    arity = Math.max(arity, nr + 1);
                    if (arguments.get(nr)) {
                        throw new FormatException(
                            "Duplicate argument edge index '%d'", nr);
                    }
                    arguments.set(nr);
                }
            }
            if (arguments.cardinality() != arity) {
                throw new FormatException(
                    "Argument edge indices %s do not constitute valid range",
                    arguments);
            }
            for (AspectEdge edge : edges) {
                Operator operation = getOperation(edge);
                if (operation != null && operation.getArity() != arity) {
                    throw new FormatException(
                        "Operation '%s' is incompatible with product node arity %d",
                        operation, arity);
                }
            }
        } else {
            // the value is VALUE; try to establish the algebra
            String type = VALUE.equals(value) ? null : value.getName();
            for (AspectEdge edge : edges) {
                if (!NestingAspect.isMetaElement(edge)) {
                    // we don't check for outgoing non-attribute edges
                    // since these may be injectivity constraints
                    // real outgoing edges can never be matched, but that's a
                    // type error

                    // if edge edge represents a constant or operator,
                    // establish its (result) type
                    String edgeType = null;
                    if (isConstant(edge)) {
                        edgeType = getAttributeValue(edge).getName();
                    } else {
                        Operator operation = getOperation(edge);
                        if (operation != null) {
                            edgeType = operation.getResultType();
                        }
                    }
                    if (edgeType != null) {
                        if (type == null) {
                            type = edgeType;
                        } else if (!type.equals(edgeType)) {
                            throw new FormatException(
                                "Incompatible types '%s' and '%s' for value node %s",
                                type, edgeType, node);
                        }
                    } else if (edge.source().equals(node)) {
                        throw new FormatException(
                            "Non-algebra label %s on value node %s",
                            edge.label(), node);
                    }
                }
            }
        }
    }

    /**
     * This implementation considers any of the signature values to be more
     * demanding than {@link #VALUE}.
     */
    @Override
    protected AspectValue getMaxValue(AspectValue value1, AspectValue value2)
        throws FormatException {
        if (VALUE.equals(value1) && algebraMap.containsKey(value2)) {
            return value2;
        } else if (VALUE.equals(value2) && algebraMap.containsKey(value1)) {
            return value1;
        } else {
            return super.getMaxValue(value1, value2);
        }
    }

    /**
     * Returns the singleton instance of this aspect.
     */
    public static AttributeAspect getInstance() {
        return instance;
    }

    /**
     * Returns the attribute aspect value associated with a given aspect
     * element. Convenience method for {@link AspectElement#getValue(Aspect)}
     * with {@link #getInstance()} as parameter.
     */
    public static AspectValue getAttributeValue(AspectElement elem) {
        return elem.getValue(getInstance());
    }

    /**
     * Tests if a given aspect value corresponds to a data type; i.e., if it is
     * not equal to {@link #ARGUMENT}, {@link #VALUE} or {@link #PRODUCT}
     */
    static public boolean isDataValue(AspectValue value) {
        return algebraMap.containsKey(value);
    }

    /**
     * Tests if a given aspect element carries an {@link AttributeAspect} value
     * that corresponds to a data type.
     * @see #isDataValue(AspectValue)
     */
    static public boolean isDataElement(AspectElement elem) {
        return isDataValue(getAttributeValue(elem));
    }

    /**
     * Tests if a given aspect element corresponds to a product node.
     */
    static public boolean isAttributeNode(AspectElement elem) {
        return elem instanceof AspectNode && getAttributeValue(elem) != null;
    }

    /**
     * Returns the aspect value corresponding to a given signature.
     */
    public static AspectValue getAttributeValueFor(Algebra<?> algebra) {
        return aspectValueMap.get(AlgebraRegister.getSignatureName(algebra));
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
            Operation operation = ((OperatorEdge) elem).getOperation();
            return getAttributeValueFor(operation.getAlgebra());
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
        value.setLabelParser(new OperationLabelParser(algebra));
    }

    /**
     * Tests if an edge encodes an algebra constant.
     */
    public static boolean isConstant(AspectEdge edge) {
        boolean result = false;
        AspectValue edgeValue = getAttributeValue(edge);
        if (edgeValue != null && !ARGUMENT.equals(edgeValue)) {
            OperationLabelParser parser =
                (OperationLabelParser) edgeValue.getLabelParser();
            // assert parser != null : String.format(
            // "Can't find parser for edge '%s'", edge.label());
            result = parser != null && parser.isConstant(edge.label().text());
        }
        return result;
    }

    /**
     * Extracts an algebra operation from an aspect edge. Returns
     * <code>null</code> if the edge is not a (valid) operation edge.
     */
    public static Operator getOperation(AspectEdge edge) {
        Operator result = null;
        AspectValue edgeValue = getAttributeValue(edge);
        if (edgeValue != null && !ARGUMENT.equals(edgeValue)) {
            OperationLabelParser parser =
                (OperationLabelParser) edgeValue.getLabelParser();
            try {
                result = parser.getOperation(edge.label().text());
            } catch (FormatException exc) {
                // no valid operation
            }
        }
        return result;
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
            ARGUMENT.setLabelParser(new NumberLabelParser());
            VALUE = instance.addNodeValue(VALUE_NAME);
            PRODUCT = instance.addNodeValue(PRODUCT_NAME);
            for (String signatureName : AlgebraRegister.getSignatureNames()) {
                AspectValue value = instance.addValue(signatureName);
                value.setEdgeToTarget(VALUE);
                addSignature(signatureName, value);
            }
            ARGUMENT.setEdgeToSource(PRODUCT);
            ARGUMENT.setEdgeToTarget(VALUE);
            // incompatibilities
            instance.setIncompatible(RuleAspect.CREATOR);
            instance.setIncompatible(RuleAspect.CNEW);
            instance.setIncompatible(RuleAspect.ERASER);
            instance.setIncompatible(NestingAspect.getInstance());
        } catch (FormatException exc) {
            throw new Error("Aspect '" + ATTRIBUTE_ASPECT_NAME
                + "' cannot be initialised due to name conflict", exc);
        }
    }

    /**
     * Parser that turns a string into a default label, after testing the string
     * for correct formatting using a callback method that can be overridden by
     * subclasses.
     */
    private static class NumberLabelParser extends FreeLabelParser {
        /** Empty constructor for the singleton instance. */
        NumberLabelParser() {
            // Empty
        }

        @Override
        protected String getExceptionText(String text) {
            try {
                Integer.parseInt(text);
                // if this succeeds, the problem was a negative number
                return String.format("String '%s' is a negative number", text);
            } catch (NumberFormatException exc) {
                return String.format(
                    "String '%s' cannot be parsed as a number", text);
            }
        }

        @Override
        protected boolean isCorrect(String text) {
            try {
                return Integer.parseInt(text) >= 0;
            } catch (NumberFormatException exc) {
                return false;
            }
        }
    }

    /**
     * Class that attempts to parse a string as the operation of a given
     * algebra, and returns the result as a DefaultLabel if successful.
     */
    private static class OperationLabelParser extends FreeLabelParser {
        /** Constructs an instance of this parser class for a given algebra. */
        OperationLabelParser(String signature) {
            this.signature = signature;
        }

        /**
         * This implementation tests if the text corresponds to an operation of
         * the associated algebra.
         */
        @Override
        protected boolean isCorrect(String text) {
            try {
                if (isConstant(text)) {
                    return true;
                } else {
                    getOperation(text);
                    return true;
                }
            } catch (FormatException e) {
                return false;
            }
        }

        /**
         * This implementation tests if the text corresponds to an operation of
         * the associated algebra.
         */
        @Override
        protected String getExceptionText(String text) {
            try {
                getOperation(text);
                return "";
            } catch (FormatException exc) {
                return exc.getMessage();
            }
        }

        /**
         * Tests if a certain string is a constant of the signature wrapped in
         * this parser.
         */
        public boolean isConstant(String text) {
            try {
                return AlgebraRegister.isConstant(this.signature, text);
            } catch (UnknownSymbolException e) {
                return false;
            }
        }

        /**
         * Extracts an operation of this algebra from a given string, if the
         * string indeed represents such an operation.
         * @throws FormatException if <code>text</code> does not represent an
         *         operation of this algebra.
         */
        public Operator getOperation(String text) throws FormatException {
            try {
                return AlgebraRegister.getOperator(this.signature, text);
            } catch (UnknownSymbolException exc) {
                throw new FormatException(exc.getMessage());
            }
        }

        /** The algebra that should understand the operation. */
        private final String signature;
    }
}
