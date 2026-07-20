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
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.Test;

import nl.utwente.groove.explore.ExploreType;
import nl.utwente.groove.explore.config.ExploreConfig;
import nl.utwente.groove.explore.config.ExploreTypeConverter;
import nl.utwente.groove.explore.encode.Serialized;
import nl.utwente.groove.grammar.Grammar;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.util.Groove;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Tests for the bridge between the exploration feature model and the legacy
 * strategy/acceptor machinery: round trips in both directions, rejection of
 * inexpressible values, and an end-to-end exploration through both paths.
 * @author Arend Rensink
 * @version $Revision$
 */
public class ExploreTypeConverterTest {
    /** Location of the sample grammars. */
    static private final String INPUT_DIR = "junit/samples";

    /**
     * Tests that expressible configurations survive the round trip through
     * the legacy exploration type.
     */
    @Test
    public void testConfigRoundTrip() throws FormatException {
        String[] configs = {
            "",
            "next=newest",
            "frontier=single successor=single",
            "frontier=single successor=single-random",
            "matcher=rete next=newest",
            "matcher=rete frontier=single successor=single",
            "matcher=rete frontier=single successor=single-random",
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
            "goal=rule:load",
            "goal=rule:load outcome=violate",
            "goal=applied:load",
            "goal=applied:load count=3",
            "goal=formula:load count=first",
            "count=3",};
        for (String text : configs) {
            ExploreConfig config = ExploreConfig.parse(text);
            ExploreType type = ExploreTypeConverter.toExploreType(config);
            assertEquals(config, ExploreTypeConverter.toConfig(type),
                         "Round trip failed for config '%s' via '%s'"
                             .formatted(text, type.getIdentifier()));
        }
    }

    /**
     * Tests that expressible legacy exploration types survive the round trip
     * through the feature model.
     */
    @Test
    public void testLegacyRoundTrip() throws FormatException {
        String[] strategies = {"bfs", "dfs", "linear", "random", "rete", "retelinear",
            "reterandom"};
        String[] acceptors = {"final", "none", "any", "inv", "ruleapp", "formula"};
        int[] bounds = {0, 1, 3};
        for (String strategy : strategies) {
            for (String acceptor : acceptors) {
                for (int bound : bounds) {
                    if ("none".equals(acceptor) && bound > 0) {
                        // a result bound without a goal is inconsistent in the
                        // feature model, so this combination does not round-trip
                        continue;
                    }
                    ExploreType type = new ExploreType(createStrategy(strategy),
                        createAcceptor(acceptor), bound);
                    ExploreType back
                        = ExploreTypeConverter.toExploreType(ExploreTypeConverter.toConfig(type));
                    assertEquals(type.getIdentifier(), back.getIdentifier());
                }
            }
        }
        // a bfs/dfs depth bound also survives the round trip
        for (String strategy : new String[] {"bfs", "dfs"}) {
            Serialized bounded = createStrategy(strategy);
            bounded.setArgument("bound", "7");
            assertLegacyRoundTrip(new ExploreType(bounded, createAcceptor("final"), 0));
        }
        // so do the conditional strategies
        Serialized cnbound = new Serialized("cnbound");
        cnbound.setArgument("node-bound", "20");
        assertLegacyRoundTrip(new ExploreType(cnbound, createAcceptor("final"), 0));
        Serialized cebound = new Serialized("cebound");
        cebound.setArgument("edge-bound", "a>2,b>3");
        assertLegacyRoundTrip(new ExploreType(cebound, createAcceptor("final"), 0));
        for (String search : new String[] {"bfs", "dfs"}) {
            for (String stop : new String[] {"->", "=>"}) {
                for (String polarity : new String[] {"Positive", "Negative"}) {
                    assertLegacyRoundTrip(new ExploreType(createUptoRule(search, stop, polarity,
                                                                         "0"),
                        createAcceptor("final"), 0));
                }
            }
        }
    }

    /** Asserts that a legacy exploration type survives the round trip. */
    private void assertLegacyRoundTrip(ExploreType type) throws FormatException {
        ExploreType back = ExploreTypeConverter.toExploreType(ExploreTypeConverter.toConfig(type));
        assertEquals(type.getIdentifier(), back.getIdentifier());
    }

    /** Creates a serialised uptorule strategy with the given arguments. */
    private Serialized createUptoRule(String search, String stop, String polarity, String bound) {
        Serialized result = new Serialized("uptorule");
        result.setArgument("search", search);
        result.setArgument("stop", stop);
        result.setArgument("polarity", polarity);
        result.setArgument("rule", "load");
        result.setArgument("bound", bound);
        return result;
    }

