/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
package nl.utwente.groove.match.plan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import nl.utwente.groove.grammar.host.HostEdge;
import nl.utwente.groove.grammar.host.HostGraph;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.grammar.rule.RuleEdge;
import nl.utwente.groove.grammar.rule.RuleNode;
import nl.utwente.groove.grammar.type.TypeEdge;
import nl.utwente.groove.grammar.type.TypeLabel;
import nl.utwente.groove.graph.EdgeComparator;
import nl.utwente.groove.match.plan.PlanSearchStrategy.Search;

/**
 * A search item that searches an image for an edge labelled with a composite
 * edge-image expression: a choice or inversion (arbitrarily nested) of atoms
 * and unnamed wildcards, such as {@code a|b}, {@code -a} or {@code ?[b]|a}.
 * In contrast to {@link RegExprEdgeSearchItem}, the item binds a genuine host
 * edge image (any witness of such an expression is a single host edge), so
 * that the injective matching of eraser edges (the DPO identification
 * condition) extends to these expressions. A candidate host edge may be
 * matched in one or both directions: an inverse alternative binds the rule
 * source to the host edge target and vice versa, and both orientations of the
 * same host edge count as distinct solutions. This item is only used for
 * non-simple (multigraph) matching; in simple-graph mode, such expressions
 * retain the automaton-based semantics, where only the end nodes are bound.
 * @author Arend Rensink
 * @version $Revision$
 */
class ChoiceEdgeSearchItem extends AbstractSearchItem {
    /**
     * Creates a search item for a given edge, whose label is a composite
     * edge-image expression (see
     * {@link nl.utwente.groove.grammar.rule.RuleLabel#getImageAlts()}).
     * @param edge the edge to be matched
     */
    public ChoiceEdgeSearchItem(RuleEdge edge) {
        assert edge.label().getImageAlts() != null;
        this.edge = edge;
        this.source = edge.source();
        this.target = edge.target();
        this.selfEdge = this.source == this.target;
        this.boundNodes = new HashSet<>();
        this.boundNodes.add(this.source);
        this.boundNodes.add(this.target);
        this.forwardTypes = edge.getMatchingTypes(false);
        this.inverseTypes = edge.getMatchingTypes(true);
        this.forwardLabels = new LinkedHashSet<>();
        this.forwardTypes.forEach(t -> this.forwardLabels.add(t.label()));
        this.inverseLabels = new LinkedHashSet<>();
        this.inverseTypes.forEach(t -> this.inverseLabels.add(t.label()));
    }

    /** Returns the end nodes of the edge. */
    @Override
    public Collection<? extends RuleNode> bindsNodes() {
        return this.boundNodes;
    }

    /** Returns the singleton set consisting of the matched edge. */
    @Override
    public Collection<? extends RuleEdge> bindsEdges() {
        return Collections.singleton(this.edge);
    }

    /** Returns the edge for which this item tests. */
    public RuleEdge getEdge() {
        return this.edge;
    }

    @Override
    public String toString() {
        return String.format("Find %s", getEdge());
    }

    @Override
    public int compareTo(SearchItem other) {
        int result = super.compareTo(other);
        if (result != 0) {
            return result;
        }
        return EdgeComparator.instance().compare(this.edge, ((ChoiceEdgeSearchItem) other).edge);
    }

