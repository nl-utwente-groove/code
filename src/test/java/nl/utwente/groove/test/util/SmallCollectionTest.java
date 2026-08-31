/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2026 University of Twente
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
package nl.utwente.groove.test.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.collect.SmallCollection;

/**
 * Tests {@link SmallCollection}, in particular the transitions between its two
 * representations: a singleton field for a one-element collection, and an inner
 * collection once a second element is added (collapsing back to the singleton
 * representation when removal brings the size back to one).
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
@NonNullByDefault
public class SmallCollectionTest {
    /** A freshly constructed collection is empty in all observable ways. */
    @Test
    public void testEmpty() {
        var coll = new SmallCollection<String>();
        assertTrue(coll.isEmpty());
        assertEquals(0, coll.size());
        assertFalse(coll.isSingleton());
        assertNull(coll.getSingleton());
        assertFalse(coll.contains("a"));
        assertFalse(coll.iterator().hasNext());
        assertFalse(coll.remove("a"));
    }

    /** The singleton constructor yields a one-element collection. */
    @Test
    public void testSingletonConstructor() {
        var coll = new SmallCollection<>("a");
        assertEquals(1, coll.size());
        assertTrue(coll.isSingleton());
        assertEquals("a", coll.getSingleton());
        assertTrue(coll.contains("a"));
        assertFalse(coll.contains("b"));
        var it = coll.iterator();
        assertEquals("a", it.next());
        assertFalse(it.hasNext());
    }

    /** Adding a second element switches to the inner collection;
     * all observations remain consistent. */
    @Test
    public void testGrowBeyondSingleton() {
        var coll = new SmallCollection<String>();
        assertTrue(coll.add("a"));
        assertTrue(coll.isSingleton());
        assertTrue(coll.add("b"));
        assertEquals(2, coll.size());
        assertFalse(coll.isSingleton());
        assertNull(coll.getSingleton());
        assertTrue(coll.contains("a"));
        assertTrue(coll.contains("b"));
        List<String> content = new ArrayList<>(coll);
        assertEquals(List.of("a", "b"), content);
    }

    /** Removing down to one element collapses back to the singleton
     * representation. */
    @Test
    public void testShrinkToSingleton() {
        var coll = new SmallCollection<String>();
        coll.add("a");
        coll.add("b");
        assertTrue(coll.remove("a"));
        assertEquals(1, coll.size());
        assertTrue(coll.isSingleton());
        assertEquals("b", coll.getSingleton());
        assertTrue(coll.remove("b"));
        assertTrue(coll.isEmpty());
    }

    /** Removing an absent element leaves the collection unaffected,
     * in both representations. */
    @Test
    public void testRemoveAbsent() {
        var coll = new SmallCollection<>("a");
        assertFalse(coll.remove("b"));
        assertEquals(1, coll.size());
        coll.add("b");
        coll.add("c");
        assertFalse(coll.remove("d"));
        assertEquals(3, coll.size());
        // removing from a size > 2 inner collection stays in the inner
        // representation
        assertTrue(coll.remove("a"));
        assertEquals(2, coll.size());
        assertFalse(coll.isSingleton());
    }

    /** Clearing empties the collection from either representation. */
    @Test
    public void testClear() {
        var coll = new SmallCollection<>("a");
        coll.clear();
        assertTrue(coll.isEmpty());
        coll.add("a");
        coll.add("b");
        coll.clear();
        assertTrue(coll.isEmpty());
        assertNull(coll.getSingleton());
    }
}
