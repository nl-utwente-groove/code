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

import groove.algebra.Operator;
import groove.graph.AbstractNode;
import groove.graph.DefaultLabel;
import groove.util.Fixable;
import groove.util.Groove;
import groove.view.FormatException;
import groove.view.aspect.ParameterAspect.ParameterAspectValue;

import java.util.ArrayList;
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
    AspectNode(int nr, String graphRole) {
        super(nr);
        this.graphRole = graphRole;
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
        return this == obj || obj != null && obj.getClass().equals(getClass())
            && ((AspectNode) obj).getNumber() == getNumber();
    }

    @Override
    public void setFixed() throws FormatException {
        if (!isFixed()) {
            setNodeLabelsFixed();
            try {
                if (isProduct()) {
                    testSignature();
                }
            } finally {
                this.allFixed = true;
            }
        }
    }

    /**
     * Checks for the correctness of product node signatures.
     */
    private void testSignature() throws FormatException {
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
            Operator operator = this.operatorEdge.getOperator();
            this.operatorEdge.target().setDataType(operator.getResultType());
            List<String> opTypes = operator.getParameterTypes();
            if (opTypes.size() != arity) {
                throw new FormatException(
                    "Product node arity %d conflicts with operator %s", arity,
                    operator, this);
            } else {
                for (int i = 0; i < arity; i++) {
                    this.argNodes.get(i).setDataType(opTypes.get(i));
                }
            }
        }
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
     * Adds a node label to this node, and processes the resulting aspects.
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
     * Concludes the processing of the node labels.
     * Afterwards {@link #setAspects(AspectLabel)} should not be called
     * any more.
     */
    private void setNodeLabelsFixed() throws FormatException {
        if (!this.nodeLabelsFixed) {
            try {
                if (isForRule()) {
                    if (!hasRole()) {
                        if (!conflictsWithRole(getType())) {
                            setRole(RuleAspect.READER);
                        }
                    } else if (conflictsWithRole(getType())) {
                        throw new FormatException(
                            "Conflicting node aspects %s and %s", getRole(),
                            getType(), this);
                    }
                } else {
                    if (hasRole()) {
                        throw new FormatException(
                            "Node aspect %s only allowed in rules", getRole(),
                            this);
                    }
                }
            } finally {
                this.nodeLabelsFixed = true;
            }
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
        if (isRole(value)) {
            if (hasRole()) {
                throw new FormatException("Conflicting node aspects %s and %s",
                    getRole(), value, this);
            } else if (value.getContent() != null) {
                throw new FormatException(
                    "Node role %s should not have quantifier name", value, this);
            } else {
                setRole(value);
            }
        } else if (isType(value)) {
            // aspect value represents a node type
            if (hasType()) {
                throw new FormatException("Conflicting node aspects %s and %s",
                    getType(), value, this);
            } else {
                setType(value);
            }
        } else if (value.getAspect() == ParameterAspect.getInstance()) {
            if (this.parameter == null) {
                this.parameter = (ParameterAspectValue) value;
            } else {
                throw new FormatException(
                    "Conflicting parameter aspects %s and %s", this.parameter,
                    value, this);
            }
        }
    }

    /** 
     * Infers aspect information from an incoming edge for this node.
     * Inferences from this node to the edge have already been drawn.
     */
    public void inferInAspect(AspectEdge edge) throws FormatException {
        assert edge.target() == this;
        testFixed(false);
        setNodeLabelsFixed();
        if ((edge.isNestedAt() || edge.isNestedIn()) && !isQuantifier()) {
            throw new FormatException(
                "Target node of %s-edge should be quantifier", edge.label(),
                this);
        }
    }

    /** Attempts to set the aspect type of this node to a given data type. */
    private void setDataType(String typeName) throws FormatException {
        AspectValue newType = AttributeAspect.getAttributeValueFor(typeName);
        assert AttributeAspect.VALUE.equals(newType)
            || AttributeAspect.isDataValue(newType);
        if (AttributeAspect.VALUE.equals(getType())) {
            setType(newType);
        } else if (!newType.equals(getType())) {
            throw new FormatException("Conflicting (inferred) types %s and %s",
                getType().getName(), newType.getName(), this);
        }
    }

    /** 
     * Infers aspect information from an outgoing edge for this node.
     * Inferences from this node to the edge have already been drawn.
     */
    public void inferOutAspect(AspectEdge edge) throws FormatException {
        assert edge.source() == this;
        testFixed(false);
        setNodeLabelsFixed();
        AspectLabel edgeLabel = edge.label();
        if (edge.isNestedAt()) {
            if (edge.getInnerText().equals(NestingAspect.AT_LABEL)) {
                if (!hasRole()) {
                    throw new FormatException(
                        "Source node of %s-edge should be rule element",
                        edgeLabel, this);
                }
                this.nestingLevel = edge.target();
            }
        } else if (edge.isNestedIn()) {
            assert edge.getInnerText().equals(NestingAspect.IN_LABEL);
            if (!isQuantifier()) {
                throw new FormatException(
                    "Source node of %s-edge should be quantifier", edgeLabel,
                    this);
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
            if (!hasType()) {
                setType(AttributeAspect.PRODUCT);
            } else if (!isProduct()) {
                throw new FormatException(
                    "Source node of %s-edge should be product node", edgeLabel,
                    this);
            } else {
                if (this.argNodes == null) {
                    this.argNodes = new ArrayList<AspectNode>();
                }
                // extend the list if necessary
                while (this.argNodes.size() <= edge.getArgument()) {
                    this.argNodes.add(null);
                }
                if (this.argNodes.get(edge.getArgument()) != null) {
                    throw new FormatException("Duplicate %s-edge",
                        edge.label(), this);
                }
                this.argNodes.set(edge.getArgument(), edge.target());
            }
        } else if (edge.isOperator()) {
            if (!hasType()) {
                setType(AttributeAspect.PRODUCT);
            } else if (!isProduct()) {
                throw new FormatException(
                    "Source node of %s-edge should be product node", edgeLabel,
                    this);
            } else if (this.operatorEdge == null) {
                this.operatorEdge = edge;
            } else if (!this.operatorEdge.getOperator().getParameterTypes().equals(
                edge.getOperator().getParameterTypes())) {
                throw new FormatException(
                    "Conflicting operator signatures for %s and %s",
                    this.operatorEdge.label(), edgeLabel, this);
            }
        } else if (edge.isAbstract() && edge.getTypeLabel().isNodeType()) {
            if (!hasType()) {
                setType(TypeAspect.ABS);
            } else if (!isAbstract()) {
                throw new FormatException(
                    "Conflicting aspect %s on abstract %s-type node",
                    getType(), edgeLabel, this);
            }
        }
    }

    /** Indicates if this node represents a remark. */
    public boolean isRemark() {
        return getType() != null && RuleAspect.REMARK.equals(getType());
    }

    /** Indicates if this node represents a quantifier. */
    public boolean isQuantifier() {
        return getType() != null
            && getType().getAspect() == NestingAspect.getInstance();
    }

    /** Indicates if this represents an abstract type node. */
    public boolean isAbstract() {
        return getType() != null && TypeAspect.ABS.equals(getType());
    }

    /** Indicates if this node has a declared role. */
    public boolean hasRole() {
        return getRole() != null;
    }

    /** Returns the role of this node. */
    public AspectValue getRole() {
        return this.role;
    }

    /** Changes the (aspect) type of this node. */
    private void setRole(AspectValue role) {
        assert isRole(role) : String.format("Aspect %s is not a valid role",
            role);
        this.role = role;
    }

    /** Indicates if this represents a product node. */
    public boolean isProduct() {
        return getType() != null && AttributeAspect.PRODUCT.equals(this.type);
    }

    /** Indicates if this represents a variable or value node. */
    public boolean isDataValue() {
        return getType() != null && !isProduct()
            && getType().getAspect() == AttributeAspect.getInstance();
    }

    /** Indicates if this represents a rule parameter. */
    public boolean hasParameter() {
        return this.parameter != null;
    }

    /** Returns the parameter aspect of this node, if any. */
    public ParameterAspectValue getParameter() {
        return this.parameter;
    }

    public boolean hasType() {
        return getType() != null;
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
     * <li> {@link TypeAspect#ABS}
     * <li> {@link AttributeAspect#PRODUCT} or {@link AttributeAspect#VALUE}
     * <li> A value satisfying {@link AttributeAspect#isDataValue(AspectValue)}, possibly with a constant content
     * </ul>
     */
    public AspectValue getType() {
        return this.type;
    }

    /** Changes the (aspect) type of this node. */
    private void setType(AspectValue type) {
        assert isType(type) : String.format("Aspect %s is not a valid role",
            this.role);
        this.type = type;
    }

    /** An aspect node is a meta-element if it is a quantifier or remark. */
    @Override
    public boolean isMeta() {
        return isQuantifier() || isRemark();
    }

    /**
     * Returns the list of node labels added to this node.
     */
    public List<AspectLabel> getNodeLabels() {
        return this.nodeLabels;
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
     * If this is a product node, returns the list of
     * argument nodes reached by outgoing argument edges.
     * @return an ordered list of argument nodes, or {@code null} if {@link #isProduct()} is {@code false}
     */
    public List<AspectNode> getArgNodes() {
        testFixed(true);
        return this.argNodes;
    }

    /** 
     * Retrieves the nesting level of this aspect node.
     * Only non-{@code null} if this node is an untyped or rule node. 
     */
    public AspectNode getNestingLevel() {
        return this.nestingLevel;
    }

    /**
     * Retrieves the parent of this node in the nesting hierarchy.
     * Only non-{@code null} if this node is a quantifier node. 
     */
    public AspectNode getNestingParent() {
        return this.nestingParent;
    }

    /** Indicates if this is supposed to be a rule element. */
    private boolean isForRule() {
        return Groove.RULE_ROLE.equals(this.graphRole);
    }

    private final String graphRole;
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
    /** The role of the aspect node. */
    private AspectValue role;
    /** The type of the aspect node. */
    private AspectValue type;
    /** The parameter aspect of this node, if any. */
    private ParameterAspectValue parameter;
    /** The aspect node representing the nesting level of this node. */
    private AspectNode nestingLevel;
    /** The aspect node representing the parent of this node in the nesting
     * hierarchy. */
    private AspectNode nestingParent;
    /** A list of argument types, if this represents a product node. */
    private List<AspectNode> argNodes;
    /** The operator of an outgoing operator edge. */
    private AspectEdge operatorEdge;

    /** Determines if a certain aspect value is a role. */
    static boolean isRole(AspectValue role) {
        return RuleAspect.CREATOR.equals(role)
            || RuleAspect.ERASER.equals(role)
            || RuleAspect.EMBARGO.equals(role)
            || RuleAspect.READER.equals(role) || RuleAspect.CNEW.equals(role);
    }

    /** Determines if a certain aspect value is a type. */
    static boolean isType(AspectValue type) {
        return RuleAspect.REMARK.equals(type) || TypeAspect.ABS.equals(type)
            || TypeAspect.SUB.equals(type)
            || type.getAspect() == NestingAspect.getInstance()
            || type.getAspect() == AttributeAspect.getInstance();
    }

    /**
     * Determines a conflict relation between aspect values used as types
     * and the presence of a role.
     */
    static boolean conflictsWithRole(AspectValue type) {
        assert type == null || isType(type);
        return type != null
            && (type.getAspect() != AttributeAspect.getInstance());
    }
}
