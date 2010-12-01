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
 * $Id: GraphCreationTest.java,v 1.5 2008-01-30 09:33:06 iovka Exp $
 */
package groove.test.graph;

import groove.graph.DefaultEdge;
import groove.graph.DefaultGraph;
import groove.graph.DefaultLabel;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Node;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;

/**
 * @version $Revision: 2754 $
 */
@SuppressWarnings("all")
public class GraphCreationTest extends TestCase {
    protected static int NR_NODES_TOTAL = 9;
    protected static int NR_NODES_IN_GRAPH = 5;
    protected static int NR_EDGES = 7;

    protected Graph g;
    protected Node[] n = new Node[NR_NODES_TOTAL];
    protected Edge[] e = new Edge[NR_EDGES];

    public GraphCreationTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() {
        this.g = new DefaultGraph();

        for (int i = 0; i < NR_NODES_TOTAL; i++) {
            this.n[i] = DefaultNode.createNode();
        }

        this.e[0] = DefaultEdge.createEdge(this.n[0], "a", this.n[1]);
        this.e[1] = DefaultEdge.createEdge(this.n[0], "b", this.n[3]);
        this.e[2] = DefaultEdge.createEdge(this.n[3], "a", this.n[3]);
        this.e[3] = DefaultEdge.createEdge(this.n[5], "b", this.n[4]);
        this.e[4] = DefaultEdge.createEdge(this.n[4], "b", this.n[6]);
        this.e[5] = DefaultEdge.createEdge(this.n[7], "a", this.n[8]);
        this.e[6] = DefaultEdge.createEdge(this.n[8], "b", this.n[8]);

        for (int i = 0; i < NR_NODES_IN_GRAPH; i++) {
            this.g.addNode(this.n[i]);
        }

        for (int i = 0; i < NR_EDGES; i++) {
            this.g.addEdge(this.e[i]);
        }
    }

    public void testCreateEqualAndCompare() {
        Graph g2 = new DefaultGraph();

        for (int i = NR_NODES_IN_GRAPH - 1; i >= 0; i--) {
            assertTrue(!this.g.equals(g2));
            g2.addNode(this.n[i]);
        }

        for (int i = NR_EDGES - 1; i >= 0; i--) {
            assertTrue(!this.g.equals(g2));
            g2.addEdge(this.e[i]);
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
        Set<Node> nodeSet = new HashSet<Node>(this.g.nodeSet());
        Set<Edge> edgeSet = new HashSet<Edge>(this.g.edgeSet());

        for (int i = 0; i < NR_EDGES; i++) {
            assertTrue(this.g.containsElement(this.e[i]));
            this.g.removeEdge(this.e[i]);
            assertTrue(!this.g.containsElement(this.e[i]));
        }

        for (int i = 0; i < NR_NODES_TOTAL; i++) {
            assertTrue(this.g.containsElement(this.n[i]));
            this.g.removeNode(this.n[i]);
            assertTrue(!this.g.containsElement(this.n[i]));
            this.g.addNode(this.n[i]);
        }

        for (int i = 0; i < NR_EDGES; i++) {
            this.g.addEdge(this.e[i]);
        }

        assertEquals(this.g.nodeSet(), nodeSet);
        assertEquals(this.g.edgeSet(), edgeSet);
    }

    public void testGetOutEdges() {
        Set<Edge> outEdges = new HashSet<Edge>();
        outEdges.add(this.e[0]);
        outEdges.add(this.e[1]);

        for (Edge edge : this.g.outEdgeSet(this.n[0])) {
            assertTrue(outEdges.contains(edge));
            outEdges.remove(edge);
        }
        assertTrue(outEdges.isEmpty());

        Iterator<? extends Edge> edgeIter =
            this.g.outEdgeSet(this.n[2]).iterator();
        assertTrue(!edgeIter.hasNext());

        edgeIter = this.g.outEdgeSet(this.n[8]).iterator();
        assertEquals(edgeIter.next(), this.e[6]);
        assertTrue(!edgeIter.hasNext());
    }

    public void testGetInEdges() {
        Set<Edge> inEdges = new HashSet<Edge>();
        inEdges.add(this.e[5]);
        inEdges.add(this.e[6]);

        for (Edge edge : this.g.edgeSet()) {
            inEdges.remove(edge);
        }
        assertTrue(inEdges.isEmpty());

        Iterator<Edge> edgeIter2 = inEdgeSet(this.g, this.n[5]).iterator();
        assertTrue(!edgeIter2.hasNext());

        edgeIter2 = inEdgeSet(this.g, this.n[6]).iterator();
        assertEquals(edgeIter2.next(), this.e[4]);
        assertTrue(!edgeIter2.hasNext());
    }

    public void testGetEdgesWithLabel() {
        Set<Edge> edges = new HashSet<Edge>();
        edges.add(this.e[0]);
        edges.add(this.e[2]);
        edges.add(this.e[5]);

        for (Edge edge : labelEdgeSet(this.g,
            DefaultLabel.createLabel(new String("a")))) {
            assertTrue(edges.contains(edge));
            edges.remove(edge);
        }
        assertEquals(new HashSet<Edge>(), edges);

        assertTrue(labelEdgeSet(this.g, DefaultLabel.createLabel("c")).isEmpty());
    }

    private Set<Edge> labelEdgeSet(Graph g, Label label) {
        Set<Edge> labelEdges = new HashSet<Edge>();
        for (Edge edge : g.edgeSet()) {
            if (edge.label().equals(label)) {
                labelEdges.add(edge);
            }
        }
        return labelEdges;
    }

    private Set<Edge> inEdgeSet(Graph g, Node target) {
        Set<Edge> inEdges = new HashSet<Edge>();
        for (Edge edge : g.edgeSet()) {
            if (edge.target().equals(target)) {
                inEdges.add(edge);
            }
        }
        return inEdges;
    }
}
