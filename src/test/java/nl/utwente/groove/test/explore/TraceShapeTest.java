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

import static nl.utwente.groove.test.explore.ExploreTestSupport.explore;
import static nl.utwente.groove.test.explore.ExploreTestSupport.loadGrammar;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.ClassRule;
import org.junit.Test;

import nl.utwente.groove.explore.Exploration;
import nl.utwente.groove.grammar.Grammar;
import nl.utwente.groove.lts.AbstractGraphState;
import nl.utwente.groove.lts.GTSFragment;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.lts.GraphTransition;
import nl.utwente.groove.lts.RecipeTransition;
import nl.utwente.groove.test.MasterSeedGuard;

/**
 * Tests the trace result shape: under {@code shape=trace}, the result of an
 * exploration is presented as the traces from the start state to the result
 * states. The traces are realised as {@link GTSFragment}s completed with
 * paths from the start state; on the public (non-internal) level, a trace
 * segment through a recipe is characterised by the corresponding recipe
 * transition — which the transition machinery reconstructs from the
 * spanning rule-step stubs, so this holds even for the retained trace of an
 * unstored exploration after its caches are gone.
 * @author Arend Rensink
 * @version $Revision$
 */
public class TraceShapeTest {
    /** Restores the master-seed state that the tests in this class modify. */
    @ClassRule
    public static final MasterSeedGuard SEED_GUARD = new MasterSeedGuard();


    /**
     * Tests that the trace fragment of a goal exploration is connected and
     * contains the result states.
     */
    @Test
    public void testTraceFragmentConnected() throws Exception {
        Grammar grammar = loadGrammar("ferryman");
        Exploration exploration
            = explore(grammar, "shape=trace goal=condition:eat count=first");
        assertFalse(exploration.getResult().isEmpty(), "The goal state should have been found");
        GTSFragment fragment = exploration.getResult().toFragment(false);
        assertConnected(fragment);
        for (GraphState result : exploration.getResult().getStates()) {
            assertTrue(fragment.nodeSet().contains(result),
                       "Result state %s should be on the trace".formatted(result));
        }
    }

    /**
     * Tests that under a stored exploration, a public-level trace through a
     * recipe is characterised by the recipe transition, without internal
     * steps.
     */
    @Test
    public void testRecipeTransitionInTrace() throws Exception {
        Grammar grammar = loadGrammar("recipe-priorities");
        Exploration exploration
            = explore(grammar, "frontier=single successor=single");
        GTSFragment fragment = lastStateFragment(exploration);
        assertConnected(fragment);
        assertTrue(fragment.edgeSet().stream().anyMatch(t -> t instanceof RecipeTransition),
                   "The public trace should contain a recipe transition");
        assertTrue(fragment.edgeSet().stream().noneMatch(GraphTransition::isInnerStep),
                   "The public trace should not contain internal steps");
    }

    /**
     * Tests that the public-level trace of an unstored exploration also
     * shows recipe transitions, even after the (softly referenced) state
     * caches are gone: the retained spanning rule-step stubs suffice for
     * the transition machinery to reconstruct the recipe transitions.
     * (Cache clearing simulates garbage collection, as in
     * {@code DeterminismTest}.)
     */
    @Test
    public void testRecipeTransitionWithoutPersistence() throws Exception {
        Grammar grammar = loadGrammar("recipe-priorities");
        Exploration exploration
            = explore(grammar, "persistence=none frontier=single successor=single");
        for (GraphState state : exploration.getGTS().nodeSet()) {
            if (state.isClosed() && state instanceof AbstractGraphState closed) {
                closed.clearCache();
            }
        }
        GTSFragment fragment = lastStateFragment(exploration);
        assertConnected(fragment);
        assertTrue(fragment.edgeSet().stream().anyMatch(t -> t instanceof RecipeTransition),
                   "The public trace should contain a reconstructed recipe transition");
        assertTrue(fragment.edgeSet().stream().noneMatch(GraphTransition::isInnerStep),
                   "The public trace should not contain internal steps");
    }

    /** Builds the completed public-level trace fragment of the last explored
     * state of an exploration. */
    private GTSFragment lastStateFragment(Exploration exploration) {
        GraphState last = exploration.getLastState();
        var fragment = new GTSFragment(exploration.getGTS(), Set.of(last), Set.of());
        fragment.complete(false);
        return fragment;
    }

    /**
     * Asserts that a fragment is connected: every edge has its endpoints in
     * the fragment, and every node except the start state has an incoming
     * edge. Internal states are exempt from the incoming-edge requirement
     * only if they are trace tips (the public view does not lead into a
     * recipe).
     */
    private void assertConnected(GTSFragment fragment) {
        for (GraphTransition trans : fragment.edgeSet()) {
            assertTrue(fragment.nodeSet().contains(trans.source()),
                       "Source of %s should be in the fragment".formatted(trans));
            assertTrue(fragment.nodeSet().contains(trans.target()),
                       "Target of %s should be in the fragment".formatted(trans));
        }
        for (GraphState node : fragment.nodeSet()) {
            if (node.equals(fragment.startState()) || node.isInner()) {
                continue;
            }
            assertTrue(fragment.edgeSet().stream().anyMatch(t -> t.target().equals(node)),
                       "Node %s should be reachable in the fragment".formatted(node));
        }
    }

}
