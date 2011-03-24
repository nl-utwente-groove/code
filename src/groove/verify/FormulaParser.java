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
import gov.nasa.ltl.trans.ParseErrorException;
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
    public static Formula parse(String str) throws ParseErrorException { // "aObAc"

        Input i = new Input(str);

        Formula result = parse(i, 0);
        String suffix = i.rest();
        if (suffix.length() > 0) {
            throw new ParseErrorException("unparsed formula suffix: "
                + i.rest());
        }
        if (DEBUG) {
            System.out.println("Formula: " + result);
            System.out.print(result.toTree());
        }
        return result;
    }

    private static Formula parse(Input i, int precedence)
        throws ParseErrorException {
        Formula formula;
        Token ch;
        int priority = i.get().getPriority();
        switch (ch = i.get()) {
        case NOT: // not
            i.skip();
            formula = Formula.Not(parse(i, priority));
            break;

        case NEXT: // next
            i.skip();
            formula = Formula.Next(parse(i, priority));
            break;

        case ALWAYS: // always
            i.skip();
            formula = Formula.Always(parse(i, priority));
            break;

        case EVENTUALLY: // eventually
            i.skip();
            formula = Formula.Eventually(parse(i, priority));
            break;

        case FORALL: // eventually
            i.skip();
            formula = Formula.Forall(parse(i, priority));
            break;

        case EXISTS: // eventually
            i.skip();
            formula = Formula.Exists(parse(i, priority));
            break;

        case LPAR:
            i.skip();
            formula = parse(i, priority);
            if (i.get() != RPAR) {
                throw new ParseErrorException("Expected " + RPAR);
            }
            i.skip();
            break;

        case TRUE:
            formula = Formula.True();
            i.skip();
            break;
        case FALSE:
            formula = Formula.False();
            i.skip();
            break;
        case ATOM:
            formula = Formula.Atom(i.text());
            i.skip();
            break;

        default:
            throw new ParseErrorException("Unexpected token: " + ch);
        }

        while (!i.done()) {
            priority = i.get().getPriority();
            switch (ch = i.get()) {
            case AND: // and
                if (precedence > priority) {
                    return formula;
                }
                i.skip();
                formula = Formula.And(formula, parse(i, priority));
                break;

            case OR: // or
                if (precedence > priority) {
                    return formula;
                }
                i.skip();
                formula = Formula.Or(formula, parse(i, priority));
                break;

            case UNTIL: // until
                if (precedence > priority) {
                    return formula;
                }
                i.skip();
                formula = Formula.Until(formula, parse(i, priority));
                break;

            case W_UNTIL: // weak until

                if (precedence > priority) {
                    return formula;
                }

                i.skip();
                formula = Formula.WUntil(formula, parse(i, priority));

                break;

            case S_RELEASE: // release
                if (precedence > priority) {
                    return formula;
                }
                i.skip();
                formula = Formula.SRelease(formula, parse(i, priority));
                break;

            case RELEASE: // weak_release
                if (precedence > priority) {
                    return formula;
                }
                i.skip();
                formula = Formula.Release(formula, parse(i, priority));
                break;

            case IMPLIES: // implies
                if (precedence > priority) {
                    return formula;
                }
                i.skip();
                formula = Formula.Implies(formula, parse(i, priority));
                break;

            case FOLLOWS: // follows
                if (precedence > priority) {
                    return formula;
                }
                i.skip();
                formula = Formula.Follows(formula, parse(i, priority));
                break;

            case EQUIV: // equivalence
                if (precedence > priority) {
                    return formula;
                }
                i.skip();
                formula = Formula.Equiv(formula, parse(i, priority));
                break;

            case RPAR:
                return formula;

            default:
                throw new ParseErrorException("Unexpected token: " + ch);
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
         * @throws ParseErrorException if there is no more token 
         */
        public Token get() throws ParseErrorException {
            readNext();
            Pair<Token,String> next = this.q.peek();
            return next.one();
        }

        /**
         * Returns the text of the front token in the input stream
         * if that is a {@link Token#ATOM}, or {@code null} if it is not.
         * @throws ParseErrorException if there is no more token 
         */
        public String text() throws ParseErrorException {
            readNext();
            Pair<Token,String> next = this.q.peek();
            return next.two();
        }

        /** 
         * Skips to the next token in the input stream.
         * @throws ParseErrorException if there is no token to skip.
         */
        public void skip() throws ParseErrorException {
            readNext();
            this.q.poll();
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
        public boolean done() {
            try {
                readNext();
                return false;
            } catch (ParseErrorException e) {
                return true;
            }
        }

        /**
         * Scans the next tokens from the input stream and appends them
         * to the token queue.
         * @throws ParseErrorException if there is no more token, or 
         * the input string cannot be parsed into tokens
         */
        private void readNext() throws ParseErrorException {
            if (!this.q.isEmpty()) {
                return;
            }
            while (this.sb.length() > 0
                && Character.isWhitespace(this.sb.charAt(0))) {
                this.sb.deleteCharAt(0);
            }
            if (this.sb.length() == 0) {
                throw new ParseErrorException("Unexpected end of text");
            }
            char c = this.sb.charAt(0);
            if (Character.isJavaIdentifierStart(c)) {
                readNextWord(c);
            } else if (c == '\'' || c == '"') {
                readNextQuotedAtom(c);
            } else {
                readNextOther(c);
            }
        }

        /**
         * Reads a token consisting of non-letter characters.
         * @param c the first character
         * @throws ParseErrorException if an unknown token is found
         */
        private void readNextOther(char c) throws ParseErrorException {
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
         * @throws ParseErrorException if an unknown temporal operator is found
         */
        private void readNextWord(char c) throws ParseErrorException {
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
         * @throws ParseErrorException if the end of text is read before
         * the atom is closed
         */
        private void readNextQuotedAtom(char c) throws ParseErrorException {
            StringBuffer text = new StringBuffer();
            char quote = c;
            int i;
            for (i = 1; i < this.sb.length()
                && (c = this.sb.charAt(i)) != quote; i++) {
                if (c == '\\') {
                    // test if this is an escaped single quote or escape
                    i++;
                    if (i == this.sb.length()) {
                        throw new ParseErrorException("Unexpected end of text");
                    }
                    c = this.sb.charAt(i);
                    if (c != '\\' && c != quote) {
                        throw new ParseErrorException(
                            "Invalid escaped character: " + c);
                    }
                }
                text.append(c);
            }
            if (i == this.sb.length()) {
                throw new ParseErrorException(
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
         * @throws ParseErrorException if no token with {@code symbol} exists
         */
        private Pair<Token,String> createToken(String symbol)
            throws ParseErrorException {
            Token token = tokenMap.get(symbol);
            if (token == null) {
                throw new ParseErrorException("Can't parse token " + symbol);
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
