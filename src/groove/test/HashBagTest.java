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
 * $Id: HashBagTest.java,v 1.3 2008-01-30 09:33:08 iovka Exp $
 */
package groove.test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import groove.util.HashBag;
import junit.framework.TestCase;

/**
 * @author Arend Rensink
 * @version $Revision$
 */
@SuppressWarnings("all")
public class HashBagTest extends TestCase {

    /**
     * Constructor for HashBagTest.
     * @param arg0
     */
    public HashBagTest(String arg0) {
        super(arg0);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        bag122 = new HashBag<Integer>();
        bag122.add(i1);
        bag122.add(i2);
        bag122.add(i2);
        bag233444 = new HashBag<Integer>();
        bag233444.add(i2);
        bag233444.add(i3);
        bag233444.add(i3);
        bag233444.add(i4);
        bag233444.add(i4);
        bag233444.add(i4);
    }

    public void testSize() {
        assertEquals(3,bag122.size());
        bag122.remove(i3); // [1,2,2]
        assertEquals(3,bag122.size());
        bag122.remove(i1); // [2,2]
        assertEquals(2,bag122.size());
        bag122.remove(i1); // [2,2]
        assertEquals(2,bag122.size());
        bag122.remove(i2); // [2]
        assertEquals(1,bag122.size());
        bag122.remove(i2); // []
        assertEquals(0,bag122.size());
        assertEquals(6,bag233444.size());
    }

    public void testClear() {
        bag122.clear();
        assertTrue(bag122.isEmpty());
        bag233444.clear();
        assertTrue(bag233444.isEmpty());
    }

    public void testClone() {
    	@SuppressWarnings("unchecked")
        HashBag<Integer> bag122Clone = (HashBag<Integer>) bag122.clone();
        assertEquals(1,bag122Clone.multiplicity(i1));        
        assertEquals(2,bag122Clone.multiplicity(i2));        
        assertEquals(0,bag122Clone.multiplicity(i3)); 
        bag122Clone.remove(new Integer(2));
        assertEquals(1,bag122Clone.multiplicity(i2));        
        assertEquals(2,bag122.multiplicity(i2));        
        bag122.remove(new Integer(2));
        assertEquals(1,bag122Clone.multiplicity(i2));        
        assertEquals(1,bag122.multiplicity(i2));        
    }
    
    public void testContainsObject() {
        assertTrue(bag122.contains(new Integer(1)));
        assertTrue(!bag122.contains(new Integer(3)));
        bag122.remove(new Integer(2));
        assertTrue(bag122.contains(new Integer(2)));
        bag122.remove(new Integer(2));
        assertTrue(!bag122.contains(new Integer(2)));
    }

    /*
     * Test for Iterator iterator()
     */
    public void testIterator() {
        Set<Integer> set122 = new HashSet<Integer>();
        Iterator<Integer> iter = bag122.iterator();
        while (iter.hasNext()) {
            set122.add(iter.next());
        }
        assertEquals(bag122.elementSet(),set122);
        iter = bag122.iterator();
        while (iter.hasNext()) {
            Object next = iter.next();
            if (next.equals(i1)) {
                iter.remove();
            }
        }
        assertEquals(2, bag122.multiplicity(i2));
        HashBag<Integer> bag22 = new HashBag<Integer>();
        bag22.add(new Integer(2));
        bag22.add(i2);
        assertEquals(bag22,bag122);
        iter = bag122.iterator();
        boolean removed2 = false;
        while (!removed2 && iter.hasNext()) {
            Object next = iter.next();
            if (next.equals(i2)) {
                iter.remove();
                removed2 = true;
            }
        }
        bag22.remove(i2);
        assertEquals(bag22,bag122);
    }

    /*
     * Test for boolean add(Object)
     */
    public void testAddObject() {
        // TODO add test
    }

    /*
     * Test for boolean remove(Object)
     */
    public void testRemoveObject() {
        // TODO add test
    }

    public void testRemoveWasLast() {
        // TODO add test
    }

    public void testElementSet() {
        // TODO add test
    }

    public void testMultiplicityMap() {
        // TODO add test
    }

    public void testMultiplicity() {
        // TODO add test
    }

    public void testNewMultiplicity() {
        // TODO add test
    }

    protected HashBag<Integer> bag122, bag233444;
    protected Integer i1 = new Integer(1);
    protected Integer i2 = new Integer(2);
    protected Integer i3 = new Integer(3);
    protected Integer i4 = new Integer(4);
    protected Integer i5 = new Integer(5);
    protected Integer i6 = new Integer(6);
}
