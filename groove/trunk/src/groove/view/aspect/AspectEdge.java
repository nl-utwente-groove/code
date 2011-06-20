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

import static groove.graph.GraphRole.RULE;
import static groove.view.aspect.AspectKind.ABSTRACT;
import static groove.view.aspect.AspectKind.ARGUMENT;
import static groove.view.aspect.AspectKind.CONNECT;
import static groove.view.aspect.AspectKind.CREATOR;
import static groove.view.aspect.AspectKind.EMBARGO;
import static groove.view.aspect.AspectKind.ERASER;
import static groove.view.aspect.AspectKind.LET;
import static groove.view.aspect.AspectKind.LITERAL;
import static groove.view.aspect.AspectKind.NESTED;
import static groove.view.aspect.AspectKind.NONE;
import static groove.view.aspect.AspectKind.PATH;
import static groove.view.aspect.AspectKind.TEST;
import static groove.view.aspect.AspectKind.READER;
import static groove.view.aspect.AspectKind.REMARK;
import static groove.view.aspect.AspectKind.SUBTYPE;
import groove.algebra.Operator;
import groove.graph.AbstractEdge;
import groove.graph.DefaultLabel;
import groove.graph.EdgeRole;
import groove.graph.GraphRole;
import groove.graph.Label;
import groove.graph.Multiplicity;
import groove.graph.TypeLabel;
import groove.rel.RegExpr;
import groove.trans.RuleLabel;
import groove.util.Duo;
import groove.util.ExprParser;
import groove.util.Fixable;
import groove.view.FormatError;
import groove.view.FormatException;
import groove.view.aspect.AspectKind.NestedValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Edge enriched with aspect data. Aspect edge labels are interpreted as
 * {@link DefaultLabel}s.
 * @author Arend Rensink
 * @version $Revision$
 */
