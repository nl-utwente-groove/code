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

import static nl.utwente.groove.io.HTMLConverter.HTML_TAG;
import static nl.utwente.groove.io.HTMLConverter.ITALIC_TAG;
import static nl.utwente.groove.io.HTMLConverter.STRONG_TAG;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.tree.DefaultMutableTreeNode;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.jgraph.event.GraphModelEvent;

import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.type.TypeEdge;
import nl.utwente.groove.grammar.type.TypeElement;
import nl.utwente.groove.grammar.type.TypeGraph;
import nl.utwente.groove.grammar.type.TypeNode;
import nl.utwente.groove.graph.EdgeRole;
import nl.utwente.groove.graph.Label;
import nl.utwente.groove.gui.Icons;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.action.CollapseAllAction;
import nl.utwente.groove.gui.jgraph.AspectJGraph;
import nl.utwente.groove.gui.jgraph.AspectJModel;
import nl.utwente.groove.gui.jgraph.JModel;
import nl.utwente.groove.gui.tree.LabelFilter.Entry;
import nl.utwente.groove.gui.tree.TypeFilter.TypeEntry;
import nl.utwente.groove.io.HTMLConverter;
import nl.utwente.groove.util.Factory;

/**
 * Scroll pane showing the list of labels currently appearing in the graph
 * model.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
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

    private PropertyChangeListener getJModelChangeListener() {
        return this.jModelChangeListener.get();
    }

    private Factory<PropertyChangeListener> jModelChangeListener
        = Factory.lazy(() -> evt -> updateModel());

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

    @Override
    protected @NonNull StringBuilder getText(Entry entry) {
        // colour the label, as dictated by the (optional) node colour of this entry or its source node
        var result = super.getText(entry);
        var typeEntry = (TypeEntry) entry;
        TypeElement typeElement = typeEntry.getType();
        TypeNode typeNode = typeElement instanceof TypeNode
            ? (TypeNode) typeElement
            : ((TypeEdge) typeElement).source();
        Color color = typeNode.getColor();
        if (color != null) {
            HTMLConverter.createColorTag(color).on(result);
        }
        // if this is an edge, add the target type
        if (typeEntry.getType() instanceof TypeEdge e && e.getRole() == EdgeRole.BINARY) {
            var target = e.target();
            result.append(": ");
            StringBuilder targetString = new StringBuilder();
            targetString.append(target.label().toLine().toHTMLString());
            color = target.getColor();
            result.append(targetString);
            if (color != null) {
                HTMLConverter.createColorTag(color).on(targetString);
            }
        }
        return result;
    }

    /**
     * Returns the button for the show-subtypes action, lazily creating it
     * first.
     */
    private JToggleButton getShowSubtypesButton() {
        var result = this.showSubtypesButton;
        if (result == null) {
            this.showSubtypesButton = result = Options.createToggleButton(new ShowModeAction(true));
            result.setSelected(true);
        }
        return result;
    }

    /** Button for setting the show subtypes mode. */
    private @Nullable JToggleButton showSubtypesButton;

    /**
     * Returns the button for the show-supertypes action, lazily creating it
     * first.
     */
    private JToggleButton getShowSupertypesButton() {
        var result = this.showSupertypesButton;
        if (result == null) {
            this.showSupertypesButton
                = result = Options.createToggleButton(new ShowModeAction(false));
        }
        return result;
    }

    /** Button for setting the show supertypes mode. */
    private @Nullable JToggleButton showSupertypesButton;

    /**
     * Returns the button for the show-supertypes action, lazily creating it
     * first.
     */
    private JToggleButton getShowAllLabelsButton() {
        var result = this.showAllLabelsButton;
        if (result == null) {
            this.showAllLabelsButton
                = result = Options.createToggleButton(new ShowAllLabelsAction());
        }
        return result;
    }

    /** Button for setting the show all actions mode. */
    private @Nullable JToggleButton showAllLabelsButton;

    /**
     * Returns the button for the collapse all action, lazily creating it
     * first.
     */
    private JButton getCollapseAllButton() {
        var result = this.collapseAllButton;
        if (result == null) {
            this.collapseAllButton
                = result = Options.createButton(new CollapseAllAction(null, this));
        }
        return result;
    }

    /** Button for collapsing the label tree. */
    private @Nullable JButton collapseAllButton;

    /**
     * Convenience method to return the type graph in the jModel.
     */
    private @Nullable TypeGraph getTypeGraph() {
        var jModel = (AspectJModel) getJModel();
        return jModel == null
            ? null
            : jModel.getTypeGraph();
    }

    @Override
    void updateFilter() {
        getFilter().update(getTypeGraph());
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
    public void graphChanged(@Nullable GraphModelEvent e) {
        if (isModelStale() || getNonNullJModel().isLoading()) {
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
                result = fillTree(getTopNode(), typeGraph.nodeSet(), typeGraph.edgeSet());
            } else if (typeGraphMap.size() == 1) {
                TypeGraph.Sub subTypeGraph = typeGraphMap.iterator().next();
                result = fillTree(getTopNode(), subTypeGraph.getNodes(), subTypeGraph.getEdges());
            } else {
                result = new ArrayList<>();
                for (TypeGraph.Sub subTypeGraph : typeGraphMap) {
                    TypeGraphTreeNode typeGraphNode = new TypeGraphTreeNode(this, subTypeGraph);
                    result
                        .addAll(fillTree(typeGraphNode, subTypeGraph.getNodes(),
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
                                    Set<? extends TypeNode> typeNodes,
                                    Set<? extends TypeEdge> typeEdges) {
        List<TreeNode> result = new ArrayList<>();
        // mapping from type nodes to dependent type nodes (in the combined type graph)
        var typeGraph = getTypeGraph();
        assert typeGraph != null;
        Map<TypeNode,Set<TypeNode>> tmpDepNodeMap = isShowsSubtypes()
            ? typeGraph.getDirectSubtypeMap()
            : typeGraph.getDirectSupertypeMap();
        // reduce the type nodes to those that should actually be included in the tree
        // Include a type node if it or a subtype occurs in typeNodes
        var subTypeNodes = new HashSet<>();
        typeNodes.stream().map(TypeNode::getSubtypes).forEach(subTypeNodes::addAll);
        Map<TypeNode,SortedSet<TypeNode>> depNodeMap = new TreeMap<>();
        for (var e : tmpDepNodeMap.entrySet()) {
            var node = e.getKey();
            if (node.isSort() || !typeNodes.contains(node)) {
                continue;
            }
            if (isShowsAllLabels()
                || node.getSubtypes().stream().anyMatch(getFilter()::hasJCells)) {
                depNodeMap.put(node, new TreeSet<>(e.getValue()));
            }
        }
        depNodeMap.values().stream().forEach(s -> s.retainAll(depNodeMap.keySet()));
        for (var e : depNodeMap.entrySet()) {
            var node = e.getKey();
            TypeEntry entry = getFilter().getEntry(node);
            TypeTreeNode nodeTypeNode = new TypeTreeNode(this, entry, true);
            topNode.add(nodeTypeNode);
            result.add(nodeTypeNode);
            for (var depNode : e.getValue()) {
                TypeTreeNode subTypeNode = new TypeTreeNode(this, depNode, false);
                nodeTypeNode.add(subTypeNode);
                result.add(subTypeNode);
            }
            if (node.isTopType()) {
                // don't show the edges as dependent on the type
                continue;
            }
            // check duplicates due to equi-labelled edges to different targets
            Set<Entry> entries = new HashSet<>();
            for (var edge : new TreeSet<>(typeGraph.outEdgeSet(node))) {
                if (typeEdges.contains(edge)) {
                    TypeEntry edgeEntry = getFilter().getEntry(edge);
                    if (entries.add(edgeEntry)) {
                        TypeTreeNode edgeTypeNode = new TypeTreeNode(this, edgeEntry, true);
                        nodeTypeNode.add(edgeTypeNode);
                        result.add(edgeTypeNode);
                    }
                }
            }
        }
        if (typeGraph.isImplicit()) {
            // add edge entries
            // check duplicates due to equi-labelled edges
            Set<Entry> entries = new HashSet<>();
            for (TypeEdge edge : typeEdges) {
                TypeEntry edgeEntry = getFilter().getEntry(edge);
                if (isShowsAllLabels() || getFilter().hasJCells(edgeEntry)) {
                    if (entries.add(edgeEntry)) {
                        TypeTreeNode edgeTypeNode = new TypeTreeNode(this, edgeEntry, true);
                        topNode.add(edgeTypeNode);
                        result.add(edgeTypeNode);
                    }
                }
            }
        }
        return result;
    }

    /**
     * If the object to be displayed is a {@link Label}, this implementation
     * returns an HTML-formatted string with the text of the label.
     */
    @Override
    public String convertValueToText(@Nullable Object value, boolean selected, boolean expanded,
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
     * Changes the value of the show-subtype flag.
     */
    private void setShowsSubtypes(boolean show) {
        this.showsSubtypes = show;
    }

    /**
     * Indicates if this tree is currently showing subtype relations.
     */
    private boolean isShowsSubtypes() {
        return this.showsSubtypes;
    }

    /** Mode of the label tree: showing subtypes or supertypes. */
    private boolean showsSubtypes = true;

    /**
     * Changes the value of the show-all-labels flag.
     */
    private void setShowsAllLabels(boolean show) {
        this.showsAllLabels = show;
    }

    /**
     * Indicates if this tree is currently showing all labels, or just those
     * existing in the graph.
     */
    private boolean isShowsAllLabels() {
        return this.showsAllLabels;
    }

    /** Mode of the label tree: showing all labels or just those in the graph. */
    private boolean showsAllLabels = false;

    /**
     * Returns the icon for subtype or supertype mode, depending on the
     * parameter.
     */
    static Icon getModeIcon(boolean subtypes) {
        return subtypes
            ? Icons.ARROW_OPEN_UP_ICON
            : Icons.ARROW_OPEN_DOWN_ICON;
    }

    /** Tree node wrapping a filter entry. */
    public class TypeTreeNode extends LabelTreeNode {
        /**
         * Constructs a new node, for a given type element.
         * @param key the key element wrapped in this node
         * @param topNode flag indicating if this is a top type node in the tree
         */
        TypeTreeNode(TypeTree tree, TypeElement key, boolean topNode) {
            this(tree, getFilter().getEntry(key), topNode);
        }

        /**
         * Constructs a new node, for a given filter entry.
         * @param entry The label wrapped in this node
         * @param topNode flag indicating if this is a top type node in the tree
         */
        TypeTreeNode(TypeTree tree, TypeEntry entry, boolean topNode) {
            super(tree, entry, topNode);
        }

        @Override
        public TypeEntry getEntry() {
            return (TypeEntry) super.getEntry();
        }

        @Override
        public boolean isPassive() {
            return getEntry().isPassive();
        }

        @Override
        public @Nullable Icon getIcon() {
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
        TypeGraphTreeNode(TypeTree tree, TypeGraph.Sub subTypeGraph) {
            this.tree = tree;
            this.name = subTypeGraph.getName();
            for (TypeNode node : subTypeGraph.getNodes()) {
                this.entries.add(getFilter().getEntry(node));
            }
            this.selected = true;
        }

        /** The type tree this is part of. */
        private final TypeTree tree;

        /** The set of node type entries in under this type graph node. */
        private final Set<Entry> entries = new HashSet<>();

        @Override
        public int hashCode() {
            return this.name.hashCode();
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof TypeGraphTreeNode other)) {
                return false;
            }
            return this.name.equals(other.name);
        }

        /** Returns the name of this type graph. */
        public final String getName() {
            return this.name;
        }

        private final String name;

        @Override
        public boolean hasCheckbox() {
            return this.tree.isFiltering();
        }

        @Override
        public boolean isSelected() {
            return this.selected;
        }

        @Override
        public void setSelected(boolean selected) {
            this.selected = selected;
            getFilter().setSelected(this.entries, selected);
        }

        private boolean selected;

        @Override
        public boolean isPassive() {
            boolean selected = this.selected;
            return this.entries.stream().anyMatch(e -> selected != e.isSelected());
        }

        @Override
        public final String toString() {
            return String.format("Type graph '%s'", this.name);
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
        public void actionPerformed(@Nullable ActionEvent e) {
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
            return this.subtypes
                ? Options.SHOW_SUBTYPES_ACTION_NAME
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
        public void actionPerformed(@Nullable ActionEvent e) {
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