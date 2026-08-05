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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.Test;

import nl.utwente.groove.explore.Exploration;
import nl.utwente.groove.explore.config.ExploreConfig;
import nl.utwente.groove.explore.config.ExploreTypeConverter;
import nl.utwente.groove.grammar.Grammar;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GraphNextState;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.transform.Transformer;
import nl.utwente.groove.util.Groove;
import nl.utwente.groove.util.Randomness;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Tests the persistence feature: under {@code persistence=none}, discovered
 * states and transitions are not stored in the GTS. Every discovered state
 * then counts as fresh (state collapse is inoperative), so the exploration
 * traverses the tree unfolding of the state space. At the end of the
 * exploration, the GTS retains the tree of traces to the result states and
 * to the last explored state — the visible product of the run — while the
 * discovered bulk is forgotten (and garbage collected).
 * <p>
 * Because there is no revisit detection, unpersisted exploration only
 * terminates if the traversed tree is finite: the ferryman fixture (cyclic)
 * is explored under a depth bound, and the fibonacci fixture (whose
 * individual computation paths all terminate, though its tree unfolding is
 * exponentially wide) is avoided altogether — its recursive recipe makes
 * even a beam explode, since transient sub-explorations are exhaustive.
 * @author Arend Rensink
 * @version $Revision$
 */
public class PersistenceTest {
    /** Location of the sample grammars. */
    static private final String INPUT_DIR = "junit/samples";

    /** Depth bound making the tree unfolding of the (cyclic) ferryman
     * grammar finite. */
    static private final String BOUND = "cost=uniform bound=cost:6";

    /**
     * Tests that without persistence, the GTS afterwards contains the trace
     * tree: strictly fewer states than were discovered, connected as a tree
     * rooted in the start state, with consistent counts.
     */
    @Test
    public void testNoneRetainsTraces() throws Exception {
        Grammar grammar = loadGrammar("ferryman");
        // the random order cannot carry a depth bound, so it is tested on the
        // tree-shaped counting grammar instead (see testNoneOnTreeGrammar)
        for (String config : new String[] {"persistence=none " + BOUND,
            "persistence=none next=newest " + BOUND}) {
            GTS gts = explore(grammar, config).getGTS();
            assertTrue(gts.nodeCount() > 1,
                       "Config '%s' should retain the trace of the last explored state"
                           .formatted(config));
            assertTrue(gts.nodeCount() < gts.getNextStateNr(),
                       "Config '%s' should retain fewer states than were discovered"
                           .formatted(config));
            assertTrue(gts.startState().isClosed(),
                       "Config '%s' should still have explored the start state"
                           .formatted(config));
            assertTraceTree(gts, config);
        }
    }

    /**
     * Tests that a goal state reachable within the depth bound is collected
     * and that its trace from the start state is retained in the GTS.
     */
    @Test
    public void testNoneCollectsResults() throws Exception {
        Grammar grammar = loadGrammar("ferryman");
        Exploration exploration
            = explore(grammar, "persistence=none goal=condition:eat " + BOUND);
        assertFalse(exploration.getResult().isEmpty(),
                    "The goal state should have been collected");
        for (GraphState result : exploration.getResult().getStates()) {
            GraphState state = result;
            while (state instanceof GraphNextState next) {
                assertTrue(exploration.getGTS().nodeSet().contains(state),
                           "Trace state %s of result %s should be retained"
                               .formatted(state, result));
                state = next.source();
            }
            assertEquals(exploration.getGTS().startState(), state,
                         "The trace of result %s should lead back to the start state"
                             .formatted(result));
        }
        assertTraceTree(exploration.getGTS(), "goal run");
    }

