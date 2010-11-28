/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.test;

import groove.control.ControlAutomaton;
import groove.control.ControlState;
import groove.control.ControlTransition;
import groove.trans.Rule;
import groove.util.Groove;
import groove.view.FormatException;
import groove.view.StoredGrammarView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

/**
 * Tests the Control language
 * @author Olaf Keijsers
 * @version $Revision $
 */
@Deprecated
@SuppressWarnings("all")
public class ControlTest extends TestCase {
    static private final String DIRECTORY = "junit/samples/control2.gps";
    static private final String DEF_CONTROL1 = "testControl1";
    static private final String DEF_CONTROL2 = "testControl2";
    static private final String DEF_CONTROL3 = "testControl3";
    static private final String DEF_CONTROL4 = "testControl4";

    StoredGrammarView sgv;

    /**
     * a;
     */
    public void testControl1() {
        ControlAutomaton ca = getAutomaton(DEF_CONTROL1);

        assertEquals(2, ca.nodeCount());
        assertEquals(1, ca.edgeCount());

        ControlAutomaton ref = new ControlAutomaton();
        ControlState q0 = addState(ref);
        ref.setStart(q0);
        ControlState s1 = addState(ref);
        s1.setSuccess();
        addTransition(ref, q0, s1, "a", null);

        assertTrue(compareAutomata(ca, ref));
    }

    /**
     * try { a; }
     * b;
     */
    public void testControl2() {
        ControlAutomaton ca = getAutomaton(DEF_CONTROL2);

        assertEquals(3, ca.nodeCount());
        assertEquals(3, ca.edgeCount());

        ControlAutomaton ref = new ControlAutomaton();
        ControlState q0 = addState(ref);
        ControlState q1 = addState(ref);
        ControlState s1 = addState(ref);
        s1.setSuccess();
        ref.addState(q0);
        ref.setStart(q0);
        ref.addState(q1);
        ref.addState(s1);
        ControlTransition ct1 = addTransition(ref, q0, q1, "a", null);
        ControlTransition ct2 = addTransition(ref, q1, s1, "b", null);
        Map<String,ControlTransition> failures =
            new HashMap<String,ControlTransition>();
        failures.put("a", ct1);
        ControlTransition ct3 = addTransition(ref, q0, s1, "b", failures);

        assertTrue(compareAutomata(ca, ref));
    }

    /**
     * alap { a | b }
     * c;
     */
    public void testControl3() {
        ControlAutomaton ca = getAutomaton(DEF_CONTROL3);

        assertEquals(2, ca.nodeCount());
        assertEquals(3, ca.edgeCount());

        ControlAutomaton ref = new ControlAutomaton();
        ControlState q0 = addState(ref);
        ref.setStart(q0);
        ControlState s1 = addState(ref);
        s1.setSuccess();
        ControlTransition ct1 = addTransition(ref, q0, q0, "a", null);
        ControlTransition ct2 = addTransition(ref, q0, q0, "b", null);
        Map<String,ControlTransition> failures =
            new HashMap<String,ControlTransition>();
        failures.put("a", ct1);
        failures.put("b", ct2);
        addTransition(ref, q0, s1, "c", failures);

        assertTrue(compareAutomata(ca, ref));
    }

    /**
     * until(a) do { main(); }
     * 
     * function main() {
     *  b;
     *  #(d);
     *  try { second(); }
     * }
     * 
     * function second() {
     *  c;
     *  e;
     * }
     */
    public void testControl4() {
        ControlAutomaton ca = getAutomaton(DEF_CONTROL4);

        assertEquals(4, ca.nodeCount());
        assertEquals(7, ca.edgeCount());

        ControlAutomaton ref = new ControlAutomaton();
        ControlState q0 = addState(ref);
        ref.setStart(q0);
        ControlState q5 = addState(ref);
        ControlState q10 = addState(ref);
        ControlState s1 = addState(ref);
        s1.setSuccess();

        Map<String,ControlTransition> failures =
            new HashMap<String,ControlTransition>();

        ControlTransition ct1 = addTransition(ref, q0, s1, "a", null);
        ControlTransition ct2 = addTransition(ref, q10, q0, "e", null);
        ControlTransition ct3 = addTransition(ref, q5, q5, "d", null);
        failures.put("d", ct3);
        ControlTransition ct4 = addTransition(ref, q5, q10, "c", failures);
        failures = new HashMap<String,ControlTransition>();
        failures.put("a", ct1);
        ControlTransition ct5 = addTransition(ref, q0, q5, "b", failures);
        failures = new HashMap<String,ControlTransition>();
        failures.put("d", ct3);
        failures.put("c", ct4);
        ControlTransition ct6 = addTransition(ref, q5, s1, "a", failures);
        failures = new HashMap<String,ControlTransition>();
        failures.put("d", ct3);
        failures.put("c", ct4);
        failures.put("a", ct6);
        ControlTransition ct7 = addTransition(ref, q5, q5, "b", failures);

        assertTrue(compareAutomata(ca, ref));
    }

