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

import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.TypeLabel;
import groove.trans.HostEdge;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.trans.RuleEdge;
import groove.trans.RuleNode;
import groove.trans.RuleToHostMap;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * This class is only a collection of utility methods for abstraction and
 * therefore should not be instantiated.
 * Stupid packaging system of Java... >:(
 * 
 * @author Eduardo Zambon 
 */
public final class Util {

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    private Util() {
        // We make the constructor private to prevent the creation of objects
        // of this class.
    }

    // ------------------------------------------------------------------------
    // Static methods
    // ------------------------------------------------------------------------

    /** Returns the set of labels used as node labels. */
    public static Set<TypeLabel> getNodeLabels(HostGraph graph, HostNode node) {
        HashSet<TypeLabel> nodeLabels = new HashSet<TypeLabel>();
        for (HostEdge edge : graph.outEdgeSet(node)) {
            if (isUnary(edge)) {
                nodeLabels.add(edge.label());
            }
        }
        return nodeLabels;
    }

    /** Returns the set of binary edges of the graph. */
    public static Set<HostEdge> getBinaryEdges(HostGraph graph) {
        HashSet<HostEdge> edges = new HashSet<HostEdge>();
        for (HostEdge edge : graph.edgeSet()) {
            if (!isUnary(edge)) {
                edges.add(edge);
            }
        }
        return edges;
    }

    /** Returns true if the given edge is unary. */
    public static boolean isUnary(Edge edge) {
        boolean result = false;
        Label label = edge.label();
        result = !label.isBinary();
        if (!result) {
            // It may be the case that binary edges are still used
            // in a plain graph...
            result =
                label.text().startsWith("type:")
                    || label.text().startsWith("flag:");
        }
        return result;
    }

    /** 
     * Returns the set of outgoing edges from the given node with the
     * given label.
     */
    @SuppressWarnings("unchecked")
    public static <N extends Node,E extends Edge> Set<E> getOutEdges(
            Graph graph, N node, Label label) {
        Set<E> outEdges = new HashSet<E>();
        for (Edge edge : graph.outEdgeSet(node)) {
            if (edge.label().equals(label)) {
                outEdges.add((E) edge);
            }
        }
        return outEdges;
    }

    /** 
     * Returns the set of outgoing edges from the given nodes with the
     * given label.
     */
    @SuppressWarnings("unchecked")
    public static <N extends Node,E extends Edge> Set<E> getOutEdges(
            Graph graph, Set<N> nodes, Label label) {
        Set<E> outEdges = new HashSet<E>();
        for (N node : nodes) {
            for (Edge edge : graph.outEdgeSet(node)) {
                if (edge.label().equals(label)) {
                    outEdges.add((E) edge);
                }
            }
        }
        return outEdges;
    }

    /** 
     * Returns the set of incoming edges to the given node with the
     * given label.
     */
    @SuppressWarnings("unchecked")
    public static <N extends Node,E extends Edge> Set<E> getInEdges(
            Graph graph, N node, Label label) {
        Set<E> inEdges = new HashSet<E>();
        for (Edge edge : graph.edgeSet(node)) {
            if (edge.target().equals(node) && edge.label().equals(label)) {
                inEdges.add((E) edge);
            }
        }
        return inEdges;
    }

    /** 
     * Returns the set of incoming edges to the given nodes with the
     * given label.
     */
    @SuppressWarnings("unchecked")
    public static <N extends Node,E extends Edge> Set<E> getInEdges(
            Graph graph, Set<N> nodes, Label label) {
        Set<E> inEdges = new HashSet<E>();
        for (N node : nodes) {
            for (Edge edge : graph.edgeSet(node)) {
                if (edge.target().equals(node) && edge.label().equals(label)) {
                    inEdges.add((E) edge);
                }
            }
        }
        return inEdges;
    }

    /** Returns the set of edges between the given nodes. See Def. 1, pg. 6. */
    public static <N extends Node,E extends Edge> Set<E> getIntersectEdges(
            Graph graph, N src, N tgt, Label label) {
        Set<E> outEdges = getOutEdges(graph, src, label);
        Set<E> inEdges = getInEdges(graph, tgt, label);
        return intersection(outEdges, inEdges);
    }

    /** Returns the set of edges between the given nodes. See Def. 1, pg. 6. */
    public static <N extends Node,E extends Edge> Set<E> getIntersectEdges(
            Graph graph, Set<N> srcs, N tgt, Label label) {
        Set<E> outEdges = getOutEdges(graph, srcs, label);
        Set<E> inEdges = getInEdges(graph, tgt, label);
        return intersection(outEdges, inEdges);
    }

    /** Returns the set of edges between the given nodes. See Def. 1, pg. 6. */
    public static <N extends Node,E extends Edge> Set<E> getIntersectEdges(
            Graph graph, N src, Set<N> tgts, Label label) {
        Set<E> outEdges = getOutEdges(graph, src, label);
        Set<E> inEdges = getInEdges(graph, tgts, label);
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

    /** Returns the label set of binary edges of the given graph */
    public static Set<TypeLabel> binaryLabelSet(HostGraph graph) {
        Set<TypeLabel> result = new HashSet<TypeLabel>();
        for (HostEdge edge : graph.edgeSet()) {
            if (!isUnary(edge)) {
                result.add(edge.label());
            }
        }
        return result;
    }

    /** Performs a reverse lookup in the node map given. */
    public static Set<RuleNode> getReverseNodeMap(RuleToHostMap map,
            HostNode value) {
        return getReverseNodeMap(map.nodeMap(), value);
    }

    /** Performs a reverse lookup in the node map given. */
    public static <N extends Node> Set<N> getReverseNodeMap(
            Map<N,HostNode> map, HostNode value) {
        Set<N> result = new HashSet<N>();
        if (map.containsValue(value)) {
            for (Entry<N,? extends Node> entry : map.entrySet()) {
                if (entry.getValue().equals(value)) {
                    result.add(entry.getKey());
                }
            }
        }
        return result;
    }

    /** Performs a reverse lookup in the edge map given. */
    public static Set<RuleEdge> getReverseEdgeMap(RuleToHostMap map,
            HostEdge value) {
        return getReverseEdgeMap(map.edgeMap(), value);
    }

    /** Performs a reverse lookup in the edge map given. */
    public static Set<RuleEdge> getReverseEdgeMap(Map<RuleEdge,HostEdge> map,
            HostEdge value) {
        Set<RuleEdge> result = new HashSet<RuleEdge>();
        if (map.containsValue(value)) {
            for (Entry<RuleEdge,? extends Edge> entry : map.entrySet()) {
                if (entry.getValue().equals(value)) {
                    result.add(entry.getKey());
                }
            }
        }
        return result;
    }

}
