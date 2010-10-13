// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/*
 * $Id: ExprParser.java,v 1.11 2008-01-30 09:32:14 iovka Exp $
 */
package groove.util;

import groove.view.FormatException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * A class that helps parse an expression.
 * 
 * @author Arend Rensink
 * @version $Revision$
 */
public class ExprParser {

    /**
     * Constructs a parser based on the standard quoting and bracketing
     * settings. The standard settings consist of double quotes and round,
     * curly, square and angle brackets.
     */
    public ExprParser() {
        this(DEFAULT_BRACKETS);
    }

    /**
     * Constructs a parser based on given bracketing settings and the standard
     * quote characters (single and double quotes).
     */
    public ExprParser(char[]... brackets) {
        this(PLACEHOLDER, DEFAULT_QUOTE_CHARS, brackets);
    }

    /**
     * Constructs a parser based on given quoting and bracketing settings.
     */
    public ExprParser(char placeholder, char[] quoteChars, char[]... brackets) {
        for (char element : quoteChars) {
            this.quoteChars[element] = true;
        }
        for (int i = 0; i < brackets.length; i++) {
            char[] element = brackets[i];
            char open = element[0];
            this.openBrackets[open] = true;
            this.openBracketsIndexMap.put(open, i);
            char close = element[1];
            this.closeBrackets[close] = true;
            this.closeBracketsIndexMap.put(close, i);
        }
        this.placeholder = placeholder;
    }

    /**
     * Parses a given string, based on the quoting and bracketing settings of
     * this parser instance.
     * @param expr the string to be parsed
     * @return the result of the parsing; see {@link #parseExpr(String)}.
     * @see #parseExpr
     */
    public Pair<String,List<String>> parse(String expr) throws FormatException {
        // flag showing that the previous character was an escape
        boolean escaped = false;
        // flag showing that we are inside a quoted string
        boolean quoted = false;
        // quote character if quoted is true
        char quoteChar = 0;
        // current stack of brackets
        Stack<Character> bracketStack = new Stack<Character>();
        // the resulting stripped expression (with PLACEHOLDER chars)
        SimpleStringBuilder strippedExpr =
            new SimpleStringBuilder(expr.length());
        // the list of replacements so far
        List<String> replacements = new LinkedList<String>();
        // the string currently being built
        SimpleStringBuilder current = strippedExpr;
        for (int i = 0; i < expr.length(); i++) {
            char nextChar = expr.charAt(i);
            Character nextCharObject = nextChar;
            if (escaped) {
                current.add(nextChar);
                escaped = false;
            } else if (nextChar == ESCAPE_CHAR) {
                current.add(nextChar);
                escaped = true;
            } else if (quoted) {
                current.add(nextChar);
                quoted = nextChar != quoteChar;
                if (!quoted && bracketStack.isEmpty()) {
                    strippedExpr.add(this.placeholder);
                    replacements.add(current.toString());
                    current = strippedExpr;
                }
            } else if (this.quoteChars[nextChar]) {
                if (bracketStack.isEmpty()) {
                    current = new SimpleStringBuilder(expr.length() - i);
                }
                current.add(nextChar);
                quoted = true;
                quoteChar = nextChar;
            } else if (this.openBrackets[nextChar]) {
                // we have an opening bracket
                if (bracketStack.isEmpty()) {
                    current = new SimpleStringBuilder(expr.length() - i);
                }
                current.add(nextChar);
                bracketStack.push(nextChar);
            } else if (this.closeBrackets[nextChar]) {
                // we have a closing bracket; see if it is expected
                if (bracketStack.isEmpty()) {
                    throw new FormatException(
                        "Unbalanced brackets in expression '%s': '%c' is not opened",
                        expr, nextChar);
                }
                Character openBracket = bracketStack.pop();
                int openBracketIndex =
                    this.openBracketsIndexMap.get(openBracket);
                int closeBracketIndex =
                    this.closeBracketsIndexMap.get(nextCharObject);
                if (openBracketIndex != closeBracketIndex) {
                    throw new FormatException(
                        "Unbalanced brackets in expression '%s': '%c' closed by '%c'",
                        expr, openBracket, nextChar);
                }
                current.add(nextChar);
                if (bracketStack.isEmpty()) {
                    // this closes the replacement substring
                    strippedExpr.add(this.placeholder);
                    replacements.add(current.toString());
                    current = strippedExpr;
                }
            } else {
                // we have an ordinary character
                current.add(nextChar);
            }
        }
        if (escaped) {
            throw new FormatException(
                "Expression '%s' ends on escape character", expr);
        } else if (quoted) {
            throw new FormatException(
                "Unbalanced quotes in expression '%s': %c is not closed", expr,
                quoteChar);
        } else if (!bracketStack.isEmpty()) {
            throw new FormatException(
                "Unbalanced brackets in expression '%s': '%c' is not closed",
                expr, bracketStack.pop());
        }
        return new Pair<String,List<String>>(strippedExpr.toString(),
            Collections.unmodifiableList(replacements));
    }

