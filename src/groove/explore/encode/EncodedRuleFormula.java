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
package groove.explore.encode;

import groove.explore.result.Predicate;
import groove.grammar.Grammar;
import groove.grammar.QualName;
import groove.grammar.Rule;
import groove.grammar.model.GrammarModel;
import groove.lts.GraphState;
import groove.util.parse.FormatException;

/**
 * An <code>EncodedRuleFormula</code> describes a predicate over graph states
 * that is described by means of a rule formula. A rule formula is constructed
 * out of rules with the logical operators <b>not</b>, <b>and</b>, <b>or</b>,
 * and <b>implies</b>.
 *
 * @see Predicate
 * @author Maarten de Mol
 */
public class EncodedRuleFormula implements EncodedType<Predicate<GraphState>,String> {

    // local information for parsing
    private String text;
    private int i;
    private int last_i;
    private Grammar ruleSystem;

    @Override
    public EncodedTypeEditor<Predicate<GraphState>,String> createEditor(GrammarModel grammar) {
        return new StringEditor<>(grammar, "ruleName; !P; P||Q; P&&Q; P->Q",
            "", 30);
    }

    @Override
    public Predicate<GraphState> parse(Grammar rules, String text) throws FormatException {
        this.text = text;
        this.i = 0;
        this.last_i = this.text.length() - 1;
        this.ruleSystem = rules;
        Predicate<GraphState> predicate = parseFormula();
        this.ruleSystem = null; // erase local reference to rule system
        if (this.i <= this.last_i) {
            throw new FormatException("Unable to consume the entire input.");
        } else {
            return predicate;
        }
    }

    /**
     * <---------------------------------------------------------------------->
     * Auxiliary parsing method. Skips all spaces.
     * <---------------------------------------------------------------------->
     */
    private void skipSpaces() {
        while (this.i <= this.last_i && this.text.charAt(this.i) == ' ') {
            this.i++;
        }
    }

    /**
      * <---------------------------------------------------------------------->
      * Auxiliary parsing method. Parses a given literal.
      * <---------------------------------------------------------------------->
      */
    private boolean parseLiteral(String literal) {
        int upto_i = this.i + literal.length() - 1;
        if (upto_i > this.last_i) {
            return false;
        }
        if (this.text.substring(this.i, upto_i + 1)
            .equals(literal)) {
            this.i = upto_i + 1;
            return true;
        } else {
            return false;
        }
    }

    /**
      * <---------------------------------------------------------------------->
      * Auxiliary parsing method. Parses a rule name.
      * <---------------------------------------------------------------------->
      */
    private Predicate<GraphState> parseRule() throws FormatException {
        int start_i = this.i;
        while (this.i <= this.last_i && this.text.charAt(this.i) != '('
            && this.text.charAt(this.i) != ')'
            //            && this.text.charAt(this.i) != '-'
            && this.text.charAt(this.i) != '>' && this.text.charAt(this.i) != '|'
            && this.text.charAt(this.i) != '&' && this.text.charAt(this.i) != ' '
            && this.text.charAt(this.i) != '!') {
            this.i++;
        }
        if (this.i == start_i) {
            throw new FormatException("Expected a rule name at character index " + this.i + " .");
        }
        QualName ruleName = QualName.parse(this.text.substring(start_i, this.i));
        Rule rule = this.ruleSystem.getRule(ruleName);
        if (rule == null) {
            throw new FormatException(
                "'" + ruleName + "' is not an enabled rule in the loaded grammar.");
        }
        return new Predicate.RuleApplicable(rule);
    }

    /**
      * <---------------------------------------------------------------------->
      * Main parsing method. Parses a formula as a whole.
      * <---------------------------------------------------------------------->
      */
    private Predicate<GraphState> parseFormula() throws FormatException {
        skipSpaces();

        // compound formula
        if (parseLiteral("(")) {
            skipSpaces();
            int bracket_open_index = this.i - 1;
            Predicate<GraphState> predicate = parseFormula();
            skipSpaces();
            if (!parseLiteral(")")) {
                throw new FormatException("Unable to find the closing bracket "
                    + "for the open bracket at index " + bracket_open_index + ".");
            }
            return predicate;
        }

        // negated formula
        if (parseLiteral("!")) {
            skipSpaces();
            Predicate<GraphState> predicate = parseFormula();
            return new Predicate.Not<>(predicate);
        }

        // rule
        Predicate<GraphState> P = parseRule();
        skipSpaces();

        // + <operator> formula
        if (parseLiteral("&&") || parseLiteral("&")) {
            Predicate<GraphState> Q = parseFormula();
            return new Predicate.And<>(P, Q);
        } else if (parseLiteral("||") || parseLiteral("|")) {
            Predicate<GraphState> Q = parseFormula();
            return new Predicate.Or<>(P, Q);
        } else if (parseLiteral("->")) {
            Predicate<GraphState> Q = parseFormula();
            return new Predicate.Implies<>(P, Q);
        } else {
            return P;
        }
    }
}
