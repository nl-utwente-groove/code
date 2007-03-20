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
 * $Id: CTLExpr.java,v 1.1.1.2 2007-03-20 10:43:00 kastenberg Exp $
 */
package groove.verify;

import groove.graph.Node;
import groove.lts.GTS;
import groove.lts.GraphTransition;
import groove.lts.State;
import groove.trans.GraphCondition;
import groove.util.ExprFormatException;
import groove.util.ExprParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
/**
 * @author Harmen Kastenberg
 * @version $Revision: 1.1.1.2 $ $Date: 2007-03-20 10:43:00 $
 * 
 * Class description.
 */
abstract public class CTLExpr {
    /** 
     * Negation operator.
     */
    public static final char NEGATION_OPERATOR = '!';
    /**
     * Symbolic name for negation operator.
     */
    public static final String NEGATION_SYMBOLIC_NAME = "Not";
    /**
     * And operator.
     */
    public static final char AND_OPERATOR = '&';
    /**
     * Symbolic name for and operator.
     */
    public static final String AND_SYMBOLIC_NAME = "And";
    /**
     * Or operator.
     */
    public static final char OR_OPERATOR = '|';
    /**
     * Symbolic name for or operator.
     */
    public static final String OR_SYMBOLIC_NAME = "Or";
    /**
     * Next operator.
     */
    public static final char NEXT_OPERATOR = 'X';
    /**
     * Symbolic name for next operator.
     */
    public static final String NEXT_SYMBOLIC_NAME = "Next";
    /**
     * Exists operator.
     */
    public static final char EXISTS_OPERATOR = 'E';
    /**
     * Symbolic name for exists operator
     */
    public static final String EXISTS_SYMBOLIC_NAME = "Exists";
    /**
     * Always operator.
     */
    public static final char ALL_OPERATOR = 'A';
    /**
     * Symbolic name for always operator
     */
    public static final String ALL_SYMBOLIC_NAME = "All";
    /**
     * Always operator.
     */
    public static final char ALWAYS_OPERATOR = 'G';
    /**
     * Symbolic name for always operator
     */
    public static final String ALWAYS_SYMBOLIC_NAME = "Globally";
    /**
     * Always operator.
     */
    public static final char FINALLY_OPERATOR = 'F';
    /**
     * Symbolic name for always operator
     */
    public static final String FINALLY_SYMBOLIC_NAME = "Finally";
    /**
     * Until operator.
     */
    public static final char UNTIL_OPERATOR = 'U';
    /**
     * Symbolic name for until operator.
     */
    public static final String UNTIL_SYMBOLIC_NAME = "Until";
    /**
     * Symbolic name for an atom.
     */
    public static final String ATOM_SYMBOLIC_NAME = "Atom";
    /**
     * Left parenthesis character used for enclosing relation-parts. 
     */
    static public final char LEFT_PARENTHESIS_CHAR =  ExprParser.ROUND_BRACKETS[0];
    /**
     * Right parenthesis character used for enclosing relation-parts. 
     */
    static public final char RIGHT_PARENTHESIS_CHAR = ExprParser.ROUND_BRACKETS[1];
    /**
     * Comment for <code>SINGLE_QUOTE_CHAR</code>
     */
    static public final char SINGLE_QUOTE_CHAR = '\'';
    /**
     * Left parenthesis string used for enclosing relation-parts. 
     */
    static public final String LEFT_PARENTHESIS = "" + LEFT_PARENTHESIS_CHAR;
    /**
     * Right parenthesis string used for enclosing relation-parts. 
     */
    static public final String RIGHT_PARENTHESIS = "" + RIGHT_PARENTHESIS_CHAR;

    /**
     * true predicate name.
     */
    static public final String TRUE = "true";
    /**
     * false predicate name.
     */
    static public final String FALSE = "false";

    /**
     * Abstract class for prefix CTL operators.
     */
    abstract static protected class Prefix extends CTLExpr {

    	/**
    	 * Creates a new prefix CTL-expression.
    	 * @param operator the operator of the CTL-expression
    	 * @param symbol the symbol of the outer CTL-expression
    	 * @param operand the single operand on which this operator will be applied
    	 */
    	public Prefix(String operator, String symbol, CTLExpr operand) {
            super(operator, symbol);
            this.operand = operand;
            this.operandList = Collections.singletonList(operand);
        }

        /**
         * @return the operand of this CTL-expression
         */
//        public CTLExpr getOperand() {
//            return operand;
//        }

        /* (non-Javadoc)
         * @see groove.verify.CTLExpr#getOperands()
         */
        public List<CTLExpr> getOperands() {
            return Collections.unmodifiableList(operandList);
        }

        /* (non-Javadoc)
         * @see groove.verify.CTLExpr#parseOperator(java.lang.String)
         */
        protected CTLExpr parseOperator(String expr) throws ExprFormatException {
            String[] operands = ExprParser.splitExpr(expr, getOperator(), ExprParser.PREFIX_POSITION);
            if (operands == null) {
                return null;
            }
            return newInstance(parse(operands[0]));
        }

