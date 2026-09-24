/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
 */
package nl.utwente.groove.test.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.collect.ForkableHashMap;
import nl.utwente.groove.util.collect.ForkableHashSet;

/**
 * Tests {@link ForkableHashMap} and {@link ForkableHashSet} against {@link HashMap} and
 * {@link HashSet} under random operations on a growing family of forks, with keys whose
 * hash codes collide heavily (probing and backward-shift deletion) and key ranges large
 * enough to trigger bucket and table growth.
 */
@NonNullByDefault
@AIGenerated("Claude Opus 5.5, 2026-09")
public class ForkableHashMapTest {
    /** Random operations on forked maps, compared with plain hash maps. */
    @Test
    public void testMapForks() {
        for (int hashRange : new int[] {7, 1 << 30}) {
            var random = new Random(hashRange);
            List<ForkableHashMap<Key,Integer>> maps = new ArrayList<>();
            List<Map<Key,Integer>> models = new ArrayList<>();
            maps.add(new ForkableHashMap<>());
            models.add(new HashMap<>());
            for (int step = 0; step < 200_000; step++) {
                int which = random.nextInt(maps.size());
                var map = maps.get(which);
                var model = models.get(which);
                var key = new Key(random.nextInt(3000), hashRange);
                int op = random.nextInt(100);
                if (op < 55) {
                    Integer value = step;
                    assertEquals(model.put(key, value), map.put(key, value));
                } else if (op < 90) {
                    assertEquals(model.remove(key), map.remove(key));
                } else if (op < 99) {
                    assertEquals(model.get(key), map.get(key));
                    assertEquals(model.containsKey(key), map.containsKey(key));
                } else if (maps.size() < 50) {
                    maps.add(new ForkableHashMap<>(map));
                    models.add(new HashMap<>(model));
                }
                assertEquals(model.size(), map.size());
            }
            for (int i = 0; i < maps.size(); i++) {
                assertEquals(models.get(i), maps.get(i));
                assertEquals(models.get(i).keySet(), maps.get(i).keySet());
                assertEquals(models.get(i).keySet().hashCode(), maps.get(i).keySet().hashCode());
                assertEquals(new HashSet<>(models.get(i).values()),
                             new HashSet<>(maps.get(i).values()));
                assertEquals(models.get(i).size(), count(maps.get(i).keySet()));
            }
        }
    }

    /** Random operations on forked sets, compared with plain hash sets. */
    @Test
    public void testSetForks() {
        var random = new Random(42);
        List<ForkableHashSet<Key>> sets = new ArrayList<>();
        List<Set<Key>> models = new ArrayList<>();
        sets.add(new ForkableHashSet<>());
        models.add(new HashSet<>());
        for (int step = 0; step < 200_000; step++) {
            int which = random.nextInt(sets.size());
            var set = sets.get(which);
            var model = models.get(which);
            var key = new Key(random.nextInt(20_000), 1 << 30);
            int op = random.nextInt(100);
            if (op < 60) {
                assertEquals(model.add(key), set.add(key));
            } else if (op < 90) {
                assertEquals(model.remove(key), set.remove(key));
            } else if (op < 99) {
                assertEquals(model.contains(key), set.contains(key));
            } else if (sets.size() < 50) {
                sets.add(new ForkableHashSet<>(set));
                models.add(new HashSet<>(model));
            }
            assertEquals(model.size(), set.size());
        }
        for (int i = 0; i < sets.size(); i++) {
            assertEquals(models.get(i), sets.get(i));
            assertEquals(models.get(i).hashCode(), sets.get(i).hashCode());
            assertEquals(models.get(i).size(), count(sets.get(i)));
        }
    }

    private static int count(Iterable<?> elements) {
        int result = 0;
        for (var e : elements) {
            assertEquals(e, e);
            result++;
        }
        return result;
    }

    /** Key with a hash code reduced to a given range, to force collisions. */
    private record Key(int value, int hashRange) {
        @Override
        public boolean equals(@Nullable Object obj) {
            return obj instanceof Key other && other.value == this.value;
        }

        @Override
        public int hashCode() {
            return this.value % this.hashRange;
        }
    }
}