    /**
     * Reverse operation of {@link #parse(String)}. Given a basis string
     * (corresponding to element 0 of the output array of {@link #parse(String)}
     * and an iterator (over the list at element 1 of the output array of
     * {@link #parse(String)}, returns a string from which
     * {@link #parse(String)} would have constructed that array.
     */
    public String unparse(String basis, List<String> replacements) {
        // Calculate the capacity of the result char array,
        int replacementLength = 0;
        for (String replacement : replacements) {
            replacementLength += replacement.length();
        }
        SimpleStringBuilder result =
            new SimpleStringBuilder(basis.length() + replacementLength);
        Iterator<String> replacementIter = replacements.iterator();
        for (int i = 0; i < basis.length(); i++) {
            char next = basis.charAt(i);
            if (next == this.placeholder) {
                // Append next replacement to result
                String replacement = replacementIter.next();
                for (int c = 0; c < replacement.length(); c++) {
                    result.add(replacement.charAt(c));
                }
            } else {
                result.add(next);
            }
        }
        return result.toString();
    }

    /**
     * Splits a given expression according to a string (<i>not</i> a regular
     * expression). Quoted strings and bracketed sub-expressions are treated as
     * atomic, and whitespaces are trimmed from the result. A whitespace
     * character as <tt>split</tt> expression will therefore stand for a
     * sequence of whitespaces, with at least one occurrence of the precise
     * <tt>split</tt> expression. Leading and trailing empty strings are
     * included in the result.
     * @param expr the string to be split
     * @param split the regular expression used to split the expression.
     * @return the resulting array of strings
     * @throws FormatException if <tt>expr</tt> has unbalanced brackets
     * @see String#split(String,int)
     */
    public String[] split(String expr, String split) throws FormatException {
        List<String> result = new ArrayList<String>();
        // Parse the expression first, so only non-quoted spaces are used to split
        Pair<String,List<String>> parseResult = parse(expr);
        String parseExpr = parseResult.first();
        Iterator<String> replacements = parseResult.second().iterator();
        // go through the parsed expression
        SimpleStringBuilder subResult = new SimpleStringBuilder(expr.length());
        for (int i = 0; i < parseExpr.length(); i++) {
            char next = parseExpr.charAt(i);
            if (next == split.charAt(0) && parseExpr.startsWith(split, i)) {
                result.add(subResult.toString());
                subResult.clear();
            } else if (next == this.placeholder) {
                // append the next replacement to the subresult
                String replacement = replacements.next();
                for (int c = 0; c < replacement.length(); c++) {
                    subResult.add(replacement.charAt(c));
                }
            } else if (!subResult.isEmpty() || !Character.isWhitespace(next)) {
                subResult.add(next);
            }
        }
        // process the last subresult
        result.add(subResult.toString());
        return result.toArray(new String[result.size()]);
    }

