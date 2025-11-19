/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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

package nl.utwente.groove.verify;

import static gov.nasa.ltl.trans.Formula.Always;
import static gov.nasa.ltl.trans.Formula.And;
import static gov.nasa.ltl.trans.Formula.Eventually;
import static gov.nasa.ltl.trans.Formula.False;
import static gov.nasa.ltl.trans.Formula.Next;
import static gov.nasa.ltl.trans.Formula.Not;
import static gov.nasa.ltl.trans.Formula.Or;
import static gov.nasa.ltl.trans.Formula.Proposition;
import static gov.nasa.ltl.trans.Formula.Release;
import static gov.nasa.ltl.trans.Formula.True;
import static gov.nasa.ltl.trans.Formula.Until;
import static gov.nasa.ltl.trans.Formula.WUntil;
import static nl.utwente.groove.verify.LogicOp.ALWAYS;
import static nl.utwente.groove.verify.LogicOp.AND;
import static nl.utwente.groove.verify.LogicOp.EQUIV;
import static nl.utwente.groove.verify.LogicOp.EVENTUALLY;
import static nl.utwente.groove.verify.LogicOp.EXISTS;
import static nl.utwente.groove.verify.LogicOp.FALSE;
import static nl.utwente.groove.verify.LogicOp.FOLLOWS;
import static nl.utwente.groove.verify.LogicOp.FORALL;
import static nl.utwente.groove.verify.LogicOp.IMPLIES;
import static nl.utwente.groove.verify.LogicOp.NEXT;
import static nl.utwente.groove.verify.LogicOp.NOT;
import static nl.utwente.groove.verify.LogicOp.OR;
import static nl.utwente.groove.verify.LogicOp.RELEASE;
import static nl.utwente.groove.verify.LogicOp.S_RELEASE;
import static nl.utwente.groove.verify.LogicOp.TRUE;
import static nl.utwente.groove.verify.LogicOp.UNTIL;
import static nl.utwente.groove.verify.LogicOp.W_UNTIL;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.syntax.Expression;
import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.line.Line;
import nl.utwente.groove.util.parse.ATermTree;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;
import nl.utwente.groove.util.parse.IdValidator;
import nl.utwente.groove.verify.Proposition.Arg;

/**
 * Data structure for temporal formulae.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-02-28 05:58:22 $
 */
public class Formula extends ATermTree<LogicOp,Formula> {
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

    /** Constructor for a unary operator. */
    Formula(LogicOp operator, Formula arg) {
        this(operator, arg, null);
        assert operator.getArity() == 1;
    }

    /** Constructor for an initially empty tree. */
    Formula(LogicOp op) {
        this(op, null, null);
    }

    /** Indicates if this formula is a proposition. */
    public boolean isProp() {
        return getProp() != null;
    }

    /** Returns the optional proposition in this formula. */
    public @Nullable Proposition getProp() {
        return this.prop;
    }

    void setProp(Proposition prop) {
        assert !isFixed();
        this.prop = prop;
    }

    /** The optional proposition in this formula.
     * The field is non-final to allow cloning to work properly.
     */
    private Proposition prop;

    /** Appends a given string buffer with a string description of this formula. */
    private void toString(StringBuilder b) {
        switch (getOp().getArity()) {
        case 0:
            toString0(b);
            break;
        case 1:
            toString1(b);
            break;
        case 2:
            toString2(b);
            break;
        default:
            assert false : String
                .format("Arity %d of operator %s not supported", getOp().getArity(), getOp());
        }
    }

