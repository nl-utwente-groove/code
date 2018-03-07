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
package groove.test.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import groove.util.collect.TreeHashSet;

/**
 * @author Arend Rensink
 * @version $Revision$
 */
@SuppressWarnings("all")
public class TreeHashSetTest {
    static final int INT_LIST_COUNT = 1000;
    final Integer[] intList1 = new Integer[INT_LIST_COUNT];
    final Integer[] intList2 = new Integer[INT_LIST_COUNT];
    final Set<Integer>[] longList = new Set[INT_LIST_COUNT];
    {
        for (int i = 0; i < INT_LIST_COUNT; i++) {
            int value = i * i - INT_LIST_COUNT * INT_LIST_COUNT / 2;
            this.intList1[i] = value;
            this.intList2[i] = value;
            this.longList[i] = new HashSet<>();
            this.longList[i].add(value);
        }
    }

    TreeHashSet<Object> defaultSet, identitySet, hashcodeSet;

    @Before
    public void setUp() {
        this.defaultSet = new TreeHashSet<>();
        this.identitySet = new TreeHashSet<Object>(TreeHashSet.IDENTITY_EQUATOR);
        this.hashcodeSet = new TreeHashSet<Object>(TreeHashSet.HASHCODE_EQUATOR);
    }

    @Test
    public void testClone() {
        testClone(this.defaultSet);
        testClone(this.identitySet);
        testClone(this.hashcodeSet);
    }