    /**
     * Splits a given expression into operands according to a given operator,
     * given as a string (<i>not</i> a regular expression) and positioning
     * information (infix, prefix or postfix) Quoted strings and bracketed
     * sub-expressions are treated as atomic. Returns <tt>null</tt> if the
     * operator is a prefix or postfix operator and does not occur in the
     * correct position; raises an <code>ExprFormatException</code> if there are
     * empty or unbalanced operands.
     * @param expr the string to be split
     * @param oper the operator; note that it is <i>not</i> a regular expression
     * @param position the positioning property of the operator; one of
     *        <tt>INFIX</tt>, <tt>PREFIX</tt> or <tt>POSTFIX</tt>
     * @return the resulting array of strings
     * @throws FormatException if <tt>expr</tt> has unbalanced brackets, or the
     *         positioning of the operator is not as required
     */
    public String[] split(String expr, String oper, int position)
        throws FormatException {
        expr = expr.trim();
        switch (position) {
        case INFIX_POSITION:
            String[] result = split(expr, oper);
            if (result.length == 1) {
                if (result[0].length() == 0) {
                    return new String[0];
                } else {
                    return result;
                }
            }
            for (int i = 0; i < result.length; i++) {
                if (result[i].length() == 0) {
                    throw new FormatException("Infix operator '" + oper
                        + "' has empty operand nr. " + i + " in \"" + expr
                        + "\"");
                }
            }
            return result;
        case PREFIX_POSITION:
            Pair<String,List<String>> parsedExpr = parse(expr);
            String parsedBasis = parsedExpr.first();
            List<String> replacements = parsedExpr.second();
            int operIndex = parsedBasis.indexOf(oper);
            if (operIndex < 0) {
                return null;
            } else if (operIndex > 0) {
                throw new FormatException("Prefix operator '" + oper
                    + "' occurs in wrong position in \"" + expr + "\"");
            } else if (expr.length() == oper.length()) {
                throw new FormatException("Prefix operator '" + oper
                    + "' has empty operand in \"" + expr + "\"");
            } else {
                return new String[] {unparse(
                    parsedBasis.substring(oper.length()), replacements)};
            }
        case POSTFIX_POSITION:
            parsedExpr = parse(expr);
            parsedBasis = parsedExpr.first();
            replacements = parsedExpr.second();
            operIndex = parsedBasis.lastIndexOf(oper);
            if (operIndex < 0) {
                return null;
            } else if (operIndex < parsedBasis.length() - oper.length()) {
                throw new FormatException("Postfix operator '" + oper
                    + "' occurs in wrong position in \"" + expr + "\"");
            } else if (operIndex == 0) {
                throw new FormatException("Postfix operator '" + oper
                    + "' has empty operand in \"" + expr + "\"");
            } else {
                return new String[] {unparse(
                    parsedBasis.substring(0, operIndex), replacements)};
            }
        default:
            // this case should not occur
            throw new IllegalArgumentException(
                "Illegal position parameter value '" + position + "'");
        }
    }

    /**
     * A bitset of quote characters.
     */
    private final boolean[] quoteChars = new boolean[0xFF];
    /**
     * A bitset of open bracket characters.
     */
    private final boolean[] openBrackets = new boolean[0xFF];
    /**
     * A bitset of close bracket characters.
     */
    private final boolean[] closeBrackets = new boolean[0xFF];
    /**
     * A map from open bracket characters to indices. The corresponding closing bracket
     * character is at the same index of <tt>closeBrackets</tt>.
     */
    private final Map<Character,Integer> openBracketsIndexMap =
        new LinkedHashMap<Character,Integer>();
    /**
     * A map of closing bracket characters to indices. The corresponding opening bracket
     * character is at the same index of <tt>openBrackets</tt>.
     */
    private final Map<Character,Integer> closeBracketsIndexMap =
        new LinkedHashMap<Character,Integer>();
    /**
     * The character to use as a placeholder in the parse result of this parser.
     */
    private final char placeholder;

