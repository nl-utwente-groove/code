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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.netbeans.jemmy.operators.JTreeOperator;

import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.gui.SimulatorListener;
import nl.utwente.groove.gui.SimulatorModel.Change;
import nl.utwente.groove.gui.display.DisplayKind;
import nl.utwente.groove.test.GuiTest;
import nl.utwente.groove.test.SlowTest;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.io.FileUtils;

/**
 * Asserts that selecting a resource in a name list brings its display to the
 * front, also when another listener fails on the same notification. The change
 * kinds are notified in enum order, so a listener failing on
 * {@link Change#HOST} used to keep the displays panel, which listens for the
 * {@link Change#DISPLAY} that comes later, from ever seeing the switch; the
 * model recorded the new display all the same, so every further selection of a
 * graph was a no-op that left the wrong display up.
 * <p>
 * Excluded from the default test run via the {@link SlowTest} and
 * {@link GuiTest} categories, and skipped in headless environments.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Opus 5, 2026-09")
@NonNullByDefault
@Tag(SlowTest.TAG)
@Tag(GuiTest.TAG)
@ExtendWith(SimulatorFixture.class)
public class DisplaySwitchGuiTest {
    /** Location of the fixture grammar; it has more than one host graph. */
    private static final String GRAMMAR = "junit/samples/leader-election.gps";

    /** Clicking a rule and a graph in the name lists switches the display. */
    @Test
    void clickSwitchesDisplay() throws Exception {
        loadGrammar(copyGrammar());
        JTreeOperator rules = tree(DisplayKind.RULE);
        JTreeOperator hosts = tree(DisplayKind.HOST);
        for (int host : leaves(hosts)) {
            click(rules, leaves(rules).get(0));
            click(hosts, host);
            assertEquals(DisplayKind.HOST, shown(), "after selecting a graph");
        }
    }

    /** A failing listener must not keep the graph display from coming up. */
    @Test
    void switchSurvivesFailingListener() throws Exception {
        loadGrammar(copyGrammar());
        List<QualName> graphs
            = new ArrayList<>(getModel().getGrammar().getNames(ResourceKind.HOST));
        SimulatorListener failing = (source, oldModel, changes) -> {
            throw new IllegalStateException("broken listener");
        };
        getModel().addListener(failing, Change.HOST);
        try {
            // select a graph, then move the display away from the graph display
            select(graphs.get(0));
            SwingUtilities.invokeAndWait(() -> getModel().setDisplay(DisplayKind.RULE));
            assertEquals(DisplayKind.RULE, shown());
            // selecting another graph must bring the graph display back up
            select(graphs.get(1));
            assertEquals(DisplayKind.HOST, shown());
        } finally {
            getModel().removeListener(failing);
        }
    }

    /**
     * Selects a host graph on the event dispatch thread, absorbing the failure
     * of the deliberately broken listener of
     * {@link #switchSurvivesFailingListener()}, which the fixture would
     * otherwise report as an uncaught exception.
     */
    private void select(QualName graph) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            try {
                getModel().doSelect(ResourceKind.HOST, graph);
            } catch (IllegalStateException exc) {
                // thrown by the deliberately broken listener
            }
        });
    }

    /** Returns the display kind currently on top of the displays panel. */
    private DisplayKind shown() {
        return simulator().getDisplaysPanel().getSelectedDisplay().getKind();
    }

    /** Clicks on a given row of a tree. */
    private void click(JTreeOperator tree, int row) {
        tree.clickOnPath(tree.getPathForRow(row));
    }

    /** Returns the leaf rows of a tree. */
    private List<Integer> leaves(JTreeOperator tree) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < tree.getRowCount(); i++) {
            TreePath path = tree.getPathForRow(i);
            if (((JTree) tree.getSource()).getModel().isLeaf(path.getLastPathComponent())) {
                result.add(i);
            }
        }
        return result;
    }

    /** Returns an operator on the name list of a given display kind. */
    private JTreeOperator tree(DisplayKind kind) {
        var listPanel = simulator().getDisplaysPanel().getDisplay(kind).getListPanel();
        assert listPanel != null;
        return new JTreeOperator((JTree) listPanel.getList());
    }

    /** Copies the fixture grammar to a scratch directory. */
    private Path copyGrammar() throws IOException {
        var tmp = this.tmp;
        assert tmp != null; // injected by JUnit
        Path result = tmp.resolve("leader-election.gps");
        FileUtils.copyDirectory(new File(GRAMMAR), result.toFile(), false);
        return result;
    }

    /** Scratch directory for the grammar copies. */
    @TempDir
    private @Nullable Path tmp;
}
