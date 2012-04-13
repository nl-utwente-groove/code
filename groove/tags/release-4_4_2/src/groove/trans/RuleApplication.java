// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/*
 * $Id: DefaultApplication.java,v 1.10 2008-02-06 17:04:38 rensink Exp $
 */
package groove.trans;

import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Node;
import groove.graph.algebra.ValueNode;
import groove.match.TreeMatch;
import groove.util.Property;
import groove.util.Visitor;
import groove.util.Visitor.Finder;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class representing a particular application of a {@link groove.trans.Rule} and a
 * graph. This is essentially the combination of a {@link RuleEvent}, the host graph,
 * and the created nodes.
 * <p>
 * The main functionality of objects of this class is to apply the rule event's changes
 * to an arbitrary {@link DeltaTarget}, and to construct the target graph as well as a 
 * morphism from host to target.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-02-06 17:04:38 $
 */
public class RuleApplication implements DeltaApplier {
    /**
     * Constructs a new derivation on the basis of a given rule and host
     * graph.
     * @param event the production rule instance involved
     * @param source the host graph to which the rule is to be applied
     */
    public RuleApplication(RuleEvent event, HostGraph source) {
        this(event, source, null);
    }

    /**
     * Constructs a new derivation on the basis of a given production rule, host
     * graph and rule factory.
     * @param event the production rule instance involved
     * @param source the host graph to which the rule is to be applied
     * @param coanchorImage the created nodes, in the order of the rule's
     *        coanchor. If <code>null</code>, the coanchor image has to be
     *        computed from the source graph.
     */
    public RuleApplication(final RuleEvent event, HostGraph source,
            HostNode[] coanchorImage) {
        this.event = event;
        this.rule = event.getRule();
        this.source = source;
        this.coanchorImage = coanchorImage;
        if (event instanceof BasicEvent) {
            this.anchorMap = ((BasicEvent) event).getAnchorMap();
        }
        assert testEvent(event, source);
    }

    /**
     * Tests if a given event has a match at a given source graph.
     */
    private boolean testEvent(final RuleEvent event, HostGraph source) {
        final Property<Proof> proofContainsEvent = new Property<Proof>() {
            @Override
            public boolean isSatisfied(Proof value) {
                return value.newEvent(null).equals(event);
            }
        };
        final Finder<Proof> eventFinder = Visitor.newFinder(proofContainsEvent);
        final Property<TreeMatch> matchContainsProof =
            new Property<TreeMatch>() {
                @Override
                public boolean isSatisfied(TreeMatch value) {
                    return value.traverseProofs(eventFinder) != null;
                }
            };
        Finder<TreeMatch> matchFinder = Visitor.newFinder(matchContainsProof);
        boolean result =
            this.rule.getEventMatcher().traverse(source, event.getAnchorMap(),
                matchFinder) != null;
        eventFinder.dispose();
        matchFinder.dispose();
        return result;
    }

    /**
     * Reconstructs a derivation on the basis of a given rule event, host
     * graph and target graph, and created nodes.
     * @param event the production rule instance involved
     * @param source the host graph to which the rule is to be applied
     * @param coanchorImage the created nodes, in the order of the rule's
     *        coanchor. If <code>null</code>, the coanchor image has to be
     *        computed from the source graph.
     */
    public RuleApplication(RuleEvent event, HostGraph source, HostGraph target,
            HostNode[] coanchorImage) {
        this(event, source, coanchorImage);
        this.target = target;
    }

    /**
     * Returns the source graph to which the rule is applied.
     */
    public HostGraph getSource() {
        return this.source;
    }

    /**
     * Returns the rule for which this is an application.
     */
    public Rule getRule() {
        return this.rule;
    }

    /**
     * Returns a target graph created as a result of the application. The target
     * is created lazily.
     */
    public HostGraph getTarget() {
        if (this.target == null) {
            if (this.rule.isModifying()) {
                this.target = computeTarget();
            } else {
                this.target = this.source;
            }
        }
        return this.target;
    }

