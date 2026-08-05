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
package nl.utwente.groove.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.junit.Test;

import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.grammar.model.Settings;
import nl.utwente.groove.grammar.model.SettingsModel;
import nl.utwente.groove.grammar.model.SettingsSchema;
import nl.utwente.groove.grammar.model.SettingsSchemas;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.io.FileType;
import nl.utwente.groove.io.store.SystemStore;
import nl.utwente.groove.util.parse.FormatError;
import nl.utwente.groove.util.parse.FormatErrorSet;

/**
 * Tests the SETTINGS resource kind: the name-based distinction from the
 * grammar properties singleton, the schema mechanism (implied by the location
 * of the resource, with the {@code $schema} entry as consistency check), and
 * the propagation of the errors of active settings resources to the grammar.
 * @author Arend Rensink
 */
@SuppressWarnings("javadoc")
public class SettingsTest {
    /** Directory of the settings test fixtures. */
    static private final String DIR = "junit/settings/";
    /** Name of the test grammar. */
    static private final String GRAMMAR = "settings-test";
    /** Key that the test schema rejects. */
    static private final String REJECTED_KEY = "bogus";

    /** Trivial schema, accepting every key except {@link #REJECTED_KEY}. */
    static private class TestSchema implements SettingsSchema {
        @Override
        public String getName() {
            return "test";
        }

        @Override
        public String getExplanation() {
            return "Test settings without any semantics, used to exercise the generic "
                + "settings mechanism; every key is accepted except the one that is not.";
        }

        @Override
        public FormatErrorSet check(Properties props) {
            FormatErrorSet result = new FormatErrorSet();
            if (props.containsKey(REJECTED_KEY)) {
                result.add("Unknown settings key '%s'", REJECTED_KEY);
            }
            return result;
        }
    }

    /** Singular variant of the test schema, admitting one resource per grammar. */
    static private class SoloSchema extends TestSchema {
        @Override
        public String getName() {
            return "solo";
        }

        @Override
        public boolean isSingular() {
            return true;
        }
    }

    /** Grammar-aware variant of the test schema: the optional {@code rule}
     * entry must name an existing rule of the surrounding grammar. */
    static private class AwareSchema extends TestSchema {
        @Override
        public String getName() {
            return "aware";
        }

        @Override
        public FormatErrorSet check(GrammarModel grammar, Properties props) {
            FormatErrorSet result = check(props);
            String rule = props.getProperty("rule");
            if (rule != null && grammar != null
                && !grammar.getNames(ResourceKind.RULE).contains(QualName.name(rule))) {
                result.add("Unknown rule '%s'", rule);
            }
            return result;
        }

        @Override
        public Set<ResourceKind> getDependencies() {
            return Set.of(ResourceKind.RULE);
        }
    }

    static {
        SettingsSchemas.register(new TestSchema());
        SettingsSchemas.register(new SoloSchema());
        SettingsSchemas.register(new AwareSchema());
    }

    // ----------------------------------------------------------------------
    // Name-based dispatch
    // ----------------------------------------------------------------------

    /**
     * Tests that every properties file in the grammar is a settings resource,
     * named by its qualified path, except the top-level ones that are reserved
     * for the grammar properties.
     */
    @Test
    public void testResourceNames() throws Exception {
        assertEquals(names("test.good", "test.mismatch", "test.nokey", "test.rejected",
                           "test.sub.nested", "test.system", "unknown"),
                     new TreeSet<>(newStore().getTexts(ResourceKind.SETTINGS).keySet()));
    }

