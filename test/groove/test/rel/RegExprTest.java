/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.test.rel;

import static groove.automaton.RegExpr.atom;
import static groove.automaton.RegExpr.empty;
import static groove.automaton.RegExpr.wildcard;
import static groove.grammar.type.TypeLabel.createLabel;
import static groove.graph.EdgeRole.BINARY;
import static groove.graph.EdgeRole.FLAG;
import static groove.graph.EdgeRole.NODE_TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import groove.automaton.RegExpr;
import groove.automaton.RegExpr.Atom;
import groove.grammar.rule.LabelVar;
import groove.grammar.type.TypeLabel;
import groove.util.parse.FormatException;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

/** Tests the class {@link RegExpr}. */
public class RegExprTest {
    private TypeLabel aLabel;
    private TypeLabel bLabel;
    private TypeLabel cLabel;
    private TypeLabel aType;
    private TypeLabel bType;
    private RegExpr aAtom;
    private RegExpr bAtom;
    private RegExpr cAtom;

    /** Initialises test fields. */
    @Before
    public void setUp() {
        this.aLabel = createLabel("a");
        this.bLabel = createLabel("b");
        this.cLabel = createLabel("c");
        this.aType = createLabel("type:a");
        this.bType = createLabel("type:b");
        this.aAtom = atom("a");
        this.bAtom = atom("b");
        this.cAtom = atom("c");
    }

    /** Tests the construction of atoms. */
    @Test
    public void testAtom() {
        assertFalse(RegExpr.isAtom(""));
        assertFalse(RegExpr.isAtom("a a"));
        assertFalse(RegExpr.isAtom("\"a\"a"));
        assertFalse(RegExpr.isAtom("<a>a"));
        testError("");
        testError("a a");
        testError("\"a");
        testError("<a");
        RegExpr e = parse("a");
        testBasic(e);
        assertEquals(e, atom("a"));
        assertEquals(e, parse("(a)"));
        assertFalse(e.equals(parse("type:a")));
        assertTrue(e.isAtom());
        assertEquals("a", e.getAtomText());
        assertEquals(Collections.emptyList(), e.getOperands());
        assertEquals("", e.getOperator());
        assertTrue(e.toLabel().isAtom());
        assertEquals(this.aLabel, ((Atom) e).toTypeLabel());
        assertTrue(e.allVarSet().isEmpty());
        assertTrue(e.boundVarSet().isEmpty());
        assertNull(e.getWildcardKind());
        // relabelling
        assertEquals(e, e.relabel(this.bLabel, this.bLabel));
        assertEquals(e, e.relabel(this.aType, this.bLabel));
        assertEquals(parse("b"), e.relabel(this.aLabel, this.bLabel));
        // a more complex atom
        e = parse("'a a'");
        assertEquals("a a", e.getAtomText());
        assertEquals("'a a'", e.toString());
        e = parse("<a a>");
        assertEquals("<a a>", e.getAtomText());
        e = parse("\"a a\"");
        assertEquals("\"a a\"", e.getAtomText());
    }

    /** Tests the construction of the empty expression. */
    @Test
    public void testEmpty() {
        testError("==");
        RegExpr e = parse("=");
        testBasic(e);
        assertEquals(e, RegExpr.empty());
        assertTrue(e.isEmpty());
        assertNull(e.getAtomText());
        assertEquals(Collections.emptyList(), e.getOperands());
        assertEquals("" + RegExpr.EMPTY_OPERATOR, e.getOperator());
        assertTrue(e.toLabel().isEmpty());
        // relabelling
        assertEquals(e, e.relabel(this.aLabel, this.bLabel));
    }

    /** Tests the construction of sharp labels. */
    @Test
    public void testSharp() {
        testError("##a");
        testError("#a");
        RegExpr e = parse("type:#a");
        testBasic(e);
        assertEquals(e, RegExpr.sharp(this.aType));
        assertTrue(e.isSharp());
        assertNull(e.getAtomText());
        assertEquals(e.getSharpLabel(), this.aType);
        assertNull(parse("a").getSharpLabel());
        assertEquals(Collections.emptyList(), e.getOperands());
        assertEquals("" + RegExpr.SHARP_OPERATOR, e.getOperator());
        assertTrue(e.toLabel().isSharp());
        // relabelling
        assertEquals(e, e.relabel(this.aLabel, this.bLabel));
        assertEquals(parse("type:#b"), e.relabel(this.aType, this.bType));
        assertEquals(parse("b"), e.relabel(this.aType, this.bLabel));
    }