    /**
     * Callback factory method to compute a target for this applier.
     */
    private HostGraph computeTarget() {
        HostGraph target = createTarget();
        applyDelta(target);
        target.setFixed();
        return target;
    }

    /**
     * Returns the match of the rule's LHS in the source graph of this
     * derivation.
     */
    public Proof getMatch() {
        if (this.match == null) {
            this.match = computeMatch();
        }
        return this.match;
    }

    /**
     * Callback method to create the matching from the rule's LHS to the source
     * graph.
     * @see #getMatch()
     */
    private Proof computeMatch() {
        return getEvent().getMatch(this.source);
    }

    /**
     * Returns the transformation morphism underlying this derivation.
     */
    public HostGraphMorphism getMorphism() {
        if (this.morphism == null) {
            this.morphism = computeMorphism();
        }
        return this.morphism;
    }

    /**
     * Constructs the morphism between source and target graph from the
     * application.
     */
    private HostGraphMorphism computeMorphism() {
        HostGraphMorphism result = createMorphism();
        MergeMap mergeMap = getMergeMap();
        // copy the source node and edge set, to avoid modification exceptions
        // in case graph aliasing was used
        Set<HostNode> sourceNodes =
            new HashSet<HostNode>(this.source.nodeSet());
        Set<HostEdge> sourceEdges =
            new HashSet<HostEdge>(this.source.edgeSet());
        for (HostNode node : sourceNodes) {
            HostNode nodeImage = mergeMap.getNode(node);
            if (nodeImage != null && getTarget().containsNode(nodeImage)) {
                result.putNode(node, nodeImage);
            }
        }
        Set<HostEdge> erasedEdges = getErasedEdges();
        for (HostEdge edge : sourceEdges) {
            if (!erasedEdges.contains(edge)) {
                HostEdge edgeImage = mergeMap.mapEdge(edge);
                if (edgeImage != null && getTarget().containsEdge(edgeImage)) {
                    result.putEdge(edge, edgeImage);
                }
            }
        }
        return result;
    }

    private HostNode[] getCreatedNodes() {
        if (this.coanchorImage == null) {
            this.coanchorImage = computeCreatedNodes();
        }
        return this.coanchorImage;
    }

    /**
     * Callback factory method to create a coanchor image for this application
     * from a given match and for a given host graph. The image consists of
     * fresh images for the creator nodes of the rule.
     */
    private HostNode[] computeCreatedNodes() {
        HostNode[] result;
        Set<HostNode> createdNodes =
            getEvent().getCreatedNodes(this.source.nodeSet());
        if (createdNodes.size() == 0) {
            result = EMPTY_COANCHOR_IMAGE;
        } else {
            result = new HostNode[createdNodes.size()];
            createdNodes.toArray(result);
        }
        return result;
    }

    /**
     * Applies the rule to a given delta target. This is presumably the host
     * graph to which the underlying rule is to be applied. The source should
     * coincide with that for which the footprint was originally created
     * @param target the target object on which the modifications are to be
     *        performed
     */
    public void applyDelta(DeltaTarget target) {
        if (this.rule.isModifying()) {
            eraseEdges(target);
            // either merge or erase the LHS nodes
            if (this.rule.hasMergers()) {
                mergeNodes(target);
            } else {
                eraseNodes(target);
            }
            if (this.rule.hasCreators()) {
                createNodes(target);
                createEdges(target);
            }
        }
    }

    /**
     * Wraps <code>target</code> into a {@link FilteredDeltaTarget} and then
     * calls {@link #applyDelta(DeltaTarget)}.
     */
    public void applyDelta(DeltaTarget target, int mode) {
        applyDelta(new FilteredDeltaTarget(target, mode));
    }

