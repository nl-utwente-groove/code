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
import groove.lts.GTS;
import groove.verify.CTLModelChecker;
import groove.verify.Formula;
import groove.verify.FormulaParser;
import groove.verify.ParseException;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * Tests the CTLStarFormula class.
 * @author Harmen Kastenberg
 * @version $Revision$
 */
public class ModelCheckingTest {

    /**
     * Tests whether the circular buffer fulfils certain properties and whether
     * the number of counter examples is correct for other properties.
     */
    @Test
    public void testCircularBuffer() {
        try {
            List<String> list = new ArrayList<String>();
            list.add("junit/samples/circular-buffer.gps");
            Generator generator = new Generator(list);
            generator.start();

            // all states satisfy the following property
            Formula property = FormulaParser.parse("AG(put|get)");
            GTS gts = generator.getGTS();
            CTLModelChecker modelChecker = new CTLModelChecker(gts, property);
            modelChecker.verify();
            assertEquals(0, property.getCounterExamples().size());

            // there is one state that does not satisfy the following property
            property = FormulaParser.parse("AX(put)");
            modelChecker = new CTLModelChecker(gts, property);
            modelChecker.verify();
            assertEquals(1, property.getCounterExamples().size());

            // all states satisfy the following property
            property = FormulaParser.parse("EX(put)");
            modelChecker = new CTLModelChecker(gts, property);
            modelChecker.verify();
            assertEquals(0, property.getCounterExamples().size());

            // all states satisfy the following property
            property = FormulaParser.parse("!put | EX(get)");
            modelChecker = new CTLModelChecker(gts, property);
            modelChecker.verify();
            assertEquals(0, property.getCounterExamples().size());

            // not a single state satisfies the following property
            property = FormulaParser.parse("AG(put)");
            modelChecker = new CTLModelChecker(gts, property);
            modelChecker.verify();
            assertEquals(gts.nodeCount(), property.getCounterExamples().size());
        } catch (ParseException efe) {
            efe.printStackTrace();
        }
    }
}
