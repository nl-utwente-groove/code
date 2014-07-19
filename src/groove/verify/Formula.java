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
 * $Id$
 */

package groove.verify;

import static groove.verify.FormulaParser.Token.ALWAYS;
import static groove.verify.FormulaParser.Token.AND;
import static groove.verify.FormulaParser.Token.ATOM;
import static groove.verify.FormulaParser.Token.EQUIV;
import static groove.verify.FormulaParser.Token.EVENTUALLY;
import static groove.verify.FormulaParser.Token.EXISTS;
import static groove.verify.FormulaParser.Token.FALSE;
import static groove.verify.FormulaParser.Token.FOLLOWS;
import static groove.verify.FormulaParser.Token.FORALL;
import static groove.verify.FormulaParser.Token.IMPLIES;
import static groove.verify.FormulaParser.Token.NEXT;
import static groove.verify.FormulaParser.Token.NOT;
import static groove.verify.FormulaParser.Token.OR;
import static groove.verify.FormulaParser.Token.RELEASE;
import static groove.verify.FormulaParser.Token.S_RELEASE;
import static groove.verify.FormulaParser.Token.TRUE;
import static groove.verify.FormulaParser.Token.UNTIL;
import static groove.verify.FormulaParser.Token.W_UNTIL;
import groove.util.parse.StringHandler;
import groove.verify.FormulaParser.Token;

import java.util.Stack;

/**
 * Data structure for temporal formulae.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-02-28 05:58:22 $
 */
public class Formula {
    /** Default constructor filling all fields. */
    Formula(Token kind, Formula arg1, Formula arg2, String prop) {
        this.token = kind;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.prop = prop;
    }

    /** Constructor for a logical constant (not an atom). */
    Formula(Token operator) {
        this(operator, null, null, null);
        assert operator.getArity() == 0 && operator != ATOM;
    }

    /** Constructor for a unary operator. */
    Formula(Token operator, Formula arg) {
        this(operator, arg, null, null);
        assert operator.getArity() == 1;
    }

    /** Constructor for a binary operator. */
    Formula(Token token, Formula arg1, Formula arg2) {
        this(token, arg1, arg2, null);
        assert token.getArity() == 2;
    }

