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
 * $Id: WildcardEdgeSearchItem.java,v 1.13 2008-01-30 09:33:29 iovka Exp $
 */
package groove.match;

import groove.graph.TypeLabel;
import groove.match.SearchPlanStrategy.Search;
import groove.trans.HostEdge;
import groove.trans.RuleEdge;

import java.util.Set;

/**
 * A search item that searches an image for an edge.
 * @author Arend Rensink
 * @version $Revision $
 */
class WildcardEdgeSearchItem extends Edge2SearchItem {
    /**
     * Constructs a new search item. The item will match any edge between the
     * end images, and record the edge label as value of the wildcard variable.
     */
    public WildcardEdgeSearchItem(RuleEdge edge) {
        super(edge);
        assert edge.label().isWildcard()
            && edge.label().getWildcardId() == null : String.format(
            "Edge %s is not a true wildcard edge", edge);
        this.labelConstraint = edge.label().getWildcardGuard();
    }

    /** This implementation returns <code>false</code>. */
    @Override
    boolean isSingular(Search search) {
        return false;
    }

    /** This implementation returns a {@link WildcardEdgeRecord}. */
    @Override
    MultipleRecord<HostEdge> createMultipleRecord(Search search) {
        return new WildcardEdgeRecord(search, this.edgeIx, this.sourceIx,
            this.targetIx, this.sourceFound, this.targetFound);
    }

    boolean isLabelConstraintSatisfied(TypeLabel label) {
        return this.labelConstraint == null
            || this.labelConstraint.isSatisfied(label);
    }

    /** The constraint on the wildcard valuation, if any. */
    final groove.util.Property<TypeLabel> labelConstraint;

    /** Record for this type of search item. */
    class WildcardEdgeRecord extends Edge2MultipleRecord {
        /** Constructs a new record, for a given matcher. */
        WildcardEdgeRecord(Search search, int edgeIx, int sourceIx,
                int targetIx, boolean sourceFound, boolean targetFound) {
            super(search, edgeIx, sourceIx, targetIx, sourceFound, targetFound);
        }

        @Override
        void initImages() {
            Set<? extends HostEdge> edgeSet;
            if (this.sourceFind != null) {
                edgeSet = this.host.outEdgeSet(this.sourceFind);
            } else if (this.targetFind != null) {
                edgeSet = this.host.inEdgeSet(this.targetFind);
            } else {
                edgeSet = this.host.edgeSet();
            }
            initImages(edgeSet, true, true, false, true);
        }

        /**
         * First tests the image label against {@link #labelConstraint}; if
         * <code>true</code>, calls the super method, otherwise returns
         * <code>false</code>.
         */
        @Override
        boolean setImage(HostEdge image) {
            if (WildcardEdgeSearchItem.this.labelConstraint == null
                || WildcardEdgeSearchItem.this.labelConstraint.isSatisfied(image.label())) {
                return super.setImage(image);
            } else {
                return false;
            }
        }
    }
}