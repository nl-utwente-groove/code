/**
 * Copyright (C) 2006 United States Government as represented by the
 * Administrator of the National Aeronautics and Space Administration
 * (NASA).  All Rights Reserved.
 *
 * This software is distributed under the NASA Open Source Agreement
 * (NOSA), version 1.3.  The NOSA has been approved by the Open Source
 * Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
 * directory tree for the complete NOSA document.
 *
 * THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
 * KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
 * LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
 * SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
 * A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
 * THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
 * DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
 */
package groove.verify;

import static groove.verify.FormulaParser.Token.ATOM;
import static groove.verify.FormulaParser.Token.LPAR;
import static groove.verify.FormulaParser.Token.NOT;
import static groove.verify.FormulaParser.Token.RPAR;
import groove.util.Pair;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Written by Dimitra Giannakopoulou, 19 Jan 2001
 * Parser by Flavio Lerda, 8 Feb 2001
 * Parser extended by Flavio Lerda, 21 Mar 2001
 * Modified to accept && and || by Roby Joehanes 15 Jul 2002
 * Extended to accept escaped quotes within quoted propositions by Arend Rensink
 * Feb 2011
 */
public class FormulaParser {
    /** Parses a string into a formula over strings. */
    public static Formula parse(String str) throws ParseException { // "aObAc"

        Input i = new Input(str);

        Formula result = parse(i, 0);
        String suffix = i.rest();
        if (suffix.length() > 0) {
            throw new ParseException("unparsed formula suffix: " + i.rest());
        }
        if (DEBUG) {
            System.out.println("Formula: " + result);
            System.out.print(result.toTree());
        }
        return result;
    }

    private static Formula parse(Input i, int precedence) throws ParseException {
        Formula formula;
        Token token;
        int priority = i.get().getPriority();
        switch (token = i.get()) {
        // constants
        case TRUE:
        case FALSE:
            formula = new Formula(token);
            i.skip();
            break;

        case ATOM:
            formula = Formula.Atom(i.text());
            i.skip();
            break;

        // unary operators
        case NOT:
        case NEXT:
        case ALWAYS:
        case EVENTUALLY:
        case FORALL:
        case EXISTS:
            i.skip();
            formula = new Formula(token, parse(i, priority));
            break;

        case LPAR:
            i.skip();
            formula = parse(i, priority);
            if (i.get() != RPAR) {
                throw new ParseException("Expected " + RPAR);
            }
            i.skip();
            break;

        default:
            throw new ParseException("Unexpected token: " + token);
        }

        while (!i.done()) {
            priority = i.get().getPriority();
            switch (token = i.get()) {
            case AND:
            case OR:
            case IMPLIES:
            case FOLLOWS:
            case EQUIV:
            case UNTIL:
            case W_UNTIL:
            case S_RELEASE:
            case RELEASE:
                if (precedence > priority) {
                    return formula;
                }
                i.skip();
                formula = new Formula(token, formula, parse(i, priority));
                break;

            case RPAR:
                return formula;

            default:
                throw new ParseException("Unexpected token: " + token);
            }
        }
        return formula;
    }

    /** Mapping from token symbols to tokens. */
    private static Map<String,Token> tokenMap = new HashMap<String,Token>();

    static {
        for (Token token : EnumSet.allOf(Token.class)) {
            tokenMap.put(token.getSymbol(), token);
        }
    }

    private final static boolean DEBUG = false;

    /**
     * Input stream for the parser.
     */
    private static class Input {
        public Input(String str) {
            this.sb = new StringBuilder(str);
        }

        /** 
         * Returns the next token from the input stream.
         * Repeated calls (without intervening {@link #skip()}) will
         * return the same token.
         * If the token is a {@link Token#ATOM}, the
         * corresponding text can be retrieved by a subsequent call to
         * #text().
         * @throws ParseException if there is no more token 
         */
        public Token get() throws ParseException {
            if (readNext()) {
                Pair<Token,String> next = this.q.peek();
                return next.one();
            } else {
                throw new ParseException("Unexpected end of text");
            }
        }