        /**
         * Creates a new instance of a specific CTL-operator given
         * the sub-formula on which it will be applied
         * @param operand the operand on which this CTL-operator will
         * be applied
         * @return a new instance of a specific CTL-expression
         */
        abstract protected CTLExpr newInstance(CTLExpr operand);

        /**
         * Comment for <code>operand</code>
         */
        private final CTLExpr operand;

        /**
         * List of operand of this CTL-operator
         */
        private final List<CTLExpr> operandList;
    }

    /**
     * Abstract class for infix CTL operators.
     */
    abstract static protected class Infix extends CTLExpr {

    	/**
    	 * Creates a new infix CTL-expression.
    	 * @param operator the operator of the CTL-expression
    	 * @param symbol the symbol of the outer CTL-expression
    	 * @param operands the list of operands on which this operator will be applied
    	 */
    	public Infix(String operator, String symbol, List<CTLExpr> operands) {
            super(operator, symbol);
            this.operandList = operands;
        }

        /* (non-Javadoc)
         * @see groove.verify.CTLExpr#getOperands()
         */
        public List<CTLExpr> getOperands() {
            return Collections.unmodifiableList(operandList);
        }

        /* (non-Javadoc)
         * @see groove.verify.CTLExpr#parseOperator(java.lang.String)
         */
        protected CTLExpr parseOperator(String expr) throws ExprFormatException {
            String[] operands = ExprParser.splitExpr(expr, getOperator(), ExprParser.INFIX_POSITION);
            if (operands.length < 2) {
                return null;
            }
            List<CTLExpr> operandList = new LinkedList<CTLExpr>();
            for (int i = 0; i < operands.length; i++) {
                operandList.add(parse(operands[i]));
            }
            return newInstance(operandList);
        }

        /**
         * Create a new instance of a specific infix-operator.
         * 
         * @param operands the list of operands of this operation.
         * @return the resulting CTL-expression.
         */
        abstract protected CTLExpr newInstance(List<CTLExpr> operands);

        /**
         * List of operands.
         */
        private final List<CTLExpr> operandList;
    }

    /**
     * Boolean negation of an expression.
     * Example syntax: given phi, then "not(phi)" is denoted by "!phi" or "!(phi)"
     */
    static public class Neg extends Prefix {

    	/**
    	 * Creates a new instance of the boolean negation operator given
    	 * the operand on which it will be applied.
    	 * @param operand the operand on which the CTL negation operator
    	 * will be applied
    	 */
    	public Neg(CTLExpr operand) {
            super("" + NEGATION_OPERATOR, NEGATION_SYMBOLIC_NAME, operand);
        }

        /**
         * Empty constructor.
         */
    	protected Neg() {
            this(null);
        }

        /* (non-Javadoc)
         * @see groove.verify.CTLExpr.Prefix#newInstance(groove.verify.CTLExpr)
         */
        protected CTLExpr newInstance(CTLExpr operand) {
            return new Neg(operand);
        }

        /* (non-Javadoc)
         * @see groove.verify.CTLExpr#mark()
         */
        public void mark(CTLFormulaMarker marker, Marking marking, GTS gts) {
//            marker.markNeg(marking, this, gts);
        }
    }

    /**
     * Boolean conjunction of two expressions.
     * Example syntax: given phi and psi, then "phi and psi" is denoted by "phi & psi"
     */
    static public class And extends Infix {

    	/**
    	 * Creates a new instance of the boolean and operator given the list
    	 * of operands on which it will be applied.
    	 * @param operands the list of operands on which this operator will be applied
    	 */
    	public And(List<CTLExpr> operands) {
            super("" + AND_OPERATOR, AND_SYMBOLIC_NAME, operands);
        }

        /**
         * Constructor.
         */
    	protected And() {
            this(null);
        }

        /* (non-Javadoc)
         * @see groove.verify.CTLExpr.Infix#newInstance(java.util.List)
         */
        protected CTLExpr newInstance(List<CTLExpr> operands) {
        	// (phi & psi & ...) <==> !(!phi | !psi | ...)
        	Neg neg = new Neg();
        	Or or = new Or();
            List<CTLExpr> negOperands = new ArrayList<CTLExpr>();
            for (CTLExpr nextOperand : operands) {
				negOperands.add(neg.newInstance(nextOperand));
			}
//            negOperands.add(neg.newInstance((CTLExpr) operands.get(0)));
//            negOperands.add(neg.newInstance((CTLExpr) operands.get(1)));
            or.newInstance(negOperands);
            return neg.newInstance(or);
        }

        /* (non-Javadoc)
         * @see groove.verify.CTLExpr#mark(groove.verify.Marking, groove.lts.GTS)
         */
        public void mark(CTLFormulaMarker marker, Marking marking, GTS gts) {
            throw new UnsupportedOperationException("Marking of " + this.getClass() + " should be redirected.");
        }
    }

    /**
     * Boolean disjunction of two expressions.
     * Example syntax: given phi and psi, then "phi or psi" is denoted by "phi | spi"
     */
    static public class Or extends Infix {

    	/**
    	 * Creates a new instance of the boolean or operator given the list
    	 * of operands on which it will be applied.
    	 * @param operands the list of operands on which this operator will be applied
    	 */
    	public Or(List<CTLExpr> operands) {
            super("" + OR_OPERATOR, OR_SYMBOLIC_NAME, operands);
        }

