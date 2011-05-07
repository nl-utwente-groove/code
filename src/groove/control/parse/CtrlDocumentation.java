/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
package groove.control.parse;

import static groove.annotation.Help.bf;
import static groove.annotation.Help.html;
import static groove.annotation.Help.it;
import static groove.annotation.Help.processTokens;
import static groove.annotation.Help.processTokensAndArgs;
import static groove.annotation.Help.tip;
import static groove.io.HTMLConverter.STRONG_TAG;
import static org.antlr.works.ate.syntax.generic.ATESyntaxLexer.TOKEN_SINGLE_COMMENT;
import groove.util.ExprParser;
import groove.util.Groove;
import groove.util.Pair;
import groove.view.FormatException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.antlr.works.ate.syntax.generic.ATESyntaxLexer;
import org.antlr.works.ate.syntax.misc.ATEToken;
import org.antlr.works.grammar.element.ElementRule;
import org.antlr.works.grammar.syntax.GrammarSyntaxLexer;
import org.antlr.works.grammar.syntax.GrammarSyntaxParser;
import org.antlr.xjlib.foundation.XJUtils;

/** Class retrieving documentation lines from the Control grammar. */
public class CtrlDocumentation {
    /** 
     * Returns a mapping from grammar nonterminals to lists of
     * rules for those nonterminals.
     */
    public Map<?,? extends List<?>> getDocMap() {
        if (!this.initialised) {
            init();
        }
        return this.ruleToLinesMap;
    }

    /** Returns a mapping from rules to tool tips for those rules. */
    public Map<?,String> getToolTipMap() {
        if (!this.initialised) {
            init();
        }
        return this.toolTipMap;
    }

    /** Initialises all data structures. */
    private void init() {
        String grammarFile = Groove.getResource(CTRL_GRAMMAR_FILE).getFile();
        String grammarText;
        try {
            grammarText = XJUtils.getStringFromFile(grammarFile);
        } catch (Exception e) {
            throw new IllegalStateException(String.format(
                "Error while reading rammar file %s: %s", CTRL_GRAMMAR_FILE,
                e.getMessage()));
        }
        for (Map.Entry<ElementRule,String> nonterminal : getRules(grammarText).entrySet()) {
            processRule(nonterminal.getKey(), nonterminal.getValue());
        }
        convertToHtml();
        this.initialised = true;
    }

    /** Extracts all documentation information for a single nonterminal. */
    private void processRule(ElementRule rule, String comment) {
        List<Line> lines = new ArrayList<Line>();
        List<ATEToken> tokens = rule.getTokens();
        for (int i = 0; i < tokens.size(); i++) {
            ATEToken token = tokens.get(i);
            if (token.type == TOKEN_SINGLE_COMMENT) {
                String text = token.getAttribute();
                if (text.startsWith(RULE_PREFIX)) {
                    Line line =
                        processLine(text.substring(RULE_PREFIX.length()), lines);
                    // collect tool tip comments following the rule definition
                    String tip = "";
                    while (i < tokens.size() - 1
                        && tokens.get(i + 1).type == TOKEN_SINGLE_COMMENT
                        && !tokens.get(i + 1).getAttribute().startsWith(
                            RULE_PREFIX)) {
                        i++;
                        tip +=
                            tokens.get(i).getAttribute().substring(
                                SIMPLE_PREFIX.length())
                                + " ";
                    }
                    if (tip.length() > 0) {
                        this.toolTipMap.put(line, tip);
                    }
                }
            }
        }
        // put the rule in the rule maps and tool tip map
        if (!lines.isEmpty()) {
            Line ruleLine = new Line(rule.name);
            this.ruleToLinesMap.put(ruleLine, lines);
            this.nameToRuleMap.put(rule.name, ruleLine);
            if (comment != null) {
                this.toolTipMap.put(ruleLine, html(tip("", comment)));
            }
        }
    }

    /** Inserts the text of a comment line into the rule map. */
    private Line processLine(String lineText, List<Line> lines) {
        int colonIx = lineText.indexOf(':');
        if (colonIx >= 0) {
            String ruleName = lineText.substring(0, colonIx);
            Line rule = this.nameToRuleMap.get(ruleName);
            if (rule == null) {
                throw new IllegalStateException(String.format(
                    "Reference to non-existent rule '%s' in comment line '%s'",
                    ruleName, lineText));
            } else {
                lines = this.ruleToLinesMap.get(rule);
            }
            lineText = lineText.substring(colonIx + 1).trim();
        }
        Line result = new Line(lineText);
        lines.add(result);
        return result;
    }

    /** 
     * Returns a mapping from the rules in the grammar
     * to corresponding (multiline) comments, or to {@code null} if there is 
     * no associated comment.
     */
    private Map<ElementRule,String> getRules(String text) {
        Map<ElementRule,String> result =
            new LinkedHashMap<ElementRule,String>();
        GrammarSyntaxLexer lexer = new GrammarSyntaxLexer();
        lexer.tokenize(text);
        GrammarSyntaxParser parser = new GrammarSyntaxParser();
        List<ATEToken> tokens = lexer.getTokens();
        parser.parse(tokens);
        for (ElementRule rule : parser.rules) {
            String content = getTokenContent(rule, tokens);
            if (content != null) {
                this.tokenMap.put(rule.name, STRONG_TAG.on(content));
            }
            String comment = getRuleComment(tokens, rule);
            result.put(rule, comment);
        }
        return result;
    }

