/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package groove.abstraction.gui;

import groove.gui.RuleJTree;
import groove.gui.Simulator;
import groove.trans.RuleEvent;

import java.util.HashMap;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * EDUARDO: Comment this...
 * @author Eduardo Zambon
 */
public class ShapeJTree extends RuleJTree {

    /**
     * Mapping from RuleMatches in the current LTS to pre-match nodes in the
     * rule directory.
     */
    protected final Map<RuleEvent,PrematchTreeNode> prematchNodeMap =
        new HashMap<RuleEvent,PrematchTreeNode>();

    /** EDUARDO: Comment this... */
    public ShapeJTree(Simulator simulator) {
        super(simulator);
    }

    @Override
    protected void clearMatchMaps() {
        super.clearMatchMaps();
        this.prematchNodeMap.clear();
    }

    @Override
    protected void refresh() {
        /*boolean oldListenToSelectionChanges = this.listenToSelectionChanges;
        this.listenToSelectionChanges = false;
        // remove current matches
        for (PrematchTreeNode prematchNode : this.prematchNodeMap.values()) {
            this.ruleDirectory.removeNodeFromParent(prematchNode);
        }
        this.clearMatchMaps();
        GraphState state = getCurrentState();
        Shape shape = (Shape) state.getGraph();
        GraphGrammar grammar = null;
        try {
            grammar = this.getSimulator().getGrammarView().toGrammar();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        for (Rule rule : grammar.getRules()) {
            Set<RuleEvent> prematches = shape.getPreMatchEvents(rule);
            for (RuleEvent event : prematches) {
                RuleTreeNode ruleNode = this.ruleNodeMap.get(rule.getName());
                assert ruleNode != null;
                int nrOfMatches = ruleNode.getChildCount();
                PrematchTreeNode prematchNode =
                    new PrematchTreeNode(nrOfMatches + 1, event);
                this.ruleDirectory.insertNodeInto(prematchNode, ruleNode,
                    nrOfMatches);
                expandPath(new TreePath(ruleNode.getPath()));
                this.prematchNodeMap.put(event, prematchNode);
            }
        }
        setEnabled(this.displayedGrammar != null);
        setBackground(getCurrentGTS() == null ? null : TREE_ENABLED_COLOR);
        this.listenToSelectionChanges = oldListenToSelectionChanges;*/
    }

    // refreshMatchesOpen
    // refreshMatchesClosed

    private class PrematchTreeNode extends DefaultMutableTreeNode {

        /** The number of this match, used in <tt>toString()</tt> */
        private final int nr;

        /**
         * Creates a new pre-match node on the basis of a given number and the
         * RuleMatch.
         */
        public PrematchTreeNode(int nr, RuleEvent event) {
            super(event, true);
            this.nr = nr;
        }

        @Override
        public String toString() {
            return "Pre-match " + this.nr;
        }

    }

}
