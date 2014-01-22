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

import static groove.graph.GraphRole.CTRL;
import groove.control.Callable;
import groove.control.CtrlVar;
import groove.control.CtrlVarSet;
import groove.control.Function;
import groove.control.Position.Type;
import groove.control.Procedure;
import groove.grammar.Action;
import groove.graph.GraphRole;
import groove.graph.Node;
import groove.graph.NodeSetEdgeSetGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
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
public class Template extends NodeSetEdgeSetGraph<Location,Switch> {
    /**
     * Constructs a automaton for a given control unit and name.
     * @param depth the transient depth of the initial state.
     */
    private Template(String name, Procedure proc, int depth) {
        super(name);
        this.maxNodeNr = -1;
        this.owner = proc;
        this.start = addLocation(depth);
    }

    /**
     * Constructs a named automaton.
     * @param depth the transient depth of the initial state.
     */
    protected Template(String name, int depth) {
        this(name, null, depth);
    }

    /**
     * Constructs a automaton for a given control unit.
     */
    protected Template(Procedure proc) {
        this(proc.getFullName(), proc, 0);
    }

    @Override
    public GraphRole getRole() {
        return CTRL;
    }

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

    /**
     * Returns the final location of this automaton.
     * The final location always exist, but may fail to be a node
     * of the template.
     * Should only be called after the automaton is fixed.
     */
    public Location getFinal() {
        assert isFixed();
        if (this.finalLoc == null) {
            this.finalLoc = computeFinal();
        }
        return this.finalLoc;
    }

    private Location finalLoc;

    /**
     * Computes the final location of this template.
     */
    private Location computeFinal() {
        Location result = null;
        for (Location loc : nodeSet()) {
            if (loc.isFinal()) {
                result = loc;
            }
        }
        if (result == null) {
            result = new Location(this, -1, 0);
            result.setType(Type.FINAL);
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
                    Template fresh = ((Function) unit).getTemplate();
                    if (seen.add(function) && fresh != null) {
                        todo.add(fresh);
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
        for (Location state : nodeSet()) {
            Set<CtrlVar> vars;
            if (state.isFinal() && hasOwner()) {
                vars = getOwner().getOutPars().keySet();
            } else {
                vars = Collections.emptySet();
            }
            state.addVars(vars);
        }
        for (Switch swit : edgeSet()) {
            swit.source().addVars(swit.getCall().getInVars().keySet());
        }
        Map<Location,Set<Switch>> inMap = getInEdgeMap();
        Queue<Switch> queue = new LinkedList<Switch>(edgeSet());
        while (!queue.isEmpty()) {
            Switch next = queue.poll();
            Location source = next.source();
            CtrlVarSet sourceVars = new CtrlVarSet(source.getVars());
            boolean modified = false;
            for (CtrlVar targetVar : next.target().getVars()) {
                if (!next.getCall().getOutVars().containsKey(targetVar)) {
                    modified |= sourceVars.add(targetVar);
                }
            }
            if (modified) {
                source.setVars(sourceVars);
                queue.addAll(inMap.get(source));
            }
        }
    }

    /** Returns a mapping from locations to sets of incoming edges. */
    public Map<Location,Set<Switch>> getInEdgeMap() {
        if (this.inEdgeMap == null) {
            this.inEdgeMap = computeInEdgeMap();
        }
        return this.inEdgeMap;
    }

    private Map<Location,Set<Switch>> inEdgeMap;

    /** Computes a mapping from locations to sets of incoming edges. */
    private Map<Location,Set<Switch>> computeInEdgeMap() {
        Map<Location,Set<Switch>> result = new HashMap<Location,Set<Switch>>();
        for (Location state : nodeSet()) {
            result.put(state, new HashSet<Switch>());
        }
        for (Switch trans : edgeSet()) {
            result.get(trans.target()).add(trans);
        }
        return result;
    }

    /**
     * Returns a deadlocked location at a certain transient depth, which
     * is not added to the nodes of the graph.
     */
    public Location getDead(int depth) {
        for (int i = this.dead.size(); i <= depth; i++) {
            Location result = new Location(this, -2 - depth, depth);
            result.setType(Type.DEAD);
            this.dead.add(result);
        }
        return this.dead.get(depth);
    }

    private final List<Location> dead = new ArrayList<Location>();

    /** Returns a copy of this automaton for a given enclosing procedure. */
    public Template clone(Procedure parent) {
        return new Template(parent);
    }

    @Override
    public boolean setFixed() {
        boolean result = super.setFixed();
        if (result) {
            for (Location loc : nodeSet()) {
                loc.setFixed();
            }
        }
        return result;
    }

    @Override
    public boolean addEdge(Switch edge) {
        boolean result = super.addEdge(edge);
        if (result) {
            edge.source().addSwitch(edge);
        }
        return result;
    }

    @Override
    public Template newGraph(String name) {
        return new Template(name, getStart().getDepth());
    }

    @Override
    public Set<? extends Switch> outEdgeSet(Node node) {
        return ((Location) node).getSwitches();
    }

    @Override
    public Set<? extends Switch> inEdgeSet(Node node) {
        return getInEdgeMap().get(node);
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
