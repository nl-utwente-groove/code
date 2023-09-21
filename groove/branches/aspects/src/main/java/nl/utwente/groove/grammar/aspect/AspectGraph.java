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

import static nl.utwente.groove.grammar.aspect.AspectKind.LET_NEW;
import static nl.utwente.groove.graph.GraphRole.HOST;
import static nl.utwente.groove.graph.GraphRole.RULE;
import static nl.utwente.groove.graph.GraphRole.TYPE;

import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.Constant;
import nl.utwente.groove.algebra.Operator;
import nl.utwente.groove.algebra.Sort;
import nl.utwente.groove.algebra.syntax.Assignment;
import nl.utwente.groove.algebra.syntax.CallExpr;
import nl.utwente.groove.algebra.syntax.Expression;
import nl.utwente.groove.algebra.syntax.FieldExpr;
import nl.utwente.groove.algebra.syntax.Typing;
import nl.utwente.groove.algebra.syntax.Variable;
import nl.utwente.groove.automaton.RegExpr;
import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.aspect.AspectContent.NestedValue;
import nl.utwente.groove.grammar.type.TypeLabel;
import nl.utwente.groove.graph.AElementMap;
import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.EdgeRole;
import nl.utwente.groove.graph.ElementFactory;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.GraphInfo;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.graph.Label;
import nl.utwente.groove.graph.Morphism;
import nl.utwente.groove.graph.Node;
import nl.utwente.groove.graph.NodeComparator;
import nl.utwente.groove.graph.NodeSetEdgeSetGraph;
import nl.utwente.groove.graph.plain.PlainEdge;
import nl.utwente.groove.graph.plain.PlainFactory;
import nl.utwente.groove.graph.plain.PlainGraph;
import nl.utwente.groove.graph.plain.PlainLabel;
import nl.utwente.groove.graph.plain.PlainNode;
import nl.utwente.groove.gui.layout.JVertexLayout;
import nl.utwente.groove.gui.layout.LayoutMap;
import nl.utwente.groove.gui.list.SearchResult;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.Keywords;
import nl.utwente.groove.util.LazyFactory;
import nl.utwente.groove.util.parse.FormatError;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Graph implementation to convert from a label prefix representation of an
 * aspect graph to a graph where the aspect values are stored in
 * {@link AspectNode}s and {@link AspectEdge}s.
 * @author Arend Rensink
 * @version $Revision $
 */
public class AspectGraph extends NodeSetEdgeSetGraph<@NonNull AspectNode,@NonNull AspectEdge> {
    /**
     * Creates an empty graph, with a given qualified name and graph role.
     */
    public AspectGraph(String name, GraphRole graphRole) {
        super(name.toString());
        this.qualName = QualName.parse(name);
        assert graphRole.inGrammar() : String
            .format("Cannot create aspect graph for %s", graphRole.toString());
        this.role = graphRole;
        this.normal = true;
        // make sure the properties object is initialised
        GraphInfo.addErrors(this, this.qualName.getErrors());
    }

    /* Also sets the qualified name. */
    @Override
    public void setName(String name) {
        super.setName(name);
        this.qualName = QualName.parse(name);
        GraphInfo.addErrors(this, this.qualName.getErrors());
    }

    /** Returns the qualified name of this aspect graph. */
    public QualName getQualName() {
        return this.qualName;
    }

    /** Changes the qualified name of this aspect graph. */
    private void setQualName(QualName qualName) {
        this.qualName = qualName;
        super.setName(qualName.toString());
    }

    private QualName qualName;

    /** Adds a given list of errors to the errors already stored in this graph. */
    private void addErrors(Collection<FormatError> errors) {
        GraphInfo.addErrors(this, errors);
    }

    /**
     * Collects search results matching the given label into the given list.
     */
    public void getSearchResults(TypeLabel label, List<SearchResult> results) {
        String msg = getRole().getDescription() + " '%s' - Element '%s'";
        for (AspectEdge edge : edgeSet()) {
            if ((edge.getRuleLabel() != null && label.equals(edge.getRuleLabel().getTypeLabel()))
                || label.equals(edge.getTypeLabel())) {
                results.add(new SearchResult(msg, this.getName(), edge, this));
            }
        }
    }

    /**
     * Creates a graph where the aspect values are represented as label prefixes
     * for the edges, and as special edges for the nodes.
     */
    public PlainGraph toPlainGraph() {
        AspectToPlainMap elementMap = new AspectToPlainMap();
        PlainGraph result = createPlainGraph();
        for (AspectNode node : nodeSet()) {
            PlainNode nodeImage = result.addNode(node.getNumber());
            elementMap.putNode(node, nodeImage);
            for (PlainLabel label : node.getPlainLabels()) {
                result.addEdge(nodeImage, label, nodeImage);
            }
        }
        for (AspectEdge edge : edgeSet()) {
            var image = elementMap.mapEdge(edge);
            assert image != null;
            result.addEdgeContext(image);
        }
        GraphInfo.transfer(this, result, elementMap);
        result.setFixed();
        return result;
    }

