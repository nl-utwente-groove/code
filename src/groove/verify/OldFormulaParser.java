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

import static groove.verify.LogicOp.ALWAYS;
import static groove.verify.LogicOp.AND;
import static groove.verify.LogicOp.EQUIV;
import static groove.verify.LogicOp.EVENTUALLY;
import static groove.verify.LogicOp.EXISTS;
import static groove.verify.LogicOp.FALSE;
import static groove.verify.LogicOp.FOLLOWS;
import static groove.verify.LogicOp.FORALL;
import static groove.verify.LogicOp.IMPLIES;
import static groove.verify.LogicOp.LPAR;
import static groove.verify.LogicOp.NEXT;
import static groove.verify.LogicOp.NOT;
import static groove.verify.LogicOp.OR;
import static groove.verify.LogicOp.PROP;
import static groove.verify.LogicOp.RELEASE;
import static groove.verify.LogicOp.RPAR;
import static groove.verify.LogicOp.S_RELEASE;
import static groove.verify.LogicOp.TRUE;
import static groove.verify.LogicOp.UNTIL;
import static groove.verify.LogicOp.W_UNTIL;
import groove.annotation.Help;
import groove.util.Pair;
import groove.util.parse.FormatException;

import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
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
public class OldFormulaParser {
    /** Parses a string into a formula over strings. */
    public static Formula parse(String str) throws FormatException { // "aObAc"

        Input i = new Input(str);

        Formula result = parse(i, 0);
        String suffix = i.rest();
        if (suffix.length() > 0) {
            throw new FormatException("unparsed formula suffix: " + i.rest());
        }
        if (DEBUG) {
            System.out.println("Formula: " + result);
            System.out.print(result.toTreeString());
        }
        return result;
    }

    /**
     * Returns a mapping from syntax documentation lines to associated (possibly {@code null}) tooltips.
     * @param ctl if {@code true}, only CTL operators are reported; if {@code false}, only LTL operators.
     */
    public static Map<String,String> getDocMap(boolean ctl) {
        Map<String,String> result = docMapMap.get(ctl);
        if (result == null) {
            docMapMap.put(ctl, result = computeDocMap(ctl));
        }
        return result;
    }

    /**
     * Computes a mapping from syntax documentation lines to associated (possibly {@code null}) tooltips.
     * @param ctl if {@code true}, only CTL operators are reported; if {@code false}, only LTL operators.
     */
    private static Map<String,String> computeDocMap(boolean ctl) {
        Map<String,String> result = new LinkedHashMap<String,String>();
        for (Field field : LogicOp.class.getFields()) {
            if (field.isEnumConstant()) {
                LogicOp token = nameToTokenMap.get(field.getName());
                if ((ctl ? CTLTokens : LTLTokens).contains(token)) {
                    Help help = Help.createHelp(field, nameToSymbolMap);
                    if (help != null) {
                        result.put(help.getItem(), help.getTip());
                    }
                }
            }
        }
        return result;
    }

