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
 * $Id: AnyTest.java 5742 2015-11-23 21:39:24Z rensink $
 */
package groove.test.control;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;

import groove.explore.Exploration;
import groove.grammar.QualName;
import groove.grammar.model.GrammarModel;
import groove.grammar.model.ResourceKind;
import groove.lts.GTS;
import groove.util.Groove;
import groove.util.parse.FormatException;

/**
 * Abstract superclass for control tests.
 * @author Arend Rensink
 * @version $Revision $
 */
@SuppressWarnings("all")
abstract public class AControlTest {
    private GrammarModel sgv;

    abstract protected String getDirectory();

    @Before
    public void loadGrammar() {
        try {
            this.sgv = Groove.loadGrammar(getDirectory());
        } catch (IOException exc) {
            throw new IllegalStateException();
        }
    }

    protected void explore(String control, int controlStates, int controlTransitions,
        int expectedNodes, int expectedEdges) {
        try {
            this.sgv.setLocalActiveNames(ResourceKind.CONTROL, QualName.parse(control));
            GTS lts = new GTS(this.sgv.toGrammar());

            Exploration exploration = Exploration.explore(lts);

            assertFalse(exploration.isInterrupted());
            assertEquals(expectedNodes, lts.nodeCount());
            assertEquals(expectedEdges, lts.edgeCount());
        } catch (FormatException e) {
            fail(e.getMessage());
        }
    }
}
