// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2023 University of Twente

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
package nl.utwente.groove.transform;

import static nl.utwente.groove.transform.RuleEvent.Reuse.EVENT;
import static nl.utwente.groove.transform.RuleEvent.Reuse.NONE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.utwente.groove.algebra.Algebra;
import nl.utwente.groove.algebra.Constant;
import nl.utwente.groove.algebra.ErrorValue;
import nl.utwente.groove.grammar.AnchorKind;
import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.grammar.UnitPar.RulePar;
import nl.utwente.groove.grammar.host.AnchorValue;
import nl.utwente.groove.grammar.host.HostEdge;
import nl.utwente.groove.grammar.host.HostEdgeSet;
import nl.utwente.groove.grammar.host.HostFactory;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.grammar.host.HostNodeSet;
import nl.utwente.groove.grammar.host.ValueNode;
import nl.utwente.groove.grammar.rule.Anchor;
import nl.utwente.groove.grammar.rule.AnchorKey;
import nl.utwente.groove.grammar.rule.LabelVar;
import nl.utwente.groove.grammar.rule.RuleEdge;
import nl.utwente.groove.grammar.rule.RuleNode;
import nl.utwente.groove.grammar.rule.RuleToHostMap;
import nl.utwente.groove.grammar.type.TypeNode;
import nl.utwente.groove.graph.plain.PlainNode;
import nl.utwente.groove.match.TreeMatch;
import nl.utwente.groove.transform.RuleEffect.Fragment;
import nl.utwente.groove.util.Groove;
import nl.utwente.groove.util.cache.CacheReference;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Class representing an instance of an {@link Rule} for a given anchor map.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-03-04 11:01:33 $
 */
final public class BasicEvent extends AbstractRuleEvent<Rule,BasicEvent.BasicEventCache> {
    /**
     * Constructs a new event on the basis of a given production rule and anchor
     * map. A further parameter determines whether information should be stored
     * for reuse.
     * @param rule the production rule involved
     * @param anchorMap map from the rule's LHS elements to the host graph
     * @param reuse if <code>true</code>, the event should store diverse data
     *        structures to optimise for reuse
     */
    public BasicEvent(Rule rule, RuleToHostMap anchorMap, Reuse reuse) {
        super(reference, rule);
        assert anchorMap != null : String
            .format("Can't produce event for %s with null anchor map", rule.getQualName());
        rule.testFixed(true);
        this.anchorImages = computeAnchorImage(anchorMap);
        this.hostFactory = anchorMap.getFactory();
        if (reuse == NONE) {
            this.freshNodeList = NO_REUSE_LIST;
        } else {
            this.freshNodeList = createFreshNodeList();
        }
    }

    /**
     * Returns the derivation record associated with this event. May be
     * <code>null</code>.
     */
    public HostFactory getHostFactory() {
        return this.hostFactory;
    }

    /** The factory for fresh host nodes. */
    private final HostFactory hostFactory;

    /**
     * Returns the store of previously created fresh nodes.
     */
    private List<List<HostNode>> getFreshNodeList() {
        return this.freshNodeList;
    }

    /**
     * The list of nodes created by {@link #createNode(TypeNode)}.
     */
    private final List<List<HostNode>> freshNodeList;

    /**
     * Returns a map from the rule anchors to elements of the host graph.
     * @see Rule#getAnchor()
     */
    @Override
    public RuleToHostMap getAnchorMap() {
        return getCache().getAnchorMap();
    }

    /**
     * Returns a string starting with {@link #ANCHOR_START}, separated by
     * {@link #ANCHOR_SEPARATOR} and ending with {@link #ANCHOR_END}.
     */
    @Override
    public String getAnchorImageString() {
        return Groove.toString(getAnchorImages(), ANCHOR_START, ANCHOR_END, ANCHOR_SEPARATOR);
    }

    /**
     * Constructs a map from the reader nodes of the RHS that are endpoints of
     * creator edges, to the target graph nodes.
     */
    public RuleToHostMap getCoanchorMap() {
        return getCache().getCoanchorMap();
    }

