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
package nl.utwente.groove.gui.tree;

import static nl.utwente.groove.io.HTMLConverter.HTML_LINEBREAK;
import static nl.utwente.groove.io.HTMLConverter.HTML_TAG;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;

import nl.utwente.groove.grammar.type.TypeLabel;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.Label;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.action.ActionStore;
import nl.utwente.groove.gui.display.DismissDelayer;
import nl.utwente.groove.gui.jgraph.AspectJGraph;
import nl.utwente.groove.gui.jgraph.JCell;
import nl.utwente.groove.gui.jgraph.JGraph;
import nl.utwente.groove.gui.jgraph.JModel;
import nl.utwente.groove.gui.menu.ShowHideMenu;
import nl.utwente.groove.gui.tree.LabelFilter.Entry;
import nl.utwente.groove.io.HTMLConverter;

/**
 * Scroll pane showing the list of labels currently appearing in the graph
 * model.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class LabelTree<G extends Graph> extends CheckboxTree
    implements GraphModelListener, TreeSelectionListener {
    /**
     * Constructs a label list associated with a given jgraph. A further
     * parameter indicates if the label tree should support subtypes.
     * @param jGraph the jgraph with which this list is to be associated
     * @param filtering if {@code true}, the panel has checkboxes to filter labels
     */
    public LabelTree(JGraph<G> jGraph, boolean filtering) {
        this.jGraph = jGraph;
        this.labelFilter = createLabelFilter();
        this.filtering = filtering;
        // make sure tool tips get displayed
        ToolTipManager.sharedInstance().registerComponent(this);
        setEnabled(jGraph.isEnabled());
        setLargeModel(true);
        installListeners();
    }

    /** Callback factory method for the label filter. */
    LabelFilter<G> createLabelFilter() {
        return new LabelFilter<>();
    }

    @SuppressWarnings("unchecked")
    void installListeners() {
        getJGraph()
            .addPropertyChangeListener(org.jgraph.JGraph.GRAPH_MODEL_PROPERTY,
                                       evt -> updateModel());
        getJGraph().addGraphSelectionListener(evt -> clearSelection());

        getFilter().addObserver(evt -> {
            LabelTree.this.repaint();
            getJGraph().refreshCells((Set<JCell<G>>) evt.getNewValue());
        });
        addMouseListener(new MyMouseListener());
    }

    /** Adds this tree as listener to a given (non-{@code null}) JModel. */
    void installJModelListeners(JModel<G> jModel) {
        jModel.addGraphModelListener(this);
    }

    /** Removes this tree as listener to a given (non-{@code null}) JModel. */
    void removeJModelListeners(JModel<G> jModel) {
        jModel.removeGraphModelListener(this);
    }

    @Override
    protected CellRenderer createRenderer() {
        return new LabelTreeCellRenderer();
    }

    /**
     * Creates an action that, on invocation,
     * will filter all labels occurring in a given array of cells.
     */
    public @Nullable Action createFilterAction(Object[] cells) {
        if (isFiltering()) {
            return new FilterAction(cells);
        } else {
            return null;
        }
    }

    /**
     * Returns the jgraph with which this label list is associated.
     */
    public JGraph<G> getJGraph() {
        return this.jGraph;
    }

    /**
     * The JGraph permanently associated with this label list.
     */
    private final JGraph<G> jGraph;

    /**
     * Returns the jModel with which this label list is associated.
     */
    public @Nullable JModel<G> getJModel() {
        return this.jModel;
    }

    /**
     * Returns the jModel with which this label list is associated.
     */
    public JModel<G> getNonNullJModel() {
        var result = getJModel();
        assert result != null;
        return result;
    }

    /**
     * The {@link JModel} currently being viewed by this label list.
     */
    private @Nullable JModel<G> jModel;

    /** Returns the label filter associated with this label tree. */
    LabelFilter<G> getFilter() {
        return this.labelFilter;
    }

    /** Set of filtered labels. */
    private final LabelFilter<G> labelFilter;

    /** Indicates if this label tree is actively filtering. */
    public boolean isFiltering() {
        return this.filtering;
    }

    /** Flag indicating if there is active filtering. */
    private final boolean filtering;

    /**
     * Returns the set of labels maintained by this label
     * tree.
     */
    public SortedMap<Entry,Set<JCell<G>>> getLabels() {
        TreeMap<Entry,Set<JCell<G>>> result = new TreeMap<>();
        for (Entry entry : getFilter().getEntries()) {
            result.put(entry, getFilter().getJCells(entry));
        }
        return result;
    }

    /**
     * Refreshes the labels according to the jModel,
     * if the jModel has changed.
     */
    public void synchroniseModel() {
        if (isModelStale()) {
            updateModel();
        }
    }

    /** Tests if the model underlying this label tree is stale w.r.t. the JGraph. */
    boolean isModelStale() {
        return this.jModel != getJGraph().getModel();
    }

    /**
     * Replaces the jmodel on which this label list is based with the
     * (supposedly new) model in the associated jgraph. Gets the labels from the
     * model and adds them to this label list.
     */
    public void updateModel() {
        if (this.jModel != null) {
            removeJModelListeners(this.jModel);
        }
        this.jModel = getJGraph().getModel();
        clearFilter();
        if (this.jModel != null) {
            installJModelListeners(this.jModel);
            updateFilter();
        }
        updateTree();
        setEnabled(this.jModel != null);
    }

    /**
     * Clears the filter, in preparation to updating it from the model.
     */
    void clearFilter() {
        getFilter().clear();
    }

    /**
     * Reloads the filter from the model.
     */
    void updateFilter() {
        for (JCell<G> cell : getNonNullJModel().getRoots()) {
            getFilter().addJCell(cell);
        }
    }

    /**
     * Updates the list from the internally kept label collection.
     */
    void updateTree() {
        // temporarily remove this component as selection listener
        removeTreeSelectionListener(this);
        // remember the collapsed paths
        Set<TreeNode> collapsedNodes = new HashSet<>();
        for (int i = 0; i < getRowCount(); i++) {
            if (isCollapsed(i)) {
                TreeNode child = (TreeNode) getPathForRow(i).getLastPathComponent();
                if (child.getChildCount() > 0) {
                    collapsedNodes.add(child);
                }
            }
        }
        // clear the selection first
        clearSelection();
        // clear the list
        getTopNode().removeAllChildren();
        List<TreeNode> newNodes = fillTree();
        getModel().reload(getTopNode());
        // expand those paths that were not collapsed before
        for (TreeNode newNode : newNodes) {
            if (newNode.isLeaf()) {
                continue;
            }
            boolean expand = true;
            TreePath path = new TreePath(newNode.getPath());
            for (Object node : path.getPath()) {
                if (collapsedNodes.contains(node)) {
                    expand = false;
                    break;
                }
            }
            if (expand) {
                expandPath(path);
            }
        }
        addTreeSelectionListener(this);
    }

    /** Updates the tree from the labels in the filter. */
    List<TreeNode> fillTree() {
        List<TreeNode> result = new ArrayList<>();
        Set<Entry> entries = new TreeSet<>(getFilter().getEntries());
        for (Entry entry : entries) {
            if (getFilter().hasJCells(entry)) {
                LabelTreeNode labelNode = new LabelTreeNode(this, entry, true);
                getTopNode().add(labelNode);
            }
        }
        return result;
    }

    /**
     * Updates the label list according to the change event.
     */
    @Override
    public void graphChanged(@Nullable GraphModelEvent evt) {
        boolean changed = evt != null && processEdit(evt.getChange());
        if (changed) {
            updateTree();
        }
    }

    /**
     * Records the changes imposed by a graph change.
     */
    private boolean processEdit(GraphModelEvent.GraphModelChange change) {
        boolean result = false;
        // insertions double as changes, so we do insertions first
        // and remove them from the change map
        Map<Object,Object> changeMap = new HashMap<>();
        Map<?,?> storedChange = change.getAttributes();
        if (storedChange != null) {
            changeMap.putAll(storedChange);
        }
        // added cells mean added labels
        Object[] addedArray = change.getInserted();
        if (addedArray != null) {
            for (Object element : addedArray) {
                // the cell may be a port, so we have to check for
                // JCell-hood
                if (element instanceof JCell) {
                    @SuppressWarnings("unchecked")
                    JCell<G> jCell = (JCell<G>) element;
                    result |= getFilter().addJCell(jCell);
                }
                changeMap.remove(element);
            }
        }
        for (Object changeEntry : changeMap.entrySet()) {
            Object element = ((Map.Entry<?,?>) changeEntry).getKey();
            if (element instanceof JCell) {
                @SuppressWarnings("unchecked")
                JCell<G> jCell = (JCell<G>) element;
                result |= getFilter().modifyJCell(jCell);
            }
        }
        // removed cells mean removed labels
        Object[] removedArray = change.getRemoved();
        if (removedArray != null) {
            for (Object element : removedArray) {
                // the cell may be a port, so we have to check for
                // JCell-hood
                if (element instanceof JCell) {
                    @SuppressWarnings("unchecked")
                    JCell<G> jCell = (JCell<G>) element;
                    result |= getFilter().removeJCell(jCell);
                }
            }
        }
        return result;
    }

    /**
     * Emphasises/deemphasises cells in the associated jmodel, based on the list
     * selection.
     */
    @Override
    public void valueChanged(@Nullable TreeSelectionEvent e) {
        Set<JCell<?>> emphSet = new HashSet<>();
        TreePath[] selectionPaths = getSelectionPaths();
        if (selectionPaths != null) {
            for (TreePath selectedPath : selectionPaths) {
                Object treeNode = selectedPath.getLastPathComponent();
                if (treeNode instanceof LabelTree.LabelTreeNode) {
                    Entry entry = ((LabelTreeNode) treeNode).getEntry();
                    Set<JCell<G>> occurrences = getFilter().getJCells(entry);
                    if (occurrences != null) {
                        emphSet.addAll(occurrences);
                    }
                }
            }
        }
        this.jGraph.setSelectionCells(emphSet.toArray());
    }

    /**
     * Creates a popup menu, consisting of show and hide actions.
     */
    private JPopupMenu createPopupMenu() {
        JPopupMenu result = new JPopupMenu();
        addActionItems(result);
        addFilterItems(result);
        addShowHideItems(result);
        return result;
    }

    /** Adds menu items for colouring and find/replace actions. */
    private void addActionItems(JPopupMenu result) {
        TreePath[] selectedValues = getSelectionPaths();
        ActionStore actions = getJGraph().getActions();
        if (selectedValues != null && selectedValues.length == 1 && actions != null) {
            result.add(actions.getFindReplaceAction());
            if (getJGraph() instanceof AspectJGraph && actions.getSelectColorAction().isEnabled()) {
                result.add(actions.getSelectColorAction());
            }
            result.addSeparator();
        }
    }

    /** Adds menu items to start or stop filtering the selected paths. */
    void addFilterItems(JPopupMenu menu) {
        TreePath[] selectedValues = getSelectionPaths();
        if (isFiltering() && selectedValues != null) {
            menu.add(new FilterAction(selectedValues, true));
            menu.add(new FilterAction(selectedValues, false));
            menu.addSeparator();
        }
    }

    /** Adds menu items for graying out. */
    private void addShowHideItems(JPopupMenu result) {
        // add the show/hide menu
        @SuppressWarnings({"unchecked", "rawtypes"})
        JPopupMenu restMenu = new ShowHideMenu(this.jGraph).getPopupMenu();
        while (restMenu.getComponentCount() > 0) {
            result.add(restMenu.getComponent(0));
        }
    }

    /**
     * If the object to be displayed is a {@link Label}, this implementation
     * returns an HTML-formatted string with the text of the label.
     */
    @Override
    public String convertValueToText(@Nullable Object value, boolean selected, boolean expanded,
                                     boolean leaf, int row, boolean hasFocus) {
        if (value instanceof LabelTreeNode labelNode) {
            Entry entry = labelNode.getEntry();
            return HTML_TAG.on(getText(entry)).toString();
        } else {
            return super.convertValueToText(value, selected, expanded, leaf, row, hasFocus);
        }
    }

    /**
     * Returns an HTML-formatted string indicating how a label should be
     * displayed.
     */
    protected StringBuilder getText(Entry entry) {
        StringBuilder result = new StringBuilder();
        var line = entry.getLine();
        boolean specialLabelColour = false;
        if (line.toFlatString().equals(TypeLabel.NODE_LABEL_TEXT)) {
            result.append(Options.NO_LABEL_TEXT);
            specialLabelColour = true;
        } else if (entry.toString().isEmpty()) {
            result.append(Options.EMPTY_LABEL_TEXT);
            specialLabelColour = true;
        } else {
            result.append(line.toHTMLString());
        }
        if (specialLabelColour) {
            HTMLConverter.createColorTag(SPECIAL_COLOR).on(result);
        }
        if (!entry.isSelected()) {
            HTMLConverter.STRIKETHROUGH_TAG.on(result);
        }
        return result;
    }

    /** Indicates if a given jCell is entirely filtered.
     * @return {@code true} if the jCell is currently visible
     */
    public boolean isIncluded(JCell<G> jCell) {
        synchroniseModel();
        return getFilter().isIncluded(jCell);
    }

    /** Indicates if a given key is actively filtered.
     * @return {@code true} if the key is currently visible
     */
    public boolean isIncluded(Label key) {
        synchroniseModel();
        return getFilter().getEntry(key).isSelected();
    }

    /** Colour HTML tag for the foreground colour of special labels. */
    private static final Color SPECIAL_COLOR = Color.LIGHT_GRAY;

    /** Tree node wrapping a filter entry. */
    public static class LabelTreeNode extends TreeNode {
        /**
         * Constructs a new node, for a given filter entry.
         * @param entry The label wrapped in this node
         * @param topNode flag indicating if this is a top type node in the tree
         */
        LabelTreeNode(LabelTree<?> tree, Entry entry, boolean topNode) {
            this.tree = tree;
            this.entry = entry;
            this.topNode = topNode;
        }

        @Override
        public int hashCode() {
            return this.entry.hashCode();
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof LabelTree.LabelTreeNode other)) {
                return false;
            }
            return this.entry.equals(other.entry);
        }

        /** Returns the label of this tree node. */
        public Entry getEntry() {
            return this.entry;
        }

        /** Indicates if this node is a top label type node in the tree. */
        public final boolean isTopNode() {
            return this.topNode;
        }

        /** Returns the (possibly {@code null}) icon of this node. */
        public @Nullable Icon getIcon() {
            return null;
        }

        /** Indicates if this tree node has a node filtering checkbox. */
        @Override
        public final boolean hasCheckbox() {
            return this.tree.isFiltering() && isTopNode();
        }

        @Override
        public boolean isSelected() {
            return getEntry().isSelected();
        }

        @Override
        public void setSelected(boolean selected) {
            this.tree.getFilter().setSelected(getEntry(), selected);
        }

        @Override
        public final String toString() {
            return "Tree node for " + this.entry.toString();
        }

        private final LabelTree<?> tree;
        private final Entry entry;
        private final boolean topNode;
    }

    /** Class to deal with mouse events over the label list. */
    private class MyMouseListener extends DismissDelayer {
        /**
         * Creates the listener for this tree.
         */
        MyMouseListener() {
            super(LabelTree.this);
        }

        @Override
        public void mouseReleased(@Nullable MouseEvent evt) {
            maybeShowPopup(evt);
        }

        @Override
        public void mouseClicked(@Nullable MouseEvent evt) {
            if (evt != null && evt.getClickCount() == 2) {
                TreePath path = getPathForLocation(evt.getPoint().x, evt.getPoint().y);
                if (path != null) {
                    Object treeNode = path.getLastPathComponent();
                    if (treeNode instanceof LabelTree.LabelTreeNode) {
                        Entry entry = ((LabelTreeNode) treeNode).getEntry();
                        getFilter().changeSelected(entry);
                    }
                }
            }
        }

        private void maybeShowPopup(@Nullable MouseEvent evt) {
            if (evt != null && evt.isPopupTrigger()) {
                createPopupMenu().show(evt.getComponent(), evt.getX(), evt.getY());
            }
        }
    }

    private class FilterAction extends AbstractAction {
        FilterAction(Object[] cells) {
            super(Options.FILTER_UNSELECT_ACTION_NAME);
            this.filter = true;
            this.entries = new ArrayList<>();
            for (Object cell : cells) {
                @SuppressWarnings("unchecked")
                JCell<G> jCell = (JCell<G>) cell;
                this.entries.addAll(getFilter().getEntries(jCell));
            }
        }

        FilterAction(TreePath[] cells, boolean filter) {
            super(filter
                ? Options.FILTER_UNSELECT_ACTION_NAME
                : Options.FILTE_SELECT_ACTION_NAME);
            this.filter = filter;
            this.entries = new ArrayList<>();
            for (TreePath path : cells) {
                Object treeNode = path.getLastPathComponent();
                if (treeNode instanceof LabelTree.LabelTreeNode) {
                    Entry entry = ((LabelTreeNode) treeNode).getEntry();
                    this.entries.add(entry);
                }
            }
        }

        @Override
        public void actionPerformed(@Nullable ActionEvent e) {
            getFilter().setSelected(this.entries, !this.filter);
        }

        private final boolean filter;
        private final Collection<Entry> entries;
    }

    /** Adds an icon, tool tip text and label colour. */
    protected class LabelTreeCellRenderer extends CellRenderer {
        /** Constructs an empty renderer. */
        public LabelTreeCellRenderer() {
            super(LabelTree.this);
        }

        @Override
        public JComponent getTreeCellRendererComponent(@Nullable JTree tree, @Nullable Object value,
                                                       boolean sel, boolean expanded, boolean leaf,
                                                       int row, boolean hasFocus) {
            JComponent result = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
                                                                   row, hasFocus);
            // set a sub- or supertype icon if the node label is a subnode
            Icon labelIcon = null;
            if (getTreeNode() instanceof LabelTreeNode entryNode) {
                Entry entry = entryNode.getEntry();
                labelIcon = entryNode.getIcon();
                // set tool tip text
                StringBuilder toolTipText = new StringBuilder();
                int count = getFilter().getCount(entry);
                toolTipText.append(count);
                toolTipText.append(" occurrence");
                if (count != 1) {
                    toolTipText.append("s");
                }
                if (isFiltering()) {
                    if (toolTipText.length() != 0) {
                        toolTipText.append(HTML_LINEBREAK);
                    }
                    var labelType = entry.isForNode()
                        ? "node type"
                        : "edge label";
                    if (entry.isSelected()) {
                        if (entry.isPassive()) {
                            toolTipText.append("Passively included ");
                            toolTipText.append(labelType);
                            toolTipText.append(HTML_LINEBREAK);
                            toolTipText.append("Excluded if ");
                            toolTipText
                                .append(entry.isForNode()
                                    ? "supertype"
                                    : "source or target node");
                            toolTipText.append(" is excluded");
                            toolTipText.append(HTML_LINEBREAK);
                            toolTipText.append("Select to actively include");
                        } else {
                            toolTipText.append("Actively included ");
                            toolTipText.append(labelType);
                            toolTipText.append(HTML_LINEBREAK);
                            toolTipText.append("Unselect to exclude");
                        }
                    } else {
                        if (entry.isPassive()) {
                            toolTipText.append("Passively excluded ");
                            toolTipText.append(labelType);
                            toolTipText.append(HTML_LINEBREAK);
                            toolTipText.append("Included if incident edge is actively included");
                            toolTipText.append(HTML_LINEBREAK);
                            toolTipText.append("Unselect to actively exclude");
                        } else {
                            toolTipText.append("Actively excluded ");
                            toolTipText.append(labelType);
                            toolTipText.append(HTML_LINEBREAK);
                            toolTipText.append("Select to include");
                        }
                    }
                }
                if (toolTipText.length() != 0) {
                    result.setToolTipText(HTMLConverter.HTML_TAG.on(toolTipText).toString());
                }
            }
            getInner().setIcon(labelIcon);
            return result;
        }
    }
}