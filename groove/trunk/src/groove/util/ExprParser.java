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
 * $Id: ExprParser.java,v 1.7 2007-06-27 11:54:59 rensink Exp $
 */
package groove.util;

import groove.view.FormatException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * A class that helps parse an expression.
 * 
 * @author Arend Rensink
 * @version $Revision: 1.7 $
 */
public class ExprParser {
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
     * Left backet character allowed as atom delimiter
     */
    static public final char LANGLE_CHAR = '<';
    /**
     * Right bracket character allowed as atom delimiter
     */
    static public final char RANGLE_CHAR = '>';

    /** Pair of round brackets, to control parsing. */
    static public final char[] ROUND_BRACKETS = { LPAR_CHAR, RPAR_CHAR };
    /** Pair of curly brackets, to control parsing. */
    static public final char[] CURLY_BRACKETS = { '{', '}' };
    /** Pair of square brackets, to control parsing. */
    static public final char[] SQUARE_BRACKETS = { '[', ']' };
    /** Pair of angle brackets, to control parsing. */
    static public final char[] ANGLE_BRACKETS = { LANGLE_CHAR, RANGLE_CHAR };

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
     * Array of default quote characters, containing
     * the single and double quotes ({@link #DOUBLE_QUOTE_CHAR} and {@link #SINGLE_QUOTE_CHAR}).
     */
    static private final char[] DEFAULT_QUOTE_CHARS = { DOUBLE_QUOTE_CHAR, SINGLE_QUOTE_CHAR };
    /**
     * Array of default bracket pairs: {@link #ROUND_BRACKETS}, {@link #ANGLE_BRACKETS},
     * {@link #CURLY_BRACKETS} and {@link #SQUARE_BRACKETS}.
     */
    static private final char[][] DEFAULT_BRACKETS =
        { ROUND_BRACKETS, ANGLE_BRACKETS, CURLY_BRACKETS, SQUARE_BRACKETS };
    /** The default character to use as a placeholder in the parse result. */
    static public final char PLACEHOLDER = '\uFFFF';

    /**
     * Parses a given string by recognizing quoted and bracketed substrings.
     * The quote characters are<tt>'</tt> and <tt>"</tt>;
     * recognized bracket pairs are <tt>()</tt>, <tt>{}</tt>, <tt>&lt;&gt;</tt>
     * and <tt>[]</tt>.
     * Within quoted strings, escape codes are intepreted as in Java.
     * Brackets are required to be properly nested.
     * The result is given as a pair of objects: the first is the string with all
     * quoted and bracketed substrings replaced by the character <tt>PLACEHOLDER</tt>,
     * and the second is a list of the replaced substrings, in the order in which
     * they appeared in the original string.  
     * @param expr the string to be parsed
     * @return the result of the parsing; <tt>result[0] instanceof String</tt>
     * and <tt>result[1] instanceof List</tt>. See above for further explanation.
     */
    static public Pair<String, List<String>> parseExpr(String expr) throws FormatException {
        return prototype.parse(expr);
    }

    /** 
     * Puts together a string from a base string, which may contain
     * {@link #PLACEHOLDER} characters, and a list of replacements for the
     * {@link #PLACEHOLDER}s.
     * This is the inverse operaiton of {@link #parseExpr(String)}.
     */
    static public String unparseExpr(String basis, List<String> replacements) {
    	return prototype.unparse(basis, replacements.iterator());
    }
    
    /**
     * Tests if {@link #parseExpr(String)} does not throw an exception.
     * @param expr the expression to be tested
     * @return <tt>true</tt> if <tt>parseExpr(expr)</tt> does not throw an exception.
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
     * @param main the result of string parsing; for the format see {@link #parseExpr(String)}.
     * @return the string from which <tt>parsedString</tt> was originally created; or 
     * <tt>null</tt> if <tt>parsedString</tt> is improperly formatted
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
     * regular expression).
     * Quoted strings and bracketed sub-expressions are treated as atomic, and 
     * whitespaces are trimmed from the result. A wgitespace character as 
     * <tt>split</tt> expression will therefore stand for a sequence of whitespaces,
     * with at least one occurrence of the precise <tt>split</tt> expression.
     * Convenience method; abbreviates <tt>new ExprParser().split(expr,split)</tt>.
     * @see #split(String,String)
     */
    static public String[] splitExpr(String expr, String split) throws FormatException {
        return prototype.split(expr, split);
    }

    /**
     * Splits a given expression according to a given operator
     * and positioning information (infix, prefix or postfix)
     * Quoted strings and bracketed sub-expressions are treated as atomic.
     * Convenience method; abbreviates <tt>new ExprParser().split(expr,split,position)</tt>.
     * @see #split(String,String,int)
     */
    static public String[] splitExpr(String expr, String split, int position) throws FormatException {
        return prototype.split(expr, split, position);
    }

    /**
     * Removes a given outermost bracket pair from a given expression,
     * if the bracket pair is in fact there. 
     * Returns <code>null</code> if the expression was not bracketed in the first place,
     * or if brackets are improperly balanced.
     * This is tested by calling {@link #parseExpr(String)} on the expression.
     * @param expr the expression to be trimmed 
     * @param open the opening bracket
     * @param close the closing bracket
     * @return the trimmed string, or <code>null</code> if the string could not be correctly parsed
     */
    static public String toTrimmed(String expr, char open, char close) {
    	// first test if we have the required surrounding brackets in the first place
    	if (expr.length() == 0 || expr.charAt(0) != open || expr.charAt(expr.length()-1) != close) {
    		return null;
    	}
    	// now test if the expression can be parsed
        try {
        	Pair<String,List<String>> parsedExpr = parseExpr(expr);
        	// yes, it can be parsed; but into how many pieces?
        	if (parsedExpr.first().length() == 1) {
        		// so strip the brackets
        		return expr.substring(1, expr.length()-1);
        	} else {
        		// we have more pieces, so all is not well
        		return null;
        	}
        } catch (FormatException exc) {
        	// prsing failed
        	return null;
        }
    }

    /**
     * Removes a given outermost bracket pair from a given expression,
     * if the bracket pair is in fact there. Also trims whitespace outside the brackets.
     * Returns the string unchanged if it was not bracketed in the first place.
     * Does not test for well-formedness or proper bracket nesting.
     * @param expr the expression to be trimmed 
     * @param open the opening bracket
     * @param close the closing bracket
     * @return the trimmed string
     * @deprecated use {@link #toTrimmed(String, char, char)} instead
     */
    @Deprecated
    static public String trim(String expr, char open, char close) {
        expr = expr.trim();
        if (matches(expr, open, close)) {
            return expr.substring(expr.indexOf(open)+1, expr.lastIndexOf(close));
        } else {
            return expr;
        }
    }

    /**
     * Tests if a given expression has a given outermost bracket pair,
     * possibly with surrounding whitespace.
     * @param expr the expression to be tested 
     * @param open the opening bracket
     * @param close the closing bracket
     * @return <tt>true</tt> if <tt>expr</tt> is has <tt>open</tt>-<tt>close</tt>
     * as an outermost bracket pair, and the enclosed string has no (unescaped) 
     * occurrence of <code>close</code>
     */
    @Deprecated
    static public boolean matches(String expr, char open, char close) {
        expr = expr.trim();
        if (expr.indexOf(open) == 0) {
            // count the difference between open and close characters
            int opens = 1;
            int i = 1;
            while (opens > 0 && i < expr.length()) {
                char c = expr.charAt(i);
                if (c == ESCAPE_CHAR) {
                    // the next char is escaped; it is certainly no match
                    i ++;
                } else if (c == close) {
                    opens--;
                } else if (c == open) {
                	opens++;
                }
                i ++;
            }
            return opens == 0 && i == expr.length();
        } else {
            return false;
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
     * Converts a regular expression to a non-regular expression, by
     * stripping away all characters with special meanings (essentially, all
     * escaped word charaters and all non-escaped non-word characters).
     */
    static public String toNormExpr(String regExpr) {
        String result = regExpr.replaceAll("\\\\\\\\", "" + "\\\\" + PLACEHOLDER);
        result = result.replaceAll("^\\W", "");
        result = result.replaceAll("([^\\\\])\\W", "$1");
        result = result.replaceAll("\\\\(\\W)", "$1");
        result = result.replace(PLACEHOLDER, '\\');
        return result;
    }

    /**
     * Transforms a string by putting quote characters around it,
     * and escaping all quote characters within the string, as well as the escape character.
     * @param string the original string
     * @param quote the quote character to be used
     * @return the quoted string
     */
    static public String toQuoted(String string, char quote) {
    	StringBuffer result = new StringBuffer();
    	result.append(quote);
    	for (char c: string.toCharArray()) {
    		// insert an ESCAPE in front of quotes or ESCAPES
    		if (c == quote || c == ESCAPE_CHAR) {
    			result.append(ESCAPE_CHAR);
    		}
    		result.append(c);
    	}
    	result.append(quote);
    	return result.toString();
    }

    /**
     * Transforms a string by removing quote characters around it, if there are any,
     * and unescaping all characters within the string.
     * @param string the original string
     * @param quote the quote character to be used
     * @return the unquoted string, or <code>null</code> if there were no
     * (unescaped) quotes around the original string
     */
    static public String toUnquoted(String string, char quote) {
        if (string.length() > 1 && string.charAt(0) == quote && string.charAt(string.length() - 1) == quote) {
        	char[] content = string.substring(1, string.length() - 1).toCharArray();
        	StringBuffer result = new StringBuffer();
        	// flag indicating that the previous character was an ESCAPE
        	boolean escaped = false;
        	for (char c: content) {
        		if (escaped) {
        			result.append(c);
        			escaped = false;
        		} else {
        			escaped = c == ESCAPE_CHAR;
        			if (!escaped) {
            			result.append(c);
        			}
        		}
        	}
        	if (escaped) {
        		// the string ended on an escaped quote
        		return null;
        	} else {
        		return result.toString();
        	}
        } else {
            return null;
        }
    }

    /** Main method used to test the class. Call without parameters. */
    static public void main(String[] args) {
        System.out.println("Empty string: "+"".substring(0,0));
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
            for (int i = 0; i < args.length; i++) {
                testParse(args[0]);
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
        string = toUnquoted(string, DOUBLE_QUOTE_CHAR);
        System.out.println(". To unquoted: " + string);
    }

    static private void testParse(String expr) {
        try {
            System.out.println("Parsing: " + expr);
            Pair<String,?> result = parseExpr(expr);
            System.out.println("Result: " + result.first() + " with replacements " + result.second());
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
            System.out.print(position == INFIX_POSITION ? "infix" : position == PREFIX_POSITION ? "prefix" : "postfix");
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
        System.out.println("Trimming bracket pair '" + open + "', '" + close + "' from \"" + expr + "\"");
        String result = toTrimmed(expr.trim(), open, close);
        System.out.println("Result: \"" + result + "\"");
        System.out.println();
    }

    /**
     * Constructs a parser based on the standard quoting and bracketing settings.
     * The standard settings consist of double quotes and round, curly, 
     * square and angle brackets. 
     */
    public ExprParser() {
        this(DEFAULT_BRACKETS);
    }

    /**
     * Constructs a parser based on given bracketing settings and the
     * standard quote characters (single and double quotes).
     */
    public ExprParser(char[][] brackets) {
    	this(DEFAULT_QUOTE_CHARS, brackets, PLACEHOLDER);
    }

    /**
     * Constructs a parser based on given quoting and bracketing settings.
     */
    public ExprParser(char[] quoteChars, char[][] brackets, char placeholder) {
        for (int i = 0; i < quoteChars.length; i++) {
            this.quoteChars.add(new Character(quoteChars[i]));
        }
        for (int i = 0; i < brackets.length; i++) {
            this.openBrackets.add(new Character(brackets[i][0]));
            this.closeBrackets.add(new Character(brackets[i][1]));
        }
        this.placeholder = placeholder;
    }

    /**
     * Parses a given string, based on the quoting and bracketing settings
     * of this parser instance.
     * @param expr the string to be parsed
     * @return the result of the parsing; see {@link #parseExpr(String)}.
     * @see #parseExpr
     */
    public Pair<String, List<String>> parse(String expr) throws FormatException {
    	// flag showing that the previous character was an escape
    	boolean escaped = false;
    	// flag showing that we are inside a quoted string
    	boolean quoted = false;
    	// quote character if quoted is true
    	char quoteChar = 0;
    	// current stack of brackets
        Stack<Character> bracketStack = new Stack<Character>();
        // the resulting stripped expression (with PLACEHOLDER chars)
        StringBuilder strippedExpr = new StringBuilder();
        // the list of replacements so far
        List<String> replacements = new LinkedList<String>();
        // the string currently being built
		StringBuilder current = strippedExpr;
		for (int i = 0; i < expr.length(); i++) {
			char nextChar = expr.charAt(i);
			Character nextTokenChar = nextChar;
			if (escaped) {
				current.append(nextChar);
				escaped = false;
			} else if (nextChar == ESCAPE_CHAR) {
				current.append(nextChar);
				escaped = true;
			} else if (quoted) {
				current.append(nextChar);
				quoted = nextChar != quoteChar;
				if (!quoted && bracketStack.isEmpty()) {
					strippedExpr.append(placeholder);
					replacements.add(current.toString());
					current = strippedExpr;
				}
			} else if (quoteChars.contains(nextTokenChar)) {
				if (bracketStack.isEmpty()) {
					current = new StringBuilder();
				}
				current.append(nextChar);
				quoted = true;
				quoteChar = nextChar;
			} else if (openBrackets.contains(nextTokenChar)) {
				// we have an opening bracket
				if (bracketStack.isEmpty()) {
					current = new StringBuilder();
				}
				current.append(nextTokenChar);
				bracketStack.push(nextTokenChar);
			} else if (closeBrackets.contains(nextTokenChar)) {
				// we have a closing bracket; see if it is expected
				if (bracketStack.isEmpty()) {
					throw new FormatException(
							"Unbalanced brackets in expression \"%s\": \'%c\' is not opened", expr,
							nextTokenChar);
				}
				Character openBracket = bracketStack.pop();
				int openBracketIndex = openBrackets.indexOf(openBracket);
				int closeBracketIndex = closeBrackets.indexOf(nextTokenChar);
				if (openBracketIndex != closeBracketIndex) {
					throw new FormatException(
							"Unbalanced brackets in expression \"%s\": '%c' closed by '%c'", expr,
							openBracket, nextTokenChar);
				}
				current.append(nextTokenChar);
				if (bracketStack.isEmpty()) {
					// this closes the replacement substring
					strippedExpr.append(placeholder);
					replacements.add(current.toString());
					current = strippedExpr;
				}
			} else {
				// we have an ordinary character
				current.append(nextTokenChar);
			}
		}
		if (escaped) {
			throw new FormatException("Expression \"%s\" ends on escape character", expr);
		} else if (quoted) {
			throw new FormatException("Unbalanced quotes in expression \"%s\": '%c' is not closed", expr, quoteChar);
		} else if (!bracketStack.isEmpty()) {
			throw new FormatException("Unbalanced brackets in expression \"%s\": '%c' is not closed", expr, bracketStack.pop());
		}
		return new Pair<String, List<String>>(strippedExpr.toString(), Collections
				.unmodifiableList(replacements));
    }
    
    /**
	 * Reverse operation of {@link #parse(String)}. Given a basis string
	 * (corresponding to element 0 of the output array of {@link #parse(String)}
	 * and an iterator (over the list at element 1 of the output array of
	 * {@link #parse(String)}, returns a string from which
	 * {@link #parse(String)} would have constructed that array.
	 */
    public String unparse(String basis, Iterator<String> replacements) {
        StringBuffer result = new StringBuffer();
        int previousPlace = 0;
        int place = basis.indexOf(placeholder, previousPlace);
        while (place >= 0) {
            result.append(basis.substring(previousPlace, place));
            result.append(replacements.next());
            previousPlace = place+1;
            place = basis.indexOf(placeholder, previousPlace);
        }
        return result.append(basis.substring(previousPlace, basis.length())).toString();
    }

    /**
     * Splits a given expression according to a string (<i>not</i> a regular expression).
     * Quoted strings and bracketed sub-expressions are treated as atomic, and 
     * whitespaces are trimmed from the result. A whitespace character as 
     * <tt>split</tt> expression will therefore stand for a sequence of whitespaces,
     * with at least one occurrence of the precise <tt>split</tt> expression.
     * Trailing empty strings are included in the result.
     * @param expr the string to be split
     * @param split the regular expression used to split the expression.
     * @return the resulting array of strings
     * @throws FormatException if <tt>expr</tt> has unbalanced brackets
     * @see String#split(String,int)
     */
    public String[] split(String expr, String split) throws FormatException {
        Pair<String,List<String>> parseResult = parse(expr);
        String parseExpr = parseResult.first();
        List<String> replacements = parseResult.second();
        // split the parsed expression
        List<String> strippedOperands = new ArrayList<String>();
        int previousIndex = 0;
        while (previousIndex < parseExpr.length() && Character.isSpaceChar(parseExpr.charAt(previousIndex))) {
            previousIndex++;
        }
        int index = parseExpr.indexOf(split, previousIndex); 
        while (index > 0) {
            strippedOperands.add(parseExpr.substring(previousIndex, index).trim());
            previousIndex = index + split.length();
            while (previousIndex < parseExpr.length() && Character.isSpaceChar(parseExpr.charAt(previousIndex))) {
                previousIndex++;
            }
            index = parseExpr.indexOf(split, previousIndex);
        }
        strippedOperands.add(parseExpr.substring(previousIndex));
        String[] result = new String[strippedOperands.size()];
        // put the replacement strings back into the operands
        Iterator<String> replacementIter = replacements.iterator();
        for (int i = 0; i < result.length; i++) {
            result[i] = unparse(strippedOperands.get(i), replacementIter);
        }
        return result;
    }

    /**
     * Splits a given expression into operands according to a given operator,
     * given as a string (<i>not</i> a regular expression)
     * and positioning information (infix, prefix or postfix)
     * Quoted strings and bracketed sub-expressions are treated as atomic.
     * Returns <tt>null</tt> if the operator is a prefix or postfix operator and does not 
     * occur in the correct position; raises an <code>ExprFormatException</code> if
     * there are empty or unbalanced operands.
     * @param expr the string to be split
     * @param oper the operator; note that it is <i>not</i> a regular expression
     * @param position the positioning property of the operator; one of <tt>INFIX</tt>, <tt>PREFIX</tt> or <tt>POSTFIX</tt>
     * @return the resulting array of strings
     * @throws FormatException if <tt>expr</tt> has unbalanced brackets, or the positioning of the operator is not as required
     */
    public String[] split(String expr, String oper, int position) throws FormatException {
        expr = expr.trim();
        switch (position) {
            case INFIX_POSITION :
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
                        throw new FormatException(
                            "Infix operator '"
                                + oper
                                + "' has empty operand nr. "
                                + i
                                + " in \""
                                + expr
                                + "\"");
                    }
                }
                return result;
            case PREFIX_POSITION :
                Pair<String,List<String>> parsedExpr = parse(expr);
                String parsedBasis = parsedExpr.first();
                List<String> replacements = parsedExpr.second();
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
                    return new String[] { unparse(parsedBasis.substring(oper.length()), replacements.iterator()) };
                }
            case POSTFIX_POSITION :
                parsedExpr = parse(expr);
                parsedBasis = parsedExpr.first();
                replacements = parsedExpr.second();
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
                    return new String[] { unparse(parsedBasis.substring(0, operIndex), replacements.iterator()) };
                }
            default :
                // this case should not occur
                throw new IllegalArgumentException("Illegal position parameter value '" + position + "'");
        }
    }

    /**
     * A vector of quote characters, encoded as a list of <tt>Character</tt>.
     */
    private final List<Character> quoteChars = new LinkedList<Character>();
    /**
     * An list of opening bracket characters.
     * The corresponding closing bracket character is at the same index of <tt>closeBrackets</tt>.
     * This is encoded as a list of <tt>Character</tt>.
     * @invariant <tt>openBrackets.size() == closeBrachets.size()</tt>
     */
    private final List<Character> openBrackets = new LinkedList<Character>();
    /**
     * An list of closing bracket characters,
     * The corresponding opening bracket character is at the same index of <tt>openBrackets</tt>.
     * This is encoded as a list of <tt>Character</tt>.
     * @invariant <tt>openBrackets.size() == closeBrachets.size()</tt>
     */
    private final List<Character> closeBrackets = new LinkedList<Character>();
    /** The character to use as a placeholder in the parse result of this parser. */
    private final char placeholder;

    /** Prototype parser, used to evaluate the static methods on. */
    static private final ExprParser prototype = new ExprParser();
}
