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

import static groove.gui.SimulatorModel.Change.GRAMMAR;
import static groove.gui.SimulatorModel.Change.GTS;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import groove.grammar.QualName;
import groove.grammar.model.GrammarModel;
import groove.grammar.model.ResourceKind;
import groove.gui.Simulator;
import groove.gui.SimulatorListener;
import groove.gui.SimulatorModel;
import groove.gui.SimulatorModel.Change;
import groove.gui.action.ActionStore;
import groove.gui.display.DismissDelayer;
import groove.gui.display.ResourceDisplay;

/** Abstract superclass for all display trees. */
public abstract class AbstractResourceTree extends JTree implements SimulatorListener {
    /** Constructs a resource tree for a given parent display. */
    protected AbstractResourceTree(ResourceDisplay parentDisplay) {
        this.parentDisplay = parentDisplay;
        this.resourceKind = parentDisplay.getResourceKind();
        ToolTipManager.sharedInstance()
            .registerComponent(this);
        addMouseListener(new DismissDelayer(this));
    }

    void installListeners() {
        addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent e) {
                AbstractResourceTree.this.repaint();
            }

            @Override
            public void focusGained(FocusEvent e) {
                AbstractResourceTree.this.repaint();
            }
        });
        addMouseListener(getMouseListener());
        activateListeners();
    }

    void activateListeners() {
        if (this.listening) {
            throw new IllegalStateException();
        }
        this.listening = true;
        addTreeSelectionListener(getSelectionListener());
        getSimulatorModel().addListener(this, GRAMMAR, GTS, Change.toChange(getResourceKind()));
    }

    /** Suspend the listeners of this tree. */
    void suspendListeners() {
        if (!this.listening) {
            throw new IllegalStateException();
        }
        this.listening = false;
        removeTreeSelectionListener(getSelectionListener());
        getSimulatorModel().removeListener(this);
    }

    private TreeSelectionListener getSelectionListener() {
        if (this.selectionListener == null) {
            this.selectionListener = createSelectionListener();
        }
        return this.selectionListener;
    }

    /** Callback factory method for the mouse listener of this resource tree. */
    TreeSelectionListener createSelectionListener() {
        return new MySelectionListener();
    }

    private MouseListener getMouseListener() {
        if (this.mouseListener == null) {
            this.mouseListener = createMouseListener();
        }
        return this.mouseListener;
    }

    /** Callback factory method for the mouse listener of this resource tree. */
    abstract MouseListener createMouseListener();

    /**
     * Creates a popup menu for the resource tree.
     * @param node tree node that the mouse is over
     */
    JPopupMenu createPopupMenu(TreeNode node) {
        JPopupMenu res = new JPopupMenu();
        res.setFocusable(false);
        res.add(getActions().getNewAction(getResourceKind()));
        if (node instanceof ResourceTreeNode) {
            res.add(getActions().getEditAction(getResourceKind()));
            res.addSeparator();
            res.add(getActions().getCopyAction(getResourceKind()));
            res.add(getActions().getDeleteAction(getResourceKind()));
            res.add(getActions().getRenameAction(getResourceKind()));
            if (getResourceKind().isEnableable()) {
                res.addSeparator();
                res.add(getActions().getEnableAction(getResourceKind()));
            }
        }
        return res;
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

    /** Unhooks this object from all observables. */
    public void dispose() {
        suspendListeners();
    }

    /** Convenience method to retrieve the current grammar view. */
    final GrammarModel getGrammar() {
        return getSimulatorModel().getGrammar();
    }

    /** Returns the parent display. */
    final ResourceDisplay getParentDisplay() {
        return this.parentDisplay;
    }

    /** Convenience method to retrieve the associated simulator. */
    final Simulator getSimulator() {
        return this.parentDisplay.getSimulator();
    }

    /** Convenience method to retrieve the simulator model. */
    final SimulatorModel getSimulatorModel() {
        return this.parentDisplay.getSimulatorModel();
    }

    /** Convenience method to retrieve the simulator action store. */
    final ActionStore getActions() {
        return this.parentDisplay.getActions();
    }

    /** Returns the resource kind of this tree. */
    final ResourceKind getResourceKind() {
        return this.resourceKind;
    }

    private final ResourceDisplay parentDisplay;
    private final ResourceKind resourceKind;
    private MouseListener mouseListener;
    private TreeSelectionListener selectionListener;
    /** Flag indicating if the listeners are currently active. */
    private boolean listening;
    /**
     * The background colour of this component when it is enabled.
     */
    private Color enabledBackground;

    /**
     * Custom selection listener, which notifies the {@link SimulatorModel}
     * of every selection change.
     */
    class MySelectionListener implements TreeSelectionListener {
        /** Empty constructor with the correct visibility. */
        public MySelectionListener() {
            // Empty
        }

        /**
         * Notifies the {@link SimulatorModel} every time the selection of
         * this resource tree changes.
         */
        @Override
        public void valueChanged(TreeSelectionEvent evt) {
            List<TreeNode> selected = new ArrayList<>();
            suspendListeners();
            TreePath[] paths = getSelectionPaths();
            for (int i = 0; paths != null && i < paths.length; i++) {
                TreeNode node = (TreeNode) paths[i].getLastPathComponent();
                collectResources(node, selected);
            }
            setSelection(selected);
            activateListeners();
        }

        /** Collects the selected resources by descending into the tree nodes with children. */
        private void collectResources(TreeNode node, Collection<TreeNode> result) {
            if (node instanceof FolderTreeNode) {
                FolderTreeNode path = (FolderTreeNode) node;
                for (int i = 0; i < path.getChildCount(); i++) {
                    collectResources(path.getChildAt(i), result);
                }
            } else {
                result.add(node);
            }
        }

        void setSelection(Collection<TreeNode> selectedNodes) {
            List<QualName> selectedNames = new ArrayList<>();
            for (TreeNode node : selectedNodes) {
                if (node instanceof ResourceTreeNode) {
                    selectedNames.add(((ResourceTreeNode) node).getQualName());
                }
            }
            getSimulatorModel().doSelectSet(getResourceKind(), selectedNames);
        }
    }

    /**
     * Directory nodes of the tree.
     */
    static class FolderTreeNode extends DisplayTreeNode {
        /**
         * Creates a new directory node with a given name.
         */
        public FolderTreeNode(String name) {
            super(name, true);
        }
    }
}
