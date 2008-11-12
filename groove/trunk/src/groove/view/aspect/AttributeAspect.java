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

import groove.algebra.AbstractIntegerAlgebra;
import groove.algebra.Algebra;
import groove.algebra.DefaultBooleanAlgebra;
import groove.algebra.DefaultIntegerAlgebra;
import groove.algebra.DefaultRealAlgebra;
import groove.algebra.DefaultStringAlgebra;
import groove.algebra.Operation;
import groove.algebra.UnknownSymbolException;
import groove.graph.Element;
import groove.graph.algebra.ArgumentEdge;
import groove.graph.algebra.OperatorEdge;
import groove.graph.algebra.ProductNode;
import groove.graph.algebra.ValueNode;
import groove.util.Groove;
import groove.view.FormatException;
import groove.view.FreeLabelParser;
import groove.view.NumberLabelParser;

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
     * graph is correct, in the following sense. For a {@link #PRODUCT} node, correctness
     * means all incident edges are outgoing, and the {@link #ARGUMENT} edges
     * are numbered in the range <code>0..arity</code>, where
     * <code>arity</code> is the number of arguments expected by the outgoing
     * operation edges. For a {@link #VALUE} node, correctness means all
     * incident edges are incoming, and of equal type if they are operation
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
                    "Argument edge indices %s do not constiture valid range",
                    arguments);
            }
            for (AspectEdge edge : edges) {
                Operation operation = getOperation(edge);
                if (operation != null && operation.arity() != arity) {
                    throw new FormatException(
                        "Operation '%s' is incompatible with product node arity %d",
                        operation, arity);
                }
            }
        } else {
            // the value is VALUE; try to establish the algebra
            Algebra type = null;
            for (AspectEdge edge : edges) {
                if (!NestingAspect.isMetaElement(edge)) {
                    // we don't check for outgoing non-algebra edges
                    // since these may be injectivity constraints
                    // real outgoing edges can never be matched, but that's a
                    // type error
                    // if (!edge.target().equals(node)) {
                    // throw new FormatException("Outgoing %s-labelled edge on
                    // value node %s",
                    // edge, node);
                    // }
                    Operation operation = getOperation(edge);
                    if (operation != null) {
                        Algebra edgeType = operation.getResultType();
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
     * Tests if a given aspect element carries an {@link AttributeAspect} value
     * that corresponds to a type.
     */
    static public boolean isTypeElement(AspectElement elem) {
        return algebraMap.containsKey(elem);
    }
//
//    /**
//     * Creates an attribute-related node from a given {@link AspectNode} found
//     * in a given {@link AspectGraph}. The type of the resulting node depends
//     * on the {@link AttributeAspect} value of the given node and its incident
//     * edges. The result is a {@link VariableNode} or {@link ProductNode}, or
//     * <code>null</code> if the node contains no special
//     * {@link AttributeAspect} value. An exception is thrown if the context of
//     * the node in the graph is incorrect.
//     * @param node the node for which we want an attribute-related node
//     * @param graph the graph containing <code>node</code>
//     * @return a {@link VariableNode} or {@link ProductNode} corresponding to
//     *         <code>node</code>, or <code>null</code>
//     * @throws FormatException if attribute-related errors are found in
//     *         <code>graph</code>
//     */
//    public static Node createAttributeNode(AspectNode node, AspectGraph graph)
//        throws FormatException {
//        Node result;
//        AspectValue attributeValue = getAttributeValue(node);
//        if (attributeValue == null) {
//            result = null;
//        } else if (attributeValue == VALUE) {
//            result = createVariableNode(node, graph);
//        } else {
//            assert attributeValue == PRODUCT : String.format(
//                "Illegal attribute aspect value: %s", attributeValue);
//            result = createProductNode(node, graph);
//        }
//        return result;
//    }
//
//    /**
//     * Creates a {@link VariableNode} corresponding to a given aspect node whose
//     * {@link AttributeAspect} value equals {@link #VALUE}. This is either a
//     * variable node (if the original node has no outgoing edges), or a constant
//     * node whose value depends on the label of the node's self-edge.
//     * @param node the node for which a {@link VariableNode} is to be created
//     * @param graph the graph in which <code>node</code> occurs
//     * @throws FormatException if the outgoing edges of <code>node</code> are
//     *         incorrect
//     */
//    private static VariableNode createVariableNode(AspectNode node, AspectGraph graph)
//        throws FormatException {
//        VariableNode result;
//        // check if there is a single constant edge on this node
//        Collection<AspectEdge> outEdges = graph.outEdgeSet(node);
//        Set<AspectEdge> attributeEdges = new HashSet<AspectEdge>();
//        for (AspectEdge outEdge : outEdges) {
//            if (getAttributeValue(outEdge) != null) {
//                attributeEdges.add(outEdge);
//            }
//        }
//        if (attributeEdges.isEmpty()) {
//            result = new VariableNode();
//        } else if (attributeEdges.size() > 1) {
//            throw new FormatException("Too many edges on constant node: %s",
//                attributeEdges);
//        } else {
//            AspectEdge attributeEdge = attributeEdges.iterator().next();
//            AspectValue algebraValue = getAttributeValue(attributeEdge);
//            if (algebraValue == null) {
//                throw new FormatException(
//                    "Label %s on value node should be a constant",
//                    attributeEdge.getLabelText());
//            }
//            Algebra algebra = algebraMap.get(algebraValue);
//            if (algebra == null) {
//                throw new FormatException(
//                    "Label %s on value node should be a constant",
//                    attributeEdge.getLabelText());
//            }
//            try {
//                Operation nodeValue =
//                    algebra.getOperation(attributeEdge.label().text());
//                if (!(nodeValue instanceof Constant)) {
//                    throw new FormatException(
//                        "Operation %s on value node should be a constant",
//                        attributeEdge.label());
//                }
//                result = new VariableNode((Constant) nodeValue);
//            } catch (UnknownSymbolException exc) {
//                throw new FormatException(exc.getMessage());
//            }
//        }
//        return result;
//    }
//
//    /**
//     * Creates a product node corresponding to a node with
//     * {@link AttributeAspect} value {@link #PRODUCT}. This only succeeds if
//     * the outgoing edges form a consecutive range of argument numbers, without
//     * duplication.
//     * @param node the node for which a {@link ProductNode} is to be created
//     * @param graph the graph in which <code>node</code> occurs
//     * @throws FormatException if the outgoing edges of <code>node</code> are
//     *         incorrect
//     */
//    private static ProductNode createProductNode(AspectNode node,
//            AspectGraph graph) throws FormatException {
//        return new ProductNode(arity(node, graph));
//    }
//
//    /**
//     * Returns the number of outgoing argument edges. Argument edges are aspect
//     * edges with {@link AttributeAspect} value {@link #ARGUMENT}. Also checks
//     * that the argument edges actually form a consecutive sequence ranging from
//     * 0 to the arity.
//     */
//    private static int arity(AspectNode node, AspectGraph graph)
//        throws FormatException {
//        Set<Integer> argNumbers = new HashSet<Integer>();
//        int maxArgNumber = -1;
//        int result = 0;
//        for (AspectEdge outEdge : graph.outEdgeSet(node)) {
//            if (getAttributeValue(outEdge) == ARGUMENT) {
//                try {
//                    int argNumber = Integer.parseInt(outEdge.label().text());
//                    if (!argNumbers.add(argNumber)) {
//                        throw new FormatException("Duplicate argument edge %d",
//                            argNumber);
//                    }
//                    maxArgNumber = Math.max(maxArgNumber, argNumber);
//                    result++;
//                } catch (NumberFormatException exc) {
//                    throw new FormatException(
//                        "Argument edge label %s is not a valid argument number",
//                        outEdge.label());
//                }
//            }
//        }
//        if (result != maxArgNumber + 1) {
//            throw new FormatException(
//                "Argument numbers %s do not form a consecutive range",
//                argNumbers);
//        }
//        return result;
//    }
//
//    /**
//     * Creates an attribute-related edge from a given {@link AspectEdge} found
//     * in a given {@link AspectGraph}, between given end nodes. The type of the
//     * resulting edge depends on the {@link AttributeAspect} value of the given
//     * edge. The result is a {@link OperatorEdge} or {@link ArgumentEdge}, or
//     * <code>null</code> if the edge contains no special
//     * {@link AttributeAspect} value. The edge is assumed to ave passed
//     * {@link #checkEdge(AspectEdge, AspectGraph)}.
//     * @param edge the edge for which we want an attribute-related edge
//     * @param ends the end nodes for the new edge
//     * @return a {@link OperatorEdge} or {@link ArgumentEdge} corresponding to
//     *         <code>edge</code>, or <code>null</code>
//     * @throws FormatException if attribute-related errors are found in
//     *         <code>graph</code>
//     */
//    public static Edge createAttributeEdge(AspectEdge edge, Node[] ends)
//        throws FormatException {
//        Edge result;
//        AspectValue attributeValue = getAttributeValue(edge);
//        if (attributeValue == null || ends[Edge.SOURCE_INDEX] == ends[Edge.TARGET_INDEX]) {
//            result = null;
//        } else if (attributeValue == ARGUMENT) {
//            int argNumber = Integer.parseInt(edge.label().text());
//            ArgumentEdge argEdge = createArgumentEdge(argNumber, ends);
//            result = argEdge;
//        } else {
//            assert algebraMap.containsKey(attributeValue);
//            Operation operation = getOperation(edge);
//            if (operation == null) {
//                throw new FormatException("Unknown operator in edge label %s",
//                    edge.getLabelText());
//            }
//            result = createOperatorEdge(operation, ends);
//        }
//        return result;
//    }
//
//    /**
//     * Creates and returns a fresh {@link OperatorEdge} derived from a given
//     * aspect edge (which should have attribute value {@link #PRODUCT}).
//     * @param operator the edge for which the image is to be created
//     * @param ends the end nodes of the edge to be created
//     * @return a fresh {@link OperatorEdge}
//     * @throws FormatException if <code>edge</code> does not have a correct
//     *         set of outgoing attribute edges in <code>graph</code>
//     */
//    private static Edge createOperatorEdge(Operation operator, Node[] ends)
//        throws FormatException {
//        assert operator != null : String.format(
//            "Cannot create edge between nodes %s for empty operator",
//            Arrays.toString(ends));
//        Node source = ends[Edge.SOURCE_INDEX];
//        Node target = ends[Edge.TARGET_INDEX];
//        if (!(source instanceof ProductNode)) {
//            throw new FormatException(
//                "Source of '%s'-edge should be a product node", operator);
//        } else if (operator.arity() != ((ProductNode) source).arity()) {
//            throw new FormatException("Source arity of '%s'-edge should be %d",
//                operator, operator.arity());
//        }
//        if (!(target instanceof VariableNode)) {
//            throw new FormatException(
//                "Target of '%s'-edge should be a variable node", operator);
//        }
//        return new OperatorEdge((ProductNode) source, (VariableNode) target,
//            operator);
//    }
//
//    /**
//     * Returns an {@link ArgumentEdge} derived from a given aspect edge (which
//     * should have attribute aspect value {@link #ARGUMENT}).
//     * @param argNumber the argument number on the edge to be created
//     * @param ends the end nodes of the edge to be created
//     * @return a fresh {@link ArgumentEdge}
//     * @throws FormatException if one of the ends is <code>null</code>
//     */
//    private static ArgumentEdge createArgumentEdge(int argNumber, Node[] ends)
//        throws FormatException {
//        Node source = ends[Edge.SOURCE_INDEX];
//        if (source == null) {
//            throw new FormatException("Source of '%d'-edge has no image",
//                argNumber);
//        } else if (!(source instanceof ProductNode)) {
//            throw new FormatException(
//                "Target of '%d'-edge should be product node", argNumber);
//        }
//        Node target = ends[Edge.TARGET_INDEX];
//        if (target == null) {
//            throw new FormatException("Target of '%d'-edge has no image",
//                argNumber);
//        } else if (!(target instanceof VariableNode)) {
//            throw new FormatException(
//                "Target of '%d'-edge should be a variable node", argNumber);
//        }
//        ArgumentEdge result =
//            new ArgumentEdge((ProductNode) source, argNumber, (VariableNode) target);
//        result.source().setArgument(argNumber, result.target());
//        return result;
//    }

    /**
     * Returns the aspect value corresponding to a given signature.
     */
    public static AspectValue getAttributeValueFor(Algebra algebra) {
        return aspectValueMap.get(algebra);
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
            return getAttributeValueFor(operation.algebra());
        } else {
            return null;
        }
    }

    /** Returns the algebra associated with a given aspect value. */
    static public Algebra getAlgebra(AspectValue aspectValue) {
        return algebraMap.get(aspectValue);
    }

    /**
     * Adds a pair of algebra and aspect value to the internal maps
     * {@link #algebraMap} and {@link #aspectValueMap}.
     */
    private static void addAlgebra(Algebra algebra, AspectValue value) {
        Algebra oldAlgebra = algebraMap.put(value, algebra);
        AspectValue oldValue = aspectValueMap.put(algebra, value);
        if (oldAlgebra != null || oldValue != null) {
            throw new IllegalStateException(String.format(
                "Duplicate algebra %s", algebra));
        }
        value.setLabelParser(new OperationLabelParser(algebra));
    }

    /**
     * Extracts an algebra operation from an aspect edge. Returns
     * <code>null</code> if the edge is not a (valid) operation edge.
     */
    public static Operation getOperation(AspectEdge edge) {
        Operation result = null;
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
    /** Name of the integer aspect value. */
    public static final String INTEGER_NAME =
        Groove.getXMLProperty("label.integer.prefix");
    /** The integer aspect value. */
    public static final AspectValue INTEGER;
    /** Name of the real aspect value. */
    public static final String REAL_NAME =
        Groove.getXMLProperty("label.real.prefix");
    /** The real aspect value. */
    public static final AspectValue REAL;
    /** Name of the integer aspect value. */
    public static final String ABSTRACT_INTEGER_NAME =
        Groove.getXMLProperty("label.abstract.integer.prefix");
    /** The integer aspect value. */
    public static final AspectValue ABSTRACT_INTEGER;
    /** Name of the boolean aspect value. */
    public static final String BOOLEAN_NAME =
        Groove.getXMLProperty("label.boolean.prefix");
    /** The boolean aspect value. */
    public static final AspectValue BOOLEAN;
    /** Name of the string aspect value. */
    public static final String STRING_NAME =
        Groove.getXMLProperty("label.string.prefix");
    /** The string aspect value. */
    public static final AspectValue STRING;

    /**
     * Map from aspect values to those algebras that they represent.
     */
    private static final Map<AspectValue,Algebra> algebraMap =
        new HashMap<AspectValue,Algebra>();
    /**
     * Map from algebras to aspect values that represent them.
     */
    private static final Map<Algebra,AspectValue> aspectValueMap =
        new HashMap<Algebra,AspectValue>();

    static {
        try {
            ARGUMENT = instance.addEdgeValue(ARGUMENT_NAME);
            ARGUMENT.setLabelParser(NumberLabelParser.getInstance());
            VALUE = instance.addNodeValue(VALUE_NAME);
            PRODUCT = instance.addNodeValue(PRODUCT_NAME);
            INTEGER = instance.addEdgeValue(INTEGER_NAME);
            ABSTRACT_INTEGER = instance.addEdgeValue(ABSTRACT_INTEGER_NAME);
            REAL = instance.addEdgeValue(REAL_NAME);
            BOOLEAN = instance.addEdgeValue(BOOLEAN_NAME);
            STRING = instance.addEdgeValue(STRING_NAME);
            ARGUMENT.setEdgeToSource(PRODUCT);
            ARGUMENT.setEdgeToTarget(VALUE);
            INTEGER.setEdgeToTarget(VALUE);
            ABSTRACT_INTEGER.setEdgeToTarget(VALUE);
            REAL.setEdgeToTarget(VALUE);
            BOOLEAN.setEdgeToTarget(VALUE);
            STRING.setEdgeToTarget(VALUE);
            // incompatibilities
            instance.setIncompatible(RuleAspect.CREATOR);
            instance.setIncompatible(RuleAspect.ERASER);
            instance.setIncompatible(NestingAspect.getInstance());
            // initialise the algebra map
            addAlgebra(DefaultIntegerAlgebra.getInstance(), INTEGER);
            addAlgebra(DefaultRealAlgebra.getInstance(), REAL);
            addAlgebra(DefaultBooleanAlgebra.getInstance(), BOOLEAN);
            addAlgebra(DefaultStringAlgebra.getInstance(), STRING);
            addAlgebra(AbstractIntegerAlgebra.getInstance(), ABSTRACT_INTEGER);
        } catch (FormatException exc) {
            throw new Error("Aspect '" + ATTRIBUTE_ASPECT_NAME
                + "' cannot be initialised due to name conflict", exc);
        }
    }

    /**
     * Class that attempts to parse a string as the operation of a given
     * algebra, and returns the result as a DefaultLabel if successful.
     */
    private static class OperationLabelParser extends FreeLabelParser {
        /** Constructs an instance of this parser class for a given algebra. */
        OperationLabelParser(Algebra algebra) {
            this.algebra = algebra;
        }

        /**
         * This implementation tests if the text corresponds to an operation of
         * the associated algebra.
         */
        @Override
        protected boolean isCorrect(String text) {
            try {
                getOperation(text);
                return true;
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
         * Extracts an operation of this algebra from a given string, if the
         * string indeed represents such an operation.
         * @throws FormatException if <code>text</code> does not represent an
         *         operation of this algebra.
         */
        public Operation getOperation(String text) throws FormatException {
            try {
                return this.algebra.getOperation(text);
            } catch (UnknownSymbolException exc) {
                throw new FormatException(exc.getMessage());
            }
        }

        /** The algebra that should understand the operation. */
        private final Algebra algebra;
    }
}
