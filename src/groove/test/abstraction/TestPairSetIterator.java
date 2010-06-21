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
 * $Id$
 */
package groove.test.abstraction;

import groove.abstraction.PairSetIterator;
import groove.util.Pair;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;

/**
 * @author Eduardo Zambon
 * @version $Revision $
 */
@SuppressWarnings("all")
public class TestPairSetIterator extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testPairSetIterator0() {
        Set<Pair<String,Set<Integer>>> pairSet =
            new HashSet<Pair<String,Set<Integer>>>();
        Set<Integer> intSet0 = new HashSet<Integer>();
        intSet0.add(0);
        intSet0.add(1);
        intSet0.add(2);
        pairSet.add(new Pair<String,Set<Integer>>("A", intSet0));
        Set<Integer> intSet1 = new HashSet<Integer>();
        intSet1.add(3);
        intSet1.add(4);
        pairSet.add(new Pair<String,Set<Integer>>("B", intSet1));
        Iterator<Set<Pair<String,Integer>>> iter =
            new PairSetIterator<String,Integer>(pairSet);
        int elements = 0;
        while (iter.hasNext()) {
            iter.next();
            elements++;
        }
        assertTrue(elements == 6);
    }

    public void testPairSetIterator1() {
        Set<Pair<String,Set<Integer>>> pairSet =
            new HashSet<Pair<String,Set<Integer>>>();
        Set<Integer> intSet0 = new HashSet<Integer>();
        intSet0.add(0);
        intSet0.add(1);
        intSet0.add(2);
        pairSet.add(new Pair<String,Set<Integer>>("A", intSet0));
        Set<Integer> intSet1 = new HashSet<Integer>();
        intSet1.add(3);
        intSet1.add(4);
        pairSet.add(new Pair<String,Set<Integer>>("B", intSet1));
        Set<Integer> intSet2 = new HashSet<Integer>();
        intSet2.add(5);
        intSet2.add(6);
        intSet2.add(7);
        pairSet.add(new Pair<String,Set<Integer>>("C", intSet2));
        Iterator<Set<Pair<String,Integer>>> iter =
            new PairSetIterator<String,Integer>(pairSet);
        int elements = 0;
        while (iter.hasNext()) {
            iter.next();
            elements++;
        }
        assertTrue(elements == 18);
    }

}
