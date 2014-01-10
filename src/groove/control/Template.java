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
public class Template extends NodeSetEdgeSetGraph<Location,CtrlEdge> {
    /**
     * Constructs a automaton for a given control unit and name.
     */
    private Template(String name, CtrlUnit unit) {
        super(name);
        this.parent = unit;
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
    protected Template(CtrlUnit unit) {
        this(unit.getFullName(), unit);
    }

    @Override
    public GraphRole getRole() {
        return CTRL;
    }

    /** 
     * Indicates if this automaton is the body of a
     * control unit.
     * @see #getParent()
     */
    public boolean hasParent() {
        return getParent() != null;
    }

    /**
     * Returns the control unit of which this automaton
     * constitutes the body, if any.
     */
    public CtrlUnit getParent() {
        return this.parent;
    }

    private final CtrlUnit parent;

    /** Returns the initial location of this automaton. */
    public Location getStart() {
        return this.start;
    }

    private final Location start;

    /**
     * Returns the final location of this automaton.
     * Should only be called after the automaton is fixed.
     */
    public Location getFinal() {
        assert isFixed();
        if (this.end == null) {
            for (Location loc : nodeSet()) {
                if (loc.isFinal()) {
                    this.end = loc;
                    break;
                }
            }
        }
        assert this.end != null;
        return this.end;
    }

    private Location end;

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
            for (CtrlEdge edge : next.edgeSet()) {
                Callable unit = edge.getUnit();
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
        return result;
    }

    /** Returns a copy of this automaton with a given parent unit. */
    public Template clone(CtrlUnit parent) {
        return new Template(parent);
    }

    @Override
    public boolean addEdge(CtrlEdge edge) {
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
    public boolean removeEdge(CtrlEdge edge) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeNode(Location node) {
        throw new UnsupportedOperationException();
    }

}
