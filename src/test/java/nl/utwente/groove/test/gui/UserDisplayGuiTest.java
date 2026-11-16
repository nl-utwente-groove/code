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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Component;
import java.awt.Container;
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

import nl.utwente.groove.annotation.UserDisplay;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.gui.display.DisplayKind;
import nl.utwente.groove.gui.jgraph.AspectJGraph;
import nl.utwente.groove.gui.jgraph.AspectJVertex;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.test.GuiTest;
import nl.utwente.groove.test.SlowTest;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.io.FileUtils;
import nl.utwente.groove.util.line.StringFormat;

/**
 * Asserts that the state display shows a user value by its custom display string
 * (see {@link UserDisplay}, gh #938) with the constructor term in the tooltip of
 * the node, whereas the host graph display shows the constructor term.
 * Uses the user-display sample, whose start graph has a single node with
 * a user value that has a display string and one of which the display method fails.
 * <p>
 * Excluded from the default test run via the {@link SlowTest} and
 * {@link GuiTest} categories, and skipped in headless environments.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.2, 2026-11")
@NonNullByDefault
@Tag(SlowTest.TAG)
@Tag(GuiTest.TAG)
@ExtendWith(SimulatorFixture.class)
public class UserDisplayGuiTest {
    /** Location of the fixture grammar. */
    private static final String GRAMMAR = "junit/samples/user-display.gps";
    /** Display string of the user value in the start graph. */
    private static final String DISPLAY = "walk <x/y>";
    /** Start of the constructor term of the user value in the start graph. */
    private static final String TERM = "UserTypeDisplay(";
    /** Start of the constructor term of the user value of which the display method fails. */
    private static final String FAILING_TERM = "UserTypeDisplayFailing(";

    /** The state display must show the display string, with the term in the tooltip. */
    @Test
    void stateShowsDisplayString() throws Exception {
        loadGrammar(copyGrammar());
        SwingUtilities.invokeAndWait(() -> assertTrue(getModel().resetGTS()));
        GTS gts = getModel().getGTS();
        assertNotNull(gts);
        SwingUtilities.invokeAndWait(() -> {
            getModel().doSetStateAndMatch(gts.startState(), null);
            getModel().setDisplay(DisplayKind.STATE);
        });
        var text = getVertexText(true);
        String label = text[0];
        assertTrue(label.contains(DISPLAY), label);
        assertFalse(label.contains(TERM), label);
        // without a display string, the term is shown
        assertTrue(label.contains(FAILING_TERM), label);
        String toolTip = text[1];
        assertTrue(toolTip.contains(TERM), toolTip);
        assertFalse(toolTip.contains(FAILING_TERM), toolTip);
    }

    /** The host graph display must show the constructor term, in the label only. */
    @Test
    void hostShowsTerm() throws Exception {
        loadGrammar(copyGrammar());
        SwingUtilities
            .invokeAndWait(() -> getModel().doSelect(ResourceKind.HOST, QualName.parse("start")));
        var text = getVertexText(false);
        String label = text[0];
        assertTrue(label.contains(TERM), label);
        assertFalse(label.contains(DISPLAY), label);
        String toolTip = text[1];
        assertFalse(toolTip.contains(TERM), toolTip);
    }

    /**
     * Waits for the state display or the host graph display to show
     * the single node of the start graph, and returns the text of its label
     * and its tooltip.
     * @param forState if {@code true}, the node is looked up in the state display,
     * otherwise in the host graph display
     */
    private String[] getVertexText(boolean forState) throws Exception {
        String display = forState
            ? "state display"
            : "host graph display";
        waitFor(display + " to show the node", () -> findVertex(forState) != null);
        var result = new String[2];
        SwingUtilities.invokeAndWait(() -> {
            var vertex = findVertex(forState);
            assert vertex != null; // established by the wait above
            result[0]
                = vertex.getVisuals().getLabel().toString(StringFormat.instance()).toString();
            result[1] = vertex.getToolTipText();
        });
        return result;
    }

    /** Returns the vertex of the single node of the graph that is showing in the state
     * display or the host graph display, if any.
     */
    private @Nullable AspectJVertex findVertex(boolean forState) {
        var jGraph = findJGraph(frame().getSource(), forState);
        if (jGraph != null && jGraph.isShowing()) {
            var jModel = jGraph.getModel();
            if (jModel != null) {
                for (var root : jModel.getRoots()) {
                    if (root instanceof AspectJVertex result) {
                        return result;
                    }
                }
            }
        }
        return null;
    }

    /** Recursively searches a component tree for the JGraph of the state display
     * or of the host graph display. */
    private @Nullable AspectJGraph findJGraph(Component comp, boolean forState) {
        if (comp instanceof AspectJGraph result && result.isForState() == forState
            && result.getGraphRole() == GraphRole.HOST) {
            return result;
        }
        if (comp instanceof Container container) {
            for (Component child : container.getComponents()) {
                AspectJGraph result = findJGraph(child, forState);
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
        Path result = tmp.resolve("user-display.gps");
        FileUtils.copyDirectory(new File(GRAMMAR), result.toFile(), false);
        return result;
    }

    /** Scratch directory for the grammar copies. */
    @TempDir
    private @Nullable Path tmp;
}
