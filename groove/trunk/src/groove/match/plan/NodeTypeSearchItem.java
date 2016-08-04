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

import groove.grammar.host.HostGraph;
import groove.grammar.host.HostNode;
import groove.grammar.rule.LabelVar;
import groove.grammar.rule.RuleEdge;
import groove.grammar.rule.RuleNode;
import groove.grammar.type.TypeElement;
import groove.grammar.type.TypeNode;
import groove.match.plan.PlanSearchStrategy.Search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
     */
    public NodeTypeSearchItem(RuleNode node) {
        this(node, node.getType());
    }

    /**
     * Creates a search item that tests for the type of a given node.
     * @param node the node to be matched
     * @param type the node type that the image should be tested for
     */
    public NodeTypeSearchItem(RuleNode node, TypeNode type) {
        this.node = node;
        this.type = type;
        this.boundVars = new ArrayList<>(node.getVars());
        this.boundNodes = Collections.singleton(node);
        this.matchingTypes = node.getMatchingTypes();
    }

    /**
     * Returns the node for which this item tests.
     */
    @Override
    public Collection<? extends RuleNode> bindsNodes() {
        return this.boundNodes;
    }

    /**
     * Returns the variables on this node.
     */
    @Override
    public Collection<LabelVar> bindsVars() {
        return this.boundVars;
    }

    /** Returns the empty set. */
    @Override
    public Collection<? extends RuleEdge> bindsEdges() {
        return Collections.emptySet();
    }

    /**
     * Returns the node for which this item tests.
     */
    public RuleNode getNode() {
        return this.node;
    }

    @Override
    public String toString() {
        return String.format("Find node %s:%s", this.node, this.matchingTypes);
    }

    /**
     * This implementation first attempts to compare node type labels, if
     * the other search item is also an {@link NodeTypeSearchItem}; otherwise,
     * it delegates to super.
     */
    @Override
    public int compareTo(SearchItem item) {
        int result = super.compareTo(item);
        if (result != 0) {
            return result;
        }
        NodeTypeSearchItem other = (NodeTypeSearchItem) item;
        result = this.type.compareTo(other.type);
        if (result != 0) {
            return result;
        }
        // compare node numbers to make sure the ordering is deterministic
        return this.node.getNumber() - other.node.getNumber();
    }

    @Override
    int computeHashCode() {
        return super.computeHashCode() * 31 * getNode().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        return getNode().equals(((NodeTypeSearchItem) obj).getNode());
    }

    @Override
    public void activate(PlanSearchStrategy strategy) {
        this.nodeFound = strategy.isNodeFound(this.node);
        this.nodeIx = strategy.getNodeIx(this.node);
        this.varIxs = new int[this.boundVars.size()];
        this.varFound = new boolean[this.boundVars.size()];
        for (int i = 0; i < this.varIxs.length; i++) {
            LabelVar var = this.boundVars.get(i);
            this.varFound[i] = strategy.isVarFound(var);
            this.varIxs[i] = strategy.getVarIx(var);
        }
    }

    /**
     * This method returns the hash code of the node type as rating.
     */
    @Override
    int getRating() {
        return this.type.hashCode();
    }

    @Override
    final public Record createRecord(groove.match.plan.PlanSearchStrategy.Search search) {
        if (this.nodeFound) {
            return createSingularRecord(search);
        } else {
            return createMultipleRecord(search);
        }
    }

    /** Creates a record for the case the image is singular. */
    SingularRecord createSingularRecord(Search search) {
        return new NodeTypeSingularRecord(search, this.nodeIx);
    }

    /** Creates a record for the case the image is not singular. */
    MultipleRecord<HostNode> createMultipleRecord(Search search) {
        return new NodeTypeMultipleRecord(search, this.nodeIx);
    }

    /**
     * The node to be matched.
     */
    final RuleNode node;
    /** The type label to be matched. */
    final TypeNode type;
    /** Type variables in the rule node. */
    private final List<LabelVar> boundVars;
    /** The set of end nodes of this edge. */
    private final Set<RuleNode> boundNodes;

    /** The index of the source in the search. */
    private int nodeIx;
    /** Flags indicating whether images for the label variables have been found. */
    boolean[] varFound;
    /** The indices of the label variables in the search. */
    int[] varIxs;
    /** Indicates if the node is found before this item is invoked. */
    boolean nodeFound;
    /** The collection of subtypes of this node type. */
    final Set<TypeNode> matchingTypes;

    /**
     * Search record to be used if the node image is already found.
     * @author Arend Rensink
     * @version $Revision $
     */
    private class NodeTypeSingularRecord extends SingularRecord {
        /** Constructs an instance for a given search. */
        public NodeTypeSingularRecord(Search search, int nodeIx) {
            super(search);
            this.nodeIx = nodeIx;
        }

        @Override
        public void initialise(HostGraph host) {
            super.initialise(host);
            this.nodeSeed = this.search.getNodeSeed(this.nodeIx);
        }

        @Override
        boolean find() {
            boolean result = false;
            this.imageType = computeImage().getType();
            result = NodeTypeSearchItem.this.matchingTypes.contains(this.imageType);
            for (int vi = 0; result && vi < NodeTypeSearchItem.this.varFound.length; vi++) {
                int varIx = NodeTypeSearchItem.this.varIxs[vi];
                if (NodeTypeSearchItem.this.varFound[vi]) {
                    result = this.search.getVar(varIx) == this.imageType;
                }
            }
            if (result) {
                result = write();
            }
            return result;
        }

        @Override
        void erase() {
            for (int vi = 0; vi < NodeTypeSearchItem.this.varFound.length; vi++) {
                if (!NodeTypeSearchItem.this.varFound[vi]) {
                    this.search.putVar(NodeTypeSearchItem.this.varIxs[vi], null);
                }
            }
        }

        @Override
        final boolean write() {
            boolean result = true;
            for (int vi = 0; result && vi < NodeTypeSearchItem.this.varFound.length; vi++) {
                if (!NodeTypeSearchItem.this.varFound[vi]) {
                    result = this.search.putVar(NodeTypeSearchItem.this.varIxs[vi], this.imageType);
                }
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
            return this.nodeSeed == null ? this.search.getNode(this.nodeIx) : this.nodeSeed;
        }

        @Override
        public String toString() {
            return NodeTypeSearchItem.this.toString() + " <= " + computeImage();
        }

        /** The pre-matched (fixed) source image, if any. */
        private HostNode nodeSeed;
        /** The index of the source in the search. */
        private final int nodeIx;
        /** The type of the currently selected image. */
        private TypeNode imageType;
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
        NodeTypeMultipleRecord(Search search, int sourceIx) {
            super(search);
            this.sourceIx = sourceIx;
            this.varFind = new TypeElement[NodeTypeSearchItem.this.varIxs.length];
        }

        @Override
        void init() {
            for (int vi = 0; vi < this.varFind.length; vi++) {
                if (NodeTypeSearchItem.this.varFound[vi]) {
                    int varIx = NodeTypeSearchItem.this.varIxs[vi];
                    this.varFind[vi] = this.search.getVar(varIx);
                }
            }
            initImages();
        }

        @Override
        boolean write(HostNode image) {
            if (!NodeTypeSearchItem.this.matchingTypes.contains(image.getType())) {
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
            if (result) {
                result = this.search.putNode(this.sourceIx, image);
            }
            if (result) {
                this.selected = image;
            } else {
                // roll back the variable assignment
                for (vi--; vi >= 0; vi--) {
                    if (this.varFind[vi] == null) {
                        this.search.putVar(NodeTypeSearchItem.this.varIxs[vi], null);
                    }
                }
            }
            return result;
        }

        @Override
        void erase() {
            this.search.putNode(this.sourceIx, null);
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
            this.imageIter = this.host.nodeSet().iterator();
        }

        @Override
        public String toString() {
            return NodeTypeSearchItem.this.toString() + " <= " + this.selected;
        }

        /** The index of the source in the search. */
        final private int sourceIx;
        /** Images for the type variables found during {@link #init()}. */
        private final TypeElement[] varFind;
        /** Image found by the latest call to {@link #next()}, if any. */
        private HostNode selected;
    }
}
