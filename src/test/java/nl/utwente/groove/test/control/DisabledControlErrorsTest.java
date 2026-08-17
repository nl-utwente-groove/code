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
package nl.utwente.groove.test.control;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.io.Groove;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;

/**
 * Tests the error messages for calls and imports of units that are declared
 * in the grammar but not available: procedures of disabled control programs,
 * disabled rules and erroneous rules (see gh #560).
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
public class DisabledControlErrorsTest {
    /** Location of the fixture grammar. */
    private static final String GRAMMAR = "junit/control/disabled";

    /** Loads the fixture grammar. */
    private GrammarModel loadGrammar() {
        try {
            return Groove.loadGrammar(GRAMMAR);
        } catch (Exception e) {
            fail(e.getMessage());
            return null;
        }
    }

    /** Returns the concatenated error text of a given control program. */
    private String getErrors(GrammarModel grammar, String controlName) {
        return grammar.getControlModel(QualName.parse(controlName)).getErrors().toString();
    }

    /** Checks that a given error text contains an expected phrase. */
    private void assertContains(String errors, String phrase) {
        assertTrue(String.format("Errors %s do not contain '%s'", errors, phrase),
                   errors.contains(phrase));
    }

    /** Tests the messages of an enabled program referring to unavailable units. */
    @Test
    public void testEnabledProgram() {
        GrammarModel grammar = loadGrammar();
        String errors = getErrors(grammar, "a");
        // import of a procedure declared in a disabled program
        assertContains(errors,
                       "Imported function 'sub.f' is declared in control program 'sub.lib', which is not enabled");
        // call of a procedure declared in a disabled program
        assertContains(errors,
                       "Function 'sub.f' is declared in control program 'sub.lib', which is not enabled");
        // call of a disabled rule
        assertContains(errors, "Rule 'disabledRule' exists but is not enabled");
        // call of an enabled rule with errors
        assertContains(errors, "Rule 'badRule' exists but has errors");
    }

    /** Tests the messages of a disabled program (checked in isolation)
     * referring to units of an enabled sibling program. */
    @Test
    public void testDisabledProgram() {
        GrammarModel grammar = loadGrammar();
        String errors = getErrors(grammar, "b");
        assertContains(errors,
                       "Imported function 'sub2.g' is declared in control program 'sub2.lib2', which is not visible while this program is disabled");
        assertContains(errors,
                       "Function 'sub2.g' is declared in control program 'sub2.lib2', which is not visible while this program is disabled");
    }

    /** Tests that a disabled program has no errors of its own. */
    @Test
    public void testDisabledLibrary() {
        GrammarModel grammar = loadGrammar();
        var errors = grammar.getControlModel(QualName.parse("sub.lib")).getErrors();
        assertTrue(String.format("Unexpected errors %s", errors), errors.isEmpty());
    }
}
