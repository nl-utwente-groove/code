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

import static org.junit.Assert.fail;
import groove.control.Call;
import groove.control.Callable;
import groove.control.term.Derivation;
import groove.control.term.DerivationAttempt;
import groove.control.term.Term;
import groove.grammar.Grammar;
import groove.grammar.Rule;
import groove.util.Groove;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import junit.framework.Assert;

import org.junit.Test;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
@SuppressWarnings("javadoc")
public class TermDerivationTest {
    @Test
    public void testDelta() {
        setSource(delta());
        assertSuccFail(null, null);
        assertDepth(0);
    }

    @Test
    public void testEpsilon() {
        setSource(epsilon());
        assertSuccFail(null, null);
        assertDepth(0);
    }

    @Test
    public void testCall() {
        setSource(this.a);
        assertEdge(this.aCall, epsilon());
        assertSuccFail(delta(), delta());
        assertDepth(0);
    }

    @Test
    public void testOr() {
        Term a = this.a;
        Term b = this.b;
        Term c = this.c;
        // a | (b|c) | skip
        setSource(a.or(b.seq(b).or(c)).or(epsilon()));
        assertEdge(this.aCall, epsilon());
        assertEdge(this.bCall, b);
        assertEdge(this.cCall, epsilon());
        assertSuccFail(epsilon(), epsilon());
        assertDepth(0);
        // (try a;a else b) | c
        setSource(a.seq(a).tryElse(b).or(c));
        assertEdge(this.aCall, a.transit());
        assertSuccFail(c, b.or(c));
        assertDepth(0);
        // c | (if (a) a else b) | c
        setSource(c.or(a.ifElse(a, b)));
        assertEdge(this.aCall, a);
        assertSuccFail(c, c.or(b));
        assertDepth(0);
        // a | { alap other }
        setSource(a.or(b.or(c).alap()));
        assertEdge(this.bCall, b.or(c).alap());
        assertEdge(this.cCall, b.or(c).alap());
        assertSuccFail(a, a.or(epsilon()));
        assertDepth(0);
    }

    @Test
    public void testIfElse() {
        Term a = this.a;
        Term b = this.b;
        Term c = this.c;
        // if true else b
        setSource(epsilon().ifElse(epsilon(), b));
        assertSuccFail(null, null);
        assertDepth(0);
        // if a
        setSource(a.ifOnly(epsilon()));
        assertEdge(this.aCall, epsilon());
        assertSuccFail(delta(), epsilon());
        assertDepth(0);
        // if (a|true) else b
        setSource(epsilon().or(a).ifElse(epsilon(), b));
        assertEdge(this.aCall, epsilon());
        assertSuccFail(epsilon(), epsilon());
        assertDepth(0);
        // if a else b
        setSource(a.ifElse(epsilon(), b));
        assertEdge(this.aCall, epsilon());
        assertSuccFail(delta(), b);
        assertDepth(0);
        // if { if a } else b
        setSource(a.ifOnly(epsilon()).ifElse(epsilon(), b));
        assertEdge(this.aCall, epsilon());
        assertSuccFail(delta(), epsilon().ifElse(epsilon(), b));
        assertDepth(0);
        // if { if a else b }
        setSource(a.ifElse(epsilon(), b).ifOnly(epsilon()));
        assertEdge(this.aCall, epsilon());
        assertSuccFail(delta(), b.ifOnly(epsilon()));
        assertDepth(0);
        // if (a) a also b else c
        setSource(a.ifAlsoElse(a, b, c));
        assertEdge(this.aCall, a);
        assertSuccFail(b, this.c);
        assertDepth(0);
        // if (a|skip) a also b else c
        setSource(a.or(epsilon()).ifAlsoElse(a, b, c));
        assertEdge(this.aCall, a);
        assertSuccFail(a.or(b), a.or(b));
        assertDepth(0);
        // if (a|d) a also b else c
        Call dCall = new Call(rule("d"));
        Term d = call("d");
        setSource(a.or(d).ifAlsoElse(a, b, c));
        assertEdge(this.aCall, a);
        assertEdge(dCall, a);
        assertSuccFail(b, c);
        assertDepth(0);
        // if (if (a) d) a also b else c
        setSource(a.ifOnly(d).ifAlsoElse(a, b, c));
        assertEdge(this.aCall, d.seq(a));
        assertSuccFail(b, a.or(b));
        assertDepth(0);
        // if (if (a) a else d) a also b else c
        setSource(a.ifElse(a, d).ifAlsoElse(a, b, c));
        assertEdge(this.aCall, a.seq(a));
        assertSuccFail(b, d.ifAlsoElse(a, b, c));
        assertDepth(0);
        // if (if (a) b also skip else d) a also b else c
        setSource(a.ifAlsoElse(b, epsilon(), d).ifAlsoElse(a, b, c));
        assertEdge(this.aCall, b.seq(a));
        assertSuccFail(a.or(b), d.ifAlsoElse(a, b, c));
        assertDepth(0);
    }

