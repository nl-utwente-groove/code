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
package groove.control.template;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import groove.control.Call;
import groove.control.CtrlVar;
import groove.control.Function;
import groove.control.Procedure;
import groove.control.graph.ControlGraph;
import groove.grammar.Action;
import groove.grammar.Callable;
import groove.grammar.QualName;
import groove.grammar.host.HostFactory;

/**
 * Control automaton template.
 * A template is either the main template of a control automaton,
 * or the body of a procedure (its owner).
 * Templates are graphs, with {@link Location}s as nodes and
 * {@link Switch}es as edges. The switches are used to represent both
 * call attempts and success/failure verdicts; however, since the graphs are
 * intended only for visualisation, some nodes and edges are suppressed.
 * This is especially true for verdict edges to dead locations, as well
 * as for the (unique) final state if it is unreachable.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Template {
    /**
     * Constructs a automaton for a given procedure and name.
     */
    private Template(QualName name, Procedure proc) {
        this.name = name;
        this.maxNodeNr = -1;
        this.owner = proc;
        this.locations = new LinkedHashSet<>();
        this.start = addLocation(0);
    }

    /**
     * Constructs a named automaton.
     */
    protected Template(QualName name) {
        this(name, null);
    }

    /**
     * Constructs a automaton for a given procedure.
     */
    protected Template(Procedure proc) {
        this(proc.getQualName(), proc);
    }

    /** Returns the template name. */
    public QualName getQualName() {
        return this.name;
    }

    private final QualName name;

    /**
     * Indicates if this automaton is the body of a procedure.
     * @see #getOwner()
     */
    public boolean hasOwner() {
        return getOwner() != null;
    }

    /**
     * Returns the procedure of which this automaton
     * constitutes the body, if any.
     */
    public Procedure getOwner() {
        return this.owner;
    }

    private final Procedure owner;

    /** Returns the initial location of this automaton. */
    public Location getStart() {
        return this.start;
    }

    private final Location start;

    /** Creates and adds a control location to this automaton. */
    public Location addLocation(int depth) {
        this.maxNodeNr++;
        Location result = new Location(this, this.maxNodeNr, depth);
        this.locations.add(result);
        return result;
    }

    /** Returns the size of this template, as number of locations. */
    public int size() {
        return this.maxNodeNr + 1;
    }

    private int maxNodeNr;

    /** Returns the set of locations of this template. */
    public Collection<Location> getLocations() {
        return this.locations;
    }

    private final Set<Location> locations;

    /**
     * Returns the set of actions occurring on control edges,
     * either on this level or recursively within function calls.
     */
    public Set<Action> getActions() {
        Set<Action> result = new LinkedHashSet<>();
        Set<Function> seen = new HashSet<>();
        Queue<Template> todo = new LinkedList<>();
        todo.add(this);
        while (!todo.isEmpty()) {
            Template t = todo.poll();
            for (Location loc : t.getLocations()) {
                if (!loc.isTrial()) {
                    continue;
                }
                for (SwitchStack swit : loc.getAttempt()) {
                    Callable unit = swit.getBottomCall()
                        .getUnit();
                    if (unit instanceof Action) {
                        result.add((Action) unit);
                    } else {
                        Function function = (Function) unit;
                        Template fresh = ((Function) unit).getTemplate();
                        if (seen.add(function) && fresh != null) {
                            todo.add(fresh);
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Computes and sets the variables of every state, based on the
     * input parameters of their outgoing transitions and the output parameters
     * of the enclosing procedure, if any.
     */
    void initVars() {
        // mapping from locations to be processed to the variable sets to be added
        Map<Location,Set<CtrlVar>> changeMap = new LinkedHashMap<>();
        // compute the map of incoming transitions
        for (Location loc : getLocations()) {
            if (loc.isFinal() && hasOwner()) {
                loc.addVars(getOwner().getOutPars()
                    .keySet());
            } else if (loc.isTrial()) {
                for (SwitchStack s : loc.getAttempt()) {
                    Switch bottom = s.get(0);
                    Call bottomCall = bottom.getCall();
                    loc.addVars(bottomCall.getInVars()
                        .keySet());
                    bottom.onFinish()
                        .addVars(bottomCall.getOutVars()
                            .keySet());
                }
            }
            Set<CtrlVar> varSet = loc.getVarSet();
            if (varSet != null && !varSet.isEmpty()) {
                propagate(changeMap, loc, varSet);
            }
        }
        // propagate all variables backward to the location where they are initialised
        while (!changeMap.isEmpty()) {
            Iterator<Map.Entry<Location,Set<CtrlVar>>> iter = changeMap.entrySet()
                .iterator();
            Map.Entry<Location,Set<CtrlVar>> e = iter.next();
            iter.remove();
            Location loc = e.getKey();
            if (!loc.isTrial()) {
                continue;
            }
            Set<CtrlVar> newVars = e.getValue();
            newVars.removeAll(loc.getVars());
            if (!newVars.isEmpty()) {
                loc.addVars(newVars);
                propagate(changeMap, loc, newVars);
            }
        }
        // propagate variables forward along verdict transitions
        Set<Location> forward = new LinkedHashSet<>(getLocations());
        while (!forward.isEmpty()) {
            Iterator<Location> iter = forward.iterator();
            Location loc = iter.next();
            iter.remove();
            if (!loc.isTrial()) {
                continue;
            }
            SwitchAttempt attempt = loc.getAttempt();
            Location onFailure = attempt.onFailure();
            if (onFailure.addVars(loc.getVarSet())) {
                forward.add(onFailure);
            }
            Location onSuccess = attempt.onSuccess();
            if (onSuccess.addVars(loc.getVarSet())) {
                forward.add(onSuccess);
            }
        }
    }

    /** Propagate a change, consisting of a set of new variables added to a given
     * location, backward to all predecessors.
     */
    private void propagate(Map<Location,Set<CtrlVar>> changeMap, Location loc,
        Set<CtrlVar> newVars) {
        Map<Location,Set<CtrlVar>> predRecord = getBackMap().get(loc);
        if (predRecord == null) {
            return;
        }
        for (Map.Entry<Location,Set<CtrlVar>> link : predRecord.entrySet()) {
            Location pred = link.getKey();
            Set<CtrlVar> newPredVars = changeMap.get(pred);
            if (newPredVars == null) {
                newPredVars = new HashSet<>();
                changeMap.put(pred, newPredVars);
            }
            Set<CtrlVar> outVars = link.getValue();
            if (!outVars.isEmpty()) {
                Set<CtrlVar> freshNewVars = new HashSet<>(newVars);
                freshNewVars.removeAll(outVars);
                newPredVars.addAll(freshNewVars);
            } else {
                newPredVars.addAll(newVars);
            }
        }
    }

    /** Returns the backward map of this template. */
    private BackMap getBackMap() {
        if (this.backMap == null) {
            this.backMap = computeBackMap();
        }
        return this.backMap;
    }

    private BackMap backMap;

    /** Computes the backward map of this template. */
    private BackMap computeBackMap() {
        BackMap result = new BackMap();
        for (Location loc : getLocations()) {
            if (loc.isTrial()) {
                SwitchAttempt attempt = loc.getAttempt();
                result.addBackLink(loc, attempt.onSuccess(), EMPTY_VAR_SET);
                result.addBackLink(loc, attempt.onFailure(), EMPTY_VAR_SET);
                for (SwitchStack swit : attempt) {
                    result.addBackLink(loc, swit.onFinish(), swit.getBottomCall()
                        .getOutVars()
                        .keySet());
                }
            }
        }
        return result;
    }

    /** Returns a new, initially empty template for the same procedure or
     * main program name as this one.
     */
    public Template newInstance() {
        return hasOwner() ? new Template(getOwner()) : new Template(getQualName());
    }

    /** Computes and inserts the host nodes to be used for constant value arguments. */
    public void initialise(HostFactory factory) {
        for (Location loc : getLocations()) {
            if (loc.isTrial()) {
                for (SwitchStack sw : loc.getAttempt()) {
                    sw.initialise(factory);
                }
            }
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.name.hashCode();
        result = prime * result + ((this.owner == null) ? 0 : this.owner.hashCode());
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
        Template other = (Template) obj;
        if (!this.name.equals(other.name)) {
            return false;
        }
        if (this.owner == null) {
            if (other.owner != null) {
                return false;
            }
        } else if (!this.owner.equals(other.owner)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getQualName() + ": " + getLocations();
    }

    /** Returns a control graph consisting of this automaton's locations and switches.
     * @param full if {@code true}, the full control flow is generated;
     * otherwise, verdict edges are omitted (and their sources and targets mapped
     * to the same node).
     */
    public ControlGraph toGraph(boolean full) {
        return ControlGraph.newGraph(this, full);
    }

    /** Constant, unmodifiable empty set of control variables. */
    private static final Set<CtrlVar> EMPTY_VAR_SET = Collections.emptySet();

    /** Mapping from precedessors (of a given target location) to
     * the sets of output values assigned along all links from that predecessor
     * to the given target location.
     */
    private static class BackRecord extends HashMap<Location,Set<CtrlVar>> {
        // empty
    }

    /** Mapping from locations to their backward records.
     */
    private static class BackMap extends HashMap<Location,BackRecord> {
        /**
         * Adds a given link (from a source to a target location,
         * initialising a given set of variables)
         * to the backward map under construction.
         */
        private void addBackLink(Location source, Location target, Set<CtrlVar> outVars) {
            BackRecord targetRecord = get(target);
            if (targetRecord == null) {
                targetRecord = new BackRecord();
                put(target, targetRecord);
            }
            assert targetRecord != null;
            Set<CtrlVar> linkVars = targetRecord.get(source);
            if (linkVars == null) {
                targetRecord.put(source, new HashSet<>(outVars));
            } else {
                linkVars.retainAll(outVars);
            }
        }
    }
}
