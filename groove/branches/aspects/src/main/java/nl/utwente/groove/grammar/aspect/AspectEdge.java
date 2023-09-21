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
package nl.utwente.groove.grammar.aspect;

import static nl.utwente.groove.grammar.aspect.AspectKind.ABSTRACT;
import static nl.utwente.groove.grammar.aspect.AspectKind.ARGUMENT;
import static nl.utwente.groove.grammar.aspect.AspectKind.CONNECT;
import static nl.utwente.groove.grammar.aspect.AspectKind.DEFAULT;
import static nl.utwente.groove.grammar.aspect.AspectKind.EMBARGO;
import static nl.utwente.groove.grammar.aspect.AspectKind.ERASER;
import static nl.utwente.groove.grammar.aspect.AspectKind.LET;
import static nl.utwente.groove.grammar.aspect.AspectKind.LET_NEW;
import static nl.utwente.groove.grammar.aspect.AspectKind.LITERAL;
import static nl.utwente.groove.grammar.aspect.AspectKind.NESTED;
import static nl.utwente.groove.grammar.aspect.AspectKind.PARAM_ASK;
import static nl.utwente.groove.grammar.aspect.AspectKind.PATH;
import static nl.utwente.groove.grammar.aspect.AspectKind.READER;
import static nl.utwente.groove.grammar.aspect.AspectKind.REMARK;
import static nl.utwente.groove.grammar.aspect.AspectKind.SUBTYPE;
import static nl.utwente.groove.grammar.aspect.AspectKind.TEST;
import static nl.utwente.groove.graph.GraphRole.RULE;
import static nl.utwente.groove.graph.GraphRole.TYPE;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;

import nl.utwente.groove.algebra.Operator;
import nl.utwente.groove.algebra.Sort;
import nl.utwente.groove.algebra.syntax.Assignment;
import nl.utwente.groove.algebra.syntax.ExprTree;
import nl.utwente.groove.algebra.syntax.Expression;
import nl.utwente.groove.algebra.syntax.Expression.Kind;
import nl.utwente.groove.automaton.RegExpr;
import nl.utwente.groove.grammar.aspect.AspectContent.ExprContent;
import nl.utwente.groove.grammar.aspect.AspectContent.IntegerContent;
import nl.utwente.groove.grammar.aspect.AspectContent.MultiplicityContent;
import nl.utwente.groove.grammar.aspect.AspectContent.NestedValue;
import nl.utwente.groove.grammar.aspect.AspectContent.OpContent;
import nl.utwente.groove.grammar.rule.RuleLabel;
import nl.utwente.groove.grammar.type.Multiplicity;
import nl.utwente.groove.grammar.type.TypeLabel;
import nl.utwente.groove.graph.AEdge;
import nl.utwente.groove.graph.EdgeRole;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.graph.Label;
import nl.utwente.groove.graph.plain.PlainLabel;
import nl.utwente.groove.gui.look.Values;
import nl.utwente.groove.io.Util;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.Fixable;
import nl.utwente.groove.util.line.Line;
import nl.utwente.groove.util.line.Line.ColorType;
import nl.utwente.groove.util.line.Line.Style;
import nl.utwente.groove.util.parse.FormatError;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;
import nl.utwente.groove.util.parse.StringHandler;

/**
 * Edge enriched with aspect data. Aspect edge labels are interpreted as
 * {@link PlainLabel}s.
 * @author Arend Rensink
 * @version $Revision$
 */
