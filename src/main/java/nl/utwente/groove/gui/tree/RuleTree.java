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

import static nl.utwente.groove.gui.SimulatorModel.Change.GRAMMAR;
import static nl.utwente.groove.gui.SimulatorModel.Change.GTS;
import static nl.utwente.groove.gui.SimulatorModel.Change.MATCH;
import static nl.utwente.groove.gui.SimulatorModel.Change.RULE;
import static nl.utwente.groove.gui.SimulatorModel.Change.STATE;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import nl.utwente.groove.grammar.Action;
import nl.utwente.groove.grammar.CheckPolicy;
import nl.utwente.groove.grammar.ModuleName;
import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.Recipe;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.grammar.model.ResourceModel;
import nl.utwente.groove.grammar.model.RuleModel;
import nl.utwente.groove.gui.Icons;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.SimulatorModel;
import nl.utwente.groove.gui.SimulatorModel.Change;
import nl.utwente.groove.gui.display.ControlDisplay;
import nl.utwente.groove.gui.display.DisplayKind;
import nl.utwente.groove.gui.display.RuleDisplay;
import nl.utwente.groove.gui.display.TextTab;
import nl.utwente.groove.io.HTMLConverter;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.lts.GraphTransition;
import nl.utwente.groove.lts.GraphTransition.Claz;
import nl.utwente.groove.lts.GraphTransitionKey;
import nl.utwente.groove.lts.MatchResult;
import nl.utwente.groove.lts.RecipeEvent;
import nl.utwente.groove.util.Exceptions;

/**
 * Panel that displays a two-level directory of rules and matches.
 * @version $Revision$
 * @author Arend Rensink
 */
