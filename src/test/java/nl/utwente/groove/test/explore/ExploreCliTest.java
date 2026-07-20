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
 *
 * $Id$
 */
package nl.utwente.groove.test.explore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;

import nl.utwente.groove.explore.Generator;
import nl.utwente.groove.util.cli.CmdLineException;

/**
 * Tests for the Generator's exploration configuration option: exploring with
 * a configuration, and the mutual exclusion with the deprecated strategy and
 * acceptor options.
 * @author Arend Rensink
 * @version $Revision$
 */
public class ExploreCliTest {
    /** Location of the sample grammar used for the tests. */
    static private final String GRAMMAR = "junit/samples/ferryman.gps";

    /** Tests exploration through the configuration option. */
    @Test
    public void testExploreOption() throws Exception {
        var full = Generator.execute("-x", "", GRAMMAR);
        var fullCount = full.getGTS().nodeCount();
        assertTrue(fullCount > 1);
        // the default configuration explores the same state space as the default type
        var dfs = Generator.execute("-x", "next=newest", GRAMMAR);
        assertEquals(fullCount, dfs.getGTS().nodeCount());
        // a linear exploration visits at most as many states
        var linear = Generator.execute("-x", "frontier=single successor=single", GRAMMAR);
        assertTrue(linear.getGTS().nodeCount() <= fullCount);
        // stopping at the first result visits at most as many states as exploring all
        var first = Generator.execute("-x", "count=first", GRAMMAR);
        assertTrue(first.getGTS().nodeCount() <= fullCount);
    }

    /** Tests that the configuration option rejects unrealisable values. */
    @Test
    public void testBadConfig() {
        assertThrows(Exception.class,
                     () -> Generator.execute("-x", "shape=trace", GRAMMAR));
        assertThrows(Exception.class, () -> Generator.execute("-x", "bogus=1", GRAMMAR));
    }

    /** Tests the mutual exclusion of the configuration and legacy options.
     * (Argument parsing only happens when the generator is started.) */
    @Test
    public void testOptionConflicts() {
        assertThrows(CmdLineException.class,
                     () -> Generator.execute("-x", "next=newest", "-s", "bfs", GRAMMAR));
        assertThrows(CmdLineException.class,
                     () -> Generator.execute("-x", "next=newest", "-a", "final", GRAMMAR));
        assertThrows(CmdLineException.class,
                     () -> Generator.execute("-x", "next=newest", "-r", "2", GRAMMAR));
    }

    /** Tests that the deprecated options still work. */
    @Test
    public void testDeprecatedOptions() throws Exception {
        var legacy = Generator.execute("-s", "dfs", "-a", "final", GRAMMAR);
        var config = Generator.execute("-x", "next=newest", GRAMMAR);
        assertEquals(config.getGTS().nodeCount(), legacy.getGTS().nodeCount());
    }
}
