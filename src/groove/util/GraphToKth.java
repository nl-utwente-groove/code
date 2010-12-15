/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package groove.util;

import groove.algebra.StringAlgebra;
import groove.graph.Edge;
import groove.graph.Node;
import groove.graph.algebra.ValueNode;
import groove.trans.HostGraph;
import groove.view.FormatException;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Exporter class to save a state graph into a simple textual representation
 * used by other verification tools developed by Marieke et al.
 * 
 * This class assumes that the graph is properly typed against a fixed type
 * graph. This type graph has basically three types:
 * - FlowNode: each of this nodes in the state graph is saved as a node in the
 *   output file. Flags, string attributes and FlowProp (see below) nodes
 *   connected to this FlowNode are all saved as atomic propositions in the
 *   file.
 * - FlowEdge: this node type is used to represent an edge in the output. It is
 *   assumed that a node with this type always has a string attribute named
 *   'label' and is connected to the source by an edge labeled 'out' and to the
 *   target by an edge labeled 'to'. A FlowEdge node will be exported as an
 *   edge with proper source, target and label.
 * - FlowProp: this node type encode atomic propositions that hold in the
 *   FlowNodes.
 *   
 * @author Eduardo Zambon
 * @version $Revision $
 */
public final class GraphToKth {

    // ------------------------------------------------------------------------
    // Public methods.
    // ------------------------------------------------------------------------

    /**
     * Converts a graph to a serialized string representation.
     * @param graph the graph to be converted, should be an
     *        <code>AspectGraph</code>.
     * @return a string with the simple encoding of the graph.
     */
    public static String convertGraph(AspectGraph graph) {
        GraphToKth.graph = graph;
        result = new StringBuilder();

        beginFile();
        convertNodes();
        convertEdges();
        endFile();

        return result.toString();
    }

    // ------------------------------------------------------------------------
    // Private methods.
    // ------------------------------------------------------------------------

    /**
     * Stub method to define the file header.
     * The current implementation does nothing.
     * Adapt as necessary if the file format changes.
     */
    private static void beginFile() {
        // Does nothing by design.
    }

    private static void convertNodes() {
        for (AspectNode node : graph.nodeSet()) {
            if (isFlowNode(node)) {
                appendNode(node);
            }
        }
    }

    private static void convertEdges() {
        for (AspectNode node : graph.nodeSet()) {
            if (isFlowEdge(node)) {
                appendFlowEdge(node);
            }
        }

        for (AspectEdge edge : graph.edgeSet()) {
            if (isEmptyEdge(edge)) {
                appendEmptyEdge(edge);
            }
        }
    }

    /**
     * Stub method to define the file tail.
     * The current implementation does nothing.
     * Adapt as necessary if the file format changes.
     */
    private static void endFile() {
        // Does nothing by design.
    }

    // ------------------------------------------------------------------------

    private static boolean isFlowNode(AspectNode node) {
        return FLOW_NODE_TYPE.equals(getType(node));
    }

    private static boolean isFlowEdge(AspectNode node) {
        return FLOW_EDGE_TYPE.equals(getType(node));
    }

    private static boolean isFlowProp(AspectNode node) {
        return FLOW_PROP_TYPE.equals(getType(node)) || isMethodProp(node)
            || isExceptionProp(node);
    }

    private static boolean isMethodProp(AspectNode node) {
        return METHOD_PROP_TYPE.equals(getType(node));
    }

    private static boolean isExceptionProp(AspectNode node) {
        return EXCEPTION_PROP_TYPE.equals(getType(node));
    }

    private static boolean isEmptyEdge(AspectEdge edge) {
        return EMPTY_EDGE_LABEL.equals(edge.label().text());
    }

    // ------------------------------------------------------------------------

    /**
     * @requires that parameter node is of type FlowNode.
     */
    private static void appendNode(AspectNode node) {
        assert isFlowNode(node) : "Node " + node + " has an illegal type.";

        result.append(BEGIN_NODE);
        result.append(SPACE);
        result.append(node.getNumber());
        for (String flag : getFlags(node)) {
            result.append(SPACE);
            result.append(flag);
        }
        for (String strAttr : getStringAttributes(node).values()) {
            result.append(SPACE);
            result.append(strAttr);
        }
        for (String prop : getPropositions(node)) {
            result.append(SPACE);
            result.append(prop);
        }
        result.append(END_NODE);
    }

    /**
     * @requires that parameter node is of type FlowEdge.
     */
    private static void appendFlowEdge(AspectNode node) {
        assert isFlowEdge(node) : "Node " + node + " has an illegal type.";

        result.append(BEGIN_EDGE);
        result.append(SPACE);
        result.append(getFlowEdgeSourceNodeNumber(node));
        result.append(SPACE);
        result.append(getFlowEdgeTargetNodeNumber(node));
        result.append(SPACE);
        result.append(getFlowEdgeLabel(node));
        result.append(END_EDGE);
    }

    private static void appendEmptyEdge(AspectEdge edge) {
        result.append(BEGIN_EDGE);
        result.append(SPACE);
        result.append(edge.source().getNumber());
        result.append(SPACE);
        result.append(edge.target().getNumber());
        result.append(SPACE);
        result.append(OUT_EMPTY_EDGE_LABEL);
        result.append(END_EDGE);
    }

