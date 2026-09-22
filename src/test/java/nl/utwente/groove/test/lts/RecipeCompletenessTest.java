// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2026 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
package nl.utwente.groove.test.lts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.explore.Exploration;
import nl.utwente.groove.explore.config.LegacySyntaxParser;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.io.store.SystemStore;
import nl.utwente.groove.lts.AbstractGraphState;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GTSListener;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.lts.GraphTransition;
import nl.utwente.groove.lts.RecipeTransition;
import nl.utwente.groove.lts.RuleTransition;
import nl.utwente.groove.lts.Status;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Checks the transient-state bookkeeping of the state caches after a full
 * exploration of the recipe and atomic-block sample grammars (gh #924):
 * every state has become full, and every recipe launch has a recipe
 * transition to every recipe target reachable from it through the inner
 * states of the recipe run. The targets are recomputed here by a plain
 * forward search over the finished GTS, independently of the bookkeeping
 * that created the recipe transitions during exploration.
 * <p>
 * Every case runs breadth-first and depth-first, each with and without a
 * simulated garbage-collection sweep of the collectable (full) state caches
 * at every closure, which forces the caches of full states to be recreated
 * and their recipe targets to be recomputed.
 * @author Arend Rensink
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public class RecipeCompletenessTest {
    /** Grammar, control program and start graph of a test case; {@code recipes} tells if the control program has recipes. */
    private record Case(String grammar, String control, String start, boolean recipes) {
        @Override
        public String toString() {
            return this.grammar + " / " + this.control + " / " + this.start;
        }
    }

    private static final List<Case> CASES = List
        .of(new Case("junit/samples/recipes.gps", "ab-recipes", "start-small", true),
            new Case("junit/samples/recipes.gps", "alap-recipes", "start-small", true),
            new Case("junit/samples/recipes.gps", "atomic", "start-small", false),
            new Case("junit/samples/recipes_conditions.gps", "recipe", "start", true),
            new Case("junit/samples/transactions.gps", "control", "start", true),
            new Case("junit/samples/transactions.gps", "control1", "start", true),
            new Case("junit/samples/transactions.gps", "control2", "start", true),
            new Case("junit/samples/transactions.gps", "try", "start", false));

    /** Breadth-first exploration. */
    @Test
    public void testBfs() {
        CASES.forEach(c -> check(c, "bfs", false));
    }

    /** Depth-first exploration. */
    @Test
    public void testDfs() {
        CASES.forEach(c -> check(c, "dfs", false));
    }

    /** Breadth-first exploration with cache collapse at every closure. */
    @Test
    public void testBfsCollapse() {
        CASES.forEach(c -> check(c, "bfs", true));
    }

    /** Depth-first exploration with cache collapse at every closure. */
    @Test
    public void testDfsCollapse() {
        CASES.forEach(c -> check(c, "dfs", true));
    }

    private void check(Case c, String strategy, boolean collapse) {
        String descr = c + " (" + strategy + (collapse
            ? ", collapsing"
            : "") + ")";
        GTS gts = explore(c, strategy, collapse);
        int launchCount = 0;
        for (GraphState state : gts.nodeSet()) {
            assertTrue(state.isClosed(), descr + ": state " + state + " is not closed");
            assertTrue(state.isFull(), descr + ": state " + state + " is not full");
        }
        for (GraphState state : gts.nodeSet()) {
            // recipe transitions per launch, as created during exploration
            Map<RuleTransition,Set<GraphState>> actual = new LinkedHashMap<>();
            for (GraphTransition trans : state.getTransitions(GraphTransition.Claz.ANY)) {
                if (trans instanceof RecipeTransition recipeTrans
                    && !recipeTrans.target().isAbsent()) {
                    actual
                        .computeIfAbsent(recipeTrans.getLaunch(), k -> new LinkedHashSet<>())
                        .add(recipeTrans.target());
                }
            }
            for (RuleTransition trans : state.getRuleTransitions()) {
                if (trans.getStep().isLaunch() && !trans.target().getActualFrame().isRemoved()) {
                    launchCount++;
                    Set<GraphState> expected = getTargets(trans);
                    Set<GraphState> found = actual.remove(trans);
                    assertEquals(expected, found == null
                        ? Set.of()
                        : found, descr + ": recipe targets of launch " + trans);
                }
            }
            assertTrue(actual.isEmpty(),
                       descr + ": recipe transitions without launch from " + state + ": " + actual);
        }
        assertEquals(c.recipes(), launchCount > 0, descr + ": recipe launches");
    }

    /** Computes the non-absent recipe targets reachable from a given launch
     * transition, by forward search over the inner steps of the finished GTS. */
    private Set<GraphState> getTargets(RuleTransition launch) {
        Set<GraphState> result = new LinkedHashSet<>();
        Set<GraphState> visited = new LinkedHashSet<>();
        Deque<GraphState> queue = new ArrayDeque<>();
        queue.add(launch.target());
        while (!queue.isEmpty()) {
            GraphState next = queue.poll();
            assert next != null; // queue is non-empty
            if (!visited.add(next)) {
                continue;
            }
            if (!next.isInner()) {
                if (!next.isAbsent()) {
                    result.add(next);
                }
            } else {
                for (RuleTransition trans : next.getRuleTransitions()) {
                    if (trans.isInnerStep() && !trans.target().getActualFrame().isRemoved()) {
                        queue.add(trans.target());
                    }
                }
            }
        }
        return result;
    }

    private GTS explore(Case c, String strategy, boolean collapse) {
        try {
            GrammarModel grammarModel = SystemStore.newGrammar(new File(c.grammar()));
            grammarModel.setLocalActiveNames(ResourceKind.HOST, QualName.name(c.start()));
            grammarModel.setLocalActiveNames(ResourceKind.CONTROL, QualName.name(c.control()));
            GTS gts = new GTS(grammarModel.toGrammar());
            if (collapse) {
                gts.addLTSListener(new GTSListener() {
                    @Override
                    public void statusUpdate(GTS observed, GraphState state, int change) {
                        if (Status.Flag.CLOSED.test(change) && state.isClosed()) {
                            // simulate a garbage collection sweep: only the
                            // softly referenced caches (of full states) can go
                            for (GraphState s : observed.nodeSet().toArray(new GraphState[0])) {
                                if (s instanceof AbstractGraphState closed && closed.isClosed()
                                    && closed.isCacheCollectable()) {
                                    closed.clearCache();
                                }
                            }
                        }
                    }
                });
            }
            Exploration exploration
                = LegacySyntaxParser.parse(strategy + " final 0").newExploration(gts, null).play();
            assertFalse(exploration.isInterrupted());
            return gts;
        } catch (IOException | FormatException exc) {
            fail(exc.toString());
            throw new IllegalStateException();
        }
    }
}
