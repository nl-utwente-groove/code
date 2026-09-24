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
 */
package nl.utwente.groove.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.explore.ExploreType;
import nl.utwente.groove.explore.config.ExploreConfig;
import nl.utwente.groove.explore.config.ExploreTypeConverter;
import nl.utwente.groove.grammar.host.HostGraph;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.Node;
import nl.utwente.groove.graph.iso.IsoChecker;
import nl.utwente.groove.io.Groove;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Tests exploration in copy mode, the data mode of the Simulator, in which every state
 * graph gets a copy of its parent's data structures ({@code Record.setRandomAccess}),
 * against swing mode, the default of the command-line tools and of every other test.
 * Both modes must produce the same state space, and every copy-mode graph must be
 * internally consistent: its global edge set must agree with its per-node and per-label
 * edge sets, which matters for the multigraph samples, whose parallel edges are
 * content-equal copies that a content-keyed set would collapse.
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public class CopyModeExplorationTest {
    /** Location of the samples. */
    static private final String INPUT_DIR = "junit/samples";

    /** Explores samples in both modes and compares the resulting state spaces. */
    @Test
    public void testSamples() {
        test("ferryman", 114, 198);
        test("mergers", 66, 143);
        test("counting", 10, 9);
        // multigraph samples: parallel edges under DPO and SPO semantics
        test("parallel-pump", 38, 198);
        test("parallel-pump-spo", 44, 241);
    }

    /**
     * Explores a sample in swing and in copy mode, checks the state and transition
     * counts, the consistency of every copy-mode state graph, and the existence
     * of an isomorphic swing-mode state for every copy-mode state.
     */
    private void test(String grammarName, int stateCount, int transitionCount) {
        try {
            GrammarModel grammarModel = Groove.loadGrammar(INPUT_DIR + "/" + grammarName);
            ExploreType exploreType = ExploreTypeConverter.toExploreType(ExploreConfig.parse(""));
            GTS swing = explore(grammarModel, exploreType, false);
            GTS copy = explore(grammarModel, exploreType, true);
            assertEquals(stateCount, swing.nodeCount(), grammarName + " states (swing)");
            assertEquals(transitionCount, swing.edgeCount(), grammarName + " transitions (swing)");
            assertEquals(stateCount, copy.nodeCount(), grammarName + " states (copy)");
            assertEquals(transitionCount, copy.edgeCount(), grammarName + " transitions (copy)");
            IsoChecker checker = IsoChecker.getInstance(true);
            for (GraphState copyState : copy.nodeSet()) {
                HostGraph copyGraph = copyState.getGraph();
                checkConsistent(grammarName, copyGraph);
                int isomorphic = 0;
                for (GraphState swingState : swing.nodeSet()) {
                    if (checker.areIsomorphic(copyGraph, swingState.getGraph())) {
                        isomorphic++;
                    }
                }
                assertEquals(1, isomorphic,
                             grammarName + ": swing-mode states isomorphic to " + copyState);
            }
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    /** Checks that the global edge set of a graph agrees with its per-node and
     * per-label edge sets, so that no parallel edge has been collapsed. */
    private void checkConsistent(String grammarName, HostGraph graph) {
        int outCount = 0;
        for (Node node : graph.nodeSet()) {
            for (Edge edge : graph.outEdgeSet(node)) {
                outCount++;
                assertTrue(graph.edgeSet().contains(edge),
                           grammarName + ": " + edge + " missing from the edge set");
            }
        }
        assertEquals(outCount, graph.edgeSet().size(),
                     grammarName + ": edge set size against out-edge sets");
        for (Edge edge : graph.edgeSet()) {
            assertTrue(graph.outEdgeSet(edge.source()).contains(edge),
                       grammarName + ": " + edge + " missing from its source's out-edges");
            assertTrue(graph.inEdgeSet(edge.target()).contains(edge),
                       grammarName + ": " + edge + " missing from its target's in-edges");
            assertTrue(graph.edgeSet(edge.label()).contains(edge),
                       grammarName + ": " + edge + " missing from its label's edges");
        }
    }

    /** Explores a grammar in the given data mode and returns the GTS. */
    private GTS explore(GrammarModel grammarModel, ExploreType exploreType,
                        boolean copyMode) throws FormatException {
        GTS gts = copyMode
            ? exploreType.newGTS(grammarModel)
            : new GTS(grammarModel.toGrammar());
        if (copyMode) {
            // as SimulatorModel.resetGTS does: the GTS built through the exploration
            // type, put into random-access mode before the start state materialises
            gts.getRecord().setRandomAccess(true);
            gts.startState();
        }
        exploreType.newExploration(gts, null).play();
        return gts;
    }
}
