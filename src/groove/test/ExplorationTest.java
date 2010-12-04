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
 * $Id: ExplorationTest.java,v 1.20 2008-01-31 14:25:54 fladder Exp $
 */

package groove.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import groove.explore.Exploration;
import groove.explore.StrategyEnumerator;
import groove.explore.encode.Serialized;
import groove.graph.Graph;
import groove.lts.GTS;
import groove.lts.LTSGraph;
import groove.trans.GraphGrammar;
import groove.util.Groove;
import groove.view.FormatException;
import groove.view.GrammarView;
import groove.view.StoredGrammarView;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

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
        public TestCaseRecord(String grammarName, String startFileName,
                String strategy, int nodeCount, int edgeCount, int openCount) {
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
        testExploration("append.gps", "append-2-list-5", null, 145, 256);
        testExploration("append.gps", "append-2-list-5", "bfs", 145, 256);
        testExploration("append.gps", "append-2-list-5", "dfs", 145, 256);
        testExploration("append.gps", "append-2-list-5", "rete", 145, 256);
        testExploration("append.gps", "append-2-list-5", "cnbound:20", 62, 88,
            13);
        testExploration("append.gps", "append-2-list-5", "cebound:append>6",
            79, 108, 12);
    }

    /** Tests the ferryman sample. */
    @Test
    public void testFerryman() {
        testExploration("ferryman.gps", "start", "bfs", 114, 198);
        testExploration("ferryman.gps", "start", "crule:eat", 40, 51);
    }

    /** Tests the mergers sample. */
    @Test
    public void testMergers() {
        testExploration("mergers.gps", 66, 143);
    }

    /** Tests the regexpr sample. */
    @Test
    public void testRegExpr() {
        testExploration("regexpr.gps", 16, 48);
    }

    /** Tests the lose-nodes sample. */
    @Test
    public void testLooseNodes() {
        testExploration("loose-nodes.gps", 104, 468);
        testExploration("loose-nodes.gps", "start", "linear", 10, 9);
    }

    /** Tests the priorities sample. */
    @Test
    public void testPriorities() {
        testExploration("priorities.gps", "start", "bfs", 13, 34);
        testExploration("priorities.gps", "start", "dfs", 13, 34);
        testExploration("priorities.gps", "start", "linear", 8, 8);
    }

    /** Tests the variables sample. */
    @Test
    public void testVariables() {
        testExploration("variables.gps", "start-smaller", "bfs", 61, 176);
    }

    /** Tests the counting sample. */
    @Test
    public void testCounting() {
        testExploration("counting.gps", 10, 9);
    }

    /** Tests the attributes sample. */
    @Test
    public void testAttributes() {
        testExploration("attributed-graphs.gps", 6, 16);
    }

    /** Tests the attributes sample. */
    @Test
    public void testSierpinsky() {
        GTS lts = testExploration("sierpinsky.gps", "start7", "linear", 8, 7);
        assertEquals(1, lts.getFinalStates().size());
        Graph finalGraph = lts.getFinalStates().iterator().next().getGraph();
        assertEquals(3290, finalGraph.nodeCount());
        assertEquals(6577, finalGraph.edgeCount());
    }

    /** Tests the petri net sample. */
    @Test
    public void testPetrinet() {
        testExploration("petrinet.gps", 6, 9);
        testExploration("petrinet.gps", "start", "rete", 6, 9);
    }

    /** Tests the wildcards sample. */
    @Test
    public void testWildcards() {
        testExploration("wildcards.gps", 8, 12);
    }

    /** tests the subtyping functionality. */
    @Test
    public void testInheritance() {
        testExploration("inheritance.gps", 756, 5374);
    }

    /** Tests various parameters settable through the system properties. */
    @Test
    public void testSystemProperties() {
        StoredGrammarView gg = loadGrammar("simple.gps", null);
        testExploration(gg, null, 41, 300, 0);
        // test check creator edges property
        StoredGrammarView ggCopy =
            loadGrammar("simpleCheckCreatorEdges.gps", null);
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
        return testExploration(testCase.grammarName, testCase.startGraphName,
            testCase.strategy, testCase.nodeCount, testCase.edgeCount);
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
    protected GTS testExploration(GrammarView view, String strategyDescr,
            int nodeCount, int edgeCount, int openCount, boolean save) {
        try {
            GraphGrammar gg = view.toGrammar();
            GTS lts = new GTS(gg);

            Exploration exploration;
            if (strategyDescr == null) {
                exploration = new Exploration();
            } else {
                Serialized strategy =
                    new StrategyEnumerator().parseCommandline(strategyDescr);
                Serialized acceptor = new Serialized("final");
                exploration = new Exploration(strategy, acceptor, 0);
            }
            exploration.play(lts, null);
            assertFalse(exploration.isInterrupted());

            if (save) {
                try {
                    Groove.saveGraph(new LTSGraph(lts), view.getName());
                } catch (IOException exc) { // proceed
                }
            }
            if (nodeCount >= 0) {
                assertEquals(nodeCount, lts.nodeCount());
            }
            if (edgeCount >= 0) {
                assertEquals(edgeCount, lts.edgeCount());
            }
            if (openCount >= 0) {
                assertEquals(openCount, lts.openStateCount());
            }
            return lts;
        } catch (FormatException exc) {
            throw new RuntimeException(exc);
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
    protected GTS testExploration(GrammarView view, String strategyDescr,
            int nodeCount, int edgeCount, int openCount) {
        return testExploration(view, strategyDescr, nodeCount, edgeCount,
            openCount, false);
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
    protected GTS testExploration(String grammarName, String startGraphName,
            String strategyDescr, int nodeCount, int edgeCount, int openCount) {
        GrammarView gg = loadGrammar(grammarName, startGraphName);
        return testExploration(gg, strategyDescr, nodeCount, edgeCount,
            openCount);
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
    protected GTS testExploration(String grammarName, String startGraphName,
            String strategyDescr, int nodeCount, int edgeCount) {
        return testExploration(grammarName, startGraphName, strategyDescr,
            nodeCount, edgeCount, -1);
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
    protected GTS testExploration(String grammarName, String startGraphName,
            int nodeCount, int edgeCount) {
        return testExploration(grammarName, startGraphName, null, nodeCount,
            edgeCount);
    }

    /**
     * Tests exploration of a given grammar, starting at the default start
     * graph. The exploration strategy is the default one.
     * @param grammarName name of the rule system to be tested
     * @param nodeCount expected number of nodes
     * @param edgeCount expected number of edges
     * @return the explored GTS
     */
    protected GTS testExploration(String grammarName, int nodeCount,
            int edgeCount) {
        return testExploration(grammarName, Groove.DEFAULT_START_GRAPH_NAME,
            nodeCount, edgeCount);
    }

    private StoredGrammarView loadGrammar(String grammarName,
            String startGraphName) {
        try {
            return StoredGrammarView.newInstance(new File(INPUT_DIR,
                grammarName), startGraphName, false);
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }
}