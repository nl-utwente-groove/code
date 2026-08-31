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
import nl.utwente.groove.lts.RecipeTransition;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Tests the liveness-based computation of control location variables (gh #561):
 * a rule or recipe out-parameter that is bound to a control variable which is
 * never used afterwards must not be retained in the target states' call stacks,
 * so that states differing only in such a dead binding collapse into one.
 * Also tests that control variables used inside expression arguments are
 * treated as uses (formerly such variables crashed exploration, since they
 * were never bound to call stack indices).
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
public class ParOutLivenessTest {
    /** Location of the fixture grammar. */
    static public final String GRAMMAR = "junit/control/parout.gps";

    /** A dead out-parameter binding does not keep target states apart:
     * both matches of the binding rule lead to the same state. */
    @Test
    public void testDeadOut() {
        GTS gts = explore("deadOut");
        assertEquals(2, gts.getStateCount());
    }

    /** A live out-parameter binding keeps the intermediate states apart
     * until the last use of the variable, after which the states collapse. */
    @Test
    public void testLiveOut() {
        GTS gts = explore("liveOut");
        // start state, two intermediate states (one per binding of z),
        // and one collapsed state after the last use of z
        assertEquals(4, gts.getStateCount());
    }

    /** A dead recipe out-parameter binding does not keep target states apart,
     * while the recipe transitions still carry the actual out-parameter
     * values as arguments. */
    @Test
    public void testRecipeOut() {
        GTS gts = explore("recipeOut");
        assertEquals(2, gts.getStateCount());
        List<RecipeTransition> recipeTranses = gts
            .edgeSet()
            .stream()
            .filter(RecipeTransition.class::isInstance)
            .map(RecipeTransition.class::cast)
            .toList();
        // one recipe transition per binding of the out-parameter
        assertEquals(2, recipeTranses.size());
        for (RecipeTransition trans : recipeTranses) {
            assertEquals(1, trans.getArguments().length);
            assertNotNull(trans.getArguments()[0]);
        }
        assertEquals(2,
                     recipeTranses
                         .stream()
                         .map(trans -> trans.getArguments()[0])
                         .distinct()
                         .count());
    }

    /** Control variables used inside expression arguments are uses:
     * they are kept live up to the expression, and the expressions evaluate
     * correctly. (This crashed with a NullPointerException before the
     * variables of expression arguments were seeded as uses.) */
    @Test
    public void testExprArg() {
        GTS gts = explore("exprArg");
        // the program is fully deterministic: x := 1, y := x+1+1 = 3, w := y+2+1 = 6
        assertEquals(4, gts.getStateCount());
        assertTrue(gts.nodeSet().stream().anyMatch(state -> state.isFinal()));
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
}
