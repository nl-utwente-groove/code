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
 * $Id$
 */
package nl.utwente.groove.grammar.type;

import static nl.utwente.groove.graph.EdgeRole.FLAG;
import static nl.utwente.groove.graph.EdgeRole.NODE_TYPE;
import static nl.utwente.groove.graph.GraphRole.TYPE;

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
import java.util.TreeSet;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.syntax.SortMap;
import nl.utwente.groove.automaton.RegExpr;
import nl.utwente.groove.automaton.RegExprTyper;
import nl.utwente.groove.automaton.RegExprTyper.Result;
import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.host.HostEdge;
import nl.utwente.groove.grammar.host.HostFactory;
import nl.utwente.groove.grammar.host.HostGraph;
import nl.utwente.groove.grammar.host.HostGraphMorphism;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.grammar.host.ValueNode;
import nl.utwente.groove.grammar.rule.DefaultRuleNode;
import nl.utwente.groove.grammar.rule.LabelVar;
import nl.utwente.groove.grammar.rule.OperatorNode;
import nl.utwente.groove.grammar.rule.RuleEdge;
import nl.utwente.groove.grammar.rule.RuleFactory;
import nl.utwente.groove.grammar.rule.RuleGraph;
import nl.utwente.groove.grammar.rule.RuleGraphMorphism;
import nl.utwente.groove.grammar.rule.RuleLabel;
import nl.utwente.groove.grammar.rule.RuleNode;
import nl.utwente.groove.grammar.rule.VariableNode;
import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.EdgeRole;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.graph.Label;
import nl.utwente.groove.graph.Node;
import nl.utwente.groove.graph.NodeSetEdgeSetGraph;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.Groove;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Extends a standard graph with some useful functionality for querying a type
 * graph.
 * @author Arend Rensink
 * @version $Revision$
 */
