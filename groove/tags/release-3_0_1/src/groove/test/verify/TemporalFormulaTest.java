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
 * $Id: TemporalFormulaTest.java,v 1.5 2008-01-30 09:33:53 iovka Exp $
 */

package groove.test.verify;

import groove.verify.CTLFormula;
import groove.verify.CTLStarFormula;
import groove.verify.LTLFormula;
import groove.verify.TemporalFormula;
import groove.view.FormatException;
import junit.framework.TestCase;

/**
 * Tests the CTLStarFormula class.
 * @author Harmen Kastenberg
 * @version $Revision: 1.5 $
 */
public class TemporalFormulaTest extends TestCase {
    @Override
	protected void setUp() throws Exception {
		//
    }

	public void testCTLFormulaCreation() {
		try {
			TemporalFormula formula;

			// CTL* formulae
			formula = CTLStarFormula.parseFormula("empty");
			formula = CTLStarFormula.parseFormula("(empty | non-empty)");
			formula = CTLStarFormula.parseFormula("F(empty & final)");
			formula = CTLStarFormula.parseFormula("G(get U empty)");

			formula = CTLStarFormula.parseFormula("AX(empty)");
			assertEquals("A(X(empty))", formula.toString());
			formula = CTLStarFormula.parseFormula("EX(empty)");
			assertEquals("E(X(empty))", formula.toString());
			formula = CTLStarFormula.parseFormula("A(non-empty U empty)");
			assertEquals("A(non-empty U empty)", formula.toString());
			formula = CTLStarFormula.parseFormula("E(non-empty U empty)");
			assertEquals("E(non-empty U empty)", formula.toString());
			formula = CTLStarFormula.parseFormula("AF(empty)");
			assertEquals("A(F(empty))", formula.toString());
			formula = CTLStarFormula.parseFormula("EF(empty)");
			assertEquals("E(F(empty))", formula.toString());
			formula = CTLStarFormula.parseFormula("AG(empty)");
			assertEquals("A(G(empty))", formula.toString());
			formula = CTLStarFormula.parseFormula("EG(empty)");
			assertEquals("E(G(empty))", formula.toString());
			formula = CTLStarFormula.parseFormula("X(empty)");
			assertEquals("X(empty)", formula.toString());
			formula = CTLStarFormula.parseFormula("G(F(empty))");
			assertEquals("G(F(empty))", formula.toString());
			formula = CTLStarFormula.parseFormula("GX(empty)");
			assertEquals("G(X(empty))", formula.toString());

			// CTL formulae
			formula = CTLFormula.parseFormula("EG(empty)");
			formula = CTLFormula.parseFormula("EG(AF(empty))");
			formula = CTLFormula.parseFormula("EX(AX(empty | full))");
			formula = CTLFormula.parseFormula("A(get U (empty | full))");

			// HARMEN: make sure that the following formula cannot be parsed as a ctl formula
			// i.e. in CTL, every temporal operator should be bounded by a path quantifier
			formula = CTLFormula.parseFormula("G(empty)");

			// LTL formulae
			formula = LTLFormula.parseFormula("G(empty | full)");
			formula = LTLFormula.parseFormula("F(full | error)");
			formula = LTLFormula.parseFormula("F(error & !(full))");
		} catch (FormatException efe) {
			efe.printStackTrace();
		}
	}
}
