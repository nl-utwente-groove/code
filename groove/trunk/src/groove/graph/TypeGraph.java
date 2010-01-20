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
import groove.view.FormatException;

import java.util.Collection;
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
    /** Adds the label as well as the edge. */
    @Override
    public BinaryEdge addEdge(Node source, Label label, Node target) {
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
     * Tests if a give graph is correct according to this type graph.
     * @param model the graph to be checked
     * @return a list of type errors found in the graph
     */
    public Set<String> checkTyping(Graph model) {
        Set<String> errors = new TreeSet<String>();
        Map<Node,Label> nodeTypes = new HashMap<Node,Label>();
        for (Edge edge : model.edgeSet()) {
            Label label = edge.label();
            if (label.isNodeType()) {
                nodeTypes.put(edge.source(), label);
            }
        }
        // detect node types
        Set<Node> untypedNodes = new HashSet<Node>();
        for (Node node : model.nodeSet()) {
            Label nodeType = nodeTypes.get(node);
            if (nodeType == null) {
                if (node instanceof VariableNode) {
                    Algebra<?> algebra = ((VariableNode) node).getAlgebra();
                    if (algebra == null) {
                        untypedNodes.add(node);
                    } else {
                        String signature =
                            AlgebraRegister.getSignatureName(algebra);
                        nodeTypes.put(node, DefaultLabel.createLabel(signature,
                            Label.NODE_TYPE));
                    }
                } else if (node instanceof ProductNode) {
                    // leave open for now
                } else {
                    untypedNodes.add(node);
                }
            } else if (!isNodeType(nodeType)) {
                errors.add(String.format(
                    "Unknown node type '%s' for node '%s'", node, nodeType));
            }
        }
        if (!untypedNodes.isEmpty()) {
            errors.add(String.format("Untyped nodes %s", untypedNodes));
        }
        for (Edge edge : model.edgeSet()) {
            if (edge instanceof ArgumentEdge || edge instanceof OperatorEdge) {
                // leave unchecked for now
                continue;
            }
            Label edgeType = edge.label();
            RegExpr expr = null;
            if (edgeType instanceof RegExprLabel) {
                expr = ((RegExprLabel) edgeType).getRegExpr();
                if (expr instanceof RegExpr.Neg) {
                    expr = expr.getNegOperand();
                }
                if (!(expr instanceof RegExpr.Empty)) {
                    errors.add(String.format(
                        "Regular expression label '%s' currently cannot be typed",
                        edgeType));
                    continue;
                }
            }
            Node source = edge.source();
            Label sourceType = nodeTypes.get(source);
            Node target = edge.opposite();
            Label targetType = nodeTypes.get(target);
            if (sourceType == null || targetType == null) {
                // this must be due to an untyped node
                // which was already reported as an error
                continue;
            }
            if (expr != null) {
                if (expr instanceof RegExpr.Empty) {
                    if (sourceType != targetType) {
                        errors.add(String.format(
                            "'%s'-node '%s' and '%s'-node '%s' cannot be merged or compared",
                            sourceType, source, targetType, target));
                    }
                }
            } else if (edgeType.isFlag()) {
                if (!hasFlag(sourceType, edgeType)) {
                    errors.add(String.format(
                        "'%s'-node '%s' has unknown flag '%s'", sourceType,
                        source, edgeType));
                }
            } else if (edgeType.isBinary()) {
                Label declaredTargetType = getTarget(sourceType, edgeType);
                if (declaredTargetType == null) {
                    errors.add(String.format(
                        "'%s'-node '%s' has unknown edge '%s'", sourceType,
                        source, edgeType));
                } else if (DefaultLabel.isDataType(declaredTargetType)
                    || DefaultLabel.isDataType(targetType)) {
                    if (!targetType.equals(declaredTargetType)) {
                        errors.add(String.format(
                            "Target node of '%s' has type '%s' but should have data type '%s'",
                            edge, targetType, declaredTargetType));
                    }
                } else if (!isSubtype(targetType, declaredTargetType)) {
                    errors.add(String.format(
                        "Target node of '%s' has type '%s' which is not a subtype of '%s'",
                        edge, targetType, declaredTargetType));
                }
            }
        }
        return errors;
    }

    /** Tests if a given node type label occurs in this type graph. */
    public boolean isNodeType(Label nodeLabel) {
        assert nodeLabel.isNodeType() : String.format(
            "Label '%s' is not a node type label", nodeLabel);
        return this.labelStore.getSubtypes(nodeLabel) != null;
    }

    /** Tests for a subtype relation between node labels. */
    public boolean isSubtype(Label subLabel, Label superLabel) {
        assert subLabel.isNodeType() : String.format(
            "Label '%s' is not a node type label", subLabel);
        assert superLabel.isNodeType() : String.format(
            "Label '%s' is not a node type label", superLabel);
        Set<Label> subtypes = this.labelStore.getSubtypes(superLabel);
        return subtypes != null && subtypes.contains(subLabel);
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
        Set<Label> supertypes = this.labelStore.getSupertypes(nodeLabel);
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
        Set<Label> supertypes = this.labelStore.getSupertypes(node);
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
        Label result = null;
        Set<? extends Edge> edgeSet = nodeEdgeMap().get(node);
        for (Edge edge : edgeSet) {
            Label label = edge.label();
            if (label.isNodeType()) {
                result = label;
                break;
            }
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

    /** Returns the labels collected in this type graph. */
    public LabelStore getLabelStore() {
        return this.labelStore;
    }

    /** Label store permanently associated with this type graph. */
    private final LabelStore labelStore = new LabelStore();
}
