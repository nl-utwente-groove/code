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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.graph.multi.MultiGraph;
import nl.utwente.groove.graph.plain.PlainFactory;
import nl.utwente.groove.graph.plain.PlainGraph;

/**
 * Tests for non-simple {@link PlainGraph}s, i.e., plain graphs that
 * can contain parallel (equi-labelled) edges.
 * @author Arend Rensink
 * @version $Revision$
 */
@SuppressWarnings("javadoc")
public class PlainMultiGraphTest {
    /** Creates a fresh, non-simple plain graph. */
    private PlainGraph createGraph() {
        return new PlainGraph("multi", GraphRole.NONE, false);
    }

    @Test
    public void testSimplicityFlags() {
        PlainGraph multiGraph = createGraph();
        assertFalse(multiGraph.isSimple());
        assertFalse(multiGraph.getFactory().isSimple());
        // simple plain graphs are based on the singleton factory
        PlainGraph simpleGraph = new PlainGraph("simple", GraphRole.NONE);
        assertTrue(simpleGraph.isSimple());
        assertSame(PlainFactory.instance(), simpleGraph.getFactory());
    }

    @Test
    public void testParallelEdges() {
        PlainGraph graph = createGraph();
        var source = graph.addNode();
        var target = graph.addNode();
        var edge1 = graph.addEdge(source, "a", target);
        var edge2 = graph.addEdge(source, "a", target);
        assertNotSame(edge1, edge2);
        assertNotEquals(edge1, edge2);
        assertNotEquals(edge1.getNumber(), edge2.getNumber());
        assertEquals(2, graph.edgeCount());
        assertTrue(graph.containsEdge(edge1));
        assertTrue(graph.containsEdge(edge2));
        // removal only removes the given parallel edge
        assertTrue(graph.removeEdge(edge1));
        assertEquals(1, graph.edgeCount());
        assertTrue(graph.containsEdge(edge2));
    }

    @Test
    public void testFactorySharing() {
        PlainGraph graph = createGraph();
        var source = graph.addNode();
        var target = graph.addNode();
        graph.addEdge(source, "a", target);
        graph.addEdge(source, "a", target);
        // clones and derived graphs share the factory, and hence edge numbering
        PlainGraph clone = graph.clone();
        assertSame(graph.getFactory(), clone.getFactory());
        assertFalse(clone.isSimple());
        assertEquals(2, clone.edgeCount());
        assertSame(graph.getFactory(), graph.newGraph("new").getFactory());
        // distinct non-simple graphs get distinct factories
        assertNotSame(graph.getFactory(), createGraph().getFactory());
    }

    @Test
    public void testInstancePreservesParallelEdges() {
        MultiGraph multi = new MultiGraph("multi", GraphRole.NONE);
        var source = multi.addNode();
        var target = multi.addNode();
        multi.addEdge(source, "a", target);
        multi.addEdge(source, "a", target);
        assertEquals(2, multi.edgeCount());
        PlainGraph image = PlainGraph.instance(multi);
        assertFalse(image.isSimple());
        assertEquals(2, image.edgeCount());
    }
}
