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
 	 * $Id: AspectGraph.java 6255 2023-10-26 16:08:19Z rensink $
 */
package nl.utwente.groove.grammar.aspect;

import static nl.utwente.groove.grammar.aspect.AspectKind.CREATOR;
import static nl.utwente.groove.grammar.aspect.AspectKind.EMBARGO;
import static nl.utwente.groove.grammar.aspect.AspectKind.ERASER;
import static nl.utwente.groove.grammar.aspect.AspectKind.NESTED;
import static nl.utwente.groove.grammar.aspect.AspectKind.READER;
import static nl.utwente.groove.graph.GraphRole.RULE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.Constant;
import nl.utwente.groove.algebra.Operator;
import nl.utwente.groove.algebra.Sort;
import nl.utwente.groove.algebra.syntax.Assignment;
import nl.utwente.groove.algebra.syntax.CallExpr;
import nl.utwente.groove.algebra.syntax.Expression;
import nl.utwente.groove.algebra.syntax.FieldExpr;
import nl.utwente.groove.algebra.syntax.Variable;
import nl.utwente.groove.grammar.aspect.AspectContent.NestedValue;
import nl.utwente.groove.grammar.aspect.AspectKind.Category;
import nl.utwente.groove.graph.GraphInfo;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.Groove;
import nl.utwente.groove.util.Keywords;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Normalised version of an AspectGraph.
 * In a normal aspect graph, all let-, test- or role:sort:field-edges
 * are substituted by primitive attribute syntax (prod-nodes with argi and sort:op-edges)
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class NormalAspectGraph extends AspectGraph {
    /**
     * Creates the normalised version of a given aspect graph.
     * @param source the (non-normalised) source of this normalised aspect graph
     */
    public NormalAspectGraph(AspectGraph source) {
        super(source.getName(), source.getRole());
        var toNormalMap = this.toNormalMap = new AspectGraphMorphism(this);
        source.cloneTo(toNormalMap);
        this.source = source;
    }

    /** Returns the (non-normalised) source of this normalised aspect graph. */
    public AspectGraph getSource() {
        return this.source;
    }

    /** The (non-normalised) source of this normalised aspect graph. */
    private final AspectGraph source;

    /** Returns the morphism from the source {@link AspectGraph} to the normalised {@link AspectGraph}. */
    public AspectGraphMorphism toNormalMap() {
        return this.toNormalMap;
    }

    /** Morphism from the source {@link AspectGraph} to the normalised {@link AspectGraph}. */
    private final AspectGraphMorphism toNormalMap;

    @Override
    public boolean setFixed() {
        boolean result = !isFixed();
        if (result) {
            setStatus(Status.NORMALISING);
            var changeMap = doNormalise();
            var toNormalMap = this.toNormalMap;
            for (var ne : toNormalMap.nodeMap().entrySet()) {
                var nv = ne.getValue();
                if (changeMap.containsNode(nv)) {
                    ne.setValue(changeMap.getNode(nv));
                }
            }
            for (var ee : toNormalMap.edgeMap().entrySet()) {
                var ev = ee.getValue();
                if (changeMap.containsEdge(ev)) {
                    ee.setValue(changeMap.getEdge(ev));
                }
            }
            GraphInfo.setErrors(this, getErrors().unwrap(toNormalMap));
            super.setFixed();
        }
        return result;
    }

    /**
     * Normalises this (non-fixed) aspect graph.
     * @return mapping from the replaced elements of the source graph to their
     * counterparts in this normalised graph
     */
    private AspectGraphMorphism doNormalise() {
        assert !isFixed();
        // identify and remove let- and test-edges
        AspectGraphMorphism map = new AspectGraphMorphism(this);
        Set<AspectEdge> letEdges = new LinkedHashSet<>();
        Set<AspectEdge> testEdges = new LinkedHashSet<>();
        Set<AspectEdge> fieldEdges = new LinkedHashSet<>();
        Set<AspectNode> exprNodes = new LinkedHashSet<>();
        for (AspectEdge edge : edgeSet()) {
            if (edge.isTest()) {
                testEdges.add(edge);
            } else if (edge.isAssign()) {
                letEdges.add(edge);
            } else if (edge.isField()) {
                fieldEdges.add(edge);
            }
        }
        for (var node : nodeSet()) {
            if (node.hasExpression()) {
                exprNodes.add(node);
            }
        }
        removeEdgeSet(letEdges);
        removeEdgeSet(testEdges);
        removeEdgeSet(fieldEdges);
        // parse the nodes and edges, to set the derived aspect information
        nodeSet().forEach(AspectNode::setParsed);
        edgeSet().forEach(AspectEdge::setParsed);
        var errors = new FormatErrorSet();
        // add expressions for the expression nodes
        Map<AspectNode,AspectNode> exprNodeMap = new HashMap<>();
        for (var node : exprNodes) {
            AspectNode level = node.getLevelNode();
            var nodeExpr = node.getExpression();
            if (nodeExpr == null) {
                assert node.hasErrors();
                errors.addAll(node.getErrors());
            } else {
                try {
                    assert nodeExpr != null;
                    AspectNode outcome = addExpression(level, node, nodeExpr);
                    exprNodeMap.put(node, outcome);
                    map.putNode(node, outcome);
                } catch (FormatException exc) {
                    errors.addAll(exc.getErrors());
                }
            }
        }
        // add assignments for the let-edges
        Map<AspectEdge,AspectEdge> edgeInverseMap = new HashMap<>();
        for (AspectEdge edge : letEdges) {
            if (edge.hasErrors()) {
                continue;
            }
            try {
                AspectNode source = edge.source();
                assert !source.has(Category.NESTING);
                AspectNode level = source.getLevelNode();
                AspectEdge normalisedEdge
                    = addAssignment(level, source, edge.getAssign(), edge.getKind(Category.ROLE));
                edgeInverseMap.put(normalisedEdge, edge);
                map.putEdge(edge, normalisedEdge);
            } catch (FormatException e) {
                errors.addAll(e.getErrors());
            }
        }
        // add conditions for the test-edges
        for (AspectEdge edge : testEdges) {
            if (edge.hasErrors()) {
                continue;
            }
            try {
                AspectNode source = edge.source();
                boolean sourceIsNesting = source.has(Category.NESTING);
                boolean nac = edge.has(Category.ROLE, AspectKind::inNAC)
                    && (sourceIsNesting || !source.has(Category.ROLE, AspectKind::inNAC));
                Expression predicate = edge.getTest();
                AspectNode level = sourceIsNesting
                    ? source.getParentNode()
                    : source.getLevelNode();
                AspectNode outcome = addExpression(level, source, predicate);
                // specify whether the outcome should be true or false
                outcome.set(Aspect.newAspect(Constant.instance(!nac), getRole()));
            } catch (FormatException e) {
                errors.addAll(e.getErrors());
            }
        }
        // add appropriate structure for the field-edges
        for (var edge : fieldEdges) {
            if (edge.hasErrors()) {
                continue;
            }
            AspectNode source = edge.source();
            AspectNode level = source.getLevelNode();
            var label = edge.getField();
            assert label != null;
            var target = addNestedNode(level, edge.has(EMBARGO));
            var sortKind = edge.getKind(Category.SORT);
            assert sortKind != null;
            target.set(sortKind.getAspect());
            AspectEdge normalisedEdge = addEdge(source, label, target);
            var role = edge.get(Category.ROLE);
            assert role != null;
            normalisedEdge.set(role);
            normalisedEdge.setParsed();
            edgeInverseMap.put(normalisedEdge, edge);
            map.putEdge(edge, normalisedEdge);
        }
        // delete old expression nodes and reroute edges to their images
        resetNodeIdMap();
        for (var entry : exprNodeMap.entrySet()) {
            var node = entry.getKey();
            var image = entry.getValue();
            node
                .getAspects()
                .values()
                .stream()
                .filter(a -> !a.has(Category.SORT))
                .forEach(image::set);
            map.putNode(node, image);
            var inEdges = new LinkedList<>(inEdgeSet(node));
            // remove the incoming edges from the graph as well as from their source nodes
            inEdges
                .stream()
                .filter(e -> e.has(NESTED))
                .forEach(e -> e.source().resetNestingEdge(e));
            removeEdgeSet(inEdges);
            for (var inEdge : inEdges) {
                var inEdgeImage = addEdge(inEdge.source(), inEdge.label(), image);
                inEdgeImage.setParsed();
                if (edgeInverseMap.containsKey(inEdge)) {
                    map.putEdge(edgeInverseMap.get(inEdge), inEdgeImage);
                } else {
                    map.putEdge(inEdge, inEdgeImage);
                }
            }
            var outEdges = new LinkedList<>(outEdgeSet(node));
            for (var outEdge : outEdges) {
                var outEdgeImage = addEdge(image, outEdge.label(), outEdge.target());
                outEdgeImage.setParsed();
                if (edgeInverseMap.containsKey(outEdge)) {
                    map.putEdge(edgeInverseMap.get(outEdge), outEdgeImage);
                } else {
                    map.putEdge(outEdge, outEdgeImage);
                }
            }
            removeEdgeSet(outEdges);
            removeNode(node);
        }
        addErrors(errors.transfer(map));
        return map;
    }

    /**
     * Adds the structure corresponding to an assignment.
     * @param level the nesting level node on which the expression should be computed
     * @param source node on which the assignment occurs
     * @param assign the parsed assignment
     * @param roleKind flag indicating if the attribute is new (so no eraser need be added for the old value)
     */
    private AspectEdge addAssignment(@Nullable AspectNode level, AspectNode source,
                                     Assignment assign,
                                     @Nullable AspectKind roleKind) throws FormatException {
        // add the expression structure
        AspectNode target = addExpression(level, source, assign.getRhs());
        target.setParsed();
        // add a creator edge (for a rule) or normal edge to the assignment target
        var newRoleKind = roleKind == READER
            ? CREATOR
            : roleKind;
        String assignLabelText = (newRoleKind == null
            ? ""
            : newRoleKind.getPrefix()) + assign.getLhs();
        AspectLabel assignLabel = parser.parse(assignLabelText, getRole());
        AspectEdge result = addEdge(source, assignLabel, target);
        // add an eraser edge if the assignment is a reader
        if (roleKind == READER) {
            Sort sort = target.getSort();
            assert sort != null;
            // add an eraser edge for the old value
            AspectNode oldTarget;
            var oldEdge = findEdge(source, assign.getLhs(), sort);
            if (oldEdge == null) {
                oldTarget = addNestedNode(level, false);
                // use the type of the new target for the old target node
                oldTarget.set(Aspect.getAspect(sort));
            } else {
                oldTarget = oldEdge.target();
                removeEdge(oldEdge);
            }
            assignLabel = parser.parse(ERASER.getPrefix() + assign.getLhs(), getRole());
            addEdge(source, assignLabel, oldTarget).setParsed();
        }
        result.setParsed();
        return result;
    }

    /**
     * Adds the structure corresponding to an expression, and returns the node in
     * which the expression result is collected.
     * The result node is parsed.
     * @param level the nesting level node on which the expression should be computed
     * @param source node on which the expression occurs
     * @param expr the parsed expression
     * @return the node holding the value of the expression
     */
    private AspectNode addExpression(@Nullable AspectNode level, AspectNode source,
                                     Expression expr) throws FormatException {
        return switch (expr.getKind()) {
        case CONST -> addConstant(source, (Constant) expr);
        case FIELD -> addField(level, source, (FieldExpr) expr);
        case CALL -> addCall(level, source, (CallExpr) expr);
        case VAR -> addVar(source, (Variable) expr);
        };
    }

    /**
     * Adds a constant node to this graph, based on a constant expression.
     * The result node is parsed.
     * @param source the node on which the constant was specified; used for its NAC status and nesting level
     * @param constant the constant for which we add a node
     * @return the node representing the constant
     */
    private AspectNode addConstant(AspectNode source, Constant constant) {
        AspectNode result = addNode();
        result.set(Aspect.newAspect(constant, getRole()));
        result.setParsed();
        return result;
    }

    /**
     * Creates or retrieves the target of a field expression.
     * @param level the nesting level node on which the expression should be computed
     * @param source the node on which the field expression occurs; used for its NAC status and nesting level,
     * and possibly as owner of the field if it specifies self
     * @param expr the field expression
     * @return the target node of the identifier
     */
    private AspectNode addField(@Nullable AspectNode level, AspectNode source,
                                FieldExpr expr) throws FormatException {
        if (getRole() != RULE) {
            throw new FormatException("Field expression '%s' only allowed in rules",
                expr.toDisplayString(), source);
        }
        // look up the field owner
        AspectNode owner;
        String ownerName = expr.getTarget();
        if (ownerName == null || ownerName.equals(Keywords.SELF)) {
            if (source.has(Category.SORT)) {
                throw new FormatException("Self-field '%s' not allowed for value nodes",
                    expr.toDisplayString(), source);
            }
            owner = source;
        } else {
            owner = getNodeForId(ownerName);
            if (owner == null) {
                throw new FormatException("Unknown node identifier '%s' in field expression '%s'",
                    ownerName, expr.toDisplayString(), source);
            }
        }
        if (owner.has(Category.ROLE, AspectKind::isCreator)) {
            throw new FormatException("Unassigned creator node field in expression '%s'",
                expr.toDisplayString(), owner, source);
        }
        if (owner.has(Category.NESTING) && !expr.getField().equals(NestedValue.COUNT.toString())) {
            throw new FormatException("Quantifier node does not have '%s'-edge", expr.getField(),
                owner, source);
        }
        // look up the field
        Sort exprSort = expr.getSort();
        AspectNode result = findTarget(owner, expr.getField(), exprSort);
        if (result == null) {
            result = addNestedNode(level, source.has(EMBARGO));
            result.set(Aspect.getAspect(exprSort));
            // add edge to newly created field
            AspectLabel idLabel = parser.parse(expr.getField(), getRole());
            addEdge(owner, idLabel, result).setParsed();
        }
        return result;
    }

    /** Looks for an outgoing edge matching a given field expression.
     * @param owner the node that should have the outgoing edge
     * @param fieldName expected label of the edge
     * @param sort expected target source of the edge
     */
    private @Nullable AspectEdge findEdge(AspectNode owner, String fieldName, Sort sort) {
        boolean allEdgesOK = getRole() != RULE || owner.has(Category.NESTING);
        Optional<? extends AspectEdge> result = outEdgeSet(owner)
            .stream()
            .filter(e -> allEdgesOK || e.has(Category.ROLE, AspectKind::inLHS))
            .filter(e -> e.getInnerText().equals(fieldName))
            .filter(e -> e.target().has(Category.SORT, k -> k.hasSort(sort)))
            .findAny();
        return Groove.orElse(result, null);
    }

    /** Looks for an outgoing edge matching a given field expression.
     * @param owner the node that should have the outgoing edge
     * @param fieldName expected label of the edge
     * @param sort expected target source of the edge
     */
    private @Nullable AspectNode findTarget(AspectNode owner, String fieldName, Sort sort) {
        var result = findEdge(owner, fieldName, sort);
        return result == null
            ? null
            : result.target();
    }

    /**
     * Adds the structure for a call expression
     * @param level the nesting level node on which the expression should be computed
     * @param source node on which the expression occurs; used for its NAC status and nesting level
     * @param call the call expression
     * @return the node representing the value of the expression
     */
    private AspectNode addCall(@Nullable AspectNode level, AspectNode source,
                               CallExpr call) throws FormatException {
        if (getRole() != RULE) {
            throw new FormatException("Call expression '%s' only allowed in rules",
                call.toParseString(), source);
        }
        var levelErrors = new FormatErrorSet();
        AspectNode argLevel;
        Operator operator = call.getOperator();
        if (operator.isSetOperator()) {
            Expression arg = call.getArgs().get(0);
            argLevel = getLevel(arg);
            if (argLevel == null || source.getLevelNode() != argLevel.getParentNode()) {
                levelErrors
                    .add("Set operator argument '%s' should be one level deeper than source node '%s'",
                         arg, source);
            }
        } else {
            argLevel = level;
        }
        var argErrors = new FormatErrorSet();
        var argResults = new ArrayList<AspectNode>();
        for (var arg : call.getArgs()) {
            try {
                argResults.add(addExpression(argLevel, source, arg));
            } catch (FormatException exc) {
                argErrors.addAll(exc.getErrors());
            }
        }
        argErrors.throwException();
        levelErrors.throwException();
        AspectNode product = addNestedNode(argLevel, source.has(EMBARGO));
        product.set(AspectKind.PRODUCT.getAspect());
        for (int i = 0; i < argResults.size(); i++) {
            var argResult = argResults.get(i);
            AspectLabel argLabel = parser.parse(AspectKind.ARGUMENT.getPrefix() + i, getRole());
            addEdge(product, argLabel, argResult).setParsed();
        }
        // add the operator edge
        AspectNode result = addNestedNode(level, source.has(EMBARGO));
        result.set(AspectKind.toAspectKind(call.getSort()).getAspect());
        AspectLabel operatorLabel = parser.parse(operator.getFullName(), getRole());
        addEdge(product, operatorLabel, result).setParsed();
        // add the arguments
        return result;
    }

    /**
     * Adds the structure for a variable expression.
     * @param source the node that the variable expression occurs on
     * @param var the variable expression
     * @return the node representing the value of the expression
     */
    private AspectNode addVar(AspectNode source, Variable var) throws FormatException {
        if (getRole() != RULE) {
            throw new FormatException("Field expression '%s' only allowed in rules",
                var.toDisplayString(), source);
        }
        // look up the field owner
        AspectNode result;
        String name = var.getName();
        result = getNodeForId(name);
        if (result == null) {
            throw new FormatException("Unknown node identifier '%s'", name, source);
        }
        return result;
    }

    /** Adds a node with a given nesting level and embargo status.
     * @param level the nesting level node on which the node should be created
     * @param embargo if {@code true}, the new node should be an embargo
     */
    private AspectNode addNestedNode(@Nullable AspectNode level, boolean embargo) {
        AspectNode result = addNode();
        if (embargo) {
            result.set(EMBARGO.getAspect());
        }
        if (level != null) {
            addEdge(result, NestedValue.AT.toString(), level).setParsed();
        }
        return result;
    }

    /** Returns the (possibly {@code null} nesting level on which an expression can be evaluated, in terms
     * of the associated quantifier node.
     * @throws FormatException if there is no common nesting level for all sub-expressions
     */
    private @Nullable AspectNode getLevel(Expression expr) throws FormatException {
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
            AspectNode target = getNodeForId(((FieldExpr) expr).getTarget());
            if (target != null) {
                result = target.getLevelNode();
            }
            break;
        default:
            throw Exceptions.UNREACHABLE;
        }
        return result;
    }

    private boolean isChild(AspectNode child, AspectNode parent) {
        boolean result = child.equals(parent);
        @Nullable
        AspectNode next = child;
        while (!result && next != null) {
            next = next.getParentNode();
            result = child.equals(parent);
        }
        return result;
    }

    /** The singleton aspect parser instance. */
    private static final AspectParser parser = AspectParser.getInstance();

}
