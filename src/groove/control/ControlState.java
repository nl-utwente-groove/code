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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
     */
    public void add(ControlTransition transition) {
        if (transition.isLambda()) {
            this.lambdaTargets.add(transition.target());
        } else if (transition.hasFailures()) {
            this.elseTransitions.add(transition);
        } else if (transition.hasLabel()) {
            // store targets by rule
            this.ruleTargetMap.put(transition.getRule(), transition);
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

    /**
     * Returns a set of success conditions for this ControlState, each of which 
     * is a map of Rules to String arrays, indicating which rules should fail 
     * with given parameters in order for this ControlState to be considered
     * a success state.
     * @return a Set<Map<Rule,String[]>> with success conditions
     */
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
    public ControlState target(Rule rule) {
        return this.ruleTargetMap.get(rule).target();
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
        if (!this.initializedVariables.contains(varName)) {
            this.initializedVariables.add(varName);
        }
    }

    /**
     * @return a set of initialized variables
     */
    public List<String> getInitializedVariables() {
        return this.initializedVariables;
    }

    /**
     * @param varName the variable to check
     * @return whether varName is initialized
     */
    public boolean isInitialized(String varName) {
        return this.initializedVariables.contains(varName);
    }

    /**
     * @param variables the list of variables
     */
    public void initializeVariables(List<String> variables) {
        for (String var : variables) {
            this.initializeVariable(var);
        }
    }

    /**
     * @param variables the list of variables
     */
    public void setInitializedVariables(List<String> variables) {
        this.initializedVariables.clear();
        initializeVariables(variables);
    }

    /**
     * Returns the name of the variable at the given position in the variables list
     * @param index the position of the parameter to return
     * @return the name of the variable at the given position in the variables list
     */
    public String getVariableName(int index) {
        return this.initializedVariables.get(index);
    }

    /**
     * Returns the index of the given variable in this ControlState's list
     * of variables
     * @return the index of the given variable in this ControlState's list 
     * of variables
     */
    public int getVariablePosition(String variable) {
        return this.initializedVariables.indexOf(variable);
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
    private final HashMap<Rule,ControlTransition> ruleTargetMap =
        new HashMap<Rule,ControlTransition>();
    /** Set of outgoing failure transitions. */
    private final Set<ControlTransition> elseTransitions =
        new HashSet<ControlTransition>();
    private final List<String> initializedVariables = new ArrayList<String>();

    /** Sets of rules which, if all rules in an element of this set fail, mean
     * this state is a success state.
     */
    private Set<Map<Rule,String[]>> successConditions;

    private boolean hasMerged = false;

    /**
     * TODO: return a Map<Rule,Parameter> or something, ensure that the caller
     * of this method knows that there are input parameters to be processed.
     * 
     * Since we know which variables are initialized we can ensure we're only
     * returning parameters which can actually be used.
     */
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
     * Returns the transition to be taken given a Rule and a set of Rules that failed
     * @param rule the Rule to apply
     * @return the transition to be taken from this state given the inputs
     */
    public ControlTransition getTransition(Rule rule) {
        ControlTransition ret = this.ruleTargetMap.get(rule);
        if (ret == null) {
            for (ControlTransition ct : this.elseTransitions) {
                if (ct.getRule() == rule) {
                    ret = ct;
                    break;
                }
            }
        }
        //System.out.println(this.toString() + ".getTransition("+ rule.getName().toString() + "): " + ret);
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
