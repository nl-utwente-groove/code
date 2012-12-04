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
import static groove.graph.EdgeRole.BINARY;
import groove.algebra.Algebras;
import groove.algebra.Constant;
import groove.algebra.Operator;
import groove.algebra.Precedence;
import groove.algebra.SignatureKind;
import groove.graph.TypeLabel;
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
    abstract public SignatureKind getType();

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

    /**
     * Returns an expression obtained from this one by changing all
     * occurrences of a certain label into another.
     * @param oldLabel the label to be changed
     * @param newLabel the new value for {@code oldLabel}
     * @return a clone of this object with changed labels, or this object
     *         if {@code oldLabel} did not occur
     */
    abstract public Expression relabel(TypeLabel oldLabel, TypeLabel newLabel);

    private final Kind kind;

    /**
     * Attempts to parse a given string as an expression or assignment.
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
     * @param expectedType the expected type; if {@code null}, the type must be inferred
     * @return the resulting expression
     * @throws FormatException if the input string contains syntax errors
     */
    public static Expression parse(String text, SignatureKind expectedType)
        throws FormatException {
        Expression result;
        if (text.length() == 0) {
            throw new FormatException(
                "Empty string cannot be parsed as expression");
        }
        Pair<String,List<String>> splitText = parser.parse(text);
        String outer = splitText.one();
        // find the signature
        int pos = outer.indexOf(AspectParser.SEPARATOR);
        String sigName;
        SignatureKind declaredType = expectedType;
        if (pos >= 0) {
            sigName = outer.substring(0, pos);
            declaredType = SignatureKind.getKind(sigName);
            if (declaredType == null) {
                throw new FormatException("Unknown signature '%s'", sigName);
            }
        }
        String rest = outer.substring(pos + 1);
        switch (splitText.two().size()) {
        case 0:
            // the text does not have brackets or quotes
            if (rest.startsWith(PAR_PREFIX)) {
                return parseAsPar(rest, declaredType);
            }
            Constant constant = Algebras.getConstant(rest);
            if (constant == null) {
                return parseAsField(rest, declaredType);
            } else if (declaredType != null
                && constant.getSignature() != declaredType) {
                throw new FormatException(
                    "Declared type %s differs from actual constant type %s",
                    declaredType, constant.getSignature());
            } else {
                result = new Const(constant);
            }
            break;
        case 1:
            if (rest.charAt(rest.length() - 1) != ExprParser.PLACEHOLDER) {
                throw new FormatException("Can't parse '%s' as expression",
                    text);
            }
            String two = splitText.two().get(0);
            if (two.charAt(0) == '\"') {
                // double quoted: must be a string constant
                if (rest.length() > 1) {
                    throw new FormatException("Can't parse '%s' as expression",
                        text);
                }
                if (declaredType != null
                    && declaredType != SignatureKind.STRING) {
                    throw new FormatException(
                        "Declared type %s differs from actual constant type %s",
                        declaredType, SignatureKind.STRING);
                }
                result = new Const(Algebras.getConstant(two));
            } else {
                String operatorName = rest.substring(0, rest.length() - 1);
                result = parseAsOperator(operatorName, two, declaredType);
            }
            break;
        default:
            throw new FormatException("Can't parse '%s' as expression", text);
        }
        if (expectedType != null && result.getType() != expectedType) {
            throw new FormatException(
                "Actual type %s differs from expected type %s",
                result.getType(), expectedType);
        }
        return result;
    }

    private static Expression parseAsPar(String text, SignatureKind type)
        throws FormatException {
        assert text.startsWith(PAR_PREFIX);
        try {
            int nr = Integer.parseInt(text.substring(1));
            return new Par(type, nr);
        } catch (NumberFormatException exc) {
            throw new FormatException("%s is not a valid parameter expression",
                text);
        }
    }

    private static Expression parseAsField(String field, SignatureKind type)
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
            String argsText, SignatureKind signature) throws FormatException {
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
            SignatureKind argType = operator.getParamTypes().get(i);
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

    private static final String PAR_PREFIX = "$";
    private static final ExprParser parser = new ExprParser(
        ExprParser.PLACEHOLDER, new char[] {'\"'}, new char[] {'(', ')'});

    /** Field expression. */
    public static class Field extends Expression {
        /** Constructs a field expression for a given field. */
        public Field(SignatureKind type, String owner, String field) {
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
        public SignatureKind getType() {
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

        @Override
        public Expression relabel(TypeLabel oldLabel, TypeLabel newLabel) {
            if (oldLabel.getRole() == BINARY
                && oldLabel.text().equals(this.field)) {
                return new Field(getType(), getOwner(), newLabel.text());
            } else {
                return this;
            }
        }

        private final String owner;
        private final String field;
        private final SignatureKind type;
    }

    /** Parameter expression. */
    public static class Par extends Expression {
        /** Constructs a field expression for a given field. */
        public Par(SignatureKind type, int nr) {
            super(Kind.PAR);
            this.type = type;
            this.nr = nr;
        }

        /** Returns the identifier, if this is an identifier expression. */
        public int getNumber() {
            return this.nr;
        }

        /** 
         * Returns the signature of this expression.
         * The signature is {@code null} if the expression is an identifier.
         */
        @Override
        public SignatureKind getType() {
            return this.type;
        }

        /** Returns the string to be used by the GUI. */
        @Override
        public String toDisplayString(Precedence precedence) {
            StringBuilder result = new StringBuilder();
            result.append(PAR_PREFIX);
            result.append(getNumber());
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
            Par other = (Par) obj;
            return this.nr == other.nr;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + this.nr;
            return result;
        }

        @Override
        String toString(boolean withType) {
            StringBuilder result = new StringBuilder();
            if (withType) {
                result.append(getType());
                result.append(AspectParser.SEPARATOR);
            }
            result.append(toDisplayString());
            return result.toString();
        }

        @Override
        public Expression relabel(TypeLabel oldLabel, TypeLabel newLabel) {
            return this;
        }

        private final int nr;
        private final SignatureKind type;
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
        public SignatureKind getType() {
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

        @Override
        public Expression relabel(TypeLabel oldLabel, TypeLabel newLabel) {
            return this;
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
        public SignatureKind getType() {
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

        @Override
        public Expression relabel(TypeLabel oldLabel, TypeLabel newLabel) {
            Call result = this;
            if (oldLabel.getRole() == BINARY) {
                List<Expression> newArgs = new ArrayList<Expression>();
                boolean isNew = false;
                for (int i = 0; i < getArguments().size(); i++) {
                    Expression oldArg = getArguments().get(i);
                    Expression newArg = oldArg.relabel(oldLabel, newLabel);
                    newArgs.add(newArg);
                    isNew |= newArg != oldArg;
                }
                if (isNew) {
                    result = new Call(getOperator());
                    result.getArguments().addAll(newArgs);
                }
            }
            return result;
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
        FIELD,
        /** Parameter. */
        PAR;
    }
}