    /** Tests the wildcard operator. */
    @Test
    public void testWildcard() {
        testError("a?a");
        testError("a?a?a");
        testError("?1");
        testError("?a a");
        testError("?[]");
        testError("?[a a]");
        testError("?[a.a]");
        testError("?['flag:a']");
        testError("?[a]a");
        // unnamed wildcard
        RegExpr e = parse("?");
        testBasic(e);
        assertEquals(e, wildcard(BINARY));
        assertTrue(e.isWildcard());
        assertEquals(BINARY, e.getWildcardKind());
        assertTrue(e.getWildcardId().getName().isEmpty());
        assertNull(e.getWildcardGuard().getLabels());
        assertTrue(e.getTypeLabels().isEmpty());
        assertEquals(Collections.emptyList(), e.getOperands());
        assertEquals("" + RegExpr.WILDCARD_OPERATOR, e.getOperator());
        assertTrue(e.toLabel().isWildcard());
        assertTrue(e.boundVarSet().isEmpty());
        assertTrue(e.allVarSet().isEmpty());
        // relabelling
        assertEquals(e, e.relabel(this.bLabel, this.aLabel));
        // named wildcard
        e = parse("flag:?x");
        testBasic(e);
        assertEquals(e, wildcard(FLAG, "x"));
        assertEquals(FLAG, e.getWildcardKind());
        assertEquals("x", e.getWildcardId().getName());
        assertEquals(Collections.singleton(new LabelVar("x", FLAG)),
            e.boundVarSet());
        assertEquals(Collections.singleton(new LabelVar("x", FLAG)),
            e.allVarSet());
        // wildcard with label constraint
        e = parse("?[a,b]");
        testBasic(e);
        assertEquals(e, wildcard(BINARY, null, false, "a", "b"));
        Set<TypeLabel> abFlags =
            new HashSet<>(Arrays.asList(this.aLabel, this.bLabel));
        assertEquals(abFlags, e.getTypeLabels());
        e = parse("type:?[^a,b]");
        testBasic(e);
        assertEquals(e, wildcard(NODE_TYPE, null, true, "a", "b"));
        Set<TypeLabel> abTypes =
            new HashSet<>(Arrays.asList(this.aType, this.bType));
        assertEquals(abTypes, e.getTypeLabels());
        // relabelling
        assertEquals(e, e.relabel(this.cLabel, this.aLabel));
        assertEquals(e, e.relabel(this.bLabel, this.aLabel));
        assertEquals(parse("type:?[^a]"), e.relabel(this.bType, this.aType));
        assertEquals(parse("type:?[^a]"), e.relabel(this.bType, this.cLabel));
    }

    /** Tests the inversion operator. */
    @Test
    public void testInv() {
        testError("-");
        RegExpr e = parse("-a");
        testBasic(e);
        assertEquals(e, this.aAtom.inv());
        assertTrue(e.isInv());
        assertFalse(e.isNeg());
        assertEquals(this.aAtom, e.getInvOperand());
        assertEquals(null, e.getNegOperand());
        assertEquals(Arrays.asList(this.aAtom), e.getOperands());
        assertEquals("" + RegExpr.INV_OPERATOR, e.getOperator());
        assertTrue(e.toLabel().isInv());
        // relabelling
        assertEquals(e, e.relabel(this.bLabel, this.aLabel));
        assertEquals(parse("-b"), e.relabel(this.aLabel, this.bLabel));
    }

    /** Tests the negation operator. */
    @Test
    public void testNeg() {
        testError("!");
        testError("a!");
        RegExpr e = parse("!a");
        testBasic(e);
        assertEquals(e, this.aAtom.neg());
        assertTrue(e.isNeg());
        assertFalse(e.isInv());
        assertEquals(null, e.getInvOperand());
        assertEquals(this.aAtom, e.getNegOperand());
        assertEquals(Arrays.asList(this.aAtom), e.getOperands());
        assertEquals("" + RegExpr.NEG_OPERATOR, e.getOperator());
        assertTrue(e.toLabel().isNeg());
        // relabelling
        assertEquals(e, e.relabel(this.bLabel, this.aLabel));
        assertEquals(parse("!b"), e.relabel(this.aLabel, this.bLabel));
    }

    /** Tests the choice operator. */
    @Test
    public void testChoice() {
        testError("|");
        testError("a|");
        testError("|a");
        RegExpr e = parse("a|b|b");
        testBasic(e);
        assertEquals(e, this.aAtom.choice(this.bAtom.choice(this.bAtom)));
        assertTrue(e.isChoice());
        assertFalse(e.isSeq());
        assertEquals(Arrays.asList(this.aAtom, this.bAtom, this.bAtom),
            e.getChoiceOperands());
        assertEquals(null, e.getSeqOperands());
        assertEquals(Arrays.asList(this.aAtom, this.bAtom, this.bAtom),
            e.getOperands());
        assertEquals("" + RegExpr.CHOICE_OPERATOR, e.getOperator());
        assertTrue(e.toLabel().isChoice());
        // relabelling
        assertEquals(e, e.relabel(this.cLabel, this.aLabel));
        assertEquals(parse("a|c|c"), e.relabel(this.bLabel, this.cLabel));
    }

