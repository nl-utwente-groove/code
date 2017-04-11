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

import java.util.LinkedList;
import java.util.List;

import groove.control.Binding;
import groove.control.Call;
import groove.control.CtrlPar;
import groove.control.CtrlVar;
import groove.grammar.Callable;
import groove.grammar.QualName;
import groove.grammar.Rule;
import groove.grammar.Signature;
import groove.grammar.UnitPar;
import groove.util.Pair;

/**
 * Transition between control locations, bearing either a call or a verdict.
 * @author Arend Rensink
 * @version $Revision $
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
        this.kind = call.getUnit()
            .getKind();
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
     * Only valid if this is a call switch.
     */
    public QualName getQualName() {
        return getUnit().getQualName();
    }

    /**
     * Convenience method to return the arguments of the call of this switch.
     * Only valid if this is a call switch.
     * @return the list of arguments
     */
    public final List<? extends CtrlPar> getArgs() {
        return getCall().getArgs();
    }

    /**
     * Convenience method to return the called unit of this switch.
     * Only valid if this is a call switch.
     * @see #getKind()
     */
    public final Callable getUnit() {
        return getCall().getUnit();
    }

    /**
     * Returns the rule or procedure call wrapped in this switch.
     */
    public final Call getCall() {
        return this.call;
    }

    /**
     * The invoked unit of this call.
     * Is {@code null} if this is not a call switch.
     */
    private final Call call;

    /** Returns the additional transient depth effected by this switch. */
    public int getTransience() {
        return this.transience;
    }

    private final int transience;

    /**
     * Returns pairs of formal input parameters of this call and corresponding
     * bindings to source location variables and constant values.
     * This is only valid for rule calls.
     */
    public List<Pair<UnitPar.RulePar,Binding>> getCallBinding() {
        assert getKind() == Callable.Kind.RULE;
        if (this.callBinding == null) {
            this.callBinding = computeCallBinding();
        }
        return this.callBinding;
    }

    /** Binding of in-parameter positions to source variables and constant arguments. */
    private List<Pair<UnitPar.RulePar,Binding>> callBinding;

    /**
     * Computes the binding of formal call parameters to source location
     * variables and constant values.
     * @return a list of pairs of call parameter variables and bindings.
     * The binding is {@code null} for a non-input-parameter.
     */
    private List<Pair<UnitPar.RulePar,Binding>> computeCallBinding() {
        List<Pair<UnitPar.RulePar,Binding>> result = new LinkedList<>();
        List<? extends CtrlPar> args = getArgs();
        Signature<UnitPar.RulePar> sig = ((Rule) getUnit()).getSignature();
        int size = args == null ? 0 : args.size();
        List<CtrlVar> sourceVars = getSource().getVars();
        for (int i = 0; i < size; i++) {
            assert args != null; // size is at least one
            CtrlPar arg = args.get(i);
            Binding bind;
            if (arg instanceof CtrlPar.Var) {
                CtrlPar.Var varArg = (CtrlPar.Var) arg;
                if (arg.isInOnly()) {
                    int ix = sourceVars.indexOf(varArg.getVar());
                    assert ix >= 0;
                    bind = Binding.var(ix);
                } else if (arg.isOutOnly()) {
                    bind = null;
                } else {
                    assert false;
                    bind = null;
                }
            } else if (arg instanceof CtrlPar.Const) {
                bind = Binding.value((CtrlPar.Const) arg);
            } else {
                assert arg instanceof CtrlPar.Wild;
                bind = null;
            }
            result.add(Pair.newPair(sig.getPar(i), bind));
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
        result = prime * result + ((this.call == null) ? 0 : this.call.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Switch)) {
            return false;
        }
        Switch other = (Switch) obj;
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
        int result = onFinish().getNumber() - o.onFinish()
            .getNumber();
        if (result != 0) {
            return result;
        }
        result = getTransience() - o.getTransience();
        if (result != 0) {
            return result;
        }
        result = getKind().ordinal() - o.getKind()
            .ordinal();
        if (result != 0) {
            return result;
        }
        result = getCall().compareTo(o.getCall());
        if (result != 0) {
            return result;
        }
        result = getSource().getNumber() - o.getSource()
            .getNumber();
        return result;
    }

    @Override
    public String toString() {
        return getSource() + "--" + getCall() + "->" + onFinish();
    }
}