    /**
     * Appends a given string builder with a string description of this nullary formula.
     */
    private void toString2(StringBuilder b) {
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

    /**
     * Appends a given string builder with a string description of this nullary formula.
     */
    private void toString0(StringBuilder b) {
        switch (getOp()) {
        case TRUE, FALSE:
            b.append(getOp().getSymbol());
            break;
        case CALL_PROP, LITERAL_PROP, DERIVED_PROP:
            var prop = getProp();
            assert prop != null;
            b.append(prop.toString());
            break;
        default:
            throw Exceptions.UNREACHABLE;
        }
    }

    /**
     * Appends a given string builder with a string description of this unary formula.
     */
    private void toString1(StringBuilder b) {
        b.append(getOp());
        if (getArg1().getOp().getPriority() < getOp().getPriority()) {
            getArg1().toParString(b);
        } else {
            if (Character.isLetter(getOp().toString().charAt(0))) {
                b.append(' ');
            }
            getArg1().toString(b);
        }
    }

    /**
     * Appends a given string builder with a string description of this formula,
     * surrounded by parentheses.
     */
    private void toParString(StringBuilder b) {
        b.append('(');
        toString(b);
        b.append(')');
    }

    /** Returns the first argument of the top-level operator, if any. */
    public Formula getArg1() {
        return getArgs().size() >= 1
            ? getArg(0)
            : null;
    }

    /** Returns the second argument of the top-level operator, if any. */
    public Formula getArg2() {
        return getArgs().size() >= 2
            ? getArg(1)
            : null;
    }

    /**
     * Indicates if this formula has a logic value set.
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
            this.ctlFormula = computeCtlFormula();
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
        Formula result = switch (getOp()) {
        case CALL_PROP, LITERAL_PROP, DERIVED_PROP, TRUE, FALSE -> this;
        case NOT -> not(getArg1().toCtlFormula());
        case OR, AND, IMPLIES, FOLLOWS, EQUIV -> new Formula(getOp(), getArg1().toCtlFormula(),
            getArg2().toCtlFormula());
        case NEXT, UNTIL, ALWAYS, EVENTUALLY, W_UNTIL, RELEASE, S_RELEASE -> throw new FormatException(
            "Temporal operator '%s' should be nested inside path quantifier in CTL formula",
            getOp());
        case FORALL, EXISTS -> {
            LogicOp subKind = getArg1().getOp();
            Formula subArg1 = getArg1().getArg1();
            Formula subArg2 = getArg1().getArg2();
            yield switch (subKind) {
            case NEXT -> new Formula(getOp(), next(subArg1.toCtlFormula()));
            case ALWAYS -> {
                LogicOp dual = getOp() == EXISTS
                    ? FORALL
                    : EXISTS;
                yield not(new Formula(dual, until(tt(), not(subArg1.toCtlFormula()))));
            }
            case EVENTUALLY -> new Formula(getOp(), until(tt(), subArg1.toCtlFormula()));
            case UNTIL -> new Formula(getOp(),
                until(subArg1.toCtlFormula(), subArg2.toCtlFormula()));
            case W_UNTIL -> new Formula(getOp(),
                wUntil(subArg1.toCtlFormula(), subArg2.toCtlFormula()));
            case RELEASE -> new Formula(getOp(),
                release(subArg1.toCtlFormula(), subArg2.toCtlFormula()));
            case S_RELEASE -> new Formula(getOp(),
                sRelease(subArg1.toCtlFormula(), subArg2.toCtlFormula()));
            default -> throw new FormatException(
                "Path quantifier '%s' must have nested temporal operator in CTL formula", getOp());
            };
        }
        case LPAR, RPAR -> throw Exceptions.UNREACHABLE;
        };
        result.setParseString(getParseString());
        return result;
    }

    /**
     * Converts this formula to a NASA LTL formula, if possible.
     * This succeeds if and only if the formula does not contain
     * {@link LogicOp#FORALL} or {@link LogicOp#EXISTS} operators.
     * @return the NASA LTL formula corresponding to this formula
     * @throws FormatException if this formula contains operators
     * that are illegal in LTL.
     */
    public gov.nasa.ltl.trans.Formula<Proposition> toLtlFormula() throws FormatException {
        getErrors().throwException();
        if (this.ltlFormula == null) {
            if (getLogic() == Logic.CTL) {
                throw new FormatException("CTL formula %s cannot be converted to LTL",
                    getParseString());
            }
            this.logic = Logic.LTL;
            this.ltlFormula = computeLtlFormula();
        }
        return this.ltlFormula;
    }

    /** The LTL formula obtained by converting this formula. */
    private gov.nasa.ltl.trans.Formula<Proposition> ltlFormula;

    /**
     * Converts this formula to a NASA LTL formula, if possible.
     * This succeeds if and only if the formula does not contain
     * {@link LogicOp#FORALL} or {@link LogicOp#EXISTS} operators.
     * @return the NASA LTL formula corresponding to this formula
     * @throws FormatException if this formula contains operators
     * that are illegal in LTL.
     */
    private gov.nasa.ltl.trans.Formula<Proposition> computeLtlFormula() throws FormatException {
        gov.nasa.ltl.trans.Formula<Proposition> arg1 = getArg1() == null
            ? null
            : getArg1().toLtlFormula();
        gov.nasa.ltl.trans.Formula<Proposition> arg2 = getArg2() == null
            ? null
            : getArg2().toLtlFormula();
        return switch (getOp()) {
        case FORALL, EXISTS -> throw new FormatException(
            "Path quantifier '%s' not allowed in LTL formula", getOp());
        case CALL_PROP, LITERAL_PROP, DERIVED_PROP -> Proposition(getProp());
        case TRUE -> True();
        case FALSE -> False();
        case NOT -> Not(arg1);
        case OR -> Or(arg1, arg2);
        case AND -> And(arg1, arg2);
        case NEXT -> Next(arg1);
        case RELEASE -> Release(arg1, arg2);
        case S_RELEASE -> Not(WUntil(Not(arg1), Not(arg2)));
        case UNTIL -> Until(arg1, arg2);
        case W_UNTIL -> WUntil(arg1, arg2);
        case ALWAYS -> Always(arg1);
        case EVENTUALLY -> Eventually(arg1);
        case EQUIV -> and(implies(getArg1(), getArg2()), follows(getArg1(), getArg2()))
            .toLtlFormula();
        case FOLLOWS -> or(getArg1(), not(getArg2())).toLtlFormula();
        case IMPLIES -> or(not(getArg1()), getArg2()).toLtlFormula();
        case LPAR, RPAR -> throw Exceptions.UNREACHABLE;
        };
    }

    @Override
    public Formula createTree(LogicOp op) {
        return new Formula(op);
    }

    @Override
    public Formula clone() {
        Formula result = super.clone();
        result.prop = this.prop;
        result.logic = this.logic;
        return result;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        var prop = getProp();
        if (prop != null) {
            result = prime * result + prop.hashCode();
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof Formula other)) {
            return false;
        }
        return Objects.equals(getProp(), other.getProp());
    }

