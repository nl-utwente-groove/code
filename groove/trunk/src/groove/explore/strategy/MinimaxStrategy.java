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

import groove.grammar.Rule;
import groove.grammar.host.AnchorValue;
import groove.grammar.host.ValueNode;
import groove.lts.GraphState;
import groove.lts.RuleTransition;
import groove.lts.StartGraphState;
import groove.transform.RuleEvent;

import java.util.LinkedList;
import java.util.Stack;

/**
 * TODO javadoc
 */
public class MinimaxStrategy extends ClosingStrategy {
    public static final int DEPTH_INFINITE = -1;
    //private boolean max = true; //determines whether we should maximize or minimize the gain
    private final LinkedList<Integer> nodevalues = new LinkedList<Integer>(); //contains the heuristic values for Minimax
    private final Stack<GraphState> explorationStack = new Stack<GraphState>();
    private final int heuristicparam;
    private final int maxdepth;

    /**
     * Constructs a strategy which uses the Minimax algorithm to generate a strategy while performing an optionally depth-bound DFS
     * @param heuristicparam
     * @param maxdepth
     */
    public MinimaxStrategy(int heuristicparam, int maxdepth) {
        super();
        this.heuristicparam = heuristicparam;
        if (maxdepth < 0) {
            this.maxdepth = DEPTH_INFINITE;
        } else {
            this.maxdepth = maxdepth;
        }
    }

    @Override
    protected GraphState getFromPool() {
        //TODO max depth
        if (this.explorationStack.isEmpty()) {
            return null;
        } else {
            GraphState result = this.explorationStack.pop();
            GraphState startstate = result.getGTS().startState();
            if (result.getMatch() == null) {
                //deze node is een final node, bereken score
                //int score = getHeuristicScore((RuleTransition) result, this.heuristicparam);

                //if (!result.equals(startstate)) { //casting goes wrong in starting graph
                if (!(result instanceof StartGraphState)) {
                    //determine minimization or maximization
                    int depth = 0;
                    GraphState s = result;
                    while (!s.equals(startstate)) {
                        //while (!(s instanceof StartGraphState)) {
                        s = ((RuleTransition) s).source();
                        depth++;
                    }
                    boolean mode = (depth % 2) == 0; //assume first state is maximization, so even number is a maximization

                    //execute the minimax algorithm
                    updateMinimax((RuleTransition) result, mode);
                    System.out.println(getMinimaxDebugTree());
                } else {
                    setNodeValue(result.getNumber(), getHeuristicScore((RuleTransition) result));
                }

            }
            return result;
        }
    }

    private void setNodeValue(int node, Integer value) {
        while (this.nodevalues.size() - 1 < node) { //grow the array
            this.nodevalues.add(null);
        }
        this.nodevalues.set(node, value);
    }

    private void unsetNodeValue(int node) {
        setNodeValue(node, null);
    }

    private Integer getNodeValue(int node) {
        if (node > this.nodevalues.size() - 1) {
            return null;
        } else {
            return this.nodevalues.get(node);
        }
    }

    /**
     * Inserts new score data into the graph
     * @param state the state which provided the score
     * @param max whether to maximize in {@code state} or to minimize
     */
    private void updateMinimax(RuleTransition state, boolean max) {
        int score = getHeuristicScore(state);
        boolean mode = !max; //true = max, false = min
        GraphState startstate = state.source().getGTS().startState();
        GraphState s = state.source();
        while (!s.equals(startstate)) { //cast gaat alleen goed buiten de start state
            //while (!(s instanceof StartGraphState)) {
            setValue(s.getNumber(), score, mode);
            //switch mode for next state
            mode = !mode;
            s = ((RuleTransition) s).source();
        }
        setValue(s.getNumber(), score, mode);
    }

    private void setValue(int node, int score, boolean max) {
        Integer currentvalue = getNodeValue(node);
        //set if the value is uninitialized
        if (currentvalue == null) {
            setNodeValue(node, score);
        } else {
            //determine new value
            int newvalue;
            if (max) {
                newvalue = Math.max(score, currentvalue);
            } else {
                newvalue = Math.min(score, currentvalue);
            }
            setNodeValue(node, Math.max(score, newvalue));
        }
    }

    @Override
    protected void putInPool(GraphState state) {
        this.explorationStack.push(state);
    }

    @Override
    protected void clearPool() {
        this.nodevalues.clear();
        this.explorationStack.clear();
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
     * @param parnum the parameter in s, containing the value
     * @return the value of the parameter after execution of transition s
     */
    private int getHeuristicScore(RuleTransition s) {
        return (Integer) ((ValueNode) MinimaxStrategy.getParameter(s, this.heuristicparam)).toJavaValue();
    }

    public String getMinimaxDebugTree() {
        //TODO tree representation
        return this.nodevalues.toString();
    }
}
