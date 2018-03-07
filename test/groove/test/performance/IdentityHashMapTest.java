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
package groove.test.performance;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.Test;

/**
 * Shows up some erroneous behaviour in {@link java.util.IdentityHashMap}. The
 * <code>IdentityHashMap#keySet().iterator().remove()</code> method does not
 * work correctly. Bug report submitted to http://bugs.sun.com/bugdatabase.
 * @author Arend Rensink
 * @version $Revision$
 */
@SuppressWarnings("all")
public class IdentityHashMapTest {

    @Test
    public void testIdentityHashMap() {
        for (int i = 0; i < 100000; i++) {
            Map<Integer,String> test = new IdentityHashMap<>();
            for (int k = 0; k < 7; k++) {
                test.put(k, "");
            }
            Iterator<Integer> testIter = test.keySet()
                .iterator();
            while (testIter.hasNext()) {
                Integer elem = testIter.next();
                if (elem.intValue() != 6) {
                    testIter.remove();
                }
            }
            assertEquals(1,
                test.keySet()
                    .size());
            assertEquals(Collections.singleton(6), test.keySet());
        }
    }
}
