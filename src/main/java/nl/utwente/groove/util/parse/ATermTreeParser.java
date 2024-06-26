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
package nl.utwente.groove.util.parse;

import static nl.utwente.groove.algebra.Sort.INT;
import static nl.utwente.groove.algebra.Sort.REAL;
import static nl.utwente.groove.util.Factory.lazy;
import static nl.utwente.groove.util.parse.ATermTreeParser.TokenClaz.CONST;
import static nl.utwente.groove.util.parse.ATermTreeParser.TokenClaz.EOT;
import static nl.utwente.groove.util.parse.ATermTreeParser.TokenClaz.LATE_OP;
import static nl.utwente.groove.util.parse.ATermTreeParser.TokenClaz.LPAR;
import static nl.utwente.groove.util.parse.ATermTreeParser.TokenClaz.NAME;
import static nl.utwente.groove.util.parse.ATermTreeParser.TokenClaz.PRE_OP;
import static nl.utwente.groove.util.parse.ATermTreeParser.TokenClaz.RPAR;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.Constant;
import nl.utwente.groove.algebra.Sort;
import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.io.Util;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.Factory;
import nl.utwente.groove.util.parse.OpKind.Direction;
import nl.utwente.groove.util.parse.OpKind.Placement;

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
abstract public class ATermTreeParser<O extends Op,X extends ATermTree<O,X>>
    extends Parser.AParser<X> {
    /**
     * Constructs a parser recognising a given enumeration of operators.
     * Neither sort declarations nor qualified identifiers are recognised
     * by default.
     * @param prototype prototype object of the tree type; used to construct instances.
     * The operator type is assumed to be an {@link Enum}, and the operator
     * kind to be {@link OpKind#ATOM}; this kind will be used to construct
     * term instances for atomic terms
     */
    @SuppressWarnings("unchecked")
    protected ATermTreeParser(String description, X prototype) {
        this(description, prototype, Factory
            .value(Arrays
                .asList(((Class<? extends O>) prototype.getOp().getClass()).getEnumConstants())));
    }

    /**
     * Constructs a parser recognising a given set of operators.
     * Neither sort declarations nor qualified identifiers are recognised
     * by default.
     * @param prototype prototype object of the tree type; used to construct instances.
     * The operator type is assumed to be an {@link Enum}, and the operator
     * kind to be {@link OpKind#ATOM}; this kind will be used to construct
     * term instances for atomic terms
     * @param ops collection of operators to be recognised by this parser;
     * should contain exactly one instance of type {@link OpKind#ATOM}
     */
    @SuppressWarnings("unchecked")
    protected ATermTreeParser(String description, X prototype,
                              Supplier<? extends Collection<? extends O>> ops) {
        super(description, (Class<X>) prototype.getClass());
        this.prototype = prototype;
        this.atomOp = prototype.getOp();
        assert !this.atomOp.hasSymbol();
        this.ops = ops;
    }

    /** Returns the set of all operators that the parser should recognise. */
    private Collection<? extends O> getOps() {
        return this.ops.get();
    }

    /** Supplier for the set of all operators that the parser should recognise. */
    private final Supplier<? extends Collection<? extends O>> ops;

    /** Sets the ability to recognise qualified identifiers. */
    public void setQualIds(boolean qualIds) {
        this.qualIds = qualIds;
    }

    /** Indicates if the parser recognises qualified identifiers. */
    boolean hasQualIds() {
        return this.qualIds;
    }

    private boolean qualIds;

    /** Returns the (supposedly unique) atom operator used by this parser. */
    protected O getAtomOp() {
        return this.prototype.getOp();
    }

    private final O atomOp;

    /** Sets the criterion for valid identifier names. */
    protected void setIdValidator(IdValidator idValidator) {
        this.idValidator = idValidator;
    }

    /** Retrieves the criterion for valid identifier names. */
    private IdValidator getIdValidator() {
        if (this.idValidator == null) {
            this.idValidator = IdValidator.JAVA_ID;
        }
        return this.idValidator;
    }

    private IdValidator idValidator;

    /**
     * Callback factory method for the term trees to be constructed.
     */
    protected X createTree(O op) {
        return this.prototype.createTree(op);
    }

    private final X prototype;

    @Override
    public boolean accepts(String text) {
        return !parse(text).hasErrors();
    }

    @Override
    public @NonNull X parse(String input) {
        init(input);
        X result = parse();
        result.setFixed();
        return result;
    }

    @Override
    public <V extends X> String unparse(V value) {
        return value.getParseString();
    }

    /** Returns the list of all token types recognised by this parser. */
    private List<TokenType> getTokenTypes() {
        return this.tokenTypes.get();
    }

    /** Lazily created list of all token types. */
    private Supplier<List<TokenType>> tokenTypes = lazy(this::computeTokenTypes);

    /** Computes the value for {@link #tokenTypes}. */
    private List<TokenType> computeTokenTypes() {
        List<TokenType> result = new ArrayList<>();
        result.addAll(getConstTokenMap().values());
        for (O op : getOps()) {
            if (op.getKind() != OpKind.NONE && op.hasSymbol()) {
                result.add(new TokenType(op));
            }
        }
        for (Sort sort : Sort.values()) {
            result.add(new TokenType(TokenClaz.SORT, sort));
        }
        for (TokenClaz claz : TokenClaz.values()) {
            if (claz.single()) {
                result.add(claz.type());
            }
        }
        return result;
    }

    /** Returns the (predefined) token family for a given string symbol, if any.*/
    private TokenFamily getTokenFamily(String symbol) {
        return getSymbolFamilyMap().get(symbol);
    }

    /** Returns the map from symbols to predefined (parsable) token types of this parser. */
    private Map<String,TokenFamily> getSymbolFamilyMap() {
        return this.symbolFamilyMap.get();
    }

    /** Lazily computed map from symbols to predefined (parsable) token types of this parser. */
    private Supplier<Map<String,TokenFamily>> symbolFamilyMap = lazy(this::computeSymbolFamilyMap);

    /** Computes the value for {@link #symbolFamilyMap}. */
    private Map<String,TokenFamily> computeSymbolFamilyMap() {
        Map<String,TokenFamily> result = new TreeMap<>();
        for (TokenType type : getTokenTypes()) {
            if (type.parsable()) {
                String symbol = type.symbol();
                TokenFamily family = result.get(symbol);
                if (family == null) {
                    result.put(symbol, family = new TokenFamily());
                }
                family.add(type);
                // also add a NAME or BOOL type for the symbol, if appropriate
                if (Sort.BOOL.denotesValue(symbol)) {
                    family.add(getConstTokenType(Sort.BOOL));
                } else if (getIdValidator().isValid(symbol)) {
                    family.add(NAME.type());
                }
            }
        }
        return result;
    }

    /** Returns the fixed default token family for a given token type. */
    @NonNull
    private TokenFamily getTokenFamily(TokenType type) {
        TokenFamily result = this.tokenFamilyMap.get().get(type);
        assert result != null;
        return result;
    }

    private Supplier<Map<TokenType,TokenFamily>> tokenFamilyMap = lazy(this::computeTokenFamilyMap);

    /** Returns the fixed default token family for a given token type. */
    @NonNull
    private Map<TokenType,TokenFamily> computeTokenFamilyMap() {
        Map<TokenType,TokenFamily> result = new HashMap<>();
        for (TokenType t : getTokenTypes()) {
            if (t.parsable()) {
                result.put(t, getTokenFamily(t.symbol()));
            } else {
                assert t.claz() == TokenClaz.CONST || t.claz() == NAME;
                result.put(t, new TokenFamily(t));
            }
        }
        return result;
    }

    /** Returns the fixed constant token type for a given sort.
     * @see TokenClaz#CONST
     */
    private TokenType getConstTokenType(Sort sort) {
        return getConstTokenMap().get(sort);
    }

    /** Returns the mapping from sorts to constant token types.
     * @see TokenClaz#CONST
     */
    private Map<Sort,TokenType> getConstTokenMap() {
        if (this.constTokenMap == null) {
            this.constTokenMap = new EnumMap<>(Sort.class);
            for (Sort sort : Sort.values()) {
                this.constTokenMap.put(sort, new TokenType(TokenClaz.CONST, sort));
            }
        }
        return this.constTokenMap;
    }

    /** Lazily created mapping from sorts to constant token types. */
    private Map<Sort,TokenType> constTokenMap;

    /** Parses the string with which this instance was initialised. */
    protected X parse() {
        X result;
        try {
            result = parse(OpKind.NONE);
            if (!has(EOT)) {
                result = result.clone();
                result
                    .getErrors()
                    .add("Unparsed suffix: %s",
                         this.input.substring(next().start(), this.input.length()));
            }
        } catch (FormatException exc) {
            result = createErrorTree(exc);
        }
        return result;
    }

    /**
     * Parses the string with which this instance was initialised,
     * in the context of an operator of a certain kind.
     * @param context the priority level and association direction within which parsing takes place
     */
    protected @NonNull X parse(OpKind context) throws FormatException {
        X result;
        Token nextToken = next();
        // first parse for prefix operators to accommodate casts
        if (nextToken.has(PRE_OP)) {
            result = parsePrefixed();
        } else if (nextToken.has(LPAR)) {
            result = parseBracketed();
        } else if (nextToken.has(NAME)) {
            result = parseName();
        } else if (nextToken.has(CONST)) {
            result = parseConst();
        } else {
            throw unexpectedToken(nextToken);
        }
        while (!has(EOT)) {
            if (!has(LATE_OP)) {
                break;
            }
            @SuppressWarnings("unchecked")
            var op = (O) next().op(LATE_OP);
            OpKind kind = op.getKind();
            if (context.compareTo(kind) > 0) {
                break;
            }
            if (context.equals(kind)) {
                if (kind.getDirection() == Direction.LEFT) {
                    break;
                } else if (kind.getDirection() == Direction.NEITHER) {
                    throw unexpectedToken(next());
                }
            }
            consume(LATE_OP);
            X arg0 = result;
            result = createTree(op);
            result.addArg(arg0);
            if (kind.getPlace() == Placement.POSTFIX) {
                break;
            }
            result.addArg(parse(kind));
        }
        setParseString(result, nextToken);
        return result;
    }

    /**
     * Attempts to parse the string as a constant expression.
     * The next token is known to be a constant token.
     */
    abstract protected @NonNull X parseConst() throws FormatException;

    /**
     * Attempts to parse the string as a bracketed expression.
     * The next token is known to be a left parenthesis.
     * @return the expression in brackets, or {@code null} if the input string does not
     * correspond to a bracketed expression
     */
    protected @NonNull X parseBracketed() throws FormatException {
        X result;
        Token lparToken = consume(LPAR);
        assert lparToken != null;
        result = parse(OpKind.NONE);
        if (consume(RPAR) == null) {
            throw expectedToken(RPAR, next());
        }
        setParseString(result, lparToken);
        return result;
    }

    /**
     * Attempts to parse the string as a prefix expression.
     * The next token is known to be a prefix operator;
     * it might still be the start of a qualified identifier.
     */
    protected @NonNull X parsePrefixed() throws FormatException {
        X result;
        Token opToken = consume(TokenClaz.PRE_OP);
        if (hasQualIds() && opToken.has(NAME) && has(TokenClaz.QUAL_SEP)) {
            rollBack();
            result = parseName();
        } else {
            result = parsePrefixed(opToken);
        }
        return result;
    }

    /**
     * Attempts to parse the string as a prefix expression.
     * @param opToken the operator token for the prefix expression;
     * guaranteed to contain a prefix operator
     */
    protected @NonNull X parsePrefixed(Token opToken) throws FormatException {
        @SuppressWarnings("unchecked")
        var op = (O) opToken.type(TokenClaz.PRE_OP).op();
        assert op != null; // never for pre-ops
        X result = createTree(op);
        if (op.getKind() == OpKind.CALL) {
            if (consume(LPAR) == null) {
                // this is not a call after all, must be a simple name
                rollBack();
                result = parseName();
                // throw expectedToken(LPAR, next());
            } else if (consume(RPAR) == null) {
                result.addArg(parse(OpKind.NONE));
                while (consume(TokenClaz.COMMA) != null) {
                    result.addArg(parse(OpKind.NONE));
                }
                if (consume(RPAR) == null) {
                    throw expectedToken(RPAR, next());
                }
                if (!op.allowsArgCount(result.getArgs().size())) {
                    throw argumentMismatch(op, result.getArgs().size(), opToken);
                }
            } else {
                // nullary call
                if (!op.allowsArgCount(0)) {
                    if (op.isVarArgs()) {
                        throw zeroArgsMismatch(op, opToken);
                    } else {
                        throw argumentMismatch(op, 0, opToken);
                    }
                }
            }
        } else if (op.getArity() == 1) {
            result.addArg(parse(op.getKind()));
        } else {
            assert op.getKind() == OpKind.ATOM : String
                .format("Encountered '%s' in prefix position", op);
        }
        setParseString(result, opToken);
        return result;
    }

    /**
     * Parses any sub-expression starting with a name token.
     * The name is guaranteed to be the start of an identifier,
     * and does not have to be investigated as operator.
     */
    abstract protected @NonNull X parseName() throws FormatException;

    /**
     * Parses the input as an identifier.
     * Assumes the first token is a {@link TokenClaz#NAME} token.
     */
    protected QualName parseId() throws FormatException {
        List<String> fragments = new ArrayList<>();
        Token nameToken = consume(NAME);
        assert nameToken != null;
        fragments.add(nameToken.substring());
        while (hasQualIds() && consume(TokenClaz.QUAL_SEP) != null) {
            nameToken = consume(NAME);
            if (nameToken == null) {
                throw unexpectedToken(next());
            }
            fragments.add(nameToken.substring());
        }
        return new QualName(fragments);
    }

    /** Factory method for atomic tree with a given error. */
    protected X createErrorTree(FormatException exc) {
        X result = createTree(getAtomOp());
        result.setParseString(this.input);
        result.getErrors().addAll(exc.getErrors());
        return result;
    }

    /** Sets the parse string of a given expression to the substring
     * starting at a given token and ending at the current position.
     * The text is only set if the tree is non-{@code null} and has no errors
     * @param tree the tree of which the parse string should be set
     * @param start the start token of the tree
     */
    protected void setParseString(X tree, Token start) {
        if (tree != null) {
            if (tree.hasErrors()) {
                tree.setParseString(this.input);
            } else {
                tree.setParseString(this.input.substring(start.start(), this.previousToken.end()));
            }
        }
    }

    /** Initialises the parser with a given input line. */
    protected void init(String text) {
        this.input = text;
        this.ix = 0;
        this.nextToken = null;
        this.futureToken = null;
        this.previousToken = null;
        this.eot = null;
    }

    /** Tests if the next token is of the expected token class;
     * if so, returns it, otherwise returns {@code null}
     * @param claz the expected token class
     * @return next token if it is of the right class, {@code null} otherwise
     * @throws ScanException if an error was encountered during scanning
     */
    protected final Token consume(TokenClaz claz) throws ScanException {
        Token result = null;
        if (has(claz)) {
            result = this.previousToken = this.nextToken;
            this.nextToken = this.futureToken;
            this.futureToken = null;
        }
        return result;
    }

    /** Tests if the next token has a certain token class.
     * Convenience method for {@code next().has(claz)}.
     */
    protected boolean has(TokenClaz claz) throws ScanException {
        return next().has(claz);
    }

    /** Returns the next unconsumed token in the input stream.
     * @throws ScanException if an error was encountered during scanning
     */
    protected final @NonNull Token next() throws ScanException {
        if (this.nextToken == null) {
            this.nextToken = scan();
        }
        return this.nextToken;
    }

    /** Rolls back the scanner by one token.
     * This can only be done once in a row;
     * i.e., only the previous token is retained and can be rolled back.
     */
    protected final void rollBack() {
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
    private @NonNull Token scan() throws ScanException {
        Token result = null;
        // the hasNext() call skips all whitespace
        if (!hasNext()) {
            result = eot();
        } else if (Character.isDigit(curChar())) {
            result = scanNumber();
        } else if (getIdValidator().isIdentifierStart(curChar())) {
            result = scanName();
        } else {
            switch (curChar()) {
            case StringHandler.SINGLE_QUOTE_CHAR, StringHandler.DOUBLE_QUOTE_CHAR:
                result = scanString();
                break;
            case '.':
                incChar();
                boolean isNumber = !atEnd() && Character.isDigit(curChar());
                decChar();
                if (isNumber) {
                    result = scanNumber();
                }
                break;
            default: // do nothing
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
        Token result = null;
        if (atEnd()) {
            result = eot();
        } else {
            int start = this.ix;
            // last recognised type
            TokenFamily type = null;
            // first index beyond the recognised type
            int typeEnd = start;
            SymbolTable map = getSymbolTable();
            while (!atEnd()) {
                SymbolTable nextMap = map.get(curChar());
                if (nextMap == null) {
                    // nextChar is not part of any operator symbol
                    break;
                }
                incChar();
                map = nextMap;
                TokenFamily recognisedType = map.getTokenFamily();
                if (recognisedType != null) {
                    type = recognisedType;
                    typeEnd = this.ix;
                }
            }
            if (type != null) {
                result = new Token(type, createFragment(start, typeEnd));
            }
            this.ix = typeEnd;
        }
        return result;
    }

    /** Scans in a number token from the input text.
     * It is guaranteed that the current character is a digit or decimal point;
     * if a decimal point, the next character is a digit.
     */
    private Token scanNumber() throws ScanException {
        assert Character.isDigit(curChar()) || curChar() == '.' && Character.isDigit(nextChar());
        int start = this.ix;
        while (!atEnd() && Character.isDigit(curChar())) {
            incChar();
        }
        Sort sort = !atEnd() && curChar() == '.'
            ? REAL
            : INT;
        if (sort == REAL) {
            // we are at the decimal point; consume it
            incChar();
            while (!atEnd() && Character.isDigit(curChar())) {
                incChar();
            }
            if (!atEnd() && Character.toUpperCase(curChar()) == 'E') {
                // we're in an exponent
                incChar();
                if (!atEnd() && curChar() == '-') {
                    // scan the optional minus sign
                    incChar();
                }
                if (atEnd() || !Character.isDigit(curChar())) {
                    throw new ScanException("Exponent does not specify value");
                }
                while (!atEnd() && Character.isDigit(curChar())) {
                    incChar();
                }
            }
        }
        return createConstToken(sort, start, this.ix);
    }

    /**
     * Scans a name token and returns it.
     * At call time, the current character is expected to be a valid identifier start.
     * @return the scanned name, or {@code null} if no valid name can be constructed
     */
    private Token scanName() {
        IdValidator validator = getIdValidator();
        assert validator.isIdentifierStart(curChar());
        int start = this.ix;
        incChar();
        while (!atEnd()
            && (validator.isIdentifierPart(curChar()) || validator.isIdentifierEnd(curChar()))) {
            incChar();
        }
        while (!validator.isIdentifierEnd(prevChar())) {
            decChar();
        }
        LineFragment fragment = createFragment(start, this.ix);
        String symbol = fragment.substring();
        TokenFamily family = getTokenFamily(symbol);
        if (family == null) {
            for (var sort : Sort.values()) {
                if (sort.denotesValue(symbol)) {
                    family = getTokenFamily(getConstTokenType(sort));
                    break;
                }
            }
        }
        if (family == null) {
            if (validator.isValid(symbol)) {
                family = getTokenFamily(NAME.type());
            } else {
                // roll back until the beginning of this token
                decChar(symbol.length());
            }
        }
        return family == null
            ? null
            : createFamilyToken(family, start, this.ix);
    }

    private Token scanString() throws ScanException {
        int start = this.ix;
        char quote = curChar();
        incChar();
        boolean escaped = false;
        while (!atEnd() && (escaped || curChar() != quote)) {
            escaped = curChar() == StringHandler.ESCAPE_CHAR;
            incChar();
        }
        if (atEnd()) {
            throw new ScanException("%s-quoted string is not closed", quote);
        } else {
            assert curChar() == quote;
            incChar();
        }
        return createConstToken(Sort.STRING, start, this.ix);
    }

    /**
     * Tests if there is a next non-EOT token.
     */
    private boolean hasNext() {
        while (!atEnd() && Character.isWhitespace(curChar())) {
            incChar();
        }
        return !atEnd();
    }

    /** Consumes all whitespace characters from the input,
     * then tests whether the end of the input string has been reached. */
    private boolean atEnd() {
        return this.ix == this.input.length();
    }

    /** Increments the character position. */
    private void incChar() {
        this.ix++;
    }

    /** Decrements the character position. */
    private void decChar() {
        this.ix--;
    }

    /** Decrements the character position. */
    private void decChar(int length) {
        this.ix -= length;
    }

    /** Returns the character just before the current position. */
    private char prevChar() {
        return this.input.charAt(this.ix - 1);
    }

    /** Returns the character at the current position. */
    private char curChar() {
        return this.input.charAt(this.ix);
    }

    /** Returns the character just after current position. */
    private char nextChar() {
        return this.input.charAt(this.ix + 1);
    }

    /** String currently being parsed. */
    private String input;

    /** Index at which the scanner currently stands. */
    private int ix;

    /** End-of-text token. */
    private Token eot() {
        if (this.eot == null) {
            int end = this.input.length();
            this.eot = createTypedToken(EOT.type(), end, end);
        }
        return this.eot;
    }

    private Token eot;

    /** Creates an exception reporting an expected but not encountered token. */
    protected ParseException expectedToken(TokenClaz claz, Token token) {
        String message = "Expected ";
        switch (claz) {
        case CONST:
            message += "a literal";
            break;
        case NAME:
            message += "an identifier";
            break;
        default:
            message += "'" + claz.symbol() + "'";
        }
        message += " rather than ";
        if (token.has(EOT)) {
            message += "end of input";
        } else {
            message += "'%s' at index %s";
        }
        return new ParseException(message, token.substring(), token.start());
    }

    /** Creates an exception reporting an unexpected token. */
    protected ParseException unexpectedToken(Token token) {
        if (token.has(EOT)) {
            return new ParseException("Unexpected end of input");
        } else {
            return new ParseException("Unexpected token '%s' at index %s", token.substring(),
                token.start());
        }
    }

    /** Creates an exception reporting an mismatch in arity. */
    protected ParseException argumentMismatch(O op, int argCount, Token token) {
        return new ParseException("Operator '%s' expects %s arguments but has %s at index %s",
            op.getSymbol(), op.getArity(), argCount, token.start());
    }

    /** Creates an exception reporting zero arguments for a collection-based operator. */
    protected ParseException zeroArgsMismatch(O op, Token token) {
        return new ParseException("Collection-based must have arguments at index %s",
            op.getSymbol(), token.start());
    }

    private ScanException unrecognisedToken() {
        return new ScanException("Unrecognised token '%s' at index %s", curChar(), this.ix);
    }

    /** Factory method for a line fragment.
     * @param start start position of the fragment
     * @param end end position of the fragment
     */
    private @NonNull LineFragment createFragment(int start, int end) {
        return new LineFragment(this.input, start, end);
    }

    private Token createConstToken(Sort sort, int start, int end) {
        return createTypedToken(getConstTokenType(sort), start, end);
    }

    private Token createTypedToken(TokenType type, int start, int end) {
        TokenFamily family = getTokenFamily(type);
        return createFamilyToken(family, start, end);
    }

    private Token createFamilyToken(@NonNull TokenFamily family, int start, int end) {
        return new Token(family, createFragment(start, end));
    }

    @Override
    public String toString() {
        return "Parser instance for " + this.input;
    }

    /** Returns the symbol table for this parser. */
    private SymbolTable getSymbolTable() {
        return this.symbolTable.get();
    }

    /** Lazily computed symbol table for this parser. */
    private Supplier<SymbolTable> symbolTable = lazy(this::computeSymbolTable);

    /** Computes the value for {@link #symbolTable}. */
    private SymbolTable computeSymbolTable() {
        return new SymbolTable(getSymbolFamilyMap().values());
    }

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
                        throw Exceptions.illegalArg("Duplicate token %s", symbol);
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

    /**
     * Token class used during parsing.
     * A token can still correspond to multiple token types,
     * though only of distinct token classes.
     * A token also contains the line fragment where it has been found.
     */
    protected static record Token(@NonNull TokenFamily family, @NonNull LineFragment fragment) {
        /** Returns the type of this token. */
        public TokenType type(TokenClaz claz) {
            return family().get(claz);
        }

        /** Indicates if this token may be of a given token class. */
        public boolean has(TokenClaz claz) {
            return family().containsKey(claz);
        }

        /** Returns the operator of a given token class, if there is
         * one in this token. */
        public Op op(TokenClaz claz) {
            assert claz == TokenClaz.PRE_OP || claz == TokenClaz.LATE_OP;
            @Nullable
            Op result = type(claz).op();
            assert result != null; // never null for pre- or late-ops
            return result;
        }

        /** Returns the start position of this token. */
        public int start() {
            return fragment().start();
        }

        /** Returns the end position of this token. */
        public int end() {
            return fragment().end();
        }

        /** Returns the string representation of the token content. */
        public @NonNull String substring() {
            return fragment().substring();
        }

        /** Creates a constant from this token,
         * if it is a constant token.
         * The token class is required to be {@link TokenClaz#CONST}.
         * @return a constant constructed from the string wrapped by this token
         */
        public Constant createConstant() {
            assert has(TokenClaz.CONST);
            Constant result = null;
            Sort sort = type(TokenClaz.CONST).sort();
            try {
                String symbol = substring();
                result = sort.createConstant(symbol);
                result.setParseString(symbol);
            } catch (FormatException exc) {
                assert false : String
                    .format("'%s' has been scanned as a token; how can it fail to be one? (%s)",
                            substring(), exc.getMessage());
            }
            return result;
        }
    }

    /**
     * Family of token types, indexed by token class.
     * This is used as a mechanism to allow some ambiguity of tokens with
     * the same symbol, as long as they are of different token class.
     * @author Arend Rensink
     * @version $Revision$
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
                assert !type.parsable() || this.symbol.equals(type.symbol());
            }
        }

        /** Returns the common symbol of all the token types in this family. */
        public String symbol() {
            return this.symbol;
        }

        private String symbol;
    }

    /** A string fragment, consisting of an input line with start and end position. */
    static record LineFragment(String line, int start, int end) {
        /**
         * Constructs a string fragment.
         * @param line the input line
         * @param start start position
         * @param end end position
         */
        public LineFragment(String line, int start, int end) {
            this.line = line;
            this.start = start;
            this.end = end;
            assert start >= 0;
            assert end >= start && end <= line.length();
        }

        /** Returns the fragment substring. */
        public @NonNull String substring() {
            return line().substring(start(), end());
        }
    }

    /**
     * Token kind; consists of a token type class and (if the type class is non-singular)
     * possibly some additional information.
     * @author Arend Rensink
     * @version $Revision$
     */
    public static record TokenType(TokenClaz claz, Sort sort, Op op) {
        /**
         * Constructs a token type for a singular type class.
         */
        public TokenType(TokenClaz claz) {
            this(claz, null, null);
        }

        /**
         * Constructs a token type for an operator.
         * @param op the (non-{@code null}) associated operator.
         */
        public TokenType(Op op) {
            this(getClaz(op.getKind().getPlace()), null, op);
        }

        /**
         * Constructs a token type for a sort or constant.
         * @param claz either {@link TokenClaz#CONST} or {@link TokenClaz#SORT}
         * @param sort the (non-{@code null}) associated sort.
         */
        public TokenType(TokenClaz claz, Sort sort) {
            this(claz, sort, null);
            assert claz == TokenClaz.CONST || claz == TokenClaz.SORT;
            assert sort != null;
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
            return switch (claz()) {
            case PRE_OP, LATE_OP -> op().getSymbol();
            case SORT -> sort().getName();
            default -> claz().symbol();
            };
        }
    }

    /** Returns the operator token class for  a given operator placement. */
    static TokenClaz getClaz(Placement place) {
        return switch (place) {
        case INFIX, POSTFIX -> TokenClaz.LATE_OP;
        case PREFIX -> TokenClaz.PRE_OP;
        };
    }

    /**
     * Token type class class.
     * Every token type has a class.
     * A token type class can either be singular, meaning that
     * there exists exactly one type of that class, or multiple.
     * @author Arend Rensink
     * @version $Revision$
     */
    public static enum TokenClaz {
        /** Prefix operator (including call operator). */
        PRE_OP(false),
        /** Latefix (i.e., non-prefix) operator. */
        LATE_OP(false),
        /** Sort name. */
        SORT(false),
        /** Algebraic constant token. */
        CONST(true),
        /**
         * Atomic name, formed like a Java identifier, with hyphens allowed in the middle.
         * @see IdValidator#GROOVE_ID
         */
        NAME(true),
        /** Qualifier separator. */
        QUAL_SEP("."),
        /** Sort separator. */
        SORT_SEP(":"),
        /** Assignment symbol. */
        ASSIGN("="),
        /** Minus sign, used for negated constants. */
        MINUS("-"),
        /** A static token, representing a left parenthesis. */
        LPAR("("),
        /** A static token, representing a right parenthesis. */
        RPAR(")"),
        /** A static token, representing a comma. */
        COMMA(","),
        /** A static token, representing an underscore. */
        UNDER("_"),
        /** A static token, representing the end of the input text. */
        EOT("" + Util.EOT),;

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
            this.type = single
                ? new TokenType(this)
                : null;
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
