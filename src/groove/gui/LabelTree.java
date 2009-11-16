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
 * $Id: LabelList.java,v 1.20 2007-12-03 08:56:08 rensink Exp $
 */
package groove.gui;

import groove.graph.Label;
import groove.gui.jgraph.JCell;
import groove.gui.jgraph.JGraph;
import groove.gui.jgraph.JModel;
import groove.gui.jgraph.JVertex;
import groove.util.Converter;
import groove.util.ObservableSet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;

/**
 * Scroll pane showing the list of labels currently appearing in the graph
 * model.
 * @author Arend Rensink
 * @version $Revision: 1915 $
 */
public class LabelTree extends JTree implements GraphModelListener,
        TreeSelectionListener {
    /**
     * Constructs a label list associated with a given jgraph and set of
     * filtered labels. {@link #updateModel()} should be called before the list
     * can be used.
     * @param jgraph the jgraph with which this list is to be associated
     */
    public LabelTree(JGraph jgraph) {
        this.filteredLabels = jgraph.getFilteredLabels();
        this.filtering = this.filteredLabels != null;
        if (this.filtering) {
            this.filteredLabels.addObserver(new Observer() {
                public void update(Observable o, Object arg) {
                    LabelTree.this.repaint();
                }
            });
        }
        // initialise the list model
        this.topNode = new DefaultMutableTreeNode();
        this.treeModel = new DefaultTreeModel(this.topNode);
        setModel(this.treeModel);
        setCellRenderer(new MyCellRenderer());
        setCellEditor(new MyCellEditor());
        setEditable(true);
        setRootVisible(false);
        setShowsRootHandles(true);
        getSelectionModel().setSelectionMode(
            TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        ToolTipManager.sharedInstance().registerComponent(this);
        this.jgraph = jgraph;
        addMouseListener(new MyMouseListener());
        // add a mouse listener to the jgraph to clear the selection of this
        // tree
        // as soon as the mouse is pressed in the jgraph
        jgraph.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                if (evt.getButton() == MouseEvent.BUTTON1
                    && !isSelectionEmpty()) {
                    clearSelection();
                }
            }
        });
        setEnabled(false);
    }

    /**
     * Returns the jgraph with which this label list is associated.
     */
    public JGraph getJGraph() {
        return this.jgraph;
    }

    /**
     * Returns an unmodifiable view on the label set maintained by this label
     * list.
     */
    public Collection<Label> getLabels() {
        return Collections.unmodifiableSet(this.labelCellMap.keySet());
    }

    /**
     * Replaces the jmodel on which this label list is based with the
     * (supposedly new) model in the associated jgraph. Gets the labels from the
     * model and adds them to this label list.
     */
    public void updateModel() {
        if (this.jmodel != null) {
            this.jmodel.removeGraphModelListener(this);
        }
        this.jmodel = this.jgraph.getModel();
        this.labelCellMap.clear();
        this.jmodel.addGraphModelListener(this);
        for (int i = 0; i < this.jmodel.getRootCount(); i++) {
            JCell cell = (JCell) this.jmodel.getRootAt(i);
            if (isListable(cell)) {
                addToLabels(cell);
            }
        }
        updateTree();
        setEnabled(true);
    }

    /**
     * Returns the set of jcells whose label sets contain a given label.
     * @param label the label looked for
     * @return the set of {@link JCell}s for which {@link JCell#getListLabels()}
     *         contains <tt>label</tt>
     */
    public Set<JCell> getJCells(Object label) {
        return this.labelCellMap.get(label);
    }

    /**
     * In addition to delegating the method to <tt>super</tt>, sets the
     * background color to <tt>null</tt> when disabled and back to the default
     * when enabled.
     */
    @Override
    public void setEnabled(boolean enabled) {
        if (enabled != isEnabled()) {
            if (!enabled) {
                this.enabledBackground = getBackground();
                setBackground(null);
            } else if (this.enabledBackground != null) {
                setBackground(this.enabledBackground);
            }
        }
        super.setEnabled(enabled);
    }

    /**
     * Updates the label list according to the change event.
     */
    public void graphChanged(GraphModelEvent e) {
        boolean changed = false;
        GraphModelEvent.GraphModelChange change = e.getChange();
        if (change instanceof JModel.RefreshEdit) {
            changed = processRefresh((JModel.RefreshEdit) change, changed);
        } else {
            changed = processRegularEdit(change, changed);
        }
        if (changed) {
            updateTree();
        }
    }

    /**
     * Records the changes imposed by a graph change that is not a
     * {@link JModel.RefreshEdit}.
     */
    private boolean processRegularEdit(GraphModelEvent.GraphModelChange change,
            boolean changed) {
        Map<?,?> changeMap = change.getAttributes();
        if (changeMap != null) {
            for (Object changeEntry : changeMap.entrySet()) {
                Object obj = ((Map.Entry<?,?>) changeEntry).getKey();
                if (isListable(obj)) { // &&
                    // attributes.containsKey(GraphConstants.VALUE))
                    // {
                    changed |= modifyLabels((JCell) obj);
                }
            }
        }
        // added cells mean added labels
        Object[] addedArray = change.getInserted();
        if (addedArray != null) {
            for (Object element : addedArray) {
                // the cell may be a port, so we have to check for
                // JCell-hood
                if (isListable(element)) {
                    JCell cell = (JCell) element;
                    changed |= addToLabels(cell);
                }
            }
        }
        // removed cells mean removed labels
        Object[] removedArray = change.getRemoved();
        if (removedArray != null) {
            for (Object element : removedArray) {
                // the cell may be a port, so we have to check for
                // JCell-hood
                if (isListable(element)) {
                    JCell cell = (JCell) element;
                    changed |= removeFromLabels(cell);
                }
            }
        }
        return changed;
    }

    /**
     * Processes the changes of a {@link JModel.RefreshEdit}.
     */
    private boolean processRefresh(JModel.RefreshEdit change, boolean changed) {
        for (JCell cell : change.getRefreshedJCells()) {
            if (isListable(cell)) {
                changed |= modifyLabels(cell);
            }
        }
        return changed;
    }

    /**
     * Callback method to determine whether a given cell should be included in
     * the label list. This should only be the case if the cell is a
     * {@link JCell} for which {@link JCell#isListable()} holds.
     */
    private boolean isListable(Object cell) {
        return cell instanceof JCell && ((JCell) cell).isListable();
    }

    /**
     * Emphasises/deemphasises cells in the associated jmodel, based on the list
     * selection.
     */
    public void valueChanged(TreeSelectionEvent e) {
        Set<JCell> emphSet = new HashSet<JCell>();
        TreePath[] selectionPaths = getSelectionPaths();
        if (selectionPaths != null) {
            for (TreePath selectedPath : selectionPaths) {
                Label label =
                    ((LabelTreeNode) selectedPath.getLastPathComponent()).getLabel();
                Set<JCell> occurrences = this.labelCellMap.get(label);
                if (occurrences != null) {
                    emphSet.addAll(occurrences);
                }
            }
        }
        this.jmodel.setEmphasized(emphSet);
    }

    /**
     * Updates the list from the internally kept label collection.
     */
    private void updateTree() {
        // temporarily remove this component as selection listener
        removeTreeSelectionListener(this);
        computeMaxLabelWidth();
        // clear the selection first
        clearSelection();
        // clear the list
        this.topNode.removeAllChildren();
        for (Label label : getLabels()) {
            LabelTreeNode labelNode = new LabelTreeNode(label);
            this.topNode.add(labelNode);
        }
        this.treeModel.reload(this.topNode);
        addTreeSelectionListener(this);
    }

    /**
     * Creates a popup menu, consisting of show and hide actions.
     */
    private JPopupMenu createPopupMenu() {
        JPopupMenu result = new JPopupMenu();
        TreePath[] selectedValues = getSelectionPaths();
        if (isFiltering() && selectedValues != null) {
            result.add(new FilterAction(selectedValues, true));
            result.add(new FilterAction(selectedValues, false));
            result.addSeparator();
        }
        // add the show/hide menu
        JPopupMenu restMenu = new ShowHideMenu(this.jgraph).getPopupMenu();
        for (int i = 0; i < restMenu.getComponentCount();) {
            result.add(restMenu.getComponent(i));
        }
        return result;
    }

    /**
     * Adds a cell to the label map. This means that for all labels of the cell,
     * the cell is inserted in that label's image. The return value indicates if
     * any labels were added
     */
    private boolean addToLabels(JCell cell) {
        boolean result = false;
        for (Label label : cell.getListLabels()) {
            result |= addToLabels(cell, label);
        }
        return result;
    }

    /**
     * Adds a cell-label pair to the label map. If the label does not yet exist
     * in the map, insetrs it. The return value indicates if the label had to be
     * created.
     */
    private boolean addToLabels(JCell cell, Label label) {
        boolean result = false;
        Set<JCell> currentCells = this.labelCellMap.get(label);
        if (currentCells == null) {
            currentCells = new HashSet<JCell>();
            this.labelCellMap.put(label, currentCells);
            result = true;
        }
        currentCells.add(cell);
        return result;
    }

    /**
     * Removes a cell from the values of the label map, and removes a label if
     * there are no cells left for it. The return value indicates if there were
     * any labels removed.
     */
    private boolean removeFromLabels(JCell cell) {
        boolean result = false;
        Iterator<Map.Entry<Label,Set<JCell>>> labelIter =
            this.labelCellMap.entrySet().iterator();
        while (labelIter.hasNext()) {
            Map.Entry<Label,Set<JCell>> labelEntry = labelIter.next();
            Set<JCell> cellSet = labelEntry.getValue();
            if (cellSet.remove(cell) && cellSet.isEmpty()) {
                labelIter.remove();
                result = true;
            }
        }
        return result;
    }

    /**
     * Modifies the presence of the cell in the label map. The return value
     * indicates if there were any labels added or removed.
     */
    private boolean modifyLabels(JCell cell) {
        boolean result = false;
        Set<Label> newLabelSet = new HashSet<Label>(cell.getListLabels());
        // go over the existing label map
        Iterator<Map.Entry<Label,Set<JCell>>> labelIter =
            this.labelCellMap.entrySet().iterator();
        while (labelIter.hasNext()) {
            Map.Entry<Label,Set<JCell>> labelEntry = labelIter.next();
            Label label = labelEntry.getKey();
            Set<JCell> cellSet = labelEntry.getValue();
            if (newLabelSet.remove(label)) {
                // the cell should be in the set
                cellSet.add(cell);
            } else if (cellSet.remove(cell) && cellSet.isEmpty()) {
                // the cell was in the set but shouldn't have been,
                // and the set is now empty
                labelIter.remove();
                result = true;
            }
        }
        // any new labels left over were not in the label map; add them
        for (Label label : newLabelSet) {
            Set<JCell> newCells = new HashSet<JCell>();
            newCells.add(cell);
            this.labelCellMap.put(label, newCells);
            result = true;
        }
        return result;
    }

    /**
     * If the object to be displayed is a {@link Label}, this implementation
     * returns an HTML-formatted string with the text of the label.
     */
    @Override
    public String convertValueToText(Object value, boolean selected,
            boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (value instanceof LabelTreeNode) {
            return getText(((LabelTreeNode) value).getLabel());
        } else {
            return super.convertValueToText(value, selected, expanded, leaf,
                row, hasFocus);
        }
    }

    /**
     * Returns an HTML-formatted string indicating how a label should be
     * displayed.
     */
    private String getText(Label label) {
        StringBuilder text = new StringBuilder();
        boolean specialLabelColour = false;
        if (label.equals(JVertex.NO_LABEL)) {
            text.append(Options.NO_LABEL_TEXT);
            specialLabelColour = true;
        } else if (label.text().length() == 0) {
            text.append(Options.EMPTY_LABEL_TEXT);
            specialLabelColour = true;
        } else {
            text.append(label.text());
        }
        Converter.toHtml(text);
        if (specialLabelColour) {
            Converter.createColorTag(SPECIAL_COLOR).on(text);
        }
        if (label.isNodeType()) {
            Converter.STRONG_TAG.on(text);
        }
        if (isFiltered(label)) {
            Converter.STRIKETHROUGH_TAG.on(text);
        }
        return Converter.HTML_TAG.on(text).toString();
    }

    /** Recomputes the value returned by {@link #getMaxLabelWidth()}. */
    private void computeMaxLabelWidth() {
        int result = 0;
        JLabel dummy = new JLabel();
        dummy.setBorder(INSET_BORDER);
        for (Label label : this.labelCellMap.keySet()) {
            dummy.setText(getText(label));
            int width = dummy.getPreferredSize().width;
            result = Math.max(result, width);
        }
        this.maxLabelWidth = result;
    }

    /** Returns the display width of the longest label in this tree. */
    public int getMaxLabelWidth() {
        return this.maxLabelWidth;
    }

    /** Indicates if this label tree supports filtering of labels. */
    public boolean isFiltering() {
        return this.filtering;
    }

    /** Indicates if a given label is currently filtered. */
    public boolean isFiltered(Label label) {
        return isFiltering() && this.filteredLabels.contains(label);
    }

    /**
     * The list model used for the JList.
     * @require <tt>listModel == listComponent.getModel()</tt>
     */
    private final DefaultTreeModel treeModel;

    /**
     * The {@link JGraph}associated to this label list.
     */
    private final JGraph jgraph;

    /**
     * The {@link JModel}currently being viewed by this label list.
     */
    private JModel jmodel;

    /**
     * The bag of labels in this jmodel.
     */
    private final Map<Label,Set<JCell>> labelCellMap =
        new TreeMap<Label,Set<JCell>>();
    /** Flag indicating if label filtering should be used. */
    private final boolean filtering;
    /** Set of filtered labels. */
    private final ObservableSet<Label> filteredLabels;
    /** The top node in the JTree. */
    private final DefaultMutableTreeNode topNode;
    /**
     * The background colour of this component when it is enabled.
     */
    private Color enabledBackground;
    /**
     * The width of the widest label in the tree. Updated by a call to
     * {@link #updateTree()}.
     */
    private int maxLabelWidth;

    private class LabelTreeNode extends DefaultMutableTreeNode {
        LabelTreeNode(Label label) {
            this.label = label;
        }

        /** Returns the label of this tree node. */
        public final Label getLabel() {
            return this.label;
        }

        @Override
        public final String toString() {
            return this.label.text();
        }

        private final Label label;
    }

    /** Class to deal with mouse events over the label list. */
    private class MyMouseListener extends MouseAdapter {
        /** Empty constructor with the correct visibility. */
        MyMouseListener() {
            // empty
        }

        @Override
        public void mousePressed(MouseEvent evt) {
            // if (evt.getButton() == MouseEvent.BUTTON3) {
            // int index =
            // getRowForLocation(evt.getPoint().x, evt.getPoint().y);
            // if (index >= 0) {
            // if (evt.isControlDown()) {
            // addSelectionInterval(index, index);
            // } else if (evt.isShiftDown()) {
            // addSelectionInterval(
            // getRowForPath(getAnchorSelectionPath()), index);
            // } else {
            // setSelectionRow(index);
            // }
            // }
            // }
            maybeShowPopup(evt);
        }

        @Override
        public void mouseReleased(MouseEvent evt) {
            maybeShowPopup(evt);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (isFiltering() && e.getClickCount() == 2) {
                TreePath path =
                    getPathForLocation(e.getPoint().x, e.getPoint().y);
                if (path != null) {
                    Label label =
                        ((LabelTreeNode) path.getLastPathComponent()).getLabel();
                    if (!LabelTree.this.filteredLabels.add(label)) {
                        LabelTree.this.filteredLabels.remove(label);
                    }
                }
            }
        }

        private void maybeShowPopup(MouseEvent evt) {
            if (evt.isPopupTrigger()) {
                createPopupMenu().show(evt.getComponent(), evt.getX(),
                    evt.getY());
            }
        }
    }

    private class FilterAction extends AbstractAction {
        FilterAction(TreePath[] cells, boolean filter) {
            super(filter ? Options.FILTER_ACTION_NAME
                    : Options.UNFILTER_ACTION_NAME);
            this.filter = filter;
            this.labels = new ArrayList<Label>();
            for (TreePath path : cells) {
                this.labels.add(((LabelTreeNode) path.getLastPathComponent()).getLabel());
            }
        }

        public void actionPerformed(ActionEvent e) {
            if (this.filter) {
                LabelTree.this.filteredLabels.addAll(this.labels);
            } else {
                LabelTree.this.filteredLabels.removeAll(this.labels);
            }
        }

        private final boolean filter;
        private final Collection<Label> labels;
    }

    /**
     * Special cell renderer that visualises the NO_LABEL label as well as
     * filtered labels.
     */
    private class MyCellRenderer extends JPanel implements TreeCellRenderer {
        /** Empty constructor with the correct visibility. */
        MyCellRenderer() {
            this.jLabel = new DefaultTreeCellRenderer();
            this.jLabel.setOpenIcon(null);
            this.jLabel.setLeafIcon(null);
            this.jLabel.setClosedIcon(null);
            this.jLabel.setBorder(LabelTree.INSET_BORDER);
            this.checkbox = new JCheckBox();
            this.checkbox.setBackground(this.jLabel.getBackgroundNonSelectionColor());
            setLayout(new BorderLayout());
            add(this.jLabel, BorderLayout.CENTER);
            setOpaque(true);
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean sel, boolean expanded, boolean leaf, int row,
                boolean hasFocus) {
            this.labelNode =
                value instanceof LabelTree.LabelTreeNode
                        ? (LabelTree.LabelTreeNode) value : null;
            if (isFiltering() && this.labelNode != null) {
                this.checkbox.setSelected(!isFiltered(this.labelNode.getLabel()));
                add(this.checkbox, BorderLayout.EAST);
            } else {
                remove(this.checkbox);
            }
            this.jLabel.getTreeCellRendererComponent(tree, value, sel,
                expanded, leaf, row, hasFocus);
            setComponentOrientation(tree.getComponentOrientation());
            if (value instanceof LabelTreeNode) {
                Label label = ((LabelTreeNode) value).getLabel();
                StringBuilder toolTipText = new StringBuilder();
                Set<JCell> occurrences = LabelTree.this.labelCellMap.get(label);
                int count = occurrences == null ? 0 : occurrences.size();
                toolTipText.append(count);
                toolTipText.append(" occurrence");
                if (count != 1) {
                    toolTipText.append("s");
                }
                if (isFiltering()) {
                    if (toolTipText.length() != 0) {
                        toolTipText.append(Converter.HTML_LINEBREAK);
                    }
                    if (LabelTree.this.filteredLabels.contains(label)) {
                        toolTipText.append("Filtered label; doubleclick to show");
                    } else {
                        toolTipText.append("Visible label; doubleclick to filter");
                    }
                }
                if (toolTipText.length() != 0) {
                    setToolTipText(Converter.HTML_TAG.on(toolTipText).toString());
                }
            }
            setOpaque(!sel);
            return this;
        }

        /** Returns the label node last rendered. */
        public LabelTree.LabelTreeNode getLabelTreeNode() {
            return this.labelNode;
        }

        /** Returns the checkbox sub-component of this renderer. */
        public JCheckBox getCheckbox() {
            return this.checkbox;
        }

        /**
         * Overrides <code>JComponent.getPreferredSize</code> to return slightly
         * wider preferred size value.
         */
        @Override
        public Dimension getPreferredSize() {
            Dimension retDimension = super.getPreferredSize();

            if (retDimension != null) {
                retDimension =
                    new Dimension(Math.min(200, getMaxLabelWidth()
                        + this.checkbox.getPreferredSize().width),
                        retDimension.height);
            }
            return retDimension;
        }

        /**
         * Overridden for performance reasons. See the <a
         * href="#override">Implementation Note</a> for more information.
         */
        @Override
        protected void firePropertyChange(String propertyName, Object oldValue,
                Object newValue) {
            // Strings get interned...
            if (propertyName == "text"
                || ((propertyName == "font" || propertyName == "foreground")
                    && oldValue != newValue && getClientProperty(javax.swing.plaf.basic.BasicHTML.propertyKey) != null)) {

                super.firePropertyChange(propertyName, oldValue, newValue);
            }
        }

        /**
         * Overridden for performance reasons. See the <a
         * href="#override">Implementation Note</a> for more information.
         */
        @Override
        public void firePropertyChange(String propertyName, byte oldValue,
                byte newValue) {
            // empty
        }

        /**
         * Overridden for performance reasons. See the <a
         * href="#override">Implementation Note</a> for more information.
         */
        @Override
        public void firePropertyChange(String propertyName, char oldValue,
                char newValue) {
            // empty
        }

        /**
         * Overridden for performance reasons. See the <a
         * href="#override">Implementation Note</a> for more information.
         */
        @Override
        public void firePropertyChange(String propertyName, short oldValue,
                short newValue) {
            // empty
        }

        /**
         * Overridden for performance reasons. See the <a
         * href="#override">Implementation Note</a> for more information.
         */
        @Override
        public void firePropertyChange(String propertyName, int oldValue,
                int newValue) {
            // empty
        }

        /**
         * Overridden for performance reasons. See the <a
         * href="#override">Implementation Note</a> for more information.
         */
        @Override
        public void firePropertyChange(String propertyName, long oldValue,
                long newValue) {
            // empty
        }

        /**
         * Overridden for performance reasons. See the <a
         * href="#override">Implementation Note</a> for more information.
         */
        @Override
        public void firePropertyChange(String propertyName, float oldValue,
                float newValue) {
            // empty
        }

        /**
         * Overridden for performance reasons. See the <a
         * href="#override">Implementation Note</a> for more information.
         */
        @Override
        public void firePropertyChange(String propertyName, double oldValue,
                double newValue) {
            // empty
        }

        /**
         * Overridden for performance reasons. See the <a
         * href="#override">Implementation Note</a> for more information.
         */
        @Override
        public void firePropertyChange(String propertyName, boolean oldValue,
                boolean newValue) {
            // empty
        }

        /** JLabel on the center of the panel. */
        private final DefaultTreeCellRenderer jLabel;
        /** Checkbox on the right hand side of the panel. */
        private final JCheckBox checkbox;
        /** Label node last rendered. */
        private LabelTree.LabelTreeNode labelNode;
    }

    /** Cell editor enabling the selection of the filtering checkboxes. */
    private class MyCellEditor extends AbstractCellEditor implements
            TreeCellEditor {
        public MyCellEditor() {
            ItemListener itemListener = new ItemListener() {
                public void itemStateChanged(ItemEvent itemEvent) {
                    stopCellEditing();
                    LabelTreeNode editedNode =
                        MyCellEditor.this.editor.getLabelTreeNode();
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                        LabelTree.this.filteredLabels.remove(editedNode.getLabel());
                    } else {
                        LabelTree.this.filteredLabels.add(editedNode.getLabel());
                    }
                }
            };
            this.editor.getCheckbox().addItemListener(itemListener);
        }

        /** Returns the {@link LabelTreeNode} currently being edited. */
        public Object getCellEditorValue() {
            return this.editor.getLabelTreeNode();
        }

        /** A cell is editable if it is a {@link LabelTreeNode}. */
        @Override
        public boolean isCellEditable(EventObject event) {
            boolean result = false;
            if (event instanceof MouseEvent) {
                MouseEvent mouseEvent = (MouseEvent) event;
                TreePath path =
                    LabelTree.this.getPathForLocation(mouseEvent.getX(),
                        mouseEvent.getY());
                if (path != null) {
                    Rectangle pathBounds = getPathBounds(path);
                    int checkboxBorder =
                        pathBounds.x
                            + pathBounds.width
                            - this.editor.getCheckbox().getPreferredSize().width;
                    result =
                        path.getLastPathComponent() instanceof LabelTreeNode
                            && mouseEvent.getX() >= checkboxBorder;
                }
            }
            return result;
        }

        @Override
        public boolean shouldSelectCell(EventObject event) {
            return false;
        }

        public Component getTreeCellEditorComponent(JTree tree, Object value,
                boolean selected, boolean expanded, boolean leaf, int row) {

            Component editor =
                this.editor.getTreeCellRendererComponent(tree, value, selected,
                    expanded, leaf, row, false);

            return editor;
        }

        /** The actual editor is just an instance of the renderer. */
        private final MyCellRenderer editor = new MyCellRenderer();
    }

    /**
     * Border to put some space to the left and right of the labels inside the
     * list.
     */
    public static final Border INSET_BORDER = new EmptyBorder(0, 2, 0, 7);
    /** Colour HTML tag for the foreground colour of special labels. */
    private static final Color SPECIAL_COLOR = Color.LIGHT_GRAY;
}