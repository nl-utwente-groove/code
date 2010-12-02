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
 * $Id: GraphTest.java,v 1.17 2008-01-30 09:32:47 iovka Exp $
 */
package groove.test.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import groove.graph.DefaultEdge;
import groove.graph.DefaultLabel;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.GraphFactory;
import groove.graph.GraphShape;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.iso.DefaultIsoChecker;
import groove.graph.iso.PartitionMap;
import groove.io.DefaultGxl;
import groove.io.ExtensionFilter;
import groove.io.Xml;
import groove.util.Groove;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Arend Rensink
 * @version $Revision$
 */
@SuppressWarnings("all")
public class GraphTest {
    static public final String MATCH_DOM_NAME = "match-dom-";
    static public final String MATCH_COD_NAME = "match-cod";
    static public final String ISO_GRAPH_NAME = "iso-";
    static public final String GRAPH_TEST_DIR = "junit/graphs";
    static public final int MATCH_DOM_COUNT = 4;
    static public final int ISO_GRAPH_COUNT = 4;

    static private final File GraphTestDir = new File(GRAPH_TEST_DIR);

    static private final ExtensionFilter gxlFilter = Groove.createGxlFilter();

    /**
     * The graph upon which most tests are done. It has three nodes, one of
     * which has an a-edge to another and a b-edge to the third.
     */
    public Graph graph;
    /** An a-label */
    public DefaultLabel aLabel;
    /** A b-label */
    public DefaultLabel bLabel;
    /** A c-label */
    public DefaultLabel cLabel;
    /** The a-edge of <tt>graph</tt>. */
    public Edge aEdge;
    /** The b-edge of <tt>graph</tt>. */
    public Edge bEdge;
    /** The source node of <tt>aEdge</tt> and <tt>bEdge</tt>. */
    public Node source;
    /** The target node of <tt>aEdge</tt>. */
    public Node aTarget;
    /** The target node of <tt>bEdge</tt>. */
    public Node bTarget;

    public Graph[] matchDom = new Graph[MATCH_DOM_COUNT];
    public Graph matchCod;
    public Graph[] isoGraph = new Graph[ISO_GRAPH_COUNT];

    public DefaultIsoChecker checker = DefaultIsoChecker.getInstance(true);

    private void setMarshaller(GraphFactory factory) {
        this.xml = new DefaultGxl(factory);
    }

    GraphFactory getFactory() {
        return GraphFactory.getInstance();
    }

    /*
     * @see TestCase#setUp()
     */
    @Before
    public void setUp() {
        this.setMarshaller(this.getFactory());
        for (int i = 0; i < this.matchDom.length; i++) {
            this.matchDom[i] = loadGraph(testFile(MATCH_DOM_NAME + i));
        }
        this.matchCod = loadGraph(testFile(MATCH_COD_NAME));
        for (int i = 0; i < this.isoGraph.length; i++) {
            this.isoGraph[i] = loadGraph(testFile(ISO_GRAPH_NAME + i));
        }
        this.graph = this.matchDom[0];
        this.aLabel = DefaultLabel.createLabel("a");
        this.bLabel = DefaultLabel.createLabel("b");
        this.cLabel = DefaultLabel.createLabel("c");

        Iterator<? extends Edge> edgeIter = this.graph.edgeSet().iterator();
        Edge edge1 = edgeIter.next();
        Edge edge2 = edgeIter.next();
        if (edge1.label().equals(this.aLabel)) {
            this.aEdge = edge1;
            this.bEdge = edge2;
        } else {
            this.aEdge = edge2;
            this.bEdge = edge1;
        }
        this.source = this.aEdge.source();
        this.aTarget = this.aEdge.target();
        this.bTarget = this.bEdge.target();
    }