    /** Tests that settings resources are not generically enableable, and that
     * for a non-activatable schema they all count as active. */
    @Test
    public void testActiveNames() throws Exception {
        GrammarModel grammar = newGrammar();
        assertEquals(grammar.getNames(ResourceKind.SETTINGS),
                     grammar.getActiveNames(ResourceKind.SETTINGS));
        assertFalse(ResourceKind.SETTINGS.isEnableable());
        // a resource of a non-activatable schema counts as active ...
        SettingsSchema schema = SettingsSchemas.get("test");
        assertNotNull(schema);
        assertFalse(schema.isActivatable());
        assertTrue(getModel(grammar, "test.good").isActive());
        // ... and the schema refuses activation
        try {
            schema.setActive(grammar.getProperties().clone(), QualName.parse("test.good"), true);
            fail("Non-activatable schema should refuse activation");
        } catch (UnsupportedOperationException expected) {
            // this is the expected outcome
        }
        // a resource of an unknown schema counts as inactive
        assertFalse(getModel(grammar, "unknown").isActive());
    }

    // ----------------------------------------------------------------------
    // Schema resolution and checking
    // ----------------------------------------------------------------------

    /** Tests that a well-formed settings resource compiles to its entries,
     * with the schema taken from the folder it lives in. */
    @Test
    public void testWellFormed() throws Exception {
        SettingsModel model = getModel(newGrammar(), "test.good");
        assertEquals("test", model.getSchemaName());
        Settings settings = model.toResource();
        assertEquals("test", settings.getSchema().getName());
        assertEquals("red", settings.getProperty("colour"));
        assertEquals("test", settings.getProperty(SettingsModel.SCHEMA_KEY));
    }

    /** Tests that resources deeper in a schema folder compile just as well. */
    @Test
    public void testNested() throws Exception {
        GrammarModel grammar = newGrammar();
        assertEquals("yellow",
                     getModel(grammar, "test.sub.nested").toResource().getProperty("colour"));
        // the name segment 'system' below top level is unproblematic
        assertEquals("white", getModel(grammar, "test.system").toResource().getProperty("colour"));
    }

    /** Tests that the schema-declaring key is optional: the location says it
     * all, and the key merely repeats it. */
    @Test
    public void testOptionalKey() throws Exception {
        SettingsModel model = getModel(newGrammar(), "test.nokey");
        assertEquals("test", model.getSchemaName());
        Settings settings = model.toResource();
        assertEquals("test", settings.getSchema().getName());
        assertEquals("blue", settings.getProperty("colour"));
    }

    /** Tests that a declaration contradicting the location is an error, even
     * when the declared schema is a registered one. */
    @Test
    public void testSchemaMismatch() throws Exception {
        GrammarModel grammar = newGrammar();
        assertEquals("test", getModel(grammar, "test.mismatch").getSchemaName());
        assertError(grammar, "test.mismatch",
                    "Declared schema 'aware' differs from the schema 'test'");
    }

    /** Tests that names within a schema folder are free, in any depth: it is
     * only the leading segment that carries meaning. */
    @Test
    public void testFreeLocalName() throws Exception {
        SystemStore store = copyStore(newStore());
        QualName name = QualName.parse("test.nightly.run");
        store.putTexts(ResourceKind.SETTINGS, Map.of(name, "colour=cyan\n"));
        SettingsModel model = getModel(store.toGrammarModel(), "test.nightly.run");
        assertEquals("test", model.getSchemaName());
        assertFalse(model.hasErrors());
        assertEquals("cyan", model.toResource().getProperty("colour"));
        // outside a schema folder the same name is homeless, declaration or not
        QualName loose = QualName.parse("nightly.run");
        store.putTexts(ResourceKind.SETTINGS, Map.of(loose, "$schema=test\ncolour=cyan\n"));
        assertError(store.toGrammarModel(), "nightly.run", "Unknown settings schema 'nightly'");
    }

    /** Tests the error for a resource whose leading name segment is no schema. */
    @Test
    public void testUnknownSchema() throws Exception {
        assertError(newGrammar(), "unknown", "Unknown settings schema 'unknown'");
    }

