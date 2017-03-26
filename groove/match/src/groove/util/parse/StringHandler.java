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
 * $Id$
 */
package groove.util.parse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import groove.util.Pair;

/**
 * A class that helps parse an expression.
 *
 * @author Arend Rensink
 * @version $Revision$
 */
public class StringHandler {

    /**
     * Constructs a parser based on the standard quoting and bracketing
     * settings. The standard settings consist of double quotes and round,
     * curly, square and angle brackets.
     */
    public StringHandler() {
        this(DEFAULT_BRACKETS);
    }

    /**
     * Constructs a parser based on given bracketing settings and the standard
     * quote characters (single and double quotes).
     * @param quoteChars string of quote characters
     * @param brackets a sequence of two-element strings,
     * consisting of opening and closing brackets
     */
    public StringHandler(String quoteChars, String... brackets) {
        this(PLACEHOLDER, quoteChars.toCharArray(), toCharArray(brackets));
    }

    /**
     * Constructs a parser based on given bracketing settings and the standard
     * quote characters (single and double quotes).
     * @param placeholder character used as placeholder in {@link #parse} and {@link #unparse}
     * @param quoteChars string of quote characters
     * @param brackets a sequence of two-element strings,
     * consisting of opening and closing brackets
     */
    public StringHandler(char placeholder, String quoteChars, String... brackets) {
        this(placeholder, quoteChars.toCharArray(), toCharArray(brackets));
    }

    /** Converts an array of strings to an array of character arrays. */
    private static char[][] toCharArray(String[] values) {
        char[][] result = new char[values.length][];
        int i = 0;
        for (String value : values) {
            result[i] = value.toCharArray();
            i++;
        }
        return result;
    }

    /**
     * Constructs a parser based on given bracketing settings and the standard
     * quote characters (single and double quotes).
     * @param brackets a sequence of two-element character arrays,
     * containing opening and closing brackets
     */
    public StringHandler(char[]... brackets) {
        this(PLACEHOLDER, DEFAULT_QUOTE_CHARS, brackets);
    }

