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
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import groove.control.Call;
import groove.control.CtrlPar;
import groove.control.CtrlType;
import groove.control.CtrlVar;
import groove.control.Procedure;
import groove.control.template.Location;
import groove.control.template.Program;
import groove.control.template.Switch;
import groove.control.template.SwitchAttempt;
import groove.control.template.SwitchStack;
import groove.control.template.Template;
import groove.grammar.QualName;
import groove.util.parse.FormatException;
import junit.framework.Assert;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
@SuppressWarnings("javadoc")
public class TemplateBuildTest extends CtrlTester {
    @Before
    public void initCalls() {
        initGrammar("abc");
        this.aCall = new Call(getRule("a"));
        this.bCall = new Call(getRule("b"));
        this.cCall = new Call(getRule("c"));
        this.dCall = new Call(getRule("d"));
        this.xInt = new CtrlVar(null, "x", CtrlType.INT);
        this.xIntOut = new CtrlPar.Var(this.xInt, false);
    }

    private Call aCall;
    private Call bCall;
    private Call cCall;
    private Call dCall;
    private CtrlVar xInt;
    private CtrlPar xIntOut;

    @Test
    public void testSimple() {
        build("a;");
        assertSize(3);
        //
        build("{}");
        assertSize(1);
        assertTrue(getStart().isFinal());
        //
        build("{ a; b; }");
        assertSize(4);
        Location loc = getInit(this.aCall);
        assertTrue(loc.isTrial());
        loc = getNext(loc, this.bCall);
        assertTrue(loc.isFinal());
        //
        build("a|b;");
        assertSize(3);
        assertEquals(getInit(this.aCall), getInit(this.bCall));
        //
        build("a*;");
        assertSize(2);
        assertFalse(getStart().isFinal());
        assertTrue(onFailure(getStart()).isFinal());
        assertTrue(onSuccess(getStart()).isFinal());
        assertEquals(getStart(), getInit(this.aCall));
        //
        build("#a;");
        assertSize(3);
        assertEquals(getStart(), getInit(this.aCall));
        assertTrue(onSuccess(getStart()).isDead());
        assertTrue(onFailure(getStart()).isFinal());
        //
        build("while (true) a;");
        assertSize(2);
        assertEquals(getStart(), getInit(this.aCall));
        assertTrue(onSuccess(getStart()).isDead());
        assertTrue(onFailure(getStart()).isDead());
        //
        build("while (a) { b; } c;");
        assertSize(5);
        assertEquals(getStart(), getNext(getInit(this.aCall), this.bCall));
        assertTrue(getNext(onFailure(getStart()), this.cCall).isFinal());
        assertFalse(onFailure(getStart()).isDead());
        //
        build("choice { if (a) b; else { c;c; } } or c;");
        assertSize(7);
        assertTrue(getInit(this.cCall).isFinal());
        assertEquals(onSuccess(getStart()), onFailure(getStart()));
        loc = getNext(onSuccess(getStart()), this.aCall);
        assertTrue(getNext(loc, this.bCall).isFinal());
        assertFalse(hasNext(loc, this.cCall));
        Location pos = onFailure(onSuccess(getStart()));
        assertFalse(getNext(pos, this.cCall).isFinal());
        assertTrue(getNext(getNext(pos, this.cCall), this.cCall).isFinal());
        //
        build("< a;b; > c;");
        loc = getInit(this.aCall);
        assertEquals(1, loc.getTransience());
        loc = getNext(loc, this.bCall);
        assertEquals(0, loc.getTransience());
        assertFalse(loc.isFinal());
        assertTrue(getNext(loc, this.cCall).isFinal());
        //
        build("< < a;b; > c; >");
        loc = getInit(this.aCall);
        assertEquals(2, loc.getTransience());
        loc = getNext(loc, this.bCall);
        assertEquals(1, loc.getTransience());
        assertFalse(loc.isFinal());
        assertTrue(getNext(loc, this.cCall).isFinal());
    }

    @Test
    public void testAlapChoice() {
        build("alap a|b;");
        assertSize(3);
        Location loc = getInit(this.bCall);
        assertTrue(onFailure(loc).isFinal());
        assertTrue(onSuccess(loc).isDead());
        assertEquals(loc, getNext(loc, this.aCall));
        assertEquals(loc, getNext(loc, this.bCall));

    }

    @Test
    public void testOther() {
        build("choice a; or { alap other; }");
        assertSize(4);
        assertEquals(onFailure(getStart()), onSuccess(getStart()));
        Location loc = onFailure(getStart());
        assertTrue(onFailure(loc).isFinal());
        assertFalse(onSuccess(loc).isFinal());
        assertEquals(loc, getNext(loc, this.bCall));
        assertEquals(loc, getNext(loc, this.cCall));
        assertEquals(loc, getNext(loc, this.dCall));
    }

    public void testUntil() {
        build("until (a) { }");
        assertSize(2);
        assertTrue(getNext(getStart(), this.aCall).isFinal());
        assertTrue(onFailure(getStart()).isDead());
        assertTrue(onSuccess(getStart()).isDead());
        //
        build("until (a) { try b; }");
        assertSize(3);
        assertTrue(getNext(getStart(), this.aCall).isFinal());
        assertTrue(onSuccess(getStart()).isDead());
        Location loc = onFailure(getStart());
        assertEquals(getStart(), getNext(loc, this.bCall));
        assertTrue(onSuccess(loc).isDead());
        assertTrue(onFailure(loc).isDead());
    }

    @Test
    public void testNormalise() {
        build("if (a) { b;choice {} or {} } else b;");
        assertSize(4);
        Location loc = onFailure(getStart());
        assertEquals(getInit(this.aCall), loc);
        assertTrue(getNext(loc, this.bCall).isFinal());
    }

