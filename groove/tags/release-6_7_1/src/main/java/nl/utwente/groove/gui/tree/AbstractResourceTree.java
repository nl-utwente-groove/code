/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.gui.tree;

import static nl.utwente.groove.gui.SimulatorModel.Change.GRAMMAR;
import static nl.utwente.groove.gui.SimulatorModel.Change.GTS;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.SimulatorListener;
import nl.utwente.groove.gui.SimulatorModel;
import nl.utwente.groove.gui.SimulatorModel.Change;
import nl.utwente.groove.gui.action.ActionStore;
import nl.utwente.groove.gui.display.DismissDelayer;
import nl.utwente.groove.gui.display.ResourceDisplay;
import nl.utwente.groove.util.Factory;

/** Abstract superclass for all display trees. */
public abstract class AbstractResourceTree extends JTree implements SimulatorListener {
    /** Constructs a resource tree for a given parent display. */
    protected AbstractResourceTree(ResourceDisplay parentDisplay) {
        this.parentDisplay = parentDisplay;
        this.resourceKind = parentDisplay.getResourceKind();
    }

    /** Adds all listeners to this resource tree. */
    void installListeners() {
        ToolTipManager.sharedInstance().registerComponent(this);
        addFocusListener(getFocusListener());
        addMouseListener(getMouseListener());
        addMouseListener(getDismissDelayer());
        addTreeSelectionListener(getSelectionListener());
        getSimulatorModel().addListener(this, GRAMMAR, GTS, Change.toChange(getResourceKind()));
        activateListening();
    }

    /** Disposes the listeners of this resource tree.
     * This is the inverse of {@link #installListeners()};
     * if a subclass overrides one, it should also override the other
     */
    void disposeListeners() {
        ToolTipManager.sharedInstance().unregisterComponent(this);
        removeFocusListener(getFocusListener());
        removeMouseListener(getMouseListener());
        removeMouseListener(getDismissDelayer());
        removeTreeSelectionListener(getSelectionListener());
        getSimulatorModel().removeListener(this);
    }

    /** Returns the (lazily computed) focus listener. */
    private FocusListener getFocusListener() {
        return this.focusListener.get();
    }

    /** The (lazily computed) focus listener. */
    private final Supplier<FocusListener> focusListener = Factory.lazy(this::computeFocusListener);

    /**
     * Computes the values for {@link #focusListener}.
     */
    private FocusListener computeFocusListener() {
        return new FocusListener() {
            @Override
            public void focusLost(FocusEvent e) {
                AbstractResourceTree.this.repaint();
            }

            @Override
            public void focusGained(FocusEvent e) {
                AbstractResourceTree.this.repaint();
            }
        };
    }

    /** Returns the (lazily computed) selection listener.
     * @see #createSelectionListener()
     */
    final TreeSelectionListener getSelectionListener() {
        return this.selectionListener.get();
    }

    private final Supplier<TreeSelectionListener> selectionListener
        = Factory.lazy(this::createSelectionListener);

    /** Callback factory method to create the mouse listener of this resource tree. */
    TreeSelectionListener createSelectionListener() {
        return new MySelectionListener();
    }

    /** Returns the (lazily computed) mouse listener. */
    private MouseListener getMouseListener() {
        return this.mouseListener.get();
    }

    /** The (lazily computed) mouse listener. */
    private final Supplier<MouseListener> mouseListener = Factory.lazy(this::createMouseListener);

    /** Callback factory method for the mouse listener of this resource tree. */
    abstract MouseListener createMouseListener();

    /** Returns the (lazily computed) dismiss delay mouse listener. */
    private MouseListener getDismissDelayer() {
        return this.dismissDelayer.get();
    }

    /** The (lazily computed) dismiss delay mouse listener. */
    private final Supplier<MouseListener> dismissDelayer = Factory.lazy(this::createDismissDelayer);

    /** Callback factory method for the dismiss delay mouse listener of this resource tree. */
    private MouseListener createDismissDelayer() {
        return new DismissDelayer(this);
    }

    /**
     * Sets the listening status to {@code false}, if it was not already {@code false}.
     * @return {@code true} if listening was suspended as a result of this call;
     * {@code false} if it was already suspended.
     */
    final boolean suspendListening() {
        boolean result = this.listening;
        if (result) {
            this.listening = false;
        }
        return result;
    }

    /** Sets the listening flag to {@code true}. */
    final void activateListening() {
        if (this.listening) {
            throw new IllegalStateException();
        }
        this.listening = true;
    }

    /** Returns the listening status. */
    final boolean isListening() {
        return this.listening;
    }

    /** Flag indicating if the listeners are currently active. */
    private boolean listening;

    /** Indicates if internal states and transitions should be included.
     * @see Options#SHOW_RECIPE_STEPS_OPTION
     */
    boolean isShowInternal() {
        return getOptions().isSelected(Options.SHOW_RECIPE_STEPS_OPTION);
    }

    /** Indicates if absent states and transitions should be included.
     * @see Options#SHOW_ABSENT_STATES_OPTION
     */
    boolean isShowAbsent() {
        return getOptions().isSelected(Options.SHOW_ABSENT_STATES_OPTION);
    }

    /** Indicates if anchors should be shown
     * @see Options#SHOW_ANCHORS_OPTION
     */
    boolean isShowAnchors() {
        return getOptions().isSelected(Options.SHOW_ANCHORS_OPTION);
    }

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
        disposeListeners();
    }

    /** Convenience method to retrieve the current grammar view. */
    final GrammarModel getGrammar() {
        return getSimulatorModel().getGrammar();
    }

    /** Returns the parent display. */
    final ResourceDisplay getParentDisplay() {
        return this.parentDisplay;
    }

    private final ResourceDisplay parentDisplay;

    /** Convenience method to retrieve the associated simulator. */
    final Simulator getSimulator() {
        return this.parentDisplay.getSimulator();
    }

    /** Convenience method to retrieve the simulator options. */
    final Options getOptions() {
        return getSimulator().getOptions();
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

    private final ResourceKind resourceKind;

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
            if (suspendListening()) {
                List<DisplayTreeNode> selected = new ArrayList<>();
                TreePath[] paths = getSelectionPaths();
                if (paths != null) {
                    for (int i = 0; i < paths.length; i++) {
                        var node = (DisplayTreeNode) paths[i].getLastPathComponent();
                        collectResources(node, selected);
                    }
                    if (selected.size() > paths.length) {
                        var newPaths = selected
                            .stream()
                            .map(DisplayTreeNode::getPath)
                            .map(TreePath::new)
                            .toList();
                        setSelectionPaths(newPaths.toArray(TreePath[]::new));
                    }
                }
                pushSelection(selected);
                activateListening();
            }
        }

        /** Collects the selected resources by descending into the tree nodes with children. */
        void collectResources(DisplayTreeNode node, Collection<DisplayTreeNode> result) {
            if (node instanceof FolderTreeNode path) {
                for (int i = 0; i < path.getChildCount(); i++) {
                    collectResources((DisplayTreeNode) path.getChildAt(i), result);
                }
            } else {
                result.add(node);
            }
        }

        /** Pushes the information in the selected nodes to the simulator model. */
        void pushSelection(Collection<DisplayTreeNode> selectedNodes) {
            List<QualName> selectedNames = new ArrayList<>();
            for (TreeNode node : selectedNodes) {
                if (node instanceof ResourceTreeNode t) {
                    selectedNames.add(t.getQualName());
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
