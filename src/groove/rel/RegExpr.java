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
 * $Id: RegExpr.java,v 1.11 2007-06-28 12:05:39 rensink Exp $
 */
package groove.rel;

import static groove.util.ExprParser.*;
import groove.util.ExprParser;
import groove.util.Pair;
import groove.view.FormatException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Class implementing a regular expression.
 * @author Arend Rensink
 * @version $Revision: 1.11 $
 */
abstract public class RegExpr implements VarSetSupport {
    /** 
     * Sequential operator.
     * @see Seq
     */
    static public final char SEQ_OPERATOR = '.';
    /**
     * Symbolic name of the sequential operator. 
     * @see Seq
     */
    static public final String SEQ_SYMBOLIC_NAME = "Seq";
    /** 
     * Kleene star operator.
     * @see Star
     */
    static public final char STAR_OPERATOR = '*';
    /**
     * Symbolic name of the Kleene star operator. 
     * @see Star
     */
    static public final String STAR_SYMBOLIC_NAME = "Some";
    /** 
     * Choice operator.
     * @see Choice
     */
    static public final char CHOICE_OPERATOR = '|';
    /**
     * Symbolic name of the choice operator. 
     * @see Choice
     */
    static public final String CHOICE_SYMBOLIC_NAME = "Or";

    /** 
     * Plus ("at least one occurence") operator.
     * @see Plus
     */
    static public final char PLUS_OPERATOR = '+';
    /**
     * Symbolic name of the plus ("at least one occurrence") operator. 
     * @see Plus
     */
    static public final String PLUS_SYMBOLIC_NAME = "More";

    /** 
     * Empty constant.
     * @see Empty
     */
    static public final char EMPTY_OPERATOR = '=';
    /**
     * Symbolic name of the empty constant. 
     * @see Empty
     */
    static public final String EMPTY_SYMBOLIC_NAME = "Empty";
    /** 
     * Woldcard constant.
     * @see Wildcard
     */
    static public final char WILDCARD_OPERATOR = '?';
    /**
     * Symbolic name of the wildcard constant. 
     * @see Wildcard
     */
    static public final String WILDCARD_SYMBOLIC_NAME = "Any";
    /** 
     * Inverse operator.
     * @see Inv
     */
    static public final char INV_OPERATOR = '-';
    /**
     * Symbolic name of the inverse operator. 
     * @see Inv
     */
    static public final String INV_SYMBOLIC_NAME = "Back";

    /** 
     * Negation operator.
     * @see Neg
     */
    static public final String NEG_OPERATOR = "!";

    /**
     * Symbolic name of the negation operator. 
     * @see Neg
     */
    static public final String NEG_SYMBOLIC_NAME = "Not";

    /**
     * Symbolic name of the atomic constant. 
     * @see Atom
     */
    static public final String ATOM_SYMBOLIC_NAME = "Atom";

    /**
     * The characters allowed in a regular expression atom, apart from letters and digits.
     * @see #isIdentifier(String)
     */
    static public final String ATOM_CHARS = "_$:";

    /**
     * The characters allowed in a wildcard identifier, apart from letters and digits.
     * @see #isIdentifier(String)
     */
    static public final String IDENTIFIER_CHARS = "_$";
    
    /** 
     * Abstract superclass for all regular expressions that are not constants.
     */
    abstract static protected class Composite extends RegExpr {
    	/** 
    	 * Constructs an instance of a composite regular expression
    	 * with a given operator name and operator symbol.
    	 * This constructor is there only for subclassing purposes.
    	 */
        protected Composite(String operator, String symbol) {
            super(operator, symbol);
        }
    }
    
    /**
     * Abstract class modelling a sequence of (more than one)
     * operand separated by a given operator string.
     */
    abstract static protected class Infix extends Composite {
        /**
         * Creates a regular expression from an infix operator and a list of operands. The operands
         * are themselves regular expressions.
         */
        public Infix(String operator, String symbol, List<RegExpr> operands) {
            super(operator, symbol);
            this.operandList = operands;
        }

        /**
         * Returns (a clone of) the operands of this regular expression.
         * @return a clone of the operands of this regular expression
         */
        @Override
        public List<RegExpr> getOperands() {
            return Collections.unmodifiableList(operandList);
        }

        @Override
        public RegExpr parseOperator(String expr) throws FormatException {
            String[] operands = ExprParser
                    .splitExpr(expr, getOperator(), ExprParser.INFIX_POSITION);
            if (operands.length < 2) {
                return null;
            }
            List<RegExpr> operandList = new LinkedList<RegExpr>();
            for (int i = 0; i < operands.length; i++) {
                operandList.add(parse(operands[i]));
            }
            return newInstance(operandList);
        }

        /**
         * Returns the operands, parenthesized if so required by the priority, separated by the
         * operator of this infix expression.
         */
        @Override
        public String toString() {
            StringBuffer result = new StringBuffer();
            Iterator<RegExpr> operandIter = getOperands().iterator();
            while (operandIter.hasNext()) {
                RegExpr operand = operandIter.next();
                if (bindsWeaker(operand, this)) {
                    result.append("" + LPAR_CHAR + operand + RPAR_CHAR);
                } else {
                    result.append(operand);
                }
                if (operandIter.hasNext()) {
                    result.append(getOperator());
                }
            }
            return result.toString();
        }
        
