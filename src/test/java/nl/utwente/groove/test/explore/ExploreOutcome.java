/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.test.explore;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import nl.utwente.groove.explore.Exploration;
import nl.utwente.groove.explore.ExploreType;
import nl.utwente.groove.grammar.Grammar;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GTSListener;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.lts.GraphTransition;
import nl.utwente.groove.lts.Status;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Summary of an exploration run, for comparison across runs: the state and
 * transition counts, the numbers of the result states, and an order-bearing
 * trace of the exploration. The trace records every transition addition and
 * every state closure in the order they occurred, so two outcomes are equal
 * only if the explorations proceeded identically; counts and result states
 * alone cannot distinguish two runs that cover the same state space in a
 * different order (and on grammars without final states, the result-state
 * set of a default exploration is always empty).
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
public record ExploreOutcome(int states, int transitions, Set<Integer> resultStates,
    List<String> trace) {

    /**
     * Explores a fresh GTS over a given grammar with a given exploration
     * type, and summarises the run.
     */
    public static ExploreOutcome explore(Grammar grammar, ExploreType type) throws FormatException {
        GTS gts = new GTS(grammar);
        List<String> trace = new ArrayList<>();
        gts.addLTSListener(new GTSListener() {
            @Override
            public void addUpdate(GTS observed, GraphTransition transition) {
                trace
                    .add(transition.source() + "--" + transition.label() + "->"
                        + transition.target());
            }

            @Override
            public void statusUpdate(GTS observed, GraphState state, int change) {
                if (Status.Flag.CLOSED.test(change) && state.isClosed()) {
                    trace.add("close " + state);
                }
            }
        });
        Exploration exploration = type.newExploration(gts, null).play();
        Set<Integer> resultStates = exploration
            .getResult()
            .getStates()
            .stream()
            .map(GraphState::getNumber)
            .collect(Collectors.toSet());
        return new ExploreOutcome(gts.nodeCount(), gts.edgeCount(), resultStates, trace);
    }
}
