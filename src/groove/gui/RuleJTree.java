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

import groove.explore.util.MatchSetCollector;
import groove.graph.GraphInfo;
import groove.graph.GraphProperties;
import groove.graph.Label;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.Rule;
import groove.trans.RuleEvent;
import groove.trans.RuleMatch;
import groove.trans.RuleName;
import groove.trans.SystemRecord;
import groove.util.Converter;
import groove.util.Groove;
import groove.view.GrammarView;
import groove.view.RuleView;
import groove.view.StoredGrammarView;

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
import java.util.Iterator;
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
public class RuleJTree extends JTree implements SimulationListener {
    /** Creates an instance for a given simulator. */
    protected RuleJTree(final Simulator simulator) {
        this.simulator = simulator;
        simulator.addSimulationListener(this);
        setRootVisible(false);
        setShowsRootHandles(true);
        setEnabled(false);
        setToggleClickCount(0);
        setCellRenderer(new MyTreeCellRenderer());
        // mzimakova - Multiple selection
        getSelectionModel().setSelectionMode(
            TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        // set icons
        DefaultTreeCellRenderer renderer =
            (DefaultTreeCellRenderer) this.cellRenderer;
        renderer.setLeafIcon(Groove.GRAPH_MATCH_ICON);
        renderer.setOpenIcon(Groove.RULE_SMALL_ICON);
        renderer.setClosedIcon(Groove.RULE_SMALL_ICON);
        addTreeSelectionListener(createRuleSelectionListener());
        this.listenToSelectionChanges = true;
        addMouseListener(new MyMouseListener());
        this.topDirectoryNode = new DefaultMutableTreeNode();
        this.ruleDirectory = new DefaultTreeModel(this.topDirectoryNode, true);
        setModel(this.ruleDirectory);
        // set key bindings
        ActionMap am = getActionMap();
        am.put(Options.UNDO_ACTION_NAME, simulator.getBackAction());
        am.put(Options.REDO_ACTION_NAME, simulator.getForwardAction());
        InputMap im = getInputMap();
        im.put(Options.UNDO_KEY, Options.UNDO_ACTION_NAME);
        im.put(Options.REDO_KEY, Options.REDO_ACTION_NAME);
        // add tool tips
        ToolTipManager.sharedInstance().registerComponent(this);
        addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent e) {
                RuleJTree.this.repaint();
            }

            @Override
            public void focusGained(FocusEvent e) {
                RuleJTree.this.repaint();
                TreePath[] paths = getSelectionPaths();
                if (paths != null && paths.length == 1) {
                    Object selectedNode = paths[0].getLastPathComponent();
                    if (selectedNode instanceof RuleTreeNode) {
                        switchSimulatorToRulePanel();
                    }
                }
            }
        });
    }

    /**
     * Fills the rule directory with rule nodes, based on a given rule system.
     * Sets the current LTS to the grammar's LTS.
     */
    public synchronized void setGrammarUpdate(StoredGrammarView grammar) {
        this.displayedGrammar = grammar;
        if (grammar == null) {
            this.ruleNodeMap.clear();
            this.matchNodeMap.clear();
            this.matchTransitionMap.clear();
            this.topDirectoryNode.removeAllChildren();
            this.ruleDirectory.reload();
        } else {
            loadGrammar(grammar);
        }
        refresh();
    }

    /**
     * Loads the j-tree with the data of the given (non-<code>null</code>)
     * grammar.
     */
    private void loadGrammar(GrammarView grammar) {
        boolean oldListenToSelectionChanges = this.listenToSelectionChanges;
        this.listenToSelectionChanges = false;
        setShowAnchorsOptionListener();
        Map<RuleName,DirectoryTreeNode> dirNodeMap =
            new HashMap<RuleName,DirectoryTreeNode>();
        this.ruleNodeMap.clear();
        this.matchNodeMap.clear();
        this.matchTransitionMap.clear();
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
                RuleName ruleName = ruleView.getRuleName();
                // recursively add parent directory nodes as required
                DefaultMutableTreeNode parentNode =
                    addParentNode(topNode, dirNodeMap, ruleName);
                // create the rule node and register it
                RuleTreeNode ruleNode = new RuleTreeNode(ruleView);
                parentNode.add(ruleNode);
                expandPath(new TreePath(ruleNode.getPath()));
                this.ruleNodeMap.put(ruleName, ruleNode);
            }
        }
        this.ruleDirectory.reload(this.topDirectoryNode);
        this.listenToSelectionChanges = oldListenToSelectionChanges;
    }

    /**
     * Creates a map from priorities to nonempty sets of rules with that
     * priority from the rule in a given grammar view.
     * @param grammar the source of the rule map
     */
    private Map<Integer,Set<RuleView>> getPriorityMap(GrammarView grammar) {
        Map<Integer,Set<RuleView>> result =
            new TreeMap<Integer,Set<RuleView>>(Rule.PRIORITY_COMPARATOR);
        for (RuleName ruleName : grammar.getRuleNames()) {
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

    /** Refreshes the view, to add match nodes. */
    public synchronized void startSimulationUpdate(GTS gts) {
        refresh();
    }

    /**
     * Refreshes the available match nodes, based on a new state. The current
     * LTS is inspected to find out the relevant derivations. Expands all rule
     * nodes to show the available matches.
     */
    public synchronized void setStateUpdate(GraphState state) {
        refresh();
    }

    /**
     * Sets the tree selection to a given rule name. Does <i>not</i> trigger
     * actions based on the selection change.
     */
    public synchronized void setRuleUpdate(RuleName name) {
        refresh();
    }

    /**
     * Sets the tree selection to a given derivation. Does <i>not</i> trigger
     * actions based on the selection change.
     */
    public synchronized void setTransitionUpdate(GraphTransition transition) {
        refresh();
    }

    /**
     * Sets the tree selection to a given derivation. Does <i>not</i> trigger
     * actions based on the selection change.
     */
    public void setMatchUpdate(RuleMatch match) {
        refresh();
    }

    /**
     * Sets the directory tree as in <tt>setStateUpdate</tt> for the currently
     * selected derivation's cod state.
     */
    public synchronized void applyTransitionUpdate(GraphTransition transition) {
        refresh();
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
    protected void setShowAnchorsOptionListener() {
        if (!this.anchorImageOptionListenerSet) {
            JMenuItem showAnchorsOptionItem =
                getSimulator().getOptions().getItem(Options.SHOW_ANCHORS_OPTION);
            if (showAnchorsOptionItem != null) {
                // listen to the option controlling the rule anchor display
                showAnchorsOptionItem.addItemListener(new ItemListener() {
                    public void itemStateChanged(ItemEvent e) {
                        refresh();
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
    private void refresh() {
        boolean oldListenToSelectionChanges = this.listenToSelectionChanges;
        this.listenToSelectionChanges = false;
        GraphState state = getCurrentState();
        if (state == null) {
            refreshMatchesClosed(Collections.<GraphTransition>emptySet());
        } else if (setDisplayedState(state)) {
            if (state.isClosed()) {
                refreshMatchesClosed(getCurrentGTS().outEdgeSet(
                    getCurrentState()));
            } else {
                SystemRecord record = getCurrentGTS().getRecord();
                Collection<RuleEvent> matches =
                    new MatchSetCollector(state,
                        record.freshCache(state), record).getMatchSet();
                refreshMatchesOpen(matches);
            }
        }
        DefaultMutableTreeNode treeNode = null;
        if (getCurrentTransition() != null) {
            treeNode = this.matchNodeMap.get(getCurrentTransition().getEvent());
        } else if (getCurrentEvent() != null) {
            treeNode = this.matchNodeMap.get(getCurrentEvent());
        } else if (getCurrentRule() != null) {
            treeNode = this.ruleNodeMap.get(getCurrentRule().getRuleName());
        }
        if (treeNode != null) {
            setSelectionPath(new TreePath(treeNode.getPath()));
        }
        setEnabled(this.displayedGrammar != null);
        setBackground(getCurrentGTS() == null ? null : TREE_ENABLED_COLOR);
        this.listenToSelectionChanges = oldListenToSelectionChanges;
    }

    /**
     * Refreshes the match nodes, based on a given derivation edge set.
     * @param matches the set of derivation edges used to create match nodes
     */
    private void refreshMatchesOpen(Collection<RuleEvent> matches) {
        // remove current matches
        for (MatchTreeNode matchNode : this.matchNodeMap.values()) {
            this.ruleDirectory.removeNodeFromParent(matchNode);
        }
        // clean up current match node map
        this.matchNodeMap.clear();
        this.matchTransitionMap.clear();
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
        // recollect the derivations so that they are ordered according to the
        // rule events
        SortedSet<RuleEvent> orderedEvents = new TreeSet<RuleEvent>();
        orderedEvents.addAll(matches);
        // insert new matches
        for (RuleEvent event : orderedEvents) {
            Label ruleName = event.getRule().getName();
            RuleTreeNode ruleNode = this.ruleNodeMap.get(ruleName);
            assert ruleNode != null : String.format(
                "Rule %s has no image in map %s", ruleName, this.ruleNodeMap);
            int nrOfMatches = ruleNode.getChildCount();
            MatchTreeNode matchNode = new MatchTreeNode(nrOfMatches + 1, event);
            this.ruleDirectory.insertNodeInto(matchNode, ruleNode, nrOfMatches);
            expandPath(new TreePath(ruleNode.getPath()));
            this.matchNodeMap.put(event, matchNode);
        }

        for (GraphTransition trans : getCurrentGTS().outEdgeSet(
            getCurrentState())) {
            this.matchTransitionMap.put(trans.getEvent(), trans);
        }
    }

    /**
     * Refreshes the match nodes, based on a given derivation edge set.
     * @param derivations the set of derivation edges used to create match nodes
     */
    private void refreshMatchesClosed(Set<? extends GraphTransition> derivations) {
        // remove current matches
        for (MatchTreeNode matchNode : this.matchNodeMap.values()) {
            this.ruleDirectory.removeNodeFromParent(matchNode);
        }
        // clean up current match node map
        this.matchNodeMap.clear();
        this.matchTransitionMap.clear();

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
        // recollect the derivations so that they are ordered according to the
        // rule events
        SortedSet<GraphTransition> orderedDerivations =
            new TreeSet<GraphTransition>(new Comparator<GraphTransition>() {
                public int compare(GraphTransition o1, GraphTransition o2) {
                    return o1.getEvent().compareTo(o2.getEvent());
                }
            });
        orderedDerivations.addAll(derivations);
        // insert new matches
        for (GraphTransition edge : orderedDerivations) {
            Label ruleName = edge.getEvent().getRule().getName();
            RuleTreeNode ruleNode = this.ruleNodeMap.get(ruleName);
            assert ruleNode != null : String.format(
                "Rule %s has no image in map %s", ruleName, this.ruleNodeMap);
            int nrOfMatches = ruleNode.getChildCount();
            MatchTreeNode matchNode = new MatchTreeNode(nrOfMatches + 1, edge);
            this.ruleDirectory.insertNodeInto(matchNode, ruleNode, nrOfMatches);
            expandPath(new TreePath(ruleNode.getPath()));
            this.matchNodeMap.put(edge.getEvent(), matchNode);
            this.matchTransitionMap.put(edge.getEvent(), edge);
        }
    }

    /** Convenience method to retrieve the current GTS from the simulator. */
    private GTS getCurrentGTS() {
        return getSimulator().getGTS();
    }

    /**
     * Convenience method to retrieve the currently selected transition from the
     * simulator.
     */
    private GraphTransition getCurrentTransition() {
        return getSimulator().getCurrentTransition();
    }

    /**
     * Convenience method to retrieve the currently selected match from the
     * state panel
     */
    private RuleEvent getCurrentEvent() {
        return getSimulator().getCurrentEvent();
    }

    /**
     * Convenience method to retrieve the currently selected state from the
     * simulator.
     */
    private GraphState getCurrentState() {
        return getSimulator().getCurrentState();
    }

    /**
     * Convenience method to retrieve the currently selected rule from the
     * simulator.
     */
    private RuleView getCurrentRule() {
        return getSimulator().getCurrentRule();
    }

    /**
     * Sets the {@link #displayedState} field to a given value, and returns an
     * indication whether the new value differs from the old.
     * @param state the new value of the displayed state
     * @return <code>true</code> if the new value differs from the old
     */
    private boolean setDisplayedState(GraphState state) {
        boolean result = state != this.displayedState;
        this.displayedState = state;
        return result;
    }

    /**
     * Creates the selection listener to be used to react on selections in this
     * rule directory. The current implementation returns a
     * <tt>RuleSelectionListener</tt>.
     * @see RuleJTree.RuleSelectionListener
     */
    protected TreeSelectionListener createRuleSelectionListener() {
        return new RuleSelectionListener();
    }

    /**
     * Creates a popup menu for this panel.
     * @param node the node for which the menu is created
     */
    protected JPopupMenu createPopupMenu(TreeNode node) {
        JPopupMenu res = new JPopupMenu();
        res.setFocusable(false);
        res.add(getSimulator().getNewRuleAction());
        if (node instanceof RuleTreeNode) {
            res.addSeparator();
            res.add(getSimulator().getEnableRuleAction());
            res.addSeparator();
            res.add(getSimulator().getCopyRuleAction());
            res.add(getSimulator().getDeleteRuleAction());
            res.add(getSimulator().getRenameRuleAction());
            res.addSeparator();
            res.add(getSimulator().getEditRulePropertiesAction());
            res.add(getSimulator().getEditRuleAction());
        } else if (node instanceof MatchTreeNode) {
            res.addSeparator();
            res.add(getSimulator().getApplyTransitionAction());
        }
        return res;
    }

    /**
     * Indicates if the rule tree is at the moment should react to selection
     * changes.
     */
    final boolean isListenToSelectionChanges() {
        return this.listenToSelectionChanges;
    }

    /**
     * Directory of production rules and their matchings to the current state.
     * Alias to the underlying model of this <tt>JTree</tt>.
     * 
     * @invariant <tt>ruleDirectory == getModel()</tt>
     */
    protected final DefaultTreeModel ruleDirectory;
    /**
     * Alias for the top node in <tt>ruleDirectory</tt>.
     * @invariant <tt>topDirectoryNode == ruleDirectory.getRoot()</tt>
     */
    protected final DefaultMutableTreeNode topDirectoryNode;

    /** Returns the associated simulator. */
    private final Simulator getSimulator() {
        return this.simulator;
    }

    /**
     * Switches the simulator to the state panel view, and
     * refreshes the actions.
     */
    private void switchSimulatorToRulePanel() {
        getSimulator().setGraphPanel(getSimulator().getRulePanel());
        getSimulator().refreshActions();
    }

    /**
     * The simulator to which this directory belongs.
     * @invariant simulator != null
     */
    private final Simulator simulator;
    /**
     * Mapping from rule names in the current grammar to rule nodes in the
     * current rule directory.
     * @invariant <tt>ruleNodeMap: StructuredRuleName --> DirectoryTreeNode
     *                                               \cup RuleTreeNode</tt>
     */
    protected final Map<RuleName,RuleTreeNode> ruleNodeMap =
        new HashMap<RuleName,RuleTreeNode>();

    /**
     * Mapping from RuleMatches in the current LTS to match nodes in the rule
     * directory
     */
    protected final Map<RuleEvent,MatchTreeNode> matchNodeMap =
        new HashMap<RuleEvent,MatchTreeNode>();

    /**
     * Mapping from RuleMatches to transitions in the current LTS, for fast
     * selecting
     */
    protected final Map<RuleEvent,GraphTransition> matchTransitionMap =
        new HashMap<RuleEvent,GraphTransition>();

    /**
     * Switch to determine whether changes in the tree selection model should
     * trigger any actions right now.
     */
    private transient boolean listenToSelectionChanges;

    /**
     * The background colour of this component when it is enabled.
     */
    private Color enabledBackground;
    /** Flag to indicate that the anchor image option listener has been set. */
    private boolean anchorImageOptionListenerSet = false;
    /** The currently displayed state. */
    private GraphState displayedState;
    /** The currently displayed grammar. */
    private GrammarView displayedGrammar;

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

    static private final Color TREE_ENABLED_COLOR = Color.WHITE;
    /** The background colour of a selected cell if the list does not have focus. */
    static private final Color SELECTION_NON_FOCUS_COLOR = Color.LIGHT_GRAY;

    /**
     * Selection listener that invokes <tt>setRule</tt> if a rule node is
     * selected, and <tt>setDerivation</tt> if a match node is selected.
     * @see Simulator#setRule
     * @see Simulator#setTransition
     */
    private class RuleSelectionListener implements TreeSelectionListener {
        /**
         * Empty constructor with the correct visibility.
         */
        public RuleSelectionListener() {
            // Empty
        }

        public void valueChanged(TreeSelectionEvent evt) {
            // only do something if a path was added to the selection
            if (isListenToSelectionChanges() && evt.isAddedPath()) {
                TreePath[] paths = getSelectionPaths();
                for (int i = 0; i < paths.length; i++) {
                    Object selectedNode = paths[i].getLastPathComponent();
                    if (selectedNode instanceof RuleTreeNode) {
                        // selected tree node is a production rule (level 1
                        // node)
                        if (paths.length == 1) {
                            getSimulator().setRule(
                                ((RuleTreeNode) selectedNode).getRule().getRuleName());
                            switchSimulatorToRulePanel();
                        }
                    } else if (selectedNode instanceof MatchTreeNode) {
                        // selected tree node is a match (level 2 node)
                        RuleEvent event =
                            ((MatchTreeNode) selectedNode).event();
                        GraphTransition trans =
                            RuleJTree.this.matchTransitionMap.get(event);
                        if (trans == null) {
                            // possibly there is a transition associated with
                            // this event that has not yet made it to the
                            // matchTransitionMap because the refresh is only
                            // occurring after setting the event; so look it
                            // up among the outgoing transitions
                            Iterator<GraphTransition> outTransitions =
                                getCurrentState().getTransitionIter();
                            while (outTransitions.hasNext()) {
                                GraphTransition outTrans =
                                    outTransitions.next();
                                if (outTrans.getEvent().equals(event)) {
                                    RuleJTree.this.matchTransitionMap.put(
                                        event, trans = outTrans);
                                    break;
                                }
                            }
                        }
                        if (trans != null) {
                            getSimulator().setTransition(trans);
                        } else {
                            getSimulator().setEvent(event);
                        }
                        if (getSimulator().getGraphPanel() == getSimulator().getRulePanel()) {
                            getSimulator().setGraphPanel(
                                getSimulator().getStatePanel());
                        }
                    }
                }
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
                    getSimulator().applyMatch();
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
    private static class RuleTreeNode extends DefaultMutableTreeNode {
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
            String name = getRule().getRuleName().child();
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
            result.append(Converter.STRONG_TAG.on(getRule().getName()));
            GraphProperties properties =
                GraphInfo.getProperties(getRule().getView(), false);
            if (properties != null && !properties.isEmpty()) {
                boolean hasProperties;
                String remark = properties.getRemark();
                if (remark != null) {
                    result.append(": ");
                    result.append(Converter.toHtml(remark));
                    hasProperties = properties.size() > 1;
                } else {
                    hasProperties = true;
                }
                if (hasProperties) {
                    for (String key : properties.getPropertyKeys()) {
                        if (!GraphProperties.isSystemKey(key)
                            && !key.equals(GraphProperties.REMARK_KEY)) {
                            result.append(Converter.HTML_LINEBREAK);
                            result.append(propertyToString(key,
                                properties.getProperty(key)));
                        }
                    }
                }
            }
            Converter.HTML_TAG.on(result);
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
         * Creates a new match node on the basis of a given number and
         * derivation edge. The node cannot have children.
         */
        public MatchTreeNode(int nr, GraphTransition trans) {
            super(trans.getEvent(), false);
            this.nr = nr;
        }

        /**
         * Creates a new match node on the basis of a given number and the
         * RuleMatch. The node cannot have children.
         */
        public MatchTreeNode(int nr, RuleEvent event) {
            super(event, false);
            this.nr = nr;
        }

        /**
         * Convenience method to return the underlying derivation edge.
         */
        public RuleEvent event() {
            return (RuleEvent) getUserObject();
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
                Options.SHOW_ANCHORS_OPTION) ? event().getAnchorImageString()
                    : "Match " + this.nr;
        }

        /** The number of this match, used in <tt>toString()</tt> */
        private final int nr;
    }

    /**
     * Class to provide proper icons for directory nodes
     */
    private class MyTreeCellRenderer extends DefaultTreeCellRenderer {
        /**
         * Empty constructor with the correct visibility.
         */
        public MyTreeCellRenderer() {
            // empty
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean sel, boolean expanded, boolean leaf, int row,
                boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded,
                leaf, row, hasFocus);

            if (value instanceof DirectoryTreeNode) {
                setIcon(Groove.GPS_FOLDER_ICON);
            } else if (value instanceof PriorityTreeNode) {
                setIcon(null);
            }
            if (value instanceof RuleTreeNode) {
                setToolTipText(((RuleTreeNode) value).getToolTipText());
            } else {
                setToolTipText(null);
            }
            setOpaque(!sel);
            return this;
        }

        @Override
        public Color getBackgroundSelectionColor() {
            Color result;
            if (RuleJTree.this.isFocusOwner()) {
                result = super.getBackgroundSelectionColor();
            } else {
                result = SELECTION_NON_FOCUS_COLOR;
                if (getCurrentGTS() == null) {
                    result = result.darker();
                }
            }
            return result;
        }

        @Override
        public Color getTextSelectionColor() {
            if (RuleJTree.this.isFocusOwner() || getCurrentGTS() == null) {
                return super.getTextSelectionColor();
            } else {
                return Color.BLACK;
            }
        }

    }
}
