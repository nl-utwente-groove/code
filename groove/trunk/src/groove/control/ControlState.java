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
import java.util.Map;
import java.util.Set;

/**
 * Represents a state in a control automaton. Typically a member of a
 * ControlShape. Can be viewed (as member of a GraphShape) in a viewer (Node
 * interface) Supplies methods for getting allowed outgoing transitions
 * (Location interface) (for exploration).
 * @author Tom Staijen
 * @version $Revision $
 */
public class ControlState implements Node, Location {
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
        return (isSuccess() ? "S" : "q") + this.stateNumber;
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
     * Sets this state to be a conditional success state
     * @param condition a map of Rules to String[], indicating which rules must 
     * fail with which input parameters in order for this state to be a success
     * state
     */
    public void addSuccess(Map<Rule,String[]> condition) {
        if (this.successConditions == null) {
            this.successConditions = new HashSet<Map<Rule,String[]>>();
        }
        this.successConditions.add(condition);
    }

    public Set<Map<Rule,String[]>> getSuccessConditions() {
        return this.successConditions;
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

    /** 
     * Returns the initial actions of this control state. 
     */
    public Map<String,ControlTransition> getInit() {
        return this.init;
    }

    /** 
     * Adds the initial actions of the passed state to the initial actions
     * of this state 
     */
    public void addInit(ControlState state) {
        this.init.putAll(state.getInit());
    }

    /** 
     * Adds a label to the initial actions of this state 
     * @param label the label to add
     */
    public void addInit(String label) {
        this.init.put(label, null);
    }

    /**
     * Adds a label and its corresponding ControlTransition to the initial
     * actions of this state
     * @param ct the ControlTransition to add
     */
    public void addInit(ControlTransition ct) {
        this.init.put(ct.getLabel(), ct);
    }

    /** Removes a label from the init of this state */
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

    /** The initial actions for this state. */
    private final HashMap<String,ControlTransition> init =
        new HashMap<String,ControlTransition>();
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

    /** Sets of rules which, if all rules in an element of this set fail, mean
     * this state is a success state.
     */
    private Set<Map<Rule,String[]>> successConditions;

    private boolean hasMerged = false;

    private String name = "";

    @Override
    public Set<Rule> getDependency(Rule rule) {
        Set<Rule> ret = new HashSet<Rule>();
        // this is a rule that has a failure dependency
        if (!this.ruleTargetMap.containsKey(rule)) {
            for (ControlTransition ct : this.elseTransitions) {
                if (ct.getRule() == rule) {
                    ret.addAll(ct.getFailureSet());
                }
            }
        }
        return ret;
    }

    @Override
    public Set<Rule> getEnabledRules(Set<Rule> matched, Set<Rule> failed) {
        Set<Rule> ret = new HashSet<Rule>();
        // add all the rules that are enabled by default:
        for (Rule r : this.ruleTargetMap.keySet()) {
            if (!matched.contains(r) && !failed.contains(r)) {
                ret.add(r);
            }
        }

        // now add the rules for which the failures are satisfied
        for (ControlTransition ct : this.elseTransitions) {
            if (!matched.contains(ct.getRule())
                && !failed.contains(ct.getRule())
                && failed.containsAll(ct.getFailureSet())) {
                ret.add(ct.getRule());
            }
        }
        return ret;
    }

    @Override
    public String getName() {
        return this.toString();
    }

    /**
     * TODO: perhaps failedRules is not needed here anymore, since a state can 
     * only have one transition for a given rule. Thus, if the rule has already
     * been reported as "allowed to match", we can find the target and return it.
     */
    @Override
    public Location getTarget(Rule rule, Set<Rule> failedRules) {
        Location ret = null;
        if (this.ruleTargetMap.containsKey(rule)) {
            ret = this.ruleTargetMap.get(rule).iterator().next();
        } else {
            for (ControlTransition ct : this.elseTransitions) {
                if (ct.getRule() == rule) {
                    //if (failedRules.containsAll(ct.getFailureSet())) {
                    ret = ct.target();
                    break;
                    //}
                }
            }
        }
        return ret;
    }

    @Override
    public boolean isSuccess(Set<Rule> rules) {
        // TODO: update this to include the parameters
        if (this.isSuccess()) {
            return true;
        } else {
            if (this.successConditions != null) {
                for (Map<Rule,String[]> successCondition : this.successConditions) {
                    if (rules.containsAll(successCondition.keySet())) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
