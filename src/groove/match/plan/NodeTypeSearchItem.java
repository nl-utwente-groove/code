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
package groove.match.plan;

import groove.graph.TypeGraph;
import groove.graph.TypeLabel;
import groove.match.plan.PlanSearchStrategy.Search;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.trans.RuleEdge;
import groove.trans.RuleNode;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A search item that searches an image for a typed node.
 * @author Arend Rensink
 * @version $Revision $
 */
class NodeTypeSearchItem extends AbstractSearchItem {
    /**
     * Creates a search item for a given typed node.
     * @param node the node to be matched
     * @param typeGraph label store containing the subtypes of the node type
     */
    public NodeTypeSearchItem(RuleNode node, TypeGraph typeGraph) {
        assert node.getType().getGraph() == typeGraph;
        this.source = node;
        this.label = node.getType().label();
        assert this.label.isNodeType() : String.format(
            "Label '%s' is not a node type", this.label);
        this.boundNodes = new HashSet<RuleNode>(Arrays.asList(node));
        Set<TypeLabel> labelStoreSubtypes = typeGraph.getSublabels(this.label);
        this.subtypes =
            labelStoreSubtypes == null ? null : new HashSet<TypeLabel>(
                labelStoreSubtypes);
        this.sharpType =
            node.isSharp() || this.subtypes == null
                || this.subtypes.size() == 1;
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
    public Collection<RuleEdge> bindsEdges() {
        return Collections.emptySet();
    }

    /**
     * Returns the edge for which this item tests.
     */
    public RuleNode getNode() {
        return this.source;
    }

    @Override
    public String toString() {
        return String.format("Find node %s %s", this.label, this.sharpType
                ? "(sharp)" : "");
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
            result = this.label.compareTo(((NodeTypeSearchItem) other).label);
        }
        if (result == 0) {
            return super.compareTo(other);
        } else {
            return result;
        }
    }

    public void activate(PlanSearchStrategy strategy) {
        // one would like the following assertion,
        // but since negative search items for the same edge also reserve the
        // index, the assertion may fail in case of a positive and negative test
        // on the same edge (stupid!)
        // assert !strategy.isEdgeFound(edge);
        this.sourceFound = strategy.isNodeFound(this.source);
        this.sourceIx = strategy.getNodeIx(this.source);
    }

    /**
     * This method returns the hash code of the label as rating.
     */
    @Override
    int getRating() {
        return this.label.hashCode();
    }

    final public Record createRecord(
            groove.match.plan.PlanSearchStrategy.Search search) {
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
        return search.getNodeSeed(this.sourceIx) != null;
    }

    /** Indicates if the source node has a singular image in the search. */
    boolean isSingular(Search search) {
        return this.sourceFound || isPreMatched(search);
    }

    /** Creates a record for the case the image is singular. */
    SingularRecord createSingularRecord(Search search) {
        return new NodeTypeSingularRecord(search, this.sourceIx);
    }

    /** Creates a record for the case the image is not singular. */
    MultipleRecord<HostNode> createMultipleRecord(Search search) {
        return new NodeTypeMultipleRecord(search, this.sourceIx,
            this.sourceFound);
    }

    /**
     * The node to be matched.
     */
    final RuleNode source;
    /** The type label to be matched. */
    final TypeLabel label;
    /** The set of end nodes of this edge. */
    private final Set<RuleNode> boundNodes;

    /** The index of the source in the search. */
    int sourceIx;
    /** Indicates if the source is found before this item is invoked. */
    boolean sourceFound;
    /** The collection of subtypes of this node type. */
    final Collection<TypeLabel> subtypes;
    /** Flag indicating if the node type has non-trivial subtypes. */
    final boolean sharpType;

    /**
     * Search record to be used if the node type image is completely determined
     * by the pre-matched end.
     * @author Arend Rensink
     * @version $Revision $
     */
    private class NodeTypeSingularRecord extends SingularRecord {
        /** Constructs an instance for a given search. */
        public NodeTypeSingularRecord(Search search, int sourceIx) {
            super(search);
            this.sourceIx = sourceIx;
        }

