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

package groove.test.verify;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import groove.explore.ExploreResult;
import groove.explore.Generator;
import groove.explore.util.LTSLabels;
import groove.graph.Graph;
import groove.util.Groove;
import groove.verify.CTLMarker;
import groove.verify.CTLModelChecker;
import groove.verify.Formula;
import junit.framework.Assert;

/**
 * Tests the CTLStarFormula class.
 * @author Harmen Kastenberg
 * @version $Revision$
 */
public class CTLTest {
    /**
     * Tests whether the circular buffer fulfils certain properties and whether
     * the number of counter examples is correct for other properties.
     */
    @Test
    public void testCircularBuffer() {
        setGTS("circular-buffer", null);
        testFormula("AG(put|get)", 5);
        testFormula("AX(put)", 4);
        testFormula("EX(put)", 5);
        testFormula("!put | EX(get)", 5);
        testFormula("AG(put)", 0);
    }

    /** Test on a specially designed transition system. */
    @Test
    public void testMC() {
        setGTS("mc", null);
        testFormula("p & !q", 1);
        testFormula("E(p U r)", 3);
        testFormula("A(p U r)", 1);
        testFormula("EX r", 3);
        testFormula("!AX r", 3);
        testFormula("EFEG p", 3);
        testFormula("AF(EG p | EG r)", 3);
        testFormula("AX q", 2);
        testFormula("EXEG p", 3);
        testFormula("AXEG p", 0);
        testFormula("AG(p|q)", 3);
        testFormula("AG p", 0);
    }

    /** Test the treatment of parameters in propositions. */
    @Test
    public void testParameters() {
        setGTS("attributes", "start");
        // real parameter
        testFormula("EF 'set_gravity(9.81)'", 1);
        testFormula("EF set_gravity(_)", 1);
        testFormula("EF set_gravity", 1);
        testFormula("EF set_gravity(9)", 0);
        testFormula("EF set_gravity()", 0);
        testFormula("EF set_gravity('text')", 0);
        // text parameter
        testFormula("EX 'set_name(\"attributes test\")'", 1);
        testFormula("EX set_name(\"attributes test\")", 1);
        testFormula("EX set_name( 'attributes test' )", 1);
        testFormula("EX set_name(_)", 1);
        testFormula("EX set_name", 1);
        testFormula("EX set_name( 'attributes  test' )", 0);
        testFormula("EX 'set_name(_)'", 0);
        testFormula("EX 'set_name'", 0);
        testFormula("EX set_name()", 0);
        testFormula("EX set_name(test)", 0);
        // two parameters
        testFormula("EXEX add_score(n0, 100)", 2);
        testFormula("EXEX add_score(_, 100)", 2);
        testFormula("EXEX add_score(n0, _)", 2);
        testFormula("EXEX add_score(_, _)", 2);
        testFormula("EXEX add_score", 2);
        testFormula("EXEX add_score(n1, _)", 0);
        testFormula("EXEX add_score(_)", 0);
        testFormula("EXEX add_score()", 0);
        testFormula("EXEX add_score('n0',100)", 0);
        testFormula("EXEX add_score(n0,'100')", 0);
    }

    /** Test on a specially designed transition system. */
    @Test
    public void testSpecialLabels() {
        setGTS("mc", "start-with-final", "-s", "crule:r");
        testFormula("EF $start", 1);
        testFormula("EF $open", 3);
        testFormula("EF $final", 3);
        testFormula("EF $result", 3);
        setGTS("mc", null, "-s", "crule:r", "-a", "any");
        testFormula("EF $start", 1);
        testFormula("EF $open", 3);
        testFormula("EF $final", 0);
        testFormula("EF $result", 3);
    }

    /** Sets the GTS to a given grammar in the JUnit samples. */
    private void setGTS(String grammarName, String startGraphName, String... otherArgs) {
        try {
            File tmp = File.createTempFile("gts-" + grammarName, ".gxl");
            this.ltsLabels = new LTSLabels("sfro");
            List<String> genArgs = new ArrayList<>();
            genArgs.addAll(Arrays.asList("-v",
                "0",
                "-o",
                tmp.getCanonicalPath(),
                "-ef",
                this.ltsLabels.toString()));
            genArgs.addAll(Arrays.asList(otherArgs));
            genArgs.add("junit/samples/" + grammarName);
            if (startGraphName != null) {
                genArgs.add(startGraphName);
            }
            Generator generator = new Generator(genArgs.toArray(new String[0]));
            this.result = generator.start();
            this.gtsGraph = Groove.loadGraph(tmp);
            tmp.delete();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    /** Tests the number of counterexamples in the current;y
     * set GTS for a given formula. */
    private void testFormula(String formula, int stateCount) {
        try {
            // all states satisfy the following property
            Formula property = Formula.parse(formula)
                .toCtlFormula();
            CTLMarker marker = new CTLMarker(property, CTLModelChecker.newModel(this.result));
            assertEquals(stateCount, marker.getCount(true));
            marker =
                new CTLMarker(property, CTLModelChecker.newModel(this.gtsGraph, this.ltsLabels));
            assertEquals(stateCount, marker.getCount(true));
        } catch (Exception efe) {
            fail(efe.getMessage());
        }
    }

    /** Transistion system used by this test. */
    private ExploreResult result;
    private LTSLabels ltsLabels;
    private Graph gtsGraph;
}
