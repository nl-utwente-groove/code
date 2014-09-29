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
 *
 * Angela Lozano's thesis. EMOOSE student 2002 - 2003 EMOOSE (European Master in
 * Object-Oriented & Software Engineering technologies) Vrije Universiteit
 * Brussel - Ecole des Mines de Nantes - Universiteit Twente
 */
package groove.grammar;

import groove.util.Groove;
import groove.util.parse.FormatException;
import groove.util.parse.StringHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Representation of a qualified name. A qualified name is a
 * name consisting of a nonempty sequence of tokens, separated by
 * {@link #SEPARATOR} characters. Each individual token may be empty. The prefix
 * without the last token is called the parent (which is <tt>null</tt> if there
 * is only a single token); the last token is called the child.
 *
 * @author Angela Lozano and Arend Rensink
 * @version $Revision$ $Date: 2008-01-30 09:32:37 $
 */
public class QualName implements Comparable<QualName> {
    /**
     * Creates a new qualified name, on the basis of a given non-empty list of tokens.
     * @param tokens the list of tokens for the qualified name
     */
    public QualName(List<String> tokens) throws FormatException {
        if (tokens.isEmpty()) {
            throw new FormatException("Name is empty");
        }
        this.tokens = new ArrayList<String>(tokens);
        this.text = Groove.toString(tokens.toArray(), "", "", SEPARATOR, SEPARATOR);
        List<String> parentTokens = new ArrayList<String>(tokens);
        parentTokens.remove(tokens.size() - 1);
        this.parent = Groove.toString(parentTokens.toArray(), "", "", SEPARATOR, SEPARATOR);
    }

    /**
     * Creates a new qualified name, on the basis of a given string.
     * {@link #SEPARATOR} characters appearing in the proposed
     * name will be interpreted as token separators.
     * @param name the text of the new qualified name (without enclosing
     *        characters)
     * @require <tt>name != null</tt>
     */
    public QualName(String name) throws FormatException {
        this(Arrays.asList(StringHandler.splitExpr(name, SEPARATOR)));
    }

    /**
     * Tests whether this name is valid, i.e., contains only allowed
     * characters, and throws an appropriate exception otherwise.
     */
    public void testValid() throws FormatException {
        for (String token : this.tokens) {
            StringBuilder error = new StringBuilder();
            if (!isValid(token, null, error)) {
                throw new FormatException(
                    "Fragment %s of qualified name %s is not well-formed: %s", token, this.text,
                    error.toString());
            }
        }
    }

    @Override
    public int hashCode() {
        return tokens().hashCode();
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
        QualName other = (QualName) obj;
        if (this.tokens.equals(other.tokens)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return this.text;
    }

    /** The text returned by {@link #toString()}. */
    private final String text;

    @Override
    public int compareTo(QualName o) {
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
     * Returns the token in this name at a specific instance
     * @param i the index at which the token is requested
     * @return the token at index <tt>i</tt>
     * @require <tt>0 <= i && i < size()</tt>
     * @ensure </tt>return == tokens[i]</tt>
     */
    public String get(int i) {
        return tokens().get(i);
    }

    /**
     * Indicates whether this qualified name has a (nun-<tt>null</tt>)
     * parent.
     * @return <tt>true</tt> if this name has a (non-<tt>null</tt>) parent
     */
    public boolean hasParent() {
        return this.parent != null;
    }

    /**
     * Returns the parent qualified name (all tokens except the last), or
     * <tt>null</tt> if there is no parent name. There is no parent
     * name iff the qualified name consists of a single token only.
     * @return the parent qualified name
     */
    public String parent() {
        return this.parent;
    }

    /** The parent qualified name (may be {@code null}). */
    private final String parent;

    /**
     * Returns the number of tokens in the qualified name.
     * @return number of tokens in this qualified name
     */
    public int size() {
        return tokens().size();
    }

    /**
     * Returns the last token of this qualified name.
     * @return the last token of the qualified name
     */
    public String child() {
        return get(size() - 1);
    }

    /**
     * Returns the tokens in this qualified name as an array of strings.
     */
    public List<String> tokens() {
        return this.tokens;
    }

    /** The tokens of which this qualified name consists. */
    private final List<String> tokens;

    /** Extends a given qualified name with a child. */
    public QualName extend(String child) throws FormatException {
        List<String> newTokens = new ArrayList<String>(tokens());
        newTokens.add(child);
        return new QualName(newTokens);
    }

    /** Indicates if the qualified name contains a wildcard text as last token. */
    public boolean hasWildCard() {
        return tokens().contains(WILDCARD);
    }

    /** Indicates if this qualified name matches another, taking
     * wildcards into account.
     */
    public boolean matches(QualName other) {
        boolean result = true;
        int min = Math.min(size(), other.size());
        for (int i = 0; result && i < min; i++) {
            if (tokens().get(i).equals(WILDCARD)) {
                break;
            }
            if (other.tokens().get(i).equals(WILDCARD)) {
                break;
            }
            result = tokens().get(i).equals(other.tokens().get(i));
        }
        return result;
    }

    /**
     * Turns a string into a qualified name.
     * Returns {@code null} if the name is not well-formed.
     */
    public static QualName name(String text) {
        try {
            return new QualName(text);
        } catch (FormatException exc) {
            return null;
        }
    }

    /** Returns the last part of a well-formed qualified name. */
    public static String lastName(String fullName) {
        try {
            return new QualName(fullName).child();
        } catch (FormatException e) {
            assert false;
            return null;
        }
    }

    /**
     * Returns the namespace of a well-formed qualified name.
     * The namespace is the qualified name minus its last component.
     * If the name does not have components, the namespace is
     * the empty string.
     */
    public static String parent(String fullName) {
        try {
            return new QualName(fullName).parent();
        } catch (FormatException e) {
            assert false;
            return null;
        }
    }

    /** Extends a given parent name with a child name.
     * If the parent is empty, the result is identical to the child;
     * otherwise, it consists of the concatenation of the two
     * separated by {@link #SEPARATOR}.
     * @param parent the parent name; may be {@code null} or empty
     * @param child the child name, to be embedded in the parent
     * @return the concatenation of parent and child
     */
    public static String extend(String parent, String child) {
        if (parent == null || parent.isEmpty()) {
            return child;
        } else {
            return parent + SEPARATOR + child;
        }
    }

    /** Extends a given parent name with a child name.
     * If the parent is {@code null}, the result is identical to the child.
     * @param parent the parent name; may be {@code null}
     * @param child the child name, to be embedded in the parent
     * @return the concatenation of parent and child
     */
    public static QualName extend(QualName parent, String child) throws FormatException {
        if (parent == null) {
            return new QualName(child);
        } else {
            return parent.extend(child);
        }
    }

    /**
     * Character to separate constituent tokens (as String).
     */
    static public final String SEPARATOR = ".";
    /**
     * Character to separate constituent tokens.
     */
    static public final char SEPARATOR_CHAR = '.';

    /** Wildcard character. */
    static public final char WILDCARD_CHAR = '*';
    /** Wildcard string. */
    static public final String WILDCARD = "" + WILDCARD_CHAR;

    /**
     * Helper method. Checks if the argument is allowed as the first character
     * of a valid token name, which is the case if it is a letter or an
     * underscore.
     * The method also appends a legalized version of the character to the
     * 'legal' string builder, and produces a parse error message in the
     * 'error' string builder if necessary.
     */
    private static boolean isValidStarter(char character, boolean hasLegal, StringBuilder legal,
        boolean hasError, StringBuilder error) {
        if ((character >= 'a' && character <= 'z') || (character >= 'A' && character <= 'Z')
            || (character == '_')) {
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
    private static boolean isValidCharacter(char character, boolean hasLegal, StringBuilder legal,
        boolean hasError, StringBuilder error) {
        if ((character >= 'a' && character <= 'z') || (character >= 'A' && character <= 'Z')
            || (character >= '0' && character <= '9') || (character == '_') || (character == '-')) {
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
            result.append("_PLING_");
            break;
        case '@':
            result.append("_AT_");
            break;
        case '#':
            result.append("_HASH_");
            break;
        case '$':
            result.append("_DOLL_");
            break;
        case '%':
            result.append("_PERC_");
            break;
        case '^':
            result.append("_HAT_");
            break;
        case '&':
            result.append("_AMP_");
            break;
        case '*':
            result.append("_STAR_");
            break;
        case '(':
            result.append("_LPAR_");
            break;
        case ')':
            result.append("_RPAR");
            break;
        case ' ':
            result.append("_SPACE_");
            break;
        case '+':
            result.append("_PLUS_");
            break;
        case '=':
            result.append("_EQ_");
            break;
        case '<':
            result.append("_LT_");
            break;
        case '>':
            result.append("_GT_");
            break;
        case ',':
            result.append("_COMMA_");
            break;
        case '?':
            result.append("_QUERY_");
            break;
        case '-':
            result.append("_-");
            break;
        default:
            if (character >= '0' && character <= '9') {
                result.append("_");
                result.append(character);
            } else {
                result.append("_UNKN_");
            }
        }
    }

    /** Tests if a given string is a well-formed qualified name,
     * i.e., has a non-trivial parent namespace. */
    public static boolean isQualified(String fullName) {
        QualName qualName = QualName.name(fullName);
        return qualName != null && qualName.tokens().size() > 1;
    }

    /** Tests if a given string is a well-formed qualified name. */
    public static boolean isValid(String fullName) {
        return isValid(fullName, null, null);
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
    public static boolean isValid(String id, StringBuilder legal, StringBuilder error) {
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
                    valid = isValidStarter(id.charAt(i), hasLegal, legal, hasError, error) && valid;
                } else {
                    valid =
                        isValidCharacter(id.charAt(i), hasLegal, legal, hasError, error) && valid;
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
    private static String PARSE_ERROR_START = "identifiers must begin with a letter or '_'";

    /** Method for a parse error on an illegal character. */
    private static String PARSE_ERROR_ILLEGAL(char illegal) {
        return "'" + illegal + "' " + PARSE_ERROR_ILLEGAL_TAIL;
    }

    /** Constant for the tail of a parse error on an illegal character. */
    private static String PARSE_ERROR_ILLEGAL_TAIL = "is not allowed in identifiers";

    /** Constant for a parse error for empty strings. */
    public static String PARSE_ERROR_EMPTY = "empty identifiers are not allowed";

    /** Constant for a parse error for strings that begin with a separator. */
    public static String PARSE_ERROR_SEPARATOR_BEGIN = "identifiers may not begin with a separator";

    /** Constant for a parse error for strings that end with a separator. */
    public static String PARSE_ERROR_SEPARATOR_END = "identifiers may not end with a separator";

    /** Constant for a parse error for strings with consecutive separators. */
    public static String PARSE_ERROR_SEPARATOR_CONSECUTIVE =
        "identifiers may not have consecutive separators";

}