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

import static groove.view.aspect.AspectKind.ABSTRACT;
import static groove.view.aspect.AspectKind.EMBARGO;
import static groove.view.aspect.AspectKind.NONE;
import static groove.view.aspect.AspectKind.PRODUCT;
import static groove.view.aspect.AspectKind.READER;
import static groove.view.aspect.AspectKind.UNTYPED;
import groove.algebra.Operator;
import groove.graph.AbstractNode;
import groove.graph.DefaultLabel;
import groove.graph.GraphRole;
import groove.util.Fixable;
import groove.view.FormatError;
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
    public AspectNode(int nr, GraphRole graphRole) {
        super(nr);
        assert graphRole.inGrammar();
        this.graphRole = graphRole;
    }

    /** Returns the graph role set for this aspect node. */
    public GraphRole getGraphRole() {
        return this.graphRole;
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
    public void setFixed() {
        if (!isFixed()) {
            this.allFixed = true;
            try {
                checkAspects();
                if (getAttrKind() == PRODUCT) {
                    testSignature();
                }
            } catch (FormatException exc) {
                addErrors(exc.getErrors());
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
        if (this.operatorEdge == null) {
            throw new FormatException("Product node has no operators", this);
        }
        int arity = this.argNodes.size();
        Operator operator = this.operatorEdge.getOperator();
        if (arity != operator.getArity()) {
            throw new FormatException(
                "Product node arity %d is incorrect for operator %s", arity,
                operator, this);
        }
        for (int i = 0; i < arity; i++) {
            AspectNode argNode = this.argNodes.get(i);
            if (argNode == null) {
                throw new FormatException("Missing product argument %d", i,
                    this);
            }
        }
        // type correctness of the parameters and result has already been tested for
        // as part of inferInAspect and inferOutAspect
    }

    @Override
    public boolean isFixed() {
        return this.allFixed;
    }

    @Override
    public void testFixed(boolean fixed) {
        if (this.allFixed != fixed) {
            throw new IllegalStateException(String.format(
                "Aspect node %d should%s be fixed", getNumber(), fixed ? ""
                        : " not"));
        }
    }

    @Override
    public boolean hasErrors() {
        return !this.errors.isEmpty();
    }

    @Override
    public List<FormatError> getErrors() {
        return this.errors;
    }

    private void addErrors(Collection<FormatError> errors) {
        for (FormatError error : errors) {
            this.errors.add(error.extend(this));
        }
    }

    /**
     * Adds a node label to this node, and processes the resulting aspects.
     */
    public void setAspects(AspectLabel label) {
        assert label.isFixed();
        testFixed(false);
        this.nodeLabels.add(label);
        if (label.hasErrors()) {
            addErrors(label.getErrors());
        } else {
            try {
                for (Aspect aspect : label.getAspects()) {
                    addAspect(aspect);
                }
            } catch (FormatException exc) {
                this.errors.addAll(exc.getErrors());
            }
        }
    }

    /** 
     * Concludes the processing of the node labels.
     * Afterwards {@link #setAspects(AspectLabel)} should not be called
     * any more.
     */
    private void checkAspects() throws FormatException {
        if (this.graphRole == GraphRole.RULE) {
            // rule nodes that are not explicitly typed must be readers
            if (!hasAspect()) {
                setAspect(READER.getAspect());
            }
            if (hasAttrAspect() && getKind() != READER && getKind() != EMBARGO) {
                throw new FormatException("Conflicting aspects %s and %s",
                    getAttrAspect(), getAspect());
            }
        } else if (getKind().isRole()) {
            throw new FormatException("Node aspect %s only allowed in rules",
                getAspect(), this);
        } else if (!hasAspect()) {
            setAspect(AspectKind.NONE.getAspect());
        }
        if (!hasAttrAspect()) {
            setAttrAspect(AspectKind.NONE.getAspect());
        }
    }

    /**
     * Adds a declared aspect value to this node.
     * @throws FormatException if the added value conflicts with a previously
     * declared one
     */
    private void addAspect(Aspect value) throws FormatException {
        assert value.isForNode(getGraphRole()) : String.format(
            "Inappropriate node aspect %s", value, this);
        AspectKind kind = value.getKind();
        //        if (!kind.isForNode(getGraphRole())) {
        //            throw new FormatException("Aspect %s is not allowed on nodes",
        //                value, this);
        //        } else 
        if (kind.isAttrKind()) {
            if (hasAttrAspect()) {
                throw new FormatException("Conflicting node aspects %s and %s",
                    getAttrKind(), value, this);
            } else {
                setAttrAspect(value);
            }
        } else if (kind.isParam()) {
            if (hasParam()) {
                throw new FormatException(
                    "Conflicting parameter aspects %s and %s", this.param,
                    value, this);
            } else {
                setParam(value);
            }
        } else if (hasAspect()) {
            throw new FormatException("Conflicting node aspects %s and %s",
                getAspect(), value, this);
        } else if (kind.isRole() && value.getContent() != null) {
            throw new FormatException(
                "Node aspect %s should not have quantifier name", value, this);
        } else {
            setAspect(value);
        }
    }

    /** 
     * Infers aspect information from an incoming edge for this node.
     * Inferences from this node to the edge have already been drawn.
     */
    public void inferInAspect(AspectEdge edge) throws FormatException {
        assert edge.target() == this;
        testFixed(false);
        //setNodeLabelsFixed();
        if ((edge.isNestedAt() || edge.isNestedIn())
            && !getKind().isQuantifier()) {
            throw new FormatException(
                "Target node of %s-edge should be quantifier", edge.label(),
                this);
        } else if (edge.isOperator()) {
            Operator operator = edge.getOperator();
            setDataType(operator.getResultType());
        }
    }

    /** Attempts to set the aspect type of this node to a given data type. */
    private void setDataType(String typeName) throws FormatException {
        assert !isFixed();
        Aspect newType = Aspect.getAspect(typeName);
        assert newType.getKind().isTypedData();
        setAttrAspect(newType);
    }

    /** 
     * Infers aspect information from an outgoing edge for this node.
     * Inferences from this node to the edge have already been drawn.
     */
    public void inferOutAspect(AspectEdge edge) throws FormatException {
        assert edge.source() == this;
        testFixed(false);
        //setNodeLabelsFixed();
        AspectLabel edgeLabel = edge.label();
        if (edge.isNestedAt()) {
            if (getKind().isMeta()) {
                throw new FormatException(
                    "Source node of %s-edge should be rule element", edgeLabel,
                    this);
            }
            this.nestingLevel = edge.target();
        } else if (edge.isNestedIn()) {
            if (!getKind().isQuantifier()) {
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
            if (!hasAttrAspect()) {
                setAttrAspect(PRODUCT.getAspect());
            } else if (getAttrKind() != PRODUCT) {
                throw new FormatException(
                    "Source node of %s-edge should be product node", edgeLabel,
                    this);
            }
            if (this.argNodes == null) {
                this.argNodes = new ArrayList<AspectNode>();
            }
            int index = edge.getArgument();
            // extend the list if necessary
            while (this.argNodes.size() <= index) {
                this.argNodes.add(null);
            }
            if (this.argNodes.get(index) != null) {
                throw new FormatException("Duplicate %s-edge", edge.label(),
                    this);
            }
            this.argNodes.set(index, edge.target());
            // infer target type if an operator edge is already present
            if (this.operatorEdge != null) {
                List<String> paramTypes =
                    this.operatorEdge.getOperator().getParamTypes();
                if (index < paramTypes.size()) {
                    edge.target().setDataType(paramTypes.get(index));
                }
            }
        } else if (edge.isOperator()) {
            if (!hasAttrAspect()) {
                setAttrAspect(PRODUCT.getAspect());
            } else if (getAttrKind() != PRODUCT) {
                throw new FormatException(
                    "Source node of %s-edge should be product node", edgeLabel,
                    this);
            } else if (this.operatorEdge == null) {
                this.operatorEdge = edge;
            } else if (!this.operatorEdge.getOperator().getParamTypes().equals(
                edge.getOperator().getParamTypes())) {
                throw new FormatException(
                    "Conflicting operator signatures for %s and %s",
                    this.operatorEdge.label(), edgeLabel, this);
            } else {
                // infer operand types of present argument edges
                for (int i = 0; i < this.argNodes.size(); i++) {
                    AspectNode argNode = this.argNodes.get(i);
                    if (argNode != null) {
                        String paramType =
                            this.operatorEdge.getOperator().getParamTypes().get(
                                i);
                        argNode.setDataType(paramType);
                    }
                }
            }
        } else if (edge.getKind() == ABSTRACT
            && edge.getTypeLabel().isNodeType()) {
            setAspect(ABSTRACT.getAspect());
        }
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

    /** Sets or specialises the attribute aspect of this node. */
    private void setAttrAspect(Aspect newAttr) throws FormatException {
        AspectKind attrKind = newAttr.getKind();
        assert attrKind == NONE || attrKind.isAttrKind() : String.format(
            "Aspect %s is not attribute-related", newAttr);
        // it may be the new attribute is inferred from an incoming edge
        // but then we only change the attribute if the new one is "better"
        if (!hasAttrAspect()) {
            this.attr = newAttr;
        } else if (getAttrKind() == UNTYPED && newAttr.getKind().isData()) {
            this.attr = newAttr;
        } else if (getAttrKind() != attrKind) {
            throw new FormatException("Conflicting (inferred) types %s and %s",
                getAspect(), attrKind, this);
        } else if (!getAttrAspect().hasContent() && newAttr.hasContent()) {
            this.attr = newAttr;
        } else if (getAttrAspect().hasContent() && newAttr.hasContent()) {
            throw new FormatException("Conflicting (inferred) types %s and %s",
                getAspect(), attrKind, this);
        }
    }

    /** Returns the parameter aspect of this node, if any. */
    public Aspect getAttrAspect() {
        return this.attr;
    }

    /** Indicates if this represents a rule parameter. */
    public boolean hasAttrAspect() {
        return this.attr != null && this.attr.getKind() != NONE;
    }

    @Override
    public AspectKind getAttrKind() {
        return hasAttrAspect() ? getAttrAspect().getKind() : NONE;
    }

    /** 
     * If this is a product node, returns the list of
     * argument nodes reached by outgoing argument edges.
     * @return an ordered list of argument nodes, or {@code null} if 
     * this is not a product node.
     */
    public List<AspectNode> getArgNodes() {
        testFixed(true);
        return this.argNodes;
    }

    /** Changes the (aspect) type of this node. */
    private void setParam(Aspect type) {
        assert type.getKind() == NONE || type.getKind().isParam() : String.format(
            "Aspect %s is not a parameter", type);
        this.param = type;
    }

    /** Returns the parameter aspect of this node, if any. */
    public Aspect getParam() {
        return this.param;
    }

    /** Indicates if this represents a rule parameter. */
    public boolean hasParam() {
        return this.param != null;
    }

    /** Returns the parameter kind of this node, if any. */
    public AspectKind getParamKind() {
        assert hasParam();
        return hasParam() ? getParam().getKind() : NONE;
    }

    /** Returns the parameter number, or {@code -1} if there is none. */
    public int getParamNr() {
        return hasParam() && getParam().hasContent()
                ? (Integer) getParam().getContent() : -1;
    }

    /** Changes the (aspect) type of this node. */
    private void setAspect(Aspect type) throws FormatException {
        assert !type.getKind().isAttrKind() && !type.getKind().isParam() : String.format(
            "Aspect %s is not a valid node type", type);
        if (this.aspect == null) {
            this.aspect = type;
        } else if (!this.aspect.equals(type)) {
            throw new FormatException("Conflicting aspects %s and %s",
                this.aspect, type, this);
        }
    }

    /** 
     * Returns the aspect that determines the kind of this node.
     */
    public Aspect getAspect() {
        return this.aspect;
    }

    /** 
     * Tests if the main aspect of this node has been initialised.
     */
    boolean hasAspect() {
        return getAspect() != null;
    }

    /** 
     * Returns the determining aspect kind of this node.
     * This is one of {@link AspectKind#REMARK}, a role, a quantifier,
     * or {@link AspectKind#ABSTRACT}.
     */
    @Override
    public AspectKind getKind() {
        return hasAspect() ? getAspect().getKind() : NONE;
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

    /** Returns the optional level name, if this is a quantifier node. */
    public String getLevelName() {
        if (getKind().isQuantifier()) {
            return (String) getAspect().getContent();
        } else {
            return null;
        }
    }

    private final GraphRole graphRole;
    /** The list of aspect labels defining node aspects. */
    private List<AspectLabel> nodeLabels = new ArrayList<AspectLabel>();
    /** Indicates that the entire node is fixed. */
    private boolean allFixed;
    /** The type of the aspect node. */
    private Aspect aspect;
    /** The attribute-related aspect. */
    private Aspect attr;
    /** The parameter aspect of this node, if any. */
    private Aspect param;
    /** The aspect node representing the nesting level of this node. */
    private AspectNode nestingLevel;
    /** The aspect node representing the parent of this node in the nesting
     * hierarchy. */
    private AspectNode nestingParent;
    /** A list of argument types, if this represents a product node. */
    private List<AspectNode> argNodes;
    /** The operator of an outgoing operator edge. */
    private AspectEdge operatorEdge;
    /** List of syntax errors in this node. */
    private final List<FormatError> errors = new ArrayList<FormatError>();
}
