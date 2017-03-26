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
 * Functionality to test whether a given string is a valid identifier.
 * At least all Java identifiers are considered valid.
 * Also allows to "repair" an invalid identifier.
 * @author Arend Rensink
 * @version $Revision $
 */
public abstract class IdValidator implements Fallible {
    /**
     * Turns a given name into one that is valid according to the rules of this validator,
     * while collecting the errors found along the way.
     * The errors can be retrieved by calling {@link #getErrors()} afterwards.
     * @see #isValid(String) for documentation on what is valid.
     * @param name the name to be tested
     * @return {@code name} if it is valid
     */
    public final String repair(String name) {
        this.errors = new FormatErrorSet();
        StringBuilder result = new StringBuilder();
        if (name == null) {
            this.errors.add("Identifier is null");
            result.append(NULL_TEXT);
        } else if (name.isEmpty()) {
            this.errors.add("Empty name");
            result.append(EMPTY_TEXT);
        } else {
            // flag indicating if an alphanumeric character has been found
            boolean containsAlpha = false;
            boolean first = true;
            int length = name.length();
            for (int i = 0; i < length; i++) {
                char nextChar = name.charAt(i);
                boolean last = (i == length - 1) || isSeparator(name.charAt(i + 1));
                if (isSeparator(nextChar)) {
                    if (first) {
                        this.errors.add("Empty name");
                        result.append(EMPTY_TEXT);
                    } else if (!containsAlpha) {
                        this.errors.add("Name without alphanumeric character");
                        result.append("0");
                    }
                    result.append(nextChar);
                    containsAlpha = false;
                    first = true;
                } else if (first ? isIdentifierStart(nextChar)
                    : last ? isIdentifierEnd(nextChar) : isIdentifierPart(nextChar)) {
                    result.append(nextChar);
                    first = false;
                } else {
                    this.errors.add("Illegal %s character '%s'",
                        first ? "first" : last ? "final" : "internal",
                        nextChar);
                    if (first && isIdentifierPart(nextChar)) {
                        // we can repair this by prepending an underscore
                        result.append("_");
                        result.append(nextChar);
                    } else if (last && isIdentifierPart(nextChar)) {
                        // we can repair this by appending an underscore
                        result.append(nextChar);
                        result.append("_");
                    } else {
                        // we can only repair by replacing
                        result.append(legalise(nextChar));
                        containsAlpha = true;
                    }
                    first = false;
                }
                containsAlpha |= Character.isLetterOrDigit(nextChar);
            }
            if (!containsAlpha) {
                if (first) {
                    this.errors.add("Empty name");
                    result.append(EMPTY_TEXT);
                } else {
                    this.errors.add("Name without alphanumeric character");
                    result.append("0");
                }
            }
        }
        return result.toString();
    }

    /** Tests whether a given name is valid according to the rule of this validator,
     * and throws an exception if this is not the case.
     * @see #isValid(String) for documentation on what is valid.
     * @param name the name to be tested
     * @return {@code name} if it is valid
     * @throws FormatException if {@code name} is not valid
     */
    public final String testValid(String name) throws FormatException {
        FormatErrorSet oldErrors = this.errors;
        repair(name);
        FormatErrorSet newErrors = this.errors;
        this.errors = oldErrors;
        newErrors.throwException();
        return name;
    }

    /** Tests whether a given name is valid according to the rule of this validator.
     *  This
     * implementation returns <code>true</code> if {@code name} is
     * non-empty, starts with a correct character (according to
     * {@link #isIdentifierStart(char)}), contains only internal characters
     * satisfying {@link #isIdentifierPart(char)}, and ends on a character
     * satisfying {@link #isIdentifierEnd(char)}.
     * Finally, the name should contain at least one alphanumeric character
     * @param name the name to be tested
     * @return <tt>true</tt> if the text does not contain any invalid characters
     */
    public final boolean isValid(String name) {
        FormatErrorSet oldErrors = this.errors;
        repair(name);
        FormatErrorSet newErrors = this.errors;
        this.errors = oldErrors;
        return newErrors.isEmpty();
    }

    /** Tests if a given character is suitable as first character for an identifier. */
    public boolean isIdentifierStart(char c) {
        return Character.isJavaIdentifierStart(c);
    }

    /** Tests if a given character is suitable as middle character of an identifier. */
    public boolean isIdentifierPart(char c) {
        return Character.isJavaIdentifierPart(c);
    }

    /** Tests if a given character is suitable as last character of an identifier. */
    public boolean isIdentifierEnd(char c) {
        return Character.isJavaIdentifierPart(c);
    }

    /** Tests if a given character is a separator between name fragments. */
    public boolean isSeparator(char c) {
        return false;
    }

    /**
     * Returns a string for an illegal character.
     */
    public String legalise(char character) {
        switch (character) {
        case '!':
            return "_PLING_";
        case '@':
            return "_AT_";
        case '#':
            return "_HASH_";
        case '$':
            return "_DLR_";
        case '%':
            return "_PERC_";
        case '^':
            return "_HAT_";
        case '&':
            return "_AMP_";
        case '*':
            return "_STAR_";
        case '(':
            return "_LPAR_";
        case ')':
            return "_RPAR";
        case ' ':
            return "_SPC_";
        case '+':
            return "_PLUS_";
        case '=':
            return "_EQ_";
        case '<':
            return "_LT_";
        case '>':
            return "_GT_";
        case ',':
            return "_COM_";
        case '?':
            return "_QRY_";
        case '-':
            return "_HYPH_";
        case '{':
            return "_LBR_";
        case '}':
            return "_RBR_";
        case '[':
            return "_LSQ_";
        case ']':
            return "_RSQ_";
        default:
            return "_UNKN_";
        }
    }

    @Override
    public FormatErrorSet getErrors() {
        return this.errors;
    }

    private FormatErrorSet errors = new FormatErrorSet();

    private static final String NULL_TEXT = "_NULL_";
    private static final String EMPTY_TEXT = "_EMP_";

    /** Validator for standard Java identifiers. */
    public static final IdValidator JAVA_ID = new IdValidator() {
        // empty
    };
}
