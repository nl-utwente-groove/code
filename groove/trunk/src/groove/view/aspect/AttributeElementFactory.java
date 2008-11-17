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
package groove.view.aspect;

import static groove.view.aspect.AttributeAspect.ARGUMENT;
import static groove.view.aspect.AttributeAspect.PRODUCT;
import static groove.view.aspect.AttributeAspect.VALUE;
import static groove.view.aspect.AttributeAspect.getAttributeValue;
import groove.algebra.AlgebraRegister;
import groove.algebra.Operation;
import groove.algebra.UnknownSymbolException;
import groove.graph.Edge;
import groove.graph.Node;
import groove.graph.algebra.ArgumentEdge;
import groove.graph.algebra.OperatorEdge;
import groove.graph.algebra.ProductNode;
import groove.graph.algebra.ValueNode;
import groove.trans.SystemProperties;
import groove.view.FormatException;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Class taking care of the creation of attribute nodes and attribute edges
 * in the translation from aspectual rule and graph views to actual rules and graphs.
 * @author Arend Rensink
 * @version $Revision $
 */
public class AttributeElementFactory {
    /** Constructs a factory for a given aspect graph. 
     * @param properties TODO*/
    public AttributeElementFactory(AspectGraph graph, SystemProperties properties) {
        this.graph = graph;
        String registerName = properties == null ? AlgebraRegister.DEFAULT_ALGEBRAS : properties.getAlgebraFamily();
        this.register = AlgebraRegister.getInstance(registerName);
    }
    /**
     * Creates an attribute-related node from a given {@link AspectNode} found
     * in a given {@link AspectGraph}. The type of the resulting node depends
     * on the {@link AttributeAspect} value of the given node and its incident
     * edges. The result is a {@link ValueNode} or {@link ProductNode}, or
     * <code>null</code> if the node contains no special
     * {@link AttributeAspect} value. An exception is thrown if the context of
     * the node in the graph is incorrect.
     * @param node the node for which we want an attribute-related node
     * @return a {@link ValueNode} or {@link ProductNode} corresponding to
     *         <code>node</code>, or <code>null</code>
     * @throws FormatException if attribute-related errors are found in
     *         <code>graph</code>
     */
    public Node createAttributeNode(AspectNode node)
        throws FormatException {
        Node result;
        AspectValue attributeValue = getAttributeValue(node);
        if (attributeValue == null) {
            result = null;
        } else if (attributeValue == VALUE) {
            result = createVariableNode(node);
        } else {
            assert attributeValue == PRODUCT : String.format(
                "Illegal attribute aspect value: %s", attributeValue);
            result = createProductNode(node);
        }
        return result;
    }

    /**
     * Creates a {@link ValueNode} corresponding to a given aspect node whose
     * {@link AttributeAspect} value equals {@link #VALUE}. This is either a
     * variable node (if the original node has no outgoing edges), or a constant
     * node whose value depends on the label of the node's self-edge.
     * @param node the node for which a {@link ValueNode} is to be created
     * @throws FormatException if the outgoing edges of <code>node</code> are
     *         incorrect
     */
    private ValueNode createVariableNode(AspectNode node)
        throws FormatException {
        ValueNode result;
        // check if there is a single constant edge on this node
        Collection<AspectEdge> outEdges = this.graph.outEdgeSet(node);
        Set<AspectEdge> attributeEdges = new HashSet<AspectEdge>();
        for (AspectEdge outEdge : outEdges) {
            if (getAttributeValue(outEdge) != null) {
                attributeEdges.add(outEdge);
            }
        }
        if (attributeEdges.isEmpty()) {
            result = ValueNode.createVariableNode();
        } else if (attributeEdges.size() > 1) {
            throw new FormatException("Too many edges on constant node: %s",
                attributeEdges);
        } else {
            AspectEdge attributeEdge = attributeEdges.iterator().next();
            AspectValue algebraValue = getAttributeValue(attributeEdge);
            if (algebraValue == null) {
                throw new FormatException(
                    "Label %s on value node should be a constant",
                    attributeEdge.getLabelText());
            }
            try {
                String signature = algebraValue.getName();
                Object nodeValue =
                    this.register.getConstant(
                        signature, attributeEdge.label().text());
                result =
                    ValueNode.createValueNode(
                        this.register.getImplementation(
                            signature), nodeValue);
            } catch (UnknownSymbolException exc) {
                throw new FormatException(exc.getMessage());
            }
        }
        return result;
    }

    /**
     * Creates a product node corresponding to a node with
     * {@link AttributeAspect} value {@link #PRODUCT}. This only succeeds if
     * the outgoing edges form a consecutive range of argument numbers, without
     * duplication.
     * @param node the node for which a {@link ProductNode} is to be created
     * @throws FormatException if the outgoing edges of <code>node</code> are
     *         incorrect
     */
    private ProductNode createProductNode(AspectNode node) throws FormatException {
        return new ProductNode(arity(node));
    }

