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
 * $Id: AspectEdge.java,v 1.10 2008-01-30 09:31:33 iovka Exp $
 */
package groove.view.aspect;

import groove.algebra.AlgebraRegister;
import groove.algebra.Operator;
import groove.algebra.UnknownSymbolException;
import groove.graph.AbstractEdge;
import groove.graph.DefaultLabel;
import groove.graph.Edge;
import groove.graph.Label;
import groove.graph.TypeLabel;
import groove.rel.RegExpr;
import groove.trans.RuleLabel;
import groove.view.FormatError;
import groove.view.FormatException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Edge enriched with aspect data. Aspect edge labels are interpreted as
 * {@link DefaultLabel}s.
 * @author Arend Rensink
 * @version $Revision$
 */
public class AspectEdge extends
        AbstractEdge<AspectNode,DefaultLabel,AspectNode> implements
        AspectElement {
    /**
     * Constructs a new edge, with source and target node, label, and aspect
     * values as given.
     * @param source the source node for this edge
     * @param target the target node for this edge
     * @param parseData the aspect values for this edge.
     * @throws FormatException if the aspect values of <code>parseData</code>
     *         are inconsistent with those of the source or target nodes
     */
    AspectEdge(AspectNode source, AspectNode target, AspectMap parseData)
        throws FormatException {
        super(source, DefaultLabel.createLabel(parseData.getText()), target);
        this.parseData = parseData;
    }

    /** 
     * Initialises and checks all aspect value-related properties.
     * This method should always be called immediately after the constructor. 
     * @throws FormatException if there are aspect-related errors.
     */
    public void initAspects() throws FormatException {
        for (AspectValue value : this.parseData.getDeclaredValues()) {
            if (!value.isEdgeValue()) {
                throw new FormatException(
                    "Aspect value '%s' cannot be used on edges", value, this);
            }
        }
        addInferences();
        testLabel();
    }

    /**
     * Adds values to the aspect map of an edge that are inferred from source
     * and target nodes.
     * @throws FormatException if an explicitly declared aspect value is
     *         overruled
     */
    private void addInferences() throws FormatException {
        AspectMap sourceData = source().getAspectMap();
        AspectMap targetData = target().getAspectMap();
        for (Aspect aspect : Aspect.getAllAspects()) {
            try {
                AspectValue edgeValue = this.parseData.get(aspect);
                AspectValue sourceValue = sourceData.get(aspect);
                AspectValue sourceInference =
                    sourceValue == null ? null : sourceValue.sourceToEdge();
                AspectValue targetValue = targetData.get(aspect);
                AspectValue targetInference =
                    targetValue == null ? null : targetValue.targetToEdge();
                AspectValue result =
                    aspect.getMax(edgeValue, sourceInference, targetInference);
                if (result != null && !result.equals(edgeValue)) {
                    this.parseData.addInferredValue(result);
                }
            } catch (FormatException e) {
                throw e.extend(this);
            }
        }
    }

    /**
     * Tests if the parsed edge label is allowed by all inferred aspects.
     * @throws FormatException if there is an aspect whose value for this edge
     *         is incompatible with the edge label
     * @see Aspect#testLabel(DefaultLabel, AspectValue, AspectValue)
     */
    private void testLabel() throws FormatException {
        List<FormatError> errors = new ArrayList<FormatError>();
        for (AspectValue declaredAspectValue : getDeclaredValues()) {
            Aspect aspect = declaredAspectValue.getAspect();
            AspectValue inferredValue = getAspectMap().get(aspect);
            try {
                aspect.testLabel(label(), declaredAspectValue, inferredValue);
            } catch (FormatException e) {
                for (FormatError error : e.getErrors()) {
                    errors.add(error.extend(this));
                }
            }
        }
        if (!errors.isEmpty()) {
            throw new FormatException(errors);
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
        return this.parseData.getDeclaredValues();
    }

    /**
     * Returns the map from aspects to aspect values for this edge, lazily
     * creating it first.
     */
    public AspectMap getAspectMap() {
        return this.parseData;
    }

    /**
     * Returns the plain text label for the aspect edge.
     */
    public String getPlainText() {
        return this.parseData.toString();
    }

    /**
     * Returns the label that this edge gets, when compiled to a model edge.
     * Convenience method for {@code getAspectMap().toModelLabel()}
     * @throws FormatException if the label contains a format error
     */
    public Label getModelLabel() throws FormatException {
        Label result;
        try {
            result = getAspectMap().toModelLabel();
        } catch (FormatException exc) {
            throw new FormatException(exc.getMessage(), this, this.source());
        }
        if (result != null && !result.isBinary() && !source().equals(target())) {
            throw new FormatException(
                "%s label '%s' should only occur on nodes",
                TypeLabel.getDescriptor(result.getKind()), result, this,
                this.source());
        }
        return result;
    }

    /**
     * Includes the hash code of the associated aspect values.
     */
    @Override
    protected int computeHashCode() {
        int result = super.computeHashCode();
        for (Aspect aspect : Aspect.getAllAspects()) {
            AspectValue value = getValue(aspect);
            if (value != null) {
                result += value.hashCode();
            }
        }
        return result;
    }

    /**
     * Tests equality of type, ends and aspect values.
     */
    @Override
    public boolean equals(Object obj) {
        return isTypeEqual(obj) && isEndEqual((Edge) obj)
            && isLabelEqual((Edge) obj) && isAspectEqual((AspectEdge) obj);
    }

    /**
     * Tests if the object is an {@link AspectEdge}.
     */
    @Override
    protected boolean isTypeEqual(Object obj) {
        return obj instanceof AspectEdge;
    }

    /** Tests if the aspect map of this edge equals that of the other. */
    protected boolean isAspectEqual(AspectEdge other) {
        for (Aspect aspect : Aspect.getAllAspects()) {
            if (getValue(aspect) != other.getValue(aspect)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the edge can be displayed as a node label.
     * @return true, if the label is a node type label or a flag;
     *         false, otherwise.
     */
    @Override
    public boolean isBinary() {
        boolean result = true;
        try {
            Label modelLabel = getModelLabel();
            if (modelLabel != null) {
                result = modelLabel.isBinary();
            }
        } catch (FormatException e1) {
            // do nothing
        }
        return result;
    }

    /**
     * This implementation makes sure that edges with node type labels are
     * ordered before other edges.
     */
    @Override
    protected int compareToEdge(Edge obj) {
        assert obj instanceof AspectEdge : String.format(
            "Can't compare aspect edge '%s' to non-aspect edge '%s'", this, obj);
        AspectEdge other = (AspectEdge) obj;
        int result;
        // first compare the source, then the aspects,
        // label, then the target
        result = source().compareTo(other.source());
        if (result == 0) {
            result = (other.isNodeType() ? 1 : 0) - (isNodeType() ? 1 : 0);
        }
        if (result == 0) {
            result = (other.isFlag() ? 1 : 0) - (isFlag() ? 1 : 0);
        }
        if (result == 0) {
            result = getAspectMap().compareTo(other.getAspectMap());
        }
        if (result == 0) {
            result = label().compareTo(other.label());
        }
        if (result == 0) {
            result = target().compareTo(other.target());
        }
        return result;
    }

    /** Tests if this aspect edge stands for a node type. */
    @Override
    public boolean isNodeType() {
        boolean result = false;
        try {
            Label modelLabel = getModelLabel();
            result = modelLabel != null && modelLabel.isNodeType();
        } catch (FormatException e) {
            // do nothing
        }
        return result;
    }

    /** Tests if this aspect edge stands for a flag. */
    @Override
    public boolean isFlag() {
        boolean result = false;
        try {
            Label modelLabel = getModelLabel();
            result = modelLabel != null && modelLabel.isFlag();
        } catch (FormatException e) {
            // do nothing
        }
        return result;
    }

    /**
     * This implementation defers to {@link #getPlainText()}
     */
    @Override
    protected String getLabelText() {
        return getPlainText();
    }

    /**
     * The aspect information of the label, set at construction time.
     */
    private final AspectMap parseData;

    /**
     * Sets the (declared) aspects for this edge from the edge label.
     * TODO eventually to be replaced by code in the constructor
     * @throws FormatException if the aspects are inconsistent
     */
    public void setAspects(AspectLabel label) throws FormatException {
        assert !label.isNodeOnly();
        for (AspectValue aspect : label.getAspects()) {
            addAspectValue(aspect, true);
        }
        this.label = label;
    }

    /** 
     * Adds aspect inference from an end node to this edge.
     * Inference exists for rule roles and nesting. 
     */
    public void inferAspects(AspectNode node) throws FormatException {
        AspectValue nodeType = node.getType();
        if (nodeType == null) {
            return;
        }
        if (nodeType.getAspect() == RuleAspect.getInstance()) {
            addAspectValue(nodeType, false);
        } else if (nodeType.getAspect() == NestingAspect.getInstance()) {
            addAspectValue(NestingAspect.NESTED, false);
        }
    }

    /**
     * Adds a declared or inferred aspect value to this edge.
     * @param value the aspect value
     * @param declared indicates if the value is declared or inferred
     * @throws FormatException if the added value conflicts with a previously
     * declared or inferred one
     */
    private void addAspectValue(AspectValue value, boolean declared)
        throws FormatException {
        assert value.isEdgeValue();
        if (value.getAspect() == TypeAspect.getInstance()) {
            assert TypeAspect.PATH.equals(value)
                || TypeAspect.EMPTY.equals(value);
            if (this.labelMode == null) {
                this.labelMode = value;
            } else {
                throw new FormatException("Conflicting edge aspects %s and %s",
                    this.labelMode, value, this);
                // actually this should not happen since both of these
                // aspects are specified to be the last in a label
            }
        } else {
            addAspectType(value, declared);
        }
    }

    /**
     * Sets the declared or inferred aspect type for this edge.
     * @param value the aspect value; must determine a type
     * @param declared indicates if the value is declared or inferred
     * @throws FormatException if the added value conflicts with a previously
     * declared or inferred one
     */
    private void addAspectType(AspectValue value, boolean declared)
        throws FormatException {
        assert value.isEdgeValue();
        // Compute the inferred type, if no errors are found
        AspectValue inferredType = null;
        if (getType() == null) {
            inferredType = value;
            String innerText = this.label.getInnerText();
            // in some cases the label text has to be parsed
            // depending on the aspect value
            if (NestingAspect.NESTED.equals(value)
                && !NestingAspect.ALLOWED_LABELS.contains(innerText)) {
                throw new FormatException("Unknown label '%s' on nesting edge",
                    innerText, this);
            } else if (AttributeAspect.ARGUMENT.equals(value)) {
                try {
                    this.argumentNr = Integer.parseInt(innerText);
                } catch (NumberFormatException exc) {
                    // do nothing
                }
                if (this.argumentNr < 0) {
                    throw new FormatException(
                        "Label '%s' is not a valid argument number", innerText,
                        this);
                }
            } else if (AttributeAspect.isDataValue(value)) {
                try {
                    this.operator =
                        AlgebraRegister.getOperator(value.getName(), innerText);
                } catch (UnknownSymbolException e) {
                    throw new FormatException(
                        "Label '%s' is not an operator of type %s", innerText,
                        value.getName(), this);
                }
            }
        } else if (!declared && value.equals(getType())) {
            // the existing type is taken to be at least as precise
            inferredType = getType();
        } else if (value.getAspect() == NestingAspect.getInstance()
            && !NestingAspect.NESTED.equals(value)) {
            // backward compatibility to take care of edges such as
            // exists=q:del:a rather than del=q:a or 
            // exists=q:a rather than use=q:a
            assert value.getContent() != null;
            // this value is a named quantifier; see if we can add the
            // name to an already declared rule role
            if (hasRole() && getType().getContent() == null) {
                inferredType = getType().newValue(value.getContent());
            } else if (getType() == null) {
                // pretend this is a named reader aspect value
                inferredType = RuleAspect.READER.newValue(value.getContent());
                this.quantifierAsReader = true;
            }
        } else if (value.getAspect() == RuleAspect.getInstance()) {
            if (this.quantifierAsReader && value.getContent() == null) {
                this.quantifierAsReader = false;
                inferredType = value.newValue(getType().getContent());
            } else if (!declared && RuleAspect.EMBARGO.equals(getType())
                && RuleAspect.ERASER.equals(value)) {
                // special case: we can have embargo edges to eraser nodes
                inferredType = getType();
            }
        }
        if (inferredType == null) {
            throw new FormatException("Conflicting edge aspects %s and %s",
                this.inferredType, value, this);
        }
        this.inferredType = inferredType;
        if (declared) {
            this.declaredType = inferredType;
        }
    }

    /** Indicates if this edge represents a remark. */
    public boolean isRemark() {
        return RuleAspect.REMARK.equals(this.inferredType);
    }

    /** Indicates if this edge represents a remark. */
    public boolean isAbstract() {
        return TypeAspect.ABS.equals(this.inferredType);
    }

    /** Indicates if this edge represents a remark. */
    public boolean isSubtype() {
        return TypeAspect.SUB.equals(this.inferredType);
    }

    /** Indicates if this edge has a declared or inferred role in a rule. */
    public boolean hasRole() {
        return this.inferredType != null && !isRemark()
            && this.inferredType.getAspect() == RuleAspect.getInstance();
    }

    /** Indicates if this edge is a "nested:at". */
    public boolean isNestedAt() {
        return this.inferredType != null
            && this.inferredType.getAspect() == NestingAspect.getInstance()
            && NestingAspect.AT_LABEL.equals(this.label.getInnerText());
    }

    /** Indicates if this edge is a "nested:in". */
    public boolean isNestedIn() {
        return this.inferredType != null
            && this.inferredType.getAspect() == NestingAspect.getInstance()
            && NestingAspect.IN_LABEL.equals(this.label.getInnerText());
    }

    /** Indicates if this is an argument edge. */
    public boolean isArgument() {
        return this.argumentNr >= 0;
    }

    /**
     * Returns the argument number, if this is an argument edge. 
     * @return a non-negative number if and only if this is an argument edge 
     */
    public int getArgument() {
        return this.argumentNr;
    }

    /** Indicates if this is an operator edge. */
    public boolean isOperator() {
        return this.operator != null;
    }

    /** 
     * Returns an algebraic operator, if this is an operator edge.
     * @return a non-{@code null} object if and only if this is an operator edge
     */
    public Operator getOperator() {
        return this.operator;
    }

    /** 
     * Returns the type of this aspect edge.
     * This can take one of the following values:
     * <ul>
     * <li> {@link RuleAspect#REMARK}
     * <li> A rule role, possibly with quantifier name, viz. one of
     *      {@link RuleAspect#EMBARGO}, 
     *      {@link RuleAspect#CREATOR}, 
     *      {@link RuleAspect#ERASER}, 
     *      {@link RuleAspect#READER} or
     *      {@link RuleAspect#CNEW}
     * <li> {@link NestingAspect#NESTED} (for "in" and "at" edges)
     * <li> {@link TypeAspect#ABS}
     * <li> {@link AttributeAspect#ARGUMENT}
     * <li> A value satisfying {@link AttributeAspect#isDataValue(AspectValue)} with operator content
     * </ul>
     */
    public AspectValue getType() {
        return this.declaredType;
    }

    /** Returns the inner text of this label, i.e., the label text without preceding aspects. */
    public String getInnerText() {
        return this.label.getInnerText();
    }

    /**
     * Returns the label that should be put on this
     * edge in the plain graph view.
     */
    public DefaultLabel getPlainLabel() {
        return DefaultLabel.createLabel(this.label.toString());
    }

    /** 
     * Returns the type label that this aspect edge gives rise to, if any.
     * @return a type label generated from the aspects on this edge, or {@code null}
     * if the edge does not give rise to a type label.
     */
    public TypeLabel createTypeLabel() {
        TypeLabel result = null;
        if (getType() == null && !TypeAspect.PATH.equals(this.labelMode)) {
            if (TypeAspect.EMPTY.equals(this.labelMode)) {
                result = TypeLabel.createLabel(this.label.getInnerText());
            } else {
                result = TypeLabel.createTypedLabel(this.label.getInnerText());
            }
        }
        return result;
    }

    /** 
     * Returns the rule label that this aspect edge gives rise to, if any.
     * @return a type label generated from the aspects on this edge, or {@code null}
     * if the edge does not give rise to a type label.
     */
    public RuleLabel createRuleLabel(AlgebraRegister register)
        throws FormatException {
        RuleLabel result = null;
        if (getType() == null || hasRole()) {
            if (TypeAspect.EMPTY.equals(this.labelMode)) {
                result = new RuleLabel(this.label.getInnerText());
            } else {
                result =
                    new RuleLabel(RegExpr.parse(this.label.getInnerText()));
            }
        } else if (isArgument()) {
            return new RuleLabel(getArgument());
        } else if (isOperator()) {
            return new RuleLabel(getOperator().getOperation(register));
        }
        return result;
    }

    /** TODO Temporary instance variable; eventually this should be 
     * replaced by #label().
     */
    private AspectLabel label;
    /** The declared type of the aspect edge. */
    private AspectValue declaredType;
    /** The declared or inferred type of the aspect edge. */
    private AspectValue inferredType;
    /** The parser mode of the label (either TypeAspect#PATH or TypeAspect#EMPTY). */
    private AspectValue labelMode;
    /** 
     * Flag indicating we have interpreted a declared quantifier aspect
     * as a substitute for a named reader aspect
     */
    private boolean quantifierAsReader;
    /** Argument number, if this is an argument edge. */
    private int argumentNr = -1;
    /** Algebraic operator, if this is an operator edge. */
    private Operator operator = null;
}
