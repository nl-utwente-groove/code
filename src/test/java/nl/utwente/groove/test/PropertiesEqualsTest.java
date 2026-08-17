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
package nl.utwente.groove.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.grammar.GrammarKey;
import nl.utwente.groove.grammar.GrammarProperties;
import nl.utwente.groove.graph.GraphProperties;
import nl.utwente.groove.util.AIGenerated;

/**
 * Tests for the content-based equality of {@link nl.utwente.groove.util.Properties}
 * and the canonical-representation invariant it relies on: values that parse
 * to their key's default are never stored, on any route into the map
 * (mutators as well as {@code load}).
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
@NonNullByDefault
public class PropertiesEqualsTest {
    /**
     * Loading normalises explicit-default entries away, so a loaded map
     * equals a programmatically built one regardless of how the file
     * spelled out defaults.
     */
    @Test
    public void testLoadNormalisesDefaults() throws IOException {
        // loopsAsLabels = true is the default and must be dropped on load;
        // matchInjective = true is not (default false) and must be kept
        var text = """
            loopsAsLabels = true
            matchInjective = true
            """;
        var loaded = new GrammarProperties();
        loaded.load(new ByteArrayInputStream(text.getBytes(StandardCharsets.ISO_8859_1)));
        assertFalse(loaded.containsKey(GrammarKey.LOOPS_AS_LABELS));
        assertTrue(loaded.containsKey(GrammarKey.INJECTIVE));

        var built = new GrammarProperties();
        built.setInjective(true);
        assertEquals(built, loaded);
        assertEquals(built.hashCode(), loaded.hashCode());
    }

    /** A store/load round trip preserves equality. */
    @Test
    public void testStoreLoadRoundTrip() throws IOException {
        var original = new GrammarProperties();
        original.setInjective(true);
        original.setProperty("userKey", "userValue");
        var writer = new StringWriter();
        original.store(writer);
        var reloaded = new GrammarProperties();
        reloaded
            .load(new ByteArrayInputStream(writer
                .toString()
                .getBytes(StandardCharsets.ISO_8859_1)));
        assertEquals(original, reloaded);
    }

    /**
     * Equality follows content through mutation: a clone is equal, diverges
     * on a real change, and setting a key to its default is a no-op for
     * equality because the entry is never stored.
     */
    @Test
    public void testEqualityFollowsContent() {
        var props = new GrammarProperties();
        var clone = props.clone();
        assertEquals(props, clone);
        assertEquals(props.hashCode(), clone.hashCode());

        clone.setInjective(true);
        assertNotEquals(props, clone);
        clone.setInjective(false);
        assertEquals(props, clone);

        // explicitly setting a default value must not affect equality
        clone.setShowLoopsAsLabels(true);
        assertEquals(props, clone);

        // user (non-key) properties do count
        clone.setProperty("userKey", "userValue");
        assertNotEquals(props, clone);
        props.setProperty("userKey", "userValue");
        assertEquals(props, clone);
    }

    /** Property maps of different classes are never equal, even when empty. */
    @Test
    public void testDifferentClassesNeverEqual() {
        // strip the version entries that the constructor stores,
        // leaving an empty map
        var grammarProps = new GrammarProperties();
        grammarProps.remove(GrammarKey.GROOVE_VERSION);
        grammarProps.remove(GrammarKey.GRAMMAR_VERSION);
        var graphProps = new GraphProperties();
        assertNotEquals(grammarProps, graphProps);
        assertNotEquals(graphProps, grammarProps);
        assertEquals(grammarProps, grammarProps.clone());
    }
}
