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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import nl.utwente.groove.grammar.GrammarKey;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.io.Groove;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.collect.DeltaMap.Delta;
import nl.utwente.groove.util.parse.FormatException;
import nl.utwente.groove.util.parse.Severity;

/**
 * Tests that disabled rules do not block grammar building, whether they are
 * disabled through the current {@code ruleEnabling} property or through the
 * legacy {@code disabledRules} property of grammars saved before GROOVE 7.4.0
 * (gh #908), and that unknown rule names in the enabling property are
 * reported as warnings rather than errors. The tests work on a copy of the
 * {@code compileErrors} fixture, all of whose rules are deliberately broken,
 * in a temporary directory with a rewritten properties file.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public class DisabledRulesTest {
    /** Location of the fixture grammar. */
    private static final String GRAMMAR_DIR = "junit/rules/compileErrors.gps";

    /** Rules disabled through the legacy key are neither compiled nor
     * blocking, and the key is converted to the current one. */
    @Test
    public void testLegacyDisabledRulesTolerated(@TempDir Path tmp) throws Exception {
        List<String> rules = getRuleNames();
        GrammarModel grammar = load(tmp, "disabledRules=" + String.join(" ", rules));
        assertFalse(grammar.hasErrors(), "Unexpected errors: " + grammar.getErrors());
        assertTrue(grammar.toGrammar().getAllRules().isEmpty());
        var properties = grammar.getProperties();
        assertNull(properties.getProperty(GrammarKey.DISABLED_RULES));
        for (String rule : rules) {
            assertEquals(Delta.REMOVE, properties.getRuleEnabling().get(QualName.parse(rule)));
        }
    }

    /** Rules disabled through the current key are neither compiled nor blocking. */
    @Test
    public void testDisabledRulesTolerated(@TempDir Path tmp) throws Exception {
        List<String> rules = getRuleNames();
        String value = rules.stream().map(r -> "-" + r).reduce((a, b) -> a + " " + b).get();
        GrammarModel grammar = load(tmp, "ruleEnabling=" + value);
        assertFalse(grammar.hasErrors(), "Unexpected errors: " + grammar.getErrors());
        assertTrue(grammar.toGrammar().getAllRules().isEmpty());
    }

    /** Control: a broken rule left enabled still blocks the grammar. */
    @Test
    public void testEnabledRuleBlocks(@TempDir Path tmp) throws Exception {
        List<String> rules = new ArrayList<>(getRuleNames());
        String enabled = rules.remove(0);
        GrammarModel grammar = load(tmp, "disabledRules=" + String.join(" ", rules));
        assertTrue(grammar.hasErrors());
        assertTrue(grammar.getErrors().toString().contains(enabled),
                   "Errors do not mention '" + enabled + "': " + grammar.getErrors());
        assertThrows(FormatException.class, grammar::toGrammar);
    }

    /** An unknown rule name in the enabling property is a warning: the
     * grammar builds and retains the diagnostic. */
    @Test
    public void testUnknownRuleNameWarns(@TempDir Path tmp) throws Exception {
        List<String> rules = getRuleNames();
        GrammarModel grammar
            = load(tmp, "disabledRules=" + String.join(" ", rules) + " nonexistent");
        assertFalse(grammar.hasErrors(), "Unexpected errors: " + grammar.getErrors());
        grammar.toGrammar();
        var warnings = grammar.getErrors().filter(Severity.WARNING);
        assertEquals(1, warnings.get().size(), "Unexpected diagnostics: " + grammar.getErrors());
        String warning = warnings.iterator().next().toString();
        assertTrue(warning.contains("nonexistent"), warning);
        assertTrue(warning.startsWith("Warning in property key"), warning);
    }

    /** Returns the names of the rules in the fixture grammar. */
    private List<String> getRuleNames() throws IOException {
        List<String> result = new ArrayList<>();
        try (Stream<Path> files = Files.list(Path.of(GRAMMAR_DIR))) {
            files
                .map(f -> f.getFileName().toString())
                .filter(n -> n.endsWith(".gpr"))
                .map(n -> n.substring(0, n.length() - ".gpr".length()))
                .sorted()
                .forEach(result::add);
        }
        assertFalse(result.isEmpty());
        return result;
    }

    /** Copies the fixture into the temporary directory, adds an empty start
     * graph (the fixture has none), and writes a properties file consisting
     * of a pre-7.4.0 version stamp, the type graph and start graph settings
     * and a given additional line; then loads the copy. */
    private GrammarModel load(Path tmp, String propertyLine) throws IOException {
        Path dir = tmp.resolve("compileErrors.gps");
        Files.createDirectory(dir);
        try (Stream<Path> files = Files.list(Path.of(GRAMMAR_DIR))) {
            for (Path file : files.toList()) {
                if (!file.getFileName().toString().equals("system.properties")) {
                    Files.copy(file, dir.resolve(file.getFileName()));
                }
            }
        }
        Files.writeString(dir.resolve("start.gst"), EMPTY_GRAPH);
        Files
            .writeString(dir.resolve("system.properties"),
                         "grammarVersion=3.11\ngrooveVersion=7.3.1\ntypeGraph=type\n"
                             + "startGraph=start\n" + propertyLine + "\n");
        return Groove.loadGrammar(dir.toString());
    }

    /** GXL source of an empty host graph named {@code start}. */
    private static final String EMPTY_GRAPH = """
        <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
        <gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
            <graph edgemode="directed" edgeids="false" role="graph" id="start">
            </graph>
        </gxl>
        """;
}
