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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Rectangle;
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
 * front: for a click anywhere on the row, and also when another listener fails
 * on the same notification.
 * <p>
 * Two defects are covered. Only the rendered label counted as a click on the
 * entry, while the look and feel paints the selection across the whole row and
 * the tree's own selection handling accepts the whole row as well; a click
 * beside the name therefore switched the display only in so far as it moved
 * the selection, which it cannot do on a list of one graph. And the change
 * kinds are notified in enum order, so a listener failing on
 * {@link Change#HOST} kept the displays panel, which listens for the
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
    private static final String GRAMMAR = "leader-election";
    /** Fixture grammar with a single host graph, as in the bug report. */
    private static final String SINGLE_GRAPH_GRAMMAR = "ferryman";

    /** Clicking a rule and a graph in the name lists switches the display. */
    @Test
    void clickSwitchesDisplay() throws Exception {
        loadGrammar(copyGrammar(GRAMMAR));
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
        loadGrammar(copyGrammar(GRAMMAR));
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
     * A click next to the name, on an entry that is already selected, must
     * switch the display as well. The look and feel paints the selection
     * across the whole row, but only the rendered label used to count as a
     * click on the entry; with a single graph in the list the selection can
     * never change, so such a click did nothing whatsoever.
     */
    @Test
    void clickBesideNameSwitchesDisplay() throws Exception {
        loadGrammar(copyGrammar(SINGLE_GRAPH_GRAMMAR));
        JTreeOperator hosts = tree(DisplayKind.HOST);
        JTreeOperator rules = tree(DisplayKind.RULE);
        // loading the grammar selects the single graph and one of the rules,
        // so neither click below can change a selection
        SwingUtilities.invokeAndWait(() -> getModel().setDisplay(DisplayKind.RULE));
        clickBesideName(hosts, getModel().getSelected(ResourceKind.HOST));
        assertEquals(DisplayKind.HOST, shown(), "after clicking beside the graph name");
        SwingUtilities.invokeAndWait(() -> getModel().setDisplay(DisplayKind.HOST));
        clickBesideName(rules, getModel().getSelected(ResourceKind.RULE));
        assertEquals(DisplayKind.RULE, shown(), "after clicking beside the rule name");
    }

    /**
     * A click in a name list that brings up another display leaves the focus
     * in the list, so that the entry clicked is rendered as actively selected.
     * The display used to take the focus on every switch, and the tree
     * renderer shows the active selection colours only while the list owns
     * the focus.
     */
    @Test
    @AIGenerated("Claude Fable 5.1, 2026-09")
    void clickKeepsFocusInNameList() throws Exception {
        loadGrammar(copyGrammar(GRAMMAR));
        JTreeOperator rules = tree(DisplayKind.RULE);
        JTreeOperator hosts = tree(DisplayKind.HOST);
        // a rule is displayed, then a graph other than the selected one is chosen
        click(rules, leaves(rules).get(0));
        SwingUtilities.invokeAndWait(() -> getModel().setDisplay(DisplayKind.RULE));
        List<Integer> graphs = leaves(hosts);
        click(hosts, graphs.get(graphs.size() - 1));
        assertEquals(DisplayKind.HOST, shown(), "after clicking the graph name");
        // the list owns the focus right after the click; the display coming up
        // used to take it away through queued requests, so let those settle
        Thread.sleep(500);
        assertTrue(hosts.getSource().isFocusOwner(), "the graph list has lost the focus");
    }

    /**
     * The state list switches the display like the name lists do, and shares
     * their hit test and focus handling: a click beside the label of its
     * (already selected) state entry brings the state display up and leaves
     * the focus in the list.
     */
    @Test
    @AIGenerated("Claude Fable 5.1, 2026-09")
    void stateListClickBesideName() throws Exception {
        loadGrammar(copyGrammar(GRAMMAR));
        JTreeOperator states = tree(DisplayKind.STATE);
        // bring the state list to the front of its lists panel, and another
        // display than the state display to the front of the displays panel
        SwingUtilities.invokeAndWait(() -> {
            var listPanel = simulator().getDisplaysPanel().getDisplay(DisplayKind.STATE).getListPanel();
            simulator().getDisplaysPanel().getUpperListsPanel().setSelectedComponent(listPanel);
            getModel().setDisplay(DisplayKind.RULE);
        });
        assertTrue(states.getRowCount() > 0, "state list is empty");
        clickBesideRow(states, 0);
        assertEquals(DisplayKind.STATE, shown(), "after clicking beside the state entry");
        Thread.sleep(500);
        assertTrue(states.getSource().isFocusOwner(), "the state list has lost the focus");
    }

    /**
     * Clicks on the row of a named resource, to the right of its rendered
     * label.
     */
    private void clickBesideName(JTreeOperator tree, QualName name) {
        int row = -1;
        for (int i = 0; row < 0 && i < tree.getRowCount(); i++) {
            if (name.toString().equals(String.valueOf(tree.getPathForRow(i).getLastPathComponent()))) {
                row = i;
            }
        }
        assertTrue(row >= 0, "no row for " + name);
        clickBesideRow(tree, row);
    }

    /** Clicks on a given row of a tree, to the right of its rendered label. */
    private void clickBesideRow(JTreeOperator tree, int row) {
        Rectangle bounds = tree.getRowBounds(row);
        tree.clickMouse(bounds.x + bounds.width + 20, bounds.y + bounds.height / 2, 1);
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

    /** Copies a fixture grammar to a scratch directory. */
    private Path copyGrammar(String name) throws IOException {
        var tmp = this.tmp;
        assert tmp != null; // injected by JUnit
        Path result = tmp.resolve(name + ".gps");
        FileUtils.copyDirectory(new File("junit/samples/" + name + ".gps"), result.toFile(), false);
        return result;
    }

    /** Scratch directory for the grammar copies. */
    @TempDir
    private @Nullable Path tmp;
}
