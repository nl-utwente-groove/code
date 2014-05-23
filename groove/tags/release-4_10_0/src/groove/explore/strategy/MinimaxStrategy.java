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

import groove.explore.result.Acceptor;
import groove.grammar.Rule;
import groove.grammar.host.AnchorValue;
import groove.grammar.host.ValueNode;
import groove.lts.GTS;
import groove.lts.GTSListener;
import groove.lts.GraphState;
import groove.lts.GraphState.Flag;
import groove.lts.GraphTransition;
import groove.lts.RuleTransition;
import groove.transform.RuleEvent;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * TODO javadoc
 * TODO catch exception when rule without parameter is evaluated
 */
public class MinimaxStrategy extends ClosingStrategy implements GTSListener {
    /** Constant used to disable bounded exploration */
    public static final int DEPTH_INFINITE = 0;
    private static final boolean DEBUG = true;

    //internal storage
    private final LinkedList<MinimaxTree> nodes = new LinkedList<MinimaxTree>(); //contains the heuristic values for Minimax

    //exploration stack (DFS)
    private final ArrayDeque<GraphState> explorationStack = new ArrayDeque<GraphState>(); //thread unsafe stack

    //configurable parameters
    private final ArrayList<String> enabledrules;
    private final boolean startmax; //determines whether we should maximize or minimize in the first state
    private final int heuristicparam; //index of the heuristic parameter used
    private final int maxdepth; //maximum depth of the exploration

    /**
     * Constructs a strategy which uses the Minimax algorithm to generate a strategy while performing an optionally depth-bound DFS
     * @param heuristicparam parameter index which will contain the heuristic score
     * @param maxdepth the maximum depth of the exploration, below 1 is infinite
     */
    public MinimaxStrategy(int heuristicparam, int maxdepth) {
        this(heuristicparam, maxdepth, null, true);
    }

    /**
     * Constructs a strategy which uses the Minimax algorithm to generate a strategy while performing an optionally depth-bound DFS
     * @param heuristicparam parameter index which will contain the heuristic score
     * @param maxdepth the maximum depth of the exploration, below 1 is infinite
     * @param enabledrules a collection of enabled rules, duplicates will be removed
     * @param startmax true when the search should attempt to maximize the gains, false when the gains should be minimized
     */
    public MinimaxStrategy(int heuristicparam, int maxdepth, Collection<String> enabledrules,
            boolean startmax) {
        super();

        //parameter
        this.heuristicparam = heuristicparam;

        //maximum depth
        if (maxdepth < 1) {
            this.maxdepth = DEPTH_INFINITE;
        } else {
            this.maxdepth = maxdepth;
        }

        //enabled rules list
        if (enabledrules == null) {
            this.enabledrules = new ArrayList<String>(0);
        } else {
            //trim double entries
            this.enabledrules = new ArrayList<String>(new HashSet<String>(enabledrules));

            //prevent potential errors by not allowing null rule names
            if (enabledrules.contains(null)) {
                enabledrules.remove(null);
            }
        }

        //starting operation (min or max)
        this.startmax = startmax;
    }

    @Override
    public void prepare(GTS gts, GraphState state, Acceptor acceptor) {
        super.prepare(gts, state, acceptor);
        getGTS().addLTSListener(this);
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
        result = ev.getAnchorImage(r.getParBinding(num).getIndex());
        return result;
    }

    /**
     * Obtains the value of the heuristic from a parameter in a transition
     * @param s the transition to obtain the value from
     * @return the value of the parameter after execution of transition s
     */
    private int getHeuristicScore(RuleTransition s) {
        try {
            return (Integer) ((ValueNode) MinimaxStrategy.getParameter(s, this.heuristicparam)).toJavaValue();
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new RuntimeException("Parameter does not exist");
        } catch (ClassCastException e) {
            throw new RuntimeException("Parameter should be of type integer");
        }
    }

    /**
     * function which prints a tree like string
     */
    public void printMinimaxDebugTree() {
        //TODO tree representation
        System.out.println(getNodeValue(0).toString());
    }

    //
    //
    //Minimax storage functions

    private void setNodeValue(int node, MinimaxTree value) {
        while (this.nodes.size() - 1 < node) { //grow the array
            this.nodes.add(null);
        }
        this.nodes.set(node, value);
    }

