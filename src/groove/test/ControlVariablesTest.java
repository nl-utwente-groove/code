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
 * $Id$
 */
package groove.test;

import groove.control.ControlAutomaton;
import groove.explore.GeneratorScenarioFactory;
import groove.explore.Scenario;
import groove.explore.strategy.BFSStrategy;
import groove.lts.GTS;
import groove.trans.SystemProperties;
import groove.util.Groove;
import groove.view.StoredGrammarView;
import junit.framework.TestCase;

/**
 * Tests the variables extension to the control language
 * @author Olaf Keijsers
 * @version $Revision $
 */
@SuppressWarnings("all")
public class ControlVariablesTest extends TestCase {
    static private final String DIRECTORY = "junit/samples/control2.gps";

    public void testVariables() {
        // a; a; a;
        explore("variablesTest1", 4, 3, 4, 6);

        // a; a(out var1); a; a(out var2); a;
        explore("variablesTest2", 6, 5, 14, 20);

        // a(out var1); a; a(out var2); try { b(var1, var2); } else { a(var1); }
        explore("variablesTest3", 5, 5, 13, 14);

        // addNode(out var1); deleteNode(var1);
        explore("variablesTest4", 3, 2, 3, 2);

        // addNode(out var1); deleteNode(var1); a(var1);
        // to show that the last rule will not match because var1 has been deleted
        explore("variablesTest5", 3, 2, 3, 2);

        // b(out var1, out var2); b(var2, out var3);
        explore("variablesTest6", 3, 2, 13, 20);

        // addNode; merge(out par1, out par2)*;
        //explore("mergeTest", 2, 1, 11, 19);
    }

    private void explore(String control, int controlStates,
            int controlTransitions, int expectedNodes, int expectedEdges) {
        try {
            StoredGrammarView sgv = Groove.loadGrammar(DIRECTORY);
            SystemProperties sp = sgv.getProperties();
            sp.setControlName(control);
            sp.setUseControl(true);
            ControlAutomaton ca =
                sgv.getControlView().toAutomaton(sgv.toGrammar());
            GTS lts = new GTS(sgv.toGrammar());

            Scenario scenario =
                GeneratorScenarioFactory.getScenarioHandler(new BFSStrategy(),
                    "Breadth first full exploration.", "full");
            scenario.prepare(lts);
            scenario.play();

            assertFalse(scenario.isInterrupted());
            assertEquals(expectedNodes, lts.nodeCount());
            assertEquals(expectedEdges, lts.edgeCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
