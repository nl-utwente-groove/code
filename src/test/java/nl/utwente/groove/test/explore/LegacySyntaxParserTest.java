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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.Test;

import nl.utwente.groove.explore.AcceptorEnumerator;
import nl.utwente.groove.explore.ExploreType;
import nl.utwente.groove.explore.LTLExploreType;
import nl.utwente.groove.explore.MinimaxExploreType;
import nl.utwente.groove.explore.RemoteExploreType;
import nl.utwente.groove.explore.StateExploreType;
import nl.utwente.groove.explore.StrategyEnumerator;
import nl.utwente.groove.explore.config.ConfiguredExploreType;
import nl.utwente.groove.explore.config.ExploreConfig;
import nl.utwente.groove.explore.config.ExploreKey;
import nl.utwente.groove.explore.config.ExploreTypeConverter;
import nl.utwente.groove.explore.config.Goal;
import nl.utwente.groove.explore.config.NextState;
import nl.utwente.groove.explore.config.Persistence;
import nl.utwente.groove.explore.config.parse.LegacySyntaxParser;
import nl.utwente.groove.explore.result.CycleAcceptor;
import nl.utwente.groove.explore.result.FinalStateAcceptor;
import nl.utwente.groove.explore.strategy.BoundedLTLStrategy;
import nl.utwente.groove.explore.strategy.BoundedPocketLTLStrategy;
import nl.utwente.groove.explore.strategy.ExploreStateStrategy;
import nl.utwente.groove.explore.strategy.LTLStrategy;
import nl.utwente.groove.explore.strategy.RemoteStrategy;
import nl.utwente.groove.grammar.Grammar;
import nl.utwente.groove.util.Groove;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Tests for the legacy exploration syntax parser: parity of the direct
 * legacy-to-configuration translation with the enumerator-based path (for
 * as long as the latter exists), the overlay semantics onto a base
 * configuration, and the dedicated exploration types for the non-config
 * strategies.
 * @author Arend Rensink
 * @version $Revision$
 */
public class LegacySyntaxParserTest {
    /** Location of the sample grammar used for the instantiation tests. */
    static private final String GRAMMAR = "junit/samples/ferryman";

    /**
     * Asserts that the direct translation of a legacy description yields the
     * same configuration as parsing it through the enumerators and
     * converting the result.
     */
    private void assertParity(String strategy, String acceptor, int count) throws FormatException {
        var legacy = new ExploreType(StrategyEnumerator.parseCommandLineStrategy(strategy),
            AcceptorEnumerator.parseCommandLineAcceptor(acceptor), count);
        var expected = ExploreTypeConverter.toConfig(legacy);
        var actual = LegacySyntaxParser.parse(strategy + " " + acceptor + " " + count);
        assertInstanceOf(ConfiguredExploreType.class, actual);
        assertEquals(expected, ((ConfiguredExploreType) actual).getConfig());
    }

    /** Tests the config-expressible strategies against the enumerator path. */
    @Test
    public void testStrategyParity() throws FormatException {
        for (String strategy : List
            .of("bfs", "bfs:5", "dfs", "dfs:3", "linear", "random", "crule:eat", "crule:!eat",
                "cnbound:20", "cebound:append>6", "cebound:a>1,b>2", "uptorule:bfs->eat",
                "uptorule:dfs=>!eat", "uptorule:dfs->eat")) {
            assertParity(strategy, "final", 0);
        }
    }

    /** Tests the config-expressible acceptors against the enumerator path. */
    @Test
    public void testAcceptorParity() throws FormatException {
        for (String acceptor : List
            .of("final", "any", "none", "ruleapp:eat", "inv:eat", "inv:!eat",
                "formula:eat|unload")) {
            assertParity("bfs", acceptor, 0);
            if (!"none".equals(acceptor)) {
                // a result count is inconsistent with the no-result goal in
                // the feature model (a deliberate tightening over the legacy
                // machinery, which silently accepted the combination)
                assertParity("dfs", acceptor, 2);
            }
        }
        assertParity("bfs", "final", 1);
        assertThrows(FormatException.class, () -> LegacySyntaxParser.parse("dfs none 2"));
    }

