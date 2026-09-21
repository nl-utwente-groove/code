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
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.Factory;

/**
 * Tests the dependency tracking of {@link Factory}: a build that reads another
 * factory is recorded as its user and reset with it, but is not kept alive by it
 * (gh #919).
 */
@NonNullByDefault
@AIGenerated("Claude Fable 5.1, 2026-09")
public class FactoryTest {
    /** A build reading another factory is recorded as its user and reset with it. */
    @Test
    public void testBuildRecordsUser() {
        Factory<Integer> base = Factory.lazy(() -> 1);
        Factory<Integer> user = Factory.lazy(() -> base.get() + 1);
        assertEquals(2, user.get());
        assertEquals(1, base.getUserCount());
        base.reset();
        assertFalse(user.isSet());
        assertEquals(0, base.getUserCount());
    }

    /** Setting a value by hand records the current builders as well. */
    @Test
    public void testSetRecordsUser() {
        Factory<Integer> base = Factory.lazy(() -> 1);
        Factory<Integer> user = Factory.lazy(() -> {
            base.set(5);
            return base.get();
        });
        assertEquals(5, user.get());
        assertEquals(1, base.getUserCount());
    }

    /** A user that is otherwise unreachable is not kept alive by the factory it read. */
    @Test
    public void testUnreachableUserIsCollected() throws InterruptedException {
        Factory<Integer> base = Factory.lazy(() -> 1);
        buildAndDrop(base);
        for (int i = 0; i < 10 && base.getUserCount() > 0; i++) {
            System.gc();
            Thread.sleep(10);
        }
        assertEquals(0, base.getUserCount(), "unreachable user retained by the factory it read");
        // a reset after the user has been collected must still work
        assertTrue(base.isSet());
        base.reset();
        assertFalse(base.isSet());
    }

    /**
     * Builds a factory from a given one and lets the result go out of scope.
     * Kept out of the test method so that no local variable of the test frame
     * keeps the user reachable.
     */
    private static void buildAndDrop(Factory<Integer> base) {
        Factory<Integer> user = Factory.lazy(() -> base.get() + 1);
        assertEquals(2, user.get());
        assertEquals(1, base.getUserCount());
    }
}
