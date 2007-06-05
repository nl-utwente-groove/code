/*
 * GROOVE: GRaphs for Object Oriented VErification
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
 * $Id: ModelCheckingTest.java,v 1.7 2007-04-29 09:22:28 rensink Exp $
 */

package groove.test.verify;

import groove.lts.GTS;
import groove.util.Generator;
import groove.verify.CTLFormula;
import groove.verify.CTLModelChecker;
import groove.verify.TemporalFormula;
import groove.view.FormatException;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

/**
 * Tests the CTLStarFormula class.
 * @author Harmen Kastenberg
 * @version $Revision: 1.7 $
 */
public class ModelCheckingTest extends TestCase {
    @Override
	protected void setUp() throws Exception {
		//
    }

	/**
	 * Tests whether the circular buffer fulfills certain properties
	 * and whether the number of counter examples is correct for other
	 * properties.
	 */
	public void testCircularBuffer() {
		try {
			List<String> list = new ArrayList<String>();
			list.add("junit/samples/circular-buffer.gps");
			Generator generator = new Generator(list);
			generator.start();

			// all states satisfy the following property
			TemporalFormula property = CTLFormula.parseFormula("AG(put|get)");
			GTS gts = generator.getGTS();
			CTLModelChecker modelChecker = new CTLModelChecker(gts, property);
			modelChecker.verify();
			assertEquals(0, property.getCounterExamples().size());

			// there is one state that does not satisfy the following property
			property = CTLFormula.parseFormula("AX(put)");
			modelChecker = new CTLModelChecker(gts, property);
			modelChecker.verify();
			assertEquals(1,property.getCounterExamples().size());

			// all states satisfy the following property
			property = CTLFormula.parseFormula("EX(put)");
			modelChecker = new CTLModelChecker(gts, property);
			modelChecker.verify();
			assertEquals(0,property.getCounterExamples().size());

			// not a single state satisfies the following property
			property = CTLFormula.parseFormula("AG(put)");
			modelChecker = new CTLModelChecker(gts, property);
			modelChecker.verify();
			assertEquals(gts.nodeCount(),property.getCounterExamples().size());
		} catch (FormatException efe) {
			efe.printStackTrace();
		}
	}
}
