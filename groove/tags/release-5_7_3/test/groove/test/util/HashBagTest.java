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
 * $Id$
 */
package groove.test.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import groove.util.collect.HashBag;

/**
 * @author Arend Rensink
 * @version $Revision$
 */
@SuppressWarnings("all")
public class HashBagTest {

    @Before
    public void setUp() {
        this.bag122 = new HashBag<>();
        this.bag122.add(i1);
        this.bag122.add(i2);
        this.bag122.add(i2);
        this.bag233444 = new HashBag<>();
        this.bag233444.add(i2);
        this.bag233444.add(i3);
        this.bag233444.add(i3);
        this.bag233444.add(i4);
        this.bag233444.add(i4);
        this.bag233444.add(i4);
    }

    @Test
    public void testSize() {
        assertEquals(3, this.bag122.size());
        this.bag122.remove(i3); // [1,2,2]
        assertEquals(3, this.bag122.size());
        this.bag122.remove(i1); // [2,2]
        assertEquals(2, this.bag122.size());
        this.bag122.remove(i1); // [2,2]
        assertEquals(2, this.bag122.size());
        this.bag122.remove(i2); // [2]
        assertEquals(1, this.bag122.size());
        this.bag122.remove(i2); // []
        assertEquals(0, this.bag122.size());
        assertEquals(6, this.bag233444.size());
    }

    @Test
    public void testClear() {
        this.bag122.clear();
        assertTrue(this.bag122.isEmpty());
        this.bag233444.clear();
        assertTrue(this.bag233444.isEmpty());
    }

    @Test
    public void testClone() {
        @SuppressWarnings("unchecked") HashBag<Integer> bag122Clone =
            (HashBag<Integer>) this.bag122.clone();
        assertEquals(1, bag122Clone.multiplicity(i1));
        assertEquals(2, bag122Clone.multiplicity(i2));
        assertEquals(0, bag122Clone.multiplicity(i3));
        bag122Clone.remove(2);
        assertEquals(1, bag122Clone.multiplicity(i2));
        assertEquals(2, this.bag122.multiplicity(i2));
        this.bag122.remove(2);
        assertEquals(1, bag122Clone.multiplicity(i2));
        assertEquals(1, this.bag122.multiplicity(i2));
    }

    @Test
    public void testContainsObject() {
        assertTrue(this.bag122.contains(1));
        assertTrue(!this.bag122.contains(3));
        this.bag122.remove(2);
        assertTrue(this.bag122.contains(2));
        this.bag122.remove(2);
        assertTrue(!this.bag122.contains(2));
    }

    /*
     * Test for Iterator iterator()
     */
    @Test
    public void testIterator() {
        Set<Integer> set122 = new HashSet<>();
        Iterator<Integer> iter = this.bag122.iterator();
        while (iter.hasNext()) {
            set122.add(iter.next());
        }
        assertEquals(this.bag122.elementSet(), set122);
        iter = this.bag122.iterator();
        while (iter.hasNext()) {
            Object next = iter.next();
            if (next.equals(i1)) {
                iter.remove();
            }
        }
        assertEquals(2, this.bag122.multiplicity(i2));
        HashBag<Integer> bag22 = new HashBag<>();
        bag22.add(2);
        bag22.add(i2);
        assertEquals(bag22, this.bag122);
        iter = this.bag122.iterator();
        boolean removed2 = false;
        while (!removed2 && iter.hasNext()) {
            Object next = iter.next();
            if (next.equals(i2)) {
                iter.remove();
                removed2 = true;
            }
        }
        bag22.remove(i2);
        assertEquals(bag22, this.bag122);
    }

    private HashBag<Integer> bag122, bag233444;
    private static final Integer i1 = 1;
    private static final Integer i2 = 2;
    private static final Integer i3 = 3;
    private static final Integer i4 = 4;
    private static final Integer i5 = 5;
    private static final Integer i6 = 6;
}
