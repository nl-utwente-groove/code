/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.match.rete;

import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.Node;

/**
 * Information about a single element in a match,
 * consisting of the index and a role indicator.
 * @author Arend Rensink
 * @version $Revision$
 */
final public record LookupEntry(int pos, nl.utwente.groove.match.rete.LookupEntry.Role role) {

    /** Retrieves a host node from an array of match units. */
    public Node lookup(Object[] units) {
        return extract(units[this.pos]);
    }

    /** Retrieves a host node from the appropriate place of a match unit. */
    private Node extract(Object unit) {
        return switch (this.role) {
        case NODE -> (Node) unit;
        case SOURCE -> unit instanceof Edge e ? e.source()
            : unit instanceof RetePathMatch m ? m.start() : null;
        case TARGET -> unit instanceof Edge e ? e.target()
            : unit instanceof RetePathMatch m ? m.end() : null;
        };
    }

    /** Role of the entry. */
    public static enum Role {
        /** Node information. */
        NODE,
        /** The source node of an edge. */
        SOURCE,
        /** The target node of an edge. */
        TARGET;
    }
}
