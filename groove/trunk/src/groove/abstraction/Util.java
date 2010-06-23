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
package groove.abstraction;

import groove.graph.DefaultLabel;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.NodeEdgeMap;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * Utility functions for abstraction.
 * @author Eduardo Zambon
 * @version $Revision $
 */
public class Util {

    /** EDUARDO */
    public static Set<Label> getNodeLabels(Graph graph, Node node) {
        HashSet<Label> nodeLabels = new HashSet<Label>();
        for (Edge edge : graph.outEdgeSet(node)) {
            if (isUnary(edge)) {
                nodeLabels.add(edge.label());
            }
        }
        return nodeLabels;
    }

    /** EDUARDO */
    public static Set<Edge> getBinaryEdges(Graph graph) {
        HashSet<Edge> edges = new HashSet<Edge>();
        for (Edge edge : graph.edgeSet()) {
            if (!isUnary(edge)) {
                edges.add(edge);
            }
        }
        return edges;
    }

    /** EDUARDO */
    public static boolean isUnary(Edge edge) {
        boolean result = false;
        Label label = edge.label();
        if (label instanceof DefaultLabel) {
            // We may have labels with proper information.
            DefaultLabel dl = (DefaultLabel) label;
            result = !dl.isBinary();
            if (!result) {
                // It may be the case that binary edges are still used
                // in a plain graph...
                result =
                    label.text().startsWith("type:")
                        || label.text().startsWith("flag:");
            }
        }
        return result;
    }

    /** EDUARDO */
    public static Set<Edge> getOutEdges(Graph graph, Node node, Label label) {
        Set<Edge> outEdges = new HashSet<Edge>();
        for (Edge edge : graph.outEdgeSet(node)) {
            if (edge.label().equals(label)) {
                outEdges.add(edge);
            }
        }
        return outEdges;
    }

    /** EDUARDO */
    public static Set<Edge> getOutEdges(Graph graph, Set<Node> nodes,
            Label label) {
        Set<Edge> outEdges = new HashSet<Edge>();
        for (Node node : nodes) {
            for (Edge edge : graph.outEdgeSet(node)) {
                if (edge.label().equals(label)) {
                    outEdges.add(edge);
                }
            }
        }
        return outEdges;
    }

    /** EDUARDO */
    public static Set<Edge> getInEdges(Graph graph, Node node, Label label) {
        Set<Edge> inEdges = new HashSet<Edge>();
        for (Edge edge : graph.edgeSet(node)) {
            if (edge.opposite().equals(node) && edge.label().equals(label)) {
                inEdges.add(edge);
            }
        }
        return inEdges;
    }

    /** EDUARDO */
    public static Set<Edge> getInEdges(Graph graph, Set<Node> nodes, Label label) {
        Set<Edge> inEdges = new HashSet<Edge>();
        for (Node node : nodes) {
            for (Edge edge : graph.edgeSet(node)) {
                if (edge.opposite().equals(node) && edge.label().equals(label)) {
                    inEdges.add(edge);
                }
            }
        }
        return inEdges;
    }

    /** EDUARDO */
    public static Set<Edge> getIntersectEdges(Graph graph, Node src, Node tgt,
            Label label) {
        Set<Edge> outEdges = getOutEdges(graph, src, label);
        Set<Edge> inEdges = getInEdges(graph, tgt, label);
        return intersection(outEdges, inEdges);
    }

    /** EDUARDO */
    public static Set<Edge> getIntersectEdges(Graph graph, Set<Node> srcs,
            Node tgt, Label label) {
        Set<Edge> outEdges = getOutEdges(graph, srcs, label);
        Set<Edge> inEdges = getInEdges(graph, tgt, label);
        return intersection(outEdges, inEdges);
    }

    /** EDUARDO */
    public static Set<Edge> getIntersectEdges(Graph graph, Node src,
            Set<Node> tgts, Label label) {
        Set<Edge> outEdges = getOutEdges(graph, src, label);
        Set<Edge> inEdges = getInEdges(graph, tgts, label);
        return intersection(outEdges, inEdges);
    }

    /** EDUARDO */
    public static Set<Edge> getIntersectEdges(Graph graph, Set<Node> srcs,
            Set<Node> tgts, Label label) {
        Set<Edge> outEdges = getOutEdges(graph, srcs, label);
        Set<Edge> inEdges = getInEdges(graph, tgts, label);
        return intersection(outEdges, inEdges);
    }

    /** Returns the intersection of two given sets. */
    public static <T> Set<T> intersection(Set<T> s0, Set<T> s2) {
        Set<T> result = new HashSet<T>(Math.min(s0.size(), s2.size()));
        for (T elem : s0) {
            if (s2.contains(elem)) {
                result.add(elem);
            }
        }
        return result;
    }

    /** Returns the label set of the given graph */
    public static Set<Label> labelSet(Graph graph) {
        Set<Label> result = new HashSet<Label>();
        for (Edge edge : graph.edgeSet()) {
            result.add(edge.label());
        }
        return result;
    }

    /** Returns the label set of binary edges of the given graph */
    public static Set<Label> binaryLabelSet(Graph graph) {
        Set<Label> result = new HashSet<Label>();
        for (Edge edge : graph.edgeSet()) {
            if (!isUnary(edge)) {
                result.add(edge.label());
            }
        }
        return result;
    }

    /** EDUARDO */
    public static Set<Node> getReverseNodeMap(NodeEdgeMap map, Node value) {
        return getReverseNodeMap(map.nodeMap(), value);
    }

    /** EDUARDO */
    public static Set<Node> getReverseNodeMap(Map<Node,? extends Node> map,
            Node value) {
        Set<Node> result = new HashSet<Node>();
        if (map.containsValue(value)) {
            for (Entry<Node,? extends Node> entry : map.entrySet()) {
                if (entry.getValue().equals(value)) {
                    result.add(entry.getKey());
                }
            }
        }
        return result;
    }

    /** EDUARDO */
    public static Set<Edge> getReverseEdgeMap(NodeEdgeMap map, Edge value) {
        return getReverseEdgeMap(map.edgeMap(), value);
    }

    /** EDUARDO */
    public static Set<Edge> getReverseEdgeMap(Map<Edge,? extends Edge> map,
            Edge value) {
        Set<Edge> result = new HashSet<Edge>();
        if (map.containsValue(value)) {
            for (Entry<Edge,? extends Edge> entry : map.entrySet()) {
                if (entry.getValue().equals(value)) {
                    result.add(entry.getKey());
                }
            }
        }
        return result;
    }

}
