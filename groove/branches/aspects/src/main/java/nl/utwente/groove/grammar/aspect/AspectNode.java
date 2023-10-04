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

import static nl.utwente.groove.grammar.aspect.AspectKind.ADDER;
import static nl.utwente.groove.grammar.aspect.AspectKind.COLOR;
import static nl.utwente.groove.grammar.aspect.AspectKind.CREATOR;
import static nl.utwente.groove.grammar.aspect.AspectKind.EMBARGO;
import static nl.utwente.groove.grammar.aspect.AspectKind.FORALL;
import static nl.utwente.groove.grammar.aspect.AspectKind.FORALL_POS;
import static nl.utwente.groove.grammar.aspect.AspectKind.PARAM_ASK;
import static nl.utwente.groove.grammar.aspect.AspectKind.PARAM_IN;
import static nl.utwente.groove.grammar.aspect.AspectKind.PRODUCT;
import static nl.utwente.groove.grammar.aspect.AspectKind.READER;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.Constant;
import nl.utwente.groove.algebra.Operator;
import nl.utwente.groove.algebra.Sort;
import nl.utwente.groove.algebra.syntax.ExprTree;
import nl.utwente.groove.algebra.syntax.Expression;
import nl.utwente.groove.grammar.aspect.AspectContent.ColorContent;
import nl.utwente.groove.grammar.aspect.AspectContent.ConstContent;
import nl.utwente.groove.grammar.aspect.AspectContent.ExprContent;
import nl.utwente.groove.grammar.aspect.AspectContent.LabelPatternContent;
import nl.utwente.groove.grammar.aspect.AspectContent.NestedValue;
import nl.utwente.groove.grammar.aspect.AspectContent.NestedValueContent;
import nl.utwente.groove.grammar.aspect.AspectKind.Category;
import nl.utwente.groove.grammar.rule.OperatorNode;
import nl.utwente.groove.grammar.rule.VariableNode;
import nl.utwente.groove.grammar.type.LabelPattern;
import nl.utwente.groove.graph.ANode;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.graph.plain.PlainLabel;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.Fixable;
import nl.utwente.groove.util.LazyFactory;
import nl.utwente.groove.util.line.Line;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Graph node implementation that supports aspects.
 * @author Arend Rensink
 * @version $Revision$
 */
public class AspectNode extends ANode implements AspectElement, Fixable {
    /** Constructs an aspect node with a given number. */
    public AspectNode(int nr, AspectGraph graph) {
        super(nr);
        assert graph.getRole().inGrammar();
        this.graph = graph;
        this.aspects = new Aspect.Map(true, graph.getRole());
    }

    @Override
    public AspectGraph getGraph() {
        return this.graph;
    }

    /** The aspect graph to which this element belongs. */
    private final AspectGraph graph;

