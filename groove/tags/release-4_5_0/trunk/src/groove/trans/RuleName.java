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
 * $Id: RuleNameLabel.java,v 1.2 2008-01-30 09:32:37 iovka Exp $
 * 
 * Angela Lozano's thesis. EMOOSE student 2002 - 2003 EMOOSE (European Master in
 * Object-Oriented & Software Engineering technologies) Vrije Universiteit
 * Brussel - Ecole des Mines de Nantes - Universiteit Twente
 */
package groove.trans;

import groove.util.Groove;
import groove.view.FormatException;

import java.util.Arrays;

/**
 * Representation of a structured rule name. A structured rule name is a rule
 * name consisting of a nonempty sequence of tokens, seperated by
 * <tt>SEPARATOR</tt> characters. Each individual token may be empty. The prefix
 * without the last token is called the parent (which is <tt>null</tt> if there
 * is only a single token); the last token is called the child.
 * 
 * @author Angela Lozano and Arend Rensink
 * @version $Revision$ $Date: 2008-01-30 09:32:37 $
 */
public class RuleName implements Comparable<RuleName> {
    /**
     * Creates a new structured rule name, on the basis of a given
     * <tt>String</tt>. <tt>SEPARATOR</tt> characters appearing in the proposed
     * name will be interpreted as token separators.
     * @param name the text of the new structured rule name (without enclosing
     *        characters)
     * @require <tt>name != null</tt>
     */
    public RuleName(String name) {
        this.tokens = name.split("\\" + SEPARATOR);
        int lastSeparator = name.lastIndexOf(SEPARATOR);
        this.parent =
            lastSeparator < 0 ? null : new RuleName(name.substring(0,
                lastSeparator));
        this.text = name;
    }

