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

import groove.control.Switch.Kind;
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
 * Location in a control template.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Location extends ANode implements Fixable, Comparable<Location> {
    /**
     * Constructs a numbered location for a given automaton.
     */
    public Location(Template template, int nr, int depth) {
        super(nr);
        this.template = template;
        this.depth = depth;
        this.outEdges = new LinkedHashSet<Switch>();
        this.outCalls = new ArrayList<Switch>();
    }

    /**
     * Returns the control template of which this is a location.
     */
    public Template getTemplate() {
        return this.template;
    }

    private final Template template;

    /** Returns the atomicity depth of this location. */
    public int getDepth() {
        return this.depth;
    }

    private final int depth;

    /**
     * Sets this state to final.
     * Should only be called if the location is not yet fixed.
     */
    public void setFinal() {
        assert !isFixed();
        assert this.successNext == null && this.failureNext == null;
        this.isFinal = true;
    }

    /**
     * Indicates if this is a final location of the template.
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
    public void addOutEdge(Switch edge) {
        assert edge.source() == this;
        assert !isFixed();
        this.outEdges.add(edge);
        if (edge.getKind() == Kind.CHOICE) {
            assert !this.isFinal;
            if (edge.isSuccess()) {
                assert this.successNext == null;
                this.successNext = edge.target();
            } else {
                assert this.failureNext == null;
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
    public Set<Switch> getOutEdges() {
        assert isFixed();
        return this.outEdges;
    }

    /**
     * Returns the list of outgoing call edges of this location.
     * Should only be invoked after the location is fixed.
     */
    public List<Switch> getOutCalls() {
        assert isFixed();
        return this.outCalls;
    }

    /** The set of all outgoing edges. */
    private final Set<Switch> outEdges;

    /** The set of outgoing call edges. */
    private final List<Switch> outCalls;

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
     * Returns the list of control variables in this location,
     * ordered alphabetically according to their names.
     */
    public List<CtrlVar> getVars() {
        if (this.vars == null) {
            getTemplate().initVars();
        }
        return this.vars;
    }

    /**
     * Callback method from {@link Template#initVars()} to add variables to this location.
     */
    void addVars(Collection<CtrlVar> variables) {
        assert isFixed();
        CtrlVarSet newVars = new CtrlVarSet(variables);
        if (this.vars != null) {
            newVars.addAll(this.vars);
        }
        this.vars = new ArrayList<CtrlVar>(newVars);
    }

    /**
     * Callback method from {@link Template#initVars()} to set variables for this location.
     */
    void setVars(Collection<CtrlVar> variables) {
        assert isFixed();
        this.vars = new ArrayList<CtrlVar>(variables);
    }

    /** The collection of variables of this control location. */
    private List<CtrlVar> vars;

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

    public int compareTo(Location o) {
        return getNumber() - o.getNumber();
    }
}
