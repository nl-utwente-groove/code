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

import groove.graph.TypeElement;
import groove.graph.TypeGraph;
import groove.graph.TypeGuard;
import groove.graph.TypeNode;
import groove.match.plan.PlanSearchStrategy.Search;
import groove.rel.LabelVar;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.trans.RuleEdge;
import groove.trans.RuleNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
        this.type = node.getType();
        this.typeVars = new ArrayList<LabelVar>();
        for (TypeGuard guard : node.getTypeGuards()) {
            if (guard.hasVar()) {
                this.typeVars.add(guard.getVar());
            }
        }
        this.boundNodes = new HashSet<RuleNode>(Arrays.asList(node));
        this.subtypes = typeGraph.getSubtypes(this.type);
        this.sharpType =
            node.isSharp() || this.subtypes == null
                || this.subtypes.size() == 1;
    }

    /**
     * Returns the node for which this item tests.
     */
    @Override
    public Collection<RuleNode> bindsNodes() {
        return this.boundNodes;
    }

    /** Returns the empty set. */
    @Override
    public Collection<RuleEdge> bindsEdges() {
        return Collections.emptySet();
    }

    /**
     * Returns the node for which this item tests.
     */
    public RuleNode getNode() {
        return this.source;
    }

    @Override
    public String toString() {
        return String.format("Find node %s:%s%s", this.source, this.type,
            this.sharpType ? " (sharp)" : "");
    }

    /**
     * This implementation first attempts to compare node type labels, if
     * the other search item is also an {@link NodeTypeSearchItem}; otherwise,
     * it delegates to super.
     */
    @Override
    public int compareTo(SearchItem other) {
        int result = 0;
        if (other instanceof NodeTypeSearchItem) {
            result = this.type.compareTo(((NodeTypeSearchItem) other).type);
        }
        if (result == 0) {
            return super.compareTo(other);
        } else {
            return result;
        }
    }

    public void activate(PlanSearchStrategy strategy) {
        this.sourceFound = strategy.isNodeFound(this.source);
        this.sourceIx = strategy.getNodeIx(this.source);
        this.varIxs = new int[this.typeVars.size()];
        for (int i = 0; i < this.varIxs.length; i++) {
            this.varIxs[i] = strategy.getVarIx(this.typeVars.get(i));
        }
    }

    /**
     * This method returns the hash code of the node type as rating.
     */
    @Override
    int getRating() {
        return this.type.hashCode();
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
    final TypeNode type;
    /** Type variables in the rule node. */
    final List<LabelVar> typeVars;
    /** The set of end nodes of this edge. */
    private final Set<RuleNode> boundNodes;

    /** The index of the source in the search. */
    int sourceIx;
    /** The indices of the label variables in the search. */
    int[] varIxs;
    /** Indicates if the source is found before this item is invoked. */
    boolean sourceFound;
    /** The collection of subtypes of this node type. */
    final Collection<TypeNode> subtypes;
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
            this.varFound = new boolean[NodeTypeSearchItem.this.varIxs.length];
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
            TypeNode sourceType = this.image.getType();
            result =
                NodeTypeSearchItem.this.source.getMatchingTypes().contains(
                    sourceType);
            //            if (NodeTypeSearchItem.this.sharpType) {
            //                result = NodeTypeSearchItem.this.type == sourceType;
            //            } else {
            //                result = NodeTypeSearchItem.this.subtypes.contains(sourceType);
            //            }
            for (int vi = 0; result && vi < this.varFound.length; vi++) {
                int varIx = NodeTypeSearchItem.this.varIxs[vi];
                TypeElement varFind = this.search.getVar(varIx);
                boolean varFound = this.varFound[vi] = varFind != null;
                if (varFound) {
                    result = varFind == sourceType;
                }
            }
            if (result) {
                result = write();
            }
            return result;
        }

        @Override
        void erase() {
            this.search.putNode(this.sourceIx, null);
            for (int vi = 0; vi < this.varFound.length; vi++) {
                if (!this.varFound[vi]) {
                    this.search.putVar(NodeTypeSearchItem.this.varIxs[vi], null);
                }
            }
        }

        @Override
        final boolean write() {
            boolean result = true;
            for (int vi = 0; result && vi < this.varFound.length; vi++) {
                if (!this.varFound[vi]) {
                    result =
                        this.search.putVar(NodeTypeSearchItem.this.varIxs[vi],
                            this.image.getType());
                }
            }
            if (result) {
                result = this.search.putNode(this.sourceIx, this.image);
            }
            if (!result) {
                erase();
            }
            return result;
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
        /** Previously found images for the type variables. */
        private final boolean[] varFound;
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
            this.varFind =
                new TypeElement[NodeTypeSearchItem.this.varIxs.length];
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
            for (int vi = 0; vi < this.varFind.length; vi++) {
                int varIx = NodeTypeSearchItem.this.varIxs[vi];
                this.varFind[vi] = this.search.getVar(varIx);
            }
            initImages();
        }

        @Override
        boolean write(HostNode image) {
            if (!NodeTypeSearchItem.this.source.getMatchingTypes().contains(
                image.getType())) {
                return false;
            }
            boolean result = true;
            int vi;
            for (vi = 0; result && vi < this.varFind.length; vi++) {
                int varIx = NodeTypeSearchItem.this.varIxs[vi];
                TypeElement varFind = this.varFind[vi];
                if (varFind != null) {
                    result = varFind == image.getType();
                } else {
                    result = this.search.putVar(varIx, image.getType());
                }
            }
            if (result && this.sourceFind == null) {
                result = this.search.putNode(this.sourceIx, image);
            }
            if (result) {
                this.selected = image;
            } else {
                // roll back the variable assignment
                for (vi--; vi >= 0; vi--) {
                    if (this.varFind[vi] == null) {
                        this.search.putVar(NodeTypeSearchItem.this.varIxs[vi],
                            null);
                    }
                }
            }
            return result;
        }

        @Override
        void erase() {
            if (this.sourceFind == null) {
                this.search.putNode(this.sourceIx, null);
            }
            for (int vi = 0; vi < this.varFind.length; vi++) {
                if (this.varFind[vi] == null) {
                    this.search.putVar(NodeTypeSearchItem.this.varIxs[vi], null);
                }
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
        /** Images for the type variables found during {@link #init()}. */
        private final TypeElement[] varFind;

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
