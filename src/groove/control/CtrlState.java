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

import groove.graph.Element;
import groove.graph.Node;
import groove.trans.Recipe;
import groove.trans.Rule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * A state in a control automaton.
 * The state stores a set of bound variables, and the
 * set of its outgoing (control) transitions.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CtrlState implements Node {
    /**
     * Creates a control state with a given number.
     * @param aut the automaton for which this state is created
     * @param nr state number
     * @param recipe optional transaction name of which this is a transient state
     */
    public CtrlState(CtrlAut aut, Recipe recipe, int nr) {
        this.aut = aut;
        this.stateNumber = nr;
        this.recipe = recipe;
    }

    /** Returns the control automaton to which this state belongs. */
    public CtrlAut getAut() {
        return this.aut;
    }

    /** The control automaton to which this state belongs. */
    private final CtrlAut aut;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.stateNumber;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CtrlState other = (CtrlState) obj;
        if (this.stateNumber != other.stateNumber) {
            return false;
        }
        return true;
    }

    public int compareTo(Element obj) {
        if (obj instanceof CtrlState) {
            return getNumber() - ((CtrlState) obj).getNumber();
        } else {
            int result =
                getNumber() - ((CtrlTransition) obj).source().getNumber();
            if (result == 0) {
                result = -1;
            }
            return result;
        }
    }

    public int getNumber() {
        return this.stateNumber;
    }

    /** 
     * Indicates if this is a transient control state.
     * A control state is transient if there is a transaction underway.
     */
    public boolean isTransient() {
        return this.recipe != null;
    }

    /** 
     * Returns the (optional) name of a recipe of which this
     * is a transient state. 
     */
    public Recipe getRecipe() {
        return this.recipe;
    }

    @Override
    public String toString() {
        return "q" + this.stateNumber;
    }

    /**
     * Add an outgoing transition to this control state, if there was not
     * already a transition for the same rule.
     * @return {@code true} if the transition was added; {@code false} if
     * there was already a transition for the same rule
     */
    public boolean addTransition(CtrlTransition transition) {
        CtrlTransition oldTransition =
            this.outTransitions.put(transition.getRule(), transition);
        boolean result = oldTransition == null;
        if (!result) {
            this.outTransitions.put(transition.getRule(), oldTransition);
        }
        if (result && transition.hasRecipe()) {
            this.recipeCount++;
        }
        return result;
    }

    /**
     * Removes an outgoing transition from this control state.
     */
    public boolean removeTransition(CtrlTransition transition) {
        boolean result =
            this.outTransitions.remove(transition.getRule()) != null;
        if (result && transition.hasRecipe()) {
            this.recipeCount--;
        }
        return result;
    }

    /** Indicates if this control state has outgoing recipe transitions. */
    public boolean hasRecipes() {
        return this.recipeCount > 0;
    }

    /** Returns the outgoing control transition for a given rule, if any. */
    public CtrlTransition getTransition(Rule rule) {
        return this.outTransitions.get(rule);
    }

    /** Returns the outgoing control transitions of this control state. */
    public Collection<CtrlTransition> getTransitions() {
        return this.outTransitions.values();
    }

    /** 
     * Returns the set of rule names of outgoing transitions, or
     * {@code null} if there is an outgoing omega transition. 
     */
    public Set<CtrlCall> getInit() {
        Set<CtrlCall> result = new HashSet<CtrlCall>();
        for (CtrlTransition trans : getTransitions()) {
            CtrlCall call = trans.getCall();
            if (call.isOmega()) {
                result = null;
                break;
            } else {
                result.add(call);
            }
        }
        return result;
    }

    /**
     * Returns the set of bound variables in this state.
     */
    public List<CtrlVar> getBoundVars() {
        return this.boundVars;
    }

    /**
     * Adds bound variables to this state.
     */
    public void addBoundVars(Collection<CtrlVar> variables) {
        Set<CtrlVar> newVars = new TreeSet<CtrlVar>(variables);
        newVars.addAll(this.boundVars);
        this.boundVars.clear();
        this.boundVars.addAll(newVars);
    }

    /**
     * Sets the bound variables of this state to the elements of a given collection.
     */
    public void setBoundVars(Collection<CtrlVar> variables) {
        this.boundVars.clear();
        this.boundVars.addAll(new TreeSet<CtrlVar>(variables));
    }

    /** Lazily creates and returns the schedule for trying the outgoing transitions of this state. */
    public CtrlSchedule getSchedule() {
        if (this.schedule == null) {
            assert this.disabledMap == null;
            this.disabledMap = computeDisabledMap();
            this.schedule =
                getSchedule(new TreeSet<CtrlTransition>(getTransitions()),
                    Collections.<CtrlCall>emptySet(),
                    Collections.<CtrlCall>emptySet());
            // discard the map to save space
            this.scheduleMap = null;
        }
        return this.schedule;
    }

    /** Returns the (optional) guard under which the transient status is exited. */
    public Collection<CtrlCall> getExitGuard() {
        return this.exitGuard;
    }

    /**
     * Indicates if this state can exit its transient status under
     * some exit guard.
     * @see #getExitGuard()
     */
    public boolean hasExitGuard() {
        return getExitGuard() != null;
    }

    /** 
     * An optional guard under which the transient status of this
     * control state is lifted; i.e., the transaction is completed.
     */
    public void setExitGuard(Collection<CtrlCall> exitGuard) {
        assert isTransient() && this.exitGuard == null;
        this.exitGuard = exitGuard;
    }

    /** Computes a map from control calls to transitions disabled by those calls. */
    private Map<CtrlCall,Set<CtrlTransition>> computeDisabledMap() {
        // look for the transition that appears in the fewest guards
        Map<CtrlCall,Set<CtrlTransition>> result =
            new HashMap<CtrlCall,Set<CtrlTransition>>();
        for (CtrlTransition trans : getTransitions()) {
            for (CtrlCall call : trans.label().getGuard()) {
                Set<CtrlTransition> disablings = result.get(call);
                if (disablings == null) {
                    result.put(call, disablings = new HashSet<CtrlTransition>());
                }
                disablings.add(trans);
            }
        }
        return result;
    }

    /** 
     * Lazily creates a schedule for
     * a given set of remaining outgoing transitions, a set of calls that have been tried,
     * and a subset of the tried calls that have failed  
     * @param transSet the set of remaining transitions
     * @param triedCalls the set of calls that have been tried before arriving at this
     * schedule
     * @param failedCalls subset of {@code triedCalls} that have failed
     * @return a schedule for the given configuration
     */
    private CtrlSchedule getSchedule(Set<CtrlTransition> transSet,
            Set<CtrlCall> triedCalls, Set<CtrlCall> failedCalls) {
        if (this.scheduleMap == null) {
            this.scheduleMap =
                new HashMap<Set<CtrlTransition>,Map<Set<CtrlCall>,CtrlSchedule>>();
        }
        Map<Set<CtrlCall>,CtrlSchedule> auxMap = this.scheduleMap.get(transSet);
        if (auxMap == null) {
            this.scheduleMap.put(transSet, auxMap =
                new HashMap<Set<CtrlCall>,CtrlSchedule>());
        }
        CtrlSchedule result = auxMap.get(triedCalls);
        if (result == null) {
            auxMap.put(triedCalls,
                result = computeSchedule(transSet, triedCalls, failedCalls));
        }
        return result;
    }

    private CtrlSchedule computeSchedule(Set<CtrlTransition> transSet,
            Set<CtrlCall> triedCalls, Set<CtrlCall> failedCalls) {
        // look for the untried call with the least disablings
        CtrlTransition trans = null;
        Set<CtrlTransition> disablings = null;
        // boolean indicating that the omega rule guards have all been satisfied
        boolean success = false;
        for (CtrlTransition tryTrans : transSet) {
            Set<CtrlCall> guard =
                new HashSet<CtrlCall>(tryTrans.label().getGuard());
            guard.removeAll(triedCalls);
            if (!guard.isEmpty()) {
                continue;
            }
            if (tryTrans.getCall().isOmega()) {
                success = true;
                continue;
            }
            CtrlCall tryCall = tryTrans.getCall();
            Set<CtrlTransition> tryDisablings;
            if (this.disabledMap.containsKey(tryCall)) {
                tryDisablings =
                    new HashSet<CtrlTransition>(this.disabledMap.get(tryCall));
                tryDisablings.retainAll(transSet);
            } else {
                tryDisablings = Collections.emptySet();
            }
            if (trans == null || tryDisablings.size() < disablings.size()) {
                trans = tryTrans;
                disablings = tryDisablings;
            }
            if (disablings.size() == 0) {
                // we're not going to find a smaller anyway
                break;
            }
        }
        boolean isTransient = isTransient();
        if (hasExitGuard()) {
            Set<CtrlCall> guard = new HashSet<CtrlCall>(getExitGuard());
            guard.removeAll(failedCalls);
            isTransient = !guard.isEmpty();
        }
        CtrlSchedule result =
            new CtrlSchedule(this, trans, triedCalls, success, isTransient);
        if (trans != null) {
            Set<CtrlCall> newTriedCalls = new HashSet<CtrlCall>(triedCalls);
            newTriedCalls.add(trans.getCall());
            Set<CtrlCall> newFailedCalls = new HashSet<CtrlCall>(failedCalls);
            newFailedCalls.add(trans.getCall());
            Set<CtrlTransition> remainder =
                new LinkedHashSet<CtrlTransition>(transSet);
            remainder.remove(trans);
            CtrlSchedule failNext =
                getSchedule(remainder, newTriedCalls, newFailedCalls);
            CtrlSchedule succNext;
            if (disablings.isEmpty()) {
                succNext = failNext;
            } else {
                remainder = new LinkedHashSet<CtrlTransition>(remainder);
                remainder.removeAll(disablings);
                succNext = getSchedule(remainder, newTriedCalls, failedCalls);
            }
            result.setNext(succNext, failNext);
        }
        return result;
    }

    /** Internal number to identify the state. */
    private final int stateNumber;
    /** Mapping from rules to outgoing transitions. */
    private final Map<Rule,CtrlTransition> outTransitions =
        new HashMap<Rule,CtrlTransition>();
    /** The collection of bound variables of this control state. */
    private final List<CtrlVar> boundVars = new ArrayList<CtrlVar>();
    /** Optional name of a recipe of which this is a transient state. */
    private final Recipe recipe;
    /** If the state is transient, an optional guard under which the transient status is exited. */
    private Collection<CtrlCall> exitGuard;
    /** The schedule for trying the outgoing transitions of this state. */
    private CtrlSchedule schedule;
    /** Map from calls to transitions disabled by their success. */
    private Map<CtrlCall,Set<CtrlTransition>> disabledMap;
    /** Map storing the computed intermediate schedules, to enable sharing. */
    private Map<Set<CtrlTransition>,Map<Set<CtrlCall>,CtrlSchedule>> scheduleMap;
    /** Count of the number of outgoing recipe transitions. */
    private int recipeCount;
}
