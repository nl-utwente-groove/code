/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2026
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
package nl.utwente.groove.test.gui;

import static nl.utwente.groove.test.gui.SimulatorFixture.getModel;
import static nl.utwente.groove.test.gui.SimulatorFixture.loadGrammar;
import static nl.utwente.groove.test.gui.SimulatorFixture.simulator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import javax.swing.SwingUtilities;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.test.GuiTest;
import nl.utwente.groove.test.SlowTest;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.io.FileUtils;

/**
 * Regression test for the start graph handling of Save Grammar As.
 * The action used to pin the stored start graph on the new grammar model
 * as an external one, after which edits of the start graph no longer reached
 * the grammar (and Refresh did not help either).
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
@Tag(SlowTest.TAG)
@Tag(GuiTest.TAG)
@ExtendWith(SimulatorFixture.class)
public class SaveGrammarAsGuiTest {
    private static final String GRAMMAR = "junit/samples/inheritance.gps";

    @Test
    void startGraphEditSurvivesSaveAs() throws Exception {
        loadGrammar(copyGrammar("inheritance.gps"));
        File target = resolve("inheritance-copy.gps").toFile();
        SwingUtilities.invokeAndWait(() -> {
            try {
                assertTrue(simulator().getActions().getSaveGrammarAction().save(target, true));
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        });
        var store = getModel().getStore();
        assertEquals(target, store.getLocation());
        var grammar = getModel().getGrammar();
        assertFalse(grammar.getStartGraphModel().isExternal(),
                   "Save As pinned an external start graph on the new grammar");
        assertFalse(grammar.toGrammar().getStartGraph().nodeSet().isEmpty());
        // replace the stored start graph by an empty one, as a save in the editor does
        AspectGraph empty = AspectGraph.emptyGraph("start", GraphRole.HOST, true);
        SwingUtilities.invokeAndWait(() -> {
            try {
                getModel().doAddGraph(ResourceKind.HOST, empty, false);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        });
        assertTrue(getModel().getGrammar().toGrammar().getStartGraph().nodeSet().isEmpty(),
                   "start graph edit did not reach the grammar");
        SwingUtilities.invokeAndWait(() -> {
            try {
                getModel().doRefreshGrammar();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        });
        assertTrue(getModel().getGrammar().toGrammar().getStartGraph().nodeSet().isEmpty(),
                   "start graph edit did not survive a refresh");
    }

    /**
     * The file proposed by the Save As dialog used to be the one last saved
     * through the chooser, which lagged behind the loaded grammar because
     * Load Grammar sets its file on a different chooser (the one that also
     * accepts archives).
     */
    @Test
    void proposedFileFollowsLoadedGrammar() throws Exception {
        for (String name : new String[] {"first.gps", "second.gps"}) {
            Path grammar = copyGrammar(name);
            loadGrammar(grammar);
            SwingUtilities.invokeAndWait(() -> {
                var chooser = simulator().getActions().getSaveGrammarAction().prepareFileChooser();
                assertEquals(grammar.toFile(), chooser.getSelectedFile(),
                             "Save As does not propose the name of the loaded grammar");
            });
        }
    }

    private Path copyGrammar(String name) throws IOException {
        Path result = resolve(name);
        FileUtils.copyDirectory(new File(GRAMMAR), result.toFile(), false);
        return result;
    }

    private Path resolve(String name) {
        var tmp = this.tmp;
        assert tmp != null; // injected by JUnit
        return tmp.resolve(name);
    }

    @TempDir
    private @Nullable Path tmp;
}
