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
import groove.control.Position;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Location in a control template.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Location implements Position<Location,SwitchStack>, Comparable<Location> {
    /**
     * Constructs a numbered location for a given automaton.
     */
    public Location(Template template, int nr, int depth) {
        this.nr = nr;
        this.template = template;
        this.depth = depth;
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

    /** Returns the number of this location within the template. */
    public int getNumber() {
        return this.nr;
    }

    /** The number of the location within the template. */
    private final int nr;

    /**
     * Sets the position type of this location.
     * Should only be called if the location is not yet fixed.
     */
    public void setType(Type type) {
        assert this.type == null;
        assert type != null;
        this.type = type;
    }

    @Override
    public Type getType() {
        return this.type;
    }

    private Type type;

    @Override
    public boolean isFinal() {
        return getType() == Type.FINAL;
    }

    @Override
    public boolean isDead() {
        return getType() == Type.DEAD;
    }

    @Override
    public boolean isTrial() {
        return getType() == Type.TRIAL;
    }

    /** 
     * Sets the attempt of this location.
     */
    public void setAttempt(SwitchAttempt attempt) {
        this.attempt = attempt;
    }

    /**
     * Returns the list of outgoing call edges of this location.
     * Should only be invoked after the location is fixed.
     */
    @Override
    public SwitchAttempt getAttempt() {
        return this.attempt;
    }

    /** The set of outgoing call edges. */
    private SwitchAttempt attempt;

    /** Indicates if this location has a non-empty set of control variables. */
    public boolean hasVars() {
        return !getVars().isEmpty();
    }

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
        this.vars = new ArrayList<CtrlVar>(variables);
    }

    /** The collection of variables of this control location. */
    private List<CtrlVar> vars;

    /** Returns a mapping from variables to their indices for this location. */
    public Map<CtrlVar,Integer> getVarIxMap() {
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
        String result = getTemplate().getName() + "." + getNumber();
        boolean brackets = false;
        if (isStart()) {
            result = result + "(start";
            brackets = true;
        }
        if (isDead()) {
            result = result + (brackets ? "," : "(");
            result = result + "dead";
            brackets = true;
        }
        if (isFinal()) {
            result = result + (brackets ? "," : "(");
            result = result + "final";
            brackets = true;
        }
        if (this.depth > 0) {
            result = result + (brackets ? "," : "(");
            result = result + "d" + this.depth;
            brackets = true;
        }

        return result + (brackets ? ")" : "");
    }

    @Override
    public int compareTo(Location o) {
        return getNumber() - o.getNumber();
    }
}