        /**
         * This implementation first calls the calculator on the operands
         * and then on the operator itself with the resulting arguments.
         * @see #applyInfix(RegExprCalculator, List)
         */
        @Override
        public <Result >Result apply(RegExprCalculator<Result> calculator) {
            List<Result> argsList = new ArrayList<Result>();
            for (RegExpr operand: getOperands()) {
                argsList.add(operand.apply(calculator));
            }
            return applyInfix(calculator, argsList);
        }

        /**
         * Factory method for an infix expression. The number of operands is guaranteed to be at
         * least 2.
         * @param operandList the list of operands of the infix expression
         * @return a new infix expression based on <tt>operands</tt>
         * @require <tt>operandList.size() >= 2</tt>
         */
        abstract protected Infix newInstance(List<RegExpr> operandList);
        
        /**
         * Calculation of the actual operation, given precalculated argumants.
         * @see #apply(RegExprCalculator)
         */
        abstract protected <Result> Result applyInfix(RegExprCalculator<Result> visitor, List<Result> argsList);

        /**
         * The operands of this infix expression.
         */
        private final List<RegExpr> operandList;
    }

    /**
     * Abstract class modelling a postfix operatior.
     * This corresponds to one operand followed by a 
     * operator string, fixed in the specializing class.
     */
    abstract static protected class Postfix extends Composite {
        /**
         * Creates a prototye regular expression.
         */
        public Postfix(String operator, String symbol, RegExpr operand) {
            super(operator, symbol);
            this.operand = operand;
            this.operandList = Collections.singletonList(operand);
        }

        /** Returns the single operand of this postfix expression. */
        public RegExpr getOperand() {
            return operand;
        }
        
        /**
         * Returns a singular list consisting of the single operand of this
         * postfix expression.
         */
        @Override
        public List<RegExpr> getOperands() {
            return operandList;
        }

        @Override
        public String toString() {
            if (bindsWeaker(operand, this)) {
                return "" + LPAR_CHAR + getOperand() + RPAR_CHAR
                        + getOperator();
            } else {
                return "" + getOperand() + getOperator();
            }
        }

        /**
         * @return <tt>null</tt> if the postfix operator (given by <tt>operator()</tt>) does
         *         not occur in <tt>tokenList</tt>
         * @throws FormatException of the operator does occur in the list, but not as the last
         *         element
         */
        @Override
        protected RegExpr parseOperator(String expr) throws FormatException {
            String[] operands = ExprParser.splitExpr(expr,
                getOperator(),
                ExprParser.POSTFIX_POSITION);
            if (operands == null) {
                return null;
            }
            return newInstance(parse(operands[0]));
        }
        
        /**
         * This implementation first calls the calculator on the operand
         * and then on the operator itself with the resulting argument.
         * @see #applyPostfix(RegExprCalculator, Object)
         */
        @Override
        public <Result> Result apply(RegExprCalculator<Result> calculator) {
            return applyPostfix(calculator, getOperand().apply(calculator));
        }

        /**
         * Factory method for a postfix expression.
         * @param operand the operand of the postfix expression
         * @return a new postfix expression based on <tt>operand</tt>
         */
        abstract protected Postfix newInstance(RegExpr operand);
        /**
         * Calculation of the actual operation, given a precalculated argumant.
         * @see #apply(RegExprCalculator)
         */
        abstract protected <Result> Result applyPostfix(RegExprCalculator<Result> visitor, Result arg);

        /**
         * The (single) operand of the postfix operator.
         */
        private final RegExpr operand;
        /**
         * The single operand wrapped in a list.
         */
        private final List<RegExpr> operandList;
    }

    /**
     * Abstract class modelling a postfix operatior.
     * This corresponds to an operator string, fixed in the specializing class,
     * followed by one operand.
     */
    abstract static protected class Prefix extends Composite {
        /**
         * Creates a prototye regular expression.
         */
        public Prefix(String operator, String symbol, RegExpr operand) {
            super(operator, symbol);
            this.operand = operand;
            this.operandList = Collections.singletonList(operand);
        }

        /** Returns the single operand of this prefix expression. */
        public RegExpr getOperand() {
            return operand;
        }

        /**
         * Returns a singular list consisting of the single operand of this
         * postfix expression.
         */
        @Override
        public List<RegExpr> getOperands() {
            return operandList;
        }

        @Override
        public String toString() {
            if (bindsWeaker(operand, this)) {
                return "" + getOperator() + LPAR_CHAR + getOperand() + RPAR_CHAR;
            } else {
                return "" + getOperator() + getOperand();
            }
        }

        /**
         * @return <tt>null</tt> if the prefix operator (given by <tt>operator()</tt>) does
         *         not occur in <tt>tokenList</tt>
         * @throws FormatException of the operator does occur in the list, but not as the first
         *         element
         */
        @Override
        protected RegExpr parseOperator(String expr) throws FormatException {
            String[] operands = ExprParser.splitExpr(expr,
                getOperator(),
                ExprParser.PREFIX_POSITION);
            if (operands == null) {
                return null;
            }
            return newInstance(parse(operands[0]));
        }
        
