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
 * $Id: GraphTest.java,v 1.1.1.2 2007-03-20 10:42:55 kastenberg Exp $
 */
package groove.test.graph;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import groove.graph.BinaryEdge;
import groove.graph.DefaultEdge;
import groove.graph.DefaultLabel;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.GraphFactory;
import groove.graph.GraphShape;
import groove.graph.InjectiveMorphism;
import groove.graph.Label;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.iso.DefaultIsoChecker;
import groove.graph.iso.IsoChecker;
import groove.io.ExtensionFilter;
import groove.io.UntypedGxl;
import groove.io.Xml;
import groove.util.Groove;
import junit.framework.TestCase;

/**
 * 
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $
 */
public class GraphTest extends TestCase {
    static public final String MATCH_DOM_NAME = "match-dom-";
    static public final String MATCH_COD_NAME = "match-cod";
    static public final String ISO_GRAPH_NAME = "iso-";
    static public final String GRAPH_TEST_DIR = "junit/graphs";
    static public final int MATCH_DOM_COUNT = 4;
    static public final int ISO_GRAPH_COUNT = 4;

    static private final File GraphTestDir = new File(GRAPH_TEST_DIR);

    static private final ExtensionFilter gxlFilter = Groove.createGxlFilter();

    static private final IsoChecker isoChecker = new DefaultIsoChecker();
    
    /** 
     * The graph upon which most tests are done.
     * It has three nodes, one of which has an a-edge to another and a b-edge to the third.
     */
    public Graph graph;
    /** An a-label */
    public Label aLabel;
    /** A b-label */
    public Label bLabel;
    /** A c-label */
    public Label cLabel;
    /** The a-edge of <tt>graph</tt>. */
    public BinaryEdge aEdge;
    /** The b-edge of <tt>graph</tt>. */
    public BinaryEdge bEdge;
    /** The source node of <tt>aEdge</tt> and <tt>bEdge</tt>. */
    public Node source;
    /** The target node of <tt>aEdge</tt>. */
    public Node aTarget;
    /** The target node of <tt>bEdge</tt>. */
    public Node bTarget;

    public Graph[] matchDom = new Graph[MATCH_DOM_COUNT];
    public Graph matchCod;
    public Graph[] isoGraph = new Graph[ISO_GRAPH_COUNT];

    /**
     * Constructor for GraphTest, with specific graph factory
     * @param arg0 JUnit parameter
     * @param factory the graph factory according to which the graphs to be tested are built
     */
    public GraphTest(String arg0, GraphFactory factory) {
        super(arg0);
//        this.graphFactory = factory;
        this.xml = new UntypedGxl(factory);
    }

    /**
     * Constructor for GraphTest, with specific graph factory
     * @param arg0 JUnit parameter
     * @param factoryGraph the graph to be used in the factory 
     * according to which the graphs to be tested are built
     */
    public GraphTest(String arg0, Graph factoryGraph) {
        this(arg0, GraphFactory.newInstance(factoryGraph));
    }

    /**
     * Constructor for GraphTest, with default graph factory
     * @param arg0 JUnit parameter
     */
    public GraphTest(String arg0) {
        this(arg0, GraphFactory.newInstance());
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        for (int i = 0; i < matchDom.length; i++) {
            matchDom[i] = loadGraph(testFile(MATCH_DOM_NAME + i));
        }
        matchCod = xml.unmarshal(testFile(MATCH_COD_NAME));
        for (int i = 0; i < isoGraph.length; i++) {
            isoGraph[i] = loadGraph(testFile(ISO_GRAPH_NAME + i));
        }
        graph = matchDom[0];
        aLabel = DefaultLabel.createLabel("a");
        bLabel = DefaultLabel.createLabel("b");
        cLabel = DefaultLabel.createLabel("c");

        Iterator<? extends Edge> edgeIter = graph.edgeSet().iterator();
        BinaryEdge edge1 = (BinaryEdge) edgeIter.next();
        BinaryEdge edge2 = (BinaryEdge) edgeIter.next();
        if (edge1.label().equals(aLabel)) {
            aEdge = edge1;
            bEdge = edge2;
        } else {
            aEdge = edge2;
            bEdge = edge1;
        }
        source = aEdge.source();
        aTarget = aEdge.target();
        bTarget = bEdge.target();
    }
    
