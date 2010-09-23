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
 * $Id: VarEdgeSearchItem.java,v 1.18 2008-01-30 09:33:29 iovka Exp $
 */
package groove.match;

import groove.graph.Edge;
import groove.graph.Label;
import groove.match.SearchPlanStrategy.Search;
import groove.rel.LabelVar;
import groove.rel.RegExprLabel;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * A search item that searches an image for an edge.
 * @author Arend Rensink
 * @version $Revision $
 */
class VarEdgeSearchItem extends Edge2SearchItem {

    /**
     * Constructs a new search item. The item will match any edge between the
     * end images, and record the edge label as value of the wildcard variable.
     */
    public VarEdgeSearchItem(Edge edge) {
        super(edge);
        this.var = RegExprLabel.getWildcardId(edge.label());
        this.labelConstraint = RegExprLabel.getWildcardGuard(edge.label());
        this.boundVars = Collections.singleton(this.var);
        assert this.var != null : String.format(
            "Edge %s is not a variable edge", edge);
        assert edge.endCount() <= Edge.END_COUNT : "Search item undefined for hyperedge";
    }

    /**
     * This implementation returns a singleton set consisting of the variable
     * bound by this item.
     */
    @Override
    public Collection<LabelVar> bindsVars() {
        return this.boundVars;
    }

    @Override
    public void activate(SearchPlanStrategy strategy) {
        super.activate(strategy);
        this.varFound = strategy.isVarFound(this.var);
        this.varIx = strategy.getVarIx(this.var);
    }

    @Override
    boolean isSingular(Search search) {
        return super.isSingular(search)
            && (this.varFound || search.getVarAnchor(this.varIx) != null);
    }

    @Override
    SingularRecord createSingularRecord(Search search) {
        return new VarEdgeSingularRecord(search, this.edgeIx, this.sourceIx,
            this.targetIx, this.varIx);
    }

    @Override
    MultipleRecord<Edge> createMultipleRecord(Search search) {
        return new VarEdgeMultipleRecord(search, this.edgeIx, this.sourceIx,
            this.targetIx, this.varIx, this.sourceFound, this.targetFound,
            this.varFound);
    }

    boolean isLabelConstraintSatisfied(Label label) {
        return label.getKind() == this.var.getKind()
            && this.labelConstraint == null
            || this.labelConstraint.isSatisfied(label);
    }

    /** The variable bound in the wildcard (not <code>null</code>). */
    private final LabelVar var;
    /** Singleton set consisting of <code>var</code>. */
    private final Collection<LabelVar> boundVars;
    /** The index of {@link #var} in the result. */
    int varIx;
    /**
     * Flag indicating that {@link #var} is matched before this item is invoked.
     */
    boolean varFound;

    /** The constraint on the variable valuation, if any. */
    private final groove.util.Property<Label> labelConstraint;

    class VarEdgeSingularRecord extends Edge2SingularRecord {
        /**
         * Constructs a record from a given search, and (possibly
         * <code>null</code>) pre-matched end node and variable images.
         */
        VarEdgeSingularRecord(Search search, int edgeIx, int sourceIx,
                int targetIx, int varIx) {
            super(search, edgeIx, sourceIx, targetIx);
            this.varIx = varIx;
            this.varPreMatch = search.getVarAnchor(varIx);
        }

        /** This implementation returns the variable image from the match. */
        @Override
        Label getLabel() {
            Label result = this.varPreMatch;
            if (result == null) {
                result = this.search.getVar(this.varIx);
            }
            return result;
        }

        /** Tests the label constraint, in addition to calling the super method. */
        @Override
        boolean isImageCorrect(Edge image) {
            return isLabelConstraintSatisfied(VarEdgeSearchItem.this.edge.label())
                && super.isImageCorrect(image);
        }

        private final Label varPreMatch;
        /** The index of {@link #var} in the result. */
        private final int varIx;
    }

    /** Record for this type of search item. */
    class VarEdgeMultipleRecord extends Edge2MultipleRecord {
        /** Constructs a new record, for a given matcher. */
        VarEdgeMultipleRecord(Search search, int edgeIx, int sourceIx,
                int targetIx, int varIx, boolean sourceFound,
                boolean targetFound, boolean varFound) {
            super(search, edgeIx, sourceIx, targetIx, sourceFound, targetFound);
            this.varFound = varFound;
            this.varIx = varIx;
            this.varPreMatch = search.getVarAnchor(varIx);
        }

        @Override
        void init() {
            // first initialise varFind, otherwise the images will not be set
            // correctly
            this.varFind = this.varPreMatch;
            if (this.varFind == null && this.varFound) {
                this.varFind = this.search.getVar(this.varIx);
            }
            super.init();
        }

        @Override
        void initImages() {
            Set<? extends Edge> edgeSet;
            if (this.varFind != null) {
                if (isLabelConstraintSatisfied(this.varFind)) {
                    edgeSet =
                        this.host.labelEdgeSet(VarEdgeSearchItem.this.arity,
                            this.varFind);
                } else {
                    edgeSet = EMPTY_IMAGE_SET;
                }
            } else {
                // take the incident edges of the pre-matched source or target,
                // if any
                // otherwise, the set of all edges
                if (this.sourceFind != null) {
                    edgeSet = this.host.outEdgeSet(this.sourceFind);
                } else if (this.targetFind != null) {
                    edgeSet =
                        this.host.edgeSet(this.targetFind, Edge.TARGET_INDEX);
                } else {
                    edgeSet = this.host.edgeSet();
                }
            }
            initImages(edgeSet, true, true, false, true);
        }

        @Override
        boolean setImage(Edge image) {
            boolean result =
                isLabelConstraintSatisfied(image.label())
                    && super.setImage(image);
            if (result && this.varFind == null) {
                result = this.search.putVar(this.varIx, image.label());
            }
            return result;
        }

        @Override
        public void reset() {
            super.reset();
            if (this.varFind == null) {
                this.search.putVar(this.varIx, null);
            }
        }

        /**
         * The pre-matched variable image, if any.
         */
        private final Label varPreMatch;
        /** The index of {@link #var} in the result. */
        private final int varIx;
        /**
         * Flag indicating that {@link #var} is matched before this item is
         * invoked.
         */
        private final boolean varFound;
        /**
         * The found variable image, if any.
         */
        private Label varFind;
    }

    private static final Set<Edge> EMPTY_IMAGE_SET = Collections.emptySet();
}
