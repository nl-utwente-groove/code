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
package groove.graph;

import java.util.Set;

import groove.util.Exceptions;

/**
 * Edge direction in a graph.
 * @author Arend Rensink
 * @version $Revision $
 */
public enum Direction {
    /** Outgoing edges. */
    OUTGOING("outgoing"),
    /** Incoming edges. */
    INCOMING("incoming");

    private Direction(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return this.description;
    }

    private final String description;

    /** Returns the origin node of an edge, according to this direction. */
    public <N extends Node,E extends GEdge<N>> N origin(E edge) {
        switch (this) {
        case OUTGOING:
            return edge.source();
        case INCOMING:
            return edge.target();
        default:
            throw Exceptions.UNREACHABLE;
        }
    }

    /** Returns the opposite node of an edge, according to this direction. */
    public <N extends Node,E extends GEdge<N>> N opposite(E edge) {
        switch (this) {
        case OUTGOING:
            return edge.target();
        case INCOMING:
            return edge.source();
        default:
            throw Exceptions.UNREACHABLE;
        }
    }

    /** Returns the set of edges connected to a given node, according to this direction. */
    public <N extends Node,E extends GEdge<N>,G extends GGraph<N,E>> Set<? extends E> edges(G graph,
        N node) {
        switch (this) {
        case OUTGOING:
            return graph.outEdgeSet(node);
        case INCOMING:
            return graph.inEdgeSet(node);
        default:
            assert false;
            return null;
        }
    }

    /** Returns the inverse direction. */
    public Direction getInverse() {
        switch (this) {
        case OUTGOING:
            return INCOMING;
        case INCOMING:
            return OUTGOING;
        default:
            assert false;
            return null;
        }
    }
}
