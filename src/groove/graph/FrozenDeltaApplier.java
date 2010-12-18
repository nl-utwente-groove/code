/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: FrozenDeltaApplier.java,v 1.3 2008-01-30 09:32:51 iovka Exp $
 */
package groove.graph;

import groove.trans.HostEdge;
import groove.trans.HostNode;

/**
 * Delta applier constructed from a frozen delta array. A frozen delta array is
 * an array of nodes and edges that together constitute an entire graph.
 * Applying the delta adds the nodes and edges in the order specified by the
 * array.
 * @author Arend Rensink
 * @version $Revision $
 */
public class FrozenDeltaApplier implements DeltaApplier {
    /** Constructs an instance with a given array of elements. */
    public FrozenDeltaApplier(Element[] elements) {
        this.elements = elements;
    }

    public void applyDelta(DeltaTarget target, int mode) {
        for (Element elem : this.elements) {
            if (elem instanceof HostNode && mode != EDGES_ONLY) {
                target.addNode((HostNode) elem);
            } else {
                assert (elem instanceof HostEdge && mode != NODES_ONLY);
                target.addEdge((HostEdge) elem);
            }
        }
    }

    public void applyDelta(DeltaTarget target) {
        applyDelta(target, ALL_ELEMENTS);
    }

    /** The frozen array of graph elements. */
    private final Element[] elements;
}
