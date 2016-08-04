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
package groove.io.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A node tuple of an attributed graph.
 * This is essentially an ordered sequence of nodes, modelling a node relation.
 * @author Arend Rensink
 * @version $Revision $
 */
public class AttrTuple {
    /**
     * Constructs a new tuple from a collection of nodes.
     * The nodes in the tuple are stored in iteration order.
     */
    public AttrTuple(Collection<AttrNode> nodes) {
        this.nodes = new ArrayList<>(nodes);
    }

    /** Returns an unmodifiable view on nodes in this tuple. */
    public List<AttrNode> getNodes() {
        return Collections.unmodifiableList(this.nodes);
    }

    @Override
    public int hashCode() {
        return this.nodes.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AttrTuple other = (AttrTuple) obj;
        return this.nodes.equals(other.nodes);
    }

    @Override
    public String toString() {
        return this.nodes.toString();
    }

    private final List<AttrNode> nodes;
}
