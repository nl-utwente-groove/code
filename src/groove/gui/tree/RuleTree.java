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
package groove.gui.tree;

import static groove.gui.SimulatorModel.Change.GRAMMAR;
import static groove.gui.SimulatorModel.Change.GTS;
import static groove.gui.SimulatorModel.Change.MATCH;
import static groove.gui.SimulatorModel.Change.RULE;
import static groove.gui.SimulatorModel.Change.STATE;
import groove.control.CalledAction;
import groove.grammar.Action;
import groove.grammar.QualName;
import groove.grammar.Recipe;
import groove.grammar.Rule;
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
import groove.lts.GraphState;
import groove.lts.MatchResult;
import groove.lts.RuleTransition;
import groove.util.Duo;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
import javax.swing.ToolTipManager;
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
        ToolTipManager.sharedInstance().registerComponent(this);
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
            selectMatch((RuleModel) ruleModel, source.getMatch());
        }
        activateListeners();
    }

    /** Clears all maps of the tree. */
    private void clearAllMaps() {
        this.ruleNodeMap.clear();
        this.recipeMap.clear();
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
        List<TreePath> expandedPaths = new ArrayList<TreePath>();
        List<TreePath> selectedPaths = new ArrayList<TreePath>();
        for (Map.Entry<Integer,Set<ActionEntry>> priorityEntry : priorityMap.entrySet()) {
            int priority = priorityEntry.getKey();
            Map<String,FolderTreeNode> dirNodeMap = new HashMap<String,FolderTreeNode>();
            // if the rule system has multiple priorities, we want an extra
            // level of nodes
            if (priorityMap.size() > 1) {
                topNode = new PriorityTreeNode(priority);
                this.topDirectoryNode.add(topNode);
                dirNodeMap.clear();
            }
            // collect entries for all actions
            Map<String,RuleEntry> ruleEntryMap = new HashMap<String,RuleEntry>();
            List<RecipeEntry> recipes = new ArrayList<RecipeEntry>();
            for (ActionEntry action : priorityEntry.getValue()) {
                if (action instanceof RecipeEntry) {
                    recipes.add((RecipeEntry) action);
                    this.recipeMap.put(action.getName(), ((RecipeEntry) action).getRecipe());
                } else {
                    ruleEntryMap.put(action.getName(), (RuleEntry) action);
                }
            }
            List<String> subruleNames = new ArrayList<String>();
            // add the recipes and their subrules to the tree
            for (RecipeEntry recipe : recipes) {
                String name = recipe.getName();
                // recursively add parent directory nodes as required
                DisplayTreeNode parentNode =
                    addParentNode(topNode, dirNodeMap, QualName.getParent(name));
                DisplayTreeNode recipeNode = createActionNode(recipe, expandedPaths, selectedPaths);
                parentNode.insertSorted(recipeNode);
                Set<Rule> subrules = recipe.getRecipe().getRules();
                if (subrules != null) {
                    for (Rule sr : subrules) {
                        String srName = sr.getFullName();
                        RuleEntry srEntry = ruleEntryMap.get(srName);
                        if (srEntry != null) {
                            DisplayTreeNode srNode =
                                createActionNode(srEntry, expandedPaths, selectedPaths);
                            recipeNode.insertSorted(srNode);
                        }
                        subruleNames.add(srName);
                    }
                }
            }
            ruleEntryMap.keySet().removeAll(subruleNames);
            // add the remaining rules to the tree
            for (RuleEntry ruleEntry : ruleEntryMap.values()) {
                String name = ruleEntry.getName();
                // recursively add parent directory nodes as required
                DisplayTreeNode parentNode =
                    addParentNode(topNode, dirNodeMap, QualName.getParent(name));
                DisplayTreeNode ruleNode =
                    createActionNode(ruleEntry, expandedPaths, selectedPaths);
                parentNode.insertSorted(ruleNode);
            }
        }
        for (TreePath path : expandedPaths) {
            expandPath(path);
        }
        this.ruleDirectory.reload(this.topDirectoryNode);
        for (TreePath path : expandedPaths) {
            expandPath(path);
        }
        setSelectionPaths(selectedPaths.toArray(new TreePath[0]));
    }

    private DisplayTreeNode createActionNode(ActionEntry action, List<TreePath> expandedPaths,
            List<TreePath> selectedPaths) {
        Collection<String> selection = getSimulatorModel().getSelectSet(ResourceKind.RULE);
        String name = action.getName();
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
        Map<Integer,Set<ActionEntry>> result =
            new TreeMap<Integer,Set<ActionEntry>>(Action.PRIORITY_COMPARATOR);
        Set<String> subRuleNames = new HashSet<String>();
        for (Recipe recipe : grammar.getControlModel().getRecipes()) {
            int priority = 0;
            Set<ActionEntry> recipes = result.get(priority);
            if (recipes == null) {
                result.put(priority, recipes = new HashSet<ActionEntry>());
            }
            recipes.add(new RecipeEntry(recipe));
            Set<Rule> subrules = recipe.getRules();
            if (subrules != null) {
                for (Rule subrule : subrules) {
                    if (subrule.getPriority() == priority) {
                        String ruleName = subrule.getFullName();
                        recipes.add(new RuleEntry(grammar.getRuleModel(ruleName)));
                        subRuleNames.add(ruleName);
                    }
                }
            }
        }
        for (ResourceModel<?> model : grammar.getResourceSet(ResourceKind.RULE)) {
            RuleModel ruleModel = (RuleModel) model;
            if (!subRuleNames.contains(ruleModel.getFullName())) {
                int priority = ruleModel.getPriority();
                Set<ActionEntry> rules = result.get(priority);
                if (rules == null) {
                    result.put(priority, rules = new HashSet<ActionEntry>());
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
            JMenuItem showAnchorsOptionItem =
                getSimulator().getOptions().getItem(Options.SHOW_ANCHORS_OPTION);
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
            Map<String,FolderTreeNode> dirNodeMap, String parentName) {
        //        QualName parent = ruleName.parent();
        if (parentName.isEmpty()) {
            // there is no parent rule name; the parent node is the top node
            return topNode;
        } else {
            // there is a proper parent rule; look it up in the node map
            FolderTreeNode result = dirNodeMap.get(parentName);
            if (result == null) {
                // the parent node did not yet exist in the tree
                // check recursively for the grandparent
                DisplayTreeNode grandParentNode =
                    addParentNode(topNode, dirNodeMap, QualName.getParent(parentName));
                // make the parent node and register it
                result = new FolderTreeNode(QualName.getLastName(parentName));
                grandParentNode.insertSorted(result);
                dirNodeMap.put(parentName, result);
            }
            return result;
        }
    }

    /**
     * Refreshes the selection in the tree, based on the current state of the
     * Simulator.
     */
    private void refresh(GraphState state) {
        SortedSet<MatchResult> matches = new TreeSet<MatchResult>(MatchResult.COMPARATOR);
        if (state != null) {
            for (RuleTransition trans : state.getRuleTransitions()) {
                matches.add(trans.getKey());
            }
            matches.addAll(state.getMatches());
        }
        refreshMatches(state, matches);
        setEnabled(getGrammar() != null);
    }

    /**
     * Selects the row of a given match result, or if that is {@code null}, a
     * given rule.
     * @param rule the rule to be selected if the event is {@code null}
     * @param match the match result to be selected
     */
    private void selectMatch(RuleModel rule, MatchResult match) {
        List<DefaultMutableTreeNode> treeNodes = new ArrayList<DefaultMutableTreeNode>();
        if (match != null) {
            MatchTreeNode node = this.matchNodeMap.get(match);
            if (node != null) {
                treeNodes.add(node);
            }
        }
        if (treeNodes.isEmpty() && rule != null) {
            treeNodes.addAll(this.ruleNodeMap.get(rule.getFullName()));
        }
        TreePath[] paths = new TreePath[treeNodes.size()];
        for (int i = 0; i < treeNodes.size(); i++) {
            paths[i] = new TreePath(treeNodes.get(i).getPath());
        }
        setSelectionPaths(paths);
    }

    /**
     * Refreshes the match nodes, based on a given match result set.
     * @param matches the set of matches used to create {@link MatchTreeNode}s
     */
    private void refreshMatches(GraphState state, Collection<? extends MatchResult> matches) {
        // remove current matches
        for (MatchTreeNode matchNode : this.matchNodeMap.values()) {
            this.ruleDirectory.removeNodeFromParent(matchNode);
        }
        // clean up current match node map
        this.matchNodeMap.clear();
        // set the tried status of the rules
        Set<? extends CalledAction> pastAttempts =
            state == null ? Collections.<CalledAction>emptySet()
                    : state.getActualFrame().getPastAttempts();
        // convert the transitions to pairs of rule name + recipe name
        Set<Duo<String>> triedPairs = new HashSet<Duo<String>>();
        for (CalledAction t : pastAttempts) {
            String ruleName = t.getRule().getFullName();
            String recipeName = t.inRecipe() ? t.getRecipe().getFullName() : null;
            triedPairs.add(Duo.newDuo(ruleName, recipeName));
        }
        Collection<RuleTreeNode> treeNodes = new ArrayList<RuleTreeNode>();
        // for all nodes, check if their rule/recipe pair has been tried
        for (Collection<RuleTreeNode> nodes : this.ruleNodeMap.values()) {
            treeNodes.addAll(nodes);
            for (RuleTreeNode n : nodes) {
                String ruleName = n.getName();
                Recipe ruleRecipe = getRecipe(n);
                String recipeName = ruleRecipe == null ? null : ruleRecipe.getFullName();
                boolean tried = triedPairs.contains(Duo.newDuo(ruleName, recipeName));
                n.setTried(tried);
            }
        }
        // expand all rule nodes and subsequently collapse all directory nodes
        for (RuleTreeNode n : treeNodes) {
            expandPath(new TreePath(n.getPath()));
        }
        for (RuleTreeNode n : treeNodes) {
            collapsePath(new TreePath(n.getPath()));
        }
        // recollect the match results so that they are ordered according to the
        // rule events
        // insert new matches
        for (MatchResult match : matches) {
            Rule rule = match.getEvent().getRule();
            Recipe recipe = match.getStep().getRecipe();
            String ruleName = rule.getFullName();
            // find the correct rule tree node
            for (RuleTreeNode ruleNode : this.ruleNodeMap.get(ruleName)) {
                if (recipe == null || recipe.equals(getRecipe(ruleNode))) {
                    int nrOfMatches = ruleNode.getChildCount();
                    MatchTreeNode matchNode =
                        new MatchTreeNode(getSimulatorModel(), state, match, nrOfMatches + 1,
                            getSimulator().getOptions().isSelected(Options.SHOW_ANCHORS_OPTION));
                    this.ruleDirectory.insertNodeInto(matchNode, ruleNode, nrOfMatches);
                    expandPath(new TreePath(ruleNode.getPath()));
                    this.matchNodeMap.put(match, matchNode);
                }
            }
        }
    }

    /** Returns the name of the recipe in which a given rule node is empedded, if any. */
    private Recipe getRecipe(RuleTreeNode ruleNode) {
        Recipe result = null;
        TreeNode parent = ruleNode.getParent();
        if (parent instanceof RecipeTreeNode) {
            result = ((RecipeTreeNode) parent).getRecipe();
        }
        return result;
    }

    @Override
    public String convertValueToText(Object value, boolean selected, boolean expanded,
            boolean leaf, int row, boolean hasFocus) {
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
     * Mapping from rule names in the current grammar to rule nodes in the
     * current rule directory.
     */
    private final Map<String,Collection<RuleTreeNode>> ruleNodeMap =
        new HashMap<String,Collection<RuleTreeNode>>();
    /**
     * Mapping from action names in the current grammar to entries in this tree.
     */
    private final Map<String,Recipe> recipeMap = new HashMap<String,Recipe>();

    /**
     * Mapping from RuleMatches in the current LTS to match nodes in the rule
     * directory
     */
    private final Map<MatchResult,MatchTreeNode> matchNodeMap =
        new HashMap<MatchResult,MatchTreeNode>();

    /** Flag to indicate that the anchor image option listener has been set. */
    private boolean anchorImageOptionListenerSet = false;

    private interface ActionEntry {
        public String getName();

        public int getPriority();

        public DisplayTreeNode createTreeNode();

        public boolean isEnabled();
    }

    private class RuleEntry implements ActionEntry {
        public RuleEntry(RuleModel model) {
            this.model = model;
        }

        @Override
        public String getName() {
            return getModel().getFullName();
        }

        @Override
        public int getPriority() {
            return getModel().getPriority();
        }

        @Override
        public RuleTreeNode createTreeNode() {
            RuleTreeNode result = new RuleTreeNode(getParentDisplay(), getName());
            Collection<RuleTreeNode> nodes = RuleTree.this.ruleNodeMap.get(getName());
            if (nodes == null) {
                RuleTree.this.ruleNodeMap.put(getName(), nodes = new ArrayList<RuleTreeNode>());
            }
            nodes.add(result);
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
        public String getName() {
            return getRecipe().getFullName();
        }

        @Override
        public int getPriority() {
            return 0;
        }

        @Override
        public RecipeTreeNode createTreeNode() {
            return new RecipeTreeNode(getRecipe());
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
            if (!done) {
                // otherwise, select a recipe if appropriate
                for (TreeNode node : selectedNodes) {
                    if (node instanceof RecipeTreeNode) {
                        String name = ((RecipeTreeNode) node).getName();
                        Recipe recipe = RuleTree.this.recipeMap.get(name);
                        getSimulatorModel().doSelectSet(ResourceKind.RULE,
                            Collections.<String>emptySet());
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
            if (selectedNode instanceof MatchTreeNode) {
                if (evt.getClickCount() == 2) {
                    getActions().getApplyMatchAction().execute();
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
     * Priority nodes (used only if the rule system has multiple priorities)
     */
    private static class PriorityTreeNode extends FolderTreeNode {
        /**
         * Creates a new priority node based on a given priority. The node can
         * (and will) have children.
         */
        public PriorityTreeNode(int priority) {
            super("Priority " + priority);
        }

        @Override
        public Icon getIcon() {
            return Icons.EMPTY_ICON;
        }
    }
}
