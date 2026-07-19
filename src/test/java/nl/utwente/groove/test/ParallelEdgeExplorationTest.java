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
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import nl.utwente.groove.explore.Exploration;
import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.host.HostGraph;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.util.Groove;

/**
 * Tests exploration with non-simple host graphs: in a grammar with the
 * {@code parallelEdges} property, a start graph stored with
 * {@code edgeids="true"} enters exploration with its parallel edges intact,
 * and an eraser matches and deletes the parallel copies one by one.
 * <p>
 * Parallel <i>creation</i> is not yet possible: rule graphs are simple, so a
 * creator edge parallel to a reader edge is pooled with it at rule
 * compilation time and drops out of the creator set. This limitation is
 * documented by {@link #testParallelCreationAbsorbed()}, which will fail
 * (and should then be updated) once rule graphs support non-simplicity.
 * @author Arend Rensink
 * @version $Revision$
 */
@SuppressWarnings("javadoc")
public class ParallelEdgeExplorationTest {
    /** Location of the fixture grammar. */
    static private final String GRAMMAR = "junit/rules/parallelEdges.gps";

    /** Explores the fixture grammar from a given start graph,
     * with only the given rule enabled. */
    private GTS explore(String startGraph, String rule) throws Exception {
        GrammarModel model = Groove.loadGrammar(GRAMMAR);
        model.setLocalActiveNames(ResourceKind.HOST, QualName.parse(startGraph));
        model.setLocalActiveNames(ResourceKind.RULE, QualName.parse(rule));
        GTS gts = new GTS(model.toGrammar());
        Exploration exploration = Exploration.explore(gts);
        assertFalse(exploration.isInterrupted());
        return gts;
    }

    /** Returns the graph of the single final state of a GTS. */
    private HostGraph getFinalGraph(GTS gts) {
        var finalStates = gts.getFinalStates();
        assertEquals(1, finalStates.size());
        return finalStates.iterator().next().getGraph();
    }

    /** The start graph is stored with {@code edgeids="true"} and two parallel
     * {@code a}-edges, which must both reach the compiled host graph; the
     * eraser then deletes them one at a time. */
    @Test
    public void testParallelDeletion() throws Exception {
        GTS gts = explore("startDouble", "del");
        HostGraph start = gts.startState().getGraph();
        assertFalse(start.isSimple());
        // both parallel a-edges of the stored start graph survive compilation
        assertEquals(2, start.edgeCount());
        // deleting the a-edges one by one gives a three-state chain
        assertEquals(3, gts.nodeCount());
        assertEquals(0, getFinalGraph(gts).edgeCount());
    }

    /** Sanity check: the same eraser on the simple-stored single-edge start
     * graph gives the usual two-state chain. */
    @Test
    public void testSingleDeletion() throws Exception {
        GTS gts = explore("start", "del");
        assertEquals(1, gts.startState().getGraph().edgeCount());
        assertEquals(2, gts.nodeCount());
        assertEquals(0, getFinalGraph(gts).edgeCount());
    }

    /** Documents the current limitation: a creator edge parallel to a reader
     * edge is pooled with it in the (simple) rule graph at compilation time,
     * so the rule creates only its {@code done} flag and no parallel
     * {@code a}-edge. Once rule graphs support non-simplicity, the final
     * edge count changes to 3 and this test should be updated. */
    @Test
    public void testParallelCreationAbsorbed() throws Exception {
        GTS gts = explore("start", "add");
        assertEquals(2, gts.nodeCount());
        HostGraph result = getFinalGraph(gts);
        assertFalse(result.isSimple());
        // the a-edge and the done flag; the parallel a-creator was absorbed
        assertEquals(2, result.edgeCount());
    }
}
