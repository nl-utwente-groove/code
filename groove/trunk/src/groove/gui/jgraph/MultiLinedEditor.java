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
 * $Id: MultiLinedEditor.java,v 1.6 2008-01-30 09:33:13 iovka Exp $
 */
package groove.gui.jgraph;

import groove.graph.Label;
import groove.view.aspect.Aspect;
import groove.view.aspect.AspectValue;
import groove.view.aspect.NestingAspect;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.util.EventObject;
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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultGraphCellEditor;
import org.jgraph.graph.GraphCellEditor;
import org.jgraph.graph.GraphConstants;

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
    public Component getGraphCellEditorComponent(JGraph graph, Object cell,
            boolean isSelected) {

        Component component =
            super.getGraphCellEditorComponent(graph, cell, isSelected);

        // set the size of an editor to that of a view
        CellView view = graph.getGraphLayoutCache().getMapping(cell, false);
        Rectangle2D tmp = view.getBounds();
        this.editingComponent.setBounds((int) tmp.getX(), (int) tmp.getY(),
            (int) tmp.getWidth(), (int) tmp.getHeight());

        // I have to set a font here instead of in the
        // RealCellEditor.getGraphCellEditorComponent() because
        // I don't know what cell is being edited when in the
        // RealCellEditor.getGraphCellEditorComponent().
        Font font = GraphConstants.getFont(view.getAllAttributes());
        this.editingComponent.setFont((font != null) ? font : graph.getFont());
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
    private static class RealCellEditor extends AbstractCellEditor implements
            GraphCellEditor, DocumentListener {
        /**
         * Initialises the editor component with the edit string of the user
         * object of <tt>value</tt> (which is required to be a {@link JCell}).
         */
        public Component getGraphCellEditorComponent(JGraph graph,
                Object value, boolean isSelected) {
            this.labels.clear();
            JModel jmodel = (JModel) graph.getModel();
            for (int i = 0; i < jmodel.getRootCount(); i++) {
                JCell cell = (JCell) jmodel.getRootAt(i);
                for (Label listLabel : cell.getListLabels()) {
                    this.labels.add(stripPrefixes(listLabel.text()));
                }
            }
            this.labels.addAll(this.prefixes);
            JTextArea result = getEditorComponent();
            String editString =
                ((EditableJCell) value).getUserObject().toEditString();
            result.setText(editString);
            result.selectAll();
            return result;
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
            InputMap focusedInputMap =
                result.getInputMap(JComponent.WHEN_FOCUSED);
            focusedInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                "enter");
            focusedInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0),
                "tab");
            focusedInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,
                InputEvent.SHIFT_DOWN_MASK), "shiftEnter");
            focusedInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,
                InputEvent.CTRL_DOWN_MASK), "metaEnter");
            result.getActionMap().put("enter", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    stopCellEditing();
                }
            });
            // make the reaction to SHIFT+ENTER and META+ENTER so a newline is
            // added
            AbstractAction newLineAction = new AbstractAction() {
                /** Inserts a newline into the edited text. */
                public void actionPerformed(ActionEvent e) {
                    Document doc = result.getDocument();
                    try {
                        doc.insertString(result.getCaretPosition(), "\n", null);
                    } catch (BadLocationException e1) {
                        e1.printStackTrace();
                    }
                }
            };
            result.getActionMap().put("shiftEnter", newLineAction);
            result.getActionMap().put("metaEnter", newLineAction);
            result.getDocument().addDocumentListener(this);
            return result;
        }

        public Object getCellEditorValue() {
            return getEditorComponent().getText();
        }

        @Override
        public boolean shouldSelectCell(EventObject event) {
            getEditorComponent().requestFocus();
            return super.shouldSelectCell(event);
        }

        public void changedUpdate(DocumentEvent ev) {
            // no action
        }

        public void removeUpdate(DocumentEvent ev) {
            // no action
        }

        public void insertUpdate(DocumentEvent ev) {
            int pos = ev.getOffset();
            String content = null;

            try {
                // only do completion if we're at the end of a label
                Document document = this.editorComponent.getDocument();
                if (pos + 1 < document.getLength()
                    && !Character.isWhitespace(document.getText(pos + 1, 1).charAt(
                        0))) {
                    return;
                }
                content = document.getText(0, pos + 1);
            } catch (BadLocationException e) {
                throw new IllegalStateException(String.format(
                    "Impossible error: %s", e));
            }

            // Find where the word starts in content
            int w;
            for (w = pos; w >= 0; w--) {
                if (Character.isWhitespace(content.charAt(w))) {
                    break;
                }
            }
            // Identify the root of the word to be completed
            String root = content.substring(w + 1);
            int rootLength = root.length();
            root = stripPrefixes(root);
            int shift = rootLength - root.length();
            w += shift;
            if (pos - w < 2) {
                // Too few chars
                return;
            }
            SortedSet<String> tailSet =
                RealCellEditor.this.labels.tailSet(root);
            if (!tailSet.isEmpty()) {
                String match = tailSet.first();
                if (match.startsWith(root)) {
                    // A completion is found
                    String completion = match.substring(pos - w);
                    // We cannot modify Document from within notification,
                    // so we submit a task that does the change later
                    SwingUtilities.invokeLater(new CompletionTask(completion,
                        pos + 1));
                }
            }
        }

        /** Strips all aspect prefixes from the root. */
        private String stripPrefixes(String root) {
            boolean strip = true;
            while (strip) {
                strip = false;
                for (String prefix : this.prefixes) {
                    if (root.startsWith(prefix)) {
                        root = root.substring(prefix.length());
                        strip = true;
                        break;
                    }
                }
            }
            return root;
        }

        /** Adds the cell editor as listener to the editor component's document. */
        private void addDocumentListener() {
            this.editorComponent.getDocument().addDocumentListener(this);
        }

        /**
         * Removes the cell editor as listener from the editor component's
         * document.
         */
        private void removeDocumentListener() {
            this.editorComponent.getDocument().removeDocumentListener(this);
        }

        /** The component actually doing the editing. */
        private JTextArea editorComponent;
        /** The existing labels of the current graph. */
        private final SortedSet<String> labels = new TreeSet<String>();
        /** The existing aspect prefixes. */
        private final List<String> prefixes = new LinkedList<String>();
        {
            for (String name : AspectValue.getValueNames()) {
                this.prefixes.add(name + Aspect.VALUE_SEPARATOR);
            }
            // add the special edge labels
            this.prefixes.add(NestingAspect.AT_LABEL);
            this.prefixes.add(NestingAspect.IN_LABEL);
        }

        private class CompletionTask implements Runnable {
            CompletionTask(String completion, int position) {
                this.completion = completion;
                this.position = position;
            }

            public void run() {
                // temporarily disable document listener to avoid
                // this insertion triggering another completion
                removeDocumentListener();
                RealCellEditor.this.editorComponent.insert(this.completion,
                    this.position);
                RealCellEditor.this.editorComponent.setCaretPosition(this.position
                    + this.completion.length());
                RealCellEditor.this.editorComponent.moveCaretPosition(this.position);
                addDocumentListener();
            }

            private final String completion;
            private final int position;
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
            super.doLayout();
            // subtract 2 pixels that were added to the preferred size of the
            // container for the
            // border.
            Dimension dim = getEditingComponent().getSize();
            getEditingComponent().setSize(dim.width - 2, dim.height);

            // reset container's size based on a potentially new preferred size
            // of a real
            // editor.
            setSize(getPreferredSize().width, getPreferredSize().height);
        }
    }
}