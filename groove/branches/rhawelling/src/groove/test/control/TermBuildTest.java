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

import groove.control.Call;
import groove.control.symbolic.Term;
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
public class TermBuildTest extends CtrlTester {
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
    }

    @Test
    public void test() {
        Term a = this.a;
        Term b = this.b;
        Term c = this.c;
        equal("a;", a);
        equal("{}", p.epsilon());
        equal("{ a; b; }", a.seq(b));
        equal("a|b;", a.or(b));
        equal("choice { a; } or b;", a.or(b));
        equal("a*;", a.star());
        equal("a+;", a.seq(a.star()));
        equal("#a;", a.alap());
        equal("while (a) { b; }", a.seq(b).whileDo());
        equal("if (a) b;", a.seq(b).ifElse(p.epsilon()));
        equal("if (a) b; else c;", a.seq(b).ifElse(c));
        equal("try b; else c;", b.tryElse(c));
    }

    @Test
    public void testAnyOther() {
        Term a = this.a;
        Term b = this.b;
        Term c = this.c;
        equal("a; other;", a.seq(b.or(c)));
        equal("a; any;", a.seq(a.or(b).or(c)));
    }

    void equal(String program, Term term) {
        Assert.assertEquals(term, buildTerm(program));
    }

    static private Term p;
    private Term a, b, c;
}
