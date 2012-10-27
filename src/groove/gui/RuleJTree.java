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
import groove.control.CtrlAut;
import groove.control.CtrlTransition;
import groove.gui.SimulatorModel.Change;
import groove.gui.action.ActionStore;
import groove.lts.GraphState;
import groove.lts.MatchResult;
import groove.lts.RuleTransition;
import groove.trans.Action;
import groove.trans.QualName;
import groove.trans.Recipe;
import groove.trans.ResourceKind;
import groove.trans.Rule;
import groove.util.Duo;
import groove.util.Strings;
import groove.view.GrammarModel;
import groove.view.ResourceModel;
import groove.view.RuleModel;

import java.awt.Color;
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
import java.util.Enumeration;
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
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
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
    protected RuleJTree(RuleDisplay display) {
        this.display = display;
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
        getSelectionModel().setSelectionMode(
            TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        // set icons
        DefaultTreeCellRenderer renderer =
            (DefaultTreeCellRenderer) this.cellRenderer;
        renderer.setLeafIcon(Icons.GRAPH_MATCH_ICON);
        this.topDirectoryNode = new DefaultMutableTreeNode();
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

    /** Clears all maps of the tree. */
    private void clearAllMaps() {
        this.ruleNodeMap.clear();
        this.actionMap.clear();
        this.clearMatchMaps();
    }

    /** Clears the match maps of the tree. */
    protected void clearMatchMaps() {
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
        DefaultMutableTreeNode topNode = this.topDirectoryNode;
        Map<Integer,Set<ActionEntry>> priorityMap = getPriorityMap(grammar);
        List<TreePath> expandedPaths = new ArrayList<TreePath>();
        List<TreePath> selectedPaths = new ArrayList<TreePath>();
        for (Map.Entry<Integer,Set<ActionEntry>> priorityEntry : priorityMap.entrySet()) {
            int priority = priorityEntry.getKey();
            Map<String,DirectoryTreeNode> dirNodeMap =
                new HashMap<String,DirectoryTreeNode>();
            // if the rule system has multiple priorities, we want an extra
            // level of nodes
            if (priorityMap.size() > 1) {
                topNode = new PriorityTreeNode(priority);
                this.topDirectoryNode.add(topNode);
                dirNodeMap.clear();
            }
            // collect entries for all actions
            Map<String,RuleEntry> ruleEntryMap =
                new HashMap<String,RuleEntry>();
            List<RecipeEntry> recipes = new ArrayList<RecipeEntry>();
            for (ActionEntry action : priorityEntry.getValue()) {
                if (action instanceof RecipeEntry) {
                    recipes.add((RecipeEntry) action);
                } else {
                    ruleEntryMap.put(action.getName(), ((RuleEntry) action));
                }
            }
            List<String> subruleNames = new ArrayList<String>();
            // add the recipes and their subrules to the tree
            for (RecipeEntry recipe : recipes) {
                String name = recipe.getName();
                // recursively add parent directory nodes as required
                DefaultMutableTreeNode parentNode =
                    addParentNode(topNode, dirNodeMap, QualName.getParent(name));
                DisplayTreeNode recipeNode =
                    createActionNode(recipe, expandedPaths, selectedPaths);
                addSortedNode(parentNode, recipeNode);
                CtrlAut body = recipe.getRecipe().getBody();
                if (body != null) {
                    Set<Rule> subrules = body.getRules();
                    if (subrules != null) {
                        for (Rule sr : subrules) {
                            String srName = sr.getFullName();
                            RuleEntry srEntry = ruleEntryMap.get(srName);
                            DisplayTreeNode srNode =
                                createActionNode(srEntry, expandedPaths,
                                    selectedPaths);
                            if (srNode != null) {
                                addSortedNode(recipeNode, srNode);
                            }
                            subruleNames.add(srName);
                        }
                    }
                }
            }
            ruleEntryMap.keySet().removeAll(subruleNames);
            // add the remaining rules to the tree
            for (RuleEntry ruleEntry : ruleEntryMap.values()) {
                String name = ruleEntry.getName();
                // recursively add parent directory nodes as required
                DefaultMutableTreeNode parentNode =
                    addParentNode(topNode, dirNodeMap, QualName.getParent(name));
                DisplayTreeNode ruleNode =
                    createActionNode(ruleEntry, expandedPaths, selectedPaths);
                addSortedNode(parentNode, ruleNode);
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

    private DisplayTreeNode createActionNode(ActionEntry action,
            List<TreePath> expandedPaths, List<TreePath> selectedPaths) {
        Collection<String> selection =
            getSimulatorModel().getSelectSet(ResourceKind.RULE);
        String name = action.getName();
        this.actionMap.put(name, action);
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
            CtrlAut body = recipe.getBody();
            if (body != null) {
                for (Rule subRule : body.getRules()) {
                    if (subRule.getPriority() == priority) {
                        String ruleName = subRule.getFullName();
                        recipes.add(new RuleEntry(
                            grammar.getRuleModel(ruleName)));
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

    /** Unhooks this object from all observables. */
    public void dispose() {
        getSimulatorModel().removeListener(this);
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel,
            Set<Change> changes) {
        suspendListeners();
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
        } else {
            if (changes.contains(GTS) || changes.contains(STATE)) {
                // if the GTS has changed, this may mean that the state 
                // displayed here has been closed, in which case we have to refresh
                // since the rule events have been changed into transitions
                refresh(source.getState());
            }
            if (changes.contains(MATCH) || changes.contains(RULE)) {
                ResourceModel<?> ruleModel =
                    source.getResource(ResourceKind.RULE);
                selectMatch((RuleModel) ruleModel, source.getMatch());
            }
        }
        activateListeners();
    }

    private void installListeners() {
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
        getSimulatorModel().addListener(this, GRAMMAR, GTS, STATE, RULE, MATCH);
        this.listening = true;
    }

    private void suspendListeners() {
        if (!this.listening) {
            throw new IllegalStateException();
        }
        removeTreeSelectionListener(createRuleSelectionListener());
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

    /** Returns the list of currently selected action names. */
    private Set<String> getSelectedActions() {
        Set<String> result = new HashSet<String>();
        int[] selectedRows = getSelectionRows();
        if (selectedRows != null) {
            for (int selectedRow : selectedRows) {
                Object[] nodes = getPathForRow(selectedRow).getPath();
                for (int i = nodes.length - 1; i >= 0; i--) {
                    if (!(nodes[i] instanceof MatchTreeNode)) {
                        collectActions((TreeNode) nodes[i], result);
                        break;
                    }
                }
            }
        }
        return result;
    }

    /** Collects all action names corresponding to a given tree node or its children. */
    private void collectActions(TreeNode node, Set<String> result) {
        if (node instanceof RuleTreeNode) {
            result.add(((RuleTreeNode) node).getName());
        } else if (node instanceof RecipeTreeNode) {
            result.add(((RecipeTreeNode) node).getName());
        } else if (node instanceof PriorityTreeNode
            || node instanceof DirectoryTreeNode) {
            for (int i = 0; i < node.getChildCount(); i++) {
                collectActions(node.getChildAt(i), result);
            }
        }
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
            Map<String,DirectoryTreeNode> dirNodeMap, String parentName) {
        //        QualName parent = ruleName.parent();
        if (parentName.isEmpty()) {
            // there is no parent rule name; the parent node is the top node
            return topNode;
        } else {
            // there is a proper parent rule; look it up in the node map
            DirectoryTreeNode result = dirNodeMap.get(parentName);
            if (result == null) {
                // the parent node did not yet exist in the tree
                // check recursively for the grandparent
                DefaultMutableTreeNode grandParentNode =
                    addParentNode(topNode, dirNodeMap,
                        QualName.getParent(parentName));
                // make the parent node and register it
                result =
                    new DirectoryTreeNode(QualName.getLastName(parentName));
                addSortedNode(grandParentNode, result);
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
        SortedSet<MatchResult> matches =
            new TreeSet<MatchResult>(MatchResult.COMPARATOR);
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
        List<DefaultMutableTreeNode> treeNodes =
            new ArrayList<DefaultMutableTreeNode>();
        if (match != null) {
            treeNodes.add(this.matchNodeMap.get(match));
        } else if (rule != null) {
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
    private void refreshMatches(GraphState state,
            Collection<? extends MatchResult> matches) {
        // remove current matches
        for (MatchTreeNode matchNode : this.matchNodeMap.values()) {
            this.ruleDirectory.removeNodeFromParent(matchNode);
        }
        // clean up current match node map
        this.clearMatchMaps();
        // set the tried status of the rules
        Set<CtrlTransition> triedTransitions =
            state == null ? Collections.<CtrlTransition>emptySet()
                    : state.getSchedule().getTriedTransitions();
        // convert the transitions to pairs of rule name + recipe name
        Set<Duo<String>> triedPairs = new HashSet<Duo<String>>();
        for (CtrlTransition t : triedTransitions) {
            String ruleName = t.getRule().getFullName();
            String recipeName =
                t.hasRecipe() ? t.getRecipe().getFullName() : null;
            triedPairs.add(Duo.newDuo(ruleName, recipeName));
        }
        Collection<RuleTreeNode> treeNodes = new ArrayList<RuleTreeNode>();
        // for all nodes, check if their rule/recipe pair has been tried
        for (Collection<RuleTreeNode> nodes : this.ruleNodeMap.values()) {
            treeNodes.addAll(nodes);
            for (RuleTreeNode n : nodes) {
                String ruleName = n.getName();
                Recipe recipe = n.getRecipe();
                String recipeName =
                    recipe == null ? null : recipe.getFullName();
                boolean tried =
                    triedPairs.contains(Duo.newDuo(ruleName, recipeName));
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
            Recipe recipe = match.getCtrlTransition().getRecipe();
            String ruleName = rule.getFullName();
            // find the correct rule tree node
            for (RuleTreeNode ruleNode : this.ruleNodeMap.get(ruleName)) {
                if (ruleNode.hasRecipe() ? ruleNode.getRecipe().equals(recipe)
                        : recipe == null) {
                    int nrOfMatches = ruleNode.getChildCount();
                    MatchTreeNode matchNode =
                        new MatchTreeNode(getSimulatorModel(), state, match,
                            nrOfMatches + 1,
                            getSimulator().getOptions().isSelected(
                                Options.SHOW_ANCHORS_OPTION));
                    this.ruleDirectory.insertNodeInto(matchNode, ruleNode,
                        nrOfMatches);
                    expandPath(new TreePath(ruleNode.getPath()));
                    this.matchNodeMap.put(match, matchNode);
                }
            }
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
        boolean overRule = node instanceof RuleTreeNode;
        JPopupMenu res = this.display.createListPopupMenu(overRule);
        if (overRule) {
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
    public String convertValueToText(Object value, boolean selected,
            boolean expanded, boolean leaf, int row, boolean hasFocus) {
        String result;
        if (value instanceof DisplayTreeNode) {
            result = ((DisplayTreeNode) value).getText();
        } else {
            result =
                super.convertValueToText(value, selected, expanded, leaf, row,
                    hasFocus);
        }
        return result;
    }

    /**
     * Insert child node into parent using a sorting based on the name of the child node (toString)
     * Uses a natural ordering sort
     * @param parent Node to add child to in a sorted order
     * @param child Child node to insert.
     */
    private void addSortedNode(MutableTreeNode parent, MutableTreeNode child) {
        Comparator<String> comparator = Strings.getNaturalComparator();
        String childString = child.toString();
        @SuppressWarnings("unchecked")
        Enumeration<MutableTreeNode> enumParent = parent.children();
        int index = 0;
        while (enumParent.hasMoreElements()) {
            MutableTreeNode nextChild = enumParent.nextElement();
            int compare = comparator.compare(nextChild.toString(), childString);
            if (compare > 0) {
                break;
            }
            index++;
        }
        parent.insert(child, index);
    }

    /** Convenience method to retrieve the current grammar view. */
    private final GrammarModel getGrammar() {
        return getSimulatorModel().getGrammar();
    }

    /** Returns the associated simulator. */
    private final Simulator getSimulator() {
        return this.display.getSimulator();
    }

    /** Convenience method to retrieve the simulator model. */
    private final SimulatorModel getSimulatorModel() {
        return this.display.getSimulatorModel();
    }

    /** Convenience method to retrieve the simulator action store. */
    private final ActionStore getActions() {
        return this.display.getActions();
    }

    /** Convenience method to retrieve the control display. */
    private final ControlDisplay getControlDisplay() {
        return getSimulator().getDisplaysPanel().getControlDisplay();
    }

    private final RuleDisplay display;

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
    private final Map<String,Collection<RuleTreeNode>> ruleNodeMap =
        new HashMap<String,Collection<RuleTreeNode>>();
    /**
     * Mapping from action names in the current grammar to entries in this tree.
     */
    private final Map<String,ActionEntry> actionMap =
        new HashMap<String,ActionEntry>();

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
            RuleTreeNode result =
                new RuleTreeNode(RuleJTree.this.display, getName());
            Collection<RuleTreeNode> nodes =
                RuleJTree.this.ruleNodeMap.get(getName());
            if (nodes == null) {
                RuleJTree.this.ruleNodeMap.put(getName(), nodes =
                    new ArrayList<RuleTreeNode>());
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
            return getRecipe().getBody() != null;
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
            suspendListeners();
            TreePath[] paths = getSelectionPaths();
            boolean done = false;
            // select a match if appropriate
            for (int i = 0; paths != null && i < paths.length; i++) {
                Object selectedNode = paths[i].getLastPathComponent();
                if (selectedNode instanceof MatchTreeNode) {
                    // selected tree node is a match (level 2 node)
                    MatchResult result =
                        ((MatchTreeNode) selectedNode).getMatch();
                    getSimulatorModel().setMatch(result);
                    done = true;
                    break;
                }
            }
            Set<String> selectedRules = new HashSet<String>();
            if (!done) {
                // otherwise, select a recipe if appropriate
                for (String actionName : getSelectedActions()) {
                    ActionEntry action =
                        RuleJTree.this.actionMap.get(actionName);
                    assert action != null;
                    if (action instanceof RuleEntry) {
                        selectedRules.add(actionName);
                    } else {
                        Recipe recipe = ((RecipeEntry) action).getRecipe();
                        getSimulatorModel().doSelectSet(ResourceKind.RULE,
                            Collections.<String>emptySet());
                        getSimulatorModel().doSelect(ResourceKind.CONTROL,
                            recipe.getControlName());
                        TextTab controlTab =
                            (TextTab) getControlDisplay().getSelectedTab();
                        controlTab.select(recipe.getStartLine(), 0);
                        done = true;
                    }
                }
            }
            if (!done) {
                // otherwise, select rules
                getSimulatorModel().doSelectSet(ResourceKind.RULE,
                    selectedRules);
            }
            activateListeners();
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
                if (evt.getButton() == MouseEvent.BUTTON3
                    && !isRowSelected(getRowForPath(path))) {
                    setSelectionPath(path);
                }
                DisplayKind toDisplay = null;
                Object lastComponent = path.getLastPathComponent();
                if (lastComponent instanceof RuleTreeNode) {
                    toDisplay = DisplayKind.RULE;
                } else if (lastComponent instanceof RecipeTreeNode) {
                    toDisplay = DisplayKind.CONTROL;
                } else if (lastComponent instanceof MatchTreeNode) {
                    toDisplay = DisplayKind.LTS;
                }
                if (evt.getClickCount() == 1 && toDisplay != null) {
                    getSimulatorModel().setDisplay(toDisplay);
                } else if (evt.getClickCount() == 2 && toDisplay != null) {
                    ResourceKind kind = toDisplay.getResource();
                    if (kind != null) {
                        getActions().getEditAction(kind).execute();
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
     * Directory nodes (= level 0 nodes) of the directory
     */
    private static class DirectoryTreeNode extends DefaultMutableTreeNode {
        /**
         * Creates a new directory node with a given name.
         */
        public DirectoryTreeNode(String name) {
            super(name, true);
        }

        /**
         * Convenience method to retrieve the user object as a rule name.
         */
        public String name() {
            return (String) getUserObject();
        }

        /**
         * To display, show child name only
         */
        @Override
        public String toString() {
            return name();
        }
    }

}
