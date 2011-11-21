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
import groove.algebra.Constant;
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
import groove.util.Duo;
import groove.view.FormatError;
import groove.view.FormatException;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
        this(name, false);
    }

    /** Constructs a fresh type graph. 
     * @param name the (non-{@code null}) name of the type graph 
     */
    public TypeGraph(String name, boolean implicit) {
        super(name);
        this.implicit = implicit;
        this.factory = new TypeFactory(this);
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
        testFixed(false);
        Map<TypeNode,TypeNode> otherToThis = new HashMap<TypeNode,TypeNode>();
        for (Node otherNode : other.nodeSet()) {
            TypeNode otherTypeNode = (TypeNode) otherNode;
            TypeNode image = addNode(otherTypeNode.label());
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
            image.setInMult(otherEdge.getInMult());
            image.setOutMult(otherEdge.getOutMult());
            image.setAbstract(otherEdge.isAbstract());
        }
        for (Duo<TypeNode> pair : other.inheritance) {
            this.inheritance.add(Duo.newDuo(otherToThis.get(pair.one()),
                otherToThis.get(pair.two())));
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
            TypeNode oldType = this.typeNodeMap.put(node.label(), node);
            assert oldType == null : String.format(
                "Duplicate type node for %s", oldType.label());
            if (node.isImported()) {
                this.imports.add(node);
            }
            this.labelStore.addLabel(node.label());
            if (node.getLabelPattern() != null) {
                this.labelStore.setPattern(node.label(), node.getLabelPattern());
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
        TypeNode result = this.typeNodeMap.get(label);
        if (result == null) {
            assert !this.predefinedNodes : "Mixed calls of TypeGraph.addNode(Node) and TypeGraph.addNode(Label)";
            result = getFactory().createNode(label);
            this.typeNodeMap.put(label, result);
            super.addNode(result);
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

    /** Adds the label as well as the edge. */
    @Override
    public TypeEdge addEdge(TypeNode source, Label label, TypeNode target) {
        TypeEdge result = super.addEdge(source, label, target);
        this.labelStore.addLabel(result.label());
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

    /**
     * Adds an inheritance pair to the type graph. The node type labels should
     * already be in the graph.
     * @throws FormatException if the supertype or subtype is not a node type,
     *         or if the new subtype relation creates a cycle.
     */
    public void addInheritance(TypeNode supertypeNode, TypeNode subtypeNode)
        throws FormatException {
        testFixed(false);
        this.inheritance.add(Duo.newDuo(subtypeNode, supertypeNode));
        TypeLabel supertype = supertypeNode.label();
        assert supertype != null : String.format(
            "Node '%s' does not have type label", supertypeNode);
        TypeLabel subtype = subtypeNode.label();
        assert subtype != null : String.format(
            "Node '%s' does not have type label", subtypeNode);
        try {
            this.labelStore.addSubtype(supertype, subtype);
        } catch (IllegalArgumentException exc) {
            throw new FormatException(exc.getMessage());
        }
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
                TypeLabel sourceType = source.label();
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

    @Override
    public void setFixed() {
        super.setFixed();
        this.labelStore.setFixed();
        // build node subtype and supertype maps
        this.nodeDirectSubtypeMap = new SubtypeMap(this);
        this.nodeDirectSupertypeMap = new SubtypeMap(this);
        this.nodeSubtypeMap = new SubtypeMap(this);
        this.nodeSupertypeMap = new SubtypeMap(this);
        for (TypeNode node : nodeSet()) {
            this.nodeSubtypeMap.get(node).add(node);
            this.nodeSupertypeMap.get(node).add(node);
        }
        // fill node subtype and supertype maps
        for (Duo<TypeNode> pair : this.inheritance) {
            TypeNode subtype = pair.one();
            TypeNode supertype = pair.two();
            this.nodeDirectSubtypeMap.get(supertype).add(subtype);
            this.nodeDirectSupertypeMap.get(subtype).add(supertype);
            Set<TypeNode> subsubtypes = this.nodeSubtypeMap.get(subtype);
            Set<TypeNode> supersupertypes =
                this.nodeSupertypeMap.get(supertype);
            for (TypeNode subsubtype : subsubtypes) {
                this.nodeSupertypeMap.get(subsubtype).addAll(supersupertypes);
            }
            for (TypeNode supersupertype : supersupertypes) {
                this.nodeSubtypeMap.get(supersupertype).addAll(subsubtypes);
            }
        }
        // build abstract edge subtype map
        for (TypeEdge edge : edgeSet()) {
            if (edge.isAbstract()) {
                Set<TypeEdge> subtypes = new HashSet<TypeEdge>();
                subtypes.add(edge);
                this.edgeSubtypeMap.put(edge, subtypes);
                Set<TypeNode> sourceSubnodes =
                    this.nodeSubtypeMap.get(edge.source());
                Set<TypeNode> targetSubnodes =
                    this.nodeSubtypeMap.get(edge.target());
                for (TypeEdge subEdge : labelEdgeSet(edge.label)) {
                    if (sourceSubnodes.contains(subEdge.source())
                        && targetSubnodes.contains(subEdge.target())) {
                        subtypes.add(subEdge);
                    }
                }
            }
        }
        // propagate colours and edge patterns to subtypes
        for (TypeNode node : nodeSet()) {
            // propagate colours
            Color nodeColour = node.getColor();
            if (nodeColour != null) {
                Set<TypeNode> propagatees = this.nodeSubtypeMap.get(node);
                while (!propagatees.isEmpty()) {
                    Iterator<TypeNode> subNodeIter = propagatees.iterator();
                    TypeNode subNode = subNodeIter.next();
                    subNodeIter.remove();
                    if (subNode.getColor() == null) {
                        subNode.setColor(nodeColour);
                    } else {
                        propagatees.removeAll(this.nodeSubtypeMap.get(subNode));
                    }
                }
            }
            // propagate label patterns
            LabelPattern nodePattern = node.getLabelPattern();
            if (nodePattern != null) {
                Set<TypeNode> propagatees = this.nodeSubtypeMap.get(node);
                while (!propagatees.isEmpty()) {
                    Iterator<TypeNode> subNodeIter = propagatees.iterator();
                    TypeNode subNode = subNodeIter.next();
                    subNodeIter.remove();
                    if (subNode.getLabelPattern() == null) {
                        subNode.setLabelPattern(nodePattern);
                    } else {
                        propagatees.removeAll(this.nodeSubtypeMap.get(subNode));
                    }
                }
            }
        }
    }

    @Override
    public TypeFactory getFactory() {
        return this.factory;
    }

    /** Returns the set of imported node types. */
    public Set<TypeNode> getImports() {
        return this.imports;
    }

    /**
     * Indicates if this is a degenerate type graph,
     * i.e. one without true node types  
     */
    public boolean isImplicit() {
        return this.implicit;
    }

    /** Tests if one type node is a subtype of another. */
    public boolean isSubtype(TypeNode subType, TypeNode superType) {
        testFixed(true);
        return getSubtypes(superType).contains(subType);
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
        testFixed(true);
        RuleFactory ruleFactory =
            parentTyping == null ? RuleFactory.newInstance(this)
                    : parentTyping.getFactory();
        RuleGraphMorphism morphism = new RuleGraphMorphism(ruleFactory);
        Set<FormatError> errors = new TreeSet<FormatError>();
        for (RuleNode node : source.nodeSet()) {
            try {
                RuleNode image;
                if (node instanceof ProductNode) {
                    image = node;
                } else if (node instanceof VariableNode) {
                    VariableNode varNode = (VariableNode) node;
                    Constant constant = varNode.getConstant();
                    if (constant == null) {
                        image =
                            ruleFactory.createVariableNode(varNode.getNumber(),
                                varNode.getSignature());
                    } else {
                        image =
                            ruleFactory.createVariableNode(varNode.getNumber(),
                                constant);
                    }
                } else if (isImplicit()) {
                    image = ruleFactory.createNode(node.getNumber());
                } else {
                    // get the type from the parent typing or from the node type edges
                    RuleLabel typingLabel = detectNodeType(source, node);
                    RuleNode parentImage =
                        parentTyping == null ? null
                                : parentTyping.getNode(node);
                    if (typingLabel == null) {
                        if (parentImage == null) {
                            throw new FormatException("Untyped node", node);
                        } else {
                            image = parentImage;
                        }
                    } else {
                        TypeNode type = getNode(typingLabel);
                        if (parentImage == null) {
                            image =
                                ruleFactory.createNode(node.getNumber(),
                                    type.label(), typingLabel.isSharp());
                        } else {
                            TypeNode parentType = parentImage.getType();
                            if (!isSubtype(type, parentType)) {
                                throw new FormatException(
                                    "Node type %s should specialise %s", type,
                                    parentType, node);
                            }
                            image = parentImage;
                        }
                    }
                }
                morphism.putNode(node, image);
            } catch (FormatException e) {
                errors.addAll(e.getErrors());
            }
        }
        for (RuleEdge edge : source.edgeSet()) {
            RuleLabel edgeLabel = edge.label();
            if (edgeLabel.isNodeType() && !isImplicit()) {
                // we already dealt with node types
                continue;
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
            if (edgeLabel.isAtom()) {
                typeEdge =
                    getTypeEdge(sourceType, edgeLabel.getTypeLabel(),
                        targetType);
                if (typeEdge == null) {
                    errors.add(new FormatError("%s-node has unknown %s-%s",
                        sourceType, edgeLabel, edge.getRole().getDescription(
                            false), edge.source()));
                } else {
                    morphism.putEdge(edge, ruleFactory.createEdge(sourceImage,
                        edgeLabel, targetImage));
                }
            } else {
                RuleLabel checkLabel =
                    edgeLabel.isNeg() ? edgeLabel.getNegOperand().toLabel()
                            : edgeLabel;
                if (checkLabel.isEmpty()) {
                    // this is a (possibly negative) comparison of nodes
                    // which can only be correct if they have a common subtype
                    if (!hasCommonSubtype(sourceType, targetType)) {
                        errors.add(new FormatError(
                            "Node types %s and %s have no common subtypes",
                            sourceType, targetType, sourceKey, targetKey));
                    }
                } else if (checkLabel.getMatchExpr() != null && !isImplicit()) {
                    // this is a regular expression, which is matched against
                    // the saturated type graph
                    Set<HostNode> startNodes =
                        Collections.<HostNode>singleton(getSaturationNodeMap().get(
                            sourceType.label()));
                    Set<HostNode> endNodes =
                        Collections.<HostNode>singleton(getSaturationNodeMap().get(
                            targetType.label()));
                    if (checkLabel.getAutomaton(this).getMatches(
                        getSaturation(), startNodes, endNodes).isEmpty()) {
                        errors.add(new FormatError(
                            "Regular expression '%s' not matched by path in type graph",
                            checkLabel, source, edge));
                    }
                }
                morphism.putEdge(edge,
                    ruleFactory.createEdge(sourceImage, edgeLabel, targetImage));
            }
        }
        if (!errors.isEmpty()) {
            throw new FormatException(errors);
        }
        return morphism;

    }

    // TODO
    // edge multiplicity check to be moved to a HostGraph method
    /**
     * Attempts to find a typing for a given host graph.
     * @param source the rule graph to be typed
     * @return a morphism from the rule graph to a typed version
     * @throws FormatException if the rule graph contains type errors
     */
    public HostGraphMorphism analyzeHost(HostGraph source)
        throws FormatException {
        testFixed(true);
        HostFactory hostFactory = HostFactory.newInstance(this);
        HostGraphMorphism morphism = new HostGraphMorphism(hostFactory);
        Set<FormatError> errors = new TreeSet<FormatError>();
        Map<HostNode,Map<TypeEdge,Integer>> inCounts =
            new HashMap<HostNode,Map<TypeEdge,Integer>>();
        Map<HostNode,Map<TypeEdge,Integer>> outCounts =
            new HashMap<HostNode,Map<TypeEdge,Integer>>();
        for (HostNode node : source.nodeSet()) {
            try {
                HostNode image;
                if (node instanceof ValueNode) {
                    ValueNode valueNode = (ValueNode) node;
                    image =
                        hostFactory.createValueNode(valueNode.getNumber(),
                            valueNode.getAlgebra(), valueNode.getValue());
                } else if (isImplicit()) {
                    image = hostFactory.createNode(node.getNumber());
                } else {
                    TypeLabel nodeType = detectNodeType(source, node);
                    if (nodeType == null) {
                        errors.add(new FormatError("Untyped node", node));
                        image = node;
                    } else {
                        TypeNode type = getNode(nodeType);
                        if (type.isAbstract()) {
                            errors.add(new FormatError(
                                "Abstract node type '%s'", type, node));
                        }
                        image =
                            hostFactory.createNode(node.getNumber(), nodeType);
                    }
                }
                morphism.putNode(node, image);
            } catch (FormatException e) {
                errors.addAll(e.getErrors());
            }
        }
        for (HostEdge edge : source.edgeSet()) {
            TypeLabel edgeType = edge.label();
            if (edgeType.isNodeType() && !isImplicit()) {
                // we already dealt with node types
                continue;
            }
            HostNode sourceImage = morphism.getNode(edge.source());
            HostNode targetImage = morphism.getNode(edge.target());
            if (sourceImage == null || targetImage == null) {
                // this must be due to an unknown node type
                // which was already reported as an error
                continue;
            }
            TypeNode sourceType = sourceImage.getType();
            TypeNode targetType = targetImage.getType();
            if (sourceType == null || targetType == null) {
                // this must be due to an untyped node
                // which was already reported as an error
                continue;
            }
            TypeEdge typeEdge = getTypeEdge(sourceType, edgeType, targetType);
            if (typeEdge == null) {
                errors.add(new FormatError("%s-node has unknown %s-%s",
                    sourceType, edgeType.text(),
                    edgeType.getRole().getDescription(false), edge.source()));
            } else if (typeEdge.isAbstract()) {
                errors.add(new FormatError("%s-node has abstract %s-%s",
                    sourceType, edgeType.text(),
                    edgeType.getRole().getDescription(false), edge.source()));
            } else {
                morphism.putEdge(edge,
                    hostFactory.createEdge(sourceImage, edgeType, targetImage));
                if (typeEdge.getOutMult() != null) {
                    countTypeEdge(outCounts, edge.source(), typeEdge);
                }
                if (typeEdge.getInMult() != null) {
                    countTypeEdge(inCounts, edge.target(), typeEdge);
                }
            }
        }
        verifyMultiplicity(source, inCounts, true, errors);
        verifyMultiplicity(source, outCounts, false, errors);
        if (!errors.isEmpty()) {
            throw new FormatException(errors);
        }
        return morphism;
    }

    /**
     * Count the occurrence of an edge type relative to a host node.
     */
    private void countTypeEdge(Map<HostNode,Map<TypeEdge,Integer>> counts,
            HostNode node, TypeEdge type) {
        Map<TypeEdge,Integer> nmap = counts.get(node);
        if (nmap == null) {
            nmap = new HashMap<TypeEdge,Integer>();
            nmap.put(type, 1);
            counts.put(node, nmap);
            return;
        }
        Integer oldCount = nmap.get(type);
        if (oldCount == null) {
            nmap.put(type, 1);
        } else {
            nmap.put(type, oldCount + 1);
        }
        counts.put(node, nmap);
    }

    /**
     * Verify counted multiplicity. The <code>inOrOut</code> argument indicates
     * whether the 'in' (value <code>true</code>) or 'out' (value <code>false
     * </code>) multiplicity must be verified. Problems are stored in the
     * argument set of {@link FormatError}s. 
     */
    private void verifyMultiplicity(HostGraph source,
            Map<HostNode,Map<TypeEdge,Integer>> counts, boolean inOrOut,
            Set<FormatError> errors) {
        for (Map.Entry<HostNode,Map<TypeEdge,Integer>> entry1 : counts.entrySet()) {
            for (Map.Entry<TypeEdge,Integer> entry2 : entry1.getValue().entrySet()) {
                Multiplicity mult =
                    inOrOut ? entry2.getKey().getInMult()
                            : entry2.getKey().getOutMult();
                int count = entry2.getValue();
                if (!mult.inRange(count)) {
                    String msg =
                        "the " + (inOrOut ? "in" : "out")
                            + " multiplicity of edge '"
                            + entry2.getKey().label()
                            + "' in node '%s' is out of range (got: " + count
                            + "; expected: " + mult.one() + ".."
                            + (mult.isUnbounded() ? "*" : mult.two()) + ")";
                    errors.add(new FormatError(msg, entry1.getKey(), source));
                }
            }
        }
    }

    /**
     * Derives a type label for a node from the outgoing node type edges in a graph.
     * @param source the source graph to create the mappings for
     * @param node the node for which to discover the type
     * @throws FormatException on nonexistent, abstract or duplicate node types
     */
    private <N extends Node,L extends Label,E extends AbstractEdge<N,L>> L detectNodeType(
            Graph<N,E> source, N node) throws FormatException {
        L result = null;
        // find a node type among the outgoing edges
        for (E edge : source.outEdgeSet(node)) {
            L label = edge.label();
            if (label.isNodeType()) {
                TypeNode type = getNode(label);
                if (type == null) {
                    throw new FormatException("Unknown node type '%s'",
                        label.text(), node);
                } else if (result == null) {
                    result = label;
                } else {
                    throw new FormatException(
                        "Duplicate node types '%s' and '%s'", label.text(),
                        result.text(), node);
                }
            }
        }
        return result;
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
            this.labelStore.getSupertypes(sourceType.label());
        Set<TypeLabel> targetSupertypes =
            this.labelStore.getSupertypes(targetType.label());
        if (sourceSupertypes != null && targetSupertypes != null) {
            for (TypeEdge edge : labelEdgeSet(label)) {
                if (sourceSupertypes.contains(edge.source().label())
                    && targetSupertypes.contains(edge.target().label())) {
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
        if (result == null && isImplicit()) {
            // this must be due to the fact that we are still editing the graph being analysed
            // return an edge that is not in the type graph
            result = getFactory().newEdge(sourceType, label, targetType);
        }
        return result;
    }

    /** 
     * Returns the type node corresponding to a given node type label,
     * if there is such a node in the type graph. Returns {@code null} 
     * if the label is not a known node type.
     */
    public TypeNode getNode(Label label) {
        assert label.isNodeType();
        if (isImplicit()) {
            return this.factory.getTopNode();
        } else {
            return this.typeNodeMap.get(getActualType(label));
        }
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

    /** Tests if two nodes have a common subtype. */
    private boolean hasCommonSubtype(TypeNode node1, TypeNode node2) {
        TypeLabel label1 = node1.label();
        TypeLabel label2 = node2.label();
        // check for common subtypes
        Set<TypeLabel> sub1 =
            new HashSet<TypeLabel>(getLabelStore().getSubtypes(label1));
        Set<TypeLabel> sub2 = getLabelStore().getSubtypes(label2);
        assert sub2 != null;
        return sub1.removeAll(sub2);
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
        HostGraph result =
            new DefaultHostGraph(getName(), HostFactory.newInstance(this));
        Map<TypeLabel,HostNode> typeNodeMap = new HashMap<TypeLabel,HostNode>();
        /* Inverse subtyping-saturated set of flag-bearing node types. */
        Set<TypeLabel> flaggedNodes = new HashSet<TypeLabel>();
        /* Inverse subtype-saturated mapping from node types to all outgoing binary edge neighbours. */
        Map<TypeLabel,Set<TypeLabel>> connectMap =
            new HashMap<TypeLabel,Set<TypeLabel>>();
        for (TypeNode typeNode : nodeSet()) {
            HostNode nodeImage = result.addNode();
            TypeLabel typeLabel = typeNode.label();
            typeNodeMap.put(typeLabel, nodeImage);
            connectMap.put(typeLabel, new HashSet<TypeLabel>());
        }
        for (TypeEdge typeEdge : edgeSet()) {
            TypeLabel edgeType = typeEdge.label();
            TypeLabel sourceType = typeEdge.source().label();
            TypeLabel targetType = typeEdge.target().label();
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

    /** Returns the set of all type labels occurring in the type graph. */
    public Set<TypeLabel> getLabels() {
        return getLabelStore().getLabels();
    }

    /** Returns the set of all type labels of a given kind occurring in the type graph. */
    public Set<TypeLabel> getLabels(EdgeRole role) {
        return getLabelStore().getLabels(role);
    }

    /** Returns the set of all sublabels of a given label. */
    public Set<TypeLabel> getSublabels(TypeLabel label) {
        return getLabelStore().getSubtypes(label);
    }

    /** Returns the mapping from node type labels to direct supertypes. */
    public Map<TypeNode,Set<TypeNode>> getDirectSupertypeMap() {
        return this.nodeDirectSupertypeMap;
    }

    /** Returns the mapping from node type labels to direct subtypes. */
    public Map<TypeNode,Set<TypeNode>> getDirectSubtypeMap() {
        return this.nodeDirectSubtypeMap;
    }

    /** Returns the set of subtypes of a given node type. */
    public Set<TypeNode> getSubtypes(TypeNode node) {
        testFixed(true);
        return this.nodeSubtypeMap.get(node);
    }

    /** Returns the set of subtypes of a given node type. */
    public Set<TypeNode> getSupertypes(TypeNode node) {
        testFixed(true);
        return this.nodeSupertypeMap.get(node);
    }

    /** Returns the set of subtypes of a given (abstract) edge type. */
    public Set<TypeEdge> getSubtypes(TypeEdge edge) {
        testFixed(true);
        return this.edgeSubtypeMap.get(edge);
    }

    /**
     * Returns the set of type nodes and edges in this type graph
     * that can be matched by a given rule label.
     */
    public Set<TypeElement> getMatches(RuleLabel label) {
        Set<TypeElement> result = new HashSet<TypeElement>();
        if (label.isInv()) {
            label = label.getInvLabel();
        }
        if (label.isWildcard()) {
            if (isImplicit() || !label.isNodeType()) {
                for (TypeEdge typeEdge : edgeSet()) {
                    if (typeEdge.getRole() == label.getWildcardKind()) {
                        result.add(typeEdge);
                    }
                }
            } else {
                result.addAll(nodeSet());
            }
        } else if (label.isSharp()) {
            if (isImplicit()) {
                result.addAll(labelEdgeSet(label.getTypeLabel()));
            } else {
                result.add(getNode(label));
            }
        } else {
            assert label.isAtom();
            if (isImplicit() || !label.isNodeType()) {
                result.addAll(labelEdgeSet(label.getTypeLabel()));
            } else {
                result.addAll(getSubtypes(getNode(label)));
            }
        }
        return result;
    }

    @Override
    protected boolean isTypeCorrect(Node node) {
        return node instanceof TypeNode;
    }

    @Override
    protected boolean isTypeCorrect(Edge edge) {
        return edge instanceof TypeEdge;
    }

    /** Type factory associated with this type graph. */
    private final TypeFactory factory;
    /** Label store permanently associated with this type graph. */
    private final LabelStore labelStore = new LabelStore();
    /** Mapping from node type labels to the corresponding type nodes. */
    private final Map<Label,TypeNode> typeNodeMap =
        new HashMap<Label,TypeNode>();

    /** Set of imported nodes. */
    private final Set<TypeNode> imports = new HashSet<TypeNode>();
    /**
     * Flag indicating that this is an implicit type graph.
     * This affects the type analysis: an implicit type graph cannot
     * give rise to typing errors.
     */
    private final boolean implicit;
    /**
     * List of inheritance pairs.
     */
    private final List<Duo<TypeNode>> inheritance =
        new ArrayList<Duo<TypeNode>>();
    /**
     * Mapping from node types to direct subtypes.
     * The inverse of {@link #nodeDirectSupertypeMap}.
     * Built at the moment of fixing the type graph.
     */
    private SubtypeMap nodeDirectSubtypeMap;
    /**
     * Mapping from node types to direct supertypes.
     * The inverse of {@link #nodeDirectSubtypeMap}.
     * Built at the moment of fixing the type graph.
     */
    private SubtypeMap nodeDirectSupertypeMap;
    /**
     * Reflexive and transitive mapping from node types to node subtypes.
     * The closure of {@link #nodeDirectSubtypeMap}, and the inverse of {@link #nodeSupertypeMap}.
     * Built at the moment of fixing the type graph.
     */
    private SubtypeMap nodeSubtypeMap;
    /**
     * Reflexive and transitive mapping from node types to node supertypes.
     * The closure of {@link #nodeDirectSupertypeMap}, and the inverse of {@link #nodeSubtypeMap}.
     * Built at the moment of fixing the type graph.
     */
    private SubtypeMap nodeSupertypeMap;
    /**
    * Mapping from abstract edge types to edge subtypes.
    * Built at the moment of fixing the type graph.
    */
    private final Map<TypeEdge,Set<TypeEdge>> edgeSubtypeMap =
        new HashMap<TypeEdge,Set<TypeEdge>>();

    /** Class holding a mapping from type nodes to sets of type nodes. */
    private static class SubtypeMap extends HashMap<TypeNode,Set<TypeNode>> {
        public SubtypeMap(TypeGraph typeGraph) {
            assert typeGraph.isFixed();
            for (TypeNode node : typeGraph.nodeSet()) {
                put(node, new HashSet<TypeNode>());
            }
        }

        /** Adds the content of another subtype map to this one,
         * modulo a mapping from the other's nodes to this one's.
         * @param other the other subtype map
         * @param map mapping from the nodes of other to the nodes of this
         */
        public void add(SubtypeMap other, Map<TypeNode,TypeNode> map) {
            for (Map.Entry<TypeNode,Set<TypeNode>> entry : other.entrySet()) {
                TypeNode myKey = map.get(entry.getKey());
                Set<TypeNode> myValue = get(myKey);
                if (myValue == null) {
                    put(myKey, myValue = new HashSet<TypeNode>());
                }
                for (TypeNode elem : entry.getValue()) {
                    myValue.add(map.get(elem));
                }
            }
        }
    }
}
