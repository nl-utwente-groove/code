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
 * $Id: DefaultAutomaton.java,v 1.8 2008-01-30 09:32:27 iovka Exp $
 */
package groove.rel;

import groove.graph.DefaultGraph;
import groove.graph.DefaultLabel;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphShape;
import groove.graph.Label;
import groove.graph.Node;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A default implementation of regular automata.
 */
public class DefaultAutomaton extends DefaultGraph implements Automaton {
    /**
     * Creates an automaton with a fresh start and end node, which does not
     * accept the empty word.
     */
    public DefaultAutomaton() {
        this.start = addNode();
        this.end = addNode();
    }

    public Node getStartNode() {
        return this.start;
    }

    public Node getEndNode() {
        return this.end;
    }

    public boolean isAcceptsEmptyWord() {
        return this.acceptsEmptyWord;
    }

    public void setAcceptsEmptyWord(boolean acceptsEmptyWord) {
        this.acceptsEmptyWord = acceptsEmptyWord;
    }

    public void setEndNode(Node endNode) {
        this.end = endNode;
    }

    public void setStartNode(Node startNode) {
        this.start = startNode;
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer(super.toString());
        result.append("\nStart node: " + getStartNode());
        result.append("\nEnd node: " + getEndNode());
        result.append("\nAccepts empty word: " + isAcceptsEmptyWord());
        return result.toString();
    }

    public boolean accepts(List<String> word) {
        if (word.isEmpty()) {
            return isAcceptsEmptyWord();
        } else {
            // keep the set of current matches (initially the start node)
            Set<Node> matchSet = Collections.singleton(getStartNode());
            boolean accepts = false;
            // go through the word
            for (int index = 0; !accepts && !matchSet.isEmpty()
                && index < word.size(); index++) {
                boolean lastIndex = index == word.size() - 1;
                Set<Node> newMatchSet = new HashSet<Node>();
                for (Node match : matchSet) {
                    Iterator<? extends Edge> outEdgeIter =
                        outEdgeSet(match).iterator();
                    while (!accepts && outEdgeIter.hasNext()) {
                        Edge outEdge = outEdgeIter.next();
                        Label label = outEdge.label();
                        boolean labelOK =
                            RegExprLabel.isWildcard(label)
                                || label.text().equals(word.get(index));
                        if (labelOK) {
                            // if we're at the last index, we don't have to
                            // build the new mtch set
                            if (lastIndex) {
                                accepts =
                                    outEdge.opposite().equals(getEndNode());
                            } else {
                                newMatchSet.add(outEdge.opposite());
                            }
                        }
                    }
                }
                matchSet = newMatchSet;
            }
            return accepts;
        }
    }

    public NodeRelation getMatches(GraphShape graph,
            Set<? extends Node> startImages, Set<? extends Node> endImages) {
        if (startImages != null) {
            // do forward maching from the start images
            return getMatchingAlgorithm(FORWARD).computeMatches(graph,
                startImages, endImages);
        } else if (endImages != null) {
            // do backwards matching from the end images
            return getMatchingAlgorithm(BACKWARD).computeMatches(graph,
                endImages, startImages);
        } else {
            // if we don't have any start or end images,
            // create a set of start images using the automaton's initial edges
            return getMatchingAlgorithm(FORWARD).computeMatches(graph,
                createStartImages(graph), endImages);
        }
    }

    /**
     * Creates a set of start nodes to be used in the search for matches if no
     * explicit start nodes are provided.
     * @see #getMatches(GraphShape, Set, Set)
     */
    protected Set<Node> createStartImages(GraphShape graph) {
        Set<Node> result = new HashSet<Node>();
        if (isAcceptsEmptyWord() || isInitWildcard()) {
            // too bad, all graph nodes can be start images
            result.addAll(graph.nodeSet());
        } else {
            addStartImages(result, graph, true);
            addStartImages(result, graph, false);
        }
        return result;
    }

