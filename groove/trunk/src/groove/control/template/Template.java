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

import groove.control.CtrlVar;
import groove.control.CtrlVarSet;
import groove.control.Function;
import groove.control.Procedure;
import groove.control.graph.ControlGraph;
import groove.grammar.Action;
import groove.grammar.Callable;
import groove.grammar.host.HostFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

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
    private Template(String name, Procedure proc) {
        this.name = name;
        this.maxNodeNr = -1;
        this.owner = proc;
        this.locations = new LinkedHashSet<Location>();
        this.start = addLocation(0);
    }

    /**
     * Constructs a named automaton.
     */
    protected Template(String name) {
        this(name, null);
    }

    /**
     * Constructs a automaton for a given procedure.
     */
    protected Template(Procedure proc) {
        this(proc.getFullName(), proc);
    }

    /** Returns the template name. */
    public String getName() {
        return this.name;
    }

    private final String name;

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
        Set<Action> result = new LinkedHashSet<Action>();
        Set<Function> seen = new HashSet<Function>();
        Queue<Template> todo = new LinkedList<Template>();
        todo.add(this);
        while (!todo.isEmpty()) {
            Template t = todo.poll();
            for (Location loc : t.getLocations()) {
                if (!loc.isTrial()) {
                    continue;
                }
                for (SwitchStack swit : loc.getAttempt()) {
                    Callable unit = swit.getBottomCall().getUnit();
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
        // compute the map of incoming transitions
        for (Location loc : getLocations()) {
            Set<CtrlVar> vars;
            if (loc.isFinal() && hasOwner()) {
                vars = getOwner().getOutPars().keySet();
            } else if (loc.isTrial()) {
                vars = new HashSet<CtrlVar>();
                for (SwitchStack s : loc.getAttempt()) {
                    vars.addAll(s.getBottomCall().getInVars().keySet());
                }
            } else {
                vars = Collections.emptySet();
            }
            loc.addVars(vars);
        }
        // propagate all variables backward to the location where they are initialised
        // mapping from locations to their predecessors
        Map<Location,Set<Location>> inMap = getPredMap();
        // queue of locations to be processed
        Set<Location> backward = new LinkedHashSet<Location>(getLocations());
        while (!backward.isEmpty()) {
            Iterator<Location> iter = backward.iterator();
            Location loc = iter.next();
            iter.remove();
            if (!loc.isTrial()) {
                continue;
            }
            SwitchAttempt attempt = loc.getAttempt();
            CtrlVarSet sourceVars = new CtrlVarSet(loc.getVars());
            boolean modified = sourceVars.addAll(attempt.onSuccess().getVars());
            modified |= sourceVars.addAll(attempt.onFailure().getVars());
            for (SwitchStack swit : attempt) {
                CtrlVarSet targetVars = new CtrlVarSet(swit.onFinish().getVars());
                targetVars.removeAll(swit.getBottomCall().getOutVars().keySet());
                modified |= sourceVars.addAll(targetVars);
            }
            if (modified) {
                loc.setVars(sourceVars);
                backward.addAll(inMap.get(loc));
            }
        }
        Set<Location> forward = new LinkedHashSet<Location>(getLocations());
        // propagate all variables forward along verdict transitions
        while (!forward.isEmpty()) {
            Iterator<Location> iter = forward.iterator();
            Location loc = iter.next();
            iter.remove();
            if (!loc.isTrial()) {
                continue;
            }
            SwitchAttempt attempt = loc.getAttempt();
            Location onFailure = attempt.onFailure();
            if (onFailure.addVars(loc.getVars())) {
                forward.add(onFailure);
            }
            Location onSuccess = attempt.onSuccess();
            if (onSuccess.addVars(loc.getVars())) {
                forward.add(onSuccess);
            }
        }
    }

    /** Returns a mapping from locations to sets of predecessors. */
    public Map<Location,Set<Location>> getPredMap() {
        if (this.predMap == null) {
            this.predMap = computePredMap();
        }
        return this.predMap;
    }

    private Map<Location,Set<Location>> predMap;

    /** Computes a mapping from locations to sets of incoming edges. */
    private Map<Location,Set<Location>> computePredMap() {
        Map<Location,Set<Location>> result = new HashMap<Location,Set<Location>>();
        for (Location loc : getLocations()) {
            result.put(loc, new HashSet<Location>());
        }
        for (Location state : getLocations()) {
            if (state.isTrial()) {
                SwitchAttempt attempt = state.getAttempt();
                result.get(attempt.onSuccess()).add(state);
                result.get(attempt.onFailure()).add(state);
                for (SwitchStack swit : state.getAttempt()) {
                    result.get(swit.onFinish()).add(state);
                }
            }
        }
        return result;
    }

    /** Returns a new, initially empty template for the same procedure or
     * main program name as this one.
     */
    public Template newInstance() {
        return hasOwner() ? new Template(getOwner()) : new Template(getName());
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
        return getName() + ": " + getLocations();
    }

    /** Returns a control graph consisting of this automaton's locations and switches.
     * @param full if {@code true}, the full control flow is generated;
     * otherwise, verdict edges are omitted (and their sources and targets mapped
     * to the same node).
     */
    public ControlGraph toGraph(boolean full) {
        return ControlGraph.newGraph(this, full);
    }
}
