/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id$
 */

package groove.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import groove.explore.Exploration;
import groove.explore.ExploreType;
import groove.explore.StrategyEnumerator;
import groove.explore.encode.Serialized;
import groove.explore.util.LTSLabels;
import groove.grammar.Grammar;
import groove.grammar.QualName;
import groove.grammar.host.HostGraph;
import groove.grammar.model.GrammarModel;
import groove.grammar.model.ResourceKind;
import groove.lts.Filter;
import groove.lts.GTS;
import groove.util.Groove;
import groove.util.parse.FormatException;
import junit.framework.Assert;

/**
 * System test class, which explores a number of graph production systems and
 * tests if this gives rise to the expected numbers of states and transitions.
 *
 * @author Arend Rensink
 * @version $Revision$
 */
public class ExplorationTest {
    /** Location of the samples. */
    static public final String INPUT_DIR = "junit/samples";

    /**
     * Record to store the data of a single test case.
     */
    public class TestCaseRecord {
        /** Sets the fields of this record. */
        public TestCaseRecord(String grammarName, String startFileName, String strategy,
            int nodeCount, int edgeCount, int openCount) {
            super();
            this.grammarName = grammarName;
            this.startGraphName = startFileName;
            this.strategy = strategy;
            this.nodeCount = nodeCount;
            this.edgeCount = edgeCount;
            this.openCount = openCount;
        }

        /** Name of the grammar to be explored in this test case */
        public final String grammarName;

        /** Name of the start state to be explored in this test case */
        public final String startGraphName;

        /** Exploration strategy of this test case */
        public final String strategy;

        /** Expected number of nodes resulting from exploration. */
        public final int nodeCount;

        /** Expected number of edges resulting from exploration. */
        public final int edgeCount;

        /** Expected number of open states remaining after exploration. */
        public final int openCount;
    }

    /** Tests the append sample. */
    @Test
    public void testAppend() {
        testExploration("append.gps", "append-2-list-5", "bfs", 145, 256);
        testExploration("append.gps", "append-2-list-5", "dfs", 145, 256);
        testExploration("append.gps", "append-2-list-5", null, 145, 256);
        testExploration("append.gps", "append-2-list-5", "rete", 145, 256);
        testExploration("append.gps", "append-2-list-5", "cnbound:20", 62, 88, 13);
        testExploration("append.gps", "append-2-list-5", "cebound:append>6", 79, 108, 12);
    }

    /** Tests the Car Platooning example without the rule that uses
     *  regular expressions
     */
    @Test
    public void testCarPlatooning() {
        testExploration("car-platooning-no-reg-exp.gps", "start-03", "bfs", 268, 561);
        testExploration("car-platooning-no-reg-exp.gps", "start-03", "dfs", 268, 561);
        testExploration("car-platooning-no-reg-exp.gps", "start-03", "rete", 268, 561);
    }

    /** Tests the complete Car Platooning example
     */
    @Test
    public void testCarPlatooningFull() {
        testExploration("car-platooning.gps", "start-03", "bfs", 268, 561);
        testExploration("car-platooning.gps", "start-03", "dfs", 268, 561);
        testExploration("car-platooning.gps", "start-03", "rete", 268, 561);
    }

    /** Tests the ferryman sample. */
    @Test
    public void testFerryman() {
        testExploration("ferryman.gps", "start", "bfs", 114, 198);
        testExploration("ferryman.gps", "start", "rete", 114, 198);
        testExploration("ferryman.gps", "start", "crule:eat", 40, 51);
    }

    /** Tests the mergers sample. */
    @Test
    public void testMergers() {
        testExploration("mergers.gps", "start", "bfs", 66, 143);
    }

    /** Tests the regexpr sample. */
    @Test
    public void testRegExpr() {
        testExploration("regexpr.gps", "start", "bfs", 16, 48);
        testExploration("regexpr.gps", "start", "rete", 16, 48);
    }

    /** Tests the As-and-Bs grammar meant to test path-match cache behavior  sample. */
    @Test
    public void testAsAndBs() {
        testExploration("As-and-Bs-reg-exp-benchmark.gps", "start", "bfs", 8240, 44774);
        testExploration("As-and-Bs-reg-exp-benchmark.gps", "start", "rete", 8240, 44774);
    }

    /** Tests the lose-nodes sample. */
    @Test
    public void testLooseNodes() {
        testExploration("loose-nodes.gps", 104, 468);
        testExploration("loose-nodes.gps", "start", "rete", 104, 468);
        testExploration("loose-nodes.gps", "start", "linear", 10, 9);
    }

    /** Tests the priorities sample. */
    @Test
    public void testPriorities() {
        testExploration("priorities.gps", "start", "bfs", 13, 34);
        testExploration("priorities.gps", "start", "dfs", 13, 34);
        testExploration("priorities.gps", "start", "linear", 8, 8);
        testExploration("recipe-priorities.gps", "start", "bfs", 13, 56);
        testExploration("recipe-priorities.gps", "start", "linear", 8, 11);
    }

