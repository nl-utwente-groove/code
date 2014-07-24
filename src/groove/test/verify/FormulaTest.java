/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
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

package groove.test.verify;

import static groove.verify.Formula.Always;
import static groove.verify.Formula.And;
import static groove.verify.Formula.Atom;
import static groove.verify.Formula.Equiv;
import static groove.verify.Formula.Eventually;
import static groove.verify.Formula.Exists;
import static groove.verify.Formula.False;
import static groove.verify.Formula.Follows;
import static groove.verify.Formula.Forall;
import static groove.verify.Formula.Implies;
import static groove.verify.Formula.Next;
import static groove.verify.Formula.Not;
import static groove.verify.Formula.Or;
import static groove.verify.Formula.Release;
import static groove.verify.Formula.SRelease;
import static groove.verify.Formula.True;
import static groove.verify.Formula.Until;
import static groove.verify.Formula.WUntil;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import groove.util.parse.FormatException;
import groove.verify.Formula;
import groove.verify.FormulaParser;

import org.junit.Test;

/**
 * Tests the Formula class.
 * @author Harmen Kastenberg and Arend Rensink
 * @version $Revision$
 */
@SuppressWarnings("all")
public class FormulaTest {
    /** Tests {@link FormulaParser#parse(String)}. */
    @Test
    public void testParse() {
        Formula a = Atom("a");
        Formula b = Atom("b");
        Formula c = Atom("c");
        Formula dc = Atom("dc");
        Formula d = Atom("d");
        Formula e = Atom("e");
        testParse("a", a);
        testParse("'a'", a);
        testParse("\"a\"", a);
        testParse("'a(1)'", Atom("a(1)"));
        testParse("true", True());
        testParse("false", False());
        // and/or/not
        testParse("!!a&!b", And(Not(Not(a)), Not(b)));
        testParse("a&b&c", And(a, And(b, c)));
        testParse("a&b|c", Or(And(a, b), c));
        testParse("a|b&c", Or(a, And(b, c)));
        testParse("(!a)|(b&c)", Or(Not(a), And(b, c)));
        testParse("!((a|b)&c)", Not(And(Or(a, b), c)));
        // implies/follows/equiv
        testParse("a->b", Implies(a, b));
        testParse("a|c<-(b&dc)", Follows(Or(a, c), And(b, dc)));
        testParse("a->b<->c", Equiv(Implies(a, b), c));
        testParse("a->(b<->c)", Implies(a, Equiv(b, c)));
        //
        testParse("(a U b) M c R (d M e)", SRelease(Until(a, b), Release(c, SRelease(d, e))));
        //
        testParse("AFG X true", Forall(Eventually(Always(Next(True())))));
        // errors
        testParseError("a=");
        testParseError("(a");
        testParseError("a(");
        testParseError("(a)A");
        testParseError("(a)U");
        testParseError("AX");
        testParseError("'a");
        testParseError("'\\a'");
        testParseError("Xtrue");
        testParseError("U true");
    }

    private void testParse(String text, Formula expected) {
        try {
            Formula result = FormulaParser.parse(text);
            assertEquals(expected, result);
        } catch (FormatException e) {
            fail(e.getMessage());
        }
    }

    private void testParseError(String text) {
        try {
            FormulaParser.parse(text);
            fail();
        } catch (FormatException e) {
            // success
        }
    }

    /** Tests the toString method of the Formula class. */
    @Test
    public void testFormulaToString() {
        Formula a = Atom("a");
        Formula b = Atom("b");
        Formula c = Atom("c");
        // atoms
        assertEquals("a", a.toString());
        assertEquals("true", True().toString());
        assertEquals("false", False().toString());
        // negation
        assertEquals("!a", Not(a).toString());
        assertEquals("!(a|b)", Not(Or(a, b)).toString());
        assertEquals("!a|b", Or(Not(a), b).toString());
        // and/or
        assertEquals("a&b", And(a, b).toString());
        assertEquals("a&b|c", Or(And(a, b), c).toString());
        assertEquals("a&(b|c)", And(a, Or(b, c)).toString());
        // implies/follows/equiv
        assertEquals("a->b", Implies(a, b).toString());
        assertEquals("a<-b", Follows(a, b).toString());
        assertEquals("a<->b", Equiv(a, b).toString());
        assertEquals("a<->b->c", Equiv(a, Implies(b, c)).toString());
        assertEquals("(a<->b)->c", Implies(Equiv(a, b), c).toString());
        // next/always/eventually
        assertEquals("X X a", Next(Next(a)).toString());
        assertEquals("F G a", Eventually(Always(a)).toString());
        // until/release
        assertEquals("a R b", Release(a, b).toString());
        assertEquals("a M b", SRelease(a, b).toString());
        assertEquals("a U b", Until(a, b).toString());
        assertEquals("a W b", WUntil(a, b).toString());
        assertEquals("(a->b)U c", Until(Implies(a, b), c).toString());
        assertEquals("a->b U c", Implies(a, Until(b, c)).toString());
        // forall/exists
        assertEquals("A true", Forall(True()).toString());
        assertEquals("E F a", Exists(Eventually(a)).toString());
        assertEquals("A a U b", Forall(Until(a, b)).toString());
        assertEquals("(A a)U b", Until(Forall(a), b).toString());
    }

    /** Tests if a given formula is ripe for CTL verification. */
    @Test
    public void testIsCtlFormula() {
        Formula a = Atom("a");
        Formula b = Atom("b");
        Formula c = Atom("c");
        // Any simple propositional formula
        assertTrue(a.isCtlFormula());
        assertTrue(True().isCtlFormula());
        assertTrue(False().isCtlFormula());
        assertTrue(And(Or(Not(a), b), c).isCtlFormula());
        // implication-like operators
        assertTrue(Implies(a, b).isCtlFormula());
        assertTrue(Follows(a, b).isCtlFormula());
        assertTrue(Equiv(a, b).isCtlFormula());
        // No next without path quantifier
        assertFalse(Next(a).isCtlFormula());
        assertTrue(Exists(Next(a)).isCtlFormula());
        // No until without path quantifier
        assertFalse(Until(a, b).isCtlFormula());
        assertTrue(Forall(Until(a, b)).isCtlFormula());
        // no weak until or release
        assertFalse(Forall(WUntil(a, b)).isCtlFormula());
        assertFalse(Forall(Release(a, b)).isCtlFormula());
        assertFalse(Forall(SRelease(a, b)).isCtlFormula());
        assertTrue(Forall(Until(a, b)).isCtlFormula());
        // No isolated path quantifier
        assertFalse(Forall(a).isCtlFormula());
    }

    @Test
    public void testToCtlFormula() {
        Formula a = Atom("a");
        Formula b = Atom("b");
        Formula c = Atom("c");
        testToCtlFormula("a->b", Implies(a, b));
        testToCtlFormula("AX a", Forall(Next(a)));
        testToCtlFormula("E(true U a)", Exists(Eventually(a)));
        testToCtlFormula("!E(true U !a)", Forall(Always(a)));
    }

    private void testToCtlFormula(String expected, Formula f) {
        try {
            assertEquals(FormulaParser.parse(expected), f.toCtlFormula());
        } catch (FormatException e) {
            fail(e.getMessage());
        }
    }
}
