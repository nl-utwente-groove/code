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
 * $Id: AspectGraph.java,v 1.16 2008-02-29 11:02:22 fladder Exp $
 */
package groove.grammar.aspect;

import static groove.graph.GraphRole.HOST;
import static groove.graph.GraphRole.RULE;
import static groove.graph.GraphRole.TYPE;
import groove.algebra.Algebras;
import groove.algebra.Constant;
import groove.algebra.Operator;
import groove.automaton.RegExpr;
import groove.grammar.aspect.Expression.Call;
import groove.grammar.aspect.Expression.Const;
import groove.grammar.aspect.Expression.Field;
import groove.grammar.aspect.Expression.Par;
import groove.grammar.model.FormatError;
import groove.grammar.model.FormatErrorSet;
import groove.grammar.model.FormatException;
import groove.grammar.type.TypeLabel;
import groove.graph.AElementMap;
import groove.graph.Edge;
import groove.graph.EdgeRole;
import groove.graph.ElementFactory;
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.graph.GraphRole;
import groove.graph.Label;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.NodeComparator;
import groove.graph.NodeSetEdgeSetGraph;
import groove.graph.plain.PlainEdge;
import groove.graph.plain.PlainFactory;
import groove.graph.plain.PlainGraph;
import groove.graph.plain.PlainLabel;
import groove.graph.plain.PlainNode;
import groove.gui.layout.JVertexLayout;
import groove.gui.layout.LayoutMap;
import groove.gui.list.SearchResult;

import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Graph implementation to convert from a label prefix representation of an
 * aspect graph to a graph where the aspect values are stored in
 * {@link AspectNode}s and {@link AspectEdge}s.
 * @author Arend Rensink
 * @version $Revision $
 */
public class AspectGraph extends NodeSetEdgeSetGraph<AspectNode,AspectEdge> {
    /**
     * Creates an empty graph, with a given name and graph role.
     */
    public AspectGraph(String name, GraphRole graphRole) {
        super(name);
        assert graphRole.inGrammar();
        this.role = graphRole;
        this.normal = true;
        // make sure the properties object is initialised
        getInfo();
    }

    /** Sets the list of errors to a copy of a given list. */
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
     * Method that returns an {@link AspectGraph} based on a graph whose edges
     * are interpreted as aspect value prefixed. This means that nodes with
     * self-edges that have no text (apart from their aspect prefixes) are
     * treated as indicating the node aspect. The method never throws an
     * exception, but the resulting graph may have format errors.
     * @param graph the graph to take as input.
     * @return an aspect graph with possible format errors
     */
    @Deprecated
    public AspectGraph fromPlainGraph(Graph graph) {
        // map from original graph elements to aspect graph elements
        GraphToAspectMap elementMap = new GraphToAspectMap(graph.getRole());
        return fromPlainGraph(graph, elementMap);
    }

