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

import static groove.verify.LogicOp.ALWAYS;
import static groove.verify.LogicOp.AND;
import static groove.verify.LogicOp.EQUIV;
import static groove.verify.LogicOp.EVENTUALLY;
import static groove.verify.LogicOp.EXISTS;
import static groove.verify.LogicOp.FALSE;
import static groove.verify.LogicOp.FOLLOWS;
import static groove.verify.LogicOp.FORALL;
import static groove.verify.LogicOp.IMPLIES;
import static groove.verify.LogicOp.NEXT;
import static groove.verify.LogicOp.NOT;
import static groove.verify.LogicOp.OR;
import static groove.verify.LogicOp.PROP;
import static groove.verify.LogicOp.RELEASE;
import static groove.verify.LogicOp.S_RELEASE;
import static groove.verify.LogicOp.TRUE;
import static groove.verify.LogicOp.UNTIL;
import static groove.verify.LogicOp.W_UNTIL;
import groove.algebra.Constant;
import groove.algebra.Sort;
import groove.util.line.Line;
import groove.util.parse.FormatException;
import groove.util.parse.Id;
import groove.util.parse.IdValidator;
import groove.util.parse.TermTree;

/**
 * Data structure for temporal formulae.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-02-28 05:58:22 $
 */
public class Formula extends TermTree<LogicOp,Formula> {
    /** Default constructor filling all fields. */
    Formula(LogicOp op, Formula arg1, Formula arg2) {
        super(op);
        if (arg1 != null) {
            assert op.getArity() >= 1;
            addArg(arg1);
        }
        if (arg2 != null) {
            assert op.getArity() >= 2;
            addArg(arg2);
        }
    }

    /** Constructor for a logical constant (not an atom). */
    Formula(LogicOp op) {
        this(op, null, null);
    }

    /** Constructor for a unary operator. */
    Formula(LogicOp operator, Formula arg) {
        this(operator, arg, null);
        assert operator.getArity() == 1;
    }

    //    @Override
    //    public String toString() {
    //        StringBuffer result = new StringBuffer();
    //        toString(result);
    //        return result.toString();
    //    }

