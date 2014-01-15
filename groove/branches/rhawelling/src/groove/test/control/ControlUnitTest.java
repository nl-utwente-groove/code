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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import groove.control.CtrlAut;
import groove.control.CtrlGuard;
import groove.control.CtrlSchedule;
import groove.control.CtrlState;
import groove.control.CtrlTransition;
import groove.control.CtrlType;
import groove.grammar.model.FormatException;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

/** Test case to exercise some otherwise uncovered methods. */
public class ControlUnitTest extends CtrlTester {
    {
        initGrammar("emptyrules");
    }

    /** Exercises some otherwise uncovered methods of {@link CtrlGuard}. */
    @Test
    public void testCtrlState() {
        try {
            CtrlAut aut = buildString("dummy", "a; b;");
            CtrlState start = aut.getStart();
            CtrlTransition trans = start.getTransitions().iterator().next();
            assertTrue(start.isStart());
            assertEquals("q0", start.toString());
            assertTrue(start.equals(new CtrlState(aut, null, 0)));
            assertFalse(start.equals(trans.target()));
            assertFalse(start.equals(null));
            assertFalse(start.equals(trans));
            CtrlState state = trans.target();
            state = state.getTransitions().iterator().next().target();
            assertNull(state.getInit());
        } catch (FormatException e) {
            fail(e.getMessage());
        }
    }

    /** Exercises some otherwise uncovered methods of {@link CtrlType}. */
    @Test
    public void testCtrlType() {
        for (CtrlType type : CtrlType.values()) {
            switch (type) {
            case NODE:
                assertNull(type.getSignature());
                break;
            default:
                assertNotNull(type.getSignature());
            }
        }
        assertNotNull(CtrlType.NODE_TYPE_NAME);
    }

    /** Exercises some otherwise uncovered methods of {@link CtrlSchedule}. */
    @Test
    public void testCtrlSchedule() {
        try {
            CtrlAut aut = buildString("dummy", "try {a;b;}");
            CtrlState start = aut.getStart();
            CtrlSchedule schedule = start.getSchedule();
            List<CtrlTransition> transList = schedule.getTransitions();
            assertEquals(1, transList.size());
            CtrlTransition trans = transList.get(0);
            assertTrue(schedule.isInitial());
            assertEquals(Collections.emptySet(), schedule.getTriedTransitions());
            assertEquals(Collections.emptySet(), schedule.getTriedCalls());
            assertEquals(Collections.emptySet(), schedule.getTriedRules());
            schedule = schedule.next(false);
            assertFalse(schedule.isInitial());
            assertEquals(Collections.singleton(trans),
                schedule.getTriedTransitions());
            assertEquals(Collections.singleton(trans.getCall()),
                schedule.getTriedCalls());
            assertEquals(Collections.singleton("a"), schedule.getTriedRules());
            assertNotNull(schedule.toString());
        } catch (FormatException e) {
            fail(e.getMessage());
        }

    }
}
