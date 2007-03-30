/*
 * GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id: CTLStarFormula.java,v 1.2 2007-03-30 15:50:41 rensink Exp $
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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Class parsing CTL* formulae.
 * 
 * @author Harmen Kastenberg
 * @version $Revision: 1.2 $ $Date: 2007-03-30 15:50:41 $
 */
public class CTLStarFormula {
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
    abstract static class Prefix extends TemporalFormula {

    	/**
    	 * Creates a new prefix CTL-expression.
    	 * @param operator the operator of the CTL-expression
    	 * @param symbol the symbol of the outer CTL-expression
    	 * @param operand the single operand on which this operator will be applied
    	 */
    	public Prefix(String operator, String symbol, TemporalFormula operand) {
            super(operator, symbol);
            this.operand = operand;
            this.operandList = Collections.singletonList(operand);
        }

        @Override
        public List<TemporalFormula> getOperands() {
            return Collections.unmodifiableList(operandList);
        }

        @Override
        protected TemporalFormula parseOperator(String expr) throws ExprFormatException {
            String[] operands = ExprParser.splitExpr(expr, getOperator(), ExprParser.PREFIX_POSITION);
            if (operands == null) {
                return null;
            }
            return newInstance(getFactory().parse(operands[0]));
        }

        /**
         * Creates a new instance of a specific CTL-operator given
         * the sub-formula on which it will be applied
         * @param operand the operand on which this CTL-operator will
         * be applied
         * @return a new instance of a specific CTL-expression
         */
        abstract protected TemporalFormula newInstance(TemporalFormula operand) throws ExprFormatException;

        @Override
        public String toString() {
        	return getOperator() + "(" + operand.toString() + ")";
        }

        /**
         * Comment for <code>operand</code>
         */
        private final TemporalFormula operand;

        /**
         * List of operand of this CTL-operator
         */
        private final List<TemporalFormula> operandList;
    }

    /**
     * Abstract class for infix CTL operators.
     */
    abstract static protected class Infix extends TemporalFormula {

    	/**
    	 * Creates a new infix CTL-expression.
    	 * @param operator the operator of the CTL-expression
    	 * @param symbol the symbol of the outer CTL-expression
    	 * @param operands the list of operands on which this operator will be applied
    	 */
    	public Infix(String operator, String symbol, List<TemporalFormula> operands) {
            super(operator, symbol);
            this.operandList.addAll(operands);
        }

        @Override
        public List<TemporalFormula> getOperands() {
            return Collections.unmodifiableList(operandList);
        }

        @Override
        protected TemporalFormula parseOperator(String expr) throws ExprFormatException {
            String[] operands = ExprParser.splitExpr(expr, getOperator(), ExprParser.INFIX_POSITION);
            if (operands.length < 2) {
                return null;
            }
            List<TemporalFormula> operandList = new LinkedList<TemporalFormula>();
            for (int i = 0; i < operands.length; i++) {
                operandList.add(getFactory().parse(operands[i]));
            }
            return newInstance(operandList);
        }

        /**
         * Create a new instance of a specific infix-operator.
         * 
         * @param operands the list of operands of this operation.
         * @return the resulting CTL-expression.
         * @throws ExprFormatException if the expression does not conform
         * structural requirements
         */
        abstract protected TemporalFormula newInstance(List<TemporalFormula> operands) throws ExprFormatException;

        @Override
        public String toString() {
        	String operandsString = operandList.get(0).toString();
        	for (int i = 1; i < operandList.size(); i++) {
        		operandsString = operandsString + " " + getOperator() + " " + operandList.get(i);
        	}
        	return operandsString;
        }

        /**
         * List of operands.
         */
        private final List<TemporalFormula> operandList = new ArrayList<TemporalFormula>();
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
    	public Neg(TemporalFormula operand) {
            super("" + NEGATION_OPERATOR, NEGATION_SYMBOLIC_NAME, operand);
        }

        /**
         * Empty constructor.
         */
        protected Neg() {
            this(null);
        }

        @Override
        protected TemporalFormula newInstance(TemporalFormula operand) throws ExprFormatException {
        	return FACTORY.createNeg(operand);
        }

        /**
         * Creates a fresh instance of the logical negation-operation.
         * @param operand the operand of the negation-operation
         * @return a fresh Neg-instance
         */
        static protected TemporalFormula createInstance(TemporalFormula operand) {
            return new Neg(operand);
        }

