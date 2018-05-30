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

import static groove.gui.SimulatorModel.Change.GRAMMAR;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JPopupMenu;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import groove.grammar.ModuleName;
import groove.grammar.QualName;
import groove.grammar.model.GrammarModel;
import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.SimulatorModel;
import groove.gui.SimulatorModel.Change;
import groove.gui.display.DismissDelayer;
import groove.gui.display.DisplayKind;
import groove.gui.display.ResourceDisplay;
import groove.lts.GraphState;

/**
 * Panel that displays a tree of resources. Each resource is added by means of
 * a list of strings, which corresponds to its full path name in the grammar.
 *
 * @author Maarten de Mol
 */
public class ResourceTree extends AbstractResourceTree {
    // The tree, and its root.
    private final DefaultTreeModel tree;
    private final DisplayTreeNode root;

    // Remembers the previous enabled background color.
    private Color enabledBackground;

    /** Creates an instance for a given simulator. */
    public ResourceTree(ResourceDisplay parent) {
        super(parent);
        // the following is the easiest way to ensure that changes in
        // tree labels will be correctly reflected in the display
        // A cleaner way is to invoke DefaultTreeModel.nodeChanged,
        // but how are we supposed to know when this occurs?
        setLargeModel(true);
        setRootVisible(false);
        setShowsRootHandles(true);
        setEnabled(false);
        setToggleClickCount(0);

        // set cell renderer
        DisplayTreeCellRenderer renderer = new DisplayTreeCellRenderer(this);
        renderer.setLeafIcon(Icons.getListIcon(parent.getResourceKind()));
        setCellRenderer(renderer);
        getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

        // initialize tree
        this.root = new DisplayTreeNode();
        this.tree = new DefaultTreeModel(this.root, true);
        setModel(this.tree);

        // set key bindings
        ActionMap am = getActionMap();
        am.put(Options.BACK_ACTION_NAME, getActions().getBackAction());
        am.put(Options.FORWARD_ACTION_NAME, getActions().getForwardAction());
        InputMap im = getInputMap();
        im.put(Options.BACK_KEY, Options.BACK_ACTION_NAME);
        im.put(Options.FORWARD_KEY, Options.FORWARD_ACTION_NAME);

        // add tool tips
        installListeners();
        ToolTipManager.sharedInstance()
            .registerComponent(this);
        addMouseListener(new DismissDelayer(this));
    }

    @Override
    MouseListener createMouseListener() {
        return new MyMouseListener();
    }

    /**
     * Loads a grammar, by adding all the corresponding resources to the
     * local tree. The resources are sorted before they are added.
     * Returns all the newly created {@link TreeNode}s.
     */
    private Set<DisplayTreeNode> loadGrammar(GrammarModel grammar) {
        // allocate result
        Set<DisplayTreeNode> result = new HashSet<>();

        // get all resources, and store them in the sorted FolderTree
        FolderTree ftree = new FolderTree();
        for (QualName resource : grammar.getNames(getResourceKind())) {
            ftree.insert(resource);
        }

        // store all FolderTree items in this.root
        ftree.store(this.root, ModuleName.TOP, result);

        // return result
        return result;
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel, Set<Change> changes) {
        suspendListeners();
        if (changes.contains(GRAMMAR)) {
            // remember the visible and selected resources (+paths)
            Set<QualName> visible = new HashSet<>();
            Set<QualName> selected = getSimulatorModel().getSelectSet(getResourceKind());
            for (int i = 0; i < getRowCount(); i++) {
                TreePath path = getPathForRow(i);
                TreeNode node = (TreeNode) path.getLastPathComponent();
                if (node instanceof ResourceTreeNode) {
                    ResourceTreeNode rnode = (ResourceTreeNode) node;
                    visible.add(rnode.getQualName());
                } else if (node instanceof PathNode) {
                    PathNode pnode = (PathNode) node;
                    visible.add(pnode.getQualName());
                }
            }

            // build new tree
            this.root.removeAllChildren();
            GrammarModel grammar = source.getGrammar();
            if (grammar != null) {
                Set<DisplayTreeNode> created = loadGrammar(grammar);
                this.tree.reload(this.root);

                // expand/select all the previously expanded/selected nodes
                for (DisplayTreeNode node : created) {
                    if (node instanceof ResourceTreeNode) {
                        QualName name = ((ResourceTreeNode) node).getQualName();
                        if (visible.contains(name) || selected.contains(name)) {
                            TreePath path = new TreePath(node.getPath());
                            expandPath(path.getParentPath());
                            if (getSimulatorModel().getSelectSet(getResourceKind())
                                .contains(name)) {
                                addSelectionPath(path);
                            }
                            if (selected.contains(name)) {
                                addSelectionPath(path);
                            }
                        }
                    } else if (node instanceof PathNode) {
                        QualName name = ((PathNode) node).getQualName();
                        if (visible.contains(name)) {
                            TreePath path = new TreePath(node.getPath());
                            expandPath(path.getParentPath());
                        }
                    }
                }

                // store new tree and refresh display
                refresh(source.getState());
            }
        }
        activateListeners();
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

    /**
     * Refreshes the selection in the tree, based on the current state of the
     * Simulator.
     */
    private void refresh(GraphState state) {
        setEnabled(getGrammar() != null);
    }

    /**
     * The default user action when a mouse button is clicked.
     * Override to get specific behavior.
     */
    public void mouseClicked(TreeNode node, MouseEvent event) {
        // default - no user action
    }

    /**
     * The default user action when a mouse button is pressed.
     * Override to get specific behavior.
     */
    public void mousePressed(TreeNode node, MouseEvent event) {
        // default - no user action
    }

    // ========================================================================
    // LOCAL CLASS - MyMouseListener
    // ========================================================================

    /**
     * Mouse listener that relays events to {@link ResourceTree#mouseClicked},
     * {@link ResourceTree#mousePressed} and {@link ResourceTree#createPopupMenu}.
     */
    private class MyMouseListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent evt) {