    private void unsetNodeValue(int node) {
        setNodeValue(node, null);
    }

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
    private boolean isRuleEnabled(Rule r) {
        return this.enabledrules == null || this.enabledrules.size() == 0
            || this.enabledrules.contains(r.getTransitionLabel());
    }

    @Override
    public void finish() {
        super.finish();
        if (DEBUG) {
            System.out.println("Exploration Finished!");
            printMinimaxDebugTree();
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
            mts = new MinimaxTreeNode(source.getNumber());
        }

        //update the score
        if (isRuleEnabled(transition.getInitial().getAction()) && target.getMatch() == null) {
            int score = getHeuristicScore((RuleTransition) transition);
            if (mtt == null) {
                mtt = new MinimaxTreeLeaf(target.getNumber(), score);
            } else {
                ((MinimaxTreeLeaf) mtt).setScore(score);
            }
        }

        //update references
        if (mtt == null) {
            //when mtt is still null (didnt exist, and rule was not enabled) create an empty node
            mtt = new MinimaxTreeNode(target.getNumber());
        }
        ((MinimaxTreeNode) mts).addChild(mtt); //add child reference, set interface ensures uniqueness
        setNodeValue(source.getNumber(), mts);
        setNodeValue(target.getNumber(), mtt);
    }

    @Override
    public void statusUpdate(GTS gts, GraphState state, Flag flag) {
        //unnessecary when all transitions are already handled
    }

    @Override
    public void addUpdate(GTS gts, GraphState state) {
        //dont do anything with states
    }

    //
    //storage classes

    /**
     * Abstract class to store the internal minimax heuristic scores as a tree
     * @author Rick
     * @version $Revision $
     */
    private abstract class MinimaxTree {
        private int nodeno;

        /**
         * Constructs an entity in a minimax tree 
         * @param nodeno the corresponding node number from the LTS
         */
        public MinimaxTree(int nodeno) {
            this.nodeno = nodeno;
        }

        public int getNodeno() {
            return this.nodeno;
        }

        public abstract Integer getScore(boolean max);

        public abstract String getText(boolean max);

        @Override
        public String toString() {
            String result = "[" + getNodeno() + ":" + getText(MinimaxStrategy.this.startmax) + "]";
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof MinimaxTree) {
                return this.nodeno == ((MinimaxTree) obj).nodeno;
            }
            return super.equals(obj);
        }
    }

    /**
     * A leaf node, which contains a score, which is always returned on getScore(...)
     * @author Rick
     * @version $Revision $
     */
    private class MinimaxTreeLeaf extends MinimaxTree {

        private Integer score;

        public MinimaxTreeLeaf(int nodeno, Integer score) {
            super(nodeno);
            this.score = score;
        }

        @Override
        public Integer getScore(boolean max) {
            return this.score;
        }

        /**
         * @param score The score to set.
         */
        public void setScore(Integer score) {
            this.score = score;
        }

        @Override
        public String getText(boolean max) {
            return this.score + "";
        }
    }

    /**
     * A branch node in the tree, can contain other nodes or leaves
     * @author Rick
     * @version $Revision $
     */
    private class MinimaxTreeNode extends MinimaxTree {
        private Set<MinimaxTree> children = new HashSet<MinimaxTree>();

        public MinimaxTreeNode(int nodeno) {
            super(nodeno);
        }

        /**
         * Obtain the children of this node
         * @return
         */
        public Set<MinimaxTree> getChildren() {
            return this.children;
        }

        /**
         * Add a child to this node
         * @param mt
         */
        public void addChild(MinimaxTree mt) {
            getChildren().add(mt);
        }

        @Override
        public Integer getScore(boolean max) {
            Integer result = null;
            if (getChildren().size() > 0) { //only valid scores exist when there is at least one leaf node
                //calculate the tree score
                for (MinimaxTree mt : getChildren()) {
                    Integer mtscore = mt.getScore(!max); //flip the mode for all children
                    if (result == null) {
                        //if there is no maximum or minimum yet, any value will do (even nulls)
                        result = mtscore;
                    } else if (mtscore != null) {
                        if (max) {
                            result = Math.max(result, mtscore);
                        } else {
                            result = Math.min(result, mtscore);
                        }
                    }
                }
            }
            return result;
        }

        @Override
        public String getText(boolean max) {
            String result = getScore(max) + " ";
            for (MinimaxTree mt : getChildren()) {
                result = result + mt.toString();
            }
            return result;
        }
    }
}
