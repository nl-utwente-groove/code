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
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import nl.utwente.groove.explore.ExploreType;
import nl.utwente.groove.grammar.host.HostEdge;
import nl.utwente.groove.grammar.host.HostGraph;
import nl.utwente.groove.grammar.host.HostGraphMorphism;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.lts.AbstractGraphState;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.lts.GraphTransition;
import nl.utwente.groove.lts.RuleTransition;
import nl.utwente.groove.transform.AbstractRuleEvent;
import nl.utwente.groove.util.Groove;

/**
 * Tests that the (GUI-informational) morphism of a rule transition maps onto
 * the identical elements of the actual target state graph, also in multigraph
 * mode. The morphism is re-derived on demand rather than stored; in a
 * non-simple graph, the re-derivation mints fresh identities for
 * merge-redirected edge images, which must be substituted by the content-equal
 * edges actually present in the target graph — via the recorded added-edge
 * identities for a state's own primary transition, and by content-matching
 * against the target graph for secondary transitions. Moreover, the edge map
 * must be injective (parallel copies stay distinct under merging); for
 * symmetry transitions this relies on the target isomorphism constructed by
 * the iso checker being injective on content-equal parallel copies.
 * @author Arend Rensink
 * @version $Revision$
 */
public class TransitionMorphismTest {
    /** Location of the samples. */
    static private final String INPUT_DIR = "junit/samples";

    /** Tests the transition morphisms of a multigraph grammar with parallel
     * creators, erasers and a merger rule. */
    @Test
    public void testParallelPump() throws Exception {
        GrammarModel grammarModel = Groove.loadGrammar(INPUT_DIR + "/parallel-pump");
        GTS gts = new GTS(grammarModel.toGrammar());
        new ExploreType("bfs", "final", 0).newExploration(gts, null).play();
        // sweep all state and event caches first: with warm caches, the
        // event-cached merge map aliases the very images the original
        // derivation put into the target graph, masking the re-derivation
        // problem the morphism substitution addresses
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
        boolean foldSeen = false;
        for (GraphTransition trans : gts.edgeSet()) {
            if (!(trans instanceof RuleTransition rule)) {
                continue;
            }
            HostGraph source = rule.source().getGraph();
            HostGraph target = rule.target().getGraph();
            HostGraphMorphism morphism = rule.getMorphism();
            // every image must be an element of the actual target graph
            for (HostNode nodeImage : morphism.nodeMap().values()) {
                assertTrue(String.format("Node image %s of %s not in target graph", nodeImage,
                                         rule),
                           target.containsNode(nodeImage));
            }
            for (HostEdge edgeImage : morphism.edgeMap().values()) {
                assertTrue(String.format("Edge image %s of %s not in target graph", edgeImage,
                                         rule),
                           target.containsEdge(edgeImage));
            }
            // the morphism must be structure-preserving
            for (var entry : morphism.edgeMap().entrySet()) {
                assertEquals(morphism.getNode(entry.getKey().source()),
                             entry.getValue().source());
                assertEquals(morphism.getNode(entry.getKey().target()),
                             entry.getValue().target());
            }
            // in multigraph mode, parallel copies stay distinct, so the
            // morphism must be injective on edges
            Set<HostEdge> edgeImages = new HashSet<>(morphism.edgeMap().values());
            assertEquals(String
                .format("Morphism of %s maps distinct edges onto the same image", rule),
                         morphism.edgeMap().size(), edgeImages.size());
            // the merger rule erases nothing, so its morphism must be total
            // on the source edges (each parallel copy mapped to its own
            // redirected copy)
            if (rule.getEvent().getAction().getQualName().toString().equals("fold")) {
                foldSeen = true;
                assertEquals(String
                    .format("Morphism of %s should cover all %s source edges", rule,
                            source.edgeSet().size()), source.edgeSet().size(),
                             morphism.edgeMap().size());
            }
        }
        assertTrue("No fold (merger) transition explored", foldSeen);
    }
}
