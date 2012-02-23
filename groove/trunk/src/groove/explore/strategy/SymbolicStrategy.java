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
package groove.explore.strategy;

import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.MatchResult;
import groove.lts.RuleTransition;
import groove.sts.CompleteSTS;
import groove.sts.Location;
import groove.sts.STS;
import groove.sts.STSException;
import groove.sts.SwitchRelation;

/**
 * Explores the graph states using a given strategy and builds an STS 
 * from the GTS.
 * @author Vincent de Bruijn
 * @version $Revision $
 */
public class SymbolicStrategy extends AbstractStrategy {

    private DFSStrategy dfsStrategy;
    private STS sts;

    @Override
    public void prepare(GTS gts, GraphState startState) {
        super.prepare(gts, startState);

        // Initiate the Depth-First strategy
        this.dfsStrategy = new DFSStrategy();
        this.dfsStrategy.prepare(gts, startState);

        this.sts = new CompleteSTS();
        this.sts.hostGraphToStartLocation(startState.getGraph());
    }

    @Override
    public boolean next() {
        if (getState() == null) {
            return false;
        }
        // If the current location is new, determine its outgoing switch
        // relations
        Location current = this.sts.getCurrentLocation();
        // Get current rule matches
        for (MatchResult next : createMatchCollector().getMatchSet()) {
            SwitchRelation sr = null;
            try {
                sr =
                    this.sts.ruleMatchToSwitchRelation(getState().getGraph(),
                        next);
            } catch (STSException e) {
                // TODO: handle this exception
                System.out.println(e.getStackTrace());
            }
            if (current.getRelationTarget(sr) == null) {
                RuleTransition transition =
                    getMatchApplier().apply(getState(), next);
                Location l =
                    this.sts.hostGraphToLocation(transition.target().getGraph());
                current.addSwitchRelation(sr, l);
            }
        }
        return updateAtState();
    }

    /**
     * Getter for the STS this strategy is building.
     * @return The STS.
     */
    public STS getSTS() {
        return this.sts;
    }

    @Override
    protected GraphState getNextState() {
        GraphState state = null;
        // Use the DfsStrategy to decide on the next state.
        state = this.dfsStrategy.getNextState();
        if (state != null) {
            this.sts.toLocation(this.sts.hostGraphToLocation(state.getGraph()));
        }
        return state;
    }

}
