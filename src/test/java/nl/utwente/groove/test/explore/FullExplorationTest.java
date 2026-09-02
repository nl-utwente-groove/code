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

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.explore.ExploreType;
import nl.utwente.groove.explore.Transformer;
import nl.utwente.groove.explore.config.ConfiguredExploreType;
import nl.utwente.groove.explore.config.ExploreConfig;
import nl.utwente.groove.grammar.GrammarKey;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.io.store.SystemStore;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;

/**
 * Tests the exhaustive exploration derived from a grammar's saved
 * exploration (see {@link ConfiguredExploreType#fullExploration}), which
 * backs the model checking of a full state space (gh #863).
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public class FullExplorationTest {
    /** Directory of the sample grammars. */
    static private final String INPUT_DIR = "junit/samples";

    /** A grammar without saved exploration yields the default configuration. */
    @Test
    public void testDefault() throws Exception {
        var grammar = newGrammar(Map.of(), null);
        assertEquals(new ExploreConfig(),
                     ConfiguredExploreType.fullExploration(grammar).getConfig());
    }

    /** Only the collapse and algebra features of the saved exploration
     * survive; every feature restricting the explored part of the state
     * space is reset to its exhaustive default. */
    @Test
    public void testProjection() throws Exception {
        var name = QualName.parse("explore.partial");
        var grammar = newGrammar(Map
            .of(name, "goal = any\ncount = first\nbound = nodes:5\npersistence = none\n"
                + "collapse = equality\nalgebra = point\n"),
                                 "partial");
        assertFalse(grammar.getResource(ResourceKind.SETTINGS, name).hasErrors());
        assertEquals(ExploreConfig.parse("collapse=equality algebra=point"),
                     ConfiguredExploreType.fullExploration(grammar).getConfig());
    }

    /** A saved legacy exploration that is not configuration-based (LTL)
     * projects to the default configuration. */
    @Test
    public void testLegacyLTL() throws Exception {
        var grammar = newGrammar(Map.of(), null);
        setLegacy(grammar, "ltl:true cycle 0");
        assertEquals(new ExploreConfig(),
                     ConfiguredExploreType.fullExploration(grammar).getConfig());
    }

    /** The saved exploration of a grammar leaves the state space partially
     * explored, whereas the full exploration derived from it is exhaustive. */
    @Test
    public void testExhaustive() throws Exception {
        var grammar = newGrammar(Map.of(), null);
        setLegacy(grammar, "linear final 0");
        GTS partial = explore(grammar, ExploreType.ofGrammar(grammar));
        assertTrue(partial.hasOpenStates());
        GTS full = explore(grammar, ConfiguredExploreType.fullExploration(grammar));
        assertFalse(full.hasOpenStates());
        assertEquals(114, full.nodeCount());
        assertTrue(partial.nodeCount() < full.nodeCount());
    }

    // ----------------------------------------------------------------------
    // Helper methods
    // ----------------------------------------------------------------------

    /** Explores a grammar under a given exploration type. */
    static private GTS explore(GrammarModel grammar, ExploreType type) throws Exception {
        var transformer = new Transformer((SystemStore) grammar.getStore());
        transformer.setExploreType(type);
        return transformer.explore().getGTS();
    }

    /**
     * Creates a modifiable ferryman grammar model with given explore settings
     * resources, and optionally the exploration property set to one of them.
     * @param reference the <i>local</i> name of the referenced resource, i.e.,
     * without the {@code explore} folder segment
     */
    static private GrammarModel newGrammar(Map<QualName,String> resources,
                                           @Nullable String reference) throws Exception {
        SystemStore original
            = SystemStore.newStore(new File(INPUT_DIR + "/ferryman.gps"), false, true);
        File dir = Files.createTempDirectory("full-exploration-test").toFile();
        dir.deleteOnExit();
        SystemStore store = original.save(new File(dir, "ferryman.gps"), true);
        if (!resources.isEmpty()) {
            store.putTexts(ResourceKind.SETTINGS, resources);
        }
        GrammarModel result = store.toGrammarModel();
        var properties = result.getProperties().clone();
        // the fixture predates explicit start graph names, and the implicit
        // default does not survive the version bump on saving the properties
        properties.setActiveNames(ResourceKind.HOST, List.of(QualName.name("start")));
        if (reference != null) {
            properties.setExplorationName(QualName.parse(reference));
        }
        store.putProperties(properties);
        return result;
    }

    /** Sets the legacy exploration strategy key in a grammar's stored properties. */
    static private void setLegacy(GrammarModel grammar, String value) throws Exception {
        var properties = grammar.getProperties().clone();
        properties.setProperty(GrammarKey.EXPLORATION.getName(), value);
        ((SystemStore) grammar.getStore()).putProperties(properties);
    }
}
