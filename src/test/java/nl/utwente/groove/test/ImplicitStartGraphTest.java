/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
 * University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id$
 */
package nl.utwente.groove.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.io.store.SystemStore;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.Version;

/**
 * Regression test for the implicit start graph of old grammars. A grammar
 * from before version 3.2 has no {@code startGraph} property; the graph
 * named {@code start} is its start graph by convention, which the grammar
 * model implements as a local override of the active host names. Loading
 * such a grammar in the Simulator upgrades and resaves it, and the resaved
 * grammar used to come back without any active start graph, because the
 * override never reached the properties file.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public class ImplicitStartGraphTest {
    /** Version 3.0 grammar without a {@code startGraph} property. */
    private static final String GRAMMAR = "junit/samples/ferryman.gps";
    private static final QualName START = QualName.parse("start");

    private Path tempDir = Path.of("");

    @BeforeEach
    void createTempDir() throws IOException {
        this.tempDir = Files.createTempDirectory("groove-start-graph");
    }

    @AfterEach
    void deleteTempDir() throws IOException {
        try (Stream<Path> paths = Files.walk(this.tempDir)) {
            paths.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }
    }

    /** The loaded grammar implies the start graph, without storing it. */
    @Test
    void startGraphIsImpliedOnLoad() throws IOException {
        GrammarModel grammar = load(new File(GRAMMAR));
        assertTrue(Version
            .compareGrammarVersions(grammar.getProperties().getGrammarVersion(),
                                    Version.GRAMMAR_VERSION_3_2) < 0,
                   "fixture is not a pre-3.2 grammar");
        assertNotNull(grammar.getLocalActiveNames(ResourceKind.HOST), "no local override");
        assertTrue(grammar.getProperties().getActiveNames(ResourceKind.HOST).isEmpty(),
                   "fixture stores a start graph");
        assertActiveStart(grammar);
    }

    /** After the upgrade and a resave, the start graph is stored and still active. */
    @Test
    void startGraphSurvivesUpgradeAndResave() throws IOException {
        SystemStore store = SystemStore.newStore(new File(GRAMMAR), false, true);
        GrammarModel grammar = store.toGrammarModel();
        grammar.upgradeProperties();
        assertEquals(Version.getCurrentGrammarVersion(),
                     grammar.getProperties().getGrammarVersion());
        assertActiveStart(grammar);
        File target = this.tempDir.resolve("ferryman.gps").toFile();
        store.save(target, false);
        GrammarModel resaved = load(target);
        assertEquals(java.util.Set.of(START),
                     resaved.getProperties().getActiveNames(ResourceKind.HOST),
                     "start graph not stored by the resave");
        assertActiveStart(resaved);
    }

    private GrammarModel load(File file) throws IOException {
        return SystemStore.newStore(file, false, true).toGrammarModel();
    }

    private void assertActiveStart(GrammarModel grammar) {
        assertTrue(grammar.getActiveNames(ResourceKind.HOST).contains(START), "start not active");
        var startGraph = grammar.getStartGraphModel();
        assertNotNull(startGraph);
        assertFalse(startGraph.hasErrors(), "start graph has errors: " + startGraph.getErrors());
        assertEquals(START.toString(), startGraph.getName());
    }
}
