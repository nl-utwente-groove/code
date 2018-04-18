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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import groove.control.Call;
import groove.control.CtrlLoader;
import groove.control.CtrlPar;
import groove.control.Procedure;
import groove.control.template.Location;
import groove.control.template.Program;
import groove.control.template.Template;
import groove.control.term.Term;
import groove.grammar.Callable;
import groove.grammar.Grammar;
import groove.grammar.QualName;
import groove.grammar.Rule;
import groove.util.Groove;
import groove.util.parse.FormatException;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
@SuppressWarnings("javadoc")
public class ProgramBuildTest {
    /** The directory from which grammars are loaded. */
    public static final String CONTROL_DIR = "junit/control/";

    @Before
    public void init() {
        initGrammar("emptyrules");
    }

    @Test
    public void testNoProcedures() {
        Program p = build("ab", "a; b;");
        assertEquals(call("a").seq(call("b")), p.getMain());
        assertEquals(0, p.getProcs()
            .size());
    }

    @Test
    public void testDouble() {
        // no procedure name may be declared more than once
        buildWrong("double", "function f() { a; } function f() { a; }");
        buildWrong("double", "function f(out node x) { a; } function f(int x) { a; }");
    }

    @Test
    public void testEmpty() {
        //  empty behaviour
        buildWrong("empty", "function f() { }");
        build("empty", "function f() { choice a; or {} }");
        build("empty", "function f() { try a; }");
        build("empty", "< >");
        build("empty", "< choice a; or {} >");
        build("empty", "< try a; >");
        buildWrong("empty", "recipe f() { }");
        buildWrong("empty", "recipe f() { choice a; or {} }");
        buildWrong("empty", "recipe f() { try a; }");
    }

    @Test
    public void testUndefinedCall() {
        buildWrong("main", "a; b; Idontexist;");
        buildWrong("nested", "function f() { g() }");
        // direction of rule parameters
        build("rulepars", "int x; bInt(out x);");
        buildWrong("rulepars", "int x; iInt(out x)");
        build("rulepars", "node x; oNode(out x);");
        build("rulepars", "node x; oNode(out x); bNode(x);");
        build("rulepars", "node x; bNode(out x); bNode(x);");
        buildWrong("rulepars", "node x; oNode(out x); oNode(x);");
        // typing of rule parameters
        buildWrong("rulepars", "iInt(1.0)");
        // direction of function parameters
        build("funpars", "function f(out node x) { oNode(out x); } node y; f(out y);");
        buildWrong("funpars",
            "function f(out node x) { oNode(out x); } node y; oNode(out y); f(y);");
        buildWrong("funpars", "function f(node x) { iNode(x); } node y; f(out y);");
        build("funpars", "function f(node x) { iNode(x); } node y; oNode(out y); f(y);");
        // typing of function parameters
        build("funpars", "function f(int x) { iInt(x); } f(1);");
        buildWrong("funpars", "function f(int x) { iInt(x); } f(1.0);");
        buildWrong("funpars", "function f(int x) { iInt(x); } node y; oNode(out y); f(y);");
    }

    @Test
    public void testEndAmbiguous() {
        // termination of a function or recipe may be ambiguous
        build("ambiguous", "function f() { a; choice b; or {} }");
        build("ambiguous", "recipe f() { a; choice b; or {} }");
        // it's OK to terminate after failure
        build("fine", "recipe f() { a; try b; }");
    }

    @Test
    public void testNonTermination() {
        build("nonterminating", "function f() { while (true) { a; } }");
        buildWrong("nonterminating", "recipe f() { while (true) { a; } }");
        build("nonterminating", "function f() { a; g; } function g() { a; f; }");
        buildWrong("nonterminating", "recipe f() { a; g; } function g() { a; f; }");
        build("terminating", "function f() { a; g; } function g() { a; (b|f); }");
        build("terminating", "recipe f() { a; g; } function g() { a; (b|f); }");
        build("alap", "recipe f() { g; alap g; } function g() { a | b; }");
        buildWrong("alap", "recipe f() { g; alap g; } function g() { a; f; }");
        build("alap", "recipe f() { a; alap g; } function g() { a; f; }");
        build("alap", "recipe f() { a; alap f; }");
    }

    @Test
    public void testRecursion() {
        Program p = build("recurse", "function f() { a; r; } function r() { if (b) f; }");
        assertEquals(2, p.getProcs()
            .size());
        Procedure fProc = proc("f");
        Term fTerm = fProc.getTerm();
        Procedure rProc = proc("r");
        Term rTerm = rProc.getTerm();
        assertEquals(call("a").seq(call(rProc)), fTerm);
        assertEquals(call("b").ifOnly(call(fProc)), rTerm);
        // circular
        buildWrong("circular", "function f() { f; }");
        buildWrong("circular", "function f() { a | g; } function g() { try a; else f; }");
        buildWrong("unguarded", "function f() { try a; } function g() { f; g; }");
        build("guarded", "function f() { try a; else b; } function g() { f; g; }");
        //
        p = build("forward-call", "f; function f() { a;f; }");
        fProc = proc("f");
        assertEquals(call(fProc), p.getMain());
        assertEquals(call("a").seq(call("f")), fProc.getTerm());
    }

