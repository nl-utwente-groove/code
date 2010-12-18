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
import groove.trans.DefaultHostGraph;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.trans.RuleLabel;
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
public class TypeGraph extends NodeSetEdgeSetGraph<TypeNode,TypeLabel,TypeEdge> {
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
        for (TypeEdge otherEdge : other.edgeSet()) {
            TypeEdge image =
                addEdge(otherToThis.get(otherEdge.source()), otherEdge.label(),
                    otherToThis.get(otherEdge.target()));
            if (otherEdge.isAbstract()) {
                image.setAbstract();
            }
        }
        this.labelStore.add(other.labelStore);
    }

    @Override
    public boolean addNode(TypeNode node) {
        boolean result = super.addNode(node);
        if (result) {
            assert !this.generatedNodes : "Mixed calls of TypeGraph.addNode(Node) and TypeGraph.addNode(Label)";
            this.predefinedNodes = true;
            TypeNode oldType = this.typeMap.put(node.getType(), node);
            assert oldType == null : String.format(
                "Duplicate type node for %s", oldType.getType());
        }
        return result;
    }

    /**
     * Adds a type node with a given (node type) label.
     * Also adds a self-edge with that label.
     * This method should not be combined with {@link #addNode(TypeNode)} to
     * the same type graph, as then there is no guarantee of distinct node
     * numbers.
     * @param label the label for the type node; must satisfy {@link Label#isNodeType()}
     * @return the created and added node type
     */
    public TypeNode addNode(TypeLabel label) {
        assert label.isNodeType() : String.format(
            "Label %s is not a node type", label);
        TypeNode result = this.typeMap.get(label);
        if (result == null) {
            assert !this.predefinedNodes : "Mixed calls of TypeGraph.addNode(Node) and TypeGraph.addNode(Label)";
            this.maxNodeNr++;
            result = new TypeNode(this.maxNodeNr, label);
            this.typeMap.put(label, result);
            super.addNode(result);
            addEdgeWithoutCheck(createEdge(result, label, result));
            this.generatedNodes = true;
        }
        return result;
    }

    /** Flag indicating that {@link #addNode(TypeLabel)} has been used
     * to add type nodes.
     */
    private boolean generatedNodes;
    /** Flag indicating that {@link #addNode(TypeNode)} has been used
     * to add type nodes.
     */
    private boolean predefinedNodes;
    /** Highest node number occurring in this type graph. */
    private int maxNodeNr;

    /** Adds the label as well as the edge. */
    @Override
    public TypeEdge addEdge(TypeNode source, TypeLabel label, TypeNode target) {
        TypeEdge result = super.addEdge(source, label, target);
        this.labelStore.addLabel(result.label());
        return result;
    }

    /** Adds the labels as well as the edges. */
    @Override
    public boolean addEdgeSetWithoutCheck(Collection<? extends TypeEdge> edgeSet) {
        for (TypeEdge edge : edgeSet) {
            this.labelStore.addLabel(edge.label());
        }
        return super.addEdgeSetWithoutCheck(edgeSet);
    }

    /** Adds the label as well as the edge. */
    @Override
    public boolean addEdgeWithoutCheck(TypeEdge edge) {
        this.labelStore.addLabel(edge.label());
        return super.addEdgeWithoutCheck(edge);
    }

    @Override
    public TypeGraph clone() {
        TypeGraph result = new TypeGraph();
        result.add(this);
        return result;
    }

    @Override
    public TypeGraph newGraph() {
        return new TypeGraph();
    }

    @Override
    public boolean removeEdge(TypeEdge edge) {
        throw new UnsupportedOperationException(
            "Edge removal not allowed in type graphs");
    }

    @Override
    public boolean removeNodeWithoutCheck(TypeNode node) {
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
    @SuppressWarnings("unchecked")
    public <N extends Node,E extends Edge> Typing<N,E> getTyping(
            Graph<N,?,E> model, Map<N,Set<TypeLabel>> parentTypeMap)
        throws FormatException {
        Map<N,TypeLabel> nodeTypeMap = new HashMap<N,TypeLabel>();
        Set<N> sharpNodes = new HashSet<N>();
        Set<Element> abstractElems = new HashSet<Element>();
        Set<FormatError> errors = new TreeSet<FormatError>();
        // detect node types
        Set<N> untypedNodes = new HashSet<N>(model.nodeSet());
        for (Edge edge : model.edgeSet()) {
            N node = (N) edge.source();
            TypeLabel label = getActualType(edge.label());
            if (label != null && label.isNodeType()) {
                if (edge.label() instanceof RuleLabel
                    && ((RuleLabel) edge.label()).isSharp()) {
                    sharpNodes.add(node);
                }
                TypeNode type = getTypeNode(label);
                if (type.isAbstract()) {
                    abstractElems.add(node);
                }
                if (isNodeType(label)) {
                    TypeLabel oldType = nodeTypeMap.put(node, label);
                    if (oldType != null) {
                        errors.add(new FormatError(
                            "Duplicate types %s and %s on node '%s'", oldType,
                            label, node));
                    } else {
                        Set<TypeLabel> parentTypes = parentTypeMap.get(node);
                        if (parentTypes != null) {
                            for (TypeLabel parentType : parentTypes) {
                                if (!isSubtype(label, parentType)) {
                                    errors.add(new FormatError(
                                        "Type %s of node '%s' should be subtype of %s",
                                        label, node, parentType));
                                }
                            }
                        }
                    }
                } else {
                    errors.add(new FormatError(
                        "Unknown node type %s for node '%s'", label, node));
                    // though we don't know the type, the node is not untyped
                    untypedNodes.remove(node);
                }
            }
        }
        for (N node : (Set<N>) model.nodeSet()) {
            if (!nodeTypeMap.containsKey(node)) {
                if (node instanceof VariableNode) {
                    Algebra<?> algebra = ((VariableNode) node).getAlgebra();
                    if (algebra != null) {
                        String signature =
                            AlgebraRegister.getSignatureName(algebra);
                        nodeTypeMap.put(node,
                            TypeLabel.createLabel(signature, Label.NODE_TYPE));
                    }
                } else if (node instanceof ProductNode) {
                    untypedNodes.remove(node);
                }
            }
        }
        untypedNodes.removeAll(nodeTypeMap.keySet());
        // add parent node types
        for (N untypedNode : untypedNodes) {
            Set<TypeLabel> parentTypes = parentTypeMap.get(untypedNode);
            if (parentTypes != null && !parentTypes.isEmpty()) {
                // find minimum type among the parent types
                TypeLabel type = null;
                for (TypeLabel parentType : parentTypes) {
                    if (type == null || isSubtype(parentType, type)) {
                        type = parentType;
                    }
                }
                nodeTypeMap.put(untypedNode, type);
            } else {
                errors.add(new FormatError("Untyped node '%s'", untypedNode));
            }
        }
        for (E edge : model.edgeSet()) {
            Label edgeType = edge.label();
            if (isNodeType(edgeType) || edge instanceof ArgumentEdge
                || edge instanceof OperatorEdge) {
                // leave unchecked for now
                continue;
            }
            if (edgeType instanceof RuleLabel && ((RuleLabel) edgeType).isNeg()) {
                edgeType = ((RuleLabel) edgeType).getNegOperand().toLabel();
            }
            Node source = edge.source();
            TypeLabel sourceType = nodeTypeMap.get(source);
            Node target = edge.target();
            TypeLabel targetType = nodeTypeMap.get(target);
            if (sourceType == null || targetType == null) {
                // this must be due to an untyped node
                // which was already reported as an error
                continue;
            }
            TypeEdge typeEdge = null;
            if (edgeType instanceof RuleLabel) {
                RuleLabel ruleEdgeType = (RuleLabel) edgeType;
                if (ruleEdgeType.getWildcardId() != null) {
                    errors.add(new FormatError(
                        "Wildcard expression '%s' not supported in typed rules",
                        edgeType, edge));
                } else if (ruleEdgeType.isEmpty()) {
                    // this is a (possibly negative) comparison of nodes
                    // which can only be correct if they have a common subtype
                    Set<TypeLabel> commonSubtypes =
                        new HashSet<TypeLabel>(getLabelStore().getSubtypes(
                            sourceType));
                    commonSubtypes.retainAll(getLabelStore().getSubtypes(
                        targetType));
                    if (commonSubtypes.isEmpty()) {
                        errors.add(new FormatError(
                            "Compared nodes %s-node '%s' and %s-node '%s' have no common subtypes",
                            sourceType, source, targetType, target));
                    }
                } else {
                    Set<HostNode> startNodes =
                        Collections.<HostNode>singleton(getSaturationNodeMap().get(
                            sourceType));
                    Set<HostNode> endNodes =
                        Collections.<HostNode>singleton(getSaturationNodeMap().get(
                            targetType));
                    if (ruleEdgeType.getAutomaton(this.labelStore).getMatches(
                        getSaturation(), startNodes, endNodes).isEmpty()) {
                        errors.add(new FormatError(
                            "Regular expression '%s' not matched by path in type graph",
                            edgeType, edge));
                    }
                }
            } else if (edgeType.isFlag()) {
                typeEdge = getTypeEdge(sourceType, (TypeLabel) edgeType);
                if (typeEdge == null) {
                    errors.add(new FormatError(
                        "%s-node '%s' has unknown flag '%s'", sourceType,
                        source, edgeType));
                }
            } else if (edgeType.isBinary()) {
                typeEdge = getTypeEdge(sourceType, (TypeLabel) edgeType);
                if (typeEdge == null) {
                    errors.add(new FormatError(
                        "%s-node '%s' has unknown edge '%s'", sourceType,
                        source, edgeType, edge));
                } else {
                    TypeLabel declaredTargetType =
                        (typeEdge.target()).getType();
                    if (declaredTargetType.isDataType()
                        || targetType.isDataType()) {
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
            if (typeEdge != null && typeEdge.isAbstract()) {
                abstractElems.add(edge);
            }
        }
        if (!errors.isEmpty()) {
            throw new FormatException(errors);
        }
        return new Typing<N,E>(nodeTypeMap, sharpNodes, abstractElems);
    }

    /**
     * Tests if a give graph is correct according to this type graph.
     * This is a convenience method for {@code getTyping(mode, Collections.emptyMap())}.
     * @param model the graph to be checked
     * @return the typing of the model
     * @throws FormatException a report of the errors found during typing
     */
    public <N extends Node,E extends Edge> Typing<N,E> checkTyping(
            Graph<N,?,E> model) throws FormatException {
        return getTyping(model, Collections.<N,Set<TypeLabel>>emptyMap());
    }

    /** Tests if a given label is a node type that occurs in this type graph. */
    public boolean isNodeType(Label nodeLabel) {
        return this.typeMap.containsKey(getActualType(nodeLabel));
    }

    /** Tests for a subtype relation between node labels. */
    public boolean isSubtype(TypeLabel subLabel, TypeLabel superLabel) {
        assert subLabel.isNodeType() : String.format(
            "Label '%s' is not a node type label", subLabel);
        assert superLabel.isNodeType() : String.format(
            "Label '%s' is not a node type label", superLabel);
        Label actualSubType = getActualType(subLabel);
        if (isSharpType(superLabel)) {
            return actualSubType.equals(getActualType(superLabel));
        } else {
            Set<TypeLabel> subtypes = this.labelStore.getSubtypes(superLabel);
            return subtypes != null && subtypes.contains(actualSubType);
        }
    }

    /**
     * Returns the type edge for a given node type label and outgoing
     * edge label, or {@code null} if the edge label does not occur for the
     * node type or any of its supertypes.
     */
    public TypeEdge getTypeEdge(TypeLabel sourceType, TypeLabel label) {
        TypeEdge result = null;
        TypeLabel resultType = null;
        assert sourceType.isNodeType() : String.format(
            "Label '%s' is not a node type label", sourceType);
        Set<TypeLabel> supertypes =
            this.labelStore.getSupertypes(getActualType(sourceType));
        if (supertypes != null) {
            Set<? extends TypeEdge> edges = labelEdgeSet(label);
            for (TypeEdge edge : edges) {
                TypeLabel edgeType = getType(edge.source());
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
        TypeLabel supertype = getType(supertypeNode);
        assert supertype != null : String.format(
            "Node '%s' does not have type label", supertypeNode);
        TypeLabel subtype = getType(subtypeNode);
        assert subtype != null : String.format(
            "Node '%s' does not have type label", subtypeNode);
        try {
            this.labelStore.addSubtype(supertype, subtype);
        } catch (IllegalArgumentException exc) {
            throw new FormatException(exc.getMessage());
        }
    }

    /** Returns the unique node type label of a given node. */
    private TypeLabel getType(Node node) {
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
        return type instanceof RuleLabel && ((RuleLabel) type).isSharp();
    }

    /**
     * Returns the actual type label wrapped inside a (possibly sharp) type.
     * Returns {@code null} if the label is an operator or argument edge.
     */
    private TypeLabel getActualType(Label type) {
        TypeLabel result;
        if (type instanceof RuleLabel) {
            RuleLabel ruleLabel = (RuleLabel) type;
            result = ruleLabel.getTypeLabel();
        } else {
            assert type instanceof TypeLabel;
            result = (TypeLabel) type;
        }
        return result;
    }

    /** Checks if the graph satisfies the properties of a type graph. */
    public void test() throws FormatException {
        Set<String> errors = new TreeSet<String>();
        // Mapping from node types to outgoing non-abstract edge types to their
        // source types
        Map<TypeLabel,Map<TypeLabel,TypeLabel>> outTypeMap =
            new HashMap<TypeLabel,Map<TypeLabel,TypeLabel>>();
        for (TypeEdge typeEdge : edgeSet()) {
            if (!typeEdge.label().isNodeType() && !typeEdge.isAbstract()) {
                TypeLabel sourceType = getType(typeEdge.source());
                // check for outgoing edge types from data types
                if (sourceType.isDataType()) {
                    errors.add(String.format("Data type '%s' cannot have %s",
                        sourceType, typeEdge.label().isFlag() ? "flags"
                                : "outgoing edges"));
                }
                // check for name shadowing
                for (TypeLabel subtype : this.labelStore.getSubtypes(sourceType)) {
                    Map<TypeLabel,TypeLabel> outTypes = outTypeMap.get(subtype);
                    if (outTypes == null) {
                        outTypeMap.put(subtype, outTypes =
                            new HashMap<TypeLabel,TypeLabel>());
                    }
                    TypeLabel oldType =
                        outTypes.put(typeEdge.label(), sourceType);
                    if (oldType != null) {
                        TypeLabel lower, upper;
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
    private Map<TypeLabel,HostNode> getSaturationNodeMap() {
        if (this.saturationNodeMap == null) {
            initSaturation();
        }
        return this.saturationNodeMap;
    }

    /**
     * Returns the saturated type graph. This is used for checking regular
     * expressions.
     */
    private HostGraph getSaturation() {
        if (this.saturation == null) {
            initSaturation();
        }
        return this.saturation;
    }

    /**
     * Computes the saturated type graph.
     * @see #getSaturation()
     */
    private void initSaturation() {
        testFixed(true);
        HostGraph result = new DefaultHostGraph();
        Map<TypeLabel,HostNode> typeNodeMap = new HashMap<TypeLabel,HostNode>();
        /* Inverse subtyping-saturated set of flag-bearing node types. */
        Set<TypeLabel> flaggedNodes = new HashSet<TypeLabel>();
        /* Inverse subtype-saturated mapping from node types to all outgoing binary edge neighbours. */
        Map<TypeLabel,Set<TypeLabel>> connectMap =
            new HashMap<TypeLabel,Set<TypeLabel>>();
        for (TypeNode typeNode : nodeSet()) {
            HostNode nodeImage = result.addNode();
            typeNodeMap.put(getType(typeNode), nodeImage);
            connectMap.put(getType(typeNode), new HashSet<TypeLabel>());
        }
        for (TypeEdge typeEdge : edgeSet()) {
            TypeLabel edgeType = typeEdge.label();
            TypeLabel sourceType = getType(typeEdge.source());
            TypeLabel targetType = getType(typeEdge.target());
            if (edgeType.isBinary()) {
                for (TypeLabel sourceSubtype : this.labelStore.getSubtypes(sourceType)) {
                    for (TypeLabel targetSubtype : this.labelStore.getSubtypes(targetType)) {
                        result.addEdge(typeNodeMap.get(sourceSubtype),
                            edgeType, typeNodeMap.get(targetSubtype));
                    }
                }
                if (connectMap.get(sourceType).add(targetType)) {
                    for (TypeLabel sourceSuperType : this.labelStore.getSupertypes(sourceType)) {
                        connectMap.get(sourceSuperType).addAll(
                            this.labelStore.getSupertypes(targetType));
                    }
                }
            } else {
                for (TypeLabel sourceSubtype : this.labelStore.getSubtypes(sourceType)) {
                    result.addEdge(typeNodeMap.get(sourceSubtype), edgeType,
                        typeNodeMap.get(sourceSubtype));
                }
                if (edgeType.isFlag() && flaggedNodes.add(sourceType)) {
                    for (TypeLabel sourceSuperType : this.labelStore.getSupertypes(sourceType)) {
                        flaggedNodes.add(sourceSuperType);
                    }
                }
            }
        }
        for (Map.Entry<TypeLabel,Set<TypeLabel>> connectEntry : connectMap.entrySet()) {
            for (Label targetType : connectEntry.getValue()) {
                HostNode source = typeNodeMap.get(connectEntry.getKey());
                HostNode target = typeNodeMap.get(targetType);
                result.addEdge(source,
                    MatrixAutomaton.getDummyLabel(Label.BINARY), target);
            }
        }
        for (Label flaggedType : flaggedNodes) {
            HostNode source = typeNodeMap.get(flaggedType);
            result.addEdge(source, MatrixAutomaton.getDummyLabel(Label.FLAG),
                source);
        }
        this.saturationNodeMap = typeNodeMap;
        this.saturation = result;
    }

    /** Mapping from type nodes to saturation graph nodes. */
    private Map<TypeLabel,HostNode> saturationNodeMap;
    /** Saturation of the type graph, for checking regular expressions. */
    private HostGraph saturation;

    /** Returns the labels collected in this type graph. */
    public LabelStore getLabelStore() {
        return this.labelStore;
    }

    @Override
    protected boolean isTypeCorrect(Node node) {
        return node instanceof TypeNode;
    }

    @Override
    protected boolean isTypeCorrect(Edge edge) {
        return edge instanceof TypeEdge;
    }

    @Override
    public TypeFactory getFactory() {
        return TypeFactory.instance();
    }

    /** Label store permanently associated with this type graph. */
    private final LabelStore labelStore = new LabelStore();
    /** Mapping from node type labels to the corresponding type nodes. */
    private final Map<Label,TypeNode> typeMap = new HashMap<Label,TypeNode>();

    /**
     * Class encoding the typing information discovered for a graph in
     * {@link TypeGraph#getTyping(Graph, Map)}.
     */
    public static class Typing<N extends Node,E extends Edge> {
        private Typing(Map<N,TypeLabel> nodeTypeMap, Set<N> sharpNodes,
                Set<Element> abstractElems) {
            this.nodeTypeMap = nodeTypeMap;
            this.sharpNodes = sharpNodes;
            this.abstractElems = abstractElems;
        }

        /** Returns an unmodifiable view on the node type map. */
        public Map<N,TypeLabel> getTypeMap() {
            return Collections.unmodifiableMap(this.nodeTypeMap);
        }

        /** Returns the node type label for a given graph node. */
        public TypeLabel getType(N node) {
            return this.nodeTypeMap.get(node);
        }

        /** Indicates if a given graph node is sharply typed. */
        public boolean isSharp(N node) {
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

        private final Map<N,TypeLabel> nodeTypeMap;
        private final Set<N> sharpNodes;
        private final Set<Element> abstractElems;
    }
}
