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
 * $Id: RegExprLabel.java,v 1.14 2008-01-30 09:32:28 iovka Exp $
 */
package groove.trans;

import groove.algebra.Operator;
import groove.graph.AbstractLabel;
import groove.graph.EdgeRole;
import groove.graph.TypeGraph;
import groove.graph.TypeGuard;
import groove.graph.TypeLabel;
import groove.io.Util;
import groove.rel.RegAut;
import groove.rel.RegAutCalculator;
import groove.rel.RegExpr;

import java.util.List;

/**
 * Implements a label corresponding to a regular expression.
 * @author Arend Rensink
 * @version $Revision: 2876 $ $Date: 2008-01-30 09:32:28 $
 */
public class RuleLabel extends AbstractLabel {
    /**
     * Constructs a rule label on the basis of a regular
     * expression.
     * @param regExpr the underlying regular expression; may not be
     *        <tt>null</tt>
     */
    public RuleLabel(RegExpr regExpr) {
        if (regExpr == null) {
            throw new IllegalArgumentException(
                "Can't create rule label from null expression");
        }
        //assert !regExpr.isNeg() : "Rule label expressions may not be negated";
        this.regExpr = regExpr;
        this.operator = null;
        this.argNr = INVALID_ARG_NR;
    }

    /**
     * Constructs an atom rule label from a given (host) label.
     * @param label the host label to be turned into 
     * an atom; may not be <tt>null</tt>
     */
    public RuleLabel(TypeLabel label) {
        this(RegExpr.atom(TypeLabel.toPrefixedString(label)));
    }

    /**
     * Constructs an atom label on the basis of a string.
     * @param text the string representation of the
     * underlying regular expression; may not be <tt>null</tt>
     */
    public RuleLabel(String text) {
        this(RegExpr.atom(text));
    }

    /** Constructs a label representing an operation argument number. */
    public RuleLabel(int argNr) {
        this.argNr = argNr;
        this.regExpr = null;
        this.operator = null;
    }

    /** Constructs a label based on a given algebraic operator. */
    public RuleLabel(Operator operator) {
        this.argNr = INVALID_ARG_NR;
        this.regExpr = null;
        this.operator = operator;
    }

    @Override
    public EdgeRole getRole() {
        EdgeRole result = super.getRole();
        if (isWildcard()) {
            result = ((RegExpr.Wildcard) getMatchExpr()).getKind();
        } else if (isSharp() || isAtom()) {
            result = getTypeLabel().getRole();
        }
        return result;
    }

    /**
     * Returns the textual description of the underlying regular expression.
     */
    public String text() {
        String result;
        if (isOperator()) {
            result = getOperator().getName();
        } else if (isArgument()) {
            result = "" + Util.LC_PI + getArgument();
        } else if (isAtom() || isSharp() || isWildcard()) {
            result = getMatchExpr().toString();
            result = EdgeRole.parseLabel(result).two();
        } else {
            result = getMatchExpr().toString();
        }
        return result;
    }

    /**
     * Indicates if this rule label can be matched, i.e.,
     * it is not an argument or operator label.
     * A label is matchable if and only if {@link #getMatchExpr()} returns
     * a value different from {@code null}.
     * @see #isArgument()
     * @see #isOperator()
     */
    public boolean isMatchable() {
        return this.regExpr != null;
    }

    /** Returns the underlying regular expression. */
    public RegExpr getMatchExpr() {
        return this.regExpr;
    }

