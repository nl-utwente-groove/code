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

import java.util.EnumSet;
import java.util.Set;

/**
 * Exploration direction in a finite automaton.
 * @author Arend Rensink
 * @version $Revision $
 */
enum Direction {
    /** Explore from start state to final state. */
    FORWARD,
    /** Explore from final state to start state. */
    BACKWARD;

    /** Returns the start node of an edge, according to this direction. */
    public RegNode start(RegEdge edge) {
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

    /** Returns the end node of an edge, according to this direction. */
    public RegNode end(RegEdge edge) {
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

    /** The set of all direction values. */
    public static final Set<Direction> all = EnumSet.allOf(Direction.class);
}
