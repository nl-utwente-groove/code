/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: RegExprLabelParser.java,v 1.6 2008-02-28 15:49:40 rensink Exp $
 */
package groove.view.aspect;

import static groove.rel.RegExpr.NEG_OPERATOR;
import static groove.util.ExprParser.LCURLY_CHAR;
import static groove.util.ExprParser.PLACEHOLDER;
import static groove.util.ExprParser.RCURLY_CHAR;
import static groove.util.ExprParser.SINGLE_QUOTE_CHAR;
import groove.graph.DefaultLabel;
import groove.graph.Label;
import groove.rel.RegExpr;
import groove.rel.RegExprLabel;
import groove.util.ExprParser;
import groove.util.Pair;
import groove.view.FormatException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Parser that attempts to turn the string into a regular expression label. */
public class RegExprLabelParser implements LabelParser {
    /** Private constructor for pre-computed images.
     * @param certain if {@code true}, the parsed text is certainly a regular 
     * expression; otherwise, it should be distinguished as such by curly braces.
     */
    private RegExprLabelParser(boolean certain) {
        this.certain = certain;
    }

    /**
     * This implementation attempts to turn <code>text</code> into a regular
     * expression, and if successful, turns the expression into a
     * {@link RegExprLabel}.
     */
    public Label parse(String text) throws FormatException {
        Label result;
        try {
            RegExpr expr = parseAsRegExpr(text);
            result = expr.toLabel();
        } catch (FormatException exc) {
            throw new FormatException(exc.getMessage() + " in label %s", text);
        }
        return result;
    }

    /**
     * Parses a given text as a regular expression, if it is surrounded by curly
     * brackets, starts with the negation symbol, or equals the merge
     * expression.
     */
    public RegExpr parseAsRegExpr(String text) throws FormatException {
        RegExpr result = null;
        text = text.trim();
        if (text.length() == 0) {
            throw new FormatException("Empty expression is not allowed");
        } else if (text.startsWith(NEG_OPERATOR)) {
            RegExpr innerExpr =
                parseAsRegExpr(text.substring(NEG_OPERATOR.length()));
            if (innerExpr.containsOperator(NEG_OPERATOR)) {
                throw new FormatException("Nested negation is not allowed");
            }
            result = innerExpr.neg();
            // } else if (text.length() == 1 && ) {
            // result = RegExpr.empty();
        } else if (this.certain || text.charAt(0) == RegExpr.EMPTY_OPERATOR
            || text.charAt(0) == RegExpr.WILDCARD_OPERATOR) {
            result = RegExpr.parse(text);
        } else {
            Pair<String,List<String>> parseResult = CURLY_PARSER.parse(text);
            List<String> substitutions = parseResult.second();
            if (!substitutions.isEmpty()) {
                String subText = substitutions.get(0);
                switch (subText.charAt(0)) {
                case ExprParser.LCURLY_CHAR:
                    if (parseResult.first().length() != 1) {
                        throw new FormatException(
                            "Incorrectly bracketed regular expression");
                    } else {
                        RegExpr resultExpr =
                            RegExpr.parse(subText.substring(1,
                                subText.length() - 1));
                        if (resultExpr.containsOperator(NEG_OPERATOR)) {
                            throw new FormatException(
                                "Nested negation is not allowed");
                        }
                        result = resultExpr;
                    }
                    break;
                default:
                    // character is a single quote (because CURLY_PARSER only
                    // recognises single quotes and brackets)
                    assert subText.charAt(0) == SINGLE_QUOTE_CHAR : String.format(
                        "Unexpected substitution character %c",
                        subText.charAt(0));
                    if (parseResult.first().length() != 1) {
                        throw new FormatException(
                            "Incorrectly quoted expression");
                    } else {
                        result =
                            RegExpr.atom(ExprParser.toUnquoted(subText,
                                SINGLE_QUOTE_CHAR));
                    }
                }
            } else {
                Character specialChar = getSpecialChar(text);
                if (specialChar != null) {
                    throw new FormatException(
                        "Special character %c should be quoted", specialChar);
                }
                result = RegExpr.atom(text);
            }
        }
        return result;
    }

    /**
     * Tests if a given string contains any of the characters in
     * {@link #SPECIAL_CHARS}.
     */
    private Character getSpecialChar(String text) {
        for (int i = 0; i < text.length(); i++) {
            Character c = text.charAt(i);
            if (SPECIAL_CHARS.contains(c)) {
                return c;
            }
        }
        return null;
    }

    /**
     * This implementation puts quotes around the label text, if it can
     * otherwise be interpreted as a non-atom.
     */
    public DefaultLabel unparse(Label label) {
        DefaultLabel result;
        if (label instanceof RegExprLabel) {
            result = unparse(((RegExprLabel) label).getRegExpr());
        } else if (label.isNodeType() || label.isFlag()) {
            result = (DefaultLabel) label;
        } else {
            // test if the label should be quoted
            boolean quote;
            try {
                // only quote if the label cannot be parsed as itself
                Label parsedLabel = parse(label.text());
                quote = !parsedLabel.equals(label);
            } catch (FormatException exc) {
                quote = true;
            }
            if (quote) {
                result =
                    DefaultLabel.createLabel(ExprParser.toQuoted(label.text(),
                        SINGLE_QUOTE_CHAR));
            } else {
                result = (DefaultLabel) label;
            }
        }
        return result;
    }

    private DefaultLabel unparse(RegExpr expr) {
        String text;
        if (expr.isNeg()) {
            text = RegExpr.NEG_OPERATOR + unparse(expr.getNegOperand());
        } else if (expr.isEmpty() || this.certain) {
            text = expr.toString();
        } else if (expr.isAtom()) {
            text = expr.getAtomText();
        } else {
            text = LCURLY_CHAR + expr.toString() + RCURLY_CHAR;
        }
        return DefaultLabel.createLabel(text);
    }

    /**
     * Indicates if the parsed text is certainly a regular expression,
     * or should be distinguished as one using curly braces.
     */
    private final boolean certain;

    /** 
     * Returns a static instance of this parser, either certain or not.
     * @param certain if {@code true}, the parsed text is certainly a regular 
     * expression; otherwise, it should be distinguished as such by curly braces.
     */
    static public RegExprLabelParser getInstance(boolean certain) {
        return certain ? certainInstance : uncertainInstance;
    }

    /** Static parser for curly-bracketed expressions. */
    static private final ExprParser CURLY_PARSER =
        new ExprParser(PLACEHOLDER, new char[] {SINGLE_QUOTE_CHAR}, new char[] {
            LCURLY_CHAR, RCURLY_CHAR});
    /**
     * String of characters whose occurrence in an atomic label requires the
     * unparsed view to be quoted.
     */
    static private final Set<Character> SPECIAL_CHARS =
        new HashSet<Character>(Arrays.asList(new Character[] {'{', '}', '\'',
            '\\', Aspect.VALUE_SEPARATOR}));
    /** Static instance of parser that will parse any text as regular expression. */
    static private final RegExprLabelParser certainInstance =
        new RegExprLabelParser(true);
    /** Static instance of parser that will parse only distinguished text as regular expressions. */
    static private final RegExprLabelParser uncertainInstance =
        new RegExprLabelParser(false);
}
