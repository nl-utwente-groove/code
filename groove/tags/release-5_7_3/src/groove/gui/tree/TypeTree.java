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
import static groove.io.HTMLConverter.ITALIC_TAG;
import static groove.io.HTMLConverter.STRONG_TAG;
import groove.grammar.aspect.AspectGraph;
import groove.grammar.type.TypeEdge;
import groove.grammar.type.TypeElement;
import groove.grammar.type.TypeGraph;
import groove.grammar.type.TypeNode;
import groove.graph.Label;
import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.action.CollapseAllAction;
import groove.gui.jgraph.AspectJGraph;
import groove.gui.jgraph.AspectJModel;
import groove.gui.jgraph.JModel;
import groove.gui.tree.LabelFilter.Entry;
import groove.gui.tree.TypeFilter.TypeEntry;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jgraph.event.GraphModelEvent;

/**
 * Scroll pane showing the list of labels currently appearing in the graph
 * model.
 * @author Arend Rensink
 * @version $Revision$
 */
public class TypeTree extends LabelTree<AspectGraph> {
    /**
     * Constructs a label list associated with a given jgraph. A further
     * parameter indicates if the label tree should support subtypes.
     * @param jGraph the jgraph with which this list is to be associated
     * @param filtering if {@code true}, the panel has checkboxes to filter labels
     */
    public TypeTree(AspectJGraph jGraph, boolean filtering) {
        super(jGraph, filtering);
    }

    @Override
    void installJModelListeners(JModel<AspectGraph> jModel) {
        super.installJModelListeners(jModel);
        ((AspectJModel) jModel).addGraphChangeListener(getJModelChangeListener());
    }

    @Override
    void removeJModelListeners(JModel<AspectGraph> jModel) {
        super.removeJModelListeners(jModel);
        ((AspectJModel) jModel).removeGraphChangeListener(getJModelChangeListener());
    }

    private Observer getJModelChangeListener() {
        if (this.jModelChangeListener == null) {
            this.jModelChangeListener = new Observer() {
                @Override
                public void update(Observable o, Object arg) {
                    updateModel();
                }
            };
        }
        return this.jModelChangeListener;
    }

    private Observer jModelChangeListener;

    /** Creates a tool bar for the label tree. */
    public JToolBar createToolBar() {
        JToolBar result = Options.createToolBar();
        result.add(getShowSubtypesButton());
        result.add(getShowSupertypesButton());
        result.addSeparator();
        result.add(getShowAllLabelsButton());
        result.add(getCollapseAllButton());
        // put the sub- and supertype buttons in a button group
        ButtonGroup modeButtonGroup = new ButtonGroup();
        modeButtonGroup.add(getShowSubtypesButton());
        modeButtonGroup.add(getShowSupertypesButton());
        return result;
    }

    @Override
    TypeFilter createLabelFilter() {
        return new TypeFilter();
    }

    @Override
    TypeFilter getFilter() {
        return (TypeFilter) super.getFilter();
    }

    /**
     * Returns the button for the show-subtypes action, lazily creating it
     * first.
     */
    private JToggleButton getShowSubtypesButton() {
        if (this.showSubtypesButton == null) {
            this.showSubtypesButton = Options.createToggleButton(new ShowModeAction(true));
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
            this.showSupertypesButton = Options.createToggleButton(new ShowModeAction(false));
        }
        return this.showSupertypesButton;
    }

    /**
     * Returns the button for the show-supertypes action, lazily creating it
     * first.
     */
    private JToggleButton getShowAllLabelsButton() {
        if (this.showAllLabelsButton == null) {
            this.showAllLabelsButton = Options.createToggleButton(new ShowAllLabelsAction());
        }
        return this.showAllLabelsButton;
    }

    /**
     * Returns the button for the collapse all action, lazily creating it
     * first.
     */
    private JButton getCollapseAllButton() {
        if (this.collapseAllButton == null) {
            Action action = new CollapseAllAction(null, this);
            this.collapseAllButton = Options.createButton(action);
        }
        return this.collapseAllButton;
    }

    /** 
     * Convenience method to return the type graph of the grammar,
     * or the graph in the jModel if that is a type graph. 
     */
    private TypeGraph getTypeGraph() {
        TypeGraph result = null;
        if (getJGraph().getModel() != null) {
            result = ((AspectJGraph) getJGraph()).getModel().getTypeGraph();
        }
        return result;
    }

    @Override
    boolean isModelStale() {
        return super.isModelStale() || this.typeGraph != getTypeGraph();
    }

    @Override
    void clearFilter() {
        if (this.typeGraph == getTypeGraph()) {
            getFilter().clearJCells();
        } else {
            this.typeGraph = getTypeGraph();
            getFilter().clear();
        }
    }

