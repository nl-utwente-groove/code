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
 * $Id: OptimizedBoundedNestedDFSStrategy.java,v 1.2 2008/02/22 13:02:45 rensink
 * Exp $
 */
package groove.explore.strategy;

import groove.verify.ProductState;
import groove.verify.ModelChecking;

import java.util.Iterator;

/**
 * This bounded version deviates from the default nested DFS in the way of
 * setting the next state to be explored if the previous iteration terminated
 * successfully. The start state is selected from the set of target states of
 * boundary-crossing transitions.
 * 
 * @author Harmen Kastenberg
 * @version $Revision$
 */
public class OptimizedBoundedNestedDFSPocketStrategy extends
        BoundedNestedDFSPocketStrategy {
    @Override
    protected void setNextStartState() {
        while (getProductGTS().hasOpenStates() && getAtBuchiState() == null) {
            // increase the boundary
            getBoundary().increase();
            ModelChecking.nextIteration();
            // iterator over the open states
            // TODO: maybe there is a more efficient way of
            // iterating over the open states than to start
            // at the beginning every time one has been
            // processed
            Iterator<ProductState> openStateIter =
                getProductGTS().getOpenStateIter();
            while (openStateIter.hasNext() && getAtBuchiState() == null) {
                ProductState nextOpenState = openStateIter.next();
                // states that are part of later iterations
                // are not considered here
                if (nextOpenState.iteration() <= ModelChecking.CURRENT_ITERATION) {
                    // furthermore, the transition by which the next open
                    // state is reached should also not cross the current
                    // boundary
                    if (getBoundary() instanceof GraphNodeSizeBoundary
                        && ((GraphNodeSizeBoundary) getBoundary()).crossingBoundary(nextOpenState.getGraphState().getGraph())) {
                        continue;
                    } else {
                        setAtBuchiState(nextOpenState);
                        // this.atBuchiState = nextOpenState;
                        ModelChecking.nextColourScheme();
                        searchStack().clear();
                        transitionStack().clear();
                        this.lastTransition = null;
                    }
                }
            }
        }
    }
}
