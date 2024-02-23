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

import static nl.utwente.groove.util.Factory.lazy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import nl.utwente.groove.algebra.syntax.Variable;
import nl.utwente.groove.control.Assignment;
import nl.utwente.groove.control.Binding;
import nl.utwente.groove.control.Binding.Source;
import nl.utwente.groove.control.Call;
import nl.utwente.groove.control.CtrlArg;
import nl.utwente.groove.control.CtrlVar;
import nl.utwente.groove.control.Procedure;
import nl.utwente.groove.grammar.Callable;
import nl.utwente.groove.grammar.Callable.Kind;
import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.grammar.Signature;
import nl.utwente.groove.grammar.UnitPar;
import nl.utwente.groove.util.Exceptions;

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
        this.call = call.bind(getSourceBindMap());
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

    /** Returns the (optional) template of which this switch is an element. */
    public Optional<Template> getTemplate() {
        return getSource().getTemplate();
    }

    /** Indicates if this switch has a call of a given kind. */
    public boolean hasKind(Kind kind) {
        return getKind() == kind;
    }

    /**
     * Returns the kind of call of this switch.
     */
    public Kind getKind() {
        return this.kind;
    }

    /** The kind of call of this switch. */
    private final Kind kind;

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
    public final List<? extends CtrlArg> getArgs() {
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

    /** Returns a function mapping all sorted source variables to corresponding bindings.
     */
    private Function<Variable,Object> getSourceBindMap() {
        Map<Variable,Binding> result = new HashMap<>();
        for (var e : getSource().getVarIxMap().entrySet()) {
            var sourceVar = e.getKey();
            if (sourceVar.type().isSort()) {
                result.put(sourceVar.toVar(), Binding.var(sourceVar.name(), e.getValue()));
            }
        }
        return v -> result.get(v);
    }

    /**
     * Returns an assignment to the formal parameters of the call, based on
     * bindings to source location variables and constant values derived
     * from the arguments.
     * The binding is {@link Source#NONE} for output parameters and wildcard arguments
     */
    public Assignment assignSource2Par() {
        return this.assignSource2Par.get();
    }

    /** Lazily computed assignment to the formal parameters of the call, based on
     * bindings to source location variables and constant values derived
     * from the arguments.
     * The binding is {@link Source#NONE} for output parameters and wildcard arguments
     */
    private Supplier<Assignment> assignSource2Par = lazy(this::computeAssignSource2Par);

    /**
     * Computes the value for {@link #assignSource2Par}.
     */
    private Assignment computeAssignSource2Par() {
        Assignment result = new Assignment();
        List<? extends CtrlArg> args = getArgs();
        Signature<? extends UnitPar> sig = getUnit().getSignature();
        int size = args.size();
        var sourceVars = getSource().getVarIxMap();
        for (int i = 0; i < size; i++) {
            var target = sig.getPar(i);
            assert args != null; // size is at least one
            CtrlArg arg = args.get(i);
            Binding bind;
            if (arg instanceof CtrlArg.Var v) {
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
            } else if (arg instanceof CtrlArg.Expr e) {
                bind = Binding.expr(target, e.expr());
            } else {
                assert arg instanceof CtrlArg.Wild;
                bind = Binding.none(target);
            }
            result.add(bind);
        }
        return result;
    }

    /**
     * Returns an assignment to the target variables of this
     * switch, based on the source variables complemented with
     * the output parameters of the call, insofar available.
     * If the switch is a rule call, the output parameter values
     * are available at the moment of applying the assignment and
     * can be retrieved from the rule application; if it is a procedure
     * call, the output parameter values are not yet available and will
     * be set to {@code null}.
     */
    public Assignment assignSource2Target() {
        return this.assignSource2Target.get();
    }

    /**
     * Lazily computed assignment to the target variables of this
     * switch, based on the source variables complemented with
     * the output parameters of the call, insofar available.
     * If the switch is a rule call, the output parameter values
     * are available at the moment of applying the assignment and
     * can be retrieved from the rule application; if it is a procedure
     * call, the output parameter values are not yet available and will
     * be set to {@code null}.
     */
    private final Supplier<Assignment> assignSource2Target = lazy(this::computeAssignSource2Target);

    /**
     * Computes the value for {@link #assignSource2Target}
     */
    private Assignment computeAssignSource2Target() {
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
                rhs = rule.getParBinding(ix).withTarget(var);
                assert rhs != null;
            } else {
                // the value is an output parameter of the procedure
                rhs = Binding.none(var);
            }
            result.add(rhs);
        }
        return result;
    }

    /**
     * Returns an assignment to the target variables of this
     * switch, based on the output parameters of the (procedure) call.
     * This is only valid if this switch contains a procedure call.
     */
    public Assignment assignPar2Target() {
        return this.assignPar2Target.get();
    }

    /**
     * Lazily computed assignment to the target variables of this
     * switch, based on the output parameters of the (procedure) call.
     * This is only valid if this switch contains a procedure call.
     */
    private final Supplier<Assignment> assignPar2Target = lazy(this::computeAssignPar2Target);

    /** Computes the value for {@link #assignPar2Target}. */
    private Assignment computeAssignPar2Target() {
        assert getKind().isProcedure();
        var outParMap = getCall().getOutVars();
        List<Binding> bindings = new ArrayList<>();
        for (var var : onFinish().getVars()) {
            var parIx = outParMap.get(var);
            Binding bind;
            if (parIx == null) {
                bind = Binding.none(var);
            } else {
                bind = Binding.var(var, parIx);
            }
            bindings.add(bind);
        }
        return new Assignment(bindings);
    }

    /** Returns an assignment to source variables of the initial location of
     * the callee template, based on source variables of this switch.
     * This is only valid if the callee is a procedure.
     */
    public Assignment assignSource2Init() {
        Procedure callee = (Procedure) getUnit();
        Template template = callee.getTemplate();
        return template.getStart().assignPar2Init().after(assignSource2Par());
    }

    /** Returns an assignment to the target variables of this switch, based on
     * the variables in a final location of the called procedure.
     * This is only valid if this switch is a procedure call.
     * @param exit the final location of the called procedure template
     */
    public Assignment assignFinal2Target(Location exit) {
        return assignPar2Target().after(exit.assignFinal2Par());
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
