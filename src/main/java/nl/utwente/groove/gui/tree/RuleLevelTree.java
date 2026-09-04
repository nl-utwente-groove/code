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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.aspect.AspectElement;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.aspect.AspectNode;
import nl.utwente.groove.grammar.model.RuleModel;
import nl.utwente.groove.grammar.model.RuleModel.Index;
import nl.utwente.groove.gui.look.VisualKey;
import nl.utwente.groove.gui.view.AspectGraphCanvas;
import nl.utwente.groove.gui.view.AspectViewCell;
import nl.utwente.groove.gui.view.GraphCanvas;
import nl.utwente.groove.gui.view.GraphCanvasListener;
import nl.utwente.groove.gui.view.GraphViewModel;

/**
 * Checkbox tree controlling the visibility of rule levels.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class RuleLevelTree extends CheckboxTree implements TreeSelectionListener {
    /** Creates a new tree, for a given rule canvas. */
    public RuleLevelTree(AspectGraphCanvas canvas) {
        this.canvas = canvas;
        setLargeModel(true);
        setEnabled(canvas.isEnabled());
        setShowsRootHandles(false);
        getUI().setCollapsedIcon(null);
        getUI().setExpandedIcon(null);
        addMouseListener(new MyMouseListener());
        // deselect the level tree whenever the graph
        // selection changes
        canvas.addCanvasListener(new GraphCanvasListener<AspectGraph>() {
            @Override
            public void selectionChanged(GraphCanvas<AspectGraph> canvas) {
                clearSelection();
            }
        });
    }

    /**
     * Replaces the view model on which this level tree is based with the
     * (supposedly new) model in the associated canvas. Gets the rule
     * model from the canvas.
     */
    private void synchroniseViewModel() {
        var viewModel = getCanvas().getViewModel();
        if (viewModel != this.viewModel) {
            this.viewModel = viewModel;
            if (viewModel == null) {
                this.rule = null;
            } else {
                this.rule = (RuleModel) getCanvas().getResourceModel();
            }
            boolean enabled = updateTree();
            for (Set<AspectViewCell> levelCells : this.levelCellMap.values()) {
                this.allCellSet.addAll(levelCells);
            }
            updateVisibleCells(this.levelNodeMap.values());
            setEnabled(enabled);
        }
    }

    /** Indicates if a given aspect cell is in the set of visible cells. */
    public boolean isVisible(AspectViewCell jCell) {
        synchroniseViewModel();
        return !this.allCellSet.contains(jCell) || this.selectedSet.contains(jCell);
    }

    /**
     * Emphasises/deemphasises cells in the associated canvas, based on the list
     * selection.
     */
    @Override
    public void valueChanged(@Nullable TreeSelectionEvent e) {
        synchroniseViewModel();
        Set<AspectViewCell> emphSet = new HashSet<>();
        TreePath[] selectionPaths = getSelectionPaths();
        if (selectionPaths != null) {
            for (TreePath selectedPath : selectionPaths) {
                Index index = ((LevelNode) selectedPath.getLastPathComponent()).getIndex();
                Set<AspectViewCell> levelCells = this.levelCellMap.get(index);
                assert levelCells != null; // every level node in the tree has its cells computed
                emphSet.addAll(levelCells);
            }
        }
        emphSet.retainAll(this.selectedSet);
        getCanvas().select(emphSet);
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
        var rule = this.rule;
        Map<Index,Set<AspectElement>> levelTree = rule == null
            ? null
            : rule.getLevelTree();
        boolean enabled = levelTree != null && levelTree.size() > 1;
        if (enabled) {
            assert levelTree != null && rule != null; // guaranteed by enabled
            var viewModel = this.viewModel;
            assert viewModel != null; // the rule was taken from a non-null model
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
                    assert parentNode != null; // the indices are traversed parents first
                    parentNode.add(levelNode);
                }
                this.levelNodeMap.put(index, levelNode);
                Set<AspectViewCell> levelCells = new HashSet<>();
                // add all cells for this level according to the rule level tree
                for (AspectElement elem : levelEntry.getValue()) {
                    // this is an element from the normalised source, about which the view model is unaware
                    var jCell = (AspectViewCell) viewModel.getJCell(elem.denormalise());
                    if (jCell != null) {
                        levelCells.add(jCell);
                    }
                }
                // now subtract the cells of the parent
                // note that we go through the indices in an ordered fashion
                // so the parent has already been computed
                if (!index.isTopLevel()) {
                    Set<AspectViewCell> parentCells = this.levelCellMap.get(index.getParent());
                    assert parentCells != null; // the parent index was processed earlier
                    levelCells.removeAll(parentCells);
                }
                // also add the nesting nodes and edges
                AspectNode ruleLevelNode = index.getLevelNode();
                if (ruleLevelNode != null) {
                    var jCell = (AspectViewCell) viewModel.getJCell(ruleLevelNode.denormalise());
                    if (jCell != null) {
                        levelCells.add(jCell);
                    }
                    for (AspectElement edge : rule.getSource().edgeSet(ruleLevelNode)) {
                        jCell = (AspectViewCell) viewModel.getJCell(edge.denormalise());
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
    private Set<AspectViewCell> updateVisibleCells(Collection<LevelNode> changedNodes) {
        Set<AspectViewCell> selecteds = new HashSet<>();
        Set<AspectViewCell> unselecteds = new HashSet<>();
        for (LevelNode node : changedNodes) {
            Set<AspectViewCell> levelCells = this.levelCellMap.get(node.getIndex());
            assert levelCells != null; // every level node in the tree has its cells computed
            if (node.isSelected()) {
                selecteds.addAll(levelCells);
            } else {
                unselecteds.addAll(levelCells);
            }
        }
        this.selectedSet.removeAll(unselecteds);
        this.selectedSet.addAll(selecteds);
        // Collect the changed cells
        Set<AspectViewCell> result = new HashSet<>(selecteds.size() + unselecteds.size());
        result.addAll(selecteds);
        result.addAll(unselecteds);
        // now refresh the changed cells
        for (AspectViewCell jCell : result) {
            jCell.setStale(VisualKey.VISIBLE);
            Iterator<? extends AspectViewCell> iter = jCell.getContext();
            while (iter.hasNext()) {
                iter.next().setStale(VisualKey.VISIBLE);
            }
        }
        return result;
    }

    /** Prevents nodes from being collapsed. */
    @Override
    protected void setExpandedState(@Nullable TreePath path, boolean state) {
        // Ignore all collapse requests; collapse events will not be fired
        if (state) {
            super.setExpandedState(path, state);
        }
    }

    private AspectGraphCanvas getCanvas() {
        return this.canvas;
    }

    /** The canvas permanently associated with this {@link JTree}. */
    private final AspectGraphCanvas canvas;
    /** Rule of which this tree shows the levels. */
    private @Nullable RuleModel rule;
    /** Mapping from level indices to level tree nodes. */
    private final Map<Index,LevelNode> levelNodeMap = new TreeMap<>();
    /**
     * Model for which {@link #levelNodeMap} {@link #levelCellMap} and
     * {@link #selectedSet} are currently computed.
     */
    private @Nullable GraphViewModel<AspectGraph> viewModel;
    /** Set of all rule elements. */
    private final Set<AspectViewCell> allCellSet = new HashSet<>();
    /** Set of rule elements that are visible according to the currently selected
     * level nodes.
     */
    private final Set<AspectViewCell> selectedSet = new HashSet<>();
    /** Mapping from level indices to jCells. */
    private final Map<Index,Set<AspectViewCell>> levelCellMap = new TreeMap<>();

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
            var result = this.name;
            if (result == null) {
                StringBuilder builder = new StringBuilder(this.index.getOperator().getSymbol());
                String levelName = null;
                var levelNode = this.index.getLevelNode();
                if (levelNode != null) {
                    levelName = levelNode.getId();
                }
                if (levelName == null) {
                    for (int level : this.index.getIntArray()) {
                        builder.append('.');
                        builder.append(level);
                    }
                } else {
                    builder.append('.');
                    builder.append(levelName);
                }
                this.name = result = builder.toString();
            }
            return result;
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
            Set<AspectViewCell> changes = updateVisibleCells(Collections.singleton(this));
            getCanvas().refresh(changes, false);
        }

        @Override
        public String toString() {
            return getName();
        }

        /** The name of this level node. */
        private @Nullable String name;
        /** The level index permanently associated with this level node. */
        private final Index index;
        /** Flag indicating if this node is currently selected. */
        private boolean selected;
    }

    /** Class to deal with mouse events over the label list. */
    private class MyMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(@Nullable MouseEvent e) {
            if (e != null && e.getClickCount() == 2) {
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
