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
package nl.utwente.groove.control.term;

import java.util.Optional;
import java.util.function.Supplier;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.control.Attempt;
import nl.utwente.groove.control.Call;
import nl.utwente.groove.control.NestedCall;
import nl.utwente.groove.util.Groove;
import nl.utwente.groove.util.LazyFactory;
import nl.utwente.groove.util.Pair;

/**
 * Symbolic derivation of a term.
 * This is a pair of the inner control call and the target term.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class Derivation extends Pair<Call,Term> implements Attempt.Stage<Term,Derivation> {
    /**
     * Constructs a derivation out of an (outer) call and a target term,
     * with an optional nested (inner) derivation.
     */
    public Derivation(Call outer, int depth, Term target, @Nullable Derivation nested) {
        super(outer, target);
        this.transience = depth;
        this.nested = Groove.ofNullable(nested);
    }

    /**
     * Constructs a derivation out of a call and a target term.
     */
    public Derivation(Call call, Term target) {
        this(call, 0, target, null);
    }

    @Override
    public Call getInnerCall() {
        return getCall().getInner();
    }

    /**
     * Returns the original derived call.
     * If this derivation has a nested derivation,
     * then this is a procedure call, otherwise it is a rule call
     * (identical to #getRuleCall())
     */
    public Call getOuterCall() {
        return one();
    }

    /**
     * Returns the target term of this derivation.
     */
    @Override
    public Term onFinish() {
        return two();
    }

    @Override
    public int getTransience() {
        return this.transience;
    }

    private final int transience;

    /** Returns the optional nested (inner) derivation of this derivation. */
    public Optional<Derivation> getNested() {
        return this.nested;
    }

    private final Optional<Derivation> nested;

    @Override
    public NestedCall getCall() {
        return this.call.get();
    }

    /** The nested call of this derivation. */
    private Supplier<NestedCall> call = LazyFactory.instance(this::computeCall);

    /** Computes the value for {@link #call}. */
    private NestedCall computeCall() {
        var result = new NestedCall();
        result.push(one());
        var derivation = this;
        while (derivation.getNested().isPresent()) {
            derivation = derivation.getNested().get();
            result.push(derivation.one());
        }
        return result;
    }

    /** Creates a new derivation, with the call and derivation stack of this one but another target term. */
    public Derivation newInstance(Term target, boolean enterAtom) {
        int depth = getTransience() + (enterAtom
            ? 1
            : 0);
        return new Derivation(getOuterCall(), depth, target, Groove.orElse(getNested(), null));
    }

    /**
     * Creates a new derivation, pushing a given derivation to the top of the nested derivation stack.
     */
    public Derivation newInstance(Derivation nested) {
        var newNested = getNested().map(n -> n.newInstance(nested)).orElse(nested);
        return new Derivation(getOuterCall(), getTransience(), onFinish(), newNested);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        getNested().ifPresent(d -> result.append(d.toString() + "::"));
        result.append(super.toString());
        return result.toString();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof Derivation other)) {
            return false;
        }
        if (!getNested().equals(other.getNested())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        if (this.hashCode == 0) {
            this.hashCode = computeHashCode();
        }
        return this.hashCode;
    }

    private int computeHashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = prime * result + getNested().hashCode();
        return result;
    }

    private int hashCode;
}