    /**
     * Factory method for a <code>Graph</code>.
     * @see #toPlainGraph()
     */
    private PlainGraph createPlainGraph() {
        PlainGraph result = new PlainGraph(getName(), getRole());
        return result;
    }

    /**
     * Returns the normalised aspect graph.
     * An aspect graph is normalised if all {@link AspectKind#LET} and
     * {@link AspectKind#TEST} edges have been substituted by explicit
     * attribute elements.
     * @param map mapping from the replaced elements of this graph to their
     * counterparts in the normalised graph; may be {@code null}
     */
    public AspectGraph normalise(AspectGraphMorphism map) {
        AspectGraph result;
        if (this.normal) {
            result = this;
        } else {
            result = clone();
            result.doNormalise(map);
            result.setFixed();
        }
        return result;
    }

    /**
     * Normalises this (non-fixed) aspect graph.
     * @param map mapping from the replaced elements of this graph to their
     * counterparts in the normalised graph; may be {@code null}
     */
    private void doNormalise(AspectGraphMorphism map) {
        assert !isFixed();
        setNodeComplete();
        // identify and remove let- and test-edges
        Set<AspectEdge> letEdges = new HashSet<>();
        Set<AspectEdge> predEdges = new HashSet<>();
        for (AspectEdge edge : edgeSet()) {
            edge.setFixed();
            if (edge.isPredicate()) {
                predEdges.add(edge);
            } else if (edge.isAssign()) {
                letEdges.add(edge);
            }
        }
        removeEdgeSet(letEdges);
        removeEdgeSet(predEdges);
        // add assignments for the let-edges
        List<FormatError> errors = new ArrayList<>();
        for (AspectEdge edge : letEdges) {
            if (edge.hasErrors()) {
                continue;
            }
            try {
                AspectNode source = edge.source();
                assert !source.getKind().isQuantifier();
                AspectNode level = source.getNestingLevel();
                AspectEdge normalisedEdge
                    = addAssignment(level, source, edge.getAssign(), edge.getKind() == LET_NEW);
                if (map != null) {
                    map.putEdge(edge, normalisedEdge);
                }
            } catch (FormatException e) {
                errors.addAll(e.getErrors());
            }
        }
        // add conditions for the pred-edges
        for (AspectEdge edge : predEdges) {
            if (edge.hasErrors()) {
                continue;
            }
            try {
                AspectNode source = edge.source();
                boolean nac = edge.getKind().inNAC() && !source.getKind().inNAC();
                Expression predicate = edge.getPredicate();
                AspectNode level = source.getKind().isQuantifier()
                    ? source.getNestingParent()
                    : source.getNestingLevel();
                AspectNode outcome = addExpression(level, source, predicate);
                // specify whether the outcome should be true or false
                Constant value = Constant.instance(!nac);
                outcome.setAspects(parser.parse(value.toString(), getRole()));
                //                }
            } catch (FormatException e) {
                errors.addAll(e.getErrors());
            }
        }
        addErrors(errors);
    }

    /**
     * Adds the structure corresponding to an assignment.
     * @param level the nesting level node on which the expression should be computed
     * @param source node on which the expression occurs
     * @param assign the parsed assignment
     * @param isNew flag indicating if the attribute is new (so no eraser need be added for the old value)
     */
    private AspectEdge addAssignment(@Nullable AspectNode level, @NonNull AspectNode source,
                                     Assignment assign, boolean isNew) throws FormatException {
        // add the expression structure
        AspectNode target = addExpression(level, source, assign.getRhs());
        // add a creator edge (for a rule) or normal edge to the assignment target
        String assignLabelText;
        if (getRole() == RULE) {
            AspectKind kind = source.getKind() == AspectKind.ADDER
                ? AspectKind.ADDER
                : AspectKind.CREATOR;
            assignLabelText = kind.getPrefix() + assign.getLhs();
        } else {
            assignLabelText = assign.getLhs();
        }
        AspectLabel assignLabel = parser.parse(assignLabelText, getRole());
        AspectEdge result = addEdge(source, assignLabel, target);
        if (getRole() == RULE && !source.getKind().isCreator() && !isNew) {
            // add an eraser edge for the old value, if this is not LET_NEW
            AspectNode oldTarget = findTarget(source, assign.getLhs(), target.getSort());
            if (oldTarget == null) {
                oldTarget = addNestedNode(level, source);
                // use the type of the new target for the old target node
                Aspect data = target.getDataAspect();
                assert data != null;
                oldTarget.setAspects(createLabel(data.getKind()));
            }
            assignLabel = AspectParser
                .getInstance()
                .parse(AspectKind.ERASER.getPrefix() + assign.getLhs(), getRole());
            addEdge(source, assignLabel, oldTarget);
        }
        return result;
    }

