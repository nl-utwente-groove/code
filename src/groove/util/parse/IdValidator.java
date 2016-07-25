/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.util.parse;

/**
 * Functionality to test whether a given identifier name is valid.
 * @author Arend Rensink
 * @version $Revision $
 */
public abstract class IdValidator {
    /** Tests whether a given name is valid according to the rule of this validator.
     *  This
     * implementation returns <code>true</code> if <code>text</code> is
     * non-empty, starts with a correct character (according to
     * {@link #isIdentifierStart(char)}), contains only internal characters
     * satisfying {@link #isIdentifierPart(char)}, and ends on a character
     * satisfying {@link #isIdentifierEnd(char)}.
     * @param idName the name to be tested
     * @return <tt>true</tt> if the text does not contain any special characters
     */
    public final boolean isValid(String idName) {
        if (idName == null || idName.isEmpty()) {
            return false;
        }
        char firstChar = idName.charAt(0);
        if (!isIdentifierStart(firstChar)) {
            return false;
        }
        // flag indicating if an alphanumeric character has been found
        boolean containsAlpha = Character.isLetterOrDigit(firstChar);
        int length = idName.length();
        for (int i = 1; i < length - 1; i++) {
            char nextChar = idName.charAt(i);
            if (!(i == 0 ? isIdentifierStart(nextChar) : isIdentifierPart(nextChar))) {
                return false;
            }
            containsAlpha |= Character.isLetterOrDigit(nextChar);
        }
        char lastChar = idName.charAt(length - 1);
        if (!isIdentifierEnd(lastChar)) {
            return false;
        }
        containsAlpha |= Character.isLetterOrDigit(lastChar);
        return containsAlpha;
    }

    /** Tests if a given character is suitable as first character for a variable name. */
    abstract public boolean isIdentifierStart(char c);

    /** Tests if a given character is suitable as middle character of a variable name. */
    abstract boolean isIdentifierPart(char c);

    /** Tests if a given character is suitable as last character of a variable name. */
    abstract boolean isIdentifierEnd(char c);

    /** Validator for standard Java identifiers. */
    public static final IdValidator JAVA_ID = new IdValidator() {
        @Override
        public boolean isIdentifierStart(char c) {
            return Character.isJavaIdentifierStart(c);
        }

        @Override
        public boolean isIdentifierPart(char c) {
            return Character.isJavaIdentifierPart(c);
        }

        @Override
        public boolean isIdentifierEnd(char c) {
            return Character.isJavaIdentifierPart(c);
        }
    };
}
