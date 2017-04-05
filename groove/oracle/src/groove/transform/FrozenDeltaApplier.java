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
 * $Id$
 */
package groove.transform;

import groove.grammar.host.HostEdge;
import groove.grammar.host.HostElement;
import groove.grammar.host.HostNode;

/**
 * Delta applier constructed from a frozen delta array. A frozen delta array is
 * an array of nodes and edges that together constitute an entire graph.
 * Applying the delta adds the nodes and edges in the order specified by the
 * array.
 * @author Arend Rensink
 * @version $Revision $
 */
public class FrozenDeltaApplier implements StoredDeltaApplier {
    /** Constructs an instance with a given array of elements. */
    public FrozenDeltaApplier(HostElement[] elements) {
        this.elements = elements;
    }

    @Override
    public void applyDelta(DeltaTarget target) {
        for (HostElement elem : this.elements) {
            if (elem instanceof HostNode) {
                target.addNode((HostNode) elem);
            } else {
                assert (elem instanceof HostEdge);
                target.addEdge((HostEdge) elem);
            }
        }
    }

    @Override
    public int size() {
        return this.elements.length;
    }

    /** The frozen array of graph elements. */
    private final HostElement[] elements;
}
