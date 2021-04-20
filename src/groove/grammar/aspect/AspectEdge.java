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
package groove.grammar.aspect;

import static groove.grammar.aspect.AspectKind.ABSTRACT;
import static groove.grammar.aspect.AspectKind.ARGUMENT;
import static groove.grammar.aspect.AspectKind.CONNECT;
import static groove.grammar.aspect.AspectKind.DEFAULT;
import static groove.grammar.aspect.AspectKind.EMBARGO;
import static groove.grammar.aspect.AspectKind.ERASER;
import static groove.grammar.aspect.AspectKind.LET;
import static groove.grammar.aspect.AspectKind.LITERAL;
import static groove.grammar.aspect.AspectKind.NESTED;
import static groove.grammar.aspect.AspectKind.PARAM_ASK;
import static groove.grammar.aspect.AspectKind.PATH;
import static groove.grammar.aspect.AspectKind.READER;
import static groove.grammar.aspect.AspectKind.REMARK;
import static groove.grammar.aspect.AspectKind.SUBTYPE;
import static groove.grammar.aspect.AspectKind.TEST;
import static groove.graph.GraphRole.RULE;

import java.util.EnumSet;
import java.util.Set;

import groove.algebra.Operator;
import groove.algebra.Sort;
import groove.algebra.syntax.Assignment;
import groove.algebra.syntax.Expression;
import groove.automaton.RegExpr;
import groove.grammar.aspect.AspectKind.NestedValue;
import groove.grammar.rule.RuleLabel;
import groove.grammar.type.Multiplicity;
import groove.grammar.type.TypeLabel;
import groove.graph.AEdge;
import groove.graph.EdgeRole;
import groove.graph.GraphRole;
import groove.graph.Label;
import groove.graph.plain.PlainLabel;
import groove.gui.look.Values;
import groove.io.Util;
import groove.util.Exceptions;
import groove.util.Fixable;
import groove.util.line.Line;
import groove.util.line.Line.ColorType;
import groove.util.line.Line.Style;
import groove.util.parse.FormatError;
import groove.util.parse.FormatErrorSet;
import groove.util.parse.FormatException;
import groove.util.parse.StringHandler;

/**
 * Edge enriched with aspect data. Aspect edge labels are interpreted as
 * {@link PlainLabel}s.
 * @author Arend Rensink
 * @version $Revision$
 */
public class AspectEdge extends AEdge<AspectNode,AspectLabel> implements AspectElement, Fixable {
    /**
     * Constructs a new edge.
     * @param source the source node for this edge
     * @param label the label for this edge
     * @param target the target node for this edge
     */
    public AspectEdge(AspectNode source, AspectLabel label, AspectNode target, int number) {
        super(source, label, target, number);
        assert label.isFixed();
        if (!label.hasErrors() && label.isNodeOnly()) {
            if (label.getNodeOnlyAspect() == null) {
                this.errors.add("Empty edge label not allowed", this);
            } else {
                this.errors
                    .add("Aspect %s not allowed in edge label", label.getNodeOnlyAspect(), this);
            }
        }
        for (FormatError error : label().getErrors()) {
            this.errors.add(error.extend(this));
        }
        this.graphRole = label.getGraphRole();
    }