    /**
     * Tests that the singleton form — a top-level file named after the schema
     * — is reserved for singular schemas: for any other schema, the settings
     * have to live inside the schema folder.
     */
    @Test
    public void testSingletonForm() throws Exception {
        SystemStore store = copyStore(newStore());
        store
            .putTexts(ResourceKind.SETTINGS,
                      Map.of(QualName.name("test"), "$schema=test\ncolour=black\n"));
        assertError(store.toGrammarModel(), "test",
                    "Settings of schema 'test' must live inside the 'test' folder");
        // for the singular schema the singleton form is the natural one
        store.putTexts(ResourceKind.SETTINGS, Map.of(QualName.name("solo"), "colour=black\n"));
        assertFalse(getModel(store.toGrammarModel(), "solo").hasErrors());
    }

    /** Tests that the schema itself gets to reject entries. */
    @Test
    public void testRejectedBySchema() throws Exception {
        assertError(newGrammar(), "test.rejected", "Unknown settings key 'bogus'");
    }

    /**
     * Tests that all resources of an over-populated singular schema are
     * flagged, in both the singleton and the folder form, and that removing
     * the surplus clears the error again.
     */
    @Test
    public void testSingularSchema() throws Exception {
        SystemStore store = copyStore(newStore());
        QualName solo = QualName.name("solo");
        QualName extra = QualName.parse("solo.extra");
        store.putTexts(ResourceKind.SETTINGS, Map.of(solo, "colour=green\n"));
        GrammarModel grammar = store.toGrammarModel();
        // a lone resource of a singular schema is fine
        assertEquals("green", getModel(grammar, "solo").toResource().getProperty("colour"));
        // a second resource of the schema puts the error on both
        store.putTexts(ResourceKind.SETTINGS, Map.of(extra, "colour=grey\n"));
        String expected = "Schema 'solo' admits only one settings resource";
        assertError(grammar, "solo", expected);
        assertError(grammar, "solo.extra", expected);
        // the non-singular test schema admits any number of resources
        assertFalse(getModel(grammar, "test.good").hasErrors());
        // removing the surplus clears the error
        store.deleteTexts(ResourceKind.SETTINGS, List.of(extra));
        assertEquals("green", getModel(grammar, "solo").toResource().getProperty("colour"));
    }

    /**
     * Tests the grammar-aware schema check and its dependency tracking: a
     * schema error referring to a missing rule appears on the resource, and is
     * recomputed (without any change to the resource itself) when the rule is
     * added or removed.
     */
    @Test
    public void testGrammarAwareSchema() throws Exception {
        SystemStore store = copyStore(newStore());
        QualName name = QualName.parse("aware.check");
        store.putTexts(ResourceKind.SETTINGS, Map.of(name, "rule=r\n"));
        GrammarModel grammar = store.toGrammarModel();
        assertError(grammar, "aware.check", "Unknown rule 'r'");
        // adding the rule clears the error, though the resource is untouched
        AspectGraph rule = AspectGraph.emptyGraph("r", GraphRole.RULE, false);
        store.putGraphs(ResourceKind.RULE, List.of(rule), false);
        assertFalse(getModel(grammar, "aware.check").hasErrors());
        // removing the rule brings the error back
        store.deleteGraphs(ResourceKind.RULE, List.of(QualName.name("r")));
        assertError(grammar, "aware.check", "Unknown rule 'r'");
    }

    /**
     * Tests the generated initial text of a new settings resource: the schema
     * explanation as wrapped comment lines, then the schema key entry; and the
     * result is a valid settings resource.
     */
    @Test
    public void testNewText() throws Exception {
        SettingsSchema schema = SettingsSchemas.get("test");
        assertNotNull(schema);
        String text = schema.getNewText();
        List<String> lines = List.of(text.split("\n"));
        assertEquals(SettingsModel.SCHEMA_KEY + " = test", lines.get(lines.size() - 1));
        List<String> comments = lines.subList(0, lines.size() - 1);
        assertFalse(comments.isEmpty());
        comments.forEach(l -> assertTrue(l, l.startsWith("# ") && l.length() <= 78));
        assertEquals(schema.getExplanation(),
                     comments
                         .stream()
                         .map(l -> l.substring(2))
                         .collect(Collectors.joining(" ")));
        SystemStore store = copyStore(newStore());
        QualName name = QualName.parse("test.fresh");
        store.putTexts(ResourceKind.SETTINGS, Map.of(name, text));
        assertFalse(getModel(store.toGrammarModel(), "test.fresh").hasErrors());
    }

