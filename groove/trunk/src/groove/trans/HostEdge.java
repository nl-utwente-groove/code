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
import groove.graph.TypeLabel;

/**
 * Class that implements the edges of a host graph.
 * @author Arend Rensink
 */
public class HostEdge extends AbstractEdge<HostNode,TypeLabel,HostNode>
        implements HostElement {
    /** Default constructor. */
    protected HostEdge(HostNode source, TypeLabel label, HostNode target, int nr) {
        super(source, label, target);
        this.nr = nr;
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
        boolean result = this == obj;
        // test that the result is the same as number equality
        // or source-label-target equality
        assert result == (obj instanceof HostEdge && this.nr == ((HostEdge) obj).nr) : String.format(
            "Distinct %s and %s %s with the same number %d",
            getClass().getName(), obj.getClass().getName(), this, this.nr);
        assert result == (obj instanceof HostEdge && super.equals(obj)) : String.format(
            "Distinct %s and %s %s with the same content",
            getClass().getName(), obj.getClass().getName(), this);
        return result;
    }

    /** 
     * Returns the number of this edge.
     * The number is guaranteed to be unique for each canonical edge representative.
     */
    public int getNumber() {
        return this.nr;
    }

    /** The (unique) number of this edge. */
    private final int nr;
}