public class TypeGraph extends NodeSetEdgeSetGraph<@NonNull TypeNode,@NonNull TypeEdge>
    implements TypeChecker {
    /** Constructs a fresh type graph.
     * @param name the (non-{@code null}) name of the type graph
     */
    public TypeGraph(QualName name) {
        this(name, false);
    }

    /** Constructs a fresh type graph.
     * @param name the (non-{@code null}) name of the type graph
     */
    public TypeGraph(QualName name, boolean implicit) {
        super(name.toString(), true);
        this.implicit = implicit;
        this.factory = new TypeFactory(this);
    }

    /** Returns the qualified name of this type model. */
    public QualName getQualName() {
        return QualName.parse(getName());
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
        Set<TypeNode> newNodes = new HashSet<>();
        Set<TypeEdge> newEdges = new HashSet<>();
        Map<TypeNode,TypeNode> otherToThis = new HashMap<>();
        for (var source : other.nodeSet()) {
            TypeNode oldImage = getNode(source.label());
            TypeNode image = addNode(source.label());
            if (source.isAbstract()) {
                image.setAbstract();
            }
            var color = source.getDeclaredColor();
            if (color != null) {
                image.setDeclaredColor(color);
            }
            image.setLabelPattern(source.getLabelPattern());
            boolean imported = source.isImported() && (oldImage == null || oldImage.isImported());
            image.setImported(imported);
            if (!imported) {
                newNodes.add(image);
            }
            otherToThis.put(source, image);
        }
        for (TypeEdge otherEdge : other.edgeSet()) {
            TypeEdge image = addEdge(otherToThis.get(otherEdge.source()), otherEdge.label(),
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
        assert label.getRole() == EdgeRole.NODE_TYPE : String
            .format("Label %s is not a node type", label);
        TypeNode result = this.typeNodeMap.get(label);
        if (result == null) {
            // the following implicitly adds the node to the graph
            result = getFactory().createNode(label);
        }
        return result;
    }

    @Override
    public TypeGraph clone() {
        TypeGraph result = new TypeGraph(getQualName());
        try {
            result.add(this);
        } catch (FormatException e) {
            assert false;
        }
        return result;
    }

    @Override
    public TypeGraph newGraph(String name) {
        return new TypeGraph(QualName.parse(name));
    }

    @Override
    public boolean removeEdge(TypeEdge edge) {
        throw Exceptions.unsupportedOp("Edge removal (%s) not allowed in type graphs", edge);
    }

    @Override
    public boolean removeNode(TypeNode node) {
        throw Exceptions.unsupportedOp("Node removal (%s) not allowed in type graphs", node);
    }

    /**
     * Adds an inheritance pair to the type graph. The node type labels should
     * already be in the graph.
     * @throws FormatException if the supertype or subtype is not a node type,
     *         or if the new subtype relation creates a cycle.
     */
    public void addInheritance(TypeNode subtype, TypeNode supertype) throws FormatException {
        testFixed(false);
        if (supertype.label().isSort()) {
            throw new FormatException("Data type '%s' cannot be supertype", supertype);
        }
        if (subtype.label().isSort()) {
            throw new FormatException("Data type '%s' cannot be subtype", subtype);
        }
        if (this.nodeSupertypeMap.get(supertype).contains(subtype)) {
            throw new FormatException(String
                .format("The relation '%s -> %s' introduces a cyclic type dependency", subtype,
                        supertype));
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

    /** Checks if the graph satisfies the properties of a type graph.
     * This should be called after the type graph has been fixed.
     */
    public void test() throws FormatException {
        FormatErrorSet errors = new FormatErrorSet();
        Set<TypeLabel> edgeLabels = new HashSet<>();
        // test for outgoing edges or flags on data types
        for (TypeEdge typeEdge : edgeSet()) {
            if (typeEdge.getRole() != NODE_TYPE && !typeEdge.isAbstract()) {
                TypeNode source = typeEdge.source();
                TypeLabel typeLabel = typeEdge.label();
                TypeLabel sourceType = source.label();
                // check for outgoing edge types from data types
                if (sourceType.isSort()) {
                    errors
                        .add("Data type '%s' cannot have %s", sourceType.text(),
                             typeLabel.getRole() == FLAG
                                 ? "flags"
                                 : "outgoing edges",
                             source);
                }
                edgeLabels.add(typeEdge.label());
            }
        }
        // check for type confusion
        for (TypeLabel edgeLabel : edgeLabels) {
            // non-abstract edge types must be distinguishable
            // either in source type or in target type
            // also for all subtypes
            List<TypeEdge> edges = new ArrayList<>(edgeSet(edgeLabel));
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
                        var sourceSubtype = getCommonSubtypes(edge1.source(), edge2.source())
                            .stream()
                            .findFirst()
                            .get();
                        if (edgeLabel.hasRole(FLAG)) {
                            errors
                                .add("Potential type confusion: Declared flags %3$s:%1$s and %4$s:%1$s cause %2$s:%1$s to be ambiguously typed",
                                     edgeLabel.text(), sourceSubtype, edge1.source(),
                                     edge2.source(), edge1, edge2, edge1.getGraph(),
                                     edge2.getGraph());
                        } else {
                            var targetSubtype = getCommonSubtypes(edge1.target(), edge2.target())
                                .stream()
                                .findFirst()
                                .get();
                            errors
                                .add("Potential type confusion: Declared edges %4$s and %5$s cause %2$s--%1$s->%3$s to be ambiguously typed",
                                     edgeLabel.text(), sourceSubtype, targetSubtype, edge1, edge2);
                        }
                    }
                }
            }
        }
        // check for inconsistent color specifications in the type hierarchy
        // this occurs if two node types with declared colours share a subtype.
        var coloredNodes = nodeSet().stream().filter(TypeNode::hasDeclaredColor).toList();
        for (var n1 : coloredNodes) {
            var n1Color = n1.getDeclaredColor();
            assert n1Color != null;
            boolean start = false;
            for (var n2 : coloredNodes) {
                if (n1 == n2) {
                    start = true;
                    continue;
                }
                if (!start) {
                    continue;
                }
                if (n1Color.equals(n2.getDeclaredColor())) {
                    continue;
                }
                if (getSubtypes(n1).contains(n2) || getSubtypes(n2).contains(n1)) {
                    continue;
                }
                if (hasCommonSubtype(n1, n2)) {
                    errors.add("Incompatible color declarations on %s and %s", n1, n2);
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
                Set<TypeEdge> subtypes = new HashSet<>();
                subtypes.add(edge);
                this.edgeSubtypeMap.put(edge, subtypes);
                Set<TypeEdge> supertypes = new HashSet<>();
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
            // propagate colours to subtypes
            var coloredNodes = nodeSet().stream().filter(TypeNode::hasDeclaredColor).toList();
            for (TypeNode node : coloredNodes) {
                // propagate colours
                Color color = node.getColor();
                assert color != null;
                Set<TypeNode> propagatees = new HashSet<>(this.nodeSubtypeMap.get(node));
                propagatees.remove(node);
                while (!propagatees.isEmpty()) {
                    Iterator<TypeNode> subNodeIter = propagatees.iterator();
                    TypeNode subNode = subNodeIter.next();
                    subNodeIter.remove();
                    if (subNode.getColor() == null) {
                        subNode.setDerivedColor(color);
                    } else {
                        propagatees.removeAll(this.nodeSubtypeMap.get(subNode));
                    }
                }
            }
            // propagate patterns to subtypes
            var patternedNodes = nodeSet().stream().filter(TypeNode::hasLabelPattern).toList();
            for (TypeNode node : patternedNodes) {
                // propagate label patterns
                LabelPattern nodePattern = node.getLabelPattern();
                assert nodePattern != null;
                Set<TypeNode> propagatees = new HashSet<>(this.nodeSubtypeMap.get(node));
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
        return result;
    }

    @Override
    public TypeFactory getFactory() {
        return this.factory;
    }

    /** Type factory associated with this type graph. */
    private final TypeFactory factory;

    /** Changes the status of a node type to imported.
     * The node type is assumed to be in the graph already.
     * @param imported TODO
     */
    public void setImported(TypeNode node, boolean imported) {
        if (imported) {
            this.imports.add(node);
        } else {
            this.imports.remove(node);
        }
    }

    /** Returns the set of imported node types.
     */
    public Set<TypeNode> getImports() {
        return this.imports;
    }

    /** Mapping from node types to the number of non-imported nodes with that type.
     * If the number is zero, the node type is imported.
     */
    private final Set<TypeNode> imports = new TreeSet<>();

    /**
     * Indicates if this is a degenerate type graph,
     * i.e. one without true node types
     */
    public boolean isImplicit() {
        return this.implicit;
    }

    /**
     * Flag indicating that this is an implicit type graph.
     * This affects the type analysis: an implicit type graph cannot
     * give rise to typing errors.
     */
    private final boolean implicit;

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
    public RuleGraphMorphism analyzeRule(RuleGraph source,
                                         RuleGraphMorphism parentTyping) throws FormatException {
        testFixed(true);
        RuleFactory ruleFactory = parentTyping.getFactory();
        RuleGraphMorphism result = new RuleGraphMorphism(ruleFactory);
        FormatErrorSet errors = new FormatErrorSet();
        // extract the variable types from the parent typing
        result.copyVarTyping(parentTyping);
        // extract node variable typing and push it into the result
        Map<RuleNode,RuleNode> nodeImageMap = null;
        if (!isImplicit()) {
            nodeImageMap = computeNodeImages(source, parentTyping, result);
        }
        // create images for the non-operator nodes, and collect the operator nodes
        List<OperatorNode> opNodes = new ArrayList<>();
        for (RuleNode node : source.nodeSet()) {
            try {
                RuleNode image;
                if (node instanceof OperatorNode) {
                    opNodes.add((OperatorNode) node);
                    continue;
                } else if (node instanceof VariableNode) {
                    image = cloneVariableNode(ruleFactory, (VariableNode) node);
                } else if (isImplicit()) {
                    image = ruleFactory.createNode(node.getNumber());
                } else {
                    assert nodeImageMap != null; // implied by !isImplicit()
                    // get the type from the parent typing or from the node type edges
                    RuleNode parentImage = parentTyping.getNode(node);
                    image = nodeImageMap.get(node);
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
            List<VariableNode> newArgs = new ArrayList<>();
            for (VariableNode arg : opNode.getArguments()) {
                VariableNode argImage = (VariableNode) result.getNode(arg);
                if (argImage == null) {
                    if (opNode.getOperator().isVarArgs()) {
                        argImage = cloneVariableNode(ruleFactory, arg);
                    } else {
                        // since we should have already added all variable nodes
                        // presumably this means that the argument contains an error
                        imageOk = false;
                        break;
                    }
                }
                newArgs.add(argImage);
            }
            VariableNode newTarget = (VariableNode) result.getNode(opNode.getTarget());
            imageOk &= newTarget != null;
            if (imageOk) {
                RuleNode image = ruleFactory
                    .createOperatorNode(opNode.getNumber(), opNode.getOperator(), newArgs,
                                        newTarget);
                result.putNode(opNode, image);
            }
        }
        // separate the edges
        // label variable edges
        Set<RuleEdge> varEdges = new HashSet<>();
        // other regular expression edges
        Set<RuleEdge> regExprEdges = new HashSet<>();
        // other typable edges (except already processed node types)
        Set<RuleEdge> simpleEdges = new HashSet<>();
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
            RuleEdge image = ruleFactory
                .createEdge(result.getNode(varEdge.source()), varEdge.label(),
                            result.getNode(varEdge.target()));
            Set<? extends TypeElement> matchingTypes = image.getMatchingTypes();
            for (TypeGuard guard : image.getTypeGuards()) {
                matchingTypes.retainAll(result.addVarTypes(guard.getVar(), matchingTypes));
            }
            if (image.getMatchingTypes().isEmpty()) {
                errors
                    .add("%s wildcard %s cannot match anything",
                         image.label().getRole().getDescription(true), image.label(), varEdge);
            }
            result.putEdge(varEdge, image);
        }
        // do the non-regular expression edges
        for (RuleEdge edge : simpleEdges) {
            RuleLabel edgeLabel = edge.label();
            RuleNode sourceImage = result.getNode(edge.source());
            assert sourceImage != null;
            RuleNode targetImage = result.getNode(edge.target());
            assert targetImage != null;
            TypeNode sourceType = sourceImage.getType();
            TypeNode targetType = targetImage.getType();
            TypeEdge typeEdge
                = getTypeEdge(sourceImage.getType(), edgeLabel.getTypeLabel(), targetType, false);
            if (typeEdge == null) {
                // if the source type is the top type, we must be in a
                // graph editor where a new edge label has been used and
                // the graph has not yet been saved. This will be solved
                // upon saving, and the error is confusing, so dont't
                // throw it
                if (!sourceType.isTopType()) {
                    errors
                        .add(unknownEdgeTypeText(sourceType, edge, targetType), sourceImage, edge);
                }
            } else {
                result.putEdge(edge, ruleFactory.createEdge(sourceImage, edgeLabel, targetImage));
            }
        }
        RegExprTyper regExprTyper = new RegExprTyper(this, result.getVarTyping());
        for (RuleEdge edge : regExprEdges) {
            RuleLabel edgeLabel = edge.label();
            RuleNode sourceImage = result.getNode(edge.source());
            assert sourceImage != null;
            RuleNode targetImage = result.getNode(edge.target());
            assert targetImage != null;
            @SuppressWarnings("null")
            RuleLabel checkLabel = edgeLabel.isNeg()
                ? edgeLabel.getNegOperand().toLabel()
                : edgeLabel;
            RegExpr expr = checkLabel.getMatchExpr();
            Result typeResult = expr.apply(regExprTyper);
            if (typeResult.hasErrors()) {
                // if the source type is the top type, we must be in a
                // graph editor where a new edge label has been used and
                // the graph has not yet been saved. This will be solved
                // upon saving, and the error is confusing, so dont't
                // throw it
                if (!sourceImage.getType().isTopType()) {
                    errors.addAll(typeResult.getErrors().extend(edge));
                }
            } else {
                // check if source and target type fit
                boolean fit;
                if (checkLabel.isAtom() && (isImplicit() || checkLabel.getRole() != NODE_TYPE)) {
                    // (negated) atoms have to correspond to an edge type
                    Set<TypeNode> resultSourceTypes = typeResult.getAll(sourceImage.getType());
                    fit = resultSourceTypes != null
                        && resultSourceTypes.contains(targetImage.getType());
                } else {
                    // for all other regular expressions, the matching path may start and
                    // end in a subtype of the regexpr edge source/target
                    fit = false;
                    Set<TypeNode> targetTypes = new HashSet<>(getMatchingTypes(targetImage));
                    for (TypeNode sourceType : getMatchingTypes(sourceImage)) {
                        Set<TypeNode> resultTargetTypes = typeResult.getAll(sourceType);
                        if (resultTargetTypes != null && targetTypes.removeAll(resultTargetTypes)) {
                            fit = true;
                            break;
                        }
                    }
                }
                if (!fit) {
                    errors
                        .add("No %s-path can exist between %s and %s", checkLabel,
                             sourceImage.getType(), targetImage.getType(), edge);
                }
            }
            result.putEdge(edge, ruleFactory.createEdge(sourceImage, edgeLabel, targetImage));
        }
        errors.throwException();
        return result;

    }

    /**
     * Clones a variable node in a given rule factory.
     * @throws FormatException if the data type of the variable node does not occur in this type graph
     */
    private VariableNode cloneVariableNode(RuleFactory ruleFactory,
                                           VariableNode node) throws FormatException {
        VariableNode image = ruleFactory.createVariableNode(node.getNumber(), node.getTerm());
        // check if the type graph actually has the primitive type
        if (!nodeSet().contains(image.getType())) {
            throw new FormatException("Data type %s does not occur in type graph", image.getType(),
                node);
        }
        return image;
    }

    /** Computes typed images for the nodes of an untyped rule graph.
     * The images are constrained to the node types satisfying the explicit type labels,
     * as well as the node type constraints imposed by label wildcards
     * @param source rule graph with which the node images are computed
     * @param parentTyping morphism from the untyped rule graph to the typed version
     * of a parent rule graph; used to get a hint for the node types
     * @param typing resulting morphism from the untyped to the typed rule graph;
     * this is modified using the node variables constraints
     * @return mapping from source rule nodes to typed versions
     * @throws FormatException if any errors were detected while constructing node images
     */
    private Map<RuleNode,RuleNode> computeNodeImages(RuleGraph source,
                                                     RuleGraphMorphism parentTyping,
                                                     RuleGraphMorphism typing) throws FormatException {
        Map<RuleNode,RuleNode> result = new HashMap<>();
        // mapping from type variables to sets of potential node types
        Map<LabelVar,Set<@NonNull TypeNode>> varTypesMap = new HashMap<>();
        FormatErrorSet errors = new FormatErrorSet();
        // mapping from rule nodes to sets of label variables that occur on them
        Map<RuleNode,Set<LabelVar>> nodeVarsMap = new HashMap<>();
        // mapping from rule nodes to their type guards
        Map<RuleNode,List<TypeGuard>> nodeGuardsMap = new HashMap<>();
        // mapping from rule nodes to their allowed types, according to the type guards
        Map<RuleNode,Set<TypeNode>> allowedTypesMap = new HashMap<>();
        // mapping from nodes to declared node type
        Map<RuleNode,TypeNode> declaredTypeMap = new HashMap<>();
        // collection of nodes declared to be sharp in the source graph
        Set<RuleNode> sharpNodes = new HashSet<>();
        for (RuleNode node : source.nodeSet()) {
            if (!(node instanceof DefaultRuleNode)) {
                continue;
            }
            // variable to detect duplicate type edges
            TypeNode declaredType = null;
            // collection of possible types for this node
            Set<TypeNode> allowedTypes = null;
            RuleNode parentNode = parentTyping.getNode(node);
            if (parentNode != null) {
                allowedTypes = new HashSet<>(parentNode.getMatchingTypes());
            }
            Set<LabelVar> vars = new HashSet<>();
            List<TypeGuard> guards = new ArrayList<>();
            for (RuleEdge edge : source.edgeSet(node)) {
                if (edge.getRole() != NODE_TYPE) {
                    continue;
                }
                if (allowedTypes == null) {
                    allowedTypes = new HashSet<>(nodeSet());
                    allowedTypes.removeAll(getFactory().getDataTypes());
                }
                if (edge.label().isWildcard()) {
                    TypeGuard guard = edge.label().getWildcardGuard();
                    // for node type wildcards, the guard is never null
                    assert guard != null;
                    guards.add(guard);
                    LabelVar var = guard.getVar();
                    vars.add(var);
                    Set<@NonNull TypeNode> varTypes = varTypesMap.get(var);
                    if (varTypes == null) {
                        varTypesMap.put(var, varTypes = new HashSet<>(allowedTypes));
                    }
                    guard.filter(varTypes);
                } else {
                    TypeLabel typeLabel = edge.label().getTypeLabel();
                    assert typeLabel != null;
                    TypeNode newType = getNode(typeLabel);
                    boolean sharp = edge.label().isSharp();
                    if (newType == null) {
                        errors.add("Unknown node type %s", typeLabel, node);
                    } else if (declaredType != null) {
                        errors
                            .add("Duplicate node types %s and %s", declaredType.label(), typeLabel,
                                 node);
                    } else {
                        // we shouldn't retype the node, or the connection between
                        // the parent level and this one will be lost
                        declaredType = parentNode == null
                            ? newType
                            : parentNode.getType();
                        allowedTypes
                            .retainAll(sharp
                                ? Collections.singleton(newType)
                                : newType.getSubtypes());
                        if (sharp) {
                            sharpNodes.add(node);
                        }
                    }
                }
            }
            if (allowedTypes == null) {
                errors.add("Untyped node", node);
            } else if (allowedTypes.isEmpty()) {
                errors.add("Inconsistent types on node", node);
            } else {
                declaredTypeMap.put(node, declaredType);
                allowedTypesMap.put(node, allowedTypes);
            }
            if (!vars.isEmpty()) {
                nodeVarsMap.put(node, vars);
            }
            nodeGuardsMap.put(node, guards);
        }
        errors.throwException();
        // iterate until all variable and rule node types are consistent
        boolean changed = true;
        while (changed) {
            changed = false;
            for (var e : nodeVarsMap.entrySet()) {
                RuleNode node = e.getKey();
                Set<TypeNode> nodeTypes = allowedTypesMap.get(node);
                for (LabelVar var : e.getValue()) {
                    Set<TypeNode> varTypes = varTypesMap.get(var);
                    changed |= varTypes.retainAll(nodeTypes);
                    changed |= nodeTypes.retainAll(varTypes);
                }
            }
        }
        // now construct the result map by taking the maximum from the type nodes
        for (var e : allowedTypesMap.entrySet()) {
            RuleNode node = e.getKey();
            var declaredType = declaredTypeMap.get(node);
            var allowedTypes = e.getValue();
            if (allowedTypes.isEmpty()) {
                // this must have become empty because of type variable constraints
                Set<LabelVar> vars = nodeVarsMap.get(node);
                String varString = Groove.toString(vars.toArray(), ", ", " and ");
                if (declaredType == null) {
                    errors.add("Inconsistent type variable constraints for %s", node);
                } else {
                    errors
                        .add("Declared node type '%s' conflicts with type variable constraints for %s",
                             declaredType, varString, node);
                }
            } else {
                TypeNode type = declaredType == null
                    ? getMaximum(allowedTypes)
                    : declaredType;
                if (type == null) {
                    errors
                        .add("Ambiguous typing: %s do not include a common supertype",
                             Groove.toString(allowedTypes.toArray(), ", ", " and "), node);
                } else {
                    RuleNode image = parentTyping
                        .getFactory()
                        .nodes(type, declaredType != null, sharpNodes.contains(node),
                               nodeGuardsMap.get(node))
                        .createNode(node.getNumber());
                    image.getMatchingTypes().retainAll(allowedTypes);
                    result.put(node, image);
                }
            }
        }
        errors.throwException();
        // push the variable typings into the typing morphism
        for (Map.Entry<LabelVar,Set<TypeNode>> e : varTypesMap.entrySet()) {
            typing.addVarTypes(e.getKey(), e.getValue());
        }
        return result;
    }

    /**
     * Returns a fresh set of all type nodes that may be matched
     * by a given (typed) rule node.
     * These are either only the rule node type itself, or all subtypes,
     * depending on whether the rule node is sharp or not.
     */
    private Set<TypeNode> getMatchingTypes(RuleNode node) {
        Set<TypeNode> result = new HashSet<>();
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
        HostFactory hostFactory = HostFactory.newInstance(getFactory(), source.isSimple());
        HostGraphMorphism morphism = new HostGraphMorphism(hostFactory);
        FormatErrorSet errors = new FormatErrorSet();
        for (HostNode node : source.nodeSet()) {
            try {
                HostNode image;
                if (node instanceof ValueNode valueNode) {
                    image = hostFactory
                        .values(valueNode.getAlgebra(), valueNode.getValue())
                        .createNode(valueNode.getNumber());
                } else if (isImplicit()) {
                    image = hostFactory.createNode(node.getNumber());
                } else {
                    List<HostEdge> nodeTypeEdges = detectNodeType(source, node);
                    if (nodeTypeEdges.isEmpty()) {
                        errors.add("Untyped node", node);
                        image = node;
                    } else if (nodeTypeEdges.size() > 1) {
                        errors
                            .add("Multiple node types %s, %s", nodeTypeEdges.get(0),
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
                            throw new FormatException("Abstract node type '%s'", type,
                                nodeTypeEdge);
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
            // nullness checks should be redundant at this point
            //            if (sourceType == null || targetType == null) {
            //                // this must be due to an untyped node
            //                // which was already reported as an error
            //                continue;
            //            }
            TypeEdge typeEdge = getTypeEdge(sourceType, edgeType, targetType, false);
            if (typeEdge == null) {
                // if the source type is the top type, we must be in a
                // graph editor where a new edge label has been used and
                // the graph has not yet been saved. This will be solved
                // upon saving, and the error is confusing, so dont't
                // throw it
                if (!sourceType.isTopType()) {
                    errors
                        .add(unknownEdgeTypeText(sourceType, edge, targetType), edge.source(), edge,
                             sourceImage);
                }
            } else if (typeEdge.isAbstract()) {
                errors.add(abstractEdgeTypeText(sourceType, edge), edge.source());
            } else {
                morphism.putEdge(edge, hostFactory.createEdge(sourceImage, edgeType, targetImage));
            }
        }
        errors.throwException();
        return morphism;
    }

    /**
     * Derives a type label for a node from the outgoing node type edges in a graph.
     * @param source the source graph to create the mappings for
     * @param node the node for which to discover the type
     * @throws FormatException on nonexistent, abstract or duplicate node types
     */
    private List<HostEdge> detectNodeType(HostGraph source, HostNode node) throws FormatException {
        List<HostEdge> result = new ArrayList<>();
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

    /** Node-label-edge-map for precisely matching type edges. */
    private TypeEdgeMap exactEdgeMap;
    /** Node-label-edge-map for type edges starting at supertypes. */
    private TypeEdgeMap superEdgeMap;

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
        return precise
            ? supertype.equals(subtype)
            : isSubtype(subtype, supertype);
    }

    /**
     * Returns the type node with a node type label, given as a
     * string. The string should be in prefix form (see {@link TypeLabel#createLabel(String)}).
     * @param label the label to look up.
     * @return the type node labelled with {@code label}, or {@code null}
     * if {@code label} does not correspond to a note type label.
     */
    public @Nullable TypeNode getNode(String label) {
        return getNode(TypeLabel.createLabel(label));
    }

    /**
     * Returns the type node with a given node type label,
     * if there is such a node in the type graph. Returns {@code null}
     * if the label is not a known node type.
     */
    public @Nullable TypeNode getNode(@NonNull Label label) {
        assert label.getRole() == NODE_TYPE;
        if (isImplicit()) {
            return this.factory.getTopNode();
        } else {
            return this.typeNodeMap.get(getActualType(label));
        }
    }

    /** Mapping from node type labels to the corresponding type nodes. */
    private final Map<Label,TypeNode> typeNodeMap = new HashMap<>();

    /**
     * Returns the actual type label wrapped inside a (possibly sharp) type.
     * Returns {@code null} if the label is an operator or argument edge.
     */
    private TypeLabel getActualType(Label type) {
        TypeLabel result;
        if (type instanceof RuleLabel ruleLabel) {
            result = ruleLabel.getTypeLabel();
        } else {
            assert type instanceof TypeLabel;
            result = (TypeLabel) type;
        }
        return result;
    }

    /** Tests if two nodes have a common (not necessarily proper) subtype. */
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
        assert sub1 != null : String
            .format("Node type %s does not exist in type graph %s", node1, this);
        assert sub2 != null : String
            .format("Node type %s does not exist in type graph %s", node2, this);
        if (sub1.size() == 1) {
            // sub1 doesn't have a proper subtype
            return sub2.size() > 1 && sub2.contains(node1);
        } else if (sub2.size() == 1) {
            // sub2 doesn't have a proper subtype
            return sub1.contains(node2);
        }
        return !Collections.disjoint(sub1, sub2);
    }

    /** Returns the set of all (not necessarily proper) subtypes. */
    private Set<TypeNode> getCommonSubtypes(TypeNode node1, TypeNode node2) {
        Set<TypeNode> result = new HashSet<>(getSubtypes(node1));
        result.retainAll(getSubtypes(node2));
        return result;
    }

    /** Returns the set of all type labels occurring in the type graph. */
    public Set<TypeLabel> getLabels() {
        testFixed(true);
        if (this.labels == null) {
            this.labels = new HashSet<>();
            for (TypeNode node : nodeSet()) {
                this.labels.add(node.label());
            }
            for (TypeEdge edge : edgeSet()) {
                this.labels.add(edge.label());
            }
        }
        return this.labels;
    }

    /** Set of all labels occurring in the type graph. */
    private Set<TypeLabel> labels;

    /** Returns an unmodifiable view on the mapping from node type labels to direct supertypes. */
    public Map<TypeNode,Set<TypeNode>> getDirectSupertypeMap() {
        return Collections.unmodifiableMap(this.nodeDirectSupertypeMap);
    }

    /**
     * Mapping from node types to direct supertypes.
     * The inverse of {@link #nodeDirectSubtypeMap}.
     * Built at the moment of fixing the type graph.
     */
    private final NodeTypeMap nodeDirectSupertypeMap = new NodeTypeMap(false);

    /** Returns an unmodifiable view on the mapping from node type labels to direct subtypes. */
    public Map<TypeNode,Set<TypeNode>> getDirectSubtypeMap() {
        return Collections.unmodifiableMap(this.nodeDirectSubtypeMap);
    }

    /**
     * Mapping from node types to direct subtypes.
     * The inverse of {@link #nodeDirectSupertypeMap}.
     * Built at the moment of fixing the type graph.
     */
    private final NodeTypeMap nodeDirectSubtypeMap = new NodeTypeMap(false);

    /** Returns the set of subtypes of a given node type. */
    public @NonNull Set<TypeNode> getSubtypes(TypeNode node) {
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

    /**
     * Reflexive and transitive mapping from node types to node subtypes.
     * The closure of {@link #nodeDirectSubtypeMap}, and the inverse of {@link #nodeSupertypeMap}.
     * Built at the moment of fixing the type graph.
     */
    private final NodeTypeMap nodeSubtypeMap = new NodeTypeMap(true);

    /** Returns the set of subtypes of a given edge type. */
    public @NonNull Set<TypeEdge> getSubtypes(TypeEdge edge) {
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

    /**
     * Mapping from abstract edge types to edge subtypes.
     * Built at the moment of fixing the type graph.
     */
    private final Map<TypeEdge,Set<TypeEdge>> edgeSubtypeMap = new HashMap<>();

    /** Returns the set of supertypes of a given node type. */
    public @NonNull Set<TypeNode> getSupertypes(TypeNode node) {
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

    /** Returns the set of direct supertypes of a given node type. */
    public Set<TypeNode> getDirectSupertypes(TypeNode node) {
        return getDirectSupertypeMap().get(node);
    }

    /**
     * Reflexive and transitive mapping from node types to node supertypes.
     * The closure of {@link #nodeDirectSupertypeMap}, and the inverse of {@link #nodeSubtypeMap}.
     * Built at the moment of fixing the type graph.
     */
    private final NodeTypeMap nodeSupertypeMap = new NodeTypeMap(true);

    /** Returns the set of supertypes of a given edge type. */
    public @NonNull Set<TypeEdge> getSupertypes(TypeEdge edge) {
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
     * Mapping from edge types to abstract edge supertypes.
     * Built at the moment of fixing the type graph.
     */
    private final Map<TypeEdge,Set<TypeEdge>> edgeSupertypeMap = new HashMap<>();

    /**
     * Returns the set of type nodes and edges in this type graph
     * that can be matched by a given rule label.
     */
    public Set<TypeElement> getMatches(RuleLabel label) {
        Set<@NonNull TypeElement> result = new HashSet<>();
        if (label.isInv()) {
            label = label.getInvLabel();
            assert label != null;
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
            var guard = label.getWildcardGuard();
            assert guard != null;
            guard.filter(result);
        } else if (label.isSharp()) {
            if (isNodeType(label) && !isImplicit()) {
                TypeNode node = getNode(label);
                assert node != null; // because isNodeType(label)
                result.add(node);
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
                var tl = label.getTypeLabel();
                assert tl != null;
                result.addAll(edgeSet(tl));
            }
        }
        return result;
    }

    /** Returns the set of type elements with a given label. */
    public Set<? extends TypeElement> getTypes(TypeLabel label) {
        Set<TypeElement> result = new HashSet<>();
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
            if (typeNode.isSort()) {
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
            if (typeNode.isSort()) {
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
        Set<TypeNode> ubs = new HashSet<>(nodeSet());
        for (TypeNode typeNode : types) {
            if (typeNode.isSort()) {
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

    /** Mapping from component type graph names to the type elements in this type graph. */
    private final SortedMap<String,Sub> componentMap = new TreeMap<>();

    @Override
    protected boolean isTypeCorrect(Node node) {
        return node instanceof TypeNode;
    }

    @Override
    protected boolean isTypeCorrect(Edge edge) {
        return edge instanceof TypeEdge;
    }

    @Override
    public TypeGraph getTypeGraph() {
        return this;
    }

    @Override
    public boolean isTrivial() {
        return getCheckers().isEmpty();
    }

    @Override
    public FormatErrorSet check(HostGraph graph) {
        FormatErrorSet result = new FormatErrorSet();
        for (TypeChecker checker : getCheckers()) {
            result.addAll(checker.check(graph));
        }
        return result;
    }

    /** Returns the list of type checkers for this type graph. */
    public List<TypeChecker> getCheckers() {
        if (this.checkers == null) {
            this.checkers = new ArrayList<>();
            if (!isImplicit()) {
                TypeChecker checker = new MultiplicityChecker(this);
                if (!checker.isTrivial()) {
                    this.checkers.add(checker);
                }
                checker = new ContainmentChecker(this);
                if (!checker.isTrivial()) {
                    this.checkers.add(checker);
                }
            }
        }
        return this.checkers;
    }

    private List<TypeChecker> checkers;

    /** Returns the error message text for an edge of unknown type. */
    static private String abstractEdgeTypeText(TypeNode source, Edge edge) {
        var role = edge.getRole();
        return ABSTRACT_EDGE_TYPE.formatted(source, edge, role.getDescription(false));
    }

    /** Returns the error message text for an edge of unknown type. */
    static private String unknownEdgeTypeText(TypeNode source, Edge edge, TypeNode target) {
        var role = edge.getRole();
        var text = role == EdgeRole.BINARY
            ? UNKNOWN_BINARY_EDGE_TYPE
            : UNKNOWN_EDGE_TYPE;
        return text.formatted(source, edge.label(), role.getDescription(false), target);
    }

    /** Error message for an abstract edge. */
    static private final String ABSTRACT_EDGE_TYPE = "%s-node has abstract %s-%s";
    /** Error message for a (non-binary) edge of unknown type. */
    static private final String UNKNOWN_EDGE_TYPE = "%s-node has unknown %s-%s";
    /** Error message for a binary edge of unknown type. */
    static private final String UNKNOWN_BINARY_EDGE_TYPE = UNKNOWN_EDGE_TYPE + " to %s";

    /** Returns a map from node type labels to the sort map of those node types.
     * The sort map is a mapping from attribute field names (of the node type or any of
     * its supertypes) to their sorts, insofar those are uniquely determined.
     */
    public Map<TypeLabel,SortMap> getTypeSortMap() {
        var result = new HashMap<TypeLabel,SortMap>();
        for (var node : nodeSet()) {
            if (!node.isSort()) {
                var sortMap = new SortMap();
                result.put(node.label(), sortMap);
                var fieldEdges = new HashSet<TypeEdge>();
                node
                    .getSupertypes()
                    .stream()
                    .flatMap(sn -> outEdgeSet(sn).stream())
                    .filter(e -> e.target().isSort())
                    .forEach(fieldEdges::add);
                var multiSorted = new HashSet<String>();
                for (var e : fieldEdges) {
                    var name = e.text();
                    if (sortMap.contains(name)) {
                        multiSorted.add(name);
                        sortMap.remove(name);
                    }
                    if (!multiSorted.contains(name)) {
                        var sort = e.target().getSort();
                        assert sort != null;
                        sortMap.add(name, sort);
                    }
                }
            }
        }
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
            Set<TypeNode> record = new HashSet<>();
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
                put(edge.label(), outEdgeMap = new HashMap<>());
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
