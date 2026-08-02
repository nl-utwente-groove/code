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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.Test;

import nl.utwente.groove.explore.Exploration;
import nl.utwente.groove.explore.ExploreType;
import nl.utwente.groove.explore.config.ConfiguredExploreType;
import nl.utwente.groove.explore.config.ExploreConfig;
import nl.utwente.groove.explore.config.ExploreTypeConverter;
import nl.utwente.groove.grammar.Grammar;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.util.Groove;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Tests for the realisability gate of the exploration feature model:
 * acceptance of the realisable configurations, rejection of inexpressible
 * values, and an end-to-end exploration through a converted configuration.
 * @author Arend Rensink
 * @version $Revision$
 */
public class ExploreTypeConverterTest {
    /** Location of the sample grammars. */
    static private final String INPUT_DIR = "junit/samples";

    /**
     * Tests that the realisable configurations are accepted, yielding a
     * configuration-based type that carries them unchanged.
     */
    @Test
    public void testAcceptedConfigs() throws FormatException {
        String[] configs = {
            "",
            "next=newest",
            "frontier=single successor=single",
            "frontier=single successor=single-random",
            "next=random",
            "frontier=beam:5",
            "next=newest frontier=beam:2",
            "next=random frontier=beam:4",
            "frontier=beam:3 goal=condition:load count=first",
            "cost=uniform bound=cost:5",
            "next=newest cost=uniform bound=cost:5",
            "bound=nodes:20",
            "bound=edges:a>2,b>3",
            "bound=upto:load",
            "bound=upto:!load",
            "next=newest bound=upto:load",
            "bound=include:load",
            "next=newest bound=include:!load count=first",
            "goal=none",
            "goal=any",
            "goal=any count=first",
            "goal=condition:load",
            "goal=condition:!load",
            "goal=\"condition:load || eat\"",
            "goal=condition:load count=first",
            "goal=condition:load outcome=violate",
            "goal=fires:load",
            "goal=fires:load count=3",
            "count=3",
            "persistence=none",
            "persistence=none next=random",
            "persistence=none frontier=beam:3",
            "persistence=none cost=uniform bound=cost:5",
            "shape=trace",
            "shape=trace goal=condition:load count=first",
            "persistence=none shape=trace",
            "collapse=equality",
            "collapse=isomorphism",
            "algebra=point",
            "algebra=big",
            "collapse=equality algebra=point persistence=none",};
        for (String text : configs) {
            ExploreConfig config = ExploreConfig.parse(text);
            ExploreType type = ExploreTypeConverter.toExploreType(config);
            assertInstanceOf(ConfiguredExploreType.class, type,
                             "Config '%s' should be accepted".formatted(text));
            assertEquals(config, ((ConfiguredExploreType) type).getConfig());
        }
    }

    /** Tests that inexpressible configurations are rejected with an error. */
    @Test
    public void testInexpressibleConfig() {
        String[] configs = {
            "frontier=beam:5 cost=uniform bound=cost:3",
            "next=newest frontier=beam:5 successor=single",
            "next=random cost=uniform bound=cost:3",
            "successor=all-random",
            "successor=single",
            "heuristic=nen",
            "cost=rule",
            "shape=trace goal=none",
            "collapse=hash",
            "goal=graph:someGraph",
            "goal=ltl:someProp",
            "goal=fires:load outcome=violate",
            "goal=any outcome=violate",
            "bound=size:100",
            "cost=uniform bound=cost:10+5",
            "frontier=single successor=single cost=uniform bound=cost:5",
            "bound=nodes:20+5",
            "next=newest bound=nodes:20",
            "next=newest bound=edges:a>2",
            "frontier=single successor=single bound=upto:load",};
        for (String text : configs) {
            assertThrows(FormatException.class, () -> ExploreTypeConverter
                .toExploreType(ExploreConfig.parse(text)), "Config '%s' should be rejected"
                    .formatted(text));
        }
    }

    /**
     * Tests that a converted configuration actually explores, that a
     * violated outcome behaves as the negated condition, and that
     * grammar-dependent goal content validates against a real grammar.
     */
    @Test
    public void testExploration() throws Exception {
        GrammarModel grammarModel = Groove.loadGrammar(INPUT_DIR + "/ferryman");
        Grammar grammar = grammarModel.toGrammar();
        // explore with the default configuration
        GTS gts = new GTS(grammar);
        ExploreType.DEFAULT.newExploration(gts, null).play();
        int fullCount = gts.nodeCount();
        // a violated outcome behaves exactly as the negated condition
        var satisfied = explore(grammar, "goal=condition:!eat outcome=violate count=first");
        var violated = explore(grammar, "goal=condition:eat count=first");
        assertFalse(satisfied.getResult().isEmpty());
        assertEquals(violated.getResult().getStates().size(),
                     satisfied.getResult().getStates().size());
        assertEquals(violated.getGTS().nodeCount(), satisfied.getGTS().nodeCount());
        assertFalse(satisfied.getGTS().nodeCount() > fullCount);
        // a goal referring to an actual rule of the grammar validates
        ExploreTypeConverter
            .toExploreType(ExploreConfig.parse("goal=condition:load count=first"))
            .test(grammar);
        // so do compound conditions
        ExploreTypeConverter
            .toExploreType(ExploreConfig.parse("goal=\"condition:load || eat\""))
            .test(grammar);
        // and an edge bound over a label actually occurring in the grammar
        var label = grammar
            .getTypeGraph()
            .getLabels()
            .stream()
            .map(l -> l.text())
            .sorted()
            .findFirst()
            .orElseThrow();
        ExploreTypeConverter
            .toExploreType(ExploreConfig.parse("bound=edges:" + label + ">2"))
            .test(grammar);
    }

    /** Explores the ferryman grammar with a given configuration. */
    private Exploration explore(Grammar grammar, String config) throws Exception {
        GTS gts = new GTS(grammar);
        return ExploreTypeConverter
            .toExploreType(ExploreConfig.parse(config))
            .newExploration(gts, null)
            .play();
    }
}
