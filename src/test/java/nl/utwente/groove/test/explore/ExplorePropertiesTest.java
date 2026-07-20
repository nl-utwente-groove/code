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
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;

import nl.utwente.groove.explore.ExploreType;
import nl.utwente.groove.explore.config.ExploreConfig;
import nl.utwente.groove.grammar.GrammarKey;
import nl.utwente.groove.grammar.GrammarProperties;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Tests for the storage of exploration configurations in the grammar
 * properties: the new 'exploration' key, its precedence over the legacy
 * 'explorationStrategy' key, and the conversion between them.
 * @author Arend Rensink
 * @version $Revision$
 */
public class ExplorePropertiesTest {
    /** Tests the values of a fresh properties object. */
    @Test
    public void testDefaults() {
        var properties = new GrammarProperties();
        assertEquals(new ExploreConfig(), properties.getExploreConfig());
        assertEquals(ExploreType.DEFAULT.unparse(), properties.getExploreType().unparse());
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
        assertEquals("dfs final 1", properties.getExploreType().unparse());
    }

    /** Tests that a stored legacy exploration type is converted on retrieval. */
    @Test
    public void testLegacyFallback() throws FormatException {
        var properties = new GrammarProperties();
        properties.setExploreType(ExploreType.parse("dfs final 1"));
        assertTrue(properties.containsKey(GrammarKey.EXPLORATION));
        assertFalse(properties.containsKey(GrammarKey.EXPLORE_CONFIG));
        assertEquals(ExploreConfig.parse("next=newest count=first"),
                     properties.getExploreConfig());
        // an inexpressible legacy value falls back to the default configuration
        properties.setExploreType(ExploreType.parse("ltl:true cycle"));
        assertEquals(new ExploreConfig(), properties.getExploreConfig());
    }

    /** Tests that the configuration key takes precedence over the legacy key. */
    @Test
    public void testPrecedence() throws FormatException {
        var properties = new GrammarProperties();
        properties.setExploreConfig(ExploreConfig.parse("next=newest"));
        // re-adding the legacy key does not change the outcome
        properties.setExploreType(ExploreType.parse("linear final 0"));
        assertTrue(properties.containsKey(GrammarKey.EXPLORATION));
        assertEquals("dfs final 0", properties.getExploreType().unparse());
        assertEquals(ExploreConfig.parse("next=newest"), properties.getExploreConfig());
    }

    /** Tests that storing the configuration removes the legacy key. */
    @Test
    public void testStoreRemovesLegacy() throws FormatException {
        var properties = new GrammarProperties();
        properties.setExploreType(ExploreType.parse("linear final 0"));
        properties.setExploreConfig(ExploreConfig.parse("next=newest"));
        assertFalse(properties.containsKey(GrammarKey.EXPLORATION));
        // storing the default configuration leaves no keys at all
        properties.setExploreType(ExploreType.parse("linear final 0"));
        properties.setExploreConfig(new ExploreConfig());
        assertFalse(properties.containsKey(GrammarKey.EXPLORATION));
        assertFalse(properties.containsKey(GrammarKey.EXPLORE_CONFIG));
        assertEquals(ExploreType.DEFAULT.unparse(), properties.getExploreType().unparse());
    }

    /** Tests that a stored but unrealisable configuration yields the default type. */
    @Test
    public void testUnrealisableConfig() throws FormatException {
        var properties = new GrammarProperties();
        var config = ExploreConfig.parse("shape=trace");
        properties.setExploreConfig(config);
        assertEquals(config, properties.getExploreConfig());
        assertEquals(ExploreType.DEFAULT.unparse(), properties.getExploreType().unparse());
        // the key checker reports the problem
        assertFalse(GrammarKey.EXPLORE_CONFIG.check(null, config).isEmpty());
        assertTrue(GrammarKey.EXPLORE_CONFIG
            .check(null, ExploreConfig.parse("next=newest"))
            .isEmpty());
    }
}
