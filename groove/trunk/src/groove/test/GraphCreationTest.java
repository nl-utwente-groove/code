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
 * $Id: GraphCreationTest.java,v 1.4 2007-09-07 19:13:36 rensink Exp $
 */
package groove.test;

import groove.graph.DefaultEdge;
import groove.graph.DefaultGraph;
import groove.graph.DefaultLabel;
import groove.graph.DefaultNode;
import groove.graph.BinaryEdge;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Node;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;

/**
 * @version $Revision: 1.4 $
 */
public class GraphCreationTest extends TestCase {
    protected static int NR_NODES_TOTAL = 9;
    protected static int NR_NODES_IN_GRAPH = 5;
    protected static int NR_EDGES = 7;

    protected Graph g;
    protected Node[] n = new Node[NR_NODES_TOTAL];
    protected BinaryEdge[] e = new BinaryEdge[NR_EDGES];

    public GraphCreationTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() {
        g = new DefaultGraph();

        for (int i = 0; i < NR_NODES_TOTAL; i++)
            n[i] = DefaultNode.createNode();

        e[0] = DefaultEdge.createEdge(n[0], "a", n[1]);
        e[1] = DefaultEdge.createEdge(n[0], "b", n[3]);
        e[2] = DefaultEdge.createEdge(n[3], "a", n[3]);
        e[3] = DefaultEdge.createEdge(n[5], "b", n[4]);
        e[4] = DefaultEdge.createEdge(n[4], "b", n[6]);
        e[5] = DefaultEdge.createEdge(n[7], "a", n[8]);
        e[6] = DefaultEdge.createEdge(n[8], "b", n[8]);

        for (int i = 0; i < NR_NODES_IN_GRAPH; i++)
            g.addNode(n[i]);

        for (int i = 0; i < NR_EDGES; i++)
            g.addEdge(e[i]);
    }

    public void testCreateEqualAndCompare() {
        Graph g2 = new DefaultGraph();

        for (int i = NR_NODES_IN_GRAPH - 1; i >= 0; i--) {
            assertTrue(! g.equals(g2));
            g2.addNode(n[i]);
        }

        for (int i = NR_EDGES - 1; i >= 0; i--) {
            assertTrue(! g.equals(g2));
            g2.addEdge(e[i]);
        }

        assertEquals(g.nodeSet(),g2.nodeSet());
        assertEquals(g.edgeSet(),g2.edgeSet());
    }

    /*
    public void testCopyAndCompare() {
        Graph g2 = new DefaultGraph();

        NodeIterator nodeIter = g.nodeSet().iterator();
        while (nodeIter.hasNext()) {
            assertTrue(! g.equals(g2));
            if (nodeIter.hasNext())
                g2.add((Node) nodeIter.next().clone());
        }

        EdgeIterator edgeIter = g.edgeIterator();
        while (edgeIter.hasNext()) {
            assertTrue(! g.equals(g2));
            if (edgeIter.hasNext())
                g2.add((BinaryEdge) edgeIter.next().clone());
        }

        assertEquals(g,g2);
        assertEquals(g2,g);
    }
    */

    public void testAddRemoveContains() {
    	Set<Node> nodeSet = new HashSet<Node>(g.nodeSet());
    	Set<Edge> edgeSet = new HashSet<Edge>(g.edgeSet());

        for (int i = 0; i < NR_EDGES; i++) {
             assertTrue(g.containsElement(e[i]));
             g.removeEdge(e[i]);
             assertTrue(! g.containsElement(e[i]));
        }

        for (int i = 0; i < NR_NODES_TOTAL; i++) {
             assertTrue(g.containsElement(n[i]));
             g.removeNode(n[i]);
             assertTrue(! g.containsElement(n[i]));
             g.addNode(n[i]);
        }

        for (int i = 0; i < NR_EDGES; i++) {
             g.addEdge(e[i]);
        }

        assertEquals(g.nodeSet(),nodeSet);
        assertEquals(g.edgeSet(),edgeSet);
    }

    public void testGetOutEdges() {
        Set<Edge> outEdges = new HashSet<Edge>();
        outEdges.add(e[0]);
        outEdges.add(e[1]);

        for (Edge edge: g.outEdgeSet(n[0])) {
            assertTrue(outEdges.contains(edge));
            outEdges.remove(edge);
        }
        assertTrue(outEdges.isEmpty());

        Iterator<? extends Edge> edgeIter = g.outEdgeSet(n[2]).iterator();
        assertTrue(! edgeIter.hasNext());

        edgeIter = g.outEdgeSet(n[8]).iterator();
        assertEquals(edgeIter.next(),e[6]);
        assertTrue(! edgeIter.hasNext());
    }

    public void testGetInEdges() {
        Set<Edge> inEdges = new HashSet<Edge>();
        inEdges.add(e[5]);
        inEdges.add(e[6]);

        for (Edge edge: g.edgeSet()) {
            inEdges.remove(edge);
        }
        assertTrue(inEdges.isEmpty());

        Iterator<Edge> edgeIter2 = inEdgeSet(g,n[5]).iterator();
        assertTrue(! edgeIter2.hasNext());

        edgeIter2 = inEdgeSet(g,n[6]).iterator();
        assertEquals(edgeIter2.next(),e[4]);
        assertTrue(! edgeIter2.hasNext());
    }

    public void testGetEdgesWithLabel() {
        Set<Edge> edges = new HashSet<Edge>();
        edges.add(e[0]);
        edges.add(e[2]);
        edges.add(e[5]);

        for (Edge edge: labelEdgeSet(g,DefaultLabel.createLabel(new String("a")))) {
            assertTrue(edges.contains(edge));
            edges.remove(edge);
        }
        assertEquals(new HashSet<Edge>(), edges);

        assertTrue(labelEdgeSet(g, DefaultLabel.createLabel("c")).isEmpty());
    }
    
    private Set<Edge> labelEdgeSet(Graph g, Label label) {
        Set<Edge> labelEdges = new HashSet<Edge>();
        for (Edge edge: g.edgeSet()) {
            if (edge.label().equals(label))
                labelEdges.add(edge);
        }
        return labelEdges;
    }
    
    private Set<Edge> inEdgeSet(Graph g, Node target) {
        Set<Edge> inEdges = new HashSet<Edge>();
        for (Edge edge: g.edgeSet()) {
            if (edge.opposite().equals(target))
                inEdges.add(edge);
        }
        return inEdges;
    }
}