    protected Graph loadGraph(File file) throws Exception {
        return xml.unmarshal(file);
    }
    
    private File testFile(String fileName) {
        return new File(GraphTestDir, gxlFilter.addExtension(fileName));
    }

    final public void testGetMatchesTo() {
        Collection<? extends Morphism> matches = matchDom[0].getMatchesTo(matchCod);
        assertEquals(4, matches.size());
        Iterator<? extends Morphism> matchIter = matches.iterator();
        while (matchIter.hasNext()) {
            Object match = matchIter.next();
            assertTrue(match instanceof Morphism);
            Morphism matchAsMorphism = (Morphism) match;
            assertTrue(matchAsMorphism.isTotal());
        }
        matches = matchDom[1].getMatchesTo(matchCod);
        assertEquals(2, matches.size());
        matches = matchDom[2].getMatchesTo(matchCod);
        assertEquals(2, matches.size());
        matches = matchDom[3].getMatchesTo(matchCod);
        assertEquals(1, matches.size());
    }

    final public void testGetInjectiveMatchesTo() {
        Collection<? extends Morphism> matches = matchDom[0].getInjectiveMatchesTo(matchCod);
        assertEquals(1, matches.size());
        Iterator<? extends Morphism> matchIter = matches.iterator();
        while (matchIter.hasNext()) {
            Object match = matchIter.next();
            assertTrue(match instanceof InjectiveMorphism);
            Morphism matchAsMorphism = (Morphism) match;
            assertTrue(matchAsMorphism.isTotal());
        }
        matches = matchDom[1].getInjectiveMatchesTo(matchCod);
        assertEquals(1, matches.size());
        matches = matchDom[2].getInjectiveMatchesTo(matchCod);
        assertEquals(1, matches.size());
        matches = matchDom[3].getInjectiveMatchesTo(matchCod);
        assertEquals(1, matches.size());
        matches = matchDom[1].getInjectiveMatchesTo(matchDom[3]);
        assertEquals(0, matches.size());
    }

    final public void testGetIsomorphismTo() {
        InjectiveMorphism iso = matchDom[0].getIsomorphismTo(isoGraph[0]);
        assertNotNull(iso);
        assertTrue(iso.isTotal());
        assertTrue(iso.isSurjective());
        for (int i = 0; i < ISO_GRAPH_COUNT; i++) {
            for (int j = 0; j < ISO_GRAPH_COUNT; j++) {
                iso = isoGraph[i].getIsomorphismTo(isoGraph[j]);
                if (i == j) {
                    assertNotNull(iso);
                    assertTrue(iso.isTotal());
                    assertTrue(iso.isSurjective());
                } else {
                    assertNull(iso);
                }
            }
        }
    }

    final public void testIsoHashCode() {
    	IsoChecker checker = new DefaultIsoChecker();
        Object[] codes = new Object[MATCH_DOM_COUNT];
        for (int i = 0; i < codes.length; i++) {
            codes[i] = matchDom[i].getCertificate();
        }
        for (int i = 0; i < codes.length; i++) {
            for (int j = 0; j < codes.length; j++) {
                if (!codes[i].equals(codes[j])) {
                    assertFalse(checker.areIsomorphic(matchDom[i],matchDom[j]));
                }
            }
        }
        codes = new Object[ISO_GRAPH_COUNT];
        for (int i = 0; i < codes.length; i++) {
            codes[i] = isoGraph[i].getCertificate();
        }
        for (int i = 0; i < codes.length; i++) {
            for (int j = 0; j < codes.length; j++) {
                if (!codes[i].equals(codes[j])) {
                    assertFalse(checker.areIsomorphic(isoGraph[i],isoGraph[j]));
                }
            }
        }
    }

