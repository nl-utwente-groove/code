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
package groove.trans;

import groove.graph.AbstractEdge;
import groove.graph.EdgeRole;
import groove.graph.TypeEdge;
import groove.graph.TypeLabel;

/**
 * Class that implements the edges of a host graph.
 * @author Arend Rensink
 */
public class HostEdge extends AbstractEdge<HostNode,TypeLabel> implements
        HostElement, AnchorValue {
    /** Constructor for a typed edge. */
    protected HostEdge(HostNode source, TypeEdge type, HostNode target, int nr) {
        super(source, type.label(), target);
        assert label().getRole() == EdgeRole.BINARY || source == target : String.format(
            "Can't create %s label %s between distinct nodes %s and %s",
            label().getRole().getDescription(false), label(), source, target);
        this.nr = nr;
        this.type = type;
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    /** Returns true if the edge is a loop. */
    public boolean isLoop() {
        return this.source().equals(this.target());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof HostEdge)) {
            return false;
        }
        HostEdge other = (HostEdge) obj;
        if (getType() != other.getType()) {
            return false;
        }
        if (getNumber() != other.getNumber()) {
            return false;
        }
        return source().equals(other.source())
            && target().equals(other.target());
    }

    /** 
     * Returns the number of this edge.
     * The number is guaranteed to be unique for each canonical edge representative.
     */
    public int getNumber() {
        return this.nr;
    }

    /** 
     * Returns the (possibly {@code null}) type of this edge.
     * The number is guaranteed to be unique for each canonical edge representative.
     */
    public TypeEdge getType() {
        return this.type;
    }

    @Override
    public AnchorKind getAnchorKind() {
        return AnchorKind.EDGE;
    }

    /** The (unique) number of this edge. */
    private final int nr;
    /** Possibly {@code null} type of this edge. */
    private final TypeEdge type;
}
