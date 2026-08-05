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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;

import nl.utwente.groove.explore.ExploreType;
import nl.utwente.groove.explore.LTLExploreType;
import nl.utwente.groove.explore.config.ConfiguredExploreType;
import nl.utwente.groove.explore.config.ExploreConfig;
import nl.utwente.groove.grammar.GrammarKey;
import nl.utwente.groove.grammar.GrammarProperties;
import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.io.store.SystemStore;
import nl.utwente.groove.util.Version;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Tests the storage of the default exploration configuration: the
 * {@code exploration} property as a reference to an {@code explore} settings
 * resource, its resolution at the grammar model level, the property checker
 * for the reference, and the read-time fallback for the legacy
 * {@code explorationStrategy} key.
 * @author Arend Rensink
 * @version $Revision$
 */
@SuppressWarnings("javadoc")
public class ExplorePropertiesTest {
    /** Directory of the sample grammars. */
    static private final String INPUT_DIR = "junit/samples";

    /** Tests the values of a fresh properties object. */
    @Test
    public void testDefaults() throws Exception {
        var properties = new GrammarProperties();
        assertNull(properties.getExplorationName());
        assertSame(ExploreType.DEFAULT, properties.getLegacyExploreType());
        // a grammar without any exploration setting yields the defaults
        GrammarModel grammar = newGrammar();
        assertSame(ExploreType.DEFAULT, grammar.getDefaultExploreType());
        assertEquals(new ExploreConfig(), grammar.getDefaultExploreConfig());
    }

    /** Tests that a referenced resource is resolved to its configuration; the
     * reference is the local name within the {@code explore} folder. */
    @Test
    public void testReference() throws Exception {
        GrammarModel grammar = newGrammar(Map
            .of(QualName.parse("explore.fast"), "next = newest\ncount = first\n"), "fast");
        assertEquals(QualName.name("fast"), grammar.getProperties().getExplorationName());
        assertConfigured("next=newest count=first", grammar.getDefaultExploreType());
        assertEquals(ExploreConfig.parse("next=newest count=first"),
                     grammar.getDefaultExploreConfig());
    }

    /** Tests that a nested resource name works as reference target, the
     * reference being the (nested) local name. */
    @Test
    public void testNestedReference() throws Exception {
        GrammarModel grammar = newGrammar(Map
            .of(QualName.parse("explore.nightly.run"), "next = random\n"), "nightly.run");
        assertConfigured("next=random", grammar.getDefaultExploreType());
    }

    /**
     * Tests the property checker for the reference: an unresolvable local name
     * and an erroneous resource are flagged; a valid reference passes.
     */
    @Test
    public void testReferenceChecker() throws Exception {
        GrammarModel grammar = newGrammar(Map
            .of(QualName.parse("explore.good"), "next = newest\n",
                QualName.parse("explore.broken"), "next = sideways\n"),
                                          null);
        var key = GrammarKey.EXPLORE_CONFIG;
        assertTrue(key.check(grammar, Optional.of(QualName.name("good"))).isEmpty());
        assertFalse(key.check(grammar, Optional.of(QualName.name("missing"))).isEmpty());
        assertFalse(key.check(grammar, Optional.of(QualName.name("broken"))).isEmpty());
        // resolution of a broken reference falls back to the default
        setExplorationName(grammar, "broken");
        assertSame(ExploreType.DEFAULT, grammar.getDefaultExploreType());
    }

    /**
     * Tests that an unrealisable configuration is flagged on the resource
     * itself (by the schema check), and resolves to the default type.
     */
    @Test
    public void testUnrealisable() throws Exception {
        GrammarModel grammar = newGrammar(Map
            .of(QualName.parse("explore.nen"), "heuristic = nen\n"), "nen");
        var model = grammar.getResource(ResourceKind.SETTINGS, QualName.parse("explore.nen"));
        assertTrue(model.hasErrors());
        assertSame(ExploreType.DEFAULT, grammar.getDefaultExploreType());
    }

