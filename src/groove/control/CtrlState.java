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
     * Creates a control state with a fresh number.
     */
    public CtrlState() {
        this(Counter.inc());
    }

    /**
     * Creates a control state with a given number.
     */
    public CtrlState(int nr) {
        this.stateNumber = nr;
    }

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
        if (obj instanceof Node) {
            return getNumber() - ((Node) obj).getNumber();
        } else {
            return getNumber() - ((Edge) obj).source().getNumber();
        }
    }

    public int getNumber() {
        return this.stateNumber;
    }

    /** Internal number to identify the state. */
    private final int stateNumber;

    @Override
    public String toString() {
        return "q" + this.stateNumber;
    }

    /**
     * Add an outgoing transition to this control state.
     */
    public boolean addTransition(CtrlTransition transition) {
        return this.outTransitions.add(transition);
    }

    /**
     * Removes an outgoing transition from this control state.
     */
    public boolean removeTransition(CtrlTransition transition) {
        return this.outTransitions.remove(transition);
    }

    /** Returns the outgoing control transitions of this control state. */
    public Set<CtrlTransition> getTransitions() {
        return this.outTransitions;
    }

    /** 
     * Returns the set of rule names of outgoing transitions, or
     * {@code null} if there is an outgoing omega transition. 
     */
    public Set<CtrlCall> getInit() {
        Set<CtrlCall> result = new HashSet<CtrlCall>();
        for (CtrlTransition trans : getTransitions()) {
            CtrlCall call = trans.label().getCall();
            if (call.isOmega()) {
                result = null;
                break;
            } else {
                result.add(call);
            }
        }
        return result;
    }

    /** Set of outgoing transitions. */
    private final Set<CtrlTransition> outTransitions =
        new HashSet<CtrlTransition>();

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
        this.boundVars.addAll(variables);
        Collections.sort(this.boundVars);
    }

    /**
     * Sets the bound variables of this state to the elements of a given collection.
     */
    public void setBoundVars(Collection<CtrlVar> variables) {
        this.boundVars.clear();
        this.boundVars.addAll(new TreeSet<CtrlVar>(variables));
    }

    /** The collection of bound variables of this control state. */
    private List<CtrlVar> boundVars = new ArrayList<CtrlVar>();

    /** Lazily creates and returns the schedule for trying the outgoing transitions of this state. */
    public CtrlSchedule getSchedule() {
        if (this.schedule == null) {
            assert this.disabledMap == null;
            this.disabledMap = computeDisabledMap();
            this.schedule =
                computeSchedule(new TreeSet<CtrlTransition>(getTransitions()),
                    Collections.<CtrlCall>emptySet());
        }
        return this.schedule;
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

    private CtrlSchedule computeSchedule(Set<CtrlTransition> transSet,
            Set<CtrlCall> triedCalls) {
        // look for the untried call with the least disablings
        CtrlTransition trans = null;
        Set<CtrlTransition> disablings = null;
        for (CtrlTransition tryTrans : transSet) {
            Set<CtrlCall> guard =
                new HashSet<CtrlCall>(tryTrans.label().getGuard());
            guard.removeAll(triedCalls);
            if (!guard.isEmpty()) {
                continue;
            }
            CtrlCall tryCall = tryTrans.label().getCall();
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
        CtrlSchedule result = new CtrlSchedule(trans, triedCalls);
        if (trans != null) {
            Set<CtrlCall> newTriedCalls = new HashSet<CtrlCall>(triedCalls);
            newTriedCalls.add(trans.label().getCall());
            Set<CtrlTransition> remainder =
                new LinkedHashSet<CtrlTransition>(transSet);
            remainder.remove(trans);
            CtrlSchedule failure = computeSchedule(remainder, newTriedCalls);
            CtrlSchedule success;
            if (disablings.isEmpty()) {
                success = failure;
            } else {
                remainder = new LinkedHashSet<CtrlTransition>(remainder);
                remainder.removeAll(disablings);
                success = computeSchedule(remainder, newTriedCalls);
            }
            result.setNext(success, failure);
        }
        return result;
    }

    /** The schedule for trying the outgoing transitions of this state. */
    private CtrlSchedule schedule;
    /** Map from calls to transitions disabled by their success. */
    private Map<CtrlCall,Set<CtrlTransition>> disabledMap;
}
