/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
 * $Id: TestingAbstGraph.java,v 1.2 2008-02-05 13:28:21 rensink Exp $
 */
package groove.test.abs;

import groove.abs.AbstrGraph;
import groove.abs.DefaultAbstrGraph;
import groove.abs.ExceptionIncompatibleWithMaxIncidence;
import groove.abs.ExceptionRemovalImpossible;
import groove.abs.GraphPattern;
import groove.abs.PatternFamily;
import groove.abs.Util;
import groove.abs.Abstraction.AbstrGraphsRelation;
import groove.graph.DefaultEdge;
import groove.graph.DefaultGraph;
import groove.graph.DefaultLabel;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.NodeEdgeHashMap;
import groove.graph.NodeEdgeMap;
import groove.rel.VarNodeEdgeMap;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

/** Testing implementation of AbstrGRaph
 * 
 * @author Iovka Boneva
 * @version $Revision $
 */
public class TestingAbstGraph extends TestCase {
	
	/** A prefix for the examples. */
	// private static final String PATH_PREFIX = "";
	
	/** Pattern family with radius one */
	private final PatternFamily pf = new PatternFamily(1, 10);
	
	/** */ protected Graph list1;
	/** */ protected Graph list2;
	/** */ protected Graph list3;
	/** */ protected Graph list4;
	/** */ protected Graph list5;
	/** */ protected Graph list10;
	
	/** */ protected DefaultAbstrGraph s_l2_1; // list with 2 cells, precision 1
	/** */ protected DefaultAbstrGraph s_l4_1; // list with 4 cells, precision 1
	/** */ protected DefaultAbstrGraph s_l4_2; // list with 4 cells, precision 2
	/** */ protected DefaultAbstrGraph s_l5_1; // list with 5 cells, precision 1
	/** */ protected DefaultAbstrGraph s_l5_2; // list with 5 cells, precision 2
	/** */ protected DefaultAbstrGraph s_l10_1; // list with 10 cells, precision 1
	/** */ protected DefaultAbstrGraph s_l10_2; // list with 10 cells, precision 2
	
	/** */ protected DefaultLabel n_label = DefaultLabel.createLabel("n");
	/** */ protected DefaultLabel c_label = DefaultLabel.createLabel("c");
	
	/** */ protected Node[] nodes = new Node[10];
	
	// used to indicate whether the last initialisation is correct
	// should be set to false by test methods that modify some of the global variables
	private boolean init = false;
	