public class AspectEdge extends AEdge<@NonNull AspectNode,@NonNull AspectLabel>
    implements AspectElement, Fixable {
    /**
     * Constructs a new, numbered edge.
     * @param source the source node for this edge
     * @param label the label for this edge
     * @param target the target node for this edge
     * @param number the edge number
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
        this.graph = source.getGraph();
    }

    /**
     * Constructs a new edge, with edge number {@code 0}.
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

    @Override
    public AspectGraph getGraph() {
        return this.graph;
    }

    /** The graph that this element belongs to. */
    private final AspectGraph graph;

    /** Checks if the graph role of this aspect edge equals a given role. */
    public boolean hasGraphRole(GraphRole role) {
        return getGraphRole() == role;
    }

    /** Returns the graph role set for this aspect edge. */
    public GraphRole getGraphRole() {
        return getGraph().getRole();
    }

    @Override
    public boolean setParsed() {
        if (DEBUG) {
            System.out.printf("setParsed called on %s%n", this);
        }
        boolean result = !isParsed();
        if (result) {
            if (DEBUG) {
                System.out.printf("Parsing %s%n", this);
            }
            this.status = Status.PARSED;
            if (!hasErrors()) {
                parseAspects();
            }
            setDefaultAttrAspect();
            setDefaultLabelMode();
        }
        return result;
    }

    @Override
    public boolean setTyped() {
        if (DEBUG) {
            System.out.printf("setTyped called on %s%n", this);
        }
        boolean result = !isTyped();
        if (result) {
            if (DEBUG) {
                System.out.printf("Typing %s%n", this);
            }
            setParsed();
            this.status = Status.TYPED;
            if (!hasErrors()) {
                typeExpression();
            }
        }
        return result;
    }

    @Override
    public Status getStatus() {
        return this.status;
    }

    /** The construction status of this AspectEdge. */
    private Status status = Status.NEW;

    @Override
    public EdgeRole getRole() {
        if (this.isPredicate() || isAssign()) {
            // We just want the edge role to be non-binary...
            return EdgeRole.FLAG;
        } else if (hasGraphRole(TYPE) && getAttrKind().hasSort()) {
            return EdgeRole.FLAG;
        } else {
            Label label = hasGraphRole(RULE)
                ? getRuleLabel()
                : getTypeLabel();
            return label == null
                ? EdgeRole.BINARY
                : label.getRole();
        }
    }

    /**
     * Parses the aspects, by first setting the declared label aspects,
     * then inferring aspects from the end nodes.
     * Should only be called if the edge has no errors otherwise.
     */
    private void parseAspects() {
        try {
            setAspects(label());
            inferAspects();
            checkAspects();
            if (hasGraphRole(RULE)) {
                this.ruleLabel = createRuleLabel();
                this.typeLabel = null;
            } else {
                this.ruleLabel = null;
                this.typeLabel = createTypeLabel();
            }
            target().inferInAspect(this);
            source().inferOutAspect(this);
            if (hasGraphRole(RULE) && !getKind().isMeta()) {
                checkRegExprs();
            }
        } catch (FormatException exc) {
            for (FormatError error : exc.getErrors()) {
                this.errors.add(error.extend(this));
            }
        }
    }

    /** Types the expression that may occur on this edge. */
    private void typeExpression() {
        try {
            if (isAssign()) {
                this.assign = createAssign();
            } else if (isPredicate()) {
                this.predicate = createPredicate();
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
        return this.errors;
    }

    /** Adds a format error to the errors in this edge. */
    public void addError(FormatError error) {
        testFixed(false);
        this.errors.add(error.extend(this));
    }

    /** List of syntax errors in this edge. */
    private final FormatErrorSet errors = new FormatErrorSet();

    /**
     * Checks for the presence and consistency of the
     * type and attribute aspects.
     */
    private void checkAspects() throws FormatException {
        if (hasGraphRole(RULE)) {
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
            if (source().getParKind() == PARAM_ASK || target().getParKind() == PARAM_ASK) {
                if (!getKind().isCreator() && getKind() != REMARK) {
                    throw new FormatException("User-provided parameters must be unconstrained");
                }
            }
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
            || ruleLabel.isWildcard(wc -> wc.getWildcardGuard().isNamed());
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
            } else if (kind.isEraser() && !source().getKind().isEraser()
                && !target().getKind().isEraser() && !ruleLabel.isWildcard()) {
                message = "Regular expression label %s not allowed on erasers";
            }
            if (message != null) {
                throw new FormatException(message, ruleLabel, this);
            }
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
            if (getKind() != NESTED && getKind() != REMARK && getAttrKind() != TEST) {
                setAspect(NESTED.getAspect().newInstance(getInnerText(), getGraphRole()));
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
            if (inferredAspect.getKind().isRole() && inferredAspect.getKind() != READER
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
        assert value.isForEdge(getGraphRole());
        AspectKind kind = value.getKind();
        switch (kind) {
        case PATH, LITERAL -> setLabelMode(value);
        case INT, REAL, BOOL, STRING -> setAttrAspect(value);
        case TEST -> setAttrAspect(value);
        case MULT_IN, MULT_OUT -> setMultiplicityAspect(value);
        case COMPOSITE -> setCompositeAspect(value);
        case EXISTS, EXISTS_OPT, FORALL, FORALL_POS -> setQuantifierAspect(value);
        }
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
        assert isParsed() && other.isFixed();
        if (getKind() == REMARK || other.getKind() == REMARK) {
            return true;
        }
        if (!Objects.equals(getAspect(), other.getAspect())) {
            return false;
        }
        if (!Objects.equals(getAttrAspect(), other.getAttrAspect())) {
            return false;
        }
        if (other instanceof AspectEdge edge) {
            if (!getLabelMode().equals(edge.getLabelMode())) {
                return false;
            }
            if (getInMult() == null
                ? edge.getInMult() != null
                : !getInMult().equals(edge.getInMult())) {
                return false;
            }
            if (getOutMult() == null
                ? edge.getOutMult() != null
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
        if (hasGraphRole(RULE)) {
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
     * If different from this aspect kind, the prefix will be displayed
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
        case LET, LET_NEW:
            assert onNode;
            String symbol;
            if (hasGraphRole(RULE) && !source().getKind().isCreator()) {
                // do not use #CHANGE_TO_SYMBOL as the prefix already tells the story
                symbol = POINTS_TO_SYMBOL;
                rolePrefix = getKind() == LET
                    ? CHANGE_PREFIX
                    : NEW_PREFIX;
            } else {
                symbol = POINTS_TO_SYMBOL;
            }
            result = getAssignLine(symbol);
            if (hasGraphRole(RULE)) {
                color = ColorType.CREATOR;
            }
            break;
        case NESTED:
            text = getAspect().getContentString();
            break;
        case REMARK:
            color = ColorType.REMARK;
            rolePrefix = REM_PREFIX;
            text = getInnerText();
            break;
        case ADDER:
            color = ColorType.CREATOR;
            rolePrefix = ADD_PREFIX;
            break;
        case EMBARGO:
            color = ColorType.EMBARGO;
            rolePrefix = NOT_PREFIX;
            break;
        case ERASER:
            color = ColorType.ERASER;
            rolePrefix = DEL_PREFIX;
            break;
        case CREATOR:
            color = ColorType.CREATOR;
            rolePrefix = NEW_PREFIX;
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
                result = getPredicateLine();
                break;
            case INT:
            case REAL:
            case STRING:
            case BOOL:
                if (hasGraphRole(TYPE)) {
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
                Label label = hasGraphRole(RULE)
                    ? getRuleLabel()
                    : getTypeLabel();
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
                    result = result.append(Util.THIN_SPACE + POINTS_TO_SYMBOL + Util.THIN_SPACE);
                    result = result.append(target().getValue().toLine());
                    break;
                case TYPE:
                    // this is a primitive type field declaration modelled through an
                    // edge to the target type
                    type = target().getSort();
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
                result = result.append(Util.HAIR_SPACE + TYPED_AS_SYMBOL + Util.HAIR_SPACE);
                result = result.append(Line.atom(type.getName()).style(Style.BOLD));
            }
        }
        if (contextKind != getKind() && rolePrefix != null) {
            result = Line.atom(rolePrefix + SPACE).append(result);
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
            result = Line
                .atom(LEVEL_NAME_SEPARATOR)
                .append(Line.atom(name).style(Style.ITALIC))
                .color(Values.NESTED_COLOR);
        }
        return result;
    }

    /** Returns the (possibly {@code null}) rule label of this edge. */
    public RuleLabel getRuleLabel() {
        assert isParsed();
        return this.ruleLabel;
    }

    /**
     * Returns the rule label that this aspect edge gives rise to, if any.
     * @return a rule label generated from the aspects on this edge, or {@code null}
     * if the edge does not give rise to a rule label.
     */
    private RuleLabel createRuleLabel() throws FormatException {
        assert hasGraphRole(RULE);
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

    /** The (possibly {@code null}) rule label modelled by this edge. */
    private RuleLabel ruleLabel;

    /** Returns the (possibly {@code null}) type label of this edge. */
    public TypeLabel getTypeLabel() {
        assert isParsed();
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
            || hasGraphRole(TYPE) && getAttrKind().hasSort()) {
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

    /** The (possibly {@code null}) type label modelled by this edge. */
    private TypeLabel typeLabel;

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
        AspectContent content = aspect.getContent();
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
                this.levelName = (String) content.get();
            }
        } else if (kind.isRole() && aspect.hasContent()) {
            if (this.levelName != null) {
                throw new FormatException("Duplicate quantifier levels %s and %s", this.levelName,
                    aspect.getContent(), this);
            } else {
                this.levelName = (String) content.get();
            }
        }
        // actually set the type, if the passed-in value was not a quantifier
        // (which we use only for its level name)
        if (kind == AspectKind.MULT_IN) {
            this.inMult = ((MultiplicityContent) content).get();
        } else if (kind == AspectKind.MULT_OUT) {
            this.outMult = ((MultiplicityContent) content).get();
        } else if (kind == AspectKind.COMPOSITE) {
            this.composite = true;
        } else if (!kind.isQuantifier()) {
            if (this.aspect == null) {
                this.aspect = aspect;
            } else if (!this.aspect.equals(aspect)) {
                var myKind = this.aspect.getKind();
                var compatible = kind == AspectKind.ADDER
                    && (myKind == AspectKind.CREATOR || myKind == AspectKind.EMBARGO);
                if (!compatible) {
                    throw new FormatException("Conflicting aspects %s and %s", this.aspect, aspect,
                        this);
                }
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
        return hasAspect()
            ? getAspect().getKind()
            : DEFAULT;
    }

    /** The declared or inferred type of the aspect edge. */
    private Aspect aspect;

    /** Retrieves the optional quantification level name of this edge. */
    public String getLevelName() {
        return this.levelName;
    }

    /** The quantifier level name, if any. */
    private String levelName;

    /** Indicates if this edge is a "nested:at". */
    public boolean isNestedAt() {
        return hasAspect() && getKind() == NESTED
            && getAspect().getContent().get() == NestedValue.AT;
    }

    /** Indicates if this edge is a "nested:in". */
    public boolean isNestedIn() {
        return hasAspect() && getKind() == NESTED
            && getAspect().getContent().get() == NestedValue.IN;
    }

    /** Indicates if this edge is a "nested:count". */
    public boolean isNestedCount() {
        return hasAspect() && getKind() == NESTED
            && getAspect().getContent().get() == NestedValue.COUNT;
    }

    /** Indicates that this is a creator element with a merge label ("="). */
    public boolean isMerger() {
        assert isParsed();
        return getKind().inRHS() && !getKind().inLHS() && getRuleLabel().isEmpty();
    }

    /** Setter for the aspect type. */
    private void setAttrAspect(Aspect type) {
        AspectKind kind = type.getKind();
        AspectContent content = type.getContent();
        assert kind == AspectKind.DEFAULT || kind.isAttrKind();
        assert this.attr == null;
        this.attr = type;
        if (type.getKind() == ARGUMENT) {
            this.attr = type;
            this.argumentNr = ((IntegerContent) content).get();
        } else if (kind.hasSort()) {
            this.attr = type;
            this.signature = kind.getSort();
            if (hasGraphRole(RULE)) {
                this.operator = ((OpContent) content).get();
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
        return hasAttrAspect()
            ? getAttrAspect().getKind()
            : DEFAULT;
    }

    /** An optional attribute-related aspect. */
    private Aspect attr;

    /** Returns the signature of the attribute aspect, if any. */
    public Sort getSignature() {
        return this.signature;
    }

    /** The signature of the attribute-related aspect, if any. */
    private Sort signature;

    /** Indicates if this is an argument edge. */
    public boolean isArgument() {
        return this.argumentNr >= 0;
    }

    /** Indicates if this is a let- or letnew-edge. */
    public boolean isAssign() {
        return this.hasAspect() && (this.getKind() == LET || this.getKind() == LET_NEW);
    }

    /** Convenience method to retrieve the attribute aspect content as an assignment. */
    public Assignment getAssign() {
        var result = this.assign;
        if (result == null && isParsed() && getGraph().isNodeComplete()) {
            setTyped();
            result = this.assign;
        }
        return result;
    }

    private Assignment createAssign() throws FormatException {
        assert isAssign();
        assert isParsed();
        return ((ExprContent) getAspect().getContent()).get().toAssignment(getGraph().getTyping());
    }

    /** Returns a line describing the assignment.
     * This is either the (correctly typed) assignment or the original aspect.
     * @see Assignment#toLine(String)
     */
    private Line getAssignLine(String assignSymbol) {
        assert isAssign();
        assert isParsed();
        return getAssign() == null
            ? Line.atom(getAspect().toString())
            : getAssign().toLine(assignSymbol);
    }

    /**
     * This edge's assignment, if the edge stands for an assignment.
     * Computed when fixing the edge.
     */
    private Assignment assign;

    /** Indicates if this is an attribute predicate edge. */
    public boolean isPredicate() {
        return this.hasAttrAspect() && this.getAttrKind() == TEST;
    }

    /** Convenience method to retrieve the attribute aspect content as a predicate. */
    public Expression getPredicate() {
        var result = this.predicate;
        if (result == null && isParsed() && getGraph().isNodeComplete()) {
            setTyped();
            result = this.predicate;
        }
        return result;
    }

    /** Returns a line describing the predicate.
     * This is either the (correctly typed) assignment or the original aspect.
     * @see Expression#toLine()
     */
    private Line getPredicateLine() {
        assert isPredicate();
        assert isParsed();
        return getPredicate() == null
            ? ((ExprContent) getAspect().getContent()).get().toLine()
            : getPredicate().toLine();
    }

    private Expression createPredicate() throws FormatException {
        assert isPredicate();
        assert isParsed();
        Expression result = null;
        ExprTree tree = ((ExprContent) getAttrAspect().getContent()).get();
        result = tree.toExpression(getGraph().getTyping());
        if (result.getKind() == Kind.FIELD) {
            throw new FormatException("Field expression '%s' not allowed as predicate expression",
                tree.getParseString(), this);
        }
        if (result.getSort() != Sort.BOOL) {
            throw new FormatException(
                "Non-boolean expression '%s' not allowed as predicate expression",
                tree.getParseString(), this);
        }
        return result;
    }

    private Expression predicate;

    /**
     * Returns the argument number, if this is an argument edge.
     * @return a non-negative number if and only if this is an argument edge
     */
    public int getArgument() {
        return this.argumentNr;
    }

    /** Argument number, if this is an argument edge. */
    private int argumentNr = -1;

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

    /** Algebraic operator, if this is an operator edge. */
    private Operator operator = null;

    /** Indicates if this is a composite type edge. */
    public boolean isComposite() {
        return this.composite;
    }

    /** Flag indicating that this is a composite type edge. */
    private boolean composite;

    /** Returns the incoming multiplicity of this (type) edge, if any. */
    public Multiplicity getInMult() {
        return this.inMult;
    }

    /** The incoming multiplicity of this (type) edge.
     * {@code null} if there is no incoming multiplicity declared.
     */
    private Multiplicity inMult;

    /** Returns the outgoing multiplicity of this (type) edge, if any. */
    public Multiplicity getOutMult() {
        return this.outMult;
    }

    /** The outgoing multiplicity of this (type) edge.
     * {@code null} if there is no outgoing multiplicity declared.
     */
    private Multiplicity outMult;

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

    /** The parser mode of the label (either TypeAspect#PATH or TypeAspect#EMPTY). */
    private Aspect labelMode;

    /**
     * Retrieves the label aspect kind of this edge, if any.
     * This is either {@link AspectKind#PATH} or {@link AspectKind#LITERAL}.
     */
    public AspectKind getLabelKind() {
        return hasLabelMode()
            ? getLabelMode().getKind()
            : null;
    }

    /** Separator between level name and edge label. */
    static private final String LEVEL_NAME_SEPARATOR = "@";
    /** Space symbol in label line. */
    static private final String SPACE = " ";
    /** Points-to symbol between attribute name and value or expression. */
    static private final String POINTS_TO_SYMBOL = "" + Util.RA;
    /** Change-to symbol between attribute name and value or expression (unused right now). */
    // static private final String CHANGE_TO_SYMBOL = "" + Util.RA_STROKE;
    /** Typed-as symbol between attribute name and type. */
    static private final String TYPED_AS_SYMBOL = ":";
    /** Prefix for newly created flags or attributes. */
    static private final String NEW_PREFIX = "+";
    /** Prefix for deleted flags or attributes. */
    static private final String DEL_PREFIX = "-";
    /** Prefix for negated flags or attributes. */
    static private final String NOT_PREFIX = "!";
    /** Prefix for changed attributes. */
    static private final String CHANGE_PREFIX = "" + Util.PLUSMINUS;
    /** Prefix for conditionally added (= negated + created) flags or attributes. */
    static private final String ADD_PREFIX = NOT_PREFIX + NEW_PREFIX;
    /** Prefix for remark lines. */
    static private final String REM_PREFIX = "//";

    /** Debug flag. */
    static private final boolean DEBUG = false;
}
