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

import groove.grammar.Grammar;
import groove.grammar.model.FormatException;
import groove.grammar.model.GrammarModel;

import java.util.ArrayList;
import java.util.List;

/**
 * An EncodedEdgeList describes an encoding of Rule label names by means of a List. The syntax
 * is [rulename[; rulename]*]?
 * @author Rick Hindriks
 * @version $Revision $
 */
public class EncodedEdgeList implements EncodedType<List<String>,String> {

    @Override
    public EncodedTypeEditor<List<String>,String> createEditor(GrammarModel grammar) {
        return new StringEditor<List<String>>(grammar, "[rulename[; rulename]*]?", "", 30);
    }

    @Override
    public List<String> parse(Grammar rules, String source) throws FormatException {
        ArrayList<String> result;
        if (source == null || source.length() == 0) {
            result = new ArrayList<String>(0); //return a disabled rule list as a zero-length list 
        } else {
            result = new ArrayList<String>();
            //trim spaces and split on ;
            String rulelabels[] = source.replaceAll("\\ ", "").split(";");
            for (String s : rulelabels) {
                parseRuleLabel(rules, s, result);
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
    private void parseRuleLabel(Grammar rules, String label, List<String> labellist)
        throws FormatException {
        if (rules.getRule(label) == null) {
            throw new FormatException("Rule name does not exist: " + label);
        } else {
            labellist.add(label);
        }
    }
}