        @Override
        public void mark(CTLFormulaMarker marker, Marking marking, GTS gts) {
        	marker.markNeg(marking, this, gts);
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
    	public And(List<TemporalFormula> operands) {
            super("" + AND_OPERATOR, AND_SYMBOLIC_NAME, operands);
        }

        /**
         * Empty constructor.
         */
    	protected And() {
            this(new ArrayList<TemporalFormula>());
        }

        @Override
    	protected TemporalFormula newInstance(List<TemporalFormula> operands) throws ExprFormatException {
    		return FACTORY.createAnd(operands);
    	}

        /**
         * @param operands
         * @return a fresh AND-instance
         */
        static protected TemporalFormula createInstance(List<TemporalFormula> operands) {
        	return new And(operands);
        }

        @Override
        public void mark(CTLFormulaMarker marker, Marking marking, GTS gts) {
        	marker.markAnd(marking, this, gts);
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
    	public Or(List<TemporalFormula> operands) {
            super("" + OR_OPERATOR, OR_SYMBOLIC_NAME, operands);
        }

        /**
         * Constructor.
         */
    	protected Or() {
            this(new ArrayList<TemporalFormula>());
        }

        @Override
        protected TemporalFormula newInstance(List<TemporalFormula> operands) throws ExprFormatException {
        	return FACTORY.createOr(operands);
        }

        /**
         * Creates a fresh instance of the logical or-operator.
         * @param operands the operands for the or-operator
         * @return the freshly created or-operator
         */
        static protected TemporalFormula createInstance(List<TemporalFormula> operands) {
        	return new Or(operands);
        }

        @Override
        public void mark(CTLFormulaMarker marker, Marking marking, GTS gts) {
        	marker.markOr(marking, this, gts);
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
    	public Next(TemporalFormula operand) {
            super("" + NEXT_OPERATOR, NEXT_SYMBOLIC_NAME, operand);
        }

        /**
         * Constructor. 
         */
    	protected Next() {
            this(null);
        }

        @Override
        protected TemporalFormula newInstance(TemporalFormula operand) throws ExprFormatException {
            return FACTORY.createNext(operand);
        }

        /**
         * Creates a fresh instance of the temporal next-operator.
         * @param operand the operands for the next-operator
         * @return the freshly created next-operator
         */
        static protected TemporalFormula createInstance(TemporalFormula operand) {
            return new Next(operand);
        }

        @Override
        public void mark(CTLFormulaMarker marker, Marking marking, GTS gts) {
        	marker.markNext(marking, this, gts);
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
    	public Until(List<TemporalFormula> operands) {
            super("" + UNTIL_OPERATOR, UNTIL_SYMBOLIC_NAME, operands);
        }

        /**
         * Constructor. 
         */
    	protected Until() {
            this(new ArrayList<TemporalFormula>());
        }

        @Override
        protected TemporalFormula newInstance(List<TemporalFormula> operands) throws ExprFormatException {
        	TemporalFormula result = FACTORY.createUntil(operands);
            return result;
        }

        /**
         * Creates a fresh instance of the temporal until-operator.
         * @param operands the operands for the until-operator
         * @return the freshly created until-operator
         */
        static protected TemporalFormula createInstance(List<TemporalFormula> operands) {
        	TemporalFormula result = new Until(operands);
        	return result;
        }

        @Override
        public void mark(CTLFormulaMarker marker, Marking marking, GTS gts) {
        	marker.markUntil(marking, this, gts);
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
    	public Globally(TemporalFormula operand) {
            super("" + ALWAYS_OPERATOR, ALWAYS_SYMBOLIC_NAME, operand);
        }

        /**
         * Constructor. 
         */
    	protected Globally() {
            this(null);
        }

        @Override
        protected TemporalFormula newInstance(TemporalFormula operand) throws ExprFormatException {
            return FACTORY.createGlobally(operand);
        }

        /**
         * Creates a fresh instance of the temporal globally-operator.
         * @param operand the operands for the globally-operator
         * @return the freshly created globally-operator
         */
        static protected TemporalFormula createInstance(TemporalFormula operand) {
        	return new Globally(operand);
        }

        @Override
        public void mark(CTLFormulaMarker marker, Marking marking, GTS gts) {
        	marker.markGlobally(marking, this, gts);
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
    	public Finally(TemporalFormula operand) {
            super("" + FINALLY_OPERATOR, FINALLY_SYMBOLIC_NAME, operand);
        }

        /**
         * Constructor.
         */
    	protected Finally() {
            this(null);
        }

        @Override
        protected TemporalFormula newInstance(TemporalFormula operand) throws ExprFormatException {
            return FACTORY.createFinally(operand);
        }

        /**
         * Creates a fresh instance of the temporal finally-operator.
         * @param operand the operands for the finally-operator
         * @return the freshly created finally-operator
         */
        static protected TemporalFormula createInstance(TemporalFormula operand) {
        	return new Finally(operand);
        }

        @Override
        public void mark(CTLFormulaMarker marker, Marking marking, GTS gts) {
        	marker.markFinally(marking, this, gts);
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
    	public Exists(TemporalFormula operand) {
            super("" + EXISTS_OPERATOR, EXISTS_SYMBOLIC_NAME, operand);
        }

        /**
         * Constructor. 
         */
        protected Exists() {
            this(null);
        }

        @Override
        protected TemporalFormula newInstance(TemporalFormula operand) throws ExprFormatException {
        	return FACTORY.createExists(operand);
        }

        /**
         * Creates a fresh instance of the temporal exists-operator.
         * @param operand the operands for the exists-operator
         * @return the freshly created exists-operator
         */
        static protected TemporalFormula createInstance(TemporalFormula operand) {
        	return new Exists(operand);
        }

        @Override
        public void mark(CTLFormulaMarker marker, Marking marking, GTS gts) {
        	marker.markExists(marking, this, gts);
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
    	public All(TemporalFormula operand) {
            super("" + ALL_OPERATOR, ALL_SYMBOLIC_NAME, operand);
        }

        /**
         * Constructor.
         */
    	protected All() {
            this(null);
        }

        @Override
        protected TemporalFormula newInstance(TemporalFormula operand) throws ExprFormatException {
        	return getFactory().createAll(operand);
        }

        /**
         * Creates a fresh instance of the temporal all-operator.
         * @param operand the operands for the all-operator
         * @return the freshly created all-operator
         */
        static protected TemporalFormula createInstance(TemporalFormula operand) {
        	return new All(operand);
        }

        @Override
        public void mark(CTLFormulaMarker marker, Marking marking, GTS gts) {
        	marker.markAll(marking, this, gts);
        }
    }

    /**
     * Atomic proposition.
     * Example syntax: "empty" or "yellow" or "p"
     */
    static public class Atom extends TemporalFormula {
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
        protected Atom() {
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

        @Override
        protected TemporalFormula parseOperator(String expr) throws ExprFormatException {
            if (ExprParser.matches(expr, LEFT_PARENTHESIS_CHAR, RIGHT_PARENTHESIS_CHAR)) {
                return getFactory().parse(ExprParser.trim(expr, LEFT_PARENTHESIS_CHAR, RIGHT_PARENTHESIS_CHAR));
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
         * @throws ExprFormatException if the expression does not conform
         * structural requirements
         */
        protected TemporalFormula newInstance(String proposition) throws ExprFormatException {
            return FACTORY.createAtom(proposition);
        }

        /**
         * Creates a fresh instance of an atomic proposition.
         * @param name the name of the atomic proposition
         * @return the freshly created atomic proposition
         */
        static protected TemporalFormula createInstance(String name) {
        	return new Atom(name);
        }

        @Override
        public List<TemporalFormula> getOperands() {
            return Collections.emptyList();
        }

        @Override
        public String toString() {
        	return predicateName();
        }

        /**
         * Returns the graph condition corresponding to this atom.
         * @return the graph condition corresponding to this atom
         */
        public GraphCondition graphCondition() {
            return graphCondition;
        }

        @Override
        public void mark(CTLFormulaMarker marker, Marking marking, GTS gts) {
        	marker.markAtom(marking, this, gts);
        }

        /**
         * Comment for <code>predicateName</code>
         */
        private final String predicateName;
        /**
         * The graph-predicate representing this CTL-expression.
         */
        protected GraphCondition graphCondition;
    }

    /**
     * TRUE atomic proposition which always holds.
     * Syntax: "true" @see {@link CTLStarFormula#TRUE}
     */
    static public class True extends Atom {

    	/**
    	 * Constructor.
    	 */
    	public True() {
            super(TRUE);
        }

        @Override
        public void mark(CTLFormulaMarker marker, Marking marking, GTS gts) {
        	marker.markTrue(marking, this, gts);
        }
    }

    /**
     * FALSE atomic proposition which never holds.
     * Syntax: "false" @see {@link CTLStarFormula#FALSE}
     */
    static public class False extends Atom {

    	/**
    	 * Constructor.
    	 */
    	public False() {
            super(FALSE);
        }

        @Override
        public void mark(CTLFormulaMarker marker, Marking marking, GTS gts) {
        	marker.markFalse(marking, this, gts);
        }
    }

    /**
     * Comment for <code>prototypes</code>
     */
    protected final TemporalFormula[] prototypes = new TemporalFormula[] {
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
     * Parses the string given to this method. It throws a {@link ExprFormatException}
     * if the string does not represent a valid CTL-expression.
     * 
     * @param expr the string to be parsed
     * @return the {@link CTLStarFormula} corresponding to the given expression
     * @throws ExprFormatException if the string does not represent a valid CTL-expression
     */
    static public TemporalFormula parseFormula(String expr) throws ExprFormatException {
    	CTLStarFormula parser = CTLStarFormula.getInstance();
    	TemporalFormula.setFactory(parser);
    	return parser.parse(expr);
    }

    protected TemporalFormula parse(String expr) throws ExprFormatException {
        // try to parse the expression using each of the available operators in turn
        for (int op = 0; op < prototypes.length; op++) {
            TemporalFormula result = prototypes[op].parseOperator(expr);
            // if the result is non-null, we are done
            if (result != null) {
                return result;
            }
        }
        return null;
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
                State predecessorState = nextTransition.source();
                result.add(predecessorState);
            }
        }
        return result.iterator();
    }



    /**
     * Creates a fresh atomic proposition.
     * @param name the name of the atomic proposition
     * @return the freshly created atomic proposition
     * @throws ExprFormatException if the expression does not conform
     * structural requirements
     */
    protected TemporalFormula createAtom(String name) throws ExprFormatException {
    	return Atom.createInstance(name);
    }

    /**
     * Creates a fresh instance of the logic neg-operator.
     * @param operand the operands of the neg-operator
     * @return the freshly created neg-instance
     * @throws ExprFormatException if the expression does not conform
     * structural requirements
     */
    protected TemporalFormula createNeg(TemporalFormula operand) throws ExprFormatException {
    	return Neg.createInstance(operand);
    }

    /**
     * Creates a fresh instance of the logic and-operator.
     * @param operands the operands of the and-operator
     * @return the freshly created and-instance
     * @throws ExprFormatException if the expression does not conform
     * structural requirements
     */
    protected TemporalFormula createAnd(List<TemporalFormula> operands) throws ExprFormatException {
    	return  And.createInstance(operands);
    }

    /**
     * Creates a fresh instance of the logic or-operator.
     * @param operands the operands of the or-operator
     * @return the freshly created or-instance
     * @throws ExprFormatException if the expression does not conform
     * structural requirements
     */
    protected TemporalFormula createOr(List<TemporalFormula> operands) throws ExprFormatException {
    	return Or.createInstance(operands);
    }

    /**
     * Creates a fresh instance of the temporal next-operator.
     * @param operand the operands of the next-operator
     * @return the freshly created next-instance
     * @throws ExprFormatException if the expression does not conform
     * structural requirements
     */
    protected TemporalFormula createNext(TemporalFormula operand) throws ExprFormatException {
    	return  Next.createInstance(operand);
    }

    /**
     * Creates a fresh instance of the temporal until-operator.
     * @param operands the operands of the until-operator
     * @return the freshly created until-instance
     * @throws ExprFormatException if the expression does not conform
     * structural requirements
     */
    protected TemporalFormula createUntil(List<TemporalFormula> operands) throws ExprFormatException {
    	return Until.createInstance(operands);
    }

    /**
     * Creates a fresh instance of the temporal globally-operator.
     * @param operand the operands of the globally-operator
     * @return the freshly created globally-instance
     * @throws ExprFormatException if the expression does not conform
     * structural requirements
     */
    protected TemporalFormula createGlobally(TemporalFormula operand) throws ExprFormatException {
    	return Globally.createInstance(operand);
    }

    /**
     * Creates a fresh instance of the temporal finally-operator.
     * @param operand the operands of the finally-operator
     * @return the freshly created finally-instance
     * @throws ExprFormatException if the expression does not conform
     * structural requirements
     */
    protected TemporalFormula createFinally(TemporalFormula operand) throws ExprFormatException {
    	return Finally.createInstance(operand);
    }

    /**
     * Creates a fresh instance of the temporal exists-operator.
     * @param operand the operands of the exists-operator
     * @return the freshly created exists-instance
     * @throws ExprFormatException if the expression does not conform
     * structural requirements
     */
    protected TemporalFormula createExists(TemporalFormula operand) throws ExprFormatException {
    	return Exists.createInstance(operand);
    }

    /**
     * Creates a fresh instance of the temporal all-operator.
     * @param operand the operands of the all-operator
     * @return the freshly created all-instance
     * @throws ExprFormatException if the expression does not conform
     * structural requirements
     */
    protected TemporalFormula createAll(TemporalFormula operand) throws ExprFormatException {
    	return All.createInstance(operand);
    }

    /**
     * Constructor.
     */
    protected CTLStarFormula() {
    	// empty constructor
    }

    /**
     * Returns the only instance of CTLStarFormula.
     * @return the singleton instance
     */
    static public CTLStarFormula getInstance() {
    	if (instance == null) {
    		instance = new CTLStarFormula();
    	}
    	return instance;
    }

    /**
     * The singleton instance of this class.
     */
    static private CTLStarFormula instance;
}
