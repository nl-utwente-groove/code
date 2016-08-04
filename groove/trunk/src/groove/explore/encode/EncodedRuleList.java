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
package groove.explore.encode;

import java.util.ArrayList;
import java.util.List;

import groove.grammar.Grammar;
import groove.grammar.QualName;
import groove.grammar.Rule;
import groove.grammar.model.GrammarModel;
import groove.util.parse.FormatException;

/**
 * An EncodedEdgeList describes an encoding of Rules by means of a List. The syntax
 * is [rulename[; rulename]*]?
 * @author Rick Hindriks (based on code from EncodedEnabledRule)
 * @version $Revision $
 */
public class EncodedRuleList implements EncodedType<List<Rule>,String> {

    @Override
    public EncodedTypeEditor<List<Rule>,String> createEditor(GrammarModel grammar) {
        return new StringEditor<>(grammar, "[rulename [; rulename]*]?", "", 20);
    }

    @Override
    public List<Rule> parse(Grammar rules, String source) throws FormatException {
        ArrayList<Rule> result;
        if (source == null || source.length() == 0) {
            result = new ArrayList<>(0); //return a disabled rule list as a zero-length list
        } else {
            result = new ArrayList<>();
            //trim spaces and split on ;
            String rulelabels[] = source.replaceAll("\\ ", "")
                .split(";");
            for (String s : rulelabels) {
                parseRuleLabel(rules, QualName.parse(s), result);
            }
        }
        return result;
    }

    /**
     * Checks whether a rule is contained in the grammar, and adds it to the list of labels
     * @param rules the grammar in which the rule should be contained
     * @param label the label of the rule to parse
     * @param labellist the list to add the label  to
     * @throws FormatException when the grammar does not contain a rule with a label equal to {@code label}
     */
    private void parseRuleLabel(Grammar rules, QualName label, List<Rule> labellist)
        throws FormatException {
        Rule result = rules.getRule(label);
        if (result == null) {
            throw new FormatException("Rule name does not exist: " + label);
        } else {
            labellist.add(result);
        }
    }
}
