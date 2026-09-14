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
package nl.utwente.groove.test.grammar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import nl.utwente.groove.grammar.LabelFormat;
import nl.utwente.groove.grammar.ResourceProperties.Key;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.io.Groove;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Tests the restricted format syntax of special transition labels (gh #877).
 * @author Arend Rensink
 * @version $Revision$
 */
@SuppressWarnings("javadoc")
@AIGenerated("Claude Fable 5.1, 2026-09")
public class LabelFormatTest {
    /** Location of a fixture grammar whose rules have parameters and special transition labels. */
    static private final String LABEL_GRAMMAR = "junit/samples/attributes-label.gps";

    @Test
    public void testParseAndApply() {
        assertEquals("go", parse("go").apply());
        assertEquals(0, parse("go").getArity());
        // embedded underscores and other characters are literal
        assertEquals("set_gravity!", parse("set_gravity!").apply());
        assertEquals("go 3", parse("go %s").apply(3));
        assertEquals(1, parse("go %s").getArity());
        assertEquals("sc(n0,100)", parse("sc(%s,%s)").apply("n0", 100));
        assertEquals(2, parse("sc(%s,%s)").getArity());
        // explicit indices reorder; implicit numbering runs independently
        assertEquals("b-a", parse("%2$s-%1$s").apply("a", "b"));
        assertEquals("b-a-a", parse("%2$s-%1$s-%s").apply("a", "b"));
        assertEquals(2, parse("%2$s-%1$s-%s").getArity());
        assertEquals(3, parse("%3$s").getArity());
        // percent signs are escaped
        assertEquals("100%", parse("100%%").apply());
        assertEquals("50% of 4", parse("50%% of %s").apply(4));
        assertEquals("%s", parse("%%s").apply());
        // surplus arguments are ignored, as with String.format
        assertEquals("go 1", parse("go %s").apply(1, 2));
        // arguments render by String.valueOf, so null is admissible
        assertEquals("go null", parse("go %s").apply((Object) null));
    }

    @Test
    public void testParseErrors() {
        // conversions other than s
        assertInvalid("go %d");
        assertInvalid("go %S");
        assertInvalid("go %n");
        assertInvalid("go %1$d");
        // flags, width, precision, date/time prefix, relative index
        assertInvalid("go %-3s");
        assertInvalid("go %5s");
        assertInvalid("go %.2s");
        assertInvalid("go %ts");
        assertInvalid("go %<s");
        assertInvalid("go %1$5s");
        // index zero, indexed or flagged percent sign
        assertInvalid("go %0$s");
        assertInvalid("go %1$%");
        assertInvalid("go %-%");
        // stray percent signs
        assertInvalid("go %");
        assertInvalid("100% sure");
        assertInvalid("go % s");
    }

    @Test
    public void testTooFewArguments() {
        try {
            parse("%s,%s").apply("a");
            fail("Expected an exception for too few arguments");
        } catch (IllegalArgumentException exc) {
            // expected
        }
    }

    /** Tests the static check of the transition label property against the rule parameters. */
    @Test
    public void testPropertyCheck() {
        GrammarModel grammar = loadGrammar();
        // set_gravity has one parameter, add_score has two
        AspectGraph oneParam = grammar.getRuleModel(QualName.parse("set_gravity")).getSource();
        AspectGraph twoParams = grammar.getRuleModel(QualName.parse("add_score")).getSource();
        assertTrue(check(oneParam, "grav").isEmpty());
        assertTrue(check(oneParam, "grav %s").isEmpty());
        assertTrue(check(oneParam, "grav %1$s %1$s").isEmpty());
        assertTrue(check(oneParam, "100%% %s").isEmpty());
        assertTrue(check(twoParams, "sc(%s,%s)").isEmpty());
        assertTrue(check(twoParams, "sc(%2$s,%1$s)").isEmpty());
        // more parameters referenced than the rule has
        assertError(check(oneParam, "grav %s %s"), "expects 2");
        assertError(check(oneParam, "grav %2$s"), "expects 2");
        assertError(check(twoParams, "sc(%3$s)"), "expects 3");
        // disallowed specifiers are reported as such, not as missing parameters
        assertError(check(oneParam, "grav %d"), "Unsupported format specifier '%d'");
        assertError(check(twoParams, "sc(%s,%5s)"), "Unsupported format specifier '%5s'");
        assertError(check(oneParam, "grav 100%"), "Incomplete format specifier");
    }

    /** Tests that the compiled rules carry the parsed label format. */
    @Test
    public void testCompiledRules() throws FormatException {
        var grammar = loadGrammar().toGrammar();
        var setGravity = grammar.getRule(QualName.parse("set_gravity"));
        var addScore = grammar.getRule(QualName.parse("add_score"));
        assertEquals("grav %s", setGravity.getSpecialLabel());
        assertEquals(1, setGravity.getSpecialLabelFormat().get().getArity());
        assertEquals("sc(%s,%s)", addScore.getSpecialLabel());
        assertEquals(2, addScore.getSpecialLabelFormat().get().getArity());
        // a rule without special label has neither
        var rule = grammar.getRule(QualName.parse("get_score"));
        assertFalse(rule.getSpecialLabelFormat().isPresent());
        assertEquals("", rule.getSpecialLabel());
    }

    private LabelFormat parse(String format) {
        try {
            return LabelFormat.parse(format);
        } catch (FormatException exc) {
            fail(exc.getMessage());
            throw new IllegalStateException();
        }
    }

    private void assertInvalid(String format) {
        try {
            LabelFormat.parse(format);
            fail("Expected format error in '" + format + "'");
        } catch (FormatException exc) {
            // expected
        }
    }

    private FormatErrorSet check(AspectGraph graph, String format) {
        try {
            return Key.TRANSITION_LABEL.check(graph, Key.TRANSITION_LABEL.parse(format));
        } catch (FormatException exc) {
            fail(exc.getMessage());
            throw new IllegalStateException();
        }
    }

    private void assertError(FormatErrorSet errors, String text) {
        assertFalse("Expected error containing '" + text + "'", errors.isEmpty());
        assertTrue(errors.toString(), errors.toString().contains(text));
    }

    private GrammarModel loadGrammar() {
        try {
            return Groove.loadGrammar(LABEL_GRAMMAR);
        } catch (IOException exc) {
            fail(exc.getMessage());
            throw new IllegalStateException();
        }
    }
}
