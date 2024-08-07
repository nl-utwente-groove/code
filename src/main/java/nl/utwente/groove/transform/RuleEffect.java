/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.transform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.utwente.groove.grammar.host.HostEdge;
import nl.utwente.groove.grammar.host.HostEdgeSet;
import nl.utwente.groove.grammar.host.HostGraph;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.grammar.host.HostNodeSet;
import nl.utwente.groove.grammar.rule.RuleNode;
import nl.utwente.groove.grammar.type.TypeLabel;
import nl.utwente.groove.transform.oracle.ValueOracle;
import nl.utwente.groove.util.DefaultFixable;
import nl.utwente.groove.util.collect.FilterIterator;

/**
 * Temporary record of the effects of a rule application.
 * Built up by a {@link RuleEvent} and then used in a {@link RuleApplication}.
 * @author Arend Rensink
 * @version $Revision$
 */
public class RuleEffect extends DefaultFixable {
    /** Creates a full record with respect to a given source graph
     * and for a given value oracle. */
    public RuleEffect(HostGraph host, ValueOracle oracle) {
        this(host, Fragment.ALL, oracle);
    }

    /**
     * Creates a possibly partial record with respect to a given source graph
     * and for a given value oracle.
     */
    public RuleEffect(HostGraph host, Fragment fragment, ValueOracle oracle) {
        this.source = host;
        this.fragment = fragment;
        this.createdNodeList = new ArrayList<>();
        this.nodesPredefined = false;
        this.oracle = oracle;
    }

    /**
     * Creates a full record based on a predefined set of created nodes.
     * @param source host graph to which this effect refers.
     */
    public RuleEffect(HostGraph source, HostNode[] createdNodes) {
        this(source, createdNodes, Fragment.ALL);
    }

    /**
     * Creates a partial record based on a predefined set of created nodes.
     * @param source host graph to which this effect refers.
     */
    public RuleEffect(HostGraph source, HostNode[] createdNodes, Fragment fragment) {
        this.source = source;
        this.fragment = fragment;
        this.nodesPredefined = true;
        this.oracle = null;
        this.createdNodeList = Arrays.asList(createdNodes);
    }

    /** Returns the source graph to which this effect refers. */
    public final HostGraph getSource() {
        return this.source;
    }

    private final HostGraph source;

    /** Returns the fragment of the record that is to be generated. */
    final Fragment getFragment() {
        return this.fragment;
    }

    /** The part of the record that is generated. */
    private final Fragment fragment;

    /**
     * Indicates that the created nodes have been predefined.
     * This implies that {@link #addCreatorNodes(RuleNode[])} rather than
     * {@link #addCreatedNodes(RuleNode[], HostNode[])} should be used to
     * complete the record.
     */
    final boolean isNodesPredefined() {
        return this.nodesPredefined;
    }

    /** Flag indicating that the effect was initialised with predefined nodes. */
    private final boolean nodesPredefined;

    /** Returns the value oracle for ask-parameter
     * @return a no-<code>null</code> oracle if the nodes are not predefined
     */
    final ValueOracle getOracle() {
        return this.oracle;
    }

    /** The value oracle to obtain values for ask-parameters.
     * Only initialised if the nodes are not predefined.
     */
    private final ValueOracle oracle;

    /**
     * Returns the currently stored map from rule node creators to
     * corresponding created nodes.
     */
    Map<RuleNode,HostNode> getCreatedNodeMap() {
        assert !isFixed();
        return this.createdNodeMap;
    }

    /**
     * Adds information about the node creators.
     * This method should be used (in preference to {@link #addCreatedNodes(RuleNode[], HostNode[])})
     * if the created nodes have been pre-initialised.
     * @param creatorNodes next set of node creators
     * @see #addCreatedNodes(RuleNode[], HostNode[])
     * @see #isNodesPredefined()
     */
    void addCreatorNodes(RuleNode[] creatorNodes) {
        assert !isFixed();
        assert isNodesPredefined();
        List<HostNode> createdNodes = this.createdNodeList;
        Map<RuleNode,HostNode> createdNodeMap = this.createdNodeMap;
        if (createdNodeMap == null) {
            this.createdNodeMap = createdNodeMap = new HashMap<>();
        }
        int createdNodeStart = this.createdNodeIndex;
        int count = creatorNodes.length;
        for (int i = 0; i < count; i++) {
            createdNodeMap.put(creatorNodes[i], createdNodes.get(createdNodeStart + i));
        }
        this.createdNodeIndex = createdNodeStart + count;
    }

