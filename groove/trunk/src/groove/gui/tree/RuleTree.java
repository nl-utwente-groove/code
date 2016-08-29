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
import static groove.gui.SimulatorModel.Change.GTS;
import static groove.gui.SimulatorModel.Change.MATCH;
import static groove.gui.SimulatorModel.Change.RULE;
import static groove.gui.SimulatorModel.Change.STATE;

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

import groove.control.CallStack;
import groove.grammar.Action;
import groove.grammar.CheckPolicy;
import groove.grammar.ModuleName;
import groove.grammar.QualName;
import groove.grammar.Recipe;
import groove.grammar.model.GrammarModel;
import groove.grammar.model.ResourceKind;
import groove.grammar.model.ResourceModel;
import groove.grammar.model.RuleModel;
import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.SimulatorModel;
import groove.gui.SimulatorModel.Change;
import groove.gui.display.ControlDisplay;
import groove.gui.display.DisplayKind;
import groove.gui.display.RuleDisplay;
import groove.gui.display.TextTab;
import groove.io.HTMLConverter;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.GraphTransition.Claz;
import groove.lts.GraphTransitionKey;
import groove.lts.MatchResult;
import groove.lts.RecipeEvent;
import groove.util.Duo;
import groove.util.Exceptions;

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
                keys.add(source.getTransition()
                    .getKey());
            }
            selectMatch((RuleModel) ruleModel, keys);
        }
        activateListeners();
    }

    /** Clears all maps of the tree. */
    private void clearAllMaps() {
        this.recipeNodeMap.clear();
        this.ruleNodeMap.clear();
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
        List<TreePath> expandedPaths = new ArrayList<>();
        List<TreePath> selectedPaths = new ArrayList<>();
        boolean hasMultipleLevels = priorityMap.size() + policyMap.size() > 1;
        for (Map.Entry<Integer,Set<ActionEntry>> priorityEntry : priorityMap.entrySet()) {
            int priority = priorityEntry.getKey();
            Map<QualName,FolderTreeNode> dirNodeMap = new HashMap<>();
            // if the rule system has multiple priorities, we want an extra
            // level of nodes
            if (hasMultipleLevels) {
                topNode = new DirectoryTreeNode(null, priority, priorityMap.size() > 1);
                this.topDirectoryNode.add(topNode);
                dirNodeMap.clear();
            }
            // collect entries for all actions
            Map<QualName,RuleEntry> ruleEntryMap = new HashMap<>();
            List<RecipeEntry> recipes = new ArrayList<>();
            for (ActionEntry action : priorityEntry.getValue()) {
                if (action instanceof RecipeEntry) {
                    recipes.add((RecipeEntry) action);
                } else {
                    ruleEntryMap.put(action.getQualName(), (RuleEntry) action);
                }
            }
            // add the recipes to the tree
            for (RecipeEntry recipe : recipes) {
                QualName recipeName = recipe.getQualName();
                // recursively add parent directory nodes as required
                DisplayTreeNode parentNode =
                    addParentNode(topNode, dirNodeMap, recipeName.parent());
                DisplayTreeNode recipeNode = createActionNode(recipe, expandedPaths, selectedPaths);
                parentNode.insertSorted(recipeNode);
            }
            // add the remaining rules to the tree
            for (RuleEntry ruleEntry : ruleEntryMap.values()) {
                QualName name = ruleEntry.getQualName();
                // recursively add parent directory nodes as required
                DisplayTreeNode parentNode = addParentNode(topNode, dirNodeMap, name.parent());
                DisplayTreeNode ruleNode =
                    createActionNode(ruleEntry, expandedPaths, selectedPaths);
                parentNode.insertSorted(ruleNode);
            }
        }
        for (Map.Entry<CheckPolicy,Set<ActionEntry>> priorityEntry : policyMap.entrySet()) {
            CheckPolicy policy = priorityEntry.getKey();
            Map<QualName,FolderTreeNode> dirNodeMap = new HashMap<>();
            // if the rule system has multiple priorities, we want an extra
            // level of nodes
            if (hasMultipleLevels) {
                topNode = new DirectoryTreeNode(policy, 0, policyMap.size() > 1);
                this.topDirectoryNode.add(topNode);
                dirNodeMap.clear();
            }
            // add the property rules to the tree
            for (ActionEntry action : priorityEntry.getValue()) {
                QualName name = action.getQualName();
                // recursively add parent directory nodes as required
                DisplayTreeNode parentNode = addParentNode(topNode, dirNodeMap, name.parent());
                DisplayTreeNode ruleNode = createActionNode(action, expandedPaths, selectedPaths);
                parentNode.insertSorted(ruleNode);
            }
        }
        this.ruleDirectory.reload(this.topDirectoryNode);
        for (TreePath path : expandedPaths) {
            expandPath(path);
        }
        setSelectionPaths(selectedPaths.toArray(new TreePath[0]));
    }

    private DisplayTreeNode createActionNode(ActionEntry action, List<TreePath> expandedPaths,
        List<TreePath> selectedPaths) {
        Collection<QualName> selection = getSimulatorModel().getSelectSet(ResourceKind.RULE);
        QualName name = action.getQualName();
        // create the rule node and register it
        DisplayTreeNode node = action.createTreeNode();
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
        for (Recipe recipe : grammar.getControlModel()
            .getRecipes()) {
            int priority = recipe.getPriority();
            Set<ActionEntry> recipes = result.get(priority);
            if (recipes == null) {
                result.put(priority, recipes = new HashSet<>());
            }
            recipes.add(new RecipeEntry(recipe));
        }
        for (ResourceModel<?> model : grammar.getResourceSet(ResourceKind.RULE)) {
            RuleModel rule = (RuleModel) model;
            if (!rule.isProperty()) {
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
    private Map<CheckPolicy,Set<ActionEntry>> getPolicyMap(GrammarModel grammar) {
        Map<CheckPolicy,Set<ActionEntry>> result = new EnumMap<>(CheckPolicy.class);
        for (ResourceModel<?> model : grammar.getResourceSet(ResourceKind.RULE)) {
            RuleModel ruleModel = (RuleModel) model;
            if (ruleModel.isProperty()) {
                CheckPolicy policy = ruleModel.getPolicy();
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
            JMenuItem showAnchorsOptionItem = getSimulator().getOptions()
                .getItem(Options.SHOW_ANCHORS_OPTION);
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
        Map<QualName,FolderTreeNode> dirNodeMap, ModuleName parentName) {
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
                DisplayTreeNode grandParentNode =
                    addParentNode(topNode, dirNodeMap, ruleName.parent());
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
            treeNodes.add(this.ruleNodeMap.get(rule.getQualName()));
        }
        TreePath[] paths = new TreePath[treeNodes.size()];
        TreePath lastPath = null;
        for (int i = 0; i < treeNodes.size(); i++) {
            lastPath = paths[i] = new TreePath(treeNodes.get(i)
                .getPath());
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
        // remove current matches
        for (RuleTreeNode subruleNode : this.subruleNodeMap.values()) {
            this.ruleDirectory.removeNodeFromParent(subruleNode);
        }
        // clean up current match node map
        this.subruleNodeMap.clear();
        Collection<DisplayTreeNode> treeNodes = new ArrayList<>();
        Set<Duo<QualName>> triedPairs = getTried(state);
        // construct rule nodes for subrules
        for (Duo<QualName> pair : triedPairs) {
            if (pair.two() == null) {
                // only do this for subrules
                continue;
            }
            RuleTreeNode ruleNode = new RuleTreeNode(getParentDisplay(), pair.one());
            ruleNode.setTried(true);
            this.subruleNodeMap.put(pair, ruleNode);
            RecipeTreeNode recipeNode = this.recipeNodeMap.get(pair.two());
            recipeNode.insertSorted(ruleNode);
            treeNodes.add(recipeNode);
            treeNodes.add(ruleNode);
        }
        // for all nodes, check if their rule/recipe pair has been tried
        for (RuleTreeNode ruleNode : this.ruleNodeMap.values()) {
            treeNodes.add(ruleNode);
            QualName ruleName = ruleNode.getQualName();
            boolean tried = triedPairs.contains(Duo.newDuo(ruleName, null));
            ruleNode.setTried(tried);
        }
        // expand all rule nodes and subsequently collapse all directory nodes
        for (DisplayTreeNode n : treeNodes) {
            expandPath(new TreePath(n.getPath()));
        }
        for (DisplayTreeNode n : treeNodes) {
            collapsePath(new TreePath(n.getPath()));
        }
        // recollect the match results so that they are ordered according to the
        // rule events
        // insert new matches
        for (GraphTransitionKey key : matches) {
            // new node to be created for this key
            DisplayTreeNode newNode;
            // parent node of the new node
            DisplayTreeNode parentNode;
            // child index of the new node in the parent node
            int matchCount;
            if (key instanceof MatchResult) {
                MatchResult match = (MatchResult) key;
                Recipe recipe = match.getStep()
                    .getRecipe();
                QualName ruleName = key.getAction()
                    .getQualName();
                // find the correct rule tree node
                parentNode = recipe == null ? this.ruleNodeMap.get(ruleName)
                    : this.subruleNodeMap.get(Duo.newDuo(ruleName, recipe.getQualName()));
                matchCount = parentNode.getChildCount();
                newNode = new MatchTreeNode(getSimulatorModel(), state, match, matchCount + 1,
                    getSimulator().getOptions()
                        .isSelected(Options.SHOW_ANCHORS_OPTION));
            } else {
                RecipeEvent event = (RecipeEvent) key;
                RecipeTreeNode recipeNode = this.recipeNodeMap.get(event.getAction()
                    .getQualName());
                matchCount = recipeNode.getTransitionCount();
                parentNode = recipeNode;

                newNode =
                    new RecipeTransitionTreeNode(getSimulatorModel(), state, event, matchCount + 1);
            }
            this.matchNodeMap.put(key, newNode);
            this.ruleDirectory.insertNodeInto(newNode, parentNode, matchCount);
            expandPath(new TreePath(parentNode.getPath()));
        }
    }

    /** Returns the set of pairs of rule/recipe name that have been tried
     * in the current state.
     */
    private Set<Duo<QualName>> getTried(GraphState state) {
        // set the tried status of the rules
        Set<? extends CallStack> pastAttempts =
            state == null ? Collections.<CallStack>emptySet() : state.getActualFrame()
                .getPastAttempts();
        // convert the transitions to pairs of rule name + recipe name
        Set<Duo<QualName>> triedPairs = new HashSet<>();
        for (CallStack t : pastAttempts) {
            QualName ruleName = t.getRule()
                .getQualName();
            QualName recipeName = t.inRecipe() ? t.getRecipe()
                .getQualName() : null;
            triedPairs.add(Duo.newDuo(ruleName, recipeName));
        }
        return triedPairs;
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
        return (ControlDisplay) getSimulator().getDisplaysPanel()
            .getDisplay(DisplayKind.CONTROL);
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
     * Mapping from rule names in the current grammar to rule nodes in the
     * current rule directory.
     */
    private final Map<QualName,RuleTreeNode> ruleNodeMap = new HashMap<>();
    /**
     * Mapping from recipe names in the current grammar to recipe nodes in the
     * current rule directory.
     */
    private final Map<QualName,RecipeTreeNode> recipeNodeMap = new HashMap<>();
    /**
     * Mapping from {@link MatchResult} in the current LTS to match nodes in the rule
     * directory
     */
    private final Map<GraphTransitionKey,DisplayTreeNode> matchNodeMap = new LinkedHashMap<>();

    /**
     * Mapping from {@link MatchResult} in the current LTS to match nodes in the rule
     * directory
     */
    private final Map<Duo<QualName>,RuleTreeNode> subruleNodeMap = new HashMap<>();

    /** Flag to indicate that the anchor image option listener has been set. */
    private boolean anchorImageOptionListenerSet = false;

    private interface ActionEntry {
        public QualName getQualName();

        public int getPriority();

        public DisplayTreeNode createTreeNode();

        public boolean isEnabled();
    }

    private class RuleEntry implements ActionEntry {
        public RuleEntry(RuleModel model) {
            this.model = model;
        }

        @Override
        public QualName getQualName() {
            return getModel().getQualName();
        }

        @Override
        public int getPriority() {
            return getModel().getPriority();
        }

        @Override
        public RuleTreeNode createTreeNode() {
            RuleTreeNode result = new RuleTreeNode(getParentDisplay(), getQualName());
            RuleTree.this.ruleNodeMap.put(getQualName(), result);
            return result;
        }

        public RuleModel getModel() {
            return this.model;
        }

        @Override
        public boolean isEnabled() {
            return getModel().isEnabled();
        }

        /** The rule wrapped by this entry. */
        private final RuleModel model;
    }

    private class RecipeEntry implements ActionEntry {
        public RecipeEntry(Recipe recipe) {
            super();
            this.recipe = recipe;
        }

        @Override
        public QualName getQualName() {
            return getRecipe().getQualName();
        }

        @Override
        public int getPriority() {
            return getRecipe().getPriority();
        }

        @Override
        public RecipeTreeNode createTreeNode() {
            RecipeTreeNode result = new RecipeTreeNode(getRecipe());
            RuleTree.this.recipeNodeMap.put(getQualName(), result);
            return result;
        }

        @Override
        public boolean isEnabled() {
            return getRecipe().getTemplate() != null;
        }

        public Recipe getRecipe() {
            return this.recipe;
        }

        private final Recipe recipe;
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
                if (node instanceof MatchTreeNode) {
                    // selected tree node is a match (level 2 node)
                    GraphState state = ((MatchTreeNode) node).getSource();
                    MatchResult match = ((MatchTreeNode) node).getMatch();
                    getSimulatorModel().setMatch(state, match);
                    done = true;
                    break;
                }
            }
            for (TreeNode node : selectedNodes) {
                if (node instanceof RecipeTransitionTreeNode) {
                    // selected tree node is a match (level 2 node)
                    GraphTransition trans = ((RecipeTransitionTreeNode) node).getTransition();
                    getSimulatorModel().setTransition(trans);
                    done = true;
                    break;
                }
            }
            if (!done) {
                // otherwise, select a recipe if appropriate
                for (TreeNode node : selectedNodes) {
                    if (node instanceof RecipeTreeNode) {
                        Recipe recipe = ((RecipeTreeNode) node).getRecipe();
                        getSimulatorModel().doSelectSet(ResourceKind.RULE,
                            Collections.<QualName>emptySet());
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
                        getActions().getEditAction(toDisplay.getResource())
                            .execute();
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
                    getActions().getApplyMatchAction()
                        .execute();
                }
            }
        }

        private void maybeShowPopup(MouseEvent evt) {
            if (evt.isPopupTrigger()) {
                TreePath selectedPath = getPathForLocation(evt.getX(), evt.getY());
                TreeNode selectedNode =
                    selectedPath == null ? null : (TreeNode) selectedPath.getLastPathComponent();
                RuleTree.this.requestFocus();
                createPopupMenu(selectedNode).show(evt.getComponent(), evt.getX(), evt.getY());
            }
        }
    }

    /**
     * Directory nodes (priorities of check policies).
     */
    public static class DirectoryTreeNode extends FolderTreeNode {
        /**
         * Creates a new priority node based on a given priority. The node can
         * (and will) have children.
         */
        public DirectoryTreeNode(CheckPolicy policy, int priority, boolean hasMultiple) {
            super(getText(policy, priority, hasMultiple));
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
            if (this.policy == null) {
                result.append("List of modifying rules and recipes");
                if (this.hasMultiple) {
                    result.append(" of priority ");
                    result.append(this.priority);
                    result.append(HTMLConverter.HTML_LINEBREAK);
                    result.append(
                        "Will be scheduled only when all higher-level priority transformers failed");
                }
            } else {
                result.append("List of graph properties, defined by unmodifying rules<br>"
                    + "and checked automatically at every non-transient state,");
                if (this.hasMultiple) {
                    switch (this.policy) {
                    case ERROR:
                        result.append("<br>which, when violated, will flag an error");
                        break;
                    case REMOVE:
                        result
                            .append("<br>which, when violated, will cause the state to be removed");
                        break;
                    default:
                        // no special text
                    }
                }
            }
            return HTMLConverter.HTML_TAG.on(result)
                .toString();
        }

        private final CheckPolicy policy;
        private final int priority;
        private final boolean hasMultiple;

        private static String getText(CheckPolicy policy, int priority, boolean hasMultiple) {
            StringBuilder result = new StringBuilder();
            if (policy == null) {
                if (hasMultiple) {
                    result.append("Priority ");
                    result.append(priority);
                    result.append(" transformers");
                } else {
                    result.append("Transformers");
                }
            } else {
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
            }
            return HTMLConverter.UNDERLINE_TAG.on(result)
                .toString();
        }
    }
}
