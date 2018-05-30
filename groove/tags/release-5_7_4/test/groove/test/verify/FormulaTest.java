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

import static groove.algebra.Constant.instance;
import static groove.grammar.QualName.name;
import static groove.verify.Formula.always;
import static groove.verify.Formula.and;
import static groove.verify.Formula.atom;
import static groove.verify.Formula.call;
import static groove.verify.Formula.equiv;
import static groove.verify.Formula.eventually;
import static groove.verify.Formula.exists;
import static groove.verify.Formula.ff;
import static groove.verify.Formula.follows;
import static groove.verify.Formula.forall;
import static groove.verify.Formula.implies;
import static groove.verify.Formula.next;
import static groove.verify.Formula.not;
import static groove.verify.Formula.or;
import static groove.verify.Formula.release;
import static groove.verify.Formula.sRelease;
import static groove.verify.Formula.tt;
import static groove.verify.Formula.until;
import static groove.verify.Formula.wUntil;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import groove.util.parse.FormatException;
import groove.verify.Formula;
import groove.verify.FormulaParser;
import groove.verify.Proposition;

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
        Formula aId = atom(name("a"));
        Formula aString = atom("a");
        Formula bId = atom(name("b"));
        Formula cId = atom(name("c"));
        Formula dcId = atom(name("dc"));
        Formula dId = atom(name("d"));
        Formula eId = atom(name("e"));
        testParse("a", aId);
        testParse("'a'", aString);
        testParse("\"a\"", aString);
        testParse("'a(1)'", atom("a(1)"));
        testParse("a(1,id,'value')", call(name("a"), instance(1), "id", instance("value")));
        testParse("a( 1 , id,  \"value\" )", call(name("a"), instance(1), "id", instance("value")));
        testParse("a(a,_)", call(name("a"), "a", Proposition.Arg.WILD_TEXT));
        testParse("true", tt());
        testParse("false", ff());
        // and/or/not
        testParse("!!a&!b", and(not(not(aId)), not(bId)));
        testParse("a&b&c", and(aId, and(bId, cId)));
        testParse("a&b|c", or(and(aId, bId), cId));
        testParse("a|b&c", or(aId, and(bId, cId)));
        testParse("(!a)|(b&c)", or(not(aId), and(bId, cId)));
        testParse("!((a|b)&c)", not(and(or(aId, bId), cId)));
        // implies/follows/equiv
        testParse("a->b", implies(aId, bId));
        testParse("a|c<-(b&dc)", follows(or(aId, cId), and(bId, dcId)));
        testParse("a->b<->c", equiv(implies(aId, bId), cId));
        testParse("a->(b<->c)", implies(aId, equiv(bId, cId)));
        //
        testParse("(a U b) M c R (d M e)",
            sRelease(until(aId, bId), release(cId, sRelease(dId, eId))));
        //
        testParse("AFG X true", forall(eventually(always(next(tt())))));
        testParse("AG(get|put)", forall(always(or(atom(name("get")), atom(name("put"))))));
        // errors
        testParseError("EXEX add_score(n0, __)");
        testParseError("a=");
        testParseError("(a");
        testParseError("a(");
        testParseError("(a)A");
        testParseError("(a)U");
        testParseError("AX");
        testParseError("'a");
        testParseError("U true");
        testParseError("F!(final U)");
    }

    private void testParse(String text, Formula expected) {
        Formula result = FormulaParser.instance()
            .parse(text);
        assertEquals(expected, result);
        if (result.hasErrors()) {
            fail(result.getErrors()
                .toString());
        }
    }

    private void testParseError(String text) {
        Formula result = FormulaParser.instance()
            .parse(text);
        assertTrue(result.hasErrors());
    }

    /** Tests the toString method of the Formula class. */
    @Test
    public void testFormulaToString() {
        Formula a = atom("a");
        Formula b = atom("b");
        Formula c = atom("c");
        // atoms
        testEquals("a", a);
        testEquals("true", tt());
        testEquals("false", ff());
        // negation
        testEquals("!a", not(a));
        testEquals("!(a|b)", not(or(a, b)));
        testEquals("!a|b", or(not(a), b));
        // and/or
        testEquals("a&b", and(a, b));
        testEquals("a&b|c", or(and(a, b), c));
        testEquals("a&(b|c)", and(a, or(b, c)));
        // implies/follows/equiv
        testEquals("a->b", implies(a, b));
        testEquals("a<-b", follows(a, b));
        testEquals("a<->b", equiv(a, b));
        testEquals("a<->b->c", equiv(a, implies(b, c)));
        testEquals("(a<->b)->c", implies(equiv(a, b), c));
        // next/always/eventually
        testEquals("XX a", next(next(a)));
        testEquals("FG a", eventually(always(a)));
        // until/release
        testEquals("a R b", release(a, b));
        testEquals("a M b", sRelease(a, b));
        testEquals("a U b", until(a, b));
        testEquals("a W b", wUntil(a, b));
        testEquals("(a->b)U c", until(implies(a, b), c));
        testEquals("a->b U c", implies(a, until(b, c)));
        // forall/exists
        testEquals("A true", forall(tt()));
        testEquals("EF a", exists(eventually(a)));
        testEquals("A a U b", forall(until(a, b)));
        testEquals("(A a)U b", until(forall(a), b));
    }

    private void testEquals(String s, Formula f) {
        assertEquals(s, f.toLine()
            .toFlatString());
    }

    /** Tests if a given formula is ripe for CTL verification. */
    @Test
    public void testIsCtlFormula() {
        Formula a = atom("a");
        Formula b = atom("b");
        Formula c = atom("c");
        // Any simple propositional formula
        assertTrue(a.isCtlFormula());
        assertTrue(tt().isCtlFormula());
        assertTrue(ff().isCtlFormula());
        assertTrue(and(or(not(a), b), c).isCtlFormula());
        // implication-like operators
        assertTrue(implies(a, b).isCtlFormula());
        assertTrue(follows(a, b).isCtlFormula());
        assertTrue(equiv(a, b).isCtlFormula());
        // No next without path quantifier
        assertFalse(next(a).isCtlFormula());
        assertTrue(exists(next(a)).isCtlFormula());
        // No until without path quantifier
        assertFalse(until(a, b).isCtlFormula());
        assertTrue(forall(until(a, b)).isCtlFormula());
        // no weak until or release
        assertFalse(forall(wUntil(a, b)).isCtlFormula());
        assertFalse(forall(release(a, b)).isCtlFormula());
        assertFalse(forall(sRelease(a, b)).isCtlFormula());
        assertTrue(forall(until(a, b)).isCtlFormula());
        // No isolated path quantifier
        assertFalse(forall(a).isCtlFormula());
    }

    @Test
    public void testToCtlFormula() {
        Formula a = atom(name("a"));
        Formula b = atom(name("b"));
        Formula c = atom(name("c"));
        testToCtlFormula("a->b", implies(a, b));
        testToCtlFormula("AX a", forall(next(a)));
        testToCtlFormula("E(true U a)", exists(eventually(a)));
        testToCtlFormula("!E(true U !a)", forall(always(a)));
    }

    private void testToCtlFormula(String expected, Formula f) {
        try {
            assertEquals(FormulaParser.instance()
                .parse(expected), f.toCtlFormula());
        } catch (FormatException e) {
            fail(e.getMessage());
        }
    }
}