    private void addStartImages(Set<Node> result, GraphShape graph,
            boolean positive) {
        Set<Label> initLabelSet =
            positive ? getInitPosLabels() : getInitInvLabels();
        for (Label initLabel : initLabelSet) {
            for (Edge graphEdge : graph.labelEdgeSet(2, initLabel)) {
                Node end = positive ? graphEdge.source() : graphEdge.opposite();
                result.add(end);
            }
        }
    }

    /** Indicates if this automaton contains any wildcards on edges. */
    public boolean hasWildcards() {
        return false;
    }

    /**
     * Returns a matching algorithm for a given matching direction. The
     * algorithm is created on demand, using
     * {@link #createMatchingAlgorithm(int)}.
     * @param direction the matching direction: either {@link #FORWARD} or
     *        {@link #BACKWARD}
     */
    protected MatchingAlgorithm getMatchingAlgorithm(int direction) {
        if (this.algorithm == null) {
            this.algorithm = new MatchingAlgorithm[2];
            this.algorithm[FORWARD] = createMatchingAlgorithm(FORWARD);
            this.algorithm[BACKWARD] = createMatchingAlgorithm(BACKWARD);
        }
        return this.algorithm[direction];
    }

    /**
     * Callback factory method to create a matching algorithm for a given
     * matching direction. This implementation returns an
     * {@link MatchingAlgorithm}
     * @param direction the matching direction: either {@link #FORWARD} or
     *        {@link #BACKWARD}
     */
    protected MatchingAlgorithm createMatchingAlgorithm(int direction) {
        return new MatchingAlgorithm(direction);
    }

    /**
     * Returns a mapping from source nodes to mappings from labels to
     * (non-empty) sets of (automaton) edges with that source node and label.
     * The map is created on demand using {@link #initNodeLabelEdgeMaps()}.
     */
    protected Map<Node,Map<Label,Set<Edge>>> getNodePosLabelEdgeMap(
            int direction) {
        if (this.nodePosLabelEdgeMap == null) {
            initNodeLabelEdgeMaps();
        }
        return this.nodePosLabelEdgeMap[direction];
    }

    /**
     * Returns a mapping from source nodes to mappings from labels to
     * (non-empty) sets of (automaton) edges with that source node, and the
     * label wrapped in a {@link RegExpr.Inv}. The map is created on demand
     * using {@link #initNodeLabelEdgeMaps()}.
     */
    protected Map<Node,Map<Label,Set<Edge>>> getNodeInvLabelEdgeMap(
            int direction) {
        if (this.nodeInvLabelEdgeMap == null) {
            initNodeLabelEdgeMaps();
        }
        return this.nodeInvLabelEdgeMap[direction];
    }

    /**
     * Returns the set of positive labels occurring on initial edges. Guaranteed
     * to be non-<code>null</code>. The map is created on demand in
     * {@link #initNodeLabelEdgeMaps()}.
     */
    protected Set<Label> getInitPosLabels() {
        if (this.initPosLabels == null) {
            initNodeLabelEdgeMaps();
        }
        return this.initPosLabels;
    }

    /**
     * Returns the set of inverse labels occurring on initial edges. Guaranteed
     * to be non-<code>null</code>. The map is created on demand in
     * {@link #initNodeLabelEdgeMaps()}.
     */
    protected Set<Label> getInitInvLabels() {
        if (this.initInvLabels == null) {
            initNodeLabelEdgeMaps();
        }
        return this.initInvLabels;
    }

    /**
     * Indicates if the automaton has an initial (normal or inverse) edge with a
     * wildcard label inside.
     */
    protected boolean isInitWildcard() {
        if (this.initPosLabels == null) {
            initNodeLabelEdgeMaps();
        }
        return this.initWildcard;
    }

