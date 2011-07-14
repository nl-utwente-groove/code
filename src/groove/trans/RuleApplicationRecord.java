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
package groove.trans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Temporary record of the changes involved in a rule application.
 * Built up by a {@link RuleEvent} and then used in a {@link RuleApplication}.
 * @author Arend Rensink
 * @version $Revision $
 */
public class RuleApplicationRecord {
    /** Creates a record with respect to a given graph node set. */
    public RuleApplicationRecord(Set<? extends HostNode> sourceNodes) {
        this.sourceNodes = sourceNodes;
    }

    /** Returns the set of nodes of the source graph. */
    Set<? extends HostNode> getSourceNodes() {
        return this.sourceNodes;
    }

    /**
     * Returns the currently stored map from rule node creators to
     * corresponding created nodes.
     */
    Map<RuleNode,HostNode> getCreatedNodeMap() {
        return this.createdNodeMap;
    }

    /** 
     * Adds information about created nodes to that already stored in the record.
     * @param createdNodeMap mapping from node creators to corresponding created nodes
     * @param createdNodes set of created nodes; should equal the values of the map
     */
    void addCreatedNodes(Map<RuleNode,HostNode> createdNodeMap,
            Set<HostNode> createdNodes) {
        assert createdNodes.containsAll(createdNodeMap.values());
        assert createdNodes.size() == createdNodeMap.size();
        if (!createdNodes.isEmpty()) {
            Map<RuleNode,HostNode> oldCreatedNodeMap = this.createdNodeMap;
            Set<HostNode> oldCreatedNodes = this.createdNodes;
            Map<RuleNode,HostNode> newCreatedNodeMap;
            Set<HostNode> newCreatedNodes;
            // alias the parameter set if there were no created nodes up till now
            if (oldCreatedNodes == null) {
                newCreatedNodeMap = createdNodeMap;
                newCreatedNodes = createdNodes;
                this.createdNodesAliased = true;
            } else {
                if (this.createdNodesAliased) {
                    // copy the currently aliased structures
                    int size =
                        (oldCreatedNodes.size() + createdNodeMap.size()) * 2;
                    newCreatedNodeMap = new HashMap<RuleNode,HostNode>(size);
                    newCreatedNodeMap.putAll(oldCreatedNodeMap);
                    newCreatedNodes = new HashSet<HostNode>(size);
                    newCreatedNodes.addAll(oldCreatedNodes);
                    this.createdNodesAliased = false;
                } else {
                    // the structures were already copied
                    newCreatedNodeMap = oldCreatedNodeMap;
                    newCreatedNodes = oldCreatedNodes;
                }
                newCreatedNodeMap.putAll(createdNodeMap);
                newCreatedNodes.addAll(createdNodes);
            }
            this.createdNodeMap = newCreatedNodeMap;
            this.createdNodes = newCreatedNodes;
        }
    }

    /** 
     * Adds a collection of erased nodes to those already stored in this record.
     */
    void addErasedNodes(Set<HostNode> erasedNodes) {
        if (!erasedNodes.isEmpty()) {
            Set<HostNode> oldErasedNodes = this.erasedNodes;
            Set<HostNode> newErasedNodes;
            if (oldErasedNodes == null) {
                newErasedNodes = erasedNodes;
                this.erasedNodesAliased = true;
            } else {
                if (this.erasedNodesAliased) {
                    newErasedNodes =
                        new HashSet<HostNode>(
                            (oldErasedNodes.size() + erasedNodes.size()) * 2);
                    newErasedNodes.addAll(oldErasedNodes);
                    this.erasedNodesAliased = false;
                } else {
                    newErasedNodes = oldErasedNodes;
                }
                newErasedNodes.addAll(erasedNodes);
            }
            this.erasedNodes = newErasedNodes;
        }
    }

    /** 
     * Adds a collection of erased edges to those already stored in this record.
     */
    void addErasedEdges(Set<HostEdge> erasedEdges) {
        Set<HostEdge> oldErasedEdges = this.erasedEdges;
        Set<HostEdge> newErasedEdges;
        if (erasedEdges.isEmpty()) {
            newErasedEdges = null;
        } else if (oldErasedEdges == null) {
            newErasedEdges = erasedEdges;
            this.erasedEdgesAliased = true;
        } else {
            if (this.erasedEdgesAliased) {
                newErasedEdges =
                    new HostEdgeSet(
                        (oldErasedEdges.size() + erasedEdges.size()) * 2);
                newErasedEdges.addAll(oldErasedEdges);
                this.erasedEdgesAliased = false;
            } else {
                newErasedEdges = oldErasedEdges;
            }
            newErasedEdges.addAll(erasedEdges);
        }
        this.erasedEdges = newErasedEdges;
    }

