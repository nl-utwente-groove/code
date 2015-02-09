/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.gui.tree;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/** JTree specialisation in which the nodes may have checkboxes. */
public class CheckboxTree extends JTree {
    /**
     * Constructs a tree, with cell renderer and editor set by
     * {@link #createRenderer()} and {@link #createEditor()}.
     */
    public CheckboxTree() {
        setCellRenderer(createRenderer());
        setCellEditor(createEditor());
        setEditable(true);
        setRootVisible(false);
        setShowsRootHandles(true);
        // make sure the checkbox never selects the label
        // note that the BasicTreeUI may not be what is used in the current LAF,
        // but I don't know any other way to modify the selection behaviour
        BasicTreeUI ui = new BasicTreeUI() {
            @Override
            protected void selectPathForEvent(TreePath path, MouseEvent event) {
                if (!isOverCheckBox(path, event.getPoint().x)) {
                    super.selectPathForEvent(path, event);
                }
            }
        };
        setUI(ui);
        // initialise the tree model
        this.topNode = new DefaultMutableTreeNode();
        this.treeModel = new DefaultTreeModel(this.topNode);
        setModel(this.treeModel);
        // set selection mode
        getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
    }

    @Override
    public BasicTreeUI getUI() {
        return (BasicTreeUI) super.getUI();
    }

    @Override
    public void setBackground(Color background) {
        if (background != null) {
            this.lastBackground = background;
        }
        super.setBackground(background);
    }

    /**
     * In addition to delegating the method to <tt>super</tt>, sets the
     * background color to <tt>null</tt> when disabled and back to the default
     * when enabled.
     */
    @Override
    public void setEnabled(boolean enabled) {
        if (enabled != isEnabled()) {
            setBackground(getColor(enabled));
        }
        super.setEnabled(enabled);
    }

    /** Tests if a given coordinate pair is over the checkbox part. */
    public boolean isOverCheckBox(int x, int y) {
        TreePath path = getPathForLocation(x, y);
        return isOverCheckBox(path, x);
    }

    /** Tests if a given x-coordinate is over the checkbox part of a tree path. */
    private boolean isOverCheckBox(TreePath path, int x) {
        boolean result = false;
        if (path != null && path.getLastPathComponent() instanceof TreeNode) {
            TreeNode labelNode = (TreeNode) path.getLastPathComponent();
            Rectangle pathBounds = getPathBounds(path);
            if (CHECKBOX_ORIENTATION.equals(BorderLayout.WEST)) {
                int checkboxBorder = pathBounds.x + CHECKBOX_WIDTH;
                result = labelNode.hasCheckbox() && x < checkboxBorder;
            } else {
                int checkboxBorder = pathBounds.x + pathBounds.width - CHECKBOX_WIDTH;
                result = labelNode.hasCheckbox() && x >= checkboxBorder;
            }
        }
        return result;
    }

    /** Factory method to create a cell renderer. */
    protected CellRenderer createRenderer() {
        return new CellRenderer(this);
    }

    /** Factory method to create a cell editor. */
    protected CellEditor createEditor() {
        return new CellEditor(this);
    }

    /** Returns the appropriate background colour for an enabledness condition. */
    Color getColor(boolean enabled) {
        return enabled ? this.lastBackground : null;
    }

    @Override
    protected void fireValueChanged(TreeSelectionEvent e) {
        // only inform the listeners if the change is not triggered
        // from this object
        if (!this.changing) {
            this.changing = true;
            super.fireValueChanged(e);
            this.changing = false;
        }
    }

    @Override
    public void clearSelection() {
        if (!this.changing) {
            this.changing = true;
            super.clearSelection();
            this.changing = false;
        }
    }

    @Override
    public DefaultTreeModel getModel() {
        return this.treeModel;
    }

    /** Returns the fixed top node of the tree. */
    public DefaultMutableTreeNode getTopNode() {
        return this.topNode;
    }

    /** The top node in the JTree. */
    private final DefaultMutableTreeNode topNode;
    /**
     * The list model used for the JList.
     * @require <tt>listModel == listComponent.getModel()</tt>
     */
    private final DefaultTreeModel treeModel;
    /**
     * Value of the last explicitly set non-{@code null} background color.
     * This is used as new background the next time the tree is enabled.
     */
    private Color lastBackground;
    /**
     * Flag indicating that the selection model is changing.
     * This means the listener should not be active.
     */
    private transient boolean changing;

