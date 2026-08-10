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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.junit.ClassRule;
import org.junit.Test;

import nl.utwente.groove.explore.config.ExploreConfig;
import nl.utwente.groove.explore.config.ExploreTypeConverter;
import nl.utwente.groove.explore.engine.BeamPool;
import nl.utwente.groove.explore.engine.BeamPool.Order;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.test.MasterSeedGuard;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.Randomness;

/**
 * Direct unit tests for {@link BeamPool}: the capacity invariant, the
 * per-order drop rule on overflowing {@link BeamPool#add}, and the exemption
 * of the re-added state from the drop on overflowing {@link BeamPool#readd}.
 * The pool treats states as opaque tokens, so the tests drive it directly
 * with states harvested from one ferryman exploration, without going through
 * an exploration.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
public class BeamPoolTest {
    /** Restores the master-seed state that the tests in this class modify. */
    @ClassRule
    public static final MasterSeedGuard SEED_GUARD = new MasterSeedGuard();

    /** States serving as opaque tokens for the pool, harvested lazily. */
    static private List<GraphState> tokens;

    /** Returns at least five distinct states to drive the pool with. */
    static private List<GraphState> tokens() throws Exception {
        var result = tokens;
        if (result == null) {
            GTS gts = new GTS(ExploreTestSupport.loadGrammar("ferryman"));
            ExploreTypeConverter
                .toExploreType(ExploreConfig.parse(""))
                .newExploration(gts, null)
                .play();
            result = new ArrayList<>(gts.nodeSet());
            assertTrue(result.size() >= 5, "Ferryman should yield at least five states");
            tokens = result;
        }
        return result;
    }

    /** Takes from a pool until it is empty and returns the taken sequence. */
    private List<GraphState> drain(BeamPool pool) {
        List<GraphState> result = new ArrayList<>();
        GraphState next;
        while ((next = pool.take()) != null) {
            result.add(next);
        }
        return result;
    }

    /** Tests that taking from an empty pool signals exhaustion. */
    @Test
    public void testTakeOnEmpty() {
        assertNull(new BeamPool(Order.OLDEST, 1).take());
    }

    /**
     * Tests the drop rule under the oldest-first (breadth-first) order: an
     * overflowing add drops the take-order-last state, which is the incoming
     * one, so the pool retains the oldest states.
     */
    @Test
    public void testOldestOrderDrop() throws Exception {
        var s = tokens();
        BeamPool pool = new BeamPool(Order.OLDEST, 3);
        for (int i = 0; i < 5; i++) {
            pool.add(s.get(i));
        }
        assertEquals(List.of(s.get(0), s.get(1), s.get(2)), drain(pool),
                     "Oldest order should retain the oldest states and take them in age order");
    }

    /**
     * Tests the drop rule under the newest-first (depth-first) order: an
     * overflowing add drops the take-order-last state, which is the oldest
     * one, so the pool retains the newest states.
     */
    @Test
    public void testNewestOrderDrop() throws Exception {
        var s = tokens();
        BeamPool pool = new BeamPool(Order.NEWEST, 3);
        for (int i = 0; i < 5; i++) {
            pool.add(s.get(i));
        }
        assertEquals(List.of(s.get(4), s.get(3), s.get(2)), drain(pool),
                     "Newest order should retain the newest states and take them newest first");
    }

    /**
     * Tests the random order: the capacity invariant holds, the retained
     * states are distinct states from among those added, and the drops and
     * takes are reproducible under a fixed master seed.
     */
    @Test
    public void testRandomOrderDrop() throws Exception {
        var s = tokens();
        Randomness.setMasterSeed(42);
        BeamPool pool = new BeamPool(Order.RANDOM, 3);
        for (int i = 0; i < 5; i++) {
            pool.add(s.get(i));
        }
        List<GraphState> first = drain(pool);
        assertEquals(3, first.size(), "Random order should respect the capacity");
        assertEquals(3, first.stream().distinct().count(),
                     "Random order should retain distinct states");
        assertTrue(s.subList(0, 5).containsAll(first),
                   "Random order should retain only states that were added");
        Randomness.setMasterSeed(42);
        BeamPool second = new BeamPool(Order.RANDOM, 3);
        for (int i = 0; i < 5; i++) {
            second.add(s.get(i));
        }
        assertEquals(first, drain(second),
                     "Same master seed should reproduce drops and takes exactly");
    }

    /**
     * Tests the normal readd path: after a take, the slot is free and the
     * re-added state is the next to be taken again.
     */
    @Test
    public void testReaddResumesFirst() throws Exception {
        var s = tokens();
        for (Order order : new Order[] {Order.OLDEST, Order.NEWEST}) {
            BeamPool pool = new BeamPool(order, 3);
            for (int i = 0; i < 3; i++) {
                pool.add(s.get(i));
            }
            GraphState taken = Objects.requireNonNull(pool.take(), "the pool was just filled");
            pool.readd(taken);
            assertEquals(taken, pool.take(),
                         "Under order %s, a re-added state should be taken next".formatted(order));
        }
    }

    /**
     * Tests that an overflowing readd never drops the re-added state itself:
     * it is partially explored, and the contract of readd is that the state
     * reaches exploration again. (Regression test: under the random order,
     * the trim used to draw its victim from the whole pool, the re-added
     * state included.)
     */
    @Test
    public void testReaddExemptFromDrop() throws Exception {
        var s = tokens();
        Randomness.setMasterSeed(42);
        for (Order order : Order.values()) {
            // repeat to give a faulty random drop many chances to hit the
            // re-added state
            for (int round = 0; round < 20; round++) {
                BeamPool pool = new BeamPool(order, 3);
                for (int i = 0; i < 3; i++) {
                    pool.add(s.get(i));
                }
                // overflowing readd: no preceding take has freed a slot
                pool.readd(s.get(3));
                List<GraphState> retained = drain(pool);
                assertEquals(3, retained.size(),
                             "Under order %s, readd should respect the capacity".formatted(order));
                assertTrue(retained.contains(s.get(3)),
                           "Under order %s, the re-added state should survive the drop"
                               .formatted(order));
            }
        }
    }
}
