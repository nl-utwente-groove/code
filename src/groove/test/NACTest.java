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
 * $Id: NACTest.java,v 1.20 2008-01-30 09:33:05 iovka Exp $
 */
package groove.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import groove.graph.DefaultEdge;
import groove.graph.DefaultGraph;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphProperties;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.trans.EdgeEmbargo;
import groove.trans.MergeEmbargo;
import groove.trans.NotCondition;
import groove.trans.Rule;
import groove.trans.RuleApplication;
import groove.trans.RuleEdge;
import groove.trans.RuleGraph;
import groove.trans.RuleGraphMap;
import groove.trans.RuleMatch;
import groove.trans.RuleName;
import groove.trans.RuleNode;
import groove.trans.SPORule;
import groove.trans.SystemProperties;
import groove.view.FormatException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

/**
 * This test suite tests Embargoes. It uses one basic production:
 * <ul>
 * <li> 0 --a--> 1 to 0 --b--> 2
 * </ul>
 * and augments this in four ways:
 * <ul>
 * <li> A merge embargo NAC0: 0 <> 1
 * <li> An edge embargo NAC1: 1 -/-c-->
 * <li> An edge embargo NAC2: 1 -/-c.a-->
 * <li> An edge embargo NAC3: 1 -/-c--> 0
 * </ul>
 * as well as the combination of 0+3, on three graphs:
 * <ul>
 * <li> g0: 0 <--b-- 1 --a--> 2 --c--> 1
 * <li> g1: 0 --a--> 0 --c--> 1
 * <li> g2: 0 --a--> 1 --a--> 2 <--c-- 1
 * </ul>
 * @version $Revision$
 */
@SuppressWarnings("all")
public class NACTest {

    protected static final int NR_NACS = 4;
    protected static final int NR_GRAPHS = 3;
    protected static final int G0_INDEX = 2;

    protected SPORule rule;
    protected NotCondition[] NACs = new NotCondition[NR_NACS];
    protected Graph[] g = new Graph[NR_GRAPHS];

    protected RuleNode[][] ruleNodes = new RuleNode[2 + NR_NACS][];
    protected RuleEdge[][] ruleEdges = new RuleEdge[2 + NR_NACS][];

    protected Node[][] stateNodes = new Node[2 + NR_GRAPHS][];
    protected Edge[][] stateEdges = new Edge[2 + NR_GRAPHS][];

    /** The setup is as in the paper */
    @Before
    public void setUp() {
        RuleGraph protREGraph = new RuleGraph();
        int[] lhsSrc = {0};
        String[] lhsLab = {"a"};
        int[] lhsTgt = {1};
        RuleGraph lhs =
            setUpRuleGraph(protREGraph, 0, 2, lhsSrc, lhsLab, lhsTgt);

        int[] rhsSrc = {0};
        String[] rhsLab = {"b"};
        int[] rhsTgt = {1};
        RuleGraph rhs =
            setUpRuleGraph(protREGraph, 1, 2, rhsSrc, rhsLab, rhsTgt);

        RuleGraphMap ruleMorphism = new RuleGraphMap();
        lhs.addNode(this.ruleNodes[0][0]);
        rhs.addNode(this.ruleNodes[1][0]);
        ruleMorphism.putNode(this.ruleNodes[0][0], this.ruleNodes[1][0]);
        GraphProperties ruleProperties = new GraphProperties();
        ruleProperties.setPriority(0);
        ruleProperties.setConfluent(false);
        this.rule =
            new SPORule(new RuleName("test"), lhs, rhs, ruleMorphism,
                ruleProperties, SystemProperties.DEFAULT_PROPERTIES);

        this.NACs[0] =
            new MergeEmbargo(lhs, this.ruleNodes[0][0], this.ruleNodes[0][1],
                null, SystemProperties.getInstance());

        this.NACs[3] =
            new EdgeEmbargo(lhs, new RuleEdge(this.ruleNodes[0][1], "c",
                this.ruleNodes[0][0]), SystemProperties.getInstance(), null);

        RuleGraph protGraph = new RuleGraph();
        int[] g0Src = {1, 1, 2};
        String[] g0Lab = {"b", "a", "c"};
        int[] g0Tgt = {0, 2, 1};
        this.g[0] = setUpStateGraph(protGraph, 0, 3, g0Src, g0Lab, g0Tgt);

        int[] g1Src = {0, 0};
        String[] g1Lab = {"a", "c"};
        int[] g1Tgt = {0, 1};
        this.g[1] = setUpStateGraph(protGraph, 1, 2, g1Src, g1Lab, g1Tgt);

        int[] g2Src = {0, 1, 1};
        String[] g2Lab = {"a", "a", "c"};
        int[] g2Tgt = {1, 2, 2};
        this.g[2] = setUpStateGraph(protGraph, 2, 3, g2Src, g2Lab, g2Tgt);
    }

