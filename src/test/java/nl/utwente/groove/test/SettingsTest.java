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

import org.junit.Test;

import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.grammar.model.Settings;
import nl.utwente.groove.grammar.model.SettingsModel;
import nl.utwente.groove.grammar.model.SettingsSchema;
import nl.utwente.groove.grammar.model.SettingsSchemas;
import nl.utwente.groove.io.FileType;
import nl.utwente.groove.io.store.SystemStore;
import nl.utwente.groove.util.parse.FormatError;
import nl.utwente.groove.util.parse.FormatErrorSet;

/**
 * Tests the SETTINGS resource kind: the name-based distinction from the
 * grammar properties singleton, the schema mechanism, and the isolation of
 * settings errors from grammar compilation.
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
        public FormatErrorSet check(Properties props) {
            FormatErrorSet result = new FormatErrorSet();
            if (props.containsKey(REJECTED_KEY)) {
                result.add("Unknown settings key '%s'", REJECTED_KEY);
            }
            return result;
        }
    }

    static {
        SettingsSchemas.register(new TestSchema());
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
        assertEquals(names("good", "no-schema", "rejected", "sub.nested", "sub.system",
                           "unknown-schema"),
                     new TreeSet<>(newStore().getTexts(ResourceKind.SETTINGS).keySet()));
    }

    /** Tests that settings resources are all active, and cannot be enabled or disabled. */
    @Test
    public void testActiveNames() throws Exception {
        GrammarModel grammar = newGrammar();
        assertEquals(grammar.getNames(ResourceKind.SETTINGS),
                     grammar.getActiveNames(ResourceKind.SETTINGS));
        assertFalse(ResourceKind.SETTINGS.isEnableable());
    }

    // ----------------------------------------------------------------------
    // Schema resolution and checking
    // ----------------------------------------------------------------------

    /** Tests that a well-formed settings resource compiles to its entries. */
    @Test
    public void testWellFormed() throws Exception {
        Settings settings = getModel(newGrammar(), "good").toResource();
        assertEquals("test", settings.getSchema().getName());
        assertEquals("red", settings.getProperty("colour"));
        assertEquals("test", settings.getProperty(SettingsModel.SCHEMA_KEY));
    }

    /** Tests that a settings resource in a subfolder compiles just as well. */
    @Test
    public void testNested() throws Exception {
        GrammarModel grammar = newGrammar();
        assertEquals("yellow", getModel(grammar, "sub.nested").toResource().getProperty("colour"));
        // a nested system.properties is a settings resource like any other
        assertEquals("white", getModel(grammar, "sub.system").toResource().getProperty("colour"));
    }

    /** Tests the error for a settings resource without a schema declaration. */
    @Test
    public void testMissingSchema() throws Exception {
        assertError(newGrammar(), "no-schema", "must declare a $schema key");
    }

    /** Tests the error for a settings resource with an unregistered schema. */
    @Test
    public void testUnknownSchema() throws Exception {
        assertError(newGrammar(), "unknown-schema", "Unknown settings schema 'nosuch'");
    }

    /** Tests that the schema itself gets to reject entries. */
    @Test
    public void testRejectedBySchema() throws Exception {
        assertError(newGrammar(), "rejected", "Unknown settings key 'bogus'");
    }

    // ----------------------------------------------------------------------
    // Isolation from the grammar
    // ----------------------------------------------------------------------

    /**
     * Tests that settings errors stay on the resource: settings do not
     * contribute to grammar compilation, so the surrounding grammar (which
     * holds three erroneous settings resources) compiles regardless.
     */
    @Test
    public void testGrammarUnaffected() throws Exception {
        GrammarModel grammar = newGrammar();
        assertTrue(getModel(grammar, "no-schema").hasErrors());
        assertTrue(getModel(grammar, "unknown-schema").hasErrors());
        assertTrue(getModel(grammar, "rejected").hasErrors());
        assertEquals(List.of(), messages(grammar.getErrors()));
        assertNotNull(grammar.toGrammar());
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
     * Tests that the store refuses to create a settings resource under the name
     * reserved for the grammar properties, at top level but not below it.
     */
    @Test
    public void testReservedName() throws Exception {
        SystemStore store = copyStore(newStore());
        try {
            store
                .putTexts(ResourceKind.SETTINGS,
                          Map.of(QualName.name("system"), "$schema=test\n"));
            fail("Storing settings under reserved name 'system' should fail");
        } catch (IOException exc) {
            assertTrue(exc.getMessage(), exc.getMessage().contains("reserved"));
        }
        // the same name is unproblematic inside a module
        QualName nested = QualName.parse("sub.other");
        store.putTexts(ResourceKind.SETTINGS, Map.of(nested, "$schema=test\ncolour=white\n"));
        assertTrue(store.getTexts(ResourceKind.SETTINGS).containsKey(nested));
        // renaming onto the reserved name is refused as well
        try {
            store.rename(ResourceKind.SETTINGS, QualName.name("good"), QualName.name("system"));
            fail("Renaming settings to reserved name 'system' should fail");
        } catch (IOException exc) {
            assertTrue(exc.getMessage(), exc.getMessage().contains("reserved"));
        }
        // the grammar name itself is only reserved in the legacy situation;
        // with a system.properties present it is a name like any other
        QualName grammarName = QualName.name(GRAMMAR);
        store.putTexts(ResourceKind.SETTINGS, Map.of(grammarName, "$schema=test\n"));
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
