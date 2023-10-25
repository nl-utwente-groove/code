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

import static nl.utwente.groove.grammar.aspect.AspectKind.ADDER;
import static nl.utwente.groove.grammar.aspect.AspectKind.COLOR;
import static nl.utwente.groove.grammar.aspect.AspectKind.CREATOR;
import static nl.utwente.groove.grammar.aspect.AspectKind.EMBARGO;
import static nl.utwente.groove.grammar.aspect.AspectKind.NESTED;
import static nl.utwente.groove.grammar.aspect.AspectKind.PARAM_ASK;
import static nl.utwente.groove.grammar.aspect.AspectKind.PARAM_IN;
import static nl.utwente.groove.grammar.aspect.AspectKind.PRODUCT;
import static nl.utwente.groove.grammar.aspect.AspectKind.READER;

import java.awt.Color;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.Constant;
import nl.utwente.groove.algebra.Sort;
import nl.utwente.groove.algebra.syntax.ExprTree;
import nl.utwente.groove.algebra.syntax.Expression;
import nl.utwente.groove.grammar.aspect.AspectContent.ColorContent;
import nl.utwente.groove.grammar.aspect.AspectContent.ConstContent;
import nl.utwente.groove.grammar.aspect.AspectContent.ExprContent;
import nl.utwente.groove.grammar.aspect.AspectContent.LabelPatternContent;
import nl.utwente.groove.grammar.aspect.AspectContent.NestedValue;
import nl.utwente.groove.grammar.aspect.AspectContent.NestedValueContent;
import nl.utwente.groove.grammar.aspect.AspectContent.NullContent;
import nl.utwente.groove.grammar.aspect.AspectKind.Category;
import nl.utwente.groove.grammar.rule.OperatorNode;
import nl.utwente.groove.grammar.rule.VariableNode;
import nl.utwente.groove.grammar.type.LabelPattern;
import nl.utwente.groove.graph.ANode;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.graph.plain.PlainLabel;
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
            case NESTING:
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
                setSort(aspect.getKind().getSort());
                AspectContent content = aspect.getContent();
                if (content instanceof ConstContent c) {
                    assert hasGraphRole(GraphRole.HOST);
                    setValue(c.get());
                } else if (content instanceof ExprContent e) {
                    assert hasGraphRole(GraphRole.RULE);
                    setExprTree(e.get());
                } else {
                    assert content instanceof NullContent;
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
        if (hasGraphRole(GraphRole.RULE) && !has(Category.NESTING) && !has(AspectKind.REMARK)
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
                checkSignature(errors);
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
        getAspects()
            .values()
            .stream()
            .filter(a -> !result.has(a.getCategory()))
            .forEach(result::set);
        return result;
    }

    @Override
    public FormatErrorSet getErrors() {
        return this.errors;
    }

    /** List of syntax errors in this node. */
    private final FormatErrorSet errors = new FormatErrorSet();

    /**
     * Analyzes the outgoing edge of this (product) node to
     * compute the arguments and operators.
     */
    private void checkSignature(FormatErrorSet errors) {
        assert has(Status.CHECKING);
        var outEdges = getGraph().outEdgeSet(this);
        var argEdges = new HashSet<AspectEdge>();
        outEdges.stream().filter(AspectEdge::isArgument).forEach(argEdges::add);
        if (argEdges.isEmpty()) {
            errors.add("Product node has no arguments");
        }
        var opEdges = new HashSet<AspectEdge>();
        outEdges.stream().filter(AspectEdge::isOperator).forEach(opEdges::add);
        if (opEdges.isEmpty()) {
            errors.add("Product node has no operators");
        }
        // compute the highest parameter index
        int maxParIndex = -1;
        for (var argEdge : argEdges) {
            int index = argEdge.getArgument();
            maxParIndex = Math.max(maxParIndex, index);
        }
        var argEdgeList = new AspectEdge[maxParIndex + 1];
        // fill the index list
        for (var argEdge : argEdges) {
            int index = argEdge.getArgument();
            if (argEdgeList[index] != null) {
                errors.add("Duplicate %s-edge", argEdge.label(), argEdge, argEdgeList[index]);
            }
            argEdgeList[index] = argEdge;
        }
        // check for missing arguments and fill the argNodes map
        var argNodes = new ArrayList<AspectNode>();
        for (var i = 0; i <= maxParIndex; i++) {
            var argEdge = argEdgeList[i];
            if (argEdge == null) {
                errors.add("Missing product node argument %s", i);
            } else {
                argNodes.add(argEdge.target());
            }
        }
        if (errors.isEmpty()) {
            var signature = argNodes.stream().map(AspectNode::getSort).collect(Collectors.toList());
            for (var opEdge : opEdges) {
                var operator = opEdge.getOperator();
                assert operator != null;
                var opSignature = operator.getParamTypes();
                if (!opSignature.equals(signature)) {
                    errors
                        .add("Product node signature %s does not equal '%s' signature %s",
                             signature, operator, opSignature, opEdge);
                }
            }
            this.argNodes = argNodes;
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

    /** Sets the sort of this node. */
    private void setSort(Sort sort) {
        this.sort = sort;
    }

    /** Returns the sort of this node, if any. */
    @Override
    public @Nullable Sort getSort() {
        return this.sort;
    }

    private @Nullable Sort sort;

    /** Sets the ID of this node.
     * @throws FormatException if the ID has already been set.
     */
    private void setId(String id) throws FormatException {
        this.id = id;
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

    /** Sets an expression on this node.
     * Also notifies the containing aspect graph that it is no longer normal
     */
    private void setExprTree(ExprTree exprTree) {
        this.exprTree = exprTree;
        if (!exprTree.hasConstant()) {
            getGraph().setNonNormal();
        }
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
                result = exprTree.toExpression(getSort(), getGraph().getTyping());
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

    /** Mapping from nested values to targets of outgoing edges with that value. */
    private Map<NestedValue,AspectEdge> nestedMap = new EnumMap<>(NestedValue.class);

    /** Sets an outgoing edge with {@link AspectKind#NESTED} aspect. */
    void setNestingEdge(AspectEdge edge) throws FormatException {
        assert this == edge.source();
        var content = (NestedValueContent) edge.getContent(NESTED);
        assert content != null;
        var value = content.get();
        // check for circularity in the nesting hierarchy
        if (value == NestedValue.IN) {
            var ancestors = new HashSet<AspectEdge>();
            var traverse = edge;
            while (traverse != null && traverse.target() != this) {
                ancestors.add(traverse);
                traverse = traverse.target().getNestedEdge(value);
            }
            if (traverse != null) {
                ancestors.add(traverse);
                throw new FormatException("Circularity in the nesting hierarchy", ancestors);
            }
        }
        var old = this.nestedMap.put(value, edge);
        if (old != null) {
            throw new FormatException("Duplicate '%s'-edges", value, edge, old, this);
        }
    }

    /** Resets an outgoing edge with {@link AspectKind#NESTED} aspect.
     */
    void resetNestingEdge(AspectEdge edge) {
        assert this == edge.source();
        var content = (NestedValueContent) edge.getContent(NESTED);
        assert content != null;
        this.nestedMap.remove(content.get());
    }

    /** Sets the nesting level edge of this node.
     * @param edge outgoing parent edge of type {@link NestedValue#AT}
     * @throws FormatException if the level edge is incompatible with other aspects
    private void setLevelEdge(AspectEdge edge) throws FormatException {
        if (has(Category.NESTING)) {
            throw new FormatException("Source node of %s-edge should be rule element", edge.label(),
                edge);
        }
        if (this.levelEdge != null) {
            throw new FormatException("Duplicate '%s'-edges", edge.label(), edge, this.levelEdge);
        }
        this.levelEdge = edge;
    }
     */

    /** Returns an outgoing nesting edge with a given nested value, if any. */
    private @Nullable AspectEdge getNestedEdge(NestedValue value) {
        return this.nestedMap.get(value);
    }

    /** Returns the target node of a given nested value edge, if any. */
    private @Nullable AspectNode getNested(NestedValue value) {
        var edge = getNestedEdge(value);
        return edge == null
            ? null
            : edge.target();
    }

    /**
     * Retrieves the nesting level of this aspect node.
     * Only non-{@code null} if this node is a rule node.
     */
    public @Nullable AspectNode getLevelNode() {
        return getNested(NestedValue.AT);
    }

    /**
     * Returns the identifier of the nesting level, if any.
     */
    public String getLevelName() {
        var levelNode = getLevelNode();
        return levelNode == null
            ? null
            : levelNode.getId();
    }

    /** Sets the nesting level parent of this node.
     * @param edge outgoing parent edge of type {@link NestedValue#IN}
     * @throws FormatException if the parent edge is incompatible with other aspects
    private void setParentEdge(AspectEdge edge) throws FormatException {
        if (!has(Category.NESTING)) {
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
     */

    /**
     * Retrieves the parent of this node in the nesting hierarchy.
     * Only non-{@code null} if this node is a quantifier node.
     */
    public @Nullable AspectNode getParentNode() {
        return getNested(NestedValue.IN);
    }

    /** Adds a match count edge to this (quantifier) node.
     * @param edge  outgoing count edge of type {@link NestedValue#COUNT}
     * @throws FormatException if the count edge is incompatible with other aspects
    private void setMatchCount(AspectEdge edge) throws FormatException {
        if (!has(FORALL) && !has(FORALL_POS)) {
            throw new FormatException("Source node of %s-edge should be universal quantifier",
                edge.label());
        }
        this.matchCountList.add(edge.target());
    }
     */

    /**
     * Retrieves a nodes encapsulating the match count for this node.
     * @return the first match count node registered for this node, or {@code null}
     * if there are none such
     */
    public AspectNode getMatchCount() {
        return getNested(NestedValue.COUNT);
    }
}
