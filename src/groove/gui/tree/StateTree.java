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

import static groove.grammar.model.ResourceKind.RULE;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import groove.grammar.Action;
import groove.grammar.QualName;
import groove.grammar.Recipe;
import groove.grammar.Rule;
import groove.grammar.model.ResourceKind;
import groove.grammar.model.RuleModel;
import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.SimulatorListener;
import groove.gui.SimulatorModel;
import groove.gui.SimulatorModel.Change;
import groove.gui.action.ActionStore;
import groove.gui.display.DisplayKind;
import groove.gui.display.ResourceDisplay;
import groove.gui.jgraph.JAttr;
import groove.io.HTMLConverter;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.GraphTransition.Claz;
import groove.lts.GraphTransitionKey;
import groove.lts.MatchResult;
import groove.lts.RecipeEvent;
import groove.lts.RecipeTransition;
import groove.lts.StartGraphState;
import groove.transform.RuleEvent;

/**
 * List of states in the LTS.
 * @author Arend Rensink
 * @version $Revision $
 */
public class StateTree extends JTree implements SimulatorListener {
    /**
     * Creates a new state list.
     */
    public StateTree(Simulator simulator) {
        this.simulator = simulator;
        setEnabled(false);
        setLargeModel(true);
        setRootVisible(false);
        setShowsRootHandles(true);
        setEnabled(false);
        setToggleClickCount(0);
        setModel(getModel());
        getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        this.setCellRenderer(new DisplayTreeCellRenderer(this));
        installListeners();
        ToolTipManager.sharedInstance()
            .registerComponent(this);
    }

    @Override
    public DefaultTreeModel getModel() {
        return this.treeModel;
    }

    /** Returns the fixed top node of the tree. */
    private DefaultMutableTreeNode getTopNode() {
        return this.topNode;
    }

    /** Installs all listeners, and sets the listening status to {@code true}. */
    protected void installListeners() {
        getSimulatorModel().addListener(this, Change.GTS, Change.STATE, Change.MATCH, Change.TRACE);
        addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent e) {
                StateTree.this.repaint();
            }

