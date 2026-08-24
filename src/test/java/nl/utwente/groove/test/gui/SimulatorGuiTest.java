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
package nl.utwente.groove.test.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.lang.Thread.UncaughtExceptionHandler;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;

import javax.swing.SwingUtilities;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.TestOut;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.operators.JFileChooserOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JMenuBarOperator;
import org.netbeans.jemmy.operators.JTreeOperator;

import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.grammar.model.RuleModel;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.SimulatorModel;
import nl.utwente.groove.gui.tree.RuleTree;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.test.GuiTest;
import nl.utwente.groove.test.SlowTest;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.io.FileUtils;

/**
 * GUI smoke tests: launch the real Simulator and drive it through the
 * Jemmy 2 UI robot, asserting on the {@link SimulatorModel} state that
 * results. The Simulator is launched once for the whole class (it is a
 * one-instance-per-JVM design: {@link nl.utwente.groove.gui.UserSettings}
 * guards against double initialisation); each test starts by loading a
 * fresh scratch copy of the ferryman grammar, so tests do not see each
 * other's edits. Uncaught exceptions on the event dispatch thread fail the
 * test that provoked them.
 * <p>
 * The tests target menus, trees and dialogs only: the JGraph canvas is not
 * widget-addressable. Actions that block the event dispatch thread behind a
 * modal dialog (exploration, dialogs, file choosers) are pushed with
 * {@code pushMenuNoBlock}, with completion awaited on the simulator model
 * rather than on the UI.
 * <p>
 * Excluded from the default test run via the {@link SlowTest} and
 * {@link GuiTest} categories, and skipped in headless environments.
 * Requires the in-memory preference isolation set up by the surefire
 * {@code argLine} (see {@link InMemoryPreferencesFactory}).
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
@NonNullByDefault
@Tag(SlowTest.TAG)
@Tag(GuiTest.TAG)
public class SimulatorGuiTest {
    /** System property from which {@link java.util.prefs.Preferences} takes its factory. */
    private static final String PREFS_FACTORY_PROPERTY = "java.util.prefs.PreferencesFactory";

    static {
        // normally set on the JVM command line (surefire argLine, Eclipse
        // launch); setting it here as well covers runners that omit it,
        // provided nothing has touched java.util.prefs yet
        if (System.getProperty(PREFS_FACTORY_PROPERTY) == null) {
            System
                .setProperty(PREFS_FACTORY_PROPERTY, InMemoryPreferencesFactory.class.getName());
        }
    }

    /** Directory containing the sample grammars used as fixtures. */
    private static final String SAMPLES = "junit/samples";
    /** Grammar every test starts on (scratch copy). */
    private static final String FIRST_GRAMMAR = "ferryman.gps";
    /** Grammar loaded on top of the first one (scratch copy). */
    private static final String SECOND_GRAMMAR = "mergers.gps";
    /** Known state count of the fully explored {@link #FIRST_GRAMMAR}. */
    private static final int FERRYMAN_STATES = 114;
    /** Known transition count of the fully explored {@link #FIRST_GRAMMAR}. */
    private static final int FERRYMAN_TRANSITIONS = 198;
    /** Poll timeout for {@link #waitFor(String, BooleanSupplier)}, in ms. */
    private static final long TIMEOUT = 60_000;

    /**
     * Checks the environment, quiets Jemmy's default stdout chatter and
     * launches the Simulator (once for the whole class).
     */
    @BeforeAll
    static void launchSimulator() throws Exception {
        assumeFalse(GraphicsEnvironment.isHeadless(), "GUI tests need a display");
        assertTrue(InMemoryPreferencesFactory.isInstalled(),
                   "Preferences are not isolated: run with -D" + PREFS_FACTORY_PROPERTY + "="
                       + InMemoryPreferencesFactory.class.getName()
                       + " (plus the --add-exports to java.prefs), as the surefire argLine"
                       + " and the 'GROOVE - all JUnit tests' launch do");
        JemmyProperties.setCurrentOutput(TestOut.getNullOutput());
        oldHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> EDT_ERRORS.add(e));
        SwingUtilities.invokeAndWait(() -> {
            var sim = new Simulator();
            simulator = sim;
            sim.start();
        });
        frame = new JFrameOperator("Production Simulator");
    }

    /** Disposes the Simulator frame and restores the exception handler. */
    @AfterAll
    static void disposeSimulator() throws Exception {
        var sim = simulator;
        if (sim != null) {
            SwingUtilities.invokeAndWait(() -> sim.getFrame().dispose());
        }
        Thread.setDefaultUncaughtExceptionHandler(oldHandler);
    }

    /** Loads a fresh scratch copy of {@link #FIRST_GRAMMAR} into the Simulator. */
    @BeforeEach
    void loadFirstGrammar() throws Exception {
        EDT_ERRORS.clear();
        loadGrammar(copyGrammar(FIRST_GRAMMAR));
    }

    /** Fails on uncaught EDT exceptions provoked by the test. */
    @AfterEach
    void checkEventThreadErrors() {
        assertEquals(List.of(), EDT_ERRORS, "uncaught exceptions on the event dispatch thread");
    }

    /**
     * Loads a grammar directory into the running Simulator, through the same
     * (synchronous) action the File menu uses, but without the file chooser.
     */
    private void loadGrammar(Path grammarDir) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            try {
                simulator().getActions().getLoadGrammarAction().load(grammarDir.toFile());
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        });
        var store = getModel().getStore();
        assertNotNull(store);
        assertEquals(grammarDir.toFile(), store.getLocation());
    }

    /**
     * Pushes Explore &gt; Explore State Space through the menu bar and asserts
     * the resulting GTS against the known state space size.
     */
    @Test
    void exploreStateSpace() throws InterruptedException {
        assertTrue(ruleTree().getRowCount() > 0, "rule tree is empty");
        // the wait condition below relies on no exploration being recorded yet
        assertNull(getModel().getLastExploreType());
        // no-block: the explore action monopolises the EDT behind a modal
        // progress dialog until exploration finishes
        new JMenuBarOperator(frame()).pushMenuNoBlock("Explore|Explore State Space");
        // the last explore type is set at the very end of the explore action,
        // after the exploration has finished and the progress dialog has closed
        waitFor("exploration to finish", () -> getModel().getLastExploreType() != null);
        GTS gts = getModel().getGTS();
        assertNotNull(gts);
        assertEquals(FERRYMAN_STATES, gts.nodeCount());
        assertEquals(FERRYMAN_TRANSITIONS, gts.edgeCount());
    }

    /**
     * Loads a second grammar through File &gt; Load Grammar and the file
     * chooser dialog, and asserts that the rule tree shows the new grammar's
     * rules (regression test for the NPE and stale rule tree on grammar
     * switch, fixed in 8637c7cf1).
     */
    @Test
    void loadSecondGrammar() throws IOException, InterruptedException {
        Path secondDir = copyGrammar(SECOND_GRAMMAR);
        assertTrue(ruleTree().getRowCount() > 0, "rule tree is empty");
        new JMenuBarOperator(frame()).pushMenuNoBlock("File|Load Grammar");
        new JFileChooserOperator().chooseFile(secondDir.toAbsolutePath().toString());
        waitFor("second grammar to load", () -> {
            var store = getModel().getStore();
            return store != null && SECOND_GRAMMAR.equals(store.getLocation().getName());
        });
        waitFor("rule tree to show the second grammar",
                () -> treeRows(ruleTree()).stream().anyMatch(row -> row.contains("merge")));
    }

    /**
     * Opens the exploration configuration dialog through the menu and closes
     * it again through its Cancel button.
     */
    @Test
    void openAndCloseExplorationDialog() {
        new JMenuBarOperator(frame()).pushMenuNoBlock("Explore|Customize Exploration");
        JDialogOperator dialog = new JDialogOperator("Customize Exploration");
        new JButtonOperator(dialog, "Cancel").push();
        dialog.waitClosed();
    }

    /**
     * Performs a grammar edit (raising a rule's priority) through the Edit
     * menu, then undoes and redoes it through the same menu, checking the
     * rule's priority in the grammar model after every step.
     */
    @Test
    void undoRedoPriorityEdit() throws Exception {
        QualName rule = QualName.parse("load");
        // deterministic rule selection: driven on the model, since the menu
        // actions under test operate on whatever rule is selected
        SwingUtilities.invokeAndWait(() -> getModel().doSelect(ResourceKind.RULE, rule));
        assertEquals(0, priority(rule));
        JMenuBarOperator menus = new JMenuBarOperator(frame());
        menus.pushMenuNoBlock("Edit|Raise Priority");
        waitFor("priority to be raised", () -> priority(rule) > 0);
        int raised = priority(rule);
        menus.pushMenuNoBlock("Edit|Undo");
        waitFor("undo to restore the priority", () -> priority(rule) == 0);
        menus.pushMenuNoBlock("Edit|Redo");
        waitFor("redo to raise the priority again", () -> priority(rule) == raised);
    }

    /** Copies a sample grammar into the scratch directory and returns the copy. */
    private Path copyGrammar(String name) throws IOException {
        Path result = tmp().resolve(name);
        FileUtils.copyDirectory(new File(SAMPLES, name), result.toFile(), false);
        return result;
    }

    /** Returns the running Simulator. */
    private static Simulator simulator() {
        var result = simulator;
        assert result != null; // launched in the class setup
        return result;
    }

    /** Returns the simulator model of the running Simulator. */
    private static SimulatorModel getModel() {
        return simulator().getModel();
    }

    /** Returns the operator for the Simulator frame. */
    private static JFrameOperator frame() {
        var result = frame;
        assert result != null; // created in the class setup
        return result;
    }

    /** Returns the scratch directory. */
    private Path tmp() {
        var result = this.tmp;
        assert result != null; // injected by JUnit
        return result;
    }

    /** Finds the rule tree in the Simulator frame. */
    private JTreeOperator ruleTree() {
        return new JTreeOperator(frame(), new ComponentChooser() {
            // no default annotation: the inherited method's parameter is unconstrained
            @Override
            @NonNullByDefault({})
            public boolean checkComponent(Component comp) {
                return comp instanceof RuleTree;
            }

            @Override
            public String getDescription() {
                return "the Simulator's rule tree";
            }
        });
    }

    /** Returns the string renderings of all visible rows of a tree. */
    private List<String> treeRows(JTreeOperator tree) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < tree.getRowCount(); i++) {
            result.add(String.valueOf(tree.getPathForRow(i).getLastPathComponent()));
        }
        return result;
    }

    /** Returns the current priority of a given rule, or {@code -1} if the
     * grammar or rule is (transiently) unavailable. */
    private int priority(QualName rule) {
        var grammar = getModel().getGrammar();
        var ruleModel = grammar == null
            ? null
            : grammar.getGraphResource(ResourceKind.RULE, rule);
        return ruleModel instanceof RuleModel r
            ? r.getPriority()
            : -1;
    }

    /** Polls a condition until it holds, failing after {@link #TIMEOUT} ms. */
    private static void waitFor(String description,
                                BooleanSupplier condition) throws InterruptedException {
        long deadline = System.currentTimeMillis() + TIMEOUT;
        while (!condition.getAsBoolean()) {
            if (System.currentTimeMillis() > deadline) {
                fail("Timed out waiting for " + description);
            }
            Thread.sleep(100);
        }
    }

    /** Scratch directory for the grammar copies; injected by JUnit per test. */
    @TempDir
    @Nullable
    Path tmp;

    /** The Simulator under test; launched once, in the class setup. */
    private static @Nullable Simulator simulator;
    /** Operator for the Simulator frame. */
    private static @Nullable JFrameOperator frame;
    /** Uncaught EDT exceptions collected during the tests. */
    private static final List<Throwable> EDT_ERRORS
        = Collections.synchronizedList(new ArrayList<>());
    /** Default uncaught-exception handler to restore after the class. */
    private static @Nullable UncaughtExceptionHandler oldHandler;
}
