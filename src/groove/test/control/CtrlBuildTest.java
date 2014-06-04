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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import groove.control.CtrlAut;
import groove.control.CtrlCall;
import groove.control.CtrlFactory;
import groove.control.CtrlGuard;
import groove.control.CtrlLabel;
import groove.control.CtrlState;
import groove.control.CtrlTransition;
import groove.grammar.Grammar;
import groove.grammar.GrammarProperties;
import groove.grammar.Rule;
import groove.grammar.model.FormatException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

/**
 * Tests the revised control automaton building.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CtrlBuildTest extends CtrlTester {
    {
        initGrammar("emptyrules");
        this.prioGrammar = loadGrammar("emptypriorules");
    }

    private Grammar prioGrammar;

    /** Regression test for errors found in old control programs. */
    @Test
    public void testRegression() {
        buildCorrect("alap {\n alap { a| b;\n } c;\n}\n", 2, 6);
        buildCorrect(
            "node x,y; bNode(out x); bNode; bNode(out y); try bNode(x); else bNode-bNode(x,y);", 5,
            5);
        buildCorrect("node x; bNode(out x); node y; bNode-oNode(x, out y); bNode-bNode(x,y);", 4, 3);
        buildCorrect("choice { try a; } or { if (e|c) c; else d; } or { b;b;}", 4, 7);

    }

    /** Tests the default automaton construction. */
    @Test
    public void testDefaultAut() {
        CtrlAut aut = null;
        try {
            aut =
                CtrlFactory.instance().buildDefault(this.prioGrammar.getActions(),
                    new GrammarProperties());
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
        CtrlTransition transC3 = first.addTransition(createLabel(callC3, emptyGuard), first);
        CtrlTransition transM3 = first.addTransition(createLabel(callM3, emptyGuard), first);
        CtrlGuard level2AllGuard = new CtrlGuard();
        level2AllGuard.add(transM3);
        level2AllGuard.add(transC3);
        CtrlTransition transC2 = first.addTransition(createLabel(callC2, level2AllGuard), first);
        CtrlTransition transM2 = first.addTransition(createLabel(callM2, level2AllGuard), first);
        CtrlGuard level1AllGuard = new CtrlGuard();
        level1AllGuard.addAll(level2AllGuard);
        level1AllGuard.add(transM2);
        level1AllGuard.add(transC2);
        CtrlTransition transC1 = first.addTransition(createLabel(callC1, level1AllGuard), first);
        CtrlTransition transM1 = first.addTransition(createLabel(callM1, level1AllGuard), first);
        CtrlGuard omegaGuard = new CtrlGuard();
        omegaGuard.addAll(Arrays.asList(transM1, transM2, transM3, transC1, transC2, transC3));
        Set<CtrlLabel> expectedSelfLabels =
            new HashSet<CtrlLabel>(Arrays.asList(transM1.label(), transM2.label(), transM3.label(),
                transC1.label(), transC2.label(), transC3.label()));
        CtrlTransition omega =
            first.addTransition(createLabel(CtrlCall.OMEGA_CALL, omegaGuard), expected.getFinal());
        Set<CtrlLabel> expectedOmegaLabels = new HashSet<CtrlLabel>(Arrays.asList(omega.label()));
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
    public void testDirectionErrors() {
        buildWrong("node x; bNode(out x); oNode(x);");
        buildWrong("int x; iInt(out x);");
        buildWrong("oNode(_)");
    }

    /** Tests building various loop structures. */
    @Test
    public void testLoops() {
        buildCorrect("while (a|b) { c; d; }", 3, 4);
        buildCorrect("until (a|b) { c; d; }", 3, 4);
        buildCorrect("alap { choice { a; b; } or c; } d;", 3, 4);
        buildCorrect("(a|b)*;", 1, 2);
    }

    /** Sequences of rule calls. */
    @Test
    public void testSeq() {
        buildCorrect("a;", 2, 1);
        buildCorrect("a; b; a;", 4, 3);
        buildCorrect("bNode(_); bNode-oNode(_,_);", 3, 2);
        buildCorrect("node x; bNode(out x); bNode-oNode(x, out x);", 3, 2);
    }

    /** Tests building if statements. */
    @Test
    public void testIf() {
        buildWrong("a|a");
        buildCorrect("if (a|b) c;", 3, 3);
        buildCorrect("if (a|b) c; d;", 4, 5);
        buildCorrect("if (a|b) c; else d;", 3, 4);
    }

    /** Tests building try statements. */
    @Test
    public void testTry() {
        buildCorrect("try { a;b; } d;", 4, 4);
        buildCorrect("try { a; b; } else { c; } d;", 4, 4);
    }

    /** Tests the {@code any} and {@code other} statements. */
    @Test
    public void testAnyOther() {
        buildWrong("any;");
        buildCorrect("node x; bNode(out x); iNode(x); iInt(3); iString-oNode(\"a\",_); other;", 6,
            14);
    }

    /** Tests function calls. */
    @Test
    public void testFunctions() {
        buildCorrect("function f() { a; } f(); ", 2, 1);
        buildCorrect("function f() { choice a; or {b;c;} } f(); f(); ", 5, 6);
        buildCorrect("function f() { node x; bNode(out x); } f(); ", 2, 1);
        buildCorrect("function g() { b; c; } function f() { a | g(); } f(); ", 3, 3);
    }
}
