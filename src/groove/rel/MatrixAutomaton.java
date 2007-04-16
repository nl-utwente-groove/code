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
package groove.rel;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import groove.graph.BinaryEdge;
import groove.graph.DefaultGraph;
import groove.graph.DefaultLabel;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Node;

/**
 * An implementation of regular automata that also keeps track 
 * of the valuation of the variables.
 */
public class MatrixAutomaton extends DefaultGraph implements VarAutomaton {
    /**
     * Indication of forward matching direction.
     */
    static private final int FORWARD = 0;
    /**
     * Indication of backward matching direction.
     */
    static private final int BACKWARD = 1;
    /** Constant wildcard label serving as a key in label-to-edge-sets maps. */
    static private final RegExprLabel WILDCARD_LABEL = new RegExprLabel(RegExpr.wildcard());
    /**
     * Class to encapsulate the algorithm used to compute the result of
     * {@link VarAutomaton#getMatches(Graph, Set, Set, Map)}.
     */
    protected class MatchingAlgorithm {
        /** Dummy object used in matching. */
        private final MatchingComputation MATCH_DUMMY = new MatchingComputation(0, null, null);
        /** 
         * Wraps a single computation of the enclosing {@link MatchingAlgorithm}.
         * An object of this class is a one-shot affair, computing the end images
         * given a key-image pair. The computation is started using {@link #start()}.
         * The result of the computation is a set of end images.
         * Computations can <i>depend</i> on one another, meaning that
         * the results of one computation are copied to the dependent ones.
         */
        protected class MatchingComputation extends HashMap<Node,Set<Map<String,Label>>> {
            /** 
             * Constructs a computation for a given key-image pair, as
             * a sub-computation of another (the <i>dependent</i> computation).
             * The results of this computation are copied to the dependent one.
             * @param keyIndex the key (index in the automaton's node array) for this computation
             * @param image the image (graph node) for this computation
             * @param dependent the dependent computation in the context of which
             * this one occurs
             * @param valuation the initial valuation for this computation
             */
            public MatchingComputation(int keyIndex, Node image, MatchingComputation dependent, Map<String, Label> valuation) {
                this.keyIndex = keyIndex;
                this.image = image;
                this.valuation = valuation;
                if (dependent != null) {
                    dependents = new ArrayList<MatchingComputation>();
                    dependents.add(dependent);
                }
            }
            
            /** 
             * Constructs a computation for a given key-image pair, without dependent computation.
             * @param keyIndex the key (index in the automaton's node array) for this computation
             * @param image the image (graph node) for this computation
             * @param valuation the initial valuation for this computation
             */
            public MatchingComputation(int keyIndex, Node image, Map<String, Label> valuation) {
                this(keyIndex, image, null, valuation);
                // if the set of end images is not null, count the number of remaining images
                if (isCountingEdgeImages()) {
                    remainingImageCount = endImages.size();
                } else {
                    // it makes no sense to try and count images
                    remainingImageCount = -1;
                }
                if (!isStoringIntermediates()) {
                    cleanOldMatches();
                }
            }
            
            /**
             * Starts this computation.
             * May be invoked only once.
             * @return an alias to this computation, as a set of end images
             */
            public Map<Node, Set<Map<String, Label>>> start() {
                propagate(keyIndex, image, valuation);
                // store the new results in all the dependent sets, if any
                if (dependents != null) {
                	for (MatchingComputation dependent: dependents) {
                        dependent.addAll(this);
                    }
                    dependents = null;
                } 
                return this;
            }
            
            /**
             * Propagates a previously made extension to the matchings, by also matching the
             * "outgoing" edges of the key-image pair.
             * @param keyIndex the node from the automaton that has been matched
             * @param image the node from the graph that has been added as a new image
             * @param valuation the valuation of the wildcard names encountered so far
             * @see #getPosEdgeSet(Node)
             * @see #getOpposite(Edge)
             */
            private void propagate(int keyIndex, Node image, Map<String,Label> valuation) {
                extend(getPosLabelEdgeMap(keyIndex), getPosEdgeSet(image), valuation, true);
                extend(getInvLabelEdgeMap(keyIndex), getInvEdgeSet(image), valuation, false);
            }

            /**
             * Extends the match map with key-image pairs derived from the
             * end points of equi-labelled edges from automaton and graph.
             * Also takes wildcard labels into account.
             * @param valuation the valuation of the wildcard names encountered so far
             */
            private void extend(Map<Label,int[]> keyLabelEdgeMap, Collection<? extends Edge> imageEdgeSet, Map<String,Label> valuation, boolean positive) {
                if (keyLabelEdgeMap != null) {
                    Iterator<? extends Edge> imageEdgeIter = imageEdgeSet.iterator();
                    while (remainingImageCount != 0 && imageEdgeIter.hasNext()) {
                        Edge imageEdge = imageEdgeIter.next();
                        Label imageLabel = imageEdge.label();
                        Node imageNode = positive ? getOpposite(imageEdge) : getThisEnd(imageEdge);
                        extend(keyLabelEdgeMap.get(imageEdge.label()), imageNode, imageLabel, valuation);
                        extend(keyLabelEdgeMap.get(WILDCARD_LABEL), imageNode, imageLabel, valuation);
                    }
                }
            }
            