    /**
     * Adds the structure corresponding to an expression.
     * @param level the nesting level node on which the expression should be computed
     * @param source node on which the expression occurs
     * @param expr the parsed expression
     * @return the node holding the value of the expression
     */
    private AspectNode addExpression(@Nullable AspectNode level, @NonNull AspectNode source,
                                     Expression expr) throws FormatException {
        return switch (expr.getKind()) {
        case CONST -> addConstant(source, expr);
        case FIELD -> addField(level, source, (FieldExpr) expr);
        case CALL -> getRole() == HOST
            ? addConstant(source, expr)
            : addCall(level, source, (CallExpr) expr);
        case VAR -> addVar(source, (Variable) expr);
        };
    }

    /**
     * Adds the structure corresponding to a constant.
     * @param source the node on which the constant was specified
     * @param constant the constant for which we add a node
     * @return the node representing the constant
     */
    private AspectNode addConstant(@NonNull AspectNode source,
                                   Expression constant) throws FormatException {
        AspectNode result = addNode();
        if (!(constant instanceof Constant)) {
            throw new FormatException("Expression '%s' not allowed as constant value",
                constant.toParseString(), source);
        }
        result.setAspects(parser.parse(constant.toString(), getRole()));
        return result;
    }

    /**
     * Creates the target of a field expression.
     * @param level the nesting level node on which the expression should be computed
     * @param source the node which currently has the field
     * @param expr the field expression
     * @return the target node of the identifier
     */
    private AspectNode addField(@Nullable AspectNode level, @NonNull AspectNode source,
                                FieldExpr expr) throws FormatException {
        if (getRole() != RULE) {
            throw new FormatException("Assignment expression '%s' only allowed in rules",
                expr.toDisplayString(), source);
        }
        // look up the field owner
        AspectNode owner;
        String ownerName = expr.getTarget();
        if (ownerName == null || ownerName.equals(Keywords.SELF)) {
            owner = source;
        } else {
            owner = this.nodeIdMap.get(ownerName);
            if (owner == null) {
                throw new FormatException(
                    "Unknown node identifier '%s' in assignment expression '%s'", ownerName,
                    expr.toDisplayString(), source);
            }
        }
        if (owner.getKind().isCreator()) {
            throw new FormatException("Unassigned creator node field in assignment expression '%s'",
                expr.toDisplayString(), owner);
        }
        if (owner.getKind().isQuantifier()
            && !expr.getField().equals(NestedValue.COUNT.toString())) {
            throw new FormatException("Quantifier node does not have '%s'-edge", expr.getField(),
                owner, source);
        }
        // look up the field
        Sort exprSort = expr.getSort();
        AspectNode result = findTarget(owner, expr.getField(), exprSort);
        if (result == null) {
            result = addNestedNode(level, source);
            result.setAspects(createLabel(AspectKind.toAspectKind(exprSort)));
        } else {
            if (result.getSort() != exprSort) {
                throw new FormatException("Declared type %s differs from actual field type %s",
                    exprSort, result.getSort(), source);
            }
        }
        AspectLabel idLabel = parser.parse(expr.getField(), getRole());
        addEdge(owner, idLabel, result).setFixed();
        return result;
    }

    /** Looks for an outgoing edge suitable for a given field expression. */
    private @Nullable AspectNode findTarget(@NonNull AspectNode owner, String fieldName,
                                            Sort sort) {
        boolean allEdgesOK = getRole() != RULE || owner.getKind().isQuantifier();
        Optional<AspectNode> result = outEdgeSet(owner)
            .stream()
            .filter(e -> allEdgesOK || e.getKind().inLHS())
            .filter(e -> e.getInnerText().equals(fieldName))
            .map(AspectEdge::target)
            .filter(n -> n.getSort() == sort)
            .findAny();
        return result.orElse(null);
    }

    /**
     * Adds the structure for a call expression
     * @param level the nesting level node on which the expression should be computed
     * @param source node on which the expression occurs
     * @param call the call expression
     * @return the node representing the value of the expression
     */
    private AspectNode addCall(@Nullable AspectNode level, @NonNull AspectNode source,
                               CallExpr call) throws FormatException {
        Operator operator = call.getOperator();
        if (getRole() != RULE) {
            throw new FormatException("Call expression '%s' only allowed in rules",
                call.toParseString(), source);
        }
        AspectNode result = addNestedNode(level, source);
        result.setAspects(createLabel(AspectKind.toAspectKind(call.getSort())));
        if (operator.isSetOperator()) {
            Expression arg = call.getArgs().get(0);
            level = getLevel(arg);
            if (level == null || source.getNestingLevel() != level.getNestingParent()) {
                throw new FormatException(
                    "Set operator argument '%s' should be one level deeper than source node '%s'",
                    arg, source);
            }
        }
        AspectNode product = addNestedNode(level, source);
        product.setAspects(createLabel(AspectKind.PRODUCT));
        // add the operator edge
        AspectLabel operatorLabel = parser.parse(operator.getFullName(), getRole());
        addEdge(product, operatorLabel, result);
        // add the arguments
        List<nl.utwente.groove.algebra.syntax.Expression> args = call.getArgs();
        for (int i = 0; i < args.size(); i++) {
            AspectNode argResult = addExpression(level, source, args.get(i));
            AspectLabel argLabel = parser.parse(AspectKind.ARGUMENT.getPrefix() + i, getRole());
            addEdge(product, argLabel, argResult);
        }
        return result;
    }

