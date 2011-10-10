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
package groove.abstraction.neigh;

import static groove.graph.EdgeRole.BINARY;
import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.shape.ShapeEdge;
import groove.graph.Edge;
import groove.graph.EdgeRole;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.TypeLabel;
import groove.trans.HostEdge;
import groove.trans.HostGraph;
import groove.trans.HostNode;

import java.util.Set;

/**
 * This class is only a collection of utility methods for abstraction and
 * therefore should not be instantiated.
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
        Set<TypeLabel> nodeLabels = new MyHashSet<TypeLabel>();
        for (HostEdge edge : graph.outEdgeSet(node)) {
            if (edge.getRole() != BINARY) {
                nodeLabels.add(edge.label());
            }
        }
        return nodeLabels;
    }

    /** Returns the label set of binary edges of the given graph */
    public static Set<TypeLabel> getBinaryLabels(HostGraph graph) {
        Set<TypeLabel> result = new MyHashSet<TypeLabel>();
        for (HostEdge edge : graph.edgeSet()) {
            if (edge.getRole() == BINARY) {
                result.add(edge.label());
            }
        }
        return result;
    }

    /** Returns the set of binary edges of the given graph. */
    @SuppressWarnings("unchecked")
    public static <E extends HostEdge> Set<E> getBinaryEdges(HostGraph graph) {
        Set<E> result = new MyHashSet<E>();
        for (HostEdge edge : graph.edgeSet()) {
            if (edge.getRole() == EdgeRole.BINARY) {
                result.add((E) edge);
            }
        }
        return result;
    }

    /** Returns the set of binary edges of the given shape. */
    public static Set<ShapeEdge> getBinaryEdges(Shape shape) {
        return Util.<ShapeEdge>getBinaryEdges((HostGraph) shape);
    }

    /** Returns the set of edges between the given nodes. See Def. 1, pg. 6. */
    @SuppressWarnings("unchecked")
    public static <N extends Node,E extends Edge> Set<E> getIntersectEdges(
            Graph<?,?> graph, N src, N tgt, Label label) {
        Set<E> result = new MyHashSet<E>();
        for (Edge outEdge : graph.outEdgeSet(src)) {
            if (outEdge.label().equals(label) && outEdge.target().equals(tgt)) {
                result.add((E) outEdge);
            }
        }
        return result;
    }

    /** Returns the set of edges between the given nodes. See Def. 1, pg. 6. */
    @SuppressWarnings("unchecked")
    public static <N extends Node,E extends Edge> Set<E> getIntersectEdges(
            Graph<?,?> graph, Set<N> srcs, N tgt, Label label) {
        Set<E> result = new MyHashSet<E>();
        for (Edge inEdge : graph.inEdgeSet(tgt)) {
            if (inEdge.label().equals(label) && srcs.contains(inEdge.source())) {
                result.add((E) inEdge);
            }
        }
        return result;
    }

    /** Returns the set of edges between the given nodes. See Def. 1, pg. 6. */
    @SuppressWarnings("unchecked")
    public static <N extends Node,E extends Edge> Set<E> getIntersectEdges(
            Graph<?,?> graph, N src, Set<N> tgts, Label label) {
        Set<E> result = new MyHashSet<E>();
        for (Edge outEdge : graph.outEdgeSet(src)) {
            if (outEdge.label().equals(label)
                && tgts.contains(outEdge.target())) {
                result.add((E) outEdge);
            }
        }
        return result;
    }

}
