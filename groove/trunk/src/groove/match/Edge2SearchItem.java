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
 * $Id: Edge2SearchItem.java,v 1.15 2008-01-30 09:33:29 iovka Exp $
 */
package groove.match;

import groove.graph.DefaultEdge;
import groove.graph.Edge;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.algebra.ValueNode;
import groove.match.SearchPlanStrategy.Search;
import groove.rel.RegExprLabel;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A search item that searches an image for an edge.
 * @author Arend Rensink
 * @version $Revision $
 */
class Edge2SearchItem extends AbstractSearchItem {
    /**
     * Creates a search item for a given binary edge.
     * @param edge the edge to be matched
     */
    public Edge2SearchItem(Edge edge) {
        this.edge = edge;
        this.source = edge.source();
        this.target = edge.target();
        this.label =
            RegExprLabel.isSharp(edge.label())
                    ? RegExprLabel.getSharpLabel(edge.label()) : edge.label();
        this.selfEdge = this.source == this.target;
        this.boundNodes = new HashSet<Node>();
        this.boundNodes.add(edge.source());
        this.boundNodes.add(edge.target());
    }

    /**
     * Returns the end nodes of the edge.
     */
    @Override
    public Collection<? extends Node> bindsNodes() {
        return this.boundNodes;
    }

    /** Returns the singleton set consisting of the matched edge. */
    @Override
    public Collection<? extends Edge> bindsEdges() {
        return Collections.singleton(this.edge);
    }

    /**
     * Returns the edge for which this item tests.
     */
    public Edge getEdge() {
        return this.edge;
    }

    @Override
    public String toString() {
        return String.format("Find %s", getEdge());
    }

    /**
     * This implementation first attempts to compare edge labels and ends, if
     * the other search item is also an {@link Edge2SearchItem}; otherwise, it
     * delegates to super.
     */
    @Override
    public int compareTo(SearchItem other) {
        int result = 0;
        if (other instanceof Edge2SearchItem) {
            // compare first the edge labels, then the edge ends
            Edge otherEdge = ((Edge2SearchItem) other).getEdge();
            result = getEdge().label().compareTo(otherEdge.label());
            if (result == 0) {
                result = this.edge.source().compareTo(otherEdge.source());
            }
            if (result == 0) {
                result = this.edge.target().compareTo(otherEdge.target());
            }
        }
        if (result == 0) {
            return super.compareTo(other);
        } else {
            return result;
        }
    }

    public void activate(SearchPlanStrategy strategy) {
        // one would like the following assertion,
        // but since negative search items for the same edge also reserve the
        // index, the assertion may fail in case of a positive and negative test
        // on the same edge (stupid!)
        // assert !strategy.isEdgeFound(edge);
        this.edgeIx = strategy.getEdgeIx(this.edge);
        this.sourceFound = strategy.isNodeFound(this.source);
        this.sourceIx = strategy.getNodeIx(this.source);
        if (this.selfEdge) {
            this.targetFound = this.sourceFound;
            this.targetIx = this.sourceIx;
        } else {
            this.targetFound = strategy.isNodeFound(this.target);
            this.targetIx = strategy.getNodeIx(this.target);
        }
    }

    /**
     * This method returns the hash code of the label as rating.
     */
    @Override
    int getRating() {
        return this.edge.label().hashCode();
    }

    final public Record getRecord(Search search) {
        if (isPreMatched(search)) {
            // the edge is unexpectedly pre-matched
            return createDummyRecord();
        } else if (isSingular(search)) {
            return createSingularRecord(search);
        } else {
            return createMultipleRecord(search);
        }
    }

    /** Indicates if the edge is pre-matched in the search. */
    boolean isPreMatched(Search search) {
        return search.getEdgeAnchor(this.edgeIx) != null;
    }

    /** Indicates if the edge has a singular image in the search. */
    boolean isSingular(Search search) {
        boolean sourceSingular =
            this.sourceFound || search.getNodeAnchor(this.sourceIx) != null;
        boolean targetSingular =
            this.targetFound || search.getNodeAnchor(this.targetIx) != null;
        return sourceSingular && targetSingular;
    }

    /** Creates a record for the case the image is singular. */
    SingularRecord createSingularRecord(Search search) {
        return new Edge2SingularRecord(search, this.edgeIx, this.sourceIx,
            this.targetIx);
    }

    /** Creates a record for the case the image is not singular. */
    MultipleRecord<Edge> createMultipleRecord(Search search) {
        return new Edge2MultipleRecord(search, this.edgeIx, this.sourceIx,
            this.targetIx, this.sourceFound, this.targetFound);
    }

    /**
     * The edge for which this search item is to find an image.
     */
    final Edge edge;
    /**
     * The source end of {@link #edge}, separately stored for efficiency.
     */
    final Node source;
    /**
     * The target end of {@link #edge}, separately stored for efficiency.
     */
    final Node target;
    /** The label of {@link #edge}, separately stored for efficiency. */
    final Label label;
    /**
     * Flag indicating that {@link #edge} is a self-edge.
     */
    final boolean selfEdge;
    /** The set of end nodes of this edge. */
    private final Set<Node> boundNodes;

