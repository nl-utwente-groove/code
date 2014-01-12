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
package groove.control;

import static groove.graph.GraphRole.CTRL;
import groove.grammar.Action;
import groove.graph.GraphRole;
import groove.graph.NodeSetEdgeSetGraph;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * Control automaton.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Template extends NodeSetEdgeSetGraph<Location,Switch> {
    /**
     * Constructs a automaton for a given control unit and name.
     */
    private Template(String name, Procedure proc) {
        super(name);
        this.parent = proc;
        this.start = addLocation(0);
    }

    /**
     * Constructs a named automaton.
     */
    protected Template(String name) {
        this(name, null);
    }

    /**
     * Constructs a automaton for a given control unit.
     */
    protected Template(Procedure proc) {
        this(proc.getFullName(), proc);
    }

    @Override
    public GraphRole getRole() {
        return CTRL;
    }

    /** 
     * Indicates if this automaton is the body of a procedure.
     * @see #getParent()
     */
    public boolean hasParent() {
        return getParent() != null;
    }

    /**
     * Returns the procedure of which this automaton
     * constitutes the body, if any.
     */
    public Procedure getParent() {
        return this.parent;
    }

    private final Procedure parent;

    /** Returns the initial location of this automaton. */
    public Location getStart() {
        return this.start;
    }

    private final Location start;

    /** Indicates if this template has a single final state without outgoing transitions. */
    public boolean hasSingleFinal() {
        return getFinal().size() == 1 && this.clearFinal;
    }

    /** 
     * Returns the single final state of this template.
     * Should only be called if {@link #hasSingleFinal()} holds.
     */
    public Location getSingleFinal() {
        assert hasSingleFinal();
        return getFinal().iterator().next();
    }

    /**
     * Returns the set of final location of this automaton.
     * Should only be called after the automaton is fixed.
     */
    public Set<Location> getFinal() {
        assert isFixed();
        if (this.finalLocs == null) {
            this.finalLocs = computeFinal();
            this.clearFinal =
                this.finalLocs.size() == 1
                    && this.finalLocs.iterator().next().getOutEdges().isEmpty();
        }
        return this.finalLocs;
    }

    private Set<Location> finalLocs;
    private boolean clearFinal;

    /**
     * Computes the set of final location of this template.
     */
    private Set<Location> computeFinal() {
        Set<Location> result = new LinkedHashSet<Location>();
        for (Location loc : nodeSet()) {
            if (loc.isFinal()) {
                result.add(loc);
            }
        }
        return result;
    }

    /** Creates and adds a control location to this automaton. */
    public Location addLocation(int depth) {
        Location result = new Location(this, this.maxNodeNr + 1, depth);
        this.maxNodeNr++;
        addNode(result);
        return result;
    }

    private int maxNodeNr;

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
            Template next = todo.poll();
            for (Switch edge : next.edgeSet()) {
                Callable unit = edge.getUnit();
                if (unit instanceof Action) {
                    result.add((Action) unit);
                } else {
                    Function function = (Function) unit;
                    Template fresh = ((Function) unit).getBody();
                    if (seen.add(function) && fresh != null) {
                        todo.add(fresh);
                    }
                }
            }
        }
        return result;
    }

    /** Returns a copy of this automaton for a given enclosing procedure. */
    public Template clone(Procedure parent) {
        return new Template(parent);
    }

    @Override
    public boolean addEdge(Switch edge) {
        boolean result = super.addEdge(edge);
        if (result) {
            edge.source().addOutEdge(edge);
        }
        return result;
    }

    @Override
    public Template newGraph(String name) {
        return new Template(name);
    }

    @Override
    public boolean addNode(Location node) {
        throw new UnsupportedOperationException("Use addState(CtrlState)");
    }

    @Override
    public boolean removeEdge(Switch edge) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeNode(Location node) {
        throw new UnsupportedOperationException();
    }

}