    @Override
    public void activate(PlanSearchStrategy strategy) {
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

    /** This method returns the hash code of the edge label as rating. */
    @Override
    int getRating() {
        return this.edge.label().hashCode();
    }

    @Override
    int computeHashCode() {
        return super.computeHashCode() * 31 + getEdge().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        return getEdge().equals(((ChoiceEdgeSearchItem) obj).getEdge());
    }

    @Override
    public Record createRecord(Search search) {
        if (search.getEdgeSeed(this.edgeIx) != null) {
            // the edge is pre-matched
            return createDummyRecord();
        } else {
            return new ChoiceEdgeRecord(search);
        }
    }

    /** Tests if a given host node fits the source end of the edge. */
    boolean checkSourceType(HostNode imageSource) {
        return this.source.getType().subsumes(imageSource.getType(), this.source.isSharp());
    }

    /** Tests if a given host node fits the target end of the edge. */
    boolean checkTargetType(HostNode imageTarget) {
        return this.target.getType().subsumes(imageTarget.getType(), this.target.isSharp());
    }

    /** The edge for which this search item is to find an image. */
    private final RuleEdge edge;
    /** The source end of {@link #edge}, separately stored for efficiency. */
    final RuleNode source;
    /** The target end of {@link #edge}, separately stored for efficiency. */
    final RuleNode target;
    /** Flag indicating that {@link #edge} is a self-edge. */
    final boolean selfEdge;
    /** The set of end nodes of this edge. */
    private final Set<RuleNode> boundNodes;
    /** The possible edge types of a forwards-matched image. */
    final Set<TypeEdge> forwardTypes;
    /** The possible edge types of an inversely-matched image. */
    final Set<TypeEdge> inverseTypes;
    /** The labels of {@link #forwardTypes}, in deterministic order. */
    final Set<TypeLabel> forwardLabels;
    /** The labels of {@link #inverseTypes}, in deterministic order. */
    final Set<TypeLabel> inverseLabels;

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

    /** A candidate image: a host edge matched in a given orientation. */
    private static record Image(HostEdge edge, boolean inverse) {
        /** Returns the image of the rule edge source under this candidate. */
        HostNode sourceImage() {
            return inverse()
                ? edge().target()
                : edge().source();
        }

        /** Returns the image of the rule edge target under this candidate. */
        HostNode targetImage() {
            return inverse()
                ? edge().source()
                : edge().target();
        }
    }

    /** Record iterating over the oriented candidate images. */
    private class ChoiceEdgeRecord extends MultipleRecord<Image> {
        /** Constructs a new record, for a given search. */
        ChoiceEdgeRecord(Search search) {
            super(search);
            assert search.getEdge(ChoiceEdgeSearchItem.this.edgeIx) == null : String
                .format("Edge %s already in %s", ChoiceEdgeSearchItem.this.edge, search);
        }

        @Override
        public void initialise(HostGraph host) {
            super.initialise(host);
            this.sourceSeed = this.search.getNodeSeed(ChoiceEdgeSearchItem.this.sourceIx);
            this.targetSeed = this.search.getNodeSeed(ChoiceEdgeSearchItem.this.targetIx);
        }

        @Override
        void init() {
            this.sourceFind = this.sourceSeed;
            if (this.sourceFind == null && ChoiceEdgeSearchItem.this.sourceFound) {
                this.sourceFind = this.search.getNode(ChoiceEdgeSearchItem.this.sourceIx);
                assert this.sourceFind != null : String
                    .format("Source node of %s not found", ChoiceEdgeSearchItem.this.edge);
            }
            this.targetFind = this.targetSeed;
            if (this.targetFind == null && ChoiceEdgeSearchItem.this.targetFound) {
                this.targetFind = this.search.getNode(ChoiceEdgeSearchItem.this.targetIx);
                assert this.targetFind != null : String
                    .format("Target node of %s not found", ChoiceEdgeSearchItem.this.edge);
            }
            List<Image> images = new ArrayList<>();
            if (this.sourceFind != null) {
                // a forward image starts at the source image,
                // an inverse image ends there
                this.host.outEdgeSet(this.sourceFind).forEach(e -> images.add(new Image(e, false)));
                this.host.inEdgeSet(this.sourceFind).forEach(e -> images.add(new Image(e, true)));
            } else if (this.targetFind != null) {
                this.host.inEdgeSet(this.targetFind).forEach(e -> images.add(new Image(e, false)));
                this.host.outEdgeSet(this.targetFind).forEach(e -> images.add(new Image(e, true)));
            } else {
                // iterate the per-label edge sets, per orientation
                for (TypeLabel label : ChoiceEdgeSearchItem.this.forwardLabels) {
                    this.host.edgeSet(label).forEach(e -> images.add(new Image(e, false)));
                }
                for (TypeLabel label : ChoiceEdgeSearchItem.this.inverseLabels) {
                    this.host.edgeSet(label).forEach(e -> images.add(new Image(e, true)));
                }
            }
            this.imageIter = images.iterator();
        }

        @Override
        boolean write(Image image) {
            Set<TypeEdge> types = image.inverse()
                ? ChoiceEdgeSearchItem.this.inverseTypes
                : ChoiceEdgeSearchItem.this.forwardTypes;
            if (!types.contains(image.edge().getType())) {
                return false;
            }
            if (!writeSourceImage(image)) {
                return false;
            }
            if (!writeTargetImage(image)) {
                eraseSourceImage();
                return false;
            }
            if (!this.search.putEdge(ChoiceEdgeSearchItem.this.edgeIx, image.edge())) {
                eraseSourceImage();
                eraseTargetImage();
                return false;
            }
            this.selected = image;
            return true;
        }

        /** Tries to write the source image of the given candidate. */
        private boolean writeSourceImage(Image image) {
            HostNode imageSource = image.sourceImage();
            if (this.sourceFind == null) {
                // maybe the prospective source image was used as
                // target image of this same edge in the previous attempt
                eraseTargetImage();
                if (!checkSourceType(imageSource)) {
                    return false;
                }
                if (!this.search.putNode(ChoiceEdgeSearchItem.this.sourceIx, imageSource)) {
                    return false;
                }
            } else if (imageSource != this.sourceFind) {
                return false;
            }
            return true;
        }

        /** Tries to write the target image of the given candidate. */
        private boolean writeTargetImage(Image image) {
            HostNode imageTarget = image.targetImage();
            if (ChoiceEdgeSearchItem.this.selfEdge) {
                if (imageTarget != image.sourceImage()) {
                    return false;
                }
            } else {
                if (this.targetFind == null) {
                    if (!checkTargetType(imageTarget)) {
                        return false;
                    }
                    if (!this.search.putNode(ChoiceEdgeSearchItem.this.targetIx, imageTarget)) {
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
            this.search.putEdge(ChoiceEdgeSearchItem.this.edgeIx, null);
            eraseSourceImage();
            eraseTargetImage();
            this.selected = null;
        }

        /** Rolls back the image set for the source. */
        private void eraseSourceImage() {
            if (this.sourceFind == null) {
                this.search.putNode(ChoiceEdgeSearchItem.this.sourceIx, null);
            }
        }

        /** Rolls back the image set for the target. */
        private void eraseTargetImage() {
            if (this.targetFind == null && !ChoiceEdgeSearchItem.this.selfEdge) {
                this.search.putNode(ChoiceEdgeSearchItem.this.targetIx, null);
            }
        }

        @Override
        public String toString() {
            return ChoiceEdgeSearchItem.this.toString() + " = " + this.selected;
        }

        /** The pre-matched (fixed) source image, if any. */
        private HostNode sourceSeed;
        /** The pre-matched (fixed) target image, if any. */
        private HostNode targetSeed;
        /**
         * The found image for the edge source, if any. A value of
         * <code>null</code> means that no image is currently selected for the
         * source.
         */
        private HostNode sourceFind;
        /**
         * The found image for the edge target, if any. A value of
         * <code>null</code> means that no image is currently selected for the
         * target.
         */
        private HostNode targetFind;
        /** Image found by the latest successful call to {@code write}, if any. */
        private Image selected;
    }
}