        /**
         * Returns the text of the front token in the input stream
         * if that is a {@link Token#ATOM}, or {@code null} if it is not.
         * @throws ParseException if there is no more token 
         */
        public String text() throws ParseException {
            if (readNext()) {
                Pair<Token,String> next = this.q.peek();
                return next.two();
            } else {
                throw new ParseException("Unexpected end of text");
            }
        }

        /** 
         * Skips to the next token in the input stream.
         * @throws ParseException if there is no token to skip.
         */
        public void skip() throws ParseException {
            if (readNext()) {
                this.q.poll();
            } else {
                throw new ParseException("Unexpected end of text");
            }
        }

        /** Returns the remainder (i.e., the unparsed part) of the input string. */
        public String rest() {
            StringBuffer result = new StringBuffer();
            for (Pair<Token,String> el : this.q) {
                if (el.one() == ATOM) {
                    result.append(el.two());
                } else {
                    result.append(el.one());
                }
            }
            if (result.length() > 0 && this.sb.length() > 0) {
                result.append(' ');
            }
            result.append(this.sb);
            return result.toString();
        }

        /** Tests if the input stream is empty. */
        public boolean done() throws ParseException {
            return !readNext();
        }

        /**
         * Scans the next tokens from the input stream and appends them
         * to the token queue.
         * @throws ParseException if there is no more token, or 
         * the input string cannot be parsed into tokens
         */
        private boolean readNext() throws ParseException {
            if (!this.q.isEmpty()) {
                return true;
            }
            while (this.sb.length() > 0
                && Character.isWhitespace(this.sb.charAt(0))) {
                this.sb.deleteCharAt(0);
            }
            if (this.sb.length() == 0) {
                return false;
            }
            char c = this.sb.charAt(0);
            if (Character.isJavaIdentifierStart(c)) {
                readNextWord(c);
            } else if (c == '\'' || c == '"') {
                readNextQuotedAtom(c);
            } else {
                readNextOther(c);
            }
            return true;
        }

        /**
         * Reads a token consisting of non-letter characters.
         * @param c the first character
         * @throws ParseException if an unknown token is found
         */
        private void readNextOther(char c) throws ParseException {
            StringBuffer text = new StringBuffer();
            text.append(c);
            // concatenate other chars, if and when appropriate
            if (concChars.contains(c)) {
                for (int i = 1; i < this.sb.length()
                    && concChars.contains(c = this.sb.charAt(i)); i++) {
                    text.append(c);
                }
            }
            this.q.add(createToken(text.toString()));
            this.sb.delete(0, text.length());
        }

        /**
         * Reads tokens formed by a sequence of characters.
         * This could constitute a sequence of temporal operators,
         * or an atom.
         * @param c the first character out of the sequence.
         * @throws ParseException if an unknown temporal operator is found
         */
        private void readNextWord(char c) throws ParseException {
            StringBuffer text = new StringBuffer();
            text.append(c);
            boolean allCaps = Character.isUpperCase(c);
            for (int i = 1; i < this.sb.length()
                && Character.isJavaIdentifierPart(c = this.sb.charAt(i)); i++) {
                text.append(c);
                allCaps &= Character.isUpperCase(c);
            }
            if (allCaps) {
                // each letter is parsed as an operator
                for (int i = 0; i < text.length(); i++) {
                    this.q.add(createToken("" + text.charAt(i)));
                }
            } else {
                this.q.add(createAtom(text));
            }
            this.sb.delete(0, text.length());
        }

        /**
         * Reads a quoted atom.
         * @param c the opening quote character
         * @throws ParseException if the end of text is read before
         * the atom is closed
         */
        private void readNextQuotedAtom(char c) throws ParseException {
            StringBuffer text = new StringBuffer();
            char quote = c;
            int i;
            for (i = 1; i < this.sb.length()
                && (c = this.sb.charAt(i)) != quote; i++) {
                if (c == '\\') {
                    // test if this is an escaped single quote or escape
                    i++;
                    if (i == this.sb.length()) {
                        throw new ParseException("Unexpected end of text");
                    }
                    c = this.sb.charAt(i);
                    if (c != '\\' && c != quote) {
                        throw new ParseException("Invalid escaped character: "
                            + c);
                    }
                }
                text.append(c);
            }
            if (i == this.sb.length()) {
                throw new ParseException(
                    "Unexpected end of text while scanning for closing "
                        + quote);
            }
            this.q.add(createAtom(text));
            this.sb.delete(0, text.length() + 2);
        }