    @Override
    void updateFilter() {
        for (TypeNode node : getTypeGraph().nodeSet()) {
            getFilter().addEntry(node);
        }
        for (TypeEdge edge : getTypeGraph().edgeSet()) {
            getFilter().addEntry(edge);
        }
        super.updateFilter();
    }

    /**
     * Enables the buttons in addition to delegating the method to <tt>super</tt>.
     */
    @Override
    public void setEnabled(boolean enabled) {
        getShowAllLabelsButton().setEnabled(enabled);
        getShowSubtypesButton().setEnabled(enabled);
        getShowSupertypesButton().setEnabled(enabled);
        getCollapseAllButton().setEnabled(enabled);
        super.setEnabled(enabled);
    }

    /**
     * Updates the label list according to the change event.
     */
    @Override
    public void graphChanged(GraphModelEvent e) {
        if (isModelStale()) {
            updateModel();
        } else {
            super.graphChanged(e);
        }
    }

    /** 
     * Updates the tree from the information in the type graph.
     * @return the set of tree nodes created for the types
     */
    @Override
    List<TreeNode> fillTree() {
        List<TreeNode> result = new ArrayList<>();
        TypeGraph typeGraph = getTypeGraph();
        if (typeGraph != null) {
            Collection<TypeGraph.Sub> typeGraphMap = typeGraph.getComponentMap().values();
            if (typeGraphMap.isEmpty()) {
                result = fillTree(getTopNode(), getTypeGraph().nodeSet(), getTypeGraph().edgeSet());
            } else if (typeGraphMap.size() == 1) {
                TypeGraph.Sub subTypeGraph = typeGraphMap.iterator().next();
                result = fillTree(getTopNode(), subTypeGraph.getNodes(), subTypeGraph.getEdges());
            } else {
                result = new ArrayList<>();
                for (TypeGraph.Sub subTypeGraph : typeGraphMap) {
                    TypeGraphTreeNode typeGraphNode = new TypeGraphTreeNode(subTypeGraph);
                    result.addAll(fillTree(typeGraphNode, subTypeGraph.getNodes(),
                        subTypeGraph.getEdges()));
                    // only add if there were any children
                    if (typeGraphNode.getChildCount() > 0) {
                        getTopNode().add(typeGraphNode);
                        result.add(typeGraphNode);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Updates part of the tree from a set of type nodes and edges. 
     * @param topNode the node to which the type graph information should be appended
     * @param typeNodes the set of type nodes for which tree nodes should be created
     * @param typeEdges the set of type edges for which tree nodes should be created
     * @return the set of tree nodes created for the types
     */
    private List<TreeNode> fillTree(DefaultMutableTreeNode topNode,
            Set<? extends TypeNode> typeNodes, Set<? extends TypeEdge> typeEdges) {
        List<TreeNode> result = new ArrayList<>();
        // mapping from type nodes to related types (in the combined type graph)
        Map<TypeNode,Set<TypeNode>> relatedMap =
            isShowsSubtypes() ? getTypeGraph().getDirectSubtypeMap()
                    : getTypeGraph().getDirectSupertypeMap();
        for (TypeNode node : new TreeSet<TypeNode>(typeNodes)) {
            if (node.isDataType()) {
                continue;
            }
            TypeEntry entry = getFilter().getEntry(node);
            if (isShowsAllLabels() || getFilter().hasJCells(entry)) {
                TypedEntryNode nodeTypeNode = new TypedEntryNode(this, entry, true);
                topNode.add(nodeTypeNode);
                result.add(nodeTypeNode);
                addRelatedTypes(typeNodes, nodeTypeNode, relatedMap, result);
                if (node.isTopType()) {
                    // don't show the edges as dependent on the type
                    continue;
                }
                // check duplicates due to equi-labelled edges to different targets
                Set<Entry> entries = new HashSet<>();
                for (TypeEdge edge : new TreeSet<TypeEdge>(getTypeGraph().outEdgeSet(node))) {
                    if (typeEdges.contains(edge)) {
                        TypeEntry edgeEntry = getFilter().getEntry(edge);
                        if (entries.add(edgeEntry)) {
                            TypedEntryNode edgeTypeNode = new TypedEntryNode(this, edgeEntry, true);
                            nodeTypeNode.add(edgeTypeNode);
                            result.add(edgeTypeNode);
                        }
                    }
                }
            }
        }
        if (getTypeGraph().isImplicit()) {
            // add edge entries
            // check duplicates due to equi-labelled edges
            Set<Entry> entries = new HashSet<>();
            for (TypeEdge edge : typeEdges) {
                TypeEntry edgeEntry = getFilter().getEntry(edge);
                if (isShowsAllLabels() || getFilter().hasJCells(edgeEntry)) {
                    if (entries.add(edgeEntry)) {
                        TypedEntryNode edgeTypeNode = new TypedEntryNode(this, edgeEntry, true);
                        topNode.add(edgeTypeNode);
                        result.add(edgeTypeNode);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Recursively adds related types to a given label node.
     * Only first level subtypes are added.
     * @param typeNodes set of type nodes from which the related types are taken
     * @param typeNode tree node for the key type
     * @param map mapping from key types to related node type (in the combined type graph)
     * @param newNodes set that collects all newly created tree nodes  
     */
    private void addRelatedTypes(Set<? extends TypeNode> typeNodes, TypedEntryNode typeNode,
            Map<TypeNode,Set<TypeNode>> map, List<TreeNode> newNodes) {
        TypeNode type = (TypeNode) typeNode.getEntry().getType();
        Set<TypeNode> relatedTypes = map.get(type);
        assert relatedTypes != null : String.format(
            "Node type '%s' does not occur in type graph '%s'", type, map.keySet());
        for (TypeNode relType : relatedTypes) {
            // test if the node type label exists in the partial type graph
            if (typeNodes.contains(relType)) {
                TypedEntryNode subTypeNode = new TypedEntryNode(this, relType, false);
                typeNode.add(subTypeNode);
                if (newNodes != null) {
                    newNodes.add(typeNode);
                }
                // change last parameter to newNodes if subtypes should be added
                // to arbitrary depth
                addRelatedTypes(typeNodes, subTypeNode, map, null);
            }
        }
    }

    /**
     * If the object to be displayed is a {@link Label}, this implementation
     * returns an HTML-formatted string with the text of the label.
     */
    @Override
    public String convertValueToText(Object value, boolean selected, boolean expanded,
            boolean leaf, int row, boolean hasFocus) {
        if (value instanceof TypeGraphTreeNode) {
            StringBuilder result = new StringBuilder();
            result.append("Type graph '");
            result.append(((TypeGraphTreeNode) value).getName());
            result.append("'");
            return HTML_TAG.on(ITALIC_TAG.on(STRONG_TAG.on(result)).toString());
        } else {
            return super.convertValueToText(value, selected, expanded, leaf, row, hasFocus);
        }
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

    /** The type graph in the model, if any. */
    private TypeGraph typeGraph;
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
    /** Button for collapsing the label tree. */
    private JButton collapseAllButton;

    /**
     * Returns the icon for subtype or supertype mode, depending on the
     * parameter.
     */
    static Icon getModeIcon(boolean subtypes) {
        return subtypes ? Icons.ARROW_OPEN_UP_ICON : Icons.ARROW_OPEN_DOWN_ICON;
    }

    /** Tree node wrapping a filter entry. */
    public class TypedEntryNode extends EntryNode {
        /**
         * Constructs a new node, for a given type element.
         * @param key the key element wrapped in this node
         * @param topNode flag indicating if this is a top type node in the tree
         */
        TypedEntryNode(TypeTree tree, TypeElement key, boolean topNode) {
            this(tree, getFilter().getEntry(key), topNode);
        }

        /**
         * Constructs a new node, for a given filter entry.
         * @param entry The label wrapped in this node
         * @param topNode flag indicating if this is a top type node in the tree
         */
        TypedEntryNode(TypeTree tree, TypeEntry entry, boolean topNode) {
            super(tree, entry, topNode);
        }

        @Override
        public TypeEntry getEntry() {
            return (TypeEntry) super.getEntry();
        }

        @Override
        public Icon getIcon() {
            if (!isTopNode()) {
                return TypeTree.getModeIcon(isShowsSubtypes());
            } else {
                return super.getIcon();
            }
        }
    }

    /** Tree node wrapping a type graph. */
    public class TypeGraphTreeNode extends TreeNode {
        /**
         * Constructs a new node, for a given type graph component
         * @param subTypeGraph the type graph component
         */
        TypeGraphTreeNode(TypeGraph.Sub subTypeGraph) {
            this.name = subTypeGraph.getName();
            for (TypeNode node : subTypeGraph.getNodes()) {
                this.entries.add(getFilter().getEntry(node));
            }
            for (TypeEdge edge : subTypeGraph.getEdges()) {
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
        private final Set<Entry> entries = new HashSet<>();
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
            return isShowsAllLabels() ? Options.SHOW_EXISTING_LABELS_ACTION_NAME
                    : Options.SHOW_ALL_LABELS_ACTION_NAME;
        }
    }
}