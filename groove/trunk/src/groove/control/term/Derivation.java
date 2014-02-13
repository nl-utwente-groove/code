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

import groove.control.Call;
import groove.util.Pair;

/**
 * Symbolic derivation of a term.
 * This is a pair of the control call and the target term.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Derivation extends Pair<Call,Term> {
    /**
     * Constructs a derivation out of a call and a target term,
     * with a given caller.
     */
    public Derivation(Call call, Term target, Derivation caller) {
        super(call, target);
        this.caller = caller;
    }

    /**
     * Constructs a derivation out of a call and a target term.
     */
    public Derivation(Call call, Term target) {
        this(call, target, null);
    }

    /** Returns the call wrapped into this edge.
     */
    public Call getCall() {
        return one();
    }

    /**
     * Returns the target term of this derivation.
     */
    public Term onFinish() {
        return two();
    }

    /** Returns the (possibly {@code null} caller of this derivation. */
    public Derivation getCaller() {
        return this.caller;
    }

    /** Indicates if this derivation has a caller. */
    public boolean hasCaller() {
        return getCaller() != null;
    }

    private final Derivation caller;

    /** Returns the stack of derivations of which this is the top element. */
    public DerivationStack getStack() {
        if (this.stack == null) {
            this.stack = new DerivationStack(this);
        }
        return this.stack;
    }

    private DerivationStack stack;

    /** Creates a new derivation, with the call and derivation stack of this one but another target term. */
    public Derivation newInstance(Term target) {
        return new Derivation(getCall(), target, getCaller());
    }

    /**
     * Creates a new derivation, with a given caller at the bottom of
     * the call stack.
     */
    public Derivation newInstance(Derivation caller) {
        Derivation result;
        if (hasCaller()) {
            result = new Derivation(getCall(), onFinish(), getCaller().newInstance(caller));
        } else {
            result = new Derivation(getCall(), onFinish(), caller);
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        if (hasCaller()) {
            result.append(getCaller().toString());
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
        if (hasCaller()) {
            if (getCaller().equals(other.hasCaller())) {
                return false;
            }
        } else {
            if (other.hasCaller()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = prime * result + (hasCaller() ? getCaller().hashCode() : 0);
        return result;
    }
}