    /**
     * Initializes all the node-to-label-to-edge-sets maps of this automaton.
     * @throws IllegalStateException if the method is called before the graph is
     *         fixed
     */
    @SuppressWarnings("unchecked")
    protected void initNodeLabelEdgeMaps() {
        if (!isFixed()) {
            throw new IllegalStateException(
                "Maps cannot be calculated reliably before automaton is closed");
        }
        this.initPosLabels = new HashSet<Label>();
        this.initInvLabels = new HashSet<Label>();
        this.nodeInvLabelEdgeMap = new Map[2];
        this.nodePosLabelEdgeMap = new Map[2];
        for (int direction = FORWARD; direction <= BACKWARD; direction++) {
            this.nodePosLabelEdgeMap[direction] =
                new HashMap<Node,Map<Label,Set<Edge>>>();
            this.nodeInvLabelEdgeMap[direction] =
                new HashMap<Node,Map<Label,Set<Edge>>>();
        }
        for (Edge edge : edgeSet()) {
            Label label = edge.label();
            RegExpr invOperand = RegExprLabel.getInvOperand(label);
            boolean isInverse = invOperand != null;
            if (isInverse && invOperand.isWildcard()
                || RegExprLabel.isWildcard(label)) {
                label = WILDCARD_LABEL;
            } else if (isInverse) {
                // strip the inverse operator and create a default label
                label = DefaultLabel.createLabel(invOperand.getAtomText());
            }
            for (int direction = FORWARD; direction <= BACKWARD; direction++) {
                Node end =
                    (direction == FORWARD) ? edge.source() : edge.opposite();
                if (isInverse) {
                    addToNodeLabelEdgeMap(this.nodeInvLabelEdgeMap[direction],
                        end, label, edge);
                    if (edge.source() == getStartNode()) {
                        this.initInvLabels.add(label);
                    }
                } else {
                    addToNodeLabelEdgeMap(this.nodePosLabelEdgeMap[direction],
                        end, label, edge);
                    if (edge.source() == getStartNode()) {
                        this.initPosLabels.add(label);
                    }
                }
            }
            if (edge.source() == getStartNode()) {
                if (label == WILDCARD_LABEL) {
                    this.initWildcard = true;
                } else {
                    (isInverse ? this.initInvLabels : this.initPosLabels).add(label);
                }
            }
        }
    }

    /**
     * Tests if a given node lies on a cycle reachable from the start state.
     * This implementation uses a precomputed set, constructed on demand using
     * {@link #initCyclicNodes()}.
     */
    protected boolean isCyclic(Node node) {
        if (this.cyclicNodes == null) {
            initCyclicNodes();
        }
        return this.cyclicNodes.contains(node);
    }

    /**
     * Initializes the set of cyclic nodes.
     * @see #isCyclic(Node)
     */
    protected void initCyclicNodes() {
        this.cyclicNodes = new HashSet<Node>();
        // the set of nodes yet to be investigated
        Set<Node> remainingNodes = new HashSet<Node>();
        remainingNodes.add(getStartNode());
        // the set of nodes already investigated
        Set<Node> visitedNodes = new HashSet<Node>();
        while (!remainingNodes.isEmpty()) {
            Node node = remainingNodes.iterator().next();
            remainingNodes.remove(node);
            for (Edge outEdge : outEdgeSet(node)) {
                Node opposite = outEdge.opposite();
                if (visitedNodes.add(opposite)) {
                    remainingNodes.add(opposite);
                } else {
                    this.cyclicNodes.add(opposite);
                }
            }
        }
    }

    /**
     * Adds a combination of node, label and edge to one of the maps in this
     * automaton.
     */
    private void addToNodeLabelEdgeMap(
            Map<Node,Map<Label,Set<Edge>>> nodeLabelEdgeMap, Node node,
            Label label, Edge edge) {
        Map<Label,Set<Edge>> labelEdgeMap = nodeLabelEdgeMap.get(node);
        if (labelEdgeMap == null) {
            nodeLabelEdgeMap.put(node, labelEdgeMap =
                new HashMap<Label,Set<Edge>>());
        }
        Set<Edge> edgeSet = labelEdgeMap.get(label);
        if (edgeSet == null) {
            labelEdgeMap.put(label, edgeSet = new HashSet<Edge>());
        }
        edgeSet.add(edge);
    }

    /**
     * The start node of the automaton.
     */
    private Node start;
    /**
     * The end node of the automaton.
     */
    private Node end;
    /**
     * Flag to indicate that the automaton is to accept the empty word.
     */
    private boolean acceptsEmptyWord;

