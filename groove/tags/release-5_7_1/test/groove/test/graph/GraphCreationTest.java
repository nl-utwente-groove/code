// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente

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
package groove.test.graph;

import groove.graph.GraphRole;
import groove.graph.Label;
import groove.graph.plain.PlainEdge;
import groove.graph.plain.PlainGraph;
import groove.graph.plain.PlainLabel;
import groove.graph.plain.PlainNode;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;

/**
 * @version $Revision$
 */
@SuppressWarnings("all")
public class GraphCreationTest extends TestCase {
    protected static int NR_NODES_TOTAL = 9;
    protected static int NR_NODES_IN_GRAPH = 5;
    protected static int NR_EDGES = 7;

    protected PlainGraph g;
    protected PlainNode[] n = new PlainNode[NR_NODES_TOTAL];
    protected PlainEdge[] e = new PlainEdge[NR_EDGES];

    public GraphCreationTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() {
        this.g = new PlainGraph("g", GraphRole.NONE);

        for (int i = 0; i < NR_NODES_TOTAL; i++) {
            this.n[i] = this.g.getFactory()
                .createNode();
        }

        this.e[0] = PlainEdge.createEdge(this.n[0], "a", this.n[1]);
        this.e[1] = PlainEdge.createEdge(this.n[0], "b", this.n[3]);
        this.e[2] = PlainEdge.createEdge(this.n[3], "a", this.n[3]);
        this.e[3] = PlainEdge.createEdge(this.n[5], "b", this.n[4]);
        this.e[4] = PlainEdge.createEdge(this.n[4], "b", this.n[6]);
        this.e[5] = PlainEdge.createEdge(this.n[7], "a", this.n[8]);
        this.e[6] = PlainEdge.createEdge(this.n[8], "b", this.n[8]);

        for (int i = 0; i < NR_NODES_IN_GRAPH; i++) {
            this.g.addNode(this.n[i]);
        }

        for (int i = 0; i < NR_EDGES; i++) {
            this.g.addEdgeContext(this.e[i]);
        }
    }

    public void testCreateEqualAndCompare() {
        PlainGraph g2 = new PlainGraph("g2", GraphRole.NONE);

        for (int i = NR_NODES_IN_GRAPH - 1; i >= 0; i--) {
            assertTrue(!this.g.equals(g2));
            g2.addNode(this.n[i]);
        }

        for (int i = NR_EDGES - 1; i >= 0; i--) {
            assertTrue(!this.g.equals(g2));
            g2.addEdgeContext(this.e[i]);
        }

        assertEquals(this.g.nodeSet(), g2.nodeSet());
        assertEquals(this.g.edgeSet(), g2.edgeSet());
    }

    /*
     * public void testCopyAndCompare() { Graph g2 = new DefaultGraph();
     *
     * NodeIterator nodeIter = g.nodeSet().iterator(); while
     * (nodeIter.hasNext()) { assertTrue(! g.equals(g2)); if
     * (nodeIter.hasNext()) g2.add((Node) nodeIter.next().clone()); }
     *
     * EdgeIterator edgeIter = g.edgeIterator(); while (edgeIter.hasNext()) {
     * assertTrue(! g.equals(g2)); if (edgeIter.hasNext()) g2.add((BinaryEdge)
     * edgeIter.next().clone()); }
     *
     * assertEquals(g,g2); assertEquals(g2,g); }
     */

    public void testAddRemoveContains() {
        Set<PlainNode> nodeSet = new HashSet<>(this.g.nodeSet());
        Set<PlainEdge> edgeSet = new HashSet<>(this.g.edgeSet());

        for (int i = 0; i < NR_EDGES; i++) {
            assertTrue(this.g.containsEdge(this.e[i]));
            this.g.removeEdge(this.e[i]);
            assertTrue(!this.g.containsEdge(this.e[i]));
        }

        for (int i = 0; i < NR_NODES_TOTAL; i++) {
            assertTrue(this.g.containsNode(this.n[i]));
            this.g.removeNode(this.n[i]);
            assertTrue(!this.g.containsNode(this.n[i]));
            this.g.addNode(this.n[i]);
        }

        for (int i = 0; i < NR_EDGES; i++) {
            this.g.addEdgeContext(this.e[i]);
        }

        assertEquals(this.g.nodeSet(), nodeSet);
        assertEquals(this.g.edgeSet(), edgeSet);
    }

    public void testGetOutEdges() {
        Set<PlainEdge> outEdges = new HashSet<>();
        outEdges.add(this.e[0]);
        outEdges.add(this.e[1]);

        for (PlainEdge edge : this.g.outEdgeSet(this.n[0])) {
            assertTrue(outEdges.contains(edge));
            outEdges.remove(edge);
        }
        assertTrue(outEdges.isEmpty());

        Iterator<? extends PlainEdge> edgeIter = this.g.outEdgeSet(this.n[2])
            .iterator();
        assertTrue(!edgeIter.hasNext());

        edgeIter = this.g.outEdgeSet(this.n[8])
            .iterator();
        assertEquals(edgeIter.next(), this.e[6]);
        assertTrue(!edgeIter.hasNext());
    }

    public void testGetInEdges() {
        Set<PlainEdge> inEdges = new HashSet<>();
        inEdges.add(this.e[5]);
        inEdges.add(this.e[6]);

        for (PlainEdge edge : this.g.edgeSet()) {
            inEdges.remove(edge);
        }
        assertTrue(inEdges.isEmpty());

        Iterator<PlainEdge> edgeIter2 = inEdgeSet(this.g, this.n[5]).iterator();
        assertTrue(!edgeIter2.hasNext());

        edgeIter2 = inEdgeSet(this.g, this.n[6]).iterator();
        assertEquals(edgeIter2.next(), this.e[4]);
        assertTrue(!edgeIter2.hasNext());
    }

    public void testGetEdgesWithLabel() {
        Set<PlainEdge> edges = new HashSet<>();
        edges.add(this.e[0]);
        edges.add(this.e[2]);
        edges.add(this.e[5]);

        for (PlainEdge edge : labelEdgeSet(this.g, PlainLabel.parseLabel(new String("a")))) {
            assertTrue(edges.contains(edge));
            edges.remove(edge);
        }
        assertEquals(new HashSet<PlainEdge>(), edges);

        assertTrue(labelEdgeSet(this.g, PlainLabel.parseLabel("c")).isEmpty());
    }

    private Set<PlainEdge> labelEdgeSet(PlainGraph g, Label label) {
        Set<PlainEdge> labelEdges = new HashSet<>();
        for (PlainEdge edge : g.edgeSet()) {
            if (edge.label()
                .equals(label)) {
                labelEdges.add(edge);
            }
        }
        return labelEdges;
    }

    private Set<PlainEdge> inEdgeSet(PlainGraph g, PlainNode target) {
        Set<PlainEdge> inEdges = new HashSet<>();
        for (PlainEdge edge : g.edgeSet()) {
            if (edge.target()
                .equals(target)) {
                inEdges.add(edge);
            }
        }
        return inEdges;
    }
}
