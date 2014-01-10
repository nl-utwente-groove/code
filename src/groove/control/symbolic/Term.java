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
package groove.control.symbolic;

import groove.control.Call;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Symbolic location, obtained by combining a number of existing locations.
 * Used as a device in building control automata.
 * @author Arend Rensink
 * @version $Revision $
 */
abstract public class Term implements Cloneable {
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

    /** Returns the set of outgoing call edges for this symbolic location. */
    public final List<OutEdge> getOutEdges() {
        if (this.outEdges == null) {
            this.outEdges = computeOutEdges();
        }
        return this.outEdges;
    }

    private List<OutEdge> outEdges;

    /** Computes the set of outgoing call edges for this symbolic location. */
    abstract protected List<OutEdge> computeOutEdges();

    /** Indicates if this term has a success transition. */
    public final boolean hasSuccess() {
        return getSuccess() != null;
    }

    /** Returns the success transition for this symbolic location. */
    public final Term getSuccess() {
        if (this.success == null) {
            this.success = computeSuccess();
        }
        return this.success;
    }

    private Term success;

    /** Computes the success transition for this symbolic location. */
    abstract protected Term computeSuccess();

    /** Indicates if this term has a failure transition. */
    public final boolean hasFailure() {
        return getFailure() != null;
    }

    /** Returns the failure transition for this symbolic location. */
    public final Term getFailure() {
        if (this.failure == null) {
            this.failure = computeFailure();
        }
        return this.failure;
    }

    private Term failure;

    /** Computes the failure transition for this symbolic location. */
    abstract protected Term computeFailure();

    /** Indicates if this is a top-revel term, i.e., with transient depth 0. */
    public final boolean isTopLevel() {
        return getTransitDepth() == 0;
    }

    /** Returns the transient depth of this symbolic location. */
    public final int getTransitDepth() {
        if (this.transitDepth == null) {
            this.transitDepth = computeTransitDepth();
        }
        return this.transitDepth;
    }

    /** Computes the transient depth of this symbolic location. */
    abstract protected int computeTransitDepth();

    private Integer transitDepth;

    /** Returns whether this symbolic location is final. */
    public final boolean isFinal() {
        if (this.isFinal == null) {
            this.isFinal = computeFinal();
        }
        return this.isFinal;
    }

    private Boolean isFinal = null;

    /** Computes whether this symbolic location is final. */
    abstract protected boolean computeFinal();

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
        return getClass().getSimpleName() + Arrays.toString(this.args);
    }

    /** Returns the sequential composition of this term with another. */
    public Term seq(Term arg1) {
        SeqTerm result = new SeqTerm(this, arg1);
        return getPool().normalise(result);
    }

    /** Returns the choice between this term and another. */
    public Term or(Term arg1) {
        Term result = new OrTerm(this, arg1);
        return getPool().normalise(result);
    }

    /** Returns the if-else of this term and another. */
    public Term ifElse(Term arg1) {
        Term result = new IfTerm(getPool(), this, arg1);
        return getPool().normalise(result);
    }

    /** Returns the try-else of this term and another. */
    public Term tryElse(Term arg1) {
        Term result = new TryTerm(this, arg1);
        return getPool().normalise(result);
    }

    /** Returns the as-long-as-possible of this term. */
    public Term alap() {
        Term result = new AlapTerm(this);
        return getPool().normalise(result);
    }

    /** Returns the until of this term and another. */
    public Term untilDo(Term arg1) {
        Term result = new UntilTerm(this, arg1);
        return getPool().normalise(result);
    }

    /** Returns the while of this term. */
    public Term whileDo() {
        Term result = new WhileTerm(this);
        return getPool().normalise(result);
    }

    /** Returns the Kleene star of this term. */
    public Term star() {
        Term result = new StarTerm(this);
        return getPool().normalise(result);
    }

    /** Returns this term, wrapped into an atomic block. */
    public Term atom() {
        AtomTerm result = new AtomTerm(this);
        return getPool().normalise(result);
    }

    /** Returns this term, with increased atom depth. */
    public Term transit() {
        TransitTerm result = new TransitTerm(this);
        return getPool().normalise(result);
    }

    /** Returns the unique delta term. */
    public Term delta() {
        DeltaTerm result = new DeltaTerm(getPool());
        return getPool().normalise(result);
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
            protected List<OutEdge> computeOutEdges() {
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
            protected int computeTransitDepth() {
                throw new UnsupportedOperationException();
            }

            @Override
            protected boolean computeFinal() {
                throw new UnsupportedOperationException();
            }
        };
    }

    /** 
     * Helper method to modify a set of outgoing edges so that
     * their targets are made transient.
     */
    static List<OutEdge> makeTransit(List<OutEdge> edges) {
        List<OutEdge> result = new ArrayList<OutEdge>();
        for (OutEdge edge : edges) {
            result.add(new OutEdge(edge.getCall(), edge.getTarget().transit()));
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
        /** If/else. */
        IF(2),
        /** Try/else. */
        TRY(2),
        /** As-long-as-possible. */
        ALAP(1),
        /** UNTIL. */
        UNTIL(2),
        /** While. */
        WHILE(1),
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