    /**
     * Returns the number of outgoing argument edges. Argument edges are aspect
     * edges with {@link AttributeAspect} value {@link #ARGUMENT}. Also checks
     * that the argument edges actually form a consecutive sequence ranging from
     * 0 to the arity.
     */
    private int arity(AspectNode node)
        throws FormatException {
        Set<Integer> argNumbers = new HashSet<Integer>();
        int maxArgNumber = -1;
        int result = 0;
        for (AspectEdge outEdge : this.graph.outEdgeSet(node)) {
            if (getAttributeValue(outEdge) == ARGUMENT) {
                try {
                    int argNumber = Integer.parseInt(outEdge.label().text());
                    if (!argNumbers.add(argNumber)) {
                        throw new FormatException("Duplicate argument edge %d",
                            argNumber);
                    }
                    maxArgNumber = Math.max(maxArgNumber, argNumber);
                    result++;
                } catch (NumberFormatException exc) {
                    throw new FormatException(
                        "Argument edge label %s is not a valid argument number",
                        outEdge.label());
                }
            }
        }
        if (result != maxArgNumber + 1) {
            throw new FormatException(
                "Argument numbers %s do not form a consecutive range",
                argNumbers);
        }
        return result;
    }

    /**
     * Creates an attribute-related edge from a given {@link AspectEdge} found
     * in a given {@link AspectGraph}, between given end nodes. The type of the
     * resulting edge depends on the {@link AttributeAspect} value of the given
     * edge. The result is a {@link OperatorEdge} or {@link ArgumentEdge}, or
     * <code>null</code> if the edge contains no special
     * {@link AttributeAspect} value.
     * @param edge the edge for which we want an attribute-related edge
     * @param ends the end nodes for the new edge
     * @return a {@link OperatorEdge} or {@link ArgumentEdge} corresponding to
     *         <code>edge</code>, or <code>null</code>
     * @throws FormatException if attribute-related errors are found in
     *         <code>graph</code>
     */
    public Edge createAttributeEdge(AspectEdge edge, Node[] ends)
        throws FormatException {
        Edge result;
        AspectValue attributeValue = getAttributeValue(edge);
        if (attributeValue == null || ends[Edge.SOURCE_INDEX] == ends[Edge.TARGET_INDEX]) {
            result = null;
        } else if (attributeValue == ARGUMENT) {
            int argNumber = Integer.parseInt(edge.label().text());
            ArgumentEdge argEdge = createArgumentEdge(argNumber, ends);
            result = argEdge;
        } else {
            try {
                Operation operation =
                    this.register.getOperation(
                        attributeValue.getName(), edge.label().text());
                result = createOperatorEdge(operation, ends);
            } catch (UnknownSymbolException e) {
                throw new FormatException(e.getMessage());
            }
        }
        return result;
    }

    /**
     * Creates and returns a fresh {@link OperatorEdge} derived from a given
     * aspect edge (which should have attribute value {@link #PRODUCT}).
     * @param operator the edge for which the image is to be created
     * @param ends the end nodes of the edge to be created
     * @return a fresh {@link OperatorEdge}
     * @throws FormatException if <code>edge</code> does not have a correct
     *         set of outgoing attribute edges in <code>graph</code>
     */
    private Edge createOperatorEdge(Operation operator, Node[] ends)
        throws FormatException {
        assert operator != null : String.format(
            "Cannot create edge between nodes %s for empty operator",
            Arrays.toString(ends));
        Node source = ends[Edge.SOURCE_INDEX];
        Node target = ends[Edge.TARGET_INDEX];
        if (!(source instanceof ProductNode)) {
            throw new FormatException(
                "Source of '%s'-edge should be a product node", operator);
        } else if (operator.getArity() != ((ProductNode) source).arity()) {
            throw new FormatException("Source arity of '%s'-edge should be %d",
                operator, operator.getArity());
        }
        if (!(target instanceof ValueNode)) {
            throw new FormatException(
                "Target of '%s'-edge should be a variable node", operator);
        }
        return new OperatorEdge((ProductNode) source, (ValueNode) target,
            operator);
    }

    /**
     * Returns an {@link ArgumentEdge} derived from a given aspect edge (which
     * should have attribute aspect value {@link #ARGUMENT}).
     * @param argNumber the argument number on the edge to be created
     * @param ends the end nodes of the edge to be created
     * @return a fresh {@link ArgumentEdge}
     * @throws FormatException if one of the ends is <code>null</code>
     */
    private ArgumentEdge createArgumentEdge(int argNumber, Node[] ends)
        throws FormatException {
        Node source = ends[Edge.SOURCE_INDEX];
        if (source == null) {
            throw new FormatException("Source of '%d'-edge has no image",
                argNumber);
        } else if (!(source instanceof ProductNode)) {
            throw new FormatException(
                "Target of '%d'-edge should be product node", argNumber);
        }
        Node target = ends[Edge.TARGET_INDEX];
        if (target == null) {
            throw new FormatException("Target of '%d'-edge has no image",
                argNumber);
        } else if (!(target instanceof ValueNode)) {
            throw new FormatException(
                "Target of '%d'-edge should be a variable node", argNumber);
        }
        ArgumentEdge result =
            new ArgumentEdge((ProductNode) source, argNumber, (ValueNode) target);
        result.source().setArgument(argNumber, result.target());
        return result;
    }
    
    /** Aspect graph on which this factory works. */
    private final AspectGraph graph;
    /** Algebra registry to use in creating the algebra values and operations. */
    private final AlgebraRegister register;
}
