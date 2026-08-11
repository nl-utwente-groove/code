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
package nl.utwente.groove.test.explore;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import nl.utwente.groove.explore.Exploration;
import nl.utwente.groove.explore.Generator;
import nl.utwente.groove.explore.config.ConfiguredExploreType;
import nl.utwente.groove.explore.config.ExploreConfig;
import nl.utwente.groove.explore.config.ExploreTypeConverter;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Tests for the GTS-tuned single-state exploration type
 * ({@link ConfiguredExploreType#stateExploration}): it must run on a GTS
 * whatever per-GTS features that GTS was explored under, and explore exactly
 * the state it is started at.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
public class StateExplorationTest {
    /** Location of the sample grammar used for the tests. */
    static private final String GRAMMAR = "junit/samples/ferryman.gps";

    /**
     * Tests that the state exploration accepts a GTS explored under a
     * per-GTS feature deviating from the grammar (where the untuned
     * initial-bound configuration is refused), and closes exactly the state
     * it is started at.
     */
    @Test
    public void testTunedToDeviatingFeatures() throws Exception {
        // a partially explored GTS under a deviating algebra family
        var gts = Generator
            .execute("-x", "algebra=point goal=any count=first", GRAMMAR)
            .getGTS();
        var open = gts
            .nodeSet()
            .stream()
            .filter(s -> !s.isClosed())
            .findFirst()
            .orElseThrow();
        // the untuned initial-bound configuration is refused: it would
        // change the algebra family of the explored state space
        var plain = ExploreTypeConverter
            .toExploreType(ExploreConfig.parse("bound=initial goal=none"));
        assertThrows(FormatException.class, () -> new Exploration(plain, open));
        // the tuned type runs, and closes exactly the state it started at
        var closedBefore = closedStates(gts);
        new Exploration(ConfiguredExploreType.stateExploration(gts), open).play();
        var closedAfter = closedStates(gts);
        assertTrue(closedAfter.remove(open), "The explored state should now be closed");
        assertEquals(closedBefore, closedAfter,
                     "No state other than the explored one should have been closed");
    }

    /**
     * Tests that the state exploration accepts a non-persistent GTS, which
     * the legacy direct type refused.
     */
    @Test
    public void testTunedToNonPersistence() throws Exception {
        // the run must be goal-bounded: an unbounded non-persistent
        // exploration of a cyclic state space does not terminate
        var gts = Generator
            .execute("-x", "persistence=none goal=any count=first", GRAMMAR)
            .getGTS();
        assertDoesNotThrow(() -> new Exploration(ConfiguredExploreType.stateExploration(gts),
            gts.startState()));
    }

    /** Returns the currently closed states of a GTS, as a fresh set. */
    private Set<GraphState> closedStates(GTS gts) {
        var result = new HashSet<GraphState>();
        gts.nodeSet().stream().filter(GraphState::isClosed).forEach(result::add);
        return result;
    }
}