    /** Orientation of the filtering checkboxes in the label cells. */
    private static final String CHECKBOX_ORIENTATION = BorderLayout.WEST;

    /** Preferred width of a checkbox. */
    private static final int CHECKBOX_WIDTH = new JCheckBox().getPreferredSize().width;

    /**
     * Special cell renderer for nodes with optional checkboxes.
     */
    static protected class CellRenderer extends JPanel implements TreeCellRenderer {
        /**
         * Empty constructor with the correct visibility.
         * @param tree the enclosing checkbox tree
         */
        CellRenderer(CheckboxTree tree) {
            this.tree = tree;
            this.jLabel = new DefaultTreeCellRenderer();
            this.jLabel.setOpenIcon(null);
            this.jLabel.setLeafIcon(null);
            this.jLabel.setClosedIcon(null);
            this.jLabel.setBorder(INSET_BORDER);
            this.checkbox = new JCheckBox();
            this.checkbox.setOpaque(false);
            setLayout(new BorderLayout());
            add(this.jLabel, BorderLayout.CENTER);
            add(this.checkbox, CheckboxTree.CHECKBOX_ORIENTATION);
            setBorder(new EmptyBorder(0, 2, 0, 0));
            setComponentOrientation(this.tree.getComponentOrientation());
            setOpaque(false);
        }

        @Override
        public JComponent getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
            boolean expanded, boolean leaf, int row, boolean hasFocus) {
            this.initialising = true;
            JComponent result;
            this.jLabel.getTreeCellRendererComponent(tree,
                value,
                sel,
                expanded,
                leaf,
                row,
                hasFocus);
            Color background = this.tree.getColor(tree.isEnabled());
            // this.jLabel.setBackgroundNonSelectionColor(background);
            this.jLabel.setOpaque(!sel);
            this.jLabel.setBackground(background);
            this.labelNode = value instanceof TreeNode ? (TreeNode) value : null;
            if (this.labelNode != null && this.labelNode.hasCheckbox()) {
                this.checkbox.setSelected(this.labelNode.isSelected());
                setBackground(background);
                // re-add the label (it gets detached if used as a stand-alone
                // renderer)
                add(this.jLabel, BorderLayout.CENTER);
                result = this;
            } else {
                result = this.jLabel;
            }
            this.initialising = false;
            return result;
        }

        /** Returns the label node last rendered. */
        public TreeNode getTreeNode() {
            return this.labelNode;
        }

        /**
         * Indicates if the renderer is initialising on a component.
         * This may let listeners know to ignore (checkbox) events.
         */
        boolean isInitialising() {
            return this.initialising;
        }

        /**
         * Flag set to true during {@link #getTreeCellRendererComponent}
         * to let listeners know to ignore (checkbox) events
         */
        private boolean initialising;

        /** Returns the checkbox sub-component of this renderer. */
        public JCheckBox getCheckbox() {
            return this.checkbox;
        }

        /** Checkbox on the right hand side of the panel. */
        private final JCheckBox checkbox;

        /** Returns the inner renderer (for the label part). */
        public DefaultTreeCellRenderer getInner() {
            return this.jLabel;
        }

        /**
         * The tree on which this renderer operates.
         */
        private final CheckboxTree tree;
        /** JLabel on the center of the panel. */
        private final DefaultTreeCellRenderer jLabel;
        /** Label node last rendered. */
        private TreeNode labelNode;

        /**
         * Border to put some space to the left and right of the labels inside the
         * list.
         */
        private static final Border INSET_BORDER = new EmptyBorder(0, 2, 0, 7);

