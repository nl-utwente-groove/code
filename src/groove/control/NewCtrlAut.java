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
public class NewCtrlAut extends NodeSetEdgeSetGraph<CtrlLocation,CtrlEdge> {
    /**
     * Constructs a automaton for a given control unit and name.
     */
    private NewCtrlAut(String name, CtrlUnit unit) {
        super(name);
        this.parent = unit;
        this.start = addNode(0, false);
        this.end = addNode(0, true);
    }

    /**
     * Constructs a named automaton.
     */
    protected NewCtrlAut(String name) {
        this(name, null);
    }

    /**
     * Constructs a automaton for a given control unit.
     */
    protected NewCtrlAut(CtrlUnit unit) {
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
    public CtrlLocation getStart() {
        return this.start;
    }

    private final CtrlLocation start;

    /** Returns the final location of this automaton. */
    public CtrlLocation getEnd() {
        return this.end;
    }

    private final CtrlLocation end;

    /** Creates and adds a control location to this automaton. */
    public CtrlLocation addNode(int depth, boolean isFinal) {
        CtrlLocation result =
            new CtrlLocation(this, this.maxNodeNr + 1, depth, isFinal);
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
        Queue<NewCtrlAut> todo = new LinkedList<NewCtrlAut>();
        todo.add(this);
        while (!todo.isEmpty()) {
            NewCtrlAut next = todo.poll();
            for (CtrlEdge edge : next.edgeSet()) {
                Callable unit = edge.getUnit();
                if (unit instanceof Action) {
                    result.add((Action) unit);
                } else {
                    Function function = (Function) unit;
                    NewCtrlAut fresh = ((Function) unit).getUnitBody();
                    if (seen.add(function) && fresh != null) {
                        todo.add(fresh);
                    }
                }
            }
        }
        return result;
    }

    /** Returns a copy of this automaton with a given parent unit. */
    public NewCtrlAut clone(CtrlUnit parent) {
        return new NewCtrlAut(parent);
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
    public NewCtrlAut newGraph(String name) {
        return new NewCtrlAut(name);
    }

    @Override
    public boolean addNode(CtrlLocation node) {
        throw new UnsupportedOperationException("Use addState(CtrlState)");
    }

    @Override
    public boolean removeEdge(CtrlEdge edge) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeNode(CtrlLocation node) {
        throw new UnsupportedOperationException();
    }

}