    /**
     * Parses a given string by recognising quoted and bracketed substrings. The
     * quote characters are<tt>'</tt> and <tt>"</tt>; recognised bracket pairs are
     * <tt>()</tt>, <tt>{}</tt>, <tt>&lt;&gt;</tt> and <tt>[]</tt>. Within
     * quoted strings, escape codes are interpreted as in Java. Brackets are
     * required to be properly nested. The result is given as a pair of objects:
     * the first is the string with all quoted and bracketed substrings replaced
     * by the character <tt>PLACEHOLDER</tt>, and the second is a list of the
     * replaced substrings, in the order in which they appeared in the original
     * string.
     * @param expr the string to be parsed
     * @return a pair of objects: the first is the string with all quoted and
     *         bracketed substrings replaced by the character
     *         {@link #PLACEHOLDER}, and the second is a list of the replaced
     *         substrings, in the order in which they appeared in the original
     *         string.
     */
    static public Pair<String,List<String>> parseExpr(String expr)
        throws FormatException {

        ExprParser p = ExprParser.prototype;

        return p.parse(expr);
    }

    /**
     * Puts together a string from a base string, which may contain
     * {@link #PLACEHOLDER} characters, and a list of replacements for the
     * {@link #PLACEHOLDER}s. This is the inverse operation of
     * {@link #parseExpr(String)}.
     */
    static public String unparseExpr(String basis, List<String> replacements) {
        return prototype.unparse(basis, replacements);
    }

    /**
     * Tests if {@link #parseExpr(String)} does not throw an exception.
     * @param expr the expression to be tested
     * @return <tt>true</tt> if <tt>parseExpr(expr)</tt> does not throw an
     *         exception.
     * @see #parseExpr(String)
     */
    static public boolean isParsable(String expr) {
        try {
            parseExpr(expr);
            return true;
        } catch (FormatException exc) {
            return false;
        }
    }

    /**
     * Turns back the result of a {@link #parseExpr(String)}-action to a string.
     * @param main the result of string parsing; for the format see
     *        {@link #parseExpr(String)}.
     * @return the string from which <tt>parsedString</tt> was originally
     *         created; or <tt>null</tt> if <tt>parsedString</tt> is improperly
     *         formatted
     */
    static public String toString(String main, List<String> args) {
        StringBuffer result = new StringBuffer();
        int placeHolderCount = 0;
        for (int c = 0; c < main.length(); c++) {
            char nextChar = main.charAt(c);
            if (nextChar == PLACEHOLDER) {
                if (placeHolderCount > args.size()) {
                    return null;
                } else {
                    result.append(args.get(placeHolderCount));
                    placeHolderCount++;
                }
            } else {
                result.append(nextChar);
            }
        }
        return result.toString();
    }

    /**
     * Splits a given expression according to a string (note: <i>not</i> a
     * regular expression). Quoted strings and bracketed sub-expressions are
     * treated as atomic, and whitespaces are trimmed from the result. A
     * whitespace character as <tt>split</tt> expression will therefore stand
     * for a sequence of whitespaces, with at least one occurrence of the
     * precise <tt>split</tt> expression. Convenience method; abbreviates
     * <tt>new ExprParser().split(expr,split)</tt>.
     * @see #split(String,String)
     */
    static public String[] splitExpr(String expr, String split)
        throws FormatException {
        return prototype.split(expr, split);
    }

    /**
     * Splits a given expression according to a given operator and positioning
     * information (infix, prefix or postfix) Quoted strings and bracketed
     * sub-expressions are treated as atomic. Convenience method; abbreviates
     * <tt>new ExprParser().split(expr,split,position)</tt>.
     * @see #split(String,String,int)
     */
    static public String[] splitExpr(String expr, String split, int position)
        throws FormatException {
        return prototype.split(expr, split, position);
    }