        /**
         * Constructor.
         */
    	protected Or() {
            this(null);
        }

        /* (non-Javadoc)
         * @see groove.verify.CTLExpr.Infix#newInstance(java.util.List)
         */
        protected CTLExpr newInstance(List<CTLExpr> operands) {
        	return new Or(operands);
        }

        /* (non-Javadoc)
         * @see groove.verify.CTLExpr#mark(groove.verify.Marking, groove.lts.GTS)
         */
        public void mark(CTLFormulaMarker marker, Marking marking, GTS gts) {
//            marker.markOr(marking, this, gts);
        }
    }

    /**
     * Temporal next operator.
     * Example syntax: given phi, then "next phi" is denoted by "X(phi)"
     */
    static public class Next extends Prefix {

    	/**
    	 * Creates a new instance of the temporal next operator given the 
    	 * single operand on which it will be applied.
    	 * @param operand the single operand on which this operator will be applied
    	 */
    	public Next(CTLExpr operand) {
            super("" + NEXT_OPERATOR, NEXT_SYMBOLIC_NAME, operand);
        }

        /**
         * Constructor. 
         */
    	protected Next() {
            this(null);
        }

        /* (non-Javadoc)
         * @see groove.verify.CTLExpr.Prefix#newInstance(groove.verify.CTLExpr)
         */
        protected CTLExpr newInstance(CTLExpr operand) {
            return new Next(operand);
        }

        /* (non-Javadoc)
         * @see groove.verify.CTLExpr#mark(groove.verify.Marking, groove.lts.GTS)
         */
        public void mark(CTLFormulaMarker marker, Marking marking, GTS gts) {
            throw new UnsupportedOperationException("Next operator (X) should be proceeded by a path quantifier...");
        }
    }

    /**
     * Temporal until operator.
     * Example syntax: given phi and psi, then "phi until psi" is denoted by "phi U psi"
     */
    static public class Until extends Infix {

    	/**
    	 * Creates a new instance of the temporal until operator given the list
    	 * of operands on which it will be applied.
    	 * @param operands the list of operands on which this operator will be applied
    	 */
    	public Until(List<CTLExpr> operands) {
            super("" + UNTIL_OPERATOR, UNTIL_SYMBOLIC_NAME, operands);
        }

        /**
         * Constructor. 
         */
    	protected Until() {
            this(null);
        }

        /* (non-Javadoc)
         * @see groove.verify.CTLExpr.Infix#newInstance(java.util.List)
         */
        protected CTLExpr newInstance(List<CTLExpr> operands) {
            return new Until(operands);
        }

        /* (non-Javadoc)
         * @see groove.verify.CTLExpr#mark(groove.verify.Marking, groove.lts.GTS)
         */
        public void mark(CTLFormulaMarker marker, Marking marking, GTS gts) {
            throw new UnsupportedOperationException("Until operator (U) should be proceeded by a path quantifier...");
        }
    }

    /**
     * Temporal globally operator.
     * Example syntax: given phi, then "globally phi" is denoted by "G(phi)"
     */
    static public class Globally extends Prefix {

    	/**
    	 * Creates a new instance of the temporal globally operator given the
    	 * single operand on which it will be applied.
    	 * @param operand the single operand on which this operator will be applied
    	 */
    	public Globally(CTLExpr operand) {
            super("" + ALWAYS_OPERATOR, ALWAYS_SYMBOLIC_NAME, operand);
        }

        /**
         * Constructor. 
         */
    	protected Globally() {
            this(null);
        }

        /* (non-Javadoc)
         * @see groove.verify.CTLExpr.Prefix#newInstance(groove.verify.CTLExpr)
         */
        protected CTLExpr newInstance(CTLExpr operand) {
            return new Globally(operand);
        }

        /* (non-Javadoc)
         * @see groove.verify.CTLExpr#mark(groove.verify.Marking, groove.lts.GTS)
         */
        public void mark(CTLFormulaMarker marker, Marking marking, GTS gts) {
            throw new UnsupportedOperationException("Globally operator (G) should be proceeded by a path quantifier...");
        }
    }

    /**
     * Temporal finally operator.
     * Example syntax: given phi, then "finally phi" is denoted by "F(phi)"
     */
    static public class Finally extends Prefix {

    	/**
    	 * Creates a new instance of the temporal finally operator given the
    	 * single operand on which it will be applied.
    	 * @param operand the single operand on which this operator will be applied
    	 */
    	public Finally(CTLExpr operand) {
            super("" + FINALLY_OPERATOR, FINALLY_SYMBOLIC_NAME, operand);
        }

        /**
         * Constructor.
         */
    	protected Finally() {
            this(null);
        }

        /* (non-Javadoc)
         * @see groove.verify.CTLExpr.Prefix#newInstance(groove.verify.CTLExpr)
         */
        protected CTLExpr newInstance(CTLExpr operand) {
            return new Finally(operand);
        }