    // ------------------------------------------------------------------------

    private static String getType(AspectNode node) {
        String type = null;
        for (AspectEdge edge : graph.outEdgeSet(node)) {
            if (edge.isNodeType()) {
                type = edge.label().text();
                break;
            }
        }
        return type;
    }

    /**
     * @requires that parameter node is of type FlowNode.
     */
    private static Collection<String> getFlags(AspectNode node) {
        assert isFlowNode(node) : "Node " + node + " has an illegal type.";
        ArrayList<String> flags = new ArrayList<String>();
        for (AspectEdge edge : graph.outEdgeSet(node)) {
            if (edge.isFlag()) {
                flags.add(edge.label().text());
            }
        }
        return flags;
    }

    /**
     * Produces a map with all string attributes of this node.
     * @param node the graph node to inspect.
     * @return a map with the attribute name as key and the attribute value.
     */
    private static Map<String,String> getStringAttributes(AspectNode node) {
        HashMap<String,String> attrs = new HashMap<String,String>();
        HostGraph model = null;
        try {
            model = graph.toGraphView(null).toModel();
        } catch (FormatException e) {
            // Should never happen...
            e.printStackTrace();
        }
        for (Edge edge : model.edgeSet()) {
            if (edge.source().getNumber() == node.getNumber()) {
                String possibleKey = edge.label().text();
                Node possibleTarget = edge.target();
                if (possibleTarget instanceof ValueNode
                    && ((ValueNode) possibleTarget).getAlgebra() instanceof StringAlgebra) {
                    // We found a string attribute.
                    String key = possibleKey;
                    ValueNode target = (ValueNode) possibleTarget;
                    String value = wrapValue((String) target.getValue(), node);
                    attrs.put(key, value);
                }
            }
        }
        return attrs;
    }

    /**
     * @requires that parameter node is of type FlowNode.
     */
    private static Collection<String> getPropositions(AspectNode node) {
        assert isFlowNode(node) : "Node " + node + " has an illegal type.";
        ArrayList<String> props = new ArrayList<String>();
        for (AspectEdge edge : graph.outEdgeSet(node)) {
            if (PROP_EDGE_LABEL.equals(edge.label().text())) {
                AspectNode propNode = edge.target();
                assert isFlowProp(propNode) : "Target node " + propNode
                    + " is not of type FlowProp.";
                props.add(getStringAttributes(propNode).get(LABEL_ATTR));
            }
        }
        return props;
    }

    // ------------------------------------------------------------------------

    private static int getFlowEdgeSourceNodeNumber(AspectNode node) {
        int n = -1;
        for (AspectEdge edge : graph.edgeSet(node)) {
            if (OUT_EDGE_LABEL.equals(edge.label().text())) {
                n = edge.source().getNumber();
            }
        }
        assert n != -1 : "Could not find the source of node " + node;
        return n;
    }

    private static int getFlowEdgeTargetNodeNumber(AspectNode node) {
        int n = -1;
        for (AspectEdge edge : graph.outEdgeSet(node)) {
            if (TO_EDGE_LABEL.equals(edge.label().text())) {
                n = edge.target().getNumber();
            }
        }
        assert n != -1 : "Could not find the target of node " + node;
        return n;
    }

    private static String getFlowEdgeLabel(AspectNode node) {
        return getStringAttributes(node).get(LABEL_ATTR);
    }

    // ------------------------------------------------------------------------

    private static String wrapValue(String value, AspectNode node) {
        String result;
        if (isMethodProp(node)) {
            result = BEGIN_METHOD + value + END_METHOD;
        } else if (isExceptionProp(node)) {
            result = BEGIN_EXCEPTION + value + END_EXCEPTION;
        } else {
            result = value;
        }
        return result;
    }

    // ------------------------------------------------------------------------

    /** The graph the exporter is working on. */
    private static AspectGraph graph;
    /** The StringBuilder the exporter is working on. */
    private static StringBuilder result;

    // Graph types and labels.
    private static final String FLOW_NODE_TYPE = "FlowNode";
    private static final String FLOW_EDGE_TYPE = "FlowEdge";
    private static final String FLOW_PROP_TYPE = "FlowProp";
    private static final String METHOD_PROP_TYPE = "MethodProp";
    private static final String EXCEPTION_PROP_TYPE = "ExceptionProp";
    private static final String EMPTY_EDGE_LABEL = "empty";
    private static final String TO_EDGE_LABEL = "to";
    private static final String OUT_EDGE_LABEL = "out";
    private static final String PROP_EDGE_LABEL = "prop";
    private static final String LABEL_ATTR = "label";

    // Output file strings.
    private static final String BEGIN_NODE = "node";
    private static final String END_NODE = "\n";
    private static final String SPACE = " ";
    private static final String BEGIN_EDGE = "edge";
    private static final String END_EDGE = "\n";
    private static final String OUT_EMPTY_EDGE_LABEL = "eps";
    private static final String BEGIN_METHOD = "meth(";
    private static final String END_METHOD = ")";
    private static final String BEGIN_EXCEPTION = "exc(";
    private static final String END_EXCEPTION = ")";
}
