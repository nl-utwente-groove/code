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
 * $Id: DefaultDeltaApplier.java,v 1.7 2008-01-30 09:32:58 iovka Exp $
 */
package groove.transform;

import groove.grammar.host.HostEdge;
import groove.grammar.host.HostEdgeSet;
import groove.grammar.host.HostElement;
import groove.grammar.host.HostNode;
import groove.grammar.host.HostNodeSet;
import groove.util.collect.DeltaSet;
import groove.util.collect.StackedSet;

import java.util.Collection;
import java.util.Set;

/**
 * Default implementation of a delta applier.
 * @author Arend Rensink
 * @version $Revision$
 */
public class DefaultDeltaApplier implements StoredDeltaApplier {
    /**
     * Creates a delta store based on explicitly given added and removed sets.
     * The sets are copied.
     */
    protected DefaultDeltaApplier(Set<HostNode> addedNodeSet,
            Set<HostNode> removedNodeSet, Set<HostEdge> addedEdgeSet,
            Set<HostEdge> removedEdgeSet) {
        this.addedNodeSet = createNodeSet(addedNodeSet);
        this.removedNodeSet = createNodeSet(removedNodeSet);
        this.addedEdgeSet = createEdgeSet(addedEdgeSet);
        this.removedEdgeSet = createEdgeSet(removedEdgeSet);
    }

    /**
     * Creates a delta store based on explicitly given added and removed sets. A
     * further parameter controls if the sets are copied or shared.
     */
    protected DefaultDeltaApplier(Set<HostNode> addedNodeSet,
            Set<HostNode> removedNodeSet, Set<HostEdge> addedEdgeSet,
            Set<HostEdge> removedEdgeSet, boolean share) {
        this.addedNodeSet = share ? addedNodeSet : createNodeSet(addedNodeSet);
        this.removedNodeSet =
            share ? removedNodeSet : createNodeSet(removedNodeSet);
        this.addedEdgeSet = share ? addedEdgeSet : createEdgeSet(addedEdgeSet);
        this.removedEdgeSet =
            share ? removedEdgeSet : createEdgeSet(removedEdgeSet);
    }

    /**
     * Processes the delta based on the cached information.
     */
    public void applyDelta(DeltaTarget target, int mode) {
        // process the added and removed sets
        if (mode != EDGES_ONLY) {
            for (HostNode addedNode : this.addedNodeSet) {
                target.addNode(addedNode);
            }
        }
        if (mode != NODES_ONLY) {
            for (HostEdge addedEdge : this.addedEdgeSet) {
                target.addEdge(addedEdge);
            }
            for (HostEdge removedEdge : this.removedEdgeSet) {
                target.removeEdge(removedEdge);
            }
        }
        // remove nodes only after the edges
        if (mode != EDGES_ONLY) {
            for (HostNode removedNode : this.removedNodeSet) {
                target.removeNode(removedNode);
            }
        }
    }

    public void applyDelta(DeltaTarget target) {
        applyDelta(target, ALL_ELEMENTS);
    }

    /** Returns an alias of the set of added nodes of this delta. */
    public Set<HostNode> getAddedNodeSet() {
        return this.addedNodeSet;
    }

    /** Returns an alias of the set of removed nodes of this delta. */
    public Set<HostNode> getRemovedNodeSet() {
        return this.removedNodeSet;
    }

    /** Returns an alias of the set of added edges of this delta. */
    public Set<HostEdge> getAddedEdgeSet() {
        return this.addedEdgeSet;
    }

    /** Returns an alias of the set of removed edges of this delta. */
    public Set<HostEdge> getRemovedEdgeSet() {
        return this.removedEdgeSet;
    }

    /**
     * Swaps the added and removed node sets, so that the delta represents the
     * inverse of what it did before.
     */
    public DeltaStore invert() {
        return invert(false);
    }

    /**
     * Swaps the added and removed node sets, so that the delta represents the
     * inverse of what it did before. A further parameter controls if the sets
     * are copied or shared.
     */
    public DeltaStore invert(boolean share) {
        return new DeltaStore(this.removedNodeSet, this.addedNodeSet,
            this.removedEdgeSet, this.addedEdgeSet, share);
    }

    /**
     * Returns the sum of the sizes of the added node and edge sets.
     */
    public int addedSize() {
        return this.addedNodeSet.size() + this.addedEdgeSet.size();
    }

    /**
     * Returns the sum of the sizes of the removed node and edge sets.
     */
    public int removedSize() {
        return this.removedNodeSet.size() + this.removedEdgeSet.size();
    }

    /**
     * Returns the sum of the sizes of all added and removed node and edge sets.
     */
    @Override
    public int size() {
        return addedSize() + removedSize();
    }

    /**
     * Callback factory method for copying a given node set. Returns the empty
     * set if the given node set is <code>null</code>.
     */
    protected Set<HostNode> createNodeSet(Collection<HostNode> set) {
        if (set == null) {
            return new HostNodeSet();
        } else {
            return new HostNodeSet(set);
        }
    }

    /**
     * Callback factory method for copying a given edge set. Returns the empty
     * set if the given edge set is <code>null</code>.
     */
    protected Set<HostEdge> createEdgeSet(Collection<HostEdge> set) {
        Set<HostEdge> result = new HostEdgeSet();
        if (set != null) {
            result.addAll(set);
        }
        return result;
    }

    /**
     * Callback factory method for creating a new delta set.
     */
    protected <E extends HostElement> DeltaSet<E> createDeltaSet(Set<E> lower,
            Set<E> added, Set<E> removed) {
        return new DeltaSet<E>(lower, added, removed);
    }

    /**
     * Callback factory method for creating a new stacked set.
     */
    protected <E extends HostElement> StackedSet<E> createStackedSet(
            Set<? extends E> lower, Set<E> added, Set<E> removed) {
        return new StackedSet<E>(lower, added, removed);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        if (!this.addedNodeSet.isEmpty()) {
            result.append('+');
            result.append(this.addedNodeSet);
            result.append(' ');
        }
        if (!this.removedNodeSet.isEmpty()) {
            result.append('-');
            result.append(this.removedNodeSet);
            result.append(' ');
        }
        if (!this.addedEdgeSet.isEmpty()) {
            result.append('+');
            result.append(this.addedEdgeSet);
            result.append(' ');
        }
        if (!this.removedEdgeSet.isEmpty()) {
            result.append('-');
            result.append(this.removedEdgeSet);
            result.append(' ');
        }
        return result.toString();
    }

    /** The set of added nodes of this delta. */
    final private Set<HostNode> addedNodeSet;
    /** The set of removed nodes of this delta. */
    final private Set<HostNode> removedNodeSet;
    /** The set of added edges of this delta. */
    final private Set<HostEdge> addedEdgeSet;
    /** The set of removed edges of this delta. */
    final private Set<HostEdge> removedEdgeSet;
}