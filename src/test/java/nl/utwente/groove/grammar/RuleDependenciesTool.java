/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
 * $Id$
 */
package nl.utwente.groove.grammar;

import static nl.utwente.groove.grammar.model.ResourceKind.RULE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.NamedResourceModel;
import nl.utwente.groove.grammar.model.RuleModel;
import nl.utwente.groove.io.Groove;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Hand-run debug harness for {@link RuleDependencies}.
 * @author Arend Rensink
 * @version $Revision$
 */
public class RuleDependenciesTool {
    private RuleDependenciesTool() {
        // empty by design
    }

    /**
     * Analyzes and prints the dependencies of a given graph grammar.
     */
    public static void main(String[] args) {
        try {
            GrammarModel grammar = Groove.loadGrammar(args[0]);
            RuleDependencies data = new RuleDependencies(grammar);
            data.collectCharacteristics();
            List<Rule> rules = getRules(grammar);
            for (Rule rule : rules) {
                System.out.println("Rule " + rule.getQualName() + ":");
                System.out.println("Positive labels: " + data.getPositiveMap().get(rule));
                System.out.println("Negative labels: " + data.getNegativeMap().get(rule));
                System.out.println("Consumed labels: " + data.getConsumedMap().get(rule));
                System.out.println("Produced labels: " + data.getProducedElementMap().get(rule));
                Collection<QualName> enablerNames = new ArrayList<>();
                for (Action depRule : data.getEnablers(rule)) {
                    enablerNames.add(depRule.getQualName());
                }
                Collection<QualName> disablerNames = new ArrayList<>();
                for (Action depRule : data.getDisablers(rule)) {
                    disablerNames.add(depRule.getQualName());
                }
                Collection<QualName> enabledNames = new ArrayList<>();
                for (Action depRule : data.getEnableds(rule)) {
                    enabledNames.add(depRule.getQualName());
                }
                Collection<QualName> disabledNames = new ArrayList<>();
                for (Action depRule : data.getDisableds(rule)) {
                    disabledNames.add(depRule.getQualName());
                }
                // disablerNames.removeAll(enablerNames);
                // disabledNames.removeAll(enabledNames);
                Collection<QualName> allRuleNames = new ArrayList<>();
                for (Action otherRule : rules) {
                    allRuleNames.add(otherRule.getQualName());
                }
                allRuleNames.removeAll(enablerNames);
                allRuleNames.removeAll(disablerNames);
                System.out.println("Enabled rules:  " + enabledNames);
                System.out.println("Disabled rules: " + disabledNames);
                System.out.println("Enablers:       " + enablerNames);
                System.out.println("Disablers:      " + disablerNames);
                System.out.println("No dependency:  " + allRuleNames);
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Returns the set of enabled rules that do not have errors. */
    static private List<Rule> getRules(GrammarModel grammar) {
        List<Rule> result = new ArrayList<>();
        // set rules
        for (NamedResourceModel<?> ruleModel : grammar.getResourceSet(RULE)) {
            try {
                // only add the active rules
                if (ruleModel.isActive()) {
                    result.add(((RuleModel) ruleModel).toResource());
                }
            } catch (FormatException exc) {
                // do not add this rule
            }
        }
        return result;
    }
}