        /**
         * Overridden for performance reasons. See the <a
         * href="#override">Implementation Note</a> for more information.
         */
        @Override
        protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
            // Strings get interned...
            if ("text".equals(propertyName)
                || (("font".equals(propertyName) || "foreground".equals(propertyName))
                    && oldValue != newValue && getClientProperty(javax.swing.plaf.basic.BasicHTML.propertyKey) != null)) {

                super.firePropertyChange(propertyName, oldValue, newValue);
            }
        }

        /**
         * Overridden for performance reasons. See the <a
         * href="#override">Implementation Note</a> for more information.
         */
        @Override
        public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {
            // empty
        }

        /**
         * Overridden for performance reasons. See the <a
         * href="#override">Implementation Note</a> for more information.
         */
        @Override
        public void firePropertyChange(String propertyName, char oldValue, char newValue) {
            // empty
        }

        /**
         * Overridden for performance reasons. See the <a
         * href="#override">Implementation Note</a> for more information.
         */
        @Override
        public void firePropertyChange(String propertyName, short oldValue, short newValue) {
            // empty
        }

        /**
         * Overridden for performance reasons. See the <a
         * href="#override">Implementation Note</a> for more information.
         */
        @Override
        public void firePropertyChange(String propertyName, int oldValue, int newValue) {
            // empty
        }

        /**
         * Overridden for performance reasons. See the <a
         * href="#override">Implementation Note</a> for more information.
         */
        @Override
        public void firePropertyChange(String propertyName, long oldValue, long newValue) {
            // empty
        }

        /**
         * Overridden for performance reasons. See the <a
         * href="#override">Implementation Note</a> for more information.
         */
        @Override
        public void firePropertyChange(String propertyName, float oldValue, float newValue) {
            // empty
        }

        /**
         * Overridden for performance reasons. See the <a
         * href="#override">Implementation Note</a> for more information.
         */
        @Override
        public void firePropertyChange(String propertyName, double oldValue, double newValue) {
            // empty
        }

        /**
         * Overridden for performance reasons. See the <a
         * href="#override">Implementation Note</a> for more information.
         */
        @Override
        public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
            // empty
        }
    }

    /** Tree cell editor to be used by subclasses of the {@link CheckboxTree}. */
    static protected class CellEditor extends AbstractCellEditor implements TreeCellEditor {
        /** Constructs a new editor for the enclosing tree. */
        public CellEditor(CheckboxTree tree) {
            this.tree = tree;
            this.editor = tree.createRenderer();
            ItemListener itemListener = new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent itemEvent) {
                    if (!CellEditor.this.editor.isInitialising()) {
                        stopCellEditing();
                        TreeNode editedNode = getInner().getTreeNode();
                        editedNode.setSelected(itemEvent.getStateChange() == ItemEvent.SELECTED);
                    }
                }
            };
            getInner().getCheckbox().addItemListener(itemListener);
        }

        /** Returns the {@link TreeNode} currently being edited. */
        @Override
        public TreeNode getCellEditorValue() {
            return this.editor.getTreeNode();
        }

        /** Returns the inner editor. */
        CellRenderer getInner() {
            return this.editor;
        }

        /** A cell is editable if the mouse is over the checkbox part. */
        @Override
        public boolean isCellEditable(EventObject event) {
            boolean result = false;
            if (event instanceof MouseEvent) {
                MouseEvent mouseEvent = (MouseEvent) event;
                result = this.tree.isOverCheckBox(mouseEvent.getX(), mouseEvent.getY());
            }
            return result;
        }

        @Override
        public boolean shouldSelectCell(EventObject event) {
            return false;
        }

        /** Passes the request on to the renderer. */
        @Override
        public Component getTreeCellEditorComponent(JTree tree, Object value, boolean selected,
            boolean expanded, boolean leaf, int row) {
            Component result =
                this.editor.getTreeCellRendererComponent(tree,
                    value,
                    selected,
                    expanded,
                    leaf,
                    row,
                    false);

            return result;
        }

        /**
         * Comment for <code>tree</code>
         */
        private final CheckboxTree tree;
        /** The actual editor is just an instance of the renderer. */
        private final CellRenderer editor;
    }

    /**
     * Tree node to be displayed in a {@link CellRenderer}.
     */
    static public abstract class TreeNode extends DefaultMutableTreeNode {
        /** Indicates if this tree node should have an associated checkbox. */
        abstract public boolean hasCheckbox();

        /** Indicates if the associated checkbox is currently selected. */
        abstract public boolean isSelected();

        /** Signals to this node that the corresponding checkbox has been selected. */
        abstract public void setSelected(boolean selected);
    }

}
