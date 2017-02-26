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
package groove.automaton;

import static groove.graph.EdgeRole.NODE_TYPE;
import static groove.util.parse.StringHandler.DOUBLE_QUOTE_CHAR;
import static groove.util.parse.StringHandler.LANGLE_CHAR;
import static groove.util.parse.StringHandler.LPAR_CHAR;
import static groove.util.parse.StringHandler.PLACEHOLDER;
import static groove.util.parse.StringHandler.RPAR_CHAR;
import static groove.util.parse.StringHandler.SINGLE_QUOTE_CHAR;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.jdt.annotation.NonNull;

import groove.annotation.Help;
import groove.annotation.Syntax;
import groove.annotation.ToolTipBody;
import groove.annotation.ToolTipHeader;
import groove.annotation.ToolTipPars;
import groove.grammar.rule.LabelVar;
import groove.grammar.rule.RuleLabel;
import groove.grammar.type.TypeGuard;
import groove.grammar.type.TypeLabel;
import groove.graph.EdgeRole;
import groove.graph.Label;
import groove.util.Groove;
import groove.util.Pair;
import groove.util.line.Line;
import groove.util.line.Line.Style;
import groove.util.parse.FormatException;
import groove.util.parse.StringHandler;

/**
 * Class implementing a regular expression.
 * @author Arend Rensink
 * @version $Revision$
 */
abstract public class RegExpr { // implements VarSetSupport {
    /**
     * Constructs a regular expression with a given operator name and operator
     * symbol. This constructor is there for subclassing purposes.
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
     * If this is a {@link RegExpr.Atom}, returns the text of the atom;
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

    /** Tests if this is a {@link RegExpr.Sharp}. */
    public boolean isSharp() {
        return this instanceof Sharp;
    }

    /**
     * Returns the type label this is a {@link RegExpr.Sharp},
     * or {@code null} otherwise.
     */
    public TypeLabel getSharpLabel() {
        return isSharp() ? ((Sharp) this).getTypeLabel() : null;
    }

    /** Tests if this is a {@link RegExpr.Wildcard}. */
    public boolean isWildcard() {
        return this instanceof Wildcard;
    }

    /**
     * If this is a {@link RegExpr.Wildcard}, returns the identifier of the
     * wildcard; otherwise returns <code>null</code>.
     */
    public LabelVar getWildcardId() {
        if (this instanceof Wildcard) {
            return ((Wildcard) this).getLabelVar();
        } else {
            return null;
        }
    }

    /**
     * If this is a {@link RegExpr.Wildcard}, returns the guard of the wildcard,
     * if any; otherwise returns <code>null</code>.
     */
    public TypeGuard getWildcardGuard() {
        if (this instanceof Wildcard) {
            return ((Wildcard) this).getGuard();
        } else {
            return null;
        }
    }

    /**
     * If this is a {@link RegExpr.Wildcard}, returns the kind of label
     * the wildcard matches against; otherwise returns {@code -1}.
     */
    public EdgeRole getWildcardKind() {
        if (this instanceof Wildcard) {
            return ((Wildcard) this).getKind();
        } else {
            return null;
        }
    }

    /** Tests if this is a {@link RegExpr.Choice}. */
    public boolean isChoice() {
        return this instanceof Choice;
    }

    /**
     * If this is a {@link RegExpr.Choice}, returns the list of operands of the
     * regular expression; otherwise returns <code>null</code>.
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
     * If this is a {@link RegExpr.Seq}, returns the list of operands of the
     * regular expression; otherwise returns <code>null</code>.
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
     * If this is a {@link RegExpr.Star}, returns the operand of the regular
     * expression; otherwise returns <code>null</code>.
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
     * If this is a {@link RegExpr.Plus}, returns the operand of the regular
     * expression; otherwise returns <code>null</code>.
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
     * If this is a {@link RegExpr.Inv}, returns the operand of the regular
     * expression; otherwise returns <code>null</code>.
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
     * If this is a {@link RegExpr.Neg}, returns the operand of the regular
     * expression; otherwise returns <code>null</code>.
     */
    public RegExpr getNegOperand() {
        if (this instanceof Neg) {
            return ((Neg) this).getOperand();
        } else {
            return null;
        }
    }

    /**
     * Creates and returns the choice composition of this regular expression and
     * another. If the other is already a choice regular expression, flattens it
     * into a single level.
     */
    public Choice choice(RegExpr other) {
        if (other instanceof Choice) {
            List<RegExpr> operands = new ArrayList<>();
            operands.add(this);
            operands.addAll(other.getOperands());
            return new Choice(operands);
        } else {
            return new Choice(Arrays.asList(new RegExpr[] {this, other}));
        }
    }

