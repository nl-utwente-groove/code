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
 * $Id: TemporalFormulaTest.java,v 1.5 2008-01-30 09:33:53 iovka Exp $
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
import static org.junit.Assert.fail;
import groove.verify.CTLFormula;
import groove.verify.CTLStarFormula;
import groove.verify.Formula;
import groove.verify.FormulaParser;
import groove.verify.ParseException;
import groove.verify.TemporalFormula;
import groove.view.FormatException;

import org.junit.Test;

/**
 * Tests the Formula class.
 * @author Harmen Kastenberg and Arend Rensink
 * @version $Revision$
 */
public class FormulaTest {
    /** Tests {@link FormulaParser#parse(String)}. */
    @Test
    public void testParse() {
        testParse("a", "a");
        testParse("'a'", "a");
        testParse("\"a\"", "a");
        testParse("'a(1)'", "'a(1)'");
        testParse("true", "true");
        testParse("false", "false");
        // and/or/not
        testParse("!!a&!b", "!!a&!b");
        testParse("a&b&c", "a&b&c");
        testParse("a&b|c", "a&b|c");
        testParse("a|b&c", "a|b&c");
        testParse("(!a)|(b&c)", "!a|b&c");
        testParse("!((a|b)&c)", "!((a|b)&c)");
        // implies/follows/equiv
        testParse("a->b", "a->b");
        testParse("a|c<-(b&dc)", "a|c<-b&dc");
        testParse("a->b<->c", "a->b<->c");
        //
        testParse("(a U b) M c V (d M e)", "(a U b)M c V d M e");
        //
        testParse("AFG X true", "A F G X true");
        // errors
        testParseError("a=");
        testParseError("a(");
        testParseError("(a)A");
        testParseError("(a)U");
        testParseError("AX");
        testParseError("'a");
        testParseError("'\\a'");
    }

    private void testParse(String text, String expected) {
        try {
            Formula result = FormulaParser.parse(text);
            assertEquals(expected, result.toString());
        } catch (ParseException e) {
            fail(e.getMessage());
        }
    }

    private void testParseError(String text) {
        try {
            FormulaParser.parse(text);
            fail();
        } catch (ParseException e) {
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
        assertEquals("a V b", Release(a, b).toString());
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

    /** test the creation of ctl formulae */
    @Test
    public void testFormulaCreation() {
        try {
            TemporalFormula formula;

            // CTL* formulae
            formula = CTLStarFormula.parseFormula("empty");
            formula = CTLStarFormula.parseFormula("(empty | non-empty)");
            formula = CTLStarFormula.parseFormula("F(empty & final)");
            formula = CTLStarFormula.parseFormula("G(get U empty)");

            formula = CTLStarFormula.parseFormula("AX(empty)");
            assertEquals("A(X(empty))", formula.toString());
            formula = CTLStarFormula.parseFormula("EX(empty)");
            assertEquals("E(X(empty))", formula.toString());
            formula = CTLStarFormula.parseFormula("A(non-empty U empty)");
            assertEquals("A(non-empty U (empty))", formula.toString());
            formula = CTLStarFormula.parseFormula("E(non-empty U empty)");
            assertEquals("E(non-empty U (empty))", formula.toString());
            formula = CTLStarFormula.parseFormula("AF(empty)");
            assertEquals("A(F(empty))", formula.toString());
            formula = CTLStarFormula.parseFormula("EF(empty)");
            assertEquals("E(F(empty))", formula.toString());
            formula = CTLStarFormula.parseFormula("AG(empty)");
            assertEquals("A(G(empty))", formula.toString());
            formula = CTLStarFormula.parseFormula("EG(empty)");
            assertEquals("E(G(empty))", formula.toString());
            formula = CTLStarFormula.parseFormula("X(empty)");
            assertEquals("X(empty)", formula.toString());
            formula = CTLStarFormula.parseFormula("G(F(empty))");
            assertEquals("G(F(empty))", formula.toString());
            formula = CTLStarFormula.parseFormula("GX(empty)");
            assertEquals("G(X(empty))", formula.toString());

            // CTL formulae
            formula = CTLFormula.parseFormula("EG(empty)");
            formula = CTLFormula.parseFormula("EG(AF(empty))");
            formula = CTLFormula.parseFormula("EX(AX(empty | full))");
            formula = CTLFormula.parseFormula("A(get U (empty | full))");
        } catch (FormatException efe) {
            efe.printStackTrace();
        }
    }
}
