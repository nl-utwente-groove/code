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
package groove.gui;

import groove.explore.util.MatchSetCollector;
import groove.gui.SimulatorModel.Change;
import groove.gui.action.ActionStore;
import groove.gui.jgraph.JAttr;
import groove.io.HTMLConverter;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.MatchResult;
import groove.lts.MatchResultSet;
import groove.lts.StartGraphState;
import groove.trans.ResourceKind;
import groove.trans.Rule;
import groove.view.GrammarModel;

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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * List of states in the LTS.
 * @author Arend Rensink
 * @version $Revision $
 */
public class StateList extends JTree implements SimulatorListener {
    /**
     * Creates a new state list.
     */
    protected StateList(Simulator simulator) {
        this.simulatorModel = simulator.getModel();
        this.actions = simulator.getActions();
        this.options = simulator.getOptions();
        setEnabled(false);
        setBackground(JAttr.STATE_BACKGROUND);
        setLargeModel(true);
        setRootVisible(false);
        setShowsRootHandles(true);
        setEnabled(false);
        setToggleClickCount(0);
        setModel(getModel());
        getSelectionModel().setSelectionMode(
            TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        this.setCellRenderer(new StateCellRenderer());
        installListeners();
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
        getSimulatorModel().addListener(this, Change.GTS, Change.STATE,
            Change.MATCH);
        addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent e) {
                StateList.this.repaint();
            }

            @Override
            public void focusGained(FocusEvent e) {
                StateList.this.repaint();
            }
        });
        addTreeWillExpandListener(new TreeWillExpandListener() {
            @Override
            public void treeWillExpand(TreeExpansionEvent event)
                throws ExpandVetoException {
                Object lastComponent = event.getPath().getLastPathComponent();
                if (!this.busy && lastComponent instanceof RangeTreeNode) {
                    this.busy = true;
                    fill((RangeTreeNode) lastComponent);
                    this.busy = false;
                }
            }

            @Override
            public void treeWillCollapse(TreeExpansionEvent event)
                throws ExpandVetoException {
                // do nothing
            }

            private boolean busy = false;
        });
        addTreeSelectionListener(new StateSelectionListener());
        addMouseListener(new StateMouseListener());
        JMenuItem showAnchorsOptionItem =
            this.options.getItem(Options.SHOW_ANCHORS_OPTION);
        if (showAnchorsOptionItem != null) {
            // listen to the option controlling the rule anchor display
            showAnchorsOptionItem.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (suspendListening()) {
                        SimulatorModel model = getSimulatorModel();
                        refreshList(model.getGts(), model.getState());
                        activateListening();
                    }
                }
            });
        }
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
            result.add(getActions().getApplyTransitionAction());
        }
        return result;
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel,
            Set<Change> changes) {
        if (suspendListening()) {
            if (changes.contains(Change.GTS)) {
                setEnabled(source.hasGts());
                refreshList(source.getGts(), oldModel.getState());
            }
            refreshSelection(source.getState(), source.getMatch());
            activateListening();
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
            for (GraphState state : gts.nodeSet()) {
                this.states[state.getNumber()] = state;
            }
            // only add range nodes if there are too many states
            if (hasRangeNodes()) {
                for (int i = 0; i < this.states.length; i += RANGE_SIZE) {
                    if (this.states[i] != null) {
                        RangeTreeNode rangeNode = new RangeTreeNode(i);
                        getTopNode().add(rangeNode);
                        // provisionally assign a single child to make the
                        // range node expandable
                        rangeNode.add(createStateNode(this.states[i]));
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
            List<StateTreeNode> stateNodes =
                new ArrayList<StateTreeNode>(RANGE_SIZE);
            int lower =
                parent instanceof RangeTreeNode
                        ? ((RangeTreeNode) parent).getLower() : 0;
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
            expandPath(createPath((RuleTreeNode) stateNode.getChildAt(i)));
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
        Map<Rule,Set<MatchResult>> matchMap =
            new TreeMap<Rule,Set<MatchResult>>();
        for (MatchResult match : getMatches(state)) {
            Rule rule = match.getEvent().getRule();
            Set<MatchResult> events = matchMap.get(rule);
            if (events == null) {
                matchMap.put(rule, events =
                    new TreeSet<MatchResult>(MatchResult.COMPARATOR));
            }
            events.add(match);
        }
        GrammarModel grammar = getSimulatorModel().getGrammar();
        boolean anchored = this.options.isSelected(Options.SHOW_ANCHORS_OPTION);
        for (Map.Entry<Rule,Set<MatchResult>> matchEntry : matchMap.entrySet()) {
            Rule rule = matchEntry.getKey();
            RuleTreeNode ruleNode =
                new RuleTreeNode(grammar.getRuleModel(rule.getFullName()));
            result.add(ruleNode);
            int count = 0;
            for (MatchResult trans : matchEntry.getValue()) {
                count++;
                MatchTreeNode transNode =
                    new MatchTreeNode(state, trans, count, anchored);
                ruleNode.add(transNode);
            }
        }
        return result;
    }

    /**
     * Refreshes the selection in the tree, based on the current state of the
     * Simulator.
     */
    private Collection<? extends MatchResult> getMatches(GraphState state) {
        MatchResultSet result = new MatchResultSet();
        result.addAll(state.getTransitionSet());
        if (!state.isClosed()) {
            GTS gts = state.getGTS();
            result.addAll(new MatchSetCollector(state, gts.getRecord(),
                gts.checkDiamonds()).getMatchSet());
        }
        return result;
    }

    /**
     * Changes the selection to a given state.
     */
    private void refreshSelection(GraphState state, MatchResult match) {
        if (state != null) {
            StateTreeNode stateNode = getStateNode(state);
            if (stateNode != null) {
                TreePath statePath = createPath(stateNode);
                expandPath(statePath);
                TreePath selectPath = statePath;
                if (match != null) {
                    // find the match among the grandchildren of the state node
                    for (int i = 0; i < stateNode.getChildCount(); i++) {
                        RuleTreeNode ruleNode =
                            (RuleTreeNode) stateNode.getChildAt(i);
                        if (ruleNode.getRule().equals(
                            match.getEvent().getRule())) {
                            for (int m = 0; m < ruleNode.getChildCount(); m++) {
                                MatchTreeNode matchNode =
                                    (MatchTreeNode) ruleNode.getChildAt(m);
                                if (matchNode.getMatch().getEvent().equals(
                                    match.getEvent())) {
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
                    RuleTreeNode ruleNode =
                        (RuleTreeNode) stateNode.getLastChild();
                    MatchTreeNode transNode =
                        (MatchTreeNode) ruleNode.getLastChild();
                    scrollPathToVisible(createPath(transNode));
                }
                scrollPathToVisible(statePath);
            }
        }
    }

    /** Callback factory method to creaet a {@link TreePath} object for a node. */
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

    private NumberedTreeNode find(TreeNode parent, int number) {
        NumberedTreeNode result = null;
        int lower = 0;
        int upper = parent.getChildCount() - 1;
        boolean found = false;
        while (!found && lower <= upper) {
            int mid = (lower + upper) / 2;
            result = (NumberedTreeNode) parent.getChildAt(mid);
            int resultNumber = result.getNumber();
            if (resultNumber < number) {
                lower = mid + 1;
            } else if (resultNumber > number) {
                upper = mid - 1;
            } else {
                found = true;
            }
        }
        return found ? result : null;
    }

    /**
     * Indicates if there are so many states that the tree has a 
     * top level of range nodes.
     */
    private boolean hasRangeNodes() {
        return this.states.length >= RANGE_SIZE;
    }

    /** Returns the simulator to which the state list belongs. */
    final private SimulatorModel getSimulatorModel() {
        return this.simulatorModel;
    }

    private final ActionStore getActions() {
        return this.actions;
    }

    private final SimulatorModel simulatorModel;
    private final ActionStore actions;
    private final Options options;
    /** The fixed top node of the tree. */
    private final DefaultMutableTreeNode topNode = new DefaultMutableTreeNode(
        null, true);
    /** The fixed tree model. */
    private final DefaultTreeModel treeModel = new DefaultTreeModel(
        this.topNode);
    /** List of states in the most recently loaded GTS. */
    private GraphState[] states = new GraphState[0];
    /** State that should be expanded. */
    private Queue<GraphState> expanded = new LinkedList<GraphState>();
    /** Flag indicating if listeners should be active. */
    private boolean listening;

    /** Number of nodes folded under a {@link RangeTreeNode}. */
    private static final int RANGE_SIZE = 100;
    /** Size of the queue of previously expanded nodes. */
    private static final int MAX_EXPANDED = 2;

    /** Tree node with a number that allows a binary search. */
    abstract static private class NumberedTreeNode extends
            DefaultMutableTreeNode {
        /** Creates a tree node with a given user object. */
        protected NumberedTreeNode(Object userObject) {
            super(userObject, true);
        }

        /** Returns the number. */
        abstract public int getNumber();
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

        @Override
        public int getNumber() {
            return getLower();
        }

        @Override
        public String toString() {
            int lower = getLower();
            int upper =
                Math.min(lower + RANGE_SIZE, StateList.this.states.length) - 1;
            return "[" + lower + ".." + upper + "]";
        }
    }

    /**
     * Tree node wrapping a graph state.
     */
    private static class StateTreeNode extends NumberedTreeNode {
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
        public String toString() {
            return HTMLConverter.HTML_TAG.on("State "
                + HTMLConverter.ITALIC_TAG.on(getState().toString()));
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
                if (evt.getButton() == MouseEvent.BUTTON3
                    && !isRowSelected(getRowForPath(path))) {
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
                DisplayKind toDisplay;
                if (node instanceof RuleTreeNode) {
                    toDisplay = DisplayKind.RULE;
                } else {
                    toDisplay = DisplayKind.LTS;
                }
                getSimulatorModel().setDisplay(toDisplay);
                break;
            case 2:
                if (node instanceof MatchTreeNode) {
                    getSimulatorModel().doApplyMatch();
                } else if (node instanceof StateTreeNode) {
                    getSimulatorModel().doExploreState();
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
                StateList.this.requestFocus();
                createPopupMenu(selectedNode).show(evt.getComponent(),
                    evt.getX(), evt.getY());
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
        public void valueChanged(TreeSelectionEvent evt) {
            if (suspendListening()) {
                TreePath[] paths = getSelectionPaths();
                if (paths != null && paths.length == 1) {
                    GraphState selectedState = null;
                    MatchResult selectedMatch = null;
                    Object selectedNode = paths[0].getLastPathComponent();
                    if (selectedNode instanceof MatchTreeNode) {
                        // selected tree node is a match (level 2 node)
                        selectedMatch =
                            ((MatchTreeNode) selectedNode).getMatch();
                        selectedState =
                            ((MatchTreeNode) selectedNode).getSource();
                    } else if (selectedNode instanceof StateTreeNode) {
                        selectedState =
                            ((StateTreeNode) selectedNode).getState();
                    } else if (selectedNode instanceof RuleTreeNode) {
                        Object parentNode =
                            paths[0].getPathComponent(paths[0].getPathCount() - 2);
                        selectedState = ((StateTreeNode) parentNode).getState();
                    }
                    if (selectedState != null) {
                        StateTreeNode stateNode = getStateNode(selectedState);
                        if (stateNode != null) {
                            expandPath(createPath(stateNode));
                            getSimulatorModel().setState(selectedState);
                            getSimulatorModel().setMatch(selectedMatch);
                        }
                    }
                }
                getSimulatorModel().doSelectSet(ResourceKind.RULE,
                    getSelectedRules());
                activateListening();
            }
        }

        /** Returns the list of currently selected full rule names. */
        private Set<String> getSelectedRules() {
            Set<String> result = new HashSet<String>();
            int[] selectedRows = getSelectionRows();
            if (selectedRows != null) {
                for (int selectedRow : selectedRows) {
                    Object[] nodes = getPathForRow(selectedRow).getPath();
                    for (int i = nodes.length - 1; i >= 0; i--) {
                        if (nodes[i] instanceof RuleTreeNode) {
                            result.add(((RuleTreeNode) nodes[i]).getRule().getFullName());
                        }
                    }
                }
            }
            return result;
        }
    }

    /**
     * Cell renderer for the state list.
     */
    private class StateCellRenderer extends DefaultTreeCellRenderer {
        // This is the only method defined by ListCellRenderer.
        // We just reconfigure the JLabel each time we're called.
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean isSelected, boolean expanded, boolean leaf, int row,
                boolean hasFocus) {
            boolean cellSelected = isSelected || hasFocus;
            boolean cellFocused = cellSelected && StateList.this.isFocusOwner();
            Component result =
                super.getTreeCellRendererComponent(tree, value, cellSelected,
                    expanded, leaf, row, false);
            Icon icon = null;
            if (value instanceof StateTreeNode) {
                icon = getIcon(((StateTreeNode) value).getState());
            } else if (value instanceof RuleTreeNode) {
                icon = Icons.RULE_LIST_ICON;
            } else if (value instanceof MatchTreeNode) {
                icon = Icons.GRAPH_MATCH_ICON;
            }
            setIcon(icon);
            setText(value.toString());
            setForeground(JAttr.getForeground(cellSelected, cellFocused, false));
            Color background =
                JAttr.getBackground(cellSelected, cellFocused, false);
            if (background == Color.WHITE) {
                background = JAttr.STATE_BACKGROUND;
            }
            if (cellSelected) {
                setBackgroundSelectionColor(background);
            } else {
                setBackgroundNonSelectionColor(background);
            }
            setOpaque(false);
            return result;
        }

        private Icon getIcon(GraphState state) {
            if (state instanceof StartGraphState) {
                return Icons.STATE_START_ICON;
            } else if (getSimulatorModel().getGts().isResult(state)) {
                return Icons.STATE_RESULT_ICON;
            } else if (getSimulatorModel().getGts().isFinal(state)) {
                return Icons.STATE_FINAL_ICON;
            } else if (state.isClosed()) {
                return Icons.STATE_CLOSED_ICON;
            } else {
                return Icons.STATE_OPEN_ICON;
            }
        }
    }
}
