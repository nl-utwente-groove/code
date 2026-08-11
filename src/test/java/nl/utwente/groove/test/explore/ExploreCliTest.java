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

import org.junit.Test;

import nl.utwente.groove.explore.Generator;
import nl.utwente.groove.lts.Filter;
import nl.utwente.groove.util.cli.CmdLineException;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Tests for the Generator's exploration configuration option: exploring with
 * a configuration, the mutual exclusion with the deprecated strategy and
 * acceptor options, and the effect of the result shape on the save filter.
 * @author Arend Rensink
 * @version $Revision$
 */
public class ExploreCliTest {
    /** Location of the sample grammar used for the tests. */
    static private final String GRAMMAR = "junit/samples/ferryman.gps";
    /** Location of the tree-shaped sample grammar used for the depth-bound test. */
    static private final String COUNTING = "junit/samples/counting.gps";

    /** The full state count of the ferryman grammar. */
    static private final int FERRYMAN_STATES = 114;

    /** Tests exploration through the configuration option. */
    @Test
    public void testExploreOption() throws Exception {
        var full = Generator.execute("-x", "", GRAMMAR);
        assertEquals(FERRYMAN_STATES, full.getGTS().nodeCount());
        // the default configuration explores the same state space as the default type
        var dfs = Generator.execute("-x", "next=newest", GRAMMAR);
        assertEquals(FERRYMAN_STATES, dfs.getGTS().nodeCount());
        // a linear exploration visits a single trace; its length is pinned
        // by the deterministic exploration order
        var linear = Generator.execute("-x", "frontier=single successor=single", GRAMMAR);
        assertEquals(7, linear.getGTS().nodeCount());
        // stopping at the first result cuts the exploration short; the state
        // count is pinned by the deterministic exploration order (a goal-less
        // count=first would not bite, as ferryman has no final states)
        var first = Generator.execute("-x", "goal=condition:eat count=first", GRAMMAR);
        assertEquals(5, first.getGTS().nodeCount());
    }

    /**
     * Tests that the depth bound means the same across the frontier orders:
     * on a tree-shaped grammar, where the discovery depth of a state equals
     * the depth of its unique path, a depth-bounded breadth-first and
     * depth-first exploration cover exactly the same states. Regression
     * test: the pools used to inherit an off-by-one divergence from the
     * legacy strategies (BFS explored one level less than DFS for the same
     * bound value).
     */
    @Test
    public void testDepthBoundUniform() throws Exception {
        var full = Generator.execute("-x", "", COUNTING).getGTS().nodeCount();
        for (int bound : new int[] {1, 2}) {
            var bfs = Generator
                .execute("-x", "cost=uniform bound=cost:" + bound, COUNTING)
                .getGTS()
                .nodeCount();
            var dfs = Generator
                .execute("-x", "next=newest cost=uniform bound=cost:" + bound, COUNTING)
                .getGTS()
                .nodeCount();
            assertEquals(bfs, dfs,
                         "Depth bound %d should mean the same under both orders"
                             .formatted(bound));
            assertTrue(bfs < full, "Depth bound %d should restrict the exploration"
                .formatted(bound));
        }
    }

    /**
     * Tests that a trace-shaped exploration ({@code shape=trace}) switches
     * the save filter to the result traces, unless an explicit filter option
     * overrides it.
     */
    @Test
    public void testTraceShapeFilter() throws Exception {
        var traced = new Generator("-x", "shape=trace goal=condition:eat count=first", GRAMMAR);
        traced.start();
        assertEquals(Filter.RESULT, traced.getFilter());
        var plain = new Generator("-x", "goal=condition:eat count=first", GRAMMAR);
        plain.start();
        assertEquals(Filter.NONE, plain.getFilter());
        var spanning = new Generator("-spanning", "-x",
            "shape=trace goal=condition:eat count=first", GRAMMAR);
        spanning.start();
        assertEquals(Filter.SPANNING, spanning.getFilter());
    }

    /** Tests that the configuration option rejects unrealisable values. */
    @Test
    public void testBadConfig() {
        assertThrows(FormatException.class,
                     () -> Generator.execute("-x", "heuristic=nen", GRAMMAR));
        assertThrows(FormatException.class, () -> Generator.execute("-x", "bogus=1", GRAMMAR));
    }

    /** Tests the mutual exclusion of the configuration and legacy options.
     * (Argument parsing only happens when the generator is started.) */
    @Test
    public void testOptionConflicts() {
        assertThrows(CmdLineException.class,
                     () -> Generator.execute("-x", "next=newest", "-s", "bfs", GRAMMAR));
        assertThrows(CmdLineException.class,
                     () -> Generator.execute("-x", "next=newest", "-a", "final", GRAMMAR));
        assertThrows(CmdLineException.class,
                     () -> Generator.execute("-x", "next=newest", "-r", "2", GRAMMAR));
    }

    /** Tests that the legacy options behave as their configuration equivalents. */
    @Test
    public void testLegacyOptions() throws Exception {
        var legacy = Generator.execute("-s", "dfs", "-a", "final", GRAMMAR);
        var config = Generator.execute("-x", "next=newest", GRAMMAR);
        assertEquals(config.getGTS().nodeCount(), legacy.getGTS().nodeCount());
        var legacyLinear = Generator.execute("-s", "linear", GRAMMAR);
        var configLinear
            = Generator.execute("-x", "frontier=single successor=single", GRAMMAR);
        assertEquals(configLinear.getGTS().nodeCount(), legacyLinear.getGTS().nodeCount());
        var legacyCounted = Generator.execute("-s", "dfs", "-a", "inv:eat", "-r", "2", GRAMMAR);
        var configCounted
            = Generator.execute("-x", "next=newest goal=condition:eat count=2", GRAMMAR);
        assertEquals(configCounted.getGTS().nodeCount(), legacyCounted.getGTS().nodeCount());
        // a bare result count behaves as its configuration equivalent
        var legacyFirst = Generator.execute("-r", "1", GRAMMAR);
        var configFirst = Generator.execute("-x", "count=first", GRAMMAR);
        assertEquals(configFirst.getGTS().nodeCount(), legacyFirst.getGTS().nodeCount());
        // a bare result count must also work when the grammar's default
        // exploration is one of the dedicated non-config types, which cannot
        // serve as a base for the legacy overlay (regression: the count used
        // to be routed through the overlay, which then failed)
        var ltlDefault = Generator
            .execute("-D", "explorationStrategy=ltl:false cycle 0", "-r", "1", GRAMMAR);
        assertFalse(ltlDefault.isEmpty());
    }

    /** Tests that malformed legacy options are rejected. */
    @Test
    public void testBadLegacyOptions() {
        assertThrows(FormatException.class, () -> Generator.execute("-s", "bogus", GRAMMAR));
        assertThrows(FormatException.class, () -> Generator.execute("-a", "cycle", GRAMMAR));
        assertThrows(FormatException.class, () -> Generator.execute("-s", "crule", GRAMMAR));
    }
}