    /** Extracts the multi-line comment for a certain grammar rule, if any. */
    private String getRuleComment(List<ATEToken> tokens, ElementRule rule) {
        int index = rule.start.index;
        // look up the comment
        String comment = null;
        if (index > 0) {
            ATEToken prev = tokens.get(index - 1);
            if (prev.type == ATESyntaxLexer.TOKEN_COMPLEX_COMMENT) {
                comment = extractComment(prev.getAttribute());
            }
        }
        return comment;
    }

    /** Removes the comment brackets and asterisks from a multi-line comment. */
    private String extractComment(String text) {
        StringBuilder result = new StringBuilder(text);
        remove(result, MULTI_PREFIX);
        remove(result, MULTI_SUFFIX);
        while (remove(result, "*")) {
            // do nothing
        }
        return result.toString().trim();
    }

    /** Replaces a substring of a string builder by the empty string. */
    private boolean remove(StringBuilder result, String substring) {
        int index = result.indexOf(substring);
        if (index >= 0) {
            result.replace(index, index + substring.length(), "");
            return true;
        } else {
            return false;
        }
    }

    /** Returns the first single-quoted string in the tokens of a given rule. */
    private String getTokenContent(ElementRule rule, List<ATEToken> tokens) {
        String result = null;
        for (int i = rule.start.index + 1; i < rule.end.index; i++) {
            ATEToken token = tokens.get(i);
            if (token.type == ATESyntaxLexer.TOKEN_SINGLE_QUOTE_STRING) {
                try {
                    result = ExprParser.toUnquoted(token.getAttribute(), '\'');
                } catch (FormatException e) {
                    // do nothing
                }
            }
        }
        return result;
    }

    /**
     * Puts HTML tags around all entries.
     */
    private void convertToHtml() {
        for (Map.Entry<Line,List<Line>> entry : this.ruleToLinesMap.entrySet()) {
            List<Line> lines = entry.getValue();
            for (int i = 0; i < lines.size(); i++) {
                Line line = lines.get(i);
                Pair<String,List<String>> format =
                    processTokensAndArgs(line.toString(), this.tokenMap);
                line.setText(html(format.one()));
                convertTipToHtml(line, format.two().toArray());
            }
            Line rule = entry.getKey();
            rule.setText(html(bf(it(rule))));
        }
    }

    /** 
     * Changes the tool tip for an old key value to a HTML-formatted
     * tool tip for a new key.
     */
    private void convertTipToHtml(Line key, Object... args) {
        // convert the corresponding tip, if any
        String tip = this.toolTipMap.get(key);
        if (tip != null) {
            tip = processTokens(tip, this.tokenMap);
            // substitute nonterminal parameters
            try {
                tip = String.format(tip, args);
            } catch (Exception e) {
                throw new IllegalStateException(String.format(
                    "Error in string format '%s' for arguments %s: %s", tip,
                    Arrays.toString(args), e.getMessage()));
            }
            this.toolTipMap.put(key, html(tip(tip)));
        }
    }

    /** Flag indicating that the object has been initialised. */
    private boolean initialised;
    /**
     * Map from constant token names to token content.
     */
    private final Map<String,String> tokenMap = new HashMap<String,String>();
    private final Map<String,Line> nameToRuleMap = new HashMap<String,Line>();
    private final Map<Line,List<Line>> ruleToLinesMap =
        new LinkedHashMap<Line,List<Line>>();
    private final Map<Line,String> toolTipMap = new HashMap<Line,String>();

    /** Tests the class by printing out the resulting documentation. */
    public static void main(String[] args) {
        CtrlDocumentation doc = new CtrlDocumentation();
        for (Map.Entry<?,? extends List<?>> ruleEntry : doc.getDocMap().entrySet()) {
            System.out.printf("Nonterminal: %s%n", ruleEntry.getKey());
            for (Object rule : ruleEntry.getValue()) {
                System.out.printf("* %s%n", rule);
                String tip = doc.getToolTipMap().get(rule);
                if (tip != null) {
                    System.out.printf("  (%s)%n", tip);
                }
            }
        }
    }

    /** Prefix starting a multiline comment. */
    public static final String MULTI_PREFIX = "/**";
    /** Suffix ending a multiline comment. */
    public static final String MULTI_SUFFIX = "*/";
    /** Prefix of a single-line comments. */
    public static final String SIMPLE_PREFIX = "//";
    /** Comment prefix indicating that the comment is a rule definition. */
    public static final String RULE_PREFIX = SIMPLE_PREFIX + "@ ";
    /** The name of the grammar file providing the documentation. */
    public static final String CTRL_GRAMMAR_FILE =
        "groove/control/parse/Ctrl.g";

    private class Line {
        public Line(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return this.text;
        }

        public void setText(String text) {
            this.text = text;
        }

        private String text;
    }
}