    @Test
    public void testParameters() {
        build("pars",
            "function f(int x, out node n) { iInt(x); node n2; oNode(out n2); bNode-oNode(n2, out n); }");
        Procedure fProc = proc("f");
        CtrlPar xIn = CtrlPar.inVar(QualName.parse("f"), "x", "int");
        CtrlPar nOut = CtrlPar.outVar(QualName.parse("f"), "n", "node");
        CtrlPar n2In = CtrlPar.inVar(QualName.parse("f"), "n2", "node");
        CtrlPar n2Out = CtrlPar.outVar(QualName.parse("f"), "n2", "node");
        assertEquals(call(rule("iInt"), xIn).seq(call(rule("oNode"), n2Out))
            .seq(call(rule("bNode-oNode"), n2In, nOut)), fProc.getTerm());
        //
        build("r",
            "recipe r(int p, out node q) { choice oNode(out q); or { bNode(out q); bInt(p); } }");
        //
        buildWrong("out", "function f(node p) { oNode(p); }");
        build("out", "function f(node p) { oNode(out p); }");
        buildWrong("out", "function f(out int p) { a | bInt(p); }");
        build("out", "function f(out node p) { oNode(out p) | bNode(out p); }");
    }

    @Test
    public void testMultiple() {
        add("main", "import sub.f; int n; f(out n);");
        add("sub.defF", "package sub; import bInt; function f(out int x) { bInt(out x); g(x); }");
        add("sub.getG", "package sub; import bInt; function g(int y) { bInt(y); }");
        Program p = build();
        Procedure fProc = proc("sub.f");
        Procedure gProc = proc("sub.g");
        assertEquals(call(fProc, CtrlPar.outVar(null, "n", "int")), p.getMain());
        CtrlPar xIn = CtrlPar.inVar(QualName.parse("sub.f"), "x", "int");
        CtrlPar xOut = CtrlPar.outVar(QualName.parse("sub.f"), "x", "int");
        assertEquals(call(rule("bInt"), xOut).seq(call(gProc, xIn)), fProc.getTerm());
        CtrlPar yIn = CtrlPar.inVar(QualName.parse("sub.g"), "y", "int");
        assertEquals(call(rule("bInt"), yIn), gProc.getTerm());
    }

    @Test
    public void testRecipes() {
        build("atomic", "function f() { a; b; } recipe r() { a; b; }");
        Template f = proc("f").getTemplate();
        Location start = f.getStart();
        assertEquals(0, start.getTransience());
        Location next = start.getAttempt()
            .get(0)
            .onFinish();
        assertEquals(0, next.getTransience());
        Location finish = next.getAttempt()
            .get(0)
            .onFinish();
        assertEquals(0, finish.getTransience());
        assertTrue(finish.isFinal());
        Template r = proc("r").getTemplate();
        start = r.getStart();
        assertEquals(0, start.getTransience());
        next = start.getAttempt()
            .get(0)
            .onFinish();
        assertEquals(1, next.getTransience());
        finish = next.getAttempt()
            .get(0)
            .onFinish();
        assertEquals(0, finish.getTransience());
        assertTrue(finish.isFinal());
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

    /** Builds a program object from a control expression.
     * @param controlName name of the control program
     * @param program control expression; non-{@code null}
     */
    protected Program build(String controlName, String program) {
        Program result = null;
        try {
            QualName qualControlName = QualName.parse(controlName);
            CtrlLoader loader = createLoader();
            loader.setDefaultMain("{}");
            loader.addControl(qualControlName, program);
            result = loader.buildProgram();
        } catch (FormatException e) {
            fail(e.toString());
        }
        this.prog = result;
        return result;
    }

    /** Attempts to build a program object from a control expression;
     * throws an exception if this succeeds.
     * @param controlName name of the control program
     * @param program control expression; non-{@code null}
     */
    protected void buildWrong(String controlName, String program) {
        try {
            QualName qualControlName = QualName.parse(controlName);
            CtrlLoader loader = createLoader();
            loader.setDefaultMain("{}");
            loader.addControl(qualControlName, program);
            loader.buildProgram();
            fail(String.format("Expected %s to be erronous, but it isn't", program));
        } catch (FormatException e) {
            // this is the expected outcome
            if (DEBUG) {
                System.out.println("Expected exception: " + e.toString());
            }
        }
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
            this.loader.addControl(qualControlName, program);
        } catch (FormatException e) {
            fail(e.toString());
        }
    }

    /** Returns the program built in successive calls to {@link #add(String, String)}. */
    protected Program build() {
        Program result = null;
        try {
            result = this.loader.buildProgram();
            this.loader = null;
            result.setFixed();
        } catch (FormatException e) {
            fail(e.toString());
        }
        this.prog = result;
        return result;
    }

    private CtrlLoader loader;

    protected Term call(String name) {
        Callable unit = rule(name);
        if (unit == null) {
            unit = proc(name);
        }
        return call(unit);
    }

    protected Term call(Callable proc, CtrlPar... pars) {
        return prot.call(new Call(proc, Arrays.asList(pars)));
    }

    /** Callback factory method for a loader of the test grammar. */
    protected CtrlLoader createLoader() {
        CtrlLoader result =
            new CtrlLoader(this.testGrammar.getProperties(), this.testGrammar.getAllRules());
        prot = result.getNamespace()
            .getPrototype();
        return result;
    }

    private Procedure proc(String name) {
        return this.prog.getProc(QualName.parse(name));
    }

    /** Most recently built program. */
    private Program prog;

    static private Term prot = Term.prototype();

    static private final boolean DEBUG = false;
}