    final public void testGetPartitionMap() {
        // iso-0
        Map<?,?> partitionMap = isoGraph[0].getCertificateStrategy().getPartitionMap();
        Collection<?> elements = getPartitionElements(partitionMap.values());
        int elementCount = isoGraph[0].nodeCount() + isoGraph[0].edgeCount();
        assertEquals(elementCount, elements.size());
        assertEquals(elementCount, partitionMap.values().size());
        // iso-1
        partitionMap = isoGraph[1].getCertificateStrategy().getPartitionMap();
        elements = getPartitionElements(partitionMap.values());
        elementCount = isoGraph[1].nodeCount() + isoGraph[1].edgeCount();
        assertEquals(elementCount, elements.size());
        assertEquals(elementCount - 2, partitionMap.values().size());
        // iso-2
        partitionMap = isoGraph[2].getCertificateStrategy().getPartitionMap();
        elements = getPartitionElements(partitionMap.values());
        elementCount = isoGraph[2].nodeCount() + isoGraph[2].edgeCount();
        assertEquals(elementCount, elements.size());
        assertEquals(elementCount, partitionMap.values().size());
        // iso-3
        partitionMap = isoGraph[3].getCertificateStrategy().getPartitionMap();
        elements = getPartitionElements(partitionMap.values());
        elementCount = isoGraph[3].nodeCount() + isoGraph[3].edgeCount();
        assertEquals(elementCount, elements.size());
        assertTrue((elementCount - 5) >= partitionMap.values().size());
    }
    
    private Collection<Object> getPartitionElements(Collection<?> values) {
        Set<Object> result = new HashSet<Object>();
        Iterator<?> valueIter = values.iterator();
        while (valueIter.hasNext()) {
            Object value = valueIter.next();
            if (value instanceof Collection) {
                result.addAll((Collection) value);
            } else {
                result.add(value);
            }
        }
        return result;
    }

    final public void testClone() {
        // graphs and clones should have equals node and edge sets
        for (int i = 0; i < matchDom.length; i++) {
            Object clone = matchDom[i].clone();
            assertTrue(clone instanceof Graph);
            assertTrue(isoChecker.areIsomorphic((Graph) clone,matchDom[i]));
        }
        // changes to the graph should not affect the clone
        Graph clone = graph.clone();
        int cloneSize = clone.size();
        graph.addNode();
        graph.addEdge(aTarget, cLabel, bTarget);
        assertEquals(cloneSize, clone.size());
    }

    final public void testNewGraph() {
        Graph newGraph = matchDom[0].newGraph();
        assertEquals(0, newGraph.nodeCount());
        assertEquals(0, newGraph.edgeCount());
        assertFalse(newGraph.isFixed());
    }

    /*
     * Test for Node addNode()
     */
    final public void testAddNode() {
        int oldNodeCount = matchDom[0].nodeCount();
        int oldEdgeCount = matchDom[0].edgeCount();
        Node newNode = matchDom[0].addNode();
        matchDom[0].containsElement(newNode);
        assertEquals(oldNodeCount + 1, matchDom[0].nodeCount());
        assertEquals(oldEdgeCount, matchDom[0].edgeCount());
        assertTrue(graph.containsElement(graph.addNode()));
    }