    @Override
    protected Line toOpLine(boolean addSpaces) {
        Line result;
        if (addSpaces) {
            // spaces are already added; no need to do wnything extra
            result = super.toOpLine(addSpaces);
        } else {
            // for temporal operators, spaces are required to maintain parsability
            int priority = getOp().getPriority();
            switch (getOp().getKind()) {
            case TEMP_PREFIX:
                if (getArg1().getOp().getPriority() <= priority) {
                    // operand has lower priority and hence parentheses are inserted
                    // or operand is another temporal prefix
                    // so no extra spaces are needed
                    result = super.toOpLine(addSpaces);
                } else {
                    result = Line.atom(getOp().getSymbol() + " ");
                }
                break;
            case TEMP_INFIX:
                String left = getArg1().getOp().getPriority() <= priority
                    ? ""
                    : " ";
                String right = getArg2().getOp().getPriority() <= priority
                    ? ""
                    : " ";
                result = Line.atom(left + getOp().getSymbol() + right);
                break;
            default:
                result = super.toOpLine(addSpaces);
            }
        }
        return result;
    }

    @Override
    protected Line toAtomLine(boolean spaces) {
        return switch (getOp()) {
        case TRUE, FALSE -> Line.atom(getOp().getSymbol());
        default -> {
            var prop = getProp();
            assert prop != null;
            yield prop.toLine(spaces);
        }
        };
    }

    /* Atoms without symbols must be propositions. */
    @Override
    protected String toAtomString() {
        var prop = getProp();
        assert prop != null;
        return prop.toString();
    }

    /** Checks if the propositions in the formula are compatible with the given GTS. */
    public void check(GTS gts) throws FormatException {
        computeErrors(gts).throwException();
    }

    /** Checks if the propositions in the formula are compatible with the given GTS,
     * and returns a (possibly empty) set of errors found in this check. */
    private FormatErrorSet computeErrors(GTS gts) {
        FormatErrorSet result = new FormatErrorSet();
        var prop = getProp();
        if (prop == null) {
            switch (getOp().getArity()) {
            case 1:
                result.addAll(getArg1().computeErrors(gts));
                break;
            case 2:
                result.addAll(getArg1().computeErrors(gts));
                result.addAll(getArg2().computeErrors(gts));
                break;
            default: // do nothing
            }
        } else {
            result.addAll(prop.check(gts));
        }
        return result;
    }