    /**
     * Tests that the unpersisted exploration is deterministic and covers at
     * least the collapsed state space: without collapse, the exploration
     * traverses the tree unfolding, which cannot be smaller.
     */
    @Test
    public void testNoneDiscoveryCount() throws Exception {
        Grammar grammar = loadGrammar("ferryman");
        GTS allGts = explore(grammar, BOUND).getGTS();
        // under full persistence, the discovery counter and node count agree
        assertEquals(allGts.nodeCount(), allGts.getNextStateNr());
        GTS first = explore(grammar, "persistence=none " + BOUND).getGTS();
        GTS second = explore(grammar, "persistence=none " + BOUND).getGTS();
        assertEquals(first.getNextStateNr(), second.getNextStateNr(),
                     "Unpersisted exploration should be deterministic");
        assertEquals(first.nodeCount(), second.nodeCount(),
                     "Unpersisted exploration should retain the same traces");
        assertTrue(first.getNextStateNr() >= allGts.nodeCount(),
                   "The tree unfolding cannot be smaller than the collapsed state space");
    }

    /**
     * Tests that persistence composes with the random orders, a restrictive
     * beam and a random walk: the exploration terminates, and what remains
     * is a trace tree of at most the discovered states. The counting grammar
     * is tree-shaped (its collapsed state space has one transition less than
     * it has states), so its unfolding equals the collapsed space and no
     * depth bound is needed for termination.
     */
    @Test
    public void testNoneOnTreeGrammar() throws Exception {
        Grammar grammar = loadGrammar("counting");
        GTS allGts = explore(grammar, "").getGTS();
        for (String config : new String[] {"persistence=none next=random",
            "persistence=none frontier=beam:2",
            "persistence=none next=random frontier=beam:2",
            "persistence=none frontier=single successor=single-random"}) {
            GTS gts = explore(grammar, config).getGTS();
            assertTrue(gts.nodeCount() > 1,
                       "Config '%s' should retain the trace of the last explored state"
                           .formatted(config));
            assertTrue(gts.getNextStateNr() <= allGts.nodeCount(),
                       "Config '%s' should not discover more than the tree-shaped state space"
                           .formatted(config));
            assertTrue(gts.nodeCount() <= gts.getNextStateNr(),
                       "Config '%s' cannot retain more than was discovered"
                           .formatted(config));
            assertTraceTree(gts, config);
        }
    }

    /**
     * Tests that the Generator's {@code -x} route (via {@link Transformer})
     * honours the configuration-only features: the configured type must be
     * passed through intact, not decomposed into its serialised components,
     * which carry neither persistence nor the engine-only keywords.
     */
    @Test
    public void testTransformerHonoursConfig() throws Exception {
        var transformer = new Transformer(new File(INPUT_DIR + "/ferryman.gps"));
        transformer
            .setExploreType(ExploreTypeConverter
                .toExploreType(ExploreConfig.parse("persistence=none " + BOUND)));
        Randomness.setMasterSeed(42);
        GTS gts = transformer.explore().getGTS();
        assertTrue(gts.nodeCount() > 1, "The trace of the last state should be retained");
        assertTrue(gts.nodeCount() < gts.getNextStateNr(),
                   "Persistence should have been honoured");
    }

    /**
     * Tests that persistence is guarded per GTS: continuing under the same
     * persistence is allowed (re-disabling the storing switch that trace
     * retention flipped back on), but a continued exploration cannot change
     * the recorded persistence — that needs a fresh state space.
     */
    @Test
    public void testPersistenceGuard() throws Exception {
        Grammar grammar = loadGrammar("ferryman");
        Randomness.setMasterSeed(42);
        GTS gts = new GTS(grammar);
        var none = ExploreTypeConverter
            .toExploreType(ExploreConfig.parse("persistence=none " + BOUND));
        none.newExploration(gts, null).play();
        assertFalse(gts.isPersistent());
        assertTrue(gts.isStoring(), "Trace retention should have re-engaged storing");
        // continuing under the same persistence is allowed
        none.newExploration(gts, gts.startState());
        assertFalse(gts.isStoring(), "Continuing unstored should re-disable storing");
        // continuing under full persistence is rejected
        var all = ExploreTypeConverter.toExploreType(ExploreConfig.parse(BOUND));
        assertThrows(FormatException.class, () -> all.newExploration(gts, gts.startState()));
        assertThrows(FormatException.class, () -> all.newExploration(gts, null));
        // and the reverse: an unstored continuation of a stored GTS is rejected
        GTS storedGts = new GTS(grammar);
        all.newExploration(storedGts, null).play();
        assertThrows(FormatException.class,
                     () -> none.newExploration(storedGts, storedGts.startState()));
    }