    /*
     * Test for BinaryEdge addEdge(Node, Label, Node)
     */
    final public void testAddEdgeNodeLabelNode() {
        int oldNodeCount = matchDom[0].nodeCount();
        int oldEdgeCount = matchDom[0].edgeCount();
        Iterator<? extends Node> nodeIter = matchDom[0].nodeSet().iterator();
        Node sourceNode = nodeIter.next();
        Node targetNode = nodeIter.next();
        Label label = DefaultLabel.createLabel("test");
        Edge newEdge = matchDom[0].addEdge(sourceNode, label, targetNode);
        newEdge.source().equals(sourceNode);
        ((BinaryEdge) newEdge).target().equals(targetNode);
        newEdge.label().equals(label);
        matchDom[0].containsElement(newEdge);
        assertEquals(oldNodeCount, matchDom[0].nodeCount());
        assertEquals(oldEdgeCount + 1, matchDom[0].edgeCount());
        assertTrue(graph.containsElement(graph.addEdge(aTarget, cLabel, aTarget)));
    }

    final public void testNodeSet() {
        Collection nodeSet = matchDom[0].nodeSet();
        assertEquals(3, nodeSet.size());
        try {
            nodeSet.add(new DefaultNode());
            fail("Addition to node set should not have been allowed");
        } catch (UnsupportedOperationException exc) {
            // proceed
        }
    }

    final public void testNodeCount() {
        assertEquals(3, matchDom[0].nodeCount());
    }

    final public void testEdgeSet() {
        Collection edgeSet = matchDom[0].edgeSet();
        assertEquals(2, edgeSet.size());
        try {
            edgeSet.add(DefaultEdge.createEdge(new DefaultNode(), "", new DefaultNode()));
            fail("Addition to node set should not have been allowed");
        } catch (UnsupportedOperationException exc) {
            // proceed
        }
    }

    final public void testEdgeCount() {
        assertEquals(2, matchDom[0].edgeCount());
    }

    final public void testOutEdgeSet() {
        Set<Edge> abEdgeSet = new HashSet<Edge>();
        abEdgeSet.add(aEdge);
        abEdgeSet.add(bEdge);
        assertEquals(abEdgeSet, graph.outEdgeSet(source));
        assertEquals(new HashSet<Edge>(), graph.outEdgeSet(aTarget));
        // these sets should be unmodifiable
        Collection<Edge> bOutEdges = (Collection<Edge>) graph.outEdgeSet(bTarget);
        try {
            bOutEdges.add(aEdge);
            fail("Adding to outgoing edge set should not have been allowed");
        } catch (UnsupportedOperationException exc) {
            // proceed
        }
        // if we add an edge to the graph, that should be visible
        Edge cEdge = graph.addEdge(bTarget, cLabel, aTarget);
        bOutEdges = new HashSet<Edge>();
        bOutEdges.add(cEdge);
        assertEquals(bOutEdges, graph.outEdgeSet(bTarget));
    }
//
//    final public void testOutEdgeMap() {
//        Set abEdgeSet = new HashSet();
//        abEdgeSet.add(aEdge);
//        abEdgeSet.add(bEdge);
//        Map outEdgeMap = new HashMap();
//        outEdgeMap.put(source, abEdgeSet);
//        //        outEdgeMap.put(aTarget, new HashSet());
//        //        outEdgeMap.put(bTarget, new HashSet());
//        assertEquals(outEdgeMap, graph.outEdgeMap());
//        // the map should be unmodifiable
//        try {
//            graph.outEdgeMap().put(aTarget, aEdge);
//            fail("Adding to outgoing edge set should not have been allowed");
//        } catch (UnsupportedOperationException exc) {
//            // proceed
//        }
//        // if we add an edge to the graph, that should be visible
//        Edge cEdge = graph.addEdge(bTarget, cLabel, aTarget);
//        Set cEdgeSet = new HashSet();
//        cEdgeSet.add(cEdge);
//        outEdgeMap.put(bTarget, cEdgeSet);
//        assertEquals(outEdgeMap, graph.outEdgeMap());
//    }

