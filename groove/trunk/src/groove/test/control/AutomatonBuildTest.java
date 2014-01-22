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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import groove.algebra.JavaIntAlgebra;
import groove.control.Call;
import groove.control.Callable;
import groove.control.CtrlLoader;
import groove.control.CtrlPar;
import groove.control.Position;
import groove.control.instance.Automaton;
import groove.control.instance.Frame;
import groove.control.instance.Step;
import groove.control.parse.CtrlTree;
import groove.control.template.Program;
import groove.control.template.Switch;
import groove.grammar.Grammar;
import groove.grammar.Rule;
import groove.grammar.model.FormatException;
import groove.gui.Viewer;
import groove.util.Groove;

import java.util.Arrays;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

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
            Viewer.showGraph(p, true);
        }
        Frame f = p.getStart();
        assertEquals(Position.Type.TRIAL, f.getType());
        Step s = f.getAttempt();
        Frame fFail = s.onFailure();
        Call bNodeCall = call("bNode", CtrlPar.outVar("r.q", "node"));
        Step sFail = fFail.getAttempt();
        assertEquals(bNodeCall, sFail.getCall());
        Frame fFailFail = sFail.onFailure();
        assertEquals(1, fFailFail.getCallStack().size());
        assertEquals(call("f"), fFailFail.getCallStack().getLast().getCall());
        Step sFailFail = fFailFail.getAttempt();
        Frame fFailSucc = sFail.onSuccess();
        Step sFailSucc = fFailSucc.getAttempt();
        Call bCall = call("b");
        assertEquals(bCall, sFailFail.getCall());
        assertEquals(bCall, sFailSucc.getCall());
        assertNotSame(fFailFail, fFailSucc);
        Frame fSucc = s.onSuccess();
        Step sSucc = fSucc.getAttempt();
        assertNotSame(fFailFail, sSucc.onFailure());
        assertSame(fFailSucc, sSucc.onFailure());
        assertEquals(bNodeCall, sSucc.getCall());
        Frame fSuccNext = sSucc.target();
        assertEquals(2, fSuccNext.getCallStack().size());
        Switch ss = fSuccNext.getCallStack().getLast();
        assertEquals(
            call("r", new CtrlPar.Const(JavaIntAlgebra.instance, 1),
                CtrlPar.outVar("f.arg", "node")), ss.getCall());
        assertEquals(f, s.source());
        assertEquals(call("oNode", CtrlPar.outVar("r.q", "node")), s.getCall());
        f = s.target();
        assertEquals(2, s.getCallStack().size());
        assertEquals(0, f.getCallStack().size());
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
            fail(e.toString());
        }
        return result;
    }

    /** Returns the rule with a given name. */
    protected Rule rule(String name) {
        return this.testGrammar.getRule(name);
    }

    /** Builds a program object from a control expression.
     * @param controlName name of the control program
     * @param program control expression; non-{@code null}
     */
    protected Automaton build(String controlName, String program) {
        Program prog = null;
        Automaton result = null;
        try {
            prog = createLoader().parse(controlName, program).check().toProgram();
            prog.setFixed();
            result = new Automaton(prog.getTemplate());
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
            this.loader.parse(controlName, program);
        } catch (FormatException e) {
            fail(e.toString());
        }
    }

    /** Returns the program build in successive calls to {@link #add(String, String)}. */
    protected Automaton build() {
        Program prog = new Program();
        Automaton result = null;
        try {
            for (Map.Entry<String,CtrlTree> entry : this.loader.check().entrySet()) {
                prog.add(entry.getValue().toProgram());
            }
            this.loader = null;
            prog.setFixed();
            result = new Automaton(prog.getTemplate());
        } catch (FormatException e) {
            fail(e.toString());
        }
        this.prog = prog;
        return result;
    }

    private CtrlLoader loader;

    protected Call call(String name) {
        Callable unit = rule(name);
        if (unit == null) {
            unit = this.prog.getProc(name);
        }
        return new Call(unit);
    }

    protected Call call(String name, CtrlPar... pars) {
        Callable unit = rule(name);
        if (unit == null) {
            unit = this.prog.getProc(name);
        }
        return new Call(unit, Arrays.asList(pars));
    }

    /** Callback factory method for a loader of the test grammar. */
    protected CtrlLoader createLoader() {
        CtrlLoader result =
            new CtrlLoader(this.testGrammar.getProperties().getAlgebraFamily(),
                this.testGrammar.getAllRules(), false);
        return result;
    }

    /** Most recently built program. */
    private Program prog;

    private final static boolean DEBUG = false;
}
