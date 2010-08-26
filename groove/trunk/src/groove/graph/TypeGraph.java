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
package groove.graph;

import groove.algebra.Algebra;
import groove.algebra.AlgebraRegister;
import groove.graph.algebra.ArgumentEdge;
import groove.graph.algebra.OperatorEdge;
import groove.graph.algebra.ProductNode;
import groove.graph.algebra.VariableNode;
import groove.rel.RegExpr;
import groove.rel.RegExprLabel;
import groove.view.FormatError;
import groove.view.FormatException;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Extends a standard graph with some useful functionality for querying a type
 * graph.
 * @author Arend Rensink
 * @version $Revision $
 */
public class TypeGraph extends NodeSetEdgeSetGraph {
    /** Constructs a fresh type graph. */
    public TypeGraph() {
        GraphInfo.setTypeRole(this);
    }

    @Override
    public boolean addNode(Node node) {
        assert node instanceof TypeNode;
        assert !this.generatedNodes : "Mixed calls of TypeGraph.addNode(Node) and TypeGraph.addNode(Label)";
        this.predefinedNodes = true;
        return super.addNode(node);
    }

    /** 
     * This method is not supported for type graphs; use {@link #addNode(Label)}
     * instead.
     * @throw UnsupportedOperationException always 
     */
    @Override
    public Node addNode() {
        throw new UnsupportedOperationException();
    }

    /**
     * Adds a type node with a given (node type) label.
     * Also adds a self-edge with that label.
     * This method should not be combined with {@link #addNode(Node)} to
     * the same type graph, as then there is no guarantee of distinct node
     * numbers.
     * @param label the label for the type node; must satisfy {@link Label#isNodeType()}
     * @return the created and added node type
     */
    public TypeNode addNode(Label label) {
        assert label.isNodeType();
        assert !this.predefinedNodes : "Mixed calls of TypeGraph.addNode(Node) and TypeGraph.addNode(Label)";
        this.maxNodeNr++;
        TypeNode result = new TypeNode(this.maxNodeNr, label);
        super.addNode(result);
        addEdge(result, label, result);
        this.generatedNodes = true;
        return result;
    }

    /** Flag indicating that {@link #addNode(Label)} has been used
     * to add type nodes.
     */
    private boolean generatedNodes;
    /** Flag indicating that {@link #addNode(Node)} has been used
     * to add type nodes.
     */
    private boolean predefinedNodes;
    /** Highest node number occurring in this type graph. */
    private int maxNodeNr;

    /** Adds the label as well as the edge. */
    @Override
    public Edge addEdge(Node source, Label label, Node target) {
        this.labelStore.addLabel(label);
        return super.addEdge(source, label, target);
    }

    /** Adds the label as well as the edge. */
    @Override
    public Edge addEdge(Node[] ends, Label label) {
        this.labelStore.addLabel(label);
        return super.addEdge(ends, label);
    }

    /** Adds the labels as well as the edges. */
    @Override
    public boolean addEdgeSetWithoutCheck(Collection<? extends Edge> edgeSet) {
        for (Edge edge : edgeSet) {
            this.labelStore.addLabel(edge.label());
        }
        return super.addEdgeSetWithoutCheck(edgeSet);
    }

    /** Adds the label as well as the edge. */
    @Override
    public boolean addEdgeWithoutCheck(Edge edge) {
        this.labelStore.addLabel(edge.label());
        return super.addEdgeWithoutCheck(edge);
    }

    @Override
    public boolean removeEdge(Edge edge) {
        throw new UnsupportedOperationException(
            "Edge removal not allowed in type graphs");
    }

    @Override
    public boolean removeNodeWithoutCheck(Node node) {
        throw new UnsupportedOperationException(
            "Node removal not allowed in type graphs");
    }

    @Override
    public void setFixed() {
        super.setFixed();
        this.labelStore.setFixed();
    }

