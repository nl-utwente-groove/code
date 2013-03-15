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
package groove.match.rete;

import groove.graph.Edge;
import groove.graph.Node;

/** 
 * Information about a single element in a match,
 * consisting of the index and a role indicator.
 * @author Arend Rensink
 * @version $Revision $
 */
final public class LookupEntry {
    /** Constructs an entry. */
    public LookupEntry(int pos, Role role) {
        this.pos = pos;
        this.role = role;
    }

    /** Returns the position of the entry in the pattern or match array. */
    public int getPos() {
        return this.pos;
    }

    /** Returns the role of the entry. */
    public Role getRole() {
        return this.role;
    }

    /** Retrieves a host node from an array of match units. */
    public Node lookup(Object[] units) {
        return extract(units[this.pos]);
    }

    /** Retrieves a host node from the appropriate place of a match unit. */
    private Node extract(Object unit) {
        Node result = null;
        switch (this.role) {
        case NODE:
            result = (Node) unit;
            break;
        case SOURCE:
            if (unit instanceof Edge) {
                result = ((Edge) unit).source();
            } else if (unit instanceof RetePathMatch) {
                result = ((RetePathMatch) unit).start();
            }
            break;
        case TARGET:
            if (unit instanceof Edge) {
                result = ((Edge) unit).target();
            } else if (unit instanceof RetePathMatch) {
                result = ((RetePathMatch) unit).end();
            }
        }
        return result;
    }

    private final int pos;
    private final Role role;

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
