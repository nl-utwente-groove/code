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
package groove.abstraction.neigh.explore.strategy;

import groove.abstraction.neigh.explore.util.ShapeMatchApplier;
import groove.abstraction.neigh.explore.util.ShapeMatchSetCollector;
import groove.abstraction.neigh.lts.AGTS;
import groove.abstraction.neigh.lts.ShapeState;
import groove.explore.strategy.NextOpenStrategy;
import groove.explore.util.MatchSetCollector;
import groove.explore.util.RuleEventApplier;
import groove.lts.GTS;
import groove.lts.GraphState;

/**
 * Breadth-first search exploration strategy for abstract state spaces.
 * @author Eduardo Zambon
 */
public class ShapeBFSStrategy extends NextOpenStrategy {

    @Override
    public void prepare(GTS gts, GraphState state) {
        assert gts instanceof AGTS;
        assert state instanceof ShapeState || state == null;
        super.prepare(gts, state);
    }

    /** Returns the match applier of this strategy. */
    @Override
    protected RuleEventApplier createMatchApplier() {
        return new ShapeMatchApplier((AGTS) this.getGTS());
    }

    /**
     * Returns a fresh match collector for this strategy, based on the current
     * state and related information.
     */
    @Override
    protected MatchSetCollector createMatchCollector() {
        return new ShapeMatchSetCollector((ShapeState) getState(), getRecord(),
            getGTS().checkDiamonds());
    }
}
