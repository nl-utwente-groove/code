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
import groove.explore.AcceptorValue;
import groove.explore.Exploration;
import groove.explore.Generator;
import groove.explore.StrategyValue;
import groove.explore.encode.Serialized;
import groove.lts.GTS;
import groove.view.FormatException;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Tests the CTLStarFormula class.
 * @author Harmen Kastenberg
 * @version $Revision: 3219 $
 */
public class LTLTest {
    /** Transistion system used by this test. */
    private GTS gts;

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
        testFormula("q M (p|r)", true);
        testFormula("p R r", false);
        //        testFormula("r R (p|q)", true);
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
    }

    /** Tests the number of counterexamples in the current;y
     * set GTS for a given formula. */
    private void testFormula(String formula, boolean succeed) {
        Serialized strategy =
            StrategyValue.LTL.getTemplate().toSerialized(formula);
        Exploration exploration =
            new Exploration(strategy, AcceptorValue.CYCLE.toSerialized(), 1);
        try {
            exploration.play(this.gts, this.gts.startState());
        } catch (FormatException e) {
            Assert.fail();
        }
        assertEquals(succeed, exploration.getLastResult().getValue().isEmpty());
    }
}
