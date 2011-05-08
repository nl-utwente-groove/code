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

import static org.antlr.works.ate.syntax.generic.ATESyntaxLexer.TOKEN_SINGLE_COMMENT;
import groove.annotation.Help;
import groove.util.ExprParser;
import groove.util.Groove;
import groove.util.Pair;
import groove.view.FormatException;

import java.util.ArrayList;
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
public class CtrlDoc {
    /** 
     * Returns a mapping from grammar rules to lists of
     * cases for those rules.
     */
    public Map<?,? extends List<?>> getItemTree() {
        if (!this.initialised) {
            init();
        }
        return this.ruleToEntriesMap;
    }

    /** Returns a mapping from syntax items to tool tips for those items. */
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
        for (Map.Entry<ElementRule,ATEToken> nonterminal : getRules(grammarText).entrySet()) {
            processRule(nonterminal.getKey(), nonterminal.getValue());
        }
        this.initialised = true;
    }

    /** 
     * Returns a mapping from the rules in the grammar
     * to corresponding (multiline) comments, or to {@code null} if there is 
     * no associated comment.
     */
    private Map<ElementRule,ATEToken> getRules(String text) {
        Map<ElementRule,ATEToken> result =
            new LinkedHashMap<ElementRule,ATEToken>();
        GrammarSyntaxLexer lexer = new GrammarSyntaxLexer();
        lexer.tokenize(text);
        GrammarSyntaxParser parser = new GrammarSyntaxParser();
        List<ATEToken> tokens = lexer.getTokens();
        parser.parse(tokens);
        for (ElementRule rule : parser.rules) {
            // add the rule to the token map, if it seems to belong there
            String content = getSymbol(rule, tokens);
            if (content != null) {
                this.tokenMap.put(rule.name, content);
            }
            // Extract the multi-line comment of the rule, if any.
            ATEToken comment = null;
            int index = rule.start.index;
            if (index > 0) {
                ATEToken prev = tokens.get(index - 1);
                if (prev.type == ATESyntaxLexer.TOKEN_COMPLEX_COMMENT) {
                    comment = prev;
                }
            }
            result.put(rule, comment);
        }
        return result;
    }

    /** Extracts all documentation information for a single nonterminal. */
    private void processRule(ElementRule rule, ATEToken comment) {
        List<Pair<String,Help>> content = new ArrayList<Pair<String,Help>>();
        Help currentHelp = null;
        List<ATEToken> tokens = rule.getTokens();
        for (int i = 0; i < tokens.size(); i++) {
            ATEToken token = tokens.get(i);
            if (token.type == TOKEN_SINGLE_COMMENT) {
                String text = token.getAttribute();
                String syntax = getSuffix(text, SYNTAX_PATTERN);
                if (syntax != null) {
                    currentHelp = new Help(this.tokenMap);
                    // add the new help item to the content for this rule, or
                    // to the content of rule 'name' if the syntax starts with 'name:'
                    int colonIx = syntax.indexOf(':');
                    if (colonIx >= 0) {
                        String ruleName = syntax.substring(0, colonIx);
                        syntax = syntax.substring(colonIx + 1);
                        content.add(Pair.newPair(ruleName, currentHelp));
                    } else {
                        content.add(Pair.newPair(rule.name, currentHelp));
                    }
                    currentHelp.setSyntax(syntax);
                }
                String header = getSuffix(text, HEADER_PATTERN);
                if (header != null) {
                    currentHelp.setHeader(header);
                }
                String body = getSuffix(text, BODY_PATTERN);
                if (body != null) {
                    currentHelp.addBody(body);
                }
                String par = getSuffix(text, PARS_PATTERN);
                if (par != null) {
                    currentHelp.addPar(par);
                }
            }
        }
        // put the rule in the rule maps and tool tip map
        List<Line> entries = new ArrayList<Line>();
        for (Pair<String,Help> help : content) {
            Line line = createLine(help.two());
            if (help.one().equals(rule.name)) {
                entries.add(line);
            } else {
                List<Line> otherEntries = this.nameToEntriesMap.get(help.one());
                if (otherEntries == null) {
                    throw new IllegalStateException(
                        String.format(
                            "Reference to non-existent rule '%s' in comment line '%s'",
                            help.one(), help.two().getItem()));
                }
                otherEntries.add(line);
            }
        }
        if (!entries.isEmpty()) {
            Help ruleHelp = extractRuleHelp(rule.name, comment);
            this.ruleToEntriesMap.put(createLine(ruleHelp), entries);
            this.nameToEntriesMap.put(rule.name, entries);
        }
    }

    /** Removes the comment brackets and asterisks from a multi-line comment. */
    private Help extractRuleHelp(String name, ATEToken token) {
        Help result = new Help(this.tokenMap);
        result.setSyntax(Help.bf(Help.it(name)), false);
        if (token != null) {
            StringBuilder text = new StringBuilder(token.getAttribute());
            remove(text, MULTI_PREFIX);
            remove(text, MULTI_SUFFIX);
            String header = null;
            List<String> body = new ArrayList<String>();
            for (String line : text.toString().split("\n")) {
                if (header == null) {
                    header = getSuffix(line, HEADER_PATTERN);
                }
                String bodyPart = getSuffix(line, BODY_PATTERN);
                if (bodyPart != null) {
                    body.add(bodyPart);
                }
            }
            if (header != null) {
                result.setHeader(header);
                result.setBody(body);
            }
        }
        return result;
    }

    /** Returns the first single-quoted string in the tokens of a given rule. */
    private String getSymbol(ElementRule rule, List<ATEToken> tokens) {
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
     * Returns the text following a certain pattern in a string,
     * or {@code null} if the pattern does not occur in the string.
     */
    private String getSuffix(String text, String pattern) {
        String result = null;
        int ix = text.indexOf(pattern);
        if (ix >= 0) {
            result = text.substring(ix + pattern.length());
        }
        return result;
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

    /** 
     * Creates and returns a line object from the syntax line of a help object.
     * Also adds the tool tip of the help object to the tool tip map.
     */
    private Line createLine(Help help) {
        Line result = new Line(help.getItem());
        String tip = help.getTip();
        if (tip != null && tip.length() > 0) {
            this.toolTipMap.put(result, tip);
        }
        return result;
    }

    /** Flag indicating that the object has been initialised. */
    private boolean initialised;
    /**
     * Map from constant token names to token content.
     */
    private final Map<String,String> tokenMap = new HashMap<String,String>();
    private final Map<String,List<Line>> nameToEntriesMap =
        new HashMap<String,List<Line>>();
    private final Map<Line,List<Line>> ruleToEntriesMap =
        new LinkedHashMap<Line,List<Line>>();
    private final Map<Line,String> toolTipMap = new HashMap<Line,String>();

    /** Tests the class by printing out the resulting documentation. */
    public static void main(String[] args) {
        CtrlDoc doc = new CtrlDoc();
        for (Map.Entry<?,? extends List<?>> ruleEntry : doc.getItemTree().entrySet()) {
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
    /** Comment prefix indicating that the comment is a rule syntax definition. */
    public static final String SYNTAX_PATTERN = "@S ";
    /** Comment prefix indicating that the comment is a rule syntax definition. */
    public static final String HEADER_PATTERN = "@H ";
    /** Comment prefix indicating that the comment is a rule syntax definition. */
    public static final String BODY_PATTERN = "@B ";
    /** Comment prefix indicating that the comment is a rule syntax definition. */
    public static final String PARS_PATTERN = "@P ";
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

        private final String text;
    }
}
