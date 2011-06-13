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

import groove.gui.SimulatorModel.Change;
import groove.gui.action.ActionStore;
import groove.gui.jgraph.JAttr;
import groove.io.HTMLConverter;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.StartGraphState;
import groove.trans.ResourceKind;
import groove.trans.Rule;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.Icon;
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
        getSimulatorModel().addListener(this, Change.GTS, Change.STATE);
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
     * Creates a popup menu for this panel.
     * @param node the node for which the menu is created
     */
    private JPopupMenu createPopupMenu(TreeNode node) {
        JPopupMenu result = new JPopupMenu();
        if (node instanceof StateTreeNode) {
            result.add(getActions().getEditStateAction());
            result.add(getActions().getSaveStateAction());
            result.add(getActions().getExportStateAction());
        } else if (node instanceof TransitionTreeNode) {
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
                refreshList(source.getGts());
            }
            refreshSelection(source.getState());
            activateListening();
        }
    }

    /**
     * Refreshes the tree model from the GTS.
     */
    private void refreshList(GTS gts) {
        getTopNode().removeAllChildren();
        if (gts == null) {
            this.states = new GraphState[0];
            getModel().reload();
        } else {
            this.states = new GraphState[gts.nodeCount()];
            for (GraphState state : gts.nodeSet()) {
                this.states[state.getNumber()] = state;
            }
            // only add range nodes if there are too many states
            if (hasRangeNodes()) {
                for (int i = 0; i < this.states.length; i += RANGE_SIZE) {
                    RangeTreeNode rangeNode = new RangeTreeNode(i);
                    getTopNode().add(rangeNode);
                    // provisionally assign a single child to make the
                    // range node expandable
                    rangeNode.add(createStateNode(this.states[i]));
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
                StateTreeNode stateNode = createStateNode(this.states[s]);
                parent.add(stateNode);
                stateNodes.add(stateNode);
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
        collapsePath(createPath((stateNode)));
    }

    /**
     * Creates a state node, with children, for a given graph state.
     */
    private StateTreeNode createStateNode(GraphState state) {
        StateTreeNode result = new StateTreeNode(state);
        Map<Rule,Set<GraphTransition>> matchMap =
            new TreeMap<Rule,Set<GraphTransition>>();
        Iterator<GraphTransition> transIter = state.getTransitionIter();
        while (transIter.hasNext()) {
            GraphTransition trans = transIter.next();
            Rule rule = trans.getEvent().getRule();
            Set<GraphTransition> events = matchMap.get(rule);
            if (events == null) {
                matchMap.put(rule, events =
                    new TreeSet<GraphTransition>(TRANSITION_COMPARATOR));
            }
            events.add(trans);
        }
        for (Map.Entry<Rule,Set<GraphTransition>> matchEntry : matchMap.entrySet()) {
            RuleTreeNode ruleNode = new RuleTreeNode(matchEntry.getKey());
            result.add(ruleNode);
            for (GraphTransition trans : matchEntry.getValue()) {
                TransitionTreeNode transNode = new TransitionTreeNode(trans);
                ruleNode.add(transNode);
            }
        }
        return result;
    }

    /**
     * Refreshes the tree model from the GTS.
     */
    private void refreshSelection(GraphState state) {
        if (state != null) {
            StateTreeNode stateNode = getStateNode(state);
            TreePath statePath = createPath(stateNode);
            setSelectionPath(statePath);
            expandPath(statePath);
            // show as much as possible of the expanded state
            if (stateNode.getChildCount() > 0) {
                RuleTreeNode ruleNode = (RuleTreeNode) stateNode.getLastChild();
                TransitionTreeNode transNode =
                    (TransitionTreeNode) ruleNode.getLastChild();
                scrollPathToVisible(createPath(transNode));
            }
            scrollPathToVisible(statePath);
        }
    }

    /** Callback factory method to creaet a {@link TreePath} object for a node. */
    private TreePath createPath(DefaultMutableTreeNode node) {
        return new TreePath(node.getPath());
    }

    private StateTreeNode getStateNode(GraphState state) {
        int nr = state.getNumber();
        if (hasRangeNodes()) {
            RangeTreeNode rangeNode =
                (RangeTreeNode) getTopNode().getChildAt(nr / RANGE_SIZE);
            fill(rangeNode);
            return (StateTreeNode) rangeNode.getChildAt(nr % RANGE_SIZE);
        } else {
            return (StateTreeNode) getTopNode().getChildAt(nr);
        }
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
    /** The fixed top node of the tree. */
    private final DefaultMutableTreeNode topNode = new DefaultMutableTreeNode(
        null, true);
    /** The fixed tree model. */
    private final DefaultTreeModel treeModel = new DefaultTreeModel(
        this.topNode);
    /** List of states in the most recently loaded GTS. */
    private GraphState[] states;
    /** Flag indicating if listeners should be active. */
    private boolean listening;
    /**
     * The background colour of this component when it is enabled.
     */
    private Color enabledBackground;

    /** Singleton instance of the {@link TransitionComparator}. */
    private static final TransitionComparator TRANSITION_COMPARATOR =
        new TransitionComparator();
    /** Number of nodes folded under a {@link RangeTreeNode}. */
    private static final int RANGE_SIZE = 100;

    /**
     * Compares two graph transitions for their target states, and if
     * those coincide, for their rule events.
     */
    private static final class TransitionComparator implements
            Comparator<GraphTransition> {
        @Override
        public int compare(GraphTransition o1, GraphTransition o2) {
            int result = o1.target().compareTo(o2.target());
            if (result == 0) {
                result = o1.getEvent().compareTo(o2.getEvent());
            }
            return result;
        }
    }

    /**
     * Tree node wrapping a range of {@link StateTreeNode}s.
     */
    private static class RangeTreeNode extends DefaultMutableTreeNode {
        /**
         * Creates a new range node based on a given lower bound. The node can have
         * children.
         */
        public RangeTreeNode(int lower) {
            super(lower, true);
        }

        /**
         * Convenience method to retrieve the lower bound of the range.
         */
        public int getLower() {
            return (Integer) getUserObject();
        }
    }

    /**
     * Tree node wrapping a graph state.
     */
    private static class StateTreeNode extends DefaultMutableTreeNode {
        /**
         * Creates a new rule node based on a given state. The node can have
         * children.
         */
        public StateTreeNode(GraphState state) {
            super(state, true);
        }

        /**
         * Convenience method to retrieve the user object as a state.
         */
        public GraphState getState() {
            return (GraphState) getUserObject();
        }
    }

    /**
     * Tree node wrapping a rule event.
     */
    private static class RuleTreeNode extends DefaultMutableTreeNode {
        /**
         * Creates a new tree node based on a given rule event. The node cannot have
         * children.
         */
        public RuleTreeNode(Rule rule) {
            super(rule, true);
        }

        /**
         * Convenience method to retrieve the user object as a rule event.
         */
        public Rule getRule() {
            return (Rule) getUserObject();
        }
    }

    /**
     * Tree node wrapping a graph transition.
     */
    private static class TransitionTreeNode extends DefaultMutableTreeNode {
        /**
         * Creates a new tree node based on a given graph transition. The node cannot have
         * children.
         */
        public TransitionTreeNode(GraphTransition event) {
            super(event, false);
        }

        /**
         * Convenience method to retrieve the user object as a graph transition.
         */
        public GraphTransition getTransition() {
            return (GraphTransition) getUserObject();
        }
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
                Object node = path.getLastPathComponent();
                switch (evt.getClickCount()) {
                case 1:
                    DisplayKind toDisplay;
                    if (node instanceof RuleTreeNode) {
                        toDisplay = DisplayKind.RULE;
                    } else if (getSimulatorModel().getDisplay() != DisplayKind.LTS) {
                        toDisplay = DisplayKind.HOST;
                    } else {
                        toDisplay = DisplayKind.LTS;
                    }
                    getSimulatorModel().setDisplay(toDisplay);
                    break;
                case 2:
                    if (node instanceof TransitionTreeNode) {
                        getSimulatorModel().doApplyMatch();
                    } else if (node instanceof StateTreeNode) {
                        getSimulatorModel().doExploreState();
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
            if (selectedNode instanceof TransitionTreeNode) {
                if (evt.getClickCount() == 2) {
                    getSimulatorModel().doApplyMatch();
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
                GraphTransition selectedMatch = null;
                for (int i = 0; paths != null && i < paths.length; i++) {
                    Object selectedNode = paths[i].getLastPathComponent();
                    if (selectedNode instanceof TransitionTreeNode) {
                        // selected tree node is a match (level 2 node)
                        selectedMatch =
                            ((TransitionTreeNode) selectedNode).getTransition();
                        break;
                    } else if (selectedNode instanceof StateTreeNode) {
                        GraphState result =
                            ((StateTreeNode) selectedNode).getState();
                        getSimulatorModel().setState(result);
                    }
                }
                getSimulatorModel().setMatch(selectedMatch);
                getSimulatorModel().doSelectSet(ResourceKind.RULE,
                    getSelectedRules());
                activateListening();
            }
        }

        /** Returns the list of currently selected rule names. */
        private Set<String> getSelectedRules() {
            Set<String> result = new HashSet<String>();
            int[] selectedRows = getSelectionRows();
            if (selectedRows != null) {
                for (int selectedRow : selectedRows) {
                    Object[] nodes = getPathForRow(selectedRow).getPath();
                    for (int i = nodes.length - 1; i >= 0; i--) {
                        if (nodes[i] instanceof RuleTreeNode) {
                            result.add(((RuleTreeNode) nodes[i]).getRule().getName());
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
    protected class StateCellRenderer extends DefaultTreeCellRenderer {
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
            String text = "";
            if (value instanceof RangeTreeNode) {
                int lower = ((RangeTreeNode) value).getLower();
                int upper =
                    Math.min(lower + RANGE_SIZE, StateList.this.states.length) - 1;
                text = "[" + lower + ".." + upper + "]";
            } else if (value instanceof StateTreeNode) {
                GraphState state = ((StateTreeNode) value).getState();
                text =
                    HTMLConverter.HTML_TAG.on("State "
                        + HTMLConverter.ITALIC_TAG.on(state.toString()));
                icon = getIcon(state);
            } else if (value instanceof RuleTreeNode) {
                icon = Icons.RULE_LIST_ICON;
                text = ((RuleTreeNode) value).getRule().getName();
            } else if (value instanceof TransitionTreeNode) {
                icon = Icons.GRAPH_MATCH_ICON;
                String state =
                    ((TransitionTreeNode) value).getTransition().target().toString();
                text =
                    HTMLConverter.HTML_TAG.on("To "
                        + HTMLConverter.ITALIC_TAG.on(state));
            }
            setIcon(icon);
            setText(text);
            setForeground(JAttr.getForeground(cellSelected, cellFocused, false));
            Color background =
                JAttr.getBackground(cellSelected, cellFocused, false);
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
