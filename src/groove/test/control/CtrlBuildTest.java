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
import groove.control.CtrlAut;
import groove.control.CtrlCall;
import groove.control.CtrlFactory;
import groove.control.CtrlLabel;
import groove.control.CtrlLoader;
import groove.control.CtrlSchedule;
import groove.control.CtrlTransition;
import groove.io.ExtensionFilter;
import groove.trans.GraphGrammar;
import groove.trans.SPORule;
import groove.util.Groove;
import groove.view.FormatException;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

/**
 * Tests the revised control automaton building.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CtrlBuildTest {
    private static final String GRAMMAR_DIR = "junit/samples/";
    private static final String CONTROL_DIR = "junit/control/";
    private static final ExtensionFilter CONTROL_FILTER =
        Groove.createControlFilter();

    private final CtrlLoader parser = CtrlLoader.getInstance();
    private GraphGrammar testGrammar;
    {
        try {
            this.testGrammar =
                Groove.loadGrammar(GRAMMAR_DIR + "emptyrules").toGrammar();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    private GraphGrammar prioGrammar;
    {
        try {
            this.prioGrammar =
                Groove.loadGrammar(GRAMMAR_DIR + "emptypriorules").toGrammar();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
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
        CtrlAut aut = CtrlFactory.instance().buildDefault(this.prioGrammar);
        assertEquals(2, aut.nodeCount());
        assertEquals(7, aut.edgeCount());
        SPORule m3 = (SPORule) this.prioGrammar.getRule("m3");
        SPORule m2 = (SPORule) this.prioGrammar.getRule("m2");
        SPORule m1 = (SPORule) this.prioGrammar.getRule("m1");
        SPORule c3 = (SPORule) this.prioGrammar.getRule("c3");
        SPORule c2 = (SPORule) this.prioGrammar.getRule("c2");
        SPORule c1 = (SPORule) this.prioGrammar.getRule("c1");
        CtrlCall callM3 = new CtrlCall(m3, null);
        CtrlCall callM2 = new CtrlCall(m2, null);
        CtrlCall callM1 = new CtrlCall(m1, null);
        CtrlCall callC3 = new CtrlCall(c3, null);
        CtrlCall callC2 = new CtrlCall(c2, null);
        CtrlCall callC1 = new CtrlCall(c1, null);
        CtrlCall omega = CtrlCall.OMEGA;
        Set<CtrlCall> level2AllGuard = new HashSet<CtrlCall>();
        level2AllGuard.add(callM3);
        level2AllGuard.add(callC3);
        Set<CtrlCall> level1AllGuard = new HashSet<CtrlCall>(level2AllGuard);
        level1AllGuard.add(callM2);
        level1AllGuard.add(callC2);
        Set<CtrlCall> omegaGuard = new HashSet<CtrlCall>();
        omegaGuard.add(callM1);
        omegaGuard.add(callM2);
        omegaGuard.add(callM3);
        Set<CtrlLabel> expectedSelfLabels =
            new HashSet<CtrlLabel>(Arrays.asList(new CtrlLabel[] {
                new CtrlLabel(callM3), new CtrlLabel(callC3),
                new CtrlLabel(callM2, level2AllGuard),
                new CtrlLabel(callC2, level2AllGuard),
                new CtrlLabel(callM1, level1AllGuard),
                new CtrlLabel(callC1, level1AllGuard),}));
        Set<CtrlLabel> expectedOmegaLabels =
            new HashSet<CtrlLabel>(
                Arrays.asList(new CtrlLabel[] {new CtrlLabel(omega, omegaGuard)}));
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
        buildCorrect("f(); function f() { a; }", 3, 2);
        buildCorrect("f(); f(); function f() { choice a; or {b;c;} }", 6, 7);
        buildCorrect("f(); function f() { node x; bNode(out x); }", 3, 2);
        buildWrong("function f() { g(); } function g() { f(); }");
        buildCorrect("function f() { a | g(); } function g() { b; c; } f(); ",
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
        assertEquals("c", getName(s1f));
        assertTrue(s1f.isSuccess());
        CtrlSchedule s1ff = s1f.next(false);
        assertEquals("e", getName(s1ff));
        assertTrue(s1ff.isSuccess());
        CtrlSchedule s1fff = s1ff.next(false);
        assertEquals("d", getName(s1fff));
        assertTrue(s1fff.isSuccess());
        assertSame(s1fff.next(false), s1fff.next(true));
        assertTrue(s1fff.next(false).isFinished());
        CtrlSchedule s1ft = s1f.next(true);
        assertEquals("e", getName(s1ft));
        assertTrue(s1ft.isSuccess());
        assertSame(s1ft.next(false), s1ft.next(true));
        assertTrue(s1ft.next(false).isFinished());
        CtrlSchedule s1t = s1.next(true);
        assertEquals("c", getName(s1t));
        CtrlSchedule s1tf = s1t.next(false);
        assertEquals("e", getName(s1tf));
        CtrlSchedule s1tff = s1tf.next(false);
        assertEquals("d", getName(s1tff));
        assertSame(s1tff.next(false), s1tff.next(true));
        assertTrue(s1tff.next(false).isFinished());
        assertFalse(s1tff.next(false).isSuccess());
        CtrlSchedule s1tt = s1t.next(true);
        assertEquals("e", getName(s1tt));
        assertSame(s1tt.next(false), s1tt.next(true));
        assertTrue(s1tt.next(true).isFinished());
        assertFalse(s1tt.next(false).isSuccess());
    }

    private String getName(CtrlSchedule s) {
        return s.getTransition().getCall().getName();
    }

    /** Builds a control automaton that should contain an error. */
    private void buildWrong(String program) {
        buildWrong(program, false);
    }

    /** Builds a control automaton that should contain an error. */
    private void buildWrong(String name, boolean file) {
        try {
            CtrlAut aut;
            if (file) {
                aut = buildFile(name);
            } else {
                aut = buildString(name);
            }
            System.err.printf("%s builds without errors: %n%s%n", name,
                aut.toString());
            assertTrue(false);
        } catch (FormatException e) {
            if (DEBUG) {
                System.out.println(e.getMessage());
            }
        }
    }

    private CtrlAut buildCorrect(String name, int nodeCount, int edgeCount) {
        return buildCorrect(name, false, nodeCount, edgeCount);
    }

    private CtrlAut buildCorrect(String name, boolean file, int nodeCount,
            int edgeCount) {
        CtrlAut result = null;
        try {
            result = file ? buildFile(name) : buildString(name);
            assertEquals(nodeCount, result.nodeCount());
            assertEquals(edgeCount, result.edgeCount());
        } catch (FormatException e) {
            System.err.printf("Errors in %s:%n%s%n", name, e.getMessage());
            fail();
        }
        return result;
    }

    /** Builds a control automaton from a file with a given name. */
    private CtrlAut buildFile(String filename) throws FormatException {
        CtrlAut result = null;
        try {
            result =
                this.parser.runFile(
                    CONTROL_FILTER.addExtension(CONTROL_DIR + filename),
                    this.testGrammar);
            if (DEBUG) {
                System.out.printf("Control automaton for %s:%n%s%n", filename,
                    result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /** Builds a control automaton from a given program. */
    private CtrlAut buildString(String program) throws FormatException {
        CtrlAut result = null;
        result = this.parser.runString(program, this.testGrammar);
        if (DEBUG) {
            System.out.printf("Control automaton for \'%s\':%n%s%n", program,
                result);
        }
        return result;
    }

    static private final boolean DEBUG = false;
}