            @Override
            public void focusGained(FocusEvent e) {
                StateTree.this.repaint();
            }
        });
        addTreeWillExpandListener(new TreeWillExpandListener() {
            @Override
            public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
                Object lastComponent = event.getPath()
                    .getLastPathComponent();
                if (!this.busy && lastComponent instanceof RangeTreeNode) {
                    this.busy = true;
                    fill((RangeTreeNode) lastComponent);
                    this.busy = false;
                }
            }

            @Override
            public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
                // do nothing
            }

            private boolean busy = false;
        });
        addTreeSelectionListener(new StateSelectionListener());
        addMouseListener(new StateMouseListener());
        // listen to the option controlling the rule anchor display
        ItemListener refreshListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (suspendListening()) {
                    SimulatorModel model = getSimulatorModel();
                    refreshList(model.getGTS(), model.getState());
                    refreshSelection(model.getState(),
                        (RuleModel) model.getResource(RULE),
                        model.getMatch(),
                        model.getTransition());
                    activateListening();
                }
            }
        };
        getOptions().getItem(Options.SHOW_ANCHORS_OPTION)
            .addItemListener(refreshListener);
        getOptions().getItem(Options.SHOW_RECIPE_STEPS_OPTION)
            .addItemListener(refreshListener);
        getOptions().getItem(Options.SHOW_ABSENT_STATES_OPTION)
            .addItemListener(refreshListener);
        activateListening();
    }

    /**
     * Sets the listening status to {@code false}, if it was not already {@code false}.
     * @return {@code true} if listening was suspended as a result of this call;
     * {@code false} if it was already suspended.
     */
    protected final boolean suspendListening() {
        boolean result = this.listening;
        if (result) {
            this.listening = false;
        }
        return result;
    }

    /** Sets the listening flag to {@code true}. */
    protected final void activateListening() {
        if (this.listening) {
            throw new IllegalStateException();
        }
        this.listening = true;
    }

    /** Returns the listening status. */
    protected final boolean isListening() {
        return this.listening;
    }

    /** Indicates if internal states and transitions should be included. */
    private boolean isShowInternal() {
        return getOptions().isSelected(Options.SHOW_RECIPE_STEPS_OPTION);
    }

    /** Indicates if absent states and transitions should be included. */
    private boolean isShowAbsent() {
        return getOptions().isSelected(Options.SHOW_ABSENT_STATES_OPTION);
    }

    /**
     * In addition to delegating the method to <tt>super</tt>, sets the
     * background colour to <tt>null</tt> when disabled and back to the default
     * when enabled.
     */
    @Override
    public void setEnabled(boolean enabled) {
        if (enabled != isEnabled()) {
            setBackground(enabled ? JAttr.STATE_BACKGROUND : null);
        }
        super.setEnabled(enabled);
    }

    /**
     * Creates a popup menu for this panel.
     * @param node the node for which the menu is created
     */
    private JPopupMenu createPopupMenu(TreeNode node) {
        JPopupMenu result = new JPopupMenu();
        if (node instanceof StateTreeNode) {
            result.add(getActions().getEditStateAction());
            result.add(getActions().getSaveStateAction());
            result.add(getActions().getExportStateAction());
        } else if (node instanceof MatchTreeNode) {
            result.add(getActions().getApplyMatchAction());
        }
        return result;
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel, Set<Change> changes) {
        if (suspendListening()) {
            if (changes.contains(Change.GTS)) {
                setEnabled(source.hasGTS());
                refreshList(source.getGTS(), oldModel.getState());
            }
            RuleModel ruleModel = (RuleModel) source.getResource(RULE);
            if (changes.contains(Change.TRACE)) {
                Set<GraphState> refreshables = new HashSet<>();
                if (!changes.contains(Change.GTS)) {
                    for (GraphTransition trans : oldModel.getTrace()) {
                        refreshables.add(trans.source());
                    }
                }
                for (GraphTransition trans : source.getTrace()) {
                    refreshables.add(trans.source());
                }
                refreshStates(refreshables);
            }
            refreshSelection(source.getState(),
                ruleModel,
                source.getMatch(),
                source.getTransition());
            activateListening();
        }
    }

    /**
     * Refreshes the rendering of the states in a given set.
     */
    private void refreshStates(Set<GraphState> refreshables) {
        for (GraphState state : refreshables) {
            StateTreeNode node = getStateNode(state);
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
            int index = parent.getIndex(node);
            node.removeFromParent();
            StateTreeNode newNode = createStateNode(state);
            parent.insert(newNode, index);
            getModel().reload(parent);
            setStateExpanded(newNode);
        }
    }

    /**
     * Refreshes the tree model from the GTS.
     */
    private void refreshList(GTS gts, GraphState previous) {
        if (gts == null) {
            getTopNode().removeAllChildren();
            getModel().reload();
            this.states = new GraphState[0];
        } else {
            // check expansion of current states
            if (previous != null) {
                StateTreeNode stateNode = getStateNode(previous);
                if (stateNode != null && isExpanded(createPath(stateNode))) {
                    this.expanded.add(previous);
                    if (this.expanded.size() > MAX_EXPANDED) {
                        this.expanded.poll();
                    }
                }
            }
            getTopNode().removeAllChildren();
            this.states = new GraphState[gts.nodeCount()];
            // list of ranges, with a single remembered state per range
            GraphState[] ranges = new GraphState[gts.nodeCount() / RANGE_SIZE + 1];
            int rangeCount = 0;
            for (GraphState state : gts.nodeSet()) {
                if (state.isAbsent() && !isShowAbsent()) {
                    continue;
                }
                if (state.isInternalState() && !isShowInternal()) {
                    continue;
                }
                int stateNr = state.getNumber();
                this.states[stateNr] = state;
                int rangeNr = stateNr / RANGE_SIZE;
                if (ranges[rangeNr] == null) {
                    ranges[rangeNr] = state;
                    rangeCount++;
                }
            }
            // only add range nodes if there are too many states
            if (rangeCount > 1) {
                for (int i = 0; i < ranges.length; i++) {
                    if (ranges[i] != null) {
                        RangeTreeNode rangeNode = new RangeTreeNode(i * RANGE_SIZE);
                        // fill the range node to make it expandable
                        rangeNode.add(createStateNode(ranges[i]));
                        getTopNode().add(rangeNode);
                    }
                }
                getModel().reload();
                // collapse all range nodes
                for (int i = 0; i < getTopNode().getChildCount(); i++) {
                    collapsePath(createPath((RangeTreeNode) getTopNode().getFirstChild()));
                }
            } else {
                fill(getTopNode());
            }
        }
        setEnabled(gts != null);
    }

    /**
     * Fills a parent node with a range of state nodes.
     * Also updates the model and makes sure the state nodes are
     * collapsed but its children are not.
     */
    private void fill(DefaultMutableTreeNode parent) {
        if (parent.getChildCount() <= 1) {
            parent.removeAllChildren();
            List<StateTreeNode> stateNodes = new ArrayList<>(RANGE_SIZE);
            int lower = parent instanceof RangeTreeNode ? ((RangeTreeNode) parent).getLower() : 0;
            int upper = Math.min(this.states.length, lower + RANGE_SIZE);
            for (int s = lower; s < upper; s++) {
                GraphState state = this.states[s];
                if (state != null) {
                    StateTreeNode stateNode = createStateNode(state);
                    parent.add(stateNode);
                    stateNodes.add(stateNode);
                }
            }
            getModel().reload(parent);
            for (StateTreeNode stateNode : stateNodes) {
                setStateExpanded(stateNode);
            }
        }
    }

    private void setStateExpanded(StateTreeNode stateNode) {
        for (int i = 0; i < stateNode.getChildCount(); i++) {
            expandPath(createPath((DisplayTreeNode) stateNode.getChildAt(i)));
        }
        if (!stateNode.isExpanded()) {
            collapsePath(createPath((stateNode)));
        }
    }

    /**
     * Creates a state node, with children, for a given graph state.
     */
    private StateTreeNode createStateNode(GraphState state) {
        boolean isExpanded = this.expanded.contains(state);
        StateTreeNode result = new StateTreeNode(state, isExpanded);
        Collection<GraphTransitionKey> matches = new ArrayList<>();
        Claz claz = Claz.getClass(isShowInternal(), isShowAbsent());
        for (GraphTransition trans : state.getTransitions(claz)) {
            matches.add(trans.getKey());
        }
        matches.addAll(state.getMatches());
        Map<Action,Set<GraphTransitionKey>> matchMap =
            new TreeMap<>(Action.PARTIAL_COMPARATOR);
        for (GraphTransitionKey match : matches) {
            Action action = match.getAction();
            Set<GraphTransitionKey> ruleMatches = matchMap.get(action);
            if (ruleMatches == null) {
                matchMap.put(action,
                    ruleMatches = new TreeSet<>(GraphTransitionKey.COMPARATOR));
            }
            ruleMatches.add(match);
        }
        boolean anchored = getOptions().isSelected(Options.SHOW_ANCHORS_OPTION);
        for (Map.Entry<Action,Set<GraphTransitionKey>> matchEntry : matchMap.entrySet()) {
            Action action = matchEntry.getKey();
            DisplayTreeNode ruleNode;
            if (action instanceof Rule) {
                ruleNode = new RuleTreeNode(getRuleDisplay(), action.getQualName());
            } else {
                ruleNode = new RecipeTreeNode((Recipe) action);
            }
            result.add(ruleNode);
            int count = 0;
            for (GraphTransitionKey trans : matchEntry.getValue()) {
                count++;
                DisplayTreeNode transNode;
                if (trans instanceof MatchResult) {
                    transNode = new MatchTreeNode(getSimulatorModel(), state, (MatchResult) trans,
                        count, anchored);
                } else {
                    transNode = new RecipeTransitionTreeNode(getSimulatorModel(), state,
                        (RecipeEvent) trans, count);
                }
                ruleNode.add(transNode);
            }
        }
        return result;
    }

    /**
     * Changes the selection to a given state.
     */
    private void refreshSelection(GraphState state, RuleModel ruleModel, MatchResult match,
        GraphTransition trans) {
        if (state != null) {
            StateTreeNode stateNode = getStateNode(state);
            if (stateNode != null) {
                TreePath statePath = createPath(stateNode);
                expandPath(statePath);
                TreePath selectPath = statePath;
                // find the match or transitions among the grandchildren of the state node
                for (int i = 0; i < stateNode.getChildCount(); i++) {
                    TreeNode child = stateNode.getChildAt(i);
                    if (match != null && child instanceof RuleTreeNode) {
                        RuleTreeNode ruleNode = (RuleTreeNode) child;
                        if (ruleNode.getRule()
                            .equals(ruleModel)) {
                            RuleEvent event = match.getEvent();
                            for (int m = 0; m < ruleNode.getChildCount(); m++) {
                                MatchTreeNode matchNode = (MatchTreeNode) ruleNode.getChildAt(m);
                                if (matchNode.getMatch()
                                    .getEvent()
                                    .equals(event)) {
                                    selectPath = createPath(matchNode);
                                    break;
                                }
                            }
                            break;
                        }
                    } else
                        if (trans instanceof RecipeTransition && child instanceof RecipeTreeNode) {
                        RecipeTreeNode recipeNode = (RecipeTreeNode) child;
                        if (recipeNode.getRecipe()
                            .equals(trans.getAction())) {
                            for (int m = 0; m < recipeNode.getChildCount(); m++) {
                                RecipeTransitionTreeNode matchNode =
                                    (RecipeTransitionTreeNode) recipeNode.getChildAt(m);
                                if (matchNode.getTransition()
                                    .equals(trans)) {
                                    selectPath = createPath(matchNode);
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }
                setSelectionPath(selectPath);
                // show as much as possible of the expanded state
                if (stateNode.getChildCount() > 0) {
                    DisplayTreeNode ruleNode = (DisplayTreeNode) stateNode.getLastChild();
                    DisplayTreeNode transNode = (DisplayTreeNode) ruleNode.getLastChild();
                    scrollPathToVisible(createPath(transNode));
                }
                scrollPathToVisible(statePath);
            }
        }
    }

    /** Callback factory method to create a {@link TreePath} object for a node. */
    private TreePath createPath(DefaultMutableTreeNode node) {
        return new TreePath(node.getPath());
    }

    private StateTreeNode getStateNode(GraphState state) {
        StateTreeNode result = null;
        int nr = state.getNumber();
        if (hasRangeNodes()) {
            RangeTreeNode rangeNode = (RangeTreeNode) find(getTopNode(), nr);
            if (rangeNode != null) {
                fill(rangeNode);
                result = (StateTreeNode) find(rangeNode, nr);
            }
        } else {
            result = (StateTreeNode) find(getTopNode(), nr);
        }
        return result;
    }

    /**
     * Retrieves the child of a given parent node that is
     * a numbered tree node with a given number, if any.
     * @return the correctly numbered child, or {@code null} if there
     * is none such
     */
    private NumberedTreeNode find(TreeNode parent, int number) {
        NumberedTreeNode result = null;
        int lower = 0;
        int upper = parent.getChildCount() - 1;
        boolean found = false;
        while (!found && lower <= upper) {
            int mid = (lower + upper) / 2;
            result = (NumberedTreeNode) parent.getChildAt(mid);
            int resultNumber = result.getNumber();
            if (result.contains(number)) {
                found = true;
            } else if (resultNumber < number) {
                lower = mid + 1;
            } else if (resultNumber > number) {
                upper = mid - 1;
            }
        }
        return found ? result : null;
    }

    /**
     * Indicates if there are so many states that the tree has a
     * top level of range nodes.
     */
    private boolean hasRangeNodes() {
        return getTopNode().getFirstChild() instanceof RangeTreeNode;
    }

    /** Returns the simulator to which the state list belongs. */
    final private SimulatorModel getSimulatorModel() {
        return this.simulator.getModel();
    }

    private final ActionStore getActions() {
        return this.simulator.getActions();
    }

    private final ResourceDisplay getRuleDisplay() {
        return (ResourceDisplay) this.simulator.getDisplaysPanel()
            .getDisplay(DisplayKind.RULE);
    }

    private final Options getOptions() {
        return this.simulator.getOptions();
    }

    private final Simulator simulator;
    /** The fixed top node of the tree. */
    private final DefaultMutableTreeNode topNode = new DefaultMutableTreeNode(null, true);
    /** The fixed tree model. */
    private final DefaultTreeModel treeModel = new DefaultTreeModel(this.topNode);
    /** List of states in the most recently loaded GTS. */
    private GraphState[] states = new GraphState[0];
    /** State that should be expanded. */
    private Queue<GraphState> expanded = new LinkedList<>();
    /** Flag indicating if listeners should be active. */
    private boolean listening;

    /** Number of nodes folded under a {@link RangeTreeNode}. */
    private static final int RANGE_SIZE = 100;
    /** Size of the queue of previously expanded nodes. */
    private static final int MAX_EXPANDED = 2;

    /** Tree node with a number that allows a binary search. */
    abstract static private class NumberedTreeNode extends DisplayTreeNode {
        /** Creates a tree node with a given user object. */
        protected NumberedTreeNode(Object userObject) {
            super(userObject, true);
        }

        /** Returns the number. */
        abstract public int getNumber();

        /** Indicates if a given number satisfies the constraints of this node. */
        abstract public boolean contains(int number);
    }

    /**
     * Tree node wrapping a range of {@link StateTreeNode}s.
     */
    private class RangeTreeNode extends NumberedTreeNode {
        /**
         * Creates a new range node based on a given lower bound. The node can have
         * children.
         */
        public RangeTreeNode(int lower) {
            super(lower);
        }

        /**
         * Convenience method to retrieve the lower bound of the range.
         */
        public int getLower() {
            return (Integer) getUserObject();
        }

        /**
         * Convenience method to retrieve the lower bound of the range.
         */
        public int getUpper() {
            return Math.min(getLower() + RANGE_SIZE, StateTree.this.states.length) - 1;
        }

        @Override
        public int getNumber() {
            return getLower();
        }

        @Override
        public String getText() {
            return "[" + getLower() + ".." + getUpper() + "]";
        }

        @Override
        public boolean contains(int number) {
            return getLower() <= number && getUpper() >= number;
        }
    }

    /**
     * Tree node wrapping a graph state.
     */
    class StateTreeNode extends NumberedTreeNode {
        /**
         * Creates a new rule node based on a given state. The node can have
         * children.
         */
        public StateTreeNode(GraphState state, boolean expanded) {
            super(state);
            this.expanded = expanded;
        }

        @Override
        public int getNumber() {
            return getState().getNumber();
        }

        @Override
        public boolean contains(int number) {
            return getNumber() == number;
        }

        /**
         * Convenience method to retrieve the user object as a state.
         */
        public GraphState getState() {
            return (GraphState) getUserObject();
        }

        /** Indicates if this tree node should be initially expanded. */
        public boolean isExpanded() {
            return this.expanded;
        }

        @Override
        public String getText() {
            return "State " + HTMLConverter.ITALIC_TAG.on(getState().toString());
        }

        @Override
        public Icon getIcon() {
            GraphState state = getState();
            if (state instanceof StartGraphState) {
                return Icons.STATE_START_ICON;
            } else if (isResult(state)) {
                return Icons.STATE_RESULT_ICON;
            } else if (state.isFinal()) {
                return Icons.STATE_FINAL_ICON;
            } else if (state.isInternalState()) {
                return state.isAbsent() ? Icons.STATE_INTERNAL_ABSENT_ICON
                    : Icons.STATE_INTERNAL_ICON;
            } else if (state.isTransient()) {
                return Icons.STATE_TRANSIENT_ICON;
            } else if (state.isAbsent()) {
                return Icons.STATE_ABSENT_ICON;
            } else if (state.isClosed()) {
                return Icons.STATE_CLOSED_ICON;
            } else {
                return Icons.STATE_OPEN_ICON;
            }
        }

        private boolean isResult(GraphState state) {
            return getSimulatorModel().hasExploreResult() && getSimulatorModel().getExploreResult()
                .containsState(state);
        }

        private final boolean expanded;
    }

    /**
     * Mouse listener that creates the popup menu and switches the view to the
     * rule panel on double-clicks.
     */
    private class StateMouseListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent evt) {
            TreePath path = getPathForLocation(evt.getX(), evt.getY());
            if (path != null) {
                if (evt.getButton() == MouseEvent.BUTTON3 && !isRowSelected(getRowForPath(path))) {
                    setSelectionPath(path);
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
            Object node = path.getLastPathComponent();
            switch (evt.getClickCount()) {
            case 1:
                DisplayKind toDisplay = null;
                if (node instanceof RuleTreeNode) {
                    toDisplay = DisplayKind.RULE;
                } else if (getSimulatorModel().getDisplay() != DisplayKind.LTS) {
                    toDisplay = DisplayKind.STATE;
                }
                if (toDisplay != null) {
                    getSimulatorModel().setDisplay(toDisplay);
                }
                break;
            case 2:
                getActions().getApplyMatchAction()
                    .execute();
            }
        }

        private void maybeShowPopup(MouseEvent evt) {
            if (evt.isPopupTrigger()) {
                TreePath selectedPath = getPathForLocation(evt.getX(), evt.getY());
                TreeNode selectedNode =
                    selectedPath == null ? null : (TreeNode) selectedPath.getLastPathComponent();
                StateTree.this.requestFocus();
                createPopupMenu(selectedNode).show(evt.getComponent(), evt.getX(), evt.getY());
            }
        }
    }

    /**
     * Selection listener that invokes <tt>setRule</tt> if a rule node is
     * selected, and <tt>setDerivation</tt> if a match node is selected.
     * @see SimulatorModel#setMatch
     */
    private class StateSelectionListener implements TreeSelectionListener {
        /**
         * Triggers a rule or match selection update by the simulator
         * based on the current selection in the tree.
         */
        @Override
        public void valueChanged(TreeSelectionEvent evt) {
            if (suspendListening()) {
                TreePath[] paths = getSelectionPaths();
                if (paths != null && paths.length == 1) {
                    GraphState selectedState = null;
                    MatchResult selectedMatch = null;
                    GraphTransition selectedTrans = null;
                    Object selectedNode = paths[0].getLastPathComponent();
                    if (selectedNode instanceof MatchTreeNode) {
                        // selected tree node is a match (level 2 node)
                        selectedMatch = ((MatchTreeNode) selectedNode).getMatch();
                        selectedState = ((MatchTreeNode) selectedNode).getSource();
                    } else if (selectedNode instanceof RecipeTransitionTreeNode) {
                        // selected tree node is a match (level 2 node)
                        selectedTrans = ((RecipeTransitionTreeNode) selectedNode).getTransition();
                    } else if (selectedNode instanceof StateTreeNode) {
                        selectedState = ((StateTreeNode) selectedNode).getState();
                    } else if (selectedNode instanceof RuleTreeNode) {
                        Object parentNode = paths[0].getPathComponent(paths[0].getPathCount() - 2);
                        selectedState = ((StateTreeNode) parentNode).getState();
                    }
                    if (selectedState != null) {
                        StateTreeNode stateNode = getStateNode(selectedState);
                        if (stateNode != null) {
                            expandPath(createPath(stateNode));
                            getSimulatorModel().setMatch(selectedState, selectedMatch);
                        }
                    } else if (selectedTrans != null) {
                        getSimulatorModel().setTransition(selectedTrans);
                    }
                }
                getSimulatorModel().doSelectSet(ResourceKind.RULE, getSelectedRules());
                activateListening();
            }
        }

        /** Returns the list of currently selected full rule names. */
        private Set<QualName> getSelectedRules() {
            Set<QualName> result = new HashSet<>();
            int[] selectedRows = getSelectionRows();
            if (selectedRows != null) {
                for (int selectedRow : selectedRows) {
                    Object[] nodes = getPathForRow(selectedRow).getPath();
                    for (int i = nodes.length - 1; i >= 0; i--) {
                        if (nodes[i] instanceof RuleTreeNode) {
                            result.add(((RuleTreeNode) nodes[i]).getRule()
                                .getQualName());
                        }
                    }
                }
            }
            return result;
        }
    }
}
