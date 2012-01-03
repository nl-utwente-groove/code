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
 * $Id: SPOEvent.java,v 1.55 2008-03-04 11:01:33 fladder Exp $
 */
package groove.trans;

import groove.graph.DefaultNode;
import groove.graph.Node;
import groove.graph.TypeGuard;
import groove.graph.TypeLabel;
import groove.graph.algebra.ValueNode;
import groove.rel.LabelVar;
import groove.trans.RuleEffect.Fragment;
import groove.util.CacheReference;
import groove.util.Groove;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class representing an instance of an {@link Rule} for a given anchor map.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-03-04 11:01:33 $
 */
final public class BasicEvent extends
        AbstractEvent<Rule,BasicEvent.SPOEventCache> {
    /**
     * Constructs a new event on the basis of a given production rule and anchor
     * map. A further parameter determines whether information should be stored
     * for reuse.
     * @param rule the production rule involved
     * @param anchorMap map from the rule's LHS elements to the host graph
     * @param reuse if <code>true</code>, the event should store diverse data
     *        structures to optimise for reuse
     */
    public BasicEvent(Rule rule, RuleToHostMap anchorMap, boolean reuse) {
        super(reference, rule, reuse);
        assert anchorMap != null : String.format(
            "Can't produce event for %s with null anchor map", rule.getName());
        rule.testFixed(true);
        this.anchorImage = computeAnchorImage(anchorMap);
        this.hostFactory = anchorMap.getFactory();
    }

    /**
     * Returns a map from the rule anchors to elements of the host graph. 
     * @see Rule#getAnchorNodes()
     * @see Rule#getAnchorEdges()
     */
    public RuleToHostMap getAnchorMap() {
        return getCache().getAnchorMap();
    }

    /**
     * Returns a string starting with {@link #ANCHOR_START}, separated by
     * {@link #ANCHOR_SEPARATOR} and ending with {@link #ANCHOR_END}.
     */
    public String getAnchorImageString() {
        return Groove.toString(getAnchorImage(), ANCHOR_START, ANCHOR_END,
            ANCHOR_SEPARATOR);
    }

    /**
     * Constructs a map from the reader nodes of the RHS that are endpoints of
     * creator edges, to the target graph nodes.
     */
    public RuleToHostMap getCoanchorMap() {
        return getCache().getCoanchorMap();
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
        HostElement[] anchorImage = getAnchorImage();
        int MAX_HASHED_ANCHOR_COUNT = 10;
        int hashedAnchorCount =
            Math.min(anchorImage.length, MAX_HASHED_ANCHOR_COUNT);
        for (int i = 0; i < hashedAnchorCount; i++) {
            HostElement elem = anchorImage[i];
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
    boolean equalsEvent(RuleEvent other) {
        return this == other
            || other instanceof BasicEvent
            && getRule().equals(other.getRule())
            && Arrays.equals(getAnchorImage(),
                ((BasicEvent) other).getAnchorImage());
    }

    /**
     * Constructs an argument array for this event, with respect to
     *  given array of added nodes (which are the images of the creator nodes).
     * @param addedNodes the added nodes; if {@code null}, the creator
     * node images will be set to {@code null}
     */
    @Override
    HostNode[] getArguments(HostNode[] addedNodes) {
        HostNode[] result;
        int size = getRule().getSignature().size();
        if (size == 0) {
            result = AbstractEvent.EMPTY_NODE_ARRAY;
        } else {
            result = new HostNode[size];
            int anchorNodeCount = getRule().getAnchorNodes().length;
            HostElement[] anchorImage = getAnchorImage();
            for (int i = 0; i < size; i++) {
                int binding = getRule().getParBinding(i);
                HostNode argument;
                if (binding < anchorNodeCount) {
                    argument = (HostNode) anchorImage[binding];
                } else if (addedNodes == null) {
                    argument = null;
                } else {
                    argument = addedNodes[binding - anchorNodeCount];
                }
                result[i] = argument;
            }
        }
        return result;
    }

    /**
     * Computes a match based on the precomputed anchor map.
     */
    public Proof getMatch(HostGraph host) {
        Proof result = null;
        if (isCorrectFor(host)) {
            result = getRule().getEventMatch(this, host);
        }
        return result;
    }

    /**
     * Compares two events first on the basis of their rules, then
     * lexicographically on the basis of their anchor images.
     */
    public int compareTo(RuleEvent other) {
        int result = getRule().compareTo(other.getRule());
        if (result != 0) {
            return result;
        }
        // we have the same rule (so the other event is also a SPOEvent)
        HostElement[] anchorImage = getAnchorImage();
        // retrieve the other even't anchor image array
        HostElement[] otherAnchorImage = ((BasicEvent) other).getAnchorImage();
        // now compare the anchor images
        // find the first index in which the anchor images differ
        int upper = Math.min(anchorImage.length, otherAnchorImage.length);
        for (int i = 0; result == 0 && i < upper; i++) {
            if (anchorImage[i] != null) {
                result = anchorImage[i].compareTo(otherAnchorImage[i]);
            }
        }
        if (result == 0) {
            return anchorImage.length - otherAnchorImage.length;
        } else {
            return result;
        }
    }

    /**
     * Tests if the anchor map fits into a given host graph.
     * @param host the graph to be tested
     * @return <code>true</code> if the anchor map images are all in
     *         <code>host</code>
     */
    private boolean isCorrectFor(HostGraph host) {
        RuleToHostMap anchorMap = getAnchorMap();
        boolean correct = true;
        Iterator<? extends HostEdge> edgeImageIter =
            anchorMap.edgeMap().values().iterator();
        while (correct && edgeImageIter.hasNext()) {
            correct = host.containsEdge(edgeImageIter.next());
        }
        if (correct) {
            Iterator<? extends HostNode> nodeImageIter =
                anchorMap.nodeMap().values().iterator();
            while (correct && nodeImageIter.hasNext()) {
                HostNode nodeImage = nodeImageIter.next();
                correct =
                    nodeImage instanceof ValueNode
                        || host.containsNode(nodeImage);
            }
        }
        return correct;
    }

    @Override
    public HostElement getAnchorImage(int i) {
        return getAnchorImage()[i];
    }

    /**
     * Returns the set of source elements that form the anchor image.
     */
    HostElement[] getAnchorImage() {
        return this.anchorImage;
    }

    /**
     * Callback method to lazily compute the set of source elements that form
     * the anchor image.
     */
    private HostElement[] computeAnchorImage(RuleToHostMap anchorMap) {
        RuleNode[] anchorNodes = getRule().getAnchorNodes();
        RuleEdge[] anchorEdges = getRule().getAnchorEdges();
        HostElement[] result =
            new HostElement[anchorNodes.length + anchorEdges.length];
        for (int i = 0; i < anchorNodes.length; i++) {
            result[i] = anchorMap.getNode(anchorNodes[i]);
            assert result[i] != null : String.format(
                "No image for %s in anchor map %s", anchorNodes[i], anchorMap);
        }
        for (int i = 0; i < anchorEdges.length; i++) {
            result[anchorNodes.length + i] = anchorMap.getEdge(anchorEdges[i]);
            assert result[anchorNodes.length + i] != null : String.format(
                "No image for %s in anchor map %s", anchorEdges[i], anchorMap);
        }
        return result;
    }

    public boolean conflicts(RuleEvent other) {
        boolean result;
        if (other instanceof BasicEvent) {
            result = false;
            // check if the other creates edges that this event erases
            Iterator<HostEdge> myErasedEdgeIter = getErasedEdges().iterator();
            Set<HostEdge> otherCreatedEdges =
                ((BasicEvent) other).getSimpleCreatedEdges();
            while (!result && myErasedEdgeIter.hasNext()) {
                result = otherCreatedEdges.contains(myErasedEdgeIter.next());
            }
            if (!result) {
                // check if the other erases edges that this event creates
                Iterator<HostEdge> myCreatedEdgeIter =
                    getSimpleCreatedEdges().iterator();
                Set<HostEdge> otherErasedEdges =
                    ((BasicEvent) other).getErasedEdges();
                while (!result && myCreatedEdgeIter.hasNext()) {
                    result =
                        otherErasedEdges.contains(myCreatedEdgeIter.next());
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
        Set<HostElement> anchorImage = ((BasicEvent) other).getAnchorImageSet();
        Iterator<HostNode> nodeIter = getErasedNodes().iterator();
        while (!result && nodeIter.hasNext()) {
            result = anchorImage.contains(nodeIter.next());
        }
        Iterator<HostEdge> edgeIter = getErasedEdges().iterator();
        while (!result && edgeIter.hasNext()) {
            result = anchorImage.contains(edgeIter.next());
        }
        return result;
    }

    /**
     * Returns the set of source elements that form the anchor image.
     */
    private Set<HostElement> getAnchorImageSet() {
        if (this.anchorImageSet == null) {
            RuleToHostMap anchorMap = getAnchorMap();
            this.anchorImageSet =
                new HashSet<HostElement>(anchorMap.nodeMap().values());
            this.anchorImageSet.addAll(anchorMap.edgeMap().values());
        }
        return this.anchorImageSet;
    }

    /**
     * Records the application of this event, by storing the relevant
     * information into the record object passed in as a parameter.
     */
    @Override
    public void recordEffect(RuleEffect record) {
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

    /** Adds the created nodes to the application record. */
    private void recordCreatedNodes(RuleEffect record) {
        RuleNode[] creatorNodes = getRule().getCreatorNodes();
        if (record.isNodesInitialised()) {
            record.addCreatorNodes(creatorNodes);
        } else {
            HostNode[] createdNodes =
                getCreatedNodes(record.getSourceNodes(),
                    record.getCreatedNodes());
            record.addCreatedNodes(creatorNodes, createdNodes);
        }
    }

    /** 
     * Adds the created edges to the application record.
     * This should be called only after any nodes have been created.
     */
    private void recordCreatedEdges(RuleEffect record) {
        Set<HostEdge> simpleCreatedEdges = getSimpleCreatedEdges();
        record.addCreatedEdges(simpleCreatedEdges);
        Map<RuleNode,HostNode> createdNodeMap = record.getCreatedNodeMap();
        RuleToHostMap anchorMap = getAnchorMap();
        for (RuleEdge edge : getRule().getComplexCreatorEdges()) {
            RuleNode source = edge.source();
            HostNode sourceImage = anchorMap.getNode(source);
            if (sourceImage == null) {
                sourceImage = createdNodeMap.get(source);
                assert sourceImage != null : String.format(
                    "Event '%s': No image for %s", this, source);
            }
            RuleNode target = edge.target();
            HostNode targetImage = anchorMap.getNode(target);
            if (targetImage == null) {
                targetImage = createdNodeMap.get(target);
                assert sourceImage != null : String.format(
                    "Event '%s': No image for %s", this, target);
            }
            HostEdge image =
                getHostFactory().createEdge(sourceImage,
                    anchorMap.mapLabel(edge.label()), targetImage);
            record.addCreatedEdge(image);
        }
    }

    /** Adds the created nodes to the application record. */
    private void recordMergeMap(RuleEffect record) {
        MergeMap lhsMergeMap = getCache().getMergeMap();
        Map<RuleNode,RuleNode> rhsMergers = getRule().getRhsMergeMap();
        if (rhsMergers.isEmpty()) {
            record.addMergeMap(lhsMergeMap);
        } else {
            MergeMap rhsMergeMap = new MergeMap(lhsMergeMap.getFactory());
            rhsMergeMap.putAll(lhsMergeMap);
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
    private Set<HostNode> getErasedNodes() {
        if (isReuse()) {
            return getCache().getErasedNodes();
        } else {
            return computeErasedNodes();
        }
    }

    /**
     * Computes the set of explicitly erased nodes, i.e., the images of the LHS
     * eraser nodes. Callback method from {@link #getErasedNodes()}.
     */
    private Set<HostNode> computeErasedNodes() {
        if (getRule().getEraserNodes().length == 0) {
            return EMPTY_NODE_SET;
        } else {
            Set<HostNode> result = createNodeSet();
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
    private Set<HostEdge> getErasedEdges() {
        if (isReuse()) {
            return getCache().getErasedEdges();
        } else {
            return computeErasedEdges();
        }
    }

    /**
     * Computes the set of explicitly erased edges, i.e., the images of the LHS
     * eraser edges. Callback method from {@link #getErasedEdges()}.
     */
    private Set<HostEdge> computeErasedEdges() {
        Set<HostEdge> result = createEdgeSet();
        RuleToHostMap anchorMap = getAnchorMap();
        RuleEdge[] eraserEdges = getRule().getEraserEdges();
        for (RuleEdge edge : eraserEdges) {
            HostEdge edgeImage = anchorMap.getEdge(edge);
            assert edgeImage != null : "Image of " + edge
                + " cannot be deduced from " + anchorMap;
            result.add(edgeImage);
        }
        return result;
    }

    /**
     * Returns the set of images of the LHS
     * creator edges.
     */
    private Set<HostEdge> getSimpleCreatedEdges() {
        if (isReuse()) {
            return getCache().getSimpleCreatedEdges();
        } else {
            return computeSimpleCreatedEdges();
        }
    }

    /**
     * Computes the set of images of the LHS
     * creator edges. Callback method from {@link #getSimpleCreatedEdges()}.
     */
    private Set<HostEdge> computeSimpleCreatedEdges() {
        Set<HostEdge> result = createEdgeSet();
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
     * @param sourceNodes the set of nodes in the source graph
     * @param added the set of nodes already added with respect to the source graph
     * @return array of fresh nodes, in the order of the node creators
     */
    private HostNode[] getCreatedNodes(Set<? extends HostNode> sourceNodes,
            Collection<HostNode> added) {
        HostNode[] result;
        RuleNode[] creatorNodes = getRule().getCreatorNodes();
        int count = creatorNodes.length;
        if (count == 0) {
            result = AbstractEvent.EMPTY_NODE_ARRAY;
        } else {
            result = new HostNode[count];
            if (added == null && this.doAggressiveNodeReuse) {
                added = new ArrayList<HostNode>();
            }
            for (int i = 0; i < count; i++) {
                TypeLabel label;
                if (creatorNodes[i].getTypeGuards().isEmpty()) {
                    label = creatorNodes[i].getType().label();
                } else {
                    // get the type from the image of the first label variable
                    label =
                        getCoanchorMap().getVar(
                            creatorNodes[i].getTypeGuards().get(0).getVar()).label();
                }
                result[i] = createNode(i, label, sourceNodes, added);
            }
        }
        // normalise the result to a previously stored instance
        if (isReuse()) {
            if (this.coanchorImageMap == null) {
                this.coanchorImageMap =
                    new HashMap<List<HostNode>,HostNode[]>();
            }
            List<HostNode> resultAsList = Arrays.asList(result);
            HostNode[] existingResult = this.coanchorImageMap.get(resultAsList);
            if (existingResult == null) {
                this.coanchorImageMap.put(resultAsList, result);
                coanchorImageCount++;
            } else {
                result = existingResult;
                coanchorImageOverlap++;
            }
        }
        return result;
    }

    /**
     * Adds a node that is fresh with respect to a given graph to a collection
     * of already added nodes. The previously created fresh nodes are tried first
     * (see {@link BasicEvent#getFreshNodes(int)}; only if all of those are
     * already in the graph, a new fresh node is created using
     * {@link #createNode(TypeLabel)}.
     * @param creatorIndex index in the creator nodes array indicating the node
     *        of the rule for which a new image is to be created
     * @param sourceNodes the existing nodes, which should not contain the
     *        fresh node
     * @param current the collection of already added nodes; the newly added node
     *        is guaranteed to be fresh with respect to these
     */
    private HostNode createNode(int creatorIndex, TypeLabel type,
            Set<? extends HostNode> sourceNodes, Collection<HostNode> current) {
        HostNode result = null;
        boolean added = false;
        List<HostNode> previous = getFreshNodes(creatorIndex);
        if (previous != null) {
            int previousCount = previous.size();
            for (int i = 0; !added && i < previousCount; i++) {
                result = previous.get(i);
                added =
                    !sourceNodes.contains(result)
                        && (current == null || current.add(result));
            }
        }
        if (!added) {
            if (this.doAggressiveNodeReuse) {
                result = getFreshNode(sourceNodes, current, type);
            } else {
                result = createNode(type);
            }
            if (current != null) {
                current.add(result);
            }
            if (previous != null) {
                previous.add(result);
            }
        }
        assert result != null;
        return result;
    }

    /** Basic setter method. */
    public void setAggressiveNodeReuse() {
        this.doAggressiveNodeReuse = true;
    }

    private HostNode getFreshNode(Set<? extends HostNode> sourceNodes,
            Collection<HostNode> current, TypeLabel type) {
        int size = sourceNodes.size();
        if (current != null) {
            size += current.size();
        }
        int numbers[] = new int[size];
        int i = 0;
        for (Node node : sourceNodes) {
            numbers[i] = node.getNumber();
            i++;
        }
        if (current != null) {
            for (Node node : current) {
                numbers[i] = node.getNumber();
                i++;
            }
        }
        assert i == numbers.length;
        return getHostFactory().createNode(type, numbers);
    }

    /**
     * Creates an array of lists to store the fresh nodes created by this rule.
     */
    private List<List<HostNode>> createFreshNodeList() {
        int creatorNodeCount = getRule().getCreatorNodes().length;
        List<List<HostNode>> result = new ArrayList<List<HostNode>>();
        for (int i = 0; i < creatorNodeCount; i++) {
            result.add(new ArrayList<HostNode>());
        }
        return result;
    }

    /**
     * Callback factory method for a newly constructed node. This implementation
     * returns a {@link DefaultNode}, with a node number determined by the
     * grammar's node counter.
     */
    private HostNode createNode(TypeLabel type) {
        BasicEvent.freshNodeCount++;
        HostFactory record = getHostFactory();
        return record.createNode(type);
    }

    /**
     * Returns the derivation record associated with this event. May be
     * <code>null</code>.
     */
    public HostFactory getHostFactory() {
        return this.hostFactory;
    }

    /**
     * Returns the list of all previously created fresh nodes. Returns
     * <code>null</code> if the reuse policy is set to <code>false</code>.
     */
    private List<HostNode> getFreshNodes(int creatorIndex) {
        if (isReuse()) {
            if (this.freshNodeList == null) {
                this.freshNodeList = createFreshNodeList();
            }
            return this.freshNodeList.get(creatorIndex);
        } else {
            return null;
        }
    }

    /**
     * Callback factory method to create the rule-to-host map.
     * @return a fresh instance of {@link RuleToHostMap}
     */
    private RuleToHostMap createRuleToHostMap() {
        return getHostFactory().createRuleToHostMap();
    }

    @Override
    protected SPOEventCache createCache() {
        return new SPOEventCache();
    }

    /**
     * Returns the number of nodes that were created during rule application.
     */
    static public int getFreshNodeCount() {
        return freshNodeCount;
    }

    /** The derivation record that has created this event, if any. */
    private final HostFactory hostFactory;
    /**
     * The set of source elements that form the anchor image.
     */
    private Set<HostElement> anchorImageSet;
    /**
     * The array of source elements that form the anchor image.
     */
    private final HostElement[] anchorImage;
    /**
     * The list of nodes created by {@link #createNode(TypeLabel)}.
     */
    private List<List<HostNode>> freshNodeList;
    /** Store of previously used (canonical) coanchor images. */
    private Map<List<HostNode>,HostNode[]> coanchorImageMap;
    /**
     * Flag that indicates if a more expensive search should be used when
     * creating nodes for this event. This is useful for abstraction, when we
     * want to make sure the node store remains small. 
     */
    private boolean doAggressiveNodeReuse;
    /**
     * The total number of nodes (over all rules) created by {@link BasicEvent}.
     */
    private static int freshNodeCount;

    /**
     * Reports the number of times a stored coanchor image has been recomputed
     * for a new rule application.
     */
    static public int getCoanchorImageOverlap() {
        return coanchorImageOverlap;
    }

    /**
     * Reports the total number of coanchor images stored.
     */
    static public int getCoanchorImageCount() {
        return coanchorImageCount;
    }

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
    /** Counter for the reuse in coanchor images. */
    static private int coanchorImageOverlap;
    /** Counter for the coanchor images. */
    static private int coanchorImageCount;
    /** Global empty set of nodes. */
    static private final Set<HostNode> EMPTY_NODE_SET =
        Collections.<HostNode>emptySet();
    /** Template reference to create empty caches. */
    static private final CacheReference<SPOEventCache> reference =
        CacheReference.<SPOEventCache>newInstance(false);

    /** Cache holding the anchor map. */
    final class SPOEventCache extends
            AbstractEvent<Rule,SPOEventCache>.AbstractEventCache {
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
            RuleNode[] anchorNodes = getRule().getAnchorNodes();
            RuleEdge[] anchorEdges = getRule().getAnchorEdges();
            HostElement[] anchorImage = getAnchorImage();
            RuleToHostMap result = createRuleToHostMap();
            for (int i = 0; i < anchorNodes.length; i++) {
                result.putNode(anchorNodes[i], (HostNode) anchorImage[i]);
            }
            for (int i = 0; i < anchorEdges.length; i++) {
                RuleEdge key = anchorEdges[i];
                // store the endpoints and the variable valuations for the
                // edges
                HostEdge edgeImage =
                    (HostEdge) anchorImage[anchorNodes.length + i];
                result.putNode(key.source(), edgeImage.source());
                result.putNode(key.target(), edgeImage.target());
                TypeGuard guard = key.label().getWildcardGuard();
                if (guard != null && guard.isNamed()) {
                    result.putVar(guard.getVar(), edgeImage.getType());
                }
                result.putEdge(key, edgeImage);
            }
            // add the eraser edges
            for (RuleEdge eraserEdge : getRule().getEraserNonAnchorEdges()) {
                HostEdge eraserImage = result.mapEdge(eraserEdge);
                assert eraserImage != null : String.format(
                    "Eraser edge %s has no image in anchor map %s", eraserEdge,
                    result);
                // result.putEdge(eraserEdge, eraserImage);
            }
            return result;
        }

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
                if (creatorEnd instanceof ValueNode) {
                    ValueNode node = (ValueNode) creatorEnd;
                    createdValue =
                        BasicEvent.this.hostFactory.createValueNode(
                            node.getAlgebra(), node.getValue());
                } else {
                    createdValue = anchorMap.getNode(creatorEnd);
                    assert creatorEnd != null : String.format(
                        "Event '%s': No coanchor image for '%s' in %s",
                        BasicEvent.this, creatorEnd, anchorMap);
                }
                // if the value is null, the image was deleted due to a delete
                // conflict
                // or it is yet to be created by a parent rule
                if (createdValue != null) {
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
            for (Map.Entry<RuleNode,RuleNode> ruleMergeEntry : getRule().getLhsMergeMap().entrySet()) {
                HostNode mergeKey = anchorMap.getNode(ruleMergeEntry.getKey());
                HostNode mergeImage =
                    anchorMap.getNode(ruleMergeEntry.getValue());
                mergeMap.putNode(mergeKey, mergeImage);
            }
            // now map the erased nodes to null
            for (HostNode node : this.getErasedNodes()) {
                mergeMap.removeNode(node);
            }
            return mergeMap;
        }

        /**
         * Returns the pre-computed and cached set of explicitly erased edges.
         */
        final Set<HostEdge> getErasedEdges() {
            if (this.erasedEdgeSet == null) {
                this.erasedEdgeSet = computeErasedEdges();
            }
            return this.erasedEdgeSet;
        }

        /**
         * Returns the pre-computed and cached set of explicitly erased edges.
         */
        final Set<HostEdge> getSimpleCreatedEdges() {
            if (this.simpleCreatedEdgeSet == null) {
                this.simpleCreatedEdgeSet = computeSimpleCreatedEdges();
            }
            return this.simpleCreatedEdgeSet;
        }

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
        final Set<HostNode> getErasedNodes() {
            if (this.erasedNodeSet == null) {
                this.erasedNodeSet = computeErasedNodes();
            }
            return this.erasedNodeSet;
        }

        /**
         * Set of nodes from the source that are to be erased in the target.
         */
        private Set<HostNode> erasedNodeSet;
        /**
         * Matching from the rule's lhs to the source graph.
         */
        private RuleToHostMap anchorMap;
        /**
         * Matching from the rule's rhs to the target graph.
         */
        private RuleToHostMap coanchorMap;
        /**
         * Minimal mapping from the source graph to target graph to reconstruct
         * the underlying morphism. The merge map is constructed in the course
         * of rule application.
         */
        private MergeMap mergeMap;
        /**
         * Set of edges from the source that are to be erased in the target.
         */
        private Set<HostEdge> erasedEdgeSet;
        /**
         * Images of the simple creator edges.
         */
        private Set<HostEdge> simpleCreatedEdgeSet;
    }
}