    /** 
     * Adds a collection of created edges to those already stored in this record.
     */
    void addCreatedEdges(Set<HostEdge> createdEdges) {
        Collection<HostEdge> oldCreatedEdges = this.createdEdges;
        Collection<HostEdge> newCreatedEdges;
        if (createdEdges.isEmpty()) {
            newCreatedEdges = oldCreatedEdges;
        } else if (oldCreatedEdges == null) {
            newCreatedEdges = createdEdges;
            this.createdEdgesAliased = true;
        } else {
            if (this.createdEdgesAliased) {
                newCreatedEdges =
                    new ArrayList<HostEdge>(
                        (oldCreatedEdges.size() + createdEdges.size()) * 2);
                newCreatedEdges.addAll(oldCreatedEdges);
                this.createdEdgesAliased = false;
            } else {
                newCreatedEdges = oldCreatedEdges;
            }
            newCreatedEdges.addAll(createdEdges);
        }
        this.createdEdges = newCreatedEdges;
    }

    /**
     * Adds a single created edges to the collection of created edges
     * stored in this record.
     */
    void addCreatedEdge(HostEdge edge) {
        Collection<HostEdge> oldCreatedEdges = this.createdEdges;
        Collection<HostEdge> newCreatedEdges;
        if (oldCreatedEdges == null) {
            newCreatedEdges = new ArrayList<HostEdge>();
        } else if (this.createdEdgesAliased) {
            newCreatedEdges =
                new ArrayList<HostEdge>(this.createdEdges.size() * 2);
            newCreatedEdges.addAll(oldCreatedEdges);
            this.createdEdgesAliased = false;
        } else {
            newCreatedEdges = oldCreatedEdges;
        }
        newCreatedEdges.add(edge);
        this.createdEdges = newCreatedEdges;
    }

    /** 
     * Adds a merge map to that already stored in this record.
     */
    void addMergeMap(MergeMap mergeMap) {
        MergeMap oldMergeMap = this.mergeMap;
        MergeMap newMergeMap;
        if (oldMergeMap == null) {
            newMergeMap = mergeMap;
            this.mergeMapAliased = true;
        } else {
            if (this.mergeMapAliased) {
                newMergeMap = new MergeMap(oldMergeMap.getFactory());
                newMergeMap.putAll(oldMergeMap);
                this.mergeMapAliased = false;
            } else {
                newMergeMap = oldMergeMap;
            }
            newMergeMap.putAll(mergeMap);
        }
        this.mergeMap = newMergeMap;
    }

    /** Returns the (possibly {@code null}) set of erased nodes. */
    final public Set<HostNode> getErasedNodes() {
        return this.erasedNodes;
    }

    /** Indicates if the set of erased nodes is non-empty. */
    final public boolean hasErasedNodes() {
        return this.erasedNodes != null;
    }

    /** Returns the (possibly {@code null}) set of erased edges. */
    final public Collection<HostEdge> getErasedEdges() {
        return this.erasedEdges;
    }

    /** Indicates if the set of erased edges is non-empty. */
    final public boolean hasErasedEdges() {
        return this.erasedEdges != null;
    }

    /** Tests if a given edge is among the erased edges. */
    final public boolean isErasedEdge(HostEdge edge) {
        return hasErasedEdges() && getErasedEdges().contains(edge);
    }

    /** Returns the (possibly {@code null}) set of created nodes. */
    final public Set<HostNode> getCreatedNodes() {
        return this.createdNodes;
    }

    /** Indicates if the set of created nodes is non-empty. */
    final public boolean hasCreatedNodes() {
        return this.createdNodes != null;
    }

