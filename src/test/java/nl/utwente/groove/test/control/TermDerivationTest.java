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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.jdt.annotation.NonNull;
import org.junit.Assert;
import org.junit.Test;

import nl.utwente.groove.control.Call;
import nl.utwente.groove.control.Function;
import nl.utwente.groove.control.term.Derivation;
import nl.utwente.groove.control.term.DerivationAttempt;
import nl.utwente.groove.control.term.Term;
import nl.utwente.groove.grammar.Callable;
import nl.utwente.groove.grammar.Grammar;
import nl.utwente.groove.grammar.GrammarProperties;
import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.grammar.Signature;
import nl.utwente.groove.grammar.UnitPar;
import nl.utwente.groove.util.Groove;

/**
 * @author Arend Rensink
 * @version $Revision$
 */
@SuppressWarnings("javadoc")
public class TermDerivationTest {
    @Test
    public void testDelta() {
        setSource(delta());
        assertSuccFail();
        assertDepth(0);
    }

    @Test
    public void testEpsilon() {
        setSource(epsilon());
        assertSuccFail();
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
        assertEdge(this.cCall, epsilon());
        assertSuccFail(a.seq(a).tryElse(b), a.seq(a).tryElse(b));
        assertDepth(0);
        // (try <a;a> else b)
        setSource(a.seq(a).atom().tryElse(b));
        assertEdge(this.aCall, a.transit());
        assertSuccFail(delta(), b);
        assertDepth(0);
        // c | (if (a) a else b)
        setSource(c.or(a.ifElse(a, b)));
        assertEdge(this.cCall, epsilon());
        assertSuccFail(a.ifElse(a, b), a.ifElse(a, b));
        assertDepth(0);
        // a | { alap { b|c } }
        setSource(b.or(c).alap());
        assertEdge(this.bCall, b.or(c).alap());
        assertEdge(this.cCall, b.or(c).alap());
        assertSuccFail(delta(), epsilon());
        assertDepth(0);
    }

    @Test
    public void testIfElse() {
        Term a = this.a;
        Term b = this.b;
        Term c = this.c;
        // if true else b
        setSource(epsilon().ifElse(epsilon(), b));
        assertSuccFail();
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
        assertTrue(source().isDead());
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
    public void testUntilDo() {
        Term a = this.a;
        Term b = this.b;
        Term c = this.c;
        // while (true) {}
        setSource(epsilon().untilDo(epsilon()));
        assertDepth(0);
        assertTrue(source().isFinal());
        // until (a|b) { c }
        setSource(a.or(b).untilDo(c));
        assertEdge(this.aCall, epsilon());
        assertEdge(this.bCall, epsilon());
        assertSuccFail(delta(), c.seq(source()));
        // until (if a) { c }
        setSource(a.ifOnly(epsilon()).untilDo(c));
        assertEdge(this.aCall, epsilon());
        assertSuccFail(delta(), epsilon());
        assertDepth(0);
    }

    @Test
    public void testAtom() {
        Term a = this.a;
        Term b = this.b;
        Term c = this.c;
        // atomic true
        setSource(epsilon().atom());
        assertSuccFail();
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
        assertSuccFail(this.p.delta(1), this.p.delta(1));
        assertDepth(1);
        // @((a|skip).c)
        setSource(a.or(epsilon()).seq(c).transit());
        assertEdge(this.aCall, c.transit());
        assertSuccFail(c.transit(), c.transit());
        assertDepth(1);
        // @(alap { a;a; })
        setSource(a.seq(a).alap().transit());
        assertEdge(this.aCall, a.seq(a.seq(a).alap()).transit());
        assertSuccFail(this.p.delta(1), epsilon());
        assertDepth(1);
        // @(alap < a;a; >)
        setSource(a.seq(a).atom().alap().transit());
        assertEdge(this.aCall, a.transit().seq(a.seq(a).atom().alap()).transit());
        assertSuccFail(this.p.delta(1), epsilon());
        assertDepth(1);
    }

    @Test
    public void testFunction() {
        Function f = function("f", this.a.star());
        Call fCall = new Call(f);
        Term fb = this.p.call(fCall).seq(this.b);
        setSource(fb);
        assertEdge(fCall, this.b, new Derivation(this.aCall, this.a.star()));
        assertSuccFail(this.b, this.b);
    }

    /** Predicts an outgoing transition of the current state. */
    private void assertEdge(Call call, Term target) {
        assertEdge(call, target, null);
    }

    /** Predicts an outgoing transition of the current state. */
    private void assertEdge(Call call, Term target, Derivation nested) {
        Derivation edge = new Derivation(call, target);
        if (nested != null) {
            edge = edge.newInstance(nested);
        }
        Assert.assertTrue(String.format("%s not in %s", edge, this.edges), this.edges.remove(edge));
    }

    /** Predicts the success and failure of the current state.
     * Should be invoked after all regular transitions have been predicted.
     */
    private void assertSuccFail(@NonNull Term success, @NonNull Term failure) {
        Assert.assertEquals(Collections.emptyList(), this.edges);
        DerivationAttempt attempt = source().getAttempt();
        Assert.assertEquals(success, attempt.onSuccess());
        Assert.assertEquals(failure, attempt.onFailure());
    }

    /** Predicts fact that the attempt cannot be computed.
     */
    private void assertSuccFail() {
        try {
            var attempt = source().getAttempt();
            Assert.fail(String.format("attempt should be undefind, not %s", attempt));
        } catch (UnsupportedOperationException exc) {
            // this is the expected behaviour
        }
    }

    /** Predicts the final nature and transition depth of the current state. */
    private void assertDepth(int depth) {
        Assert.assertEquals(depth, source().getTransience());
    }

    private Term delta() {
        return this.p.delta();
    }

    private Term epsilon() {
        return this.p.epsilon();
    }

    private Term source() {
        return this.source;
    }

    private void setSource(Term term) {
        this.source = term;
        this.edges = new ArrayList<>();
        if (term.isTrial()) {
            this.edges.addAll(term.getAttempt());
        }
        // make sure the other values are properly computed
        this.source.isFinal();
        this.source.getTransience();
        if (DEBUG) {
            System.out.println(this.source.toDebugString());
            System.out.println();
        }
    }

    private Term call(String name) {
        Callable unit = rule(name);
        return this.p.call(new Call(unit));
    }

    /** Constructs a function with a given name and body, and an empty signature. */
    private Function function(String name, Term body) {
        QualName fullName = QualName.name(name);
        Signature<UnitPar.ProcedurePar> sig = new Signature<>();
        QualName controlName = QualName.name("control");
        GrammarProperties properties = this.grammar.getProperties();
        Function result = new Function(fullName, sig, controlName, 0, properties);
        result.setTerm(body);
        result.setFixed();
        return result;
    }

    /** Returns the rule with a given name. */
    private Rule rule(String name) {
        return this.grammar.getRule(QualName.parse(name));
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
    private final Term p = Term.prototype();
    private final Term a, b, c;

    {
        this.aCall = new Call(rule("a"));
        this.bCall = new Call(rule("b"));
        this.cCall = new Call(rule("c"));
        this.a = this.p.call(this.aCall);
        this.b = this.p.call(this.bCall);
        this.c = this.p.call(this.cCall);
    }
    private static final String CONTROL_DIR = "junit/control/";
    private static final boolean DEBUG = false;

}
