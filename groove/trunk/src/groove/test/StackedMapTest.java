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
 * $Id: StackedMapTest.java,v 1.3 2008-01-30 09:33:04 iovka Exp $
 */
package groove.test;

import groove.util.StackedMap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

@SuppressWarnings("all")
public class StackedMapTest extends TestCase {
    static final Integer EEN = new Integer(1);
    static final Integer EEN_ = new Integer(-1);
    static final Integer TWEE = new Integer(2);
    static final Integer TWEE_ = new Integer(-2);
    static final Integer DRIE = new Integer(3);
    static final Integer DRIE_ = new Integer(-3);
    static final Integer VIER = new Integer(4);

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.lower = new HashMap<String,Integer>();
        this.lower.put("Een", EEN);
        this.lower.put("Twee", TWEE);
        this.stacked = new StackedMap<String,Integer>(this.lower);
        this.stacked.put("Twee", TWEE_);
        this.stacked.put("Drie", DRIE);
    }

    public void testGet() {
        assertEquals(EEN, this.stacked.get("Een"));
        assertEquals(TWEE_, this.stacked.get("Twee"));
        assertEquals(DRIE, this.stacked.get("Drie"));
    }

    public void testKeySet() {
        Set<String> keySet = new HashSet<String>();
        keySet.add("Een");
        keySet.add("Twee");
        keySet.add("Drie");
        assertEquals(keySet, this.stacked.keySet());
    }

    public void testPut() {
        assertNull(this.stacked.put("Vier", VIER));
        assertEquals(EEN, this.stacked.put("Een", EEN_));
        assertEquals(EEN_, this.stacked.get("Een"));
        assertEquals(DRIE, this.stacked.put("Drie", DRIE_));
        assertEquals(DRIE_, this.stacked.get("Drie"));
    }

    public void testCointainsKey() {
        assertTrue(this.stacked.containsKey("Een"));
        assertTrue(this.stacked.containsKey("Twee"));
        assertTrue(this.stacked.containsKey("Drie"));
        assertFalse(this.stacked.containsKey("Vier"));
        assertNull(this.stacked.put("Vier", VIER));
        assertTrue(this.stacked.containsKey("Vier"));
    }

    public void testCointainsValue() {
        assertTrue(this.stacked.containsValue(EEN));
        assertTrue(this.stacked.containsValue(TWEE_));
        assertFalse(this.stacked.containsValue(TWEE));
        assertTrue(this.stacked.containsValue(DRIE));
        assertNotNull(this.stacked.put("Twee", TWEE));
        assertNotNull(this.stacked.put("Drie", DRIE_));
        assertFalse(this.stacked.containsValue(TWEE_));
        assertTrue(this.stacked.containsValue(TWEE));
        assertFalse(this.stacked.containsValue(DRIE));
        assertTrue(this.stacked.containsValue(DRIE_));
    }

    private Map<String,Integer> lower;
    private Map<String,Integer> stacked;
}
