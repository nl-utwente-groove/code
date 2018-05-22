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
package groove.control.term;

import groove.control.Attempt;
import groove.control.Call;
import groove.control.CallStack;
import groove.util.Pair;

/**
 * Symbolic derivation of a term.
 * This is a pair of the control call and the target term.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Derivation extends Pair<Call,Term> implements Attempt.Stage<Term,Derivation> {
    /**
     * Constructs a derivation out of a call and a target term,
     * with a given caller.
     */
    public Derivation(Call call, int depth, Term target, Derivation nested) {
        super(call, target);
        this.depth = depth;
        this.nested = nested;
    }

    /**
     * Constructs a derivation out of a call and a target term.
     */
    public Derivation(Call call, Term target) {
        this(call, 0, target, null);
    }

    @Override
    public Call getRuleCall() {
        return getStack().peekLast()
            .getCall();
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

    /** Returns the (possibly {@code null} nested derivation of this derivation. */
    public Derivation getNested() {
        return this.nested;
    }

    /** Indicates if this derivation has a nested derivation. */
    public boolean hasNested() {
        return getNested() != null;
    }

    private final Derivation nested;

    /** Returns the stack of derivations of which this is the top element. */
    public DerivationStack getStack() {
        if (this.stack == null) {
            this.stack = new DerivationStack(this);
        }
        return this.stack;
    }

    private DerivationStack stack;

    @Override
    public CallStack getCallStack() {
        return getStack().getCallStack();
    }

    /** Creates a new derivation, with the call and derivation stack of this one but another target term. */
    public Derivation newInstance(Term target, boolean enterAtom) {
        int depth = getTransience() + (enterAtom ? 1 : 0);
        return new Derivation(getCall(), depth, target, getNested());
    }

    /**
     * Creates a new derivation, with a given nested call at the top of
     * the call stack.
     */
    public Derivation newInstance(Derivation nested) {
        Derivation result;
        if (hasNested()) {
            result = new Derivation(getCall(), getTransience(), onFinish(),
                getNested().newInstance(nested));
        } else {
            result = new Derivation(getCall(), getTransience(), onFinish(), nested);
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        if (hasNested()) {
            result.append(getNested().toString());
            result.append("::");
        }
        result.append(super.toString());
        return result.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof Derivation)) {
            return false;
        }
        Derivation other = (Derivation) obj;
        if (hasNested()) {
            if (!getNested().equals(other.getNested())) {
                return false;
            }
        } else {
            if (other.hasNested()) {
                return false;
            }
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
        result = prime * result + (hasNested() ? getNested().hashCode() : 0);
        return result;
    }

    private int hashCode;
}
