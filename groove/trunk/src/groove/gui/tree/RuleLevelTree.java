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

import groove.grammar.aspect.AspectEdge;
import groove.grammar.aspect.AspectElement;
import groove.grammar.aspect.AspectNode;
import groove.grammar.model.RuleModel;
import groove.grammar.model.RuleModel.Index;
import groove.gui.jgraph.AspectJCell;
import groove.gui.jgraph.AspectJGraph;
import groove.gui.jgraph.AspectJModel;
import groove.gui.look.VisualKey;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;

/**
 * Checkbox tree controlling the visibility of rule levels.
 * @author Arend Rensink
 * @version $Revision $
 */
public class RuleLevelTree extends CheckboxTree implements TreeSelectionListener {
    /** Creates a new tree, for a given rule model. */
    public RuleLevelTree(AspectJGraph jGraph) {
        this.jGraph = jGraph;
        setLargeModel(true);
        setEnabled(jGraph.isEnabled());
        setShowsRootHandles(false);
        getUI().setCollapsedIcon(null);
        getUI().setExpandedIcon(null);
        addMouseListener(new MyMouseListener());
        // deselect the level tree whenever the graph
        // selection changes
        jGraph.addGraphSelectionListener(new GraphSelectionListener() {
            @Override
            public void valueChanged(GraphSelectionEvent e) {
                clearSelection();
            }
        });
    }

    /**
     * Replaces the jmodel on which this level tree is based with the
     * (supposedly new) model in the associated jgraph. Gets the rule
     * model from the jmodel.
     */
    private void synchroniseJModel() {
        AspectJModel jModel = getJGraph().getModel();
        if (jModel != this.jModel) {
            this.jModel = jModel;
            if (jModel == null) {
                this.rule = null;
            } else {
                this.rule = (RuleModel) jModel.getResourceModel();
            }
            boolean enabled = updateTree();
            for (Set<AspectJCell> levelCells : this.levelCellMap.values()) {
                this.allCellSet.addAll(levelCells);
            }
            updateVisibleCells(this.levelNodeMap.values());
            setEnabled(enabled);
        }
    }

    /** Indicates if a given aspect cell is in the set of visible cells. */
    public boolean isVisible(AspectJCell jCell) {
        synchroniseJModel();
        return !this.allCellSet.contains(jCell) || this.selectedSet.contains(jCell);
    }

    /**
     * Emphasises/deemphasises cells in the associated jmodel, based on the list
     * selection.
     */
    @Override
    public void valueChanged(TreeSelectionEvent e) {
        synchroniseJModel();
        Set<AspectJCell> emphSet = new HashSet<>();
        TreePath[] selectionPaths = getSelectionPaths();
        if (selectionPaths != null) {
            for (TreePath selectedPath : selectionPaths) {
                Index index = ((LevelNode) selectedPath.getLastPathComponent()).getIndex();
                emphSet.addAll(this.levelCellMap.get(index));
            }
        }
        emphSet.retainAll(this.selectedSet);
        getJGraph().setSelectionCells(emphSet.toArray());
    }

    /**
     * Updates the tree from the set of rule levels.
     * @return {@code true} if the rule tree should be enabled
     */
    private boolean updateTree() {
        // temporarily remove this component as selection listener
        removeTreeSelectionListener(this);
        // clear the selection first
        clearSelection();
        // clear the list
        getTopNode().removeAllChildren();
        Set<LevelNode> newNodes = new HashSet<>();
        Map<Index,Set<AspectElement>> levelTree =
            this.rule == null ? null : this.rule.getLevelTree();
        boolean enabled = levelTree != null && levelTree.size() > 1;
        if (enabled) {
            assert levelTree != null; // guaranteed by enabled
            for (Map.Entry<Index,Set<AspectElement>> levelEntry : levelTree.entrySet()) {
                Index index = levelEntry.getKey();
                if (!index.isTopLevel() && index.getLevelNode() == null) {
                    continue;
                }
                LevelNode levelNode = new LevelNode(index);
                if (index.isTopLevel()) {
                    getTopNode().add(levelNode);
                } else {
                    LevelNode parentNode = this.levelNodeMap.get(index.getParent());
                    parentNode.add(levelNode);
                }
                this.levelNodeMap.put(index, levelNode);
                AspectJModel jModel = getJGraph().getModel();
                Set<AspectJCell> levelCells = new HashSet<>();
                // add all cells for this level according to the rule level tree
                for (AspectElement elem : levelEntry.getValue()) {
                    AspectJCell jCell = jModel.getJCell(elem);
                    if (jCell != null) {
                        levelCells.add(jCell);
                    }
                }
                // now subtract the cells of the parent
                // note that we go through the indices in an ordered fashion
                // so the parent has already been computed
                if (!index.isTopLevel()) {
                    levelCells.removeAll(this.levelCellMap.get(index.getParent()));
                }
                // also add the nesting nodes and edges
                AspectNode ruleLevelNode = index.getLevelNode();
                if (ruleLevelNode != null) {
                    AspectJCell jCell = jModel.getJCell(ruleLevelNode);
                    if (jCell != null) {
                        levelCells.add(jCell);
                    }
                    for (AspectEdge edge : this.rule.getSource()
                        .edgeSet(ruleLevelNode)) {
                        jCell = jModel.getJCell(edge);
                        if (jCell != null) {
                            levelCells.add(jCell);
                        }
                    }
                }
                this.levelCellMap.put(index, levelCells);
                newNodes.add(levelNode);
            }
        }
        getModel().reload(getTopNode());
        for (LevelNode newNode : newNodes) {
            expandPath(new TreePath(newNode.getPath()));
        }
        addTreeSelectionListener(this);
        return enabled;
    }

