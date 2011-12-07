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
import groove.graph.TypeGraph;
import groove.graph.TypeLabel;
import groove.graph.TypeNode;
import groove.gui.action.ActionStore;
import groove.gui.jgraph.AspectJGraph;
import groove.gui.jgraph.GraphJCell;
import groove.gui.jgraph.GraphJGraph;
import groove.gui.jgraph.GraphJModel;
import groove.io.HTMLConverter;
import groove.util.ObservableSet;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
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
     */
    public LabelTree(GraphJGraph jGraph, boolean toolBar) {
        this.jGraph = jGraph;
        this.filteredLabels = jGraph.getFilteredLabels();
        this.filtering = this.filteredLabels != null;
        this.toolBar = toolBar;
        if (this.filtering) {
            this.filteredLabels.addObserver(new Observer() {
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

    /** Convenience method to return the label store of the jgraph. */
    TypeGraph getTypeGraph() {
        return getJGraph() instanceof AspectJGraph
                ? ((AspectJGraph) getJGraph()).getTypeGraph() : null;
    }

    /** Convenience method to return the labels map of the jgraph. */
    private Map<String,TypeGraph> getTypeGraphMap() {
        return getJGraph() instanceof AspectJGraph
                ? ((AspectJGraph) getJGraph()).getTypeGraphMap() : null;
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
        if (this.jModel != null) {
            this.jModel.removeGraphModelListener(this);
        }
        this.jModel = this.jGraph.getModel();
        this.labelCellMap.clear();
        if (this.jModel != null) {
            this.jModel.addGraphModelListener(this);
            for (int i = 0; i < this.jModel.getRootCount(); i++) {
                GraphJCell cell = (GraphJCell) this.jModel.getRootAt(i);
                if (isListable(cell)) {
                    addToLabels(cell);
                }
            }
        }
        updateTree();
        setDragEnabled(getTypeGraph() != null && !getTypeGraph().isFixed());
        setEnabled(this.jModel != null);
    }

    /**
     * Returns the set of jcells whose label sets contain a given label.
     * @param label the label looked for
     * @return the set of {@link GraphJCell}s for which {@link GraphJCell#getListLabels()}
     *         contains <tt>label</tt>
     */
    public Set<GraphJCell> getJCells(Object label) {
        return this.labelCellMap.get(label);
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
                    changed |= modifyLabels((GraphJCell) obj);
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
                    changed |= addToLabels((GraphJCell) element);
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
                    changed |= removeFromLabels((GraphJCell) element);
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
                Label label =
                    ((LabelTreeNode) selectedPath.getLastPathComponent()).getLabel();
                Set<GraphJCell> occurrences = this.labelCellMap.get(label);
                if (occurrences != null) {
                    emphSet.addAll(occurrences);
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
        // clear the selection first
        clearSelection();
        // clear the list
        getTopNode().removeAllChildren();
        Set<Label> labels = new TreeSet<Label>(getLabels());
        TypeGraph typeGraph = getTypeGraph();
        if (isShowsAllLabels() && typeGraph != null) {
            labels.addAll(typeGraph.getLabels());
        }
        Set<LabelTreeNode> newNodes = new HashSet<LabelTreeNode>();
        for (Label label : labels) {
            LabelTreeNode labelNode = new LabelTreeNode(label, true);
            getTopNode().add(labelNode);
            if (typeGraph != null && typeGraph.getLabels().contains(label)) {
                addRelatedTypes(labelNode,
                    isShowsSubtypes() ? typeGraph.getDirectSubtypeMap()
                            : typeGraph.getDirectSupertypeMap(), newNodes);
            }
        }
        getModel().reload(getTopNode());
        for (LabelTreeNode newNode : newNodes) {
            expandPath(new TreePath(newNode.getPath()));
        }
        addTreeSelectionListener(this);
    }

    /**
     * Recursively adds related types to a given label node.
     * Only first level subtypes are added.
     */
    private void addRelatedTypes(LabelTreeNode labelNode,
            Map<TypeNode,Set<TypeNode>> map, Set<LabelTreeNode> newNodes) {
        Label label = labelNode.getLabel();
        if (!label.isNodeType()) {
            return;
        }
        TypeNode type = getTypeGraph().getNode(label);
        if (type == null) {
            return;
        }
        Set<TypeNode> relatedTypes = map.get(type);
        assert relatedTypes != null : String.format(
            "Label '%s' does not occur in label store '%s'", label,
            map.keySet());
        for (TypeNode relType : relatedTypes) {
            LabelTreeNode typeNode = new LabelTreeNode(relType.label(), false);
            labelNode.add(typeNode);
            if (newNodes != null) {
                newNodes.add(labelNode);
            }
            // change last parameter to newNodes if subtypes should be added
            // to arbitrary depth
            addRelatedTypes(typeNode, map, null);
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
        if (isFiltering() && getTypeGraphMap() != null) {
            if (getTypeGraphMap().size() > 1) {
                result.add(new TypeFilterMenu(getTypeGraphMap(), true));
                result.add(new TypeFilterMenu(getTypeGraphMap(), false));
                result.addSeparator();
            }
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
     * Adds a cell to the label map. This means that for all labels of the cell,
     * the cell is inserted in that label's image. The return value indicates if
     * any labels were added
     */
    private boolean addToLabels(GraphJCell cell) {
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
    private boolean addToLabels(GraphJCell cell, Label label) {
        boolean result = false;
        Set<GraphJCell> currentCells = this.labelCellMap.get(label);
        if (currentCells == null) {
            currentCells = new HashSet<GraphJCell>();
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
    private boolean removeFromLabels(GraphJCell cell) {
        boolean result = false;
        Iterator<Map.Entry<Label,Set<GraphJCell>>> labelIter =
            this.labelCellMap.entrySet().iterator();
        while (labelIter.hasNext()) {
            Map.Entry<Label,Set<GraphJCell>> labelEntry = labelIter.next();
            Set<GraphJCell> cellSet = labelEntry.getValue();
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
    private boolean modifyLabels(GraphJCell cell) {
        boolean result = false;
        Set<Label> newLabelSet = new HashSet<Label>(cell.getListLabels());
        // go over the existing label map
        Iterator<Map.Entry<Label,Set<GraphJCell>>> labelIter =
            this.labelCellMap.entrySet().iterator();
        while (labelIter.hasNext()) {
            Map.Entry<Label,Set<GraphJCell>> labelEntry = labelIter.next();
            Label label = labelEntry.getKey();
            Set<GraphJCell> cellSet = labelEntry.getValue();
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
            Set<GraphJCell> newCells = new HashSet<GraphJCell>();
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
        if (label.equals(GraphJCell.NO_LABEL)) {
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
        if (isFiltered(label)) {
            HTMLConverter.STRIKETHROUGH_TAG.on(text);
        }
        return HTMLConverter.HTML_TAG.on(text).toString();
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
     * The {@link GraphJGraph}associated to this label list.
     */
    private final GraphJGraph jGraph;

    /**
     * The {@link GraphJModel}currently being viewed by this label list.
     */
    private GraphJModel<?,?> jModel;

    /**
     * The bag of labels in this jmodel.
     */
    final Map<Label,Set<GraphJCell>> labelCellMap =
        new TreeMap<Label,Set<GraphJCell>>();
    /** Flag indicating if a tool bar should be used. */
    private final boolean toolBar;
    /** Flag indicating if label filtering should be used. */
    private final boolean filtering;
    /** Set of filtered labels. */
    final ObservableSet<Label> filteredLabels;
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
    static Icon getModeIcon(boolean subtypes) {
        return subtypes ? Icons.ARROW_OPEN_UP_ICON : Icons.ARROW_OPEN_DOWN_ICON;
    }

    /** Colour HTML tag for the foreground colour of special labels. */
    private static final Color SPECIAL_COLOR = Color.LIGHT_GRAY;

    /** Tree node wrapping a label. */
    public class LabelTreeNode extends TreeNode {
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
        @Override
        public final boolean hasCheckbox() {
            return isFiltering() && isTopNode();
        }

        @Override
        public boolean isSelected() {
            return !isFiltered(getLabel());
        }

        @Override
        public void setSelected(boolean selected) {
            if (selected) {
                LabelTree.this.filteredLabels.remove(getLabel());
            } else {
                LabelTree.this.filteredLabels.add(getLabel());
            }
        }

        @Override
        public final String toString() {
            return "Tree node for " + this.label.text();
        }

        private final Label label;
        private final boolean topNode;
    }

    /** Tree node wrapping a type graph. */
    public class TypeGraphTreeNode extends TreeNode {
        /**
         * Constructs a new node, for a given type graph.
         * @param name name of the type graph
         * @param labels labels declared in the type graph 
         */
        TypeGraphTreeNode(String name, Set<TypeLabel> labels) {
            this.name = name;
            this.labels = labels;
        }

        /** Returns the name of this type graph. */
        public final String getName() {
            return this.name;
        }

        /** Returns the set of labels defined in this type graph. */
        public final Set<TypeLabel> getLabels() {
            return this.labels;
        }

        /** Indicates if the type graph is currently showing. */
        public final boolean isShowing() {
            return this.showing;
        }

        /** Changes the showing status of this node. */
        public final void setShowing(boolean showing) {
            this.showing = showing;
        }

        @Override
        public boolean hasCheckbox() {
            return true;
        }

        @Override
        public boolean isSelected() {
            return isShowing();
        }

        @Override
        public void setSelected(boolean selected) {
            // currently does nothins
        }

        @Override
        public final String toString() {
            return "Type graph node for " + this.name;
        }

        private final String name;
        private final Set<TypeLabel> labels;
        private boolean showing;
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

    /** Menu offering a selection of type graphs to be filtered. */
    private class TypeFilterMenu extends JMenu {
        TypeFilterMenu(Map<String,TypeGraph> labelsMap, boolean filter) {
            super(filter ? Options.FILTER_TYPE_ACTION_NAME
                    : Options.UNFILTER_TYPE_ACTION_NAME);
            this.filter = filter;
            for (Map.Entry<String,TypeGraph> labelsEntry : labelsMap.entrySet()) {
                add(new TypeFilterMenuItem(labelsEntry.getKey(),
                    labelsEntry.getValue()));
            }
        }

        private final boolean filter;

        private class TypeFilterMenuItem extends AbstractAction {
            TypeFilterMenuItem(String name, TypeGraph typeGraph) {
                super(name);
                this.labels = new HashSet<Label>();
                this.labels.addAll(typeGraph.getLabels());
            }

            public void actionPerformed(ActionEvent e) {
                if (TypeFilterMenu.this.filter) {
                    LabelTree.this.filteredLabels.addAll(this.labels);
                } else {
                    LabelTree.this.filteredLabels.removeAll(this.labels);
                }
            }

            private final Collection<Label> labels;
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
            LabelTreeNode labelTreeNode = null;
            if (getTreeNode() instanceof LabelTreeNode) {
                labelTreeNode = (LabelTreeNode) getTreeNode();
            }
            if (labelTreeNode != null && !labelTreeNode.isTopNode()) {
                labelIcon = LabelTree.getModeIcon(isShowsSubtypes());
            }
            getInner().setIcon(labelIcon);
            // set tool tip text
            if (labelTreeNode != null) {
                Label label = labelTreeNode.getLabel();
                StringBuilder toolTipText = new StringBuilder();
                Set<GraphJCell> occurrences =
                    LabelTree.this.labelCellMap.get(label);
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
                    if (isFiltered(label)) {
                        toolTipText.append("Filtered label; doubleclick to show");
                    } else {
                        toolTipText.append("Visible label; doubleclick to filter");
                    }
                }
                if (toolTipText.length() != 0) {
                    result.setToolTipText(HTMLConverter.HTML_TAG.on(toolTipText).toString());
                }
                // set node colour
                if (label instanceof TypeLabel && label.isNodeType()) {
                    TypeGraph typeGraph = getTypeGraph();
                    TypeNode typeNode =
                        typeGraph != null ? typeGraph.getNode(label) : null;
                    if (typeNode != null && typeNode.getColor() != null) {
                        getInner().setForeground(typeNode.getColor());
                    }
                }
            }
            return result;
        }
    }
}