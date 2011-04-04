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
 * $Id: ModelCheckingTest.java,v 1.8 2008-01-30 09:33:53 iovka Exp $
 */

package groove.test.verify;

import static org.junit.Assert.assertEquals;
import groove.explore.Generator;
import groove.explore.ModelCheckingScenario;
import groove.explore.strategy.ModelCheckingStrategy;
import groove.explore.strategy.NestedDFSStrategy;
import groove.lts.GTS;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * Tests the CTLStarFormula class.
 * @author Harmen Kastenberg
 * @version $Revision: 3219 $
 */
public class LTLTest {
    /** Transistion system used by this test. */
    private GTS gts;
    private ModelCheckingScenario scenario;

    /**
     * Tests whether the circular buffer fulfils certain properties and whether
     * the number of counter examples is correct for other properties.
     */
    @Test
    public void testCircularBuffer() {
        prepare("circular-buffer");
    }

    /** Test on a specially designed transition system. */
    @Test
    public void testMC() {
        prepare("mc");
        testFormula("p U r", false);
        testFormula("p W r", true);
        testFormula("GF q", true);
        testFormula("FG p", false);
        testFormula("!FG p", false);
        //testFormula("(p|r) M q", true);
        testFormula("p V r", false);
        // testFormula("(p|q) V r", true);
        testFormula("G p", false);
        testFormula("G(p|q)", true);
        testFormula("G(p|X(q|X q))", true);
        testFormula("X q", true);
    }

    /** Sets the GTS to a given grammar in the JUnit samples. */
    private void prepare(String grammarName) {
        List<String> list = new ArrayList<String>();
        list.add("-v");
        list.add("0");
        list.add("junit/samples/" + grammarName);
        Generator generator = new Generator(list);
        generator.start();
        this.gts = generator.getGTS();
        ModelCheckingStrategy strategy = new NestedDFSStrategy();
        this.scenario = new ModelCheckingScenario(strategy, null, null);
    }

    /** Tests the number of counterexamples in the current;y
     * set GTS for a given formula. */
    private void testFormula(String formula, boolean succeed) {
        // all states satisfy the following property
        this.scenario.setProperty(formula);
        this.scenario.prepare(this.gts);
        this.scenario.play();
        assertEquals(succeed, this.scenario.getResult().getValue().isEmpty());
    }
}
