/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import nl.utwente.groove.algebra.syntax.Expression;
import nl.utwente.groove.control.Assignment;
import nl.utwente.groove.control.Binding;
import nl.utwente.groove.control.Call;
import nl.utwente.groove.control.CtrlArg;
import nl.utwente.groove.control.CtrlLoader;
import nl.utwente.groove.control.CtrlType;
import nl.utwente.groove.control.CtrlVar;
import nl.utwente.groove.control.Position;
import nl.utwente.groove.control.Procedure;
import nl.utwente.groove.control.instance.Automaton;
import nl.utwente.groove.control.instance.CallStackChange;
import nl.utwente.groove.control.instance.Frame;
import nl.utwente.groove.control.instance.Step;
import nl.utwente.groove.control.instance.StepAttempt;
import nl.utwente.groove.control.template.Fragment;
import nl.utwente.groove.control.template.NestedSwitch;
import nl.utwente.groove.control.template.Program;
import nl.utwente.groove.control.template.Switch;
import nl.utwente.groove.grammar.Callable;
import nl.utwente.groove.grammar.Grammar;
import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.gui.Viewer;
import nl.utwente.groove.util.Groove;
import nl.utwente.groove.util.parse.FormatException;

/**
 * @author Arend Rensink
 * @version $Revision$
 */
@SuppressWarnings("javadoc")
public class AutomatonBuildTest {
    /** The directory from which grammars are loaded. */
    public static final String CONTROL_DIR = "junit/control/";

    @Before
    public void init() {
        initGrammar("emptyrules");
    }

    @Test
    public void testNesting() {
        add("f", "function f() { node arg; choice try r(1,out arg); or b; }");
        add("r",
            "recipe r(int p, out node q) { choice oNode(out q); or { bNode(out q); bInt(p); } }");
        add("main", "f|a; ");
        Automaton p = build();
        p.explore();
        if (DEBUG) {
            Viewer.showGraph(p.toGraph(FULL_GRAPH), true);
        }
        Frame f = p.getStart();
        assertEquals(Position.Type.TRIAL, f.getType());
        StepAttempt s = f.getAttempt();
        assertEquals(2, s.size());
        Call fCall = call("f");
        Call aCall = call("a");
        Call bCall = call("b");
        assertEquals(2, s.get(0).getStack().size());
        assertEquals(fCall, s.get(0).getStack().getOutermostCall());
        assertEquals(bCall, s.get(0).getInnermostCall());
        assertEquals(aCall, s.get(1).getInnermostCall());
        assertEquals(s.onFailure(), s.onSuccess());
        //
        Frame fFail = s.onFailure();
        StepAttempt sFail = fFail.getAttempt();
        assertEquals(2, sFail.size());
        Call rCall = call("r", new CtrlArg.Expr(Expression.i(1)),
                          CtrlArg.outVar(QualName.parse("f"), "arg", "node"));
        Call oNodeCall = call("oNode", CtrlArg.outVar(QualName.parse("r"), "q", "node"));
        Call bNodeCall = call("bNode", CtrlArg.outVar(QualName.parse("r"), "q", "node"));
        NestedSwitch swFail0 = sFail.get(0).getStack();
        var swFail0Iter = swFail0.iterator();
        assertEquals(fCall, swFail0Iter.next().getCall());
        assertEquals(rCall, swFail0Iter.next().getCall());
        assertEquals(oNodeCall, swFail0Iter.next().getCall());
        assertEquals(bNodeCall, sFail.get(1).getInnermostCall());
        //
        Frame fFailFail = sFail.onFailure();
        assertEquals(0, fFailFail.getContextStack().size());
        assertTrue(fFailFail.isFinal());
        //
        Frame fFailSucc = sFail.onSuccess();
        assertTrue(fFailSucc.isDead());
        assertTrue(sFail.get(0).onFinish().isFinal());
        Frame fNext = sFail.get(1).onFinish();
        NestedSwitch swNext = fNext.getContextStack();
        assertEquals(2, swNext.size());
        var swNextIter = swNext.iterator();
        assertEquals(fCall, swNextIter.next().getCall());
        assertEquals(rCall, swNextIter.next().getCall());
        StepAttempt sNext = fNext.getAttempt();
        assertEquals(1, sNext.size());
        Call bIntCall = call("bInt", CtrlArg.inVar(QualName.parse("r"), "p", "int"));
        assertEquals(bIntCall, sNext.get(0).getInnermostCall());
    }

