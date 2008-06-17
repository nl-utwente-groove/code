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
 * $Id: TestingPatternFamily.java,v 1.2 2008-02-05 13:28:21 rensink Exp $
 */
package groove.abs;

import groove.graph.DefaultEdge;
import groove.graph.DefaultGraph;
import groove.graph.DefaultLabel;
import groove.graph.DefaultNode;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Node;
import groove.match.GraphSearchPlanFactory;
import groove.match.SearchPlanStrategy;

/** */
public class TestingPatternFamily extends junit.framework.TestCase {

	Node[] nodes;
	
	/** A graph with several symmetric patterns. */
	// 0 -a-> 1   0 -a-> 2
	// 1 -b-> 3   2 -b-> 3   4 -b-> 3
	// 4 -c-> 5   4 -c-> 6   5 -d-> 6   6 -d-> 5  (4 -b-> 3)
	// 8 -e-> 7   9 -e-> 7   8 -e-> 9                           (non symmetric part)
	Graph withSym;
	
	Label a_lab = DefaultLabel.createLabel("a");
	Label b_lab = DefaultLabel.createLabel("b");
	Label c_lab = DefaultLabel.createLabel("c");
	Label d_lab = DefaultLabel.createLabel("d");
	Label e_lab = DefaultLabel.createLabel("e");

	private boolean init;
	@SuppressWarnings("unqualified-field-access")
	@Override
	public void setUp () {
		if (this.init) { return ;}
		
		// init nodes
		this.nodes = new Node[10];
		for (int i = 0; i < 10; i++) { this.nodes[i] = DefaultNode.createNode(); }
		
		// init withSym
		withSym = new DefaultGraph();
		withSym.addEdge(DefaultEdge.createEdge(nodes[0], a_lab, nodes[1]));
		withSym.addEdge(DefaultEdge.createEdge(nodes[0], a_lab, nodes[2]));
		withSym.addEdge(DefaultEdge.createEdge(nodes[1], b_lab, nodes[3]));
		withSym.addEdge(DefaultEdge.createEdge(nodes[2], b_lab, nodes[3]));
		withSym.addEdge(DefaultEdge.createEdge(nodes[4], b_lab, nodes[3]));
		withSym.addEdge(DefaultEdge.createEdge(nodes[5], c_lab, nodes[4]));
		withSym.addEdge(DefaultEdge.createEdge(nodes[6], c_lab, nodes[4]));
		withSym.addEdge(DefaultEdge.createEdge(nodes[5], d_lab, nodes[6]));
		withSym.addEdge(DefaultEdge.createEdge(nodes[6], d_lab, nodes[5]));
		withSym.addEdge(DefaultEdge.createEdge(nodes[8], e_lab, nodes[7]));
		withSym.addEdge(DefaultEdge.createEdge(nodes[9], e_lab, nodes[7]));
		withSym.addEdge(DefaultEdge.createEdge(nodes[8], e_lab, nodes[9]));
		
		
		this.init = true;
	}
	