    @Test
    public void testWhileDo() {
        Term a = this.a;
        Term b = this.b;
        Term c = this.c;
        // while (true) {}
        setSource(epsilon().whileDo(epsilon()));
        assertDepth(0);
        // while (a|b) {}
        setSource(a.or(b).whileDo(epsilon()));
        assertEdge(this.aCall, a.or(b).whileDo(epsilon()));
        assertEdge(this.bCall, a.or(b).whileDo(epsilon()));
        assertSuccFail(delta(), epsilon());
        assertDepth(0);
        // while (a|b) { c }
        setSource(a.or(b).whileDo(c));
        assertEdge(this.aCall, c.seq(a.or(b).whileDo(c)));
        assertEdge(this.bCall, c.seq(a.or(b).whileDo(c)));
        assertSuccFail(delta(), epsilon());
        assertDepth(0);
        // while (if a) {}
        setSource(a.ifOnly(epsilon()).whileDo(epsilon()));
        assertEdge(this.aCall, source());
        assertSuccFail(delta(), this.source);
        assertDepth(0);
    }

    @Test
    public void testAtom() {
        Term a = this.a;
        Term b = this.b;
        Term c = this.c;
        // atomic true
        setSource(epsilon().atom());
        assertSuccFail(null, null);
        assertDepth(0);
        // atomic a
        setSource(a.atom());
        assertEdge(this.aCall, epsilon());
        assertSuccFail(delta(), delta());
        assertDepth(0);
        // atomic { a; b }
        setSource(a.seq(b).atom());
        assertEdge(this.aCall, b.transit());
        assertSuccFail(delta(), delta());
        assertDepth(0);
        // atomic { if (a) b else b;c }
        setSource(a.ifElse(b, b.seq(c)).atom());
        assertEdge(this.aCall, b.transit());
        assertSuccFail(delta().atom(), b.seq(c).atom());
        assertDepth(0);
    }

    @Test
    public void testTransit() {
        Term a = this.a;
        Term c = this.c;
        // @a
        setSource(a.transit());
        assertEdge(this.aCall, epsilon());
        assertSuccFail(p.delta(1), p.delta(1));
        assertDepth(1);
        // @((a|skip).c)
        setSource(a.or(epsilon()).seq(c).transit());
        assertEdge(this.aCall, c.transit());
        assertSuccFail(c.transit(), c.transit());
        assertDepth(1);
        // @(alap { a;a; })
        setSource(a.seq(a).alap().transit());
        assertEdge(this.aCall, a.transit().seq(a.seq(a).alap()).transit());
        assertSuccFail(p.delta(1), epsilon());
        assertDepth(1);
    }

    /** Predicts an outgoing transition of the current state. */
    private void assertEdge(Call call, Term target) {
        Derivation edge = new Derivation(call, target);
        Assert.assertTrue(String.format("%s not in %s", edge, this.edges), this.edges.remove(edge));
    }

    /** Predicts the success and failure of the current state.
     * Should be invoked after all regular transitions have been predicted.
     */
    private void assertSuccFail(Term success, Term failure) {
        Assert.assertEquals(Collections.emptyList(), this.edges);
        DerivationAttempt attempt = source().getAttempt();
        Assert.assertEquals(success == null ? attempt : attempt.onSuccess(), success);
        Assert.assertEquals(failure == null ? attempt : attempt.onFailure(), failure);
    }

    /** Predicts the final nature and transition depth of the current state. */
    private void assertDepth(int depth) {
        Assert.assertEquals(depth, source().getDepth());
    }

    private Term delta() {
        return p.delta();
    }

    private Term epsilon() {
        return p.epsilon();
    }

    private Term source() {
        return this.source;
    }

    private void setSource(Term term) {
        this.source = term;
        this.edges = new ArrayList<Derivation>();
        if (term.isTrial()) {
            this.edges.addAll(term.getAttempt());
        }
        // make sure the other values are properly computed
        this.source.isFinal();
        this.source.getDepth();
        if (DEBUG) {
            System.out.println(this.source.toDebugString());
            System.out.println();
        }
    }

    private Term call(String name) {
        Callable unit = rule(name);
        return p.call(new Call(unit));
    }

    /** Returns the rule with a given name. */
    private Rule rule(String name) {
        return this.grammar.getRule(name);
    }

    private Term source;
    private Collection<Derivation> edges;
    private final Grammar grammar;

    {
        Grammar grammar;
        try {
            grammar = Groove.loadGrammar(CONTROL_DIR + "abc").toGrammar();
        } catch (Exception e) {
            fail(e.getMessage());
            grammar = null;
        }
        this.grammar = grammar;
    }

    private final Call aCall, bCall, cCall;
    private final Term a, b, c;
    {
        this.aCall = new Call(rule("a"));
        this.bCall = new Call(rule("b"));
        this.cCall = new Call(rule("c"));
        this.a = p.call(this.aCall);
        this.b = p.call(this.bCall);
        this.c = p.call(this.cCall);
    }

    private final static Term p = Term.prototype();
    private static final String CONTROL_DIR = "junit/control/";
    private static final boolean DEBUG = false;

}
