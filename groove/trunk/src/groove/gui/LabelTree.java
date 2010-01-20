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

import groove.graph.DefaultLabel;
import groove.graph.Label;
import groove.graph.LabelStore;
import groove.gui.jgraph.JCell;
import groove.gui.jgraph.JGraph;
import groove.gui.jgraph.JModel;
import groove.gui.jgraph.JVertex;
import groove.util.Converter;
import groove.util.Groove;
import groove.util.ObservableSet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.DropMode;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.TransferHandler;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicTreeUI;
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
     * Constructs a label list associated with a given jgraph. A further
     * parameter indicates if the label stree should support subtypes.
     * {@link #updateModel()} should be called before the list can be used.
     * @param jgraph the jgraph with which this list is to be associated
     * @param supportsSubtypes if <code>true</code>, the tree should support
     *        subtype display and operations, by using the jgraph's label store.
     */
    public LabelTree(JGraph jgraph, boolean supportsSubtypes) {
        this.jgraph = jgraph;
        this.supportsSubtypes = supportsSubtypes;
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
        // set drag and drop
        setDragEnabled(getLabelStore() != null && !getLabelStore().isFixed());
        setDropMode(DropMode.ON_OR_INSERT);
        setTransferHandler(new MyTransferHandler());
        // make sure the checkbox never selects the label
        // note that the BasicTreeUI may not be what is used in the current LAF,
        // but I don't know any other way to modify the selection behaviour
        setUI(new BasicTreeUI() {
            @Override
            protected void selectPathForEvent(TreePath path, MouseEvent event) {
                if (!isOverCheckBox(path, event.getPoint().x)) {
                    super.selectPathForEvent(path, event);
                }
            }
        });
        // set selection mode
        getSelectionModel().setSelectionMode(
            TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        // make sure tool tips get displayed
        ToolTipManager.sharedInstance().registerComponent(this);
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

    /** Creates a tool bar for the label tree. */
    JToolBar createToolBar() {
        JToolBar result = null;
        if (isSupportsSubtypes()) {
            result = new JToolBar();
            result.setFloatable(false);
            result.add(getShowSubtypesButton());
            result.add(getShowSupertypesButton());
            result.addSeparator();
            result.add(getShowAllLabelsButton());
            // put the sub- and supertype buttons in a button group
            ButtonGroup modeButtonGroup = new ButtonGroup();
            modeButtonGroup.add(getShowSubtypesButton());
            modeButtonGroup.add(getShowSupertypesButton());
        }
        return result;
    }

    /**
     * Returns the button for the show-subtypes action, lazily creating it
     * first.
     */
    private JToggleButton getShowSubtypesButton() {
        if (this.showSubtypesButton == null) {
            this.showSubtypesButton =
                new JToggleButton(new ShowModeAction(true));
            this.showSubtypesButton.setSelected(true);
            this.showSubtypesButton.setMargin(new Insets(1, 1, 1, 1));
        }
        return this.showSubtypesButton;
    }

    /**
     * Returns the button for the show-supertypes action, lazily creating it
     * first.
     */
    private JToggleButton getShowSupertypesButton() {
        if (this.showSupertypesButton == null) {
            this.showSupertypesButton =
                new JToggleButton(new ShowModeAction(false));
            this.showSupertypesButton.setMargin(new Insets(1, 1, 1, 1));
        }
        return this.showSupertypesButton;
    }

    /**
     * Returns the button for the show-supertypes action, lazily creating it
     * first.
     */
    private JToggleButton getShowAllLabelsButton() {
        if (this.showAllLabelsButton == null) {
            this.showAllLabelsButton =
                new JToggleButton(new ShowAllLabelsAction());
            this.showAllLabelsButton.setMargin(new Insets(1, 1, 1, 1));
            // this.showAllLabelsButton.setMargin(new Insets(4, 4, 4, 4));
        }
        return this.showAllLabelsButton;
    }

    /**
     * Returns the jgraph with which this label list is associated.
     */
    public JGraph getJGraph() {
        return this.jgraph;
    }

    /** Convenience method to return the label store of the jgraph. */
    private LabelStore getLabelStore() {
        return this.jgraph.getLabelStore();
    }

    /**
     * Returns an unmodifiable view on the label set maintained by this label
     * tree.
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
        setDragEnabled(getLabelStore() != null && !getLabelStore().isFixed());
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
            // if (!enabled) {
            // this.enabledBackground = getBackground();
            // setBackground(null);
            // } else if (this.enabledBackground != null) {
            // setBackground(this.enabledBackground);
            // }
            setBackground(getColor(enabled));
        }
        getShowAllLabelsButton().setEnabled(enabled);
        getShowSubtypesButton().setEnabled(enabled);
        getShowSupertypesButton().setEnabled(enabled);
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
        // clear the selection first
        clearSelection();
        // clear the list
        this.topNode.removeAllChildren();
        Set<Label> labels = new TreeSet<Label>(getLabels());
        LabelStore labelStore = getLabelStore();
        if (isShowsAllLabels() && labelStore != null) {
            labels.addAll(labelStore.getLabels());
        }
        Set<LabelTreeNode> newNodes = new HashSet<LabelTreeNode>();
        for (Label label : labels) {
            LabelTreeNode labelNode = new LabelTreeNode(label, true);
            this.topNode.add(labelNode);
            if (labelStore != null && labelStore.getLabels().contains(label)) {
                addRelatedTypes(labelNode, isShowsSubtypes()
                        ? labelStore.getDirectSubtypeMap()
                        : labelStore.getDirectSupertypeMap(), newNodes);
            }
        }
        this.treeModel.reload(this.topNode);
        for (LabelTreeNode newNode : newNodes) {
            expandPath(new TreePath(newNode.getPath()));
        }
        addTreeSelectionListener(this);
    }

    /** Recursively adds related types to a given label node. */
    private void addRelatedTypes(LabelTreeNode labelNode,
            Map<Label,Set<Label>> map, Set<LabelTreeNode> newNodes) {
        Label label = labelNode.getLabel();
        Set<Label> relatedTypes = map.get(label);
        assert relatedTypes != null : String.format(
            "Label '%s' does not occur in label store '%s'", label,
            map.keySet());
        for (Label type : relatedTypes) {
            LabelTreeNode typeNode = new LabelTreeNode(type, false);
            labelNode.add(typeNode);
            newNodes.add(labelNode);
            addRelatedTypes(typeNode, map, newNodes);
        }
    }

    /**
     * Creates a popup menu, consisting of show and hide actions.
     */
    private JPopupMenu createPopupMenu() {
        JPopupMenu result = new JPopupMenu();
        TreePath[] selectedValues = getSelectionPaths();
        boolean itemAdded = false;
        Simulator simulator = getJGraph().getSimulator();
        if (selectedValues != null && selectedValues.length == 1
            && simulator != null) {
            result.add(simulator.getRelabelAction());
            itemAdded = true;
        }
        if (isFiltering() && selectedValues != null) {
            result.add(new FilterAction(selectedValues, true));
            result.add(new FilterAction(selectedValues, false));
            itemAdded = true;
        }
        if (itemAdded) {
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
            text.append(DefaultLabel.toHtmlString(label));
        }
        if (specialLabelColour) {
            Converter.createColorTag(SPECIAL_COLOR).on(text);
        }
        if (isFiltered(label)) {
            Converter.STRIKETHROUGH_TAG.on(text);
        }
        return Converter.HTML_TAG.on(text).toString();
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
     * Indicates if this tree supports subtypes.
     */
    private boolean isSupportsSubtypes() {
        return this.supportsSubtypes;
    }

    /**
     * Indicates if this tree is currently showing all labels, or just those
     * existing in the graph.
     */
    private boolean isShowsAllLabels() {
        return this.showsAllLabels;
    }

    /**
     * Changes the value of the show-all-labels flag.
     */
    private void setShowsAllLabels(boolean show) {
        this.showsAllLabels = show;
    }

    /**
     * Indicates if this tree is currently showing subtype relations.
     */
    private boolean isShowsSubtypes() {
        return this.showsSubtypes;
    }

    /**
     * Changes the value of the show-subtype flag.
     */
    private void setShowsSubtypes(boolean show) {
        this.showsSubtypes = show;
    }

    /**
     * Adds an observer to this label tree. The observers will be updates when
     * the subtyping relation changes as a result of a drag-and-drop action in
     * the label tree.
     */
    public void addLabelStoreObserver(Observer observer) {
        this.labelStoreChange.addObserver(observer);
    }

    /** Tests if a given x-coordinate is over the checkbox part of a tree path. */
    private boolean isOverCheckBox(TreePath path, int x) {
        boolean result = false;
        if (path != null
            && path.getLastPathComponent() instanceof LabelTreeNode) {
            LabelTreeNode labelNode =
                (LabelTreeNode) path.getLastPathComponent();
            Rectangle pathBounds = getPathBounds(path);
            if (CHECKBOX_ORIENTATION.equals(BorderLayout.WEST)) {
                int checkboxBorder = pathBounds.x + CHECKBOX_WIDTH;
                result = labelNode.hasFilterControl() && x < checkboxBorder;
            } else {
                int checkboxBorder =
                    pathBounds.x + pathBounds.width - CHECKBOX_WIDTH;
                result = labelNode.hasFilterControl() && x >= checkboxBorder;
            }
        }
        return result;
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
    /** Observable used to signal changes to the label store. */
    private final Observable labelStoreChange = new Observable() {
        @Override
        public void notifyObservers(Object arg) {
            // make sure the notification indeed reaches the observers
            setChanged();
            super.notifyObservers(arg);
        }
    };

    /** Flag indicating if this instance of the label tree supports subtypes. */
    private final boolean supportsSubtypes;
    /** Mode of the label tree: showing subtypes or supertypes. */
    private boolean showsSubtypes = true;
    /** Mode of the label tree: showing all labels or just those in the graph. */
    private boolean showsAllLabels = false;

    /** Button for setting the show subtypes mode. */
    private JToggleButton showSubtypesButton;
    /** Button for setting the show supertypes mode. */
    private JToggleButton showSupertypesButton;
    /** Button for setting the show all actions mode. */
    private JToggleButton showAllLabelsButton;

    /**
     * Returns the icon for subtype or supertype mode, depending on the
     * parameter.
     */
    private static Icon getModeIcon(boolean subtypes) {
        return subtypes ? Groove.OPEN_UP_ARROW_ICON
                : Groove.OPEN_DOWN_ARROW_ICON;
    }

    /**
     * Border to put some space to the left and right of the labels inside the
     * list.
     */
    public static final Border INSET_BORDER = new EmptyBorder(0, 2, 0, 7);

    /** Colour HTML tag for the foreground colour of special labels. */
    private static final Color SPECIAL_COLOR = Color.LIGHT_GRAY;

    /** Orientation of the filtering checkboxes in the label cells. */
    private static final String CHECKBOX_ORIENTATION = BorderLayout.WEST;

    /** Preferred width of a checkbox. */
    private static final int CHECKBOX_WIDTH =
        new JCheckBox().getPreferredSize().width;

    /** Returns the appropriate background colour for an enabledness condition. */
    static private Color getColor(boolean enabled) {
        return enabled ? ENABLED_COLOUR : DISABLED_COLOUR;
    }

    private static JTextField enabledField = new JTextField();
    private static JTextField disabledField = new JTextField();
    static {
        enabledField.setEditable(true);
        disabledField.setEditable(false);
    }
    /** The background colour of an enabled component. */
    private static Color ENABLED_COLOUR = enabledField.getBackground();
    /** The background colour of a disabled component. */
    private static Color DISABLED_COLOUR = disabledField.getBackground();

    /** Tree node wrapping a label. */
    public class LabelTreeNode extends DefaultMutableTreeNode {
        /**
         * Constructs a new node, for a given label.
         * @param label The label wrapped in this node
         * @param topNode flag indicating if this is a top type node in the tree
         */
        LabelTreeNode(Label label, boolean topNode) {
            this.label = label;
            this.topNode = topNode;
        }

        /** Returns the label of this tree node. */
        public final Label getLabel() {
            return this.label;
        }

        /** Indicates if this node is a top label type node in the tree. */
        public final boolean isTopNode() {
            return this.topNode;
        }

        /** Indicates if this tree node has a node filtering checkbox. */
        public final boolean hasFilterControl() {
            return isFiltering() && isTopNode();
        }

        @Override
        public final String toString() {
            return "Tree node for " + this.label.text();
        }

        private final Label label;
        private final boolean topNode;
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
            this.checkbox.setOpaque(false);
            setLayout(new BorderLayout());
            add(this.jLabel, BorderLayout.CENTER);
            add(this.checkbox, CHECKBOX_ORIENTATION);
            setBorder(new EmptyBorder(0, 2, 0, 0));
            setComponentOrientation(LabelTree.this.getComponentOrientation());
            setOpaque(false);
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean sel, boolean expanded, boolean leaf, int row,
                boolean hasFocus) {
            JComponent result;
            this.jLabel.getTreeCellRendererComponent(tree, value, sel,
                expanded, leaf, row, hasFocus);
            Color background = getColor(LabelTree.this.isEnabled());
            boolean isDropCell = isDropCell(tree, row);
            // this.jLabel.setBackgroundNonSelectionColor(background);
            this.jLabel.setOpaque(!sel && !isDropCell);
            this.labelNode =
                value instanceof LabelTree.LabelTreeNode
                        ? (LabelTree.LabelTreeNode) value : null;
            if (this.labelNode != null && this.labelNode.hasFilterControl()) {
                this.checkbox.setSelected(!isFiltered(this.labelNode.getLabel()));
                setBackground(background);
                // re-add the label (it gets detached if used as a stand-alone
                // renderer)
                add(this.jLabel, BorderLayout.CENTER);
                result = this;
            } else {
                result = this.jLabel;
            }
            // set a sub- or supertype icon if the node label is a subnode
            Icon labelIcon = null;
            if (this.labelNode != null && !this.labelNode.isTopNode()) {
                labelIcon = getModeIcon(isShowsSubtypes());
            }
            this.jLabel.setIcon(labelIcon);
            // set tool tip text
            if (this.labelNode != null) {
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
                    result.setToolTipText(Converter.HTML_TAG.on(toolTipText).toString());
                }
            }
            return result;
        }

        private boolean isDropCell(JTree tree, int row) {
            JTree.DropLocation dropLocation = tree.getDropLocation();
            return dropLocation != null && dropLocation.getChildIndex() == -1
                && tree.getRowForPath(dropLocation.getPath()) == row;

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
                result = isOverCheckBox(path, mouseEvent.getX());
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

    private class MyTransferHandler extends TransferHandler {
        @Override
        public int getSourceActions(JComponent c) {
            return COPY_OR_MOVE;
        }

        @Override
        public boolean canImport(TransferSupport support) {
            boolean result = false;
            JTree.DropLocation location =
                (JTree.DropLocation) support.getDropLocation();
            TreePath dropPath = location.getPath();
            if (dropPath != null) {
                if (dropPath.getLastPathComponent() instanceof LabelTreeNode) {
                    LabelTreeNode labelNode =
                        (LabelTreeNode) dropPath.getLastPathComponent();
                    result =
                        labelNode.getLabel().isNodeType()
                            && location.getChildIndex() < 0;
                } else {
                    result = true;
                }
            }
            return result;
        }

        @Override
        public boolean importData(TransferSupport support) {
            boolean result = false;
            try {
                // decompose transferred data
                Map<Label,Set<Label>> draggedLabels =
                    new HashMap<Label,Set<Label>>();
                String data =
                    (String) support.getTransferable().getTransferData(
                        DataFlavor.stringFlavor);
                for (String dataRow : data.split("\n")) {
                    int separatorIndex = dataRow.indexOf(' ');
                    if (separatorIndex < 0) {
                        Label keyType =
                            DefaultLabel.createLabel(dataRow, Label.NODE_TYPE);
                        if (!draggedLabels.containsKey(keyType)) {
                            draggedLabels.put(keyType, new HashSet<Label>());
                        }
                    } else {
                        Label keyType =
                            DefaultLabel.createLabel(dataRow.substring(0,
                                separatorIndex), Label.NODE_TYPE);
                        Label valueType =
                            DefaultLabel.createLabel(
                                dataRow.substring(separatorIndex + 1),
                                Label.NODE_TYPE);
                        Set<Label> values = draggedLabels.get(keyType);
                        if (values == null) {
                            draggedLabels.put(keyType, values =
                                new HashSet<Label>());
                        }
                        values.add(valueType);
                    }
                }
                JTree.DropLocation location =
                    (JTree.DropLocation) support.getDropLocation();
                LabelStore newStore = getLabelStore().clone();
                // first remove subtypings if action was move
                if (support.getDropAction() == MOVE) {
                    for (Map.Entry<Label,Set<Label>> dragEntry : draggedLabels.entrySet()) {
                        for (Label value : dragEntry.getValue()) {
                            Label oldSubtype =
                                isShowsSubtypes() ? dragEntry.getKey() : value;
                            Label oldSupertype =
                                isShowsSubtypes() ? value : dragEntry.getKey();
                            newStore.removeSubtype(oldSupertype, oldSubtype);
                        }
                    }
                }
                // now add new subtypings, if the drop is on an existing node
                // type
                if (location.getChildIndex() < 0) {
                    TreePath dropPath = location.getPath();
                    Label targetType =
                        ((LabelTreeNode) dropPath.getLastPathComponent()).getLabel();
                    for (Label keyType : draggedLabels.keySet()) {
                        Label newSubtype =
                            isShowsSubtypes() ? keyType : targetType;
                        Label newSupertype =
                            isShowsSubtypes() ? targetType : keyType;
                        if (!newStore.getSubtypes(newSubtype).contains(
                            newSupertype)) {
                            newStore.addSubtype(newSupertype, newSubtype);
                        }
                    }
                }
                if (!newStore.equals(getLabelStore())) {
                    LabelTree.this.labelStoreChange.notifyObservers(newStore);
                    result = true;
                }
            } catch (IOException exc) {
                // do nothing
            } catch (UnsupportedFlavorException exc) {
                // do nothing
            }
            return result;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            Transferable result = null;
            if (isSupportsSubtypes() && !isSelectionEmpty()) {
                StringBuffer content = new StringBuffer();
                List<TreePath> keepSelection = new ArrayList<TreePath>();
                for (TreePath path : getSelectionPaths()) {
                    Label label =
                        ((LabelTreeNode) path.getLastPathComponent()).getLabel();
                    if (label.isNodeType()) {
                        content.append(label.text());
                        Object parentNode =
                            path.getParentPath().getLastPathComponent();
                        if (parentNode instanceof LabelTreeNode) {
                            content.append(" ");
                            content.append(((LabelTreeNode) parentNode).getLabel().text());
                        }
                        content.append("\n");
                        keepSelection.add(path);
                    }
                }
                if (keepSelection.size() > 0) {
                    setSelectionPaths(keepSelection.toArray(new TreePath[0]));
                    result = new StringSelection(content.toString());
                }
            }
            return result;
        }

    }

    /** Action changing the show mode to showing subtypes or supertypes. */
    private class ShowModeAction extends AbstractAction {
        /**
         * Creates an action, with a parameter indicating if it is subtypes or
         * supertypes that should be shown.
         * @param subtypes if <code>true</code>, the action should show
         *        subtypes; otherwise, it should show supertypes.
         */
        public ShowModeAction(boolean subtypes) {
            super(null, getModeIcon(subtypes));
            this.subtypes = subtypes;
            putValue(Action.SHORT_DESCRIPTION, computeName());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (isShowsSubtypes() != this.subtypes) {
                setShowsSubtypes(this.subtypes);
                updateTree();
            }
        }

        /**
         * Returns the appropriate name for this action, based on the current
         * value of {@link #subtypes}
         */
        private String computeName() {
            return this.subtypes ? Options.SHOW_SUBTYPES_ACTION_NAME
                    : Options.SHOW_SUPERTYPES_ACTION_NAME;
        }

        /** Flag indicating if this action should show subtypes. */
        private final boolean subtypes;
    }

    /**
     * Action flipping the show mode between all labels and just the labels in
     * the current graph.
     */
    private class ShowAllLabelsAction extends AbstractAction {
        public ShowAllLabelsAction() {
            super(null, Groove.E_A_CHOICE_ICON);
            putValue(Action.SHORT_DESCRIPTION, computeName());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            setShowsAllLabels(!isShowsAllLabels());
            setName(computeName());
            putValue(Action.SHORT_DESCRIPTION, computeName());
            updateTree();
        }

        /**
         * Returns the appropriate name for this action, based on the current
         * value of {@link #isShowsAllLabels()}
         */
        private String computeName() {
            return isShowsAllLabels()
                    ? Options.SHOW_EXISTING_LABELS_ACTION_NAME
                    : Options.SHOW_ALL_LABELS_ACTION_NAME;
        }
    }
}