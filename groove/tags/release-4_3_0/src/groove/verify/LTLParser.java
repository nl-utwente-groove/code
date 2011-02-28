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

import gov.nasa.ltl.trans.Formula;
import gov.nasa.ltl.trans.ParseErrorException;

import java.util.Stack;

/**
 * Written by Dimitra Giannakopoulou, 19 Jan 2001
 * Parser by Flavio Lerda, 8 Feb 2001
 * Parser extended by Flavio Lerda, 21 Mar 2001
 * Modified to accept && and || by Roby Joehanes 15 Jul 2002
 * Extended to accept escaped quotes within quoted propositions by Arend Rensink
 * Feb 2011
 */
public class LTLParser {

    /**
     * DOCUMENT ME!
     */
    private static class Input {
        private StringBuilder sb;

        public Input(String str) {
            this.sb = new StringBuilder(str);
        }

        public char get() throws EndOfInputException {
            try {
                return this.sb.charAt(0);
            } catch (StringIndexOutOfBoundsException e) {
                throw new EndOfInputException();
            }
        }

        public void skip() throws EndOfInputException {
            try {
                this.sb.deleteCharAt(0);
            } catch (StringIndexOutOfBoundsException e) {
                throw new EndOfInputException();
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    @SuppressWarnings("serial")
    private static class EndOfInputException extends Exception {
        // empty extension
    }

    /** Parses a string into a formula over strings. */
    public static Formula<String> parse(String str) throws ParseErrorException { // "aObAc"

        Input i = new Input(str);

        Formula<String> result = parse(i, P_ALL);
        StringBuilder suffix = new StringBuilder();
        boolean empty = true;
        try {
            while (true) {
                if (!empty || i.get() != ' ') {
                    empty = false;
                    suffix.append(i.get());
                }
                i.skip();
            }
        } catch (EndOfInputException e) {
            // we're at the end of input, as expected
        }
        if (!empty) {
            throw new ParseErrorException("unparsed formula suffix: " + suffix);
        }
        if (DEBUG) {
            System.out.println("Formula: " + result);
            System.out.print(toString(result));
        }
        return result;
    }

    private static Formula<String> parse(Input i, int precedence)
        throws ParseErrorException {
        try {
            Formula<String> formula;
            char ch;

            while (i.get() == ' ') {
                i.skip();
            }

            switch (ch = i.get()) {
            case '/': // and
            case '&': // robbyjo's and
            case '\\': // or
            case '|': // robbyjo's or
            case 'U': // until
            case 'W': // weak until
            case 'V': // release
            case 'M': // dual of W - weak release
            case ')':
                throw new ParseErrorException("invalid character: " + ch);

            case '!': // not
                i.skip();
                formula = Formula.Not(parse(i, P_NOT));

                break;

            case 'X': // next
                i.skip();
                formula = Formula.Next(parse(i, P_NEXT));

                break;

            case '[': // always
                i.skip();

                if (i.get() != ']') {
                    throw new ParseErrorException("expected ]");
                }

                i.skip();
                formula = Formula.Always(parse(i, P_ALWAYS));

                break;

            case 'G': // standard always
                i.skip();
                formula = Formula.Always(parse(i, P_ALWAYS));

                break;

            case '<': // eventually
                i.skip();

                if (i.get() != '>') {
                    throw new ParseErrorException("expected >");
                }

                i.skip();
                formula = Formula.Eventually(parse(i, P_EVENTUALLY));

                break;

            case 'F': // standard eventually
                i.skip();
                formula = Formula.Eventually(parse(i, P_EVENTUALLY));

                break;

            case '(':
                i.skip();
                formula = parse(i, P_ALL);

                if (i.get() != ')') {
                    throw new ParseErrorException("invalid character: " + ch);
                }

                i.skip();

                break;

            case '\'':
                // single-quoted propositions
                StringBuilder sb = new StringBuilder();
                i.skip();

                while ((ch = i.get()) != '\'') {
                    if (ch == '\\') {
                        // test if this is an escaped single quote or escape
                        i.skip();
                        ch = i.get();
                        switch (ch) {
                        case '\\':
                        case '\'':
                            break;
                        default:
                            throw new ParseErrorException(
                                "invalid escaped character: " + ch);
                        }
                    }
                    sb.append(ch);
                    i.skip();
                }

                i.skip();

                formula = Formula.Proposition(sb.toString());

                break;

            case '"':
                // double-quoted propositions
                sb = new StringBuilder();
                i.skip();

                while ((ch = i.get()) != '"') {
                    if (ch == '\\') {
                        // test if this is an escaped double quote or escape
                        i.skip();
                        ch = i.get();
                        switch (ch) {
                        case '\\':
                        case '"':
                            break;
                        default:
                            throw new ParseErrorException(
                                "invalid escaped character: " + ch);
                        }
                    }
                    sb.append(ch);
                    i.skip();
                }

                i.skip();

                formula = Formula.Proposition(sb.toString());

                break;

            default:
                if (Character.isJavaIdentifierStart(ch)) {
                    StringBuilder sbf = new StringBuilder();

                    sbf.append(ch);
                    i.skip();

                    try {
                        while (Character.isJavaIdentifierPart(ch = i.get())) {
                            sbf.append(ch);
                            i.skip();
                        }
                    } catch (EndOfInputException e) {
                        // return Proposition(sbf.toString());
                    }

                    String id = sbf.toString();

                    if (id.equals("true")) {
                        formula = Formula.True();
                    } else if (id.equals("false")) {
                        formula = Formula.False();
                    } else {
                        formula = Formula.Proposition(id);
                    }
                } else {
                    throw new ParseErrorException("invalid character: " + ch);
                }

                break;
            }

            try {
                while (i.get() == ' ') {
                    i.skip();
                }

                ch = i.get();
            } catch (EndOfInputException e) {
                return formula;
            }

            while (true) {
                switch (ch) {
                case '/': // and

                    if (precedence > P_AND) {
                        return formula;
                    }

                    i.skip();

                    if (i.get() != '\\') {
                        throw new ParseErrorException("expected \\");
                    }

                    i.skip();
                    formula = Formula.And(formula, parse(i, P_AND));

                    break;

                case '&': // robbyjo's and

                    if (precedence > P_AND) {
                        return formula;
                    }

                    i.skip();

                    if (i.get() != '&') {
                        throw new ParseErrorException("expected &&");
                    }

                    i.skip();
                    formula = Formula.And(formula, parse(i, P_AND));

                    break;

                case '\\': // or

                    if (precedence > P_OR) {
                        return formula;
                    }

                    i.skip();

                    if (i.get() != '/') {
                        throw new ParseErrorException("expected /");
                    }

                    i.skip();
                    formula = Formula.Or(formula, parse(i, P_OR));

                    break;

                case '|': // robbyjo's or

                    if (precedence > P_OR) {
                        return formula;
                    }

                    i.skip();

                    if (i.get() != '|') {
                        throw new ParseErrorException("expected ||");
                    }

                    i.skip();
                    formula = Formula.Or(formula, parse(i, P_OR));

                    break;

                case 'U': // until

                    if (precedence > P_UNTIL) {
                        return formula;
                    }

                    i.skip();
                    formula = Formula.Until(formula, parse(i, P_UNTIL));

                    break;

                case 'W': // weak until

                    if (precedence > P_WUNTIL) {
                        return formula;
                    }

                    i.skip();
                    formula = Formula.WUntil(formula, parse(i, P_WUNTIL));

                    break;

                case 'V': // release

                    if (precedence > P_RELEASE) {
                        return formula;
                    }

                    i.skip();
                    formula = Formula.Release(formula, parse(i, P_RELEASE));

                    break;

                case 'M': // weak_release

                    if (precedence > P_WRELEASE) {
                        return formula;
                    }

                    i.skip();
                    formula = Formula.WRelease(formula, parse(i, P_WRELEASE));

                    break;

                case '-': // implies

                    if (precedence > P_IMPLIES) {
                        return formula;
                    }

                    i.skip();

                    if (i.get() != '>') {
                        throw new ParseErrorException("expected >");
                    }

                    i.skip();
                    formula = Formula.Implies(formula, parse(i, P_IMPLIES));

                    break;

                case ')':
                    return formula;

                case '!':
                case 'X':
                case '[':
                case '<':
                case '(':
                default:
                    throw new ParseErrorException("invalid character: " + ch);
                }

                try {
                    while (i.get() == ' ') {
                        i.skip();
                    }

                    ch = i.get();
                } catch (EndOfInputException e) {
                    break;
                }
            }

            return formula;
        } catch (EndOfInputException e) {
            throw new ParseErrorException("unexpected end of input");
        }
    }

    /** Returns the syntax tree of the formula. */
    public static final String toString(Formula<String> formula) {
        StringBuilder result = new StringBuilder();
        toString(formula, new Stack<Boolean>(), result);
        result.append('\n');
        return result.toString();
    }

    private static final void toString(Formula<String> formula,
            Stack<Boolean> indent, StringBuilder result) {
        switch (formula.getContent()) {
        case AND:
        case OR:
        case RELEASE:
        case UNTIL:
        case WEAK_UNTIL:
            result.append(formula.getContent() + "+-");
            indent.push(true);
            toString(formula.getSub1(), indent, result);
            result.append('\n');
            addIndent(indent, result);
            indent.pop();
            indent.push(false);
            toString(formula.getSub2(), indent, result);
            indent.pop();
            break;
        case NEXT:
        case NOT:
            result.append(formula.getContent() + "--");
            indent.push(false);
            toString(formula.getSub1(), indent, result);
            indent.pop();
            break;
        case FALSE:
        case TRUE:
            result.append(formula.getContent());
            break;
        case PROPOSITION:
            result.append(formula.getName());
        }
    }

    private static final void addIndent(Stack<Boolean> indent,
            StringBuilder result) {
        for (int i = 0; i < indent.size(); i++) {
            boolean b = indent.get(i);
            result.append(b ? (i == indent.size() - 1 ? " +-" : " | ") : "   ");
        }
    }

    private final static boolean DEBUG = false;

    private static final int P_ALL = 0;
    private static final int P_IMPLIES = 1;
    private static final int P_OR = 2;
    private static final int P_AND = 3;
    private static final int P_UNTIL = 4;
    private static final int P_WUNTIL = 4;
    private static final int P_RELEASE = 5;
    private static final int P_WRELEASE = 5;
    private static final int P_NOT = 6;
    private static final int P_NEXT = 6;
    private static final int P_ALWAYS = 6;
    private static final int P_EVENTUALLY = 6;
}
