/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package groove.explore.prettyparse;

/**
 * A <code>StringConsumer</code> is a wrapper around a <code>String</code> that
 * allows the beginning of the string to be parsed for literals, identifiers
 * and numbers.
 * 
 * @author Maarten de Mol
 */
public class StringConsumer {

    // The wrapped String.
    private String text;

    // Memory of the last item that was consumed successfully.
    private String lastConsumed;

    /**
     * Builds a new <code>StringConsumer</code> that wraps a given text.
     */
    public StringConsumer(String text) {
        this.text = text;
    }

    /**
     * Getter for the <code>lastConsumed</code> field, which contains the last
     * literal, identifier or number that was consumed successfully.
     */
    public String getLastConsumed() {
        return this.lastConsumed;
    }

    /**
     * Consumes the entire buffer.
     */
    public boolean consumeAll() {
        this.lastConsumed = this.text;
        this.text = "";
        return true;
    }

    /**
     * Attempts to consume a given literal at the beginning of the text.
     * The returned <code>boolean</code> indicates if the literal was found (in
     * which case it is removed from text), or not (in which case the text is
     * not changed in any way).
     */
    public boolean consumeLiteral(String literal) {
        if (this.text.startsWith(literal)) {
            this.text = this.text.substring(literal.length());
            this.lastConsumed = literal;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Attempts to consume an identifier from the beginning of the text.
     * The returned <code>boolean</code> indicates if a non-empty identifier
     * was found (in which case it is removed from text), or not (in which case
     * the text is not changed in any way).
     * The grammar for an identifier is:
     * <pre>{@code
     * Ident :== SingleQuotedText | Letter IdentChar*
     *IdentChar :== Letter | Digit | DOLLAR | UNDERSCORE
     * }</pre>
     * 
     * @see StringConsumer#getLastConsumed
     */
    public boolean consumeIdentifier() {
        String identifier = parseIdentifier(this.text);
        if (identifier == null) {
            return false;
        } else {
            this.lastConsumed = identifier;
            this.text = this.text.substring(identifier.length());
            return true;
        }
    }

    /**
     * Checks if the text has been consumed totally.
     */
    public boolean isEmpty() {
        return this.text.isEmpty();
    }

    /**
     * Attempts to consume a positive number from the beginning of the text.
     * The returned <code>boolean</code> indicates if a number was found (in
     * which case it is removed from text), or not (in which case the text is
     * not changed in any way).
     * 
     * @see StringConsumer#getLastConsumed
     */
    public boolean consumeNumber() {
        String identifier = parseNumber(this.text);
        if (identifier == null) {
            return false;
        } else {
            this.lastConsumed = identifier;
            this.text = this.text.substring(identifier.length());
            return true;
        }
    }

    /**
     * Returns an identifier at the beginning of a string.
     * Returns {@code null} if no identifier was found.
     * The grammar for an identifier is:
     * <pre>{@code
     * Ident :== SingleQuotedText | Letter IdentChar*
     *IdentChar :== Letter | Digit | DOLLAR | UNDERSCORE
     * }</pre>
     * 
     * @see StringConsumer#getLastConsumed
     */
    public static String parseIdentifier(String text) {
        if (text.length() == 0) {
            return null;
        } else if (Character.isLetter(text.charAt(0))) {
            int endOfIdentifier = 0;
            while (endOfIdentifier + 1 < text.length()
                && isIdentChar(text.charAt(endOfIdentifier + 1))) {
                endOfIdentifier++;
            }
            return text.substring(0, endOfIdentifier + 1);
        } else if (text.charAt(0) == '\'') {
            int secondQuote = text.substring(1).indexOf("'");
            if (secondQuote < 1) {
                return null;
            }
            return text.substring(1, secondQuote + 1);
        } else {
            return null;
        }
    }

    /**
     * Convenience method for determining if a character is valid within an
     * identifier.
     */
    private static boolean isIdentChar(char c) {
        return (Character.isLetterOrDigit(c) || c == '_' || c == '$' || c == '-');
    }

    /**
     * Returns a positive number from the beginning of the text.
     * Returns {@code null} if the text does not start with a number.
     * @see StringConsumer#getLastConsumed
     */
    public static String parseNumber(String text) {
        if (text.length() == 0) {
            return null;
        } else if (Character.isDigit(text.charAt(0))) {
            int endOfNumber = 0;
            while (endOfNumber + 1 < text.length()
                && Character.isDigit(text.charAt(endOfNumber + 1))) {
                endOfNumber++;
            }
            return text.substring(0, endOfNumber + 1);
        } else {
            return null;
        }
    }
}
