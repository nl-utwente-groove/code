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
        if (this.text.length() == 0) {
            return false;
        } else if (Character.isLetter(this.text.charAt(0))) {
            int endOfIdentifier = 0;
            while (endOfIdentifier + 1 < this.text.length()
                && isIdentChar(this.text.charAt(endOfIdentifier + 1))) {
                endOfIdentifier++;
            }
            this.lastConsumed = this.text.substring(0, endOfIdentifier + 1);
            this.text = this.text.substring(this.lastConsumed.length());
            return true;
        } else if (this.text.charAt(0) == '\'') {
            int secondQuote = this.text.substring(1).indexOf("'");
            if (secondQuote < 1) {
                return false;
            }
            this.lastConsumed = this.text.substring(1, secondQuote + 1);
            this.text = this.text.substring(this.lastConsumed.length() + 2);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Convenience method for determining if a character is valid within an
     * identifier.
     */
    private boolean isIdentChar(char c) {
        return (Character.isLetterOrDigit(c) || c == '_' || c == '$');
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
        if (this.text.length() == 0) {
            return false;
        } else if (Character.isDigit(this.text.charAt(0))) {
            int endOfNumber = 0;
            while (endOfNumber + 1 < this.text.length()
                && Character.isDigit(this.text.charAt(endOfNumber + 1))) {
                endOfNumber++;
            }
            this.lastConsumed = this.text.substring(0, endOfNumber + 1);
            this.text = this.text.substring(this.lastConsumed.length());
            return true;
        } else {
            return false;
        }
    }
}