    /**
     * Removes a given outermost bracket pair from a given expression, if the
     * bracket pair is in fact there. Returns <code>null</code> if the
     * expression was not bracketed in the first place, or if brackets are
     * improperly balanced. This is tested by calling {@link #parseExpr(String)}
     * on the expression.
     * @param expr the expression to be trimmed
     * @param open the opening bracket
     * @param close the closing bracket
     * @return the trimmed string
     * @throws FormatException if the string could not be correctly parsed
     */
    static public String toTrimmed(String expr, char open, char close)
        throws FormatException {
        Pair<String,List<String>> parseResult =
            new ExprParser(new char[] {open, close}).parse(expr);
        if (parseResult.first().length() != 1
            || parseResult.second().get(0).charAt(0) != open) {
            throw new FormatException(
                "Expression %s not surrounded by bracket pair %c%c", expr,
                open, close);
        } else {
            return expr.substring(1, expr.length() - 1);
        }
    }

    /**
     * Converts an ordinary string to a regular expression that matches it, by
     * escaping all non-word characters.
     */
    static public String toRegExpr(String expr) {
        return expr.replaceAll("(\\W)", "\\\\$1");
    }

    /**
     * Converts a regular expression to a non-regular expression, by stripping
     * away all characters with special meanings (essentially, all escaped word
     * charaters and all non-escaped non-word characters).
     */
    static public String toNormExpr(String regExpr) {
        String result =
            regExpr.replaceAll("\\\\\\\\", "" + "\\\\" + PLACEHOLDER);
        result = result.replaceAll("^\\W", "");
        result = result.replaceAll("([^\\\\])\\W", "$1");
        result = result.replaceAll("\\\\(\\W)", "$1");
        result = result.replace(PLACEHOLDER, '\\');
        return result;
    }

    /**
     * Transforms a string by escaping all characters from of a given set.
     * Escaping a character implies putting {@link #ESCAPE_CHAR} in front. The
     * {@link #ESCAPE_CHAR} itself is by default also escaped.
     * @param string the original string
     * @param specialChars the characters to be escaped
     * @return the resulting string
     */
    static public String toEscaped(String string, Set<Character> specialChars) {
        StringBuffer result = new StringBuffer();
        for (char c : string.toCharArray()) {
            // insert an ESCAPE in front of quotes or ESCAPES
            if (c == ESCAPE_CHAR || specialChars.contains(c)) {
                result.append(ESCAPE_CHAR);
            }
            result.append(c);
        }
        return result.toString();
    }

    /**
     * Transforms a string by putting quote characters around it, and escaping
     * all quote characters within the string, as well as the escape character.
     * @param string the original string
     * @param quote the quote character to be used
     * @return the quoted string
     */
    static public String toQuoted(String string, char quote) {
        StringBuffer result = new StringBuffer();
        result.append(quote);
        result.append(toEscaped(string, Collections.singleton(quote)));
        result.append(quote);
        return result.toString();
    }

    /**
     * Transforms a string by removing quote characters around it, if there are
     * any, and unescaping all characters within the string. The original string
     * does not need to be quoted.
     * - boolean test if <code>true</code>, the method throws an exception upon
     *           finding a format error; otherwise, it returns <code>null</code
     * @param string the original string
     * @param quote the quote character to be used
     * @return the unquoted string (which may equal the original, if there are
     *         no quotes or escaped characters in the string), or
     *         <code>null</code> if <code>test</code> is <code>false</code> and
     *         there is a format error.
     * @throws FormatException if there are unescaped quotes at any position
     *         except the first or last, or if there are no matching begin or
     *         end quotes
     */
    static public String toUnquoted(String string, char quote)
        throws FormatException {
        boolean startsWithQuote =
            !string.isEmpty() && string.charAt(0) == quote;
        boolean endsWithQuote = false;
        char[] content = string.toCharArray();
        StringBuffer result = new StringBuffer();
        // flag indicating that the previous character was an ESCAPE
        boolean escaped = false;
        // number of (unescaped) quotes encountered
        int quoteCount = 0;
        for (char c : content) {
            if (escaped) {
                result.append(c);
                escaped = false;
                endsWithQuote = false;
            } else {
                escaped = c == ESCAPE_CHAR;
                if (!escaped) {
                    result.append(c);
                    if (c == quote) {
                        endsWithQuote = true;
                        quoteCount++;
                    } else {
                        endsWithQuote = false;
                    }
                }
            }
        }
        // check for errors
        if (escaped) {
            throw new FormatException("String %s ends on escape character");
        } else if (startsWithQuote ? !(endsWithQuote && quoteCount == 2)
                : quoteCount != 0) {
            throw new FormatException("Unbalanced quotes in %s", string);
        } else if (startsWithQuote) {
            return result.substring(1, result.length() - 1);
        } else {
            return result.toString();
        }
    }

