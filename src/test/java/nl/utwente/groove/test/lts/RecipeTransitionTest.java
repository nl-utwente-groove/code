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
package nl.utwente.groove.test.lts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.explore.Exploration;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.graph.EdgeRole;
import nl.utwente.groove.io.store.SystemStore;
import nl.utwente.groove.lts.DefaultRuleTransition;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.lts.RecipeTransition;
import nl.utwente.groove.lts.RuleTransition;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Tests the API of the transition objects making up an explored GTS —
 * {@link RecipeTransition} with its steps, path, morphism and event, and the
 * underlying {@link RuleTransition}s — rather than just the state and
 * transition counts that the exploration-based tests assert on.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
@NonNullByDefault
public class RecipeTransitionTest {
    /** Location of the fixture grammar. */
    static public final String GRAMMAR = "junit/samples/recipes.gps";

    /** The steps of a recipe transition form a subgraph from its source to
     * its target through inner states, and the path is a chain through that
     * subgraph. */
    @Test
    public void testStepsAndPath() {
        boolean multiStep = false;
        for (String control : new String[] {"ab-recipes", "alap-recipes"}) {
            GTS gts = explore(control, "start-tiny");
            var recipeTranss = getRecipeTransitions(gts);
            assertFalse(recipeTranss.isEmpty());
            for (RecipeTransition trans : recipeTranss) {
                RuleTransition launch = trans.getLaunch();
                assertSame(trans.source(), launch.source());
                var steps = trans.getSteps();
                assertTrue(steps.contains(launch));
                for (RuleTransition step : steps) {
                    assertTrue(step.target().isInner() || step.target() == trans.target());
                    // a rule transition is its own singleton step sequence
                    assertEquals(List.of(step), toList(step.getSteps()));
                }
                List<RuleTransition> path = trans.getPath();
                assertSame(launch, path.get(0));
                assertSame(trans.target(), path.get(path.size() - 1).target());
                for (int i = 0; i < path.size(); i++) {
                    assertTrue(steps.contains(path.get(i)));
                    if (i > 0) {
                        assertSame(path.get(i - 1).target(), path.get(i).source());
                    }
                }
                multiStep |= path.size() > 1;
            }
        }
        // the fixture recipes are multi-rule, so some path must have more
        // than one step, or the test is not testing what it should; a recipe
        // whose alap body fails immediately legitimately has a one-step path
        assertTrue(multiStep);
    }

    /** The (lazily reconstructed) morphism of a transition maps nodes and
     * edges of the source graph to nodes and edges of the target graph. */
    @Test
    public void testMorphism() {
        GTS gts = explore("alap-recipes", "start-tiny");
        int checked = 0;
        for (var trans : gts.edgeSet()) {
            var morphism = trans.getMorphism();
            var sourceGraph = trans.source().getGraph();
            var targetGraph = trans.target().getGraph();
            for (var nodeEntry : morphism.nodeMap().entrySet()) {
                assertTrue(sourceGraph.containsNode(nodeEntry.getKey()));
                assertTrue(targetGraph.containsNode(nodeEntry.getValue()));
            }
            for (var edgeEntry : morphism.edgeMap().entrySet()) {
                assertTrue(sourceGraph.containsEdge(edgeEntry.getKey()));
                assertTrue(targetGraph.containsEdge(edgeEntry.getValue()));
            }
            checked++;
        }
        assertTrue(checked > 0);
    }

    /** A recipe transition survives the round trip through its event, and a
     * rule transition the round trip through its stub. */
    @Test
    public void testEventRoundTrip() {
        GTS gts = explore("ab-recipes", "start-tiny");
        for (RecipeTransition trans : getRecipeTransitions(gts)) {
            var event = trans.getEvent();
            assertSame(event, trans.getKey());
            assertSame(event, trans.toStub());
            assertSame(trans.target(), event.getTarget());
            assertSame(trans.getAction(), event.getAction());
            assertEquals(trans.getLaunch(), event.getInitial().toTransition(trans.source()));
            RecipeTransition clone = event.toTransition(trans.source());
            assertEquals(trans, clone);
            assertEquals(trans.hashCode(), clone.hashCode());
            assertEquals(event, clone.getEvent());
            assertEquals(event.hashCode(), clone.getEvent().hashCode());
            assertEquals(0, event.compareTo(clone.getEvent()));
            assertFalse(event.toString().isEmpty());
        }
        for (var trans : gts.edgeSet()) {
            if (trans instanceof RuleTransition ruleTrans) {
                var source = ruleTrans.source();
                var stub = ruleTrans.toStub();
                assertEquals(ruleTrans, stub.toTransition(source));
                assertSame(ruleTrans.target(), stub.getTarget(source));
                assertSame(ruleTrans.getEvent(), stub.getEvent());
            }
        }
    }

