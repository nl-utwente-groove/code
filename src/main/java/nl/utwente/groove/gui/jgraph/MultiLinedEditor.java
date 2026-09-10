/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
package nl.utwente.groove.gui.jgraph;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.jgraph.graph.DefaultGraphCellEditor;
import org.jgraph.graph.GraphCellEditor;

import nl.utwente.groove.util.Fonts;
import nl.utwente.groove.gui.view.AspectViewCell;
import nl.utwente.groove.gui.view.LabelCompletion;

/**
 * Multiline jcell editor, essentially taken from
 * <code>org.jgraph.cellview.JGraphMultilineView</code>.
 * @author Arend Rensink
 * @version $Revision$
 */
public class MultiLinedEditor extends DefaultGraphCellEditor {
    /**
     * Overriding this in order to set the size of an editor to that of an
     * edited view.
     */
    @Override
    public Component getGraphCellEditorComponent(org.jgraph.JGraph graph, Object cell,
                                                 boolean isSelected) {
        Component component = super.getGraphCellEditorComponent(graph, cell, isSelected);
        return component;
    }

    @Override
    protected GraphCellEditor createGraphCellEditor() {
        return new RealCellEditor();
    }

    /**
     * Overwriting this so that I could modify an editor container. see
     * {@code http://sourceforge.net/forum/forum.php?thread_id=781479&forum_id=140880}
     */
    @Override
    protected Container createContainer() {
        return new ModifiedEditorContainer();
    }

    /** Returns the editing component. */
    final Component getEditingComponent() {
        return this.editingComponent;
    }

    /** Internal editor implementation. */
    private static class RealCellEditor extends AbstractCellEditor implements GraphCellEditor {
        /**
         * Initialises the editor component with the edit string of the user
         * object of <tt>value</tt> (which is required to be a {@link nl.utwente.groove.gui.view.ViewCell}).
         */
        @Override
        public Component getGraphCellEditorComponent(org.jgraph.JGraph graph, Object value,
                                                     boolean isSelected) {
            AspectViewCell jCell = (AspectViewCell) ((JCell<?>) value).getViewCell();
            JTextArea result = getEditorComponent();
            // fill the set of labels for autocompletion
            AspectJModel jmodel = (AspectJModel) graph.getModel();
            this.completion.setLabels(LabelCompletion.labelsFor(jmodel.getTypeGraph()));
            // scale with the jGraph
            Font font = Fonts.getLabelFont().deriveFont(jCell.getVisuals().getFont());
            font = (font != null)
                ? font
                : graph.getFont();
            if (graph.getScale() != 1) {
                double scale = graph.getScale();
                Dimension size = result.getSize();
                size.height *= scale;
                size.width *= scale;
                result.setSize(size);
                font = font.deriveFont((float) (font.getSize() * scale));
            }
            result.setFont(font);
            String editString = jCell.getEditableLabels().toEditString();
            result.setText(editString);
            result.selectAll();
            return result;
        }

        /** Returns the document of the editor component. */
        private Document getDocument() {
            return getEditorComponent().getDocument();
        }

        /** Lazily creates the actual editor component. */
        private JTextArea getEditorComponent() {
            if (this.editorComponent == null) {
                this.editorComponent = computeEditorComponent();
            }
            return this.editorComponent;
        }

        /** Computes a new editor component. */
        private JTextArea computeEditorComponent() {
            final JTextArea result = new JTextArea();
            result.setBorder(UIManager.getBorder("Tree.editorBorder"));
            result.setWrapStyleWord(true);

            // substitute a JTextArea's VK_ENTER action with our own that will
            // stop an edit.
            InputMap focusedInputMap = result.getInputMap(JComponent.WHEN_FOCUSED);
            focusedInputMap.put(STOP_EDIT_KEY_1, STOP_EDIT_STRING);
            focusedInputMap.put(STOP_EDIT_KEY_2, STOP_EDIT_STRING);
            focusedInputMap.put(NEWLINE_KEY_1, NEWLINE_STRING);
            focusedInputMap.put(NEWLINE_KEY_2, NEWLINE_STRING);
            result.getActionMap().put(STOP_EDIT_STRING, new StopEditAction());
            result.getActionMap().put(NEWLINE_STRING, new NewlineAction());
            this.completion = new LabelCompletion(result);
            return result;
        }

        @Override
        public Object getCellEditorValue() {
            return getEditorComponent().getText();
        }

        @Override
        public boolean shouldSelectCell(EventObject event) {
            getEditorComponent().requestFocus();
            return super.shouldSelectCell(event);
        }

        /** The component actually doing the editing. */
        private JTextArea editorComponent;
        /** The label completion on the editing component. */
        private LabelCompletion completion;

        private final static String NEWLINE_STRING = "newline";
        private final static String STOP_EDIT_STRING = "stop";
        private final static KeyStroke NEWLINE_KEY_1
            = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.SHIFT_DOWN_MASK);
        private final static KeyStroke NEWLINE_KEY_2
            = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK);
        private final static KeyStroke STOP_EDIT_KEY_1
            = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        private final static KeyStroke STOP_EDIT_KEY_2
            = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);

        private class StopEditAction extends AbstractAction {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopCellEditing();
            }
        }

        private class NewlineAction extends AbstractAction {
            /** Inserts a newline into the edited text. */
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    getDocument().insertString(getEditorComponent().getCaretPosition(), "\n", null);
                } catch (BadLocationException e1) {
                    throw new IllegalStateException(e1);
                }
            }
        }

    }

    /** Specialisation of the editor container that adapts the size. */
    private class ModifiedEditorContainer extends EditorContainer {
        /** Empty constructor with the correct visibility. */
        ModifiedEditorContainer() {
            // empty
        }

        @Override
        public void doLayout() {
            if (getEditingComponent() != null) {
                Dimension size = getEditingComponent().getPreferredSize();
                int w = size.width + 3;
                int minw = 45;
                int maxw = getEditingComponent().getMaximumSize().width;
                if (getParent() != null && maxw > getParent().getWidth()) {
                    maxw = getParent().getWidth();
                }
                w = Math.max(minw, Math.min(w, maxw));
                getEditingComponent()
                    .setBounds(MultiLinedEditor.this.offsetX, MultiLinedEditor.this.offsetY, w,
                               size.height);

                // reset container's size based on a potentially new preferred size
                // of the editing component
                setSize(getPreferredSize());
            }
        }
    }
}