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

import static nl.utwente.groove.test.gui.SimulatorFixture.frame;
import static nl.utwente.groove.test.gui.SimulatorFixture.getModel;
import static nl.utwente.groove.test.gui.SimulatorFixture.loadGrammar;
import static nl.utwente.groove.test.gui.SimulatorFixture.waitFor;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Component;
import java.awt.Container;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.gui.tree.TypeTree;
import nl.utwente.groove.test.GuiTest;
import nl.utwente.groove.test.SlowTest;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.Unicode;
import nl.utwente.groove.util.io.FileUtils;

/**
 * Asserts that the Labels panel of the Type display does not decorate its
 * entries with occurrence counts, which are always 1 for type graph elements
 * (regression test for gh #879), while the Host display's Labels panel keeps
 * showing them. Uses the inheritance sample, which has a type graph with
 * three node types and an edge type, and a start graph.
 * <p>
 * Excluded from the default test run via the {@link SlowTest} and
 * {@link GuiTest} categories, and skipped in headless environments.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
@Tag(SlowTest.TAG)
@Tag(GuiTest.TAG)
@ExtendWith(SimulatorFixture.class)
public class LabelCountGuiTest {
    /** Location of the fixture grammar. */
    private static final String GRAMMAR = "junit/samples/inheritance.gps";

    /** The type display's label tree must show no occurrence counts. */
    @Test
    void typeDisplayHidesCounts() throws Exception {
        loadGrammar(copyGrammar());
        List<String> rows = labelRows(ResourceKind.TYPE, "type", GraphRole.TYPE);
        assertFalse(rows.isEmpty(), "type label tree is empty");
        assertTrue(rows.stream().noneMatch(LabelCountGuiTest::hasCount),
                   "type label tree shows occurrence counts: " + rows);
    }

    /** The host display's label tree must still show occurrence counts. */
    @Test
    void hostDisplayShowsCounts() throws Exception {
        loadGrammar(copyGrammar());
        List<String> rows = labelRows(ResourceKind.HOST, "start", GraphRole.HOST);
        assertFalse(rows.isEmpty(), "host label tree is empty");
        assertTrue(rows.stream().anyMatch(LabelCountGuiTest::hasCount),
                   "host label tree shows no occurrence counts: " + rows);
    }

    /** Tests if a rendered tree row carries the count suffix. */
    private static boolean hasCount(String row) {
        return row.indexOf(Unicode.TIMES) >= 0;
    }

    /**
     * Selects a named resource of a given kind in the Simulator and returns
     * the rendered rows of the label tree of the corresponding display,
     * identified by the graph role of the JGraph it belongs to.
     */
    private List<String> labelRows(ResourceKind kind, String name,
                                   GraphRole role) throws Exception {
        SwingUtilities.invokeAndWait(() -> getModel().doSelect(kind, QualName.parse(name)));
        waitFor("label tree of the " + role + " display to show", () -> {
            TypeTree tree = findLabelTree(frame().getSource(), role);
            return tree != null && tree.isShowing() && tree.getRowCount() > 0;
        });
        List<String> result = new ArrayList<>();
        SwingUtilities.invokeAndWait(() -> {
            TypeTree tree = findLabelTree(frame().getSource(), role);
            assert tree != null; // established by the wait above
            tree.synchroniseModel();
            for (int i = 0; i < tree.getRowCount(); i++) {
                Object node = tree.getPathForRow(i).getLastPathComponent();
                result.add(tree.convertValueToText(node, false, false, false, i, false));
            }
        });
        return result;
    }

    /** Recursively searches a component tree for the label tree whose
     * canvas has a given graph role. */
    private @Nullable TypeTree findLabelTree(Component comp, GraphRole role) {
        if (comp instanceof TypeTree result && result.getCanvas().hasGraphRole(role)) {
            return result;
        }
        if (comp instanceof Container container) {
            for (Component child : container.getComponents()) {
                TypeTree result = findLabelTree(child, role);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    /** Copies the fixture grammar to a scratch directory. */
    private Path copyGrammar() throws IOException {
        var tmp = this.tmp;
        assert tmp != null; // injected by JUnit
        Path result = tmp.resolve("inheritance.gps");
        FileUtils.copyDirectory(new File(GRAMMAR), result.toFile(), false);
        return result;
    }

    /** Scratch directory for the grammar copies. */
    @TempDir
    private @Nullable Path tmp;
}
