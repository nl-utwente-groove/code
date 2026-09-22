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
 */
package nl.utwente.groove.test.explore;

import static nl.utwente.groove.test.explore.ExploreTestSupport.explore;
import static nl.utwente.groove.test.explore.ExploreTestSupport.loadGrammar;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.explore.config.ExploreConfig;
import nl.utwente.groove.explore.config.ExploreTypeConverter;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GTSListener;
import nl.utwente.groove.lts.GraphTransition;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.Randomness;

/**
 * Tests that an interrupted exploration halts between the transitions of
 * a state rather than only between states, and that a subsequent
 * exploration of the same GTS resumes the state cut short.
 */
@NonNullByDefault
@AIGenerated("Claude Fable 5.1, 2026-09")
public class ExploreInterruptTest {
    /** Sample grammar whose start state has more than one outgoing transition. */
    private static final String GRAMMAR = "ferryman";

    /** Interrupts a run upon its first transition and then resumes it. */
    @Test
    public void interruptHaltsWithinState() throws Exception {
        // reference run, for the full counts and the out-degree of the start state
        var reference = explore(loadGrammar(GRAMMAR), "").getGTS();
        int startOutDegree = reference.startState().getTransitions().size();
        assertTrue(startOutDegree > 1, "The test needs a branching start state");
        // interrupted run: the thread is interrupted upon the first transition
        Randomness.setMasterSeed(42);
        var gts = new GTS(loadGrammar(GRAMMAR));
        var interrupter = new GTSListener() {
            @Override
            public void addUpdate(GTS g, GraphTransition transition) {
                Thread.currentThread().interrupt();
            }
        };
        gts.addLTSListener(interrupter);
        var exploreType = ExploreTypeConverter.toExploreType(ExploreConfig.parse(""));
        try {
            var interrupted = exploreType.newExploration(gts, null).play();
            assertTrue(interrupted.isInterrupted(), "The exploration should report the interrupt");
        } finally {
            gts.removeLTSListener(interrupter);
            // clear the interrupt status the listener left behind
            Thread.interrupted();
        }
        assertEquals(1, gts.edgeCount(), "Only the transition before the interrupt should exist");
        assertFalse(gts.startState().isClosed(), "The state cut short should stay open");
        // a fresh exploration of the same GTS completes the state space
        exploreType.newExploration(gts, null).play();
        assertTrue(gts.startState().isClosed());
        assertEquals(reference.nodeCount(), gts.nodeCount());
        assertEquals(reference.edgeCount(), gts.edgeCount());
    }
}
