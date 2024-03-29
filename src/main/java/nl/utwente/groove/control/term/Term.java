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

import static nl.utwente.groove.util.Factory.lazy;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.control.Call;
import nl.utwente.groove.control.CtrlVar;
import nl.utwente.groove.control.Position;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.collect.Pool;

/**
 * Control term
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
abstract public class Term implements Position<Term,Derivation> {
    /** Constructor for a prototype term. */
    private Term(Pool<Term> pool) {
        this.pool = pool;
        this.op = null;
        this.args = null;
    }

    /**
     * Constructs a term with a give operator and arguments.
     */
    protected Term(Pool<Term> pool, Op op) {
        this.op = op;
        this.args = new Term[0];
        this.pool = pool;
        assert op.getArity() == 0;
    }

    /**
     * Constructs a term with a give operator and arguments.
     */
    protected Term(Op op, Term arg0, Term... otherArgs) {
        this.op = op;
        var args = new Term[otherArgs.length + 1];
        args[0] = arg0;
        System.arraycopy(otherArgs, 0, args, 1, otherArgs.length);
        this.pool = arg0.getPool();
        this.args = args;
        assert op.getArity() == args.length;
        assert argsSharePool();
    }

    /** Returns the term pool used to normalise this term. */
    Pool<Term> getPool() {
        return this.pool;
    }

    private final Pool<Term> pool;

    /** Tests if all arguments of this term share the term pool. */
    private boolean argsSharePool() {
        boolean result = true;
        for (Term arg : getArgs()) {
            if (arg.getPool() != getPool()) {
                result = false;
                break;
            }
        }
        return result;
    }

    /** Returns the operator of this term. */
    public Op getOp() {
        var result = this.op;
        if (result == null) {
            throw Exceptions.illegalState("Prototype term has no operator");
        }
        return result;
    }

    private final @Nullable Op op;

    /** Returns the arguments of this term. */
    public Term[] getArgs() {
        var result = this.args;
        if (result == null) {
            throw Exceptions.illegalState("Prototype term has no arguments");
        }
        return result;
    }

    private final Term @Nullable [] args;

    /** Returns the first argument of this term. */
    protected final Term arg0() {
        return getArgs()[0];
    }

    /** Returns the second argument of this term. */
    protected final Term arg1() {
        return getArgs()[1];
    }

    /** Returns the third argument of this term. */
    protected final Term arg2() {
        return getArgs()[2];
    }

    /** Returns the third argument of this term. */
    protected final Term arg3() {
        return getArgs()[3];
    }

    @Override
    public boolean isStart() {
        return false;
    }

    @Override
    public final Type getType() {
        return this.type.get();
    }

    private Supplier<Type> type = lazy(this::computeType);

    /** Computes the position type of this term. */
    abstract protected Type computeType();

    @Override
    public final boolean isFinal() {
        return getType() == Type.FINAL;
    }

    @Override
    public final boolean isTrial() {
        return getType() == Type.TRIAL;
    }

    @Override
    public final boolean isDead() {
        return getType() == Type.DEAD;
    }

    @Override
    public final DerivationAttempt getAttempt() {
        return getAttempt(true);
    }

    /**
     * Returns the derivation for this term.
     * @param nested if {@code true}, the nested derivation is computed,
     * otherwise only the bottom-level derivation is computed
     */
    public final DerivationAttempt getAttempt(boolean nested) {
        return nested
            ? this.nestedAttempt.get()
            : this.flatAttempt.get();
    }

    private Supplier<DerivationAttempt> flatAttempt = lazy(() -> this.computeAttempt(false));
    private Supplier<DerivationAttempt> nestedAttempt = lazy(() -> this.computeAttempt(true));

    /**
     * Computes the derivation of this term.
     * @param nested if {@code true}, the nested derivation is computed,
     * otherwise only the bottom-level derivation is computed
     * @throws UnsupportedOperationException if this term is not a trial
     */
    abstract protected DerivationAttempt computeAttempt(boolean nested) throws UnsupportedOperationException;

    /** Callback factory method for a list of attempts. */
    protected final DerivationAttempt createAttempt() {
        return new DerivationAttempt();
    }

    /** Returns the prioritised choice of subterms.
     * This is a mapping from priorities to subterms, such that this (top-level) term is of the form
     * {@code or(prio_n).or(prio_{n-1}).prio_0} (where prio_0...prio_n are the values in the result map).
     * This means that if this is not a choice term, the result map will have only 1 entry.
     * The map is used to correctly build choices between prioritised rules.
     * TODO this is an unfinished attempt to resolve SF feature request #195
    abstract protected Map<Integer,Term> getPriorityChoice();
     */

    /** Callback method check whether {@link #getAttempt()} is called on a trial term. */
    protected void checkTrial() throws UnsupportedOperationException {
        if (!isTrial()) {
            throw noTrial();
        }
    }

    /** Callback method to created a uniform exception in case {@link #getAttempt()} is called on a non-trial term. */
    protected UnsupportedOperationException noTrial() {
        return Exceptions.unsupportedOp("Non-trial term %s does not have a derivation", this);
    }

    /** Indicates if the failure verdicts transitively lead to a final term. */
    public final boolean willSucceed() {
        if (isTrial()) {
            return getAttempt().onFailure().willSucceed();
        } else {
            return isFinal();
        }
    }

    /** Indicates if this is a top-revel term, i.e., with transient depth 0. */
    public final boolean isTopLevel() {
        return getTransience() == 0;
    }

    /** Returns the transient depth of this symbolic location. */
    @Override
    public final int getTransience() {
        return this.depth.get();
    }

    /** Computes the transient depth of this symbolic location. */
    abstract protected int computeDepth();

    private Supplier<Integer> depth = lazy(this::computeDepth);

    /**
     * Indicates if the execution of this term is guaranteed to
     * be atomic.
     */
    abstract protected boolean isAtomic();

    @Override
    public final int hashCode() {
        if (this.hashCode == 0) {
            this.hashCode = computeHashCode();
        }
        return this.hashCode;
    }

    /** Callback mathod to store the hash code. */
    protected int computeHashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(this.args);
        result = prime * result + getOp().hashCode();
        return result == 0
            ? 1
            : result;
    }

    private int hashCode;

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Term other)) {
            return false;
        }
        if (!Arrays.equals(this.args, other.args)) {
            return false;
        }
        if (this.op != other.op) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String name = getClass().getSimpleName();
        name = name.substring(0, name.lastIndexOf("Term"));
        String args = getOp().arity == 0
            ? ""
            : Arrays.toString(getArgs());
        return name + args;
    }

    /** Yields an extensive description of the term. */
    public String toDebugString() {
        String result = toString() + ": transient depth " + getTransience();
        switch (getType()) {
        case DEAD -> result = result + ", deadlocked";
        case FINAL -> result = result + ", final";
        case TRIAL -> {
            DerivationAttempt attempt = getAttempt(false);
            for (Derivation deriv : attempt) {
                result = result + "\n  --" + deriv.getOuterCall() + "--> "
                    + deriv.onFinish().toString();
            }
            result = result + "\nSuccess: " + attempt.onSuccess().toString();
            result = result + "\nFailure: " + attempt.onFailure().toString();
        }
        default -> throw Exceptions.UNREACHABLE;
        }
        return result;
    }

    /** Returns the sequential composition of this term with another. */
    public Term seq(Term arg1) {
        if (isFinal()) {
            return arg1;
        } else if (isDead()) {
            return this;
        } else if (arg1.isFinal()) {
            return this;
        } else {
            SeqTerm result = new SeqTerm(this, arg1);
            return getPool().canonical(result);
        }
    }

    /** Returns the choice between this term and another. */
    public Term or(Term arg1) {
        if (isDead()) {
            return arg1;
        } else if (arg1.isDead()) {
            return this;
        } else {
            Term result = new OrTerm(this, arg1);
            return getPool().canonical(result);
        }
    }

    /** Returns the if-also-else of this term and two others. */
    public Term ifAlsoElse(Term thenPart, Term alsoPart, Term elsePart) {
        if (isDead()) {
            return elsePart;
        } else if (isFinal()) {
            return thenPart.or(alsoPart);
        } else if (alsoPart.isDead() && elsePart.isDead()) {
            return seq(thenPart);
        } else {
            Term result = new IfTerm(this, thenPart, alsoPart, elsePart);
            return getPool().canonical(result);
        }
    }

    /** Returns the if-also of this term (which is the same as if-also-epsilon). */
    public final Term ifAlso(Term thenPart, Term alsoPart) {
        return ifAlsoElse(thenPart, alsoPart, epsilon());
    }

    /** Returns the if of this term (which is the same as if-delta-else). */
    public final Term ifElse(Term thenPart, Term elsePart) {
        return ifAlsoElse(thenPart, delta(), elsePart);
    }

    /** Returns the if of this term (which is the same as if-delta-epsilon). */
    public final Term ifOnly(Term thenPart) {
        return ifElse(thenPart, epsilon());
    }

    /** Returns the try-else of this term and another.
     * This is implemented as <code>if atomic { this } else arg1</code>.
     */
    public final Term tryElse(Term elsePart) {
        return ifElse(epsilon(), elsePart);
    }

    /** Returns the try of this term (which is the same as try-else-epsilon). */
    public final Term tryOnly() {
        return tryElse(epsilon());
    }

    /**
     * Returns the as-long-as-possible of this term.
     * This is implemented as <code>while atomic { this }</code>.
     */
    public final Term alap() {
        return whileDo(epsilon());
    }

    /** Returns the until of this term and another. */
    public Term untilDo(Term arg1) {
        if (isFinal()) {
            return epsilon();
        } else if (isDead()) {
            return star().seq(delta());
        } else {
            Term result = new UntilTerm(this, arg1);
            return getPool().canonical(result);
        }
    }

    /** Returns the while of this term. */
    public Term whileDo(Term bodyPart) {
        if (isDead()) {
            return epsilon();
        } else {
            Term result = new WhileTerm(this, bodyPart);
            return getPool().canonical(result);
        }
    }

    /** Returns the Kleene star of this term. */
    public Term star() {
        if (isDead() || isFinal()) {
            return epsilon();
        } else {
            Term result = new StarTerm(this);
            return getPool().canonical(result);
        }
    }

    /** Returns this term, wrapped into an atomic block. */
    public Term atom() {
        if (isAtomic()) {
            return this;
        } else {
            AtomTerm result = new AtomTerm(this);
            return getPool().canonical(result);
        }
    }

    /** Returns this term, with increased atom depth. */
    public Term transit() {
        if (isFinal()) {
            return epsilon();
        } else if (isDead()) {
            return delta(getTransience() + 1);
        } else {
            TransitTerm result = new TransitTerm(this);
            return getPool().canonical(result);
        }
    }

    /** Returns the unique delta term at a certain depth. */
    public Term delta(int depth) {
        DeltaTerm result = new DeltaTerm(getPool(), depth);
        return getPool().canonical(result);
    }

    /** Returns the unique delta term at depth 0. */
    public Term delta() {
        return delta(0);
    }

    /** Returns the unique epsilon term. */
    public Term epsilon() {
        EpsilonTerm result = new EpsilonTerm(getPool());
        return getPool().canonical(result);
    }

    /** Returns a call term wrapping a given call. */
    public Term call(Call call) {
        CallTerm result = new CallTerm(getPool(), call);
        return getPool().canonical(result);
    }

    /** Returns a term wrapped as the body of a called unit. */
    public Term body(Term inner, Derivation caller) {
        if (inner.isFinal()) {
            return epsilon();
        } else if (inner.isDead()) {
            return delta(inner.getTransience());
        } else {
            BodyTerm result = new BodyTerm(inner, caller);
            return getPool().canonical(result);
        }
    }

    @Override
    public boolean hasVars() {
        return false;
    }

    @Override
    public List<CtrlVar> getVars() {
        return Collections.emptyList();
    }

    /** Creates a prototype term. */
    public static Term prototype() {
        return new Term(new Pool<>()) {
            @Override
            protected DerivationAttempt computeAttempt(boolean nested) {
                throw new UnsupportedOperationException();
            }

            @Override
            protected int computeDepth() {
                throw new UnsupportedOperationException();
            }

            @Override
            protected Type computeType() {
                throw new UnsupportedOperationException();
            }

            @Override
            protected boolean isAtomic() {
                return false;
            }
        };
    }

    /** Operators available for construction. */
    public static enum Op {
        /** Call. */
        CALL(0),
        /** Deadlocked state. */
        DELTA(0),
        /** Terminated state. */
        EPSILON(0),
        /** Sequential composition. */
        SEQ(2),
        /** Choice. */
        OR(2),
        /** If/also/else. */
        IF(4),
        /** Try/else. */
        HASH(2),
        /** As-long-as-possible. */
        ALAP(1),
        /** UNTIL. */
        UNTIL(2),
        /** While. */
        WHILE(2),
        /** Kleene star. */
        STAR(1),
        /** Atomic block. */
        ATOM(1),
        /** Transient term. */
        TRANSIT(1),
        /** Procedure body. */
        BODY(1),;

        private Op(int arity) {
            this.arity = arity;
        }

        /** Returns the arity of the operator. */
        public int getArity() {
            return this.arity;
        }

        private final int arity;
    }
}