    final public void testLabelEdgeSet() {
        Set<Edge> aEdgeSet = new HashSet<Edge>();
        aEdgeSet.add(aEdge);
        Set<Edge> bEdgeSet = new HashSet<Edge>();
        bEdgeSet.add(bEdge);
        Set<Edge> cEdgeSet = new HashSet<Edge>();
        assertEquals(new HashSet<Edge>(), graph.labelEdgeSet(1, aLabel));
        assertEquals(aEdgeSet, graph.labelEdgeSet(2, aLabel));
        assertEquals(bEdgeSet, graph.labelEdgeSet(2, bLabel));
        assertEquals(cEdgeSet, graph.labelEdgeSet(2, cLabel));
        // if we add an edge to the graph, that should be visible
        Edge cEdge = graph.addEdge(bTarget, cLabel, aTarget);
        cEdgeSet.add(cEdge);
        assertEquals(cEdgeSet, graph.labelEdgeSet(2, cLabel));
    }

    final public void testLabelEdgeMap() {
        // prepare the expected map
        Set<Edge> aEdgeSet = new HashSet<Edge>();
        aEdgeSet.add(aEdge);
        Set<Edge> bEdgeSet = new HashSet<Edge>();
        bEdgeSet.add(bEdge);
        Map<Label,Set<Edge>> labelEdgeMap = new HashMap<Label,Set<Edge>>();
        labelEdgeMap.put(aLabel, aEdgeSet);
        labelEdgeMap.put(bLabel, bEdgeSet);
        // now test it
        assertEquals(new HashMap<Label,Set<Edge>>(), graph.labelEdgeMap(1));
        assertEquals(labelEdgeMap, graph.labelEdgeMap(2));
        // if we add an edge to the graph, that should be visible
        Edge anotherAEdge = graph.addEdge(bTarget, aLabel, source);
        aEdgeSet.add(anotherAEdge);
        Edge cEdge = graph.addEdge(bTarget, cLabel, aTarget);
        Set<Edge> cEdgeSet = new HashSet<Edge>();
        cEdgeSet.add(cEdge);
        labelEdgeMap.put(cLabel, cEdgeSet);
        assertEquals(cEdgeSet, graph.labelEdgeSet(2, cLabel));
    }

    final public void testSize() {
        assertEquals(5, graph.size());
        graph.addEdge(aTarget, cLabel, bTarget);
        assertEquals(6, graph.size());
        graph.removeNode(aTarget);
        assertEquals(3, graph.size());
    }

    final public void testIsEmpty() {
        assertFalse(graph.isEmpty());
        graph.removeNodeSet(new HashSet<Node>(graph.nodeSet()));
        assertTrue(graph.isEmpty());
        assertTrue(graph.newGraph().isEmpty());
    }

    final public void testIsFixed() {
        assertFalse(graph.isFixed());
        graph.setFixed();
        assertTrue(graph.isFixed());
        assertFalse(graph.newGraph().isFixed());
    }

    final public void testContainsElement() {
        assertTrue(graph.containsElement(source));
        assertTrue(graph.containsElement(aEdge));
        assertFalse(graph.containsElement(new DefaultNode()));
        assertFalse(graph.containsElement(DefaultEdge.createEdge(aTarget, cLabel, aTarget)));
        assertTrue(graph.containsElement(DefaultEdge.createEdge(source, aLabel, aTarget)));
    }

    final public void testContainsElementSet() {
        Set<Element> elementSet = new HashSet<Element>();
        assertTrue(graph.containsElementSet(elementSet));
        elementSet.add(source);
        assertTrue(graph.containsElementSet(elementSet));
        elementSet.add(aEdge);
        assertTrue(graph.containsElementSet(elementSet));
        elementSet.add(DefaultEdge.createEdge(source, bLabel, bTarget));
        assertTrue(graph.containsElementSet(elementSet));
        elementSet.add(DefaultEdge.createEdge(aTarget, cLabel, bTarget));
        assertFalse(graph.containsElementSet(elementSet));
    }