    /**
     * Direction-indexed array of mappings from nodes in this automaton to maps
     * from labels to corresponding sets of edges, where the node key is either
     * the source node or the target node of the edge, depending on the
     * direction. Initialized using {@link #initNodeLabelEdgeMaps()}.
     */
    private Map<Node,Map<Label,Set<Edge>>>[] nodePosLabelEdgeMap;
    /**
     * Direction-indexed array of mappings from nodes in this automaton to maps
     * from labels to sets of edges where the label occurs in the context of a
     * {@link RegExpr.Inv}. The node key is either the source node or the
     * target node of the edge, depending on the direction. Initialized using
     * {@link #initNodeLabelEdgeMaps()}.
     */
    private Map<Node,Map<Label,Set<Edge>>>[] nodeInvLabelEdgeMap;
    /**
     * The set of positive labels occurring on initial edges of this automaton.
     */
    private Set<Label> initPosLabels;
    /**
     * The set of inverse labels occurring on initial edges of this automaton.
     */
    private Set<Label> initInvLabels;
    /**
     * Flag to indicate that the initial edges of this automaton include one
     * with wildcard label. Used to create a set of initial start images if none
     * is provided.
     */
    private boolean initWildcard;
    /**
     * The set of nodes of this automaton that lie on a cycle reachable from the
     * start node.
     */
    private Set<Node> cyclicNodes;
    /**
     * Array of algorithm for matching, indexed by the matching direction.
     */
    private MatchingAlgorithm[] algorithm;
    /**
     * Indication of forward matching direction.
     */
    static private final int FORWARD = 0;
    /**
     * Indication of backward matching direction.
     */
    static private final int BACKWARD = 1;
    /** Constant wildcard label serving as a key in label-to-edge-sets maps. */
    static final RegExprLabel WILDCARD_LABEL = RegExpr.wildcard().toLabel();

    /**
     * Class to encapsulate the algorithm used to compute the result of
     * {@link Automaton#getMatches(GraphShape, Set, Set)}.
     */
    protected class MatchingAlgorithm {
        /**
         * Creates an instance of the algorithm that matches in a given
         * direction ({@link #FORWARD} or {@link #BACKWARD}).
         */
        public MatchingAlgorithm(int direction) {
            switch (direction) {
            case FORWARD:
                this.start = getStartNode();
                this.end = getEndNode();
                break;
            case BACKWARD:
                this.start = getEndNode();
                this.end = getStartNode();
                break;
            default:
                throw new IllegalArgumentException(
                    "Illegal matching direction value" + direction);
            }
            this.nodePosLabelEdgeMap = getNodePosLabelEdgeMap(direction);
            this.nodeInvLabelEdgeMap = getNodeInvLabelEdgeMap(direction);
            this.direction = direction;
        }

        /**
         * Computes the matches according to this algorithm, for a given graph
         * and a given set of images for the start node (set at construction
         * time). A second parameter gives the option of passing in a set of
         * potential end images; if not <code>null</code>, the computation
         * will terminate when it has found all the end images.
         * @param graph the graph in which we are trying to find matches
         * @param startImages the allowed images of the start node of the
         *        algorithm; may not be <code>null</code>
         * @param endImages the allowed images for the end node of the
         *        algorithm; may be <code>null</code> if all end images are
         *        allowed
         */
        public NodeRelation computeMatches(GraphShape graph,
                Set<? extends Node> startImages, Set<? extends Node> endImages) {
            this.graph = graph;
            this.endImages = endImages;
            this.result = createRelation(graph);
            for (Node startImage : startImages) {
                addMatches(startImage);
            }
            return this.result;
        }

        /**
         * Indicats if a match can be found between two nodes of a graph.
         * @param graph the graph in which we are trying to find matches
         * @param startImage the required image of the start node
         * @param endImage the required image of the end node
         */
        public boolean hasMatch(Graph graph, Node startImage, Node endImage) {
            this.graph = graph;
            this.result = createRelation(graph);
            addMatches(startImage);
            return this.remainingImageCount == 0;
        }

