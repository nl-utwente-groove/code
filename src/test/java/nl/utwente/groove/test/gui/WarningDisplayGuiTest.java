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

import static nl.utwente.groove.test.gui.SimulatorFixture.frame;
import static nl.utwente.groove.test.gui.SimulatorFixture.getModel;
import static nl.utwente.groove.test.gui.SimulatorFixture.loadGrammar;
import static nl.utwente.groove.test.gui.SimulatorFixture.waitFor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.awt.Component;
import java.awt.Container;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import javax.swing.SwingUtilities;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import nl.utwente.groove.gui.list.ListTabbedPane;
import nl.utwente.groove.test.GuiTest;
import nl.utwente.groove.test.SlowTest;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.io.FileUtils;
import nl.utwente.groove.util.parse.Severity;

/**
 * Asserts that non-blocking diagnostics (gh #885/#904) actually reach the
 * Simulator frame: the "Errors in grammar" results panel must appear and
 * list the warning, both when a grammar with warnings is loaded and when a
 * property edit on a loaded grammar first provokes the warning.
 * Uses the creatorNac fixture with the checkCreatorEdges property, whose
 * readerCreator rule then carries a compile-time warning.
 * <p>
 * Excluded from the default test run via the {@link SlowTest} and
 * {@link GuiTest} categories, and skipped in headless environments.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Opus 5, 2026-08")
@NonNullByDefault
@Tag(SlowTest.TAG)
@Tag(GuiTest.TAG)
@ExtendWith(SimulatorFixture.class)
public class WarningDisplayGuiTest {
    /** Location of the fixture grammar. */
    private static final String GRAMMAR = "junit/rules/creatorNac.gps";
    /** The guard property provoking the warning. */
    private static final String GUARD = "checkCreatorEdges";

    /** Loading a grammar with a rule warning must pop up the results panel
     * with the warning listed. */
    @Test
    void warningShownOnLoad() throws Exception {
        loadGrammar(copyGrammar(true));
        assertEquals(Severity.WARNING, getModel().getGrammar().getErrors().getSeverity());
        waitFor("grammar error list to show the warning", this::errorListShowing);
    }

    /** Setting the guard property on a loaded (warning-free) grammar must
     * make the warning appear; removing it again must clear the list. */
    @Test
    void warningShownOnPropertyChange() throws Exception {
        loadGrammar(copyGrammar(false));
        assertFalse(getModel().getGrammar().getErrors().getSeverity() == Severity.WARNING);
        setGuard(true);
        waitFor("warning to appear after property change", this::errorListShowing);
        setGuard(false);
        waitFor("warning to disappear after property change", () -> !errorListShowing());
    }

    /** Sets or resets the guard property through the same simulator-model
     * action the properties dialog uses. */
    private void setGuard(boolean value) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            try {
                var properties = getModel().getGrammar().getProperties().clone();
                properties.setCheckCreatorEdges(value);
                getModel().doSetProperties(properties);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        });
    }

    /** Indicates if the grammar-level error list is currently in the frame,
     * showing, and non-empty. */
    private boolean errorListShowing() {
        ListTabbedPane results = findResultsPanel(frame().getSource());
        return results != null && results.isShowing()
            && results.getErrorListPanel().hasContent();
    }

    /** Recursively searches a component tree for the results panel. */
    private @Nullable ListTabbedPane findResultsPanel(Component comp) {
        if (comp instanceof ListTabbedPane result) {
            return result;
        }
        if (comp instanceof Container container) {
            for (Component child : container.getComponents()) {
                ListTabbedPane result = findResultsPanel(child);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    /** Copies the fixture grammar to a scratch directory, optionally with
     * the guard property switched on. */
    private Path copyGrammar(boolean guard) throws IOException {
        var tmp = this.tmp;
        assert tmp != null; // injected by JUnit
        Path result = tmp.resolve("creatorNac.gps");
        FileUtils.copyDirectory(new File(GRAMMAR), result.toFile(), false);
        if (guard) {
            Files
                .writeString(result.resolve("system.properties"),
                             System.lineSeparator() + GUARD + "=true",
                             StandardOpenOption.APPEND);
        }
        return result;
    }

    /** Scratch directory for the grammar copies. */
    @TempDir
    private @Nullable Path tmp;
}