    /**
     * Adds the structure for a variable expression.
     * @param var the variable expression
     * @return the node representing the value of the expression
     */
    private AspectNode addVar(@NonNull AspectNode source,
                              @NonNull Variable var) throws FormatException {
        if (getRole() != RULE) {
            throw new FormatException("Field expression '%s' only allowed in rules",
                var.toDisplayString(), source);
        }
        // look up the field owner
        AspectNode result;
        String name = var.getName();
        result = this.nodeIdMap.get(name);
        if (result == null) {
            throw new FormatException("Unknown node identifier '%s'", name, source);
        }
        return result;
    }

    /** Adds a node on a given nesting level with NAC derived from a source node.
     * @param level the nesting level node on which the node should be created
     * @param source node from which the NAC aspect should be copied
     */
    private AspectNode addNestedNode(@Nullable AspectNode level,
                                     @NonNull AspectNode source) throws FormatException {
        AspectNode result = addNode();
        if (source.getKind() == AspectKind.EMBARGO) {
            result.setAspect(source.getAspect());
        }
        if (level != null) {
            addEdge(result, NestedValue.AT.toString(), level);
        }
        return result;
    }

    /** Returns the nesting level on which an expression can be evaluated, in terms
     * of the associated quantifier node.
     * @throws FormatException if there is no common nesting level for all sub-expressions
     */
    private AspectNode getLevel(Expression expr) throws FormatException {
        AspectNode result = null;
        switch (expr.getKind()) {
        case CALL:
            for (Expression sub : ((CallExpr) expr).getArgs()) {
                AspectNode subLevel = getLevel(sub);
                if (result == null) {
                    result = subLevel;
                    continue;
                } else if (subLevel == null || isChild(result, subLevel)) {
                    continue;
                } else if (isChild(subLevel, result)) {
                    result = subLevel;
                    continue;
                }
                throw new FormatException(
                    "Incompatible quantified nodes '%s' and '%s' in expression '%s'", result,
                    subLevel, expr);
            }
            break;
        case CONST:
            break;
        case FIELD:
            AspectNode target = this.nodeIdMap.get(((FieldExpr) expr).getTarget());
            if (target != null) {
                result = target.getNestingLevel();
            }
            break;
        default:
            throw Exceptions.UNREACHABLE;
        }
        return result;
    }

    private boolean isChild(AspectNode child, AspectNode parent) {
        boolean result = child.equals(parent);
        while (!result && child != null) {
            child = child.getNestingParent();
        }
        return result;
    }

    /** Callback method to create an aspect label out of an aspect kind. */
    private AspectLabel createLabel(AspectKind kind) {
        return parser.parse(kind.getPrefix(), getRole());
    }

    /**
     * Returns a new aspect graph obtained from this one
     * by renumbering the nodes in a consecutive sequence starting from {@code 0}
     */
    public AspectGraph renumber() {
        AspectGraph result = this;
        // renumber the nodes in their original order
        SortedSet<AspectNode> nodes = new TreeSet<>(NodeComparator.instance());
        nodes.addAll(nodeSet());
        if (!nodes.isEmpty() && nodes.last().getNumber() != nodeCount() - 1) {
            result = newGraph(getName());
            AspectGraphMorphism elementMap = new AspectGraphMorphism(this);
            int nodeNr = 0;
            for (AspectNode node : nodes) {
                AspectNode image = result.addNode(nodeNr);
                for (AspectLabel label : node.getNodeLabels()) {
                    image.setAspects(label);
                }
                elementMap.putNode(node, image);
                nodeNr++;
            }
            for (AspectEdge edge : edgeSet()) {
                var image = elementMap.mapEdge(edge);
                assert image != null;
                result.addEdgeContext(image);
            }
            GraphInfo.transfer(this, result, elementMap);
            result.setFixed();
        }
        return result;
    }

