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
package groove.grammar.type;

import static groove.graph.EdgeRole.FLAG;
import static groove.graph.EdgeRole.NODE_TYPE;
import static groove.graph.GraphRole.TYPE;
import groove.automaton.RegExpr;
import groove.automaton.RegExprTyper;
import groove.automaton.RegExprTyper.Result;
import groove.grammar.host.HostEdge;
import groove.grammar.host.HostFactory;
import groove.grammar.host.HostGraph;
import groove.grammar.host.HostGraphMorphism;
import groove.grammar.host.HostNode;
import groove.grammar.host.ValueNode;
import groove.grammar.model.FormatError;
import groove.grammar.model.FormatErrorSet;
import groove.grammar.model.FormatException;
import groove.grammar.rule.LabelVar;
import groove.grammar.rule.OperatorNode;
import groove.grammar.rule.RuleEdge;
import groove.grammar.rule.RuleFactory;
import groove.grammar.rule.RuleGraph;
import groove.grammar.rule.RuleGraphMorphism;
import groove.grammar.rule.RuleLabel;
import groove.grammar.rule.RuleNode;
import groove.grammar.rule.VariableNode;
import groove.graph.Edge;
import groove.graph.EdgeRole;
import groove.graph.GraphRole;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.NodeSetEdgeSetGraph;
import groove.util.Groove;

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
import java.util.SortedMap;
import java.util.TreeMap;

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
            image.setComposite(otherEdge.isComposite());
            newEdges.add(image);
        }
        for (Map.Entry<TypeNode,Set<TypeNode>> entry : other.nodeDirectSupertypeMap.entrySet()) {
            for (TypeNode supertype : entry.getValue()) {
                addInheritance(otherToThis.get(entry.getKey()), otherToThis.get(supertype));
            }
        }
        this.componentMap.put(other.getName(), new Sub(other.getName(), newNodes, newEdges));
        return otherToThis;
    }

    @Override
    public boolean addNode(TypeNode node) {
        boolean result = super.addNode(node);
        if (result) {
            TypeNode oldType = this.typeNodeMap.put(node.label(), node);
            assert oldType == null : String.format("Duplicate type node for %s", oldType.label());
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
     * @param label the label for the type node; must be an {@link EdgeRole#NODE_TYPE}
     * @return the created and added node type
     */
    public TypeNode addNode(TypeLabel label) {
        assert label.getRole() == EdgeRole.NODE_TYPE : String.format("Label %s is not a node type",
            label);
        TypeNode result = this.typeNodeMap.get(label);
        if (result == null) {
            // the following implicitly adds the node to the graph
            result = getFactory().createNode(label);
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
        throw new UnsupportedOperationException("Edge removal not allowed in type graphs");
    }

    @Override
    public boolean removeNode(TypeNode node) {
        throw new UnsupportedOperationException("Node removal not allowed in type graphs");
    }

    /**
     * Adds an inheritance pair to the type graph. The node type labels should
     * already be in the graph.
     * @throws FormatException if the supertype or subtype is not a node type,
     *         or if the new subtype relation creates a cycle.
     */
    public void addInheritance(TypeNode subtype, TypeNode supertype) throws FormatException {
        testFixed(false);
        if (supertype.label().isDataType()) {
            throw new FormatException("Data type '%s' cannot be supertype", supertype);
        }
        if (subtype.label().isDataType()) {
            throw new FormatException("Data type '%s' cannot be subtype", subtype);
        }
        if (this.nodeSupertypeMap.get(supertype).contains(subtype)) {
            throw new FormatException(String.format(
                "The relation '%s -> %s' introduces a cyclic type dependency", subtype, supertype));
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
        FormatErrorSet errors = new FormatErrorSet();
        // Set of edge labels occurring in the type graph
        Set<TypeLabel> edgeLabels = new HashSet<TypeLabel>();
        for (TypeEdge typeEdge : edgeSet()) {
            if (typeEdge.getRole() != NODE_TYPE && !typeEdge.isAbstract()) {
                TypeNode source = typeEdge.source();
                TypeLabel typeLabel = typeEdge.label();
                TypeLabel sourceType = source.label();
                // check for outgoing edge types from data types
                if (sourceType.isDataType()) {
                    errors.add("Data type '%s' cannot have %s", sourceType.text(),
                        typeLabel.getRole() == FLAG ? "flags" : "outgoing edges", source);
                }
                edgeLabels.add(typeEdge.label());
            }
        }
        for (TypeLabel edgeLabel : edgeLabels) {
            // non-abstract edge types must be distinguishable
            // either in source type or in target type
            // also for all subtypes
            List<TypeEdge> edges = new ArrayList<TypeEdge>(edgeSet(edgeLabel));
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
                        errors.add("Possible type confusion of %s-%ss", edgeLabel.text(),
                            edgeLabel.getRole() == FLAG ? "flag" : "edge", edge1, edge2);
                    }
                }
            }
        }
        errors.throwException();
    }

    @Override
    public boolean setFixed() {
        boolean result = super.setFixed();
        if (result) {
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
                    Set<TypeNode> sourceSubnodes = this.nodeSubtypeMap.get(edge.source());
                    Set<TypeNode> targetSubnodes = this.nodeSubtypeMap.get(edge.target());
                    for (TypeEdge subEdge : edgeSet(edge.label())) {
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
        return result;
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
    public boolean isSubtype(TypeNode subtype, TypeNode supertype) {
        if (subtype.equals(supertype)) {
            return true;
        }
        if (isImplicit()) {
            return false;
        }
        testFixed(true);
        Set<TypeNode> allSubtypes = getSubtypes(supertype);
        if (allSubtypes.size() == 1) {
            return false;
        }
        return getSubtypes(supertype).contains(subtype);
    }

    /** Tests if one edge type is a subtype of another. */
    public boolean isSubtype(TypeEdge subtype, TypeEdge supertype) {
        if (subtype.equals(supertype)) {
            return true;
        }
        if (isImplicit()) {
            return false;
        }
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
    public RuleGraphMorphism analyzeRule(RuleGraph source, RuleGraphMorphism parentTyping)
        throws FormatException {
        testFixed(true);
        RuleFactory ruleFactory = parentTyping.getFactory();
        RuleGraphMorphism result = new RuleGraphMorphism(ruleFactory);
        FormatErrorSet errors = new FormatErrorSet();
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
                    image = ruleFactory.createVariableNode(varNode.getNumber(), varNode.getTerm());
                    // check if the type graph actually has the primitive type
                    if (!nodeSet().contains(image.getType())) {
                        throw new FormatException("Data type %s not used in type graph",
                            image.getType(), node);
                    }
                } else if (isImplicit()) {
                    image = ruleFactory.createNode(node.getNumber());
                } else {
                    // get the type from the parent typing or from the node type edges
                    RuleNode parentImage = parentTyping.getNode(node);
                    image = createRuleNode(parentTyping, source, node, result.getVarTyping());
                    if (parentImage != null) {
                        TypeNode parentType = parentImage.getType();
                        if (!isSubtype(image.getType(), parentType)) {
                            throw new FormatException("Node type %s should specialise %s",
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
                    // since we should have already added all variable nodes
                    // presumably this means that the argument contains an error
                    imageOk = false;
                    break;
                }
                newArgs.add(argImage);
            }
            VariableNode newTarget = (VariableNode) result.getNode(opNode.getTarget());
            imageOk &= newTarget != null;
            if (imageOk) {
                RuleNode image =
                    ruleFactory.createOperatorNode(opNode.getNumber(), opNode.getOperator(),
                        newArgs, newTarget);
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
            if (result.nodeMap().containsKey(edge.source())
                && result.nodeMap().containsKey(edge.target()) && !isNodeType(edge)) {
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
                ruleFactory.createEdge(result.getNode(varEdge.source()), varEdge.label(),
                    result.getNode(varEdge.target()));
            Set<? extends TypeElement> matchingTypes = image.getMatchingTypes();
            for (TypeGuard guard : image.getTypeGuards()) {
                matchingTypes.retainAll(result.addVarTypes(guard.getVar(), matchingTypes));
            }
            if (image.getMatchingTypes().isEmpty()) {
                errors.add("Inconsistent %s type %s",
                    image.label().getRole().getDescription(false), image.label(), varEdge);
            }
            result.putEdge(varEdge, image);
        }
        // do the non-regular expression edges
        for (RuleEdge edge : simpleEdges) {
            RuleLabel edgeLabel = edge.label();
            RuleNode sourceImage = result.getNode(edge.source());
            RuleNode targetImage = result.getNode(edge.target());
            TypeNode sourceType = sourceImage.getType();
            TypeEdge typeEdge =
                getTypeEdge(sourceImage.getType(), edgeLabel.getTypeLabel(), targetImage.getType(),
                    false);
            if (typeEdge == null) {
                // if the source type is the top type, we must be in a 
                // graph editor where a new edge label has been used and
                // the graph has not yet been saved. This will be solved 
                // upon saving, and the error is confusing, so dont't 
                // throw it
                if (!sourceType.isTopType()) {
                    errors.add("%s-node has unknown %s-%s", sourceType, edgeLabel,
                        edge.getRole().getDescription(false), edge);
                }
            } else {
                result.putEdge(edge, ruleFactory.createEdge(sourceImage, edgeLabel, targetImage));
            }
        }
        RegExprTyper regExprTyper = new RegExprTyper(this, result.getVarTyping());
        for (RuleEdge edge : regExprEdges) {
            RuleLabel edgeLabel = edge.label();
            RuleNode sourceImage = result.getNode(edge.source());
            RuleNode targetImage = result.getNode(edge.target());
            RuleLabel checkLabel =
                edgeLabel.isNeg() ? edgeLabel.getNegOperand().toLabel() : edgeLabel;
            RegExpr expr = checkLabel.getMatchExpr();
            Result typeResult = expr.apply(regExprTyper);
            if (typeResult.hasErrors()) {
                // if the source type is the top type, we must be in a 
                // graph editor where a new edge label has been used and
                // the graph has not yet been saved. This will be solved 
                // upon saving, and the error is confusing, so dont't 
                // throw it
                if (!sourceImage.getType().isTopType()) {
                    for (FormatError error : typeResult.getErrors()) {
                        errors.add(new FormatError(error, edge));
                    }
                }
            } else {
                // check if source and target type fit
                boolean fit = false;
                Set<TypeNode> targetTypes = new HashSet<TypeNode>(getMatchingTypes(targetImage));
                for (TypeNode sourceType : getMatchingTypes(sourceImage)) {
                    Set<TypeNode> resultTargetTypes = typeResult.getAll(sourceType);
                    if (resultTargetTypes != null && targetTypes.removeAll(resultTargetTypes)) {
                        fit = true;
                        break;
                    }
                }
                if (!fit) {
                    errors.add("No %s-path can exist between %s and %s", checkLabel,
                        sourceImage.getType(), targetImage.getType(), edge);
                }
            }
            result.putEdge(edge, ruleFactory.createEdge(sourceImage, edgeLabel, targetImage));
        }
        errors.throwException();
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
    private RuleNode createRuleNode(RuleGraphMorphism parentTyping, RuleGraph graph, RuleNode node,
            Map<LabelVar,Set<? extends TypeElement>> varTyping) throws FormatException {
        List<TypeGuard> typeGuards = new ArrayList<TypeGuard>();
        Set<LabelVar> labelVars = new HashSet<LabelVar>();
        RuleNode parentImage = parentTyping.getNode(node);
        TypeNode type = null;
        boolean sharp = false;
        for (RuleEdge edge : graph.outEdgeSet(node)) {
            if (edge.label().getRole() != NODE_TYPE) {
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
                        throw new FormatException("Unknown node type %s", typeLabel, edge);
                    }
                } else {
                    throw new FormatException("Duplicate node types %s and %s", type.label(),
                        typeLabel, node);
                }
            }
        }
        // collect the matching non-data type nodes
        Set<TypeNode> validTypes = new HashSet<TypeNode>();
        for (TypeNode tn : parentImage == null ? nodeSet() : parentImage.getMatchingTypes()) {
            if (!tn.isDataType()) {
                validTypes.add(tn);
            }
        }
        // apply the known type guards for the label variables on this node
        boolean validTypesChanged = false;
        for (LabelVar var : labelVars) {
            validTypesChanged |= validTypes.retainAll(varTyping.get(var));
        }
        if (validTypesChanged) {
            // push the constraints back to the variables
            for (LabelVar var : labelVars) {
                varTyping.get(var).retainAll(validTypes);
            }
        }
        if (validTypes.isEmpty()) {
            String constraints = Groove.toString(typeGuards.toArray(), "", "", ", ", " and ");
            throw new FormatException("Inconsistent type constraint%s %s", typeGuards.size() == 1
                    ? "" : "s", constraints, node);
        }
        if (type == null) {
            if (parentImage == null && typeGuards.isEmpty()) {
                throw new FormatException("Untyped node", node);
            }
            // find a maximal element w.r.t. subtyping
            type = getMaximum(validTypes);
            if (type == null) {
                throw new FormatException(
                    "Ambiguous typing: %s does not contain a common supertype", validTypes, node);
            }
        } else if (validTypes.retainAll(type.getSubtypes())) {
            // again push the constraints back to the variables
            for (LabelVar var : labelVars) {
                varTyping.get(var).retainAll(validTypes);
            }
            if (validTypes.isEmpty()) {
                String constraints = Groove.toString(typeGuards.toArray(), "", "", ", ", " and ");
                throw new FormatException("Node type %s conflicts with type constraint%s %s", type,
                    typeGuards.size() == 1 ? "" : "s", constraints, node);
            }
        }
        RuleNode result =
            parentTyping.getFactory().nodes(type, sharp, typeGuards).createNode(node.getNumber());
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
    public HostGraphMorphism analyzeHost(HostGraph source) throws FormatException {
        testFixed(true);
        HostFactory hostFactory = HostFactory.newInstance(getFactory());
        HostGraphMorphism morphism = new HostGraphMorphism(hostFactory);
        FormatErrorSet errors = new FormatErrorSet();
        for (HostNode node : source.nodeSet()) {
            try {
                HostNode image;
                if (node instanceof ValueNode) {
                    ValueNode valueNode = (ValueNode) node;
                    image =
                        hostFactory.values(valueNode.getAlgebra(), valueNode.getValue()).createNode(
                            valueNode.getNumber());
                } else if (isImplicit()) {
                    image = hostFactory.createNode(node.getNumber());
                } else {
                    List<HostEdge> nodeTypeEdges = detectNodeType(source, node);
                    if (nodeTypeEdges.isEmpty()) {
                        errors.add("Untyped node", node);
                        image = node;
                    } else if (nodeTypeEdges.size() > 1) {
                        errors.add("Multiple node types %s, %s", nodeTypeEdges.get(0),
                            nodeTypeEdges.get(1), node);
                        image = node;
                    } else {
                        HostEdge nodeTypeEdge = nodeTypeEdges.get(0);
                        TypeLabel nodeType = nodeTypeEdge.label();
                        TypeNode type = getNode(nodeType);
                        if (type == null) {
                            throw new FormatException("Unknown node type '%s'", nodeType,
                                nodeTypeEdge);
                        }
                        if (type.isAbstract()) {
                            throw new FormatException("Abstract node type '%s'", type, nodeTypeEdge);
                        }
                        image = hostFactory.nodes(type).createNode(node.getNumber());
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
            TypeEdge typeEdge = getTypeEdge(sourceType, edgeType, targetType, false);
            if (typeEdge == null) {
                // if the source type is the top type, we must be in a 
                // graph editor where a new edge label has been used and
                // the graph has not yet been saved. This will be solved 
                // upon saving, and the error is confusing, so dont't 
                // throw it
                if (!sourceType.isTopType()) {
                    errors.add("%s-node has unknown %s-%s", sourceType, edgeType.text(),
                        edgeType.getRole().getDescription(false), edge.source());
                }
            } else if (typeEdge.isAbstract()) {
                errors.add("%s-node has abstract %s-%s", sourceType, edgeType.text(),
                    edgeType.getRole().getDescription(false), edge.source());
            } else {
                morphism.putEdge(edge, hostFactory.createEdge(sourceImage, edgeType, targetImage));
            }
        }
        errors.throwException();
        EdgeMultiplicityVerifier.verifyMultiplicities(source, this);
        return morphism;
    }

    /**
     * Derives a type label for a node from the outgoing node type edges in a graph.
     * @param source the source graph to create the mappings for
     * @param node the node for which to discover the type
     * @throws FormatException on nonexistent, abstract or duplicate node types
     */
    private List<HostEdge> detectNodeType(HostGraph source, HostNode node) throws FormatException {
        List<HostEdge> result = new ArrayList<HostEdge>();
        // find a node type among the outgoing edges
        for (HostEdge edge : source.outEdgeSet(node)) {
            if (edge.getRole() == NODE_TYPE) {
                result.add(edge);
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
    public TypeEdge getTypeEdge(TypeNode sourceType, TypeLabel label, TypeNode targetType,
            boolean precise) {
        TypeEdge result = null;
        if (isFixed()) {
            TypeEdgeMap edgeMap;
            if (precise) {
                edgeMap = this.exactEdgeMap;
                if (edgeMap == null) {
                    edgeMap = this.exactEdgeMap = computeEdgeMap(true);
                }
            } else {
                edgeMap = this.superEdgeMap;
                if (edgeMap == null) {
                    edgeMap = this.superEdgeMap = computeEdgeMap(false);
                }
            }
            result = edgeMap.get(sourceType, label, targetType);
        } else {
            result = findTypeEdge(sourceType, label, targetType, precise);
        }
        return result;
    }

    private TypeEdgeMap computeEdgeMap(boolean precise) {
        TypeEdgeMap result = new TypeEdgeMap();
        for (TypeEdge edge : edgeSet()) {
            if (precise) {
                result.put(edge.source(), edge.target(), edge);
            } else {
                for (TypeNode source : getSubtypes(edge.source())) {
                    for (TypeNode target : getSubtypes(edge.target())) {
                        TypeEdge image = result.get(source, edge.label(), target);
                        // override existing image if this edge is concrete
                        if (image == null || !edge.isAbstract()) {
                            result.put(source, target, edge);
                        }
                    }
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
    private TypeEdge findTypeEdge(TypeNode sourceType, TypeLabel label, TypeNode targetType,
            boolean precise) {
        TypeEdge result = null;
        for (TypeEdge edge : edgeSet(label)) {
            if (!isSubtype(sourceType, edge.source(), precise)) {
                continue;
            }
            if (!isSubtype(targetType, edge.target(), precise)) {
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
        return result;
    }

    /** Tests for either the subtype relation or type equality. */
    private boolean isSubtype(TypeNode subtype, TypeNode supertype, boolean precise) {
        return precise ? supertype.equals(subtype) : isSubtype(subtype, supertype);
    }

    /** 
     * Returns the type node with a node type label, given as a 
     * string. The string should be in prefix form (see {@link TypeLabel#createLabel(String)}).
     * @param label the label to look up.
     * @return the type node labelled with {@code label}, or {@code null}
     * if {@code label} does not correspond to a note type label.
     */
    public TypeNode getNode(String label) {
        return getNode(TypeLabel.createLabel(label));
    }

    /** 
     * Returns the type node with a given node type label,
     * if there is such a node in the type graph. Returns {@code null} 
     * if the label is not a known node type.
     */
    public TypeNode getNode(Label label) {
        assert label.getRole() == NODE_TYPE;
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
        if (node1.equals(node2)) {
            return true;
        }
        if (isImplicit()) {
            return false;
        }
        Set<TypeNode> sub1 = getSubtypes(node1);
        Set<TypeNode> sub2 = getSubtypes(node2);
        assert sub1 != null : String.format("Node type %s does not exist in type graph %s", node1,
            this);
        assert sub2 != null : String.format("Node type %s does not exist in type graph %s", node2,
            this);
        if (sub1.size() == 1) {
            // sub1 doesn't have a proper subtype
            return sub2.size() > 1 && sub2.contains(node1);
        } else if (sub2.size() == 1) {
            // sub2 doesn't have a proper subtype
            return sub1.contains(node2);
        }
        return !Collections.disjoint(sub1, sub2);
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
        Set<TypeNode> result;
        if (isImplicit()) {
            result = Collections.singleton(node);
        } else {
            assert isFixed();
            result = this.nodeSubtypeMap.get(node);
        }
        assert result != null;
        return result;
    }

    /** Returns the set of subtypes of a given edge type. */
    public Set<TypeEdge> getSubtypes(TypeEdge edge) {
        Set<TypeEdge> result;
        if (isImplicit()) {
            result = Collections.singleton(edge);
        } else {
            assert isFixed();
            result = this.edgeSubtypeMap.get(edge);
        }
        assert result != null;
        return result;
    }

    /** Returns the set of supertypes of a given node type. */
    public Set<TypeNode> getSupertypes(TypeNode node) {
        Set<TypeNode> result;
        if (isImplicit()) {
            result = Collections.singleton(node);
        } else {
            assert isFixed();
            result = this.nodeSupertypeMap.get(node);
        }
        assert result != null;
        return result;
    }

    /** Returns the set of supertypes of a given edge type. */
    public Set<TypeEdge> getSupertypes(TypeEdge edge) {
        Set<TypeEdge> result;
        if (isImplicit()) {
            result = Collections.singleton(edge);
        } else {
            assert isFixed();
            result = this.edgeSupertypeMap.get(edge);
        }
        assert result != null;
        return result;
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
            if (isNodeType(label) && !isImplicit()) {
                result.addAll(nodeSet());
                result.removeAll(getFactory().getDataTypes());
            } else {
                for (TypeEdge te : edgeSet()) {
                    if (te.getRole() == label.getRole()) {
                        result.add(te);
                    }
                }
            }
            label.getWildcardGuard().filter(result);
        } else if (label.isSharp()) {
            if (isNodeType(label) && !isImplicit()) {
                result.add(getNode(label));
            } else {
                result.addAll(edgeSet(label.getSharpLabel()));
            }
        } else {
            assert label.isAtom();
            if (isNodeType(label) && !isImplicit()) {
                TypeNode tn = getNode(label);
                if (tn != null) {
                    result.addAll(getSubtypes(tn));
                }
            } else {
                result.addAll(edgeSet(label.getTypeLabel()));
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
            result.addAll(edgeSet(label));
        }
        return result;
    }

    /** 
     * Returns the most abstract element with respect to subtyping from a given set of types,
     * if one of the types is maximal.
     * @param types the set of types in which the maximum is sought
     * @return the most abstract element from {@code types} if it exists, or {@code null}
     * if none of the types is maximal
     */
    public TypeNode getMaximum(Collection<TypeNode> types) {
        TypeNode result = null;
        for (TypeNode typeNode : types) {
            if (typeNode.isDataType()) {
                continue;
            }
            if (result == null || isSubtype(result, typeNode)) {
                result = typeNode;
            }
        }
        if (result != null && !result.getSubtypes().containsAll(types)) {
            result = null;
        }
        return result;
    }

    /** Returns the most concrete element with respect to subtyping from a given set of types,
     * if one of the types is minimal.
     * @param types the set of types in which the minimum is sought
     * @return the most concrete element from {@code types} if it exists, or {@code null}
     * if the set does not have a minimum
     */
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
    private final Map<Label,TypeNode> typeNodeMap = new HashMap<Label,TypeNode>();

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
    private final SortedMap<String,Sub> componentMap = new TreeMap<String,Sub>();

    /** Set of all labels occurring in the type graph. */
    private Set<TypeLabel> labels;

    /** Node-label-edge-map for precisely matching type edges. */
    private TypeEdgeMap exactEdgeMap;
    /** Node-label-edge-map for type edges starting at supertypes. */
    private TypeEdgeMap superEdgeMap;

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

    private class TypeEdgeMap extends HashMap<TypeLabel,Map<TypeNode,TypeEdge[]>> {
        void put(TypeNode source, TypeNode target, TypeEdge edge) {
            Map<TypeNode,TypeEdge[]> outEdgeMap = get(edge.label());
            if (outEdgeMap == null) {
                put(edge.label(), outEdgeMap = new HashMap<TypeNode,TypeEdge[]>());
            }
            TypeEdge[] targetEdges = outEdgeMap.get(source);
            if (targetEdges == null) {
                outEdgeMap.put(source, targetEdges = new TypeEdge[getFactory().getMaxNodeNr() + 1]);
            }
            targetEdges[target.getNumber()] = edge;
        }

        TypeEdge get(TypeNode source, TypeLabel label, TypeNode target) {
            TypeEdge result = null;
            Map<TypeNode,TypeEdge[]> outEdgeMap = get(label);
            if (outEdgeMap != null) {
                TypeEdge[] targetEdges = outEdgeMap.get(source);
                if (targetEdges != null) {
                    result = targetEdges[target.getNumber()];
                }
            }
            return result;
        }
    }
}
