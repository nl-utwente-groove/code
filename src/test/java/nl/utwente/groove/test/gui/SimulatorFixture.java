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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;

import javax.swing.SwingUtilities;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.TestOut;
import org.netbeans.jemmy.operators.JFrameOperator;

import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.SimulatorModel;
import nl.utwente.groove.util.AIGenerated;

/**
 * JUnit extension owning the shared Simulator instance of the GUI test
 * suite. The Simulator is a one-instance-per-JVM design (its constructor
 * guards against a second instance), so GUI test classes must not launch
 * their own; instead they declare {@code @ExtendWith(SimulatorFixture.class)}
 * and access the shared instance through the static methods of this class.
 * The first test class to run launches the Simulator; every later class
 * reuses it; the frame is disposed once, at the end of the whole test run
 * (the launch is registered as an auto-closed resource in the root
 * extension store). Since the Simulator is shared, every test should start
 * by loading a fresh scratch grammar copy (see {@link #loadGrammar(Path)}),
 * so that tests do not see each other's edits.
 * <p>
 * The fixture also checks the environment (a display is needed, and the
 * preference store must be the in-memory one, see
 * {@link InMemoryPreferencesFactory}) and collects uncaught exceptions on
 * the event dispatch thread, failing the test that provoked them.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
@NonNullByDefault
public class SimulatorFixture implements BeforeAllCallback, BeforeEachCallback, AfterEachCallback {
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

    /** Poll timeout for {@link #waitFor(String, BooleanSupplier)}, in ms. */
    private static final long TIMEOUT = 60_000;

    /**
     * Checks the environment and launches the shared Simulator if no earlier
     * GUI test class has done so already.
     */
    // the callback overrides carry no default annotation:
    // the inherited methods' parameters are unconstrained
    @Override
    @NonNullByDefault({})
    // no resource leak: the launch is owned and auto-closed by the extension store
    @SuppressWarnings("resource")
    public void beforeAll(ExtensionContext context) {
        assumeFalse(GraphicsEnvironment.isHeadless(), "GUI tests need a display");
        assertTrue(InMemoryPreferencesFactory.isInstalled(),
                   "Preferences are not isolated: run with -D" + PREFS_FACTORY_PROPERTY + "="
                       + InMemoryPreferencesFactory.class.getName()
                       + " (plus the --add-exports to java.prefs), as the surefire argLine"
                       + " and the 'GROOVE - all JUnit tests' launch do");
        // the root store makes the launch span all GUI test classes and
        // auto-closes it at the end of the whole test run
        context
            .getRoot()
            .getStore(Namespace.create(SimulatorFixture.class))
            .getOrComputeIfAbsent(Launch.class, k -> Launch.perform(), Launch.class);
    }

    /** Clears the uncaught EDT exceptions of any earlier test. */
    @Override
    @NonNullByDefault({})
    public void beforeEach(ExtensionContext context) {
        EDT_ERRORS.clear();
    }

    /** Fails on uncaught EDT exceptions provoked by the test. */
    @Override
    @NonNullByDefault({})
    public void afterEach(ExtensionContext context) {
        assertEquals(List.of(), EDT_ERRORS, "uncaught exceptions on the event dispatch thread");
    }

    /** Returns the shared Simulator. */
    public static Simulator simulator() {
        var result = simulator;
        assert result != null; // launched by the extension before any test runs
        return result;
    }

    /** Returns the simulator model of the shared Simulator. */
    public static SimulatorModel getModel() {
        return simulator().getModel();
    }

    /** Returns the operator for the Simulator frame. */
    public static JFrameOperator frame() {
        var result = frame;
        assert result != null; // created by the extension before any test runs
        return result;
    }

    /**
     * Loads a grammar directory into the shared Simulator, through the same
     * (synchronous) action the File menu uses, but without the file chooser.
     */
    public static void loadGrammar(Path grammarDir) throws Exception {
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

    /** Polls a condition until it holds, failing after {@link #TIMEOUT} ms. */
    public static void waitFor(String description,
                               BooleanSupplier condition) throws InterruptedException {
        long deadline = System.currentTimeMillis() + TIMEOUT;
        while (!condition.getAsBoolean()) {
            if (System.currentTimeMillis() > deadline) {
                fail("Timed out waiting for " + description);
            }
            Thread.sleep(100);
        }
    }

    /** The shared Simulator; launched by {@link Launch#perform()}. */
    private static @Nullable Simulator simulator;
    /** Operator for the Simulator frame. */
    private static @Nullable JFrameOperator frame;
    /** Uncaught EDT exceptions collected during the tests. */
    private static final List<Throwable> EDT_ERRORS
        = Collections.synchronizedList(new ArrayList<>());

    /**
     * The Simulator launch, as an extension-store resource: created at most
     * once per test run, closed automatically when the root extension
     * context is torn down at the end of the run.
     */
    private static class Launch implements AutoCloseable {
        Launch(@Nullable UncaughtExceptionHandler oldHandler) {
            this.oldHandler = oldHandler;
        }

        @Override
        public void close() throws Exception {
            var sim = simulator;
            if (sim != null) {
                SwingUtilities.invokeAndWait(() -> sim.getFrame().dispose());
            }
            simulator = null;
            frame = null;
            Thread.setDefaultUncaughtExceptionHandler(this.oldHandler);
        }

        /** Quiets Jemmy, installs the EDT exception collector and launches
         * the Simulator. */
        static Launch perform() {
            JemmyProperties.setCurrentOutput(TestOut.getNullOutput());
            var oldHandler = Thread.getDefaultUncaughtExceptionHandler();
            Thread.setDefaultUncaughtExceptionHandler((t, e) -> EDT_ERRORS.add(e));
            try {
                SwingUtilities.invokeAndWait(() -> {
                    var sim = new Simulator();
                    simulator = sim;
                    sim.start();
                });
            } catch (Exception e) {
                throw new IllegalStateException("Simulator launch failed", e);
            }
            frame = new JFrameOperator("Production Simulator");
            return new Launch(oldHandler);
        }

        /** Default uncaught-exception handler to restore on close. */
        private final @Nullable UncaughtExceptionHandler oldHandler;
    }
}
