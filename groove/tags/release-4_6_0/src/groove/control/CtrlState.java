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
import groove.graph.GraphInfo;
import groove.graph.Node;
import groove.trans.Recipe;
import groove.trans.Rule;
import groove.view.FormatErrorSet;

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
     * Creates and adds an outgoing transition to this control state.
     * It is an error if there was
     * already a transition for the same rule.
     */
    public CtrlTransition addTransition(CtrlLabel label, CtrlState target) {
        CtrlTransition result = new CtrlTransition(this, label, target);
        this.transitions.add(result);
        CtrlTransition oldTrans =
            this.transitionMap.put(result.getRule(), result);
        if (oldTrans != null) {
            FormatErrorSet errors =
                new FormatErrorSet("Nondeterminism for '%s'", result.getCall());
            GraphInfo.addErrors(getAut(), errors);
        }
        if (result.hasRecipe()) {
            this.recipeCount++;
        }
        if (label.getCall().isOmega()) {
            getAut().addOmega(result);
        }
        if (label.isStart() && isTransient()) {
            setExitGuard(label.getGuard());
        }
        return result;
    }

    /**
     * Removes an outgoing transition from this control state.
     */
    public boolean removeOmega(CtrlTransition trans) {
        assert trans.getCall().isOmega();
        assert trans.getNumber() == this.transitions.size() - 1;
        this.transitions.remove(trans.getNumber());
        boolean result = this.transitionMap.remove(trans.getRule()) != null;
        if (result) {
            getAut().removeOmega(trans);
        }
        return result;
    }

    /** Indicates if this control state has outgoing recipe transitions. */
    public boolean hasRecipes() {
        return this.recipeCount > 0;
    }

    /** Returns the outgoing control transition for a given rule, if any. */
    public CtrlTransition getTransition(Rule rule) {
        return this.transitionMap.get(rule);
    }

    /** Returns the outgoing control transitions of this control state. */
    public Collection<CtrlTransition> getTransitions() {
        return this.transitions;
    }

    /** Indicates if this state currently only has a single unguarded outgoing omega-transition. */
    public boolean isOmegaOnly() {
        boolean result = false;
        if (getTransitions().size() == 1) {
            CtrlTransition trans = getTransitions().iterator().next();
            result = trans.getCall().isOmega() && trans.getGuard().isEmpty();
        }
        return result;
    }

    /** 
     * Returns the set of rule names of outgoing transitions, or
     * {@code null} if there is an outgoing omega transition. 
     */
    public CtrlGuard getInit() {
        CtrlGuard result = new CtrlGuard();
        for (CtrlTransition trans : getTransitions()) {
            CtrlCall call = trans.getCall();
            if (call.isOmega()) {
                result = null;
                break;
            } else {
                result.add(trans);
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
                    Collections.<CtrlTransition>emptySet(),
                    Collections.<CtrlTransition>emptySet());
            // discard the map to save space
            this.scheduleMap = null;
        }
        return this.schedule;
    }

    /** Returns the (optional) guard under which the transient status is exited. */
    public CtrlGuard getExitGuard() {
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
    public void setExitGuard(CtrlGuard exitGuard) {
        assert isTransient()
            && (this.exitGuard == null || this.exitGuard.equals(exitGuard));
        this.exitGuard = exitGuard;
    }

    /** 
     * Copies all outgoing transitions of this state to another.
     * Both the source state and the target states are mapped, as are
     * the guard transitions.
     * @param stateMap mapping from states in this automaton to states in
     * the target automaton
     * @param guard optional additional guard transitions to be added to all guards
     * in the copied transitions
     */
    public void copyTransitions(Map<CtrlState,CtrlState> stateMap,
            CtrlGuard guard) {
        CtrlState sourceImage = stateMap.get(this);
        Map<CtrlTransition,CtrlTransition> transMap =
            new HashMap<CtrlTransition,CtrlTransition>();
        // copy the transitions to avoid concurrent modification 
        // in case the source image equals this
        for (CtrlTransition key : new ArrayList<CtrlTransition>(
            this.transitions)) {
            CtrlState targetImage = stateMap.get(key.target());
            CtrlLabel newLabel = key.label().newLabel(transMap, guard);
            CtrlTransition image =
                sourceImage.addTransition(newLabel, targetImage);
            transMap.put(key, image);
        }
    }

    /** Computes a map from control calls to transitions disabled by those calls. */
    private Map<CtrlTransition,Set<CtrlTransition>> computeDisabledMap() {
        // look for the transition that appears in the fewest guards
        Map<CtrlTransition,Set<CtrlTransition>> result =
            new HashMap<CtrlTransition,Set<CtrlTransition>>();
        for (CtrlTransition trans : getTransitions()) {
            for (CtrlTransition call : trans.label().getGuard()) {
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
            Set<CtrlTransition> triedCalls, Set<CtrlTransition> failedCalls) {
        if (this.scheduleMap == null) {
            this.scheduleMap =
                new HashMap<Set<CtrlTransition>,Map<Set<CtrlTransition>,CtrlSchedule>>();
        }
        Map<Set<CtrlTransition>,CtrlSchedule> auxMap =
            this.scheduleMap.get(transSet);
        if (auxMap == null) {
            this.scheduleMap.put(transSet, auxMap =
                new HashMap<Set<CtrlTransition>,CtrlSchedule>());
        }
        CtrlSchedule result = auxMap.get(triedCalls);
        if (result == null) {
            auxMap.put(triedCalls,
                result = computeSchedule(transSet, triedCalls, failedCalls));
        }
        return result;
    }

    private CtrlSchedule computeSchedule(Set<CtrlTransition> transSet,
            Set<CtrlTransition> tried, Set<CtrlTransition> failed) {
        // look for the untried call with the least disablings
        CtrlTransition chosenTrans = null;
        Set<CtrlTransition> chosenDisablings = null;
        // boolean indicating that the omega rule guards have all been satisfied
        boolean success = false;
        for (CtrlTransition tryTrans : transSet) {
            Set<CtrlTransition> guard =
                new HashSet<CtrlTransition>(tryTrans.getGuard());
            guard.removeAll(tried);
            if (!guard.isEmpty()) {
                continue;
            }
            if (tryTrans.getCall().isOmega()) {
                success = true;
                continue;
            }
            Set<CtrlTransition> tryDisablings;
            if (this.disabledMap.containsKey(tryTrans)) {
                tryDisablings =
                    new HashSet<CtrlTransition>(this.disabledMap.get(tryTrans));
                tryDisablings.retainAll(transSet);
            } else {
                tryDisablings = Collections.emptySet();
            }
            if (chosenTrans == null
                || tryDisablings.size() < chosenDisablings.size()
                || (tryDisablings.size() == chosenDisablings.size() && smallerThan(
                    tryTrans, chosenTrans))) {
                chosenTrans = tryTrans;
                chosenDisablings = tryDisablings;
            }
        }
        boolean isTransient = isTransient();
        if (hasExitGuard()) {
            Set<CtrlTransition> guard =
                new HashSet<CtrlTransition>(getExitGuard());
            guard.removeAll(failed);
            isTransient = !guard.isEmpty();
        }
        CtrlSchedule result =
            new CtrlSchedule(this, chosenTrans, tried, success, isTransient);
        if (chosenTrans != null) {
            Set<CtrlTransition> newTried = new HashSet<CtrlTransition>(tried);
            newTried.add(chosenTrans);
            Set<CtrlTransition> newFailed = new HashSet<CtrlTransition>(failed);
            newFailed.add(chosenTrans);
            Set<CtrlTransition> remainder =
                new LinkedHashSet<CtrlTransition>(transSet);
            remainder.remove(chosenTrans);
            CtrlSchedule failNext = getSchedule(remainder, newTried, newFailed);
            CtrlSchedule succNext;
            if (chosenDisablings.isEmpty()) {
                succNext = failNext;
            } else {
                remainder = new LinkedHashSet<CtrlTransition>(remainder);
                remainder.removeAll(chosenDisablings);
                succNext = getSchedule(remainder, newTried, failed);
            }
            result.setNext(succNext, failNext);
        }
        return result;
    }

    /** Compares two control transition for the purpose of deciding which one
     * is to come first in the schedule.
     */
    private boolean smallerThan(CtrlTransition one, CtrlTransition two) {
        int result =
            one.getCall().toString().compareTo(two.getCall().toString());
        if (result == 0) {
            result = one.getGuard().compareTo(two.getGuard());
        }
        if (result == 0) {
            int myRecipe = one.hasRecipe() ? 1 : 0;
            int hisRecipe = two.hasRecipe() ? 1 : 0;
            result = myRecipe - hisRecipe;
            if (result == 0 && one.hasRecipe()) {
                result = getRecipe().compareTo(two.getRecipe());
            }
        }
        if (result == 0) {
            int myStart = one.isStart() ? 1 : 0;
            int hisStart = two.isStart() ? 1 : 0;
            result = myStart - hisStart;
        }
        if (result == 0) {
            result = one.getNumber() - two.getNumber();
        }
        return result < 0;
    }

    /** Internal number to identify the state. */
    private final int stateNumber;
    /** List of outgoing transitions. */
    private final List<CtrlTransition> transitions =
        new ArrayList<CtrlTransition>();
    /** Mapping from rules to outgoing transitions. */
    private final Map<Rule,CtrlTransition> transitionMap =
        new HashMap<Rule,CtrlTransition>();
    /** The collection of bound variables of this control state. */
    private final List<CtrlVar> boundVars = new ArrayList<CtrlVar>();
    /** Optional name of a recipe of which this is a transient state. */
    private final Recipe recipe;
    /** If the state is transient, an optional guard under which the transient status is exited. */
    private CtrlGuard exitGuard;
    /** The schedule for trying the outgoing transitions of this state. */
    private CtrlSchedule schedule;
    /** Map from calls to transitions disabled by their success. */
    private Map<CtrlTransition,Set<CtrlTransition>> disabledMap;
    /** Map storing the computed intermediate schedules, to enable sharing. */
    private Map<Set<CtrlTransition>,Map<Set<CtrlTransition>,CtrlSchedule>> scheduleMap;
    /** Count of the number of outgoing recipe transitions. */
    private int recipeCount;
}
