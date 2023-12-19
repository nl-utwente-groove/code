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

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import nl.utwente.groove.control.Assignment;
import nl.utwente.groove.control.Binding;
import nl.utwente.groove.control.Binding.Source;
import nl.utwente.groove.control.Call;
import nl.utwente.groove.control.CtrlPar;
import nl.utwente.groove.control.CtrlVar;
import nl.utwente.groove.control.Procedure;
import nl.utwente.groove.grammar.Callable;
import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.grammar.Signature;
import nl.utwente.groove.grammar.UnitPar;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.LazyFactory;

/**
 * Transition between control locations, bearing a call.
 * @author Arend Rensink
 * @version $Revision$
 */
public class Switch implements Comparable<Switch>, Relocatable {
    /**
     * Constructs a new switch.
     * @param source source location of the switch; non-{@code null}
     * @param call call to be used as label
     * @param transience the additional transient depth entered by this switch
     * @param onFinish target location of the switch
     */
    public Switch(Location source, Call call, int transience, Location onFinish) {
        assert onFinish != null;
        this.source = source;
        this.onFinish = onFinish;
        this.kind = call.getUnit().getKind();
        this.call = call;
        this.transience = transience;
    }

    /** Returns the source location of this switch. */
    public Location getSource() {
        return this.source;
    }

    private final Location source;

    /** Returns the target position of this switch. */
    public Location onFinish() {
        return this.onFinish;
    }

    private final Location onFinish;

    /**
     * Returns the kind of switch.
     */
    public Callable.Kind getKind() {
        return this.kind;
    }

    private final Callable.Kind kind;

    /**
     * Convenience method to return the name of the unit called in
     * this switch.
     */
    public QualName getQualName() {
        return getUnit().getQualName();
    }

    /**
     * Convenience method to return the arguments of the call of this switch.
     * @return the list of arguments
     */
    public final List<? extends CtrlPar> getArgs() {
        return getCall().getArgs();
    }

    /**
     * Convenience method to return the called unit of this switch.
     */
    public final Callable getUnit() {
        return getCall().getUnit();
    }

    /**
     * Returns the call wrapped in this switch.
     */
    public final Call getCall() {
        return this.call;
    }

    /**
     * The call of this switch.
     */
    private final Call call;

    /** Returns the additional transient depth effected by this switch. */
    public int getTransience() {
        return this.transience;
    }

    private final int transience;

    /** Returns the assignment to source variables of the initial location of
     * the callee template, based on source variables of this switch and
     * constant arguments of the call.
     * This is only valid if the callee is a procedure.
     */
    public Assignment getCalleeAssign() {
        Procedure callee = (Procedure) getUnit();
        Template template = callee.getTemplate();
        return template.getSourceAssign().then(getParAssign());
    }

    /**
     * Returns an assignment to the target variables of this
     * switch, based on the source variables and the output parameters of the call.
     * If the switch is a rule call, the output parameter values
     * are available at the moment of applying the assignment and
     * can be retrieved from the rule application; if it is a procedure
     * call, the output parameter values are not yet available and will
     * be set to {@code null}.
     */
    public Assignment getTargetAssign() {
        Assignment result = new Assignment();
        var sourceVars = getSource().getVarIxMap();
        Map<CtrlVar,Integer> outVars = getCall().getOutVars();
        for (CtrlVar var : onFinish().getVars()) {
            Integer ix = outVars.get(var);
            Binding rhs;
            if (ix == null) {
                // the value comes from the source variables
                int pos = sourceVars.get(var);
                assert pos >= 0;
                rhs = Binding.var(var, pos);
            } else if (getUnit() instanceof Rule rule) {
                // the value is an output parameter of the rule
                rhs = Binding.bind(var, rule.getParBinding(ix));
                assert rhs != null;
            } else {
                rhs = Binding.none(var);
            }
            result.add(rhs);
        }
        return result;
    }

