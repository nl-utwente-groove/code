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
package groove.test.control;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import groove.algebra.JavaIntAlgebra;
import groove.control.Binding;
import groove.control.Binding.Source;
import groove.control.Call;
import groove.control.CtrlLoader;
import groove.control.CtrlPar;
import groove.control.Position;
import groove.control.Procedure;
import groove.control.instance.Assignment;
import groove.control.instance.Automaton;
import groove.control.instance.Frame;
import groove.control.instance.Step;
import groove.control.instance.StepAttempt;
import groove.control.template.Fragment;
import groove.control.template.Program;
import groove.control.template.Switch;
import groove.control.template.SwitchStack;
import groove.grammar.Callable;
import groove.grammar.Grammar;
import groove.grammar.QualName;
import groove.grammar.Rule;
import groove.gui.Viewer;
import groove.util.Groove;
import groove.util.parse.FormatException;

/**
 * @author Arend Rensink
 * @version $Revision $
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
        assertEquals(2, s.get(0)
            .getSwitchStack()
            .size());
        assertEquals(fCall, s.get(0)
            .getSwitchStack()
            .getBottomCall());
        assertEquals(bCall, s.get(0)
            .getRuleCall());
        assertEquals(aCall, s.get(1)
            .getRuleCall());
        assertEquals(s.onFailure(), s.onSuccess());
        //
        Frame fFail = s.onFailure();
        StepAttempt sFail = fFail.getAttempt();
        assertEquals(2, sFail.size());
        Call rCall = call("r",
            new CtrlPar.Const(JavaIntAlgebra.instance, 1),
            CtrlPar.outVar(QualName.parse("f"), "arg", "node"));
        Call oNodeCall = call("oNode", CtrlPar.outVar(QualName.parse("r"), "q", "node"));
        Call bNodeCall = call("bNode", CtrlPar.outVar(QualName.parse("r"), "q", "node"));
        SwitchStack swFail0 = sFail.get(0)
            .getSwitchStack();
        assertEquals(fCall, swFail0.get(0)
            .getCall());
        assertEquals(rCall, swFail0.get(1)
            .getCall());
        assertEquals(oNodeCall, swFail0.getRuleCall());
        assertEquals(bNodeCall, sFail.get(1)
            .getRuleCall());
        //
        Frame fFailFail = sFail.onFailure();
        assertEquals(0, fFailFail.getSwitchStack()
            .size());
        assertTrue(fFailFail.isFinal());
        //
        Frame fFailSucc = sFail.onSuccess();
        assertTrue(fFailSucc.isDead());
        assertTrue(sFail.get(0)
            .onFinish()
            .isFinal());
        Frame fNext = sFail.get(1)
            .onFinish();
        SwitchStack swNext = fNext.getSwitchStack();
        assertEquals(2, swNext.size());
        assertEquals(fCall, swNext.getBottomCall());
        assertEquals(rCall, swNext.get(1)
            .getCall());
        StepAttempt sNext = fNext.getAttempt();
        assertEquals(1, sNext.size());
        Call bIntCall = call("bInt", CtrlPar.inVar(QualName.parse("r"), "p", "int"));
        assertEquals(bIntCall, sNext.get(0)
            .getRuleCall());
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
        assertEquals(1, f0.getAttempt()
            .size());
        Step s0 = f0.getAttempt()
            .get(0);
        Frame f1 = s0.onFinish();
        assertEquals(1, f1.getAttempt()
            .size());
        Step s1 = f1.getAttempt()
            .get(0);
        Frame f2 = s1.onFinish();
        assertEquals(1, f2.getAttempt()
            .size());
        Step s2 = f2.getAttempt()
            .get(0);
        Frame f3 = s2.onFinish();
        assertEquals(1, f3.getAttempt()
            .size());
        Step s3 = f3.getAttempt()
            .get(0);
        Frame f4 = s3.onFinish();
        assertEquals(1, f4.getAttempt()
            .size());
        Step s4 = f4.getAttempt()
            .get(0);
        Frame f5 = s4.onFinish();
        assertEquals(1, f5.getAttempt()
            .size());
        Step s5 = f5.getAttempt()
            .get(0);
        Frame f6 = s5.onFinish();
        //
        assertEquals(0, s0.getCallDepthChange());
        List<Assignment> change = s0.getApplyAssignments();
        assertEquals(1, change.size());
        Assignment a00 = change.get(0);
        assertEquals(Assignment.Kind.MODIFY, a00.getKind());
        Binding[] b00 = a00.getBindings();
        assertEquals(1, b00.length);
        assertEquals(Source.CREATOR, b00[0].getSource());
        assertEquals(0, b00[0].getIndex());
        //
        assertEquals(2, s1.getCallDepthChange());
        change = s1.getApplyAssignments();
        assertEquals(3, change.size());
        List<Binding> b = Arrays.asList(Binding.var(0));
        assertEquals(Assignment.push(b), change.get(0));
        b = Arrays.asList();
        assertEquals(Assignment.push(b), change.get(1));
        b = Arrays.asList(Binding.anchor(0), Binding.creator(0));
        assertEquals(Assignment.call(b), change.get(2));
        //
        assertEquals(-1, s2.getCallDepthChange());
        change = s2.getApplyAssignments();
        assertEquals(2, change.size());
        b = Arrays.asList(Binding.var(1));
        assertEquals(Assignment.call(b), change.get(0));
        b = Arrays.asList(Binding.caller(0), Binding.var(0));
        assertEquals(Assignment.pop(b), change.get(1));
        //
        assertEquals(0, s3.getCallDepthChange());
        change = s3.getApplyAssignments();
        assertEquals(1, change.size());
        b = Arrays.asList(Binding.var(0), Binding.var(1));
        assertEquals(Assignment.call(b), change.get(0));
        //
        assertEquals(0, s4.getCallDepthChange());
        change = s4.getApplyAssignments();
        assertEquals(3, change.size());
        b = Arrays.asList(Binding.var(0));
        assertEquals(Assignment.push(b), change.get(0));
        b = Arrays.asList();
        assertEquals(Assignment.call(b), change.get(1));
        b = Arrays.asList(Binding.caller(1));
        assertEquals(Assignment.pop(b), change.get(2));
        //
        assertEquals(-1, s5.getCallDepthChange());
        change = s5.getApplyAssignments();
        assertEquals(4, change.size());
        b = Arrays.asList(Binding.var(0));
        assertEquals(Assignment.push(b), change.get(0));
        b = Arrays.asList();
        assertEquals(Assignment.call(b), change.get(1));
        b = Arrays.asList(Binding.caller(0));
        assertEquals(Assignment.pop(b), change.get(2));
        b = Arrays.asList(Binding.var(0));
        assertEquals(Assignment.pop(b), change.get(3));
        //
        assertTrue(f6.isFinal());
    }

    @Test
    public void testNestedLoop() {
        Automaton p = build("alap-choice", "alap a|b;");
        p.explore();
        Frame f = p.getStart();
        assertEquals(2, f.getAttempt()
            .size());
        assertTrue(f.getAttempt()
            .onSuccess()
            .isDead());
        assertTrue(f.getAttempt()
            .onFailure()
            .isFinal());
        Step s = f.getAttempt()
            .get(0);
        assertEquals(f, s.onFinish());
        assertEquals(call("a"), s.getCallStack()
            .peek());
        s = f.getAttempt()
            .get(1);
        assertEquals(call("b"), s.getCallStack()
            .peek());
        assertEquals(f, s.onFinish());
        //
        p = build("nested", "function f() { a; alap a; } recipe r() { f; alap f; } r;");
        p.explore();
        if (DEBUG) {
            Viewer.showGraph(p.toGraph(FULL_GRAPH), true);
        }
        SwitchStack stack = new SwitchStack();
        stack.add(this.prog.getTemplate()
            .getStart()
            .getAttempt()
            .get(0)
            .getBottom());
        Switch r0Switch = proc("r").getTemplate()
            .getStart()
            .getAttempt()
            .get(0)
            .getBottom();
        stack.add(r0Switch);
        Switch f0Switch = proc("f").getTemplate()
            .getStart()
            .getAttempt()
            .get(0)
            .getBottom();
        stack.add(f0Switch);
        Frame f0 = p.getStart();
        StepAttempt a0 = f0.getAttempt();
        assertEquals(1, a0.size());
        assertTrue(a0.onSuccess()
            .isDead());
        assertEquals(a0.onFailure(), a0.onSuccess());
        Step s0 = a0.get(0);
        assertEquals(stack, s0.getSwitchStack());
        Frame f1 = s0.onFinish();
        StepAttempt a1 = f1.getAttempt();
        assertTrue(a1.onSuccess()
            .isDead());
        assertEquals(1, a1.size());
        Step s1 = a1.get(0);
        Switch f1Switch = f0Switch.onFinish()
            .getAttempt()
            .get(0)
            .getBottom();
        assertDistinct(f1Switch, stack.get(2));
        SwitchStack f1Stack = new SwitchStack(stack);
        f1Stack.set(2, f1Switch);
        assertEquals(f1Stack, s1.getSwitchStack());
        assertEquals(f1, s1.onFinish());
        Frame f2 = a1.onFailure();
        StepAttempt a2 = f2.getAttempt();
        Step s2 = a2.get(0);
        Switch r2Switch = r0Switch.onFinish()
            .getAttempt()
            .get(0)
            .getBottom();
        assertDistinct(r2Switch, stack.get(1));
        SwitchStack r2Stack = new SwitchStack(stack);
        r2Stack.set(1, r2Switch);
        assertEquals(r2Stack, s2.getSwitchStack());
        assertDistinct(f1, s2.onFinish());
        assertTrue(a2.onSuccess()
            .isDead());
        assertTrue(a2.onFailure()
            .isFinal());
    }

    /** Builds an automaton consisting of a single non-trivial recipe. */
    @Test
    public void testSingleRecipe() {
        Automaton p = build("control", "recipe f() { a; #b; c; } alap f;");
        Frame f0 = p.getStart();
        StepAttempt a0 = f0.getAttempt();
        assertEquals(1, a0.size());
        assertTrue(a0.onSuccess()
            .isDead());
        assertTrue(a0.onFailure()
            .isFinal());
        Step s0 = a0.get(0);
        assertEquals(2, s0.getCallStack()
            .size());
        assertEquals(proc("f"), s0.getCallStack()
            .get(0)
            .getUnit());
        assertEquals(rule("a"), s0.getCallStack()
            .get(1)
            .getUnit());
        Frame f1 = s0.onFinish();
        StepAttempt a1 = f1.getAttempt();
        assertEquals(1, a1.size());
        assertTrue(a1.onSuccess()
            .isDead());
        Step s1 = a1.get(0);
        assertEquals(f1, s1.onFinish());
        assertEquals(2, s1.getCallStack()
            .size());
        assertEquals(proc("f"), s1.getCallStack()
            .get(0)
            .getUnit());
        assertEquals(rule("b"), s1.getCallStack()
            .get(1)
            .getUnit());
        Frame f2 = a1.onFailure();
        StepAttempt a2 = f2.getAttempt();
        assertEquals(1, a2.size());
        assertTrue(a2.onSuccess()
            .isDead());
        Step s2 = a2.get(0);
        assertEquals(f0, s2.onFinish());
        assertEquals(2, s2.getCallStack()
            .size());
        assertEquals(proc("f"), s2.getCallStack()
            .get(0)
            .getUnit());
        assertEquals(rule("c"), s2.getCallStack()
            .get(1)
            .getUnit());
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
            result = Groove.loadGrammar(CONTROL_DIR + name)
                .toGrammar();
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
            Fragment fragment = createLoader().addControl(qualControlName, program)
                .check()
                .toFragment();
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

    protected Call call(String name, CtrlPar... pars) {
        Callable unit = rule(name);
        if (unit == null) {
            unit = this.prog.getProc(QualName.parse(name));
        }
        return new Call(unit, Arrays.asList(pars));
    }

    /** Callback factory method for a loader of the test grammar. */
    protected CtrlLoader createLoader() {
        CtrlLoader result =
            new CtrlLoader(this.testGrammar.getProperties(), this.testGrammar.getAllRules());
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