            // find the node that belongs to this event
            TreeNode selected = getMousedNode(evt);

            // show popup menu
            if (evt.isPopupTrigger()) {
                showPopupMenu(selected, evt);
            }

            // change active tab in the Simulator
            DisplayKind display = DisplayKind.toDisplay(getResourceKind());
            if (selected instanceof ResourceTreeNode && display != null) {
                getSimulatorModel().setDisplay(display);
            }

            // invoke editor, if this was a double click
            if (selected instanceof ResourceTreeNode && evt.getClickCount() > 1) {
                getActions().getEditAction(getResourceKind())
                    .execute();
            }

            // invoke user actions
            ResourceTree.this.mousePressed(selected, evt);
        }

        @Override
        public void mouseReleased(MouseEvent evt) {

            // find the node that belongs to this event
            TreeNode selected = getMousedNode(evt);

            // show popup menu
            if (evt.isPopupTrigger()) {
                showPopupMenu(selected, evt);
            }
        }

        @Override
        public void mouseClicked(MouseEvent evt) {
            ResourceTree.this.mouseClicked(getMousedNode(evt), evt);
        }

        /** Get the TreeNode of the event. */
        private TreeNode getMousedNode(MouseEvent evt) {

            // get the TreePath that belongs to this event
            TreePath path = getPathForLocation(evt.getX(), evt.getY());

            // if no TreePath, then no node was selected
            if (path == null) {
                return null;
            }

            // on right click, also explicitly set the selected item
            if (evt.isPopupTrigger()) {
                setSelectionPath(path);
            }

            // get the selected object out of the TreePath, and return it
            Object selected = path.getLastPathComponent();
            if (selected instanceof TreeNode) {
                return (TreeNode) selected;
            } else {
                return null;
            }
        }

        /** Show the popup menu. */
        public void showPopupMenu(TreeNode node, MouseEvent evt) {
            ResourceTree.this.requestFocus();
            JPopupMenu menu = createPopupMenu(node);
            if (menu != null) {
                menu.show(evt.getComponent(), evt.getX(), evt.getY());
            }
        }
    }

    // ========================================================================
    // LOCAL CLASS - PathNode
    // ========================================================================

    /**
     * A {@link PathNode} is a {@link DefaultMutableTreeNode} that corresponds
     * to a path in the current grammar.
     */
    class PathNode extends FolderTreeNode {
        // The (full) name of the path.
        private final QualName qualName;

        /** Default constructor. */
        public PathNode(QualName qualName, String shortName) {
            super(shortName);
            this.qualName = qualName;
        }

        /** Getter for the resource name. */
        public QualName getQualName() {
            return this.qualName;
        }
    }

    // ========================================================================
    // LOCAL CLASS - FolderTree
    // ========================================================================

    /**
     * A {@link FolderTree} is a sorted tree of resources. Each resource is
     * split in its path components, and each component is stored as a
     * separate level in the tree.
     */
    private class FolderTree {

        // The subfolders of this tree.
        private final TreeMap<String,FolderTree> folders;

        // The resources that are stores directly at this level.
        private final TreeSet<String> resources;

        /** Create a new (empty) FolderTree. */
        public FolderTree() {
            this.folders = new TreeMap<>();
            this.resources = new TreeSet<>();
        }

        /** Insert a new resource in the tree. */
        public void insert(QualName resource) {
            insert(0, resource.tokens(), resource);
        }

        /** Local indexes insert. */
        private void insert(int index, List<String> components, QualName resource) {
            if (index == components.size() - 1) {
                this.resources.add(components.get(index));
            } else {
                FolderTree folder = this.folders.get(components.get(index));
                if (folder == null) {
                    folder = new FolderTree();
                }
                folder.insert(index + 1, components, resource);
                this.folders.put(components.get(index), folder);
            }
        }

        /**
         * Adds all tree resources to a DefaultMutableTreeNode (with the given
         * path). Also collects the created {@link TreeNode}s.
         */
        public void store(DisplayTreeNode root, ModuleName path, Set<DisplayTreeNode> created) {
            for (Map.Entry<String,FolderTree> entry : this.folders.entrySet()) {
                QualName subpath = path.extend(entry.getKey());
                PathNode node = new PathNode(subpath, entry.getKey());
                entry.getValue()
                    .store(node, subpath, created);
                created.add(node);
                //root.add(node);
                root.insertSorted(node);
            }
            for (String resource : this.resources) {
                QualName qualName = path.extend(resource);
                ResourceTreeNode leaf = new ResourceTreeNode(getParentDisplay(), qualName);
                created.add(leaf);
                //root.add(leaf);
                root.insertSorted(leaf);
            }
        }
    }

}