    /*
     * Test for boolean addNode(Node)
     */
    final public void testAddNodeNode() {
        assertFalse(graph.addNode(source));
        Node newNode = new DefaultNode();
        assertTrue(graph.addNode(newNode));
        assertTrue(graph.containsElement(newNode));
    }

    /*
     * Test for boolean addEdge(Edge)
     */
    final public void testAddEdgeEdge() {
        assertFalse(graph.addEdge(aEdge));
        assertFalse(graph.addEdge(DefaultEdge.createEdge(source, aLabel, aTarget)));
        Edge newEdge = DefaultEdge.createEdge(aTarget, cLabel, bTarget);
        assertTrue(graph.addEdge(newEdge));
        assertTrue(graph.containsElement(newEdge));
        Node newNode = new DefaultNode();
        newEdge = DefaultEdge.createEdge(bTarget, cLabel, newNode);
        assertTrue(graph.addEdge(newEdge));
        assertTrue(graph.containsElement(newNode));
    }

    final public void testAddNodeSet() {
        Set<Node> nodeSet = new HashSet<Node>();
        assertFalse(graph.addNodeSet(nodeSet));
        nodeSet.add(source);
        assertFalse(graph.addNodeSet(nodeSet));
        Node newNode = new DefaultNode();
        nodeSet.add(newNode);
        assertTrue(graph.addNodeSet(nodeSet));
        assertTrue(graph.containsElement(newNode));
    }

    final public void testAddEdgeSet() {
        Set<Edge> edgeSet = new HashSet<Edge>();
        assertFalse(graph.addEdgeSet(edgeSet));
        edgeSet.add(aEdge);
        assertFalse(graph.addEdgeSet(edgeSet));
        edgeSet.add(DefaultEdge.createEdge(source, bLabel, bTarget));
        assertFalse(graph.addEdgeSet(edgeSet));
        Node newNode = new DefaultNode();
        Edge newEdge = DefaultEdge.createEdge(bTarget, cLabel, newNode);
        edgeSet.add(newEdge);
        assertTrue(graph.addEdgeSet(edgeSet));
        assertTrue(graph.containsElement(newNode));
    }

    final public void testRemoveNode() {
        Node newNode = new DefaultNode();
        assertFalse(graph.removeNode(newNode));
        graph.addNode(newNode);
        assertTrue(graph.removeNode(newNode));
        assertTrue(graph.removeNode(source));
        assertFalse(graph.containsElement(source));
        assertFalse(graph.containsElement(aEdge));
        assertFalse(graph.removeNode(source));
    }

    final public void testRemoveEdge() {
        Edge newEdge = DefaultEdge.createEdge(source, bLabel, aTarget);
        assertFalse(graph.removeEdge(newEdge));
        graph.addEdge(newEdge);
        assertTrue(graph.removeEdge(newEdge));
        assertTrue(graph.removeEdge(aEdge));
        assertTrue(graph.containsElement(source));
        assertFalse(graph.containsElement(aEdge));
        assertFalse(graph.removeEdge(aEdge));
        assertTrue(graph.removeEdge(DefaultEdge.createEdge(source, bLabel, bTarget)));
    }

    final public void testRemoveNodeSet() {
        Set<Node> nodeSet = new HashSet<Node>();
        assertFalse(graph.removeNodeSet(nodeSet));
        Node newNode = new DefaultNode();
        nodeSet.add(newNode);
        assertFalse(graph.removeNodeSet(nodeSet));
        graph.addNode(newNode);
        assertTrue(graph.removeNodeSet(nodeSet));
        assertFalse(graph.containsElement(newNode));
        nodeSet.add(source);
        assertTrue(graph.removeNodeSet(nodeSet));
        assertFalse(graph.containsElement(source));
        assertFalse(graph.containsElement(aEdge));
    }

