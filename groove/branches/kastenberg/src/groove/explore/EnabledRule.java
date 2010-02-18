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
package groove.explore;

import groove.gui.dialog.ExplorationDialog;
import groove.trans.RuleName;
import groove.view.StoredGrammarView;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Wrapper class that stores information about an enabled rule.
 * It stores the RuleName, and a String representation (made in HTML) for the
 * user interface (with colors for use on the ExplorationDialog).
 * Also contains a factory method to create a sorted array of EnabledRules.
 */
public class EnabledRule implements Comparable<EnabledRule> {

    private RuleName ruleName;
    private String guiLine;

    /**
     * Default constructor. Stores the ruleName, and creates the guiLine that
     * goes with it.
     * @param ruleName - name of the rule (is assumed to be enabled)
     */
    public EnabledRule(RuleName ruleName) {
        this.ruleName = ruleName;
        this.guiLine =
            new String("<HTML><FONT color=" + ExplorationDialog.INFO_COLOR
                + ">" + ruleName.toString() + "</FONT></HTML>");
    }

    /**
     * Getter for the ruleName.
     */
    public RuleName getRuleName() {
        return this.ruleName;
    }

    /**
     * Getter for the guiLine. Overrides toString.
     */
    @Override
    public String toString() {
        return this.guiLine;
    }

    /**
     * Define ordering on EnabledRules to be the ordering on ruleNames.
     */
    @Override
    public int compareTo(EnabledRule o) {
        return getRuleName().toString().compareTo(o.getRuleName().toString());
    }

    /**
     * Creates a sorted array of all the enabled rules in the system.
     * @param grammar - the grammar that contains the rules
     * @return a sorted array of enabled rules
     */
    public static EnabledRule[] findEnabledRules(StoredGrammarView grammar) {
        // Get all the rule names from the grammar.
        Set<RuleName> ruleNames = grammar.getRuleNames();

        // Filter the rules that are enabled, and add them one by one to a
        // a sorted set.
        SortedSet<EnabledRule> enabledRules = new TreeSet<EnabledRule>();
        for (RuleName ruleName : ruleNames) {
            if (grammar.getRuleView(ruleName).isEnabled()) {
                enabledRules.add(new EnabledRule(ruleName));
            }
        }

        // Create an array out of the sorted set and return it. 
        EnabledRule[] output = new EnabledRule[enabledRules.size()];
        int index = 0;
        for (EnabledRule enabledRule : enabledRules) {
            output[index] = enabledRule;
            index++;
        }
        return output;
    }
}