    /** The index of the edge in the search. */
    int edgeIx;
    /** The index of the source in the search. */
    int sourceIx;
    /** The index of the target in the search. */
    int targetIx;
    /** Indicates if the source is found before this item is invoked. */
    boolean sourceFound;
    /** Indicates if the target is found before this item is invoked. */
    boolean targetFound;

    /**
     * Search record to be used if the edge image is completely determined by
     * the pre-matched ends.
     * @author Arend Rensink
     * @version $Revision $
     */
    class Edge2SingularRecord extends SingularRecord {
        /** Constructs an instance for a given search. */
        public Edge2SingularRecord(Search search, int edgeIx, int sourceIx,
                int targetIx) {
            super(search);
            this.edgeIx = edgeIx;
            this.sourceIx = sourceIx;
            this.targetIx = targetIx;
            this.sourcePreMatch = search.getNodeAnchor(sourceIx);
            this.targetPreMatch = search.getNodeAnchor(targetIx);
        }

        @Override
        final boolean set() {
            Edge image = getEdgeImage();
            assert image != null;
            boolean result = isImageCorrect(image);
            if (result) {
                this.search.putEdge(this.edgeIx, image);
            }
            return result;
        }

        @Override
        public void reset() {
            super.reset();
            this.search.putEdge(this.edgeIx, null);
        }

        /** Tests if the (uniquely determined) edge image can be used. */
        boolean isImageCorrect(Edge image) {
            return this.host.containsEdge(image);
        }

        /**
         * Creates and returns the edge image, as constructed from the available
         * end node images.
         */
        private Edge getEdgeImage() {
            Node sourceFind = this.sourcePreMatch;
            if (sourceFind == null) {
                sourceFind = this.search.getNode(this.sourceIx);
            }
            assert sourceFind != null : String.format(
                "Source node of %s has not been found",
                Edge2SearchItem.this.edge);
            Node targetFind = this.targetPreMatch;
            if (targetFind == null) {
                targetFind = this.search.getNode(this.targetIx);
            }
            assert targetFind != null : String.format(
                "Target node of %s has not been found",
                Edge2SearchItem.this.edge);
            return DefaultEdge.createEdge(sourceFind, getLabel(), targetFind);
        }

        /** Callback method to determine the label of the edge image. */
        Label getLabel() {
            return Edge2SearchItem.this.label;
        }

        @Override
        public String toString() {
            return Edge2SearchItem.this.toString() + " = " + getEdgeImage();
        }

        /** The pre-matched (fixed) source image, if any. */
        private final Node sourcePreMatch;
        /** The pre-matched (fixed) target image, if any. */
        private final Node targetPreMatch;
        /** The index of the edge in the search. */
        private final int edgeIx;
        /** The index of the source in the search. */
        private final int sourceIx;
        /** The index of the target in the search. */
        private final int targetIx;
    }

    /**
     * Record of an edge search item, storing an iterator over the candidate
     * images.
     * @author Arend Rensink
     * @version $Revision $
     */
    class Edge2MultipleRecord extends MultipleRecord<Edge> {
        /**
         * Creates a record based on a given search.
         */
        Edge2MultipleRecord(Search search, int edgeIx, int sourceIx,
                int targetIx, boolean sourceFound, boolean targetFound) {
            super(search);
            this.edgeIx = edgeIx;
            this.sourceIx = sourceIx;
            this.targetIx = targetIx;
            this.sourceFound = sourceFound;
            this.targetFound = targetFound;
            this.sourcePreMatch = search.getNodeAnchor(sourceIx);
            this.targetPreMatch = search.getNodeAnchor(targetIx);
            assert search.getEdge(edgeIx) == null : String.format(
                "Edge %s already in %s", Edge2SearchItem.this.edge, search);
        }

        @Override
        void init() {
            this.sourceFind = this.sourcePreMatch;
            if (this.sourceFind == null && this.sourceFound) {
                this.sourceFind = this.search.getNode(this.sourceIx);
                assert this.sourceFind != null : String.format(
                    "Source node of %s not found", Edge2SearchItem.this.edge);
            }
            this.targetFind = this.targetPreMatch;
            if (this.targetFind == null && this.targetFound) {
                this.targetFind = this.search.getNode(this.targetIx);
                assert this.targetFind != null : String.format(
                    "Target node of %s not found", Edge2SearchItem.this.edge);
            }
            initImages();
        }

        @Override
        boolean setImage(Edge image) {
            Node source = image.source();
            if (this.sourceFind == null) {
                // maybe the prospective source image was used as
                // target image of this same edge in the previous attempt
                rollBackTargetImage();
                if (!this.search.putNode(this.sourceIx, source)) {
                    return false;
                }
            } else if (this.checkSource) {
                if (source != this.sourceFind) {
                    return false;
                }
            }
            Node target = image.target();
            if (Edge2SearchItem.this.selfEdge) {
                if (target != source) {
                    return false;
                }
            } else {
                if (this.targetFind == null) {
                    if (!this.search.putNode(this.targetIx, target)) {
                        return false;
                    }
                } else if (this.checkTarget) {
                    if (target != this.targetFind) {
                        return false;
                    }
                }
            }
            if (this.checkLabel) {
                if (image.label() != Edge2SearchItem.this.label) {
                    return false;
                }
            }
            if (this.setEdge) {
                this.search.putEdge(this.edgeIx, image);
            }
            this.selected = image;
            return true;
        }

