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
package groove.util.parse;

import static groove.util.parse.ExprParser.TokenType.ID;
import static groove.util.parse.ExprParser.TokenType.INT;
import static groove.util.parse.ExprParser.TokenType.NAME;
import static groove.util.parse.ExprParser.TokenType.REAL;
import static groove.util.parse.ExprParser.TokenType.RPAR;
import static groove.util.parse.ExprParser.TokenType.STRING;
import groove.util.Triple;
import groove.util.parse.Expr.BoolContent;
import groove.util.parse.Expr.Content;
import groove.util.parse.Expr.Id;
import groove.util.parse.Expr.IdContent;
import groove.util.parse.Expr.IntContent;
import groove.util.parse.Expr.RealContent;
import groove.util.parse.Expr.StringContent;
import groove.util.parse.Precedence.Direction;
import groove.util.parse.Precedence.Placement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * General expression parser, parameterised with the type of operators to be recognised.
 * @author Arend Rensink
 * @version $Id$
 */
public class ExprParser<O extends Op> implements Parser<Expr<O>> {
    /**
     * Constructs a parser for a given operator type.
     * @param constOp operator instance to be used for parser constants;
     * should be parameterless and without a symbol
     * @param description description of the parsed expression values.
     */
    public ExprParser(O constOp, String description) {
        assert constOp.getArity() == 0 && !constOp.hasSymbol();
        this.errorOp = constOp;
        this.description = description;
    }

    /** Returns the type of expression operators that this parser handles. */
    @SuppressWarnings("unchecked")
    public Class<? extends O> getOpType() {
        return (Class<? extends O>) this.errorOp.getClass();
    }

    /** Returns the error operator used by this parser. */
    public O getErrorOp() {
        return this.errorOp;
    }

    private final O errorOp;

    @Override
    public String getDescription() {
        return this.description;
    }

    private final String description;

    @Override
    public boolean accepts(String text) {
        return !parse(text).hasErrors();
    }

    @Override
    public Expr<O> parse(String text) {
        return new Instance(text).parse();
    }

