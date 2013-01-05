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
import groove.grammar.host.HostEdge;
import groove.grammar.host.HostGraph;
import groove.grammar.rule.RuleEdge;
import groove.grammar.type.TypeLabel;
import groove.graph.Edge;
import groove.graph.EdgeRole;
import groove.graph.Graph;
import groove.graph.Node;
import groove.util.collect.UnmodifiableSetView;

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
        return new UnmodifiableSetView<HostEdge>(graph.edgeSet()) {
            @Override
            public boolean approves(Object obj) {
                if (!(obj instanceof HostEdge)) {
                    return false;
                }
                HostEdge edge = (HostEdge) obj;
                return edge.getRole() == EdgeRole.BINARY;
            }
        };
    }

    /** Returns the set of labels used as node labels. */
    // EZ says: this method is only used when lifting simple graphs, thus it
    // doesn't impact performance much. It is not a good idea to try to use
    // an UnmodifiableSetView because we need to check for containment in the
    // returned set which would be inefficient.
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

}
