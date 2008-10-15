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
package groove.graph;

import groove.util.DeltaSet;
import groove.util.StackedSet;
import groove.util.TreeHashSet;

import java.util.Collection;
import java.util.Set;

/**
 * Default implementation of a delta applier.
 * @author Arend Rensink
 * @version $Revision$
 */
public class DefaultDeltaApplier implements DeltaApplier {
    /**
     * Creates a delta store based on explicitly given added and removed sets.
     * The sets are copied.
     */
    protected DefaultDeltaApplier(Set<Node> addedNodeSet,
            Set<Node> removedNodeSet, Set<Edge> addedEdgeSet,
            Set<Edge> removedEdgeSet) {
        this.addedNodeSet = createNodeSet(addedNodeSet);
        this.removedNodeSet = createNodeSet(removedNodeSet);
        this.addedEdgeSet = createEdgeSet(addedEdgeSet);
        this.removedEdgeSet = createEdgeSet(removedEdgeSet);
    }

    /**
     * Creates a delta store based on explicitly given added and removed sets. A
     * further parameter controls if the sets are copied or shared.
     */
    protected DefaultDeltaApplier(Set<Node> addedNodeSet,
            Set<Node> removedNodeSet, Set<Edge> addedEdgeSet,
            Set<Edge> removedEdgeSet, boolean share) {
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
            for (Node addedNode : this.addedNodeSet) {
                target.addNode(addedNode);
            }
        }
        if (mode != NODES_ONLY) {
            for (Edge addedEdge : this.addedEdgeSet) {
                target.addEdge(addedEdge);
            }
            for (Edge removedEdge : this.removedEdgeSet) {
                target.removeEdge(removedEdge);
            }
        }
        // remove nodes only after the edges
        if (mode != EDGES_ONLY) {
            for (Node removedNode : this.removedNodeSet) {
                target.removeNode(removedNode);
            }
        }
    }

    public void applyDelta(DeltaTarget target) {
        applyDelta(target, ALL_ELEMENTS);
    }

    /** Returns an alias of the set of added nodes of this delta. */
    public Set<Node> getAddedNodeSet() {
        return this.addedNodeSet;
    }

    /** Returns an alias of the set of removed nodes of this delta. */
    public Set<Node> getRemovedNodeSet() {
        return this.removedNodeSet;
    }

    /** Returns an alias of the set of added edges of this delta. */
    public Set<Edge> getAddedEdgeSet() {
        return this.addedEdgeSet;
    }

    /** Returns an alias of the set of removed edges of this delta. */
    public Set<Edge> getRemovedEdgeSet() {
        return this.removedEdgeSet;
    }

    /**
     * Constructs a set by applying the node delta to a given origin set. The
     * resulting set is a copy of the origin, minus the removed edges and plus
     * the added edges.
     * @param origin the set to create the result from
     */
    public Set<Node> newNodeSet(Collection<? extends Node> origin) {
        Set<Node> result = createNodeSet(origin);
        result.removeAll(this.removedNodeSet);
        result.addAll(this.addedNodeSet);
        return result;
    }

    /**
     * Constructs a set by applying the edge delta to a given origin set. The
     * resulting set is a copy of the origin, minus the removed edges and plus
     * the added edges.
     * @param origin the set to create the result from
     */
    public Set<Edge> newEdgeSet(Collection<? extends Edge> origin) {
        Set<Edge> result = createEdgeSet(origin);
        result.removeAll(this.removedEdgeSet);
        result.addAll(this.addedEdgeSet);
        return result;
    }

    /**
     * Creates a {@link StackedSet} based on a given set of edges and the delta
     * information in this store. It is assumed that the start set corresponds
     * to origin of the delta; it is aliased as the lower set of the result.
     * @param origin the set of edges on top of which a stacked set is created
     * @return a stacked set based on <code>origin</code>, and the added and
     *         removed edge sets in this store
     */
    public StackedSet<Edge> newStackedEdgeSet(Set<? extends Edge> origin) {
        return createStackedSet(origin, this.addedEdgeSet, this.removedEdgeSet);
    }

    /**
     * Creates a {@link StackedSet} based on a given set of nodes and the delta
     * information in this store. It is assumed that the start set corresponds
     * to origin of the delta; it is aliased as the lower set of the result.
     * @param origin the set of nodes on top of which a stacked set is created
     * @return a stacked set based on <code>origin</code>, and the added and
     *         removed node sets in this store
     */
    public StackedSet<Node> newStackedNodeSet(Set<? extends Node> origin) {
        return createStackedSet(origin, this.addedNodeSet, this.removedNodeSet);
    }

    /**
     * Creates a {@link DeltaSet} based on a given set of edges and the delta
     * information in this store. It is assumed that the start set corresponds
     * to origin of the delta; the lower set of the result is a copy of the
     * origin, to which the delta is applied.
     * @param origin the set of edges on top of which a delta set is created
     * @return a delta set based on <code>origin</code>, and the added and
     *         removed edge sets in this store
     */
    public DeltaSet<Edge> newDeltaEdgeSet(Collection<Edge> origin) {
        return createDeltaSet(newEdgeSet(origin), this.addedEdgeSet,
            this.removedEdgeSet);
    }

    /**
     * Creates a {@link DeltaSet} based on a given set of nodes and the delta
     * information in this store. It is assumed that the start set corresponds
     * to origin of the delta; the lower set of the result is a copy of the
     * origin, to which the delta is applied.
     * @param origin the set of nodes on top of which a delta set is created
     * @return a delta set based on <code>origin</code>, and the added and
     *         removed node sets in this store
     */
    public DeltaSet<Node> newDeltaNodeSet(Collection<Node> origin) {
        return createDeltaSet(newNodeSet(origin), this.addedNodeSet,
            this.removedNodeSet);
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
    public int size() {
        return addedSize() + removedSize();
    }

    /**
     * Callback factory method for copying a given node set. Returns the empty
     * set if the given node set is <code>null</code>.
     */
    protected Set<Node> createNodeSet(Collection<? extends Node> set) {
        if (set == null) {
            return new NodeSet();
        } else {
            return new NodeSet(set);
        }
    }

    /**
     * Callback factory method for copying a given edge set. Returns the empty
     * set if the given edge set is <code>null</code>.
     */
    protected Set<Edge> createEdgeSet(Collection<? extends Edge> set) {
        Set<Edge> result = new TreeHashSet<Edge>();
        if (set != null) {
            result.addAll(set);
        }
        return result;
    }

    /**
     * Callback factory method for creating a new delta set.
     */
    protected <E extends Element> DeltaSet<E> createDeltaSet(Set<E> lower,
            Set<E> added, Set<E> removed) {
        return new DeltaSet<E>(lower, added, removed);
    }

    /**
     * Callback factory method for creating a new stacked set.
     */
    protected <E extends Element> StackedSet<E> createStackedSet(
            Set<? extends E> lower, Set<E> added, Set<E> removed) {
        return new StackedSet<E>(lower, added, removed);
    }

    /** The set of added nodes of this delta. */
    final private Set<Node> addedNodeSet;
    /** The set of removed nodes of this delta. */
    final private Set<Node> removedNodeSet;
    /** The set of added edges of this delta. */
    final private Set<Edge> addedEdgeSet;
    /** The set of removed edges of this delta. */
    final private Set<Edge> removedEdgeSet;
}