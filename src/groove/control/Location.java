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

import groove.control.CtrlEdge.Kind;
import groove.graph.ANode;
import groove.util.Fixable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Control state automaton.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Location extends ANode implements Fixable {
    /**
     * Constructs a numbered location for a given automaton.
     */
    public Location(Template aut, int nr, int depth) {
        super(nr);
        this.aut = aut;
        this.depth = depth;
        this.outEdges = new LinkedHashSet<CtrlEdge>();
        this.outCalls = new ArrayList<CtrlEdge>();
    }

    /**
     * Returns the control automaton of which this is a state.
     */
    public Template getAut() {
        return this.aut;
    }

    private final Template aut;

    /** Returns the atomicity depth of this location. */
    public int getDepth() {
        return this.depth;
    }

    private final int depth;

    /** Sets this state to final. */
    public void setFinal() {
        assert !isFixed();
        assert !isFinal();
        this.isFinal = true;
    }

    /**
     * Indicates if this is the final state of the automaton.
     */
    public boolean isFinal() {
        return this.isFinal;
    }

    private boolean isFinal;

    /**
     * Adds an outgoing edge to this location.
     * Should only be invoked if the location is not yet fixed.
     * @param edge the edge to be added
     */
    public void addOutEdge(CtrlEdge edge) {
        assert edge.source() == this;
        assert !isFixed();
        assert !isFinal();
        this.outEdges.add(edge);
        if (edge.getKind() == Kind.CHOICE) {
            if (edge.isSuccess()) {
                this.successNext = edge.target();
            } else {
                this.failureNext = edge.target();
            }
        } else {
            this.outCalls.add(edge);
        }
    }

    /**
     * Returns the list of all outgoing edges of this location.
     * Should only be invoked after the location is fixed.
     */
    public Set<CtrlEdge> getOutEdges() {
        assert isFixed();
        return this.outEdges;
    }

    /**
     * Returns the list of outgoing call edges of this location.
     * Should only be invoked after the location is fixed.
     */
    public List<CtrlEdge> getOutCalls() {
        assert isFixed();
        return this.outCalls;
    }

    /** The set of all outgoing edges. */
    private final Set<CtrlEdge> outEdges;

    /** The set of outgoing call edges. */
    private final List<CtrlEdge> outCalls;

    /** 
     * Indicates if there is a next state after failure.
     * Should only be called after the state is fixed.
     * @return {@code true} if {@link #getFailureNext()} is non-{@code null}
     */
    public boolean hasFailureNext() {
        return getFailureNext() != null;
    }

    /**
     * Returns the next state to be tried after all transitions have failed.
     * Should only be called after the state is fixed.
     * @return the next state after success; may be {@code null}
     */
    public Location getFailureNext() {
        assert isFixed();
        return this.failureNext;
    }

    private Location failureNext;

    /** 
     * Indicates if there is a next state after success.
     * Should only be called after the state is fixed.
     * @return {@code true} if {@link #getSuccessNext()} is non-{@code null}
     */
    public boolean hasSuccessNext() {
        return getSuccessNext() != null;
    }

    /**
     * Returns the next state to be tried after at least one transition
     * has succeeded.
     * Should only be called after the state is fixed.
     * @return the next state after success; may be {@code null}
     */
    public Location getSuccessNext() {
        assert isFixed();
        return this.successNext;
    }

    private Location successNext;

    /**
     * Returns the list of control variables in this state,
     * ordered according to the natural ordering of the control variables.
     */
    public List<CtrlVar> getVars() {
        assert isFixed();
        return this.vars;
    }

    /**
     * Adds control variables to this state.
     */
    public void addars(Collection<CtrlVar> variables) {
        assert !isFixed();
        CtrlVarSet newVars = new CtrlVarSet(variables);
        newVars.addAll(this.vars);
        this.vars.clear();
        this.vars.addAll(newVars);
    }

    /**
     * Sets the control variables of this state to the elements of a given collection.
     */
    public void setVars(Collection<CtrlVar> variables) {
        assert !isFixed();
        this.vars.clear();
        this.vars.addAll(new CtrlVarSet(variables));
    }

    /** The collection of variables of this control location. */
    private final List<CtrlVar> vars = new ArrayList<CtrlVar>();

    /** Returns a mapping from variables to their indices for this location. */
    public Map<CtrlVar,Integer> getVarIxMap() {
        assert isFixed();
        if (this.varIxMap == null) {
            this.varIxMap = computeVarIxMap();
        }
        return this.varIxMap;
    }

    private Map<CtrlVar,Integer> computeVarIxMap() {
        Map<CtrlVar,Integer> result = new LinkedHashMap<CtrlVar,Integer>();
        for (int i = 0; i < getVars().size(); i++) {
            result.put(getVars().get(i), i);
        }
        return result;
    }

    private Map<CtrlVar,Integer> varIxMap;

    @Override
    protected String getToStringPrefix() {
        return "c";
    }

    public boolean setFixed() {
        boolean result = !this.fixed;
        this.fixed = true;
        return result;
    }

    public boolean isFixed() {
        return this.fixed;
    }

    public void testFixed(boolean fixed) {
        if (fixed != isFixed()) {
            throw new IllegalStateException(String.format(
                "Control state should %sbe fixed", fixed ? "" : "not "));
        }
    }

    private boolean fixed;
}