	/**
	 * @throws ExceptionIncompatibleWithMaxIncidence  */
	@SuppressWarnings("unqualified-field-access")
	public void testGraphPattern () throws ExceptionIncompatibleWithMaxIncidence {
		// 
		PatternFamily pf = new PatternFamily(1, 10); 
		
		GraphPattern p0 = pf.computeAddPattern(withSym, nodes[0]);
		GraphPattern p3 = pf.computeAddPattern(withSym, nodes[3]);
		GraphPattern p4 = pf.computeAddPattern(withSym, nodes[4]);
		GraphPattern p7 = pf.computeAddPattern(withSym, nodes[7]);
		
		// local nodes, for the small graphs.
		Node[] nl = new Node[4];
		for (int i = 0; i < nl.length; i++) { nl[i] = DefaultNode.createNode(); }
 
		// The graphs. The first number within parenthesis is the central node idx
		// The second number within parenthesis is the expected number of morphisms
		Graph g0_0 = getG00(nl);  // (0)  0 -a-> 1                                              (1,2)
		Graph g0_1 = getG01(nl);  // (0)  0 -a-> 1  0 -a-> 2                                    (1,2)
		Graph g3_0 = getG30(nl);  // (0)  1 -b-> 0  2 -b-> 0                                    (1,6)
		Graph g3_1 = getG31(nl);  // (0)  1 -b-> 0  2 -b-> 0  3 -b-> 0                          (1,6)
		Graph g3_2 = getG32(nl);  // (0)  1 -b-> 0  2 -b-> 0  0 -b-> 3                          (0,0)
		Graph g3_3 = getG33(nl);  // (0)  1 -b-> 0  1 -b-> 2                                    (0,0)
		Graph g4_0 = getG40(nl);  // (0)  1 -c-> 0  2 -c-> 0  1 -d-> 2  2 -d-> 1                (1,2)
		Graph g4_1 = getG41(nl);  // (0)  1 -c-> 0  2 -c-> 0  1 -d-> 2  0 -b-> 3                (1,2)
		Graph g4_2 = getG42(nl);  // (0)  1 -c-> 0  2 -c-> 0  3 -c-> 0  1 -d-> 2  2 -d-> 3      (0,0)
		Graph g7_0 = getG70(nl);  // (0)  1 -e-> 0                                              (2,2)
		
	
		assertEquals(1, pf.getSelfIsomorphisms(p0).size());
		
		assertEquals(1, p0.possibleTypings(g0_0, nl[0], true).size());		
		assertEquals(1, p0.possibleTypings(g0_1, nl[0], true).size());
		assertEquals(1, p3.possibleTypings(g3_0, nl[0], true).size());
		assertEquals(1, p3.possibleTypings(g3_1, nl[0], true).size());
		assertEquals(0, p3.possibleTypings(g3_2, nl[0], true).size());
		assertEquals(0, p3.possibleTypings(g3_3, nl[0], true).size());
		assertEquals(1, p4.possibleTypings(g4_0, nl[0], true).size());
		assertEquals(1, p4.possibleTypings(g4_1, nl[0], true).size());
		assertEquals(0, p4.possibleTypings(g4_2, nl[0], true).size());
		assertEquals(2, p7.possibleTypings(g7_0, nl[0], true).size());
		
		assertEquals(2, p0.possibleTypings(g0_0, nl[0], false).size());
		assertEquals(2, p0.possibleTypings(g0_1, nl[0], false).size());
		assertEquals(6, p3.possibleTypings(g3_0, nl[0], false).size());
		assertEquals(6, p3.possibleTypings(g3_1, nl[0], false).size());
		assertEquals(0, p3.possibleTypings(g3_2, nl[0], false).size());
		assertEquals(0, p3.possibleTypings(g3_3, nl[0], false).size());
		assertEquals(2, p4.possibleTypings(g4_0, nl[0], false).size());
		assertEquals(2, p4.possibleTypings(g4_1, nl[0], false).size());
		assertEquals(0, p4.possibleTypings(g4_2, nl[0], false).size());
		assertEquals(2, p7.possibleTypings(g7_0, nl[0], false).size());

		
	}
	