    // ----------------------------------------------------------------------
    // Propagation to the grammar
    // ----------------------------------------------------------------------

    /**
     * Tests that the errors of <i>active</i> settings resources propagate into
     * the grammar's error set, whereas those of inactive resources (here: a
     * resource of an unknown schema) stay on the resource itself.
     */
    @Test
    public void testGrammarErrorPropagation() throws Exception {
        GrammarModel grammar = newGrammar();
        assertTrue(getModel(grammar, "unknown").hasErrors());
        assertTrue(getModel(grammar, "test.mismatch").hasErrors());
        assertTrue(getModel(grammar, "test.rejected").hasErrors());
        // the active broken resources block the grammar ...
        List<String> errors = messages(grammar.getErrors());
        assertTrue(errors.toString(), errors.stream().anyMatch(e -> e.contains("test.mismatch")));
        assertTrue(errors.toString(), errors.stream().anyMatch(e -> e.contains("test.rejected")));
        // ... whereas the inactive one (of an unknown schema) does not
        assertFalse(errors.toString(), errors.stream().anyMatch(e -> e.contains("unknown")));
        // with the active broken resources gone, the grammar compiles again,
        // even though the inactive broken resource is still there
        SystemStore store = copyStore(newStore());
        store
            .deleteTexts(ResourceKind.SETTINGS,
                         List.of(QualName.parse("test.mismatch"), QualName.parse("test.rejected")));
        GrammarModel fixed = store.toGrammarModel();
        assertTrue(getModel(fixed, "unknown").hasErrors());
        assertEquals(List.of(), messages(fixed.getErrors()));
        assertNotNull(fixed.toGrammar());
    }

    // ----------------------------------------------------------------------
    // Store operations
    // ----------------------------------------------------------------------

    /** Tests that the settings resources survive a save and a reload of the store. */
    @Test
    public void testRoundTrip() throws Exception {
        SystemStore original = newStore();
        SystemStore saved = copyStore(original);
        assertEquals(original.getTexts(ResourceKind.SETTINGS),
                     saved.getTexts(ResourceKind.SETTINGS));
        SystemStore reloaded = SystemStore.newStore(saved.getLocation(), false, true);
        assertEquals(original.getTexts(ResourceKind.SETTINGS),
                     reloaded.getTexts(ResourceKind.SETTINGS));
    }

    /**
     * Tests that the store refuses to create a settings resource under the
     * {@code system} schema, which is built in: both the singleton name and
     * the folder are reserved.
     */
    @Test
    public void testReservedName() throws Exception {
        SystemStore store = copyStore(newStore());
        for (String name : new String[] {"system", "system.other"}) {
            try {
                store
                    .putTexts(ResourceKind.SETTINGS,
                              Map.of(QualName.parse(name), "colour=black\n"));
                fail("Storing settings under reserved name '" + name + "' should fail");
            } catch (IOException exc) {
                assertTrue(exc.getMessage(), exc.getMessage().contains("reserved"));
            }
        }
        // the same segment is unproblematic below the top level
        QualName nested = QualName.parse("test.sub.other");
        store.putTexts(ResourceKind.SETTINGS, Map.of(nested, "colour=white\n"));
        assertTrue(store.getTexts(ResourceKind.SETTINGS).containsKey(nested));
        // renaming onto a reserved name is refused as well
        try {
            store
                .rename(ResourceKind.SETTINGS, QualName.parse("test.good"),
                        QualName.name("system"));
            fail("Renaming settings to reserved name 'system' should fail");
        } catch (IOException exc) {
            assertTrue(exc.getMessage(), exc.getMessage().contains("reserved"));
        }
        // the grammar name itself is only reserved in the legacy situation;
        // with a system.properties present it is a name like any other
        QualName grammarName = QualName.name(GRAMMAR);
        store.putTexts(ResourceKind.SETTINGS, Map.of(grammarName, "colour=black\n"));
        assertTrue(store.getTexts(ResourceKind.SETTINGS).containsKey(grammarName));
    }

