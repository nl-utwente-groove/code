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
import groove.view.FormatException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Graph node implementation that supports aspects.
 * @author Arend Rensink
 * @version $Revision$
 */
public class AspectNode extends AbstractNode implements AspectElement {
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

    /**
     * Sets the (declared) aspects for this node.
     * @throws FormatException if the aspects are inconsistent
     */
    public void setAspects(AspectLabel label) throws FormatException {
        assert !label.isEdgeOnly();
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

    /** Registers an incoming edge for this node. */
    public void addInEdge(AspectEdge edge) throws FormatException {

    }

    /** Registers an outgoing edge for this node. */
    public void addOutEdge(AspectEdge edge) throws FormatException {

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
        if (getType() != null) {
            result.add(toLabel(getType()));
        }
        if (hasParameter()) {
            result.add(toLabel(this.parameter));
        }
        return result;
    }

    private DefaultLabel toLabel(AspectValue value) {
        return DefaultLabel.createLabel(value.toString());
    }

    /**
     * Tests the consistency of the status of this node.
     * Throws an exception if the status is inconsistent;
     * has no effect otherwise. 
     * @throws FormatException if the status is inconsistent.
     */
    public void testConsistency() throws FormatException {
        // empty for now
        // TODO we may want to test for the correctness of the arguments
        // of a product node
    }

    /** The type of the aspect node. */
    private AspectValue type;
    /** The parameter aspect of this node, if any. */
    private AspectValue parameter;
    /** The inferred type of this node, if it is the result of an algebraic operation. */
    private AspectValue inferredType;
    /** A list of argument types, if this represents a product node. */
    private List<AspectValue> argTypes;

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