    /**
     * List of created nodes, either predefined or built up during construction
     */
    private final List<HostNode> createdNodeList;

    /** Index of the first unused element in {@link #createdNodeList}. */
    private int createdNodeIndex;

    /**
     * Adds information about created nodes to that already stored in the record.
     * Should only be used if {@link #isNodesPredefined()} is {@code false}.
     * @param creatorNodes node creators
     * @param createdNodes set of created nodes; should equal the values of the map
     */
    void addCreatedNodes(RuleNode[] creatorNodes, HostNode[] createdNodes) {
        assert !isFixed();
        assert !isNodesPredefined();
        int createdNodeCount = createdNodes.length;
        if (createdNodeCount > 0) {
            var createdNodeMap = this.createdNodeMap;
            var createdNodeList = this.createdNodeList;
            if (createdNodeMap == null) {
                this.createdNodeMap = createdNodeMap = new HashMap<>();
            }
            var createdNodeSet = this.createdNodeSet;
            for (int i = 0; i < createdNodeCount; i++) {
                HostNode createdNode = createdNodes[i];
                createdNodeMap.put(creatorNodes[i], createdNode);
                createdNodeList.add(createdNode);
                if (createdNodeSet != null) {
                    boolean fresh = createdNodeSet.add(createdNode);
                    assert fresh : "New node %s was already in created nodes list %s"
                        .formatted(createdNode, createdNodeSet);
                }
            }
        }
    }

    /** Mapping from rule node creators to the corresponding created nodes. */
    private Map<RuleNode,HostNode> createdNodeMap;

    /** Tests if either the source graph contains a given node or this effect record adds it. */
    boolean containsNode(HostNode node) {
        if (getSource().containsNode(node)) {
            return true;
        }
        var createdNodeSet = this.createdNodeSet;
        if (createdNodeSet == null) {
            this.createdNodeSet = createdNodeSet = new HashSet<>(this.createdNodeList);
            assert createdNodeSet.size() == this.createdNodeList
                .size() : "Duplicate node in list %s".formatted(this.createdNodeList);
        }
        return createdNodeSet.contains(node);
    }

    private Set<HostNode> createdNodeSet;

    /** Creates and adds an edge by invoking the source graph's factory. */
    void addCreateEdge(HostNode source, TypeLabel label, HostNode target) {
        HostEdge edge = getSource().getFactory().createEdge(source, label, target);
        addCreatedEdge(edge);
    }

    /**
     * Adds a collection of erased nodes to those already stored in this record.
     */
    void addErasedNodes(HostNodeSet erasedNodes) {
        assert !isFixed();
        if (!erasedNodes.isEmpty()) {
            if (this.mergeMap == null) {
                // we maintain erasedNodes only if there is no merge map
                HostNodeSet oldErasedNodes = this.erasedNodes;
                HostNodeSet newErasedNodes;
                if (oldErasedNodes == null) {
                    newErasedNodes = erasedNodes;
                    this.erasedNodesAliased = true;
                } else {
                    if (this.erasedNodesAliased) {
                        newErasedNodes
                            = new HostNodeSet((oldErasedNodes.size() + erasedNodes.size()) * 2);
                        newErasedNodes.addAll(oldErasedNodes);
                        this.erasedNodesAliased = false;
                    } else {
                        newErasedNodes = oldErasedNodes;
                    }
                    newErasedNodes.addAll(erasedNodes);
                }
                this.erasedNodes = newErasedNodes;
            } else {
                // there is a merge map; add the erased nodes to it
                for (HostNode node : erasedNodes) {
                    this.mergeMap.removeNode(node);
                }
            }
        }
    }

