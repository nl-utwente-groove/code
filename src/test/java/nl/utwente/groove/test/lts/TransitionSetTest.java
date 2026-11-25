/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2026 University of Twente
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
package nl.utwente.groove.test.lts;

import static nl.utwente.groove.test.explore.ExploreTestSupport.loadGrammar;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.explore.config.ExploreConfig;
import nl.utwente.groove.explore.config.ExploreTypeConverter;
import nl.utwente.groove.lts.AbstractGraphState;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.lts.GraphTransition;
import nl.utwente.groove.util.AIGenerated;

/**
 * Checks the iteration over the transition set of a GTS, which is a view put
 * together from the outgoing transitions of the states. The transitions of a
 * state are built in its cache, from the stubs stored in the state, and the
 * iteration should do that state by state: an iterator that prepares all
 * states before it returns its first transition recreates and holds on to
 * the transition sets of the entire GTS at once, which costs more memory than
 * the GTS itself.
 * @author Arend Rensink
 */
@AIGenerated("Claude Fable 5.2, 2026-11")
@NonNullByDefault
public class TransitionSetTest {
    /**
     * Tests that the iteration yields the outgoing transitions of the states,
     * in the order of the states, and recreates the cache of a state only when
     * it gets to that state.
     */
    @Test
    public void testIteratesStateByState() throws Exception {
        GTS gts = new GTS(loadGrammar("ferryman"));
        ExploreTypeConverter
            .toExploreType(ExploreConfig.parse(""))
            .newExploration(gts, null)
            .play();
        List<GraphTransition> expected = new ArrayList<>();
        for (GraphState state : gts.nodeSet()) {
            expected.addAll(gts.outEdgeSet(state));
        }
        assertEquals(gts.edgeCount(), expected.size());
        // all states are closed by now, so their caches can go
        for (GraphState state : gts.nodeSet()) {
            ((AbstractGraphState) state).clearCache();
        }
        assertEquals(0, cacheCount(gts));
        Iterator<? extends GraphTransition> iter = gts.edgeSet().iterator();
        List<GraphTransition> actual = new ArrayList<>();
        actual.add(iter.next());
        int touched = cacheCount(gts);
        assertTrue(touched < gts.nodeCount() / 2, String
            .format("Getting the first transition recreated the caches of %d out of %d states",
                    touched, gts.nodeCount()));
        iter.forEachRemaining(actual::add);
        assertEquals(expected, actual);
    }

    /** Returns the number of states of a GTS that currently have a cache. */
    private int cacheCount(GTS gts) {
        int result = 0;
        for (GraphState state : gts.nodeSet()) {
            if (((AbstractGraphState) state).hasCache()) {
                result++;
            }
        }
        return result;
    }
}
