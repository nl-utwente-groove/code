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

import static groove.io.HTMLConverter.HTML_TAG;
import static groove.io.HTMLConverter.ITALIC_TAG;
import static groove.io.HTMLConverter.STRONG_TAG;
import groove.graph.Element;
import groove.graph.GraphRole;
import groove.graph.Label;
import groove.graph.TypeEdge;
import groove.graph.TypeElement;
import groove.graph.TypeGraph;
import groove.graph.TypeLabel;
import groove.graph.TypeNode;
import groove.gui.LabelFilter.Entry;
import groove.gui.LabelFilter.TypeEntry;
import groove.gui.action.ActionStore;
import groove.gui.jgraph.AspectJGraph;
import groove.gui.jgraph.GraphJCell;
import groove.gui.jgraph.GraphJGraph;
import groove.gui.jgraph.GraphJModel;
import groove.gui.jgraph.GraphJVertex;
import groove.io.HTMLConverter;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;

/**
 * Scroll pane showing the list of labels currently appearing in the graph
 * model.
 * @author Arend Rensink
 * @version $Revision: 1915 $
 */
public class LabelTree extends CheckboxTree implements GraphModelListener,
        TreeSelectionListener {
    /**
     * Constructs a label list associated with a given jgraph. A further
     * parameter indicates if the label stree should support subtypes.
     * {@link #updateModel()} should be called before the list can be used.
     * @param jGraph the jgraph with which this list is to be associated
     * @param toolBar if {@code true}, the panel should have a tool bar
     * @param filtering if {@code true}, the panel has checkboxes to filter labels
     */
    public LabelTree(GraphJGraph jGraph, boolean toolBar, boolean filtering) {
        this.jGraph = jGraph;
        this.labelFilter = new LabelFilter();
        this.filtering = filtering;
        this.toolBar = toolBar;
        if (filtering) {
            this.labelFilter.addObserver(new Observer() {
                public void update(Observable o, Object arg) {
                    LabelTree.this.repaint();
                }
            });
        }
        // make sure tool tips get displayed
        ToolTipManager.sharedInstance().registerComponent(this);
        addMouseListener(new MyMouseListener());
        setEnabled(jGraph.isEnabled());
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
        return new FilterAction(cells);
    }

    /** Creates a tool bar for the label tree. */
    JToolBar createToolBar() {
        JToolBar result = null;
        if (this.toolBar) {
            result = Options.createToolBar();
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
                Options.createToggleButton(new ShowModeAction(true));
            this.showSubtypesButton.setSelected(true);
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
                Options.createToggleButton(new ShowModeAction(false));
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
                Options.createToggleButton(new ShowAllLabelsAction());
        }
        return this.showAllLabelsButton;
    }

    /**
     * Returns the jgraph with which this label list is associated.
     */
    public GraphJGraph getJGraph() {
        return this.jGraph;
    }

    /** Convenience method to return the type graph of the jGraph. */
    TypeGraph getTypeGraph() {
        return getJGraph() instanceof AspectJGraph
                ? ((AspectJGraph) getJGraph()).getTypeGraph() : null;
    }

    /** Convenience method to return the type graph map of the jGraph. */
    SortedMap<String,TypeGraph> getTypeGraphMap() {
        return getJGraph() instanceof AspectJGraph
                ? ((AspectJGraph) getJGraph()).getTypeGraphMap() : null;
    }

    /** Returns the label filter associated with this label tree. */
    public LabelFilter getFilter() {
        synchroniseModel();
        return this.labelFilter;
    }

    /**
     * Returns the set of labels maintained by this label
     * tree.
     */
    public SortedSet<Label> getLabels() {
        TreeSet<Label> result = new TreeSet<Label>();
        for (Entry entry : getFilter().getEntries()) {
            result.add(entry.getLabel());
        }
        return result;
    }

    /** 
     * Refreshes the labels according to the jModel,
     * if the jModel has changed.
     */
    private void synchroniseModel() {
        if (this.jModel != getJGraph().getModel()) {
            updateModel();
        }
    }

    /**
     * Replaces the jmodel on which this label list is based with the
     * (supposedly new) model in the associated jgraph. Gets the labels from the
     * model and adds them to this label list.
     */
    public void updateModel() {
        if (this.jModel != null) {
            this.jModel.removeGraphModelListener(this);
        }
        this.jModel = getJGraph().getModel();
        if (this.typeGraph != getTypeGraph()) {
            this.typeGraph = getTypeGraph();
            getFilter().clear(
                this.typeGraph == null || this.typeGraph.isImplicit());
        } else {
            getFilter().clearJCells();
        }
        if (this.jModel == null) {
            getFilter().clear(true);
        } else {
            this.jModel.addGraphModelListener(this);
            if (getTypeGraph() != null) {
                for (TypeNode node : getTypeGraph().nodeSet()) {
                    getFilter().addEntry(node);
                }
                for (TypeEdge edge : getTypeGraph().edgeSet()) {
                    getFilter().addEntry(edge);
                }
            }
            for (int i = 0; i < this.jModel.getRootCount(); i++) {
                GraphJCell cell = (GraphJCell) this.jModel.getRootAt(i);
                if (isListable(cell)) {
                    getFilter().addJCell(cell);
                }
            }
        }
        updateTree();
        setEnabled(this.jModel != null);
    }

    /**
     * Enables the buttons in addition to delegating the method to <tt>super</tt>.
     */
    @Override
    public void setEnabled(boolean enabled) {
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
        changed = processRegularEdit(change, changed);
        if (changed) {
            updateTree();
        }
    }

    /**
     * Records the changes imposed by a graph change.
     */
    private boolean processRegularEdit(GraphModelEvent.GraphModelChange change,
            boolean changed) {
        Map<?,?> changeMap = change.getAttributes();
        if (changeMap != null) {
            for (Object changeEntry : changeMap.entrySet()) {
                Object obj = ((Map.Entry<?,?>) changeEntry).getKey();
                if (isListable(obj)) {
                    changed |= getFilter().modifyJCell((GraphJCell) obj);
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
                    changed |= getFilter().addJCell((GraphJCell) element);
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
                    changed |= getFilter().removeJCell((GraphJCell) element);
                }
            }
        }
        return changed;
    }

    /**
     * Callback method to determine whether a given cell should be included in
     * the label list. This should only be the case if the cell is a
     * {@link GraphJCell}.
     */
    private boolean isListable(Object cell) {
        return cell instanceof GraphJCell;
    }

    /**
     * Emphasises/deemphasises cells in the associated jmodel, based on the list
     * selection.
     */
    public void valueChanged(TreeSelectionEvent e) {
        Set<GraphJCell> emphSet = new HashSet<GraphJCell>();
        TreePath[] selectionPaths = getSelectionPaths();
        if (selectionPaths != null) {
            for (TreePath selectedPath : selectionPaths) {
                Object treeNode = selectedPath.getLastPathComponent();
                if (treeNode instanceof EntryNode) {
                    Entry entry = ((EntryNode) treeNode).getEntry();
                    Set<GraphJCell> occurrences = getFilter().getJCells(entry);
                    if (occurrences != null) {
                        emphSet.addAll(occurrences);
                    }
                }
            }
        }
        this.jGraph.setSelectionCells(emphSet.toArray());
    }

    /**
     * Updates the list from the internally kept label collection.
     */
    private void updateTree() {
        // temporarily remove this component as selection listener
        removeTreeSelectionListener(this);
        // remember the collapsed paths
        Set<TreeNode> collapsedNodes = new HashSet<TreeNode>();
        for (int i = 0; i < getRowCount(); i++) {
            if (isCollapsed(i)) {
                TreeNode child =
                    (TreeNode) getPathForRow(i).getLastPathComponent();
                if (child.getChildCount() > 0) {
                    collapsedNodes.add(child);
                }
            }
        }
        // clear the selection first
        clearSelection();
        // clear the list
        getTopNode().removeAllChildren();
        List<TreeNode> newNodes =
            getFilter().isLabelBased() ? updateTreeFromLabels()
                    : updateTreeFromTypeGraph();
        getModel().reload(getTopNode());
        // expand those paths that were not collapsed before
        for (TreeNode newNode : newNodes) {
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
    private List<TreeNode> updateTreeFromLabels() {
        List<TreeNode> result = new ArrayList<TreeNode>();
        Set<Entry> entries = new TreeSet<Entry>(getFilter().getEntries());
        for (Entry entry : entries) {
            if (isShowsAllLabels() || getFilter().hasJCells(entry)) {
                EntryNode labelNode = new EntryNode(entry, true);
                getTopNode().add(labelNode);
            }
        }
        return result;
    }

    /** Updates the tree from the information in the type graph map. */
    private List<TreeNode> updateTreeFromTypeGraph() {
        List<TreeNode> newNodes = new ArrayList<TreeNode>();
        SortedMap<String,TypeGraph> typeGraphMap = getTypeGraphMap();
        if (typeGraphMap.size() == 1) {
            String name = typeGraphMap.firstKey();
            newNodes = updateTreeFromTypeGraph(getTopNode(), name);
        } else if (this.jModel.getGraph().getRole() == GraphRole.TYPE) {
            newNodes =
                updateTreeFromTypeGraph(getTopNode(),
                    this.jModel.getGraph().getName());
        } else {
            newNodes = new ArrayList<TreeNode>();
            for (Map.Entry<String,TypeGraph> typeGraphEntry : typeGraphMap.entrySet()) {
                String name = typeGraphEntry.getKey();
                TypeGraphTreeNode typeGraphNode = new TypeGraphTreeNode(name);
                getTopNode().add(typeGraphNode);
                newNodes.add(typeGraphNode);
                newNodes.addAll(updateTreeFromTypeGraph(typeGraphNode, name));
            }
        }
        return newNodes;
    }

    /** Updates the tree from the information in a given type graph. */
    private List<TreeNode> updateTreeFromTypeGraph(
            DefaultMutableTreeNode topNode, String name) {
        TypeGraph typeGraph = getTypeGraphMap().get(name);
        List<TreeNode> newNodes = new ArrayList<TreeNode>();
        // mapping from type nodes to related types (in the combined type graph)
        Map<TypeNode,Set<TypeNode>> relatedMap =
            isShowsSubtypes() ? getTypeGraph().getDirectSubtypeMap()
                    : getTypeGraph().getDirectSupertypeMap();
        for (TypeNode node : new TreeSet<TypeNode>(typeGraph.nodeSet())) {
            Entry entry = getFilter().getEntry(node);
            if (isShowsAllLabels() || getFilter().hasJCells(entry)) {
                EntryNode nodeTypeNode = new EntryNode(entry, true);
                topNode.add(nodeTypeNode);
                newNodes.add(nodeTypeNode);
                addRelatedTypes(typeGraph, nodeTypeNode, relatedMap, newNodes);
                for (TypeEdge edge : new TreeSet<TypeEdge>(
                    typeGraph.outEdgeSet(node))) {
                    Entry edgeEntry = getFilter().getEntry(edge);
                    EntryNode edgeTypeNode = new EntryNode(edgeEntry, true);
                    nodeTypeNode.add(edgeTypeNode);
                    newNodes.add(edgeTypeNode);
                }
            }
        }
        return newNodes;
    }

    /**
     * Recursively adds related types to a given label node.
     * Only first level subtypes are added.
     * @param typeGraph partial type graph from which the related types are taken
     * @param typeNode tree node for the key type
     * @param map mapping from key types to related node type (in the combined type graph)
     * @param newNodes set that collects all newly created tree nodes  
     */
    private void addRelatedTypes(TypeGraph typeGraph, EntryNode typeNode,
            Map<TypeNode,Set<TypeNode>> map, List<TreeNode> newNodes) {
        TypeNode type = (TypeNode) ((TypeEntry) typeNode.getEntry()).getType();
        Set<TypeNode> relatedTypes = map.get(type);
        assert relatedTypes != null : String.format(
            "Node type '%s' does not occur in type graph '%s'", type,
            map.keySet());
        for (TypeNode relType : relatedTypes) {
            // test if the node type label exists in the partial type graph
            if (typeGraph.getLabels().contains(relType.label())) {
                EntryNode subTypeNode = new EntryNode(relType, false);
                typeNode.add(subTypeNode);
                if (newNodes != null) {
                    newNodes.add(typeNode);
                }
                // change last parameter to newNodes if subtypes should be added
                // to arbitrary depth
                addRelatedTypes(typeGraph, subTypeNode, map, null);
            }
        }
    }

    /**
     * Creates a popup menu, consisting of show and hide actions.
     */
    private JPopupMenu createPopupMenu() {
        JPopupMenu result = new JPopupMenu();
        TreePath[] selectedValues = getSelectionPaths();
        ActionStore actions = getJGraph().getActions();
        if (selectedValues != null && selectedValues.length == 1
            && actions != null) {
            result.add(actions.getFindReplaceAction());
            if (getJGraph() instanceof AspectJGraph
                && actions.getSelectColorAction().isEnabled()) {
                result.add(actions.getSelectColorAction());
            }
            result.addSeparator();
        }
        if (isFiltering() && selectedValues != null) {
            result.add(new FilterAction(selectedValues, true));
            result.add(new FilterAction(selectedValues, false));
            result.addSeparator();
        }
        // add the show/hide menu
        JPopupMenu restMenu = new ShowHideMenu(this.jGraph).getPopupMenu();
        while (restMenu.getComponentCount() > 0) {
            result.add(restMenu.getComponent(0));
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
        if (value instanceof EntryNode) {
            return getText(((EntryNode) value).getEntry());
        } else if (value instanceof TypeGraphTreeNode) {
            StringBuilder result = new StringBuilder();
            result.append("Type graph '");
            result.append(((TypeGraphTreeNode) value).getName());
            result.append("'");
            return HTMLConverter.HTML_TAG.on(
                ITALIC_TAG.on(STRONG_TAG.on(result))).toString();
        } else {
            return super.convertValueToText(value, selected, expanded, leaf,
                row, hasFocus);
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
        if (label.equals(TypeLabel.NODE) || label.equals(GraphJVertex.NO_LABEL)) {
            text.append(Options.NO_LABEL_TEXT);
            specialLabelColour = true;
        } else if (label.text().length() == 0) {
            text.append(Options.EMPTY_LABEL_TEXT);
            specialLabelColour = true;
        } else {
            text.append(TypeLabel.toHtmlString(label));
        }
        if (specialLabelColour) {
            HTMLConverter.createColorTag(SPECIAL_COLOR).on(text);
        }
        if (isFiltered(entry)) {
            HTMLConverter.STRIKETHROUGH_TAG.on(text);
        }
        return HTML_TAG.on(text).toString();
    }

    /** Indicates if this label tree supports filtering of labels. */
    public boolean isFiltering() {
        return this.filtering;
    }

    /** Indicates if a given label is currently filtered. */
    public boolean isFiltered(Element element) {
        return isFiltered(getFilter().getEntry(element));
    }

    /** Indicates if a given tree entry is currently filtered. */
    private boolean isFiltered(Entry entry) {
        synchroniseModel();
        return isFiltering() && !getFilter().isSelected(entry);
    }

    /**
     * Indicates if this tree is currently showing subtype relations.
     */
    boolean isShowsSubtypes() {
        return this.showsSubtypes;
    }

    /**
     * Changes the value of the show-subtype flag.
     */
    private void setShowsSubtypes(boolean show) {
        this.showsSubtypes = show;
    }

    /**
     * Indicates if this tree is currently showing all labels, or just those
     * existing in the graph.
     */
    public boolean isShowsAllLabels() {
        return this.showsAllLabels;
    }

    /**
     * Changes the value of the show-all-labels flag.
     */
    public void setShowsAllLabels(boolean show) {
        this.showsAllLabels = show;
    }

    /**
     * The {@link GraphJGraph}associated to this label list.
     */
    private final GraphJGraph jGraph;

    /**
     * The {@link GraphJModel}currently being viewed by this label list.
     */
    private GraphJModel<?,?> jModel;
    /** The type graph in the model, if any. */
    private TypeGraph typeGraph;
    /** Flag indicating if a tool bar should be used. */
    private final boolean toolBar;
    /** Flag indicating if label filtering should be used. */
    private final boolean filtering;
    /** Set of filtered labels. */
    private final LabelFilter labelFilter;
    /** Mode of the label tree: showing all labels or just those in the graph. */
    private boolean showsAllLabels = false;
    /** Mode of the label tree: showing subtypes or supertypes. */
    private boolean showsSubtypes = true;
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
    static Icon getModeIcon(boolean subtypes) {
        return subtypes ? Icons.ARROW_OPEN_UP_ICON : Icons.ARROW_OPEN_DOWN_ICON;
    }

    /** Colour HTML tag for the foreground colour of special labels. */
    private static final Color SPECIAL_COLOR = Color.LIGHT_GRAY;

    /** Tree node wrapping a filter entry. */
    public class EntryNode extends TreeNode {
        /**
         * Constructs a new node, for a given type element.
         * @param key the key element wrapped in this node
         * @param topNode flag indicating if this is a top type node in the tree
         */
        EntryNode(TypeElement key, boolean topNode) {
            this(getFilter().getEntry(key), topNode);
        }

        /**
         * Constructs a new node, for a given filter entry.
         * @param entry The label wrapped in this node
         * @param topNode flag indicating if this is a top type node in the tree
         */
        EntryNode(Entry entry, boolean topNode) {
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
            if (!(obj instanceof EntryNode)) {
                return false;
            }
            EntryNode other = (EntryNode) obj;
            return this.entry.equals(other.entry);
        }

        /** Returns the label of this tree node. */
        public final Entry getEntry() {
            return this.entry;
        }

        /** Indicates if this node is a top label type node in the tree. */
        public final boolean isTopNode() {
            return this.topNode;
        }

        /** Indicates if this tree node has a node filtering checkbox. */
        @Override
        public final boolean hasCheckbox() {
            return isFiltering() && isTopNode();
        }

        @Override
        public boolean isSelected() {
            return getFilter().isSelected(getEntry());
        }

        @Override
        public void setSelected(boolean selected) {
            getFilter().setSelected(getEntry(), selected);
        }

        @Override
        public final String toString() {
            return "Tree node for " + this.entry.toString();
        }

        private final Entry entry;
        private final boolean topNode;
    }

    /** Tree node wrapping a type graph. */
    public class TypeGraphTreeNode extends TreeNode {
        /**
         * Constructs a new node, for a given type graph.
         * @param name name of the type graph
         */
        TypeGraphTreeNode(String name) {
            this.name = name;
            TypeGraph typeGraph = getTypeGraphMap().get(name);
            for (TypeNode node : typeGraph.nodeSet()) {
                this.entries.add(getFilter().getEntry(node));
            }
            for (TypeEdge edge : typeGraph.edgeSet()) {
                this.entries.add(getFilter().getEntry(edge));
            }
        }

        @Override
        public int hashCode() {
            return this.name.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof TypeGraphTreeNode)) {
                return false;
            }
            TypeGraphTreeNode other = (TypeGraphTreeNode) obj;
            return this.name.equals(other.name);
        }

        /** Returns the name of this type graph. */
        public final String getName() {
            return this.name;
        }

        @Override
        public boolean hasCheckbox() {
            return true;
        }

        @Override
        public boolean isSelected() {
            return getFilter().isSelected(this.entries);
        }

        @Override
        public void setSelected(boolean selected) {
            getFilter().setSelected(this.entries, selected);
        }

        @Override
        public final String toString() {
            return String.format("Type graph '%s'", this.name);
        }

        private final String name;
        private final Set<Entry> entries = new HashSet<Entry>();
    }

    /** Class to deal with mouse events over the label list. */
    private class MyMouseListener extends MouseAdapter {
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
                    Object treeNode = path.getLastPathComponent();
                    if (treeNode instanceof EntryNode) {
                        Entry entry = ((EntryNode) treeNode).getEntry();
                        getFilter().changeSelected(entry);
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
        FilterAction(Object[] cells) {
            super(Options.FILTER_ACTION_NAME);
            this.filter = true;
            this.entries = new ArrayList<Entry>();
            for (Object cell : cells) {
                this.entries.addAll(getFilter().getEntries((GraphJCell) cell));
            }
        }

        FilterAction(TreePath[] cells, boolean filter) {
            super(filter ? Options.FILTER_ACTION_NAME
                    : Options.UNFILTER_ACTION_NAME);
            this.filter = filter;
            this.entries = new ArrayList<Entry>();
            for (TreePath path : cells) {
                Object treeNode = path.getLastPathComponent();
                if (treeNode instanceof EntryNode) {
                    this.entries.add(((EntryNode) treeNode).getEntry());
                }
            }
        }

        public void actionPerformed(ActionEvent e) {
            getFilter().setSelected(this.entries, !this.filter);
        }

        private final boolean filter;
        private final Collection<Entry> entries;
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
            super(null, Icons.E_A_CHOICE_ICON);
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

    /** Adds an icon, tool tip text and label colour. */
    private class MyTreeCellRenderer extends CellRenderer {
        public MyTreeCellRenderer() {
            super(LabelTree.this);
        }

        @Override
        public JComponent getTreeCellRendererComponent(JTree tree,
                Object value, boolean sel, boolean expanded, boolean leaf,
                int row, boolean hasFocus) {
            JComponent result =
                super.getTreeCellRendererComponent(tree, value, sel, expanded,
                    leaf, row, hasFocus);
            // set a sub- or supertype icon if the node label is a subnode
            Icon labelIcon = null;
            Entry entry = null;
            boolean topNode = false;
            if (getTreeNode() instanceof EntryNode) {
                entry = ((EntryNode) getTreeNode()).getEntry();
                topNode = ((EntryNode) getTreeNode()).isTopNode();
                if (!topNode) {
                    labelIcon = LabelTree.getModeIcon(isShowsSubtypes());
                }
                // set tool tip text
                StringBuilder toolTipText = new StringBuilder();
                Set<GraphJCell> occurrences = getFilter().getJCells(entry);
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
                        typeElement instanceof TypeNode
                                ? (TypeNode) typeElement
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