    /** Flag indicating that the created nodes are predefined. */
    /** Collection of erased nodes. */
    private HostNodeSet erasedNodes;
    /** Flag indicating if {@link #erasedNodes} is currently an alias. */
    private boolean erasedNodesAliased;

    /**
     * Adds a collection of erased edges to those already stored in this record.
     */
    void addErasedEdges(HostEdgeSet erasedEdges) {
        assert !isFixed();
        HostEdgeSet oldErasedEdges = this.erasedEdges;
        HostEdgeSet newErasedEdges;
        if (erasedEdges.isEmpty()) {
            newErasedEdges = null;
        } else if (oldErasedEdges == null) {
            newErasedEdges = erasedEdges;
            this.erasedEdgesAliased = true;
        } else {
            if (this.erasedEdgesAliased) {
                newErasedEdges = new HostEdgeSet((oldErasedEdges.size() + erasedEdges.size()) * 2);
                newErasedEdges.addAll(oldErasedEdges);
                this.erasedEdgesAliased = false;
            } else {
                newErasedEdges = oldErasedEdges;
            }
            newErasedEdges.addAll(erasedEdges);
        }
        this.erasedEdges = newErasedEdges;
    }

    /** Collection of erased edges. */
    private HostEdgeSet erasedEdges;
    /** Flag indicating if {@link #erasedEdges} is currently an alias. */
    private boolean erasedEdgesAliased;