    @Test
    public void testBinding() {
        add("f", "function f(node fx, out node fy) { g(fx, out fy); iInt(1); h(fx); h(fy); }");
        add("g", "function g(node gx, out node gy) { bNode-oNode(out gx, out gy); bNode(gx); }");
        add("h", "function h(node hx) { bNode(hx); }");
        add("main", "node n; oNode(out n); f(n, out n);");
        Automaton p = build();
        p.explore();
        if (DEBUG) {
            Viewer.showGraph(p.toGraph(FULL_GRAPH), true);
        }
        Frame f0 = p.getStart();
        assertEquals(1, f0.getAttempt().size());
        Step s0 = f0.getAttempt().get(0);
        Frame f1 = s0.onFinish();
        assertEquals(1, f1.getAttempt().size());
        Step s1 = f1.getAttempt().get(0);
        Frame f2 = s1.onFinish();
        assertEquals(1, f2.getAttempt().size());
        Step s2 = f2.getAttempt().get(0);
        Frame f3 = s2.onFinish();
        assertEquals(1, f3.getAttempt().size());
        Step s3 = f3.getAttempt().get(0);
        Frame f4 = s3.onFinish();
        assertEquals(1, f4.getAttempt().size());
        Step s4 = f4.getAttempt().get(0);
        Frame f5 = s4.onFinish();
        assertEquals(1, f5.getAttempt().size());
        Step s5 = f5.getAttempt().get(0);
        Frame f6 = s5.onFinish();
        //
        var n = new CtrlVar(null, "n", CtrlType.NODE);
        var fx = new CtrlVar(new QualName("f"), "fx", CtrlType.NODE);
        var fy = new CtrlVar(new QualName("f"), "fy", CtrlType.NODE);
        var gx = new CtrlVar(new QualName("g"), "gx", CtrlType.NODE);
        var gy = new CtrlVar(new QualName("g"), "gy", CtrlType.NODE);
        //
        assertEquals(0, s0.getCallDepthChange());
        var change = s0.getApplyChange().toList();
        assertEquals(1, change.size());
        var aMain = new Assignment(Binding.creator(n, 0));
        assertEquals(CallStackChange.push(aMain), change.get(0));
        //
        assertEquals(2, s1.getCallDepthChange());
        change = s1.getApplyChange().toList();
        assertEquals(1, change.size());
        aMain = new Assignment(Binding.none(n));
        var aF = new Assignment(Binding.var(fx, 0), Binding.none(fy));
        var aG = new Assignment(Binding.anchor(gx, 0), Binding.creator(gy, 0));
        assertEquals(CallStackChange.push(aMain, aF, aG), change.get(0));
        //
        assertEquals(-1, s2.getCallDepthChange());
        change = s2.getApplyChange().toList();
        assertEquals(2, change.size());
        aG = new Assignment(Binding.var(gy, 1));
        assertEquals(CallStackChange.push(aG), change.get(0));
        aF = new Assignment(Binding.none(fx), Binding.var(fy, 0));
        assertEquals(CallStackChange.pop(aF), change.get(1));
        //
        assertEquals(0, s3.getCallDepthChange());
        change = s3.getApplyChange().toList();
        assertEquals(1, change.size());
        aF = new Assignment(Binding.var(fx, 0), Binding.var(fy, 1));
        assertEquals(CallStackChange.push(aF), change.get(0));
        //
        assertEquals(0, s4.getCallDepthChange());
        change = s4.getApplyChange().toList();
        assertEquals(2, change.size());
        aF = new Assignment(Binding.var(fy, 1));
        var aH = new Assignment();
        assertEquals(CallStackChange.push(aF, aH), change.get(0));
        aF = new Assignment(Binding.none(fy));
        assertEquals(CallStackChange.pop(aF), change.get(1));
        //
        assertEquals(-1, s5.getCallDepthChange());
        change = s5.getApplyChange().toList();
        assertEquals(3, change.size());
        aF = new Assignment(Binding.var(fy, 0));
        aH = new Assignment();
        assertEquals(CallStackChange.push(aF, aH), change.get(0));
        aF = new Assignment(Binding.none(fy));
        assertEquals(CallStackChange.pop(aF), change.get(1));
        aMain = new Assignment(Binding.var(n, 0));
        assertEquals(CallStackChange.pop(aMain), change.get(2));
        //
        assertTrue(f6.isFinal());
    }