	/** Initialises the graphs. */
	@SuppressWarnings("unqualified-field-access")
	@Override
	public void setUp () throws IOException, ExceptionIncompatibleWithMaxIncidence {
		if (init) { return; }
		
		for (int i = 0; i < 10; i++) { nodes[i] = DefaultNode.createNode(); }
		
		list1 = new DefaultGraph();
		list1.addNode(nodes[0]);
		list1.addEdge(DefaultEdge.createEdge(nodes[0], c_label, nodes[0]));
		
		list2 = new DefaultGraph();
		for (int i = 0; i < 1; i++) { list2.addEdge(DefaultEdge.createEdge(nodes[i], n_label, nodes[i+1])); }
		for (int i = 0; i < 2; i++) { list2.addEdge(DefaultEdge.createEdge(nodes[i], c_label, nodes[i])); }
		
		list3 = new DefaultGraph();
		for (int i = 0; i < 2; i++) { list3.addEdge(DefaultEdge.createEdge(nodes[i], n_label, nodes[i+1])); }
		for (int i = 0; i < 3; i++) { list3.addEdge(DefaultEdge.createEdge(nodes[i], c_label, nodes[i])); }
		
		list4 = new DefaultGraph();
		for (int i = 0; i < 3; i++) { list4.addEdge(DefaultEdge.createEdge(nodes[i], n_label, nodes[i+1])); }
		for (int i = 0; i < 4; i++) { list4.addEdge(DefaultEdge.createEdge(nodes[i], c_label, nodes[i])); }
		
		list5 = new DefaultGraph();
		for (int i = 0; i < 4; i++) { list5.addEdge(DefaultEdge.createEdge(nodes[i], n_label, nodes[i+1])); }
		for (int i = 0; i < 5; i++) { list5.addEdge(DefaultEdge.createEdge(nodes[i], c_label, nodes[i])); }
		
		list10 = new DefaultGraph();
		for (int i = 0; i < 9; i++) { list10.addEdge(DefaultEdge.createEdge(nodes[i], n_label, nodes[i+1])); }
		for (int i = 0; i < 10; i++) { list10.addEdge(DefaultEdge.createEdge(nodes[i], c_label, nodes[i])); }
		
		
		s_l2_1 = DefaultAbstrGraph.factory(pf,1).getShapeGraphFor(list2);
		s_l4_1 = DefaultAbstrGraph.factory(pf,1).getShapeGraphFor(list4);
		s_l4_2 = DefaultAbstrGraph.factory(pf,2).getShapeGraphFor(list4);
		s_l5_1 = DefaultAbstrGraph.factory(pf,1).getShapeGraphFor(list5);
		s_l5_2 = DefaultAbstrGraph.factory(pf,2).getShapeGraphFor(list5);
		s_l10_1 = DefaultAbstrGraph.factory(pf,1).getShapeGraphFor(list10);
		s_l10_2 = DefaultAbstrGraph.factory(pf,2).getShapeGraphFor(list10);
	}
	
	/** */
	@SuppressWarnings("unqualified-field-access")
	public void testIsomorphism () throws AssertionError{
		
		// TODO needs more test cases
	
		assertNotNull(s_l4_1.getIsomorphismToAbstrGraph(s_l4_1));
		assertNotNull(s_l4_1.getIsomorphismToAbstrGraph(s_l5_1));
		assertNotNull(s_l5_1.getIsomorphismToAbstrGraph(s_l4_1));
		
		assertNotNull(s_l4_2.getIsomorphismToAbstrGraph(s_l5_2));
		assertNotNull(s_l5_2.getIsomorphismToAbstrGraph(s_l4_2));
		
		assertNotNull(s_l5_1.getIsomorphismToAbstrGraph(s_l10_1));
		assertNotNull(s_l10_1.getIsomorphismToAbstrGraph(s_l5_1));
		assertNotNull(s_l5_2.getIsomorphismToAbstrGraph(s_l10_2));
		assertNotNull(s_l10_2.getIsomorphismToAbstrGraph(s_l5_2));
		
		assertNull(s_l2_1.getIsomorphismToAbstrGraph(s_l4_1));
	}
	
	/** */
	@SuppressWarnings("unqualified-field-access")
	public void testIsInjectiveMap () throws AssertionError {
		// find the nodes of s_l4_2 and s_l4_1
		Node first2 = firstInList(s_l4_2); assertNotNull(first2);
		Node middle2 = middleInList(s_l4_2); assertNotNull(middle2);
		Node last2 = lastInList(s_l4_2); assertNotNull(last2);
		
		Node middle1 = middleInList(s_l4_1); assertNotNull(middle1);
		
//		System.out.println(s_l4_2);
//		System.out.println("first : " + first2);
//		System.out.println("middle : " + middle2);
//		System.out.println("last : " + last2);
//		
//		System.out.println(s_l4_1);
//		System.out.println("first : " + first1);
//		System.out.println("middle : " + middle1);
//		System.out.println("last : " + last1);

	
		NodeEdgeMap map; // local variable
		Collection<VarNodeEdgeMap> matches; // local variable
		
		// a map from list3 into s_l4_2, all nodes mapping into the middle cell
		map = new NodeEdgeHashMap();
		for (Node node : list3.nodeSet()) { map.putNode(node, middle2); }
		matches = Util.getMatchSet(list3, s_l4_2, map);
		assertEquals(1, matches.size());
		VarNodeEdgeMap match1 = matches.iterator().next();
		
		// a map from list3 into s_l4_1, all nodes mapping into the middle cell
		map = new NodeEdgeHashMap();
		for (Node node : list3.nodeSet()) { map.putNode(node, middle1); }
		matches = Util.getMatchSet(list3, s_l4_1, map);
		assertEquals(1, matches.size());
		VarNodeEdgeMap match2 = matches.iterator().next();
		
		// a map from list3 into s_l4_2, each node mapping into a different cell
		map = new NodeEdgeHashMap();
		map.putNode(nodes[0], first2); map.putNode(nodes[1], middle2); map.putNode(nodes[2], last2);
		matches = Util.getMatchSet(list3, s_l4_2, map);
		assertEquals(1, matches.size());
		VarNodeEdgeMap match3 = matches.iterator().next();
		
		assertFalse(s_l4_2.isInjectiveMap(match1));
		assertTrue(s_l4_1.isInjectiveMap(match2));
		assertTrue(s_l4_2.isInjectiveMap(match3));
	}