    /**
     * Creates a new structured rule name, on the basis of a given parent name
     * and child <tt>String</tt>. If the parent name is <tt>null</tt>, the
     * resulting structured rule name consists of a single token (just the
     * child).
     * @param parent the parent structured rule name
     * @param child the child rule name (without enclosing characters)
     * @throws FormatException if {@code child} contains forbidden symbols
     */
    public RuleName(RuleName parent, String child) throws FormatException {
        if (child.contains(SEPARATOR)) {
            throw new FormatException(
                "Rule name %s should not contain separator symbol %s", child,
                SEPARATOR);
        }
        int parentSize = parent == null ? 0 : parent.size();
        this.tokens = new String[parentSize + 1];
        if (parent != null) {
            System.arraycopy(parent.tokens, 0, this.tokens, 0, parentSize);
        }
        this.tokens[this.tokens.length - 1] = child;
        this.parent = parent;
        this.text = Groove.toString(this.tokens, "", "", SEPARATOR, SEPARATOR);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(this.tokens);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        RuleName other = (RuleName) obj;
        if (!Arrays.equals(this.tokens, other.tokens)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.text;
    }

    @Override
    public int compareTo(RuleName o) {
        int result = 0;
        int minSize = Math.max(size(), o.size());
        for (int i = 0; result == 0 && i < minSize; i++) {
            result = get(i).compareTo(o.get(i));
        }
        if (result == 0) {
            result = size() - o.size();
        }
        return result;
    }

    /**
     * Returns the token in this rule name at a specific instance
     * @param i the index at which the token is requested
     * @return the token at index <tt>i</tt>
     * @require <tt>0 <= i && i < size()</tt>
     * @ensure </tt>return == tokens[i]</tt>
     */
    public String get(int i) {
        return tokens()[i];
    }

    /**
     * Indicates whether this structured rule name has a (nun-<tt>null</tt>)
     * parent.
     * @return <tt>true</tt> if this rule name has a (non-<tt>null</tt>) parent
     * @ensure <tt>return == size()>1</tt>
     */
    public boolean hasParent() {
        return this.parent != null;
    }

    /**
     * Returns the number of tokens in the rule name.
     * @return number of tokens in this rule name
     * @ensure <tt>return > 0</tt>
     */
    public int size() {
        return tokens().length;
    }

    /**
     * Returns the last token of this rule name.
     * @return the last token of the rule name
     * @ensure <tt>return == get(size()-1)</tt>
     */
    public String child() {
        return tokens()[size() - 1];
    }

    /**
     * Returns the parent rule name (all tokens except the last), or
     * <tt>null</tt> if there is no parent rule name. There is no parent rule
     * name iff the rule name consists of a single token only.
     * @return the parent rule name
     * @ensure <tt>return == null</tt> iff <tt>size() == 1</tt>
     */
    public RuleName parent() {
        return this.parent;
    }

    /**
     * Returns the tokens in this structured rule name as an array of strings.
     */
    public String[] tokens() {
        return this.tokens;
    }

    /** The parent rule name (may be {@code null}). */
    private final RuleName parent;
    /** The tokens of which this rule name consists. */
    private final String[] tokens;
    /** The text returned by {@link #toString()}. */
    private final String text;

    /** Returns the last part of a qualified name. */
    public static String getLastName(String fullName) {
        return new RuleName(fullName).child();
    }

    /** Returns the namespace of a qualified name. */
    public static String getParent(String fullName) {
        RuleName parent = new RuleName(fullName).parent();
        return parent == null ? "" : parent.toString();
    }

    /**
     * Character to separate constituent tokens (as String).
     */
    static public final String SEPARATOR = ".";
    /**
     * Character to separate constituent tokens.
     */
    static public final char SEPARATOR_CHAR = '.';

    /**
     * Helper method. Checks if the argument is allowed as the first character
     * of a valid token name, which is the case if it is a letter or an
     * underscore.
     * The method also appends a legalized version of the character to the
     * 'legal' string builder, and produces a parse error message in the
     * 'error' string builder if necessary.
     */
    private static boolean isValidStarter(char character, boolean hasLegal,
            StringBuilder legal, boolean hasError, StringBuilder error) {
        if ((character >= 'a' && character <= 'z')
            || (character >= 'A' && character <= 'Z') || (character == '_')) {
            if (hasLegal) {
                legal.append(character);
            }
            return true;
        } else {
            if (hasLegal) {
                legalize(character, legal);
            }
            if (hasError && error.length() == 0) {
                error.append(PARSE_ERROR_START);
            }
            return false;
        }
    }

    /**
     * Helper method. Checks if the argument is allowed as an inner character
     * of a valid token name, which is the case if it conforms to:
     *    ('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'-')
     * The method also appends a legalized version of the character to the
     * 'legal' string builder, and produces a parse error message in the
     * 'error' string builder if necessary.
     */
    private static boolean isValidCharacter(char character, boolean hasLegal,
            StringBuilder legal, boolean hasError, StringBuilder error) {
        if ((character >= 'a' && character <= 'z')
            || (character >= 'A' && character <= 'Z')
            || (character >= '0' && character <= '9') || (character == '_')
            || (character == '-')) {
            if (hasLegal) {
                legal.append(character);
            }
            return true;
        } else {
            if (hasLegal) {
                legalize(character, legal);
            }
            if (hasError && error.length() == 0) {
                error.append(PARSE_ERROR_ILLEGAL(character));
            }
            return false;
        }
    }

    /**
     * Helper method. Produces a legal string for each illegal character.
     */
    private static void legalize(char character, StringBuilder result) {
        switch (character) {
        case '!':
            result.append("_EXCL");
            break;
        case '@':
            result.append("_AT");
            break;
        case '#':
            result.append("_HASH");
            break;
        case '$':
            result.append("_DOLL");
            break;
        case '%':
            result.append("_PERC");
            break;
        case '^':
            result.append("_HAT");
            break;
        case '&':
            result.append("_AMP");
            break;
        case '*':
            result.append("_STAR");
            break;
        case '(':
            result.append("_LBRA");
            break;
        case ')':
            result.append("_RBRA");
            break;
        case ' ':
            result.append("_SPCE");
            break;
        case '+':
            result.append("_PLUS");
            break;
        case '=':
            result.append("_EQ");
            break;
        case '<':
            result.append("_LT");
            break;
        case '>':
            result.append("_GT");
            break;
        case ',':
            result.append("_COMM");
            break;
        case '?':
            result.append("_QSTN");
            break;
        case '-':
            result.append("_-");
            break;
        default:
            if (character >= '0' && character <= '9') {
                result.append("_");
                result.append(character);
            } else {
                result.append("_UNKN");
            }
        }
    }

    /**
     * Verification method to determine if an identifier is a valid (rule) name,
     * which is the case if it conforms to the following grammar:
     *    token:  ('a'..'z'|'A'..'Z'|'_')('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'-')*
     *    ID:     token('.' token)*
     * The method also appends a legalized version of the character to the
     * 'legal' string builder, and produces a parse error message in the
     * 'error' string builder if necessary.
     */
    public static boolean isValid(String id, StringBuilder legal,
            StringBuilder error) {
        boolean first = true;
        boolean valid = true;
        boolean hasLegal = legal != null;
        boolean hasError = error != null;
        for (int i = 0; i < id.length(); i++) {
            if (id.charAt(i) == SEPARATOR_CHAR) {
                valid = valid && !first;
                if (first && hasError && error.length() == 0) {
                    if (i == 0) {
                        error.append(PARSE_ERROR_SEPARATOR_BEGIN);
                    } else {
                        error.append(PARSE_ERROR_SEPARATOR_CONSECUTIVE);
                    }
                }
                first = true;
                if (hasLegal) {
                    legal.append(SEPARATOR_CHAR);
                }
            } else {
                if (first) {
                    valid =
                        isValidStarter(id.charAt(i), hasLegal, legal, hasError,
                            error) && valid;
                } else {
                    valid =
                        isValidCharacter(id.charAt(i), hasLegal, legal,
                            hasError, error) && valid;
                }
                first = false;
            }
        }
        if (first && valid) {
            if (hasError && error.length() == 0) {
                if (id.length() == 0) {
                    error.append(PARSE_ERROR_EMPTY);
                } else {
                    error.append(PARSE_ERROR_SEPARATOR_END);
                }
            }
            return false;
        } else {
            return valid;
        }
    }

    /** Constant for a parse error on the first character of an identifier. */
    private static String PARSE_ERROR_START =
        "identifiers must begin with a letter or '_'";

    /** Method for a parse error on an illegal character. */
    private static String PARSE_ERROR_ILLEGAL(char illegal) {
        return "'" + illegal + "' " + PARSE_ERROR_ILLEGAL_TAIL;
    }

    /** Constant for the tail of a parse error on an illegal character. */
    private static String PARSE_ERROR_ILLEGAL_TAIL =
        "is not allowed in identifiers";

    /** Constant for a parse error for empty strings. */
    public static String PARSE_ERROR_EMPTY =
        "empty identifiers are not allowed";

    /** Constant for a parse error for strings that begin with a separator. */
    public static String PARSE_ERROR_SEPARATOR_BEGIN =
        "identifiers may not begin with a separator";

    /** Constant for a parse error for strings that end with a separator. */
    public static String PARSE_ERROR_SEPARATOR_END =
        "identifiers may not end with a separator";

    /** Constant for a parse error for strings with consecutive separators. */
    public static String PARSE_ERROR_SEPARATOR_CONSECUTIVE =
        "identifiers may not have consecutive separators";
}