            /**
             * Extends the result with key-image pairs derived from the
             * end points of equi-labelled edges from automaton and graph.
             * @param label the label of the graph edge, used if the automaton edge is a named wildcard
             * @param valuation the valuation of the wildcard names encountered so far
             */
            private void extend(int[] keyEdgeIndices, Node imageNode, Label label, Map<String,Label> valuation) {
                if (keyEdgeIndices != null) {
                    for (int i = 0; remainingImageCount != 0 && i < keyEdgeIndices.length; i++) {
                        int keyEdgeIndex = keyEdgeIndices[i];
                        boolean labelOk;
                        String id = RegExprLabel.getWildcardId(getLabel(keyEdgeIndex));
                        if (id != null) {
                            // we have a wildcard id; let's look it up
                            Label oldLabel = valuation.get(id);
                            if (oldLabel == null) {
                                // it's a new id; store it
                                labelOk = true;
                                if (valuation == null) {
                                    valuation = Collections.singletonMap(id, label);
                                } else {
                                    valuation = new HashMap<String,Label>(valuation);
                                    valuation.put(id, label);
                                }
                            } else {
                                // it's a know id; check its value
                                labelOk = oldLabel.equals(label);
                            }
                        } else {
                            // this is not a wildcard id; just go on
                            labelOk = true;
                        }
                        if (labelOk) {
                            extend(getOpposite(keyEdgeIndex), imageNode, valuation);
                        }
                    }
                }
            }
            
            /**
             * Extends the matchings found so far with a given key-image pair. If this is a real
             * extension, it is subsequently propagated.
             * @param keyIndex the node from the automaton that has been matched
             * @param image the node from the graph that has been found as a new image
             * @param valuation the valuation of the wildcard names encountered so far
             */
            private void extend(int keyIndex, Node image, Map<String,Label> valuation) {
                if (keyIndex == endIndex) {
                    add(image, valuation);
                } else if (!isCyclic(keyIndex)) {
                    // if the key is not cyclic, we can propagate the match immediately
                    propagate(keyIndex, image, valuation);
                } else {
                    MatchingComputation previous = getMatch(keyIndex,image);
                    if (previous != null) {
                        // image has been encountered before for this keyIndex
                        if (isStoringIntermediates()) {
                            previous.copyTo(this);
                        }
                        // if we're not storing intermediates, nothing needs to be done
                    } else {
                        // image is new for this keyIndex
                        if (isStoringIntermediates()) {
                            // start a sub-process
                            // create a new result set for the key/image-pair
                            MatchingComputation newResult = new MatchingComputation(keyIndex, image, this, valuation);
                            putMatch(newResult);
                            newResult.start();
                        } else {
                            // just store a dummy object; we do the work ourselves
                            putDummyMatch(keyIndex, image);
                            propagate(keyIndex, image, valuation);
                        }
                    }
                }
            }

            /**
             * Adds a combination of edge image and wildcard name valuation
             * to the currently stored result.
             */
            public boolean add(Node image, Map<String, Label> valuation) {
                if (isStoringIntermediates() || isAllowedResult(image)) {
                    if (hasVars()) {
                        // add the valuations to those stored for the image
                        Set<Map<String,Label>> currentValuations = get(image);
                        if (currentValuations == null) {
                            put(image, currentValuations = new HashSet<Map<String,Label>>());
                        }
                        return currentValuations.add(valuation);
                    } else {
                        // store the result and
                        boolean result = super.put(image, Collections.<Map<String,Label>>emptySet()) == null;
                        if (result) {
                            if (remainingImageCount > 0) {
                                remainingImageCount--;
                            }
                        }
                        return result;
                    }
                } else {
                    return false;
                }
            }
            
            /**
             * Adds all results from another computation to this one.
             */
            public void addAll(MatchingComputation other) {
                if (hasVars()) {
                	for (Map.Entry<Node,Set<Map<String,Label>>> otherEntry: other.entrySet()) {
                        Node image = otherEntry.getKey();
                        Set<Map<String,Label>> valuations = otherEntry.getValue();
                        // add the valuations to those stored for the image
                        Set<Map<String,Label>> currentValuations = get(image);
                        if (currentValuations == null) {
                            put(image, currentValuations = new HashSet<Map<String,Label>>());
                        }
                        currentValuations.addAll(valuations);
                    }
                } else {
                    putAll(other);
                }
            }

            /**
             * Retrieves a result from the previously computed matches.
             * @param keyIndex the index of the automaton node for the required result
             * @param image the graph node for the required result
             * @return the result previously computed for <code>keyIndex</code> and <code>image</code>,
             * or <code>null</code> if no result is stored
             * @see #putMatch(MatchingComputation)
             */
            protected MatchingComputation getMatch(int keyIndex, Node image) {
                if (auxResults[keyIndex] == null) {
                    return null;
                } else {
                    return auxResults[keyIndex].get(image);
                }
            }

            /**
             * Inserts a result in the store of computations.
             * The key index and graph node of the result are available from the object itself.
             * @param result the previously computed result to be stored
             * @see #getMatch(int, Node)
             */
            protected void putMatch(MatchingComputation result) {
                Map<Node,MatchingComputation> matched = auxResults[result.keyIndex];
                if (matched == null) {
                    matched = auxResults[result.keyIndex] = new HashMap<Node,MatchingComputation>();
                }
                matched.put(result.image, result);
            }

            /**
             * Inserts a dummy object in the store of computations.
             * The key index and graph node of the result are available from the object itself.
             * @param keyIndex the index of the automaton node at which the result is to be stored
             * @param image the graph node for the required result
             * @see #getMatch(int, Node)
             */
            protected void putDummyMatch(int keyIndex, Node image) {
                Map<Node,MatchingComputation> matched = auxResults[keyIndex];
                if (matched == null) {
                    matched = auxResults[keyIndex] = new HashMap<Node,MatchingComputation>();
                }
                matched.put(image, MATCH_DUMMY);
            }
            
            /**
             * Makes sure that the results of this computation are copied to another.
             * This is either done straight away, if this computation is already finished,
             * or the other is appended to the dependent computations, in which case
             * the results will be copied as soon as this one is done.
             * @param other the dependent computation to copy the results to.
             */
            protected void copyTo(MatchingComputation other) {
                if (isBusy()) {
                    dependents.add(other);
                } else {
                    other.addAll(this);
                }
            }
            
            /**
             * Indicates if this computation is still budy computing,
             * that is, its {@link #start()} method is running.
             */
            protected boolean isBusy() {
                return dependents != null;
            }

