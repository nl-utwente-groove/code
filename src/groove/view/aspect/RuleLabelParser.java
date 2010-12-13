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
import groove.graph.TypeLabel;
import groove.rel.RegExpr;
import groove.trans.RuleLabel;
import groove.util.ExprParser;
import groove.util.Pair;
import groove.view.FormatException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Parser that attempts to turn the string into a regular expression label. */
public class RuleLabelParser implements LabelParser {
    /** Private constructor for pre-computed images.
     * @param certain if {@code true}, the parsed text is certainly a regular 
     * expression; otherwise, it should be distinguished as such by curly braces.
     */
    private RuleLabelParser(boolean certain) {
        this.certain = certain;
    }

    /**
     * This implementation attempts to turn <code>text</code> into a regular
     * expression, and if successful, turns the expression into a
     * {@link RuleLabel}.
     */
    public RuleLabel parse(String text) throws FormatException {
        RuleLabel result;
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
            RegExpr innerExpr;
            String subText = text.substring(NEG_OPERATOR.length());
            try {
                innerExpr = RegExpr.parse(subText);
            } catch (FormatException exc) {
                innerExpr = parseAsRegExpr(subText);
            }
            result = innerExpr.neg();
        } else {
            try {
                result = RegExpr.parse(text);
                if (!(this.certain || result.isWildcard() || result.isEmpty() || result.isSharp())) {
                    result = null;
                }
            } catch (FormatException exc) {
                // the expression is not regular
            }
        }
        if (result == null) {
            Pair<String,List<String>> parseResult = CURLY_PARSER.parse(text);
            List<String> substitutions = parseResult.two();
            if (!substitutions.isEmpty()) {
                String subText = substitutions.get(0);
                switch (subText.charAt(0)) {
                case ExprParser.LCURLY_CHAR:
                    if (parseResult.one().length() != 1) {
                        throw new FormatException(
                            "Incorrectly bracketed regular expression");
                    } else {
                        result =
                            RegExpr.parse(subText.substring(1,
                                subText.length() - 1));
                    }
                    break;
                default:
                    // character is a single quote (because CURLY_PARSER only
                    // recognises single quotes and brackets)
                    assert subText.charAt(0) == SINGLE_QUOTE_CHAR : String.format(
                        "Unexpected substitution character %c",
                        subText.charAt(0));
                    if (parseResult.one().length() != 1) {
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
                    RegExpr.assertAtom(text);
                }
                result = RegExpr.atom(text);
            }
        }
        if (result.containsOperator(NEG_OPERATOR)) {
            throw new FormatException("Nested negation is not allowed");
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

    /** Returns a default label that parses to this RuleLabel. */
    public TypeLabel unparse(RuleLabel label) {
        TypeLabel result = null;
        RegExpr expr = label.getRegExpr();
        if (expr != null) {
            String text;
            if (expr.isNeg()) {
                text =
                    RegExpr.NEG_OPERATOR
                        + unparse(expr.getNegOperand().toLabel());
            } else if (expr.isEmpty() || this.certain) {
                text = expr.toString();
            } else if (expr.isAtom()) {
                text = expr.getAtomText();
            } else {
                text = LCURLY_CHAR + expr.toString() + RCURLY_CHAR;
            }
            result = TypeLabel.createLabel(text);
        }
        return result;
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
    static public RuleLabelParser getInstance(boolean certain) {
        return certain ? certainInstance : uncertainInstance;
    }

    /** Static parser for curly-bracketed expressions. */
    static private final ExprParser CURLY_PARSER = new ExprParser(PLACEHOLDER,
        new char[] {SINGLE_QUOTE_CHAR}, new char[] {LCURLY_CHAR, RCURLY_CHAR});
    /**
     * String of characters whose occurrence in an atomic label requires the
     * unparsed view to be quoted.
     */
    static private final Set<Character> SPECIAL_CHARS = new HashSet<Character>(
        Arrays.asList(new Character[] {'{', '}', '\'', '\\',
            Aspect.VALUE_SEPARATOR}));
    /** Static instance of parser that will parse any text as regular expression. */
    static private final RuleLabelParser certainInstance = new RuleLabelParser(
        true);
    /** Static instance of parser that will parse only distinguished text as regular expressions. */
    static private final RuleLabelParser uncertainInstance =
        new RuleLabelParser(false);
}