    /** Main method used to test the class. Call without parameters. */
    static public void main(String[] args) {
        System.out.println("Empty string: " + "".substring(0, 0));
        if (args.length == 0) {
            System.out.println("Regular expression tests");
            System.out.println("------- ---------- -----");
            testRegExpr("a(3)");
            testRegExpr("$ \\ii*2");
            testRegExpr("\\\\\\a \\");
            System.out.println();

            System.out.println("String quotation tests");
            System.out.println("----- --------- -----");
            testQuoteString("a\"3\"");
            testQuoteString("\"a \\\"stress\\\" test\"");
            testQuoteString("a\\\"");
            System.out.println();

            System.out.println("Parsing tests");
            System.out.println("------- -----");
            testParse("");
            testParse("()");
            testParse("(<)");
            testParse("(\\<)");
            testParse("()')");
            testParse("()\\)");
            testParse("(\\')");
            testParse("()')'");
            testParse("a'b+c");
            testParse("a()b<(c)>");
            testParse("{a()b<(c)>}");
            testParse("\"{a()b<(c)>\"");
            testParse("\"(\"(a+b)");
            testParse("(\"(\"a+b)");
            testParse("\"");
            testParse("(");
            testParse(")");
            testParse("\\'");
            testParse("{a()b<(c)}");
            System.out.println();

            System.out.println("Splitting tests");
            System.out.println("--------- -----");
            testSplit("\"a \\\"stress\\\" test\"", ",");
            testSplit("a|(b.c)*", "|");
            testSplit("a|(b.c)*", "*");
            testSplit("a|(b.c)*", ".");
            //
            testSplit("a|(b.c)*", "|", INFIX_POSITION);
            testSplit("a|(b.c)*", "|", POSTFIX_POSITION);
            testSplit("a|(b.c)*", "*", INFIX_POSITION);
            testSplit("a|(b.c)*", "*", POSTFIX_POSITION);
            testSplit("a|(b.c)*", "a", PREFIX_POSITION);
            testSplit("a|(b.c)*", "a", POSTFIX_POSITION);
            //
            testTrim("(b.c ) ", '(', ')');
            testTrim("a|(b.c)*", '(', ')');
            testTrim(" (b.c)* ", '(', ')');
        } else {
            for (String element : args) {
                testParse(element);
            }
        }
    }

    static private void testRegExpr(String expr) {
        System.out.print("Expression " + expr);
        expr = toRegExpr(expr);
        System.out.print(". To regular: " + expr);
        expr = toNormExpr(expr);
        System.out.println(". To normal: " + expr);
    }

    static private void testQuoteString(String string) {
        System.out.print("String " + string);
        string = toQuoted(string, DOUBLE_QUOTE_CHAR);
        System.out.print(". To quoted: " + string);
        try {
            string = toUnquoted(string, DOUBLE_QUOTE_CHAR);
            System.out.println(". To unquoted: " + string);
        } catch (FormatException e) {
            System.out.println(". Error: " + e);
        }
    }

