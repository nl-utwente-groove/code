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
 * $Id: RuleJTree.java,v 1.33 2008-03-18 12:54:42 iovka Exp $
 */
package groove.gui;

import static groove.gui.SimulatorModel.Change.GRAMMAR;
import static groove.gui.SimulatorModel.Change.GTS;
import static groove.gui.SimulatorModel.Change.MATCH;
import static groove.gui.SimulatorModel.Change.RULE;
import static groove.gui.SimulatorModel.Change.STATE;
import groove.gui.SimulatorModel.Change;
import groove.gui.action.ActionStore;
import groove.lts.GraphState;
import groove.trans.ResourceKind;
import groove.view.GrammarModel;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * Panel that displays a tree of resources. Each resource is added by means of
 * a list of strings, which corresponds to its full path name in the grammar. 
 *  
 * @author Maarten de Mol
 */
public class ResourceTree extends JTree implements SimulatorListener {

    // The display in which this tree is contained.
    private final ResourceDisplay parentDisplay;

    // The tree, and its root.
    private final DefaultTreeModel tree;
    private final SortingTreeNode root;

    // The kind of resources that are displayed.
    private final ResourceKind resourceKind;

    // The listeners.
    private final TreeSelectionListener selectionListener;
    private boolean listening;

    // Remembers the previous enabled background color.
    private Color enabledBackground;

    // Used separator character.
    private static final String SEPARATOR = ".";

