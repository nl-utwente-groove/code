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

import java.util.Map;
import java.util.TreeMap;

import groove.grammar.Grammar;
import groove.grammar.QualName;
import groove.grammar.Rule;
import groove.grammar.model.GrammarModel;
import groove.grammar.model.ResourceKind;
import groove.util.parse.FormatException;

/**
 * <!=========================================================================>
 * An EncodedEnabledRule describes an encoding of a Rule by means of a String.
 * <!=========================================================================>
 * @author Maarten de Mol
 */
public class EncodedEnabledRule extends EncodedEnumeratedType<Rule> {

    /**
     * Finds all enabled rules in the current grammar, and returns them as
     * a <String,String> mapping. The entries in this map are simply
     * <ruleName, ruleName>.
     */
    @Override
    public Map<String,String> generateOptions(GrammarModel grammar) {
        // Filter the rules that are enabled, and add them one by one to a
        // a sorted map.
        TreeMap<String,String> enabledRules = new TreeMap<>();
        for (QualName ruleName : grammar.getActiveNames(ResourceKind.RULE)) {
            enabledRules.put(ruleName.toString(), ruleName.toString());
        }

        // Return the sorted map.
        return enabledRules;
    }

    /**
     * Attempts to finds the Rule with the given name. If such a rule does
     * not exist, or is not enabled, a FormatException is thrown.
     */
    @Override
    public Rule parse(Grammar rules, String name) throws FormatException {
        QualName qualName = QualName.parse(name);
        Rule rule = rules.getRule(qualName);
        if (rule == null) {
            throw new FormatException(
                "'" + qualName + "' is not an enabled rule in the loaded grammar.");
        } else {
            return rule;
        }
    }
}
