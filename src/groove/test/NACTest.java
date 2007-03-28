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
 * $Id: NACTest.java,v 1.2 2007-03-28 15:12:34 rensink Exp $
 */
package groove.test;

import groove.graph.DefaultEdge;
import groove.graph.DefaultGraph;
import groove.graph.DefaultInjectiveMorphism;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Morphism;
import groove.rel.RegExprGraph;
import groove.rel.VarGraph;
import groove.trans.EdgeEmbargo;
import groove.trans.Matching;
import groove.trans.MergeEmbargo;
import groove.trans.NAC;
import groove.trans.NameLabel;
import groove.trans.RuleApplication;
import groove.trans.SPORule;
import groove.trans.DefaultRuleFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.TestCase;

/**
 * This test suite tests Embargoes.
 * It uses one basic production:<ul>
 * <li> 0 --a--> 1   to   0 --b--> 2
 * </ul> and augments this in four ways:<ul>
 * <li> A merge embargo NAC0: 0 <> 1
 * <li> An edge embargo NAC1: 1 -/-c-->
 * <li> An edge embargo NAC2: 1 -/-c.a-->
 * <li> An edge embargo NAC3: 1 -/-c--> 0
 * </ul> as well as the combination of 0+3, on three graphs: <ul>
 * <li> g0: 0 <--b-- 1 --a--> 2 --c--> 1
 * <li> g1: 0 --a--> 0 --c--> 1
 * <li> g2: 0 --a--> 1 --a--> 2 <--c-- 1
 * </ul>
 * @version $Revision: 1.2 $
 */
public class NACTest extends TestCase {
    public NACTest(String name) {
        super(name);
    }

    protected static final int NR_NACS = 4;
    protected static final int NR_GRAPHS = 3;
    protected static final int G0_INDEX = 2;

    protected SPORule rule;
    protected NAC[] NACs = new NAC[NR_NACS];
    protected Graph[] g = new Graph[NR_GRAPHS];

    protected DefaultNode[][] n = new DefaultNode[2+NR_NACS+NR_GRAPHS][];
    protected Edge[][] e = new Edge[2+NR_NACS+NR_GRAPHS][];

    /** The setup is as in the paper */
    @Override
    protected void setUp() {
        VarGraph protREGraph = new RegExprGraph();
        int[] lhsSrc    = { 0 };
        String[] lhsLab = {"a"};
        int[] lhsTgt    = { 1 };
        VarGraph lhs = (VarGraph) setUpGraph(protREGraph, 0, 2, lhsSrc, lhsLab, lhsTgt);

        int[] rhsSrc    = { 0 };
        String[] rhsLab = {"b"};
        int[] rhsTgt    = { 1 };
        VarGraph rhs = (VarGraph) setUpGraph(protREGraph, 1, 2, rhsSrc, rhsLab, rhsTgt);

        DefaultInjectiveMorphism ruleMorphism = new DefaultInjectiveMorphism(lhs,rhs);
        ruleMorphism.dom().addNode(n[0][0]);
        ruleMorphism.cod().addNode(n[1][0]);
        ruleMorphism.putNode(n[0][0], n[1][0]);
        rule = (SPORule) DefaultRuleFactory.getInstance().createRule(ruleMorphism, new NameLabel("test"), 0);

        NACs[0] = new MergeEmbargo(lhs, n[0][0],n[0][1], DefaultRuleFactory.getInstance());
        //String[] NAC1Lab = {"c"};
        //NACs[1] = new Embargo(lhs, n[0][1],labelArray(NAC1Lab));
        //String[] NAC2Lab = {"c","a"};
        //NACs[2] = new Embargo(lhs, n[0][1],labelArray(NAC2Lab));
        NACs[3] = new EdgeEmbargo(lhs, DefaultEdge.createEdge(n[0][1], "c" ,n[0][0]), DefaultRuleFactory.getInstance());

        Graph protGraph = new DefaultGraph();
        int[] g0Src    = { 1 , 1 , 2 };
        String[] g0Lab = {"b","a","c"};
        int[] g0Tgt    = { 0 , 2 , 1 };
        g[0] = setUpGraph(protGraph, G0_INDEX+0, 3, g0Src, g0Lab, g0Tgt);

        int[] g1Src    = { 0 , 0 };
        String[] g1Lab = {"a","c"};
        int[] g1Tgt    = { 0 , 1 };
        g[1] = setUpGraph(protGraph, G0_INDEX+1, 2, g1Src, g1Lab, g1Tgt);

        int[] g2Src    = { 0 , 1 , 1 };
        String[] g2Lab = {"a","a","c"};
        int[] g2Tgt    = { 1 , 2 , 2 };
        g[2] = setUpGraph(protGraph, G0_INDEX+2, 3, g2Src, g2Lab, g2Tgt);
    }

