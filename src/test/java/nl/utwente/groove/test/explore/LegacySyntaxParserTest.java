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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.Test;

import nl.utwente.groove.explore.ExploreType;
import nl.utwente.groove.explore.LTLExploreType;
import nl.utwente.groove.explore.config.ConfiguredExploreType;
import nl.utwente.groove.explore.config.ExploreConfig;
import nl.utwente.groove.explore.config.ExploreTypeConverter;
import nl.utwente.groove.explore.config.LegacySyntaxParser;
import nl.utwente.groove.explore.feature.ExploreKey;
import nl.utwente.groove.explore.feature.Goal;
import nl.utwente.groove.explore.feature.NextState;
import nl.utwente.groove.explore.feature.Persistence;
import nl.utwente.groove.explore.verify.BoundedLTLStrategy;
import nl.utwente.groove.explore.verify.BoundedPocketLTLStrategy;
import nl.utwente.groove.explore.verify.LTLStrategy;
import nl.utwente.groove.grammar.Grammar;
import nl.utwente.groove.util.Groove;
import nl.utwente.groove.util.parse.FormatException;
import nl.utwente.groove.verify.CycleAcceptor;

/**
 * Tests for the legacy exploration syntax parser: the translation of the
 * legacy strategy and acceptor keywords into the feature model, the overlay
 * semantics onto a base configuration, and the dedicated exploration types
 * for the non-config strategies.
 * @author Arend Rensink
 * @version $Revision$
 */
public class LegacySyntaxParserTest {
    /** Location of the sample grammar used for the instantiation tests. */
    static private final String GRAMMAR = "junit/samples/ferryman";

    /**
     * Asserts that a legacy description translates to a configuration-based
     * type with a given configuration.
     */
    private void assertTranslation(String expected, String strategy, String acceptor,
                                   int count) throws FormatException {
        var actual = LegacySyntaxParser.parse(strategy + " " + acceptor + " " + count);
        assertInstanceOf(ConfiguredExploreType.class, actual,
                         "'%s %s %d' should translate to a configuration"
                             .formatted(strategy, acceptor, count));
        assertEquals(ExploreConfig.parse(expected),
                     ((ConfiguredExploreType) actual).getConfig(),
                     "Translation mismatch for '%s %s %d'"
                         .formatted(strategy, acceptor, count));
    }

    /** Tests the translation of the config-expressible strategies. */
    @Test
    public void testStrategyTranslation() throws FormatException {
        String[][] cases = {
            {"bfs", ""},
            {"bfs:5", "cost=uniform bound=cost:5"},
            {"dfs", "next=newest"},
            {"dfs:3", "next=newest cost=uniform bound=cost:3"},
            {"linear", "frontier=single successor=single"},
            {"random", "frontier=single successor=single-random"},
            {"state", "bound=initial"},
            {"crule:eat", "bound=upto:eat"},
            {"crule:!eat", "bound=upto:!eat"},
            {"cnbound:20", "bound=nodes:20"},
            {"cebound:append>6", "bound=edges:append>6"},
            {"cebound:a>1,b>2", "bound=edges:a>1,b>2"},
            {"cebound:type:A>3,flag:f>1", "bound=edges:type:A>3,flag:f>1"},
            {"uptorule:bfs->eat", "bound=upto:eat"},
            {"uptorule:dfs=>!eat", "next=newest bound=include:!eat"},
            {"uptorule:dfs->eat", "next=newest bound=upto:eat"},};
        for (String[] pair : cases) {
            assertTranslation(pair[1], pair[0], "final", 0);
        }
    }

    /** Tests the translation of the config-expressible acceptors and the count. */
    @Test
    public void testAcceptorTranslation() throws FormatException {
        String[][] cases = {
            {"final", ""},
            {"any", "goal=any"},
            {"none", "goal=none"},
            {"ruleapp:eat", "goal=fires:eat"},
            {"inv:eat", "goal=condition:eat"},
            {"inv:!eat", "goal=condition:!eat"},
            {"formula:eat|unload", "goal=condition:eat|unload"},};
        for (String[] pair : cases) {
            assertTranslation(pair[1], "bfs", pair[0], 0);
            if (!"none".equals(pair[0])) {
                // a result count is inconsistent with the no-result goal in
                // the feature model (a deliberate tightening over the legacy
                // machinery, which silently accepted the combination)
                assertTranslation("next=newest count=2 "
                    + pair[1], "dfs", pair[0], 2);
            }
        }
        assertTranslation("count=first", "bfs", "final", 1);
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
        // legacy components cannot be overlaid on a non-configuration type
        var ltl = LegacySyntaxParser.parse("ltl:true cycle 0");
        assertThrows(FormatException.class,
                     () -> LegacySyntaxParser.overlay(ltl, null, "any", 0));
    }

