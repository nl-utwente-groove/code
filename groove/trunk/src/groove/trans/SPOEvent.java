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
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.MergeMap;
import groove.graph.Node;
import groove.graph.NodeEdgeMap;
import groove.graph.NodeFactory;
import groove.graph.algebra.ValueNode;
import groove.rel.RegExprLabel;
import groove.rel.VarNodeEdgeHashMap;
import groove.rel.VarNodeEdgeMap;
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
 * Class representing an instance of an {@link SPORule} for a given anchor map.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-03-04 11:01:33 $
 */
final public class SPOEvent extends
        AbstractEvent<SPORule,SPOEvent.SPOEventCache> {
    /**
     * Constructs a new event on the basis of a given production rule and anchor
     * map. A further parameter determines whether information should be stored
     * for reuse.
     * @param rule the production rule involved
     * @param anchorMap map from the rule's LHS elements to the host graph
     * @param nodeFactory factory for fresh nodes; may be <code>null</code>
     * @param reuse if <code>true</code>, the event should store diverse data
     *        structures to optimise for reuse
     */
    public SPOEvent(SPORule rule, VarNodeEdgeMap anchorMap,
            NodeFactory nodeFactory, boolean reuse) {
        super(reference, rule, reuse);
        rule.testFixed(true);
        this.anchorImage = computeAnchorImage(anchorMap);
        this.nodeFactory = nodeFactory;
    }

    /**
     * Returns a map from the rule anchors to elements of the host graph. #see
     * {@link SPORule#anchor()}
     */
    public VarNodeEdgeMap getAnchorMap() {
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
    public VarNodeEdgeMap getCoanchorMap() {
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
        Element[] anchors = getRule().anchor();
        NodeEdgeMap anchorMap = getAnchorMap();
        int MAX_HASHED_ANCHOR_COUNT = 10;
        int hashedAnchorCount =
            Math.min(anchors.length, MAX_HASHED_ANCHOR_COUNT);
        for (int i = 0; i < hashedAnchorCount; i++) {
            Element anchor = anchors[i];
            if (anchor instanceof Node) {
                Node anchorNode = anchorMap.getNode((Node) anchor);
                // this can happen if we're looking for a creator node
                if (anchorNode != null) {
                    result += anchorMap.getNode((Node) anchor).hashCode() << i;
                }
            } else {
                result += anchorMap.getEdge((Edge) anchor).hashCode() << i;
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
            || other instanceof SPOEvent
            && getRule().equals(other.getRule())
            && Arrays.equals(getAnchorImage(),
                ((SPOEvent) other).getAnchorImage());
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer(getRule().getName().text());
        // result.append(getAnchorImageString());
        if (getRule().getProperties().isUseParameters()) {
            result.append(getParameterString());
        }
        return result.toString();
    }

    /**
     * Returns the string of actual parameter values of this event.
     */
    public String getParameterString() {
        StringBuilder result = new StringBuilder();
        result.append('(');
        List<Node> map = getRule().getInPars();
        if (map != null) {
            for (int i = 0; i < map.size(); i++) {
                Node node = getAnchorMap().getNode(map.get(i));
                if (node != null && node instanceof ValueNode) {
                    result.append(((ValueNode) node).getSymbol());
                } else {
                    result.append(node);
                }
                if (i < map.size() - 1) {
                    result.append(',');
                }
            }
        }
        result.append(')');
        return result.toString();
    }

    /**
     * Computes a match based on the precomputed anchor map.
     */
    public RuleMatch getMatch(Graph host) {
        RuleMatch result = null;
        if (isCorrectFor(host)) {
            Iterator<VarNodeEdgeMap> eventMatchMapIter =
                getRule().getEventMatcher().getMatchIter(host, getAnchorMap());
            Iterator<RuleMatch> matchIter =
                getRule().computeMatchIter(host, eventMatchMapIter);
            if (matchIter.hasNext()) {
                result = matchIter.next();
            }
        }
        return result;
    }

    /**
     * Tests if there is a matching of this event to a given host graph. A
     * matching may fail to exist because the anchor map does not map into the
     * host graph, or because conditions outside the anchor map are not
     * fulfilled.
     */
    public boolean hasMatch(Graph host) {
        if (isCorrectFor(host)) {
            Iterator<VarNodeEdgeMap> eventMatchMapIter =
                getRule().getEventMatcher().getMatchIter(host, getAnchorMap());
            return getRule().computeMatchIter(host, eventMatchMapIter).hasNext();
        } else {
            return false;
        }
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
        Element[] anchorImage = getAnchorImage();
        // retrieve the other even't anchor image array
        Element[] otherAnchorImage = ((SPOEvent) other).getAnchorImage();
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
    private boolean isCorrectFor(Graph host) {
        VarNodeEdgeMap anchorMap = getAnchorMap();
        boolean correct = true;
        Iterator<Edge> edgeImageIter = anchorMap.edgeMap().values().iterator();
        while (correct && edgeImageIter.hasNext()) {
            correct = host.containsElement(edgeImageIter.next());
        }
        if (correct) {
            Iterator<Node> nodeImageIter =
                anchorMap.nodeMap().values().iterator();
            while (correct && nodeImageIter.hasNext()) {
                Node nodeImage = nodeImageIter.next();
                correct =
                    nodeImage instanceof ValueNode
                        || host.containsElement(nodeImage);
            }
        }
        return correct;
    }

    /**
     * Returns the set of source elements that form the anchor image.
     */
    Element[] getAnchorImage() {
        return this.anchorImage;
    }

    /**
     * Callback method to lazily compute the set of source elements that form
     * the anchor image.
     */
    private Element[] computeAnchorImage(VarNodeEdgeMap anchorMap) {
        Element[] anchor = getRule().anchor();
        int anchorSize = anchor.length;
        Element[] result = new Element[anchor.length];
        for (int i = 0; i < anchorSize; i++) {
            if (anchor[i] instanceof Node) {
                result[i] = anchorMap.getNode((Node) anchor[i]);
            } else {
                result[i] = anchorMap.getEdge((Edge) anchor[i]);
            }
            assert result[i] != null : String.format(
                "No image for %s in anchor map %s", anchor[i], anchorMap);
        }
        return result;
    }

    public boolean conflicts(RuleEvent other) {
        boolean result;
        if (other instanceof SPOEvent) {
            result = false;
            // check if the other creates edges that this event erases
            Iterator<Edge> myErasedEdgeIter = getSimpleErasedEdges().iterator();
            Set<Edge> otherCreatedEdges =
                ((SPOEvent) other).getSimpleCreatedEdges();
            while (!result && myErasedEdgeIter.hasNext()) {
                result = otherCreatedEdges.contains(myErasedEdgeIter.next());
            }
            if (!result) {
                // check if the other erases edges that this event creates
                Iterator<Edge> myCreatedEdgeIter =
                    getSimpleCreatedEdges().iterator();
                Set<Edge> otherErasedEdges =
                    ((SPOEvent) other).getSimpleErasedEdges();
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
        Set<Element> anchorImage = ((SPOEvent) other).getAnchorImageSet();
        Iterator<Node> nodeIter = getErasedNodes().iterator();
        while (!result && nodeIter.hasNext()) {
            result = anchorImage.contains(nodeIter.next());
        }
        Iterator<Edge> edgeIter = getSimpleErasedEdges().iterator();
        while (!result && edgeIter.hasNext()) {
            result = anchorImage.contains(edgeIter.next());
        }
        return result;
    }

    /**
     * Returns the set of source elements that form the anchor image.
     */
    private Set<Element> getAnchorImageSet() {
        if (this.anchorImageSet == null) {
            NodeEdgeMap anchorMap = getAnchorMap();
            this.anchorImageSet =
                new HashSet<Element>(anchorMap.nodeMap().values());
            this.anchorImageSet.addAll(anchorMap.edgeMap().values());
        }
        return this.anchorImageSet;
    }

    /**
     * Returns the set of explicitly erased nodes, i.e., the images of the LHS
     * eraser nodes.
     */
    @Override
    public Set<Node> getErasedNodes() {
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
    Set<Node> computeErasedNodes() {
        Node[] eraserNodes = getRule().getEraserNodes();
        if (eraserNodes.length == 0) {
            return EMPTY_NODE_SET;
        } else {
            Set<Node> result = createNodeSet();
            collectErasedNodes(result);
            return result;
        }
    }

    /**
     * Adds the set of explicitly erased nodes, i.e., the images of the LHS
     * eraser nodes, to a given result set. Callback method from
     * {@link #computeErasedNodes()}.
     */
    void collectErasedNodes(Set<Node> result) {
        NodeEdgeMap anchorMap = getAnchorMap();
        // register the node erasures
        for (Node node : getRule().getEraserNodes()) {
            result.add(anchorMap.getNode(node));
        }
    }

    /**
     * Returns the set of explicitly erased edges, i.e., the images of the LHS
     * eraser edges.
     */
    public Set<Edge> getSimpleErasedEdges() {
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
    Set<Edge> computeSimpleErasedEdges() {
        Set<Edge> result = createEdgeSet();
        collectSimpleErasedEdges(result);
        return result;
    }

    /**
     * Collects the set of explicitly erased edges, i.e., the images of the LHS
     * eraser edges, into a given result set. Callback method from
     * {@link #computeSimpleErasedEdges()}.
     */
    void collectSimpleErasedEdges(Set<Edge> result) {
        VarNodeEdgeMap anchorMap = getAnchorMap();
        Edge[] eraserEdges = getRule().getEraserEdges();
        for (Edge edge : eraserEdges) {
            Edge edgeImage = anchorMap.getEdge(edge);
            assert edgeImage != null : "Image of " + edge
                + " cannot be deduced from " + anchorMap;
            result.add(edgeImage);
        }
    }

    public Set<Edge> getSimpleCreatedEdges() {
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
    Set<Edge> computeSimpleCreatedEdges() {
        Set<Edge> result = createEdgeSet();
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
    void collectSimpleCreatedEdges(Set<Node> erasedNodes, Set<Edge> result) {
        VarNodeEdgeMap coAnchorMap = getCoanchorMap();
        for (Edge edge : getRule().getSimpleCreatorEdges()) {
            Edge edgeImage = coAnchorMap.mapEdge(edge);
            if (edgeImage != null) {
                if (erasedNodes == null
                    || !(erasedNodes.contains(edgeImage.source()) || erasedNodes.contains(edgeImage.opposite()))) {
                    result.add(edgeImage);
                }
            }
        }
    }

    public Set<Edge> getComplexCreatedEdges(Iterator<Node> createdNodes) {
        Set<Edge> result = createEdgeSet();
        collectComplexCreatedEdges(null, createdNodes, null, result);
        return result;
    }

    /**
     * Collects the set of created edges of which at least one incident nodes is
     * also created, i.e., the images of the LHS creator edges between existing
     * nodes, into a given set. Callback method from
     * {@link #getComplexCreatedEdges(Iterator)}. TODO the parameter erasedNodes
     * is a hack, this should be solved by setting the coAnchorMap correctly
     * @param erasedNodes set of erased nodes; if not <code>null</code>, check
     *        if created edges have incident nodes in this set
     * @param coRootImages mapping from creator nodes that are co-roots in
     *        sub-rules to the corresponding created nodes
     */
    void collectComplexCreatedEdges(Set<Node> erasedNodes,
            Iterator<Node> createdNodes, Map<Node,Node> coRootImages,
            Set<Edge> result) {
        VarNodeEdgeMap coanchorMap = getCoanchorMap().clone();
        boolean hasSubRules = getRule().hasSubRules();
        // add creator node images
        for (Node creatorNode : getRule().getCreatorNodes()) {
            Node createdNode = createdNodes.next();
            coanchorMap.putNode(creatorNode, createdNode);
            if (hasSubRules) {
                coRootImages.put(creatorNode, createdNode);
            }
        }
        for (Map.Entry<Node,Node> coRootEntry : getRule().getCoRootMap().nodeMap().entrySet()) {
            Node coanchor = coRootEntry.getValue();
            // do something if the coanchor is a creator edge end for which we
            // do
            // not have an image
            if (getRule().getCreatorGraph().nodeSet().contains(coanchor)
                && !coanchorMap.containsKey(coanchor)) {
                Node coanchorImage = coRootImages.get(coRootEntry.getKey());
                assert coanchorImage != null : String.format(
                    "Event '%s': Coroot image map %s does not contain image for coanchor root '%s'",
                    this, coRootImages, coRootEntry.getKey());
                coanchorMap.putNode(coanchor, coanchorImage);
            }
        }
        // now compute and add the complex creator edge images
        for (Edge edge : getRule().getComplexCreatorEdges()) {
            Edge image = coanchorMap.mapEdge(edge);
            // only add if the image exists
            if (image != null) {
                // only add if image has no incident erased node
                if (erasedNodes == null
                    || !(erasedNodes.contains(image.source()) || erasedNodes.contains(image.opposite()))) {
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
    private List<List<Node>> createFreshNodeList() {
        int creatorNodeCount = getRule().getCreatorNodes().length;
        List<List<Node>> result = new ArrayList<List<Node>>();
        for (int i = 0; i < creatorNodeCount; i++) {
            result.add(new ArrayList<Node>());
        }
        return result;
    }

    public Set<? extends Node> getCreatedNodes(Set<? extends Node> hostNodes) {
        Set<Node> result = computeCreatedNodes(hostNodes);
        if (isReuse()) {
            if (this.coanchorImageMap == null) {
                this.coanchorImageMap = new HashMap<Set<Node>,Set<Node>>();
            }
            Set<Node> existingResult = this.coanchorImageMap.get(result);
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

    private Set<Node> computeCreatedNodes(Set<? extends Node> currentNodes) {
        Set<Node> result;
        int coanchorSize = getRule().getCreatorNodes().length;
        if (coanchorSize == 0) {
            result = EMPTY_COANCHOR_IMAGE;
        } else {
            result = new LinkedHashSet<Node>(coanchorSize);
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
    void collectCreatedNodes(Set<? extends Node> currentNodes, Set<Node> result) {
        Node[] creatorNodes = getRule().getCreatorNodes();
        int creatorNodeCount = creatorNodes.length;
        for (int i = 0; i < creatorNodeCount; i++) {
            addFreshNode(i, currentNodes, result);
        }
    }

    /**
     * Adds a node that is fresh with respect to a given graph to a collection
     * of already added node. The previously created fresh nodes are tried first
     * (see {@link SPOEvent#getFreshNodes(int)}; only if all of those are
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
            Set<? extends Node> currentNodes, Set<Node> result) {
        boolean added = false;
        Collection<Node> currentFreshNodes = getFreshNodes(creatorIndex);
        if (currentFreshNodes != null) {
            Iterator<Node> freshNodeIter = currentFreshNodes.iterator();
            while (!added && freshNodeIter.hasNext()) {
                Node freshNode = freshNodeIter.next();
                added =
                    !currentNodes.contains(freshNode) && result.add(freshNode);
            }
        }
        if (!added) {
            Node addedNode = createNode();
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
    private Node createNode() {
        DefaultApplication.freshNodeCount++;
        NodeFactory record = getNodeFactory();
        Node result =
            record == null ? DefaultNode.createNode() : record.newNode();
        return result;
    }

    /**
     * Returns the derivation record associated with this event. May be
     * <code>null</code>.
     */
    private NodeFactory getNodeFactory() {
        return this.nodeFactory;
    }

    /**
     * Returns the list of all previously created fresh nodes. Returns
     * <code>null</code> if the reuse policy is set to <code>false</code>.
     */
    private List<Node> getFreshNodes(int creatorIndex) {
        if (isReuse()) {
            if (this.freshNodeList == null) {
                this.freshNodeList = createFreshNodeList();
            }
            return this.freshNodeList.get(creatorIndex);
        } else {
            return null;
        }
    }

    @Override
    protected SPOEventCache createCache() {
        return new SPOEventCache();
    }

    /** The derivation record that has created this event, if any. */
    private final NodeFactory nodeFactory;
    /**
     * The set of source elements that form the anchor image.
     */
    private Set<Element> anchorImageSet;
    /**
     * The array of source elements that form the anchor image.
     */
    private final Element[] anchorImage;
    /**
     * The list of nodes created by {@link #createNode()}.
     */
    private List<List<Node>> freshNodeList;
    /** Store of previously used coanchor images. */
    private Map<Set<Node>,Set<Node>> coanchorImageMap;

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
    static private final Set<Node> EMPTY_NODE_SET =
        Collections.<Node>emptySet();
    /** Global empty list of nodes. */
    static private final Set<Node> EMPTY_COANCHOR_IMAGE =
        Collections.emptySet();
    /** Template reference to create empty caches. */
    static private final CacheReference<SPOEventCache> reference =
        CacheReference.<SPOEventCache>newInstance(false);

    /** Cache holding the anchor map. */
    final class SPOEventCache extends
            AbstractEvent<SPORule,SPOEventCache>.AbstractEventCache {
        /**
         * @return Returns the anchorMap.
         */
        public final VarNodeEdgeMap getAnchorMap() {
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
        private VarNodeEdgeMap computeAnchorMap() {
            Element[] anchor = getRule().anchor();
            Element[] anchorImage = getAnchorImage();
            VarNodeEdgeMap result = createVarMap();
            for (int i = 0; i < anchor.length; i++) {
                Element key = anchor[i];
                Element image = anchorImage[i];
                if (key instanceof Edge) {
                    // store the endpoints and the variable valuations for the
                    // edges
                    Edge edgeKey = (Edge) key;
                    Edge edgeImage = (Edge) image;
                    int arity = edgeKey.endCount();
                    for (int end = 0; end < arity; end++) {
                        result.putNode(edgeKey.end(end), edgeImage.end(end));
                    }
                    String var = RegExprLabel.getWildcardId(edgeKey.label());
                    if (var != null) {
                        result.putVar(var, edgeImage.label());
                    }
                    result.putEdge(edgeKey, edgeImage);
                } else {
                    result.putNode((Node) key, (Node) image);
                }
            }
            // add the eraser edges
            for (Edge eraserEdge : getRule().getEraserNonAnchorEdges()) {
                Edge eraserImage = result.mapEdge(eraserEdge);
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
        public final VarNodeEdgeMap getCoanchorMap() {
            if (this.coanchorMap == null) {
                this.coanchorMap = computeCoanchorMap();
            }
            return this.coanchorMap;
        }

        /**
         * Constructs a map from the reader nodes of the RHS that are endpoints
         * of creator edges, to the target graph nodes.
         */
        private VarNodeEdgeMap computeCoanchorMap() {
            final VarNodeEdgeMap result = createVarMap();
            VarNodeEdgeMap anchorMap = getAnchorMap();
            NodeEdgeMap mergeMap =
                getRule().hasMergers() ? getMergeMap() : null;
            Set<Node> erasedNodes = getErasedNodes();
            // add coanchor mappings for creator edge ends that are themselves
            // not creators
            for (Map.Entry<Node,Node> creatorEntry : getRule().getCreatorMap().nodeMap().entrySet()) {
                Node creatorKey = creatorEntry.getKey();
                Node creatorValue = creatorEntry.getValue();
                if (!(creatorValue instanceof ValueNode)) {
                    creatorValue = anchorMap.getNode(creatorEntry.getValue());
                    assert creatorValue != null : String.format(
                        "Event '%s': No coanchor image for '%s' in %s",
                        SPOEvent.this, creatorKey, anchorMap);
                }
                if (mergeMap != null) {
                    creatorValue = mergeMap.getNode(creatorValue);
                } else if (erasedNodes.contains(creatorValue)) {
                    creatorValue = null;
                }
                // if the value is null, the image was deleted due to a delete
                // conflict
                // or it is yet to be created by a parent rule
                if (creatorValue != null) {
                    result.putNode(creatorKey, creatorValue);
                }
            }
            // add variable images
            for (String var : getRule().getCreatorVars()) {
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
            VarNodeEdgeMap anchorMap = getAnchorMap();
            MergeMap mergeMap = createMergeMap();
            for (Map.Entry<Node,Node> ruleMergeEntry : getRule().getMergeMap().entrySet()) {
                Node mergeKey = anchorMap.getNode(ruleMergeEntry.getKey());
                Node mergeImage = anchorMap.getNode(ruleMergeEntry.getValue());
                mergeMap.putNode(mergeKey, mergeImage);
            }
            // now map the erased nodes to null
            for (Node node : getErasedNodes()) {
                mergeMap.removeNode(node);
            }
            return mergeMap;
        }

        /**
         * Returns the pre-computed and cached set of explicitly erased edges.
         */
        public Set<Edge> getSimpleErasedEdges() {
            if (this.erasedEdgeSet == null) {
                this.erasedEdgeSet = computeSimpleErasedEdges();
            }
            return this.erasedEdgeSet;
        }

        /**
         * Returns the pre-computed and cached set of explicitly erased edges.
         */
        final public Set<Edge> getSimpleCreatedEdges() {
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
            return new MergeMap();
        }

        /**
         * Callback factory method to create the map object for
         * {@link #computeCoanchorMap()}.
         * @return a fresh instance of {@link VarNodeEdgeHashMap}
         */
        private VarNodeEdgeMap createVarMap() {
            return new VarNodeEdgeHashMap();
        }

        /**
         * Matching from the rule's lhs to the source graph.
         */
        private VarNodeEdgeMap anchorMap;
        /**
         * Matching from the rule's rhs to the target graph.
         */
        private VarNodeEdgeMap coanchorMap;
        /**
         * Minimal mapping from the source graph to target graph to reconstruct
         * the underlying morphism. The merge map is constructed in the course
         * of rule application.
         */
        private MergeMap mergeMap;
        /**
         * Set of edges from the source that are to be erased in the target.
         */
        private Set<Edge> erasedEdgeSet;
        /**
         * Images of the simple creator edges.
         */
        private Set<Edge> simpleCreatedEdgeSet;
    }
}