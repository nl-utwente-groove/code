// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/* 
 * $Id: RegExprLabel.java,v 1.1.1.1 2007-03-20 10:05:24 kastenberg Exp $
 */
package groove.rel;

import java.util.List;

import groove.graph.AbstractLabel;
import groove.graph.GraphFormatException;
import groove.graph.Label;
import groove.util.ExprFormatException;

/**
 * Implements a label corresponding to a regular expression.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.1 $ $Date: 2007-03-20 10:05:24 $
 */
public class RegExprLabel extends AbstractLabel {
    /**
     * Attempts to construct a label by interpreting a given string as a regular expression.
     * @param text the string to be parsed
     * @exception GraphFormatException if <tt>text</tt> is not a correctly formatted
     * regular expression text
     * @see RegExpr#parse(String)
     * @require <tt>text != null</tt>
     * @deprecated parsing string to get labels is not reliable
     */
	@Deprecated
    public static RegExprLabel parseLabel(String text) throws GraphFormatException {
        try {
            return new RegExprLabel(RegExpr.parse(text));
        } catch (ExprFormatException exc) {
            throw new GraphFormatException(exc.getMessage());
        }
    }
    
    /** Tests if a given label is a {@link RegExprLabel} wrapping a {@link RegExpr.Atom}. */
    public static boolean isAtom(Label label) {
        return getAtomText(label) != null;
    }
    
    /** 
     * If a given label is a {@link RegExprLabel} wrapping a {@link RegExpr.Atom},
     * returns the text of the atom.
     * Returns <code>null</code> in all other cases.
     */
    public static String getAtomText(Label label) {
        if (label instanceof RegExprLabel) {
            RegExpr expr = ((RegExprLabel) label).getRegExpr();
            if (expr instanceof RegExpr.Atom) {
                return ((RegExpr.Atom) expr).text();
            }
        }
        return null;
    }
    
    /** Tests if a given label is a {@link RegExprLabel} wrapping a {@link RegExpr.Empty}. */
    public static boolean isEmpty(Label label) {
        return (label instanceof RegExprLabel) && ((RegExprLabel) label).getRegExpr() instanceof RegExpr.Empty;
    }
    
    /** Tests if a given label is a {@link RegExprLabel} wrapping a {@link RegExpr.Wildcard}. */
    public static boolean isWildcard(Label label) {
        return (label instanceof RegExprLabel) && ((RegExprLabel) label).getRegExpr() instanceof RegExpr.Wildcard;
    }
    
    /** 
     * If a given label is a {@link RegExprLabel} wrapping a {@link RegExpr.Wildcard},
     * returns the identifier of the wildcard, if any.
     * Returns <code>null</code> in all other cases.
     */
    public static String getWildcardId(Label label) {
        if (label instanceof RegExprLabel) {
            RegExpr expr = ((RegExprLabel) label).getRegExpr();
            if (expr instanceof RegExpr.Wildcard) {
                return ((RegExpr.Wildcard) expr).getIdentifier();
            }
        }
        return null;
    }
    
    /** Tests if a given label is a {@link RegExprLabel} wrapping a {@link RegExpr.Choice}. */
    public static boolean isChoice(Label label) {
        return getChoiceOperands(label) != null;
    }
    
    /** 
     * If a given label is a {@link RegExprLabel} wrapping a {@link RegExpr.Choice},
     * returns the list of operands of the regular expression.
     * Returns <code>null</code> in all other cases.
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
    
    /** Tests if a given label is a {@link RegExprLabel} wrapping a {@link RegExpr.Seq}. */
    public static boolean isSeq(Label label) {
        return getSeqOperands(label) != null;
    }
    
    /** 
     * If a given label is a {@link RegExprLabel} wrapping a {@link RegExpr.Seq},
     * returns the list of operands of the regular expression.
     * Returns <code>null</code> in all other cases.
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
    
    /** Tests if a given label is a {@link RegExprLabel} wrapping a {@link RegExpr.Star}. */
    public static boolean isStar(Label label) {
        return getStarOperand(label) != null;
    }
    
    /** 
     * If a given label is a {@link RegExprLabel} wrapping a {@link RegExpr.Star},
     * returns the operand of the regular expression.
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
    
    /** Tests if a given label is a {@link RegExprLabel} wrapping a {@link RegExpr.Plus}. */
    public static boolean isPlus(Label label) {
        return getPlusOperand(label) != null;
    }
    
    /** 
     * If a given label is a {@link RegExprLabel} wrapping a {@link RegExpr.Plus},
     * returns the operand of the regular expression.
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
    
    /** Tests if a given label is a {@link RegExprLabel} wrapping a {@link RegExpr.Inv}. */
    public static boolean isInv(Label label) {
        return getInvOperand(label) != null;
    }
    
    /** 
     * If a given label is a {@link RegExprLabel} wrapping a {@link RegExpr.Inv},
     * returns the operand of the regular expression.
     * Returns <code>null</code> in all other cases.
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
    
    /** Tests if a given label is a {@link RegExprLabel} wrapping a {@link RegExpr.Neg}. */
    public static boolean isNeg(Label label) {
        return getNegOperand(label) != null;
    }
    
    /** 
     * If a given label is a {@link RegExprLabel} wrapping a {@link RegExpr.Neg},
     * returns the operand of the regular expression.
     * Returns <code>null</code> in all other cases.
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

    /** 
     * The static calculator instance used for calculating the 
     * label automaton.
     * @see #getAutomaton()
     */
    static private final AutomatonCalculator calculator = new AutomatonCalculator();
    
    /**
     * Constructs a regular expression label on the basis of a regular expression.
     * @param regExpr the underlying regular expression; may not be <tt>null</tt>
     */
    public RegExprLabel(RegExpr regExpr) {
        if (regExpr == null) {
            throw new IllegalArgumentException("Can't create regular expression label from null expression");
        }
        this.regExpr = regExpr;
    }

    /**
     * Factory method: returns a label corresponding to a given string.
     */
    @Deprecated
    public Label parse(String text) throws GraphFormatException {
        return parseLabel(text);
    }

    /**
     * Returns the textual description of the underlying regular expression.
     */
    public String text() {
        return regExpr.toString();
    }
    
    /**
     * Returns the underlying regular expression.
     */
    public RegExpr getRegExpr() {
        return regExpr;
    }
    
    /** Returns the regular automaton for this label. */
    public Automaton getAutomaton() {
        if (automaton == null) {
            automaton = calculator.compute(getRegExpr());
        }
        return automaton;
    }
//    
//    /**
//     * Compares the textual descriptions.
//     */
//    public int compareTo(Object obj) {
//        return text().compareTo(obj.toString());
//    }
//
//    /**
//     * Returns the hash code of the regular expression.
//     */
//    public int hashCode() {
//        return regExpr.hashCode();
//    }
//
//    public boolean equals(Object obj) {
//        return (obj instanceof RegExprLabel) && regExpr.equals(((RegExprLabel) obj).regExpr);
//    }
//
//    /**
//     * Returns the textual description of the label.
//     */
//    public String toString() {
//        return regExpr.toString();
//    }

    /** The underlying regular expression. */
    protected final RegExpr regExpr;
    /** An automaton constructed lazily for the regular expression. */
    private Automaton automaton;
}