    /** Updates the {@link #selectedSet} based on the currently selected
     * level nodes.
     * @return the set of changed cells
     */
    private Set<AspectJCell> updateVisibleCells(Collection<LevelNode> changedNodes) {
        Set<AspectJCell> selecteds = new HashSet<>();
        Set<AspectJCell> unselecteds = new HashSet<>();
        for (LevelNode node : changedNodes) {
            Set<AspectJCell> levelCells = this.levelCellMap.get(node.getIndex());
            if (node.isSelected()) {
                selecteds.addAll(levelCells);
            } else {
                unselecteds.addAll(levelCells);
            }
        }
        this.selectedSet.removeAll(unselecteds);
        this.selectedSet.addAll(selecteds);
        // Collect the changed cells
        Set<AspectJCell> result = new HashSet<>(selecteds.size() + unselecteds.size());
        result.addAll(selecteds);
        result.addAll(unselecteds);
        // now refresh the changed cells
        for (AspectJCell jCell : result) {
            jCell.setStale(VisualKey.VISIBLE);
            Iterator<? extends AspectJCell> iter = jCell.getContext();
            while (iter.hasNext()) {
                iter.next()
                    .setStale(VisualKey.VISIBLE);
            }
        }
        return result;
    }

    /** Prevents nodes from being collapsed. */
    @Override
    protected void setExpandedState(TreePath path, boolean state) {
        // Ignore all collapse requests; collapse events will not be fired
        if (state) {
            super.setExpandedState(path, state);
        }
    }

    private AspectJGraph getJGraph() {
        return this.jGraph;
    }

    /** The JGraph permanently associated with this {@link JTree}. */
    private final AspectJGraph jGraph;
    /** Rule of which this tree shows the levels. */
    private RuleModel rule;
    /** Mapping from level indices to level tree nodes. */
    private final Map<Index,LevelNode> levelNodeMap = new TreeMap<>();
    /**
     * Model for which {@link #levelNodeMap} {@link #levelCellMap} and
     * {@link #selectedSet} are currently computed.
     */
    private AspectJModel jModel;
    /** Set of all rule elements. */
    private final Set<AspectJCell> allCellSet = new HashSet<>();
    /** Set of rule elements that are visible according to the currently selected
     * level nodes.
     */
    private final Set<AspectJCell> selectedSet = new HashSet<>();
    /** Mapping from level indices to jCells. */
    private final Map<Index,Set<AspectJCell>> levelCellMap = new TreeMap<>();

    private class LevelNode extends TreeNode {
        /** Creates an instance for a given level index. */
        public LevelNode(Index index) {
            this.index = index;
            this.selected = true;
        }

        /**
         * Returns the level index wrapped in this node.
         */
        public Index getIndex() {
            return this.index;
        }

        public String getName() {
            if (this.name == null) {
                StringBuilder result = new StringBuilder(this.index.getOperator()
                    .getSymbol());
                String levelName = null;
                if (this.index.getLevelNode() != null) {
                    levelName = this.index.getLevelNode()
                        .getLevelName();
                }
                if (levelName == null) {
                    for (int level : this.index.getIntArray()) {
                        result.append('.');
                        result.append(level);
                    }
                } else {
                    result.append('.');
                    result.append(levelName);
                }
                this.name = result.toString();
            }
            return this.name;
        }

        @Override
        public boolean hasCheckbox() {
            return true;
        }

        @Override
        public boolean isSelected() {
            return this.selected;
        }

        @Override
        public void setSelected(boolean selected) {
            this.selected = selected;
            Set<AspectJCell> changes = updateVisibleCells(Collections.singleton(this));
            getJGraph().refreshCells(changes);
        }

        @Override
        public String toString() {
            return getName();
        }

        /** The name of this level node. */
        private String name;
        /** The level index permanently associated with this level node. */
        private final Index index;
        /** Flag indicating if this node is currently selected. */
        private boolean selected;
    }

    /** Class to deal with mouse events over the label list. */
    private class MyMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                TreePath path = getPathForLocation(e.getPoint().x, e.getPoint().y);
                if (path != null) {
                    LevelNode levelNode = (LevelNode) path.getLastPathComponent();
                    levelNode.setSelected(!levelNode.isSelected());
                    RuleLevelTree.this.repaint();
                }
            }
        }
    }
}
