/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package groove.abstraction.pattern.match;

import groove.abstraction.MyHashSet;
import groove.abstraction.pattern.match.Matcher.Search;
import groove.abstraction.pattern.shape.PatternEdge;
import groove.abstraction.pattern.shape.PatternNode;
import groove.abstraction.pattern.shape.TypeEdge;
import groove.abstraction.pattern.shape.TypeNode;
import groove.abstraction.pattern.trans.RuleEdge;
import groove.abstraction.pattern.trans.RuleNode;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * A search item that searches an image for a pattern edge.
 * This is a light version of Edge2SearchItem.
 * 
 * @author Eduardo Zambon
 */
public final class PatternEdgeSearchItem extends SearchItem {

    /** The edge for which this search item is to find an image. */
    private final RuleEdge edge;
    /** The label of {@link #edge}, separately stored for efficiency. */
    private final TypeEdge type;
    /** The source end of {@link #edge}, separately stored for efficiency. */
    private final RuleNode source;
    /** The type of {@link #source}. */
    private final TypeNode sourceType;
    /** The target end of {@link #edge}, separately stored for efficiency. */
    private final RuleNode target;
    /** The type of {@link #target}. */
    private final TypeNode targetType;
    /** The set of end nodes of this edge. */
    private final Set<RuleNode> boundNodes;
    /** The index of the edge in the search. */
    private int edgeIx;
    /** The index of the source in the search. */
    private int sourceIx;
    /** The index of the target in the search. */
    private int targetIx;

    /**
     * Creates a search item for a given binary edge.
     * @param edge the edge to be matched
     */
    public PatternEdgeSearchItem(RuleEdge edge) {
        this.edge = edge;
        this.type = edge.getType();
        this.source = edge.source();
        this.sourceType = this.source.getType();
        this.target = edge.target();
        this.targetType = this.target.getType();
        this.boundNodes = new MyHashSet<RuleNode>();
        this.boundNodes.add(this.source);
        this.boundNodes.add(this.target);
    }

    /**
     * Returns the nodes for which this item tests.
     */
    @Override
    public Collection<RuleNode> bindsNodes() {
        return this.boundNodes;
    }

    /** Returns the singleton set consisting of the matched edge. */
    @Override
    public Collection<RuleEdge> bindsEdges() {
        return Collections.singleton(this.edge);
    }

    @Override
    public String toString() {
        return String.format("Find %s", getEdge());
    }

    /**
     * This implementation first attempts to compare edge labels and ends, if
     * the other search item is also an {@link PatternEdgeSearchItem};
     * otherwise, it delegates to super.
     */
    @Override
    public int compareTo(SearchItem other) {
        int result = 0;
        if (other instanceof PatternEdgeSearchItem) {
            // compare first the edge labels, then the edge ends
            RuleEdge otherEdge = ((PatternEdgeSearchItem) other).getEdge();
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

    @Override
    Record createRecord(Search search) {
        return new PatternEdgeSearchRecord(search, this.edgeIx, this.sourceIx,
            this.targetIx);
    }

    /** This method returns the hash code of the edge type as rating. */
    @Override
    int getRating() {
        return this.type.hashCode();
    }

    @Override
    void activate(Matcher matcher) {
        this.edgeIx = matcher.getEdgeIx(this.edge);
        this.sourceIx = matcher.getNodeIx(this.source);
        this.targetIx = matcher.getNodeIx(this.target);
    }

    /**
     * Returns the edge for which this item tests.
     */
    public RuleEdge getEdge() {
        return this.edge;
    }

    /** Tests if a given pattern edge type matches the search item. */
    boolean checkEdgeType(PatternEdge image) {
        return this.type == image.getType();
    }

    /** Tests if a given pattern edge source type matches the search item. */
    boolean checkSourceType(PatternNode imageSource) {
        return this.sourceType == imageSource.getType();
    }

    /** Tests if a given pattern edge target type matches the search item. */
    boolean checkTargetType(PatternNode imageTarget) {
        return this.targetType == imageTarget.getType();
    }

    /**
     * Record of a pattern edge search item, storing an iterator over the
     * candidate images.
     * @author Arend Rensink and Eduardo Zambon
     */
    private class PatternEdgeSearchRecord extends AbstractRecord<PatternEdge> {

        /** The index of the edge in the search. */
        final int edgeIx;
        /** The index of the source in the search. */
        final int sourceIx;
        /** The index of the target in the search. */
        final int targetIx;
        /**
         * The pre-matched image for the edge source, if any. A value of
         * <code>null</code> means that no image is currently selected for the
         * source.
         */
        PatternNode sourceFind;
        /**
         * The pre-matched image for the edge target, if any. A value of
         * <code>null</code> means that no image is currently selected for the
         * target, or the target was pre-matched.
         */
        PatternNode targetFind;
        /** Image found by the latest call to {@link #next()}, if any. */
        PatternEdge selected;

        /**
         * Creates a record based on a given search.
         */
        PatternEdgeSearchRecord(Search search, int edgeIx, int sourceIx,
                int targetIx) {
            super(search);
            this.edgeIx = edgeIx;
            this.sourceIx = sourceIx;
            this.targetIx = targetIx;
            assert search.getEdge(edgeIx) == null : String.format(
                "Edge %s already in %s", PatternEdgeSearchItem.this.edge,
                search);
        }

        @Override
        void init() {
            Set<? extends PatternEdge> result = null;
            Set<? extends PatternEdge> labelEdgeSet =
                this.host.labelEdgeSet(PatternEdgeSearchItem.this.type.label());
            if (this.sourceFind != null) {
                Set<? extends PatternEdge> nodeEdgeSet =
                    this.host.edgeSet(this.sourceFind);
                if (nodeEdgeSet.size() < labelEdgeSet.size()) {
                    result = nodeEdgeSet;
                }
            } else if (this.targetFind != null) {
                Set<? extends PatternEdge> nodeEdgeSet =
                    this.host.edgeSet(this.targetFind);
                if (nodeEdgeSet == null) {
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
        boolean write(PatternEdge image) {
            if (!checkEdgeType(image)) {
                return false;
            }
            PatternNode imageSource = image.source();
            if (this.sourceFind == null) {
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
            PatternNode imageTarget = image.target();
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
            if (!this.search.putEdge(this.edgeIx, image)) {
                return false;
            }
            this.selected = image;
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

        /** Rolls back the image set for the target. */
        private void eraseTargetImage() {
            if (this.targetFind == null) {
                this.search.putNode(this.targetIx, null);
            }
        }

        @Override
        public String toString() {
            return PatternEdgeSearchItem.this.toString() + " = "
                + this.selected;
        }

    }

}