    final public void testRemoveEdgeSet() {
        Set<Edge> edgeSet = new HashSet<Edge>();
        assertFalse(graph.removeEdgeSet(edgeSet));
        Node newNode = new DefaultNode();
        Edge newEdge = DefaultEdge.createEdge(bTarget, cLabel, newNode);
        edgeSet.add(newEdge);
        assertFalse(graph.removeEdgeSet(edgeSet));
        edgeSet.add(DefaultEdge.createEdge(source, bLabel, bTarget));
        edgeSet.add(aEdge);
        assertTrue(graph.removeEdgeSet(edgeSet));
        assertFalse(graph.containsElement(bEdge));
        assertFalse(graph.containsElement(aEdge));
        assertFalse(graph.removeEdgeSet(edgeSet));
    }

    final public void testSetFixed() {
        graph.setFixed();
        assertTrue(graph.isFixed());
        // fixedness of the graph is not tested, it is a precondition
        // so there is little else to do here
    }

    /*
     * Test for boolean equals(Object)
     */
    final public void testEqualsObject() {
        // according to the current implementation,
        // equality is deferred to <tt>Object</tt>
        // so not even graphs and their clones are equal
        assertFalse(graph.equals(graph.clone()));
    }

    /*
     * Test for boolean equals(GraphShape)
     */
    final public void testEqualsGraphShape() {
        // according to the current implementation,
        // equality is deferred to <tt>Object</tt>
        // so not even graphs and their clones are equal
        assertFalse(graph.equals(graph.clone()));
    }

    final public void testAddGraphListenerAdd() {
        Graph graph1 = matchCod;
        GraphListener listener = new GraphListener();
        listener.addGraph(graph);
        graph.addGraphListener(listener);
        listener.addGraph(graph1);
        graph1.addGraphListener(listener);
        List<Element> addedGraphElements = new LinkedList<Element>();
        // add a fresh node using addNode()
        Node addedNode = graph.addNode();
        addedGraphElements.add(addedNode);
        // add a fresh node using addNode(Node)
        addedNode = new DefaultNode();
        graph.addNode(addedNode);
        addedGraphElements.add(addedNode);
        // add a fresh edge using addEdge(Node,Label,Node)
        Edge addedEdge = graph.addEdge(bTarget, aLabel, aTarget);
        addedGraphElements.add(addedEdge);
        // add an existing edge using addEdge(Node,Label,Node)
        graph.addEdge(source, aLabel, aTarget);
        // add a fresh edge with a fresh end node       
        addedNode = new DefaultNode();
        addedEdge = DefaultEdge.createEdge(aTarget, cLabel, addedNode);
        graph.addEdge(addedEdge);
        addedGraphElements.add(addedNode);
        addedGraphElements.add(addedEdge);
        // add a set of nodes
        Collection<Node> nodeSet = new LinkedList<Node>();
        nodeSet.add(new DefaultNode());
        nodeSet.add(new DefaultNode());
        nodeSet.add(new DefaultNode());
        addedGraphElements.addAll(nodeSet);
        graph.addNodeSet(nodeSet);
        // add a set of edges        
        Collection<Edge> edgeSet = new LinkedList<Edge>();
        addedNode = new DefaultNode();
        addedEdge = DefaultEdge.createEdge(bTarget, cLabel, addedNode);
        addedGraphElements.add(addedNode);
        addedGraphElements.add(addedEdge);
        edgeSet.add(addedEdge);
        addedEdge = DefaultEdge.createEdge(source, aLabel, aTarget);        
        edgeSet.add(addedEdge);
        addedEdge = DefaultEdge.createEdge(source, bLabel, aTarget);        
        edgeSet.add(addedEdge);
        addedGraphElements.add(addedEdge);
        graph.addEdgeSet(edgeSet);
        // now test if we did ok
        assertEquals(addedGraphElements, listener.added.get(graph));
    }

