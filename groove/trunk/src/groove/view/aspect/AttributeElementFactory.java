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
 * $Id$
 */
package groove.view.aspect;

import static groove.view.aspect.AttributeAspect.ARGUMENT;
import static groove.view.aspect.AttributeAspect.PRODUCT;
import static groove.view.aspect.AttributeAspect.VALUE;
import static groove.view.aspect.AttributeAspect.getAttributeValue;
import groove.algebra.Algebra;
import groove.algebra.AlgebraRegister;
import groove.algebra.Operation;
import groove.algebra.UnknownSymbolException;
import groove.graph.algebra.ArgumentEdge;
import groove.graph.algebra.OperatorEdge;
import groove.graph.algebra.ProductNode;
import groove.graph.algebra.ValueNode;
import groove.graph.algebra.VariableNode;
import groove.trans.RuleEdge;
import groove.trans.RuleNode;
import groove.trans.SystemProperties;
import groove.view.FormatException;
import groove.view.aspect.AttributeAspect.ConstantAspectValue;

import java.util.HashSet;
import java.util.Set;

/**
 * Class taking care of the creation of attribute nodes and attribute edges in
 * the translation from aspectual rule and graph views to actual rules and
 * graphs.
 * @author Arend Rensink
 * @version $Revision $
 */
public class AttributeElementFactory {
    /**
     * Constructs a factory for a given aspect graph. System properties may be
     * passed in to determine the algebra family from which to take the values
     * and operations.
     * @param graph the aspect graph for which the attribute elements are to be
     *        created
     * @param properties source of the algebra family to be used; if
     *        <code>null</code>, the {@link AlgebraRegister#DEFAULT_ALGEBRAS} is
     *        used.
     * @see SystemProperties#getAlgebraFamily()
     */
    public AttributeElementFactory(AspectGraph graph,
            SystemProperties properties) {
        this.graph = graph;
        String registerName =
            properties == null ? AlgebraRegister.DEFAULT_ALGEBRAS
                    : properties.getAlgebraFamily();
        this.register = AlgebraRegister.getInstance(registerName);
    }

    /**
     * Creates an attribute-related node from a given {@link AspectNode} found
     * in a given {@link AspectGraph}. The type of the resulting node depends on
     * the {@link AttributeAspect} value of the given node and its incident
     * edges. The result is a {@link VariableNode} or {@link ProductNode}, or
     * <code>null</code> if the node contains no special {@link AttributeAspect}
     * value. An exception is thrown if the context of the node in the graph is
     * incorrect.
     * @param node the node for which we want an attribute-related node
     * @return a {@link VariableNode} or {@link ProductNode} corresponding to
     *         <code>node</code>, or <code>null</code>
     * @throws FormatException if attribute-related errors are found in
     *         <code>graph</code>
     */
    public ProductNode createAttributeNode(AspectNode node)
        throws FormatException {
        ProductNode result;
        AspectValue attributeValue = getAttributeValue(node);
        if (attributeValue == null) {
            result = null;
        } else if (attributeValue.equals(VALUE)
            || AttributeAspect.getAlgebra(attributeValue) != null) {
            result = createValueNode(node);
        } else {
            assert attributeValue.equals(PRODUCT) : String.format(
                "Illegal attribute aspect value: %s", attributeValue);
            result = createProductNode(node);
        }
        return result;
    }

    /**
     * Creates a {@link VariableNode} corresponding to a given aspect node whose
     * {@link AttributeAspect} value equals {@link #VALUE}. This is either a
     * true variable node (if the original node has no outgoing edges), or a
     * {@link ValueNode} whose value depends on the label of the node's
     * self-edge.
     * @param node the node for which a {@link VariableNode} is to be created
     * @throws FormatException if the outgoing edges of <code>node</code> are
     *         incorrect
     */
    private VariableNode createValueNode(AspectNode node)
        throws FormatException {
        VariableNode result;
        AspectValue attributeValue = AttributeAspect.getAttributeValue(node);
        Algebra<?> nodeAlgebra =
            attributeValue == null
                || AttributeAspect.VALUE.equals(attributeValue) ? null
                    : this.register.getImplementation(attributeValue.getName());
        assert attributeValue == null
            || AttributeAspect.VALUE.equals(attributeValue)
            || nodeAlgebra != null;
        String constant =
            nodeAlgebra == null ? null
                    : ((ConstantAspectValue) attributeValue).getContent();
        if (constant == null) {
            result =
                VariableNode.createVariableNode(node.getNumber(), nodeAlgebra);
        } else {
            result =
                ValueNode.createValueNode(nodeAlgebra,
                    nodeAlgebra.getValue(constant));
        }
        return result;
    }

