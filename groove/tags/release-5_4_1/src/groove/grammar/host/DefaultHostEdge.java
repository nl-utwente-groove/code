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
package groove.grammar.host;

import groove.grammar.AnchorKind;
import groove.grammar.type.TypeEdge;
import groove.grammar.type.TypeLabel;
import groove.graph.AEdge;

/**
 * Class that implements the edges of a host graph.
 * @author Arend Rensink
 */
public class DefaultHostEdge extends AEdge<HostNode,TypeLabel> implements HostEdge {
    /** Constructor for a typed edge. */
    protected DefaultHostEdge(HostNode source, TypeEdge type, HostNode target, int nr) {
        super(source, type.label(), target, nr);
        this.type = type;
        assert type != null;
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public boolean isSimple() {
        return true;
    }

    @Override
    protected boolean isTypeEqual(Object obj) {
        return obj instanceof DefaultHostEdge;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        DefaultHostEdge other = (DefaultHostEdge) obj;
        if (getType() != other.getType()) {
            return false;
        }
        return true;
    }

    @Override
    public TypeEdge getType() {
        return this.type;
    }

    @Override
    public AnchorKind getAnchorKind() {
        return AnchorKind.EDGE;
    }

    /** Non-{@code null} type of this edge. */
    private final TypeEdge type;
}