    /**
     * This class does not guarantee unique representatives for the same number,
     * so we need to override {@link #hashCode()} and {@link #equals(Object)}.
     */
    @Override
    protected int computeHashCode() {
        return getNumber() ^ getClass().hashCode();
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

    /**
     * Use the same prefix as for default nodes, so the error messages
     * remain understandable.
     */
    @Override
    protected String getToStringPrefix() {
        if (has(Category.SORT)) {
            return VariableNode.TO_STRING_PREFIX;
        } else if (has(Category.ATTR)) {
            return OperatorNode.TO_STRING_PREFIX;
        } else {
            return super.getToStringPrefix();
        }
    }

    /** Adds an aspect label to this node.
     * Also adds the label aspects to the aspects of this node,
     * and the label errors to the errors in this node.
     */
    public void addLabel(AspectLabel label) {
        assert label.isFixed();
        assert getGraphRole() == label.getGraphRole();
        assert !isParsed();
        getNodeLabels().add(label);
        addErrors(label.getErrors());
        label.getAspects().forEach(this::set);
    }

    /**
     * Returns the list of node labels added to this node.
     */
    public List<AspectLabel> getNodeLabels() {
        return this.nodeLabels;
    }

    /** The list of aspect labels defining node aspects. */
    private final List<AspectLabel> nodeLabels = new ArrayList<>();

    /**
     * Returns the list of (plain) labels that should be put on this
     * node in the plain graph view.
     */
    public List<PlainLabel> getPlainLabels() {
        List<PlainLabel> result = new ArrayList<>();
        for (AspectLabel label : this.nodeLabels) {
            String text = label.toString();
            if (text.length() > 0) {
                result.add(PlainLabel.parseLabel(text));
            }
        }
        return result;
    }

    @Override
    public Aspect.Map getAspects() {
        return this.aspects;
    }

    @Override
    public void set(Aspect aspect) {
        assert aspect.isForNode(getGraphRole()) : String
            .format("Inappropriate node aspect %s", aspect);
        try {
            getAspects().add(aspect);
            var cat = aspect.getCategory();
            switch (cat) {
            case COLOR:
                setColor(((ColorContent) aspect.getContent()).get());
                break;
            case EDGE:
                setEdgePattern(((LabelPatternContent) aspect.getContent()).get());
                break;
            case ID:
                setId(aspect.getContentString());
                break;
            case META:
                if (aspect.hasContent()) {
                    setId(aspect.getContentString());
                }
                break;
            case ROLE:
                if (aspect.hasContent()) {
                    throw new FormatException("Node aspect %s should not have quantifier name",
                        aspect, this);
                }
                break;
            case SORT:
                AspectContent content = aspect.getContent();
                if (content instanceof ConstContent c) {
                    assert hasGraphRole(GraphRole.HOST);
                    setValue(c.get());
                } else if (content instanceof ExprContent e) {
                    assert hasGraphRole(GraphRole.RULE);
                    setExprTree(e.get());
                }
                break;
            default:
                // no additional actions needed
            }
        } catch (FormatException exc) {
            addErrors(exc.getErrors());
        }
    }

    /** The initially empty aspect map. */
    private final Aspect.Map aspects;

    @Override
    public void parseAspects() throws FormatException {
        // READER role should be made explicit
        if (hasGraphRole(GraphRole.RULE) && !has(Category.META) && !has(AspectKind.REMARK)
            && !has(Category.ROLE)) {
            set(READER.getAspect());
        }
    }

    @Override
    public void checkAspects() throws FormatException {
        var errors = new FormatErrorSet();
        switch (getGraphRole()) {
        case RULE:
            if (has(PARAM_ASK) && !has(Category.SORT)) {
                errors.add("User-provided parameter must be a data value");
            }
            if (has(PARAM_IN) && has(CREATOR)) {
                errors.add("Input parameter can't be %s", get(Category.ROLE));
            }
            if (has(Category.PARAM) && (has(EMBARGO) || has(ADDER))) {
                errors.add("Parameter can't be %s", get(Category.ROLE));
            }
            if (has(Category.SORT) && !has(READER) && !has(EMBARGO)) {
                errors.add("Data node can't be %s", get(Category.ROLE));
            }
            if (has(PRODUCT)) {
                if (!has(READER) && !has(EMBARGO)) {
                    errors.add("Product node can't be %s", getKind(Category.ROLE));
                }
                if (hasId()) {
                    errors.add("Node identifier ('%s') not allowed for product node", getId());
                }
                var argNodes = this.argNodes;
                var operator = this.operator;
                if (argNodes == null) {
                    errors.add("Product node has no arguments");
                } else if (operator == null) {
                    errors.add("Product node has no operators");
                } else {
                    int arity = argNodes.size();
                    if (arity != operator.getArity()) {
                        errors
                            .add("Product node arity %d is incorrect for operator %s", arity,
                                 operator);
                    }
                    for (int i = 0; i < arity; i++) {
                        AspectNode argNode = this.argNodes.get(i);
                        if (argNode == null) {
                            errors.add("Missing product argument %d", i);
                        }
                    }
                }
            }
            if (has(COLOR) && (has(EMBARGO) || has(AspectKind.ERASER))) {
                errors
                    .add("Conflicting aspects %s and %s", getKind(Category.COLOR),
                         getKind(Category.ROLE), this);
            }
            break;
        case HOST:
            if (has(Category.SORT) && hasId()) {
                errors
                    .add("Node identifier ('%s') not allowed for value node %s", getId(),
                         getValue());
            }
            break;
        default:
            // no other checks
        }
        addErrors(errors);
    }

    @Override
    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public Status getStatus() {
        return this.status;
    }

    /** Indicates that the entire node is fixed. */
    private Status status = Status.NEW;

    /**
     * Creates a clone of this node, for a given aspect graph.
     * The clone is not yet parsed.
     * @param graph the graph to which the new node belongs
     */
    public AspectNode clone(AspectGraph graph) {
        return clone(graph, getNumber());
    }

    /**
     * Clones an {@link AspectNode}, and also renumbers it.
     * The clone is not yet parsed.
     * @param graph the graph to which the new node belongs
     * @param newNr the number for the new aspect node.
     */
    public AspectNode clone(AspectGraph graph, int newNr) {
        AspectNode result = new AspectNode(newNr, graph);
        getNodeLabels().forEach(result::addLabel);
        return result;
    }

    @Override
    public FormatErrorSet getErrors() {
        return this.errors;
    }

    /** List of syntax errors in this node. */
    private final FormatErrorSet errors = new FormatErrorSet();

    /** Adds an argument to this node, based on an outgoing argument edge.
     * This is only correct if this is a {@link AspectKind#PRODUCT} node,
     * and no argument with this index has yet been added.
     * @param argEdge the argument to be added
     * @throws FormatException if the argument is not compatible with other aspects
     */
    void addArgument(AspectEdge argEdge) throws FormatException {
        set(PRODUCT.getAspect());
        var argNodes = this.argNodes;
        if (argNodes == null) {
            argNodes = this.argNodes = new ArrayList<>();
        }
        int index = argEdge.getArgument();
        // extend the list if necessary
        while (argNodes.size() <= index) {
            argNodes.add(null);
        }
        if (argNodes.get(index) != null) {
            throw new FormatException("Duplicate %s-edge", argEdge.label());
        }
        argNodes.set(index, argEdge.target());
        // infer target type if an operator edge is already present
        var operator = getOperator();
        if (operator != null) {
            List<Sort> paramTypes = operator.getParamTypes();
            if (index < paramTypes.size()) {
                argEdge.target().set(Aspect.getAspect(paramTypes.get(index)));
            }
        }
    }

    /**
     * If this is a product node, returns the list of
     * argument nodes reached by outgoing argument edges.
     * Should only be called after the node has been fixed
     * @return an ordered list of argument nodes, or {@code null} if
     * this is not a product node.
     */
    public List<AspectNode> getArgNodes() {
        testFixed(true);
        return this.argNodes;
    }

    /** A list of argument types, if this represents a product node. */
    private List<AspectNode> argNodes;

    /** Sets an operator on this node, based on an outgoing operator edge.
     * This is only correct if this is a {@link AspectKind#PRODUCT} node.
     * @param opEdge the operator to be added
     * @throws FormatException if the operator is not compatible with other aspects
     */
    void setOperator(AspectEdge opEdge) throws FormatException {
        var operator = this.operator;
        var argNodes = this.argNodes;
        if (operator == null) {
            this.operator = opEdge.getOperator();
        } else if (!operator.getParamTypes().equals(opEdge.getOperator().getParamTypes())) {
            throw new FormatException("Conflicting operator signatures for %s and %s", operator,
                opEdge.getOperator(), this);
        } else if (!hasErrors() && argNodes != null) {
            // only go here if there are no (signature) errors
            // infer operand types of present argument edges
            for (int i = 0; i < argNodes.size(); i++) {
                AspectNode argNode = argNodes.get(i);
                if (argNode != null) {
                    Sort paramType = operator.getParamTypes().get(i);
                    argNode.set(Aspect.getAspect(paramType));
                }
            }
        }
    }

    /** Returns the first operator registered for this (product) node.
     * This is stored to check compatibility of source types for the operators.
     */
    private @Nullable Operator getOperator() {
        return this.operator;
    }

    /** The first operator registered for this (product) node. */
    private @Nullable Operator operator;

    /** Sets the ID of this node.
     * @throws FormatException if the ID has already been set.
     */
    private void setId(String id) throws FormatException {
        if (hasId()) {
            throw new FormatException("Duplicate node identifiers '%s' and '%s'", getId(), id);
        }
        this.id = id;
        getGraph().notifyNodeId(this);
    }

    /** Returns the ID of this node, if any. */
    public @Nullable String getId() {
        return this.id;
    }

    /** Indicates if this node has an identifier. */
    public boolean hasId() {
        return this.id != null;
    }

    /** The ID of this node, if any. */
    private @Nullable String id;

    /** Sets the colour aspect of this node. */
    private void setColor(Color color) {
        this.color = color;
    }

    /** Checks if a colour has been set. */
    public boolean hasColor() {
        return getColor() != null;
    }

    /** Returns the colour of this node, if the colour aspect has been set. */
    public @Nullable Color getColor() {
        return this.color;
    }

    /** The colour of this node, if any. */
    private @Nullable Color color;

    /** Sets an expression on this node. */
    private void setValue(Constant value) {
        this.value = value;
    }

    /** Checks if there is a constant data value on this node. */
    public boolean hasValue() {
        return getValue() != null;
    }

    /** Returns the constant vaue on this node, if any. */
    public @Nullable Constant getValue() {
        return this.value;
    }

    private @Nullable Constant value;

    /** Returns the line describing the value or expression on this node, if any.
     * Should only be called if {@link #hasValue()} holds.
     */
    public Line getValueLine() {
        var value = getValue();
        assert value != null;
        return value.toLine();
    }

    /** Sets an expression on this node. */
    private void setExprTree(ExprTree exprTree) {
        this.exprTree = exprTree;
    }

    /** Returns the expression on this node, if any. */
    private @Nullable ExprTree getExprTree() {
        return this.exprTree;
    }

    /** The expression tree on this node, if any. */
    private @Nullable ExprTree exprTree;

    /** Checks if there is an expression on this node. */
    public boolean hasExpression() {
        return getExprTree() != null;
    }

    /** Returns the expression on this node, if any. */
    public @Nullable Expression getExpression() {
        return this.expression.get();
    }

    /** Creates an expression for this node, if the {@link ExprTree} is set
     * and there are no errors in typing it. */
    private Expression createExpression() {
        Expression result = null;
        var exprTree = getExprTree();
        if (exprTree != null) {
            try {
                result = exprTree.toExpression(getGraph().getTyping());
            } catch (FormatException exc) {
                addErrors(exc.getErrors());
            }
        }
        return result;
    }

    /** The expression on this node, if any. */
    private LazyFactory<Expression> expression = LazyFactory.instance(this::createExpression);

    /** Returns the line displaying the expression, if any.
     * Should only be called if {@link #hasExpression()} holds.
     */
    public Line getExpressionLine() {
        var exprTree = getExprTree();
        assert exprTree != null;
        Expression expression = getExpression();
        return expression == null
            ? exprTree.toLine()
            : expression.toLine();
    }

    /** Sets the edge label pattern of this node. */
    private void setEdgePattern(LabelPattern edge) {
        this.edgePattern = edge;
    }

    /** Returns the edge label pattern of this node, if any. */
    public LabelPattern getEdgePattern() {
        return this.edgePattern;
    }

    /** The edge label pattern of this node, if any. */
    private LabelPattern edgePattern;

    /** Sets an outgoing edge with {@link AspectKind#NESTED} aspect. */
    void setNestingEdge(AspectEdge edge) throws FormatException {
        assert this == edge.source();
        var nestedContent = (NestedValueContent) edge.getContent(AspectKind.NESTED);
        assert nestedContent != null;
        switch (nestedContent.get()) {
        case AT -> setLevelEdge(edge);
        case COUNT -> setMatchCount(edge);
        case IN -> setParentEdge(edge);
        default -> throw Exceptions.UNREACHABLE;
        }
    }

    /** Sets the nesting level edge of this node.
     * @param edge outgoing parent edge of type {@link NestedValue#AT}
     * @throws FormatException if the level edge is incompatible with other aspects
     */
    private void setLevelEdge(AspectEdge edge) throws FormatException {
        if (has(Category.META)) {
            throw new FormatException("Source node of %s-edge should be rule element", edge.label(),
                edge);
        }
        if (this.levelEdge != null) {
            throw new FormatException("Duplicate '%s'-edges", edge.label(), edge, this.levelEdge);
        }
        this.levelEdge = edge;
    }

    /**
     * Retrieves the edge to the nesting level of this aspect node.
     * Only non-{@code null} if this node is a rule node.
     */
    public @Nullable AspectEdge getLevelEdge() {
        return this.levelEdge;
    }

    /** The edge pointing to the nesting level of this node, if any. */
    private @Nullable AspectEdge levelEdge;

    /**
     * Returns the identifier of the nesting level, if any.
     */
    public String getLevelName() {
        var levelNode = getLevelNode();
        return levelNode == null
            ? null
            : levelNode.getId();
    }

    /**
     * Retrieves the nesting level of this aspect node.
     * Only non-{@code null} if this node is a rule node.
     */
    public @Nullable AspectNode getLevelNode() {
        AspectEdge edge = getLevelEdge();
        return edge == null
            ? null
            : edge.target();
    }

    /** Sets the nesting level parent of this node.
     * @param edge outgoing parent edge of type {@link NestedValue#IN}
     * @throws FormatException if the parent edge is incompatible with other aspects
     */
    private void setParentEdge(AspectEdge edge) throws FormatException {
        if (!has(Category.META)) {
            throw new FormatException("Source node of %s-edge should be quantifier", edge.label());
        }
        if (this.parentEdge != null) {
            throw new FormatException("Duplicate outgoing '%s'-edges", edge.label(),
                this.parentEdge, edge);
        }
        // collect collective nesting grandparents to test for circularity
        Set<AspectNode> ancestors = new HashSet<>();
        AspectNode parent = edge.target();
        while (parent != null) {
            ancestors.add(parent);
            parent = parent.getParentNode();
        }
        if (ancestors.contains(this)) {
            throw new FormatException("Circularity in the nesting hierarchy");
        }
        this.parentEdge = edge;
    }

    /**
     * Retrieves edge to the parent of this node in the nesting hierarchy.
     * Only non-{@code null} if this node is a quantifier node.
     */
    public @Nullable AspectEdge getParentEdge() {
        return this.parentEdge;
    }

    /** The node pointing to the parent of this node in the nesting
     * hierarchy, if any. */
    private @Nullable AspectEdge parentEdge;

    /**
     * Retrieves the parent of this node in the nesting hierarchy.
     * Only non-{@code null} if this node is a quantifier node.
     */
    public @Nullable AspectNode getParentNode() {
        AspectEdge edge = getParentEdge();
        return edge == null
            ? null
            : edge.target();
    }

    /** Adds a match count edge to this (quantifier) node.
     * @param edge  outgoing count edge of type {@link NestedValue#COUNT}
     * @throws FormatException if the count edge is incompatible with other aspects
     */
    private void setMatchCount(AspectEdge edge) throws FormatException {
        if (!has(FORALL) && !has(FORALL_POS)) {
            throw new FormatException("Source node of %s-edge should be universal quantifier",
                edge.label());
        }
        this.matchCountList.add(edge.target());
    }

    /**
     * Retrieves a nodes encapsulating the match count for this node.
     * @return the first match count node registered for this node, or {@code null}
     * if there are none such
     */
    public AspectNode getMatchCount() {
        var matchCountList = getMatchCountList();
        return matchCountList.isEmpty()
            ? null
            : matchCountList.get(0);
    }

    /**
     * Retrieves the nodes encapsulating the match count for this node.
     * Only non-empty if this node is a universal quantifier node.
     */
    public List<AspectNode> getMatchCountList() {
        return this.matchCountList;
    }

    /** The aspect nodes representing the match count of a universal quantifier. */
    private List<AspectNode> matchCountList = new ArrayList<>();
}