	/** */
	@SuppressWarnings("unqualified-field-access")
	public void testNodeFor () throws AssertionError {
		Set<GraphPattern> thePatterns = new HashSet<GraphPattern>(10);
		for (GraphPattern p : pf) {
			thePatterns.add(p);
		}
		assertEquals(3, thePatterns.size());
	
		for (GraphPattern p : thePatterns) {
			assertNotNull(s_l5_2.nodeFor(p));
			assertNotNull(s_l10_1.nodeFor(p));
		}
	}
	
	/** */
	@SuppressWarnings("unqualified-field-access")
	public void testAddRemove () throws ExceptionRemovalImpossible, AssertionError {
		// this method modifies some of the abstract graphs, so initialisation should be re-done afterwards
		init = false;
		
		// get the middle nodes of s_l4_2, s_l5_1, sl10_1
		Node middle4_2 = middleInList(s_l4_2); assertNotNull(middle4_2);
		Node middle5_1 = middleInList(s_l5_1); assertNotNull(middle5_1);
		Node middle10_1 = middleInList(s_l10_1); assertNotNull(middle10_1);
		
		s_l5_1.removeFrom(middle5_1, 1);
		s_l10_1.removeFrom(middle10_1, 1);
		assertEquals(AbstrGraphsRelation.EQUAL, s_l5_1.compare(s_l10_1, false));
		
		s_l4_2.addTo(s_l4_2.typeOf(middle4_2), 1);
		Morphism m = s_l4_2.getIsomorphismToAbstrGraph(s_l4_1);
		assertNotNull(m);
		

		// TODO more tests
	}
	
	/** */
	@SuppressWarnings("unqualified-field-access")
	public void testCompare () throws ExceptionRemovalImpossible, AssertionError {
		
		assertEquals(AbstrGraphsRelation.EQUAL, s_l4_1.compare(s_l10_1, false));
		assertEquals(AbstrGraphsRelation.NOTEQUAL, s_l5_2.compare(s_l10_1, false));

		// remove twice from middle5_1
		Node middle5_1 = middleInList(s_l5_1);
		s_l5_1.removeFrom(middle5_1, 1);
		assertEquals(AbstrGraphsRelation.SUPER, s_l5_1.compare(s_l10_1, false));
		s_l5_1.removeFrom(middle5_1, 1);
		assertEquals(AbstrGraphsRelation.SUPER, s_l5_1.compare(s_l10_1, false));
		
		// remove one from middle5_2 and two from middle10_2 
		Node middle5_2 = middleInList(s_l5_2);
		s_l5_2.removeFrom(middle5_2, 1);
		assertEquals(AbstrGraphsRelation.SUPER, s_l5_2.compare(s_l10_2, false));
		Node middle10_2 = middleInList(s_l10_2);
		s_l10_2.removeFrom(middle10_2, 2);
		assertEquals(AbstrGraphsRelation.SUB, s_l5_2.compare(s_l10_2, false));
		
		// remove one more from middle5_2
		s_l5_2.removeFrom(middle5_2, 1);
		assertEquals(AbstrGraphsRelation.EQUAL, s_l10_2.compare(s_l5_2, false));
		
		Node middle4_2 = middleInList(s_l4_2);
		s_l4_2.removeFrom(middle4_2, 1);
		assertEquals(AbstrGraphsRelation.SUB, s_l4_2.compare(s_l5_2, true));
		assertEquals(AbstrGraphsRelation.QUASI, s_l5_2.compare(s_l4_2, false));	
	}
	
