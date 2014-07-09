/*
 * 10/08/2004
 *
 * JavaScriptTokenMaker.java - An object that can take a chunk of text and
 * return a linked list of <code>Token</code>s representing it in the JavaScript
 * programming language. Copyright (C) 2004 Robert Futrell robert_futrell at
 * users.sourceforge.net http://fifesoft.com/rsyntaxtextarea
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */
package groove.control.parse;

import javax.swing.text.Segment;

import org.fife.ui.rsyntaxtextarea.AbstractTokenMaker;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMap;

/**
 * A token maker that turns text into a linked list of <code>Token</code>s for
 * syntax highlighting in the JavaScript programming language.
 *
 * @author Robert Futrell
 * @version 0.1
 */
@SuppressWarnings("all")
public class CtrlTokenMaker extends AbstractTokenMaker {

    protected final String operators = "+-*/%!=^&|?:#";

    protected final String separators = "()[]{}<>";

    protected final String separators2 = ".,;"; // Characters you don't want
    // syntax highlighted but
    // separate identifiers.

    protected final String hexCharacters = "0123456789ABCDEFabcdef";

    protected final String numberEndChars = "FfLl"; // Characters used to
    // specify literal number
    // types.

    private int currentTokenStart;
    private int currentTokenType;

    /**
     * Constructor.
     */
    public CtrlTokenMaker() {
        super(); // Initializes tokensToHighlight.
    }

    /**
     * Checks the token to give it the exact ID it deserves before being passed
     * up to the super method.
     *
     * @param segment <code>Segment</code> to get text from.
     * @param start Start offset in <code>segment</code> of token.
     * @param end End offset in <code>segment</code> of token.
     * @param tokenType The token's type.
     * @param startOffset The offset in the document at which the token occurs.
     */
    @Override
    public void addToken(Segment segment, int start, int end, int tokenType, int startOffset) {

        switch (tokenType) {
        // Since reserved words, functions, and data types are all passed
        // into here as "identifiers," we have to see what the token
        // really is...
        case Token.IDENTIFIER:
            int value = this.wordsToHighlight.get(segment, start, end);
            if (value != -1) {
                tokenType = value;
            }
            break;
        case Token.WHITESPACE:
        case Token.SEPARATOR:
        case Token.OPERATOR:
        case Token.ERROR_IDENTIFIER:
        case Token.ERROR_NUMBER_FORMAT:
        case Token.ERROR_STRING_DOUBLE:
        case Token.ERROR_CHAR:
        case Token.COMMENT_EOL:
        case Token.COMMENT_MULTILINE:
        case Token.LITERAL_BOOLEAN:
        case Token.LITERAL_NUMBER_DECIMAL_INT:
        case Token.LITERAL_NUMBER_FLOAT:
        case Token.LITERAL_NUMBER_HEXADECIMAL:
        case Token.LITERAL_STRING_DOUBLE_QUOTE:
        case Token.LITERAL_CHAR:
            break;

        default:
            throw new IllegalArgumentException("Unknown tokenType: '" + tokenType + "'");
        }

        super.addToken(segment, start, end, tokenType, startOffset);

    }

    /**
     * Returns the text to place at the beginning and end of a line to "comment"
     * it in a this programming language.
     *
     * @return The start and end strings to add to a line to "comment" it out.
     */
    @Override
    public String[] getLineCommentStartAndEnd() {
        return new String[] {"//", null};
    }

    /**
     * Returns the words to highlight for the Groove control language.
     *
     * @return A <code>TokenMap</code> containing the words to highlight for the
     *         Groove control language.
     * @see org.fife.ui.rsyntaxtextarea.AbstractTokenMaker#getWordsToHighlight
     */
    @Override
    public TokenMap getWordsToHighlight() {

        TokenMap tokenMap = new TokenMap();

        int reservedWord = Token.RESERVED_WORD;
        tokenMap.put("alap", reservedWord);
        tokenMap.put("any", reservedWord);
        tokenMap.put("choice", reservedWord);
        tokenMap.put("do", reservedWord);
        tokenMap.put("else", reservedWord);
        tokenMap.put("function", reservedWord);
        tokenMap.put("if", reservedWord);
        tokenMap.put("import", reservedWord);
        tokenMap.put("or", reservedWord);
        tokenMap.put("other", reservedWord);
        tokenMap.put("package", reservedWord);
        tokenMap.put("priority", reservedWord);
        tokenMap.put("recipe", reservedWord);
        tokenMap.put("try", reservedWord);
        tokenMap.put("until", reservedWord);
        tokenMap.put("var", reservedWord);
        tokenMap.put("while", reservedWord);

        int literalBoolean = Token.LITERAL_BOOLEAN;
        tokenMap.put("false", literalBoolean);
        tokenMap.put("true", literalBoolean);

        int dataType = Token.DATA_TYPE;
        tokenMap.put("bool", dataType);
        tokenMap.put("float", dataType);
        tokenMap.put("int", dataType);
        tokenMap.put("node", dataType);
        tokenMap.put("string", dataType);
        tokenMap.put("out", dataType);

        return tokenMap;

    }

