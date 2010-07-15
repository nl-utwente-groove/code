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

import groove.explore.result.RuleFormula;
import groove.gui.Simulator;
import groove.lts.GTS;
import groove.trans.Rule;
import groove.trans.RuleSystem;
import groove.view.FormatException;

/**
 * <!=========================================================================>
 * An EncodedEnabledRuleList describes an encoding of a list of enabled rules
 * by means of a comma separated String.
 * <!=========================================================================>
 * @author Maarten de Mol
 */
public class EncodedRuleFormula implements EncodedType<RuleFormula,String> {

    // local information for parsing
    private String text;
    private int i;
    private int last_i;

    @Override
    public EncodedTypeEditor<RuleFormula,String> createEditor(
            Simulator simulator) {
        return new StringEditor<RuleFormula>(
            "ruleName; !P; (P||Q); (P&&Q); (P->Q)", "", 30);
    }

    @Override
    public RuleFormula parse(GTS gts, String source) throws FormatException {
        this.text = source;
        this.i = 0;
        this.last_i = this.text.length() - 1;
        RuleFormula formula = parseFormula(gts.getGrammar().getRuleSystem());
        if (this.i <= this.last_i) {
            throw new FormatException("Illegal input at end of formula.");
        } else {
            return formula;
        }
    }

    private RuleFormula parseFormula(RuleSystem rs) throws FormatException {
        if (this.i > this.last_i) {
            throw new FormatException("Unexpected end of string.");
        }
        if (this.text.charAt(this.i) == '!') {
            this.i++;
            return RuleFormula.createNot(parseFormula(rs));
        }
        if (this.text.charAt(this.i) == '(') {
            this.i++;
            RuleFormula operand1 = parseFormula(rs);
            if (this.i + 1 > this.last_i) {
                throw new FormatException(
                    "Unexpected end of string. Expected binary operator.");
            }
            skipSpaces();
            String operator = this.text.substring(this.i, this.i + 2);
            this.i = this.i + 2;
            skipSpaces();
            RuleFormula operand2 = parseFormula(rs);
            if (this.i > this.last_i) {
                throw new FormatException(
                    "Unexpected end of string. Expected ).");
            }
            if (this.text.charAt(this.i) != ')') {
                throw new FormatException("Expected ), got "
                    + this.text.charAt(this.i) + ".");
            }
            this.i++;
            if (operator.equals("&&")) {
                return RuleFormula.createAnd(operand1, operand2);
            } else if (operator.equals("||")) {
                return RuleFormula.createOr(operand1, operand2);
            } else if (operator.equals("->")) {
                return RuleFormula.createImplies(operand1, operand2);
            } else {
                throw new FormatException(operator
                    + " is not a valid operator.");
            }
        }
        return parseRule(rs);
    }

    private void skipSpaces() {
        while (this.i <= this.last_i && this.text.charAt(this.i) == ' ') {
            this.i++;
        }
    }

    private RuleFormula parseRule(RuleSystem rs) throws FormatException {
        int start_i = this.i;
        while (this.i <= this.last_i && this.text.charAt(this.i) != '('
            && this.text.charAt(this.i) != ')'
            && this.text.charAt(this.i) != '-'
            && this.text.charAt(this.i) != '>'
            && this.text.charAt(this.i) != '|'
            && this.text.charAt(this.i) != '&'
            && this.text.charAt(this.i) != ' '
            && this.text.charAt(this.i) != '!') {
            this.i++;
        }
        String ruleName = this.text.substring(start_i, this.i);
        Rule rule = rs.getRule(ruleName);
        if (rule == null) {
            throw new FormatException("'" + ruleName
                + "' is not an enabled rule in the loaded grammar.");
        }
        return RuleFormula.createBasic(rule);
    }
}
