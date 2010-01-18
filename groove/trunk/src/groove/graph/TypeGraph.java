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

import groove.view.FormatException;

import java.util.Collection;
import java.util.HashMap;
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
        this.labels.addLabel(label);
        return super.addEdge(source, label, target);
    }

    /** Adds the label as well as the edge. */
    @Override
    public Edge addEdge(Node[] ends, Label label) {
        this.labels.addLabel(label);
        return super.addEdge(ends, label);
    }

    /** Adds the labels as well as the edges. */
    @Override
    public boolean addEdgeSetWithoutCheck(Collection<? extends Edge> edgeSet) {
        for (Edge edge : edgeSet) {
            this.labels.addLabel(edge.label());
        }
        return super.addEdgeSetWithoutCheck(edgeSet);
    }

    /** Adds the label as well as the edge. */
    @Override
    public boolean addEdgeWithoutCheck(Edge edge) {
        this.labels.addLabel(edge.label());
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

    /** Returns the unique node type label of a given node. */
    public Label getType(Node node) {
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

    /** Tests if a given node has a given type or a supertype. */
    public boolean hasType(Node node, Label label) {
        assert label.isNodeType() : String.format(
            "Label '%s' is not a node type label", label);
        return this.labels.getSubtypes(getType(node)).contains(label);
    }

    /** Returns the opposite node for a given type node and binary edge label. */
    public Node getTarget(Node node, Label label) {
        Node result = null;
        assert label.isBinary() : String.format(
            "Label '%s' is not a binary edge label", label);
        Set<Label> supertypes = this.labels.getSupertypes(getType(node));
        Set<? extends Edge> edges = labelEdgeSet(2, label);
        for (Edge edge : edges) {
            if (supertypes.contains(getType(edge.source()))) {
                result = edge.opposite();
                break;
            }
        }
        return result;
    }

    /** Tests if a given type node has a given flag. */
    public boolean hasFlag(Node node, Label flag) {
        assert flag.isFlag() : String.format("Label '%s' is not a flag", flag);
        return containsElement(DefaultEdge.createEdge(node, flag, node));
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
            this.labels.addSubtype(supertype, subtype);
        } catch (IllegalArgumentException exc) {
            throw new FormatException(exc.getMessage());
        }
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
                for (Label subtype : this.labels.getSubtypes(sourceType)) {
                    Map<Label,Label> outTypes = outTypeMap.get(subtype);
                    if (outTypes == null) {
                        outTypeMap.put(subtype, outTypes =
                            new HashMap<Label,Label>());
                    }
                    Label oldType = outTypes.put(typeEdge.label(), sourceType);
                    if (oldType != null) {
                        Label lower, upper;
                        if (this.labels.getSubtypes(sourceType).contains(
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
        return this.labels;
    }

    /** Label store permanently associated with this type graph. */
    private final LabelStore labels = new LabelStore();
}
