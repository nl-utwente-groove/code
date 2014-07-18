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

import groove.abstraction.neigh.lts.AGTS;
import groove.abstraction.neigh.lts.ShapeState;
import groove.explore.result.Acceptor;
import groove.explore.strategy.DFSStrategy;
import groove.lts.GTS;
import groove.lts.GraphState;

/**
 * Depth-first search exploration strategy for abstract state spaces.
 * 
 * At each state all matches for all rules are explored and the state is closed.
 * Traversal then continues over one immediate successor. This means that
 * the states are visited according to a LIFO order. Due to the subsumption
 * checks performed during exploration this strategy is more likely to yield
 * a smaller state space and thus uses less memory than BFS.
 * 
 * @author Eduardo Zambon
 */
public final class ShapeDFSStrategy extends DFSStrategy {
    @Override
    public void prepare(GTS gts, GraphState state, Acceptor acceptor) {
        assert gts instanceof AGTS;
        assert state instanceof ShapeState || state == null;
        super.prepare(gts, state, acceptor);
    }

    @Override
    protected GraphState computeNextState() {
        ShapeState result;
        do {
            result = (ShapeState) super.computeNextState();
        } while (result != null && result.isSubsumed());
        return result;
    }
}
