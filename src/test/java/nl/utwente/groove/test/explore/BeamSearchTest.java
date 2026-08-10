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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;

import nl.utwente.groove.explore.ExploreType;
import nl.utwente.groove.explore.config.ConfiguredExploreType;
import nl.utwente.groove.explore.config.ExploreConfig;
import nl.utwente.groove.explore.config.ExploreTypeConverter;
import nl.utwente.groove.explore.engine.FrontierStrategy;
import nl.utwente.groove.grammar.Grammar;
import nl.utwente.groove.util.Groove;
import nl.utwente.groove.util.Randomness;

/**
 * Tests for beam search ({@code frontier=beam}): an unrestricted beam must
 * coincide exactly with the corresponding plain frontier pool, a restrictive
 * beam must actually cut off part of the state space, and both must be
 * deterministic (under a fixed master seed, for the random order).
 * @author Arend Rensink
 * @version $Revision$
 */
public class BeamSearchTest {
    /** Location of the sample grammars. */
    static private final String INPUT_DIR = "junit/samples";

    /** A beam capacity that the ferryman frontier never reaches. */
    static private final int HUGE = 100000;

    /**
     * Tests that a beam too large to ever overflow behaves identically to the
     * plain pool with the same exploration order — bit-identical, including
     * the state numbering and (for the random order) the seeded random draws.
     */
    @Test
    public void testUnrestrictedBeamEquivalence() throws Exception {
        Grammar grammar = loadGrammar();
        String[][] pairs = {
            {"", "frontier=beam:" + HUGE},
            {"next=newest", "next=newest frontier=beam:" + HUGE},
            {"next=random", "next=random frontier=beam:" + HUGE},};
        for (String[] pair : pairs) {
            Randomness.setMasterSeed(42);
            ExploreOutcome plain = explore(grammar, pair[0]);
            Randomness.setMasterSeed(42);
            ExploreOutcome beam = explore(grammar, pair[1]);
            // outcome equality covers the exploration trace, so this asserts
            // that both runs added the same transitions and closed the same
            // states in the same order, not just that the final GTSs agree
            assertEquals(plain, beam,
                         "Unrestricted beam '%s' should coincide with '%s'"
                             .formatted(pair[1], pair[0]));
        }
    }

    /**
     * Tests that a restrictive beam explores strictly less than the full
     * state space, and reproducibly so (under a fixed master seed, for the
     * random order).
     */
    @Test
    public void testRestrictedBeam() throws Exception {
        Grammar grammar = loadGrammar();
        int full = explore(grammar, "").states();
        String[] configs
            = {"frontier=beam:2", "next=newest frontier=beam:2", "next=random frontier=beam:2",};
        for (String text : configs) {
            Randomness.setMasterSeed(42);
            ExploreOutcome first = explore(grammar, text);
            assertTrue(first.states() > 1, "Beam search '%s' should explore beyond the start state"
                .formatted(text));
            assertTrue(first.states() < full,
                       "Beam search '%s' should cut off part of the state space (explored %s of %s)"
                           .formatted(text, first.states(), full));
            Randomness.setMasterSeed(42);
            ExploreOutcome second = explore(grammar, text);
            // trace-bearing equality: the runs must explore identically
            assertEquals(first, second,
                         "Beam search '%s' should be reproducible".formatted(text));
        }
    }

    /**
     * Tests that beam configurations instantiate the parametric engine.
     */
    @Test
    public void testEngineInstantiation() throws Exception {
        Grammar grammar = loadGrammar();
        ExploreType type = ExploreTypeConverter
            .toExploreType(ExploreConfig.parse("next=newest frontier=beam:3"));
        assertInstanceOf(ConfiguredExploreType.class, type);
        assertInstanceOf(FrontierStrategy.class, type.getParsedStrategy(grammar));
    }

    /** Explores a fresh GTS with a given configuration and summarises it. */
    private ExploreOutcome explore(Grammar grammar, String config) throws Exception {
        ExploreType type = ExploreTypeConverter.toExploreType(ExploreConfig.parse(config));
        return ExploreOutcome.explore(grammar, type);
    }

    /** Loads the ferryman grammar. */
    private Grammar loadGrammar() throws Exception {
        return Groove.loadGrammar(INPUT_DIR + "/ferryman").toGrammar();
    }
}