        /**
         * Adds matches for a given image of the start node (set at construction
         * time). A second parameter gives the option of passing in a set of
         * potential end images; if not <code>null</code>, the computation
         * will terminate when it has found all the end images.
         */
        public void addMatches(Node startImage) {
            // this.matchMap = new HashMap();
            this.startImage = startImage;
            // if the set of end images is not null, count the number of
            // remaining images
            if (this.endImages == null || hasWildcards()) {
                // it makes no sense to try and count images
                this.remainingImageCount = -1;
            } else {
                this.remainingImageCount = this.endImages.size();
            }
            if (isAcceptsEmptyWord()) {
                this.result.addSelfRelated(startImage);
            }
            this.matched = new SetNodeRelation(this.graph);
            extend(this.start, startImage);
        }

        /**
         * Extends the matchings found so far with a given key-image pair. If
         * this is a real extension, it is subsequently propagated.
         * @param key the node from the automaton that has been matched
         * @param image the node from the graph that has been found as a new
         *        image
         */
        private void extend(Node key, Node image) {
            if (key == this.end) {
                if ((this.endImages == null || this.endImages.contains(image))
                    && addRelated(this.result, this.startImage, image)) {
                    if (this.remainingImageCount > 0) {
                        this.remainingImageCount--;
                    }
                }
            } else {
                // test if this is a new key-image pair that deserves to be
                // propagated
                // if the key is not cyclic, the match is certainly new
                boolean isNew = !isCyclic(key);
                if (!isNew) {
                    // a cyclic key is new if we did not see it before
                    if (this.matched == null) {
                        this.matched = new SetNodeRelation(this.graph);
                    }
                    // Set imageSet = (Set) matchMap.get(key);
                    // if (imageSet == null) {
                    // matchMap.put(key, imageSet = new HashSet());
                    // }
                    // isNew = imageSet.add(image);
                    isNew = this.matched.addRelated(key, image);
                }
                if (isNew) {
                    propagate(key, image);
                }
            }
        }

        /**
         * Propagates a previously made extension to the matchings, by also
         * matching the "outgoing" edges of the key-image pair.
         * @param key the node from the automaton that has been matched
         * @param image the node from the graph that has been added as a new
         *        image
         * @see #getPosEdgeSet(GraphShape, Node)
         * @see #getOpposite(Edge)
         */
        private void propagate(Node key, Node image) {
            extend(getPosLabelEdgeMap(key), getPosEdgeSet(this.graph, image),
                true);
            extend(getInvLabelEdgeMap(key), getInvEdgeSet(this.graph, image),
                false);
        }

        /**
         * Extends the match map with key-image pairs derived from the end
         * points of equi-labelled edges from automaton and graph. Also takes
         * wildcard labels into account.
         */
        private void extend(Map<Label,Set<Edge>> keyLabelEdgeMap,
                Collection<? extends Edge> imageEdgeSet, boolean positive) {
            if (keyLabelEdgeMap != null) {
                Iterator<? extends Edge> imageEdgeIter =
                    imageEdgeSet.iterator();
                while (this.remainingImageCount != 0 && imageEdgeIter.hasNext()) {
                    Edge imageEdge = imageEdgeIter.next();
                    extend(keyLabelEdgeMap.get(imageEdge.label()), imageEdge,
                        positive);
                    extend(keyLabelEdgeMap.get(WILDCARD_LABEL), imageEdge,
                        positive);
                }
            }
        }

        /**
         * Extends the match map with key-image pairs derived from the end
         * points of equi-labelled edges from automaton and graph.
         */
        private void extend(Set<Edge> keyEdgeSet, Edge imageEdge,
                boolean positive) {
            if (keyEdgeSet != null) {
                for (Edge keyEdge : keyEdgeSet) {
                    Node imageNode =
                        positive ? getOpposite(imageEdge)
                                : getThisEnd(imageEdge);
                    extend(getOpposite(keyEdge), imageNode);
                }
            }
        }

        /**
         * Callback factory method. Creates a relation over a given graph. This
         * implementation returns a {@link SetNodeRelation}.
         */
        protected NodeRelation createRelation(GraphShape graph) {
            return new SetNodeRelation(graph);
        }

