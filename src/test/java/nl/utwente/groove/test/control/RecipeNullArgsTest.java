/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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

package nl.utwente.groove.test.control;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import nl.utwente.groove.explore.Exploration;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.io.store.SystemStore;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.lts.RecipeTransition;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Tests the semantics of undefined (null) control variables and action
 * arguments arising from node deletion, in particular around recipes
 * (see claude/recipe-outpar-deletion.md):
 * <ul>
 * <li>a rule call with a null in-argument is inapplicable;
 * <li>a recipe call with a null in-argument is inapplicable as a whole,
 * without any partial (transient) execution;
 * <li>a recipe out-parameter bound to a node that is deleted before the recipe
 * completes results in a null recipe transition argument, displayed as "_";
 * <li>a recipe out-parameter bound to a value node computed, or a node
 * created, by the final step survives as the recipe transition argument
 * (neither has an image in the final transition's morphism, but both are
 * valid target values).
 * </ul>
 * The exploration of the out-parameter cases formerly failed an assertion in
 * {@code StateCache} (with assertions enabled) or produced a dangling node
 * argument (without).
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
public class RecipeNullArgsTest {
    /** Location of the fixture grammar. */
    static public final String GRAMMAR = "junit/control/nullargs.gps";

    /** A rule call on a variable nulled by deletion is inapplicable:
     * exploration deadlocks rather than crashing or matching. */
    @Test
    public void testPlainNullVariable() {
        GTS gts = explore("plainNull");
        assertEquals(3, gts.getStateCount());
        assertEquals(2, gts.getTransitionCount());
        assertTrue(gts
            .edgeSet()
            .stream()
            .noneMatch(trans -> trans.getAction().getQualName().toString().equals("use")));
    }

    /** A recipe called with a null in-argument does not start at all:
     * no inner states or transitions are created, even though the recipe's
     * initial rule does not consume the argument. */
    @Test
    public void testRecipeBoundary() {
        GTS gts = explore("recipeBoundary");
        assertEquals(3, gts.nodeSet().size());
        assertTrue(gts.nodeSet().stream().noneMatch(GraphState::isInner));
        assertTrue(gts
            .edgeSet()
            .stream()
            .noneMatch(trans -> trans.getAction().getQualName().toString().equals("grow")));
        assertTrue(getRecipeTransitions(gts).isEmpty());
    }

    /** A recipe out-parameter deleted by the final step becomes null,
     * when the final step also merges nodes (non-identity morphism). */
    @Test
    public void testOutParDeleteWithMerge() {
        GTS gts = explore("outParDelete");
        RecipeTransition trans = getSingleRecipeTransition(gts);
        assertEquals(1, trans.getArguments().length);
        assertNull(trans.getArguments()[0]);
        assertEquals("r(_)", trans.label().text(false));
    }

    /** A recipe out-parameter deleted by the final step becomes null,
     * also when the final step only deletes (identity-like morphism);
     * formerly this silently kept the deleted node as argument. */
    @Test
    public void testOutParDeletePlain() {
        GTS gts = explore("outParDeletePlain");
        RecipeTransition trans = getSingleRecipeTransition(gts);
        assertEquals(1, trans.getArguments().length);
        assertNull(trans.getArguments()[0]);
        assertEquals("r(_)", trans.label().text(false));
    }

    /** A recipe in-parameter survives a merging final step;
     * formerly this failed an assertion on the in-parameter slot. */
    @Test
    public void testInParFuse() {
        GTS gts = explore("inParFuse");
        RecipeTransition trans = getSingleRecipeTransition(gts);
        assertEquals(1, trans.getArguments().length);
        assertNotNull(trans.getArguments()[0]);
    }

    /** A recipe out-parameter bound to a value node computed by the final
     * step survives as the recipe transition argument: the value node is an
     * anchor image that is not a source-graph node of the final transition,
     * so it has no image in the transition morphism, but is canonical and
     * hence a valid target value. Formerly nulled by the unconditional
     * morphism mapping. */
    @Test
    public void testOutParComputedValue() {
        GTS gts = explore("outParValue");
        RecipeTransition trans = getSingleRecipeTransition(gts);
        assertEquals(1, trans.getArguments().length);
        assertNotNull(trans.getArguments()[0]);
        assertEquals("v(1)", trans.label().text(false));
    }

    /** A recipe out-parameter bound to a node created by the final step
     * survives as the recipe transition argument: the created node has no
     * image in the transition morphism (whose domain is the source graph),
     * but is a target-graph identity. Formerly nulled by the unconditional
     * morphism mapping. */
    @Test
    public void testOutParCreatedNode() {
        GTS gts = explore("outParCreated");
        RecipeTransition trans = getSingleRecipeTransition(gts);
        assertEquals(1, trans.getArguments().length);
        var arg = trans.getArguments()[0];
        assertNotNull(arg);
        assertTrue(trans.target().getGraph().containsNode(arg));
    }

    /** Explores the fixture grammar under a given control program
     * and returns the resulting GTS. */
    private GTS explore(String control) {
        try {
            GrammarModel ggModel = SystemStore.newGrammar(new File(GRAMMAR));
            ggModel.setLocalActiveNames(ResourceKind.CONTROL, QualName.name(control));
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

    /** Returns the single recipe transition of a given GTS,
     * failing the test if there is not exactly one. */
    private RecipeTransition getSingleRecipeTransition(GTS gts) {
        var result = getRecipeTransitions(gts);
        assertEquals(1, result.size());
        return result.get(0);
    }
}
