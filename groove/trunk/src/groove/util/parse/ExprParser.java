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

import static groove.algebra.SignatureKind.INT;
import static groove.algebra.SignatureKind.REAL;
import static groove.util.parse.ExprParser.TokenClaz.EOT;
import static groove.util.parse.ExprParser.TokenClaz.LPAR;
import static groove.util.parse.ExprParser.TokenClaz.NAME;
import static groove.util.parse.ExprParser.TokenClaz.RPAR;
import groove.algebra.BoolSignature;
import groove.algebra.Constant;
import groove.algebra.SignatureKind;
import groove.io.Util;
import groove.util.Duo;
import groove.util.Pair;
import groove.util.Triple;
import groove.util.parse.OpKind.Direction;
import groove.util.parse.OpKind.Placement;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * General expression parser, parameterised with the type of operators to be recognised.
 * The parser operates according to the following rules:
 * <code><ul>
 * <li> EX ::= ID
 * <br>     | LITERAL
 * <br>     | ID '(' (EX (',' EX)*)? ')'
 * <br>     | prefix-op EX
 * <br>     | EX infix-op EX
 * <br>     | EX postfix-op
 * <br>     | '(' EX ')'
 * <li> ID ::= (NAME ':')? NAME ('.' NAME)*
 * </ul></code>
 * Here, <code>LITERAL</code> is a literal data constant, and <code>NAME</code> a name
 * formed according to the Java rules, where additionally hyphens are allowed inside names.
 * <p>
 * Identifier prefixes and identifier qualification are only enabled set in the constructor;
 * call expressions are only enabled if the
 * passed-in operator type includes an operator of kind {@link OpKind#CALL}.
 * @author Arend Rensink
 * @version $Id$
 */
public class ExprParser<O extends Op> implements Parser<Expr<O>> {
    /**
     * Constructs a parser for a given operator type, without 
     * prefixed or qualified identifiers.
     * @param atomOp operator instance to be used for parser constants;
     * should be of kind {@link OpKind#ATOM}
     * @param description description of the parsed expression values.
     */
    public ExprParser(O atomOp, String description) {
        this(atomOp, false, false, description);
    }

    /**
     * Constructs a parser for a given operator type.
     * @param atomOp operator instance to be used for parser constants;
     * should be of kind {@link OpKind#ATOM}
     * @param prefixIds if {@code true}, IDs can have an optional prefix (a name),
     * separated from the main ID by the #createPrefixSymbol
     * @param qualIds if {@code true}, IDs can be qualified (consisting of
     * a #createQualSymbol-separated names)
     * separated from the main ID by the #createPrefixSymbol
     * @param description description of the parsed expression values.
     */
    public ExprParser(O atomOp, boolean prefixIds, boolean qualIds, String description) {
        assert atomOp.getKind() == OpKind.ATOM;
        this.atomOp = atomOp;
        this.description = description;
        this.prefixIds = prefixIds;
        this.qualIds = qualIds;
    }

    /** Returns the type of expression operators that this parser handles. */
    @SuppressWarnings("unchecked")
    public Class<? extends O> getOpType() {
        return (Class<? extends O>) this.atomOp.getClass();
    }

    /** Indicates if the operator type has a call operator. */
    boolean hasCallOp() {
        return getCallOp() != null;
    }

    /** Returns the call operator, if any. */
    public O getCallOp() {
        if (this.callOp == null) {
            this.callOp = retrieveKindOp(OpKind.CALL);
        }
        return this.callOp;
    }

    private O callOp;

    /** Indicates if the parser recognises prefixed identifiers. */
    public boolean hasPrefixIds() {
        return this.prefixIds;
    }

    private final boolean prefixIds;

    /** Returns the token separating the prefix from the main ID,
     * if {@link #hasPrefixIds()} holds.
     */
    TokenType getPrefixIdToken() {
        assert hasPrefixIds();
        if (this.prefixIdToken == null) {
            this.prefixIdToken = new TokenType(getPrefixSep());
        }
        return this.prefixIdToken;
    }

    private TokenType prefixIdToken;

    /** Callback factory method for the separator symbol between prefix
     * and main ID, if {@link #hasPrefixIds()} holds.
     */
    protected String getPrefixSep() {
        return ":";
    }

    /** Indicates if the parser recognises qualified identifiers. */
    boolean hasQualIds() {
        return this.qualIds;
    }

    private final boolean qualIds;

    /** Returns the token separating the prefix from the main ID,
     * if {@link #hasQualIds()} holds.
     */
    TokenType getQualSepToken() {
        assert hasQualIds();
        if (this.qualSepToken == null) {
            this.qualSepToken = new TokenType(getQualSep());
        }
        return this.qualSepToken;
    }

    private TokenType qualSepToken;

    /** Callback factory method for the separator symbol between prefix
     * and main ID, if {@link #hasPrefixIds()} holds.
     */
    protected String getQualSep() {
        return ".";
    }

    /** Retrieves the (supposedly unique) operator of a given kind. */
    private O retrieveKindOp(OpKind kind) {
        O result = null;
        for (O op : getOpType().getEnumConstants()) {
            if (op.getKind() == kind) {
                if (result == null) {
                    result = op;
                } else {
                    throw new IllegalArgumentException(String.format(
                        "Duplicate %s operators %s and %s", kind, result, op));
                }
            }
        }
        return result;
    }

    /** Returns the atom operator used by this parser. */
    public O getAtomOp() {
        return this.atomOp;
    }

    private final O atomOp;

    @Override
    public String getDescription() {
        return this.description;
    }

    private final String description;

    /**
     * Callback factory method for the expression objects to be constructed.
     * May be overridden to specialise the expression type.
     */
    protected Expr<O> createExpr(O op) {
        return new Expr<O>(op);
    }

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

    /** Returns the map from symbols to predefined (parsable) token types of this parser. */
    Map<String,TokenType> getTokens() {
        if (this.tokens == null) {
            this.tokens = new TreeMap<String,TokenType>();
            for (O op : getOpType().getEnumConstants()) {
                if (op.hasSymbol()) {
                    String symbol = op.getSymbol();
                    TokenType opToken = this.tokens.get(symbol);
                    OpFamily<O> family;
                    if (opToken == null) {
                        family = new OpFamily<O>(op);
                        addToken(new TokenType(family));
                    } else {
                        family = opToken.op();
                        family.add(op);
                    }
                }
            }
            for (TokenClaz claz : TokenClaz.values()) {
                if (claz.parsable()) {
                    addToken(claz.getSingleType());
                }
            }
            if (hasPrefixIds()) {
                addToken(getPrefixIdToken());
            }
            if (hasQualIds()) {
                addToken(getQualSepToken());
            }
        }
        return this.tokens;
    }

    private void addToken(TokenType token) {
        TokenType oldType = this.tokens.put(token.symbol(), token);
        assert oldType == null;
    }

    private Map<String,TokenType> tokens;

    /** Returns the fixed token type for a given signature kind. */
    TokenType getTokenType(SignatureKind sig) {
        return getSigTokenMap().get(sig);
    }

    private Map<SignatureKind,TokenType> getSigTokenMap() {
        if (this.sigTokenMap == null) {
            this.sigTokenMap = new EnumMap<SignatureKind,TokenType>(SignatureKind.class);
            for (SignatureKind sig : SignatureKind.values()) {
                this.sigTokenMap.put(sig, new TokenType(sig));
            }
        }
        return this.sigTokenMap;
    }

    private Map<SignatureKind,TokenType> sigTokenMap;

    /** Returns the symbol table for this parser. */
    SymbolTable getSymbolTable() {
        if (this.symbolTable == null) {
            this.symbolTable = new SymbolTable(getTokens().values());
        }
        return this.symbolTable;
    }

    private SymbolTable symbolTable;

    /** Mapping to enable efficient scanning of tokens. */
    private class SymbolTable extends HashMap<Character,SymbolTable> {
        SymbolTable(Collection<TokenType> tokens) {
            this(tokens, "");
        }

        SymbolTable(Collection<TokenType> tokens, String prefix) {
            TokenType mine = null;
            for (TokenType token : tokens) {
                String symbol = token.symbol();
                if (!symbol.startsWith(prefix)) {
                    continue;
                }
                if (symbol.equals(prefix)) {
                    if (mine != null) {
                        throw new IllegalArgumentException("Duplicate token " + symbol);
                    }
                    mine = token;
                } else {
                    char next = symbol.charAt(prefix.length());
                    if (!containsKey(next)) {
                        put(next, new SymbolTable(tokens, prefix + next));
                    }
                }
            }
            this.mine = mine;
        }

        TokenType getTokenType() {
            return this.mine;
        }

        private final TokenType mine;
    }

    /** Throwaway instance of this parser, initialised on a given string. */
    private class Instance {
        Instance(String input) {
            this.input = input;
        }

        /** Parses the string with which this instance was initialised. */
        Expr<O> parse() {
            Expr<O> result;
            try {
                result = parse(OpKind.NONE);
                if (next() != eot()) {
                    result.addErrors(unexpectedToken(next()));
                    result.addError(new FormatError("Unparsed suffix: %s", this.input.substring(
                        next().start(), this.input.length())));
                }
            } catch (FormatException exc) {
                result = createErrorExpr(exc);
            }
            return result;
        }

        /** Parses the string with which this instance was initialised. */
        @SuppressWarnings("unchecked")
        private Expr<O> parse(OpKind context) throws FormatException {
            Expr<O> result = null;
            Token nextToken = next();
            switch (nextToken.claz()) {
            case OP:
                result = parsePrefixed();
                break;
            case LPAR:
                result = parseBracketed();
                break;
            case NAME:
                result = parseCall();
                break;
            case CONST:
                consume();
                result = createConstantExpr(nextToken.createConstant());
                break;
            default:
                throw unexpectedToken(nextToken);
            }
            while (next().claz() != EOT) {
                nextToken = next();
                if (nextToken.claz() != TokenClaz.OP) {
                    break;
                }
                OpFamily<O> ops = nextToken.ops();
                if (!ops.hasLatefixOp()) {
                    break;
                }
                O op = ops.latefixOp();
                OpKind kind = op.getKind();
                if (context.compareTo(kind) > 0) {
                    break;
                }
                if (context.equals(kind) && kind.getDirection() != Direction.RIGHT) {
                    break;
                }
                consume();
                if (kind.getPlace() == Placement.POSTFIX) {
                    result = createNodrmalExpr(op, result);
                    break;
                }
                result = createNodrmalExpr(op, result, parse(kind));
                if (kind.getDirection() == Direction.NEITHER) {
                    break;
                }
            }
            return result;
        }

        /**
         * Attempts to parse the string as a bracketed expression.
         * @return the expression in brackets, or {@code null} if the input string does not
         * correspond to a bracketed expression
         */
        private Expr<O> parseBracketed() throws FormatException {
            assert next().claz() == LPAR;
            Expr<O> result = null;
            consume();
            result = parse(OpKind.NONE);
            if (next().claz() != RPAR) {
                throw unbalancedBracket(next());
            }
            consume();
            return result;
        }

        /**
         * Attempts to parse the string as a prefix expression.
         * The next token is known to be an operator (though not necessarily
         * a prefix operator).
         */
        @SuppressWarnings("unchecked")
        private Expr<O> parsePrefixed() throws FormatException {
            Expr<O> result = null;
            Token opToken = next();
            consume();
            OpFamily<O> ops = opToken.ops();
            assert ops != null;
            if (ops.hasPrefixOp()) {
                O op = ops.prefixOp();
                Expr<O> arg = parse(op.getKind());
                result = createNodrmalExpr(op, arg);
            } else {
                result = parse();
                result.addErrors(noPrefixOp(opToken));
            }
            return result;
        }

        /** Parses the input as a call expression. */
        private Expr<O> parseCall() throws FormatException {
            assert next().claz() == NAME;
            Expr<O> result = parseId();
            if (next().claz() == LPAR) {
                if (!hasCallOp()) {
                    throw unexpectedToken(next());
                }
                consume();
                result = createIdExpr(getCallOp(), result.getId());
                if (next().claz() != RPAR) {
                    result.addArg(parse(OpKind.NONE));
                    while (next().claz() == TokenClaz.COMMA) {
                        consume();
                        result.addArg(parse(OpKind.NONE));
                    }
                }
                if (next().claz() != RPAR) {
                    throw unbalancedBracket(next());
                }
                consume();
            }
            return result;
        }

        /** Parses the input as a call expression. */
        private Expr<O> parseId() throws FormatException {
            assert next().claz() == NAME;
            Expr<O> result;
            Token nameToken = next();
            consume();
            Id id;
            if (next().type() == getPrefixIdToken()) {
                consume();
                id = new Id(nameToken.substring());
                if (next().claz() != NAME) {
                    throw unexpectedToken(next());
                }
                nameToken = next();
                consume();
            } else {
                id = new Id(null);
            }
            id.addName(nameToken.substring());
            result = createIdExpr(getAtomOp(), id);
            while (next().type() == getQualSepToken()) {
                consume();
                if (next().claz() != NAME) {
                    throw unexpectedToken(next());
                }
                id.addName(next().substring());
                consume();
            }
            return result;
        }

        /** Factory method for an expression with a given operator 
         * and list of arguments. 
         */
        private Expr<O> createNodrmalExpr(O op, Expr<O>... args) {
            Expr<O> result = createExpr(op);
            for (Expr<O> arg : args) {
                result.addArg(arg);
            }
            result.setParseString(this.input);
            return result;
        }

        /**
         * Factory method for an expression with a given operator and identifier,
         * and a list of arguments. 
         */
        private Expr<O> createIdExpr(O op, Id id) {
            Expr<O> result = createExpr(op);
            result.setId(id);
            result.setParseString(this.input);
            return result;
        }

        /** Factory method for a constant expression. */
        private Expr<O> createConstantExpr(Constant constant) {
            Expr<O> result = createExpr(getAtomOp());
            result.setConstant(constant);
            result.setParseString(this.input);
            return result;
        }

        /** Factory method for atomic expression with a given error. */
        private Expr<O> createErrorExpr(FormatException exc) {
            Expr<O> result = createExpr(getAtomOp());
            result.setParseString(this.input);
            result.addErrors(exc);
            return result;
        }

        /** Returns the next unconsumed token in the input stream. */
        private Token next() throws FormatException {
            if (this.nextToken == null) {
                this.nextToken = scan();
            }
            return this.nextToken;
        }

        /** Consumes the current token, causing the next call of {@link #next()}
         * to scan the next token.
         */
        private void consume() {
            this.nextToken = null;
        }

        private Token nextToken;

        /**
         * Scans and returns the next token in the input string.
         * Whitespace should have been skipped before this method is invoked.
         */
        private Token scan() throws FormatException {
            Token result = null;
            if (atEnd()) {
                result = eot();
            } else if (Character.isDigit(charAt())) {
                result = scanNumber();
            } else if (StringHandler.isIdentifierStart(charAt())) {
                result = scanName();
            } else {
                switch (charAt()) {
                case StringHandler.SINGLE_QUOTE_CHAR:
                case StringHandler.DOUBLE_QUOTE_CHAR:
                    result = scanString();
                    break;
                case '.':
                    int nextIx = this.ix + 1;
                    if (nextIx == this.input.length()) {
                        break;
                    }
                    if (!Character.isDigit(this.input.charAt(nextIx))) {
                        break;
                    }
                    result = scanNumber();
                }
            }
            if (result == null) {
                result = scanStatic();
            }
            if (result == null) {
                throw unrecognisedToken();
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
            TokenType type = null;
            int start = this.ix;
            int end = start;
            SymbolTable map = getSymbolTable();
            while (end < this.input.length()) {
                char nextChar = this.input.charAt(end);
                SymbolTable nextMap = map.get(nextChar);
                if (nextMap == null) {
                    // nextChar is not part of any operator symbol
                    type = map.getTokenType();
                    break;
                }
                end++;
                if (end == this.input.length()) {
                    // there is no next character after this
                    type = nextMap.getTokenType();
                    break;
                }
                map = nextMap;
            }
            Token result = null;
            if (atEnd()) {
                result = eot();
            } else if (type != null) {
                this.ix = end;
                result = new Token(type, createFragment(start, end));
            }
            return result;
        }

        /** Scans in a number token from the input text.
         * It is guaranteed that the current character is a digit or decimal point;
         * if a decimal point, the next character is a digit.
         */
        private Token scanNumber() {
            assert Character.isDigit(charAt()) || charAt() == '.'
                && Character.isDigit(this.input.charAt(this.ix + 1));
            int start = this.ix;
            while (!atEnd() && Character.isDigit(charAt())) {
                nextChar();
            }
            SignatureKind sig = !atEnd() && charAt() == '.' ? REAL : INT;
            if (sig == REAL) {
                nextChar();
                while (!atEnd() && Character.isDigit(charAt())) {
                    nextChar();
                }
            }
            return new Token(getTokenType(sig), createFragment(start, this.ix));
        }

        /**
         * Scans a name token.
         * The current character is guaranteed to be an identifier start.
         */
        private Token scanName() {
            assert StringHandler.isIdentifierStart(charAt());
            int start = this.ix;
            nextChar();
            while (!atEnd() && StringHandler.isIdentifierPart(charAt())) {
                nextChar();
            }
            if (!StringHandler.isIdentifierEnd(this.input.charAt(this.ix - 1))) {
                prevChar();
            }
            LineFragment fragment = createFragment(start, this.ix);
            String substring = fragment.substring();
            if (substring.equals(BoolSignature.TRUE.toDisplayString())
                || substring.equals(BoolSignature.FALSE.toDisplayString())) {
                return new Token(getTokenType(SignatureKind.BOOL), fragment);
            } else {
                return new Token(NAME.getSingleType(), fragment);
            }
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
                assert charAt() == quote;
                nextChar();
            }
            return new Token(getTokenType(SignatureKind.STRING), createFragment(start, this.ix));
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

        private void prevChar() {
            this.ix--;
        }

        private char charAt() {
            return this.input.charAt(this.ix);
        }

        private int ix;
        private final String input;

        /** End-of-text token. */
        private Token eot() {
            if (this.eot == null) {
                int end = this.input.length();
                this.eot = new Token(TokenClaz.EOT.getSingleType(), createFragment(end, end));
            }
            return this.eot;
        }

        private Token eot;

        private FormatException unexpectedToken(Token token) {
            if (token.claz() == EOT) {
                return new FormatException("Unexpected end of input");
            } else {
                return new FormatException("Unexpected token '%s' at index %s", token.substring(),
                    token.start());
            }
        }

        private FormatException unrecognisedToken() {
            return new FormatException("Unrecognised token '%s' at index %s", charAt(), this.ix);
        }

        private FormatException unbalancedBracket(Token token) {
            return new FormatException("Expected ')' rather than %s at index %s", token == eot()
                ? "end of line" : "'" + token.substring() + "'", token.start());
        }

        private FormatException noPrefixOp(Token opToken) {
            Op op = opToken.ops().latefixOp();
            return new FormatException("%s operator '%s' in prefix position at index %s",
                StringHandler.toUpper(op.getKind().getPlace().name().toLowerCase()),
                op.getSymbol(), opToken.start());
        }

        /** Factory method for a line fragment.
         * @param start start position of the fragment
         * @param end end position of the fragment
         */
        private LineFragment createFragment(int start, int end) {
            return new LineFragment(this.input, start, end);
        }
    }

    /**
     * Token class used during parsing.
     * A token consists of a token type and a line fragment where the token occurs.
     */
    static class Token extends Pair<TokenType,LineFragment> {
        public Token(TokenType type, LineFragment fragment) {
            super(type, fragment);
        }

        /** Returns the type of this token. */
        public TokenType type() {
            return one();
        }

        /** Returns the type class of this token. */
        public TokenClaz claz() {
            return type().claz();
        }

        /** Returns the operator family of the token type. */
        public <O extends Op> OpFamily<O> ops() {
            return type().op();
        }

        /** Returns the start position of this token. */
        public int start() {
            return two().start();
        }

        /** Returns the end position of this token. */
        public int end() {
            return two().end();
        }

        /** Returns the string representation of the token content. */
        public String substring() {
            return two().substring();
        }

        public Constant createConstant() throws FormatException {
            return one().sig().createConstant(substring());
        }
    }

    /** A placement-indexed family of operators with the same symbol. */
    static class OpFamily<O extends Op> extends Duo<O> {
        /** Returns an operator family, initialised with a given operator. */
        OpFamily(O op) {
            super(null, null);
            this.symbol = op.getSymbol();
            add(op);
        }

        /** Adds an operator to this family. */
        public void add(O value) {
            O oldValue;
            if (value.getKind().getPlace() == Placement.PREFIX) {
                oldValue = setOne(value);
            } else {
                oldValue = setTwo(value);
            }
            assert oldValue == null;
            assert value.getSymbol().equals(symbol());
        }

        /** Indicates if there is a prefix operator in this family. */
        public boolean hasPrefixOp() {
            return prefixOp() != null;
        }

        /** Returns the prefix operator in this family. */
        public O prefixOp() {
            return one();
        }

        /** Indicates if there is a non-prefix operator in this family. */
        public boolean hasLatefixOp() {
            return latefixOp() != null;
        }

        /** Returns the non-prefix operator in this family. */
        public O latefixOp() {
            return two();
        }

        /** Returns the common symbol for the operators in this family. */
        String symbol() {
            return this.symbol;
        }

        private final String symbol;
    }

    /** A string fragment, consisting of an input line with start and end position. */
    static class LineFragment extends Triple<String,Integer,Integer> {
        /**
         * Constructs a string fragment.
         * @param line the input line
         * @param start start position
         * @param end end position
         */
        public LineFragment(String line, Integer start, Integer end) {
            super(line, start, end);
            assert start >= 0;
            assert end >= start && end <= line.length();
        }

        /** Returns the fragment substring. */
        public String substring() {
            return line().substring(start(), end());
        }

        /** Returns complete input line. */
        public String line() {
            return one();
        }

        /** Returns the start position of the fragment. */
        public int start() {
            return two();
        }

        /** Returns the end position of the fragment. */
        public int end() {
            return three();
        }
    }

    /**
     * Token kind; consists of a token type class and (if the type class is non-singular)
     * possibly some additional information.
     * @author Arend Rensink
     * @version $Revision $
     */
    static class TokenType extends Pair<TokenClaz,Object> {
        /**
         * Constructs a token type for a singular type class.
         */
        public TokenType(TokenClaz claz) {
            super(claz, null);
        }

        /**
         * Constructs a separator token type with a given symbol.
         */
        public TokenType(String symbol) {
            super(TokenClaz.SEP, symbol);
        }

        /**
         * Constructs a token type for an operator.
         * @param op the (non-{@code null}) associated operator.
         */
        public TokenType(OpFamily<?> op) {
            super(TokenClaz.OP, op);
            assert op != null;
        }

        /**
         * Constructs a token type for a constant type.
         * @param sig the (non-{@code null}) associated constant type.
         */
        public TokenType(SignatureKind sig) {
            super(TokenClaz.CONST, sig);
            assert sig != null;
        }

        /** Returns the type class of this token type. */
        public TokenClaz claz() {
            return one();
        }

        /** Returns the operator wrapped in this token type, if any. */
        @SuppressWarnings("unchecked")
        public <O extends Op> OpFamily<O> op() {
            return claz() == TokenClaz.OP ? (OpFamily<O>) two() : null;
        }

        /** Returns the signature kind wrapped in this token type, if any. */
        public SignatureKind sig() {
            return claz() == TokenClaz.CONST ? (SignatureKind) two() : null;
        }

        /**
         * Returns the symbol for this token type, if
         * it is a singleton type.
         */
        public String symbol() {
            String result = null;
            if (claz() == TokenClaz.OP) {
                result = op().symbol();
            } else if (claz() == TokenClaz.SEP) {
                return (String) two();
            } else if (claz().single()) {
                result = claz().symbol();
            }
            return result;
        }
    }

    /** 
     * Token type class class.
     * Every token type has a class. 
     * A token type class can either be singular, meaning that
     * there exists exactly one type of that class, or multiple.
     * @author Arend Rensink
     * @version $Revision $
     */
    static enum TokenClaz {
        /** Id-separator (for prefix and qualification. */
        SEP(false),
        /** Operator. */
        OP(false),
        /** Algebraic constant token. */
        CONST(false),
        /**
         * Atomic name, formed like a Java identifier, with hyphens allowed in the middle.
         * @see StringHandler#isIdentifier(String)
         */
        NAME(true),
        /** A static token, representing a left parenthesis. */
        LPAR("("),
        /** A static token, representing a right parenthesis. */
        RPAR(")"),
        /** A static token, representing a comma. */
        COMMA(","),
        /** A static token, representing the end of the input text. */
        EOT("" + Util.EOT), ;

        /**
         * Constructs a token kind instance.
         * @param single if {@code true}, there is only a single type of this kind.
         */
        private TokenClaz(boolean single) {
            this(single, null);
        }

        /**
         * Constructs a singular token kind instance.
         * @param text non-{@code null} text of the token kind (and type)
         */
        private TokenClaz(String text) {
            this(true, text);
        }

        /**
         * General constructor for a token kind instance.
         * @param single if {@code true}, there is only a single type of this kind.
         * @param text if single, a non-{@code null} text of the token kind (and type)
         */
        private TokenClaz(boolean single, String text) {
            this.symbol = text;
            this.type = single ? new TokenType(this) : null;
        }

        /**
         * Indicates if this is a token kind 
         * of which only a single token type can exist.
         * If that is the case, then the unique
         * token type is given by {@link #getSingleType()}
         */
        public boolean single() {
            return getSingleType() != null;
        }

        /** Returns the unique token type of this kind, if 
         * the kind is singular.
         */
        public TokenType getSingleType() {
            return this.type;
        }

        private final TokenType type;

        /** Indicates if this token kind is parsable, i.e., has a non-{@code null} symbol.
         * Only singular token kinds can be parsable.
         */
        public boolean parsable() {
            return symbol() != null;
        }

        /**
         * Returns the (possibly {@code null}) symbol of this token type class.
         */
        public String symbol() {
            return this.symbol;
        }

        private final String symbol;
    }
}
