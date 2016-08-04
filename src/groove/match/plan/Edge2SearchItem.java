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
 * $Id$
 */
package groove.match.plan;

import groove.grammar.host.HostEdge;
import groove.grammar.host.HostGraph;
import groove.grammar.host.HostNode;
import groove.grammar.host.ValueNode;
import groove.grammar.rule.RuleEdge;
import groove.grammar.rule.RuleNode;
import groove.grammar.type.TypeEdge;
import groove.grammar.type.TypeNode;
import groove.graph.NodeComparator;
import groove.match.plan.PlanSearchStrategy.Search;

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
    public Edge2SearchItem(RuleEdge edge, boolean simple) {
        // as this is subclassed by VarEdgeSearchItem,
        // the label may actually be an arbitrary regular expression
        assert edge.label().isSharp() || edge.label().isAtom() || edge.label().isWildcard();
        assert edge.getType() != null || edge.label().isWildcard();
        this.edge = edge;
        this.simple = simple;
        this.type = edge.getType();
        this.source = edge.source();
        TypeNode sourceType = this.source.getType();
        this.sourceType =
            this.source.isSharp() || this.type == null || sourceType != this.type.source()
                ? sourceType : null;
        this.target = edge.target();
        TypeNode targetType = this.target.getType();
        this.targetType =
            this.target.isSharp() || this.type == null || targetType != this.type.target()
                ? targetType : null;
        this.selfEdge = this.source == this.target;
        this.boundNodes = new HashSet<>();
        this.boundNodes.add(this.source);
        this.boundNodes.add(this.target);
    }

    /**
     * Returns the end nodes of the edge.
     */
    @Override
    public Collection<? extends RuleNode> bindsNodes() {
        return this.boundNodes;
    }

    /** Returns the singleton set consisting of the matched edge. */
    @Override
    public Collection<? extends RuleEdge> bindsEdges() {
        return Collections.singleton(this.edge);
    }

    /**
     * Returns the edge for which this item tests.
     */
    public RuleEdge getEdge() {
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
        int result = super.compareTo(other);
        if (result != 0) {
            return result;
        }
        // compare first the edge labels, then the edge ends
        RuleEdge otherEdge = ((Edge2SearchItem) other).getEdge();
        result = getEdge().label().compareTo(otherEdge.label());
        if (result != 0) {
            return result;
        }
        result = nodeComparator.compare(getEdge().source(), otherEdge.source());
        if (result != 0) {
            return result;
        }
        result = nodeComparator.compare(getEdge().target(), otherEdge.target());
        return result;
    }

    @Override
    public void activate(PlanSearchStrategy strategy) {
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
     * This method returns the hash code of the edge label as rating.
     */
    @Override
    int getRating() {
        return this.edge.label().hashCode();
    }

    @Override
    int computeHashCode() {
        int result = super.computeHashCode();
        return result * 31 + getEdge().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        Edge2SearchItem other = (Edge2SearchItem) obj;
        return getEdge().equals(other.getEdge());
    }

    @Override
    final public Record createRecord(groove.match.plan.PlanSearchStrategy.Search search) {
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
        return search.getEdgeSeed(this.edgeIx) != null;
    }

    /** Indicates if the edge has a singular image in the search. */
    boolean isSingular(Search search) {
        return this.sourceFound && this.targetFound && this.simple;
    }

    /** Creates a record for the case the image is singular. */
    SingularRecord createSingularRecord(Search search) {
        return new Edge2SingularRecord(search, this.edgeIx, this.sourceIx, this.targetIx);
    }

    /** Creates a record for the case the image is not singular. */
    MultipleRecord<HostEdge> createMultipleRecord(Search search) {
        return new Edge2MultipleRecord(search, this.edgeIx, this.sourceIx, this.targetIx,
            this.sourceFound, this.targetFound);
    }

    /** Tests if a given host edge type matches the search item. */
    boolean checkEdgeType(HostEdge image) {
        return this.type == null || this.type.subsumes(image.getType());
    }

    /** Tests if a given host edge source type matches the search item. */
    boolean checkSourceType(HostNode imageSource) {
        return this.sourceType == null
            || this.sourceType.subsumes(imageSource.getType(), this.source.isSharp());
    }

    /** Tests if a given host edge target type matches the search item. */
    boolean checkTargetType(HostNode imageTarget) {
        return this.targetType == null
            || this.targetType.subsumes(imageTarget.getType(), this.target.isSharp());
    }

    /**
     * The edge for which this search item is to find an image.
     */
    final RuleEdge edge;
    /** Indicates if we are looking for a simple edge. */
    final boolean simple;
    /** The label of {@link #edge}, separately stored for efficiency. */
    final TypeEdge type;
    /**
     * The source end of {@link #edge}, separately stored for efficiency.
     */
    final RuleNode source;
    /**
     * The type of {@link #source}, if this has to be checked explicitly
     * (which is the case if it is a sharp type, or a proper subtype of
     * {@code type.source()}).
     */
    final TypeNode sourceType;
    /**
     * The target end of {@link #edge}, separately stored for efficiency.
     */
    final RuleNode target;
    /**
     * The type of {@link #target}, if this has to be checked explicitly
     * (which is the case if it is a sharp type, or a proper subtype of
     * {@code type.target()}).
     */
    final TypeNode targetType;
    /**
     * Flag indicating that {@link #edge} is a self-edge.
     */
    final boolean selfEdge;
    /** The set of end nodes of this edge. */
    private final Set<RuleNode> boundNodes;

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

    private static final NodeComparator nodeComparator = NodeComparator.instance();

    /**
     * Search record to be used if the edge image is completely determined by
     * the pre-matched ends.
     * @author Arend Rensink
     * @version $Revision $
     */
    class Edge2SingularRecord extends SingularRecord {
        /** Constructs an instance for a given search. */
        public Edge2SingularRecord(Search search, int edgeIx, int sourceIx, int targetIx) {
            super(search);
            this.edgeIx = edgeIx;
            this.sourceIx = sourceIx;
            this.targetIx = targetIx;
        }

        @Override
        public void initialise(HostGraph host) {
            super.initialise(host);
            this.sourceSeed = this.search.getNodeSeed(this.sourceIx);
            this.targetSeed = this.search.getNodeSeed(this.targetIx);
        }

        @Override
        boolean find() {
            HostEdge image = getEdgeImage();
            assert image != null;
            boolean result = isImageCorrect(image);
            if (result) {
                this.image = image;
                write();
            }
            return result;
        }

        @Override
        final boolean write() {
            return this.search.putEdge(this.edgeIx, this.image);
        }

        @Override
        void erase() {
            this.search.putEdge(this.edgeIx, null);
        }

        /** Tests if the (uniquely determined) edge image can be used. */
        boolean isImageCorrect(HostEdge image) {
            return this.host.containsEdge(image);
        }

        /**
         * Creates and returns the edge image, as constructed from the available
         * end node images.
         */
        private HostEdge getEdgeImage() {
            HostNode sourceFind = this.sourceSeed;
            if (sourceFind == null) {
                sourceFind = this.search.getNode(this.sourceIx);
            }
            assert sourceFind != null : String.format("Source node of %s has not been found",
                Edge2SearchItem.this.edge);
            HostNode targetFind = this.targetSeed;
            if (targetFind == null) {
                targetFind = this.search.getNode(this.targetIx);
            }
            assert targetFind != null : String.format("Target node of %s has not been found",
                Edge2SearchItem.this.edge);
            return this.host.getFactory().createEdge(sourceFind, getType(), targetFind);
        }

        /** Callback method to determine the label of the edge image. */
        TypeEdge getType() {
            return Edge2SearchItem.this.type;
        }

        @Override
        public String toString() {
            return Edge2SearchItem.this.toString() + " = " + getEdgeImage();
        }

        /** The pre-matched (fixed) source image, if any. */
        private HostNode sourceSeed;
        /** The pre-matched (fixed) target image, if any. */
        private HostNode targetSeed;
        /** The index of the edge in the search. */
        private final int edgeIx;
        /** The index of the source in the search. */
        private final int sourceIx;
        /** The index of the target in the search. */
        private final int targetIx;
        /** The previously found edge, if the state is {@link SearchItem.State#FOUND} or {@link SearchItem.State#FULL}. */
        private HostEdge image;
    }

    /**
     * Record of an edge search item, storing an iterator over the candidate
     * images.
     * @author Arend Rensink
     * @version $Revision $
     */
    class Edge2MultipleRecord extends MultipleRecord<HostEdge> {
        /**
         * Creates a record based on a given search.
         */
        Edge2MultipleRecord(Search search, int edgeIx, int sourceIx, int targetIx,
            boolean sourceFound, boolean targetFound) {
            super(search);
            this.edgeIx = edgeIx;
            this.sourceIx = sourceIx;
            this.targetIx = targetIx;
            this.sourceFound = sourceFound;
            this.targetFound = targetFound;
            assert search.getEdge(edgeIx) == null : String.format("Edge %s already in %s",
                Edge2SearchItem.this.edge,
                search);
        }

        @Override
        public void initialise(HostGraph host) {
            super.initialise(host);
            this.sourceSeed = this.search.getNodeSeed(this.sourceIx);
            this.targetSeed = this.search.getNodeSeed(this.targetIx);
        }

        @Override
        void init() {
            this.sourceFind = this.sourceSeed;
            if (this.sourceFind == null && this.sourceFound) {
                this.sourceFind = this.search.getNode(this.sourceIx);
                assert this.sourceFind != null : String.format("Source node of %s not found",
                    Edge2SearchItem.this.edge);
            }
            this.targetFind = this.targetSeed;
            if (this.targetFind == null && this.targetFound) {
                this.targetFind = this.search.getNode(this.targetIx);
                assert this.targetFind != null : String.format("Target node of %s not found",
                    Edge2SearchItem.this.edge);
            }
            initImages();
        }

        @Override
        boolean write(HostEdge image) {
            if (!checkEdgeType(image)) {
                return false;
            }
            if (!writeSourceImage(image)) {
                return false;
            }
            if (!writeTargetImage(image)) {
                eraseSourceImage();
                return false;
            }
            if (!this.search.putEdge(this.edgeIx, image)) {
                eraseSourceImage();
                eraseTargetImage();
                return false;
            }
            this.selected = image;
            return true;
        }

        /** Tries to write the source image of the given edge. */
        private boolean writeSourceImage(HostEdge image) {
            HostNode imageSource = image.source();
            if (this.sourceFind == null) {
                // maybe the prospective source image was used as
                // target image of this same edge in the previous attempt
                eraseTargetImage();
                if (!checkSourceType(imageSource)) {
                    return false;
                }
                if (!this.search.putNode(this.sourceIx, imageSource)) {
                    return false;
                }
            } else if (imageSource != this.sourceFind) {
                return false;
            }
            return true;
        }

        /** Tries to write the target image of the given edge. */
        private boolean writeTargetImage(HostEdge image) {
            HostNode imageTarget = image.target();
            if (Edge2SearchItem.this.selfEdge) {
                if (imageTarget != image.source()) {
                    return false;
                }
            } else {
                if (this.targetFind == null) {
                    if (!checkTargetType(imageTarget)) {
                        return false;
                    }
                    if (!this.search.putNode(this.targetIx, imageTarget)) {
                        return false;
                    }
                } else if (imageTarget != this.targetFind) {
                    return false;
                }
            }
            return true;
        }

        @Override
        void erase() {
            this.search.putEdge(this.edgeIx, null);
            eraseSourceImage();
            eraseTargetImage();
            this.selected = null;
        }

        /** Rolls back the image set for the source. */
        private void eraseSourceImage() {
            if (this.sourceFind == null) {
                this.search.putNode(this.sourceIx, null);
            }
        }

        /** Rolls back the image set for the source. */
        private void eraseTargetImage() {
            if (this.targetFind == null && !Edge2SearchItem.this.selfEdge) {
                this.search.putNode(this.targetIx, null);
            }
        }

        /**
         * Returns an iterator over those
         * images for {@link #edge} that are consistent with the pre-matched
         * edge ends.
         */
        void initImages() {
            Set<? extends HostEdge> result = null;
            // it does not pay off here to take only the incident edges of
            // pre-matched ends,
            // no doubt because building the necessary additional data
            // structures takes more
            // time than is saved by trying out fewer images
            Set<? extends HostEdge> labelEdgeSet =
                this.host.edgeSet(Edge2SearchItem.this.type.label());
            if (this.sourceFind != null) {
                Set<? extends HostEdge> nodeEdgeSet = this.host.edgeSet(this.sourceFind);
                if (nodeEdgeSet.size() < labelEdgeSet.size()) {
                    result = nodeEdgeSet;
                }
            } else if (this.targetFind != null) {
                Set<? extends HostEdge> nodeEdgeSet = this.host.edgeSet(this.targetFind);
                if (nodeEdgeSet == null) {
                    assert this.targetFind instanceof ValueNode : String.format("Host graph does not contain edges for node %s",
                        this.targetFind);
                    result = Collections.emptySet();
                } else if (nodeEdgeSet.size() < labelEdgeSet.size()) {
                    result = nodeEdgeSet;
                }
            }
            if (result == null) {
                result = labelEdgeSet;
            }
            this.imageIter = result.iterator();
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

        private HostNode sourceSeed;
        private HostNode targetSeed;
        /**
         * The pre-matched image for the edge source, if any. A value of
         * <code>null</code> means that no image is currently selected for the
         * source, or the source was pre-matched.
         */
        HostNode sourceFind;
        /**
         * The pre-matched image for the edge target, if any. A value of
         * <code>null</code> means that no image is currently selected for the
         * target, or the target was pre-matched.
         */
        HostNode targetFind;
        /** Image found by the latest call to {@link #next()}, if any. */
        HostEdge selected;
    }
}