            /**
             * The collection of dependent sets.
             * If <code>null</code>, the computation is considered to be finished.
             */
            private Collection<MatchingComputation> dependents;
            /** The automaton key for which this computation has bee constructs. */
            private final int keyIndex;
            /** The graph node for which this computation has bee constructs. */
            private final Node image;
            /**
             * The initial valuation for the matching. 
             */
            private final Map<String,Label> valuation;
        }
        
        /**
         * Creates a matching algorithm for a given direction of matching
         * @param direction either {@link #FORWARD} or {@link #BACKWARD}.
         */
        public MatchingAlgorithm(int direction) {
            switch (direction) {
            case FORWARD :
                this.startIndex = getStartNodeIndex();
                this.endIndex = getEndNodeIndex();
                break;
            case BACKWARD :
                this.startIndex = getEndNodeIndex();
                this.endIndex = getStartNodeIndex();
                break;
            default :
                throw new IllegalArgumentException("Illegal matching direction value"+direction);
            }
            this.nodePosLabelEdgeMap = getNodePosLabelEdgeMap(direction);
            this.nodeInvLabelEdgeMap = getNodeInvLabelEdgeMap(direction);
            this.direction = direction;
        }

        /**
         * Computes the matches according to this algorithm, for a given graph and a given set of
         * images for the start node (set at construction time). A second parameter gives the option
         * of passing in a set of potential end images; if not <code>null</code>, the computation
         * will terminate when it has found all the end images.
         * @param graph the graph in which we are trying to find matches
         * @param startImages the allowed images of the start node of the algorithm;
         * may not be <code>null</code>
         * @param endImages the allowed images for the end node of the algorithm; 
         * may be <code>null</code> if all end images are allowed
         * @param valuation initial mapping from variables to labels
         */
        public NodeRelation computeMatches(Graph graph, Set<? extends Node> startImages, Set<? extends Node> endImages, Map<String, Label> valuation) {
            if (graph != this.graph) {
                // we're working on a different graph, so the previous matchings are no good
                cleanOldMatches();
                this.graph = graph;
            }
            this.endImages = endImages;
            this.storeIntermediates = !hasVars();// && endImages == null;// && startImages.size() > 1;
            this.result = createRelation(graph);
            for (Node startImage: startImages) {
                if (isAcceptsEmptyWord() && isAllowedResult(startImage)) {
                    result.addSelfRelated(startImage);
                }
                Map<Node,Set<Map<String,Label>>> resultMap = new MatchingComputation(startIndex, startImage, valuation).start();
                for (Map.Entry<Node,Set<Map<String,Label>>> resultEntry: resultMap.entrySet()) {
                    Node endImage = resultEntry.getKey();
                    if (isAllowedResult(endImage)) {
                        if (hasVars()) {
                            addRelated(startImage, endImage, resultEntry.getValue());
                        } else {
                            addRelated(startImage, endImage);
                        }
                    }
                }
            }
            return result;
        }
//
//        /**
//         * Indicats if a match can be found between two nodes of a graph.
//         * @param graph the graph in which we are trying to find matches
//         * @param startImage the required image of the start node
//         * @param endImage the required image of the end node
//         */
//        public boolean hasMatch(Graph graph, Node startImage, Node endImage) {
//            this.graph = graph;
//            this.result = createRelation(graph);
//            computeEndImages(startImage);
//            return remainingImageCount == 0;
//        }

        /**
         * Callback factory method. Creates a relation over a given graph. This implementation
         * returns a {@link SetNodeRelation}.
         */
        protected NodeRelation createRelation(Graph graph) {
            return new SetNodeRelation(graph);
        }

        /**
         * Retrieves the label-to-node-sets mapping for a given automaton node.
         */
        protected Map<Label, int[]> getPosLabelEdgeMap(int nodeIndex) {
            return nodePosLabelEdgeMap[nodeIndex];
        }

        /**
         * Retrieves the inverse-label-to-node-sets mapping for a given automaton node.
         */
        protected Map<Label, int[]> getInvLabelEdgeMap(int nodeIndex) {
            return nodeInvLabelEdgeMap[nodeIndex];
        }

        /**
         * Returns the "outgoing" edge set for a given graph node. This may be implemented either by
         * the outgoing or by the incoming edges, depending on whether we do forward or backward
         * matching.
         * @see #getInvEdgeSet(Node)
         */
        protected Collection<? extends Edge> getPosEdgeSet(Node node) {
            switch (direction) {
            case FORWARD : return graph.outEdgeSet(node);
            default : return graph.edgeSet(node, Edge.TARGET_INDEX);
            }
        }

        /**
         * Returns the "incoming" edge set for a given graph node. This may be implemented either by
         * the incoming or by the outgoing edges, depending on whether we do forward or backward
         * matching.
         * @see #getPosEdgeSet(Node)
         */
        protected Collection<? extends Edge> getInvEdgeSet(Node node)  {
            switch (direction) {
            case FORWARD : return graph.edgeSet(node, Edge.TARGET_INDEX);
            default : return graph.outEdgeSet(node);
            }
        }

        /**
         * Returns the "opposite" end of a graph edge. This may be either the target or the source,
         * depending on whether we do forward or backward matching.
         */
        protected Node getThisEnd(Edge edge) {
            switch (direction) {
            case FORWARD : return edge.source();
            default : return edge.opposite();
            }
        }

        /**
         * Returns the "opposite" end of a graph edge. This may be either the target or the source,
         * depending on whether we do forward or backward matching.
         */
        protected Node getOpposite(Edge edge) {
            switch (direction) {
            case FORWARD : return edge.opposite();
            default : return edge.source();
            }
        }

        /**
         * Returns the "opposite" end index of an automaton edge index.
         * This may be either the target or the source,
         * depending on whether we do forward or backward matching.
         */
        protected int getOpposite(int edgeIndex) {
            switch (direction) {
            case FORWARD : return getTarget(edgeIndex);
            default : return getSource(edgeIndex);
            }
        }

