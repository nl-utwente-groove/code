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
 * $Id: AspectNode.java,v 1.4 2008-01-30 09:31:33 iovka Exp $
 */
package groove.view.aspect;

import groove.graph.AbstractNode;
import groove.graph.DefaultLabel;
import groove.util.Fixable;
import groove.view.FormatException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Graph node implementation that supports aspects.
 * @author Arend Rensink
 * @version $Revision$
 */
public class AspectNode extends AbstractNode implements AspectElement, Fixable {
    /** Constructs an aspect node with a given number. */
    AspectNode(int nr) {
        super(nr);
        this.aspectMap = new AspectMap(true);
    }

    /**
     * This class does not guarantee unique representatives for the same number,
     * so we need to override {@link #hashCode()} and {@link #equals(Object)}.
     */
    @Override
    protected int computeHashCode() {
        return getNumber() ^ getClass().hashCode();
    }

    /** 
     * Use the same prefix as for default nodes, so the error messages
     * remain understandable.
     */
    @Override
    protected String getToStringPrefix() {
        return "n";
    }

    /**
     * This class does not guarantee unique representatives for the same number,
     * so we need to override {@link #hashCode()} and {@link #equals(Object)}.
     */
    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass().equals(getClass())
            && ((AspectNode) obj).getNumber() == getNumber();
    }

    /**
     * Adds an aspect value to the node, or updates an existing value.
     * @param value the aspect value to be added
     * @throws FormatException if the node already has a value for
     *         <code>value.getAspect()</code>
     */
    public void addInferredValue(AspectValue value) throws FormatException {
        try {
            getAspectMap().addInferredValue(value);
        } catch (FormatException e) {
            throw e.extend(this);
        }
    }

    /**
     * Adds an aspect value to the node.
     * @param value the aspect value to be added
     * @throws FormatException if the node already has a value for
     *         <code>value.getAspect()</code>
     */
    public void addDeclaredValue(AspectValue value) throws FormatException {
        try {
            getAspectMap().addDeclaredValue(value);
        } catch (FormatException e) {
            throw e.extend(this);
        }
    }

    public AspectValue getValue(Aspect aspect) {
        AspectValue result = getAspectMap().get(aspect);
        if (result == null) {
            result = aspect.getDefaultValue();
        }
        return result;
    }

    public Collection<AspectValue> getDeclaredValues() {
        return getAspectMap().getDeclaredValues();
    }

    /**
     * Returns the value for a given aspect for an edge that has this node as
     * its source, in case the node's own value gives a way to predict this.
     * @param aspect the aspect for which a value is to be inferred
     * @return an aspect value for an edge that has this node as its source, or
     *         <code>null</code> if no value can be inferred.
     */
    public AspectValue getSourceToEdgeValue(Aspect aspect) {
        AspectValue ownValue = getValue(aspect);
        if (ownValue == null) {
            return null;
        } else {
            return ownValue.sourceToEdge();
        }
    }

    /**
     * Returns the value for a given aspect for an edge that has this node as
     * its target, in case the node's own value gives a way to predict this.
     * @param aspect the aspect for which a value is to be inferred
     * @return an aspect value for an edge that has this node as its target, or
     *         <code>null</code> if no value can be inferred.
     */
    public AspectValue getTargetToEdgeValue(Aspect aspect) {
        AspectValue ownValue = getValue(aspect);
        if (ownValue == null) {
            return null;
        } else {
            return ownValue.targetToEdge();
        }
    }

    /**
     * Returns the map from aspects to aspect values for this edge.
     */
    public AspectMap getAspectMap() {
        return this.aspectMap;
    }

    /**
     * The internal map from aspects to corresponding values.
     */
    private final AspectMap aspectMap;

    @Override
    public void setFixed() throws FormatException {
        // check for correctness of product node signatures.
        if (!isFixed() && isProduct()) {
            if (this.argNodes == null) {
                throw new FormatException("Product node has no arguments", this);
            }
            int arity = this.argNodes.size();
            boolean argsOk = true;
            List<String> argTypes = new ArrayList<String>();
            for (int i = 0; i < arity; i++) {
                AspectNode argNode = this.argNodes.get(i);
                if (argNode == null) {
                    throw new FormatException("Missing product argument %d", i,
                        this);
                }
                AspectValue argNodeType = argNode.getType();
                if (argNodeType == null) {
                    argsOk = false;
                } else {
                    argTypes.add(argNodeType.getName());
                }
            }
            if (this.operatorEdge != null && argsOk) {
                List<String> opTypes =
                    this.operatorEdge.getOperator().getParameterTypes();
                if (!argTypes.equals(opTypes)) {
                    throw new FormatException(
                        "Product node signature %s differs from signature %s of %s",
                        argTypes, opTypes, this.operatorEdge.label(), this);
                }
            }
        }
        this.allFixed = true;
    }

    @Override
    public boolean isFixed() {
        return this.allFixed;
    }

    @Override
    public void testFixed(boolean fixed) {
        if (this.allFixed != fixed) {
            throw new IllegalStateException("Node fixation is not as expected");
        }
    }

    /**
     * Sets the (declared) aspects for this node.
     * @throws FormatException if the aspects are inconsistent
     */
    public void setAspects(AspectLabel label) throws FormatException {
        assert !label.isEdgeOnly();
        testFixed(false);
        if (this.nodeLabelsFixed) {
            throw new IllegalStateException(
                "Can't add node labels after edge inferences have been drawn");
        }
        this.nodeLabels.add(label);
        for (AspectValue aspect : label.getAspects()) {
            addAspectValue(aspect);
        }
    }

    /**
     * Adds a declared aspect value to this node.
     * @throws FormatException if the added value conflicts with a previously
     * declared one
     */
    private void addAspectValue(AspectValue value) throws FormatException {
        assert value.isNodeValue() : String.format(
            "Inappropriate node aspect %s", value, this);
        if (value.getAspect() == ParameterAspect.getInstance()) {
            if (this.parameter == null) {
                this.parameter = value;
            } else {
                throw new FormatException(
                    "Conflicting parameter aspects %s and %s", this.parameter,
                    value, this);
            }
        } else {
            // aspect value represents a node type
            if (this.type == null) {
                this.type = value;
            } else {
                throw new FormatException("Conflicting node aspects %s and %s",
                    this.type, value, this);
            }
        }
        if (value.getAspect() == RuleAspect.getInstance()
            && value.getContent() != null) {
            throw new FormatException(
                "Node role %s should not have quantifier name", value, this);
        }
    }

    /** 
     * Infers aspect information from an incoming edge for this node.
     * Inferences from this node to the edge have already been drawn.
     */
    public void inferInAspect(AspectEdge edge) throws FormatException {
        assert edge.target() == this;
        testFixed(false);
        this.nodeLabelsFixed = true;
        if ((edge.isNestedAt() || edge.isNestedIn()) && !isQuantifier()) {
            throw new FormatException(
                "Target node of %s-edge should be quantifier", edge.label(),
                this);
        } else if (edge.isArgument() && !AttributeAspect.isDataValue(getType())) {
            throw new FormatException(
                "Target node of %s-edge should be typed data node",
                edge.label(), this);
        } else if (edge.isOperator()) {
            AspectValue resultType =
                AttributeAspect.getAttributeValueFor(edge.getOperator().getResultType());
            if (getType() == null || AttributeAspect.VALUE.equals(getType())) {
                this.type = resultType;
            } else if (!resultType.equals(getType())) {
                throw new FormatException(
                    "Target node type %s conflicts with result type of %s-edge",
                    getType(), edge.label(), this);
            }
        }
    }

    /** 
     * Infers aspect information from an outgoing edge for this node.
     * Inferences from this node to the edge have already been drawn.
     */
    public void inferOutAspect(AspectEdge edge) throws FormatException {
        assert edge.source() == this;
        testFixed(false);
        this.nodeLabelsFixed = true;
        if (edge.isNestedAt()) {
            if (edge.getLabelText().equals(NestingAspect.AT_LABEL)) {
                if (getType() != null && !hasRuleRole()) {
                    throw new FormatException(
                        "Source node of %s-edge should be rule element",
                        edge.label(), this);
                }
                this.nestingLevel = edge.target();
            }
        } else if (edge.isNestedIn()) {
            assert edge.getLabelText().equals(NestingAspect.IN_LABEL);
            if (!isQuantifier()) {
                throw new FormatException(
                    "Source node of %s-edge should be quantifier",
                    edge.label(), this);
            } else {
                // collect collective nesting grandparents to test for circularity
                Set<AspectNode> grandparents = new HashSet<AspectNode>();
                AspectNode parent = this.nestingParent;
                while (parent != null) {
                    grandparents.add(parent);
                    parent = parent.getNestingParent();
                }
                if (grandparents.contains(this)) {
                    throw new FormatException(
                        "Circularity in the nesting hierarchy", this);
                }
                this.nestingParent = edge.target();
            }
        } else if (edge.isArgument()) {
            if (getType() == null) {
                this.type = AttributeAspect.PRODUCT;
            } else if (!isProduct()) {
                throw new FormatException(
                    "Source node of %s-edge should be product node",
                    edge.label(), this);
            } else {
                if (this.argNodes == null) {
                    this.argNodes = new ArrayList<AspectNode>();
                }
                // extend the list if necessary
                while (this.argNodes.size() < edge.getArgument()) {
                    this.argNodes.add(null);
                }
                if (this.argNodes.get(edge.getArgument()) != null) {
                    throw new FormatException("Duplicate %s-edge",
                        edge.label(), this);
                }
                this.argNodes.add(edge.getArgument(), edge.target());
            }
        } else if (edge.isOperator()) {
            if (getType() == null) {
                this.type = AttributeAspect.PRODUCT;
            } else if (!isProduct()) {
                throw new FormatException(
                    "Source node of %s-edge should be product node",
                    edge.label(), this);
            } else if (this.operatorEdge == null) {
                this.operatorEdge = edge;
            } else if (!this.operatorEdge.getOperator().getParameterTypes().equals(
                edge.getOperator().getParameterTypes())) {
                throw new FormatException(
                    "Conflicting operator signatures for %s and %s",
                    this.operatorEdge.label(), edge.label(), this);
            }
        }
    }

    /** Indicates if this node represents a remark. */
    public boolean isRemark() {
        return this.type != null && RuleAspect.REMARK.equals(this.type);
    }

    /** Indicates if this node represents a quantifier. */
    public boolean isQuantifier() {
        return this.type != null
            && this.type.getAspect() == NestingAspect.getInstance();
    }

    /** Indicates if this represents an abstract type node. */
    public boolean isAbstract() {
        return this.type != null && TypeAspect.ABS.equals(this.type);
    }

    /** Indicates if this node has a declared role. */
    public boolean hasRuleRole() {
        return this.type != null && !isRemark()
            && this.type.getAspect() == RuleAspect.getInstance();
    }

    /** Indicates if this represents a product node. */
    public boolean isProduct() {
        return this.type != null && AttributeAspect.PRODUCT.equals(this.type);
    }

    /** Indicates if this represents a variable or value node. */
    public boolean isDataValue() {
        return this.type != null && !isProduct()
            && this.type.getAspect() == AttributeAspect.getInstance();
    }

    /** Indicates if this represents a rule parameter. */
    public boolean hasParameter() {
        return this.parameter != null;
    }

    /** Returns the parameter aspect of this node, if any. */
    public AspectValue getParameter() {
        return this.parameter;
    }

    /** 
     * Returns the type of this aspect node.
     * This is one of the following values:
     * <ul>
     * <li> {@link RuleAspect#REMARK}
     * <li> A quantifier, viz. one of
     *      {@link NestingAspect#EXISTS}, 
     *      {@link NestingAspect#FORALL} or
     *      {@link NestingAspect#FORALL_POS}
     * <li> A rule role (without a quantifier name), viz. one of
     *      {@link RuleAspect#EMBARGO}, 
     *      {@link RuleAspect#CREATOR}, 
     *      {@link RuleAspect#ERASER}, 
     *      {@link RuleAspect#READER} or
     *      {@link RuleAspect#CNEW}
     * <li> {@link TypeAspect#ABS}
     * <li> {@link AttributeAspect#PRODUCT} or {@link AttributeAspect#VALUE}
     * <li> A value satisfying {@link AttributeAspect#isDataValue(AspectValue)}, possibly with a constant content
     * </ul>
     */
    public AspectValue getType() {
        return this.type;
    }

    /**
     * Returns the list of (plain) labels that should be put on this
     * node in the plain graph view.
     */
    public List<DefaultLabel> getPlainLabels() {
        List<DefaultLabel> result = new ArrayList<DefaultLabel>();
        for (AspectLabel label : this.nodeLabels) {
            result.add(DefaultLabel.createLabel(label.toString()));
        }
        return result;
    }

    /** 
     * Retrieves the nesting level of this aspect node.
     * Only non-{@code null} if this node is an untyped or rule node. 
     */
    AspectNode getNestingLevel() {
        return this.nestingLevel;
    }

    /**
     * Retrieves the parent of this node in the nesting hierarchy.
     * Only non-{@code null} if this node is a quantifier node. 
     */
    AspectNode getNestingParent() {
        return this.nestingParent;
    }

    /** The list of aspect labels defining node aspects. */
    private List<AspectLabel> nodeLabels = new ArrayList<AspectLabel>();
    /**
     * Indicates that the list of node labels is now fixed, and nothing
     * should be added to it any more. In particular, {@link #setAspects(AspectLabel)}
     * should not be called any more.
     */
    private boolean nodeLabelsFixed;
    /** Indicates that the entire node is fixed. */
    private boolean allFixed;
    /** The type of the aspect node. */
    private AspectValue type;
    /** The parameter aspect of this node, if any. */
    private AspectValue parameter;
    /** The aspect node representing the nesting level of this node. */
    private AspectNode nestingLevel;
    /** The aspect node representing the parent of this node in the nesting
     * hierarchy. */
    private AspectNode nestingParent;
    /** A list of argument types, if this represents a product node. */
    private List<AspectNode> argNodes;
    /** The operator of an outgoing operator edge. */
    private AspectEdge operatorEdge;

    /** A quantifier as occurring on an aspect node. */
    public static class Quantifier {
        /** 
         * Creates a new, possibly named quantifier.
         * @param existential if {@code true}, this is an existential
         * quantifier, otherwise it is a universal
         * @param positive if {@code true}, this is a positive universal.
         * Cannot be {@code true} if {@code existential} is {@code true}
         * @param name the (possibly {@code null}) name of this quantifier
         */
        public Quantifier(boolean existential, boolean positive, String name) {
            assert !(existential && positive);
            this.name = name;
            this.existential = existential;
            this.positive = positive;
        }

        /** Returns the (possibly {@code null} name of this quantifier. */
        public String getName() {
            return this.name;
        }

        /** Indicates if this quantifier is existential. If not, it is universal. */
        public boolean isExistential() {
            return this.existential;
        }

        /** Indicates if this quantifier is a positive universal. */
        public boolean isPositive() {
            return this.positive;
        }

        /** If {@code true}, this quantifier is existential, otherwise it is universal. */
        private final boolean existential;
        /** The (possibly {@code null}) name of this quantifier. */
        private final String name;
        /** if {@code true}, this quantifier is a positive universal. */
        private final boolean positive;
    }
}
