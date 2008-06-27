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
 * $Id: TestingMultiplicities.java,v 1.1 2007-11-28 15:35:03 iovka Exp $
 */
package groove.test.abs;

import groove.abs.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import groove.abs.Abstraction.MultInfoRelation;

import junit.framework.TestCase;

/** Tests functions on multiplicities 
 * 
 * @author Iovka Boneva
 * @version $Revision $
 */
public class TestingMultiplicities extends TestCase {

	// do not change
	private static final int precision = 2;
	private static final Multiplicity M = Abstraction.MULTIPLICITY;  //a shortcut
	
	// the values among which to test
	private static final Set<MultiplicityInformation> zero, one, two, omega, omega_two, omega_two_one, omega_two_one_zero;
	private static final ArrayList<Set<MultiplicityInformation>> all;
	static {
		zero = new HashSet<MultiplicityInformation>(1);
		one =  new HashSet<MultiplicityInformation>(1);
		two = new HashSet<MultiplicityInformation>(1);
		omega = new HashSet<MultiplicityInformation>(1);
		omega_two =  new HashSet<MultiplicityInformation>(2);
		omega_two_one = new HashSet<MultiplicityInformation>(3);
		omega_two_one_zero = new HashSet<MultiplicityInformation>(4);
		zero.add(M.getElement(0, precision));
		one.add(M.getElement(1, precision));
		two.add(M.getElement(2, precision));
		omega.add(M.getElement(3, precision));
		omega_two.addAll(omega); omega_two.addAll(two);
		omega_two_one.addAll(omega_two); omega_two_one.addAll(one);
		omega_two_one_zero.addAll(omega_two_one); omega_two_one_zero.addAll(zero);
		all = new ArrayList<Set<MultiplicityInformation>>(7);
		all.add(zero); all.add(one); all.add(two); all.add(omega);
		all.add(omega_two); all.add(omega_two_one); all.add(omega_two_one_zero);
	}
	/** */
	public void testOperations () throws ExceptionRemovalImpossible,  AssertionError {
		MultiplicityInformation[] T = new MultiplicityInformation[4];
		for (int i = 0; i < T.length; i++) {
			T[i] = M.getElement(i, precision);
		}
		
		// test correctness of the initialisation
		assertEquals(arrayToSet(M.getPreciseElements(T[0])), zero);
		assertEquals(arrayToSet(M.getPreciseElements(T[1])), one);
		assertEquals(arrayToSet(M.getPreciseElements(T[2])), two);
		assertEquals(arrayToSet(M.getPreciseElements(T[3])), omega);
	
		// test the add operation
		assertEquals(multToSet(M.add(T[0], 1)), one);
		assertEquals(multToSet(M.add(T[0], 2)), two);
		assertEquals(multToSet(M.add(T[0], 3)), omega);
		
		assertEquals(multToSet(M.add(T[1], 1)), two);
		assertEquals(multToSet(M.add(T[1], 2)), omega);
		
		assertEquals(multToSet(M.add(T[2], 1)), omega);
		
		// test the remove operation
		MultiplicityInformation omega_minus_one = M.remove(T[3], 1);
		assertEquals(multToSet(omega_minus_one), omega_two);
		
		MultiplicityInformation omega_minus_two = M.remove(T[3], 2);
		assertEquals(multToSet(omega_minus_two), omega_two_one);
		
		MultiplicityInformation two_minus_one = M.remove(T[2], 1);
		assertEquals(multToSet(two_minus_one), one);
		
		MultiplicityInformation zero_minus_zero = M.remove(T[0], 0);
		assertEquals(multToSet(zero_minus_zero), zero);
		
		// test the add operation
		assertEquals(multToSet(M.add(omega_minus_two, 1)), omega_two);
		assertEquals(multToSet(M.add(omega_minus_one, 1)), omega);
			
		// test exception for impossible removal
		try {
			M.remove(T[1], 2);
			assertTrue(false);
		} catch (ExceptionRemovalImpossible e) { /** */ }
		
		try {
			M.remove(T[0], 1);
			assertTrue(false);
		} catch (ExceptionRemovalImpossible e) { /** */ }

		// test canRemove
		assertTrue(M.canRemove(omega_minus_two, 3));
		assertTrue(M.canRemove(T[3], 5));
		assertTrue(M.canRemove(T[2], 2));
		assertFalse(M.canRemove(T[1], 3));
		
		// the other operations
		
		assertEquals(M.getPrecision(T[0]), precision);
		
		assertTrue(M.isZero(T[0]));
		assertFalse(M.isZero(T[2]));
		assertFalse(M.isZero(omega_minus_two));
		
		assertTrue(M.containsOmega(omega_minus_one));
		assertTrue(M.containsOmega(T[3]));
		assertFalse(M.containsOmega(T[1]));
		assertFalse(M.containsOmega(T[0]));
		
		assertEquals(M.preciseCard(T[3]), -1);
		assertEquals(M.preciseCard(T[1]), 1);
		assertEquals(M.preciseCard(T[0]), 0);	
	}
	/** */
	public void testCompare() throws ExceptionRemovalImpossible, AssertionError{
		MultiplicityInformation[] T = new MultiplicityInformation[4];
		for (int i = 0; i < T.length; i++) {
			T[i] = M.getElement(i, precision);
		}
		
		MultiplicityInformation zero_one_two_omega = M.remove(T[3], 3);
		MultiplicityInformation one_two_omega = M.remove(T[3], 2);
		MultiplicityInformation two_omega = M.remove(T[3], 1);
		
		for (int i = 0; i < T.length; i++) {
			for (int j = 0; j < T.length; j++) {
				if (i == j) {
					assertEquals(M.compare(T[i], T[j]), MultInfoRelation.M_EQUAL);
				} else {
					assertEquals(M.compare(T[i], T[j]), MultInfoRelation.M_NOTEQUAL);
				}

			}
		}
		
		for (int i = 0; i < T.length-1; i++) {
			assertEquals(M.compare(T[i], zero_one_two_omega), MultInfoRelation.M_BELONGS);
			assertEquals(M.compare(zero_one_two_omega, T[i]), MultInfoRelation.M_CONTAINS);
		}
		assertEquals(M.compare(T[3], zero_one_two_omega), MultInfoRelation.M_SUBSET);
		
		assertEquals(M.compare(one_two_omega, zero_one_two_omega), MultInfoRelation.M_SUBSET);
		
		assertEquals(M.compare(zero_one_two_omega, one_two_omega), MultInfoRelation.M_SUPERSET);
		
		assertEquals(M.compare(two_omega, M.remove(T[3], 1)), MultInfoRelation.M_EQUAL);
	}
	
	/** */
	public void testPrecision0 () throws ExceptionRemovalImpossible {
		int P = 0;
		
		MultiplicityInformation[] mult = new MultiplicityInformation[3];
		mult[0] = M.getElement(0, P); 
		mult[1] = M.getElement(1, P); 
		mult[2] = M.remove(mult[1], 3);

		assertTrue(M.canRemove(mult[2], 5));
		
		assertEquals(mult[1], M.add(mult[2], 1));
	}
	
	private Set<MultiplicityInformation> arrayToSet(MultiplicityInformation[] array) {
		Set<MultiplicityInformation> result = new HashSet<MultiplicityInformation>(4);
		for (MultiplicityInformation m : array) {
			result.add(m);
		}
		return result;
	}
	
	private Set<MultiplicityInformation> multToSet(MultiplicityInformation mult) {
		return arrayToSet(M.getPreciseElements(mult));
	}
	
	
	
	
}
