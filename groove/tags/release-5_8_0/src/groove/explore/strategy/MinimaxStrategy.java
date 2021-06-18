/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.explore.strategy;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import groove.explore.result.Acceptor;
import groove.grammar.Rule;
import groove.grammar.host.AnchorValue;
import groove.grammar.host.ValueNode;
import groove.lts.GTS;
import groove.lts.GTSListener;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.RuleTransition;
import groove.transform.RuleEvent;

/**
 * An exploration strategy which calculates the Minimax value of the starting state and all states reachable from it.
 */
public class MinimaxStrategy extends ClosingStrategy implements GTSListener {
    /** Constant used to disable bounded exploration */
    public static final int DEPTH_INFINITE = 0;
    private static final boolean DEBUG = false;
    private static final boolean VERBOSE = DEBUG && false;

    private long timer;

    //internal storage
    private final LinkedList<MinimaxTree> nodes = new LinkedList<>(); //contains the heuristic values for Minimax

    //exploration stack (DFS)
    private final ArrayDeque<GraphState> explorationStack = new ArrayDeque<>(); //unsynchronized stack

    //configurable parameters
    private final int heuristicparam; //index of the heuristic parameter used
    private final int minmaxparam; //index of the turn parameter used
    private final ArrayList<String> enabledrules; //names of evaluation rules
    private final String minmaxRule; //name of the turn rule
    private final int maxdepth; //maximum depth of the exploration

    /**
     * Constructs a strategy which uses the Minimax algorithm to generate a strategy while performing an optionally depth-bound DFS
     * @param heuristicparam parameter index which will contain the heuristic score
     * @param maxdepth the maximum depth of the exploration, below 1 is infinite
     */
    public MinimaxStrategy(int heuristicparam, int maxdepth, Rule evalrule, int minmaxparam) {
        this(heuristicparam, maxdepth, null, evalrule, minmaxparam);
    }

    /**
     * Constructs a strategy which uses the Minimax algorithm to generate a strategy while performing an optionally depth-bound DFS
     * @param heuristicparam parameter index which will contain the heuristic score
     * @param maxdepth the maximum depth of the exploration, below 1 is infinite
     * @param enabledrules a collection of enabled rules, duplicates will be removed
     */
    public MinimaxStrategy(int heuristicparam, int maxdepth, Collection<Rule> enabledrules,
        Rule evalrule, int minmaxparam) {
        super();

        //parameters
        this.heuristicparam = heuristicparam;
        this.minmaxparam = minmaxparam;

        //maximum depth
        if (maxdepth < 1) {
            this.maxdepth = DEPTH_INFINITE;
        } else {
            this.maxdepth = maxdepth;
        }

        //enabled rules list
        if (enabledrules == null) {
            this.enabledrules = new ArrayList<>(0);
        } else {
            //trim double entries
            HashSet<String> temp = new HashSet<>();
            for (Rule r : enabledrules) {
                temp.add(r.getTransitionLabel());
            }
            this.enabledrules = new ArrayList<>(temp);

            //prevent potential errors by not allowing null rule names
            if (enabledrules.contains(null)) {
                enabledrules.remove(null);
            }
        }

        //check for an empty string and assign rule parameter
        if ("".equals(evalrule.getTransitionLabel())) {
            this.minmaxRule = null;
        } else {
            this.minmaxRule = evalrule.getTransitionLabel();
        }
    }

    @Override
    protected void prepare(GTS gts, GraphState state, Acceptor acceptor) {
        super.prepare(gts, state, acceptor);
        getGTS().addLTSListener(this);
        this.timer = System.currentTimeMillis();
    }

    @Override
    protected GraphState getFromPool() {
        if (this.explorationStack.isEmpty()) {
            return null;
        } else {
            GraphState result = this.explorationStack.pop();
            GraphState startstate = getStartState();
            int depth = getNodeDepth(result, startstate);
            if (this.maxdepth != DEPTH_INFINITE && depth > this.maxdepth) { //if the next node is too deep, do not explore it
                result = this.getFromPool(); //explore the next node (the current node is already popped from the stack)
            }
            return result;
        }
    }

    @Override
    protected void putInPool(GraphState state) {
        this.explorationStack.push(state);
    }

    @Override
    protected void clearPool() {
        this.nodes.clear();
        this.explorationStack.clear();
    }

    /**
     * Calculates distance between s and target
     * Requires that by going up in the tree from s, target will eventually be reached
     * @param s the state to start from
     * @param target the state to measure the distance to
     * @return the distance between s and target
     */
    public static int getNodeDepth(GraphState s, GraphState target) {
        int depth = 0;
        GraphState state = s;
        while (!state.equals(target)) {
            state = ((RuleTransition) state).source();
            depth++;
        }
        return depth;
    }