    /** Creates an instance for a given simulator. */
    public ResourceTree(ResourceDisplay parent) {

        // store parent display, resource kind and leaf icon
        this.parentDisplay = parent;
        this.resourceKind = parent.getResourceKind();
        this.selectionListener = new MySelectionListener();

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
        getSelectionModel().setSelectionMode(
            TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

        // initialize tree
        this.root = new SortingTreeNode();
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
        ToolTipManager.sharedInstance().registerComponent(this);
    }

    /**
     * Loads a grammar, by adding all the corresponding resources to the
     * local tree. The resources are sorted before they are added.
     * Returns all the newly created {@link TreeNode}s.
     */
    private Set<SortingTreeNode> loadGrammar(GrammarModel grammar) {
        // allocate result
        Set<SortingTreeNode> result = new HashSet<SortingTreeNode>();

        // get all resources, and store them in the sorted FolderTree
        FolderTree ftree = new FolderTree();
        for (String resource : grammar.getNames(this.resourceKind)) {
            ftree.insert(resource);
        }

        // store all FolderTree items in this.root
        ftree.store(this.root, "", result);

        // return result
        return result;
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel,
            Set<Change> changes) {
        suspendListeners();
        if (changes.contains(GRAMMAR)) {
            // remember the visible and selected resources (+paths)
            Set<String> visible = new HashSet<String>();
            Set<String> selected =
                getSimulatorModel().getSelectSet(this.resourceKind);
            for (int i = 0; i < getRowCount(); i++) {
                TreePath path = getPathForRow(i);
                TreeNode node = (TreeNode) path.getLastPathComponent();
                if (node instanceof ResourceTreeNode) {
                    ResourceTreeNode rnode = (ResourceTreeNode) node;
                    visible.add(rnode.getName());
                } else if (node instanceof PathNode) {
                    PathNode pnode = (PathNode) node;
                    visible.add(pnode.getPathName());
                }
            }

            // build new tree
            this.root.removeAllChildren();
            GrammarModel grammar = source.getGrammar();
            if (grammar != null) {
                Set<SortingTreeNode> created = loadGrammar(grammar);
                this.tree.reload(this.root);

                // expand/select all the previously expanded/selected nodes
                for (SortingTreeNode node : created) {
                    if (node instanceof ResourceTreeNode) {
                        String name = ((ResourceTreeNode) node).getName();
                        if (visible.contains(name) || selected.contains(name)) {
                            TreePath path = new TreePath(node.getPath());
                            expandPath(path.getParentPath());
                            if (getSimulatorModel().getSelectSet(
                                this.resourceKind).contains(name)) {
                                addSelectionPath(path);
                            }
                            if (selected.contains(name)) {
                                addSelectionPath(path);
                            }
                        }
                    } else if (node instanceof PathNode) {
                        String name = ((PathNode) node).getPathName();
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

    private void installListeners() {
        addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent e) {
                ResourceTree.this.repaint();
            }

            @Override
            public void focusGained(FocusEvent e) {
                ResourceTree.this.repaint();
            }
        });
        addMouseListener(new MyMouseListener());
        activateListeners();
    }

    private void activateListeners() {
        if (this.listening) {
            throw new IllegalStateException();
        }
        addTreeSelectionListener(this.selectionListener);
        getSimulatorModel().addListener(this, GRAMMAR, GTS, STATE, RULE, MATCH);
        this.listening = true;
    }

    /** Suspend the listeners of this tree. */
    protected void suspendListeners() {
        if (!this.listening) {
            throw new IllegalStateException();
        }
        removeTreeSelectionListener(this.selectionListener);
        getSimulatorModel().removeListener(this);
        this.listening = false;
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

    /** Convenience method to retrieve the current grammar view. */
    private final GrammarModel getGrammar() {
        return getSimulatorModel().getGrammar();
    }

    /** Convenience method to retrieve the simulator model. */
    private final SimulatorModel getSimulatorModel() {
        return this.parentDisplay.getSimulatorModel();
    }

    /** Convenience method to retrieve the simulator action store. */
    private final ActionStore getActions() {
        return this.parentDisplay.getActions();
    }

    // ========================================================================
    // Methods to be overridden for specific behavior.
    // ========================================================================

    /**
     * Creates the popup menu for a given selected {@link TreeNode}.
     * If nothing is selected, the node is <code>null</code>.
     * This method may return <code>null</code> if no popup menu should be
     * created.
     */
    public JPopupMenu createPopupMenu(TreeNode node) {
        return this.parentDisplay.createListPopupMenu(node instanceof ResourceTreeNode);
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
    // LOCAL CLASS - MySelectionListener
    // ========================================================================

    /**
     * Custom selection listener, which notifies the {@link SimulatorModel}
     * of every selection change. 
     */
    private class MySelectionListener implements TreeSelectionListener {
        /** Empty constructor with the correct visibility. */
        public MySelectionListener() {
            // Empty
        }

        /**
         * Notifies the {@link SimulatorModel} every time the selection of
         * this resource tree changes.
         */
        public void valueChanged(TreeSelectionEvent evt) {
            Set<String> selected = new HashSet<String>();
            suspendListeners();
            TreePath[] paths = getSelectionPaths();
            for (int i = 0; paths != null && i < paths.length; i++) {
                TreeNode node = (TreeNode) paths[i].getLastPathComponent();
                collectResources(node, selected);
            }
            getSimulatorModel().doSelectSet(ResourceTree.this.resourceKind,
                selected);
            activateListeners();
        }

        private void collectResources(TreeNode node, Set<String> result) {
            if (node instanceof PathNode) {
                PathNode path = (PathNode) node;
                for (int i = 0; i < path.getChildCount(); i++) {
                    collectResources(path.getChildAt(i), result);
                }
            } else if (node instanceof ResourceTreeNode) {
                result.add(((ResourceTreeNode) node).getName());
            }
        }
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
            DisplayKind display =
                DisplayKind.toDisplay(ResourceTree.this.resourceKind);
            if (selected instanceof ResourceTreeNode && display != null) {
                getSimulatorModel().setDisplay(display);
            }

            // invoke editor, if this was a double click
            if (selected instanceof ResourceTreeNode && evt.getClickCount() > 1) {
                ResourceTree.this.parentDisplay.getEditAction().execute();
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
    // LOCAL CLASS - ResourceNode
    // ========================================================================

    // ========================================================================
    // LOCAL CLASS - PathNode
    // ========================================================================

    /**
     * A {@link PathNode} is a {@link DefaultMutableTreeNode} that corresponds
     * to a path in the current grammar.
     */
    public class PathNode extends SortingTreeNode {

        // The (full) name of the path.
        private final String pathName;

        /** Default constructor. */
        public PathNode(String fullName, String shortName) {
            super(shortName, true);
            this.pathName = fullName;
        }

        /** Getter for the resource name. */
        public String getPathName() {
            return this.pathName;
        }
    }

    // ========================================================================
    // LOCAL CLASS - FolderTree
    // ========================================================================

    /**
     * A {@link FolderTree} is a sorted tree of resources. Each resource is
     * split in its path components, and each components is stored as a
     * separate level in the tree.
     */
    private class FolderTree {

        // The subfolders of this tree. 
        public final TreeMap<String,FolderTree> folders;

        // The resources that are stores directly at this level.
        public final TreeSet<String> resources;

        /** Create a new (empty) FolderTree. */
        public FolderTree() {
            this.folders = new TreeMap<String,FolderTree>();
            this.resources = new TreeSet<String>();
        }

        /** Insert a new resource in the tree. */
        public void insert(String resource) {
            String[] components = resource.split("\\" + SEPARATOR);
            insert(0, components, resource);
        }

        /** Local indexes insert. */
        private void insert(int index, String[] components, String resource) {
            if (index == components.length - 1) {
                this.resources.add(components[index]);
            } else {
                FolderTree folder = this.folders.get(components[index]);
                if (folder == null) {
                    folder = new FolderTree();
                }
                folder.insert(index + 1, components, resource);
                this.folders.put(components[index], folder);
            }
        }

        /** 
         * Adds all tree resources to a DefaultMutableTreeNode (with the given
         * path). Also collects the created {@link TreeNode}s.
         */
        public void store(SortingTreeNode root, String path,
                Set<SortingTreeNode> created) {
            for (Map.Entry<String,FolderTree> entry : this.folders.entrySet()) {
                String subpath = extendPath(path, entry.getKey());
                PathNode node = new PathNode(subpath, entry.getKey());
                entry.getValue().store(node, subpath, created);
                created.add(node);
                //root.add(node);
                root.insertSorted(node);
            }
            for (String resource : this.resources) {
                String fullName = extendPath(path, resource);
                ResourceTreeNode leaf =
                    new ResourceTreeNode(ResourceTree.this.parentDisplay,
                        fullName);
                created.add(leaf);
                //root.add(leaf);
                root.insertSorted(leaf);
            }
        }

        /** Extend a path string with a new subpath. */
        private String extendPath(String path, String child) {
            if (path.equals("")) {
                return child;
            } else {
                return path + "." + child;
            }
        }

    }

}