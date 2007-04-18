// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/*
 * $Id: RuleJTree.java,v 1.4 2007-04-18 08:41:19 rensink Exp $
 */
package groove.gui;

import groove.graph.Label;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.State;
import groove.lts.Transition;
import groove.trans.GraphGrammar;
import groove.trans.Rule;
import groove.trans.NameLabel;
import groove.trans.StructuredRuleName;
import groove.util.Groove;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * Panel that displays a two-level directory of rules and matches.
 * @version $Revision: 1.4 $
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
        setCellRenderer(new MyRenderer());
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        // set icons
        DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) cellRenderer;
        renderer.setLeafIcon(Groove.GRAPH_MATCH_ICON);
        renderer.setOpenIcon(Groove.RULE_SMALL_ICON);
        renderer.setClosedIcon(Groove.RULE_SMALL_ICON);
        addTreeSelectionListener(createRuleSelectionListener());
        this.listenToSelectionChanges = true;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                maybeShowPopup(evt);
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                maybeShowPopup(evt);
            }

            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2) {
                    TreePath path = getSelectionPath();
                    if (path != null) {
						Object selectedNode = path.getLastPathComponent();
						if (selectedNode instanceof MatchTreeNode) {
							// selected tree node is a derivation edge (level 2
							// node)
							simulator.setTransition(((MatchTreeNode) selectedNode).edge());
							simulator.applyTransition();
						}
					}
                }
            }

            private void maybeShowPopup(MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                    createPopupMenu().show(evt.getComponent(), evt.getX(), evt.getY());
                }
            }
        });
        topDirectoryNode = new DefaultMutableTreeNode();
        ruleDirectory = new DefaultTreeModel(topDirectoryNode, true);
        setModel(ruleDirectory);
        ActionMap am = getActionMap();
        am.put(Options.UNDO_ACTION_NAME, simulator.getUndoAction());
        am.put(Options.REDO_ACTION_NAME, simulator.getRedoAction());
        InputMap im = getInputMap();
        im.put(Options.UNDO_KEY, Options.UNDO_ACTION_NAME);
        im.put(Options.REDO_KEY, Options.REDO_ACTION_NAME);
    }

    /**
     * Fills the rule directory with rule nodes, based on a given rule system.
     * Sets the current LTS to the grammar's LTS.
     */
    public synchronized void setGrammarUpdate(GTS gts) {
    	boolean oldListenToSelectionChanges = listenToSelectionChanges;
        listenToSelectionChanges = false;
        setShowAnchorsOptionListener();
        ruleNodeMap.clear();
        matchNodeMap.clear();
        topDirectoryNode.removeAllChildren();

        // if the rule system has multiple priorities, we want an extra level of nodes
        // then we need to remember the last priority encountered
        int lastPriority = Integer.MAX_VALUE;
        DefaultMutableTreeNode topNode = topDirectoryNode;
        // get the rule names
        GraphGrammar grammar = gts.ruleSystem();
        for (Rule rule: new ArrayList<Rule>(grammar.getRules())) {
            StructuredRuleName ruleName = (StructuredRuleName) rule.getName();
            // create new top node for the rule, if the rule has a different priority then the last
            if (grammar.hasMultiplePriorities()) {
                int rulePriority = rule.getPriority();
                if (lastPriority != rulePriority) {
                    lastPriority = rulePriority;
                    topNode = new PriorityTreeNode(lastPriority);
                    topDirectoryNode.add(topNode);
                }
            }
            // recursively add parent directory nodes as required
            DefaultMutableTreeNode parentNode = addParentNode(topNode, ruleName);
            // create the rule node and register it
            RuleTreeNode ruleNode = new RuleTreeNode(ruleName);
            parentNode.add(ruleNode);
            expandPath(new TreePath(ruleNode.getPath()));
            ruleNodeMap.put(ruleName, ruleNode);
        }
        ruleDirectory.reload(topDirectoryNode);
        setEnabled(true);
        refresh();
        listenToSelectionChanges = oldListenToSelectionChanges;
    }

    /**
     * In addition to delegating the method to <tt>super</tt>,
     * sets the background color to <tt>null</tt> when disabled and
     * back to the default when enabled.
     */
    @Override
    public void setEnabled(boolean enabled) {
        if (enabled != isEnabled()) {
            if (!enabled) {
                enabledBackground = getBackground();
                setBackground(null);
            } else if (enabledBackground != null) {
                setBackground(enabledBackground);
            }
        }
        super.setEnabled(enabled);
    }
    
    /**
     * Refreshes the available match nodes, based on a new state.
     * The current LTS is inspected to find out the relevant derivations.
     * Expands all rule nodes to show the available matches.
     */
    public synchronized void setStateUpdate(GraphState state) {
        refresh();
    }

    /**
     * Sets the tree selection to a given rule name.
     * Does <i>not</i> trigger actions based on the selection change.
     */
    public synchronized void setRuleUpdate(NameLabel name) {
        refresh();
    }

    /**
     * Sets the tree selection to a given derivation.
     * Does <i>not</i> trigger actions based on the selection change.
     */
    public synchronized void setTransitionUpdate(GraphTransition transition) {
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
     * Sets a listener to the anchor image option, if that has not yet been done.
     */
    protected void setShowAnchorsOptionListener() {
    	if (! anchorImageOptionListenerSet) {
    		JCheckBoxMenuItem showAnchorsOptionItem = simulator.getOptions().getItem(Options.SHOW_ANCHORS_OPTION);
    		if (showAnchorsOptionItem != null) {
    	        // listen to the option controlling the rule anchor display
    			showAnchorsOptionItem.addItemListener(new ItemListener() {
    				public void itemStateChanged(ItemEvent e) {
    					refresh();
    				}
    	        });
    	        anchorImageOptionListenerSet = true;
    		}
    	}
    }

    /** Adds tree nodes for all levels of a structured rule name. */
    private DefaultMutableTreeNode addParentNode(DefaultMutableTreeNode topNode, StructuredRuleName ruleName) {
        StructuredRuleName parent = ruleName.parent();
        if (parent == null)
            // there is no parent rule name; the parent node is the top node
            return topNode;
        else {
            // there is a proper parent rule; look it up in the node map
            DirectoryTreeNode result = (DirectoryTreeNode) ruleNodeMap.get(parent);
            if (result == null) {
                // the parent node did not yet exist in the tree
                // check recursively for the grandparent
                DefaultMutableTreeNode grandParentNode = addParentNode(topNode, parent);
                // make the parent node and register it
                result = new DirectoryTreeNode(parent);
                grandParentNode.add(result);
                ruleNodeMap.put(parent, result);
            }
            return result;
        }
    }

    private void refresh() {
    	boolean oldListenToSelectionChanges = listenToSelectionChanges;
        listenToSelectionChanges = false;
    	if (getCurrentState() == null) {
            refreshMatches(Collections.<GraphTransition>emptySet());
    	} else {
    		refreshMatches(getCurrentGTS().outEdgeSet(getCurrentState()));
    	}
    	DefaultMutableTreeNode treeNode = null;
    	if (getCurrentTransition() != null) {
    		treeNode = matchNodeMap.get(getCurrentTransition());
    	} else if (getCurrentRule() != null) {
    		treeNode = ruleNodeMap.get(getCurrentRule());
    	}
        if (treeNode != null) {
            setSelectionPath(new TreePath(treeNode.getPath()));
        }
        listenToSelectionChanges = oldListenToSelectionChanges;
    }
    
    /**
     * Refreshes the match nodes, based on a given derivation edge set.
     * @param derivations the set of derivation edges used to create match nodes
     */
    private void refreshMatches(Collection<GraphTransition> derivations) {
    	if (displayedState != simulator.getCurrentState()) {
        // remove current matches
    	for (MatchTreeNode matchNode: matchNodeMap.values()) {
            ruleDirectory.removeNodeFromParent(matchNode);
        }
        // clean up current match node map
        matchNodeMap.clear();
        // expand all rule nodes and subsequently collapse all directory nodes
        for (DefaultMutableTreeNode nextNode: ruleNodeMap.values()) {
            if (!(nextNode instanceof DirectoryTreeNode)) {
                expandPath(new TreePath(nextNode.getPath()));
            }
        }
        for (DefaultMutableTreeNode nextNode: ruleNodeMap.values()) {
            if (nextNode instanceof DirectoryTreeNode) {
                collapsePath(new TreePath(nextNode.getPath()));
            }
        }
        // recollect the derivations so that they are ordered according to the rule events
        SortedSet<GraphTransition> orderedDerivations = new TreeSet<GraphTransition>(new Comparator<GraphTransition>() {
			public int compare(GraphTransition o1, GraphTransition o2) {
				return o1.getEvent().compareTo(o2.getEvent());
			}
        });
        orderedDerivations.addAll(derivations);
        // insert new matches
        for (GraphTransition edge: orderedDerivations) {
            Label ruleName = edge.getRule().getName();
            RuleTreeNode ruleNode = (RuleTreeNode) ruleNodeMap.get(ruleName);
            int nrOfMatches = ruleNode.getChildCount();
            MatchTreeNode matchNode = new MatchTreeNode(nrOfMatches + 1, edge);
            ruleDirectory.insertNodeInto(matchNode, ruleNode, nrOfMatches);
            expandPath(new TreePath(ruleNode.getPath()));
            matchNodeMap.put(edge, matchNode);
        }
        displayedState = simulator.getCurrentState();
    	}
    }

    /** Convenience method to retrieve the current GTS from the simulator. */
    private GTS getCurrentGTS() {
    	return simulator.getCurrentGTS();
    }

    /** Convenience method to retrieve the currently selected transition from the simulator. */
    private Transition getCurrentTransition() {
    	return simulator.getCurrentTransition();
    }

    /** Convenience method to retrieve the currently selected state from the simulator. */
    private State getCurrentState() {
    	return simulator.getCurrentState();
    }

    /** Convenience method to retrieve the currently selected rule from the simulator. */
    private Rule getCurrentRule() {
    	return simulator.getCurrentRule();
    }

    /**
     * Creates the selection listener to be used to
     * react on selections in this rule directory.
     * The current implementation returns a <tt>RuleSelectionListener</tt>.
     * @see RuleSelectionListener
     */
    protected TreeSelectionListener createRuleSelectionListener() {
        return new RuleSelectionListener();
    }

    /** Creates a menu for this panel. */
    protected JPopupMenu createPopupMenu() {
        JPopupMenu res = new JPopupMenu();
        res.add(new JMenuItem(simulator.getApplyTransitionAction()));
        res.addSeparator();
        res.add(new AbstractAction(Options.EDIT_RULE_ACTION_NAME) {
            public void actionPerformed(ActionEvent evt) {
                simulator.handleEditRule();
            }
        });
        return res;
    }

    /**
     * Selection listener that invokes <tt>setRule</tt> if a
     * rule node is selected, and <tt>setDerivation</tt> if
     * a match node is selected.
     * @see Simulator#setRule
     * @see Simulator#setTransition
     */
    protected class RuleSelectionListener implements TreeSelectionListener {
        public void valueChanged(TreeSelectionEvent evt) {
            // only do something if a path was added to the selection
            if (listenToSelectionChanges && evt.isAddedPath()) {
                Object selectedNode = evt.getPath().getLastPathComponent();
                if (selectedNode instanceof RuleTreeNode) {
                    // selected tree node is a production rule (level 1 node)
                    simulator.setRule(((RuleTreeNode) selectedNode).name());
                } else if (selectedNode instanceof MatchTreeNode) {
                    // selected tree node is a derivation edge (level 2 node)
                    simulator.setTransition(((MatchTreeNode) selectedNode).edge());
                }
            }
        }
    }

    /**
     * Priority nodes (used only if the rule system has multiple priorities)
     */
    protected class PriorityTreeNode extends DefaultMutableTreeNode {
        /**
         * Creates a new priority node based on a given priority.
         * The node can (and will) have children.
         */
        public PriorityTreeNode(int priority) {
            super("Priority "+priority, true);
        }
    }

    /**
     * Rule nodes (= level 1 nodes) of the directory
     */
    protected class RuleTreeNode extends DefaultMutableTreeNode {
        /**
         * Creates a new rule node based on a given rule name.
         * The node can have children.
         */
        public RuleTreeNode(StructuredRuleName name) {
            super(name, true);
        }

        /**
         * Convenience method to retrieve the user object as a rule name.
         */
        public StructuredRuleName name() {
            return (StructuredRuleName) getUserObject();
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
     * Directory nodes (= level 0 nodes) of the directory
     */
    protected class DirectoryTreeNode extends DefaultMutableTreeNode {
        /**
         * Creates a new rule node based on a given rule name.
         * The node can have children.
         */
        public DirectoryTreeNode(StructuredRuleName name) {
            super(name, true);
        }

        /**
         * Convenience method to retrieve the user object as a rule name.
         */
        public StructuredRuleName name() {
            return (StructuredRuleName) getUserObject();
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
     * Match nodes (= level 2 nodes) of the directory.
     * Stores a <tt>Transition</tt> as user object.
     */
    protected class MatchTreeNode extends DefaultMutableTreeNode {
        /**
         * Creates a new match node on the basis of a given number and derivation edge.
         * The node cannot have children.
         */
        public MatchTreeNode(int nr, Transition edge) {
            super(edge, false);
            this.nr = nr;
        }

        /**
         * Convenience method to return the underlying derivation edge.
         */
        public GraphTransition edge() {
            return (GraphTransition) getUserObject();
        }

        /**
         * Object identity is good enough as a notion of equality.
         */
        @Override
        public boolean equals(Object obj) {
            return this == obj;
        }

        /**
         * A description of this derivation edge in the rule directory.
         * Returns <tt>"Match ??</tt>, where <tt>??</tt> is the node number.
         */
        @Override
        public String toString() {
            return simulator.getOptions().getValue(Options.SHOW_ANCHORS_OPTION) ? edge().getEvent().getAnchorImageString() : "Match " + nr;
        }

        /** The number of this match, used in <tt>toString()</tt> */
        private final int nr;
    }

    /**
     * Class to provide proper icons for directory nodes
     */
    protected class MyRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(
            JTree tree,
            Object value,
            boolean sel,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus) {
            // failed attempt to get rid of root handles for childless nodes 
            // expanded = expanded ||
            //            ((DefaultMutableTreeNode) value).getChildCount()==0;
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

            if (value instanceof DirectoryTreeNode) {
                setIcon(Groove.GPS_FOLDER_ICON);
            } else if (value instanceof PriorityTreeNode) {
                setIcon(null);
            }

            return this;
        }
    }

    /**
     * Directory of production rules and their matchings to the current state.
     * Alias to the underlying model of this <tt>JTree</tt>.
     * @invariant <tt>ruleDirectory == getModel()</tt>
     */
    protected final DefaultTreeModel ruleDirectory;
    /**
     * Alias for the top node in <tt>ruleDirectory</tt>.
     * @invariant <tt>topDirectoryNode == ruleDirectory.getRoot()</tt>
     */
    protected final DefaultMutableTreeNode topDirectoryNode;
    /**
     * The simulator to which this directory belongs.
     * @invariant simulator != null
     */
    protected final Simulator simulator;
    /** 
     * Mapping from rule names in the current grammar to rule nodes in
     * the current rule directory.
     * @invariant <tt>ruleNodeMap: StructuredRuleName --> DirectoryTreeNode
     *                                               \cup RuleTreeNode</tt>
     */
    protected final Map<NameLabel,DefaultMutableTreeNode> ruleNodeMap = new HashMap<NameLabel,DefaultMutableTreeNode>();
    /** 
     * Mapping from derivation edges in the current LTS to match nodes in
     * the current rule directory.
     * @invariant <tt>matchNodeMap: Transition --> MatchTreeNode</tt>
     */
    protected final Map<Transition,MatchTreeNode> matchNodeMap = new HashMap<Transition,MatchTreeNode>();

    /**
     * Switch to determine wheter chnges in the tree selection model
     * should trigger any actions right now.
     */
    private transient boolean listenToSelectionChanges;

    /**
     * The background color of this component when it is enabled.
     */
    private Color enabledBackground;
    /** Flag to indicate that the anchor image option listener has been set. */
    private boolean anchorImageOptionListenerSet = false;
    private GraphState displayedState;
}