    /** Tests the variables sample. */
    @Test
    public void testVariables() {
        testExploration("variables.gps", "start-smaller", "bfs", 61, 176);
        testExploration("variables.gps", "start-smaller", "rete", 61, 176);
    }

    /** Tests the counting sample. */
    @Test
    public void testCounting() {
        testExploration("counting.gps", "start", "bfs", 10, 9);
        testExploration("counting.gps", "start", "rete", 10, 9);
    }

    /** A very simple/pure tests for the quantifier counting. */
    @Test
    public void testQuantifierCounter() {
        testExploration("quantifierCounter.gps", 16, 41);
        testExploration("quantifierCounter.gps", "start", "rete", 16, 41);
    }

    /** Tests the quantifier counting sample. */
    @Test
    public void testForallCount() {
        testExploration("forallCount.gps", 8, 24);
    }

    /** Tests the attributes sample. */
    @Test
    public void testAttributes() {
        testExploration("attributed-graphs.gps", "start", "bfs", 6, 16);
        testExploration("attributed-graphs.gps", "start", "rete", 6, 16);
    }

    /** Tests the attributes sample. */
    @Test
    public void testSierpinsky() {
        GTS lts = testExploration("sierpinsky.gps", "start7", "linear", 8, 7);
        assertEquals(1, lts.getFinalStates()
            .size());
        HostGraph finalGraph = lts.getFinalStates()
            .iterator()
            .next()
            .getGraph();
        assertEquals(3290, finalGraph.nodeCount());
        assertEquals(6577, finalGraph.edgeCount());
    }

    /** Tests the petri net sample. */
    @Test
    public void testPetrinet() {
        testExploration("petrinet.gps", 6, 9);
        testExploration("petrinet.gps", "start", "rete", 6, 9);
    }

    /** Tests the fibonacci sample. */
    @Test
    public void testFibonacci() {
        testExploration("fibonacci.gps", 63, 63);
    }

    /** Tests the wildcards sample. */
    @Test
    public void testWildcards() {
        testExploration("wildcards.gps", 8, 12);
        testExploration("wildcards.gps", "start", "rete", 8, 12);
    }

    /** tests the subtyping functionality. */
    @Test
    public void testInheritance() {
        testExploration("inheritance.gps", "start", "bfs", 756, 5374);
        testExploration("inheritance.gps", "start", "rete", 756, 5374);
    }

    /** tests attributes, quantifiers and NACs */
    @Test
    public void testLeaderElection() {
        testExploration("leader-election.gps", "start-2", "bfs", 21, 29);
        testExploration("leader-election.gps", "start-2", "rete", 21, 29);
    }

    /** tests recipes */
    @Test
    public void testSubsets() {
        testExploration("subsets.gps", "start-small", "bfs", 8, 10);
        testExploration("subsets.gps", "start", "bfs", 306, 712);
    }

    /** Tests various parameters settable through the system properties. */
    @Test
    public void testSystemProperties() {
        GrammarModel gg = loadGrammar("simple.gps", null);
        testExploration(gg, null, 41, 300, 0);
        // test check creator edges property
        GrammarModel ggCopy = loadGrammar("simpleCheckCreatorEdges.gps", null);
        testExploration(ggCopy, null, 41, 188, 0);
        // test dangling edges property
        ggCopy = loadGrammar("simpleCheckDanglingEdges.gps", null);
        testExploration(ggCopy, null, 41, 230, 0);
        // test injectivity property
        ggCopy = loadGrammar("simpleInjective.gps", null);
        testExploration(ggCopy, null, 13, 64, 0);
        // test check-isomorphism property
        ggCopy = loadGrammar("simpleNoIsomorphism.gps", null);
        testExploration(ggCopy, null, 73, 536, 0);
        // test rhs-is-nac property
        gg = loadGrammar("rhs-is-nac.gps", null);
        testExploration(gg, null, 21, 56, 0);
    }

    /** Tests the wildcards sample. */
    @Test
    public void testInjective() {
        testExploration("injective-nac.gps", 2, 1);
        testExploration("injective-forall.gps", 8, 12);
    }

    /**
     * Tests exploration according to a given test case record.
     * @return the explored GTS
     */
    protected GTS testExploration(TestCaseRecord testCase) {
        return testExploration(testCase.grammarName,
            testCase.startGraphName,
            testCase.strategy,
            testCase.nodeCount,
            testCase.edgeCount);
    }