    /** Tests the dedicated exploration types for the non-config strategies. */
    @Test
    public void testDirectTypes() throws Exception {
        Grammar grammar = Groove.loadGrammar(GRAMMAR).toGrammar();
        // properties may contain spaces, so the LTL flavours are exercised
        // through the overlay entry point, which takes the -s value whole
        var ltl = LegacySyntaxParser.overlay(ExploreType.getDefault(), "ltl:F eat", "cycle", 0);
        assertInstanceOf(LTLExploreType.class, ltl);
        var ltlRealisation = ltl.realise(grammar);
        assertInstanceOf(LTLStrategy.class, ltlRealisation.strategy());
        assertInstanceOf(CycleAcceptor.class, ltlRealisation.collector());
        // the boundary of the bounded flavours resolves against the grammar
        var bounded = LegacySyntaxParser.overlay(ExploreType.getDefault(),
                                                 "ltlbounded:5,3;F eat", null, 0);
        var boundedStrategy = bounded.realise(grammar).strategy();
        assertInstanceOf(BoundedLTLStrategy.class, boundedStrategy);
        assertEquals(BoundedLTLStrategy.class, boundedStrategy.getClass());
        assertEquals("5,3", ((BoundedLTLStrategy) boundedStrategy).getBoundary().toString(),
                     "The graph-size boundary should carry the parsed size and step");
        var pocket = LegacySyntaxParser.overlay(ExploreType.getDefault(),
                                                "ltlpocket:eat,unload;F eat", null, 0);
        var pocketStrategy = pocket.realise(grammar).strategy();
        assertInstanceOf(BoundedPocketLTLStrategy.class, pocketStrategy);
        // the rule set iterates in set order, so compare order-insensitively
        var pocketRules = ((BoundedLTLStrategy) pocketStrategy).getBoundary().toString();
        assertEquals(Set.of("eat", "unload"), Set.of(pocketRules.split(",")),
                     "The rule-set boundary should carry the parsed rules");
        // a boundary with a leading comma is a format error, not a crash
        // (regression: BoundaryParser indexed into the empty first element)
        var commaBounded = LegacySyntaxParser.overlay(ExploreType.getDefault(),
                                                      "ltlbounded:,eat;F eat", null, 0);
        assertThrows(FormatException.class, () -> commaBounded.realise(grammar));
    }

    /** Tests the rejection of malformed or inconsistent legacy descriptions. */
    @Test
    public void testErrors() {
        assertThrows(FormatException.class, () -> LegacySyntaxParser.parse("bogus final 0"));
        assertThrows(FormatException.class, () -> LegacySyntaxParser.parse("bfs bogus 0"));
        // remote exploration was removed altogether
        assertThrows(FormatException.class,
                     () -> LegacySyntaxParser.parse("remote:http://localhost any 0"));
        // the minimax strategy was removed in release 8.0, with a
        // dedicated error pointing to gh #890
        var minimaxError = assertThrows(FormatException.class,
                                        () -> LegacySyntaxParser
                                            .parse("minimax:1,10,eat;load,max,eat,2 final 0"));
        assertTrue(minimaxError.getMessage().contains("890"),
                   "The minimax error should point to the removal issue");
        // the cycle acceptor requires an LTL strategy, and vice versa
        assertThrows(FormatException.class, () -> LegacySyntaxParser.parse("bfs cycle 0"));
        assertThrows(FormatException.class, () -> LegacySyntaxParser.parse("state cycle 0"));
        assertThrows(FormatException.class, () -> LegacySyntaxParser.parse("ltl:prop final 0"));
        // a condition bound cannot be combined with a depth bound
        assertThrows(FormatException.class,
                     () -> LegacySyntaxParser.parse("uptorule:bfs2->eat final 0"));
        // missing or malformed arguments
        assertThrows(FormatException.class, () -> LegacySyntaxParser.parse("state:5 final 0"));
        assertThrows(FormatException.class, () -> LegacySyntaxParser.parse("crule final 0"));
        assertThrows(FormatException.class, () -> LegacySyntaxParser.parse("cnbound:xx final 0"));
        assertThrows(FormatException.class,
                     () -> LegacySyntaxParser.parse("cebound:append final 0"));
        assertThrows(FormatException.class,
                     () -> LegacySyntaxParser.parse("ltlbounded:prop cycle 0"));
        assertThrows(FormatException.class, () -> LegacySyntaxParser.parse("bfs final -1"));
        assertThrows(FormatException.class, () -> LegacySyntaxParser.parse("bfs"));
    }
}