    final public void testAddGraphListenerRemove() {
        GraphListener listener = new GraphListener();
        listener.addGraph(graph);
        graph.addGraphListener(listener);
        List<Element> removedGraphElements = new LinkedList<Element>();
        // try to remove a non-existent edge
        Edge removedEdge = DefaultEdge.createEdge(aTarget, aLabel, aTarget);
        graph.removeEdge(removedEdge);
        assertEquals(removedGraphElements, listener.removed.get(graph));
        // remove an existing edge
        graph.removeEdge(bEdge);
        removedGraphElements.add(bEdge);
        assertEquals(removedGraphElements, listener.removed.get(graph));
        // try to remove a non-existent node
        Node removedNode = new DefaultNode();
        graph.removeNode(removedNode);
        assertEquals(removedGraphElements, listener.removed.get(graph));
        // remove an existing node
        graph.removeNode(aTarget);
        removedGraphElements.add(aEdge);
        removedGraphElements.add(aTarget);
        assertEquals(removedGraphElements, listener.removed.get(graph));
        // add the nodes and edges again, to continue testing
        graph.addEdge(aEdge);
        graph.addEdge(bEdge);
        assertEquals(removedGraphElements, listener.removed.get(graph));
        // remove a set of nodes
        Collection<Node> nodeSet = new LinkedList<Node>();
        nodeSet.add(new DefaultNode());
        nodeSet.add(bTarget);
        removedGraphElements.add(bEdge);
        removedGraphElements.add(bTarget);
        graph.removeNodeSet(nodeSet);
        assertEquals(removedGraphElements, listener.removed.get(graph));
        // remove a set of edges        
        Collection<Edge> edgeSet = new LinkedList<Edge>();
        edgeSet.add(DefaultEdge.createEdge(bTarget, cLabel, new DefaultNode()));
        edgeSet.add(aEdge);
        removedGraphElements.add(aEdge);
        graph.removeEdgeSet(edgeSet);
        // now test if we did ok
        assertEquals(removedGraphElements, listener.removed.get(graph));
    }

    final public void testRemoveGraphListener() {
        GraphListener listener = new GraphListener();
        graph.addGraphListener(listener);
    }

    private class GraphListener implements groove.graph.GraphListener {
        public void addUpdate(GraphShape graph, Node node) {
            assertTrue(listeningTo.contains(graph));
            added.get(graph).add(node);
        }

        public void addUpdate(GraphShape graph, Edge edge) {
            assertTrue(listeningTo.contains(graph));
            added.get(graph).add(edge);
        }

        public void removeUpdate(GraphShape graph, Node node) {
            assertTrue(listeningTo.contains(graph));
            removed.get(graph).add(node);
        }

        public void removeUpdate(GraphShape graph, Edge elem) {
            assertTrue(listeningTo.contains(graph));
            removed.get(graph).add(elem);
        }

        public void replaceUpdate(GraphShape graph, Node from, Node to) {
            assertTrue(listeningTo.contains(graph));
            replacedFrom.get(graph).add(from);
            replacedBy.get(graph).add(to);
        }
        

        public void replaceUpdate(GraphShape graph, Edge elem1, Edge elem2) {
            assertTrue(listeningTo.contains(graph));
            replacedFrom.get(graph).add(elem1);
            replacedBy.get(graph).add(elem2);
        }
        
        public void addGraph(Graph graph) {
            assertFalse(listeningTo.contains(graph));
            added.put(graph, new LinkedList<Element>());
            removed.put(graph, new LinkedList<Element>());
            replacedFrom.put(graph, new LinkedList<Element>());
            replacedBy.put(graph, new LinkedList<Element>());
        }

        private Map<Graph,List<Element>> added = new HashMap<Graph,List<Element>>();
        private Map<Graph,List<Element>> removed = new HashMap<Graph,List<Element>>();
        private Map<Graph,List<Element>> replacedFrom = new HashMap<Graph,List<Element>>(); 
        private Map<Graph,List<Element>> replacedBy = new HashMap<Graph,List<Element>>(); 
        private Set<Graph> listeningTo = added.keySet();
    }
    
    private final Xml xml;
}
