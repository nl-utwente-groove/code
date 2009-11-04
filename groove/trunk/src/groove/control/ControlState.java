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
 * $Id: ControlState.java,v 1.10 2008-01-30 12:37:39 fladder Exp $
 */
package groove.control;

import groove.control.parse.Counter;
import groove.graph.Element;
import groove.graph.Node;
import groove.trans.Rule;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a state in a control automaton. Typically a member of a
 * ControlShape. Can be viewed (as member of a GraphShape) in a viewer (Node
 * interface) Supplies methods for getting allowed outgoing transitions
 * (Location interface) (for exploration).
 * @author Tom Staijen
 * @version $Revision $
 */
public class ControlState implements Node {
    /**
     * Create a ControlState. A ControlState needs to know the ControlShape it
     * is in to be able to properly delete it.
     * @param parent
     */
    public ControlState(ControlShape parent) {
        this.parent = parent;
        this.lambdaTargets = new HashSet<ControlState>();
        this.stateNumber = Counter.inc();
    }

    public int compareTo(Element obj) {
        return hashCode() - ((ControlState) obj).hashCode();
    }

    public int getNumber() {
        return this.stateNumber;
    }

    /**
     * Add an outgoing transition to this control state.
     * @param transition
     */
    public void add(ControlTransition transition) {
        if (transition.isLambda()) {
            this.lambdaTargets.add(transition.target());
        } else if (transition.hasFailures()) {
            this.elseTransitions.add(transition);
        } else if (transition.hasLabel()) {
            Rule rule = transition.getRule();
            // store targets by rule
            Set<ControlState> targetSet = this.ruleTargetMap.get(rule);
            if (targetSet == null) {
                this.ruleTargetMap.put(rule, targetSet =
                    new HashSet<ControlState>());
            }
            targetSet.add(transition.target());
        } else {
            // should never be reached
            assert false;
        }
    }

    @Override
    public String toString() {
        String variables = this.initializedVariables.isEmpty() ? "" :
                                    " " + this.initializedVariables.toString();
        return (isSuccess() ? "S" : "q") + this.stateNumber + variables;
    }

    /**
     * Indicates whether this control state is a success state
     */
    public boolean isSuccess() {
        return this.success;
    }

    /**
     * Set this state to be a success state
     */
    public void setSuccess() {
        this.success = true;
    }

    /**
     * Returns the Shape this state is owned by.
     */
    public ControlShape getParent() {
        return this.parent;
    }

    /**
     * Returns all control states reachable through a single lambda transition.
     */
    public HashSet<ControlState> lambdaTargets() {
        return this.lambdaTargets;
    }

    /**
     * Returns the outgoing failure transitions.
     */
    public Set<ControlTransition> elseTransitions() {
        return this.elseTransitions;
    }

    /**
     * Returns the set of target states for a given rule.
     * @return the set of target states; may be <code>null</code> if the rule
     *         is not enabled in this state.
     */
    public Set<ControlState> targets(Rule rule) {
        return this.ruleTargetMap.get(rule);
    }

    /**
     * Returns the set of enabled rules at this control state.
     */
    public Set<Rule> rules() {
        return this.ruleTargetMap.keySet();
    }

    /** Returns the init of this control state. */
    public Set<String> getInit() {
        return this.init;
    }

    /** Adds the init of the passed state to the init of this state * */
    public void addInit(ControlState state) {
        this.init.addAll(state.getInit());
    }

    /** Adds a label to the init of this state * */
    public void addInit(String label) {
        this.init.add(label);
    }

    /** Removes a label from the init of this state * */
    public void delInit(String label) {
        this.init.remove(label);
    }

    /**
     * Marks a variable as active (ready to use as input)
     * @param varName the name of the variable
     */
    public void initializeVariable(String varName) {
        this.initializedVariables.add(varName);
    }
    
    /**
     * @return a set of initialized variables
     */
    public Set<String> getInitializedVariables() {
        return this.initializedVariables;
    }
    
    /**
     * @param variables
     */
    public void initializeVariables(Set<String> variables) {
        this.initializedVariables.addAll(variables);
    }
    
    /**
     * @param variables
     */
    public void setInitializedVariables(Set<String> variables) {
        this.initializedVariables.clear();
        initializeVariables(variables);
    }
    
    /**
     * Mark this state as merged.
     */
    public void setMerged() {
        this.hasMerged = true;
    }
    
    /**
     * @return the merged flag of this state.
     */
    public boolean getMerged() {
        return this.hasMerged;
    }

    private final HashSet<String> init = new HashSet<String>();
    private final ControlShape parent;
    /** Internal number to identify the state. */
    private final int stateNumber;
    /** hold the 'success' property of the state. */
    private boolean success = false;
    /** Contains the targets of outgoing lambda-transitions of this state. */
    private final HashSet<ControlState> lambdaTargets;
    /** Map from rules to sets of target states. */
    private final HashMap<Rule,Set<ControlState>> ruleTargetMap =
        new HashMap<Rule,Set<ControlState>>();
    /** Set of outgoing failure transitions. */
    private final Set<ControlTransition> elseTransitions =
        new HashSet<ControlTransition>();
    private final Set<String> initializedVariables = new HashSet<String>();
    
    private boolean hasMerged = false;
}