    /**
     * Tests exploration of a given grammar, saving the GTS if required.
     * @param view the graph grammar to be tested
     * @param strategyDescr description of the exploration strategy to be used
     * @param nodeCount expected number of nodes; disregarded if < 0
     * @param edgeCount expected number of edges; disregarded if < 0
     * @param openCount expected number of open states; disregarded if < 0
     * @return the explored GTS
     */
    protected GTS testExploration(GrammarModel view, String strategyDescr, int nodeCount,
        int edgeCount, int openCount, boolean save) {
        try {
            Grammar gg = view.toGrammar();
            GTS gts = new GTS(gg);

            ExploreType exploreType;
            if (strategyDescr == null) {
                exploreType = ExploreType.DEFAULT;
            } else {
                Serialized strategy = StrategyEnumerator.instance()
                    .parseCommandline(strategyDescr);
                Serialized acceptor = new Serialized("final");
                exploreType = new ExploreType(strategy, acceptor, 0);
            }
            Exploration exploration = exploreType.newExploration(gts, null);
            exploration.play();
            assertFalse(exploration.isInterrupted());

            if (save) {
                try {
                    Groove.saveGraph(gts.toPlainGraph(LTSLabels.DEFAULT, Filter.NONE, null),
                        view.getName());
                } catch (IOException exc) { // proceed
                }
            }
            if (nodeCount >= 0) {
                assertEquals(nodeCount, gts.nodeCount());
            }
            if (edgeCount >= 0) {
                assertEquals(edgeCount, gts.edgeCount());
            }
            if (openCount >= 0) {
                assertEquals(openCount, gts.getOpenStateCount());
            }
            return gts;
        } catch (FormatException exc) {
            Assert.fail(exc.toString());
            return null;
        }
    }

    /**
     * Tests exploration of a given grammar.
     *
     * @param view the graph grammar to be tested
     * @param strategyDescr description of the exploration strategy to be used
     * @param nodeCount expected number of nodes; disregarded if < 0
     * @param edgeCount expected number of edges; disregarded if < 0
     * @param openCount expected number of open states; disregarded if < 0
     * @return the explored GTS
     */
    protected GTS testExploration(GrammarModel view, String strategyDescr, int nodeCount,
        int edgeCount, int openCount) {
        return testExploration(view, strategyDescr, nodeCount, edgeCount, openCount, false);
    }

    /**
     * Tests exploration of a given grammar, starting at a given start graph,
     * and using a given exploration strategy.
     * @param grammarName name of the rule system to be tested
     * @param startGraphName name of the start graph
     * @param strategyDescr description of the exploration strategy to be used
     * @param nodeCount expected number of nodes; disregarded if < 0
     * @param edgeCount expected number of edges; disregarded if < 0
     * @param openCount expected number of open states; disregarded if < 0
     * @return the explored GTS
     */
    protected GTS testExploration(String grammarName, String startGraphName, String strategyDescr,
        int nodeCount, int edgeCount, int openCount) {
        GrammarModel gg = loadGrammar(grammarName, startGraphName);
        return testExploration(gg, strategyDescr, nodeCount, edgeCount, openCount);
    }

    /**
     * Tests exploration of a given grammar, starting at a given start graph,
     * and using a given exploration strategy.
     * @param grammarName name of the rule system to be tested
     * @param startGraphName name of the start graph
     * @param strategyDescr description of the exploration strategy to be used
     * @param nodeCount expected number of nodes; disregarded if < 0
     * @param edgeCount expected number of edges; disregarded if < 0
     * @return the explored GTS
     */
    protected GTS testExploration(String grammarName, String startGraphName, String strategyDescr,
        int nodeCount, int edgeCount) {
        return testExploration(grammarName,
            startGraphName,
            strategyDescr,
            nodeCount,
            edgeCount,
            -1);
    }

    /**
     * Tests exploration of a given grammar, starting at a given start graph.
     * The exploration strategy is the default one.
     * @param grammarName name of the rule system to be tested
     * @param startGraphName name of the start graph
     * @param nodeCount expected number of nodes; disregarded if < 0
     * @param edgeCount expected number of edges; disregarded if < 0
     * @return the explored GTS
     */
    protected GTS testExploration(String grammarName, String startGraphName, int nodeCount,
        int edgeCount) {
        return testExploration(grammarName, startGraphName, null, nodeCount, edgeCount);
    }

    /**
     * Tests exploration of a given grammar, starting at the default start
     * graph. The exploration strategy is the default one.
     * @param grammarName name of the rule system to be tested
     * @param nodeCount expected number of nodes
     * @param edgeCount expected number of edges
     * @return the explored GTS
     */
    protected GTS testExploration(String grammarName, int nodeCount, int edgeCount) {
        return testExploration(grammarName, Groove.DEFAULT_START_GRAPH_NAME, nodeCount, edgeCount);
    }

    private GrammarModel loadGrammar(String grammarName, String startGraphName) {
        try {
            GrammarModel result = GrammarModel.newInstance(new File(INPUT_DIR, grammarName), false);
            if (startGraphName != null) {
                result.setLocalActiveNames(ResourceKind.HOST, QualName.parse(startGraphName));
            }
            return result;
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }
}