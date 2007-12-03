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
 * $Id: TestingTransforming.java,v 1.4 2007-12-03 20:52:29 iovka Exp $
 */
package groove.abs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import groove.graph.DefaultEdge;
import groove.graph.DefaultGraph;
import groove.graph.DefaultLabel;
import groove.graph.DefaultMorphism;
import groove.graph.DefaultNode;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.Edge;
import groove.graph.NodeEdgeHashMap;
import groove.graph.NodeEdgeMap;
import groove.io.AspectualViewGps;
import groove.io.DefaultGxl;
import groove.rel.VarNodeEdgeHashMap;
import groove.rel.VarNodeEdgeMap;
import groove.trans.DefaultApplication;
import groove.trans.GraphGrammar;
import groove.trans.RuleApplication;
import groove.trans.RuleEvent;
import groove.trans.SPOEvent;
import groove.trans.SPORule;
import groove.trans.SystemRecord;
import groove.view.FormatException;
import junit.framework.TestCase;

/** Tests for the transformation. */
public class TestingTransforming extends TestCase {
	
	private final Abstraction.Options options = new Abstraction.Options();
	// -----------------------------------------------------------------
	// GRAPHS AND MATCHINGS
	//	 -----------------------------------------------------------------
	/** A prefix for the examples. */
	private static final String PATH_PREFIX = "../tests/junit/";
	
	
	/** Empty graph; its type is used for instanciating all graphs for testing */
	Graph type = new DefaultGraph();
	
	/** Set of nodes to be used for the graphs */
	Node[] nodes = new Node[20];
	
	
	/** List of 5 elements */
	Graph list5;
	/** List of 12 elements */
	Graph list12;
	/** List cell */
	Graph cell;
	
	/** List transformations */
	GraphGrammar circularListGrammar4;
	GraphGrammar listGrammar4;
	GraphGrammar listGrammar5;
	GraphGrammar listGrammar10;
	GraphGrammar binaryTreeGrammar;
	
	@SuppressWarnings("unqualified-field-access")
	@Override
	protected void setUp () {
		// Initialise the list graphs and the cell graph
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = DefaultNode.createNode(i);
		}
		
		list5 = type.clone();
 		for (int i = 0; i < 4; i++) {
 			list5.addEdge(DefaultEdge.createEdge(nodes[i], "n" , nodes[i+1]));
 		}
 		list12 = type.clone();
 		for (int i = 0; i < 11; i++) {
 			list12.addEdge(DefaultEdge.createEdge(nodes[i], "n" , nodes[i+1]));
 		}
 		
		cell = type.clone();
 		cell.addNode(nodes[12]);	 			
 		
		try {
			listGrammar4 = (new AspectualViewGps()).unmarshal(new File(PATH_PREFIX+"list4.gps"), "start").toGrammar();
		} catch (FormatException e1) {
			e1.printStackTrace();
			System.exit(1);
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(1);
		} 
		
		try {
			listGrammar10 = (new AspectualViewGps()).unmarshal(new File(PATH_PREFIX+"list10.gps"), "start").toGrammar();
		} catch (FormatException e1) {
			e1.printStackTrace();
			System.exit(1);
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(1);
		} 
		
		try {
			listGrammar5 = (new AspectualViewGps()).unmarshal(new File(PATH_PREFIX+"list5.gps"), "start").toGrammar();
		} catch (FormatException e1) {
			e1.printStackTrace();
			System.exit(1);
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(1);
		} 
		