    private Formula(String prop) {
        this(ATOM, null, null, prop);
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
            prime * result + ((this.token == null) ? 0 : this.token.hashCode());
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
        if (this.token != other.token) {
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
        switch (getToken().getArity()) {
        case 0:
            if (getToken() == ATOM) {
                String text = getProp();
                if (isAtom(text)) {
                    b.append(getProp());
                } else {
                    b.append(StringHandler.toQuoted(text, '\''));
                }
            } else {
                b.append(getToken());
            }
            break;
        case 1:
            b.append(getToken());
            if (getArg1().getToken().getPriority() < getToken().getPriority()) {
                getArg1().toParString(b);
            } else {
                if (Character.isLetter(getToken().toString().charAt(0))) {
                    b.append(' ');
                }
                getArg1().toString(b);
            }
            break;
        default:
            assert getToken().getArity() == 2;
            boolean arg1Par =
                getArg1().getToken().getPriority() <= getToken().getPriority();
            boolean arg2Par =
                getArg2().getToken().getPriority() < getToken().getPriority();
            boolean opLetter =
                Character.isLetter(getToken().toString().charAt(0));
            if (arg1Par) {
                getArg1().toParString(b);
            } else {
                getArg1().toString(b);
            }
            if (opLetter && !arg1Par) {
                b.append(' ');
            }
            b.append(getToken());
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
        toString(b);
        b.append(')');
    }

    /** Returns the syntax tree of the formula. */
    public final String toTree() {
        StringBuilder result = new StringBuilder();
        toTree(new Stack<Boolean>(), result);
        result.append('\n');
        return result.toString();
    }

    private final void toTree(Stack<Boolean> indent, StringBuilder result) {
        switch (getToken().getArity()) {
        case 2:
            result.append(getToken() + "+-");
            indent.push(true);
            getArg1().toTree(indent, result);
            result.append('\n');
            addIndent(indent, result);
            indent.pop();
            indent.push(false);
            getArg2().toTree(indent, result);
            indent.pop();
            break;
        case 1:
            result.append(getToken() + "--");
            indent.push(false);
            getArg1().toTree(indent, result);
            indent.pop();
            break;
        case 0:
            if (getToken() == ATOM) {
                result.append(getToken() == ATOM ? getProp() : getToken());
            }
            break;
        }
    }

    private static final void addIndent(Stack<Boolean> indent,
            StringBuilder result) {
        for (int i = 0; i < indent.size(); i++) {
            boolean b = indent.get(i);
            result.append(b ? (i == indent.size() - 1 ? " +--" : " |  ")
                    : "    ");
        }
    }

    /** Returns the top-level operator of the formula. */
    public Token getToken() {
        return this.token;
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
     * Tests if this formula is of the restricted format corresponding
     * to a directly model checkable CTL formula.
     */
    public boolean isCtlFormula() {
        switch (getToken()) {
        case ATOM:
        case TRUE:
        case FALSE:
            return true;
        case NOT:
            return getArg1().isCtlFormula();
        case OR:
        case AND:
        case IMPLIES:
        case FOLLOWS:
        case EQUIV:
            return getArg1().isCtlFormula() && getArg2().isCtlFormula();
        case EXISTS:
        case FORALL:
            Formula arg = getArg1();
            switch (arg.getToken()) {
            case NEXT:
                return arg.getArg1().isCtlFormula();
            case UNTIL:
                return arg.getArg1().isCtlFormula()
                    && arg.getArg2().isCtlFormula();
            default:
                return false;
            }
        default:
            return false;
        }
    }

    /** 
     * Converts this formula to a CTL formula, if possible.
     * This succeeds if and only if all temporal operators in this
     * formula are immediately nested inside a path quantifier, and
     * vice versa.
     * @return the CTL formula corresponding to this formula
     * @throws ParseException if this formula contains combinations of operators
     * that are illegal in CTL.
     */
    public Formula toCtlFormula() throws ParseException {
        switch (getToken()) {
        case ATOM:
        case TRUE:
        case FALSE:
            return this;
        case NOT:
            return Not(getArg1().toCtlFormula());
        case OR:
        case AND:
        case IMPLIES:
        case FOLLOWS:
        case EQUIV:
            return new Formula(getToken(), getArg1().toCtlFormula(),
                getArg2().toCtlFormula());
        case NEXT:
        case UNTIL:
        case ALWAYS:
        case EVENTUALLY:
            throw new ParseException(
                "Temporal operator '%s' should be nested inside path quantifier in CTL formula",
                getToken());
        case W_UNTIL:
        case RELEASE:
        case S_RELEASE:
            throw new ParseException(
                "Temporal operator '%s' not allowed in CTL formula", getToken());
        case FORALL:
        case EXISTS:
            FormulaParser.Token subKind = getArg1().getToken();
            Formula subArg1 = getArg1().getArg1();
            Formula subArg2 = getArg1().getArg2();
            switch (subKind) {
            case NEXT:
                return new Formula(getToken(), Next(subArg1.toCtlFormula()));
            case ALWAYS:
                Token dual = getToken() == EXISTS ? FORALL : EXISTS;
                return Not(new Formula(dual, Until(True(),
                    Not(subArg1.toCtlFormula()))));
            case EVENTUALLY:
                return new Formula(getToken(), Until(True(),
                    subArg1.toCtlFormula()));
            case UNTIL:
                return new Formula(getToken(), Until(subArg1.toCtlFormula(),
                    subArg2.toCtlFormula()));
            case W_UNTIL:
            case RELEASE:
            case S_RELEASE:
                throw new ParseException(
                    "Temporal operator '%s' not allowed in CTL formula",
                    subKind);
            default:
                throw new ParseException(
                    "Path quantifier '%s' must have nested temporal operator in CTL formula",
                    getToken());
            }
        default:
            throw new ParseException("Unknown temporal operator %s", getToken());
        }
    }

    /** 
     * Converts this formula to a NASA LTL formula, if possible.
     * This succeeds if and only if the formula does not contain
     * {@link Token#FORALL} or {@link Token#EXISTS} operators.
     * @return the NASA LTL formula corresponding to this formula
     * @throws ParseException if this formula contains operators
     * that are illegal in LTL.
     */
    public gov.nasa.ltl.trans.Formula<String> toLtlFormula()
        throws ParseException {
        gov.nasa.ltl.trans.Formula<String> arg1 =
            getArg1() == null ? null : getArg1().toLtlFormula();
        gov.nasa.ltl.trans.Formula<String> arg2 =
            getArg2() == null ? null : getArg2().toLtlFormula();
        switch (getToken()) {
        case FORALL:
        case EXISTS:
            throw new ParseException(
                "Path quantifier '%s' not allowed in LTL formula", getToken());
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
            return And(Implies(getArg1(), getArg2()),
                Follows(getArg1(), getArg2())).toLtlFormula();
        case FOLLOWS:
            return Or(getArg1(), Not(getArg2())).toLtlFormula();
        case IMPLIES:
            return Or(Not(getArg1()), getArg2()).toLtlFormula();
        }
        throw new ParseException("Unknown temporal operator %s", getToken());
    }

    private final Token token;
    private final Formula arg1;
    private final Formula arg2;
    private final String prop;

    /** Tests if a given string can be understood as an atom without
     * being quoted.
     * This is the case if the string is a valid identifier.
     * @param text the text to be tested
     * @return {@code true} if {@code text} does not require quotes
     */
    public boolean isAtom(String text) {
        boolean result = text.length() > 0;
        if (result) {
            result = Character.isJavaIdentifierStart(text.charAt(0));
            for (int i = 1; result && i < text.length(); i++) {
                result = Character.isJavaIdentifierPart(text.charAt(i));
            }
        }
        return result;
    }

    /** Factory method for a propositional formula. */
    public static Formula Atom(String prop) {
        return new Formula(prop);
    }

    /** The constant formula for true. */
    public static final Formula True() {
        return new Formula(TRUE);
    }

    /** The constant formula for false. */
    public static final Formula False() {
        return new Formula(FALSE);
    }

    /** Creator method for a negation. */
    public static Formula Not(Formula f) {
        return new Formula(NOT, f);
    }

    /** Creator method for a conjunction. */
    public static Formula And(Formula f1, Formula f2) {
        return new Formula(AND, f1, f2);
    }

    /** Creator method for a disjunction. */
    public static Formula Or(Formula f1, Formula f2) {
        return new Formula(OR, f1, f2);
    }

    /** Creator method for an implication. */
    public static Formula Implies(Formula f1, Formula f2) {
        return new Formula(IMPLIES, f1, f2);
    }

    /** Creator method for an inverse implication. */
    public static Formula Follows(Formula f1, Formula f2) {
        return new Formula(FOLLOWS, f1, f2);
    }

    /** Creator method for an equivalence. */
    public static Formula Equiv(Formula f1, Formula f2) {
        return new Formula(EQUIV, f1, f2);
    }

    /** Creator method for an until formula. */
    public static Formula Until(Formula f1, Formula f2) {
        return new Formula(UNTIL, f1, f2);
    }

    /** Creator method for an until formula. */
    public static Formula Next(Formula f) {
        return new Formula(NEXT, f);
    }

    /** Creator method for a release formula. */
    public static Formula Release(Formula f1, Formula f2) {
        return new Formula(RELEASE, f1, f2);
    }

    /** Creator method for a weak until formula. */
    public static Formula WUntil(Formula f1, Formula f2) {
        return new Formula(W_UNTIL, f1, f2);
    }

    /** Creator method for a strong release formula. */
    public static Formula SRelease(Formula f1, Formula f2) {
        return new Formula(S_RELEASE, f1, f2);
    }

    /** Creator method for an always-true formula. */
    public static Formula Always(Formula f) {
        return new Formula(ALWAYS, f);
    }

    /** Creator method for an eventually-true formula. */
    public static Formula Eventually(Formula f) {
        return new Formula(EVENTUALLY, f);
    }

    /** Creator method for a for-all-paths formula. */
    public static Formula Forall(Formula f) {
        return new Formula(FORALL, f);
    }

    /** Creator method for a for-some-path formula. */
    public static Formula Exists(Formula f) {
        return new Formula(EXISTS, f);
    }
}
