/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: CTLStarFormula.java,v 1.11 2008-02-28 05:58:22 kastenberg Exp $
 */

package groove.verify;

import groove.view.FormatException;

/**
 * Data structure for temporal formulae.
 * @author Arend Rensink
 * @version $Revision: 3124 $ $Date: 2008-02-28 05:58:22 $
 */
public class Formula {
    /** Default constructor filling all fields. */
    private Formula(Kind kind, Formula arg1, Formula arg2, String prop) {
        this.kind = kind;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.prop = prop;
    }

    private Formula(Kind operator) {
        this(operator, null, null, null);
        assert operator.getArity() == 0 && operator != Kind.ATOM;
    }

    private Formula(Kind operator, Formula arg) {
        this(operator, arg, null, null);
        assert operator.getArity() == 1;
    }

    private Formula(Kind operator, Formula arg1, Formula arg2) {
        this(operator, arg1, arg2, null);
        assert operator.getArity() == 2;
    }

    private Formula(String prop) {
        this(Kind.ATOM, null, null, prop);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result =
            prime * result + ((this.arg1 == null) ? 0 : this.arg1.hashCode());
        result =
            prime * result + ((this.arg2 == null) ? 0 : this.arg2.hashCode());
        result =
            prime * result + ((this.kind == null) ? 0 : this.kind.hashCode());
        result =
            prime * result + ((this.prop == null) ? 0 : this.prop.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Formula other = (Formula) obj;
        if (this.kind != other.kind) {
            return false;
        }
        if (this.arg1 != null && !this.arg1.equals(other.arg1)) {
            return false;
        }
        if (this.arg2 != null && !this.arg2.equals(other.arg2)) {
            return false;
        }
        if (this.prop != null && !this.prop.equals(other.prop)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        toString(result);
        return result.toString();
    }

    /** Appends a given string buffer with a string description of this formula. */
    private void toString(StringBuffer b) {
        switch (getKind().getArity()) {
        case 0:
            b.append(getProp());
            break;
        case 1:
            b.append(getKind());
            if (getArg1().getKind().getPriority() < getKind().getPriority()) {
                getArg1().toParString(b);
            } else {
                b.append(' ');
                getArg1().toString(b);
            }
            break;
        default:
            assert getKind().getArity() == 2;
            boolean arg1Par =
                getArg1().getKind().getPriority() <= getKind().getPriority();
            boolean arg2Par =
                getArg2().getKind().getPriority() < getKind().getPriority();
            boolean opLetter =
                Character.isLetter(getKind().toString().charAt(0));
            if (arg1Par) {
                getArg1().toParString(b);
            } else {
                getArg1().toString(b);
            }
            if (opLetter && !arg1Par) {
                b.append(' ');
            }
            b.append(getKind());
            if (opLetter && !arg2Par) {
                b.append(' ');
            }
            if (arg2Par) {
                getArg2().toParString(b);
            } else {
                getArg2().toString(b);
            }
        }
    }

    /**
     * Appends a given string buffer with a string description of this formula,
     * surrounded by parentheses. 
     */
    private void toParString(StringBuffer b) {
        b.append('(');
        getArg1().toString(b);
        b.append(')');
    }

    /** Returns the top-level operator of the formula. */
    public Kind getKind() {
        return this.kind;
    }

    /** Returns the first argument of the top-level operator, if any. */
    public Formula getArg1() {
        return this.arg1;
    }

    /** Returns the second argument of the top-level operator, if any. */
    public Formula getArg2() {
        return this.arg2;
    }

    /** Returns the proposition wrapped in this formula, if any. */
    public String getProp() {
        return this.prop;
    }

    /** 
     * Converts this formula to a CTL formula, if possible.
     * This succeeds if and only if all temporal operators in this
     * formula are immediately nested inside a path quantifier, and
     * vice versa.
     * @return the CTL formula corresponding to this formula
     * @throws FormatException if this formula contains combinations of operators
     * that are illegal in CTL.
     */
    public Formula toCtlFormula() throws FormatException {
        switch (getKind()) {
        case ATOM:
        case TRUE:
        case FALSE:
            return this;
        case NOT:
            return not(getArg1().toCtlFormula());
        case OR:
        case AND:
            return new Formula(getKind(), getArg1().toCtlFormula(),
                getArg2().toCtlFormula());
        case IMPLIES:
            return or(not(getArg1()), getArg2()).toCtlFormula();
        case FOLLOWS:
            return or(getArg1(), not(getArg2())).toCtlFormula();
        case EQUIV:
            return and(implies(getArg1(), getArg2()),
                follows(getArg1(), getArg2())).toCtlFormula();
        case NEXT:
        case UNTIL:
        case W_UNTIL:
        case RELEASE:
        case S_RELEASE:
        case ALWAYS:
        case EVENTUALLY:
            throw new FormatException(
                "Temporal operator '%s' should be nested inside path quantifier in CTL formula",
                getKind());
        case FORALL:
        case EXISTS:
            Kind subKind = getArg1().getKind();
            Formula subArg1 = getArg1().getArg1();
            Formula subArg2 = getArg1().getArg2();
            switch (subKind) {
            case NEXT:
            case ALWAYS:
            case EVENTUALLY:
                return new Formula(getKind(), new Formula(subKind,
                    subArg1.toCtlFormula()));
            case UNTIL:
                return new Formula(getKind(), until(subArg1.toCtlFormula(),
                    subArg2.toCtlFormula()));
            case W_UNTIL:
                return or(until(subArg1, subArg2), always(subArg1)).toCtlFormula();
            case RELEASE:
                return not(until(not(subArg2), not(subArg1))).toCtlFormula();
            case S_RELEASE:
                return not(w_until(not(subArg2), not(subArg1))).toCtlFormula();
            default:
                throw new FormatException(
                    "Path quantifier '%s' must have nested temporal operator in CTL formula",
                    getKind());
            }
        default:
            throw new FormatException("Unknown temporal operator %s", getKind());
        }
    }

    /** 
     * Converts this formula to a NASA LTL formula, if possible.
     * This succeeds if and only if the formula does not contain
     * {@link Kind#FORALL} or {@link Kind#EXISTS} operators.
     * @return the NASA LTL formula corresponding to this formula
     * @throws FormatException if this formula contains operators
     * that are illegal in LTL.
     */
    public gov.nasa.ltl.trans.Formula<String> toLtlFormula()
        throws FormatException {
        gov.nasa.ltl.trans.Formula<String> arg1 =
            getArg1() == null ? null : getArg1().toLtlFormula();
        gov.nasa.ltl.trans.Formula<String> arg2 =
            getArg2() == null ? null : getArg2().toLtlFormula();
        switch (getKind()) {
        case FORALL:
        case EXISTS:
            throw new FormatException(
                "Path quantifier '%s' not allowed in LTL formula", getKind());
        case ATOM:
            return gov.nasa.ltl.trans.Formula.Proposition(getProp());
        case TRUE:
            return gov.nasa.ltl.trans.Formula.True();
        case FALSE:
            return gov.nasa.ltl.trans.Formula.False();
        case NOT:
            return gov.nasa.ltl.trans.Formula.Not(arg1);
        case OR:
            return gov.nasa.ltl.trans.Formula.Or(arg1, arg2);
        case AND:
            return gov.nasa.ltl.trans.Formula.And(arg1, arg2);
        case NEXT:
            return gov.nasa.ltl.trans.Formula.Next(arg1);
        case RELEASE:
            return gov.nasa.ltl.trans.Formula.WRelease(arg1, arg2);
        case S_RELEASE:
            return gov.nasa.ltl.trans.Formula.Release(arg1, arg2);
        case UNTIL:
            return gov.nasa.ltl.trans.Formula.Until(arg1, arg2);
        case W_UNTIL:
            return gov.nasa.ltl.trans.Formula.WUntil(arg1, arg2);
        case ALWAYS:
            return gov.nasa.ltl.trans.Formula.Always(arg1);
        case EVENTUALLY:
            return gov.nasa.ltl.trans.Formula.Eventually(arg1);
        case EQUIV:
            return and(implies(getArg1(), getArg2()),
                follows(getArg1(), getArg2())).toLtlFormula();
        case FOLLOWS:
            return or(getArg1(), not(getArg2())).toLtlFormula();
        case IMPLIES:
            return or(not(getArg1()), getArg2()).toLtlFormula();
        }
        throw new FormatException("Unknown temporal operator %s", getKind());
    }

    private final Kind kind;
    private final Formula arg1;
    private final Formula arg2;
    private final String prop;

    /** Factory method for a propositional formula. */
    public static Formula atom(String prop) {
        return new Formula(prop);
    }

    /** Creator method for a negation. */
    public static Formula not(Formula f) {
        return new Formula(Kind.NOT, f);
    }

    /** Creator method for a conjunction. */
    public static Formula and(Formula f1, Formula f2) {
        return new Formula(Kind.AND, f1, f2);
    }

    /** Creator method for a disjunction. */
    public static Formula or(Formula f1, Formula f2) {
        return new Formula(Kind.OR, f1, f2);
    }

    /** Creator method for an implication. */
    public static Formula implies(Formula f1, Formula f2) {
        return new Formula(Kind.IMPLIES, f1, f2);
    }

    /** Creator method for an inverse implication. */
    public static Formula follows(Formula f1, Formula f2) {
        return new Formula(Kind.FOLLOWS, f1, f2);
    }

    /** Creator method for an equivalence. */
    public static Formula equiv(Formula f1, Formula f2) {
        return new Formula(Kind.EQUIV, f1, f2);
    }

    /** Creator method for an until formula. */
    public static Formula until(Formula f1, Formula f2) {
        return new Formula(Kind.UNTIL, f1, f2);
    }

    /** Creator method for an until formula. */
    public static Formula next(Formula f) {
        return new Formula(Kind.NEXT, f);
    }

    /** Creator method for a release formula. */
    public static Formula release(Formula f1, Formula f2) {
        return new Formula(Kind.RELEASE, f1, f2);
    }

    /** Creator method for a weak until formula. */
    public static Formula w_until(Formula f1, Formula f2) {
        return new Formula(Kind.W_UNTIL, f1, f2);
    }

    /** Creator method for a strong release formula. */
    public static Formula s_release(Formula f1, Formula f2) {
        return new Formula(Kind.S_RELEASE, f1, f2);
    }

    /** Creator method for an always-true formula. */
    public static Formula always(Formula f) {
        return new Formula(Kind.ALWAYS, f);
    }

    /** Creator method for an eventually-true formula. */
    public static Formula eventually(Formula f) {
        return new Formula(Kind.EVENTUALLY, f);
    }

    /** Creator method for a for-all-paths formula. */
    public static Formula forall(Formula f) {
        return new Formula(Kind.FORALL, f);
    }

    /** Creator method for a for-some-path formula. */
    public static Formula exists(Formula f) {
        return new Formula(Kind.EXISTS, f);
    }

    /** The constant formula for true. */
    public static final Formula TRUE = new Formula(Kind.TRUE);

    /** The constant formula for false. */
    public static final Formula FALSE = new Formula(Kind.FALSE);

    /** The kind (i.e., top level operator) of a formula. */
    static public enum Kind {
        /** Atomic proposition. */
        ATOM("", 0, 7),
        /** True. */
        TRUE("", 0, 7),
        /** False. */
        FALSE("", 0, 7),
        /** Negation. */
        NOT("!", 1, 6),
        /** Disjunction. */
        OR("|", 2, 2),
        /** Conjunction. */
        AND("&", 2, 3),
        /** Implication. */
        IMPLIES("->", 2, 1),
        /** Inverse implication. */
        FOLLOWS("<-", 2, 1),
        /** Equivalence. */
        EQUIV("<->", 2, 1),
        /** Next-state. */
        NEXT("X", 1, 6),
        /** Temporal until. */
        UNTIL("U", 2, 4),
        /** Weak temporal until (second operand may never hold). */
        W_UNTIL("W", 2, 4),
        /** Temporal release. */
        RELEASE("V", 2, 4),
        /** Strong temporal release (second operand must eventually hold). */
        S_RELEASE("M", 2, 4),
        /** Everywhere along a path. */
        ALWAYS("G", 1, 6),
        /** Eventually along a path. */
        EVENTUALLY("F", 1, 6),
        /** For all paths. */
        FORALL("A", 1, 3),
        /** There exists a path. */
        EXISTS("E", 1, 3);

        private Kind(String symbol, int arity, int priority) {
            this.symbol = symbol;
            this.arity = arity;
            this.priority = priority;
        }

        @Override
        public String toString() {
            return getSymbol();
        }

        /** Returns the symbol for the top-level operator. */
        String getSymbol() {
            return this.symbol;
        }

        /** Returns the number of arguments of the operator. */
        int getArity() {
            return this.arity;
        }

        /** Returns the priority of the operator. */
        int getPriority() {
            return this.priority;
        }

        /** The symbol for the top-level operator. */
        private final String symbol;
        /** The number of operands of a formula of this kind. */
        private final int arity;
        /** The priority of the top-level operator. */
        private final int priority;
    }
}
