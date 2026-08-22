/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2026
 * University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id$
 */

package nl.utwente.groove.test;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import nl.utwente.groove.explore.ExploreType;
import nl.utwente.groove.grammar.host.HostEdge;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.io.Groove;
import nl.utwente.groove.lts.AbstractGraphState;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.lts.GraphTransition;
import nl.utwente.groove.lts.RuleTransition;
import nl.utwente.groove.transform.AbstractRuleEvent;

/**
 * Tests that state graphs reconstructed after a cache collapse consist of
 * exactly the same node and edge identities as the originally derived graphs.
 * State caches are softly referenced and may be cleared by the garbage
 * collector at any time; the target graph of a transition is then re-derived
 * from the stored event. Created nodes are reproduced from the added-node
 * array stored in the transition; in simple-graph mode, created edges are
 * reproduced by the content-pooling of the host factory, but in multigraph
 * mode the factory mints a fresh edge identity on every creation, so the
 * added-edge identities must likewise be recorded in the transition
 * (at the first derivation) and replayed on re-derivation.
 * @author Arend Rensink
 * @version $Revision$
 */
public class CacheReconstructionTest {
    /** Location of the samples. */
    static private final String INPUT_DIR = "junit/samples";

    /** Tests graph reconstruction in a simple-graph grammar. */
    @Test
    public void testFerryman() {
        test("ferryman");
    }

    /** Tests graph reconstruction in a multigraph grammar with parallel
     * creators, erasers and mergers. */
    @Test
    public void testParallelPump() {
        test("parallel-pump");
    }

    /** Tests graph reconstruction in the SPO variant of the multigraph
     * grammar, where reader and eraser copies may share an image. */
    @Test
    public void testParallelPumpSpo() {
        test("parallel-pump-spo");
    }

    /**
     * Fully explores a named grammar, records the node and edge identities of
     * every state graph, clears all state caches (simulating a
     * garbage-collection sweep), and asserts that the re-derived graphs
     * consist of the identical elements.
     */
    private void test(String grammarName) {
        try {
            GrammarModel grammarModel = Groove.loadGrammar(INPUT_DIR + "/" + grammarName);
            GTS gts = new GTS(grammarModel.toGrammar());
            ExploreType.getDefault().newExploration(gts, null).play();
            // record the elements of all state graphs
            Map<GraphState,Set<HostNode>> nodes = new LinkedHashMap<>();
            Map<GraphState,Set<HostEdge>> edges = new LinkedHashMap<>();
            for (GraphState state : gts.nodeSet()) {
                nodes.put(state, new HashSet<>(state.getGraph().nodeSet()));
                edges.put(state, new HashSet<>(state.getGraph().edgeSet()));
            }
            // simulate a garbage-collection sweep of all (softly referenced)
            // caches: those of the states, but also those of the events —
            // the latter hold the created-edge sets of the events, so a
            // state-cache-only sweep would not force fresh edge minting
            for (GraphState state : gts.nodeSet()) {
                if (state.isClosed() && state instanceof AbstractGraphState closed) {
                    closed.clearCache();
                }
            }
            for (GraphTransition trans : gts.edgeSet()) {
                if (trans instanceof RuleTransition rule
                    && rule.getEvent() instanceof AbstractRuleEvent<?> event) {
                    event.clearCache();
                }
            }
            // the re-derived graphs must consist of the identical elements
            for (GraphState state : gts.nodeSet()) {
                assertEquals(String.format("Reconstructed node set of %s differs", state),
                             nodes.get(state), new HashSet<>(state.getGraph().nodeSet()));
                assertEquals(String.format("Reconstructed edge set of %s differs", state),
                             edges.get(state), new HashSet<>(state.getGraph().edgeSet()));
            }
        } catch (Exception e) {
            org.junit.Assert.fail(e.toString());
        }
    }
}