    private ControlState addState(ControlAutomaton ca) {
        ControlState ret = new ControlState(ca);
        ca.addState(ret);
        return ret;
    }

    private ControlTransition addTransition(ControlAutomaton ca,
            ControlState s1, ControlState s2, String label,
            Map<String,ControlTransition> failures) {
        ControlTransition ct;
        Rule r = null;
        try {
            r = this.sgv.toGrammar().getRule(label);
        } catch (FormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (failures == null) {
            ct = new ControlTransition(s1, s2, label);
        } else {
            ct = new ControlTransition(s1, s2, label, failures);
        }
        ct.setRule(r);
        ca.addTransition(ct);
        return ct;
    }

    private ControlAutomaton getAutomaton(String controlProgram) {
        ControlAutomaton ret = null;
        try {
            this.sgv = Groove.loadGrammar(DIRECTORY);
            ret =
                this.sgv.getControlView(controlProgram).toAutomaton(
                    this.sgv.toGrammar());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    private static boolean compareAutomata(ControlAutomaton ca1,
            ControlAutomaton ca2) {
        return findMorphism(ca1, ca2) && findMorphism(ca2, ca1);
    }

    private static boolean findMorphism(ControlAutomaton ca1,
            ControlAutomaton ca2) {
        Map<ControlState,ControlState> morphism =
            new HashMap<ControlState,ControlState>();
        morphism.put(ca1.getStart(), ca2.getStart());
        boolean done = false;

        while (!done) {
            done = true;
            for (ControlState cs : new HashSet<ControlState>(morphism.keySet())) {
                for (ControlTransition ct : transitionsFrom(ca1, cs)) {
                    if (!morphism.containsKey(ct.target())) {
                        ControlTransition ct2 =
                            isomorphicTransition(ca1, ca2, ct, morphism);
                        if (ct2 != null) {
                            morphism.put(ct.target(), ct2.target());
                            done = false;
                        } else {
                            assertTrue(
                                "no suitable transition was found to match "
                                    + ct, false);
                            return false;
                        }
                    }
                }
            }
        }

        for (Map.Entry<ControlState,ControlState> e : morphism.entrySet()) {
            assertSame("" + e.getKey() + " is isomorphic with " + e.getValue()
                + ", but their 'success' is not equal.",
                e.getValue().isSuccess(), e.getKey().isSuccess());
        }

        for (ControlTransition ct : ca1.transitions()) {
            assertNotNull("" + ct + " has no matching image",
                isomorphicTransition(ca1, ca2, ct, morphism));
        }

        return true;
    }

    private static Set<ControlTransition> transitionsFrom(ControlAutomaton ca,
            ControlState cs) {
        Set<ControlTransition> ret = new HashSet<ControlTransition>();
        for (ControlTransition ct : ca.transitions()) {
            if (ct.source() == cs) {
                ret.add(ct);
            }
        }
        return ret;
    }

    private static ControlTransition isomorphicTransition(ControlAutomaton ca1,
            ControlAutomaton ca2, ControlTransition ct,
            Map<ControlState,ControlState> morphism) {
        for (ControlTransition ct2 : transitionsFrom(ca2,
            morphism.get(ct.source()))) {
            Set<String> keySet1 = ct.getFailures().keySet();
            Set<String> keySet2 = ct2.getFailures().keySet();
            if (ct.getRule().equals(ct2.getRule()) && keySet1.equals(keySet2)) {
                return ct2;
            }
        }
        return null;
    }
}
