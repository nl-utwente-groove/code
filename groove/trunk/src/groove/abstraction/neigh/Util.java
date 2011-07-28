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
import gnu.trove.THashSet;
import groove.abstraction.neigh.equiv.EquivClass;
import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.shape.ShapeEdge;
import groove.graph.EdgeRole;
import groove.graph.TypeLabel;
import groove.trans.HostEdge;
import groove.trans.HostGraph;
import groove.trans.HostNode;

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
    public static THashSet<TypeLabel> getNodeLabels(HostGraph graph,
            HostNode node) {
        THashSet<TypeLabel> nodeLabels = new THashSet<TypeLabel>();
        for (HostEdge edge : graph.outEdgeSet(node)) {
            if (edge.getRole() != BINARY) {
                nodeLabels.add(edge.label());
            }
        }
        return nodeLabels;
    }

    /** Returns the label set of binary edges of the given graph */
    public static THashSet<TypeLabel> getBinaryLabels(HostGraph graph) {
        THashSet<TypeLabel> result = new THashSet<TypeLabel>();
        for (HostEdge edge : graph.edgeSet()) {
            if (edge.getRole() == BINARY) {
                result.add(edge.label());
            }
        }
        return result;
    }

    /** Returns the set of binary edges of the given graph. */
    @SuppressWarnings("unchecked")
    public static <E extends HostEdge> THashSet<E> getBinaryEdges(
            HostGraph graph) {
        THashSet<E> result = new THashSet<E>();
        for (HostEdge edge : graph.edgeSet()) {
            if (edge.getRole() == EdgeRole.BINARY) {
                result.add((E) edge);
            }
        }
        return result;
    }

    /** Returns the set of binary edges of the given shape. */
    public static THashSet<ShapeEdge> getBinaryEdges(Shape shape) {
        return Util.<ShapeEdge>getBinaryEdges((HostGraph) shape);
    }

    /** Returns the set of edges between the given nodes. See Def. 1, pg. 6. */
    public static THashSet<HostEdge> getIntersectEdges(HostGraph graph,
            HostNode src, HostNode tgt, TypeLabel label) {
        THashSet<HostEdge> result = new THashSet<HostEdge>();
        for (HostEdge outEdge : graph.outEdgeSet(src)) {
            if (outEdge.label().equals(label) && outEdge.target().equals(tgt)) {
                result.add(outEdge);
            }
        }
        return result;
    }

    /** Returns the set of edges between the given nodes. See Def. 1, pg. 6. */
    public static THashSet<HostEdge> getIntersectEdges(HostGraph graph,
            EquivClass<? extends HostNode> srcs, HostNode tgt, TypeLabel label) {
        THashSet<HostEdge> result = new THashSet<HostEdge>();
        for (HostEdge inEdge : graph.inEdgeSet(tgt)) {
            if (inEdge.label().equals(label) && srcs.contains(inEdge.source())) {
                result.add(inEdge);
            }
        }
        return result;
    }

    /** Returns the set of edges between the given nodes. See Def. 1, pg. 6. */
    public static THashSet<HostEdge> getIntersectEdges(HostGraph graph,
            HostNode src, EquivClass<? extends HostNode> tgts, TypeLabel label) {
        THashSet<HostEdge> result = new THashSet<HostEdge>();
        for (HostEdge outEdge : graph.outEdgeSet(src)) {
            if (outEdge.label().equals(label)
                && tgts.contains(outEdge.target())) {
                result.add(outEdge);
            }
        }
        return result;
    }

}