        /**
         * This implementation first calls the calculator on the operand
         * and then on the operator itself with the resulting argument.
         * @see #applyPrefix(RegExprCalculator, Object)
         */
        @Override
        public <Result> Result apply(RegExprCalculator<Result> calculator) {
            return applyPrefix(calculator, getOperand().apply(calculator));
        }

        /**
         * Factory method for a prefix expression.
         * @param operand the operand of the prefix expression
         * @return a new prefix expression based on <tt>operand</tt>
         */
        abstract protected Prefix newInstance(RegExpr operand);
        /**
         * Calculation of the actual operation, given a precalculated argumant.
         * @see #apply(RegExprCalculator)
         */
        abstract protected <Result> Result applyPrefix(RegExprCalculator<Result> visitor, Result arg);

        /**
         * The (single) operand of the prefix operator.
         */
        private final RegExpr operand;
        /**
         * The single operand wrapped in a list.
         */
        private final List<RegExpr> operandList;
    }

    /**
     * Abstract class modelling a constant regular expression.
     */
    abstract static protected class Constant extends RegExpr {
        /**
         * Creates a prototye regular expression.
         */
        private Constant(String operator, String symbol) {
            super(operator, symbol);
        }

        /**
         * This implementation returns an empty list.
         */
        @Override
        public List<RegExpr> getOperands() {
            return Collections.emptyList();
        }
        
        /**
         * This implementation returns the operator, as determined by {@link #getOperator()}.
         */
        @Override
        public String toString() {
            return getOperator();
        }

        /**
         * @return <tt>null</tt> if the postfix operator (given by <tt>operator()</tt>) does
         *         not occur in <tt>tokenList</tt>
         * @throws FormatException of the operator does occur in the list, but not as the last
         *         element
         */
        @Override
        protected RegExpr parseOperator(String expr) throws FormatException {
            if (expr.equals(getOperator())) {
                return newInstance();
            } else {
                return null;
            }
        }

        /**
         * Factory method for a postfix expression.
         * @return a new postfix expression based on <tt>operand</tt>
         */
        abstract protected Constant newInstance();
    }

    /**
     * Sequential composition operator.
     * This is an infix operator that concatenates its operands sequentially.
     */
    static public class Seq extends Infix {
    	/** Creates a sequential composition of a list of expressions. */
        public Seq(List<RegExpr> innerRegExps) {
            super("" + SEQ_OPERATOR, SEQ_SYMBOLIC_NAME, innerRegExps);
        }

        /** Creates a prototype instance. */
        private Seq() {
            this(null);
        }

        @Override
        protected Infix newInstance(List<RegExpr> operandList) {
            return new Seq(operandList);
        }
        
        /**
         * Calls {@link RegExprCalculator#computeSeq(RegExpr.Seq, List)} on the visitor.
         */
        @Override
        protected <Result> Result applyInfix(RegExprCalculator<Result> visitor, List<Result> argsList) {
            return visitor.computeSeq(this, argsList);
        }
    }

    /**
     * Choice operator.
     * This is an infix operator that offers a choice among its operands.
     */
    static public class Choice extends Infix {
    	/** Creates a choice between a list of expressions. */
        public Choice(List<RegExpr> tokenList) {
            super("" + CHOICE_OPERATOR, CHOICE_SYMBOLIC_NAME, tokenList);
        }

        /** Creates a prototype instance. */
        private Choice() {
            this(null);
        }

        @Override
        protected Infix newInstance(List<RegExpr> operandList) {
            return new Choice(operandList);
        }
        
        /**
         * Calls {@link RegExprCalculator#computeChoice(RegExpr.Choice, List)} on the visitor.
         */
        @Override
        protected <Result> Result applyInfix(RegExprCalculator<Result> visitor, List<Result> argsList) {
            return visitor.computeChoice(this, argsList);
        }
    }

    /**
     * Constant expression that stands for all edges existing in the graph.
     * The wildcard may contain an identifier, which then acts as a variable
     * that may be bound to a value when the expression is matched.
     */
    static public class Wildcard extends Constant {
    	/** Creates an instance without variable identifier. */
        public Wildcard() {
            super("" + WILDCARD_OPERATOR, WILDCARD_SYMBOLIC_NAME);
        }

        /**
         * Constructs a wildcard expression with a given identifier.
         * Currently not supported.
         * @param identifier the wildcard identifier
         */
        public Wildcard(String identifier) {
            this();
            this.identifier = identifier;
        }
        
        /**
         * Calls {@link RegExprCalculator#computeWildcard(RegExpr.Wildcard)} on the visitor.
         */
        @Override
        public <Result> Result apply(RegExprCalculator<Result> calculator) {
            return calculator.computeWildcard(this);
        }

        /**
         * This implementation delegates to <code>super</code> if {@link #getDescription()}
         * returns <code>null</code>, otherwise it returns the concatenation of the
         * operator and the identifier.
         */
        @Override
        public String toString() {
            if (getIdentifier() == null) {
                return super.toString();
            } else {
                return getOperator()+getIdentifier();
            }
        }

        /**
         * This implementation delegates to {@link #toString()}.
         */
        @Override
        public String getDescription() {
            return toString();
        }

