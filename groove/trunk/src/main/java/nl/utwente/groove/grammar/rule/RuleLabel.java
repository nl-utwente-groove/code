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
package nl.utwente.groove.grammar.rule;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.automaton.RegAut;
import nl.utwente.groove.automaton.RegAutCalculator;
import nl.utwente.groove.automaton.RegExpr;
import nl.utwente.groove.automaton.RegExpr.Neg;
import nl.utwente.groove.automaton.RegExpr.Wildcard;
import nl.utwente.groove.grammar.type.TypeGraph;
import nl.utwente.groove.grammar.type.TypeGuard;
import nl.utwente.groove.grammar.type.TypeLabel;
import nl.utwente.groove.graph.ALabel;
import nl.utwente.groove.graph.EdgeRole;
import nl.utwente.groove.graph.Label;
import nl.utwente.groove.util.line.Line;

/**
 * Implements a label corresponding to a regular expression.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-01-30 09:32:28 $
 */
@NonNullByDefault
public class RuleLabel extends ALabel {
    /**
     * Constructs a rule label on the basis of a regular
     * expression.
     * @param regExpr the underlying regular expression; may not be
     *        <tt>null</tt>
     */
    public RuleLabel(RegExpr regExpr) {
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
        if (result == 0 && obj instanceof RuleLabel other) {
            if (isAtom() != other.isAtom()) {
                result = isAtom()
                    ? -1
                    : +1;
            }
        }
        if (result == 0) {
            result = text().compareTo(obj.text());
        }
        return result;
    }

    @SuppressWarnings("null")
    @Override
    public EdgeRole getRole() {
        EdgeRole result = super.getRole();
        if (isWildcard()) {
            result = ((RegExpr.Wildcard) getMatchExpr()).getKind();
        } else if (isSharp() || isAtom()) {
            var tl = getTypeLabel();
            assert tl != null;
            result = tl.getRole();
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
        var result = this.automaton;
        if (result == null || result.getTypeGraph() != typeGraph) {
            this.automaton = result = calculator.compute(getMatchExpr(), typeGraph);
        }
        return result;
    }

    /** An automaton constructed lazily for the regular expression. */
    private @Nullable RegAut automaton;

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;
        if (obj instanceof RuleLabel other) {
            result = getMatchExpr().equals(other.getMatchExpr());
        }
        return result;
    }

    /** Tests this label wraps a {@link nl.utwente.groove.automaton.RegExpr.Atom}. */
    public boolean isAtom() {
        return getAtomText() != null;
    }

    /**
     * If this label wraps a {@link nl.utwente.groove.automaton.RegExpr.Atom}, returns the
     * text of the atom. Returns <code>null</code> otherwise.
     */
    public @Nullable String getAtomText() {
        RegExpr expr = getMatchExpr();
        return expr instanceof RegExpr.Atom atom
            ? atom.text()
            : null;
    }

    /**
     * If this label wraps a
     * {@link nl.utwente.groove.automaton.RegExpr.Atom} or a {@link nl.utwente.groove.automaton.RegExpr.Sharp},
     * returns the default label corresponding
     * to the atom or sharp text. Returns
     * <code>null</code> otherwise.
     */
    public @Nullable TypeLabel getTypeLabel() {
        RegExpr expr = getMatchExpr();
        if (expr instanceof RegExpr.Atom) {
            return ((RegExpr.Atom) expr).toTypeLabel();
        } else if (expr instanceof RegExpr.Sharp) {
            return ((RegExpr.Sharp) expr).getSharpLabel();
        } else {
            return null;
        }
    }

    /** Tests if this label wraps a {@link nl.utwente.groove.automaton.RegExpr.Empty}. */
    public boolean isEmpty() {
        return getMatchExpr() instanceof RegExpr.Empty;
    }

    /** Tests if this label wraps a {@link nl.utwente.groove.automaton.RegExpr.Sharp}. */
    public boolean isSharp() {
        return getMatchExpr().isSharp();
    }

    /**
     * If this label wraps a
     * {@link nl.utwente.groove.automaton.RegExpr.Sharp}, returns the sharp type label.
     * Returns {@code null} otherwise.
     */
    public TypeLabel getSharpLabel() {
        return getMatchExpr().getSharpLabel();
    }

    /** Tests if this label wraps a {@link Wildcard}. */
    public boolean isWildcard() {
        return getMatchExpr().isWildcard();
    }

    /** Tests if this label wraps a {@link Wildcard} with a given property. */
    public boolean isWildcard(Predicate<Wildcard> prop) {
        return getMatchExpr().isWildcard() && prop.test((Wildcard) getMatchExpr());
    }

