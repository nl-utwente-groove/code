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

package groove.test.control;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import groove.control.instance.Automaton;
import groove.explore.Exploration;
import groove.explore.ExploreType;
import groove.explore.StrategyEnumerator;
import groove.explore.encode.Serialized;
import groove.grammar.Grammar;
import groove.grammar.QualName;
import groove.grammar.model.GrammarModel;
import groove.grammar.model.ResourceKind;
import groove.gui.Viewer;
import groove.lts.GTS;
import groove.lts.GTSCounter;
import groove.util.parse.FormatException;

/**
 * System test class, which explores a number of graph production systems and
 * tests if this gives rise to the expected numbers of states and transitions.
 *
 * @author Arend Rensink
 * @version $Revision$
 */
@SuppressWarnings("all")
public class RecipeTest {
    /** Location of the samples. */
    static public final String GRAMMAR = "junit/samples/recipes.gps";
    /** Counter for the GTS. */
    static public final GTSCounter counter = new GTSCounter();

    @Test
    public void testAOnly() {
        testExploration("start-A-only", "ab-recipes", 2, 2, 1, 1);
        testExploration("start-A-only", "alap-recipes", 3, 3, 3, 3);
    }

    @Test
    public void testABOnly() {
        testExploration("start-AB-only", "ab-recipes", 4, 8, 6, 10);
        testExploration("start-AB-only", "alap-recipes", 3, 7, 6, 10);
    }

    @Test
    public void testTiny() {
        testExploration("start-tiny", "ab-recipes", 6, 6, 8, 8);
        testExploration("start-tiny", "alap-recipes", 6, 8, 12, 14);
    }

    @Test
    public void testSmall() {
        testExploration("start-small", "ab-recipes", 16, 32, 56, 72);
        testExploration("start-small", "alap-recipes", 6, 28, 24, 60);
    }

    @Test
    public void testFull() {
        testExploration("start", "ab-recipes", 224, 736, 1888, 2368);
        testExploration("start", "alap-recipes", 6, 316, 48, 1176);
    }

    private void setStateCount(int highLevelCount, int lowLevelCount) {
        this.highLevelStateCount = highLevelCount;
        this.lowLevelStateCount = lowLevelCount;
    }

    private int highLevelStateCount;
    private int lowLevelStateCount;

    private void setTransitionCount(int highLevelCount, int lowLevelCount) {
        this.highLevelTransCount = highLevelCount;
        this.lowLevelTransCount = lowLevelCount;
    }

    private int lowLevelTransCount;
    private int highLevelTransCount;

    /**
     *
     * @param startGraphName name of the start graph
     * @param control name of the control program
     * @param hls high-level state count
     * @param lls low-level state count
     * @param hlt high-level transition count
     * @param llt low-level transition count
     */
    private void testExploration(String startGraphName, String control, int hls, int lls, int hlt,
        int llt) {
        setStateCount(hls, lls);
        setTransitionCount(hlt, llt);
        testExploration(startGraphName, control);
    }

    /**
     * Tests exploration of a given start graph,
     * and using a given control program.
     * @param startGraphName name of the start graph
     * @param control name of the control program
     */
    private void testExploration(String startGraphName, String control) {
        testExploration(startGraphName, control, "bfs");
        testExploration(startGraphName, control, "dfs");
    }

    /**
     * Tests exploration of a given start graph,
     * and using a given control program and exploration strategy.
     * @param startGraphName name of the start graph
     * @param control name of the control program
     * @param strategyDescr description of the exploration strategy to be used
     */
    private void testExploration(String startGraphName, String control, String strategyDescr) {
        try {
            GrammarModel ggModel = loadGrammar(GRAMMAR, startGraphName);
            ggModel.setLocalActiveNames(ResourceKind.CONTROL, QualName.name(control));
            Grammar gg = ggModel.toGrammar();
            Automaton a = gg.getControl();
            a.explore();
            if (DEBUG) {
                Viewer.showGraph(a.toGraph(FULL_GRAPH), true);
            }
            runExploration(gg, strategyDescr);
            assertEquals(this.highLevelStateCount,
                counter.getStateCount() - counter.getRecipeStageCount());
            assertEquals(this.lowLevelStateCount,
                counter.getStateCount() - counter.getAbsentStateCount());
            assertEquals(this.highLevelTransCount,
                counter.getTransitionCount() - counter.getRecipeStepCount());
            assertEquals(this.lowLevelTransCount,
                counter.getRuleTransitionCount() - counter.getAbsentTransitionCount());
        } catch (FormatException exc) {
            fail(exc.toString());
        }
    }

    private void runExploration(Grammar gg, String strategyDescr) {
        try {
            GTS gts = new GTS(gg);
            counter.setGTS(gts);

            ExploreType exploreType;
            if (strategyDescr == null) {
                exploreType = ExploreType.DEFAULT;
            } else {
                Serialized strategy = StrategyEnumerator.instance()
                    .parseCommandline(strategyDescr);
                Serialized acceptor = new Serialized("final");
                exploreType = new ExploreType(strategy, acceptor, 0);
            }
            Exploration exploration = exploreType.newExploration(gts)
                .play();
            assertFalse(exploration.isInterrupted());
        } catch (FormatException exc) {
            fail(exc.toString());
        }
    }

    private GrammarModel loadGrammar(String grammarName, String startGraphName) {
        try {
            GrammarModel result = GrammarModel.newInstance(new File(grammarName), false);
            if (startGraphName != null) {
                result.setLocalActiveNames(ResourceKind.HOST, QualName.name(startGraphName));
            }
            return result;
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    private final static boolean DEBUG = false;
    private final static boolean FULL_GRAPH = false;
}