    private void testClone(TreeHashSet<Object> original) {
        fill(original, this.intList1);
        fill(original, this.longList);
        TreeHashSet<Object> clone = new TreeHashSet<>(original);
        assertEquals(clone, original);
        Iterator<Object> iter;
        while ((iter = clone.iterator()).hasNext()) {
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
    @Test
    public void testSize() {
        // default equator
        fill(this.defaultSet, this.intList1);
        assertEquals(INT_LIST_COUNT, this.defaultSet.size());
        fill(this.defaultSet, this.intList2);
        assertEquals(INT_LIST_COUNT, this.defaultSet.size());
        fill(this.defaultSet, this.longList);
        assertEquals(2 * INT_LIST_COUNT, this.defaultSet.size());
        // identity equator
        fill(this.identitySet, this.intList1);
        assertEquals(INT_LIST_COUNT, this.identitySet.size());
        fill(this.identitySet, this.intList2);
        assertEquals(2 * INT_LIST_COUNT, this.identitySet.size());
        fill(this.identitySet, this.longList);
        assertEquals(3 * INT_LIST_COUNT, this.identitySet.size());
        // hashcode equator
        fill(this.hashcodeSet, this.intList1);
        assertEquals(INT_LIST_COUNT, this.hashcodeSet.size());
        fill(this.hashcodeSet, this.intList2);
        assertEquals(INT_LIST_COUNT, this.hashcodeSet.size());
        fill(this.hashcodeSet, this.longList);
        assertEquals(INT_LIST_COUNT, this.hashcodeSet.size());
    }

    /*
     * Test method for 'groove.util.TreeStoreSet.clear()'
     */
    @Test
    public void testClear() {
        fill(this.defaultSet, this.intList1);
        this.defaultSet.clear();
        assertEquals(0, this.defaultSet.size());
    }

    /*
     * Test method for 'groove.util.TreeStoreSet.iterator()'
     */
    @Test
    public void testIterator() {
        Set<Object> testSet = new HashSet<>();
        for (int i = 0; i < INT_LIST_COUNT; i++) {
            this.defaultSet.add(this.intList1[i]);
            testSet = new HashSet<>();
            Iterator<Object> iter = this.defaultSet.iterator();
            while (iter.hasNext()) {
                Object element = iter.next();
                testSet.add(element);
            }
            assertEquals(testSet, this.defaultSet);
        }
        assertEquals(testSet, new HashSet<Object>(Arrays.asList(this.intList1)));
    }

    /*
     * Test method for 'groove.util.TreeStoreSet.iterator()'
     */
    @Test
    public void testSortedIterator() {
        for (int i = INT_LIST_COUNT - 1; i >= 0; i--) {
            this.defaultSet.add(this.intList1[i]);
        }
        Iterator<Object> iter = this.defaultSet.sortedIterator();
        Integer first = (Integer) iter.next();
        assertNotNull(first);
        while (iter.hasNext()) {
            Integer next = (Integer) iter.next();
            assertNotNull(next);
            // assertTrue(next > first);
            first = next;
        }
    }

    /*
     * Test method for 'groove.util.TreeStoreSet.add(Object)'
     */
    @Test
    public void testAddObject() {
        for (int i = 0; i < INT_LIST_COUNT; i++) {
            // default equator
            assertTrue(this.defaultSet.add(this.intList1[i]));
            assertFalse(this.defaultSet.add(this.intList1[i]));
            assertFalse(this.defaultSet.add(this.intList2[i]));
            assertTrue(this.defaultSet.add(this.longList[i]));
            // identity equator
            assertTrue(this.identitySet.add(this.intList1[i]));
            assertFalse(this.identitySet.add(this.intList1[i]));
            assertTrue(this.identitySet.add(this.intList2[i]));
            // hashcode equator
            assertTrue(this.hashcodeSet.add(this.intList1[i]));
            assertFalse(this.hashcodeSet.add(this.intList1[i]));
            assertFalse(this.hashcodeSet.add(this.longList[i]));
        }
    }

    @Test
    public void testRemoveObject() {
        int i = 0;
        int size = INT_LIST_COUNT / 2;
        for (; i < size; i++) {
            this.defaultSet.add(this.intList1[i]);
            this.identitySet.add(this.intList1[i]);
            this.identitySet.add(this.intList2[i]);
            this.hashcodeSet.add(this.intList1[i]);
        }
        int identitySetSize = 2 * size;
        for (; i < INT_LIST_COUNT; i++) {
            // default equator
            assertFalse(this.defaultSet.remove(this.intList1[i]));
            this.defaultSet.add(this.intList1[i]);
            assertTrue(this.defaultSet.remove(this.intList1[i]));
            assertEquals(size, this.defaultSet.size());
            assertFalse(this.defaultSet.remove(this.intList1[i]));
            this.defaultSet.add(this.intList1[i]);
            assertFalse(this.defaultSet.remove(this.longList[i]));
            assertEquals(size + 1, this.defaultSet.size());
            assertTrue(this.defaultSet.remove(this.intList2[i]));
            assertEquals(size, this.defaultSet.size());
            this.defaultSet.add(this.intList1[i]);
            this.defaultSet.add(this.longList[i]);
            assertTrue(this.defaultSet.remove(this.longList[i]));
            assertFalse(this.defaultSet.remove(this.longList[i]));
            assertEquals(size + 1, this.defaultSet.size());
            assertTrue(this.defaultSet.remove(this.intList2[i]));
            assertEquals(size, this.defaultSet.size());
            // identity equator
            assertFalse(this.identitySet.remove(this.intList1[i]));
            this.identitySet.add(this.intList1[i]);
            assertTrue(this.identitySet.remove(this.intList1[i]));
            assertEquals(identitySetSize, this.identitySet.size());
            assertFalse(this.identitySet.remove(this.intList1[i]));
            this.identitySet.add(this.intList1[i]);
            assertFalse(this.identitySet.remove(this.longList[i]));
            assertEquals(identitySetSize + 1, this.identitySet.size());
            assertFalse(this.identitySet.remove(this.intList2[i]));
            assertEquals(identitySetSize + 1, this.identitySet.size());
            identitySetSize++;
            // hashcode equator
            assertFalse(this.hashcodeSet.remove(this.intList1[i]));
            this.hashcodeSet.add(this.intList1[i]);
            assertTrue(this.hashcodeSet.remove(this.intList1[i]));
            assertEquals(size, this.hashcodeSet.size());
            assertFalse(this.hashcodeSet.remove(this.intList1[i]));
            this.hashcodeSet.add(this.intList1[i]);
            assertTrue(this.hashcodeSet.remove(this.longList[i]));
            assertEquals(size, this.hashcodeSet.size());
            assertFalse(this.hashcodeSet.remove(this.intList2[i]));
            assertEquals(size, this.hashcodeSet.size());
        }
    }

    /*
     * Test method for 'groove.util.TreeStoreSet.contains(Object)'
     */
    @Test
    public void testContainsObject() {
        for (int i = 0; i < INT_LIST_COUNT; i++) {
            // default equator
            assertFalse(this.defaultSet.contains(this.intList1[i]));
            this.defaultSet.add(this.intList1[i]);
            assertTrue(this.defaultSet.contains(this.intList1[i]));
            assertTrue(this.defaultSet.contains(this.intList2[i]));
            assertFalse(this.defaultSet.contains(this.longList[i]));
            // identity equator
            assertFalse(this.identitySet.contains(this.intList1[i]));
            this.identitySet.add(this.intList1[i]);
            assertTrue(this.identitySet.contains(this.intList1[i]));
            assertFalse(this.identitySet.contains(this.intList2[i]));
            assertFalse(this.identitySet.contains(this.longList[i]));
            // hashcode equator
            assertFalse(this.hashcodeSet.contains(this.intList1[i]));
            this.hashcodeSet.add(this.intList1[i]);
            assertTrue(this.hashcodeSet.contains(this.intList1[i]));
            assertTrue(this.hashcodeSet.contains(this.intList2[i]));
            assertTrue(this.hashcodeSet.contains(this.longList[i]));
        }
    }

    /*
     * Test method for 'java.util.AbstractSet.equals(Object)'
     */
    @Test
    public void testEquals() {
        TreeHashSet<Object> clone = new TreeHashSet<>();
        fill(this.defaultSet, this.intList1);
        fill(this.defaultSet, this.longList);
        fill(clone, this.longList);
        fill(clone, this.intList2);
        assertEquals(clone, this.defaultSet);
        clone.remove(this.intList1[INT_LIST_COUNT - 1]);
        clone.add(this.longList[INT_LIST_COUNT - 1]);
        assertFalse(clone.equals(this.defaultSet));
        clone.clear();
        fill(clone, this.intList2);
        fill(clone, this.longList);
        Iterator<Integer> iter = Arrays.asList(this.intList2)
            .iterator();
        while (iter.hasNext()) {
            Object next = iter.next();
            clone.remove(next);
            assertFalse(clone.equals(this.defaultSet));
            assertFalse(this.defaultSet.equals(clone));
            this.defaultSet.remove(next);
            assertTrue(clone.equals(this.defaultSet));
            assertTrue(this.defaultSet.equals(clone));
        }
        clone.clear();
        fill(clone, this.longList);
        assertTrue(clone.equals(this.defaultSet));
        assertTrue(this.defaultSet.equals(clone));
    }

    private <T> void fill(Set<T> set, T[] list) {
        set.addAll(Arrays.asList(list));
    }
}
