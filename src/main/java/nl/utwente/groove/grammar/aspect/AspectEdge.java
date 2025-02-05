/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
import static nl.utwente.groove.grammar.aspect.AspectKind.ADDER;
import static nl.utwente.groove.grammar.aspect.AspectKind.ARGUMENT;
import static nl.utwente.groove.grammar.aspect.AspectKind.ATOM;
import static nl.utwente.groove.grammar.aspect.AspectKind.CONNECT;
import static nl.utwente.groove.grammar.aspect.AspectKind.CREATOR;
import static nl.utwente.groove.grammar.aspect.AspectKind.EMBARGO;
import static nl.utwente.groove.grammar.aspect.AspectKind.ERASER;
import static nl.utwente.groove.grammar.aspect.AspectKind.INT;
import static nl.utwente.groove.grammar.aspect.AspectKind.LET;
import static nl.utwente.groove.grammar.aspect.AspectKind.LITERAL;
import static nl.utwente.groove.grammar.aspect.AspectKind.NESTED;
import static nl.utwente.groove.grammar.aspect.AspectKind.PARAM_ASK;
import static nl.utwente.groove.grammar.aspect.AspectKind.PATH;
import static nl.utwente.groove.grammar.aspect.AspectKind.READER;
import static nl.utwente.groove.grammar.aspect.AspectKind.REMARK;
import static nl.utwente.groove.grammar.aspect.AspectKind.SUBTYPE;
import static nl.utwente.groove.grammar.aspect.AspectKind.TEST;
import static nl.utwente.groove.graph.GraphRole.HOST;
import static nl.utwente.groove.graph.GraphRole.RULE;
import static nl.utwente.groove.graph.GraphRole.TYPE;
import static nl.utwente.groove.util.Factory.lazy;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.Operator;
import nl.utwente.groove.algebra.Sort;
import nl.utwente.groove.algebra.syntax.Assignment;
import nl.utwente.groove.algebra.syntax.CallExpr;
import nl.utwente.groove.algebra.syntax.ExprTree;
import nl.utwente.groove.algebra.syntax.Expression;
import nl.utwente.groove.algebra.syntax.Expression.Kind;
import nl.utwente.groove.algebra.syntax.FieldExpr;
import nl.utwente.groove.algebra.syntax.SortMap;
import nl.utwente.groove.automaton.RegExpr;
import nl.utwente.groove.grammar.aspect.AspectContent.ExprContent;
import nl.utwente.groove.grammar.aspect.AspectContent.IdContent;
import nl.utwente.groove.grammar.aspect.AspectContent.IntegerContent;
import nl.utwente.groove.grammar.aspect.AspectContent.MultiplicityContent;
import nl.utwente.groove.grammar.aspect.AspectContent.NestedValue;
import nl.utwente.groove.grammar.aspect.AspectKind.Category;
import nl.utwente.groove.grammar.rule.RuleLabel;
import nl.utwente.groove.grammar.type.Multiplicity;
import nl.utwente.groove.grammar.type.TypeLabel;
import nl.utwente.groove.graph.AEdge;
import nl.utwente.groove.graph.EdgeRole;
import nl.utwente.groove.graph.Label;
import nl.utwente.groove.graph.plain.PlainLabel;
import nl.utwente.groove.gui.look.Values;
import nl.utwente.groove.io.Util;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.Factory;
import nl.utwente.groove.util.Fixable;
import nl.utwente.groove.util.line.Line;
import nl.utwente.groove.util.line.Line.ColorType;
import nl.utwente.groove.util.line.Line.Style;
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
        this.graph = source.getGraph();
        this.aspects = new Aspect.Map(false, this.graph.getRole());
        if (!label.hasErrors() && label.isNodeOnly()) {
            if (label.getNodeOnlyAspect() == null) {
                addError("Empty edge label not allowed");
            } else {
                addError("Aspect %s not allowed in edge label", label.getNodeOnlyAspect());
            }
        }
        addErrors(label.getErrors());
        label.getAspects().forEach(this::set);
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

    @Override
    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public Status getStatus() {
        return this.status;
    }

    /** The construction status of this AspectEdge. */
    private Status status = Status.NEW;

    @Override
    public EdgeRole getRole() {
        if (isTest() || isAssign() || isField()) {
            // We just want the edge role to be non-binary...
            return EdgeRole.FLAG;
        } else if (hasGraphRole(TYPE) && has(Category.SORT)) {
            return EdgeRole.FLAG;
        } else {
            /* The following seems more elaborate than necessary
            Label label = hasGraphRole(RULE)
                ? getRuleLabel()
                : getTypeLabel();
            return label == null
                ? EdgeRole.BINARY
                : label.getRole();
            */
            return EdgeRole.parseLabel(getInnerText()).one();
        }
    }

    @Override
    public Aspect.Map getAspects() {
        return this.aspects;
    }

    /** The initially empty aspect map. */
    private Aspect.Map aspects;

    @Override
    public void set(Aspect aspect) {
        if (!aspect.isForEdge(getGraphRole())) {
            addError("Aspect '%s' not allowed on edge", aspect);
        } else {
            try {
                getAspects().add(aspect);
                AspectContent content = aspect.getContent();
                var cat = aspect.getCategory();
                switch (cat) {
                case ATTR:
                    if (aspect.has(ARGUMENT)) {
                        setArgument(((IntegerContent) content).get());
                    } else if (aspect.has(TEST)) {
                        setPredicateTree(((ExprContent) content).get());
                        getGraph().setNonNormal();
                    } else {
                        assert aspect.has(LET);
                        setAssignTree(((ExprContent) content).get());
                        getGraph().setNonNormal();
                    }
                    break;
                case SORT:
                    if (hasGraphRole(RULE)) {
                        String id = (String) content.get();
                        if (isLoop()) {
                            // this is an attribute field on a self-edge
                            setField(id);
                            getGraph().setNonNormal();
                        } else {
                            @SuppressWarnings("null")
                            var op = getSort().getOperator(id);
                            assert op != null;
                            setOperator(op);
                        }
                    }
                    break;
                case MULT_IN:
                    setInMult(((MultiplicityContent) content).get());
                    break;
                case MULT_OUT:
                    setOutMult(((MultiplicityContent) content).get());
                    break;
                case NESTING:
                    if (aspect.getKind().isQuantifier()) {
                        // backward compatibility to take care of edges such as
                        // exists=q:del:a rather than del=q:a or
                        // exists=q:a rather than use=q:a
                        if (content.isNull()) {
                            throw new FormatException("Unnamed quantifier %s not allowed on edge",
                                aspect);
                        } else {
                            setLevelName(((IdContent) content).get());
                        }
                    }
                    break;
                case ROLE:
                    if (!content.isNull()) {
                        setLevelName(((IdContent) content).get());
                    }
                    break;
                default:
                    // do nothing else
                }
            } catch (FormatException exc) {
                addErrors(exc.getErrors());
            }
        }
    }

    @Override
    public void parseAspects() throws FormatException {
        source().setParsed();
        target().setParsed();
        if (source().has(REMARK) || target().has(REMARK)) {
            getAspects().remove(Category.LABEL);
            set(REMARK.getAspect());
        } else if (source().has(Category.NESTING) || target().has(Category.NESTING)) {
            if (!has(NESTED) && !has(REMARK) && !has(TEST)) {
                set(NESTED.newAspect(getInnerText(), getGraphRole()));
            }
        } else if (hasGraphRole(RULE) && !has(REMARK)
            && !has(Category.NESTING, k -> !k.isQuantifier()) && !has(Category.ROLE)) {
            // infer a role from the source or target node
            AspectKind sourceRole = null;
            AspectKind targetRole = null;
            if (source().has(Category.ROLE) && !source().has(READER)) {
                sourceRole = source().getKind(Category.ROLE);
            }
            if (target().has(Category.ROLE) && !target().has(READER)) {
                targetRole = target().getKind(Category.ROLE);
            }
            AspectKind inferredRole;
            if (sourceRole == null) {
                inferredRole = targetRole;
            } else if (targetRole == null) {
                inferredRole = sourceRole;
            } else if (sourceRole == targetRole) {
                inferredRole = targetRole;
            } else if ((sourceRole == ERASER || sourceRole == ADDER) && targetRole == EMBARGO) {
                inferredRole = targetRole;
            } else if ((targetRole == ERASER || targetRole == ADDER) && sourceRole == EMBARGO) {
                inferredRole = sourceRole;
            } else {
                throw new FormatException("Conflicting source and target roles '%s' and '%s'",
                    sourceRole, targetRole);
            }
            if (inferredRole == null) {
                inferredRole = READER;
            }
            set(inferredRole.getAspect());
        }
        if (hasGraphRole(RULE) && (isArgument() || isOperator())) {
            source().set(AspectKind.PRODUCT.getAspect());
        }
        // check whether the edge should have a label aspect
        boolean hasLabel = switch (getGraphRole()) {
        case HOST -> !has(REMARK) && !has(Category.ATTR);
        case RULE -> has(Category.ROLE) && !has(Category.ATTR) && !has(Category.SORT)
            && !has(Category.NESTING, k -> !k.isQuantifier());
        case TYPE -> !has(REMARK) && !has(SUBTYPE) && !has(Category.SORT);
        default -> throw Exceptions.UNREACHABLE;
        };
        // set the label category if not yet done
        if (hasLabel && !has(Category.LABEL)) {
            AspectKind labelKind;
            if (hasGraphRole(RULE)) {
                var regExpr = parse(getInnerText());
                labelKind = regExpr.isSingular()
                    ? ATOM
                    : PATH;
            } else {
                labelKind = ATOM;
            }
            set(labelKind.getAspect());
        }
        // infer source node aspects
        AspectNode source = source();
        if (has(NESTED)) {
            source.setNestingEdge(this);
        } else if (has(ABSTRACT) && hasRole(EdgeRole.NODE_TYPE)) {
            source.set(ABSTRACT.getAspect());
        }
    }

    @Override
    public void checkAspects() throws FormatException {
        FormatErrorSet errors = new FormatErrorSet();
        if (hasGraphRole(RULE)) {
            if (has(CONNECT)) {
                if (!source().has(EMBARGO)) {
                    errors
                        .add("Adjacent node of %s-edge should be embargo", CONNECT.getPrefix(),
                             source());
                }
                if (!target().has(EMBARGO)) {
                    errors
                        .add("Adjacent node of %s-edge should be embargo", CONNECT.getPrefix(),
                             target());
                }
            }
            if (has(Category.LABEL)) {
                checkRegExprs(errors);
            }
            if (isOperator() && !has(READER) && !has(EMBARGO)) {
                errors
                    .add("Conflicting aspects '%s' and '%s' on operator edge",
                         getKind(Category.SORT), getKind(Category.ROLE));
            }
            if (isTest() && has(ERASER) && !source().has(ERASER)) {
                var test = getTestTree();
                assert test != null;
                errors.add("Test '%s' cannot be eraser", test.getParseString());
            }
            if (source().has(PARAM_ASK) || target().has(PARAM_ASK)) {
                if (!has(CREATOR) && !has(REMARK) && !has(ARGUMENT)) {
                    errors
                        .add("User-provided parameter '%s' must be unconstrained",
                             target().get(PARAM_ASK));
                }
            }
            AspectKind role = getKind(Category.ROLE);
            if (role != null) {
                AspectKind sourceRole = source().getKind(Category.ROLE);
                if (!isCompatible(role, sourceRole)) {
                    errors
                        .add("Role of %s-edge not compatible with source role '%s'", label(),
                             sourceRole);
                }
                AspectKind targetRole = target().getKind(Category.ROLE);
                if (!isCompatible(role, targetRole)) {
                    errors
                        .add("Role of %s-edge not compatible with target role '%s'", label(),
                             targetRole);
                }
            }
            var target = target();
            if (has(ARGUMENT)) {
                if (!target.has(Category.SORT)) {
                    throw new FormatException("Target node of %s-edge should be attribute",
                        label());
                }
            } else if ((isNestedAt() || isNestedIn()) && !target.has(Category.NESTING)) {
                throw new FormatException("Target node of %s-edge should be quantifier", label());
            } else if (isNestedCount()) {
                if (!target.has(INT)) {
                    throw new FormatException("Target node of %s-edge should be int-node", label());
                }
                if (!source().has(Category.NESTING, AspectKind::isForall)) {
                    throw new FormatException(
                        "Source node of %s-edge should be universal quantifier", label());
                }
            } else if (isOperator()) {
                @SuppressWarnings("null")
                Sort operSort = getOperator().getResultType();
                var targetSort = target.getSort();
                if (targetSort == null) {
                    throw new FormatException("Target node of %s-edge should be %s-attribute",
                        label(), operSort);
                } else if (targetSort != operSort) {
                    throw new FormatException(
                        "Inferred type %s of %s-target conflicts with declared type %s", operSort,
                        label(), targetSort);
                }
            }
        }
        addErrors(errors);
        getErrors().throwException();
    }

    /** Tests whether a given edge role is compatible with the role of an adjacent node. */
    private boolean isCompatible(AspectKind edgeRole, AspectKind nodeRole) {
        return edgeRole == nodeRole || switch (edgeRole) {
        case ADDER -> nodeRole == READER;
        case EMBARGO -> nodeRole == READER || nodeRole == ERASER || nodeRole == ADDER;
        case ERASER -> nodeRole == READER;
        case CREATOR -> nodeRole == READER || nodeRole == ADDER;
        default -> false;
        };
    }

    @Override
    public boolean hasErrors() {
        return !getErrors().isEmpty();
    }

    @Override
    public FormatErrorSet getErrors() {
        return this.errors;
    }

    /** List of syntax errors in this edge. */
    private FormatErrorSet errors = new FormatErrorSet();

    /**
     * Tests if regular expression usage does not go beyond what is allowed.
     * In particular, regular expressions cannot be erasers or creators.
     * @param errors the error set to which any errors should be added
     */
    private void checkRegExprs(FormatErrorSet errors) {
        // this is called after the rule label has been computed
        RuleLabel ruleLabel = getRuleLabel();
        if (ruleLabel != null && (!ruleLabel.isSingular()
            || ruleLabel.isWildcard(wc -> !wc.getWildcardGuard().isNamed()))) {
            assert ruleLabel != null; // implied by !simple
            String message = null;
            RegExpr matchExpr = ruleLabel.getMatchExpr();
            if (matchExpr.containsOperator(RegExpr.NEG_OPERATOR)) {
                message = "Negation only allowed as top-level operator";
            } else if (has(Category.ROLE, AspectKind::isCreator)) {
                if (ruleLabel.isWildcard()) {
                    message = "Unnamed wildcard '%s' not allowed on creator";
                } else if (!ruleLabel.isEmpty()) {
                    message = "Regular expression label '%s' not allowed on creator";
                }
            } else if (has(ERASER) && !source().has(ERASER) && !target().has(ERASER)
                && !ruleLabel.isWildcard()) {
                message = "Regular expression label '%s' not allowed on eraser";
            }
            if (message != null) {
                errors.add(message, ruleLabel, this);
            }
        }
    }

    @Override
    public void fixDataStructures() {
        this.aspects.setFixed();
        this.aspects = Aspect.normalise(this.aspects);
        // generate label before errors are fixed
        getMatchLabel();
        // generate derived terms before errors are fixed
        if (isTest()) {
            this.test.get();
        }
        if (isAssign()) {
            this.assign.get();
        }
        if (hasErrors()) {
            this.errors.setFixed();
        } else {
            this.errors = FormatErrorSet.EMPTY;
        }
    }

    /** Tests if this edge has the same aspects as another aspect edge. */
    public boolean isCompatible(AspectEdge other) {
        assert isParsed() && other.isParsed();
        return getAspects().equals(other.getAspects());
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
     * @param context aspect map of the element on which the line should be displayed.
     * If different from this aspect kind, the prefix will be displayed
     */
    public Line toLine(boolean onNode, Aspect.Map context) {
        Line result = null;
        // Aspect on which the role prefix is based
        AspectKind roleAspect = null;
        // Role prefix
        String rolePrefix = null;
        // line text, if the line is just atomic text
        String text = null;
        // colour to be set for the entire line
        ColorType color = null;
        // prefix
        if (has(CONNECT)) {
            assert !onNode;
            text = CONNECT_LABEL;
        } else if (has(SUBTYPE)) {
            assert !onNode;
            text = "";
        } else if (has(NESTED)) {
            @SuppressWarnings("null")
            var content = text = get(NESTED).getContentString();
            text = content;
        } else if (has(REMARK)) {
            color = ColorType.REMARK;
            roleAspect = REMARK;
            rolePrefix = REM_PREFIX;
            text = getInnerText();
        } else if (has(Category.ATTR)) {
            AspectKind attrKind = getKind(Category.ATTR);
            assert attrKind != null;
            switch (attrKind) {
            case ARGUMENT:
                text = "" + Util.LC_PI + getArgument();
                break;
            case TEST:
                result = getTestLine();
                break;
            case LET:
                assert onNode;
                if (hasGraphRole(RULE) && !source().has(Category.ROLE, AspectKind::isCreator)) {
                    // do not use #CHANGE_TO_SYMBOL as the prefix already tells the story
                    rolePrefix = has(LET)
                        ? CHANGE_PREFIX
                        : NEW_PREFIX;
                }
                result = getAssignLine(POINTS_TO_SYMBOL);
                if (hasGraphRole(RULE)) {
                    color = ColorType.CREATOR;
                }
                break;
            default:
                throw Exceptions.UNREACHABLE;
            }
        } else if (has(Category.SORT)) {
            text = get(Category.SORT, Aspect::getContentString);
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
                if (source().has(ABSTRACT)) {
                    result = result.style(Style.ITALIC);
                }
            } else {
                result = Line.atom(text);
            }
        }
        if (onNode) {
            Sort type = null;
            if (isLoop()) {
                type = getSort();
            } else if (target().hasSort()) {
                type = target().getSort();
                assert type != null;
                switch (getGraphRole()) {
                case HOST:
                case RULE:
                    // this is an attribute edge displayed as a node label
                    result = result.append(Util.THIN_SPACE + POINTS_TO_SYMBOL + Util.THIN_SPACE);
                    var targetValue = hasGraphRole(HOST)
                        ? target().getValue()
                        : target().getExpression();
                    var targetLine = targetValue == null
                        ? Line.atom(type.getName()).style(Style.BOLD)
                        : targetValue.toLine();
                    result = result.append(targetLine);
                    // reset type to null to prevent further suffixing below
                    type = null;
                    break;
                case TYPE:
                    // this is a primitive type field declaration modelled through an
                    // edge to the target type
                    type = target().getSort();
                    break;
                default:
                    throw Exceptions.UNREACHABLE;
                }
            }
            if (type != null) {
                var separator = hasGraphRole(RULE)
                    ? POINTS_TO_SYMBOL
                    : TYPED_AS_SYMBOL;
                result = result.append(Util.HAIR_SPACE + separator + Util.HAIR_SPACE);
                result = result.append(Line.atom(type.getName()).style(Style.BOLD));
            }
        }
        if (has(Category.ROLE)) {
            var roleKind = getKind(Category.ROLE);
            assert roleKind != null;
            switch (roleKind) {
            case ADDER:
                color = ColorType.CREATOR;
                roleAspect = ADDER;
                rolePrefix = ADD_PREFIX;
                break;
            case EMBARGO:
                color = ColorType.EMBARGO;
                roleAspect = EMBARGO;
                rolePrefix = NOT_PREFIX;
                break;
            case ERASER:
                color = ColorType.ERASER;
                roleAspect = ERASER;
                rolePrefix = DEL_PREFIX;
                break;
            case CREATOR:
                color = ColorType.CREATOR;
                roleAspect = CREATOR;
                rolePrefix = NEW_PREFIX;
                break;
            default:
                // no annotation
            }
        }
        if (!context.has(roleAspect) && rolePrefix != null) {
            result = Line.atom(rolePrefix + SPACE).append(result);
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
        return this.ruleLabel.get();
    }

    /**
     * Returns the rule label that this aspect edge gives rise to, if any.
     * @return a rule label generated from the aspects on this edge, or {@code null}
     * if the edge does not give rise to a rule label.
     */
    private RuleLabel createRuleLabel() {
        assert isParsed();
        RuleLabel result = null;
        try {
            if (has(Category.ROLE) && has(Category.LABEL)) {
                result = has(LITERAL)
                    ? new RuleLabel(getInnerText())
                    : new RuleLabel(parse(getInnerText()));
            }
        } catch (FormatException exc) {
            addErrors(exc.getErrors());
        }
        return result;
    }

    /** The (possibly {@code null}) rule label modelled by this edge. */
    private Factory<RuleLabel> ruleLabel = lazy(this::createRuleLabel);

    /** Returns the (possibly {@code null}) type label of this edge. */
    public TypeLabel getTypeLabel() {
        return this.typeLabel.get();
    }

    /**
     * Returns the type label that this aspect edge gives rise to, if any.
     * @return a type label generated from the aspects on this edge, or {@code null}
     * if the edge does not give rise to a type label.
     */
    private TypeLabel createTypeLabel() {
        assert isParsed();
        TypeLabel result = null;
        var labelKind = getKind(Category.LABEL);
        if (labelKind != null) {
            try {
                result = switch (labelKind) {
                case LITERAL -> TypeLabel.createBinaryLabel(getInnerText());
                case ATOM -> TypeLabel.createLabelWithCheck(getInnerText());
                default -> throw Exceptions.UNREACHABLE;
                };
            } catch (FormatException exc) {
                addErrors(exc.getErrors());
            }
        }
        return result;
    }

    /** The (possibly {@code null}) type label modelled by this edge. */
    private Factory<TypeLabel> typeLabel = lazy(this::createTypeLabel);

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

    private void setLevelName(String levelName) throws FormatException {
        if (hasLevelName()) {
            throw new FormatException("Duplicate quantifier levels '%s' and '%s'", this.levelName,
                levelName);
        }
        this.levelName = levelName;
    }

    /** Checks if this edge has a quantification level name. */
    public boolean hasLevelName() {
        return getLevelName() != null;
    }

    /** Retrieves the optional quantification level name of this edge. */
    public String getLevelName() {
        return this.levelName;
    }

    /** The quantifier level name, if any. */
    private String levelName;

    /** Indicates if this edge is a "nested:at". */
    public boolean isNestedAt() {
        return hasContent(NESTED, c -> c.has(NestedValue.AT));
    }

    /** Indicates if this edge is a "nested:in". */
    public boolean isNestedIn() {
        return hasContent(NESTED, c -> c.has(NestedValue.IN));
    }

    /** Indicates if this edge is a "nested:count". */
    public boolean isNestedCount() {
        return hasContent(NESTED, c -> c.has(NestedValue.COUNT));
    }

    /** Indicates if this is a let-edge. */
    public boolean isAssign() {
        return has(LET);
    }

    /** Sets the expression tree of an assignment edge. */
    private void setAssignTree(ExprTree assignTree) {
        this.assignTree = assignTree;
    }

    /** Returns the expression tree of this assignment edge, if any. */
    private @Nullable ExprTree getAssignTree() {
        return this.assignTree;
    }

    /** The expression tree, if this is an assignment edge. */
    private @Nullable ExprTree assignTree;

    /** Returns the assignment wrapped by this edge, if any.
     * Should only be called if {@link #isAssign()} holds.
     */
    public Assignment getAssign() throws FormatException {
        var result = this.assign.get();
        getErrors().throwException();
        return result;
    }

    /** Factory method for the assignment expression. */
    private Assignment createAssign() {
        Assignment result = null;
        try {
            var assignTree = getAssignTree();
            assert assignTree != null;
            result = assignTree.toAssignment(getSortMap());
        } catch (FormatException exc) {
            addErrors(exc.getErrors());
        }
        return result;
    }

    private Factory<Assignment> assign = lazy(this::createAssign);

    /** Returns a line describing the assignment.
     * Should only be called if {@link #isAssign()} holds.
     */
    private Line getAssignLine(String assignSymbol) {
        assert isParsed();
        var assignTree = getAssignTree();
        assert assignTree != null;
        try {
            return getAssign().toLine(assignSymbol);
        } catch (FormatException exc) {
            return assignTree.toLine();
        }
    }

    /** Indicates if this is an attribute test edge. */
    public boolean isTest() {
        return has(TEST);
    }

    /** Sets the expression tree of this test edge. */
    private void setPredicateTree(ExprTree testTree) {
        this.testTree = testTree;
    }

    /** Returns the expression tree of this test edge, if any. */
    private @Nullable ExprTree getTestTree() {
        return this.testTree;
    }

    /** The expression tree, if this is a test edge. */
    private @Nullable ExprTree testTree;

    /** Factory method for the test expression. */
    private Expression createTest() {
        Expression result = null;
        try {
            var tree = getTestTree();
            assert tree != null;
            result = tree.toExpression(getSortMap());
            if (result.getKind() == Kind.FIELD) {
                throw new FormatException(
                    "Field expression '%s' not allowed as predicate expression",
                    result.toDisplayString());
            }
            if (result.getSort() != Sort.BOOL) {
                throw new FormatException(
                    "Non-boolean expression '%s' not allowed as predicate expression",
                    result.toDisplayString());
            }
        } catch (FormatException exc) {
            addErrors(exc.getErrors());
        }
        return result;
    }

    private Factory<Expression> test = lazy(this::createTest);

    /** Returns the test wrapped by this edge, if any.
     * Should only be called if {@link #isTest()} holds.
     */
    public Expression getTest() throws FormatException {
        var result = this.test.get();
        getErrors().throwException();
        return result;
    }

    /** Returns a line describing the test on this edge.
     * Should only be called if {@link #isTest()} holds.
     */
    private Line getTestLine() {
        assert isParsed();
        try {
            // make a test for a self-field look like an edge, like for let:
            if (getTest() instanceof CallExpr c && c.getOperator().isEquality()
                && c.getArgs().get(0) instanceof FieldExpr f && f.isSelf()) {
                return new Assignment(f.getField(), c.getArgs().get(1)).toLine(POINTS_TO_SYMBOL);
            } else {
                return getTest().toLine();
            }
        } catch (FormatException exc) {
            var testTree = getTestTree();
            assert testTree != null;
            return testTree.toLine();
        }
    }

    /** Constructs and returns a sort map consisting of the
     * containing {@link AspectGraph}'s sort map, complemented with
     * self-fields for the source node type, if derivable.
     */
    private SortMap getSortMap() {
        return getGraph().getSortMap(source().getType());
    }

    /** Indicates if this is an argument edge. */
    public boolean isArgument() {
        return this.argumentNr >= 0;
    }

    /** Sets the argument number to a (non-negative) value. */
    private void setArgument(int argumentNr) {
        assert argumentNr >= 0;
        this.argumentNr = argumentNr;
    }

    /**
     * Returns the argument number, if this is an argument edge.
     * @return a non-negative number if and only if this is an argument edge
     */
    public int getArgument() {
        int result = this.argumentNr;
        assert result >= 0;
        return result;
    }

    /** Argument number, if this is an argument edge. */
    private int argumentNr = -1;

    /** Sets the operator of this edge. */
    private void setOperator(@NonNull Operator operator) {
        this.operator = operator;
    }

    /** Indicates if this is an operator edge. */
    public boolean isOperator() {
        return this.operator != null;
    }

    /**
     * Returns an algebraic operator, if this is an operator edge.
     * @return a non-{@code null} object if and only if this is an operator edge
     */
    public @Nullable Operator getOperator() {
        return this.operator;
    }

    /** Algebraic operator, if this is an operator edge. */
    private Operator operator;

    /** Sets the field identifier of this edge. */
    private void setField(@NonNull String field) {
        this.field = field;
    }

    /** Indicates if this is a field self-edge. */
    public boolean isField() {
        return this.field != null;
    }

    /**
     * Returns the field identifier, if this is a field edge.
     * @return a non-{@code null} object if and only if this is a field edge
     */
    public @Nullable String getField() {
        return this.field;
    }

    /** Algebraic operator, if this is an operator edge. */
    private String field;

    /** Sets the incoming multiplicity for this edge. */
    private void setInMult(Multiplicity inMult) {
        this.inMult = inMult;
    }

    /** Returns the incoming multiplicity of this (type) edge, if any. */
    public Multiplicity getInMult() {
        return this.inMult;
    }

    /** The incoming multiplicity of this (type) edge.
     * {@code null} if there is no incoming multiplicity declared.
     */
    private Multiplicity inMult;

    /** Sets the outgoing multiplicity for this edge. */
    private void setOutMult(Multiplicity outMult) {
        this.outMult = outMult;
    }

    /** Returns the outgoing multiplicity of this (type) edge, if any. */
    public Multiplicity getOutMult() {
        return this.outMult;
    }

    /** The outgoing multiplicity of this (type) edge.
     * {@code null} if there is no outgoing multiplicity declared.
     */
    private Multiplicity outMult;

    @Override
    protected int computeHashCode() {
        return super.computeHashCode() + 31 * getGraph().externalHashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        assert obj != null;
        return getGraph().externalEquals(((AspectEdge) obj).getGraph());
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
    /** Label of an {@link AspectKind#CONNECT} edge. */
    static private final String CONNECT_LABEL = "+";
    /** Debug flag. */
    //static private final boolean DEBUG = false;
}
