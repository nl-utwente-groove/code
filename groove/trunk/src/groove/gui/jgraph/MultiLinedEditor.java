/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
 * $Id: MultiLinedEditor.java,v 1.6 2008-01-30 09:33:13 iovka Exp $
 */
package groove.gui.jgraph;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
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

import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultGraphCellEditor;
import org.jgraph.graph.GraphCellEditor;
import org.jgraph.graph.GraphConstants;

/**
 * Multiline jcell editor, essentially taken from <code>org.jgraph.cellview.JGraphMultilineView</code>.
 * @author Arend Rensink
 * @version $Revision: 1.6 $
 */
public class MultiLinedEditor extends DefaultGraphCellEditor {
    /**
     * Overriding this in order to set the size of an editor to that of an edited view.
     */
    @Override
    public Component getGraphCellEditorComponent(JGraph graph, Object cell, boolean isSelected) {

        Component component = super.getGraphCellEditorComponent(graph, cell, isSelected);

        // set the size of an editor to that of a view
        CellView view = graph.getGraphLayoutCache().getMapping(cell, false);
        Rectangle2D tmp = view.getBounds();
        editingComponent.setBounds((int) tmp.getX(),
            (int) tmp.getY(),
            (int) tmp.getWidth(),
            (int) tmp.getHeight());

        // I have to set a font here instead of in the
        // RealCellEditor.getGraphCellEditorComponent() because
        // I don't know what cell is being edited when in the
        // RealCellEditor.getGraphCellEditorComponent().
        Font font = GraphConstants.getFont(view.getAllAttributes());
        editingComponent.setFont((font != null) ? font : graph.getFont());
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
    	return editingComponent;
    }
    
	/** Internal editor implementation. */
    private class RealCellEditor extends AbstractCellEditor implements GraphCellEditor {
        /** Constructs a new instance of the editor. */
        public RealCellEditor() {
            editorComponent.setBorder(UIManager.getBorder("Tree.editorBorder"));
            // editorComponent.setLineWrap(true);
            editorComponent.setWrapStyleWord(true);

            // substitute a JTextArea's VK_ENTER action with our own that will stop an edit.
            InputMap focusedInputMap = editorComponent.getInputMap(JComponent.WHEN_FOCUSED);
            focusedInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                "enter");
            focusedInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.SHIFT_DOWN_MASK),
                "shiftEnter");
            focusedInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.CTRL_DOWN_MASK),
                "metaEnter");
            focusedInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                "enter");
            editorComponent.getActionMap().put("enter", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    stopCellEditing();
                }
            });
            // make the reaction to SHIFT+ENTER and META+ENTER so a newline is added
            AbstractAction newLineAction = new AbstractAction() {
                /** Inserts a newline into the edited text. */
                public void actionPerformed(ActionEvent e) {
                    Document doc = editorComponent.getDocument();
                    try {
                        doc.insertString(editorComponent.getCaretPosition(), "\n", null);
                    } catch (BadLocationException e1) {
                        e1.printStackTrace();
                    }
                }
            };
            editorComponent.getActionMap().put("shiftEnter", newLineAction);
            editorComponent.getActionMap().put("metaEnter", newLineAction);
        }

        /**
         * Initializes the editor component with the edit string of the user object of
         * <tt>value</tt> (which is required to be a {@link JCell}).
         * @require <tt>value instanceof JCell</tt>
         */
        public Component getGraphCellEditorComponent(JGraph graph, Object value, boolean isSelected) {
            String editString = ((EditableJCell) value).getUserObject().toEditString();
            editorComponent.setText(editString);
            editorComponent.selectAll();
            return editorComponent;
        }

        public Object getCellEditorValue() {
            return editorComponent.getText();
        }

        @Override
        public boolean shouldSelectCell(EventObject event) {
            editorComponent.requestFocus();
            return super.shouldSelectCell(event);
        }
        
        /** The component actually doing the editing. */
        protected final JTextArea editorComponent = new JTextArea();
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
            // subtract 2 pixels that were added to the preferred size of the container for the
            // border.
            Dimension dim = getEditingComponent().getSize();
            getEditingComponent().setSize(dim.width - 2, dim.height);

            // reset container's size based on a potentially new preferred size of a real
            // editor.
            setSize(getPreferredSize().width, getPreferredSize().height);
        }
    }
}