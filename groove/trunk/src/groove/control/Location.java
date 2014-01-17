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
public class Location extends ANode implements TemplatePosition, Fixable {
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

    /** Indicates if this is the template's start locations. */
    public boolean isStart() {
        return getTemplate().getStart() == this;
    }

    @Override
    public int getDepth() {
        return this.depth;
    }

    private final int depth;

    /**
     * Sets the position type of this location.
     * Should only be called if the location is not yet fixed.
     */
    public void setType(Type type) {
        assert !isFixed();
        assert this.type == null;
        assert type != null;
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }

    private Type type;

    @Override
    public boolean isFinal() {
        return getType() == Type.FINAL;
    }

    public boolean isDead() {
        return false;
    }

    public boolean isTrial() {
        return getType() == Type.TRIAL;
    }

    /**
     * Adds an outgoing edge to this location.
     * Should only be invoked if the location is not yet fixed.
     * @param edge the edge to be added
     */
    public void addOutEdge(Switch edge) {
        assert edge.source() == this;
        assert !isFixed();
        this.outEdges.add(edge);
        if (edge.getKind() == Kind.VERDICT) {
            assert !this.isFinal();
            if (edge.isSuccess()) {
                assert this.success == null;
                this.success = edge.target();
            } else {
                assert this.failure == null;
                this.failure = edge.target();
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
    @Override
    public List<Switch> getAttempts() {
        assert isFixed();
        return this.outCalls;
    }

    /** The set of all outgoing edges. */
    private final Set<Switch> outEdges;

    /** The set of outgoing call edges. */
    private final List<Switch> outCalls;

    /** Sets the failure verdict to {@link Deadlock}. */
    void setDeadFailure() {
        assert !isFixed();
        assert this.failure == null;
        this.failure = Deadlock.instance(getDepth());
    }

    @Override
    public TemplatePosition onFailure() {
        assert isFixed();
        return this.failure;
    }

    private TemplatePosition failure;

    /** Sets the success verdict to {@link Deadlock}. */
    void setDeadSuccess() {
        assert !isFixed();
        assert this.failure == null;
        this.success = Deadlock.instance(getDepth());
    }

    @Override
    public TemplatePosition onSuccess() {
        assert isFixed();
        return this.success;
    }

    private TemplatePosition success;

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
    public String toString() {
        String result = super.toString();
        if (isStart()) {
            result = result + ", start";
        }
        if (this.type == Type.FINAL) {
            result = result + ", final";
        }
        if (this.depth > 0) {
            result = result + ", depth=" + this.depth;
        }
        return result;
    }

    @Override
    protected String getToStringPrefix() {
        return "c";
    }

    public boolean setFixed() {
        assert this.type != null;
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

    public int compareTo(TemplatePosition o) {
        return getNumber() - o.getNumber();
    }
}
