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
import groove.gui.jgraph.JAttr;
import groove.io.HTMLConverter;
import groove.lts.GraphState;
import groove.trans.ResourceKind;
import groove.view.GrammarModel;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
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
    private final DefaultMutableTreeNode root;

    // The kind of resources that are displayed.
    private final ResourceKind resourceKind;

    // The listeners.
    private final TreeSelectionListener selectionListener;
    private boolean listening;

    // Remembers the previous enabled background color.
    private Color enabledBackground;

    // Used separator character.
    private static final char SEPARATOR_CHAR = '.';
    private static final String SEPARATOR = ".";

    /** Creates an instance for a given simulator. */
    public ResourceTree(ResourceDisplay parent, ResourceKind kind) {

        // store parent display, resource kind and leaf icon
        this.parentDisplay = parent;
        this.resourceKind = kind;
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
        MyTreeCellRenderer renderer = new MyTreeCellRenderer();
        renderer.setLeafIcon(Icons.getListIcon(kind));
        setCellRenderer(renderer);
        getSelectionModel().setSelectionMode(
            TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

        // initialize tree
        this.root = new DefaultMutableTreeNode();
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
     */
    private void loadGrammar(GrammarModel grammar) {

        // get all resource names
        Set<String> rawNames = new TreeSet<String>(new LeveledComparator());
        rawNames.addAll(grammar.getNames(this.resourceKind));

        // add each resource, one by one
        for (String name : rawNames) {
            String[] components = name.split("\\" + SEPARATOR);

            // recursively add this resource
            DefaultMutableTreeNode parent = this.root;
            for (int i = 0; i < components.length; i++) {
                DefaultMutableTreeNode lastChild = null;
                if (parent.getChildCount() > 0) {
                    lastChild = (DefaultMutableTreeNode) parent.getLastChild();
                }
                if (lastChild != null && lastChild instanceof PathNode
                    && ((PathNode) lastChild).getPath().equals(components[i])) {
                    // path node already exists, continue
                    parent = lastChild;
                } else {
                    if (i < components.length - 1) {
                        // create new path node
                        PathNode newChild = new PathNode(components[i]);
                        parent.add(newChild);
                        parent = newChild;
                    } else {
                        // create new resource (leaf) node
                        ResourceNode leaf =
                            new ResourceNode(name, components[i]);
                        parent.add(leaf);
                    }
                }
            }
        }
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel,
            Set<Change> changes) {
        suspendListeners();
        if (changes.contains(GRAMMAR)) {
            this.root.removeAllChildren();
            GrammarModel grammar = source.getGrammar();
            if (grammar != null) {
                loadGrammar(grammar);
            }
            this.tree.reload(this.root);
            refresh(source.getState());
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
     * Creates the text to be displayed for a given resource.
     */
    public String getDisplayText(String resourceName) {
        return resourceName;
    }

    /**
     * Creates the tool tip text for a given resource.
     */
    public String getToolTip(String resourceName) {
        StringBuilder result = new StringBuilder();
        switch (this.resourceKind) {
        case HOST:
            result.append("Host graph ");
            break;
        case RULE:
            result.append("Rule ");
            break;
        case TYPE:
            result.append("Type graph ");
            break;
        }
        result.append(HTMLConverter.STRONG_TAG.on(resourceName));
        HTMLConverter.HTML_TAG.on(result);
        return result.toString();
    }

    /**
     * Creates the popup menu for a given selected {@link TreeNode}.
     * If nothing is selected, the node is <code>null</code>.
     * This method may return <code>null</code> if no popup menu should be
     * created.
     */
    public JPopupMenu createPopupMenu(TreeNode node) {
        return this.parentDisplay.createListPopupMenu(node instanceof ResourceNode);
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
    // LOCAL CLASS - MyTreeCellRenderer
    // ========================================================================

    /**
     * Custom cell renderer, which properly sets fore- and background colours
     * (based on focus, and possibly errors)
     */
    private class MyTreeCellRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean sel, boolean expanded, boolean leaf, int row,
                boolean hasFocus) {
            boolean cellSelected = sel || hasFocus;
            boolean cellFocused =
                cellSelected && ResourceTree.this.isFocusOwner();
            super.getTreeCellRendererComponent(tree, value, cellSelected,
                expanded, leaf, row, false);

            boolean error = false;
            if (value instanceof ResourceNode) {
                String resourceName = ((ResourceNode) value).getResourceName();
                setToolTipText(getToolTip(resourceName));
                setIcon(ResourceTree.this.parentDisplay.getListIcon(resourceName));
                error = ResourceTree.this.parentDisplay.hasError(resourceName);
            }
            setForeground(JAttr.getForeground(cellSelected, cellFocused, error));
            Color background =
                JAttr.getBackground(cellSelected, cellFocused, error);
            if (cellSelected) {
                setBackgroundSelectionColor(background);
            } else {
                setBackgroundNonSelectionColor(background);
            }
            setOpaque(false);
            return this;
        }
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
                Object selectedNode = paths[i].getLastPathComponent();
                if (selectedNode instanceof ResourceNode) {
                    selected.add(((ResourceNode) selectedNode).getResourceName());
                }
            }
            getSimulatorModel().doSelectSet(ResourceTree.this.resourceKind,
                selected);
            activateListeners();
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
            if (selected instanceof ResourceNode && display != null) {
                getSimulatorModel().setDisplay(display);
            }

            // invoke editor, if this was a double click
            if (selected instanceof ResourceNode && evt.getClickCount() > 1) {
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

    /**
     * A {@link ResourceNode} is a {@link DefaultMutableTreeNode} that
     * corresponds to a resource.
     */
    public class ResourceNode extends DefaultMutableTreeNode {

        // The (full) name of the resource.
        private final String resourceName;

        /** Default constructor. */
        public ResourceNode(String resourceName, String lastComponent) {
            super(getDisplayText(lastComponent), false);
            this.resourceName = resourceName;
        }

        /** Getter for the resource name. */
        public String getResourceName() {
            return this.resourceName;
        }
    }

    // ========================================================================
    // LOCAL CLASS - PathNode
    // ========================================================================

    /**
     * A {@link PathNode} is a {@link DefaultMutableTreeNode} that corresponds
     * to a path in the current grammar.
     */
    public class PathNode extends DefaultMutableTreeNode {

        // The (local) name of the path.
        private final String pathName;

        /** Default constructor. */
        public PathNode(String pathName) {
            super(pathName, true);
            this.pathName = pathName;
        }

        /** Getter for the resource name. */
        public String getPathName() {
            return this.pathName;
        }
    }

    // ========================================================================
    // LOCAL CLASS - LeveledComparator
    // ========================================================================

    /**
     * Customized comparator, which first looks at the number of separators,
     * and then at the texts themselves. Ensures that paths are always smaller
     * than files.
     */
    private class LeveledComparator implements Comparator<String> {

        @Override
        public int compare(String o1, String o2) {
            int c1 = countSeparators(o1);
            int c2 = countSeparators(o2);
            if (c1 < c2) {
                return 1;
            }
            if (c1 > c2) {
                return -1;
            }
            return o1.compareTo(o2);
        }

        private int countSeparators(String name) {
            char[] chars = name.toCharArray();
            int count = 0;
            for (int i = 0; i < chars.length; i++) {
                if (chars[i] == SEPARATOR_CHAR) {
                    count++;
                }
            }
            return count;
        }
    }
}