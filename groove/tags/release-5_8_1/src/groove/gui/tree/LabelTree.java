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
package groove.gui.tree;

import static groove.io.HTMLConverter.HTML_TAG;
import groove.grammar.type.TypeEdge;
import groove.grammar.type.TypeElement;
import groove.grammar.type.TypeLabel;
import groove.grammar.type.TypeNode;
import groove.graph.Graph;
import groove.graph.Label;
import groove.gui.Options;
import groove.gui.action.ActionStore;
import groove.gui.jgraph.AspectJGraph;
import groove.gui.jgraph.JCell;
import groove.gui.jgraph.JGraph;
import groove.gui.jgraph.JModel;
import groove.gui.menu.ShowHideMenu;
import groove.gui.tree.LabelFilter.Entry;
import groove.gui.tree.TypeFilter.TypeEntry;
import groove.io.HTMLConverter;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.SortedSet;
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

import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;

/**
 * Scroll pane showing the list of labels currently appearing in the graph
 * model.
 * @author Arend Rensink
 * @version $Revision$
 */
public class LabelTree<G extends Graph> extends CheckboxTree implements GraphModelListener,
        TreeSelectionListener {
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

    void installListeners() {
        getJGraph().addPropertyChangeListener(org.jgraph.JGraph.GRAPH_MODEL_PROPERTY,
            new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    updateModel();
                }
            });
        getJGraph().addGraphSelectionListener(new GraphSelectionListener() {
            @Override
            public void valueChanged(GraphSelectionEvent e) {
                clearSelection();
            }
        });
        getFilter().addObserver(new Observer() {
            @Override
            @SuppressWarnings("unchecked")
            public void update(Observable o, Object arg) {
                LabelTree.this.repaint();
                getJGraph().refreshCells((Set<JCell<G>>) arg);
            }
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
        return new MyTreeCellRenderer();
    }

    /**
     * Creates an action that, on invocation,
     * will filter all labels occurring in a given array of cells.
     */
    public Action createFilterAction(Object[] cells) {
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

    /** Returns the label filter associated with this label tree. */
    LabelFilter<G> getFilter() {
        return this.labelFilter;
    }

    /** Indicates if this label tree has a label filter. */
    public boolean hasFilter() {
        return getFilter() != null;
    }

    /** Indicates if this label tree is actively filtering. */
    public boolean isFiltering() {
        return this.filtering;
    }

    /**
     * Returns the set of labels maintained by this label
     * tree.
     */
    public SortedSet<Label> getLabels() {
        TreeSet<Label> result = new TreeSet<>();
        for (Entry entry : getFilter().getEntries()) {
            result.add(entry.getLabel());
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
        if (hasFilter()) {
            clearFilter();
        }
        if (this.jModel != null) {
            installJModelListeners(this.jModel);
            if (hasFilter()) {
                updateFilter();
            }
        }
        updateTree();
        setEnabled(this.jModel != null);
    }

    /**
     * Clears the filter, in preparation to updating it from the model.
     * Only called when {@link #hasFilter()} holds.
     */
    void clearFilter() {
        getFilter().clear();
    }

    /**
     * Reloads the filter from the model.
     * Only called when {@link #hasFilter()} holds.
     */
    void updateFilter() {
        for (JCell<G> cell : this.jModel.getRoots()) {
            if (cell.getVisuals().isVisible()) {
                getFilter().addJCell(cell);
            }
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
                EntryNode labelNode = new EntryNode(this, entry, true);
                getTopNode().add(labelNode);
            }
        }
        return result;
    }

    /**
     * Updates the label list according to the change event.
     */
    @Override
    public void graphChanged(GraphModelEvent e) {
        boolean changed = false;
        GraphModelEvent.GraphModelChange change = e.getChange();
        changed = processEdit(change, changed);
        if (changed) {
            updateTree();
        }
    }

    /**
     * Records the changes imposed by a graph change.
     */
    private boolean processEdit(GraphModelEvent.GraphModelChange change, boolean changed) {
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
                    changed |= getFilter().addJCell(jCell);
                }
                changeMap.remove(element);
            }
        }
        for (Object changeEntry : changeMap.entrySet()) {
            Object element = ((Map.Entry<?,?>) changeEntry).getKey();
            if (element instanceof JCell) {
                @SuppressWarnings("unchecked")
                JCell<G> jCell = (JCell<G>) element;
                changed |= getFilter().modifyJCell(jCell);
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
                    changed |= getFilter().removeJCell(jCell);
                }
            }
        }
        return changed;
    }

    /**
     * Emphasises/deemphasises cells in the associated jmodel, based on the list
     * selection.
     */
    @Override
    public void valueChanged(TreeSelectionEvent e) {
        Set<JCell<?>> emphSet = new HashSet<>();
        TreePath[] selectionPaths = getSelectionPaths();
        if (selectionPaths != null) {
            for (TreePath selectedPath : selectionPaths) {
                Object treeNode = selectedPath.getLastPathComponent();
                if (treeNode instanceof LabelTree.EntryNode) {
                    Entry entry = ((EntryNode) treeNode).getEntry();
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
    public String convertValueToText(Object value, boolean selected, boolean expanded,
            boolean leaf, int row, boolean hasFocus) {
        if (value instanceof LabelTree.EntryNode) {
            Entry entry = ((EntryNode) value).getEntry();
            return getText(entry);
        } else {
            return super.convertValueToText(value, selected, expanded, leaf, row, hasFocus);
        }
    }

    /**
     * Returns an HTML-formatted string indicating how a label should be
     * displayed.
     */
    private String getText(Entry entry) {
        StringBuilder text = new StringBuilder();
        Label label = entry.getLabel();
        boolean specialLabelColour = false;
        if (label.equals(TypeLabel.NODE)) {
            text.append(Options.NO_LABEL_TEXT);
            specialLabelColour = true;
        } else if (label.text().length() == 0) {
            text.append(Options.EMPTY_LABEL_TEXT);
            specialLabelColour = true;
        } else {
            text.append(label.toLine().toHTMLString());
        }
        if (specialLabelColour) {
            HTMLConverter.createColorTag(SPECIAL_COLOR).on(text);
        }
        if (!getFilter().isSelected(entry)) {
            HTMLConverter.STRIKETHROUGH_TAG.on(text);
        }
        return HTML_TAG.on(text).toString();
    }

    /** Indicates if a given jCell is entirely filtered. */
    public boolean isFiltered(JCell<G> jCell) {
        synchroniseModel();
        return hasFilter() && getFilter().isFiltered(jCell, getJGraph().isShowUnfilteredEdges());
    }

    /** Indicates if a given key is actively filtered. */
    public boolean isFiltered(Label key) {
        synchroniseModel();
        return hasFilter() && !getFilter().isSelected(getFilter().getEntry(key));
    }

    /**
     * The JGraph permanently associated with this label list.
     */
    private final JGraph<G> jGraph;

    /**
     * The {@link JModel} currently being viewed by this label list.
     */
    private JModel<G> jModel;
    /** Set of filtered labels. */
    private final LabelFilter<G> labelFilter;
    /** Flag indicating if there is active filtering. */
    private final boolean filtering;

    /** Colour HTML tag for the foreground colour of special labels. */
    private static final Color SPECIAL_COLOR = Color.LIGHT_GRAY;

    /** Tree node wrapping a filter entry. */
    public static class EntryNode extends TreeNode {
        /**
         * Constructs a new node, for a given filter entry.
         * @param entry The label wrapped in this node
         * @param topNode flag indicating if this is a top type node in the tree
         */
        EntryNode(LabelTree<?> tree, Entry entry, boolean topNode) {
            this.tree = tree;
            this.entry = entry;
            this.topNode = topNode;
        }

        @Override
        public int hashCode() {
            return this.entry.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof LabelTree.EntryNode)) {
                return false;
            }
            EntryNode other = (EntryNode) obj;
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
        public Icon getIcon() {
            return null;
        }

        /** Indicates if this tree node has a node filtering checkbox. */
        @Override
        public final boolean hasCheckbox() {
            return this.tree.isFiltering() && isTopNode();
        }

        @Override
        public boolean isSelected() {
            return this.tree.getFilter().isSelected(getEntry());
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
    private class MyMouseListener extends MouseAdapter {
        @Override
        public void mouseReleased(MouseEvent evt) {
            maybeShowPopup(evt);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (hasFilter() && e.getClickCount() == 2) {
                TreePath path = getPathForLocation(e.getPoint().x, e.getPoint().y);
                if (path != null) {
                    Object treeNode = path.getLastPathComponent();
                    if (treeNode instanceof LabelTree.EntryNode) {
                        Entry entry = ((EntryNode) treeNode).getEntry();
                        getFilter().changeSelected(entry);
                    }
                }
            }
        }

        private void maybeShowPopup(MouseEvent evt) {
            if (evt.isPopupTrigger()) {
                createPopupMenu().show(evt.getComponent(), evt.getX(), evt.getY());
            }
        }
    }

    private class FilterAction extends AbstractAction {
        FilterAction(Object[] cells) {
            super(Options.FILTER_ACTION_NAME);
            this.filter = true;
            this.entries = new ArrayList<>();
            for (Object cell : cells) {
                @SuppressWarnings("unchecked")
                JCell<G> jCell = (JCell<G>) cell;
                this.entries.addAll(getFilter().getEntries(jCell));
            }
        }

        FilterAction(TreePath[] cells, boolean filter) {
            super(filter ? Options.FILTER_ACTION_NAME : Options.UNFILTER_ACTION_NAME);
            this.filter = filter;
            this.entries = new ArrayList<>();
            for (TreePath path : cells) {
                Object treeNode = path.getLastPathComponent();
                if (treeNode instanceof LabelTree.EntryNode) {
                    Entry entry = ((EntryNode) treeNode).getEntry();
                    this.entries.add(entry);
                }
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            getFilter().setSelected(this.entries, !this.filter);
        }

        private final boolean filter;
        private final Collection<Entry> entries;
    }

    /** Adds an icon, tool tip text and label colour. */
    private class MyTreeCellRenderer extends CellRenderer {
        public MyTreeCellRenderer() {
            super(LabelTree.this);
        }

        @Override
        public JComponent getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
                boolean expanded, boolean leaf, int row, boolean hasFocus) {
            JComponent result =
                super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            // set a sub- or supertype icon if the node label is a subnode
            Icon labelIcon = null;
            if (getTreeNode() instanceof LabelTree.EntryNode) {
                EntryNode entryNode = (EntryNode) getTreeNode();
                Entry entry = entryNode.getEntry();
                labelIcon = entryNode.getIcon();
                // set tool tip text
                StringBuilder toolTipText = new StringBuilder();
                Set<JCell<G>> occurrences = getFilter().getJCells(entry);
                int count = occurrences == null ? 0 : occurrences.size();
                toolTipText.append(count);
                toolTipText.append(" occurrence");
                if (count != 1) {
                    toolTipText.append("s");
                }
                if (isFiltering()) {
                    if (toolTipText.length() != 0) {
                        toolTipText.append(HTMLConverter.HTML_LINEBREAK);
                    }
                    if (getFilter().isSelected(entry)) {
                        toolTipText.append("Visible label; doubleclick to filter");
                    } else {
                        toolTipText.append("Filtered label; doubleclick to show");
                    }
                }
                if (toolTipText.length() != 0) {
                    result.setToolTipText(HTMLConverter.HTML_TAG.on(toolTipText).toString());
                }
                // set node colour
                if (entry instanceof TypeEntry) {
                    TypeElement typeElement = ((TypeEntry) entry).getType();
                    TypeNode typeNode =
                        typeElement instanceof TypeNode ? (TypeNode) typeElement
                                : ((TypeEdge) typeElement).source();
                    Color color = typeNode.getColor();
                    if (color != null) {
                        getInner().setForeground(color);
                    }
                }
            }
            getInner().setIcon(labelIcon);
            return result;
        }
    }
}