    /**
     * Erases the images of the reader nodes of the rule, together with their
     * incident edges.
     * @param target the target to which to apply the changes
     */
    private void eraseNodes(DeltaTarget target) {
        Set<HostNode> nodeSet = getErasedNodes();
        // also remove the incident edges of the eraser nodes
        if (!nodeSet.isEmpty()) {
            // there is a choice here to query the graph for its incident edge
            // set
            // which may be expensive if it hasn't yet been computed
            Set<HostEdge> removedEdges = new HashSet<HostEdge>();
            for (HostNode node : nodeSet) {
                for (HostEdge edge : this.source.edgeSet(node)) {
                    if (removedEdges.add(edge)) {
                        target.removeEdge(edge);
                        registerErasure(edge);
                    }
                }
            }
            // // the alternative is to iterate over all edges of the source
            // graph
            // // currently this seems to be fastest
            // for (Edge edgeMatch: source.edgeSet()) {
            // int arity = edgeMatch.endCount();
            // boolean removed = false;
            // for (int i = 0; !removed && i < arity; i++) {
            // removed = nodeSet.contains(edgeMatch.end(i));
            // }
            // if (removed) {
            // target.removeEdge(edgeMatch);
            // registerErasure(edgeMatch);
            // }
            // }
            removeNodeSet(target, nodeSet);
        }
        removeIsolatedValueNodes(target);
    }

    /**
     * Removes those value nodes whose incoming edges have all been erased.
     */
    private void removeIsolatedValueNodes(DeltaTarget target) {
        // for efficiency we don't use the getter but test for null
        if (this.isolatedValueNodes != null) {
            for (ValueNode node : this.isolatedValueNodes) {
                target.removeNode(node);
                if (this.removedValueNodes == null) {
                    this.removedValueNodes = new HashSet<ValueNode>();
                }
                this.removedValueNodes.add(node);
            }
        }
    }

    /**
     * Performs the edge erasure necessary according to the rule.
     * @param target the target to which to apply the changes
     */
    private void eraseEdges(DeltaTarget target) {
        for (HostEdge erasedEdge : getErasedEdges()) {
            target.removeEdge(erasedEdge);
            registerErasure(erasedEdge);
        }
    }

    /**
     * Callback method to notify that an edge has been erased. Used to ensure
     * that isolated value nodes are removed from the graph.
     */
    private void registerErasure(HostEdge edge) {
        HostNode target = edge.target();
        if (target instanceof ValueNode) {
            Set<HostEdge> edges = getValueNodeEdges((ValueNode) target);
            edges.remove(edge);
            if (edges.isEmpty()) {
                getIsolatedValueNodes().add((ValueNode) target);
            }
        }
    }

    /**
     * Performs the node (and edge) merging.
     * @param target the target to which to apply the changes
     */
    private void mergeNodes(DeltaTarget target) {
        // delete the merged nodes
        MergeMap mergeMap = getMergeMap();
        Set<HostEdge> renamedEdges = new HashSet<HostEdge>();
        Set<HostEdge> erasedEdges = getErasedEdges();
        for (HostNode mergedElem : mergeMap.nodeMap().keySet()) {
            removeNode(target, mergedElem);
            // replace the incident edges of the merged nodes
            for (HostEdge sourceEdge : this.source.edgeSet(mergedElem)) {
                if (!erasedEdges.contains(sourceEdge)) {
                    target.removeEdge(sourceEdge);
                    HostEdge image = mergeMap.mapEdge(sourceEdge);
                    assert image != sourceEdge;
                    // if the edge is in the source and not erased, it is also
                    // already
                    // in the target, so we do not have to add it
                    if (image != null
                        && (erasedEdges.contains(image) || !this.source.containsEdge(image))) {
                        // maybe we added the edge already, due to another
                        // merged node
                        if (renamedEdges.add(image)) {
                            addEdge(target, image);
                        }
                    } else {
                        registerErasure(sourceEdge);
                    }
                }
            }
        }
        if (!renamedEdges.isEmpty()) {
            getRenamedEdges().addAll(renamedEdges);
        }
        removeIsolatedValueNodes(target);
    }