    /**
     * Tests if a give graph is correct according to this type graph,
     * and if so, returns the mapping from graph nodes to node type labels.
     * @param model the graph to be checked
     * @param parentTypeMap non-null map of nodes to types that have been defined 
     * on a parent level
     * @return mapping from nodes in {@code model} to node type labels
     * @throws FormatException report of typing errors
     */
    public Map<Node,Label> getTyping(Graph model,
            Map<Node,Set<Label>> parentTypeMap) throws FormatException {
        Map<Node,Label> result = new HashMap<Node,Label>();
        Set<FormatError> errors = new TreeSet<FormatError>();
        // detect node types
        Set<Node> untypedNodes = new HashSet<Node>(model.nodeSet());
        for (Edge edge : model.edgeSet()) {
            Node node = edge.source();
            Label label = edge.label();
            if (label.isNodeType()) {
                if (isNodeType(label)) {
                    Label oldLabel = result.put(node, label);
                    if (oldLabel != null) {
                        errors.add(new FormatError(
                            "Duplicate types %s and %s on node '%s'", oldLabel,
                            label, node));
                    } else {
                        Set<Label> parentTypes = parentTypeMap.get(node);
                        if (parentTypes != null) {
                            for (Label parentType : parentTypes) {
                                if (!isSubtype(label, parentType)) {
                                    errors.add(new FormatError(
                                        "Type %s of node '%s' should be subtype of %s",
                                        label, node, parentType));
                                }
                            }
                        }
                    }
                } else if (!(label instanceof RegExprLabel)) {
                    errors.add(new FormatError(
                        "Unknown node type %s for node '%s'", label, node));
                    // though we don't know the type, the node is not untyped
                    untypedNodes.remove(node);
                }
            }
        }
        untypedNodes.removeAll(result.keySet());
        for (Node node : model.nodeSet()) {
            Label nodeType = result.get(node);
            if (nodeType == null) {
                if (node instanceof VariableNode) {
                    Algebra<?> algebra = ((VariableNode) node).getAlgebra();
                    if (algebra != null) {
                        String signature =
                            AlgebraRegister.getSignatureName(algebra);
                        result.put(node, DefaultLabel.createLabel(signature,
                            Label.NODE_TYPE));
                        untypedNodes.remove(node);
                    }
                } else if (node instanceof ProductNode) {
                    untypedNodes.remove(node);
                }
            }
        }
        // add parent node types
        for (Node untypedNode : untypedNodes) {
            Set<Label> parentTypes = parentTypeMap.get(untypedNode);
            if (parentTypes != null && !parentTypes.isEmpty()) {
                // find minimum type among the parent types
                Label type = null;
                for (Label parentType : parentTypes) {
                    if (type == null || isSubtype(parentType, type)) {
                        type = parentType;
                    }
                }
            } else {
                errors.add(new FormatError("Untyped node '%s'", untypedNode));
            }
        }
        for (Edge edge : model.edgeSet()) {
            Label edgeType = edge.label();
            if (isNodeType(edgeType) || edge instanceof ArgumentEdge
                || edge instanceof OperatorEdge) {
                // leave unchecked for now
                continue;
            }
            if (RegExprLabel.isNeg(edgeType)) {
                edgeType = RegExprLabel.getNegOperand(edgeType).toLabel();
            }
            Node source = edge.source();
            Label sourceType = result.get(source);
            Node target = edge.opposite();
            Label targetType = result.get(target);
            if (sourceType == null || targetType == null) {
                // this must be due to an untyped node
                // which was already reported as an error
                continue;
            }
            if (edgeType instanceof RegExprLabel) {
                if (RegExprLabel.isWildcard(edgeType)
                    || RegExprLabel.getRegExpr(edgeType).containsOperator(
                        RegExpr.wildcard())) {
                    errors.add(new FormatError(
                        "Wildcard expression '%s' not allowed in typed graphs",
                        edgeType, edge));
                } else {
                    Set<Node> startNodes = getNodesForType(sourceType);
                    Set<Node> endNodes = getNodesForType(targetType);
                    if (((RegExprLabel) edgeType).getAutomaton(this.labelStore).getMatches(
                        getSaturation(), startNodes, endNodes).isEmpty()) {
                        errors.add(new FormatError(
                            "Regular expression '%s' is incorrectly typed",
                            edgeType, edge));
                    }
                }
            } else if (edgeType instanceof MergeLabel) {
                boolean error = false;
                if (!isSharpType(sourceType)) {
                    errors.add(new FormatError(
                        "Merged %s-node '%s' should be sharply typed",
                        sourceType, source));
                    error = true;
                }
                if (!isSharpType(targetType)) {
                    errors.add(new FormatError(
                        "Merged %s-node '%s' should be sharply typed",
                        targetType, target));
                    error = true;
                }
                if (!error && !sourceType.equals(targetType)) {
                    errors.add(new FormatError(
                        "Merged %s-node '%s' and %s-node '%s' should be identically typed",
                        sourceType, source, targetType, target));
                }
            } else if (edgeType.isFlag()) {
                if (!hasFlag(sourceType, edgeType)) {
                    errors.add(new FormatError(
                        "%s-node '%s' has unknown flag '%s'", sourceType,
                        source, edgeType));
                }
            } else if (edgeType.isBinary()) {
                Label declaredTargetType = getTarget(sourceType, edgeType);
                if (declaredTargetType == null) {
                    errors.add(new FormatError(
                        "%s-node '%s' has unknown edge '%s'", sourceType,
                        source, edgeType, edge));
                } else if (DefaultLabel.isDataType(declaredTargetType)
                    || DefaultLabel.isDataType(targetType)) {
                    if (!targetType.equals(declaredTargetType)) {
                        errors.add(new FormatError(
                            "%s-node '%s' is '%s.%s'-target and hence should be of type %s",
                            targetType, target, sourceType, edgeType,
                            declaredTargetType));
                    }
                } else if (!isSubtype(targetType, declaredTargetType)) {
                    errors.add(new FormatError(
                        "%s-node '%s' is '%s.%s'-target and hence should have %s-subtype",
                        targetType, target, getActualType(sourceType),
                        edgeType, declaredTargetType));
                }
            }
        }
        if (!errors.isEmpty()) {
            throw new FormatException(errors);
        }
        return result;
    }