    /**
     * Tests the conversions of legacy types that map to the feature model but
     * do not round-trip to themselves.
     */
    @Test
    public void testLegacyOneWay() throws FormatException {
        // crule converts to the same config as the equivalent uptorule
        Serialized crule = new Serialized("crule");
        crule.setArgument("rule", "load");
        crule.setArgument("polarity", "Positive");
        assertEquals(ExploreConfig.parse("bound=upto:load"),
                     ExploreTypeConverter.toConfig(new ExploreType(crule,
                         createAcceptor("final"), 0)));
        // uptorule with a nonzero depth bound cannot be expressed
        ExploreType bounded = new ExploreType(createUptoRule("bfs", "->", "Positive", "7"),
            createAcceptor("final"), 0);
        assertThrows(FormatException.class, () -> ExploreTypeConverter.toConfig(bounded));
    }

    /** Creates a serialised strategy with the given keyword, in canonical form
     * (i.e., with the depth bound argument that a parsed bfs/dfs always carries). */
    private Serialized createStrategy(String keyword) {
        Serialized result = new Serialized(keyword);
        if ("bfs".equals(keyword) || "dfs".equals(keyword)) {
            result.setArgument("bound", "0");
        }
        return result;
    }

    /** Creates a serialised acceptor with the given keyword, with arguments where needed. */
    private Serialized createAcceptor(String keyword) {
        Serialized result = new Serialized(keyword);
        switch (keyword) {
        case "inv" -> {
            result.setArgument("rule", "load");
            result.setArgument("polarity", "Positive");
        }
        case "ruleapp" -> result.setArgument("rule", "load");
        case "formula" -> result.setArgument("formula", "load");
        default -> {
            // no arguments
        }
        }
        return result;
    }

    /** Tests that inexpressible configurations are rejected with an error. */
    @Test
    public void testInexpressibleConfig() {
        String[] configs = {
            "frontier=beam:5",
            "next=random",
            "successor=all-random",
            "successor=single",
            "matcher=rete",
            "heuristic=nen",
            "cost=rule",
            "shape=trace",
            "persistence=none",
            "collapse=equality",
            "algebra=point",
            "goal=graph:someGraph",
            "goal=ltl:someProp",
            "goal=formula:load outcome=violate",
            "goal=applied:load outcome=violate",
            "goal=any outcome=violate",
            "bound=size:100",
            "cost=uniform bound=cost:10+5",
            "frontier=single successor=single cost=uniform bound=cost:5",
            "bound=nodes:20+5",
            "next=newest bound=nodes:20",
            "next=newest bound=edges:a>2",
            "matcher=rete next=newest bound=upto:load",
            "frontier=single successor=single bound=upto:load",};
        for (String text : configs) {
            assertThrows(FormatException.class, () -> ExploreTypeConverter
                .toExploreType(ExploreConfig.parse(text)), "Config '%s' should be rejected"
                    .formatted(text));
        }
    }

    /** Tests that legacy types without feature-model equivalent are rejected. */
    @Test
    public void testInexpressibleLegacy() {
        String[][] types = {{"state", "final"}, {"ltl", "cycle"},
            {"minimax", "final"}, {"remote", "final"}, {"bfs", "cycle"},};
        for (String[] pair : types) {
            ExploreType type = new ExploreType(pair[0], pair[1], 0);
            assertThrows(FormatException.class, () -> ExploreTypeConverter.toConfig(type),
                         "Type '%s / %s' should be rejected".formatted(pair[0], pair[1]));
        }
    }

    /**
     * Tests that a converted configuration actually explores, with the same
     * result as the equivalent directly-built legacy exploration type, and
     * that grammar-dependent goal content validates against a real grammar.
     */
    @Test
    public void testExploration() throws Exception {
        GrammarModel grammarModel = Groove.loadGrammar(INPUT_DIR + "/ferryman");
        Grammar grammar = grammarModel.toGrammar();
        // explore with the default configuration, converted
        GTS gtsFromConfig = new GTS(grammar);
        ExploreTypeConverter
            .toExploreType(new ExploreConfig())
            .newExploration(gtsFromConfig, null)
            .play();
        // explore with the directly-built default exploration type
        GTS gtsDirect = new GTS(grammar);
        ExploreType.DEFAULT.newExploration(gtsDirect, null).play();
        assertEquals(gtsDirect.nodeCount(), gtsFromConfig.nodeCount());
        assertEquals(gtsDirect.edgeCount(), gtsFromConfig.edgeCount());
        // a goal referring to an actual rule of the grammar validates
        ExploreTypeConverter
            .toExploreType(ExploreConfig.parse("goal=rule:load count=first"))
            .test(grammar);
        // so do formula goals, with and without operators
        ExploreTypeConverter.toExploreType(ExploreConfig.parse("goal=formula:load")).test(grammar);
        ExploreTypeConverter
            .toExploreType(ExploreConfig.parse("goal=\"formula:load || eat\""))
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
}
