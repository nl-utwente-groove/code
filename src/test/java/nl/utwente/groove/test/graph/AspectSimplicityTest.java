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
package nl.utwente.groove.test.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.aspect.AspectNode;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.graph.plain.PlainGraph;

/**
 * Tests for parallel-edge support in {@link AspectGraph}: non-simple aspect
 * graphs can hold content-equal (parallel) edges, distinguished by their
 * parallel index, and conversions from and to other non-simplicity-supporting
 * graph versions preserve them. Simple aspect graphs keep collapsing
 * duplicates, with all edges at parallel index 0.
 * @author Arend Rensink
 * @version $Revision$
 */
@SuppressWarnings("javadoc")
public class AspectSimplicityTest {
    /** Builds a host-role aspect graph with two parallel "a"-edges and one
     * "b"-edge between two nodes (in the simple case, the parallel edges
     * collapse into one). The result is not fixed. */
    private AspectGraph buildGraph(boolean simple) {
        AspectGraph result = new AspectGraph("host", GraphRole.HOST, simple);
        AspectNode n0 = result.addNode();
        AspectNode n1 = result.addNode();
        result.addEdge(n0, "a", n1);
        result.addEdge(n0, "a", n1);
        result.addEdge(n0, "b", n1);
        return result;
    }

    @Test
    public void testParallelEdgesCoexist() {
        AspectGraph graph = buildGraph(false);
        assertFalse(graph.isSimple());
        assertEquals(3, graph.edgeCount());
        // the parallel indices of the "a"-edges are 0 and 1
        var numbers = graph
            .edgeSet()
            .stream()
            .filter(e -> e.label().text().equals("a"))
            .map(e -> e.getNumber())
            .sorted()
            .toList();
        assertEquals(List.of(0, 1), numbers);
    }

    @Test
    public void testSimpleCollapse() {
        AspectGraph graph = buildGraph(true);
        assertTrue(graph.isSimple());
        assertEquals(2, graph.edgeCount());
        // all edges of a simple graph are at parallel index 0
        assertTrue(graph.edgeSet().stream().allMatch(e -> e.getNumber() == 0));
    }

    @Test
    public void testPlainRoundTrip() {
        AspectGraph graph = buildGraph(false);
        graph.setFixed();
        PlainGraph plain = graph.toPlainGraph();
        assertFalse(plain.isSimple());
        assertEquals(3, plain.edgeCount());
        AspectGraph back = AspectGraph.newInstance(plain);
        assertFalse(back.isSimple());
        assertEquals(3, back.edgeCount());
    }

    @Test
    public void testClonePreservesParallelEdges() {
        AspectGraph graph = buildGraph(false);
        graph.setFixed();
        AspectGraph clone = graph.clone();
        assertFalse(clone.isSimple());
        assertEquals(3, clone.edgeCount());
    }

    /** Two independently built but identically constructed graphs must have
     * equal node and edge sets; GUI state (label filters, layout) keyed by
     * graph elements relies on this cross-instance equality (gh #806, #809). */
    @Test
    public void testCrossInstanceEquality() {
        AspectGraph one = buildGraph(false);
        AspectGraph two = buildGraph(false);
        assertEquals(one.nodeSet(), two.nodeSet());
        assertEquals(one.edgeSet(), two.edgeSet());
        AspectGraph simpleOne = buildGraph(true);
        AspectGraph simpleTwo = buildGraph(true);
        assertEquals(simpleOne.edgeSet(), simpleTwo.edgeSet());
    }
}