    /**
     * Obtain a parameter object from a transition
     * @param s the transition to obtain the object from
     * @param num the index of the parameter in the transition
     * @return the anchored value of the parameter in the rule transition
     */
    public static AnchorValue getParameter(RuleTransition s, int num) {
        AnchorValue result = null;
        Rule r = s.getAction();
        RuleEvent ev = s.getEvent();
        result = ev.getAnchorImage(r.getParBinding(num)
            .getIndex());
        return result;
    }

    /**
     * Obtains the value of the heuristic from a parameter in a transition
     * @param s the transition to obtain the value from
     * @return the value of the parameter after execution of transition s
     */
    private int getHeuristicScore(RuleTransition s) {
        try {
            return (Integer) ((ValueNode) MinimaxStrategy.getParameter(s, this.heuristicparam))
                .toJavaValue();
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new RuntimeException("Parameter does not exist");
        } catch (ClassCastException e) {
            throw new RuntimeException("Parameter should be of type integer");
        }
    }

    /**
     * Obtains the value of the minmax rule from a parameter in a transition
     * @param s the transition to obtain the value from
     * @return the value of the parameter after execution of transition s
     */
    private Boolean getMinMaxParam(RuleTransition s) {
        try {
            return (Boolean) ((ValueNode) MinimaxStrategy.getParameter(s, this.minmaxparam))
                .toJavaValue();
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new RuntimeException("Parameter does not exist");
        } catch (ClassCastException e) {
            throw new RuntimeException("Parameter should be of type boolean");
        }
    }

