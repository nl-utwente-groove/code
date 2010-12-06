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
import groove.graph.LabelStore;
import groove.graph.Node;
import groove.match.SearchPlanStrategy.Search;
import groove.trans.RuleEdge;
import groove.trans.RuleNode;
import groove.util.NestedIterator;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A search item that searches an image for a node type edge.
 * @author Arend Rensink
 * @version $Revision $
 */
class NodeTypeSearchItem extends AbstractSearchItem {
    /**
     * Creates a search item for a given node type edge.
     * @param edge the edge to be matched
     * @param labelStore label store containing the subtypes of the node type
     */
    public NodeTypeSearchItem(RuleEdge edge, LabelStore labelStore) {
        this.edge = edge;
        this.source = edge.source();
        this.label = edge.label();
        assert this.label.isNodeType() : String.format(
            "Label '%s' is not a node type", this.label);
        this.boundNodes = new HashSet<RuleNode>(Arrays.asList(edge.source()));
        Set<Label> labelStoreSubtypes = labelStore.getSubtypes(this.label);
        this.subtypes =
            labelStoreSubtypes == null ? null : new HashSet<Label>(
                labelStoreSubtypes);
        this.hasProperSubtypes =
            this.subtypes != null && this.subtypes.size() > 1;
    }

    /**
     * Returns the end nodes of the edge.
     */
    @Override
    public Collection<RuleNode> bindsNodes() {
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
        return String.format("Find node type %s", getEdge());
    }