        /* (non-Javadoc)
         * @see groove.verify.CTLExpr#mark(groove.verify.Marking, groove.lts.GTS)
         */
        public void mark(CTLFormulaMarker marker, Marking marking, GTS gts) {
            throw new UnsupportedOperationException("Finally operator (F) should be proceeded by a path quantifier...");
        }
    }

    /**
     * Existential path quantifier.
     * Example syntax: given phi, then "exists next phi" is denoted by "E(X(phi))"
     */
    static public class Exists extends Prefix {

    	/**
    	 * Creates a new instance of the exists path quatifier given the
    	 * single operand on which it will be applied.
    	 * @param operand the single operand on which this path quantifier
    	 * will be applied
    	 */
    	public Exists(CTLExpr operand) {
            super("" + EXISTS_OPERATOR, EXISTS_SYMBOLIC_NAME, operand);
        }

        /**
         * Constructor. 
         */
    	protected Exists() {
            this(null);
        }

        /* (non-Javadoc)
         * @see groove.verify.CTLExpr.Prefix#newInstance(groove.verify.CTLExpr)
         */
        protected CTLExpr newInstance(CTLExpr operand) {
            if (operand instanceof CTLExpr.Next) {
                return new ExistsNext((CTLExpr) operand.getOperands().get(0));
            }
            if (operand instanceof CTLExpr.Finally) {
                ExistsFinally result = new ExistsFinally();
                return result.newInstance((CTLExpr) operand.getOperands().get(0));
            }
            // EG(phi) <==> !(AF(!phi))
            if (operand instanceof CTLExpr.Globally) {
                ExistsGlobally result = new ExistsGlobally();
                return result.newInstance((CTLExpr) operand.getOperands().get(0));
            }
            if (operand instanceof CTLExpr.Until) {
                ExistsUntil result = new ExistsUntil();
                return result.newInstance(operand.getOperands());
            } else {
                // if not, throw an exception
            	// HARMEN: handle this a bit more user-friendly
                return null;
            }
        }

        /* (non-Javadoc)
         * @see groove.verify.CTLExpr#parseOperator(java.lang.String)
         */
        protected CTLExpr parseOperator(String expr) throws ExprFormatException {
            String[] operands = ExprParser.splitExpr(expr, getOperator(), ExprParser.PREFIX_POSITION);
            if (operands == null) {
                return null;
            }
            CTLExpr result = newInstance(parse(operands[0]));
            if (result == null)
                throw new ExprFormatException("Unproper use of Exists-construction.");
            return result;
        }

        /* (non-Javadoc)
         * @see groove.verify.CTLExpr#mark(groove.verify.Marking, groove.lts.GTS)
         */
        public void mark(CTLFormulaMarker marker, Marking marking, GTS gts) {
            throw new UnsupportedOperationException("This call should have been redirected to another class than " + Exists.class);
        }
    }

    /**
     * Universal path quantifier.
     * Example syntax: given phi, then "All next phi" is denoted by "A(X(phi))"
     */
    static public class All extends Prefix {

    	/**
    	 * Creates a new instance of the universal path quatifier given the
    	 * single operand on which it will be applied.
    	 * @param operand the single operand on which this path quantifier
    	 * will be applied
    	 */
    	public All(CTLExpr operand) {
            super("" + ALL_OPERATOR, ALL_SYMBOLIC_NAME, operand);
        }

        /**
         * Constructor.
         */
        protected All() {
            this(null);
        }

        /* (non-Javadoc)
         * @see groove.verify.CTLExpr.Prefix#newInstance(groove.verify.CTLExpr)
         */
        protected CTLExpr newInstance(CTLExpr operand) {
            if (operand instanceof CTLExpr.Next) {
                AllNext allNext = new AllNext();
                return allNext.newInstance((CTLExpr) operand.getOperands().get(0));
            }
            if (operand instanceof CTLExpr.Finally) {
                AllFinally allFinally = new AllFinally();
                return allFinally.newInstance((CTLExpr) operand.getOperands().get(0));
            }
            if (operand instanceof CTLExpr.Globally) {
                AllGlobally allGlobally = new AllGlobally();
                return allGlobally.newInstance((CTLExpr) operand.getOperands().get(0));
            }
            if (operand instanceof CTLExpr.Until) {
                AllUntil allUntil = new AllUntil();
                return allUntil.newInstance(operand.getOperands());
            }
            else {
                return null;
            }
        }

        /* (non-Javadoc)
         * @see groove.verify.CTLExpr#parseOperator(java.lang.String)
         */
        protected CTLExpr parseOperator(String expr) throws ExprFormatException {
            String[] operands = ExprParser.splitExpr(expr, getOperator(), ExprParser.PREFIX_POSITION);
            if (operands == null) {
                return null;
            }
            CTLExpr result = newInstance(parse(operands[0]));
            if (result == null)
                throw new ExprFormatException("Unproper use of All-construction.");
            return result;
        }

        /* (non-Javadoc)
         * @see groove.verify.CTLExpr#mark(groove.verify.Marking, groove.lts.GTS)
         */
        public void mark(CTLFormulaMarker marker, Marking marking, GTS gts) {
            throw new UnsupportedOperationException("This call should have been redirected to another class than " + All.class);
        }
    }

