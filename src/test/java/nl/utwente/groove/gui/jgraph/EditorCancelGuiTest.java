/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2026 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.utwente.groove.gui.jgraph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.awt.Component;
import java.awt.Container;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.model.GraphBasedModel;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.gui.display.DisplayKind;
import nl.utwente.groove.gui.view.AspectGraphViewController;
import nl.utwente.groove.gui.view.AspectGraphViewModel;
import nl.utwente.groove.gui.view.cell.AspectVertexCell;
import nl.utwente.groove.io.Groove;
import nl.utwente.groove.test.GuiTest;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;

/**
 * Asserts that the in-place label editor of the JGraph graph editor commits
 * the edited text on Enter and discards it on Escape (gh #819: Escape used
 * to commit as well). Drives the editor through the key bindings of its text
 * component rather than through real key events, so no focus is needed.
 * <p>
 * Excluded from the default test run via the {@link GuiTest} category,
 * and skipped in headless environments.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
@Tag(GuiTest.TAG)
public class EditorCancelGuiTest {
    /** Location of the fixture grammar. */
    private static final String GRAMMAR = "junit/types/mult.gps";
    /** Edit text of the vertex being edited. */
    private static final String ORIGINAL = "type:A";
    /** Edit text typed into the editor. */
    private static final String EDITED = "type:Z";

    /** Escape ends the edit without changing the label. */
    @Test
    void escapeCancelsEdit() throws Exception {
        assertEquals(ORIGINAL, edit(KeyEvent.VK_ESCAPE));
    }

    /** Enter ends the edit and applies the typed text. */
    @Test
    void enterCommitsEdit() throws Exception {
        assertEquals(EDITED, edit(KeyEvent.VK_ENTER));
    }

    /**
     * Starts editing the fixture's {@code type:A} vertex, replaces the text
     * by {@link #EDITED}, ends the edit by the binding of a given key,
     * and returns the resulting edit text of the vertex.
     */
    private static String edit(int keyCode) throws Exception {
        assumeFalse(GraphicsEnvironment.isHeadless(), "needs a display");
        String[] result = new String[1];
        SwingUtilities.invokeAndWait(() -> {
            try {
                AspectJGraph canvas = editorCanvas();
                AspectVertexCell vertex
                    = vertex(canvas.getNonNullModel().getViewModel(), ORIGINAL);
                canvas.startEditingAtCell(JCell.of(vertex));
                assertTrue(canvas.isEditing(), "editing started");
                JTextArea area = textArea(canvas);
                assertNotNull(area, "editor text component is showing");
                assertEquals(ORIGINAL, area.getText(), "editor shows the vertex text");
                area.setText(EDITED);
                fireBinding(area, keyCode);
                assertFalse(canvas.isEditing(), "editing ended");
                result[0] = vertex.getEditableLabels().toEditString();
            } catch (Exception exc) {
                throw new IllegalStateException(exc);
            }
        });
        String text = result[0];
        assertNotNull(text);
        return text;
    }

    /** Creates an editing JGraph canvas showing the fixture's type graph. */
    private static AspectJGraph editorCanvas() throws Exception {
        GrammarModel grammar = Groove.loadGrammar(GRAMMAR);
        var typeModel
            = (GraphBasedModel<?>) grammar.getResource(ResourceKind.TYPE, QualName.parse("type"));
        assertNotNull(typeModel, "the fixture grammar has a type graph");
        AspectGraph typeGraph = typeModel.getSource();
        var controller = new AspectGraphViewController(null, DisplayKind.TYPE.getGraphRole(), true);
        controller.setGrammar(grammar);
        AspectJGraph canvas = (AspectJGraph) controller.getCanvas();
        AspectGraphViewModel model = canvas.newViewModel();
        model.setBeingEdited(true);
        model.loadGraph(typeGraph);
        canvas.setViewModel(model);
        // the editor is positioned relative to the viewport
        new JScrollPane(canvas);
        return canvas;
    }

    /** Returns the vertex of a model with a given edit text. */
    private static AspectVertexCell vertex(AspectGraphViewModel model, String editText) {
        for (var cell : model.getCells()) {
            if (cell instanceof AspectVertexCell vertex
                && editText.equals(vertex.getEditableLabels().toEditString())) {
                return vertex;
            }
        }
        throw new IllegalStateException("No vertex with edit text " + editText);
    }

    /** Returns the text area of the in-place editor within a component tree, if any. */
    private static @Nullable JTextArea textArea(Component comp) {
        if (comp instanceof JTextArea result) {
            return result;
        }
        if (comp instanceof Container container) {
            for (Component child : container.getComponents()) {
                JTextArea result = textArea(child);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    /** Invokes the action bound to an unmodified key in the focused input map of a component. */
    private static void fireBinding(JComponent comp, int keyCode) {
        Object key = comp.getInputMap(JComponent.WHEN_FOCUSED).get(KeyStroke.getKeyStroke(keyCode, 0));
        assertNotNull(key, "key is bound");
        Action action = comp.getActionMap().get(key);
        assertNotNull(action, "bound action exists");
        action.actionPerformed(new ActionEvent(comp, ActionEvent.ACTION_PERFORMED, key.toString()));
    }
}
