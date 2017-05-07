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
 * $Id$
 */
package groove.transform;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;

import groove.grammar.Rule;
import groove.grammar.UnitPar.Direction;
import groove.grammar.host.HostEdge;
import groove.grammar.host.HostEdgeSet;
import groove.grammar.host.HostGraph;
import groove.grammar.host.HostGraphMorphism;
import groove.grammar.host.HostNode;
import groove.grammar.host.HostNodeSet;
import groove.grammar.host.ValueNode;
import groove.grammar.rule.Anchor;
import groove.grammar.rule.AnchorKey;
import groove.grammar.rule.RuleNode;
import groove.match.TreeMatch;
import groove.transform.oracle.ValueOracle;
import groove.util.Property;
import groove.util.Visitor;
import groove.util.Visitor.Finder;

/**
 * Class representing a particular application of a {@link groove.grammar.Rule} and a
 * graph. This is essentially the combination of an existing {@link RuleEvent}, the host graph,
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
     * Constructs a new application on the basis of a given rule and host
     * graph. The target graph is computed.
     * @param event the production rule instance involved
     * @param source the host graph to which the rule is to be applied
     */
    public RuleApplication(RuleEvent event, HostGraph source) {
        this(event, source, (ValueOracle) null);
        assert !event.getRule()
            .getSignature()
            .has(Direction.ASK) : "Rule signature should not have user-provided parameters";
    }

    /**
     * Constructs a new application on the basis of a given rule, host
     * graph and value oracle. The target graph is computed.
     * @param event the production rule instance involved
     * @param source the host graph to which the rule is to be applied
     */
    public RuleApplication(RuleEvent event, HostGraph source, ValueOracle oracle) {
        this(event, source, (HostNode[]) null);
        this.oracle = oracle;
    }

    /**
     * Constructs a new derivation on the basis of a given production rule, host
     * graph and added node set.
     * @param event the production rule instance involved
     * @param source the host graph to which the rule is to be applied
     * @param addedNodes the non-<code>null</code> array of created nodes,
     * in the order of the rule's coanchor. If <code>null</code>, the added nodes are yet to be
     * generated.
     */
    public RuleApplication(final RuleEvent event, HostGraph source, HostNode[] addedNodes) {
        this.event = event;
        this.rule = event.getRule();
        this.source = source;
        this.addedNodes = addedNodes;
        assert testEvent(event, source) : String.format("Event error for %s applied to %s",
            event,
            source);
    }

    /**
     * Reconstructs a derivation on the basis of a given rule event, host
     * graph and target graph, and created nodes.
     * @param event the production rule instance involved
     * @param source the host graph to which the rule is to be applied
     * @param addedNodes the non-<code>null</code> array of created nodes,
     * in the order of the rule's coanchor.
     */
    public RuleApplication(RuleEvent event, HostGraph source, HostGraph target,
        @NonNull HostNode[] addedNodes) {
        this(event, source, addedNodes);
        this.target = target;
    }

    /**
     * Tests if a given event has a match at a given source graph.
     */
    private boolean testEvent(final RuleEvent event, HostGraph source) {
        final Property<Proof> proofContainsEvent = new Property<Proof>() {
            @Override
            public boolean isSatisfied(Proof proof) {
                return event.createEvent(proof)
                    .equals(event);
            }
        };
        final Finder<Proof> eventFinder = Visitor.newFinder(proofContainsEvent);
        final Property<TreeMatch> matchContainsProof = new Property<TreeMatch>() {
            @Override
            public boolean isSatisfied(TreeMatch value) {
                return value.traverseProofs(eventFinder) != null;
            }
        };
        Finder<TreeMatch> matchFinder = Visitor.newFinder(matchContainsProof);
        boolean result = getRule().getEventMatcher(source.isSimple())
            .traverse(source, event.getAnchorMap(), matchFinder) != null;
        eventFinder.dispose();
        matchFinder.dispose();
        return result;
    }

    /**
     * Returns the source graph to which the rule is applied.
     */
    public HostGraph getSource() {
        return this.source;
    }

    /**
     * The source graph of this derivation. May not be <tt>null</tt>.
     */
    private final HostGraph source;

    /**
     * Returns the rule for which this is an application.
     */
    public Rule getRule() {
        return this.rule;
    }

    /**
     * Matching from the rule's lhs to the source graph.
     */
    private final Rule rule;

    /** Returns the (possibly {@code null}) array of added nodes for the rule application. */
    private HostNode[] getAddedNodes() {
        return this.addedNodes;
    }

    /**
     * The images of the creator nodes. This is part of the information needed
     * to (re)construct the derivation target.
     */
    private final HostNode[] addedNodes;

    /** Returns the optional value oracle. */
    private ValueOracle getOracle() {
        return this.oracle;
    }

    private ValueOracle oracle;

    /**
     * Returns a target graph created as a result of the application. The target
     * is created lazily.
     */
    public HostGraph getTarget() {
        if (this.target == null) {
            if (this.rule.isModifying()) {
                this.target = computeTarget();
            } else {
                this.target = getSource();
            }
        }
        return this.target;
    }

    /**
     * Callback factory method to compute a target for this applier.
     */
    protected HostGraph computeTarget() {
        HostGraph target = createTarget();
        applyDelta(target);
        target.setFixed();
        return target;
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
     * The target graph of this derivation, created lazily in
     * {@link #computeTarget()}.
     */
    private HostGraph target;

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
     * Matching from the rule's LHS to the source. Created lazily in
     * {@link #getMatch()}.
     */
    private Proof match;

    /**
     * Returns the transformation morphism underlying this derivation.
     */
    public HostGraphMorphism getMorphism() {
        if (this.morphism == null) {
            this.morphism = computeMorphism(getEffect());
        }
        return this.morphism;
    }

    /**
     * Constructs the morphism between source and target graph from the
     * application.
     */
    private HostGraphMorphism computeMorphism(RuleEffect record) {
        HostGraphMorphism result = createMorphism();
        MergeMap mergeMap = record.getMergeMap();
        // copy the source node and edge set, to avoid modification exceptions
        // in case graph aliasing was used
        HostNodeSet sourceNodes = new HostNodeSet(this.source.nodeSet());
        HostEdgeSet sourceEdges = new HostEdgeSet(this.source.edgeSet());
        for (HostNode node : sourceNodes) {
            HostNode nodeImage = mergeMap == null ? node : mergeMap.getNode(node);
            if (nodeImage != null && getTarget().containsNode(nodeImage)) {
                result.putNode(node, nodeImage);
            }
        }
        for (HostEdge edge : sourceEdges) {
            if (!record.isErasedEdge(edge)) {
                HostEdge edgeImage = mergeMap == null ? edge : mergeMap.mapEdge(edge);
                if (edgeImage != null && getTarget().containsEdge(edgeImage)) {
                    result.putEdge(edge, edgeImage);
                }
            }
        }
        return result;
    }

    /**
     * Callback factory method to create a morphism from source to target graph.
     * Note that this is <i>not</i> the same kind of object as the matching.
     */
    private HostGraphMorphism createMorphism() {
        return getSource().getFactory()
            .createMorphism();
    }

    /**
     * Underlying morphism from the source to the target.
     */
    private HostGraphMorphism morphism;

    private RuleEffect getEffect() {
        RuleEffect result = this.effect;
        if (result == null) {
            // use the predefined created nodes, if available
            if (getAddedNodes() == null) {
                result = new RuleEffect(getSource(), getOracle());
            } else {
                result = new RuleEffect(getSource(), getAddedNodes());
            }
            try {
                getEvent().recordEffect(result);
            } catch (InterruptedException exc) {
                throw new IllegalStateException("By assumption, value oracles are ruled out");
            }
            result.setFixed();
            this.effect = result;
        }
        return result;
    }

    /** The application record. */
    private RuleEffect effect;

    /**
     * Applies the rule to a given delta target. This is presumably the host
     * graph to which the underlying rule is to be applied. The source should
     * coincide with that for which the footprint was originally created
     * @param target the target object on which the modifications are to be
     *        performed
     */
    @Override
    public void applyDelta(DeltaTarget target) {
        if (getRule().isModifying()) {
            RuleEffect record = getEffect();
            removeEdges(record, target);
            removeNodes(record, target);
            addNodes(record, target);
            addEdges(record, target);
            removeIsolatedValueNodes(target);
        }
    }

    /**
     * Erases the images of the reader nodes of the rule.
     * @param target the target to which to apply the changes
     */
    protected void removeNodes(RuleEffect record, DeltaTarget target) {
        if (record.hasRemovedNodes()) {
            for (HostNode node : record.getRemovedNodes()) {
                target.removeNode(node);
            }
        }
    }

    /**
     * Removes those value nodes whose incoming edges have all been erased
     * (and none have been added).
     */
    private void removeIsolatedValueNodes(DeltaTarget target) {
        // for efficiency we don't use the getter but test for null
        if (this.isolatedValueNodes != null) {
            for (ValueNode node : this.isolatedValueNodes) {
                target.removeNode(node);
            }
        }
    }

    /**
     * Performs the edge erasure necessary according to a given application record.
     * @param record object holding the set of edges to be erased
     * @param target the target to which to apply the changes
     */
    private void removeEdges(RuleEffect record, DeltaTarget target) {
        if (record.hasRemovedEdges()) {
            for (HostEdge edge : record.getRemovedEdges()) {
                target.removeEdge(edge);
                // register the removal of an edge pointing to a value node
                HostNode edgeTarget = edge.target();
                if (edgeTarget instanceof ValueNode) {
                    HostEdgeSet edges = getValueNodeEdges((ValueNode) edgeTarget);
                    edges.remove(edge);
                    if (edges.isEmpty()) {
                        registerIsolatedValueNode((ValueNode) edgeTarget);
                    }
                }
            }
        }
    }

    /**
     * Adds nodes to the target graph, as dictated by the rule's RHS.
     *
     * @param target the target to which to apply the changes
     */
    private void addNodes(RuleEffect record, DeltaTarget target) {
        if (record.hasAddedNodes()) {
            for (HostNode node : record.getAddedNodes()) {
                target.addNode(node);
                if (node instanceof ValueNode) {
                    registerAddedValueNode((ValueNode) node);
                }
            }
        }
    }

    /**
     * Adds edges to the target, as dictated by the rule's RHS.
     * @param target the target to which to apply the changes
     */
    private void addEdges(RuleEffect record, DeltaTarget target) {
        if (record.hasAddedEdges()) {
            for (HostEdge edge : record.getAddedEdges()) {
                HostNode targetNode = edge.target();
                if (targetNode instanceof ValueNode) {
                    ValueNode valueNode = (ValueNode) targetNode;
                    if (this.source.containsNode(targetNode)) {
                        unregisterIsolatedValueNode(valueNode);
                    } else if (registerAddedValueNode(valueNode)) {
                        target.addNode(targetNode);
                    }
                }
                target.addEdge(edge);
            }
        }
    }

    /**
     * The hash code is based on the identity of the event.
     */
    @Override
    public int hashCode() {
        return getEvent().hashCode() ^ getSource().hashCode();
    }

    /**
     * Two rule applications are equal if they have the same source and event.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof RuleApplication)) {
            return false;
        }
        RuleApplication other = (RuleApplication) obj;
        return getEvent() == other.getEvent() && getSource() == other.getSource();
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer("Application of rule " + getRule().getQualName());
        result.append("\nEffect: " + getEffect());
        return result.toString();
    }

    /**
     * Returns the event underlying this application.
     */
    public RuleEvent getEvent() {
        return this.event;
    }

    /**
     * The event from which we get the rule and anchor image.
     */
    private final RuleEvent event;

    /**
     * Lazily creates and returns the set of remaining incident edges of a given
     * value node.
     */
    private HostEdgeSet getValueNodeEdges(ValueNode node) {
        if (this.valueNodeEdgesMap == null) {
            this.valueNodeEdgesMap = new HashMap<>();
        }
        HostEdgeSet result = this.valueNodeEdgesMap.get(node);
        if (result == null) {
            result = new HostEdgeSet(this.source.inEdgeSet(node));
            this.valueNodeEdgesMap.put(node, result);
        }
        return result;
    }

    /**
     * A mapping from target value nodes of erased edges to their remaining
     * incident edges, used to judge spurious value nodes.
     */
    private Map<ValueNode,HostEdgeSet> valueNodeEdgesMap;

    /**
     * Adds a node to the set of value nodes that have become isolated
     * due to edge erasure.
     */
    private void registerIsolatedValueNode(ValueNode node) {
        if (this.isolatedValueNodes == null) {
            this.isolatedValueNodes = new HashSet<>();
        }
        this.isolatedValueNodes.add(node);
    }

    /**
     * Removes an isolated node (a new edge to it has been added).
     */
    private void unregisterIsolatedValueNode(ValueNode node) {
        if (this.isolatedValueNodes != null) {
            this.isolatedValueNodes.remove(node);
        }
    }

    /** The set of value nodes that have become isolated due to edge erasure. */
    private Set<ValueNode> isolatedValueNodes;

    /**
     * Registers that a value node has been added.
     * @return {@code true} if this is a newly registered node
     */
    private boolean registerAddedValueNode(ValueNode node) {
        if (this.addedValueNodes == null) {
            this.addedValueNodes = new HashSet<>();
        }
        return this.addedValueNodes.add(node);
    }

    /** The set of value nodes that have been added due to edge creation. */
    private Set<ValueNode> addedValueNodes;

    /** Returns the relation between rule nodes and target graph nodes. */
    public Map<RuleNode,HostNodeSet> getComatch() {
        if (this.comatch == null) {
            this.comatch = computeComatch();
        }
        return this.comatch;
    }

    /** Computes the relation between rule nodes and target graph nodes. */
    private Map<RuleNode,HostNodeSet> computeComatch() {
        Map<RuleNode,HostNodeSet> result = new HashMap<>();
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

    private void collectComatch(Map<RuleNode,HostNodeSet> result, BasicEvent event) {
        Rule rule = event.getRule();
        Anchor anchor = rule.getAnchor();
        for (int i = 0; i < anchor.size(); i++) {
            AnchorKey anchorKey = anchor.get(i);
            if (anchorKey instanceof RuleNode) {
                HostNode anchorValue = (HostNode) event.getAnchorImage(i);
                HostNode image = getMorphism().getNode(anchorValue);
                if (image != null) {
                    addToComatch(result, (RuleNode) anchorKey, image);
                }
            }
        }
        RuleNode[] creators = rule.getCreatorNodes();
        for (int i = 0; i < creators.length; i++) {
            addToComatch(result, creators[i], this.addedNodes[i]);
        }
    }

    /** Adds a key/value pair to a relational map. */
    private void addToComatch(Map<RuleNode,HostNodeSet> result, RuleNode ruleNode,
        HostNode hostNode) {
        assert hostNode != null;
        HostNodeSet image = result.get(ruleNode);
        if (image == null) {
            result.put(ruleNode, image = new HostNodeSet());
        }
        image.add(hostNode);
    }

    /**
     * Mapping from selected RHS elements to target graph. The comatch is
     * constructed in the course of rule application.
     */
    private Map<RuleNode,HostNodeSet> comatch;
}