    private Graph setUpGraph(Graph prototype,
                             int graphNr,
                             int nrNodes,
                             int[] sources, 
                             String[] labels, 
                             int[] targets) {
        Graph res = prototype.newGraph();

        n[graphNr] = new DefaultNode[nrNodes];
        for (int j = 0; j < nrNodes; j++) {
             n[graphNr][j] = new DefaultNode();
             res.addNode(n[graphNr][j]);
        }

        int nrEdges = sources.length;
        e[graphNr] = new Edge[nrEdges];
        for (int j = 0; j < nrEdges; j++) {
            e[graphNr][j] = DefaultEdge.createEdge(n[graphNr][sources[j]], 
                                             labels[j], 
                                             n[graphNr][targets[j]]);
            res.addEdge(e[graphNr][j]);
        }

        return res;
    }

    public void testRule() {
        Collection<RuleApplication> derivSet = getDerivations(rule, g[0]);
        assertEquals(1, derivSet.size());
        Iterator<RuleApplication> derivIter = derivSet.iterator();
        RuleApplication deriv = derivIter.next();
        equalsG0Deriv(deriv.getMorphism());

        derivSet = getDerivations(rule, g[1]);
        assertEquals(1, derivSet.size());
        derivIter = derivSet.iterator();
        deriv = derivIter.next();

        derivSet = getDerivations(rule, g[2]);
        assertEquals(2, derivSet.size());
        derivIter = derivSet.iterator();
    }

    public void testNAC0() {
        rule.addNAC(NACs[0]);

        Collection<RuleApplication> derivSet = getDerivations(rule, g[0]);
        assertEquals(1, derivSet.size());

        derivSet = getDerivations(rule, g[1]);
        assertEquals(0, derivSet.size());

        derivSet = getDerivations(rule, g[2]);
        assertEquals(2, derivSet.size());
    }
    /*
    public void testNAC1() {
        rule.add(NACs[1]);

        Collection derivSet = rule.getApplicationsTo(g[0]);
        assertEquals(0, derivSet.size());

        derivSet = rule.getApplicationsTo(g[1]);
        assertEquals(0, derivSet.size());

        derivSet = rule.getApplicationsTo(g[2]);
        assertEquals(1, derivSet.size());
        Iterator derivIter = derivSet.iterator();
        Derivation deriv = (Derivation) derivIter.next();
        equalsG2Deriv1((DefaultTransformation) deriv.transformation());
    }

    public void testNAC2() {
        rule.add(NACs[2]);

        Collection derivSet = rule.getApplicationsTo(g[0]);
        assertEquals(0, derivSet.size());

        derivSet = rule.getApplicationsTo(g[1]);
        assertEquals(1, derivSet.size());

        derivSet = rule.getApplicationsTo(g[2]);
        assertEquals(2, derivSet.size());
    }
    */

    public void testNAC3() {
        rule.addNAC(NACs[3]);

        Collection<RuleApplication> derivSet = getDerivations(rule, g[0]);
        assertEquals(0, derivSet.size());

        derivSet = getDerivations(rule, g[1]);
        assertEquals(1, derivSet.size());

        derivSet = getDerivations(rule, g[2]);
        assertEquals(2, derivSet.size());
    }

    public void testNAC03() {
        rule.addNAC(NACs[0]);
        rule.addNAC(NACs[3]);

        Collection<RuleApplication> derivSet = getDerivations(rule, g[0]);
        assertEquals(0, derivSet.size());

        derivSet = getDerivations(rule, g[1]);
        assertEquals(0, derivSet.size());

        derivSet = getDerivations(rule, g[2]);
        assertEquals(2, derivSet.size());
    }

    private Collection<RuleApplication> getDerivations(SPORule rule, Graph graph) {
    	Collection<RuleApplication> result = new ArrayList<RuleApplication>();
    	Iterator<? extends Matching> matchIter = rule.getMatchingIter(graph);
    	while (matchIter.hasNext()) {
			Matching match = matchIter.next();
			result.add(rule.createApplication(match));
		}
    	return result;
    }
    
    private void equalsG0Deriv(Morphism derivMorph) {
        int g_index = G0_INDEX;
        assertEquals(g[0],derivMorph.dom());
        assertEquals(null,derivMorph.getNode(n[g_index][2]));
        Edge image = derivMorph.getEdge(e[g_index][0]);
        assertTrue(image != null);
        Collection<? extends Edge> targetOutEdgeSet = derivMorph.cod().outEdgeSet(derivMorph.getNode(n[g_index][1]));
        assertEquals(2,targetOutEdgeSet.size());
        assertTrue(targetOutEdgeSet.contains(image));
        // assertEquals(2,targetOutEdgeSet.withLabel(new DefaultLabel("b")).size());
    }
}