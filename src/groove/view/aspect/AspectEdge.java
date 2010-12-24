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

import groove.algebra.Algebras;
import groove.algebra.Operator;
import groove.algebra.UnknownSymbolException;
import groove.graph.AbstractEdge;
import groove.graph.DefaultLabel;
import groove.graph.Label;
import groove.graph.TypeLabel;
import groove.rel.RegExpr;
import groove.trans.RuleLabel;
import groove.util.ExprParser;
import groove.util.Fixable;
import groove.util.Groove;
import groove.view.FormatException;

/**
 * Edge enriched with aspect data. Aspect edge labels are interpreted as
 * {@link DefaultLabel}s.
 * @author Arend Rensink
 * @version $Revision$
 */
public class AspectEdge extends AbstractEdge<AspectNode,AspectLabel,AspectNode>
        implements AspectElement, Fixable {
    /**
     * Constructs a new edge.
     * @param source the source node for this edge
     * @param label the label for this edge
     * @param target the target node for this edge
     * @param graphRole the role of the graph in which this edge occurs
     */
    AspectEdge(AspectNode source, AspectLabel label, AspectNode target,
            String graphRole) {
        super(source, label, target);
        this.graphRole = graphRole;
    }

    @Override
    public void setFixed() throws FormatException {
        if (!isFixed()) {
            try {
                setAspects(label());
                inferAspects();
                checkRole();
                if (isForRule()) {
                    this.ruleLabel = createRuleLabel();
                    this.typeLabel = null;
                } else {
                    this.ruleLabel = null;
                    this.typeLabel = createTypeLabel();
                }
                target().inferInAspect(this);
                source().inferOutAspect(this);
                if (isForRule() && !isMeta()) {
                    checkRegExprs();
                }
            } finally {
                // whatever happened, this edge is now fixed
                this.fixed = true;
            }
        }
    }

    /**
     * Checks for the presence and consistency of a rule role aspect.
     */
    private void checkRole() throws FormatException {
        if (isForRule()) {
            if (isAbstract() || isSubtype()) {
                throw new FormatException(
                    "Edge aspect %s not allowed in rules", getRole(), this);
            } else if (!hasRole() && !AspectNode.conflictsWithRole(getType())) {
                setRole(RuleAspect.READER);
            }
            if (hasRole() && hasType() && AspectNode.isQuantifier(getType())) {
                // backward compatibility to take care of edges such as
                // exists=q:del:a rather than del=q:a or 
                // exists=q:a rather than use=q:a
                assert getType().getContent() != null;
                // this value is a named quantifier; see if we can add the
                // name to an already declared rule role
                if (getRole().getContent() != null) {
                    throw new FormatException(
                        "Duplicate quantifier names in %s and %s", getRole(),
                        getType(), this);
                } else {
                    setRole(getRole().newValue(getType().getContent()));
                    setType(null);
                }
            } else if (hasRole() && AspectNode.conflictsWithRole(getType())) {
                throw new FormatException("Conflicting edge aspects %s and %s",
                    getRole(), getType(), this);
            }
        } else if (hasRole()) {
            throw new FormatException("Edge aspect %s only allowed in rules",
                getRole(), this);
        }
    }

    /**
     * Tests if regular expression usage does not go beyond what is allowed.
     * In particular, regular expressions cannot be erasers or creators.
     * @throws FormatException if a wrong usage is detected 
     */
    private void checkRegExprs() throws FormatException {
        // this is called after the rule label has been computed
        RuleLabel ruleLabel = this.ruleLabel;
        boolean simple =
            ruleLabel.isAtom() || ruleLabel.isSharp() || ruleLabel.isWildcard();
        if (!simple && ruleLabel.isMatchable()) {
            if (RuleAspect.isCreator(this) && !ruleLabel.isEmpty()) {
                throw new FormatException(
                    "Regular expression label %s not allowed in creators",
                    ruleLabel, this);
            } else if (RuleAspect.isEraser(this)) {
                throw new FormatException(
                    "Regular expression label %s not allowed in erasers",
                    ruleLabel, this);
            }
        }
    }

    @Override
    public boolean isFixed() {
        return this.fixed;
    }

    @Override
    public void testFixed(boolean fixed) {
        if (fixed != isFixed()) {
            throw new IllegalStateException(String.format(
                "Incorrect fixation of %s", this));
        }
    }

    /**
     * Sets the (declared) aspects for this edge from the edge label.
     * @throws FormatException if the aspects are inconsistent
     */
    private void setAspects(AspectLabel label) throws FormatException {
        assert !label.isNodeOnly();
        for (AspectValue aspect : label.getAspects()) {
            declareAspect(aspect);
        }
    }

    /** 
     * Infers aspects from the end nodes of this edge.
     * Inference exists for rule roles, remarks and nesting. 
     */
    private void inferAspects() throws FormatException {
        if (source().isRemark() || target().isRemark()) {
            addAspectType(RuleAspect.REMARK, false);
        } else if (source().isQuantifier() || target().isQuantifier()) {
            addAspectType(NestingAspect.NESTED, false);
        } else {
            if (source().hasRole()
                && !RuleAspect.READER.equals(source().getRole())) {
                addAspectRole(source().getRole(), false);
            }
            if (target().hasRole()
                && !RuleAspect.READER.equals(target().getRole())) {
                addAspectRole(target().getRole(), false);
            }
        }
    }

    /**
     * Adds a declared or inferred aspect value to this edge.
     * @param value the aspect value
     * @throws FormatException if the added value conflicts with a previously
     * declared or inferred one
     */
    private void declareAspect(AspectValue value) throws FormatException {
        assert value.isEdgeValue();
        if (value.getAspect() == TypeAspect.getInstance()
            && !TypeAspect.SUB.equals(value)) {
            assert TypeAspect.PATH.equals(value)
                || TypeAspect.EMPTY.equals(value) : String.format(
                "Unexpected aspect %s", value);
            if (this.labelMode == null) {
                this.labelMode = value;
            } else {
                throw new FormatException("Conflicting edge aspects %s and %s",
                    this.labelMode, value, this);
                // actually this should not happen since both of these
                // aspects are specified to be the last in a label
            }
        } else if (AspectNode.isRole(value)) {
            addAspectRole(value, true);
        } else {
            addAspectType(value, true);
        }
    }

    /**
     * Sets the declared or inferred aspect type for this edge.
     * @param value the aspect value; must determine a type
     * @param declared indicates if the value is declared or inferred
     * @throws FormatException if the added value conflicts with a previously
     * declared or inferred one
     */
    private void addAspectRole(AspectValue value, boolean declared)
        throws FormatException {
        if (!declared && AspectNode.conflictsWithRole(getType())) {
            // do nothing
        } else if (getRole() == null) {
            setRole(value);
        } else if (RuleAspect.EMBARGO.equals(getRole())
            && RuleAspect.ERASER.equals(value)) {
            // do nothing
        } else if (RuleAspect.EMBARGO.equals(value)
            && RuleAspect.ERASER.equals(getRole())) {
            setRole(value);
        } else if (declared || !value.equals(getRole())) {
            throw new FormatException("Conflicting edge aspects %s and %s",
                getRole(), value, this);
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
        if (!hasType()) {
            inferredType = value;
            String innerText = getInnerText();
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
                        Algebras.getOperator(value.getName(), innerText);
                } catch (UnknownSymbolException e) {
                    throw new FormatException(
                        "Label '%s' is not an operator of type %s", innerText,
                        value.getName(), this);
                }
            }
        } else if (!declared && value.equals(getType())) {
            // the existing type is taken to be at least as precise
            inferredType = getType();
        }
        if (inferredType == null) {
            throw new FormatException("Conflicting edge aspects %s and %s",
                getType(), value, this);
        }
        setType(inferredType);
    }

    /** Indicates if this edge represents a remark. */
    public boolean isRemark() {
        return RuleAspect.REMARK.equals(getType());
    }

    /** Indicates if this edge represents a remark. */
    public boolean isAbstract() {
        return TypeAspect.ABS.equals(getType());
    }

    /** Indicates if this edge represents a remark. */
    public boolean isSubtype() {
        return TypeAspect.SUB.equals(getType());
    }

    /** Indicates if this edge has a declared or inferred role in a rule. */
    public boolean hasRole() {
        return getRole() != null;
    }

    public AspectValue getRole() {
        return this.role;
    }

    /** Setter for the aspect role. */
    private void setRole(AspectValue role) {
        assert AspectNode.isRole(role);
        this.role = role;
    }

    /** Indicates if this edge is a "nested:at". */
    public boolean isNestedAt() {
        return hasType()
            && getType().getAspect() == NestingAspect.getInstance()
            && NestingAspect.AT_LABEL.equals(getInnerText());
    }

    /** Indicates if this edge is a "nested:in". */
    public boolean isNestedIn() {
        return hasType()
            && getType().getAspect() == NestingAspect.getInstance()
            && NestingAspect.IN_LABEL.equals(getInnerText());
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

    @Override
    public boolean hasType() {
        return getType() != null;
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
     * <li> A value satisfying {@link AttributeAspect#isDataValue(AspectValue)}
     * </ul>
     */
    @Override
    public AspectValue getType() {
        return this.type;
    }

    /** Setter for the aspect type. */
    private void setType(AspectValue type) {
        assert type == null || AspectNode.isType(type);
        this.type = type;
    }

    /** An aspect node is a meta-element if it is a nesting edge or remark. */
    @Override
    public boolean isMeta() {
        return isNestedAt() || isNestedIn() || isRemark();
    }

    /** Indicates that this is a creator element with a merge label ("="). */
    public boolean isMerger() {
        testFixed(true);
        return hasRole() && getRole() == RuleAspect.CREATOR
            && getRuleLabel().isEmpty();
    }

    /** Tests if this edge has the same aspect type as another aspect element. */
    public boolean equalsAspects(AspectElement other) {
        boolean result =
            hasType() ? getType().equals(other.getType()) : !other.hasType();
        if (result) {
            result =
                hasRole() ? getRole().equals(other.getRole())
                        : !other.hasRole();
        }
        if (result && other instanceof AspectEdge) {
            AspectValue otherMode = ((AspectEdge) other).labelMode;
            result =
                this.labelMode == null ? otherMode == null
                        : this.labelMode.equals(otherMode);
        }
        return result;
    }

    /** 
     * Returns the label parsing mode of this edge.
     * @return {@link TypeAspect#EMPTY}, {@link TypeAspect#PATH},
     * or {@code null}
     */
    AspectValue getLabelMode() {
        return this.labelMode;
    }

    /** Returns the inner text of this label, i.e., the label text without preceding aspects. */
    String getInnerText() {
        return label().getInnerText();
    }

    /**
     * Returns the label that should be put on this
     * edge in the plain graph view.
     */
    public DefaultLabel getPlainLabel() {
        return DefaultLabel.createLabel(label().toString());
    }

    /**
     * Returns the label of this edge as it should be displayed. 
     * This is either the type edge, or the rule edge, or (if neither are defined)
     * a default edge constructed from the inner text of the aspect label.
     */
    public Label getDisplayLabel() {
        Label result = null;
        if (isForRule()) {
            result = getRuleLabel();
        } else {
            result = getTypeLabel();
        }
        if (result == null) {
            result = DefaultLabel.createLabel(getInnerText());
        }
        return result;
    }

    /** Returns the (possibly {@code null}) type label of this edge. */
    public RuleLabel getRuleLabel() {
        testFixed(true);
        return this.ruleLabel;
    }

    /** Returns the (possibly {@code null}) rule label of this edge. */
    public TypeLabel getTypeLabel() {
        testFixed(true);
        return this.typeLabel;
    }

    /** 
     * Returns the type label that this aspect edge gives rise to, if any.
     * @return a type label generated from the aspects on this edge, or {@code null}
     * if the edge does not give rise to a type label.
     */
    private TypeLabel createTypeLabel() throws FormatException {
        TypeLabel result;
        if (isRemark()) {
            result = null;
        } else if (!hasRole() && !TypeAspect.PATH.equals(this.labelMode)) {
            if (TypeAspect.EMPTY.equals(this.labelMode)) {
                result = TypeLabel.createLabel(getInnerText());
            } else {
                result = TypeLabel.createTypedLabel(getInnerText());
            }
        } else {
            throw new FormatException(
                "Edge label '%s' is only allowed in rules", label(), this);
        }
        return result;
    }

    /** 
     * Returns the rule label that this aspect edge gives rise to, if any.
     * @return a type label generated from the aspects on this edge, or {@code null}
     * if the edge does not give rise to a type label.
     */
    private RuleLabel createRuleLabel() throws FormatException {
        RuleLabel result;
        if (isArgument()) {
            result = new RuleLabel(getArgument());
        } else if (isOperator()) {
            result = new RuleLabel(getOperator());
        } else if (hasRole()) {
            if (TypeAspect.EMPTY.equals(this.labelMode)) {
                result = new RuleLabel(getInnerText());
            } else {
                result = new RuleLabel(parse(getInnerText()));
            }
        } else {
            assert isMeta();
            result = null;
        }
        return result;
    }

    /** 
     * Parses a given string as a regular expression,
     * taking potential curly braces into account.
     */
    private RegExpr parse(String text) throws FormatException {
        if (text.startsWith(RegExpr.NEG_OPERATOR)) {
            RegExpr innerExpr =
                parse(text.substring(RegExpr.NEG_OPERATOR.length()));
            return new RegExpr.Neg(innerExpr);
        } else {
            if (text.startsWith("" + ExprParser.LCURLY)) {
                text =
                    ExprParser.toTrimmed(text, ExprParser.LCURLY,
                        ExprParser.RCURLY);
            }
            return RegExpr.parse(text);
        }
    }

    /** Indicates if this is supposed to be a rule element. */
    private boolean isForRule() {
        return Groove.RULE_ROLE.equals(this.graphRole);
    }

    /** The graph role for this element. */
    private final String graphRole;
    /** Flag indicating if this edge has been fully computed. */
    private boolean fixed;
    /** The (possibly {@code null}) type label modelled by this edge. */
    private TypeLabel typeLabel;
    /** The (possibly {@code null}) rule label modelled by this edge. */
    private RuleLabel ruleLabel;
    /** The declared or inferred type of the aspect edge. */
    private AspectValue type;
    /** The declared or inferred role of the aspect edge. */
    private AspectValue role;
    /** The parser mode of the label (either TypeAspect#PATH or TypeAspect#EMPTY). */
    private AspectValue labelMode;
    /** Argument number, if this is an argument edge. */
    private int argumentNr = -1;
    /** Algebraic operator, if this is an operator edge. */
    private Operator operator = null;
}
