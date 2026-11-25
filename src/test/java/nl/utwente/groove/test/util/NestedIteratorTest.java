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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.collect.NestedIterator;

/**
 * Tests the {@link NestedIterator} over a stream of iterables: it flattens
 * them in order, and it takes an iterable from the stream only when it gets
 * to it (the transition set of a GTS relies on that, so as not to build the
 * transitions of all its states at once).
 * @author Arend Rensink
 */
@AIGenerated("Claude Fable 5.2, 2026-11")
@NonNullByDefault
public class NestedIteratorTest {
    /** Tests the order of the elements and the moment at which the stream is consumed. */
    @Test
    public void testStreamIsConsumedLazily() {
        List<List<Integer>> parts
            = List.of(List.of(), List.of(1, 2), List.of(), List.of(3), List.of(4, 5), List.of());
        // the number of parts that the stream has handed out
        int[] taken = {0};
        Iterator<Integer> iter = NestedIterator.newInstance(parts.stream().map(part -> {
            taken[0]++;
            return part;
        }));
        assertEquals(0, taken[0]);
        List<Integer> result = new ArrayList<>();
        // the expected number of parts taken once an element has been returned
        int[] takenAt = {2, 2, 4, 5, 5};
        for (int at : takenAt) {
            result.add(iter.next());
            assertEquals(at, taken[0]);
        }
        assertFalse(iter.hasNext());
        assertEquals(parts.size(), taken[0]);
        assertEquals(List.of(1, 2, 3, 4, 5), result);
    }
}
