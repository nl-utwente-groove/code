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
package groove.grammar.rule;

import java.util.List;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;

import groove.automaton.RegAut;
import groove.automaton.RegAutCalculator;
import groove.automaton.RegExpr;
import groove.grammar.type.TypeGraph;
import groove.grammar.type.TypeGuard;
import groove.grammar.type.TypeLabel;
import groove.graph.ALabel;
import groove.graph.EdgeRole;
import groove.graph.Label;
import groove.util.line.Line;

/**
 * Implements a label corresponding to a regular expression.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-01-30 09:32:28 $
 */
public class RuleLabel extends ALabel {
    /**
     * Constructs a rule label on the basis of a regular
     * expression.
     * @param regExpr the underlying regular expression; may not be
     *        <tt>null</tt>
     */
    public RuleLabel(RegExpr regExpr) {
        if (regExpr == null) {
            throw new IllegalArgumentException("Can't create rule label from null expression");
        }
        //assert !regExpr.isNeg() : "Rule label expressions may not be negated";
        this.regExpr = regExpr;
    }

    /**
     * Constructs an atom rule label from a given (host) label.
     * @param label the host label to be turned into
     * an atom; may not be <tt>null</tt>
     */
    public RuleLabel(TypeLabel label) {
        this(RegExpr.atom(label.toParsableString()));
    }

    /**
     * Constructs an atom label on the basis of a string.
     * @param text the string representation of the
     * underlying regular expression; may not be <tt>null</tt>
     */
    public RuleLabel(String text) {
        this(RegExpr.atom(text));
    }

    @Override
    public int compareTo(Label obj) {
        int result = getRole().compareTo(obj.getRole());
        if (result == 0 && obj instanceof RuleLabel) {
            RuleLabel other = (RuleLabel) obj;
            if (isAtom() != other.isAtom()) {
                result = isAtom() ? -1 : +1;
            }
        }
        if (result == 0) {
            result = text().compareTo(obj.text());
        }
        return result;
    }

    @Override
    public EdgeRole getRole() {
        EdgeRole result = super.getRole();
        if (isWildcard()) {
            result = ((RegExpr.Wildcard) getMatchExpr()).getKind();
        } else if (isSharp() || isAtom()) {
            result = getTypeLabel().getRole();
        } else if (isEmpty()) {
            result = EdgeRole.BINARY;
        } else if (isNeg() && getNegOperand().isEmpty()) {
            result = EdgeRole.BINARY;
        } else if (getMatchExpr().isBinary()) {
            result = EdgeRole.BINARY;
        } else {
            result = EdgeRole.FLAG;
        }
        return result;
    }

    /**
     * Returns the textual description of the underlying regular expression.
     */
    @Override
    protected Line computeLine() {
        return getMatchExpr().toLine();
    }

