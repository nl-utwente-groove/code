/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2026 University of Twente
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
package nl.utwente.groove.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.explore.ExploreType;
import nl.utwente.groove.explore.Exploration;
import nl.utwente.groove.grammar.GrammarKey;
import nl.utwente.groove.grammar.GrammarProperties;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.io.Groove;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;

/**
 * Tests the {@code matchBound} grammar property (gh #784): when the number of
 * matches collected for a single state exceeds the bound, exploration halts
 * gracefully rather than crashing, flagging the offending state as an error
 * state. The fixture grammar sets {@code matchBound=10} and contains two ways
 * of exceeding it: a plain rule with 20 matches on the default start graph
 * ({@code pickOne} on {@code host}), and a forall-exists rule with 16
 * amalgamated match combinations on the alternative start graph
 * ({@code bigForall} on {@code bipartite}).
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
@NonNullByDefault
public class MatchBoundTest {
    /** Location of the fixture grammar. */
    private static final String GRAMMAR = "junit/rules/matchbound";

    /** The default value of the property is the bound previously
     * hard-wired into the tree matcher. */
    @Test
    public void testDefaultBound() {
        assertEquals(GrammarKey.DEFAULT_MATCH_BOUND, new GrammarProperties().getMatchBound());
    }

    /** The bound in the fixture's system.properties is picked up. */
    @Test
    public void testStoredBound() throws Exception {
        assertEquals(10, loadGrammar().getProperties().getMatchBound());
    }

    /** A plain rule with 20 matches on a bound of 10: exploration halts,
     * the start state is flagged as an error state, and the GTS records
     * the problem as an error naming the rule. */
    @Test
    public void testPlainFanoutHalts() throws Exception {
        Exploration exploration = explore("host", 10);
        assertTrue(exploration.isHalted());
        assertTrue(exploration.getLastMessage().contains("pickOne"));
        assertTrue(exploration.getLastMessage().contains(GrammarKey.MATCH_BOUND.getName()));
        GTS gts = exploration.getGTS();
        assertTrue(gts.hasErrors());
        assertTrue(gts.startState().isError());
    }

    /** The same exploration with the bound lifted completes: the 20
     * indistinguishable nodes collapse to 21 states up to isomorphism. */
    @Test
    public void testPlainFanoutCompletes() throws Exception {
        Exploration exploration = explore("host", 0);
        assertFalse(exploration.isHalted());
        GTS gts = exploration.getGTS();
        assertFalse(gts.hasErrors());
        assertEquals(21, gts.nodeCount());
    }

    /** A forall-exists rule whose amalgamated match combinations (2 choices
     * for each of 4 universal instances, so 16 in all) exceed the bound of
     * 10: exploration halts. */
    @Test
    public void testAmalgamationHalts() throws Exception {
        Exploration exploration = explore("bipartite", 10);
        assertTrue(exploration.isHalted());
        assertTrue(exploration.getLastMessage().contains("bigForall"));
        GTS gts = exploration.getGTS();
        assertTrue(gts.hasErrors());
        assertTrue(gts.startState().isError());
    }

    /** The same exploration with a sufficient bound completes, with all
     * 16 amalgamated matches as transitions out of the start state. */
    @Test
    public void testAmalgamationCompletes() throws Exception {
        Exploration exploration = explore("bipartite", 100);
        assertFalse(exploration.isHalted());
        GTS gts = exploration.getGTS();
        assertFalse(gts.hasErrors());
        assertEquals(16, gts.startState().getTransitions().size());
    }

    /** Loads the fixture grammar. */
    private GrammarModel loadGrammar() throws Exception {
        return Groove.loadGrammar(GRAMMAR);
    }

    /**
     * Runs a default exploration of the fixture grammar from a given start
     * graph, with a given match bound, and returns the (played) exploration.
     * @param startGraph name of the start graph to explore from
     * @param matchBound the match bound to set; {@code 0} means no bound
     */
    private Exploration explore(String startGraph, int matchBound) throws Exception {
        GrammarModel grammarModel = loadGrammar();
        var properties = grammarModel.getProperties().clone();
        properties.setMatchBound(matchBound);
        grammarModel.setProperties(properties);
        var hostModel = grammarModel.getHostModel(QualName.name(startGraph));
        grammarModel.setStartGraph(hostModel.getSource());
        GTS gts = new GTS(grammarModel.toGrammar());
        return ExploreType.getDefault().newExploration(gts, null).play();
    }
}