    /**
     * Returns an aspect graph obtained from this one by changing all
     * occurrences of a certain label into another.
     * @param oldLabel the label to be changed
     * @param newLabel the new value for {@code oldLabel}
     * @return a clone of this aspect graph with changed labels, or this graph
     *         if {@code oldLabel} did not occur
     */
    public AspectGraph relabel(TypeLabel oldLabel, TypeLabel newLabel) {
        // create a plain graph under relabelling
        PlainGraph result = createPlainGraph();
        AspectToPlainMap elementMap = new AspectToPlainMap();
        // flag registering if anything changed due to relabelling
        boolean graphChanged = false;
        for (AspectNode node : nodeSet()) {
            PlainNode image = result.addNode(node.getNumber());
            elementMap.putNode(node, image);
            for (AspectLabel oldNodeLabel : node.getNodeLabels()) {
                AspectLabel newNodeLabel = oldNodeLabel.relabel(oldLabel, newLabel);
                newNodeLabel.setFixed();
                graphChanged |= newNodeLabel != oldNodeLabel;
                String text = newNodeLabel.toString();
                assert !text.isEmpty();
                result.addEdge(image, PlainLabel.parseLabel(text), image);
            }
        }
        for (AspectEdge edge : edgeSet()) {
            String replacement = null;
            if (edge.getRuleLabel() != null) {
                RegExpr oldLabelExpr = edge.getRuleLabel().getMatchExpr();
                RegExpr newLabelExpr = oldLabelExpr.relabel(oldLabel, newLabel);
                if (newLabelExpr != oldLabelExpr) {
                    replacement = newLabelExpr.toString();
                }
            } else if (oldLabel.equals(edge.getTypeLabel())) {
                replacement = newLabel.toParsableString();
            }
            AspectLabel edgeLabel = edge.label();
            AspectLabel newEdgeLabel = edgeLabel.relabel(oldLabel, newLabel);
            // force a new object if the inner text has to change
            if (replacement != null && newEdgeLabel == edgeLabel) {
                newEdgeLabel = edgeLabel.clone();
            }
            if (newEdgeLabel != edgeLabel) {
                graphChanged = true;
                if (replacement != null) {
                    newEdgeLabel.setInnerText(replacement);
                }
                newEdgeLabel.setFixed();
                edgeLabel = newEdgeLabel;
            }
            PlainNode sourceImage = elementMap.getNode(edge.source());
            PlainNode targetImage = elementMap.getNode(edge.target());
            PlainEdge edgeImage = result.addEdge(sourceImage, edgeLabel.toString(), targetImage);
            elementMap.putEdge(edge, edgeImage);
        }
        if (!graphChanged) {
            return this;
        } else {
            GraphInfo.transfer(this, result, elementMap);
            result.setFixed();
            return newInstance(result);
        }
    }

    /**
     * Returns an aspect graph obtained from this one by changing the colour
     * of one of the node types.
     * This is only valid for type graphs.
     * @param label the node type label to be changed; must be a {@link EdgeRole#NODE_TYPE}.
     * @param colour the new colour for the node type; may be {@code null}
     * if the colour is to be reset to default
     * @return a clone of this aspect graph with changed labels, or this graph
     *         if {@code label} did not occur
     */
    public AspectGraph colour(TypeLabel label, Aspect colour) {
        assert getRole() == TYPE;
        // create a plain graph under relabelling
        PlainGraph result = createPlainGraph();
        AspectToPlainMap elementMap = new AspectToPlainMap();
        // flag registering if anything changed due to relabelling
        boolean graphChanged = false;
        // construct the plain graph for the aspect nodes,
        // except for the colour aspects
        for (AspectNode node : nodeSet()) {
            PlainNode image = result.addNode(node.getNumber());
            elementMap.putNode(node, image);
            for (AspectLabel nodeLabel : node.getNodeLabels()) {
                List<Aspect> nodeAspects = nodeLabel.getAspects();
                if (nodeAspects.isEmpty() || nodeAspects.get(0).getKind() != AspectKind.COLOR) {
                    result.addEdge(image, nodeLabel.toString(), image);
                }
            }
        }
        // construct the plain edges, adding colour edges when a node
        // type is found
        for (AspectEdge edge : edgeSet()) {
            AspectLabel edgeLabel = edge.label();
            PlainNode sourceImage = elementMap.getNode(edge.source());
            PlainNode targetImage = elementMap.getNode(edge.target());
            PlainEdge edgeImage = result.addEdge(sourceImage, edgeLabel.toString(), targetImage);
            elementMap.putEdge(edge, edgeImage);
            if (edge.getRole() == EdgeRole.NODE_TYPE) {
                TypeLabel nodeType = edge.getTypeLabel();
                boolean labelChanged = nodeType.equals(label);
                graphChanged |= labelChanged;
                Aspect newColour = labelChanged
                    ? colour
                    : edge.source().getColorAspect();
                if (newColour != null) {
                    result.addEdge(sourceImage, newColour.toString(), targetImage);
                }
            }
        }
        if (!graphChanged) {
            return this;
        } else {
            GraphInfo.transfer(this, result, elementMap);
            result.setFixed();
            return newInstance(result);
        }
    }

    @Override
    public boolean addEdge(AspectEdge edge) {
        edge.setParsed();
        this.normal &= !edge.isAssign() && !edge.isPredicate();
        return super.addEdge(edge);
    }

    /**
     * Returns the role of this default graph.
     * The role is set at construction time.
     */
    @Override
    public final GraphRole getRole() {
        return this.role;
    }

    @Override
    public boolean isFixed() {
        return this.status == Status.FIXED;
    }