    /**
     * Creates and returns the sequential composition of this regular expression
     * and another. If the other is already a sequential regular expression,
     * flattens it into a single level.
     */
    public Seq seq(RegExpr other) {
        if (other instanceof Seq) {
            List<RegExpr> operands = new ArrayList<>();
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
     * Returns a clone of this expression where all occurrences of a given label
     * are replaced by a new label.
     * @param oldLabel the label to be replaced
     * @param newLabel the new value for {@code oldLabel}
     * @return a clone of this expression, or this expression itself if {@code
     *         oldLabel} did not occur
     */
    abstract public RegExpr relabel(TypeLabel oldLabel, TypeLabel newLabel);

    /**
     * Returns the set of labels occurring in this regular expression. These are
     * the labels that, when relabelled, result in a different expression.
     * @see #relabel(TypeLabel, TypeLabel)
     */
    abstract public Set<TypeLabel> getTypeLabels();

    /** Indicates if the regular expression accepts the empty word. */
    public abstract boolean isAcceptsEmptyWord();

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
     * Tests if this expression contains the top-level operator of another
     * expression in one of its sub-expressions.
     * @param operator the expression of which we are seeing the top-level
     *        operator
     */
    public boolean containsOperator(RegExpr operator) {
        return containsOperator(operator.getOperator());
    }

    /**
     * Returns the set of all named variables occurring as identifiers in
     * {@link Wildcard}-subexpressions, in the order of the sub-expressions.
     */
    public Set<LabelVar> allVarSet() {
        // by making a linked set we make sure the order is preserved
        // and yet no identifier occurs more than once
        Set<LabelVar> result = new LinkedHashSet<>();
        if (getWildcardId() != null && getWildcardId().hasName()) {
            result.add(getWildcardId());
        } else {
            for (RegExpr operand : getOperands()) {
                result.addAll(operand.allVarSet());
            }
        }
        return result;
    }

    /**
     * Returns the list of named variables <i>bound</i> by this regular expression. A
     * variable is bound if the expression cannot be matched without providing a
     * value for it.
     * @see #allVarSet()
     */
    public Set<LabelVar> boundVarSet() {
        Set<LabelVar> result = new LinkedHashSet<>();
        if (getWildcardId() != null && getWildcardId().hasName()) {
            result.add(getWildcardId());
        }
        return result;
    }

    /**
     * Returns the (plain text) denotation for the operator in this class, as
     * set in the constructor.
     * @return the denotation for the operator in this class
     */
    public String getOperator() {
        return this.operator;
    }

    /**
     * Returns a textual description of this regular expression. This
     * implementation returns the symbolic name (see {@link #getSymbol()}
     * followed by the descriptions of the operands between square brackets, if
     * any.
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
     * Returns the symbolic name for the type of expression in this class, as
     * set in the constructor.
     */
    public String getSymbol() {
        return this.symbol;
    }

    /** Tests for equality of the {@link #toString()} results. */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof RegExpr && toString().equals(obj.toString());
    }

    /** Returns a label based on this expression. */
    public RuleLabel toLabel() {
        if (this.label == null) {
            this.label = new RuleLabel(this);
        }
        return this.label;
    }

    /**
     * Returns the hash code of the {@link #toString()} method, combined with a
     * bit pattern derived from the {@link RegExpr} class.
     */
    @Override
    public int hashCode() {
        return classHashCode ^ toString().hashCode();
    }

    /**
     * Indicates if this expression is matched by a single node
     * This is the case if it consists only of node types and flags.
     */
    abstract public boolean isBinary();

    /** Returns a line representing this expression typeset properly. */
    public @NonNull Line toLine() {
        Line result = this.line;
        if (result == null) {
            this.line = result = computeLine();
        }
        return result;
    }

    /** Callback method to create the line representation for this expression. */
    abstract protected @NonNull Line computeLine();

    private Line line;

    /**
     * Returns a list of {@link RegExpr}s that are the operands of this regular
     * expression.
     */
    abstract public List<RegExpr> getOperands();

    /**
     * Accept method for a calculator.
     * @param calculator the calculator
     * @return the return value of the calculation
     */
    public abstract <Result> Result apply(RegExprCalculator<Result> calculator);

    /**
     * Creates and returns a regular expression from a string. An implementation
     * should check the string using its own syntax rules. If the string does
     * not look like an expression of the right kind, the function should return
     * <tt>null</tt>; if it looks correct but is malformed (e.g., the correct
     * operator is there but the operands are missing) the function should raise
     * an exception.
     * @param expr the expression to be parsed; this is guaranteed to have
     *        correct bracketing and quoting (according to
     *        {@link StringHandler#parseExpr(String)}).
     * @return a valid regular expression, or <tt>null</tt> if <tt>expr</tt>
     *         does not appear to be a regular expression of the kind
     *         implemented by this class
     * @throws FormatException if <tt>expr</tt> appears to be an expression (of
     *         the kind implemented by the class) but is malformed
     */
    abstract protected RegExpr parseOperator(String expr) throws FormatException;

    /**
     * Tests whether a given text may be regarded as an atom, according to the
     * rules of regular expressions. (If not, then it should be single-quoted.)
     * Atoms may be preceded by a label kind prefix.
     * Throws an exception if the text is empty or contains any
     * characters not allowed by {@link #isAtomChar(char)}.
     * @param text the text to be tested
     * @throws FormatException if the text contains a special character
     * @see #isAtom(String)
     */
    static public void assertAtom(String text) throws FormatException {
        if (text.length() == 0) {
            throw new FormatException("Empty atom");
        } else {
            switch (text.charAt(0)) {
            case DOUBLE_QUOTE_CHAR:
            case LANGLE_CHAR:
                // quoted/bracketed atoms
                Pair<String,List<String>> parseResult = StringHandler.parseExpr(text);
                if (parseResult.one()
                    .length() != 1) {
                    String error;
                    if (text.charAt(0) == DOUBLE_QUOTE_CHAR) {
                        error = String.format("Atom '%s' has unbalanced quotes", text);
                    } else {
                        error = String.format("Atom '%s' has unbalanced brackets", text);
                    }
                    throw new FormatException(error);
                } else {
                    break;
                }
            case INV_OPERATOR:
                String error = String.format("Atom '%s' contains invalid first character '%c'",
                    text,
                    INV_OPERATOR);
                throw new FormatException(error);
            default:
                // default atoms
                // skip any node type or flag prefix
                text = TypeLabel.createLabelWithCheck(text)
                    .text();
                boolean correct = true;
                int i;
                for (i = 0; correct && i < text.length(); i++) {
                    correct = isAtomChar(text.charAt(i));
                }
                if (!correct) {
                    throw new FormatException("Atom '%s' contains invalid character '%c'", text,
                        text.charAt(i - 1));
                }
            }
        }
    }

    /**
     * Tests whether a given text may be regarded as an atom, according to the
     * rules of regular expressions. If not, then it should be single-quoted. If
     * <tt>true</tt>, the text will be parsed by {@link #parse(String)}as an
     * {@link Atom}. This implementation returns <tt>true</tt> if the text does
     * not contain any special characters
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

    /** Tests if a character may occur in an atom. */
    static public boolean isAtomChar(char c) {
        return Character.isLetterOrDigit(c) || ATOM_CHARS.indexOf(c) >= 0;
    }

    /**
     * Tests if a given object equals the operator of this regular expression
     * class.
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
            return bindsWeaker(((Composite) operator1).getOperator(),
                ((Composite) operator2).getOperator());
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
    private RuleLabel label;

    /**
     * Parses a given string as a regular expression. Throws an exception if the
     * parsing does not succeed.
     * @param expr the string to be parsed
     * @return a regular expression which, when turned back into a string,
     *         equals <code>expr</code>
     * @throws FormatException if <code>expr</code> cannot be parsed
     */
    static public RegExpr parse(String expr) throws FormatException {
        // first test if the quoting and bracketing is correct
        StringHandler.parseExpr(expr);
        // try to parse the expression using each of the available operators in
        // turn
        for (RegExpr prototype : prototypes) {
            RegExpr result = prototype.parseOperator(expr);
            // if the result is non-null, we are done
            if (result != null) {
                return result;
            }
        }
        throw new FormatException("Unable to parse expression %s as regular expression", expr);
    }

    /** Creates and returns an atomic regular expression with a given atom text. */
    public static Atom atom(String text) {
        return new Atom(text);
    }

    /**
     * Creates and returns a sharp test for a given node type label.
     */
    public static Sharp sharp(TypeLabel typeLabel) {
        return new Sharp(typeLabel);
    }

    /**
     * Creates and returns an unnamed wildcard.
     * @param kind the kind of labels the wildcard will match
     */
    public static Wildcard wildcard(EdgeRole kind) {
        return wildcard(kind, null, true);
    }

    /**
     * Creates and returns a named wildcard.
     * @param kind the kind of labels the wildcard will match
     * @param name the name of the wildcard; non-{@code null}
     */
    public static Wildcard wildcard(EdgeRole kind, String name) {
        assert name != null;
        return wildcard(kind, name, true);
    }

    /**
     * Creates and returns an unnamed wildcard with an optional label constraint.
     * @param kind the kind of labels the wildcard will match
     * @param negated flag indicating if the label constraint is negative
     * @param labels optional list of labels constraining the value of the wildcard;
     * if empty, there is no constraint
     */
    public static Wildcard wildcard(EdgeRole kind, boolean negated, String... labels) {
        return wildcard(kind, null, negated, labels);
    }

    /**
     * Creates and returns an optionally named wildcard with an optional label constraint.
     * @param kind the kind of labels the wildcard will match
     * @param name the name of the wildcard variable; may be {@code null} for an unnamed wildcard
     * @param negated flag indicating if the label constraint is negative
     * @param labels optional list of labels constraining the value of the wildcard;
     * if empty, there is no constraint
     */
    public static Wildcard wildcard(EdgeRole kind, String name, boolean negated, String... labels) {
        Wildcard prototype = new Wildcard();
        TypeGuard guard =
            new TypeGuard(name == null ? new LabelVar(kind) : new LabelVar(name, kind));
        if (labels.length > 0) {
            guard.setLabels(Arrays.asList(labels), negated);
        }
        return prototype.newInstance(guard);
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
        if (args.length == 0) {
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
            test("type:?ab[a,b]");
            test("flag:?ab[^a,b]");
        } else {
            for (String arg : args) {
                test(arg);
            }
        }
    }

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
     * Wildcard constant.
     * @see Wildcard
     */
    static public final char WILDCARD_OPERATOR = '?';
    /**
     * Symbolic name of the wildcard constant.
     * @see Wildcard
     */
    static public final String WILDCARD_SYMBOLIC_NAME = "Any";
    /**
     * Wildcard constant.
     * @see Wildcard
     */
    static public final char SHARP_OPERATOR = '#';
    /**
     * Symbolic name of the wildcard constant.
     * @see Wildcard
     */
    static public final String SHARP_SYMBOLIC_NAME = "Sharp";
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
     * The characters allowed in a regular expression atom, apart from letters
     * and digits.
     * @see StringHandler#isIdentifier(String)
     */
    static public final String ATOM_CHARS = "_$-";

    /**
     * An array of prototype regular expressions, in order of increasing
     * priority. In particular, atoms that have special meaning should come
     * before the {@link Atom}.
     */
    static private final RegExpr[] prototypes = new RegExpr[] {new Atom(), new Neg(), new Choice(),
        new Seq(), new Inv(), new Star(), new Plus(), new Wildcard(), new Sharp(), new Empty()};

    /**
     * The list of operators into which a regular expression will be parsed, in
     * order of increasing priority.
     */
    static private final List<String> operators;
    /**
     * Mapping from keywords in syntax descriptions to corresponding text.
     */
    static private final Map<String,String> tokenMap;

    static {
        operators = new LinkedList<>();
        tokenMap = new HashMap<>();
        for (RegExpr prototype : prototypes) {
            if (!(prototype instanceof Atom)) {
                operators.add(prototype.getOperator());
                tokenMap.put(prototype.getClass()
                    .getSimpleName(), prototype.getOperator());
            }
        }
        tokenMap.put("LSQUARE", "[");
        tokenMap.put("RSQUARE", "]");
        tokenMap.put("COMMA", ",");
        tokenMap.put("COLON", ":");
        tokenMap.put("HAT", "^");
        tokenMap.put("FLAG", "flag");
        tokenMap.put("TYPE", "type");
    }

    /** Constant hash code characterising the class. */
    static private final int classHashCode = System.identityHashCode(RegExpr.class);

    /**
     * Returns a syntax helper mapping from syntax items
     * to (possibly {@code null}) tool tips.
     */
    public static Map<String,String> getDocMap() {
        if (docMap == null) {
            docMap = computeDocMap();
        }
        return docMap;
    }

    private static Map<String,String> computeDocMap() {
        Map<String,String> result = new TreeMap<>();
        for (Class<?> subClass : RegExpr.class.getClasses()) {
            Help help = Help.createHelp(subClass, tokenMap);
            if (help != null) {
                result.put(help.getItem(), help.getTip());
            }
        }
        return result;
    }

    /** Syntax helper map, from syntax items to associated tool tips. */
    private static Map<String,String> docMap;

    /**
     * Abstract superclass for all regular expressions that are not constants.
     */
    abstract static protected class Composite extends RegExpr {
        /**
         * Constructs an instance of a composite regular expression with a given
         * operator name and operator symbol. This constructor is there only for
         * subclassing purposes.
         */
        protected Composite(String operator, String symbol) {
            super(operator, symbol);
        }

        @Override
        public boolean isBinary() {
            boolean result = false;
            for (RegExpr operand : getOperands()) {
                if (operand.isBinary()) {
                    result = true;
                    break;
                }
            }
            return result;
        }
    }

    /**
     * Abstract class modelling a sequence of (more than one) operand separated
     * by a given operator string.
     */
    abstract static protected class Infix extends Composite {
        /**
         * Creates a regular expression from an infix operator and a list of
         * operands. The operands are themselves regular expressions.
         */
        public Infix(String operator, String symbol, List<RegExpr> operands) {
            super(operator, symbol);
            this.operandList = operands;
            this.acceptsEmptyWord = operands != null && computeAcceptsEmptyWord(operands);
        }

        /**
         * Returns (a clone of) the operands of this regular expression.
         * @return a clone of the operands of this regular expression
         */
        @Override
        public List<RegExpr> getOperands() {
            return Collections.unmodifiableList(this.operandList);
        }

        @Override
        public RegExpr parseOperator(String expr) throws FormatException {
            String[] operands =
                StringHandler.splitExpr(expr, getOperator(), StringHandler.INFIX_POSITION);
            if (operands.length < 2) {
                return null;
            }
            List<RegExpr> operandList = new LinkedList<>();
            for (String element : operands) {
                operandList.add(parse(element));
            }
            return newInstance(operandList);
        }

        /**
         * Returns the operands, parenthesized if so required by the priority,
         * separated by the operator of this infix expression.
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

        @Override
        protected Line computeLine() {
            Line result = Line.empty();
            Iterator<RegExpr> operandIter = getOperands().iterator();
            while (operandIter.hasNext()) {
                RegExpr operand = operandIter.next();
                if (bindsWeaker(operand, this)) {
                    result = result.append("" + LPAR_CHAR)
                        .append(operand.toLine())
                        .append("" + RPAR_CHAR);
                } else {
                    result = result.append(operand.toLine());
                }
                if (operandIter.hasNext()) {
                    result = result.append(getOperator());
                }
            }
            return result;
        }

        /**
         * This implementation first calls the calculator on the operands and
         * then on the operator itself with the resulting arguments.
         * @see #applyInfix(RegExprCalculator, List)
         */
        @Override
        public <Result> Result apply(RegExprCalculator<Result> calculator) {
            List<Result> argsList = new ArrayList<>();
            for (RegExpr operand : getOperands()) {
                argsList.add(operand.apply(calculator));
            }
            return applyInfix(calculator, argsList);
        }

        @Override
        public RegExpr relabel(TypeLabel oldLabel, TypeLabel newLabel) {
            List<RegExpr> newOperands = new ArrayList<>();
            boolean hasChanged = false;
            for (RegExpr operand : getOperands()) {
                RegExpr newOperand = operand.relabel(oldLabel, newLabel);
                newOperands.add(newOperand);
                hasChanged |= newOperand != operand;
            }
            return hasChanged ? newInstance(newOperands) : this;
        }

        @Override
        public Set<TypeLabel> getTypeLabels() {
            Set<TypeLabel> result = new HashSet<>();
            for (RegExpr operand : getOperands()) {
                result.addAll(operand.getTypeLabels());
            }
            return result;
        }

        /**
         * Factory method for an infix expression. The number of operands is
         * guaranteed to be at least 2.
         * @param operandList the list of operands of the infix expression
         * @return a new infix expression based on <tt>operands</tt>
         * @require <tt>operandList.size() >= 2</tt>
         */
        abstract protected Infix newInstance(List<RegExpr> operandList);

        /**
         * Calculation of the actual operation, given precalculated argumants.
         * @see #apply(RegExprCalculator)
         */
        abstract protected <Result> Result applyInfix(RegExprCalculator<Result> visitor,
            List<Result> argsList);

        @Override
        public boolean isAcceptsEmptyWord() {
            return this.acceptsEmptyWord;
        }

        /**
         * Callback method to compute whether the expression accepts the
         * empty word.
         */

        abstract boolean computeAcceptsEmptyWord(List<RegExpr> operandList);

        /**
         * The operands of this infix expression.
         */
        private final List<RegExpr> operandList;
        /*8 Flag indicating of the expression accepts the empty word. */
        private final boolean acceptsEmptyWord;
    }

    /**
     * Abstract class modelling a postfix operatior. This corresponds to one
     * operand followed by a operator string, fixed in the specializing class.
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

        @Override
        public RegExpr relabel(TypeLabel oldLabel, TypeLabel newLabel) {
            RegExpr newOperand = getOperand().relabel(oldLabel, newLabel);
            return newOperand != getOperand() ? newInstance(newOperand) : this;
        }

        @Override
        public Set<TypeLabel> getTypeLabels() {
            return getOperand().getTypeLabels();
        }

        /** Returns the single operand of this postfix expression. */
        public RegExpr getOperand() {
            return this.operand;
        }

        /**
         * Returns a singular list consisting of the single operand of this
         * postfix expression.
         */
        @Override
        public List<RegExpr> getOperands() {
            return this.operandList;
        }

        @Override
        public String toString() {
            if (bindsWeaker(this.operand, this)) {
                return "" + LPAR_CHAR + getOperand() + RPAR_CHAR + getOperator();
            } else {
                return "" + getOperand() + getOperator();
            }
        }

        @Override
        protected Line computeLine() {
            Line result = Line.empty();
            if (bindsWeaker(this.operand, this)) {
                return result.append("" + LPAR_CHAR)
                    .append(getOperand().toLine())
                    .append("" + RPAR_CHAR + getOperator());
            } else {
                return result.append(getOperand().toLine())
                    .append(getOperator());
            }
        }

        /**
         * @return <tt>null</tt> if the postfix operator (given by
         *         <tt>operator()</tt>) does not occur in <tt>tokenList</tt>
         * @throws FormatException of the operator does occur in the list, but
         *         not as the last element
         */
        @Override
        protected RegExpr parseOperator(String expr) throws FormatException {
            String[] operands =
                StringHandler.splitExpr(expr, getOperator(), StringHandler.POSTFIX_POSITION);
            if (operands == null) {
                return null;
            }
            return newInstance(parse(operands[0]));
        }

        /**
         * This implementation first calls the calculator on the operand and
         * then on the operator itself with the resulting argument.
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
        abstract protected <Result> Result applyPostfix(RegExprCalculator<Result> visitor,
            Result arg);

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
     * Abstract class modelling a postfix operatior. This corresponds to an
     * operator string, fixed in the specializing class, followed by one
     * operand.
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

        @Override
        public RegExpr relabel(TypeLabel oldLabel, TypeLabel newLabel) {
            RegExpr newOperand = getOperand().relabel(oldLabel, newLabel);
            return newOperand != getOperand() ? newInstance(newOperand) : this;
        }

        @Override
        public Set<TypeLabel> getTypeLabels() {
            return getOperand().getTypeLabels();
        }

        /** Returns the single operand of this prefix expression. */
        public RegExpr getOperand() {
            return this.operand;
        }

        /**
         * Returns a singular list consisting of the single operand of this
         * postfix expression.
         */
        @Override
        public List<RegExpr> getOperands() {
            return this.operandList;
        }

        @Override
        public String toString() {
            if (bindsWeaker(this.operand, this)) {
                return "" + getOperator() + LPAR_CHAR + getOperand() + RPAR_CHAR;
            } else {
                return "" + getOperator() + getOperand();
            }
        }

        @Override
        protected Line computeLine() {
            Line result = Line.empty();
            if (bindsWeaker(this.operand, this)) {
                return result.append(getOperator() + LPAR_CHAR)
                    .append(getOperand().toLine())
                    .append("" + RPAR_CHAR);
            } else {
                return result.append("" + getOperator())
                    .append(getOperand().toLine());
            }
        }

        /**
         * @return <tt>null</tt> if the prefix operator (given by
         *         <tt>operator()</tt>) does not occur in <tt>tokenList</tt>
         * @throws FormatException of the operator does occur in the list, but
         *         not as the first element
         */
        @Override
        protected RegExpr parseOperator(String expr) throws FormatException {
            String[] operands =
                StringHandler.splitExpr(expr, getOperator(), StringHandler.PREFIX_POSITION);
            if (operands == null) {
                return null;
            }
            return newInstance(parse(operands[0]));
        }

        /**
         * This implementation first calls the calculator on the operand and
         * then on the operator itself with the resulting argument.
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
        abstract protected <Result> Result applyPrefix(RegExprCalculator<Result> visitor,
            Result arg);

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
        Constant(String operator, String symbol) {
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
         * This implementation returns the operator, as determined by
         * {@link #getOperator()}.
         */
        @Override
        public String toString() {
            return getOperator();
        }

        /**
         * @return {@code null} if {@code expr} does not equal the constant (given by
         *         {@link #getOperator()}
         * @throws FormatException of the operator does occur in the list, but
         *         not as the last element
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
     * Sequential composition operator. This is an infix operator that
     * concatenates its operands sequentially.
     */
    @Syntax("expr1 %s expr2")
    @ToolTipHeader("Concatenation")
    @ToolTipBody({"Satisfied by a path <i>p</i> if it is the concatenation",
        "of a path <i>p1</i> satisfying %1$s, followed by a path <i>p2</i>", "satisfying %2$s"})
    static public class Seq extends Infix {
        /** Creates a sequential composition of a list of expressions. */
        public Seq(List<RegExpr> innerRegExps) {
            super("" + SEQ_OPERATOR, SEQ_SYMBOLIC_NAME, innerRegExps);
        }

        /** Creates a prototype instance. */
        Seq() {
            this(null);
        }

        @Override
        protected Infix newInstance(List<RegExpr> operandList) {
            return new Seq(operandList);
        }

        /**
         * Calls {@link RegExprCalculator#computeSeq(RegExpr.Seq, List)} on the
         * visitor.
         */
        @Override
        protected <Result> Result applyInfix(RegExprCalculator<Result> visitor,
            List<Result> argsList) {
            return visitor.computeSeq(this, argsList);
        }

        /** A sequence accepts the empty word if all operands do. */
        @Override
        boolean computeAcceptsEmptyWord(List<RegExpr> operandList) {
            boolean result = true;
            for (RegExpr operand : operandList) {
                if (!operand.isAcceptsEmptyWord()) {
                    result = false;
                    break;
                }
            }
            return result;
        }

    }

    /**
     * Choice operator. This is an infix operator that offers a choice among its
     * operands.
     */
    @Syntax("expr1 %s expr2")
    @ToolTipHeader("Choice")
    @ToolTipBody({"Satisfied by a path <i>p</i> if satisfies either %1$s or %2$s"})
    static public class Choice extends Infix {
        /** Creates a choice between a list of expressions. */
        public Choice(List<RegExpr> tokenList) {
            super("" + CHOICE_OPERATOR, CHOICE_SYMBOLIC_NAME, tokenList);
        }

        /** Creates a prototype instance. */
        Choice() {
            this(null);
        }

        @Override
        protected Infix newInstance(List<RegExpr> operandList) {
            return new Choice(operandList);
        }

        /**
         * Calls {@link RegExprCalculator#computeChoice(RegExpr.Choice, List)}
         * on the visitor.
         */
        @Override
        protected <Result> Result applyInfix(RegExprCalculator<Result> visitor,
            List<Result> argsList) {
            return visitor.computeChoice(this, argsList);
        }

        /** A choice accepts the empty word if at least one operand does. */
        @Override
        boolean computeAcceptsEmptyWord(List<RegExpr> operandList) {
            boolean result = false;
            for (RegExpr operand : operandList) {
                if (operand.isAcceptsEmptyWord()) {
                    result = true;
                    break;
                }
            }
            return result;
        }

    }

    /**
     * Constant expression that stands for all edges existing in the graph. The
     * wildcard may contain an identifier, which then acts as a variable that
     * may be bound to a value when the expression is matched.
     */
    @Syntax("[role COLON] %s [name] [ LSQUARE [HAT]label_list RSQUARE ]")
    @ToolTipHeader("Label variable or wildcard")
    @ToolTipBody({"Satisfied by an edge if its label fulfils the following conditions:", "<ul>",
        "<li> If the prefix %1$s is specified (either FLAG or TYPE)",
        "then the label must have that role;", "if it is absent, the edge must be binary.",
        "<li> If a suffix of the form LSQUARE %3$s RSQUARE is specified",
        "then the label must be one of the elements of %3$s.",
        "<li> If a suffix of the form LSQUARE HAT %3$s RSQUARE is specified",
        "then the label may <i>not</i> be one of the elements of %3$s.", "</ul>",
        "The optional %2$s acts as a variable binding the label.",
        "Multiple occurrences of %2$s in a rule must all be bound to the same label."})
    @ToolTipPars({"the optional role of the label: either FLAG or TYPE",
        "the optional wildcard variable name",
        "comma-separated list of labels, either containing or excluding the matched label"})
    static public class Wildcard extends Constant {
        /** Creates an prototype instance. */
        Wildcard() {
            this(null);
        }

        /**
         * Constructs a wildcard expression with a given (possibly {@code null})
         * identifier and (possibly {@code null}) label constraint.
         */
        private Wildcard(TypeGuard guard) {
            super("" + WILDCARD_OPERATOR, WILDCARD_SYMBOLIC_NAME);
            this.guard = guard;
        }

        @Override
        public RegExpr relabel(TypeLabel oldLabel, TypeLabel newLabel) {
            TypeGuard newGuard = null;
            newGuard = this.guard.relabel(oldLabel, newLabel);
            return newGuard == this.guard ? this : newInstance(newGuard);
        }

        @Override
        public Set<TypeLabel> getTypeLabels() {
            Set<TypeLabel> result = this.guard.getLabels();
            if (result == null) {
                result = Collections.emptySet();
            }
            return result;
        }

        /**
         * Calls {@link RegExprCalculator#computeWildcard(RegExpr.Wildcard)} on
         * the visitor.
         */
        @Override
        public <Result> Result apply(RegExprCalculator<Result> calculator) {
            return calculator.computeWildcard(this);
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();
            result.append(getGuard().getKind()
                .getPrefix());
            result.append(super.toString());
            result.append(getLabelVar().getName());
            result.append(getGuard());
            return result.toString();
        }

        @Override
        protected Line computeLine() {
            Line result = Line.atom(super.toString() + getLabelVar().getName() + getGuard());
            switch (getGuard().getKind()) {
            case FLAG:
                result = result.style(Style.ITALIC);
                break;
            case NODE_TYPE:
                result = result.style(Style.BOLD);
                break;
            default:
                // no style imposed
            }
            return result;
        }

        /**
         * This implementation delegates to {@link #toString()}.
         */
        @Override
        public String getDescription() {
            StringBuilder result = new StringBuilder();
            if (getIdentifier() != null) {
                result.append(getIdentifier() + "=");
            }
            result.append(getSymbol());
            if (this.guard != null) {
                String type = this.guard.getKind()
                    .getPrefix();
                if (!type.isEmpty()) {
                    result.append(" ");
                    result.append(type.subSequence(0, type.length() - 1));
                }
                Set<TypeLabel> labels = this.guard.getLabels();
                if (labels != null) {
                    result.append(this.guard.isNegated() ? " not in " : " from ");
                    result.append(Groove.toString(labels.toArray()));
                }
            }
            return result.toString();
        }

        /**
         * Returns the optional guard of this wildcard expression.
         */
        public TypeGuard getGuard() {
            return this.guard;
        }

        /** Returns the kind of labels accepted by this wildcard. */
        public EdgeRole getKind() {
            return getGuard().getKind();
        }

        @Override
        public boolean isBinary() {
            return getKind() == EdgeRole.BINARY;
        }

        /**
         * Returns the optional identifier of this wildcard expression.
         */
        public String getIdentifier() {
            return getLabelVar().getName();
        }

        /**
         * Returns the optional label variable of this wildcard expression.
         * The variable is the identifier combined with the constraint kind.
         */
        public LabelVar getLabelVar() {
            return this.guard.getVar();
        }

        /**
         * Tests if there is a single wildcard operator in {@code expr};
         * if so, tests for a label prefix, and if there is text following
         * the operator, whether that can be parsed as an identifier
         * and/or a label constraint.
         */
        @Override
        protected RegExpr parseOperator(String expr) throws FormatException {
            FormatException error =
                new FormatException("Can't parse wildcard expression '%s'", expr);
            int index = expr.indexOf(getOperator());
            if (index < 0) {
                return null;
            }
            String text = expr.substring(index + 1);
            if (text.indexOf(getOperator()) >= 0) {
                throw error;
            }
            String prefix = expr.substring(0, index);
            // derive the type of labels the wildcard should match
            Pair<EdgeRole,String> parsedPrefix = EdgeRole.parseLabel(prefix);
            if (parsedPrefix.two()
                .length() > 0) {
                throw error;
            }
            EdgeRole kind = parsedPrefix.one();
            // parse the identifier and constraint expression
            String identifier = null;
            String parameter = null;
            if (!text.isEmpty()) {
                // decompose text into identifier and label list
                Pair<String,List<String>> operand = StringHandler.parseExpr(text);
                int subStringCount = operand.two()
                    .size();
                identifier = operand.one();
                if (subStringCount > 1) {
                    throw error;
                } else if (subStringCount == 1) {
                    parameter = operand.two()
                        .iterator()
                        .next();
                    if (identifier.indexOf(StringHandler.PLACEHOLDER) != identifier.length() - 1) {
                        throw error;
                    }
                    identifier = identifier.substring(0, identifier.length() - 1);
                }
                if (identifier.isEmpty()) {
                    identifier = null;
                } else if (!StringHandler.isIdentifier(identifier)) {
                    throw new FormatException("Invalid wildcard identifier '%s'", text);
                }
            }
            LabelVar var = identifier == null ? new LabelVar(kind) : new LabelVar(identifier, kind);
            TypeGuard guard = new TypeGuard(var);
            if (parameter != null) {
                Pair<List<String>,Boolean> constraint = getConstraint(parameter);
                guard.setLabels(constraint.one(), constraint.two());
            }
            return newInstance(guard);
        }

        /**
         * Retrieves label constraint information from a given string
         * @param parameter the string encoding the label information
         * @throws FormatException if <code>parameter</code> is not correctly
         *         formed as a constraint.
         */
        private Pair<List<String>,Boolean> getConstraint(String parameter) throws FormatException {
            String constraintList =
                StringHandler.toTrimmed(parameter, TypeGuard.OPEN, TypeGuard.CLOSE);
            if (constraintList == null) {
                throw new FormatException("Invalid constraint parameter '%s'", parameter);
            }
            boolean negated = constraintList.indexOf(TypeGuard.NEGATOR) == 0;
            if (negated) {
                constraintList = constraintList.substring(1);
            }
            String[] constraintParts = StringHandler.splitExpr(constraintList,
                "" + TypeGuard.SEPARATOR,
                StringHandler.INFIX_POSITION);
            if (constraintParts.length == 0) {
                throw new FormatException("Invalid constraint parameter '%s'", parameter);
            }
            final List<String> constrainedLabels = new ArrayList<>();
            for (String part : constraintParts) {
                RegExpr atom;
                try {
                    atom = parse(part);
                } catch (FormatException exc) {
                    throw new FormatException("Label '%s' in constraint '%s' cannot be parsed",
                        part, parameter);
                }
                if (atom instanceof Atom) {
                    Label label = atom.toLabel();
                    if (label.getRole() != EdgeRole.BINARY) {
                        throw new FormatException(
                            "Label '%s' in constraint '%s' should not be prefixed", part,
                            parameter);
                    }
                    constrainedLabels.add(label.text());
                } else {
                    throw new FormatException("Label '%s' in constraint '%s' should be an atom",
                        part, parameter);
                }
            }
            return Pair.newPair(constrainedLabels, negated);
        }

        /** Returns a {@link Wildcard} with a given identifier. */
        protected Wildcard newInstance(TypeGuard constraint) {
            return new Wildcard(constraint);
        }

        /** This implementation returns a {@link Wildcard}. */
        @Override
        protected Constant newInstance() {
            return new Wildcard();
        }

        @Override
        public boolean isAcceptsEmptyWord() {
            return false;
        }

        /** The label constraint for this wildcard expression. */
        private final TypeGuard guard;
    }

    /**
     * Constant expression that stands for all edges precisely matching
     * a given type label (rather than modulo subtyping).
     */
    @Syntax("%s label")
    @ToolTipHeader("Sharp type")
    @ToolTipBody({"Satisfied only by the node type %s (and not by any subtype of it)"})
    static public class Sharp extends Constant {
        /** Creates an instance without variable identifier. */
        public Sharp() {
            super("" + SHARP_OPERATOR, SHARP_SYMBOLIC_NAME);
        }

        /**
         * Constructs a sharp test for a given node type label
         * @param typeLabel the type label
         */
        public Sharp(TypeLabel typeLabel) {
            this();
            assert typeLabel.getRole() == NODE_TYPE;
            this.typeLabel = typeLabel;
        }

        @Override
        public RegExpr relabel(TypeLabel oldLabel, TypeLabel newLabel) {
            RegExpr result;
            if (getTypeLabel().equals(oldLabel)) {
                if (newLabel.getRole() == NODE_TYPE) {
                    result = newInstance(newLabel);
                } else {
                    result = new Atom(newLabel.text());
                }
            } else {
                result = this;
            }
            return result;
        }

        @Override
        public Set<TypeLabel> getTypeLabels() {
            Set<TypeLabel> result = new HashSet<>();
            result.add(getTypeLabel());
            return result;
        }

        /**
         * Calls {@link RegExprCalculator#computeWildcard(RegExpr.Wildcard)} on
         * the visitor.
         */
        @Override
        public <Result> Result apply(RegExprCalculator<Result> calculator) {
            return calculator.computeSharp(this);
        }

        /**
         * This implementation delegates to <code>super</code> if
         * {@link #getDescription()} returns <code>null</code>, otherwise it
         * returns the concatenation of the operator and the identifier.
         */
        @Override
        public String toString() {
            return NODE_TYPE.getPrefix() + super.toString() + getTypeLabel().text();
        }

        @Override
        protected Line computeLine() {
            return Line.atom(super.toString() + getTypeLabel().text())
                .style(Style.BOLD);
        }

        /**
         * First tries the super implementation, but if that does not work,
         * tries to parse <code>expr</code> as a prefix expression where the
         * operand is an identifier (according to
         * {@link StringHandler#isIdentifier(String)}).
         */
        @Override
        protected RegExpr parseOperator(String expr) throws FormatException {
            int index = expr.indexOf(getOperator());
            if (index < 0) {
                return null;
            }
            // separate the expression into operator and text
            Pair<EdgeRole,String> parsedExpr = EdgeRole.parseLabel(expr.substring(0, index));
            String text = expr.substring(index + 1);
            if (parsedExpr.one() != NODE_TYPE || parsedExpr.two()
                .length() != 0) {
                throw new FormatException("Sharp operator '%s' must be preceded by '%s'",
                    getOperator(), NODE_TYPE.getPrefix());
            }
            return newInstance(TypeLabel.createLabel(NODE_TYPE, text, true));
        }

        /** Returns a {@link Wildcard} with a given identifier. */
        protected Sharp newInstance(TypeLabel typeLabel) {
            return new Sharp(typeLabel);
        }

        /** This implementation returns a {@link Wildcard}. */
        @Override
        protected Constant newInstance() {
            return new Sharp();
        }

        /** Returns the type label that should be sharply matched. */
        public TypeLabel getTypeLabel() {
            return this.typeLabel;
        }

        @Override
        public boolean isAcceptsEmptyWord() {
            return false;
        }

        @Override
        public boolean isBinary() {
            return false;
        }

        /** The type labels that should be matched sharply. */
        private TypeLabel typeLabel;
    }

    /**
     * Constant expression that stands for all reflexive pairs.
     */
    @Syntax("%s")
    @ToolTipHeader("Equality or merging")
    @ToolTipBody({"Satisfied only by an empty path; i.e., a path not containing any edges."})
    static public class Empty extends Constant {
        /** Creates an instance of this expression. */
        public Empty() {
            super("" + EMPTY_OPERATOR, EMPTY_SYMBOLIC_NAME);
        }

        @Override
        public RegExpr relabel(TypeLabel oldLabel, TypeLabel newLabel) {
            return this;
        }

        @Override
        public Set<TypeLabel> getTypeLabels() {
            return Collections.emptySet();
        }

        /** This implementation returns a {@link Empty}. */
        @Override
        protected Constant newInstance() {
            return new Empty();
        }

        /**
         * Calls {@link RegExprCalculator#computeEmpty(RegExpr.Empty)} on the
         * visitor.
         */
        @Override
        public <Result> Result apply(RegExprCalculator<Result> calculator) {
            return calculator.computeEmpty(this);
        }

        @Override
        public boolean isAcceptsEmptyWord() {
            return true;
        }

        @Override
        public boolean isBinary() {
            return false;
        }

        @Override
        protected Line computeLine() {
            return Line.atom(getOperator());
        }
    }

    /**
     * Constant expression that stands for a fixed symbol. The symbol is know as
     * the <i>text</i> of the atom.
     */
    @Syntax("[role COLON] label")
    @ToolTipHeader("Single edge or label")
    @ToolTipBody({"Matched by a single edge labelled %2$s (if no %1$s is specified),",
        "a %2$s-flag (if %1$s equals FLAG) or a %2$s-type (if %1$s equals TYPE).",
        "In the latter case, any subtype of %2$s is also correct."})
    @ToolTipPars({"optional role: either TYPE or FLAG",
        "edge label; should be single-quoted if it contains non-identifier characters."})
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
         * Creates a prototype regular expression.
         */
        Atom() {
            this("");
        }

        @Override
        public RegExpr relabel(TypeLabel oldLabel, TypeLabel newLabel) {
            return oldLabel.equals(toTypeLabel()) ? newInstance(newLabel.toParsableString()) : this;
        }

        @Override
        public Set<TypeLabel> getTypeLabels() {
            Set<TypeLabel> result = new HashSet<>();
            result.add(toTypeLabel());
            return result;
        }

        /**
         * Puts single quotes around the atom text if it could otherwise be
         * parsed as something else.
         */
        @Override
        public String toString() {
            if (isAtom(text())) {
                // the atom text can be understood as is
                return text();
            } else {
                // the atom text looks like something else if we parse it as is
                return StringHandler.toQuoted(text(), SINGLE_QUOTE_CHAR);
            }
        }

        @Override
        protected Line computeLine() {
            return toTypeLabel().toLine();
        }

        @Override
        public String getDescription() {
            return this.text;
        }

        /**
         * Returns the bare text of the atom.
         */
        public String text() {
            return this.text;
        }

        /** Constructs a default label from this atom. */
        public TypeLabel toTypeLabel() {
            return TypeLabel.createLabel(text());
        }

        /**
         * This implementation never returns <tt>null</tt>, since it is assumed
         * to be at the end of the chain of prototypes tried out during parsing.
         * @throws FormatException if <tt>tokenList</tt> is not a singleton or
         *         its element is not recognised as a nested expression or atom
         */
        @Override
        public RegExpr parseOperator(String expr) throws FormatException {
            expr = expr.trim();
            if (expr.length() == 0) {
                throw new FormatException("Empty string not allowed in expression");
            } else if (isAtom(expr)) {
                return newInstance(expr);
            } else {
                // the only hope is that the expression is quoted or bracketed
                Pair<String,List<String>> parseResult = StringHandler.parseExpr(expr);
                if (parseResult.one()
                    .length() == 1
                    && parseResult.one()
                        .charAt(0) == PLACEHOLDER) {
                    String parsedExpr = parseResult.two()
                        .get(0);
                    switch (parsedExpr.charAt(0)) {
                    case LPAR_CHAR:
                        return parse(parsedExpr.substring(1, expr.length() - 1));
                    case SINGLE_QUOTE_CHAR:
                        return newInstance(StringHandler.toUnquoted(parsedExpr, SINGLE_QUOTE_CHAR));
                    default:
                        return null;
                    }
                } else {
                    // the expression is not atomic when parsed
                    return null;
                }
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
         * Factory method: creates a new atomic regular expression, from a given
         * text. Does not test for proper atom format.
         */
        protected Atom newInstance(String text) {
            return new Atom(text);
        }

        /**
         * Calls {@link RegExprCalculator#computeAtom(RegExpr.Atom)} on the
         * visitor.
         */
        @Override
        public <Result> Result apply(RegExprCalculator<Result> calculator) {
            return calculator.computeAtom(this);
        }

        @Override
        public boolean isAcceptsEmptyWord() {
            return false;
        }

        @Override
        public boolean isBinary() {
            return toTypeLabel().getRole() == EdgeRole.BINARY;
        }

        /** The text of the atom. */
        private final String text;
    }

    /**
     * Postfix operator standing for a repetition of its operand of zero or more
     * occurrences.
     * @see Plus
     */
    @Syntax("expr %s")
    @ToolTipHeader("Zero or more")
    @ToolTipBody({"Matched by a path <i>p</i> if it is the concatenation of multiple",
        "fragments satisfying %1$s"})
    static public class Star extends Postfix {
        /** Creates the repetition of a given regular expression. */
        public Star(RegExpr operand) {
            super("" + STAR_OPERATOR, STAR_SYMBOLIC_NAME, operand);
        }

        /** Creates a prototype instance. */
        Star() {
            this(null);
        }

        @Override
        protected Postfix newInstance(RegExpr operand) {
            return new Star(operand);
        }

        /**
         * Calls {@link RegExprCalculator#computeStar(RegExpr.Star, Object)} on
         * the visitor.
         */
        @Override
        protected <Result> Result applyPostfix(RegExprCalculator<Result> visitor, Result arg) {
            return visitor.computeStar(this, arg);
        }

        @Override
        public boolean isAcceptsEmptyWord() {
            return true;
        }
    }

    /**
     * Postfix operator standing for a repetition of its operand of at least one
     * occurrence.
     * @see Star
     */
    @Syntax("expr %s")
    @ToolTipHeader("One or more")
    @ToolTipBody({"Matched by a path <i>p</i> if it is the concatenation of at least one",
        "fragment satisfying %1$s"})
    static public class Plus extends Postfix {
        /** Creates a non-empty repetition of a given regular expression. */
        public Plus(RegExpr operand) {
            super("" + PLUS_OPERATOR, PLUS_SYMBOLIC_NAME, operand);
        }

        /** Creates a prototype instance. */
        Plus() {
            this(null);
        }

        @Override
        protected Postfix newInstance(RegExpr operand) {
            return new Plus(operand);
        }

        /**
         * Calls {@link RegExprCalculator#computePlus(RegExpr.Plus, Object)} on
         * the visitor.
         */
        @Override
        protected <Result> Result applyPostfix(RegExprCalculator<Result> visitor, Result arg) {
            return visitor.computePlus(this, arg);
        }

        @Override
        public boolean isAcceptsEmptyWord() {
            return false;
        }
    }

    /**
     * Inversion is a prefix operator standing for a backwards interpretation of
     * its operand.
     * @see Neg
     */
    @Syntax("%s expr")
    @ToolTipHeader("Inversion")
    @ToolTipBody({"Matched by a path <i>p</i> that, when traversed backwards, satisfies %1$s."})
    static public class Inv extends Prefix {
        /** Creates the inversion of a given regular expression. */
        public Inv(RegExpr operand) {
            super("" + INV_OPERATOR, INV_SYMBOLIC_NAME, operand);
        }

        /** Creates a prototype instance. */
        Inv() {
            this(null);
        }

        @Override
        protected Prefix newInstance(RegExpr operand) {
            return new Inv(operand);
        }

        /**
         * Calls {@link RegExprCalculator#computeInv(RegExpr.Inv, Object)} on
         * the visitor.
         */
        @Override
        protected <Result> Result applyPrefix(RegExprCalculator<Result> visitor, Result arg) {
            return visitor.computeInv(this, arg);
        }

        @Override
        public boolean isAcceptsEmptyWord() {
            return getOperand().isAcceptsEmptyWord();
        }
    }

    /**
     * Negation is a prefix operator; the resulting expression applies
     * everywhere where the operand does not apply.
     * @see Inv
     */
    @Syntax("%s expr")
    @ToolTipHeader("Negation")
    @ToolTipBody({"Matched by any path <i>p</i> that does <i>not</i> satisfy %1$s."})
    static public class Neg extends Prefix {
        /** Creates the negation of a given regular expression. */
        public Neg(RegExpr operand) {
            super(NEG_OPERATOR, NEG_SYMBOLIC_NAME, operand);
        }

        /** Creates a prototype instance. */
        Neg() {
            this(null);
        }

        @Override
        protected Prefix newInstance(RegExpr operand) {
            return new Neg(operand);
        }

        /**
         * Calls {@link RegExprCalculator#computeNeg(RegExpr.Neg, Object)} on
         * the visitor.
         */
        @Override
        protected <Result> Result applyPrefix(RegExprCalculator<Result> visitor, Result arg) {
            return visitor.computeNeg(this, arg);
        }

        @Override
        public boolean isAcceptsEmptyWord() {
            return getOperand().isAcceptsEmptyWord();
        }
    }
}