    /** Returns the formula for {@code !this}. */
    public Formula neg() {
        return not(this);
    }

    /** Returns the formula for {@code this & arg}. */
    public Formula and(Formula arg) {
        return and(this, arg);
    }

    /** Returns the formula for {@code this | arg}. */
    public Formula or(Formula arg) {
        return or(this, arg);
    }

    /** Returns the formula for {@code this -> arg}. */
    public Formula implies(Formula arg) {
        return implies(this, arg);
    }

    /** Returns the formula for {@code this <- arg}. */
    public Formula follows(Formula arg) {
        return follows(this, arg);
    }

    /** Returns the formula for {@code this <-> arg}. */
    public Formula equiv(Formula arg) {
        return equiv(this, arg);
    }

    /** Returns the formula for {@code this U arg}. */
    public Formula U(Formula arg) {
        return until(this, arg);
    }

    /** Returns the formula for {@code this W arg}. */
    public Formula W(Formula arg) {
        return wUntil(this, arg);
    }

    /** Returns the formula for {@code this R arg}. */
    public Formula R(Formula arg) {
        return release(this, arg);
    }

    /** Returns the formula for {@code this M arg}. */
    public Formula M(Formula arg) {
        return sRelease(this, arg);
    }

    /** Returns the formula for {@code X this}. */
    public Formula X() {
        return next(this);
    }

    /** Returns the formula for {@code G this}. */
    public Formula F() {
        return eventually(this);
    }

    /** Returns the formula for {@code G this}. */
    public Formula G() {
        return always(this);
    }

    /** Returns the formula for {@code A this}. */
    public Formula A() {
        return forall(this);
    }

    /** Returns the formula for {@code E this}. */
    public Formula E() {
        return exists(this);
    }

    /** Returns the formula for {@code AX this}. */
    public Formula AX() {
        return this.X().A();
    }

    /** Returns the formula for {@code EX this}. */
    public Formula EX() {
        return this.X().E();
    }

    /** Returns the formula for {@code AF this}. */
    public Formula AF() {
        return this.F().A();
    }

    /** Returns the formula for {@code EF this}. */
    public Formula EF() {
        return this.F().E();
    }

    /** Returns the formula for {@code AG this}. */
    public Formula AG() {
        return this.G().A();
    }

    /** Returns the formula for {@code EG this}. */
    public Formula EG() {
        return this.G().E();
    }

    /** Returns the formula for {@code A(this U arg)}. */
    public Formula AU(Formula arg) {
        return this.U(arg).A();
    }

    /** Returns the formula for {@code E(this U arg)}. */
    public Formula EU(Formula arg) {
        return this.U(arg).A();
    }

    /** Returns the formula for {@code A(this W arg)}. */
    public Formula AW(Formula arg) {
        return this.W(arg).A();
    }

    /** Returns the formula for {@code E(this W arg)}. */
    public Formula EW(Formula arg) {
        return this.W(arg).E();
    }

    /** Returns the formula for {@code A(this R arg)}. */
    public Formula AR(Formula arg) {
        return this.R(arg).A();
    }

    /** Returns the formula for {@code E(this R arg)}. */
    public Formula ER(Formula arg) {
        return this.R(arg).E();
    }

    /** Returns the formula for {@code A(this M arg)}. */
    public Formula AM(Formula arg) {
        return this.M(arg).A();
    }