    /** Tests that a stored legacy exploration description is translated on
     * retrieval, in both the type and the configuration form. */
    @Test
    public void testLegacyFallback() throws Exception {
        GrammarModel grammar = newGrammar();
        setLegacy(grammar, "dfs final 1");
        assertConfigured("next=newest count=first", grammar.getDefaultExploreType());
        assertEquals(ExploreConfig.parse("next=newest count=first"),
                     grammar.getDefaultExploreConfig());
        // a non-configuration legacy value yields its dedicated type,
        // and falls back to the default configuration
        setLegacy(grammar, "ltl:true cycle 0");
        assertInstanceOf(LTLExploreType.class, grammar.getDefaultExploreType());
        assertEquals(new ExploreConfig(), grammar.getDefaultExploreConfig());
    }

    /** Tests that the reference takes precedence over the legacy key. */
    @Test
    public void testPrecedence() throws Exception {
        GrammarModel grammar = newGrammar(Map
            .of(QualName.parse("explore.fast"), "next = newest\n"), "fast");
        setLegacy(grammar, "linear final 0");
        assertConfigured("next=newest", grammar.getDefaultExploreType());
    }

    /** Tests that setting the reference removes the legacy key. */
    @Test
    public void testSetReferenceRemovesLegacy() throws Exception {
        var properties = new GrammarProperties();
        properties.setProperty(GrammarKey.EXPLORATION.getName(), "linear final 0");
        properties.setExplorationName(QualName.name("fast"));
        assertFalse(properties.containsKey(GrammarKey.EXPLORATION));
        assertEquals(QualName.name("fast"), properties.getExplorationName());
    }

    /** Tests that the version repair leaves the legacy key in place: it is
     * interpreted by the read-time fallback instead of being converted. */
    @Test
    public void testNoRepair() {
        var properties = new GrammarProperties();
        properties
            .setProperty(GrammarKey.GRAMMAR_VERSION.getName(), Version.GRAMMAR_VERSION_3_11);
        properties.setProperty(GrammarKey.EXPLORATION.getName(), "dfs final 1");
        var repaired = properties.repairVersion();
        assertEquals("dfs final 1", repaired.getProperty(GrammarKey.EXPLORATION.getName()));
        assertFalse(repaired.containsKey(GrammarKey.EXPLORE_CONFIG));
    }

    // ----------------------------------------------------------------------
    // Helper methods
    // ----------------------------------------------------------------------

    /** Asserts that an exploration type is configuration-based with a given
     * configuration text. */
    private void assertConfigured(String expected, ExploreType type) throws FormatException {
        assertInstanceOf(ConfiguredExploreType.class, type);
        assertEquals(ExploreConfig.parse(expected), ((ConfiguredExploreType) type).getConfig());
    }

    /** Creates a modifiable ferryman grammar model without exploration settings. */
    static private GrammarModel newGrammar() throws Exception {
        return newGrammar(Map.of(), null);
    }

    /**
     * Creates a modifiable ferryman grammar model with given explore settings
     * resources, and optionally the exploration property set to one of them.
     * @param reference the <i>local</i> name of the referenced resource, i.e.,
     * without the {@code explore} folder segment
     */
    static private GrammarModel newGrammar(Map<QualName,String> resources,
                                           String reference) throws Exception {
        SystemStore original = SystemStore
            .newStore(new File(INPUT_DIR + "/ferryman.gps"), false, true);
        File dir = Files.createTempDirectory("explore-properties-test").toFile();
        dir.deleteOnExit();
        SystemStore store = original.save(new File(dir, "ferryman.gps"), true);
        if (!resources.isEmpty()) {
            store.putTexts(ResourceKind.SETTINGS, resources);
        }
        GrammarModel result = store.toGrammarModel();
        if (reference != null) {
            setExplorationName(result, reference);
        }
        return result;
    }

    /** Sets the exploration reference in a grammar's stored properties. */
    static private void setExplorationName(GrammarModel grammar, String name) throws Exception {
        var properties = grammar.getProperties().clone();
        properties.setExplorationName(QualName.parse(name));
        ((SystemStore) grammar.getStore()).putProperties(properties);
    }

    /** Sets the legacy exploration strategy key in a grammar's stored properties. */
    static private void setLegacy(GrammarModel grammar, String value) throws Exception {
        var properties = grammar.getProperties().clone();
        properties.setProperty(GrammarKey.EXPLORATION.getName(), value);
        ((SystemStore) grammar.getStore()).putProperties(properties);
    }
}