    private RuleGraph setUpRuleGraph(RuleGraph prototype, int graphNr,
            int nrNodes, int[] sources, String[] labels, int[] targets) {
        RuleGraph res = prototype.newGraph();

        this.ruleNodes[graphNr] = new DefaultNode[nrNodes];
        for (int j = 0; j < nrNodes; j++) {
            this.ruleNodes[graphNr][j] = DefaultNode.createNode();
            res.addNode(this.ruleNodes[graphNr][j]);
        }

        int nrEdges = sources.length;
        this.ruleEdges[graphNr] = new RuleEdge[nrEdges];
        for (int j = 0; j < nrEdges; j++) {
            this.ruleEdges[graphNr][j] =
                new RuleEdge(this.ruleNodes[graphNr][sources[j]], labels[j],
                    this.ruleNodes[graphNr][targets[j]]);
            res.addEdge(this.ruleEdges[graphNr][j]);
        }

        return res;
    }

    private Graph setUpStateGraph(RuleGraph prototype, int graphNr,
            int nrNodes, int[] sources, String[] labels, int[] targets) {
        Graph res = new DefaultGraph();

        this.stateNodes[graphNr] = new DefaultNode[nrNodes];
        for (int j = 0; j < nrNodes; j++) {
            this.stateNodes[graphNr][j] = DefaultNode.createNode();
            res.addNode(this.stateNodes[graphNr][j]);
        }

        int nrEdges = sources.length;
        this.stateEdges[graphNr] = new DefaultEdge[nrEdges];
        for (int j = 0; j < nrEdges; j++) {
            this.stateEdges[graphNr][j] =
                DefaultEdge.createEdge(this.stateNodes[graphNr][sources[j]],
                    labels[j], this.stateNodes[graphNr][targets[j]]);
            res.addEdge(this.stateEdges[graphNr][j]);
        }

        return res;
    }

    @Test
    public void testRule() {
        try {
            this.rule.setFixed();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        Collection<RuleApplication> derivSet =
            getDerivations(this.rule, this.g[0]);
        assertEquals(1, derivSet.size());
        Iterator<RuleApplication> derivIter = derivSet.iterator();
        RuleApplication deriv = derivIter.next();
        equalsG0Deriv(deriv.getMorphism());

        derivSet = getDerivations(this.rule, this.g[1]);
        assertEquals(1, derivSet.size());
        derivIter = derivSet.iterator();
        deriv = derivIter.next();

        derivSet = getDerivations(this.rule, this.g[2]);
        assertEquals(2, derivSet.size());
        derivIter = derivSet.iterator();
    }

    @Test
    public void testNAC0() {
        this.rule.addSubCondition(this.NACs[0]);
        try {
            this.rule.setFixed();
        } catch (FormatException e) {
            e.printStackTrace();
        }

        Collection<RuleApplication> derivSet =
            getDerivations(this.rule, this.g[0]);
        assertEquals(1, derivSet.size());

        derivSet = getDerivations(this.rule, this.g[1]);
        assertEquals(0, derivSet.size());

        derivSet = getDerivations(this.rule, this.g[2]);
        assertEquals(2, derivSet.size());
    }

    @Test
    public void testNAC3() {
        this.rule.addSubCondition(this.NACs[3]);
        try {
            this.rule.setFixed();
        } catch (FormatException e) {
            e.printStackTrace();
        }

        Collection<RuleApplication> derivSet =
            getDerivations(this.rule, this.g[0]);
        assertEquals(0, derivSet.size());

        derivSet = getDerivations(this.rule, this.g[1]);
        assertEquals(1, derivSet.size());

        derivSet = getDerivations(this.rule, this.g[2]);
        assertEquals(2, derivSet.size());
    }

    @Test
    public void testNAC03() {
        this.rule.addSubCondition(this.NACs[0]);
        this.rule.addSubCondition(this.NACs[3]);
        try {
            this.rule.setFixed();
        } catch (FormatException e) {
            e.printStackTrace();
        }

        Collection<RuleApplication> derivSet =
            getDerivations(this.rule, this.g[0]);
        assertEquals(0, derivSet.size());

        derivSet = getDerivations(this.rule, this.g[1]);
        assertEquals(0, derivSet.size());

        derivSet = getDerivations(this.rule, this.g[2]);
        assertEquals(2, derivSet.size());
    }

    private Collection<RuleApplication> getDerivations(SPORule rule, Graph graph) {
        Collection<RuleApplication> result = new ArrayList<RuleApplication>();
        for (RuleMatch match : ((Rule) rule).getMatches(graph, null)) {
            result.add(match.newEvent(null).newApplication(graph));
        }
        return result;
    }

    private void equalsG0Deriv(Morphism derivMorph) {
        assertEquals(this.g[0], derivMorph.dom());
        assertEquals(null, derivMorph.getNode(this.stateNodes[0][2]));
        Edge image = derivMorph.getEdge(this.stateEdges[0][0]);
        assertTrue(image != null);
        Collection<? extends Edge> targetOutEdgeSet =
            derivMorph.cod().outEdgeSet(
                derivMorph.getNode(this.stateNodes[0][1]));
        assertEquals(2, targetOutEdgeSet.size());
        assertTrue(targetOutEdgeSet.contains(image));
    }
}