    /**
     * Tests if a give graph is correct according to this type graph.
     * @param model the graph to be checked
     * @return a list of type errors found in the graph
     */
    public Collection<FormatError> checkTyping(Graph model) {
        try {
            getTyping(model, Collections.<Node,Set<Label>>emptyMap());
            return Collections.emptyList();
        } catch (FormatException e) {
            return e.getErrors();
        }
    }

    /** Tests if a given label is a node type that occurs in this type graph. */
    public boolean isNodeType(Label nodeLabel) {
        return nodeLabel.isNodeType()
            && this.labelStore.getSubtypes(getActualType(nodeLabel)) != null;
    }

    /** Tests for a subtype relation between node labels. */
    public boolean isSubtype(Label subLabel, Label superLabel) {
        assert subLabel.isNodeType() : String.format(
            "Label '%s' is not a node type label", subLabel);
        assert superLabel.isNodeType() : String.format(
            "Label '%s' is not a node type label", superLabel);
        Label actualSubType = getActualType(subLabel);
        if (isSharpType(superLabel)) {
            return actualSubType.equals(getActualType(superLabel));
        } else {
            Set<Label> subtypes = this.labelStore.getSubtypes(superLabel);
            return subtypes != null && subtypes.contains(actualSubType);
        }
    }

    /**
     * Returns the opposite node for a given node type label and outgoing binary
     * edge label, or {@code null} if the binary edge does not occur for the
     * node type or any of its supertypes.
     */
    public Label getTarget(Label nodeLabel, Label label) {
        Node result = null;
        assert nodeLabel.isNodeType() : String.format(
            "Label '%s' is not a node type label", nodeLabel);
        assert label.isBinary() : String.format(
            "Label '%s' is not a binary edge label", label);
        Set<Label> supertypes =
            this.labelStore.getSupertypes(getActualType(nodeLabel));
        Set<? extends Edge> edges = labelEdgeSet(2, label);
        for (Edge edge : edges) {
            if (supertypes.contains(getType(edge.source()))) {
                result = edge.opposite();
                break;
            }
        }
        return result == null ? null : getType(result);
    }

