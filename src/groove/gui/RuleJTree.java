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
import static groove.gui.SimulatorModel.Change.STATE;
import groove.explore.util.MatchSetCollector;
import groove.graph.GraphInfo;
import groove.graph.GraphProperties;
import groove.gui.SimulatorModel.Change;
import groove.gui.SimulatorPanel.TabKind;
import groove.gui.action.ActionStore;
import groove.io.HTMLConverter;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.MatchResult;
import groove.trans.Rule;
import groove.trans.RuleName;
import groove.view.GrammarView;
import groove.view.RuleView;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
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
 * Panel that displays a two-level directory of rules and matches.
 * @version $Revision$
 * @author Arend Rensink
 */
public class RuleJTree extends JTree implements SimulatorListener {
    /** Creates an instance for a given simulator. */
    protected RuleJTree(final Simulator simulator) {
        this.simulator = simulator;
        setRootVisible(false);
        setShowsRootHandles(true);
        setEnabled(false);
        setToggleClickCount(0);
        setCellRenderer(new MyTreeCellRenderer());
        getSelectionModel().setSelectionMode(
            TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        // set icons
        DefaultTreeCellRenderer renderer =
            (DefaultTreeCellRenderer) this.cellRenderer;
        renderer.setLeafIcon(Icons.GRAPH_MATCH_ICON);
        renderer.setOpenIcon(Icons.RULE_SMALL_ICON);
        renderer.setClosedIcon(Icons.RULE_SMALL_ICON);
        this.topDirectoryNode = new DefaultMutableTreeNode();
        this.ruleDirectory = new DefaultTreeModel(this.topDirectoryNode, true);
        setModel(this.ruleDirectory);
        // set key bindings
        ActionMap am = getActionMap();
        am.put(Options.UNDO_ACTION_NAME, simulator.getActions().getBackAction());
        am.put(Options.REDO_ACTION_NAME,
            simulator.getActions().getForwardAction());
        InputMap im = getInputMap();
        im.put(Options.UNDO_KEY, Options.UNDO_ACTION_NAME);
        im.put(Options.REDO_KEY, Options.REDO_ACTION_NAME);
        // add tool tips
        installListeners();
        ToolTipManager.sharedInstance().registerComponent(this);
    }

    /** Clears all maps of the tree. */
    private void clearAllMaps() {
        this.ruleNodeMap.clear();
        this.clearMatchMaps();
    }

    /** Clears the match maps of the tree. */
    protected void clearMatchMaps() {
        this.matchNodeMap.clear();
    }

    /**
     * Loads the j-tree with the data of the given (non-<code>null</code>)
     * grammar.
     */
    private void loadGrammar(GrammarView grammar) {
        setShowAnchorsOptionListener();
        Map<RuleName,DirectoryTreeNode> dirNodeMap =
            new HashMap<RuleName,DirectoryTreeNode>();
        this.clearAllMaps();
        this.topDirectoryNode.removeAllChildren();
        DefaultMutableTreeNode topNode = this.topDirectoryNode;
        Map<Integer,Set<RuleView>> priorityMap = getPriorityMap(grammar);
        for (Map.Entry<Integer,Set<RuleView>> priorityEntry : priorityMap.entrySet()) {
            // if the rule system has multiple priorities, we want an extra
            // level of nodes
            if (priorityMap.size() > 1) {
                topNode = new PriorityTreeNode(priorityEntry.getKey());
                this.topDirectoryNode.add(topNode);
                dirNodeMap.clear();
            }
            for (RuleView ruleView : priorityEntry.getValue()) {
                String ruleName = ruleView.getName();
                // recursively add parent directory nodes as required
                DefaultMutableTreeNode parentNode =
                    addParentNode(topNode, dirNodeMap, new RuleName(ruleName));
                // create the rule node and register it
                RuleTreeNode ruleNode = new RuleTreeNode(ruleView);
                parentNode.add(ruleNode);
                expandPath(new TreePath(ruleNode.getPath()));
                this.ruleNodeMap.put(ruleView, ruleNode);
            }
        }
        this.ruleDirectory.reload(this.topDirectoryNode);
    }

    /**
     * Creates a map from priorities to nonempty sets of rules with that
     * priority from the rule in a given grammar view.
     * @param grammar the source of the rule map
     */
    private Map<Integer,Set<RuleView>> getPriorityMap(GrammarView grammar) {
        Map<Integer,Set<RuleView>> result =
            new TreeMap<Integer,Set<RuleView>>(Rule.PRIORITY_COMPARATOR);
        for (String ruleName : grammar.getRuleNames()) {
            RuleView ruleView = grammar.getRuleView(ruleName);
            int priority = ruleView.getPriority();
            Set<RuleView> priorityRules = result.get(priority);
            if (priorityRules == null) {
                result.put(priority, priorityRules = new TreeSet<RuleView>());
            }
            priorityRules.add(ruleView);
        }
        return result;
    }

    /** Unhooks this object from all observables. */
    public void dispose() {
        getSimulatorModel().removeListener(this);
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel,
            Set<Change> changes) {
        suspendListeners();
        if (changes.contains(GRAMMAR)) {
            GrammarView grammar = source.getGrammar();
            if (grammar == null) {
                this.clearAllMaps();
                this.topDirectoryNode.removeAllChildren();
                this.ruleDirectory.reload();
            } else if (grammar != oldModel.getGrammar()
                || grammar.getRuleNames().size() != this.ruleNodeMap.size()) {
                loadGrammar(grammar);
            } else {
                // compare the individual rule views
                for (RuleView ruleView : this.ruleNodeMap.keySet()) {
                    if (!ruleView.equals(grammar.getRuleView(ruleView.getName()))) {
                        loadGrammar(grammar);
                        break;
                    }
                }
            }
            refresh(source.getState());
        } else {
            if (changes.contains(GTS) || changes.contains(STATE)) {
                // if the GTS has changed, this may mean that the state 
                // displayed here has been closed, in which case we have to refresh
                // since the rule events have been changed into transitions
                refresh(source.getState());
            }
            if (changes.contains(MATCH)) {
                selectMatch(source.getRule(), source.getMatch());
            }
        }
        activateListeners();
    }

    private void installListeners() {
        getSimulatorModel().addListener(this, GRAMMAR, GTS, STATE, MATCH);
        addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent e) {
                RuleJTree.this.repaint();
            }

            @Override
            public void focusGained(FocusEvent e) {
                RuleJTree.this.repaint();
            }
        });
        addMouseListener(new MyMouseListener());
        activateListeners();
    }

    private void activateListeners() {
        if (this.listening) {
            throw new IllegalStateException();
        }
        addTreeSelectionListener(createRuleSelectionListener());
        this.listening = true;
    }

    private void suspendListeners() {
        if (!this.listening) {
            throw new IllegalStateException();
        }
        removeTreeSelectionListener(createRuleSelectionListener());
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

    /** Returns the list of currently selected rule names. */
    public List<RuleView> getSelectedRules() {
        List<RuleView> result = new ArrayList<RuleView>();
        int[] selectedRows = getSelectionRows();
        if (selectedRows != null) {
            for (int selectedRow : selectedRows) {
                Object[] nodes = getPathForRow(selectedRow).getPath();
                for (int i = nodes.length - 1; i >= 0; i--) {
                    if (nodes[i] instanceof RuleTreeNode) {
                        result.add(((RuleTreeNode) nodes[i]).getRule());
                        break;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Sets a listener to the anchor image option, if that has not yet been
     * done.
     */
    private void setShowAnchorsOptionListener() {
        if (!this.anchorImageOptionListenerSet) {
            JMenuItem showAnchorsOptionItem =
                getSimulator().getOptions().getItem(Options.SHOW_ANCHORS_OPTION);
            if (showAnchorsOptionItem != null) {
                // listen to the option controlling the rule anchor display
                showAnchorsOptionItem.addItemListener(new ItemListener() {
                    public void itemStateChanged(ItemEvent e) {
                        suspendListeners();
                        refresh(getSimulatorModel().getState());
                        activateListeners();
                    }
                });
                this.anchorImageOptionListenerSet = true;
            }
        }
    }

    /** Adds tree nodes for all levels of a structured rule name. */
    private DefaultMutableTreeNode addParentNode(
            DefaultMutableTreeNode topNode,
            Map<RuleName,DirectoryTreeNode> dirNodeMap, RuleName ruleName) {
        RuleName parent = ruleName.parent();
        if (parent == null) {
            // there is no parent rule name; the parent node is the top node
            return topNode;
        } else {
            // there is a proper parent rule; look it up in the node map
            DirectoryTreeNode result = dirNodeMap.get(parent);
            if (result == null) {
                // the parent node did not yet exist in the tree
                // check recursively for the grandparent
                DefaultMutableTreeNode grandParentNode =
                    addParentNode(topNode, dirNodeMap, parent);
                // make the parent node and register it
                result = new DirectoryTreeNode(parent);
                grandParentNode.add(result);
                dirNodeMap.put(parent, result);
            }
            return result;
        }
    }

    /**
     * Refreshes the selection in the tree, based on the current state of the
     * Simulator.
     */
    private void refresh(GraphState state) {
        Collection<? extends MatchResult> matches;
        if (state == null) {
            matches = Collections.<MatchResult>emptySet();
        } else if (state.isClosed()) {
            matches = state.getTransitionSet();
        } else {
            matches =
                new MatchSetCollector(state, getGTS().getRecord(),
                    getGTS().checkDiamonds()).getMatchSet();
        }
        refreshMatches(matches);
        setEnabled(getGrammar() != null);
    }

    /** 
     * Selects the row of a given match result, or if that is {@code null}, a
     * given rule.
     * @param rule the rule to be selected if the event is {@code null}
     * @param match the match result to be selected
     */
    private void selectMatch(RuleView rule, MatchResult match) {
        DefaultMutableTreeNode treeNode = null;
        if (match != null) {
            treeNode = this.matchNodeMap.get(match);
        } else if (rule != null) {
            treeNode = this.ruleNodeMap.get(rule);
        }
        if (treeNode != null) {
            setSelectionPath(new TreePath(treeNode.getPath()));
        }
    }

    /**
     * Refreshes the match nodes, based on a given match result set.
     * @param matches the set of matches used to create {@link MatchTreeNode}s
     */
    private void refreshMatches(Collection<? extends MatchResult> matches) {
        // remove current matches
        for (MatchTreeNode matchNode : this.matchNodeMap.values()) {
            this.ruleDirectory.removeNodeFromParent(matchNode);
        }
        // clean up current match node map
        this.clearMatchMaps();
        // expand all rule nodes and subsequently collapse all directory nodes
        for (DefaultMutableTreeNode nextNode : this.ruleNodeMap.values()) {
            if (!(nextNode instanceof DirectoryTreeNode)) {
                expandPath(new TreePath(nextNode.getPath()));
            }
        }
        for (DefaultMutableTreeNode nextNode : this.ruleNodeMap.values()) {
            if (nextNode instanceof DirectoryTreeNode) {
                collapsePath(new TreePath(nextNode.getPath()));
            }
        }
        // recollect the match results so that they are ordered according to the
        // rule events
        SortedSet<MatchResult> orderedEvents =
            new TreeSet<MatchResult>(new Comparator<MatchResult>() {
                @Override
                public int compare(MatchResult o1, MatchResult o2) {
                    return o1.getEvent().compareTo(o2.getEvent());
                }

            });
        orderedEvents.addAll(matches);
        // insert new matches
        for (MatchResult match : orderedEvents) {
            String ruleName = match.getEvent().getRule().getName();
            RuleView ruleView = getGrammar().getRuleView(ruleName);
            assert ruleView != null : String.format(
                "Rule view %s does not exist in grammar", ruleView);
            RuleTreeNode ruleNode = this.ruleNodeMap.get(ruleView);
            assert ruleNode != null : String.format(
                "Rule %s has no image in map %s", ruleName, this.ruleNodeMap);
            int nrOfMatches = ruleNode.getChildCount();
            MatchTreeNode matchNode = new MatchTreeNode(nrOfMatches + 1, match);
            this.ruleDirectory.insertNodeInto(matchNode, ruleNode, nrOfMatches);
            expandPath(new TreePath(ruleNode.getPath()));
            this.matchNodeMap.put(match, matchNode);
        }

    }

    /**
     * Returns the listener used to propagate changes in the tree selection.
     */
    private TreeSelectionListener createRuleSelectionListener() {
        if (this.ruleSelectionListener == null) {
            this.ruleSelectionListener = new RuleSelectionListener();
        }
        return this.ruleSelectionListener;
    }

    /**
     * Creates a popup menu for this panel.
     * @param node the node for which the menu is created
     */
    private JPopupMenu createPopupMenu(TreeNode node) {
        JPopupMenu res = new JPopupMenu();
        res.setFocusable(false);
        res.add(getActions().getNewRuleAction());
        if (node instanceof RuleTreeNode) {
            res.add(getActions().getEditRuleAction());
            res.addSeparator();
            res.add(getActions().getCopyRuleAction());
            res.add(getActions().getDeleteRuleAction());
            res.add(getActions().getRenameRuleAction());
            res.addSeparator();
            res.add(getActions().getEnableRuleAction());
            res.add(getActions().getEditRulePropertiesAction());
        } else if (node instanceof MatchTreeNode) {
            res.addSeparator();
            res.add(getActions().getApplyTransitionAction());
        }
        return res;
    }

    /** Convenience method to retrieve the current grammar view. */
    private final GrammarView getGrammar() {
        return getSimulatorModel().getGrammar();
    }

    /** Convenience method to retrieve the current GTS from the simulator. */
    private final GTS getGTS() {
        return getSimulatorModel().getGts();
    }

    /** Returns the associated simulator. */
    private final Simulator getSimulator() {
        return this.simulator;
    }

    /** Convenience method to retrieve the simulator model. */
    private final SimulatorModel getSimulatorModel() {
        return this.simulator.getModel();
    }

    /** Convenience method to retrieve the simulator panel. */
    private final SimulatorPanel getSimulatorPanel() {
        return this.simulator.getSimulatorPanel();
    }

    /** Convenience method to retrieve the simulator action store. */
    private final ActionStore getActions() {
        return this.simulator.getActions();
    }

    /**
     * The simulator to which this directory belongs.
     * @invariant simulator != null
     */
    private final Simulator simulator;

    private boolean listening;
    private RuleSelectionListener ruleSelectionListener;

    /**
     * Directory of production rules and their matchings to the current state.
     * Alias to the underlying model of this <tt>JTree</tt>.
     * 
     * @invariant <tt>ruleDirectory == getModel()</tt>
     */
    private final DefaultTreeModel ruleDirectory;
    /**
     * Alias for the top node in <tt>ruleDirectory</tt>.
     * @invariant <tt>topDirectoryNode == ruleDirectory.getRoot()</tt>
     */
    private final DefaultMutableTreeNode topDirectoryNode;
    /**
     * Mapping from rule names in the current grammar to rule nodes in the
     * current rule directory.
     */
    private final Map<RuleView,RuleTreeNode> ruleNodeMap =
        new HashMap<RuleView,RuleTreeNode>();

    /**
     * Mapping from RuleMatches in the current LTS to match nodes in the rule
     * directory
     */
    private final Map<MatchResult,MatchTreeNode> matchNodeMap =
        new HashMap<MatchResult,MatchTreeNode>();

    /**
     * The background colour of this component when it is enabled.
     */
    private Color enabledBackground;
    /** Flag to indicate that the anchor image option listener has been set. */
    private boolean anchorImageOptionListenerSet = false;

    /**
     * Transforms a given rule name into the string that shows this rule is
     * disabled. This implementation puts brackets around the rule name.
     * @param name The rule name; non-<code>null</code>
     * @return a string constructed from <code>name</code> that shows the rule
     *         to be disabled
     */
    static public String showDisabled(String name) {
        return "(" + name + ")";
    }

    /** The background colour of the tree when enabled. */
    public static final Color TREE_ENABLED_COLOR = Color.WHITE;
    /** The background colour of a selected cell if the list does not have focus. */
    static private final Color SELECTION_NON_FOCUS_COLOR = Color.LIGHT_GRAY;

    /**
     * Selection listener that invokes <tt>setRule</tt> if a rule node is
     * selected, and <tt>setDerivation</tt> if a match node is selected.
     * @see SimulatorModel#setRule
     * @see SimulatorModel#setMatch
     */
    private class RuleSelectionListener implements TreeSelectionListener {
        /**
         * Empty constructor with the correct visibility.
         */
        public RuleSelectionListener() {
            // Empty
        }

        /**
         * Triggers a rule or match selection update by the simulator
         * based on the current selection in the tree.
         */
        public void valueChanged(TreeSelectionEvent evt) {
            TreePath[] paths = getSelectionPaths();
            Set<RuleView> selectedRules = new HashSet<RuleView>();
            boolean matchSelected = false;
            for (int i = 0; paths != null && i < paths.length; i++) {
                Object selectedNode = paths[i].getLastPathComponent();
                RuleTreeNode ruleNode = null;
                if (selectedNode instanceof RuleTreeNode) {
                    // selected tree node is a production rule (level 1
                    // node)
                    ruleNode = (RuleTreeNode) selectedNode;
                } else if (!matchSelected
                    && selectedNode instanceof MatchTreeNode) {
                    ruleNode =
                        (RuleTreeNode) paths[i].getParentPath().getLastPathComponent();
                    matchSelected = true;
                    // selected tree node is a match (level 2 node)
                    MatchResult result =
                        ((MatchTreeNode) selectedNode).getResult();
                    getSimulatorModel().setMatch(result);
                }
                if (ruleNode != null) {
                    selectedRules.add(ruleNode.getRule());
                }
            }
            getSimulatorModel().setRuleSet(selectedRules);
            if (evt.isAddedPath() && paths.length == 1) {
                TabKind newTab;
                if (evt.getPath().getLastPathComponent() instanceof RuleTreeNode) {
                    newTab = TabKind.RULE;
                } else if (getSimulatorPanel().getSelectedTab() != TabKind.LTS) {
                    newTab = TabKind.GRAPH;
                } else {
                    newTab = TabKind.LTS;
                }
                getSimulatorModel().setTabKind(newTab);
            }
        }
    }

    /**
     * Mouse listener that creates the popup menu and switches the view to the
     * rule panel on double-clicks.
     */
    private class MyMouseListener extends MouseAdapter {
        /**
         * Empty constructor with the correct visibility.
         */
        public MyMouseListener() {
            // empty
        }

        @Override
        public void mousePressed(MouseEvent evt) {
            if (evt.getButton() == MouseEvent.BUTTON3) {
                TreePath selectedPath =
                    getPathForLocation(evt.getX(), evt.getY());
                if (selectedPath != null) {
                    TreePath[] paths = getSelectionPaths();
                    boolean pathIsSelected = false;
                    for (int i = 0; paths != null && i < paths.length; i++) {
                        if (selectedPath == paths[i]) {
                            pathIsSelected = true;
                        }
                    }
                    if (pathIsSelected == false) {
                        setSelectionPath(selectedPath);
                    }
                }
            }
            maybeShowPopup(evt);
        }

        @Override
        public void mouseReleased(MouseEvent evt) {
            maybeShowPopup(evt);
        }

        @Override
        public void mouseClicked(MouseEvent evt) {
            if (evt.getButton() != MouseEvent.BUTTON1) {
                return;
            }
            TreePath path = getSelectionPath();
            if (path == null) {
                return;
            }
            Object selectedNode = path.getLastPathComponent();
            if (selectedNode instanceof MatchTreeNode) {
                if (evt.getClickCount() == 2) {
                    getSimulatorModel().applyMatch();
                }
            }
        }

        private void maybeShowPopup(MouseEvent evt) {
            if (evt.isPopupTrigger()) {
                TreePath selectedPath =
                    getPathForLocation(evt.getX(), evt.getY());
                TreeNode selectedNode =
                    selectedPath == null ? null
                            : (TreeNode) selectedPath.getLastPathComponent();
                RuleJTree.this.requestFocus();
                createPopupMenu(selectedNode).show(evt.getComponent(),
                    evt.getX(), evt.getY());
            }
        }
    }

    /**
     * Priority nodes (used only if the rule system has multiple priorities)
     */
    private static class PriorityTreeNode extends DefaultMutableTreeNode {
        /**
         * Creates a new priority node based on a given priority. The node can
         * (and will) have children.
         */
        public PriorityTreeNode(int priority) {
            super("Priority " + priority, true);
        }
    }

    /**
     * Rule nodes (= level 1 nodes) of the directory
     */
    public static class RuleTreeNode extends DefaultMutableTreeNode {
        /**
         * Creates a new rule node based on a given rule name. The node can have
         * children.
         */
        public RuleTreeNode(RuleView rule) {
            super(rule, true);
        }

        /**
         * Convenience method to retrieve the user object as a rule name.
         */
        public RuleView getRule() {
            return (RuleView) getUserObject();
        }

        /**
         * To display, show child name only. Also visualise enabledness.
         * @see RuleJTree#showDisabled(String)
         */
        @Override
        public String toString() {
            String name = new RuleName(getRule().getName()).child();
            if (getRule().isEnabled()) {
                return name;
            } else {
                return showDisabled(name);
            }
        }

        /** Returns HTML-formatted tool tip text for this rule node. */
        public String getToolTipText() {
            StringBuilder result = new StringBuilder();
            result.append("Rule ");
            result.append(HTMLConverter.STRONG_TAG.on(getRule().getName()));
            GraphProperties properties =
                GraphInfo.getProperties(getRule().getAspectGraph(), false);
            if (properties != null && !properties.isEmpty()) {
                boolean hasProperties;
                String remark = properties.getRemark();
                if (remark != null) {
                    result.append(": ");
                    result.append(HTMLConverter.toHtml(remark));
                    hasProperties = properties.size() > 1;
                } else {
                    hasProperties = true;
                }
                if (hasProperties) {
                    for (String key : properties.getPropertyKeys()) {
                        if (!GraphProperties.isSystemKey(key)
                            && !key.equals(GraphProperties.REMARK_KEY)) {
                            result.append(HTMLConverter.HTML_LINEBREAK);
                            result.append(propertyToString(key,
                                properties.getProperty(key)));
                        }
                    }
                }
            }
            HTMLConverter.HTML_TAG.on(result);
            return result.toString();
        }

        /** Returns an HTML-formatted string for a given key/value-pair. */
        private String propertyToString(String key, String value) {
            return "<b>" + key + "</b> = " + value;
        }
    }

    /**
     * Directory nodes (= level 0 nodes) of the directory
     */
    private static class DirectoryTreeNode extends DefaultMutableTreeNode {
        /**
         * Creates a new rule node based on a given rule name. The node can have
         * children.
         */
        public DirectoryTreeNode(RuleName name) {
            super(name, true);
        }

        /**
         * Convenience method to retrieve the user object as a rule name.
         */
        public RuleName name() {
            return (RuleName) getUserObject();
        }

        /**
         * To display, show child name only
         */
        @Override
        public String toString() {
            return name().child();
        }
    }

    /**
     * Match nodes (= level 2 nodes) of the directory. Stores a
     * <tt>Transition</tt> as user object.
     */
    private class MatchTreeNode extends DefaultMutableTreeNode {
        /**
         * Creates a new match node on the basis of a given number and the
         * RuleMatch. The node cannot have children.
         */
        public MatchTreeNode(int nr, MatchResult result) {
            super(result, false);
            this.nr = nr;
        }

        /**
         * Convenience method to return the underlying derivation edge.
         */
        public MatchResult getResult() {
            return (MatchResult) getUserObject();
        }

        /**
         * Object identity is good enough as a notion of equality.
         */
        @Override
        public boolean equals(Object obj) {
            return this == obj;
        }

        /**
         * A description of this derivation edge in the rule directory. Returns
         * <tt>"Match ??</tt>, where <tt>??</tt> is the node number.
         */
        @Override
        public String toString() {
            return getSimulator().getOptions().isSelected(
                Options.SHOW_ANCHORS_OPTION)
                    ? getResult().getEvent().getAnchorImageString() : "Match "
                        + this.nr;
        }

        /** The number of this match, used in <tt>toString()</tt> */
        private final int nr;
    }

    /**
     * Class to provide proper icons for directory nodes
     */
    private class MyTreeCellRenderer extends DefaultTreeCellRenderer {

        /** The background colour of an enabled component. */
        private final Color ENABLED_COLOUR;

        /**
         * Empty constructor with the correct visibility.
         */
        public MyTreeCellRenderer() {
            JTextField enabledField = new JTextField();
            enabledField.setEditable(true);
            this.ENABLED_COLOUR = enabledField.getBackground();
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean sel, boolean expanded, boolean leaf, int row,
                boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded,
                leaf, row, hasFocus);

            if (value instanceof DirectoryTreeNode) {
                setIcon(Icons.GPS_FOLDER_ICON);
            } else if (value instanceof PriorityTreeNode) {
                setIcon(null);
            }
            if (value instanceof RuleTreeNode) {
                setToolTipText(((RuleTreeNode) value).getToolTipText());
            } else {
                setToolTipText(null);
            }
            setOpaque(!sel);
            setBackground(this.ENABLED_COLOUR);
            return this;
        }

        @Override
        public Color getBackgroundSelectionColor() {
            Color result;
            if (RuleJTree.this.isFocusOwner()) {
                result = super.getBackgroundSelectionColor();
            } else {
                result = SELECTION_NON_FOCUS_COLOR;
                if (getGTS() == null) {
                    result = result.darker();
                }
            }
            return result;
        }

        @Override
        public Color getTextSelectionColor() {
            if (RuleJTree.this.isFocusOwner() || getGTS() == null) {
                return super.getTextSelectionColor();
            } else {
                return Color.BLACK;
            }
        }

    }
}
