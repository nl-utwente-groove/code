/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
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
package groove.gui.jgraph;

import groove.grammar.aspect.AspectKind;
import groove.grammar.type.TypeGraph;
import groove.grammar.type.TypeLabel;
import groove.graph.EdgeRole;
import groove.gui.Options;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.EventObject;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;

import org.jgraph.graph.DefaultGraphCellEditor;
import org.jgraph.graph.GraphCellEditor;

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
     * http://sourceforge.net/forum/forum.php?thread_id=781479&forum_id=140880
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
    private static class RealCellEditor extends AbstractCellEditor implements GraphCellEditor,
            CaretListener {
        /**
         * Initialises the editor component with the edit string of the user
         * object of <tt>value</tt> (which is required to be a {@link JCell}).
         */
        @Override
        public Component getGraphCellEditorComponent(org.jgraph.JGraph graph, Object value,
                boolean isSelected) {
            AspectJCell jCell = (AspectJCell) value;
            // fill the set of labels for autocompletion
            this.labels.clear();
            this.labels.addAll(prefixes);
            AspectJModel jmodel = (AspectJModel) graph.getModel();
            TypeGraph type = jmodel.getResourceModel().getGrammar().getTypeGraph();
            for (TypeLabel label : type.getLabels()) {
                this.labels.add(label.text());
            }
            JTextArea result = getEditorComponent();
            // scale with the jGraph
            Font font = Options.getLabelFont().deriveFont(jCell.getVisuals().getFont());
            font = (font != null) ? font : graph.getFont();
            if (graph.getScale() != 1) {
                double scale = graph.getScale();
                Dimension size = result.getSize();
                size.height *= scale;
                size.width *= scale;
                result.setSize(size);
                font = font.deriveFont((float) (font.getSize() * scale));
            }
            result.setFont(font);
            String editString = ((AspectJCell) value).getUserObject().toEditString();
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
            focusedInputMap.put(AUTOCOMPLETE_KEY, AUTOCOMPLETE_STRING);
            result.getActionMap().put(STOP_EDIT_STRING, new StopEditAction());
            result.getActionMap().put(NEWLINE_STRING, new NewlineAction());
            result.getActionMap().put(AUTOCOMPLETE_STRING, new AutocompleteAction());
            result.addCaretListener(this);
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

        @Override
        public void caretUpdate(CaretEvent e) {
            resetAutocomplete();
        }

        private String getNextCompletion() {
            if (this.completions == null) {
                this.completions = computeCompletions();
            }
            String result = this.completions.poll();
            if (result != null) {
                this.completions.add(result);
            }
            return result;
        }

        private LinkedList<String> computeCompletions() {
            LinkedList<String> result = new LinkedList<>();
            Caret caret = getEditorComponent().getCaret();
            int dot = caret.getDot();
            int mark = caret.getMark();
            int min = Math.min(dot, mark);
            int max = Math.max(dot, mark);

            String content;
            try {
                // only do completion if the selection runs up to the end of a label
                Document document = getDocument();
                if (max < document.getLength()
                    && Character.isLetterOrDigit(document.getText(max, 1).charAt(0))) {
                    return result;
                }
                content = document.getText(0, min);
            } catch (BadLocationException exc) {
                throw new IllegalStateException(String.format("Impossible error: %s", exc));
            }

            // Find where the label starts
            int start = min;
            while (start > 0 && Character.isLetterOrDigit(content.charAt(start - 1))) {
                start--;
            }
            if (start < min) {
                // Identify the root of the word to be completed
                String root = content.substring(start);
                SortedSet<String> tailSet = RealCellEditor.this.labels.tailSet(root);
                if (!tailSet.isEmpty()) {
                    Iterator<String> iter = tailSet.iterator();
                    String nextCompletion = iter.next();
                    while (nextCompletion.startsWith(root)) {
                        result.add(nextCompletion.substring(min - start));
                        nextCompletion = iter.next();
                    }
                }
            }
            return result;
        }

        private void resetAutocomplete() {
            this.completions = null;
        }

        private void doAutocomplete() {
            String completion = getNextCompletion();
            if (completion != null) {
                SwingUtilities.invokeLater(new CompletionTask(completion));
            }
        }

        /** The component actually doing the editing. */
        private JTextArea editorComponent;
        /** The existing labels of the current graph. */
        private final SortedSet<String> labels = new TreeSet<>();
        /** List of autocompletions. */
        private LinkedList<String> completions = null;

        private final static String AUTOCOMPLETE_STRING = "autocomplete";
        private final static String NEWLINE_STRING = "newline";
        private final static String STOP_EDIT_STRING = "stop";
        private final static KeyStroke AUTOCOMPLETE_KEY = KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,
            InputEvent.CTRL_DOWN_MASK);
        private final static KeyStroke NEWLINE_KEY_1 = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,
            InputEvent.SHIFT_DOWN_MASK);
        private final static KeyStroke NEWLINE_KEY_2 = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,
            InputEvent.CTRL_DOWN_MASK);
        private final static KeyStroke STOP_EDIT_KEY_1 = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,
            0);
        private final static KeyStroke STOP_EDIT_KEY_2 = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,
            0);

        /** The existing aspect prefixes. */
        private final static List<String> prefixes = new LinkedList<>();
        static {
            for (AspectKind aspectKind : AspectKind.values()) {
                String prefix = aspectKind.getPrefix();
                if (prefix.length() > 1) {
                    prefixes.add(prefix);
                }
            }
            for (EdgeRole edgeRole : EdgeRole.values()) {
                String prefix = edgeRole.getPrefix();
                if (prefix.length() > 1) {
                    prefixes.add(prefix);
                }
            }
        }

        private class CompletionTask implements Runnable {
            CompletionTask(String completion) {
                this.completion = completion;
            }

            @Override
            public void run() {
                getEditorComponent().removeCaretListener(RealCellEditor.this);
                Caret caret = getEditorComponent().getCaret();
                int pos = Math.min(caret.getDot(), caret.getMark());
                getEditorComponent().replaceSelection(this.completion);
                getEditorComponent().setCaretPosition(pos);
                getEditorComponent().moveCaretPosition(pos + this.completion.length());
                getEditorComponent().addCaretListener(RealCellEditor.this);
            }

            private final String completion;
        }

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

        private class AutocompleteAction extends AbstractAction {
            /** Inserts a newline into the edited text. */
            @Override
            public void actionPerformed(ActionEvent evt) {
                doAutocomplete();
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
                getEditingComponent().setBounds(MultiLinedEditor.this.offsetX,
                    MultiLinedEditor.this.offsetY, w, size.height);

                // reset container's size based on a potentially new preferred size
                // of the editing component
                setSize(getPreferredSize());
            }
        }
    }
}