    static private void testParse(String expr) {
        try {
            System.out.println("Parsing: " + expr);
            Pair<String,?> result = parseExpr(expr);
            System.out.println("Result: " + result.first()
                + " with replacements " + result.second());
        } catch (FormatException exc) {
            System.out.println("Error: " + exc.getMessage());
        }
        System.out.println();
    }

    static private void testSplit(String expr, String split) {
        try {
            System.out.println("Splitting: \"" + expr + "\" according to \""
                + split + "\"");
            Object[] result = splitExpr(expr, split);
            if (result == null) {
                System.out.println("null");
            } else {
                System.out.print("[\"");
                for (int i = 0; i < result.length; i++) {
                    System.out.print(result[i]);
                    if (i < result.length - 1) {
                        System.out.print("\", \"");
                    }
                }
                System.out.println("\"]");
            }
        } catch (FormatException exc) {
            System.out.println("Error: " + exc.getMessage());
        }
        System.out.println();
    }

    static private void testSplit(String expr, String oper, int position) {
        try {
            System.out.print("Splitting: \"" + expr + "\" according to ");
            System.out.print(position == INFIX_POSITION ? "infix"
                    : position == PREFIX_POSITION ? "prefix" : "postfix");
            System.out.println(" operator \"" + oper + "\"");
            String[] result = splitExpr(expr, oper, position);
            System.out.print("Result: ");
            if (result == null) {
                System.out.println("null");
            } else {
                System.out.print("[\"");
                for (int i = 0; i < result.length; i++) {
                    System.out.print(result[i]);
                    if (i < result.length - 1) {
                        System.out.print("\", \"");
                    }
                }
                System.out.println("\"]");
            }
        } catch (FormatException exc) {
            System.out.println("Error: " + exc.getMessage());
        }
        System.out.println();
    }

    static private void testTrim(String expr, char open, char close) {
        System.out.println("Trimming bracket pair '" + open + "', '" + close
            + "' from \"" + expr + "\"");
        String result;
        try {
            result = toTrimmed(expr.trim(), open, close);
            System.out.printf("Result: \"%s\"%n", result);
        } catch (FormatException e) {
            System.out.printf("Error: \"%s\"%n", e);
        }
    }

    /** Tests if a character may occur in a wildcard identifier. */
    static public boolean isIdentifierChar(char c) {
        return Character.isLetterOrDigit(c) || IDENTIFIER_CHARS.indexOf(c) >= 0;
    }

    /**
     * Tests if a character may occur as the first character in a wildcard
     * identifier.
     */
    static public boolean isIdentifierStartChar(char c) {
        return Character.isLetter(c) || IDENTIFIER_START_CHARS.indexOf(c) >= 0;
    }

    /**
     * Tests whether a given text can serve as a wildcard identifier. This
     * implementation returns <code>true</code> if <code>text</code> is
     * non-empty, starts with a correct character (according to
     * {@link #isIdentifierStartChar(char)}), and contains only characters
     * satisfying {@link #isIdentifierChar(char)}.
     * @param text the text to be tested
     * @return <tt>true</tt> if the text does not contain any special characters
     */
    static public boolean isIdentifier(String text) {
        if (text.length() == 0) {
            return false;
        }
        for (int i = 0; i < text.length(); i++) {
            char nextChar = text.charAt(i);
            if (!(i == 0 ? isIdentifierStartChar(nextChar)
                    : isIdentifierChar(nextChar))) {
                return false;
            }
        }
        return true;
    }