	private Graph getG00(Node[] n) {
		Graph result = new DefaultGraph();
		result.addEdge(DefaultEdge.createEdge(n[0], this.a_lab, n[1]));
		return result;
	}
	private Graph getG01(Node[] n) {
		Graph result = new DefaultGraph();
		result.addEdge(DefaultEdge.createEdge(n[0], this.a_lab, n[1]));
		result.addEdge(DefaultEdge.createEdge(n[0], this.a_lab, n[2]));
		return result;
	}
	private Graph getG30(Node[] n) {
		Graph result = new DefaultGraph();
		result.addEdge(DefaultEdge.createEdge(n[1], this.b_lab, n[0]));
		result.addEdge(DefaultEdge.createEdge(n[2], this.b_lab, n[0]));
		return result;
	}
	private Graph getG31(Node[] n) {
		Graph result = new DefaultGraph();
		result.addEdge(DefaultEdge.createEdge(n[1], this.b_lab, n[0]));
		result.addEdge(DefaultEdge.createEdge(n[2], this.b_lab, n[0]));
		result.addEdge(DefaultEdge.createEdge(n[3], this.b_lab, n[0]));
		return result;
	}
	private Graph getG32(Node[] n) {
		Graph result = new DefaultGraph();
		result.addEdge(DefaultEdge.createEdge(n[1], this.b_lab, n[0]));
		result.addEdge(DefaultEdge.createEdge(n[2], this.b_lab, n[0]));
		result.addEdge(DefaultEdge.createEdge(n[0], this.b_lab, n[3]));
		return result;
	}
	private Graph getG33(Node[] n) {
		Graph result = new DefaultGraph();
		result.addEdge(DefaultEdge.createEdge(n[1], this.b_lab, n[0]));
		result.addEdge(DefaultEdge.createEdge(n[1], this.b_lab, n[2]));
		return result;
	}
	private Graph getG40(Node[] n) {
		Graph result = new DefaultGraph();
		result.addEdge(DefaultEdge.createEdge(n[1], this.c_lab, n[0]));
		result.addEdge(DefaultEdge.createEdge(n[2], this.c_lab, n[0]));
		result.addEdge(DefaultEdge.createEdge(n[1], this.d_lab, n[2]));
		result.addEdge(DefaultEdge.createEdge(n[2], this.d_lab, n[1]));
		return result;
	}
	private Graph getG41(Node[] n) {
		Graph result = new DefaultGraph();
		result.addEdge(DefaultEdge.createEdge(n[1], this.c_lab, n[0]));
		result.addEdge(DefaultEdge.createEdge(n[2], this.c_lab, n[0]));
		result.addEdge(DefaultEdge.createEdge(n[1], this.d_lab, n[2]));
		result.addEdge(DefaultEdge.createEdge(n[0], this.b_lab, n[3]));
		return result;
	}
	private Graph getG42(Node[] n) {
		Graph result = new DefaultGraph();
		result.addEdge(DefaultEdge.createEdge(n[1], this.c_lab, n[0]));
		result.addEdge(DefaultEdge.createEdge(n[2], this.c_lab, n[0]));
		result.addEdge(DefaultEdge.createEdge(n[3], this.c_lab, n[0]));
		result.addEdge(DefaultEdge.createEdge(n[1], this.d_lab, n[2]));
		result.addEdge(DefaultEdge.createEdge(n[2], this.d_lab, n[3]));
		return result;
	}
	private Graph getG70(Node[] n) {
		Graph result = new DefaultGraph();
		result.addEdge(DefaultEdge.createEdge(n[1], this.e_lab, n[0]));
		return result;
	}

	public void _testMatching1 () {
		Graph g = new DefaultGraph();
		Node n0 = g.addNode();
		Node n1 = g.addNode();
		Node n2 = g.addNode();
		
		g.addEdge(n1, DefaultLabel.createLabel("a"), n0);
		g.addEdge(n2, DefaultLabel.createLabel("a"), n0);
		
		GraphSearchPlanFactory injspf = GraphSearchPlanFactory.getInstance(true, false);
		SearchPlanStrategy mstr = injspf.createMatcher(g, null, null);
		System.out.println(mstr.getMatchSet(g, null));
		
		
		Graph g2 = new DefaultGraph();
		Node m0 = g2.addNode();
		Node m1 = g2.addNode();
		g2.addEdge(m1, DefaultLabel.createLabel("a"), m0);
		
		SearchPlanStrategy mstr2 = injspf.createMatcher(g2, null, null);
		System.out.println("---------------------------------\n");
		System.out.println(mstr2.getMatchSet(g, null));
		
	}
	
	public static void main (String[] args) {
		TestingPatternFamily test = new TestingPatternFamily();
		test._testMatching1();
	}
	

}