    /** Tests if a given node type (or a supertype) has a given flag. */
    public boolean hasFlag(Label node, Label flag) {
        boolean result = false;
        assert flag.isFlag() : String.format("Label '%s' is not a flag", flag);
        Set<Label> supertypes =
            this.labelStore.getSupertypes(getActualType(node));
        Set<? extends Edge> edges = labelEdgeSet(2, flag);
        for (Edge edge : edges) {
            if (supertypes.contains(getType(edge.source()))) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Adds a subtype node pair to the type graph. The node type labels should
     * already be in the graph.
     * @throws FormatException if the supertype or subtype is not a node type,
     *         or if the new subtype relation creates a cycle.
     */
    public void addSubtype(Node supertypeNode, Node subtypeNode)
        throws FormatException {
        Label supertype = getType(supertypeNode);
        assert supertype != null : String.format(
            "Node '%s' does not have type label", supertypeNode);
        Label subtype = getType(subtypeNode);
        assert subtype != null : String.format(
            "Node '%s' does not have type label", subtypeNode);
        try {
            this.labelStore.addSubtype(supertype, subtype);
        } catch (IllegalArgumentException exc) {
            throw new FormatException(exc.getMessage());
        }
    }

    /** Returns the unique node type label of a given node. */
    private Label getType(Node node) {
        return ((TypeNode) node).getType();
    }

    /** 
     * Indicates if a given type label is a sharp type.
     * Convenience method for {@code RegExprLabel.isSharp(type)}.
     */
    private boolean isSharpType(Label type) {
        return RegExprLabel.isSharp(type);
    }

    /**
     * Returns the actual type label wrapped inside a (possibly sharp) type.
     */
    private Label getActualType(Label type) {
        Label result = RegExprLabel.getSharpLabel(type);
        if (result == null) {
            result = type;
        }
        return result;
    }

    /** Checks if the graph satisfies the properties of a type graph. */
    public void test() throws FormatException {
        Set<String> errors = new TreeSet<String>();
        // Mapping from node types to outgoing edge types to their source types.
        Map<Label,Map<Label,Label>> outTypeMap =
            new HashMap<Label,Map<Label,Label>>();
        for (Edge typeEdge : edgeSet()) {
            if (!typeEdge.label().isNodeType()) {
                Label sourceType = getType(typeEdge.source());
                // check for outgoing edge types from data types
                if (DefaultLabel.isDataType(sourceType)) {
                    errors.add(String.format("Data type '%s' cannot have %s",
                        sourceType, typeEdge.label().isFlag() ? "flags"
                                : "outgoing edges"));
                }
                // check for name shadowing
                for (Label subtype : this.labelStore.getSubtypes(sourceType)) {
                    Map<Label,Label> outTypes = outTypeMap.get(subtype);
                    if (outTypes == null) {
                        outTypeMap.put(subtype, outTypes =
                            new HashMap<Label,Label>());
                    }
                    Label oldType = outTypes.put(typeEdge.label(), sourceType);
                    if (oldType != null) {
                        Label lower, upper;
                        if (this.labelStore.getSubtypes(sourceType).contains(
                            oldType)) {
                            lower = oldType;
                            upper = sourceType;
                        } else {
                            lower = sourceType;
                            upper = oldType;
                        }
                        errors.add(String.format(
                            "Edge type '%s' in '%s' shadows the one in '%s'",
                            typeEdge.label(), lower, upper));
                    }
                }
            }
        }
        if (!errors.isEmpty()) {
            throw new FormatException(errors);
        }
    }

    /** 
     * Returns the set of type nodes for a given node type label.
     * The set is always a singleton.
     */
    private Set<Node> getNodesForType(Label type) {
        Set<Node> result = new HashSet<Node>();
        Set<? extends Edge> edgesForType = labelEdgeSet(2, getActualType(type));
        assert edgesForType.size() == 1;
        result.add(edgesForType.iterator().next().source());
        return result;
    }

    /**
     * Returns the saturated type graph. This is used for checking regular
     * expressions.
     */
    private Graph getSaturation() {
        if (this.saturation == null) {
            this.saturation = computeSaturation();
        }
        return this.saturation;
    }

    /**
     * Computes the saturated type graph.
     * @see #getSaturation()
     */
    private Graph computeSaturation() {
        testFixed(true);
        Graph result = new NodeSetEdgeSetGraph();
        Map<Label,Node> typeNodeMap = new HashMap<Label,Node>();
        for (Node typeNode : nodeSet()) {
            result.addNode(typeNode);
            typeNodeMap.put(getType(typeNode), typeNode);
        }
        for (Edge typeEdge : edgeSet()) {
            Label edgeType = typeEdge.label();
            Label sourceType = getType(typeEdge.source());
            Label targetType = getType(typeEdge.opposite());
            if (edgeType.isBinary()) {
                for (Label sourceSubtype : this.labelStore.getSubtypes(sourceType)) {
                    for (Label targetSubtype : this.labelStore.getSubtypes(targetType)) {
                        result.addEdge(typeNodeMap.get(sourceSubtype),
                            edgeType, typeNodeMap.get(targetSubtype));
                    }
                }
            } else {
                for (Label sourceSubtype : this.labelStore.getSubtypes(sourceType)) {
                    result.addEdge(typeNodeMap.get(sourceSubtype), edgeType,
                        typeNodeMap.get(sourceSubtype));
                }
            }
        }
        return result;
    }

    /** Saturation of the type graph, for checking regular expressions. */
    private Graph saturation;

    /** Returns the labels collected in this type graph. */
    public LabelStore getLabelStore() {
        return this.labelStore;
    }

    /** Label store permanently associated with this type graph. */
    private final LabelStore labelStore = new LabelStore();
}
