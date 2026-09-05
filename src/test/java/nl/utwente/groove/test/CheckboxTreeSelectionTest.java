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
package nl.utwente.groove.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.UIManager;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.gui.display.DisplayKind;
import nl.utwente.groove.gui.view.AspectGraphViewController;
import nl.utwente.groove.gui.tree.CheckboxTree;
import nl.utwente.groove.gui.tree.TypeTree;
import nl.utwente.groove.io.Groove;
import nl.utwente.groove.util.AIGenerated;

/**
 * Regression test for the selection colours of a {@link CheckboxTree}, as
 * used for the label tree of a graph view. The tree installs its own
 * {@code BasicTreeUI}, which leaves painting the selection to the cell
 * renderer, while the look-and-feel (FlatLaf) tells the standard renderer
 * not to fill its background because its own tree UI would paint the
 * selection. Consequently, selected rows used to show the selection
 * foreground on the ordinary background; the renderer must paint the
 * selection background of the look-and-feel itself.
 * <p>
 * The graph is rendered into a headless canvas of the JGraph backend, the way the
 * {@code Imager} does it, and only the renderer components are inspected.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public class CheckboxTreeSelectionTest {
    /** Grammar whose start graph gives rise to a nonempty label tree. */
    private static final String GRAMMAR = "junit/samples/mergers.gps";

    /** A selected row is rendered in the selection colours of the
     * look-and-feel, an unselected row is not. */
    @Test
    void selectedRowShowsSelectionColours() throws IOException {
        TypeTree tree = buildTree();
        Object row = tree.getTopNode().getChildAt(0);
        Rendering selected = render(tree, row, true);
        Rendering unselected = render(tree, row, false);
        // the selection colours of the look-and-feel, for the focus state of the tree
        boolean focused = tree.isFocusOwner();
        Color selectionBackground
            = uiColor(focused, "Tree.selectionInactiveBackground", "Tree.selectionBackground");
        Color selectionForeground
            = uiColor(focused, "Tree.selectionInactiveForeground", "Tree.selectionForeground");
        assertTrue(selected.labelOpaque, "selected label is not opaque");
        assertEquals(selectionBackground, selected.labelBackground, "selected label background");
        assertEquals(selectionForeground, selected.labelForeground, "selected label foreground");
        assertTrue(selected.cellOpaque, "selected cell is not opaque");
        assertEquals(selectionBackground, selected.cellBackground, "selected cell background");
        assertNotEquals(selectionBackground, unselected.labelBackground,
                        "unselected label background");
    }

    /** Returns a colour from the UI defaults: the first key if the tree is
     * not focused and the key is defined, otherwise the second key. */
    private Color uiColor(boolean focused, String inactiveKey, String activeKey) {
        Color result = focused
            ? null
            : UIManager.getColor(inactiveKey);
        if (result == null) {
            result = UIManager.getColor(activeKey);
        }
        assertTrue(result != null, "no UI colour for " + activeKey);
        return result;
    }

    /** Renders a tree row through the tree's cell renderer and captures the
     * colours of the resulting label and cell component. */
    private Rendering render(CheckboxTree tree, Object row, boolean selected) {
        Component cell = tree
            .getCellRenderer()
            .getTreeCellRendererComponent(tree, row, selected, false, true, 0, false);
        JLabel label = findLabel(cell);
        if (label == null) {
            return fail("no label in the rendered cell " + cell);
        }
        return new Rendering(label.isOpaque(), label.getBackground(), label.getForeground(),
            cell.isOpaque(), cell.getBackground());
    }

    /** Colours of a rendered row, copied out of the (reused) renderer. */
    private record Rendering(boolean labelOpaque, Color labelBackground, Color labelForeground,
        boolean cellOpaque, Color cellBackground) {
        // no additional functionality
    }

    /** Finds the label in a rendered cell, which is either the label itself
     * or a panel containing it next to a checkbox. */
    private @Nullable JLabel findLabel(Component cell) {
        if (cell instanceof JLabel result) {
            return result;
        }
        if (cell instanceof Container container) {
            for (Component child : container.getComponents()) {
                JLabel result = findLabel(child);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    /** Loads the fixture start graph into a headless JGraph and returns
     * the label tree built for it. */
    private TypeTree buildTree() throws IOException {
        GrammarModel grammar = Groove.loadGrammar(GRAMMAR);
        AspectGraph graph = grammar.getStartGraphModel().getSource();
        assert graph != null; // the grammar has a single start graph
        var controller = new AspectGraphViewController(null, DisplayKind.HOST, false);
        controller.setGrammar(grammar);
        var jGraph = controller.getCanvas();
        var model = jGraph.newViewModel();
        model.loadGraph(graph);
        jGraph.setViewModel(model);
        TypeTree result = new TypeTree(jGraph, true);
        result.synchroniseModel();
        assertTrue(result.getTopNode().getChildCount() > 0, "label tree is empty");
        return result;
    }
}
