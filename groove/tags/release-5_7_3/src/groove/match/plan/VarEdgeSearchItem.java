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

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import groove.grammar.host.HostEdge;
import groove.grammar.host.HostEdgeSet;
import groove.grammar.host.HostGraph;
import groove.grammar.rule.LabelVar;
import groove.grammar.rule.RuleEdge;
import groove.grammar.type.TypeEdge;
import groove.grammar.type.TypeElement;
import groove.grammar.type.TypeGuard;
import groove.match.plan.PlanSearchStrategy.Search;

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
    public VarEdgeSearchItem(RuleEdge edge, boolean simple) {
        super(edge, simple);
        this.guard = edge.label().getWildcardGuard();
        this.var = this.guard.getVar();
        this.boundVars = Collections.singleton(this.var);
        this.boundEdges = Collections.singleton(edge);
        assert this.var != null : String.format("Edge %s is not a variable edge", edge);
    }

    @Override
    public Collection<? extends RuleEdge> bindsEdges() {
        return this.boundEdges;
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
    public void activate(PlanSearchStrategy strategy) {
        super.activate(strategy);
        if (this.var.hasName()) {
            this.varFound = strategy.isVarFound(this.var);
            this.varIx = strategy.getVarIx(this.var);
        } else {
            this.varIx = -1;
        }
    }

    @Override
    boolean isSingular(Search search) {
        return super.isSingular(search) && this.varIx >= 0
            && (this.varFound || search.getVarSeed(this.varIx) != null);
    }

    @Override
    SingularRecord createSingularRecord(Search search) {
        return new VarEdgeSingularRecord(search, this.edgeIx, this.sourceIx, this.targetIx,
            this.varIx);
    }

    @Override
    MultipleRecord<HostEdge> createMultipleRecord(Search search) {
        return new VarEdgeMultipleRecord(search, this.edgeIx, this.sourceIx, this.targetIx,
            this.varIx, this.sourceFound, this.targetFound, this.varFound);
    }

    boolean isGuardSatisfied(TypeElement type) {
        return this.guard.isSatisfied(type);
    }

    /** The variable bound in the wildcard (not <code>null</code>). */
    private final LabelVar var;
    /** Singleton set consisting of the matched edge. */
    private final Collection<RuleEdge> boundEdges;
    /** Singleton set consisting of <code>var</code>. */
    private final Collection<LabelVar> boundVars;
    /** The index of {@link #var} in the result. */
    int varIx;
    /**
     * Flag indicating that {@link #var} is matched before this item is invoked.
     */
    boolean varFound;

    /** The constraint on the variable valuation, if any. */
    private final TypeGuard guard;

    private class VarEdgeSingularRecord extends Edge2SingularRecord {
        /**
         * Constructs a record from a given search, and (possibly
         * <code>null</code>) pre-matched end node and variable images.
         */
        VarEdgeSingularRecord(Search search, int edgeIx, int sourceIx, int targetIx, int varIx) {
            super(search, edgeIx, sourceIx, targetIx);
            this.varIx = varIx;
        }

        @Override
        public void initialise(HostGraph host) {
            super.initialise(host);
            this.varSeed = (TypeEdge) this.search.getVarSeed(this.varIx);
        }

        /** This implementation returns the variable image from the match. */
        @Override
        TypeEdge getType() {
            TypeEdge result = this.varSeed;
            if (result == null) {
                result = (TypeEdge) this.search.getVar(this.varIx);
            }
            return result;
        }

        /** Tests the label constraint, in addition to calling the super method. */
        @Override
        boolean isImageCorrect(HostEdge image) {
            return isGuardSatisfied(image.getType()) && super.isImageCorrect(image);
        }

        private TypeEdge varSeed;
        /** The (non-negative) index of {@link #var} in the result. */
        private final int varIx;
    }

    /** Record for this type of search item. */
    private class VarEdgeMultipleRecord extends Edge2MultipleRecord {
        /** Constructs a new record, for a given matcher. */
        VarEdgeMultipleRecord(Search search, int edgeIx, int sourceIx, int targetIx, int varIx,
            boolean sourceFound, boolean targetFound, boolean varFound) {
            super(search, edgeIx, sourceIx, targetIx, sourceFound, targetFound);
            this.varFound = varFound;
            this.varIx = varIx;
        }

        @Override
        public void initialise(HostGraph host) {
            super.initialise(host);
            if (this.varIx >= 0) {
                this.varSeed = (TypeEdge) this.search.getVarSeed(this.varIx);
            }
        }

        @Override
        void init() {
            this.varFind = this.varSeed;
            if (this.varFind == null && this.varFound) {
                this.varFind = this.search.getVar(this.varIx);
            }
            super.init();
        }

        @Override
        void initImages() {
            Set<? extends HostEdge> edgeSet;
            if (this.varFind != null) {
                if (isGuardSatisfied(this.varFind)) {
                    edgeSet = this.host.edgeSet(this.varFind.label());
                } else {
                    edgeSet = EMPTY_IMAGE_SET;
                }
            } else {
                // take the incident edges of the pre-matched source or target, if any
                // otherwise, the set of all edges
                if (this.sourceFind != null) {
                    edgeSet = this.host.outEdgeSet(this.sourceFind);
                } else if (this.targetFind != null) {
                    edgeSet = this.host.inEdgeSet(this.targetFind);
                } else {
                    edgeSet = this.host.edgeSet();
                }
            }
            this.imageIter = edgeSet.iterator();
        }

        @Override
        boolean write(HostEdge image) {
            boolean result = isGuardSatisfied(image.getType()) && super.write(image);
            if (result && this.varFind == null && this.varIx >= 0) {
                result = this.search.putVar(this.varIx, image.getType());
            }
            return result;
        }

        @Override
        void erase() {
            super.erase();
            if (this.varFind == null && this.varIx >= 0) {
                this.search.putVar(this.varIx, null);
            }
        }

        /**
         * The pre-matched variable image, if any.
         */
        private TypeEdge varSeed;
        /** The index of {@link #var} in the result; negative if the variable is unnamed. */
        private final int varIx;
        /**
         * Flag indicating that {@link #var} is matched before this item is
         * invoked.
         */
        private final boolean varFound;
        /**
         * The found variable image, if any.
         */
        private TypeElement varFind;
    }

    private static final HostEdgeSet EMPTY_IMAGE_SET = new HostEdgeSet();
}
