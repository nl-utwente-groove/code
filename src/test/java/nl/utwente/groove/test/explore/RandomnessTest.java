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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.ClassRule;
import org.junit.Test;

import nl.utwente.groove.explore.ExploreType;
import nl.utwente.groove.explore.config.ConfiguredExploreType;
import nl.utwente.groove.explore.config.ExploreConfig;
import nl.utwente.groove.explore.config.ExploreTypeConverter;
import nl.utwente.groove.explore.engine.FrontierStrategy;
import nl.utwente.groove.explore.util.RandomChooserInSequence;
import nl.utwente.groove.grammar.Grammar;
import nl.utwente.groove.test.MasterSeedGuard;
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
    /** Restores the master-seed state that the tests in this class modify. */
    @ClassRule
    public static final MasterSeedGuard SEED_GUARD = new MasterSeedGuard();

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
     * Tests the snapshot-restore cycle underlying {@link MasterSeedGuard}:
     * the peeked state comes back exactly, including the unresolved state.
     */
    @Test
    public void testPeekRestore() {
        var saved = Randomness.peekMasterSeed();
        Randomness.setMasterSeed(4711);
        assertEquals(Long.valueOf(4711), Randomness.peekMasterSeed());
        Randomness.restoreMasterSeed(null);
        assertNull(Randomness.peekMasterSeed());
        Randomness.restoreMasterSeed(saved);
        assertEquals(saved, Randomness.peekMasterSeed());
    }

    /**
     * Tests that random configurations instantiate the engine path, and that
     * a fixed master seed makes their exploration reproducible: the runs
     * must add the same transitions and close the same states in the same
     * order (compared through the exploration trace of the outcome).
     */
    @Test
    public void testSeededDeterminism() throws Exception {
        Grammar grammar = loadGrammar();
        for (String text : RANDOM_CONFIGS) {
            ExploreType type = ExploreTypeConverter.toExploreType(ExploreConfig.parse(text));
            assertInstanceOf(ConfiguredExploreType.class, type,
                             "Config '%s' should instantiate the engine path".formatted(text));
            Randomness.setMasterSeed(42);
            ExploreOutcome first = ExploreOutcome.explore(grammar, type);
            Randomness.setMasterSeed(42);
            ExploreOutcome second = ExploreOutcome.explore(grammar, type);
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
        ExploreOutcome randomOutcome = ExploreOutcome.explore(grammar, random);
        ExploreType bfs = ExploreTypeConverter.toExploreType(ExploreConfig.parse(""));
        ExploreOutcome bfsOutcome = ExploreOutcome.explore(grammar, bfs);
        // deliberately order-blind: only the covered state space must agree
        assertEquals(bfsOutcome.states(), randomOutcome.states(),
                     "Random frontier order should explore the full state space");
        assertEquals(bfsOutcome.transitions(), randomOutcome.transitions(),
                     "Random frontier order should find all transitions");
    }

    /**
     * Tests that successive choosers sharing one generator make varying
     * choices. Regression test: the chooser used to seed a generator of its
     * own at construction, so every chooser — and the LTL strategies
     * construct one per successor choice — replayed the same drawings,
     * making the choice a fixed function of the number of candidates.
     */
    @Test
    public void testChooserDrawsFreshValues() {
        Randomness.setMasterSeed(42);
        Random rgen = Randomness.newRandom(Purpose.EXPLORATION);
        Set<Integer> picks = new HashSet<>();
        for (int i = 0; i < 20; i++) {
            var chooser = new RandomChooserInSequence<Integer>(rgen);
            chooser.show(0);
            chooser.show(1);
            picks.add(chooser.pickRandom());
        }
        assertEquals(Set.of(0, 1), picks,
                     "Twenty two-candidate choices from one generator should not all coincide");
    }

    /** Loads the ferryman grammar. */
    private Grammar loadGrammar() throws Exception {
        return Groove.loadGrammar(INPUT_DIR + "/ferryman").toGrammar();
    }
}