        /**
         * Returns the optional identifier of this wildcard expression.
         */
        public String getIdentifier() {
            return identifier;
        }

        /**
         * First tries the super implementation, but if that does not work,
         * tries to parse <code>expr</code> as a prefix expression where
         * the operand is an identifier (according to {@link #isIdentifier(String)}).
         */
        @Override
        protected RegExpr parseOperator(String expr) throws FormatException {
            RegExpr result = super.parseOperator(expr);
            if (result == null) {
                String[] operands = ExprParser.splitExpr(expr,
                    getOperator(),
                    ExprParser.PREFIX_POSITION);
                if (operands == null) {
                    return null;
                } else if (isIdentifier(operands[0])) {
                    return newInstance(operands[0]);
                } else {
                    throw new FormatException("Wildcard operand "+operands[0]+" is not a valied identifier");
                }
            } else {
                return result;
            }
        }

        /** Returns a {@link Wildcard} with a given identifier. */
        protected Wildcard newInstance(String identifier) {
            return new Wildcard(identifier);
        }

        /** This implementation retrns a {@link Wildcard}. */
        @Override
        protected Constant newInstance() {
            return new Wildcard();
        }
        
        /** The (optional) identifier for this wildvard. */
        private String identifier;
    }

    /**
     * Constant expression that stands for all reflexive pairs.
     */
    static public class Empty extends Constant {
    	/** Creates an instance of this expression. */
        public Empty() {
            super("" + EMPTY_OPERATOR, EMPTY_SYMBOLIC_NAME);
        }

        /** This implementation retrns a {@link Empty}. */
        @Override
        protected Constant newInstance() {
            return new Empty();
        }
        
        /**
         * Calls {@link RegExprCalculator#computeEmpty(RegExpr.Empty)} on the visitor.
         */
        @Override
        public <Result> Result apply(RegExprCalculator<Result> calculator) {
            return calculator.computeEmpty(this);
        }
    }

    /**
     * Constant expression that stands for a fixed symbol.
     * The symbol is know as the <i>text</i> of the atom.
     */
    static public class Atom extends Constant {
        /**
         * Creates a new atomic expression, based on a given text.
         * @param token the text to create the atom from
         * @require <tt>isAtom(token)</tt>
         */
        public Atom(String token) {
            super("", ATOM_SYMBOLIC_NAME);
            this.text = token;
        }

        /**
         * Creates a prototye regular expression.
         */
        private Atom() {
            this("");
        }

        /**
         * Puts single quotes around the atom text if it could otherwise be parsed as something
         * else.
         */
        @Override
        public String toString() {
            if (isAtom(text())) {
                // the atom text can be understood as is
                return text();
            } else {
                // the atom text looks like something else if we parse it as is
                return ExprParser.toQuoted(text(), SINGLE_QUOTE_CHAR);
            }
        }

        @Override
        public String getDescription() {
            return text;
        }

        /**
         * Returns the bare text of the atom.
         */
        public String text() {
            return text;
        }

        /**
         * This implementation never returns <tt>null</tt>, since it is assumed to be at the end
         * of the chain of prototyes tried out during parsing.
         * @throws FormatException if <tt>tokenList</tt> is not a singleton or its element is
         *         not recognized as a nested expression or atom
         */
        @Override
        public RegExpr parseOperator(String expr) throws FormatException {
        	expr = expr.trim();
        	if (expr.length() == 0) {
        		throw new FormatException("Empty string not allowed in expression");
        	}
        	// the only hope is that the expression is quoted or bracketed
        	Pair<String,List<String>> parseResult = ExprParser.parseExpr(expr);
        	if (parseResult.first().length() == 1 && parseResult.first().charAt(0) == PLACEHOLDER) {
        		String parsedExpr = parseResult.second().get(0);
        		switch (parsedExpr.charAt(0)) {
				case LPAR_CHAR:
					return parse(parsedExpr.substring(1, expr.length()-1));
				case SINGLE_QUOTE_CHAR:
					return newInstance(ExprParser.toUnquoted(parsedExpr, SINGLE_QUOTE_CHAR));
				case LANGLE_CHAR:
				case DOUBLE_QUOTE_CHAR:
					return newInstance(parsedExpr);
				default:
					return null;
				}
        	} else if (isAtom(expr)) {
            	return newInstance(expr);
            } else {
        		// the expression is not atomic when parsed
        		return null;
        	}
        }

        /**
         * Required factory method from {@link Constant}.
         * @throws UnsupportedOperationException always
         */
        @Override
        protected Constant newInstance() {
            throw new UnsupportedOperationException("Atom instances must have a parameter");
        }

        /**
         * Factory method: creates a new atomic regular expression, from a given text. Does not test
         * for proper atom format.
         */
        protected Atom newInstance(String text) throws FormatException {
            return new Atom(text);
        }
        
        /**
         * Calls {@link RegExprCalculator#computeAtom(RegExpr.Atom)} on the visitor.
         */
        @Override
        public <Result> Result apply(RegExprCalculator<Result> calculator) {
            return calculator.computeAtom(this);
        }

        /** The text of the atom. */
        private final String text;
    }