    /** 
     * Returns the (possibly {@code null}) set of created edges,
     * modified by the merge map (if any).
     */
    final public Iterable<HostEdge> getCreatedTargetEdges() {
        final Collection<HostEdge> createdEdges = this.createdEdges;
        if (createdEdges == null) {
            return null;
        } else if (hasMergeMap()) {
            return new Iterable<HostEdge>() {
                @Override
                public Iterator<HostEdge> iterator() {
                    return new Iterator<HostEdge>() {
                        @Override
                        public boolean hasNext() {
                            HostEdge next = this.next;
                            Set<HostEdge> previous = this.mergedEdges;
                            MergeMap mergeMap = getMergeMap();
                            Iterator<HostEdge> inner = this.createdEdgeIter;
                            while (next == null && inner.hasNext()) {
                                next = mergeMap.mapEdge(inner.next());
                                if (next != null && !previous.add(next)) {
                                    // not a new edge
                                    next = null;
                                }
                            }
                            this.next = next;
                            return next != null;
                        }

                        @Override
                        public HostEdge next() {
                            if (hasNext()) {
                                HostEdge result = this.next;
                                this.next = null;
                                return result;
                            } else {
                                throw new UnsupportedOperationException();
                            }
                        }

                        @Override
                        public void remove() {
                            throw new UnsupportedOperationException();
                        }

                        private HostEdge next;
                        private final Iterator<HostEdge> createdEdgeIter =
                            createdEdges.iterator();
                        /** Set of previously computed merged edges. */
                        private final Set<HostEdge> mergedEdges =
                            new HostEdgeSet();
                    };
                }
            };
        } else if (hasErasedNodes()) {
            return new Iterable<HostEdge>() {
                @Override
                public Iterator<HostEdge> iterator() {
                    return new Iterator<HostEdge>() {
                        @Override
                        public boolean hasNext() {
                            HostEdge next = this.next;
                            Set<HostNode> erasedNodes = getErasedNodes();
                            Iterator<HostEdge> inner = this.createdEdgeIter;
                            while (next == null && inner.hasNext()) {
                                next = inner.next();
                                if (next != null
                                    && (erasedNodes.contains(next.source()) || erasedNodes.contains(next.target()))) {
                                    // the created edge should not exist
                                    next = null;
                                }
                            }
                            this.next = next;
                            return next != null;
                        }

                        @Override
                        public HostEdge next() {
                            if (hasNext()) {
                                HostEdge result = this.next;
                                this.next = null;
                                return result;
                            } else {
                                throw new UnsupportedOperationException();
                            }
                        }

                        @Override
                        public void remove() {
                            throw new UnsupportedOperationException();
                        }

                        private HostEdge next;
                        private final Iterator<HostEdge> createdEdgeIter =
                            createdEdges.iterator();
                    };
                }
            };
        } else {
            return createdEdges;
        }
    }

    /** 
     * Indicates if there are any created edges.
     * Note that the {@link #getCreatedTargetEdges()} may nevertheless
     * return an empty set, if node deletion
     * or merging invalidates all created edges.
     */
    public final boolean hasCreatedEdges() {
        return this.createdEdges != null;
    }

    /** Returns the (possibly {@code null}) merge map. */
    public final MergeMap getMergeMap() {
        return this.mergeMap;
    }

    /** Indicates if the set of erased nodes is non-empty. */
    public final boolean hasMergeMap() {
        return this.mergeMap != null;
    }

    /** The nodes of the source graph. */
    private final Set<? extends HostNode> sourceNodes;
    /** Collection of erased nodes. */
    private Set<HostNode> erasedNodes;
    /** Flag indicating if {@link #erasedNodes} is currently an alias. */
    private boolean erasedNodesAliased;
    /** Collection of erased edges. */
    private Set<HostEdge> erasedEdges;
    /** Flag indicating if {@link #erasedEdges} is currently an alias. */
    private boolean erasedEdgesAliased;
    /** Mapping from rule node creators to the corresponding created nodes. */
    private Map<RuleNode,HostNode> createdNodeMap;
    /** Collection of created nodes. */
    private Set<HostNode> createdNodes;
    /** Flag indicating if {@link #createdNodes} is currently an alias. */
    private boolean createdNodesAliased;
    /** Collection of created edges. */
    private Collection<HostEdge> createdEdges;
    /** Flag indicating if {@link #createdEdges} is currently an alias. */
    private boolean createdEdgesAliased;
    /** Mapping from merged nodes to their merge targets. */
    private MergeMap mergeMap;
    /** Flag indicating if {@link #mergeMap} is currently an alias. */
    private boolean mergeMapAliased;
}
