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

import java.util.HashSet;
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
            if (!edge.isBinary()) {
                nodeLabels.add(edge.label());
            }
        }
        return nodeLabels;
    }

    /** Returns the label set of binary edges of the given graph */
    public static Set<TypeLabel> getBinaryLabels(HostGraph graph) {
        Set<TypeLabel> result = new HashSet<TypeLabel>();
        for (HostEdge edge : graph.edgeSet()) {
            if (edge.isBinary()) {
                result.add(edge.label());
            }
        }
        return result;
    }

    /** Returns the set of edges between the given nodes. See Def. 1, pg. 6. */
    public static <N extends Node,E extends Edge> Set<E> getIntersectEdges(
            Graph<N,?,E> graph, N src, N tgt, Label label) {
        Set<E> result = new HashSet<E>();
        for (E outEdge : graph.outEdgeSet(src)) {
            if (outEdge.label().equals(label) && outEdge.target().equals(tgt)) {
                result.add(outEdge);
            }
        }
        return result;
    }

    /** Returns the set of edges between the given nodes. See Def. 1, pg. 6. */
    public static <N extends Node,E extends Edge> Set<E> getIntersectEdges(
            Graph<N,?,E> graph, Set<N> srcs, N tgt, Label label) {
        Set<E> result = new HashSet<E>();
        for (E inEdge : graph.inEdgeSet(tgt)) {
            if (inEdge.label().equals(label) && srcs.contains(inEdge.source())) {
                result.add(inEdge);
            }
        }
        return result;
    }

    /** Returns the set of edges between the given nodes. See Def. 1, pg. 6. */
    public static <N extends Node,E extends Edge> Set<E> getIntersectEdges(
            Graph<N,?,E> graph, N src, Set<N> tgts, Label label) {
        Set<E> result = new HashSet<E>();
        for (E outEdge : graph.outEdgeSet(src)) {
            if (outEdge.label().equals(label)
                && tgts.contains(outEdge.target())) {
                result.add(outEdge);
            }
        }
        return result;
    }
}
