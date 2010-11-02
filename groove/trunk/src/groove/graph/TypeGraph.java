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
import groove.rel.MatrixAutomaton;
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

    /** 
     * Adds type nodes and edges from another type graph.
     * Equally labelled type nodes are merged.
     * This may change the node numbering of the other type graph.
     */
    public void add(TypeGraph other) {
        Map<TypeNode,TypeNode> otherToThis = new HashMap<TypeNode,TypeNode>();
        for (Node otherNode : other.nodeSet()) {
            TypeNode otherTypeNode = (TypeNode) otherNode;
            TypeNode image = addNode(otherTypeNode.getType());
            otherToThis.put(otherTypeNode, image);
        }
        for (Edge otherEdge : other.edgeSet()) {
            Edge image =
                addEdge(otherToThis.get(otherEdge.source()), otherEdge.label(),
                    otherToThis.get(otherEdge.target()));
            if (other.isAbstract(otherEdge)) {
                setAbstract(image);
            }
        }
        this.labelStore.add(other.labelStore);
    }

    @Override
    public boolean addNode(Node node) {
        assert node instanceof TypeNode;
        assert !this.generatedNodes : "Mixed calls of TypeGraph.addNode(Node) and TypeGraph.addNode(Label)";
        this.predefinedNodes = true;
        TypeNode oldType =
            this.typeMap.put(((TypeNode) node).getType(), (TypeNode) node);
        assert oldType == null || oldType.equals(node) : String.format(
            "Duplicate type node for %s", oldType.getType());
        return super.addNode(node);
    }

    /** 
     * This method is not supported for type graphs; use {@link #addNode(Label)}
     * instead.
     * @throws UnsupportedOperationException always 
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
        assert label.isNodeType() : String.format(
            "Label %s is not a node type", label);
        assert !RegExprLabel.isSharp(label) : String.format(
            "Can't use sharp type %s in type graph", label);
        assert !this.predefinedNodes : "Mixed calls of TypeGraph.addNode(Node) and TypeGraph.addNode(Label)";
        TypeNode result = this.typeMap.get(label);
        if (result == null) {
            this.maxNodeNr++;
            result = new TypeNode(this.maxNodeNr, label);
            this.typeMap.put(label, result);
            addEdge(result, label, result);
            this.generatedNodes = true;
        }
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

    /** Sets a given edge type to abstract. */
    public void setAbstract(Edge edge) {
        this.abstractTypes.add(edge);
        if (edge.label().isNodeType()) {
            this.abstractTypes.add(edge.source());
        }
    }

    /** Indicates if a given type is abstract. */
    public boolean isAbstract(Element elem) {
        return this.abstractTypes.contains(elem);
    }

    /** Adds the label as well as the edge. */
    @Override
    @Deprecated
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
    public Typing getTyping(Graph model, Map<Node,Set<Label>> parentTypeMap)
        throws FormatException {
        Map<Node,Label> nodeTypeMap = new HashMap<Node,Label>();
        Set<Node> sharpNodes = new HashSet<Node>();
        Set<Element> abstractElems = new HashSet<Element>();
        Set<FormatError> errors = new TreeSet<FormatError>();
        // detect node types
        Set<Node> untypedNodes = new HashSet<Node>(model.nodeSet());
        for (Edge edge : model.edgeSet()) {
            Node node = edge.source();
            Label label = getActualType(edge.label());
            if (label.isNodeType()) {
                if (RegExprLabel.isSharp(edge.label())) {
                    sharpNodes.add(node);
                }
                TypeNode type = getTypeNode(label);
                if (isAbstract(type)) {
                    abstractElems.add(node);
                }
                if (isNodeType(label)) {
                    Label oldType = nodeTypeMap.put(node, label);
                    if (oldType != null) {
                        errors.add(new FormatError(
                            "Duplicate types %s and %s on node '%s'", oldType,
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
        for (Node node : model.nodeSet()) {
            if (!nodeTypeMap.containsKey(node)) {
                if (node instanceof VariableNode) {
                    Algebra<?> algebra = ((VariableNode) node).getAlgebra();
                    if (algebra != null) {
                        String signature =
                            AlgebraRegister.getSignatureName(algebra);
                        nodeTypeMap.put(node, DefaultLabel.createLabel(
                            signature, Label.NODE_TYPE));
                    }
                } else if (node instanceof ProductNode) {
                    untypedNodes.remove(node);
                }
            }
        }
        untypedNodes.removeAll(nodeTypeMap.keySet());
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
                nodeTypeMap.put(untypedNode, type);
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
            Label sourceType = nodeTypeMap.get(source);
            Node target = edge.target();
            Label targetType = nodeTypeMap.get(target);
            if (sourceType == null || targetType == null) {
                // this must be due to an untyped node
                // which was already reported as an error
                continue;
            }
            Edge typeEdge = null;
            if (edgeType instanceof RegExprLabel) {
                if (RegExprLabel.getWildcardId(edgeType) != null) {
                    errors.add(new FormatError(
                        "Wildcard expression '%s' not supported in typed rules",
                        edgeType, edge));
                } else {
                    Set<Node> startNodes =
                        Collections.<Node>singleton(getTypeNode(sourceType));
                    Set<Node> endNodes =
                        Collections.<Node>singleton(getTypeNode(targetType));
                    if (((RegExprLabel) edgeType).getAutomaton(this.labelStore).getMatches(
                        getSaturation(), startNodes, endNodes).isEmpty()) {
                        errors.add(new FormatError(
                            "Regular expression '%s' not matched by path in type graph",
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
                typeEdge = getTypeEdge(sourceType, edgeType);
                if (typeEdge == null) {
                    errors.add(new FormatError(
                        "%s-node '%s' has unknown flag '%s'", sourceType,
                        source, edgeType));
                }
            } else if (edgeType.isBinary()) {
                typeEdge = getTypeEdge(sourceType, edgeType);
                if (typeEdge == null) {
                    errors.add(new FormatError(
                        "%s-node '%s' has unknown edge '%s'", sourceType,
                        source, edgeType, edge));
                } else {
                    Label declaredTargetType =
                        ((TypeNode) typeEdge.target()).getType();
                    if (DefaultLabel.isDataType(declaredTargetType)
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
            if (isAbstract(typeEdge)) {
                abstractElems.add(edge);
            }
        }
        if (!errors.isEmpty()) {
            throw new FormatException(errors);
        }
        return new Typing(nodeTypeMap, sharpNodes, abstractElems);
    }

    /**
     * Tests if a give graph is correct according to this type graph.
     * This is a convenience method for {@code getTyping(mode, Collections.emptyMap())}.
     * @param model the graph to be checked
     * @return the typing of the model
     * @throws FormatException a report of the errors found during typing
     */
    public Typing checkTyping(Graph model) throws FormatException {
        return getTyping(model, Collections.<Node,Set<Label>>emptyMap());
    }

    /** Tests if a given label is a node type that occurs in this type graph. */
    public boolean isNodeType(Label nodeLabel) {
        return this.typeMap.containsKey(getActualType(nodeLabel));
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
     * Returns the type edge for a given node type label and outgoing
     * edge label, or {@code null} if the edge label does not occur for the
     * node type or any of its supertypes.
     */
    public Edge getTypeEdge(Label sourceType, Label label) {
        Edge result = null;
        Label resultType = null;
        assert sourceType.isNodeType() : String.format(
            "Label '%s' is not a node type label", sourceType);
        Set<Label> supertypes =
            this.labelStore.getSupertypes(getActualType(sourceType));
        if (supertypes != null) {
            Set<? extends Edge> edges = labelEdgeSet(label);
            for (Edge edge : edges) {
                Label edgeType = getType(edge.source());
                if (supertypes.contains(edgeType)) {
                    if (result == null || isSubtype(edgeType, resultType)) {
                        result = edge;
                        resultType = edgeType;
                    }
                }
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
     * Returns the type node corresponding to a given node type label,
     * if there is such a node in the type graph. Returns {@code null}
     * if the label is not a known node type.
     */
    private TypeNode getTypeNode(Label label) {
        return this.typeMap.get(getActualType(label));
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
        // Mapping from node types to outgoing non-abstract edge types to their
        // source types
        Map<Label,Map<Label,Label>> outTypeMap =
            new HashMap<Label,Map<Label,Label>>();
        for (Edge typeEdge : edgeSet()) {
            if (!typeEdge.label().isNodeType() && !isAbstract(typeEdge)) {
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
        /* Inverse subtyping-saturated set of flag-bearing node types. */
        Set<Label> flaggedNodes = new HashSet<Label>();
        /* Inverse subtype-saturated mapping from node types to all outgoing binary edge neighbours. */
        Map<Label,Set<Label>> connectMap = new HashMap<Label,Set<Label>>();
        for (Node typeNode : nodeSet()) {
            result.addNode(typeNode);
            typeNodeMap.put(getType(typeNode), typeNode);
            connectMap.put(getType(typeNode), new HashSet<Label>());
        }
        for (Edge typeEdge : edgeSet()) {
            Label edgeType = typeEdge.label();
            Label sourceType = getType(typeEdge.source());
            Label targetType = getType(typeEdge.target());
            if (edgeType.isBinary()) {
                for (Label sourceSubtype : this.labelStore.getSubtypes(sourceType)) {
                    for (Label targetSubtype : this.labelStore.getSubtypes(targetType)) {
                        result.addEdge(typeNodeMap.get(sourceSubtype),
                            edgeType, typeNodeMap.get(targetSubtype));
                    }
                }
                if (connectMap.get(sourceType).add(targetType)) {
                    for (Label sourceSuperType : this.labelStore.getSupertypes(sourceType)) {
                        connectMap.get(sourceSuperType).addAll(
                            this.labelStore.getSupertypes(targetType));
                    }
                }
            } else {
                for (Label sourceSubtype : this.labelStore.getSubtypes(sourceType)) {
                    result.addEdge(typeNodeMap.get(sourceSubtype), edgeType,
                        typeNodeMap.get(sourceSubtype));
                }
                if (edgeType.isFlag() && flaggedNodes.add(sourceType)) {
                    for (Label sourceSuperType : this.labelStore.getSupertypes(sourceType)) {
                        flaggedNodes.add(sourceSuperType);
                    }
                }
            }
        }
        for (Map.Entry<Label,Set<Label>> connectEntry : connectMap.entrySet()) {
            for (Label targetType : connectEntry.getValue()) {
                Node source = typeNodeMap.get(connectEntry.getKey());
                Node target = typeNodeMap.get(targetType);
                result.addEdge(source,
                    MatrixAutomaton.getDummyLabel(Label.BINARY), target);
            }
        }
        for (Label flaggedType : flaggedNodes) {
            Node source = typeNodeMap.get(flaggedType);
            result.addEdge(source, MatrixAutomaton.getDummyLabel(Label.FLAG),
                source);
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
    /** Mapping from node type labels to the corresponding type nodes. */
    private final Map<Label,TypeNode> typeMap = new HashMap<Label,TypeNode>();
    /** Set of abstract type edges. */
    private final Set<Element> abstractTypes = new HashSet<Element>();

    /**
     * Class encoding the typing information discovered for a graph in
     * {@link TypeGraph#getTyping(Graph, Map)}.
     */
    public static class Typing {
        private Typing(Map<Node,Label> nodeTypeMap, Set<Node> sharpNodes,
                Set<Element> abstractElems) {
            this.nodeTypeMap = nodeTypeMap;
            this.sharpNodes = sharpNodes;
            this.abstractElems = abstractElems;
        }

        /** Returns an unmodifiable view on the node type map. */
        public Map<Node,Label> getTypeMap() {
            return Collections.unmodifiableMap(this.nodeTypeMap);
        }

        /** Returns the node type label for a given graph node. */
        public Label getType(Node node) {
            return this.nodeTypeMap.get(node);
        }

        /** Indicates if a given graph node is sharply typed. */
        public boolean isSharp(Node node) {
            return this.sharpNodes.contains(node);
        }

        /** Returns the set of abstractly typed elements. */
        public Set<Element> getAbstractElements() {
            return Collections.unmodifiableSet(this.abstractElems);
        }

        /** Indicates if a given graph element is abstractly typed. */
        public boolean isAbstract(Element elem) {
            return this.abstractElems.contains(elem);
        }

        private final Map<Node,Label> nodeTypeMap;
        private final Set<Node> sharpNodes;
        private final Set<Element> abstractElems;
    }
}