    /**
     * Existential path quantifier with temporal next operator.
     * Example syntax: given phi, then "Exists next phi" is denoted by "EX(phi)" or "E(X(phi))"
     */
    static public class ExistsNext extends Prefix {

    	/**
    	 * Creates a new instance of the existential path quatifier with the
    	 * temporal next operator given the single operand on which it will
    	 * be applied.
    	 * @param operand the single operand on which this path quantifier
    	 * will be applied
    	 */
        public ExistsNext(CTLExpr operand) {
            super("" + EXISTS_OPERATOR, EXISTS_SYMBOLIC_NAME, operand);
        }

        /**
         * Constructor.
         */
        private ExistsNext() {
            this(null);
        }

        /* (non-Javadoc)
         * @see groove.verify.CTLExpr.Prefix#newInstance(groove.verify.CTLExpr)
         */
        protected CTLExpr newInstance(CTLExpr operand) {
            return new ExistsNext(operand);
        }

        /* (non-Javadoc)
         * @see groove.verify.CTLExpr#mark()
         */
        public void mark(CTLFormulaMarker marker, Marking marking, GTS gts) {
//            marker.markExistsNext(marking, this, gts);
        }
    }

    /**
     * Universal path quantifier with temporal next operator.
     * Example syntax: given phi, then "All next phi" is denoted by "AX(phi)" or "A(X(phi))"
     */
    static public class AllNext extends Prefix {

    	/**
    	 * Creates a new instance of the universal path quatifier with the
    	 * temporal next operator given the single operand on which it will
    	 * be applied.
    	 * @param operand the single operand on which this path quantifier
    	 * will be applied
    	 */
    	public AllNext(CTLExpr operand) {
            super("" + ALL_OPERATOR, ALL_SYMBOLIC_NAME, operand);
        }

        /**
         * Constructor.
         */
        private AllNext() {
            this(null);
        }

        /* (non-Javadoc)
         * @see groove.verify.CTLExpr.Prefix#newInstance(groove.verify.CTLExpr)
         */
        protected CTLExpr newInstance(CTLExpr operand) {
            // AX(phi) <==> !EX(!phi)
            Neg neg = new Neg();
            ExistsNext existsNext = new ExistsNext();
            return neg.newInstance(existsNext.newInstance(neg.newInstance(operand)));
        }

        /* (non-Javadoc)
         * @see groove.verify.CTLExpr#mark(groove.verify.Marking, groove.lts.GTS)
         */
        public void mark(CTLFormulaMarker marker, Marking marking, GTS gts) {
            throw new UnsupportedOperationException("Marking of " + this.getClass() + " should be redirected.");
        }
    }

    /**
     * Existential path quantifier with temporal finally operator.
     * Example syntax: given phi, then "Exists finally phi" is denoted by "EF(phi)" or "E(F(phi))"
     */
    static public class ExistsFinally extends Prefix {

    	/**
    	 * Creates a new instance of the existential path quatifier with the
    	 * temporal finally operator given the single operand on which it will
    	 * be applied.
    	 * @param operand the single operand on which this path quantifier
    	 * will be applied
    	 */
    	public ExistsFinally(CTLExpr operand) {
            super("" + EXISTS_OPERATOR, EXISTS_SYMBOLIC_NAME, operand);
        }

        /**
         * Constructor.
         */
        private ExistsFinally() {
            this(null);
        }

        /* (non-Javadoc)
         * @see groove.verify.CTLExpr.Prefix#newInstance(groove.verify.CTLExpr)
         */
        protected CTLExpr newInstance(CTLExpr operand) {
        	// EF(phi) <==> E(true U phi)
            ExistsUntil existsUntil = new ExistsUntil();
            List<CTLExpr> operands = new LinkedList<CTLExpr>();
            CTLExpr trueOperand = new True();
            operands.add(trueOperand);
            operands.add(operand);
            return existsUntil.newInstance(operands);
        }

        /* (non-Javadoc)
         * @see groove.verify.CTLExpr#mark(groove.verify.Marking, groove.lts.GTS)
         */
        public void mark(CTLFormulaMarker marker, Marking marking, GTS gts) {
            throw new UnsupportedOperationException("Marking of " + this.getClass() + " should be redirected.");
        }
    }


    /**
     * Universal path quantifier with temporal finally operator.
     * Example syntax: given phi, then "All finally phi" is denoted by "AF(phi)" or "A(F(phi))"
     */
    static public class AllFinally extends Prefix {

    	/**
    	 * Creates a new instance of the universal path quatifier with the
    	 * temporal finally operator given the single operand on which it will
    	 * be applied.
    	 * @param operand the single operand on which this path quantifier
    	 * will be applied
    	 */
    	public AllFinally(CTLExpr operand) {
            super("" + ALL_OPERATOR, ALL_SYMBOLIC_NAME, operand);
        }

        /**
         * Constructor. 
         */
        private AllFinally() {
            this(null);
        }

        /* (non-Javadoc)
         * @see groove.verify.CTLExpr.Prefix#newInstance(groove.verify.CTLExpr)
         */
        protected CTLExpr newInstance(CTLExpr operand) {
        	// AF(phi) <==> A(true U phi)
            AllUntil allUntil = new AllUntil();
            List<CTLExpr> operands = new LinkedList<CTLExpr>();
            CTLExpr trueOperand = new True();
            operands.add(trueOperand);
            operands.add(operand);
            return allUntil.newInstance(operands);
        }