public class AspectEdge extends AbstractEdge<AspectNode,AspectLabel> implements
        AspectElement, Fixable {
    /**
     * Constructs a new edge.
     * @param source the source node for this edge
     * @param label the label for this edge
     * @param target the target node for this edge
     */
    public AspectEdge(AspectNode source, AspectLabel label, AspectNode target) {
        super(source, label, target);
        assert label.isFixed();
        if (!label.hasErrors() && label.isNodeOnly()) {
            if (label.getNodeOnlyAspect() == null) {
                this.errors.add(new FormatError("Empty edge label not allowed",
                    this));
            } else {
                this.errors.add(new FormatError(
                    "Aspect %s not allowed in edge label",
                    label.getNodeOnlyAspect(), this));
            }
        }
        for (FormatError error : label().getErrors()) {
            this.errors.add(error.extend(this));
        }
        this.graphRole = label.getGraphRole();
    }

    /** Returns the graph role set for this aspect edge. */
    public GraphRole getGraphRole() {
        return this.graphRole;
    }

    @Override
    public void setFixed() {
        if (!isFixed()) {
            this.fixed = true;
            if (!hasErrors()) {
                setAspectsFixed();
            }
        }
    }

    @Override
    public EdgeRole getRole() {
        if (this.isPredicate()) {
            // We just want the edge role to be non-binary...
            return EdgeRole.NODE_TYPE;
        } else {
            return super.getRole();
        }
    }

    /**
     * Fixes the aspects, by first setting the declared label aspects,
     * then inferring aspects from the end nodes.
     * Should only be called if the edge has no errors otherwise.
     */
    private void setAspectsFixed() {
        try {
            setAspects(label());
            inferAspects();
            checkAspects();
            if (this.graphRole == RULE) {
                this.ruleLabel = createRuleLabel();
                this.typeLabel = null;
            } else {
                this.ruleLabel = null;
                this.typeLabel = createTypeLabel();
            }
            target().inferInAspect(this);
            source().inferOutAspect(this);
            if (this.graphRole == RULE && !getKind().isMeta()) {
                checkRegExprs();
            }
        } catch (FormatException exc) {
            for (FormatError error : exc.getErrors()) {
                this.errors.add(error.extend(this));
            }
        }
    }

    @Override
    public boolean hasErrors() {
        return !getErrors().isEmpty();
    }

    @Override
    public List<FormatError> getErrors() {
        setFixed();
        return this.errors;
    }

    /** Adds a format error to the errors in this edge. */
    public void addError(FormatError error) {
        testFixed(false);
        this.errors.add(error.extend(this));
    }

    /**
     * Checks for the presence and consistency of the
     * type and attribute aspects.
     */
    private void checkAspects() throws FormatException {
        if (this.graphRole == RULE) {
            if (getKind() == ABSTRACT || getKind() == SUBTYPE) {
                throw new FormatException(
                    "Edge aspect %s not allowed in rules", getAspect(), this);
            } else if (!hasAspect()) {
                setAspect(AspectKind.READER.getAspect());
            }
            if (isAssign() && getKind() != READER && getKind() != CREATOR) {
                throw new FormatException("Conflicting aspects %s and %s",
                    getAttrAspect(), getAspect());
            }
            if (hasAttrAspect() && getKind() != READER && getKind() != EMBARGO) {
                throw new FormatException("Conflicting aspects %s and %s",
                    getAttrAspect(), getAspect());
            }
        } else if (getKind().isRole()) {
            throw new FormatException("Edge aspect %s only allowed in rules",
                getAspect(), this);
        } else if (!hasAspect()) {
            setAspect(AspectKind.NONE.getAspect());
        }
        if (!hasAttrAspect()) {
            setAttrAspect(AspectKind.NONE.getAspect());
        }
        if (!hasLabelMode()) {
            setLabelMode(AspectKind.NONE.getAspect());
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
            ruleLabel == null || ruleLabel.isAtom() || ruleLabel.isSharp()
                || ruleLabel.isWildcard();
        if (!simple && ruleLabel.isMatchable()) {
            AspectKind kind = getKind();
            assert kind.isRole();
            if (kind.inRHS() && !kind.inLHS() && !ruleLabel.isEmpty()) {
                throw new FormatException(
                    "Regular expression label %s not allowed in creators",
                    ruleLabel, this);
            } else if (kind.inLHS() && !kind.inRHS()) {
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
                "Aspect edge %s should %sbe fixed", this, fixed ? "" : "not "));
        }
    }

    /**
     * Sets the (declared) aspects for this edge from the edge label.
     * @throws FormatException if the aspects are inconsistent
     */
    private void setAspects(AspectLabel label) throws FormatException {
        assert !label.isNodeOnly();
        for (Aspect aspect : label.getAspects()) {
            declareAspect(aspect);
        }
    }

    /** 
     * Infers aspects from the end nodes of this edge.
     * Inference exists for rule roles, remarks and nesting. 
     */
    private void inferAspects() throws FormatException {
        AspectKind sourceKind = this.source.getKind();
        AspectKind targetKind = this.target.getKind();
        if (sourceKind == REMARK || targetKind == REMARK) {
            setAspect(REMARK.getAspect());
        } else if (sourceKind.isQuantifier() || targetKind.isQuantifier()) {
            if (getKind() != NESTED && getKind() != REMARK) {
                setAspect(NESTED.getAspect().newInstance(getInnerText()));
            }
        } else if (getKind() != REMARK && getKind() != SUBTYPE
            && getKind() != CONNECT) {
            AspectKind sourceRole = null;
            AspectKind targetRole = null;
            if (sourceKind.isRole() && sourceKind != READER) {
                sourceRole = sourceKind;
            }
            if (targetKind.isRole() && targetKind != READER) {
                targetRole = targetKind;
            }
            Aspect inferredAspect;
            if (sourceRole == null) {
                inferredAspect = target().getAspect();
            } else if (targetRole == null) {
                inferredAspect = source().getAspect();
            } else if (sourceRole == ERASER && targetRole == EMBARGO) {
                inferredAspect = target().getAspect();
            } else if (sourceRole == EMBARGO && targetRole == ERASER) {
                inferredAspect = source().getAspect();
            } else if (sourceRole == targetRole) {
                inferredAspect = source().getAspect();
            } else {
                throw new FormatException("Conflicting aspects %s and %s",
                    source().getAspect(), target().getAspect());
            }
            if (inferredAspect != null
                && inferredAspect.getKind().isRole()
                && inferredAspect.getKind() != READER
                && !(inferredAspect.getKind() == ERASER && getKind() == EMBARGO)) {
                setAspect(inferredAspect);
            }
        }
    }

    /**
     * Adds a declared or inferred aspect value to this edge.
     * @param value the aspect value
     * @throws FormatException if the added value conflicts with a previously
     * declared or inferred one
     */
    private void declareAspect(Aspect value) throws FormatException {
        assert value.isForEdge(this.graphRole);
        AspectKind kind = value.getKind();
        if (kind == PATH || kind == LITERAL) {
            setLabelMode(value);
        } else if (kind.isAttrKind()) {
            setAttrAspect(value);
        } else {
            setAspect(value);
        }
    }

    /** Tests if this edge has the same aspect type as another aspect element. */
    public boolean equalsAspects(AspectElement other) {
        assert isFixed() && other.isFixed();
        boolean result =
            getAspect() == null ? other.getAspect() == null
                    : getAspect().equals(other.getAspect());
        if (result) {
            result =
                getAttrAspect() == null ? other.getAttrAspect() == null
                        : getAttrAspect().equals(other.getAttrAspect());
        }
        if (result && other instanceof AspectEdge) {
            Aspect otherMode = ((AspectEdge) other).getLabelMode();
            result = getLabelMode().equals(otherMode);
        }
        return result;
    }

    /** Returns the inner text of this label, i.e., the label text without preceding aspects. */
    public String getInnerText() {
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
     * This is either the type label, or the rule label, or (if neither are defined)
     * a default edge constructed from the inner text of the aspect label.
     */
    public Label getDisplayLabel() {
        Label result = null;
        if (this.graphRole == RULE) {
            result = getRuleLabel();
        } else {
            result = getTypeLabel();
        }
        if (result == null) {
            String text;
            if (getKind() == NESTED) {
                text = getAspect().getContentString();
            } else if (isPredicate()) {
                text = getPredicate().getDisplayString();
            } else if (isAssign()) {
                text = getAssign().getDisplayString();
            } else if (getKind() == CONNECT) {
                text = "+";
            } else {
                text = getInnerText();
            }
            result = DefaultLabel.createLabel(text);
        }
        return result;
    }

    /** Returns the (possibly {@code null}) type label of this edge. */
    public RuleLabel getRuleLabel() {
        testFixed(true);
        return this.ruleLabel;
    }

    /** 
     * Returns the rule label that this aspect edge gives rise to, if any.
     * @return a type label generated from the aspects on this edge, or {@code null}
     * if the edge does not give rise to a type label.
     */
    private RuleLabel createRuleLabel() throws FormatException {
        RuleLabel result;
        if (getKind().isMeta() || isAssign() || isPredicate()) {
            result = null;
        } else if (getAttrKind() == ARGUMENT) {
            result = new RuleLabel(getArgument());
        } else if (getAttrKind().isData()) {
            result = new RuleLabel(getOperator());
        } else {
            assert getKind().isRole();
            if (getLabelKind() == LITERAL) {
                result = new RuleLabel(getInnerText());
            } else {
                result = new RuleLabel(parse(getInnerText()));
            }
        }
        return result;
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
        if (getKind() == REMARK || isAssign() || isPredicate()) {
            result = null;
        } else if (!getKind().isRole() && getLabelKind() != PATH) {
            if (getLabelKind() == LITERAL) {
                result = TypeLabel.createBinaryLabel(getInnerText());
            } else {
                result = TypeLabel.createLabel(getInnerText());
            }
        } else {
            throw new FormatException(
                "Edge label '%s' is only allowed in rules", label(), this);
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

    /** Setter for the aspect type. */
    private void setAspect(Aspect aspect) throws FormatException {
        AspectKind kind = aspect.getKind();
        assert !kind.isAttrKind() && kind != AspectKind.PATH
            && kind != AspectKind.LITERAL;
        // process the content, if any
        if (kind.isQuantifier()) {
            // backward compatibility to take care of edges such as
            // exists=q:del:a rather than del=q:a or 
            // exists=q:a rather than use=q:a
            if (!aspect.hasContent()) {
                throw new FormatException(
                    "Unnamed quantifier %s not allowed on edge", aspect, this);
            } else if (this.levelName != null) {
                throw new FormatException(
                    "Duplicate quantifier levels %s and %s", this.levelName,
                    aspect.getContent(), this);
            } else {
                this.levelName = (String) aspect.getContent();
            }
        } else if (kind.isRole() && aspect.hasContent()) {
            if (this.levelName != null) {
                throw new FormatException(
                    "Duplicate quantifier levels %s and %s", this.levelName,
                    aspect.getContent(), this);
            } else {
                this.levelName = (String) aspect.getContent();
            }
        }
        // actually set the type, if the passed-in value was not a quantifier
        // (which we use only for its level name)
        if (kind == AspectKind.MULT_IN) {
            this.inMult = (Multiplicity) aspect.getContent();
        } else if (kind == AspectKind.MULT_OUT) {
            this.outMult = (Multiplicity) aspect.getContent();
        } else if (kind == AspectKind.COMPOSITE) {
            this.composite = true;
        } else if (!kind.isQuantifier()) {
            if (this.aspect == null) {
                this.aspect = aspect;
            } else if (!this.aspect.equals(aspect)) {
                throw new FormatException("Conflicting aspects %s and %s",
                    this.aspect, aspect, this);
            }
        }
    }

    @Override
    public Aspect getAspect() {
        return this.aspect;
    }

    private boolean hasAspect() {
        return this.aspect != null;
    }

    /** 
     * Returns the determining aspect kind of this edge.
     * This is one of {@link AspectKind#REMARK}, a role, {@link AspectKind#NESTED},
     * {@link AspectKind#ABSTRACT} or {@link AspectKind#SUBTYPE}.
     */
    @Override
    public AspectKind getKind() {
        return hasAspect() ? getAspect().getKind() : NONE;
    }

    /** Retrieves the optional quantification level name of this edge. */
    public String getLevelName() {
        return this.levelName;
    }

    /** Indicates if this edge is a "nested:at". */
    public boolean isNestedAt() {
        return hasAspect() && getKind() == NESTED
            && getAspect().getContent() == NestedValue.AT;
    }

    /** Indicates if this edge is a "nested:in". */
    public boolean isNestedIn() {
        return hasAspect() && getKind() == NESTED
            && getAspect().getContent() == NestedValue.IN;
    }

    /** Indicates if this edge is a "nested:count". */
    public boolean isNestedCount() {
        return hasAspect() && getKind() == NESTED
            && getAspect().getContent() == NestedValue.COUNT;
    }

    /** Indicates that this is a creator element with a merge label ("="). */
    public boolean isMerger() {
        testFixed(true);
        return getKind().inRHS() && !getKind().inLHS()
            && getRuleLabel().isEmpty();
    }

    /** Setter for the aspect type. */
    private void setAttrAspect(Aspect type) {
        AspectKind kind = type.getKind();
        assert kind == AspectKind.NONE || kind.isAttrKind();
        assert this.attr == null;
        this.attr = type;
        if (type.getKind() == ARGUMENT) {
            this.attr = type;
            this.argumentNr = (Integer) this.attr.getContent();
        } else if (kind.isTypedData()) {
            this.attr = type;
            this.operator = (Operator) type.getContent();
        }
    }

    @Override
    public Aspect getAttrAspect() {
        return this.attr;
    }

    @Override
    public boolean hasAttrAspect() {
        return this.attr != null && this.attr.getKind() != NONE;
    }

    @Override
    public AspectKind getAttrKind() {
        return hasAttrAspect() ? getAttrAspect().getKind() : NONE;
    }

    /** Indicates if this is an argument edge. */
    public boolean isArgument() {
        return this.argumentNr >= 0;
    }

    /** Indicates if this is a let-edge. */
    public boolean isAssign() {
        return this.hasAttrAspect() && this.getAttrKind() == LET;
    }

    /** Convenience method to retrieve the attribute aspect content as an assignment. */
    public Assignment getAssign() {
        assert isAssign();
        return (Assignment) getAttrAspect().getContent();
    }

    /** Indicates if this is an attribute predicate edge. */
    public boolean isPredicate() {
        return this.hasAttrAspect() && this.getAttrKind() == TEST;
    }

    /** Convenience method to retrieve the attribute aspect content as a predicate. */
    public Expression getPredicate() {
        assert isPredicate();
        return (Expression) getAttrAspect().getContent();
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

    /** Indicates if this is a composite type edge. */
    public boolean isComposite() {
        return this.composite;
    }

    /** Returns the incoming multiplicity of this (type) edge, if any. */
    public Multiplicity getInMult() {
        return this.inMult;
    }

    /** Returns the outgoing multiplicity of this (type) edge, if any. */
    public Duo<Integer> getOutMult() {
        return this.outMult;
    }

    /** Setter for the label mode. */
    private void setLabelMode(Aspect type) throws FormatException {
        AspectKind kind = type.getKind();
        assert kind == NONE || kind == PATH || kind == LITERAL;
        if (this.labelMode == null) {
            this.labelMode = type;
        } else {
            throw new FormatException("Conflicting edge aspects %s and %s",
                this.labelMode, type, this);
            // actually this should not happen since both of these
            // aspects are specified to be the last in a label
        }
    }

    /**
     * Retrieves the label mode of this edge.
     * This is either {@link AspectKind#NONE}, {@link AspectKind#PATH} or {@link AspectKind#LITERAL}.
     */
    public Aspect getLabelMode() {
        return this.labelMode;
    }

    /** Indicates if this edge has a label mode. */
    public boolean hasLabelMode() {
        return getLabelMode() != null;
    }

    /**
     * Retrieves the label aspect kind of this edge, if any.
     * This is either {@link AspectKind#PATH} or {@link AspectKind#LITERAL}.
     */
    public AspectKind getLabelKind() {
        return hasLabelMode() ? getLabelMode().getKind() : null;
    }

    /** The graph role for this element. */
    private final GraphRole graphRole;
    /** The (possibly {@code null}) type label modelled by this edge. */
    private TypeLabel typeLabel;
    /** The (possibly {@code null}) rule label modelled by this edge. */
    private RuleLabel ruleLabel;
    /** The declared or inferred type of the aspect edge. */
    private Aspect aspect;
    /** An optional attribute-related aspect. */
    private Aspect attr;
    /** The parser mode of the label (either TypeAspect#PATH or TypeAspect#EMPTY). */
    private Aspect labelMode;
    /** The quantifier level name, if any. */
    private String levelName;
    /** Argument number, if this is an argument edge. */
    private int argumentNr = -1;
    /** Algebraic operator, if this is an operator edge. */
    private Operator operator = null;
    /** The incoming multiplicity of this (type) edge.
     * {@code null} if there is no incoming multiplicity declared.
     */
    private Multiplicity inMult;
    /** The outgoing multiplicity of this (type) edge.
     * {@code null} if there is no outgoing multiplicity declared.
     */
    private Multiplicity outMult;
    /** Flag indicating that this is a composite type edge. */
    private boolean composite;
    /** Flag indicating if the edge is fixed. */
    private boolean fixed;
    /** List of syntax errors in this edge. */
    private final List<FormatError> errors = new ArrayList<FormatError>();
}