public class RuleTree extends AbstractResourceTree {
    /** Creates an instance for a given simulator. */
    public RuleTree(RuleDisplay display) {
        super(display);
        // the following is the easiest way to ensure that changes in
        // tree labels will be correctly reflected in the display
        // A cleaner way is to invoke DefaultTreeModel.nodeChanged,
        // but how are we supposed to know when this occurs?
        setLargeModel(true);
        setRootVisible(false);
        setShowsRootHandles(true);
        setEnabled(false);
        setToggleClickCount(0);
        setCellRenderer(new DisplayTreeCellRenderer(this));
        getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        // set icons
        DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) this.cellRenderer;
        renderer.setLeafIcon(Icons.GRAPH_MATCH_ICON);
        this.topDirectoryNode = new DisplayTreeNode();
        this.ruleDirectory = new DefaultTreeModel(this.topDirectoryNode, true);
        setModel(this.ruleDirectory);
        // set key bindings
        ActionMap am = getActionMap();
        am.put(Options.BACK_ACTION_NAME, getActions().getBackAction());
        am.put(Options.FORWARD_ACTION_NAME, getActions().getForwardAction());
        InputMap im = getInputMap();
        im.put(Options.BACK_KEY, Options.BACK_ACTION_NAME);
        im.put(Options.FORWARD_KEY, Options.FORWARD_ACTION_NAME);
        // add tool tips
        installListeners();
    }

    @Override
    void activateListeners() {
        super.activateListeners();
        getSimulatorModel().addListener(this, STATE, MATCH);
    }

    @Override
    TreeSelectionListener createSelectionListener() {
        return new RuleSelectionListener();
    }

    @Override
    MouseListener createMouseListener() {
        return new MyMouseListener();
    }

    @Override
    JPopupMenu createPopupMenu(TreeNode node) {
        JPopupMenu res = super.createPopupMenu(node);
        if (node instanceof RuleTreeNode) {
            res.add(getActions().getSetPriorityAction());
            res.add(getActions().getShiftPriorityAction(true));
            res.add(getActions().getShiftPriorityAction(false));
            res.add(getActions().getEditRulePropertiesAction());
        } else if (node instanceof MatchTreeNode) {
            res.addSeparator();
            res.add(getActions().getApplyMatchAction());
        }
        return res;
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel, Set<Change> changes) {
        suspendListeners();
        boolean renewSelection = false;
        if (changes.contains(GRAMMAR)) {
            GrammarModel grammar = source.getGrammar();
            if (grammar == null) {
                this.clearAllMaps();
                this.topDirectoryNode.removeAllChildren();
                this.ruleDirectory.reload();
            } else {
                loadGrammar(grammar);
            }
            refresh(source.getState());
            renewSelection = true;
        } else {
            if (changes.contains(GTS) || changes.contains(STATE)) {
                // if the GTS has changed, this may mean that the state
                // displayed here has been closed, in which case we have to refresh
                // since the rule events have been changed into transitions
                refresh(source.getState());
                renewSelection = true;
            }
            if (changes.contains(MATCH) || changes.contains(RULE)) {
                renewSelection = true;
            }
        }
        if (renewSelection) {
            ResourceModel<?> ruleModel = source.getResource(ResourceKind.RULE);
            Set<GraphTransitionKey> keys = new HashSet<>();
            if (source.hasMatch()) {
                keys.add(source.getMatch());
            }
            if (source.hasTransition()) {
                keys.add(source.getTransition().getKey());
            }
            selectMatch((RuleModel) ruleModel, keys);
        }
        activateListeners();
    }

    /** Clears all maps of the tree. */
    private void clearAllMaps() {
        this.actionNodeMap.clear();
        this.matchNodeMap.clear();
    }

    /**
     * Loads the j-tree with the data of a given grammar.
     * @param grammar the grammar to be loaded; non-{@code null}
     */
    private void loadGrammar(GrammarModel grammar) {
        setShowAnchorsOptionListener();
        this.clearAllMaps();
        this.topDirectoryNode.removeAllChildren();
        DisplayTreeNode topNode = this.topDirectoryNode;
        Map<Integer,Set<ActionEntry>> priorityMap = getPriorityMap(grammar);
        Map<CheckPolicy,Set<ActionEntry>> policyMap = getPolicyMap(grammar);
        Set<RuleEntry> fragmentSet = getFragments(grammar);
        List<TreePath> expandedPaths = new ArrayList<>();
        List<TreePath> selectedPaths = new ArrayList<>();
        boolean hasMultipleLevels
            = !fragmentSet.isEmpty() || priorityMap.size() + policyMap.size() > 1;
        // add the recipes and rules
        for (Map.Entry<Integer,Set<ActionEntry>> priorityEntry : priorityMap.entrySet()) {
            int priority = priorityEntry.getKey();
            Map<QualName,FolderTreeNode> dirNodeMap = new HashMap<>();
            // if the rule system has multiple priorities, we want an extra
            // level of nodes
            if (hasMultipleLevels) {
                topNode = new DirectoryTreeNode(DirectoryKind.ACTION, null, priority,
                    priorityMap.size() > 1);
                this.topDirectoryNode.add(topNode);
                dirNodeMap.clear();
            }
            // add the actions to the tree
            for (var action : priorityEntry.getValue()) {
                QualName name = action.getQualName();
                // recursively add parent directory nodes as required
                DisplayTreeNode parentNode = addParentNode(topNode, dirNodeMap, name.parent());
                var node = createActionNode(action, expandedPaths, selectedPaths);
                parentNode.insertSorted(node);
            }
        }
        // add the recipe fragments
        if (!fragmentSet.isEmpty()) {
            topNode = new DirectoryTreeNode(DirectoryKind.FRAGMENT, null, 0, false);
            this.topDirectoryNode.add(topNode);
            for (var ruleEntry : fragmentSet) {
                Map<QualName,FolderTreeNode> dirNodeMap = new HashMap<>();
                QualName name = ruleEntry.getQualName();
                // recursively add parent directory nodes as required
                var parentNode = addParentNode(topNode, dirNodeMap, name.parent());
                var actionNode = createActionNode(ruleEntry, expandedPaths, selectedPaths);
                parentNode.insertSorted(actionNode);
            }
        }
        // add the conditions
        for (Map.Entry<CheckPolicy,Set<ActionEntry>> priorityEntry : policyMap.entrySet()) {
            CheckPolicy policy = priorityEntry.getKey();
            if (policy == CheckPolicy.OFF) {
                policy = CheckPolicy.SILENT;
            }
            Map<QualName,FolderTreeNode> dirNodeMap = new HashMap<>();
            // if the rule system has multiple priorities, we want an extra
            // level of nodes
            if (hasMultipleLevels) {
                topNode = new DirectoryTreeNode(DirectoryKind.CONDITION, policy, 0,
                    policyMap.size() > 1);
                this.topDirectoryNode.add(topNode);
                dirNodeMap.clear();
            }
            // add the property rules to the tree
            for (ActionEntry action : priorityEntry.getValue()) {
                QualName name = action.getQualName();
                // recursively add parent directory nodes as required
                DisplayTreeNode parentNode = addParentNode(topNode, dirNodeMap, name.parent());
                var node = createActionNode(action, expandedPaths, selectedPaths);
                parentNode.insertSorted(node);
            }
        }
        this.ruleDirectory.reload(this.topDirectoryNode);
        for (TreePath path : expandedPaths) {
            expandPath(path);
        }
        setSelectionPaths(selectedPaths.toArray(new TreePath[0]));
    }

    private ActionTreeNode createActionNode(ActionEntry action, List<TreePath> expandedPaths,
                                            List<TreePath> selectedPaths) {
        Collection<QualName> selection = getSimulatorModel().getSelectSet(ResourceKind.RULE);
        QualName name = action.getQualName();
        // create the rule node and register it
        ActionTreeNode node = action.createTreeNode();
        this.actionNodeMap.put(action.getQualName(), node);
        TreePath path = new TreePath(node.getPath());
        expandedPaths.add(path);
        if (selection.contains(name)) {
            selectedPaths.add(path);
        }
        return node;
    }

    /**
     * Creates a map from priorities to nonempty sets of rules with that
     * priority from the rule in a given grammar view.
     * @param grammar the source of the rule map
     */
    private Map<Integer,Set<ActionEntry>> getPriorityMap(GrammarModel grammar) {
        Map<Integer,Set<ActionEntry>> result = new TreeMap<>(Action.PRIORITY_COMPARATOR);
        for (Recipe recipe : grammar.getControlModel().getRecipes()) {
            int priority = recipe.getPriority();
            Set<ActionEntry> recipes = result.get(priority);
            if (recipes == null) {
                result.put(priority, recipes = new HashSet<>());
            }
            recipes.add(new RecipeEntry(recipe));
        }
        for (ResourceModel<?> model : grammar.getResourceSet(ResourceKind.RULE)) {
            RuleModel rule = (RuleModel) model;
            if (!rule.isProperty() && !rule.hasRecipes()) {
                int priority = rule.getPriority();
                Set<ActionEntry> rules = result.get(priority);
                if (rules == null) {
                    result.put(priority, rules = new HashSet<>());
                }
                rules.add(new RuleEntry(rule));
            }
        }
        return result;
    }

    /**
     * Creates a map from check policies to nonempty sets of property rules with that
     * policy.
     * @param grammar the source of the rule map
     */
    private Set<RuleEntry> getFragments(GrammarModel grammar) {
        Set<RuleEntry> result = new HashSet<>();
        for (ResourceModel<?> model : grammar.getResourceSet(ResourceKind.RULE)) {
            RuleModel rule = (RuleModel) model;
            if (rule.hasRecipes()) {
                result.add(new RuleEntry(rule));
            }
        }
        return result;
    }

    /**
     * Creates a map from check policies to nonempty sets of property rules with that
     * policy.
     * @param grammar the source of the rule map
     */
    private Map<CheckPolicy,Set<ActionEntry>> getPolicyMap(GrammarModel grammar) {
        Map<CheckPolicy,Set<ActionEntry>> result = new EnumMap<>(CheckPolicy.class);
        for (ResourceModel<?> model : grammar.getResourceSet(ResourceKind.RULE)) {
            RuleModel ruleModel = (RuleModel) model;
            if (ruleModel.isProperty()) {
                CheckPolicy policy = ruleModel.getPolicy();
                policy = policy == CheckPolicy.OFF
                    ? CheckPolicy.SILENT
                    : policy;
                Set<ActionEntry> rules = result.get(policy);
                if (rules == null) {
                    result.put(policy, rules = new HashSet<>());
                }
                rules.add(new RuleEntry(ruleModel));
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
            JMenuItem showAnchorsOptionItem
                = getSimulator().getOptions().getItem(Options.SHOW_ANCHORS_OPTION);
            if (showAnchorsOptionItem != null) {
                // listen to the option controlling the rule anchor display
                showAnchorsOptionItem.addItemListener(new ItemListener() {
                    @Override
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
    private DisplayTreeNode addParentNode(DisplayTreeNode topNode,
                                          Map<QualName,FolderTreeNode> dirNodeMap,
                                          ModuleName parentName) {
        //        QualName parent = ruleName.parent();
        if (parentName.isTop()) {
            // there is no parent rule name; the parent node is the top node
            return topNode;
        } else {
            // there is a proper parent rule; look it up in the node map
            QualName ruleName = (QualName) parentName;
            FolderTreeNode result = dirNodeMap.get(parentName);
            if (result == null) {
                // the parent node did not yet exist in the tree
                // check recursively for the grandparent
                DisplayTreeNode grandParentNode
                    = addParentNode(topNode, dirNodeMap, ruleName.parent());
                // make the parent node and register it
                result = new FolderTreeNode(ruleName.last());
                grandParentNode.insertSorted(result);
                dirNodeMap.put(ruleName, result);
            }
            return result;
        }
    }

    /**
     * Refreshes the selection in the tree, based on the current state of the
     * Simulator.
     */
    private void refresh(GraphState state) {
        SortedSet<GraphTransitionKey> matches = new TreeSet<>(GraphTransitionKey.COMPARATOR);
        if (state != null) {
            for (GraphTransition trans : state.getTransitions(Claz.ANY)) {
                matches.add(trans.getKey());
            }
            matches.addAll(state.getMatches());
        }
        refreshMatches(state, matches);
        setEnabled(getGrammar() != null);
    }

    /**
     * Selects the rows of a given set of match keys, or if that is empty, a
     * given rule.
     * @param rule the rule to be selected if the event is {@code null}
     * @param keys the match results to be selected
     */
    private void selectMatch(RuleModel rule, Set<GraphTransitionKey> keys) {
        List<DisplayTreeNode> treeNodes = new ArrayList<>();
        for (GraphTransitionKey key : keys) {
            DisplayTreeNode node = this.matchNodeMap.get(key);
            if (node != null) {
                treeNodes.add(node);
            }
        }
        boolean matchSelected = !treeNodes.isEmpty();
        if (!matchSelected && rule != null) {
            treeNodes.add((RuleTreeNode) this.actionNodeMap.get(rule.getQualName()));
        }
        TreePath[] paths = new TreePath[treeNodes.size()];
        TreePath lastPath = null;
        for (int i = 0; i < treeNodes.size(); i++) {
            lastPath = paths[i] = new TreePath(treeNodes.get(i).getPath());
        }
        setSelectionPaths(paths);
        if (matchSelected) {
            scrollPathToVisible(lastPath);
        }
    }

    /**
     * Refreshes the match nodes, based on a given match result set.
     * @param matches the set of matches used to create {@link MatchTreeNode}s
     */
    private void refreshMatches(GraphState state, Collection<GraphTransitionKey> matches) {
        // remove current matches
        for (DisplayTreeNode matchNode : this.matchNodeMap.values()) {
            this.ruleDirectory.removeNodeFromParent(matchNode);
        }
        // clean up current match node map
        this.matchNodeMap.clear();
        Collection<ActionTreeNode> treeNodes = new ArrayList<>();
        var tried = getTried(state);
        // for all action nodes, check if it has been tried
        for (var actionNode : this.actionNodeMap.values()) {
            actionNode.setTried(tried.contains(actionNode.getQualName()));
            treeNodes.add(actionNode);
        }
        // expand all rule nodes and subsequently collapse all directory nodes
        for (var n : treeNodes) {
            expandPath(new TreePath(n.getPath()));
        }
        for (var n : treeNodes) {
            collapsePath(new TreePath(n.getPath()));
        }
        // recollect the match results so that they are ordered according to the
        // rule events
        // insert new matches
        for (GraphTransitionKey key : matches) {
            QualName actionName = key.getAction().getQualName();
            // new node to be created for this key
            DisplayTreeNode newNode;
            // parent node of the new node
            ActionTreeNode parentNode = this.actionNodeMap.get(actionName);
            // child index of the new node in the parent node
            int matchCount = parentNode.getChildCount();
            if (key instanceof MatchResult match) {
                newNode = new MatchTreeNode(getSimulatorModel(), state, match, matchCount + 1,
                    getSimulator().getOptions().isSelected(Options.SHOW_ANCHORS_OPTION));
            } else {
                RecipeEvent event = (RecipeEvent) key;
                newNode = new RecipeTransitionTreeNode(getSimulatorModel(), state, event,
                    matchCount + 1);
            }
            this.matchNodeMap.put(key, newNode);
            this.ruleDirectory.insertNodeInto(newNode, parentNode, matchCount);
            expandPath(new TreePath(parentNode.getPath()));
        }
    }

    /** Returns the set of pairs of rule/recipe name that have been tried
     * in the current state.
     */
    private Set<QualName> getTried(GraphState state) {
        Set<QualName> result = new HashSet<>();
        // set the tried status of the rules
        if (state != null) {
            var frame = state.getActualFrame();
            for (var attempt : frame.getPastAttempts()) {
                result.add(attempt.getRule().getQualName());
                attempt.getRecipe().map(Recipe::getQualName).ifPresent(result::add);
            }
            frame.getRecipe().map(Recipe::getQualName).ifPresent(result::add);
        }
        return result;
    }

    @Override
    public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf,
                                     int row, boolean hasFocus) {
        String result;
        if (value instanceof DisplayTreeNode) {
            result = ((DisplayTreeNode) value).getText();
        } else {
            result = super.convertValueToText(value, selected, expanded, leaf, row, hasFocus);
        }
        return result;
    }

    /** Convenience method to retrieve the control display. */
    private final ControlDisplay getControlDisplay() {
        return (ControlDisplay) getSimulator().getDisplaysPanel().getDisplay(DisplayKind.CONTROL);
    }

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
    private final DisplayTreeNode topDirectoryNode;
    /**
     * Mapping from action names in the current grammar to action nodes in the
     * current rule directory.
     */
    private final Map<QualName,ActionTreeNode> actionNodeMap = new HashMap<>();
    /**
     * Mapping from {@link MatchResult} in the current LTS to match nodes in the rule
     * directory
     */
    private final Map<GraphTransitionKey,DisplayTreeNode> matchNodeMap = new LinkedHashMap<>();

    /** Flag to indicate that the anchor image option listener has been set. */
    private boolean anchorImageOptionListenerSet = false;

    /** Tree entry showing the application of a rule or recipe. */
    private interface ActionEntry {
        public QualName getQualName();

        abstract public ActionTreeNode createTreeNode();
    }

    /** Tree entry showing the application of a rule. */
    private class RuleEntry implements ActionEntry {
        public RuleEntry(RuleModel model) {
            this.name = model.getQualName();
        }

        @Override
        public QualName getQualName() {
            return this.name;
        }

        private final QualName name;

        @Override
        public RuleTreeNode createTreeNode() {
            return new RuleTreeNode(getParentDisplay(), this.name);
        }
    }

    /** Tree entry showing the application of a recipe. */
    private class RecipeEntry implements ActionEntry {
        public RecipeEntry(Recipe recipe) {
            this.recipe = recipe;
        }

        private final Recipe recipe;

        @Override
        public QualName getQualName() {
            return this.recipe.getQualName();
        }

        @Override
        public RecipeTreeNode createTreeNode() {
            return new RecipeTreeNode(this.recipe);
        }
    }

    /**
     * Selection listener that invokes <tt>setRule</tt> if a rule node is
     * selected, and <tt>setDerivation</tt> if a match node is selected.
     * @see SimulatorModel#setMatch
     */
    private class RuleSelectionListener extends MySelectionListener {
        /**
         * Empty constructor with the correct visibility.
         */
        public RuleSelectionListener() {
            // Empty
        }

        @Override
        void setSelection(Collection<TreeNode> selectedNodes) {
            boolean done = false;
            for (TreeNode node : selectedNodes) {
                if (node instanceof MatchTreeNode mtn) {
                    // selected tree node is a match (level 2 node)
                    GraphState state = mtn.getSource();
                    MatchResult match = mtn.getMatch();
                    getSimulatorModel().setMatch(state, match);
                    done = true;
                    break;
                }
            }
            for (TreeNode node : selectedNodes) {
                if (node instanceof RecipeTransitionTreeNode rttn) {
                    // selected tree node is a match (level 2 node)
                    GraphTransition trans = rttn.getTransition();
                    getSimulatorModel().setTransition(trans);
                    done = true;
                    break;
                }
            }
            if (!done) {
                // otherwise, select a recipe if appropriate
                for (TreeNode node : selectedNodes) {
                    if (node instanceof RecipeTreeNode rtn) {
                        Recipe recipe = rtn.getRecipe();
                        getSimulatorModel()
                            .doSelectSet(ResourceKind.RULE, Collections.<QualName>emptySet());
                        getSimulatorModel().doSelect(ResourceKind.CONTROL, recipe.getControlName());
                        TextTab controlTab = (TextTab) getControlDisplay().getSelectedTab();
                        controlTab.select(recipe.getStartLine(), 0);
                        done = true;
                        break;
                    }
                }
            }
            if (!done) {
                super.setSelection(selectedNodes);
            }
        }
    }

    /**
     * Mouse listener that creates the popup menu and switches the view to the
     * rule panel on double-clicks.
     */
    private class MyMouseListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent evt) {
            TreePath path = getPathForLocation(evt.getX(), evt.getY());
            if (path != null) {
                if (evt.getButton() == MouseEvent.BUTTON3 && !isRowSelected(getRowForPath(path))) {
                    setSelectionPath(path);
                }
                DisplayKind toDisplay = null;
                Object lastComponent = path.getLastPathComponent();
                if (lastComponent instanceof RuleTreeNode) {
                    toDisplay = DisplayKind.RULE;
                } else if (lastComponent instanceof RecipeTreeNode) {
                    toDisplay = DisplayKind.CONTROL;
                } else if (lastComponent instanceof MatchTreeNode
                    && getSimulatorModel().getDisplay() != DisplayKind.LTS) {
                    toDisplay = DisplayKind.STATE;
                }
                if (evt.getClickCount() == 1 && toDisplay != null) {
                    getSimulatorModel().setDisplay(toDisplay);
                } else if (evt.getClickCount() == 2 && toDisplay != null) {
                    if (toDisplay.hasResource()) {
                        getActions().getEditAction(toDisplay.getResource()).execute();
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
            if (selectedNode instanceof MatchTreeNode
                || selectedNode instanceof RecipeTransitionTreeNode) {
                if (evt.getClickCount() == 2) {
                    getActions().getApplyMatchAction().execute();
                }
            }
        }

        private void maybeShowPopup(MouseEvent evt) {
            if (evt.isPopupTrigger()) {
                TreePath selectedPath = getPathForLocation(evt.getX(), evt.getY());
                TreeNode selectedNode = selectedPath == null
                    ? null
                    : (TreeNode) selectedPath.getLastPathComponent();
                RuleTree.this.requestFocus();
                createPopupMenu(selectedNode).show(evt.getComponent(), evt.getX(), evt.getY());
            }
        }
    }

    /** Kinds of directory nodes in the tree. */
    static public enum DirectoryKind {
        /** Directory of rules and recipes. */
        ACTION,
        /** Directory of recipe fragments. */
        FRAGMENT,
        /** Directory of conditions. */
        CONDITION;
    }

    /**
     * Directory nodes (priorities of check policies).
     */
    public static class DirectoryTreeNode extends FolderTreeNode {
        /**
         * Creates a new priority node based on a given priority. The node can
         * (and will) have children.
         */
        public DirectoryTreeNode(DirectoryKind kind, CheckPolicy policy, int priority,
                                 boolean hasMultiple) {
            super(getText(kind, policy, priority, hasMultiple));
            this.kind = kind;
            this.policy = policy;
            this.priority = priority;
            this.hasMultiple = hasMultiple;
        }

        @Override
        public Icon getIcon() {
            return Icons.EMPTY_ICON;
        }

        @Override
        public String getTip() {
            StringBuilder result = new StringBuilder();
            switch (this.kind) {
            case ACTION:
                result.append("List of modifying rules and recipes");
                if (this.hasMultiple) {
                    result.append(" of priority ");
                    result.append(this.priority);
                    result.append(HTMLConverter.HTML_LINEBREAK);
                    result
                        .append("Will be scheduled only when all higher-level priority transformers failed");
                }
                break;
            case CONDITION:
                result
                    .append("List of graph properties, defined by unmodifying rules<br>"
                        + "and checked automatically at every non-transient state,");
                switch (this.policy) {
                case ERROR:
                    result.append("<br>which, when violated, will flag an error");
                    break;
                case REMOVE:
                    result.append("<br>which, when violated, will cause the state to be removed");
                    break;
                default:
                    // no special text
                }
                break;
            case FRAGMENT:
                result.append("List of recipe fragments.<br>");
                result.append("These are only applied as part of a recipe");
                break;
            default:
                throw Exceptions.UNREACHABLE;
            }
            return HTMLConverter.HTML_TAG.on(result).toString();
        }

        /** Returns the kind of items under this directory node. */
        public DirectoryKind getKind() {
            return this.kind;
        }

        private final DirectoryKind kind;
        private final CheckPolicy policy;
        private final int priority;
        private final boolean hasMultiple;

        private static String getText(DirectoryKind kind, CheckPolicy policy, int priority,
                                      boolean hasMultiple) {
            StringBuilder result = new StringBuilder();
            switch (kind) {
            case ACTION:
                if (hasMultiple) {
                    result.append("Priority ");
                    result.append(priority);
                    result.append(" transformers");
                } else {
                    result.append("Transformers");
                }
                break;
            case FRAGMENT:
                result.append("Recipe ingredients");
                break;
            case CONDITION:
                switch (policy) {
                case SILENT:
                    result.append("Graph conditions");
                    break;
                case ERROR:
                    result.append("Safety constraints");
                    break;
                case REMOVE:
                    result.append("Enforced constraints");
                    break;
                case OFF:
                    // no special text
                    break;
                default:
                    throw Exceptions.UNREACHABLE;
                }
                break;
            default:
                throw Exceptions.UNREACHABLE;
            }
            return HTMLConverter.UNDERLINE_TAG.on(result).toString();
        }
    }
}