    /**
     * If this label wraps a
     * {@link nl.utwente.groove.automaton.RegExpr.Wildcard}, returns the guard of the wildcard.
     * Returns <code>null</code> in all other cases.
     */
    public @Nullable TypeGuard getWildcardGuard() {
        return getMatchExpr().getWildcardGuard();
    }

    /** Tests if this label wraps a {@link nl.utwente.groove.automaton.RegExpr.Choice}. */
    public boolean isChoice() {
        return getChoiceOperands() != null;
    }

    /**
     * If this label wraps a
     * {@link nl.utwente.groove.automaton.RegExpr.Choice}, returns the list of operands of the regular
     * expression. Returns <code>null</code> otherwise.
     */
    public @Nullable List<RegExpr> getChoiceOperands() {
        RegExpr expr = getMatchExpr();
        if (expr instanceof RegExpr.Choice choice) {
            return choice.getOperands();
        }
        return null;
    }

    /** Tests if this label wraps a {@link nl.utwente.groove.automaton.RegExpr.Seq}. */
    public boolean isSeq() {
        return getSeqOperands() != null;
    }

    /**
     * If this label wraps a {@link nl.utwente.groove.automaton.RegExpr.Seq},
     * returns the list of operands of the regular expression. Returns
     * <code>null</code> in all other cases.
     */
    public @Nullable List<RegExpr> getSeqOperands() {
        RegExpr expr = getMatchExpr();
        if (expr instanceof RegExpr.Seq seq) {
            return seq.getOperands();
        }
        return null;
    }

    /** Tests if this label wraps a {@link nl.utwente.groove.automaton.RegExpr.Star}. */
    public boolean isStar() {
        return getStarOperand() != null;
    }

    /**
     * If this label wraps a
     * {@link nl.utwente.groove.automaton.RegExpr.Star}, returns the operand of the regular expression.
     * Returns <code>null</code> otherwise.
     */
    public @Nullable RegExpr getStarOperand() {
        RegExpr expr = getMatchExpr();
        if (expr instanceof RegExpr.Star star) {
            return star.getOperand();
        }
        return null;
    }

    /** Tests if this label wraps a {@link nl.utwente.groove.automaton.RegExpr.Plus}. */
    public boolean isPlus() {
        return getPlusOperand() != null;
    }

    /**
     * If this label wraps a
     * {@link nl.utwente.groove.automaton.RegExpr.Plus}, returns the operand of the regular expression.
     * Returns <code>null</code> otherwise.
     */
    public @Nullable RegExpr getPlusOperand() {
        RegExpr expr = getMatchExpr();
        if (expr instanceof RegExpr.Plus plus) {
            return plus.getOperand();
        }
        return null;
    }

    /** Tests if this label wraps a {@link nl.utwente.groove.automaton.RegExpr.Inv}. */
    public boolean isInv() {
        return getInvLabel() != null;
    }

    /**
     * If this label wraps a {@link nl.utwente.groove.automaton.RegExpr.Inv},
     * returns the operand label. Returns
     * <code>null</code> otherwise.
     */
    public @Nullable RuleLabel getInvLabel() {
        RuleLabel result = null;
        RegExpr expr = getMatchExpr();
        if (expr instanceof RegExpr.Inv inv) {
            result = inv.getOperand().toLabel();
        }
        return result;
    }

    /** Tests if this label wraps a {@link Neg}. */
    public boolean isNeg() {
        return getNegOperand() != null;
    }

    /** Tests if this label wraps a {@link Neg} satisfying a given property. */
    public boolean isNeg(Predicate<Neg> prop) {
        if (getMatchExpr() instanceof RegExpr.Neg neg) {
            return prop.test(neg);
        } else {
            return false;
        }
    }

    /**
     * If  this label wraps a {@link Neg},
     * returns the operand of the regular expression. Returns
     * <code>null</code> in all other cases.
     */
    public @Nullable RegExpr getNegOperand() {
        if (getMatchExpr() instanceof RegExpr.Neg neg) {
            return neg.getOperand();
        }
        return null;
    }

    /** Returns the set of label variables occurring in this label. */
    public Set<LabelVar> allVarSet() {
        return getMatchExpr().allVarSet();
    }

    /** The underlying regular expression, if any. */
    private final RegExpr regExpr;
    /** Calculator used to construct all the automata. */
    static private final RegAutCalculator calculator = new RegAutCalculator();
    /** Number used for labels that are not argument labels. */
    public static final int INVALID_ARG_NR = -1;
}