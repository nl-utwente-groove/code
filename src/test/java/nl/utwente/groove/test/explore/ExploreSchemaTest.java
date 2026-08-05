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
import static org.junit.jupiter.api.Assertions.assertNull;
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
     * the resource; as long as the broken resource is inactive that error does
     * not affect the grammar, but activating it makes the grammar erroneous.
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
        // the saved copy carries current-version properties, so the legacy
        // default start graph no longer applies: name it explicitly, or the
        // grammar has an unrelated 'No active start graph' error
        grammar.setLocalActiveNames(ResourceKind.HOST, QualName.name("start"));
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
        // neither resource is referenced by the exploration property, so the
        // broken one is inactive and its error does not reach the grammar
        assertFalse(badModel.isActive());
        assertFalse(grammar.hasErrors(), grammar.getErrors().toString());
        // activating the broken resource propagates its error to the grammar;
        // the property holds the local name within the explore folder
        var props = grammar.getProperties().clone();
        props.setExplorationName(QualName.name("broken"));
        store.putProperties(props);
        assertTrue(grammar.hasErrors());
        assertTrue(grammar
            .getErrors()
            .stream()
            .anyMatch(e -> e.toString().contains("explore.broken")),
                   grammar.getErrors().toString());
    }

    /**
     * Tests schema-driven activation: setting a resource active establishes
     * the exploration reference in the grammar properties (as the local name
     * within the {@code explore} folder), the resource's active status follows
     * the reference, and deactivation removes it again.
     */
    @Test
    public void testActivation() throws Exception {
        // copy the fixture to a temporary directory, so that it can be modified
        SystemStore original = SystemStore
            .newStore(new File(INPUT_DIR + "/ferryman.gps"), false, true);
        File dir = Files.createTempDirectory("explore-activation-test").toFile();
        dir.deleteOnExit();
        SystemStore store = original.save(new File(dir, "ferryman.gps"), true);
        GrammarModel grammar = store.toGrammarModel();
        QualName name = QualName.parse("explore.fast");
        store.putTexts(ResourceKind.SETTINGS, Map.of(name, "next = newest\n"));
        var model = (SettingsModel) grammar.getResource(ResourceKind.SETTINGS, name);
        assertNotNull(model);
        var schema = model.getSchema();
        assertEquals(ExploreConfigSchema.INSTANCE, schema);
        assertTrue(schema.isActivatable());
        // initially the resource is not the grammar's exploration
        assertFalse(model.isActive());
        // activation sets the exploration reference
        var props = grammar.getProperties().clone();
        schema.setActive(props, name, true);
        store.putProperties(props);
        assertEquals(QualName.name("fast"), grammar.getProperties().getExplorationName());
        model = (SettingsModel) grammar.getResource(ResourceKind.SETTINGS, name);
        assertTrue(model.isActive());
        assertEquals(ExploreConfig.parse("next=newest"), grammar.getDefaultExploreConfig());
        // deactivation removes the reference again
        props = grammar.getProperties().clone();
        schema.setActive(props, name, false);
        store.putProperties(props);
        assertNull(grammar.getProperties().getExplorationName());
        model = (SettingsModel) grammar.getResource(ResourceKind.SETTINGS, name);
        assertFalse(model.isActive());
    }

    /**
     * Tests that exploration settings have to live inside the {@code explore}
     * folder: a top-level resource declaring the schema is not recognised as
     * one, since its own name is taken for the schema name.
     */
    @Test
    public void testOutsideFolder() throws Exception {
        SystemStore store = newTempStore("explore-free-name-test");
        GrammarModel grammar = store.toGrammarModel();
        QualName name = QualName.name("fast");
        store
            .putTexts(ResourceKind.SETTINGS,
                      Map.of(name, "$schema = explore\nnext = newest\n"));
        var model = (SettingsModel) grammar.getResource(ResourceKind.SETTINGS, name);
        assertNotNull(model);
        assertEquals("fast", model.getSchemaName());
        assertNull(model.getSchema());
        assertTrue(model.hasErrors());
    }

    /**
     * Tests that a declaration contradicting the folder is an error: the
     * location decides, and the declaration only gets to agree with it.
     */
    @Test
    public void testDeclarationMismatch() throws Exception {
        SystemStore store = newTempStore("explore-mismatch-test");
        GrammarModel grammar = store.toGrammarModel();
        QualName name = QualName.parse("ecore.something");
        store
            .putTexts(ResourceKind.SETTINGS,
                      Map.of(name, "$schema = explore\ncount = first\n"));
        var model = (SettingsModel) grammar.getResource(ResourceKind.SETTINGS, name);
        assertNotNull(model);
        assertTrue(model.hasErrors());
        assertTrue(model
            .getErrors()
            .stream()
            .anyMatch(e -> e.toString().contains("Declared schema 'explore'")),
                   model.getErrors().toString());
    }

    /**
     * Tests that activation is stated in resource names while the property
     * holds the local name: the schema converts in both directions.
     */
    @Test
    public void testLocalNameRoundTrip() throws Exception {
        SystemStore store = newTempStore("explore-local-name-test");
        GrammarModel grammar = store.toGrammarModel();
        QualName name = QualName.parse("explore.nightly.run");
        store.putTexts(ResourceKind.SETTINGS, Map.of(name, "next = newest\n"));
        var schema = ExploreConfigSchema.INSTANCE;
        QualName local = schema.getLocalName(name);
        assertEquals(QualName.parse("nightly.run"), local);
        var props = grammar.getProperties().clone();
        schema.setActive(props, name, true);
        // the property stores the local name, without the folder segment
        assertEquals(local, props.getExplorationName());
        store.putProperties(props);
        assertTrue(schema.isActive(grammar, name));
        assertEquals(name, schema.getResourceName(local));
        assertEquals(ExploreConfig.parse("next=newest"), grammar.getDefaultExploreConfig());
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

    /** Copies the ferryman fixture to a fresh temporary directory, so that the
     * resulting store can be modified. */
    static private SystemStore newTempStore(String prefix) throws Exception {
        SystemStore original = SystemStore
            .newStore(new File(INPUT_DIR + "/ferryman.gps"), false, true);
        File dir = Files.createTempDirectory(prefix).toFile();
        dir.deleteOnExit();
        return original.save(new File(dir, "ferryman.gps"), true);
    }

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
