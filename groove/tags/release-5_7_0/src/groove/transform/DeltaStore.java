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
import groove.grammar.host.HostEdgeSet;
import groove.grammar.host.HostNode;
import groove.grammar.host.HostNodeSet;
import groove.grammar.host.ValueNode;

/**
 * Delta target that collects the addition and removal information and can play
 * it back later, in the role of delta applier.
 * @author Arend Rensink
 * @version $Revision$
 */
public class DeltaStore extends DefaultDeltaApplier implements DeltaTarget {
    /**
     * Created an initially empty delta store.
     */
    public DeltaStore() {
        this(null, null, null, null);
    }

    /**
     * Creates a delta store based on explicitly given added and removed sets.
     * The sets are copied.
     */
    protected DeltaStore(HostNodeSet addedNodeSet, HostNodeSet removedNodeSet,
        HostEdgeSet addedEdgeSet, HostEdgeSet removedEdgeSet) {
        super(addedNodeSet, removedNodeSet, addedEdgeSet, removedEdgeSet);
    }

    /**
     * Creates a delta store based on explicitly given added and removed sets. A
     * further parameter controls if the sets are copied or shared.
     */
    protected DeltaStore(HostNodeSet addedNodeSet, HostNodeSet removedNodeSet,
        HostEdgeSet addedEdgeSet, HostEdgeSet removedEdgeSet, boolean share) {
        super(addedNodeSet, removedNodeSet, addedEdgeSet, removedEdgeSet, share);
    }

    /**
     * Creates a delta store by applying a delta applier to an empty initial
     * store.
     */
    public DeltaStore(DeltaApplier basis) {
        this();
        basis.applyDelta(this);
    }

    @Override
    public boolean addEdge(HostEdge edge) {
        if (!getRemovedEdgeSet().remove(edge)) {
            assert !getAddedEdgeSet().contains(edge) : "Added edge set " + getAddedEdgeSet()
                + " already contains " + edge;
            return getAddedEdgeSet().add(edge);
        } else {
            return true;
        }
    }

    @Override
    public boolean addNode(HostNode node) {
        if (!getRemovedNodeSet().remove(node)) {
            boolean added = getAddedNodeSet().add(node);
            assert added || node instanceof ValueNode : "Added node set " + getAddedNodeSet()
                + " already contains " + node;
            return added;
        } else {
            return true;
        }
    }

    @Override
    public boolean removeEdge(HostEdge edge) {
        if (!getAddedEdgeSet().remove(edge)) {
            // assert !removedEdgeSet.contains(elem) : "Removed edge set "
            // + removedEdgeSet + " already contains " + elem;
            return getRemovedEdgeSet().add(edge);
        } else {
            return true;
        }
    }

    @Override
    public boolean removeNode(HostNode node) {
        if (!getAddedNodeSet().remove(node)) {
            // assert !removedNodeSet.contains(elem) : "Removed node set "
            // + removedNodeSet + " already contains " + elem;
            return getRemovedNodeSet().add(node);
        } else {
            return true;
        }
    }
}