    /**
     * Adds nodes to the target graph, as dictated by the rule's RHS.
     * 
     * @param target the target to which to apply the changes
     */
    private void createNodes(DeltaTarget target) {
        for (HostNode node : getCreatedNodes()) {
            target.addNode(node);
        }
    }

    /**
     * Adds edges to the target, as dictated by the rule's RHS.
     * @param target the target to which to apply the changes
     */
    protected void createEdges(DeltaTarget target) {
        // don't build the renamed edge set if that was not done already
        Set<HostEdge> renamedEdges = this.renamedEdges;
        // first add the (pre-computed) simple creator edge images
        for (HostEdge image : getEvent().getSimpleCreatedEdges()) {
            // only add if not already in the source or renamed, or just erased
            boolean existing =
                this.source.containsEdge(image) || renamedEdges != null
                    && renamedEdges.contains(image);
            if (!existing || getErasedEdges().contains(image)) {
                addEdge(target, image);
            }
        }
        // now compute and add the complex creator edge images
        for (HostEdge image : getEvent().getComplexCreatedEdges(
            Arrays.asList(getCreatedNodes()).iterator())) {
            // only add if the image exists
            if (image != null) {
                addEdge(target, image);
            }
        }
    }

    /**
     * The hash code is based on the identity of the event.
     */
    @Override
    public int hashCode() {
        return getEvent().identityHashCode();
    }

    /**
     * Two rule applications are equal if they have the same source and event.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RuleApplication) {
            RuleApplication other = (RuleApplication) obj;
            return equalsEvent(other) && equalsSource(other);
        } else {
            return false;
        }
    }

    /**
     * Tests if the rules of two rule applications coincide. Callback method
     * from {@link #equals(Object)}.
     */
    protected boolean equalsSource(RuleApplication other) {
        return getSource() == other.getSource();
    }

    /**
     * Tests if the rules of two rule applications coincide. Callback method
     * from {@link #equals(Object)}.
     */
    protected boolean equalsEvent(RuleApplication other) {
        return getEvent() == other.getEvent();
    }

    @Override
    public String toString() {
        StringBuffer result =
            new StringBuffer("Derivation for rule " + getRule().getName());
        result.append("\nMatching:\n  " + this.anchorMap);
        return result.toString();
    }

    /**
     * Returns the set of explicitly erased nodes, i.e., the images of the LHS
     * eraser nodes.
     */
    protected Set<HostNode> getErasedNodes() {
        return this.event.getErasedNodes();
    }

    /**
     * Returns the set of explicitly erased edges, i.e., the images of the LHS
     * eraser edges.
     */
    protected Set<HostEdge> getErasedEdges() {
        if (this.erasedEdges == null) {
            this.erasedEdges = this.event.getSimpleErasedEdges();
        }
        return this.erasedEdges;
    }

    /**
     * Returns the event underlying this application.
     */
    public RuleEvent getEvent() {
        return this.event;
    }

    /**
     * Returns a mapping from source to target graph nodes, dictated by the
     * merger and eraser nodes in the rules.
     * @return an {@link MergeMap} that maps nodes of the source that are merged
     *         away to their merged images, and deleted nodes to
     *         <code>null</code>.
     */
    protected MergeMap getMergeMap() {
        return this.event.getMergeMap();
    }

    /**
     * Callback factory method to create a morphism from source to target graph.
     * Note that this is <i>not</i> the same kind of object as the matching.
     */
    protected HostGraphMorphism createMorphism() {
        return new HostGraphMorphism(getSource().getFactory());
    }

    /**
     * Callback factory method for creating the target graph of an application.
     * This implementation clones the source.
     * @see HostGraph#clone()
     */
    protected HostGraph createTarget() {
        return getSource().clone();
    }

