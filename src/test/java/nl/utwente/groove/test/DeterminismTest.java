/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2026
 * University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id$
 */

package nl.utwente.groove.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import nl.utwente.groove.explore.ExploreType;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.lts.AbstractGraphState;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GTSListener;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.util.Groove;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Tests that successive explorations of the same rule system, within one JVM,
 * enumerate exactly the same states and transitions in the same order.
 * This guards against run-to-run nondeterminism creeping in through
 * identity-based hash codes in iterated hash collections, or through
 * unordered collections on the exploration path.
 * @author Arend Rensink
 * @version $Revision$
 */
public class DeterminismTest {
    /** Location of the samples. */
    static private final String INPUT_DIR = "junit/samples";

    /** Tests determinism of the default (plan-based) exploration. */
    @Test
    public void testPlanEngineDeterminism() {
        test("ferryman", "bfs");
        test("loose-nodes", "bfs");
    }

    /** Tests determinism of the RETE-based exploration. */
    @Test
    public void testReteEngineDeterminism() {
        test("ferryman", "rete");
        test("loose-nodes", "rete");
    }

    /**
     * Explores a named grammar repeatedly with a given strategy — perturbing
     * the identity hash code sequence in between, and aggressively collapsing
     * state caches during the later explorations — and asserts that all
     * explorations enumerate identical states and transitions in identical
     * order.
     */
    private void test(String grammarName, String strategy) {
        try {
            GrammarModel grammarModel = Groove.loadGrammar(INPUT_DIR + "/" + grammarName);
            ExploreType exploreType = new ExploreType(strategy, "final", 0);
            String first = explore(grammarModel, exploreType, false);
            perturbIdentityHashes();
            String second = explore(grammarModel, exploreType, false);
            assertEquals(String
                .format("Non-deterministic %s exploration of grammar %s", strategy, grammarName),
                         first, second);
            for (int i = 0; i < 3; i++) {
                perturbIdentityHashes();
                String third = explore(grammarModel, exploreType, true);
                assertEquals(String
                    .format("Non-deterministic %s exploration of grammar %s under cache collapse",
                            strategy, grammarName),
                             first, third);
            }
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    /**
     * Explores a grammar and returns the enumeration signature of the resulting GTS.
     * @param collapseCaches if {@code true}, every state's cache is cleared as
     * soon as the state is closed. This simulates the garbage collector
     * clearing the (softly referenced) caches under memory pressure, which
     * forces graphs and transition data to be reconstructed on next use —
     * the enumeration must be insensitive to such reconstructions.
     */
    private String explore(GrammarModel grammarModel, ExploreType exploreType,
                           boolean collapseCaches) throws FormatException {
        GTS gts = new GTS(grammarModel.toGrammar());
        if (collapseCaches) {
            gts.addLTSListener(new GTSListener() {
                @Override
                public void statusUpdate(GTS observed, GraphState state, int change) {
                    if (state.isClosed() && state instanceof AbstractGraphState closed) {
                        closed.clearCache();
                    }
                }
            });
        }
        exploreType.newExploration(gts, null).play();
        StringBuilder result = new StringBuilder();
        gts.nodeSet().forEach(n -> result.append(n).append('\n'));
        gts
            .edgeSet()
            .forEach(e -> result
                .append(e.source())
                .append("--")
                .append(e.label())
                .append("->")
                .append(e.target())
                // the transition hash covers the (content-based) event and
                // state-number hashes, which must also be reproducible
                .append(" #")
                .append(e.hashCode())
                .append('\n'));
        return result.toString();
    }

    /**
     * Advances the JVM's identity hash code sequence, so that objects created
     * by a subsequent exploration receive different identity hashes than in a
     * previous one. This is what exposes iteration over identity-hash-keyed
     * collections.
     */
    private void perturbIdentityHashes() {
        int sink = 0;
        for (int i = 0; i < 100_000; i++) {
            sink += System.identityHashCode(new Object());
        }
        // use the sink so the loop cannot be optimised away
        if (sink == Integer.MIN_VALUE) {
            fail("unreachable");
        }
    }
}
