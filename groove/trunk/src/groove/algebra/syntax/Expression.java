/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
 * $Id$
 */
package groove.algebra.syntax;

import groove.algebra.Constant;
import groove.algebra.Precedence;
import groove.algebra.SignatureKind;
import groove.grammar.model.FormatException;
import groove.grammar.type.TypeLabel;
import groove.gui.look.Line;

import java.util.HashMap;
import java.util.Map;

import org.antlr.runtime.RecognitionException;

/**
 * Expressions are constants, variables, field expressions or call expressions.
 * @author Arend Rensink
 * @version $Revision $
 */
public abstract class Expression {
    /** Constructor for subclasses. */
    protected Expression() {
        this.kind = kindMap.get(getClass());
        assert this.kind != null;
    }

    /** Returns the type of this term. */
    public abstract SignatureKind getSignature();

    /**
     * Returns a text representation of the term.
     * The difference with {@link #toString()} is that
     * the display string does not contain type prefixes.
     */
    final public String toDisplayString() {
        return toLine().toFlatString();
    }

    /**
     * Builds the display string for this expression in the 
     * result parameter.
     */
    abstract protected void buildDisplayString(StringBuilder result,
            Precedence context);

    /**
     * Returns a text representation of the term.
     * The difference with {@link #toString()} is that
     * the display string does not contain type prefixes.
     */
    final public Line toLine() {
        return toLine(Precedence.NONE);
    }

    /**
     * Builds the display string for this expression in the 
     * result parameter.
     */
    abstract protected Line toLine(Precedence context);

    /** 
     * Returns a string representation from which
     * this expression has been parsed.
     * If the expression has been constructed rather
     * than parsed, returns the display string instead.
     * @see #toDisplayString()
     */
    public String toInputString() {
        if (this.inputString == null) {
            return toDisplayString();
        } else {
            return this.inputString;
        }
    }

    /** Sets the string from which this expression has been parsed. */
    public void setInputString(String inputString) {
        this.inputString = inputString;
    }

    /**
     * Indicates if this expression is a term,
     * i.e., an element of the appropriate term algebra.
     */
    public abstract boolean isTerm();

    /**
     * Indicates if this expression is closed, i.e., does not contain any variables.
     * Convenience method for {@code getVariables().isEmpty()}
     * @return {@code true} if the expression is closed.
     */
    public boolean isClosed() {
        return getVariables().isEmpty();
    }

    /**
     * Returns a mapping from all variables occurring in this expression
     * to the corresponding types.
     */
    public Map<String,SignatureKind> getVariables() {
        if (this.varMap == null) {
            this.varMap = computeVarMap();
        }
        return this.varMap;
    }

    /** Returns the precedence of the top-level operator of this expression,
     * or {@link Precedence#ATOM} if this is not a call expression.
     */
    public Precedence getPrecedence() {
        return Precedence.ATOM;
    }

    /** Factory method to create the variable map for this expression. */
    abstract protected Map<String,SignatureKind> computeVarMap();

    /**
     * Returns an expression obtained from this one by changing all
     * occurrences of a certain label into another.
     * In particular, this concerns field names.
     * @param oldLabel the label to be changed
     * @param newLabel the new value for {@code oldLabel}
     * @return a clone of this object with changed labels, or this object
     *         if {@code oldLabel} did not occur
     */
    public Expression relabel(TypeLabel oldLabel, TypeLabel newLabel) {
        return this;
    }

    /** Returns the expression kind of this expression. */
    public final Kind getKind() {
        return this.kind;
    }

    /** The expression kind of this expression. */
    private final Kind kind;
    /** The string from which this expression has been parsed, if any. */
    private String inputString;
    /** The mapping from variables occurring in this expression to their types. */
    private Map<String,SignatureKind> varMap;

    /**
     * Returns the expression tree for a given test:-content string. 
     * @param term the string to be parsed as a test-expression
     */
    public static Expression parseTest(String term) throws FormatException {
        return parseToTree(term, true).toExpression();
    }

    /**
     * Returns the expression tree for a given string. 
     * @param term the string to be parsed as an expression
     */
    public static Expression parse(String term) throws FormatException {
        return parseToTree(term, false).toExpression();
    }

    /**
     * Returns the expression tree for a given string. 
     * @param term the string to be parsed as an expression
     * @param test if {@code true}, {@link ExprParser#test_expression()}
     * is used for parsing, otherwise {@link ExprParser#expression()}
     */
    private static ExprTree parseToTree(String term, boolean test)
        throws FormatException {
        ExprParser parser = ExprParser.instance(term);
        try {
            ExprTree result =
                (ExprTree) (test ? parser.test_expression().getTree()
                        : parser.expression().getTree());
            parser.getErrors().throwException();
            return result;
        } catch (FormatException e) {
            throw new FormatException("Can't parse %s: %s", term,
                e.getMessage());
        } catch (RecognitionException re) {
            throw new FormatException(re.getMessage(), re.line,
                re.charPositionInLine);
        }
    }

    /** Call with &lt;expression> */
    public static void main(String[] args) {
        try {
            ExprTree tree = parseToTree(args[0], false);
            System.out.printf("Original expression: %s%n", args[0]);
            System.out.printf("Flattened term tree: %s%n", tree.toStringTree());
            System.out.printf("Corresponding term:  %s%n", tree.toExpression());
            System.out.printf("Display string:      %s%n",
                tree.toExpression().toDisplayString());
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

    static private final Map<Class<? extends Expression>,Kind> kindMap =
        new HashMap<Class<? extends Expression>,Expression.Kind>();

    static {
        kindMap.put(Constant.class, Kind.CONST);
        kindMap.put(Variable.class, Kind.VAR);
        kindMap.put(Parameter.class, Kind.PAR);
        kindMap.put(FieldExpr.class, Kind.FIELD);
        kindMap.put(CallExpr.class, Kind.CALL);
    }

    /** Expression kinds. */
    public static enum Kind {
        /** Constant expression. */
        CONST,
        /** Parameter expression. */
        PAR,
        /** Variable expression. */
        VAR,
        /** Field expression. */
        FIELD,
        /** Call expression. */
        CALL;
    }
}
