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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.prefs.Preferences;

import javax.swing.SwingUtilities;

import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.TestOut;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JMenuBarOperator;
import org.netbeans.jemmy.operators.JTreeOperator;

import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.test.SlowTest;
import nl.utwente.groove.util.AIGenerated;

/**
 * Spike test assessing whether the Jemmy 2 library (as republished by
 * Apache NetBeans) can drive the GROOVE Simulator on the current JDK:
 * launches the Simulator on a scratch copy of the ferryman grammar,
 * pushes Explore &gt; Explore State Space through the menu bar, and
 * asserts the resulting GTS against the known state space size.
 * <p>
 * Requires a display; excluded from the default test run via the
 * {@link SlowTest} category.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
@Category(SlowTest.class)
public class JemmySpikeTest {
    static {
        // must run before any class touches java.util.prefs;
        // the factory is captured on first use of Preferences
        System
            .setProperty("java.util.prefs.PreferencesFactory",
                         InMemoryPreferencesFactory.class.getName());
    }

    private static final String FIXTURE = "junit/samples/ferryman.gps";
    private static final int FERRYMAN_STATES = 114;
    private static final int FERRYMAN_TRANSITIONS = 198;

    private static Path tempDir;
    private static Simulator simulator;

    @BeforeClass
    public static void launchSimulator() throws Exception {
        Assume.assumeFalse("Needs a display", GraphicsEnvironment.isHeadless());
        // guard against preference leakage to the real user store:
        // if another test initialised Preferences first, abort
        assertTrue("Preferences factory not isolated; run this test in its own JVM",
                   Preferences.userRoot().getClass().getName().contains("InMemoryPreferences"));
        // work on a scratch copy: the Simulator saves into its grammar dir
        tempDir = Files.createTempDirectory("jemmy-spike");
        Path grammarDir = tempDir.resolve("ferryman.gps");
        Files.createDirectory(grammarDir);
        for (File file : new File(FIXTURE).listFiles()) {
            if (file.isFile()) {
                Files.copy(file.toPath(), grammarDir.resolve(file.getName()));
            }
        }
        // quiet Jemmy's default stdout chatter
        JemmyProperties.setCurrentOutput(TestOut.getNullOutput());
        SwingUtilities.invokeAndWait(() -> {
            simulator = new Simulator(grammarDir.toString());
            simulator.start();
        });
    }

    @AfterClass
    public static void disposeSimulator() throws Exception {
        if (simulator != null) {
            SwingUtilities.invokeAndWait(() -> simulator.getFrame().dispose());
        }
        if (tempDir != null) {
            try (var walk = Files.walk(tempDir)) {
                walk
                    .sorted((p, q) -> q.getNameCount() - p.getNameCount())
                    .forEach(p -> p.toFile().delete());
            }
        }
    }

    @Test
    public void testExploreThroughMenu() throws Exception {
        // find the frame by title substring
        JFrameOperator frame = new JFrameOperator("Production Simulator");
        // the grammar is loaded asynchronously after construction
        waitFor("grammar to load", () -> simulator.getModel().getGrammar() != null);
        // the rule tree should be populated; proves non-menu component lookup
        JTreeOperator ruleTree = new JTreeOperator(frame);
        assertTrue("rule tree is empty", ruleTree.getRowCount() > 0);
        // no-block: the explore action monopolises the EDT behind a modal
        // progress dialog until exploration finishes
        new JMenuBarOperator(frame).pushMenuNoBlock("Explore|Explore State Space");
        waitFor("exploration to finish", () -> {
            GTS gts = simulator.getModel().getGTS();
            return gts != null && gts.nodeCount() == FERRYMAN_STATES;
        });
        GTS gts = simulator.getModel().getGTS();
        assertNotNull(gts);
        assertEquals(FERRYMAN_STATES, gts.nodeCount());
        assertEquals(FERRYMAN_TRANSITIONS, gts.edgeCount());
    }

    /** Polls a condition until it holds, failing after a 30s timeout. */
    private static void waitFor(String description,
                                java.util.function.BooleanSupplier condition)
        throws InterruptedException {
        long deadline = System.currentTimeMillis() + 30_000;
        while (!condition.getAsBoolean()) {
            if (System.currentTimeMillis() > deadline) {
                fail("Timed out waiting for " + description);
            }
            Thread.sleep(100);
        }
    }
}