    /**
     * Method that returns an {@link AspectGraph} based on a graph whose edges
     * are interpreted as aspect value prefixed. This means that nodes with
     * self-edges that have no text (apart from their aspect prefixes) are
     * treated as indicating the node aspect. The mapping from the old to the
     * new graph is stored in a parameter. The method never throws an exception,
     * but the resulting graph may have format errors.
     * @param graph the graph to take as input.
     * @param elementMap output parameter for mapping from plain graph elements
     *        to resulting {@link AspectGraph} elements; should be initially
     *        empty
     */
    @Deprecated
    private AspectGraph fromPlainGraph(Graph graph, GraphToAspectMap elementMap) {
        GraphRole role = graph.getRole();
        AspectGraph result = new AspectGraph(graph.getName(), role);
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
        Map<Edge,AspectLabel> edgeDataMap = new HashMap<Edge,AspectLabel>();
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
            AspectEdge edgeImage =
                result.addEdge(elementMap.getNode(edge.source()), label,
                    elementMap.getNode(edge.target()));
            elementMap.putEdge(edge, edgeImage);
            if (!edge.source().equals(edge.target())
                && edgeImage.getRole() != EdgeRole.BINARY) {
                errors.add("%s %s must be a node label",
                    label.getRole().getDescription(true), label, edgeImage);
            }
        }
        GraphInfo.transfer(graph, result, elementMap);
        result.addErrors(errors);
        result.setFixed();
        return result;
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
            result.addEdgeContext(elementMap.mapEdge(edge));
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
        PlainGraph result = new PlainGraph(getName());
        result.setRole(getRole());
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
        // identify and remove let- and test-edges
        Set<AspectEdge> letEdges = new HashSet<AspectEdge>();
        Set<AspectEdge> predEdges = new HashSet<AspectEdge>();
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
        List<FormatError> errors = new ArrayList<FormatError>();
        for (AspectEdge edge : letEdges) {
            try {
                AspectEdge normalisedEdge =
                    addAssignment(edge.source(), edge.getAssign());
                if (map != null) {
                    map.putEdge(edge, normalisedEdge);
                }
            } catch (FormatException e) {
                errors.addAll(e.getErrors());
            }
        }
        // add conditions for the pred-edges
        for (AspectEdge edge : predEdges) {
            try {
                AspectNode source = edge.source();
                boolean nac =
                    edge.getKind().inNAC() && !source.getKind().inNAC();
                Object predicate = edge.getPredicate();
                AspectNode outcome;
                if (predicate instanceof Assignment) {
                    Assignment test = (Assignment) predicate;
                    AspectNode value = addExpression(source, test.getRhs());
                    String aspect = nac ? edge.getAspect().toString() : "";
                    AspectLabel idLabel =
                        parser.parse(aspect + test.getLhs(), getRole());
                    addEdge(source, idLabel, value).setFixed();
                } else {
                    outcome =
                        addExpression(source, (Expression) edge.getPredicate());
                    // specify whether the outcome should be true or false
                    Constant value =
                        Algebras.getConstant(nac ? "false" : "true");
                    outcome.setAspects(parser.parse(value.toString(), getRole()));
                }
            } catch (FormatException e) {
                errors.addAll(e.getErrors());
            }
        }
        addErrors(errors);
    }

    /**
     * Adds the structure corresponding to an assignment.
     */
    private AspectEdge addAssignment(AspectNode source, Assignment assign)
        throws FormatException {
        // add the expression structure
        AspectNode target = addExpression(source, assign.getRhs());
        // add a creator edge (for a rule) or normal edge to the assignment target
        String assignLabelText =
            getRole() == RULE ? AspectKind.CREATOR.getPrefix()
                + assign.getLhs() : assign.getLhs();
        AspectLabel assignLabel = parser.parse(assignLabelText, getRole());
        AspectEdge result = addEdge(source, assignLabel, target);
        if (getRole() == RULE && !source.getKind().isCreator()) {
            // add an eraser edge for the old value 
            AspectNode oldTarget =
                findTarget(source, assign.getLhs(), target.getAttrKind());
            if (oldTarget == null) {
                oldTarget = addNestedNode(source);
                // use the type of the new target for the new target node
                oldTarget.setAspects(createLabel(target.getAttrKind()));
            }
            assignLabel =
                AspectParser.getInstance().parse(
                    AspectKind.ERASER.getPrefix() + assign.getLhs(), getRole());
            addEdge(source, assignLabel, oldTarget);
        }
        return result;
    }

    /**
     * Adds the structure corresponding to an expression.
     * @param source node on which the expression edge occurs
     * @param expr the parsed expression
     * @return the node holding the value of the expression
     */
    private AspectNode addExpression(AspectNode source, Expression expr)
        throws FormatException {
        switch (expr.getKind()) {
        case CONSTANT:
            return addConstant(((Const) expr).getConstant());
        case FIELD:
            return addField(source, (Field) expr);
        case CALL:
            return addCall(source, (Call) expr);
        case PAR:
            return addPar(source, (Par) expr);
        default:
            assert false;
            return null;
        }
    }

    /**
     * Adds the structure corresponding to a constant.
     * @param constant the constant for which we add a node
     * @return the node representing the constant
     */
    private AspectNode addConstant(Constant constant) {
        AspectNode result = addNode();
        result.setAspects(parser.parse(constant.toString(), getRole()));
        return result;
    }

    /**
     * Creates the target of a field expression.
     * @param source the node which currently has the field
     * @param field the field expression
     * @return the target node of the identifier
     */
    private AspectNode addField(AspectNode source, Field field)
        throws FormatException {
        if (getRole() != RULE) {
            throw new FormatException(
                "Field expression '%s' only allowed in rules",
                field.toString(false), source);
        }
        // look up the field owner
        AspectNode owner;
        String ownerName = field.getOwner();
        if (ownerName == null) {
            owner = source;
        } else {
            owner = this.nodeIdMap.get(ownerName);
            if (owner == null) {
                throw new FormatException("Unknown node identifier '%s'",
                    ownerName, source);
            }
        }
        if (owner.getKind().isQuantifier()
            && !field.getField().equals(AspectKind.NestedValue.COUNT.toString())) {
            throw new FormatException(
                "Quantifier node does not have '%s'-edge", field.getField(),
                owner, source);
        }
        // look up the field
        AspectKind sigKind = AspectKind.toAspectKind(field.getType());
        AspectNode result = findTarget(owner, field.getField(), sigKind);
        if (result == null) {
            result = addNestedNode(owner);
            result.setAspects(createLabel(sigKind));
        } else {
            if (result.getAttrKind() != sigKind) {
                throw new FormatException(
                    "Declared type %s differs from actual field type %s",
                    sigKind.getName(), result.getAttrKind().getName(), source);
            }
        }
        assert sigKind != null;
        AspectLabel idLabel = parser.parse(field.getField(), getRole());
        addEdge(owner, idLabel, result).setFixed();
        return result;
    }

    /** Looks for an outgoing edge suitable for a given field expression. */
    private AspectNode findTarget(AspectNode owner, String fieldName,
            AspectKind fieldKind) {
        AspectNode result = null;
        for (AspectEdge edge : outEdgeSet(owner)) {
            if (edge.getDisplayLabel().text().equals(fieldName)) {
                AspectNode target = edge.target();
                // make sure we have an LHS edge or a count edge
                if (target.getAttrKind() == fieldKind
                    && (getRole() != RULE || edge.getKind().inLHS() || owner.getKind().isQuantifier())) {
                    result = edge.target();
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Adds the structure for a call expression
     * @param source node on which the expression occurs
     * @param call the call expression
     * @return the node representing the value of the expression
     */
    private AspectNode addCall(AspectNode source, Call call)
        throws FormatException {
        Operator operator = call.getOperator();
        if (getRole() != RULE) {
            throw new FormatException(
                "Operator expression '%s' only allowed in rules",
                operator.getTypedName(), source);
        }
        AspectNode result = addNestedNode(source);
        result.setAspects(createLabel(AspectKind.toAspectKind(call.getType())));
        AspectNode product = addNestedNode(source);
        product.setAspects(createLabel(AspectKind.PRODUCT));
        // add the operator edge
        AspectLabel operatorLabel =
            parser.parse(operator.getTypedName(), getRole());
        addEdge(product, operatorLabel, result);
        // add the arguments
        List<Expression> args = call.getArguments();
        for (int i = 0; i < args.size(); i++) {
            AspectNode argResult = addExpression(source, args.get(i));
            AspectLabel argLabel =
                parser.parse(AspectKind.ARGUMENT.getPrefix() + i, getRole());
            addEdge(product, argLabel, argResult);
        }
        return result;
    }

    /**
     * Adds the structure for a par expression
     * @param source node on which the expression occurs
     * @param par the par expression
     * @return the node representing the value of the expression
     */
    private AspectNode addPar(AspectNode source, Par par)
        throws FormatException {
        int nr = par.getNumber();
        if (getRole() != RULE) {
            throw new FormatException(
                "Parameter expression '%s' only allowed in rules",
                par.toDisplayString(), source);
        }
        AspectNode result = addNode();
        AspectLabel parLabel =
            parser.parse(AspectKind.PARAM_IN.getPrefix() + nr, getRole());
        result.setAspects(parLabel);
        AspectLabel typeLabel =
            createLabel(AspectKind.toAspectKind(par.getType()));
        result.setAspects(typeLabel);
        return result;
    }

    /** Adds a node with the same nesting level as a given source node. */
    private AspectNode addNestedNode(AspectNode source) throws FormatException {
        AspectNode result = addNode();
        if (source.getKind() == AspectKind.EMBARGO) {
            result.setAspect(source.getAspect());
        }
        AspectNode nesting =
            source.getKind().isQuantifier() ? source.getNestingParent()
                    : source.getNestingLevel();
        if (nesting != null) {
            addEdge(result, AspectKind.NestedValue.AT.toString(), nesting);
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
        SortedSet<AspectNode> nodes =
            new TreeSet<AspectNode>(NodeComparator.instance());
        nodes.addAll(nodeSet());
        if (!nodes.isEmpty() && nodes.last().getNumber() != nodeCount() - 1) {
            result = newGraph(getName());
            AspectGraphMorphism elementMap = new AspectGraphMorphism(getRole());
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
                AspectEdge edgeImage = elementMap.mapEdge(edge);
                result.addEdgeContext(edgeImage);
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
            for (PlainLabel nodeLabel : node.relabel(oldLabel, newLabel).getPlainLabels()) {
                result.addEdge(image, nodeLabel, image);
            }
        }
        for (AspectEdge edge : edgeSet()) {
            String replacement = null;
            if (edge.getRuleLabel() != null) {
                RegExpr oldLabelExpr = edge.getRuleLabel().getMatchExpr();
                if (oldLabelExpr != null) {
                    RegExpr newLabelExpr =
                        oldLabelExpr.relabel(oldLabel, newLabel);
                    if (newLabelExpr != oldLabelExpr) {
                        replacement = newLabelExpr.toString();
                    }
                }
            } else if (oldLabel.equals(edge.getTypeLabel())) {
                replacement = newLabel.toPrefixedString();
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
            PlainEdge edgeImage =
                result.addEdge(sourceImage, edgeLabel.toString(), targetImage);
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
                if (nodeAspects.isEmpty()
                    || nodeAspects.get(0).getKind() != AspectKind.COLOR) {
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
            PlainEdge edgeImage =
                result.addEdge(sourceImage, edgeLabel.toString(), targetImage);
            elementMap.putEdge(edge, edgeImage);
            if (edge.getRole() == EdgeRole.NODE_TYPE) {
                TypeLabel nodeType = edge.getTypeLabel();
                boolean labelChanged = nodeType.equals(label);
                graphChanged |= labelChanged;
                Aspect newColour =
                    labelChanged ? colour : edge.source().getColor();
                if (newColour != null) {
                    result.addEdge(sourceImage, newColour.toString(),
                        targetImage);
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
        edge.setFixed();
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
    public boolean setFixed() {
        boolean result = !isFixed();
        if (result) {
            // first fix the edges, then the nodes
            FormatErrorSet errors = new FormatErrorSet();
            for (AspectEdge edge : edgeSet()) {
                edge.setFixed();
                errors.addAll(edge.getErrors());
            }
            for (AspectNode node : nodeSet()) {
                node.setFixed();
                errors.addAll(node.getErrors());
            }
            // check for duplicate node identifiers
            this.nodeIdMap = new HashMap<String,AspectNode>();
            for (AspectNode node : nodeSet()) {
                Aspect id = node.getId();
                if (id != null) {
                    String name = id.getContentString();
                    AspectNode oldNode = this.nodeIdMap.put(name, node);
                    if (oldNode != null) {
                        errors.add("Duplicate node identifier %s", name, node,
                            oldNode);
                    }
                }
            }
            // check for non-binary edges with explicit layout
            for (Edge edge : GraphInfo.getLayoutMap(this).edgeMap().keySet()) {
                if (edge.getRole() != EdgeRole.BINARY) {
                    errors.add("Node label '%s' not allowed on edges",
                        edge.label(), edge);
                }
            }
            addErrors(errors);
            super.setFixed();
        }
        return result;
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
        AspectGraph result = newGraph(getName());
        AspectGraphMorphism map = new AspectGraphMorphism(getRole());
        for (AspectNode node : nodeSet()) {
            AspectNode clone = node.clone();
            map.putNode(node, clone);
            result.addNode(clone);
        }
        for (AspectEdge edge : edgeSet()) {
            AspectEdge edgeImage = map.mapEdge(edge);
            result.addEdgeContext(edgeImage);
        }
        if (this.nodeIdMap != null) {
            Map<String,AspectNode> newNodeIdMap =
                new HashMap<String,AspectNode>();
            for (Map.Entry<String,AspectNode> e : this.nodeIdMap.entrySet()) {
                newNodeIdMap.put(e.getKey(), map.getNode(e.getValue()));
            }
            result.nodeIdMap = newNodeIdMap;
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
    public AspectGraph rename(String name) {
        AspectGraph result = clone();
        result.setName(name);
        result.setFixed();
        return result;
    }

    @Override
    public AspectFactory getFactory() {
        return AspectFactory.instance(getRole());
    }

    /** The graph role of the aspect graph. */
    private final GraphRole role;
    /** Flag indicating whether the graph is normal. */
    private boolean normal;
    /** Mapping from node identifiers to nodes. */
    private Map<String,AspectNode> nodeIdMap;

    /**
     * Creates an aspect graph from a given (plain) graph.
     * @param graph the plain graph to convert; non-null
     * @return the resulting aspect graph; non-null
     */
    public static AspectGraph newInstance(Graph graph) {
        // map from original graph elements to aspect graph elements
        GraphToAspectMap elementMap = new GraphToAspectMap(graph.getRole());
        GraphRole role = graph.getRole();
        AspectGraph result = new AspectGraph(graph.getName(), role);
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
        Map<Edge,AspectLabel> edgeDataMap = new HashMap<Edge,AspectLabel>();
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
            AspectEdge edgeImage =
                result.addEdge(elementMap.getNode(edge.source()), label,
                    elementMap.getNode(edge.target()));
            elementMap.putEdge(edge, edgeImage);
            if (!edge.source().equals(edge.target())
                && edgeImage.getRole() != EdgeRole.BINARY) {
                errors.add("%s %s must be a node label",
                    label.getRole().getDescription(true), label, edgeImage);
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
        List<Point.Double> dimensions = new ArrayList<Point.Double>();
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
        Map<AspectNode,AspectNode> nodeMap =
            new HashMap<AspectNode,AspectNode>();
        Map<String,AspectNode> sharedNodes = new HashMap<String,AspectNode>();

        // Copy the graphs one by one into the combined graph
        for (AspectGraph graph : graphs) {
            nodeMap.clear();
            LayoutMap oldLayoutMap = GraphInfo.getLayoutMap(graph);
            // Copy the nodes
            for (AspectNode node : graph.nodeSet()) {
                AspectNode fresh = null;
                if (node.getId() != null) {
                    String id = node.getId().getContentString();
                    if (sharedNodes.containsKey(id)) {
                        nodeMap.put(node, sharedNodes.get(id));
                    } else {
                        fresh = node.clone(nodeNr++);
                        sharedNodes.put(id, fresh);
                    }
                } else {
                    fresh = node.clone(nodeNr++);
                }
                if (fresh != null) {
                    newLayoutMap.copyNodeWithOffset(fresh, node, oldLayoutMap,
                        offsetX, offsetY);
                    nodeMap.put(node, fresh);
                    result.addNode(fresh);
                }
            }
            // Copy the edges
            for (AspectEdge edge : graph.edgeSet()) {
                AspectEdge fresh =
                    new AspectEdge(nodeMap.get(edge.source()), edge.label(),
                        nodeMap.get(edge.target()));
                newLayoutMap.copyEdgeWithOffset(fresh, edge, oldLayoutMap,
                    offsetX, offsetY);
                result.addEdgeContext(fresh);
            }
            // Copy the errors
            for (FormatError oldError : GraphInfo.getErrors(graph)) {
                newErrors.add("Error in start graph %s: %s", name, oldError);
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

    /** Factory for AspectGraph elements. */
    public static class AspectFactory implements
            ElementFactory<AspectNode,AspectEdge> {
        /** Private constructor to ensure singleton usage. */
        protected AspectFactory(GraphRole graphRole) {
            this.graphRole = graphRole;
        }

        @Override
        public AspectNode createNode(int nr) {
            this.maxNodeNr = Math.max(this.maxNodeNr, nr);
            return new AspectNode(nr, this.graphRole);
        }

        @Override
        public AspectLabel createLabel(String text) {
            return AspectParser.getInstance().parse(text, this.graphRole);
        }

        @Override
        public AspectEdge createEdge(AspectNode source, String text,
                AspectNode target) {
            return new AspectEdge(source, createLabel(text), target);
        }

        @Override
        public AspectEdge createEdge(AspectNode source, Label label,
                AspectNode target) {
            return new AspectEdge(source, (AspectLabel) label, target);
        }

        @Override
        public AspectGraphMorphism createMorphism() {
            return new AspectGraphMorphism(this.graphRole);
        }

        @Override
        public int getMaxNodeNr() {
            return this.maxNodeNr;
        }

        /** The highest node number returned by this factory. */
        private int maxNodeNr;

        /** The graph role of the created elements. */
        private final GraphRole graphRole;

        /** Returns the singleton instance of this class. */
        static public AspectFactory instance(GraphRole graphRole) {
            return factoryMap.get(graphRole);
        }

        /** Mapping from graph rules to element-producing factories. */
        static private Map<GraphRole,AspectFactory> factoryMap =
            new EnumMap<GraphRole,AspectFactory>(GraphRole.class);

        static {
            factoryMap.put(RULE, new AspectFactory(RULE));
            factoryMap.put(HOST, new AspectFactory(HOST));
            factoryMap.put(TYPE, new AspectFactory(TYPE));
        }
    }

    /** Mapping from one aspect graph to another. */
    public static class AspectGraphMorphism extends
            Morphism<AspectNode,AspectEdge> {
        /** Constructs a new, empty map. */
        public AspectGraphMorphism(GraphRole graphRole) {
            super(AspectFactory.instance(graphRole));
            assert graphRole.inGrammar();
            this.graphRole = graphRole;
        }

        @Override
        public AspectGraphMorphism newMap() {
            return new AspectGraphMorphism(this.graphRole);
        }

        /** The graph role of the created elements. */
        private final GraphRole graphRole;
    }

    private static class AspectToPlainMap extends
            AElementMap<AspectNode,AspectEdge,PlainNode,PlainEdge> {
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
            return getFactory().createEdge(imageSource, key.getPlainLabel(),
                imageTarget);
        }
    }

    /**
     * Graph element map from a plain graph to an aspect graph.
     * @author Arend Rensink
     * @version $Revision $
     */
    private static class GraphToAspectMap extends
            AElementMap<Node,Edge,AspectNode,AspectEdge> {
        /** Creates a fresh, empty map. */
        public GraphToAspectMap(GraphRole graphRole) {
            super(AspectFactory.instance(graphRole));
        }
    }
}