    /** The single quote character, to control parsing. */
    static public final char SINGLE_QUOTE_CHAR = '\'';
    /** The double quote character, to control parsing. */
    static public final char DOUBLE_QUOTE_CHAR = '"';
    /** The escape character commonly used. */
    static public final char ESCAPE_CHAR = '\\';
    /**
     * Left parenthesis character used for grouping regular (sub)expressions.
     */
    static public final char LPAR_CHAR = '(';
    /**
     * Right parenthesis character used for grouping regular (sub)expressions.
     */
    static public final char RPAR_CHAR = ')';
    /**
     * Left bracket character allowed as atom delimiter
     */
    static public final char LANGLE_CHAR = '<';
    /**
     * Right bracket character allowed as atom delimiter
     */
    static public final char RANGLE_CHAR = '>';
    /**
     * Left bracket character allowed as atom delimiter
     */
    static public final char LCURLY_CHAR = '{';
    /**
     * Right bracket character allowed as atom delimiter
     */
    static public final char RCURLY_CHAR = '}';

    /** Pair of round brackets, to control parsing. */
    static public final char[] ROUND_BRACKETS = {LPAR_CHAR, RPAR_CHAR};
    /** Pair of curly brackets, to control parsing. */
    static private final char[] CURLY_BRACKETS = {LCURLY_CHAR, RCURLY_CHAR};
    /** Pair of square brackets, to control parsing. */
    static private final char[] SQUARE_BRACKETS = {'[', ']'};
    /** Pair of angle brackets, to control parsing. */
    static private final char[] ANGLE_BRACKETS = {LANGLE_CHAR, RANGLE_CHAR};

    /**
     * Positioning value for an infix operator.
     * @see #split(String,String,int)
     */
    static public final int INFIX_POSITION = 0;
    /**
     * Positioning value for an infix operator.
     * @see #split(String,String,int)
     */
    static public final int PREFIX_POSITION = 1;
    /**
     * Positioning value for an infix operator.
     * @see #split(String,String,int)
     */
    static public final int POSTFIX_POSITION = 2;

    /**
     * Array of default quote characters, containing the single and double
     * quotes ({@link #DOUBLE_QUOTE_CHAR} and {@link #SINGLE_QUOTE_CHAR}).
     */
    static private final char[] DEFAULT_QUOTE_CHARS = {DOUBLE_QUOTE_CHAR,
        SINGLE_QUOTE_CHAR};
    /**
     * Array of default bracket pairs: {@link #ROUND_BRACKETS},
     * {@link #ANGLE_BRACKETS}, {@link #CURLY_BRACKETS} and
     * {@link #SQUARE_BRACKETS}.
     */
    static private final char[][] DEFAULT_BRACKETS = {ROUND_BRACKETS,
        ANGLE_BRACKETS, CURLY_BRACKETS, SQUARE_BRACKETS};
    /** The default character to use as a placeholder in the parse result. */
    static public final char PLACEHOLDER = '\uFFFF';

    /**
     * The characters allowed in a wildcard identifier, apart from letters and
     * digits.
     * @see ExprParser#isIdentifier(String)
     */
    static public final String IDENTIFIER_CHARS = "_$";
    /**
     * The characters allowed at the start of a wildcard identifier, apart from
     * letters.
     * @see ExprParser#isIdentifier(String)
     */
    static public final String IDENTIFIER_START_CHARS = "_";

    /** Prototype parser, used to evaluate the static methods on. */
    static private final ExprParser prototype = new ExprParser();

    /** Class wrapping a fixed length char array
     * with functionality to add chars and convert the result into a string. 
     * @author Arend Rensink
     * @version $Revision $
     */
    private static class SimpleStringBuilder {
        /** Constructs a builder with a given (fixed) length. */
        public SimpleStringBuilder(int capacity) {
            this.sequence = new char[capacity];
        }

        /** Appends a char to the builder. */
        public void add(char next) {
            this.sequence[this.length] = next;
            this.length++;
        }

        @Override
        public String toString() {
            return new String(this.sequence, 0, this.length);
        }

        /** Indicates if the sequence is currently empty. */
        public boolean isEmpty() {
            return this.length == 0;
        }

        /** Resets the sequence to length 0. */
        public void clear() {
            this.length = 0;
        }

        private final char[] sequence;
        private int length;
    }
}
