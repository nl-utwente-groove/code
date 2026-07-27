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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;

import nl.utwente.groove.explore.ExploreType;
import nl.utwente.groove.explore.config.ExploreConfig;
import nl.utwente.groove.explore.config.ExploreTypeConverter;
import nl.utwente.groove.grammar.Grammar;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.util.Groove;
import nl.utwente.groove.util.Randomness;

/**
 * Tests that transient states bypass the exploration pool: they belong to a
 * nested sub-exploration ending in an atomic (transactional) transition,
 * which the strategy must run to completion regardless of the exploration
 * order. The fibonacci grammar is the regression fixture: its recursive
 * recipe produces transient states whose control-frame attempt has a pending
 * verdict (the try/else around a nested recipe call), which formerly leaked
 * them into the pool through the trial re-add of ClosingStrategy.doNext —
 * under a restrictive beam such a state could then be dropped, cutting into
 * a transaction.
 * @author Arend Rensink
 * @version $Revision$
 */
public class TransientNestingTest {
    /** Location of the sample grammars. */
    static private final String INPUT_DIR = "junit/samples";

    /** State and transition count of the fully explored fibonacci grammar
     * (with its default recursive-recipe control program), as also asserted
     * by {@code ExplorationTest.testFibonacci}. */
    static private final int FIB_STATES = 63;
    static private final int FIB_TRANSITIONS = 63;

    /**
     * Tests that the complete exploration orders all fully explore the
     * fibonacci state space, and that no transient state is left open.
     */
    @Test
    public void testCompleteOrders() throws Exception {
        Grammar grammar = loadGrammar();
        for (String config : new String[] {"", "next=newest", "next=random"}) {
            GTS gts = explore(grammar, config);
            assertEquals(FIB_STATES, gts.nodeCount(),
                         "Config '%s' should explore the full state space".formatted(config));
            assertEquals(FIB_TRANSITIONS, gts.edgeCount(),
                         "Config '%s' should find all transitions".formatted(config));
            assertNestingInvariant(gts, config);
        }
    }

    /**
     * Tests that a restrictive beam never cuts into a transaction: however
     * much of the state space it drops, every transient state it discovers
     * is still explored to completion.
     */
    @Test
    public void testBeamOrders() throws Exception {
        Grammar grammar = loadGrammar();
        for (String config : new String[] {"frontier=beam:2", "next=newest frontier=beam:2",
            "next=random frontier=beam:2"}) {
            GTS gts = explore(grammar, config);
            assertTrue(gts.nodeCount() > 1,
                       "Config '%s' should explore beyond the start state".formatted(config));
            assertNestingInvariant(gts, config);
        }
    }

    /** Asserts that every transient state of a GTS has been fully explored
     * (closed), i.e., no transaction was suspended or cut off. */
    private void assertNestingInvariant(GTS gts, String config) {
        for (GraphState state : gts.nodeSet()) {
            if (state.isTransient()) {
                assertTrue(state.isClosed(),
                           "Config '%s' left transient state %s unexplored"
                               .formatted(config, state));
            }
        }
    }

    /** Explores a fresh GTS with a given configuration, under a fixed
     * master seed (relevant for the random orders). */
    private GTS explore(Grammar grammar, String config) throws Exception {
        Randomness.setMasterSeed(42);
        ExploreType type = ExploreTypeConverter.toExploreType(ExploreConfig.parse(config));
        GTS gts = new GTS(grammar);
        type.newExploration(gts, null).play();
        return gts;
    }

    /** Loads the fibonacci grammar, whose default control program is the
     * recursive recipe. */
    private Grammar loadGrammar() throws Exception {
        return Groove.loadGrammar(INPUT_DIR + "/fibonacci").toGrammar();
    }
}
