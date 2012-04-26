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
     */
    private static boolean isValidTokenStarter(char character) {
        if (character >= 'a' && character <= 'z') {
            return true;
        }
        if (character >= 'A' && character <= 'Z') {
            return true;
        }
        if (character == '_') {
            return true;
        }
        return false;
    }

    /**
     * Helper method. Checks if the argument is allowed as an inner character
     * of a valid token name, which is the case if it conforms to:
     *    ('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'-') 
     */
    private static boolean isValidTokenCharacter(char character) {
        if (character >= 'a' && character <= 'z') {
            return true;
        }
        if (character >= 'A' && character <= 'Z') {
            return true;
        }
        if (character >= '0' && character <= '9') {
            return true;
        }
        if (character == '_' || character == '-') {
            return true;
        }
        return false;
    }

    /**
     * Verification method to determine if an identifier is a valid (rule) name,
     * which is the case if it conforms to the following grammar:
     *    token:  ('a'..'z'|'A'..'Z'|'_')('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'-')*
     *    ID:     token('.' token)*
     * The method returns {@link #PARSE_OK} if the id is valid, and a (short)
     * parse error message otherwise. 
     */
    public static String isValid(String id) {
        boolean first = true;
        for (int i = 0; i < id.length(); i++) {
            if (first) {
                if (!isValidTokenStarter(id.charAt(i))) {
                    if (i > 0 && id.charAt(i) == SEPARATOR_CHAR) {
                        return "identifiers may not contain consecutive '.'";
                    } else {
                        return "identifiers must begin with a letter or '_'";
                    }
                } else {
                    first = false;
                }
            } else {
                if (!isValidTokenCharacter(id.charAt(i))) {
                    if (id.charAt(i) == SEPARATOR_CHAR) {
                        first = true;
                    } else {
                        return "'" + id.charAt(i)
                            + "' is not allowed in identifiers";
                    }
                }
            }
        }
        if (first) {
            if (id.length() == 0) {
                return "empty identifiers are not allowed";
            } else {
                return "identifiers may not end with '.'";
            }
        } else {
            return PARSE_OK;
        }
    }

    /** Constant for the success return value of {@link #isValid}. */
    public static String PARSE_OK = "OK";
}