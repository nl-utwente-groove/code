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

import groove.explore.Scenario;
import groove.explore.ScenarioFactory;
import groove.explore.result.Acceptor;
import groove.explore.strategy.DFSStrategy;
import groove.io.FileGps;
import groove.lts.GTS;
import groove.trans.GraphGrammar;
import groove.util.Generator;
import groove.util.Groove;
import groove.view.DefaultGrammarView;
import groove.view.FormatException;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import junit.framework.TestCase;

/**
 * @author Tom Staijen
 * @version $Revision $
 */
public class IOTest extends TestCase {

    static private final String DIRECTORY = "junit/samples/control.gps";
    static private final String JAR_FILE = "junit/samples.jar";
    static private final String ZIP_FILE = "junit/samples.zip";
    static private final String PATH_IN_ARCHIVE = "/samples/control.gps";

    static private final String DEF_START = "start";
    static private final String ALT_START = "start2";
    static private final String DEF_CONTROL = "control";
    static private final String ALT_CONTROL = "control2";

    /**
     * Parser for the exploration strategies.
     */
    private final Generator.ExploreStrategyParser parser =
        new Generator.ExploreStrategyParser(false);

    /** test loading a directory grammar directly */
    public void testLoadDefault() {
        int nodecount = 10;
        int edgecount = 11;
        try {
            testControl(Groove.loadGrammar(DIRECTORY), DEF_START, DEF_CONTROL,
                nodecount, edgecount);
            testControl(Groove.loadGrammar(DIRECTORY, null), DEF_START,
                DEF_CONTROL, nodecount, edgecount);
            
            File file = new File(DIRECTORY);
            URL url = FileGps.toURL(file);
            
            FileGps gps = new FileGps(false);
            testControl(gps.unmarshal(file), DEF_START,DEF_CONTROL, nodecount, edgecount);
            testControl(gps.unmarshal(file, null), DEF_START, DEF_CONTROL, nodecount, edgecount);
            
            testControl(gps.unmarshal(url), DEF_START, DEF_CONTROL, nodecount, edgecount);
            testControl(gps.unmarshal(url, null,null), DEF_START, DEF_CONTROL, nodecount, edgecount);
            testControl(gps.unmarshal(url, DEF_START,null), DEF_START, DEF_CONTROL, nodecount, edgecount);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void testLoadAltStart() {
        int nodecount = 11;
        int edgecount = 13;
        try {
            testControl(Groove.loadGrammar(DIRECTORY, ALT_START), ALT_START, DEF_CONTROL, nodecount,
                edgecount);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void testLoadAltControl() {
        int nodecount = 10;
        int edgecount = 11;
//        try {
//           
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    public void testLoadAltBoth() {
        int nodecount = 12;
        int edgecount = 15;
        try {
            URL dir = FileGps.toURL(new File(DIRECTORY));
            testControl(new FileGps(true).unmarshal(dir, ALT_START, ALT_CONTROL), ALT_START, ALT_CONTROL, nodecount, edgecount);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    protected void testControl(DefaultGrammarView view, String startName,
            String controlName, int nodecount, int edgecount) {
        testExploration(view, "control", startName, controlName, 3, nodecount,
            edgecount);
    }

    /**
     * Tests exploration of a given grammar, saving the GTS if required.
     * @param view the graph grammar to be tested
     * @param strategyDescr description of the exploration strategy to be used,
     *        in the format of {@link groove.util.Generator.ExploreOption}
     * @param nodeCount expected number of nodes; disregarded if < 0
     * @param edgeCount expected number of edges; disregarded if < 0
     * @param openCount expected number of open states; disregarded if < 0
     * @return the explored GTS
     */
    protected GTS testExploration(DefaultGrammarView view, String grammarName,
            String startName, String controlName, int rulecount, int nodeCount,
            int edgeCount) {
        try {

            GraphGrammar gg = view.toGrammar();

            assertEquals(grammarName, gg.getName());
            assertEquals(startName, view.getStartGraph().getName());
            assertEquals(controlName, view.getControl().getName());
            assertEquals(rulecount, gg.getRules().size());

            GTS lts = new GTS(gg);
            Scenario scenario = ScenarioFactory.getScenario(new DFSStrategy(), new Acceptor(), "bah", "dus");
            scenario.prepare(lts);
            scenario.play();
            assertFalse(scenario.isInterrupted());

            if (nodeCount >= 0) {
                assertEquals(nodeCount, lts.nodeCount());
            }
            if (edgeCount >= 0) {
                assertEquals(edgeCount, lts.edgeCount());
            }
            return lts;
        } catch (FormatException exc) {
            throw new RuntimeException(exc);
        }
    }

}
