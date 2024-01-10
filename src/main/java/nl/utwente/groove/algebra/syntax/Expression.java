/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.algebra.syntax;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.Constant;
import nl.utwente.groove.algebra.Operator;
import nl.utwente.groove.algebra.Signature.OpValue;
import nl.utwente.groove.algebra.Sort;
import nl.utwente.groove.annotation.Help;
import nl.utwente.groove.annotation.HelpMap;
import nl.utwente.groove.grammar.type.TypeLabel;
import nl.utwente.groove.graph.EdgeRole;
import nl.utwente.groove.util.LazyFactory;
import nl.utwente.groove.util.line.Line;
import nl.utwente.groove.util.parse.FormatException;
import nl.utwente.groove.util.parse.OpKind;

/**
 * Expressions are constants, variables, field expressions or call expressions.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public sealed abstract class Expression permits Constant, Variable, FieldExpr, CallExpr {
    /**
     * Constructor for subclasses.
     * @param prefixed indicates if the expression was explicitly typed
     * by a type prefix in the parsed text
     */
    protected Expression(boolean prefixed) {
        this.prefixed = prefixed;
        this.kind = kindMap.get(getClass());
        assert this.kind != null;
    }

    /** Returns the type of this term. */
    public abstract Sort getSort();

    /**
     * Returns a text representation of the term.
     * The difference with {@link #toString()} is that
     * the display string does not contain type prefixes.
     */
    final public String toDisplayString() {
        return toLine().toFlatString();
    }

    /**
     * Returns a text representation of the term.
     * The difference with {@link #toString()} is that
     * the display string does not contain type prefixes.
     */
    final public Line toLine() {
        return toLine(OpKind.NONE);
    }

    /**
     * Builds the display string for this expression in the
     * result parameter.
     */
    abstract protected Line toLine(OpKind context);

    /**
     * Returns a string representation from which
     * this expression can be been parsed.
     * If the expression has been constructed rather
     * than parsed, calls {@link #createParseString()}.
     * @see #toDisplayString()
     */
    public String toParseString() {
        String result = this.parseString;
        if (result == null) {
            this.parseString = result = createParseString();
        }
        return result;
    }

    /**
     * Callback method to create the input string for such
     * expressions that were constructed rather than parsed.
     */
    abstract protected String createParseString();

    /** Sets the string from which this expression has been parsed. */
    public void setParseString(String parseString) {
        this.parseString = parseString;
    }

    /** The string from which this expression has been parsed, if any. */
    private @Nullable String parseString;

    /**
     * Indicates if this expression is a term,
     * i.e., an element of the appropriate term algebra.
     */
    public abstract boolean isTerm();

    /**
     * Indicates if this expression is closed, i.e., does not contain any variables.
     * Convenience method for {@code getTyping().isEmpty()}
     * @return {@code true} if the expression is closed.
     */
    public boolean isClosed() {
        return getTyping().isEmpty();
    }

    /**
     * Returns a mapping from all variables occurring in this expression
     * to the corresponding types.
     */
    public SortMap getTyping() {
        SortMap result = this.typing;
        if (result == null) {
            this.typing = result = computeTyping();
        }
        return result;
    }

    /** The mapping from variables occurring in this expression to their types. */
    private @Nullable SortMap typing;

    /** Factory method to create the variable map for this expression. */
    abstract protected SortMap computeTyping();

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

    /**
     * Indicates if the parsed text for this expression has an explicit
     * type prefix.
     */
    public boolean isPrefixed() {
        return this.prefixed;
    }

    /** Flag indicating if the parsed text for this expression had a type prefix. */
    private final boolean prefixed;

    /** Creates and returns a boolean constant, with given value.
     * Convenience method for {@link Constant#instance(Boolean)}.
     */
    static public Expression b(boolean value) {
        return Constant.instance(value);
    }

    /** Creates and returns an integer constant, with given value.
     * Convenience method for {@link Constant#instance(int)}.
     */
    static public Expression i(int value) {
        return Constant.instance(value);
    }

    /** Creates and returns a real constant, with given value.
     * Convenience method for {@link Constant#instance(double)}.
     */
    static public Expression r(double value) {
        return Constant.instance(value);
    }

    /** Creates and returns a string constant, with given value.
     * Convenience method for {@link Constant#instance(String)}.
     */
    static public Expression s(String value) {
        return Constant.instance(value);
    }

    /** Creates and returns a an expression consisting of the application
     * of a given operator to a given list of arguments.
     */
    static public Expression op(Operator op, Expression... args) {
        return new CallExpr(op, args);
    }

    /** Creates and returns a an expression consisting of the application
     * of a given operator to a given list of arguments.
     * The operator is given as an {@link OpValue}.
     */
    static public Expression op(OpValue op, Expression... args) {
        return op(op.getOperator(), args);
    }

    /** Creates and returns an expression consisting of the application
     * of a given operator to a given list of arguments.
     * The operator is given as an combination of sort and name or symbol.
     */
    static public Expression op(Sort sort, String name, Expression... args) {
        var op = Operator.getOp(sort, name);
        assert op != null;
        return op(op, args);
    }

    /** Creates and returns an expression consisting of the application
     * of a given boolean operator to a given list of arguments.
     */
    static public Expression bOp(String name, Expression... args) {
        return op(Sort.BOOL, name, args);
    }

    /** Creates and returns an expression consisting of the application
     * of a given integer operator to a given list of arguments.
     */
    static public Expression iOp(String name, Expression... args) {
        return op(Sort.INT, name, args);
    }

    /** Creates and returns an expression consisting of the application
     * of a given real operator to a given list of arguments.
     */
    static public Expression rOp(String name, Expression... args) {
        return op(Sort.REAL, name, args);
    }

    /** Creates and returns an expression consisting of the application
     * of a given string operator to a given list of arguments.
     */
    static public Expression sOp(String name, Expression... args) {
        return op(Sort.STRING, name, args);
    }

    /** Creates and returns a variable of given sort and name.
     */
    static public Expression var(Sort sort, String name) {
        return new Variable(name, sort);
    }

    /** Creates and returns a boolean variable with given name.
     */
    static public Expression bVar(String name) {
        return var(Sort.BOOL, name);
    }

    /** Creates and returns an integer variable with given name.
     */
    static public Expression iVar(String name) {
        return var(Sort.INT, name);
    }

    /** Creates and returns a real variable with given name.
     */
    static public Expression rVar(String name) {
        return var(Sort.REAL, name);
    }

    /** Creates and returns a string variable with given name.
     */
    static public Expression sVar(String name) {
        return var(Sort.STRING, name);
    }

    /** Creates and returns a field expression of given sort, (optional) target and field name.
     */
    static public Expression field(Sort sort, @Nullable String target, String name) {
        return new FieldExpr(target, name, sort);
    }

    /** Creates and returns a boolean field expression with given target and field name.
     */
    static public Expression bField(String target, String name) {
        return field(Sort.BOOL, target, name);
    }

    /** Creates and returns a boolean field expression with {@code null} target and given field name.
     */
    static public Expression bField(String name) {
        return field(Sort.BOOL, null, name);
    }

    /** Creates and returns an integer field expression with given target and field name.
     */
    static public Expression iField(String target, String name) {
        return field(Sort.INT, target, name);
    }

    /** Creates and returns an integer field expression with {@code null} target and given field name.
     */
    static public Expression iField(String name) {
        return field(Sort.INT, null, name);
    }

    /** Creates and returns a real-typed field expression with given target and field name.
     */
    static public Expression rField(String target, String name) {
        return field(Sort.REAL, target, name);
    }

    /** Creates and returns a real-typed field expression with {@code null} target and given field name.
     */
    static public Expression rField(String name) {
        return field(Sort.REAL, null, name);
    }

    /** Creates and returns a string-typed field expression with given target and field name.
     */
    static public Expression sField(String target, String name) {
        return field(Sort.STRING, target, name);
    }

    /** Creates and returns a string-typed field expression with {@code null} target and given field name.
     */
    static public Expression sField(String name) {
        return field(Sort.STRING, null, name);
    }

    /**
     * Returns the expression tree for a given test:-content string.
     * @param term the string to be parsed as a test-expression
     */
    public static ExprTree parseTest(String term) throws FormatException {
        return ExprTreeParser.parseExpr(term, true);
    }

    /**
     * Returns the expression tree for a given string.
     * @param term the string to be parsed as an expression
     */
    public static ExprTree parse(String term) throws FormatException {
        return ExprTreeParser.parseExpr(term, false);
    }

    /** Call with &lt;expression> */
    public static void main(String[] args) {
        try {
            ExprTree tree = ExprTreeParser.parseExpr(args[0], false);
            System.out.printf("Original expression: %s%n", args[0]);
            System.out.printf("Flattened term tree: %s%n", tree.toString());
            System.out.printf("Corresponding term:  %s%n", tree.toExpression());
            System.out.printf("Display string:      %s%n", tree.toExpression().toDisplayString());
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

    static private final Map<Class<? extends Expression>,Kind> kindMap = new HashMap<>();

    static {
        kindMap.put(Constant.class, Kind.CONST);
        kindMap.put(Variable.class, Kind.VAR);
        kindMap.put(FieldExpr.class, Kind.FIELD);
        kindMap.put(CallExpr.class, Kind.CALL);
    }

    /**
     * Returns a syntax helper mapping from syntax items
     * to (possibly {@code null}) tool tips.
     */
    public static HelpMap getDocMap() {
        return docMap.get();
    }

    /** Computes the documentation map for the edge roles. */
    private static HelpMap computeDocMap() {
        var result = new HelpMap();
        for (Field field : EdgeRole.class.getFields()) {
            if (field.isEnumConstant()) {
                result.add(Help.createHelp(field, EdgeRole.nameToSymbolMap));
            }
        }
        return result;
    }

    /** Syntax helper map, from syntax items to associated tool tips. */
    private static final LazyFactory<HelpMap> docMap
        = LazyFactory.instance(Expression::computeDocMap);

    /**
     * Mapping from keywords in syntax descriptions to corresponding text.
     */
    private static final Map<String,String> tokenMap;

    static {
        tokenMap = new HashMap<>();
        tokenMap.put("LPAR", "(");
        tokenMap.put("RPAR", ")");
        tokenMap.put("COMMA", ",");
        tokenMap.put("COLON", ":");
        tokenMap.put("TRUE", "true");
        tokenMap.put("FALSE", "false");
    }

    /** Expression kinds. */
    public static enum Kind {
        /** Constant expression. */
        CONST,
        /** Variable expression. */
        VAR,
        /** Field expression. */
        FIELD,
        /** Call expression. */
        CALL;
    }
}
