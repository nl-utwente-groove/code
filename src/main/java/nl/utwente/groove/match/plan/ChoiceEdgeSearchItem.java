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
import java.util.List;
import java.util.Set;

import nl.utwente.groove.grammar.host.HostEdge;
import nl.utwente.groove.grammar.rule.RuleEdge;
import nl.utwente.groove.grammar.type.TypeEdge;
import nl.utwente.groove.grammar.type.TypeLabel;
import nl.utwente.groove.match.plan.PlanSearchStrategy.Search;

/**
 * A search item that searches an image for an edge labelled with a choice
 * between atoms. In contrast to {@link RegExprEdgeSearchItem}, the item binds a
 * genuine host edge image (any witness of the choice is a single host edge),
 * so that the injective matching of eraser edges (the DPO identification
 * condition) extends to such choices. This item is only used for non-simple
 * (multigraph) matching; in simple-graph mode, choices retain the
 * automaton-based semantics, where only the end nodes are bound.
 * @author Arend Rensink
 * @version $Revision$
 */
class ChoiceEdgeSearchItem extends Edge2SearchItem {
    /**
     * Creates a search item for a given edge, whose label is a choice
     * between atoms (see {@link nl.utwente.groove.grammar.rule.RuleLabel#isAtomChoice()}).
     * @param edge the edge to be matched
     */
    public ChoiceEdgeSearchItem(RuleEdge edge) {
        // the simple flag is hardcoded to false: in simple-graph mode the
        // image would be singular for pre-matched ends, which does not hold
        // for a choice as the image label is not determined
        super(edge, false);
        List<TypeLabel> choiceLabels = edge.label().getAtomChoiceLabels();
        assert choiceLabels != null;
        this.choiceLabels = choiceLabels;
        this.matchingTypes = edge.getMatchingTypes();
    }

    /** Tests if a given host edge type is among the possible types of the choice. */
    @Override
    boolean checkEdgeType(HostEdge image) {
        return this.matchingTypes.contains(image.getType());
    }

    @Override
    MultipleRecord<HostEdge> createMultipleRecord(Search search) {
        return new ChoiceEdgeMultipleRecord(search, this.edgeIx, this.sourceIx, this.targetIx,
            this.sourceFound, this.targetFound);
    }

    /** The type labels of the choice operands, in operand order. */
    private final List<TypeLabel> choiceLabels;
    /** The possible edge types of the image. */
    private final Set<TypeEdge> matchingTypes;

    /** Record for this type of search item. */
    private class ChoiceEdgeMultipleRecord extends Edge2MultipleRecord {
        /** Constructs a new record, for a given matcher. */
        ChoiceEdgeMultipleRecord(Search search, int edgeIx, int sourceIx, int targetIx,
                                 boolean sourceFound, boolean targetFound) {
            super(search, edgeIx, sourceIx, targetIx, sourceFound, targetFound);
        }

        @Override
        void initImages() {
            if (this.sourceFind != null) {
                this.imageIter = this.host.outEdgeSet(this.sourceFind).iterator();
            } else if (this.targetFind != null) {
                this.imageIter = this.host.inEdgeSet(this.targetFind).iterator();
            } else {
                // concatenate the per-operand-label edge sets, in operand order
                List<HostEdge> images = new ArrayList<>();
                for (TypeLabel label : ChoiceEdgeSearchItem.this.choiceLabels) {
                    images.addAll(this.host.edgeSet(label));
                }
                this.imageIter = images.iterator();
            }
        }
    }
}