    @Override
    public boolean setFixed() {
        if (DEBUG) {
            System.out.printf("setFixed called on %s %s%n", getRole(), getName());
        }
        boolean result = !isFixed();
        if (result) {
            setNodeComplete();
            FormatErrorSet errors = new FormatErrorSet();
            // first fix the edges, then the nodes
            for (AspectEdge edge : edgeSet()) {
                edge.setFixed();
                errors.addAll(edge.getErrors());
            }
            for (AspectNode node : nodeSet()) {
                node.setFixed();
                errors.addAll(node.getErrors());
            }
            if (!GraphInfo.hasErrors(this)) {
                addErrors(errors);
            }
            this.status = Status.FIXED;
            super.setFixed();
        }
        return result;
    }

    /**
     * Callback method to indicate that no more identified nodes will be added.
     * This allows the node identities and typing to be computed.
     */
    public void setNodeComplete() {
        if (!isNodeComplete()) {
            FormatErrorSet errors = new FormatErrorSet();
            // collect node identifiers and check for duplicates
            HashMap<String,AspectNode> nodeIdMap = new HashMap<>();
            Typing typing = new Typing();
            for (AspectNode node : nodeSet()) {
                String id = node.getId();
                if (id != null) {
                    AspectNode oldNode = nodeIdMap.put(id, node);
                    if (oldNode != null) {
                        errors.add("Duplicate node identifier %s", id, node, oldNode);
                    } else if (node.hasDataAspect()) {
                        Sort s = node.getSort();
                        assert s != null;
                        typing.add(id, s);
                    }
                }
            }
            this.nodeIdMap = nodeIdMap;
            this.typing = typing;
            addErrors(errors);
            this.status = Status.NODE_COMPLETE;
        }
    }

    @Override
    public AspectGraph newGraph(String name) {
        return new AspectGraph(name, getRole());
    }

    /**
     * Copies this aspect graph to one with the same nodes, edges and graph
     * info. The result is not fixed.
     */
    @Override
    public AspectGraph clone() {
        return clone(new AspectGraphMorphism(this));
    }

    /**
     * Copies this aspect graph, using a given
     * (empty) map to keep track.
     */
    private AspectGraph clone(AspectGraphMorphism map) {
        assert isFixed();
        AspectGraph result = newGraph(getName());
        for (AspectNode node : nodeSet()) {
            AspectNode clone = node.clone(result);
            map.putNode(node, clone);
            result.addNode(clone);
        }
        for (AspectEdge edge : edgeSet()) {
            var image = map.mapEdge(edge);
            assert image != null;
            result.addEdgeContext(image);
        }
        if (getNodeIdMap() != null) {
            Map<String,AspectNode> newNodeIdMap = new HashMap<>();
            getNodeIdMap()
                .entrySet()
                .stream()
                .forEach(e -> newNodeIdMap.put(e.getKey(), map.getNode(e.getValue())));
            result.nodeIdMap = newNodeIdMap;
            var newTyping = new Typing();
            getTyping().entrySet().stream().forEach(e -> newTyping.add(e.getKey(), e.getValue()));
            result.typing = newTyping;
        }
        GraphInfo.transfer(this, result, null);
        return result;
    }

    /**
     * Clones this aspect graph while giving it a different name.
     * This graph is required to be fixed, and the resulting graph
     * will be fixed as well.
     * @param name the new graph name; non-{@code null}
     */
    public AspectGraph rename(QualName name) {
        AspectGraph result = clone();
        result.setQualName(name);
        result.setFixed();
        return result;
    }

    /** Returns a copy of this graph with all labels unwrapped.
     * @see AspectLabel#unwrap()
     */
    public AspectGraph unwrap() {
        AspectGraph result = clone(new AspectGraphUnwrapper(this));
        result.setFixed();
        return result;
    }

    @Override
    public AspectFactory getFactory() {
        return this.aspectFactory.get();
    }

    /** Creates a new aspect factory. */
    private AspectFactory createFactory() {
        return new AspectFactory(this);
    }

    /** Aspect factory for this graph. */
    private LazyFactory<AspectFactory> aspectFactory = LazyFactory.instance(this::createFactory);

    /** The graph role of the aspect graph. */
    private final GraphRole role;
    /** Flag indicating whether the graph is normal. */
    private boolean normal;

    /** Checks if the graph is in the phase where all identified nodes have been added.
     * If so, the typing information is complete.
     */
    public boolean isNodeComplete() {
        return this.status != Status.NEW;
    }

    private Status status = Status.NEW;

    /** Returns the mapping from declared node identities to nodes. */
    Map<String,AspectNode> getNodeIdMap() {
        assert isNodeComplete();
        return this.nodeIdMap;
    }

    /** Mapping from node identifiers to nodes. */
    private Map<String,AspectNode> nodeIdMap;

    /** Returns the mapping from declared node identities to the primitive node sorts. */
    Typing getTyping() {
        assert isNodeComplete();
        return this.typing;
    }

    /** Mapping from node identifiers to sorts. */
    private Typing typing;

