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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;

import nl.utwente.groove.explore.ExploreType;
import nl.utwente.groove.explore.LTLExploreType;
import nl.utwente.groove.explore.config.ConfiguredExploreType;
import nl.utwente.groove.explore.config.ExploreConfig;
import nl.utwente.groove.grammar.GrammarKey;
import nl.utwente.groove.grammar.GrammarProperties;
import nl.utwente.groove.util.Version;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Tests for the storage of exploration configurations in the grammar
 * properties: the new 'exploration' key, its precedence over the legacy
 * 'explorationStrategy' key, and the conversion between them.
 * @author Arend Rensink
 * @version $Revision$
 */
public class ExplorePropertiesTest {
    /**
     * Tests the version-based repair: a pre-3.12 grammar with a stored
     * legacy exploration gets it converted to the new key; an unparsable or
     * inexpressible legacy value is left in place for the read-time
     * fallback; if both keys are present, the shadowed legacy key is
     * dropped; and a 3.12 grammar is not repaired.
     */
    @Test
    public void testVersionRepair() {
        // plain conversion
        var properties = versioned(Version.GRAMMAR_VERSION_3_11);
        properties.setProperty(GrammarKey.EXPLORATION.getName(), "dfs final 1");
        var repaired = properties.repairVersion();
        assertFalse(repaired.containsKey(GrammarKey.EXPLORATION));
        assertEquals("next=newest count=first",
                     repaired.getProperty(GrammarKey.EXPLORE_CONFIG.getName()));
        // an inexpressible legacy value survives for the read-time fallback
        properties = versioned(Version.GRAMMAR_VERSION_3_11);
        properties.setProperty(GrammarKey.EXPLORATION.getName(), "ltl:true final 0");
        repaired = properties.repairVersion();
        assertEquals("ltl:true final 0",
                     repaired.getProperty(GrammarKey.EXPLORATION.getName()));
        assertFalse(repaired.containsKey(GrammarKey.EXPLORE_CONFIG));
        // a shadowed legacy key is dropped without conversion
        properties = versioned(Version.GRAMMAR_VERSION_3_11);
        properties.setProperty(GrammarKey.EXPLORATION.getName(), "dfs final 1");
        properties.setProperty(GrammarKey.EXPLORE_CONFIG.getName(), "next=random");
        repaired = properties.repairVersion();
        assertFalse(repaired.containsKey(GrammarKey.EXPLORATION));
        assertEquals("next=random", repaired.getProperty(GrammarKey.EXPLORE_CONFIG.getName()));
        // a current-version grammar is left alone
        properties = versioned(Version.GRAMMAR_VERSION_3_12);
        properties.setProperty(GrammarKey.EXPLORATION.getName(), "dfs final 1");
        repaired = properties.repairVersion();
        assertEquals("dfs final 1", repaired.getProperty(GrammarKey.EXPLORATION.getName()));
    }

    /** Creates a properties object stamped with a given grammar version. */
    private GrammarProperties versioned(String version) {
        var properties = new GrammarProperties();
        properties.setProperty(GrammarKey.GRAMMAR_VERSION.getName(), version);
        return properties;
    }

    /** Stores a raw legacy exploration description, as a pre-3.12 grammar
     * file would contain it. */
    private GrammarProperties withLegacy(GrammarProperties properties, String legacy) {
        properties.setProperty(GrammarKey.EXPLORATION.getName(), legacy);
        return properties;
    }

    /** Asserts that an exploration type is configuration-based with a given
     * configuration text. */
    private void assertConfigured(String expected, ExploreType type) throws FormatException {
        assertInstanceOf(ConfiguredExploreType.class, type);
        assertEquals(ExploreConfig.parse(expected), ((ConfiguredExploreType) type).getConfig());
    }

    /** Tests the values of a fresh properties object. */
    @Test
    public void testDefaults() {
        var properties = new GrammarProperties();
        assertEquals(new ExploreConfig(), properties.getExploreConfig());
        assertSame(ExploreType.DEFAULT, properties.getExploreType());
    }

    /** Tests that a stored configuration is retrievable in both forms. */
    @Test
    public void testStoreConfig() throws FormatException {
        var properties = new GrammarProperties();
        var config = ExploreConfig.parse("next=newest count=first");
        properties.setExploreConfig(config);
        assertEquals("next=newest count=first",
                     properties.getProperty(GrammarKey.EXPLORE_CONFIG.getName()));
        assertFalse(properties.containsKey(GrammarKey.EXPLORATION));
        assertEquals(config, properties.getExploreConfig());
        assertConfigured("next=newest count=first", properties.getExploreType());
    }

    /** Tests that a stored legacy exploration description is translated on
     * retrieval, in both the type and the configuration form. */
    @Test
    public void testLegacyFallback() throws FormatException {
        var properties = withLegacy(new GrammarProperties(), "dfs final 1");
        assertTrue(properties.containsKey(GrammarKey.EXPLORATION));
        assertFalse(properties.containsKey(GrammarKey.EXPLORE_CONFIG));
        assertConfigured("next=newest count=first", properties.getExploreType());
        assertEquals(ExploreConfig.parse("next=newest count=first"),
                     properties.getExploreConfig());
        // a non-configuration legacy value yields its dedicated type,
        // and falls back to the default configuration
        properties = withLegacy(new GrammarProperties(), "ltl:true cycle 0");
        assertInstanceOf(LTLExploreType.class, properties.getExploreType());
        assertEquals(new ExploreConfig(), properties.getExploreConfig());
    }

    /** Tests that the configuration key takes precedence over the legacy key. */
    @Test
    public void testPrecedence() throws FormatException {
        var properties = new GrammarProperties();
        properties.setExploreConfig(ExploreConfig.parse("next=newest"));
        // re-adding the legacy key does not change the outcome
        withLegacy(properties, "linear final 0");
        assertTrue(properties.containsKey(GrammarKey.EXPLORATION));
        assertConfigured("next=newest", properties.getExploreType());
        assertEquals(ExploreConfig.parse("next=newest"), properties.getExploreConfig());
    }

    /** Tests that storing the configuration removes the legacy key. */
    @Test
    public void testStoreRemovesLegacy() throws FormatException {
        var properties = withLegacy(new GrammarProperties(), "linear final 0");
        properties.setExploreConfig(ExploreConfig.parse("next=newest"));
        assertFalse(properties.containsKey(GrammarKey.EXPLORATION));
        // storing the default configuration leaves no keys at all
        withLegacy(properties, "linear final 0");
        properties.setExploreConfig(new ExploreConfig());
        assertFalse(properties.containsKey(GrammarKey.EXPLORATION));
        assertFalse(properties.containsKey(GrammarKey.EXPLORE_CONFIG));
        assertSame(ExploreType.DEFAULT, properties.getExploreType());
    }

    /** Tests that a stored but unrealisable configuration yields the default type. */
    @Test
    public void testUnrealisableConfig() throws FormatException {
        var properties = new GrammarProperties();
        var config = ExploreConfig.parse("heuristic=nen");
        properties.setExploreConfig(config);
        assertEquals(config, properties.getExploreConfig());
        assertSame(ExploreType.DEFAULT, properties.getExploreType());
        // the key checker reports the problem
        assertFalse(GrammarKey.EXPLORE_CONFIG.check(null, config).isEmpty());
        assertTrue(GrammarKey.EXPLORE_CONFIG
            .check(null, ExploreConfig.parse("next=newest"))
            .isEmpty());
    }
}