    @Test
    public void testVars() {
        initGrammar("emptyrules");
        Call bIntXCall = new Call(getRule("bInt"), Arrays.asList(this.xIntOut));
        Call bIntWildCall = new Call(getRule("bInt"), Arrays.asList(CtrlPar.wild()));
        List<CtrlVar> xList = Arrays.asList(this.xInt);
        //
        build("int x; bInt(out x);");
        Location loc = getInit(bIntXCall);
        assertEquals(xList, loc.getVars());
        assertTrue(loc.isFinal());
        //
        build("bInt(_);");
        loc = getInit(bIntWildCall);
        assertTrue(loc.getVars()
            .isEmpty());
        assertTrue(loc.isFinal());
        //
        build("int x; bInt(_); a;");
        loc = getInit(bIntWildCall);
        assertTrue(loc.getVars()
            .isEmpty());
        assertFalse(loc.isFinal());
        //
        build("int x; bInt(out x); bInt(x);");
        loc = getInit(bIntXCall);
        assertEquals(xList, loc.getVars());
        assertFalse(loc.isFinal());
        //
        build("int x; bInt(out x); bInt(_);");
        loc = getInit(bIntXCall);
        assertEquals(xList, loc.getVars());
        //
        buildWrong("node x; if (a) bNode(out x); bNode(x);");
    }

    @Test
    public void testProcedure() {
        buildFunction("function f() { a; b; }", "f");
        Location loc = getInit(this.aCall);
        assertEquals(0, loc.getTransience());
        assertTrue(getNext(loc, this.bCall).isFinal());
        //
        buildFunction("recipe r() { a; b; }", "r");
        loc = getInit(this.aCall);
        assertEquals(1, loc.getTransience());
        assertTrue(getNext(loc, this.bCall).isFinal());
        //
        build("function f() { (a|g); c; } function g() { (b|c); } (d|f);");
        SwitchAttempt s = this.template.getStart()
            .getAttempt();
        assertEquals(4, s.size());
        assertTrue(s.sameVerdict());
        SwitchStack s0 = s.get(0);
        assertEquals(this.dCall, s0.getBottomCall());
        //
        SwitchStack s1 = s.get(1);
        Procedure f = (Procedure) s1.getBottomCall()
            .getUnit();
        assertEquals("f", f.getQualName()
            .toString());
        Switch s1N = s1.get(1);
        assertEquals(this.aCall, s1N.getCall());
        assertEquals(getNext(f.getTemplate()
            .getStart(), this.aCall), s1N.onFinish());
        //
        SwitchStack s2 = s.get(2);
        assertEquals(f, s2.getBottomCall()
            .getUnit());
        Switch s2N = s2.get(1);
        Procedure g = (Procedure) s2N.getCall()
            .getUnit();
        assertEquals("g", g.getQualName()
            .toString());
        Switch s2NN = s2.get(2);
        assertEquals(this.bCall, s2NN.getCall());
        assertEquals(getNext(g.getTemplate()
            .getStart(), this.bCall), s2NN.onFinish());
        //
        SwitchStack s3 = s.get(3);
        assertEquals(f, s3.getBottomCall()
            .getUnit());
        Switch s3N = s3.get(1);
        assertEquals(g, s3N.getCall()
            .getUnit());
        Switch s3NN = s3.get(2);
        assertEquals(this.cCall, s3NN.getCall());
        //
        build("function f() { a*; } f;b;");
        s = this.template.getStart()
            .getAttempt();
        assertEquals(1, s.size());
        assertTrue(s.sameVerdict());
        s0 = s.get(0);
        f = (Procedure) s0.getBottomCall()
            .getUnit();
        assertEquals("f", f.getQualName()
            .toString());
        Switch s0N = s0.get(1);
        assertEquals(this.aCall, s0N.getCall());
        assertEquals(f.getTemplate()
            .getStart(), s0N.onFinish());
    }

    private void assertSize(int locCount) {
        Assert.assertEquals(locCount, this.template.getLocations()
            .size());
    }

    private void buildFunction(String program, String procName) {
        Program prog = buildProgram(program + procName + ";");
        this.template = prog.getProc(QualName.parse(procName))
            .getTemplate();
    }

    private void build(String program) {
        this.template = buildProgram(program).getTemplate();
    }

    private Program buildProgram(String program) {
        Program result = new Program();
        try {
            result.add(buildFragment(program));
            result.setFixed();
        } catch (FormatException exc) {
            Assert.fail(exc.getMessage());
        }
        return result;
    }

    private Location getStart() {
        return this.template.getStart();
    }

    private Location getInit(Call call) {
        return getNext(this.template.getStart(), call);
    }

    private Set<Location> getNexts(Location loc, Call call) {
        Set<Location> result = new HashSet<>();
        for (SwitchStack swit : loc.getAttempt()) {
            if (swit.getBottomCall()
                .equals(call)) {
                result.add(swit.onFinish());
            }
        }
        assertNotNull(result);
        return result;
    }

    private Location getNext(Location loc, Call call) {
        Set<Location> result = getNexts(loc, call);
        assertEquals(1, result.size());
        return result.iterator()
            .next();
    }

    private boolean hasNext(Location loc, Call call) {
        Set<Location> result = getNexts(loc, call);
        return !result.isEmpty();
    }

    private Location onSuccess(Location loc) {
        assertTrue(loc.isTrial());
        return loc.getAttempt()
            .onSuccess();
    }

    private Location onFailure(Location loc) {
        assertTrue(loc.isTrial());
        return loc.getAttempt()
            .onFailure();
    }

    private Template template;
}
