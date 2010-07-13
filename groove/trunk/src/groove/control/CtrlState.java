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
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Node;
import groove.trans.Rule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a state in a control automaton. Typically a member of a
 * ControlShape. Can be viewed (as member of a GraphShape) in a viewer (Node
 * interface) Supplies methods for getting allowed outgoing transitions
 * (Location interface) (for exploration).
 * @author Tom Staijen
 * @version $Revision $
 */
public class CtrlState implements Node, Location {
    /**
     * Create a ControlState. A ControlState needs to know the ControlShape it
     * is in to be able to properly delete it.
     * @param parent
     */
    public CtrlState(ControlShape parent) {
        this.lambdaTargets = new HashSet<CtrlState>();
        this.stateNumber = Counter.inc();
    }

    public int compareTo(Element obj) {
        if (obj instanceof Node) {
            return getNumber() - ((Node) obj).getNumber();
        } else {
            return getNumber() - ((Edge) obj).source().getNumber();
        }
    }

    public int getNumber() {
        return this.stateNumber;
    }

    /**
     * Add an outgoing transition to this control state.
     */
    public void add(CtrlTransition transition) {
        this.outTransitions.add(transition);
    }

    @Override
    public String toString() {
        return (isSuccess() ? "S" : "q") + this.stateNumber;
    }

    /**
     * Sets this state to be a conditional success state
     * @param condition a map of Rules to String[], indicating which rules must 
     * fail with which input parameters in order for this state to be a success
     * state
     */
    public void addSuccess(Collection<String> condition) {
        if (this.successConditions == null) {
            this.successConditions = new HashSet<Collection<String>>();
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
    public Set<Collection<String>> getSuccessConditions() {
        return this.successConditions;
    }

    /**
     * Returns all control states reachable through a single lambda transition.
     */
    public HashSet<CtrlState> lambdaTargets() {
        return this.lambdaTargets;
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
     * @param variables
     */
    public void initializeVariables(List<String> variables) {
        for (String var : variables) {
            this.initializeVariable(var);
        }
    }

    /**
     * @param variables
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
     * @param variable
     * @return the index of the given variable in this ControlState's list 
     * of variables
     */
    public int getVariablePosition(String variable) {
        return this.initializedVariables.indexOf(variable);
    }

    /** Internal number to identify the state. */
    private final int stateNumber;
    /** Contains the targets of outgoing lambda-transitions of this state. */
    private final HashSet<CtrlState> lambdaTargets;
    /** Map from rules to sets of target states. */
    private final Set<CtrlTransition> outTransitions =
        new HashSet<CtrlTransition>();
    private final List<String> initializedVariables = new ArrayList<String>();

    /** Sets of rules which, if all rules in an element of this set fail, mean
     * this state is a success state.
     */
    private Set<Collection<String>> successConditions;

    @Override
    public String getName() {
        return this.toString();
    }

    public boolean isSuccess() {
        return this.successConditions.contains(Collections.emptySet());
    }

    @Override
    public boolean isSuccess(Set<Rule> rules) {
        // TODO: update this to include the parameters
        if (this.isSuccess()) {
            return true;
        } else {
            if (this.successConditions != null) {
                for (Collection<String> successCondition : this.successConditions) {
                    if (rules.containsAll(successCondition)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    @Override
    public Set<Rule> getEnabledRules(Set<Rule> matched, Set<Rule> failed) {
        // TODO Auto-generated method stub
        return null;
    }
}