    /**
     * Function which exports a tree like string to a file
     * The file is overwritten by this method
     */
    public void printMinimaxDebugTree(File out) {
        MinimaxTree mt = getNodeValue(this.getStartState()
            .getNumber());
        try { //write to a file, as tree representations can get quite large (10MB for tic-tac-toe)
            File f = out;
            if (f.exists()) {
                f.delete();
                f.createNewFile();
            }
            try (PrintWriter pw = new PrintWriter(f)) {
                pw.println(mt.toString());
                pw.flush();
            }
            System.out.println("Wrote tree to file: " + f.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //
    //
    //Minimax storage functions

    /**
     * Set the tree node at the given position
     * @param node the position of the assigned tree node
     * @param value the assigned tree node
     */
    private void setNodeValue(int node, MinimaxTree value) {
        while (this.nodes.size() - 1 < node) { //grow the array
            this.nodes.add(null);
        }
        this.nodes.set(node, value);
    }

    /**
     * Obtain the tree node stored at the given position
     * @param node the position
     * @return the tree node stored at the given position, or null if no tree node has been stored at that position.
     */
    private MinimaxTree getNodeValue(int node) {
        if (node > this.nodes.size() - 1) {
            return null;
        } else {
            return this.nodes.get(node);
        }
    }

    /**
     * Tests whether a specific rule has been enabled for minimax evaluation
     * @param r the rule to test for
     * @return true when the label of r is in the list of enabled rules, or when all labels are allowed
     */
    private boolean isRuleEnabled(String r) {
        return this.enabledrules == null || this.enabledrules.size() == 0
            || this.enabledrules.contains(r);
    }

    private boolean isMinMaxrule(String r) {
        return this.minmaxRule.equals(r);
    }

    @Override
    public void finish() {
        super.finish();
        if (DEBUG) {
            System.out.println("Exploration Finished! It took "
                + (System.currentTimeMillis() - this.timer) + "ms");
            printMinimaxDebugTree(new File("tree.txt"));
        }
    }

    @Override
    public void addUpdate(GTS gts, GraphTransition transition) {
        //update the tree datastructure
        GraphState source = transition.source();
        GraphState target = transition.target();
        MinimaxTree mts = getNodeValue(source.getNumber());
        MinimaxTree mtt = getNodeValue(target.getNumber());
        if (mts == null) {
            assert source.getNumber() == this.getStartState()
                .getNumber(); //the source node only exists at the first node
            mts = new MinimaxTree(source.getNumber());
            setNodeValue(mts.getNodeno(), mts);
        }
        if (mtt == null) {
            mtt = new MinimaxTree(target.getNumber());
            setNodeValue(mtt.getNodeno(), mtt);
        }
        if (VERBOSE) {
            System.out.println("State added: " + transition.target()
                .getNumber());
        }
        //if we have a minmax rule, update the variable in the tree, and dont add tree nodes
        if (isMinMaxrule(transition.label()
            .getAction()
            .getLastName())) {
            Boolean minmax = getMinMaxParam((RuleTransition) transition);
            mts.setMinMax(minmax);
        } else {
            //update the score
            if (isRuleEnabled(transition.label()
                .getAction()
                .getLastName()) && target.getMatch() == null) {
                int score = getHeuristicScore((RuleTransition) transition);
                mtt.setScore(score);
            }
        }
        mts.addChild(mtt); //add child reference, set interface ensures uniqueness
        if (VERBOSE) {
            System.out.printf("Child added: %s for %s%n", mtt.getNodeno(), mts.getNodeno());
        }
    }

    //
    //storage classes

    /**
     * Abstract class to store the internal minimax heuristic scores as a tree
     * @author Rick
     * @version $Revision $
     */
    private class MinimaxTree {
        private int nodeno;
        private boolean max;
        private Integer score = null;
        private Set<MinimaxTree> children = null;

        /**
         * Constructs an entity in a minimax tree
         * @param nodeno the corresponding node number from the LTS
         */
        public MinimaxTree(int nodeno) {
            this.nodeno = nodeno;
        }

        /**
         * Obtain the node number of this tree node
         * @return the node number
         */
        public int getNodeno() {
            return this.nodeno;
        }

        /**
         * Set whether to maximize or minimize the values of children nodes
         * @param max true to maximize, false to minimize
         */
        public void setMinMax(boolean max) {
            this.max = max;
        }

        /**
         * Obtains whether this node will maximize or minimize the values of children nodes
         * @return true when this node will maximize the values of children nodes
         */
        public boolean getMinMax() {
            return this.max;
        }

        /**
         * Checks whether this tree node functions as a leaf node
         * @return true when this node is a leaf node
         */
        public boolean isLeafNode() {
            return this.score != null;
        }

        /**
         * Obtain the children of this node, converts this node to a tree node
         * @return a set with the children of this node
         */
        public Set<MinimaxTree> getChildren() {
            ensureChildren();
            return this.children;
        }

        /**
         * Add a child to this node, converts this node to a tree node
         * @param mt the node to be added as a child of this node
         */
        public void addChild(MinimaxTree mt) {
            ensureChildren();
            if (mt != this && !mt.isDescendant(this)) {
                getChildren().add(mt);
            } else {
                if (VERBOSE) {
                    System.out.println("Prevented cycle to node: " + mt.getNodeno());
                }
            }
        }

        /**
         * Checks whether a node is a descendant of this node
         * @param mt the node to look for
         * @return true if mt is a descendant of this node
         */
        public boolean isDescendant(MinimaxTree mt) {
            if (isLeafNode()) {
                return false;
            } else {
                for (MinimaxTree child : getChildren()) {
                    if (child.isDescendant(mt)) {
                        return true;
                    }
                }
                return false;
            }
        }

        /**
         * Obtain the minimax score of this node
         * @return the minimax score of this node
         */
        public Integer getScore() {
            Integer result = null;
            if (isLeafNode()) {
                result = this.score;
            } else {
                if (getChildren().size() > 0) { //only valid scores exist when there is at least one leaf node
                    //calculate the tree score
                    for (MinimaxTree mt : getChildren()) {
                        Integer mtscore = mt.getScore();
                        if (result == null) {
                            //if there is no maximum or minimum yet, any value will do (even nulls)
                            result = mtscore;
                        } else if (mtscore != null) {
                            if (getMinMax()) {
                                result = Math.max(result, mtscore);
                            } else {
                                result = Math.min(result, mtscore);
                            }
                        }
                    }
                }
            }
            return result;
        }

        /**
         * Sets the heuristic score of this node. Converts this node to a leaf node.
         * @param score The score to set.
         */
        public void setScore(Integer score) {
            this.children = null;
            this.score = score;
        }

        /**
         * Helper method for recursive printing of tree nodes.
         * @return a string representation of the score of this node, and the representations of all child nodes.
         */
        private String getText() {
            String result = getScore() + "";
            if (!isLeafNode()) {
                result = result + " ";
                for (MinimaxTree mt : getChildren()) {
                    result = result + mt.toString();
                }
            }
            return result;
        }

        @Override
        public String toString() {
            String result =
                "[" + getNodeno() + ";" + (getMinMax() ? "min()" : "max()") + ":" + getText() + "]";
            return result;
        }

        /**
         * Converts this node to a tree node if it is not already a tree node.
         */
        private void ensureChildren() {
            if (this.children == null) {
                this.children = new HashSet<>();
                this.score = null;
            }
        }
    }
}
