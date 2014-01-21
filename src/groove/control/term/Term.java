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
import groove.control.Position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Symbolic location, obtained by combining a number of existing locations.
 * Used as a device in building control automata.
 * @author Arend Rensink
 * @version $Revision $
 */
abstract public class Term implements Position<Term> {
    /** Constructor for a prototype term. */
    private Term(TermPool pool) {
        this.pool = pool;
        this.op = null;
        this.args = null;
    }

    /**
     * Constructs a term with a give operator and arguments.
     */
    protected Term(TermPool pool, Op op) {
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
        this.args = new Term[otherArgs.length + 1];
        this.args[0] = arg0;
        System.arraycopy(otherArgs, 0, this.args, 1, otherArgs.length);
        this.pool = arg0.getPool();
        assert op.getArity() == this.args.length;
        assert argsSharePool();
    }

    /** Returns the term pool used to normalise this term. */
    TermPool getPool() {
        return this.pool;
    }

    private final TermPool pool;

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
        return this.op;
    }

    private final Op op;

    /** Returns the arguments of this term. */
    public Term[] getArgs() {
        return this.args;
    }

    private final Term[] args;

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

    /** Returns whether this symbolic location is final. */
    public final Type getType() {
        if (this.type == null) {
            this.type = computeType();
        }
        return this.type;
    }

    private Type type = null;

    /** Computes whether this symbolic location is final. */
    abstract protected Type computeType();

    /** Returns whether this symbolic location is final. */
    public final boolean isFinal() {
        return getType() == Type.FINAL;
    }

    /** Indicates if this term has any outgoing edges. */
    public final boolean isTrial() {
        return getType() == Type.TRIAL;
    }

    /** Indicates that this term is dead, i.e., has no outgoing edges and is not final. */
    public final boolean isDead() {
        return getType() == Type.DEAD;
    }

    /** Returns the set of derivations for this symbolic location. */
    public final DerivationList getAttempt() {
        if (this.outEdges == null) {
            this.outEdges = computeAttempt();
        }
        return this.outEdges;
    }

    private DerivationList outEdges;

    /** Computes the set of outgoing call edges for this symbolic location. */
    abstract protected DerivationList computeAttempt();

    /** Callback factory method for a list of attempts. */
    protected final DerivationList createAttempt() {
        return new DerivationList();
    }

    /** Returns the success transition for this symbolic location. */
    public final Term onSuccess() {
        if (this.success == null) {
            this.success = computeSuccess();
        }
        return this.success;
    }

    private Term success;

    /** Computes the success transition for this symbolic location. */
    abstract protected Term computeSuccess();

    /** Returns the failure transition for this symbolic location. */
    public final Term onFailure() {
        if (this.failure == null) {
            this.failure = computeFailure();
        }
        return this.failure;
    }

    private Term failure;

    /** Computes the failure transition for this symbolic location. */
    abstract protected Term computeFailure();

    /** Indicates if the failure verdicts transitively lead to a final term. */
    public final boolean willSucceed() {
        if (isTrial()) {
            return onFailure().willSucceed();
        } else {
            return isFinal();
        }
    }

    /** Indicates if this is a top-revel term, i.e., with transient depth 0. */
    public final boolean isTopLevel() {
        return getDepth() == 0;
    }

    /** Returns the transient depth of this symbolic location. */
    public final int getDepth() {
        if (this.depth == null) {
            this.depth = computeDepth();
        }
        return this.depth;
    }

    /** Computes the transient depth of this symbolic location. */
    abstract protected int computeDepth();

    private Integer depth;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(this.args);
        result = prime * result + this.op.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Term)) {
            return false;
        }
        Term other = (Term) obj;
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
        String args = getOp().arity == 0 ? "" : Arrays.toString(getArgs());
        return name + args;
    }

    /** Yields an extensive description of the term. */
    public String toDebugString() {
        String result = toString() + ": transient depth " + getDepth();
        switch (getType()) {
        case DEAD:
            result = result + ", deadlocked";
            break;
        case FINAL:
            result = result + ", final";
            break;
        case TRIAL:
            for (Derivation edge : getAttempt()) {
                result =
                    result + "\n  --" + edge.getCall() + "--> "
                        + edge.target().toString();
            }
            result = result + "\nSuccess: " + onSuccess().toString();
            result = result + "\nFailure: " + onFailure().toString();
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
            return getPool().normalise(result);
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
            return getPool().normalise(result);
        }
    }

    /** Returns the if-also-else of this term and two others. */
    public Term ifAlsoElse(Term thenPart, Term alsoPart, Term elsePart) {
        if (isDead()) {
            return elsePart;
        } else if (isFinal()) {
            return thenPart.or(alsoPart);
        } else {
            Term result = new IfTerm(this, thenPart, alsoPart, elsePart);
            return getPool().normalise(result);
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
        return atom().ifElse(epsilon(), elsePart);
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
        return atom().whileDo(epsilon());
    }

    /** Returns the until of this term and another. */
    public Term untilDo(Term arg1) {
        if (isFinal()) {
            return epsilon();
        } else if (isDead()) {
            return star().seq(delta());
        } else {
            Term result = new UntilTerm(this, arg1);
            return getPool().normalise(result);
        }
    }

    /** Returns the while of this term. */
    public Term whileDo(Term bodyPart) {
        if (isDead()) {
            return epsilon();
        } else {
            Term result = new WhileTerm(this, bodyPart);
            return getPool().normalise(result);
        }
    }

    /** Returns the Kleene star of this term. */
    public Term star() {
        if (isDead() || isFinal()) {
            return epsilon();
        } else {
            Term result = new StarTerm(this);
            return getPool().normalise(result);
        }
    }

    /** Returns this term, wrapped into an atomic block. */
    public Term atom() {
        if (isDead()) {
            return this;
        } else if (isFinal()) {
            return epsilon();
        } else {
            AtomTerm result = new AtomTerm(this);
            return getPool().normalise(result);
        }
    }

    /** Returns this term, with increased atom depth. */
    public Term transit() {
        if (isFinal()) {
            return epsilon();
        } else if (isDead()) {
            return delta(getDepth() + 1);
        } else {
            TransitTerm result = new TransitTerm(this);
            return getPool().normalise(result);
        }
    }

    /** Returns the unique delta term at a certain depth. */
    public Term delta(int depth) {
        DeltaTerm result = new DeltaTerm(getPool(), depth);
        return getPool().normalise(result);
    }

    /** Returns the unique delta term at depth 0. */
    public Term delta() {
        return delta(0);
    }

    /** Returns the unique epsilon term. */
    public Term epsilon() {
        EpsilonTerm result = new EpsilonTerm(getPool());
        return getPool().normalise(result);
    }

    /** Returns a call term wrapping a given call. */
    public Term call(Call call) {
        CallTerm result = new CallTerm(getPool(), call);
        return getPool().normalise(result);
    }

    /** Creates a prototype term. */
    public static Term prototype() {
        return new Term(new TermPool()) {
            @Override
            protected DerivationList computeAttempt() {
                throw new UnsupportedOperationException();
            }

            @Override
            protected Term computeSuccess() {
                throw new UnsupportedOperationException();
            }

            @Override
            protected Term computeFailure() {
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
        };
    }

    /** 
     * Helper method to modify a set of outgoing edges so that
     * their targets are made transient.
     */
    static List<Derivation> makeTransit(List<Derivation> edges) {
        List<Derivation> result = new ArrayList<Derivation>();
        for (Derivation edge : edges) {
            result.add(edge.newAttempt(edge.target().transit()));
        }
        return result;
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
        TRANSIT(1), ;

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
