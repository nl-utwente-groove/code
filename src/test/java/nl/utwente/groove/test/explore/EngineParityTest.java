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

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import nl.utwente.groove.explore.Exploration;
import nl.utwente.groove.explore.ExploreType;
import nl.utwente.groove.explore.config.ConfiguredExploreType;
import nl.utwente.groove.explore.config.ExploreConfig;
import nl.utwente.groove.explore.config.ExploreTypeConverter;
import nl.utwente.groove.explore.engine.FrontierStrategy;
import nl.utwente.groove.explore.strategy.LinearStrategy;
import nl.utwente.groove.grammar.Grammar;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.util.Groove;

/**
 * A/B tests for the parametric exploration engine: every expressible
 * configuration is explored both through the new engine (the
 * {@link ConfiguredExploreType} instantiation path) and through the legacy
 * enumerator-instantiated strategy and acceptor classes, and the resulting
 * transition systems must coincide in state count, transition count and
 * result count. The engine must moreover be deterministic: repeated runs of
 * the same configuration must produce identical results.
 * @author Arend Rensink
 * @version $Revision$
 */
public class EngineParityTest {
    /** Location of the sample grammars. */
    static private final String INPUT_DIR = "junit/samples";

    /** The configurations to be run on the ferryman grammar. */
    static private final String[] CONFIGS = {
        // plain search orders
        "",
        "next=newest",
        "frontier=single successor=single",
        // depth bounds
        "cost=uniform bound=cost:3",
        "next=newest cost=uniform bound=cost:3",
        // node, edge and condition bounds
        "bound=nodes:8",
        "bound=upto:load",
        "bound=upto:!load",
        "next=newest bound=upto:load",
        "bound=include:load",
        "next=newest bound=include:!load",
        // goals and result counts
        "goal=none",
        "goal=any count=first",
        "goal=any count=3",
        "goal=condition:load count=first",
        "goal=condition:!load count=first",
        "goal=\"condition:load || eat\" count=first",
        "goal=condition:load outcome=violate count=first",
        "goal=fires:load count=first",};

    /**
     * Runs every configuration through both instantiation paths and compares
     * the outcomes.
     */
    @Test
    public void testParity() throws Exception {
        Grammar grammar = loadGrammar();
        for (String text : CONFIGS) {
            compare(grammar, text);
        }
        // the edge bound needs a label actually occurring in the grammar
        var label = grammar
            .getTypeGraph()
            .getLabels()
            .stream()
            .map(l -> l.text())
            .sorted()
            .findFirst()
            .orElseThrow();
        compare(grammar, "bound=edges:" + label + ">2");
    }

    /**
     * Tests that the configuration path instantiates the new engine classes
     * rather than the legacy strategy subclasses.
     */
    @Test
    public void testEngineInstantiation() throws Exception {
        Grammar grammar = loadGrammar();
        ExploreType search = ExploreTypeConverter.toExploreType(ExploreConfig.parse(""));
        assertInstanceOf(ConfiguredExploreType.class, search);
        assertInstanceOf(FrontierStrategy.class, search.getParsedStrategy(grammar));
        ExploreType conditional = ExploreTypeConverter
            .toExploreType(ExploreConfig.parse("next=newest bound=upto:load"));
        assertInstanceOf(FrontierStrategy.class, conditional.getParsedStrategy(grammar));
        ExploreType linear = ExploreTypeConverter
            .toExploreType(ExploreConfig.parse("frontier=single successor=single"));
        assertInstanceOf(LinearStrategy.class, linear.getParsedStrategy(grammar));
    }

    /**
     * Tests that the randomised single-successor configuration runs on the
     * engine path. Its outcome is not reproducible, so no parity or
     * determinism is asserted, only that a nonempty path is explored.
     */
    @Test
    public void testRandomLinear() throws Exception {
        Grammar grammar = loadGrammar();
        ExploreType type = ExploreTypeConverter
            .toExploreType(ExploreConfig.parse("frontier=single successor=single-random"));
        GTS gts = new GTS(grammar);
        type.newExploration(gts, null).play();
        assertTrue(gts.nodeCount() > 1, "Random linear exploration should explore a path");
    }

    /**
     * Explores a configuration through the engine path and the legacy path
     * and asserts equal outcomes; also asserts that a repeated engine run
     * reproduces the same result exactly.
     */
    private void compare(Grammar grammar, String text) throws Exception {
        ExploreType engineType = ExploreTypeConverter.toExploreType(ExploreConfig.parse(text));
        assertInstanceOf(ConfiguredExploreType.class, engineType,
                         "Config '%s' should instantiate the engine path".formatted(text));
        // the same descriptors, but as a plain exploration type,
        // run through the legacy enumerator machinery
        ExploreType legacyType = new ExploreType(engineType.getStrategy(),
            engineType.getAcceptor(), engineType.getBound());
        Outcome engine = explore(grammar, engineType);
        Outcome legacy = explore(grammar, legacyType);
        assertEquals(legacy, engine, "Engine outcome differs from legacy for config '%s'"
            .formatted(text));
        // determinism: an engine re-run reproduces the outcome exactly
        Outcome repeat = explore(grammar, engineType);
        assertEquals(engine, repeat, "Engine re-run differs for config '%s'".formatted(text));
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

    /** Summary of an exploration, for comparison across engine paths. */
    private record Outcome(int states, int transitions, Set<Integer> resultStates) {
        // record body intentionally empty
    }

    /** Loads the ferryman grammar. */
    private Grammar loadGrammar() throws Exception {
        return Groove.loadGrammar(INPUT_DIR + "/ferryman").toGrammar();
    }
}