    /**
     * Tests the overlay semantics: a strategy resets only the
     * strategy-owned features, an acceptor only the goal and outcome, and
     * all other features of the base configuration are preserved.
     */
    @Test
    public void testOverlay() throws FormatException {
        var base = ExploreTypeConverter
            .toExploreType(ExploreConfig.parse("persistence=none next=newest goal=fires:eat"));
        // a strategy overlay resets the traversal but keeps persistence and goal
        var strategic
            = (ConfiguredExploreType) LegacySyntaxParser.overlay(base, "bfs", null, 0);
        assertEquals(Persistence.NONE, strategic.getConfig().getKind(ExploreKey.PERSISTENCE));
        assertEquals(NextState.OLDEST, strategic.getConfig().getKind(ExploreKey.NEXT));
        assertEquals(Goal.FIRES, strategic.getConfig().getKind(ExploreKey.GOAL));
        // an acceptor overlay resets the goal but keeps the traversal
        var accepting
            = (ConfiguredExploreType) LegacySyntaxParser.overlay(base, null, "any", 0);
        assertEquals(NextState.NEWEST, accepting.getConfig().getKind(ExploreKey.NEXT));
        assertEquals(Goal.ANY, accepting.getConfig().getKind(ExploreKey.GOAL));
        // a count overlay only adds the count
        var counted = (ConfiguredExploreType) LegacySyntaxParser.overlay(base, null, null, 2);
        assertEquals(ExploreConfig
            .parse("persistence=none next=newest goal=fires:eat count=2"), counted.getConfig());
    }

    /** Tests the dedicated exploration types for the non-config strategies. */
    @Test
    public void testDirectTypes() throws Exception {
        Grammar grammar = Groove.loadGrammar(GRAMMAR).toGrammar();
        // properties may contain spaces, so the LTL flavours are exercised
        // through the overlay entry point, which takes the -s value whole
        var ltl = LegacySyntaxParser.overlay(ExploreType.DEFAULT, "ltl:F eat", "cycle", 0);
        assertInstanceOf(LTLExploreType.class, ltl);
        assertInstanceOf(LTLStrategy.class, ltl.getParsedStrategy(grammar));
        assertInstanceOf(CycleAcceptor.class, ltl.getParsedAcceptor(grammar));
        // the boundary of the bounded flavours resolves against the grammar
        var bounded = LegacySyntaxParser.overlay(ExploreType.DEFAULT,
                                                 "ltlbounded:5,3;F eat", null, 0);
        var boundedStrategy = bounded.getParsedStrategy(grammar);
        assertInstanceOf(BoundedLTLStrategy.class, boundedStrategy);
        assertEquals(BoundedLTLStrategy.class, boundedStrategy.getClass());
        var pocket = LegacySyntaxParser.overlay(ExploreType.DEFAULT,
                                                "ltlpocket:eat,unload;F eat", null, 0);
        assertInstanceOf(BoundedPocketLTLStrategy.class, pocket.getParsedStrategy(grammar));
        // single-state and remote exploration
        var state = LegacySyntaxParser.parse("state final 0");
        assertInstanceOf(StateExploreType.class, state);
        assertInstanceOf(ExploreStateStrategy.class, state.getParsedStrategy(grammar));
        assertInstanceOf(FinalStateAcceptor.class, state.getParsedAcceptor(grammar));
        var remote = LegacySyntaxParser.parse("remote:http://localhost any 0");
        assertInstanceOf(RemoteExploreType.class, remote);
        assertInstanceOf(RemoteStrategy.class, remote.getParsedStrategy(grammar));
        // minimax construction (instantiation needs parametrised rules)
        var minimax = LegacySyntaxParser.parse("minimax:1,10,eat;load,max,eat,2 final 0");
        assertInstanceOf(MinimaxExploreType.class, minimax);
    }

    /** Tests the rejection of malformed or inconsistent legacy descriptions. */
    @Test
    public void testErrors() {
        assertThrows(FormatException.class, () -> LegacySyntaxParser.parse("bogus final 0"));
        assertThrows(FormatException.class, () -> LegacySyntaxParser.parse("bfs bogus 0"));
        // the cycle acceptor requires an LTL strategy, and vice versa
        assertThrows(FormatException.class, () -> LegacySyntaxParser.parse("bfs cycle 0"));
        assertThrows(FormatException.class, () -> LegacySyntaxParser.parse("ltl:prop final 0"));
        // a condition bound cannot be combined with a depth bound
        assertThrows(FormatException.class,
                     () -> LegacySyntaxParser.parse("uptorule:bfs2->eat final 0"));
        // missing or malformed arguments
        assertThrows(FormatException.class, () -> LegacySyntaxParser.parse("crule final 0"));
        assertThrows(FormatException.class, () -> LegacySyntaxParser.parse("cnbound:xx final 0"));
        assertThrows(FormatException.class,
                     () -> LegacySyntaxParser.parse("cebound:append final 0"));
        assertThrows(FormatException.class,
                     () -> LegacySyntaxParser.parse("ltlbounded:prop cycle 0"));
        assertThrows(FormatException.class,
                     () -> LegacySyntaxParser.parse("minimax:1,10 final 0"));
        assertThrows(FormatException.class, () -> LegacySyntaxParser.parse("bfs final -1"));
        assertThrows(FormatException.class, () -> LegacySyntaxParser.parse("bfs"));
    }
}