    /**
     * This implementation first attempts to compare edge labels and ends, if
     * the other search item is also an {@link NodeTypeSearchItem}; otherwise,
     * it delegates to super.
     */
    @Override
    public int compareTo(SearchItem other) {
        int result = 0;
        if (other instanceof NodeTypeSearchItem) {
            // compare first the edge labels, then the edge ends
            Edge otherEdge = ((NodeTypeSearchItem) other).getEdge();
            result = getEdge().label().compareTo(otherEdge.label());
            if (result == 0) {
                result = getEdge().source().compareTo(otherEdge.source());
            }
            if (result == 0) {
                result = getEdge().target().compareTo(otherEdge.target());
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

    /** Indicates if the source node has a singular image in the search. */
    boolean isSingular(Search search) {
        boolean sourceSingular =
            this.sourceFound || search.getNodeAnchor(this.sourceIx) != null;
        return sourceSingular;
    }

    /** Creates a record for the case the image is singular. */
    SingularRecord createSingularRecord(Search search) {
        return new NodeTypeSingularRecord(search, this.edgeIx, this.sourceIx);
    }

    /** Creates a record for the case the image is not singular. */
    MultipleRecord<Edge> createMultipleRecord(Search search) {
        return new NodeTypeMultipleRecord(search, this.edgeIx, this.sourceIx,
            this.sourceFound);
    }

    /**
     * The (node type) edge for which this search item is to find an image.
     */
    final RuleEdge edge;
    /**
     * The source end of {@link #edge}, separately stored for efficiency.
     */
    final RuleNode source;
    /** The label of {@link #edge}, separately stored for efficiency. */
    final Label label;
    /** The set of end nodes of this edge. */
    private final Set<RuleNode> boundNodes;

    /** The index of the edge in the search. */
    int edgeIx;
    /** The index of the source in the search. */
    int sourceIx;
    /** Indicates if the source is found before this item is invoked. */
    boolean sourceFound;
    /** The collection of subtypes of this node type. */
    final Collection<Label> subtypes;
    /** Flag indicating if the node type has non-trivial subtypes. */
    final boolean hasProperSubtypes;

    /**
     * Search record to be used if the node type image is completely determined
     * by the pre-matched end.
     * @author Arend Rensink
     * @version $Revision $
     */
    class NodeTypeSingularRecord extends SingularRecord {
        /** Constructs an instance for a given search. */
        public NodeTypeSingularRecord(Search search, int edgeIx, int sourceIx) {
            super(search);
            this.edgeIx = edgeIx;
            this.sourceIx = sourceIx;
            this.sourcePreMatch = search.getNodeAnchor(sourceIx);
        }

        @Override
        final boolean set() {
            boolean result = false;
            if (NodeTypeSearchItem.this.hasProperSubtypes) {
                // iterate over the subtypes
                Iterator<Edge> edgeImageIter = getEdgeImageIter();
                while (edgeImageIter.hasNext()) {
                    Edge edgeImage = edgeImageIter.next();
                    result = isImageCorrect(edgeImage);
                    if (result) {
                        this.search.putEdge(this.edgeIx, edgeImage);
                        break;
                    }
                }
            } else {
                // there is no proper subtype, so we only need try out the node
                // type itself
                Edge edgeImage = getEdgeImage();
                result = isImageCorrect(edgeImage);
                if (result) {
                    this.search.putEdge(this.edgeIx, edgeImage);
                }
            }
            return result;
        }

        @Override
        public void reset() {
            super.reset();
            this.search.putEdge(this.edgeIx, null);
        }

        /** Tests if the (uniquely determined) edge image can be used. */
        private boolean isImageCorrect(Edge image) {
            return this.host.containsEdge(image);
        }

        /**
         * Returns an iterator over potential edge images created from subtypes.
         */
        private Iterator<Edge> getEdgeImageIter() {
            final Node sourceFind =
                this.sourcePreMatch == null
                        ? this.search.getNode(this.sourceIx)
                        : this.sourcePreMatch;
            assert sourceFind != null : String.format(
                "Source node of %s has not been found",
                NodeTypeSearchItem.this.edge);
            final Iterator<Label> subtypeIter =
                NodeTypeSearchItem.this.subtypes.iterator();
            return new Iterator<Edge>() {
                @Override
                public boolean hasNext() {
                    return subtypeIter.hasNext();
                }

                @Override
                public Edge next() {
                    Label subtype = subtypeIter.next();
                    return DefaultEdge.createEdge(sourceFind, subtype,
                        sourceFind);
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };

        }

        /**
         * Creates and returns the edge image, as constructed from the available
         * end node images.
         */
        private Edge getEdgeImage() {
            final Node sourceFind =
                this.sourcePreMatch == null
                        ? this.search.getNode(this.sourceIx)
                        : this.sourcePreMatch;
            assert sourceFind != null : String.format(
                "Source node of %s has not been found",
                NodeTypeSearchItem.this.edge);
            return DefaultEdge.createEdge(sourceFind, getLabel(), sourceFind);
        }

        /** Callback method to determine the label of the edge image. */
        private Label getLabel() {
            return NodeTypeSearchItem.this.label;
        }

        @Override
        public String toString() {
            return NodeTypeSearchItem.this.toString() + " <= " + getEdgeImage();
        }

        /** The pre-matched (fixed) source image, if any. */
        private final Node sourcePreMatch;
        /** The index of the edge in the search. */
        private final int edgeIx;
        /** The index of the source in the search. */
        private final int sourceIx;
    }

    /**
     * Record of a node type search item, storing an iterator over the candidate
     * images.
     * @author Arend Rensink
     * @version $Revision $
     */
    class NodeTypeMultipleRecord extends MultipleRecord<Edge> {
        /**
         * Creates a record based on a given search.
         */
        NodeTypeMultipleRecord(Search search, int edgeIx, int sourceIx,
                boolean sourceFound) {
            super(search);
            this.edgeIx = edgeIx;
            this.sourceIx = sourceIx;
            this.sourceFound = sourceFound;
            this.sourcePreMatch = search.getNodeAnchor(sourceIx);
            assert search.getEdge(edgeIx) == null : String.format(
                "Edge %s already in %s", NodeTypeSearchItem.this.edge, search);
        }

        @Override
        void init() {
            this.sourceFind = this.sourcePreMatch;
            if (this.sourceFind == null && this.sourceFound) {
                this.sourceFind = this.search.getNode(this.sourceIx);
                assert this.sourceFind != null : String.format(
                    "Source node of %s not found", NodeTypeSearchItem.this.edge);
            }
            initImages();
        }

        @Override
        boolean setImage(Edge image) {
            assert image.target() == image.source();
            if (this.sourceFind == null) {
                if (!this.search.putNode(this.sourceIx, image.source())) {
                    return false;
                }
            } else if (this.checkSource) {
                if (image.source() != this.sourceFind) {
                    return false;
                }
            }
            if (this.checkLabel) {
                if (NodeTypeSearchItem.this.hasProperSubtypes
                    && NodeTypeSearchItem.this.subtypes.contains(image.label())
                    || NodeTypeSearchItem.this.label == image.label()) {
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
            this.selected = null;
        }

        /** Rolls back the image set for the source. */
        private void rollBackSourceImage() {
            if (this.sourceFind == null) {
                this.search.putNode(this.sourceIx, null);
            }
        }

        /**
         * Callback method to set the iterator over potential images. Also sets
         * flags indicating whether potential images still have to be checked
         * for correctness of the source or label parts.
         */
        private void initImages() {
            if (this.sourceFind != null) {
                // only check the source node edges
                this.imageIter = this.host.edgeSet(this.sourceFind).iterator();
                this.checkLabel = true;
                this.checkSource = false;
            } else {
                if (NodeTypeSearchItem.this.hasProperSubtypes) {
                    // iterate over all edges of all subtypes
                    final Iterator<Label> subtypeIter =
                        NodeTypeSearchItem.this.subtypes.iterator();
                    this.imageIter =
                        new NestedIterator<Edge>(
                            new Iterator<Iterator<? extends Edge>>() {
                                @Override
                                public boolean hasNext() {
                                    return subtypeIter.hasNext();
                                }

                                @Override
                                public Iterator<? extends Edge> next() {
                                    return NodeTypeMultipleRecord.this.host.labelEdgeSet(
                                        subtypeIter.next()).iterator();
                                }

                                @Override
                                public void remove() {
                                    throw new UnsupportedOperationException();
                                }
                            });
                } else {
                    this.imageIter =
                        this.host.labelEdgeSet(NodeTypeSearchItem.this.label).iterator();
                }
                this.checkLabel = false;
                this.checkSource = true;
            }
            this.setEdge = true;
        }

        @Override
        public String toString() {
            return NodeTypeSearchItem.this.toString() + " <= " + this.selected;
        }

        /** The index of the edge in the search. */
        final private int edgeIx;
        /** The index of the source in the search. */
        final int sourceIx;
        /** Indicates if the source is found before this item is invoked. */
        final private boolean sourceFound;

        private final Node sourcePreMatch;

        /**
         * The pre-matched image for the edge source, if any. A value of
         * <code>null</code> means that no image is currently selected for the
         * source, or the source was pre-matched.
         */
        Node sourceFind;
        /**
         * Flag indicating the if sources of images returned by
         * {@link #initImages()} have to be checked against the found source
         * image.
         */
        private boolean checkSource;
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
        private Edge selected;
    }
}
