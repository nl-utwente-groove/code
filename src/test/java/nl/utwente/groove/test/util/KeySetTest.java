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
import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.collect.KeySet;

/**
 * Tests {@link KeySet}: a set that simultaneously acts as a map from a
 * uniquely defining key of its elements to the elements themselves. Membership
 * is decided by key only, an element whose key is already present is not
 * added, and iteration follows insertion order (the exploration code in
 * {@code lts.StateCache} relies on the latter for determinism).
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
@NonNullByDefault
public class KeySetTest {
    /** Test element: an item uniquely identified by its key, with a payload
     * that does not take part in the key. */
    private record Item(String key, int payload) {
        // no additional members
    }

    /** Creates a key set over {@link Item}s, keyed by {@link Item#key()}.
     * The key type argument is nullable: {@link KeySet} interprets a
     * {@code null} key as "this object has no key", as happens here for
     * objects of a foreign type. */
    private KeySet<@Nullable String,Item> createSet() {
        return new KeySet<>() {
            @Override
            protected @Nullable String getKey(@Nullable Object value) {
                return value instanceof Item item
                    ? item.key()
                    : null;
            }
        };
    }

    /** Adding elements with fresh keys succeeds; an element with an already
     * present key is rejected, keeping the original element. */
    @Test
    public void testAdd() {
        var set = createSet();
        assertTrue(set.add(new Item("a", 1)));
        assertTrue(set.add(new Item("b", 2)));
        assertFalse(set.add(new Item("a", 3)));
        assertEquals(2, set.size());
        assertEquals(new Item("a", 1), set.get("a"));
    }

    /** Membership and removal are decided by key alone, not by the
     * element's other state. */
    @Test
    public void testContainsAndRemoveByKey() {
        var set = createSet();
        set.add(new Item("a", 1));
        assertTrue(set.contains(new Item("a", 99)));
        assertFalse(set.contains(new Item("b", 1)));
        assertTrue(set.remove(new Item("a", 99)));
        assertTrue(set.isEmpty());
        assertFalse(set.remove(new Item("a", 1)));
    }

    /** Objects without a key (of a foreign type) are never contained and
     * cannot be removed. */
    @Test
    public void testForeignObject() {
        var set = createSet();
        set.add(new Item("a", 1));
        assertFalse(set.contains((Object) "a"));
        assertFalse(set.remove((Object) "a"));
        assertEquals(1, set.size());
    }

    /** The map view retrieves elements by key; absent keys yield
     * {@code null}. */
    @Test
    public void testGet() {
        var set = createSet();
        var item = new Item("a", 1);
        set.add(item);
        assertEquals(item, set.get("a"));
        assertNull(set.get("b"));
    }

    /** Iteration follows insertion order. */
    @Test
    public void testIterationOrder() {
        var set = createSet();
        var items = List.of(new Item("c", 1), new Item("a", 2), new Item("b", 3));
        items.forEach(set::add);
        assertEquals(items, new ArrayList<>(set));
    }

    /** Clearing empties the set. */
    @Test
    public void testClear() {
        var set = createSet();
        set.add(new Item("a", 1));
        set.clear();
        assertTrue(set.isEmpty());
        assertNull(set.get("a"));
    }
}
