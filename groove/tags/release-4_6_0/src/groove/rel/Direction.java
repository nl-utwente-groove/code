/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.rel;

import groove.graph.AbstractEdge;
import groove.graph.AbstractGraph;
import groove.graph.Edge;
import groove.graph.Label;
import groove.graph.Node;
import groove.trans.HostEdge;
import groove.trans.HostGraph;
import groove.trans.HostNode;

import java.util.Set;

/**
 * Exploration direction in a finite automaton.
 * @author Arend Rensink
 * @version $Revision $
 */
public enum Direction {
    /** Explore from start state to final state. */
    FORWARD,
    /** Explore from final state to start state. */
    BACKWARD;

    /** Returns the origin node of an edge, according to this direction. */
    public <N extends Node,L extends Label,E extends AbstractEdge<N,L>> N origin(
            E edge) {
        switch (this) {
        case FORWARD:
            return edge.source();
        case BACKWARD:
            return edge.target();
        default:
            assert false;
            return null;
        }
    }

    /** Returns the opposite node of an edge, according to this direction. */
    public <N extends Node,L extends Label,E extends AbstractEdge<N,L>> N opposite(
            E edge) {
        switch (this) {
        case FORWARD:
            return edge.target();
        case BACKWARD:
            return edge.source();
        default:
            assert false;
            return null;
        }
    }

    /** Returns the set of edges connected to a given node, according to this direction. */
    public <N extends Node,E extends Edge,G extends AbstractGraph<N,E>> Set<? extends E> edges(
            G graph, N node) {
        switch (this) {
        case FORWARD:
            return graph.outEdgeSet(node);
        case BACKWARD:
            return graph.inEdgeSet(node);
        default:
            assert false;
            return null;
        }
    }

    /** Returns the set of edges connected to a given host node, according to this direction. */
    public Set<? extends HostEdge> edges(HostGraph graph, HostNode node) {
        switch (this) {
        case FORWARD:
            return graph.outEdgeSet(node);
        case BACKWARD:
            return graph.inEdgeSet(node);
        default:
            assert false;
            return null;
        }
    }

    /** Returns the inverse direction. */
    public Direction getInverse() {
        switch (this) {
        case FORWARD:
            return BACKWARD;
        case BACKWARD:
            return FORWARD;
        default:
            assert false;
            return null;
        }
    }
}