    private static Formula parse(Input i, int precedence) throws FormatException {
        Formula formula;
        LogicOp token;
        int priority = i.get().getPriority();
        switch (token = i.get()) {
        // constants
        case TRUE:
        case FALSE:
            formula = new Formula(token);
            i.skip();
            break;

        case PROP:
            formula = Formula.Prop(i.text());
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
                throw new FormatException("Expected " + RPAR);
            }
            i.skip();
            break;

        default:
            throw new FormatException("Unexpected token: " + token);
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
                throw new FormatException("Unexpected token: " + token);
            }
        }
        return formula;
    }

    /** Mapping from token names to token values. */
    private static Map<String,LogicOp> nameToTokenMap = new HashMap<String,LogicOp>();
    /** Mapping from token symbols to token values. */
    private static Map<String,LogicOp> symbolToTokenMap = new HashMap<String,LogicOp>();
    /** Mapping from token names to HTML-formatted token strings. */
    private static Map<String,String> nameToSymbolMap = new HashMap<String,String>();
    private static Map<Boolean,Map<String,String>> docMapMap =
        new HashMap<Boolean,Map<String,String>>();

    static {
        for (LogicOp token : LogicOp.values()) {
            symbolToTokenMap.put(token.getSymbol(), token);
            nameToTokenMap.put(token.name(), token);
            nameToSymbolMap.put(token.name(), token.getSymbol());
        }
    }

    private final static boolean DEBUG = false;

    /** Set of tokens that can occur in CTL formulas. */
    final static Set<LogicOp> CTLTokens = EnumSet.of(PROP, TRUE, FALSE, NOT, OR, AND, IMPLIES,
        FOLLOWS, EQUIV, NEXT, UNTIL, ALWAYS, EVENTUALLY, FORALL, EXISTS, LPAR);
    /** Set of tokens that can occur in LTL formulas. */
    final static Set<LogicOp> LTLTokens = EnumSet.of(PROP, TRUE, FALSE, NOT, OR, AND, IMPLIES,
        FOLLOWS, EQUIV, NEXT, UNTIL, W_UNTIL, RELEASE, S_RELEASE, ALWAYS, EVENTUALLY, LPAR);

    /**
     * Input stream for the parser.
     */
    static class Input {
        public Input(String str) {
            this.sb = new StringBuilder(str);
        }

        /**
         * Returns the next token from the input stream.
         * Repeated calls (without intervening {@link #skip()}) will
         * return the same token.
         * If the token is a {@link LogicOp#PROP}, the
         * corresponding text can be retrieved by a subsequent call to
         * #text().
         * @throws FormatException if there is no more token
         */
        public LogicOp get() throws FormatException {
            if (readNext()) {
                Pair<LogicOp,String> next = this.q.peek();
                return next.one();
            } else {
                throw new FormatException("Unexpected end of text");
            }
        }

        /**
         * Returns the text of the front token in the input stream
         * if that is a {@link LogicOp#PROP}, or {@code null} if it is not.
         * @throws FormatException if there is no more token
         */
        public String text() throws FormatException {
            if (readNext()) {
                Pair<LogicOp,String> next = this.q.peek();
                return next.two();
            } else {
                throw new FormatException("Unexpected end of text");
            }
        }

        /**
         * Skips to the next token in the input stream.
         * @throws FormatException if there is no token to skip.
         */
        public void skip() throws FormatException {
            if (readNext()) {
                this.q.poll();
            } else {
                throw new FormatException("Unexpected end of text");
            }
        }

        /** Returns the remainder (i.e., the unparsed part) of the input string. */
        public String rest() {
            StringBuffer result = new StringBuffer();
            for (Pair<LogicOp,String> el : this.q) {
                if (el.one() == PROP) {
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
        public boolean done() throws FormatException {
            return !readNext();
        }

        /**
         * Scans the next tokens from the input stream and appends them
         * to the token queue.
         * @throws FormatException if there is no more token, or
         * the input string cannot be parsed into tokens
         */
        private boolean readNext() throws FormatException {
            if (!this.q.isEmpty()) {
                return true;
            }
            while (this.sb.length() > 0 && Character.isWhitespace(this.sb.charAt(0))) {
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
         * @throws FormatException if an unknown token is found
         */
        private void readNextOther(char c) throws FormatException {
            StringBuffer text = new StringBuffer();
            text.append(c);
            // concatenate other chars, if and when appropriate
            if (concChars.contains(c)) {
                for (int i = 1; i < this.sb.length() && concChars.contains(c = this.sb.charAt(i)); i++) {
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
         * @throws FormatException if an unknown temporal operator is found
         */
        private void readNextWord(char c) throws FormatException {
            StringBuffer text = new StringBuffer();
            text.append(c);
            boolean caps = Character.isUpperCase(c);
            boolean allCaps = Character.isUpperCase(c);
            for (int i = 1; i < this.sb.length()
                && Character.isJavaIdentifierPart(c = this.sb.charAt(i)); i++) {
                text.append(c);
                allCaps &= Character.isUpperCase(c);
            }
            if (caps) {
                if (!allCaps) {
                    throw new FormatException("Uppercase atom '%s' not allowed", text);
                }
                // each letter is parsed as an operator
                for (int i = 0; i < text.length(); i++) {
                    this.q.add(createToken("" + text.charAt(i)));
                }
            } else {
                this.q.add(createAtom(text.toString()));
            }
            this.sb.delete(0, text.length());
        }

        /**
         * Reads a quoted atom.
         * @param c the opening quote character
         * @throws FormatException if the end of text is read before
         * the atom is closed
         */
        private void readNextQuotedAtom(char c) throws FormatException {
            StringBuffer text = new StringBuffer();
            char quote = c;
            int i;
            for (i = 1; i < this.sb.length() && (c = this.sb.charAt(i)) != quote; i++) {
                if (c == '\\') {
                    // test if this is an escaped single quote or escape
                    i++;
                    if (i == this.sb.length()) {
                        throw new FormatException("Unexpected end of text");
                    }
                    c = this.sb.charAt(i);
                    if (c != '\\' && c != quote) {
                        throw new FormatException("Invalid escaped character: " + c);
                    }
                }
                text.append(c);
            }
            if (i == this.sb.length()) {
                throw new FormatException("Unexpected end of text while scanning for closing "
                    + quote);
            }
            this.q.add(createAtom(text.toString()));
            this.sb.delete(0, text.length() + 2);
        }

        /**
         * Creates a token/string pair consisting of the token with a given
         * symbol, and a {@code null} string.
         * @param symbol the symbol of the requested token
         * @throws FormatException if no token with {@code symbol} exists
         */
        private Pair<LogicOp,String> createToken(String symbol) throws FormatException {
            LogicOp token = symbolToTokenMap.get(symbol);
            if (token == null) {
                throw new FormatException("Can't parse token '%s'", symbol);
            }
            return new Pair<LogicOp,String>(token, null);
        }

        /**
         * Creates a token/string pair consisting of an {@link LogicOp#PROP}
         * and a given atom text.
         */
        private Pair<LogicOp,String> createAtom(String text) {
            if (text.equals(TRUE.getSymbol())) {
                return new Pair<LogicOp,String>(TRUE, null);
            } else if (text.equals(FALSE.getSymbol())) {
                return new Pair<LogicOp,String>(FALSE, null);
            } else {
                return new Pair<LogicOp,String>(PROP, text);
            }
        }

        /** Input string. */
        private final StringBuilder sb;
        /** Queue of tokens with possible associated text. */
        private final Queue<Pair<LogicOp,String>> q = new LinkedList<Pair<LogicOp,String>>();
        /** Set of concatenable characters. */
        private final static Set<Character> concChars = new HashSet<Character>();
        // add all non-letter, non-parenthesis characters from the tokens to concChars
        static {
            for (LogicOp token : LogicOp.values()) {
                if (token == LPAR || token == RPAR || token == NOT) {
                    continue;
                }
                if (!token.hasSymbol()) {
                    continue;
                }
                if (Character.isLetter(token.getSymbol().charAt(0))) {
                    continue;
                }
                for (char c : token.getSymbol().toCharArray()) {
                    concChars.add(c);
                }
            }
        }
    }
}