        @Override
        public void initialise(HostGraph host) {
            super.initialise(host);
            this.sourcePreMatch = this.search.getNodeSeed(this.sourceIx);
        }

        @Override
        boolean find() {
            boolean result = false;
            this.image = computeImage();
            TypeLabel sourceLabel = this.image.getType().label();
            if (NodeTypeSearchItem.this.sharpType) {
                result = NodeTypeSearchItem.this.label.equals(sourceLabel);
            } else {
                result = NodeTypeSearchItem.this.subtypes.contains(sourceLabel);
            }
            if (result) {
                write();
            }
            return result;
        }

        @Override
        void erase() {
            this.search.putNode(this.sourceIx, null);
        }

        @Override
        final boolean write() {
            return this.search.putNode(this.sourceIx, this.image);
        }

        /**
         * Creates and returns the edge image, as constructed from the available
         * end node images.
         */
        private HostNode computeImage() {
            return this.sourcePreMatch == null
                    ? this.search.getNode(this.sourceIx) : this.sourcePreMatch;
        }

        @Override
        public String toString() {
            return NodeTypeSearchItem.this.toString() + " <= " + computeImage();
        }

        /** The pre-matched (fixed) source image, if any. */
        private HostNode sourcePreMatch;
        /** The index of the source in the search. */
        private final int sourceIx;
        /** The previously found type, if the state is {@link SearchItem.State#FOUND} or {@link SearchItem.State#FULL}. */
        private HostNode image;
    }

    /**
     * Record of a node type search item, storing an iterator over the candidate
     * images.
     * @author Arend Rensink
     * @version $Revision $
     */
    private class NodeTypeMultipleRecord extends MultipleRecord<HostNode> {
        /**
         * Creates a record based on a given search.
         */
        NodeTypeMultipleRecord(Search search, int sourceIx, boolean sourceFound) {
            super(search);
            this.sourceIx = sourceIx;
            this.sourceFound = sourceFound;
        }

        @Override
        public void initialise(HostGraph host) {
            super.initialise(host);
            this.sourcePreMatch = this.search.getNodeSeed(this.sourceIx);
        }

        @Override
        void init() {
            this.sourceFind = this.sourcePreMatch;
            if (this.sourceFind == null && this.sourceFound) {
                this.sourceFind = this.search.getNode(this.sourceIx);
                assert this.sourceFind != null : String.format(
                    "Node %s not found", NodeTypeSearchItem.this.source);
            }
            initImages();
        }

        @Override
        boolean write(HostNode image) {
            if (NodeTypeSearchItem.this.sharpType
                && NodeTypeSearchItem.this.label != image.getType().label()
                || !NodeTypeSearchItem.this.subtypes.contains(image.getType().label())) {
                return false;
            }
            if (this.sourceFind == null) {
                if (!this.search.putNode(this.sourceIx, image)) {
                    return false;
                }
            }
            this.selected = image;
            return true;
        }

        @Override
        void erase() {
            if (this.sourceFind == null) {
                this.search.putNode(this.sourceIx, null);
            }
            this.selected = null;
        }

        /**
         * Callback method to set the iterator over potential images. Also sets
         * flags indicating whether potential images still have to be checked
         * for correctness of the source or label parts.
         */
        private void initImages() {
            if (this.sourceFind != null) {
                this.imageIter =
                    Collections.singleton(this.sourceFind).iterator();
            } else {
                this.imageIter = this.host.nodeSet().iterator();
            }
        }

        @Override
        public String toString() {
            return NodeTypeSearchItem.this.toString() + " <= " + this.selected;
        }

        /** The index of the source in the search. */
        final private int sourceIx;
        /** Indicates if the source is found before this item is invoked. */
        final private boolean sourceFound;

        private HostNode sourcePreMatch;

        /**
         * The pre-matched image for the edge source, if any. A value of
         * <code>null</code> means that no image is currently selected for the
         * source, or the source was pre-matched.
         */
        private HostNode sourceFind;
        /** Image found by the latest call to {@link #next()}, if any. */
        private HostNode selected;
    }
}