    /** Returns the formula for {@code E(this M arg)}. */
    public Formula EM(Formula arg) {
        return this.M(arg).E();
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

    /** Factory method for an atomic proposition testing for a literal string. */
    public static Formula literal(String label) {
        return atom(Proposition.literal(label));
    }

    /** Factory method for an atomic proposition testing for a special flag. */
    public static Formula derived(String name) {
        return atom(Proposition.derived(name));
    }

    /** Factory method for an atomic proposition testing for a rule call (without parameter test). */
    public static Formula call(QualName id) {
        return atom(Proposition.call(id));
    }

    /** Factory method for a propositional formula consisting of a rule call.
     * The arguments are parsed into identifiers or constants.
     * @param args list of arguments: {@link String} or {@link Expression} values to be interpreted
     * as {@link Arg}s
     */
    public static Formula call(QualName id, Object... args) {
        List<Arg> callArgs = new ArrayList<>();
        for (Object arg : args) {
            Arg callArg;
            if (arg instanceof String stringArg) {
                if (isId(stringArg)) {
                    callArg = Arg.arg(stringArg);
                } else if (arg.equals(Arg.WILD_TEXT)) {
                    callArg = Arg.WILD_ARG;
                } else {
                    throw Exceptions.illegalArg("Illegal call argument '%s'", arg);
                }
            } else {
                callArg = Arg.arg((Expression) arg);
            }
            callArgs.add(callArg);
        }
        return atom(Proposition.call(id, callArgs));
    }

    /** Factory method for an atomic formula wrapping a given proposition. */
    public static Formula atom(Proposition prop) {
        Formula result = new Formula(prop.getOp());
        result.setProp(prop);
        result.setFixed();
        return result;
    }

    /** The constant formula for true. */
    public static final Formula tt() {
        Formula result = new Formula(TRUE);
        result.setFixed();
        return result;
    }

    /** The constant formula for false. */
    public static final Formula ff() {
        Formula result = new Formula(FALSE);
        result.setFixed();
        return result;
    }

    /** Creator method for a negation. */
    public static Formula not(Formula f) {
        Formula result = new Formula(NOT, f);
        result.setFixed();
        return result;
    }

    /** Creator method for a conjunction. */
    public static Formula and(Formula f1, Formula f2) {
        Formula result = new Formula(AND, f1, f2);
        result.setFixed();
        return result;
    }

    /** Creator method for a disjunction. */
    public static Formula or(Formula f1, Formula f2) {
        Formula result = new Formula(OR, f1, f2);
        result.setFixed();
        return result;
    }

    /** Creator method for an implication. */
    public static Formula implies(Formula f1, Formula f2) {
        Formula result = new Formula(IMPLIES, f1, f2);
        result.setFixed();
        return result;
    }

    /** Creator method for an inverse implication. */
    public static Formula follows(Formula f1, Formula f2) {
        Formula result = new Formula(FOLLOWS, f1, f2);
        result.setFixed();
        return result;
    }

    /** Creator method for an equivalence. */
    public static Formula equiv(Formula f1, Formula f2) {
        Formula result = new Formula(EQUIV, f1, f2);
        result.setFixed();
        return result;
    }

    /** Creator method for an until formula. */
    public static Formula until(Formula f1, Formula f2) {
        Formula result = new Formula(UNTIL, f1, f2);
        result.setFixed();
        return result;
    }

    /** Creator method for an until formula. */
    public static Formula next(Formula f) {
        Formula result = new Formula(NEXT, f);
        result.setFixed();
        return result;
    }

    /** Creator method for a release formula. */
    public static Formula release(Formula f1, Formula f2) {
        Formula result = new Formula(RELEASE, f1, f2);
        result.setFixed();
        return result;
    }

    /** Creator method for a weak until formula. */
    public static Formula wUntil(Formula f1, Formula f2) {
        Formula result = new Formula(W_UNTIL, f1, f2);
        result.setFixed();
        return result;
    }

    /** Creator method for a strong release formula. */
    public static Formula sRelease(Formula f1, Formula f2) {
        Formula result = new Formula(S_RELEASE, f1, f2);
        result.setFixed();
        return result;
    }

    /** Creator method for an always-true formula. */
    public static Formula always(Formula f) {
        Formula result = new Formula(ALWAYS, f);
        result.setFixed();
        return result;
    }

    /** Creator method for an eventually-true formula. */
    public static Formula eventually(Formula f) {
        Formula result = new Formula(EVENTUALLY, f);
        result.setFixed();
        return result;
    }

    /** Creator method for a for-all-paths formula. */
    public static Formula forall(Formula f) {
        Formula result = new Formula(FORALL, f);
        result.setFixed();
        return result;
    }

    /** Creator method for a for-some-path formula. */
    public static Formula exists(Formula f) {
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
