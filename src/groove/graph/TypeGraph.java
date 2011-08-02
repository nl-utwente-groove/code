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

import static groove.graph.GraphRole.TYPE;
import groove.graph.algebra.ProductNode;
import groove.graph.algebra.ValueNode;
import groove.graph.algebra.VariableNode;
import groove.rel.MatrixAutomaton;
import groove.trans.DefaultHostGraph;
import groove.trans.HostEdge;
import groove.trans.HostFactory;
import groove.trans.HostGraph;
import groove.trans.HostGraphMorphism;
import groove.trans.HostNode;
import groove.trans.RuleEdge;
import groove.trans.RuleFactory;
import groove.trans.RuleGraph;
import groove.trans.RuleGraphMorphism;
import groove.trans.RuleLabel;
import groove.trans.RuleNode;
import groove.view.FormatError;
import groove.view.FormatException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Extends a standard graph with some useful functionality for querying a type
 * graph.
 * @author Arend Rensink
 * @version $Revision $
 */
public class TypeGraph extends NodeSetEdgeSetGraph<TypeNode,TypeEdge> {
    /** Constructs a fresh type graph. 
     * @param name the (non-{@code null}) name of the type graph 
     */
    public TypeGraph(String name) {
        super(name);
    }

    @Override
    public GraphRole getRole() {
        return TYPE;
    }

    /** 
     * Adds type nodes and edges from another type graph.
     * Equally labelled type nodes are merged.
     * This may change the node numbering of the other type graph.
     * @return mapping from nodes in the added type graph to the corresponding nodes
     * in this type graph
     */
    public Map<TypeNode,TypeNode> add(TypeGraph other) {
        Map<TypeNode,TypeNode> otherToThis = new HashMap<TypeNode,TypeNode>();
        for (Node otherNode : other.nodeSet()) {
            TypeNode otherTypeNode = (TypeNode) otherNode;
            TypeNode image = addNode(otherTypeNode.getLabel());
            image.setAbstract(otherTypeNode.isAbstract());
            image.setColor(otherTypeNode.getColor());
            image.setLabelPattern(otherTypeNode.getLabelPattern());
            boolean imported = image.isImported() && otherTypeNode.isImported();
            image.setImported(imported);
            if (imported) {
                this.imports.add(image);
            } else {
                this.imports.remove(image);
            }
            otherToThis.put(otherTypeNode, image);
        }
        for (TypeEdge otherEdge : other.edgeSet()) {
            TypeEdge image =
                addEdge(otherToThis.get(otherEdge.source()), otherEdge.label(),
                    otherToThis.get(otherEdge.target()));
            image.setAbstract(otherEdge.isAbstract());
        }
        this.labelStore.add(other.labelStore);
        return otherToThis;
    }