    /**
     * Tests that the start state is entered in the state set even when the
     * GTS is prepared (applying the persistence feature, which disables
     * storing) before the start state materialises, as the Simulator's GTS
     * reset does. Regression test: a fresh unstored GTS otherwise had a
     * non-null start state that was missing from the node set — violating
     * the invariant on {@code GTS.startState} — which crashed the state
     * list display.
     */
    @Test
    public void testStartStateAlwaysStored() throws Exception {
        Grammar grammar = loadGrammar("ferryman");
        Randomness.setMasterSeed(42);
        GTS gts = new GTS(grammar);
        var none = ExploreTypeConverter
            .toExploreType(ExploreConfig.parse("persistence=none " + BOUND));
        // the Simulator prepares the fresh GTS before the start state is built
        none.prepareGTS(gts);
        assertFalse(gts.isStoring());
        GraphState start = gts.startState();
        assertTrue(gts.nodeSet().contains(start),
                   "The start state should be stored even in an unstored GTS");
        assertEquals(1, gts.nodeCount());
        assertFalse(gts.isStoring(), "Storing the start state should not re-engage storing");
        // a subsequent exploration of the prepared GTS works as before
        none.newExploration(gts, start).play();
        assertTraceTree(gts, "prepared-first run");
    }

    /**
     * Tests that an explicit {@code persistence=all} behaves identically to
     * the default configuration.
     */
    @Test
    public void testAllIsDefault() throws Exception {
        Grammar grammar = loadGrammar("ferryman");
        GTS byDefault = explore(grammar, "").getGTS();
        GTS explicit = explore(grammar, "persistence=all").getGTS();
        assertEquals(byDefault.nodeCount(), explicit.nodeCount());
        assertEquals(byDefault.edgeCount(), explicit.edgeCount());
        assertEquals(byDefault.getNextStateNr(), explicit.getNextStateNr());
    }

    /**
     * Asserts that a GTS is a trace tree with consistent bookkeeping: every
     * state except the start state has its parent in the GTS, there is
     * exactly one (spanning) transition per non-root state, and the flagged
     * state lists agree with the counts (their internal assertion fires on
     * inconsistency).
     */
    private void assertTraceTree(GTS gts, String config) {
        for (GraphState state : gts.nodeSet()) {
            if (state instanceof GraphNextState next) {
                assertTrue(gts.nodeSet().contains(next.source()),
                           "Config '%s': parent of retained state %s should be retained"
                               .formatted(config, state));
            } else {
                assertEquals(gts.startState(), state,
                             "Config '%s': only root should be the start state"
                                 .formatted(config));
            }
        }
        assertEquals(gts.nodeCount() - 1, gts.edgeCount(),
                     "Config '%s': a trace tree has one transition per non-root state"
                         .formatted(config));
        // exercise the count-vs-list consistency assertions
        assertTrue(gts.getFinalStates().size() >= 0);
        assertTrue(gts.getErrorStates().size() >= 0);
        assertTrue(gts.getOpenStateCount() >= 0,
                   "Config '%s': open state count should not go negative".formatted(config));
    }

    /** Explores a fresh GTS with a given configuration, under a fixed
     * master seed (relevant for the random orders). */
    private Exploration explore(Grammar grammar, String config) throws Exception {
        Randomness.setMasterSeed(42);
        GTS gts = new GTS(grammar);
        return ExploreTypeConverter
            .toExploreType(ExploreConfig.parse(config))
            .newExploration(gts, null)
            .play();
    }

    /** Loads a sample grammar by name. */
    private Grammar loadGrammar(String name) throws Exception {
        return Groove.loadGrammar(INPUT_DIR + "/" + name).toGrammar();
    }
}