    @Override
    public Reuse getReuse() {
        if (getFreshNodeList() == NO_REUSE_LIST) {
            return NONE;
        } else {
            return EVENT;
        }
    }

    /**
     * Callback method to compute the event hash code.
     */
    @Override
    int computeEventHashCode() {
        int result = getRule().hashCode();
        // we don't use getAnchorImage() because the events are often
        // just created to look up a stored event; then we shouldn't spend too
        // much time on this one
        AnchorValue[] anchorImage = getAnchorImages();
        int MAX_HASHED_ANCHOR_COUNT = 10;
        int hashedAnchorCount = Math.min(anchorImage.length, MAX_HASHED_ANCHOR_COUNT);
        for (int i = 0; i < hashedAnchorCount; i++) {
            AnchorValue elem = anchorImage[i];
            if (elem != null) {
                result += elem.hashCode() << i;
            }
        }
        return result;
    }

    /**
     * Tests if the content of this event coincides with that of the other. The
     * content consists of the rule and the anchor images. Callback method from
     * {@link #equals(Object)}.
     */
    @Override
    boolean equalsEvent(RuleEvent obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BasicEvent other)) {
            return false;
        }
        if (!getRule().equals(obj.getRule())) {
            return false;
        }
        if (!Arrays.equals(getAnchorImages(), other.getAnchorImages())) {
            return false;
        }
        return true;
    }

    @Override
    protected Proof extractProof(TreeMatch match) {
        // this is a simple event, so there are no subrules;
        // the match consists only of the pattern map
        return new Proof(getRule().getCondition(), match.getPatternMap());
    }

    @Override
    public RuleEvent createEvent(Proof proof) {
        return proof.newEvent(null);
    }

    /**
     * Compares two events first on the basis of their rules, then
     * lexicographically on the basis of their anchor images.
     */
    @Override
    public int compareTo(RuleEvent other) {
        int result = getRule().compareTo(other.getRule());
        if (result != 0) {
            return result;
        }
        // we have the same rule (so the other event is also a SPOEvent)
        AnchorValue[] anchorImage = getAnchorImages();
        // retrieve the other even't anchor image array
        AnchorValue[] hisAnchorImage = ((BasicEvent) other).getAnchorImages();
        // now compare the anchor images
        // find the first index in which the anchor images differ
        int upper = Math.min(anchorImage.length, hisAnchorImage.length);
        for (int i = 0; result == 0 && i < upper; i++) {
            result = AnchorKind.compare(anchorImage[i], hisAnchorImage[i]);
        }
        if (result == 0) {
            return anchorImage.length - hisAnchorImage.length;
        } else {
            return result;
        }
    }

    /**
     * Returns the set of source elements that form the anchor image.
     */
    @Override
    public AnchorValue[] getAnchorImages() {
        return this.anchorImages;
    }

    /**
     * The array of source elements that form the anchor image.
     */
    private final AnchorValue[] anchorImages;

    /**
     * Callback method to lazily compute the set of source elements that form
     * the anchor image.
     */
    private AnchorValue[] computeAnchorImage(RuleToHostMap anchorMap) {
        Anchor anchor = getRule().getAnchor();
        AnchorValue[] result = new AnchorValue[anchor.size()];
        for (int i = 0; i < result.length; i++) {
            AnchorKey key = anchor.get(i);
            result[i] = anchorMap.get(key);
            assert result[i] != null : String
                .format("No image for %s in anchor map %s", key, anchorMap);
        }
        return result;
    }

    @Override
    public boolean conflicts(RuleEvent other) {
        boolean result;
        if (other instanceof BasicEvent event) {
            result = false;
            // check if the other creates edges that this event erases
            Iterator<HostEdge> myErasedEdgeIter = getErasedEdges().iterator();
            HostEdgeSet otherCreatedEdges = event.getSimpleCreatedEdges();
            while (!result && myErasedEdgeIter.hasNext()) {
                result = otherCreatedEdges.contains(myErasedEdgeIter.next());
            }
            if (!result) {
                // check if the other erases edges that this event creates
                Iterator<HostEdge> myCreatedEdgeIter = getSimpleCreatedEdges().iterator();
                HostEdgeSet otherErasedEdges = event.getErasedEdges();
                while (!result && myCreatedEdgeIter.hasNext()) {
                    result = otherErasedEdges.contains(myCreatedEdgeIter.next());
                }
            }
        } else {
            result = true;
        }
        return result;
    }

    /**
     * Indicates if this rule event removes a part of the anchor image of
     * another. If so, it means that the other event will not match in any graph
     * reached after this one.
     *
     * @param other the event that we want to establish conflict with
     * @return <code>true</code> if this event disables the other
     */
    public boolean disables(RuleEvent other) {
        boolean result = false;
        Set<AnchorValue> anchorImage = ((BasicEvent) other).getAnchorImageSet();
        // we have conflict if any anchor image node has been erased
        Iterator<HostNode> nodeIter = getErasedNodes().iterator();
        while (!result && nodeIter.hasNext()) {
            result = anchorImage.contains(nodeIter.next());
        }
        // we have conflict if any anchor image edge has been erased
        Iterator<HostEdge> edgeIter = getErasedEdges().iterator();
        while (!result && edgeIter.hasNext()) {
            result = anchorImage.contains(edgeIter.next());
        }
        return result;
    }

    /**
     * Returns the set of source elements that form the anchor image.
     */
    private Set<AnchorValue> getAnchorImageSet() {
        return getCache().getAnchorImageSet();
    }

    @Override
    public void recordEffect(RuleEffect record) throws InterruptedException {
        if (getRule().isModifying()) {
            if (getRule().getCreatorNodes().length > 0) {
                recordCreatedNodes(record);
            }
            if (record.getFragment() != Fragment.NODE_CREATION) {
                if (getRule().getEraserNodes().length > 0) {
                    recordErasedNodes(record);
                }
                if (!getRule().getLhsMergeMap().isEmpty()
                    || !getRule().getRhsMergeMap().isEmpty()) {
                    recordMergeMap(record);
                }
            }
            if (record.getFragment() == Fragment.ALL) {
                if (getRule().getEraserEdges().length > 0) {
                    recordErasedEdges(record);
                }
                if (getRule().getCreatorEdges().length > 0) {
                    recordCreatedEdges(record);
                }
            }
        }
    }

    private void recordErasedNodes(RuleEffect record) {
        record.addErasedNodes(getErasedNodes());
    }

    private void recordErasedEdges(RuleEffect record) {
        record.addErasedEdges(getErasedEdges());
    }

    /** Adds the created nodes to the application record.
     * @throws InterruptedException if an oracle input was cancelled
     */
    private void recordCreatedNodes(RuleEffect record) throws InterruptedException {
        RuleNode[] creatorNodes = getRule().getCreatorNodes();
        if (record.isNodesPredefined()) {
            record.addCreatorNodes(creatorNodes);
        } else {
            HostNode[] createdNodes = getCreatedNodes(record);
            record.addCreatedNodes(creatorNodes, createdNodes);
        }
    }

    /**
     * Adds the created edges to the application record.
     * This should be called only after any nodes have been created.
     */
    private void recordCreatedEdges(RuleEffect record) {
        HostEdgeSet simpleCreatedEdges = getSimpleCreatedEdges();
        record.addCreatedEdges(simpleCreatedEdges);
        Map<RuleNode,HostNode> createdNodeMap = record.getCreatedNodeMap();
        RuleToHostMap anchorMap = getAnchorMap();
        for (RuleEdge edge : getRule().getComplexCreatorEdges()) {
            RuleNode source = edge.source();
            HostNode sourceImage = anchorMap.getNode(source);
            if (sourceImage == null) {
                sourceImage = createdNodeMap.get(source);
                assert sourceImage != null : String
                    .format("Event '%s': No image for %s", this, source);
            }
            RuleNode target = edge.target();
            HostNode targetImage = anchorMap.getNode(target);
            if (targetImage == null) {
                targetImage = createdNodeMap.get(target);
                assert sourceImage != null : String
                    .format("Event '%s': No image for %s", this, target);
            }
            record.addCreateEdge(sourceImage, anchorMap.mapLabel(edge.label()), targetImage);
        }
    }

    /** Records the effect of node merging in the application record. */
    private void recordMergeMap(RuleEffect record) {
        MergeMap lhsMergeMap = getCache().getMergeMap();
        Map<RuleNode,RuleNode> rhsMergers = getRule().getRhsMergeMap();
        if (rhsMergers.isEmpty()) {
            record.addMergeMap(lhsMergeMap);
        } else {
            MergeMap rhsMergeMap = lhsMergeMap.clone();
            RuleToHostMap anchorMap = getAnchorMap();
            Map<RuleNode,HostNode> createdNodeMap = record.getCreatedNodeMap();
            for (Map.Entry<RuleNode,RuleNode> rhsMergeEntry : rhsMergers.entrySet()) {
                RuleNode ruleSource = rhsMergeEntry.getKey();
                RuleNode ruleTarget = rhsMergeEntry.getValue();
                HostNode source = anchorMap.getNode(ruleSource);
                if (source == null) {
                    source = createdNodeMap.get(ruleSource);
                }
                HostNode target = anchorMap.getNode(ruleTarget);
                if (target == null) {
                    target = createdNodeMap.get(ruleTarget);
                }
                rhsMergeMap.putNode(source, target);
            }
            record.addMergeMap(rhsMergeMap);
        }
    }

    /**
     * Returns the set of explicitly erased nodes, i.e., the images of the LHS
     * eraser nodes.
     */
    private HostNodeSet getErasedNodes() {
        return getCache().getErasedNodes();
    }

    /**
     * Computes the set of explicitly erased nodes, i.e., the images of the LHS
     * eraser nodes. Callback method from {@link #getErasedNodes()}.
     */
    private HostNodeSet computeErasedNodes() {
        if (getRule().getEraserNodes().length == 0) {
            return EMPTY_NODE_SET;
        } else {
            HostNodeSet result = createNodeSet();
            RuleToHostMap anchorMap = getAnchorMap();
            // register the node erasures
            for (RuleNode node : getRule().getEraserNodes()) {
                result.add(anchorMap.getNode(node));
            }
            return result;
        }
    }

    /**
     * Returns the set of explicitly erased edges, i.e., the images of the LHS
     * eraser edges.
     */
    private HostEdgeSet getErasedEdges() {
        return getCache().getErasedEdges();
    }

    /**
     * Computes the set of explicitly erased edges, i.e., the images of the LHS
     * eraser edges. Callback method from {@link #getErasedEdges()}.
     */
    private HostEdgeSet computeErasedEdges() {
        HostEdgeSet result = createEdgeSet();
        RuleToHostMap anchorMap = getAnchorMap();
        RuleEdge[] eraserEdges = getRule().getEraserEdges();
        for (RuleEdge edge : eraserEdges) {
            HostEdge edgeImage = anchorMap.getEdge(edge);
            assert edgeImage != null : "Image of " + edge + " cannot be deduced from " + anchorMap;
            result.add(edgeImage);
        }
        return result;
    }

    /**
     * Returns the set of images of the LHS
     * creator edges.
     */
    private HostEdgeSet getSimpleCreatedEdges() {
        return getCache().getSimpleCreatedEdges();
    }

    /**
     * Computes the set of images of the LHS
     * creator edges. Callback method from {@link #getSimpleCreatedEdges()}.
     */
    private HostEdgeSet computeSimpleCreatedEdges() {
        HostEdgeSet result = createEdgeSet();
        RuleToHostMap coAnchorMap = getCoanchorMap();
        for (RuleEdge edge : getRule().getSimpleCreatorEdges()) {
            HostEdge edgeImage = coAnchorMap.mapEdge(edge);
            if (edgeImage != null) {
                result.add(edgeImage);
            }
        }
        return result;
    }

    /** Computes an array of created nodes that are fresh both
     * with respect to a given set of source graph nodes and with respect
     * to a set of nodes that were already created.
     * @param record object in which the effect of this {@link BasicEvent} (and possibly others) is recorded
     * @return array of fresh nodes, in the order of the node creators
     * @throws InterruptedException if an oracle input was cancelled
     */
    private HostNode[] getCreatedNodes(RuleEffect record) throws InterruptedException {
        HostNode[] result;
        RuleNode[] creatorNodes = getRule().getCreatorNodes();
        int count = creatorNodes.length;
        if (count == 0) {
            result = AbstractRuleEvent.EMPTY_NODE_ARRAY;
        } else {
            result = new HostNode[count];
            for (int i = 0; i < count; i++) {
                var creatorNode = creatorNodes[i];
                var typeGuards = creatorNode.getTypeGuards();
                TypeNode type = typeGuards.isEmpty()
                    ? creatorNode.getType()
                    : (TypeNode) getCoanchorMap()
                        .getVar(creatorNode.getTypeGuards().get(0).getVar());
                if (type.isSort()) {
                    // value nodes can only be created if they are ask parameters
                    result[i] = createValueNode(record, creatorNode.getPar().get());
                } else {
                    result[i] = createNode(record, i, type);
                }
            }
            // normalise the result to a previously stored instance
            if (getReuse() != NONE) {
                result = getHostFactory().normalise(result);
            }
        }
        return result;
    }

    /**
     * Creates a value node by querying the oracle.
     * @throws InterruptedException if an oracle input was cancelled
     */
    private ValueNode createValueNode(RuleEffect record, RulePar par) throws InterruptedException {
        var sort = par.getType().getSort();
        Algebra<?> alg = getAction().getGrammarProperties().getAlgebraFamily().getAlgebra(sort);
        Object value;
        try {
            Constant c = record.getOracle().getValue(record.getSource(), this, par);
            value = alg.toValueFromConstant(c);
        } catch (FormatException exc) {
            value = new ErrorValue(sort, exc);
        }
        return record.getSource().getFactory().createNode(alg, value);
    }

    /**
     * Adds a node that is fresh with respect to a given graph to a collection
     * of already added nodes. The previously created fresh nodes are tried first
     * (see {@link BasicEvent#getFreshNodes(int)}; only if all of those are
     * already in the graph, a new fresh node is created using
     * {@link #createNode(TypeNode)}.
     * @param record the rule effect with respect to which the node should be fresh
     * @param creatorIndex index in the creator nodes array indicating the node
     *        of the rule for which a new image is to be created
     * @param type type of the node to be created
     */
    private HostNode createNode(RuleEffect record, int creatorIndex, TypeNode type) {
        HostNode result = null;
        boolean added = false;
        List<HostNode> previous = getFreshNodes(creatorIndex);
        if (previous != null) {
            int previousCount = previous.size();
            for (int i = 0; !added && i < previousCount; i++) {
                result = previous.get(i);
                added = !record.containsNode(result);
            }
        }
        if (!added) {
            result = createNode(type);
            if (previous != null) {
                previous.add(result);
            }
        }
        assert result != null;
        return result;
    }

    /**
     * Creates an array of lists to store the fresh nodes created by this rule.
     */
    private List<List<HostNode>> createFreshNodeList() {
        int creatorNodeCount = getRule().getCreatorNodes().length;
        List<List<HostNode>> result = new ArrayList<>();
        for (int i = 0; i < creatorNodeCount; i++) {
            result.add(new ArrayList<>());
        }
        return result;
    }

    /**
     * Callback factory method for a newly constructed node. This implementation
     * returns a {@link PlainNode}, with a node number determined by the
     * grammar's node counter.
     */
    private HostNode createNode(TypeNode type) {
        BasicEvent.freshNodeCount++;
        HostFactory record = getHostFactory();
        return record.nodes(type).createNode();
    }

    /**
     * Returns the list of all previously created fresh nodes. Returns
     * <code>null</code> if the reuse policy is set to <code>false</code>.
     */
    private List<HostNode> getFreshNodes(int creatorIndex) {
        if (getReuse() == EVENT) {
            return getFreshNodeList().get(creatorIndex);
        } else {
            return null;
        }
    }

    @Override
    protected BasicEventCache createCache() {
        return new BasicEventCache();
    }

    /**
     * Returns the number of nodes that were created during rule application.
     */
    static public int getFreshNodeCount() {
        return freshNodeCount;
    }

    /**
     * The total number of nodes (over all rules) created by {@link BasicEvent}.
     */
    private static int freshNodeCount;

    /**
     * The start string of the anchor image description.
     * @see #getAnchorImageString()
     */
    static public final String ANCHOR_START = "(";
    /**
     * The string separating the elements in the anchor image description.
     * @see #getAnchorImageString()
     */
    static public final String ANCHOR_SEPARATOR = ",";
    /**
     * The end string of the anchor image description.
     * @see #getAnchorImageString()
     */
    static public final String ANCHOR_END = ")";
    /** Global empty set of nodes. */
    static private final HostNodeSet EMPTY_NODE_SET = new HostNodeSet(0);
    /** Value for {@link #freshNodeList} that indicates {@link #NONE} mode. */
    static private List<List<HostNode>> NO_REUSE_LIST = new ArrayList<>();
    /** Template reference to create empty caches. */
    static private final CacheReference<BasicEventCache> reference
        = CacheReference.<BasicEventCache>newInstance(false);

    /** Cache holding auxiliary data structures for the event. */
    final class BasicEventCache extends AbstractRuleEvent<Rule,BasicEventCache>.AbstractEventCache {
        /**
         * @return Returns the anchorMap.
         */
        final RuleToHostMap getAnchorMap() {
            if (this.anchorMap == null) {
                this.anchorMap = computeAnchorMap();
            }
            return this.anchorMap;
        }

        /**
         * Creates the normalised anchor map from the currently stored anchor
         * map. The resulting map contains images for the anchor and eraser
         * edges and any variables on them.
         */
        private RuleToHostMap computeAnchorMap() {
            Anchor anchor = getRule().getAnchor();
            AnchorValue[] anchorImage = getAnchorImages();
            RuleToHostMap result = createRuleToHostMap();
            for (int i = 0; i < anchor.size(); i++) {
                result.put(anchor.get(i), anchorImage[i]);
            }
            // add the eraser edges
            for (RuleEdge eraserEdge : getRule().getEraserNonAnchorEdges()) {
                HostEdge eraserImage = result.mapEdge(eraserEdge);
                assert eraserImage != null : String
                    .format("Eraser edge %s has no image in anchor map %s", eraserEdge, result);
                // result.putEdge(eraserEdge, eraserImage);
            }
            return result;
        }

        /**
         * Matching from the rule's lhs to the source graph.
         */
        private RuleToHostMap anchorMap;

        /**
         * Returns the set of source elements that form the anchor image.
         */
        Set<AnchorValue> getAnchorImageSet() {
            if (this.anchorImageSet == null) {
                RuleToHostMap anchorMap = getAnchorMap();
                this.anchorImageSet = new HashSet<>(anchorMap.nodeMap().values());
                this.anchorImageSet.addAll(anchorMap.edgeMap().values());
            }
            return this.anchorImageSet;
        }

        /**
         * The set of source elements that form the anchor image.
         */
        private Set<AnchorValue> anchorImageSet;

        /**
         * Constructs a map from the reader nodes of the RHS that are endpoints
         * of creator edges, to the target graph nodes.
         */
        final RuleToHostMap getCoanchorMap() {
            if (this.coanchorMap == null) {
                this.coanchorMap = computeCoanchorMap();
            }
            return this.coanchorMap;
        }

        /**
         * Constructs a map from the reader nodes of the RHS that are endpoints
         * of creator edges, to the target graph nodes.
         */
        private RuleToHostMap computeCoanchorMap() {
            final RuleToHostMap result = createRuleToHostMap();
            RuleToHostMap anchorMap = getAnchorMap();
            // add coanchor mappings for creator edge ends that are themselves
            // not creators
            for (RuleNode creatorEnd : getRule().getCreatorEnds()) {
                HostNode createdValue;
                createdValue = anchorMap.getNode(creatorEnd);
                assert createdValue != null : String
                    .format("Event '%s': No coanchor image for '%s' in %s", BasicEvent.this,
                            creatorEnd, anchorMap);
                // if the value is null, the image was deleted due to a delete
                // conflict
                // or it is yet to be created by a parent rule
                if (!getErasedNodes().contains(createdValue)) {
                    result.putNode(creatorEnd, createdValue);
                }
            }
            // add variable images
            for (LabelVar var : getRule().getCreatorVars()) {
                result.putVar(var, anchorMap.getVar(var));
            }
            return result;
        }

        /**
         * Matching from the rule's rhs to the target graph.
         */
        private RuleToHostMap coanchorMap;

        /**
         * Returns a mapping from source to source graph nodes, dictated by the
         * LHS mergers and erasers in the rule.
         * @return an {@link MergeMap} that maps nodes of the source that are
         *         merged away to their merged images, and the erased nodes to
         *         {@code null}.
         */
        final MergeMap getMergeMap() {
            if (this.mergeMap == null) {
                this.mergeMap = computeMergeMap();
            }
            return this.mergeMap;
        }

        /**
         * Callback method from {@link #getMergeMap()} to compute the merge map.
         * This is constructed on the basis of matching and rule, without
         * reference to the actual target graph, which indeed may not yet be
         * constructed at the time of invoking this method. The map is an
         * {@link MergeMap} to improve performance.
         */
        private MergeMap computeMergeMap() {
            RuleToHostMap anchorMap = getAnchorMap();
            MergeMap mergeMap = createMergeMap();
            for (Map.Entry<RuleNode,RuleNode> ruleMergeEntry : getRule()
                .getLhsMergeMap()
                .entrySet()) {
                HostNode mergeKey = anchorMap.getNode(ruleMergeEntry.getKey());
                HostNode mergeImage = anchorMap.getNode(ruleMergeEntry.getValue());
                mergeMap.putNode(mergeKey, mergeImage);
            }
            // now map the erased nodes to null
            for (HostNode node : this.getErasedNodes()) {
                mergeMap.removeNode(node);
            }
            return mergeMap;
        }

        /**
         * Minimal mapping from the source graph to target graph to reconstruct
         * the underlying morphism. The merge map is constructed in the course
         * of rule application.
         */
        private MergeMap mergeMap;

        /**
         * Returns the pre-computed and cached set of explicitly erased edges.
         */
        final HostEdgeSet getErasedEdges() {
            if (this.erasedEdgeSet == null) {
                this.erasedEdgeSet = computeErasedEdges();
            }
            return this.erasedEdgeSet;
        }

        /**
         * Set of edges from the source that are to be erased in the target.
         */
        private HostEdgeSet erasedEdgeSet;

        /**
         * Returns the pre-computed and cached set of explicitly erased edges.
         */
        final HostEdgeSet getSimpleCreatedEdges() {
            if (this.simpleCreatedEdgeSet == null) {
                this.simpleCreatedEdgeSet = computeSimpleCreatedEdges();
            }
            return this.simpleCreatedEdgeSet;
        }

        /**
         * Images of the simple creator edges.
         */
        private HostEdgeSet simpleCreatedEdgeSet;

        /**
         * Callback factory method to create the merge map object for
         * {@link #computeMergeMap()}.
         *
         * @return a fresh instance of {@link MergeMap}
         */
        private MergeMap createMergeMap() {
            return new MergeMap(getHostFactory());
        }

        /** Returns the cached set of nodes erased by the event. */
        final HostNodeSet getErasedNodes() {
            if (this.erasedNodeSet == null) {
                this.erasedNodeSet = computeErasedNodes();
            }
            return this.erasedNodeSet;
        }

        /**
         * Set of nodes from the source that are to be erased in the target.
         */
        private HostNodeSet erasedNodeSet;

        /**
         * Callback factory method to create the rule-to-host map.
         * @return a fresh instance of {@link RuleToHostMap}
         */
        private RuleToHostMap createRuleToHostMap() {
            return getHostFactory().createRuleToHostMap();
        }
    }
}