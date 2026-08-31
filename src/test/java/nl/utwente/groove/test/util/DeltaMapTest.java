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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.collect.DeltaMap;
import nl.utwente.groove.util.collect.DeltaMap.Delta;
import nl.utwente.groove.util.parse.FormatException;
import nl.utwente.groove.util.parse.Parser;
import nl.utwente.groove.util.parse.StringParser;

/**
 * Tests {@link DeltaMap}: consistency of the forward and inverse view under
 * {@code set} and {@code remove}, value semantics, and the space-separated
 * {@code +key}/{@code -key} format of the parser behind
 * {@link DeltaMap#parser(Parser)}.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
@NonNullByDefault
public class DeltaMapTest {
    /** Setting a delta is reflected in both the forward and the inverse
     * view; overwriting returns the old delta and migrates the key between
     * the inverse sets. */
    @Test
    public void testSetAndInverse() {
        var map = new DeltaMap<String>();
        assertNull(map.set("a", Delta.ADD));
        assertNull(map.set("b", Delta.REMOVE));
        assertEquals(Delta.ADD, map.get("a"));
        assertEquals(Set.of("a"), map.getKeys(Delta.ADD));
        assertEquals(Set.of("b"), map.getKeys(Delta.REMOVE));
        // overwriting moves the key to the other inverse set
        assertEquals(Delta.ADD, map.set("a", Delta.REMOVE));
        assertTrue(map.getKeys(Delta.ADD).isEmpty());
        assertEquals(Set.of("a", "b"), map.getKeys(Delta.REMOVE));
    }

    /** Removal returns the old delta and cleans up the inverse view;
     * removing an absent key returns {@code null}. */
    @Test
    public void testRemove() {
        var map = new DeltaMap<String>();
        map.set("a", Delta.ADD);
        assertEquals(Delta.ADD, map.remove("a"));
        assertNull(map.get("a"));
        assertTrue(map.getKeys(Delta.ADD).isEmpty());
        assertNull(map.remove("a"));
    }

    /** The copy constructor yields an equal but independent map;
     * equality and hash code are content-based. */
    @Test
    public void testCopyAndEquals() {
        var map = new DeltaMap<String>();
        map.set("a", Delta.ADD);
        map.set("b", Delta.REMOVE);
        var copy = new DeltaMap<>(map);
        assertEquals(map, copy);
        assertEquals(map.hashCode(), copy.hashCode());
        copy.set("c", Delta.ADD);
        assertNotEquals(map, copy);
        assertEquals(Set.of("a"), map.getKeys(Delta.ADD));
    }

    /** Parses a well-formed list, including surplus whitespace and the
     * empty string. */
    @Test
    public void testParse() throws FormatException {
        var parser = createParser();
        var map = parser.parse(" +a  -b ");
        assertEquals(Delta.ADD, map.get("a"));
        assertEquals(Delta.REMOVE, map.get("b"));
        assertEquals(2, map.entrySet().size());
        assertTrue(parser.parse("").entrySet().isEmpty());
    }

    /** An entry without a {@code +}/{@code -} prefix and a key occurring
     * twice are format errors. */
    @Test
    public void testParseErrors() {
        var parser = createParser();
        assertThrows(FormatException.class, () -> parser.parse("a"));
        assertThrows(FormatException.class, () -> parser.parse("+a -a"));
    }

    /** Unparsing produces the {@code +key}/{@code -key} list (in key order),
     * and parsing it back yields an equal map. */
    @Test
    public void testUnparseRoundtrip() throws FormatException {
        var parser = createParser();
        var map = new DeltaMap<String>();
        map.set("b", Delta.REMOVE);
        map.set("a", Delta.ADD);
        String text = parser.unparse(map);
        assertEquals("+a -b", text.trim());
        assertEquals(map, parser.parse(text));
    }

    private Parser<DeltaMap<String>> createParser() {
        return DeltaMap.parser(StringParser.identity());
    }
}