        /* (non-Javadoc)
         * @see groove.verify.CTLExpr#mark(groove.verify.Marking, groove.lts.GTS)
         */
        public void mark(CTLFormulaMarker marker, Marking marking, GTS gts) {
            throw new UnsupportedOperationException("Marking of " + this.getClass() + " should be redirected.");
        }
    }


    /**
     * Existential path quantifier with temporal globally operator.
     * Example syntax: given phi, then "Exists globally phi" is denoted by "EG(phi)" or "E(G(phi))"
     */
    static public class ExistsGlobally extends Prefix {

    	/**
    	 * Creates a new instance of the existential path quatifier with the
    	 * temporal globally operator given the single operand on which it will
    	 * be applied.
    	 * @param operand the single operand on which this path quantifier
    	 * will be applied
    	 */
    	public ExistsGlobally(CTLExpr operand) {
            super("" + EXISTS_OPERATOR, EXISTS_SYMBOLIC_NAME, operand);
        }

        /**
         * Constructor. 
         */
        private ExistsGlobally() {
            this(null);
        }

        /* (non-Javadoc)
         * @see groove.verify.CTLExpr.Prefix#newInstance(groove.verify.CTLExpr)
         */
        protected CTLExpr newInstance(CTLExpr operand) {
        	// EG(phi) <==> !(AF(!phi))
            Neg neg = new Neg();
            AllFinally allFinally = new AllFinally();
            return neg.newInstance(allFinally.newInstance(neg.newInstance(operand)));
        }

        /* (non-Javadoc)
         * @see groove.verify.CTLExpr#mark(groove.verify.Marking, groove.lts.GTS)
         */
        public void mark(CTLFormulaMarker marker, Marking marking, GTS gts) {
            throw new UnsupportedOperationException("Marking of " + this.getClass() + " should be redirected.");
        }
    }

    /**
     * Universal path quantifier with temporal globally operator.
     * Example syntax: given phi, then "All globally phi" is denoted by "AG(phi)" or "A(G(phi))"
     */
    static public class AllGlobally extends Prefix {

    	/**
    	 * Creates a new instance of the universal path quatifier with the
    	 * temporal globally operator given the single operand on which it will
    	 * be applied.
    	 * @param operand the single operand on which this path quantifier
    	 * will be applied
    	 */
    	public AllGlobally(CTLExpr operand) {
            super("" + ALL_OPERATOR, ALL_SYMBOLIC_NAME, operand);
        }

        /**
         * Constructor.
         */
        private AllGlobally() {
            this(null);
        }

        /* (non-Javadoc)
         * @see groove.verify.CTLExpr.Prefix#newInstance(groove.verify.CTLExpr)
         */
        protected CTLExpr newInstance(CTLExpr operand) {
        	// AG(phi) <==> !(EF(!phi))
            Neg neg = new Neg();
            ExistsFinally existsFinally = new ExistsFinally();
            return neg.newInstance(existsFinally.newInstance(neg.newInstance(operand)));
        }

        /* (non-Javadoc)
         * @see groove.verify.CTLExpr#mark(groove.verify.Marking, groove.lts.GTS)
         */
        public void mark(CTLFormulaMarker marker, Marking marking, GTS gts) {
            throw new UnsupportedOperationException("Marking of " + this.getClass() + " should be redirected.");
        }
    }


    /**
     * Existential path quantifier with temporal until operator.
     * Example syntax: given phi, then "Exists phi until psi" is denoted by "E(phi U psi)"
     */
    static public class ExistsUntil extends Infix {

    	/**
    	 * Creates a new instance of the existential path quatifier with the
    	 * temporal until operator given the list of operands on which it will
    	 * be applied.
    	 * @param operands the list of operands on which this path quantifier
    	 * will be applied
    	 */
    	public ExistsUntil(List<CTLExpr> operands) {
            super("" + EXISTS_OPERATOR, EXISTS_SYMBOLIC_NAME, operands);
        }

        /**
         * Constructor.
         */
        private ExistsUntil() {
            this(null);
        }

        /* (non-Javadoc)
         * @see groove.verify.CTLExpr.Prefix#newInstance(groove.verify.CTLExpr)
         */
        protected CTLExpr newInstance(List<CTLExpr> operands) {
            return new ExistsUntil(operands);
        }

        /* (non-Javadoc)
         * @see groove.verify.CTLExpr#mark()
         */
        public void mark(CTLFormulaMarker marker, Marking marking, GTS gts) {
//            marker.markExistsUntil(marking, this, gts);
        }
    }


    /**
     * Universal path quantifier with temporal until operator.
     * Example syntax: given phi, then "All phi until psi" is denoted by "A(phi U psi)"
     */
    static public class AllUntil extends Infix {

    	/**
    	 * Creates a new instance of the universal path quatifier with the
    	 * temporal until operator given the list of operands on which it will
    	 * be applied.
    	 * @param operands the list of operands on which this path quantifier
    	 * will be applied
    	 */
    	public AllUntil(List<CTLExpr> operands) {
            super("" + ALWAYS_OPERATOR, ALWAYS_SYMBOLIC_NAME, operands);
        }