    /**
     * Adds an edge to a delta target, if the edge is not <code>null</code>
     * and not already in the source graph. Optimises by trying to call
     * {@link Graph#addEdgeWithoutCheck(Edge)} if the target is an
     * {@link Graph}.
     */
    protected void addEdge(DeltaTarget target, HostEdge edge) {
        HostNode targetNode = edge.target();
        if (targetNode instanceof ValueNode
            && (!this.source.containsNode(targetNode) && !getAddedValueNodes().contains(
                targetNode)) || this.removedValueNodes != null
            && this.removedValueNodes.contains(targetNode)) {
            target.addNode(targetNode);
            boolean nodeAdded =
                getAddedValueNodes().add((ValueNode) targetNode);
            assert nodeAdded : String.format("%s already contained %s",
                getAddedValueNodes(), targetNode);
            if (this.removedValueNodes != null
                && this.removedValueNodes.contains(targetNode)) {
                this.removedValueNodes.remove(targetNode);
            }
        }
        if (target instanceof HostGraph) {
            ((HostGraph) target).addEdgeWithoutCheck(edge);
        } else {
            // apparently the target wasn't an InternalGraph
            // so we can't do efficient edge addition
            target.addEdge(edge);
        }
    }

    /**
     * Removes a node from a delta target. Optimises by trying to call
     * {@link Graph#removeNodeWithoutCheck(Node)} if the target is an
     * {@link Graph}.
     */
    private void removeNode(DeltaTarget target, HostNode node) {
        if (target instanceof HostGraph) {
            ((HostGraph) target).removeNodeWithoutCheck(node);
        } else {
            // apparently the target wasn't an InternalGraph
            // so we can't do efficient edge removal
            target.removeNode(node);
        }
    }

    /**
     * Removes a set of nodes from a delta target. Optimizes by trying to call
     * {@link Graph#removeNodeWithoutCheck(Node)} if the target is an
     * {@link Graph}.
     */
    private void removeNodeSet(DeltaTarget target, Collection<HostNode> nodeSet) {
        if (target instanceof HostGraph) {
            ((HostGraph) target).removeNodeSetWithoutCheck(nodeSet);
        } else {
            // apparently the target wasn't an InternalGraph
            // so we can't do efficient edge removal
            for (HostNode node : nodeSet) {
                target.removeNode(node);
            }
        }
    }

    /**
     * Lazily creates and returns the set of remaining incident edges of a given
     * value node.
     */
    private Set<HostEdge> getValueNodeEdges(ValueNode node) {
        if (this.valueNodeEdgesMap == null) {
            this.valueNodeEdgesMap = new HashMap<ValueNode,Set<HostEdge>>();
        }
        Set<HostEdge> result = this.valueNodeEdgesMap.get(node);
        if (result == null) {
            result = new HashSet<HostEdge>(this.source.inEdgeSet(node));
            this.valueNodeEdgesMap.put(node, result);
        }
        return result;
    }

    /**
     * Returns the currently detected set of value nodes that have become
     * isolated due to edge erasure.
     */
    private Set<ValueNode> getIsolatedValueNodes() {
        if (this.isolatedValueNodes == null) {
            this.isolatedValueNodes = new HashSet<ValueNode>();
        }
        return this.isolatedValueNodes;
    }

    /**
     * Returns the currently added set of value nodes.
     */
    private Set<ValueNode> getAddedValueNodes() {
        if (this.addedValueNodes == null) {
            this.addedValueNodes = new HashSet<ValueNode>();
        }
        return this.addedValueNodes;
    }

    /**
     * Returns the set of edges that were renamed due to node mergers.
     */
    private Set<HostEdge> getRenamedEdges() {
        if (this.renamedEdges == null) {
            this.renamedEdges = new HashSet<HostEdge>();
        }
        return this.renamedEdges;
    }

    /** Returns the relation between rule nodes and target graph nodes. */
    public Map<RuleNode,Set<HostNode>> getComatch() {
        if (this.comatch == null) {
            this.comatch = computeComatch();
        }
        return this.comatch;
    }