    /**
     * Postfix operator standing for a repetition of its
     * operand of zero or more occurrences.
     * @see Plus
     */
    static public class Star extends Postfix {
    	/** Creates the repetition of a given regular expression. */
        public Star(RegExpr operand) {
            super("" + STAR_OPERATOR, STAR_SYMBOLIC_NAME, operand);
        }

        /** Creates a prototype instance. */
        private Star() {
            this(null);
        }

        @Override
        protected Postfix newInstance(RegExpr operand) {
            return new Star(operand);
        }

        /**
         * Calls {@link RegExprCalculator#computeStar(RegExpr.Star, Object)} on the visitor.
         */
        @Override
        protected <Result> Result applyPostfix(RegExprCalculator<Result> visitor, Result arg) {
            return visitor.computeStar(this, arg);
        }
    }

    /**
     * Postfix operator standing for a repetition of its
     * operand of at least one occurrence.
     * @see Star
     */
    static public class Plus extends Postfix {
    	/** Creates a non-empty repetition of a given regular expression. */
            public Plus(RegExpr operand) {
                super("" + PLUS_OPERATOR, PLUS_SYMBOLIC_NAME, operand);
            }
    
            /** Creates a prototype instance. */
            private Plus() {
                this(null);
            }
    
            @Override
            protected Postfix newInstance(RegExpr operand) {
                return new Plus(operand);
            }
    
            /**
             * Calls {@link RegExprCalculator#computePlus(RegExpr.Plus, Object)} on the visitor.
             */
            @Override
            protected <Result> Result applyPostfix(RegExprCalculator<Result> visitor, Result arg) {
                return visitor.computePlus(this, arg);
            }
        }

    /**
     * Inversion is a prefix operator standing for a backwards
     * interpretation of its operand.
     * @see Neg
     */
    static public class Inv extends Prefix {
    	/** Creates the inversion of a given regular expression. */
        public Inv(RegExpr operand) {
            super("" + INV_OPERATOR, INV_SYMBOLIC_NAME, operand);
        }

        /** Creates a prototype instance. */
        private Inv() {
            this(null);
        }

        @Override
        protected Prefix newInstance(RegExpr operand) {
            return new Inv(operand);
        }

        /**
         * Calls {@link RegExprCalculator#computeInv(RegExpr.Inv, Object)} on the visitor.
         */
        @Override
        protected <Result> Result applyPrefix(RegExprCalculator<Result> visitor, Result arg) {
            return visitor.computeInv(this, arg);
        }
    }

    /**
     * Negation is a prefix operator; the resulting expression applies
     * everywhere where the operand does not apply.
     * @see Inv
     */
    static public class Neg extends Prefix {
    	/** Creates the negation of a given regular expression. */
        public Neg(RegExpr operand) {
            super(NEG_OPERATOR, NEG_SYMBOLIC_NAME, operand);
        }

        /** Creates a prototype instance. */
        private Neg() {
            this(null);
        }

        @Override
        protected Prefix newInstance(RegExpr operand) {
            return new Neg(operand);
        }

        /**
         * Calls {@link RegExprCalculator#computeNeg(RegExpr.Neg, Object)} on the visitor.
         */
        @Override
        protected <Result> Result applyPrefix(RegExprCalculator<Result> visitor, Result arg) {
            return visitor.computeNeg(this, arg);
        }
    }

    /**
     * Parses a given string as a regular expression.
     * Throws an exception if the parsing does not succeed.
     * @param expr the string to be parsed 
     * @return a regular expression which, when turned back into a string,
     * equals <code>expr</code>
     * @throws FormatException if <code>expr</code> cannot be parsed
     */
    static public RegExpr parse(String expr) throws FormatException {
    	// first test if the quoting and bracketing is correct
    	ExprParser.parseExpr(expr);
        // try to parse the expression using each of the available operators in turn
        for (RegExpr prototype: prototypes) {
            RegExpr result = prototype.parseOperator(expr);
            // if the result is non-null, we are done
            if (result != null) {
                return result;
            }
        }
        throw new FormatException("Unable to parse expression %s", expr);
    }

    /** Creates and returns an atomic regular expression with a given atom text. */
    public static Atom atom(String text) {
        return new Atom(text);
    }

    /**
     * Creates and returns a wildcard regular expression.
     */
    public static Wildcard wildcard() {
        return new Wildcard();
    }

    /**
     * Creates and returns a named wildcard regular expression.
     */
    public static Wildcard wildcard(String text) {
        return new Wildcard(text);
    }

    /**
     * Creates and returns an empty regular expression.
     */
    public static Empty empty() {
        return new Empty();
    }
    
    /** Helper method for a test if this class. */
    static private void test(String text) {
        try {
            System.out.println("Input: " + text);
            System.out.println("Output: " + parse(text));
            System.out.println("Description: " + parse(text).getDescription());
        } catch (FormatException e) {
            System.out.println("Error:  " + e.getMessage());
        }
    }

    /** Tests this class. */
    static public void main(String[] args) {
        test("");
        test("?");
        test("a|b");
        test("|b");
        test("*");
        test("((a).(b))*");
        test("((a)*|b)+");
        test("?.'b.c'. 'b'. \"c\". (d*)");
        test("a+*");
        test("a.?*");
        test("((a)");
        test("(<a)");
        test("(a . b)* .c. d|e*");
        test("=. b|c*");
        test("!a*");
        test("!a.b | !(a.!b)");
        test("?ab");
    }

