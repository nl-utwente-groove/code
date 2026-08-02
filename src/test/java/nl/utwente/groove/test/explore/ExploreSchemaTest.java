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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;

import nl.utwente.groove.explore.config.ExploreConfig;
import nl.utwente.groove.explore.config.ExploreConfigSchema;
import nl.utwente.groove.explore.config.ExploreKey;
import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.grammar.model.SettingsModel;
import nl.utwente.groove.grammar.model.SettingsSchemas;
import nl.utwente.groove.io.store.SystemStore;
import nl.utwente.groove.util.Groove;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Tests the {@code explore} settings schema: the properties form of
 * {@link ExploreConfig}, the schema checks (structure, cross-key consistency,
 * grammar-dependent contents) and the generated template.
 * @author Arend Rensink
 */
@SuppressWarnings("javadoc")
public class ExploreSchemaTest {
    /** Directory of the sample grammars. */
    static private final String INPUT_DIR = "junit/samples";

    /** Tests that the schema is registered under its leading name segment. */
    @Test
    public void testRegistered() {
        assertEquals(ExploreConfigSchema.INSTANCE, SettingsSchemas.get(ExploreConfigSchema.NAME));
    }

    /** Tests the properties form of a configuration. */
    @Test
    public void testFromProperties() throws Exception {
        assertEquals(ExploreConfig.parse("next=newest count=first"),
                     config("next = newest\ncount = first\n$schema = explore\n"));
        // absent entries take their defaults; the empty file is the default config
        assertEquals(new ExploreConfig(), config(""));
        // explicitly written defaults parse to the same configuration
        assertEquals(new ExploreConfig(), config("next = oldest\ngoal = final\n"));
        // content-carrying values work without quoting
        var config = config("goal = condition:load || eat\ncount = 3\n");
        assertEquals(ExploreConfig.parse("goal=\"condition:load || eat\" count=3"), config);
    }

    /** Tests the structural error cases of the properties form. */
    @Test
    public void testFromPropertiesErrors() {
        assertBroken("frontier = narrow\n"); // unknown kind
        assertBroken("depth = 3\n"); // unknown key
        assertBroken("count = first\nbogus = yes\n"); // good and bad mixed
    }

    /** Tests that the schema check includes the cross-key consistency rules. */
    @Test
    public void testConsistencyCheck() throws Exception {
        // structurally fine but inconsistent: next=random needs successor=all
        var props = properties("next = random\nsuccessor = depth\n");
        assertFalse(ExploreConfigSchema.INSTANCE.check(props).isEmpty());
        assertTrue(ExploreConfigSchema.INSTANCE.check(properties("next = random\n")).isEmpty());
    }

    /**
     * Tests the grammar-dependent checks: a resource referring to a missing
     * rule is flagged, and recovers when checked against a grammar that has
     * the rule.
     */
    @Test
    public void testGrammarDependentCheck() throws Exception {
        GrammarModel grammar = Groove.loadGrammar(INPUT_DIR + "/ferryman");
        var goodProps = properties("goal = condition:load\n");
        var badProps = properties("goal = condition:no-such-rule\n");
        assertTrue(ExploreConfigSchema.INSTANCE.check(grammar, goodProps).isEmpty());
        assertFalse(ExploreConfigSchema.INSTANCE.check(grammar, badProps).isEmpty());
        // without a grammar, only structure and consistency are checked
        assertTrue(ExploreConfigSchema.INSTANCE.check(badProps).isEmpty());
    }

    /**
     * Tests an {@code explore} resource in a live store: a well-formed
     * resource compiles, a grammar-dependently broken one shows its error on
     * the resource without affecting the grammar.
     */
    @Test
    public void testResourceInStore() throws Exception {
        // copy the fixture to a temporary directory, so that it can be modified
        SystemStore original = SystemStore
            .newStore(new File(INPUT_DIR + "/ferryman.gps"), false, true);
        File dir = Files.createTempDirectory("explore-schema-test").toFile();
        dir.deleteOnExit();
        SystemStore store = original.save(new File(dir, "ferryman.gps"), true);
        GrammarModel grammar = store.toGrammarModel();
        QualName good = QualName.parse("explore.fast");
        QualName bad = QualName.parse("explore.broken");
        store
            .putTexts(ResourceKind.SETTINGS,
                      Map.of(good, "next = newest\ngoal = condition:load\n", bad,
                             "goal = condition:no-such-rule\n"));
        var goodModel = (SettingsModel) grammar.getResource(ResourceKind.SETTINGS, good);
        assertNotNull(goodModel);
        assertFalse(goodModel.hasErrors());
        var badModel = (SettingsModel) grammar.getResource(ResourceKind.SETTINGS, bad);
        assertNotNull(badModel);
        assertTrue(badModel.hasErrors());
    }

    /** Tests that the generated template is valid and semantically empty. */
    @Test
    public void testTemplate() throws Exception {
        String text = ExploreConfigSchema.INSTANCE.getNewText();
        assertEquals(new ExploreConfig(), config(text));
        for (ExploreKey key : ExploreKey.values()) {
            assertTrue(text.contains("# " + key.getName() + " = " + key.getDefaultKind().getName()),
                       key.getName());
        }
    }

    /**
     * Tests the targeted line edits of the resource writer: comments,
     * ordering and hand-written entries survive, a key reverting to its
     * default keeps its line with the default spelled out, and missing
     * non-default keys are appended.
     */
    @Test
    public void testSetConfigText() throws Exception {
        // a fresh text holds the schema key and the non-default entries
        var config = ExploreConfig.parse("next=newest count=first");
        assertEquals("$schema = explore\nnext = newest\ncount = first\n",
                     ExploreConfigSchema.setConfigText(null, config));
        // targeted edits leave comments and unaffected lines untouched
        String old = "# my comment\n$schema = explore\nnext = random\ngoal = final\n";
        String edited = ExploreConfigSchema.setConfigText(old, config);
        assertEquals("# my comment\n$schema = explore\nnext = newest\ngoal = final\n"
            + "count = first\n", edited);
        // a line whose value already expresses the setting is left verbatim
        assertEquals(edited, ExploreConfigSchema.setConfigText(edited, config));
        // reverting to the default keeps the line, with the default spelled out
        String reverted
            = ExploreConfigSchema.setConfigText(edited, new ExploreConfig());
        assertEquals("# my comment\n$schema = explore\nnext = oldest\ngoal = final\n"
            + "count = all\n", reverted);
        // the round trip through the parser is exact
        assertEquals(config, config(edited));
    }

    /** Tests that the help map documents every exploration key. */
    @Test
    public void testHelpMap() {
        var docMap = ExploreConfigSchema.INSTANCE.getHelpMap();
        assertEquals(ExploreKey.values().length, docMap.size());
        docMap.forEach((item, tip) -> {
            assertTrue(item.startsWith("<html>"), item);
            assertNotNull(tip, item);
        });
    }

    // ----------------------------------------------------------------------
    // Helper methods
    // ----------------------------------------------------------------------

    /** Parses a properties text into a properties object. */
    static private Properties properties(String text) throws Exception {
        var result = new Properties();
        result.load(new StringReader(text));
        return result;
    }

    /** Parses a properties text into a configuration. */
    static private ExploreConfig config(String text) throws Exception {
        return ExploreConfig.fromProperties(properties(text));
    }

    /** Asserts that a properties text is rejected as a configuration. */
    static private void assertBroken(String text) {
        try {
            config(text);
            fail("Configuration '" + text + "' should be rejected");
        } catch (FormatException expected) {
            // this is the expected outcome
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }
    }
}
