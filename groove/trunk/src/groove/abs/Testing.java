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
 * $Id: Testing.java,v 1.3 2008-01-30 09:32:23 iovka Exp $
 */
package groove.abs;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JFrame;

import groove.graph.DefaultEdge;
import groove.graph.DefaultGraph;
import groove.graph.DefaultLabel;
import groove.graph.DefaultMorphism;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.NodeEdgeHashMap;
import groove.graph.NodeEdgeMap;
import groove.gui.Options;
import groove.gui.jgraph.GraphJModel;
import groove.gui.jgraph.JGraph;
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

public class Testing  extends TestCase { 

	// -----------------------------------------------------------------
	// GRAPHS AND MATCHINGS
	//	 -----------------------------------------------------------------
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
	GraphGrammar listGrammar4;
	GraphGrammar listGrammar5;
	GraphGrammar listGrammar10;
	
	
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
			listGrammar4 = (new AspectualViewGps()).unmarshal(new File("../tests/list4.gps"), "start").toGrammar();
		} catch (FormatException e1) {
			e1.printStackTrace();
			System.exit(1);
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(1);
		} 
		
		try {
			listGrammar10 = (new AspectualViewGps()).unmarshal(new File("../tests/list10.gps"), "start").toGrammar();
		} catch (FormatException e1) {
			e1.printStackTrace();
			System.exit(1);
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(1);
		} 
		
		try {
			listGrammar5 = (new AspectualViewGps()).unmarshal(new File("../tests/list5.gps"), "start").toGrammar();
		} catch (FormatException e1) {
			e1.printStackTrace();
			System.exit(1);
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(1);
		} 
	}

	
	public void _testConstructionConcrPart () {
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
		class Toto implements ConcretePart.Typing {

			DefaultAbstrGraph sg;
			Morphism m;
			Toto (DefaultAbstrGraph sg, Morphism m) {
				this.sg = sg;
				this.m = m;
			}
			
			public GraphPattern typeOf(Node n) {
				return sg.typeOf(m.getNode(n));
			}			
		}
		
		Collection<ConcretePart> ext = ConcretePart.extensions(cell, new Toto(s, morph), pf, false, null);	
		assertEquals(1, ext.size());
	}
	
	/** Test with
	 * - list with 4 elements
	 * - rule adding an object 
	 * - matching to the middle shape node
	 * - precision 2
	 * - radius 1
	 */
	public void _testSetMaterialisations1 () {
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
		class Toto implements ConcretePart.Typing {

			DefaultAbstrGraph sg;
			Morphism m;
			Toto (DefaultAbstrGraph sg, Morphism m) {
				this.sg = sg;
				this.m = m;
			}
			
			public GraphPattern typeOf(Node n) {
				return this.sg.typeOf(this.m.getNode(n));
			}			
		}
		SystemRecord syst = new SystemRecord(listGrammar4, true);
		Toto typing = new Toto(s, morph);
		Collection<ConcretePart> ext = ConcretePart.extensions(rule.lhs(), typing, pf, false, syst);
		// there is only one extension
		ConcretePart cp = ext.iterator().next();
		Abstraction.Parameters options = new Abstraction.Parameters(1,2,10);
		SetMaterialisations smat = new SetMaterialisations(cp, s, morph.elementMap(), options);
	
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
		assertEquals(result.size(), 3);	
	}
	
	/** Test with
	 * - list with 4 elements
	 * - rule adding an object 
	 * - matching to the middle shape node
	 * - precision 1
	 * - radius 1
	 */
	public void _testSetMaterialisations2 () {
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
		class Toto implements ConcretePart.Typing {

			DefaultAbstrGraph sg;
			Morphism m;
			Toto (DefaultAbstrGraph sg, Morphism m) {
				this.sg = sg;
				this.m = m;
			}
			
			public GraphPattern typeOf(Node n) {
				return this.sg.typeOf(this.m.getNode(n));
			}			
		}
		Toto typing = new Toto(s, morph);
		SystemRecord syst = new SystemRecord(listGrammar4, true);
		Collection<ConcretePart> ext = ConcretePart.extensions(rule.lhs(), typing, pf, false, syst);
		// there is only one extension
		ConcretePart cp = ext.iterator().next();
		Abstraction.Parameters options = new Abstraction.Parameters(1,1,10);
		SetMaterialisations smat = new SetMaterialisations(cp, s, morph.elementMap(), options);

		
		
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
		assertEquals(result.size(), 10);	
	}
	
	
	/** Test with
	 * - list with 10 elements
	 * - rule adding an object 
	 * - matching to the middle shape node
	 * - precision 1
	 * - radius 2
	 */
	public void _testSetMaterialisations3 () {
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
		class Toto implements ConcretePart.Typing {

			DefaultAbstrGraph sg;
			Morphism m;
			Toto (DefaultAbstrGraph sg, Morphism m) {
				this.sg = sg;
				this.m = m;
			}
			
			public GraphPattern typeOf(Node n) {
				return this.sg.typeOf(this.m.getNode(n));
			}			
		}
		Toto typing = new Toto(s, morph);
		SystemRecord syst = new SystemRecord(listGrammar10, true);
		Collection<ConcretePart> ext = ConcretePart.extensions(rule.lhs(), typing, pf, false, syst);
		// there is only one extension
		ConcretePart cp = ext.iterator().next();
		Abstraction.Parameters options = new Abstraction.Parameters(2,1,10);
		SetMaterialisations smat = new SetMaterialisations(cp, s, morph.elementMap(), options);

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
		assertEquals(result.size(), 17);	
	}
	
	/** Tests all possible materialisations, for all possible matchings
	  * - list with 4 elements
	  * - precision 2
	  * - radius 1
	  */
	public void testSetMaterialisations4() {
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
			
			class Toto implements ConcretePart.Typing {

				DefaultAbstrGraph sg;
				NodeEdgeMap m;
				Toto (DefaultAbstrGraph sg, NodeEdgeMap m) {
					this.sg = sg;
					this.m = m;
				}
				
				public GraphPattern typeOf(Node n) {
					return this.sg.typeOf(this.m.getNode(n));
				}			
			}
			SystemRecord syst = new SystemRecord(listGrammar4, true);
			Toto typing = new Toto(s, match);
			Collection<ConcretePart> ext = ConcretePart.extensions(rule.lhs(), typing, pf, false, syst);
			for (ConcretePart cp : ext) {
				Abstraction.Parameters options = new Abstraction.Parameters(1,2,10);
				SetMaterialisations smat = new SetMaterialisations(cp, s, match, options);

				// remap the initial mapping into the concrete part
				NodeEdgeMap m = new NodeEdgeHashMap();
				for (Node n : match.nodeMap().keySet()){
					m.putNode(n,n);
				}
				for (Edge e : match.edgeMap().keySet()) {
					m.putEdge(e, e);
				}
			
				RuleEvent event = new SPOEvent(rule, smat.updateMatch(match), syst, false);
				RuleApplication appl = new DefaultApplication(event, cp.graph());
				Collection<AbstrGraph> result = smat.transform(appl, syst);
				all.addAll(result);
				for (AbstrGraph g : result) {
					System.out.println(g);
				}
			}
			System.out.println("*******************************************");
		}
		assertEquals(7, all.size());
	}
	
	public void _testDefaultAbstractGraph () {
		// Test isomorphism check
 		PatternFamily pf1 = new PatternFamily(1, 10);
 		PatternFamily pf2 = pf1; //new PatternFamily(1, 10, Util.labelSet(listGrammar10.getStartGraph()));
 		
 		DefaultAbstrGraph s_l4_1 = null; // list with 4 cells, precision 1
 		DefaultAbstrGraph s_l4_2 = null; // list with 4 cells, precision 2
 		DefaultAbstrGraph s_l5_1 = null; // list with 5 cells, precision 1
 		DefaultAbstrGraph s_l5_2 = null; // list with 5 cells, precision 2
 		DefaultAbstrGraph s_l10_1 = null; // list with 10 cells, precision 1
 		DefaultAbstrGraph s_l10_2 = null; // list with 10 cells, precision 2
 		
		try {
			s_l4_1 = DefaultAbstrGraph.factory(pf1,1).getShapeGraphFor(listGrammar4.getStartGraph());
			s_l4_2 = DefaultAbstrGraph.factory(pf2,2).getShapeGraphFor(listGrammar4.getStartGraph());
			s_l5_1 = DefaultAbstrGraph.factory(pf1,1).getShapeGraphFor(listGrammar5.getStartGraph());
			s_l5_2 = DefaultAbstrGraph.factory(pf2,2).getShapeGraphFor(listGrammar5.getStartGraph());
			s_l10_1 = DefaultAbstrGraph.factory(pf1,1).getShapeGraphFor(listGrammar10.getStartGraph());
			s_l10_2 = DefaultAbstrGraph.factory(pf2,2).getShapeGraphFor(listGrammar10.getStartGraph());
		} catch (ExceptionIncompatibleWithMaxIncidence e) {
			e.printStackTrace();
		}
		assertNotNull(s_l4_1.getIsomorphismToAbstrGraph(s_l4_1));
		assertNotNull(s_l4_1.getIsomorphismToAbstrGraph(s_l5_1));
		assertNotNull(s_l5_1.getIsomorphismToAbstrGraph(s_l4_1));
		
		assertNull(s_l4_2.getIsomorphismToAbstrGraph(s_l5_2));
		assertNull(s_l5_2.getIsomorphismToAbstrGraph(s_l4_2));
		
		assertNotNull(s_l5_1.getIsomorphismToAbstrGraph(s_l10_1));
		assertNotNull(s_l10_1.getIsomorphismToAbstrGraph(s_l5_1));
		assertNotNull(s_l5_2.getIsomorphismToAbstrGraph(s_l10_2));
		assertNotNull(s_l10_2.getIsomorphismToAbstrGraph(s_l5_2));
	}
	
	
	
	public void _testMultiplicities () {
		
		int precision = 2;
		Multiplicity m = Abstraction.MULTIPLICITY;
		MultiplicityInformation i[] = new MultiplicityInformation[12];
		
		// The values for testing
		
		
		
		i[0] = m.getElement(0, precision);
		i[1] = m.getElement(1, precision);
		i[2] = m.getElement(2, precision);
		i[11] = m.getElement(3, precision);
		
		i[3] = m.add(i[1], 0);
		i[4] = m.add(i[1], 1);
		i[5] = m.add(i[1], 2);
		
		
		
		// assertEquals();
		// assertEquals();
		System.out.println("0 = " + i[0]);
		System.out.println("1 = " + i[1]);
		System.out.println("2 = " + i[2]);
		System.out.println("{w} = " + i[11]);
		
		System.out.println("1 = " + i[3]);
		System.out.println("2 = " + i[4]);
		System.out.println("{w} = " + i[5]);
		
		try {
			i[6] = m.remove(i[11], 0);
			i[7] = m.remove(i[11], 1);
			i[8] = m.remove(i[11], 2);
			i[9] = m.remove(i[11], 3);
			
			System.out.println("{w} = " + i[6]);
			System.out.println("{2,w} = " + i[7]);
			System.out.println("{1,2,w} = " + i[8]);
			System.out.println("{0,1,2,w} = " + i[9]);
			
			
			i[10] = m.add(i[8], 1);
			System.out.println("{2,w} = " + i[10]);
			
		} catch (ExceptionRemovalImpossible e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		
		// testing comparison
		System.out.println("\nTESTING COMPARISONS\n");
		
		MultiplicityInformation j[] = new MultiplicityInformation[7];
		j[0] = m.getElement(0, precision);
		j[1] = m.getElement(1, precision);
		j[2] = m.getElement(2, precision);
		j[3] = m.getElement(3, precision);
		try {
		j[4] = m.remove(j[3], 1);
		j[5] = m.remove(j[3], 2);
		j[6] = m.remove(j[3], 3);
		} catch (ExceptionRemovalImpossible e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		for (int k = 0; k < j.length; k++) {
			for (int n = 0; n < j.length; n++) {
				System.out.println(String.format("%s %s %s\n", j[k], m.compare(j[k], j[n]), j[n]));
			}
		}
		
		
	}
	
	
	public static void main (String[] args) {
		Testing TEST = new Testing();
		// TEST.setUp();
		// TEST.testSetMaterialisations4();
		
		
		//TEST.graphTest();
		
		TEST._testMultiplicities();
		
		
		Graph g = new DefaultGraph();
		Node n1 = g.addNode();
		Node n2 = g.addNode();
		g.addEdge(DefaultEdge.createEdge(n1, DefaultLabel.createLabel("a"), n2));
		g.addEdge(DefaultEdge.createEdge(n1, DefaultLabel.createLabel("b"), n1));
		g.addEdge(DefaultEdge.createEdge(n2, DefaultLabel.createLabel("b"), n2));
		g.addEdge(DefaultEdge.createEdge(n2, DefaultLabel.createLabel("c"), n1));	
		
		System.out.println(g);
		System.out.println("Node : " + n1 + "\n" + g.edgeSet(n1, Edge.SOURCE_INDEX));
		System.out.println(g.edgeSet(n1, Edge.TARGET_INDEX));
		
		System.out.println("Node : " + n2 + "\n" + g.edgeSet(n2, Edge.SOURCE_INDEX));
		System.out.println(g.edgeSet(n2, Edge.TARGET_INDEX));

		
		
	}
	
	
	public void graphTest() {
		JFrame frame = new JFrame("Toto");
		
		Graph g = new DefaultGraph();
		Node n1 = g.addNode();
		Node n2 = g.addNode();
		g.addEdge(DefaultEdge.createEdge(n1, DefaultLabel.createLabel("a"), n2));
		
		GraphJModel model = GraphJModel.newInstance(g, new Options());
		JGraph jgraph = new JGraph(model,false);
		jgraph.setPreferredSize(new Dimension(300, 300));
		frame.getContentPane().add(jgraph);
		frame.pack();
		frame.setVisible(true);

	}
	

//	
//	public void testComputeNewType () {
//		if (true) return;
//		
//		// Graph used for computing the type of a node
//		Graph pattern = null;
//		try {
//    		pattern = (new DefaultGxl()).unmarshalGraph(new File("../tests/testComputeNewType.gps/type.gst"));
//        } catch (IOException exc) {
//            System.err.println("Error in loading graph");
//            System.err.print(exc);
//            System.exit(1);
//        }
//        // Graph used as a host graph
//        Graph host = null;
//		try {
//    		pattern = (new DefaultGxl()).unmarshalGraph(new File("../tests/testComputeNewType.gps/host.gst"));
//        } catch (IOException exc) {
//            System.err.println("Error in loading graph");
//            System.err.print(exc);
//            System.exit(1);
//        }
//        // The production rule
//		GraphGrammar grammar = null;
//		try {
//			grammar = (new AspectualViewGps()).unmarshal(new File("../tests/lists.gps"), "start").toGrammar();
//		} catch (FormatException e1) {
//			e1.printStackTrace();
//			System.exit(1);
//		} catch (IOException e1) {
//			e1.printStackTrace();
//			System.exit(1);
//		} 
//		SystemRecord syst = new SystemRecord(grammar);
//		SPORule rule = (SPORule) grammar.getRule("rule");
//		
//		// Determine the node, compute its type and the typing matching
//		Node nodePattern = null; // source of an "a" edge
//		Node nodeHost = null; 
//		for (Node n : pattern.nodeSet()) {
//			for (Edge ee : pattern.outEdgeSet(n)) {
//				DefaultEdge e = (DefaultEdge) ee;
//				if (e.label().equals(DefaultLabel.createLabel("a"))) {
//					nodePattern = n;
//				}
//			}
//		}
//		for (Node n : host.nodeSet()) {
//			for (Edge ee : pattern.outEdgeSet(n)) {
//				DefaultEdge e = (DefaultEdge) ee;
//				if (e.label().equals(DefaultLabel.createLabel("a"))) {
//					nodeHost = n;
//				}
//			}
//		}
//		assert nodePattern != null && nodeHost != null : "Incorrect initialisation";
//		
//		PatternFamily family = new PatternFamily(1, 10, null);
//		GraphPattern type = null;
//		try {
//			type = family.computeAddPattern(pattern, nodePattern);
//		} catch (ExceptionIncompatibleWithMaxIncidence e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		// Construct a SetMaterialisations object
//		DefaultAbstractGraph shape = null;
//		try {
//			shape = DefaultAbstractGraph.factory(family, 1).getShapeGraphFor(host);
//		} catch (ExceptionIncompatibleWithMaxIncidence e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}		
//	}
	
//	public static void main (String[] args) {
//
//		firstTest();
//		System.out.println("\n*************************\n");
//		secondTest();
//	}
//	
//	static void firstTest() {
//		GraphGrammar grammar = null;
//		try {
//			grammar = (new AspectualViewGps()).unmarshal(new File("../tests/toto.gps"), "start").toGrammar();
//		} catch (FormatException e1) {
//			e1.printStackTrace();
//			System.exit(1);
//		} catch (IOException e1) {
//			e1.printStackTrace();
//			System.exit(1);
//		} 
//		Graph host = grammar.getStartGraph();
//		Graph g = grammar.getRule("toto").lhs();
//		
//		
//		
//		// Get the middle node of g and of the host graph
//		Node middleG = null;
//		for (Node n : g.nodeSet()) {
//			if (g.edgeSet(n).size() > 1) {
//				middleG = n;
//				break;
//			}
//		}
//		Node middleH = null;
//		for (Node n : g.nodeSet()) {
//			if (g.edgeSet(n).size() > 1) {
//				middleH = n;
//				break;
//			}
//		}
//		// should not happen
//		assertTrue(middleG != null);
//		assertTrue(middleH != null);
//		
//		NodeEdgeMap map = new NodeEdgeHashMap();
//		map.putNode(middleG, middleH);
//		
//		Collection<VarNodeEdgeMap> matches = Util.getInjMatchSet(g, host, map);
//		
//		System.out.println("G : " + g);
//		System.out.println("H : " + host + "\n");
//		
//		System.out.println("Middle G : " + middleG + ", Middle H : " + middleH + "\n");
//		
//		
//		for (VarNodeEdgeMap m : matches) {
//			System.out.println(m);
//		}
//	}
//	
//	static void secondTest () {
//		GraphGrammar grammar = null;
//		try {
//			grammar = (new AspectualViewGps()).unmarshal(new File("../tests/testInj.gps"), "start").toGrammar();
//		} catch (FormatException e1) {
//			e1.printStackTrace();
//			System.exit(1);
//		} catch (IOException e1) {
//			e1.printStackTrace();
//			System.exit(1);
//		} 
//		Graph host = grammar.getStartGraph();
//		Graph g = grammar.getRule("rule").lhs();
//		
//		System.out.println("G : " + g);
//		System.out.println("H : " + host + "\n");
//		
//		// Get the middle node the host graph
//		Node middleH = null;
//		for (Node n : host.nodeSet()) {
//			if (host.edgeSet(n).size() > 1) {
//				middleH = n;
//				break;
//			}
//		}		
//		// should not happen
//		assertTrue(middleH != null);
//		System.out.println("Middle G : " + middleH + "\n");
//		
//		// Match both nodes of g to the middle node of the host graph
//		NodeEdgeMap map = new NodeEdgeHashMap();
//		for (Node n : host.nodeSet()) {
//			map.putNode(n, middleH);
//		}
//
//		Collection<VarNodeEdgeMap> matches = Util.getInjMatchSet(g, host, map);
//
//		for (VarNodeEdgeMap m : matches) {
//			System.out.println(m);
//		}
//	}
	
}
