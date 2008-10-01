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
/**
 * 
 */
package groove.test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import groove.util.TreeHashSet;
import junit.framework.TestCase;

/**
 *
 * @author Arend Rensink
 * @version $Revision$
 */
@SuppressWarnings("all")
public class TreeSetTest extends TestCase {
	static final int INT_LIST_COUNT = 1000;
	final Integer[] intList1 = new Integer[INT_LIST_COUNT];
	final Integer[] intList2 = new Integer[INT_LIST_COUNT];
	final Set<Integer>[] longList = new Set[INT_LIST_COUNT];
	{
		for (int i = 0; i < INT_LIST_COUNT; i++) {
			int value = i*i - INT_LIST_COUNT * INT_LIST_COUNT/2;
			intList1[i] = new Integer(value); 
			intList2[i] = new Integer(value); 
			longList[i] = new HashSet<Integer>();
			longList[i].add(new Integer(value));
		}
	}
	
	TreeHashSet<Object> defaultSet, identitySet, hashcodeSet;
	
    @Override
	protected void setUp() throws Exception {
		defaultSet = new TreeHashSet<Object>();
		identitySet = new TreeHashSet<Object>(TreeHashSet.IDENTITY_EQUATOR);
		hashcodeSet = new TreeHashSet<Object>(TreeHashSet.HASHCODE_EQUATOR);
	}

	public void testClone() {
		testClone(defaultSet);
		testClone(identitySet);
		testClone(hashcodeSet);
	}
	
	private void testClone(TreeHashSet<Object> original) {
		fill(original, intList1);
		fill(original, longList);
		TreeHashSet<Object> clone = new TreeHashSet<Object>(original);
		assertEquals(clone, original);
		Iterator<Object> iter;
		while ((iter= clone.iterator()).hasNext()) {
			Object next = iter.next();
			clone.remove(next);
			assertFalse(clone.equals(original));
			original.remove(next);
			assertTrue(clone.equals(original));
		}		
	}
	
	/*
	 * Test method for 'groove.util.TreeStoreSet.size()'
	 */
	public void testSize() {
		// default equator
		fill(defaultSet, intList1);
		assertEquals(INT_LIST_COUNT, defaultSet.size());
		fill(defaultSet, intList2);
		assertEquals(INT_LIST_COUNT, defaultSet.size());
		fill(defaultSet, longList);
		assertEquals(2*INT_LIST_COUNT, defaultSet.size());
		// identity equator
		fill(identitySet, intList1);
		assertEquals(INT_LIST_COUNT, identitySet.size());
		fill(identitySet, intList2);
		assertEquals(2*INT_LIST_COUNT, identitySet.size());
		fill(identitySet, longList);
		assertEquals(3*INT_LIST_COUNT, identitySet.size());
		// hashcode equator
		fill(hashcodeSet, intList1);
		assertEquals(INT_LIST_COUNT, hashcodeSet.size());
		fill(hashcodeSet, intList2);
		assertEquals(INT_LIST_COUNT, hashcodeSet.size());
		fill(hashcodeSet, longList);
		assertEquals(INT_LIST_COUNT, hashcodeSet.size());
	}

	/*
	 * Test method for 'groove.util.TreeStoreSet.clear()'
	 */
	public void testClear() {
		fill(defaultSet, intList1);
		defaultSet.clear();
		assertEquals(0, defaultSet.size());
	}

	/*
	 * Test method for 'groove.util.TreeStoreSet.iterator()'
	 */
	public void testIterator() {
		Set<Object> testSet = new HashSet<Object>();
		for (int i = 0; i < INT_LIST_COUNT; i++) {
			defaultSet.add(intList1[i]);
			testSet = new HashSet<Object>();
			Iterator<Object> iter = defaultSet.iterator();
			while (iter.hasNext()) {
				Object element = iter.next();
				testSet.add(element);
			}
			assertEquals(testSet, defaultSet);
		}
		assertEquals(testSet, new HashSet<Object>(Arrays.asList(intList1)));
	}

	/*
	 * Test method for 'groove.util.TreeStoreSet.add(Object)'
	 */
	public void testAddObject() {
		for (int i = 0; i < INT_LIST_COUNT; i++) {
			// default equator
			assertTrue(defaultSet.add(intList1[i]));
			assertFalse(defaultSet.add(intList1[i]));
			assertFalse(defaultSet.add(intList2[i]));
			assertTrue(defaultSet.add(longList[i]));
			// identity equator
			assertTrue(identitySet.add(intList1[i]));
			assertFalse(identitySet.add(intList1[i]));
			assertTrue(identitySet.add(intList2[i]));
			// hashcode equator
			assertTrue(hashcodeSet.add(intList1[i]));
			assertFalse(hashcodeSet.add(intList1[i]));
			assertFalse(hashcodeSet.add(longList[i]));
		}
	}