    /**
     * Creates an aspect graph from a given (plain) graph.
     * @param graph the plain graph to convert; non-null
     * @return the resulting aspect graph; non-null
     */
    public static AspectGraph newInstance(Graph graph) {
        GraphRole role = graph.getRole();
        AspectGraph result = new AspectGraph(graph.getName(), role);
        // map from original graph elements to aspect graph elements
        GraphToAspectMap elementMap = new GraphToAspectMap(result);
        FormatErrorSet errors = new FormatErrorSet();
        assert elementMap != null && elementMap.isEmpty();
        // first do the nodes;
        for (Node node : graph.nodeSet()) {
            AspectNode nodeImage = result.addNode(node.getNumber());
            // update the maps
            elementMap.putNode(node, nodeImage);
        }
        // look for node aspect indicators
        // and put all correct aspect vales in a map
        Map<Edge,AspectLabel> edgeDataMap = new HashMap<>();
        for (Edge edge : graph.edgeSet()) {
            AspectLabel label = parser.parse(edge.label().text(), role);
            if (label.isNodeOnly()) {
                AspectNode sourceImage = elementMap.getNode(edge.source());
                sourceImage.setAspects(label);
            } else {
                edgeDataMap.put(edge, label);
            }
        }
        // Now iterate over the remaining edges
        for (Map.Entry<Edge,AspectLabel> entry : edgeDataMap.entrySet()) {
            Edge edge = entry.getKey();
            AspectLabel label = entry.getValue();
            AspectEdge edgeImage = result
                .addEdge(elementMap.getNode(edge.source()), label,
                         elementMap.getNode(edge.target()));
            elementMap.putEdge(edge, edgeImage);
            if (!edge.source().equals(edge.target()) && edgeImage.getRole() != EdgeRole.BINARY) {
                errors
                    .add("%s %s must be a node label", label.getRole().getDescription(true), label,
                         edgeImage);
            }
        }
        GraphInfo.transfer(graph, result, elementMap);
        result.addErrors(errors);
        result.setFixed();
        return result;
    }

    /** Creates an empty, fixed, named aspect graph, with a given graph role. */
    public static AspectGraph emptyGraph(String name, GraphRole role) {
        AspectGraph result = new AspectGraph(name, role);
        result.setFixed();
        return result;
    }

    /** Creates an empty, fixed aspect graph, with a given graph role. */
    public static AspectGraph emptyGraph(GraphRole role) {
        return emptyGraph("", role);
    }

    /**
     * Merges a given set of graphs into a single graph.
     * Nodes with the same {@link AspectKind#ID} value are merged,
     * all other nodes are kept distinct.
     * The merged graph is layed out by placing the original graphs next to one another.
     * @return a merged aspect graph or {@code null} if the set of input graphs is empty
     */
    public static AspectGraph mergeGraphs(Collection<AspectGraph> graphs) {
        if (graphs.size() == 0) {
            return null;
        }
        // Compute name and layout boundaries
        StringBuilder name = new StringBuilder();
        List<Point.Double> dimensions = new ArrayList<>();
        double globalMaxX = 0;
        double globalMaxY = 0;
        for (AspectGraph graph : graphs) {
            assert graph.getRole() == HOST;
            if (name.length() != 0) {
                name.append("_");
            }
            name.append(graph.getName());
            // compute dimensions of this graph
            double maxX = 0;
            double maxY = 0;
            LayoutMap layoutMap = GraphInfo.getLayoutMap(graph);
            if (layoutMap != null) {
                for (AspectNode node : graph.nodeSet()) {
                    JVertexLayout layout = layoutMap.nodeMap().get(node);
                    if (layout != null) {
                        Rectangle2D b = layout.getBounds();
                        maxX = Math.max(maxX, b.getX() + b.getWidth());
                        maxY = Math.max(maxY, b.getY() + b.getHeight());
                    }
                }
            }
            dimensions.add(new Point.Double(maxX, maxY));
            globalMaxX = Math.max(globalMaxX, maxX);
            globalMaxY = Math.max(globalMaxY, maxY);
        }
        // construct the result graph
        AspectGraph result = new AspectGraph(name.toString(), HOST);
        LayoutMap newLayoutMap = new LayoutMap();
        FormatErrorSet newErrors = new FormatErrorSet();
        // Local bookkeeping.
        int nodeNr = 0;
        int index = 0;
        double offsetX = 0;
        double offsetY = 0;
        Map<AspectNode,AspectNode> nodeMap = new HashMap<>();
        Map<String,AspectNode> sharedNodes = new HashMap<>();

        // Copy the graphs one by one into the combined graph
        for (AspectGraph graph : graphs) {
            nodeMap.clear();
            LayoutMap oldLayoutMap = GraphInfo.getLayoutMap(graph);
            // Copy the nodes
            for (AspectNode node : graph.nodeSet()) {
                AspectNode fresh = null;
                if (node.hasId()) {
                    String id = node.getId();
                    if (sharedNodes.containsKey(id)) {
                        nodeMap.put(node, sharedNodes.get(id));
                    } else {
                        fresh = node.clone(result, nodeNr++);
                        sharedNodes.put(id, fresh);
                    }
                } else {
                    fresh = node.clone(result, nodeNr++);
                }
                if (fresh != null) {
                    newLayoutMap.copyNodeWithOffset(fresh, node, oldLayoutMap, offsetX, offsetY);
                    nodeMap.put(node, fresh);
                    result.addNode(fresh);
                }
            }
            // Copy the edges
            for (AspectEdge edge : graph.edgeSet()) {
                AspectEdge fresh = new AspectEdge(nodeMap.get(edge.source()), edge.label(),
                    nodeMap.get(edge.target()), edge.getNumber());
                newLayoutMap.copyEdgeWithOffset(fresh, edge, oldLayoutMap, offsetX, offsetY);
                result.addEdgeContext(fresh);
            }
            // Copy the errors
            for (FormatError oldError : GraphInfo.getErrors(graph)) {
                newErrors.add("Error in start graph '%s': %s", name, oldError);
            }
            // Move the offsets
            if (globalMaxX > globalMaxY) {
                offsetY = offsetY + dimensions.get(index).getY() + 50;
            } else {
                offsetX = offsetX + dimensions.get(index).getX() + 50;
            }
            index++;
        }

        // Finalise combined graph.
        GraphInfo.setLayoutMap(result, newLayoutMap);
        GraphInfo.setErrors(result, newErrors);
        result.setFixed();
        return result;
    }