        /**
         * Adds a related pair to the result relation, with
         * given pre- and post-images or swapped, depending on whether we do
         * forward or backward matching.
         */
        protected boolean addRelated(Node startImage, Node endImage) {
            switch (direction) {
            case FORWARD : 
                return result.addRelated(startImage, endImage);
            default : 
                return result.addRelated(endImage, startImage);
            }
        }

        /**
         * Adds a set of edges to the result relation, with
         * given pre- and post-images or swapped, depending on whether we do
         * forward or backward matching, and labels containing wildcard
         * name valuations.
         */
        protected boolean addRelated(Node startImage, Node endImage, Set<Map<String, Label>> valuations) {
            boolean res = false;
            for( Map<String,Label> valuation: valuations) {
//                Label label = new ValuationLabel(valuation);
                ValuationEdge edge;
                switch (direction) {
                case FORWARD : 
                    edge = createValuationEdge(startImage, endImage, valuation);
                    break;
                default : 
                    edge = createValuationEdge(endImage, startImage, valuation);
                }
                res |= result.addRelated(edge);
            }
            return res;
        }
        
        /**
         * Callback factory method for a {@link ValuationEdge}.
         */
        protected ValuationEdge createValuationEdge(Node source, Node target, Map<String,Label> valuation) {
        	return new ValuationEdge(source, target, valuation);
        }
        
        protected void cleanOldMatches() {
            // just a security in case we're in pre-initialization phase
            if (auxResults != null) {
                for (int i = 0; i < auxResults.length; i++) {
                    auxResults[i] = null;
                }
            }
        }
        
        /**
         * Tests if a given node is in the pre-set set of allowd end images.
         * Always returns <code>true</code> if there is no such set.
         */
        protected boolean isAllowedResult(Node image) {
            return endImages == null || endImages.contains(image);
        }
        
        /**
         * Indicates if we will optimize by counting the number of discovered end images
         * (and comparing this with the required number).
         */
        protected boolean isCountingEdgeImages() {
            return endImages != null && !hasVars() && !isStoringIntermediates();
        }
        
        /**
         * Indicates if we will optimize by storing intermediate results,
         * in the form of sets of end images found for other key-image pairs.
         */
        protected boolean isStoringIntermediates() {
//            return false;
            return storeIntermediates;
        }

        /**
         * Automaton node where the matching starts. This may be the automaton's start or end node,
         * depending on whether we do forward or backward matching.
         */
        private final int startIndex;

        /**
         * Automaton node where the matching ends. This may be the automaton's end or start node,
         * depending on whether we do forward or backward matching.
         */
        private final int endIndex;

        /**
         * Mapping from automaton nodes to label-to-opposite-node-ends maps. May reflect the
         * automaton's outgoing or incoming edge structure, depending on whether we do forward or
         * backward matching.
         */
        private final Map<Label,int[]>[] nodePosLabelEdgeMap;

        /**
         * Mapping from automaton nodes to inverse label-to-opposite-node-ends maps. May reflect the
         * automaton's outgoing or incoming edge structure, depending on whether we do forward or
         * backward matching.
         */
        private final Map<Label,int[]>[] nodeInvLabelEdgeMap;
        /**
         * Flag indicating if we are doing forward or backward matching.
         */
        private final int direction;

        /**
         * Graph on which the current matching computation is performed.
         */
        private transient Graph graph;

        /**
         * Set of potential end images for the current matching computation.
         */
        private transient Set<? extends Node> endImages;

        /**
         * Number of images yet to add.
         * Only used if non-negative.
         */
        private transient int remainingImageCount;

        /**
         * Relation where the result of the current matching computation is built.
         */
        private transient NodeRelation result;
        /** Flag indicating if intermediate results are to be stored during matching. */
        private transient boolean storeIntermediates;

        /**
         * Array containing for each automaton node a mapping from images of that node
         * to either a dummy object indicating that the image has been found, or, if
         * {@link #isStoringIntermediates()} holds, a set of end images reachable from the
         * automaton key/graph image pairs.
         */
        private final Map<Node,MatchingComputation>[] auxResults = new Map[indexedNodeCount()];
    }

    /**
     * Creates an automaton with a fresh start and end node,
     * which does not accept the empty word.
     */
    public MatrixAutomaton() {
        this.start = addNode();
        this.end = addNode();
    }
    
    public Node getStartNode() {
        return start;
    }
    
    public Node getEndNode() {
        return end;
    }

    public boolean isAcceptsEmptyWord() {
        return acceptsEmptyWord;
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
        result.append("\nStart node: "+getStartNode());
        result.append("\nEnd node: "+getEndNode());
        result.append("\nAccepts empty word: "+isAcceptsEmptyWord());
        return result.toString();
    }

    public boolean accepts(List<String> word) {
        if (word.isEmpty()) {
            return isAcceptsEmptyWord();
        } else {
            // keep the set of current matches (initially the start node)
            Map<Node,? extends Map<String,String>> matchSet = Collections.singletonMap(getStartNode(), new HashMap<String,String>());
            boolean accepts = false;
            // go through the word
            for (int index = 0; !accepts && !matchSet.isEmpty() && index < word.size(); index++) {
                boolean lastIndex = index == word.size() - 1;
                Map<Node,Map<String,String>> newMatchSet = new HashMap<Node,Map<String,String>>();
                Iterator<? extends Map.Entry<Node,? extends Map<String,String>>> matchIter = matchSet.entrySet().iterator();
                while (!accepts && matchIter.hasNext()) {
                    Map.Entry<Node,? extends Map<String,String>> matchEntry = matchIter.next();
                    Node match = matchEntry.getKey();
                    Map<String,String> idMap = matchEntry.getValue();
                    Iterator<? extends Edge> outEdgeIter = outEdgeSet(match).iterator();
                    while (!accepts && outEdgeIter.hasNext()) {
                        Edge outEdge = outEdgeIter.next();
                        Label label = outEdge.label();
                        String wildcardId = RegExprLabel.getWildcardId(label);
                        boolean labelOK;
                        if (wildcardId == null) {
                            labelOK = RegExprLabel.isWildcard(label) || label.text().equals(word.get(index));
                        } else {
                            idMap = new HashMap<String,String>(idMap);
                            String oldIdValue = idMap.put(wildcardId, label.text());
                            labelOK = oldIdValue == null || oldIdValue.equals(label.text());
                        }
                        if (labelOK) {
                            // if we're at the last index, we don't have to build the new mtch set
                            if (lastIndex) {
                                accepts = outEdge.opposite().equals(getEndNode());
                            } else {
                                newMatchSet.put(outEdge.opposite(), idMap);
                            }
                        }
                    }
                }
                matchSet = newMatchSet;
            }
            return accepts;
        }
    }

