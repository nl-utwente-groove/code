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
import static groove.util.parse.ExprParser.TokenClaz.CONST;
import static groove.util.parse.ExprParser.TokenClaz.EOT;
import static groove.util.parse.ExprParser.TokenClaz.LATE_OP;
import static groove.util.parse.ExprParser.TokenClaz.LPAR;
import static groove.util.parse.ExprParser.TokenClaz.NAME;
import static groove.util.parse.ExprParser.TokenClaz.PRE_OP;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
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
     * Constructs a parser recognising a given enumeration of operators.
     * Neither signature declarations nor qualified identifiers are recognised
     * by default.
     * @param opType enumerated type of the operators;
     * should contain exactly one instance of type {@link OpKind#ATOM}
     */
    public ExprParser(Class<? extends O> opType) {
        this.ops = Arrays.asList(opType.getEnumConstants());
    }

    /**
     * Constructs a parser recognising a given set of operators.
     * Neither signature declarations nor qualified identifiers are recognised
     * by default.
     * @param ops collection of operators to be recognised by this parser;
     * should contain exactly one instance of type {@link OpKind#ATOM}
     */
    public ExprParser(Collection<? extends O> ops) {
        this.ops = Collections.unmodifiableList(new ArrayList<O>(ops));
    }

    /** Returns the list of operators that this parser recognises. */
    public List<? extends O> getOps() {
        return this.ops;
    }

    private final List<? extends O> ops;

    /** Returns the type of expression operators that this parser handles. */
    @SuppressWarnings("unchecked")
    private Class<? extends O> getOpType() {
        return (Class<? extends O>) getAtomOp().getClass();
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

    /** Sets the ability to recognise signature prefixes. */
    public void setSigPrefixes(boolean sigPrefixes) {
        this.sigPrefixes = sigPrefixes;
    }

    /** Indicates if the parser recognises prefixed identifiers. */
    public boolean hasPrefixIds() {
        return this.sigPrefixes;
    }

    private boolean sigPrefixes;

    /** Sets the ability to recognise qualified identifiers. */
    public void setQualIds(boolean qualIds) {
        this.qualIds = qualIds;
    }

    /** Indicates if the parser recognises qualified identifiers. */
    boolean hasQualIds() {
        return this.qualIds;
    }

    private boolean qualIds;

    /** Retrieves the (supposedly unique) operator of a given kind. */
    private O retrieveKindOp(OpKind kind) {
        O result = null;
        for (O op : getOps()) {
            if (op.getKind() == kind && !op.hasSymbol()) {
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

    /** Returns the (supposedly unique) atom operator used by this parser. */
    public O getAtomOp() {
        if (this.atomOp == null) {
            this.atomOp = retrieveKindOp(OpKind.ATOM);
        }
        return this.atomOp;
    }

    private O atomOp;

    /** Sets the description of the expressions being parsed. */
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    private String description;

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
        return createInstance(text).parse();
    }

    /** Callback factory method for a parser instance initialised on a given input string. */
    protected Instance createInstance(String text) {
        return new Instance(text);
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

    /** Returns the list of all token types recognised by this parser. */
    List<TokenType> getTokenTypes() {
        if (this.tokenTypes == null) {
            this.tokenTypes = new ArrayList<TokenType>();
            this.tokenTypes.addAll(getConstTokenMap().values());
            for (O op : getOps()) {
                if (op.hasSymbol()) {
                    this.tokenTypes.add(new TokenType(op));
                }
            }
            for (SignatureKind sig : SignatureKind.values()) {
                this.tokenTypes.add(new TokenType(TokenClaz.SIG, sig));
            }
            for (TokenClaz claz : TokenClaz.values()) {
                if (claz.single()) {
                    this.tokenTypes.add(claz.type());
                }
            }
        }
        return this.tokenTypes;
    }

    /** Lazily created list of all token types. */
    private List<TokenType> tokenTypes;

    /** Returns the (predefined) token family for a given string symbol, if any.*/
    TokenFamily getTokenFamily(String symbol) {
        return getSymbolFamilyMap().get(symbol);
    }

    /** Returns the map from symbols to predefined (parsable) token types of this parser. */
    private Map<String,TokenFamily> getSymbolFamilyMap() {
        if (this.symbolFamilyMap == null) {
            Map<String,TokenFamily> result =
                this.symbolFamilyMap = new TreeMap<String,TokenFamily>();
            for (TokenType type : getTokenTypes()) {
                if (type.parsable()) {
                    String symbol = type.symbol();
                    TokenFamily family = result.get(symbol);
                    if (family == null) {
                        result.put(symbol, family = new TokenFamily());
                    }
                    family.add(type);
                    // also add a NAME type for the symbol, if it is an identifier
                    if (StringHandler.isIdentifier(symbol)) {
                        family.add(NAME.type());
                    }
                }
            }
        }
        return this.symbolFamilyMap;
    }

    private Map<String,TokenFamily> symbolFamilyMap;

    /** Returns the fixed default token family for a given token type. */
    TokenFamily getTokenFamily(TokenType type) {
        if (this.typeFamilyMap == null) {
            Map<TokenType,TokenFamily> result =
                this.typeFamilyMap = new HashMap<TokenType,TokenFamily>();
            for (TokenType t : getTokenTypes()) {
                if (t.parsable()) {
                    result.put(t, getTokenFamily(t.symbol()));
                } else {
                    assert t.claz() == TokenClaz.CONST || t.claz() == NAME;
                    result.put(t, new TokenFamily(t));
                }
            }
        }
        assert this.typeFamilyMap.containsKey(type);
        return this.typeFamilyMap.get(type);
    }

    private Map<TokenType,TokenFamily> typeFamilyMap;

    /** Returns the fixed constant token type for a given signature kind. 
     * @see TokenClaz#CONST 
     */
    TokenType getConstTokenType(SignatureKind sig) {
        return getConstTokenMap().get(sig);
    }

    /** Returns the mapping from signatures to constant token types.
     * @see TokenClaz#CONST 
     */
    private Map<SignatureKind,TokenType> getConstTokenMap() {
        if (this.constTokenMap == null) {
            this.constTokenMap = new EnumMap<SignatureKind,TokenType>(SignatureKind.class);
            for (SignatureKind sig : SignatureKind.values()) {
                this.constTokenMap.put(sig, new TokenType(TokenClaz.CONST, sig));
            }
        }
        return this.constTokenMap;
    }

    /** Lazily created mapping from signatures to constant token types. */
    private Map<SignatureKind,TokenType> constTokenMap;

    /** Returns the symbol table for this parser. */
    SymbolTable getSymbolTable() {
        if (this.symbolTable == null) {
            this.symbolTable = new SymbolTable(getSymbolFamilyMap().values());
        }
        return this.symbolTable;
    }

    private SymbolTable symbolTable;

    /** Mapping to enable efficient scanning of tokens. */
    private class SymbolTable extends HashMap<Character,SymbolTable> {
        SymbolTable(Collection<TokenFamily> tokens) {
            this(tokens, "");
        }

        SymbolTable(Collection<TokenFamily> tokens, String prefix) {
            TokenFamily family = null;
            for (TokenFamily token : tokens) {
                String symbol = token.symbol();
                if (!symbol.startsWith(prefix)) {
                    continue;
                }
                if (symbol.equals(prefix)) {
                    if (family != null) {
                        throw new IllegalArgumentException("Duplicate token " + symbol);
                    }
                    family = token;
                } else {
                    char next = symbol.charAt(prefix.length());
                    if (!containsKey(next)) {
                        put(next, new SymbolTable(tokens, prefix + next));
                    }
                }
            }
            this.family = family;
        }

        /** Returns the token family corresponding to the symbol scanned so far. */
        TokenFamily getTokenFamily() {
            return this.family;
        }

        private final TokenFamily family;
    }

    /** Throwaway instance of this parser, initialised on a given string. */
    protected class Instance {
        Instance(String input) {
            this.input = input;
        }

        /** Parses the string with which this instance was initialised. */
        protected Expr<O> parse() {
            Expr<O> result;
            try {
                result = parse(OpKind.NONE);
                if (!next().has(EOT)) {
                    result.addErrors(unexpectedToken(next()));
                    result.addError(new FormatError("Unparsed suffix: %s", this.input.substring(
                        next().start(), this.input.length())));
                }
            } catch (FormatException exc) {
                result = createErrorExpr(exc);
            }
            return result;
        }

        /**
         * Parses the string with which this instance was initialised,
         * in the context of an operator of a certain kind.
         */
        @SuppressWarnings("unchecked")
        private Expr<O> parse(OpKind context) throws FormatException {
            Expr<O> result = null;
            Token nextToken = next();
            if (nextToken.has(LPAR)) {
                result = parseBracketed();
            } else if (nextToken.has(NAME)) {
                result = parseCall();
            } else if (nextToken.has(CONST)) {
                result = createConstantExpr(consume(CONST).createConstant());
            } else if (nextToken.has(PRE_OP)) {
                result = parsePrefixed();
            } else {
                throw unexpectedToken(nextToken);
            }
            while (!next().has(EOT)) {
                nextToken = next();
                if (!nextToken.has(LATE_OP)) {
                    break;
                }
                O op = nextToken.op(LATE_OP);
                OpKind kind = op.getKind();
                if (context.compareTo(kind) > 0) {
                    break;
                }
                if (context.equals(kind)) {
                    if (kind.getDirection() == Direction.LEFT) {
                        break;
                    } else if (kind.getDirection() == Direction.NEITHER) {
                        throw unexpectedToken(nextToken);
                    }
                }
                consume();
                if (kind.getPlace() == Placement.POSTFIX) {
                    result = createOpExpr(op, result);
                    break;
                }
                result = createOpExpr(op, result, parse(kind));
            }
            return result;
        }

        /**
         * Attempts to parse the string as a bracketed expression.
         * @return the expression in brackets, or {@code null} if the input string does not
         * correspond to a bracketed expression
         */
        private Expr<O> parseBracketed() throws FormatException {
            Expr<O> result = null;
            Token next = consume(LPAR);
            assert next != null;
            result = parse(OpKind.NONE);
            if (!next().has(RPAR)) {
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
            Token opToken = consume(TokenClaz.PRE_OP);
            O op = opToken.type(TokenClaz.PRE_OP).op();
            Expr<O> arg = parse(op.getKind());
            result = createOpExpr(op, arg);
            return result;
        }

        /** Parses the input as a call or atomic expression,
         * optionally signature-prefixed.
         */
        @SuppressWarnings("unchecked")
        private Expr<O> parseCall() throws FormatException {
            assert next().has(NAME);
            Expr<O> result = null;
            SignatureKind sig = null;
            if (hasPrefixIds() && next().has(TokenClaz.SIG)) {
                Token sigToken = consume(TokenClaz.SIG);
                if (consume(TokenClaz.SIG_SEP) == null) {
                    // this wasn't meant to be a signature prefix after all
                    rollBack();
                } else {
                    sig = sigToken.type(TokenClaz.SIG).sig();
                }
            }
            // see if we are dealing with a constant
            // because we may be behind a signature declaration, negation has to be
            // parsed explicitly (- is not part of the constant token)
            Token minusToken = consume(TokenClaz.MINUS);
            O minus = null;
            if (minusToken != null) {
                if (minusToken.has(PRE_OP)) {
                    minus = minusToken.one().get(PRE_OP).op();
                } else {
                    throw unexpectedToken(minusToken);
                }
            }
            Token constToken = consume(TokenClaz.CONST);
            if (minusToken != null || constToken != null) {
                if (constToken == null) {
                    throw expectedConstant(next());
                }
                Constant constant = constToken.createConstant();
                if (sig != null && sig != constant.getSignature()) {
                    throw invalidDeclaration(sig, constToken);
                }
                result = createConstantExpr(constant);
                if (minus != null) {
                    result = createOpExpr(minus, result);
                }
            }
            if (result == null && next().has(NAME)) {
                // we now expect either a call operation or a user-defined identifier
                if (next().has(TokenClaz.PRE_OP)) {
                    O op = consume(TokenClaz.PRE_OP).type(TokenClaz.PRE_OP).op();
                    if (!next().has(LPAR)) {
                        // this wasn't an operator call after all
                        rollBack();
                    } else {
                        result = createOpExpr(op, sig);
                    }
                }
                if (result == null) {
                    // it's a user-defined identifier: this should not be signature-prefixed
                    if (sig != null) {
                        throw invalidDeclaration(sig, next());
                    }
                    Id id = parseId();
                    boolean call = next().has(LPAR);
                    if (call && !hasCallOp()) {
                        throw unexpectedToken(next());
                    }
                    result = createIdExpr(call ? getCallOp() : getAtomOp(), id);
                }
                if (consume(LPAR) != null) {
                    if (consume(RPAR) == null) {
                        result.addArg(parse(OpKind.NONE));
                        while (consume(TokenClaz.COMMA) != null) {
                            result.addArg(parse(OpKind.NONE));
                        }
                        if (consume(RPAR) == null) {
                            throw unbalancedBracket(next());
                        }
                    }
                }
            }
            if (result == null) {
                throw unexpectedToken(next());
            }
            return result;
        }

        /**
         * Parses the input as an identifier.
         * Assumes the first token is a {@link TokenClaz#NAME} token.
         */
        protected Id parseId() throws FormatException {
            Id result = new Id();
            Token nameToken = consume(NAME);
            assert nameToken != null;
            result.add(nameToken.substring());
            while (hasQualIds() && consume(TokenClaz.QUAL_SEP) != null) {
                nameToken = consume(NAME);
                if (nameToken == null) {
                    throw unexpectedToken(next());
                }
                result.add(nameToken.substring());
            }
            return result;
        }

        /** Factory method for an expression with a given operator and optional signature declaration. 
         */
        private Expr<O> createOpExpr(O op, SignatureKind sig) {
            Expr<O> result = createExpr(op);
            if (sig != null) {
                result.setSig(sig);
            }
            result.setParseString(this.input);
            return result;
        }

        /** Factory method for an expression with a given operator 
         * and list of arguments. 
         */
        private Expr<O> createOpExpr(O op, Expr<O>... args) {
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
            assert op.getKind() == OpKind.ATOM || op.getKind() == OpKind.CALL;
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

        /** Tests if the next token is of the expected token class;
         * if so, returns it, otherwise returns {@code null}
         * @param claz the expected token class
         * @return next token if it is of the right class, {@code null} otherwise
         * @throws ScanException if an error was encountered during scanning
         */
        protected final Token consume(TokenClaz claz) throws ScanException {
            Token next = next();
            if (next.has(claz)) {
                consume();
            } else {
                next = null;
            }
            return next;
        }

        /** Returns the next unconsumed token in the input stream.
         * @throws ScanException if an error was encountered during scanning
         */
        private Token next() throws ScanException {
            if (this.nextToken == null) {
                this.nextToken = scan();
            }
            return this.nextToken;
        }

        /** Consumes the current token, causing the next call of {@link #next()}
         * to scan the next token.
         */
        private void consume() {
            this.previousToken = this.nextToken;
            this.nextToken = this.futureToken;
            this.futureToken = null;
        }

        private void rollBack() {
            assert this.futureToken == null;
            this.futureToken = this.nextToken;
            this.nextToken = this.previousToken;
            this.previousToken = null;
        }

        /** The next token produced by the scanner. */
        private Token nextToken;

        /** The token after #nextToken; used for rollback purposes. */
        private Token futureToken;

        /** The most recently consumed token. */
        private Token previousToken;

        /**
         * Scans and returns the next token in the input string.
         * @throws ScanException if an error was encountered during scanning
         */
        private Token scan() throws ScanException {
            Token result = null;
            // the atEnd() call skips all whitespace
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
            TokenFamily type = null;
            int start = this.ix;
            int end = start;
            SymbolTable map = getSymbolTable();
            while (end < this.input.length()) {
                char nextChar = this.input.charAt(end);
                SymbolTable nextMap = map.get(nextChar);
                if (nextMap == null) {
                    // nextChar is not part of any operator symbol
                    type = map.getTokenFamily();
                    break;
                }
                end++;
                if (end == this.input.length()) {
                    // there is no next character after this
                    type = nextMap.getTokenFamily();
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
            return createConstToken(sig, start, this.ix);
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
            String symbol = fragment.substring();
            if (symbol.equals(BoolSignature.TRUE.toDisplayString())
                || symbol.equals(BoolSignature.FALSE.toDisplayString())) {
                return createConstToken(SignatureKind.BOOL, start, this.ix);
            } else {
                TokenFamily family = getTokenFamily(symbol);
                if (family == null) {
                    family = getTokenFamily(NAME.type());
                }
                return createFamilyToken(family, start, this.ix);
            }
        }

        private Token scanString() throws ScanException {
            int start = this.ix;
            char quote = charAt();
            nextChar();
            boolean escaped = false;
            while (!atEnd() && (escaped || charAt() != quote)) {
                escaped = charAt() == StringHandler.ESCAPE_CHAR;
                nextChar();
            }
            if (atEnd()) {
                throw new ScanException("%s-quoted string is not closed", quote);
            } else {
                assert charAt() == quote;
                nextChar();
            }
            return createConstToken(SignatureKind.STRING, start, this.ix);
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
                this.eot = createTypedToken(EOT.type(), end, end);
            }
            return this.eot;
        }

        private Token eot;

        private ParseException expectedConstant(Token token) {
            String message = "Expected a literal rather than ";
            if (token.has(EOT)) {
                message += "end of input";
            } else {
                message += "token '%s' at index %s";
            }
            return new ParseException(message, token.substring(), token.start());
        }

        private ParseException unexpectedToken(Token token) {
            if (token.has(EOT)) {
                return new ParseException("Unexpected end of input");
            } else {
                return new ParseException("Unexpected token '%s' at index %s", token.substring(),
                    token.start());
            }
        }

        private ScanException unrecognisedToken() {
            return new ScanException("Unrecognised token '%s' at index %s", charAt(), this.ix);
        }

        private ParseException unbalancedBracket(Token token) {
            return new ParseException("Expected ')' rather than %s at index %s", token == eot()
                ? "end of line" : "'" + token.substring() + "'", token.start());
        }

        private ParseException invalidDeclaration(SignatureKind sig, Token token) {
            return new ParseException("Invalid signature '%s:%s' at %s", sig.getName(),
                token.substring(), token.start());
        }

        /** Factory method for a line fragment.
         * @param start start position of the fragment
         * @param end end position of the fragment
         */
        private LineFragment createFragment(int start, int end) {
            return new LineFragment(this.input, start, end);
        }

        private Token createConstToken(SignatureKind sig, int start, int end) {
            return createTypedToken(getConstTokenType(sig), start, end);
        }

        private Token createTypedToken(TokenType type, int start, int end) {
            TokenFamily family = getTokenFamily(type);
            return createFamilyToken(family, start, end);
        }

        private Token createFamilyToken(TokenFamily family, int start, int end) {
            return new Token(family, createFragment(start, end));
        }

        @Override
        public String toString() {
            return "Parser instance for " + this.input;
        }
    }

    /**
     * Token class used during parsing.
     * A token can still correspond to multiple token types,
     * though only of distinct token classes.
     * A token also contains the line fragment where it has been found.
     */
    protected static class Token extends Pair<TokenFamily,LineFragment> {
        /** Constructs a token of a given type family. */
        Token(TokenFamily family, LineFragment fragment) {
            super(family, fragment);
            assert family != null;
            assert fragment != null;
        }

        /** Returns the type of this token. */
        public TokenType type(TokenClaz claz) {
            return one().get(claz);
        }

        /** Indicates if this token may be of a given token class. */
        public boolean has(TokenClaz claz) {
            return one().containsKey(claz);
        }

        /** Returns the operator of a given token class, if there is
         * one in this token. */
        public <O extends Op> O op(TokenClaz claz) {
            assert claz == TokenClaz.PRE_OP || claz == TokenClaz.LATE_OP;
            return type(claz).op();
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

        /** Creates a constant from this token,
         * if it is a constant token.
         * The token class is required to be {@link TokenClaz#CONST}.
         * @return a constant constructed from the string wrapped by this token
         */
        public Constant createConstant() {
            assert has(TokenClaz.CONST);
            SignatureKind sig = type(TokenClaz.CONST).sig();
            try {
                return sig.createConstant(substring());
            } catch (FormatException exc) {
                assert false : String.format(
                    "'%s' has been scanned as a token; how can it fail to be one? (%s)",
                    substring(), exc.getMessage());
                return null;
            }
        }
    }

    /**
     * Family of token types, indexed by token class.
     * This is used as a mechanism to allow some ambiguity of tokens with
     * the same symbol, as long as they are of different token class.
     * @author Arend Rensink
     * @version $Revision $
     */
    static class TokenFamily extends EnumMap<TokenClaz,TokenType> {
        /**
         * Constructs an initially empty family.
         */
        public TokenFamily() {
            super(TokenClaz.class);
        }

        /**
         * Constructs a family with a single initial member.
         */
        public TokenFamily(TokenType type) {
            super(TokenClaz.class);
            add(type);
        }

        public void add(TokenType type) {
            TokenType old = put(type.claz(), type);
            assert old == null;
            if (this.symbol == null) {
                this.symbol = type.symbol();
            } else {
                assert type.claz() == NAME || this.symbol.equals(type.symbol());
            }
        }

        /** Returns the common symbol of all the token types in this family. */
        public String symbol() {
            return this.symbol;
        }

        private String symbol;
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
    protected static class TokenType extends Pair<TokenClaz,Object> {
        /**
         * Constructs a token type for a singular type class.
         */
        public TokenType(TokenClaz claz) {
            super(claz, null);
        }

        /**
         * Constructs a token type for an operator.
         * @param op the (non-{@code null}) associated operator.
         */
        public TokenType(Op op) {
            super(getClaz(op.getKind().getPlace()), op);
        }

        /**
         * Constructs a token type for a signature or constant.
         * @param claz either {@link TokenClaz#CONST} or {@link TokenClaz#SIG}
         * @param sig the (non-{@code null}) associated signature.
         */
        public TokenType(TokenClaz claz, SignatureKind sig) {
            super(claz, sig);
            assert claz == TokenClaz.CONST || claz == TokenClaz.SIG;
            assert sig != null;
        }

        /** Returns the type class of this token type. */
        public TokenClaz claz() {
            return one();
        }

        /** Returns the operator wrapped in this token type, if any. */
        @SuppressWarnings("unchecked")
        public <O extends Op> O op() {
            return claz() == TokenClaz.PRE_OP || claz() == TokenClaz.LATE_OP ? (O) two() : null;
        }

        /** Returns the signature kind wrapped in this token type, if any. */
        public SignatureKind sig() {
            assert claz() == TokenClaz.SIG || claz() == TokenClaz.CONST;
            return (SignatureKind) two();
        }

        /** Indicates if this token type has a parsable symbol.
         */
        public boolean parsable() {
            return symbol() != null;
        }

        /**
         * Returns the symbol for this token type, if
         * it is a single (parsable) type.
         */
        public String symbol() {
            String result;
            switch (claz()) {
            case PRE_OP:
            case LATE_OP:
                result = op().getSymbol();
                break;
            case SIG:
                result = sig().getName();
                break;
            default:
                result = claz().symbol();
            }
            return result;
        }
    }

    /** Returns the operator token class for  a given operator placement. */
    static TokenClaz getClaz(Placement place) {
        switch (place) {
        case INFIX:
        case POSTFIX:
            return TokenClaz.LATE_OP;
        case PREFIX:
            return TokenClaz.PRE_OP;
        default:
            assert false;
            return null;
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
    protected static enum TokenClaz {
        /** Prefix operator (including call operator). */
        PRE_OP(false),
        /** Latefix (i.e., non-prefix) operator. */
        LATE_OP(false),
        /** Signature kind name. */
        SIG(false),
        /** Algebraic constant token. */
        CONST(true),
        /**
         * Atomic name, formed like a Java identifier, with hyphens allowed in the middle.
         * @see StringHandler#isIdentifier(String)
         */
        NAME(true),
        /** Qualifier separator. */
        QUAL_SEP("."),
        /** Signature separator. */
        SIG_SEP(":"),
        /** Assignment operator. */
        ASSIGN("="),
        /** Minus sign, used for negated constants. */
        MINUS("-"),
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
            this.single = single;
            this.type = single ? new TokenType(this) : null;
        }

        /**
         * Indicates if this is a token kind 
         * of which only a single token type can exist.
         * If that is the case, then the unique
         * token type is given by {@link #type()}
         */
        public boolean single() {
            return this.single;
        }

        private final boolean single;

        /** Returns the unique token type of this kind, if 
         * the kind is singular.
         */
        public TokenType type() {
            assert this.single;
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

    /** Special exception occurring in the parsing phase of parsing. */
    protected static class ParseException extends FormatException {
        /**
         * Constructs an exception from a String format formatted message
         * and a list of arguments.
         */
        public ParseException(String message, Object... parameters) {
            super(message, parameters);
        }
    }

    /** Special exception occurring in the scanner phase of parsing. */
    protected static class ScanException extends FormatException {
        /**
         * Constructs an exception from a String format formatted message
         * and a list of arguments.
         */
        public ScanException(String message, Object... parameters) {
            super(message, parameters);
        }
    }
}