    /** Returns the underlying regular expression. */
    public @NonNull RegExpr getMatchExpr() {
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
        if (this.automaton == null || this.automaton.getTypeGraph() != typeGraph) {
            this.automaton = calculator.compute(getMatchExpr(), typeGraph);
        }
        return this.automaton;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof RuleLabel) {
            RuleLabel other = (RuleLabel) obj;
            result = getMatchExpr().equals(other.getMatchExpr());
        }
        return result;
    }

    /** Tests this label wraps a {@link groove.automaton.RegExpr.Atom}. */
    public boolean isAtom() {
        return getAtomText() != null;
    }

    /**
     * If this label wraps a {@link groove.automaton.RegExpr.Atom}, returns the
     * text of the atom. Returns <code>null</code> otherwise.
     */
    public String getAtomText() {
        RegExpr expr = getMatchExpr();
        return expr instanceof RegExpr.Atom ? ((RegExpr.Atom) expr).text() : null;
    }

    /**
     * If this label wraps a
     * {@link groove.automaton.RegExpr.Atom} or a {@link groove.automaton.RegExpr.Sharp},
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

    /** Tests if this label wraps a {@link groove.automaton.RegExpr.Empty}. */
    public boolean isEmpty() {
        return getMatchExpr() instanceof RegExpr.Empty;
    }

    /** Tests if this label wraps a {@link groove.automaton.RegExpr.Sharp}. */
    public boolean isSharp() {
        return getMatchExpr().isSharp();
    }

    /**
     * If this label wraps a
     * {@link groove.automaton.RegExpr.Sharp}, returns the sharp type label.
     * Returns {@code null} otherwise.
     */
    public TypeLabel getSharpLabel() {
        return getMatchExpr().getSharpLabel();
    }

    /** Tests if this label wraps a {@link groove.automaton.RegExpr.Wildcard}. */
    public boolean isWildcard() {
        return getMatchExpr().isWildcard();
    }

    /**
     * If this label wraps a
     * {@link groove.automaton.RegExpr.Wildcard}, returns the guard of the wildcard.
     * Returns <code>null</code> in all other cases.
     */
    public TypeGuard getWildcardGuard() {
        return getMatchExpr().getWildcardGuard();
    }

    /** Tests if this label wraps a {@link groove.automaton.RegExpr.Choice}. */
    public boolean isChoice() {
        return getChoiceOperands() != null;
    }

    /**
     * If this label wraps a
     * {@link groove.automaton.RegExpr.Choice}, returns the list of operands of the regular
     * expression. Returns <code>null</code> otherwise.
     */
    public List<RegExpr> getChoiceOperands() {
        RegExpr expr = getMatchExpr();
        if (expr instanceof RegExpr.Choice) {
            return ((RegExpr.Choice) expr).getOperands();
        }
        return null;
    }

    /** Tests if this label wraps a {@link groove.automaton.RegExpr.Seq}. */
    public boolean isSeq() {
        return getSeqOperands() != null;
    }

    /**
     * If this label wraps a {@link groove.automaton.RegExpr.Seq},
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

    /** Tests if this label wraps a {@link groove.automaton.RegExpr.Star}. */
    public boolean isStar() {
        return getStarOperand() != null;
    }

    /**
     * If this label wraps a
     * {@link groove.automaton.RegExpr.Star}, returns the operand of the regular expression.
     * Returns <code>null</code> otherwise.
     */
    public RegExpr getStarOperand() {
        RegExpr expr = getMatchExpr();
        if (expr instanceof RegExpr.Star) {
            return ((RegExpr.Star) expr).getOperand();
        }
        return null;
    }

    /** Tests if this label wraps a {@link groove.automaton.RegExpr.Plus}. */
    public boolean isPlus() {
        return getPlusOperand() != null;
    }

    /**
     * If this label wraps a
     * {@link groove.automaton.RegExpr.Plus}, returns the operand of the regular expression.
     * Returns <code>null</code> otherwise.
     */
    public RegExpr getPlusOperand() {
        RegExpr expr = getMatchExpr();
        if (expr instanceof RegExpr.Plus) {
            return ((RegExpr.Plus) expr).getOperand();
        }
        return null;
    }

    /** Tests if this label wraps a {@link groove.automaton.RegExpr.Inv}. */
    public boolean isInv() {
        return getInvLabel() != null;
    }

    /**
     * If this label wraps a {@link groove.automaton.RegExpr.Inv},
     * returns the operand label. Returns
     * <code>null</code> otherwise.
     */
    public RuleLabel getInvLabel() {
        RuleLabel result = null;
        RegExpr expr = getMatchExpr();
        if (expr instanceof RegExpr.Inv) {
            result = ((RegExpr.Inv) expr).getOperand()
                .toLabel();
        }
        return result;
    }

    /** Tests if this label wraps a {@link groove.automaton.RegExpr.Neg}. */
    public boolean isNeg() {
        return getNegOperand() != null;
    }

    /**
     * If  this label wraps a {@link groove.automaton.RegExpr.Neg},
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

    /** Returns the set of label variables occurring in this label. */
    public Set<LabelVar> allVarSet() {
        return getMatchExpr().allVarSet();
    }

    /** The underlying regular expression, if any. */
    private final @NonNull RegExpr regExpr;
    /** An automaton constructed lazily for the regular expression. */
    private RegAut automaton;
    /** Calculator used to construct all the automata. */
    static private final RegAutCalculator calculator = new RegAutCalculator();
    /** Number used for labels that are not argument labels. */
    public static final int INVALID_ARG_NR = -1;
}