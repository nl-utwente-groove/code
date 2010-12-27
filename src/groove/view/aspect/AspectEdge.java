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

import static groove.view.aspect.AspectKind.ABSTRACT;
import static groove.view.aspect.AspectKind.ARGUMENT;
import static groove.view.aspect.AspectKind.EMBARGO;
import static groove.view.aspect.AspectKind.ERASER;
import static groove.view.aspect.AspectKind.LITERAL;
import static groove.view.aspect.AspectKind.NESTED;
import static groove.view.aspect.AspectKind.NONE;
import static groove.view.aspect.AspectKind.PATH;
import static groove.view.aspect.AspectKind.READER;
import static groove.view.aspect.AspectKind.REMARK;
import static groove.view.aspect.AspectKind.SUBTYPE;
import groove.algebra.Algebras;
import groove.algebra.Operator;
import groove.algebra.UnknownSymbolException;
import groove.graph.AbstractEdge;
import groove.graph.DefaultLabel;
import groove.graph.GraphRole;
import groove.graph.Label;
import groove.graph.TypeLabel;
import groove.rel.RegExpr;
import groove.trans.RuleLabel;
import groove.util.ExprParser;
import groove.util.Fixable;
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
            GraphRole graphRole) {
        super(source, label, target);
        this.graphRole = graphRole;
    }

    @Override
    public void setFixed() throws FormatException {
        if (!isFixed()) {
            try {
                setAspects(label());
                inferAspects();
                checkAspects();
                if (this.graphRole == GraphRole.RULE) {
                    this.ruleLabel = createRuleLabel();
                    this.typeLabel = null;
                } else {
                    this.ruleLabel = null;
                    this.typeLabel = createTypeLabel();
                }
                target().inferInAspect(this);
                source().inferOutAspect(this);
                if (this.graphRole == GraphRole.RULE && !getKind().isMeta()) {
                    checkRegExprs();
                }
            } finally {
                // whatever happened, this edge is now fixed
                this.fixed = true;
            }
        }
    }

    /**
     * Checks for the presence and consistency of the
     * type and attribute aspects.
     */
    private void checkAspects() throws FormatException {
        if (this.graphRole == GraphRole.RULE) {
            if (getKind() == ABSTRACT || getKind() == SUBTYPE) {
                throw new FormatException(
                    "Edge aspect %s not allowed in rules", getAspect(), this);
            } else if (!hasAspect()) {
                setAspect(AspectKind.READER.getAspect());
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
            ruleLabel.isAtom() || ruleLabel.isSharp() || ruleLabel.isWildcard();
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
                "Incorrect fixation of %s", this));
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
            setAspect(NESTED.getAspect());
        } else {
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
        AspectKind kind = value.getKind();
        assert kind.isForEdge(this.graphRole);
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
        boolean result = getAspect().equals(other.getAspect());
        if (result) {
            result = getAttrAspect().equals(other.getAttrAspect());
        }
        if (result && other instanceof AspectEdge) {
            Aspect otherMode = ((AspectEdge) other).getLabelMode();
            result = getLabelMode().equals(otherMode);
        }
        return result;
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
        if (this.graphRole == GraphRole.RULE) {
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

    /** 
     * Returns the rule label that this aspect edge gives rise to, if any.
     * @return a type label generated from the aspects on this edge, or {@code null}
     * if the edge does not give rise to a type label.
     */
    private RuleLabel createRuleLabel() throws FormatException {
        RuleLabel result;
        if (getAttrKind() == ARGUMENT) {
            result = new RuleLabel(getArgument());
        } else if (getAttrKind().isData()) {
            result = new RuleLabel(getOperator());
        } else if (getKind().isRole()) {
            if (getLabelKind() == LITERAL) {
                result = new RuleLabel(getInnerText());
            } else {
                result = new RuleLabel(parse(getInnerText()));
            }
        } else {
            assert getKind().isMeta();
            result = null;
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
        if (getKind() == REMARK) {
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
        if (kind == NESTED
            && !AspectLabel.ALLOWED_LABELS.contains(getInnerText())) {
            throw new FormatException("Unknown label '%s' on nesting edge",
                getInnerText(), this);
        } else if (kind.isQuantifier()) {
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
        if (!kind.isQuantifier()) {
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
            && AspectLabel.AT_LABEL.equals(getInnerText());
    }

    /** Indicates if this edge is a "nested:in". */
    public boolean isNestedIn() {
        return hasAspect() && getKind() == NESTED
            && AspectLabel.IN_LABEL.equals(getInnerText());
    }

    /** Indicates that this is a creator element with a merge label ("="). */
    public boolean isMerger() {
        testFixed(true);
        return getKind().inRHS() && !getKind().inLHS()
            && getRuleLabel().isEmpty();
    }

    /** Setter for the aspect type. */
    private void setAttrAspect(Aspect type) throws FormatException {
        AspectKind kind = type.getKind();
        assert kind == AspectKind.NONE || kind.isAttrKind();
        assert this.attr == null;
        this.attr = type;
        if (type.getKind() == ARGUMENT) {
            this.attr = type.newInstance(getInnerText());
            this.argumentNr = (Integer) this.attr.getContent();
        } else if (kind.isTypedData()) {
            try {
                this.operator =
                    Algebras.getOperator(kind.getName(), getInnerText());
            } catch (UnknownSymbolException e) {
                throw new FormatException(
                    "Label '%s' is not an operator of type %s", getInnerText(),
                    kind, this);
            }
        }
    }

    @Override
    public Aspect getAttrAspect() {
        return this.attr;
    }

    @Override
    public boolean hasAttrAspect() {
        return this.attr != null;
    }

    @Override
    public AspectKind getAttrKind() {
        return hasAttrAspect() ? getAttrAspect().getKind() : null;
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
    /** Flag indicating if this edge has been fully computed. */
    private boolean fixed;
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
}
