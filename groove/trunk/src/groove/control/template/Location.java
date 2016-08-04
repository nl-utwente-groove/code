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
import groove.control.Position;
import groove.grammar.CheckPolicy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Location in a control template.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Location implements Position<Location,SwitchStack>, Comparable<Location>, Relocatable {
    /**
     * Constructs a numbered location for a given template.
     * @param nr the location number;
     */
    public Location(Template template, int nr, int depth) {
        this.nr = nr;
        this.template = template;
        this.depth = depth;
        if (template == null) {
            this.type = Type.DEAD;
        }
    }

    /**
     * Indicates whether this is a special location.
     * Special locations do not have an underlying template,
     * but are used to indicate errors or absence of a state.
     * @return {@code true} if and only if {@link #getTemplate()} returns {@code null}
     */
    public boolean isSpecial() {
        return getTemplate() == null;
    }

    /**
     * Returns the control template of which this is a location.
     * This is non-{@code null} except for special locations.
     * @see #isSpecial()
     */
    public Template getTemplate() {
        return this.template;
    }

    private final Template template;

    @Override
    public boolean isStart() {
        return !isSpecial() && getTemplate().getStart() == this;
    }

    @Override
    public int getTransience() {
        return this.depth;
    }

    private final int depth;

    /** Returns the number of this location within the template.
     * The number is non-negative except if this is a special location.
     */
    public int getNumber() {
        return this.nr;
    }

    /** Indicates whether this is an error location.
     * @see #isSpecial()
     */
    public boolean isError() {
        return this.nr == ERROR_NR;
    }

    /** Indicates whether this is an absence location.
     * @see #isSpecial()
     */
    public boolean isRemoved() {
        return this.nr == REMOVE_NR;
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

    @Override
    public boolean hasVars() {
        return !getVars().isEmpty();
    }

    @Override
    public List<CtrlVar> getVars() {
        if (!isDead() && this.vars == null) {
            // this may only happen before the variables have been
            // properly initialised; use the empty list as initial value.
            this.vars = new ArrayList<>(getVarSet());
            Collections.sort(this.vars);
        }
        return this.vars;
    }

    /** Returns the unordered set of control variables. */
    Set<CtrlVar> getVarSet() {
        if (!isDead() && this.varSet == null) {
            assert this.vars == null;
            this.varSet = new HashSet<>();
        }
        return this.varSet;
    }

    /**
     * Callback method from {@link Template#initVars()} to add variables to this location.
     * @return {@code true} if the variable set changed as a consequence of this call
     */
    boolean addVars(Collection<CtrlVar> variables) {
        boolean result = false;
        if (!isDead() && variables != null && !variables.isEmpty()) {
            result = getVarSet().addAll(variables);
            if (result) {
                this.vars = null;
            }
        }
        return result;
    }

    /** Unordered set of control variables, used during variable initialisation. */
    private Set<CtrlVar> varSet;
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
        Map<CtrlVar,Integer> result = new LinkedHashMap<>();
        for (int i = 0; i < getVars().size(); i++) {
            result.put(getVars().get(i), i);
        }
        return result;
    }

    private Map<CtrlVar,Integer> varIxMap;

    @Override
    public Location relocate(Relocation map) {
        Location result = map.get(this);
        result.setAttempt(getAttempt().relocate(map));
        return result;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        if (isError()) {
            result.append("error");
        } else if (isRemoved()) {
            result.append("removed");
        } else {
            result.append(getTemplate().hasOwner() ? getTemplate().getQualName() : "main");
            result.append(".");
            result.append(getNumber());
        }
        return result.toString();
    }

    @Override
    public int compareTo(Location o) {
        int result = getNumber() - o.getNumber();
        if (result != 0) {
            return result;
        }
        return getTransience() - o.getTransience();
    }

    /** Location number of an error location. */
    private static final int ERROR_NR = -1;
    /** Location number of an absence location. */
    private static final int REMOVE_NR = -2;

    /** Returns an absence location of given transient depth. */
    public static Location getSpecial(CheckPolicy policy, int transience) {
        List<Location> locations = policy == CheckPolicy.ERROR ? errorLocations : removeLocations;
        for (int i = locations.size(); i <= transience; i++) {
            locations.add(new Location(null, policy == CheckPolicy.ERROR ? ERROR_NR : REMOVE_NR, i));
        }
        return locations.get(transience);
    }

    /** Global list of error locations of given transience. */
    private static final List<Location> errorLocations = new ArrayList<>();
    /** Global list of absence locations of given transience. */
    private static final List<Location> removeLocations = new ArrayList<>();
}
