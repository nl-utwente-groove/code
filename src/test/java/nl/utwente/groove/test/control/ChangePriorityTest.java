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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import nl.utwente.groove.control.CtrlLoader;
import nl.utwente.groove.grammar.Recipe;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Tests {@link CtrlLoader#changePriority}, which rewrites control programs
 * so as to reflect new recipe priorities (see gh #733).
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
@SuppressWarnings("javadoc")
public class ChangePriorityTest extends CtrlTester {
    {
        initGrammar("emptyrules");
    }

    /** Name of the control program used throughout the tests. */
    private static final QualName PROG = QualName.name("prog");

    private CtrlLoader loader;

    @Before
    public void initLoader() {
        this.loader = createLoader();
    }

    /** Parses a control program under the name {@link #PROG}. */
    private void addProgram(String program) {
        try {
            this.loader.addControl(PROG, program);
            this.loader.buildProgram();
        } catch (FormatException exc) {
            fail(exc.getMessage());
        }
    }

    /** Invokes {@link CtrlLoader#changePriority} for a single recipe. */
    private Map<QualName,String> changePriority(String recipeName, int priority) {
        Map<QualName,Integer> prioMap = new HashMap<>();
        prioMap.put(QualName.parse(recipeName), priority);
        return this.loader.changePriority(prioMap);
    }

    /** Checks that a rewritten program parses and assigns an expected recipe priority. */
    private void assertRecipePriority(String program, String recipeName, int priority) {
        try {
            CtrlLoader newLoader = createLoader();
            newLoader.addControl(PROG, program);
            newLoader.buildProgram();
            Recipe recipe = null;
            for (Recipe r : newLoader.getRecipes()) {
                if (r.getQualName().equals(QualName.parse(recipeName))) {
                    recipe = r;
                }
            }
            assertTrue("Recipe " + recipeName + " not found", recipe != null);
            assertEquals(priority, recipe.getPriority());
        } catch (FormatException exc) {
            fail("Rewritten program does not parse: " + exc.getMessage());
        }
    }

    @Test
    public void testInsertPriority() {
        addProgram("recipe r() { a; } b;");
        Map<QualName,String> result = changePriority("r", 2);
        String newProgram = result.get(PROG);
        assertEquals("recipe r() priority 2 { a; } b;", newProgram);
        assertRecipePriority(newProgram, "r", 2);
    }

    @Test
    public void testReplacePriority() {
        addProgram("recipe r() priority 1 { a; } b;");
        Map<QualName,String> result = changePriority("r", 2);
        String newProgram = result.get(PROG);
        assertEquals("recipe r() priority 2 { a; } b;", newProgram);
        assertRecipePriority(newProgram, "r", 2);
    }

    @Test
    public void testRemovePriority() {
        addProgram("recipe r() priority 1 { a; } b;");
        Map<QualName,String> result = changePriority("r", 0);
        String newProgram = result.get(PROG);
        assertEquals("recipe r() { a; } b;", newProgram);
        assertRecipePriority(newProgram, "r", 0);
    }

    @Test
    public void testUnchangedPriority() {
        addProgram("recipe r() priority 1 { a; } recipe s() { b; } c;");
        Map<QualName,Integer> prioMap = new HashMap<>();
        prioMap.put(QualName.name("r"), 1);
        prioMap.put(QualName.name("s"), 0);
        assertTrue(this.loader.changePriority(prioMap).isEmpty());
    }

    @Test
    public void testTwoRecipesInOneProgram() {
        addProgram("recipe r() { a; } recipe s() priority 1 { b; } c;");
        Map<QualName,Integer> prioMap = new HashMap<>();
        prioMap.put(QualName.name("r"), 3);
        prioMap.put(QualName.name("s"), 2);
        Map<QualName,String> result = this.loader.changePriority(prioMap);
        String newProgram = result.get(PROG);
        assertEquals("recipe r() priority 3 { a; } recipe s() priority 2 { b; } c;", newProgram);
        assertRecipePriority(newProgram, "r", 3);
        assertRecipePriority(newProgram, "s", 2);
    }

    @Test
    public void testLeadingComment() {
        addProgram("// leading comment\nrecipe r() priority 1 { a; } b;");
        Map<QualName,String> result = changePriority("r", 2);
        String newProgram = result.get(PROG);
        assertEquals("// leading comment\nrecipe r() priority 2 { a; } b;", newProgram);
        assertRecipePriority(newProgram, "r", 2);
    }

    @Test
    public void testNonRecipeName() {
        addProgram("recipe r() { a; } b;");
        // rule names have no declaring control program and should be skipped
        assertTrue(changePriority("a", 2).isEmpty());
    }
}
