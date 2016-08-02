/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
package groove.test.type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import groove.explore.Exploration;
import groove.grammar.QualName;
import groove.grammar.model.GrammarModel;
import groove.grammar.model.ResourceKind;
import groove.lts.GTS;
import groove.util.Groove;
import junit.framework.Assert;

/** Set of tests for dynamic type constraints. */
public class DynamicConstraintTest {
    /** Location of the samples. */
    static public final String INPUT_DIR = "junit/types";

    /** Tests type multiplicities. */
    @Test
    public void testContainment() {
        testError("containment", "start-cycle");
        test("containment", "start-no-cycle", 16, 20, 8);
    }

    /** Tests type multiplicities. */
    @Test
    public void testMultiplicities() {
        testError("mult", "start-violates-in");
        testError("mult", "start-violates-out");
        test("mult", "start-correct-in", 3, 3, 2);
        test("mult", "start-correct-out", 3, 3, 2);
    }

    private void testError(String grammarName, String startGraphName) {
        try {
            loadGTS(grammarName, startGraphName);
            Assert.fail("Loading " + grammarName + " with start graph " + startGraphName
                + " should fail but didn't");
        } catch (Exception exc) {
            // expected behaviour
        }
    }

    /**
     * Tests exploration of a given grammar, saving the GTS if required.
     * @param grammarName name of the graph grammar to be tested
     * @param startGraphName name of the start graph to be tested
     * @param nodeCount expected number of nodes
     * @param edgeCount expected number of edges
     * @param errorCount expected number of error states
     */
    private void test(String grammarName, String startGraphName, int nodeCount, int edgeCount,
        int errorCount) {
        try {
            GTS lts = loadGTS(grammarName, startGraphName);

            Exploration exploration = Exploration.explore(lts);
            assertFalse(exploration.isInterrupted());

            assertEquals(nodeCount, lts.nodeCount());
            assertEquals(edgeCount, lts.edgeCount());
            assertEquals(errorCount, lts.getErrorStateCount());
        } catch (Exception exc) {
            exc.printStackTrace();
            Assert.fail(exc.toString());
        }
    }

    private GTS loadGTS(String grammarName, String startGraphName) throws Exception {
        GrammarModel model = Groove.loadGrammar(INPUT_DIR + "/" + grammarName);
        model.setLocalActiveNames(ResourceKind.HOST, QualName.parse(startGraphName));
        return new GTS(model.toGrammar());
    }
}
