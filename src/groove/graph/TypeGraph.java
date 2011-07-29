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
import groove.graph.algebra.ArgumentEdge;
import groove.graph.algebra.OperatorEdge;
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
        RuleFactory ruleFactory = source.getFactory();
        RuleGraphMorphism morphism = new RuleGraphMorphism();
        Set<FormatError> errors = new TreeSet<FormatError>();
        // detect node types
        for (RuleEdge edge : source.edgeSet()) {
            RuleNode node = edge.source();
            TypeLabel label = getActualType(edge.label());
            if (label != null && label.isNodeType()) {
                TypeNode type = getTypeNode(label);
                if (type == null) {
                    errors.add(new FormatError("Unknown type %s for node '%s'",
                        label.text(), node));
                    // though we don't know the type, the node is not untyped
                    morphism.putNode(node, node);
                } else {
                    RuleNode parentImage =
                        parentTyping == null ? null
                                : parentTyping.getNode(node);
                    if (parentImage != null
                        && !isSubtype(type, parentImage.getType())) {
                        errors.add(new FormatError(
                            "Type %s of node '%s' should be subtype of %s",
                            label.text(), node,
                            parentImage.getType().getLabel().text()));
                    }
                    RuleNode nodeImage =
                        ruleFactory.createNode(node.getNumber(), type,
                            edge.label().isSharp());
                    RuleNode oldImage = morphism.putNode(node, nodeImage);
                    if (oldImage != null) {
                        errors.add(new FormatError(
                            "Duplicate types %s and %s on node '%s'",
                            oldImage.getType().getLabel().text(), label.text(),
                            node));
                    }
                }
            }
        }
        for (RuleNode node : source.nodeSet()) {
            if (!morphism.containsNodeKey(node)) {
                if (node instanceof VariableNode
                    && ((VariableNode) node).getSignature() != null) {
                    morphism.putNode(node, node);
                } else if (node instanceof ProductNode) {
                    morphism.putNode(node, node);
                } else {
                    RuleNode parentImage = parentTyping.getNode(node);
                    if (parentImage != null) {
                        morphism.putNode(node, parentImage);
                    } else {
                        errors.add(new FormatError("Untyped node '%s'", node));
                    }
                }
            }
        }
        for (RuleEdge edge : source.edgeSet()) {
            RuleLabel edgeType = edge.label();
            if (edgeType.isNodeType()) {
                // we already dealt with node types
                continue;
            }
            if (edge instanceof ArgumentEdge || edge instanceof OperatorEdge) {
                morphism.putEdge(edge, edge);
                // leave unchecked for now
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
            TypeLabel sourceType = sourceImage.getType().getLabel();
            TypeLabel targetType = targetImage.getType().getLabel();
            TypeEdge typeEdge = null;
            if (!edgeType.isAtom()) {
                if (edgeType.isEmpty()) {
                    // this is a (possibly negative) comparison of nodes
                    // which can only be correct if they have a common subtype
                    Set<TypeLabel> subtypes =
                        getLabelStore().getSubtypes(sourceType);
                    Set<TypeLabel> commonSubtypes =
                        new HashSet<TypeLabel>(subtypes);
                    commonSubtypes.retainAll(getLabelStore().getSubtypes(
                        targetType));
                    if (commonSubtypes.isEmpty()) {
                        errors.add(new FormatError(
                            "Node types %s and %s have no common subtypes",
                            sourceType.text(), targetType.text(), sourceKey,
                            targetKey));
                    }
                } else {
                    // this is a regular expression, which is matched against
                    // the saturated type graph
                    Set<HostNode> startNodes =
                        Collections.<HostNode>singleton(getSaturationNodeMap().get(
                            sourceType));
                    Set<HostNode> endNodes =
                        Collections.<HostNode>singleton(getSaturationNodeMap().get(
                            targetType));
                    if (edgeType.getAutomaton(this.labelStore).getMatches(
                        getSaturation(), startNodes, endNodes).isEmpty()) {
                        errors.add(new FormatError(
                            "Regular expression '%s' not matched by path in type graph",
                            edgeType, source, edge));
                    }
                }
                morphism.putEdge(edge,
                    ruleFactory.createEdge(sourceImage, edgeType, targetImage));
            } else if (edgeType.isFlag()) {
                typeEdge = getTypeEdge(sourceType, getActualType(edgeType));
                if (typeEdge == null) {
                    errors.add(new FormatError("%s-node has unknown flag '%s'",
                        sourceType.text(), edgeType, source));
                } else {
                    morphism.putEdge(edge, ruleFactory.createEdge(sourceImage,
                        typeEdge, targetImage));
                }
            } else if (edgeType.isBinary()) {
                typeEdge = getTypeEdge(sourceType, getActualType(edgeType));
                if (typeEdge == null) {
                    errors.add(new FormatError("%s-node has unknown edge '%s'",
                        sourceType.text(), edgeType, source, edge));
                } else {
                    // check the target of the type edge against the type of the edge target
                    TypeLabel declaredTargetType = typeEdge.target().getLabel();
                    if (declaredTargetType.isDataType()
                        || targetType.isDataType()) {
                        if (!targetType.equals(declaredTargetType)) {
                            errors.add(new FormatError(
                                "%s.%s-target should have type %s rather than %s",
                                sourceType.text(), edgeType,
                                declaredTargetType.text(), targetType.text(),
                                sourceKey, edge, targetKey));
                        }
                    } else if (!isSubtype(targetType, declaredTargetType)) {
                        errors.add(new FormatError(
                            "%s.%s-target should have %s-subtype rather than %s",
                            getActualType(sourceType).text(), edgeType,
                            declaredTargetType.text(), targetType.text(),
                            sourceKey, edge, targetKey));
                    }
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
        Set<FormatError> errors = new TreeSet<FormatError>();
        // detect node types
        for (HostEdge edge : source.edgeSet()) {
            HostNode node = edge.source();
            TypeLabel label = getActualType(edge.label());
            if (label != null && label.isNodeType()) {
                TypeNode type = getTypeNode(label);
                if (type == null) {
                    errors.add(new FormatError("Unknown type %s for node '%s'",
                        label.text(), node));
                    // though we don't know the type, the node is not untyped
                    morphism.putNode(node, node);
                } else {
                    HostNode nodeImage =
                        hostFactory.createNode(node.getNumber(), type);
                    HostNode oldImage = morphism.putNode(node, nodeImage);
                    if (oldImage != null) {
                        errors.add(new FormatError(
                            "Duplicate types %s and %s on node '%s'",
                            oldImage.getType().getLabel().text(), label.text(),
                            node));
                    }
                }
            }
        }
        for (HostNode node : source.nodeSet()) {
            if (!morphism.containsNodeKey(node)) {
                if (!(node instanceof ValueNode)) {
                    errors.add(new FormatError("Untyped node '%s'", node));
                }
                morphism.putNode(node, node);
            }
        }
        for (HostEdge edge : source.edgeSet()) {
            TypeLabel edgeType = edge.label();
            if (edgeType.isNodeType()) {
                // we already dealt with node types
                continue;
            }
            HostNode sourceKey = edge.source();
            HostNode targetKey = edge.target();
            HostNode sourceImage = morphism.getNode(sourceKey);
            HostNode targetImage = morphism.getNode(targetKey);
            if (sourceImage == null || targetImage == null) {
                // this must be due to an untyped node
                // which was already reported as an error
                continue;
            }
            TypeLabel sourceType = sourceImage.getType().getLabel();
            TypeLabel targetType = targetImage.getType().getLabel();
            TypeEdge typeEdge = null;
            if (edgeType.isFlag()) {
                typeEdge = getTypeEdge(sourceType, getActualType(edgeType));
                if (typeEdge == null) {
                    errors.add(new FormatError("%s-node has unknown flag '%s'",
                        sourceType.text(), edgeType, source));
                } else {
                    morphism.putEdge(edge, hostFactory.createEdge(sourceImage,
                        typeEdge, targetImage));
                }
            } else if (edgeType.isBinary()) {
                typeEdge = getTypeEdge(sourceType, getActualType(edgeType));
                if (typeEdge == null) {
                    errors.add(new FormatError("%s-node has unknown edge '%s'",
                        sourceType.text(), edgeType, source, edge));
                } else {
                    // check the target of the type edge against the type of the edge target
                    TypeLabel declaredTargetType = typeEdge.target().getLabel();
                    if (declaredTargetType.isDataType()
                        || targetType.isDataType()) {
                        if (!targetType.equals(declaredTargetType)) {
                            errors.add(new FormatError(
                                "%s.%s-target should have type %s rather than %s",
                                sourceType.text(), edgeType,
                                declaredTargetType.text(), targetType.text(),
                                sourceKey, edge, targetKey));
                        }
                    } else if (!isSubtype(targetType, declaredTargetType)) {
                        errors.add(new FormatError(
                            "%s.%s-target should have %s-subtype rather than %s",
                            getActualType(sourceType).text(), edgeType,
                            declaredTargetType.text(), targetType.text(),
                            sourceKey, edge, targetKey));
                    }
                    morphism.putEdge(edge, hostFactory.createEdge(sourceImage,
                        typeEdge, targetImage));
                }
            }
        }
        if (!errors.isEmpty()) {
            throw new FormatException(errors);
        }
        return morphism;

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

    /** Returns the set of imported node types. */
    public Set<TypeNode> getImports() {
        return this.imports;
    }

    /** Returns the unique node type label of a given node. */
    private TypeLabel getType(Node node) {
        return ((TypeNode) node).getLabel();
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
        HostGraph result = new DefaultHostGraph(getName());
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