    public NodeRelation getMatches(Graph graph, Set<? extends Node> startImages, Set<? extends Node> endImages, Map<String,Label> valuation) {
        if (valuation == null) {
            valuation = Collections.emptyMap();
        }
        if (startImages != null) {
            // do forward maching from the start images
            return getMatchingAlgorithm(FORWARD).computeMatches(graph, startImages, endImages, valuation);
        } else if (endImages != null) {
            // do backwards matching from the end images
            return getMatchingAlgorithm(BACKWARD).computeMatches(graph, endImages, startImages, valuation);            
        } else {
            // if we don't have any start or end images, 
            // create a set of start images using the automaton's initial edges
            return getMatchingAlgorithm(FORWARD).computeMatches(graph, createStartImages(graph), endImages, valuation);
            
        }
    }

    public NodeRelation getMatches(Graph graph, Set<? extends Node> startImages, Set<? extends Node> endImages) {
        return getMatches(graph, startImages, endImages, null);
    }
    
    /**
     * Creates a set of start nodes to be used in the search for matches
     * if no explicit start nodes are provided.
     * @see #getMatches(Graph, Set, Set, Map)
     */
    protected Set<Node> createStartImages(Graph graph) {
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
    
    private void addStartImages(Set<Node> result, Graph graph, boolean positive) {
        Label[] initLabels = positive ? getInitPosLabels() : getInitInvLabels();
        for (int i = 0; i < initLabels.length; i++) {
            Label initLabel = initLabels[i];
            for (Edge graphEdge: graph.labelEdgeSet(2, initLabel)) {
                Node end = positive ? graphEdge.source() : graphEdge.opposite();
                result.add(end);
            }
        }
    }

    /**
     * Returns the set of all wildcard variables occurring in this automaton.
     */
    public Set<String> allVarSet() {
        if (allVarSet == null) {
            initVarSets();
        }
        return Collections.unmodifiableSet(allVarSet);
    }

    /**
     * Returns the set of wildcard variables bound in this automaton.
     * A variable is bound if it occurrs on every path from start to end node.
     */
    public Set<String> boundVarSet() {
        if (boundVarSet == null) {
            initVarSets();
        }
        return Collections.unmodifiableSet(boundVarSet);
    }
    
    /**
     * Indicates if this automaton includes wildcard variables.
     * Convenience method for <code>!getWildcardIds().isEmpty()</code>.
     */
    public boolean hasVars() {
        if (allVarSet == null) {
            initVarSets();
        }
        return ! allVarSet.isEmpty();
    }
    
    /**
     * Indicates if this automaton includes wildcard variables.
     * Convenience method for <code>!getWildcardIds().isEmpty()</code>.
     */
    public boolean bindsVars() {
        if (boundVarSet == null) {
            initVarSets();
        }
        return ! boundVarSet.isEmpty();
    }
    
    /**
     * Indicates if a given variable occurs in this automaton.
     * Convenience method for <code>allVarSet().contains(var)</code>.
     */
    public boolean hasVar(String var) {
        if (allVarSet == null) {
            initVarSets();
        }
        return allVarSet.contains(var);
    }
    
    /**
     * Indicates if this automaton binds a given wildcard variable.
     * Convenience method for <code>boundVarSet().contains(var)</code>.
     */
    public boolean bindsVar(String var) {
        if (boundVarSet == null) {
            initVarSets();
        }
        return ! boundVarSet.contains(var);
    }
    
    /**
     * Returns a matching algorithm for a given matching direction.
     * The algorithm is created on demand, using {@link #createMatchingAlgorithm(int)}.
     * @param direction the matching direction: either {@link #FORWARD} or {@link #BACKWARD}
     */
    protected MatchingAlgorithm getMatchingAlgorithm(int direction) {
        if (algorithm == null) {
            algorithm = new MatchingAlgorithm[2];
            algorithm[FORWARD] = createMatchingAlgorithm(FORWARD);
            algorithm[BACKWARD] = createMatchingAlgorithm(BACKWARD);
        }
        return algorithm[direction];
    }
    
    /**
     * Callback factory method to create a matching algorithm for a given matching direction.
     * This implementation returns an {@link MatchingAlgorithm}
     * @param direction the matching direction: either {@link #FORWARD} or {@link #BACKWARD}
     */
    protected MatchingAlgorithm createMatchingAlgorithm(int direction) {
        return new MatchingAlgorithm(direction);
    }

    /**
     * Returns a mapping from source nodes to mappings from labels to (non-empty)
     * sets of (automaton) edges with that source node and label.
     * The map is created on demand using {@link #initNodeLabelEdgeMaps()}.
     */
    protected Map<Label, int[]>[] getNodePosLabelEdgeMap(int direction) {
        if (nodePosLabelEdgeIndicesMap == null) {
            initNodeLabelEdgeMaps();
        }
        return nodePosLabelEdgeIndicesMap[direction];
    }

    /**
     * Returns a mapping from source nodes to mappings from labels to (non-empty)
     * sets of (automaton) edges with that source node, and the label wrapped in 
     * a {@link RegExpr.Inv}.
     * The map is created on demand using {@link #initNodeLabelEdgeMaps()}.
     */
    protected Map<Label, int[]>[] getNodeInvLabelEdgeMap(int direction) {
        if (nodeInvLabelEdgeIndicesMap == null) {
            initNodeLabelEdgeMaps();
        }
        return nodeInvLabelEdgeIndicesMap[direction];
    }
    /**
     * Returns the set of positive labels occurring on initial edges.
     * Guaranteed to be non-<code>null</code>.
     * The map is created on demand in {@link #initNodeLabelEdgeMaps()}.
     */
    protected Label[] getInitPosLabels() {
        if (initPosLabels == null) {
            initNodeLabelEdgeMaps();
        }
        return initPosLabels;
    }
    /**
     * Returns the set of inverse labels occurring on initial edges.
     * Guaranteed to be non-<code>null</code>.
     * The map is created on demand in {@link #initNodeLabelEdgeMaps()}.
     */
    protected Label[] getInitInvLabels() {
        if (initInvLabels == null) {
            initNodeLabelEdgeMaps();
        }
        return initInvLabels;
    }
    
    /**
     * Indicates if the automaton has an initial (normal or inverse) edge
     * with a wildcard label inside.
     */
    protected boolean isInitWildcard() {
        if (initPosLabels == null) {
            initNodeLabelEdgeMaps();
        }
        return initWildcard;
    }
    
    /**
     * Initializes all the node-to-label-to-edge-sets maps of this
     * automaton.
     * @throws IllegalStateException if the method is called before the graph is fixed
     */
    protected void initNodeLabelEdgeMaps() {
        if (! isFixed()) {
            throw new IllegalStateException("Maps cannot be calculated reliably before automaton is closed");
        }
        Set<Label> initPosLabelSet = new HashSet<Label>();
        Set<Label> initInvLabelSet = new HashSet<Label>();
        Map<Label,Set<Edge>>[][] nodeInvLabelEdgeMap = new Map[2][indexedNodeCount()];
        Map<Label,Set<Edge>>[][] nodePosLabelEdgeMap = new Map[2][indexedNodeCount()];
        for (Edge edge: edgeSet()) {
            Label label = edge.label();
            RegExpr invOperand = RegExprLabel.getInvOperand(label);
            boolean isInverse = invOperand != null;
            if (isInverse && invOperand.isWildcard() || RegExprLabel.isWildcard(label)) {
                label = WILDCARD_LABEL;
            } else if (isInverse) {
                // strip the inverse operator and create a default label
                label = DefaultLabel.createLabel(invOperand.getAtomText());
            }
            for (int direction = FORWARD; direction <= BACKWARD; direction++) {
                Node end = (direction == FORWARD) ? edge.source() : edge.opposite();
                if (isInverse) {
                    addToNodeLabelEdgeSetMap(nodeInvLabelEdgeMap[direction], end, label, edge);
                    if (edge.source() == getStartNode()) {
                        initInvLabelSet.add(label);
                    }
                } else {
                    addToNodeLabelEdgeSetMap(nodePosLabelEdgeMap[direction], end, label, edge);
                    if (edge.source() == getStartNode()) {
                        initPosLabelSet.add(label);
                    }
                }
            }
            if (edge.source() == getStartNode()) {
                if (label == WILDCARD_LABEL) {
                    initWildcard = true;
                } else {
                    (isInverse ? initInvLabelSet : initPosLabelSet).add(label);
                }
            }
        }
        // now convert the sets of nodes to arrays of node indices
        initPosLabels = new Label[initPosLabelSet.size()];
        initPosLabelSet.toArray(initPosLabels);
        initInvLabels = new Label[initInvLabelSet.size()];
        initInvLabelSet.toArray(initInvLabels);
        this.nodePosLabelEdgeIndicesMap = new Map[2][indexedNodeCount()];
        this.nodeInvLabelEdgeIndicesMap = new Map[2][indexedNodeCount()];
        for (int direction = FORWARD; direction<= BACKWARD; direction++) {
            for (int nodeIndex = 0; nodeIndex < indexedNodeCount(); nodeIndex++) {
                this.nodePosLabelEdgeIndicesMap[direction][nodeIndex] = toIntArrayMap(nodePosLabelEdgeMap[direction][nodeIndex]);
                this.nodeInvLabelEdgeIndicesMap[direction][nodeIndex] = toIntArrayMap(nodeInvLabelEdgeMap[direction][nodeIndex]);
            }
        }
        initPosLabels = new Label[initPosLabelSet.size()];
        initPosLabelSet.toArray(initPosLabels);
    }
    
    /**
     * Tests if a given node lies on a cycle reachable from the
     * start state.
     * This implementation uses a precomputed set, constructed on demand using
     * {@link #initNodeIndices()}.
     */
    protected boolean isCyclic(int nodeIndex) {
        if (cyclicNodes == null) {
            initNodeIndices();
        }
        return cyclicNodes.get(nodeIndex);
    }

    /**
     * Initializes the node indices and the set of cyclic nodes.
     * @see #getIndex(Node)
     * @see #isCyclic(int)
     */
    protected void initNodeIndices() {
        nodeIndexMap = new HashMap<Node,Integer>();
        Set<Node> cyclicNodeSet = new HashSet<Node>();
        cyclicNodes = new BitSet(nodeCount());
        // the nodeList is a precursor to the nodes array
        List<Node> nodeList = new ArrayList<Node>();
        nodeList.add(getStartNode());
        // the set of nodes already investigated
        Set<Node> visitedNodes = new HashSet<Node>();
        // set the index of the first node
        int nodeIndex = 0;
        while (nodeIndex < nodeList.size()) {
            Node node = nodeList.get(nodeIndex);
            nodeIndexMap.put(node, new Integer(nodeIndex));
            nodeIndex++;
            for (Edge outEdge: outEdgeSet(node)) {
                Node opposite = outEdge.opposite();
                if (visitedNodes.add(opposite)) {
                    nodeList.add(opposite);
                } else {
                    cyclicNodeSet.add(opposite);
                }
            }
        }
        // convert the node list to a node array
        nodes = new Node[nodeList.size()];
        nodeList.toArray(nodes);
        // convert the cyclic node set to a bitset
        cyclicNodes = new BitSet(nodeIndex);
        for (Node cyclicNode: cyclicNodeSet) {
            cyclicNodes.set(getIndex(cyclicNode));
        }
    }

    /**
     * Initializes the node indices and the set of cyclic nodes.
     * @see #getIndex(Node)
     * @see #isCyclic(int)
     */
    protected void initEdgeIndices() {
        edgeIndexMap = new HashMap<Edge,Integer>();
        // the following lists are precursors to the corresponding arrays
        List<Node> sourceList = new ArrayList<Node>();
        List<Node> targetList = new ArrayList<Node>();
        List<Label> labelList = new ArrayList<Label>();
        // set the index of the first edge
        int edgeIndex = 0;
        for (Edge edge: edgeSet()) {
            edgeIndexMap.put(edge, edgeIndex);
            edgeIndex++;
            sourceList.add(edge.source());
            targetList.add(edge.opposite());
            labelList.add(edge.label());
        }
        // convert the lists to arrays
        sources = toIntArray(sourceList);
        targets = toIntArray(targetList);
        labels = new Label[labelList.size()];
        labelList.toArray(labels);
    }
    
    /**
     * Initializes the sets of all variables and bound variables.
     * @see #allVarSet()
     * @see #boundVarSet()
     */
    protected void initVarSets() {
        // traverse the automaton
        Set<Node> remainingNodes = new HashSet<Node>();
        remainingNodes.add(getStartNode());
        // keep maps from automaton nodes to all vars and bound vars
        Map<Node,Set<String>> allVarMap = new HashMap<Node,Set<String>>();
        allVarMap.put(getStartNode(), new HashSet<String>());
        Map<Node,Set<String>> boundVarMap = new HashMap<Node,Set<String>>();
        boundVarMap.put(getStartNode(), new HashSet<String>());
        while (! remainingNodes.isEmpty()) {
            Node source = remainingNodes.iterator().next();
            remainingNodes.remove(source);
            Set<String> sourceAllVarSet = allVarMap.get(source);
            Set<String> sourceBoundVarSet = boundVarMap.get(source);
            Iterator<? extends Edge> outEdgeIter = outEdgeSet(source).iterator();
            while (outEdgeIter.hasNext()) {
                BinaryEdge outEdge = (BinaryEdge) outEdgeIter.next();
                Node target = outEdge.target();
                Set<String> targetAllVarSet = new HashSet<String>(sourceAllVarSet);
                Set<String> targetBoundVarSet = new HashSet<String>(sourceBoundVarSet);
                if (outEdge.label() instanceof RegExprLabel) {
                    RegExpr expr = ((RegExprLabel) outEdge.label()).getRegExpr();
                    targetAllVarSet.addAll(expr.allVarSet());
                    targetBoundVarSet.addAll(expr.boundVarSet());
                }
                if (allVarMap.containsKey(target)) {
                    // the target is known; take the union of all vars and the intersection of the bound vars
                    allVarMap.get(target).addAll(targetAllVarSet);
                    boundVarMap.get(target).retainAll(targetAllVarSet);
                } else {
                    // the target is new; store all and bound vars
                    remainingNodes.add(target);
                    allVarMap.put(target, targetAllVarSet);
                    boundVarMap.put(target, targetBoundVarSet);
                }
            }
        }
        this.allVarSet = allVarMap.get(getEndNode());
        if (allVarSet == null) {
            allVarSet = Collections.emptySet();
        }
        this.boundVarSet = boundVarMap.get(getEndNode());
        if (boundVarSet == null) {
            boundVarSet = Collections.emptySet();
        }
    }
    
    /**
     * Adds a combination of node, label and edge to one of the maps in this automaton.
     */
    private void addToNodeLabelEdgeSetMap(Map<Label,Set<Edge>>[] nodeLabelEdgeSetMap, Node node, Label label, Edge edge) {
        Map<Label,Set<Edge>> labelEdgeMap = nodeLabelEdgeSetMap[getIndex(node)];
        if (labelEdgeMap == null) {
            nodeLabelEdgeSetMap[getIndex(node)] = labelEdgeMap = new HashMap<Label,Set<Edge>>();
        }
        Set<Edge> edgeSet = labelEdgeMap.get(label);
        if (edgeSet == null) {
            labelEdgeMap.put(label, edgeSet = new HashSet<Edge>());
        }
        edgeSet.add(edge);
    }
    
    /**
     * Returns the index calculated for a given node.
     * The indices are initialized on demand using {@link #initNodeIndices()}.
     * @param node the node for which the index is to be returned
     * @return the index for <code>node</code>, or <code>-1</code> if
     * <code>node</code> is not a known node.
     */
    protected final int getIndex(Node node) {
        if (nodeIndexMap == null) {
            initNodeIndices();
        }
        Integer result = nodeIndexMap.get(node);
        if (result != null) {
            return result;
        } else {
            return -1;
        }
    }
    
    /**
     * Returns the index calculated for a given edge.
     * The indices are initialized on demand using {@link #initEdgeIndices()}.
     * @param edge the edge for which the index is to be returned
     * @return the index for <code>edge</code>, or <code>-1</code> if
     * <code>edge</code> is not a known edge.
     */
    protected final int getIndex(Edge edge) {
        if (edgeIndexMap == null) {
            initEdgeIndices();
        }
        Integer result = edgeIndexMap.get(edge);
        if (result != null) {
            return result;
        } else {
            return -1;
        }
    }
    
    /**
     * Returns the number of indexed nodes.
     * This number is one higher than the highest valid node index.
     * @see #getNode(int)
     */
    protected final int indexedNodeCount() {
        if (nodes == null) {
            initNodeIndices();
        }
        return nodes.length;
    }
    
    /**
     * Returns the number of indexed nodes.
     * This number is one higher than the highest valid node index.
     * @see #getNode(int)
     */
    protected final int indexedEdgeCount() {
        if (sources == null) {
            initEdgeIndices();
        }
        return sources.length;
    }
    
    /**
     * Returns the index of the start node.
     * Always returns <code>0</code>.
     */
    protected final int getStartNodeIndex() {
        return 0;
    }
    
    /**
     * Returns the node at a given index.
     * Inverse mapping of {@link #getIndex(Node)}.
     * Initialized on demand in {@link #initNodeIndices()}.
     * @param nodeIndex the index of the node to be retrieved
     */
    protected final Node getNode(int nodeIndex) {
        if (nodes == null) {
            initNodeIndices();
        }
        return nodes[nodeIndex];
    }
    
    /**
     * Returns the source index of an edge at a given index.
     * Inverse mapping of {@link #getIndex(Node)}.
     * Initialized on demand in {@link #initNodeIndices()}.
     * @param edgeIndex the index of the node to be retrieved
     */
    protected final int getSource(int edgeIndex) {
        if (sources == null) {
            initEdgeIndices();
        }
        return sources[edgeIndex];
    }
    
    /**
     * Returns the target index of an edge at a given index.
     * Inverse mapping of {@link #getIndex(Node)}.
     * Initialized on demand in {@link #initNodeIndices()}.
     * @param edgeIndex the index of the node to be retrieved
     */
    protected final int getTarget(int edgeIndex) {
        if (targets == null) {
            initEdgeIndices();
        }
        return targets[edgeIndex];
    }
    
    /**
     * Returns the label at a given index.
     * Inverse mapping of {@link #getIndex(Node)}.
     * Initialized on demand in {@link #initNodeIndices()}.
     * @param edgeIndex the index of the node to be retrieved
     */
    protected final Label getLabel(int edgeIndex) {
        if (labels == null) {
            initEdgeIndices();
        }
        return labels[edgeIndex];
    }

    /**
     * Returns the index of the end node.
     * Convenience method for <code>getIndex(getEndNode())</code>.
     */
    protected final int getEndNodeIndex() {
        if (endNodeIndex < 0) {
            endNodeIndex = getIndex(getEndNode());
        }
        return endNodeIndex;
    }

    /** Returns a node with a number that is guaranteed to be fresh every time. */
    @Override
    public Node createNode() {
        return new DefaultNode();
    }
    
    private Map<Label, int[]> toIntArrayMap(Map<Label, ? extends Collection<? extends Element>> labelSetMap) {
        if (labelSetMap != null) {
            Map<Label,int[]> result = new HashMap<Label,int[]>();
            for (Map.Entry<Label,? extends Collection<? extends Element>> entry: labelSetMap.entrySet()) {
                result.put(entry.getKey(), toIntArray(entry.getValue()));
            }
            return result;
        } else {
            return null;
        }
    }

    private int[] toIntArray(Collection<? extends Element> elementSet) {
        int[] result = new int[elementSet.size()];
        int i = 0;
        for (Element element: elementSet) {
            result[i] = (element instanceof Node) ? getIndex((Node) element)
                    : getIndex((Edge) element);
            i++;
        }
        return result;
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
     * Direction-indexed array of mappings from nodes in this automaton 
     * to maps from labels to corresponding sets of edges, where the node key
     * is either the source node or the target node of the edge, depending on the direction. 
     * Initialized using  {@link #initNodeLabelEdgeMaps()}.
     */
    private Map<Label,int[]>[][] nodePosLabelEdgeIndicesMap;
//
//    /**
//     * Mapping from target nodes in this automaton to maps from labels to corresponding sets of edges.
//     * Initialized using  {@link #initNodeLabelEdgeMaps()}.
//     */
//    private Map posTargetLabelEdgeMap;
    /**
     * Direction-indexed array of mappings from nodes in this automaton to maps from labels 
     * to sets of edges where the label occurs in the context of a {@link RegExpr.Inv}. 
     * The node key
     * is either the source node or the target node of the edge, depending on the direction.
     * Initialized using  {@link #initNodeLabelEdgeMaps()}.
     */
    private Map<Label,int[]>[][] nodeInvLabelEdgeIndicesMap;
    /**
     * The set of positive labels occurring on initial edges of this automaton.
     */
    private Label[] initPosLabels;
    /**
     * The set of inverse labels occurring on initial edges of this automaton.
     */
    private Label[] initInvLabels;
    /** 
     * Flag to indicate that the initial edges of this automaton include one with wildcard label.
     * Used to create a set of initial start images if none is provided.
     */
    private boolean initWildcard;
    /**
     * The set of nodes of this automaton that lie on a cycle reachable from the
     * start node.
     */
    private BitSet cyclicNodes;
    /**
     * Mapping from the nodes of this automaton to node indices.
     */
    private Map<Node,Integer> nodeIndexMap;
    /**
     * Array of nodes, in the order of the node indices.
     * Provides the inverse mapping to {@link #nodeIndexMap}.
     */
    private Node[] nodes;
    /**
     * Mapping from the edges of this automaton to edge indices.
     */
    private Map<Edge,Integer> edgeIndexMap;
    /**
     * Array of edge sources, in the order of the edge indices.
     * Provides the inverse mapping to {@link #edgeIndexMap}.
     */
    private int[] sources;
    /**
     * Array of edge targets, in the order of the edge indices.
     * Provides the inverse mapping to {@link #edgeIndexMap}.
     */
    private int[] targets;
    /**
     * Array of edge labels, in the order of the edge indices.
     * Provides the inverse mapping to {@link #edgeIndexMap}.
     */
    private Label[] labels;
    /**
     * Set of wildcard ids occurring in this automaton.
     */
    private Set<String> allVarSet;
    /**
     * Set of wildcard ids occurring in this automaton.
     */
    private Set<String> boundVarSet;
    /**
     * The index of the end node.
     */
    private int endNodeIndex = -1;
    /**
     * Array of algorithm for matching, indexed by the matching direction.
     */
    private MatchingAlgorithm[] algorithm;
}