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
 * $Id: StackedMapTest.java,v 1.1.1.1 2007-03-20 10:05:22 kastenberg Exp $
 */
package groove.test;

import groove.util.StackedMap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

public class StackedMapTest extends TestCase {
    static final Integer EEN = new Integer(1);
    static final Integer EEN_ = new Integer(-1);
    static final Integer TWEE = new Integer(2);
    static final Integer TWEE_ = new Integer(-2);
    static final Integer DRIE = new Integer(3);
    static final Integer DRIE_ = new Integer(-3);
    static final Integer VIER = new Integer(4);
    
    protected void setUp() throws Exception {
        super.setUp();
        lower = new HashMap<String,Integer>();
        lower.put("Een", EEN);
        lower.put("Twee", TWEE);
        stacked = new StackedMap<String,Integer>(lower);
        stacked.put("Twee", TWEE_);
        stacked.put("Drie", DRIE);
    }

    public void testGet() {
        assertEquals(EEN, stacked.get("Een"));
        assertEquals(TWEE_, stacked.get("Twee"));
        assertEquals(DRIE, stacked.get("Drie"));
    }
    
    public void testKeySet() {
        Set<String> keySet = new HashSet<String>();
        keySet.add("Een");
        keySet.add("Twee");
        keySet.add("Drie");
        assertEquals(keySet, stacked.keySet());
    }
    
    public void testPut() {
        assertNull(stacked.put("Vier",VIER));
        assertEquals(EEN, stacked.put("Een", EEN_));
        assertEquals(EEN_, stacked.get("Een"));
        assertEquals(DRIE, stacked.put("Drie", DRIE_));
        assertEquals(DRIE_, stacked.get("Drie"));
    }
    
    public void testCointainsKey() {
        assertTrue(stacked.containsKey("Een"));
        assertTrue(stacked.containsKey("Twee"));
        assertTrue(stacked.containsKey("Drie"));
        assertFalse(stacked.containsKey("Vier"));
        assertNull(stacked.put("Vier",VIER));
        assertTrue(stacked.containsKey("Vier"));
    }
    
    public void testCointainsValue() {
        assertTrue(stacked.containsValue(EEN));
        assertTrue(stacked.containsValue(TWEE_));
        assertFalse(stacked.containsValue(TWEE));
        assertTrue(stacked.containsValue(DRIE));
        assertNotNull(stacked.put("Twee",TWEE));
        assertNotNull(stacked.put("Drie",DRIE_));
        assertFalse(stacked.containsValue(TWEE_));
        assertTrue(stacked.containsValue(TWEE));
        assertFalse(stacked.containsValue(DRIE));
        assertTrue(stacked.containsValue(DRIE_));
    }
    
    private Map<String,Integer> lower;
    private Map<String,Integer> stacked;
}
