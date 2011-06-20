/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
package groove.view.aspect;

import groove.algebra.Algebras;
import groove.algebra.Constant;
import groove.algebra.Operator;
import groove.util.ExprParser;
import groove.util.Pair;
import groove.view.FormatException;

import java.util.ArrayList;
import java.util.List;

/**
 * Predicate in a rule graph.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Expression {
    /** General constructor setting all fields. */
    private Expression(Kind kind, String type, Operator operator,
            Constant constant, String owner, String field) {
        this.kind = kind;
        this.type = type;
        this.operator = operator;
        this.constant = constant;
        this.owner = owner;
        this.field = field;
    }

    /** Constructor for an operator expression. */
    public Expression(Operator operator) {
        this(Kind.CALL, operator.getResultType(), operator, null, null,
            null);
    }

    /** Constructor for a constant expression. */
    public Expression(Constant constant) {
        this(Kind.CONSTANT, constant.getSignature(), null, constant, null, null);
    }

    /** Constructor for an identifier expression. */
    public Expression(String signature, String owner, String field) {
        this(Kind.FIELD, signature, null, null, owner, field);
    }

    /** Returns the kind of this expression. */
    public Kind getKind() {
        return this.kind;
    }

    /** Returns the operator, if this is an operator expression. */
    public Operator getOperator() {
        return this.operator;
    }

    /**
     * Adds an argument to this expression.
     * This expression should be of kind {@link Kind#CALL}.
     */
    public void addArgument(Expression arg) {
        assert getKind() == Kind.CALL;
        this.arguments.add(arg);
    }

    /** Returns the arguments, if this is an operator expression. */
    public List<Expression> getArguments() {
        return this.arguments;
    }

    /** Returns the constant, if this is an operator expression. */
    public Constant getConstant() {
        return this.constant;
    }

    /** Returns the identifier, if this is an identifier expression. */
    public String getOwner() {
        return this.owner;
    }

    /** Returns the identifier, if this is an identifier expression. */
    public String getField() {
        return this.field;
    }

    /** 
     * Returns the signature of this expression.
     * The signature is {@code null} if the expression is an identifier.
     */
    public String getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return toString(true);
    }

    /**
     * Returns a string representation of this expression, with an optional
     * type prefix.
     * @param withType if {@code true}, the string representation
     * will preceded by a type prefix if it is not a constant
     * @return the string representation of this expression
     */
    String toString(boolean withType) {
        StringBuilder result = new StringBuilder();
        switch (getKind()) {
        case CONSTANT:
            result.append(getConstant().getSymbol());
            break;
        case FIELD:
            if (withType) {
                result.append(getType());
                result.append(AspectParser.SEPARATOR);
            }
            if (this.owner != null) {
                result.append(this.owner);
                result.append('.');
            }
            result.append(this.field);
            break;
        case CALL:
            assert getArguments().size() == getOperator().getArity();
            result.append(getType());
            result.append(AspectParser.SEPARATOR);
            result.append(getOperator().getName());
            result.append('(');
            for (int i = 0; i < getArguments().size(); i++) {
                if (i > 0) {
                    result.append(',');
                }
                result.append(getArguments().get(i).toString(false));
            }
            result.append(')');
            break;
        default:
            assert false;
        }
        return result.toString();
    }

    /** Returns the string to be used by the GUI. */
    public String getDisplayString() {
        StringBuilder result = new StringBuilder();
        switch (getKind()) {
        case CONSTANT:
            result.append(getConstant().getSymbol());
            break;
        case FIELD:
            if (this.owner != null) {
                result.append(this.owner);
                result.append('.');
            }
            result.append(this.field);
            break;
        case CALL:
            assert getArguments().size() == getOperator().getArity();
            result.append(getOperator().getName());
            result.append('(');
            for (int i = 0; i < getArguments().size(); i++) {
                if (i > 0) {
                    result.append(',');
                }
                result.append(getArguments().get(i).getDisplayString());
            }
            result.append(')');
            break;
        default:
            assert false;
            return null;
        }
        return result.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result =
            prime * result
                + ((this.arguments == null) ? 0 : this.arguments.hashCode());
        result =
            prime * result
                + ((this.constant == null) ? 0 : this.constant.hashCode());
        result =
            prime * result + ((this.owner == null) ? 0 : this.owner.hashCode());
        result =
            prime * result + ((this.field == null) ? 0 : this.field.hashCode());
        result =
            prime * result + ((this.kind == null) ? 0 : this.kind.hashCode());
        result =
            prime * result
                + ((this.operator == null) ? 0 : this.operator.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Expression)) {
            return false;
        }
        Expression other = (Expression) obj;
        if (this.kind != other.kind) {
            return false;
        }
        switch (this.kind) {
        case CONSTANT:
            return this.constant.equals(other.constant);
        case FIELD:
            boolean result =
                this.owner == null ? other.owner == null
                        : this.owner.equals(other.owner);
            return result && this.field.equals(other.field);
        case CALL:
            return this.operator.equals(other.operator)
                && this.arguments.equals(other.arguments);
        default:
            assert false;
            return false;
        }
    }

    private final Kind kind;
    private final String type;
    private final Operator operator;
    private final List<Expression> arguments = new ArrayList<Expression>();
    private final Constant constant;
    private final String owner;
    private final String field;

    /**
     * Attempts to parse a given string as an expression.
     * @param text the string that is to be parsed as expression
     * @return the resulting expression
     * @throws FormatException if the input string contains syntax errors
     */
    public static Expression parse(String text) throws FormatException {
        return parse(text, null);
    }

    /**
     * Attempts to parse a given string as an expression.
     * @param text the string that is to be parsed as expression
     * @param type the expected type; if {@code null}, the type must be inferred
     * @return the resulting expression
     * @throws FormatException if the input string contains syntax errors
     */
    public static Expression parse(String text, String type)
        throws FormatException {
        if (text.length() == 0) {
            throw new FormatException(
                "Empty string cannot be parsed as expression");
        }
        Pair<String,List<String>> splitText = parser.parse(text);
        String outer = splitText.one();
        // find the signature
        int pos = outer.indexOf(AspectParser.SEPARATOR);
        //        if (pos < 0 && type == null) {
        //            throw new FormatException(
        //                "Cannot determine type of '%s'", text);
        //        }
        String signature = type;
        if (pos >= 0) {
            signature = outer.substring(0, pos);
            if (!Algebras.isSigName(signature)) {
                throw new FormatException("Unknown signature '%s'", signature,
                    text);
            }
            if (type != null && !signature.equals(type)) {
                throw new FormatException(
                    "Declared type %s differs from inferred type %s",
                    signature, type);
            }
        }
        String rest = outer.substring(pos + 1);
        switch (splitText.two().size()) {
        case 0:
            Constant constant = Algebras.getConstant(rest);
            if (constant == null) {
                return parseAsField(rest, signature);
            } else if (signature != null
                && !constant.getSignature().equals(signature)) {
                throw new FormatException(
                    "Declared type %s differs from constant type %s",
                    signature, constant.getSignature());
            } else {
                return new Expression(constant);
            }
        case 1:
            if (rest.charAt(rest.length() - 1) != ExprParser.PLACEHOLDER) {
                throw new FormatException("Can't parse '%s' as expression",
                    text);
            }
            String operatorName = rest.substring(0, rest.length() - 1);
            return parseAsOperator(operatorName, splitText.two().get(0),
                signature);
        default:
            throw new FormatException("Can't parse '%s' as expression", text);
        }
    }

    private static Expression parseAsField(String field, String type)
        throws FormatException {
        if (type == null) {
            throw new FormatException("Missing type declaration for field %s",
                field);
        }
        int pos = field.indexOf('.');
        if (pos < 0) {
            if (!isIdentifier(field)) {
                throw new FormatException(
                    "Field name '%s' is not a valid identifier", field);
            }
            return new Expression(type, null, field);
        } else {
            String owner = field.substring(0, pos);
            if (!isIdentifier(owner)) {
                throw new FormatException(
                    "Field owner '%s' is not a valid identifier", owner);
            }
            String name = field.substring(pos + 1);
            if (!isIdentifier(name)) {
                throw new FormatException(
                    "Field name '%s' is not a valid identifier", name);
            }
            return new Expression(type, owner, name);
        }
    }

    /**
     * Attempts to parse a given pair of operator name and argument text
     * as an operator invocation
     * @param operatorName the operator name
     * @param argsText the argument text: a parenthesised, comma-separated list of 
     * argument expressions
     * @param signature the signature of the operator; non-{@code null} 
     * @return the operator expression, if parsing was successful
     * @throws FormatException if there is a syntax error
     */
    private static Expression parseAsOperator(String operatorName,
            String argsText, String signature) throws FormatException {
        if (signature == null) {
            throw new FormatException(
                "Missing type declaration for operator %s", operatorName);
        }
        if (argsText.charAt(0) != '(') {
            throw new FormatException("Can't parse '%s' as argument list",
                argsText);
        }
        Operator operator = Algebras.getOperator(signature, operatorName);
        if (operator == null) {
            throw new FormatException("No operator '%s' in signature '%s'",
                operatorName, signature);
        }
        Expression result = new Expression(operator);
        int arity = result.getOperator().getArity();
        String[] argsArray =
            parser.split(argsText.substring(1, argsText.length() - 1), ",");
        if (arity != argsArray.length) {
            throw new FormatException(
                "Wrong argument count %s (expected %s) for '%s'",
                argsArray.length, arity, operatorName);
        }
        for (int i = 0; i < argsArray.length; i++) {
            String argType = operator.getParamTypes().get(i);
            result.addArgument(parse(argsArray[i], argType));
        }
        return result;
    }

    private static boolean isIdentifier(String text) {
        boolean result = text.length() > 0;
        if (result) {
            result = Character.isJavaIdentifierStart(text.charAt(0));
            for (int i = 1; result && i < text.length(); i++) {
                result = Character.isJavaIdentifierPart(text.charAt(i));
            }
        }
        return result;
    }

    private static final ExprParser parser = new ExprParser(
        ExprParser.PLACEHOLDER, new char[] {}, new char[] {'(', ')'});

    /** Expression kind. */
    public static enum Kind {
        /** Algebraic operator. */
        CALL,
        /** Data constant. */
        CONSTANT,
        /** Identifier. */
        FIELD;
    }
}
