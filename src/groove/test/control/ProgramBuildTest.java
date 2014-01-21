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
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.fail;
import groove.control.Call;
import groove.control.Callable;
import groove.control.CtrlLoader;
import groove.control.CtrlPar;
import groove.control.Procedure;
import groove.control.parse.CtrlTree;
import groove.control.template.Program;
import groove.control.term.Term;
import groove.grammar.Grammar;
import groove.grammar.Rule;
import groove.grammar.model.FormatException;
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
        assertEquals(call("a").seq(call("b")), p.getTerm());
        assertEquals(0, p.getProcs().size());
    }

    @Test
    public void testDouble() {
        // no procedure name may be declared more than once
        buildWrong("double", "function f() { a; } function f() { a; }");
        buildWrong("double",
            "function f(out node x) { a; } function f(int x) { a; }");
    }

    @Test
    public void testEmpty() {
        //  empty behaviour
        buildWrong("empty", "function f() { }");
        build("empty", "function f() { choice a; or {} }");
        build("empty", "function f() { try a; }");
        build("empty", "atomic { }");
        build("empty", "atomic { choice a; or {} }");
        build("empty", "atomic { try a; }");
        buildWrong("empty", "recipe f() { }");
        buildWrong("empty", "recipe f() { choice a; or {} }");
        buildWrong("empty", "recipe f() { try a; }");
    }

    public void testNonTermination() {
        build("nonterminating", "function f() { while (true) { a; } }");
        buildWrong("nonterminating", "recipe f() { while (true) { a; } }");
        build("nonterminating", "function f() { a; g; } function g() { a; f; }");
        buildWrong("nonterminating",
            "recipe f() { a; g; } function g() { a; f; }");
        build("terminating",
            "function f() { a; g; } function g() { a; (b|f); }");
        build("terminating", "recipe f() { a; g; } function g() { a; (b|f); }");
    }

    @Test
    public void testRecursion() {
        Program p =
            build("recurse",
                "function f() { a; r; } function r() { if (b) f; }");
        assertNull(p.getTerm());
        assertEquals(2, p.getProcs().size());
        Procedure fProc = p.getProc("f");
        Term fTerm = fProc.getTerm();
        Procedure rProc = p.getProc("r");
        Term rTerm = rProc.getTerm();
        assertEquals(call("a").seq(call(rProc)), fTerm);
        assertEquals(call("b").ifOnly(call(fProc)), rTerm);
        // circular
        buildWrong("circular", "function f() { f; }");
        buildWrong("circular",
            "function f() { a | g; } function g() { try a; else f; }");
        buildWrong("unguarded",
            "function f() { try a; } function g() { f; g; }");
        build("guarded",
            "function f() { try a; else b; } function g() { f; g; }");
        //
        p = build("forward call", "f; function f() { a;f; }");
        fProc = p.getProc("f");
        assertEquals(call(fProc), p.getTerm());
        assertEquals(call("a").seq(call("f")), fProc.getTerm());
    }

    @Test
    public void testParameters() {
        Program p =
            build(
                "pars",
                "function f(int x, out node n) { iInt(x); node n2; oNode(out n2); bNode-oNode(n2, out n); }");
        Procedure fProc = p.getProc("f");
        CtrlPar xIn = CtrlPar.inVar("f.x", "int");
        CtrlPar nOut = CtrlPar.outVar("f.n", "node");
        CtrlPar n2In = CtrlPar.inVar("f.n2", "node");
        CtrlPar n2Out = CtrlPar.outVar("f.n2", "node");
        assertEquals(
            call(rule("iInt"), xIn).seq(call(rule("oNode"), n2Out)).seq(
                call(rule("bNode-oNode"), n2In, nOut)), fProc.getTerm());
    }

    @Test
    public void testMultiple() {
        add("main", "import sub.f; int n; f(out n);");
        add("sub.defF",
            "package sub; import bInt; function f(out int x) { bInt(out x); g(x); }");
        add("sub.getG",
            "package sub; import bInt; function g(int y) { bInt(y); }");
        Program p = build();
        Procedure fProc = p.getProc("sub.f");
        Procedure gProc = p.getProc("sub.g");
        assertEquals(call(fProc, CtrlPar.outVar("n", "int")), p.getTerm());
        CtrlPar xIn = CtrlPar.inVar("sub.f.x", "int");
        CtrlPar xOut = CtrlPar.outVar("sub.f.x", "int");
        assertEquals(call(rule("bInt"), xOut).seq(call(gProc, xIn)),
            fProc.getTerm());
        CtrlPar yIn = CtrlPar.inVar("sub.g.y", "int");
        assertEquals(call(rule("bInt"), yIn), gProc.getTerm());
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
    protected Program build(String controlName, String program) {
        Program result = null;
        try {
            result =
                createLoader().parse(controlName, program).check().toProgram();
            result.setFixed();
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
            createLoader().parse(controlName, program).check().toProgram().setFixed();
            fail(String.format("Expected %s to be erronous, but it isn't",
                program));
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
            this.loader.parse(controlName, program);
        } catch (FormatException e) {
            fail(e.toString());
        }
    }

    /** Returns the program build in successive calls to {@link #add(String, String)}. */
    protected Program build() {
        Program result = new Program();
        try {
            for (Map.Entry<String,CtrlTree> entry : this.loader.check().entrySet()) {
                result.add(entry.getValue().toProgram());
            }
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
            unit = this.prog.getProc(name);
        }
        return call(unit);
    }

    protected Term call(Callable proc, CtrlPar... pars) {
        return prot.call(new Call(proc, Arrays.asList(pars)));
    }

    /** Callback factory method for a loader of the test grammar. */
    protected CtrlLoader createLoader() {
        CtrlLoader result =
            new CtrlLoader(this.testGrammar.getProperties().getAlgebraFamily(),
                this.testGrammar.getAllRules(), false);
        prot = result.getNamespace().getPrototype();
        return result;
    }

    /** Most recently built program. */
    private Program prog;

    static private Term prot = Term.prototype();

    static private final boolean DEBUG = false;
}