    @Override
    public String toParsableString(Object value) {
        return ((Expr<?>) value).toParsableString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<Expr<O>> getValueType() {
        return (Class<Expr<O>>) (Class<? extends Expr<?>>) Expr.class;
    }

    @Override
    public boolean isValue(Object value) {
        boolean result = value instanceof Expr;
        if (result) {
            Expr<?> expr = (Expr<?>) value;
            result = getOpType().equals(expr.getOp().getClass());
        }
        return result;
    }

    @Override
    public boolean hasDefault() {
        return false;
    }

    @Override
    public boolean isDefault(Object value) {
        return false;
    }

    @Override
    public Expr<O> getDefaultValue() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getDefaultString() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    SymbolMap getSymbolMap() {
        if (this.symbolMap == null) {
            List<Token> tokens = new ArrayList<Token>();
            for (O op : getOpType().getEnumConstants()) {
                if (op.hasSymbol()) {
                    tokens.add(new Token(TokenType.OP, op.getSymbol(), op));
                }
            }
            for (TokenType type : TokenType.values()) {
                Token token = type.token();
                if (token != null) {
                    tokens.add(token);
                }
            }
            this.symbolMap = new SymbolMap(tokens);
        }
        return this.symbolMap;
    }

    private SymbolMap symbolMap;

    /** Mapping to enable efficient scanning of tokens. */
    private class SymbolMap extends HashMap<Character,SymbolMap> {
        SymbolMap(List<Token> tokens) {
            this(tokens, "");
        }

        SymbolMap(List<Token> tokens, String prefix) {
            Token mine = null;
            for (Token token : tokens) {
                String symbol = token.getParseString();
                if (!symbol.startsWith(prefix)) {
                    continue;
                }
                if (symbol.equals(prefix)) {
                    assert mine == null;
                    mine = token;
                } else {
                    char next = symbol.charAt(prefix.length());
                    if (!containsKey(next)) {
                        put(next, new SymbolMap(tokens, prefix + next));
                    }
                }
            }
            this.mine = mine;
        }

        Token getToken() {
            return this.mine;
        }

        private final Token mine;
    }

    /** Throwaway instance of this parser. */
    private class Instance {
        Instance(String input) {
            this.input = input;
        }

        /** Parses the string with which this instance was initialised. */
        Expr<O> parse() {
            return parse(Precedence.NONE);
        }

        /** Parses the string with which this instance was initialised. */
        Expr<O> parse(Precedence context) {
            Expr<O> result = null;
            try {
                int start = nextIx();
                Token nextToken = next();
                switch (nextToken.one()) {
                case RPAR:
                case COMMA:
                    throw new FormatException("Unexpected token '%s' at index %s",
                        nextToken.getParseString(), nextIx());
                case OP:
                    consumeToken();
                    result = parsePrefixed(nextToken, start);
                    break;
                case LPAR:
                    consumeToken();
                    result = parseBracketed(nextToken, start);
                    break;
                default:
                    consumeToken();
                    result = createExpr(getErrorOp(), nextToken.getContent());
                }
                boolean more = hasNext();
                while (more & hasNext()) {
                    nextToken = next();
                    switch (nextToken.one()) {
                    case RPAR:
                    case BOOL:
                    case ID:
                    case INT:
                    case REAL:
                    case STRING:
                        throw new FormatException("Unexpected token '%s' at index %s",
                            nextToken.getParseString(), nextIx());
                    case OP:
                        O op = nextToken.getOp();
                        Precedence opPrec = op.getPrecedence();
                        int compare = context.compareTo(opPrec);
                        if (compare < 0 || context.getDirection() == Direction.LEFT && compare == 0) {
                            consumeToken();
                            Expr<O> arg0 = result;
                            result = createExpr(op);
                            switch (opPrec.getPlace()) {
                            case POSTFIX:
                                result.addArg(arg0);
                                more = false;
                                break;
                            case INFIX:
                                Expr<O> arg1 = parse(opPrec);
                                result.addArg(arg0);
                                result.addArg(arg1);
                                break;
                            case PREFIX:
                            }
                        } else {
                            more = false;
                        }
                        break;
                    default:
                        more = false;
                    }
                }
            } catch (FormatException exc) {
                if (result == null) {
                    result = createExpr(getErrorOp());
                }
                result.addErrors(exc.getErrors());
            }
            return result;
        }

        /**
         * Attempts to parse the string as a bracketed expression.
         * @return the expression in brackets, or {@code null} if the input string does not
         * correspond to a bracketed expression
         */
        private Expr<O> parseBracketed(Token first, int firstIx) {
            Expr<O> result = null;
            result = parse(Precedence.NONE);
            String error = "Unbalanced opening bracket at index " + firstIx;
            try {
                if (next().one() == RPAR) {
                    consumeToken();
                    error = null;
                }
            } catch (FormatException exc) {
                // do nothing
            }
            if (error != null) {
                result.addError(new FormatError(error));
            }
            return result;
        }

        /** Attempts to parse the string as an operator-prefixed expression.
         * @return an operator-prefixed expression, or {@code null} if the input string does not
         * start with an operator
         */
        Expr<O> parsePrefixed(Token opToken, int opIx) {
            Expr<O> result = null;
            O op = opToken.getOp();
            Precedence prec = op.getPrecedence();
            result = createExpr(op);
            result.addArg(parse(prec));
            if (prec.getPlace() != Placement.PREFIX) {
                result.addError(new FormatError("%s operator '%s' in prefix position at index %s",
                    StringHandler.toUpper(prec.getPlace().name().toLowerCase()), op.getSymbol(),
                    opIx));
            }
            return result;
        }

        /** Factory method for an expression with a given operator. */
        private Expr<O> createExpr(O op) {
            return createExpr(op, null);
        }

        /** Factory method for an expression with a given operator and content. */
        private Expr<O> createExpr(O op, Content<?> content) {
            Expr<O> result = new Expr<O>(op, content);
            result.setParseString(this.input);
            return result;
        }

        /** Returns {@code true} if scanning has not yet reached the end of the input. */
        private boolean hasNext() {
            return !atEnd();
        }

        /** Returns the next unconsumed token in the input stream. */
        private Token next() throws FormatException {
            if (this.nextToken == null && !atEnd()) {
                this.nextIx = this.ix;
                this.nextToken = scan();
            }
            if (this.nextToken == null) {
                throw new FormatException("Unexpected ent of line");
            }
            return this.nextToken;
        }

        /** Returns the start position of the token last returned by {@link #next()}. */
        private int nextIx() {
            return this.nextIx;
        }

        /** Consumes the current token, causing the next call or {@link #next()}
         * to scan the next token.
         */
        private void consumeToken() {
            this.nextToken = null;
        }

        private Token nextToken;
        private int nextIx;

        /**
         * Scans and returns the next token in the input string.
         * Whitespace should have been skipped before this method is invoked.
         */
        private Token scan() throws FormatException {
            Token result = scanStatic();
            if (result == null) {
                char c = charAt();
                if (Character.isDigit(c)) {
                    result = scanNumber();
                } else if (StringHandler.isIdentifierStart(c)) {
                    result = scanId();
                } else {
                    switch (c) {
                    case StringHandler.SINGLE_QUOTE_CHAR:
                    case StringHandler.DOUBLE_QUOTE_CHAR:
                        result = scanString();
                        break;
                    }
                }
            }
            if (result == null) {
                throw new FormatException("Unrecognised token at index " + this.ix);
            }
            return result;
        }

        /**
         * Scans in the next static token from the input string.
         * Whitespace should have been skipped before this method is invoked.
         * @return the next static token, or {@code null} if the input
         * is at an end or the next token is not static
         */
        private Token scanStatic() {
            Token result = null;
            int start = this.ix;
            SymbolMap map = getSymbolMap();
            while (start < this.input.length()) {
                char nextChar = this.input.charAt(this.ix);
                SymbolMap nextMap = map.get(nextChar);
                if (nextMap == null) {
                    // nextChar is not part of any operator symbol
                    result = map.getToken();
                    break;
                }
                start++;
                if (start == this.input.length()) {
                    // there is no next character after this
                    result = nextMap.getToken();
                    break;
                }
                map = nextMap;
            }
            if (result != null) {
                this.ix += result.getParseString().length();
            }
            return result;
        }

        private Token scanNumber() throws FormatException {
            int start = this.ix;
            while (!atEnd() && Character.isDigit(charAt())) {
                nextChar();
            }
            TokenType type = !atEnd() && charAt() == '.' ? REAL : INT;
            if (type == REAL) {
                nextChar();
                while (!atEnd() && Character.isDigit(charAt())) {
                    nextChar();
                }
            }
            Token result;
            String content = this.input.substring(start, this.ix);
            if (type == REAL && content.length() == 1) {
                throw new FormatException("Can't parse stand-alone '.' at index " + this.ix);
            }
            result = new Token(type, content);
            return result;
        }

        private Token scanId() throws FormatException {
            int start = this.ix;
            Token token = scanName();
            String prefix = null;
            List<String> names = new ArrayList<String>();
            if (hasNext()) {
                if (next().getType() == TokenType.COLON) {
                    consumeToken();
                    token = scanName();
                }
                names.add(token.getParseString());
                while (hasNext() && next().getType() == TokenType.DOT) {
                    consumeToken();
                    token = scanName();
                    names.add(token.getParseString());
                }
            }
            Id id = new Id(prefix, names);
            return new Token(ID, this.input.substring(start, this.ix), id);
        }

        private FormatException unexpectedEnd() {
            return new FormatException("Unexpected end of input");
        }

        private FormatException malformedId(int start) {
            return new FormatException("Malformed identifier '%s' at index %s",
                this.input.substring(start, this.ix), start);
        }

        private Token scanName() throws FormatException {
            int start = this.ix;
            if (atEnd()) {
                throw unexpectedEnd();
            } else if (!StringHandler.isIdentifierStart(charAt())) {
                throw malformedId(start);
            }
            nextChar();
            while (!atEnd() && StringHandler.isIdentifierPart(charAt())) {
                nextChar();
            }
            return new Token(NAME, this.input.substring(start, this.ix));
        }

        private Token scanString() throws FormatException {
            int start = this.ix;
            char quote = charAt();
            nextChar();
            boolean escaped = false;
            while (!atEnd() && (escaped || charAt() != quote)) {
                escaped = charAt() == StringHandler.ESCAPE_CHAR;
                nextChar();
            }
            if (atEnd()) {
                throw new FormatException("%s-quoted string is not closed", quote);
            } else {
                nextChar();
            }
            return new Token(STRING, this.input.substring(start, this.ix));
        }

        /** Consumes all whitespace characters from the input,
         * then tests whether the end of the input string has been reached. */
        private boolean atEnd() {
            while (this.ix < this.input.length()
                && Character.isWhitespace(this.input.charAt(this.ix))) {
                nextChar();
            }
            return this.ix == this.input.length();
        }

        private void nextChar() {
            this.ix++;
        }

        private char charAt() throws FormatException {
            if (this.ix < this.input.length()) {
                return this.input.charAt(this.ix);
            } else {
                throw unexpectedEnd();
            }

        }

        private int ix;
        private final String input;
    }

    static class Token extends Triple<TokenType,String,Object> {
        public Token(TokenType type, String parseString) {
            super(type, parseString, null);
        }

        public Token(TokenType one, String parseString, Object payload) {
            super(one, parseString, payload);
        }

        /** Returns the type of this token. */
        public TokenType getType() {
            return one();
        }

        /** Returns the token content as an operator. */
        @SuppressWarnings("unchecked")
        public <O extends Op> O getOp() {
            assert one() == TokenType.OP;
            return (O) three();
        }

        /** Returns the token payload as an identifier. */
        public Id getId() {
            assert one() == ID;
            return (Id) three();
        }

        /** Returns the string representation of the token content. */
        public String getParseString() {
            return two();
        }

        /** Returns the content wrapped in this token.
         * Only valid for non-static tokens.
         */
        public Content<?> getContent() {
            switch (one()) {
            case BOOL:
                return new BoolContent(getParseString());
            case INT:
                return new IntContent(getParseString());
            case REAL:
                return new RealContent(getParseString());
            case STRING:
                return new StringContent(getParseString());
            case ID:
                return new IdContent(getParseString(), getId());
            default:
                throw new UnsupportedOperationException();
            }
        }
    }

    static enum TokenType {
        /** Operator. */
        OP,
        /** Single- or double-quoted string value. */
        STRING,
        /** Integer value. */
        INT,
        /** Real-number value. */
        REAL,
        /** Boolean value. */
        BOOL,
        /**
         * Composite identifier, consisting of an optional prefix (an atomic name),
         * separated by a colon from the remainder, and a non-empty dot-separated list of atomic names.
         */
        ID,
        /**
         * Atomic name, formed like a Java identifier, with hyphens allowed in the middle.
         * @see StringHandler#isIdentifier(String)
         */
        NAME,
        /** A static token, representing a left parenthesis. */
        LPAR("("),
        /** A static token, representing a right parenthesis. */
        RPAR(")"),
        /** A static token, representing a comma. */
        COMMA(","),
        /** A static token, representing a dot. */
        DOT("."),
        /** A static token, representing a colon. */
        COLON(":"), ;

        private TokenType() {
            this(null);
        }

        private TokenType(String text) {
            this.token = text == null ? null : new Token(this, text);
        }

        /** Returns the default token of this type, if any. */
        public Token token() {
            return this.token;
        }

        private final Token token;
    }
}
