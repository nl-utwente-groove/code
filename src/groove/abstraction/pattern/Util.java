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
package groove.abstraction.pattern;

import static groove.graph.EdgeRole.BINARY;
import groove.abstraction.MyHashSet;
import groove.graph.Edge;
import groove.graph.EdgeRole;
import groove.graph.Graph;
import groove.graph.Node;
import groove.graph.TypeLabel;
import groove.trans.HostEdge;
import groove.trans.HostGraph;
import groove.trans.RuleEdge;

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
        // We make the constructor private to prevent creation of objects
        // of this class.
    }

    // ------------------------------------------------------------------------
    // Static methods
    // ------------------------------------------------------------------------

    /** Returns the number of binary edges of the given graph. */
    public static int getBinaryEdgesCount(HostGraph graph) {
        int result = 0;
        for (HostEdge edge : graph.edgeSet()) {
            if (edge.getRole() == EdgeRole.BINARY) {
                result++;
            }
        }
        return result;
    }

    /** Returns the set of binary edges of the given graph. */
    public static Set<HostEdge> getBinaryEdges(HostGraph graph) {
        Set<HostEdge> result = new MyHashSet<HostEdge>();
        for (HostEdge edge : graph.edgeSet()) {
            if (edge.getRole() == EdgeRole.BINARY) {
                result.add(edge);
            }
        }
        return result;
    }

    /** Returns the set of labels used as node labels. */
    public static Set<TypeLabel> getNodeLabels(Graph<?,?> graph, Node node) {
        Set<TypeLabel> nodeLabels = new MyHashSet<TypeLabel>();
        for (Edge edge : graph.edgeSet(node)) {
            if (edge.getRole() != BINARY) {
                if (edge instanceof HostEdge) {
                    nodeLabels.add(((HostEdge) edge).label());
                } else if (edge instanceof RuleEdge) {
                    nodeLabels.add(((RuleEdge) edge).getType().label());
                } else {
                    assert false;
                }
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

}