		try {
			circularListGrammar4 = (new AspectualViewGps()).unmarshal(new File(PATH_PREFIX+"circularlist4.gps"), "start").toGrammar();
		} catch (FormatException e1) {
			e1.printStackTrace();
			System.exit(1);
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(1);
		} 
		try {
			binaryTreeGrammar = (new AspectualViewGps()).unmarshal(new File(PATH_PREFIX+"generate-binary-tree.gps"), "start").toGrammar();
		} catch (FormatException e1) {
			e1.printStackTrace();
			System.exit(1);
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(1);
		} 
	}
	
	/** */
	public void _test () {
		testConstructionConcrPart();
		testSetMaterialisations1();
		testSetMaterialisations2();
		testSetMaterialisations3();
		testSetMaterialisations4();
		testTransformCircularList();
	}
	
	
	/** */
	@SuppressWarnings("unqualified-field-access")
	public void testConstructionConcrPart () throws AssertionError {
 		PatternFamily pf = new PatternFamily(1, 10);
 		
		DefaultAbstrGraph s = null;
		try {
			s = DefaultAbstrGraph.factory(pf,1).getShapeGraphFor(list5);
		} catch (ExceptionIncompatibleWithMaxIncidence e) {
			e.printStackTrace();
		}
		
		// Compute a morphism from cell into the middle node of the shape
		Node middle = null;
		for (Node n : s.nodeSet()) {
			if (s.edgeSet(n).size() > 1) {
				middle = n;
				break;
			}
		}
		// should not happen
		assertTrue(middle != null);
		
		Morphism morph = new DefaultMorphism(cell,s);
		Node cellNode = cell.nodeSet().iterator().next();
		morph.putNode(cellNode, middle);
		morph = Util.getTotalExtension(morph);

		// Constuct concrete parts
		Collection<ConcretePart> ext = ConcretePart.extensions(cell, new TypingImpl(s, morph), pf, false, null);	
		assertEquals(1, ext.size());
	}
	
	/** Test with
	 * - list with 4 elements
	 * - rule adding an object 
	 * - matching to the middle shape node
	 * - precision 2
	 * - radius 1
	 */
	@SuppressWarnings("unqualified-field-access")
	public void testSetMaterialisations1 () throws AssertionError {
 		PatternFamily pf = new PatternFamily(1, 10);
		DefaultAbstrGraph s = null;
		try {
			s = DefaultAbstrGraph.factory(pf,2).getShapeGraphFor(listGrammar4.getStartGraph());
		} catch (ExceptionIncompatibleWithMaxIncidence e) {
			e.printStackTrace();
		}
		
		// Compute a morphism from cell into the middle node of the shape
		Node middle = null;
		for (Node n : s.nodeSet()) {
			if (s.edgeSet(n).size() > 2) {
				middle = n;
				break;
			}
		}
		// should not happen
		assertTrue(middle != null);
		
		SPORule rule = (SPORule) listGrammar4.getRule("add");
		Morphism morph = new DefaultMorphism(rule.lhs(),s);
		Node cellNode = rule.lhs().nodeSet().iterator().next();
		morph.putNode(cellNode, middle);
		morph = Util.getTotalExtension(morph);
		
		// Constuct concrete parts
		SystemRecord syst = new SystemRecord(listGrammar4, true);
		ConcretePart.Typing typing = new TypingImpl(s, morph);
		Collection<ConcretePart> ext = ConcretePart.extensions(rule.lhs(), typing, pf, false, syst);
		// there is only one extension
		ConcretePart cp = ext.iterator().next();
		SetMaterialisations smat = new SetMaterialisations(cp, s, morph.elementMap(), this.options);

		// remap the initial mapping into the concrete part
		NodeEdgeMap match = new NodeEdgeHashMap();
		for (Node n : morph.nodeMap().keySet()){
			match.putNode(n,n);
		}
		for (Edge e : morph.edgeMap().keySet()) {
			match.putEdge(e, e);
		}
		
		RuleEvent event = new SPOEvent(rule, new VarNodeEdgeHashMap(match), syst, false);
		RuleApplication appl = new DefaultApplication(event, cp.graph());
		Collection<AbstrGraph> result = smat.transform(appl, syst);
		assertEquals(2, result.size());	
	}
	
	/** Test with
	 * - list with 4 elements
	 * - rule adding an object 
	 * - matching to the middle shape node
	 * - precision 1
	 * - radius 1
	 */
	@SuppressWarnings("unqualified-field-access")
	public void testSetMaterialisations2 () throws AssertionError {
 		PatternFamily pf = new PatternFamily(1, 10);
		DefaultAbstrGraph s = null;
		try {
			s = DefaultAbstrGraph.factory(pf,1).getShapeGraphFor(listGrammar4.getStartGraph());
		} catch (ExceptionIncompatibleWithMaxIncidence e) {
			e.printStackTrace();
		}
		
		// Compute a morphism from cell into the middle node of the shape
		Node middle = null;
		for (Node n : s.nodeSet()) {
			if (s.edgeSet(n).size() > 2) {
				middle = n;
				break;
			}
		}
		// should not happen
		assertTrue(middle != null);
		
		SPORule rule = (SPORule) listGrammar4.getRule("add");
		Morphism morph = new DefaultMorphism(rule.lhs(),s);
		Node cellNode = rule.lhs().nodeSet().iterator().next();
		morph.putNode(cellNode, middle);
		morph = Util.getTotalExtension(morph);
		
		// Constuct concrete parts
		SystemRecord syst = new SystemRecord(listGrammar4, true);
		ConcretePart.Typing typing = new TypingImpl(s, morph);
		Collection<ConcretePart> ext = ConcretePart.extensions(rule.lhs(), typing, pf, false, syst);
		// there is only one extension
		ConcretePart cp = ext.iterator().next();
		SetMaterialisations smat = new SetMaterialisations(cp, s, morph.elementMap(), this.options);
		
		// remap the initial mapping into the concrete part
		NodeEdgeMap match = new NodeEdgeHashMap();
		for (Node n : morph.nodeMap().keySet()){
			match.putNode(n,n);
		}
		for (Edge e : morph.edgeMap().keySet()) {
			match.putEdge(e, e);
		}
		
		RuleEvent event = new SPOEvent(rule, new VarNodeEdgeHashMap(match), syst, false);
		RuleApplication appl = new DefaultApplication(event, cp.graph());
		Collection<AbstrGraph> result = smat.transform(appl, syst);
		
		String fileNameBase = "../tests/out3/graph";
		int i = 1;
		for (AbstrGraph ag : result) {
			//System.out.println(ag + "\n");
			String fileName = fileNameBase + i++;
			try {
				(new DefaultGxl()).marshalGraph(ag, new File(fileName));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.err.println("Unable to write file " + fileName);
				e1.printStackTrace(); 
			}
		}
		assertEquals(10, result.size());	
	}

	
	/** Test with
	 * - list with 10 elements
	 * - rule adding an object 
	 * - matching to the middle shape node
	 * - precision 1
	 * - radius 2
	 */
	@SuppressWarnings("unqualified-field-access")
	public void testSetMaterialisations3 () throws AssertionError {
 		PatternFamily pf = new PatternFamily(2, 10);
		DefaultAbstrGraph s = null;
		try {
			s = DefaultAbstrGraph.factory(pf,1).getShapeGraphFor(listGrammar10.getStartGraph());
		} catch (ExceptionIncompatibleWithMaxIncidence e) {
			e.printStackTrace();
		}
		
		// Compute a morphism from cell into the middle node of the shape
		Node middle = null;
		for (Node n : s.nodeSet()) {
			if (s.edgeSet(n).size() > 3) {
				middle = n;
				break;
			}
		}
		// should not happen
		assertTrue(middle != null);
		
		SPORule rule = (SPORule) listGrammar10.getRule("add");
		Morphism morph = new DefaultMorphism(rule.lhs(),s);
		Node cellNode = rule.lhs().nodeSet().iterator().next();
		morph.putNode(cellNode, middle);
		morph = Util.getTotalExtension(morph);
		
		// Constuct concrete parts
		SystemRecord syst = new SystemRecord(listGrammar10, true);
		ConcretePart.Typing typing = new TypingImpl(s, morph);
		Collection<ConcretePart> ext = ConcretePart.extensions(rule.lhs(), typing, pf, false, syst);
		// there is only one extension
		ConcretePart cp = ext.iterator().next();
		SetMaterialisations smat = new SetMaterialisations(cp, s, morph.elementMap(), this.options);

		// remap the initial mapping into the concrete part
		NodeEdgeMap match = new NodeEdgeHashMap();
		for (Node n : morph.nodeMap().keySet()){
			match.putNode(n,n);
		}
		for (Edge e : morph.edgeMap().keySet()) {
			match.putEdge(e, e);
		}
		
		RuleEvent event = new SPOEvent(rule, new VarNodeEdgeHashMap(match), syst, false);
		RuleApplication appl = new DefaultApplication(event, cp.graph());
		Collection<AbstrGraph> result = smat.transform(appl, syst);
		
		String fileNameBase = "../tests/out4/graph";
		int i = 1;
		for (AbstrGraph ag : result) {
			//System.out.println(ag + "\n");
			String fileName = fileNameBase + i++;
			try {
				(new DefaultGxl()).marshalGraph(ag, new File(fileName));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.err.println("Unable to write file " + fileName);
				e1.printStackTrace(); 
			}
		}
		assertEquals(17, result.size());	
	}

	/** Tests all possible materialisations, for all possible matchings
	  * - list with 4 elements
	  * - precision 2
	  * - radius 1
	  */
	@SuppressWarnings("unqualified-field-access")
	public void testSetMaterialisations4() throws AssertionError {
		PatternFamily pf = new PatternFamily(1, 10);
		DefaultAbstrGraph s = null;
		try {
			s = DefaultAbstrGraph.factory(pf,2).getShapeGraphFor(listGrammar4.getStartGraph());
		} catch (ExceptionIncompatibleWithMaxIncidence e) {
			e.printStackTrace();
		}
		
		SPORule rule = (SPORule) listGrammar4.getRule("add");
		
		Collection<AbstrGraph> all = new ArrayList<AbstrGraph>();
		
		for (VarNodeEdgeMap match : Util.getMatchesIter(rule.lhs(), s, new NodeEdgeHashMap())) {
			if (! s.isInjectiveMap(match)) { continue; }
			
			SystemRecord syst = new SystemRecord(listGrammar4, true);
			ConcretePart.Typing typing = new TypingImpl(s, match);
			Collection<ConcretePart> ext = ConcretePart.extensions(rule.lhs(), typing, pf, false, syst);
			for (ConcretePart cp : ext) {
				SetMaterialisations smat = new SetMaterialisations(cp, s, match, this.options);
				RuleEvent event = new SPOEvent(rule, smat.updateMatch(match), syst, false);
				RuleApplication appl = new DefaultApplication(event, cp.graph());
				Collection<AbstrGraph> result = smat.transform(appl, syst);
				all.addAll(result);
			}
		}
		assertEquals(4, all.size());
	}
	
	/** */
	@SuppressWarnings("unqualified-field-access")
	public void testTransformCircularList () throws AssertionError {
		// common variables
		SystemRecord syst = new SystemRecord(circularListGrammar4, true);
		SPORule rule = (SPORule) listGrammar4.getRule("add");
		PatternFamily pf = new PatternFamily(1, 10);
		
		
		// The first abstract graph
		DefaultAbstrGraph s = null;
		
		// The second abstract graph
		AbstrGraph s2 = null;
		
		// Compute the first abstract graph
		{
			try {
				s = DefaultAbstrGraph.factory(pf,1).getShapeGraphFor(circularListGrammar4.getStartGraph());
			} catch (ExceptionIncompatibleWithMaxIncidence e) {
				e.printStackTrace();
			}
		}
		
		// Compute the unique derivation, which initialises s2
		{
			VarNodeEdgeMap match = Util.getMatchesIter(rule.lhs(), s, new NodeEdgeHashMap()).iterator().next();
			ConcretePart.Typing typing = new TypingImpl(s, match);
			ConcretePart cp = ConcretePart.extensions(rule.lhs(), typing, pf, false, syst).iterator().next();
			SetMaterialisations smat = new SetMaterialisations(cp, s, match, this.options);
			RuleEvent event = new SPOEvent(rule, smat.updateMatch(match), syst, false);
			RuleApplication appl = new DefaultApplication(event, cp.graph());
			s2 = smat.transform(appl, syst).iterator().next();
			assertEquals(5, s2.nodeCount());
		}
		
		// Compute a matching into the abstract node
		VarNodeEdgeMap match2 = null;
		
		{
			NodeEdgeMap map = new NodeEdgeHashMap();
	
			Node abstrNode = null;
			for (Node n : s2.nodeSet()) {
				if (Abstraction.MULTIPLICITY.containsOmega(s2.multiplicityOf(n))) { abstrNode = n; }
			}
			assertNotNull(abstrNode);
			map.putNode(rule.lhs().nodeSet().iterator().next(), abstrNode);
			match2 = new VarNodeEdgeHashMap(map);
		}
		
		// Construct the second set of materialisations, and the rule application
		SetMaterialisations smat2 = null;
		RuleApplication appl2 = null;
		
		{
			TypingImpl typing2 = new TypingImpl((DefaultAbstrGraph) s2, match2);
			ConcretePart cp2 = ConcretePart.extensions(rule.lhs(), typing2, pf, false, syst).iterator().next();
			smat2 = new SetMaterialisations(cp2, (DefaultAbstrGraph) s2, match2, this.options);
			RuleEvent event2 = new SPOEvent(rule, smat2.updateMatch(match2), syst, false);
			appl2 = new DefaultApplication(event2, cp2.graph());
		}
		
		// Transform
		Collection<AbstrGraph> result = smat2.transform(appl2, syst);

//		for (AbstrGraph ag : result) {
//			System.out.println(ag + "\n");
//		}
//		
//		int size = result.size();
//		Set<AbstrGraph> resultSet = new HashSet<AbstrGraph>(result);
//		
//		System.out.println("Before: " + size + ", After: " + resultSet.size());
		
	}
	
	public void testTransformBinaryTree1 () throws ExceptionIncompatibleWithMaxIncidence {
		// Construct a problematic graph
		PatternFamily pf = new PatternFamily(1, 10);
		DefaultAbstrGraph.AbstrGraphCreator creator =  DefaultAbstrGraph.getAbstrGraphCreatorInstance();
		creator.init(pf, 1);
		MultiplicityInformation one = Abstraction.MULTIPLICITY.getElement(1, 1);
		MultiplicityInformation omega = Abstraction.MULTIPLICITY.getElement(2, 1);
		Label laba = DefaultLabel.createLabel("a");
		Label labm = DefaultLabel.createLabel("m");
		Label labl = DefaultLabel.createLabel("l");

		
		// create the patterns
		Graph gA = new DefaultGraph();
		Graph gB = new DefaultGraph();
		Graph gC = new DefaultGraph();
		Graph gD = new DefaultGraph();
		Graph gL = new DefaultGraph();
		initGraphs(gA, gB, gC, gD, gL);
		GraphPattern pA, pB, pC, pD, pL;
		pA = pf.computeAddPattern(gA, nodes[0]);
		pB = pf.computeAddPattern(gB, nodes[0]);
		pC = pf.computeAddPattern(gC, nodes[0]);
		pD = pf.computeAddPattern(gD, nodes[0]);
		pL = pf.computeAddPattern(gL, nodes[0]);
		
		Node nA = creator.addNode(one, pA);
		Node nC = creator.addNode(omega, pC);		
		Node nB = creator.addNode(one, pB);
		Node nL = creator.addNode(omega, pL);
		creator.addEdge(nA, labm, nA);
		creator.addEdge(nC, labm, nC);
		creator.addEdge(nC, laba, nC);
		creator.addEdge(nB, labm, nB);
		creator.addEdge(nL, labl, nL);//
		creator.addEdge(nA, laba, nC);
		creator.addEdge(nC, laba, nB);
		creator.addEdge(nB, laba, nL);
		creator.addEdge(nA, laba, nL);
		creator.addEdge(nC, laba, nL);
		
		creator.setFixed();
		AbstrGraph ag = creator.getConstructedGraph();
		
		
		
	}
		
	private void initGraphs(Graph gA, Graph gB, Graph gC, Graph gD, Graph gL) {
		
		Label laba = DefaultLabel.createLabel("a");
		Label labm = DefaultLabel.createLabel("m");
		Label labl = DefaultLabel.createLabel("l");

		for (int i = 0; i < 3; i++) { gA.addNode(nodes[i]); }
		gA.addEdge(DefaultEdge.createEdge(nodes[0], labm, nodes[0]));
		gA.addEdge(DefaultEdge.createEdge(nodes[1], labm, nodes[1]));
		gA.addEdge(DefaultEdge.createEdge(nodes[2], labm, nodes[2]));//
		gA.addEdge(DefaultEdge.createEdge(nodes[0], laba, nodes[1]));
		gA.addEdge(DefaultEdge.createEdge(nodes[0], laba, nodes[2]));

		for (int i = 0; i < 4; i++) { gB.addNode(nodes[i]); }
		gB.addEdge(DefaultEdge.createEdge(nodes[0], labm, nodes[0]));
		gB.addEdge(DefaultEdge.createEdge(nodes[1], labl, nodes[1]));
		gB.addEdge(DefaultEdge.createEdge(nodes[2], labl, nodes[2]));
		gB.addEdge(DefaultEdge.createEdge(nodes[3], labm, nodes[3]));//
		gB.addEdge(DefaultEdge.createEdge(nodes[0], laba, nodes[1]));
		gB.addEdge(DefaultEdge.createEdge(nodes[0], laba, nodes[2]));
		gB.addEdge(DefaultEdge.createEdge(nodes[3], laba, nodes[0]));
		
		for (int i = 0; i < 4; i++) { gC.addNode(nodes[i]); }
		gC.addEdge(DefaultEdge.createEdge(nodes[0], labm, nodes[0]));
		gC.addEdge(DefaultEdge.createEdge(nodes[1], labm, nodes[1]));
		gC.addEdge(DefaultEdge.createEdge(nodes[2], labl, nodes[2]));
		gC.addEdge(DefaultEdge.createEdge(nodes[3], labm, nodes[3]));//
		gC.addEdge(DefaultEdge.createEdge(nodes[0], laba, nodes[1]));
		gC.addEdge(DefaultEdge.createEdge(nodes[0], laba, nodes[2]));
		gC.addEdge(DefaultEdge.createEdge(nodes[3], laba, nodes[0]));
		
		for (int i = 0; i < 4; i++) { gD.addNode(nodes[i]); }
		gD.addEdge(DefaultEdge.createEdge(nodes[0], labm, nodes[0]));
		gD.addEdge(DefaultEdge.createEdge(nodes[1], labm, nodes[1]));
		gD.addEdge(DefaultEdge.createEdge(nodes[2], labm, nodes[2]));
		gD.addEdge(DefaultEdge.createEdge(nodes[3], labm, nodes[3]));//
		gD.addEdge(DefaultEdge.createEdge(nodes[0], laba, nodes[1]));
		gD.addEdge(DefaultEdge.createEdge(nodes[0], laba, nodes[2]));
		gD.addEdge(DefaultEdge.createEdge(nodes[3], laba, nodes[0]));
		
		for (int i = 0; i < 2; i++) { gL.addNode(nodes[i]); }
		gL.addEdge(DefaultEdge.createEdge(nodes[0], labl, nodes[0]));
		gL.addEdge(DefaultEdge.createEdge(nodes[1], labm, nodes[1]));
		gL.addEdge(DefaultEdge.createEdge(nodes[1], laba, nodes[0]));
	}

	public static void main (String[] args) {
		TestingTransforming test = new TestingTransforming();
		test.setUp();
		//test.testTransformBinaryTree();
		
		// --------------------------
		SPORule rule = (SPORule) test.binaryTreeGrammar.getRule("expand");
		SystemRecord syst = new SystemRecord(test.binaryTreeGrammar, true);
		Graph g = rule.lhs().clone();
		Node n = g.nodeSet().iterator().next();
		VarNodeEdgeMap match2 = new VarNodeEdgeHashMap();
		match2.putNode(n, n);
		match2 = Util.getMatchesIter(rule.lhs(), g, match2).iterator().next();
		RuleEvent event2 = new SPOEvent(rule, match2, syst, true);
		RuleApplication appl2 = new DefaultApplication(event2, g);
		appl2.applyDelta(g);
		Graph g3 = appl2.getTarget();
		System.out.println(g3);
		
	}
	
	
	
	private class TypingImpl implements ConcretePart.Typing {
		DefaultAbstrGraph sg;
		NodeEdgeMap m;
		TypingImpl (DefaultAbstrGraph sg, NodeEdgeMap m) { this.sg = sg;  this.m = m; }
		public GraphPattern typeOf(Node n) { return this.sg.typeOf(this.m.getNode(n)); }
	}
	
}