    /** Tests the sequential operator. */
    @Test
    public void testSeq() {
        testError(".");
        testError("a.");
        testError(".a");
        RegExpr e = parse("a.b.b");
        testBasic(e);
        assertEquals(e, this.aAtom.seq(this.bAtom.seq(this.bAtom)));
        assertFalse(e.isChoice());
        assertTrue(e.isSeq());
        assertEquals(null, e.getChoiceOperands());
        assertEquals(Arrays.asList(this.aAtom, this.bAtom, this.bAtom),
            e.getSeqOperands());
        assertEquals(Arrays.asList(this.aAtom, this.bAtom, this.bAtom),
            e.getOperands());
        assertEquals("" + RegExpr.SEQ_OPERATOR, e.getOperator());
        assertTrue(e.toLabel().isSeq());
        // relabelling
        assertEquals(e, e.relabel(this.cLabel, this.aLabel));
        assertEquals(parse("a.c.c"), e.relabel(this.bLabel, this.cLabel));
    }

    /** Tests the star suffix operator. */
    @Test
    public void testStar() {
        testError("*");
        testError("*a");
        RegExpr e = parse("a*");
        testBasic(e);
        assertEquals(e, this.aAtom.star());
        assertTrue(e.isStar());
        assertFalse(e.isPlus());
        assertNull(e.getPlusOperand());
        assertEquals(this.aAtom, e.getStarOperand());
        assertEquals(Arrays.asList(this.aAtom), e.getOperands());
        assertEquals("" + RegExpr.STAR_OPERATOR, e.getOperator());
        // relabelling
        assertEquals(e, e.relabel(this.bLabel, this.aLabel));
        assertEquals(parse("b*"), e.relabel(this.aLabel, this.bLabel));
    }

    /** Tests the plus suffix operator. */
    @Test
    public void testPlus() {
        testError("+");
        testError("+a");
        RegExpr e = parse("a+");
        testBasic(e);
        assertEquals(e, this.aAtom.plus());
        assertTrue(e.isPlus());
        assertFalse(e.isStar());
        assertEquals(this.aAtom, e.getPlusOperand());
        assertNull(e.getStarOperand());
        assertEquals(Arrays.asList(this.aAtom), e.getOperands());
        assertEquals("" + RegExpr.PLUS_OPERATOR, e.getOperator());
        // relabelling
        assertEquals(e, e.relabel(this.bLabel, this.aLabel));
        assertEquals(parse("b+"), e.relabel(this.aLabel, this.bLabel));
    }

    /** Tests some more complex expressions. */
    @Test
    public void testComplex() {
        testError("*");
        testError("((a)");
        testError("(<a)");
        RegExpr e = parse("((a).(b))*");
        testBasic(e);
        assertEquals(e, this.aAtom.seq(this.bAtom).star());
        e = parse("((a)*|type:#b)+");
        testBasic(e);
        assertEquals(e,
            this.aAtom.star().choice(RegExpr.sharp(this.bType)).plus());
        e = parse("?.'b.c'. 'b'. \"c\". (d*)");
        testBasic(e);
        assertEquals(
            e,
            wildcard(BINARY).seq(
                atom("b.c").seq(
                    this.bAtom.seq(atom("\"c\"").seq(atom("d").star())))));
        e = parse("a+*");
        testBasic(e);
        assertEquals(e, this.aAtom.plus().star());
        e = parse("a.?*");
        testBasic(e);
        assertEquals(e, this.aAtom.seq(wildcard(BINARY).star()));
        e = parse("(a . b)* .c. d|e*");
        testBasic(e);
        assertEquals(
            e,
            this.aAtom.seq(this.bAtom).star().seq(this.cAtom.seq(atom("d"))).choice(
                atom("e").star()));
        e = parse("=. flag:?|c*");
        testBasic(e);
        assertEquals(e, empty().seq(wildcard(FLAG)).choice(this.cAtom.star()));
        e = parse("!a*");
        testBasic(e);
        assertEquals(e, this.aAtom.star().neg());
        e = parse("!a.flag:?x[a] | (!a.(!type:?[^b]))");
        testBasic(e);
        assertEquals(
            e,
            this.aAtom.seq(wildcard(FLAG, "x", false, "a")).choice(
                this.aAtom.seq(wildcard(NODE_TYPE, true, "b").neg()).neg()).neg());
    }

    /** Tests the basic methods of a regular expression. */
    private void testBasic(RegExpr e) {
        assertEquals(e, e);
        assertFalse(e.equals(null));
        assertFalse(e.equals(""));
        assertNotNull(e.toString());
        assertNotNull(e.getDescription());
        assertEquals(e, e.toLabel().getMatchExpr());
        assertTrue(e.getOperands().isEmpty()
            || e.containsOperator(e.getOperands().iterator().next()));
    }

    /** Tests that a string cannot be parsed as regular expression. */
    private void testError(String s) {
        try {
            RegExpr.parse(s);
            fail();
        } catch (FormatException e) {
            // success
        }
    }

    private RegExpr parse(String s) {
        RegExpr result = null;
        try {
            result = RegExpr.parse(s);
            RegExpr other = RegExpr.parse(s);
            assertEquals(result, other);
            assertEquals(result.hashCode(), other.hashCode());
        } catch (FormatException e) {
            fail("Can't parse expression " + s);
        }
        return result;
    }
}
