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
package groove.transform;

import groove.grammar.host.HostEdge;
import groove.grammar.host.HostEdgeSet;
import groove.grammar.host.HostGraph;
import groove.grammar.host.HostNode;
import groove.grammar.rule.RuleNode;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Temporary record of the effects of a rule application.
 * Built up by a {@link RuleEvent} and then used in a {@link RuleApplication}.
 * @author Arend Rensink
 * @version $Revision $
 */
public class RuleEffect {
    /** Creates a full record with respect to a given source graph. */
    public RuleEffect(HostGraph host) {
        this(host, Fragment.ALL);
    }

    /** 
     * Creates a partial record with respect to a given source graph.
     */
    public RuleEffect(HostGraph host, Fragment fragment) {
        this.sourceNodes = host.nodeSet();
        this.fragment = fragment;
        this.createdNodeArray = null;
        this.createdNodeIndex = -1;
    }

    /** 
     * Creates a full record based on a predefined set of created nodes.
     */
    public RuleEffect(HostNode[] createdNodes) {
        this(createdNodes, Fragment.ALL);
    }

    /** 
     * Creates a partial record based on a predefined set of created nodes.
     */
    public RuleEffect(HostNode[] createdNodes, Fragment fragment) {
        this.sourceNodes = null;
        this.fragment = fragment;
        this.createdNodeArray = createdNodes;
        this.createdNodeIndex = 0;
    }

    /** Returns the set of nodes of the source graph. */
    Set<? extends HostNode> getSourceNodes() {
        return this.sourceNodes;
    }

    /** Returns the fragment of the record that is to be generated. */
    final Fragment getFragment() {
        return this.fragment;
    }

    /** 
     * Indicates that the created nodes have been predefined.
     * This implies that {@link #addCreatorNodes(RuleNode[])} rather than
     * {@link #addCreatedNodes(RuleNode[], HostNode[])} should be used to 
     * complete the record. 
     */
    final boolean isNodesInitialised() {
        return this.createdNodeIndex >= 0;
    }

    /**
     * Returns the currently stored map from rule node creators to
     * corresponding created nodes.
     */
    Map<RuleNode,HostNode> getCreatedNodeMap() {
        return this.createdNodeMap;
    }

    /** 
     * Adds information about the node creators.
     * This method should be used (in preference to {@link #addCreatedNodes(RuleNode[], HostNode[])})
     * if the created nodes have been pre-initialised.
     * @param creatorNodes next set of node creators
     * @see #addCreatedNodes(RuleNode[], HostNode[])
     * @see #isNodesInitialised()
     */
    void addCreatorNodes(RuleNode[] creatorNodes) {
        assert isNodesInitialised();
        HostNode[] createdNodes = this.createdNodeArray;
        Map<RuleNode,HostNode> createdNodeMap = this.createdNodeMap;
        if (createdNodeMap == null) {
            this.createdNodeMap =
                createdNodeMap = new HashMap<RuleNode,HostNode>();
        }
        int createdNodeStart = this.createdNodeIndex;
        int creatorCount = creatorNodes.length;
        for (int i = 0; i < creatorCount; i++) {
            createdNodeMap.put(creatorNodes[i], createdNodes[createdNodeStart
                + i]);
        }
        this.createdNodeIndex = createdNodeStart + creatorCount;
    }

    /** 
     * Adds information about created nodes to that already stored in the record.
     * Should only be used if {@link #isNodesInitialised()} is {@code false}.
     * @param creatorNodes node creators
     * @param createdNodes set of created nodes; should equal the values of the map
     */
    void addCreatedNodes(RuleNode[] creatorNodes, HostNode[] createdNodes) {
        assert !isNodesInitialised();
        int createdNodeCount = createdNodes.length;
        if (createdNodeCount > 0) {
            Map<RuleNode,HostNode> oldCreatedNodeMap = this.createdNodeMap;
            Set<HostNode> oldCreatedNodes = this.createdNodeSet;
            if (oldCreatedNodes == null) {
                int size = createdNodeCount * 2;
                oldCreatedNodeMap = new HashMap<RuleNode,HostNode>(size);
                oldCreatedNodes = new LinkedHashSet<HostNode>(size);
            }
            for (int i = 0; i < createdNodeCount; i++) {
                HostNode createdNode = createdNodes[i];
                oldCreatedNodes.add(createdNode);
                oldCreatedNodeMap.put(creatorNodes[i], createdNode);
            }
            this.createdNodeMap = oldCreatedNodeMap;
            this.createdNodeSet = oldCreatedNodes;
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
                    new HostEdgeSet(
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
            newCreatedEdges = new HostEdgeSet();
        } else if (this.createdEdgesAliased) {
            newCreatedEdges = new HostEdgeSet(this.createdEdges.size() * 2);
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

    /** Returns the (possibly {@code null}) array of created nodes. */
    final public HostNode[] getCreatedNodeArray() {
        if (this.createdNodeArray == null && this.createdNodeSet != null) {
            this.createdNodeArray =
                this.createdNodeSet.toArray(new HostNode[this.createdNodeSet.size()]);
        }
        return this.createdNodeArray;
    }

    /** Returns the (possibly {@code null}) set of created nodes. */
    final public Collection<HostNode> getCreatedNodes() {
        return isNodesInitialised() ? Arrays.asList(this.createdNodeArray)
                : this.createdNodeSet;
    }

    /** Indicates if the set of created nodes is non-empty. */
    final public boolean hasCreatedNodes() {
        return isNodesInitialised() ? this.createdNodeArray.length > 0
                : this.createdNodeSet != null;
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
    /** The part of the record that is generated. */
    private final Fragment fragment;
    /** Flag indicating that the created nodes are predefined. */
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
    /** 
     * Collection of created nodes. 
     * Only used if the created nodes are not predefined (see {@link #createdNodeArray}). 
     */
    private Set<HostNode> createdNodeSet;
    /** 
     * Predefined array of created nodes; if {@code null}, the created nodes
     * are not predefined.
     */
    private HostNode[] createdNodeArray;
    /** Index of the first unused element in {@link #createdNodeArray}. */
    private int createdNodeIndex;
    /** Collection of created edges. */
    private Collection<HostEdge> createdEdges;
    /** Flag indicating if {@link #createdEdges} is currently an alias. */
    private boolean createdEdgesAliased;
    /** Mapping from merged nodes to their merge targets. */
    private MergeMap mergeMap;
    /** Flag indicating if {@link #mergeMap} is currently an alias. */
    private boolean mergeMapAliased;

    /** Values indicating which part of the effect is recorded. */
    public static enum Fragment {
        /** Only node creation is recorded. */
        NODE_CREATION,
        /** Only node manipulation (creation, merging and deletion) is recorded. */
        NODE_ALL,
        /** Everything is recorded. */
        ALL;
    }
}
