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
import groove.control.Call;
import groove.control.parse.CtrlTree;
import groove.control.term.Term;
import groove.util.parse.FormatException;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test class for the construction of symbolic control terms.
 * @author Arend Rensink
 * @version $Revision $
 */
@SuppressWarnings("javadoc")
public class TermTest extends CtrlTester {
    {
        initGrammar("abc");
    }

    @BeforeClass
    public static void initPrototype() {
        p = Term.prototype();
    }

    @Before
    public void initCalls() {
        this.a = p.call(new Call(getRule("a")));
        this.b = p.call(new Call(getRule("b")));
        this.c = p.call(new Call(getRule("c")));
        this.d = p.call(new Call(getRule("d")));
    }

    @Test
    public void test() {
        Term a = this.a;
        Term b = this.b;
        Term c = this.c;
        equal("a;", a);
        equal("{}", epsilon());
        equal("{ a; b; }", a.seq(b));
        equal("a|b;", a.or(b));
        equal("choice { a; } or b;", a.or(b));
        equal("a*;", a.star());
        equal("a+;", a.seq(a.star()));
        equal("#a;", a.alap());
        equal("while (a) { b; }", a.whileDo(b));
        equal("if (a) b;", a.ifOnly(b));
        equal("if (a) b; else c;", a.ifElse(b, c));
        equal("try b; else c;", b.tryElse(c));
    }

    @Test
    public void testAnyOther() {
        Term a = this.a;
        Term b = this.b;
        Term c = this.c;
        Term d = this.d;
        equal("a; other;", a.seq(b.or(c)
            .or(d)));
        equal("a; any;", a.seq(a.or(b)
            .or(c)
            .or(d)));
    }

    @Test
    public void testProcedures() {
        assertEquals(buildTerm("a;b;"), buildProcTerm("function f() { a; b; }", "f", true));
        assertEquals(buildTerm("a;b;"), buildProcTerm("recipe r() { a; b; }", "r", false));
    }

    private Term epsilon() {
        return p.epsilon();
    }

    void equal(String program, Term term) {
        assertEquals(term, buildTerm(program));
    }

    /**
     * Builds a symbolic term from a control program.
     * @param program control expression; non-{@code null}
     */
    protected Term buildTerm(String program) {
        return buildFragment(program).getMain();
    }

    /**
     * Builds a symbolic term from a function or recipe in a control program.
     * @param program control expression; non-{@code null}
     * @param procName name of the recipe or function
     * @param function if {@code true}, a function is retrieved, otherwise a recipe
     */
    protected Term buildProcTerm(String program, String procName, boolean function) {
        try {
            CtrlTree tree = createLoader().addControl(DUMMY, program)
                .check()
                .getChild(function ? 2 : 3);
            CtrlTree body = null;
            for (int i = 0; i < tree.getChildCount(); i++) {
                CtrlTree procTree = tree.getChild(i);
                if (procTree.getChild(0)
                    .getText()
                    .equals(procName)) {
                    body = procTree.getChild(2);
                }
            }
            assert body != null : String.format("Invoked procedure '%s' not declared in '%s'",
                procName,
                program);
            return body.toTerm();
        } catch (FormatException e) {
            Assert.fail(e.getMessage());
            return null;
        }
    }

    static private Term p;
    private Term a, b, c, d;
}