	/** */
	@SuppressWarnings("unqualified-field-access")
	public void testMisc() throws AssertionError {
		// get the middle and first nodes of s_l4_2, s_l5_1
		Node middle4_2 = middleInList(s_l4_2);
		Node middle5_1 = middleInList(s_l5_1);
		Node first4_1 = firstInList(s_l4_1);
		
		// Tests
		assertTrue(s_l4_2.typeOf(middle4_2).equals(s_l5_1.typeOf(middle5_1)));
		assertFalse(s_l4_1.typeOf(first4_1).equals(s_l4_2.typeOf(middle4_2)));
	}

	// -------------------------------------------------------------------------
	// AUX FUNCTIONS
	// -------------------------------------------------------------------------

	/** Determines the first node in an abstract graph for a list. 
	 * @param list should be one of the precomputed lists.
	 */
	@SuppressWarnings("unqualified-field-access")
	private Node firstInList (AbstrGraph list) {
		Node result = null;
		for (Node node : list.nodeSet()) {
			Set<Edge> out_n_edges = new HashSet<Edge>();
			for (Edge e : list.edgeSet(node, 0)) {
				if (e.label().equals(n_label)) { out_n_edges.add(e); }
			}
			Set<Edge> in_n_edges = new HashSet<Edge>();
			for (Edge e : list.edgeSet(node, 1)) {
				if (e.label().equals(n_label)) { in_n_edges.add(e); }
			}
			if (out_n_edges.size() == 1 && in_n_edges.size() == 0) { result = node; }
		}
		return result;
	}
	
	/** Determines the middle node in an abstract graph for a list. 
	 * @param list should be one of the precomputed lists.
	 */
	@SuppressWarnings("unqualified-field-access")
	private Node middleInList (AbstrGraph list) {
		Node result = null;
		for (Node node : list.nodeSet()) {
			Set<Edge> out_n_edges = new HashSet<Edge>();
			for (Edge e : list.edgeSet(node, 0)) {
				if (e.label().equals(n_label)) { out_n_edges.add(e); }
			}
			Set<Edge> in_n_edges = new HashSet<Edge>();
			for (Edge e : list.edgeSet(node, 1)) {
				if (e.label().equals(n_label)) { in_n_edges.add(e); }
			}
			if (in_n_edges.size() > 1 && out_n_edges.size() > 1) { result = node; }
		}
		return result;
	}
	
	/** Determines the last node in an abstract graph for a list. 
	 * @param list should be one of the precomputed lists.
	 */
	@SuppressWarnings("unqualified-field-access")
	private Node lastInList (AbstrGraph list) {
		Node result = null;
		for (Node node : list.nodeSet()) {
			Set<Edge> out_n_edges = new HashSet<Edge>();
			for (Edge e : list.edgeSet(node, 0)) {
				if (e.label().equals(n_label)) { out_n_edges.add(e); }
			}
			Set<Edge> in_n_edges = new HashSet<Edge>();
			for (Edge e : list.edgeSet(node, 1)) {
				if (e.label().equals(n_label)) { in_n_edges.add(e); }
			}
			if (in_n_edges.size() == 1 && out_n_edges.size() == 0) { result = node; }
		}
		return result;
	}
	
	
}
