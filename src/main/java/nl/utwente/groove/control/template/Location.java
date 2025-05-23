/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.control.template;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.control.Assignment;
import nl.utwente.groove.control.Binding;
import nl.utwente.groove.control.Binding.Source;
import nl.utwente.groove.control.CtrlVar;
import nl.utwente.groove.control.Position;
import nl.utwente.groove.control.Procedure;
import nl.utwente.groove.grammar.CheckPolicy;
import nl.utwente.groove.util.Factory;

/**
 * Location in a control template.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class Location
    implements Position<Location,NestedSwitch>, Comparable<Location>, Relocatable {
    /**
     * Constructs a numbered location for a given template, or a dead location.
     * @param template the template for which this is a location, or {@code null} if this is the universal dead location
     * @param nr the location number
     * @param transience the transient depth
     */
    public Location(@Nullable Template template, int nr, int transience) {
        this.nr = nr;
        this.template = template == null
            ? Optional.empty()
            : Optional.of(template);
        this.transience = transience;
        if (template == null) {
            this.type = Type.DEAD;
        }
    }

    /**
     * Indicates whether this is a special location.
     * Special locations do not have an underlying template,
     * but are used to indicate errors or absence of a state.
     * @return {@code true} if and only if {@link #getTemplate()} is empty
     */
    public boolean isSpecial() {
        return getTemplate().isEmpty();
    }

    /**
     * Returns the control template of which this is a location.
     * This is non-{@code null} except for special locations.
     * @see #isSpecial()
     */
    public Optional<Template> getTemplate() {
        return this.template;
    }

    private final Optional<Template> template;

    @Override
    public boolean isStart() {
        return getTemplate().filter(t -> t.getStart() == this).isPresent();
    }

    @Override
    public int getTransience() {
        return this.transience;
    }

    private final int transience;

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
        var result = this.type;
        assert result != null;
        return result;
    }

    private @Nullable Type type;

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
        var result = this.attempt;
        assert result != null;
        return result;
    }

    /** The set of outgoing call edges. */
    private @Nullable SwitchAttempt attempt;

    @Override
    public boolean hasVars() {
        return !getVars().isEmpty();
    }

    @Override
    public List<CtrlVar> getVars() {
        var result = this.vars;
        if (result == null) {
            // this may only happen before the variables have been
            // properly initialised; use the empty list as initial value.
            this.vars = result = new ArrayList<>(getVarSet());
            Collections.sort(result);
        }
        return result;
    }

    /** Returns the unordered set of control variables. */
    Set<CtrlVar> getVarSet() {
        var result = this.varSet;
        if (result == null) {
            assert this.vars == null;
            this.varSet = result = new HashSet<>();
        }
        return result;
    }

    /**
     * Callback method from {@link Template#initVars()} to add variables to this location.
     * @return {@code true} if the variable set changed as a consequence of this call
     */
    boolean addVars(Collection<CtrlVar> variables) {
        boolean result = false;
        // don't call isDead() as the type may not yet have been set
        if (this.type != Type.DEAD && !variables.isEmpty()) {
            result = getVarSet().addAll(variables);
            if (result) {
                this.vars = null;
                this.varIxMap.reset();
            }
        }
        return result;
    }

    /** Unordered set of control variables, used during variable initialisation. */
    private @Nullable Set<CtrlVar> varSet;
    /** The collection of variables of this control location. */
    private @Nullable List<CtrlVar> vars;

    /** Returns a mapping from variables to their indices for this location. */
    public Map<CtrlVar,Integer> getVarIxMap() {
        return this.varIxMap.get();
    }

    private Map<CtrlVar,Integer> computeVarIxMap() {
        Map<CtrlVar,Integer> result = new LinkedHashMap<>();
        for (int i = 0; i < getVars().size(); i++) {
            result.put(getVars().get(i), i);
        }
        assert result.size() == getVars().size();
        return result;
    }

    private Factory<Map<CtrlVar,Integer>> varIxMap = Factory.lazy(this::computeVarIxMap);

    /** Returns an assignment from the in-parameters of the owning procedure
     * to the variables of this location.
     * Only valid if this location's template is owned by a procedure, and
     * this location is the start location.
     */
    public Assignment assignPar2Init() {
        return this.assignPar2Init.get();
    }

    /** Lazy factory for {@link #assignPar2Init()}. */
    private final Factory<Assignment> assignPar2Init = Factory.lazy(this::computeAssignPar2Init);

    /** Computes the value of {@link #assignPar2Init}. */
    private Assignment computeAssignPar2Init() {
        Procedure owner = getTemplate().get().getOwner();
        assert owner != null && isStart();
        List<Binding> bindings = new ArrayList<>();
        for (var var : getVars()) {
            var parIx = owner.getInPars().get(var);
            bindings.add(Binding.var(var, parIx));
        }
        return new Assignment(bindings);
    }

    /** Returns an assignment from the variables of this location
     * to the (out-)parameters of the owning procedure.
     * Only valid if this location's template is owned by a procedure.
     * The resulting assignment is {@link Source#NONE} for input parameters.
     */
    public Assignment assignFinal2Par() {
        return this.assignFinal2Par.get();
    }

    /** Lazy factory for {@link #assignFinal2Par()}. */
    private final Factory<Assignment> assignFinal2Par = Factory.lazy(this::computeAssignFinal2Par);

    /** Computes the value of {@link #assignFinal2Par}. */
    private Assignment computeAssignFinal2Par() {
        Procedure owner = getTemplate().get().getOwner();
        assert owner != null;
        var signature = owner.getSignature();
        var bindings = new ArrayList<Binding>();
        for (var par : signature.getPars()) {
            Binding bind;
            if (par.isOutOnly()) {
                var varIx = getVarIxMap().get(par.getVar());
                bind = Binding.var(par, varIx);
            } else {
                bind = Binding.none(par);
            }
            bindings.add(bind);
        }
        return new Assignment(bindings);
    }

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
            var template = getTemplate().get();
            result
                .append(template.hasOwner()
                    ? template.getQualName()
                    : "main");
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
        List<Location> locations = policy == CheckPolicy.ERROR
            ? errorLocations
            : removeLocations;
        for (int i = locations.size(); i <= transience; i++) {
            locations
                .add(new Location(null, policy == CheckPolicy.ERROR
                    ? ERROR_NR
                    : REMOVE_NR, i));
        }
        return locations.get(transience);
    }

    /** Global list of error locations of given transience. */
    private static final List<Location> errorLocations = new ArrayList<>();
    /** Global list of absence locations of given transience. */
    private static final List<Location> removeLocations = new ArrayList<>();
}