        /**
         * Empty constructor.
         */
        private AllUntil() {
            this(null);
        }

        /* (non-Javadoc)
         * @see groove.verify.CTLExpr.Infix#newInstance(java.util.List)
         */
        protected CTLExpr newInstance(List<CTLExpr> operands) {
            return new AllUntil(operands);
        }

        /* (non-Javadoc)
         * @see groove.verify.CTLExpr#mark(groove.verify.Marking, groove.lts.GTS)
         */
        public void mark(CTLFormulaMarker marker, Marking marking, GTS gts) {
//            marker.markAllUntil(marking, this, gts);
        }
    }

    /**
     * Atomic proposition.
     * Example syntax: "empty" or "yellow" or "p"
     */
    static public class Atom extends CTLExpr {
        /**
         * @param condition
         */
        public Atom(String condition) {
            super("", ATOM_SYMBOLIC_NAME);
            this.predicateName = condition;
        }

        /**
         * Empty constructor. 
         */
        private Atom() {
            this(null);
        }

        /**
         * Returns the name of this predicate represented by this atom.
         * 
         * @return the name of this predicate
         */
        public String predicateName() {
            return predicateName;
        }

        /* (non-Javadoc)
         * @see groove.verify.CTLExpr#parseOperator(java.lang.String)
         */
        protected CTLExpr parseOperator(String expr) throws ExprFormatException {
            if (ExprParser.matches(expr, LEFT_PARENTHESIS_CHAR, RIGHT_PARENTHESIS_CHAR)) {
                return parse(ExprParser.trim(expr, LEFT_PARENTHESIS_CHAR, RIGHT_PARENTHESIS_CHAR));
            } else {
                assertAtom(expr);
                String trimmed = expr.trim();
                if (trimmed.equals(TRUE))
                    return new True();
                else if (trimmed.equals(FALSE))
                    return new False();
                else
                    return newInstance(expr.trim());
            }
        }

        /**
         * Create a new instance of an atomic proposition.
         * @param proposition the string representation of the atomic proposition
         * @return the resulting CTL-expression
         */
        protected CTLExpr newInstance(String proposition) {
            return new Atom(proposition);
        }

        /* (non-Javadoc)
         * @see groove.verify.CTLExpr#getOperands()
         */
        public List<CTLExpr> getOperands() {
            return Collections.EMPTY_LIST;
        }

        /**
         * @return
         */
//        public GraphPredicate toGraphPredicate() {
//            return graphCondition.getNegConjunct();
//        }

        /**
         * Returns the graph condition corresponding to this atom.
         * @return the graph condition corresponding to this atom
         */
        public GraphCondition graphCondition() {
            return graphCondition;
        }

        /* (non-Javadoc)
         * @see groove.verify.CTLExpr#mark()
         */
        public void mark(CTLFormulaMarker marker, Marking marking, GTS gts) {
//            marker.markAtom(marking, this, gts);
        }

        /**
         * Comment for <code>predicateName</code>
         */
        private final String predicateName;
        /**
         * The graph-predicate representing this CTL-expression.
         */
        private GraphCondition graphCondition;
    }


    /**
     * TRUE atomic proposition which always holds.
     * Syntax: "true" @see {@link CTLExpr#TRUE}
     */
    static public class True extends Atom {

    	/**
    	 * Constructor.
    	 */
    	public True() {
            super(TRUE);
        }

        /* (non-Javadoc)
         * @see groove.verify.CTLExpr#mark(groove.verify.Marking, groove.lts.GTS)
         */
        public void mark(CTLFormulaMarker marker, Marking marking, GTS gts) {
//            marker.markTrue(marking, this, gts);
        }
    }


    /**
     * FALSE atomic proposition which never holds.
     * Syntax: "false" @see {@link CTLExpr#FALSE}
     */
    static public class False extends Atom {

    	/**
    	 * Constructor.
    	 */
    	public False() {
            super(FALSE);
        }

        /* (non-Javadoc)
         * @see groove.verify.CTLExpr#mark(groove.verify.Marking, groove.lts.GTS)
         */
        public void mark(CTLFormulaMarker marker, Marking marking, GTS gts) {
//            marker.markFalse(marking, this, gts);
        }
    }

    /**
     * Comment for <code>prototypes</code>
     */
    static private final CTLExpr[] prototypes = new CTLExpr[] {
            new Exists(),
            new All(),
            new Finally(),
            new Globally(),
            new And(),
            new Or(),
            new Next(),
            new Until(),
            new Neg(),
            new Atom()};
    /**
     * The list of operators into which a ctl expression will be parsed, in order of increasing
     * priority.
     */
    static private final List<String> operators;

    static {
        List<String> result = new LinkedList<String>();
        for (int op = 0; op < prototypes.length; op++) {
            CTLExpr prototype = prototypes[op];
            if (!(prototype instanceof Atom)) {
                result.add(prototype.getOperator());
            }
        }
        operators = result;
    }