        /**
         * Creates a token/string pair consisting of the token with a given
         * symbol, and a {@code null} string.
         * @param symbol the symbol of the requested token
         * @throws ParseException if no token with {@code symbol} exists
         */
        private Pair<Token,String> createToken(String symbol)
            throws ParseException {
            Token token = tokenMap.get(symbol);
            if (token == null) {
                throw new ParseException("Can't parse token '%s'", symbol);
            }
            return new Pair<Token,String>(token, null);
        }

        /** 
         * Creates a token/string pair consisting of an {@link Token#ATOM}
         * and a given atom text.
         */
        private Pair<Token,String> createAtom(StringBuffer text) {
            return new Pair<Token,String>(ATOM, text.toString());
        }

        /** Input string. */
        private final StringBuilder sb;
        /** Queue of tokens with possible associated text. */
        private final Queue<Pair<Token,String>> q =
            new LinkedList<Pair<Token,String>>();
        /** Set of concatenable characters. */
        private final static Set<Character> concChars =
            new HashSet<Character>();
        // add all non-letter, non-parenthesis characters from the tokens to concChars
        static {
            for (Token token : EnumSet.allOf(Token.class)) {
                if (token != ATOM && token != LPAR && token != RPAR
                    && token != NOT
                    && !Character.isLetter(token.symbol.charAt(0))) {
                    for (char c : token.getSymbol().toCharArray()) {
                        concChars.add(c);
                    }
                }
            }
        }
    }

    /** The kind (i.e., top level operator) of a formula. */
    static public enum Token {
        /** Atomic proposition. */
        ATOM("", 0, 7),
        /** True. */
        TRUE("true", 0, 7),
        /** False. */
        FALSE("false", 0, 7),
        /** Negation. */
        NOT("!", 1, 6),
        /** Disjunction. */
        OR("|", 2, 2),
        /** Conjunction. */
        AND("&", 2, 3),
        /** Implication. */
        IMPLIES("->", 2, 1),
        /** Inverse implication. */
        FOLLOWS("<-", 2, 1),
        /** Equivalence. */
        EQUIV("<->", 2, 1),
        /** Next-state. */
        NEXT("X", 1, 6),
        /** Temporal until. */
        UNTIL("U", 2, 4),
        /** Weak temporal until (second operand may never hold). */
        W_UNTIL("W", 2, 4),
        /** Temporal release. */
        RELEASE("V", 2, 4),
        /** Strong temporal release (second operand must eventually hold). */
        S_RELEASE("M", 2, 4),
        /** Everywhere along a path. */
        ALWAYS("G", 1, 6),
        /** Eventually along a path. */
        EVENTUALLY("F", 1, 6),
        /** For all paths. */
        FORALL("A", 1, 3),
        /** There exists a path. */
        EXISTS("E", 1, 3),
        /** Left parenthesis. */
        LPAR("("),
        /** Right parenthesis. */
        RPAR(")");

        /** Private constructor for a non-operator token. */
        private Token(String symbol) {
            this(symbol, -1, 0);
        }

        /** Private constructor for an operator token. */
        private Token(String symbol, int arity, int priority) {
            this.symbol = symbol;
            this.arity = arity;
            this.priority = priority;
        }

        @Override
        public String toString() {
            return getSymbol();
        }

        /** Returns the symbol for the top-level operator. */
        String getSymbol() {
            return this.symbol;
        }

        /** Returns the number of arguments of the operator. */
        int getArity() {
            return this.arity;
        }

        /** Returns the priority of the operator. */
        int getPriority() {
            return this.priority;
        }

        /** The symbol for the top-level operator. */
        private final String symbol;
        /** The number of operands of a formula of this kind. */
        private final int arity;
        /** The priority of the top-level operator. */
        private final int priority;
    }
}
