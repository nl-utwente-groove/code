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
import static nl.utwente.groove.test.gui.SimulatorFixture.simulator;
import static nl.utwente.groove.test.gui.SimulatorFixture.waitFor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
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
import org.netbeans.jemmy.operators.JMenuBarOperator;

import nl.utwente.groove.gui.display.DisplayKind;
import nl.utwente.groove.gui.display.LTSDisplay;
import nl.utwente.groove.gui.look.Values;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.test.GuiTest;
import nl.utwente.groove.test.SlowTest;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.io.FileUtils;

/**
 * Asserts that the LTS display keeps its filter background colour, which
 * signals that not all states are shown, across state selections and display
 * switches.
 * <p>
 * The LTS canvas colour used to have two independent writers: the filter
 * colour was set when the state bound or the filter changed, and the colour
 * reflecting the status of the selected state was written straight onto the
 * canvas on every state change. A click on a state (with or without the Ctrl
 * that also brings up the state display) therefore replaced the filter colour
 * by the plain state colour, and since the panel's stored enabled colour was
 * still the filter colour, a later refresh could not even restore it.
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
public class LTSBackgroundGuiTest {
    /** Fixture grammar; its state space exceeds the minimum state bound. */
    private static final String GRAMMAR = "ferryman.gps";
    /** Known state count of the fully explored {@link #GRAMMAR}. */
    private static final int STATES = 114;
    /** State bound below the state count: the minimum the spinner allows. */
    private static final int LOW_BOUND = 100;
    /** State bound above the state count. */
    private static final int HIGH_BOUND = 200;

    /**
     * Selecting a state, and switching to the state display and back, leave
     * the filter colour of an incompletely shown LTS in place; raising the
     * bound above the state count then brings the plain state colour back.
     */
    @Test
    void filterColourSurvivesStateSelection() throws Exception {
        loadGrammar(copyGrammar(GRAMMAR));
        // the wait condition below relies on no exploration being recorded yet
        assertNull(getModel().getLastExploreType());
        // no-block: the explore action monopolises the EDT behind a modal
        // progress dialog until exploration finishes
        new JMenuBarOperator(frame()).pushMenuNoBlock("Explore|Explore State Space");
        waitFor("exploration to finish", () -> getModel().getLastExploreType() != null);
        GTS gts = getModel().getGTS();
        assertNotNull(gts);
        assertEquals(STATES, gts.nodeCount());
        LTSDisplay lts = (LTSDisplay) simulator().getDisplaysPanel().getDisplay(DisplayKind.LTS);
        SwingUtilities.invokeAndWait(() -> lts.setStateBound(LOW_BOUND));
        assertEquals(Values.FILTER_BACKGROUND, background(lts), "with states hidden");
        // select a shown state other than the currently selected one
        GraphState current = getModel().getState();
        GraphState other = gts
            .nodeSet()
            .stream()
            .filter(s -> s != current && s.getNumber() < LOW_BOUND)
            .findFirst()
            .orElseThrow();
        SwingUtilities.invokeAndWait(() -> getModel().setState(other));
        assertEquals(Values.FILTER_BACKGROUND, background(lts), "after selecting a state");
        SwingUtilities.invokeAndWait(() -> {
            getModel().setDisplay(DisplayKind.STATE);
            getModel().setDisplay(DisplayKind.LTS);
        });
        assertEquals(Values.FILTER_BACKGROUND, background(lts), "after switching displays");
        SwingUtilities.invokeAndWait(() -> lts.setStateBound(HIGH_BOUND));
        assertEquals(Values.STATE_BACKGROUND, background(lts), "with all states shown");
        assertTrue(!other.isError() && !other.isInner(), "the selected state has a plain status");
    }

    /** Returns the current background colour of the LTS canvas, read on the EDT. */
    private @Nullable Color background(LTSDisplay lts) throws Exception {
        Color[] result = new Color[1];
        SwingUtilities.invokeAndWait(() -> result[0] = lts.getCanvas().getComponent().getBackground());
        return result[0];
    }

    /** Copies a fixture grammar to a scratch directory. */
    private Path copyGrammar(String name) throws IOException {
        var tmp = this.tmp;
        assert tmp != null; // injected by JUnit
        Path result = tmp.resolve(name);
        FileUtils.copyDirectory(new File("junit/samples/" + name), result.toFile(), false);
        return result;
    }

    /** Scratch directory for the grammar copies. */
    @TempDir
    private @Nullable Path tmp;
}