    protected Graph loadGraph(File file) {
        Graph result = null;
        try {
            result = this.xml.unmarshalGraph(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private File testFile(String fileName) {
        return new File(GraphTestDir, gxlFilter.addExtension(fileName));
    }

    @Test
    final public void testIsoHashCode() {
        Object[] codes = new Object[MATCH_DOM_COUNT];
        for (int i = 0; i < codes.length; i++) {
            codes[i] =
                this.checker.getCertifier(this.matchDom[i], true).getGraphCertificate();
        }
        for (int i = 0; i < codes.length; i++) {
            for (int j = 0; j < codes.length; j++) {
                if (!codes[i].equals(codes[j])) {
                    assertFalse(this.checker.areIsomorphic(this.matchDom[i],
                        this.matchDom[j]));
                }
            }
        }
        codes = new Object[ISO_GRAPH_COUNT];
        for (int i = 0; i < codes.length; i++) {
            codes[i] =
                this.checker.getCertifier(this.isoGraph[i], true).getGraphCertificate();
        }
        for (int i = 0; i < codes.length; i++) {
            for (int j = 0; j < codes.length; j++) {
                if (!codes[i].equals(codes[j])) {
                    assertFalse(this.checker.areIsomorphic(this.isoGraph[i],
                        this.isoGraph[j]));
                }
            }
        }
    }

    @Test
    final public void testGetPartitionMap() {
        // iso-0
        PartitionMap nodePartitionMap =
            this.checker.getCertifier(this.isoGraph[0], true).getNodePartitionMap();
        assertEquals(this.isoGraph[0].nodeCount(), nodePartitionMap.size());
        PartitionMap edgePartitionMap =
            this.checker.getCertifier(this.isoGraph[0], true).getEdgePartitionMap();
        assertEquals(this.isoGraph[0].edgeCount(), edgePartitionMap.size());
        // iso-1
        nodePartitionMap =
            this.checker.getCertifier(this.isoGraph[1], true).getNodePartitionMap();
        assertEquals(this.isoGraph[1].nodeCount() - 1, nodePartitionMap.size());
        edgePartitionMap =
            this.checker.getCertifier(this.isoGraph[1], true).getEdgePartitionMap();
        assertEquals(this.isoGraph[1].edgeCount() - 1, edgePartitionMap.size());
        // iso-2
        nodePartitionMap =
            this.checker.getCertifier(this.isoGraph[2], true).getNodePartitionMap();
        assertEquals(this.isoGraph[2].nodeCount(), nodePartitionMap.size());
        edgePartitionMap =
            this.checker.getCertifier(this.isoGraph[2], true).getEdgePartitionMap();
        assertEquals(this.isoGraph[2].edgeCount(), edgePartitionMap.size());
        // iso-3
        nodePartitionMap =
            this.checker.getCertifier(this.isoGraph[3], true).getNodePartitionMap();
        assertEquals(this.isoGraph[3].nodeCount() - 2, nodePartitionMap.size());
        edgePartitionMap =
            this.checker.getCertifier(this.isoGraph[3], true).getEdgePartitionMap();
        assertEquals(this.isoGraph[3].edgeCount() - 3, edgePartitionMap.size());
    }

    @Test
    final public void testNewGraph() {
        Graph newGraph = this.matchDom[0].newGraph();
        assertEquals(0, newGraph.nodeCount());
        assertEquals(0, newGraph.edgeCount());
        assertFalse(newGraph.isFixed());
    }

    /*
     * Test for Node addNode()
     */
    @Test
    final public void testAddNode() {
        int oldNodeCount = this.matchDom[0].nodeCount();
        int oldEdgeCount = this.matchDom[0].edgeCount();
        Node newNode = this.matchDom[0].addNode();
        this.matchDom[0].containsElement(newNode);
        assertEquals(oldNodeCount + 1, this.matchDom[0].nodeCount());
        assertEquals(oldEdgeCount, this.matchDom[0].edgeCount());
        assertTrue(this.graph.containsElement(this.graph.addNode()));
    }

    /*
     * Test for BinaryEdge addEdge(Node, Label, Node)
     */
    @Test
    final public void testAddEdgeNodeLabelNode() {
        int oldNodeCount = this.matchDom[0].nodeCount();
        int oldEdgeCount = this.matchDom[0].edgeCount();
        Iterator<? extends Node> nodeIter =
            this.matchDom[0].nodeSet().iterator();
        Node sourceNode = nodeIter.next();
        Node targetNode = nodeIter.next();
        Label label = DefaultLabel.createLabel("test");
        Edge newEdge = this.matchDom[0].addEdge(sourceNode, label, targetNode);
        newEdge.source().equals(sourceNode);
        newEdge.target().equals(targetNode);
        newEdge.label().equals(label);
        this.matchDom[0].containsElement(newEdge);
        assertEquals(oldNodeCount, this.matchDom[0].nodeCount());
        assertEquals(oldEdgeCount + 1, this.matchDom[0].edgeCount());
        assertTrue(this.graph.containsElement(this.graph.addEdge(this.aTarget,
            this.cLabel, this.aTarget)));
    }

    @Test
    final public void testNodeSet() {
        Collection nodeSet = this.matchDom[0].nodeSet();
        assertEquals(3, nodeSet.size());
        try {
            nodeSet.add(DefaultNode.createNode());
            fail("Addition to node set should not have been allowed");
        } catch (UnsupportedOperationException exc) {
            // proceed
        }
    }

    @Test
    final public void testNodeCount() {
        assertEquals(3, this.matchDom[0].nodeCount());
    }

    @Test
    final public void testEdgeSet() {
        Collection edgeSet = this.matchDom[0].edgeSet();
        assertEquals(2, edgeSet.size());
        try {
            edgeSet.add(DefaultEdge.createEdge(DefaultNode.createNode(), "",
                DefaultNode.createNode()));
            fail("Addition to node set should not have been allowed");
        } catch (UnsupportedOperationException exc) {
            // proceed
        }
    }

    @Test
    final public void testEdgeCount() {
        assertEquals(2, this.matchDom[0].edgeCount());
    }

    @Test
    final public void testOutEdgeSet() {
        Set<Edge> abEdgeSet = new HashSet<Edge>();
        abEdgeSet.add(this.aEdge);
        abEdgeSet.add(this.bEdge);
        assertEquals(abEdgeSet, this.graph.outEdgeSet(this.source));
        assertEquals(new HashSet<Edge>(), this.graph.outEdgeSet(this.aTarget));
        // these sets should be unmodifiable
        Collection<Edge> bOutEdges =
            (Collection<Edge>) this.graph.outEdgeSet(this.bTarget);
        try {
            bOutEdges.add(this.aEdge);
            fail("Adding to outgoing edge set should not have been allowed");
        } catch (UnsupportedOperationException exc) {
            // proceed
        }
        // if we add an edge to the graph, that should be visible
        Edge cEdge =
            this.graph.addEdge(this.bTarget, this.cLabel, this.aTarget);
        bOutEdges = new HashSet<Edge>();
        bOutEdges.add(cEdge);
        assertEquals(bOutEdges, this.graph.outEdgeSet(this.bTarget));
    }

    @Test
    final public void testLabelEdgeSet() {
        Set<Edge> aEdgeSet = new HashSet<Edge>();
        aEdgeSet.add(this.aEdge);
        Set<Edge> bEdgeSet = new HashSet<Edge>();
        bEdgeSet.add(this.bEdge);
        Set<Edge> cEdgeSet = new HashSet<Edge>();
        assertEquals(aEdgeSet, this.graph.labelEdgeSet(this.aLabel));
        assertEquals(bEdgeSet, this.graph.labelEdgeSet(this.bLabel));
        assertEquals(cEdgeSet, this.graph.labelEdgeSet(this.cLabel));
        // if we add an edge to the graph, that should be visible
        Edge cEdge =
            this.graph.addEdge(this.bTarget, this.cLabel, this.aTarget);
        cEdgeSet.add(cEdge);
        assertEquals(cEdgeSet, this.graph.labelEdgeSet(this.cLabel));
    }

    @Test
    final public void testSize() {
        assertEquals(5, this.graph.size());
        this.graph.addEdge(this.aTarget, this.cLabel, this.bTarget);
        assertEquals(6, this.graph.size());
        this.graph.removeNode(this.aTarget);
        assertEquals(3, this.graph.size());
    }

    @Test
    final public void testIsEmpty() {
        assertFalse(this.graph.isEmpty());
        this.graph.removeNodeSet(new HashSet<Node>(this.graph.nodeSet()));
        assertTrue(this.graph.isEmpty());
        assertTrue(this.graph.newGraph().isEmpty());
    }

    @Test
    final public void testIsFixed() {
        assertFalse(this.graph.isFixed());
        this.graph.setFixed();
        assertTrue(this.graph.isFixed());
        assertFalse(this.graph.newGraph().isFixed());
    }

    @Test
    final public void testContainsElement() {
        assertTrue(this.graph.containsElement(this.source));
        assertTrue(this.graph.containsElement(this.aEdge));
        assertFalse(this.graph.containsElement(DefaultNode.createNode()));
        assertFalse(this.graph.containsElement(DefaultEdge.createEdge(
            this.aTarget, this.cLabel, this.aTarget)));
        assertTrue(this.graph.containsElement(DefaultEdge.createEdge(
            this.source, this.aLabel, this.aTarget)));
    }

    @Test
    final public void testContainsElementSet() {
        Set<Element> elementSet = new HashSet<Element>();
        assertTrue(this.graph.containsElementSet(elementSet));
        elementSet.add(this.source);
        assertTrue(this.graph.containsElementSet(elementSet));
        elementSet.add(this.aEdge);
        assertTrue(this.graph.containsElementSet(elementSet));
        elementSet.add(DefaultEdge.createEdge(this.source, this.bLabel,
            this.bTarget));
        assertTrue(this.graph.containsElementSet(elementSet));
        elementSet.add(DefaultEdge.createEdge(this.aTarget, this.cLabel,
            this.bTarget));
        assertFalse(this.graph.containsElementSet(elementSet));
    }

    /*
     * Test for boolean addNode(Node)
     */
    @Test
    final public void testAddNodeNode() {
        assertFalse(this.graph.addNode(this.source));
        Node newNode = DefaultNode.createNode();
        assertTrue(this.graph.addNode(newNode));
        assertTrue(this.graph.containsElement(newNode));
    }

    /*
     * Test for boolean addEdge(Edge)
     */
    @Test
    final public void testAddEdgeEdge() {
        assertFalse(this.graph.addEdge(this.aEdge));
        assertFalse(this.graph.addEdge(DefaultEdge.createEdge(this.source,
            this.aLabel, this.aTarget)));
        Edge newEdge =
            DefaultEdge.createEdge(this.aTarget, this.cLabel, this.bTarget);
        assertTrue(this.graph.addEdge(newEdge));
        assertTrue(this.graph.containsElement(newEdge));
        Node newNode = DefaultNode.createNode();
        newEdge = DefaultEdge.createEdge(this.bTarget, this.cLabel, newNode);
        assertTrue(this.graph.addEdge(newEdge));
        assertTrue(this.graph.containsElement(newNode));
    }

    @Test
    final public void testAddNodeSet() {
        Set<Node> nodeSet = new HashSet<Node>();
        assertFalse(this.graph.addNodeSet(nodeSet));
        nodeSet.add(this.source);
        assertFalse(this.graph.addNodeSet(nodeSet));
        Node newNode = DefaultNode.createNode();
        nodeSet.add(newNode);
        assertTrue(this.graph.addNodeSet(nodeSet));
        assertTrue(this.graph.containsElement(newNode));
    }

    @Test
    final public void testAddEdgeSet() {
        Set<Edge> edgeSet = new HashSet<Edge>();
        assertFalse(this.graph.addEdgeSet(edgeSet));
        edgeSet.add(this.aEdge);
        assertFalse(this.graph.addEdgeSet(edgeSet));
        edgeSet.add(DefaultEdge.createEdge(this.source, this.bLabel,
            this.bTarget));
        assertFalse(this.graph.addEdgeSet(edgeSet));
        Node newNode = DefaultNode.createNode();
        Edge newEdge =
            DefaultEdge.createEdge(this.bTarget, this.cLabel, newNode);
        edgeSet.add(newEdge);
        assertTrue(this.graph.addEdgeSet(edgeSet));
        assertTrue(this.graph.containsElement(newNode));
    }

    @Test
    final public void testRemoveNode() {
        Node newNode = DefaultNode.createNode();
        assertFalse(this.graph.removeNode(newNode));
        this.graph.addNode(newNode);
        assertTrue(this.graph.removeNode(newNode));
        assertTrue(this.graph.removeNode(this.source));
        assertFalse(this.graph.containsElement(this.source));
        assertFalse(this.graph.containsElement(this.aEdge));
        assertFalse(this.graph.removeNode(this.source));
    }

    @Test
    final public void testRemoveEdge() {
        Edge newEdge =
            DefaultEdge.createEdge(this.source, this.bLabel, this.aTarget);
        assertFalse(this.graph.removeEdge(newEdge));
        this.graph.addEdge(newEdge);
        assertTrue(this.graph.removeEdge(newEdge));
        assertTrue(this.graph.removeEdge(this.aEdge));
        assertTrue(this.graph.containsElement(this.source));
        assertFalse(this.graph.containsElement(this.aEdge));
        assertFalse(this.graph.removeEdge(this.aEdge));
        assertTrue(this.graph.removeEdge(DefaultEdge.createEdge(this.source,
            this.bLabel, this.bTarget)));
    }

    @Test
    final public void testRemoveNodeSet() {
        Set<Node> nodeSet = new HashSet<Node>();
        assertFalse(this.graph.removeNodeSet(nodeSet));
        Node newNode = DefaultNode.createNode();
        nodeSet.add(newNode);
        assertFalse(this.graph.removeNodeSet(nodeSet));
        this.graph.addNode(newNode);
        assertTrue(this.graph.removeNodeSet(nodeSet));
        assertFalse(this.graph.containsElement(newNode));
        nodeSet.add(this.source);
        assertTrue(this.graph.removeNodeSet(nodeSet));
        assertFalse(this.graph.containsElement(this.source));
        assertFalse(this.graph.containsElement(this.aEdge));
    }

    @Test
    final public void testRemoveEdgeSet() {
        Set<Edge> edgeSet = new HashSet<Edge>();
        assertFalse(this.graph.removeEdgeSet(edgeSet));
        Node newNode = DefaultNode.createNode();
        Edge newEdge =
            DefaultEdge.createEdge(this.bTarget, this.cLabel, newNode);
        edgeSet.add(newEdge);
        assertFalse(this.graph.removeEdgeSet(edgeSet));
        edgeSet.add(DefaultEdge.createEdge(this.source, this.bLabel,
            this.bTarget));
        edgeSet.add(this.aEdge);
        assertTrue(this.graph.removeEdgeSet(edgeSet));
        assertFalse(this.graph.containsElement(this.bEdge));
        assertFalse(this.graph.containsElement(this.aEdge));
        assertFalse(this.graph.removeEdgeSet(edgeSet));
    }

    @Test
    final public void testSetFixed() {
        this.graph.setFixed();
        assertTrue(this.graph.isFixed());
        // fixedness of the graph is not tested, it is a precondition
        // so there is little else to do here
    }

    @Test
    final public void testAddGraphListenerAdd() {
        Graph graph1 = this.matchCod;
        GraphListener listener = new GraphListener();
        listener.addGraph(this.graph);
        this.graph.addGraphListener(listener);
        listener.addGraph(graph1);
        graph1.addGraphListener(listener);
        List<Element> addedGraphElements = new LinkedList<Element>();
        // add a fresh node using addNode()
        Node addedNode = this.graph.addNode();
        addedGraphElements.add(addedNode);
        // add a fresh node using addNode(Node)
        addedNode = DefaultNode.createNode();
        this.graph.addNode(addedNode);
        addedGraphElements.add(addedNode);
        // add a fresh edge using addEdge(Node,Label,Node)
        Edge addedEdge =
            this.graph.addEdge(this.bTarget, this.aLabel, this.aTarget);
        addedGraphElements.add(addedEdge);
        // add an existing edge using addEdge(Node,Label,Node)
        this.graph.addEdge(this.source, this.aLabel, this.aTarget);
        // add a fresh edge with a fresh end node
        addedNode = DefaultNode.createNode();
        addedEdge =
            DefaultEdge.createEdge(this.aTarget, this.cLabel, addedNode);
        this.graph.addEdge(addedEdge);
        addedGraphElements.add(addedNode);
        addedGraphElements.add(addedEdge);
        // add a set of nodes
        Collection<Node> nodeSet = new LinkedList<Node>();
        nodeSet.add(DefaultNode.createNode());
        nodeSet.add(DefaultNode.createNode());
        nodeSet.add(DefaultNode.createNode());
        addedGraphElements.addAll(nodeSet);
        this.graph.addNodeSet(nodeSet);
        // add a set of edges
        Collection<Edge> edgeSet = new LinkedList<Edge>();
        addedNode = DefaultNode.createNode();
        addedEdge =
            DefaultEdge.createEdge(this.bTarget, this.cLabel, addedNode);
        addedGraphElements.add(addedNode);
        addedGraphElements.add(addedEdge);
        edgeSet.add(addedEdge);
        addedEdge =
            DefaultEdge.createEdge(this.source, this.aLabel, this.aTarget);
        edgeSet.add(addedEdge);
        addedEdge =
            DefaultEdge.createEdge(this.source, this.bLabel, this.aTarget);
        edgeSet.add(addedEdge);
        addedGraphElements.add(addedEdge);
        this.graph.addEdgeSet(edgeSet);
        // now test if we did ok
        assertEquals(addedGraphElements, listener.added.get(this.graph));
    }

    @Test
    final public void testAddGraphListenerRemove() {
        GraphListener listener = new GraphListener();
        listener.addGraph(this.graph);
        this.graph.addGraphListener(listener);
        List<Element> removedGraphElements = new LinkedList<Element>();
        // try to remove a non-existent edge
        Edge removedEdge =
            DefaultEdge.createEdge(this.aTarget, this.aLabel, this.aTarget);
        this.graph.removeEdge(removedEdge);
        assertEquals(removedGraphElements, listener.removed.get(this.graph));
        // remove an existing edge
        this.graph.removeEdge(this.bEdge);
        removedGraphElements.add(this.bEdge);
        assertEquals(removedGraphElements, listener.removed.get(this.graph));
        // try to remove a non-existent node
        Node removedNode = DefaultNode.createNode();
        this.graph.removeNode(removedNode);
        assertEquals(removedGraphElements, listener.removed.get(this.graph));
        // remove an existing node
        this.graph.removeNode(this.aTarget);
        removedGraphElements.add(this.aEdge);
        removedGraphElements.add(this.aTarget);
        assertEquals(removedGraphElements, listener.removed.get(this.graph));
        // add the nodes and edges again, to continue testing
        this.graph.addEdge(this.aEdge);
        this.graph.addEdge(this.bEdge);
        assertEquals(removedGraphElements, listener.removed.get(this.graph));
        // remove a set of nodes
        Collection<Node> nodeSet = new LinkedList<Node>();
        nodeSet.add(DefaultNode.createNode());
        nodeSet.add(this.bTarget);
        removedGraphElements.add(this.bEdge);
        removedGraphElements.add(this.bTarget);
        this.graph.removeNodeSet(nodeSet);
        assertEquals(removedGraphElements, listener.removed.get(this.graph));
        // remove a set of edges
        Collection<Edge> edgeSet = new LinkedList<Edge>();
        edgeSet.add(DefaultEdge.createEdge(this.bTarget, this.cLabel,
            DefaultNode.createNode()));
        edgeSet.add(this.aEdge);
        removedGraphElements.add(this.aEdge);
        this.graph.removeEdgeSet(edgeSet);
        // now test if we did ok
        assertEquals(removedGraphElements, listener.removed.get(this.graph));
    }

    @Test
    final public void testRemoveGraphListener() {
        GraphListener listener = new GraphListener();
        this.graph.addGraphListener(listener);
    }

    private class GraphListener implements groove.graph.GraphListener {
        /** Empty constructor with the correct visibility. */
        GraphListener() {
            // empty
        }

        public void addUpdate(GraphShape graph, Node node) {
            assertTrue(this.listeningTo.contains(graph));
            this.added.get(graph).add(node);
        }

        public void addUpdate(GraphShape graph, Edge edge) {
            assertTrue(this.listeningTo.contains(graph));
            this.added.get(graph).add(edge);
        }

        public void removeUpdate(GraphShape graph, Node node) {
            assertTrue(this.listeningTo.contains(graph));
            this.removed.get(graph).add(node);
        }

        public void removeUpdate(GraphShape graph, Edge elem) {
            assertTrue(this.listeningTo.contains(graph));
            this.removed.get(graph).add(elem);
        }

        public void replaceUpdate(GraphShape graph, Node from, Node to) {
            assertTrue(this.listeningTo.contains(graph));
            this.replacedFrom.get(graph).add(from);
            this.replacedBy.get(graph).add(to);
        }

        public void replaceUpdate(GraphShape graph, Edge elem1, Edge elem2) {
            assertTrue(this.listeningTo.contains(graph));
            this.replacedFrom.get(graph).add(elem1);
            this.replacedBy.get(graph).add(elem2);
        }

        public void addGraph(Graph graph) {
            assertFalse(this.listeningTo.contains(graph));
            this.added.put(graph, new LinkedList<Element>());
            this.removed.put(graph, new LinkedList<Element>());
            this.replacedFrom.put(graph, new LinkedList<Element>());
            this.replacedBy.put(graph, new LinkedList<Element>());
        }

        Map<Graph,List<Element>> added = new HashMap<Graph,List<Element>>();
        Map<Graph,List<Element>> removed = new HashMap<Graph,List<Element>>();
        Map<Graph,List<Element>> replacedFrom =
            new HashMap<Graph,List<Element>>();
        Map<Graph,List<Element>> replacedBy =
            new HashMap<Graph,List<Element>>();
        Set<Graph> listeningTo = this.added.keySet();
    }

    private Xml<Graph> xml;
}
