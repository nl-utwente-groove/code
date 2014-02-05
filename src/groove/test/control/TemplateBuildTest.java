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
import groove.control.Call;
import groove.control.CtrlPar;
import groove.control.CtrlType;
import groove.control.CtrlVar;
import groove.control.template.Location;
import groove.control.template.Switch;
import groove.control.template.Template;
import groove.control.template.TemplateBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

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
        this.xInt = new CtrlVar("x", CtrlType.INT);
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
        assertSize(2, 1);
        //
        build("{}");
        assertSize(1, 0);
        assertTrue(getStart().isFinal());
        //
        build("{ a; b; }");
        //        Viewer.showGraph(this.template, false);
        //        Viewer.showGraph(this.minimal, true);
        assertSize(3, 2);
        Location loc = getInit(this.aCall);
        assertTrue(loc.isTrial());
        loc = getNext(loc, this.bCall);
        assertTrue(loc.isFinal());
        //
        build("a|b;");
        assertSize(2, 2);
        assertEquals(getInit(this.aCall), getInit(this.bCall));
        //
        build("a*;");
        assertSize(2, 3);
        assertFalse(getStart().isFinal());
        assertTrue(onFailure(getStart()).isFinal());
        assertTrue(onSuccess(getStart()).isFinal());
        assertEquals(getStart(), getInit(this.aCall));
        //
        build("#a;");
        assertSize(2, 2);
        assertEquals(getStart(), getInit(this.aCall));
        assertTrue(onSuccess(getStart()).isDead());
        assertTrue(onFailure(getStart()).isFinal());
        //
        build("while (true) a;");
        assertSize(1, 1);
        assertEquals(getStart(), getInit(this.aCall));
        assertTrue(onSuccess(getStart()).isDead());
        assertTrue(onFailure(getStart()).isDead());
        //
        build("while (a) { b; } c;");
        assertSize(4, 4);
        assertEquals(getStart(), getNext(getInit(this.aCall), this.bCall));
        assertTrue(getNext(onFailure(getStart()), this.cCall).isFinal());
        assertFalse(onFailure(getStart()).isDead());
        //
        build("choice { if (a) b; else { c;c; } } or c;");
        assertSize(5, 7);
        loc = getInit(this.aCall);
        assertTrue(getNext(loc, this.bCall).isFinal());
        assertFalse(hasNext(loc, this.cCall));
        Location pos = onSuccess(getStart());
        assertTrue(getNext(pos, this.cCall).isFinal());
        Set<Location> locs = getNexts(onFailure(getStart()), this.cCall);
        assertTrue(locs.contains(pos));
        assertTrue(locs.contains(getNext(pos, this.cCall)));
        //
        build("atomic { a;b; } c;");
        loc = getInit(this.aCall);
        assertEquals(1, loc.getDepth());
        loc = getNext(loc, this.bCall);
        assertEquals(0, loc.getDepth());
        assertFalse(loc.isFinal());
        assertTrue(getNext(loc, this.cCall).isFinal());
        //
        build("atomic { atomic { a;b; } c; }");
        loc = getInit(this.aCall);
        assertEquals(2, loc.getDepth());
        loc = getNext(loc, this.bCall);
        assertEquals(1, loc.getDepth());
        assertFalse(loc.isFinal());
        assertTrue(getNext(loc, this.cCall).isFinal());
    }

    @Test
    public void testAlapChoice() {
        build("alap a|b;");
        assertSize(2, 3);
        Location loc = getInit(this.bCall);
        assertTrue(onFailure(loc).isFinal());
        assertTrue(onSuccess(loc).isDead());
        assertEquals(loc, getNext(loc, this.aCall));
        assertEquals(loc, getNext(loc, this.bCall));

    }

    @Test
    public void testOther() {
        build("choice a; or { alap other; }");
        assertSize(5, 13);
        assertTrue(getNext(onFailure(getStart()), this.aCall).isFinal());
        Location loc = getInit(this.bCall);
        assertEquals(loc, getInit(this.cCall));
        assertEquals(loc, getNext(loc, this.bCall));
        assertEquals(loc, getNext(loc, this.cCall));
        assertEquals(loc, getNext(loc, this.dCall));
        assertTrue(onFailure(loc).isFinal());
        assertFalse(onSuccess(loc).isFinal());
    }

    @Test
    public void testNormalise() {
        build("if (a) { b;choice {} or {} } else b;");
        assertSize(3, 3);
        Location loc = onFailure(getStart());
        assertEquals(getInit(this.aCall), loc);
        assertTrue(getNext(loc, this.bCall).isFinal());
    }

    @Test
    public void testVars() {
        initGrammar("emptyrules");
        Call call = new Call(getRule("bInt"), Arrays.asList(this.xIntOut));
        //
        build("int x; bInt(out x);");
        Location loc = getInit(call);
        assertTrue(loc.getVars().isEmpty());
        assertTrue(loc.isFinal());
        //
        build("int x; bInt(out x); a;");
        loc = getInit(call);
        assertTrue(loc.getVars().isEmpty());
        assertFalse(loc.isFinal());
        //
        build("int x; bInt(out x); bInt(x);");
        loc = getInit(call);
        assertEquals(Collections.singletonList(this.xInt), loc.getVars());
        assertFalse(loc.isFinal());
        //
        build("int x; bInt(out x); bInt(_);");
        loc = getInit(call);
        assertEquals(Collections.emptyList(), loc.getVars());
        // 
        buildWrong("node x; if (a) bNode(out x); bNode(x);");
    }

    @Test
    public void testProcedure() {
        build("function f() { a; b; }");
        assertTrue(this.template.getStart().isFinal());
        //
        buildFunction("function f() { a; b; }", "f", true);
        Location loc = getInit(this.aCall);
        assertTrue(getNext(loc, this.bCall).isFinal());
        //
        buildFunction("recipe r() { a; b; }", "r", false);
        loc = getInit(this.aCall);
        assertEquals(0, loc.getDepth());
    }

    private void assertSize(int locCount, int switchCount) {
        Assert.assertEquals(locCount, this.minimal.nodeCount());
        Assert.assertEquals(switchCount, this.minimal.edgeCount());
    }

    private void buildFunction(String program, String procName, boolean function) {
        this.template = builder.build(null, program, buildProcTerm(program, procName, function));
        this.minimal = builder.normalise(this.template);
    }

    private void build(String program) {
        this.template = builder.build(null, program, buildTerm(program));
        this.minimal = builder.normalise(this.template);
    }

    private Location getStart() {
        return this.minimal.getStart();
    }

    private Location getInit(Call call) {
        return getNext(this.minimal.getStart(), call);
    }

    private Set<Location> getNexts(Location loc, Call call) {
        Set<Location> result = new HashSet<Location>();
        for (Switch edge : loc.getAttempt()) {
            if (edge.getCall().equals(call)) {
                result.add(edge.target());
            }
        }
        assertNotNull(result);
        return result;
    }

    private Location getNext(Location loc, Call call) {
        Set<Location> result = getNexts(loc, call);
        assertEquals(1, result.size());
        return result.iterator().next();
    }

    private boolean hasNext(Location loc, Call call) {
        Set<Location> result = getNexts(loc, call);
        return !result.isEmpty();
    }

    private Location onSuccess(Location loc) {
        assertTrue(loc.isTrial());
        return loc.getAttempt().onSuccess();
    }

    private Location onFailure(Location loc) {
        assertTrue(loc.isTrial());
        return loc.getAttempt().onFailure();
    }

    private Template template;
    private Template minimal;
    static private final TemplateBuilder builder = TemplateBuilder.instance();
}