    @Test
    public void testNestedLoop() {
        Automaton p = build("alap-choice", "alap a|b;");
        p.explore();
        Frame f = p.getStart();
        assertEquals(2, f.getAttempt().size());
        assertTrue(f.getAttempt().onSuccess().isDead());
        assertTrue(f.getAttempt().onFailure().isFinal());
        Step s = f.getAttempt().get(0);
        assertEquals(f, s.onFinish());
        assertEquals(call("a"), s.getCall().getInner());
        s = f.getAttempt().get(1);
        assertEquals(call("b"), s.getCall().getInner());
        assertEquals(f, s.onFinish());
        //
        p = build("nested", "function f() { a; alap a; } recipe r() { f; alap f; } r;");
        p.explore();
        if (DEBUG) {
            Viewer.showGraph(p.toGraph(FULL_GRAPH), true);
        }
        NestedSwitch swt = new NestedSwitch();
        swt.push(this.prog.getTemplate().getStart().getAttempt().get(0).getOuter());
        Switch r0Switch = proc("r").getTemplate().getStart().getAttempt().get(0).getOuter();
        swt.push(r0Switch);
        Switch f0Switch = proc("f").getTemplate().getStart().getAttempt().get(0).getOuter();
        swt.push(f0Switch);
        Frame f0 = p.getStart();
        StepAttempt a0 = f0.getAttempt();
        assertEquals(1, a0.size());
        assertTrue(a0.onSuccess().isDead());
        assertEquals(a0.onFailure(), a0.onSuccess());
        Step s0 = a0.get(0);
        assertEquals(swt, s0.getStack());
        Frame f1 = s0.onFinish();
        StepAttempt a1 = f1.getAttempt();
        assertTrue(a1.onSuccess().isDead());
        assertEquals(1, a1.size());
        Step s1 = a1.get(0);
        Switch f1Switch = f0Switch.onFinish().getAttempt().get(0).getOuter();
        assertDistinct(f1Switch, f0Switch);
        NestedSwitch f1Stack = new NestedSwitch();
        f1Stack.push(f1Switch);
        assertEquals(f1Stack, s1.getStack());
        assertEquals(f1, s1.onFinish());
        Frame f2 = a1.onFailure();
        StepAttempt a2 = f2.getAttempt();
        Step s2 = a2.get(0);
        Switch r2Switch = r0Switch.onFinish().getAttempt().get(0).getOuter();
        assertDistinct(r2Switch, r0Switch);
        NestedSwitch r2Stack = new NestedSwitch();
        r2Stack.push(r2Switch);
        r2Stack.push(f0Switch);
        assertEquals(r2Stack, s2.getStack());
        assertDistinct(f1, s2.onFinish());
        assertTrue(a2.onSuccess().isDead());
        assertTrue(a2.onFailure().isFinal());
    }

    /** Builds an automaton consisting of a single non-trivial recipe. */
    @Test
    public void testSingleRecipe() {
        Automaton p = build("control", "recipe f() { a; #b; c; } alap f;");
        Frame f0 = p.getStart();
        StepAttempt a0 = f0.getAttempt();
        assertEquals(1, a0.size());
        assertTrue(a0.onSuccess().isDead());
        assertTrue(a0.onFailure().isFinal());
        Step s0 = a0.get(0);
        assertEquals(2, s0.getCall().depth());
        var s0Iter = s0.getCall().iterator();
        assertEquals(proc("f"), s0Iter.next().getUnit());
        assertEquals(rule("a"), s0Iter.next().getUnit());
        Frame f1 = s0.onFinish();
        var f1Call = f1.getContextStack().getCall();
        assertEquals(1, f1Call.depth());
        assertEquals(proc("f"), f1Call.getOuter().getUnit());
        StepAttempt a1 = f1.getAttempt();
        assertEquals(1, a1.size());
        assertTrue(a1.onSuccess().isDead());
        Step s1 = a1.get(0);
        assertEquals(f1, s1.onFinish());
        assertEquals(1, s1.getCall().depth());
        assertEquals(rule("b"), s1.getCall().getOuter().getUnit());
        Frame f2 = a1.onFailure();
        StepAttempt a2 = f2.getAttempt();
        assertEquals(1, a2.size());
        assertTrue(a2.onSuccess().isDead());
        Step s2 = a2.get(0);
        assertEquals(f0, s2.onFinish());
        assertEquals(1, s2.getCall().depth());
        assertEquals(rule("c"), s2.getCall().getOuter().getUnit());
        p.explore();
        if (DEBUG) {
            Viewer.showGraph(p.toGraph(FULL_GRAPH), true);
        }
    }