    /**
     * An array of propotype regular expressions, in order of increasing priority. In particular,
     * atoms that have special meaning should come before the {@link Atom}.
     */
    static private final RegExpr[] prototypes = new RegExpr[] { new Atom(), new Choice(),
        new Seq(),
        new Neg(),
            new Star(), new Plus(), new Wildcard(), new Empty(), 
            new Inv()};

    /**
     * The list of operators into which a regular expression will be parsed, in order of increasing
     * priority.
     */
    static private final List<String> operators;

    static {
        List<String> result = new LinkedList<String>();
        for (int op = 0; op < prototypes.length; op++) {
            RegExpr prototype = prototypes[op];
            if (!(prototype instanceof Atom)) {
                result.add(prototype.getOperator());
            }
        }
        operators = result;
    }

    /** 
     * Constructs a regular expression with a given operator name
     * and operator symbol.
     * This constructor is there for subclassing purposes.
     */
    protected RegExpr(String operator, String symbol) {
        this.operator = operator;
        this.symbol = symbol;
    }
    
    /** Tests if this is a {@link RegExpr.Atom}. */
    public boolean isAtom() {
        return getAtomText() != null;
    }

    /** 
     * If this is a {@link RegExpr.Atom},
     * returns the text of the atom; 
     * otherwise returns <code>null</code>.
     */
    public String getAtomText() {
        if (this instanceof Atom) {
            return ((Atom) this).text();
        } else {
            return null;
        }
    }

    /** Tests if this is a {@link RegExpr.Empty}. */
    public boolean isEmpty() {
        return this instanceof Empty;
    }

    /** Tests if this is {@link RegExpr.Wildcard}. */
    public boolean isWildcard() {
        return this instanceof Wildcard;
    }

    /** 
     * If this is a {@link RegExpr.Wildcard},
     * returns the identifier of the wildcard, if any; 
     * otherwise returns <code>null</code>.
     */
    public String getWildcardId() {
        if (this instanceof Wildcard) {
            return ((Wildcard) this).getIdentifier();
        } else {
            return null;
        }
    }

    /** Tests if this is a {@link RegExpr.Choice}. */
    public boolean isChoice() {
        return this instanceof Choice;
    }

    /** 
     * If this is a {@link RegExpr.Choice},
     * returns the list of operands of the regular expression; 
     * otherwise returns <code>null</code>.
     */
    public List<RegExpr> getChoiceOperands() {
        if (this instanceof Choice) {
            return ((Choice) this).getOperands();
        } else {
            return null;
        }
    }

    /** Tests if this is a {@link RegExpr.Seq}. */
    public boolean isSeq() {
        return this instanceof Seq;
    }

    /** 
     * If this is a {@link RegExpr.Seq},
     * returns the list of operands of the regular expression; 
     * otherwise returns <code>null</code>.
     */
    public List<RegExpr> getSeqOperands() {
        if (this instanceof Seq) {
            return ((Seq) this).getOperands();
        } else {
            return null;
        }
    }

    /** Tests if this is a {@link RegExpr.Star}. */
    public boolean isStar() {
        return this instanceof Star;
    }

    /** 
     * If this is a {@link RegExpr.Star},
     * returns the operand of the regular expression; 
     * otherwise returns <code>null</code>.
     */
    public RegExpr getStarOperand() {
        if (this instanceof Star) {
            return ((Star) this).getOperand();
        } else {
            return null;
        }
    }

    /** Tests if this is {@link RegExpr.Plus}. */
    public boolean isPlus() {
        return this instanceof Plus;
    }

    /** 
     * If this is a {@link RegExpr.Plus},
     * returns the operand of the regular expression; 
     * otherwise returns <code>null</code>.
     */
    public RegExpr getPlusOperand() {
        if (this instanceof Plus) {
            return ((Plus) this).getOperand();
        } else {
            return null;
        }
    }

    /** Tests if this is a {@link RegExpr.Inv}. */
    public boolean isInv() {
        return this instanceof Inv;
    }

    /** 
     * If this is a {@link RegExpr.Inv},
     * returns the operand of the regular expression; 
     * otherwise returns <code>null</code>.
     */
    public RegExpr getInvOperand() {
        if (this instanceof Inv) {
            return ((Inv) this).getOperand();
        } else {
            return null;
        }
    }

    /** Tests if this is a {@link RegExpr.Neg}. */
    public boolean isNeg() {
        return this instanceof Neg;
    }

    /** 
     * If this is a {@link RegExpr.Neg},
     * returns the operand of the regular expression; 
     * otherwise returns <code>null</code>.
     */
    public RegExpr getNegOperand() {
        if (this instanceof Neg) {
            return ((Neg) this).getOperand();
        } else {
            return null;
        }
    }

    /**
     * Creates and returns the choice composition of this regular expression
     * and another.
     * If the other is already a choice regular expression, flattens it into
     * a single level.
     */
    public Choice choice(RegExpr other) {
        if (other instanceof Choice) {
            List<RegExpr> operands = new ArrayList<RegExpr>();
            operands.add(this);
            operands.addAll(other.getOperands());
            return new Choice(operands);
        } else {
            return new Choice(Arrays.asList(new RegExpr[] {this, other}));
        }
    }
    
