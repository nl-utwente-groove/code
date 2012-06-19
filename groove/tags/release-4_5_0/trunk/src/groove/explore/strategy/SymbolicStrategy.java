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
import groove.sts.Location;
import groove.sts.STS;
import groove.sts.STSException;
import groove.sts.SwitchRelation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Explores the graph states using a given strategy and builds an STS 
 * from the GTS. The best result is obtained using a Point Algebra.
 * @author Vincent de Bruijn
 * @version $Revision $
 */
public class SymbolicStrategy extends AbstractStrategy {

    /**
     * The strategy this SymbolicStrategy will use.
     */
    protected ClosingStrategy strategy;
    /**
     * The STS this SymbolicStrategy will build.
     */
    protected STS sts;

    @Override
    public void prepare(GTS gts, GraphState startState) {
        super.prepare(gts, startState);
        if (this.strategy != null) {
            this.strategy.prepare(gts, startState);
        }
        if (this.sts == null) {
            // throw exception
        } else {
            this.sts.hostGraphToStartLocation(startState.getGraph());
        }
    }

    /**
     * Set the exploration strategy to use.
     * @param strategy The strategy.
     */
    public void setStrategy(ClosingStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Sets the sts to use.
     * @param sts The sts.
     */
    public void setSTS(STS sts) {
        this.sts = sts;
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
        Collection<MatchResult> matchSet = createMatchCollector().getMatchSet();
        if (!matchSet.isEmpty()) {
            // Sort the matches in priority groups
            List<Collection<MatchResult>> priorityGroups =
                createPriorityGroups(matchSet);
            Set<SwitchRelation> higherPriorityRelations =
                new HashSet<SwitchRelation>();
            Set<SwitchRelation> temp = new HashSet<SwitchRelation>();
            boolean emptyGuard = false;
            for (Collection<MatchResult> matches : priorityGroups) {
                for (MatchResult next : matches) {
                    SwitchRelation sr = null;
                    try {
                        sr =
                            this.sts.ruleMatchToSwitchRelation(
                                getState().getGraph(), next,
                                higherPriorityRelations);
                    } catch (STSException e) {
                        // TODO: handle this exception
                        System.out.println(e.getStackTrace());
                    }
                    if (sr.getGuard().isEmpty()) {
                        emptyGuard = true;
                    }
                    temp.add(sr);
                    RuleTransition transition =
                        getMatchApplier().apply(getState(), next);
                    Location l =
                        this.sts.hostGraphToLocation(transition.target().getGraph());
                    current.addSwitchRelation(sr, l);
                }
                if (emptyGuard) {
                    // A higher priority rule is always applicable from the current location,
                    // therefore the lower priority rules do not need to be checked.
                    break;
                }
                higherPriorityRelations.addAll(temp);
                temp.clear();
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
        // Use the strategy to decide on the next state.
        state = this.strategy.getNextState();
        if (state != null) {
            this.sts.toLocation(this.sts.hostGraphToLocation(state.getGraph()));
        }
        return state;
    }

    /**
     * Turns a collection of match results into a list of collections of match
     * results, ordered by rule priority.
     */
    private List<Collection<MatchResult>> createPriorityGroups(
            Collection<MatchResult> matches) {
        List<MatchResult> sortedMatches = new ArrayList<MatchResult>(matches);
        Collections.sort(sortedMatches, new PriorityComparator());
        List<Collection<MatchResult>> priorityGroups =
            new ArrayList<Collection<MatchResult>>();
        int priority = sortedMatches.get(0).getEvent().getRule().getPriority();
        Collection<MatchResult> current = new HashSet<MatchResult>();
        for (MatchResult match : sortedMatches) {
            if (match.getEvent().getRule().getPriority() != priority) {
                priorityGroups.add(current);
                current = new HashSet<MatchResult>();
                priority = match.getEvent().getRule().getPriority();
            }
            current.add(match);
        }
        priorityGroups.add(current);
        return priorityGroups;
    }

    /* Comparator for priority sorting. */
    private class PriorityComparator implements Comparator<MatchResult> {

        @Override
        public int compare(MatchResult res1, MatchResult res2) {
            return res2.getEvent().getRule().getPriority()
                - res1.getEvent().getRule().getPriority();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Comparator<?>)) {
                return false;
            } else {
                return true;
            }
        }
    }

}