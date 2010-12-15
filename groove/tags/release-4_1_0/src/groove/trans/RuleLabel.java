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

import groove.algebra.Operation;
import groove.graph.AbstractLabel;
import groove.graph.LabelStore;
import groove.graph.TypeLabel;
import groove.rel.Automaton;
import groove.rel.AutomatonCalculator;
import groove.rel.LabelVar;
import groove.rel.RegExpr;
import groove.util.Groove;
import groove.util.Property;

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
    public RuleLabel(Operation operator) {
        this.argNr = INVALID_ARG_NR;
        this.regExpr = null;
        this.operator = operator;
    }

    @Override
    public int getKind() {
        int result = super.getKind();
        RegExpr expr = getRegExpr();
        if (isWildcard()) {
            result = ((RegExpr.Wildcard) expr).getKind();
        } else if (isSharp() || isAtom()) {
            result = getTypeLabel().getKind();
        }
        return result;
    }

    /**
     * Returns the textual description of the underlying regular expression.
     */
    public String text() {
        String result;
        if (isOperator()) {
            result = getOperator().getSymbol();
        } else if (isArgument()) {
            result = "" + Groove.LC_PI + getArgument();
        } else {
            result = getRegExpr().toString();
            // if the label is not binary, it means the regular expression
            // is preceded with a kind prefix that we have to strip off
            // in order to get the label text
            if (!isBinary()) {
                result =
                    result.substring(TypeLabel.getPrefix(getKind()).length());
            }
        }
        return result;
    }

    /**
     * Returns the underlying regular expression.
     */
    public RegExpr getRegExpr() {
        return this.regExpr;
    }

    /**
     * Returns the regular automaton for this label., given a store
     * of existing labels. It is required that all the regular expression
     * labels occur in the label store.
     * @param labelStore alphabet of the automaton,
     * used to match node type labels properly; non-{@code null}
     */
    public Automaton getAutomaton(LabelStore labelStore) {
        if (getRegExpr() != null && this.automaton == null
            || this.automaton.getLabelStore() != labelStore) {
            this.automaton = calculator.compute(getRegExpr(), labelStore);
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
                result = getRegExpr().equals(other.getRegExpr());
            }
        }
        return result;
    }

    /**
     * Tests this label wraps a
     * {@link RegExpr.Atom}.
     */
    public boolean isAtom() {
        return getAtomText() != null;
    }

    /**
     * If this label wraps a
     * {@link RegExpr.Atom}, returns the text of the atom. Returns
     * <code>null</code> otherwise.
     */
    public String getAtomText() {
        RegExpr expr = getRegExpr();
        return expr instanceof RegExpr.Atom ? ((RegExpr.Atom) expr).text()
                : null;
    }

    /**
     * If this label wraps a
     * {@link RegExpr.Atom} or a {@link RegExpr.Sharp}, 
     * returns the default label corresponding
     * to the atom or sharp text. Returns
     * <code>null</code> otherwise.
     */
    public TypeLabel getTypeLabel() {
        RegExpr expr = getRegExpr();
        if (expr instanceof RegExpr.Atom) {
            return ((RegExpr.Atom) expr).toTypeLabel();
        } else if (expr instanceof RegExpr.Sharp) {
            return ((RegExpr.Sharp) expr).getSharpLabel();
        } else {
            return null;
        }
    }

    /**
     * Tests if this label wraps a
     * {@link RegExpr.Empty}.
     */
    public boolean isEmpty() {
        return getRegExpr() instanceof RegExpr.Empty;
    }

    /**
     * Tests if this label wraps a
     * {@link RegExpr.Sharp}.
     */
    public boolean isSharp() {
        RegExpr regExpr = getRegExpr();
        return regExpr == null ? false : regExpr.isSharp();
    }

    /**
     * If this label wraps a
     * {@link RegExpr.Sharp}, returns the sharp type label.
     * Returns {@code null} otherwise.
     */
    public TypeLabel getSharpLabel() {
        RegExpr regExpr = getRegExpr();
        return regExpr == null ? null : regExpr.getSharpLabel();
    }

    /**
     * Tests if this label wraps a
     * {@link RegExpr.Wildcard}.
     */
    public boolean isWildcard() {
        RegExpr regExpr = getRegExpr();
        return regExpr == null ? false : regExpr.isWildcard();
    }

    /**
     * Returns the identifier of the expression wildcard, if any.
     * Returns <code>null</code> otherwise.
     */
    public LabelVar getWildcardId() {
        RegExpr regExpr = getRegExpr();
        return regExpr == null ? null : regExpr.getWildcardId();
    }

    /**
     * If this label wraps a
     * {@link RegExpr.Wildcard}, returns the kind of label the wildcard
     * matches against.
     * Returns {@code -1} otherwise.
     */
    public int getWildcardKind() {
        RegExpr regExpr = getRegExpr();
        return regExpr == null ? null : regExpr.getWildcardKind();
    }

    /**
     * If this label wraps a
     * {@link RegExpr.Wildcard}, returns the constraint of the wildcard, if any.
     * Returns <code>null</code> in all other cases.
     */
    public Property<TypeLabel> getWildcardGuard() {
        RegExpr regExpr = getRegExpr();
        return regExpr == null ? null : regExpr.getWildcardGuard();
    }

    /**
     * Tests if this label wraps a
     * {@link RegExpr.Choice}.
     */
    public boolean isChoice() {
        return getChoiceOperands() != null;
    }

    /**
     * If this label wraps a
     * {@link RegExpr.Choice}, returns the list of operands of the regular
     * expression. Returns <code>null</code> otherwise.
     */
    public List<RegExpr> getChoiceOperands() {
        RegExpr expr = getRegExpr();
        if (expr instanceof RegExpr.Choice) {
            return ((RegExpr.Choice) expr).getOperands();
        }
        return null;
    }

    /**
     * Tests if this label wraps a
     * {@link RegExpr.Seq}.
     */
    public boolean isSeq() {
        return getSeqOperands() != null;
    }

    /**
     * If this label wraps a {@link RegExpr.Seq},
     * returns the list of operands of the regular expression. Returns
     * <code>null</code> in all other cases.
     */
    public List<RegExpr> getSeqOperands() {
        RegExpr expr = getRegExpr();
        if (expr instanceof RegExpr.Seq) {
            return ((RegExpr.Seq) expr).getOperands();
        }
        return null;
    }

    /**
     * Tests if this label wraps a
     * {@link RegExpr.Star}.
     */
    public boolean isStar() {
        return getStarOperand() != null;
    }

    /**
     * If this label wraps a
     * {@link RegExpr.Star}, returns the operand of the regular expression.
     * Returns <code>null</code> otherwise.
     */
    public RegExpr getStarOperand() {
        RegExpr expr = getRegExpr();
        if (expr instanceof RegExpr.Star) {
            return ((RegExpr.Star) expr).getOperand();
        }
        return null;
    }

    /**
     * Tests if whis label wraps a
     * {@link RegExpr.Plus}.
     */
    public boolean isPlus() {
        return getPlusOperand() != null;
    }

    /**
     * If this label wraps a
     * {@link RegExpr.Plus}, returns the operand of the regular expression.
     * Returns <code>null</code> otherwise.
     */
    public RegExpr getPlusOperand() {
        RegExpr expr = getRegExpr();
        if (expr instanceof RegExpr.Plus) {
            return ((RegExpr.Plus) expr).getOperand();
        }
        return null;
    }

    /**
     * Tests if this label wraps a
     * {@link RegExpr.Inv}.
     */
    public boolean isInv() {
        return getInvLabel() != null;
    }

    /**
     * If this label wraps a {@link RegExpr.Inv},
     * returns the operand label. Returns
     * <code>null</code> otherwise.
     */
    public RuleLabel getInvLabel() {
        RuleLabel result = null;
        RegExpr expr = getRegExpr();
        if (expr instanceof RegExpr.Inv) {
            result = ((RegExpr.Inv) expr).getOperand().toLabel();
        }
        return result;
    }

    /**
     * Tests if this label wraps a
     * {@link RegExpr.Neg}.
     */
    public boolean isNeg() {
        return getNegOperand() != null;
    }

    /**
     * If  this label wraps a {@link RegExpr.Neg},
     * returns the operand of the regular expression. Returns
     * <code>null</code> in all other cases.
     */
    public RegExpr getNegOperand() {
        RegExpr expr = getRegExpr();
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
    public Operation getOperator() {
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
    private Automaton automaton;
    /** The wrapped operator, if any. */
    private final Operation operator;
    /** The argument number wrapped by this label, if any. */
    private final int argNr;

    /** Calculator used to construct all the automata. */
    static private final AutomatonCalculator calculator =
        new AutomatonCalculator();
    /** Number used for labels that are not argument labels. */
    public static final int INVALID_ARG_NR = -1;
}