    /**
     * Creates and returns the sequential composition of this regular expression
     * and another.
     * If the other is already a sequential regular expression, flattens it into
     * a single level.
     */
    public Seq seq(RegExpr other) {
        if (other instanceof Choice) {
            List<RegExpr> operands = new ArrayList<RegExpr>();
            operands.add(this);
            operands.addAll(other.getOperands());
            return new Seq(operands);
        } else {
            return new Seq(Arrays.asList(new RegExpr[] {this, other}));
        }
    }
    
    /**
     * Creates and returns a star regular expression (zero or more occurrences) 
     * with this one as its operand.
     */
    public Star star() {
        return new Star(this);
    }
    
    /**
     * Creates and returns a plus regular expression (one or more occurrences)
     * with this one as its operand.
     */
    public Plus plus() {
        return new Plus(this);
    }
    
    /**
     * Creates and returns an inversion of this regular expression.
     */
    public Inv inv() {
        return new Inv(this);
    }
    
    /**
     * Creates and returns the negation of this regular expression.
     */
    public Neg neg() {
        return new Neg(this);
    }
    
    /**
     * Tests if this expression contains a given operator (given by its string
     * representation) in one of its sub-expressions.
     * @param operator the string description of the operator sought
     */
    public boolean containsOperator(String operator) {
        boolean found = false;
        Iterator<RegExpr> operandIter = getOperands().iterator();
        while (!found && operandIter.hasNext()) {
            RegExpr operand = operandIter.next();
            found = operand.isMyOperator(operator) || operand.containsOperator(operator);
        }
        return found;
    }

    /**
     * Tests if this expression contains the top-level operator of another expression in one of its
     * sub-expressions.
     * @param operator the expression of which we are seeing the top-level operator
     */
    public boolean containsOperator(RegExpr operator) {
        return containsOperator(operator.getOperator());
    }

    /**
     * Returns the set of all variables occurring as identifiers in 
     * {@link Wildcard}-subexpressions, in the order of the sub-expressions.
     */
    public Set<String> allVarSet() {
        // by making a linked set we make sure the order is preserved
        // and yet no identifier occurs more than once
        Set<String> result = new LinkedHashSet<String>();
        if (getWildcardId() != null) {
            result.add(getWildcardId());
        } else {
        	for (RegExpr operand: getOperands()) {
                result.addAll(operand.allVarSet());
            }
        }
        return result;
    }

    public boolean hasVar(String var) {
        if (getWildcardId() != null) {
            return getWildcardId().equals(var);
        } else {
            boolean result = false;
            Iterator<RegExpr> operands = getOperands().iterator();
            while (!result && operands.hasNext()) {
                RegExpr operand = operands.next();
                result = operand.hasVar(var);
            }
            return result;
        }
    }

    public boolean hasVars() {
        if (getWildcardId() != null) {
            return true;
        } else {
            boolean result = false;
            Iterator<RegExpr> operands = getOperands().iterator();
            while (!result && operands.hasNext()) {
                RegExpr operand = operands.next();
                result = operand.hasVars();
            }
            return result;
        }
    }

    /**
     * Returns the list of variables <i>bound</i> by this regular expression.
     * A variable is bound if the expression cannot be matched without providing
     * a value for it.
     * @see #allVarSet()
     */
    public Set<String> boundVarSet() {
        Set<String> result = new LinkedHashSet<String>();
        if (getWildcardId() != null) {
            result.add(getWildcardId());
        } else if (isChoice()) {
            Iterator<RegExpr> operands = getOperands().iterator();
            RegExpr operand = operands.next();
            result.addAll(operand.boundVarSet());
            while (operands.hasNext()) {
                operand = operands.next();
                result.retainAll(operand.boundVarSet());
            }
        } else if (! isStar()) {
        	for (RegExpr operand: getOperands()) {
                result.addAll(operand.boundVarSet());
            }
        }
        return result;
    }
    
    public boolean bindsVar(String var) {
        return boundVarSet().contains(var);
    }

    /**
     * Returns the (plain text) denotation for the operator in this class, as set in the
     * constructor.
     * @return the denotation for the operator in this class
     */
    public String getOperator() {
        return operator;
    }

    /**
     * Returns a textual description of this regular expression.
     * This implementation returns the symbolic name (see {@link #getSymbol()}
     * followed by the descriptions of the operands between square brackets, if any.
     */
    public String getDescription() {
        StringBuffer result = new StringBuffer(getSymbol());
        Iterator<RegExpr> operandIter = getOperands().iterator();
        if (operandIter.hasNext()) {
            result.append('[');
            while (operandIter.hasNext()) {
                RegExpr operand = operandIter.next();
                result.append(operand.getDescription());
                if (operandIter.hasNext()) {
                    result.append(", ");
                }
            }
            result.append(']');
        }
        return result.toString();
    }

    /**
     * Returns the symbolc name for the type of expression in this class, as set in the
     * constructor.
     */
    public String getSymbol() {
        return symbol;
    }

