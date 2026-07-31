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
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;

import nl.utwente.groove.explore.Exploration;
import nl.utwente.groove.explore.config.ExploreConfig;
import nl.utwente.groove.explore.config.ExploreTypeConverter;
import nl.utwente.groove.grammar.Grammar;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.util.Groove;
import nl.utwente.groove.util.Randomness;

/**
 * Tests the persistence feature: under {@code persistence=none}, discovered
 * states and transitions are not stored in the GTS. Every discovered state
 * then counts as fresh (state collapse is inoperative), so the exploration
 * traverses the tree unfolding of the state space; the GTS retains only the
 * start state, while acceptors still see the full exploration and pin their
 * result states.
 * <p>
 * Because there is no revisit detection, unpersisted exploration only
 * terminates if the traversed tree is finite: the ferryman fixture (cyclic)
 * is explored under a depth bound, and the fibonacci fixture (whose
 * individual computation paths all terminate, though its tree unfolding is
 * exponentially wide) under a restrictive beam.
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
     * Tests that without persistence, the GTS retains only the start state
     * while the exploration itself proceeds: states are discovered beyond
     * the retained one, under all next-state orders.
     */
    @Test
    public void testNoneRetainsOnlyStart() throws Exception {
        Grammar grammar = loadGrammar("ferryman");
        // the random order cannot carry a depth bound, so it is tested on the
        // tree-shaped counting grammar instead (see testNoneOnTreeGrammar)
        for (String config : new String[] {"persistence=none " + BOUND,
            "persistence=none next=newest " + BOUND}) {
            GTS gts = explore(grammar, config).getGTS();
            assertEquals(1, gts.nodeCount(),
                         "Config '%s' should retain only the start state".formatted(config));
            assertEquals(0, gts.edgeCount(),
                         "Config '%s' should retain no transitions".formatted(config));
            assertTrue(gts.startState().isClosed(),
                       "Config '%s' should still have explored the start state"
                           .formatted(config));
            assertTrue(gts.getNextStateNr() > 1,
                       "Config '%s' should have discovered states beyond the start state"
                           .formatted(config));
        }
    }

    /**
     * Tests that acceptors still see the unpersisted exploration: a goal
     * reachable within the depth bound is found and its witness state is
     * pinned in the result, even though the GTS has forgotten it.
     */
    @Test
    public void testNoneCollectsResults() throws Exception {
        Grammar grammar = loadGrammar("ferryman");
        Exploration exploration
            = explore(grammar, "persistence=none goal=condition:eat " + BOUND);
        assertFalse(exploration.getResult().isEmpty(),
                    "The goal state should be collected although it is not retained");
        assertEquals(1, exploration.getGTS().nodeCount());
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
        assertTrue(first.getNextStateNr() >= allGts.nodeCount(),
                   "The tree unfolding cannot be smaller than the collapsed state space");
    }

    /**
     * Tests that persistence composes with the random order and with a
     * restrictive beam: dropped states simply vanish, and the exploration
     * terminates with only the start state retained. The counting grammar is
     * tree-shaped (its collapsed state space has one transition less than it
     * has states), so its unfolding equals the collapsed space, no depth
     * bound is needed for termination, and the beam can at most shrink the
     * discovery count.
     */
    @Test
    public void testNoneOnTreeGrammar() throws Exception {
        Grammar grammar = loadGrammar("counting");
        GTS allGts = explore(grammar, "").getGTS();
        for (String config : new String[] {"persistence=none next=random",
            "persistence=none frontier=beam:2",
            "persistence=none next=random frontier=beam:2"}) {
            GTS gts = explore(grammar, config).getGTS();
            assertEquals(1, gts.nodeCount(),
                         "Config '%s' should retain only the start state".formatted(config));
            assertTrue(gts.getNextStateNr() > 1,
                       "Config '%s' should have discovered states beyond the start state"
                           .formatted(config));
            assertTrue(gts.getNextStateNr() <= allGts.nodeCount(),
                       "Config '%s' should not discover more than the tree-shaped state space"
                           .formatted(config));
        }
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
