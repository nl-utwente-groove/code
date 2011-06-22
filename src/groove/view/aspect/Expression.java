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

import static groove.algebra.Precedence.NONE;
import groove.algebra.Algebras;
import groove.algebra.Constant;
import groove.algebra.Operator;
import groove.algebra.Precedence;
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
abstract public class Expression {
    /** General constructor setting all fields. */
    protected Expression(Kind kind) {
        this.kind = kind;
    }

    /** Returns the kind of this expression. */
    public Kind getKind() {
        return this.kind;
    }

    /** Returns the type of this expression (as a signature name). */
    abstract public String getType();

    @Override
    public final String toString() {
        return toString(true);
    }

    /**
     * Returns a string representation of this expression, with an optional
     * type prefix.
     * @param withType if {@code true}, the string representation
     * will preceded by a type prefix if it is not a constant
     * @return the string representation of this expression
     */
    abstract String toString(boolean withType);

    /** Returns the string to be used by the GUI. */
    public final String toDisplayString() {
        return toDisplayString(Precedence.NONE);
    }

    /** Returns a string representation for this expression,
     * if it is placed in a context where the next higher operator
     * has a given precedence.
     */
    abstract String toDisplayString(Precedence context);

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        Expression other = (Expression) obj;
        return this.kind == other.kind;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result =
            prime * result + ((this.kind == null) ? 0 : this.kind.hashCode());
        return result;
    }

    private final Kind kind;

    /**
     * Attempts to parse a given string as an expression or assignment.
     * @param text the string that is to be parsed as expression
     * @return the resulting expression
     * @throws FormatException if the input string contains syntax errors
     */
    public static Object parse(String text) throws FormatException {
        Object result;
        try {
            result = Assignment.parse(text);
        } catch (FormatException e) {
            result = parse(text, null);
        }
        return result;
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
                return new Const(constant);
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
            return new Field(type, null, field);
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
            return new Field(type, owner, name);
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
        Call result = new Call(operator);
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

    /** Field expression. */
    public static class Field extends Expression {
        /** Constructs a call expression for a given operator. */
        public Field(String type, String owner, String field) {
            super(Kind.FIELD);
            this.type = type;
            this.owner = owner;
            this.field = field;
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
        @Override
        public String getType() {
            return this.type;
        }

        /** Returns the string to be used by the GUI. */
        @Override
        public String toDisplayString(Precedence precedence) {
            StringBuilder result = new StringBuilder();
            if (this.owner != null) {
                result.append(this.owner);
                result.append('.');
            }
            result.append(this.field);
            return result.toString();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!super.equals(obj)) {
                return false;
            }
            Field other = (Field) obj;
            if (!this.type.equals(other.type)) {
                return false;
            }
            boolean result =
                this.owner == null ? other.owner == null
                        : this.owner.equals(other.owner);
            return result && this.field.equals(other.field);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + this.field.hashCode();
            result =
                prime * result
                    + (this.owner == null ? 0 : this.owner.hashCode());
            result = prime * result + this.type.hashCode();
            return result;
        }

        @Override
        String toString(boolean withType) {
            StringBuilder result = new StringBuilder();
            if (withType) {
                result.append(getType());
                result.append(AspectParser.SEPARATOR);
            }
            if (this.owner != null) {
                result.append(this.owner);
                result.append('.');
            }
            result.append(this.field);
            return result.toString();
        }

        private final String owner;
        private final String field;
        private final String type;

    }

    /** Constant expression. */
    public static class Const extends Expression {
        /** Creates a constant expression for a given constant. */
        public Const(Constant constant) {
            super(Kind.CONSTANT);
            this.constant = constant;
        }

        /** Returns the constant, if this is an operator expression. */
        public Constant getConstant() {
            return this.constant;
        }

        @Override
        public String getType() {
            return getConstant().getSignature();
        }

        @Override
        public String toDisplayString(Precedence precedence) {
            StringBuilder result = new StringBuilder();
            result.append(getConstant().getSymbol());
            return result.toString();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!super.equals(obj)) {
                return false;
            }
            Const other = (Const) obj;
            return this.constant.equals(other.constant);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + this.constant.hashCode();
            return result;
        }

        @Override
        String toString(boolean withType) {
            StringBuilder result = new StringBuilder();
            result.append(getConstant().getSymbol());
            return result.toString();
        }

        private final Constant constant;

    }

    /** Operator call expression. */
    public static class Call extends Expression {
        /** Constructs a call expression for a given operator. */
        public Call(Operator operator) {
            super(Kind.CALL);
            this.operator = operator;
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

        @Override
        public String getType() {
            return getOperator().getResultType();
        }

        @Override
        String toDisplayString(Precedence context) {
            StringBuilder result = new StringBuilder();
            Precedence precedence = getOperator().getPrecedence();
            if (precedence == null) {
                assert getArguments().size() == getOperator().getArity();
                result.append(getOperator().getName());
                result.append('(');
                for (int i = 0; i < getArguments().size(); i++) {
                    if (i > 0) {
                        result.append(',');
                    }
                    result.append(getArguments().get(i).toDisplayString(NONE));
                }
                result.append(')');
            } else {
                if (precedence == Precedence.UNARY) {
                    assert getArguments().size() == 1;
                    result.append(getOperator().getSymbol());
                    result.append(getArguments().get(0).toDisplayString(
                        precedence));
                } else {
                    assert getArguments().size() == 2;
                    result.append(getArguments().get(0).toDisplayString(
                        precedence));
                    result.append(' ');
                    result.append(getOperator().getSymbol());
                    result.append(' ');
                    result.append(getArguments().get(1).toDisplayString(
                        precedence));
                }
                if (context.compareTo(precedence) > 0) {
                    result.insert(0, '(');
                    result.append(')');
                }
            }
            return result.toString();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!super.equals(obj)) {
                return false;
            }
            Call other = (Call) obj;
            return this.operator.equals(other.operator)
                && this.arguments.equals(other.arguments);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + this.arguments.hashCode();
            result = prime * result + this.operator.hashCode();
            return result;
        }

        @Override
        String toString(boolean withType) {
            StringBuilder result = new StringBuilder();
            assert getArguments().size() == getOperator().getArity();
            result.append(getOperator().getSignature());
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
            return result.toString();
        }

        private final Operator operator;
        private final List<Expression> arguments = new ArrayList<Expression>();

    }

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
