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
import groove.graph.algebra.ValueNode;
import groove.rel.LabelVar;
import groove.util.CacheReference;
import groove.util.Groove;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
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
    public HostNode[] getArguments(HostNode[] addedNodes) {
        HostNode[] result;
        int size = getRule().getSignature().size();
        if (size == 0) {
            result = EMPTY_NODE_ARRAY;
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

    @Override
    public int getAnchorSize() {
        return getAnchorImage().length;
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
            Iterator<HostEdge> myErasedEdgeIter =
                getSimpleErasedEdges().iterator();
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
                    ((BasicEvent) other).getSimpleErasedEdges();
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
        Iterator<HostEdge> edgeIter = getSimpleErasedEdges().iterator();
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
     * Returns the set of explicitly erased nodes, i.e., the images of the LHS
     * eraser nodes.
     */
    @Override
    public Set<HostNode> getErasedNodes() {
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
    @Override
    Set<HostNode> computeErasedNodes() {
        if (getRule().getEraserNodes().length == 0) {
            return EMPTY_NODE_SET;
        } else {
            Set<HostNode> result = createNodeSet();
            collectErasedNodes(result);
            return result;
        }
    }

    /**
     * Adds the set of explicitly erased nodes, i.e., the images of the LHS
     * eraser nodes, to a given result set. Callback method from
     * {@link #computeErasedNodes()}.
     */
    void collectErasedNodes(Set<HostNode> result) {
        RuleToHostMap anchorMap = getAnchorMap();
        // register the node erasures
        for (RuleNode node : getRule().getEraserNodes()) {
            result.add(anchorMap.getNode(node));
        }
    }

    /**
     * Returns the set of explicitly erased edges, i.e., the images of the LHS
     * eraser edges.
     */
    public Set<HostEdge> getSimpleErasedEdges() {
        if (isReuse()) {
            return getCache().getSimpleErasedEdges();
        } else {
            return computeSimpleErasedEdges();
        }
    }

    /**
     * Computes the set of explicitly erased edges, i.e., the images of the LHS
     * eraser edges. Callback method from {@link #getSimpleErasedEdges()}.
     */
    Set<HostEdge> computeSimpleErasedEdges() {
        Set<HostEdge> result = createEdgeSet();
        collectSimpleErasedEdges(result);
        return result;
    }

    /**
     * Collects the set of explicitly erased edges, i.e., the images of the LHS
     * eraser edges, into a given result set. Callback method from
     * {@link #computeSimpleErasedEdges()}.
     */
    void collectSimpleErasedEdges(Set<HostEdge> result) {
        RuleToHostMap anchorMap = getAnchorMap();
        RuleEdge[] eraserEdges = getRule().getEraserEdges();
        for (RuleEdge edge : eraserEdges) {
            HostEdge edgeImage = anchorMap.getEdge(edge);
            assert edgeImage != null : "Image of " + edge
                + " cannot be deduced from " + anchorMap;
            result.add(edgeImage);
        }
    }

    public Set<HostEdge> getSimpleCreatedEdges() {
        if (isReuse()) {
            return getCache().getSimpleCreatedEdges();
        } else {
            return computeSimpleCreatedEdges();
        }
    }

    /**
     * Computes the set of explicitly erased edges, i.e., the images of the LHS
     * eraser edges. Callback method from {@link #getSimpleErasedEdges()}.
     */
    Set<HostEdge> computeSimpleCreatedEdges() {
        Set<HostEdge> result = createEdgeSet();
        collectSimpleCreatedEdges(null, result);
        return result;
    }

    /**
     * Collects the set of simple created edges, i.e., the images of the LHS
     * creator edges between existing nodes, into a given set. Callback method
     * from {@link #computeSimpleCreatedEdges()}. TODO the parameter erasedNodes
     * is a hack, this should be solved by setting the coAnchorMap correctly
     * @param erasedNodes set of erased nodes; if not <code>null</code>, check
     *        if created edges have incident nodes in this set
     */
    void collectSimpleCreatedEdges(Set<HostNode> erasedNodes,
            Set<HostEdge> result) {
        RuleToHostMap coAnchorMap = getCoanchorMap();
        for (RuleEdge edge : getRule().getSimpleCreatorEdges()) {
            HostEdge edgeImage = coAnchorMap.mapEdge(edge);
            if (edgeImage != null) {
                if (erasedNodes == null
                    || !(erasedNodes.contains(edgeImage.source()) || erasedNodes.contains(edgeImage.target()))) {
                    result.add(edgeImage);
                }
            }
        }
    }

    public Collection<HostEdge> getComplexCreatedEdges(
            Iterator<HostNode> createdNodes) {
        Set<HostEdge> result = createEdgeSet();
        collectComplexCreatedEdges(null, createdNodes, null, result);
        return result;
    }

    /**
     * Collects the set of created edges of which at least one incident nodes is
     * also created, into a given set. Callback method from
     * {@link #getComplexCreatedEdges(Iterator)}. TODO the parameter erasedNodes
     * is a hack, this should be solved by setting the coAnchorMap correctly
     * @param erasedNodes set of erased nodes; if not <code>null</code>, check
     *        if created edges have incident nodes in this set
     * @param coRootImages mapping from creator nodes that are co-roots in
     *        sub-rules to the corresponding created nodes
     */
    void collectComplexCreatedEdges(Set<HostNode> erasedNodes,
            Iterator<HostNode> createdNodes,
            Map<RuleNode,HostNode> coRootImages, Set<HostEdge> result) {
        RuleToHostMap coanchorMap = getCoanchorMap().clone();
        boolean hasSubRules = getRule().hasSubRules();
        // add creator node images
        for (RuleNode creatorNode : getRule().getCreatorNodes()) {
            HostNode createdNode = createdNodes.next();
            coanchorMap.putNode(creatorNode, createdNode);
            if (hasSubRules) {
                coRootImages.put(creatorNode, createdNode);
            }
        }
        for (Map.Entry<RuleNode,RuleNode> coRootEntry : getRule().getCoRootMap().nodeMap().entrySet()) {
            RuleNode coanchor = coRootEntry.getValue();
            // do something if the coanchor is a creator edge end for which we
            // do
            // not have an image
            if (getRule().getCreatorGraph().nodeSet().contains(coanchor)
                && !coanchorMap.containsNodeKey(coanchor)) {
                HostNode coanchorImage = coRootImages.get(coRootEntry.getKey());
                assert coanchorImage != null : String.format(
                    "Event '%s': Coroot image map %s does not contain image for coanchor root '%s'",
                    this, coRootImages, coRootEntry.getKey());
                coanchorMap.putNode(coanchor, coanchorImage);
            }
        }
        // now compute and add the complex creator edge images
        for (RuleEdge edge : getRule().getComplexCreatorEdges()) {
            HostEdge image = coanchorMap.mapEdge(edge);
            // only add if the image exists
            if (image != null) {
                // only add if image has no incident erased node
                if (erasedNodes == null
                    || !(erasedNodes.contains(image.source()) || erasedNodes.contains(image.target()))) {
                    result.add(image);
                }
            }
        }
    }

    /**
     * Returns a mapping from source to target graph nodes, dictated by the
     * merger and eraser nodes in the rules.
     * @return an {@link MergeMap} that maps nodes of the source that are merged
     *         away to their merged images, and deleted nodes to
     *         <code>null</code>.
     */
    public MergeMap getMergeMap() {
        return getCache().getMergeMap();
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

    public Set<HostNode> getCreatedNodes(Set<? extends HostNode> hostNodes) {
        Set<HostNode> result = computeCreatedNodes(hostNodes);
        if (isReuse()) {
            if (this.coanchorImageMap == null) {
                this.coanchorImageMap =
                    new HashMap<Set<HostNode>,Set<HostNode>>();
            }
            Set<HostNode> existingResult = this.coanchorImageMap.get(result);
            if (existingResult == null) {
                this.coanchorImageMap.put(result, result);
                coanchorImageCount++;
            } else {
                result = existingResult;
                coanchorImageOverlap++;
            }
        }
        return result;
    }

    private Set<HostNode> computeCreatedNodes(
            Set<? extends HostNode> currentNodes) {
        Set<HostNode> result;
        int coanchorSize = getRule().getCreatorNodes().length;
        if (coanchorSize == 0) {
            result = EMPTY_NODE_SET;
        } else {
            result = new LinkedHashSet<HostNode>(coanchorSize);
            collectCreatedNodes(currentNodes, result);
        }
        return result;
    }

    /**
     * Adds nodes created by this event into a given list of created nodes. The
     * created nodes are guaranteed to be fresh with respect to a given set of
     * currently existing nodes
     * @param currentNodes the set of currently existing nodes
     * @param result list of created nodes to be extended by this method
     */
    void collectCreatedNodes(Set<? extends HostNode> currentNodes,
            Set<HostNode> result) {
        RuleNode[] creatorNodes = getRule().getCreatorNodes();
        int creatorNodeCount = creatorNodes.length;
        for (int i = 0; i < creatorNodeCount; i++) {
            addFreshNode(i, currentNodes, result);
        }
    }

    /**
     * Adds a node that is fresh with respect to a given graph to a collection
     * of already added node. The previously created fresh nodes are tried first
     * (see {@link BasicEvent#getFreshNodes(int)}; only if all of those are
     * already in the graph, a new fresh node is created using
     * {@link #createNode()}.
     * @param creatorIndex index in the rhsOnlyNodes array indicating the node
     *        of the rule for which a new image is to be created
     * @param currentNodes the existing nodes, which should not contain the
     *        fresh node
     * @param result the collection of already added nodes; the newly added node
     *        is guaranteed to be fresh with respect to these
     */
    public void addFreshNode(int creatorIndex,
            Set<? extends HostNode> currentNodes, Set<HostNode> result) {
        boolean added = false;
        Collection<HostNode> currentFreshNodes = getFreshNodes(creatorIndex);
        if (currentFreshNodes != null) {
            Iterator<HostNode> freshNodeIter = currentFreshNodes.iterator();
            while (!added && freshNodeIter.hasNext()) {
                HostNode freshNode = freshNodeIter.next();
                added =
                    !currentNodes.contains(freshNode) && result.add(freshNode);
            }
        }
        if (!added) {
            HostNode addedNode = createNode();
            result.add(addedNode);
            if (currentFreshNodes != null) {
                currentFreshNodes.add(addedNode);
            }
        }
    }

    /**
     * Callback factory method for a newly constructed node. This implementation
     * returns a {@link DefaultNode}, with a node number determined by the
     * grammar's node counter.
     */
    private HostNode createNode() {
        RuleApplication.freshNodeCount++;
        HostFactory record = getHostFactory();
        return record.createNode();
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
     * The list of nodes created by {@link #createNode()}.
     */
    private List<List<HostNode>> freshNodeList;
    /** Store of previously used (canonical) coanchor images. */
    private Map<Set<HostNode>,Set<HostNode>> coanchorImageMap;

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
    /** Global empty set of nodes. */
    static private final HostNode[] EMPTY_NODE_ARRAY = new HostNode[0];
    /** Template reference to create empty caches. */
    static private final CacheReference<SPOEventCache> reference =
        CacheReference.<SPOEventCache>newInstance(false);

    /** Cache holding the anchor map. */
    final class SPOEventCache extends
            AbstractEvent<Rule,SPOEventCache>.AbstractEventCache {
        /**
         * @return Returns the anchorMap.
         */
        public final RuleToHostMap getAnchorMap() {
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
                LabelVar var = key.label().getWildcardId();
                if (var != null) {
                    result.putVar(var, edgeImage.label());
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
        public final RuleToHostMap getCoanchorMap() {
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
            MergeMap mergeMap = getRule().hasMergers() ? getMergeMap() : null;
            Set<HostNode> erasedNodes = this.getErasedNodes();
            // add coanchor mappings for creator edge ends that are themselves
            // not creators
            for (Map.Entry<RuleNode,RuleNode> creatorEntry : getRule().getCreatorMap().nodeMap().entrySet()) {
                RuleNode creatorKey = creatorEntry.getKey();
                RuleNode creatorValue = creatorEntry.getValue();
                HostNode createdValue;
                if (creatorValue instanceof ValueNode) {
                    createdValue = (ValueNode) creatorValue;
                } else {
                    createdValue = anchorMap.getNode(creatorEntry.getValue());
                    assert creatorValue != null : String.format(
                        "Event '%s': No coanchor image for '%s' in %s",
                        BasicEvent.this, creatorKey, anchorMap);
                }
                if (mergeMap != null) {
                    createdValue = mergeMap.getNode(createdValue);
                } else if (erasedNodes.contains(createdValue)) {
                    createdValue = null;
                }
                // if the value is null, the image was deleted due to a delete
                // conflict
                // or it is yet to be created by a parent rule
                if (createdValue != null) {
                    result.putNode(creatorKey, createdValue);
                }
            }
            // add variable images
            for (LabelVar var : getRule().getCreatorVars()) {
                result.putVar(var, anchorMap.getVar(var));
            }
            return result;
        }

        /**
         * Returns a mapping from source to target graph nodes, dictated by the
         * merger and eraser nodes in the rules.
         * @return an {@link MergeMap} that maps nodes of the source that are
         *         merged away to their merged images, and deleted nodes to
         *         <code>null</code>.
         */
        public final MergeMap getMergeMap() {
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
            for (Map.Entry<RuleNode,RuleNode> ruleMergeEntry : getRule().getMergeMap().entrySet()) {
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
        public Set<HostEdge> getSimpleErasedEdges() {
            if (this.erasedEdgeSet == null) {
                this.erasedEdgeSet = computeSimpleErasedEdges();
            }
            return this.erasedEdgeSet;
        }

        /**
         * Returns the pre-computed and cached set of explicitly erased edges.
         */
        final public Set<HostEdge> getSimpleCreatedEdges() {
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