    /**
     * Returns an assignment to the formal parameters of the call, based on
     * bindings to source location variables and constant values derived
     * from the arguments.
     * The binding is {@link Source#NONE} for output parameters and wildcard arguments
     */
    public Assignment getParAssign() {
        return this.parAssign.get();
    }

    /** Lazily computed assignment to the formal parameters of the call, based on
     * bindings to source location variables and constant values derived
     * from the arguments.
     * The binding is {@link Source#NONE} for output parameters and wildcard arguments
     */
    private Supplier<Assignment> parAssign = LazyFactory.instance(this::computeParAssign);

    /**
     * Computes the value for {@link #parAssign}.
     */
    private Assignment computeParAssign() {
        Assignment result = new Assignment();
        List<? extends CtrlPar> args = getArgs();
        Signature<? extends UnitPar> sig = getUnit().getSignature();
        int size = args.size();
        var sourceVars = getSource().getVarIxMap();
        for (int i = 0; i < size; i++) {
            var target = sig.getPar(i);
            assert args != null; // size is at least one
            CtrlPar arg = args.get(i);
            Binding bind;
            if (arg instanceof CtrlPar.Var v) {
                if (arg.inOnly()) {
                    int ix = sourceVars.get(v.var());
                    assert ix >= 0;
                    bind = Binding.var(target, ix);
                } else if (arg.outOnly()) {
                    bind = Binding.none(target);
                } else {
                    throw Exceptions
                        .illegalState("Call argument %s of %s is neither input-only nor output-only",
                                      arg, this);
                }
            } else if (arg instanceof CtrlPar.Const c) {
                bind = Binding.value(target, c);
            } else {
                assert arg instanceof CtrlPar.Wild;
                bind = Binding.none(target);
            }
            result.add(bind);
        }
        return result;
    }

    @Override
    synchronized public Switch relocate(Relocation map) {
        Switch result = this.image;
        if (map != this.map) {
            this.map = map;
            this.image = result = computeRelocated(map);
        }
        return result;
    }

    /** Computes the relocated switch under a given map. */
    private Switch computeRelocated(Relocation map) {
        Location newSource = map.get(getSource());
        Location newFinish = map.get(onFinish());
        return new Switch(newSource, getCall(), getTransience(), newFinish);
    }

    /** The map under which {@link #image} has been computed */
    private Relocation map;
    /** The relocated image under {@link #map}, if any. */
    private Switch image;

    @Override
    public int hashCode() {
        return hashCode(true);
    }

    /** Computes the hash code of this switch, optionally taking the nested switch into account. */
    public int hashCode(boolean full) {
        final int prime = 31;
        int result = 1;
        result = prime * result + getSource().hashCode();
        result = prime * result + onFinish().hashCode();
        result = prime * result + getKind().hashCode();
        result = prime * result + getTransience();
        result = prime * result + ((this.call == null)
            ? 0
            : this.call.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Switch other)) {
            return false;
        }
        if (getKind() != other.getKind()) {
            return false;
        }
        if (getTransience() != other.getTransience()) {
            return false;
        }
        if (!getSource().equals(other.getSource())) {
            return false;
        }
        if (!onFinish().equals(other.onFinish())) {
            return false;
        }
        if (!getCall().equals(other.getCall())) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Switch o) {
        int result = onFinish().getNumber() - o.onFinish().getNumber();
        if (result != 0) {
            return result;
        }
        result = getTransience() - o.getTransience();
        if (result != 0) {
            return result;
        }
        result = getKind().ordinal() - o.getKind().ordinal();
        if (result != 0) {
            return result;
        }
        result = getCall().compareTo(o.getCall());
        if (result != 0) {
            return result;
        }
        result = getSource().getNumber() - o.getSource().getNumber();
        return result;
    }

    @Override
    public String toString() {
        return getSource() + "--" + getCall() + "->" + onFinish();
    }
}