    /**
     * Returns the regular automaton for this label., given a store
     * of existing labels. It is required that all the regular expression
     * labels occur in the label store.
     * @param typeGraph alphabet of the automaton,
     * used to match node type labels properly; non-{@code null}
     */
    public RegAut getAutomaton(TypeGraph typeGraph) {
        if (isMatchable() && this.automaton == null
            || this.automaton.getTypeGraph() != typeGraph) {
            this.automaton = calculator.compute(getMatchExpr(), typeGraph);
        }
        return this.automaton;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof RuleLabel) {
            RuleLabel other = (RuleLabel) obj;
            if (isArgument()) {
                result =
                    other.isArgument() && getArgument() == other.getArgument();
            } else if (isOperator()) {
                result =
                    other.isOperator()
                        && getOperator().equals(other.getOperator());
            } else {
                assert isMatchable();
                result = getMatchExpr().equals(other.getMatchExpr());
            }
        }
        return result;
    }

    /** Tests this label wraps a {@link groove.rel.RegExpr.Atom}. */
    public boolean isAtom() {
        return getAtomText() != null;
    }

    /**
     * If this label wraps a {@link groove.rel.RegExpr.Atom}, returns the
     * text of the atom. Returns <code>null</code> otherwise.
     */
    public String getAtomText() {
        RegExpr expr = getMatchExpr();
        return expr instanceof RegExpr.Atom ? ((RegExpr.Atom) expr).text()
                : null;
    }

    /**
     * If this label wraps a
     * {@link groove.rel.RegExpr.Atom} or a {@link groove.rel.RegExpr.Sharp}, 
     * returns the default label corresponding
     * to the atom or sharp text. Returns
     * <code>null</code> otherwise.
     */
    public TypeLabel getTypeLabel() {
        RegExpr expr = getMatchExpr();
        if (expr instanceof RegExpr.Atom) {
            return ((RegExpr.Atom) expr).toTypeLabel();
        } else if (expr instanceof RegExpr.Sharp) {
            return ((RegExpr.Sharp) expr).getSharpLabel();
        } else {
            return null;
        }
    }

    /** Tests if this label wraps a {@link groove.rel.RegExpr.Empty}. */
    public boolean isEmpty() {
        return getMatchExpr() instanceof RegExpr.Empty;
    }

    /** Tests if this label wraps a {@link groove.rel.RegExpr.Sharp}. */
    public boolean isSharp() {
        RegExpr regExpr = getMatchExpr();
        return regExpr == null ? false : regExpr.isSharp();
    }

    /**
     * If this label wraps a
     * {@link groove.rel.RegExpr.Sharp}, returns the sharp type label.
     * Returns {@code null} otherwise.
     */
    public TypeLabel getSharpLabel() {
        RegExpr regExpr = getMatchExpr();
        return regExpr == null ? null : regExpr.getSharpLabel();
    }

    /** Tests if this label wraps a {@link groove.rel.RegExpr.Wildcard}. */
    public boolean isWildcard() {
        RegExpr regExpr = getMatchExpr();
        return regExpr == null ? false : regExpr.isWildcard();
    }

    /**
     * If this label wraps a
     * {@link groove.rel.RegExpr.Wildcard}, returns the guard of the wildcard.
     * Returns <code>null</code> in all other cases.
     */
    public TypeGuard getWildcardGuard() {
        RegExpr regExpr = getMatchExpr();
        return regExpr == null ? null : regExpr.getWildcardGuard();
    }

    /** Tests if this label wraps a {@link groove.rel.RegExpr.Choice}. */
    public boolean isChoice() {
        return getChoiceOperands() != null;
    }

    /**
     * If this label wraps a
     * {@link groove.rel.RegExpr.Choice}, returns the list of operands of the regular
     * expression. Returns <code>null</code> otherwise.
     */
    public List<RegExpr> getChoiceOperands() {
        RegExpr expr = getMatchExpr();
        if (expr instanceof RegExpr.Choice) {
            return ((RegExpr.Choice) expr).getOperands();
        }
        return null;
    }

    /** Tests if this label wraps a {@link groove.rel.RegExpr.Seq}. */
    public boolean isSeq() {
        return getSeqOperands() != null;
    }

    /**
     * If this label wraps a {@link groove.rel.RegExpr.Seq},
     * returns the list of operands of the regular expression. Returns
     * <code>null</code> in all other cases.
     */
    public List<RegExpr> getSeqOperands() {
        RegExpr expr = getMatchExpr();
        if (expr instanceof RegExpr.Seq) {
            return ((RegExpr.Seq) expr).getOperands();
        }
        return null;
    }

    /** Tests if this label wraps a {@link groove.rel.RegExpr.Star}. */
    public boolean isStar() {
        return getStarOperand() != null;
    }

    /**
     * If this label wraps a
     * {@link groove.rel.RegExpr.Star}, returns the operand of the regular expression.
     * Returns <code>null</code> otherwise.
     */
    public RegExpr getStarOperand() {
        RegExpr expr = getMatchExpr();
        if (expr instanceof RegExpr.Star) {
            return ((RegExpr.Star) expr).getOperand();
        }
        return null;
    }

    /** Tests if this label wraps a {@link groove.rel.RegExpr.Plus}. */
    public boolean isPlus() {
        return getPlusOperand() != null;
    }

    /**
     * If this label wraps a
     * {@link groove.rel.RegExpr.Plus}, returns the operand of the regular expression.
     * Returns <code>null</code> otherwise.
     */
    public RegExpr getPlusOperand() {
        RegExpr expr = getMatchExpr();
        if (expr instanceof RegExpr.Plus) {
            return ((RegExpr.Plus) expr).getOperand();
        }
        return null;
    }

    /** Tests if this label wraps a {@link groove.rel.RegExpr.Inv}. */
    public boolean isInv() {
        return getInvLabel() != null;
    }

    /**
     * If this label wraps a {@link groove.rel.RegExpr.Inv},
     * returns the operand label. Returns
     * <code>null</code> otherwise.
     */
    public RuleLabel getInvLabel() {
        RuleLabel result = null;
        RegExpr expr = getMatchExpr();
        if (expr instanceof RegExpr.Inv) {
            result = ((RegExpr.Inv) expr).getOperand().toLabel();
        }
        return result;
    }

    /** Tests if this label wraps a {@link groove.rel.RegExpr.Neg}. */
    public boolean isNeg() {
        return getNegOperand() != null;
    }

    /**
     * If  this label wraps a {@link groove.rel.RegExpr.Neg},
     * returns the operand of the regular expression. Returns
     * <code>null</code> in all other cases.
     */
    public RegExpr getNegOperand() {
        RegExpr expr = getMatchExpr();
        if (expr instanceof RegExpr.Neg) {
            return ((RegExpr.Neg) expr).getOperand();
        }
        return null;
    }

    /** Indicates whether this label wraps an algebraic operator. */
    public boolean isOperator() {
        return this.operator != null;
    }

    /** Returns the operator wrapped in this label, if any. */
    public Operator getOperator() {
        return this.operator;
    }

    /** Indicates whether this label wraps an argument number. */
    public boolean isArgument() {
        return this.argNr != INVALID_ARG_NR;
    }

    /** Returns the argument number wrapped in this label, if any. */
    public int getArgument() {
        return this.argNr;
    }

    /** The underlying regular expression, if any. */
    private final RegExpr regExpr;
    /** An automaton constructed lazily for the regular expression. */
    private RegAut automaton;
    /** The wrapped operator, if any. */
    private final Operator operator;
    /** The argument number wrapped by this label, if any. */
    private final int argNr;

    /** Calculator used to construct all the automata. */
    static private final RegAutCalculator calculator = new RegAutCalculator();
    /** Number used for labels that are not argument labels. */
    public static final int INVALID_ARG_NR = -1;
}