        @Override
        public void reset() {
            super.reset();
            if (this.setEdge) {
                this.search.putEdge(this.edgeIx, null);
            }
            rollBackSourceImage();
            rollBackTargetImage();
            this.selected = null;
        }

        /** Rolls back the image set for the source. */
        private void rollBackSourceImage() {
            if (this.sourceFind == null) {
                this.search.putNode(this.sourceIx, null);
            }
        }

        /** Rolls back the image set for the source. */
        private void rollBackTargetImage() {
            if (this.targetFind == null && !Edge2SearchItem.this.selfEdge) {
                this.search.putNode(this.targetIx, null);
            }
        }

        /**
         * IOVKA this comment is not updated ! Returns an iterator over those
         * images for {@link #edge} that are consistent with the pre-matched
         * edge ends. The iterator actually selects the returned edges in the
         * result map, as a side effect.
         */
        void initImages() {
            Set<? extends Edge> result = null;
            boolean checkLabel = false;
            // it does not pay off here to take only the incident edges of
            // pre-matched ends,
            // no doubt because building the necessary additional data
            // structures takes more
            // time than is saved by trying out fewer images
            Set<? extends Edge> labelEdgeSet =
                this.host.labelEdgeSet(Edge2SearchItem.this.label);
            if (this.sourceFind != null) {
                Set<? extends Edge> nodeEdgeSet =
                    this.host.edgeSet(this.sourceFind);
                if (nodeEdgeSet.size() < labelEdgeSet.size()) {
                    result = nodeEdgeSet;
                    checkLabel = true;
                }
            } else if (this.targetFind != null) {
                Set<? extends Edge> nodeEdgeSet =
                    this.host.edgeSet(this.targetFind);
                if (nodeEdgeSet == null) {
                    assert this.targetFind instanceof ValueNode : String.format(
                        "Host graph does not contain edges for node %s",
                        this.targetFind);
                    result = Collections.emptySet();
                } else if (nodeEdgeSet.size() < labelEdgeSet.size()) {
                    result = nodeEdgeSet;
                    checkLabel = true;
                }
            }
            if (result == null) {
                result = labelEdgeSet;
            }
            initImages(result, true, true, checkLabel, true);
        }

        /**
         * Callback method to set the iterator over potential images. Also sets
         * flags indicating whether potential images still have to be checked
         * for correctness of the source, target or label parts.
         * @param imageSet the iterator over potential images
         * @param checkSource if <code>true</code>, the sources of potential
         *        images have to be compared with {@link #sourceFind}
         * @param checkTarget if <code>true</code>, the sources of potential
         *        images have to be compared with {@link #targetFind}
         * @param checkLabel if <code>true</code>, the sources of potential
         *        images have to be compared with #label.
         */
        final void initImages(Set<? extends Edge> imageSet,
                boolean checkSource, boolean checkTarget, boolean checkLabel,
                boolean setEdge) {
            this.imageIter = imageSet.iterator();
            this.checkSource = checkSource;
            this.checkTarget = checkTarget;
            this.checkLabel = checkLabel;
            this.setEdge = setEdge;
        }

        @Override
        public String toString() {
            return Edge2SearchItem.this.toString() + " = " + this.selected;
        }

        /** The index of the edge in the search. */
        final private int edgeIx;
        /** The index of the source in the search. */
        final int sourceIx;
        /** The index of the target in the search. */
        final int targetIx;
        /** Indicates if the source is found before this item is invoked. */
        final private boolean sourceFound;
        /** Indicates if the target is found before this item is invoked. */
        final private boolean targetFound;

        private final Node sourcePreMatch;
        private final Node targetPreMatch;
        /**
         * The pre-matched image for the edge source, if any. A value of
         * <code>null</code> means that no image is currently selected for the
         * source, or the source was pre-matched.
         */
        Node sourceFind;
        /**
         * The pre-matched image for the edge target, if any. A value of
         * <code>null</code> means that no image is currently selected for the
         * target, or the target was pre-matched.
         */
        Node targetFind;
        /**
         * Flag indicating the if sources of images returned by
         * {@link #initImages()} have to be checked against the found source
         * image.
         */
        private boolean checkSource;
        /**
         * Flag indicating the if targets of images returned by
         * {@link #initImages()} have to be checked against the found target
         * image.
         */
        private boolean checkTarget;
        /**
         * Flag indicating the if labels of images returned by
         * {@link #initImages()} have to be checked against the edge label.
         */
        private boolean checkLabel;
        /**
         * Flag indicating if the edge image should actually be set in the
         * search.
         */
        private boolean setEdge;
        /** Image found by the latest call to {@link #find()}, if any. */
        Edge selected;
    }
}
