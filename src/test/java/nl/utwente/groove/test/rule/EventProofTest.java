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
package nl.utwente.groove.test.rule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.grammar.Grammar;
import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.grammar.host.HostGraph;
import nl.utwente.groove.io.Groove;
import nl.utwente.groove.match.Proof;
import nl.utwente.groove.transform.RuleEvent;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Tests the reconstruction of a proof from a rule event
 * (see {@link RuleEvent#getMatch(HostGraph)}).
 * This is the path by which the simulator emphasises a selected match
 * in the state graph.
 */
@NonNullByDefault
@AIGenerated("Claude Fable 5.1, 2026-09")
public class EventProofTest {
    /** Location of the test grammars. */
    static private final String INPUT_DIR = "junit/rules";

    /**
     * Regression test for gh #858: the proof reconstructed from a basic event
     * consisted only of the top-level pattern map, dropping the images of
     * non-modifying universal sub-levels.
     */
    @Test
    public void testRegressionGh858() throws FormatException, IOException {
        Grammar grammar = Groove.loadGrammar(INPUT_DIR + "/regression-gh858").toGrammar();
        HostGraph host = grammar.getStartGraph();
        Rule rule = grammar.getRule(QualName.name("subLevelReaders"));
        assertNotNull(rule);
        var proofs = rule.getProver().getAllMatches(host);
        assertEquals(2, proofs.size());
        for (Proof proof : proofs) {
            assertFalse(proof.getSubProofs().isEmpty());
            RuleEvent event = RuleEvent.createEvent(proof, null);
            Proof recovered = event.getMatch(host);
            assertEquals(proof.getNodeValues(), recovered.getNodeValues());
            assertEquals(proof.getEdgeValues(), recovered.getEdgeValues());
            // each match covers the whole host graph
            assertEquals(host.nodeSet(), recovered.getNodeValues());
            assertEquals(host.edgeSet(), recovered.getEdgeValues());
        }
    }
}
