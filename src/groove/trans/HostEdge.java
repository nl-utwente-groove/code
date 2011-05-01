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
public class HostEdge extends AbstractEdge<HostNode,TypeLabel> implements
        HostElement {
    /** Default constructor. */
    protected HostEdge(HostFactory factory, HostNode source, TypeLabel label,
            HostNode target, int nr) {
        super(source, label, target);
        assert source.equals(target) || label.isBinary() : String.format(
            "Can't create %s label %s between distinct nodes %s and %s",
            label.getRole().getDescription(false), label, source, target);
        this.factory = factory;
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
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof HostEdge)) {
            return false;
        }
        HostEdge other = (HostEdge) obj;
        if (this.factory == other.factory) {
            // the objects can now only be equal if they are identical
            assert getNumber() != other.getNumber();
            return false;
        }
        if (!other.source().equals(source())) {
            return false;
        }
        if (!other.target().equals(target())) {
            return false;
        }
        if (!other.label().equals(label())) {
            return false;
        }
        return true;
        //        boolean result = this == obj;
        //        // test that the result is the same as number equality
        //        // or source-label-target equality
        //        assert result == (obj instanceof HostEdge && this.nr == ((HostEdge) obj).nr) : String.format(
        //            "Distinct %s and %s %s with the same number %d",
        //            getClass().getName(), obj.getClass().getName(), this, this.nr);
        //        assert result == (obj instanceof HostEdge && super.equals(obj)) : String.format(
        //            "Distinct %s and %s %s with the same content",
        //            getClass().getName(), obj.getClass().getName(), this);
        //        return result;
    }

    /** 
     * Returns the number of this edge.
     * The number is guaranteed to be unique for each canonical edge representative.
     */
    public int getNumber() {
        return this.nr;
    }

    /** The factory that created this edge. */
    private final HostFactory factory;
    /** The (unique) number of this edge. */
    private final int nr;
}
