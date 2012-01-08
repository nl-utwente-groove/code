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
import groove.algebra.SignatureKind;
import groove.graph.algebra.OperatorNode;
import groove.graph.algebra.ValueNode;
import groove.graph.algebra.VariableNode;
import groove.rel.LabelVar;
import groove.rel.RegExpr;
import groove.rel.RegExprTyper;
import groove.rel.RegExprTyper.Result;
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
import groove.util.Groove;
import groove.view.FormatError;
import groove.view.FormatException;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
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
        // always add the basic data types
        addNodeSet(this.factory.getDataTypes());
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
    public Map<TypeNode,TypeNode> add(TypeGraph other) throws FormatException {
        testFixed(false);
        Set<TypeNode> newNodes = new HashSet<TypeNode>();
        Set<TypeEdge> newEdges = new HashSet<TypeEdge>();
        Map<TypeNode,TypeNode> otherToThis = new HashMap<TypeNode,TypeNode>();
        for (Node otherNode : other.nodeSet()) {
            TypeNode otherTypeNode = (TypeNode) otherNode;
            TypeNode image = addNode(otherTypeNode.label());
            image.setAbstract(otherTypeNode.isAbstract());
            if (otherTypeNode.getColor() != null) {
                image.setColor(otherTypeNode.getColor());
            }
            image.setLabelPattern(otherTypeNode.getLabelPattern());
            boolean imported = image.isImported() && otherTypeNode.isImported();
            image.setImported(imported);
            if (imported) {
                this.imports.add(image);
            } else {
                this.imports.remove(image);
            }
            if (!otherTypeNode.isImported()) {
                newNodes.add(image);
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
            newEdges.add(image);
        }
        for (Map.Entry<TypeNode,Set<TypeNode>> entry : other.nodeDirectSupertypeMap.entrySet()) {
            for (TypeNode supertype : entry.getValue()) {
                addInheritance(otherToThis.get(entry.getKey()),
                    otherToThis.get(supertype));
            }
        }
        this.componentMap.put(other.getName(), new Sub(other.getName(),
            newNodes, newEdges));
        return otherToThis;
    }

    @Override
    public boolean addNode(TypeNode node) {
        boolean result = super.addNode(node);
        if (result) {
            TypeNode oldType = this.typeNodeMap.put(node.label(), node);
            assert oldType == null : String.format(
                "Duplicate type node for %s", oldType.label());
            if (node.isImported()) {
                this.imports.add(node);
            }
            this.nodeDirectSubtypeMap.add(node);
            this.nodeDirectSupertypeMap.add(node);
            this.nodeSubtypeMap.add(node);
            this.nodeSupertypeMap.add(node);
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
            result = getFactory().createNode(label);
            addNode(result);
        }
        return result;
    }

    @Override
    public TypeGraph clone() {
        TypeGraph result = new TypeGraph(getName());
        try {
            result.add(this);
        } catch (FormatException e) {
            assert false;
        }
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
    public void addInheritance(TypeNode subtype, TypeNode supertype)
        throws FormatException {
        testFixed(false);
        if (supertype.label().isDataType()) {
            throw new FormatException("Data type '%s' cannot be supertype",
                supertype);
        }
        if (subtype.label().isDataType()) {
            throw new FormatException("Data type '%s' cannot be subtype",
                subtype);
        }
        if (this.nodeSupertypeMap.get(supertype).contains(subtype)) {
            throw new FormatException(String.format(
                "The relation '%s -> %s' introduces a cyclic type dependency",
                subtype, supertype));
        }
        this.nodeDirectSubtypeMap.get(supertype).add(subtype);
        this.nodeDirectSupertypeMap.get(subtype).add(supertype);
        Set<TypeNode> subsubtypes = this.nodeSubtypeMap.get(subtype);
        Set<TypeNode> supersupertypes = this.nodeSupertypeMap.get(supertype);
        for (TypeNode subsubtype : subsubtypes) {
            this.nodeSupertypeMap.get(subsubtype).addAll(supersupertypes);
        }
        for (TypeNode supersupertype : supersupertypes) {
            this.nodeSubtypeMap.get(supersupertype).addAll(subsubtypes);
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
                                    : "edge", edge1, edge2));
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
        // build edge subtype map and fill them reflexively
        for (TypeEdge edge : edgeSet()) {
            Set<TypeEdge> subtypes = new HashSet<TypeEdge>();
            subtypes.add(edge);
            this.edgeSubtypeMap.put(edge, subtypes);
            Set<TypeEdge> supertypes = new HashSet<TypeEdge>();
            supertypes.add(edge);
            this.edgeSupertypeMap.put(edge, supertypes);
        }
        // add the relations from abstract edge types to subtypes and back
        for (TypeEdge edge : edgeSet()) {
            Set<TypeEdge> subtypes = this.edgeSubtypeMap.get(edge);
            if (edge.isAbstract()) {
                Set<TypeNode> sourceSubnodes =
                    this.nodeSubtypeMap.get(edge.source());
                Set<TypeNode> targetSubnodes =
                    this.nodeSubtypeMap.get(edge.target());
                for (TypeEdge subEdge : labelEdgeSet(edge.label)) {
                    if (sourceSubnodes.contains(subEdge.source())
                        && targetSubnodes.contains(subEdge.target())) {
                        subtypes.add(subEdge);
                        this.edgeSupertypeMap.get(subEdge).add(edge);
                    }
                }
            }
        }
        // propagate colours and edge patterns to subtypes
        for (TypeNode node : nodeSet()) {
            // propagate colours
            Color nodeColour = node.getColor();
            if (nodeColour != null) {
                Set<TypeNode> propagatees =
                    new HashSet<TypeNode>(this.nodeSubtypeMap.get(node));
                propagatees.remove(node);
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
                Set<TypeNode> propagatees =
                    new HashSet<TypeNode>(this.nodeSubtypeMap.get(node));
                propagatees.remove(node);
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

    /** Indicates if a given label kind is used to determine node types. */
    public boolean isNodeType(EdgeRole role) {
        return role == EdgeRole.NODE_TYPE && !isImplicit();
    }

    /** Indicates if a given label is used to determine node types. */
    public boolean isNodeType(Label label) {
        return isNodeType(label.getRole());
    }

    /** Indicates if a given label is used to determine node types. */
    public boolean isNodeType(Edge edge) {
        return isNodeType(edge.getRole());
    }

    /** Tests if one type node is a subtype of another. */
    public boolean isSubtype(TypeNode subType, TypeNode superType) {
        testFixed(true);
        return getSubtypes(superType).contains(subType);
    }

    /** Tests if one edge type is a subtype of another. */
    public boolean isSubtype(TypeEdge subtype, TypeEdge supertype) {
        testFixed(true);
        return getSubtypes(supertype).contains(subtype);
    }

    /**
     * Attempts to find a typing for a given rule graph.
     * @param source the rule graph to be typed
     * @param parentTyping typing on the next higher nesting level; non-{@code null}
     * @return a morphism from the rule graph to a typed version
     * @throws FormatException if the rule graph contains type errors
     */
    public RuleGraphMorphism analyzeRule(RuleGraph source,
            RuleGraphMorphism parentTyping) throws FormatException {
        testFixed(true);
        RuleFactory ruleFactory = parentTyping.getFactory();
        RuleGraphMorphism result = new RuleGraphMorphism(ruleFactory);
        Set<FormatError> errors = new TreeSet<FormatError>();
        // extract the variable types from the parent typing
        result.copyVarTyping(parentTyping);
        // extract additional node variable typing from the source edges
        for (RuleEdge edge : source.edgeSet()) {
            if (isNodeType(edge) && edge.label().isWildcard()) {
                TypeGuard guard = edge.label().getWildcardGuard();
                LabelVar var = guard.getVar();
                Set<? extends TypeElement> types = result.getVarTypes(var);
                if (types == null) {
                    types = new HashSet<TypeElement>(nodeSet());
                }
                guard.filter(types);
                result.addVarTypes(var, types);
            }
        }
        // determine the node types
        List<OperatorNode> opNodes = new ArrayList<OperatorNode>();
        for (RuleNode node : source.nodeSet()) {
            try {
                RuleNode image;
                if (node instanceof OperatorNode) {
                    opNodes.add((OperatorNode) node);
                    continue;
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
                    // check if the type graph actually has the primitive type
                    if (!nodeSet().contains(image.getType())) {
                        throw new FormatException(
                            "Data type %s not used in type graph",
                            image.getType(), node);
                    }
                } else if (isImplicit()) {
                    image = ruleFactory.createNode(node.getNumber());
                } else {
                    // get the type from the parent typing or from the node type edges
                    RuleNode parentImage = parentTyping.getNode(node);
                    image =
                        createRuleNode(parentTyping, source, node,
                            result.getVarTyping());
                    if (parentImage != null) {
                        TypeNode parentType = parentImage.getType();
                        if (!isSubtype(image.getType(), parentType)) {
                            throw new FormatException(
                                "Node type %s should specialise %s",
                                image.getType(), parentType, node);
                        }
                    }
                }
                result.putNode(node, image);
            } catch (FormatException e) {
                errors.addAll(e.getErrors());
            }
        }
        // create images for the operator nodes
        for (OperatorNode opNode : opNodes) {
            boolean imageOk = true;
            List<VariableNode> newArgs = new ArrayList<VariableNode>();
            for (VariableNode arg : opNode.getArguments()) {
                VariableNode argImage = (VariableNode) result.getNode(arg);
                if (argImage == null) {
                    imageOk = false;
                    break;
                }
                newArgs.add(argImage);
            }
            VariableNode newTarget =
                (VariableNode) result.getNode(opNode.getTarget());
            imageOk &= newTarget != null;
            if (imageOk) {
                OperatorNode image =
                    ruleFactory.createOperatorNode(opNode.getNumber(),
                        opNode.getOperator(), newArgs, newTarget);
                result.putNode(opNode, image);
            }
        }
        // separate the edges
        // label variable edges
        Set<RuleEdge> varEdges = new HashSet<RuleEdge>();
        // other regular expression edges
        Set<RuleEdge> regExprEdges = new HashSet<RuleEdge>();
        // other typable edges (except already processed node types)
        Set<RuleEdge> simpleEdges = new HashSet<RuleEdge>();
        for (RuleEdge edge : source.edgeSet()) {
            // only consider edges for which source and target are typed
            // which may fail to hold due to a previous error
            if (result.containsNodeKey(edge.source())
                && result.containsNodeKey(edge.target()) && !isNodeType(edge)) {
                RuleLabel edgeLabel = edge.label();
                if (edgeLabel.isAtom() || edgeLabel.isSharp()) {
                    simpleEdges.add(edge);
                } else if (edgeLabel.isWildcard()) {
                    varEdges.add(edge);
                } else {
                    regExprEdges.add(edge);
                }
            }
        }
        // process the wildcard edges
        for (RuleEdge varEdge : varEdges) {
            RuleEdge image =
                ruleFactory.createEdge(result.getNode(varEdge.source()),
                    varEdge.label(), result.getNode(varEdge.target()));
            Set<? extends TypeElement> matchingTypes = image.getMatchingTypes();
            for (TypeGuard guard : image.getTypeGuards()) {
                matchingTypes.retainAll(result.addVarTypes(guard.getVar(),
                    matchingTypes));
            }
            if (image.getMatchingTypes().isEmpty()) {
                errors.add(new FormatError("Inconsistent %s type %s",
                    image.label().getRole().getDescription(false),
                    image.label(), varEdge));
            }
            result.putEdge(varEdge, image);
        }
        // do the non-regular expression edges
        for (RuleEdge edge : simpleEdges) {
            RuleLabel edgeLabel = edge.label();
            RuleNode sourceImage = result.getNode(edge.source());
            RuleNode targetImage = result.getNode(edge.target());
            TypeEdge typeEdge =
                getTypeEdge(sourceImage.getType(), edgeLabel.getTypeLabel(),
                    targetImage.getType(), false);
            if (typeEdge == null) {
                errors.add(new FormatError("%s-node has unknown %s-%s",
                    sourceImage.getType(), edgeLabel,
                    edge.getRole().getDescription(false), edge));
            } else {
                result.putEdge(edge,
                    ruleFactory.createEdge(sourceImage, edgeLabel, targetImage));
            }
        }
        RegExprTyper regExprTyper =
            new RegExprTyper(this, result.getVarTyping());
        for (RuleEdge edge : regExprEdges) {
            RuleLabel edgeLabel = edge.label();
            RuleNode sourceImage = result.getNode(edge.source());
            RuleNode targetImage = result.getNode(edge.target());
            RuleLabel checkLabel =
                edgeLabel.isNeg() ? edgeLabel.getNegOperand().toLabel()
                        : edgeLabel;
            RegExpr expr = checkLabel.getMatchExpr();
            Result typeResult = expr.apply(regExprTyper);
            if (typeResult.hasErrors()) {
                for (FormatError error : typeResult.getErrors()) {
                    errors.add(new FormatError(error, edge));
                }
            } else {
                // check if source and target type fit
                boolean fit = false;
                Set<TypeNode> targetTypes =
                    new HashSet<TypeNode>(getMatchingTypes(targetImage));
                for (TypeNode sourceType : getMatchingTypes(sourceImage)) {
                    Set<TypeNode> resultTargetTypes =
                        typeResult.getAll(sourceType);
                    if (resultTargetTypes != null
                        && targetTypes.removeAll(resultTargetTypes)) {
                        fit = true;
                        break;
                    }
                }
                if (!fit) {
                    errors.add(new FormatError(
                        "No %s-path exists between %s and %s", checkLabel,
                        sourceImage.getType(), targetImage.getType(), edge));
                }
            }
            result.putEdge(edge,
                ruleFactory.createEdge(sourceImage, edgeLabel, targetImage));
        }
        if (!errors.isEmpty()) {
            throw new FormatException(errors);
        }
        return result;

    }

    /** 
     * Creates a rule node with a type to be determined by a list
     * of typing edges.
     * @param varTyping predetermined typing of the variable edges
     * @param graph the source graph
     * @param node the node for which we want an image
     * @return a non-{@code null} node with number {@code nr} and type
     * determined by {@code typingEdges}
     * @throws FormatException if no unambiguous node type can be derived 
     */
    private RuleNode createRuleNode(RuleGraphMorphism parentTyping,
            RuleGraph graph, RuleNode node,
            Map<LabelVar,Set<? extends TypeElement>> varTyping)
        throws FormatException {
        List<TypeGuard> typeGuards = new ArrayList<TypeGuard>();
        Set<LabelVar> labelVars = new HashSet<LabelVar>();
        RuleNode parentImage = parentTyping.getNode(node);
        Set<TypeNode> validTypes =
            new HashSet<TypeNode>(parentImage == null ? nodeSet()
                    : parentImage.getMatchingTypes());
        validTypes.removeAll(this.factory.getDataTypes());
        TypeNode type = null;
        boolean sharp = false;
        for (RuleEdge edge : graph.outEdgeSet(node)) {
            if (!edge.label().isNodeType()) {
                continue;
            }
            TypeGuard guard = edge.label().getWildcardGuard();
            if (guard != null) {
                typeGuards.add(guard);
                labelVars.add(guard.getVar());
            } else {
                TypeLabel typeLabel = edge.label().getTypeLabel();
                assert typeLabel != null;
                if (type == null) {
                    type = getNode(typeLabel);
                    sharp = edge.label().isSharp();
                    if (type == null) {
                        throw new FormatException("Unknown node type %s",
                            typeLabel, edge);
                    }
                } else {
                    throw new FormatException("Duplicate node types %s and %s",
                        type.label(), typeLabel, node);
                }
            }
        }
        // apply the known type guards for the label variables on this node
        for (LabelVar var : labelVars) {
            validTypes.retainAll(varTyping.get(var));
        }
        // push the constraints back to the variables
        for (LabelVar var : labelVars) {
            varTyping.get(var).retainAll(validTypes);
        }
        if (validTypes.isEmpty()) {
            String constraints =
                Groove.toString(typeGuards.toArray(), "", "", ", ", " and ");
            throw new FormatException("Inconsistent type constraint%s %s",
                typeGuards.size() == 1 ? "" : "s", constraints, node);
        }
        if (type == null) {
            if (parentImage == null && typeGuards.isEmpty()) {
                throw new FormatException("Untyped node", node);
            }
            // find a maximal element w.r.t. subtyping
            type = getLub(validTypes);
            if (type == null) {
                throw new FormatException(
                    "Ambiguous typing: %s do not have least common supertype",
                    validTypes, node);
            }
        } else {
            validTypes.retainAll(type.getSubtypes());
            // again push the constraints back to the variables
            for (LabelVar var : labelVars) {
                varTyping.get(var).retainAll(validTypes);
            }
            if (validTypes.isEmpty()) {
                String constraints =
                    Groove.toString(typeGuards.toArray(), "", "", ", ", " and ");
                throw new FormatException(
                    "Node type %s conflicts with type constraint%s %s", type,
                    typeGuards.size() == 1 ? "" : "s", constraints, node);
            }
        }
        RuleNode result =
            parentTyping.getFactory().createNode(node.getNumber(),
                type.label(), sharp, typeGuards);
        result.getMatchingTypes().retainAll(validTypes);
        return result;
    }

    /** 
     * Returns a fresh set of all type nodes that may be matched
     * by a given (typed) rule node.
     * These are either only the rule node type itself, or all subtypes,
     * depending on whether the rule node is sharp or not.
     */
    private Set<TypeNode> getMatchingTypes(RuleNode node) {
        Set<TypeNode> result = new HashSet<TypeNode>();
        if (node.isSharp()) {
            result.add(node.getType());
        } else {
            result.addAll(getSubtypes(node.getType()));
        }
        return result;
    }

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
                    List<HostEdge> nodeTypeEdges = detectNodeType(source, node);
                    if (nodeTypeEdges.isEmpty()) {
                        errors.add(new FormatError("Untyped node", node));
                        image = node;
                    } else if (nodeTypeEdges.size() > 1) {
                        errors.add(new FormatError(
                            "Multiple node types %s, %s", nodeTypeEdges.get(0),
                            nodeTypeEdges.get(1), node));
                        image = node;
                    } else {
                        HostEdge nodeTypeEdge = nodeTypeEdges.get(0);
                        TypeLabel nodeType = nodeTypeEdge.label();
                        TypeNode type = getNode(nodeType);
                        if (type.isAbstract()) {
                            errors.add(new FormatError(
                                "Abstract node type '%s'", type, nodeTypeEdge));
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
            if (isNodeType(edge)) {
                // we already dealt with node types
                continue;
            }
            TypeLabel edgeType = edge.label();
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
            TypeEdge typeEdge =
                getTypeEdge(sourceType, edgeType, targetType, false);
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
            }
        }
        if (!errors.isEmpty()) {
            throw new FormatException(errors);
        }
        return morphism;
    }

    /**
     * Derives a type label for a node from the outgoing node type edges in a graph.
     * @param source the source graph to create the mappings for
     * @param node the node for which to discover the type
     * @throws FormatException on nonexistent, abstract or duplicate node types
     */
    private <N extends Node,L extends Label,E extends AbstractEdge<N,L>> List<E> detectNodeType(
            Graph<N,E> source, N node) throws FormatException {
        List<E> result = new ArrayList<E>();
        // find a node type among the outgoing edges
        for (E edge : source.outEdgeSet(node)) {
            L label = edge.label();
            if (label.isNodeType()) {
                result.add(edge);
            }
        }
        return result;
    }

    /**
     * Returns the set of type edges for a given source and target node type,
     * or one of their supertypes.
     * @param sourceType the minimal source type for the required edges; if {@code null}, the
     * source type is unconstrained
     * @param targetType the minimal target type for the required edges; if {@code null}, the
     * target type is unconstrained
     * @param role the role of the edges to be looked up
     */
    private Set<TypeEdge> getTypeEdges(TypeNode sourceType,
            TypeNode targetType, EdgeRole role) {
        Set<TypeEdge> result = new HashSet<TypeEdge>();
        boolean sourceFixed = sourceType != null;
        boolean targetFixed = targetType != null;
        Set<TypeNode> derivedSourceTypes =
            sourceFixed ? getSupertypes(sourceType) : null;
        Set<TypeNode> derivedTargetTypes =
            targetFixed ? getSupertypes(targetType) : null;
        if (sourceFixed) {
            if (derivedSourceTypes != null
                && (!targetFixed || derivedTargetTypes != null)) {
                for (TypeNode derivedSourceType : derivedSourceTypes) {
                    for (TypeEdge edge : outEdgeSet(derivedSourceType)) {
                        if (!targetFixed
                            || derivedTargetTypes.contains(edge.target())) {
                            result.add(edge);
                        }
                    }
                }
            }
        } else {
            assert targetFixed;
            if (derivedTargetTypes != null) {
                for (TypeNode derivedTargetType : derivedTargetTypes) {
                    result.addAll(inEdgeSet(derivedTargetType));
                }
            }
        }
        return result;
    }

    /**
     * Returns the (most concrete) type edge for a given source and target node type
     * and edge label, or {@code null} if the edge label does not occur for the
     * node type or any of its supertypes.
     * @param precise if {@code true}, the source and target types must be observed
     * precisely; otherwise, supertypes are allowed
     */
    public TypeEdge getTypeEdge(TypeNode sourceType, TypeLabel label,
            TypeNode targetType, boolean precise) {
        TypeEdge result = null;
        for (TypeEdge edge : getTypeEdges(sourceType, targetType,
            label.getRole())) {
            if (precise
                && !(edge.source().equals(sourceType) && edge.target().equals(
                    targetType))) {
                continue;
            }
            if (!edge.label().equals(label)) {
                continue;
            }
            // try to find a concrete type
            if (result == null || result.isAbstract()) {
                result = edge;
                // if we've found a concrete type, we're done
                if (!result.isAbstract()) {
                    break;
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
        // check for common subtypes
        Set<TypeNode> sub1 = getSubtypes(node1);
        assert sub1 != null : String.format(
            "Node type %s does not exist in type graph %s", node1, this);
        Set<TypeNode> sub2 = getSubtypes(node2);
        assert sub2 != null : String.format(
            "Node type %s does not exist in type graph %s", node2, this);
        return new HashSet<TypeNode>(sub1).removeAll(sub2);
    }

    /** Returns the set of all type labels occurring in the type graph. */
    public Set<TypeLabel> getLabels() {
        testFixed(true);
        if (this.labels == null) {
            this.labels = new HashSet<TypeLabel>();
            for (TypeNode node : nodeSet()) {
                this.labels.add(node.label());
            }
            for (TypeEdge edge : edgeSet()) {
                this.labels.add(edge.label());
            }
        }
        return this.labels;
    }

    /** Returns an unmodifiable view on the mapping from node type labels to direct supertypes. */
    public Map<TypeNode,Set<TypeNode>> getDirectSupertypeMap() {
        return Collections.unmodifiableMap(this.nodeDirectSupertypeMap);
    }

    /** Returns an unmodifiable view on the mapping from node type labels to direct subtypes. */
    public Map<TypeNode,Set<TypeNode>> getDirectSubtypeMap() {
        return Collections.unmodifiableMap(this.nodeDirectSubtypeMap);
    }

    /** Returns the set of subtypes of a given node type. */
    public Set<TypeNode> getSubtypes(TypeNode node) {
        return this.nodeSubtypeMap.get(node);
    }

    /** Returns the set of subtypes of a given edge type. */
    public Set<TypeEdge> getSubtypes(TypeEdge edge) {
        return this.edgeSubtypeMap.get(edge);
    }

    /** Returns the set of supertypes of a given node type. */
    public Set<TypeNode> getSupertypes(TypeNode node) {
        return this.nodeSupertypeMap.get(node);
    }

    /** Returns the set of supertypes of a given edge type. */
    public Set<TypeEdge> getSupertypes(TypeEdge edge) {
        return this.edgeSupertypeMap.get(edge);
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
            if (isNodeType(label)) {
                result.addAll(nodeSet());
            } else {
                result.addAll(edgeSet());
            }
            label.getWildcardGuard().filter(result);
        } else if (label.isSharp()) {
            if (isNodeType(label)) {
                result.add(getNode(label));
            } else {
                result.addAll(labelEdgeSet(label.getTypeLabel()));
            }
        } else {
            assert label.isAtom();
            if (isNodeType(label)) {
                result.addAll(getSubtypes(getNode(label)));
            } else {
                result.addAll(labelEdgeSet(label.getTypeLabel()));
            }
        }
        return result;
    }

    /** Returns the set of type elements with a given label. */
    public Set<? extends TypeElement> getTypes(TypeLabel label) {
        Set<TypeElement> result = new HashSet<TypeElement>();
        if (isNodeType(label)) {
            result.add(getNode(label));
        } else {
            result.addAll(labelEdgeSet(label));
        }
        return result;
    }

    /** Returns a minimal element with respect to subtyping, if this exists. */
    public TypeNode getMinimum(Collection<TypeNode> types) {
        TypeNode result = null;
        for (TypeNode typeNode : types) {
            if (typeNode.isDataType()) {
                continue;
            }
            if (result == null || isSubtype(typeNode, result)) {
                result = typeNode;
            }
        }
        if (result != null && !result.getSupertypes().containsAll(types)) {
            result = null;
        }
        return result;
    }

    /** Returns a least upper bound with respect to subtyping, if this exists. */
    public TypeNode getLub(Collection<TypeNode> types) {
        Set<TypeNode> ubs = new HashSet<TypeNode>(nodeSet());
        for (TypeNode typeNode : types) {
            if (typeNode.isDataType()) {
                continue;
            }
            ubs.retainAll(getSupertypes(typeNode));
        }
        return getMinimum(ubs);
    }

    /**
     * Returns the (possibly empty) mapping from component type graphs
     * to the elements defined therein.
     * The map is only nonempty if this is a composite type graph, filled
     * through calls of {@link #add(TypeGraph)}.
     */
    public SortedMap<String,Sub> getComponentMap() {
        return Collections.unmodifiableSortedMap(this.componentMap);
    }

    /** Indicates if this is a composite type graph,
     * filled through calls of {@link #add(TypeGraph)}.
     * @see #getComponentMap()
     */
    public boolean isComposite() {
        return !this.componentMap.isEmpty();
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
     * Mapping from node types to direct subtypes.
     * The inverse of {@link #nodeDirectSupertypeMap}.
     * Built at the moment of fixing the type graph.
     */
    private final NodeTypeMap nodeDirectSubtypeMap = new NodeTypeMap(false);
    /**
     * Mapping from node types to direct supertypes.
     * The inverse of {@link #nodeDirectSubtypeMap}.
     * Built at the moment of fixing the type graph.
     */
    private final NodeTypeMap nodeDirectSupertypeMap = new NodeTypeMap(false);
    /**
     * Reflexive and transitive mapping from node types to node subtypes.
     * The closure of {@link #nodeDirectSubtypeMap}, and the inverse of {@link #nodeSupertypeMap}.
     * Built at the moment of fixing the type graph.
     */
    private final NodeTypeMap nodeSubtypeMap = new NodeTypeMap(true);
    /**
     * Reflexive and transitive mapping from node types to node supertypes.
     * The closure of {@link #nodeDirectSupertypeMap}, and the inverse of {@link #nodeSubtypeMap}.
     * Built at the moment of fixing the type graph.
     */
    private final NodeTypeMap nodeSupertypeMap = new NodeTypeMap(true);
    /**
    * Mapping from abstract edge types to edge subtypes.
    * Built at the moment of fixing the type graph.
    */
    private final Map<TypeEdge,Set<TypeEdge>> edgeSubtypeMap =
        new HashMap<TypeEdge,Set<TypeEdge>>();
    /**
    * Mapping from edge types to abstract edge supertypes.
    * Built at the moment of fixing the type graph.
    */
    private final Map<TypeEdge,Set<TypeEdge>> edgeSupertypeMap =
        new HashMap<TypeEdge,Set<TypeEdge>>();

    /** Mapping from component type graph names to the type elements in this type graph. */
    private final SortedMap<String,Sub> componentMap =
        new TreeMap<String,Sub>();

    /** Set of all labels occurring in the type graph. */
    private Set<TypeLabel> labels;

    /** Creates an implicit type graph for a given set of labels. */
    public static TypeGraph createImplicitType(Set<TypeLabel> labels) {
        TypeGraph result = new TypeGraph("implicit type graph", true);
        TypeFactory factory = result.getFactory();
        TypeNode top = factory.getTopNode();
        result.addNode(top);
        for (SignatureKind sigKind : EnumSet.allOf(SignatureKind.class)) {
            result.addNode(factory.getDataType(sigKind));
        }
        for (TypeLabel label : labels) {
            if (label.isBinary()) {
                for (TypeNode target : result.nodeSet()) {
                    result.addEdge(top, label, target);
                }
            } else {
                result.addEdge(top, label, top);
            }
        }
        result.setFixed();
        return result;
    }

    /** Class holding a mapping from type nodes to sets of type nodes. */
    private static class NodeTypeMap extends HashMap<TypeNode,Set<TypeNode>> {
        /** Creates a new, possibly reflexive map. */
        public NodeTypeMap(boolean reflexive) {
            this.reflexive = reflexive;
        }

        /** 
         * Adds a node to the keys of the map, with an initially empty image.
         * If the map is reflexive, the node itself is added to the image.
         * @param node the node to be added.
         */
        public void add(TypeNode node) {
            Set<TypeNode> record = new HashSet<TypeNode>();
            Set<TypeNode> oldRecord = put(node, record);
            assert oldRecord == null;
            if (this.reflexive) {
                record.add(node);
            }
        }

        private final boolean reflexive;
    }

    /** Component type graph. */
    public static class Sub {
        /** Constructs a component type entry. */
        public Sub(String name, Set<TypeNode> nodes, Set<TypeEdge> edges) {
            super();
            this.name = name;
            this.nodes = nodes;
            this.edges = edges;
        }

        /** Returns the name of the component. */
        public String getName() {
            return this.name;
        }

        /** Returns the set of nodes of the component. */
        public Set<TypeNode> getNodes() {
            return this.nodes;
        }

        /** Returns the set of edges of the component. */
        public Set<TypeEdge> getEdges() {
            return this.edges;
        }

        private final String name;
        private final Set<TypeNode> nodes;
        private final Set<TypeEdge> edges;
    }
}