    /**
     * Constructs a parser based on given quoting and bracketing settings.
     * @param placeholder character used as placeholder in {@link #parse} and {@link #unparse}
     * @param quoteChars array of quote characters
     * @param brackets a sequence of two-element character arrays,
     * containing opening and closing brackets
     */
    public StringHandler(char placeholder, char[] quoteChars, char[]... brackets) {
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
     * @throws FormatException if {@code expr} has unbalanced quotes or brackets
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
        Stack<Character> bracketStack = new Stack<>();
        // the resulting stripped expression (with PLACEHOLDER chars)
        SimpleStringBuilder strippedExpr = new SimpleStringBuilder(expr.length());
        // the list of replacements so far
        List<String> replacements = new LinkedList<>();
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
                        "Unbalanced brackets in expression '%s': '%c' is not opened", expr,
                        nextChar);
                }
                Character openBracket = bracketStack.pop();
                int openBracketIndex = this.openBracketsIndexMap.get(openBracket);
                int closeBracketIndex = this.closeBracketsIndexMap.get(nextCharObject);
                if (openBracketIndex != closeBracketIndex) {
                    throw new FormatException(
                        "Unbalanced brackets in expression '%s': '%c' closed by '%c'", expr,
                        openBracket, nextChar);
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
            throw new FormatException("Expression '%s' ends on escape character", expr);
        } else if (quoted) {
            throw new FormatException("Unbalanced quotes in expression '%s': %c is not closed",
                expr, quoteChar);
        } else if (!bracketStack.isEmpty()) {
            throw new FormatException("Unbalanced brackets in expression '%s': '%c' is not closed",
                expr, bracketStack.pop());
        }
        return new Pair<>(strippedExpr.toString(),
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
        SimpleStringBuilder result = new SimpleStringBuilder(basis.length() + replacementLength);
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
     * @param split the substring used to split the expression.
     * @return the resulting array of strings
     * @throws FormatException if {@code expr} has unbalanced quotes or brackets
     * @see String#split(String,int)
     */
    public String[] split(String expr, String split) throws FormatException {
        List<String> result = new ArrayList<>();
        // Parse the expression first, so only non-quoted substrings are used to split
        Pair<String,List<String>> parseResult = parse(expr);
        String parseExpr = parseResult.one();
        Iterator<String> replacements = parseResult.two().iterator();
        // go through the parsed expression
        SimpleStringBuilder subResult = new SimpleStringBuilder(expr.length());
        for (int i = 0; i < parseExpr.length(); i++) {
            char next = parseExpr.charAt(i);
            if (next == split.charAt(0) && parseExpr.startsWith(split, i)) {
                result.add(subResult.toString());
                subResult.clear();
                i += split.length() - 1;
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
     * @throws FormatException if <tt>expr</tt> has unbalanced quotes or brackets, or the
     *         positioning of the operator is not as required
     */
    public String[] split(String expr, String oper, int position) throws FormatException {
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
                    throw new FormatException("Infix operator '" + oper + "' has empty operand nr. "
                        + i + " in \"" + expr + "\"");
                }
            }
            return result;
        case PREFIX_POSITION:
            Pair<String,List<String>> parsedExpr = parse(expr);
            String parsedBasis = parsedExpr.one();
            List<String> replacements = parsedExpr.two();
            int operIndex = parsedBasis.indexOf(oper);
            if (operIndex < 0) {
                return null;
            } else if (operIndex > 0) {
                throw new FormatException(
                    "Prefix operator '" + oper + "' occurs in wrong position in \"" + expr + "\"");
            } else if (expr.length() == oper.length()) {
                throw new FormatException(
                    "Prefix operator '" + oper + "' has empty operand in \"" + expr + "\"");
            } else {
                return new String[] {unparse(parsedBasis.substring(oper.length()), replacements)};
            }
        case POSTFIX_POSITION:
            parsedExpr = parse(expr);
            parsedBasis = parsedExpr.one();
            replacements = parsedExpr.two();
            operIndex = parsedBasis.lastIndexOf(oper);
            if (operIndex < 0) {
                return null;
            } else if (operIndex < parsedBasis.length() - oper.length()) {
                throw new FormatException(
                    "Postfix operator '" + oper + "' occurs in wrong position in \"" + expr + "\"");
            } else if (operIndex == 0) {
                throw new FormatException(
                    "Postfix operator '" + oper + "' has empty operand in \"" + expr + "\"");
            } else {
                return new String[] {unparse(parsedBasis.substring(0, operIndex), replacements)};
            }
        default:
            // this case should not occur
            throw new IllegalArgumentException(
                "Illegal position parameter value '" + position + "'");
        }
    }

    /**
     * Fills out a string to a given length by padding it with white space on
     * the left or right. Has no effect if the string is already longer than the
     * desired length.
     * @param text the string to be padded
     * @param length the desired length
     * @param right <tt>true</tt> if the space should be added on the right
     * @return A new string, consisting of <tt>text</tt> preceded or followed by
     *         spaces, up to minimum length <tt>length</tt>
     */
    static public String pad(String text, int length, boolean right) {
        StringBuffer result = new StringBuffer(text);
        while (result.length() < length) {
            if (right) {
                result.append(' ');
            } else {
                result.insert(0, ' ');
            }
        }
        return result.toString();
    }

    /**
     * Fills out a string to a given length by padding it with white space on
     * the right. Has no effect if the string is already longer than the desired
     * length.
     * @param text the string to be padded
     * @param length the desired length
     * @return A new string, with <tt>text</tt> as prefix, followed by spaces,
     *         up to minimum length <tt>length</tt>
     */
    static public String pad(String text, int length) {
        return pad(text, length, true);
    }

    /**
     * Turns a camel-case string into a space-separated string.
     * The first character is capitalised in any case; next
     * words are capitalised optionally.
     * @param input the (non-{@code null}) input string, in camel case
     * @param caps if {@code true}, all words (not just the first) are capitalised
     * @return a converted string
     */
    public static String unCamel(String input, boolean caps) {
        StringBuilder result = new StringBuilder(input);
        int ix = 0;
        boolean wasLower = true;
        while (ix < result.length()) {
            char c = result.charAt(ix);
            boolean isLower = Character.isLowerCase(c);
            boolean atStart = ix == 0;
            if (atStart || wasLower && !isLower) {
                if (!atStart) {
                    result.insert(ix, ' ');
                    ix++;
                }
                // determine if next character should be upper or lower case
                boolean toUpper = atStart || caps;
                if (!toUpper) {
                    toUpper =
                        ix < result.length() - 1 && Character.isUpperCase(result.charAt(ix + 1));
                }
                result.setCharAt(ix, toUpper ? Character.toUpperCase(c) : Character.toLowerCase(c));
            }
            wasLower = isLower;
            ix++;
        }
        return result.toString();
    }

    /**
     * Converts a space- or underscore-separated string into camel case
     * @param input the (non-{@code null}) input string; should
     * consist of alphanumeric characters separated by (but not beginning with)
     * spaces or underscores
     * @return a converted string
     */
    public static String toCamel(String input) {
        StringBuilder result = new StringBuilder(input);
        int ix = 0;
        boolean wasSep = false;
        while (ix < result.length()) {
            char c = result.charAt(ix);
            boolean isSep = c == ' ' || c == '_';
            if (isSep) {
                result.delete(ix, ix + 1);
            } else {
                assert Character.isLetterOrDigit(c);
                c = wasSep ? Character.toUpperCase(c) : Character.toLowerCase(c);
                result.setCharAt(ix, c);
                ix++;
            }
            wasSep = isSep;
        }
        return result.toString();
    }

    /**
     * Converts the initial character of a given input string to uppercase.
     * @param input the input string
     */
    public static String toUpper(String input) {
        if (!Character.isLowerCase(input.charAt(0))) {
            return input;
        } else {
            StringBuilder result = new StringBuilder(input);
            if (result.length() > 0) {
                char c = result.charAt(0);
                c = Character.toUpperCase(c);
                result.setCharAt(0, c);
            }
            return result.toString();
        }
    }

    /**
     * Converts the initial character of a given input string to lowercase.
     * @param input the input string
     */
    public static String toLower(String input) {
        if (!Character.isUpperCase(input.charAt(0))) {
            return input;
        } else {
            StringBuilder result = new StringBuilder(input);
            if (result.length() > 0) {
                char c = result.charAt(0);
                c = Character.toLowerCase(c);
                result.setCharAt(0, c);
            }
            return result.toString();
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
        new LinkedHashMap<>();
    /**
     * A map of closing bracket characters to indices. The corresponding opening bracket
     * character is at the same index of <tt>openBrackets</tt>.
     */
    private final Map<Character,Integer> closeBracketsIndexMap =
        new LinkedHashMap<>();
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
    static public Pair<String,List<String>> parseExpr(String expr) throws FormatException {

        StringHandler p = StringHandler.prototype;

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
    static public String[] splitExpr(String expr, String split) throws FormatException {
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
    static public String toTrimmed(String expr, char open, char close) throws FormatException {
        Pair<String,List<String>> parseResult =
            new StringHandler(new char[] {open, close}).parse(expr);
        if (parseResult.one().length() != 1 || parseResult.two().get(0).charAt(0) != open) {
            throw new FormatException("Expression %s not surrounded by bracket pair %c%c", expr,
                open, close);
        } else {
            return expr.substring(1, expr.length() - 1);
        }
    }

    /**
     * Transforms a string by escaping all characters from of a given set.
     * Escaping a character implies putting {@link #ESCAPE_CHAR} in front.
     * @param string the original string
     * @param specialChars the characters to be escaped
     * @return the resulting string
     */
    static public String toEscaped(String string, Set<Character> specialChars) {
        StringBuffer result = new StringBuffer();
        for (char c : string.toCharArray()) {
            // insert an ESCAPE in front of quotes or ESCAPES
            if (specialChars.contains(c)) {
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
     * Transforms a string by removing quote characters around it.
     * This calls {@link #toUnquoted(String, char)} after discovering the
     * quote character, which has to be the first character of the string.
     * @throws FormatException if {@link #toUnquoted(String, char)} does so,
     * or {@code string} does not start with a quote character
     */
    static public String toUnquoted(String string) throws FormatException {
        if (string.isEmpty()) {
            throw new FormatException("Can't unquote empty string");
        }
        char quote = string.charAt(0);
        if (quote != SINGLE_QUOTE_CHAR && quote != DOUBLE_QUOTE_CHAR) {
            throw new FormatException("%s is not quoted", string);
        }
        return toUnquoted(string, quote);
    }

    /**
     * Transforms a string by removing quote characters around it, if there are
     * any, and unescaping all quote characters within the string (using '\' as
     * escape character). No other characters are unescaped, except "\\" occurring
     * at the end of the string (just before the closing quote).
     * Hence
     * <li><code>'line'</code> is converted to <code>line</code></li>
     * <li><code>'\'lin\'e'</code> is converted to <code>'lin'e</code></li>
     * <li><code>'\li\\ne\''</code> is converted to <code>\li\\ne'</code></li>
     * <li><code>'li\'ne\\'</code> is converted to <code>li'ne\</code></li>
     * </ul>The original string does not need to be quoted; if the first character
     * is not a quote character, the string is treated the same as if the
     * quote character were added in front and at the end.
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
    static public String toUnquoted(String string, char quote) throws FormatException {
        boolean startsWithQuote = !string.isEmpty() && string.charAt(0) == quote;
        int start = startsWithQuote ? 1 : 0;
        int end = string.length();
        // count of the number of consecutive escapes
        boolean escaped = false;
        // index of first unescaped quote
        int quoteIndex = -1;
        StringBuffer result = new StringBuffer();
        for (int i = start; quoteIndex < 0 && i < end; i++) {
            char c = string.charAt(i);
            if (c == quote) {
                if (escaped) {
                    result.append(c);
                } else {
                    quoteIndex = i;
                }
            } else if (escaped) {
                // we didn't append the escape char yet, awaiting a possible quote
                result.append(ESCAPE_CHAR);
            }
            escaped = c == ESCAPE_CHAR;
            if (!escaped && c != quote) {
                result.append(c);
            }
        }
        if (escaped) {
            // outstanding escape character
            result.append(ESCAPE_CHAR);
        }
        // check for errors
        if (startsWithQuote ? quoteIndex != end - 1 : quoteIndex >= 0) {
            throw new FormatException("Unbalanced quotes in %s", string);
        } else {
            return result.toString();
        }
    }

    /** Main method used to test the class. Call without parameters. */
    static public void main(String[] args) {
        System.out.println("Empty string: " + "".substring(0, 0));
        if (args.length == 0) {
            System.out.println("String quotation tests");
            System.out.println("------ --------- -----");
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
            System.out.println("Result: " + result.one() + " with replacements " + result.two());
        } catch (FormatException exc) {
            System.out.println("Error: " + exc.getMessage());
        }
        System.out.println();
    }

    static private void testSplit(String expr, String split) {
        try {
            System.out.println("Splitting: \"" + expr + "\" according to \"" + split + "\"");
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
        System.out
            .println("Trimming bracket pair '" + open + "', '" + close + "' from \"" + expr + "\"");
        String result;
        try {
            result = toTrimmed(expr.trim(), open, close);
            System.out.printf("Result: \"%s\"%n", result);
        } catch (FormatException e) {
            System.out.printf("Error: \"%s\"%n", e);
        }
    }

    /** Validator that allows hyphens within names. */
    public static final IdValidator ID_VALIDATOR = new IdValidator() {
        @Override
        public boolean isIdentifierPart(char c) {
            return Character.isJavaIdentifierPart(c) || c == HYPHEN;
        }

        @Override
        public boolean isIdentifierEnd(char c) {
            return Character.isJavaIdentifierPart(c);
        }

        @Override
        public boolean isIdentifierStart(char c) {
            return Character.isJavaIdentifierStart(c);
        }
    };

    /**
     * Tests whether a given string is a valid identifier.
     * Invokes {@link IdValidator#isValid(String)} on {@link #ID_VALIDATOR}.
     */
    static public boolean isIdentifier(String text) {
        return ID_VALIDATOR.isValid(text);
    }

    /** The underscore character. This is allowed as part of an identifier, as long
     * as the identifier also contains alphanumeric characters. */
    static public final char UNDER = '_';
    /** The hyphen character. This is allowed as part of an identifier. */
    static public final char HYPHEN = '-';
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
    static public final char LCURLY = '{';
    /**
     * Right bracket character allowed as atom delimiter
     */
    static public final char RCURLY = '}';

    /** Pair of round brackets, to control parsing. */
    static public final char[] ROUND_BRACKETS = {LPAR_CHAR, RPAR_CHAR};
    /** Pair of curly brackets, to control parsing. */
    static private final char[] CURLY_BRACKETS = {LCURLY, RCURLY};
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
    static private final char[] DEFAULT_QUOTE_CHARS = {DOUBLE_QUOTE_CHAR, SINGLE_QUOTE_CHAR};
    /**
     * Array of default bracket pairs: {@link #ROUND_BRACKETS},
     * {@link #ANGLE_BRACKETS}, {@link #CURLY_BRACKETS} and
     * {@link #SQUARE_BRACKETS}.
     */
    static private final char[][] DEFAULT_BRACKETS =
        {ROUND_BRACKETS, ANGLE_BRACKETS, CURLY_BRACKETS, SQUARE_BRACKETS};
    /** The default character to use as a placeholder in the parse result. */
    static public final char PLACEHOLDER = '\uFFFF';

    /**
     * The characters allowed in a wildcard identifier, apart from letters and
     * digits.
     * @see StringHandler#isIdentifier(String)
     */
    static public final String IDENTIFIER_CHARS = "_$";
    /**
     * The characters allowed at the start of a wildcard identifier, apart from
     * letters.
     * @see StringHandler#isIdentifier(String)
     */
    static public final String IDENTIFIER_START_CHARS = "_";

    /** Prototype parser, used to evaluate the static methods on. */
    static private final StringHandler prototype = new StringHandler();

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