    /** Tests for equality of the {@link #toString()} results. */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof RegExpr && toString().equals(obj.toString());
	}

	/** Returns a label based on this expression. */
	public RegExprLabel toLabel() {
		if (label == null) {
			label = new RegExprLabel(this);
		}
		return label;
	}
	
	/**
	 * Returns the hash code of the {@link #toString()} method,
	 * combined with a bit pattern derived from the {@link RegExpr} class.
	 */
	@Override
	public int hashCode() {
		return System.identityHashCode(RegExpr.class) ^ toString().hashCode();
	}

	/**
     * Returns a list of {@link RegExpr}s that are the operands of this regular expression.
     */
    abstract public List<RegExpr> getOperands();

    /**
     * Accept method for a calculator.
     * @param calculator the calculator
     * @return the return value of the calculation
     */
    public abstract <Result> Result apply(RegExprCalculator<Result> calculator);
    
    /**
     * Creates and returns a regular expression from a string. An implementation should check the
     * string using its own syntax rules. If the string does not look like an expression of the
     * right kind, the function should return <tt>null</tt>; if it looks correct but is malformed
     * (e.g., the correct operator is there but the operands are missing) the function should raise
     * an exception.
     * @param expr the expression to be parsed; this is guaranteed to have correct bracketing 
     * and quoting (according to {@link ExprParser#parseExpr(String)}).
     * @return a valid regular expression, or <tt>null</tt> if <tt>expr</tt> does not appear to
     *         be a regular expression of the kind implemented by this class
     * @throws FormatException if <tt>expr</tt> appears to be an expression (of the kind
     *         implemented by the class) but is malformed
     */
    abstract protected RegExpr parseOperator(String expr) throws FormatException;

    /**
     * Tests whether a given text may be regarded as an atom, according to the rules of regular
     * expressions. (If not, then it should be single-quoted.) This implementation throws an
     * exception if the text is empty contains any characters not allowed by {@link #isIdentifierChar(char)}.
     * @param text the text to be tested
     * @throws FormatException if the text contains a special character
     * @see #isAtom(String)
     */
    static public void assertAtom(String text) throws FormatException {
    	if (ExprParser.toUnquoted(text, SINGLE_QUOTE_CHAR) == null) {
    		boolean correct = true;
			int i;
			for (i = 0; correct && i < text.length(); i++) {
				correct = isAtomChar(text.charAt(i));
			}
			if (!correct) {
				throw new FormatException("Atom '%s' contains invalid character '%c'", text, text
						.charAt(i - 1));
			}
		}
    }

    /**
     * Tests whether a given text may be regarded as an atom, according to the rules of regular
     * expressions. If not, then it should be single-quoted. If <tt>true</tt>, the text will be
     * parsed by {@link #parse(String)}as an {@link Atom}. This implementation returns
     * <tt>true</tt> if the text does not contain any special characters
     * @param text the text to be tested
     * @return <tt>true</tt> if the text does not contain any special characters
     * @see #assertAtom(String)
     */
    static public boolean isAtom(String text) {
        try {
            assertAtom(text);
            return true;
        } catch (FormatException exc) {
            return false;
        }
    }

    /**
     * Tests whether a given text can serve as a wildcard identifier.
     * This implementation returns <code>true</code> if <code>text</code> is non-empty
     * and contains
     * only letters or digits (accordingto {@link Character#isLetterOrDigit(char)})
     * and characters from {@link #IDENTIFIER_CHARS}.
     * @param text the text to be tested
     * @return <tt>true</tt> if the text does not contain any special characters
     */
    static public boolean isIdentifier(String text) {
        if (text.length() == 0) {
            return false;
        }
        for (int i = 0; i < text.length(); i++) {
            char nextChar = text.charAt(i);
            if (!isIdentifierChar(nextChar)) {
                return false;
            }
        }
        return true;
    }

    /** Tests if a character may occur in an atom. */
    static public boolean isAtomChar(char c) {
    	return Character.isLetterOrDigit(c) || ATOM_CHARS.indexOf(c) >= 0;
    }

    /** Tests if a character may occur in a wildcard identifier. */
    static public boolean isIdentifierChar(char c) {
    	return Character.isLetterOrDigit(c) || IDENTIFIER_CHARS.indexOf(c) >= 0;
    }
    
    /**
     * Tests if a given opject equals the operator of this regular expression class.
     */
    protected boolean isMyOperator(Object token) {
        return getOperator().equals(token);
    }

    /**
     * Indicates the priority of operators.
     */
    protected boolean bindsWeaker(String operator1, String operator2) {
        return operators.indexOf(operator1) <= operators.indexOf(operator2);
    }

    /**
     * Indicates the priority of operators.
     */
    protected boolean bindsWeaker(RegExpr operator1, RegExpr operator2) {
        if (operator2 instanceof Constant) {
            return true;
        } else if (operator1 instanceof Constant) {
            return false;
        } else {
        return bindsWeaker(((Composite) operator1).getOperator(), ((Composite) operator2).getOperator());
        }
    }

    /**
     * The operator of this expression.
     */
    private final String operator;

    /**
     * The symbolic name operator of this kind of expression.
     */
    private final String symbol;
    /**
     * A regular expression label based on this expression.
     */
    private RegExprLabel label;
}