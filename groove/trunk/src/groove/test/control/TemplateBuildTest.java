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
import groove.control.Location;
import groove.control.Switch;
import groove.control.Template;
import groove.control.TemplateBuilder;

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
        this.xInt = new CtrlVar("x", CtrlType.INT);
        this.xIntOut = new CtrlPar.Var(this.xInt, false);
    }

    private Call aCall;
    private Call bCall;
    private Call cCall;
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
        assertSize(3, 2);
        Location loc = getInit(this.aCall);
        assertFalse(loc.hasSuccessNext());
        loc = getNext(loc, this.bCall);
        assertTrue(loc.isFinal());
        //
        build("a|b;");
        assertSize(2, 2);
        assertEquals(getInit(this.aCall), getInit(this.bCall));
        //
        build("a*;");
        assertSize(1, 1);
        assertTrue(getStart().isFinal());
        assertEquals(getStart(), getInit(this.aCall));
        //
        build("#a;");
        assertSize(3, 3);
        assertEquals(getStart(), getInit(this.aCall));
        assertTrue(getFailure(getStart()).isFinal());
        loc = getSuccess(getStart());
        assertFalse(loc.isFinal());
        assertEquals(Collections.emptySet(), loc.getOutEdges());
        //
        build("while (a) { b; } c;");
        assertSize(5, 5);
        assertEquals(getStart(), getNext(getInit(this.aCall), this.bCall));
        assertTrue(getNext(getFailure(getStart()), this.cCall).isFinal());
        assertFalse(getFailure(getStart()).isFinal());
        //
        build("choice { if (a) b; else { c;c; } } or c;");
        assertSize(5, 7);
        loc = getInit(this.aCall);
        assertTrue(getNext(loc, this.bCall).isFinal());
        assertFalse(hasNext(loc, this.cCall));
        loc = getSuccess(getStart());
        assertTrue(getNext(loc, this.cCall).isFinal());
        Set<Location> locs = getNexts(getFailure(getStart()), this.cCall);
        assertTrue(locs.contains(loc));
        assertTrue(locs.contains(getNext(loc, this.cCall)));
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
    public void testOther() {
        build("choice a; or { alap other; }");
        assertSize(6, 10);
        assertTrue(getNext(getFailure(getStart()), this.aCall).isFinal());
        Location loc = getInit(this.bCall);
        assertEquals(loc, getInit(this.cCall));
        assertEquals(loc, getNext(loc, this.bCall));
        assertEquals(loc, getNext(loc, this.cCall));
        assertTrue(getFailure(loc).isFinal());
        assertFalse(getSuccess(loc).isFinal());
    }

    @Test
    public void testNormalise() {
        build("if (a) { b;choice {} or {} } else b;");
        assertEquals(6, this.template.nodeCount());
        assertEquals(5, this.template.edgeCount());
        assertSize(4, 4);
    }

    @Test
    public void testVars() {
        initGrammar("emptyRules");
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
    }

    @Test
    public void testFunction() {
        build("function f() { a; b; }");
    }

    private void assertSize(int locCount, int switchCount) {
        Assert.assertEquals(locCount, this.minimal.nodeCount());
        Assert.assertEquals(switchCount, this.minimal.edgeCount());
    }

    private void build(String program) {
        this.template = builder.build(program, buildTerm(program));
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
        for (Switch edge : loc.getOutCalls()) {
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

    private Location getSuccess(Location loc) {
        assertTrue(loc.hasSuccessNext());
        return loc.getSuccessNext();
    }

    private Location getFailure(Location loc) {
        assertTrue(loc.hasFailureNext());
        return loc.getFailureNext();
    }

    private Template template;
    private Template minimal;
    static private final TemplateBuilder builder = TemplateBuilder.instance();
}
