/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.test.control;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import groove.algebra.AlgebraFamily;
import groove.control.CtrlAut;
import groove.control.CtrlCall;
import groove.control.CtrlFactory;
import groove.control.CtrlGuard;
import groove.control.CtrlLabel;
import groove.control.CtrlSchedule;
import groove.control.CtrlState;
import groove.control.CtrlTransition;
import groove.grammar.Grammar;
import groove.grammar.Rule;
import groove.grammar.model.FormatException;
import groove.util.Groove;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

/**
 * Tests the revised control automaton building.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CtrlBuildTest extends CtrlTester {
    private static final String GRAMMAR_DIR = "junit/control/";

    private Grammar prioGrammar;

    {
        try {
            this.prioGrammar =
                Groove.loadGrammar(GRAMMAR_DIR + "emptypriorules").toGrammar();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /** Regression test for errors found in old control programs. */
    @Test
    public void testRegression() {
        buildCorrect("alap {\n alap { a| b;\n } c;\n}\n", 3, 7);
        CtrlAut aut =
            buildCorrect(
                "node x,y; bNode(out x); bNode; bNode(out y); try bNode(x); else bNode-bNode(x,y);",
                6, 6);
        CtrlTransition t1 = aut.getStart().getTransitions().iterator().next();
        CtrlTransition t2 = t1.target().getTransitions().iterator().next();
        CtrlTransition t3 = t2.target().getTransitions().iterator().next();
        assertEquals(2, t3.target().getBoundVars().size());
    }

    /** Tests the default automaton construction. */
    @Test
    public void testDefaultAut() {
        CtrlAut aut = null;
        try {
            aut =
                CtrlFactory.instance().buildDefault(
                    this.prioGrammar.getActions(), AlgebraFamily.DEFAULT);
        } catch (FormatException e) {
            fail();
        }
        assertEquals(2, aut.nodeCount());
        assertEquals(7, aut.edgeCount());
        CtrlAut expected = new CtrlAut("expected");
        Rule m3 = this.prioGrammar.getRule("m3");
        Rule m2 = this.prioGrammar.getRule("m2");
        Rule m1 = this.prioGrammar.getRule("m1");
        Rule c3 = this.prioGrammar.getRule("c3");
        Rule c2 = this.prioGrammar.getRule("c2");
        Rule c1 = this.prioGrammar.getRule("c1");
        CtrlCall callM3 = new CtrlCall(m3, null);
        CtrlCall callM2 = new CtrlCall(m2, null);
        CtrlCall callM1 = new CtrlCall(m1, null);
        CtrlCall callC3 = new CtrlCall(c3, null);
        CtrlCall callC2 = new CtrlCall(c2, null);
        CtrlCall callC1 = new CtrlCall(c1, null);
        CtrlState first = expected.getStart();
        CtrlGuard emptyGuard = new CtrlGuard();
        CtrlTransition transM3 =
            first.addTransition(createLabel(callM3, emptyGuard), first);
        CtrlTransition transC3 =
            first.addTransition(createLabel(callC3, emptyGuard), first);
        CtrlGuard level2AllGuard = new CtrlGuard();
        level2AllGuard.add(transM3);
        level2AllGuard.add(transC3);
        CtrlTransition transC2 =
            first.addTransition(createLabel(callC2, level2AllGuard), first);
        CtrlTransition transM2 =
            first.addTransition(createLabel(callM2, level2AllGuard), first);
        CtrlGuard level1AllGuard = new CtrlGuard();
        level1AllGuard.addAll(level2AllGuard);
        level1AllGuard.add(transM2);
        level1AllGuard.add(transC2);
        CtrlTransition transC1 =
            first.addTransition(createLabel(callC1, level1AllGuard), first);
        CtrlTransition transM1 =
            first.addTransition(createLabel(callM1, level1AllGuard), first);
        CtrlGuard omegaGuard = new CtrlGuard();
        omegaGuard.addAll(Arrays.asList(transM1, transM2, transM3, transC1,
            transC2, transC3));
        Set<CtrlLabel> expectedSelfLabels =
            new HashSet<CtrlLabel>(Arrays.asList(transM1.label(),
                transM2.label(), transM3.label(), transC1.label(),
                transC2.label(), transC3.label()));
        CtrlTransition omega =
            first.addTransition(createLabel(CtrlCall.OMEGA_CALL, omegaGuard),
                expected.getFinal());
        Set<CtrlLabel> expectedOmegaLabels =
            new HashSet<CtrlLabel>(Arrays.asList(omega.label()));
        Set<CtrlLabel> actualSelfLabels = new HashSet<CtrlLabel>();
        Set<CtrlLabel> actualOmegaLabels = new HashSet<CtrlLabel>();
        for (CtrlTransition trans : aut.getStart().getTransitions()) {
            if (trans.target() == aut.getStart()) {
                actualSelfLabels.add(trans.label());
            } else {
                assertSame(trans.target(), aut.getFinal());
                actualOmegaLabels.add(trans.label());
            }
        }
        assertEquals(expectedSelfLabels, actualSelfLabels);
        assertEquals(expectedOmegaLabels, actualOmegaLabels);
    }

    private CtrlLabel createLabel(CtrlCall call, CtrlGuard guard) {
        return new CtrlLabel(call, guard, true);
    }

    /** Test for initialisation errors. */
    @Test
    public void testInitErrors() {
        buildWrong("node x; if (a) bNode(out x); bNode(x);");
        buildWrong("node x; bNode(x);");
    }

    /** Test for typing errors. */
    @Test
    public void testTypeErrors() {
        buildWrong("bInt(\"string\");");
        buildWrong("node x; bInt(out x);");
        buildWrong("a(_);");
    }

    /** Test for in/output parameter errors. */
    @Test
    public void testDirErrors() {
        buildWrong("node x; bNode(out x); oNode(x);");
        buildWrong("int x; iInt(out x);");
        buildWrong("oNode(_)");
    }

    /** Tests building various loop structures. */
    @Test
    public void testLoops() {
        buildCorrect("while (a|b) { c; d; }", 4, 5);
        buildCorrect("until (a|b) { c; d; }", 4, 5);
        buildCorrect("alap { choice { a; b; } or c; } d;", 4, 5);
        buildCorrect("(a|b)*;", 2, 3);
    }

    /** Sequences of rule calls. */
    @Test
    public void testSeq() {
        buildCorrect("a;", 3, 2);
        buildCorrect("a; b; a;", 5, 4);
        buildCorrect("bNode(_); bNode-oNode(_,_);", 4, 3);
        buildCorrect("node x; bNode(out x); bNode-oNode(x, out x);", 4, 3);
    }

    /** Tests building if statements. */
    @Test
    public void testIf() {
        buildWrong("a|a");
        buildCorrect("if (a|b) c;", 4, 5);
        buildCorrect("if (a|b) c; d;", 5, 6);
        buildCorrect("if (a|b) c; else d;", 4, 5);
    }

    /** Tests building try statements. */
    @Test
    public void testTry() {
        buildCorrect("try { a;b; } d;", 5, 5);
        buildCorrect("try { a; b; } else { c; } d;", 5, 5);
    }

    /** Tests the {@code any} and {@code other} statements. */
    @Test
    public void testAnyOther() {
        buildWrong("any;");
        buildCorrect(
            "node x; bNode(out x); iNode(x); iInt(3); iString-oNode(\"a\",_); other;",
            7, 15);
    }

    /** Tests function calls. */
    @Test
    public void testFunctions() {
        buildCorrect("function f() { a; } f(); ", 3, 2);
        buildCorrect("function f() { choice a; or {b;c;} } f(); f(); ", 6, 7);
        buildCorrect("function f() { node x; bNode(out x); } f(); ", 3, 2);
        buildWrong("function f() { g(); } function g() { f(); }");
        buildCorrect("function g() { b; c; } function f() { a | g(); } f(); ",
            4, 4);
    }

    /** Tests the variable binding. */
    @Test
    public void testVarBinding() {
        CtrlAut aut =
            buildCorrect(
                "node x; bNode(out x); node y; bNode-oNode(x, out y); bNode-bNode(x,y);",
                5, 4);
        CtrlTransition first =
            aut.getStart().getTransitions().iterator().next();
        CtrlTransition second =
            first.target().getTransitions().iterator().next();
        CtrlTransition third =
            second.target().getTransitions().iterator().next();
        CtrlTransition fourth =
            third.target().getTransitions().iterator().next();
        assertEquals(1, first.target().getBoundVars().size());
        assertEquals(2, second.target().getBoundVars().size());
        assertEquals(0, third.target().getBoundVars().size());
        assertEquals(0, fourth.target().getBoundVars().size());
        int[] targetVarBinding = second.getTargetVarBinding();
        assertEquals(2, targetVarBinding.length);
        assertEquals(0, targetVarBinding[0]);
        assertEquals(2, targetVarBinding[1]);
        int[] parBinding = second.getParBinding();
        assertEquals(2, parBinding.length);
        assertEquals(0, parBinding[0]);
        assertEquals(1, parBinding[1]);
    }

    /** Tests the transition scheduling. */
    @Test
    public void testSchedule() {
        CtrlAut aut =
            buildCorrect(
                "choice { try a; } or { if (e|c) c; else d; } or { b;b;}", 5, 9);
        CtrlSchedule s0 = aut.getStart().getSchedule();
        assertEquals("b", getName(s0));
        CtrlSchedule s1 = s0.next(false);
        assertEquals("a", getName(s1));
        assertTrue(s1 == s0.next(true));
        CtrlSchedule s1f = s1.next(false);
        assertTrue(s1f.isSuccess());
        assertEquals(Arrays.asList("c", "e"), getNames(s1f));
        CtrlSchedule s1ff = s1f.next(false);
        assertEquals("d", getName(s1ff));
        assertTrue(s1ff.isSuccess());
        assertSame(s1ff.next(false), s1ff.next(true));
        assertTrue(s1ff.next(false).isFinished());
        CtrlSchedule s1ft = s1f.next(true);
        assertTrue(s1ft.isSuccess());
        assertTrue(s1ft.isFinished());
        CtrlSchedule s1t = s1.next(true);
        assertEquals(Arrays.asList("c", "e"), getNames(s1t));
        CtrlSchedule s1tf = s1t.next(false);
        assertEquals("d", getName(s1tf));
        assertSame(s1tf.next(false), s1tf.next(true));
        assertTrue(s1tf.next(false).isFinished());
        assertFalse(s1tf.next(false).isSuccess());
        CtrlSchedule s1tt = s1t.next(true);
        assertTrue(s1tt.isFinished());
        assertFalse(s1tt.isSuccess());
    }

    private String getName(CtrlSchedule s) {
        return s.getTransitions().get(0).getCall().getName();
    }

    private List<String> getNames(CtrlSchedule s) {
        List<String> result = new ArrayList<String>();
        for (CtrlTransition trans : s.getTransitions()) {
            result.add(trans.getCall().getName());
        }
        return result;
    }
}
