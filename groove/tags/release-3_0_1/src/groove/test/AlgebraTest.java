// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/*
 * Created on 2005/11/01 at 10:34:32
 * Author: Harmen Kastenberg
 * Revision: $(revision 1.1)
 */
package groove.test;

import groove.algebra.Algebra;
import groove.algebra.Constant;
import groove.algebra.DefaultBooleanAlgebra;
import groove.algebra.Operation;
import groove.algebra.UnknownSymbolException;
import groove.graph.algebra.AlgebraConstants;
import groove.graph.algebra.AlgebraGraph;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

public class AlgebraTest extends TestCase {
	
    AlgebraGraph algebraGraph;
	Algebra bAlgebra, iAlgebra, sAlgebra;
	Operation bOrOper, bAndOper;
	Constant bFalseConst, bTrueConst, iOneConst;
	Operation aOperInst, bOperInst, cOperInst;
	
    @Override
	public void setUp() {
	    algebraGraph = AlgebraGraph.getInstance();
		bAlgebra = algebraGraph.getAlgebra(AlgebraConstants.BOOLEAN);
		iAlgebra = algebraGraph.getAlgebra(AlgebraConstants.INTEGER);
		sAlgebra = algebraGraph.getAlgebra(AlgebraConstants.STRING);

		try {
			bAndOper = bAlgebra.getOperation(DefaultBooleanAlgebra.AND_SYMBOL);
			bOrOper = bAlgebra.getOperation(DefaultBooleanAlgebra.OR_SYMBOL);
			bTrueConst = (Constant) bAlgebra.getOperation(DefaultBooleanAlgebra.TRUE);
			bFalseConst = (Constant) bAlgebra.getOperation(DefaultBooleanAlgebra.FALSE);
		}
		catch (UnknownSymbolException use) {
			System.err.println(use.toString());
		}
	}

	final public void testIsValidOpration() {
		assertTrue(bAlgebra.isValidOperation(bFalseConst));
//		bOper = new Operation("TRUE", 0);
//		bOper.setAlgebra(bAlgebra);
//		assertTrue(bAlgebra.isValidOperation(bOper));
	}

	final public void testGetOperations() {

		// test for method getOperations()
		HashSet<Operation> testSet = new HashSet<Operation>();
		testSet.add(bTrueConst);
		testSet.add(bFalseConst);
		testSet.add(bAndOper);
		testSet.add(bOrOper);

		Set<Operation> operations = bAlgebra.getOperations();
		for (Operation testOper: testSet) {
			assertTrue(operations.contains(testOper));
		}

		// tests for method getOperations(int)
		operations = bAlgebra.getOperations(2);
		// this set may only contain operations of arity 2
		// this includes the following operations
		assertFalse(operations.contains(bTrueConst));
		assertFalse(operations.contains(bFalseConst));

		// this excludes the following operations
		assertTrue(operations.contains(bAndOper));
		assertTrue(operations.contains(bOrOper));
	}

	final public void testEquals() {
		assertNotSame(bAlgebra, iAlgebra);
		assertNotSame(bAlgebra, sAlgebra);
		assertNotSame(iAlgebra, sAlgebra);
	}
}