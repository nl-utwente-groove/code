// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2023 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/*
 * $Id$
 */
package nl.utwente.groove.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import nl.utwente.groove.grammar.Grammar;
import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.io.Groove;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GraphState;

/**
 * Tests that repeated GTS-level application of an edge-creating rule in a
 * multigraph grammar keeps adding fresh parallel copies. Because rule events
 * are pooled per (rule, anchor) and cache their created-edge images, a
 * repeated application of the same event to a graph that already contains
 * the cached image must mint a fresh copy rather than silently re-adding
 * (and thereby dropping) the existing one.
 * @author Arend Rensink
 * @version $Revision$
 */
public class FreshCreatorEdgeTest {
    /** A binary-edge creator keeps growing the parallel-edge count. */
    @Test
    public void testReaderCreator() throws Exception {
        test("readerCreator");
    }

    /** A flag creator keeps growing the parallel-flag count. */
    @Test
    public void testFlagCreator() throws Exception {
        test("flagCreator");
    }

    /** A field-assignment creator keeps growing the parallel-value-edge count. */
    @Test
    public void testLetCreator() throws Exception {
        test("letCreator");
    }

    /**
     * Applies the named rule of the mult grammar three times in succession,
     * starting from the host graph {@code <ruleName>-0}, and asserts that
     * every application yields a fresh state with one edge more.
     */
    private void test(String ruleName) throws Exception {
        var grammarModel = Groove.loadGrammar("junit/rules/mult");
        grammarModel.setLocalActiveNames(ResourceKind.HOST, QualName.name(ruleName + "-0"));
        Grammar grammar = grammarModel.toGrammar();
        GTS gts = new GTS(grammar);
        GraphState state = gts.startState();
        int edgeCount = state.getGraph().edgeSet().size();
        for (int i = 0; i < 3; i++) {
            var match = state
                .getMatches()
                .stream()
                .filter(m -> m.getEvent().getAction().getQualName().toString().equals(ruleName))
                .findFirst()
                .orElseThrow();
            GraphState target = gts.getMatchApplier().apply(state, match).target();
            assertNotEquals(state, target);
            edgeCount++;
            assertEquals(edgeCount, target.getGraph().edgeSet().size());
            state = target;
        }
    }
}
