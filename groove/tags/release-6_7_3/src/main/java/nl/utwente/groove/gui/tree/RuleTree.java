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
import java.util.function.Supplier;

import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JPopupMenu;
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
import nl.utwente.groove.lts.RecipeTransition;
import nl.utwente.groove.lts.RuleTransition;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.Factory;

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
    void installListeners() {
        super.installListeners();
        getSimulatorModel().addListener(this, STATE, MATCH, RULE);
        getOptions().getItem(Options.SHOW_ANCHORS_OPTION).addItemListener(getOptionsListener());
        getOptions()
            .getItem(Options.SHOW_RECIPE_STEPS_OPTION)
            .addItemListener(getOptionsListener());
        getOptions()
            .getItem(Options.SHOW_ABSENT_STATES_OPTION)
            .addItemListener(getOptionsListener());
    }

    @Override
    void disposeListeners() {
        super.disposeListeners();
        getOptions().removeItemListener(getOptionsListener());
    }

    @Override
    RuleSelectionListener createSelectionListener() {
        return new RuleSelectionListener();
    }

    @Override
    MouseListener createMouseListener() {
        return new MyMouseListener();
    }

    /** Returns the lazily computed options listener. */
    private ItemListener getOptionsListener() {
        return this.optionsListener.get();
    }

    /** Lazily computed options listener. */
    private final Supplier<ItemListener> optionsListener
        = Factory.lazy(this::computeOptionsListener);

    /** Computes the value for {@link #optionsListener}. */
    private ItemListener computeOptionsListener() {
        return e -> {
            if (suspendListening()) {
                if (e.getItem() == Options.SHOW_ANCHORS_OPTION) {
                    refresh(getSimulatorModel().getState());
                } else {
                    loadGrammar(getSimulatorModel().getGrammar());
                    refresh(getSimulatorModel().getState());
                }
                activateListening();
            }
        };
    }

    @Override
    JPopupMenu createPopupMenu(TreeNode node) {
        JPopupMenu res = super.createPopupMenu(node);
        if (node instanceof RuleTreeNode) {
            res.add(getActions().getSetPriorityAction());
            res.add(getActions().getShiftPriorityAction(true));
            res.add(getActions().getShiftPriorityAction(false));
            res.add(getActions().getEditRulePropertiesAction());
        } else if (node instanceof RuleMatchTreeNode) {
            res.addSeparator();
            res.add(getActions().getApplyMatchAction());
        }
        return res;
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel, Set<Change> changes) {
        if (suspendListening()) {
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
                DisplayTreeNode node = null;
                if (source.hasTransition()) {
                    var trans = source.getTransition();
                    if (trans instanceof RuleTransition ruleTrans) {
                        node = this.actionMatchNodeMap.get(ruleTrans.getKey());
                    } else {
                        node = this.recipeTransitionNodeMap
                            .get(((RecipeTransition) trans).getKey());
                    }
                } else if (source.hasMatch()) {
                    var match = source.getMatch();
                    node = this.actionMatchNodeMap.get(match);
                } else {
                    var rule = source.getResource(ResourceKind.RULE);
                    if (rule != null) {
                        node = this.actionNodeMap.get(rule.getQualName());
                    }
                }
                TreePath path = null;
                if (node != null) {
                    path = new TreePath(node.getPath());
                }
                setSelectionPath(path);
                if (path != null) {
                    scrollPathToVisible(path);
                }
            }
            selectSiblings();
            activateListening();
        }
    }

    /**
     * Loads the j-tree with the data of a given grammar.
     * @param grammar the grammar to be loaded; non-{@code null}
     */
    private void loadGrammar(GrammarModel grammar) {
        this.clearAllMaps();
        var absoluteTopNode = this.topDirectoryNode;
        absoluteTopNode.removeAllChildren();
        var topNode = absoluteTopNode;
        var priorityMap = getPriorityMap(grammar);
        var policyMap = getPolicyMap(grammar);
        Set<RuleTreeNode> fragmentSet = getFragments(grammar);
        List<TreePath> expandedPaths = new ArrayList<>();
        List<TreePath> selectedPaths = new ArrayList<>();
        boolean hasMultipleLevels
            = !fragmentSet.isEmpty() || priorityMap.size() + policyMap.size() > 1;
        // add the recipes and rules
        for (var priorityEntry : priorityMap.entrySet()) {
            int priority = priorityEntry.getKey();
            Map<QualName,FolderTreeNode> dirNodeMap = new HashMap<>();
            // if the rule system has multiple priorities, we want an extra
            // level of nodes
            if (hasMultipleLevels) {
                topNode = new DirectoryTreeNode(DirectoryKind.ACTION, null, priority,
                    priorityMap.size() > 1);
                absoluteTopNode.add(topNode);
                dirNodeMap.clear();
            }
            // add the actions to the tree
            for (var node : priorityEntry.getValue()) {
                addActionNode(node, topNode, dirNodeMap, expandedPaths, selectedPaths);
            }
        }
        // add the recipe fragments
        if (!fragmentSet.isEmpty()) {
            topNode = new DirectoryTreeNode(DirectoryKind.FRAGMENT, null, 0, false);
            absoluteTopNode.add(topNode);
            Map<QualName,FolderTreeNode> dirNodeMap = new HashMap<>();
            for (var node : fragmentSet) {
                addActionNode(node, topNode, dirNodeMap, expandedPaths, selectedPaths);
            }
        }
        // add the conditions
        for (var priorityEntry : policyMap.entrySet()) {
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
                absoluteTopNode.add(topNode);
                dirNodeMap.clear();
            }
            // add the property rules to the tree
            for (var node : priorityEntry.getValue()) {
                addActionNode(node, topNode, dirNodeMap, expandedPaths, selectedPaths);
            }
        }
        this.ruleDirectory.reload(absoluteTopNode);
        for (TreePath path : expandedPaths) {
            expandPath(path);
        }
        setSelectionPaths(selectedPaths.toArray(new TreePath[0]));
    }

    /** Clears all maps of the tree. */
    private void clearAllMaps() {
        this.actionNodeMap.clear();
        clearMatchNodeMaps();
    }

    /**
     * Creates a map from priorities to nonempty sets of rule tree nodes with that
     * priority from the rule in a given grammar view.
     * @param grammar the source of the rule map
     */
    private Map<Integer,Set<ActionTreeNode>> getPriorityMap(GrammarModel grammar) {
        Map<Integer,Set<ActionTreeNode>> result = new TreeMap<>(Action.PRIORITY_COMPARATOR);
        var display = getParentDisplay();
        for (Recipe recipe : grammar.getControlModel().getRecipes()) {
            int priority = recipe.getPriority();
            var recipes = result.get(priority);
            if (recipes == null) {
                result.put(priority, recipes = new HashSet<>());
            }
            recipes.add(new RecipeTreeNode(display, recipe));
        }
        for (var model : grammar.getResourceSet(ResourceKind.RULE)) {
            RuleModel rule = (RuleModel) model;
            if (!rule.isProperty() && !rule.hasRecipes()) {
                int priority = rule.getPriority();
                var rules = result.get(priority);
                if (rules == null) {
                    result.put(priority, rules = new HashSet<>());
                }
                rules.add(new RuleTreeNode(display, rule));
            }
        }
        return result;
    }

    /**
     * Creates a set of fragment rules in the grammar.
     * @param grammar the source of the rule map
     */
    private Set<RuleTreeNode> getFragments(GrammarModel grammar) {
        Set<RuleTreeNode> result = new HashSet<>();
        var display = getParentDisplay();
        for (var model : grammar.getResourceSet(ResourceKind.RULE)) {
            RuleModel rule = (RuleModel) model;
            if (rule.hasRecipes()) {
                result.add(new RuleTreeNode(display, rule));
            }
        }
        return result;
    }

    /**
     * Creates a map from check policies to nonempty sets of property rules with that
     * policy.
     * @param grammar the source of the rule map
     */
    private Map<CheckPolicy,Set<ActionTreeNode>> getPolicyMap(GrammarModel grammar) {
        Map<CheckPolicy,Set<ActionTreeNode>> result = new EnumMap<>(CheckPolicy.class);
        var display = getParentDisplay();
        for (ResourceModel<?> model : grammar.getResourceSet(ResourceKind.RULE)) {
            RuleModel ruleModel = (RuleModel) model;
            if (ruleModel.isProperty()) {
                CheckPolicy policy = ruleModel.getPolicy();
                policy = policy == CheckPolicy.OFF
                    ? CheckPolicy.SILENT
                    : policy;
                Set<ActionTreeNode> rules = result.get(policy);
                if (rules == null) {
                    result.put(policy, rules = new HashSet<>());
                }
                rules.add(new RuleTreeNode(display, ruleModel));
            }
        }
        return result;
    }

    /** Adds an action tree node to the tree. */
    private void addActionNode(ActionTreeNode node, DisplayTreeNode topNode,
                               Map<QualName,FolderTreeNode> dirNodeMap,
                               List<TreePath> expandedPaths, List<TreePath> selectedPaths) {
        Collection<QualName> selection = getSimulatorModel().getSelectSet(ResourceKind.RULE);
        QualName name = node.getQualName();
        // create the rule node and register it
        this.actionNodeMap.put(name, node);
        TreePath path = new TreePath(node.getPath());
        expandedPaths.add(path);
        if (selection.contains(name)) {
            selectedPaths.add(path);
        }
        DisplayTreeNode parentNode = addParentNode(topNode, dirNodeMap, name.parent());
        parentNode.insertSorted(node);
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

    /** Clears all maps of the tree. */
    private void clearMatchNodeMaps() {
        var directory = this.ruleDirectory;
        // remove current rule matches and clean up the map
        this.actionMatchNodeMap.values().forEach(directory::removeNodeFromParent);
        this.recipeMatchNodeMap.values().forEach(directory::removeNodeFromParent);
        this.recipeTransitionNodeMap.values().forEach(directory::removeNodeFromParent);
        if (this.ongoingRecipeNode != null) {
            directory.removeNodeFromParent(this.ongoingRecipeNode);
        }
        this.actionMatchNodeMap.clear();
        this.recipeTransitionNodeMap.clear();
        this.recipeMatchNodeMap.clear();
        this.ongoingRecipeNode = null;
    }

    /**
     * Refreshes the match nodes, based on a given match result set.
     * @param matches the set of matches used to create {@link RuleMatchTreeNode}s
     */
    private void refreshMatches(GraphState state, Collection<GraphTransitionKey> matches) {
        clearMatchNodeMaps();
        Collection<ResourceTreeNode> treeNodes = new ArrayList<>();
        var tried = getTried(state);
        // for all action nodes, check if it has been tried
        for (var actionNode : this.actionNodeMap.values()) {
            actionNode.setActivated(tried.contains(actionNode.getQualName()));
            treeNodes.add(actionNode);
        }
        // expand all rule nodes and subsequently collapse all directory nodes
        for (var n : treeNodes) {
            expandPath(new TreePath(n.getPath()));
        }
        for (var n : treeNodes) {
            collapsePath(new TreePath(n.getPath()));
        }
        // flag to detect ongoing recipe transition
        MatchResult ongoingRecipeMatch = null;
        // potential initial recipe matches
        List<MatchResult> potential = new ArrayList<>();
        // insert new matches
        for (GraphTransitionKey key : matches) {
            QualName actionName = key.getAction().getQualName();
            // parent node of the new node
            var parentNode = this.actionNodeMap.get(actionName);
            if (!getGrammar().getControlModel().getRecipes(actionName).isEmpty()
                && !isShowInternal()) {
                // this indicates that the action is a fragment rule and internals are not shown
                // we may still need this as a recipe match
                var match = (MatchResult) key;
                var trans = match.getTransition();
                if (trans == null || !trans.target().isDone()) {
                    // register a potential initial recipe match
                    potential.add(match);
                }
                continue;
            }
            // child index of the new node in the parent node
            int matchCount = parentNode.getChildCount();
            MatchTreeNode newNode;
            if (key instanceof MatchResult match) {
                var node = new RuleMatchTreeNode(getSimulatorModel(), state, match, matchCount + 1,
                    isShowAnchors());
                var recipe = match.getStep().getRecipe();
                if (recipe.isPresent()) {
                    // the match is part of a recipe, but maybe it is already among the recipe transitions
                    var trans = match.getTransition();
                    if (state.isInternalState()) {
                        // register an ongoing transition
                        ongoingRecipeMatch = match;
                    } else if (trans == null || !trans.target().isDone()) {
                        // register a potential initial recipe match
                        potential.add(match);
                    }
                }
                if (node.isAbsent() && !isShowAbsent()) {
                    continue;
                }
                this.actionMatchNodeMap.put(match, node);
                newNode = node;
            } else {
                RecipeEvent event = (RecipeEvent) key;
                var node = new RecipeTransitionTreeNode(getSimulatorModel(), state, event,
                    matchCount + 1);
                if (node.isAbsent() && !isShowAbsent()) {
                    continue;
                }
                this.recipeTransitionNodeMap.put(event, node);
                newNode = node;
            }
            this.ruleDirectory.insertNodeInto(newNode, parentNode, matchCount);
            expandPath(new TreePath(parentNode.getPath()));
        }
        // add the optional ongoing recipe
        if (ongoingRecipeMatch != null) {
            // parent node of the new node
            var recipe = state.getActualFrame().getRecipe().get();
            var parentNode = this.actionNodeMap.get(recipe.getQualName());
            int matchCount = parentNode.getChildCount();
            var newNode = new RecipeOngoingTreeNode(getSimulatorModel(), state, ongoingRecipeMatch,
                matchCount + 1);
            this.ongoingRecipeNode = newNode;
            this.ruleDirectory.insertNodeInto(newNode, parentNode, matchCount);
            expandPath(new TreePath(parentNode.getPath()));
        }
        // add the recipe matches
        for (var match : potential) {
            // parent node of the new node
            var recipe = match.getStep().getRecipe().get();
            var parentNode = this.actionNodeMap.get(recipe.getQualName());
            int matchCount = parentNode.getChildCount();
            var newNode = new RecipeMatchTreeNode(getSimulatorModel(), state, match, matchCount + 1,
                isShowAnchors());
            this.recipeMatchNodeMap.put(match, newNode);
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

    /**
     * Mapping from {@link MatchResult}s in the current state to {@link RuleMatchTreeNode}s in the rule directory
     */
    private final Map<MatchResult,RuleMatchTreeNode> actionMatchNodeMap = new LinkedHashMap<>();
    /**
     * Mapping from {@link MatchResult}s in the current state to {@link RecipeTransitionTreeNode}s in the rule directory
     */
    private final Map<RecipeEvent,RecipeTransitionTreeNode> recipeTransitionNodeMap
        = new LinkedHashMap<>();
    /**
     * Mapping from {@link MatchResult}s in the current state to recipe match nodes in the rule
     * directory
     */
    private final Map<MatchResult,RecipeMatchTreeNode> recipeMatchNodeMap = new LinkedHashMap<>();
    /** Match node for an ongoing recipe, if any. */
    private MatchTreeNode ongoingRecipeNode;

    /** Selects any match nodes from the same state and with the same match key. */
    void selectSiblings() {
        var paths = getSelectionPaths();
        if (paths != null) {
            List<TreePath> siblingPaths = new ArrayList<>();
            for (int i = 0; i < paths.length; i++) {
                var path = paths[i];
                if (path.getLastPathComponent() instanceof MatchTreeNode matchNode) {
                    var key = matchNode.getKey();
                    for (var sibling : getSiblings(key)) {
                        if (sibling == matchNode || matchNode.getClass() != sibling.getClass()) {
                            siblingPaths.add(new TreePath(sibling.getPath()));
                        }
                    }
                } else {
                    siblingPaths.add(path);
                }
            }
            setSelectionPaths(siblingPaths.toArray(TreePath[]::new));
        }
    }

    /** Returns the set of match nodes based on a given graph transition key. */
    private Set<MatchTreeNode> getSiblings(MatchResult key) {
        Set<MatchTreeNode> result = new HashSet<>();
        MatchTreeNode node = this.actionMatchNodeMap.get(key);
        if (node != null) {
            result.add(node);
        }
        node = this.recipeMatchNodeMap.get(key);
        if (node != null) {
            result.add(node);
        }
        for (var transNode : this.recipeTransitionNodeMap.values()) {
            if (transNode.getKey().equals(key)) {
                result.add(transNode);
            }
        }
        node = this.ongoingRecipeNode;
        if (node != null) {
            result.add(node);
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
        void collectResources(DisplayTreeNode node, Collection<DisplayTreeNode> result) {
            if (node instanceof MatchTreeNode) {
                // only select this node itself for now,
                // so we still know which node the user selected
                result.add(node);
            } else {
                super.collectResources(node, result);
            }
        }

        @Override
        void pushSelection(Collection<DisplayTreeNode> selectedNodes) {
            boolean done = false;
            for (TreeNode node : selectedNodes) {
                if (node instanceof RuleMatchTreeNode mtn) {
                    // selected tree node is a match (level 2 node)
                    GraphState state = mtn.getSource();
                    MatchResult match = mtn.getMatch();
                    getSimulatorModel().setMatch(state, match);
                    done = true;
                    break;
                } else if (node instanceof RecipeTransitionTreeNode rttn) {
                    // selected tree node is a match (level 2 node)
                    GraphTransition trans = rttn.getTransition();
                    getSimulatorModel().setTransition(trans);
                    done = true;
                    break;
                } else if (node instanceof RecipeMatchTreeNode rmtn) {
                    var state = rmtn.getSource();
                    var match = rmtn.getInitMatch();
                    getSimulatorModel().setMatch(state, match);
                    done = true;
                    break;
                } else if (node instanceof RecipeOngoingTreeNode rotn) {
                    var state = rotn.getSource();
                    var match = rotn.getInnerMatch();
                    getSimulatorModel().setMatch(state, match);
                    done = true;
                    break;
                }
            }
            if (done) {
                selectSiblings();
            } else {
                // otherwise, select a recipe node if appropriate
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
                super.pushSelection(selectedNodes);
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
                } else if (lastComponent instanceof RuleMatchTreeNode
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
            TreePath path = getPathForLocation(evt.getX(), evt.getY());
            if (path == null) {
                return;
            }
            Object selectedNode = path.getLastPathComponent();
            if (evt.getClickCount() == 2) {
                if (selectedNode instanceof RecipeMatchTreeNode) {
                    getActions().getExploreAction().doExploreState();
                } else if (selectedNode instanceof MatchTreeNode) {
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
    static public class DirectoryTreeNode extends FolderTreeNode {
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
