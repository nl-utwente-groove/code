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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import groove.algebra.AlgebraFamily;
import groove.explore.result.Acceptor;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.MatchResult;
import groove.lts.RuleTransition;
import groove.sts.Location;
import groove.sts.STS;
import groove.sts.STSException;
import groove.sts.SwitchRelation;

/**
 * Explores the graph states using a given strategy and builds an STS
 * from the GTS. The best result is obtained using a Point Algebra.
 * @author Vincent de Bruijn
 * @version $Revision $
 */
public class SymbolicStrategy extends GTSStrategy {

    /**
     * The strategy this SymbolicStrategy will use.
     */
    protected ClosingStrategy strategy;
    /**
     * The STS this SymbolicStrategy will build.
     */
    protected STS sts;

    /**
     * Set the exploration strategy to use.
     * @param strategy The strategy.
     */
    public void setStrategy(ClosingStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Getter for the STS this strategy is building.
     * @return The STS.
     */
    public STS getSTS() {
        return this.sts;
    }

    @Override
    protected void prepare(GTS gts, GraphState state, Acceptor acceptor) {
        super.prepare(gts, state, acceptor);
        // Check if the point algebra is set. This should be moved to the hook
        // in an upcoming feature
        if (getGTS().getGrammar()
            .getProperties()
            .getAlgebraFamily() != AlgebraFamily.POINT) {
            System.err.print("Grammar AlgebraFamily property should be point,"
                + "if the SymbolicStrategy is used.");
            return;
        }

        if (this.strategy == null) {
            this.strategy = new BFSStrategy();
        }
        this.strategy.prepare(gts, state, acceptor);
        this.sts = new STS();
        this.sts.hostGraphToStartLocation(getGTS().getGrammar()
            .getStartGraph());
    }

    @Override
    public GraphState doNext() throws InterruptedException {
        GraphState state = getNextState();
        assert state != null;
        // If the current location is new, determine its outgoing switch
        // relations
        Location current = this.sts.getCurrentLocation();
        // Get current rule matches
        Collection<? extends MatchResult> matchSet = state.getMatches();
        if (!matchSet.isEmpty()) {
            // Sort the matches in priority groups
            List<Collection<? extends MatchResult>> priorityGroups = createPriorityGroups(matchSet);
            Set<SwitchRelation> higherPriorityRelations = new HashSet<>();
            Set<SwitchRelation> temp = new HashSet<>();
            boolean emptyGuard = false;
            for (Collection<? extends MatchResult> matches : priorityGroups) {
                for (MatchResult next : matches) {
                    SwitchRelation sr = null;
                    try {
                        sr = this.sts.ruleMatchToSwitchRelation(getNextState().getGraph(),
                            next,
                            higherPriorityRelations);
                    } catch (STSException e) {
                        // TODO: handle this exception
                        throw new IllegalStateException(e);
                    }
                    if (sr.getGuard()
                        .isEmpty()) {
                        emptyGuard = true;
                    }
                    temp.add(sr);
                    RuleTransition transition = getNextState().applyMatch(next);
                    Location l = this.sts.hostGraphToLocation(transition.target()
                        .getGraph());
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
        setNextState();
        return state;
    }

    @Override
    protected GraphState computeNextState() {
        GraphState state = null;
        // Use the strategy to decide on the next state.
        state = this.strategy.computeNextState();
        if (state != null) {
            this.sts.toLocation(this.sts.hostGraphToLocation(state.getGraph()));
        }
        return state;
    }

    /**
     * Turns a collection of match results into a list of collections of match
     * results, ordered by rule priority.
     */
    private List<Collection<? extends MatchResult>> createPriorityGroups(
        Collection<? extends MatchResult> matches) {
        List<MatchResult> sortedMatches = new ArrayList<>(matches);
        Collections.sort(sortedMatches, new PriorityComparator());
        List<Collection<? extends MatchResult>> priorityGroups =
            new ArrayList<>();
        int priority = sortedMatches.get(0)
            .getAction()
            .getPriority();
        Collection<MatchResult> current = new HashSet<>();
        for (MatchResult match : sortedMatches) {
            if (match.getAction()
                .getPriority() != priority) {
                priorityGroups.add(current);
                current = new HashSet<>();
                priority = match.getAction()
                    .getPriority();
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
            return res2.getAction()
                .getPriority()
                - res1.getAction()
                    .getPriority();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Comparator<?>)) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        public int hashCode() {
            return Comparator.class.hashCode();
        }
    }

}