    /**
     * Tests the legacy situation of a store whose properties live in an
     * old-style {@code <grammar name>.properties} file: that file acts as the
     * grammar properties (not as a settings resource) and its name is
     * reserved, until a properties save migrates it to
     * {@code system.properties} and thereby frees the name.
     */
    @Test
    public void testLegacyProperties() throws Exception {
        File dir = Files.createTempDirectory("settings-legacy").toFile();
        dir.deleteOnExit();
        File gps = new File(dir, "legacy" + FileType.GRAMMAR.getExtension());
        assertTrue(gps.mkdir());
        Files
            .writeString(new File(gps, "legacy.properties").toPath(),
                         "# legacy grammar properties\n");
        SystemStore store = SystemStore.newStore(gps, false, true);
        QualName legacyName = QualName.name("legacy");
        // the old-style file is the grammar properties, not a settings resource
        assertTrue(store.getTexts(ResourceKind.SETTINGS).isEmpty());
        try {
            store.putTexts(ResourceKind.SETTINGS, Map.of(legacyName, "$schema=test\n"));
            fail("Storing settings under the legacy properties name should fail");
        } catch (IOException exc) {
            assertTrue(exc.getMessage(), exc.getMessage().contains("reserved"));
        }
        // saving the properties migrates them to system.properties and frees the name
        store.putProperties(store.getProperties());
        assertFalse(new File(gps, "legacy.properties").exists());
        store.putTexts(ResourceKind.SETTINGS, Map.of(legacyName, "$schema=test\n"));
        assertTrue(store.getTexts(ResourceKind.SETTINGS).containsKey(legacyName));
    }

    // ----------------------------------------------------------------------
    // Helper methods
    // ----------------------------------------------------------------------

    /** Asserts that a named settings resource has a single error with a given text. */
    static private void assertError(GrammarModel grammar, String name, String expected) {
        List<String> errors = messages(getModel(grammar, name).getErrors());
        assertEquals(errors.toString(), 1, errors.size());
        assertTrue(errors.get(0), errors.get(0).contains(expected));
    }

    /** Returns the settings model of a given name in a given grammar. */
    static private SettingsModel getModel(GrammarModel grammar, String name) {
        var result = grammar.getResource(ResourceKind.SETTINGS, QualName.parse(name));
        assertNotNull(result);
        return (SettingsModel) result;
    }

    /** Returns the messages of a set of format errors, for readable assertions. */
    static private List<String> messages(FormatErrorSet errors) {
        return errors.stream().map(FormatError::toString).toList();
    }

    /** Converts a sequence of strings to the corresponding set of qualified names. */
    static private Set<QualName> names(String... names) {
        Set<QualName> result = new TreeSet<>();
        for (String name : names) {
            result.add(QualName.parse(name));
        }
        return result;
    }

    /** Creates a grammar model on the test fixture. */
    static private GrammarModel newGrammar() throws Exception {
        return newStore().toGrammarModel();
    }

    /** Loads the test fixture store afresh. */
    static private SystemStore newStore() throws Exception {
        File file = new File(DIR + GRAMMAR + FileType.GRAMMAR.getExtension());
        return SystemStore.newStore(file, false, true);
    }

    /** Saves a store to a fresh temporary directory, so that it can be modified. */
    static private SystemStore copyStore(SystemStore store) throws Exception {
        File dir = Files.createTempDirectory("settings-test").toFile();
        dir.deleteOnExit();
        return store.save(new File(dir, GRAMMAR + FileType.GRAMMAR.getExtension()), true);
    }
}
