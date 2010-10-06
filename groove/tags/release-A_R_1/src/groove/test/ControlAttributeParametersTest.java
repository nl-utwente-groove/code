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

import groove.explore.Exploration;
import groove.lts.GTS;
import groove.trans.SystemProperties;
import groove.util.Groove;
import groove.view.FormatException;
import groove.view.StoredGrammarView;

import java.io.IOException;

import junit.framework.TestCase;

/**
 * Tests the attribute parameter part of the control language.
 * @author Olaf Keijsers
 * @version $Revision $
 */
@SuppressWarnings("all")
public class ControlAttributeParametersTest extends TestCase {
    static private final String DIRECTORY = "junit/samples/attributes.gps";

    public void testAttributes() {
        explore("control", 9, 8, 54, 67);
    }

    private void explore(String control, int controlStates,
            int controlTransitions, int expectedNodes, int expectedEdges) {
        try {
            StoredGrammarView sgv = Groove.loadGrammar(DIRECTORY);
            SystemProperties sp = sgv.getProperties();
            sp.setControlName(control);
            sp.setUseControl(true);
            GTS lts = new GTS(sgv.toGrammar());

            Exploration exploration = new Exploration();
            exploration.play(lts, null);

            assertFalse(exploration.isInterrupted());
            assertEquals(expectedNodes, lts.nodeCount());
            assertEquals(expectedEdges, lts.edgeCount());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } catch (FormatException e) {
            throw new IllegalStateException(e);
        }
    }
}