	public void testRemoveObject() {
		int i = 0;
		int size = INT_LIST_COUNT / 2;
		for (; i < size; i++) {
			defaultSet.add(intList1[i]);
			identitySet.add(intList1[i]);
			identitySet.add(intList2[i]);
			hashcodeSet.add(intList1[i]);
		}
		int identitySetSize = 2*size;
		for (; i < INT_LIST_COUNT; i++) {
			// default equator
			assertFalse(defaultSet.remove(intList1[i]));
			defaultSet.add(intList1[i]);
			assertTrue(defaultSet.remove(intList1[i]));
			assertEquals(size, defaultSet.size());
			assertFalse(defaultSet.remove(intList1[i]));
			defaultSet.add(intList1[i]);
			assertFalse(defaultSet.remove(longList[i]));
			assertEquals(size+1, defaultSet.size());
			assertTrue(defaultSet.remove(intList2[i]));
			assertEquals(size, defaultSet.size());
			defaultSet.add(intList1[i]);
			defaultSet.add(longList[i]);
			assertTrue(defaultSet.remove(longList[i]));
			assertFalse(defaultSet.remove(longList[i]));
			assertEquals(size+1, defaultSet.size());
			assertTrue(defaultSet.remove(intList2[i]));
			assertEquals(size, defaultSet.size());
			// identity equator
			assertFalse(identitySet.remove(intList1[i]));
			identitySet.add(intList1[i]);
			assertTrue(identitySet.remove(intList1[i]));
			assertEquals(identitySetSize, identitySet.size());
			assertFalse(identitySet.remove(intList1[i]));
			identitySet.add(intList1[i]);
			assertFalse(identitySet.remove(longList[i]));
			assertEquals(identitySetSize+1, identitySet.size());
			assertFalse(identitySet.remove(intList2[i]));
			assertEquals(identitySetSize+1, identitySet.size());
			identitySetSize++;
			// hashcode equator
			assertFalse(hashcodeSet.remove(intList1[i]));
			hashcodeSet.add(intList1[i]);
			assertTrue(hashcodeSet.remove(intList1[i]));
			assertEquals(size, hashcodeSet.size());
			assertFalse(hashcodeSet.remove(intList1[i]));
			hashcodeSet.add(intList1[i]);
			assertTrue(hashcodeSet.remove(longList[i]));
			assertEquals(size, hashcodeSet.size());
			assertFalse(hashcodeSet.remove(intList2[i]));
			assertEquals(size, hashcodeSet.size());
		}
	}
	
	/*
	 * Test method for 'groove.util.TreeStoreSet.contains(Object)'
	 */
	public void testContainsObject() {
		for (int i = 0; i < INT_LIST_COUNT; i++) {
			// default equator
			assertFalse(defaultSet.contains(intList1[i]));
			defaultSet.add(intList1[i]);
			assertTrue(defaultSet.contains(intList1[i]));
			assertTrue(defaultSet.contains(intList2[i]));
			assertFalse(defaultSet.contains(longList[i]));
			// identity equator
			assertFalse(identitySet.contains(intList1[i]));
			identitySet.add(intList1[i]);
			assertTrue(identitySet.contains(intList1[i]));
			assertFalse(identitySet.contains(intList2[i]));
			assertFalse(identitySet.contains(longList[i]));
			// hashcode equator
			assertFalse(hashcodeSet.contains(intList1[i]));
			hashcodeSet.add(intList1[i]);
			assertTrue(hashcodeSet.contains(intList1[i]));
			assertTrue(hashcodeSet.contains(intList2[i]));
			assertTrue(hashcodeSet.contains(longList[i]));
		}
	}

	/*
	 * Test method for 'java.util.AbstractSet.equals(Object)'
	 */
	public void testEquals() {
		TreeHashSet<Object> clone = new TreeHashSet<Object>();
		fill(defaultSet, intList1);
		fill(defaultSet, longList);
		fill(clone, longList);
		fill(clone, intList2);
		assertEquals(clone, defaultSet);
		clone.remove(intList1[INT_LIST_COUNT-1]);
		clone.add(longList[INT_LIST_COUNT-1]);
		assertFalse(clone.equals(defaultSet));
		clone.clear();
		fill(clone, intList2);
		fill(clone, longList);
		Iterator<Integer> iter = Arrays.asList(intList2).iterator();
		while (iter.hasNext()) {
			Object next = iter.next();
			clone.remove(next);
			assertFalse(clone.equals(defaultSet));
			assertFalse(defaultSet.equals(clone));
			defaultSet.remove(next);
			assertTrue(clone.equals(defaultSet));
			assertTrue(defaultSet.equals(clone));
		}
		clone.clear();
		fill(clone, longList);
		assertTrue(clone.equals(defaultSet));
		assertTrue(defaultSet.equals(clone));
	}
	
	private <T> void fill(Set<T> set, T[] list) {
		set.addAll(Arrays.asList(list));
	}
}
