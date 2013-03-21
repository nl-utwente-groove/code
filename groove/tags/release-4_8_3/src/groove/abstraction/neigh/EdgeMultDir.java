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
package groove.abstraction.neigh;

import groove.abstraction.neigh.shape.ShapeEdge;
import groove.abstraction.neigh.shape.ShapeNode;
import groove.grammar.host.HostEdge;
import groove.grammar.host.HostNode;

/** Enumeration of edge multiplicity directions. */
public enum EdgeMultDir {
    /** Outgoing edge multiplicity. */
    OUTGOING {
        @Override
        public EdgeMultDir reverse() {
            return INCOMING;
        }

        @Override
        public ShapeNode incident(ShapeEdge edge) {
            return edge.source();
        }

        @Override
        public ShapeNode opposite(ShapeEdge edge) {
            return edge.target();
        }

        @Override
        public HostNode incident(HostEdge edge) {
            return edge.source();
        }

        @Override
        public HostNode opposite(HostEdge edge) {
            return edge.target();
        }
    },
    /** Incoming edge multiplicity. */
    INCOMING {
        @Override
        public EdgeMultDir reverse() {
            return OUTGOING;
        }

        @Override
        public ShapeNode incident(ShapeEdge edge) {
            return edge.target();
        }

        @Override
        public ShapeNode opposite(ShapeEdge edge) {
            return edge.source();
        }

        @Override
        public HostNode incident(HostEdge edge) {
            return edge.target();
        }

        @Override
        public HostNode opposite(HostEdge edge) {
            return edge.source();
        }
    };

    /** Returns the reverse direction. */
    abstract public EdgeMultDir reverse();

    /**
     * Returns the incident end of an edge according to this direction.
     * @return the edge target if this is {@link #INCOMING},
     *         the source if this is {@link #OUTGOING}
     */
    abstract public ShapeNode incident(ShapeEdge edge);

    /**
     * Returns the opposite end of an edge according to this direction.
     * @return the edge source if this is {@link #INCOMING},
     *         the target if this is {@link #OUTGOING}
     */
    abstract public ShapeNode opposite(ShapeEdge edge);

    /**
     * Returns the incident end of an edge according to this direction.
     * @return the edge target if this is {@link #INCOMING},
     *         the source if this is {@link #OUTGOING}
     */
    abstract public HostNode incident(HostEdge edge);

    /**
     * Returns the opposite end of an edge according to this direction.
     * @return the edge source if this is {@link #INCOMING},
     *         the target if this is {@link #OUTGOING}
     */
    abstract public HostNode opposite(HostEdge edge);
}