    /**
     * Constructor creating an CTL-expression given an operator and its symbol.
     * 
     * @param operator the operator of this CTL-expression
     * @param symbol the symbol of this CTL-expression
     */
    public CTLExpr(String operator, String symbol) {
        this.operator = operator;
        this.symbol = symbol;
        this.counterExamples = new HashSet<State>();
    }

    /**
     * Parses the string given to this method. It throws a {@link ExprFormatException}
     * if the string does not represent a valid CTL-expression.
     * 
     * @param expr the string to be parsed
     * @return the {@link CTLExpr} corresponding to the given expression
     * @throws ExprFormatException if the string does not represent a valid CTL-expression
     */
    static public CTLExpr parse(String expr) throws ExprFormatException {
        // try to parse the expression using each of the available operators in turn
        for (int op = 0; op < prototypes.length; op++) {
            CTLExpr result = prototypes[op].parseOperator(expr);
            // if the result is non-null, we are done
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the operator of this CTL-expression.
     * 
     * @return the operator of this CTL-expression.
     */
    public String getOperator() {
        return operator;
    }

    /**
     * Returns the operands of this CTL-expression as a list.
     * @return the operands of this CTL-expression as a list
     */
    abstract public List<CTLExpr> getOperands();

    /**
     * Mark all the states that satisfy this CTL-expression.
     * @param marker the marker to which marking is deferred
     * @param marking marking of states so far
     * @param gts the gts providing the states and transitions
     */
    public abstract void mark(CTLFormulaMarker marker, Marking marking, GTS gts);

    /**
     * Parse the given expression with the current operator and return the correct
     * CTL-expression. If the given expression is in the wrong format, throw an
     * exception.
     * 
     * @param expr the expression to be parsed
     * @return the CTL-expression represented by the given string
     * @throws ExprFormatException if the string expression is in the wrong format
     */
    abstract protected CTLExpr parseOperator(String expr) throws ExprFormatException ;

    /**
     * Tests whether a given text may be regarded as an atom, according to the rules of regular
     * expressions. (If not, then it should be single-quoted.) This implementation throws an
     * exception if the text contains any of the operator strings in {@link #operators}
     * as a sub-string. which is the case if the text does not contain any special characters
     * @param text the text to be tested
     * @throws ExprFormatException if the text contains a special character
     */
    protected void assertAtom(String text) throws ExprFormatException {
        for (int c = 0; c < operators.size(); c++) {
            if (text.indexOf((String) operators.get(c)) >= 0) {
                throw new ExprFormatException("Operator " + operators.get(c) + " in unquoted atom "
                        + text);
            }
        }
    }

    /**
     * Creates an iterator over the predecessor-states of the given states in the
     * given {@link GTS}.
     * @param gts the <code>GTS</code> in which to look for predecessor-states
     * @param state the state for which to look for predecessors.
     * @return an iterator over the predecessor-states
     */
    protected Iterator<State> getPredecessorsIterator(GTS gts, State state) {
        Set<State> result = new HashSet<State>();
        // for the time being, we look for predecessor-states by
        // checking all transitions having the current state as target
        Iterator<? extends GraphTransition> transitionIter = gts.edgeSet().iterator();
        while (transitionIter.hasNext()) {
            GraphTransition nextTransition = transitionIter.next();
            Node target = nextTransition.target();
            if (target instanceof State && ((State) target).equals(state)) {
                State predecessorState = (State) nextTransition.source();
                result.add(predecessorState);
            }
        }
        return result.iterator();
    }

    /**
     * Links the propositions to the rules (only used in {@link CTLMatchingMarker}).
     * @param verifier the <code>CTLModelChecker</code>-instance which keeps track of the gratra-rules.
     * @throws GraphPredicateNotFoundException if some graph-predicate used in the formula is not available
     */
    public void linkPredicates(CTLModelChecker verifier) throws GraphPredicateNotFoundException {
        if (this.getClass().equals(Atom.class)) {
            GraphCondition graphCondition = verifier.getGraphCondition(((Atom) this).predicateName());
            if (graphCondition == null)
                throw new GraphPredicateNotFoundException("Graph-predicate '" + ((Atom) this).predicateName() + "' not present.");
            else
            ((Atom) this).graphCondition = graphCondition; 
        }
        else {
	        List<CTLExpr> operands = getOperands();
	        for (int i = 0; i < operands.size(); i++) {
	            CTLExpr operand = (CTLExpr) operands.get(i);
                operand.linkPredicates(verifier);
	        }
        }
    }

    /**
     * Returns whether this CTL-expression has any counter-examples.
     * @return <tt>true</tt> if the list of counter-examples is non-empty, <tt>false</tt> otherwise.
     */
    protected boolean hasCounterExamples() {
        return !(counterExamples.isEmpty());
    }

    /**
     * Returns the collection of counter-examples.
     * @return the collection of counter-examples.
     */
    public Collection<State> getCounterExamples() {
        return counterExamples;
    }

    /**
     * The operator of the CTL-expression.
     */
    private final String operator;

    /**
     * Symbol of the CTL-expression.
     */
    private final String symbol;

    /**
     * The collection of counter-examples of the CTL-expression.
     */
    private Collection<State> counterExamples;
}