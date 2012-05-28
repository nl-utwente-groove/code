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
package groove.abstraction.neigh.explore.util;

import groove.abstraction.neigh.lts.ShapeState;
import groove.abstraction.neigh.match.PreMatch;
import groove.abstraction.neigh.trans.NeighAnchorFactory;
import groove.control.CtrlTransition;
import groove.explore.util.MatchSetCollector;
import groove.lts.MatchResult;
import groove.trans.Proof;
import groove.trans.RuleEvent;
import groove.trans.SystemRecord;

import java.util.Collection;

/**
 * Collector of pre-matches of rules over shapes.
 * In order for this class to compute the proper number of matches, a
 * dedicated anchor factory must be installed. For the neighbourhood 
 * abstraction we used the {@link NeighAnchorFactory}.
 * 
 * @author Eduardo Zambon
 */
public final class ShapeMatchSetCollector extends MatchSetCollector {

    /**
     * Constructs a match collector for a given (start) state.
     * @param state the state for which matches are to be collected
     * @param record factory to turn {@link Proof}es in to
     *        {@link RuleEvent}s.
     */
    public ShapeMatchSetCollector(ShapeState state, SystemRecord record,
            boolean checkDiamonds) {
        super(state, record, checkDiamonds);
    }

    @Override
    protected boolean collectEvents(CtrlTransition ctrlTrans,
            final Collection<MatchResult> result) {
        for (Proof preMatch : PreMatch.getPreMatches(
            ((ShapeState) this.state).getGraph(), ctrlTrans.getRule())) {
            result.add(this.record.getEvent(preMatch));
        }
        return !result.isEmpty();
    }

}