    /** The singleton aspect parser instance. */
    private static final AspectParser parser = AspectParser.getInstance();

    /** Debug flag. */
    static private final boolean DEBUG = false;

    /** Factory for AspectGraph elements. */
    public static class AspectFactory extends ElementFactory<AspectNode,AspectEdge> {
        /** Creates a factory for a given graph. */
        public AspectFactory(AspectGraph graph) {
            this.graph = graph;
        }

        @Override
        protected AspectNode newNode(int nr) {
            return new AspectNode(nr, this.graph);
        }

        @Override
        public AspectLabel createLabel(String text) {
            return AspectParser.getInstance().parse(text, this.graph.getRole());
        }

        @Override
        public AspectEdge createEdge(AspectNode source, Label label, AspectNode target) {
            int nr = 0;
            AspectLabel aLabel = (AspectLabel) label;
            if (aLabel.containsAspect(AspectKind.REMARK)) {
                nr = this.remarkCount;
                this.remarkCount++;
            }
            return new AspectEdge(source, (AspectLabel) label, target, nr);
        }

        /** Number of remark edges encountered thus far. */
        private int remarkCount;

        @Override
        public AspectGraphMorphism createMorphism() {
            return new AspectGraphMorphism(this.graph);
        }

        /** The graph role of the created elements. */
        private final AspectGraph graph;
    }

    /** Mapping from one aspect graph to another. */
    public static class AspectGraphMorphism extends Morphism<AspectNode,AspectEdge> {
        /** Constructs a new, empty map. */
        public AspectGraphMorphism(AspectGraph graph) {
            super(graph.getFactory());
            assert graph.getRole().inGrammar();
            this.graph = graph;
        }

        @Override
        public AspectGraphMorphism newMap() {
            return new AspectGraphMorphism(this.graph);
        }

        /** The graph role of the created elements. */
        private final AspectGraph graph;
    }

    /** Mapping from one aspect graph to another. */
    public static class AspectGraphUnwrapper extends AspectGraphMorphism {
        /** Constructs a new, empty map. */
        public AspectGraphUnwrapper(AspectGraph graph) {
            super(graph);
        }

        @Override
        public Label mapLabel(Label label) {
            return ((AspectLabel) label).unwrap();
        }
    }

    private static class AspectToPlainMap
        extends AElementMap<AspectNode,AspectEdge,PlainNode,PlainEdge> {
        /** Constructs a new, empty map. */
        public AspectToPlainMap() {
            super(PlainFactory.instance());
        }

        @Override
        public PlainEdge createImage(AspectEdge key) {
            PlainNode imageSource = getNode(key.source());
            if (imageSource == null) {
                return null;
            }
            PlainNode imageTarget = getNode(key.target());
            if (imageTarget == null) {
                return null;
            }
            return getFactory().createEdge(imageSource, key.getPlainLabel(), imageTarget);
        }
    }

    /**
     * Graph element map from a plain graph to an aspect graph.
     * @author Arend Rensink
     * @version $Revision $
     */
    private static class GraphToAspectMap extends AElementMap<Node,Edge,AspectNode,AspectEdge> {
        /** Creates a fresh, empty map. */
        public GraphToAspectMap(AspectGraph graph) {
            super(graph.getFactory());
        }
    }

    /** Construction status of an {@link AspectGraph}. */
    static private enum Status {
        /** In the process of being built up; nodes and edges are being added. */
        NEW,
        /** No identified nodes will be added any more. */
        NODE_COMPLETE,
        /** Completely fixed. */
        FIXED,;
    }
}