    /**
     * Creates a product node corresponding to a node with
     * {@link AttributeAspect} value {@link #PRODUCT}. This only succeeds if the
     * outgoing edges form a consecutive range of argument numbers, without
     * duplication.
     * @param node the node for which a {@link ProductNode} is to be created
     * @throws FormatException if the outgoing edges of <code>node</code> are
     *         incorrect
     */
    private ProductNode createProductNode(AspectNode node)
        throws FormatException {
        return new ProductNode(node.getNumber(), arity(node));
    }

    /**
     * Returns the number of outgoing argument edges. Argument edges are aspect
     * edges with {@link AttributeAspect} value {@link #ARGUMENT}. Also checks
     * that the argument edges actually form a consecutive sequence ranging from
     * 0 to the arity.
     */
    private int arity(AspectNode node) throws FormatException {
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
     * <code>null</code> if the edge contains no special {@link AttributeAspect}
     * value or stands for a constant.
     * @param edge the edge for which we want an attribute-related edge
     * @param source the source node of the new edge
     * @param target the target node of the new edge
     * @return a {@link OperatorEdge} or {@link ArgumentEdge} corresponding to
     *         <code>edge</code>, or <code>null</code>
     * @throws FormatException if attribute-related errors are found in
     *         <code>graph</code>
     */
    public RuleEdge createAttributeEdge(AspectEdge edge, RuleNode source,
            RuleNode target) throws FormatException {
        RuleEdge result;
        AspectValue attributeValue = getAttributeValue(edge);
        if (attributeValue == null || source == target) {
            result = null;
        } else if (attributeValue == ARGUMENT) {
            int argNumber = Integer.parseInt(edge.label().text());
            ArgumentEdge argEdge =
                createArgumentEdge(argNumber, source, target);
            result = argEdge;
        } else {
            try {
                Operation operation =
                    this.register.getOperation(attributeValue.getName(),
                        edge.label().text());
                result = createOperatorEdge(operation, source, target);
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
     * @param source the source node of the edge to be created
     * @param target the target node of the edge to be created
     * @return a fresh {@link OperatorEdge}
     * @throws FormatException if <code>edge</code> does not have a correct set
     *         of outgoing attribute edges in <code>graph</code>
     */
    private OperatorEdge createOperatorEdge(Operation operator,
            RuleNode source, RuleNode target) throws FormatException {
        assert operator != null : String.format(
            "Cannot create edge between %s and %s for empty operator", source,
            target);
        if (!(source instanceof ProductNode)) {
            throw new FormatException(
                "Source of '%s'-edge should be a product node", operator);
        } else if (operator.getArity() != ((ProductNode) source).arity()) {
            throw new FormatException("Source arity of '%s'-edge should be %d",
                operator, operator.getArity());
        }
        if (!(target instanceof VariableNode)) {
            throw new FormatException(
                "Target of '%s'-edge should be a variable node", operator);
        }
        return new OperatorEdge((ProductNode) source, (VariableNode) target,
            operator);
    }

    /**
     * Returns an {@link ArgumentEdge} derived from a given aspect edge (which
     * should have attribute aspect value {@link #ARGUMENT}).
     * @param argNumber the argument number on the edge to be created
     * @param source the source node of the edge to be created
     * @param target the target node of the edge to be created
     * @return a fresh {@link ArgumentEdge}
     * @throws FormatException if one of the ends is <code>null</code>
     */
    private ArgumentEdge createArgumentEdge(int argNumber, RuleNode source,
            RuleNode target) throws FormatException {
        if (source == null) {
            throw new FormatException("Source of '%d'-edge has no image",
                argNumber);
        } else if (!(source instanceof ProductNode)) {
            throw new FormatException(
                "Source of '%d'-edge should be product node", argNumber);
        }
        if (target == null) {
            throw new FormatException("Target of '%d'-edge has no image",
                argNumber);
        } else if (!(target instanceof VariableNode)) {
            throw new FormatException(
                "Target of '%d'-edge should be a variable node", argNumber);
        }
        ArgumentEdge result =
            new ArgumentEdge((ProductNode) source, argNumber,
                (VariableNode) target);
        result.source().setArgument(argNumber, result.target());
        return result;
    }

    /** Two factories are equal if they have the same aspect graph and register. */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AttributeElementFactory) {
            AttributeElementFactory other = (AttributeElementFactory) obj;
            return this.graph == other.graph && this.register == other.register;
        } else {
            return false;
        }
    }

    /** The hash code is the sum of that of the aspect graph and register. */
    @Override
    public int hashCode() {
        return this.graph.hashCode() + this.register.hashCode();
    }

    /** Aspect graph on which this factory works. */
    private final AspectGraph graph;
    /** Algebra registry to use in creating the algebra values and operations. */
    private final AlgebraRegister register;
}