    /** Loads the grammar to be used for testing. */
    protected void initGrammar(String name) {
        if (!name.equals(this.grammarName)) {
            this.testGrammar = loadGrammar(name);
            this.grammarName = name;
        }
    }

    private String grammarName;

    /** Returns the currently loaded grammar. */
    protected Grammar getGrammar() {
        return this.testGrammar;
    }

    private Grammar testGrammar;

    /** Loads a named grammar from {@link #CONTROL_DIR}.*/
    protected Grammar loadGrammar(String name) {
        Grammar result = null;
        try {
            result = Groove.loadGrammar(CONTROL_DIR + name).toGrammar();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
        return result;
    }

    /** Returns the rule with a given name. */
    protected Rule rule(String name) {
        return this.testGrammar.getRule(QualName.parse(name));
    }

    /** Returns the procedure with a given name, from the latest built program. */
    protected Procedure proc(String name) {
        return this.prog.getProc(QualName.parse(name));
    }

    /** Builds a program object from a control expression.
     * @param controlName name of the control program
     * @param program control expression; non-{@code null}
     */
    protected Automaton build(String controlName, String program) {
        Program prog = new Program();
        Automaton result = null;
        try {
            QualName qualControlName = QualName.parse(controlName);
            Fragment fragment
                = createLoader().addControl(qualControlName, program).check().toFragment();
            prog.add(fragment);
            prog.setFixed();
            result = new Automaton(prog);
        } catch (FormatException e) {
            fail(e.toString());
        }
        this.prog = prog;
        return result;
    }

    /** Incrementally adds control expressions to a complete program.
     * The result can be retrieve by {@link #build()}.
     */
    protected void add(String controlName, String program) {
        if (this.loader == null) {
            this.loader = createLoader();
        }
        try {
            QualName qualControlName = QualName.parse(controlName);
            assert !qualControlName.hasErrors();
            this.loader.addControl(qualControlName, program);
            this.controlNames.add(qualControlName);
        } catch (FormatException e) {
            fail(e.toString());
        }
    }

    /** Returns the program build in successive calls to {@link #add(String, String)}. */
    protected Automaton build() {
        Automaton result = null;
        try {
            this.prog = this.loader.buildProgram(this.controlNames);
            result = new Automaton(this.prog);
        } catch (FormatException e) {
            fail(e.toString());
        }
        // reset the loader so we get a fresh one next time
        this.loader = null;
        this.controlNames.clear();
        return result;
    }

    private CtrlLoader loader;
    private final Set<QualName> controlNames = new HashSet<>();

    protected Call call(String name) {
        Callable unit = rule(name);
        if (unit == null) {
            unit = this.prog.getProc(QualName.parse(name));
        }
        return new Call(unit);
    }

    protected Call call(String name, CtrlArg... pars) {
        Callable unit = rule(name);
        if (unit == null) {
            unit = this.prog.getProc(QualName.parse(name));
        }
        return new Call(unit, Arrays.asList(pars));
    }

    /** Callback factory method for a loader of the test grammar. */
    protected CtrlLoader createLoader() {
        CtrlLoader result
            = new CtrlLoader(this.testGrammar.getProperties(), this.testGrammar.getAllRules());
        return result;
    }

    /** Most recently built program. */
    private Program prog;

    static private void assertDistinct(Object o1, Object o2) {
        assertFalse(String.format("%s and %s expected to be distinct", o1, o2), o1.equals(o2));
    }

    private final static boolean DEBUG = false;
    private final static boolean FULL_GRAPH = true;
}