        /**
         * Retrieves the label-to-node-sets mapping for a given automaton node.
         */
        protected Map<Label,Set<Edge>> getPosLabelEdgeMap(Node node) {
            return this.nodePosLabelEdgeMap.get(node);
        }

        /**
         * Retrieves the inverse-label-to-node-sets mapping for a given
         * automaton node.
         */
        protected Map<Label,Set<Edge>> getInvLabelEdgeMap(Node node) {
            return this.nodeInvLabelEdgeMap.get(node);
        }

        /**
         * Returns the "outgoing" edge set for a given graph node. This may be
         * implemented either by the outgoing or by the incoming edges,
         * depending on whether we do forward or backward matching.
         * @see #getInvEdgeSet(GraphShape, Node)
         */
        protected Collection<? extends Edge> getPosEdgeSet(GraphShape graph,
                Node node) {
            switch (this.direction) {
            case FORWARD:
                return graph.outEdgeSet(node);
            default:
                return graph.edgeSet(node, Edge.TARGET_INDEX);
            }
        }

        /**
         * Returns the "incoming" edge set for a given graph node. This may be
         * implemented either by the incoming or by the outgoing edges,
         * depending on whether we do forward or backward matching.
         * @see #getPosEdgeSet(GraphShape, Node)
         */
        protected Collection<? extends Edge> getInvEdgeSet(GraphShape graph,
                Node node) {
            switch (this.direction) {
            case FORWARD:
                return graph.edgeSet(node, Edge.TARGET_INDEX);
            default:
                return graph.outEdgeSet(node);
            }
        }

        /**
         * Returns the "opposite" end of a graph edge. This may be either the
         * target or the source, depending on whether we do forward or backward
         * matching.
         */
        protected Node getThisEnd(Edge edge) {
            switch (this.direction) {
            case FORWARD:
                return edge.source();
            default:
                return edge.opposite();
            }
        }

        /**
         * Returns the "opposite" end of a graph edge. This may be either the
         * target or the source, depending on whether we do forward or backward
         * matching.
         */
        protected Node getOpposite(Edge edge) {
            switch (this.direction) {
            case FORWARD:
                return edge.opposite();
            default:
                return edge.source();
            }
        }

        /**
         * Adds a related pair to the result relation, with given pre- and
         * post-images or swapped, depending on whether we do forward or
         * backward matching.
         */
        protected boolean addRelated(NodeRelation result, Node startImage,
                Node endImage) {
            switch (this.direction) {
            case FORWARD:
                return result.addRelated(startImage, endImage);
            default:
                return result.addRelated(endImage, startImage);
            }
        }

        /**
         * Automaton node where the matching starts. This may be the automaton's
         * start or end node, depending on whether we do forward or backward
         * matching.
         */
        private final Node start;

        /**
         * Automaton node where the matching ends. This may be the automaton's
         * end or start node, depending on whether we do forward or backward
         * matching.
         */
        private final Node end;

        /**
         * Mapping from automaton nodes to label-to-opposite-node-ends maps. May
         * reflect the automaton's outgoing or incoming edge structure,
         * depending on whether we do forward or backward matching.
         */
        private final Map<Node,Map<Label,Set<Edge>>> nodePosLabelEdgeMap;

        /**
         * Mapping from automaton nodes to inverse label-to-opposite-node-ends
         * maps. May reflect the automaton's outgoing or incoming edge
         * structure, depending on whether we do forward or backward matching.
         */
        private final Map<Node,Map<Label,Set<Edge>>> nodeInvLabelEdgeMap;
        /**
         * Flag indicating if we are doing forward or backward matching.
         */
        private final int direction;

        /**
         * Graph on which the current matching computation is performed.
         */
        private transient GraphShape graph;

        /**
         * Start image for the current matching computation.
         */
        private transient Node startImage;

        /**
         * Set of potential end images for the current matching computation.
         */
        private transient Set<? extends Node> endImages;

        /**
         * Number of images yet to add. Only used if non-negative.
         */
        private transient int remainingImageCount;

        /**
         * Relation where the result of the current matching computation is
         * built.
         */
        private transient NodeRelation result;

        private transient NodeRelation matched;
    }
}