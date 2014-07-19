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
package groove.explore.strategy;

import groove.verify.ModelChecking;
import groove.verify.ProductState;
import groove.verify.ProductTransition;

/**
 * This bounded version deviates from the default nested DFS in the way it deals
 * with so-called pocket states. This strategy black-paints the pocket states
 * such that they will not be considered in any further iteration.
 * 
 * @author Harmen Kastenberg
 * @version $Revision$
 */
public class BoundedPocketLTLStrategy extends BoundedLTLStrategy {
    @Override
    protected void colourState(ProductState state) {
        checkPocket(state);
        // if this state is a pocket-state we actually do not
        // need to further colour it blue or red
        // nevertheless, for correctness reasons we still do it
        // (in case the pocket detection is faulty, the colouring
        // is at least correct)
        super.colourState(state);
    }

    /**
     * Checks whether the given state is unexplored. This is determined based on
     * the state-colour.
     * @param newState the state to be checked
     * @return <tt>true</tt> if the state is a non-pocket state or colour
     *         neither cyan, blue, nor red, <tt>false</tt> otherwise
     */
    @Override
    protected boolean isUnexplored(ProductState newState) {
        boolean result =
            (!newState.isPocket() || newState.colour() == ModelChecking.NO_COLOUR)
                && super.isUnexplored(newState);
        return result;
    }

    /**
     * Determines whether a given state can be marked as a pocket state. This is the case
     * when either the state has no outgoing transitions, or when all its
     * successor-states are pocket states.
     * @param state the state to be marked black potentially
     */
    private void checkPocket(ProductState state) {
        for (ProductTransition transition : state.outTransitions()) {
            if (transition.graphTransition() != null
                && !transition.target().isPocket()) {
                return;
            }
        }
        state.setPocket();
        return;
    }

}
