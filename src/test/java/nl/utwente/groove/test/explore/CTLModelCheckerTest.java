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
package nl.utwente.groove.test.explore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.explore.CTLModelChecker;
import nl.utwente.groove.grammar.GrammarKey;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.io.store.SystemStore;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;

/**
 * Smoke tests for the {@link CTLModelChecker} command-line tool, which had no
 * test coverage at all: the whole pipeline of argument parsing, state-space
 * generation, formula checking against the GTS and outcome reporting.
 * The checking logic itself is covered by {@code CTLTest}; here the point is
 * that the CLI wiring works and its error paths reject bad input.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
@NonNullByDefault
public class CTLModelCheckerTest {
    /** Location of the fixture grammar. */
    static private final String GRAMMAR = "junit/samples/control.gps";

    /** A satisfied and a violated property on a grammar directory:
     * the tool generates the state space and reports both outcomes
     * without throwing. */
    @Test
    public void testCheckGrammar() throws Exception {
        new CTLModelChecker("-v", "0", "-ctl", "EF exit", "-ctl", "AG false", GRAMMAR).start();
    }

    /** A grammar directory is explored exhaustively, regardless of the
     * (partial) exploration saved with the grammar (gh #863): on the full
     * ferryman state space, {@code AF(eat)} is violated, whereas the linear
     * trace of the saved exploration happens to satisfy it. Passing the
     * grammar through the generator arguments instead keeps the saved
     * exploration in force. */
    @Test
    public void testSavedExplorationIgnored() throws Exception {
        File dir = Files.createTempDirectory("ctl-model-checker-test").toFile();
        dir.deleteOnExit();
        SystemStore store = SystemStore
            .newStore(new File("junit/samples/ferryman.gps"), false, true)
            .save(new File(dir, "ferryman.gps"), true);
        var properties = store.toGrammarModel().getProperties().clone();
        // the fixture predates explicit start graph names, and the implicit
        // default does not survive the version bump on saving the properties
        properties.setActiveNames(ResourceKind.HOST, List.of(QualName.name("start")));
        properties.setProperty(GrammarKey.EXPLORATION.getName(), "linear final 0");
        store.putProperties(properties);
        String grammar = store.getLocation().getPath();
        var outcome = new CTLModelChecker("-v", "0", "-ctl", "AF(eat)", grammar).start();
        assertNotNull(outcome);
        assertEquals(List.of(false), List.copyOf(outcome.values()));
        outcome = new CTLModelChecker("-v", "0", "-ctl", "AF(eat)", "-g", grammar).start();
        assertNotNull(outcome);
        assertEquals(List.of(true), List.copyOf(outcome.values()));
    }

    /** Without a model (neither a file name nor generator arguments),
     * the tool refuses to run. */
    @Test
    public void testMissingModel() {
        assertThrows(Exception.class,
                     () -> new CTLModelChecker("-v", "0", "-ctl", "true").start());
    }

    /** An unparseable CTL property is rejected during argument parsing. */
    @Test
    public void testBadFormula() {
        assertThrows(Exception.class,
                     () -> new CTLModelChecker("-v", "0", "-ctl", "EF (", GRAMMAR).start());
    }

    /** A property whose proposition matches nothing in the grammar is
     * rejected by the formula-model compatibility check. */
    @Test
    public void testUnknownProposition() {
        assertThrows(Exception.class,
                     () -> new CTLModelChecker("-v", "0", "-ctl", "EF no_such_rule", GRAMMAR)
                         .start());
    }
}
