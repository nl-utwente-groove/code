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
 */

package nl.utwente.groove.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.Test;

import nl.utwente.groove.explore.ExploreType;
import nl.utwente.groove.grammar.host.HostEdge;
import nl.utwente.groove.grammar.host.HostGraph;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.io.Groove;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GraphTransition;
import nl.utwente.groove.lts.MatchApplier;
import nl.utwente.groove.lts.RuleTransition;
import nl.utwente.groove.transform.RuleApplication;
import nl.utwente.groove.util.AIGenerated;

/**
 * Tests the confluent-diamond shortcut of {@link MatchApplier} in a
 * multigraph. In {@code parallel-creators}, the rules {@code left} and
 * {@code right} both create an {@code a}-edge between the same pair of nodes,
 * and {@code cut} erases a copy of it. The creators commute, also on edge
 * identities, since a created parallel copy is resolved from the graph and
 * the edge content alone; so their diamonds are closed by the shortcut.
 * Erasing a copy and creating one do not commute on identities, since which
 * copy survives depends on the order; so those diamonds must be refused by
 * {@code RuleEvent.conflicts}. Every transition that is not a symmetry must
 * therefore lead to exactly the graph its event produces from its source.
 * @author Arend Rensink
 */
@NonNullByDefault
@AIGenerated("Claude Opus 5.5, 2026-09")
public class ConfluentDiamondTest {
    /** Location of the samples. */
    static private final String INPUT_DIR = "junit/samples";

    /** Explores the grammar and checks every transition against a fresh application. */
    @Test
    public void testParallelCreators() throws Exception {
        GrammarModel grammarModel = Groove.loadGrammar(INPUT_DIR + "/parallel-creators");
        GTS gts = new GTS(grammarModel.toGrammar());
        int diamondsBefore = MatchApplier.getConfluentDiamondCount();
        ExploreType.getDefault().newExploration(gts, null).play();
        int diamonds = MatchApplier.getConfluentDiamondCount() - diamondsBefore;
        assertEquals(STATE_COUNT, gts.nodeCount());
        assertEquals(TRANSITION_COUNT, gts.edgeCount());
        assertTrue("No confluent diamond closed", diamonds > 0);
        for (GraphTransition trans : gts.edgeSet()) {
            if (!(trans instanceof RuleTransition rule) || rule.isSymmetry()) {
                continue;
            }
            // node and edge sets alias stores that are handed on along the
            // materialisation chain, so each graph is copied before the next
            // one is touched
            HostGraph source = rule.source().getGraph();
            HostGraph applied = new RuleApplication(rule.getEvent(), source,
                rule.getAddedNodes()).getTarget();
            Set<HostNode> appliedNodes = new HashSet<>(applied.nodeSet());
            Set<HostEdge> appliedEdges = new HashSet<>(applied.edgeSet());
            HostGraph target = rule.target().getGraph();
            Set<HostNode> targetNodes = new HashSet<>(target.nodeSet());
            Set<HostEdge> targetEdges = new HashSet<>(target.edgeSet());
            assertEquals(String.format("Nodes of %s", rule), appliedNodes, targetNodes);
            // parallel copies print alike, so equal printouts mean that the
            // graphs differ in the identity of a copy
            assertEquals(String.format("Edges of %s (compared by identity)", rule), appliedEdges,
                         targetEdges);
        }
    }

    /** The number of states of {@code parallel-creators}. */
    static private final int STATE_COUNT = 8;
    /** The number of transitions of {@code parallel-creators}. */
    static private final int TRANSITION_COUNT = 16;
}