    @Override
    public boolean addNode(TypeNode node) {
        boolean result = super.addNode(node);
        if (result) {
            assert !this.generatedNodes : "Mixed calls of TypeGraph.addNode(Node) and TypeGraph.addNode(Label)";
            this.predefinedNodes = true;
            TypeNode oldType = this.typeMap.put(node.getLabel(), node);
            assert oldType == null : String.format(
                "Duplicate type node for %s", oldType.getLabel());
            if (node.isImported()) {
                this.imports.add(node);
            }
            this.labelStore.addLabel(node.getLabel());
            if (node.getColor() != null) {
                this.labelStore.setColor(node.getLabel(), node.getColor());
            }
            if (node.getLabelPattern() != null) {
                this.labelStore.setPattern(node.getLabel(),
                    node.getLabelPattern());
            }
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
            result.setImported(true);
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
    public TypeEdge addEdge(TypeNode source, Label label, TypeNode target) {
        TypeEdge result = super.addEdge(source, label, target);
        this.labelStore.addLabel(result.label());
        if (label.isNodeType() && source.getColor() != null) {
            this.labelStore.setColor(result.label(), source.getColor());
        }
        if (label.isNodeType() && source.getLabelPattern() != null) {
            this.labelStore.setPattern(result.label(), source.getLabelPattern());
        }
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
        TypeGraph result = new TypeGraph(getName());
        result.add(this);
        return result;
    }

    @Override
    public TypeGraph newGraph(String name) {
        return new TypeGraph(getName());
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

    @Override
    public TypeFactory getFactory() {
        return TypeFactory.instance();
    }

    /**
     * Attempts to find a typing for a given rule graph.
     * @param source the rule graph to be typed
     * @param parentTyping typing on the next higher nesting level
     * @return a morphism from the rule graph to a typed version
     * @throws FormatException if the rule graph contains type errors
     */
    public RuleGraphMorphism analyzeRule(RuleGraph source,
            RuleGraphMorphism parentTyping) throws FormatException {
        RuleFactory ruleFactory = RuleFactory.instance();
        RuleGraphMorphism morphism = new RuleGraphMorphism();
        Map<RuleNode,RuleLabel> nodeTypeMap = new HashMap<RuleNode,RuleLabel>();
        if (!isDegenerate()) {
            detectNodeTypes(source, nodeTypeMap);
        }
        Set<FormatError> errors = new TreeSet<FormatError>();
        for (RuleNode node : source.nodeSet()) {
            RuleNode image;
            if (node instanceof VariableNode || node instanceof ProductNode) {
                image = node;
            } else if (isDegenerate()) {
                image =
                    ruleFactory.createNode(node.getNumber(), TypeNode.TOP_NODE,
                        true);
            } else {
                // get the type from the parent typing or from the node type edges
                RuleLabel typingLabel = nodeTypeMap.get(node);
                RuleNode parentImage =
                    parentTyping == null ? null : parentTyping.getNode(node);
                if (typingLabel == null) {
                    if (parentImage == null) {
                        errors.add(new FormatError("Untyped node", node));
                        image = node;
                    } else {
                        image = parentImage;
                    }
                } else {
                    TypeNode type = getTypeNode(typingLabel);
                    if (parentImage == null) {
                        image =
                            ruleFactory.createNode(node.getNumber(), type,
                                typingLabel.isSharp());
                    } else {
                        TypeNode parentType = parentImage.getType();
                        if (!isSubtype(type, parentType)) {
                            errors.add(new FormatError(
                                "Node type %s should specialise %s", type,
                                parentType, node));
                        }
                        image = parentImage;
                    }
                }
            }
            morphism.putNode(node, image);
        }
        for (RuleEdge edge : source.edgeSet()) {
            RuleLabel edgeType = edge.label();
            if (edgeType.isNodeType() && !isDegenerate()) {
                // we already dealt with node types
                continue;
            }
            // invert negated edges
            if (edgeType.isNeg()) {
                edgeType = edgeType.getNegOperand().toLabel();
            }
            RuleNode sourceKey = edge.source();
            RuleNode targetKey = edge.target();
            RuleNode sourceImage = morphism.getNode(sourceKey);
            RuleNode targetImage = morphism.getNode(targetKey);
            if (sourceImage == null || targetImage == null) {
                // this must be due to an untyped node
                // which was already reported as an error
                continue;
            }
            TypeNode sourceType = sourceImage.getType();
            TypeNode targetType = targetImage.getType();
            TypeEdge typeEdge = null;
            if (!edgeType.isAtom()) {
                if (edgeType.isEmpty()) {
                    // this is a (possibly negative) comparison of nodes
                    // which can only be correct if they have a common subtype
                    if (!hasCommonSubtype(sourceImage.getType(),
                        targetImage.getType())) {
                        errors.add(new FormatError(
                            "Node types %s and %s have no common subtypes",
                            sourceType, targetType, sourceKey, targetKey));
                    }
                } else if (edgeType.getMatchExpr() != null) {
                    // this is a regular expression, which is matched against
                    // the saturated type graph
                    Set<HostNode> startNodes =
                        Collections.<HostNode>singleton(getSaturationNodeMap().get(
                            sourceType.getLabel()));
                    Set<HostNode> endNodes =
                        Collections.<HostNode>singleton(getSaturationNodeMap().get(
                            targetType.getLabel()));
                    if (edgeType.getAutomaton(this.labelStore).getMatches(
                        getSaturation(), startNodes, endNodes).isEmpty()) {
                        errors.add(new FormatError(
                            "Regular expression '%s' not matched by path in type graph",
                            edgeType, source, edge));
                    }
                }
                morphism.putEdge(edge,
                    ruleFactory.createEdge(sourceImage, edgeType, targetImage));
            } else {
                typeEdge =
                    getTypeEdge(sourceType, getActualType(edgeType), targetType);
                if (typeEdge == null) {
                    errors.add(new FormatError("%s-node has unknown %s '%s'",
                        sourceType, edge.getRole().getDescription(false),
                        edgeType, source));
                } else {
                    morphism.putEdge(edge, ruleFactory.createEdge(sourceImage,
                        typeEdge, targetImage));
                }
            }
        }
        if (!errors.isEmpty()) {
            throw new FormatException(errors);
        }
        return morphism;

    }

    /**
     * Attempts to find a typing for a given host graph.
     * @param source the rule graph to be typed
     * @return a morphism from the rule graph to a typed version
     * @throws FormatException if the rule graph contains type errors
     */
    public HostGraphMorphism analyzeHost(HostGraph source)
        throws FormatException {
        HostFactory hostFactory = HostFactory.newInstance();
        HostGraphMorphism morphism = new HostGraphMorphism(hostFactory);
        Map<HostNode,TypeLabel> nodeTypeMap = new HashMap<HostNode,TypeLabel>();
        if (!isDegenerate()) {
            detectNodeTypes(source, nodeTypeMap);
        }
        Set<FormatError> errors = new TreeSet<FormatError>();
        for (HostNode node : source.nodeSet()) {
            HostNode image;
            if (node instanceof ValueNode) {
                image = node;
            } else if (isDegenerate()) {
                image =
                    hostFactory.createNode(node.getNumber(), TypeNode.TOP_NODE);
            } else {
                TypeLabel typingLabel = nodeTypeMap.get(node);
                if (typingLabel == null) {
                    errors.add(new FormatError("Untyped node", node));
                    image = node;
                } else {
                    TypeNode type = getTypeNode(typingLabel);
                    if (type.isAbstract()) {
                        errors.add(new FormatError("Abstract node type '%s'",
                            type, node));
                    }
                    image = hostFactory.createNode(node.getNumber(), type);
                }
            }
            morphism.putNode(node, image);
        }
        for (HostEdge edge : source.edgeSet()) {
            TypeLabel edgeType = edge.label();
            if (edgeType.isNodeType() && !isDegenerate()) {
                // we already dealt with node types
                continue;
            }
            HostNode sourceImage = morphism.getNode(edge.source());
            HostNode targetImage = morphism.getNode(edge.target());
            if (sourceImage == null || targetImage == null) {
                // this must be due to an untyped node
                // which was already reported as an error
                continue;
            }
            TypeNode sourceType = sourceImage.getType();
            TypeNode targetType = targetImage.getType();
            TypeEdge typeEdge =
                getTypeEdge(sourceType, getActualType(edgeType), targetType);
            if (typeEdge == null) {
                errors.add(new FormatError("%s-node has unknown %s '%s'",
                    sourceType, edgeType.getRole().getDescription(false),
                    edgeType.text(), source));
            } else if (typeEdge.isAbstract()) {
                errors.add(new FormatError("%s-node has abstract %s '%s'",
                    sourceType, edgeType.getRole().getDescription(false),
                    edgeType.text(), source));
            } else {
                morphism.putEdge(edge,
                    hostFactory.createEdge(sourceImage, typeEdge, targetImage));
            }
        }
        if (!errors.isEmpty()) {
            throw new FormatException(errors);
        }
        return morphism;

    }

    /**
     * Indicates if this is a degenerate type graph,
     * i.e. one without true node types  
     */
    public boolean isDegenerate() {
        return containsNode(TypeNode.TOP_NODE);
    }

    /**
     * Derives type labels from the outgoing node type edges in a graph.
     * @param source the source graph to create the mappings for
     * @param result mapping from source graph nodes to type labels.
     * @throws FormatException on nonexistent, abstract or duplicate node types
     */
    private <N extends Node,L extends Label,E extends AbstractEdge<N,L>> void detectNodeTypes(
            Graph<N,E> source, Map<N,L> result) throws FormatException {
        Set<FormatError> errors = new TreeSet<FormatError>();
        for (N node : source.nodeSet()) {
            boolean isTyped =
                node instanceof ValueNode || node instanceof VariableNode
                    || node instanceof ProductNode;
            if (!isTyped) {
                // find a node type among the outgoing edges
                L image = null;
                for (E edge : source.outEdgeSet(node)) {
                    TypeLabel label = getActualType(edge.label());
                    if (label != null && label.isNodeType()) {
                        TypeNode type = getTypeNode(label);
                        if (type == null) {
                            errors.add(new FormatError(
                                "Unknown node type '%s'", label.text(), node));
                        } else if (image == null) {
                            image = edge.label();
                        } else {
                            errors.add(new FormatError(
                                "Duplicate node types '%s' and '%s'",
                                edge.label().text(), label.text(), node));
                        }
                    }
                }
                if (image != null) {
                    result.put(node, image);
                }
            }
        }
        if (!errors.isEmpty()) {
            throw new FormatException(errors);
        }
    }

    /** Tests if a given label is a node type that occurs in this type graph. */
    public boolean isNodeType(Label nodeLabel) {
        return this.typeMap.containsKey(getActualType(nodeLabel));
    }

    /** Tests if one type node is a subtype of another. */
    public boolean isSubtype(TypeNode subType, TypeNode superType) {
        return isSubtype(subType.getLabel(), superType.getLabel());
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
     * Returns the type edge for a given source and target node types and 
     * edge label, or {@code null} if the edge label does not occur for the
     * node type or any of its supertypes.
     */
    public TypeEdge getTypeEdge(TypeNode sourceType, TypeLabel label,
            TypeNode targetType) {
        TypeEdge result = null;
        Set<TypeLabel> sourceSupertypes =
            this.labelStore.getSupertypes(sourceType.getLabel());
        Set<TypeLabel> targetSupertypes =
            this.labelStore.getSupertypes(targetType.getLabel());
        if (sourceSupertypes != null && targetSupertypes != null) {
            for (TypeEdge edge : labelEdgeSet(label)) {
                if (sourceSupertypes.contains(edge.source().getLabel())
                    && targetSupertypes.contains(edge.target().getLabel())) {
                    // try to find a concrete type
                    if (result == null || result.isAbstract()) {
                        result = edge;
                        // if we've found a concrete type, we're done
                        if (!result.isAbstract()) {
                            break;
                        }
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
    public void addSubtype(TypeNode supertypeNode, TypeNode subtypeNode)
        throws FormatException {
        TypeLabel supertype = supertypeNode.getLabel();
        assert supertype != null : String.format(
            "Node '%s' does not have type label", supertypeNode);
        TypeLabel subtype = subtypeNode.getLabel();
        assert subtype != null : String.format(
            "Node '%s' does not have type label", subtypeNode);
        try {
            this.labelStore.addSubtype(supertype, subtype);
        } catch (IllegalArgumentException exc) {
            throw new FormatException(exc.getMessage());
        }
    }

    /** Returns the set of imported node types. */
    public Set<TypeNode> getImports() {
        return this.imports;
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
        Set<FormatError> errors = new TreeSet<FormatError>();
        // Set of edge labels occurring in the type graph
        Set<TypeLabel> edgeLabels = new HashSet<TypeLabel>();
        for (TypeEdge typeEdge : edgeSet()) {
            if (!typeEdge.label().isNodeType() && !typeEdge.isAbstract()) {
                TypeNode source = typeEdge.source();
                TypeLabel typeLabel = typeEdge.label();
                TypeLabel sourceType = source.getLabel();
                // check for outgoing edge types from data types
                if (sourceType.isDataType()) {
                    errors.add(new FormatError("Data type '%s' cannot have %s",
                        sourceType.text(), typeLabel.isFlag() ? "flags"
                                : "outgoing edges", source));
                }
                edgeLabels.add(typeEdge.label());
            }
        }
        for (TypeLabel edgeLabel : edgeLabels) {
            // non-abstract edge types must be distinguishable
            // either in source type or in target type
            // also for all subtypes
            List<TypeEdge> edges =
                new ArrayList<TypeEdge>(labelEdgeSet(edgeLabel));
            for (int i = 0; i < edges.size() - 1; i++) {
                TypeEdge edge1 = edges.get(i);
                // abstract edge types are OK
                if (edge1.isAbstract()) {
                    continue;
                }
                for (int j = i + 1; j < edges.size(); j++) {
                    // abstract edge types are OK
                    TypeEdge edge2 = edges.get(j);
                    if (edge2.isAbstract()) {
                        continue;
                    }
                    if (hasCommonSubtype(edge1.source(), edge2.source())
                        && hasCommonSubtype(edge1.target(), edge2.target())) {
                        errors.add(new FormatError(
                            "Possible type confusion of %s-%ss",
                            edgeLabel.text(), edgeLabel.isFlag() ? "flag"
                                    : "edge", edge1.source(), edge1,
                            edge2.source(), edge2));
                    }
                }
            }
        }
        if (!errors.isEmpty()) {
            throw new FormatException(errors);
        }
    }

    /** Tests if two nodes have a common subtype. */
    private boolean hasCommonSubtype(TypeNode node1, TypeNode node2) {
        TypeLabel label1 = node1.getLabel();
        TypeLabel label2 = node2.getLabel();
        // check for common subtypes
        Set<TypeLabel> sub1 =
            new HashSet<TypeLabel>(getLabelStore().getSubtypes(label1));
        return sub1.removeAll(getLabelStore().getSubtypes(label2));
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
        HostGraph result = new DefaultHostGraph(getName());
        Map<TypeLabel,HostNode> typeNodeMap = new HashMap<TypeLabel,HostNode>();
        /* Inverse subtyping-saturated set of flag-bearing node types. */
        Set<TypeLabel> flaggedNodes = new HashSet<TypeLabel>();
        /* Inverse subtype-saturated mapping from node types to all outgoing binary edge neighbours. */
        Map<TypeLabel,Set<TypeLabel>> connectMap =
            new HashMap<TypeLabel,Set<TypeLabel>>();
        for (TypeNode typeNode : nodeSet()) {
            HostNode nodeImage = result.addNode();
            TypeLabel typeLabel = typeNode.getLabel();
            typeNodeMap.put(typeLabel, nodeImage);
            connectMap.put(typeLabel, new HashSet<TypeLabel>());
        }
        for (TypeEdge typeEdge : edgeSet()) {
            TypeLabel edgeType = typeEdge.label();
            TypeLabel sourceType = typeEdge.source().getLabel();
            TypeLabel targetType = typeEdge.target().getLabel();
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
                    MatrixAutomaton.getDummyLabel(EdgeRole.BINARY), target);
            }
        }
        for (Label flaggedType : flaggedNodes) {
            HostNode source = typeNodeMap.get(flaggedType);
            result.addEdge(source,
                MatrixAutomaton.getDummyLabel(EdgeRole.FLAG), source);
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

    /** Label store permanently associated with this type graph. */
    private final LabelStore labelStore = new LabelStore();
    /** Mapping from node type labels to the corresponding type nodes. */
    private final Map<Label,TypeNode> typeMap = new HashMap<Label,TypeNode>();

    /** Set of imported nodes. */
    private final Set<TypeNode> imports = new HashSet<TypeNode>();
}
