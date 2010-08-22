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
package groove.rel;

import groove.graph.AbstractLabel;
import groove.graph.DefaultLabel;
import groove.graph.Label;
import groove.graph.LabelStore;
import groove.util.Property;

import java.util.List;

/**
 * Implements a label corresponding to a regular expression.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-01-30 09:32:28 $
 */
public class RegExprLabel extends AbstractLabel {
    /**
     * Constructs a regular expression label on the basis of a regular
     * expression. Local constructor; should be called only from
     * {@link RegExpr#toLabel()}.
     * @param regExpr the underlying regular expression; may not be
     *        <tt>null</tt>
     */
    RegExprLabel(RegExpr regExpr) {
        if (regExpr == null) {
            throw new IllegalArgumentException(
                "Can't create regular expression label from null expression");
        }
        this.regExpr = regExpr;
    }

    @Override
    public int getKind() {
        if (getRegExpr().isWildcard()) {
            return ((RegExpr.Wildcard) getRegExpr()).getKind();
        } else if (getRegExpr().isSharp()) {
            return getRegExpr().getSharpLabel().getKind();
        } else {
            return super.getKind();
        }
    }

    /**
     * Returns the textual description of the underlying regular expression.
     */
    public String text() {
        String result = this.regExpr.toString();
        // if the label is not binary, it means the regular expression
        // is preceded with a kind prefix that we have to strip off
        // in order to get the label text
        if (!isBinary()) {
            result =
                result.substring(DefaultLabel.getPrefix(getKind()).length());
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
     * Returns the regular automaton for this label. 
     * @param labelStore alphabet of the automaton,
     * used to match node type labels properly
     */
    public Automaton getAutomaton(LabelStore labelStore) {
        if (this.automaton == null) {
            calculator.setLabelStore(labelStore);
            this.automaton = calculator.compute(getRegExpr());
        }
        return this.automaton;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RegExprLabel) {
            return getRegExpr().equals(((RegExprLabel) obj).getRegExpr());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return getRegExpr().hashCode();
    }

    /** The underlying regular expression. */
    private final RegExpr regExpr;
    /** An automaton constructed lazily for the regular expression. */
    private Automaton automaton;

    /**
     * Returns the regular expression on the label if it is a
     * {@link RegExprLabel}, <code>null</code> otherwise.
     */
    public static RegExpr getRegExpr(Label label) {
        return label instanceof RegExprLabel
                ? ((RegExprLabel) label).getRegExpr() : null;
    }

    /**
     * Tests if a given label is a {@link RegExprLabel} wrapping a
     * {@link RegExpr.Atom}.
     */
    public static boolean isAtom(Label label) {
        return getAtomText(label) != null;
    }

    /**
     * If a given label is a {@link RegExprLabel} wrapping a
     * {@link RegExpr.Atom}, returns the text of the atom. Returns
     * <code>null</code> in all other cases.
     */
    public static String getAtomText(Label label) {
        RegExpr expr = getRegExpr(label);
        return expr instanceof RegExpr.Atom ? ((RegExpr.Atom) expr).text()
                : null;
    }

    /**
     * Tests if a given label is a {@link RegExprLabel} wrapping a
     * {@link RegExpr.Empty}.
     */
    public static boolean isEmpty(Label label) {
        return (label instanceof RegExprLabel)
            && ((RegExprLabel) label).getRegExpr() instanceof RegExpr.Empty;
    }

    /**
     * Tests if a given label is a {@link RegExprLabel} wrapping a
     * {@link RegExpr.Sharp}.
     */
    public static boolean isSharp(Label label) {
        return (label instanceof RegExprLabel)
            && ((RegExprLabel) label).getRegExpr().isSharp();
    }

    /**
     * If a given label is a {@link RegExprLabel} wrapping a
     * {@link RegExpr.Sharp}, returns the sharp type label.
     * Returns {@code null} in all other cases.
     */
    public static Label getSharpLabel(Label label) {
        if (label instanceof RegExprLabel) {
            return ((RegExprLabel) label).getRegExpr().getSharpLabel();
        }
        return null;
    }

    /**
     * Tests if a given label is a {@link RegExprLabel} wrapping a
     * {@link RegExpr.Wildcard}.
     */
    public static boolean isWildcard(Label label) {
        return (label instanceof RegExprLabel)
            && ((RegExprLabel) label).getRegExpr().isWildcard();
    }

    /**
     * If a given label is a {@link RegExprLabel} wrapping a
     * {@link RegExpr.Wildcard}, returns the identifier of the wildcard, if any.
     * Returns <code>null</code> in all other cases.
     */
    public static LabelVar getWildcardId(Label label) {
        if (label instanceof RegExprLabel) {
            return ((RegExprLabel) label).getRegExpr().getWildcardId();
        }
        return null;
    }

    /**
     * If a given label is a {@link RegExprLabel} wrapping a
     * {@link RegExpr.Wildcard}, returns the kind of label the wildcard
     * matches against.
     * Returns {@code -1} in all other cases.
     */
    public static int getWildcardKind(Label label) {
        if (label instanceof RegExprLabel) {
            return ((RegExprLabel) label).getRegExpr().getWildcardKind();
        }
        return -1;
    }

    /**
     * If a given label is a {@link RegExprLabel} wrapping a
     * {@link RegExpr.Wildcard}, returns the constraint of the wildcard, if any.
     * Returns <code>null</code> in all other cases.
     */
    public static Property<Label> getWildcardGuard(Label label) {
        if (label instanceof RegExprLabel) {
            return ((RegExprLabel) label).getRegExpr().getWildcardGuard();
        }
        return null;
    }

    /**
     * Tests if a given label is a {@link RegExprLabel} wrapping a
     * {@link RegExpr.Choice}.
     */
    public static boolean isChoice(Label label) {
        return getChoiceOperands(label) != null;
    }

    /**
     * If a given label is a {@link RegExprLabel} wrapping a
     * {@link RegExpr.Choice}, returns the list of operands of the regular
     * expression. Returns <code>null</code> in all other cases.
     */
    public static List<RegExpr> getChoiceOperands(Label label) {
        if (label instanceof RegExprLabel) {
            RegExpr expr = ((RegExprLabel) label).getRegExpr();
            if (expr instanceof RegExpr.Choice) {
                return ((RegExpr.Choice) expr).getOperands();
            }
        }
        return null;
    }

    /**
     * Tests if a given label is a {@link RegExprLabel} wrapping a
     * {@link RegExpr.Seq}.
     */
    public static boolean isSeq(Label label) {
        return getSeqOperands(label) != null;
    }

    /**
     * If a given label is a {@link RegExprLabel} wrapping a {@link RegExpr.Seq}
     * , returns the list of operands of the regular expression. Returns
     * <code>null</code> in all other cases.
     */
    public static List<RegExpr> getSeqOperands(Label label) {
        if (label instanceof RegExprLabel) {
            RegExpr expr = ((RegExprLabel) label).getRegExpr();
            if (expr instanceof RegExpr.Seq) {
                return ((RegExpr.Seq) expr).getOperands();
            }
        }
        return null;
    }

    /**
     * Tests if a given label is a {@link RegExprLabel} wrapping a
     * {@link RegExpr.Star}.
     */
    public static boolean isStar(Label label) {
        return getStarOperand(label) != null;
    }

    /**
     * If a given label is a {@link RegExprLabel} wrapping a
     * {@link RegExpr.Star}, returns the operand of the regular expression.
     * Returns <code>null</code> in all other cases.
     */
    public static RegExpr getStarOperand(Label label) {
        if (label instanceof RegExprLabel) {
            RegExpr expr = ((RegExprLabel) label).getRegExpr();
            if (expr instanceof RegExpr.Star) {
                return ((RegExpr.Star) expr).getOperand();
            }
        }
        return null;
    }

    /**
     * Tests if a given label is a {@link RegExprLabel} wrapping a
     * {@link RegExpr.Plus}.
     */
    public static boolean isPlus(Label label) {
        return getPlusOperand(label) != null;
    }

    /**
     * If a given label is a {@link RegExprLabel} wrapping a
     * {@link RegExpr.Plus}, returns the operand of the regular expression.
     * Returns <code>null</code> in all other cases.
     */
    public static RegExpr getPlusOperand(Label label) {
        if (label instanceof RegExprLabel) {
            RegExpr expr = ((RegExprLabel) label).getRegExpr();
            if (expr instanceof RegExpr.Plus) {
                return ((RegExpr.Plus) expr).getOperand();
            }
        }
        return null;
    }

    /**
     * Tests if a given label is a {@link RegExprLabel} wrapping a
     * {@link RegExpr.Inv}.
     */
    public static boolean isInv(Label label) {
        return getInvOperand(label) != null;
    }

    /**
     * If a given label is a {@link RegExprLabel} wrapping a {@link RegExpr.Inv}
     * , returns the operand of the regular expression. Returns
     * <code>null</code> in all other cases.
     */
    public static RegExpr getInvOperand(Label label) {
        if (label instanceof RegExprLabel) {
            RegExpr expr = ((RegExprLabel) label).getRegExpr();
            if (expr instanceof RegExpr.Inv) {
                return ((RegExpr.Inv) expr).getOperand();
            }
        }
        return null;
    }

    /**
     * Tests if a given label is a {@link RegExprLabel} wrapping a
     * {@link RegExpr.Neg}.
     */
    public static boolean isNeg(Label label) {
        return getNegOperand(label) != null;
    }

    /**
     * If a given label is a {@link RegExprLabel} wrapping a {@link RegExpr.Neg}
     * , returns the operand of the regular expression. Returns
     * <code>null</code> in all other cases.
     */
    public static RegExpr getNegOperand(Label label) {
        if (label instanceof RegExprLabel) {
            RegExpr expr = ((RegExprLabel) label).getRegExpr();
            if (expr instanceof RegExpr.Neg) {
                return ((RegExpr.Neg) expr).getOperand();
            }
        }
        return null;
    }

    /** Calculator used to construct all the automata. */
    static private final AutomatonCalculator calculator =
        new AutomatonCalculator();
}