    /** Computes the relation between rule nodes and target graph nodes. */
    private Map<RuleNode,Set<HostNode>> computeComatch() {
        Map<RuleNode,Set<HostNode>> result =
            new HashMap<RuleNode,Set<HostNode>>();
        RuleEvent event = getEvent();
        if (event instanceof BasicEvent) {
            collectComatch(result, (BasicEvent) event);
        } else {
            for (BasicEvent subEvent : ((CompositeEvent) event).getEventSet()) {
                collectComatch(result, subEvent);
            }
        }
        return result;
    }

    private void collectComatch(Map<RuleNode,Set<HostNode>> result,
            BasicEvent event) {
        Rule rule = event.getRule();
        RuleNode[] anchors = rule.getAnchorNodes();
        for (int i = 0; i < anchors.length; i++) {
            HostNode image =
                getMorphism().getNode((HostNode) event.getAnchorImage(i));
            if (image != null) {
                addToComatch(result, anchors[i], image);
            }
        }
        RuleNode[] creators = rule.getCreatorNodes();
        for (int i = 0; i < creators.length; i++) {
            addToComatch(result, creators[i], this.coanchorImage[i]);
        }
    }

    /** Adds a key/value pair to a relational map. */
    private void addToComatch(Map<RuleNode,Set<HostNode>> result,
            RuleNode ruleNode, HostNode hostNode) {
        assert hostNode != null;
        Set<HostNode> image = result.get(ruleNode);
        if (image == null) {
            result.put(ruleNode, image = new HashSet<HostNode>());
        }
        image.add(hostNode);
    }

    /**
     * Matching from the rule's lhs to the source graph.
     */
    private final Rule rule;
    /**
     * The source graph of this derivation. May not be <tt>null</tt>.
     */
    private final HostGraph source;
    /**
     * Matching from the rule's lhs to the source graph.
     */
    private RuleToHostMap anchorMap;
    /**
     * The event from which we get the rule and anchor image.
     */
    private final RuleEvent event;
    /**
     * Mapping from selected RHS elements to target graph. The comatch is
     * constructed in the course of rule application.
     */
    private Map<RuleNode,Set<HostNode>> comatch;
    /**
     * The target graph of this derivation, created lazily in
     * {@link #computeTarget()}.
     */
    private HostGraph target;
    /**
     * Matching from the rule's LHS to the source. Created lazily in
     * {@link #getMatch()}.
     */
    private Proof match;
    /**
     * Underlying morphism from the source to the target.
     */
    private HostGraphMorphism morphism;
    /**
     * The images of the creator nodes. This is part of the information needed
     * to (re)construct the derivation target.
     */
    private HostNode[] coanchorImage;
    /**
     * A mapping from target value nodes of erased edges to their remaining
     * incident edges, used to judge spurious value nodes.
     */
    private Map<ValueNode,Set<HostEdge>> valueNodeEdgesMap;
    /** The set of value nodes that have become isolated due to edge erasure. */
    private Set<ValueNode> isolatedValueNodes;
    /** The set of value nodes that have been added due to edge creation. */
    private Set<ValueNode> addedValueNodes;
    /** The set of value nodes that have been removed due to edge deletion. */
    private Set<ValueNode> removedValueNodes;
    /** The set of edges (to be) erased by this rule applications. */
    private Set<HostEdge> erasedEdges;
    /** 
     * Set of edges that were already in the source graph but were changed
     * in the target because of node mergers.
     */
    private Set<HostEdge> renamedEdges;

    /**
     * Returns the number of nodes that were created during rule application.
     */
    static public int getFreshNodeCount() {
        return freshNodeCount;
    }

    /**
     * The total number of nodes (over all rules) created by {@link BasicEvent}.
     */
    static int freshNodeCount;

    /** Static constant for rules with coanchors. */
    static private final HostNode[] EMPTY_COANCHOR_IMAGE = new HostNode[0];
    /** Reporter for profiling the application class. */
}