    /** The source-dependent accessors reject a foreign source state. Only
     * {@link DefaultRuleTransition} documents the rejection (a state acting
     * as its own incoming transition merely asserts), so the rule-transition
     * half of the test is restricted to that class. */
    @Test
    public void testForeignSource() {
        GTS gts = explore("ab-recipes", "start-tiny");
        for (RecipeTransition trans : getRecipeTransitions(gts)) {
            GraphState foreign = getForeignState(gts, trans.source());
            assertSame(trans, trans.toTransition(trans.source()));
            assertThrows(IllegalArgumentException.class, () -> trans.toTransition(foreign));
        }
        int checked = 0;
        for (var edge : gts.edgeSet()) {
            if (edge instanceof DefaultRuleTransition ruleTrans) {
                GraphState foreign = getForeignState(gts, ruleTrans.source());
                assertSame(ruleTrans, ruleTrans.toTransition(ruleTrans.source()));
                // getKey mints a fresh MatchResult per call, so equality only
                assertEquals(ruleTrans.getKey(), ruleTrans.getKey(ruleTrans.source()));
                assertThrows(IllegalArgumentException.class, () -> ruleTrans.toTransition(foreign));
                assertThrows(IllegalArgumentException.class, () -> ruleTrans.getKey(foreign));
                assertThrows(IllegalArgumentException.class, () -> ruleTrans.getTarget(foreign));
                checked++;
            }
        }
        assertTrue(checked > 0);
    }

    /** Display and ordering properties of the transition labels. */
    @Test
    public void testDisplayAndOrder() {
        GTS gts = explore("ab-recipes", "start-tiny");
        var recipeTranss = getRecipeTransitions(gts);
        for (RecipeTransition trans : recipeTranss) {
            assertSame(trans, trans.label());
            assertEquals(trans.text(), trans.text(true));
            assertEquals(trans.getAction().getQualName().toString(), trans.text());
            assertEquals(EdgeRole.BINARY, trans.getRole());
            assertFalse(trans.isInnerStep());
            assertTrue(trans.isPublicStep());
            assertEquals(0, trans.compareTo(trans));
            // the comparison inverts consistently against rule transition
            // labels and other recipe transitions
            for (var other : gts.edgeSet()) {
                var otherLabel = other.label();
                assertEquals(Integer.signum(trans.compareTo(otherLabel)),
                             -Integer.signum(otherLabel.compareTo(trans)));
            }
        }
        // distinct recipe transitions are unequal
        for (RecipeTransition one : recipeTranss) {
            for (RecipeTransition two : recipeTranss) {
                assertEquals(one == two, one.equals(two));
            }
        }
    }

    /** Explores the fixture grammar under a given control program and start
     * graph, and returns the resulting GTS. */
    private GTS explore(String control, String start) {
        try {
            GrammarModel ggModel = SystemStore.newGrammar(new File(GRAMMAR));
            ggModel.setLocalActiveNames(ResourceKind.CONTROL, QualName.name(control));
            ggModel.setLocalActiveNames(ResourceKind.HOST, QualName.name(start));
            GTS gts = new GTS(ggModel.toGrammar());
            Exploration exploration = Exploration.explore(gts);
            assertFalse(exploration.isInterrupted());
            return gts;
        } catch (IOException | FormatException exc) {
            fail(exc.toString());
            throw new IllegalStateException(); // unreachable
        }
    }

    /** Returns the recipe transitions of a given GTS. */
    private List<RecipeTransition> getRecipeTransitions(GTS gts) {
        return gts
            .edgeSet()
            .stream()
            .filter(RecipeTransition.class::isInstance)
            .map(RecipeTransition.class::cast)
            .toList();
    }

    /** Returns some state of the GTS that is not the given state. */
    private GraphState getForeignState(GTS gts, GraphState state) {
        var result
            = gts.nodeSet().stream().filter(s -> s != state).findFirst().orElse(null);
        assertNotNull(result);
        return result;
    }

    /** Collects an iterable into a list. */
    private <T> List<T> toList(Iterable<T> iterable) {
        var result = new java.util.ArrayList<T>();
        iterable.forEach(result::add);
        return result;
    }
}