    /**
     * Returns the first token in the linked list of tokens generated from
     * <code>text</code>. This method must be implemented by subclasses so they
     * can correctly implement syntax highlighting.
     *
     * @param text The text from which to get tokens.
     * @param initialTokenType The token type we should start with.
     * @param startOffset The offset into the document at which
     *        <code>text</code> starts.
     * @return The first <code>Token</code> in a linked list representing the
     *         syntax highlighted text.
     */
    @Override
    public Token getTokenList(Segment text, int initialTokenType, int startOffset) {

        resetTokenList();

        char[] array = text.array;
        int offset = text.offset;
        int count = text.count;
        int end = offset + count;

        // See, when we find a token, its starting position is always of the
        // form: 'startOffset + (currentTokenStart-offset)'; but since
        // startOffset and offset are constant, tokens' starting positions
        // become: 'newStartOffset+currentTokenStart' for one less subraction
        // operation.
        int newStartOffset = startOffset - offset;

        this.currentTokenStart = offset;
        this.currentTokenType = initialTokenType;
        boolean backslash = false;
        boolean numContainsExponent = false;
        boolean numContainsEndCharacter = false;

        for (int i = offset; i < end; i++) {

            char c = array[i];

            switch (this.currentTokenType) {

            case Token.NULL:

                this.currentTokenStart = i; // Starting a new token here.

                switch (c) {

                case ' ':
                case '\t':
                    this.currentTokenType = Token.WHITESPACE;
                    break;

                case '"':
                    if (backslash) { // Escaped double quote => call '"' an
                        // identifier..
                        addToken(text, this.currentTokenStart, i, Token.IDENTIFIER, newStartOffset
                            + this.currentTokenStart);
                        backslash = false;
                    } else {
                        this.currentTokenType = Token.ERROR_STRING_DOUBLE;
                    }
                    break;

                case '\'':
                    if (backslash) { // Escaped single quote => call '\'' an
                        // identifier.
                        addToken(text, this.currentTokenStart, i, Token.IDENTIFIER, newStartOffset
                            + this.currentTokenStart);
                        backslash = false;
                    } else {
                        this.currentTokenType = Token.ERROR_CHAR;
                    }
                    break;

                case '\\':
                    addToken(text, this.currentTokenStart, i, Token.IDENTIFIER, newStartOffset
                        + this.currentTokenStart);
                    this.currentTokenType = Token.NULL;
                    backslash = !backslash;
                    break;

                default:
                    if (RSyntaxUtilities.isDigit(c)) {
                        this.currentTokenType = Token.LITERAL_NUMBER_DECIMAL_INT;
                        break;
                    } else if (RSyntaxUtilities.isLetter(c) || c == '_') {
                        this.currentTokenType = Token.IDENTIFIER;
                        break;
                    }
                    int indexOf = this.operators.indexOf(c, 0);
                    if (indexOf > -1) {
                        this.currentTokenStart = i;
                        this.currentTokenType = Token.OPERATOR;
                        break;
                    }
                    indexOf = this.separators.indexOf(c, 0);
                    if (indexOf > -1) {
                        addToken(text, this.currentTokenStart, i, Token.SEPARATOR, newStartOffset
                            + this.currentTokenStart);
                        this.currentTokenType = Token.NULL;
                        break;
                    }
                    indexOf = this.separators2.indexOf(c, 0);
                    if (indexOf > -1) {
                        addToken(text, this.currentTokenStart, i, Token.IDENTIFIER, newStartOffset
                            + this.currentTokenStart);
                        this.currentTokenType = Token.NULL;
                        break;
                    } else {
                        this.currentTokenType = Token.ERROR_IDENTIFIER;
                        break;
                    }

                } // End of switch (c).

                break;

            case Token.WHITESPACE:

                switch (c) {

                case ' ':
                case '\t':
                    break; // Still whitespace.

                case '\\':
                    addToken(text, this.currentTokenStart, i - 1, Token.WHITESPACE, newStartOffset
                        + this.currentTokenStart);
                    addToken(text, i, i, Token.IDENTIFIER, newStartOffset + i);
                    this.currentTokenType = Token.NULL;
                    backslash = true; // Previous char whitespace => this must
                    // be first backslash.
                    break;

                case '"': // Don't need to worry about backslashes as previous
                    // char is space.
                    addToken(text, this.currentTokenStart, i - 1, Token.WHITESPACE, newStartOffset
                        + this.currentTokenStart);
                    this.currentTokenStart = i;
                    this.currentTokenType = Token.ERROR_STRING_DOUBLE;
                    backslash = false;
                    break;

                case '\'': // Don't need to worry about backslashes as previous
                    // char is space.
                    addToken(text, this.currentTokenStart, i - 1, Token.WHITESPACE, newStartOffset
                        + this.currentTokenStart);
                    this.currentTokenStart = i;
                    this.currentTokenType = Token.ERROR_CHAR;
                    backslash = false;
                    break;

                default: // Add the whitespace token and start anew.

                    addToken(text, this.currentTokenStart, i - 1, Token.WHITESPACE, newStartOffset
                        + this.currentTokenStart);
                    this.currentTokenStart = i;

                    if (RSyntaxUtilities.isDigit(c)) {
                        this.currentTokenType = Token.LITERAL_NUMBER_DECIMAL_INT;
                        break;
                    } else if (RSyntaxUtilities.isLetter(c) || c == '_') {
                        this.currentTokenType = Token.IDENTIFIER;
                        break;
                    }
                    int indexOf = this.operators.indexOf(c, 0);
                    if (indexOf > -1) {
                        this.currentTokenStart = i;
                        this.currentTokenType = Token.OPERATOR;
                        break;
                    }
                    indexOf = this.separators.indexOf(c, 0);
                    if (indexOf > -1) {
                        addToken(text, i, i, Token.SEPARATOR, newStartOffset + i);
                        this.currentTokenType = Token.NULL;
                        break;
                    }
                    indexOf = this.separators2.indexOf(c, 0);
                    if (indexOf > -1) {
                        addToken(text, i, i, Token.IDENTIFIER, newStartOffset + i);
                        this.currentTokenType = Token.NULL;
                        break;
                    } else {
                        this.currentTokenType = Token.ERROR_IDENTIFIER;
                    }

                } // End of switch (c).

                break;

            default: // Should never happen
            case Token.IDENTIFIER:

                switch (c) {

                case ' ':
                case '\t':
                    addToken(text, this.currentTokenStart, i - 1, Token.IDENTIFIER, newStartOffset
                        + this.currentTokenStart);
                    this.currentTokenStart = i;
                    this.currentTokenType = Token.WHITESPACE;
                    break;

                case '"': // Don't need to worry about backslashes as previous
                    // char is non-backslash.
                    addToken(text, this.currentTokenStart, i - 1, Token.IDENTIFIER, newStartOffset
                        + this.currentTokenStart);
                    this.currentTokenStart = i;
                    this.currentTokenType = Token.ERROR_STRING_DOUBLE;
                    backslash = false;
                    break;

                case '\'': // Don't need to worry about backslashes as previous
                    // char is non-backslash.
                    addToken(text, this.currentTokenStart, i - 1, Token.IDENTIFIER, newStartOffset
                        + this.currentTokenStart);
                    this.currentTokenStart = i;
                    this.currentTokenType = Token.ERROR_CHAR;
                    backslash = false;
                    break;

                case '\\':
                    addToken(text, this.currentTokenStart, i - 1, Token.IDENTIFIER, newStartOffset
                        + this.currentTokenStart);
                    addToken(text, i, i, Token.IDENTIFIER, newStartOffset + i);
                    this.currentTokenType = Token.NULL;
                    backslash = true;
                    break;

                default:
                    if (RSyntaxUtilities.isLetterOrDigit(c) || c == '_') {
                        break; // Still an identifier of some type.
                    }
                    int indexOf = this.operators.indexOf(c);
                    if (indexOf > -1) {
                        addToken(text, this.currentTokenStart, i - 1, Token.IDENTIFIER,
                            newStartOffset + this.currentTokenStart);
                        this.currentTokenStart = i;
                        this.currentTokenType = Token.OPERATOR;
                        break;
                    }
                    indexOf = this.separators.indexOf(c, 0);
                    if (indexOf > -1) {
                        addToken(text, this.currentTokenStart, i - 1, Token.IDENTIFIER,
                            newStartOffset + this.currentTokenStart);
                        addToken(text, i, i, Token.SEPARATOR, newStartOffset + i);
                        this.currentTokenType = Token.NULL;
                        break;
                    }
                    indexOf = this.separators2.indexOf(c, 0);
                    if (indexOf > -1) {
                        addToken(text, this.currentTokenStart, i - 1, Token.IDENTIFIER,
                            newStartOffset + this.currentTokenStart);
                        addToken(text, i, i, Token.IDENTIFIER, newStartOffset + i);
                        this.currentTokenType = Token.NULL;
                        break;
                    } else {
                        this.currentTokenType = Token.ERROR_IDENTIFIER;
                    }

                } // End of switch (c).

                break;

            case Token.LITERAL_NUMBER_DECIMAL_INT:

                // Reset our boolean states if we only have one digit char
                // before
                // the current one.
                if (this.currentTokenStart == i - 1) {
                    numContainsExponent = false;
                    numContainsEndCharacter = false;
                }

                switch (c) {

                case ' ':
                case '\t':
                    addToken(text, this.currentTokenStart, i - 1, Token.LITERAL_NUMBER_DECIMAL_INT,
                        newStartOffset + this.currentTokenStart);
                    this.currentTokenStart = i;
                    this.currentTokenType = Token.WHITESPACE;
                    break;

                case '"': // Don't need to worry about backslashes as previous
                    // char is non-backslash.
                    addToken(text, this.currentTokenStart, i - 1, Token.LITERAL_NUMBER_DECIMAL_INT,
                        newStartOffset + this.currentTokenStart);
                    this.currentTokenStart = i;
                    this.currentTokenType = Token.ERROR_STRING_DOUBLE;
                    backslash = false;
                    break;

                case '\'': // Don't need to worry about backslashes as previous
                    // char is non-backslash.
                    addToken(text, this.currentTokenStart, i - 1, Token.LITERAL_NUMBER_DECIMAL_INT,
                        newStartOffset + this.currentTokenStart);
                    this.currentTokenStart = i;
                    this.currentTokenType = Token.ERROR_CHAR;
                    backslash = false;
                    break;

                case '\\':
                    addToken(text, this.currentTokenStart, i - 1, Token.LITERAL_NUMBER_DECIMAL_INT,
                        newStartOffset + this.currentTokenStart);
                    addToken(text, i, i, Token.IDENTIFIER, newStartOffset + i);
                    this.currentTokenType = Token.NULL;
                    backslash = true;
                    break;

                default:

                    if (RSyntaxUtilities.isDigit(c)) {
                        break; // Still a literal number.
                    } else if (c == 'e') { // Exponent.
                        if (numContainsExponent == false) {
                            numContainsExponent = true;
                        } else {
                            this.currentTokenType = Token.ERROR_NUMBER_FORMAT;
                        }
                        break;
                    } else if (c == '.') { // Decimal point.
                        if (numContainsExponent == true) {
                            this.currentTokenType = Token.ERROR_NUMBER_FORMAT;
                        } else {
                            this.currentTokenType = Token.LITERAL_NUMBER_FLOAT;
                        }
                        break;
                    }
                    int indexOf = this.numberEndChars.indexOf(c);
                    if (indexOf > -1) { // Numbers can end in 'f','F','l','L',
                        // etc.
                        if (numContainsEndCharacter == true) {
                            this.currentTokenType = Token.ERROR_NUMBER_FORMAT;
                        } else {
                            numContainsEndCharacter = true;
                        }
                        break;
                    }
                    indexOf = this.operators.indexOf(c);
                    if (indexOf > -1) {
                        addToken(text, this.currentTokenStart, i - 1,
                            Token.LITERAL_NUMBER_DECIMAL_INT, newStartOffset
                                + this.currentTokenStart);
                        this.currentTokenStart = i;
                        this.currentTokenType = Token.OPERATOR;
                        break;
                    }
                    indexOf = this.separators.indexOf(c);
                    if (indexOf > -1) {
                        addToken(text, this.currentTokenStart, i - 1,
                            Token.LITERAL_NUMBER_DECIMAL_INT, newStartOffset
                                + this.currentTokenStart);
                        addToken(text, i, i, Token.SEPARATOR, newStartOffset + i);
                        this.currentTokenType = Token.NULL;
                        break;
                    }
                    indexOf = this.separators2.indexOf(c);
                    if (indexOf > -1) {
                        addToken(text, this.currentTokenStart, i - 1,
                            Token.LITERAL_NUMBER_DECIMAL_INT, newStartOffset
                                + this.currentTokenStart);
                        addToken(text, i, i, Token.IDENTIFIER, newStartOffset + i);
                        this.currentTokenType = Token.NULL;
                        break;
                    }

                    // Otherwise, the token is an error.
                    this.currentTokenType = Token.ERROR_NUMBER_FORMAT;

                } // End of switch (c).

                break;

            case Token.LITERAL_NUMBER_FLOAT:

                switch (c) {

                case ' ':
                case '\t':
                    addToken(text, this.currentTokenStart, i - 1, Token.LITERAL_NUMBER_FLOAT,
                        newStartOffset + this.currentTokenStart);
                    this.currentTokenStart = i;
                    this.currentTokenType = Token.WHITESPACE;
                    break;

                case '"': // Don't need to worry about backslashes as previous
                    // char is non-backslash.
                    addToken(text, this.currentTokenStart, i - 1, Token.LITERAL_NUMBER_FLOAT,
                        newStartOffset + this.currentTokenStart);
                    this.currentTokenStart = i;
                    this.currentTokenType = Token.ERROR_STRING_DOUBLE;
                    backslash = false;
                    break;

                case '\'': // Don't need to worry about backslashes as previous
                    // char is non-backslash.
                    addToken(text, this.currentTokenStart, i - 1, Token.LITERAL_NUMBER_FLOAT,
                        newStartOffset + this.currentTokenStart);
                    this.currentTokenStart = i;
                    this.currentTokenType = Token.ERROR_CHAR;
                    backslash = false;
                    break;

                case '\\':
                    addToken(text, this.currentTokenStart, i - 1, Token.LITERAL_NUMBER_FLOAT,
                        newStartOffset + this.currentTokenStart);
                    addToken(text, i, i, Token.IDENTIFIER, newStartOffset + i);
                    this.currentTokenType = Token.NULL;
                    backslash = true;
                    break;

                default:

                    if (RSyntaxUtilities.isDigit(c)) {
                        break; // Still a literal number.
                    } else if (c == 'e') { // Exponent.
                        if (numContainsExponent == false) {
                            numContainsExponent = true;
                        } else {
                            this.currentTokenType = Token.ERROR_NUMBER_FORMAT;
                        }
                        break;
                    } else if (c == '.') { // Second decimal point; must catch
                        // now because it's a "separator"
                        // below.
                        this.currentTokenType = Token.ERROR_NUMBER_FORMAT;
                        break;
                    }
                    int indexOf = this.numberEndChars.indexOf(c);
                    if (indexOf > -1) { // Numbers can end in 'f','F','l','L',
                        // etc.
                        if (numContainsEndCharacter == true) {
                            this.currentTokenType = Token.ERROR_NUMBER_FORMAT;
                        } else {
                            numContainsEndCharacter = true;
                        }
                        break;
                    }
                    indexOf = this.operators.indexOf(c);
                    if (indexOf > -1) {
                        addToken(text, this.currentTokenStart, i - 1, Token.LITERAL_NUMBER_FLOAT,
                            newStartOffset + this.currentTokenStart);
                        this.currentTokenStart = i;
                        this.currentTokenType = Token.OPERATOR;
                        break;
                    }
                    indexOf = this.separators.indexOf(c);
                    if (indexOf > -1) {
                        addToken(text, this.currentTokenStart, i - 1, Token.LITERAL_NUMBER_FLOAT,
                            newStartOffset + this.currentTokenStart);
                        addToken(text, i, i, Token.SEPARATOR, newStartOffset + i);
                        this.currentTokenType = Token.NULL;
                        break;
                    }
                    indexOf = this.separators2.indexOf(c);
                    if (indexOf > -1) {
                        addToken(text, this.currentTokenStart, i - 1, Token.LITERAL_NUMBER_FLOAT,
                            newStartOffset + this.currentTokenStart);
                        addToken(text, i, i, Token.IDENTIFIER, newStartOffset + i);
                        this.currentTokenType = Token.NULL;
                        break;
                    }

                    // Otherwise, the token is an error.
                    this.currentTokenType = Token.ERROR_NUMBER_FORMAT;

                } // End of switch (c).

                break;

            case Token.LITERAL_NUMBER_HEXADECIMAL:

                switch (c) {

                case ' ':
                case '\t':
                    addToken(text, this.currentTokenStart, i - 1, Token.LITERAL_NUMBER_HEXADECIMAL,
                        newStartOffset + this.currentTokenStart);
                    this.currentTokenStart = i;
                    this.currentTokenType = Token.WHITESPACE;
                    break;

                case '"': // Don't need to worry about backslashes as previous
                    // char is non-backslash.
                    addToken(text, this.currentTokenStart, i - 1, Token.LITERAL_NUMBER_HEXADECIMAL,
                        newStartOffset + this.currentTokenStart);
                    this.currentTokenStart = i;
                    this.currentTokenType = Token.ERROR_STRING_DOUBLE;
                    backslash = false;
                    break;

                case '\'': // Don't need to worry about backslashes as previous
                    // char is non-backslash.
                    addToken(text, this.currentTokenStart, i - 1, Token.LITERAL_NUMBER_HEXADECIMAL,
                        newStartOffset + this.currentTokenStart);
                    this.currentTokenStart = i;
                    this.currentTokenType = Token.ERROR_CHAR;
                    backslash = false;
                    break;

                case '\\':
                    addToken(text, this.currentTokenStart, i - 1, Token.LITERAL_NUMBER_HEXADECIMAL,
                        newStartOffset + this.currentTokenStart);
                    addToken(text, i, i, Token.IDENTIFIER, newStartOffset + i);
                    this.currentTokenType = Token.NULL;
                    backslash = true;
                    break;

                default:

                    if (c == 'e') { // Exponent.
                        if (numContainsExponent == false) {
                            numContainsExponent = true;
                        } else {
                            this.currentTokenType = Token.ERROR_NUMBER_FORMAT;
                        }
                        break;
                    }
                    int indexOf = this.hexCharacters.indexOf(c);
                    if (indexOf > -1) {
                        break; // Still a hexadecimal number.
                    }
                    indexOf = this.numberEndChars.indexOf(c);
                    if (indexOf > -1) { // Numbers can end in 'f','F','l','L',
                        // etc.
                        if (numContainsEndCharacter == true) {
                            this.currentTokenType = Token.ERROR_NUMBER_FORMAT;
                        } else {
                            numContainsEndCharacter = true;
                        }
                        break;
                    }
                    indexOf = this.operators.indexOf(c);
                    if (indexOf > -1) {
                        addToken(text, this.currentTokenStart, i - 1,
                            Token.LITERAL_NUMBER_HEXADECIMAL, newStartOffset
                                + this.currentTokenStart);
                        this.currentTokenStart = i;
                        this.currentTokenType = Token.OPERATOR;
                        break;
                    }
                    indexOf = this.separators.indexOf(c);
                    if (indexOf > -1) {
                        addToken(text, this.currentTokenStart, i - 1,
                            Token.LITERAL_NUMBER_HEXADECIMAL, newStartOffset
                                + this.currentTokenStart);
                        addToken(text, i, i, Token.SEPARATOR, newStartOffset + i);
                        this.currentTokenType = Token.NULL;
                        break;
                    }
                    indexOf = this.separators2.indexOf(c);
                    if (indexOf > -1) {
                        addToken(text, this.currentTokenStart, i - 1,
                            Token.LITERAL_NUMBER_HEXADECIMAL, newStartOffset
                                + this.currentTokenStart);
                        addToken(text, i, i, Token.IDENTIFIER, newStartOffset + i);
                        this.currentTokenType = Token.NULL;
                        break;
                    }

                    // Otherwise, the token is an error.
                    this.currentTokenType = Token.ERROR_NUMBER_FORMAT;

                } // End of switch (c).

                break;

            case Token.COMMENT_MULTILINE:

                // Find either end of MLC or end of the current line.
                while (i < end - 1) {
                    if (array[i] == '*' && array[i + 1] == '/') {
                        addToken(text, this.currentTokenStart, i + 1, Token.COMMENT_MULTILINE,
                            newStartOffset + this.currentTokenStart);
                        i = i + 1;
                        this.currentTokenType = Token.NULL;
                        backslash = false; // Backslashes can't accumulate
                        // before and after a comment...
                        break;
                    }
                    i++;
                }

                break;

            case Token.COMMENT_EOL:
                i = end - 1;
                addToken(text, this.currentTokenStart, i, Token.COMMENT_EOL, newStartOffset
                    + this.currentTokenStart);
                // We need to set token type to null so at the bottom we don't
                // add one more token.
                this.currentTokenType = Token.NULL;
                break;

            // We need this state because comments always start with '/', which
            // is an operator.
            // Note that when we enter this state, the PREVIOUS character was an
            // operator.
            case Token.OPERATOR:

                if (array[i - 1] == '/') { // Possibility of comments.

                    if (c == '*') {
                        this.currentTokenType = Token.COMMENT_MULTILINE;
                        break;
                    }

                    else if (c == '/') {
                        this.currentTokenType = Token.COMMENT_EOL;
                        i = end - 1; // Since we know the rest of the line is in
                        // this token.
                    }

                    else {
                        // We MUST add the token at the previous char now; if we
                        // don't and let
                        // operators accumulate before we print them, we will
                        // mess up syntax
                        // highlighting if we get an end-of-line comment.
                        addToken(text, this.currentTokenStart, i - 1, Token.OPERATOR,
                            newStartOffset + this.currentTokenStart);
                        this.currentTokenType = Token.NULL;
                        i = i - 1;
                    }

                }

                else {

                    addToken(text, this.currentTokenStart, i - 1, Token.OPERATOR, newStartOffset
                        + this.currentTokenStart);

                    // Hack to keep code size down...
                    i--;
                    this.currentTokenType = Token.NULL;

                }

                break;

            case Token.ERROR_IDENTIFIER:

                switch (c) {

                case ' ':
                case '\t':
                    addToken(text, this.currentTokenStart, i - 1, Token.ERROR_IDENTIFIER,
                        newStartOffset + this.currentTokenStart);
                    this.currentTokenStart = i;
                    this.currentTokenType = Token.WHITESPACE;
                    break;

                case '"': // Don't need to worry about backslashes as previous
                    // char is non-backslash.
                    addToken(text, this.currentTokenStart, i - 1, Token.ERROR_IDENTIFIER,
                        newStartOffset + this.currentTokenStart);
                    this.currentTokenStart = i;
                    this.currentTokenType = Token.ERROR_STRING_DOUBLE;
                    backslash = false;
                    break;

                case '\'': // Don't need to worry about backslashes as previous
                    // char is non-backslash.
                    addToken(text, this.currentTokenStart, i - 1, Token.ERROR_IDENTIFIER,
                        newStartOffset + this.currentTokenStart);
                    this.currentTokenStart = i;
                    this.currentTokenType = Token.ERROR_CHAR;
                    backslash = false;
                    break;

                case ';':
                    addToken(text, this.currentTokenStart, i - 1, Token.ERROR_IDENTIFIER,
                        newStartOffset + this.currentTokenStart);
                    addToken(text, i, i, Token.IDENTIFIER, newStartOffset + i);
                    this.currentTokenType = Token.NULL;
                    break;

                case '\\':
                    addToken(text, this.currentTokenStart, i - 1, Token.ERROR_IDENTIFIER,
                        newStartOffset + this.currentTokenStart);
                    addToken(text, i, i, Token.IDENTIFIER, newStartOffset + i);
                    this.currentTokenType = Token.NULL;
                    backslash = true; // Must be first backslash in a row since
                    // previous character is identifier char.
                    break;

                default:
                    int indexOf = this.operators.indexOf(c);
                    if (indexOf > -1) {
                        addToken(text, this.currentTokenStart, i - 1, Token.ERROR_IDENTIFIER,
                            newStartOffset + this.currentTokenStart);
                        this.currentTokenStart = i;
                        this.currentTokenType = Token.OPERATOR;
                    }
                    indexOf = this.separators.indexOf(c);
                    if (indexOf > -1) {
                        addToken(text, this.currentTokenStart, i - 1, Token.ERROR_IDENTIFIER,
                            newStartOffset + this.currentTokenStart);
                        addToken(text, i, i, Token.SEPARATOR, newStartOffset + i);
                        this.currentTokenType = Token.NULL;
                    }
                    indexOf = this.separators2.indexOf(c);
                    if (indexOf > -1) {
                        addToken(text, this.currentTokenStart, i - 1, Token.ERROR_IDENTIFIER,
                            newStartOffset + this.currentTokenStart);
                        addToken(text, i, i, Token.IDENTIFIER, newStartOffset + i);
                        this.currentTokenType = Token.NULL;
                    }

                } // End of switch (c).

                break;

            case Token.ERROR_NUMBER_FORMAT:

                switch (c) {

                case ' ':
                case '\t':
                    addToken(text, this.currentTokenStart, i - 1, Token.ERROR_NUMBER_FORMAT,
                        newStartOffset + this.currentTokenStart);
                    this.currentTokenStart = i;
                    this.currentTokenType = Token.WHITESPACE;
                    break;

                case '"': // Don't need to worry about backslashes as previous
                    // char is non-backslash.
                    addToken(text, this.currentTokenStart, i - 1, Token.ERROR_NUMBER_FORMAT,
                        newStartOffset + this.currentTokenStart);
                    this.currentTokenStart = i;
                    this.currentTokenType = Token.ERROR_STRING_DOUBLE;
                    backslash = false;
                    break;

                case '\'': // Don't need to worry about backslashes as previous
                    // char is non-backslash.
                    addToken(text, this.currentTokenStart, i - 1, Token.ERROR_NUMBER_FORMAT,
                        newStartOffset + this.currentTokenStart);
                    this.currentTokenStart = i;
                    this.currentTokenType = Token.ERROR_CHAR;
                    backslash = false;
                    break;

                case ';':
                    addToken(text, this.currentTokenStart, i - 1, Token.ERROR_NUMBER_FORMAT,
                        newStartOffset + this.currentTokenStart);
                    addToken(text, i, i, Token.IDENTIFIER, newStartOffset + i);
                    this.currentTokenType = Token.NULL;
                    break;

                case '\\':
                    addToken(text, this.currentTokenStart, i - 1, Token.ERROR_NUMBER_FORMAT,
                        newStartOffset + this.currentTokenStart);
                    addToken(text, i, i, Token.IDENTIFIER, newStartOffset + i);
                    this.currentTokenType = Token.NULL;
                    backslash = true; // Must be first backslash in a row since
                    // previous char is a number char.
                    break;

                default:

                    // Could be going into hexadecimal.
                    int indexOf = this.hexCharacters.indexOf(c);
                    if (indexOf > -1
                        && (i - this.currentTokenStart == 2 && array[i - 1] == 'x' && array[i - 2] == '0')) {
                        this.currentTokenType = Token.LITERAL_NUMBER_HEXADECIMAL;
                        break;
                    }

                    indexOf = this.operators.indexOf(c);
                    if (indexOf > -1) {
                        addToken(text, this.currentTokenStart, i - 1, Token.ERROR_NUMBER_FORMAT,
                            newStartOffset + this.currentTokenStart);
                        this.currentTokenStart = i;
                        this.currentTokenType = Token.OPERATOR;
                    }
                    indexOf = this.separators.indexOf(c);
                    if (indexOf > -1) {
                        addToken(text, this.currentTokenStart, i - 1, Token.ERROR_NUMBER_FORMAT,
                            newStartOffset + this.currentTokenStart);
                        addToken(text, i, i, Token.SEPARATOR, newStartOffset + i);
                        this.currentTokenType = Token.NULL;
                    }
                    indexOf = this.separators2.indexOf(c);
                    if (indexOf > -1) {
                        addToken(text, this.currentTokenStart, i - 1, Token.ERROR_NUMBER_FORMAT,
                            newStartOffset + this.currentTokenStart);
                        addToken(text, i, i, Token.IDENTIFIER, newStartOffset + i);
                        this.currentTokenType = Token.NULL;
                    }

                } // End of switch (c).

                break;

            case Token.ERROR_CHAR:

                if (c == '\\') {
                    backslash = !backslash; // Okay because if we got in here,
                    // backslash was initially false.
                } else {

                    if (c == '\'' && !backslash) {
                        addToken(text, this.currentTokenStart, i, Token.LITERAL_CHAR,
                            newStartOffset + this.currentTokenStart);
                        this.currentTokenType = Token.NULL;
                        // backslash is definitely false when we leave.
                    }

                    backslash = false; // Need to set backslash to false here as
                    // a character was typed.

                }
                // Otherwise, we're still an unclosed char...

                break;

            case Token.ERROR_STRING_DOUBLE:

                if (c == '\\') {
                    backslash = !backslash; // Okay because if we got in here,
                    // backslash was initially false.
                } else {
                    if (c == '"' && !backslash) {
                        addToken(text, this.currentTokenStart, i,
                            Token.LITERAL_STRING_DOUBLE_QUOTE, newStartOffset
                                + this.currentTokenStart);
                        this.currentTokenType = Token.NULL;
                        // backslash is definitely false when we leave.
                    }

                    backslash = false; // Need to set backslash to false here as
                    // a character was typed.

                }
                // Otherwise, we're still an unclosed string...

                break;

            } // End of switch (currentTokenType).

        } // End of for (int i=offset; i<end; i++).

        // Deal with the (possibly there) last token.
        if (this.currentTokenType != Token.NULL) {
            addToken(text, this.currentTokenStart, end - 1, this.currentTokenType, newStartOffset
                + this.currentTokenStart);
        }
        if (this.currentTokenType != Token.COMMENT_MULTILINE) {
            addNullToken();
        }

        // Return the first token in our linked list.
        return this.firstToken;

    }

}