    /**
     * Adds a collection of created edges to those already stored in this record.
     */
    void addCreatedEdges(HostEdgeSet createdEdges) {
        assert !isFixed();
        HostEdgeSet oldCreatedEdges = this.createdEdges;
        HostEdgeSet newCreatedEdges;
        if (createdEdges.isEmpty()) {
            newCreatedEdges = oldCreatedEdges;
        } else if (oldCreatedEdges == null) {
            newCreatedEdges = createdEdges;
            this.createdEdgesAliased = true;
        } else {
            if (this.createdEdgesAliased) {
                newCreatedEdges
                    = new HostEdgeSet((oldCreatedEdges.size() + createdEdges.size()) * 2);
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
        assert !isFixed();
        HostEdgeSet oldCreatedEdges = this.createdEdges;
        HostEdgeSet newCreatedEdges;
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

    /** Collection of created edges. */
    private HostEdgeSet createdEdges;
    /** Flag indicating if {@link #createdEdges} is currently an alias. */
    private boolean createdEdgesAliased;

    /**
     * Adds a merge map to that already stored in this record.
     */
    void addMergeMap(MergeMap mergeMap) {
        assert !isFixed();
        MergeMap oldMergeMap = this.mergeMap;
        HostNodeSet erasedNodes = this.erasedNodes;
        MergeMap newMergeMap;
        if (oldMergeMap == null && erasedNodes == null) {
            newMergeMap = mergeMap;
            this.mergeMapAliased = true;
        } else {
            if (oldMergeMap == null) {
                // this is the first merge map but we have erased nodes
                newMergeMap = mergeMap.clone();
            } else if (this.mergeMapAliased) {
                newMergeMap = oldMergeMap.clone();
                this.mergeMapAliased = false;
                newMergeMap.putAll(mergeMap);
            } else {
                newMergeMap = oldMergeMap;
                newMergeMap.putAll(mergeMap);
            }
            if (erasedNodes != null) {
                for (HostNode node : erasedNodes) {
                    newMergeMap.removeNode(node);
                }
            }
        }
        this.mergeMap = newMergeMap;
    }

    /** Mapping from merged nodes to their merge targets. */
    private MergeMap mergeMap;
    /** Flag indicating if {@link #mergeMap} is currently an alias. */
    private boolean mergeMapAliased;

    /**
     * Returns the (possibly {@code null}) array of created nodes.
     * The elements of the array are given in the order of the rule creators.
     * Created nodes may be duplicated or {@code null} due to the
     * combined effect of creation, merging and deletion.
     */
    final public HostNode[] getCreatedNodeArray() {
        assert isFixed();
        List<HostNode> createdNodes = this.createdNodeList;
        int createdNodeCount = createdNodes.size();
        HostNode[] result = new HostNode[createdNodeCount];
        for (int i = 0; i < createdNodeCount; i++) {
            HostNode n = createdNodes.get(i);
            if (!isNodesPredefined() && hasMergeMap()) {
                n = getMergeMap().getNode(n);
            }
            result[i] = n;
        }
        return result;
    }

    /** Indicates if the set of removed nodes is non-empty.
     * @see #getRemovedNodes()
     */
    final public boolean hasRemovedNodes() {
        assert isFixed();
        return this.erasedNodes != null || hasMergeMap();
    }

    /**
     * Returns the (possibly {@code null}) set of removed nodes.
     * This combines the explicitly erased nodes and the merged nodes.
     */
    final public Set<HostNode> getRemovedNodes() {
        assert isFixed();
        Set<HostNode> result;
        if (hasMergeMap()) {
            result = this.removedNodes;
            if (result == null) {
                this.removedNodes = result = new HostNodeSet();
                for (HostNode node : getMergeMap().nodeMap().keySet()) {
                    if (getSource().containsNode(node)) {
                        result.add(node);
                    }
                }
            }
        } else {
            result = this.erasedNodes;
        }
        return result;
    }

    /**
     * The computed set of removed nodes, in case there is a merge map.
     * Otherwise, {@link #erasedNodes} is used
     */
    private Set<HostNode> removedNodes;

    /** Indicates if the set of removed edges is non-empty.
     * @see #getRemovedEdges()
     */
    final public boolean hasRemovedEdges() {
        assert isFixed();
        return this.erasedEdges != null || hasRemovedNodes();
    }

    /**
     * Returns the (possibly {@code null}) set of removed edges.
     * This combines the explicitly erased edges and the incident edges
     * of the removed nodes (including the merged nodes).
     * @see #getRemovedNodes()
     */
    final public Set<HostEdge> getRemovedEdges() {
        assert isFixed();
        Set<HostEdge> result = this.erasedEdges;
        if (hasRemovedNodes()) {
            result = this.removedEdges;
            if (result == null) {
                result = this.removedEdges = new HostEdgeSet();
                if (this.erasedEdges != null) {
                    result.addAll(this.erasedEdges);
                }
                for (HostNode node : getRemovedNodes()) {
                    result.addAll(getSource().edgeSet(node));
                }
            }
        }
        return result;
    }

    /**
     * The computed set of removed edges, in case there are removed nodes.
     * Otherwise, {@link #erasedNodes} is used
     */
    private HostEdgeSet removedEdges;

    /** Tests if a given edge is among the explicitly erased edges. */
    final public boolean isErasedEdge(HostEdge edge) {
        assert isFixed();
        return this.erasedEdges != null && this.erasedEdges.contains(edge);
    }

    /**
     * Indicates if the set of added nodes is non-empty.
     * @see #getAddedNodes()
     */
    final public boolean hasAddedNodes() {
        assert isFixed();
        return !this.createdNodeList.isEmpty();
    }

    /**
     * Returns the (possibly {@code null}) iterator over the set of added nodes.
     * The nodes returned are guaranteed to be non-{@code null} and
     * without duplicates.
     */
    final public Iterable<HostNode> getAddedNodes() {
        assert isFixed();
        Collection<HostNode> result = this.createdNodeList;
        if (!result.isEmpty() && hasMergeMap()) {
            result = this.addedNodes;
            if (result == null) {
                MergeMap mergeMap = getMergeMap();
                result = this.addedNodes = new HostNodeSet();
                for (HostNode node : this.createdNodeList) {
                    if (node != null) {
                        node = mergeMap.getNode(node);
                    }
                    if (node != null && !getSource().containsNode(node)) {
                        result.add(node);
                    }
                }
            }
        }
        return result;
    }

    private HostNodeSet addedNodes;

    /**
     * Indicates if there are any added edges, either through
     * explicit edge creation or through node merging.
     * Note that the {@link #getAddedEdges()} may nevertheless
     * return an empty set, if node deletion
     * or merging invalidates all created edges.
     * @see #getAddedEdges()
     */
    public final boolean hasAddedEdges() {
        assert isFixed();
        return this.createdEdges != null || hasMergeMap();
    }

    /**
     * Returns the (possibly {@code null}) set of created edges,
     * modified by the merge map (if any).
     * This includes the edges that are created due to merging.
     * The edges returned by the iterator are guaranteed to be
     * without duplicates and fresh w.r.t. the source graph.
     */
    final public Iterable<HostEdge> getAddedEdges() {
        assert isFixed();
        Iterable<HostEdge> result = null;
        final Set<HostEdge> createdEdges = this.createdEdges;
        if (hasMergeMap()) {
            HostEdgeSet addedEdges = this.addedEdges;
            if (addedEdges == null) {
                this.addedEdges = addedEdges = new HostEdgeSet();
                MergeMap mergeMap = getMergeMap();
                if (createdEdges != null) {
                    // transform the created edges through the merge map
                    for (HostEdge edge : createdEdges) {
                        HostEdge image = mergeMap.mapEdge(edge);
                        if (image == null) {
                            continue;
                        }
                        if (!getSource().containsEdge(image) || isErasedEdge(edge)) {
                            addedEdges.add(image);
                        }
                    }
                }
                // add the incident edges of the merged nodes
                for (HostNode node : mergeMap.nodeMap().keySet()) {
                    // only consider nodes that are not removed
                    if (mergeMap.getNode(node) == null) {
                        continue;
                    }
                    for (HostEdge edge : getSource().edgeSet(node)) {
                        // only consider edges that are not erased
                        if (isErasedEdge(edge)) {
                            continue;
                        }
                        HostEdge image = mergeMap.mapEdge(edge);
                        if (image == null) {
                            continue;
                        }
                        if (!getSource().containsEdge(image)) {
                            addedEdges.add(image);
                        }
                    }
                }
            }
            result = this.addedEdges;
        } else if (createdEdges != null) {
            final Set<HostNode> removedNodes = getRemovedNodes();
            // filter the added edges through the set of removed nodes
            result = new Iterable<>() {
                @Override
                public Iterator<HostEdge> iterator() {
                    return new FilterIterator<>(RuleEffect.this.createdEdges.iterator()) {
                        @Override
                        protected boolean approves(Object obj) {
                            if (!(obj instanceof HostEdge edge)) {
                                return false;
                            }
                            if (getSource().containsEdge(edge) && !isErasedEdge(edge)) {
                                return false;
                            }
                            if (removedNodes == null) {
                                return true;
                            }
                            return !removedNodes.contains(edge.source())
                                && !removedNodes.contains(edge.target());
                        }
                    };
                }
            };
        }
        return result;
    }

    private HostEdgeSet addedEdges;

    /**
     * Indicates if there are mergers.
     * @see #getMergeMap()
     */
    public final boolean hasMergeMap() {
        assert isFixed();
        return this.mergeMap != null;
    }

    /** Returns the (possibly {@code null}) merge map.
     * This maps nodes that were merged as a consequence of the rule application
     * to the result node, and also maps removed nodes to {@code null}.
     */
    public final MergeMap getMergeMap() {
        assert isFixed();
        return this.mergeMap;
    }

    /** Function indicating whether the the associated node mapping is the identity.
     */
    public boolean isNodeId() {
        assert isFixed();
        return !hasMergeMap() && !hasRemovedNodes();
    }

    /** Function mapping a given (source) node to its image,
     * taking the merge map and removed nodes into account.
     */
    public HostNode mapNode(HostNode node) {
        assert isFixed();
        if (hasMergeMap()) {
            return getMergeMap().getNode(node);
        } else if (hasRemovedNodes() && getRemovedNodes().contains(node)) {
            return null;
        } else {
            return node;
        }
    }

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
