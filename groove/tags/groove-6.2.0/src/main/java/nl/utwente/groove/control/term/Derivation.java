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
package nl.utwente.groove.control.term;

import java.util.Optional;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.control.Attempt;
import nl.utwente.groove.control.Call;
import nl.utwente.groove.control.CallStack;
import nl.utwente.groove.util.Groove;
import nl.utwente.groove.util.Pair;

/**
 * Symbolic derivation of a term.
 * This is a pair of the control call and the target term.
 * @author Arend Rensink
 * @version $Revision $
 */
@NonNullByDefault
public class Derivation extends Pair<Call,Term> implements Attempt.Stage<Term,Derivation> {
    /**
     * Constructs a derivation out of a call and a target term,
     * with a given caller.
     */
    public Derivation(Call call, int depth, Term target, @Nullable Derivation nested) {
        super(call, target);
        this.depth = depth;
        this.nested = Groove.ofNullable(nested);
    }

    /**
     * Constructs a derivation out of a call and a target term.
     */
    public Derivation(Call call, Term target) {
        this(call, 0, target, null);
    }

    @Override
    public Call getRuleCall() {
        return getStack().peekLast().getCall();
    }

    /**
     * Returns the original derived call.
     * If this derivation has a nested derivation,
     * then this is a procedure call, otherwise it is a rule call
     * (identical to #getRuleCall())
     */
    public Call getCall() {
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
        return this.depth;
    }

    private final int depth;

    /** Returns the optional nested derivation of this derivation. */
    public Optional<Derivation> getNested() {
        return this.nested;
    }

    private final Optional<Derivation> nested;

    /** Returns the stack of derivations of which this is the top element. */
    public DerivationStack getStack() {
        var result = this.stack;
        if (result == null) {
            this.stack = result = new DerivationStack(this);
        }
        return result;
    }

    private @Nullable DerivationStack stack;

    @Override
    public CallStack getCallStack() {
        return getStack().getCallStack();
    }

    /** Creates a new derivation, with the call and derivation stack of this one but another target term. */
    public Derivation newInstance(Term target, boolean enterAtom) {
        int depth = getTransience() + (enterAtom
            ? 1
            : 0);
        return new Derivation(getCall(), depth, target, Groove.orElse(getNested(), null));
    }

    /**
     * Creates a new derivation, with a given nested call at the top of
     * the call stack.
     */
    public Derivation newInstance(Derivation nested) {
        var newNested = getNested().map(n -> n.newInstance(nested)).orElse(nested);
        return new Derivation(getCall(), getTransience(), onFinish(), newNested);
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