    /** Appends a given string buffer with a string description of this formula. */
    private void toString(StringBuffer b) {
        switch (getOp().getArity()) {
        case 0:
            if (getOp() == PROP) {
                if (hasId()) {
                    b.append(getId().getName());
                } else {
                    b.append(getConstant().getSymbol());
                }
            } else {
                b.append(getOp());
            }
            break;
        case 1:
            b.append(getOp());
            if (getArg1().getOp().getPriority() < getOp().getPriority()) {
                getArg1().toParString(b);
            } else {
                if (Character.isLetter(getOp().toString().charAt(0))) {
                    b.append(' ');
                }
                getArg1().toString(b);
            }
            break;
        default:
            boolean arg1Par = getArg1().getOp().getPriority() <= getOp().getPriority();
            boolean arg2Par = getArg2().getOp().getPriority() < getOp().getPriority();
            boolean opLetter = Character.isLetter(getOp().toString().charAt(0));
            if (arg1Par) {
                getArg1().toParString(b);
            } else {
                getArg1().toString(b);
            }
            if (opLetter && !arg1Par) {
                b.append(' ');
            }
            b.append(getOp());
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

    /** Returns the first argument of the top-level operator, if any. */
    public Formula getArg1() {
        return getArgs().size() >= 1 ? getArg(0) : null;
    }

    /** Returns the second argument of the top-level operator, if any. */
    public Formula getArg2() {
        return getArgs().size() >= 2 ? getArg(1) : null;
    }

    @Override
    public void setConstant(Constant constant) {
        if (getOp() == PROP && constant.getSort() == Sort.STRING && isId(constant.getStringRepr())) {
            super.setId(new Id(constant.getStringRepr()));
        } else {
            super.setConstant(constant);
        }
    }

    @Override
    protected Line getOpLine(boolean addSpaces) {
        Line result = null;
        if (!addSpaces) {
            switch (getOp()) {
            case ALWAYS:
            case EVENTUALLY:
            case EXISTS:
            case FORALL:
            case NEXT:
                result = Line.atom(getOp().getSymbol() + " ");
                break;
            case RELEASE:
            case S_RELEASE:
            case UNTIL:
            case W_UNTIL:
                result = Line.atom(" " + getOp().getSymbol() + " ");
            }
        }
        if (result == null) {
            result = super.getOpLine(addSpaces);
        }
        return result;
    }

    /** Returns the proposition wrapped in this formula, if any. */
    public String getProp() {
        if (hasConstant()) {
            assert getConstant().getSort() == Sort.STRING;
            return getConstant().getStringRepr();
        } else if (hasId()) {
            return getId().getName();
        } else {
            return "";
        }
    }

    /** 
     * Returns the particular logic of this formula, if any.
     * Convenience method for {@code getLogic() != null}.
     * @see #getLogic()
     */
    public boolean hasLogic() {
        return getLogic() != null;
    }

    /** Returns the logic of this formula,
     * if either {@link #toLtlFormula()} or {@link #toCtlFormula()} has been invoked.
     * @return {@link Logic#LTL} if {@link #toLtlFormula()} has been invoked,
     * or {@link Logic#CTL} if {@link #toCtlFormula()} has been invoked, or {@code null}
     * otherwise.
     */
    public Logic getLogic() {
        return this.logic;
    }

    private Logic logic;

    /** 
     * Tests if this formula is of the restricted format corresponding
     * to a directly model checkable CTL formula.
     */
    public boolean isCtlFormula() {
        boolean result = true;
        try {
            toCtlFormula();
        } catch (FormatException exc) {
            result = false;
        }
        return result;
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
        getErrors().throwException();
        if (this.ctlFormula == null) {
            if (getLogic() == Logic.LTL) {
                throw new FormatException("LTL formula %s cannot be converted to CTL",
                    getParseString());
            }
            this.logic = Logic.CTL;
            try {
                this.ctlFormula = computeCtlFormula();
            } catch (FormatException exc) {
                addErrors(exc);
                throw exc;
            }
        }
        return this.ctlFormula;
    }

    /** The CTL formula obtained by converting this formula. */
    private Formula ctlFormula;

    /** 
     * Converts this formula to a CTL formula, if possible.
     * This succeeds if and only if all temporal operators in this
     * formula are immediately nested inside a path quantifier, and
     * vice versa.
     * @return the CTL formula corresponding to this formula
     * @throws FormatException if this formula contains combinations of operators
     * that are illegal in CTL.
     */
    private Formula computeCtlFormula() throws FormatException {
        switch (getOp()) {
        case PROP:
        case TRUE:
        case FALSE:
            return this;
        case CALL:
            throw new FormatException("Rule call of '%s' not yet supported in CTL",
                getId().getName());
        case ARG:
            // this is called recursively but can be ignored
            return null;
        case NOT:
            return Not(getArg1().toCtlFormula());
        case OR:
        case AND:
        case IMPLIES:
        case FOLLOWS:
        case EQUIV:
            return new Formula(getOp(), getArg1().toCtlFormula(), getArg2().toCtlFormula());
        case NEXT:
        case UNTIL:
        case ALWAYS:
        case EVENTUALLY:
            throw new FormatException(
                "Temporal operator '%s' should be nested inside path quantifier in CTL formula",
                getOp());
        case W_UNTIL:
        case RELEASE:
        case S_RELEASE:
            throw new FormatException("Temporal operator '%s' not allowed in CTL formula", getOp());
        case FORALL:
        case EXISTS:
            LogicOp subKind = getArg1().getOp();
            Formula subArg1 = getArg1().getArg1();
            Formula subArg2 = getArg1().getArg2();
            switch (subKind) {
            case NEXT:
                return new Formula(getOp(), Next(subArg1.toCtlFormula()));
            case ALWAYS:
                LogicOp dual = getOp() == EXISTS ? FORALL : EXISTS;
                return Not(new Formula(dual, Until(True(), Not(subArg1.toCtlFormula()))));
            case EVENTUALLY:
                return new Formula(getOp(), Until(True(), subArg1.toCtlFormula()));
            case UNTIL:
                return new Formula(getOp(), Until(subArg1.toCtlFormula(), subArg2.toCtlFormula()));
            case W_UNTIL:
            case RELEASE:
            case S_RELEASE:
                throw new FormatException("Temporal operator '%s' not allowed in CTL formula",
                    subKind);
            default:
                throw new FormatException(
                    "Path quantifier '%s' must have nested temporal operator in CTL formula",
                    getOp());
            }
        default:
            throw new FormatException("Unknown temporal operator %s", getOp());
        }
    }

    /** 
     * Converts this formula to a NASA LTL formula, if possible.
     * This succeeds if and only if the formula does not contain
     * {@link LogicOp#FORALL} or {@link LogicOp#EXISTS} operators.
     * @return the NASA LTL formula corresponding to this formula
     * @throws FormatException if this formula contains operators
     * that are illegal in LTL.
     */
    public gov.nasa.ltl.trans.Formula<String> toLtlFormula() throws FormatException {
        getErrors().throwException();
        if (this.ltlFormula == null) {
            if (getLogic() == Logic.CTL) {
                throw new FormatException("CTL formula %s cannot be converted to LTL",
                    getParseString());
            }
            this.logic = Logic.LTL;
            try {
                this.ltlFormula = computeLtlFormula();
            } catch (FormatException exc) {
                addErrors(exc);
                throw exc;
            }
        }
        return this.ltlFormula;
    }

    /** The LTL formula obtained by converting this formula. */
    private gov.nasa.ltl.trans.Formula<String> ltlFormula;

    /** 
     * Converts this formula to a NASA LTL formula, if possible.
     * This succeeds if and only if the formula does not contain
     * {@link LogicOp#FORALL} or {@link LogicOp#EXISTS} operators.
     * @return the NASA LTL formula corresponding to this formula
     * @throws FormatException if this formula contains operators
     * that are illegal in LTL.
     */
    private gov.nasa.ltl.trans.Formula<String> computeLtlFormula() throws FormatException {
        gov.nasa.ltl.trans.Formula<String> arg1 =
            getArg1() == null ? null : getArg1().toLtlFormula();
        gov.nasa.ltl.trans.Formula<String> arg2 =
            getArg2() == null ? null : getArg2().toLtlFormula();
        switch (getOp()) {
        case FORALL:
        case EXISTS:
            throw new FormatException("Path quantifier '%s' not allowed in LTL formula", getOp());
        case PROP:
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
            return And(Implies(getArg1(), getArg2()), Follows(getArg1(), getArg2())).toLtlFormula();
        case FOLLOWS:
            return Or(getArg1(), Not(getArg2())).toLtlFormula();
        case IMPLIES:
            return Or(Not(getArg1()), getArg2()).toLtlFormula();
        case CALL:
            throw new FormatException("Rule call of '%s' not yet supported in LTL",
                getId().getName());
        case ARG:
            // this is called recursively but can be ignored
            return null;
        }
        throw new FormatException("Unknown temporal operator %s", getOp());
    }

    @Override
    public Formula createTree(LogicOp op) {
        return new Formula(op);
    }

    /** Tests if a given string can be understood as an atom without
     * being quoted.
     * This is the case if the string is a valid identifier.
     * @param text the text to be tested
     * @return {@code true} if {@code text} does not require quotes
     */
    static public boolean isId(String text) {
        return IdValidator.JAVA_ID.isValid(text);
    }

    /** Factory method for an atomic formula testing for a string constant. */
    public static Formula Prop(String prop) {
        Formula result = new Formula(PROP);
        result.setConstant(Constant.instance(prop));
        result.setFixed();
        return result;
    }

    /** Factory method for an atomic formula testing for an identifier. */
    public static Formula Prop(Id id) {
        Formula result = new Formula(PROP);
        result.setId(id);
        result.setFixed();
        return result;
    }

    /** Factory method for a propositional formula consisting of a rule call. */
    public static Formula Call(Id id, String... args) {
        Formula result = new Formula(LogicOp.CALL);
        result.setId(id);
        for (String arg : args) {
            Formula f = new Formula(LogicOp.ARG);
            if (isId(arg)) {
                f.setId(Id.id(arg));
            } else {
                f.setConstant(Constant.instance(arg));
            }
            result.addArg(f);
        }
        result.setFixed();
        return result;
    }

    /** The constant formula for true. */
    public static final Formula True() {
        Formula result = new Formula(TRUE);
        result.setFixed();
        return result;
    }

    /** The constant formula for false. */
    public static final Formula False() {
        Formula result = new Formula(FALSE);
        result.setFixed();
        return result;
    }

    /** Creator method for a negation. */
    public static Formula Not(Formula f) {
        Formula result = new Formula(NOT, f);
        result.setFixed();
        return result;
    }

    /** Creator method for a conjunction. */
    public static Formula And(Formula f1, Formula f2) {
        Formula result = new Formula(AND, f1, f2);
        result.setFixed();
        return result;
    }

    /** Creator method for a disjunction. */
    public static Formula Or(Formula f1, Formula f2) {
        Formula result = new Formula(OR, f1, f2);
        result.setFixed();
        return result;
    }

    /** Creator method for an implication. */
    public static Formula Implies(Formula f1, Formula f2) {
        Formula result = new Formula(IMPLIES, f1, f2);
        result.setFixed();
        return result;
    }

    /** Creator method for an inverse implication. */
    public static Formula Follows(Formula f1, Formula f2) {
        Formula result = new Formula(FOLLOWS, f1, f2);
        result.setFixed();
        return result;
    }

    /** Creator method for an equivalence. */
    public static Formula Equiv(Formula f1, Formula f2) {
        Formula result = new Formula(EQUIV, f1, f2);
        result.setFixed();
        return result;
    }

    /** Creator method for an until formula. */
    public static Formula Until(Formula f1, Formula f2) {
        Formula result = new Formula(UNTIL, f1, f2);
        result.setFixed();
        return result;
    }

    /** Creator method for an until formula. */
    public static Formula Next(Formula f) {
        Formula result = new Formula(NEXT, f);
        result.setFixed();
        return result;
    }

    /** Creator method for a release formula. */
    public static Formula Release(Formula f1, Formula f2) {
        Formula result = new Formula(RELEASE, f1, f2);
        result.setFixed();
        return result;
    }

    /** Creator method for a weak until formula. */
    public static Formula WUntil(Formula f1, Formula f2) {
        Formula result = new Formula(W_UNTIL, f1, f2);
        result.setFixed();
        return result;
    }

    /** Creator method for a strong release formula. */
    public static Formula SRelease(Formula f1, Formula f2) {
        Formula result = new Formula(S_RELEASE, f1, f2);
        result.setFixed();
        return result;
    }

    /** Creator method for an always-true formula. */
    public static Formula Always(Formula f) {
        Formula result = new Formula(ALWAYS, f);
        result.setFixed();
        return result;
    }

    /** Creator method for an eventually-true formula. */
    public static Formula Eventually(Formula f) {
        Formula result = new Formula(EVENTUALLY, f);
        result.setFixed();
        return result;
    }

    /** Creator method for a for-all-paths formula. */
    public static Formula Forall(Formula f) {
        Formula result = new Formula(FORALL, f);
        result.setFixed();
        return result;
    }

    /** Creator method for a for-some-path formula. */
    public static Formula Exists(Formula f) {
        Formula result = new Formula(EXISTS, f);
        result.setFixed();
        return result;
    }

    /** Parses a given input string into a formula.
     * @param input The (non-{@code null}) input string
     * @return the resulting formula
     * @throws FormatException if there were parse errors
     */
    public static Formula parse(String input) throws FormatException {
        Formula result = FormulaParser.instance().parse(input);
        result.getErrors().throwException();
        return result;
    }

    /** Parses a given input string into a formula of a given logic.
     * @param logic the logic according to which the formula should be parsed
     * @param input The (non-{@code null}) input string
     * @return the resulting formula
     * @throws FormatException if there were parse errors
     */
    public static Formula parse(Logic logic, String input) throws FormatException {
        Formula result = FormulaParser.instance(logic).parse(input);
        result.getErrors().throwException();
        return result;
    }
}
