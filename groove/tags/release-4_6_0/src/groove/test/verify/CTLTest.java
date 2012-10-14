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
import static org.junit.Assert.fail;
import groove.explore.Generator;
import groove.lts.GTS;
import groove.verify.DefaultMarker;
import groove.verify.Formula;
import groove.verify.FormulaParser;
import groove.verify.ParseException;

import org.junit.Test;

/**
 * Tests the CTLStarFormula class.
 * @author Harmen Kastenberg
 * @version $Revision$
 */
public class CTLTest {
    /** Transistion system used by this test. */
    private GTS gts;

    /**
     * Tests whether the circular buffer fulfils certain properties and whether
     * the number of counter examples is correct for other properties.
     */
    @Test
    public void testCircularBuffer() {
        setGTS("circular-buffer");
        testFormula("AG(put|get)", 5);
        testFormula("AX(put)", 4);
        testFormula("EX(put)", 5);
        testFormula("!put | EX(get)", 5);
        testFormula("AG(put)", 0);
    }

    /** Test on a specially designed transition system. */
    @Test
    public void testMC() {
        setGTS("mc");
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

    /** Sets the GTS to a given grammar in the JUnit samples. */
    private void setGTS(String grammarName) {
        Generator generator =
            new Generator("-v", "0", "junit/samples/" + grammarName);
        generator.start();
        this.gts = generator.getGTS();
    }

    /** Tests the number of counterexamples in the current;y
     * set GTS for a given formula. */
    private void testFormula(String formula, int stateCount) {
        try {
            // all states satisfy the following property
            Formula property = FormulaParser.parse(formula).toCtlFormula();
            DefaultMarker modelChecker = new DefaultMarker(property, this.gts);
            modelChecker.verify();
            assertEquals(stateCount, modelChecker.getCount(true));
        } catch (ParseException efe) {
            fail(efe.getMessage());
        }
    }
}