    /**
     * Constructs a new edge.
     * @param source the source node for this edge
     * @param label the label for this edge
     * @param target the target node for this edge
     */
    public AspectEdge(AspectNode source, AspectLabel label, AspectNode target) {
        this(source, label, target, 0);
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    /** Returns the graph role set for this aspect edge. */
    public GraphRole getGraphRole() {
        return this.graphRole;
    }

    @Override
    public boolean setFixed() {
        boolean result = !isFixed();
        if (result) {
            this.fixed = true;
            if (!hasErrors()) {
                setAspectsFixed();
            }
            setDefaultAttrAspect();
            setDefaultLabelMode();
        }
        return result;
    }

    @Override
    public EdgeRole getRole() {
        if (this.isPredicate() || isAssign()) {
            // We just want the edge role to be non-binary...
            return EdgeRole.FLAG;
        } else if (getGraphRole() == GraphRole.TYPE && getAttrKind().hasSort()) {
            return EdgeRole.FLAG;
        } else {
            Label label = getGraphRole() == RULE ? getRuleLabel() : getTypeLabel();
            return label == null ? EdgeRole.BINARY : label.getRole();
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
    public FormatErrorSet getErrors() {
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
                throw new FormatException("Edge aspect %s not allowed in rules", getAspect(), this);
            } else if (!hasAspect()) {
                setAspect(AspectKind.READER.getAspect());
            }
            if (getAttrKind() == TEST) {
                if (getKind().isCreator()) {
                    throw new FormatException("Conflicting aspects %s and %s", getAttrAspect(),
                        getAspect());
                }
            } else if (hasAttrAspect() && getKind() != READER && getKind() != EMBARGO) {
                throw new FormatException("Conflicting aspects %s and %s", getAttrAspect(),
                    getAspect());
            }
            if (source().getParamKind() == PARAM_ASK || target().getParamKind() == PARAM_ASK) {
                if (!getKind().isCreator()) {
                    throw new FormatException("User-provided parameters must be unconstrained");
                }
            }
        } else if (getKind().isRole()) {
            throw new FormatException("Edge aspect %s only allowed in rules", getAspect(), this);
        } else if (!hasAspect()) {
            setAspect(AspectKind.DEFAULT.getAspect());
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
        boolean simple = ruleLabel == null || ruleLabel.isAtom() || ruleLabel.isSharp()
            || ruleLabel.isWildcard() && ruleLabel.getWildcardGuard()
                .isNamed();
        if (!simple) {
            assert ruleLabel != null; // implied by !simple
            AspectKind kind = getKind();
            assert kind.isRole();
            String message = null;
            RegExpr matchExpr = ruleLabel.getMatchExpr();
            if (matchExpr.containsOperator(RegExpr.NEG_OPERATOR)) {
                message = "Negation only allowed as top-level operator";
            } else if (kind.isCreator()) {
                if (ruleLabel.isWildcard()) {
                    message = "Unnamed wildcard %s not allowed on creators";
                } else if (!ruleLabel.isEmpty()) {
                    message = "Regular expression label %s not allowed on creators";
                }
            } else if (kind.isEraser() && !source().getKind()
                .isEraser()
                && !target().getKind()
                    .isEraser()
                && !ruleLabel.isWildcard()) {
                message = "Regular expression label %s not allowed on erasers";
            }
            if (message != null) {
                throw new FormatException(message, ruleLabel, this);
            }
        }
    }

    @Override
    public boolean isFixed() {
        return this.fixed;
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
            if (getKind() != NESTED && getKind() != REMARK && getAttrKind() != TEST) {
                setAspect(NESTED.getAspect()
                    .newInstance(getInnerText(), getGraphRole()));
            }
        } else if (getKind() != REMARK && getKind() != SUBTYPE && getKind() != CONNECT
            && getKind() != LET) {
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
                throw new FormatException("Conflicting aspects %s and %s", source().getAspect(),
                    target().getAspect());
            }
            if (inferredAspect != null && inferredAspect.getKind()
                .isRole() && inferredAspect.getKind() != READER
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
    public boolean isCompatible(AspectElement other) {
        assert isFixed() && other.isFixed();
        if (getKind() == REMARK || other.getKind() == REMARK) {
            return true;
        }
        if (getAspect() == null ? other.getAspect() != null
            : !getAspect().equals(other.getAspect())) {
            return false;
        }
        if (getAttrAspect() == null ? other.getAttrAspect() != null
            : !getAttrAspect().equals(other.getAttrAspect())) {
            return false;
        }
        if (other instanceof AspectEdge) {
            AspectEdge edge = (AspectEdge) other;
            if (!getLabelMode().equals(edge.getLabelMode())) {
                return false;
            }
            if (getInMult() == null ? edge.getInMult() != null
                : !getInMult().equals(edge.getInMult())) {
                return false;
            }
            if (getOutMult() == null ? edge.getOutMult() != null
                : !getOutMult().equals(edge.getOutMult())) {
                return false;
            }
            if (isComposite() != edge.isComposite()) {
                return false;
            }
        }
        return true;
    }

    /** Returns the inner text of the edge label, i.e.,
     * the aspect label text without preceding aspects.
     */
    public String getInnerText() {
        return label().getInnerText();
    }

    /**
     * Returns the label that should be put on this
     * edge in the plain graph view.
     */
    public PlainLabel getPlainLabel() {
        return PlainLabel.parseLabel(label().toString());
    }

    /**
     * Returns the rule label or the type label, whichever is appropriate
     * depending on the graph role of this edge.
     * @see #getRuleLabel()
     * @see #getTypeLabel()
     */
    public Label getMatchLabel() {
        Label result = null;
        if (this.graphRole == RULE) {
            result = getRuleLabel();
        } else {
            result = getTypeLabel();
        }
        return result;
    }

    /**
     * Returns the display line corresponding to this aspect edge.
     * @param onNode if {@code true}, the line will be part of the node label,
     * otherwise it is labelling a binary edge
     * @param contextKind aspect kind of the element on which the line should be displayed.
     * If different from this aspect kind, the prefix will be displaued
     */
    public Line toLine(boolean onNode, AspectKind contextKind) {
        Line result = null;
        // Role prefix
        String rolePrefix = null;
        // line text, if the line is just atomic text
        String text = null;
        // set of line styles to be added to the entire line
        Set<Style> styles = EnumSet.noneOf(Style.class);
        // colour to be set for the entire line
        ColorType color = null;
        // prefix
        switch (getKind()) {
        case CONNECT:
            assert !onNode;
            text = "+";
            break;
        case LET:
            assert onNode;
            String symbol = getGraphRole() == RULE && !source().getKind()
                .isCreator() ? ":=" : "=";
            result = getAssign().toLine(symbol);
            if (getGraphRole() == RULE) {
                color = ColorType.CREATOR;
            }
            break;
        case NESTED:
            text = getAspect().getContentString();
            break;
        case REMARK:
            color = ColorType.REMARK;
            rolePrefix = "// ";
            text = getInnerText();
            break;
        case ADDER:
            color = ColorType.CREATOR;
            rolePrefix = "!+ ";
            break;
        case EMBARGO:
            color = ColorType.EMBARGO;
            rolePrefix = "! ";
            break;
        case ERASER:
            color = ColorType.ERASER;
            rolePrefix = "- ";
            break;
        case CREATOR:
            color = ColorType.CREATOR;
            rolePrefix = "+ ";
            break;
        default:
            // no annotation
        }
        if (result == null && text == null) {
            switch (getAttrKind()) {
            case ARGUMENT:
                text = "" + Util.LC_PI + getArgument();
                break;
            case TEST:
                result = getPredicate().toLine();
                break;
            case INT:
            case REAL:
            case STRING:
            case BOOL:
                if (getGraphRole() == GraphRole.TYPE) {
                    text = getAttrAspect().getContentString();
                } else {
                    text = getOperator().getName();
                }
                break;
            default:
                // not attribute-related text
            }
        }
        if (result == null) {
            if (text == null) {
                Label label = getGraphRole() == RULE ? getRuleLabel() : getTypeLabel();
                if (label == null) {
                    label = label();
                }
                result = label.toLine();
                if (source().getKind() == ABSTRACT) {
                    result = result.style(Style.ITALIC);
                }
            } else {
                result = Line.atom(text);
            }
        }
        if (onNode) {
            Sort type = null;
            if (!isLoop()) {
                switch (getGraphRole()) {
                case HOST:
                case RULE:
                    // this is an attribute edge displayed as a node label
                    String suffix = ASSIGN_TEXT + target().getAttrAspect()
                        .getContentString();
                    result = result.append(suffix);
                    break;
                case TYPE:
                    // this is a primitive type field declaration modelled through an
                    // edge to the target type
                    type = target().getAttrKind()
                        .getSort();
                    break;
                default:
                    throw Exceptions.UNREACHABLE;
                }
            } else if (getAttrKind().hasSort()) {
                // this is a primitive type field declaration
                // modelled through a self-edge
                type = getAttrKind().getSort();
            }
            if (type != null) {
                result = result.append(TYPE_TEXT);
                result = result.append(Line.atom(type.getName())
                    .style(Style.BOLD));
            }
        }
        if (contextKind != getKind() && rolePrefix != null) {
            result = Line.atom(rolePrefix)
                .append(result);
        }
        for (Style s : styles) {
            result = result.style(s);
        }
        if (color != null) {
            result = result.color(color);
        }
        Line levelSuffix = toLevelName();
        if (levelSuffix != null) {
            result = result.append(levelSuffix);
        }
        return result;
    }

    /**
     * Appends a level name to a given text,
     * depending on an edge role.
     */
    private Line toLevelName() {
        Line result = null;
        String name = getLevelName();
        // only consider proper names unequal to source or target level
        if (name != null && name.length() != 0 && !name.equals(source().getLevelName())
            && !name.equals(target().getLevelName())) {
            result = Line.atom(LEVEL_NAME_SEPARATOR)
                .append(Line.atom(name)
                    .style(Style.ITALIC))
                .color(Values.NESTED_COLOR);
        }
        return result;
    }

    /** Returns the (possibly {@code null}) rule label of this edge. */
    public RuleLabel getRuleLabel() {
        testFixed(true);
        return this.ruleLabel;
    }

    /**
     * Returns the rule label that this aspect edge gives rise to, if any.
     * @return a rule label generated from the aspects on this edge, or {@code null}
     * if the edge does not give rise to a rule label.
     */
    private RuleLabel createRuleLabel() throws FormatException {
        assert getGraphRole() == RULE;
        RuleLabel result;
        if (getKind().isMeta() || isAssign() || isPredicate()) {
            result = null;
        } else if (getAttrKind() != DEFAULT) {
            result = null;
        } else {
            assert isAssign() || getKind().isRole();
            if (getLabelKind() == LITERAL) {
                result = new RuleLabel(getInnerText());
            } else {
                result = new RuleLabel(parse(getInnerText()));
            }
        }
        return result;
    }

    /** Returns the (possibly {@code null}) type label of this edge. */
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
        if (getKind() == REMARK || isAssign() || isPredicate()
            || getGraphRole() == GraphRole.TYPE && getAttrKind().hasSort()) {
            result = null;
        } else if (!getKind().isRole() && getLabelKind() != PATH) {
            if (getLabelKind() == LITERAL) {
                result = TypeLabel.createBinaryLabel(getInnerText());
            } else {
                result = TypeLabel.createLabelWithCheck(getInnerText());
            }
        } else {
            throw new FormatException("Edge label '%s' is only allowed in rules", label(), this);
        }
        return result;
    }

    /**
     * Parses a given string as a regular expression,
     * taking potential curly braces into account.
     */
    private RegExpr parse(String text) throws FormatException {
        if (text.startsWith(RegExpr.NEG_OPERATOR)) {
            RegExpr innerExpr = parse(text.substring(RegExpr.NEG_OPERATOR.length()));
            return new RegExpr.Neg(innerExpr);
        } else {
            if (text.startsWith("" + StringHandler.LCURLY)) {
                text = StringHandler.toTrimmed(text, StringHandler.LCURLY, StringHandler.RCURLY);
            }
            return RegExpr.parse(text);
        }
    }

    /** Setter for the aspect type. */
    private void setAspect(Aspect aspect) throws FormatException {
        AspectKind kind = aspect.getKind();
        assert !kind.isAttrKind() && kind != AspectKind.PATH && kind != AspectKind.LITERAL;
        // process the content, if any
        if (kind.isQuantifier()) {
            // backward compatibility to take care of edges such as
            // exists=q:del:a rather than del=q:a or
            // exists=q:a rather than use=q:a
            if (!aspect.hasContent()) {
                throw new FormatException("Unnamed quantifier %s not allowed on edge", aspect,
                    this);
            } else if (this.levelName != null) {
                throw new FormatException("Duplicate quantifier levels %s and %s", this.levelName,
                    aspect.getContent(), this);
            } else {
                this.levelName = (String) aspect.getContent();
            }
        } else if (kind.isRole() && aspect.hasContent()) {
            if (this.levelName != null) {
                throw new FormatException("Duplicate quantifier levels %s and %s", this.levelName,
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
                throw new FormatException("Conflicting aspects %s and %s", this.aspect, aspect,
                    this);
            }
        }
    }

    @Override
    public Aspect getAspect() {
        return this.aspect;
    }

    @Override
    public boolean hasAspect() {
        return this.aspect != null;
    }

    /**
     * Returns the determining aspect kind of this edge.
     * This is one of {@link AspectKind#REMARK}, a role, {@link AspectKind#NESTED},
     * {@link AspectKind#ABSTRACT} or {@link AspectKind#SUBTYPE}.
     */
    @Override
    public AspectKind getKind() {
        return hasAspect() ? getAspect().getKind() : DEFAULT;
    }

    /** Retrieves the optional quantification level name of this edge. */
    public String getLevelName() {
        return this.levelName;
    }

    /** Indicates if this edge is a "nested:at". */
    public boolean isNestedAt() {
        return hasAspect() && getKind() == NESTED && getAspect().getContent() == NestedValue.AT;
    }

    /** Indicates if this edge is a "nested:in". */
    public boolean isNestedIn() {
        return hasAspect() && getKind() == NESTED && getAspect().getContent() == NestedValue.IN;
    }

    /** Indicates if this edge is a "nested:count". */
    public boolean isNestedCount() {
        return hasAspect() && getKind() == NESTED && getAspect().getContent() == NestedValue.COUNT;
    }

    /** Indicates that this is a creator element with a merge label ("="). */
    public boolean isMerger() {
        testFixed(true);
        return getKind().inRHS() && !getKind().inLHS() && getRuleLabel().isEmpty();
    }

    /** Setter for the aspect type. */
    private void setAttrAspect(Aspect type) {
        AspectKind kind = type.getKind();
        assert kind == AspectKind.DEFAULT || kind.isAttrKind();
        assert this.attr == null;
        this.attr = type;
        if (type.getKind() == ARGUMENT) {
            this.attr = type;
            this.argumentNr = (Integer) this.attr.getContent();
        } else if (kind.hasSort()) {
            this.attr = type;
            this.signature = kind.getSort();
            if (getGraphRole() == RULE) {
                this.operator = (Operator) type.getContent();
            }
        }
    }

    /** If the attribute aspect is yet unset, set it to the default. */
    private void setDefaultAttrAspect() {
        if (!hasAttrAspect()) {
            this.attr = AspectKind.DEFAULT.getAspect();
        }
    }

    @Override
    public Aspect getAttrAspect() {
        return this.attr;
    }

    @Override
    public boolean hasAttrAspect() {
        return this.attr != null && this.attr.getKind() != DEFAULT;
    }

    @Override
    public AspectKind getAttrKind() {
        return hasAttrAspect() ? getAttrAspect().getKind() : DEFAULT;
    }

    /** Returns the signature of the attribute aspect, if any. */
    public Sort getSignature() {
        return this.signature;
    }

    /** Indicates if this is an argument edge. */
    public boolean isArgument() {
        return this.argumentNr >= 0;
    }

    /** Indicates if this is a let-edge. */
    public boolean isAssign() {
        return this.hasAspect() && this.getKind() == LET;
    }

    /** Convenience method to retrieve the attribute aspect content as an assignment. */
    public Assignment getAssign() {
        assert isAssign();
        return (Assignment) getAspect().getContent();
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
    public Multiplicity getOutMult() {
        return this.outMult;
    }

    /** Setter for the label mode. */
    private void setLabelMode(Aspect type) throws FormatException {
        AspectKind kind = type.getKind();
        assert kind == DEFAULT || kind == PATH || kind == LITERAL;
        if (this.labelMode == null) {
            this.labelMode = type;
        } else {
            throw new FormatException("Conflicting edge aspects %s and %s", this.labelMode, type,
                this);
            // actually this should not happen since both of these
            // aspects are specified to be the last in a label
        }
    }

    /** If the label mode is yet unset, set it to the default. */
    private void setDefaultLabelMode() {
        if (!hasLabelMode()) {
            this.labelMode = AspectKind.DEFAULT.getAspect();
        }
    }

    /**
     * Retrieves the label mode of this edge.
     * This is either {@link AspectKind#DEFAULT}, {@link AspectKind#PATH} or {@link AspectKind#LITERAL}.
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
    /** The signature of the attribute-related aspect, if any. */
    private Sort signature;
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
    private final FormatErrorSet errors = new FormatErrorSet();
    /** Separator between level name and edge label. */
    static private final String LEVEL_NAME_SEPARATOR = "@";
    static private final String ASSIGN_TEXT = " = ";
    static private final String TYPE_TEXT = ": ";
}
