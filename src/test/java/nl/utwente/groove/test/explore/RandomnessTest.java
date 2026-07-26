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

import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import nl.utwente.groove.explore.Exploration;
import nl.utwente.groove.explore.ExploreType;
import nl.utwente.groove.explore.config.ConfiguredExploreType;
import nl.utwente.groove.explore.config.ExploreConfig;
import nl.utwente.groove.explore.config.ExploreTypeConverter;
import nl.utwente.groove.explore.engine.FrontierStrategy;
import nl.utwente.groove.grammar.Grammar;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.util.Groove;
import nl.utwente.groove.util.Randomness;
import nl.utwente.groove.util.Randomness.Purpose;

/**
 * Tests for seedable randomness: the derivation of per-purpose streams from
 * the master seed, and the reproducibility of random explorations under a
 * fixed master seed.
 * @author Arend Rensink
 * @version $Revision$
 */
public class RandomnessTest {
    /** Location of the sample grammars. */
    static private final String INPUT_DIR = "junit/samples";

    /** The randomised configurations whose determinism is tested. */
    static private final String[] RANDOM_CONFIGS
        = {"next=random", "frontier=single successor=single-random",};

    /**
     * Tests the stream derivation: the same master seed and purpose always
     * give the same sequence; different purposes or master seeds give
     * different sequences.
     */
    @Test
    public void testStreamDerivation() {
        Randomness.setMasterSeed(42);
        Random first = Randomness.newRandom(Purpose.EXPLORATION);
        Random second = Randomness.newRandom(Purpose.EXPLORATION);
        for (int i = 0; i < 20; i++) {
            assertEquals(first.nextLong(), second.nextLong(),
                         "Same purpose and master seed should give the same stream");
        }
        Random exploration = Randomness.newRandom(Purpose.EXPLORATION);
        Random oracle = Randomness.newRandom(Purpose.ORACLE);
        boolean differ = false;
        for (int i = 0; i < 20; i++) {
            differ |= exploration.nextLong() != oracle.nextLong();
        }
        assertTrue(differ, "Different purposes should give different streams");
        Randomness.setMasterSeed(43);
        Random other = Randomness.newRandom(Purpose.EXPLORATION);
        Randomness.setMasterSeed(42);
        Random original = Randomness.newRandom(Purpose.EXPLORATION);
        differ = false;
        for (int i = 0; i < 20; i++) {
            differ |= other.nextLong() != original.nextLong();
        }
        assertTrue(differ, "Different master seeds should give different streams");
    }

    /**
     * Tests that random configurations instantiate the engine path, and that
     * a fixed master seed makes their exploration reproducible, including
     * the (order-dependent) state numbering of the result states.
     */
    @Test
    public void testSeededDeterminism() throws Exception {
        Grammar grammar = loadGrammar();
        for (String text : RANDOM_CONFIGS) {
            ExploreType type = ExploreTypeConverter.toExploreType(ExploreConfig.parse(text));
            assertInstanceOf(ConfiguredExploreType.class, type,
                             "Config '%s' should instantiate the engine path".formatted(text));
            Randomness.setMasterSeed(42);
            Outcome first = explore(grammar, type);
            Randomness.setMasterSeed(42);
            Outcome second = explore(grammar, type);
            assertEquals(first, second,
                         "Same master seed should reproduce config '%s' exactly".formatted(text));
        }
    }

    /**
     * Tests that the random frontier order explores the same state space as
     * the default (breadth-first) order, only in a different order.
     */
    @Test
    public void testRandomFrontierCoverage() throws Exception {
        Randomness.setMasterSeed(42);
        Grammar grammar = loadGrammar();
        ExploreType random = ExploreTypeConverter.toExploreType(ExploreConfig.parse("next=random"));
        assertInstanceOf(FrontierStrategy.class, random.getParsedStrategy(grammar));
        Outcome randomOutcome = explore(grammar, random);
        ExploreType bfs = ExploreTypeConverter.toExploreType(ExploreConfig.parse(""));
        Outcome bfsOutcome = explore(grammar, bfs);
        assertEquals(bfsOutcome.states(), randomOutcome.states(),
                     "Random frontier order should explore the full state space");
        assertEquals(bfsOutcome.transitions(), randomOutcome.transitions(),
                     "Random frontier order should find all transitions");
    }

    /** Explores a fresh GTS with a given exploration type and summarises it. */
    private Outcome explore(Grammar grammar, ExploreType type) throws Exception {
        GTS gts = new GTS(grammar);
        Exploration exploration = type.newExploration(gts, null).play();
        Set<Integer> resultStates = exploration
            .getResult()
            .getStates()
            .stream()
            .map(s -> s.getNumber())
            .collect(Collectors.toSet());
        return new Outcome(gts.nodeCount(), gts.edgeCount(), resultStates);
    }

    /** Summary of an exploration, for comparison across runs. */
    private record Outcome(int states, int transitions, Set<Integer> resultStates) {
        // record body intentionally empty
    }

    /** Loads the ferryman grammar. */
    private Grammar loadGrammar() throws Exception {
        return Groove.loadGrammar(INPUT_DIR + "/ferryman").toGrammar();
    }
}
